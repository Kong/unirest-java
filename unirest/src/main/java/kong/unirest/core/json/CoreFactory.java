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

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class CoreFactory {
    private static final List<Supplier<JsonEngine>> SERVICE_LOCATORS = List.of(
            CoreFactory::findEngineWithServiceLocator,
            CoreFactory::findEngineWithClassLoader
    );

    private static final List<String> KNOWN_IMPLEMENTATIONS = List.of(
            "kong.unirest.jackson.JacksonEngine",
            "kong.unirest.gson.GsonEngine"
    );

    private static JsonEngine engine;

    static {
        autoConfig();
    }

    public static void autoConfig() {
        engine = findEngine();
    }

    public static JsonEngine getCore() {
        if(engine == null){
            throw getException();
        }
        return engine;
    }

    public static void setEngine(JsonEngine jsonEngine){
        engine = jsonEngine;
    }

    public static JsonEngine findEngine() {
        for(Supplier<JsonEngine> engineSupplier : SERVICE_LOCATORS){
            var foundEngine = engineSupplier.get();
            if(foundEngine != null){
                return foundEngine;
            }
        }
        return null;
    }

    public static JsonEngine findEngineWithServiceLocator() {
        return ServiceLoader.load(JsonEngine.class)
                .findFirst()
                .orElse(null);
    }

    public static JsonEngine findEngineWithClassLoader() {
        for(String className : KNOWN_IMPLEMENTATIONS) {
            try {
                Class<?> engineClass = Class.forName(className);
                return (JsonEngine) engineClass.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static UnirestConfigException getException() {
        return new UnirestConfigException("No Json Parsing Implementation Provided\n" +
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
}
