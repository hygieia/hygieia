package jenkins.plugins.hygieia;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hygieia.utils.HygieiaUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class HygieiaPublisher extends Notifier {

    private static final Logger logger = Logger.getLogger(HygieiaPublisher.class.getName());

    private HygieiaBuild hygieiaBuild;
    private HygieiaTest hygieiaTest;
    private HygieiaArtifact hygieiaArtifact;
    private HygieiaSonar hygieiaSonar;
    private HygieiaDeploy hygieiaDeploy;

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public HygieiaBuild getHygieiaBuild() {
        return hygieiaBuild;
    }


    public HygieiaTest getHygieiaTest() {
        return hygieiaTest;
    }

    public HygieiaArtifact getHygieiaArtifact() {
        return hygieiaArtifact;
    }

    public HygieiaSonar getHygieiaSonar() {
        return hygieiaSonar;
    }

    public HygieiaDeploy getHygieiaDeploy() {
        return hygieiaDeploy;
    }

    public static class HygieiaArtifact {
        private final String artifactName;
        private final String artifactDirectory;
        private final String artifactGroup;
        private final String artifactVersion;

        @DataBoundConstructor
        public HygieiaArtifact(String artifactDirectory, String artifactName, String artifactGroup, String artifactVersion) {
            this.artifactDirectory = artifactDirectory;
            this.artifactName = artifactName;
            this.artifactGroup = artifactGroup;
            this.artifactVersion = artifactVersion;
        }

        public String getArtifactName() {
            return artifactName;
        }

        public String getArtifactDirectory() {
            return artifactDirectory;
        }

        public String getArtifactGroup() {
            return artifactGroup;
        }

        public String getArtifactVersion() {
            return artifactVersion;
        }

        public boolean checkFileds() {
            return (!"".equals(artifactName));
        }
    }

    public static class HygieiaDeploy {
        private final String artifactName;
        private final String artifactDirectory;
        private final String artifactGroup;
        private final String artifactVersion;
        private final String applicationName;
        private final String environmentName;
        private final boolean publishDeployStart;

        @DataBoundConstructor
        public HygieiaDeploy(String artifactDirectory, String artifactName, String artifactGroup, String artifactVersion, String applicationName, String environmentName, boolean publishDeployStart) {
            this.artifactDirectory = artifactDirectory;
            this.artifactName = artifactName;
            this.artifactGroup = artifactGroup;
            this.artifactVersion = artifactVersion;
            this.applicationName = applicationName;
            this.environmentName = environmentName;
            this.publishDeployStart = publishDeployStart;
        }

        public String getArtifactName() {
            return artifactName;
        }

        public String getArtifactDirectory() {
            return artifactDirectory;
        }

        public String getArtifactGroup() {
            return artifactGroup;
        }

        public String getArtifactVersion() {
            return artifactVersion;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public String getEnvironmentName() {
            return environmentName;
        }

        public boolean isPublishDeployStart() {
            return publishDeployStart;
        }
    }

    public static class HygieiaBuild {
        private final boolean publishBuildStart;

        @DataBoundConstructor
        public HygieiaBuild(boolean publishBuildStart) {
            this.publishBuildStart = publishBuildStart;
        }

        public boolean isPublishBuildStart() {
            return publishBuildStart;
        }

    }

    public static class HygieiaSonar {
        private final boolean publishBuildStart;

        //Sonar 5.2+ changes: get query interval and max attempts from config
        private final String ceQueryIntervalInSeconds;
        private final String ceQueryMaxAttempts;


        @DataBoundConstructor
        public HygieiaSonar(boolean publishBuildStart, String ceQueryIntervalInSeconds, String ceQueryMaxAttempts) {
            this.publishBuildStart = publishBuildStart;
            this.ceQueryIntervalInSeconds = ceQueryIntervalInSeconds;
            this.ceQueryMaxAttempts = ceQueryMaxAttempts;
        }

        public boolean isPublishBuildStart() {
            return publishBuildStart;
        }

        /**
         * Sonar 5.2+ changes: get query interval from config
         * If value is empty or null - return 10 (recommended value from SonarQube)
         *
         * @return max number of attempts to query Sonar CE API (10 if blank)
         */
        public String getCeQueryIntervalInSeconds() {
            return ceQueryIntervalInSeconds;
        }

        /**
         * Sonar 5.2+ changes: get query max attempts from config
         * If value is empty or null - return 30 (recommended value from SonarQube)
         *
         * @return max number of attempts to query Sonar CE API (30 if blank)
         */
        public String getCeQueryMaxAttempts() {
            return ceQueryMaxAttempts;
        }

    }

    public static class HygieiaTest {
        private final boolean publishTestStart;
        private final boolean publishEvenBuildFails;
        private final String testFileNamePattern;
        private final String testResultsDirectory;
        private final String testType;
        private final String testApplicationName;
        private final String testEnvironmentName;

        @DataBoundConstructor
        public HygieiaTest(boolean publishTestStart, boolean publishEvenBuildFails, String testFileNamePattern, String testResultsDirectory, String testType, String testApplicationName, String testEnvironmentName) {
            this.publishTestStart = publishTestStart;
            this.publishEvenBuildFails = publishEvenBuildFails;
            this.testFileNamePattern = testFileNamePattern;
            this.testResultsDirectory = testResultsDirectory;
            this.testType = testType;
            this.testApplicationName = testApplicationName;
            this.testEnvironmentName = testEnvironmentName;
        }

        public boolean isPublishTestStart() {
            return publishTestStart;
        }

        public boolean isPublishEvenBuildFails() {
            return publishEvenBuildFails;
        }

        public String getTestFileNamePattern() {
            return testFileNamePattern;
        }


        public String getTestResultsDirectory() {
            return testResultsDirectory;
        }

        public String getTestType() {
            return testType;
        }

        public String getTestApplicationName() {
            return testApplicationName;
        }

        public String getTestEnvironmentName() {
            return testEnvironmentName;
        }
    }

        public static class GenericCollectorItem extends AbstractDescribableImpl<GenericCollectorItem> {
//    public static class GenericCollectorItem {
        public final String toolName;
        public final String pattern;


        @DataBoundConstructor
        public GenericCollectorItem(String toolName, String pattern) {
            this.toolName = toolName;
            this.pattern = pattern;
        }

        public String getToolName() {
            return toolName;
        }

        public String getPattern() {
            return pattern;
        }

        @Extension
        public static class DescriptorImpl extends Descriptor<GenericCollectorItem> {
            @Override
            public String getDisplayName() {
                return "";
            }
        }

    }

    @DataBoundConstructor
    public HygieiaPublisher(final HygieiaBuild hygieiaBuild,
                            final HygieiaTest hygieiaTest, final HygieiaArtifact hygieiaArtifact, final HygieiaSonar hygieiaSonar, final HygieiaDeploy hygieiaDeploy) {
        super();
        this.hygieiaBuild = hygieiaBuild;
        this.hygieiaTest = hygieiaTest;
        this.hygieiaArtifact = hygieiaArtifact;
        this.hygieiaSonar = hygieiaSonar;
        this.hygieiaDeploy = hygieiaDeploy;
    }


    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public HygieiaService newHygieiaService(AbstractBuild r, BuildListener listener) {
        EnvVars env;
        try {
            env = r.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
            env = new EnvVars();
        }
        return makeService(env);
    }

    private HygieiaService makeService(EnvVars env) {
        String hygieiaAPIUrl = getDescriptor().getHygieiaAPIUrl();
        String hygieiaToken = getDescriptor().getHygieiaToken();
        String hygieiaJenkinsName = getDescriptor().getHygieiaJenkinsName();
        boolean useProxy = getDescriptor().isUseProxy();
        hygieiaAPIUrl = env.expand(hygieiaAPIUrl);
        hygieiaToken = env.expand(hygieiaToken);
        hygieiaJenkinsName = env.expand(hygieiaJenkinsName);
        return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)  {
        return true;
    }


    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public String hygieiaAPIUrl;
        public String hygieiaAppUrl;
        public String hygieiaToken;
        public String hygieiaJenkinsName;
        public String hygieiaExcludeJobNames;
        public boolean useProxy;
        public boolean hygieiaPublishBuildDataGlobal;
        public boolean hygieiaPublishSonarDataGlobal;
        public boolean showConsoleOutput;
        public List<GenericCollectorItem> hygieiaPublishGenericCollectorItems;
        public String pluginVersionInfo;

        public DescriptorImpl() {
            load();
        }

        public String getHygieiaAPIUrl() {
            return hygieiaAPIUrl;
        }

        public String getHygieiaAppUrl() {
            return hygieiaAppUrl;
        }    

        public String getHygieiaToken() {
            return hygieiaToken;
        }

        public String getHygieiaJenkinsName() {
            return hygieiaJenkinsName;
        }

        public String getHygieiaExcludeJobNames() { return hygieiaExcludeJobNames; }

        public boolean isUseProxy() {
            return useProxy;
        }

        public boolean isHygieiaPublishBuildDataGlobal() {
            return hygieiaPublishBuildDataGlobal;
        }

        public boolean isHygieiaPublishSonarDataGlobal() {
            return hygieiaPublishSonarDataGlobal;
        }

        public boolean isShowConsoleOutput() { return showConsoleOutput; }

        public String getPluginVersionInfo() {
            return StringUtils.isNotEmpty(pluginVersionInfo) ? pluginVersionInfo : this.getPlugin().getShortName()+" version "+this.getPlugin().getVersion(); }

        public List<GenericCollectorItem> getHygieiaPublishGenericCollectorItems() {
            return hygieiaPublishGenericCollectorItems;
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public HygieiaPublisher newInstance(StaplerRequest sr, JSONObject json) {
            HygieiaBuild hygieiaBuild = sr.bindJSON(HygieiaBuild.class, (JSONObject) json.get("hygieiaBuild"));
            HygieiaArtifact hygieiaArtifact = sr.bindJSON(HygieiaArtifact.class, (JSONObject) json.get("hygieiaArtifact"));
            HygieiaTest hygieiaTest = sr.bindJSON(HygieiaTest.class, (JSONObject) json.get("hygieiaTest"));
            HygieiaSonar hygieiaSonar = sr.bindJSON(HygieiaSonar.class, (JSONObject) json.get("hygieiaSonar"));
            HygieiaDeploy hygieiaDeploy = sr.bindJSON(HygieiaDeploy.class, (JSONObject) json.get("hygieiaDeploy"));
            return new HygieiaPublisher(hygieiaBuild, hygieiaTest, hygieiaArtifact, hygieiaSonar, hygieiaDeploy);
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException {
            sr.bindJSON(this, formData.getJSONObject("hygieia-publisher"));
            save();
            return super.configure(sr, formData);
        }

        public HygieiaService getHygieiaService(final String hygieiaAPIUrl, final String hygieiaToken, final String hygieiaJenkinsName, final boolean useProxy) {
            return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
        }

        @Override
        public String getDisplayName() {
            return "Hygieia Publisher";
        }

        public FormValidation doTestConnection(@QueryParameter("hygieiaAPIUrl") final String hygieiaAPIUrl,
                                               @QueryParameter("hygieiaToken") final String hygieiaToken,
                                               @QueryParameter("hygieiaJenkinsName") final String hygieiaJenkinsName,
                                               @QueryParameter("useProxy") final String sUseProxy)  {

            final String SUCCESS_MSG = "Connection to all endpoint(s) successful.";
            final String WARNING_MSG = "Failed connecting to endpoint(s) - ";
            String hostUrl = hygieiaAPIUrl;
            if (StringUtils.isEmpty(hostUrl)) {
                hostUrl = this.hygieiaAPIUrl;
            }
            String targetToken = hygieiaToken;
            if (StringUtils.isEmpty(targetToken)) {
                targetToken = this.hygieiaToken;
            }
            String name = hygieiaJenkinsName;
            if (StringUtils.isEmpty(name)) {
                name = this.hygieiaJenkinsName;
            }
            boolean bProxy = "true".equalsIgnoreCase(sUseProxy);
            if (StringUtils.isEmpty(sUseProxy)) {
                bProxy = this.useProxy;
            }

            List<String> apiEndpoints = Arrays.asList(hostUrl.split(HygieiaUtils.SEPERATOR));
            boolean SUCCESS = true;
            String ERROR_ENDPOINTS = " ";
            for(String apiEndpoint : apiEndpoints) {
                HygieiaService testHygieiaService = getHygieiaService(apiEndpoint, targetToken, name, bProxy);
                if (testHygieiaService != null) {
                    boolean RESULT = testHygieiaService.testConnection();
                    SUCCESS = SUCCESS && RESULT;
                    if (!RESULT){
                        ERROR_ENDPOINTS = ERROR_ENDPOINTS + apiEndpoint + " ";
                    }
                } else {
                    SUCCESS = Boolean.FALSE;
                }
            }
            return SUCCESS ? FormValidation.ok(SUCCESS_MSG) : FormValidation.error(WARNING_MSG + ERROR_ENDPOINTS);
        }

    }
}
