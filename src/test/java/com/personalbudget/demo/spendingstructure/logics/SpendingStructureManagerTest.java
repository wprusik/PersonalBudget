package com.personalbudget.demo.spendingstructure.logics;

import com.personalbudget.demo.spendingstructure.dto.ChartData;
import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class SpendingStructureManagerTest {

    @Autowired
    SpendingStructureManager manager;
    
    private Map data;
    private RedirectAttributes redirectAttributes;
    private Model model;
    private ChartTemplate template;
    private ChartData chartData;
    
    @Test
    public void getCustomChartDataShouldReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        model = new ExtendedModelMap();
        template = new ChartTemplate(new Date(1000), new Date(2000), "", "");
        // when
        data = manager.getCustomChartData(model, redirectAttributes, template);
        redirectAttributes = (RedirectAttributes)data.get("redirectAttributes");
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void getCustomChartDataShouldReturnProperModelAndNotReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        model = new ExtendedModelMap();
        template = new ChartTemplate(new Date(1000), new Date(), "86578643065942356403625734", "Rozrywka");
        // when
        data = manager.getCustomChartData(model, redirectAttributes, template);
        redirectAttributes = (RedirectAttributes)data.get("redirectAttributes");
        model = (Model)data.get("model");
        // then
        assertThat("Returned error attribute", redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
        assertThat("Have not returned ChartData data", (List<ChartData>)model.asMap().get("data"), hasSize(greaterThan(0)));
        assertThat("Have not returned proper title", (String)model.asMap().get("title"), not(isEmptyOrNullString()));
        assertThat("Have not returned proper timeScale", (List<String>)model.asMap().get("timeScale"), hasSize(greaterThan(1)));
    }
    
    @Test
    public void getExpenditureCategoriesDistributionShouldReturnNonEmptyList() {
        // given
        assertThat((List<ChartData>)manager.getExpenditureCategoriesDistribution(), not(empty()));
    }
    
    @Test
    public void getAccountsDistributionShouldReturnNonEmptyList() {
        assertThat((List<ChartData>)manager.getAccountsDistribution(), not(empty()));
    }
    
    @Test
    public void getExpenditureCategoriesShouldReturnNonEmptyList() {
        assertThat((List<String>)manager.getExpenditureCategories(), not(empty()));
    }
    
    @Test
    public void readMonthValueTest() {
        // given
        String month = "DECEMBER";
        int year = 2019;
        String date = month.substring(0,3) + year;
        LocalDateTime ldt;
        // when
        ldt = manager.readMonthValue(date);
        // then
        assertEquals(ldt.getYear(), year, "Incorrect year");
        assertEquals(ldt.getMonth().toString(), month, "Incorrect month");
    }
    
    @Test
    public void readDayValueTest() {
        // given
        LocalDateTime ldt;
        int day = 15;
        String month = "JUNE";
        String date = month.substring(0,3) + "-" + day;
        // when
        ldt = manager.readDayValue(date, LocalDateTime.of(2017,5,5,1,1));
        // then
        assertEquals(ldt.getDayOfMonth(), day, "Incorrect day");
        assertEquals(ldt.getMonth().toString(), month, "Incorrect month");
    }
}