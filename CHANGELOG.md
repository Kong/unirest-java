#2.2.08
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
