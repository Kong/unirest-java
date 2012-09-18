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

package com.mashape.client.test.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mashape.client.authentication.utils.AuthUtil;

public class AuthUtilTest {

	@Test
	public void testRequestToken() {
		assertEquals(60, AuthUtil.generateAuthenticationHeader("ciao", "marco").getValue().length());
		assertEquals(60, AuthUtil.generateAuthenticationHeader("ciao", "piipopopopopopopo").getValue().length());
	}
}
