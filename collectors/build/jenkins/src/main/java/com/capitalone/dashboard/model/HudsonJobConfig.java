package com.capitalone.dashboard.model;

/**
 * CollectorItem extension to store the instance, build job and build url.
 */
public class HudsonJobConfig extends CollectorItemConfigHistory {

    protected static final String CURRENT_JOB_NAME = "currentName";
    protected static final String OLD_JOB_NAME = "oldName";
    protected static final String JOB_URL = "jobUrl";
    protected static final String HAS_CONFIG = "hasConfig";


    public String getCurrentJobName() {
        return (String) getChangeMap().get(CURRENT_JOB_NAME);
    }

    public String getOldJobName() {
        return (String) getChangeMap().get(OLD_JOB_NAME);
    }

    public String getJobUrl() {
        return (String) getChangeMap().get(JOB_URL);
    }

    public boolean isHasConfig() {
        return (Boolean) getChangeMap().get(HAS_CONFIG);
    }

    public void setCurrentJobName (String currentJobName) {
        getChangeMap().put(CURRENT_JOB_NAME, currentJobName);
    }

    public void setOldJobName (String oldJobName) {
        getChangeMap().put(OLD_JOB_NAME, oldJobName);
    }
    public void setJobUrl (String jobUrl) {
        getChangeMap().put(JOB_URL, jobUrl);
    }
    public void setHasConfig (boolean value) {
        getChangeMap().put(HAS_CONFIG, value);
    }

}
