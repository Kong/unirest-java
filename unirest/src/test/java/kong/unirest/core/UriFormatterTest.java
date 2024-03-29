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

package kong.unirest.core;


import kong.unirest.core.BaseRequest;
import kong.unirest.core.Config;
import kong.unirest.core.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UriFormatterTest {

    @Test
    public void testMangler_encoding() {
        assertLinkSurvives("http://localhost/test%2Fthis");
    }

    @Test
    public void testMangler_fragment() {
        assertLinkSurvives("http://localhost/test?a=b#fragment");
    }

    @Test
    public void basicBoringUri() {
        assertLinkSurvives("http://localhost/test?a=b");
    }

    @Test
    public void semicolonsAsParam() {
        assertLinkSurvives("http://localhost/test?a=b;foo=bar");
    }

    @Test
    public void utf8Chars(){
        assertLinkSurvives("http://localhost/test?foo=こんにちは");
    }

    private void assertLinkSurvives(String s) {
        assertEquals(s, new BaseRequest(new Config(), HttpMethod.GET, s){}.url.toString());
    }

}