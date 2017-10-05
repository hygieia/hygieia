package com.capitalone.dashboard.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import com.google.common.collect.Lists;

/**
 * A client that communicates via REST API calls to jira.
 * <p>
 * Latest REST API: https://docs.atlassian.com/jira/REST/latest/
 * <br>
 * Created against API for Jira 7.x. Should work with 6.x and 5.x.
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
@Component
public class DefaultJiraClient implements JiraClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraClient.class);
	private static final ClientUtil TOOLS = ClientUtil.getInstance();
	
	private static final String TEMPO_TEAMS_REST_SUFFIX = "rest/tempo-teams/1/team";
	private static final String BOARD_TEAMS_REST_SUFFIX = "rest/agile/1.0/board";
	
	private final DateFormat QUERY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static final Set<String> DEFAULT_FIELDS = new HashSet<>();
	static {
		DEFAULT_FIELDS.add("*all,-comment,-watches,-worklog,-votes,-reporter,-creator,-attachment");
	}
	
	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	
	private JiraRestClient client;
	
	@Autowired
	public DefaultJiraClient(FeatureSettings featureSettings, FeatureWidgetQueries featureWidgetQueries, JiraRestClientSupplier restSupplier) {
		this.featureSettings = featureSettings;
		this.featureWidgetQueries = featureWidgetQueries;
		this.client = restSupplier.get();
	}
	
	@Override
	public List<Issue> getIssues(long startTime, int pageStart) {
		List<Issue> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				// example "1900-01-01 00:00"
				String startDateStr = QUERY_DATE_FORMAT.format(new Date(startTime));
				
				String query = featureWidgetQueries.getStoryQuery(startDateStr,
						featureSettings.getJiraIssueTypeNames(), featureSettings.getStoryQuery());
				
				Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(
						query, featureSettings.getPageSize(), pageStart, DEFAULT_FIELDS);
				
				SearchResult sr = promisedRs.claim();

				Iterable<Issue> jiraRawRs = sr.getIssues();
				
				if (jiraRawRs != null) {
					if (LOGGER.isDebugEnabled()) {
						int pageEnd = Math.min(pageStart + getPageSize() - 1, sr.getTotal());
						
						LOGGER.debug(String.format("Processing issues %d - %d out of %d", pageStart, pageEnd, sr.getTotal()));
					}
					
					rt = Lists.newArrayList(jiraRawRs);
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().isPresent() && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				LOGGER.debug("Exception", e);
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt;
	}

	@Override
	public List<BasicProject> getProjects() {
		List<BasicProject> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				Promise<Iterable<BasicProject>> promisedRs = client.getProjectClient().getAllProjects();
				
				Iterable<BasicProject> jiraRawRs = promisedRs.claim();
				if (jiraRawRs != null) {
					rt = Lists.newArrayList(jiraRawRs);
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().isPresent() && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				LOGGER.debug("Exception", e);
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt;
	}

	@Override
	@SuppressWarnings({"PMD.NPathComplexity"})
	public List<Team> getBoards(int startAt, List<Team> result) {
		LOGGER.debug("startAt " + startAt);
		if (StringUtils.isEmpty(featureSettings.getJiraTeamFieldName())) {
			return result;
		}

		try {
			URL url = new URL(featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/")? "" : "/")
					+ BOARD_TEAMS_REST_SUFFIX + "?startAt=" + startAt);
			URLConnection connection = null;

			if (featureSettings.getJiraProxyUrl() != null && !featureSettings.getJiraProxyUrl().isEmpty() && (featureSettings.getJiraProxyPort() != null)) {
				String fullProxyUrl = featureSettings.getJiraProxyUrl() + ":" + featureSettings.getJiraProxyPort();
				URL proxyUrl = new URL(fullProxyUrl);
				URI proxyUri = new URI(proxyUrl.getProtocol(), proxyUrl.getUserInfo(),
						proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getPath(), proxyUrl.getQuery(), null);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort()));
				connection = url.openConnection(proxy);

				if (!StringUtils.isEmpty(featureSettings.getJiraCredentials())) {
					String[] creds = (new String(Base64.decodeBase64(featureSettings.getJiraCredentials()))).split(":");
					final String uname = creds[0];
					final String pword = creds.length > 1? creds[1] : null;
					Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(uname, pword.toCharArray());
						}
					});
					connection.setRequestProperty("Proxy-Authorization", "Basic " + featureSettings.getJiraCredentials());
				}
			} else {
				connection = url.openConnection();
			}

			HttpURLConnection request = (HttpURLConnection) connection;
			request.setRequestProperty("Authorization" , "Basic " + featureSettings.getJiraCredentials());
			request.connect();

			try (InputStreamReader streamReader = new InputStreamReader((InputStream) request.getContent(), Charset.forName("UTF-8"));
				 BufferedReader inReader = new BufferedReader(streamReader)) {
				StringBuilder sb = new StringBuilder();
				int cp;
				while ((cp = inReader.read()) != -1) {
					sb.append((char) cp);
				}

				JSONParser parser = new JSONParser();
				try {
					JSONObject teamsJson = (JSONObject) parser.parse(sb.toString());

					if (teamsJson != null) {
						JSONArray valuesArray = (JSONArray)teamsJson.get("values");
						for (Object obj : valuesArray) {
							String teamId = TOOLS.sanitizeResponse(((JSONObject) obj).get("id"));
							String teamName = TOOLS.sanitizeResponse(getJSONString((JSONObject) obj, "name"));
							String teamType = TOOLS.sanitizeResponse(getJSONString((JSONObject) obj, "type"));
							Team team = new Team(teamId, teamName);
							team.setTeamType(teamType);
							result.add(team);
						}

						boolean isLast = (boolean)teamsJson.get("isLast");

						if (!isLast) {
							getBoards(startAt + 50, result);
						}
					}
				} catch (ParseException pe) {
					LOGGER.error("Parser exception when parsing teams", pe);
				}
			}
		} catch (org.springframework.web.client.RestClientException rce) {
			LOGGER.error("Client exception when loading teams", rce);
			throw rce;
		}  catch (MalformedURLException mfe) {
			LOGGER.error("Malformed url for loading teams", mfe);
		} catch (IOException ioe) {
			LOGGER.error("IOException", ioe);
		} catch (URISyntaxException urie) {
			LOGGER.error("URISyntaxException for Jira connection", urie);
		}

		return result;
	}
	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public List<Team> getTeams() {
	    List<Team> result = new ArrayList<>();
		
	    if (StringUtils.isEmpty(featureSettings.getJiraTeamFieldName())) {
	        return result;
	    }
	    
		try {			
			URL url = new URL(featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/")? "" : "/") 
						+ TEMPO_TEAMS_REST_SUFFIX);
			URLConnection connection = null;
			
			if (featureSettings.getJiraProxyUrl() != null && !featureSettings.getJiraProxyUrl().isEmpty() && (featureSettings.getJiraProxyPort() != null)) {
				String fullProxyUrl = featureSettings.getJiraProxyUrl() + ":" + featureSettings.getJiraProxyPort();
				URL proxyUrl = new URL(fullProxyUrl);
				URI proxyUri = new URI(proxyUrl.getProtocol(), proxyUrl.getUserInfo(),
					proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getPath(), proxyUrl.getQuery(), null);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort()));
				connection = url.openConnection(proxy);

				if (!StringUtils.isEmpty(featureSettings.getJiraCredentials())) {
					String[] creds = (new String(Base64.decodeBase64(featureSettings.getJiraCredentials()))).split(":");
					final String uname = creds[0];
					final String pword = creds.length > 1? creds[1] : null;
					Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(uname, pword.toCharArray());
						}
					});
					connection.setRequestProperty("Proxy-Authorization", "Basic " + featureSettings.getJiraCredentials());
				}
			} else {
				connection = url.openConnection();
			}

			HttpURLConnection request = (HttpURLConnection) connection;
			request.setRequestProperty("Authorization" , "Basic " + featureSettings.getJiraCredentials());
			request.connect();
			
			try (InputStreamReader streamReader = new InputStreamReader((InputStream) request.getContent(), Charset.forName("UTF-8"));
			        BufferedReader inReader = new BufferedReader(streamReader)) {
			    StringBuilder sb = new StringBuilder();	    
    			int cp;
    		    while ((cp = inReader.read()) != -1) {
    				sb.append((char) cp);		      
    		    }
    		    
                JSONParser parser = new JSONParser();
                try {
                    JSONArray teamsJson = (JSONArray) parser.parse(sb.toString());
                    
                    if (teamsJson != null) {
                        for (Object obj : teamsJson) {
                            String teamId = TOOLS.sanitizeResponse(((JSONObject) obj).get("id"));
                            String teamName = TOOLS.sanitizeResponse(getJSONString((JSONObject) obj, "name"));
                            result.add(new Team(teamId, teamName));
                        }
                    }
                } catch (ParseException pe) {
                    LOGGER.error("Parser exception when parsing teams", pe);
                }
			}
        } catch (org.springframework.web.client.RestClientException rce) {
            LOGGER.error("Client exception when loading teams", rce);
            throw rce;
        }  catch (MalformedURLException mfe) {
            LOGGER.error("Malformed url for loading teams", mfe);
        } catch (IOException ioe) {
			LOGGER.error("IOException", ioe);
		} catch (URISyntaxException urie) {
			LOGGER.error("URISyntaxException for Jira connection", urie);
		}
		
		return result;
	}
	
	private String getJSONString(JSONObject obj, String field) {
        return ((String) obj.get(field));
    }

	@Override
	public Issue getEpic(String epicKey) {
		List<Issue> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				String query = this.featureWidgetQueries.getEpicQuery(epicKey, "epic");
				
				Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(
						query, featureSettings.getPageSize(), 0, DEFAULT_FIELDS);
				
				SearchResult sr = promisedRs.claim();
				
				Iterable<Issue> jiraRawRs = sr.getIssues();
				
				if (jiraRawRs != null) {
					rt = Lists.newArrayList(jiraRawRs);
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().isPresent() && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				LOGGER.debug("Exception", e);
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt.isEmpty()? null : rt.iterator().next();
	}
	
	/**
	 * 
	 */
	@Override
	public List<Issue> getEpics(List<String> epicKeys) {
		List<Issue> rt = new ArrayList<>();
		
		if (client != null) {
			try {
				String query = this.featureWidgetQueries.getEpicQuery(epicKeys, "epics");
				
				// This could be paged too
				int total = Integer.MAX_VALUE;
				for (int j = 0; j < total; j += featureSettings.getPageSize()) {

					Promise<SearchResult> promisedRs = client.getSearchClient().searchJql(
							query, featureSettings.getPageSize(), j, DEFAULT_FIELDS);
					
					SearchResult sr = promisedRs.claim();
					total = sr.getTotal();
					
					Iterable<Issue> jiraRawRs = sr.getIssues();
					
					if (jiraRawRs != null) {
						rt.addAll(Lists.newArrayList(jiraRawRs));
					}
				}
			} catch (RestClientException e) {
				if (e.getStatusCode().isPresent() && e.getStatusCode().get() == 401 ) {
					LOGGER.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOGGER.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
				}
				LOGGER.debug("Exception", e);
			}
		} else {
			LOGGER.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}
		
		return rt;
	}
	
	@Override
	public int getPageSize() {
		return featureSettings.getPageSize();
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	@Override
	public Map<String, String> getStatusMapping() {
		Map<String, String> statusMap = new HashMap<>();
		
		try {			
			URL url = new URL(featureSettings.getJiraBaseUrl() + (featureSettings.getJiraBaseUrl().endsWith("/")? "" : "/") 
					+ featureSettings.getJiraQueryEndpoint() + (featureSettings.getJiraQueryEndpoint().endsWith("/")? "" : "/") + "status/");
			URLConnection connection = null;
			
			if (featureSettings.getJiraProxyUrl() != null && !featureSettings.getJiraProxyUrl().isEmpty() && (featureSettings.getJiraProxyPort() != null)) {
				String fullProxyUrl = featureSettings.getJiraProxyUrl() + ":" + featureSettings.getJiraProxyPort();
				URL proxyUrl = new URL(fullProxyUrl);
				URI proxyUri = new URI(proxyUrl.getProtocol(), proxyUrl.getUserInfo(),
					proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getPath(), proxyUrl.getQuery(), null);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort()));
				connection = url.openConnection(proxy);

				if (!StringUtils.isEmpty(featureSettings.getJiraCredentials())) {
					String[] creds = (new String(Base64.decodeBase64(featureSettings.getJiraCredentials()))).split(":");
					final String uname = creds[0];
					final String pword = creds.length > 1? creds[1] : null;
					Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(uname, pword.toCharArray());
						}
					});
					connection.setRequestProperty("Proxy-Authorization", "Basic " + featureSettings.getJiraCredentials());
				}
			} else {
				connection = url.openConnection();
			}

			HttpURLConnection request = (HttpURLConnection) connection;
			request.setRequestProperty("Authorization" , "Basic " + featureSettings.getJiraCredentials());
			request.connect();
			
			InputStream in = (InputStream) request.getContent();
			BufferedReader inReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
			StringBuilder sb = new StringBuilder();
		    
			int cp;
		    while ((cp = inReader.read()) != -1) {
				sb.append((char) cp);		      
		    } 
            JSONParser parser = new JSONParser();

            try {
                JSONArray statuses = (JSONArray) parser.parse(sb.toString());

                for (Object status : statuses) {
                    JSONObject jsonStatus = (JSONObject) status;
                    String statusName = (String) jsonStatus.get("name");
                    
                    // Not added until jira 6. Old versions may still work - they will just have to manually specify 
                    // categories in the jira properties file
                    Object statusCategory = jsonStatus.get("statusCategory");
                    if (statusCategory != null) {
	                    JSONObject jsonStatusCategory = (JSONObject) statusCategory;
	                    String statusCategoryName = (String) jsonStatusCategory.get("name");
						if (statusCategoryName == null) {
							LOGGER.warn("No statusCategory for status : " + statusName);
							continue;
						}
						
						statusMap.put(statusName, statusCategoryName);
                    }
                }
            } catch (ParseException pe) {
                LOGGER.error("Parser exception when parsing statuses", pe);
            } 
        } catch (org.springframework.web.client.RestClientException rce) {
            LOGGER.error("Client exception when loading statuses", rce);
            throw rce;
        }  catch (MalformedURLException mfe) {
            LOGGER.error("Malformed url for loading statuses", mfe);
        } catch (IOException ioe) {
			LOGGER.error("IOException", ioe);
		} catch (URISyntaxException urie) {
			LOGGER.error("URISyntaxException for Jira connection", urie);
		}
		
		return statusMap;
	}
}
