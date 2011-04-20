package com.mashapeclient.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashapeclient.exceptions.MashapeEmptyResponseException;
import com.mashapeclient.exceptions.MashapeInvalidResponseException;
import com.mashapeclient.exceptions.MashapeUnknownHTTPMethodException;
import com.mashapeclient.http.ssl.CustomVerifier;
import com.mashapeclient.utils.StreamUtils;

public class RestClientHelper {

	private static final String CLIENTLIB_VERSION = "V02";
	private static final String CLIENTLIB_JAVA = "JAVA";

	public static final String VERSION_PARAM = "_version";
	public static final String LANGUAGE_PARAM = "_language";
	public static final String TOKEN_PARAM = "_token";

	private ArrayList<NameValuePair> params;

	private String url;

	private int responseCode;
	private String message;

	private String response;

	public String getResponse() {
		return response;
	}

	public JSONObject getJSONResponse() throws MashapeEmptyResponseException,
			MashapeInvalidResponseException {
		if (response == null || response.length() == 0) {
			throw new MashapeEmptyResponseException();
		}
		try {
			return new JSONObject(response);
		} catch (JSONException e) {
			throw new MashapeInvalidResponseException(response);
		}
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClientHelper(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void execute(RequestMethod httpMethod, String token)
			throws MashapeUnknownHTTPMethodException {

		addParam(TOKEN_PARAM, token);
		addParam(LANGUAGE_PARAM, CLIENTLIB_JAVA);
		addParam(VERSION_PARAM, CLIENTLIB_VERSION);

		this.url = UrlUtils.addClientParameters(url);

		switch (httpMethod) {
		case GET: {
			doGet();
			break;
		}
		case POST: {
			doPost();
			break;
		}
		case PUT: {
			doRequest(new HttpPut(UrlUtils.removeQueryString(replaceParameters())));
			break;
		}
		case DELETE: {
			doRequest(new HttpDeleteWithBody(UrlUtils.removeQueryString(replaceParameters())));
			break;
		}
		default:
			throw new MashapeUnknownHTTPMethodException();
		}
	}

	public void doPost() {
		doRequest(new HttpPost(UrlUtils.removeQueryString((replaceParameters()))));
	}

	private void doRequest(HttpEntityEnclosingRequestBase request) {
		RestClientUtils.initRequest(request,
				NVPUtils.merge(params, UrlUtils.getQueryStringParameters(url)));
		executeRequest(request);
	}

	private String replaceParameters() {
		String finalUrl = new String(url);
		if (!params.isEmpty()) {
			for (NameValuePair p : params) {

				try {
					finalUrl = finalUrl.replace("{" + p.getName() + "}",
							URLEncoder.encode(p.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}

			}
		}
		return finalUrl;
	}
	
	private void doGet() {
		// add parameters
		
		HttpGet request = new HttpGet(replaceParameters());
		executeRequest(request);
	}

	private void executeRequest(HttpUriRequest request) {
		HttpClient client = new DefaultHttpClient();
		
		// Accept any SSL certificate
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client
				.getConnectionManager().getSchemeRegistry().getScheme("https")
				.getSocketFactory();
		final X509HostnameVerifier delegate = sslSocketFactory
				.getHostnameVerifier();
		if (!(delegate instanceof CustomVerifier)) {
			sslSocketFactory.setHostnameVerifier(new CustomVerifier());
		}

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				response = StreamUtils.convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			throw new RuntimeException(e);
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			throw new RuntimeException(e);
		}
	}
	
}
