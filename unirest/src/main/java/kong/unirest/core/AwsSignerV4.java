package kong.unirest.core;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AwsSignerV4 implements Signer {

    public static final String AWS_4_HMAC_SHA_256 = "AWS4-HMAC-SHA256";

    @Override
    public void sign(HttpRequest<?> request) {

    }

    public static class CanonicalRequest {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        private final HttpRequest request;
        private final URI url;
        private final MultiHashMap<String, String> signingHeaders;
        private final String accessKey;
        private final String secretKey;
        private final String region;
        private final String service;
        private final Clock clock;

        public CanonicalRequest(HttpRequest request){
            this(request, null, null, null, null, Clock.systemUTC());
        }

        public CanonicalRequest(HttpRequest request, String accessKey, String secretKey, String region, String service, Clock clock) {
            this.clock = clock;
            this.request = request;
            this.url = URI.create(request.getUrl());
            this.signingHeaders = processHeaders(request);
            this.accessKey = accessKey;
            this.secretKey = secretKey;
            this.region = region;
            this.service = service;
        }

        private MultiHashMap<String, String> processHeaders(HttpRequest request) {
            var headers = new MultiHashMap<String, String>();
            request.getHeaders().all().forEach(h -> {
                var key = h.getName().toLowerCase();
                headers.add(key, h.getValue().trim());
            });
            if(!headers.containsKey("host")){
                headers.add("host", url.getHost());
            }
            if(!headers.containsKey("x-amz-date")){
                headers.add("x-amz-date", getFullDateStamp());
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
            return URI.create(path).getPath();
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
                    .collect(Collectors.joining("\n")) + "\n";
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
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        }

        //"AWS4-HMAC-SHA256 Credential=YOUR_ACCESS_KEY/20250822/us-east-1/execute-api/aws4_request
        // , SignedHeaders=host;x-amz-date
        // , Signature=7caad2ddfd8355f9a7149eaa47793b00870f5fc1e1b64ddd010f6a84faa6be1f"
        public String getAuthHeader() {
            var sb = new StringBuilder(AWS_4_HMAC_SHA_256).append(" ");
            sb.append("Credential=").append(accessKey)
                    .append("/").append(getShortDateStamp())
                    .append("/").append(region)
                    .append("/").append(service)
                    .append("/").append("aws4_request")
                    .append(", ")
                    .append("SignedHeaders=").append(getSignedHeaders())
                    .append(", ")
                    .append("Signature=").append(sign());

            return sb.toString();
        }

        private String sign() {
            String amzDate = getFullDateStamp();
            String datestamp = getShortDateStamp();


//            if (AwsCredentials.getAwsSessionToken() != null && !AwsCredentials.getAwsSessionToken().isEmpty()) {
//                headers.putIfAbsent("x-amz-security-token", AwsCredentials.getAwsSessionToken());
//            }

            // Create the canonical request
            String canonicalRequest = toString();

            // Create the string to sign
            String credentialScope = datestamp + "/" + region + "/" + service + "/aws4_request";
            String stringToSign = AWS_4_HMAC_SHA_256 + "\n" + amzDate + "\n" + credentialScope + "\n" + calculateHash(canonicalRequest);

            // Calculate the signature
            byte[] signingKey = getSignatureKey(secretKey, datestamp, region, service);
            return calculateHmacHex(signingKey, stringToSign);
        }

        @Override
        public String toString() {
            String query = getCanonicalQueryString();
            String headers = getCanonicalHeaders();
            String signedHeaders = getSignedHeaders();

            String canonicalRequest = getHttpMethod()
                    + "\n" + getCanonicalUri()
                    + "\n" + query + "\n" +
                    headers + "\n" + signedHeaders + "\n" + getHashedPayload();
            return canonicalRequest;
        }

        private static String calculateHmacHex(byte[] key, String data) {
            byte[] hmac = hmacSHA256(key, data);
            return bytesToHex(hmac);
        }

        private static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) {
            byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
            byte[] kDate = hmacSHA256(kSecret, dateStamp);
            byte[] kRegion = hmacSHA256(kDate, regionName);
            byte[] kService = hmacSHA256(kRegion, serviceName);
            return hmacSHA256(kService, "aws4_request");
        }

        private static byte[] hmacSHA256(byte[] key, String data) {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(key, "HmacSHA256"));
                return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
            }
        }

        private String calculateHash(String data) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(hash);
            } catch (Exception e) {
                throw new RuntimeException("Failed to calculate SHA-256 hash", e);
            }
        }



        public String getFullDateStamp() {
            return clock.instant().atZone(clock.getZone()).format(FORMATTER);
            //return "20250214T131509Z";
        }

        private String getShortDateStamp(){
            return clock.instant().atZone(clock.getZone()).format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        }
    }
}