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

/**
 * Constants for standard HTTP status codes as defined by IANA.
 * <p>
 * This class provides named constants for HTTP response status codes,
 * organized into the following categories:
 * </p>
 * <ul>
 *   <li><b>2xx Success</b> - The request was successfully received, understood, and accepted</li>
 *   <li><b>3xx Redirection</b> - Further action needs to be taken to complete the request</li>
 *   <li><b>4xx Client Error</b> - The request contains bad syntax or cannot be fulfilled</li>
 *   <li><b>5xx Server Error</b> - The server failed to fulfill a valid request</li>
 * </ul>
 *
 * @see <a href="https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml">IANA HTTP Status Code Registry</a>
 * @see <a href="https://tools.ietf.org/html/rfc7231">RFC 7231 - HTTP/1.1 Semantics and Content</a>
 */
public final class HttpStatus {

    // 2xx Success

    /** {@code 200 OK} - The request has succeeded. */
    public static final int OK = 200;

    /** {@code 201 Created} - The request has been fulfilled and a new resource has been created. */
    public static final int CREATED = 201;

    /** {@code 202 Accepted} - The request has been accepted for processing, but processing has not been completed. */
    public static final int ACCEPTED = 202;

    /** {@code 203 Non-Authoritative Information} - The returned metadata is not exactly the same as available from the origin server. */
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;

    /** {@code 204 No Content} - The server has fulfilled the request but does not need to return a response body. */
    public static final int NO_CONTENT = 204;

    /** {@code 205 Reset Content} - The server has fulfilled the request and the user agent should reset the document view. */
    public static final int RESET_CONTENT = 205;

    /** {@code 206 Partial Content} - The server has fulfilled the partial GET request for the resource. */
    public static final int PARTIAL_CONTENT = 206;

    /** {@code 207 Multi-Status} - Provides status for multiple independent operations (WebDAV). */
    public static final int MULTI_STATUS = 207;

    /** {@code 208 Already Reported} - Used inside a DAV:propstat response element to avoid enumerating the internal members of multiple bindings (WebDAV). */
    public static final int ALREADY_REPORTED = 208;

    /** {@code 226 IM Used} - The server has fulfilled a GET request and the response is a representation of the result of one or more instance-manipulations. */
    public static final int IM_USED = 226;

    // 3xx Redirection

    /** {@code 300 Multiple Choices} - The target resource has more than one representation available. */
    public static final int MULTIPLE_CHOICE = 300;

    /** {@code 301 Moved Permanently} - The target resource has been assigned a new permanent URI. */
    public static final int MOVED_PERMANENTLY = 301;

    /** {@code 302 Found} - The target resource resides temporarily under a different URI. */
    public static final int FOUND = 302;

    /** {@code 303 See Other} - The server is redirecting the user agent to a different resource. */
    public static final int SEE_OTHER = 303;

    /** {@code 304 Not Modified} - The resource has not been modified since the version specified by the request headers. */
    public static final int NOT_MODIFIED = 304;

    /** {@code 305 Use Proxy} - The requested resource must be accessed through the proxy given by the Location field. Deprecated. */
    public static final int USE_PROXY = 305;

    /** {@code 306 (Unused)} - This status code is no longer used but is reserved. */
    public static final int UNUSED = 306;

    /** {@code 307 Temporary Redirect} - The target resource resides temporarily under a different URI; the user agent must not change the request method. */
    public static final int TEMPORARY_REDIRECT = 307;

    /** {@code 308 Permanent Redirect} - The target resource has been assigned a new permanent URI; the user agent must not change the request method. */
    public static final int PERMANENT_REDIRECT = 308;

    // 4xx Client Error

    /** {@code 400 Bad Request} - The server cannot process the request due to client error. */
    public static final int BAD_REQUEST = 400;

    /** {@code 401 Unauthorized} - The request requires user authentication. */
    public static final int UNAUTHORIZED = 401;

    /** {@code 402 Payment Required} - Reserved for future use. */
    public static final int PAYMENT_REQUIRED = 402;

    /** {@code 403 Forbidden} - The server understood the request but refuses to authorize it. */
    public static final int FORBIDDEN = 403;

    /** {@code 404 Not Found} - The server has not found anything matching the requested URI. */
    public static final int NOT_FOUND = 404;

    /** {@code 405 Method Not Allowed} - The method specified in the request is not allowed for the resource. */
    public static final int METHOD_NOT_ALLOWED = 405;

    /** {@code 406 Not Acceptable} - The resource is not available in a format acceptable to the request. */
    public static final int NOT_ACCEPTABLE = 406;

    /** {@code 407 Proxy Authentication Required} - The client must first authenticate itself with the proxy. */
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;

    /** {@code 408 Request Timeout} - The server timed out waiting for the request. */
    public static final int REQUEST_TIMEOUT = 408;

    /** {@code 409 Conflict} - The request could not be completed due to a conflict with the current state of the resource. */
    public static final int CONFLICT = 409;

    /** {@code 410 Gone} - The requested resource is no longer available and no forwarding address is known. */
    public static final int GONE = 410;

    /** {@code 411 Length Required} - The server requires a Content-Length header in the request. */
    public static final int LENGTH_REQUIRED = 411;

    /** {@code 412 Precondition Failed} - One or more preconditions given in the request header fields evaluated to false. */
    public static final int PRECONDITION_FAILED = 412;

    /** {@code 413 Payload Too Large} - The request entity is larger than the server is willing or able to process. */
    public static final int PAYLOAD_TOO_LARGE = 413;

    /** {@code 414 URI Too Long} - The request URI is longer than the server is willing to interpret. */
    public static final int URI_TOO_LONG = 414;

    /** {@code 415 Unsupported Media Type} - The server does not support the media type of the request entity. */
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;

    /** {@code 416 Range Not Satisfiable} - The range specified in the Range header cannot be fulfilled. */
    public static final int RANGE_NOT_SATISFIABLE = 416;

    /** {@code 417 Expectation Failed} - The expectation given in the Expect header could not be met by the server. */
    public static final int EXPECTATION_FAILED = 417;

    /** {@code 418 I'm a teapot} - The server refuses to brew coffee because it is, permanently, a teapot (RFC 2324). */
    public static final int IM_A_TEAPOT = 418;

    /** {@code 421 Misdirected Request} - The request was directed at a server that is not able to produce a response. */
    public static final int MISDIRECTED_REQUEST = 421;

    /** {@code 422 Unprocessable Entity} - The server understands the content type but was unable to process the contained instructions (WebDAV). */
    public static final int UNPROCESSABLE_ENTITY = 422;

    /** {@code 423 Locked} - The resource that is being accessed is locked (WebDAV). */
    public static final int LOCKED = 423;

    /** {@code 424 Failed Dependency} - The request failed due to failure of a previous request (WebDAV). */
    public static final int FAILED_DEPENDENCY = 424;

    /** {@code 425 Too Early} - The server is unwilling to risk processing a request that might be replayed. */
    public static final int TOO_EARLY = 425;

    /** {@code 426 Upgrade Required} - The server refuses to perform the request using the current protocol but might after upgrade. */
    public static final int UPGRADE_REQUIRED = 426;

    /** {@code 428 Precondition Required} - The origin server requires the request to be conditional. */
    public static final int PRECONDITION_REQUIRED = 428;

    /** {@code 429 Too Many Requests} - The user has sent too many requests in a given amount of time (rate limiting). */
    public static final int TOO_MANY_REQUESTS = 429;

    /** {@code 431 Request Header Fields Too Large} - The server is unwilling to process the request because its header fields are too large. */
    public static final int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;

    /** {@code 451 Unavailable For Legal Reasons} - The server is denying access to the resource as a consequence of a legal demand. */
    public static final int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

    // 5xx Server Error

    /** {@code 500 Internal Server Error} - The server encountered an unexpected condition that prevented it from fulfilling the request. */
    public static final int INTERNAL_SERVER_ERROR = 500;

    /** {@code 501 Not Implemented} - The server does not support the functionality required to fulfill the request. */
    public static final int NOT_IMPLEMENTED = 501;

    /** {@code 502 Bad Gateway} - The server, acting as a gateway or proxy, received an invalid response from an upstream server. */
    public static final int BAD_GATEWAY = 502;

    /** {@code 503 Service Unavailable} - The server is currently unable to handle the request due to temporary overloading or maintenance. */
    public static final int SERVICE_UNAVAILABLE = 503;

    /** {@code 504 Gateway Timeout} - The server, acting as a gateway or proxy, did not receive a timely response from an upstream server. */
    public static final int GATEWAY_TIMEOUT = 504;

    /** {@code 505 HTTP Version Not Supported} - The server does not support the HTTP protocol version used in the request. */
    public static final int VERSION_NOT_SUPPORTED = 505;

    /** {@code 506 Variant Also Negotiates} - The server has an internal configuration error: transparent content negotiation resulted in a circular reference. */
    public static final int VARIANT_ALSO_NEGOTIATES = 506;

    /** {@code 507 Insufficient Storage} - The server is unable to store the representation needed to complete the request (WebDAV). */
    public static final int INSUFFICIENT_STORAGE = 507;

    /** {@code 508 Loop Detected} - The server detected an infinite loop while processing the request (WebDAV). */
    public static final int LOOP_DETECTED = 508;

    /** {@code 510 Not Extended} - Further extensions to the request are required for the server to fulfill it. */
    public static final int NOT_EXTENDED = 510;

    /** {@code 511 Network Authentication Required} - The client needs to authenticate to gain network access. */
    public static final int NETWORK_AUTHENTICATION_REQUIRED = 511;
}
