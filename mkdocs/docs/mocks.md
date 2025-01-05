# Unirest Mocks

## About
A series of mocks for use with Unirest for unit testing. Mocked clients will not make any real web requests. This allows you to test the input into unirest and to mock responses from expected requests.

```mermaid
    flowchart TD
        A[Your Code] --> B(Unirest Pubilc Interface)
        B --> C{Core Implimentation}
        C -->|When Mocked| D[Mocked Client]
        C -->|Normal Runtime| F[Java HttpClient]
```

## Expecting Requests
You can either mock the default static implementation or a per instance implementation. In both cases you need to register the mock with Unirest.

### Static Mocking
```java
class MyTest {
    @Test
    void mockStatic(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                        .thenReturn("You can do anything!");
        
        assertEquals(
            "You can do anything!", 
            Unirest.get("http://zombo.com").asString().getBody()
        );
    }
}
```

### Instant Mocking
```java
    @Test
    void mockInstant(){
        UnirestInstance unirest = Unirest.spawnInstance();
        MockClient mock = MockClient.register(unirest);
        
        mock.expect(HttpMethod.GET, "http://zombo.com")
                        .thenReturn("You can do anything!");
        
        assertEquals(
            "You can do anything!", 
            unirest.get("http://zombo.com").asString().getBody()
        );
    }
```

### Multiple Expects
HTTP requests can have many parts, some of which are automatic or at least uninteresting from the standpoint of testing. This means that setting up an exact expectation to match the request exactly can be tedious. 

You can register as many expects as you like. Which one is used for any particular invocation of Unirest depends on a points system. Each expectation is evaluated and given points for each positive part while any negative part immediately discards the expect. The expectation that has the most points "wins".

In this example, we have three expectations, one doesn't match at all. and two others match but one does more than the other so the most specific match is used.

```java
    @Test
    void multipleExpects(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "https://somewhere.bad")
                .thenReturn("I'm Bad");

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .header("foo", "bar")
                .thenReturn("You can do anything with headers!");

        assertEquals(
                "You can do anything with headers!",
                Unirest.get("http://zombo.com")
                        .header("foo", "bar")
                        .asString().getBody()
        );

        assertEquals(
                "You can do anything!",
                Unirest.get("http://zombo.com")
                        .asString().getBody()
        );
    }
```

### Verifying Expects
Sometimes we only want to know that the needful was done. In this case we can validate our mock. The simplest way is to call verifyAll which will validate that all expects were called at least once.

```java
    @Test
    void verifyAll(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .thenReturn().withStatus(200);

        Unirest.post("http://zombo.com").asString().getBody();

        mock.verifyAll();
    }

```

If you want to get more specific we can keep around our expectations and validate them explicitly. We can also inject a number of times we want to validate (including zero)
```java
    @Test
    void verifyMultiple(){
        MockClient mock = MockClient.register();
    
        var zombo =    mock.expect(HttpMethod.POST, "http://zombo.com").thenReturn();
        var homestar = mock.expect(HttpMethod.DELETE, "http://homestarrunner.com").thenReturn();
    
        Unirest.post("http://zombo.com").asString().getBody();
    
        zombo.verify();
        homestar.verify(Times.never());
    }
```

### Expected Body Matching
You can match specific body content with some limitations. Complex bodies must implement ```BodyMatcher```. There are two implementations available: ```EqualsBodyMatcher``` which is used for simple equality and ```FieldMatcher``` which is for form params. You can create your own.

#### Simple Bodies
```java
    @Test
    void simpleBody() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .body("I can do anything? Anything at all?")
                .thenReturn()
                .withStatus(201);

        assertEquals(201,
                Unirest.post("http://zombo.com").body("I can do anything? Anything at all?").asEmpty().getStatus()
        );
    }
```

#### Form Params
```java
    @Test
    void formParams() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .body(FieldMatcher.of("foo", "bar",
                                      "baz", "qux"))
                .thenReturn()
                .withStatus(201);

        assertEquals(201,
                Unirest.post("http://zombo.com")
                        .field("foo", "bar")
                        .field("baz", "qux")
                        .asEmpty().getStatus()
        );
    }
```

## Expected Responses
You can set all properties of a response.
```java
    @Test
    void response() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("Care for some tea mum?")
                .withHeader("x-zombo-brewing", "active")
                .withStatus(418, "I am a teapot");

        var response = Unirest.get("http://zombo.com").asString();

        assertEquals(418, response.getStatus());
        assertEquals("I am a teapot", response.getStatusText());
        assertEquals("Care for some tea mum?", response.getBody());
        assertEquals("active", response.getHeaders().getFirst("x-zombo-brewing"));
    }
```

### Responses with JSON Bodies
The mocking framework will use whatever ObjectMapper is configured with Unirest to marshall Pojos to expected responses.

```java

    static class Teapot { public String brewstatus = "on"; }
    @Test
    void pojos() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn(new Teapot());

        var response = Unirest.get("http://zombo.com").asString();

        assertEquals("{\"brewstatus\":\"on\"}", response.getBody());
    }
```





