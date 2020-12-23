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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Path {
    private String url;
    private String rawPath;

    Path(String url, String defaultBasePath) {
        if(defaultBasePath != null && url != null && !url.toLowerCase().startsWith("http")){
            String full = defaultBasePath + url;
            this.url = full;
            this.rawPath = full;
        } else {
            this.url = url;
            this.rawPath = url;
        }
    }

    public Path(String url) {
        this(url, null);
    }

    public void param(Map<String, Object> params) {
        params.forEach((key, value) -> param(key, String.valueOf(value)));
    }

    public void param(String name, String value) {
        Matcher matcher = Pattern.compile("\\{" + name + "\\}").matcher(url);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        if (count == 0) {
            throw new UnirestException("Can't find route parameter name \"" + name + "\"");
        }
        this.url = url.replaceAll("\\{" + name + "\\}", encodePath(value));
    }

    private String encodePath(String value) {
        if(value == null){
            return "";
        }
        return Util.encode(value).replaceAll("\\+", "%20");
    }

    public void queryString(String name, Collection<?> value){
        for (Object cur : value) {
            queryString(name, cur);
        }
    }

    public void queryString(String name, Object value) {
        StringBuilder queryString = new StringBuilder();
        if (url.contains("?")) {
            queryString.append("&");
        } else {
            queryString.append("?");
        }
        try {
            queryString.append(URLEncoder.encode(name, "UTF-8"));
            if(value != null) {
                queryString.append("=").append(URLEncoder.encode(String.valueOf(value), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new UnirestException(e);
        }
        url += queryString.toString();
    }

    public void queryString(Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                queryString(param.getKey(), param.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return url;
    }

    public String rawPath() {
        return rawPath;
    }

    public String baseUrl() {
        if(url != null && url.contains("?")){
            return url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    public String getQueryString(){
        return url.substring(url.indexOf("?")+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Path path = (Path) o;
        return Objects.equals(url, path.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}

