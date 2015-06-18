package com.mashape.unirest.request;

import com.github.bingoohuang.utils.codec.Json;
import com.google.common.primitives.Primitives;

public class ValueUtils {
	public static String processValue(Object value) {
		return (value == null) ? "" : tryJson(value);
	}

	private static String tryJson(Object value) {
		if (value instanceof CharSequence ) return value.toString();
		if (value.getClass().isPrimitive()) return value.toString();
		if (Primitives.isWrapperType(value.getClass())) return value.toString();
		if (value instanceof Enum) return value.toString();

		return Json.json(value);
	}

}
