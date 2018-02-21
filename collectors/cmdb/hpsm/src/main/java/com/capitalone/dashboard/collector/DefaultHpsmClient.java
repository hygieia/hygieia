package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ChangeOrder;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.HpsmSoapModel;
import com.capitalone.dashboard.model.Incident;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * HpsmClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultHpsmClient implements HpsmClient {

    private static final Log LOG = LogFactory.getLog(DefaultHpsmClient.class);
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

    private static final String APP_TYPE = "app";
	private static final String COMPONENT_TYPE = "component";
	private static final String ENVIRONMENT_TYPE = "environment";

	private static final String DEFAULT_CHANGE_QUERY_FORMAT = "(date.entered > ''{0}'' and date.entered < ''{1}'') or (close.time > ''{0}'' and close.time < ''{1}'')";
	private static final String DEFAULT_INCIDENT_QUERY_FORMAT = "(Severity=1 or Severity=2 or Severity=3 or Severity=4) and update.time > ''{0}'' and update.time < ''{1}''";

	private static final String QUERY_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

	public static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

	private long lastExecuted;
	private long incidentCount;
	private long changeCount;

	private enum SoapRequestType {
		CMDB, CHANGE_ORDER, INCIDENT
	}

	@Override
	public void setLastExecuted(long lastExecuted) { this.lastExecuted = lastExecuted; };

	@Override
	public long getLastExecuted() { return lastExecuted; };

	@Override
	public long getIncidentCount() { return incidentCount; }

	@Override
	public void setIncidentCount(long incidentCount) { this.incidentCount = incidentCount; }

	@Override
	public long getChangeCount() { return changeCount; }

	@Override
	public void setChangeCount(long changeCount) { this.changeCount = changeCount; }

	@Autowired
	public DefaultHpsmClient(HpsmSettings hpsmSettings) {
		this.hpsmSettings = hpsmSettings;
	}

    /**
     *
     * @return Combined List<Cmdb> of APPs and Components
     */
	@Override
	public List<Cmdb> getApps() throws HygieiaException {

		String limit = hpsmSettings.getCmdbBatchLimit();
		if(limit != null && !limit.isEmpty()) {
			LOG.info("NOTE: Collector run limited to " + limit + " results by property file setting.");
		}
		List<Cmdb> cmdbList = new ArrayList<>();

		String statusString = hpsmSettings.getAppStatus();
		String[] statusArray = (statusString == null || statusString.isEmpty()) ? new String[]{null} : statusString.split(",");

		for(int i = 0; i < statusArray.length; i++) {
			if(statusArray[i] != null) {
				// this is just for logging what we are doing - it is perfectly valid for this to be null, but will
				// only run once - additional logging is unnecessary.
				LOG.info("Retrieving for status: " + statusArray[i]);
			}
			cmdbList.addAll(getAppList(statusArray[i]));
			cmdbList.addAll(getComponentList(statusArray[i]));
			cmdbList.addAll(getEnvironmentList(statusArray[i]));
		}

		return cmdbList;
	}

	@Override
	public List<ChangeOrder> getChangeOrders() throws HygieiaException{
		List<ChangeOrder> changeOrderList;
		changeOrderList = getChangeOrderList();
		return changeOrderList;
	}

	@Override
	public List<Incident> getIncidents() throws HygieiaException{
		List<Incident> incidentList;
		incidentList = getIncidentList();
		return incidentList;
	}

	/**
	 *
	 * Returns List<Cmdb> of Apps
	 * @return List<Cmdb>
	 */
	private List<Cmdb> getAppList(String status) throws HygieiaException{
		List<Cmdb> appList;

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
        hpsmSoapModel.setItemSubType(hpsmSettings.getAppSubType());
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
        hpsmSoapModel.setStatus(status);

		appList = getConfigurationItemList(hpsmSoapModel);

		return appList;
	}

	/**
	 *
	 * @return  Returns List<Cmdb> of Components
	 */
	private List<Cmdb> getComponentList(String status) throws HygieiaException{
		List<Cmdb> componentList;
        HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();

		hpsmSoapModel.setItemSubType(hpsmSettings.getCompSubType());
        hpsmSoapModel.setItemType(hpsmSettings.getCompType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
        hpsmSoapModel.setStatus(status);

		componentList = getConfigurationItemList(hpsmSoapModel);

        return componentList;
	}

	/**
	 *
	 * Returns List<Cmdb> of Environments
	 * @return List<Cmdb>
	 */
	private List<Cmdb> getEnvironmentList(String status) throws HygieiaException{
		List<Cmdb> componentList;
		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();

		hpsmSoapModel.setItemSubType(hpsmSettings.getEnvSubType());
		hpsmSoapModel.setItemType(hpsmSettings.getEnvType());
		hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
		hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
		hpsmSoapModel.setStatus(status);

		componentList = getConfigurationItemList(hpsmSoapModel);

		return componentList;
	}


	/**
	 * Takes hpsmSoapModel with settings set. Makes SOAP call and returns  List <Cmdb> with details
	 * @param hpsmSoapModel
	 * @return
	 */
	private List<Cmdb> getConfigurationItemList(HpsmSoapModel hpsmSoapModel) throws  HygieiaException{
		List<Cmdb> configurationItemList = new ArrayList<>();
		List<Cmdb> detailsList;
		String batchLimit = hpsmSettings.getCmdbBatchLimit();
		boolean getMore = true;
		int startValue = 0;
		while(getMore){
			String more = "";
			String status = "";

			int returnLimit = Integer.parseInt(batchLimit);


			String newStart = Integer.toString(startValue);
			String soapString = getSoapMessage(hpsmSoapModel,newStart, batchLimit, SoapRequestType.CMDB);
			String response = makeSoapCall(soapString, hpsmSoapModel);
			Document doc = responseToDoc(response);
			NodeList instanceNodeList = doc.getElementsByTagName("RetrieveDeviceListResponse");

			for (int i = 0; i < instanceNodeList.getLength(); i++) {
				NamedNodeMap instanceChildNodes = instanceNodeList.item(i).getAttributes();
				more = instanceChildNodes.getNamedItem("more").getNodeValue();
				status = instanceChildNodes.getNamedItem("status").getNodeValue();

			}
			detailsList = responseToDetailsList(response);

			if(detailsList != null && !detailsList.isEmpty()){
				configurationItemList.addAll(detailsList);
			}

			if(more == null || !more.equals("1") || status == null || !status.equals("SUCCESS")){
				getMore = false;
				LOG.info("No more items retrieved. Item count " + configurationItemList.size());
			}
			startValue += returnLimit;
		}

		return configurationItemList;

	}

	/**
	 *  Takes SOAP response and creates List <Cmdb> with details
	 * @param response
	 * @return List <Cmdb>
	 */
	private List <Cmdb> responseToDetailsList(String response) throws  HygieiaException{
        List <Cmdb> returnList = new ArrayList<>();
		try {

			JSONObject bodyObject = getBodyFromResponse(response.trim());


			JSONObject cmdbListResponse = getObject(bodyObject, "RetrieveDeviceListResponse");
			Object instance = cmdbListResponse.get("instance");

			if (instance instanceof JSONArray) {
				JSONArray instanceArray = (JSONArray) instance;
				for(Object arrayObject: instanceArray){
					if(arrayObject instanceof JSONObject){
						JSONObject contentObj = (JSONObject) arrayObject;
						returnList = getCmdbItem(contentObj, returnList);
					}else{
						LOG.info("No Object found for instanceArray");
					}
				}
			}else if(instance instanceof  JSONObject){
				JSONObject instanceObject = (JSONObject) instance;

				returnList = getCmdbItem(instanceObject, returnList);


			}else{
				throw new HygieiaException("No items fround in instance | response: " +cmdbListResponse.toString(), HygieiaException.BAD_DATA);
			}




		}catch(Exception e){
			LOG.error(e);
		}
		return returnList;
	}

	private List <ChangeOrder> responseToChangeOrderList(String response) {
		List <ChangeOrder> returnList = new ArrayList<>();
		try {
			JSONObject bodyObject = getBodyFromResponse(response.trim());


			JSONObject changeListResponse = getObject(bodyObject, "RetrieveChangeListResponse");
			Object instance = changeListResponse.get("instance");

			if (instance instanceof JSONArray) {
				JSONArray instanceArray = (JSONArray) instance;
				for (Object instanceArrayObject : instanceArray) {
					if (instanceArrayObject instanceof JSONObject) {
						JSONObject instanceJsonObject = (JSONObject) instanceArrayObject;
						returnList = getChangeItem(instanceJsonObject, returnList);
					}
				}
			} else if(instance instanceof  JSONObject){
				JSONObject instanceObject = (JSONObject) instance;

				returnList = getChangeItem(instanceObject, returnList);

			}else{
				LOG.info("No items to return");
			}


		}catch(Exception e){
			LOG.error(e);
		}

		return returnList;
	}

	private List <Incident> responseToIncidentList(String response) {
		List <Incident> returnList = new ArrayList<>();
		try {
			JSONObject bodyObject = getBodyFromResponse(response.trim());

			JSONObject incidentListResponse = getObject(bodyObject, "RetrieveIncidentListResponse");
			Object instance = incidentListResponse.get("instance");

			if (instance instanceof JSONArray) {
				JSONArray instanceArray = (JSONArray) instance;
				for (Object instanceArrayObject : instanceArray) {
					if (instanceArrayObject instanceof JSONObject) {
						JSONObject instanceJsonObject = (JSONObject) instanceArrayObject;
						returnList = getIncidentItem(instanceJsonObject, returnList);
					}
				}
			} else if(instance instanceof  JSONObject){
				JSONObject instanceObject = (JSONObject) instance;

				returnList = getIncidentItem(instanceObject, returnList);

			}else{
				LOG.info("No items to return");
			}


		}catch(Exception e){
			LOG.error(e);
		}
		return returnList;
	}

	/**
	 *
	 * Returns List<ChangeOrder> of Change Orders
	 * @return List<ChangeOrder>
	 */
	private List<ChangeOrder> getChangeOrderList() throws HygieiaException{
		List<ChangeOrder> changeOrderList;
		String limit = hpsmSettings.getChangeOrderReturnLimit();

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
		hpsmSoapModel.setRequestTypeName(hpsmSettings.getChangeOrderRequestType());
		hpsmSoapModel.setSoapAction(hpsmSettings.getChangeOrderSoapAction());

		String soapString = getSoapMessage(hpsmSoapModel,"",limit, SoapRequestType.CHANGE_ORDER);

		String response  = makeSoapCall(soapString, hpsmSoapModel);

		changeOrderList = responseToChangeOrderList(response);

		return changeOrderList;
	}

	/**
	 *
	 * Returns List<Incident> of Incidents
	 * @return List<Incident>
	 */
	private List<Incident> getIncidentList() throws HygieiaException{
		List<Incident> incidentList;
		String limit = hpsmSettings.getIncidentReturnLimit();

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
		hpsmSoapModel.setRequestTypeName(hpsmSettings.getIncidentRequestType());
		hpsmSoapModel.setSoapAction(hpsmSettings.getIncidentSoapAction());

		String soapString = getSoapMessage(hpsmSoapModel, "", limit, SoapRequestType.INCIDENT );

		String response  = makeSoapCall(soapString, hpsmSoapModel);

		incidentList = responseToIncidentList(response);

		return incidentList;
	}


	/**
	 * Returns the type of the configuration item.
	 * @param cmdb
	 * @return the type of the configuration item.
	 */
	private String getItemType(Cmdb cmdb) {
		String itemType = null;
		String subType = cmdb.getConfigurationItemSubType();
		String type = cmdb.getConfigurationItemType();

		String hpsmSettingsSubType = hpsmSettings.getAppSubType();
		String hpsmSettingsType = hpsmSettings.getAppType();

		boolean typeCheck = false;
		boolean subTypeCheck = false;

		if(!"".equals(hpsmSettingsType)){
			typeCheck = true;
		}
		if(!"".equals(hpsmSettingsSubType)){
			subTypeCheck = true;
		}

		if(!typeCheck && subTypeCheck){
			if(subType != null && subType.equals(hpsmSettings.getAppSubType())){
				itemType = APP_TYPE;
			}
			else if(subType != null && subType.equals(hpsmSettings.getCompSubType())){
				itemType = COMPONENT_TYPE;
			}
			else if(subType != null && subType.equals(hpsmSettings.getEnvSubType())) {
				itemType = ENVIRONMENT_TYPE;
			}
		}else if(typeCheck && !subTypeCheck){
			if(type != null && type.equals(hpsmSettings.getAppType())){
				itemType = APP_TYPE;
			}
			else if(type != null && type.equals(hpsmSettings.getCompType())){
				itemType = COMPONENT_TYPE;
			}
			else if(type != null && type.equals(hpsmSettings.getEnvType())) {
				itemType = ENVIRONMENT_TYPE;
			}
		}else{
			if(subType != null && subType.equals(hpsmSettings.getAppSubType()) && type != null && type.equals(hpsmSettings.getAppType())){
				itemType = APP_TYPE;
			}
			else if(subType != null && subType.equals(hpsmSettings.getCompSubType()) && type != null && type.equals(hpsmSettings.getCompType())){
				itemType = COMPONENT_TYPE;
			}
			else if(subType != null && subType.equals(hpsmSettings.getEnvSubType()) && type != null && type.equals(hpsmSettings.getEnvType())){
				itemType = ENVIRONMENT_TYPE;
			}

		}

		return itemType;
	}
	/**
	 *  Converts String response into document for parsing
	 * @param response SOAP response required for creation of Document
	 * @return Document Object
	 */
	private Document responseToDoc(String response){

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

	/**
	 *  Start SOAP connection
	 */
	private void startHttpConnection(){
		server = hpsmSettings.getServer();
		port = hpsmSettings.getPort();
		protocol = hpsmSettings.getProtocol() + "://";
		resource = hpsmSettings.getResource();
		userName = hpsmSettings.getUser();
		password = hpsmSettings.getPass();

		if(!usedClient){
			strURL = protocol + server + ":" + port + "/"
					+ resource;
			// Prepare HTTP post
			post = new PostMethod(strURL);


			// Get HTTP client
			httpclient.getParams().setAuthenticationPreemptive(true);

			Credentials defaultcreds = new UsernamePasswordCredentials(userName,
					password);
			httpclient.getState().setCredentials(
					new AuthScope(server, port, AuthScope.ANY_REALM), defaultcreds);
			usedClient = true;
		}

	}

    /**
     * Ends SOAP Connection
     */
	private void stopHttpConnection() {
		if(post != null && usedClient){
			post.releaseConnection();
		}
		if(manager != null && usedClient){
			manager.shutdown();
		}
		usedClient = false;
	}
	private String getResponseString(InputStream in) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] byteArray = new byte[1024];
		int count;
		while ((count = in.read(byteArray, 0, byteArray.length)) > 0) {
			outputStream.write(byteArray, 0, count);
		}
		return new String(outputStream.toByteArray(), "UTF-8");
	}
    /**
     *  Makes SOAP request for given soap message
     * @param soapMessageString Generated SOAP ready for POST
     * @param hpsmSoapModel hpsmSoapModel
     * @return Soap response
     */
    private String makeSoapCall(String soapMessageString, HpsmSoapModel hpsmSoapModel) throws HygieiaException{

        String requestAction = hpsmSoapModel.getSoapAction();
        String response = "";
        contentType = hpsmSettings.getContentType();
        charset = hpsmSettings.getCharset();

        try {
            startHttpConnection();

            RequestEntity entity = new StringRequestEntity(
                    soapMessageString, contentType, charset);
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

	private String getSoapMessage(HpsmSoapModel hpsmSoapModel, String start, String limit, SoapRequestType type){
		String strMsg = "";
		SOAPMessage soapMsg;
		String requestTypeName = hpsmSoapModel.getRequestTypeName();

		try {
			MessageFactory factory = MessageFactory.newInstance();

			soapMsg = factory.createMessage();

			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			envelope.addNamespaceDeclaration("ns", "http://schemas.hp.com/SM/7");
			envelope.addNamespaceDeclaration("com", "http://schemas.hp.com/SM/7/Common");
			envelope.addNamespaceDeclaration("xm", "http://www.w3.org/2005/05/xmlmime");

			SOAPBody body = envelope.getBody();

			SOAPBodyElement requestType = body.addBodyElement(envelope.createName(requestTypeName,"ns", ""));

			if(limit != null && !limit.isEmpty()) {
				QName name1 = new QName("count");
				requestType.addAttribute(name1, limit);
			}
			if(start != null && !start.isEmpty()) {
				QName qNameStart = new QName("start");
				requestType.addAttribute(qNameStart, start);
			}
			QName qNameIgnoreEmptyValues = new QName("ignoreEmptyElements");
			requestType.addAttribute(qNameIgnoreEmptyValues, "true");

			SOAPBodyElement modelTag = body.addBodyElement(envelope.createName("model","ns", ""));

			SOAPBodyElement keysTag = body.addBodyElement(envelope.createName("keys","ns", ""));

			// creates instance tag
			body.addBodyElement(envelope.createName("instance", "ns", ""));

			if(type.equals(SoapRequestType.CHANGE_ORDER)){
				handleChangeSoapMessage(keysTag);
			}else if(type.equals(SoapRequestType.INCIDENT)){
				handleIncidentSoapMessage(keysTag);
			}else{
				handleCmdbSoapMessage(hpsmSoapModel, envelope, keysTag);
			}

			modelTag.addChildElement(keysTag);

			requestType.addChildElement(modelTag);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			soapMsg.writeTo(out);
			strMsg = new String(out.toByteArray());

		} catch (SOAPException e) {
			LOG.error("SOAPException: " + e);
		} catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException: " + e);
		} catch (IOException e) {
			LOG.error("IOException: " + e);
		}

		return strMsg;
	}

    private void handleCmdbSoapMessage(HpsmSoapModel hpsmSoapModel, SOAPEnvelope envelope, SOAPBodyElement keysTag) throws SOAPException{

		String itemType = hpsmSoapModel.getItemType();
		String itemSubType = hpsmSoapModel.getItemSubType();
		String item = hpsmSoapModel.getItem();
		String status = hpsmSoapModel.getStatus();

		SOAPBody body = envelope.getBody();

		if (itemType != null && !itemType.isEmpty()) {

			SOAPBodyElement configItemType = body.addBodyElement(envelope.createName("ConfigurationItemType", "ns", ""));
			configItemType.addTextNode(itemType);
			keysTag.addChildElement(configItemType);

		}
		if (itemSubType != null && !itemSubType.isEmpty()) {

			SOAPBodyElement configItemSubType = body.addBodyElement(envelope.createName("ConfigurationItemSubType", "ns", ""));
			configItemSubType.addTextNode(itemSubType);
			keysTag.addChildElement(configItemSubType);

		}
		if (item != null && !item.isEmpty()) {

			SOAPBodyElement configItem = body.addBodyElement(envelope.createName("ConfigurationItem", "ns", ""));
			configItem.addTextNode(item);
			keysTag.addChildElement(configItem);

		}
		if (status != null && !status.isEmpty()) {

			SOAPBodyElement configItemStatus = body.addBodyElement(envelope.createName("Status", "ns", ""));
			configItemStatus.addTextNode(status);
			keysTag.addChildElement(configItemStatus);

		}
	}


	private void handleIncidentSoapMessage(SOAPBodyElement keysTag) throws SOAPException{

		QName query = new QName("query");

		// Incidents can be queried based on time.  This code retrieves the incidents since
		// the last time it was run.  If that time cannot be determined, it counts backwards
		// the number of days specified in hpsm.properties and retrieves those incidents.

		// Get current date/time
		Date nowDate = new Date();

		// Get the last time this collector was run
		Date previousDate = new Date(this.lastExecuted);

		// Convert the above times to milliseconds for comparison
		long nowMillis = nowDate.getTime();
		long previousMillis = previousDate.getTime();

		// calculate the difference in days between the two dates by dividing the difference by the number of milliseconds in a day
		int diffInDays = (int) (Math.abs((nowMillis - previousMillis)) / MILLISECONDS_IN_DAY);

		// get the number of days specified in the hpsm.properties file
		int incidentDays = hpsmSettings.getIncidentDays();

		// IF there are no incidents in the collection, or the collection does not exist
		// OR if the times are reversed
		// OR the number of days since collector last ran is greater than the requested number of days
		// THEN the last time the collector ran is irrelevant so use the number of days in hpsm.properties
		if((incidentCount < 1) || (previousMillis > nowMillis) || (diffInDays > incidentDays)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(nowDate);
			cal.add(Calendar.DATE, - incidentDays);
			previousDate = cal.getTime();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(QUERY_DATE_FORMAT);
		String now = dateFormat.format(nowDate);
		String previous = dateFormat.format(previousDate);

		String format = hpsmSettings.getIncidentQuery();
		if(format == null || format.isEmpty()){
			format = DEFAULT_INCIDENT_QUERY_FORMAT;
		}

		Object[] args = new Object[]{ previous, now };
		String queryString = MessageFormat.format(format, args);

		keysTag.addAttribute(query,  queryString);

	}

	private void handleChangeSoapMessage(SOAPBodyElement keysTag) throws SOAPException{

		QName query = new QName("query");

		// Changes can be queried based on time.  This code retrieves the changes since
		// the last time it was run.  If that time cannot be determined, it counts backwards
		// the number of days specified in hpsm.properties and retrieves those changes.

		// Get current date/time
		Date nowDate = new Date();

		// Get the last time this collector was run
		Date previousDate = new Date(this.lastExecuted);

		// Convert the above times to milliseconds for comparison
		long nowMillis = nowDate.getTime();
		long previousMillis = previousDate.getTime();

		// calculate the difference in days between the two dates by dividing the difference by the number of milliseconds in a day
		int diffInDays = (int) (Math.abs((nowMillis - previousMillis)) / MILLISECONDS_IN_DAY);

		// get the number of days specified in the hpsm.properties file
		int changeDays = hpsmSettings.getChangeOrderDays();

		// IF there are no changess in the collection, or the collection does not exist
		// OR if the times are reversed
		// OR the number of days since collector last ran is greater than the requested number of days
		// THEN the last time the collector ran is irrelevant so use the number of days in hpsm.properties
		if((changeCount < 1) || (previousMillis > nowMillis) || (diffInDays > changeDays)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(nowDate);
			cal.add(Calendar.DATE, - changeDays);
			previousDate = cal.getTime();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(QUERY_DATE_FORMAT);
		String now = dateFormat.format(nowDate);
		String previous = dateFormat.format(previousDate);

		String format = hpsmSettings.getChangeOrderQuery();
		if(format == null || format.isEmpty()){
			format = DEFAULT_CHANGE_QUERY_FORMAT;
		}

		Object[] args = new Object[]{ previous, now };
		String queryString = MessageFormat.format(format, args);

		keysTag.addAttribute(query,  queryString);

	}

	/**
	 * Takes response xml string, creates json string and returns an object
	 * @param response
	 * @return
	 */
	private JSONObject getBodyFromResponse(String response) throws HygieiaException{
		JSONObject instanceObject =  new JSONObject();
		try {
			JSONObject xmlJSONObj = XML.toJSONObject(response.trim());

			JSONObject envelope = getObject(xmlJSONObj, "SOAP-ENV:Envelope");
			if(!envelope.has("SOAP-ENV:Body")){
				throw new HygieiaException("Body not found in response | Response " +response,HygieiaException.BAD_DATA);
			}
			if (envelope != null) {
				Object object  = envelope.get("SOAP-ENV:Body");
				if (object != null && object instanceof JSONObject) {
					instanceObject = (JSONObject) object;
				}
			}
		}catch(Exception e){
			LOG.error(e);
		}
		return instanceObject;
	}
	private List<Cmdb> getCmdbItem(JSONObject instance,  List<Cmdb> list) {
		Cmdb cmdb = new Cmdb();
		if(!instance.has("ConfigurationItem")) return list;

		JSONObject configurationItem = getObject(instance, "ConfigurationItem");
		JSONObject configurationItemSubType = getObject(instance, "ConfigurationItemSubType");
		JSONObject configurationItemType = getObject(instance, "ConfigurationItemType");
		JSONObject assignmentGroup = getObject(instance, "AssignmentGroup");
		JSONObject appServiceOwner = getObject(instance, "AppServiceOwner");
		JSONObject businessOwner = getObject(instance, "BusinessOwner");
		JSONObject supportOwner = getObject(instance, "SupportOwner");
		JSONObject developmentOwner = getObject(instance, "DevelopmentOwner");
		JSONObject ownerDept = getObject(instance, "OwnerDept");
		JSONObject commonName = getObject(instance, "CommonName");


		String configurationItemValue = getString(configurationItem, "content");
		String configurationItemSubTypeValue = getString(configurationItemSubType, "content");
		String configurationItemTypeValue = getString(configurationItemType, "content");
		String assignmentGroupValue = getString(assignmentGroup, "content");
		String appServiceOwnerValue = getString(appServiceOwner, "content");
		String businessOwnerValue = getString(businessOwner, "content");
		String supportOwnerValue = getString(supportOwner, "content");
		String developmentOwnerValue = getString(developmentOwner, "content");
		String ownerDeptValue = getString(ownerDept, "content");
		String commonNameValue = getString(commonName, "content");

		cmdb.setConfigurationItem(configurationItemValue);
		cmdb.setConfigurationItemSubType(configurationItemSubTypeValue);
		cmdb.setConfigurationItemType(configurationItemTypeValue);
		cmdb.setAssignmentGroup(assignmentGroupValue);
		cmdb.setAppServiceOwner(appServiceOwnerValue);
		cmdb.setBusinessOwner(businessOwnerValue);
		cmdb.setSupportOwner(supportOwnerValue);
		cmdb.setDevelopmentOwner(developmentOwnerValue);
		cmdb.setOwnerDept(ownerDeptValue);
		cmdb.setCommonName(commonNameValue);
		cmdb.setItemType(getItemType(cmdb));
		cmdb.setValidConfigItem(true);
		cmdb.setTimestamp(System.currentTimeMillis());
		if(configurationItemValue != null && !configurationItemValue.isEmpty()) {
			list.add(cmdb);
		}
		return list;
	}
	private List<Incident> getIncidentItem(JSONObject instance,  List<Incident> list) {
		Incident incident = new Incident();
		if(!instance.has("IncidentID")) return list;

		JSONObject incidentID = getObject(instance, "IncidentID");
		JSONObject category = getObject(instance, "Category");
		JSONObject openTime = getObject(instance, "OpenTime");
		JSONObject openedBy = getObject(instance, "OpenedBy");
		JSONObject severity = getObject(instance, "Severity");
		JSONObject updatedTime = getObject(instance, "UpdatedTime");
		JSONObject primaryAssignmentGroup = getObject(instance, "PrimaryAssignmentGroup");
		JSONObject status = getObject(instance, "Status");
		JSONObject affectedItem = getObject(instance, "AffectedItem");
		JSONObject incidentDescription = getObject(instance, "IncidentDescription");

		String incidentIdValue = getString(incidentID, "content");
		incident.setIncidentID(incidentIdValue);
		incident.setCategory(getString(category, "content"));
		incident.setOpenTime(getString(openTime, "content"));
		incident.setOpenedBy(getString(openedBy,"content"));
		incident.setUpdatedTime(getString(updatedTime,"content"));
		incident.setSeverity(getString(severity,"content"));
		incident.setPrimaryAssignmentGroup(getString(primaryAssignmentGroup,"content"));
		incident.setStatus(getString(status,"content"));
		incident.setAffectedItem(getString(affectedItem,"content"));


		Object incidentDescriptionObject = incidentDescription.get("IncidentDescription");

		if(incidentDescriptionObject instanceof JSONArray){
			JSONArray incidentDescriptionArray = (JSONArray) incidentDescriptionObject;
			List<String> descriptionList = new ArrayList<>();
			for (Object obj: incidentDescriptionArray){
				if(obj instanceof JSONObject){
					JSONObject incidentDescriptionContent = (JSONObject) obj;
					descriptionList.add(incidentDescriptionContent.getString("content"));
				}
			}
			incident.setIncidentDescription(descriptionList);
		}
		if(incidentIdValue != null && !incidentIdValue.isEmpty()) {
			list.add(incident);
		}
		return list;
	}

	private List <ChangeOrder> getChangeItem(JSONObject instance, List <ChangeOrder> list) {
		ChangeOrder change = new ChangeOrder();
		JSONObject header = getObject(instance, "header");

		if(!header.has("ChangeID")) return list;

		JSONObject changeID = getObject(header, "ChangeID");
		JSONObject category = getObject(header, "Category");
		JSONObject status = getObject(header, "Status");
		JSONObject approvalStatus = getObject(header, "ApprovalStatus");
		JSONObject initiatedBy = getObject(header, "InitiatedBy");
		JSONObject assignedTo = getObject(header, "AssignedTo");
		JSONObject assignmentGroup = getObject(header, "AssignmentGroup");
		JSONObject plannedStart = getObject(header, "PlannedStart");
		JSONObject plannedEnd = getObject(header, "PlannedEnd");
		JSONObject reason = getObject(header, "Reason");

		JSONObject phase = getObject(header, "Phase");
		JSONObject riskAssessment = getObject(header, "RiskAssessment");
		JSONObject dateEntered = getObject(header, "DateEntered");
		JSONObject open = getObject(header, "Open");
		JSONObject title = getObject(header, "Title");
		JSONObject subcategory = getObject(header, "Subcategory");
		JSONObject changeModel = getObject(header, "ChangeModel");

		String changeIdValue = getString(changeID, "content");
		change.setChangeID(changeIdValue);
		change.setCategory(getString(category, "content"));
		change.setStatus(getString(status, "content"));
		change.setApprovalStatus(getString(approvalStatus, "content"));
		change.setInitiatedBy(getString(initiatedBy, "content"));
		change.setAssignedTo(getString(assignedTo, "content"));
		change.setAssignmentGroup(getString(assignmentGroup, "content"));
		change.setPlannedStart(getString(plannedStart, "content"));
		change.setPlannedEnd(getString(plannedEnd, "content"));
		change.setReason(getString(reason, "content"));
		change.setPhase(getString(phase, "content"));
		change.setRiskAssessment(getString(riskAssessment, "content"));
		change.setDateEntered(getString(dateEntered, "content"));
		change.setOpen(getString(open, "content"));
		change.setTitle(getString(title, "content"));
		change.setSubcategory(getString(subcategory, "content"));
		change.setChangeModel(getString(changeModel, "content"));

		if (changeIdValue != null && !changeIdValue.isEmpty()){
			list.add(change);
		}

		return list;
	}

	private JSONObject getObject(JSONObject json, String key) {
		if (json == null) return new JSONObject();
		if (!json.has(key)) return new JSONObject();
		return (JSONObject) json.get(key);
	}
	private String getString(JSONObject json, String key) {
		if (json == null || !json.has(key)) return "";
		Object value = json.get(key);
		return (value == null) ? "" : value.toString();
	}
}