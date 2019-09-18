package com.personalbudget.demo.currency.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class CurrencyServiceImplTest {

   @Autowired
   CurrencyService currencyService;
    
   @Test
   public void getCurrenciesShouldReturnNonEmptyList() {
       assertThat(currencyService.getCurrencies(), is(not(empty())));
   }   
}