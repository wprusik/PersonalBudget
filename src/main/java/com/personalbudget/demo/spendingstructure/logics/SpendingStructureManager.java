package com.personalbudget.demo.spendingstructure.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.currency.exchange.ExchangeManager;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.spendingstructure.dto.ChartData;
import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.logics.TransactionsManager;
import com.personalbudget.demo.transaction.service.TransactionService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class SpendingStructureManager {
    private SpendingStructureValidator validator;
    private TransactionService transactionService;
    private TransactionsManager transactionsManager;
    private AccountService accountService;
    private ExpenditureCategoryService expService;
    private ExpenditureCategoryService expCatService;

    @Autowired
    public SpendingStructureManager(SpendingStructureValidator validator, TransactionService transactionService, AccountService accountService, ExpenditureCategoryService expService, ExpenditureCategoryService expCatService, TransactionsManager transactionsManager) {
        this.validator = validator;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.expService = expService;
        this.expCatService = expCatService;
        this.transactionsManager = transactionsManager;
    }    
    
    public Map getCustomChartData(Model model, RedirectAttributes redirectAttributes, ChartTemplate template) {
        Map result = new HashMap();
        
        redirectAttributes = validator.validateFormCompletion(template, redirectAttributes);
        if (haveError(redirectAttributes)) {
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }        
        List<Transaction> outgoingTransactions = getAllOutgoingTransactions(template);
        
        redirectAttributes = validator.validateTransactionList(outgoingTransactions, redirectAttributes);
        if (haveError(redirectAttributes)) {            
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }
                        
        Map temp = getTimeRange(template, outgoingTransactions, redirectAttributes);
        redirectAttributes = (RedirectAttributes)temp.get("redirectAttributes");
        if (haveError(redirectAttributes)) {
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }
        LocalDateTime startLdf = (LocalDateTime)temp.get("start");
        LocalDateTime endLdf = (LocalDateTime)temp.get("end");
        temp = null;
        
        List<Transaction> finalTransactionList = getTransactionsFromTimeRange(outgoingTransactions, startLdf, endLdf);        
        redirectAttributes = validator.validateTransactionList(finalTransactionList, redirectAttributes);        
        if (haveError(redirectAttributes)) {            
            result.put("redirectAttributes", redirectAttributes);
            return result;
        } 
         
        redirectAttributes = validator.validateTimeRange(startLdf, endLdf, redirectAttributes);
        if (haveError(redirectAttributes)) {
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }
        
        
        temp = getTimeScale(startLdf, endLdf);
        List<String> timeScale = (List<String>)temp.get("timeScale");
        String theCase = (String)temp.get("case");
        temp = null;        
        
        boolean isAccountOptionSelected = validator.isAccountOptionSelected(template);
        String title = getTitle(template, isAccountOptionSelected);        
        List<String> chartLegend = getChartLegend(isAccountOptionSelected);
        
        temp = getMainData(redirectAttributes, theCase, timeScale, chartLegend, finalTransactionList, isAccountOptionSelected);
        redirectAttributes = (RedirectAttributes)temp.get("redirectAttributes");
        List<ChartData> mainData = (List<ChartData>)temp.get("mainData");        
        temp = null;
        
        mainData = formatMainData(mainData, isAccountOptionSelected);
        
        model.addAttribute("data", mainData);
        model.addAttribute("title", title);
        model.addAttribute("timeScale", timeScale);
        result.put("model", model);
        result.put("redirectAttributes", redirectAttributes);
        return result;
    }   
    
    public List<ChartData> getExpenditureCategoriesDistribution() {
        ExchangeManager exManager = new ExchangeManager();  
        List<Transaction> transactions = transactionService.getTransactions();
        List<ExpenditureCategory> expCategories = expCatService.getExpenditureCategories();
        List<ChartData> data = new ArrayList(); 
        for (ExpenditureCategory item : expCategories){
            float totalAmount = 0;
            int numberOfTransactions = 0;            
            for (Transaction tran : transactions) {
                if (validator.doesTransactionmatchExpenditureCategory(tran, item)) {
                    numberOfTransactions++;
                    float amount = exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    totalAmount += amount;
                }                
            }            
            ChartData chartData = new ChartData(item.getDescription(), totalAmount, numberOfTransactions, item.getExpenditureType());
            data.add(chartData);
        }
        
        if (data.isEmpty()) {
            ChartData chartData = new ChartData("You don't have any expenditure categories defined", 0, 0, "No expenditure categories");
            data.add(chartData);
        }
        return data;
    }
    
    public List<ChartData> getAccountsDistribution() {
        List<Account> accounts = accountService.getAccounts();
        List<ChartData> data = new ArrayList<ChartData>();
        List<Transaction> transactions = transactionService.getTransactions();
        List<ExpenditureCategory> expCategories = expCatService.getExpenditureCategories();
        ExchangeManager exManager = new ExchangeManager();
        
        if (expCategories != null && accounts != null) {
            for (Account acc : accounts) {
                List<Float> tempList = new LinkedList<Float>();
                for (ExpenditureCategory expCat : expCategories) {
                    float totalAmount = 0;                    
                    for (Transaction tran : transactions) {
                         if (validator.doesTransactionMatchAccountAndExpenditureCategory(tran, acc, expCat)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                         }
                    }
                    tempList.add(totalAmount);
                }
                String tempString = acc.getBank() + "  " + acc.getAccountName() + " (" + acc.getAccountNumber() + ")";
                ChartData tempData = new ChartData(tempString, tempList);
                data.add(tempData);
            }
        }
        else {                
            if (accounts == null) {
                List<Float> tempList = new LinkedList<Float>();
                tempList.add((float) 0);
                ChartData tempData = new ChartData("No accounts", tempList);
                data.add(tempData);
            }
            else {                
                for (Account account : accounts) {
                    String tempString = account.getBank() + "  " + account.getAccountName() + " (" + account.getAccountNumber() + ")";
                    List<Float> tempList = new LinkedList<Float>();
                    tempList.add((float) 0);
                    data.add(new ChartData(tempString, tempList));
                }
            }
        }
        return data;
    }
    
    public List<String> getExpenditureCategories() {        
        List<ExpenditureCategory> expCategories = expCatService.getExpenditureCategories();
        List<String> result = new LinkedList<String>();        
        if (expCategories != null) {                        
            for (ExpenditureCategory item : expCategories) {
                result.add(item.getExpenditureType());
            }
        }
        else {
            result.add("No expenditure categories defined");
        }      
        return result;
    }
    
    private ArrayList getAllOutgoingTransactions(ChartTemplate template) {
        List<Transaction> allTransactions = transactionService.getTransactions(); 
        ArrayList<Transaction> outgoingTransactions = new ArrayList();
        if (template.getAccount() != null) {
            for (Transaction tran : allTransactions) {
                if (tran.getType() != null && tran.getExpenditureType() != null && tran.getCurrency() != null && tran.getAccountNumberFrom() != null) {
                    if (tran.getType().equals("outgoing") && tran.getAccountNumberFrom().equals(template.getAccount())) {
                        outgoingTransactions.add(tran);
                    }
                }
            }       
        }
        else if (template.getExpType() != null) {
            for (Transaction tran : allTransactions) {
                if (tran.getType() != null && tran.getExpenditureType() != null && tran.getCurrency() != null && tran.getAccountNumberFrom() != null) {
                    if (tran.getType().equals("outgoing") && tran.getExpenditureType().equals(template.getExpType())) {
                        outgoingTransactions.add(tran);
                    }
                }
            }
        }
        return outgoingTransactions;
    }
    
    private Map getTimeRange(ChartTemplate template, List<Transaction> transactions, RedirectAttributes redirectAttributes) {
        Map result = new HashMap();
        LocalDateTime startLdf;
        LocalDateTime endLdf;
        if (validator.isStartDateSet(template)) {
            startLdf = LocalDateTime.ofInstant(template.getStartDate().toInstant(), ZoneId.systemDefault());
            if (validator.isEndDateSet(template)) {
                endLdf = LocalDateTime.ofInstant(template.getEndDate().toInstant(), ZoneId.systemDefault());
                if (startLdf.isAfter(endLdf)) {
                    redirectAttributes.addFlashAttribute("error", "The end date can not be set before the start date");
                    result.put("redirectAttributes", redirectAttributes);
                    return result;
                }
            }
            else {
                endLdf = transactions.get(0).getDateTime();
                if (transactions.size() > 1) {
                    for (int i=1; i<transactions.size(); i++) {
                        if (transactions.get(i).getDateTime().isAfter(endLdf)) {
                            endLdf = transactions.get(i).getDateTime();
                        }
                    }
                }  
            }
        }
        else if (validator.isEndDateSet(template)) {
            endLdf = LocalDateTime.ofInstant(template.getEndDate().toInstant(), ZoneId.systemDefault());            
            startLdf = transactions.get(0).getDateTime();
            if (transactions.size() > 1) {
                for (int i=1; i<transactions.size(); i++) {
                    if (transactions.get(i).getDateTime().isBefore(startLdf)) {
                        startLdf = transactions.get(i).getDateTime();
                    }
                }
            }              
        }
        else {
            endLdf = transactions.get(0).getDateTime();            
            if (transactions.size() > 1) {
                for (int i=1; i<transactions.size(); i++) {
                    if (transactions.get(i).getDateTime().isAfter(endLdf)) {
                        endLdf = transactions.get(i).getDateTime();
                    }
                }
            }             
            startLdf = transactions.get(0).getDateTime();
            if (transactions.size() > 1) {
                for (int i=1; i<transactions.size(); i++) {
                    if (transactions.get(i).getDateTime().isBefore(startLdf)) {
                        startLdf = transactions.get(i).getDateTime();
                    }
                }
            }    
        }
        
        result.put("redirectAttributes", redirectAttributes);
        result.put("start", startLdf);
        result.put("end", endLdf);
        return result;
    }
    
    private List<Transaction> getTransactionsFromTimeRange(List<Transaction> allTransactions, LocalDateTime start, LocalDateTime end) {
        List<Transaction> result = new ArrayList<Transaction>();
        for (Transaction tran : allTransactions) {
            LocalDateTime tempDate = tran.getDateTime();
            if ((tempDate.isAfter(start) || tempDate.isEqual(start)) && (tempDate.isBefore(end) || tempDate.isEqual(end)))
                result.add(tran);
        }
        return result;
    }
    
    private Map getTimeScale(LocalDateTime start, LocalDateTime end) {
        Map result = new HashMap();
        long years = ChronoUnit.YEARS.between(start, end);
        long months = ChronoUnit.MONTHS.between(start, end);
        long days = ChronoUnit.DAYS.between(start, end);
        long hours = ChronoUnit.HOURS.between(start, end);
        String[] monthNames = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        int[] lengthOfMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] lengthOfMonthsLeap = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        String theCase;        
        List<String> timeScale = new ArrayList();
        
        if (months > 12) {
            if (years > 3) {
                theCase = "years";
                
                if (years <= 12) {
                    int min = start.getYear();;
                    int max = end.getYear();
                    for (int i=min; i<=max; i++) {
                        timeScale.add(i + "");
                    }
                }
                else {
                    int max = end.getYear();
                    int min = start.getYear();
                    int step = (int) years/12;
                    
                    while (true) {
                        if (min >= max) {
                            timeScale.add(max + "");
                            break;
                        }
                        timeScale.add(min + "");
                        min+=step;                        
                    }
                }               
            }
            else {                
                theCase = "months";                
                int min = start.getMonthValue();
                int max = end.getMonthValue();
                int step = (int) months/12;                                
                int count = 0;
                int tempYear = start.getYear();                
                
                while (true) {                 
                    if (count >= months) {                        
                        timeScale.add(monthNames[max-1] + " '" + new String(tempYear + "").substring(2,4));
                        break;
                    }
                    timeScale.add(monthNames[min-1] + " '" + new String(tempYear + "").substring(2,4));                                                         
                    min+=step;
                    
                    if (min>12) {
                        min=min-12;
                        tempYear += 1;
                    }                        
                    
                    count+=step;
                }
            }
        }
        else {
            if (hours <= 24) {
                theCase = "hours";                
                int min = start.getHour();
                int count=0;
                
                while (true) {
                    String temp = min + "";
                    if (temp.length() == 1) {
                        timeScale.add("0" + min + ":00");
                    }
                    else {
                        timeScale.add(min + ":00");
                    }
                    
                    min+=2;                    
                    if (min>23)
                        min=min-24;
                    count++;
                    if (count==13)
                        break;
                }                
            }
            else if (days <= 90) {                
                theCase = "days";                
                int min = start.getDayOfMonth();
                int max = end.getDayOfMonth();
                int step = (int) days/12;
                
                if (step==0)
                    step=1;              
                
                int count = 0;
                boolean isYearLeap = false;
                int tempYear = start.getYear();
                int actualMonth = start.getMonthValue();
                
                if ((tempYear%4==0 && tempYear%100!=0) || (tempYear%100==0 && tempYear%400==0))
                    isYearLeap = true;                          
                                
                while (true) {                       
                    if (count >= days) {   
                        String tempStr = max + "";
                        if (tempStr.length() == 1)
                            tempStr = "0" + tempStr;

                        if (tempStr.charAt(tempStr.length()-1) == '1')
                            timeScale.add(monthNames[end.getMonthValue()-1] + " " + tempStr + "st");
                        else if (tempStr.charAt(tempStr.length()-1) == '2')
                            timeScale.add(monthNames[end.getMonthValue()-1] + " " + tempStr + "nd");
                        else if (tempStr.charAt(tempStr.length()-1) == '3')
                            timeScale.add(monthNames[end.getMonthValue()-1] + " " + tempStr + "rd");
                        else
                            timeScale.add(monthNames[end.getMonthValue()-1] + " " + tempStr + "th");
                        
                        break;
                    }
                    
                    String tempStr = min + "";
                    if (tempStr.length() == 1)
                        tempStr = "0" + tempStr;
                    
                    if (tempStr.charAt(tempStr.length()-1) == '1')
                        timeScale.add(monthNames[actualMonth-1] + " " + tempStr + "st");
                    else if (tempStr.charAt(tempStr.length()-1) == '2')
                        timeScale.add(monthNames[actualMonth-1] + " " + tempStr + "nd");
                    else if (tempStr.charAt(tempStr.length()-1) == '3')
                        timeScale.add(monthNames[actualMonth-1] + " " + tempStr + "rd");
                    else
                        timeScale.add(monthNames[actualMonth-1] + " " + tempStr + "th");
                                                                             
                    min+=step;
                    
                    if (isYearLeap) {
                        if (min>lengthOfMonthsLeap[actualMonth-1]) {
                            min=min-lengthOfMonthsLeap[actualMonth-1];
                            if (actualMonth==12) {
                                actualMonth=1;
                                tempYear+=1;
                                isYearLeap=false;
                            }
                            else {
                                actualMonth+=1;                          
                            }
                        }
                    }
                    else {
                        if (min>lengthOfMonths[actualMonth-1]) {
                            min=min-lengthOfMonths[actualMonth-1];
                            if (actualMonth==12) {
                                actualMonth=1;
                                tempYear+=1;                                
                                if ((tempYear%4==0 && tempYear%100!=0) || (tempYear%100==0 && tempYear%400==0)) {
                                    isYearLeap = true;
                                }
                            }
                            else {
                                actualMonth+=1;                   
                            }
                        }
                    }                    
                    count+=step;
                }                
            }
            else {                
                theCase = "months";                
                int min = start.getMonthValue();
                int max = end.getMonthValue();
                int step = 1;                                
                int count = 0;
                int tempYear = start.getYear();                
                
                while (true) {                 
                    if (count >= months) {                        
                        timeScale.add(monthNames[max-1] + " '" + new String(tempYear + "").substring(2,4));
                        break;
                    }
                    timeScale.add(monthNames[min-1] + " '" + new String(tempYear + "").substring(2,4));                                                         
                    min+=step;
                    
                    if (min>12) {
                        min=min-12;
                        tempYear += 1;
                    }
                    count+=step;
                }
            }
        }
        result.put("case", theCase);
        result.put("timeScale", timeScale);
        return result;
    }
    
    private String getTitle(ChartTemplate template, boolean isAccountOptionSelected) {
        if (isAccountOptionSelected) {
            Account tempAccount = accountService.getAccount(template.getAccount());
            String str = tempAccount.getBank() + "  " + tempAccount.getAccountName() + " (" + template.getAccount() + ")";
            return "for account: " + str;
        }
        return "for expenditure category: " + template.getExpType();
    }
    
    private List<String> getChartLegend(boolean isAccountOptionSelected) {
        List<Account> allAccounts = accountService.getAccounts();
        List<ExpenditureCategory> allExpCategories = expService.getExpenditureCategories();
        List<String> list = new ArrayList();
        if (isAccountOptionSelected) {            
            for (ExpenditureCategory item : allExpCategories)
                list.add(item.getExpenditureType());
        }
        else {
            for (Account item : allAccounts)
              list.add(item.getAccountNumber());            
        }
        return list;
    }
    
    private Map getMainData(RedirectAttributes redirectAttributes, String theCase, List<String> timeScale, List<String> chartLegend, List<Transaction> finalTransactionList, boolean isAccountOptionSelected) {
        Map result = new HashMap();
        List<ChartData> mainData = new ArrayList();
        ExchangeManager exManager = new ExchangeManager();
        if (theCase.equals("years")) {
            int ranges = timeScale.size();            
            for (String item : chartLegend) {
                List<Float> listOfAmounts = new ArrayList();
                float totalAmount = 0;
                int firstYear = Integer.parseInt(timeScale.get(0));
                for (Transaction tran : finalTransactionList) {
                    if (isAccountOptionSelected) {
                        if (tran.getDateTime().getYear() == firstYear && tran.getExpenditureType().equals(item)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                    else {
                        if (tran.getDateTime().getYear() == firstYear && tran.getAccountNumberFrom().equals(item)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                }
                listOfAmounts.add(totalAmount);
                for (int i=1; i<ranges; i++) {
                    totalAmount = 0;
                    for (Transaction tran : finalTransactionList) {
                        int tempYear = tran.getDateTime().getYear();                        
                        if (isAccountOptionSelected) {
                            if (tempYear>Integer.parseInt(timeScale.get(i-1)) && tempYear<=Integer.parseInt(timeScale.get(i)) && tran.getExpenditureType().equals(item)) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                        }
                        else {
                            if (tempYear>Integer.parseInt(timeScale.get(i-1)) && tempYear<=Integer.parseInt(timeScale.get(i)) && tran.getAccountNumberFrom().equals(item)) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                        }
                    }
                    listOfAmounts.add(totalAmount);
                }
                ChartData tempChartData = new ChartData(item, listOfAmounts);
                mainData.add(tempChartData);
            }
        }
        else if (theCase.equals("months")) {
            int ranges = timeScale.size();
            for (String item : chartLegend) {
                List<Float> listOfAmounts = new ArrayList();
                float totalAmount = 0;
                LocalDateTime beginDate = readMonthValue(timeScale.get(0));

                if (beginDate.isEqual(LocalDateTime.MIN)) {
                    redirectAttributes.addFlashAttribute("error", "Something went wrong");
                    result.put("redirectAttributes", redirectAttributes);
                    return result;
                }                            

                for (Transaction tran : finalTransactionList) {
                    if (isAccountOptionSelected) {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getExpenditureType().equals(item)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                    else {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getAccountNumberFrom().equals(item)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                }
                listOfAmounts.add(totalAmount);
                for (int i=1; i<ranges; i++) {
                    totalAmount = 0;
                    for (Transaction tran : finalTransactionList) {
                        LocalDateTime tempDate = tran.getDateTime();
                        LocalDateTime previousDate = readMonthValue(timeScale.get(i-1));
                        LocalDateTime followingDate = readMonthValue(timeScale.get(i));
                        
                        if (previousDate.isEqual(LocalDateTime.MIN) || followingDate.isEqual(LocalDateTime.MIN)) {
                            redirectAttributes.addFlashAttribute("error", "Something went wrong");
                            result.put("redirectAttributes", redirectAttributes);
                            return result;
                        }

                        if (isAccountOptionSelected) {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getExpenditureType().equals((item))) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                        }
                        else {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getAccountNumberFrom().equals((item))) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                        }
                    }
                    listOfAmounts.add(totalAmount);
                }
                ChartData tempChartData = new ChartData(item, listOfAmounts);
                mainData.add(tempChartData);
            }
        }
        else if (theCase.equals("days")) {
            int ranges = timeScale.size();
            LocalDateTime earliestDateTime = LocalDateTime.MAX;   
            
            for (Transaction tran : finalTransactionList) {                
                if (tran.getDateTime().isBefore(earliestDateTime)) {
                    earliestDateTime = LocalDateTime.of(tran.getDateTime().toLocalDate(), tran.getDateTime().toLocalTime());
                }
            }
            
            if (earliestDateTime.isEqual(LocalDateTime.MAX)) {
                redirectAttributes.addFlashAttribute("error", "Something went wrong");
                result.put("redirectAttributes", redirectAttributes);
                return result;
            }

            for (String item : chartLegend) {
                float totalAmount = 0;
                List<Float> listOfAmounts = new ArrayList();
                LocalDateTime beginDate = readDayValue(timeScale.get(0), earliestDateTime);
                if (beginDate.isEqual(LocalDateTime.MIN)) {
                    redirectAttributes.addFlashAttribute("error", "Something went wrong");
                    result.put("redirectAttributes", redirectAttributes);
                    return result;
                } 

                for (Transaction tran : finalTransactionList) {
                    if (isAccountOptionSelected) {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getExpenditureType().equals(item)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                    else {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getAccountNumberFrom().equals(item)) {
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                    
                }
                listOfAmounts.add(totalAmount);
                
                for (int i=1; i<ranges; i++) {
                    totalAmount = 0;
                    for (Transaction tran : finalTransactionList) {
                        LocalDateTime tempDate = tran.getDateTime();
                        LocalDateTime previousDate = readDayValue(timeScale.get(i-1), earliestDateTime);
                        LocalDateTime followingDate = readDayValue(timeScale.get(i), earliestDateTime);

                        if (previousDate.isEqual(LocalDateTime.MIN) || followingDate.isEqual(LocalDateTime.MIN)) {
                            redirectAttributes.addFlashAttribute("error", "Something went wrong");
                            result.put("redirectAttributes", redirectAttributes);
                            return result;
                        }

                        if (isAccountOptionSelected) {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getExpenditureType().equals(item)) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                        }
                        else {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getAccountNumberFrom().equals(item)) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                        }                        
                    }
                    listOfAmounts.add(totalAmount);
                }                    

                ChartData tempChartData = new ChartData(item, listOfAmounts);
                mainData.add(tempChartData);
            }
        }
        else if (theCase.equals("hours")) {
            int ranges = timeScale.size();
            for (String item : chartLegend) {
                List<Float> listOfAmounts = new ArrayList();
                float totalAmount = 0;
                int firstHour = Integer.parseInt(timeScale.get(0).substring(0,2));
                int day = 0;
                List<Transaction> tempFinalTransactions = new ArrayList();                
                LocalDateTime tempTime = LocalDateTime.MAX;
                
                for (Transaction tran : finalTransactionList) {
                    tempFinalTransactions.add(tran);
                    if (tran.getDateTime().isBefore(tempTime)) {
                        tempTime = LocalDateTime.of(tran.getDateTime().toLocalDate(), tran.getDateTime().toLocalTime());
                    }
                }
                day = tempTime.getDayOfMonth();
                tempTime = null;
                
                for (Transaction tran : tempFinalTransactions) {
                    if (tran.getDateTime().getDayOfMonth() == day) {
                        if (isAccountOptionSelected) {
                            if (tran.getDateTime().getHour()== firstHour && tran.getExpenditureType().equals(item)) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                finalTransactionList.remove(tran);
                            }
                                
                        }
                        else {
                            if (tran.getDateTime().getHour()== firstHour && tran.getAccountNumberFrom().equals(item)) {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                finalTransactionList.remove(tran);
                            }                                
                        }
                    }
                }
                tempFinalTransactions = null;
                listOfAmounts.add(totalAmount);
                for (int i=1; i<ranges; i++) {
                    totalAmount = 0;
                    boolean isDayNext = false;
                    if (Integer.parseInt(timeScale.get(i).substring(0,2)) < Integer.parseInt(timeScale.get(i-1).substring(0,2))) {
                        isDayNext = true;
                    }

                    if (isDayNext) {
                        for (Transaction tran : finalTransactionList) {
                            int tempHour = tran.getDateTime().getHour();
                            
                            if (isAccountOptionSelected) {
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) || tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2))) && tran.getExpenditureType().equals(item)) {
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                }
                            }
                            else {
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) || tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2))) && tran.getAccountNumberFrom().equals(item)) {
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                }
                            }                            
                        }
                    }
                    else {
                        for (Transaction tran : finalTransactionList) {
                            int tempHour = tran.getDateTime().getHour();    
                            if (isAccountOptionSelected) {                                
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) && tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2)))  && tran.getExpenditureType().equals(item)) {
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                }
                            }
                            else {
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) && tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2))) && tran.getAccountNumberFrom().equals(item)) {
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                }
                            }                            
                        }
                    }
                    listOfAmounts.add(totalAmount);
                }
                ChartData tempChartData = new ChartData(item, listOfAmounts);
                mainData.add(tempChartData);
            }
        }
        else {       
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }
        result.put("mainData", mainData);
        result.put("redirectAttributes", redirectAttributes);
        return result;
    }
    
    public static LocalDateTime readMonthValue(String month) {
        String monthString = month.substring(0,3);
        int monthValue = 0;
        String[] monthNames = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        
        for (int i=0; i<monthNames.length; i++) {
            if (monthNames[i].equals(monthString)) {
                monthValue = i+1;
                break;
            }                
        }
        
        String yearString = "20" + month.substring(5,7);
        int year = Integer.parseInt(yearString);        
        LocalDateTime date;
        
        if (monthValue>0) {
            date = LocalDateTime.of(year,monthValue,1,23,59);
            return date;
        }
        else {
            date = LocalDateTime.MIN;
            return date;
        }
    }
    
    public static LocalDateTime readDayValue(String day, LocalDateTime earliestDateTime) {
        String monthString = day.substring(0,3);
        int dayValue = Integer.parseInt(day.substring(4,6));
        int monthValue = 0;
        String[] monthNames = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        LocalDateTime date;
        
        for (int i=0; i<monthNames.length; i++) {
            if (monthNames[i].equals(monthString)) {
                monthValue = i+1;
                break;
            }                
        }  
        
        if (monthValue>0 && dayValue>0) {    
            int year = earliestDateTime.getYear();
            if (monthValue < earliestDateTime.getMonthValue()) {
                year += 1;
            }            
            date = LocalDateTime.of(year,monthValue,dayValue,23,59);
            return date;
        }
        else {
            date = LocalDateTime.MIN;
            return date;
        }
    }
    
    private List<ChartData> formatMainData(List<ChartData> mainData, boolean isAccountOptionSelected) {
        if (!isAccountOptionSelected) {
            for (ChartData item : mainData) {
                Account tempAccount = accountService.getAccount(item.getName());
                String str = tempAccount.getBank() + " - " + tempAccount.getAccountName() + " (" + tempAccount.getAccountNumber() + ")";
                item.setName(str);
            }
        }
        return mainData;
    }
}
