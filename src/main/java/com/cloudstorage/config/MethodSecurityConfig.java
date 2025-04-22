// src/main/java/com/cloudstorage/config/MethodSecurityConfig.java

package com.cloudstorage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // ✅ Enables use of @PreAuthorize
public class MethodSecurityConfig {
}
