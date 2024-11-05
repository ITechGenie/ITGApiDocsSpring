package com.itechgenie.apps.apidocs.dtos;

import lombok.Data;

@Data
public class ITGApiDocsDTO {
	
	private String uniqueId;
	
	private String title;
	private String apiName;
	private String tags;
	private String method;
	private String uri;
	private String description;
	private String summary;
	private String definition;
	private String apiVersion;
	private String operationId;
	
	
	private String error;

}
