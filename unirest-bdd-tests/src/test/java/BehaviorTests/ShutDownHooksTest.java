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

package BehaviorTests;

import com.google.common.collect.Sets;
import kong.unirest.Unirest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShutDownHooksTest extends BddTest {

    @Override @BeforeEach
    public void setUp() {
        super.setUp();
        clearUnirestHooks();
    }

    @Test
    void ifClientsAreAlreadyRunningCanAddShutdownHooks() throws Exception  {
        assertShutdownHooks(0);

        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET).asEmptyAsync();
        Unirest.config().addShutdownHook(true);
        Unirest.config().addShutdownHook(true);

        assertShutdownHooks(1);
    }

    @Test
    void canAddShutdownHooks() throws Exception {
        assertShutdownHooks(0);

        Unirest.config().addShutdownHook(true).getClient();
        Unirest.config().addShutdownHook(true).getAsyncClient();

        assertShutdownHooks(1);
    }

    private void assertShutdownHooks(int expected) {
        Set<Thread> threads = getShutdownHookMap();

        assertEquals(expected, threads.stream().filter(t -> "Unirest Apache Client Shutdown Hook".equals(t.getName())).count());
        assertEquals(expected, threads.stream().filter(t -> "Unirest Apache Async Client Shutdown Hook".equals(t.getName())).count());
    }


    private void clearUnirestHooks() {
        getShutdownHookMap()
                .stream()
                .filter(t -> t.getName().contains("Unirest"))
                .forEach(t -> Runtime.getRuntime().removeShutdownHook(t));
    }

    private Set<Thread> getShutdownHookMap() {
        try {
            // oh this is so dirty and horrible. Set to @Disabled if it starts to be a problem.
            Class clazz = Class.forName("java.lang.ApplicationShutdownHooks");
            Field field = clazz.getDeclaredField("hooks");
            field.setAccessible(true);
            Set<Thread> threads = ((Map<Thread, Thread>) field.get(null)).keySet();
            return Sets.newHashSet(threads);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
