package com.capitalone.dashboard.collector;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.XLDeployApplication;
import com.capitalone.dashboard.model.XLDeployApplicationHistoryItem;
import com.capitalone.dashboard.util.Supplier;

@Component
public class DefaultXLDeployClient implements XLDeployClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXLDeployClient.class);
	
	private static final String UDM_APPLICATION = "udm.Application";
	private static final String UDM_ENVIRONMENT = "udm.Environment";
	
	private final DateFormat FULL_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final DateFormat REST_DATE_INPUT = new SimpleDateFormat("dd MMM yy HH:mm:ss z");
	
	private final XLDeploySettings xlDeploySettings;
	private final RestOperations restOperations;
	
	@Autowired
	public DefaultXLDeployClient(XLDeploySettings xlDeploySettings, Supplier<RestOperations> restOperationsSupplier) {
        this.xlDeploySettings = xlDeploySettings;
        this.restOperations = restOperationsSupplier.get();
	}
	
	@Override
	public List<XLDeployApplication> getApplications(String instanceUrl) {
		List<XLDeployApplication> applications = new ArrayList<>();
		
		ResponseEntity<String> res = makeRestCall(instanceUrl, "repository/query?type=" + UDM_APPLICATION + "&resultsPerPage=-1");
		
		if (res != null) {
			for (Node n : parseAsList(res.getBody(), "ci")) {
				String nRef = attr(n, "ref");
				String nType = attr(n, "type");
				String nName = nRef != null && nRef.matches(".*/[^/]+")? nRef.substring(nRef.lastIndexOf('/') + 1) : null;
				
				XLDeployApplication application = new XLDeployApplication();
				application.setInstanceUrl(instanceUrl);
				application.setApplicationName(nName);
				application.setApplicationId(nRef);
				application.setApplicationType(nType);
				
				applications.add(application);
			}
		}
		
		return applications;
	}
	
	@Override
	public List<Environment> getEnvironments(String instanceUrl) {
		List<Environment> environments = new ArrayList<>();
		
		// http://localhost:4516/deployit/repository/query?type=udm.Environment
		ResponseEntity<String> res = makeRestCall(instanceUrl, "repository/query?type=" + UDM_ENVIRONMENT + "&resultsPerPage=-1");
		
		if (res != null) {
			for (Node n : parseAsList(res.getBody(), "ci")) {
				String nRef = attr(n, "ref");
				String nType = attr(n, "type");
				String nName = nRef != null && nRef.matches(".*/[^/]+")? nRef.substring(nRef.lastIndexOf('/') + 1) : null;
				
				environments.add(new Environment(nRef, nName, nType));
			}
		}
		
		return environments;
	}
	


	@Override
	public List<XLDeployApplicationHistoryItem> getApplicationHistory(XLDeployApplication application, Date startDate, Date endDate) {
		return getApplicationHistory(Collections.singletonList(application), startDate, endDate);
	}
	
	@Override
	@SuppressWarnings({"PMD.NPathComplexity"})
	public List<XLDeployApplicationHistoryItem> getApplicationHistory(List<XLDeployApplication> applications, Date startDate, Date endDate) {
		if (applications == null || applications.isEmpty()) {
			return Collections.<XLDeployApplicationHistoryItem>emptyList();
		}
		
		List<XLDeployApplicationHistoryItem> history = new ArrayList<>();
		
		String paramFilterType = "application";
		String paramBeginDate = REST_DATE_INPUT.format(startDate);
		String paramEndDate = REST_DATE_INPUT.format(endDate);
		
		String body = postFilter(applications);
		
		
		
		ResponseEntity<String> res = makeRestPost(applications.get(0).getInstanceUrl(), 
				"internal/reports/tasks?filterType=" + paramFilterType + "&begin=" + paramBeginDate + "&end=" + paramEndDate, body);
		
		if (res != null) {
			Document doc = doc(res.getBody());
			
			for (Node line : asList(doc.getElementsByTagName("lines"))) {
				Node values = null;
				for (int i = 0; i < line.getChildNodes().getLength(); ++i) {
					Node temp = line.getChildNodes().item(i);
					if (temp.getNodeType() == Node.ELEMENT_NODE && "values".equalsIgnoreCase(temp.getNodeName())) {
						values = temp;
						break;
					}
				}
				Map<String, String> deploymentData = asMap(values.getChildNodes());
				
				XLDeployApplicationHistoryItem historyItem = new XLDeployApplicationHistoryItem();
				historyItem.setEnvironmentName(deploymentData.get("environment"));
				
				String pkg = deploymentData.get("package");
				
				// updates seem to give both packages in play separated by a comma
				if (pkg != null && pkg.indexOf(',') >= 0) {
					pkg = pkg.substring(0, pkg.indexOf(','));
				}
				historyItem.setDeploymentPackage(pkg);
				historyItem.setEnvironmentId(deploymentData.get("environmentId"));
				
				try {
					if (deploymentData.get("completionDate") != null) {
						Date completionDate = FULL_DATE.parse(deploymentData.get("completionDate"));
						historyItem.setCompletionDate(completionDate.getTime());
					}
				} catch (ParseException e) {
					LOGGER.error("Failed to parse date: " + deploymentData.get("completionDate"), e);
				}
				
				historyItem.setType(deploymentData.get("type"));
				historyItem.setUser(deploymentData.get("user"));
				historyItem.setTaskId(deploymentData.get("taskId"));
				
				try {
					if (deploymentData.get("startDate") != null) {
						Date deployStartDate = FULL_DATE.parse(deploymentData.get("startDate"));
						historyItem.setStartDate(deployStartDate.getTime());
					}
				} catch (ParseException e) {
					LOGGER.error("Failed to parse date: " + deploymentData.get("startDate"), e);
				}
				
				historyItem.setStatus(deploymentData.get("status"));
				
				history.add(historyItem);
			}
		}
		
		return history;
	}
	
	// ////// Helpers
	
    private ResponseEntity<String> makeRestCall(String instanceUrl, String endpoint) {
        String url = normalizeUrl(instanceUrl, "/deployit/" + endpoint);
        ResponseEntity<String> response = null;
        try {
            response = restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(instanceUrl)), String.class);

        } catch (RestClientException re) {
            LOGGER.error("Error with REST url: " + url);
            LOGGER.error(re.getMessage());
        }
        return response;
    }
    
    private ResponseEntity<String> makeRestPost(String instanceUrl, String endpoint, Object body) {
        String url = normalizeUrl(instanceUrl, "/deployit/" + endpoint);
        ResponseEntity<String> response = null;
        try {
        	HttpHeaders headers = createHeaders(instanceUrl);
        	headers.setContentType(MediaType.APPLICATION_XML);
        	
            response = restOperations.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);

        } catch (RestClientException re) {
            LOGGER.error("Error with REST url: " + url);
            LOGGER.error(re.getMessage());
        }
        return response;
    }

    private String normalizeUrl(String instanceUrl, String remainder) {
        return StringUtils.removeEnd(instanceUrl, "/") + remainder;
    }

    protected HttpHeaders createHeaders(String instanceUrl) {
    	int idx = xlDeploySettings.getServers().indexOf(instanceUrl);
    	if (idx < 0 || xlDeploySettings.getUsernames() == null || xlDeploySettings.getPasswords() == null) {
    		return new HttpHeaders();
    	}
    	
    	String username = xlDeploySettings.getUsernames().get(idx);
    	String password = xlDeploySettings.getPasswords().get(idx);
    	
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(
                StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }
    
    // for convenience
    private List<Node> parseAsList(String body, String tagname) {
    	if (body == null) {
    		return Collections.<Node>emptyList();
    	}
    	
    	Document doc = doc(body);
    	
    	NodeList nl = null;
    	
    	if (tagname != null) {
    		nl = doc.getElementsByTagName(tagname);
    	} else {
    		nl = doc.getFirstChild().getChildNodes();
    	}

    	List<Node> rt = new ArrayList<Node>(nl.getLength());
    	
    	for (int i = 0; i < nl.getLength(); ++i) {
    		rt.add(nl.item(i));
    	}
    	
    	return rt;
    }
    
    private Document doc(String entity) {
		Reader reader = null;
		
		try {
			reader = new StringReader(entity);

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document d = builder.parse(new InputSource(reader));
			
			return d;
		} catch (ParserConfigurationException e) {
			LOGGER.error("Failed to convert to XML DOC", e);
			LOGGER.debug(entity);
		} catch (SAXException e) {
			LOGGER.error("Failed to convert to XML DOC", e);
			LOGGER.debug(entity);
		} catch (IOException e) {
			LOGGER.error("Failed to convert to XML DOC", e);
			LOGGER.debug(entity);
		} finally {
			if (reader != null) { try {
				reader.close();
			} catch (IOException e) {
				LOGGER.error("Failed to close Reader", e);
			} }
		}
		
		 
		return null;
    }
    
    private String attr(Node n, String attrName) {
    	if (n == null) { 
    		return null; 
    	}
    	
    	return n.getAttributes().getNamedItem(attrName) != null?
    			n.getAttributes().getNamedItem(attrName).getNodeValue() : null;
    }
    
    /**
     * Parses a list of key/value pairs into a map.
     * <pre>
     * &lt;item&gt;
     *   &lt;key&gt;...&lt;/key&gt;
     *   &lt;value&gt;...&lt;/value&gt;
     * &lt;/item&gt;
     * &lt;item&gt;
     *   ...
     * &lt;/item&gt;
     * ...
     * </pre>
     * 
     * @param kvList
     * @return
     */
    private Map<String, String> asMap(NodeList kvList) {
    	Map<String, String> rt = new HashMap<String, String>();
    	
    	for (int i = 0; i < kvList.getLength(); ++i) {
    		Node item = kvList.item(i);
    		NodeList itemChildren = item.getChildNodes();
    		
    		if (item.getNodeType() != Node.ELEMENT_NODE) {
    			continue;
    		}
    		
    		String key = null;
    		String value = null;
    		
    		for (int j = 0; j < itemChildren.getLength(); ++j) {
    			Node child = itemChildren.item(j);
    			
    			if (child.getNodeType() != Node.ELEMENT_NODE) {
        			continue;
        		}
    			
    			if ("key".equalsIgnoreCase(child.getNodeName())) {
    				key = child.getTextContent();
    			} else if ("value".equalsIgnoreCase(child.getNodeName())) {
    				value = child.getTextContent();
    			}
    		}
    		
    		if (key != null && value != null) {
    			rt.put(key, value);
    		} else {
    			LOGGER.error("Invalid K/V pair: key: " + key + ", value: " + value);
    		}
    	}
    	
    	return rt;
    }
    
    //http://stackoverflow.com/questions/19589231/can-i-iterate-through-a-nodelist-using-for-each-in-java
	private List<Node> asList(NodeList n) {
		return n.getLength() == 0 ? Collections.<Node> emptyList() : new NodeListWrapper(n);
	}
	
	private String postFilter(List<XLDeployApplication> application) {
		StringBuilder sb = new StringBuilder();
		
		if (application == null || application.isEmpty()) {
			sb.append("<empty/>");
		} else {
			sb.append("<list>\n");
			
			for (XLDeployApplication app : application) {
				sb.append("  <ci ref=\"" + app.getApplicationName() + "\" type=\"" + app.getApplicationType() + "\"/>\n");
			}
			
			sb.append("</list>");
		}
		
		return sb.toString();
	}
    
    //http://stackoverflow.com/questions/19589231/can-i-iterate-through-a-nodelist-using-for-each-in-java
	static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
		private final NodeList list;

		NodeListWrapper(NodeList l) {
			list = l;
		}

		public Node get(int index) {
			return list.item(index);
		}

		public int size() {
			return list.getLength();
		}
	}
}
