# Jackson Object Mapper for Unirest

Welcome. This is a Object Mapper for Unirest based on the popular Jackson JSON parser.
Use it like this:

```java
     Unirest.config().setObjectMapper(new JacksonObjectMapper());
```

You may also provide it with your own com.fasterxml.jackson.databind.ObjectMapper.

## Install With [Maven](https://mvnrepository.com/artifact/com.konghq/)
```
<dependency>
    <groupId>com.konghq</groupId>
        <artifactId>unirest-objectmapper-jackson</artifactId>
        <version>3.0.01</version>
</dependency>
```