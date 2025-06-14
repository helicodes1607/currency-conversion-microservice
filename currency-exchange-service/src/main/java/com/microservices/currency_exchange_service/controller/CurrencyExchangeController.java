package com.microservices.currency_exchange_service.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservices.currency_exchange_service.repository.CurrencyExchangeRepository;
import com.microservices.currency_exchange_service.dto.ExchangeRatesResponse;
import com.microservices.currency_exchange_service.entity.CurrencyExchange;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfiguration {
    
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@RestController
public class CurrencyExchangeController {
	
	@Autowired
	private CurrencyExchangeRepository repository;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${exchange.api.key}")
	private String apiKey;

	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyExchange retrieveExchangeValue(
			@PathVariable String from,
			@PathVariable String to) {
		
		CurrencyExchange currencyExchange 
					= repository.findByFromAndTo(from, to);
		
		if(currencyExchange ==null) {
			throw new RuntimeException
				("Unable to Find data for " + from + " to " + to);
		}
		
		String port = environment.getProperty("local.server.port");
		
		currencyExchange.setEnvironment(port);
		
		return currencyExchange;
		
	}

	@GetMapping("/currency-exchange-live-rate/from/{from}/to/{to}")
	public CurrencyExchange getLiveRate(@PathVariable String from, @PathVariable String to) {

		String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + from;

		ResponseEntity<ExchangeRatesResponse> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				ExchangeRatesResponse.class
		);

		ExchangeRatesResponse external = response.getBody();

		if (external == null || !"success".equalsIgnoreCase(external.getResult())) {
			throw new RuntimeException("Failed to fetch exchange rate from " + from);
		}

		BigDecimal rate = external.getConversion_rates().get(to.toUpperCase());

		if (rate == null) {
			throw new RuntimeException("Currency code '" + to + "' not supported.");
		}

		CurrencyExchange exchange = new CurrencyExchange();
		exchange.setFrom(from.toUpperCase());
		exchange.setTo(to.toUpperCase());
		exchange.setConversionMultiple(rate);
		exchange.setEnvironment("Live via API (Port: " + environment.getProperty("local.server.port") + ")");

		return exchange;
	}


}
