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
import kong.unirest.core.UnirestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CoreFactoryTest {

    @AfterEach
    void tearDown() {
        try {
            CoreFactory.autoConfig();
        } catch (Exception e){}
    }

    @Test
    void whenThereIsNoImpl() {
        CoreFactory.setEngine(null);
        var ex = assertThrows(UnirestConfigException.class,
                () -> CoreFactory.getCore());
        assertEquals(String.format("No Json Parsing Implementation Provided%n" +
                "Please add a dependency for a Unirest JSON Engine. This can be one of:%n" +
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
                "</dependency>)%n" +
                "%n" +
                "Alternatively you may register your own JsonEngine directly with CoreFactory.setEngine(JsonEngine jsonEngine)"), ex.getMessage());
    }

    @Test
    void canSetItManually() {
        var mock = mock(JsonEngine.class);
        CoreFactory.setEngine(mock);
        assertThat(CoreFactory.getCore()).isSameAs(mock);
    }
}