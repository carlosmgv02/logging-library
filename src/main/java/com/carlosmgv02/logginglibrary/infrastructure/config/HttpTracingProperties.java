package com.carlosmgv02.logginglibrary.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "logging.http.tracing")
public class HttpTracingProperties {

    private boolean enabled = true;

    private List<String> excludePatterns = new ArrayList<>();

    private boolean logRequests = true;

    private boolean logResponses = true;

    private boolean includeHeaders = false;

    private boolean includePayload = false;

    private int maxPayloadLength = 1000;

    private List<String> excludeHeaders = List.of(
            "authorization", "cookie", "set-cookie", "x-api-key", "x-auth-token"
    );

    public HttpTracingProperties() {
        excludePatterns.add("/actuator/**");
        excludePatterns.add("/health/**");
        excludePatterns.add("/metrics/**");
        excludePatterns.add("/favicon.ico");
    }
}