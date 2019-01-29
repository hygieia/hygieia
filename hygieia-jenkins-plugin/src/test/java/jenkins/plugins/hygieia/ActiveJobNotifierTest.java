package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import hudson.FilePath;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Result;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Job.class, Build.class, Run.class})
public class ActiveJobNotifierTest {

    @Mock
    private BuildListener mockBuildListener;

    @Mock
    private Build mockBuild;

    @Mock
    private HygieiaService mockHygieiaService;

    @Mock
    private HygieiaPublisher mockPublisher;

    @Mock
    private Project mockProject;

    @Mock
    private Job mockJob;

    @Mock
    private PrintStream mockStream;

    @Mock
    private ChangeLogSet<? extends ChangeLogSet.Entry> mockChangeSet;

    @Mock
    private SCM mockSCM;

    @Mock
    private HygieiaPublisher.DescriptorImpl mockDescriptor;

    private ActiveJobNotifier activeJobNotifier;
    private HygieiaResponse hygieiaResponse;


    private void setup() {
        hygieiaResponse = new HygieiaResponse(HttpStatus.SC_CREATED, "1234");
        activeJobNotifier = new ActiveJobNotifier(mockPublisher, mockBuildListener);

        when(mockPublisher.newHygieiaService(mockBuild, mockBuildListener)).thenReturn(mockHygieiaService);
        when(mockBuild.getProject()).thenReturn(mockProject);
        when(mockBuild.getParent()).thenReturn(mockJob);
        when(mockProject.getAbsoluteUrl()).thenReturn("http://jenkins.test.com/job/testJob/1");
        when(mockJob.getAbsoluteUrl()).thenReturn("http://jenkins.test.com/job/testJob/1");
        when(mockProject.getName()).thenReturn("testJob");
        when(mockJob.getName()).thenReturn("testJob");
        when(mockPublisher.getDescriptor()).thenReturn(mockDescriptor);
        when(mockDescriptor.getHygieiaJenkinsName()).thenReturn("jenkins");
        when(mockBuildListener.getLogger()).thenReturn(mockStream);
        when(mockProject.getScm()).thenReturn(mockSCM);
        when(mockSCM.getType()).thenReturn("test");
        when(mockHygieiaService.publishBuildData(any(BuildDataCreateRequest.class))).thenReturn(hygieiaResponse);
        when(mockHygieiaService.publishArtifactData(any(BinaryArtifactCreateRequest.class))).thenReturn(hygieiaResponse);
        when(mockHygieiaService.publishDeployData(any(DeployDataCreateRequest.class))).thenReturn(hygieiaResponse);
    }

    @Test
    public void startedYesPublish() {
        setup();
        when(mockPublisher.getHygieiaBuild()).thenReturn(new HygieiaPublisher.HygieiaBuild(true));
        ArgumentCaptor<BuildDataCreateRequest> captor = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        activeJobNotifier.started(mockBuild);

        verify(mockHygieiaService).publishBuildData(captor.capture());
        BuildDataCreateRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getBuildStatus()).isEqualToIgnoringCase(BuildStatus.InProgress.toString());
        assertThat(capturedRequest.getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(capturedRequest.getNiceName()).isEqualTo("jenkins");
        verify(mockStream, times(1)).println("Hygieia: Published Build Start Data. " + hygieiaResponse.toString());
    }

    @Test
    public void startedNoPublish() {
        setup();
        when(mockPublisher.getHygieiaBuild()).thenReturn(new HygieiaPublisher.HygieiaBuild(false));
        ArgumentCaptor<BuildDataCreateRequest> captor = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        activeJobNotifier.started(mockBuild);
        verify(mockHygieiaService, never()).publishBuildData(captor.capture());
        verify(mockStream, never()).println("Hygieia: Published Build Complete Data. " + hygieiaResponse.toString());
    }


    @Test
    public void completedBuildOnlyYesPublish() {
        setup();
        when(mockPublisher.getHygieiaBuild()).thenReturn(new HygieiaPublisher.HygieiaBuild(true));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        activeJobNotifier.completed(mockBuild);
        ArgumentCaptor<BuildDataCreateRequest> captor = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService).publishBuildData(captor.capture());

        BuildDataCreateRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(capturedRequest.getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(capturedRequest.getNiceName()).isEqualTo("jenkins");
        verify(mockStream, times(1)).println("Hygieia: Published Build Complete Data. " + hygieiaResponse.toString());
    }

    @Test
    public void completedBuildOnlyNoPublish() {
        setup();
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        activeJobNotifier.completed(mockBuild);
        ArgumentCaptor<BuildDataCreateRequest> captor = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService, never()).publishBuildData(captor.capture());

        verify(mockStream, never()).println("Hygieia: Published Build Complete Data. " + hygieiaResponse.toString());
    }

    @Test
    public void completedBuildGlobalBuildPublishYes() {
        setup();
        when(mockPublisher.getHygieiaBuild()).thenReturn(new HygieiaPublisher.HygieiaBuild(true));
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        activeJobNotifier.completed(mockBuild);
        ArgumentCaptor<BuildDataCreateRequest> captor = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService, never()).publishBuildData(captor.capture());

        verify(mockStream, never()).println("Hygieia: Published Build Complete Data. " + hygieiaResponse.toString());
    }

    @Test
    public void completedBuildGlobalSonarPublishYes() {
        setup();
        when(mockPublisher.getHygieiaBuild()).thenReturn(new HygieiaPublisher.HygieiaBuild(true));
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(true);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        activeJobNotifier.completed(mockBuild);
        ArgumentCaptor<BuildDataCreateRequest> captor = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService, never()).publishBuildData(captor.capture());

        verify(mockStream, never()).println("Hygieia: Published Build Complete Data. " + hygieiaResponse.toString());
    }

    @Test
    public void completedBuildPublishSonar() throws IOException {
        setup();
        when(mockPublisher.getHygieiaSonar()).thenReturn(new HygieiaPublisher.HygieiaSonar(true, "10", "30"));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        Reader dummyReader = new StringReader("test");
        when(mockBuild.getLogReader()).thenReturn(dummyReader);

        activeJobNotifier.completed(mockBuild);

        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService).publishBuildData(captorBuild.capture());
        verify(mockHygieiaService, never()).publishSonarResults(captorSonar.capture());

        BuildDataCreateRequest capturedRequest = captorBuild.getValue();
        assertThat(capturedRequest.getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(capturedRequest.getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(capturedRequest.getNiceName()).isEqualTo("jenkins");
    }

    @Test
    public void completedBuildPublishArtifact() throws URISyntaxException {
        setup();
        when(mockPublisher.getHygieiaArtifact()).thenReturn(new HygieiaPublisher.HygieiaArtifact(".", "*", "com.hygieia", "1.0.0"));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        when(mockBuild.getWorkspace()).thenReturn(new FilePath(new File(this.getClass().getResource("").toURI())));
        activeJobNotifier.completed(mockBuild);

        ArgumentCaptor<BinaryArtifactCreateRequest> captorArtifact = ArgumentCaptor.forClass(BinaryArtifactCreateRequest.class);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService).publishBuildData(captorBuild.capture());
        verify(mockHygieiaService,atLeastOnce()).publishArtifactData(captorArtifact.capture());

        BuildDataCreateRequest capturedRequest = captorBuild.getValue();
        assertThat(capturedRequest.getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(capturedRequest.getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(capturedRequest.getNiceName()).isEqualTo("jenkins");
    }


    @Test
    public void completedBuildPublishDeploy() throws URISyntaxException {
        setup();
        when(mockPublisher.getHygieiaDeploy()).thenReturn(new HygieiaPublisher.HygieiaDeploy(".", "*", "com.hygieia", "1.0.0", "testApp", "testEnv", false));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        when(mockBuild.getWorkspace()).thenReturn(new FilePath(new File(this.getClass().getResource("").toURI())));

        activeJobNotifier.completed(mockBuild);

        ArgumentCaptor<DeployDataCreateRequest> captorDeploy = ArgumentCaptor.forClass(DeployDataCreateRequest.class);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService).publishBuildData(captorBuild.capture());
        verify(mockHygieiaService,atLeastOnce()).publishDeployData(captorDeploy.capture());

        BuildDataCreateRequest capturedRequest = captorBuild.getValue();
        assertThat(capturedRequest.getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(capturedRequest.getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(capturedRequest.getNiceName()).isEqualTo("jenkins");
    }


    @Test
    public void completedBuildPublishTest() throws URISyntaxException {
        setup();
        when(mockPublisher.getHygieiaTest()).thenReturn(new HygieiaPublisher.HygieiaTest(false, false, "*", ".", "Functional", "testApp", "testEnv"));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        when(mockBuild.getWorkspace()).thenReturn(new FilePath(new File(this.getClass().getResource("").toURI())));

        activeJobNotifier.completed(mockBuild);

        ArgumentCaptor<TestDataCreateRequest> captorTest = ArgumentCaptor.forClass(TestDataCreateRequest.class);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService).publishBuildData(captorBuild.capture());
        verify(mockHygieiaService,never()).publishTestResults(captorTest.capture());

        BuildDataCreateRequest capturedRequest = captorBuild.getValue();
        assertThat(capturedRequest.getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(capturedRequest.getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(capturedRequest.getNiceName()).isEqualTo("jenkins");
    }

}