# Upgrade Guide

## Upgrading to Unirest 3.0
The primary difference in Unirest 3 is that the org.json dependency has been replaced by a clean-room implementation of org.json's interface using Google Gson as the engine. 

### What? Why?
This was done due to conflicts with the org.json license which requires that "The Software shall be used for Good, not Evil.". While many people would rightly view this as silly and unenforceable by law, many organizations such as Eclipse, Debian, and Apache will not allow using it.

### Why not switch to the google implementation of org.json?
Several reasons:
* It has not been maintained in several years and no longer matches the org.json signatures.
* It causes classpath conflicts which many projects forbid.
* We would like Unirest to be able to expand beyond org.json and offer more advanced native features like object mapping.

### Why Gson and not Jackson?
* Gson is closest in spirit and method signature to org.json and was deemed quicker to adopt.
* It's small, mature and a single dependency. 
* It would conflict less in other projects than Jackson would which is both more popular and far more complex.

### How was this done?
Implementation was done without looking at the internals of the org.json classes. This was accomplished by  writing extensive unit tests in order to document behavior and method signatures and then simply changing the test to use this projects own classes as well as Google Gson.

### Differences between org.json and kong.unirest.json
* The namespace is now ```kong.unirest.json```
* For the most part kong.unirest.json honors all public interfaces and behavior of ```JSONArray```, ```JSONObject```, and ```JSONPointer```. 
* The utility classes in org.json have NOT been implemented as they are not required for Unirest's use case. So things like XML-to-JSON, and CSV-to-JSON have not been implemented.
* Custom indenting with ```.toString(int spaces)``` does not honor the indent factor and always uses 2 spaces. Waiting on https://github.com/google/gson/pull/1280 for a fix.
* There are some slight differences in the details of some error messages.


## Upgrading to Unirest 2.0 from previous versions

### Package
All main classes are now in the ```kong.unirest``` package. Classes related to the underlying Apache Http client that powers unirest are kept in ```kong.unirest.apache``` This project doesn't have many files, it really doesn't need anything more complicated than that.

### Removed Methods and Java Requirements
* Java 8: Java 8 is now required for Unirest due to extensive lambda support.
* ```.asBinary()``` and ```.getRawResponse()```  methods have been removed. These have been replaced by consumer methods which allow you to read the InputStream directly and not a copy. (see ```HttpRequest::thenConsume(Consumer<RawResponse> consumer)```
* Removal of all Apache classes in the non-config interfaces. These have ben replaced by Unirest native interfaces.
  Typically these interfaces are very similar to the older Apache classes and so updating shouldn't be a problem.



### Configuration
Previous versions of unirest had configuration split across several different places. 

```java
   // Sometimes it was on Unirest
    Unirest.setTimeouts(5000, 10000);
    Unirest.setConcurrency(25, 10);

   //Sometimes it was on Options
   Options.setOption(HTTPCLIENT, client);
```

Often you could do it in both places with different impacts on the lifecycle of the client. All configuration has now been centralized in ```Unirest.config()```

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



