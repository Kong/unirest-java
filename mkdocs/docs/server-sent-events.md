
## About Server Sent Events
Server-Sent Events (SSE) is a server push technology enabling a client to receive automatic updates from a server via an HTTP connection, and describes how servers can initiate data transmission towards clients once an initial client connection has been established. They are commonly used to send message updates or continuous data streams to a browser client and designed to enhance native, cross-browser streaming through a JavaScript API called EventSource, through which a client requests a particular URL in order to receive an event stream. The EventSource API is standardized as part of [HTML Living Standard by the WHATWG](https://html.spec.whatwg.org/multipage/server-sent-events.html). 
The media type for SSE is ```text/event-stream```.

## Consuming Server Sent Events With Unirest-Java
Unirest has two ways to consume a SSE web service; one async and one synchronous. Please be mindful that SSE is a persistent connection and unirest will keep the connection open as long as the server is willing and able. For this reason you may find the async method a better fit for most production systems. 

### Async Call
The following subscribes to wikipedia's SSE stream of recently changed pages. Maps each event into a POJO, and outputs the name of the changed page.
Note that Unirest will return you a CompletableFuture<Void> which you can hold on to to monitor the process.
```java
     var future = Unirest.sse("https://stream.wikimedia.org/v2/stream/recentchange")
        .connect(event -> {
            var change = event.asObject(RecentChange.class);
            System.out.println("Changed Page: " + change.getTitle());
        });

```


### Synchronous Call
The following subscribes to wikipedia's SSE stream of recently changed pages. Maps each event into a POJO, and outputs the name of the changed page.
```java
     Unirest.sse("https://stream.wikimedia.org/v2/stream/recentchange")
                    .connect()
                    .map(e -> e.asObject(RecentChange.class))
                    .forEach(r -> System.out.println("Changed Page: " + r.getTitle()));

```
