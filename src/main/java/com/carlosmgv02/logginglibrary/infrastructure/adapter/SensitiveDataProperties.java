package com.carlosmgv02.logginglibrary.infrastructure.adapter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "logging.sensitive-data")
public class SensitiveDataProperties {
    private boolean enabled = true;
    private List<String> patterns = List.of(
            "(?i)password[\\s]*[:=][\\s]*\\S+",
            "(?i)token[\\s]*[:=][\\s]*\\S+",
            "(?i)secret[\\s]*[:=][\\s]*\\S+",
            "(?i)key[\\s]*[:=][\\s]*\\S+",
            "(?i)authorization[\\s]*[:=][\\s]*\\S+",
            "\\b\\d{16}\\b",
            "\\b\\d{3}-\\d{2}-\\d{4}\\b"
    );
}