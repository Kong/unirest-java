## 2.0.00
* Add a noop response type when you just don't care about the body.
```java
   HttpResponse re = Unirest.get("http://no.body.knows").asEmpty();
```
* Add the ability to follow a paged response by providing a lambda for the response type and one for getting the next page. It will stop once the next link is null or empty.
* The PagedList has handy dandy methods to pass consumers to all success and failure responses.
```java
PagedList<JsonNode> result =  Unirest.get("http://and.pagey")
                .asPaged(
                        r -> r.asJson(),
                        r -> r.getHeaders().getFirst("nextPage")
                );
```
* Add the ability to make requests to non-standard web methods for things like WebDAV, SVN, or other horrible things.
```java
   Unirest.request("CHEESE", "http://some.cheesy.thing")
          .asString();
```
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
* access headers in order
* Adds chainable ifSuccess and ifFailure consumer methods to the response. Now you can handle the response like
```java
 Unirest.get("https://localhost/somewhere")
                .asString()
                .ifSuccess(r -> log.info("Yippy!"))
                .ifFailure(r -> log.error("Booo"));
```
* Allow the configuration of header suppliers.
```java
   Unirest.config().setDefaultHeader("trace", () -> value);
```
* add method to replace a header rather than append to it.
* Now you can stream the results into a file!
   * It doesn't need to be a file either. It could be any result. Unirest will shove it in a file.
```java
File file = Unirest.get("https://someplace/file.tar.gz")
                   .asFile("/local/storage/file.tar.gz")
                   .getBody();
```
* When encountering a parsing error with asObject or asJson capture the body in a UnirestParsingException
* Detect if the async client has stopped for some reason and construct a new one. This one may be different from the one that was originally configured so we need to add a way to configure a supplier for clients rather than a direct client.
* Add several functional methods for dealing with the raw response before the connection is closed. This is nice for large responses.
* Support Java system properties for proxies via ```Unirest.config().useSystemProperties(true);```
    * https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html
* Support for authenticated proxies with ```Unirest.config().proxy("proxy.server.host", 80, "username","password")```
* The namespace has been shortened to just **kong.unirest** (inspired by Java Spark)
* The configuration system has been completely redone. Previously it was scattered across several classes and was entirely static. The new system has instances and supports multiple configurations. See the UPGRADE_GUIDE.md for details.
    * Start accessing the config with ```Unirest.config()```
* Almost everything is now fronted by interfaces, this makes testing easier.
* Unirest itself can now be accessed as a interface with UnirestInstance
* ObjectMappers can be passed in as part of the builder.
* option no longer supports body (per http spec)
* Accept and ContentType are now 1st class headers in the builder (```Unirest.get(path).accept("application/json")```)
* Major internal fixes so that which builder operations were available when is consistent.
* Lazy init the HttpClients so they don't get in the way of setting custom clients.
* Updated org.json dependency
* Add async versions of generic type methods
* Add support for generic types with object mappers.
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
* Add optional flag overload to ```Unirest.shutDown(false)```. The flag indicates if the various Options should be cleared. This only applies to options that could survive a shutdown. The HttpClients and thread monitors will still be discarded.
* Change default MultiPart mode to BROWSER_COMPATIBLE in order to support unicode filenames. Clients wishing to use the legacy mode can set it to STRICT. This should not be a problem for most users as few servers today lack support for unicode file names. Issue #35
* Update Apache dependencies https://archive.apache.org/dist/httpcomponents/httpclient/RELEASE_NOTES-4.5.x.txt
* add an option to disable cookie management with ```Options.enableCookieManagement(false)```.
* In the future ignoring cookies will be the default.
* added ability to turn off redirect following with ```Options.followRedirects(boolean enable)``` (default is true)
* Add ```.charset(Charset charset)``` to POSTS (both form and body)
* Clean up some ambiguous methods in form posting. This may be a breaking change for a very small number of users. There are better methods for handling these cases.
* Added the ability to add HttpRequestInterceptors to the client. Though ```  Options.addInterceptor(new TestInterceptor()); ```
* ```Unirest.shutdown()``` no longer throws a checked exception
* Fix NPE with null bodies from HEAD requests.
* Quitly consume and close InputStreams that may not be complete.
* Major refactoring of how response objects are built. This internalizes HttpClientHelper which was previously public but not needed if you were using the library in in the expected way. This refactoring sets up the library for future work to extend the number of supported formats and for greater expression in the methods for those formats.
* Additional of functional map methods to HttpResponse so you don't have to stop the flow for further transformations.
* Fixed issue with achor hashes in URLs
* Fixed NPE in passing args as maps
* JSON parsing errors no longer just throw out of ```asJson``` but are captured and can be inspected via ```HttpResponse::getParsingError```. This resolves the fact that most API's do not return valid JSON for non-200 status codes.
* Return CompletableFuture rather that boring old Futures for all async methods.
* UnirestException is no longer checked
* Thread leak in Options fixed
