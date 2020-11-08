# Unirest Mocks

### About
A series of mocks for use with Unirest for unit testing. The mock clients will not make any real web requests.

### Installation
Maven
```xml
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-mocks</artifactId>
    <version>3.11.04</version>
    <scope>test</scope>
</dependency>
```

### Usage
```java
class MyTest {
    @Test
    void expectGet(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                        .thenReturn("You can do anything!");
        
        assertEquals(
            "You can do anything!", 
            Unirest.get("http://zombo.com").asString().getBody()
        );
        
        //Optional: Verify all expectations were fulfilled
        mock.verifyAll();
    }
}
```


### Future Enhancements
   * Mocks currently only work with the regular client and not the async engine.
   * Body expectations are limited and do not include forms or files.
