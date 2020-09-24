# Unirest Mocks

### About
A series of mocks for use with Unirest for unit testing. The mock clients will not make any real web requests.

### Usage
```java
class MyTest {
    @Test
    void expectGet(){
        MockClient mock = new MockClient();
        Unirest.config().httpClient(mock);

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
