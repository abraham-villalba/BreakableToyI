package com.todos.backend.backend_todos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to set up CORS (Cross-Origin Resource Sharing) settings for the application.
 * This allows the backend to handle requests from different origins, which is essential for
 * enabling communication between the frontend and backend when they are hosted on different domains.
 */
@Configuration
public class WebConfig {

    /**
     * Bean to configure CORS settings.
     * 
     * @return a WebMvcConfigurer with customized CORS settings.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Configure CORS mappings.
             * 
             * @param registry the CorsRegistry to add mappings to.
             */
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:8080") // Allow requests from this origin
                    .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
                    .allowedHeaders("*") // Allow all headers
                    .allowCredentials(true); // Allow credentials (cookies, authorization headers, etc.)
            }
        };
    }
}
