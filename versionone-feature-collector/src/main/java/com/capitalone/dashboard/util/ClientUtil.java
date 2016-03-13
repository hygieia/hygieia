/*************************
 * DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************DA-BOARD-LICENSE-END
 *********************************/

package com.capitalone.dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class houses any globally-used utility methods re-used by aspects of
 * clients in this collector
 *
 * @author KFK884
 */
public class ClientUtil {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUtil.class);

    /**
     * Utility method used to sanitize / canonicalize a String-based response
     * artifact from a source system. This will return a valid UTF-8 strings, or
     * a "" (blank) response for any of the following cases:
     * "NULL";"Null";"null";null;""
     *
     * @param nativeRs The string response artifact retrieved from the source system
     *                 to be sanitized
     * @return A UTF-8 sanitized response
     */
    public static String sanitizeResponse(String nativeRs) {
        if (StringUtils.isEmpty(nativeRs)) return "";

        byte[] utf8Bytes;
        CharsetDecoder cs = StandardCharsets.UTF_8.newDecoder();

        if ("null".equalsIgnoreCase(nativeRs)) return "";
        utf8Bytes = nativeRs.getBytes(StandardCharsets.UTF_8);
        try {
            cs.decode(ByteBuffer.wrap(utf8Bytes));
            return new String(utf8Bytes, StandardCharsets.UTF_8);
        } catch (CharacterCodingException e) {
            return "[INVALID NON UTF-8 ENCODING]";
        }
    }

    /**
     * Canonicalizes date format returned from source system. Some source
     * systems have incorrectly formatted dates, or date times stamps that are
     * not database friendly.
     *
     * @param nativeRs Native date format as a string
     * @return A stringified canonical date format
     */
    public static String toCanonicalDate(String nativeRs) {
        return nativeRs;
        /** what's the point, this doesnt do anything..
         String canonicalRs = new String();

         //canonicalRs = nativeRs.replace("T", " ");
         canonicalRs = nativeRs;

         return canonicalRs;
         **/
    }

    /**
     * Canonicalizes a given JSONArray to a basic List object to avoid the use of JSON parsers.
     *
     * @param list A given JSONArray object response from the source system
     * @return The sanitized, canonical List<String>
     */
    public static List<String> toCanonicalList(List<String> list) {
        return list.stream().map(ClientUtil::sanitizeResponse).collect(Collectors.toList());
    }
}
