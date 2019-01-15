## 3.3.02
* Add the ability to make requests to non-standard web methods for things like WebDAV, SVN, or other horrible things.
```java
   Unirest.request("CHEESE", "http://some.cheesy.thing")
          .asString();
```

## 3.3.00
* Slight breaking change with regard to accessing Apache specific classes via the config
   * getClient and getAsyncClient return the Unirest containers for clients. You can still get to the Apache client through that for now but this is also deprecated. Eventually Unirest will make Apache just one of several modules.
   * Apache client wrappers are now package local.
* Add support for overriding the default expected encoding both on a per-request basis and as a default int he config.
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

## 3.2.06
* access headers in order

## 3.2.05
* UnirestInstance is autoclosable

## 3.2.04
* Adds chainable ifSuccess and ifFailure consumer methods to the response. Now you can handle the response like
```java
 Unirest.get("https://localhost/somewhere")
                .asString()
                .ifSuccess(r -> log.info("Yippy!"))
                .ifFailure(r -> log.error("Booo"));
```

## 3.2.03
* Allow the configuration of header suppliers.
```java
   Unirest.config().setDefaultHeader("trace", () -> value);
```

## 3.2.02
* distinguish between set and add for default headers.

## 3.2.01
* add method to replace a header rather than append to it.

## 3.2.00
* Now you can stream the results into a file!
   * It doesn't need to be a file either. It could be any result. Unirest will shove it in a file.
```java
File file = Unirest.get("https://someplace/file.tar.gz")
                   .asFile("/local/storage/file.tar.gz")
                   .getBody();
```

## 3.1.02
* When encountering a parsing error with asObject or asJson capture the body in a UnirestParsingException
* New BETA feature asFile method to stream the response into a file.

## 3.1.01
* Detect if the async client has stopped for some reason and construct a new one. This one may be different from the one that was originally configured so we need to add a way to configure a supplier for clients rather than a direct client.

## 3.1.00
* Deprecate methods that expose Apache. In the 4 line we will start supporting other clients. Primarily the java one supplied in Java9 (apache will still exist for 8-)
* Add several functional methods for dealing with the raw response before the connection is closed. This is nice for large responses.

## 3.0.04
* Parsing handler should capture unirest exceptions just like other exceptions.

## 3.0.03
* Support Java system properties for proxies via ```Unirest.config().useSystemProperties(true);```
    * https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html

## 3.0.02
* Add support for the authenticated proxies to the async client

## 3.0.01
* Support for authenticated proxies with ```Unirest.config().proxy("proxy.server.host", 80, "username","password")```

## 3.0.00
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

## 2.5.03
* Lazy init the HttpClients so they don't get in the way of setting custom clients.

## 2.5.02
* More safety for issue #41

## 2.5.01
* Fix Issue #41: possible init error in HttpClient under heavy load

## 2.5.00
* Extracted Interface for HttpResponse to make testing easier

## 2.4.02
* Updated org.json dependency

## 2.4.01
* Add async versions of generic type methods

## 2.4.00
* Issue #19 Add support for generic types with object mappers.

## 2.3.00
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

## 2.2.12
* Add optional flag overload to ```Unirest.shutDown(false)```. The flag indicates if the various Options should be cleared. This only applies to options that could survive a shutdown. The HttpClients and thread monitors will still be discarded.

## 2.2.11
* Change default MultiPart mode to BROWSER_COMPATIBLE in order to support unicode filenames. Clients wishing to use the legacy mode can set it to STRICT. This should not be a problem for most users as few servers today lack support for unicode file names. Issue #35

## 2.2.10
* Update Apache dependencies https://archive.apache.org/dist/httpcomponents/httpclient/RELEASE_NOTES-4.5.x.txt

## 2.2.08
   * add an option to disable cookie management with ```Options.enableCookieManagement(false)```.
   * In the future ignoring cookies will be the default.

## 2.2.07
* Fix init error

## 2.2.06
* added ability to turn off redirect following with ```Options.followRedirects(boolean enable)``` (default is true)

## 2.2.04
* Add ```.charset(Charset charset)``` to POSTS (both form and body)
* Clean up some ambiguous methods in form posting. This may be a breaking change for a very small number of users. There are better methods for handling these cases.
* Added the ability to add HttpRequestInterceptors to the client. Though ```  Options.addInterceptor(new TestInterceptor()); ```

## 2.2.03
* Expose the ```Options.init()``` method to restore the system to a fresh start after a shutdown.
* ```Unirest.shutdown()``` no longer throws a checked exception

## 2.2.02
* Fix NPE with null bodies from HEAD requests.

## 2.2.01
* Quitly consume and close InputStreams that may not be complete.

## 2.2.00
* Major refactoring of how response objects are built. This internalizes HttpClientHelper which was previously public but not needed if you were using the library in in the expected way. This refactoring sets up the library for future work to extend the number of supported formats and for greater expression in the methods for those formats.
* Additional of functional map methods to HttpResponse so you don't have to stop the flow for further transformations.
* Update the org.json dependency
* Fixed issue with achor hashes in URLs (#17)
* Fixed NPE in passing args as maps (#20)

## 2.1.01
* JSON parsing errors no longer just throw out of ```asJson``` but are captured and can be inspected via ```HttpResponse::getParsingError```. This resolves the fact that most API's do not return valid JSON for non-200 status codes.
* Return CompletableFuture rather that boring old Futures for all async methods.


## 2.0.02
* UnirestException is no longer checked
* Namespaces have been migrated to new **io.gitgub.openunirest** namespace
* Thread leak in Options fixed
