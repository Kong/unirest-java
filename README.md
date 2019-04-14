# Unirest for Java 

[![Build Status](https://travis-ci.org/Kong/unirest-java.svg?branch=master)](https://travis-ci.org/Kong/unirest-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.konghq/unirest-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.kong/unirest-java)
[![Javadocs](http://www.javadoc.io/badge/com.konghq/unirest-java.svg)](http://www.javadoc.io/doc/com.konghq/unirest-java)


We are looking for official maintainers, check out issue [#252](https://github.com/Kong/unirest-java/issues/252)

## Install With [Maven](https://mvnrepository.com/artifact/com.konghq/unirest-java)[:](https://repo.maven.apache.org/maven2/com/konghq/unirest-java/)
```xml
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-java</artifactId>
    <version>2.2.00</version>
</dependency>
```

### Upgrading from Previous Versions 
See the [Upgrade Guide](UPGRADE_GUIDE.md)

## Features

* Make `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS` requests
* Both synchronous and asynchronous (non-blocking) requests
* It supports form parameters, file uploads and custom body entities
* Easily add route parameters without ugly string concatenations
* Supports gzip
* Supports Basic Authentication natively
* Customizable timeout, concurrency levels and proxy settings
* Customizable default headers for every request (DRY)
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

Requests are made when `as[Type]()` is invoked, possible types include `Json`, `String`, `Object` `Empty` and `File`.


### Route Parameters
Sometimes you want to add dynamic parameters in the URL, you can easily do that by adding a placeholder in the URL, and then by setting the route parameters with the `routeParam` function, like:

```java
Unirest.get("http://httpbin.org/{method}")
  .routeParam("method", "get")
  .asJson();
```
In the example above the final URL will be `http://httpbin.org/get` - Basically the placeholder `{method}` will be replaced with `get`.

The placeholder's format is as easy as: `{custom_name}`

### Advanced Object Mapping with Jackson, GSON, JAX-B or others
Before an `asObject(Class)` or a `.body(Object)` invocation, is necessary to provide a custom implementation of the `ObjectMapper` interface.
This should be done only the first time, as the instance of the ObjectMapper will be shared globally.
Unirest offers a few plug-ins implementing popular object mappers like Jackson and Gson. See [mvn central](https://mvnrepository.com/artifact/com.konghq) for details.
nes of code.
For example, serializing Json from\to Object using the popular Jackson ObjectMapper takes only few li

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

#### Errors in Object or JSON parsing
You can't always get what you want. And sometimes results you get from web services will not map into what you expect them to.
When this happens with a ```asObject``` or ```asJson``` request the resulting body will be null, but the response object will contain a ParsingException that allows you to get the error and the original body for inspection.

```java
UnirestParsingException ex = response.getParsingError().get();

ex.getOriginalBody(); // Has the original body as a string.
ex.getMessage(); // Will have the parsing exception.
ex.getCause(); // of course will have the original parsing exception itself.
```

#### Asynchronous Requests
Sometimes, well most of the time, you want your application to be asynchronous and not block, Unirest supports this in Java using anonymous callbacks, or direct method placement:

```java
CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("param1", "value1")
  .field("param2", "value2")
  .asJsonAsync(response -> {
        int code = response.getStatus();
        JsonNode body = response.getBody();
    });
```

#### Custom mappings and handling large responses
Most response methods (```asString```, ```asJson```, and even ```asBinary```) read the entire
response stream into memory. In order to read the original stream and handle large responses you
can use several functional methods like:

```java
   Map r = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                .getBody();

```

or consumers:

```java

         Unirest.get(MockServer.GET)
                .thenConsumeAsync(r -> {
                       // something like writing a file to disk
                });
```

#### File Uploads
Creating `multipart` requests with Java is trivial, simply pass along a `File` or an InputStream Object as a field:

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("parameter", "value")
  .field("file", new File("/tmp/file"))
  .asJson();
```

#### Custom Entity Body

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .body("{\"parameter\":\"value\", \"foo\":\"bar\"}")
  .asJson();
```

#### JSON Patch Requests
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

#### Basic Authentication
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
           .proxy(new Proxy("https://proxy"))
           .setDefaultHeader("Accept", "application/json")
           .followRedirects(false)
           .enableCookieManagement(false)
           .addInterceptor(new MyCustomInterceptor());
```

#### Config Options

| Builder Method  | Impact | Default |
| ------------- | ------------- | ------------- |
| ```socketTimeout(int)``` | Sets the socket timeout for all requests in millis  | 60000 |
| ```connectTimeout(int)``` | Sets the connection timeout for all requests in millis  | 10000 |
| ```concurrency(int, int)``` | Sets concurrency rates; max total, max per route  | 200, 20 |
| ```proxy(proxy)``` | Sets a proxy object for negotiating proxy servers. Can include auth credentials  |  |
| ```setDefaultHeader(String, String)``` | Sets  a default header. Will overwrite if it exists  |  |
| ```setDefaultHeader(String, Supplier<String>)``` | Sets a default header by supplier. Good for setting trace tokens for microservice architectures. Will overwrite if it exists  |  |
| ```addDefaultHeader(String, String)``` | Adds a default header. Multiple for the same name can exist  |  |
| ```addDefaultHeader(String, Supplier<String>)``` | Add a default header by supplier. Good for setting trace tokens for microservice architectures.  |  |
| ```setDefaultBasicAuth(String, String)``` | Add a default Basic Auth Header |  |
| ```followRedirects(boolean)``` | toggle following redirects | true |
| ```enableCookieManagement(boolean)``` | toggle accepting and storing cookies | true |
| ```cookieSpec(String)``` | set a cookie policy. Acceptable values: 'default' (same as Netscape), 'netscape', 'ignoreCookies', 'standard' (RFC 6265 interoprability profile) , 'standard-strict' (RFC 6265 strict profile) | default |
| ```automaticRetries(boolean)``` | toggle disabling automatic retries (up to 4 times) for socket timeouts | true |
| ```verifySsl(boolean)``` |toggle enforcing SSL | true |
| ```addShutdownHook(boolean)``` | toggle to add the clients to the system shutdown hooks automatically | false |
| ```clientCertificateStore(String,String)``` | Add a PKCS12 KeyStore by path for doing client certificates |  |
| ```clientCertificateStore(KeyStore,String)``` | Add a PKCS12 KeyStore for doing client certificates |  |



#### Changing the config
Changing Unirest's config should ideally be done once, or rarely. There are several background threads spawned by both Unirest itself and Apache HttpAsyncClient. Once Unirest has been activated configuration options that are involved in creating the client cannot be changed without an explicit shutdown or reset.

```Java
     Unirest.config()
            .reset()
            .connectTimeout(5000)
```

#### Setting custom Apache Client
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




## Exiting an application

Unirest starts a background event loop and your Java application won't be able to exit until you manually shutdown all the threads by invoking:

```java
Unirest.shutdown();
```

Once shutdown, using Unirest again will re-init the system


