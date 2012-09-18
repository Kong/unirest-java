package com.mashape.client.http.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;


public class HttpUtils {

	public static String getQueryString(Map<String, Object> parameters) {
		if (parameters != null) {
			StringBuilder result = new StringBuilder();
			boolean isFirst = true;
			for(Entry<String, Object> entry : parameters.entrySet()) {
				if (!(entry.getValue() instanceof File)) {
					if (!isFirst) {
						result.append("&");
					}
					result.append(entry.getKey())
						  .append("=")
						  .append(encodeParameter(entry.getValue().toString()));
					
					if (isFirst) isFirst = false;
				}
			}
			return result.toString();
		}
		return "";
	}
	
	public static String encodeUrl(String url) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : url.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }
	
	public static String encodeParameter(String value) {
		if (value != null) {
			try {
				return URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public static <T> MashapeResponse<T> getResponse(ResponseType responseType, HttpResponse response) {
		MashapeResponse<T> mashapeResponse = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try {
				byte[] rawBody;
				try {
					rawBody = IOUtils.toByteArray(entity.getContent());
				} catch (IOException e2) {
					throw new RuntimeException(e2);
				}
				InputStream inputStream = new ByteArrayInputStream(rawBody);
				
				switch(responseType) {
				case BINARY:
					mashapeResponse = (MashapeResponse<T>) new MashapeResponse<InputStream>(response, inputStream, inputStream);
					break;
				case STRING:
					mashapeResponse = (MashapeResponse<T>) new MashapeResponse<String>(response, inputStream, new String(rawBody));
					break;
				case JSON:
					String jsonString = new String(rawBody).trim();
					if (jsonString.startsWith("[")) {
						try {
							mashapeResponse = (MashapeResponse<T>) new MashapeResponse<JSONArray>(response, inputStream, new JSONArray(jsonString));
						} catch (Exception e) {
							throw new RuntimeException("Invalid JSON array: " + jsonString);
						}
					} else {
						try {
							mashapeResponse = (MashapeResponse<T>) new MashapeResponse<JSONObject>(response, inputStream, new JSONObject(jsonString));
						} catch (Exception e) {
							throw new RuntimeException("Invalid JSON object: " + jsonString);
						}
					}
					break;
				}
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
		return mashapeResponse;
	}
	
}
