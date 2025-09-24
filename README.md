# Professional Logging Library

[![Version](https://img.shields.io/badge/version-0.0.2-blue.svg)](https://github.com/carlosmgv02/logging-library)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)

## Overview

A **production-ready, enterprise-grade logging library** built with **Hexagonal Architecture** for Spring Boot applications. This library provides centralized logging capabilities with advanced features like automatic system validation, sensitive data filtering, distributed tracing integration, metrics collection, and seamless integration with the ELK stack.

## 🚀 Key Features

### 🏗️ **Enterprise Architecture**
- **Hexagonal Architecture** with clear separation of concerns
- **Domain-driven design** with proper abstractions
- **SOLID principles** implementation
- **Spring Boot auto-configuration** with zero-configuration defaults
- **Startup validation** with comprehensive health checks

### 🔒 **Security & Privacy**
- **Sensitive data filtering** with configurable patterns
- **PII protection** (passwords, tokens, credit cards, SSN)
- **Customizable redaction patterns**
- **Runtime pattern validation**

### 📊 **Observability & Monitoring**
- **Distributed tracing** integration with OpenTelemetry
- **Metrics collection** via Micrometer
- **Automatic trace ID and span ID injection**
- **Performance monitoring** and bottleneck detection
- **Real-time connectivity validation** for external systems

### 🛠️ **Professional Features**
- **Startup system validation** with detailed reporting
- **External system connectivity checks** (Logstash, ELK)
- **Async processing** with configurable buffer sizes
- **Multiple output formats** (JSON, structured, colored console)
- **Graceful degradation** on external system failures
- **Zero-downtime configuration updates**

### ✅ **System Validation**
- **Automatic logging system health checks** on startup
- **Logstash connectivity validation** with real connection tests
- **Appender status verification**
- **Configuration error detection**
- **Customizable validation policies** (strict mode, fail-fast options)

## How to Use

### Step 1: Add the Logging Library to Your Spring Boot Project

To integrate the library into your **Spring Boot** project:

1. Install the library in your local Maven repository:

```bash
mvn clean install
```

2. In your Spring Boot project, add the library dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.carlosmgv02</groupId>
    <artifactId>logging-library</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>
```

### Step 2: Configure Logback

To configure **Logback** to use the logging library and send logs to **Logstash**, add or modify your `logback-spring.xml` or `logback.xml` file in your Spring Boot project as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
   <!-- Custom LogColorizer to colorize logs -->
   <property scope="context" name="COLORIZER_COLORS" value="red@,yellow@,green@,blue@,cyan@"/>
   <conversionRule conversionWord="colorize" converterClass="org.tuxdude.logback.extensions.LogColorizer"/>

   <!-- Appender to send logs to Logstash -->
   <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
      <destination>localhost:5050</destination> <!-- Dirección de Logstash -->
      <encoder class="net.logstash.logback.encoder.LogstashEncoder">
         <customFields>{"service":"${spring.application.name:-undefined-service}"}</customFields>
      </encoder>
   </appender>

   <!-- Console appender to print logs to the console -->
   <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
         <pattern>
            %d{yyyy-MM-dd HH:mm:ss.SSS} [%colorize(%-5level)] %magenta(${spring.application.name:-undefined-service}) [%boldCyan(traceId: %X{traceId}) %boldBlue(spanId: %X{spanId})] [%logger{36}] - %msg%n
         </pattern>
      </encoder>
   </appender>

   <!-- Logger de root -->
   <root level="INFO">
      <appender-ref ref="LOGSTASH"/>
      <appender-ref ref="STDOUT"/>
   </root>
</configuration>
```

### Step 3: Running with Docker Compose

I provide a **Docker Compose** file that sets up **Elasticsearch**, **Logstash**, **Kibana**, and the **OpenTelemetry Collector**. You can download it from the project:

[Download docker-compose.yaml](docker-compose.yaml)

Once you have downloaded the `docker-compose.yaml` file, you can start the entire logging stack by running:

```bash
docker-compose up -d
```

The `docker-compose.yaml` file includes:

- **Elasticsearch** on port `9200`
- **Logstash** on port `5000` (for logs)
- **Kibana** on port `5601`
- **OTLP Collector** on port `4317`
> [!WARNING]
> Check ports at docker-compose.yaml, content might be different.
### Step 4: Automating Data View Creation in Kibana

Once the logs are being ingested into Elasticsearch, you can automate the creation of a **Data View** in **Kibana** using the following API request:

```bash
curl -X POST "http://localhost:5601/api/data_views/data_view" \
-H "Content-Type: application/json" \
-H "kbn-xsrf: true" \
-d '{
  "data_view": {
    "title": "logstash-logs-*",
    "timeFieldName": "@timestamp",
    "name": "Logstash Logs Data View"
  }
}'
```
> [!NOTE]
> Alternatively you can open `http://localhost:5601`and do the following:

#### Access Data Views:
1. In Kibana, go to the **Management** section in the left-hand side menu.
2. In the menu, select **Stack Management**.
3. Under the **Kibana** section, select **Data Views** (formerly known as **Index Patterns**).

#### Create a Data View:
1. Click the **Create data view** button (or similar, depending on your version).
2. In the **Index pattern** field, enter `logstash-logs-*` to match the indices being created by Logstash in Elasticsearch.
   - **Note**: If you're unsure of the exact index name, you can check the current indices in **Index Management**, also within **Stack Management**.

#### Select Time Field:
1. If your Logstash configuration includes a `@timestamp` field, select this as the primary time field.
2. Click **Create data view**.

#### Explore Logs:
1. Once the Data View is created, go to the **Discover** section in the left-hand side menu.
2. Select the newly created Data View (`logstash-logs-*`).
3. You should now be able to see the logs being sent from your application and stored in Elasticsearch.

### Step 5: Testing Locally

Once everything is set up:

1. **Start the application:**

```bash
mvn spring-boot:run -Dspring.application.name=your-application-name
```
> [!NOTE]
> The -Dspring.application.name flag will let logstash know the name of the app, useful when tracking request through different services.

2. **Generate logs** by interacting with your application (e.g., sending requests to your API).

3. **Check logs in Kibana**:
   - Access Kibana at `http://localhost:5601`.
   - Open Analytics > Discover on the left side bar.
   - You should see the Data View named **"Logstash Logs Data View"** in Kibana, where you can explore and visualize the logs generated by your application.

## ⚙️ Advanced Configuration

### System Validation Configuration

The library automatically validates your logging system on startup. You can customize this behavior:

```yaml
logging:
  library:
    # Core library settings
    enabled: true
    metrics-enabled: true
    trace-enabled: true
    service-name: ${spring.application.name}
    buffer-size: 1000
    flush-interval-ms: 5000
    log-level: INFO

    # Validation settings
    validation:
      enabled: true                          # Enable/disable startup validation
      strict-mode: false                     # Fail if any appender has issues
      fail-on-logstash-connection-error: false # Fail if Logstash is unreachable
      fail-on-connection-warnings: false    # Fail on any connection warnings
      validation-delay-ms: 2000             # Wait time before validation

  # Sensitive data filtering
  sensitive-data:
    enabled: true
    patterns:
      - "(?i)password[\\s]*[:=][\\s]*\\S+"
      - "(?i)token[\\s]*[:=][\\s]*\\S+"
      - "(?i)secret[\\s]*[:=][\\s]*\\S+"
      - "(?i)key[\\s]*[:=][\\s]*\\S+"
      - "(?i)authorization[\\s]*[:=][\\s]*\\S+"
      - "\\b\\d{16}\\b"                     # Credit card numbers
      - "\\b\\d{3}-\\d{2}-\\d{4}\\b"        # SSN pattern

  # Demo mode for testing
  demo:
    enabled: false
```

### Validation Messages

The library provides concise validation messages on startup:

#### ✅ Success Messages
```
Logging system: OK - 3 appenders active, Logstash connected
Logging system: OK - 2 appenders active, Local only
```

#### ⚠️ Warning Messages
```
Logging system: OK with warnings - Logstash unreachable
Logging system: OK with warnings - Appender 'FILE' not started, 1 logging errors
```

#### ❌ Error Messages
```
Logging system: FAILED - No appenders started
Logging system: FAILED - Logstash connection error
Logging system: FAILED - 3 system errors found
```

### Customizable Logback Configuration

Our enhanced logback configuration includes:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Dynamic properties from Spring -->
    <springProperty scope="context" name="SERVICE_NAME" source="spring.application.name" defaultValue="undefined-service"/>
    <springProperty scope="context" name="LOGSTASH_HOST" source="logging.logstash.host" defaultValue="localhost"/>
    <springProperty scope="context" name="LOGSTASH_PORT" source="logging.logstash.port" defaultValue="5050"/>

    <!-- Custom LogColorizer -->
    <property scope="context" name="COLORIZER_COLORS" value="red@,yellow@,green@,blue@,cyan@"/>
    <conversionRule conversionWord="colorize" converterClass="org.tuxdude.logback.extensions.LogColorizer"/>

    <!-- Console Appender with colors and tracing -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{HH:mm:ss.SSS} [%colorize(%-5level)] %magenta(${SERVICE_NAME}) [%boldCyan(traceId: %X{traceId:-}) %boldBlue(spanId: %X{spanId:-})] [%logger{36}] - %msg%n
            </pattern>
        </encoder>
    </appender>

    <!-- Async Console Appender for performance -->
    <appender name="ASYNC_CONSOLE" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="CONSOLE"/>
        <queueSize>1000</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <includeCallerData>false</includeCallerData>
    </appender>

    <!-- Logstash TCP Appender with connection settings -->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${LOGSTASH_HOST}:${LOGSTASH_PORT}</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>true</includeContext>
            <includeMdc>true</includeMdc>
            <customFields>{"service":"${SERVICE_NAME}"}</customFields>
            <fieldNames>
                <timestamp>@timestamp</timestamp>
                <message>message</message>
                <level>level</level>
                <thread>thread</thread>
                <logger>logger</logger>
            </fieldNames>
        </encoder>
        <connectionTimeout>5000</connectionTimeout>
        <keepAliveDuration>20000</keepAliveDuration>
    </appender>

    <!-- Async Logstash Appender -->
    <appender name="ASYNC_LOGSTASH" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="LOGSTASH"/>
        <queueSize>1000</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <includeCallerData>false</includeCallerData>
    </appender>

    <!-- File Appender for development -->
    <springProfile name="!prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/${SERVICE_NAME}.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/${SERVICE_NAME}-%d{yyyy-MM-dd}.log.gz</fileNamePattern>
                <maxHistory>30</maxHistory>
                <totalSizeCap>1GB</totalSizeCap>
            </rollingPolicy>
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <includeContext>true</includeContext>
                <includeMdc>true</includeMdc>
            </encoder>
        </appender>
    </springProfile>

    <!-- Root logger with all appenders -->
    <root level="INFO">
        <appender-ref ref="ASYNC_CONSOLE"/>
        <appender-ref ref="ASYNC_LOGSTASH"/>
        <springProfile name="!prod">
            <appender-ref ref="FILE"/>
        </springProfile>
    </root>

    <!-- Reduce noise from frameworks -->
    <logger name="org.springframework" level="WARN"/>
    <logger name="org.hibernate" level="WARN"/>
    <logger name="com.zaxxer.hikari" level="WARN"/>
    <logger name="org.apache" level="WARN"/>
</configuration>
```

### Production Considerations

For production environments:

- **Security**: Enable security (`xpack.security.enabled=true`) and use HTTPS for Elasticsearch
- **Authentication**: Configure RBAC (Role-Based Access Control) and enable authentication in Kibana
- **Monitoring**: Set `fail-on-logstash-connection-error: true` for critical applications
- **Performance**: Tune buffer sizes and flush intervals based on your load
- **Storage**: Configure appropriate log retention policies

---

### Logstash Configuration

This config handles the log ingestion process:

```yaml
input {
    tcp {
        port => 5000
        codec => json_lines
    }
}

output {
    elasticsearch {
        hosts => ["http://elasticsearch:9200"]
        index => "logstash-logs-%{+YYYY.MM.dd}"
    }
    stdout { codec => rubydebug }
}
```

- **Input**: Listens on port `5000` for JSON logs.
- **Output**: Sends logs to **Elasticsearch** and prints them to the console for debugging.

### OpenTelemetry (OTLP) Collector Configuration

This config handles trace collection:

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317

exporters:
  logging:
    loglevel: debug

processors:
  batch:
    timeout: 5s

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [logging]
```

- **Receivers**: Accepts gRPC traces on port `4317`.
- **Exporters**: Logs traces to the console.
- **Processors**: Batches traces for efficiency.


## Author
* **Carlos Martínez García-Villarrubia**
---

https://github.com/user-attachments/assets/baf0e73d-1960-4297-aa3b-dd9c15d668ac



