package kong.unirest.core;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AwsSignerV4 implements Signer {

    @Override
    public void sign(HttpRequest<?> request) {

    }

    public static class CanonicalRequest {
        private final HttpRequest request;
        private final URI url;
        private final MultiHashMap<String, String> signingHeaders;

        public CanonicalRequest(HttpRequest request){
            this.request = request;
            this.url = URI.create(request.getUrl());
            this.signingHeaders = processHeaders(request);
        }

        private MultiHashMap<String, String> processHeaders(HttpRequest request) {
            var headers = new MultiHashMap<String, String>();
            request.getHeaders().all().forEach(h -> {
                var key = h.getName().toLowerCase();
                if(key.startsWith("x-amz") || key.equals("host")) {
                    headers.add(key, h.getValue().trim());
                }
            });
            if(!headers.containsKey("host")){
                headers.add("host", url.getHost());
            }
            return headers;
        }

        /**
         * HTTPMethod is one of the HTTP methods, for example GET, PUT, HEAD, and DELETE.
         * example: GET
         *
         * @return header value as string
         */
        public String getHttpMethod() {
            return request.getHttpMethod().name();
        }

        /**
         * Is the URI-encoded version of the absolute path component
         * of the URI—everything starting with the "/" that follows
         * the domain name and up to the end of the string or to the
         * question mark character ('?') if you have query string parameters.
         *<p>
         * The URI in the following example, /examplebucket/myphoto.jpg,
         * is the absolute path and you don't encode the "/" in the absolute path:
         * example: http://s3.amazonaws.com/examplebucket/myphoto.jpg
         *
         * @return the uri as a string
         */
        public String getCanonicalUri() {
            String path = Util.isNullOrEmpty(url.getPath()) ? "/" : url.getPath();
            return URLEncoder.encode(url.getScheme() + "://" + url.getHost() + path, StandardCharsets.UTF_8);
        }

        /**
         * Specifies the URI-encoded query string parameters.
         * You URI-encode name and values individually.
         * You must also sort the parameters in the canonical query string
         * alphabetically by key name. The sorting occurs after encoding.
         * <p>
         * The query string in http://s3.amazonaws.com/examplebucket?prefix=somePrefix&marker=someMarker&max-keys=20
         * is prefix=somePrefix&marker=someMarker&max-keys=20:
         * <p>
         * The canonical query string is as follows (line breaks are added to this example for readability):
         *    UriEncode("marker")+"="+UriEncode("someMarker")+"&"+
         *    UriEncode("max-keys")+"="+UriEncode("20") + "&" +
         *    UriEncode("prefix")+"="+UriEncode("somePrefix")
         * <p>
         * When a request targets a subresource, the corresponding query
         * parameter value will be an empty string (""). For example,
         * the following URI identifies the ACL subresource on the examplebucket bucket:
         *   http://s3.amazonaws.com/examplebucket?acl
         * <p>
         * The CanonicalQueryString in this case is as follows:
         * <p>
         * UriEncode("acl") + "=" + ""
         * <p>
         * If the URI does not include a '?', there is no query string in the request,
         * and you set the canonical query string to an empty string ("").
         * You will still need to include the "\n".
         *
         * @return the query string
         */
        public String getCanonicalQueryString() {
            var uri = QueryParams.fromURI(request.getUrl());
            return uri.getQueryParams()
                    .stream()
                    .sorted(Comparator.comparing(QueryParams.NameValuePair::getName))
                    .map(nv -> nv.toString())
                    .collect(Collectors.joining("&"));
        }

        /**
         * CanonicalHeaders is a list of request headers with their values.
         * Individual header name and value pairs are separated by the newline character ("\n").
         * Header names must be in lowercase. You must sort the header names
         * alphabetically to construct the string, as shown in the following example:
         * <code>
         *      Lowercase(<HeaderName1>)+":"+Trim(<value>)+"\n"
         *      Lowercase(<HeaderName2>)+":"+Trim(<value>)+"\n"
         *       ...
         *      Lowercase(<HeaderNameN>)+":"+Trim(<value>)+"\n"
         * </code>
         *The Lowercase() and Trim() functions used in this example are described in the preceding section.
         *<p>
         * The CanonicalHeaders list must include the following:
         *      - HTTP host header.
         *      - If the Content-Type header is present in the request, you must add it to the CanonicalHeaders list.
         *      - Any x-amz-* headers that you plan to include in your request must also be added.
         *          For example, if you are using temporary security credentials,
         *          you need to include x-amz-security-token in your request.
         *          You must add this header in the list of CanonicalHeaders.
         * <p>
         * NOTE: The x-amz-content-sha256 header is required for all AWS Signature Version 4 requests.
         * It provides a hash of the request payload.
         * If there is no payload, you must provide the hash of an empty string.
         *<p>
         * The following is an example CanonicalHeaders string. The header names are in lowercase and sorted.
         * <code>
         *     host:s3.amazonaws.com
         *     x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
         *     x-amz-date:20130708T220855Z
         * </code>
         *
         * NOTE:
         * For the purpose of calculating an authorization signature, only the host
         * and any x-amz-* headers are required; however, in order to prevent data tampering,
         * you should consider including all the headers in the signature calculation.
         *
         * @return the header string
         */
        public String getCanonicalHeaders() {
            return signingHeaders.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(this::renderHeader)
                    .collect(Collectors.joining("\n"));
        }

        /**
         * SignedHeaders is an alphabetically sorted, semicolon-separated list
         * of lowercase request header names. The request headers in the list
         * are the same headers that you included in the CanonicalHeaders string.
         * <p>
         * For example, for the previous example, the value of SignedHeaders
         * would be as follows: host;x-amz-content-sha256;x-amz-date
         *
         * @return the signed headers
         */
        public String getSignedHeaders() {
            return signingHeaders.keySet().stream().sorted().collect(Collectors.joining(";"));
        }

        private String renderHeader(Map.Entry<String, Set<String>> header) {
            return header.getKey() + ":" + header
                    .getValue()
                    .stream()
                    .map(v -> Util.nullToEmpty(v).trim())
                    .sorted()
                    .collect(Collectors.joining(","));
        }

        /**
         * HashedPayload is the hexadecimal value of the SHA256 hash
         * of the request payload.
         *<code>Hex(SHA256Hash({payload})</code>
         *<p>
         * If there is no payload in the request, you compute a hash
         * of the empty string as follows:
         * <code>>Hex(SHA256Hash(""))</code>
         * The hash returns the following value:
         *     e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
         * <p>
         * For example, when you upload an object by using a
         * PUT request, you provide object data in the body.
         * When you retrieve an object by using a GET request,
         * you compute the empty string hash.
         *
         * @return the hashed payload
         */
        public String getHashedPayload() {
            return sha256((String)request.getBody().orElse(""));
        }

        private String sha256(String o){
            try {
                var digest = MessageDigest.getInstance("SHA-256");
                var encodedhash = digest.digest(
                        o.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(encodedhash);
            }catch (NoSuchAlgorithmException e){
                throw new UnirestException(e);
            }
        }

        private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
        private static String bytesToHex(byte[] bytes) {
            byte[] hexChars = new byte[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars, StandardCharsets.UTF_8);
        }
    }
}