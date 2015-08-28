package com.mashape.unirest.http.async.utils;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

public class AsyncIdleConnectionMonitorThread extends Thread {

	private final PoolingNHttpClientConnectionManager connMgr;

	public AsyncIdleConnectionMonitorThread(PoolingNHttpClientConnectionManager connMgr) {
		super();
		super.setDaemon(true);
		this.connMgr = connMgr;
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
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

}