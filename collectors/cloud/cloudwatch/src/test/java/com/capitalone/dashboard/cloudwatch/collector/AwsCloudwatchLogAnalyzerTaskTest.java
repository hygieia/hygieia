package com.capitalone.dashboard.cloudwatch.collector;

import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.capitalone.dashboard.cloudwatch.model.CloudWatchJob;
import com.capitalone.dashboard.cloudwatch.model.Series;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.springframework.scheduling.TaskScheduler;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by stevegal on 16/06/2018.
 */
public class AwsCloudwatchLogAnalyzerTaskTest {

    private AwsCloudwatchLogAnalyzerTask subject;

    private TaskScheduler mockScheduler;

    private BaseCollectorRepository<AwsCloudwatchLogAnalyzer> mockRepo;

    private AwsCloudwatchLogAnalyzerSettings settings;

    private AwsCloudWatchClientFactory mockFactory;

    @Before
    public void setup(){
        this.settings = this.produceDefaultSettings();
        this.mockScheduler = mock(TaskScheduler.class);
        this.mockRepo = mock(BaseCollectorRepository.class);
        this.mockFactory = mock(AwsCloudWatchClientFactory.class);
        this.subject= new AwsCloudwatchLogAnalyzerTask(this.mockScheduler, this.mockRepo, this.mockFactory,this.settings);
    }

    private AwsCloudwatchLogAnalyzerSettings produceDefaultSettings(){
        AwsCloudwatchLogAnalyzerSettings settings = new AwsCloudwatchLogAnalyzerSettings();
        settings.setCron("*/5 * * *");
        CloudWatchJob job = new CloudWatchJob();
        job.setName("MyFirstGraph");
        Series series1 = new Series();
        series1.setName("event1");
        series1.setLogGroupName("myFirstLogGroup");
        series1.addLogStream("myStream1");
        series1.addLogStream("myStream2");
        series1.addLogStream("myStream3");
        series1.setFilterPattern("{$.event=\"myValue\"}");

        job.addSeries(series1);
        settings.addJob(job);

        return settings;
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
        assertThat(cron).isEqualTo("*/5 * * *");
    }

    @Test
    public void collectFromNewCreatesAllJobs() {
        AwsCloudwatchLogAnalyzer mockAnalyzer = mock(AwsCloudwatchLogAnalyzer.class);
        when(mockRepo.findByCollectorTypeAndName(CollectorType.Log,"AwsCloudwatchAnalyzerTask")).thenReturn(Collections.EMPTY_LIST);
        AWSLogsClient mockClient = mock(AWSLogsClient.class);
        when(mockFactory.getInstance()).thenReturn(mockClient);

        FilterLogEventsResult fakeLogEventResult = new FilterLogEventsResult();
        when(mockClient.filterLogEvents(any(FilterLogEventsRequest.class))).thenReturn(fakeLogEventResult);

        subject.collect(mockAnalyzer);

        long currentTime = System.currentTimeMillis();
        ArgumentCaptor<FilterLogEventsRequest> logFilterRequestCaptor = ArgumentCaptor.forClass(FilterLogEventsRequest.class);
        verify(mockClient).filterLogEvents(logFilterRequestCaptor.capture());
        FilterLogEventsRequest actualFilterLogEventRequest = logFilterRequestCaptor.getValue();
        assertThat(actualFilterLogEventRequest.getLogGroupName()).isEqualTo("myFirstLogGroup");
        assertThat(actualFilterLogEventRequest.getLogStreamNames()).containsExactlyInAnyOrder("myStream1","myStream2","myStream3");
        assertThat(actualFilterLogEventRequest.getFilterPattern()).isEqualTo("{$.event=\"myValue\"}");
        assertThat(actualFilterLogEventRequest.getEndTime()).isBetween(currentTime-1000,currentTime);
        assertThat(actualFilterLogEventRequest.getStartTime()).isBetween(currentTime-((5*60*1000)-1000),currentTime-(5*60*1000));
    }
}
