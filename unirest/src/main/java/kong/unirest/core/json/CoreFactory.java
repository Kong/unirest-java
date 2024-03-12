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

/**
 * The CoreFactory is a service locator for JsonEngines
 * Because core does not have a dependency on the various Json implementations
 * this class automatically finds and holds on to a implementation.
 *
 * It will look in the following places in this order:
 *  1. use the java.util.ServiceLoader to load a class by looking for a meta config file in
 *     the resources. They should exist at META-INF.services/kong.unirest.core.json.JsonEngine
 *     The ServiceLoader will use the first one it finds.
 *     see https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
 *
 *  2. It will attempt to load the loader by class name from the classloader by known names in order. These are:
 *      1. kong.unirest.jackson.JacksonEngine
 *      2. kong.unirest.gson.GsonEngine
 *
 *  3. Clients may set a engine with the setEngine method
 */
public class CoreFactory {
    private static final List<Supplier<JsonEngine>> SERVICE_LOCATORS = List.of(
            CoreFactory::findEngineWithServiceLocator,
            CoreFactory::findEngineWithClassLoader
    );

    private static final List<String> KNOWN_IMPLEMENTATIONS = List.of(
            "kong.unirest.modules.jackson.JacksonEngine",
            "kong.unirest.modules.gson.GsonEngine"
    );

    private static JsonEngine engine;

    static {
        autoConfig();
    }

    /**
     * Automatically find and register a JsonEngine.
     * This method is called by the static block of this class.
     */
    public static void autoConfig() {
        engine = findEngine();
    }

    /**
     * Gets the registered instance
     * @return the JsonEngine registered with this class
     * @throws UnirestConfigException if there is no known instance
     */
    public static JsonEngine getCore() {
        if(engine == null){
            throw getException();
        }
        return engine;
    }

    /**
     * Sets the locators engine to a specific instance
     * @param jsonEngine the engine you wish to register
     */
    public static void setEngine(JsonEngine jsonEngine){
        engine = jsonEngine;
    }

    /**
     * Attempt to find the engine by one of the two strategies
     *  1. use the java.util.ServiceLoader to load a class by looking for a meta config file in
     *     the resources. They should exist at META-INF.services/kong.unirest.core.json.JsonEngine
     *     The ServiceLoader will use the first one it finds.
     *     see https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
     *
     *  2. It will attempt to load the loader by class name from the classloader by known names in order. These are:
     *      1. kong.unirest.jackson.JacksonEngine
     *      2. kong.unirest.gson.GsonEngine
     * @return the first JsonEngine it finds
     */
    public static JsonEngine findEngine() {
        for(Supplier<JsonEngine> engineSupplier : SERVICE_LOCATORS){
            var foundEngine = engineSupplier.get();
            if(foundEngine != null){
                return foundEngine;
            }
        }
        return null;
    }

    /**
     * Finds an engine with the ServiceLoader
     * uses the java.util.ServiceLoader to load a class by looking for a meta config file in
     *     the resources. They should exist at META-INF.services/kong.unirest.core.json.JsonEngine
     *     The ServiceLoader will use the first one it finds.
     *     see https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
     * @return the first JsonEngine it finds
     */
    public static JsonEngine findEngineWithServiceLocator() {
        return ServiceLoader.load(JsonEngine.class)
                .findFirst()
                .orElse(null);
    }

    /**
     * It will attempt to load the loader by class name from the classloader by known names in order. These are:
     *      1. kong.unirest.jackson.JacksonEngine
     *      2. kong.unirest.gson.GsonEngine
     *  @return the first JsonEngine it finds
     */
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
        return new UnirestConfigException(String.format("No Json Parsing Implementation Provided%n" +
                "Please add a dependency for a Unirest JSON Engine. " +
                "This can be one of:" +
                "%n" +
                "<!-- Google Gson (the previous core impl) -->%n" +
                "<dependency>%n" +
                "  <groupId>com.konghq</groupId>%n" +
                "  <artifactId>unirest-object-mappers-gson</artifactId>%n" +
                "  <version>${latest-version}</version>%n" +
                "</dependency>%n" +
                "%n" +
                "<!-- Jackson -->%n" +
                "<dependency>%n" +
                "  <groupId>com.konghq</groupId>%n" +
                "  <artifactId>unirest-object-mappers-jackson</artifactId>%n" +
                "  <version>${latest-version}</version>%n" +
                "</dependency>)%n%n" +
                "Alternatively you may register your own JsonEngine directly with CoreFactory.setEngine(JsonEngine jsonEngine)"));
    }
}
