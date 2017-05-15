package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.HpsmSoapModel;
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
import java.util.ArrayList;
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
    private HttpClient httpclient = new HttpClient(manager);
    private boolean usedClient = false;
    private int port;

    private String strURL;
    private String protocol;
    private String server;
    private String resource;
    private String contentType;
    private String charset;
    private String userName = "";
    private String password = "";

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

		return cmdbList;
	}

	/**
	 *
	 * Returns List<Cmdb> of Apps
	 * @return List<Cmdb>
	 */
	private List<Cmdb> getAppList(){
		List<Cmdb> appList;

		HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
        hpsmSoapModel.setConfigurationItemSubType(hpsmSettings.getAppSubType());
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
        hpsmSoapModel.setStatus(hpsmSettings.getAppStatus());

        String soapString = getDefaultSoapMessage(hpsmSoapModel);

		String response = makeSoapCall(soapString, hpsmSoapModel);

		appList = responseToDetailsList(response);

		return appList;
	}

	/**
	 * Takes HpsmSoapModel and sets model settings based on properties file
	 *
	 * @return  Returns List<Cmdb> of Components
	 */
	private List<Cmdb> getComponentList(){
		List<Cmdb> componentList;
        HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();

		hpsmSoapModel.setConfigurationItemSubType(hpsmSettings.getCompSubType());
        hpsmSoapModel.setConfigurationItemType(hpsmSettings.getCompType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getDetailsSoapAction());
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getDetailsRequestType());
        hpsmSoapModel.setStatus(hpsmSettings.getAppStatus());

		String soapString = getDefaultSoapMessage(hpsmSoapModel);

		String response  = makeSoapCall(soapString, hpsmSoapModel);

        componentList = responseToDetailsList(response);

        return componentList;
	}

	/**
	 *  Going from response to List<Cmdb> of APPs
	 * @param response SOAP response required for creation of List<Cmdb>
	 * @return List<Cmdb>
	 */
	private List<Cmdb> responseToList(String response) {

		Document doc = responseToDoc(response);
		List <Cmdb> returnList = new ArrayList<>();

    	NodeList nodeList = doc.getElementsByTagName("ConfigurationItem");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Cmdb cmdb = new Cmdb();
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				String name = elem.getTextContent();
					cmdb.setConfigurationItem(name);
			}
			returnList.add(cmdb);
		}


		return returnList;
	}


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
            returnList.add(cmdb);
        }
		return returnList;
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

    /**
     * Ends SOAP Connection
     */
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
     *  Makes SOAP request for given soap message
     * @param soapMessageString Generated SOAP ready for POST
     * @param hpsmSoapModel hpsmSoapModel
     * @return Soap response
     */
    private String makeSoapCall(String soapMessageString, HpsmSoapModel hpsmSoapModel){

        String requestAction = hpsmSoapModel.getSoapAction();
        String response = "";
        contentType = hpsmSettings.getApiContentType();
        charset = hpsmSettings.getApiCharset();

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
        } catch (java.io.IOException e) {
            LOG.error("Error while trying to make soap call: " + e);
        }
        return response;

    }

    /**
     * 	Creates a SOAP message string based on HpsmSoapModel
     * @return soap message string
     */
    private String getDefaultSoapMessage(HpsmSoapModel hpsmSoapModel){

        String strMsg = "";
        SOAPMessage soapMsg;
        String status = hpsmSoapModel.getStatus();
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
            QName name1 = new QName("count");
			requestType.addAttribute(name1,"10");
            SOAPBodyElement modelTag = body.addBodyElement(envelope.createName("model","ns", ""));

            SOAPBodyElement keysTag = body.addBodyElement(envelope.createName("keys","ns", ""));

            SOAPBodyElement instanceTag = body.addBodyElement(envelope.createName("instance","ns", ""));

            if(itemType != null && !itemType.isEmpty() ){
                SOAPBodyElement configItemType= body.addBodyElement(envelope.createName("ConfigerationItemType","ns", ""));
                configItemType.addTextNode(itemType);
				keysTag.addChildElement(configItemType);

            }
            if(itemSubType != null && !itemSubType.isEmpty() ){
                SOAPBodyElement configItemSubType= body.addBodyElement(envelope.createName("ConfigurationItemSubType","ns", ""));
                configItemSubType.addTextNode(itemSubType);
				keysTag.addChildElement(configItemSubType);
            }
            if(item != null && !item.isEmpty() ){

                SOAPBodyElement configItem= body.addBodyElement(envelope.createName("ConfigurationItem","ns", ""));
                configItem.addTextNode(item);
				keysTag.addChildElement(configItem);

            }
            if(item != null && !item.isEmpty() ){

                SOAPBodyElement configItemStatus= body.addBodyElement(envelope.createName("Status","ns", ""));
                configItemStatus.addTextNode(item);
				keysTag.addChildElement(configItemStatus);

            }

            modelTag.addChildElement(keysTag);
            modelTag.addChildElement(instanceTag);
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

}
