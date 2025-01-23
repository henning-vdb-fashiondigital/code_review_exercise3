package com.example.code_review_exercise3;

import javax.sql.DataSource;

import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SqlServerSequenceMaxValueIncrementer;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public JobRepository jobRepository(DataSource dataSource) throws Exception {
    	JobRepositoryFactoryBean bean = new JobRepositoryFactoryBean();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(new ResourcelessTransactionManager());
		bean.setIncrementerFactory(new DefaultDataFieldMaxValueIncrementerFactory(dataSource) {
			@Override
			public DataFieldMaxValueIncrementer getIncrementer(String incrementerType, String incrementerName) {
				return new SqlServerSequenceMaxValueIncrementer(dataSource, incrementerName);
			}
		});
		bean.setJobKeyGenerator(new DefaultJobKeyGenerator());
		bean.setJdbcOperations(new JdbcTemplate(dataSource));
		bean.setDatabaseType("h2");
		bean.setConversionService(new GenericConversionService());
		bean.setSerializer(new DefaultExecutionContextSerializer());
		return bean.getObject();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS Schema");
        dataSource.setUsername("sa");
        dataSource.setPassword("password");
        return dataSource;
    }
}

