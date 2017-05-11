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
        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);

    }

    @Test
    public void copesWithJunitEmpty() throws Exception {
        when(mockStep.getJunitFilePattern()).thenReturn("");
        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);

    }

    @Test
    public void ignoresFileIncorrectlyIdentified() throws Throwable {
        when(mockStep.getJunitFilePattern()).thenReturn("**/target/junit.xml");
        // javadoc says it may be empty but not null
        FilePath[] files = new FilePath[0];

        when(mockChannel.call(any(Callable.class))).thenReturn(files);
        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void copesWithEmptyFile() throws Throwable {
        when(mockStep.getJunitFilePattern()).thenReturn("**/target/junit.xml");
        FilePath[] files = new FilePath[]{};

        when(mockChannel.call(any(Callable.class))).thenReturn(files);
        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void doesPmd() throws Throwable {
        when(mockStep.getPmdFilePattern()).thenReturn("**/target/pmd.xml");
        FilePath[] files = new FilePath[]{new FilePath(new File(this.getClass().getResource("/pmd.xml").toURI()))};

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
    public void pmdNull() throws Exception{
        when(mockStep.getPmdFilePattern()).thenReturn(null);

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void pmdEmpty() throws Exception{
        when(mockStep.getPmdFilePattern()).thenReturn("");

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void doesFindbugs() throws Throwable {
        when(mockStep.getFindbugsFilePattern()).thenReturn("**/target/findbugs.xml");
        FilePath[] files = new FilePath[]{new FilePath(new File(this.getClass().getResource("/findbugs.xml").toURI()))};

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
    public void findbugsNull() throws Exception{
        when(mockStep.getFindbugsFilePattern()).thenReturn(null);

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void findbugsEmpty() throws Exception{
        when(mockStep.getFindbugsFilePattern()).thenReturn("");

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void doesCheckstyle() throws Throwable {
        when(mockStep.getCheckstyleFilePattern()).thenReturn("**/target/checkstyle-report.xml");
        FilePath[] files = new FilePath[]{new FilePath(new File(this.getClass().getResource("/checkstyle-report.xml").toURI()))};

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
    public void checkstyleNull() throws Exception{
        when(mockStep.getCheckstyleFilePattern()).thenReturn(null);

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void checkstyleEmpty() throws Exception{
        when(mockStep.getCheckstyleFilePattern()).thenReturn("");

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void doesJacoco() throws Throwable {
        when(mockStep.getJacocoFilePattern()).thenReturn("**/target/jacoco.xml");
        FilePath[] files = new FilePath[]{new FilePath(new File(this.getClass().getResource("/jacoco.xml").toURI()))};

        when(mockChannel.call(any(Callable.class))).thenReturn(files);
        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(6);
    }

    @Test
    public void jacocoNull() throws Exception{
        when(mockStep.getJacocoFilePattern()).thenReturn(null);

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }

    @Test
    public void jacocoEmpty() throws Exception{
        when(mockStep.getJacocoFilePattern()).thenReturn("");

        HygieiaService mockHygieiaService = mock(HygieiaService.class);
        when(mockStep.getService()).thenReturn(mockHygieiaService);

        when(mockStep.getContext()).thenReturn(context);

        subject.run();

        ArgumentCaptor<CodeQualityCreateRequest> captor = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService).publishSonarResults(captor.capture());

        assertThat(captor.getValue().getMetrics()).hasSize(0);
    }


}
