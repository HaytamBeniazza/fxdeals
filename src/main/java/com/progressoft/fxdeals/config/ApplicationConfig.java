package com.progressoft.fxdeals.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class ApplicationConfig {

    @Bean
    public OpenAPI fxDealsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FX Deals Data Warehouse API")
                        .description("A comprehensive API for managing Foreign Exchange deals data warehouse. " +
                                    "This API allows you to submit FX deals, retrieve deal information, " +
                                    "and query deals by various criteria including currency pairs and time ranges.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FX Deals Team")
                                .email("fxdeals@progressoft.com")
                                .url("https://progressoft.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 