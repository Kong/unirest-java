# Jackson Object Mapper for Open Unirest
[![Build Status](https://travis-ci.org/OpenUnirest/unirest-java.svg?branch=master)](https://travis-ci.org/OpenUnirest/unirest-java)

Welcome. This is a Object Mapper for Open Unirest based on the popular Jackson JSON parser.
Use it like this:

```java
     Unirest.config().setObjectMapper(new JacksonObjectMapper());
```

You may also provide it with your own com.fasterxml.jackson.databind.ObjectMapper.

## Install With [Maven](https://mvnrepository.com/artifact/io.github.openunirest/)
```
<dependency>
    <groupId>io.github.openunirest</groupId>
        <artifactId>object-mappers-jackson</artifactId>
        <version>3.0.01</version>
</dependency>
```