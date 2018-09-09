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
