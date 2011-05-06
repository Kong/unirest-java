package com.mashape.client.http;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.client.exceptions.MashapeClientException;

public class TokenUtil {
	private static final String TOKEN_URL = "https://api.mashape.com/requestToken";

	public static String requestToken(String developerKey)
			throws MashapeClientException {

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("devkey", developerKey);
		JSONObject response = (JSONObject) HttpClient.doRequest(HttpMethod.POST, TOKEN_URL,
				parameters, null);

		try {
			JSONArray errors = response.getJSONArray("errors");
			if (errors.length() > 0) {
				JSONObject error = errors.getJSONObject(0);
				throw new MashapeClientException(error.getString("message"),
						error.getInt("code"));
			} else {
				return response.getString("token");
			}
		} catch (MashapeClientException e1) {
			throw e1;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
