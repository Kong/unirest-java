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

import com.google.common.base.Strings;
import io.javalin.Javalin;
import io.javalin.http.Context;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.TestUtil;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.UrlEncoded;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class MockServer {
    public static int timesCalled;
    private static int pages = 1;
    private static int onPage = 1;
    private static final List<Pair<String, String>> responseHeaders = new ArrayList<>();
    private static final List<Cookie> cookies = new ArrayList<>();

    private static final JacksonObjectMapper om = new JacksonObjectMapper();
    private static String responseBody;
    public static final int PORT = 4567;
    public static final String HOST = "http://localhost:" + PORT;
    public static final String WINDOWS_LATIN_1_FILE = HOST + "/data/cp1250.txt";
    public static final String REDIRECT = HOST + "/redirect";
    public static final String JAVALIN = HOST + "/sparkle/{spark}/yippy";
    public static final String BINARYFILE = HOST + "/binary";
    public static final String NOBODY = HOST + "/nobody";
    public static final String PAGED = HOST + "/paged";
    public static final String POST = HOST + "/post";
    public static final String GET = HOST + "/get";
    public static final String ERROR_RESPONSE = HOST + "/error";
    public static final String DELETE = HOST + "/delete";
    public static final String GZIP = HOST + "/gzip";
    public static final String EMPTY_GZIP = HOST + "/empty-gzip";
    public static final String PATCH = HOST + "/patch";
    public static final String INVALID_REQUEST = HOST + "/invalid";
    public static final String PASSED_PATH_PARAM = GET + "/{params}/passed";
    public static final String PASSED_PATH_PARAM_MULTI = PASSED_PATH_PARAM + "/{another}";
    public static final String CHEESE = HOST + "/cheese";
    public static final String ALTGET = "http://127.0.0.1:" + PORT + "/get";
    public static final String ECHO_RAW = HOST + "/raw";
    private static Javalin app;


    public static void setJsonAsResponse(Object o) {
        responseBody = om.writeValue(o);
    }

    public static void reset() {
        responseBody = null;
        responseHeaders.clear();
        cookies.clear();
        pages = 1;
        onPage = 1;
        timesCalled = 0;
    }

    static {
        app = Javalin.create(c -> {
            c.addStaticFiles("public/");

        }).start(PORT);
        app.error(404, MockServer::notFound);
        app.before(c -> timesCalled++);
        app.delete("/delete", MockServer::jsonResponse);
        app.get("/sparkle/:spark/yippy", MockServer::sparkle);
        app.post("/post", MockServer::jsonResponse);
        app.get("/get", MockServer::jsonResponse);
        app.get("/gzip", MockServer::gzipResponse);
        app.post("/empty-gzip", MockServer::emptyGzipResponse);
        app.get("/redirect", MockServer::redirect);
        app.patch("/patch", MockServer::jsonResponse);
        app.get("/invalid", MockServer::inValid);
        app.options("/get", MockServer::jsonResponse);
        app.get("/nobody", MockServer::nobody);
        app.head("/get", MockServer::jsonResponse);
        app.put("/post", MockServer::jsonResponse);
        app.get("/get/:params/passed", MockServer::jsonResponse);
        app.get("/get/:params/passed/:another", MockServer::jsonResponse);
        app.get("/proxy", MockServer::proxiedResponse);
        app.get("/binary", MockServer::file);
        app.get("/paged", MockServer::paged);
        app.post("/raw", MockServer::echo);
        app.get("/error", MockServer::error);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        try {
            new CountDownLatch(1).await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sparkle(Context request) {
        Map<String, String> sparks = new HashMap<>();
        sparks.put("contentType()", request.contentType());
        sparks.put("contextPath()", request.contextPath());
        sparks.put("host()", request.host());
        sparks.put("ip()", request.ip());
        sparks.put("pathInfo()", request.req.getPathInfo());
        sparks.put("port()", String.valueOf(request.port()));
        sparks.put("protocol()", request.protocol());
        sparks.put("scheme()", request.scheme());
        sparks.put("servletPath()", request.req.getServletPath());
        sparks.put("requestMethod()", request.method());
        sparks.put("uri()", request.req.getRequestURI());
        sparks.put("url()", request.url());
        sparks.put("userAgent()", request.userAgent());
        sparks.put("queryString()", request.queryString());
        request.result(om.writeValue(sparks));
    }

    private static void error(Context request) {
        request.status(400);
        request.result(Strings.nullToEmpty(responseBody));
    }

    private static void echo(Context request) {
        request.result(request.body());
    }

    private static void paged(Context context) {
        if (pages > onPage) {
            onPage++;
            context.header("nextPage", PAGED + "?page=" + onPage);
        }
        jsonResponse(context);
    }

    private static void notFound(Context context) {
        RequestCapture value = getRequestCapture(context);
        value.setStatus(404);
        context.status(404);
        context.result(om.writeValue(value));
    }

    private static Object file(Context context) throws Exception {
        File f = TestUtil.rezFile("/spidey.jpg");
        context.res.setContentType("application/octet-stream");
        context.res.setHeader("Content-Disposition", "attachment;filename=image.jpg");
        context.res.setHeader("Content-Length", String.valueOf(f.length()));
        context.status(200);
        final ServletOutputStream out = context.res.getOutputStream();
        final FileInputStream in = new FileInputStream(f);
        IOUtils.copy(in, out);
        out.close();
        in.close();
        return null;
    }

    private static void nobody(Context request) {
        request.status(200);
    }

    private static void redirect(Context request) {
        request.redirect("/get", 301);
    }

    private static void inValid(Context request) {
        request.status(400);
        request.result("You did something bad");
    }

    private static Object emptyGzipResponse(Context response) throws Exception {
        response.res.setHeader("Content-Encoding", "gzip");
        response.res.setContentType("application/json");
        response.res.setStatus(200);
        response.res.getOutputStream().close();
        return null;
    }

    private static void gzipResponse(Context context) {
        context.header("Content-Encoding", "gzip");
        jsonResponse(context, true);
    }

    private static void proxiedResponse(Context context) {
         simpleResponse(context)
                .orElseGet(() -> {
                    RequestCapture value = getRequestCapture(context);
                    value.setIsProxied(true);
                    return om.writeValue(value);
                });
    }

    private static Optional<String> simpleResponse(Context context) {
        cookies.forEach(context::cookie);
        responseHeaders.forEach(h -> context.res.addHeader(h.key, h.value));

        if (responseBody != null) {
            return Optional.of(responseBody);
        }
        return Optional.empty();
    }

    private static void jsonResponse(Context c){
        jsonResponse(c, false);
    }
    private static void jsonResponse(Context c, Boolean compress) {
         String content = simpleResponse(c)
                .orElseGet(() -> {
                    RequestCapture value = getRequestCapture(c);
                    return om.writeValue(value);
                });
         if(compress){
             c.result(zip(content));
         } else {
             c.res.setCharacterEncoding(StandardCharsets.UTF_8.name());
             c.result(content);
         }
    }

    private static byte[] zip(String content) {
        try {
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(content.getBytes("UTF-8"));
            gzip.close();

            return obj.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static RequestCapture getRequestCapture(Context context) {
        RequestCapture value = new RequestCapture(context);
        value.writeBody(context);
        return value;
    }

    public static void shutdown() {
        app.stop();
    }

    public static void setStringResponse(String stringResponse) {
        MockServer.responseBody = stringResponse;
    }

    public static void addResponseHeader(String key, String value) {
        responseHeaders.add(new Pair<>(key, value));
    }

    public static void expectedPages(int expected) {
        pages = expected;
    }

    public static void clearCookies() {
        cookies.clear();
    }

    public static void expectCookie(String name, String value) {
        cookies.add(new Cookie(name, UrlEncoded.encodeString(value)));
    }

    public static void expectCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public static void clearHeaders() {
        responseHeaders.clear();
    }
}
