package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.TestSuiteType;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hygieia.transformer.HygieiaConstants;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

//import org.json.simple.JSONObject;

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

        public static final int DEFAULT_QUERY_INTERVAL = 10;
        public static final int DEFAULT_QUERY_MAX_ATTEMPTS = 30;

        @DataBoundConstructor
        public HygieiaSonar(boolean publishBuildStart, String ceQueryIntervalInSeconds, String ceQueryMaxAttempts ) {
            this.publishBuildStart = publishBuildStart;
            this.ceQueryIntervalInSeconds = ceQueryIntervalInSeconds;
            this.ceQueryMaxAttempts = ceQueryMaxAttempts;
        }

        public boolean isPublishBuildStart() {
            return publishBuildStart;
        }

        /** Sonar 5.2+ changes: get query interval from config
         * If value is empty or null - return 10 (recommended value from SonarQube)
         * @return max number of attempts to query Sonar CE API (10 if blank)
         */
        public String getCeQueryIntervalInSeconds() {
            if (!StringUtils.isEmpty(ceQueryIntervalInSeconds)) {
                return ceQueryIntervalInSeconds;
            }
            else {
                return String.valueOf(DEFAULT_QUERY_INTERVAL);
            }
        }

        /** Sonar 5.2+ changes: get query max attempts from config
         * If value is empty or null - return 30 (recommended value from SonarQube)
         * @return max number of attempts to query Sonar CE API (30 if blank)
         */
        public String getCeQueryMaxAttempts() {
            if (!StringUtils.isEmpty(ceQueryIntervalInSeconds)) {
                return ceQueryMaxAttempts;
            }
            else {
                return String.valueOf(DEFAULT_QUERY_MAX_ATTEMPTS);
            }
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
        String hygieiaAPIUrl = getDescriptor().getHygieiaAPIUrl();
        String hygieiaToken = getDescriptor().getHygieiaToken();
        String hygieiaJenkinsName = getDescriptor().getHygieiaJenkinsName();
        boolean useProxy = getDescriptor().isUseProxy();
        EnvVars env;
        try {
            env = r.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
            env = new EnvVars();
        }
        hygieiaAPIUrl = env.expand(hygieiaAPIUrl);
        hygieiaToken = env.expand(hygieiaToken);
        hygieiaJenkinsName = env.expand(hygieiaJenkinsName);
        return new DefaultHygieiaService(hygieiaAPIUrl, hygieiaToken, hygieiaJenkinsName, useProxy);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }


    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String hygieiaAPIUrl;
        private String hygieiaToken;
        private String hygieiaJenkinsName;
        private boolean useProxy;
        private Set<String> deployAppNames = new HashSet<String>();
        private Set<String> deployEnvNames = new HashSet<String>();

        private String deployApplicationNameSelected;
        private String deployEnvSelected;
        private String testApplicationNameSelected;
        private String testEnvSelected;

        private Map<String, Set<String>> appEnv = new HashMap<String, Set<String>>();

        public DescriptorImpl() {
            load();
        }


        public String getHygieiaAPIUrl() {
            return hygieiaAPIUrl;
        }

        public String getHygieiaToken() {
            return hygieiaToken;
        }

        public String getHygieiaJenkinsName() {
            return hygieiaJenkinsName;
        }

        public boolean isUseProxy() {
            return useProxy;
        }

        public ListBoxModel doFillTestTypeItems(String testType) {
            ListBoxModel model = new ListBoxModel();

            model.add(HygieiaConstants.UNIT_TEST_DISPLAY, TestSuiteType.Unit.toString());
            model.add(HygieiaConstants.INTEGRATION_TEST_DISPLAY, TestSuiteType.Integration.toString());
            model.add(HygieiaConstants.FUNCTIONAL_TEST_DISPLAY, TestSuiteType.Functional.toString());
            model.add(HygieiaConstants.REGRESSION_TEST_DISPLAY, TestSuiteType.Regression.toString());
            model.add(HygieiaConstants.PERFORMANCE_TEST_DISPLAY, TestSuiteType.Performance.toString());
            model.add(HygieiaConstants.SECURITY_TEST_DISPLAY, TestSuiteType.Security.toString());
            return model;
        }

        /**
         * This method provides auto-completion items for the 'state' field.
         * Stapler finds this method via the naming convention.
         *
         * @param value The text that the user entered.
         */
        public AutoCompletionCandidates doAutoCompleteApplicationName(@QueryParameter String value, @QueryParameter("hygieiaAPIUrl") final String hygieiaAPIUrl,
                                                                      @QueryParameter("hygieiaToken") final String hygieiaToken,
                                                                      @QueryParameter("hygieiaJenkinsName") final String hygieiaJenkinsName,
                                                                      @QueryParameter("useProxy") final String sUseProxy) {

            String hostUrl = hygieiaAPIUrl;
            if (StringUtils.isEmpty(hostUrl)) {
                hostUrl = this.hygieiaAPIUrl;
            }
            String targetToken = hygieiaToken;
            if (StringUtils.isEmpty(targetToken)) {
                targetToken = this.hygieiaToken;
            }
            String niceName = hygieiaJenkinsName;
            if (StringUtils.isEmpty(niceName)) {
                niceName = this.hygieiaJenkinsName;
            }
            boolean bProxy = "true".equalsIgnoreCase(sUseProxy);
            if (StringUtils.isEmpty(sUseProxy)) {
                bProxy = this.useProxy;
            }
            AutoCompletionCandidates c = new AutoCompletionCandidates();
            if (CollectionUtils.isEmpty(deployAppNames)) fillApplicationNames(hostUrl, targetToken, niceName, bProxy);
            for (String aN : deployAppNames) {
                if (aN.toLowerCase().startsWith(value.toLowerCase())) {
                    c.add(aN);
                }
            }
            return c;
        }


        /**
         * This method provides auto-completion items for the 'state' field.
         * Stapler finds this method via the naming convention.
         *
         * @param value The text that the user entered.
         */
        public AutoCompletionCandidates doAutoCompleteEnvironmentName(@QueryParameter String value, @QueryParameter("hygieiaAPIUrl") final String hygieiaAPIUrl,
                                                                      @QueryParameter("hygieiaToken") final String hygieiaToken,
                                                                      @QueryParameter("hygieiaJenkinsName") final String hygieiaJenkinsName,
                                                                      @QueryParameter("useProxy") final String sUseProxy) {
            String hostUrl = hygieiaAPIUrl;
            if (StringUtils.isEmpty(hostUrl)) {
                hostUrl = this.hygieiaAPIUrl;
            }
            String targetToken = hygieiaToken;
            if (StringUtils.isEmpty(targetToken)) {
                targetToken = this.hygieiaToken;
            }
            String niceName = hygieiaJenkinsName;
            if (StringUtils.isEmpty(niceName)) {
                niceName = this.hygieiaJenkinsName;
            }
            boolean bProxy = "true".equalsIgnoreCase(sUseProxy);
            if (StringUtils.isEmpty(sUseProxy)) {
                bProxy = this.useProxy;
            }

            if (!StringUtils.isEmpty(deployApplicationNameSelected)) {
                deployEnvNames = getHygieiaService(hostUrl, targetToken, niceName, bProxy)
                        .getDeploymentEnvironments(deployApplicationNameSelected);
            }
            AutoCompletionCandidates c = new AutoCompletionCandidates();
            for (String eN : deployEnvNames) {
                if (eN.toLowerCase().startsWith(value.toLowerCase())) {
                    c.add(eN);
                }
            }
            return c;
        }


        private void fillApplicationNames(String hostUrl, String targetToken, String niceName, boolean useProxy) {
            for (org.json.simple.JSONObject item : getHygieiaService(hostUrl, targetToken, niceName, useProxy)
                    .getCollectorItemOptions(HygieiaConstants.COLLECTOR_ITEM_DEPLOYMENT)) {
                String name = (String) item.get("applicationName");
                if (!StringUtils.isEmpty(name)) {
                    deployAppNames.add(name);
                }
            }
        }

        /**
         * This method provides auto-completion items for the 'state' field.
         * Stapler finds this method via the naming convention.
         *
         * @param value The text that the user entered.
         */
        public AutoCompletionCandidates doAutoCompleteTestApplicationName(@QueryParameter String value, @QueryParameter("hygieiaAPIUrl") final String hygieiaAPIUrl,
                                                                          @QueryParameter("hygieiaToken") final String hygieiaToken,
                                                                          @QueryParameter("hygieiaJenkinsName") final String hygieiaJenkinsName,
                                                                          @QueryParameter("useProxy") final String sUseProxy) {

            String hostUrl = hygieiaAPIUrl;
            if (StringUtils.isEmpty(hostUrl)) {
                hostUrl = this.hygieiaAPIUrl;
            }
            String targetToken = hygieiaToken;
            if (StringUtils.isEmpty(targetToken)) {
                targetToken = this.hygieiaToken;
            }
            String niceName = hygieiaJenkinsName;
            if (StringUtils.isEmpty(niceName)) {
                niceName = this.hygieiaJenkinsName;
            }
            boolean bProxy = "true".equalsIgnoreCase(sUseProxy);
            if (StringUtils.isEmpty(sUseProxy)) {
                bProxy = this.useProxy;
            }
            AutoCompletionCandidates c = new AutoCompletionCandidates();
            if (CollectionUtils.isEmpty(deployAppNames)) fillApplicationNames(hostUrl, targetToken, niceName, bProxy);
            for (String aN : deployAppNames) {
                if (aN.toLowerCase().startsWith(value.toLowerCase())) {
                    c.add(aN);
                }
            }
            return c;
        }

        /**
         * This method provides auto-completion items for the 'state' field.
         * Stapler finds this method via the naming convention.
         *
         * @param value The text that the user entered.
         */
        public AutoCompletionCandidates doAutoCompleteTestEnvironmentName(@QueryParameter String value, @QueryParameter("hygieiaAPIUrl") final String hygieiaAPIUrl,
                                                                          @QueryParameter("hygieiaToken") final String hygieiaToken,
                                                                          @QueryParameter("hygieiaJenkinsName") final String hygieiaJenkinsName,
                                                                          @QueryParameter("useProxy") final String sUseProxy) {
            String hostUrl = hygieiaAPIUrl;
            if (StringUtils.isEmpty(hostUrl)) {
                hostUrl = this.hygieiaAPIUrl;
            }
            String targetToken = hygieiaToken;
            if (StringUtils.isEmpty(targetToken)) {
                targetToken = this.hygieiaToken;
            }
            String niceName = hygieiaJenkinsName;
            if (StringUtils.isEmpty(niceName)) {
                niceName = this.hygieiaJenkinsName;
            }
            boolean bProxy = "true".equalsIgnoreCase(sUseProxy);
            if (StringUtils.isEmpty(sUseProxy)) {
                bProxy = this.useProxy;
            }
            if (!StringUtils.isEmpty(testApplicationNameSelected)) {
                deployEnvNames = getHygieiaService(hostUrl, targetToken, niceName, bProxy)
                        .getDeploymentEnvironments(testApplicationNameSelected);
            }

            AutoCompletionCandidates c = new AutoCompletionCandidates();
            for (String eN : deployEnvNames) {
                if (eN.toLowerCase().startsWith(value.toLowerCase())) {
                    c.add(eN);
                }
            }
            return c;
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
            hygieiaAPIUrl = sr.getParameter("hygieiaAPIUrl");
            hygieiaToken = sr.getParameter("hygieiaToken");
            hygieiaJenkinsName = sr.getParameter("hygieiaJenkinsName");
            useProxy = "on".equals(sr.getParameter("useProxy"));
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
                                               @QueryParameter("useProxy") final String sUseProxy) throws FormException {

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
            HygieiaService testHygieiaService = getHygieiaService(hostUrl, targetToken, name, bProxy);
            if (testHygieiaService != null) {
                boolean success = testHygieiaService.testConnection();
                return success ? FormValidation.ok("Success") : FormValidation.error("Failure");
            } else {
                return FormValidation.error("Failure");
            }
        }

        public FormValidation doCheckValue(@QueryParameter String value) throws IOException, ServletException {
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckDeployAppNameValue(@QueryParameter String value) throws IOException, ServletException {
            deployApplicationNameSelected = value;
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
//            else if (!CollectionUtils.isEmpty(deployAppNames) && !deployAppNames.contains(value.trim())) {
//                return FormValidation.warning("You have entered a name that does not exist in Hygieia yet. This will create a new application in Hygieia.");
//            }

            return FormValidation.ok();
        }

        public FormValidation doCheckDeployEnvValue(@QueryParameter String value) throws IOException, ServletException {
            deployEnvSelected = value;
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
//            else if (!CollectionUtils.isEmpty(deployEnvNames) && !deployEnvNames.contains(value.trim())) {
//                return FormValidation.warning("You have entered a name that does not exist in Hygieia yet. This will create a new environment for application '" +
//                        deployApplicationNameSelected + "' in Hygieia.");
//            }

            return FormValidation.ok();
        }


        public FormValidation doCheckTestingAppNameValue(@QueryParameter String value) throws IOException, ServletException {
            testApplicationNameSelected = value;
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
//            else if (!CollectionUtils.isEmpty(deployAppNames) && !deployAppNames.contains(value.trim())) {
//                return FormValidation.warning("You have entered a name that does not exist in Hygieia yet. This will create a new application in Hygieia.");
//            }

            return FormValidation.ok();
        }

        public FormValidation doCheckTestingEnvValue(@QueryParameter String value) throws IOException, ServletException {
            testEnvSelected = value;
            if (value.isEmpty()) {
                return FormValidation.warning("You must fill this box!");
            }
//            else if (!CollectionUtils.isEmpty(deployEnvNames) && !deployEnvNames.contains(value.trim())) {
//                return FormValidation.warning("You have entered a name that does not exist in Hygieia yet. This will create a new environment for application '" +
//                        testApplicationNameSelected + "' in Hygieia.");
//            }

            return FormValidation.ok();
        }

    }
}
