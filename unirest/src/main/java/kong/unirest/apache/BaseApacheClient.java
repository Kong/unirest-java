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

import kong.unirest.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.util.function.Function;

abstract class BaseApacheClient {
    protected RequestConfigFactory configFactory = new DefaultFactory();


    protected CredentialsProvider toApacheCreds(Proxy proxy) {
        if(proxy != null && proxy.isAuthenticated()) {
            CredentialsProvider proxyCreds = new BasicCredentialsProvider();
            proxyCreds.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                    new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
            return proxyCreds;
        }
        return null;
    }

    protected <T> HttpResponse<T> transformBody(Function<RawResponse, HttpResponse<T>> transformer, RawResponse rr) {
        try {
            return transformer.apply(rr);
        }catch (RuntimeException e){
            String originalBody = recoverBody(rr);
            return new BasicResponse(rr, originalBody, e);
        }
    }

    private String recoverBody(RawResponse rr){
        try {
            return rr.getContentAsString();
        }catch (Exception e){
            return null;
        }
    }

    protected <T> void handleError(Config config, HttpResponse<T> httpResponse) {
        if(!httpResponse.isSuccess()){
            config.getErrorHandler().accept(httpResponse);
        }
    }

    protected <T> HttpResponse<T> handleError(Config config, HttpRequest<?> request, Exception e) {
        return (HttpResponse<T>)config.getErrorHandler().handle(request, e);
    }


    public void setConfigFactory(RequestConfigFactory configFactory) {
        this.configFactory = configFactory;
    }
}
