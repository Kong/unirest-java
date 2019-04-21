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

import kong.unirest.Headers;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.utils.IOUtils;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.TestUtil;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class MockServer {
	private static int pages = 1;
	private static int onPage = 1;
	private static final List<Pair<String,String>> responseHeaders = new ArrayList<>();
	private static final List<Pair<String,String>> cookies = new ArrayList<>();

	private static final JacksonObjectMapper om = new JacksonObjectMapper();
	private static Object responseBody;
	public static final int PORT = 4567;
	public static final String HOST = "http://localhost:" + PORT;
	public static final String WINDOWS_LATIN_1_FILE = HOST + "data/cp1250.txt";
	public static final String REDIRECT = HOST + "/redirect";
	public static final String BINARYFILE = HOST + "/binary";
	public static final String NOBODY = HOST + "/nobody";
	public static final String PAGED = HOST + "/paged";
	public static final String PROXY = "localhost:4567";
	public static final String POST = HOST + "/post";
	public static final String GET = HOST + "/get";
	public static final String ERROR_RESPONSE = HOST + "/error";
	public static final String DELETE = HOST + "/delete";
	public static final String GZIP = HOST + "/gzip";
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
	}

	static {
		port(PORT);
		Spark.staticFileLocation("data");
		Spark.notFound(MockServer::notFound);
		delete("/delete", MockServer::jsonResponse);
		post("/post", MockServer::jsonResponse);
		get("/get", MockServer::jsonResponse);
		get("/gzip", MockServer::gzipResponse);
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

	private static Object error(Request request, Response response) {
		return Spark.halt(400, om.writeValue(new ErrorThing("boom!")));
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
		 File f = TestUtil.rezFile("/image.jpg");
		 response.raw().setContentType("application/octet-stream");
		 response.raw().setHeader("Content-Disposition", "attachment;filename=image.jpg");
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
		cookies.forEach(c -> res.cookie(c.key, c.value));
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

	public static void main(String[] args){
		
	}

	public static void expectCookie(String name, String value) {
		cookies.add(new Pair<>(name, value));
	}
}
