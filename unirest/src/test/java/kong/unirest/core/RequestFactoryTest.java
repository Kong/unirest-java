package kong.unirest.core;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static kong.unirest.core.RequestFactoryTest.RequestAsserts.assertRequest;
import static kong.unirest.core.Util.tryCast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RequestFactoryTest {

    String url = "http://foo";
    String headerKey = "header-key";
    String headerValue = "header-value";
    String queryKey = "query-key";
    String queryValue = "query-value";
    String urlWithQuery = url + "?" + queryKey + "=" + queryValue;
    ProgressMonitor downloadMonitor = mock(ProgressMonitor.class);
    ProgressMonitor uploadMonitor = mock(ProgressMonitor.class);
    ObjectMapper om = mock(ObjectMapper.class);
    int timeout = 38393;

    @Test
    void copy_get() {
        var req = Unirest.get(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .downloadMonitor(downloadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestNoBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.GET, urlWithQuery)
                .downloadMonitorIs(downloadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_options() {
        var req = Unirest.options(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .downloadMonitor(downloadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestNoBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.OPTIONS, urlWithQuery)
                .downloadMonitorIs(downloadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_head() {
        var req = Unirest.head(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .downloadMonitor(downloadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestNoBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.HEAD, urlWithQuery)
                .downloadMonitorIs(downloadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_delete() {
        var req = Unirest.delete(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .charset(StandardCharsets.ISO_8859_1)
                .downloadMonitor(downloadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.DELETE, urlWithQuery)
                .downloadMonitorIs(downloadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_put_unibody() {
        var req = Unirest.put(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .charset(StandardCharsets.ISO_8859_1)
                .body("hi mom")
                .downloadMonitor(downloadMonitor)
                .uploadMonitor(uploadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestUniBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.PUT, urlWithQuery)
                .hasCharset(StandardCharsets.ISO_8859_1)
                .hasBody("hi mom")
                .downloadMonitorIs(downloadMonitor)
                .uploadMonitorIs(uploadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_patch() {
        var req = Unirest.patch(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .charset(StandardCharsets.ISO_8859_1)
                .body("hi mom")
                .downloadMonitor(downloadMonitor)
                .uploadMonitor(uploadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestUniBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.PATCH, urlWithQuery)
                .hasCharset(StandardCharsets.ISO_8859_1)
                .hasBody("hi mom")
                .downloadMonitorIs(downloadMonitor)
                .uploadMonitorIs(uploadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_json_patch() {
        var req = Unirest.jsonPatch(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .add("/foo/bar", "one")
                .downloadMonitor(downloadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestJsonPatch.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.PATCH, urlWithQuery)
                .hasPatch(JsonPatchOperation.add, "/foo/bar", "one")
                .hasCharset(StandardCharsets.UTF_8)
                .downloadMonitorIs(downloadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_post_unibody() {
        var req = Unirest.post(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .charset(StandardCharsets.ISO_8859_1)
                .body("hi mom")
                .downloadMonitor(downloadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestUniBody.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.POST, urlWithQuery)
                .hasCharset(StandardCharsets.ISO_8859_1)
                .hasBody("hi mom")
                .downloadMonitorIs(downloadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }

    @Test
    void copy_post_formBody() {
        var req = Unirest.post(url)
                .header(headerKey, headerValue)
                .queryString(queryKey, queryValue)
                .field("foo", "bar")
                .field("file", new File("./myfile.xml"))
                .boundary("my-boundary")
                .downloadMonitor(downloadMonitor)
                .uploadMonitor(uploadMonitor)
                .withObjectMapper(om)
                .connectTimeout(timeout);

        var copy = RequestFactory.copy(req);

        assertRequest(copy)
                .isInstanceOf(HttpRequestMultiPart.class)
                .hasHeader(headerKey, headerValue)
                .hasRoute(HttpMethod.POST, urlWithQuery)
                .hasField("foo", "bar")
                .hasBoundary("my-boundary")
                .downloadMonitorIs(downloadMonitor)
                .uploadMonitorIs(uploadMonitor)
                .objectMapperIs(om)
                .hasTimeout(timeout);
    }


    static class RequestAsserts extends AbstractAssert<RequestAsserts, HttpRequest> {

        public static RequestAsserts assertRequest(HttpRequest request) {
            return new RequestAsserts(request);
        }

        RequestAsserts(HttpRequest httpRequest) {
            super(httpRequest, RequestAsserts.class);
        }

        public RequestAsserts hasHeader(String headerKey, String headerValue) {
            assertTrue(actual.getHeaders().containsKey(headerKey), "Missing Header Key " + headerKey);
            assertEquals(headerValue, actual.getHeaders().getFirst(headerKey));
            return this;
        }

        public RequestAsserts hasRoute(HttpMethod get, String url) {
            assertEquals(get, actual.getHttpMethod());
            assertEquals(url, actual.getUrl());
            return this;
        }

        public RequestAsserts hasCharset(Charset expected) {
            Body o = getBody();
            assertEquals(expected, o.getCharset(), "Mismatched charset on body content");
            return this;
        }

        public RequestAsserts hasBody(String expected) {
            Body o = getBody();
            assertEquals(expected, o.uniPart().getValue());
            return this;
        }

        private Body getBody() {
            var b = tryAs(HttpRequest.class);
            Body o = (Body) b.getBody().get();
            return o;
        }

        private <T> T tryAs(Class<T> clss) {
            return tryCast(actual, clss)
                    .orElseThrow(() -> err("Could  not cast subject (%s) to (%s)", actual.getClass(), clss));
        }

        private AssertionError err(String mssg, Object... args) {
            return new AssertionError(String.format(mssg, args));
        }

        public RequestAsserts hasField(String key, String value) {
            var body = tryAs(HttpRequestMultiPart.class)
                    .getBody()
                    .orElseThrow(() -> new AssertionError("No body found!"));
            for (BodyPart part : body.multiParts()) {
                if (part.getName().equals(key) && part.getValue().equals(value)) {
                    return this;
                }
            }
            throw err("Cannot find field: %s: %s", key, value);
        }

        public RequestAsserts hasBoundary(String boundary) {
            assertEquals(boundary, tryAs(HttpRequestMultiPart.class).getBoundary(), "Wrong Boundary!");
            return this;
        }

        public RequestAsserts downloadMonitorIs(ProgressMonitor expected) {
            assertSame(expected, tryAs(BaseRequest.class).getDownloadMonitor());
            return this;
        }

        public RequestAsserts uploadMonitorIs(ProgressMonitor uploadMonitor) {
            assertSame(uploadMonitor, getUploadMonitor());
            return this;
        }

        private ProgressMonitor getUploadMonitor() {
            try {
                return tryAs(HttpRequestUniBody.class).getMonitor();
            } catch (AssertionError e) {
                return tryAs(HttpRequestMultiPart.class).getMonitor();
            }
        }

        public RequestAsserts objectMapperIs(ObjectMapper om) {
            assertSame(om, tryAs(BaseRequest.class).getObjectMapper());
            return this;
        }

        public RequestAsserts hasTimeout(int timeout) {
            assertEquals(timeout, actual.getConnectTimeout());
            return this;
        }

        public RequestAsserts hasPatch(JsonPatchOperation operation, String path, String value) {
            var items = tryAs(HttpRequestJsonPatch.class).getPatch().getOperations();
            assertThat(items).contains(new JsonPatchItem(operation, path, value));
            return this;
        }
    }
}