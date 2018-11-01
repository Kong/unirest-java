package unirest;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.function.Function;

class UriFormatter implements Function<HttpRequest, String> {
    @Override
    public String apply(HttpRequest request) {
        try {
            return new URIBuilder(request.getUrl()).toString();
        } catch (URISyntaxException e) {
            throw new UnirestException(e);
        }
    }

}
