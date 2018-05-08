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
