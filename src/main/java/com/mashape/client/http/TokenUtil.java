/*
 * 
 * Mashape Java Client library.
 * Copyright (C) 2011 Mashape, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * The author of this software is Mashape, Inc.
 * For any question or feedback please contact us at: support@mashape.com
 * 
 */

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
