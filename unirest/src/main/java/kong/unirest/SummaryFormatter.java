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

package kong.unirest;

import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;

class SummaryFormatter implements Function<HttpRequest<?>, String> {

    @Override
    public String apply(HttpRequest<?> req) {
        StringJoiner sb = new StringJoiner(System.lineSeparator());
        sb.add(req.getHttpMethod().name() + " " + req.getUrl());
        req.getHeaders().all().forEach(h -> sb.add(h.getName() + "=" + h.getValue()));
        sb.add("===================================");
        addBody(req, sb);
        return sb.toString();
    }

    private void addBody(HttpRequest<?> req, StringJoiner sb) {
        req.getBody().ifPresent(b -> {
            if(b.isEntityBody()){
                sb.add(String.valueOf(b.uniPart().getValue()));
            } else if (b.isMultiPart()) {
                toMultiPartAproximation(b, sb);
            } else {
                Path path = new Path("/");
                b.multiParts().stream().filter(p -> !p.isFile()).forEach(p  -> {
                    path.queryString(p.getName(), p.getValue());
                });
                sb.add(path.getQueryString());
            }
        });
    }

    private String toMultiPartAproximation(Body b, StringJoiner sj) {
        b.multiParts().forEach(p -> {
            String partid = UUID.randomUUID().toString();
            sj.add("--"+partid);
            if(p.isFile()){
                sj.add(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", p.getName(), p.getFileName()));
                sj.add("Content-Type: application/octet-stream");
                sj.add("<BINARY DATA>");
            } else {
                sj.add("Content-Disposition: form-data; name:\""+p.getName()+"\"");
                sj.add(String.valueOf(p.getValue()));
            }
            sj.add("");
        });
        return sj.toString();
    }

    //--4-yp9f7bDX_Brgpd7oOwkUyZJPCHfBV_X
    //Content-Disposition: form-data; name="album"
    //
    //77
    //--4-yp9f7bDX_Brgpd7oOwkUyZJPCHfBV_X
    //Content-Disposition: form-data; name="band"
    //
    //Talking Heads
}
