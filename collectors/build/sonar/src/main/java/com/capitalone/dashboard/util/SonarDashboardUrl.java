package com.capitalone.dashboard.util;

public class SonarDashboardUrl {

	private static final String SLASH = "/";
	private static final String PATH = "dashboard/index/";
	
	private String projectUrl;
	private String instanceId;
	
	public SonarDashboardUrl(String projectUrl, String instanceId) {
		this.projectUrl = projectUrl;
		this.instanceId = instanceId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(projectUrl);
		if(!projectUrl.endsWith(SLASH)) {
			sb.append(SLASH);
		}
		
		sb.append(PATH)
		  .append(instanceId);
		return sb.toString();
	}
}
