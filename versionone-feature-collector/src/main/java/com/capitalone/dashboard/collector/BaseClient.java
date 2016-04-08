package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoryDataClient.class);
    private final VersionOneDataFactoryImpl vOneApi;
    private final FeatureSettings featureSettings;

    protected BaseClient(VersionOneDataFactoryImpl vOneApi, FeatureSettings featureSettings) {
        this.vOneApi = vOneApi;
        this.featureSettings = featureSettings;
    }

    protected String getJSONString(JSONObject obj, String field) {
        return sanitizeResponse((String) obj.get(field));
    }


    public void updateObjectInformation(String query) throws HygieiaException {
        long start = System.nanoTime();
        int pageIndex = 0;
        int pageSize = this.featureSettings.getPageSize();
        vOneApi.setPageSize(pageSize);
        vOneApi.setBasicQuery(query);
        vOneApi.buildPagingQuery(0);
        JSONArray outPutMainArray = vOneApi.getPagingQueryResponse();
        if (CollectionUtils.isEmpty(outPutMainArray))
            throw new HygieiaException(
                    "FAILED: FAILED: VersionOne response included unexpected JSON format",
                    HygieiaException.JSON_FORMAT_ERROR);

        JSONArray tmpDetailArray = (JSONArray) outPutMainArray.get(0);
        while (!CollectionUtils.isEmpty(tmpDetailArray)) {
            updateMongoInfo(tmpDetailArray);
            pageIndex = pageIndex + pageSize;
            vOneApi.buildPagingQuery(pageIndex);
            outPutMainArray = vOneApi.getPagingQueryResponse();
            if (outPutMainArray == null) {
                LOGGER.info("FAILED: Script Completed with Error");
                throw new HygieiaException(
                        "FAILED: Nothing to update from VersionOne's response",
                        HygieiaException.NOTHING_TO_UPDATE);
            }
            tmpDetailArray = (JSONArray) outPutMainArray.get(0);
        }
        double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
        LOGGER.info("Process took :" + elapsedTime + " seconds to update");
    }

    protected abstract void updateMongoInfo(JSONArray tmpDetailArray);

    protected abstract String getMaxChangeDate();

    public VersionOneDataFactoryImpl getvOneApi() {
        return vOneApi;
    }

    public FeatureSettings getFeatureSettings() {
        return featureSettings;
    }

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
     * Canonicalizes a given JSONArray to a basic List object to avoid the use of JSON parsers.
     *
     * @param list A given JSONArray object response from the source system
     * @return The sanitized, canonical List<String>
     */
    public static List<String> toCanonicalList(List<String> list) {
        return list.stream().map(BaseClient::sanitizeResponse).collect(Collectors.toList());
    }


    /**
     * Retrieves source system queries based on the query name (without the file
     * type) and a specified change date parameter.
     *
     * @param changeDatePara
     *            The change date specified from which to pull data with a given
     *            query template.
     * @param queryName
     *            The source system query name (without the file type).
     * @return A given source system query, in String format.
     */
    public String getQuery(String changeDatePara, String queryName) {
        ST st = (new STGroupDir(featureSettings.getQueryFolder(), '$', '$')).getInstanceOf(queryName);
        st.add("changeDate", changeDatePara);
        return st.render();
    }

    /**
     * Retrieves source system history/trending queries based on the query name
     * (without the file type) and other parameters.
     *
     * @param sprintStartDate
     *            The sprint start data in ISO format.
     * @param sprintEndDate
     *            The sprint end data in ISO format.
     * @param sprintDeltaDate
     *            The delta date in ISO format.
     * @param queryName
     *            The source system query name (without the file type).
     * @return A given historical source system query, in String format.
     */
    public String getTrendingQuery(String sprintStartDate,
                                   String sprintEndDate, String sprintDeltaDate, String queryName) {
        ST st = (new STGroupDir(featureSettings.getQueryFolder(), '$', '$')).getInstanceOf(queryName);
        st.add("sprintStartDate", sprintStartDate);
        st.add("sprintEndDate", sprintEndDate);
        st.add("sprintDeltaDate", sprintDeltaDate);

        return st.render();
    }


}
