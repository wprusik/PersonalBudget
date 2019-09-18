package com.personalbudget.demo.currency.exchange;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class ExchangeManagerTest {

    @Autowired
    private ExchangeManager manager;
    
    @Test
    public void getExchangesTest() {
        // given
        Exchange exchange;
        // when
        try {
            exchange = manager.getExchanges();
        }
        catch (ResponseStatusException ex) {
            exchange = null;
        }
        // then
        assertNotNull(exchange, "Error while connecting to api");
    }
    
    @Test
    public void convertCurrencyTest() {
        // given
        float amount = 10;
        float convertedAmount;
        // when
        try {
            convertedAmount = manager.convertCurrency("PLN", "USD", amount);
        }
        catch (Exception ex) {
            convertedAmount = 0;
        }
        // then
        assertThat(convertedAmount, greaterThan((float)0));
    }

}