package com.mashape.unicorn.http.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MapUtil {

	public static List<NameValuePair> getList(Map<String, Object> parameters) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();
		if (parameters != null) {

			Set<String> keySet = parameters.keySet();
			for (String key : keySet) {
				result.add(new BasicNameValuePair(key, parameters.get(key).toString()));
			}

		}
		return result;
	}

}

