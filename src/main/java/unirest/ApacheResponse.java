/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ApacheResponse implements RawResponse {
    private final HttpResponse r;

    public ApacheResponse(HttpResponse r){
        this.r = r;
    }

    @Override
    public int getStatus(){
        return r.getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusText(){
        return r.getStatusLine().getReasonPhrase();
    }

    @Override
    public Headers getHeaders(){
        return new Headers(r.getAllHeaders());
    }

    @Override
    public InputStream getContent(){
        try {
            HttpEntity entity = r.getEntity();
            if(entity != null) {
                return entity.getContent();
            }
            return Util.emptyStream();
        } catch (IOException e) {
            throw new UnirestException(e);
        }
    }

    @Override
    public InputStreamReader getContentReader(){
        return new InputStreamReader(getContent());
    }
}
