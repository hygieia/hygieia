package com.capitalone.dashboard.cloudwatch.collector;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.scheduling.TaskScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by stevegal on 16/06/2018.
 */
public class AwsCloudwatchLogAnalyzerTaskTest {

    private AwsCloudwatchLogAnalyzerTask subject;

    private TaskScheduler mockScheduler;

    private BaseCollectorRepository<AwsCloudwatchLogAnalyzer> mockRepo;

    private AwsCloudwatchLogAnalyzerSettings settings;

    @Before
    public void setup(){
        this.settings = new AwsCloudwatchLogAnalyzerSettings();
        this.settings.setCron("* * * 5");
        this.mockScheduler = mock(TaskScheduler.class);
        this.mockRepo = mock(BaseCollectorRepository.class);
        this.subject= new AwsCloudwatchLogAnalyzerTask(mockScheduler, this.mockRepo, this.settings);
    }

    @Test
    public void hasNameSet(){

        assertThat((String)Whitebox.getInternalState(subject,"collectorName")).isEqualTo("AwsCloudwatchAnalyzerTask");
    }

    @Test
    public void getCollector(){
        AwsCloudwatchLogAnalyzer collector = subject.getCollector();
        assertThat(collector).isNotNull();
    }

    @Test
    public void getRepo() {
        BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repo = subject.getCollectorRepository();

        assertThat(repo).isNotNull();
        assertThat(repo).isSameAs(mockRepo);
    }

    @Test
    public void getCron() {
        String cron = subject.getCron();
        assertThat(cron).isEqualTo("* * * 5");
    }
}
