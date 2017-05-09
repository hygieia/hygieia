package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.HpsmCollector;
import com.capitalone.dashboard.model.HpsmSoapModel;
import com.capitalone.dashboard.util.Supplier;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.apache.commons.codec.binary.Base64;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    //TODO: Set details to model for component and app
    //TODO: return to data model in API for insert

    private static final Log LOG = LogFactory.getLog(DefaultHpsmClient.class);

	private final HpsmSettings hpsmSettings;

	private final RestOperations restOperations;

	protected String strURL;
	protected PostMethod post;
	protected SimpleHttpConnectionManager manager = new SimpleHttpConnectionManager(true);
	protected HttpClient httpclient = new HttpClient(manager);
	protected boolean usedClient = false;
	protected String protocol;
	protected int port;
	protected String server;

	protected String resource;
	protected String contentType;
	protected String charset;
	protected String userName = "";
	protected String password = "";

	@Autowired
	public DefaultHpsmClient(HpsmSettings hpsmSettings,
							 Supplier<RestOperations> restOperationsSupplier) {
		this.hpsmSettings = hpsmSettings;
		this.restOperations = restOperationsSupplier.get();
	}

	@Override
	public List<HpsmCollector> getApps() {

		List<HpsmCollector> collection = new ArrayList<>();
        collection = getAppList();
        LOG.info("Collection list count First" + collection.size());
        collection.addAll(getComponentList());
        LOG.info("Collection list count" + collection.size());
        //updateOwnerInfo(appList);
		return collection;
	}

	/**
	 * rename to app
	 * Returns List<HpsmCollector> of Apps
	 * @return
	 */
	private List<HpsmCollector> getAppList(){
		List<HpsmCollector> appList = new ArrayList<>();

		HpsmSoapModel model = new HpsmSoapModel();
		model.setConfigurationItemSubType(hpsmSettings.getAppSubType());
		model.setRequestTypeName(hpsmSettings.getAppRequestType());
		model.setSoapAction(hpsmSettings.getAppSoapAction());

		String soapString = getDefaultSoapMessage(model);

		appList = makeSoapCall(soapString, model);

		//updateOwnerInfo(appList);

		return appList;
	}

	private void updateOwnerInfo(List<HpsmCollector> itemList) {
		HpsmSoapModel model = new HpsmSoapModel();
		model.setConfigurationItem("ASVWEAVE");
		model.setRequestTypeName("RetrieveDeviceListRequest");
		model.setSoapAction("RetrieveList");
		String soapString = getDefaultSoapMessage(model);
		LOG.info(soapString);
        itemList = makeSoapCall(soapString, model);
	}

	/**
	 *
	 *  Returns List<HpsmCollector> of Components
	 * @return
	 */
	private List<HpsmCollector> getComponentList(){
		List<HpsmCollector> componentList = new ArrayList<>();
		HpsmSoapModel model = new HpsmSoapModel();
		model.setConfigurationItemSubType(hpsmSettings.getCompSubType());
		model.setConfigurationItemType(hpsmSettings.getCompType());
		model.setSoapAction(hpsmSettings.getCompSoapAction());
		model.setRequestTypeName(hpsmSettings.getCompRequestType());

		String soapString = getDefaultSoapMessage(model);
        componentList = makeSoapCall(soapString, model);
		return componentList;
	}

	/**
	 *  Makes SOAP request for given soap message
	 * @param soapMessageString
	 * @param model
	 * @return
	 */
	private List<HpsmCollector> makeSoapCall(String soapMessageString, HpsmSoapModel model){
		List<HpsmCollector> returnList = new ArrayList<>();
		String requestAction = model.getSoapAction();

		contentType = hpsmSettings.getApiContentType();
		charset = hpsmSettings.getApiCharset();
        LOG.info(soapMessageString);
		try {
			startHttpConnection();

			RequestEntity entity = new StringRequestEntity(
					soapMessageString, contentType, charset);
			post.setRequestEntity(entity);

			post.setRequestHeader("SOAPAction", requestAction);

			httpclient.executeMethod(post);

			LOG.info("Status: " +post.getStatusCode());
			String response = getResponseString(post.getResponseBodyAsStream());
			LOG.info(response);

			post.releaseConnection();
			Document doc = responseToDoc(response);
			returnList = docToList(doc);

			stopHttpConnection();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return returnList;

	}


	/**
	 *  Going from doc to List<HpsmCollector>
	 * @param doc
	 * @return
	 */
	private List<HpsmCollector> docToList(Document doc) {
		List<HpsmCollector> returnList = new ArrayList<>();


		List<String> fieldList = new ArrayList<>();
        fieldList = hpsmSettings.getFieldList();

		for(String key: fieldList){

            NodeList nodeList = doc.getElementsByTagName(key);

            if(nodeList != null && nodeList.getLength() > 0){
                for (int i = 0; i < nodeList.getLength(); i++) {
                    HpsmCollector model = new HpsmCollector();
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) node;
                        String name = elem.getTextContent();
                        model.setAppName(name);
                        returnList.add(model);
                    }
                }
            }else{
                LOG.info("Empty Node Element For: " +key);
            }

        }



//
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			HpsmCollector model = new HpsmCollector();
//
//			Node node = nodeList.item(i);
//
//			if (node.getNodeType() == Node.ELEMENT_NODE) {
//				Element elem = (Element) node;
//				String name = elem.getTextContent();
//				model.setAppName(name);
//                returnList.add(model);
//			}
//
//		}
		LOG.info("CI SIZE after: " +returnList.size());
		return returnList;
	}

	/**
	 *  Converts String response into document for parsing
	 * @param response
	 * @return
	 */
	private Document responseToDoc(String response){

		Document doc = null;

		try {

			DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
			DocumentBuilderFactory.newInstance();
			DocumentBuilder builder =  factory.newDocumentBuilder();
			ByteArrayInputStream input =  new ByteArrayInputStream(response.toString().getBytes("UTF-8"));
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
	protected void startHttpConnection(){
		server = hpsmSettings.getApiServer();
		port = hpsmSettings.getApiPort();
		protocol = hpsmSettings.getApiProtocol();
		resource = hpsmSettings.getApiResource();
		userName = hpsmSettings.getApiUser();
		password = hpsmSettings.getApiPass();

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

	private void stopHttpConnection() {
		try{
			if(post != null && usedClient){
				post.releaseConnection();
			}
			if(manager != null && usedClient){
				manager.shutdown();
			}
		}
		catch(Throwable t){
			t.printStackTrace();
			LOG.error("Error while trying to close http Connection: " + t);
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
     * 	Creates SOAP message based of HpsmSoapModel
     * @return
     */
    private String getDefaultSoapMessage(HpsmSoapModel hpsmSoapModel){

        String strMsg = "";
        SOAPMessage soapMsg = null;
        String itemType = hpsmSoapModel.getConfigurationItemType();
        String itemSubType = hpsmSoapModel.getConfigurationItemSubType();
        String item = hpsmSoapModel.getConfigurationItem();
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

            SOAPBodyElement model = body.addBodyElement(envelope.createName("model","ns", ""));


            SOAPBodyElement keys = body.addBodyElement(envelope.createName("keys","ns", ""));

            SOAPBodyElement instance = body.addBodyElement(envelope.createName("instance","ns", ""));

            if(itemType != null && !itemType.isEmpty() ){
                SOAPBodyElement configItemType= body.addBodyElement(envelope.createName("ConfigerationItemType","ns", ""));
                configItemType.addTextNode(itemType);
                instance.addChildElement(configItemType);

            }
            if(itemSubType != null && !itemSubType.isEmpty() ){
                SOAPBodyElement configItemSubType= body.addBodyElement(envelope.createName("ConfigurationItemSubType","ns", ""));
                configItemSubType.addTextNode(itemSubType);
                instance.addChildElement(configItemSubType);
            }
            if(item != null && !item.isEmpty() ){

                SOAPBodyElement configItem= body.addBodyElement(envelope.createName("ConfigurationItem","ns", ""));
                configItem.addTextNode(item);
                keys.addChildElement(configItem);

            }
            model.addChildElement(keys);
            model.addChildElement(instance);
            requestType.addChildElement(model);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMsg.writeTo(out);
            strMsg = new String(out.toByteArray());

        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strMsg;
    }

	private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateInstance);
		cal.add(Calendar.DATE, offsetDays);
		cal.add(Calendar.MINUTE, offsetMinutes);
		return cal.getTime();
	}

	private ResponseEntity<String> makeRestCall(String url, String userId,
												String password) {
		// Basic Auth only.
		if (!"".equals(userId) && !"".equals(password)) {
			return restOperations.exchange(url, HttpMethod.GET,
					new HttpEntity<>(createHeaders(userId, password)),
					String.class);

		} else {
			return restOperations.exchange(url, HttpMethod.GET, null,
					String.class);
		}

	}

	private HttpHeaders createHeaders(final String userId, final String password) {
		String auth = userId + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
		String authHeader = "Basic " + new String(encodedAuth);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		return headers;
	}
}
