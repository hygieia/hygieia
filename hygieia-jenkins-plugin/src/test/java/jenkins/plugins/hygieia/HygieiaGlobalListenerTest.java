package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Result;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import hygieia.builder.BuildBuilder;
import hygieia.builder.GenericCollectorItemBuilder;
import hygieia.builder.SonarBuilder;
import jenkins.model.Jenkins;
import org.apache.http.HttpStatus;
import org.assertj.core.util.Lists;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, Job.class, Build.class, Run.class, SonarBuilder.class, BuildBuilder.class})
public class HygieiaGlobalListenerTest {

    @Mock
    private Jenkins mockJenkins;

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
    SonarBuilder mockSonarBuilder;

    @Mock
    GenericCollectorItemBuilder genericCollectorItemBuilder;


    @Spy
    private HygieiaGlobalListener hygieiaGlobalListener = new HygieiaGlobalListener();


    private HygieiaResponse hygieiaResponse = new HygieiaResponse(HttpStatus.SC_CREATED, "1234");


    public void setup() throws ParseException, IOException, URISyntaxException {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptorByType(HygieiaPublisher.DescriptorImpl.class)).thenReturn(mockDescriptor);

        when(mockDescriptor.getHygieiaService(any(String.class),any(String.class), any(String.class), any(Boolean.class))).thenReturn(mockHygieiaService);

        when(mockHygieiaService.publishBuildData(any(BuildDataCreateRequest.class))).thenReturn(hygieiaResponse);
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
        when(mockBuildListener.getLogger()).thenReturn(mockStream);
        when(mockProject.getScm()).thenReturn(mockSCM);
        when(mockSCM.getType()).thenReturn("test");

        when(mockSonarBuilder.getSonarMetrics()).thenReturn(mock(CodeQualityCreateRequest.class));
    }

    @Test
    public void onCompletedBuildNoPublish() throws ParseException, IOException, URISyntaxException {
        setup();
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        verify(mockHygieiaService,never()).publishBuildData(any(BuildDataCreateRequest.class));
        verify(mockHygieiaService,never()).publishSonarResults(any(CodeQualityCreateRequest.class));
    }

    @Test
    public void onCompletedBuildPublishBuildNoSonar() throws ParseException, IOException, URISyntaxException {
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
        assertThat(captorBuild.getValue().getNiceName()).isEqualTo("jenkins");    }



    @Test
    public void onCompletedBuildPublishBuildAndSonar() throws IOException, ParseException, URISyntaxException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(true);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(true);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);
        String initialString = "";
        Reader targetReader = new StringReader(initialString);
        targetReader.close();

        doReturn(mockSonarBuilder).when(hygieiaGlobalListener).getSonarBuilder(hygieiaResponse, mockBuild, mockBuildListener, mockDescriptor);

        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<BuildDataCreateRequest> captorBuild = ArgumentCaptor.forClass(BuildDataCreateRequest.class);
        verify(mockHygieiaService,times(1)).publishBuildData(captorBuild.capture());
        ArgumentCaptor<CodeQualityCreateRequest> captorSonar = ArgumentCaptor.forClass(CodeQualityCreateRequest.class);
        verify(mockHygieiaService,times(1)).publishSonarResults(captorSonar.capture());
        assertThat(captorBuild.getValue().getBuildStatus()).isEqualToIgnoringCase(BuildStatus.Success.toString());
        assertThat(captorBuild.getValue().getInstanceUrl()).isEqualTo("http://jenkins.test.com");
        assertThat(captorBuild.getValue().getNiceName()).isEqualTo("jenkins");
    }


    @Test
    public void onCompletedBuildPublishGenericEmpty() throws IOException, ParseException, URISyntaxException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(false);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(false);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);
        when(mockChangeSet.isEmptySet()).thenReturn(true);

        Reader reader = new StringReader("this is a pattern http://whatever");
        when(mockBuild.getLogReader()).thenReturn(reader);

        doReturn(genericCollectorItemBuilder).when(hygieiaGlobalListener).getGenericCollectorItemBuilder(any(HygieiaResponse.class), any(Run.class), any(HygieiaPublisher.DescriptorImpl.class), anyString(), anyString());

        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<GenericCollectorItemCreateRequest> captorBuild = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService,times(0)).publishGenericCollectorItemData(captorBuild.capture());
    }


    @Test
    public void onCompletedBuildPublishGenericOne() throws IOException, ParseException, URISyntaxException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(false);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(false);
        HygieiaPublisher.GenericCollectorItem item = new HygieiaPublisher.GenericCollectorItem("mytool", "some pattern");

        when(mockDescriptor.getHygieiaPublishGenericCollectorItems()).thenReturn(Lists.newArrayList(item));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);

        Reader reader = new StringReader("this is a pattern http://whatever");
        when(mockBuild.getLogReader()).thenReturn(reader);

        doReturn(genericCollectorItemBuilder).when(hygieiaGlobalListener).getGenericCollectorItemBuilder(any(HygieiaResponse.class), any(Run.class), any(HygieiaPublisher.DescriptorImpl.class), anyString(), anyString());

        GenericCollectorItemCreateRequest gc = new GenericCollectorItemCreateRequest();
        gc.setBuildId("1234");
        gc.setRelatedCollectorItemId("9876");
        gc.setRawData("some data");
        gc.setSource("some source");
        when(genericCollectorItemBuilder.getRequests()).thenReturn(Lists.newArrayList(gc));
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<GenericCollectorItemCreateRequest> captorBuild = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService,times(1)).publishGenericCollectorItemData(captorBuild.capture());
    }

    @Test
    public void onCompletedBuildPublishGenericTwo() throws IOException, ParseException, URISyntaxException {
        setup();
        when(mockDescriptor.isHygieiaPublishBuildDataGlobal()).thenReturn(false);
        when(mockDescriptor.isHygieiaPublishSonarDataGlobal()).thenReturn(false);
        HygieiaPublisher.GenericCollectorItem item = new HygieiaPublisher.GenericCollectorItem("mytool", "some pattern");

        when(mockDescriptor.getHygieiaPublishGenericCollectorItems()).thenReturn(Lists.newArrayList(item));
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);

        Reader reader = new StringReader("this is a pattern http://whatever");
        when(mockBuild.getLogReader()).thenReturn(reader);

        doReturn(genericCollectorItemBuilder).when(hygieiaGlobalListener).getGenericCollectorItemBuilder(any(HygieiaResponse.class), any(Run.class), any(HygieiaPublisher.DescriptorImpl.class), anyString(), anyString());

        List<GenericCollectorItemCreateRequest> gcList = new ArrayList<>();
        GenericCollectorItemCreateRequest gc = new GenericCollectorItemCreateRequest();
        gc.setBuildId("1234");
        gc.setRelatedCollectorItemId("9876");
        gc.setRawData("some data");
        gc.setSource("some source");
        gcList.add(gc);

        gc = new GenericCollectorItemCreateRequest();
        gc.setBuildId("1234");
        gc.setRelatedCollectorItemId("9876");
        gc.setRawData("some data again");
        gc.setSource("some source");
        gcList.add(gc);

        when(genericCollectorItemBuilder.getRequests()).thenReturn(gcList);
        hygieiaGlobalListener.onCompleted(mockBuild, mockBuildListener);

        ArgumentCaptor<GenericCollectorItemCreateRequest> captorBuild = ArgumentCaptor.forClass(GenericCollectorItemCreateRequest.class);
        verify(mockHygieiaService,times(2)).publishGenericCollectorItemData(captorBuild.capture());
    }


}