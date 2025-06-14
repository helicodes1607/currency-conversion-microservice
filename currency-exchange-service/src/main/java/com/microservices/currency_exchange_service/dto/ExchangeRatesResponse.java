package com.microservices.currency_exchange_service.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRatesResponse {
    private String result;
    private String base_code;
    private Map<String, BigDecimal> conversion_rates;

    // Getters and Setters

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBase_code() {
        return base_code;
    }

    public void setBase_code(String base_code) {
        this.base_code = base_code;
    }

    public Map<String, BigDecimal> getConversion_rates() {
        return conversion_rates;
    }

    public void setConversion_rates(Map<String, BigDecimal> conversion_rates) {
        this.conversion_rates = conversion_rates;
    }
}
