package BehaviorTests;

import io.github.openunirest.http.GsonObjectMapper;
import io.github.openunirest.http.Unirest;
import io.github.openunirest.request.GenericType;
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
        Unirest.setObjectMapper(new GsonObjectMapper());

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
