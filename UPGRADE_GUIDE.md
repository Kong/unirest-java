## Upgrading to Unirest 2.0 from previous versions

### Package
All main classes are now in the ```kong.unirest``` package. Classes related to the underlying Apache Http client that powers unirest are kept in ```kong.unirest.apache``` This project doesn't have many files, it really doesn't need anything more complicated than that.

### Removed Methods and Java Requirements
* Java 8: Java 8 is now required for Unirest due to extensive lambda support.
* ```.asBinary()``` and ```.getRawResponse()```  methods have been removed. These have been replaced by consumer methods which allow you to read the InputStream directly and not a copy. (see ```HttpRequest::thenConsume(Consumer<RawResponse> consumer)```
* Removal of all Apache classes in the non-config interfaces. These have ben replaced by Unirest native interfaces.
  Typically these interfaces are very similar to the older Apache classes and so updating shouldn't be a problem.



### Configuration
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



