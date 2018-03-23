package io.github.openunirest.http;


import java.io.InputStream;

import static io.github.openunirest.http.BodyData.from;

public class ResponseBuilder {
    private final org.apache.http.HttpResponse response;

    public ResponseBuilder(org.apache.http.HttpResponse response) {
        this.response = response;
    }

    public HttpResponse<InputStream> asBinary(){
        return new HttpResponse<>(response, from(response.getEntity(), BodyData::getRawInput));
    }

}
