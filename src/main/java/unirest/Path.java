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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    private String url;

    public Path(String url) {
        this.url = url;
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
        this.url = url.replaceAll("\\{" + name + "\\}", URLParamEncoder.encode(value));
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
            queryString.append(URLEncoder.encode(name));
            if(value != null) {
                queryString.append("=").append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new UnirestException(e);
        }
        url += queryString.toString();
    }

    public void queryString(Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                if (param.getValue() instanceof String || param.getValue() instanceof Number || param.getValue() instanceof Boolean || param.getValue() == null) {
                    queryString(param.getKey(), param.getValue());
                } else {
                    throw new UnirestException("Parameter \"" + param.getKey() +
                            "\" can't be sent with a GET request because of type: "
                            + param.getValue().getClass().getName());
                }
            }
        }
    }

    @Override
    public String toString() {
        return url;
    }
}
