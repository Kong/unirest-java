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

import org.eclipse.jetty.util.UrlEncoded;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.utils.IOUtils;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.TestUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class MockServer {
    public static int timesCalled;
    private static int pages = 1;
	private static int onPage = 1;
	private static final List<Pair<String,String>> responseHeaders = new ArrayList<>();
	private static final List<Cookie> cookies = new ArrayList<>();

	private static final JacksonObjectMapper om = new JacksonObjectMapper();
	private static String responseBody;
	public static final int PORT = 4567;
	public static final String HOST = "http://localhost:" + PORT;
	public static final String WINDOWS_LATIN_1_FILE = HOST + "/data/cp1250.txt";
	public static final String REDIRECT = HOST + "/redirect";
	public static final String SPARKLE = HOST + "/sparkle/{spark}/yippy";
	public static final String BINARYFILE = HOST + "/binary";
	public static final String NOBODY = HOST + "/nobody";
	public static final String PAGED = HOST + "/paged";
	public static final String PROXY = "localhost:4567";
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


	public static void setJsonAsResponse(Object o){
		responseBody = om.writeValue(o);
	}

	public static void reset(){
		responseBody = null;
		responseHeaders.clear();
		cookies.clear();
		pages = 1;
		onPage = 1;
		timesCalled = 0;
	}

	static {
		Spark.staticFileLocation("public/");
		port(PORT);
		Spark.notFound(MockServer::notFound);
		Spark.before((p,f) -> timesCalled++);
		delete("/delete", MockServer::jsonResponse);
		get("/sparkle/:spark/yippy", MockServer::sparkle);
		post("/post", MockServer::jsonResponse);
		get("/get", MockServer::jsonResponse);
		get("/gzip", MockServer::gzipResponse);
		post("/empty-gzip", MockServer::emptyGzipResponse);
		get("/redirect", MockServer::redirect);
		patch("/patch", MockServer::jsonResponse);
		get("/invalid", MockServer::inValid);
		options("/get", MockServer::jsonResponse);
		get("/nobody", MockServer::nobody);
		head("/get", MockServer::jsonResponse);
		put("/post", MockServer::jsonResponse);
		get("/get/:params/passed", MockServer::jsonResponse);
		get("/get/:params/passed/:another", MockServer::jsonResponse);
		get("/proxy", MockServer::proxiedResponse);
		get("/binary", MockServer::file);
		get("/paged", MockServer::paged);
		post("/raw", MockServer::echo);
		get("/error", MockServer::error);
        Runtime.getRuntime().addShutdownHook(new Thread(Spark::stop));
		try {
			new CountDownLatch(1).await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Object sparkle(Request request, Response response) {
		Map<String, String> sparks = new HashMap<>();
		sparks.put("contentType()", request.contentType());
		sparks.put("contextPath()", request.contextPath());
		sparks.put("host()", request.host());
		sparks.put("ip()", request.ip());
		sparks.put("pathInfo()", request.pathInfo());
		sparks.put("port()", String.valueOf(request.port()));
		sparks.put("protocol()", request.protocol());
		sparks.put("scheme()", request.scheme());
		sparks.put("servletPath()", request.servletPath());
		sparks.put("requestMethod()", request.requestMethod());
		sparks.put("splat()", String.join(" | ", request.splat()));
		sparks.put("uri()", request.uri());
		sparks.put("url()", request.url());
		sparks.put("userAgent()", request.userAgent());
		sparks.put("queryString()", request.queryString());

		return om.writeValue(sparks);
	}

	private static Object error(Request request, Response response) {
		return Spark.halt(400, responseBody);
	}

	private static Object echo(Request request, Response response) {
		return request.body();
	}

	private static Object paged(Request request, Response response) {
		if(pages > onPage){
			onPage++;
			response.header("nextPage", PAGED + "?page=" + onPage);
		}
		return jsonResponse(request, response);
	}

	private static Object notFound(Request req, Response res) {
		RequestCapture value = getRequestCapture(req, res);
		value.setStatus(404);
		return om.writeValue(value);
	}

	private static Object file(Request request, Response response) throws Exception {
		 File f = TestUtil.rezFile("/spidey.jpg");
		 response.raw().setContentType("application/octet-stream");
		 response.raw().setHeader("Content-Disposition", "attachment;filename=image.jpg");
		 response.raw().setHeader("Content-Length", String.valueOf(f.length()));
		 response.status(200);
		 final ServletOutputStream out = response.raw().getOutputStream();
		 final FileInputStream in = new FileInputStream(f);
		 IOUtils.copy(in, out);
		 out.close();
		 in.close();
		 return null;
	}

	private static Object nobody(Request request, Response response) {
		Spark.halt(200);
		return null;
	}

	private static Object redirect(Request request, Response response) {
		response.redirect("/get", 301);
		return null;
	}

	private static Object inValid(Request request, Response response) {
		response.status(400);
		return "You did something bad";
	}

  private static Object emptyGzipResponse(Request request, Response response) throws Exception {
    response.raw().setHeader("Content-Encoding", "gzip");
    response.raw().setContentType("application/json");
    response.raw().setStatus(200);
    response.raw().getOutputStream().close();
    return null;
  }

	private static Object gzipResponse(Request request, Response response) {
		response.header("Content-Encoding", "gzip");
		return jsonResponse(request, response);
	}

	private static Object proxiedResponse(Request req, Response res) {
		return simpleResponse(req, res)
				.orElseGet(() -> {
					RequestCapture value = getRequestCapture(req, res);
					value.setIsProxied(true);
					return om.writeValue(value);
				});
	}

	private static Optional<Object> simpleResponse(Request req, Response res) {
		cookies.forEach(c -> res.raw().addCookie(c));
		responseHeaders.forEach(h -> res.header(h.key, h.value));

		if(responseBody != null) {
			return Optional.of(responseBody);
		}
		return Optional.empty();
	}

	private static Object jsonResponse(Request req, Response res) {
		return simpleResponse(req, res)
				.orElseGet(() -> {
					RequestCapture value = getRequestCapture(req, res);
					return om.writeValue(value);
				});
	}

	private static RequestCapture getRequestCapture(Request req, Response res) {
		RequestCapture value = new RequestCapture(req);
		value.writeBody(req);
		return value;
	}

	public static void shutdown() {
		Spark.stop();
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

	public static void expectCookie(String name, String value) {
		cookies.add(new Cookie(name, UrlEncoded.encodeString(value)));
	}

	public static void expectCookie(Cookie cookie) {
		cookies.add(cookie);
	}
}
