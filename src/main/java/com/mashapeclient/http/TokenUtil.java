package com.mashapeclient.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashapeclient.exceptions.MashapeApiKeyException;
import com.mashapeclient.exceptions.MashapeEmptyResponseException;
import com.mashapeclient.exceptions.MashapeInvalidResponseException;

public class TokenUtil {

	private static final String TOKEN_URL = "https://api.mashape.com/requestToken";
	
	public static String getToken(String apiKey) throws MashapeEmptyResponseException, MashapeInvalidResponseException, MashapeApiKeyException {
		RestClientHelper restClient = new RestClientHelper(TOKEN_URL);
		restClient.addParam("apikey", apiKey);
		try {
			restClient.doPost();
			JSONObject jsonResponse = restClient.getJSONResponse();
			JSONArray errors = jsonResponse.getJSONArray("errors");
			if (errors.length() > 0) {
				JSONObject error = errors.getJSONObject(0);
				throw new MashapeApiKeyException(error.getString("message"), error.getInt("code"));
			} else {
				return jsonResponse.getString("token");
			}
		} catch (JSONException e) {
			throw new MashapeInvalidResponseException();
		}
	}
	
}
