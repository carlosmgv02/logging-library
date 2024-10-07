# Logging Library

## Description

This **logging library** is designed to centralize and send logs from multiple microservices to an **Elasticsearch** and **Logstash** stack for centralized log analysis. It helps manage traceability across services using **traceId** and **spanId** and handles exceptions in **Spring Boot** applications.


### Features:
- Sends logs to **Logstash** via TCP.
- Integrates with **Elasticsearch** for storing logs and visualizing them in **Kibana**.
- Automates the creation of **Data Views** in **Kibana** through an API.

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
    <version>0.0.1-SNAPSHOT</version>
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
> Check ports at docker-compose.yaml, contend might be different.
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

### Step 6: Future improvements

For production:

- Enable security (`xpack.security.enabled=true`) and use HTTPS for Elasticsearch.
- Configure RBAC (Role-Based Access Control) to restrict access based on roles.
- Enable authentication in Kibana.

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



