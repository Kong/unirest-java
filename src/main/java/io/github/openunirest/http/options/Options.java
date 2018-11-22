package io.github.openunirest.http.options;

import io.github.openunirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.http.utils.SyncIdleConnectionMonitorThread;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import static io.github.openunirest.http.options.Option.*;

public class Options {
    public static final int MAX_TOTAL = 200;
    public static final int MAX_PER_ROUTE = 20;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int SOCKET_TIMEOUT = 60000;

    private static boolean customClientSet = false;
    private static boolean customAsyncClientSet = false;
    private static Map<Option, Object> options = new HashMap<>();
    private static List<HttpRequestInterceptor> interceptors = new ArrayList<>();

    static {
        setDefaults();
    }

    public static void customClientSet() {
        customClientSet = true;
    }

    private static void setIfAbsent(Option option, Object value) {
        validateOption(option);
        options.putIfAbsent(option, value);
    }

    public static void setOption(Option option, Object value) {
        validateOption(option);
        flagClients(option);
        options.put(option, value);
    }

    private static void flagClients(Option option) {
        if(option == Option.HTTPCLIENT){
            customClientSet = true;
        } else if (option == Option.ASYNCHTTPCLIENT){
            customAsyncClientSet = true;
        }
    }

    private static void warmUpifClient(Option option) {
        if (isOptionNotSet(option, Option.HTTPCLIENT)) {
            customClientSet = false;
            buildHttpClient();
        } else if (isOptionNotSet(option, Option.ASYNCHTTPCLIENT)) {
            customAsyncClientSet = false;
            buildAsyncClient();
        }
    }

    private static boolean isOptionNotSet(Option o, Option expected){
        return o == expected && options.get(o) == null;
    }

    private static void validateOption(Option option) {
        if ((option == Option.CONNECTION_TIMEOUT || option == Option.SOCKET_TIMEOUT) && customClientSet) {
            throw new RuntimeException(
                    "You can't set custom timeouts when providing custom client implementations. " +
                            "Set the timeouts directly in your custom client configuration instead."
            );
        }
    }

    public static Object getOption(Option option) {
        warmUpifClient(option);
        return options.get(option);
    }


    private static <T> T getOptionOrDefault(Option option, T defaultValue) {
        return (T) options.computeIfAbsent(option, o -> defaultValue);
    }


    public static void refresh() {
        if (!customClientSet) {
            buildHttpClient();
        }
        if (!customAsyncClientSet) {
            buildAsyncClient();
        }
    }

    private static synchronized CloseableHttpAsyncClient buildAsyncClient() {
        PoolingNHttpClientConnectionManager asyncConnectionManager;
        try {
            asyncConnectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
            asyncConnectionManager.setMaxTotal(getOptionOrDefault(Option.MAX_TOTAL, MAX_TOTAL));
            asyncConnectionManager.setDefaultMaxPerRoute(getOptionOrDefault(Option.MAX_PER_ROUTE, MAX_PER_ROUTE));
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }

        HttpAsyncClientBuilder ab = HttpAsyncClientBuilder.create()
                .setDefaultRequestConfig(getRequestConfig())
                .setConnectionManager(asyncConnectionManager)
                .useSystemProperties();
        if (shouldDisableRedirects()) {
            ab.setRedirectStrategy(new NoRedirects());
        }
        if (shouldDisableCookieManagement()) {
            ab.disableCookieManagement();
        }
        interceptors.stream().forEach(i -> ab.addInterceptorFirst(i));
        CloseableHttpAsyncClient build = ab.build();

        options.put(Option.ASYNCHTTPCLIENT, build);
        options.put(Option.ASYNC_MONITOR, new AsyncIdleConnectionMonitorThread(asyncConnectionManager));
        return build;
    }

    private static synchronized void buildHttpClient() {
        PoolingHttpClientConnectionManager syncman = getSyncMonitor();

        // Create clients
        HttpClientBuilder cb = HttpClientBuilder.create()
                .setDefaultRequestConfig(getRequestConfig())
                .setConnectionManager(syncman)
                .useSystemProperties();
        if (shouldDisableRedirects()) {
            cb.disableRedirectHandling();
        }
        if (shouldDisableCookieManagement()) {
            cb.disableCookieManagement();
        }
        interceptors.stream().forEach(i -> cb.addInterceptorFirst(i));
        CloseableHttpClient build = cb.build();

        options.put(Option.HTTPCLIENT, build);
    }

    private static PoolingHttpClientConnectionManager getSyncMonitor() {
        PoolingHttpClientConnectionManager conman = (PoolingHttpClientConnectionManager)options.get(CONNECTION_MONITOR);
        if(conman != null){
            return conman;
        }

        conman = new PoolingHttpClientConnectionManager();
        conman.setMaxTotal(getOptionOrDefault(Option.MAX_TOTAL, MAX_TOTAL));
        conman.setDefaultMaxPerRoute(getOptionOrDefault(Option.MAX_PER_ROUTE, MAX_PER_ROUTE));

        SyncIdleConnectionMonitorThread defaultSyncMonitor = new SyncIdleConnectionMonitorThread(conman);
        defaultSyncMonitor.start();
        options.put(Option.SYNC_MONITOR, defaultSyncMonitor);
        options.put(Option.CONNECTION_MONITOR, conman);
        return conman;
    }

    private static boolean shouldDisableCookieManagement() {
        return !(Boolean) options.getOrDefault(COOKIE_MANAGEMENT, true);
    }

    private static boolean shouldDisableRedirects() {
        return !(Boolean) options.getOrDefault(FOLLOW_REDIRECTS, true);
    }

    private static RequestConfig getRequestConfig() {
        Integer connectionTimeout = getOptionOrDefault(Option.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        Integer socketTimeout = getOptionOrDefault(Option.SOCKET_TIMEOUT, SOCKET_TIMEOUT);
        HttpHost proxy = (HttpHost) Options.getOption(Option.PROXY);
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(socketTimeout)
                .setProxy(proxy)
                .build();
    }

    @Deprecated
    public static void init() {
        setDefaults();
        refresh();
    }

    private static void setDefaults() {
        customClientSet = false;
        customAsyncClientSet = false;
        setIfAbsent(Option.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        setIfAbsent(Option.SOCKET_TIMEOUT, SOCKET_TIMEOUT);
        setIfAbsent(Option.MAX_TOTAL, MAX_TOTAL);
        setIfAbsent(Option.MAX_PER_ROUTE, MAX_PER_ROUTE);
    }

    public static <T> Optional<T> tryGet(Option option, Class<T> as) {
        Object o = getOption(option);
        if (Objects.isNull(o) || !as.isAssignableFrom(o.getClass())) {
            return Optional.empty();
        }
        return Optional.of((T) o);
    }

    public static void removeOption(Option objectMapper) {
        options.remove(objectMapper);
    }

    public static boolean isRunning() {
        return options.get(HTTPCLIENT) != null || options.get(ASYNCHTTPCLIENT) != null;
    }

    public static void shutDown() {
        shutDown(true);
    }

    public static void shutDown(boolean clearOptions) {
        tryGet(Option.HTTPCLIENT,
                CloseableHttpClient.class)
                .ifPresent(Options::closeIt);
        options.remove(Option.HTTPCLIENT);

        tryGet(Option.SYNC_MONITOR,
                SyncIdleConnectionMonitorThread.class)
                .ifPresent(Thread::interrupt);
        options.remove(Option.SYNC_MONITOR);

        tryGet(Option.ASYNCHTTPCLIENT,
                CloseableHttpAsyncClient.class)
                .filter(CloseableHttpAsyncClient::isRunning)
                .ifPresent(Options::closeIt);
        options.remove(Option.ASYNCHTTPCLIENT);

        tryGet(Option.ASYNC_MONITOR,
                AsyncIdleConnectionMonitorThread.class)
                .ifPresent(Thread::interrupt);
        options.remove(Option.ASYNC_MONITOR);

        if (clearOptions) {
            options.clear();
            interceptors.clear();
            setDefaults();
        }
        customAsyncClientSet = false;
        customClientSet = false;
    }

    public static void closeIt(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            throw new UnirestException(e);
        }
    }

    public static void addInterceptor(HttpRequestInterceptor interceptor) {
        interceptors.add(interceptor);
        refresh();
    }

    public static void followRedirects(boolean enable) {
        options.put(FOLLOW_REDIRECTS, enable);
        refresh();
    }

    public static void enableCookieManagement(boolean enable) {
        options.put(COOKIE_MANAGEMENT, enable);
        refresh();
    }

    public static List<HttpRequestInterceptor> getInterceptors() {
        return interceptors;
    }
}
