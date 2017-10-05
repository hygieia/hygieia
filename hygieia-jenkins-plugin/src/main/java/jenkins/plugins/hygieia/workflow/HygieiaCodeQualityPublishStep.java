package jenkins.plugins.hygieia.workflow;


import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.quality.*;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hygieia.builder.BuildBuilder;
import jenkins.model.Jenkins;
import jenkins.plugins.hygieia.DefaultHygieiaService;
import jenkins.plugins.hygieia.HygieiaPublisher;
import jenkins.plugins.hygieia.HygieiaResponse;
import jenkins.plugins.hygieia.HygieiaService;
import jenkins.plugins.hygieia.utils.CodeQualityMetricsConverter;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.xml.sax.*;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;


public class HygieiaCodeQualityPublishStep extends AbstractStepImpl {

    private String junitFilePattern;
    private String findbugsFilePattern;
    private String pmdFilePattern;
    private String checkstyleFilePattern;
    private String jacocoFilePattern;
    private JAXBContext context;
    private HygieiaService service;
    private HygieiaPublisher.DescriptorImpl hygieiaDesc;

    @DataBoundConstructor
    public HygieiaCodeQualityPublishStep() throws JAXBException {
        context = JAXBContext.newInstance(JunitXmlReport.class, JacocoXmlReport.class,
                FindBugsXmlReport.class, CheckstyleReport.class, PmdReport.class);
        if (null != Jenkins.getInstance()) {
            hygieiaDesc = Jenkins.getInstance().getDescriptorByType(HygieiaPublisher.DescriptorImpl.class);
            service = new DefaultHygieiaService(hygieiaDesc.getHygieiaAPIUrl(), hygieiaDesc.getHygieiaToken(),
                    hygieiaDesc.getHygieiaJenkinsName(), hygieiaDesc.isUseProxy());
        }

    }

    public HygieiaPublisher.DescriptorImpl getHygieiaDesc() {
        return hygieiaDesc;
    }

    public JAXBContext getContext() {
        return context;
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

    public HygieiaService getService() {
        return service;
    }

    public void setService(HygieiaService service) {
        this.service = service;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
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

        private static final long serialVersionUID = 1L;

        @Inject
        transient HygieiaCodeQualityPublishStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        @StepContextParameter
        transient FilePath filepath;

        @Override
        protected Void run() throws Exception {
            HygieiaService service = step.getService();


            BuildBuilder buildBuilder = new BuildBuilder(run, step.getHygieiaDesc().getHygieiaJenkinsName(), listener, BuildStatus.Success, false);
            HygieiaResponse buildResponse = service.publishBuildData(buildBuilder.getBuildData());
            CodeQualityMetricsConverter converter = new CodeQualityMetricsConverter();
            Unmarshaller unmarshaller = step.getContext().createUnmarshaller();

            // junit
            if (null!=step.getJunitFilePattern() && !step.getJunitFilePattern().isEmpty()) {
                FilePath[] filePaths = filepath.list(step.getJunitFilePattern());
                for (FilePath junit : filePaths) {
                    JunitXmlReport report = unmarshall(unmarshaller, junit);
                    report.accept(converter);
                }
            }

            // pmd
            if (null!=step.getPmdFilePattern() && !step.getPmdFilePattern().isEmpty()) {
                FilePath[] filePaths = filepath.list(step.getPmdFilePattern());
                for (FilePath pmd : filePaths) {
                    PmdReport report = unmarshall(unmarshaller, pmd);
                    report.accept(converter);
                }
            }

            // findbugs
            if (null!=step.getFindbugsFilePattern() && !step.getFindbugsFilePattern().isEmpty()) {
                FilePath[] filePaths = filepath.list(step.getFindbugsFilePattern());
                for (FilePath findbugs : filePaths) {
                    FindBugsXmlReport report = unmarshall(unmarshaller, findbugs);
                    report.accept(converter);
                }
            }

            // checkstyle
            if (null!=step.getCheckstyleFilePattern() && !step.getCheckstyleFilePattern().isEmpty()) {
                FilePath[] filePaths = filepath.list(step.getCheckstyleFilePattern());
                for (FilePath checkstyle : filePaths) {
                    CheckstyleReport report = unmarshall(unmarshaller, checkstyle);
                    report.accept(converter);
                }
            }

            //jacoco
            if (null!=step.getJacocoFilePattern() && !step.getJacocoFilePattern().isEmpty()) {
                FilePath[] filePaths = filepath.list(step.getJacocoFilePattern());
                for (FilePath checkstyle : filePaths) {
                    JacocoXmlReport report = unmarshall(unmarshaller, checkstyle);
                    report.accept(converter);
                }
            }


            // results
            CodeQuality codeQuality = converter.produceResult();

            CodeQualityCreateRequest request = convertToRequest(codeQuality);
            request.setProjectName(run.getParent().getFullName());
            request.setProjectUrl(run.getParent().getUrl());
            request.setNiceName(step.getHygieiaDesc().getHygieiaJenkinsName());
            request.setType(CodeQualityType.StaticAnalysis);
            request.setTimestamp(run.getTimeInMillis());
            request.setProjectVersion(run.getId());
            request.setHygieiaId(buildResponse.getResponseValue());
            request.setProjectId(run.getParent().getFullName());
            request.setServerUrl(run.getParent().getAbsoluteUrl());

            service.publishSonarResults(request);
            return null;
        }

        private CodeQualityCreateRequest convertToRequest(CodeQuality quality) {
            CodeQualityCreateRequest request = new CodeQualityCreateRequest();
            for (CodeQualityMetric metric : quality.getMetrics()) {
                request.getMetrics().add(metric);
            }

            return request;
        }

        private <T> T unmarshall(Unmarshaller unmarshaller, FilePath path) throws IOException, InterruptedException, JAXBException, SAXException, ParserConfigurationException {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://xml.org/sax/features/validation", false);

            XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            InputSource inputSource = new InputSource(
                    path.read());
            SAXSource source = new SAXSource(xmlReader, inputSource);

            //TODO prevent malicious xml attack, or ignore? (https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#SAXTransformerFactory)
            return (T) unmarshaller.unmarshal(source);
        }
    }
}
