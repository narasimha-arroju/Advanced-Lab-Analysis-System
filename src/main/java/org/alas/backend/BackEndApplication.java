package org.alas.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@ConfigurationPropertiesScan
@Configuration
public class BackEndApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(BackEndApplication.class);
		springApplication.setWebApplicationType(WebApplicationType.REACTIVE);
		springApplication.run(args);
	}

}
