package com.capitalone.dashboard.model;

/**
 * Special use case at AA: Coverity Project is used as a "folder."
 * Coverity Streams within Coverity Project are used for the actual application.
 *
 * Hence a unique "CoverityProject" is identified by
 * Coverity Project AND Coverity Stream.
 *
 * The Class name is misleading.
 */
public class CoverityProject extends CollectorItem {

    // keys for inherited Map<String, Object> options
    private static final String PROJECT_NAME = "projectName";
    private static final String INSTANCE_URL = "instanceUrl";
    private static final String PROJECT_KEY = "projectKey";
    private static final String DATE_CREATED = "dateCreated";
    private static final String DATE_MODIFIED= "dateModified";
    private static final String STREAM = "stream";

    /**
     * Do not use inherited setDescription(String) method from CollectorItem
     * to avoid inconsistent state
     * @param projectName
     * @param stream
     */
    public void setDescription(String projectName, String stream) {
        super.setDescription(projectName+": "+stream);

        setProjectName(projectName);
        setStream(stream);
    }

    public String getProjectName() {
        return (String) getOptions().get(PROJECT_NAME);
    }

    private void setProjectName(String projectName) {
        getOptions().put(PROJECT_NAME, projectName);
    }

	public Long getProjectKey() {
        return (Long) getOptions().get(PROJECT_KEY);
    }

    public void setProjectKey(long projectKey) {
        getOptions().put(PROJECT_KEY, projectKey);
    }

    public Long getDateCreated() {
        return (Long) getOptions().get(DATE_CREATED);
    }

    public void setDateCreated(long dateCreated) {
        getOptions().put(DATE_CREATED, dateCreated);
    }

    public Long getDateModified() {
        return (Long) getOptions().get(DATE_MODIFIED);
    }

    public void setDateModified(long dateModified) {
        getOptions().put(DATE_MODIFIED, dateModified);
    }

    public String getStream() {
        return (String) getOptions().get(STREAM);
    }

    private void setStream(String stream) {
        getOptions().put(STREAM, stream);
    }

    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CoverityProject that = (CoverityProject) o;
		return getProjectKey().equals(that.getProjectKey())
		        && getDateCreated().equals(that.getDateCreated())
		        && getInstanceUrl().equals(that.getInstanceUrl());
	}

	@Override
	public int hashCode() {
		int result = getInstanceUrl().hashCode();
		long dateCreated = getDateCreated();

		result = 31 * result + getDescription().hashCode() + (int) dateCreated;
		return result;
	}

}
