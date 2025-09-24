# 📦 How to Use Logging Library from GitHub Packages

## 🔧 Setup for Consumer Projects

Since this library is published to **GitHub Packages**, you need to configure your project to access the GitHub Maven repository.

### 1. 📝 Add Repository Configuration

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/carlosmgv02/logging-library</url>
    </repository>
</repositories>
```

### 2. 🔑 Authentication Setup

GitHub Packages requires authentication. Create a **Personal Access Token (PAT)** with `read:packages` permission.

#### Option A: Using `~/.m2/settings.xml` (Recommended)

Create or update your `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
        </server>
    </servers>
</settings>
```

#### Option B: Using Environment Variables

Set these environment variables:

```bash
export GITHUB_USERNAME=your_github_username
export GITHUB_TOKEN=your_personal_access_token
```

Then use in your `pom.xml`:

```xml
<servers>
    <server>
        <id>github</id>
        <username>${env.GITHUB_USERNAME}</username>
        <password>${env.GITHUB_TOKEN}</password>
    </server>
</servers>
```

### 3. 📦 Add Dependency

Add the logging library dependency to your `pom.xml`:

#### For Production Releases:
```xml
<dependency>
    <groupId>com.carlosmgv02</groupId>
    <artifactId>logging-library</artifactId>
    <version>0.0.3</version>
</dependency>
```

#### For Development Snapshots:
```xml
<dependency>
    <groupId>com.carlosmgv02</groupId>
    <artifactId>logging-library</artifactId>
    <version>0.0.4-SNAPSHOT</version>
</dependency>
```

### 4. 🔄 Complete Example `pom.xml`

Here's a complete example of how to configure your project:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Your project details -->
    <groupId>com.example</groupId>
    <artifactId>my-project</artifactId>
    <version>1.0.0</version>
    
    <!-- Add GitHub Packages repository -->
    <repositories>
        <repository>
            <id>github</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/carlosmgv02/logging-library</url>
        </repository>
    </repositories>
    
    <dependencies>
        <!-- Add logging library dependency -->
        <dependency>
            <groupId>com.carlosmgv02</groupId>
            <artifactId>logging-library</artifactId>
            <version>0.0.3</version>
        </dependency>
        
        <!-- Your other dependencies -->
    </dependencies>
</project>
```

## 🚀 Usage Example

Once configured, you can use the logging library in your code:

```java
import com.carlosmgv02.logginglibrary.CustomLogger;
import com.carlosmgv02.logginglibrary.application.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {
    
    @Autowired
    private LoggingService loggingService;
    
    public void doSomething() {
        // Use the custom logger
        CustomLogger.info("Starting business operation");
        
        try {
            // Your business logic here
            performBusinessLogic();
            
            // Log success with structured data
            loggingService.logInfo("Business operation completed successfully", 
                Map.of("operation", "doSomething", "status", "success"));
                
        } catch (Exception e) {
            // Log error with context
            loggingService.logError("Business operation failed", e,
                Map.of("operation", "doSomething", "error", e.getMessage()));
        }
    }
}
```

## 🔍 Available Versions

### Production Releases
- `0.0.3` - Latest stable release
- `0.0.2` - Previous stable release

### Development Snapshots
- `0.0.4-SNAPSHOT` - Latest development version

Check [Releases](https://github.com/carlosmgv02/logging-library/releases) for the most recent versions.

## 🛠️ Troubleshooting

### Authentication Issues

**Error:** `Could not find artifact com.carlosmgv02:logging-library`

**Solution:** 
1. Verify your GitHub PAT has `read:packages` permission
2. Check that your username and token are correctly configured
3. Ensure the repository URL is exactly: `https://maven.pkg.github.com/carlosmgv02/logging-library`

### Version Not Found

**Error:** `Could not find artifact com.carlosmgv02:logging-library:jar:X.X.X`

**Solution:**
1. Check [available versions](https://github.com/carlosmgv02/logging-library/packages)
2. For snapshots, ensure you're using the exact snapshot version
3. Clear your local Maven cache: `mvn dependency:purge-local-repository`

### Network Issues

**Error:** `Connection refused` or `timeout`

**Solution:**
1. Check your internet connection
2. Verify you can access GitHub Packages: `curl -H "Authorization: token YOUR_TOKEN" https://maven.pkg.github.com/carlosmgv02/logging-library/`
3. Check corporate firewall settings

## 📞 Support

If you encounter issues:

1. **Check Authentication:** Verify your GitHub PAT and permissions
2. **Review Configuration:** Ensure repository URL and server ID match
3. **Clear Cache:** Run `mvn clean` and `mvn dependency:resolve`
4. **Create Issue:** [Report issues here](https://github.com/carlosmgv02/logging-library/issues)

## 🔗 Related Links

- [GitHub Packages Documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
- [Creating Personal Access Tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
- [Maven Settings Reference](https://maven.apache.org/settings.html)

---

**Last Updated:** $(date +"%Y-%m-%d")
