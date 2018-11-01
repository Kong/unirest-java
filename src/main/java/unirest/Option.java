package unirest;

public enum Option {
	HTTPCLIENT(false),
	ASYNCHTTPCLIENT(false),
	CONNECTION_TIMEOUT,
	SOCKET_TIMEOUT,
	DEFAULT_HEADERS,
	SYNC_MONITOR(false),
	ASYNC_MONITOR(false),
	MAX_TOTAL,
	MAX_PER_ROUTE,
	PROXY,
	OBJECT_MAPPER,
	FOLLOW_REDIRECTS,
	COOKIE_MANAGEMENT;

	private final boolean canSurviveShutdown;

	Option(){this(true);}
	Option(boolean canSurviveShutdown){
		this.canSurviveShutdown = canSurviveShutdown;
	}

	public boolean canSurviveShutdown() {
		return canSurviveShutdown;
	}
}
