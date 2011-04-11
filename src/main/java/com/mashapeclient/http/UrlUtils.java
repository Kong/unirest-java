package com.mashapeclient.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class UrlUtils {

	private static String addRouteParameter(String url, String parameterName) {
		String result = url;
		if (!result.contains("?")) {
			result += "?";
		}
		if (!result.substring(result.length() - 1).equals("?")) {
			result += "&";
		}
		result += parameterName + "={" + parameterName + "}";
		return result;
	}

	public static String addClientParameters(String url) {
		String result = addRouteParameter(url, RestClientHelper.TOKEN_PARAM);
		result = addRouteParameter(result, RestClientHelper.LANGUAGE_PARAM);
		result = addRouteParameter(result, RestClientHelper.VERSION_PARAM);
		return result;
	}

	public static String getCleanUrl(String url, List<NameValuePair> parameters) {
		StringBuilder finalUrl = new StringBuilder();
		if (url != null && url.length() > 0) {
			if (parameters == null) {
				parameters = new ArrayList<NameValuePair>();
			}

			for (int i = 0; i < url.length(); i++) {

				String curChar = url.substring(i, i + 1);

				if (curChar.equals("{")) {
					// It may be a placeholder

					int pos = url.indexOf("}", i);
					if (pos > 0) {
						// It's a placeholder

						String placeHolder = url.substring(i + 1, pos);
						if (NVPUtils.existKey(placeHolder, parameters) == false) {
							// If it doesn't exist in the array, remove it

							if (url.substring(i - 1, i).equals("=")) {
								// It's a query string placeholder, remove also its name
								
								for (int t=finalUrl.length() - 1;t >=0;t--) {
									String backChar = finalUrl.substring(t, t + 1);
									if (backChar.equals("?") || backChar.equals("&")) {
										finalUrl.replace(0, finalUrl.length(), finalUrl.substring(0, (backChar.equals("?") ? t + 1 : t)));
										break;
									}
								}
								
							}

							i = pos;
							continue;

						}
					}
				}

				finalUrl.append(curChar);

			}
		}

		return finalUrl.toString().replace("?&", "?");
	}

	public static String removeQueryString(String url) {
		return url.split("\\?")[0];
	}

	public static List<NameValuePair> getQueryStringParameters(String url) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();

		String[] urlParts = url.split("\\?");
		if (urlParts.length > 1) {

			String queryString = urlParts[1];
			String[] parameters = queryString.split("&");

			for (String parameter : parameters) {
				String[] parameterParts = parameter.split("=");
				if (parameterParts.length > 1) {
					if (isPlaceholder(parameterParts[1]) == false) {
						result.add(new BasicNameValuePair(parameterParts[0],
								parameterParts[1]));
					}
				}
			}

		}
		return result;
	}

	public static boolean isPlaceholder(String val) {
		if (val != null && val.length() >= 2) {
			if (val.substring(0, 1).equals("{")
					&& val.substring(val.length() - 1).equals("}")) {
				return true;
			}
		}
		return false;
	}
}
