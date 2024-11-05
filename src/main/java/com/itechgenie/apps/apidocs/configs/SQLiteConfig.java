package com.itechgenie.apps.apidocs.configs;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class SQLiteConfig {

	@Bean
	DataSource dataSource() {
		// You can set the database path where you want to store the SQLite file
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.sqlite.JDBC");
		dataSource.setUrl("jdbc:sqlite:swaggerDocs.db");
		return dataSource;
	}

	@Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}