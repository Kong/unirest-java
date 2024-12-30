# Unirest Mocks

### About
A series of mocks for use with Unirest for unit testing. Mocked clients will not make any real web requests. This allows you to test the input into unirest and to mock responses from expected requests.

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
* Body expectations are limited and do not include forms or files.
