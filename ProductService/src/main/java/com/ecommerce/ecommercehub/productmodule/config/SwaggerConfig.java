package com.ecommerce.ecommercehub.productmodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-Commerce Application")
                .description("API documentation for the E-Commerce backend services")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Saurabh")
                    .url("https://github.com/demo/")
                    .email("demo@gmail.com"))
                .license(new License()
                    .name("Custom License")
                    .url("/license")))
            .externalDocs(new ExternalDocumentation()
                .description("Additional documentation for product and order services")
                .url("http://localhost:8081/swagger-ui/index.html"));
    }
}