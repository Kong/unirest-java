package io.github.openunirest.http;

import org.apache.http.Header;
import org.omg.CORBA.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Headers extends HashMap<String, List<String>> {

	private static final long serialVersionUID = 71310341388734766L;

	public Headers(){
		super();
	}

	public Headers(Header[] pairs){
		for (Header header : pairs) {
			add(header.getName(), header.getValue());
		}
	}

	public String getFirst(Object key) {
		List<String> list = get(key);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public void add(String name, String value){
		computeIfAbsent(name, k -> new ArrayList<>()).add(value);
	}

}
