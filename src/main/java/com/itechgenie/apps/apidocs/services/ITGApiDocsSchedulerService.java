package com.itechgenie.apps.apidocs.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ITGApiDocsSchedulerService {

	private final ITGApiDocsService itgapiDocsService;

	private static final List<String> API_URLS = Arrays.asList("https://petstore.swagger.io/v2/swagger.json",
			"https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/examples/v3.0/petstore.yaml",
			"https://api.nasa.gov/",
			"https://raw.githubusercontent.com/github/rest-api-description/main/descriptions/api.github.com/api.github.com.yaml",
			"https://api.twitter.com/2/openapi.json");

	public ITGApiDocsSchedulerService(ITGApiDocsService itgapiDocsService) {
		this.itgapiDocsService = itgapiDocsService;
	}

	@Scheduled(fixedRate = 28800000) // Every 8 hours
	public void scheduleApiParsing() {
		
		Long currentTime = System.currentTimeMillis();
		
		itgapiDocsService.parseAndCacheApis(currentTime, API_URLS);
	}

}