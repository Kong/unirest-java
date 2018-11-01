package unirest;

import unirest.MockApacheResponse;
import org.junit.Test;
import unirest.HttpResponseImpl;
import unirest.TestUtil;

import static org.junit.Assert.assertEquals;
import static unirest.TestUtil.emptyInput;

public class HttpResponseTest {

    @Test
    public void canMapBody() {
        MockApacheResponse r = new MockApacheResponse();
        Integer v = new HttpResponseImpl<>(r, "42", emptyInput()).mapBody(Integer::valueOf);
        assertEquals(new Integer(42), v);
    }

    @Test
    public void canMapRawBody() {
        MockApacheResponse r = new MockApacheResponse();
        Integer v = new HttpResponseImpl<>(r, "42", TestUtil.toInputStream("42"))
                .mapRawBody(i -> Integer.valueOf(TestUtil.toString(i)));

        assertEquals(new Integer(42), v);
    }
}