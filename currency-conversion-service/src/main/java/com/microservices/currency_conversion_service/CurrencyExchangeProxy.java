package com.microservices.currency_conversion_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Proxy interface for communicating with the currency-exchange microservice.
 * Uses Feign to make REST calls to retrieve exchange values.
 */
@FeignClient(name="currency-exchange")
public interface CurrencyExchangeProxy {
	
	/**
     * Retrieves the currency exchange value from one currency to another.
     * 
     * @param from the source currency code
     * @param to the target currency code
     * @return a CurrencyConversion object with exchange rate details
     */
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversion retrieveExchangeValue(
			@PathVariable("from") String from,
			@PathVariable("to") String to);

}