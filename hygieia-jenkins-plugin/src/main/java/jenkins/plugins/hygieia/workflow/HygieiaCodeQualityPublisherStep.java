package jenkins.plugins.hygieia.workflow;


import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;


public class HygieiaCodeQualityPublisherStep extends AbstractStepImpl {

    private String junitFilePattern;
    private String findbugsFilePattern;
    private String pmdFilePattern;
    private String checkstyleFilePattern;
    private String jacocoFilePattern;

    @DataBoundConstructor
    public HygieiaCodeQualityPublisherStep() {

    }

    @DataBoundSetter
    public void setJunitFilePattern(String junitFilePattern) {
        this.junitFilePattern = junitFilePattern;
    }

    public String getJunitFilePattern() {
        return junitFilePattern;
    }

    @DataBoundSetter
    public void setFindbugsFilePattern(String findbugsFilePattern) {
        this.findbugsFilePattern = findbugsFilePattern;
    }

    public String getFindbugsFilePattern() {
        return findbugsFilePattern;
    }

    @DataBoundSetter
    public void setPmdFilePattern(String pmdFilePattern) {
        this.pmdFilePattern = pmdFilePattern;
    }

    public String getPmdFilePattern() {
        return pmdFilePattern;
    }

    @DataBoundSetter
    public void setCheckstyleFilePattern(String checkstyleFilePattern) {
        this.checkstyleFilePattern = checkstyleFilePattern;
    }

    public String getCheckstyleFilePattern() {
        return checkstyleFilePattern;
    }

    @DataBoundSetter
    public void setJacocoFilePattern(String jacocoFilePattern) {
        this.jacocoFilePattern = jacocoFilePattern;
    }

    public String getJacocoFilePattern() {
        return jacocoFilePattern;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl(){
            super(HygieiaCodeQualityPublisherStepExecution.class);
        }

        @Override
        public String getDisplayName() {
            return "Hygieia CodeQuality Publish Step";
        }

        @Override
        public String getFunctionName() {
            return "hygieiaCodeQualityPublishStep";
        }

    }

    public static class HygieiaCodeQualityPublisherStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        @Override
        protected Void run() throws Exception {
            return null;
        }
    }
}
