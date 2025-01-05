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

| Builder Method                                         | Impact                                                                                                                                                                                         | Default         |
|--------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|
| ```connectTimeout(int)```                              | Sets the connection timeout for all requests in millis                                                                                                                                         | 10000           |
| ```requestTimeout(int)```                              | Sets the request timeout for all requests in millis                                                                                                                                            | none (infinite) |
| ```proxy(proxy)```                                     | Sets a proxy object for negotiating proxy servers. Can include auth credentials                                                                                                                |                 |
| ```setDefaultHeader(String, String)```                 | Sets  a default header. Will overwrite if it exists                                                                                                                                            |                 |
| ```setDefaultHeader(String, Supplier<String>)```       | Sets a default header by supplier. Good for setting trace tokens for microservice architectures. Will overwrite if it exists                                                                   |                 |
| ```addDefaultHeader(String, String)```                 | Adds a default header. Multiple for the same name can exist                                                                                                                                    |                 |
| ```addDefaultHeader(String, Supplier<String>)```       | Add a default header by supplier. Good for setting trace tokens for microservice architectures.                                                                                                |                 |
| ```setDefaultBasicAuth(String, String)```              | Add a default Basic Auth Header                                                                                                                                                                |                 |
| ```followRedirects(boolean)```                         | toggle following redirects                                                                                                                                                                     | true            |
| ```enableCookieManagement(boolean)```                  | toggle accepting and storing cookies                                                                                                                                                           | true            |
| ```cookieSpec(String)```                               | set a cookie policy. Acceptable values: 'default' (same as Netscape), 'netscape', 'ignoreCookies', 'standard' (RFC 6265 interoprability profile) , 'standard-strict' (RFC 6265 strict profile) | default         |
| ```automaticRetries(boolean)```                        | toggle disabling automatic retries (up to 4 times) for socket timeouts                                                                                                                         | true            |
| ```verifySsl(boolean)```                               | toggle enforcing SSL                                                                                                                                                                           | true            |
| ```clientCertificateStore(String,String)```            | Add a PKCS12 KeyStore by path for doing client certificates                                                                                                                                    |                 |
| ```clientCertificateStore(KeyStore,String)```          | Add a PKCS12 KeyStore for doing client certificates                                                                                                                                            |                 |
| ```connectionTTL(long,TimeUnit)```                     | Total time to live (TTL)  defines maximum life span of persistent connections regardless of their expiration setting. No persistent connection will be re-used past its TTL value.             | -1              |
| ```connectionTTL(Duration)```                          | Add total time to live (TTL) by [Duration](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html). Good for moderns Java APIs.                                                     | -1              |
| ```errorHandler(Consumer<HttpResponse<?>> consumer)``` | Set a global error handler that will be invoked for any status > 400 or a parsing error                                                                                                        |                 | 
| ```interceptor(Interceptor value)```                   | Set a global Interceptor handler that will be invoked before and after each request                                                                                                            |                 | 
| ```defaultBaseUrl(String value)```                     | Set a default base URL to be used for all requests that do not already contain a scheme                                                                                                        |                 | 

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
