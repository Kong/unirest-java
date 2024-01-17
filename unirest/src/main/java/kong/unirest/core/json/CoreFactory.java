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

package kong.unirest.core.json;

import kong.unirest.core.UnirestConfigException;

import java.util.Objects;
import java.util.ServiceLoader;

public class CoreFactory {
    private static JsonEngine ENGINE;
    static {
        ENGINE = ServiceLoader.load(JsonEngine.class)
                .findFirst()
                .orElse(null);
    }
    public static JsonEngine getCore() {
        if(ENGINE == null){
            throw new UnirestConfigException("No Json Parsing Implementation Provided\n" +
                    "Please add a dependency for a Unirest JSON Engine. This can be one of:" +
                    "\n" +
                    "<!-- Google Gson (the previous core impl) -->\n" +
                    "<dependency>\n" +
                    "  <groupId>com.konghq</groupId>\n" +
                    "  <artifactId>unirest-object-mappers-gson</artifactId>\n" +
                    "  <version>${latest-version}</version>\n" +
                    "</dependency>\n" +
                    "\n" +
                    "<!-- Jackson -->\n" +
                    "<dependency>\n" +
                    "  <groupId>com.konghq</groupId>\n" +
                    "  <artifactId>unirest-object-mappers-jackson</artifactId>\n" +
                    "  <version>${latest-version}</version>\n" +
                    "</dependency>)");
        }
        return ENGINE;
    }

    public static void setEngine(JsonEngine engine){
        Objects.requireNonNull(engine, "JsonEngine may not be null");
        ENGINE = engine;
    }
}
