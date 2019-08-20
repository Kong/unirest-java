/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kong.unirest.apache;

import kong.unirest.Config;
import kong.unirest.HttpRequest;
import org.apache.http.client.config.RequestConfig;

class DefaultFactory implements RequestConfigFactory {
    private static boolean isOldApache = false;
    @Override
    public RequestConfig apply(Config config, HttpRequest request) {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(request.getConnectTimeout())
                .setSocketTimeout(request.getSocketTimeout())
                .setConnectionRequestTimeout(request.getSocketTimeout())
                .setProxy(RequestOptions.toApacheProxy(request.getProxy()))
                .setCookieSpec(config.getCookieSpec());

        return tryNormalize(builder).build();
    }

    private RequestConfig.Builder tryNormalize(RequestConfig.Builder builder) {
        if(isOldApache){
            return builder;
        }

        try {
            return builder.setNormalizeUri(false);
        }catch (NoSuchMethodError e) {
            // setNormalizeUri doesnt exist in old version of apache client
            // the behavior that it does used to just be standard
            // so remember that we don't care
            isOldApache = true;
            return builder;
        }
    }
}
