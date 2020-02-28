package br.gov.go.sefaz.pat.procuracao.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan(basePackages = {WebProcuracaoConfigurationParameters.PACKAGE_REPOSITORY_JDBC})
public class DomainProcuracaoJdbcConfiguration {

	@Autowired
	private DataSource patProcuracaoDataSource;
	
	@Bean
	public JdbcTemplate patProcuracaojdbcTemplate(){
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(patProcuracaoDataSource);
		
		return jdbcTemplate;
	}
}