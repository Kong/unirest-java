# Proxies
Sometimes you need to tunnel through a proxy. Unirest provides several different mechanisms for this.

## Simple Proxy
You can set a simple single proxy object. This will create a simple ```java.net.ProxySelector``` and ```java.net.Authenticator``` (if passing creds)
```java
    // Configure with authentication:
    Unirest.config().proxy("proxy.com", 7777, "username", "password1!");

    // or without
    Unirest.config().proxy("proxy.com", 7777);
    
    // You can also pass a Unirest Proxy object
    Unirest.config().proxy(new Proxy("proxy.com", 7777));
```

## Using System Settings For Proxies
Java has some defined system properties for Proxies. 
```java
    System.setProperty("http.proxyHost", "localhost");
    System.setProperty("http.proxyPort", "7777");

    Unirest.config().useSystemProperties(true);
```

## Dealing with Multiple Proxies
Sometimes, if the universe hates you. You might need to use different proxies for different hosts. 
This can be done by using a ```ProxySelector``` (and a ```Authenticator``` if auth is required)

```java
    Unirest.config()
        .proxy(new ProxySelector() {
            @Override
            public List<java.net.Proxy> select(URI uri) {
                if (uri.getHost().equals("homestarrunner.com")) {
                    return List.of(new java.net.Proxy(HTTP, InetSocketAddress.createUnresolved("proxy-sad.com", 7777)));
                }

                return List.of(new java.net.Proxy(HTTP, InetSocketAddress.createUnresolved("default.com", 7777)));
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

            }
        })
        .authenticator(new Authenticator() {
            @Override
            public PasswordAuthentication requestPasswordAuthenticationInstance(String host, InetAddress addr,
                                                                                int port, String protocol,
                                                                                String prompt, String scheme,
                                                                                URL url, RequestorType reqType) {
                // Please don't hardcode passwords in your code :D
                if(host.equals("homestarrunner.com")) {
                    return new PasswordAuthentication("strongbad", "password".toCharArray());
                }
                return new PasswordAuthentication("default", "password".toCharArray());
            }
        });
```