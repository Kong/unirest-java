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

package com.mashape.client.http.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {

	private static final String CLIENTLIB_VERSION = "V03";
	private static final String CLIENTLIB_LANGUAGE = "JAVA";

	private static final String VERSION_PARAM = "_version";
	private static final String LANGUAGE_PARAM = "_language";
	private static final String TOKEN_PARAM = "_token";

	public static RequestPrepareResult prepareRequest(String url,
			Map<String, String> parameters, boolean addRegularQueryStringParameters) throws UnsupportedEncodingException {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		Set<String> keySet = new HashSet<String>(parameters.keySet());
		for (String key : keySet) {
			if (parameters.get(key) == null) {
				parameters.remove(key);
			}
		}

		Pattern p = Pattern.compile("\\{([\\w\\.]+)\\}");

		Matcher matcher = p.matcher(url);
		String finalUrl = url;
		while (matcher.find()) {
			String key = matcher.group(1);
			if (parameters.containsKey(key)) {
				String parameterValue = parameters.get(key);
				finalUrl = finalUrl.replaceAll("(\\?.+)\\{" + key + "\\}",
						"$1" + URLEncoder.encode(parameterValue, "UTF-8"));
				finalUrl = finalUrl.replaceAll("\\{" + key + "\\}",
						UriUtils.encodeUri(parameterValue, "UTF-8"));
			} else {
				finalUrl = finalUrl.replaceAll("&?[\\w]*=?\\{" + key + "\\}", "");
			}
		}

		finalUrl = finalUrl.replaceAll("\\?&", "?");
		finalUrl = finalUrl.replaceAll("\\?$", "");
		
		if (addRegularQueryStringParameters) {
			addRegularQueryStringParameters(finalUrl, parameters);
		}

		return new RequestPrepareResult(finalUrl, parameters);
	}

	private static void addRegularQueryStringParameters(
			String url, Map<String, String> parameters) throws UnsupportedEncodingException {
		String[] urlParts = url.split("\\?");
		if (urlParts.length > 1) {
			String queryString = urlParts[1];
			for (String param : queryString.split("&")) {
				String[] pair = param.split("=");
				if (pair.length > 1) {
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = URLDecoder.decode(pair[1], "UTF-8");
					if (!parameters.containsKey(key)) {
						parameters.put(key, value);
					}
				}
			}
		}

	}

	public static RequestPrepareResult addClientParameters(String url,
			Map<String, String> parameters, String token) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}

		StringBuilder result = new StringBuilder(url);

		if (url.contains("?")) {
			result.append("&");
		} else {
			result.append("?");
		}

		result.append(addClientParameter(TOKEN_PARAM));
		parameters.put(TOKEN_PARAM, token);
		result.append("&" + addClientParameter(LANGUAGE_PARAM));
		parameters.put(LANGUAGE_PARAM, CLIENTLIB_LANGUAGE);
		result.append("&" + addClientParameter(VERSION_PARAM));
		parameters.put(VERSION_PARAM, CLIENTLIB_VERSION);

		return new RequestPrepareResult(result.toString(), parameters);
	}

	private static String addClientParameter(String parameter) {
		return parameter + "={" + parameter + "}";
	}

}
