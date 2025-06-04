package top.thesumst.llm_eval_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration
 */
@Configuration
@EnableJpaRepositories(basePackages = "top.thesumst.llm_eval_backend.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // Additional database configurations can be added here if needed
} 