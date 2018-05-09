# Unirest for Java [![Build Status](https://travis-ci.org/OpenUnirest/unirest-java.svg?branch=master)](https://travis-ci.org/OpenUnirest/unirest-java)

Do yourself a favor, and start making HTTP requests like this:

```java
Unirest.post("http://httpbin.org/post")
  .queryString("name", "Mark")
  .field("last", "Polo")
  .asJson()
```

## Install With [Maven](https://mvnrepository.com/artifact/io.github.openunirest/unirest-java)
```
<dependency>
    <groupId>io.github.openunirest</groupId>
    <artifactId>unirest-java</artifactId>
    <version>2.2.03</version>
</dependency>
```

## Features

* Make `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS` requests
* Both synchronous and asynchronous (non-blocking) requests
* It supports form parameters, file uploads and custom body entities
* Easily add route parameters without ugly string concatenations
* Supports gzip
* Supports Basic Authentication natively
* Customizable timeout, concurrency levels and proxy settings
* Customizable default headers for every request (DRY)
* Customizable `HttpClient` and `HttpAsyncClient` implementation
* Automatic JSON parsing into a native object for JSON responses
* Customizable binding, with mapping from response body to java Object 


## Creating Request
So you're probably wondering how using Unirest makes creating requests in Java easier, here is a basic POST request that will explain everything:

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .queryString("apiKey", "123")
  .field("parameter", "value")
  .field("foo", "bar")
  .asJson();
```

Requests are made when `as[Type]()` is invoked, possible types include `Json`, `Binary`, `String`, `Object`.

If the request supports and it is of type `HttpRequestWithBody`, a body it can be passed along with `.body(String|JsonNode|Object)`. For using `.body(Object)` some pre-configuration is needed (see below).

If you already have a map of parameters or do not wish to use seperate field methods for each one there is a `.fields(Map<String, Object> fields)` method that will serialize each key - value to form parameters on your request.

`.headers(Map<String, String> headers)` is also supported in replacement of multiple header methods.

## Serialization
Before an `asObject(Class)` or a `.body(Object)` invokation, is necessary to provide a custom implementation of the `ObjectMapper` interface.
This should be done only the first time, as the instance of the ObjectMapper will be shared globally.

For example, serializing Json from\to Object using the popular Jackson ObjectMapper takes only few lines of code.

```java
// Only one time
Unirest.setObjectMapper(new ObjectMapper() {
    private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                = new com.fasterxml.jackson.databind.ObjectMapper();
    
    public <T> T readValue(String value, Class<T> valueType) {
        try {
            return jacksonObjectMapper.readValue(value, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeValue(Object value) {
        try {
            return jacksonObjectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
});

// Response to Object
HttpResponse<Book> bookResponse = Unirest.get("http://httpbin.org/books/1").asObject(Book.class);
Book bookObject = bookResponse.getBody();

HttpResponse<Author> authorResponse = Unirest.get("http://httpbin.org/books/{id}/author")
    .routeParam("id", bookObject.getId())
    .asObject(Author.class);
    
Author authorObject = authorResponse.getBody();

// Object to Json
HttpResponse<JsonNode> postResponse = Unirest.post("http://httpbin.org/authors/post")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(authorObject)
        .asJson();
```

### Route Parameters

Sometimes you want to add dynamic parameters in the URL, you can easily do that by adding a placeholder in the URL, and then by setting the route parameters with the `routeParam` function, like:

```java
Unirest.get("http://httpbin.org/{method}")
  .routeParam("method", "get")
  .queryString("name", "Mark")
  .asJson();
```
In the example above the final URL will be `http://httpbin.org/get` - Basically the placeholder `{method}` will be replaced with `get`.

The placeholder's format is as easy as: `{custom_name}`

## Asynchronous Requests
Sometimes, well most of the time, you want your application to be asynchronous and not block, Unirest supports this in Java using anonymous callbacks, or direct method placement:

```java
CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("param1", "value1")
  .field("param2", "value2")
  .asJsonAsync(new Callback<JsonNode>() {

	public void failed(UnirestException e) {
		System.out.println("The request has failed");
	}

	public void completed(HttpResponse<JsonNode> response) {
		 int code = response.getStatus();
	     Map<String, String> headers = response.getHeaders();
	     JsonNode body = response.getBody();
	     InputStream rawBody = response.getRawBody();
	}

	public void cancelled() {
		System.out.println("The request has been cancelled");
	}

});
```

## File Uploads
Creating `multipart` requests with Java is trivial, simply pass along a `File` or an InputStream Object as a field:

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("parameter", "value")
  .field("file", new File("/tmp/file"))
  .asJson();
```

## Custom Entity Body

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .body("{\"parameter\":\"value\", \"foo\":\"bar\"}")
  .asJson();
```

## Byte Stream as Entity Body

```java
final InputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));
final byte[] bytes = new byte[stream.available()];
stream.read(bytes);
stream.close();
final HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .field("name", "Mark")
  .field("file", bytes, "image.jpg")
  .asJson();
```

## InputStream as Entity Body

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .field("name", "Mark")
  .field("file", new FileInputStream(new File(getClass().getResource("/image.jpg").toURI())), ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
  .asJson();
```

## Basic Authentication
Authenticating the request with basic authentication can be done by calling the `basicAuth(username, password)` function:
```java
HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/headers").basicAuth("username", "password").asJson();
```

# Request

The Java Unirest library follows the builder style conventions. You start building your request by creating a `HttpRequest` object using one of the following:

```java
GetRequest request = Unirest.get(String url);
GetRequest request = Unirest.head(String url);
HttpRequestWithBody request = Unirest.post(String url);
HttpRequestWithBody request = Unirest.put(String url);
HttpRequestWithBody request = Unirest.patch(String url);
HttpRequestWithBody request = Unirest.options(String url);
HttpRequestWithBody request = Unirest.delete(String url);
```

# Response

Upon recieving a response Unirest returns the result in the form of an Object, this object should always have the same keys for each language regarding to the response details.

- `.getStatus()` - HTTP Response Status Code (Example: 200)
- `.getStatusText()` - HTTP Response Status Text (Example: "OK")
- `.getHeaders()` - HTTP Response Headers
- `.getBody()` - Parsed response body where applicable, for example JSON responses are parsed to Objects / Associative Arrays.
- `.getRawBody()` - Un-parsed response body
- `.getParsingError()` - Optional RuntimeException containing the error that was thrown when parsing the response body

# Advanced Configuration

You can set some advanced configuration to tune Unirest-Java:

### Custom HTTP clients

You can explicitly set your own `HttpClient` and `HttpAsyncClient` implementations by using the following methods:

```java
Unirest.setHttpClient(httpClient);
Unirest.setAsyncHttpClient(asyncHttpClient);
```
### Timeouts

You can set custom connection and socket timeout values (in **milliseconds**):

```java
Unirest.setTimeouts(long connectionTimeout, long socketTimeout);
```

By default the connection timeout (the time it takes to connect to a server) is `10000`, and the socket timeout (the time it takes to receive data) is `60000`. You can set any of these timeouts to zero to disable the timeout.

### Default Request Headers

You can set default headers that will be sent on every request:

```java
Unirest.setDefaultHeader("Header1", "Value1");
Unirest.setDefaultHeader("Header2", "Value2");
```

You can clear the default headers anytime with:

```java
Unirest.clearDefaultHeaders();
```

### Concurrency

You can set custom concurrency levels if you need to tune the performance of the syncronous or asyncronous client:

```java
Unirest.setConcurrency(int maxTotal, int maxPerRoute);
```

By default the maxTotal (overall connection limit in the pool) is `200`, and the maxPerRoute (connection limit per target host) is `20`.

### Proxy

You can set a proxy by invoking:

```java
Unirest.setProxy(new HttpHost("127.0.0.1", 8000));
```

# Exiting an application

Unirest starts a background event loop and your Java application won't be able to exit until you manually shutdown all the threads by invoking:

```java
Unirest.shutdown();
```

Once shutdown, you can call `Options.init()` to re-initialize Unirest.


