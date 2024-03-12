---
layout: default
title: Documentation
rightmenu: true
---

<div id="spy-nav" class="right-menu" markdown="1">
- [Requests](#requests)
  - [Route Parameters](#route-parameters)
  - [Query Parameters](#query-parameters)
  - [Headers](#headers)
    - [Basic Authentication](#basic-authentication)
  - [Body Data](#body-data)
    - [Entity Bodies](#entity-bodies)
    - [JSON Patch Bodies](#json-patch-bodies)
    - [Basic Forms](#basic-forms)
    - [File Uploads](#file-uploads)
    - [Upload Progress Monitoring](#upload-progress-monitoring)
  - [Asynchronous Requests](#asynchronous-requests)
  - [Paged Requests](#paged-requests)
  - [Client Certificates](#client-certificates)
  - [Proxies](#proxies)
- [Responses](#responses)
  - [Empty Responses](#empty-responses)
  - [String Responses](#string-responses)
  - [Object Mapped Responses](#object-mapped-responses)
    - [Errors in Object or JSON parsing](#errors-in-object-or-json-parsing)
    - [Mapping Error Objects](#mapping-error-objects)
    - [Mapping one body type to another without an object mapper](#mapping-one-body-type-to-another-without-an-object-mapper)
  - [File Responses](#file-responses)
  - [JSON responses](#json-responses)
  - [Large Responses](#large-responses)
  - [Error Handling](#error-handling)
  - [Caching](#caching)
- [Configuration](#configuration)
  - [Config Options](#config-options)
  - [Multiple Configurations](#multiple-configurations)
  - [Object Mappers](#object-mappers)
  - [Metrics](#metrics)
- [Shutting Down](#shutting-down)
</div>

<h1 class="no-margin-top">Documentation</h1>

### Install With [Maven](https://mvnrepository.com/artifact/com.konghq/unirest-java)[:](https://repo.maven.apache.org/maven2/com/konghq/unirest-java/)
```xml
<dependencyManagement>
    <dependencies>
        <!-- https://mvnrepository.com/artifact/com.konghq/unirest-java-bom -->
        <dependency>
            <groupId>com.konghq</groupId>
            <artifactId>unirest-java-bom</artifactId>
            <version>4.3.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
<!-- https://mvnrepository.com/artifact/com.konghq/unirest-java-core -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-java-core</artifactId>
</dependency>

<!-- pick a JSON module if you want to parse JSON include one of these: -->
<!-- Google GSON -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-modules-gson</artifactId>
</dependency>

<!-- OR maybe you like Jackson better? -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-modules-jackson</artifactId>
</dependency>
</dependencies>
```

### Upgrading from Previous Versions
See the [Upgrade Guide](https://github.com/Kong/unirest-java/blob/master/UPGRADE_GUIDE.md)

### ChangeLog 
See the [Change Log](https://github.com/Kong/unirest-java/blob/master/CHANGELOG.md) for recent changes.

# Requests
So you're probably wondering how using Unirest makes creating requests in Java easier, here is a basic POST request that will explain everything:

```java
HttpResponse<JsonNode> response = Unirest.post("http://localhost/post")
      .header("accept", "application/json")
      .queryString("apiKey", "123")
      .field("parameter", "value")
      .field("firstname", "Gary")
      .asJson();
```

Requests are made when `as[Type]()` is invoked, possible types include `Json`, `String`, `Object` `Empty` and `File`.


## Route Parameters
Sometimes you want to add dynamic parameters in the URL, you can easily do that by adding a placeholder in the URL, and then by setting the route parameters with the `routeParam` function, like:

```java
Unirest.get("http://localhost/{fruit}")
     .routeParam("fruit", "apple")
     .asString();

// Results in `http://localhost/apple`
```
The placeholder `{fruit}` will be replaced with `apple`.

The placeholder's format is as easy as wrapping in curly braces: `{custom_name}`

All param values will be URL-Encoded for you

## Default Base URLs
You  can configure a default base URL to be used for all requests that do not contain a full URL.

This configuration will result in a GET to "http://homestar.com/runner"
```java
   Unirest.config().defaultBaseUrl("http://homestar.com");
    
   Unirest.get("/runner").asString();
``` 

## Query Parameters
Query-string params can be built up one by one

```java
Unirest.get("http://localhost")
                .queryString("fruit", "apple")
                .queryString("droid", "R2D2")
                .asString();

// Results in "http://localhost?fruit=apple&droid=R2D2"
```

Again all param values will be URL-Encoded.

You can also pass in query strings as arrays and maps:
```java
Unirest.get("http://localhost")
        .queryString("fruit", Arrays.asList("apple", "orange"))
        .queryString(ImmutableMap.of("droid", "R2D2", "beatle", "Ringo"))
        .asString();

 // Results in "http://localhost?fruit=apple&fruit=orange&droid=R2D2&beatle=Ringo"
```

## Headers
Request headers can be added with the ```header``` method.
```java
Unirest.get("http://localhost")
            .header("Accept", "application/json")
            .header("x-custom-header", "hello")
            .asString();
```

### Basic Authentication
Unirest exposes a shortcut for doing basic auth when you need to. Unirest handles the Base64 encoding part.
Please make sure you are always doing this over HTTPS!

```java
Unirest.get("http://localhost")
            .basicAuth("user", "password1!")
            .asString();

// this adds the header "Authorization: Basic dXNlcjpwYXNzd29yZDEh"
```

## Body Data

### Entity Bodies
You can post entity objects as the full body easily. This is the default behavior of most REST services.

Unless you specify otherwise the default ```Content-Type``` is ```text/plain; charset=UTF-8```

```java
Unirest.post("http://localhost")
                .body("This is the entire body")
                .asEmpty();
```

You can also post as a Object that is serialized using a configured ObjectMapper. (see [Object Mappers](#object-mappers) for implementation details.
Unirest comes with a default mapper that will serialize to json using the popular Google Gson library
```java
Unirest.post("http://localhost")
            .header("Content-Type", "application/json")
            .body(new SomeUserObject("Bob"))
            .asEmpty();

// This will use Jackson to serialize the object into JSON.
```

### JSON Patch Bodies
Unirest has full native support for JSON Patch requests (RFC-6902 see <http://jsonpatch.com/>)
Per the spec, the default ```Content-Type``` for json-patch is ```application/json-patch+json```

```java
     Unirest.jsonPatch("http://localhost")
            .add("/fruits/-", "Apple")
            .remove("/bugs")
            .replace("/lastname", "Flintstone")
            .test("/firstname", "Fred")
            .move("/old/location", "/new/location")
            .copy("/original/location", "/new/location")
            .asJson();
```
will send a request with a body of
```js
  [
     {"op":"add","path":"/fruits/-","value":"Apple"},
     {"op":"remove","path":"/bugs"},
     {"op":"replace","path":"/lastname","value":"Flintstone"},
     {"op":"test","path":"/firstname","value":"Fred"},
     {"op":"move","path":"/new/location","from":"/old/location"},
     {"op":"copy","path":"/new/location","from":"/original/location"}
  ]
```

### Basic Forms
Basic http name value body params can be passed with simple field calls.
The ```Content-Type``` for this type of request is defaulted to  ```application/x-www-form-urlencoded```

```java
Unirest.post("http://localhost")
       .field("fruit", "apple")
       .field("droid", "R2D2")
       .asEmpty();

  // This will post a simple name-value pair body the same as a HTML form. This looks like
  // `fruit=apple&droid=R2D2'
```

### File Uploads
You can also post binary data in a form. Like a file.

The ```Content-Type``` for this type of request is defaulted to  ```multipart/form-data```

```java
Unirest.post("http://localhost")
       .field("upload", new File("/MyFile.zip"))
       .asEmpty();
```

For large files you may want to use a InputStream. Pass it a file name if you want one.
We are using a FileInputStream here but it can actually be any kind of InputStream.

```java
InputStream file = new FileInputStream(new File("/MyFile.zip"));

Unirest.post("http://localhost")
       .field("upload", file, "MyFile.zip")
       .asEmpty();
```

### Upload Progress Monitoring
If you are uploading large files you might want to provide some time of progress bar to a user. You can monitor this progress by providing a ProgresMonitor.

```java
          Unirest.post("http://localhost")
                .field("upload", new File("/MyFile.zip"))
                .uploadMonitor((field, fileName, bytesWritten, totalBytes) -> {
                    updateProgressBarWithBytesLeft(totalBytes - bytesWritten);
                })
                .asEmpty();
```

## Asynchronous Requests
Sometimes, well most of the time, you want your application to be asynchronous and not block, Unirest supports this in Java using anonymous callbacks, or direct method placement. All request types also support async versions.

```java
CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post("http://localhost/post")
  .header("accept", "application/json")
  .field("param1", "value1")
  .field("param2", "value2")
  .asJsonAsync(response -> {
        int code = response.getStatus();
        JsonNode body = response.getBody();
    });
```

## Paged Requests
Sometimes services offer paged requests. How this is done is not standardized but Unirest proves a mechanism to follow pages until all have been consumed. You must provide two functions for extracting the next page. The first is to get the HttpResponse in the format you want, the other is to extract the ```next``` link from the response. The result is a ```PagedList``` of ```HttpResponse<T>```. The paged list has some handy methods for dealing with the results. Here we are getting a paged list of Dogs where the ```next``` link is in the headers.

```java
PagedList<Doggos> result =  Unirest.get("https://somewhere/dogs")
                .asPaged(
                        r -> r.asObject(Doggos.class),
                        r -> r.getHeaders().getFirst("nextPage")
                );
                
```

## Client Certificates
In case you need to use a custom client certificate to call a service you can provide unirest with a custom keystore.
You may either pass a KeyStore object or a path to a valid PKCS#12 keystore file.

```java
Unirest.config()
  .clientCertificateStore("/path/mykeystore.p12", "password1!");

Unirest.get("https://some.custom.secured.place.com")
                .asString();
```

## Proxies
Sometimes you need to tunnel through a proxy. Unirest can be configured to do this. Note that authenticated proxies cannot be configured on a per-request basis unless you want to build it into the URL itself.

```java
    // Configure with authentication:
    Unirest.config().proxy("proxy.com", 7777, "username", "password1!");

    // or without
    Unirest.config().proxy("proxy.com", 7777);

    // or pass it in the request. This will override any proxy done in the config
    // currently only unauthenticated proxies work
    Unirest.get(MockServer.GET)
                    .proxy("proxy.com", 7777)
                    .asString();
```

# Responses
Unirest makes the actual request the moment you invoke of it's ```as[type]``` method. These methods also inform Unirest what type to map the response to. Options are ```Empty```, ```String```, ```File```, ```Object```, ```byte``` and ```Json```.

The response returns as a ```HttpResponse<T>``` where the ```HttpResponse``` object has all of the common response data like status and headers. The Body (if present) can be accessed via the desired type with the ```.getBody()``` method. 

## Empty Responses
If you aren't expecting a body back, ```asEmpty``` is the easiest choice. You will still get back response information like status and headers.

```java
HttpResponse response = Unirest.delete("http://localhost").asEmpty()
```

## String Responses
The next easiest response type is String. You can do whatever you want with it after that.

```java
String body = Unirest.get("http://localhost")
					 .asString()
					 .getBody();
```

## Object Mapped Responses
Most of the time when consuming RESTful services you probably want to map the response into an object. 

For this you need to provide the Unirest configuration with a implementation of ```ObjectMapper``` (see [Object Mappers](#object-mappers) for details.).

If the response is JSON you are in luck and Unirest comes with a basic ```JsonObjectMapper``` basic on Google GSON  

Before an `asObject(Class)` it is necessary to provide a custom implementation of the `ObjectMapper` interface (if you do not wish to use the default mapper). This should be done only the first time, as the instance of the ObjectMapper will be shared globally.

Unirest offers a few plug-ins implementing popular object mappers like Jackson and Gson. See [mvn central](https://mvnrepository.com/artifact/com.konghq) for details.

For example, 
```java
// Response to Object
Book book = Unirest.get("http://localhost/books/1")
                   .asObject(Book.class)
                   .getBody();

// Generic types can be resolved by using a GenericType subclass to avoid erasure
List<Book> books = Unirest.get("http://localhost/books/")
			  .asObject(new GenericType<List<Book>>(){})
			  .getBody();

Author author = Unirest.get("http://localhost/books/{id}/author")
                       .routeParam("id", bookObject.getId())
                       .asObject(Author.class)
                       .getBody();
```

### Errors in Object or JSON parsing
You can't always get what you want. And sometimes results you get from web services will not map into what you expect them to.
When this happens with a ```asObject``` or ```asJson``` request the resulting body will be null, but the response object will contain a ParsingException that allows you to get the error and the original body for inspection.

```java
UnirestParsingException ex = response.getParsingError().get();

ex.getOriginalBody(); // Has the original body as a string.
ex.getMessage(); // Will have the parsing exception.
ex.getCause(); // of course will have the original parsing exception itself.
```

### Mapping Error Objects
Sometimes with REST API's the service will return a error object that can be parsed. You can optionally map this into an POJO like

```java
    HttpResponse<Book> book = Unirest.get("http://localhost/books/{id}")
                                     .asObject(Book.class);

    // This will be null if there wasn't an error
    Error er = book.mapError(Error.class);

    // You can also take advantage of this inside of the ifFailure method
    Unirest.get("http://localhost/books/{id}")
           .asObject(Book.class)
           .ifFailure(Error.class, r -> {
                    Error e = r.getBody();
           });
```

### Mapping one body type to another without an object mapper
If you don't want to provide a full ObjectMapper implementation you may  use  a simple function to map the response

```java
    int body = Unirest.get("http://httpbin/count")
                      .asString()
                      .mapBody(Integer::valueOf);
```

## File Responses
Sometimes you just want to download a file, or maybe capture the response body into a file. Unirest can do both. Just tell Unirest where you want to put the file.

```java
File result = Unirest.get("http://some.file.location/file.zip")
                .asFile("/disk/location/file.zip")
                .getBody();
```

### Download Progress Monitoring
If you are uploading large files you might want to provide some time of progress bar to a user. You can monitor this progress by providing a ProgresMonitor.

```java
          Unirest.get("http://localhost")
                .downLoadMonitor((b, fileName, bytesWritten, totalBytes) -> {
                    updateProgressBarWithBytesLeft(totalBytes - bytesWritten);
                })
                .asFile("/disk/location/file.zip");
```

## JSON responses
Unirest offers a lightweight JSON response type when you don't need a full Object Mapper.

```java
String result = Unirest.get("http://some.json.com")
				       .asJson()
				       .getBody()
				       .getObject()
				       .getJSONObject("car")
				       .getJSONArray("wheels")
				       .get(0)
```


## Large Responses
Some response methods (```asString```, ```asJson```) read the entire
response stream into memory. In order to read the original stream and handle large responses you
can use several functional methods like:

```java
   Map r = Unirest.get(MockServer.GET)
                .queryString("firstname", "Gary")
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

## Error Handling
the HttpResponse object has a few handler methods that can be chained to deal with success and failure:
   * ```ifSuccess(Consumer<HttpResponse<T>> response)``` will be called if the response was a 200-series response and any body processing (like ```json``` or ```Object``` was successful.
   * ```ifFailure(Consumer<HttpResponse> response``` will be called if the status was 400+ or body processing failed.
   
Putting them together might look like this:
```java
         Unirest.get("http://somewhere")
                .asJson()
                .ifSuccess(response -> someSuccessMethod(response))
                .ifFailure(response -> {
                    log.error("Oh No! Status" + response.getStatus());
                    response.getParsingError().ifPresent(e -> {
                        log.error("Parsing Exception: ", e);
                        log.error("Original body: " + e.getOriginalBody());
                    });
                });
```   
## Caching
Unirest offers a simple im memory response caching mechanism with a few options for entry expiration.
This can be either be enabled with defaults, with expiration options or consumers may supply a custom cache backed by the cache of their choice.
It is reccomended that in high load systems consumers back the cache with a dedicated cache implementation like EHCache or Guava.

#### Basic cache:
```java
     Unirest.config().cacheResponses(true);

     //These 1st response will be cached in this case:
     Unirest.get("https://somwhere").asString();
     Unirest.get("https://somwhere").asString();
```
#### Advanced Options:
You can use a builder to customize eviction rules:

```java
   Unirest.config().cacheResponses(builder()
               .depth(5) // Depth is the max number of entries cached
               .maxAge(5, TimeUnit.MINUTES)); // Max age is how long the entry will be kept.
```

#### Custom Caches
You can also supply a custom cache by implementing the Cache Interface
```java
    public static void main(String[] args){
       Unirest.config().cacheResponses(Cache.builder().backingCache(new GuavaCache()));
    }

    // Example backing cache using Guava
    public static class GuavaCache implements Cache {
            com.google.common.cache.Cache<Key, HttpResponse> regular = CacheBuilder.newBuilder().build();
            com.google.common.cache.Cache<Key, CompletableFuture> async = CacheBuilder.newBuilder().build();
            @Override
            public <T> HttpResponse get(Key key, Supplier<HttpResponse<T>> fetcher) {
                try {
                    return regular.get(key, fetcher::get);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
    
            @Override
            public <T> CompletableFuture getAsync(Key key, Supplier<CompletableFuture<HttpResponse<T>>> fetcher) {
                try {
                    return async.get(key, fetcher::get);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

```


# Configuration
Previous versions of unirest had configuration split across several different places. Sometimes it was done on ```Unirest```, sometimes it was done on ```Option```, sometimes it was somewhere else.
All configuration is now done through ```Unirest.config()```


```java
    Unirest.config()
           .connectTimeout(1000)
           .proxy(new Proxy("https://proxy"))
           .setDefaultHeader("Accept", "application/json")
           .followRedirects(false)
           .enableCookieManagement(false)
           .addInterceptor(new MyCustomInterceptor());
```

Changing Unirest's config should ideally be done once, or rarely. Once Unirest has been activated configuration options that are involved in creating the client cannot be changed without an explicit shutdown or reset.



## Config Options

| Builder Method  | Impact | Default |
| ------------- | ------------- | ------------- |
| ```connectTimeout(int)``` | Sets the connection timeout for all requests in millis  | 10000 |
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
| ```connectionTTL(long,TimeUnit)``` | Total time to live (TTL)  defines maximum life span of persistent connections regardless of their expiration setting. No persistent connection will be re-used past its TTL value.| -1  |
| ```connectionTTL(Duration)``` | Add total time to live (TTL) by [Duration](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html). Good for moderns Java APIs. | -1  |
| ```errorHandler(Consumer<HttpResponse<?>> consumer)``` | Set a global error handler that will be invoked for any status > 400 or a parsing error | | 
| ```interceptor(Interceptor value)``` | Set a global Interceptor handler that will be invoked before and after each request | | 
| ```defaultBaseUrl(String value)``` | Set a default base URL to be used for all requests that do not already contain a scheme |  | 

##  Global Interceptor
You can set a global interceptor for your configuration. This is invoked before and after each request.
This can be useful for logging or injecting common attributes.

See [Interceptor.java](https://github.com/Kong/unirest-java/blob/main/unirest/src/main/java/kong/unirest/core/Interceptor.java) for details.


## Multiple Configurations
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

## Object Mappers
Unirest offers a few different Object Mapper's based on popular JSON libraries (Jackson and GSON).
```xml
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-modules-jackson</artifactId>
</dependency>

<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-modules-gson</artifactId>
</dependency>
```

If you have some other need you can supply your own Object mapper by implementing the ```ObjectMapper``` interface. It has only a few methods

## Metrics
Unirest has hooks for collecting metrics on your runtime code. This is a simple and lightweight framework that marks two events:
   1. The moment just before the actual request is made
   1. The moment just after the actual request is made

Context information like method and request path are given to you so that you can collect based on whatever your needs are.
In its simplest form it might look like this:

```java
   Unirest.config().instrumentWith(requestSummary -> {
              long startNanos = System.nanoTime();
              return (responseSummary,exception) -> logger.info("path: {} status: {} time: {}",
                      requestSummary.getRawPath(),
                      responseSummary.getStatus(),
                      System.nanoTime() - startNanos);
   });
```

By providing more feature rich UniMetric instances you could easily calculate averages per route, uptime, or other fun facts.

# Shutting Down

Unirest starts a background event loop and your Java application won't be able to exit until you manually shutdown all the threads by invoking:

```java
Unirest.shutdown();
```

Once shutdown, using Unirest again will re-init the system


