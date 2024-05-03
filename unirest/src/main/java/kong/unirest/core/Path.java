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

package kong.unirest.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for building a URI with query params
 */
public class Path {
    private String url;
    private String rawPath;

    /**
     * construct a path
     * @param url the URL
     */
    public Path(String url) {
        this(url, null);
    }

    /**
     * Construct a path with a URL that could be relative and a default base for it
     * @param url the url
     * @param defaultBasePath the default base
     */
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

    /**
     * replace path params designated with curley braces with a value
     * @param params a map of param names and values
     */
    public void param(Map<String, Object> params) {
        params.forEach((key, value) -> param(key, String.valueOf(value)));
    }

    /**
     * replace a single path param by name
     * @param name the name of the path param
     * @param value the value to replace it with
     */
    public void param(String name, String value) {
        Matcher matcher = Pattern.compile("\\{" + name + "\\}").matcher(url);
        if (!matcher.find()) {
            throw new UnirestException("Can't find route parameter name \"" + name + "\"");
        }
        this.url = matcher.replaceAll(encodePath(value));
    }

    private String encodePath(String value) {
        if(value == null){
            return "";
        }
        return Util.encode(value).replaceAll("\\+", "%20");
    }

    /**
     * Add a query param. This will result in a query param per value
     * @param name the name
     * @param value a collection of values
     */
    public void queryString(String name, Collection<?> value){
        for (Object cur : value) {
            queryString(name, cur);
        }
    }

    /**
     * Add a query param
     * @param name the name
     * @param value the value
     */
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

    /**
     * Add query params as a map of key/values
     * @param parameters the params to add
     */
    public void queryString(Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                queryString(param.getKey(), param.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return escape(url);
    }

    private String escape(String string) {
        return string.replaceAll(" ", "%20").replaceAll("\t", "%09");
    }

    /**
     * @return the full raw path
     */
    public String rawPath() {
        return rawPath;
    }

    /**
     * @return the URL without the query string
     */
    public String baseUrl() {
        if(url != null && url.contains("?")){
            return url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    /**
     * @return just the query string
     */
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

