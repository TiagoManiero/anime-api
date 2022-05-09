package com.tiagomaniero.essentials;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EssentialsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EssentialsApplication.class, args);
	}

	//configuração do grafana
	@Bean
	MeterRegistryCustomizer<MeterRegistry> configurer(
			@Value("${pring.application.name}") String applicationName) {
		return (registry) -> registry.config().commonTags("application",applicationName);
	}

}
