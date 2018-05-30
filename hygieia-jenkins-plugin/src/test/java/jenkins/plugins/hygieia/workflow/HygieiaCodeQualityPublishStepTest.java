package jenkins.plugins.hygieia.workflow;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class HygieiaCodeQualityPublishStepTest {

    @Test
    public void descriptorImplDescribesCorrectStep() {
        HygieiaCodeQualityPublishStep.DescriptorImpl description = new HygieiaCodeQualityPublishStep.DescriptorImpl();

        assertThat(description.getExecutionType(),is(HygieiaCodeQualityPublishStep.HygieiaCodeQualityPublisherStepExecution.class.getClass()));
    }

    @Test
    public void descriptorImplHasDefinedFunction() {
        HygieiaCodeQualityPublishStep.DescriptorImpl description = new HygieiaCodeQualityPublishStep.DescriptorImpl();

        assertThat(description.getFunctionName(),is("hygieiaCodeQualityPublishStep"));
    }

    @Test
    public void descriptorImplHasDisplayName() {
        HygieiaCodeQualityPublishStep.DescriptorImpl description = new HygieiaCodeQualityPublishStep.DescriptorImpl();

        assertThat(description.getDisplayName(),is("Hygieia CodeQuality Publish Step"));
    }


    @Test
    public void configurationInOuterDoesNotRequireAnything() throws JAXBException {
        HygieiaCodeQualityPublishStep step = new HygieiaCodeQualityPublishStep();

        assertThat(step,is(CoreMatchers.<HygieiaCodeQualityPublishStep>notNullValue()));

    }

    @Test
    public void configurationCanSetJunitFilePattern() throws JAXBException {

        HygieiaCodeQualityPublishStep step = new HygieiaCodeQualityPublishStep();

        step.setJunitFilePattern("**/target/junit.xml");

        assertThat(step.getJunitFilePattern(),is("**/target/junit.xml"));

    }

    @Test
    public void configurationCanSetFindbugsFilePattern() throws JAXBException {
        HygieiaCodeQualityPublishStep step = new HygieiaCodeQualityPublishStep();

        step.setFindbugsFilePattern("**/target/findbugs.xml");

        assertThat(step.getFindbugsFilePattern(),is("**/target/findbugs.xml"));
    }

    @Test
    public void configurationCanSetPmdFilePattern() throws JAXBException {
        HygieiaCodeQualityPublishStep step = new HygieiaCodeQualityPublishStep();

        step.setPmdFilePattern("**/target/pmd.xml");

        assertThat(step.getPmdFilePattern(),is("**/target/pmd.xml"));
    }

    @Test
    public void configurationCanSetCheckstyleFilePattern() throws JAXBException {
        HygieiaCodeQualityPublishStep step = new HygieiaCodeQualityPublishStep();

        step.setCheckstyleFilePattern("**/target/checkstyle-result.xml");

        assertThat(step.getCheckstyleFilePattern(),is("**/target/checkstyle-result.xml"));
    }

    @Test
    public void configurationCanSetJacocoFilePattern() throws JAXBException {
        HygieiaCodeQualityPublishStep step = new HygieiaCodeQualityPublishStep();

        step.setJacocoFilePattern("**/target/checkstyle-result.xml");

        assertThat(step.getJacocoFilePattern(),is("**/target/checkstyle-result.xml"));
    }

}