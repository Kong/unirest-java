/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

/*
The MIT License

Copyright (c) 2013 OpenUnirest (http://github.com/OpenUnirest)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package BehaviorTests;

import spark.utils.IOUtils;
import unirest.JacksonObjectMapper;
import spark.Request;
import spark.Response;
import spark.Spark;
import unirest.TestUtil;

import javax.servlet.ServletOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static spark.Spark.*;

public class MockServer {

    private static final JacksonObjectMapper om = new JacksonObjectMapper();
	private static Object responseBody;
	public static final int PORT = 4567;
	public static final String HOST = "http://localhost:" + PORT;
	public static final String REDIRECT = HOST + "/redirect";
	public static final String BINARYFILE = HOST + "/binary";
	public static final String NOBODY = HOST + "/nobody";
	public static final String PROXY = "localhost:4567";
	public static final String POST = HOST + "/post";
	public static final String GET = HOST + "/get";
	public static final String DELETE = HOST + "/delete";
	public static final String GZIP = HOST + "/gzip";
	public static final String PATCH = HOST + "/patch";
	public static final String INVALID_REQUEST = HOST + "/invalid";
	public static final String PASSED_PATH_PARAM = GET + "/{param}/passed";
	public static final String ALTGET = "http://127.0.0.1:" + PORT + "/get";


	public static void setJsonAsResponse(Object o){
		responseBody = om.writeValue(o);
	}

	public static void reset(){
		responseBody = null;
	}

	static {
		port(PORT);
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
		get("/get/:p/passed", MockServer::jsonResponse);
		get("/proxy", MockServer::proxiedResponse);
		get("/binary", MockServer::file);
        Runtime.getRuntime().addShutdownHook(new Thread(Spark::stop));
		try {
			new CountDownLatch(1).await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		res.cookie("JSESSIONID", "ABC123");
		if(responseBody != null){
			return responseBody;
		}
		RequestCapture value = new RequestCapture(req);
		value.writeBody(req);
		value.setIsProxied(true);
		return om.writeValue(value);
	}

	private static Object jsonResponse(Request req, Response res) {
		res.cookie("JSESSIONID", "ABC123");
		if(responseBody != null){
			return responseBody;
		}
        RequestCapture value = new RequestCapture(req);
		value.writeBody(req);
        return om.writeValue(value);
	}

	public static void shutdown() {
		Spark.stop();
	}

	public static void setStringResponse(String stringResponse) {
		MockServer.responseBody = stringResponse;
	}
}
