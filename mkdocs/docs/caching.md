
# Caching
Unirest offers a simple im memory response caching mechanism with a few options for entry expiration.
This can be either be enabled with defaults, with expiration options or consumers may supply a custom cache backed by the cache of their choice.
It is reccomended that in high load systems consumers back the cache with a dedicated cache implementation like EHCache or Guava.

#### Basic cache:
```java
     Unirest.config().cacheResponses(true);

     //These 1st response will be cached in this case:
     Unirest.get("https://somwhere").asString();
     Unirest.get("https://somwhere").asString();
```
#### Advanced Options:
You can use a builder to customize eviction rules:

```java
   Unirest.config().cacheResponses(builder()
               .depth(5) // Depth is the max number of entries cached
               .maxAge(5, TimeUnit.MINUTES)); // Max age is how long the entry will be kept.
```

#### Custom Caches
You can also supply a custom cache by implementing the Cache Interface
```java
    public static void main(String[] args){
       Unirest.config().cacheResponses(Cache.builder().backingCache(new GuavaCache()));
    }

    // Example backing cache using Guava
    public static class GuavaCache implements Cache {
            com.google.common.cache.Cache<Key, HttpResponse> regular = CacheBuilder.newBuilder().build();
            com.google.common.cache.Cache<Key, CompletableFuture> async = CacheBuilder.newBuilder().build();
            @Override
            public <T> HttpResponse get(Key key, Supplier<HttpResponse<T>> fetcher) {
                try {
                    return regular.get(key, fetcher::get);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
    
            @Override
            public <T> CompletableFuture getAsync(Key key, Supplier<CompletableFuture<HttpResponse<T>>> fetcher) {
                try {
                    return async.get(key, fetcher::get);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

```