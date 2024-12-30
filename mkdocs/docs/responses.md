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
