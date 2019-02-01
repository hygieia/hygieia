package com.capitalone.dashboard.model;

public class FortifyProject extends CollectorItem{

	private static final String INSTANCE_URL = "instanceUrl";
	private static final String PROJECT_NAME = "projectName";
	private static final String PROJECT_ID = "projectId";
	private static final String PROJECT_VERSION = "projectVersion";
	private static final String VERSION_ID = "versionID";

	public String getVersionId() {
		return (String) getOptions().get(VERSION_ID);
	}

	public void setVersionId(String versionId) {
		getOptions().put(VERSION_ID, versionId);
	}
	
	public String getProjectVersion() {
		return (String) getOptions().get(PROJECT_VERSION);
	}

	public void setProjectVersion(String projectVersion) {
		getOptions().put(PROJECT_VERSION, projectVersion);
	}
	
	public String getInstanceUrl() {
		return (String) getOptions().get(INSTANCE_URL);
	}

	public void setInstanceUrl(String instanceUrl) {
		getOptions().put(INSTANCE_URL, instanceUrl);
	}

	public String getProjectId() {
		return (String) getOptions().get(PROJECT_ID);
	}

	public void setProjectId(String id) {
		getOptions().put(PROJECT_ID, id);
	}

	public String getProjectName() {
		return (String) getOptions().get(PROJECT_NAME);
	}

	public void setProjectName(String name) {
		getOptions().put(PROJECT_NAME, name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		FortifyProject that = (FortifyProject) o;
		return getProjectId().equals(that.getProjectId()) && getInstanceUrl().equals(that.getInstanceUrl()) && getVersionId().equals(that.getVersionId());
	}

	@Override
	public int hashCode() {
		int result = getInstanceUrl().hashCode();
		result = 31 * result + getVersionId().hashCode();
		return result;
	}
}
