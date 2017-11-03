package com.capitalone.dashboard.collector;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.*;
import java.lang.reflect.Method;
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
	public List<Cmdb> getApps() {

		List<Cmdb> cmdbList;
        cmdbList = getAppList();
        cmdbList.addAll(getComponentList());
		cmdbList.addAll(getEnvironmentList());

		return cmdbList;
	}

	@Override
	public List<ChangeOrder> getChangeOrders() {
		List<ChangeOrder> changeOrderList;
		changeOrderList = getChangeOrderList();
		return changeOrderList;
	}

	@Override
	public List<Incident> getIncidents() {
		List<Incident> incidentList;
		incidentList = getIncidentList();
		return incidentList;
	}

	/**
	 *
	 * Returns List<Cmdb> of Apps
	 * @return List<Cmdb>
	 */
	private List<Cmdb> getAppList(){
		List<Cmdb> appList;

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
        hpsmSoapModel.setItemSubType(hpsmSettings.getAppSubType());
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
        hpsmSoapModel.setStatus(hpsmSettings.getAppStatus());

		appList = getConfigurationItemList(hpsmSoapModel);

		return appList;
	}

	/**
	 *
	 * @return  Returns List<Cmdb> of Components
	 */
	private List<Cmdb> getComponentList(){
		List<Cmdb> componentList;
        HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();

		hpsmSoapModel.setItemSubType(hpsmSettings.getCompSubType());
        hpsmSoapModel.setItemType(hpsmSettings.getCompType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
        hpsmSoapModel.setStatus(hpsmSettings.getAppStatus());

		componentList = getConfigurationItemList(hpsmSoapModel);

        return componentList;
	}

	/**
	 *
	 * Returns List<Cmdb> of Environments
	 * @return List<Cmdb>
	 */
	private List<Cmdb> getEnvironmentList(){
		List<Cmdb> componentList;
		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();

		hpsmSoapModel.setItemSubType(hpsmSettings.getEnvSubType());
		hpsmSoapModel.setItemType(hpsmSettings.getEnvType());
		hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
		hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
		hpsmSoapModel.setStatus(hpsmSettings.getAppStatus());

		componentList = getConfigurationItemList(hpsmSoapModel);

		return componentList;
	}


	/**
	 * Takes hpsmSoapModel with settings set. Makes SOAP call and returns  List <Cmdb> with details
	 * @param hpsmSoapModel
	 * @return
	 */
	private List<Cmdb> getConfigurationItemList(HpsmSoapModel hpsmSoapModel){
		List<Cmdb> configurationItemList;

		String soapString = getDefaultSoapMessage(SoapRequestType.CMDB, hpsmSoapModel);

		String response  = makeSoapCall(soapString, hpsmSoapModel);

		configurationItemList = responseToDetailsList(response);

		return configurationItemList;
	}

	/**
	 *  Takes SOAP response and creates List <Cmdb> with details
	 * @param response
	 * @return List <Cmdb>
	 */
	private List <Cmdb> responseToDetailsList(String response) {
        List <Cmdb> returnList = new ArrayList<>();
		Document doc = responseToDoc(response);
        NodeList instanceNodeList = doc.getElementsByTagName("instance");
        for (int i = 0; i < instanceNodeList.getLength(); i++) {
            NodeList instanceChildNodes = instanceNodeList.item(i).getChildNodes();
            Cmdb cmdb = new Cmdb();
            for (int j = 0; j < instanceChildNodes.getLength(); j++) {
                Node node = instanceChildNodes.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String tagName = elem.getTagName();
                    String setMethod = "set" + tagName;
                    String name = elem.getTextContent();

                    callMethod(cmdb, setMethod, new Object[] { name }, String.class);

                }
            }
			cmdb.setValidConfigItem(true);
            cmdb.setItemType(getItemType(cmdb));

            returnList.add(cmdb);
        }
		return returnList;
	}

	private List <ChangeOrder> responseToChangeOrderList(String response) {
		List <ChangeOrder> returnList = new ArrayList<>();
		Document doc = responseToDoc(response);
		NodeList instanceNodeList = doc.getElementsByTagName("instance");
		for (int i = 0; i < instanceNodeList.getLength(); i++) {
			NodeList instanceChildNodes = instanceNodeList.item(i).getChildNodes();
			ChangeOrder change = new ChangeOrder();
			for (int j = 0; j < instanceChildNodes.getLength(); j++) {

				Node node = instanceChildNodes.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					String tagName = elem.getTagName();
					if(tagName.equals("header")){
						NodeList headerNodes = node.getChildNodes();
						for(int k = 0; k < headerNodes.getLength(); k++) {
							Node headerNode = headerNodes.item(k);
							Element headerElem = (Element) headerNode;
							String headerTagName = headerElem.getTagName();
							String setMethod = "set" + headerTagName;
							String name = headerElem.getTextContent();

							callMethod(change, setMethod, new Object[] { name }, String.class);

						}
					}

				}
			}
			change.setValidChangeItem(true);

			returnList.add(change);
		}
		return returnList;
	}

	private List <Incident> responseToIncidentList(String response) {
		List <Incident> returnList = new ArrayList<>();
		Document doc = responseToDoc(response);
		NodeList instanceNodeList = doc.getElementsByTagName("instance");
		for (int i = 0; i < instanceNodeList.getLength(); i++) {
			NodeList instanceChildNodes = instanceNodeList.item(i).getChildNodes();
			Incident incident = new Incident();
			for (int j = 0; j < instanceChildNodes.getLength(); j++) {
				Node node = instanceChildNodes.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					String tagName = elem.getTagName();
					String setMethod = "set" + tagName;
					String name = elem.getTextContent();

					callMethod(incident, setMethod, new Object[] { name }, String.class);

				}
			}
			incident.setValidIncidentItem(true);

			returnList.add(incident);
		}
		return returnList;
	}

	/**
	 *
	 * Returns List<ChangeOrder> of Change Orders
	 * @return List<ChangeOrder>
	 */
	private List<ChangeOrder> getChangeOrderList(){
		List<ChangeOrder> changeOrderList;

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
		hpsmSoapModel.setRequestTypeName(hpsmSettings.getChangeOrderRequestType());
		hpsmSoapModel.setSoapAction(hpsmSettings.getChangeOrderSoapAction());

		String soapString = getDefaultSoapMessage(SoapRequestType.CHANGE_ORDER, hpsmSoapModel);

		String response  = makeSoapCall(soapString, hpsmSoapModel);

		changeOrderList = responseToChangeOrderList(response);

		return changeOrderList;
	}

	/**
	 *
	 * Returns List<Incident> of Incidents
	 * @return List<Incident>
	 */
	private List<Incident> getIncidentList(){
		List<Incident> incidentList;

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
		hpsmSoapModel.setRequestTypeName(hpsmSettings.getIncidentRequestType());
		hpsmSoapModel.setSoapAction(hpsmSettings.getIncidentSoapAction());

		String soapString = getDefaultSoapMessage(SoapRequestType.INCIDENT, hpsmSoapModel);

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
     *  Takes a model , methodName, value to be set, and value type and uses reflection to excute model methods.
     * @param target model input
     * @param methodName method to run
     * @param args value for for method
     * @param params class
     * @return result
     */
	private Object callMethod(Object target, String methodName, Object[] args, Class<?>...params){
		Object result;
		Method put = ReflectionUtils.findMethod(target.getClass(), methodName, params);
		if(put != null){
			result = ReflectionUtils.invokeMethod(put, target, args);
		}
		else{
            result = null;
		}
		return result;
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
    private String makeSoapCall(String soapMessageString, HpsmSoapModel hpsmSoapModel){

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

            if("FAILURE".equals(post.getStatusText())){
                LOG.info("Soap Request Failure: " +  post.getStatusCode() + "|response: " +response);
            }

            stopHttpConnection();
        } catch (IOException e) {
            LOG.error("Error while trying to make soap call: " + e);
        }
        return response;

    }

    /**
     * 	Creates a SOAP message string based on HpsmSoapModel
     * @return soap message string
     */
    private String getDefaultSoapMessage(SoapRequestType soapRequestType, HpsmSoapModel hpsmSoapModel){

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

            SOAPBodyElement modelTag = body.addBodyElement(envelope.createName("model","ns", ""));

            SOAPBodyElement keysTag = body.addBodyElement(envelope.createName("keys","ns", ""));

			SOAPBodyElement instanceTag = body.addBodyElement(envelope.createName("instance", "ns", ""));

			if(soapRequestType == SoapRequestType.CMDB) {

				handleCmdbSoapMessage(hpsmSoapModel, envelope, keysTag);

			}
			else if(soapRequestType == SoapRequestType.CHANGE_ORDER){
				handleChangeSoapMessage(keysTag);


			}
			else if(soapRequestType == SoapRequestType.INCIDENT){
				handleIncidentSoapMessage(keysTag);
			}

            modelTag.addChildElement(keysTag);
			if(soapRequestType != SoapRequestType.CHANGE_ORDER) {
				modelTag.addChildElement(instanceTag);
			}

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

		String status = hpsmSoapModel.getStatus();
		String itemType = hpsmSoapModel.getItemType();
		String itemSubType = hpsmSoapModel.getItemSubType();
		String item = hpsmSoapModel.getItem();

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
}