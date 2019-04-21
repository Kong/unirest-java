## 2.2.02 (pending)
* Add a mapper function to transform a HttpResponse<V> to a HttpResponse<T>
```java
HttpResponse<Integer> response = Unirest.get("http://someplace/number")
                .asString()
                .map(Integer::valueOf);
```
* Add a new response builder for getting an object OR an alternative error object. This is nice for systems that return errors in the same format as the primary objects (json, xml, etc).
```java
HttpEither<Thing, ErrorThing> response = Unirest.get("http://someplace")
                .asObject(Thing.class, ErrorThing.class);

response.getBody(); // Has the thing
response.getError(); // has an error object (as defined by the server)

```

## 2.2.01
* add cookiespec policies to config. These are the same as Apache Http Clients cookiespec. See org.apache.http.client.config.CookieSpec

## 2.2.00
* Introduce default implementations of popular object mappers
    * Jackson
    * GSON

## 2.1.03
* Finally address #26 You can add a file upload progress monitor. This monitor will get called for each file in a multipart file upload by name.
``` java
    Unirest.post("http://someplace")
           .field("myFile", new File("/file/somthing.tar")
           .uploadMonitor((fieldName, fileName, bytesWritten, totalBytes) -> {
                // draw a upload progress bar or something
           })
           .asEmpty()
```
* Fix an issue where when using generic methods that take object, passing in complex objects like InputStreams would not get send as the correct type.

## 2.1.02
* #120 support client certificates. You can pass in an entire keystore or just the path to it in the config.
* part of #260: only support a single basic auth header.

## 2.1.01
* #260 add option to configure a default basic auth header

## 2.1.00
* #259 optionally flag the config to auto register the clients with shutdownhooks.
* #165 allow forcing a simple url-encoded param POST/PUT to be multipart. This adds a new method to the body interface so bumping the minor.

## 2.0.05
* #118 enable overriding socket and connection timeouts per request
* #72 enable passing a proxy per request. Only works with simple proxies for now.

## 2.0.04
* Disable SSL validation with ```Unirest.config().verifySsl(false)```. PLEASE DO NOT DO THIS IN PRODUCTION
* Disable Automatic retries with ```Unirest.config().automaticRetries(false)```

## 2.0.03
* Make sure the GzipInputStream gets closed.
* Support disabling compressed results which is asked for by default
```java
Unirest.config().requestCompression(false);
```
## 2.0.02
* Pass route params as a map
```java
Unirest.get("http://example")
       .routeParam(ImmutableMap.of("cheese", "cheddar", "age", 42))
```

# The following were releases from the merged OpenUnirest project

### OpenUnirest:3.3.05
* Add a noop response type when you just don't care about the body.
```java
   HttpResponse re = Unirest.get("http://no.body.knows").asEmpty();
```

### OpenUnirest:3.3.04
* Add the ability to follow a paged response by providing a lambda for the response type and one for getting the next page. It will stop once the next link is null or empty.
* The PagedList has handy dandy methods to pass consumers to all success and failure responses.
```java
PagedList<JsonNode> result =  Unirest.get("http://and.pagey")
                .asPaged(
                        r -> r.asJson(),
                        r -> r.getHeaders().getFirst("nextPage")
                );
```


### OpenUnirest:3.3.03
* Pulled isSuccess() up to the response interface so it can be used and enjoyed by everyone.

### OpenUnirest:3.3.02
* Add the ability to make requests to non-standard web methods for things like WebDAV, SVN, or other horrible things.
```java
   Unirest.request("CHEESE", "http://some.cheesy.thing")
          .asString();
```

### OpenUnirest:3.3.00
* Slight breaking change with regard to accessing Apache specific classes via the config
   * getClient and getAsyncClient return the Unirest containers for clients. You can still get to the Apache client through that for now but this is also deprecated. Eventually Unirest will make Apache just one of several modules.
   * Apache client wrappers are now package local.
* Add support for overriding the default expected encoding both on a per-request basis and as a default in the config.
```java
// Changing the default from UTF-8 for all requests
// Unirest will still honor content-encoding if defined in the response headers
Unirest.config().setDefaultResponseEncoding("windows-1250");

// Changing the default on a request. 
// This will explicitly be used EVEN IF the headers define something else.
Unirest.get("http://some.file.with.windows.encoding/file.txt")
                .responseEncoding("windows-1250")
                .asString();
```

### OpenUnirest:3.2.06
* access headers in order

### OpenUnirest:3.2.05
* UnirestInstance is autoclosable

### OpenUnirest:3.2.04
* Adds chainable ifSuccess and ifFailure consumer methods to the response. Now you can handle the response like
```java
 Unirest.get("https://localhost/somewhere")
                .asString()
                .ifSuccess(r -> log.info("Yippy!"))
                .ifFailure(r -> log.error("Booo"));
```

### OpenUnirest:3.2.03
* Allow the configuration of header suppliers.
```java
   Unirest.config().setDefaultHeader("trace", () -> value);
```

### OpenUnirest:3.2.02
* distinguish between set and add for default headers.

### OpenUnirest:3.2.01
* add method to replace a header rather than append to it.

### OpenUnirest:3.2.00
* Now you can stream the results into a file!
   * It doesn't need to be a file either. It could be any result. Unirest will shove it in a file.
```java
File file = Unirest.get("https://someplace/file.tar.gz")
                   .asFile("/local/storage/file.tar.gz")
                   .getBody();
```

### OpenUnirest:3.1.02
* When encountering a parsing error with asObject or asJson capture the body in a UnirestParsingException
* New BETA feature asFile method to stream the response into a file.

### OpenUnirest:3.1.01
* Detect if the async client has stopped for some reason and construct a new one. This one may be different from the one that was originally configured so we need to add a way to configure a supplier for clients rather than a direct client.

### OpenUnirest:3.1.00
* Deprecate methods that expose Apache. In the 4 line we will start supporting other clients. Primarily the java one supplied in Java9 (apache will still exist for 8-)
* Add several functional methods for dealing with the raw response before the connection is closed. This is nice for large responses.

### OpenUnirest:3.0.04
* Parsing handler should capture unirest exceptions just like other exceptions.

### OpenUnirest:3.0.03
* Support Java system properties for proxies via ```Unirest.config().useSystemProperties(true);```
    * https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html

### OpenUnirest:3.0.02
* Add support for the authenticated proxies to the async client

### OpenUnirest:3.0.01
* Support for authenticated proxies with ```Unirest.config().proxy("proxy.server.host", 80, "username","password")```

### OpenUnirest:3.0.00
* This is a **major** release with several **breaking changes** which (other than the namespace change) should ONLY impact you if you are using some of Unirests more advanced features or custom configurations.
* The maven artifact has changed to ```open-unirest-java```
* The namespace has been shortened to just **unirest** (inspired by Java Spark)
* The configuration system has been completely redone. Previously it was scattered across several classes and was entirely static. The new system has instances and supports multiple configurations. See the UPGRADE_GUIDE.md for details.
    * Start accessing the config with ```Unirest.config()```
* Almost everything is now fronted by interfaces, this makes testing easier.
* Unirest itself can now be accessed as a interface with UnirestInstance
* ObjectMappers can be passed in as part of the builder.
* option no longer supports body (per http spec)
* Accept and ContentType are now 1st class headers in the builder (```Unirest.get(path).accept("application/json")```)
* Major internal fixes so that which builder operations were available when is consistent.

### OpenUnirest:2.5.03
* Lazy init the HttpClients so they don't get in the way of setting custom clients.

### OpenUnirest:2.5.02
* More safety for issue #41

### OpenUnirest:2.5.01
* Fix Issue #41: possible init error in HttpClient under heavy load

### OpenUnirest:2.5.00
* Extracted Interface for HttpResponse to make testing easier

### OpenUnirest:2.4.02
* Updated org.json dependency

### OpenUnirest:2.4.01
* Add async versions of generic type methods

### OpenUnirest:2.4.00
* Issue #19 Add support for generic types with object mappers.

### OpenUnirest:2.3.00
* Add support for the JSON Patch standard (RFC6902) https://tools.ietf.org/html/rfc6902
```java
     Unirest.jsonPatch(MockServer.PATCH)
            .add("/fruits/-", "Apple")
            .remove("/bugs")
            .replace("/lastname", "Flintsone")
            .test("/firstname", "Fred")
            .move("/old/location", "/new/location")
            .copy("/original/location", "/new/location")
            .asJson();
```

### OpenUnirest:2.2.12
* Add optional flag overload to ```Unirest.shutDown(false)```. The flag indicates if the various Options should be cleared. This only applies to options that could survive a shutdown. The HttpClients and thread monitors will still be discarded.

### OpenUnirest:2.2.11
* Change default MultiPart mode to BROWSER_COMPATIBLE in order to support unicode filenames. Clients wishing to use the legacy mode can set it to STRICT. This should not be a problem for most users as few servers today lack support for unicode file names. Issue #35

### OpenUnirest:2.2.10
* Update Apache dependencies https://archive.apache.org/dist/httpcomponents/httpclient/RELEASE_NOTES-4.5.x.txt

### OpenUnirest:2.2.08
   * add an option to disable cookie management with ```Options.enableCookieManagement(false)```.
   * In the future ignoring cookies will be the default.

### OpenUnirest:2.2.07
* Fix init error

### OpenUnirest:2.2.06
* added ability to turn off redirect following with ```Options.followRedirects(boolean enable)``` (default is true)

### OpenUnirest:2.2.04
* Add ```.charset(Charset charset)``` to POSTS (both form and body)
* Clean up some ambiguous methods in form posting. This may be a breaking change for a very small number of users. There are better methods for handling these cases.
* Added the ability to add HttpRequestInterceptors to the client. Though ```  Options.addInterceptor(new TestInterceptor()); ```

### OpenUnirest:2.2.03
* Expose the ```Options.init()``` method to restore the system to a fresh start after a shutdown.
* ```Unirest.shutdown()``` no longer throws a checked exception

### OpenUnirest:2.2.02
* Fix NPE with null bodies from HEAD requests.

### OpenUnirest:2.2.01
* Quitly consume and close InputStreams that may not be complete.

### OpenUnirest:2.2.00
* Major refactoring of how response objects are built. This internalizes HttpClientHelper which was previously public but not needed if you were using the library in in the expected way. This refactoring sets up the library for future work to extend the number of supported formats and for greater expression in the methods for those formats.
* Additional of functional map methods to HttpResponse so you don't have to stop the flow for further transformations.
* Update the org.json dependency
* Fixed issue with achor hashes in URLs (#17)
* Fixed NPE in passing args as maps (#20)

### OpenUnirest:2.1.01
* JSON parsing errors no longer just throw out of ```asJson``` but are captured and can be inspected via ```HttpResponse::getParsingError```. This resolves the fact that most API's do not return valid JSON for non-200 status codes.
* Return CompletableFuture rather that boring old Futures for all async methods.


### OpenUnirest:2.0.02
* UnirestException is no longer checked
* Namespaces have been migrated to new **io.gitgub.openunirest** namespace
* Thread leak in Options fixed
