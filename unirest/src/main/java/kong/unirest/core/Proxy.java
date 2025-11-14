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
 * represents basic proxy settings
 * When this is used to configure the client it sets the following:
 *    Proxy host and port are configured as a simple java.net.ProxySelector and set as the default selector
 *    Username and password are configured as a single java.net.Authenticator which will apply to ALL auth challenges (not just the proxy)
 */
public class Proxy {
    private final String host;
    private final Integer port;
    private final String username;
    private final String password;

    /**
     * Construct just based on host and port
     * @param host hostname
     * @param port the port
     */
    public Proxy(String host, Integer port){
        this(host, port, null, null);
    }

    /**
     * Construct with all the things
     * @param host hostname
     * @param port the port
     * @param username username for authentication
     * @param password password for authentication
     */
    public Proxy(String host, Integer port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @return the username for authentication
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return indicates that the username and password have been configured.
     */
    public boolean isAuthenticated() {
        return username != null && password != null;
    }
}
