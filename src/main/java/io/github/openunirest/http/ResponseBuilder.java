package io.github.openunirest.http;


import java.io.InputStream;

import static io.github.openunirest.http.BodyData.from;

public class ResponseBuilder {

    public HttpResponse<InputStream> asBinary(org.apache.http.HttpResponse response){
        return new HttpResponse<>(response, from(response.getEntity(), BodyData::getRawInput));
    }

}
