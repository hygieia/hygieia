package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.HpsmSoapModel;
import com.capitalone.dashboard.model.Incident;
import com.capitalone.dashboard.util.HpsmCollectorConstants;
import com.capitalone.dashboard.util.XmlUtil;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DefaultBaseClient {
    private static final Log LOG = LogFactory.getLog(DefaultBaseClient.class);

    private final HpsmSettings hpsmSettings;
    private PostMethod post;
    private SimpleHttpConnectionManager manager = new SimpleHttpConnectionManager(true);
    HttpClient httpclient = new HttpClient(manager);
    boolean usedClient = false;
    int port;
    String strURL;
    String protocol;
    String server;
    String resource;
    String contentType;
    String charset;
    String userName = "";
    String password = "";

    private static final String APPLICATION_PREFIX = "ASV";
    private static final String ENVIRONMENT_PREFIX = "ENV";

    @Autowired
    public DefaultBaseClient(HpsmSettings hpsmSettings) {
        this.hpsmSettings = hpsmSettings;
    }

    /**
     *  Makes SOAP request for given soap message
     * @param soapMessageString Generated SOAP ready for POST
     * @param hpsmSoapModel hpsmSoapModel
     * @return Soap response
     */
    protected String makeSoapCall(String soapMessageString, HpsmSoapModel hpsmSoapModel) throws HygieiaException {

        String requestAction = hpsmSoapModel.getSoapAction();
        String response = "";
        contentType = hpsmSettings.getContentType();
        charset = hpsmSettings.getCharset();

        try {
            startHttpConnection();

            RequestEntity entity = new StringRequestEntity(soapMessageString, contentType, charset);
            post.setRequestEntity(entity);
            post.setRequestHeader("SOAPAction", requestAction);

            httpclient.executeMethod(post);

            response = getResponseString(post.getResponseBodyAsStream());

            if(!"OK".equals(post.getStatusText())){
                throw new HygieiaException("Soap Request Failure: " +  post.getStatusCode() + "|response: " +response, HygieiaException.BAD_DATA);
            }

            stopHttpConnection();
        } catch (IOException e) {
            LOG.error("Error while trying to make soap call: " + e);
        }

        return response;
    }

    protected void startHttpConnection() {
        server = hpsmSettings.getServer();
        port = hpsmSettings.getPort();
        protocol = hpsmSettings.getProtocol() + "://";
        resource = hpsmSettings.getResource();
        userName = hpsmSettings.getUser();
        password = hpsmSettings.getPass();

        if(!usedClient){
            strURL = protocol + server + ":" + port + "/" + resource;
            post = new PostMethod(strURL);

            // Get HTTP client
            httpclient.getParams().setAuthenticationPreemptive(true);

            Credentials defaultcreds = new UsernamePasswordCredentials(userName, password);
            httpclient.getState().setCredentials(new AuthScope(server, port, AuthScope.ANY_REALM), defaultcreds);
            usedClient = true;
        }
    }

    protected void stopHttpConnection() {
        if(post != null && usedClient) { post.releaseConnection(); }

        if(manager != null && usedClient) { manager.shutdown(); }

        usedClient = false;
    }

    protected String getResponseString(InputStream in) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] byteArray = new byte[1024];
        int count;
        while ((count = in.read(byteArray, 0, byteArray.length)) > 0) {
            outputStream.write(byteArray, 0, count);
        }
        return new String(outputStream.toByteArray(), "UTF-8");
    }

    protected List<Incident> responseToIncidentList(String response) {
        List <Incident> returnList = new ArrayList<>();
        try {
            Document doc = responseToDoc(response);
            for(Node n: XmlUtil.asList(doc.getElementsByTagName("instance"))){
                Map xmlMap = XmlUtil.getElementKeyValue(n.getChildNodes());
                returnList.addAll(getIncidentFromXmlMap(xmlMap));
            }
        } catch(Exception e){
            LOG.error(e);
        }
        return returnList;
    }

    /**
     *  Converts String response into document for parsing
     * @param response SOAP response required for creation of Document
     * @return Document Object
     */
    protected Document responseToDoc(String response){
        Document doc = null;
        try {
            DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
            DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =  factory.newDocumentBuilder();
            ByteArrayInputStream input =  new ByteArrayInputStream(response.getBytes("UTF-8"));
            doc = builder.parse(input);
        } catch (ParserConfigurationException e) {
            LOG.error("ParserConfigurationException", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException", e);
        } catch (IOException e) {
            LOG.error("IOException", e);
        } catch (SAXException e) {
            LOG.error("SAXException", e);
        }

        return doc;
    }

    protected List<Incident> getIncidentFromXmlMap(Map map) {
        if(map == null || map.isEmpty()) return new ArrayList<>();
        if(getStringValueFromMap(map, HpsmCollectorConstants.INCIDENT_ID).isEmpty()) return new ArrayList<>();

        // Environment Check
        String environment = getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_ENVIRONMENT);
        if (!environmentCheck(environment)) { return new ArrayList<>();}

        Incident incident = new Incident();
        incident.setIncidentID(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_ID));
        incident.setCategory(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_CATEGORY));
        incident.setOpenTime(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_OPEN_TIME));
        String closedTime = getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_CLOSE_TIME);
        if (!StringUtils.isEmpty(closedTime)) {
            incident.setClosedTime(closedTime);
        } else {
            incident.setClosedTime(0L);
        }
        incident.setOpenedBy(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_OPEN_BY));
        incident.setUpdatedTime(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_UPDATE_TIME));
        incident.setSeverity(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_SEVERITY));
        incident.setPrimaryAssignmentGroup(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_PRIMARY_ASSIGNMENT_GROUP));
        incident.setStatus(getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_STATUS));

        // Determine the affected Item.
        String affectedItem = getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_AFFECTED_ITEM);
        String service = getStringValueFromMap(map,HpsmCollectorConstants.INCIDENT_SERVICE);
        String affectedItemResult = getAffectedItem(affectedItem, service);
        incident.setAffectedItem(affectedItemResult);

        incident.setIncidentDescription(getStringValueFromMap(map, HpsmCollectorConstants.INCIDENT_DESCRIPTION));
        List<Incident> list = new ArrayList<>();
        list.add(incident);

        return list;
    }

    protected boolean environmentCheck (String environment) {
        if (StringUtils.isEmpty(environment)) { return true; }

        List<String> configuredEnvironmentList = hpsmSettings.getIncidentEnvironments();
        if (!CollectionUtils.isEmpty(configuredEnvironmentList) && !StringUtils.isEmpty(environment)) {
            String searchResult = configuredEnvironmentList.stream()
                                    .filter(configuredEnv -> configuredEnv.equalsIgnoreCase(environment))
                                    .findFirst().orElse(null);
            if (!StringUtils.isEmpty(searchResult)) return true;
        }
        return false;
    }

    protected String getAffectedItem(String affectedItem, String service) {
        // Return affectedItem, if it is truly a BAP/CI
        if  (!StringUtils.isEmpty(affectedItem)
                && !affectedItem.toUpperCase(Locale.ENGLISH).startsWith(APPLICATION_PREFIX)
                && !affectedItem.toUpperCase(Locale.ENGLISH).startsWith(ENVIRONMENT_PREFIX)) {
            return affectedItem;
        }

        // Return the service if it happens to be a BAP/CI
        if  (!StringUtils.isEmpty(service)
                && !service.toUpperCase(Locale.ENGLISH).startsWith(APPLICATION_PREFIX)
                && !service.toUpperCase(Locale.ENGLISH).startsWith(ENVIRONMENT_PREFIX)) {
            return service;
        }

        // Return affected Item if it is available.
        if  (!StringUtils.isEmpty(affectedItem)) { return affectedItem; }

        // Return service if it is available.
        if  (!StringUtils.isEmpty(service)) { return service; }

        return null;
    }

    protected String getStringValueFromMap(Map map, String key) {
        if(!map.containsKey(key)
                || map.get(key) == null
                || "".equals(key)) return "";
        return map.get(key).toString();
    }

    /**
     * Date utility
     *
     * @param dateInstance
     * @param offsetDays
     * @param offsetMinutes
     * @return
     */
    protected static DateTime getDate(DateTime dateInstance, int offsetDays, int offsetMinutes) {
        return dateInstance.minusDays(offsetDays).minusMinutes(offsetMinutes);
    }
}
