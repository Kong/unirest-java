/*
 * 
 * Mashape Java Client library.
 * Copyright (C) 2011 Mashape, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * The author of this software is Mashape, Inc.
 * For any question or feedback please contact us at: support@mashape.com
 * 
 */

package com.mashape.client.http;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

class HttpPatchWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PATCH";
    public String getMethod() { return METHOD_NAME; }

    public HttpPatchWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }
    public HttpPatchWithBody(final URI uri) {
        super();
        setURI(uri);
    }
    public HttpPatchWithBody() { super(); }
}
