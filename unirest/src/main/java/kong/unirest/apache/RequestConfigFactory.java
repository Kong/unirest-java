package kong.unirest.apache;

import kong.unirest.Config;
import kong.unirest.HttpRequest;
import org.apache.http.client.config.RequestConfig;

import java.util.function.BiFunction;

class RequestConfigFactory implements BiFunction<Config, HttpRequest, RequestConfig> {
    @Override
    public RequestConfig apply(Config config, HttpRequest request) {
        return RequestConfig.custom()
                .setConnectTimeout(request.getConnectTimeout())
                .setSocketTimeout(request.getSocketTimeout())
                .setNormalizeUri(false)
                .setConnectionRequestTimeout(request.getSocketTimeout())
                .setProxy(RequestOptions.toApacheProxy(request.getProxy()))
                .setCookieSpec(config.getCookieSpec())
                .build();
    }
}
