package jenkins.plugins.hygieia.workflow;

import com.capitalone.dashboard.model.quality.*;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import jenkins.plugins.hygieia.HygieiaService;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import hudson.remoting.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HygieiaCodeQualityPublisherStepExecutionTest {

    @Mock
    private HygieiaCodeQualityPublisherStep mockStep;

    @Mock
    private TaskListener listener;

    @Mock
    private Run run;

    @Mock
    private VirtualChannel mockChannel;

    private JAXBContext context;


    @InjectMocks
    private HygieiaCodeQualityPublisherStep.HygieiaCodeQualityPublisherStepExecution subject;

    @Before
    public void setup() throws JAXBException {
        context = JAXBContext.newInstance(JunitXmlReport.class, JacocoXmlReport.class,
                FindBugsXmlReport.class, CheckstyleReport.class, PmdReport.class);
        subject.filepath = new FilePath(mockChannel,"remote");
    }

    @Test
    public void runCollectsJunitResultFromJob() throws Throwable {
        when(mockStep.getJunitFilePattern()).thenReturn("**/target/junit.xml");
        FilePath[] files = new FilePath[]{new FilePath(new File(this.getClass().getResource("/junit.xml").toURI()))};

        when(mockChannel.call(any(Callable.class))).thenReturn(files);
        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(4);
    }

    @Test
    public void copesWithJunitNotDefined() throws Exception {
        when(mockStep.getJunitFilePattern()).thenReturn(null);

        subject.run();

    }

    @Test
    public void ignoresFileIncorrectlyIdentified() throws Exception {
        TestCase.fail("not done yet");
    }

}
