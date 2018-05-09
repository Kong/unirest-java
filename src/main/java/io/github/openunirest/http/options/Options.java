package io.github.openunirest.http.options;

import io.github.openunirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.http.utils.SyncIdleConnectionMonitorThread;
import org.apache.http.HttpHost;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Options {
	public static final int MAX_TOTAL = 200;
	public static final int MAX_PER_ROUTE = 20;
	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int SOCKET_TIMEOUT = 60000;

	private static Map<Option, Object> options = new HashMap<>();
	private static boolean customClientSet = false;
	private static PoolingHttpClientConnectionManager syncConnectionManager;
	private static boolean isRunning = true;
	private static SyncIdleConnectionMonitorThread defaultSyncMonitor;

	public static void customClientSet() {
		customClientSet = true;
	}

	public static void setOption(Option option, Object value) {
		validateOption(option);
		options.put(option, value);
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
		warmUp();
		return options.get(option);
	}

	private static void warmUp() {
		if(!isRunning()){
			init();
		}
	}

	private static <T> T getOptionOrDefault(Option option, T defaultValue){
		warmUp();
		return (T)options.computeIfAbsent(option, o -> defaultValue);
	}

	private static void createMonitors() {
		syncConnectionManager = new PoolingHttpClientConnectionManager();
		defaultSyncMonitor = new SyncIdleConnectionMonitorThread(syncConnectionManager);
		defaultSyncMonitor.start();
		setOption(Option.SYNC_MONITOR, defaultSyncMonitor);
		isRunning = true;
	}


	public static void refresh() {
		RequestConfig clientConfig = getRequestConfig();

		Integer maxTotal = getOptionOrDefault(Option.MAX_TOTAL, MAX_TOTAL);
		Integer maxPerRoute = getOptionOrDefault(Option.MAX_PER_ROUTE, MAX_PER_ROUTE);
		syncConnectionManager.setMaxTotal(maxTotal);
		syncConnectionManager.setDefaultMaxPerRoute(maxPerRoute);

		// Create clients
		CloseableHttpClient build = HttpClientBuilder.create()
				.setDefaultRequestConfig(clientConfig)
				.setConnectionManager(syncConnectionManager)
				.useSystemProperties()
				.build();

		setOption(Option.HTTPCLIENT, build);

		PoolingNHttpClientConnectionManager asyncConnectionManager;
		try {
			asyncConnectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
			asyncConnectionManager.setMaxTotal(maxTotal);
			asyncConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
		} catch (IOReactorException e) {
			throw new RuntimeException(e);
		}

		CloseableHttpAsyncClient asyncClient = HttpAsyncClientBuilder.create()
				.setDefaultRequestConfig(clientConfig)
				.setConnectionManager(asyncConnectionManager)
				.useSystemProperties()
				.build();

		setOption(Option.ASYNCHTTPCLIENT, asyncClient);
		setOption(Option.ASYNC_MONITOR, new AsyncIdleConnectionMonitorThread(asyncConnectionManager));
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

	public static void init() {
		createMonitors();
		setDefaults();
		refresh();
	}

	private static void setDefaults() {
		customClientSet = false;
		options.clear();
		setOption(Option.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		setOption(Option.SOCKET_TIMEOUT, SOCKET_TIMEOUT);
		setOption(Option.MAX_TOTAL, MAX_TOTAL);
		setOption(Option.MAX_PER_ROUTE, MAX_PER_ROUTE);
		setOption(Option.SYNC_MONITOR, defaultSyncMonitor);
	}

	public static <T> Optional<T> tryGet(Option option, Class<T> as) {
		Object o = getOption(option);
		if(Objects.isNull(o) || !as.isAssignableFrom(o.getClass())){
			return Optional.empty();
		}
		return Optional.of((T) o);
	}

	public static void removeOption(Option objectMapper) {
		options.remove(objectMapper);
	}

	public static boolean isRunning() {
		return isRunning;
	}

	public static void shutDown() {
		tryGet(Option.HTTPCLIENT,
				CloseableHttpClient.class)
				.ifPresent(Options::closeIt);

		tryGet(Option.SYNC_MONITOR,
				SyncIdleConnectionMonitorThread.class)
				.ifPresent(Thread::interrupt);

		tryGet(Option.ASYNCHTTPCLIENT,
				CloseableHttpAsyncClient.class)
				.filter(CloseableHttpAsyncClient::isRunning)
				.ifPresent(Options::closeIt);

		tryGet(Option.ASYNC_MONITOR,
				AsyncIdleConnectionMonitorThread.class)
				.ifPresent(Thread::interrupt);
		
		isRunning = false;
	}

	public static void closeIt(Closeable c) {
		try {
			c.close();
		}catch (IOException e){
			throw new UnirestException(e);
		}
	}
}
