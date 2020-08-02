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

package kong.unirest;

import java.util.*;

class CompoundInterceptor implements Interceptor {
    private List<Interceptor> interceptors;

    CompoundInterceptor() {
        this(Collections.singletonList(new DefaultInterceptor()));
    }

    CompoundInterceptor(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public void onRequest(HttpRequest<?> request, Config config) {
        interceptors.forEach(i -> i.onRequest(request, config));
    }

    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
        interceptors.forEach(i -> i.onResponse(response, request, config));
    }

    @Override
    public HttpResponse<?> onFail(Exception e, HttpRequestSummary request, Config config) throws UnirestException {
        return  interceptors.stream()
                .map(i -> Optional.ofNullable(i.onFail(e, request, config)))
                .flatMap(Util::stream)
                .findFirst()
                .orElseThrow(() -> new UnirestException(e));
    }

    int size() {
        return interceptors.size();
    }

    List<Interceptor> getInterceptors() {
        return new ArrayList<>(interceptors);
    }

    void register(Interceptor t1) {
        if(interceptors.stream().anyMatch(i -> i instanceof DefaultInterceptor)){
            interceptors = new ArrayList<>();
        }
        if(!interceptors.contains(t1)){
            interceptors.add(t1);
        }
    }
}
