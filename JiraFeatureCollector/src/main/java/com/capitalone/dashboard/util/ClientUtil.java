/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This class houses any globally-used utility methods re-used by aspects of
 * clients in this collector
 *
 * @author KFK884
 *
 */
public class ClientUtil {
	@SuppressWarnings("unused")
	private static Log LOGGER = LogFactory.getLog(ClientUtil.class);

	/**
	 * Default constructor
	 */
	public ClientUtil() {

	}

	/**
	 * Utility method used to sanitize / canonicalize a String-based response
	 * artifact from a source system. This will return a valid UTF-8 strings, or
	 * a "" (blank) response for any of the following cases:
	 * "NULL";"Null";"null";null;""
	 *
	 * @param nativeRs
	 *            The string response artifact retrieved from the source system
	 *            to be sanitized
	 * @return A UTF-8 sanitized response
	 */
	public String sanitizeResponse(String nativeRs) {
		boolean isNull = false;
		byte[] utf8Bytes;
		CharsetDecoder cs = Charset.forName("UTF-8").newDecoder();
		String canonicalRs = new String();
		try {
			isNull = nativeRs.equalsIgnoreCase("null");
			if (isNull) {
				return "";
			}
			isNull = nativeRs.isEmpty();
			if (isNull) {
				return "";
			}
			utf8Bytes = nativeRs.getBytes("UTF-8");
			cs.decode(ByteBuffer.wrap(utf8Bytes));
			canonicalRs = new String(utf8Bytes, StandardCharsets.UTF_8);
		} catch (NullPointerException npe) {
			return "";
		} catch (Exception e) {
			return "[INVALID NON UTF-8 ENCODING]";
		}

		return canonicalRs;
	}

	/**
	 * Utility method used to sanitize / canonicalize a String-based response
	 * artifact from a source system. This will return a valid UTF-8 strings, or
	 * a "" (blank) response for any of the following cases:
	 * "NULL";"Null";"null";null;""
	 *
	 * @param nativeRs
	 *            The string response artifact retrieved from the source system
	 *            to be sanitized
	 * @return A UTF-8 sanitized response
	 */
	public String sanitizeResponse(Object nativeRs) {
		boolean isNull = false;
		byte[] utf8Bytes;
		CharsetDecoder cs = Charset.forName("UTF-8").newDecoder();
		String canonicalRs = new String();
		try {
			isNull = nativeRs.toString().equalsIgnoreCase("null");
			if (isNull) {
				return "";
			}
			isNull = nativeRs.toString().isEmpty();
			if (isNull) {
				return "";
			}
			utf8Bytes = nativeRs.toString().getBytes("UTF-8");
			cs.decode(ByteBuffer.wrap(utf8Bytes));
			canonicalRs = new String(utf8Bytes, StandardCharsets.UTF_8);
		} catch (NullPointerException npe) {
			return "";
		} catch (Exception e) {
			return "[INVALID NON UTF-8 ENCODING]";
		}

		return canonicalRs;
	}

	/**
	 * Canonicalizes date format returned from source system. Some source
	 * systems have incorrectly formatted dates, or date times stamps that are
	 * not database friendly.
	 *
	 * @param nativeRs
	 *            Native date format as a string
	 * @return A stringified canonical date format
	 */
	public String toCanonicalDate(String nativeRs) {
		String canonicalRs = new String();
		StringBuilder interrimRs = new StringBuilder();
		int maxIndex = 23;

		try {
			interrimRs = new StringBuilder(nativeRs);
			if (interrimRs.length() > 0) {
				canonicalRs = interrimRs.substring(0, maxIndex);
				canonicalRs = canonicalRs.concat("0000");
			} else {
				canonicalRs = "";
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			canonicalRs = "";
		}

		return canonicalRs;
	}

	/**
	 * Canonicalizes a given JSONArray to a basic List object to avoid the use
	 * of JSON parsers.
	 *
	 * @param list
	 *            A given JSONArray object response from the source system
	 * @return The sanitized, canonical List<String>
	 */
	public List<String> toCanonicalList(List<String> list) {
		List<String> canonicalRs = new ArrayList<String>();

		try {
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				canonicalRs.add(this.sanitizeResponse(iterator.next()));
			}
		} catch (NullPointerException e) {
			return canonicalRs;
		}

		return canonicalRs;
	}

	/**
	 * Converts a given ISO formatted date string representation used by the
	 * local MongoDB instance into a string date representation used by the
	 * source system. This can be used to convert dates found in MongoDB into
	 * source system syntax for querying the source system based on local data
	 *
	 * @param canonicalDate
	 *            A string representation of an ISO format used by the local
	 *            MongoDB instance
	 * @return A nativized date string that can be consumed by a source system
	 */
	public String toNativeDate(String canonicalDate) {
		String nativeDate = new String();
		try {
			nativeDate = canonicalDate.replace("T", "%20");
			try {
				nativeDate = nativeDate.substring(0, 18);
			} catch (StringIndexOutOfBoundsException e) {
				nativeDate = nativeDate.concat("%2000:00");
			}
		} catch (NullPointerException e) {
			nativeDate = "1900-01-01%2000:00";
		}

		return nativeDate;
	}

	/**
	 * Converts a Jira string representation of sprint artifacts into a
	 * canonical JSONArray format.
	 *
	 * @param nativeRs
	 *            a sanitized String representation of a sprint artifact link
	 *            from Jira
	 * @return A canonical JSONArray of Jira sprint artifacts
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toCanonicalSprint(String nativeRs) {
		JSONObject canonicalRs = new JSONObject();
		String interrimStr = new String();
		String temp = new String();
		StringBuffer interrimBuf = new StringBuffer();
		CharSequence interrimChar;
		int start = 0;
		int end = 0;

		try {
			start = nativeRs.indexOf('[') + 1;
			end = nativeRs.length() - 1;
			interrimBuf = new StringBuffer(nativeRs);
			interrimChar = interrimBuf.subSequence(start, end);
			interrimStr = interrimChar.toString();

			List<String> list = Arrays.asList(interrimStr.split(","));
			Iterator<String> listIt = list.iterator();
			while (listIt.hasNext()) {
				String key = new String();
				String value = new String();
				temp = listIt.next();
				List<String> keyValuePair = Arrays.asList(temp.split("=", 2));
				key = keyValuePair.get(0).toString();
				value = keyValuePair.get(1).toString();
				if (value.equalsIgnoreCase("<null>")) {
					value = "";
				}
				canonicalRs.put(key, value);
			}
		} catch (NullPointerException | StringIndexOutOfBoundsException
				| ArrayIndexOutOfBoundsException e) {
			canonicalRs.clear();
		}

		return canonicalRs;
	}
}
