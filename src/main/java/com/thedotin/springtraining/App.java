package com.thedotin.springtraining;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author valentin.raduti
 */
@SpringBootApplication
@EnableSwagger2
public class App {

    public static void main(String[] args) {
    	SpringApplication.run(App.class, args);
    }

    @Bean
    public Docket swaggerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
			.groupName("api")
			.apiInfo(apiInfo())
			.select()
			.paths(regex("/api.*|/public.*"))
			.build();
    }

    private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Spring training")
			.description("Anatomy of a Spring application")
			.license("Commercial License")
			.version("1.1")
			.build();
    }
}
