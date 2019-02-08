package jenkins.plugins.hygieia;

import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Run;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Job.class, Build.class, Run.class})
public class HygieiaJobListenerTest {
    @Mock
    private BuildListener mockBuildListener;

    @Mock
    private HygieiaPublisher mockPublisher;

    @Mock
    private Project mockProject;

    @InjectMocks
    private HygieiaJobListener mockHygieiaJobListener = new HygieiaJobListener();

    @Mock
    private DescribableList mockDescribableList;


    @Test
    public void getNotifier() {

        Map<Descriptor<Publisher>, Publisher> map = new HashMap<>();
        HygieiaPublisher.DescriptorImpl descriptor = mock(HygieiaPublisher.DescriptorImpl.class);

        map.put(descriptor, mockPublisher);
        when(mockProject.getPublishersList()).thenReturn(mockDescribableList);
        when(mockDescribableList.toMap()).thenReturn(map);

        FineGrainedNotifier fineGrainedNotifier = mockHygieiaJobListener.getNotifier(mockProject, mockBuildListener);
        assertThat(fineGrainedNotifier, instanceOf(ActiveJobNotifier.class));

    }
}