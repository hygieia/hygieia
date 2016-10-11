package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.Artifact;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityRepository;
import com.capitalone.dashboard.utils.CodeQualityConverter;
import junit.framework.TestCase;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;


/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsCodeQualityCollectorTaskTest {

  private JenkinsCodeQualityCollectorTask testee;

  private TaskScheduler mockScheduler;
  private JenkinsCodeQualityRepository mockRepo;
  private JenkinsClient mockJenkinsHelper;
  private CodeQualityConverter mockCodeQualityConverter;
  private CodeQualityRepository mockCodeQualityRepository;

  @Before
  public void setup() {
    mockScheduler = mock(TaskScheduler.class);
    mockRepo = mock(JenkinsCodeQualityRepository.class);
    mockJenkinsHelper = mock(JenkinsClient.class);
    mockCodeQualityConverter = mock(CodeQualityConverter.class);
    mockCodeQualityRepository = mock(CodeQualityRepository.class);
    this.testee = new JenkinsCodeQualityCollectorTask(mockScheduler, mockRepo, "0 * * * * *", mockJenkinsHelper, mockCodeQualityConverter, mockCodeQualityRepository);
  }

  @Test
  public void getCollectorReturnsAJenkinsCodeQualityCollector() {

    assertThat(testee.getCollector()).isNotNull().isInstanceOf(JenkinsCodeQualityCollector.class);
  }

  @Test
  public void getCollectorRepositoryReturnsTheRepository() {
    assertThat(testee.getCollectorRepository()).isNotNull().isSameAs(mockRepo);
  }

  @Test
  public void onStartupTheCronJobIsScheduledAndStartStausUpdated() {
    JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
    when(mockRepo.findByName(eq("JenkinsCodeQuality"))).thenReturn(mockCollector);

    testee.onStartup();

    verify(mockRepo).findByName(eq("JenkinsCodeQuality"));
    ArgumentCaptor<Trigger> capturedTrigger = ArgumentCaptor.forClass(Trigger.class);
    verify(mockScheduler).schedule(same(testee), capturedTrigger.capture());
    // TODO assert the cron trigger!

    verify(mockRepo).save(same(mockCollector));
    verify(mockCollector).setOnline(eq(true));
  }

  @Test
  public void collectCausesACollectionOfResultsFromConfiguredJobs() throws Exception {

    JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
    List<String> buildServers = new ArrayList<>();
    buildServers.add("http://buildserver");
    buildServers.add("http://buildserver2");
    when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> buildServers);

    List<JenkinsJob> allJenkinsJobs = getJenkinsJobs();
    when(mockJenkinsHelper.getJobs(argThat(hasItems("http://buildserver", "http://buildserver")))).thenReturn(allJenkinsJobs);
    JunitXmlReport testXmlReport = new JunitXmlReport();
    JunitXmlReport testXmlReport1 = new JunitXmlReport();
    List<JunitXmlReport> testXmlReports = Arrays.asList(testXmlReport, testXmlReport1);

    when(mockJenkinsHelper.getLatestArtifacts(eq(JunitXmlReport.class), any(JenkinsJob.class), anyList())).thenReturn(testXmlReports);

    Set<CodeQualityMetric> codeMetrics = getCodeQualityMetrics(Arrays.asList(tuple("test_success_density", "5"), tuple("test_failures", "1"), tuple("test_errors", "2"), tuple("tests", "8")));
    when(mockCodeQualityConverter.analyse(same(testXmlReport))).thenReturn(codeMetrics);

    Set<CodeQualityMetric> codeMetrics1 = getCodeQualityMetrics(Arrays.asList(tuple("test_success_density", "5"), tuple("test_failures", "0"), tuple("test_errors", "1"), tuple("tests", "6")));
    when(mockCodeQualityConverter.analyse(same(testXmlReport1))).thenReturn(codeMetrics1);

    // test
    testee.collect(mockCollector);

    ArgumentCaptor<JenkinsJob> jobCapture = ArgumentCaptor.forClass(JenkinsJob.class);
    ArgumentCaptor<List> patternListCaptor = ArgumentCaptor.forClass(List.class);
    verify(mockJenkinsHelper).getLatestArtifacts(eq(JunitXmlReport.class), jobCapture.capture(), patternListCaptor.capture());
    JenkinsJob capturedJob = jobCapture.getValue();
    assertThat(capturedJob).satisfies(job -> "job2".equals(job.getJobName())).satisfies(job -> "http://buildserver".equals(job.getJenkinsServer()));
    List<Pattern> capturedPatterns = patternListCaptor.getValue();
    assertThat(capturedPatterns).hasSize(1).allMatch(
        pattern -> pattern.pattern().equals(".*\\.xml"));

    verify(mockCodeQualityConverter).analyse(same(testXmlReport));
    ArgumentCaptor<CodeQuality> argumentCaptor = ArgumentCaptor.forClass(CodeQuality.class);
    verify(mockCodeQualityRepository).save(argumentCaptor.capture());
    CodeQuality capturedCodeQuality = argumentCaptor.getValue();
    assertThat(capturedCodeQuality.getMetrics()).extracting("name", "formattedValue").contains(tuple("test_success_density", "10"), tuple("test_failures", "1"), tuple("test_errors", "3"), tuple("tests", "14"));
  }

  private Set<CodeQualityMetric> getCodeQualityMetrics(List<Tuple> tuples) {
    Set<CodeQualityMetric> codeMetrics = new HashSet<>();
    for (Tuple tuple : tuples) {
      CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
      codeQualityMetric.setName((String) tuple.toArray()[0]);
      codeQualityMetric.setFormattedValue((String) tuple.toArray()[1]);
      codeMetrics.add(codeQualityMetric);
    }

    return codeMetrics;
  }

  private List<JenkinsJob> getJenkinsJobs() {
    List<JenkinsJob> allJenkinsJobs = new ArrayList<>();
    allJenkinsJobs.add(JenkinsJob.newBuilder()
        .jenkinsServer("http://buildserver2").jobName("job1")
        .artifact(Artifact.newBuilder().artifactName("junit.xml").build())
        .artifact(Artifact.newBuilder().artifactName("something.war").build())
        .build());
    allJenkinsJobs.add(JenkinsJob.newBuilder()
        .jenkinsServer("http://buildserver").jobName("job2")
        .artifact(Artifact.newBuilder().artifactName("junit.txt").build())
        .artifact(Artifact.newBuilder().artifactName("something.war").build())
        .build());
    allJenkinsJobs.add(JenkinsJob.newBuilder()
        .jenkinsServer("http://buildserver").jobName("job3")
        .artifact(Artifact.newBuilder().artifactName("something.war").path("/artifact/").build())
        .build());
    return allJenkinsJobs;
  }

  @Test
  public void multipleArtifactsAvailableFromSingleJob() {
    TestCase.fail();
  }

  @Test
  public void jenkinsHelperDoesNotReturnAnyJobsTheCollectShouldReturn() {
    TestCase.fail();
  }

  @Test
  public void unableToParseArtifactIntoCorrectFormat() {
    TestCase.fail();
  }

}