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

/**
 * Application configuration for FX Deals Data Warehouse
 */
@Configuration
public class ApplicationConfig {

    /**
     * Configure OpenAPI documentation
     */
    @Bean
    public OpenAPI fxDealsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FX Deals Data Warehouse API")
                        .description("A comprehensive API for managing Foreign Exchange deals data warehouse. " +
                                    "This API allows you to submit FX deals, retrieve deal information, " +
                                    "and perform various queries on the deal data.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FX Deals Team")
                                .email("fxdeals@progressoft.com")
                                .url("https://progressoft.com"))
                        .license(new License()
                                .name("ProgressSoft Corporation")
                                .url("https://progressoft.com")));
    }

    /**
     * Configure CORS for cross-origin requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (in production, be more restrictive)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 