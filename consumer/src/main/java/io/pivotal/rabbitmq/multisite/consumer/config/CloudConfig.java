package io.pivotal.rabbitmq.multisite.consumer.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(JpaProperties.class)
public class CloudConfig extends AbstractCloudConfig {

	@Bean
	@Primary
	public DataSource dataSource() {
		return connectionFactory().dataSource("event-store-db");
	}

}
