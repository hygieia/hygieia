package jenkins.plugins.hygieia.workflow;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class HygieiaMetaDataPublishStepTest {

    @Test
    public void descriptorImplDescribesCorrectStep() {
        HygieiaMetaDataPublishStep.DescriptorImpl description = new HygieiaMetaDataPublishStep.DescriptorImpl();

        assertThat(description.getExecutionType(), is(HygieiaMetaDataPublishStep.HygieiaMetaDataPublisherStepExecution.class.getClass()));
    }

    @Test
    public void descriptorImplHasDefinedFunction() {
        HygieiaMetaDataPublishStep.DescriptorImpl description = new HygieiaMetaDataPublishStep.DescriptorImpl();

        assertThat(description.getFunctionName(), is("hygieiaMetaDataPublishStep"));
    }

    @Test
    public void descriptorImplHasDisplayName() {
        HygieiaMetaDataPublishStep.DescriptorImpl description = new HygieiaMetaDataPublishStep.DescriptorImpl();

        assertThat(description.getDisplayName(), is("Hygieia Metadata Publish Step"));
    }


    @Test
    public void configurationInOuterDoesNotRequireAnything() throws JAXBException {
        HygieiaMetaDataPublishStep step = new HygieiaMetaDataPublishStep("key", "type", "rawData", "source");

        assertThat(step, is(CoreMatchers.<HygieiaMetaDataPublishStep>notNullValue()));

    }

    @Test
    public void configurationCanSetValues() throws JAXBException {

        HygieiaMetaDataPublishStep step = new HygieiaMetaDataPublishStep("key", "type", "rawData", "source");


        assertThat(step.getKey(), is("key"));
        assertThat(step.getType(), is("type"));
        assertThat(step.getRawData(), is("rawData"));
        assertThat(step.getSource(), is("source"));

    }

}