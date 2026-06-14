# Bug Report: `spring.data.mongodb.uri` credentials ignored in Spring Boot 4.0.6

**Spring Boot version:** 4.0.6
**MongoDB driver version:** 5.6.5 (shipped with starter)
**MongoDB server version:** 8.3.4
**Java version:** 25.0.3 (Eclipse Adoptium)
**OS:** Linux

---

## Description

When configuring MongoDB via `spring.data.mongodb.uri` in `application.yaml`, the `MongoClient` is created with `credential=null`, ignoring the username and password embedded in the connection string. This causes authentication to fail with error code 13 (`Command listDatabases requires authentication`).

## Expected Behavior

The `MongoClient` should be created with `MongoCredential{userName='admin', source='admin'}` extracted from the URI.

## Actual Behavior

`MongoClientSettings` shows `credential=null`, even though the URI contains valid credentials at `mongodb://admin:admin123@localhost:27017/medicare?authSource=admin`.

```yaml
# application.yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:admin123@localhost:27017/medicare?authSource=admin
```

Log output:
```
MongoClientSettings{... credential=null ...}
```

## Steps to Reproduce

1. Create a Spring Boot 4.0.6 project with `spring-boot-starter-data-mongodb`
2. Configure `spring.data.mongodb.uri` in `application.yaml` with a URI that includes credentials (e.g., `mongodb://user:pass@localhost:27017/db?authSource=admin`)
3. Start the application
4. Check the logs — the `MongoClient` will have `credential=null`

## Workaround

Creating a `@Configuration` class with a manual `MongoClient` bean resolves the issue:

```java
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "medicare");
    }
}
```

With this workaround, the logs correctly show:
```
credential=MongoCredential{mechanism=null, userName='admin', source='admin', password=<hidden>, ...}
```

## Additional Notes

- The individual properties (`spring.data.mongodb.username`, `spring.data.mongodb.password`, `spring.data.mongodb.authentication-database`) also do not work — they are also ignored.
- The `spring.data.mongodb.database` and `spring.data.mongodb.host` / `spring.data.mongodb.port` properties are correctly picked up.
- This worked correctly in Spring Boot 3.x.
- No profile-specific configuration files are present. Only the default `application.yaml`.
