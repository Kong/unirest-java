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

import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static BehaviorTests.TestUtil.rezFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BodyLogSummaryTest extends BddTest {

    @Test
    void forSimpleGets() {
        var log = Unirest.get("http://somewhere/{magic}")
                .routeParam("magic", "beans")
                .queryString("fruit", "apple")
                .header("Accept", "image/raw")
                .toSummary()
                .asString();

        assertEquals("GET http://somewhere/beans?fruit=apple\n" +
                "Accept: image/raw\n" +
                "===================================", log);
    }

    @Test
    void forSimpleBodies() {
        var log = Unirest.post("http://somewhere/{magic}")
                .routeParam("magic", "beans")
                .queryString("fruit", "apple")
                .header("Accept", "image/raw")
                .body("this is the body")
                .toSummary()
                .asString();

        assertEquals("POST http://somewhere/beans?fruit=apple\n" +
                "Accept: image/raw\n" +
                "===================================\n" +
                "this is the body", log);
    }

    @Test
    void forJsonBodies() {
        var log = Unirest.post("http://somewhere/{magic}")
                .routeParam("magic", "beans")
                .queryString("fruit", "apple")
                .header("Accept", "image/raw")
                .body(new JSONObject().put("muppet","Gonzo"))
                .toSummary()
                .asString();

        assertEquals("POST http://somewhere/beans?fruit=apple\n" +
                "Accept: image/raw\n" +
                "===================================\n" +
                "{\"muppet\":\"Gonzo\"}", log);
    }

    @Test
    void forObjectBodies() {
        var log = Unirest.post("http://somewhere/{magic}")
                .routeParam("magic", "beans")
                .queryString("fruit", "apple")
                .header("Accept", "image/raw")
                .body(new Foo("zip"))
                .toSummary()
                .asString();

        assertEquals("POST http://somewhere/beans?fruit=apple\n" +
                "Accept: image/raw\n" +
                "===================================\n" +
                "{\"bar\":\"zip\"}", log);
    }

    @Test
    void simpleFormBody() {
        var log = Unirest.post("http://somewhere/{magic}")
                .routeParam("magic", "beans")
                .queryString("fruit", "apple")
                .header("Accept", "image/raw")
                .field("band", "Talking Heads")
                .field("album", "77")
                .toSummary()
                .asString();

        assertEquals("POST http://somewhere/beans?fruit=apple\n" +
                "Accept: image/raw\n" +
                "===================================\n" +
                "album=77&band=Talking+Heads", log);
    }

    @Test
    void multiPart() {
        var boundary = "ABC-123-BOUNDARY";
        var body = Unirest.post(MockServer.ECHO_RAW)
                .header("Accept", "image/raw")
                .field("band", "Talking Heads")
                .field("album", "77")
                .field("file", rezFile("/test.txt"))
                .boundary(boundary)
                .toSummary()
                .asString();

        assertThat(body).isEqualTo(
                "POST http://localhost:4567/raw\n" +
                        "Accept: image/raw\n" +
                        "Content-Type: multipart/form-data; boundary=ABC-123-BOUNDARY;charset=UTF-8\"\n" +
                        "===================================\n" +
                        "--ABC-123-BOUNDARY\n" +
                        "Content-Disposition: form-data; name:\"album\"\n" +
                        "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\n" +
                        "77\n" +
                        "\n" +
                        "--ABC-123-BOUNDARY\n" +
                        "Content-Disposition: form-data; name:\"band\"\n" +
                        "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\n" +
                        "Talking Heads\n" +
                        "\n" +
                        "--ABC-123-BOUNDARY\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\n" +
                        "Content-Type: application/octet-stream\n" +
                        "<BINARY DATA>\n"
        );

    }


}