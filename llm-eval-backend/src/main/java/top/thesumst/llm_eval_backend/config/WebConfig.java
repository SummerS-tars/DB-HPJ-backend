package top.thesumst.llm_eval_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration including CORS settings
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethods;
    
    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;
    
    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Parse allowed origins/patterns
        String[] origins = allowedOrigins.split(",");
        String[] methods = allowedMethods.split(",");
        String[] headers = allowedHeaders.split(",");
        
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origins)
                .allowedMethods(methods)
                .allowedHeaders(headers)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
        
        // Also add mapping for actuator endpoints (health checks)
        registry.addMapping("/actuator/**")
                .allowedOriginPatterns(origins)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders(headers)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
} 