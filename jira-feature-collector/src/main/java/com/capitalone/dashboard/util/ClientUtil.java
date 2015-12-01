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

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
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
@SuppressWarnings("PMD.AvoidCatchingNPE") // agreed..fixme
public class ClientUtil {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientUtil.class);

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
	public String sanitizeResponse(Object inNativeRs) {
		if (inNativeRs == null) {
			return "";
		}
		String nativeRs = inNativeRs.toString();

		byte[] utf8Bytes;
		CharsetDecoder cs = StandardCharsets.UTF_8.newDecoder();
		try {
			if ("null".equalsIgnoreCase(nativeRs)) {
				return "";
			}
			if (nativeRs.isEmpty()) {
				return "";
			}
			utf8Bytes = nativeRs.getBytes(StandardCharsets.UTF_8);
			cs.decode(ByteBuffer.wrap(utf8Bytes));
			return new String(utf8Bytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return "[INVALID NON UTF-8 ENCODING]";
		}
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
		String canonicalRs = "";
		int maxIndex = 23;

		try {
			StringBuilder interrimRs = new StringBuilder(nativeRs);
			if (interrimRs.length() > 0) {
				canonicalRs = interrimRs.substring(0, maxIndex);
				canonicalRs = canonicalRs.concat("0000");
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// nothing, fall through.
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
		List<String> canonicalRs = new ArrayList<>();

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
		String nativeDate = "";
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
		CharSequence interrimChar;
		int start = 0;
		int end = 0;

		try {
			start = nativeRs.indexOf('[') + 1;
			end = nativeRs.length() - 1;
			StringBuffer interrimBuf = new StringBuffer(nativeRs);
			interrimChar = interrimBuf.subSequence(start, end);
			String interrimStr = interrimChar.toString();

			List<String> list = Arrays.asList(interrimStr.split(","));
			Iterator<String> listIt = list.iterator();
			while (listIt.hasNext()) {
				String temp = listIt.next();
				List<String> keyValuePair = Arrays.asList(temp.split("=", 2));
				String key = keyValuePair.get(0).toString();
				String value = keyValuePair.get(1).toString();
				if ("<null>".equalsIgnoreCase(value)) {
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
