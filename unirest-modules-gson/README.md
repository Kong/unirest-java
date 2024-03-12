# Gson Object Mapper for Unirest

Welcome. This is a Object Mapper for Unirest based on the popular google Gson JSON parser.
Use it like this:

```java
     Unirest.config().setObjectMapper(new GsonObjectMapper());
```

You may also provide it with your own Gson instance.

## Install With [Maven](https://mvnrepository.com/artifact/com.konghq/unirest-object-mappers-gson)
```
<dependency>
    <groupId>com.konghq</groupId>
        <artifactId>unirest-object-mappers-gson</artifactId>
        <version>2.0.00</version>
</dependency>
```