package com.mashape.unicorn.http.async;

import com.mashape.unicorn.http.HttpResponse;

public interface Callback<T> {

	void completed(HttpResponse<T> response);	
	
}
