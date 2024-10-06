# Logging Library

## Description

This **logging library** is designed to centralize and send logs from multiple microservices to an **Elasticsearch** and **Logstash** stack for centralized log analysis. It helps manage traceability across services using **traceId** and **spanId** and handles exceptions in **Spring Boot** applications.

Logs are sent to **Logstash**, processed, and then stored in **Elasticsearch**. You can visualize these logs in **Kibana** using a **Data View** that can be automated via an API request.

### Features:
- Automatic capture and management of **traceId** and **spanId** to trace requests across microservices.
- Sends logs to **Logstash** via TCP.
- Integrates with **Elasticsearch** for storing logs and visualizing them in **Kibana**.
- Automates the creation of **Data Views** in **Kibana** through an API.

## How to Use

### Step 1: Add the Logging Library to Your Spring Boot Project

You can integrate the logging library into your **Spring Boot** application by including it as a dependency. If you've built the library locally:

1. Install the library in your local Maven repository:

```bash
mvn clean install
```

2. In your Spring Boot project, add the library dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.carlosmgv02</groupId>
    <artifactId>logging-library</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Step 2: Configure Logback

To configure **Logback** to use the logging library and send logs to **Logstash**, add or modify your `logback-spring.xml` or `logback.xml` file in your Spring Boot project as follows:

```xml
<configuration>
    <!-- Console Appender with traceId and spanId -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                [%boldCyan(traceId: %X{traceId}) %boldBlue(spanId: %X{spanId})] %d{yyyy-MM-dd HH:mm:ss.SSS} [%level] %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>

    <!-- Logstash Appender -->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>localhost:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"${spring.application.name:-undefined-service}"}</customFields>
            <pattern>
                {"@timestamp":"%d{yyyy-MM-dd'T'HH:mm:ss.SSSZZ}","level":"%level","logger":"%logger","traceId":"%X{traceId}","spanId":"%X{spanId}"}
            </pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="LOGSTASH" />
    </root>
</configuration>
```

### Step 3: Running with Docker Compose

We provide a **Docker Compose** file that sets up **Elasticsearch**, **Logstash**, **Kibana**, and the **OpenTelemetry Collector**. You can download it from the project:

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

### Step 5: Testing Locally

Once everything is set up:

1. **Start the application:**

```bash
mvn spring-boot:run
```

2. **Generate logs** by interacting with your application (e.g., sending requests to your API).

3. **Check logs in Kibana**:
   - Access Kibana at `http://localhost:5601`.
   - You should see the Data View named **"Logstash Logs Data View"** in Kibana, where you can explore and visualize the logs generated by your application.

### Step 6: Securing Elasticsearch and Kibana

When running in production, you should secure access to **Elasticsearch** and **Kibana**. You can do this by:

- **Enabling security features** in Elasticsearch (`xpack.security.enabled=true`) and using HTTPS.
- **Configuring role-based access control (RBAC)** to restrict access to the logs based on user roles.
- **Enabling authentication in Kibana**, requiring users to log in with credentials.

---

https://github.com/user-attachments/assets/baf0e73d-1960-4297-aa3b-dd9c15d668ac



