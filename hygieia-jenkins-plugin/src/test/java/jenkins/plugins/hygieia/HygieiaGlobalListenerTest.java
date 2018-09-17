package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import hudson.EnvVars;
import hudson.Functions;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Result;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import hudson.tasks.BatchFile;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hygieia.builder.BuildBuilder;
import hygieia.builder.SonarBuilder;
import jenkins.model.Jenkins;
import org.apache.http.HttpStatus;
import org.json.simple.parser.ParseException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Job.class, Build.class, Run.class})
public class HygieiaGlobalListenerTest {

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

    @Mock
    SonarBuilder sonarBuilder;


    @InjectMocks
    private HygieiaGlobalListener hygieiaGlobalListener;



    private HygieiaResponse hygieiaResponse;

    private void setup() throws ParseException {
        hygieiaResponse = new HygieiaResponse(HttpStatus.SC_CREATED, "1234");
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
        when(mockHygieiaService.publishSonarResults(any(CodeQualityCreateRequest.class))).thenReturn(hygieiaResponse);
        when(sonarBuilder.getSonarMetrics()).thenReturn(mock(CodeQualityCreateRequest.class));
    }

    @Test
    public void onCompletedBuildNoPublish() throws ParseException {
        setup();
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService,never()).publishBuildData(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService,never()).publishSonarResults(captorSonar.capture());
    }

    @Test
    public void onCompletedBuildPublishBuildNoSonar() throws ParseException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService,times(1)).publishBuildData(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService,never()).publishSonarResults(captorSonar.capture());
        assertThat(captorBuild.getValue().getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(captorBuild.getValue().getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(captorBuild.getValue().getNiceName()).isEqualTo("jenkins");
    }



    @Test
    public void onCompletedBuildPublishBuildAndSonar() throws IOException, ParseException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(true);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService,times(1)).publishBuildData(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService,times(1)).publishSonarResults(captorSonar.capture());
        assertThat(captorBuild.getValue().getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(captorBuild.getValue().getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(captorBuild.getValue().getNiceName()).isEqualTo("jenkins");
    }
}