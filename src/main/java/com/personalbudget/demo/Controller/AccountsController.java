package com.personalbudget.demo.Controller;

import com.personalbudget.demo.Entity.Account;
import com.personalbudget.demo.Entity.Currency;
import com.personalbudget.demo.REST.ExchangeManager;
import com.personalbudget.demo.Service.AccountService;
import com.personalbudget.demo.Service.CurrencyService;
import com.personalbudget.demo.Service.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/accounts")
public class AccountsController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @Autowired
    private TransactionService transactionService;    
   
    
    @GetMapping("/delete")
    public String deleteAccount(@RequestParam("accountNumber") String accountNumber, RedirectAttributes redirectAttributes)
    {
        // delete the account
        accountService.removeAccount(accountNumber);
        
        redirectAttributes.addAttribute("success", "delete");
        
        return "redirect:/accounts";
    }
    
    
    @GetMapping("/newAccount")
    public String addNewAccountForm(Model theModel)
    {
        Account account = new Account();
        List<Currency> currencies = currencyService.getCurrencies();
        
        theModel.addAttribute("account", account);
        theModel.addAttribute("currencies", currencies);
        
        return "new-account";
    }
    
    @PostMapping("/newAccount/add")
    public String addNewAccount(@ModelAttribute("account") Account theAccount, RedirectAttributes redirectAttributes, BindingResult bindingReuslt, Model model) {
        
        Account account = new Account();
        List<Currency> currencies = currencyService.getCurrencies();
        
        model.addAttribute("account", account);
        model.addAttribute("currencies", currencies);
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        // VALIDATE FORM
        // check if there aren't any empty fields
        if (theAccount.getAccountName().isBlank() || theAccount.getAccountNumber().isBlank() || theAccount.getBank().isBlank() || theAccount.getCurrency().isBlank())
        {
            model.addAttribute("error", "All fields must be completed.");
            return "new-account";
        }        
        
        
        // validate account name
        if ((theAccount.getAccountName().trim().length() < 4) || (theAccount.getAccountName().trim().length() > 30))
        {
            model.addAttribute("error", "Account name must be between 4 and 30 characters long.");
            return "new-account";
        }
        else
            theAccount.setAccountName(theAccount.getAccountName().trim());
        
        
        
        // validate bank name
        if ((theAccount.getBank().trim().length() < 3) || (theAccount.getBank().trim().length() > 40))
        {
            model.addAttribute("error", "Bank name must be between 3 and 40 characters long.");
            return "new-account";
        }
        else 
            theAccount.setBank(theAccount.getBank().trim());
        
            theAccount.setAccountName(theAccount.getAccountName().trim());
            
        // validate account number
        String accountNumber = theAccount.getAccountNumber().trim().replaceAll(" ","").replaceAll(",", "");
        
        
        // first check if it's a number
        boolean tempFlag = true;
        
        for (int i=0; i<accountNumber.length(); i++)
            if (!Character.isDigit(accountNumber.charAt(i)))
                tempFlag = false;
        
        if (!tempFlag)
        {
            model.addAttribute("error", "Wrong account number.");
            return "new-account";
        }
        
        
        // then check its length
        if (accountNumber.length() != 26)
        {
            model.addAttribute("error", "Account number must be 26 numbers long.");
            return "new-account";
        }
        List<String> tempAccountList = accountService.getAllAccountNumbers();
        
        for (String item : tempAccountList)
        {
            if (item.equals(accountNumber))
            {
                model.addAttribute("error", "That account number is already taken.");
                return "new-account";
            }
        }
        
        theAccount.setAccountNumber(accountNumber);
        theAccount.setUsername(username);
        
                
        accountService.saveAccount(theAccount);
                
        redirectAttributes.addAttribute("success", "accountAdded");
        
        return "redirect:/accounts";
    }
    
    @GetMapping("/edit")
    public String editAccountForm(@RequestParam("accountNumber") String accountNumber, Model theModel)
    {
        Account account = accountService.getAccount(accountNumber);
        List<Currency> currencies = currencyService.getCurrencies();
        
        theModel.addAttribute("transactions", transactionService.getTransactions());
        theModel.addAttribute("account", account);
        theModel.addAttribute("currencies", currencies);
        
        return "edit-account";
    }
    
    @PostMapping("/edit/applyChanges")
    public String editAccount(@ModelAttribute("account") Account theAccount, RedirectAttributes redirectAttributes, BindingResult bindingReuslt, Model model)
    {        
        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("currencies", currencies);
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        theAccount.setUsername(username);
        
        
        // VALIDATE FORM
        // check if there aren't any empty fields
        if (theAccount.getAccountName().isBlank() || theAccount.getAccountNumber().isBlank() || theAccount.getBank().isBlank() || theAccount.getCurrency().isBlank())
        {
            model.addAttribute("error", "All fields must be completed.");
            return "edit-account";
        }        
        
        
        // validate account name
        if ((theAccount.getAccountName().trim().length() < 4) || (theAccount.getAccountName().trim().length() > 30))
        {
            model.addAttribute("error", "Account name must be between 4 and 30 characters long.");
            return "edit-account";
        }
        else
            theAccount.setAccountName(theAccount.getAccountName().trim());
        
        
        
        // validate bank name
        if ((theAccount.getBank().trim().length() < 3) || (theAccount.getBank().trim().length() > 40))
        {
            model.addAttribute("error", "Bank name must be between 3 and 40 characters long.");
            return "edit-account";
        }
        else 
            theAccount.setBank(theAccount.getBank().trim());
        
            theAccount.setAccountName(theAccount.getAccountName().trim());
            
        // validate account number
        String accountNumber = theAccount.getAccountNumber().trim().replaceAll(" ","").replaceAll(",", "");
        
        
        // first check if it's a number
        boolean tempFlag = true;
        
        for (int i=0; i<accountNumber.length(); i++)
            if (!Character.isDigit(accountNumber.charAt(i)))
                tempFlag = false;
        
        if (!tempFlag)
        {
            model.addAttribute("error", "Wrong account number.");
            return "edit-account";
        }
        
        
        // then check its length
        if (accountNumber.length() != 26)
        {
            model.addAttribute("error", "Account number must be 26 numbers long.");
            return "edit-account";
        }
        
        
        theAccount.setAccountNumber(accountNumber);
        
        accountService.updateAccount(theAccount);
        
        redirectAttributes.addAttribute("success", "accountUpdate");
        
        return "redirect:/accounts";
    }
}
