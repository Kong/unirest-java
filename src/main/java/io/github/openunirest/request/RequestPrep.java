package io.github.openunirest.request;

import io.github.openunirest.http.HttpMethod;
import io.github.openunirest.http.options.Option;
import io.github.openunirest.http.options.Options;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.nio.entity.NByteArrayEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RequestPrep {
    private static final String CONTENT_TYPE = "content-type";
    private static final String ACCEPT_ENCODING_HEADER = "accept-encoding";
    private static final String USER_AGENT_HEADER = "user-agent";
    private static final String USER_AGENT = "unirest-java/1.3.11";
    private static final UriFormatter URI_FORMATTER = new UriFormatter();

    public static HttpRequestBase prepareRequest(HttpRequest request, boolean async) {

        setDefaultHeaders(request);

        if (!request.getHeaders().containsKey(USER_AGENT_HEADER)) {
            request.header(USER_AGENT_HEADER, USER_AGENT);
        }
        if (!request.getHeaders().containsKey(ACCEPT_ENCODING_HEADER)) {
            request.header(ACCEPT_ENCODING_HEADER, "gzip");
        }

        HttpRequestBase reqObj = getHttpRequest(request);
        setRequestHeaders(request, reqObj);
        setBody(request, async, reqObj);

        return reqObj;
    }

    private static void setBody(HttpRequest request, boolean async, HttpRequestBase reqObj) {
        if (!(request.getHttpMethod() == HttpMethod.GET || request.getHttpMethod() == HttpMethod.HEAD)) {
            if (request.getBody() != null) {
                HttpEntity entity = request.getBody().getEntity();
                if (async) {
                    if (reqObj.getHeaders(CONTENT_TYPE) == null || reqObj.getHeaders(CONTENT_TYPE).length == 0) {
                        reqObj.setHeader(entity.getContentType());
                    }
                    try {
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        entity.writeTo(output);
                        NByteArrayEntity en = new NByteArrayEntity(output.toByteArray());
                        ((HttpEntityEnclosingRequestBase) reqObj).setEntity(en);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ((HttpEntityEnclosingRequestBase) reqObj).setEntity(entity);
                }
            }
        }
    }

    private static void setRequestHeaders(HttpRequest request, HttpRequestBase reqObj) {
        Set<Map.Entry<String, List<String>>> entrySet = request.getHeaders().entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            List<String> values = entry.getValue();
            if (values != null) {
                for (String value : values) {
                    reqObj.addHeader(entry.getKey(), value);
                }
            }
        }
    }

    private static void setDefaultHeaders(HttpRequest request) {
        Object defaultHeaders = Options.getOption(Option.DEFAULT_HEADERS);
        if (defaultHeaders != null) {
            @SuppressWarnings("unchecked")
            Set<Map.Entry<String, String>> entrySet = ((Map<String, String>) defaultHeaders).entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
    }

    private static HttpRequestBase getHttpRequest(HttpRequest request) {
        HttpRequestBase reqObj = null;

        String urlToRequest = URI_FORMATTER.apply(request);

        switch (request.getHttpMethod()) {
            case GET:
                reqObj = new HttpGet(urlToRequest);
                break;
            case POST:
                reqObj = new HttpPost(urlToRequest);
                break;
            case PUT:
                reqObj = new HttpPut(urlToRequest);
                break;
            case DELETE:
                reqObj = new HttpDeleteWithBody(urlToRequest);
                break;
            case PATCH:
                reqObj = new HttpPatchWithBody(urlToRequest);
                break;
            case OPTIONS:
                reqObj = new HttpOptions(urlToRequest);
                break;
            case HEAD:
                reqObj = new HttpHead(urlToRequest);
                break;
        }
        return reqObj;
    }
}
