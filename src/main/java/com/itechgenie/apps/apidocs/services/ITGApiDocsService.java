package com.itechgenie.apps.apidocs.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.itechgenie.apps.apidocs.dtos.ITGApiDocsDTO;
import com.itechgenie.apps.apidocs.repo.ITGApiDocsRepository;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ITGApiDocsService {

	private static final String PARSE_ERROR = "PARSE_ERROR";

	private final ITGApiDocsRepository itgApiDocsRepository;

	public ITGApiDocsService(ITGApiDocsRepository itgApiDocsRepository) {
		this.itgApiDocsRepository = itgApiDocsRepository;
	}

	public void insertSwaggerDoc(String apiName, String tags, String method, String uri, String description,
			String summary, String definition, String apiVersion, String operationId, String uniqueId, String error) {
		itgApiDocsRepository.insertSwaggerDoc(apiName, tags, method, uri, description, summary, definition, apiVersion,
				operationId, uniqueId, error);
	}

	public void insertSwaggerDoc(ITGApiDocsDTO itgApiDTO) {
		itgApiDocsRepository.insertSwaggerDoc(itgApiDTO.getApiName(), itgApiDTO.getTags(), itgApiDTO.getMethod(),
				itgApiDTO.getUri(), itgApiDTO.getDescription(), itgApiDTO.getSummary(), itgApiDTO.getDefinition(),
				itgApiDTO.getApiVersion(), itgApiDTO.getOperationId(), itgApiDTO.getUniqueId(), itgApiDTO.getError());
	}

	// Example search function using FTS5
	public List<ITGApiDocsDTO> searchDocs(String query) {
		return itgApiDocsRepository.searchDocs(query);
	}

	public List<ITGApiDocsDTO> parseSwagger(String url) {
		List<ITGApiDocsDTO> respList = new ArrayList<>();

		try {
			// Parse Swagger/OpenAPI definition
			SwaggerParseResult result = new OpenAPIV3Parser().readLocation(url, null, null);
			OpenAPI openAPI = result.getOpenAPI();

			if (openAPI == null) {
				ITGApiDocsDTO response = new ITGApiDocsDTO();
				response.setError(PARSE_ERROR);
				respList.add(response);
			} else if (openAPI.getPaths() != null) {

				ITGApiDocsDTO doc = new ITGApiDocsDTO();
				doc.setTitle(openAPI.getInfo().getTitle());
				doc.setDescription(openAPI.getInfo().getDescription());
				doc.setApiVersion(openAPI.getInfo().getVersion());

				for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
					String path = entry.getKey();
					PathItem pathItem = entry.getValue();

					// Iterate through each HTTP method in the PathItem
					pathItem.readOperationsMap().forEach((method, operation) -> {

						String uniqueId = doc.getTitle() + operation.getOperationId() + method;
						doc.setUniqueId(uniqueId);
						doc.setOperationId(operation.getOperationId());

						log.info("Path: " + path);
						log.info("Method: " + method);

						doc.setUri(path);
						doc.setMethod(method.name());

						// Get tags as a list of strings
						List<String> tags = operation.getTags();
						log.info("Tags: " + (tags != null ? tags : new ArrayList<>()));

						String tagsListStr = String.join(",", tags);
						doc.setTags(tagsListStr);

						// Print the summary or description if available
						String summary = operation.getSummary() != null ? operation.getSummary()
								: "No summary available";
						doc.setSummary(summary);

						log.info("----");

						respList.add(doc);
					});
				}
			}
		} catch (Exception e) {
			log.error("Exception processing: " + url, e);
		}

		return respList;
	}

	public void parseAndCacheApis(Long currentTime, List<String> apiUrls) {
		for (String apiUrl : apiUrls) {
			List<ITGApiDocsDTO> docs = parseSwagger(apiUrl);

			// Process objects in parallel and collect the results
			List<ITGApiDocsDTO> results = docs.parallelStream().peek(this::insertSwaggerDoc) // Invoking a void method
																								// in parallel
					// .map(MyObject::process) // Processing and mapping the result
					.collect(Collectors.toList());

			results.forEach(System.out::println);

		}
	}
}