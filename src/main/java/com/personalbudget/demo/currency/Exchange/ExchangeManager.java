package com.personalbudget.demo.currency.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ExchangeManager {
    
    public static Exchange getExchanges() {        
        try {
            ObjectMapper mapper = new ObjectMapper();
            URL url = new URL("https://api.exchangeratesapi.io/latest");
            Exchange exchange = mapper.readValue(url, Exchange.class);                        
            return exchange;            
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY, "Error of an external server"
              );
        }        
    }
    
    public float convertCurrency(String currencyTo, String currencyFrom, float amountFrom) {        
        try {
            ObjectMapper mapper = new ObjectMapper();

            URL url = new URL("https://api.exchangeratesapi.io/latest");

            Exchange exchange = mapper.readValue(url, Exchange.class);
            
            
            HashMap<String, Float> map = exchange.getRates();
            
            float rateFrom;
            float rateTo;
            float finalAmount;
            
            if (currencyFrom.equals("EUR"))
                rateFrom = 1;
            else
                rateFrom = (float) map.get(currencyFrom);
            
                        
            rateTo = (float) map.get(currencyTo);
            
                        
            finalAmount = (amountFrom / rateFrom) * rateTo;
                        
            
            finalAmount = Math.round(finalAmount*200)/200;
            
            return finalAmount;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY, "Error of an external server"
              );
        }
    }
    
    
    
}
