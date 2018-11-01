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

package unirest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class ResponseUtils {

    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

    /**
     * Parse out a charset from a content type header.
     *
     * @param contentType e.g. "text/html; charset=EUC-JP"
     * @return "EUC-JP", or null if not found. Charset is trimmed and uppercased.
     */
    public static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }

        Matcher m = CHARSET_PATTERN.matcher(contentType);
        if (m.find()) {
            return m.group(1).trim().toUpperCase();
        }
        return null;
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;
    }

    public static boolean isGzipped(Header contentEncoding) {
        if (contentEncoding != null) {
            String value = contentEncoding.getValue();
            if (value != null && "gzip".equals(value.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public static String getCharSet(HttpEntity responseEntity) {
        Header contentType = responseEntity.getContentType();
        if (contentType != null) {
            String responseCharset = getCharsetFromContentType(contentType.getValue());
            if (responseCharset != null && !responseCharset.trim().equals("")) {
                return responseCharset;
            }
        }
        return "UTF-8";
    }

    public static byte[] getRawBody(HttpEntity responseEntity) {
        try {
            InputStream is = responseEntity.getContent();
            if (isGzipped(responseEntity.getContentEncoding())) {
                is = new GZIPInputStream(responseEntity.getContent());
            }
            return getBytes(is);
        } catch (IOException e2) {
            throw new UnirestException(e2);
        } finally {
            EntityUtils.consumeQuietly(responseEntity);
        }
    }
}
