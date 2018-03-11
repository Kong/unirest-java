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


package io.github.openunirest.http;

import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import static java.lang.System.getProperty;
import static spark.Spark.*;

public class MockServer {
	private static final JacksonObjectMapper om = new JacksonObjectMapper();
    private static Object responseBody;
    public static final int PORT = 4567;
    public static final String HOST = "http://localhost:" + PORT;
    public static final String POST = HOST + "/post";
    public static final String GETJSON = HOST + "/get";
    public static final String DELETE = HOST + "/delete";


	public static void setJsonAsResponse(Object o){
		responseBody = om.writeValue(o);
	}

	public static void reset(){
		responseBody = null;
	}

	public static void start() {
		port(PORT);
		delete("/delete", MockServer::jsonResponse);
		post("/post", ContentType.APPLICATION_JSON.getMimeType(), MockServer::jsonResponse);
		post("/post", ContentType.MULTIPART_FORM_DATA.getMimeType(), multipost);
        get("/get", ContentType.APPLICATION_JSON.getMimeType(), MockServer::jsonResponse);
	}

    private static Object jsonResponse(Request req, Response res) {
		if(responseBody != null){
			return responseBody;
		}
        RequestCapture value = new RequestCapture(req);
		value.writeBody(req);
        return om.writeValue(value);
	}


	private static Route multipost = (req, res) -> {
        RequestCapture body = new RequestCapture(req);
		body.writeMultipart(req);
        return om.writeValue(body);
    };

	public static void shutdown() {
		Spark.stop();
	}
}
