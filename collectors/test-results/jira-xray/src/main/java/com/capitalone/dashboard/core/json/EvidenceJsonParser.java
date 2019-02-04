package com.capitalone.dashboard.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.capitalone.dashboard.api.domain.Evidence;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * This class will parse the JSON for an Evidence
 */
public class EvidenceJsonParser implements JsonObjectParser<Evidence> {
    public static final String KEY_ID="id";
    public static final String KEY_FILENAME="fileName";
    public static final String KEY_FILESIZE="fileSize";
    public static final String KEY_AUTHOR="author";
    public static final String KEY_FILEURI="fileURL";

    public Evidence parse(JSONObject jsonObject) throws JSONException {
        Evidence myEvidence=null;

        jsonObject.put("self",""); // TODO: GET THE SELF URI FOR NAVIGATION
        Long id=jsonObject.getLong(KEY_ID);
        String fileName=jsonObject.getString(KEY_FILENAME);
        String fileSize=jsonObject.getString(KEY_FILESIZE);
        Date createdOn=parseDate();
        String author=jsonObject.getString(KEY_AUTHOR);
        URI fileURI= null;

        try {
            fileURI = new URI(jsonObject.getString(KEY_FILEURI));
        } catch (URISyntaxException e) {
            fileURI=null;
        }
        myEvidence=new Evidence(id,fileName,fileSize,createdOn,author,fileURI);
        return myEvidence;
    }

    /**
     * TODO: IMPLEMENT IT
     * @param
     * @return
     */
    private Date parseDate(){
    return new Date();}
}
