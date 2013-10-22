# Unirest for Java [![Build Status](https://api.travis-ci.org/Mashape/unirest-java.png)](https://travis-ci.org/Mashape/unirest-java)

Unirest is a set of lightweight HTTP libraries available in multiple languages.

## Installing
Is easy as pie. Kidding. It's about as easy as doing these little steps:

Using with Maven by adding the Mashape repository:

```xml
<repository>
    <id>mashape-releases</id>
    <url>http://maven.mashape.com/releases</url>
</repository>
```

and including the library:

```xml
<dependency>
    <groupId>com.mashape.unirest</groupId>
    <artifactId>unirest-java</artifactId>
    <version>1.2.6</version>
</dependency>
```

There are dependencies for the Java library, these should be already installed, and they are as follows:

```xml
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpclient</artifactId>
  <version>4.3</version>
</dependency>
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpasyncclient</artifactId>
  <version>4.0-beta4</version>
</dependency>
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpmime</artifactId>
  <version>4.3</version>
</dependency>
<dependency>
  <groupId>org.json</groupId>
  <artifactId>json</artifactId>
  <version>20090211</version>
</dependency>
```

## Creating Request
So you're probably wondering how using Unirest makes creating requests in Java easier, here is a basic POST request that will explain everything:

```java
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("parameter", "value")
  .field("foo", "bar")
  .asJson();
```

Requests are made when `as[Type]()` is invoked, possible types include `Json`, `Binary`, `String`. If the request supports and it is of type `HttpRequestWithBody`, a body it can be passed along with `.body(String|JsonNode)`. If you already have a map of parameters or do not wish to use seperate field methods for each one there is a `.fields(Map<String, Object> fields)` method that will serialize each key - value to form parameters on your request.

`.headers(Map<String, String> headers)` is also supported in replacement of multiple header methods.

## Asynchronous Requests
Sometimes, well most of the time, you want your application to be asynchronous and not block, Unirest supports this in Java using anonymous callbacks, or direct method placement:

```java
Future<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .field("param1", "value1")
  .field("param2", "value2")
  .asJsonAsync(new Callback<JsonNode>() {
	  
	public void failed(Exception e) {
		System.out.println("The request has failed");
	}
	
	public void completed(HttpResponse<JsonNode> response) {
		 int code = response.getCode();
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
Creating `multipart` requests with Java is trivial, simply pass along a `File` Object as a field:

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

## Basic Authentication
Authenticating the request with basic authentication can be done by calling the `basicAuth(username, password)` function:
```java
HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/headers").basicAuth("username", "password").asJson();
```

# Request

The Java Unirest library follows the builder style conventions. You start building your request by creating a `HttpRequest` object using one of the following:

```java
HttpRequest request = Unirest.get(String url);
HttpRequestWithBody request = Unirest.post(String url);
HttpRequestWithBody request = Unirest.put(String url);
HttpRequestWithBody request = Unirest.patch(String url);
HttpRequest request = Unirest.delete(String url);
```

# Response

Upon recieving a response Unirest returns the result in the form of an Object, this object should always have the same keys for each language regarding to the response details.

- `.getCode()` - HTTP Response Status Code (Example 200)
- `.getHeaders()` - HTTP Response Headers
- `.getBody()` - Parsed response body where applicable, for example JSON responses are parsed to Objects / Associative Arrays.
- `.getRawBody()` - Un-parsed response body

# Advanced Configuration

You can set some advanced configuration to tune Unirest-Java:

### Custom HTTP clients

You can explicitly set your own `HttpClient` and `HttpAsyncClient` implementations by using the following methods:

```java
Unirest.setHttpClient(httpClient);
Unirest.setAsyncHttpClient(asyncHttpClient);
```
### Timeouts

You can set custom connection and socket timeout values (in milliseconds):

```java
Unirest.setTimeouts(long connectionTimeout, long socketTimeout);
```

By default the connection timeout is `10000`, and the socket timeout is `60000`.

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