package com.capitalone.dashboard.request;


import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardRemoteRequest {
    @Valid
    private DashboardMetaData metaData;

    @Valid
    private List<FeatureEntry> featureEntries = new ArrayList<>();

    @Valid
    private List<CodeRepoEntry> codeRepoEntries = new ArrayList<>();

    @Valid
    private List<BuildEntry> buildEntries = new ArrayList<>();

    @Valid
    private List<StaticCodeEntry> staticCodeEntries = new ArrayList<>();

    @Valid
    private List<SecurityScanEntry> securityScanEntries = new ArrayList<>();

    @Valid
    private List<DeploymentEntry> deploymentEntries = new ArrayList<>();

    @Valid
    private List<LibraryScanEntry> libraryScanEntries = new ArrayList<>();

    @Valid
    private List<FunctionalTestEntry> functionalTestEntries = new ArrayList<>();

    /**
     * Dashboard Metadata
     */

    public static class DashboardMetaData {
        @NotNull
        private String template;

        @NotNull
        private String type;

        @NotNull
        @Size(min = 6, max = 50)
        @Pattern(message = "Special character(s) found", regexp = "^[a-zA-Z0-9 ]*$")
        private String title;

        @NotNull
        private String applicationName;

        @NotNull
        private String componentName;

        @NotNull
        Owner owner;

        private String businessService;

        private String businessApplication;

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getComponentName() {
            return componentName;
        }

        public void setComponentName(String componentName) {
            this.componentName = componentName;
        }

        public String getBusinessService() {
            return businessService;
        }

        public void setBusinessService(String businessService) {
            this.businessService = businessService;
        }

        public String getBusinessApplication() {
            return businessApplication;
        }

        public void setBusinessApplication(String businessApplication) {
            this.businessApplication = businessApplication;
        }

        public Owner getOwner() {
            return owner;
        }

        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * An abstract class to hold the entries: Jira project, github project, build job etc.
     */

    public static abstract class Entry {
        @NotNull
        String toolName;

        public abstract CollectorType getType();

        public String getToolName() {
            return toolName;
        }

        public void setToolName(String toolName) {
            this.toolName = toolName;
        }

        public abstract CollectorItem toCollectorItem();

        public abstract String getWidgetId();

        public abstract String getWidgetName();

        public abstract Map<String, Object> toWidgetOptions();

    }

    /**
     * Details for creating Feature widget
     */
    public class FeatureEntry extends Entry {

        @Override
        public CollectorType getType() {
            return CollectorType.AgileTool;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }
    }

    /**
     * Details for creating Code Repo widget
     */
    public static class CodeRepoEntry extends Entry {
        @NotNull
        String url;
        String branch;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        @Override
        public CollectorType getType() {
            return CollectorType.SCM;
        }

        @Override
        public CollectorItem toCollectorItem() {
            CollectorItem collectorItem = new CollectorItem();
            collectorItem.setEnabled(false);
            collectorItem.setPushed(true);
            collectorItem.setLastUpdated(System.currentTimeMillis());
            collectorItem.getOptions().put("url", url);
            collectorItem.getOptions().put("branch", branch);
            collectorItem.getOptions().put("scm", toolName);
            return collectorItem;
        }

        @Override
        public String getWidgetId() {
            return "repo0";
        }

        @Override
        public String getWidgetName() {
            return "repo";
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            Map<String, Object> options = new HashMap<>();
            options.put("name", "repo");
            options.put("id", "repo0");
            options.put("url", url);
            if (!StringUtils.isEmpty(branch)) {
                options.put("branch", branch);
            }
            Map<String, String> scm = new HashMap<>();
            scm.put("name", toolName);
            scm.put("value", toolName);
            options.put("scm", scm);
            return options;
        }
    }

    /**
     * Details for creating Build widget
     */
    public class BuildEntry extends Entry {
        @NotNull
        String jobUrl;
        @NotNull
        String instanceUrl;


        public String getJobUrl() {
            return jobUrl;
        }

        public void setJobUrl(String jobUrl) {
            this.jobUrl = jobUrl;
        }

        public String getInstanceUrl() {
            return instanceUrl;
        }

        public void setInstanceUrl(String instanceUrl) {
            this.instanceUrl = instanceUrl;
        }

        @Override
        public CollectorType getType() {
            return CollectorType.Build;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }

    }

    /**
     * Details for creating Static Code Analysis in Code Quality Widget
     */

    public class StaticCodeEntry extends Entry {
        @NotNull
        String instanceUrl;
        @NotNull
        String projectName;
        @NotNull
        String projectId;


        public String getInstanceUrl() {
            return instanceUrl;
        }

        public void setInstanceUrl(String instanceUrl) {
            this.instanceUrl = instanceUrl;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        @Override
        public CollectorType getType() {
            return CollectorType.CodeQuality;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }

    }

    /**
     * Entry to create Library Scan in Code Quality Widget
     *
     */
    public class LibraryScanEntry extends Entry {

        @Override
        public CollectorType getType() {
            return CollectorType.LibraryPolicy;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }

    }

    /**
     * Entry to create Security Scan in Code Quality Widget
     */
    public class SecurityScanEntry extends Entry {

        @Override
        public CollectorType getType() {
            return CollectorType.StaticSecurityScan;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }
    }

    /**
     * Entry to create Functional Test in Code Quality Widget
     */
    public class FunctionalTestEntry extends Entry {

        @Override
        public CollectorType getType() {
            return CollectorType.Test;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }
    }

    /**
     * Entry to create Deployment widget
     */
    public class DeploymentEntry extends Entry {

        @Override
        public CollectorType getType() {
            return CollectorType.Deployment;
        }

        @Override
        public CollectorItem toCollectorItem() {
            return null;
        }

        @Override
        public String getWidgetId() {
            return null;
        }

        @Override
        public String getWidgetName() {
            return null;
        }

        @Override
        public Map<String, Object> toWidgetOptions() {
            return null;
        }

    }

    // Getters and setters

    public DashboardMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(DashboardMetaData dashboardRequest) {
        this.metaData = dashboardRequest;
    }

    public List<FeatureEntry> getFeatureEntries() {
        return featureEntries;
    }

    public void setFeatureEntries(List<FeatureEntry> featureEntries) {
        this.featureEntries = featureEntries;
    }

    public List<CodeRepoEntry> getCodeRepoEntries() {
        return codeRepoEntries;
    }

    public void setCodeRepoEntries(List<CodeRepoEntry> codeRepoEntries) {
        this.codeRepoEntries = codeRepoEntries;
    }

    public List<BuildEntry> getBuildEntries() {
        return buildEntries;
    }

    public void setBuildEntries(List<BuildEntry> buildEntries) {
        this.buildEntries = buildEntries;
    }



    public List<Entry> getAllEntries() {
        List<Entry> all = new ArrayList<>();
        all.addAll(buildEntries);
        all.addAll(codeRepoEntries);
        all.addAll(staticCodeEntries);
        all.addAll(libraryScanEntries);
        all.addAll(securityScanEntries);
        all.addAll(functionalTestEntries);
        all.addAll(deploymentEntries);
        return all;
    }
}