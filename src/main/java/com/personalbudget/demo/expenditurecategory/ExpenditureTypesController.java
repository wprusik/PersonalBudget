package com.personalbudget.demo.expenditurecategory;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import com.personalbudget.demo.expenditurecategory.logics.ExpenditureCategoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/expenditure-categories")
public class ExpenditureTypesController {
        
    private ExpenditureCategoryManager expCatManager;
    
    @Autowired
    public ExpenditureTypesController(ExpenditureCategoryManager expCatManager) {
        this.expCatManager = expCatManager;
    }    
    
    @GetMapping("/new")
    public String addCategoryForm(Model model) {
        ExpenditureCategory category = new ExpenditureCategory();
        model.addAttribute("expenditureCategory", category);
        return "new-expenditure-category";
    }
    
    @PostMapping("/new/add")
    public String addCategory(@ModelAttribute("expenditureCategory") ExpenditureCategory expenditureCategory, RedirectAttributes redirectAttributes) {
        redirectAttributes = expCatManager.addCategory(expenditureCategory, redirectAttributes);        
        if (haveError(redirectAttributes)) {
            return "redirect:/expenditure-categories/new";
        }  
        return "redirect:/expenditure-categories";
    }
    
    @GetMapping("/delete")
    public String deleteExpenditureType(@RequestParam("expenditureType") String expenditureType, RedirectAttributes redirectAttributes) {        
        redirectAttributes = expCatManager.deleteExpenditureType(expenditureType, redirectAttributes);
        return "redirect:/expenditure-categories";
    }
}
