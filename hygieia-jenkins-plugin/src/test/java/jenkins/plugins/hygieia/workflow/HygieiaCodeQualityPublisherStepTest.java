package jenkins.plugins.hygieia.workflow;

import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class HygieiaCodeQualityPublisherStepTest {

    @Test
    public void descriptorImplDescribesCorrectStep() {
        HygieiaCodeQualityPublisherStep.DescriptorImpl description = new HygieiaCodeQualityPublisherStep.DescriptorImpl();

        assertThat(description.getExecutionType(),is(HygieiaCodeQualityPublisherStep.HygieiaCodeQualityPublisherStepExecution.class.getClass()));
    }

    @Test
    public void descriptorImplHasDefinedFunction() {
        HygieiaCodeQualityPublisherStep.DescriptorImpl description = new HygieiaCodeQualityPublisherStep.DescriptorImpl();

        assertThat(description.getFunctionName(),is("hygieiaCodeQualityPublishStep"));
    }

    @Test
    public void descriptorImplHasDisplayName() {
        HygieiaCodeQualityPublisherStep.DescriptorImpl description = new HygieiaCodeQualityPublisherStep.DescriptorImpl();

        assertThat(description.getDisplayName(),is("Hygieia CodeQuality Publish Step"));
    }


    @Test
    public void configurationInOuterDoesNotRequireAnything() {
        HygieiaCodeQualityPublisherStep step = new HygieiaCodeQualityPublisherStep();

        assertThat(step,is(CoreMatchers.<HygieiaCodeQualityPublisherStep>notNullValue()));

    }

    @Test
    public void configurationCanSetJunitFilePattern() {

        HygieiaCodeQualityPublisherStep step = new HygieiaCodeQualityPublisherStep();

        step.setJunitFilePattern("**/target/junit.xml");

        assertThat(step.getJunitFilePattern(),is("**/target/junit.xml"));

    }

    @Test
    public void configurationCanSetFindbugsFilePattern() {
        HygieiaCodeQualityPublisherStep step = new HygieiaCodeQualityPublisherStep();

        step.setFindbugsFilePattern("**/target/findbugs.xml");

        assertThat(step.getFindbugsFilePattern(),is("**/target/findbugs.xml"));
    }

    @Test
    public void configurationCanSetPmdFilePattern() {
        HygieiaCodeQualityPublisherStep step = new HygieiaCodeQualityPublisherStep();

        step.setPmdFilePattern("**/target/pmd.xml");

        assertThat(step.getPmdFilePattern(),is("**/target/pmd.xml"));
    }

    @Test
    public void configurationCanSetCheckstyleFilePattern(){
        HygieiaCodeQualityPublisherStep step = new HygieiaCodeQualityPublisherStep();

        step.setCheckstyleFilePattern("**/target/checkstyle-result.xml");

        assertThat(step.getCheckstyleFilePattern(),is("**/target/checkstyle-result.xml"));
    }

    @Test
    public void configurationCanSetJacocoFilePattern() {
        HygieiaCodeQualityPublisherStep step = new HygieiaCodeQualityPublisherStep();

        step.setJacocoFilePattern("**/target/checkstyle-result.xml");

        assertThat(step.getJacocoFilePattern(),is("**/target/checkstyle-result.xml"));
    }

    @Test
    public void executionDoesSomething() throws Exception {
        HygieiaCodeQualityPublisherStep.HygieiaCodeQualityPublisherStepExecution execution = new HygieiaCodeQualityPublisherStep.HygieiaCodeQualityPublisherStepExecution();

        execution.run();

        TestCase.fail("no written yet");
    }

}