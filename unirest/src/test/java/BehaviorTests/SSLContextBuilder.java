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


import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


public class SSLContextBuilder {

    private final Set<KeyManager> keyManagers;

    public static SSLContextBuilder create() {
        return new SSLContextBuilder();
    }

    public SSLContextBuilder() {
        super();
        this.keyManagers = new LinkedHashSet<>();
    }

    public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword)
            throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {

        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword);
        final KeyManager[] kms = kmfactory.getKeyManagers();
        if (kms != null) {
            for (final KeyManager km : kms) {
                keyManagers.add(km);
            }
        }
        return this;
    }


    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        KeyManager[] km = !((Collection<KeyManager>) keyManagers).isEmpty() ? ((Collection<KeyManager>) keyManagers).toArray(new KeyManager[((Collection<KeyManager>) keyManagers).size()]) : null;
        sslContext.init(
                km,
                null,
                null);
        return sslContext;
    }
}