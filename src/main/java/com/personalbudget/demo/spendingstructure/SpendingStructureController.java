package com.personalbudget.demo.spendingstructure;

import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import com.personalbudget.demo.spendingstructure.dto.ChartData;
import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.service.TransactionService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import com.personalbudget.demo.currency.exchange.ExchangeManager;

@Controller
@RequestMapping("/spending-structure")
public class SpendingStructureController {

    
    @Autowired
    TransactionService transactionService;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    ExpenditureCategoryService expService;
    
    
    @PostMapping("/custom-chart")
    public String generateCustomChart(Model model, @ModelAttribute("chartTemplate") ChartTemplate template, RedirectAttributes redirectAttributes)
    {        
        LocalDateTime startLdf;
        LocalDateTime endLdf;
        
        if ((template.getAccount() == null || template.getAccount().isBlank()) && (template.getExpType() == null || template.getExpType().isBlank()))
        {
            redirectAttributes.addFlashAttribute("error", "One of two fields (account or expenditure type) must be selected to generate a custom chart");
            return "redirect:/spending-structure";
        }
        
        
        
        List<Transaction> allTransactions = transactionService.getTransactions();
        List<Transaction> theTransactions = new ArrayList<Transaction>();
        List<Account> allAccounts = accountService.getAccounts();
        List<ExpenditureCategory> allExpCategories = expService.getExpenditureCategories();
        
        if (template.getAccount() != null) 
            for (Transaction tran : allTransactions) 
            {
                if (tran.getType() != null && tran.getExpenditureType() != null && tran.getCurrency() != null && tran.getAccountNumberFrom() != null)
                    if (tran.getType().equals("outgoing") && tran.getAccountNumberFrom().equals(template.getAccount()))
                        theTransactions.add(tran);    
            }       
        else if (template.getExpType() != null)         
            for (Transaction tran : allTransactions)
            {
                if (tran.getType() != null && tran.getExpenditureType() != null && tran.getCurrency() != null && tran.getAccountNumberFrom() != null)
                    if (tran.getType().equals("outgoing") && tran.getExpenditureType().equals(template.getExpType()))
                        theTransactions.add(tran);
            }
            
                
        
        if (theTransactions.isEmpty())
            {
                redirectAttributes.addFlashAttribute("error", "There are no transactions matching the given criteria");
                return "redirect:/spending-structure";
            }
        
        if (template.getStartDate() != null)        // startDate is set
        {
            startLdf = LocalDateTime.ofInstant(template.getStartDate().toInstant(), ZoneId.systemDefault());
            if (template.getEndDate() != null)  // endDate is set also
            {
                endLdf = LocalDateTime.ofInstant(template.getEndDate().toInstant(), ZoneId.systemDefault());
                if (startLdf.isAfter(endLdf))
                {
                    redirectAttributes.addFlashAttribute("error", "The end date can not be set before the start date");
                    return "redirect:/spending-structure";
                }
            }
            else    // startDate is set, endDate is not
            {
                endLdf = theTransactions.get(0).getDateTime();
                if (theTransactions.size() > 1)
                {
                    for (int i=1; i<theTransactions.size(); i++)
                    {
                        if (theTransactions.get(i).getDateTime().isAfter(endLdf))
                            endLdf = theTransactions.get(i).getDateTime();
                    }
                }  
            }
        }
        else if (template.getEndDate() != null)     // startDate is not set, endDate is
        {
            endLdf = LocalDateTime.ofInstant(template.getEndDate().toInstant(), ZoneId.systemDefault());
            
            startLdf = theTransactions.get(0).getDateTime();

            if (theTransactions.size() > 1)
            {
                for (int i=1; i<theTransactions.size(); i++)
                {
                    if (theTransactions.get(i).getDateTime().isBefore(startLdf))
                        startLdf = theTransactions.get(i).getDateTime();
                }
            }              
        }
        else    // neither startDate nor endDate are set
        {
            endLdf = theTransactions.get(0).getDateTime();
            
            if (theTransactions.size() > 1)
            {
                for (int i=1; i<theTransactions.size(); i++)
                {
                    if (theTransactions.get(i).getDateTime().isAfter(endLdf))
                        endLdf = theTransactions.get(i).getDateTime();
                }
            } 
            
            startLdf = theTransactions.get(0).getDateTime();

            if (theTransactions.size() > 1)
            {
                for (int i=1; i<theTransactions.size(); i++)
                {
                    if (theTransactions.get(i).getDateTime().isBefore(startLdf))
                        startLdf = theTransactions.get(i).getDateTime();
                }
            }    
        }
           
                       
        List<Transaction> finalTransactions = new ArrayList<Transaction>();
                            
        
        for (Transaction tran : theTransactions)
        {
            LocalDateTime tempDate = tran.getDateTime();
            if ((tempDate.isAfter(startLdf) || tempDate.isEqual(startLdf)) && (tempDate.isBefore(endLdf) || tempDate.isEqual(endLdf)))
                finalTransactions.add(tran);
        }
        
        if (finalTransactions.isEmpty())
        {
            redirectAttributes.addFlashAttribute("error", "There are no transactions matching the given criteria");
            return "redirect:/spending-structure";
        }
        
        
        long years = ChronoUnit.YEARS.between(startLdf, endLdf);
        long months = ChronoUnit.MONTHS.between(startLdf, endLdf);
        long days = ChronoUnit.DAYS.between(startLdf, endLdf);
        long hours = ChronoUnit.HOURS.between(startLdf, endLdf);
        long seconds = ChronoUnit.SECONDS.between(startLdf, endLdf);
        
        if (template.getStartDate() != null || template.getEndDate() != null)
            if (hours<2)
            {
                redirectAttributes.addFlashAttribute("error", "The time span between transactions is too small to generate a more detailed chart than those above");
                return "redirect:/spending-structure";
            }
        
        
        String[] monthNames = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        int[] lengthOfMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] lengthOfMonthsLeap = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        String theCase;



        List<String> timeScale = new ArrayList();
        
        if (months > 12)
        {
            if (years > 3)
            {
                theCase = "years";
                
                if (years <= 12)
                {
                    int min = startLdf.getYear();;
                    int max = endLdf.getYear();
                    
                    
                    for (int i=min; i<=max; i++)
                    {
                        timeScale.add(i + "");
                    }
                }
                else
                {
                    int max = endLdf.getYear();
                    int min = startLdf.getYear();
                    int step = (int) years/12;
                    
                    while (true)
                    {
                        if (min >= max)
                        {
                            timeScale.add(max + "");
                            break;
                        }
                        timeScale.add(min + "");
                        min+=step;                        
                    }
                }               
            }
            else
            {                
                theCase = "months";
                
                int min = startLdf.getMonthValue();
                int max = endLdf.getMonthValue();
                int step = (int) months/12;
                                
                int count = 0;
                int tempYear = startLdf.getYear();                
                
                while (true)
                {                 
                    if (count >= months)
                    {                        
                        timeScale.add(monthNames[max-1] + " '" + new String(tempYear + "").substring(2,4));
                        break;
                    }
                    timeScale.add(monthNames[min-1] + " '" + new String(tempYear + "").substring(2,4));
                                                         
                    min+=step;
                    
                    if (min>12)
                    {
                        min=min-12;
                        tempYear += 1;
                    }                        
                    
                    count+=step;
                }
            }
        }
        else
        {
            if (hours <= 24)
            {
                theCase = "hours";
                
                int min = startLdf.getHour();
                int count=0;
                
                while (true)
                {
                    String temp = min + "";
                    if (temp.length() == 1)
                        timeScale.add("0" + min + ":00");
                    else                        
                        timeScale.add(min + ":00");
                    
                    min+=2;
                    
                    if (min>23)
                        min=min-24;
                    count++;
                    if (count==13)
                        break;
                }                
            }
            else if (days <= 90)
            {                
                theCase = "days";
                
                int min = startLdf.getDayOfMonth();
                int max = endLdf.getDayOfMonth();
                int step = (int) days/12;
                
                if (step==0)
                    step=1;
                
                int count = 0;
                boolean isYearLeap = false;
                int tempYear = startLdf.getYear();
                int actualMonth = startLdf.getMonthValue();
                
                if ((tempYear%4==0 && tempYear%100!=0) || (tempYear%100==0 && tempYear%400==0)) // if the year is leap
                    isYearLeap = true;                
                           
                                
                while (true)
                {                       
                    if (count >= days)
                    {   
                        String tempStr = max + "";
                        if (tempStr.length() == 1)
                            tempStr = "0" + tempStr;

                        if (tempStr.charAt(tempStr.length()-1) == '1')
                            timeScale.add(monthNames[endLdf.getMonthValue()-1] + " " + tempStr + "st");
                        else if (tempStr.charAt(tempStr.length()-1) == '2')
                            timeScale.add(monthNames[endLdf.getMonthValue()-1] + " " + tempStr + "nd");
                        else if (tempStr.charAt(tempStr.length()-1) == '3')
                            timeScale.add(monthNames[endLdf.getMonthValue()-1] + " " + tempStr + "rd");
                        else
                            timeScale.add(monthNames[endLdf.getMonthValue()-1] + " " + tempStr + "th");
                        
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
                    
                    if (isYearLeap)
                    {
                        if (min>lengthOfMonthsLeap[actualMonth-1])
                        {
                            min=min-lengthOfMonthsLeap[actualMonth-1];
                            if (actualMonth==12)
                            {
                                actualMonth=1;
                                tempYear+=1;
                                isYearLeap=false;
                            }
                            else
                                actualMonth+=1;                          
                        }
                    }
                    else
                    {
                        if (min>lengthOfMonths[actualMonth-1])
                        {
                            min=min-lengthOfMonths[actualMonth-1];
                            if (actualMonth==12)
                            {
                                actualMonth=1;
                                tempYear+=1;                                
                                if ((tempYear%4==0 && tempYear%100!=0) || (tempYear%100==0 && tempYear%400==0)) // if the year is leap
                                    isYearLeap = true;
                            }
                            else
                                actualMonth+=1;                   
                        }
                    }    
                    
                    count+=step;
                }                
            }
            else
            {
                // months number below or equal 12
                theCase = "months";
                
                int min = startLdf.getMonthValue();
                int max = endLdf.getMonthValue();
                int step = 1;
                                
                int count = 0;
                int tempYear = startLdf.getYear();                
                
                while (true)
                {                 
                    if (count >= months)
                    {                        
                        timeScale.add(monthNames[max-1] + " '" + new String(tempYear + "").substring(2,4));
                        break;
                    }
                    timeScale.add(monthNames[min-1] + " '" + new String(tempYear + "").substring(2,4));
                                                         
                    min+=step;
                    
                    if (min>12)
                    {
                        min=min-12;
                        tempYear += 1;
                    }                        
                    
                    count+=step;
                }
            }
        }
        
        
        
        
        
        List<String> tempList = new ArrayList();
        String title;
        
        boolean accFlag;
        
        if (template.getAccount() != null)
        {
            Account tempAccount = accountService.getAccount(template.getAccount());
            String str = "for account: " + tempAccount.getBank() + "  " + tempAccount.getAccountName() + " (" + template.getAccount() + ")";
            title = "for account: " + str;
            
            accFlag = true; // flag defining whether the option for the account or for expenditure category been selected in the form; true - account; false - expCat
            
            for (ExpenditureCategory item : allExpCategories)
                tempList.add(item.getExpenditureType());
        }
        else    // if not account - expenditure type
        {
            title = "for expenditure category: " + template.getExpType();
                 
            accFlag = false;
            
            for (Account item : allAccounts)
              tempList.add(item.getAccountNumber());            
        }
        
        
        
        List<ChartData> theList = new ArrayList();

        ExchangeManager exManager = new ExchangeManager();
        
        
        if (theCase.equals("years"))
        {
            int ranges = timeScale.size();
            
            for (String item : tempList)    // iterate through exchange categories
            {
                List<Float> listOfAmounts = new ArrayList();

                // for first range
                float totalAmount = 0;
                int firstYear = Integer.parseInt(timeScale.get(0));
                for (Transaction tran : finalTransactions)
                {
                    if (accFlag)
                    {
                        if (tran.getDateTime().getYear() == firstYear && tran.getExpenditureType().equals(item))
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    }
                    else
                    {
                        if (tran.getDateTime().getYear() == firstYear && tran.getAccountNumberFrom().equals(item))
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    }
                }
                listOfAmounts.add(totalAmount);
                // for other ranges
                for (int i=1; i<ranges; i++)  // through time scale
                {
                    totalAmount = 0;
                    for (Transaction tran : finalTransactions)
                    {
                        int tempYear = tran.getDateTime().getYear();
                        
                        if (accFlag)
                        {
                            if (tempYear>Integer.parseInt(timeScale.get(i-1)) && tempYear<=Integer.parseInt(timeScale.get(i)) && tran.getExpenditureType().equals(item))
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                        else
                        {
                            if (tempYear>Integer.parseInt(timeScale.get(i-1)) && tempYear<=Integer.parseInt(timeScale.get(i)) && tran.getAccountNumberFrom().equals(item))
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                    }
                    listOfAmounts.add(totalAmount);
                }                    

                ChartData tempChartData = new ChartData(item, listOfAmounts);
                theList.add(tempChartData);
            }
        }
        else if (theCase.equals("months"))
        {
            int ranges = timeScale.size();

            for (String item : tempList)    // iterate through exchange categories
            {
                List<Float> listOfAmounts = new ArrayList();

                // for first range
                float totalAmount = 0;
                LocalDateTime beginDate = readMonthValue(timeScale.get(0));

                if (beginDate.isEqual(LocalDateTime.MIN))
                {
                    redirectAttributes.addFlashAttribute("error", "Something went wrong");
                    return "redirect:/spending-structure";
                }                            

                for (Transaction tran : finalTransactions)
                {
                    if (accFlag)
                    {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getExpenditureType().equals(item))
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    }
                    else
                    {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getAccountNumberFrom().equals(item))
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    }
                }
                listOfAmounts.add(totalAmount);
                // for other ranges
                for (int i=1; i<ranges; i++)  // through time scale
                {
                    totalAmount = 0;
                    for (Transaction tran : finalTransactions)
                    {
                        LocalDateTime tempDate = tran.getDateTime();
                        LocalDateTime previousDate = readMonthValue(timeScale.get(i-1));
                        LocalDateTime followingDate = readMonthValue(timeScale.get(i));

                        if (previousDate.isEqual(LocalDateTime.MIN) || followingDate.isEqual(LocalDateTime.MIN))
                        {
                            redirectAttributes.addFlashAttribute("error", "Something went wrong");
                            return "redirect:/spending-structure";
                        }

                        if (accFlag)
                        {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getExpenditureType().equals((item)))
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                        else
                        {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getAccountNumberFrom().equals((item)))
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                        
                        
                    }
                    listOfAmounts.add(totalAmount);
                }                    

                ChartData tempChartData = new ChartData(item, listOfAmounts);
                theList.add(tempChartData);
            }
        }
        else if (theCase.equals("days"))
        {
            int ranges = timeScale.size();

            LocalDateTime earliestDateTime = LocalDateTime.MAX;
            
            for (Transaction tran : finalTransactions)
            {                
                if (tran.getDateTime().isBefore(earliestDateTime))
                    earliestDateTime = LocalDateTime.of(tran.getDateTime().toLocalDate(), tran.getDateTime().toLocalTime());
            }
            
            
            if (earliestDateTime.isEqual(LocalDateTime.MAX))
            {
                redirectAttributes.addFlashAttribute("error", "Something went wrong");
                return "redirect:/spending-structure";
            }

            for (String item : tempList)    // iterate through exchange categories
            {
                float totalAmount = 0;
                List<Float> listOfAmounts = new ArrayList();

                // for the first range
                LocalDateTime beginDate = readDayValue(timeScale.get(0), earliestDateTime);

                if (beginDate.isEqual(LocalDateTime.MIN))
                {
                    redirectAttributes.addFlashAttribute("error", "Something went wrong");
                    return "redirect:/spending-structure";
                } 

                for (Transaction tran : finalTransactions)
                {
                    if (accFlag)
                    {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getExpenditureType().equals(item))
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    }
                    else
                    {
                        if ((tran.getDateTime().isBefore(beginDate) || tran.getDateTime().isEqual(beginDate)) && tran.getAccountNumberFrom().equals(item))
                            totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    }
                    
                }
                listOfAmounts.add(totalAmount);

                // for other ranges                    
                for (int i=1; i<ranges; i++)  // through time scale
                {
                    totalAmount = 0;
                    for (Transaction tran : finalTransactions)
                    {
                        LocalDateTime tempDate = tran.getDateTime();
                        LocalDateTime previousDate = readDayValue(timeScale.get(i-1), earliestDateTime);
                        LocalDateTime followingDate = readDayValue(timeScale.get(i), earliestDateTime);

                        if (previousDate.isEqual(LocalDateTime.MIN) || followingDate.isEqual(LocalDateTime.MIN))
                        {
                            redirectAttributes.addFlashAttribute("error", "Something went wrong");
                            return "redirect:/spending-structure";
                        }

                        if (accFlag)
                        {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getExpenditureType().equals(item))
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                        else
                        {
                            if (tempDate.isAfter(previousDate) && (tempDate.isBefore(followingDate) || tempDate.isEqual(followingDate)) && tran.getAccountNumberFrom().equals(item))
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        }
                        
                    }
                    listOfAmounts.add(totalAmount);
                }                    

                ChartData tempChartData = new ChartData(item, listOfAmounts);
                theList.add(tempChartData);
            }
        }
        else if (theCase.equals("hours"))
        {
            int ranges = timeScale.size();

            for (String item : tempList)    // iterate through exchange categories
            {
                List<Float> listOfAmounts = new ArrayList();

                // for first range
                float totalAmount = 0;
                int firstHour = Integer.parseInt(timeScale.get(0).substring(0,2));
                int day = 0;
                List<Transaction> tempFinalTransactions = new ArrayList();
                
                LocalDateTime temp = LocalDateTime.MAX;
                for (Transaction tran : finalTransactions)
                {
                    tempFinalTransactions.add(tran);
                    if (tran.getDateTime().isBefore(temp))
                        temp = LocalDateTime.of(tran.getDateTime().toLocalDate(), tran.getDateTime().toLocalTime());
                }
                day = temp.getDayOfMonth();
                temp = null;
                
                
                                
                
                for (Transaction tran : tempFinalTransactions)
                {
                    if (tran.getDateTime().getDayOfMonth() == day)
                    {
                        if (accFlag)
                        {
                            if (tran.getDateTime().getHour()== firstHour && tran.getExpenditureType().equals(item))
                            {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                finalTransactions.remove(tran);
                            }
                                
                        }
                        else
                        {
                            if (tran.getDateTime().getHour()== firstHour && tran.getAccountNumberFrom().equals(item))
                            {
                                totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                                finalTransactions.remove(tran);
                            }
                                
                        }
                    }
                }
                tempFinalTransactions = null;
                listOfAmounts.add(totalAmount);
                // for other ranges
                for (int i=1; i<ranges; i++)  // through time scale
                {
                    totalAmount = 0;
                    boolean isDayNext = false;
                    if (Integer.parseInt(timeScale.get(i).substring(0,2)) < Integer.parseInt(timeScale.get(i-1).substring(0,2)))
                        isDayNext = true;

                    if (isDayNext)
                    {
                        for (Transaction tran : finalTransactions)
                        {
                            int tempHour = tran.getDateTime().getHour();
                            
                            if (accFlag)
                            {
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) || tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2))) && tran.getExpenditureType().equals(item))
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                            else
                            {
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) || tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2))) && tran.getAccountNumberFrom().equals(item))
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }                            
                        }
                    }
                    else
                    {
                        for (Transaction tran : finalTransactions)
                        {
                            int tempHour = tran.getDateTime().getHour();    
                            if (accFlag)
                            {                                
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) && tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2)))  && tran.getExpenditureType().equals(item))
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }
                            else
                            {
                                if ((tempHour>Integer.parseInt(timeScale.get(i-1).substring(0,2)) && tempHour<=Integer.parseInt(timeScale.get(i).substring(0,2))) && tran.getAccountNumberFrom().equals(item))
                                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                            }                            
                        }
                    }

                    listOfAmounts.add(totalAmount);
                }                    

                ChartData tempChartData = new ChartData(item, listOfAmounts);
                theList.add(tempChartData);
            }
        }
        else
        {            
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
            return "redirect:/spending-structure";
        }

                
        // change account numbers to nicer form
        if (template.getExpType()!= null)
            for (ChartData item : theList)
            {
                Account tempAccount = accountService.getAccount(item.getName());
                String str = tempAccount.getBank() + " - " + tempAccount.getAccountName() + " (" + tempAccount.getAccountNumber() + ")";
                item.setName(str);
            }
        
        
        
        model.addAttribute("data", theList);
        model.addAttribute("title", title);
        model.addAttribute("timeScale", timeScale);
        
                       
        return "custom-chart";
    }
    
    public static LocalDateTime readMonthValue(String month)
    {
        String monthString = month.substring(0,3);
        int monthValue = 0;
        String[] monthNames = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        
        for (int i=0; i<monthNames.length; i++)
        {
            if (monthNames[i].equals(monthString))
            {
                monthValue = i+1;
                break;
            }                
        }
        
        String yearString = "20" + month.substring(5,7);
        int year = Integer.parseInt(yearString);
        
        LocalDateTime date;
        
        if (monthValue>0)
        {
            date = LocalDateTime.of(year,monthValue,1,23,59);
            return date;
        }
        else
        {
            date = LocalDateTime.MIN;
            return date;
        }
    }
    
    public static LocalDateTime readDayValue(String day, LocalDateTime earliestDateTime)
    {
        String monthString = day.substring(0,3);
        int dayValue = Integer.parseInt(day.substring(4,6));
        int monthValue = 0;
        String[] monthNames = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        
        for (int i=0; i<monthNames.length; i++)
        {
            if (monthNames[i].equals(monthString))
            {
                monthValue = i+1;
                break;
            }                
        }
        
        LocalDateTime date;        
        
        if (monthValue>0 && dayValue>0)
        {    
            int year = earliestDateTime.getYear();
            if (monthValue < earliestDateTime.getMonthValue())
                year += 1;
            
            date = LocalDateTime.of(year,monthValue,dayValue,23,59);
            return date;
        }
        else
        {
            date = LocalDateTime.MIN;
            return date;
        }
    }
}
