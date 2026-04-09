package com.secondhand.marketplace.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "test")
@Data
public class TestConfig {
    private boolean enabled;
    private Long defaultUserId;
    private Long defaultAdminId;
}
