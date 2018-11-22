# Unirest for Java 

[![Build Status](https://travis-ci.org/OpenUnirest/unirest-java.svg?branch=master)](https://travis-ci.org/OpenUnirest/unirest-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.openunirest/unirest-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.openunirest/unirest-java)
[![DepShield Badge](https://depshield.sonatype.org/badges/OpenUnirest/unirest-java/depshield.svg)](https://depshield.github.io)

## About This Repository
This repo is an updated, maintained, and independent fork of the original Mashape/Kong Unirest-Java project. That project is no longer being maintained so this project was set up to keep it alive.

##### See the [UPGRADE_GUIDE](UPGRADE_GUIDE.md) for differences between this library and last kong release.

## Install With [Maven](https://mvnrepository.com/artifact/io.github.openunirest/open-unirest-java)
```
<dependency>
    <groupId>io.github.openunirest</groupId>
    <artifactId>open-unirest-java</artifactId>
    <version>3.0.00</version>
</dependency>
```

#### Note on 2.x 
Because 3.0 made several very not backwards compatible changes the artifactID was changed. The 2.0 line is still available [Here](https://mvnrepository.com/artifact/io.github.openunirest/unirest-java). Security patches and major defects will still be patched on it until 2020.


## Features

* Make `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS` requests
* Both synchronous and asynchronous (non-blocking) requests
* It supports form parameters, file uploads and custom body entities
* Easily add route parameters without ugly string concatenations
* Supports gzip
* Supports Basic Authentication natively
* Customizable timeout, concurrency levels and proxy settings
* Customizable default headers for every request (DRY)
* Customizable `HttpClient` and `HttpAsyncClient` implementation
* Automatic JSON parsing into a native object for JSON responses
* Customizable binding, with mapping from response body to java Object 


## Creating Request
So you're probably wondering how using Unirest makes creating requests in Java easier, here is a basic POST request that will explain everything:

```java
HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post")
      .header("accept", "application/json")
      .queryString("apiKey", "123")
      .field("parameter", "value")
      .field("foo", "bar")
      .asJson();
```

Requests are made when `as[Type]()` is invoked, possible types include `Json`, `Binary`, `String`, `Object`.

If the request supports and it is of type `HttpRequestWithBody`, a body it can be passed along with `.body(String|JsonNode|Object)`. For using `.body(Object)` some pre-configuration is needed (see below).

If you already have a map of parameters or do not wish to use seperate field methods for each one there is a `.fields(Map<String, Object> fields)` method that will serialize each key - value to form parameters on your request.

`.headers(Map<String, String> headers)` is also supported in replacement of multiple header methods.

## JSON Patch Requests
Unirest has full native support for JSON Patch requests
```java
     Unirest.jsonPatch(MockServer.PATCH)
            .add("/fruits/-", "Apple")
            .remove("/bugs")
            .replace("/lastname", "Flintstone")
            .test("/firstname", "Fred")
            .move("/old/location", "/new/location")
            .copy("/original/location", "/new/location")
            .asJson();
```
will send a request with a body of
```json
  [
     {"op":"add","path":"/fruits/-","value":"Apple"},
     {"op":"remove","path":"/bugs"},
     {"op":"replace","path":"/lastname","value":"Flintstone"},
     {"op":"test","path":"/firstname","value":"Fred"},
     {"op":"move","path":"/new/location","from":"/old/location"},
     {"op":"copy","path":"/new/location","from":"/original/location"}
  ]

```

## Advanced Object Mapping with Jackson, GSON, JAX-B or others
Before an `asObject(Class)` or a `.body(Object)` invokation, is necessary to provide a custom implementation of the `ObjectMapper` interface.
This should be done only the first time, as the instance of the ObjectMapper will be shared globally.
Open Unirest offers a few plug-ins implementing popular object mappers like Jackson and Gson. See [mvn central](https://mvnrepository.com/artifact/io.github.openunirest) for details.

For example, serializing Json from\to Object using the popular Jackson ObjectMapper takes only few lines of code.

```java
// Only one time
Unirest.config().setObjectMapper(new JacksonObjectMapper());

// Response to Object
Book book = Unirest.get("http://httpbin.org/books/1")
                   .asObject(Book.class)
                   .getBody();

Author author = Unirest.get("http://httpbin.org/books/{id}/author")
                       .routeParam("id", bookObject.getId())
                       .asObject(Author.class)
                       .getBody();

// Sending a JSON object
Unirest.post("http://httpbin.org/authors/post")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(author)
        .asJson();
```

### Route Parameters
Sometimes you want to add dynamic parameters in the URL, you can easily do that by adding a placeholder in the URL, and then by setting the route parameters with the `routeParam` function, like:

```java
Unirest.get("http://httpbin.org/{method}")
  .routeParam("method", "get")
  .queryString("name", "Mark")
  .asJson();
```
In the example above the final URL will be `http://httpbin.org/get` - Basically the placeholder `{method}` will be replaced with `get`.

The placeholder's format is as easy as: `{custom_name}`

## Asynchronous Requests
Sometimes, well most of the time, you want your application to be asynchronous and not block, Unirest supports this in Java using anonymous callbacks, or direct method placement:

```java
CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("param1", "value1")
  .field("param2", "value2")
  .asJsonAsync(response -> {
        int code = response.getStatus();
        Map<String, String> headers = response.getHeaders();
        JsonNode body = response.getBody();
        InputStream rawBody = response.getRawBody();
    });
```

## File Uploads
Creating `multipart` requests with Java is trivial, simply pass along a `File` or an InputStream Object as a field:

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("parameter", "value")
  .field("file", new File("/tmp/file"))
  .asJson();
```

## Custom Entity Body

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .body("{\"parameter\":\"value\", \"foo\":\"bar\"}")
  .asJson();
```

## Byte Stream as Entity Body

```java
final InputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));
final byte[] bytes = new byte[stream.available()];
stream.read(bytes);
stream.close();
final HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .field("name", "Mark")
  .field("file", bytes, "image.jpg")
  .asJson();
```

## InputStream as Entity Body

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .field("name", "Mark")
  .field("file", new FileInputStream(new File(getClass().getResource("/image.jpg").toURI())), ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
  .asJson();
```

## Basic Authentication
Authenticating the request with basic authentication can be done by calling the `basicAuth(username, password)` function:
```java
 Unirest.get("http://httpbin.org/headers")
        .basicAuth("username", "password")
        .asJson();
```

# Configuration
Previous versions of unirest had configuration split across several different places. Sometimes it was done on ```Unirest```, sometimes it was done on ```Option```, sometimes it was somewhere else. 
All configuration is now done through ```Unirest.config()```

#### Unirest.config()
Unirest config allows easy access to build a configuration just like you would build a request:

```java
    Unirest.config()
           .socketTimeout(500)
           .connectTimeout(1000)
           .concurrency(10, 5)
           .proxy(new HttpHost("https://proxy"))
           .setDefaultHeader("Accept", "application/json")
           .followRedirects(false)
           .enableCookieManagement(false)
           .addInterceptor(new MyCustomInterceptor());
```

##### Changing the config
Changing Unirest's config should ideally be done once, or rarely. There are several background threads spawned by both Unirest itself and Apache HttpAsyncClient. Once Unirest has been activated configuration options that are involved in creating the client cannot be changed without an explicit shutdown or reset.

```Java
     Unirest.config()
            .reset()
            .connectTimeout(5000)
```

##### Setting custom Apache Client
You can set your own custom Apache HttpClient and HttpAsyncClient. Note that Unirest settings like timeouts or interceptors are not applied to custom clients.

```java
     Unirest.config()
            .httpClient(myClient)
            .asyncClient(myAsyncClient)
```

#### Multiple Configuration Instances
As usual, Unirest maintains a primary single instance. Sometimes you might want different configurations for different systems. You might also want an instance rather than a static context for testing purposes.

```java

    // this returns the same instance used by Unirest.get("http://somewhere/")
    UnirestInstance unirest = Unirest.primaryInstance(); 
    // It can be configured and used just like the static context
    unirest.config().connectTimeout(5000);
    String result = unirest.get("http://foo").asString().getBody();
    
    // You can also get a whole new instance
    UnirestInstance unirest = Unirest.spawnInstance();
```

**WARNING!** If you get a new instance of unirest YOU are responsible for shutting it down when the JVM shuts down. It is not tracked or shut down by ```Unirest.shutDown();```




# Exiting an application

Unirest starts a background event loop and your Java application won't be able to exit until you manually shutdown all the threads by invoking:

```java
Unirest.shutdown();
```

Once shutdown, using Unirest again will re-init the system


