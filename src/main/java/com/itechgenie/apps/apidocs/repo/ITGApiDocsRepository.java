package com.itechgenie.apps.apidocs.repo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.itechgenie.apps.apidocs.dtos.ITGApiDocsDTO;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ITGApiDocsRepository {

	private final JdbcTemplate jdbcTemplate;

	public ITGApiDocsRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostConstruct
	public void initializeDatabase() {
		// Create FTS5 virtual table if it doesn't exist
		String sql = "CREATE VIRTUAL TABLE IF NOT EXISTS swaggerDocs USING fts5("
				+ "apiName, tags, method, uri, description, summary, definition, apiVersion, operationId, uniqueId, error"
				+ ")";
		jdbcTemplate.execute(sql);
		System.out.println("FTS5 virtual table 'swaggerDocs' created.");
	}

	public void insertSwaggerDoc(String apiName, String tags, String method, String uri, String description,
			String summary, String definition, String apiVersion, String operationId, String uniqueId, String error) {
		log.info("Inserting " + uniqueId);
		String sql = "INSERT INTO swaggerDocs (apiName, tags, method, uri, description, summary, definition, apiVersion, operationId, uniqueId, error) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, apiName, tags, method, uri, description, summary, definition, apiVersion, operationId,
				uniqueId, error);
	}

	// Example search function using FTS5
	public List<ITGApiDocsDTO> searchDocs(String query) {
		String sql = "SELECT * FROM swaggerDocs WHERE swaggerDocs MATCH ?";
		return convertToITGSwaggerDocDTOList(jdbcTemplate.queryForList(sql, query));
	}

	private List<ITGApiDocsDTO> convertToITGSwaggerDocDTOList(List<Map<String, Object>> mapList) {
		return mapList.stream().map(ITGApiDocsRepository::mapToITGSwaggerDocDTO).collect(Collectors.toList());
	}

	private static ITGApiDocsDTO mapToITGSwaggerDocDTO(Map<String, Object> map) {
		ITGApiDocsDTO myObject = new ITGApiDocsDTO();
		map.forEach((key, value) -> {
			try {
				Field field = ITGApiDocsDTO.class.getDeclaredField(key);
				field.setAccessible(true);
				field.set(myObject, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				log.error("No matching field found or field not accessible: " + key, e);
			}
		});
		return myObject;
	}
}