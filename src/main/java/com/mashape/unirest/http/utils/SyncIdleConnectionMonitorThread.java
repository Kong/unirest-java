package com.mashape.unirest.http.utils;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class SyncIdleConnectionMonitorThread extends Thread implements ConnectionMonitorThread {

	private final HttpClientConnectionManager connMgr;
	private boolean shutdown;

	public SyncIdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
		super();
		super.setDaemon(true);
		this.connMgr = connMgr;
		this.shutdown = false;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				synchronized (this) {
					wait(5000);
					// Close expired connections
					connMgr.closeExpiredConnections();
					// Optionally, close connections
					// that have been idle longer than 30 sec
					connMgr.closeIdleConnections(30, TimeUnit.SECONDS);

					if (shutdown) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

	public void shutdown() {
		this.shutdown = true;
	}
}
