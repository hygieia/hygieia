package hygieia.builder;

import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import hudson.model.Build;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, Job.class, Build.class, Run.class})
public class GenericCollectorItemBuilderTest {

    @Mock
    Run run;

    @Mock
    Job job;

    @Mock
    Jenkins mockJenkins;

    @Test
    public void getRequestEmptyLog() throws IOException {
        GenericCollectorItemBuilder gib = GenericCollectorItemBuilder.getInstance();
        when(run.getParent()).thenReturn(job);
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getRootUrl()).thenReturn("http://myjenkins.com");

        Reader reader = new StringReader("");
        when(run.getLogReader()).thenReturn(reader);
        List<GenericCollectorItemCreateRequest> requests = gib.getRequests(run,"mytool", "this is a pattern", "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa");

        assertEquals(requests.size(), 0);
    }

    @Test
    public void getRequestOneMatch() throws IOException {
        GenericCollectorItemBuilder gib = GenericCollectorItemBuilder.getInstance();
        when(run.getParent()).thenReturn(job);
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getRootUrl()).thenReturn("http://myjenkins.com");
        Reader reader = new StringReader("this is a pattern http://whatever");
        when(run.getLogReader()).thenReturn(reader);
        List<GenericCollectorItemCreateRequest> requests = gib.getRequests(run,"mytool", "this is a pattern", "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa");

        assertEquals(requests.size(), 1);
        GenericCollectorItemCreateRequest request = requests.get(0);
        assertEquals("5ba16a0b0be2d34a64291205", request.getBuildId());
        assertEquals("56c39f487fab7c63c8f947aa", request.getRelatedCollectorItemId());
        assertEquals("http://whatever", request.getRawData());
        assertEquals("mytool", request.getToolName());
    }


    @Test
    public void getRequestTwoMatches() throws IOException {
        GenericCollectorItemBuilder gib = GenericCollectorItemBuilder.getInstance();
        when(run.getParent()).thenReturn(job);
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getRootUrl()).thenReturn("http://myjenkins.com");
        Reader reader = new StringReader("this is a pattern http://whatever  \n this is a pattern http://another.whatever");
        when(run.getLogReader()).thenReturn(reader);
        List<GenericCollectorItemCreateRequest> requests = gib.getRequests(run,"mytool", "this is a pattern", "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa");

        assertEquals(requests.size(), 2);
        GenericCollectorItemCreateRequest request = requests.get(0);
        assertEquals("5ba16a0b0be2d34a64291205", request.getBuildId());
        assertEquals("56c39f487fab7c63c8f947aa", request.getRelatedCollectorItemId());
        assertEquals("http://whatever", request.getRawData());
        assertEquals("mytool", request.getToolName());

        request = requests.get(1);
        assertEquals("5ba16a0b0be2d34a64291205", request.getBuildId());
        assertEquals("56c39f487fab7c63c8f947aa", request.getRelatedCollectorItemId());
        assertEquals("http://another.whatever", request.getRawData());
        assertEquals("mytool", request.getToolName());
    }

    @Test
    public void getRequestNoMatch() throws IOException {
        GenericCollectorItemBuilder gib = GenericCollectorItemBuilder.getInstance();
        when(run.getParent()).thenReturn(job);
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getRootUrl()).thenReturn("http://myjenkins.com");

        Reader reader = new StringReader("this is not a pattern");
        when(run.getLogReader()).thenReturn(reader);
        List<GenericCollectorItemCreateRequest> requests = gib.getRequests(run,"mytool", "this is a pattern", "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa");

        assertEquals(requests.size(), 0);
    }

}