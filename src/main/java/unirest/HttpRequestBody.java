package unirest;

import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

public interface HttpRequestBody extends HttpRequest<HttpRequestWithBody> {
    HttpRequestBody charset(Charset charset);

    HttpRequestMultPart field(String name, Collection<?> value);

    HttpRequestMultPart field(String name, File file);

    HttpRequestMultPart field(String name, File file, String contentType);

    HttpRequestMultPart field(String name, Object value);

    HttpRequestMultPart field(String name, Object value, String contentType);

    HttpRequestMultPart fields(Map<String, Object> parameters);

    HttpRequestMultPart field(String name, InputStream stream, ContentType contentType, String fileName);

    HttpRequestMultPart field(String name, InputStream stream, String fileName);

    HttpRequestUniBody body(JsonNode body);

    HttpRequestUniBody body(String body);

    HttpRequestUniBody body(Object body);

    HttpRequestUniBody body(byte[] body);

    HttpRequestUniBody body(JSONObject body);

    HttpRequestUniBody body(JSONArray body);

    Charset getCharset();
}
