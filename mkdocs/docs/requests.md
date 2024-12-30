# Requests
So you're probably wondering how using Unirest makes creating requests in Java easier, here is a basic POST request that will explain everything:

```java
HttpResponse<JsonNode> response = Unirest.post("http://localhost/post")
      .header("accept", "application/json")
      .queryString("apiKey", "123")
      .field("parameter", "value")
      .field("firstname", "Gary")
      .asJson();
```

Requests are made when `as[Type]()` is invoked, possible types include `Json`, `String`, `Object` `Empty` and `File`.


## Route Parameters
Sometimes you want to add dynamic parameters in the URL, you can easily do that by adding a placeholder in the URL, and then by setting the route parameters with the `routeParam` function, like:

```java
Unirest.get("http://localhost/{fruit}")
     .routeParam("fruit", "apple")
     .asString();

// Results in `http://localhost/apple`
```
The placeholder `{fruit}` will be replaced with `apple`.

The placeholder's format is as easy as wrapping in curly braces: `{custom_name}`

All param values will be URL-Encoded for you

## Default Base URLs
You  can configure a default base URL to be used for all requests that do not contain a full URL.

This configuration will result in a GET to "http://homestar.com/runner"
```java
   Unirest.config().defaultBaseUrl("http://homestar.com");
    
   Unirest.get("/runner").asString();
``` 

## Query Parameters
Query-string params can be built up one by one

```java
Unirest.get("http://localhost")
                .queryString("fruit", "apple")
                .queryString("droid", "R2D2")
                .asString();

// Results in "http://localhost?fruit=apple&droid=R2D2"
```

Again all param values will be URL-Encoded.

You can also pass in query strings as arrays and maps:
```java
Unirest.get("http://localhost")
        .queryString("fruit", Arrays.asList("apple", "orange"))
        .queryString(ImmutableMap.of("droid", "R2D2", "beatle", "Ringo"))
        .asString();

 // Results in "http://localhost?fruit=apple&fruit=orange&droid=R2D2&beatle=Ringo"
```

## Headers
Request headers can be added with the ```header``` method.
```java
Unirest.get("http://localhost")
            .header("Accept", "application/json")
            .header("x-custom-header", "hello")
            .asString();
```

### Basic Authentication
Unirest exposes a shortcut for doing basic auth when you need to. Unirest handles the Base64 encoding part.
Please make sure you are always doing this over HTTPS!

```java
Unirest.get("http://localhost")
            .basicAuth("user", "password1!")
            .asString();

// this adds the header "Authorization: Basic dXNlcjpwYXNzd29yZDEh"
```

## Body Data

### Entity Bodies
You can post entity objects as the full body easily. This is the default behavior of most REST services.

Unless you specify otherwise the default ```Content-Type``` is ```text/plain; charset=UTF-8```

```java
Unirest.post("http://localhost")
                .body("This is the entire body")
                .asEmpty();
```

You can also post as a Object that is serialized using a configured ObjectMapper. (see [Object Mappers](#object-mappers) for implementation details.
Unirest comes with a default mapper that will serialize to json using the popular Google Gson library
```java
Unirest.post("http://localhost")
            .header("Content-Type", "application/json")
            .body(new SomeUserObject("Bob"))
            .asEmpty();

// This will use Jackson to serialize the object into JSON.
```

### JSON Patch Bodies
Unirest has full native support for JSON Patch requests (RFC-6902 see <http://jsonpatch.com/>)
Per the spec, the default ```Content-Type``` for json-patch is ```application/json-patch+json```

```java
     Unirest.jsonPatch("http://localhost")
            .add("/fruits/-", "Apple")
            .remove("/bugs")
            .replace("/lastname", "Flintstone")
            .test("/firstname", "Fred")
            .move("/old/location", "/new/location")
            .copy("/original/location", "/new/location")
            .asJson();
```
will send a request with a body of
```js
  [
     {"op":"add","path":"/fruits/-","value":"Apple"},
     {"op":"remove","path":"/bugs"},
     {"op":"replace","path":"/lastname","value":"Flintstone"},
     {"op":"test","path":"/firstname","value":"Fred"},
     {"op":"move","path":"/new/location","from":"/old/location"},
     {"op":"copy","path":"/new/location","from":"/original/location"}
  ]
```

### Basic Forms
Basic http name value body params can be passed with simple field calls.
The ```Content-Type``` for this type of request is defaulted to  ```application/x-www-form-urlencoded```

```java
Unirest.post("http://localhost")
       .field("fruit", "apple")
       .field("droid", "R2D2")
       .asEmpty();

  // This will post a simple name-value pair body the same as a HTML form. This looks like
  // `fruit=apple&droid=R2D2'
```

### File Uploads
You can also post binary data in a form. Like a file.

The ```Content-Type``` for this type of request is defaulted to  ```multipart/form-data```

```java
Unirest.post("http://localhost")
       .field("upload", new File("/MyFile.zip"))
       .asEmpty();
```

For large files you may want to use a InputStream. Pass it a file name if you want one.
We are using a FileInputStream here but it can actually be any kind of InputStream.

```java
InputStream file = new FileInputStream(new File("/MyFile.zip"));

Unirest.post("http://localhost")
       .field("upload", file, "MyFile.zip")
       .asEmpty();
```

### Upload Progress Monitoring
If you are uploading large files you might want to provide some time of progress bar to a user. You can monitor this progress by providing a ProgresMonitor.

```java
          Unirest.post("http://localhost")
                .field("upload", new File("/MyFile.zip"))
                .uploadMonitor((field, fileName, bytesWritten, totalBytes) -> {
                    updateProgressBarWithBytesLeft(totalBytes - bytesWritten);
                })
                .asEmpty();
```

## Asynchronous Requests
Sometimes, well most of the time, you want your application to be asynchronous and not block, Unirest supports this in Java using anonymous callbacks, or direct method placement. All request types also support async versions.

```java
CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post("http://localhost/post")
  .header("accept", "application/json")
  .field("param1", "value1")
  .field("param2", "value2")
  .asJsonAsync(response -> {
        int code = response.getStatus();
        JsonNode body = response.getBody();
    });
```

## Paged Requests
Sometimes services offer paged requests. How this is done is not standardized but Unirest proves a mechanism to follow pages until all have been consumed. You must provide two functions for extracting the next page. The first is to get the HttpResponse in the format you want, the other is to extract the ```next``` link from the response. The result is a ```PagedList``` of ```HttpResponse<T>```. The paged list has some handy methods for dealing with the results. Here we are getting a paged list of Dogs where the ```next``` link is in the headers.

```java
PagedList<Doggos> result =  Unirest.get("https://somewhere/dogs")
                .asPaged(
                        r -> r.asObject(Doggos.class),
                        r -> r.getHeaders().getFirst("nextPage")
                );
                
```

## Client Certificates
In case you need to use a custom client certificate to call a service you can provide unirest with a custom keystore.
You may either pass a KeyStore object or a path to a valid PKCS#12 keystore file.

```java
Unirest.config()
  .clientCertificateStore("/path/mykeystore.p12", "password1!");

Unirest.get("https://some.custom.secured.place.com")
                .asString();
```

## Proxies
Sometimes you need to tunnel through a proxy. Unirest can be configured to do this. Note that authenticated proxies cannot be configured on a per-request basis unless you want to build it into the URL itself.

```java
    // Configure with authentication:
    Unirest.config().proxy("proxy.com", 7777, "username", "password1!");

    // or without
    Unirest.config().proxy("proxy.com", 7777);

    // or pass it in the request. This will override any proxy done in the config
    // currently only unauthenticated proxies work
    Unirest.get(MockServer.GET)
                    .proxy("proxy.com", 7777)
                    .asString();
```
