package com.capitalone.dashboard.cloudwatch.collector;

import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.amazonaws.services.logs.model.FilteredLogEvent;
import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import com.capitalone.dashboard.cloudwatch.model.CloudWatchJob;
import com.capitalone.dashboard.cloudwatch.model.Series;
import com.capitalone.dashboard.repository.AwsLogCollectorItemRepository;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.LogAnalysizerRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.springframework.scheduling.TaskScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AwsCloudwatchLogAnalyzerTaskTest {

    private AwsCloudwatchLogAnalyzerTask subject;

    private TaskScheduler mockScheduler;

    private BaseCollectorRepository<AwsCloudwatchLogAnalyzer> mockRepo;

    private AwsCloudwatchLogAnalyzerSettings settings;

    private AwsCloudWatchClientFactory mockFactory;

    private LogAnalysizerRepository mockLogRepo;

    private AwsLogCollectorItemRepository mockJobRepo;

    @Before
    public void setup(){
        this.settings = this.produceDefaultSettings();
        this.mockScheduler = mock(TaskScheduler.class);
        this.mockRepo = mock(BaseCollectorRepository.class);
        this.mockLogRepo = mock(LogAnalysizerRepository.class);
        this.mockJobRepo = mock(AwsLogCollectorItemRepository.class);
        this.mockFactory = mock(AwsCloudWatchClientFactory.class);
        this.subject= new AwsCloudwatchLogAnalyzerTask(this.mockScheduler, this.mockRepo, this.mockFactory, this.mockJobRepo,this.mockLogRepo,this.settings);
    }

    private AwsCloudwatchLogAnalyzerSettings produceDefaultSettings(){
        AwsCloudwatchLogAnalyzerSettings settings = new AwsCloudwatchLogAnalyzerSettings();
        settings.setCron("*/5 * * *");
        settings.setLogAnalysisPeriod(2);
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

        assertThat((String)Whitebox.getInternalState(subject,"collectorName")).isEqualTo("AwsCloudwatchLogAnalyzer");
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
    public void collectWithExistingAddsNewMetric() {
        AwsCloudwatchLogAnalyzer mockAnalyzer = mock(AwsCloudwatchLogAnalyzer.class);
        when(mockRepo.findByCollectorTypeAndName(CollectorType.Log,"AwsCloudwatchLogAnalyzer")).thenReturn(Collections.EMPTY_LIST);
        AWSLogsClient mockClient = mock(AWSLogsClient.class);
        when(mockFactory.getInstance()).thenReturn(mockClient);

        FilterLogEventsResult fakeLogEventResult = new FilterLogEventsResult();
        List<FilteredLogEvent> events = new ArrayList<>();
        events.add(new FilteredLogEvent());
        events.add(new FilteredLogEvent());
        fakeLogEventResult.setEvents(events);
        when(mockClient.filterLogEvents(any(FilterLogEventsRequest.class))).thenReturn(fakeLogEventResult);
        ObjectId fakeId = new ObjectId();
        when(mockAnalyzer.getId()).thenReturn(fakeId);

        AwsLogCollectorItem fakeAnalayser = new AwsLogCollectorItem();
        fakeAnalayser.setNiceName("myFirstGraph");
        ObjectId fakeJobId = new ObjectId();
        fakeAnalayser.setId(fakeJobId);
        when(mockJobRepo.findByCollectorIdAndDescription(same(fakeId),eq("MyFirstGraph"))).thenReturn(Collections.singletonList(fakeAnalayser));

        subject.collect(mockAnalyzer);

        long currentTime = System.currentTimeMillis();
        ArgumentCaptor<FilterLogEventsRequest> logFilterRequestCaptor = ArgumentCaptor.forClass(FilterLogEventsRequest.class);
        verify(mockClient).filterLogEvents(logFilterRequestCaptor.capture());
        FilterLogEventsRequest actualFilterLogEventRequest = logFilterRequestCaptor.getValue();
        assertThat(actualFilterLogEventRequest.getLogGroupName()).isEqualTo("myFirstLogGroup");
        assertThat(actualFilterLogEventRequest.getLogStreamNames()).containsExactlyInAnyOrder("myStream1","myStream2","myStream3");
        assertThat(actualFilterLogEventRequest.getFilterPattern()).isEqualTo("{$.event=\"myValue\"}");
        assertThat(actualFilterLogEventRequest.getEndTime()).isBetween(currentTime-1000,currentTime);
        assertThat(actualFilterLogEventRequest.getStartTime()).isBetween(currentTime-((2*60*1000)+1000),currentTime-(2*60*1000));

        ArgumentCaptor<LogAnalysis> captor = ArgumentCaptor.forClass(LogAnalysis.class);
        verify(mockLogRepo).save(captor.capture());

        assertThat(captor.getValue().getCollectorItemId()).isSameAs(fakeJobId);
        assertThat(captor.getValue().getMetrics()).hasSize(1);
        assertThat(captor.getValue().getTimestamp()).isBetween(currentTime-1000, currentTime);
        assertThat(captor.getValue().getName()).isEqualTo("MyFirstGraph");
        assertThat(captor.getValue().getMetrics().get(0).getName()).isEqualTo("event1");
        assertThat(captor.getValue().getMetrics().get(0).getValue()).isEqualTo(2);
    }

    @Test
    public void createsNewCollectorItemWhenNullInCollectorRepo() {
        AwsCloudwatchLogAnalyzer mockAnalyzer = mock(AwsCloudwatchLogAnalyzer.class);
        AWSLogsClient mockClient = mock(AWSLogsClient.class);
        when(mockFactory.getInstance()).thenReturn(mockClient);

        ObjectId fakeCollectorId = new ObjectId();
        when(mockAnalyzer.getId()).thenReturn(fakeCollectorId);
        when(mockJobRepo.findByCollectorIdAndDescription(same(fakeCollectorId),eq("MyFirstGraph"))).thenReturn(null);

        subject.collect(mockAnalyzer);

        ArgumentCaptor<AwsLogCollectorItem> captor = ArgumentCaptor.forClass(AwsLogCollectorItem.class);
        verify(mockJobRepo).save(captor.capture());

        assertThat(captor.getValue()).isNotNull();
        assertThat(captor.getValue().getNiceName()).isEqualTo("MyFirstGraph");
        assertThat(captor.getValue().getDescription()).isEqualTo("MyFirstGraph");
        assertThat(captor.getValue().getCollectorId()).isSameAs(fakeCollectorId);
    }

    @Test
    public void createsNewCollectorItemWhenEmptyInCollectorRepo() {
        AwsCloudwatchLogAnalyzer mockAnalyzer = mock(AwsCloudwatchLogAnalyzer.class);
        AWSLogsClient mockClient = mock(AWSLogsClient.class);
        when(mockFactory.getInstance()).thenReturn(mockClient);

        ObjectId fakeCollectorId = new ObjectId();
        when(mockAnalyzer.getId()).thenReturn(fakeCollectorId);
        when(mockJobRepo.findByCollectorIdAndDescription(same(fakeCollectorId),eq("MyFirstGraph"))).thenReturn(Collections.emptyList());

        subject.collect(mockAnalyzer);

        ArgumentCaptor<AwsLogCollectorItem> captor = ArgumentCaptor.forClass(AwsLogCollectorItem.class);
        verify(mockJobRepo).save(captor.capture());

        assertThat(captor.getValue()).isNotNull();
        assertThat(captor.getValue().getCollectorId()).isSameAs(fakeCollectorId);
    }
}
