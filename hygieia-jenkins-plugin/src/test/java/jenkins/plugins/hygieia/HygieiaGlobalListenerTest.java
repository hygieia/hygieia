package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.response.BuildDataCreateResponse;
import com.google.common.collect.Sets;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Result;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import hygieia.builder.BuildBuilder;
import hygieia.builder.SonarBuilder;
import hygieia.utils.HygieiaUtils;
import jenkins.model.Jenkins;
import org.apache.http.HttpStatus;
import org.assertj.core.util.Lists;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, Job.class, Build.class, Run.class, SonarBuilder.class, BuildBuilder.class, HygieiaUtils.class})
public class HygieiaGlobalListenerTest {

    @Mock
    private Jenkins mockJenkins;

    @Mock
    private BuildListener mockBuildListener;

    @Mock
    private Build mockBuild;

    @Mock
    private Run mockRun;

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

    @Spy
    private HygieiaGlobalListener hygieiaGlobalListener = new HygieiaGlobalListener();

    @Mock
    private SonarBuilder mockSonarBuilder;

    private HygieiaResponse hygieiaResponse = new HygieiaResponse(HttpStatus.SC_CREATED, "1234");

    public void setup() throws IOException {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptorByType(HygieiaPublisher.DescriptorImpl.class)).thenReturn(mockDescriptor);
        PowerMockito.mockStatic(HygieiaUtils.class);
        PowerMockito.when(HygieiaUtils.convertJsonToObject("1234", BuildDataCreateResponse.class)).thenReturn(getBuildDataCreateResponse());
        PowerMockito.when(HygieiaUtils.getInstanceUrl(mockBuild, mockBuildListener)).thenReturn("http://jenkins.test.com");
        PowerMockito.when(HygieiaUtils.getInstanceUrl(mockRun, mockBuildListener)).thenReturn("http://jenkins.test.com");
        PowerMockito.when(HygieiaUtils.getBuildStatus(Result.SUCCESS)).thenReturn(BuildStatus.Success);
        when(mockDescriptor.getHygieiaService(any(String.class), any(String.class), any(String.class), any(Boolean.class))).thenReturn(mockHygieiaService);

        when(mockHygieiaService.publishBuildDataV3(any(BuildDataCreateRequest.class))).thenReturn(hygieiaResponse);
        when(mockHygieiaService.publishSonarResults(any(CodeQualityCreateRequest.class))).thenReturn(hygieiaResponse);
        when(mockHygieiaService.publishGenericCollectorItemData(any(GenericCollectorItemCreateRequest.class))).thenReturn(hygieiaResponse);

        when(mockBuild.getProject()).thenReturn(mockProject);
        when(mockBuild.getParent()).thenReturn(mockJob);
        when(mockProject.getAbsoluteUrl()).thenReturn("http://jenkins.test.com/job/testJob/1");
        when(mockJob.getAbsoluteUrl()).thenReturn("http://jenkins.test.com/job/testJob/1");
        when(mockProject.getName()).thenReturn("testJob");
        when(mockJob.getName()).thenReturn("testJob");

        when(mockPublisher.getDescriptor()).thenReturn(mockDescriptor);
        when(mockDescriptor.getHygieiaJenkinsName()).thenReturn("jenkins");
        when(mockPublisher.getDescriptor().getPluginVersionInfo()).thenReturn("hygieia-publisher version 2.1.6-SNAPSHOT");
        when(mockDescriptor.getHygieiaAPIUrl()).thenReturn("http:localhost:8080/api");
        when(mockDescriptor.isShowConsoleOutput()).thenReturn(true);
        when(mockBuildListener.getLogger()).thenReturn(mockStream);
        when(mockProject.getScm()).thenReturn(mockSCM);
        when(mockSCM.getType()).thenReturn("test");
        when(mockRun.getParent()).thenReturn(mockJob);
    }

    @Test
    public void onCompletedBuildNoPublish() throws IOException {
        setup();
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        verify(mockHygieiaService, never()).publishBuildDataV3(any(BuildDataCreateRequest.class));
        verify(mockHygieiaService, never()).publishSonarResults(any(CodeQualityCreateRequest.class));
    }

    @Test
    public void onCompletedBuildSkipJob() throws IOException {
        setup();
        when(mockDescriptor.getHygieiaExcludeJobNames()).thenReturn("PR-,test");
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);
        verify(mockStream, never()).println("Hygieia: Skipping publish to hygieia as the job was excluded in global configuration." + hygieiaResponse.toString());
    }

    @Test
    public void onCompletedBuildPublishBuildNoSonar() throws IOException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        hygieiaGlobalListener.onCompleted(mockRun, mockBuildListener);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService, times(1)).publishBuildDataV3(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService, never()).publishSonarResults(captorSonar.capture());
        assertThat(captorBuild.getValue().getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(captorBuild.getValue().getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(captorBuild.getValue().getNiceName()).isEqualTo("jenkins");
    }

    @Test
    public void onCompletedFailPublishBuild() throws IOException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        when(mockHygieiaService.publishBuildDataV3(any(BuildDataCreateRequest.class))).thenReturn(new HygieiaResponse(HttpStatus.SC_UNAUTHORIZED, ""));
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);
        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService, times(1)).publishBuildDataV3(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        ArgumentCaptor<GenericCollectorItemCreateRequest> captorGeneric = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService, never()).publishSonarResults(captorSonar.capture());
        verify(mockHygieiaService, never()).publishGenericCollectorItemData(captorGeneric.capture());
    }

    @Test
    public void onCompletedBuildPublishBuildAndSonar() throws IOException, ParseException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(true);
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        String initialString = "";
        Reader targetReader = new StringReader(initialString);
        targetReader.close();
        //when(new SonarBuilder().getSonarMetrics(mockRun, mockBuildListener,"jenkins",null,null,"5bda33528d6a01caebd4be20,5bda33528d6a01caebd4be1f", false)).thenReturn(new CodeQualityCreateRequest());
        //when(hygieiaGlobalListener.getSonarBuilder()).thenReturn(mockSonarBuilder);
        PowerMockito.mockStatic(SonarBuilder.class);
        PowerMockito.when(SonarBuilder.getInstance()).thenReturn(mockSonarBuilder);
        PowerMockito.when(mockSonarBuilder
                .getSonarMetrics(mockRun, mockBuildListener, "jenkins", null,
                        null, "5bda33528d6a01caebd4be20,5bda33528d6a01caebd4be1f", false))
                .thenReturn(new CodeQualityCreateRequest());
        hygieiaGlobalListener.onCompleted(mockRun, mockBuildListener);

        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService, times(1)).publishBuildDataV3(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService, times(1)).publishSonarResults(captorSonar.capture());
        assertThat(captorBuild.getValue().getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(captorBuild.getValue().getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(captorBuild.getValue().getNiceName()).isEqualTo("jenkins");
    }


    @Test
    public void onCompletedBuildPublishGenericEmpty() throws IOException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(false);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(false);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        Reader reader = new StringReader("this is a pattern http://whatever");
        when(mockBuild.getLogReader()).thenReturn(reader);

        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<GenericCollectorItemCreateRequest> captorBuild = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService, times(0)).publishGenericCollectorItemData(captorBuild.capture());
    }


    @Test
    public void onCompletedBuildPublishGenericOne() throws IOException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(false);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(false);
        HygieiaPublisher.GenericCollectorItem item = new HygieiaPublisher.GenericCollectorItem("mytool", "some pattern");

        when(mockDescriptor.getHygieiaPublishGenericCollectorItems()).thenReturn(Lists.newArrayList(item));
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);

        Reader reader = new StringReader("this is a pattern http://whatever");
        when(mockBuild.getLogReader()).thenReturn(reader);
        when(mockRun.getLogReader()).thenReturn(reader);
        Set<String> matchedLines = Sets.newHashSet(Collections.singletonList("line1"));
        String pattern = ".*" + Pattern.quote("some pattern") + "(.*)";
        PowerMockito.when(HygieiaUtils.getMatchedLinesFromLog(mockRun, pattern)).thenReturn(matchedLines);
        hygieiaGlobalListener.onCompleted(mockRun, mockBuildListener);
        ArgumentCaptor<GenericCollectorItemCreateRequest> captorBuild = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService, times(1)).publishGenericCollectorItemData(captorBuild.capture());
    }

    @Test
    public void onCompletedBuildPublishGenericTwo() throws IOException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(false);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(false);
        HygieiaPublisher.GenericCollectorItem item = new HygieiaPublisher.GenericCollectorItem("mytool", "some pattern");

        when(mockDescriptor.getHygieiaPublishGenericCollectorItems()).thenReturn(Lists.newArrayList(item));
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);

        Reader reader = new StringReader("this is a pattern http://whatever");
        when(mockRun.getLogReader()).thenReturn(reader);
        Set<String> matchedLines = Sets.newHashSet(Arrays.asList("line1", "line2"));
        String pattern = ".*" + Pattern.quote("some pattern") + "(.*)";
        PowerMockito.when(HygieiaUtils.getMatchedLinesFromLog(mockRun, pattern)).thenReturn(matchedLines);

        hygieiaGlobalListener.onCompleted(mockRun, mockBuildListener);

        ArgumentCaptor<GenericCollectorItemCreateRequest> captorBuild = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService, times(2)).publishGenericCollectorItemData(captorBuild.capture());
    }

    private BuildDataCreateResponse getBuildDataCreateResponse() {
        BuildDataCreateResponse response = new BuildDataCreateResponse();
        response.setBuildUrl("");
        response.setBuildStatus(BuildStatus.Success);
        response.setCollectorItemId(new ObjectId("5bda33528d6a01caebd4be1f"));
        response.setId(new ObjectId("5bda33528d6a01caebd4be20"));
        return response;
    }


}