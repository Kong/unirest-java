package com.mashapeclient.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

public class NVPUtils {

	public static boolean existKey(String key, List<NameValuePair> list) {
		if (list != null) {
			for (NameValuePair ele : list) {
				if (ele.getName().equals(key)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<NameValuePair> merge(List<NameValuePair> list1, List<NameValuePair> list2) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();
		
		for (NameValuePair ele : list1) {
			if (!existKey(ele.getName(), result)) {
				result.add(ele);
			}
		}
		
		for (NameValuePair ele : list2) {
			if (!existKey(ele.getName(), result)) {
				result.add(ele);
			}
		}
		
		return result;
	}
	
}
