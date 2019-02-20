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

import kong.unirest.GsonObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.GenericType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class AsGenericTypeTest extends BddTest {

    private final List<Foo> foos = Arrays.asList(
            new Foo("foo"),
            new Foo("bar"),
            new Foo("baz")
    );

    @Test
    public void canGetAListOfObjects() {
        MockServer.setJsonAsResponse(foos);

        List<Foo> foos = Unirest.get(MockServer.GET)
                                .asObject(new GenericType<List<Foo>>(){})
                                .getBody();

        assertTheFoos(foos);
    }

    @Test
    public void canGetAListOfObjectsAsync() throws ExecutionException, InterruptedException {
        MockServer.setJsonAsResponse(foos);

        List<Foo> foos = Unirest.get(MockServer.GET)
                .asObjectAsync(new GenericType<List<Foo>>(){})
                .get()
                .getBody();

        assertTheFoos(foos);
    }

    @Test
    public void canGetAListOfObjectsAsyncWithCallback()  {
        MockServer.setJsonAsResponse(foos);

        Unirest.get(MockServer.GET)
                .asObjectAsync(new GenericType<List<Foo>>(){},
                r -> {
                    List<Foo> f = r.getBody();
                    assertTheFoos(f);
                    asyncSuccess();
                });

        assertAsync();
    }

    @Test
    public void soManyLayersOfGenerics() {
        MockServer.setJsonAsResponse(new WeirdType<>(foos, "hey"));

        WeirdType<List<Foo>> foos = Unirest.get(MockServer.GET)
                .asObject(new GenericType<WeirdType<List<Foo>>>(){})
                .getBody();

        List<Foo> someTees = foos.getSomeTees();
        assertTheFoos(someTees);
    }

    @Test
    public void itAlsoWorksWithGson() {
        Unirest.config().setObjectMapper(new GsonObjectMapper());

        MockServer.setJsonAsResponse(new WeirdType<>(foos, "hey"));

        WeirdType<List<Foo>> foos = Unirest.get(MockServer.GET)
                .asObject(new GenericType<WeirdType<List<Foo>>>(){})
                .getBody();

        List<Foo> someTees = foos.getSomeTees();
        assertTheFoos(someTees);
    }

    private void assertTheFoos(List<Foo> someTees) {
        assertEquals(3, someTees.size());
        assertEquals("foo", someTees.get(0).bar);
        assertEquals("bar", someTees.get(1).bar);
        assertEquals("baz", someTees.get(2).bar);
    }

    public static class WeirdType<T> {

        private T someTees;
        private String words;

        public WeirdType(){}

        public WeirdType(T someTees, String words){
            this.someTees = someTees;
            this.words = words;
        }

        public T getSomeTees() {
            return someTees;
        }
    }
}
