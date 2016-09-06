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

import org.codehaus.jettison.json.JSONException;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.client.Sprint;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class houses any globally-used utility methods re-used by aspects of
 * clients in this collector
 * 
 * @author KFK884
 * 
 */
public final class ClientUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientUtil.class);
	
	private static final ClientUtil INSTANCE = new ClientUtil();
	
	// not static because not thread safe
	private static final String SPRINT_SPLIT = "(?=,\\w+=)";

	/**
	 * Default constructor
	 */
	private ClientUtil() {

	}

	/**
	 * Utility method used to sanitize / canonicalize a String-based response
	 * artifact from a source system. This will return a valid UTF-8 strings, or
	 * a "" (blank) response for any of the following cases:
	 * "NULL";"Null";"null";null;""
	 * 
	 * @param inNativeRs
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
		if (nativeRs != null && !nativeRs.isEmpty()) {
			try {
				DateTime dt = ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(nativeRs);
				// add 0's at end for backwards compatability
				return ISODateTimeFormat.dateHourMinuteSecondMillis().print(dt) + "0000";
			} catch (IllegalArgumentException e) {
				LOGGER.error("Failed to parse date: " + nativeRs);
				LOGGER.debug("Exception", e);
			}
		}
		
		return "";
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

		if ((list != null) && !(list.isEmpty())) {
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				canonicalRs.add(this.sanitizeResponse(iterator.next()));
			}
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

		if ((canonicalDate != null) && !(canonicalDate.isEmpty())) {
			nativeDate = canonicalDate.replace("T", " ");
			try {
				nativeDate = nativeDate.substring(0, 16);
			} catch (StringIndexOutOfBoundsException e) {
				nativeDate = nativeDate.concat(" 00:00");
			}
		} else {
			nativeDate = "1900-01-01 00:00";
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
	public JSONObject toCanonicalSprintJSON(String nativeRs) {
		JSONObject canonicalRs = new JSONObject();
		CharSequence interrimChar;
		int start = 0;
		int end = 0;

		if ((nativeRs != null) && !(nativeRs.isEmpty())) {
			start = nativeRs.indexOf('[') + 1;
			end = nativeRs.length() - 1;
			StringBuffer interrimBuf = new StringBuffer(nativeRs);
			interrimChar = interrimBuf.subSequence(start, end);
			String interrimStr = interrimChar.toString();

			List<String> list = Arrays.asList(interrimStr.split(","));
			if ((list != null) && !(list.isEmpty())) {
				Iterator<String> listIt = list.iterator();
				while (listIt.hasNext()) {
					String temp = listIt.next();
					String[] keyValuePair = temp.split("=", 2);
					String key = keyValuePair[0];
					String value = "";
					
					if (keyValuePair.length > 1) {
						value = keyValuePair[1];
					}
					
					if ("<null>".equalsIgnoreCase(value)) {
						value = "";
					}
					canonicalRs.put(key, value);
				}
			}
		} else {
			canonicalRs.clear();
		}

		return canonicalRs;
	}

	/**
	 * Converts a Jira string representation of sprint artifacts into a POJO Map
	 * object, with string as keys.
	 * 
	 * @param nativeRs
	 *            a sanitized String representation of a sprint artifact link
	 *            from Jira
	 * @return A canonical Map of Jira sprint artifacts
	 */
	public Map<String, Object> toCanonicalSprintPOJO(String nativeRs) {
		JSONObject nativeSprint = this.toCanonicalSprintJSON(nativeRs.substring(1,
				nativeRs.length() - 2));
		Map<String, Object> canonicalSprint = new HashMap<String, Object>();

		if ((nativeSprint != null) && !(nativeSprint.isEmpty())) {
			@SuppressWarnings("unchecked")
			Set<String> keys = nativeSprint.keySet();
			Iterator<String> keysItr = keys.iterator();
			while (keysItr.hasNext()) {
				String key = keysItr.next();
				Object value = nativeSprint.get(key);

				if (value instanceof JSONArray) {
					try {
						value = this.toList((JSONArray) value);
					} catch (JSONException e) {
						value = new ArrayList<String>();
					}
				} else if (value instanceof JSONObject) {
					value = this.toCanonicalSprintPOJO(value.toString());
				}
				canonicalSprint.put(key, value);
			}
		}

		return canonicalSprint;
	}
	
	/**
	 * Parse a json array of raw sprint tostrings to Sprint objects
	 * 
	 * @param data
	 * @return a list of Sprints that were parsed if possible.
	 * @throws ParseException if a sprint could not be parsed
	 */
	public List<Sprint> parseSprints(Object data) throws ParseException {
		List<Sprint> sprints = new ArrayList<>();
		
		if (data instanceof JSONArray) {
			for (Object obj : (JSONArray)data) {
				String rawToString = obj != null? obj.toString() : null;
				
				Sprint sprint = parseSprint(rawToString);
				
				sprints.add(sprint);
			}
		} else if (data instanceof org.codehaus.jettison.json.JSONArray) {
			org.codehaus.jettison.json.JSONArray jsonA = (org.codehaus.jettison.json.JSONArray)data;
			for (int i = 0; i < jsonA.length(); ++i) {
				Object obj;
				try {
					obj = jsonA.get(i);
				} catch (JSONException e) {
					throw new RuntimeException("", e);
				}
				
				String rawToString = obj != null? obj.toString() : null;
				
				Sprint sprint = parseSprint(rawToString);
				
				sprints.add(sprint);
			}
		}
		
		return sprints;
	}
	
	@SuppressWarnings({ "PMD.NPathComplexity" })
	public Sprint parseSprint(String rawSprintToString) throws ParseException {
		Sprint sprint = new Sprint();
		
		if (rawSprintToString != null && rawSprintToString.matches(".*\\[.+\\][^\\]]*")) {
			String rawToString = rawSprintToString.substring(rawSprintToString.indexOf('[') + 1, rawSprintToString.length() - 1);
			String[] kvRaws = rawToString.split(SPRINT_SPLIT);
			
			for (String kvRaw : kvRaws) {
				int eqIdx = kvRaw.indexOf('=');
				
				// just in case logic changes above
				if (eqIdx > 0) {
					String key = kvRaw.charAt(0) == ','? kvRaw.substring(1, eqIdx) : kvRaw.substring(0, eqIdx);
					String valueAsStr = eqIdx == kvRaw.length() - 1? "" : kvRaw.substring(eqIdx + 1, kvRaw.length());
					
					if ("<null>".equalsIgnoreCase(valueAsStr)) {
						valueAsStr = "";
					}

					if ("id".equals(key)) {
						sprint.setId(Long.valueOf(valueAsStr));
					} else if ("state".equals(key)) {
						sprint.setState(valueAsStr);
					} else if ("name".equals(key)) {
						sprint.setName(valueAsStr);
					} else if ("startDate".equals(key)) {
						sprint.setStartDateStr(valueAsStr);
					} else if ("endDate".equals(key)) {
						sprint.setEndDateStr(valueAsStr);
					} else if ("completeDate".equals(key)) {
						sprint.setCompleteDateStr(valueAsStr);
					} else if ("sequence".equals(key)) {
						sprint.setSequence(Integer.valueOf(valueAsStr));
					}
				}
			}
		} else {
			throw new ParseException("Unparsable sprint: " + rawSprintToString, 0);
		}
		
		return sprint;
	}

	/**
	 * Converts JSONArray to list artifact
	 * 
	 * @param array
	 *            JSONArray artifact
	 * @return A List artifact representing JSONArray information
	 * @throws JSONException
	 */
	private List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = this.toCanonicalSprintPOJO(value.toString());
			}
			list.add(value);
		}
		return list;
	}

	/**
	 * Jira story estimate in minutes, converted to hours, rounded down: For
	 * Jira, 8 hours = 1 day; 5 days = 1 week
	 * 
	 * @param estimate
	 *            Minute representation of estimate content
	 * @return Hour representation of minutes, rounded down
	 */
	public String toHours(String estimate) {
		String nullLiteral = "null";
		String hours = "";
		long minutes = 0;
		if ((estimate != null) && !estimate.isEmpty() && !nullLiteral.equalsIgnoreCase(estimate)) {
			minutes = Long.valueOf(estimate);
			hours = this.sanitizeResponse(Integer.toString((int) (minutes / 60)));
		} else {
			hours = "0";
		}

		return hours;
	}
	
	public static ClientUtil getInstance() {
		return INSTANCE;
	}
}
