package com.itechgenie.apps.apidocs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itechgenie.apps.apidocs.dtos.ITGApiDocsDTO;
import com.itechgenie.apps.apidocs.services.ITGApiDocsSchedulerService;
import com.itechgenie.apps.apidocs.services.ITGApiDocsService;

@RestController
@RequestMapping("/api")
public class ITGApiDocsController {

	private final ITGApiDocsService itgApiDocsService;
	private final ITGApiDocsSchedulerService itgApiDocsSchedulerService;

	public ITGApiDocsController(ITGApiDocsService itgApiDocsService,
			ITGApiDocsSchedulerService itgApiDocsSchedulerService) {
		this.itgApiDocsService = itgApiDocsService;
		this.itgApiDocsSchedulerService = itgApiDocsSchedulerService;
	}

	@PostMapping("/parse-swagger")
	public ResponseEntity<String> parseSwagger(@RequestParam String url) {
		List<ITGApiDocsDTO> parsedData = itgApiDocsService.parseSwagger(url);
		String done = "OK";
		return ResponseEntity.ok(done);
	}

	@GetMapping("/parse-swagger")
	public ResponseEntity<String> parseAllSwagger() {
		itgApiDocsSchedulerService.scheduleApiParsing();
		String done = "OK";
		return ResponseEntity.ok(done);
	}

}
