package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.JenkinsCucumberTestCollector;
import com.capitalone.dashboard.model.JenkinsJob;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.JenkinsCucumberTestCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCucumberTestJobRepository;
import com.capitalone.dashboard.repository.TestResultRepository;

/**
 * Created by Kyle Heide on 2/12/15.
 */
@Component
public class JenkinsCucumberTestCollectorTask extends
		CollectorTask<JenkinsCucumberTestCollector> {

	private static final Log LOG = LogFactory
			.getLog(JenkinsCucumberTestCollector.class);

	private final JenkinsCucumberTestCollectorRepository jenkinsCucumberTestCollectorRepository;
	private final JenkinsCucumberTestJobRepository jenkinsCucumberTestJobRepository;
	private final TestResultRepository testResultRepository;
	private final JenkinsClient jenkinsClient;
	private final JenkinsSettings jenkinsCucumberTestSettings;
	private final ComponentRepository dbComponentRepository;
	private final int CLEANUP_INTERVAL = 3600000;

	@Autowired
	public JenkinsCucumberTestCollectorTask(
			TaskScheduler taskScheduler,
			JenkinsCucumberTestCollectorRepository jenkinsCucumberTestCollectorRepository,
			JenkinsCucumberTestJobRepository jenkinsCucumberTestJobRepository,
			TestResultRepository testResultRepository,
			JenkinsClient jenkinsCucumberTestClient,
			JenkinsSettings jenkinsCucumberTestSettings,
			ComponentRepository dbComponentRepository) {
		super(taskScheduler, "JenkinsCucumberTest");
		this.jenkinsCucumberTestCollectorRepository = jenkinsCucumberTestCollectorRepository;
		this.jenkinsCucumberTestJobRepository = jenkinsCucumberTestJobRepository;
		this.testResultRepository = testResultRepository;
		this.jenkinsClient = jenkinsCucumberTestClient;
		this.jenkinsCucumberTestSettings = jenkinsCucumberTestSettings;
		this.dbComponentRepository = dbComponentRepository;
	}

	@Override
	public JenkinsCucumberTestCollector getCollector() {
		return JenkinsCucumberTestCollector
				.prototype(jenkinsCucumberTestSettings.getServers());
	}

	@Override
	public BaseCollectorRepository<JenkinsCucumberTestCollector> getCollectorRepository() {
		return jenkinsCucumberTestCollectorRepository;
	}

	@Override
	public String getCron() {
		return jenkinsCucumberTestSettings.getCron();
	}

	@Override
	public void collect(JenkinsCucumberTestCollector collector) {

		long start = System.currentTimeMillis();

		// Clean up every hour
		if ((start - collector.getLastExecuted()) > CLEANUP_INTERVAL) {
			clean(collector);
		}

		for (String instanceUrl : collector.getBuildServers()) {
			logInstanceBanner(instanceUrl);

			Map<JenkinsJob, Set<Build>> buildsByJob = jenkinsClient
					.getInstanceJobs(instanceUrl);
			log("Fetched jobs", start);

			addNewJobs(buildsByJob.keySet(), collector);

			addNewTestSuites(enabledJobs(collector, instanceUrl), buildsByJob);

			log("Finished", start);
		}

	}

	/**
	 * Clean up unused hudson/jenkins collector items
	 *
	 * @param collector
	 *            the {@link HudsonCollector}
	 */

	private void clean(JenkinsCucumberTestCollector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
				.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(
						CollectorType.Test);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null && ci.getCollectorId().equals(collector.getId())) {
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<JenkinsJob> jobList = new ArrayList<JenkinsJob>();
		Set<ObjectId> udId = new HashSet<ObjectId>();
		udId.add(collector.getId());
		for (JenkinsJob job : jenkinsCucumberTestJobRepository
				.findByCollectorIdIn(udId)) {
			if (job != null) {
				job.setEnabled(uniqueIDs.contains(job.getId()));
				jobList.add(job);
			}
		}
		jenkinsCucumberTestJobRepository.save(jobList);
	}

	// Jenkins Helper methods

	private List<JenkinsJob> enabledJobs(
			JenkinsCucumberTestCollector collector, String instanceUrl) {
		return jenkinsCucumberTestJobRepository.findEnabledJenkinsJobs(
				collector.getId(), instanceUrl);
	}

	/**
	 * Adds new {@link JenkinsJob}s to the database as disabled jobs.
	 *
	 * @param jobs
	 *            list of {@link JenkinsJob}s
	 * @param collector
	 *            the {@link JenkinsCucumberTestCollector}
	 */
	private void addNewJobs(Set<JenkinsJob> jobs,
			JenkinsCucumberTestCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;

		for (JenkinsJob job : jobs) {

			if (jenkinsClient.buildHasCucumberResults(job.getJobUrl())
					&& isNewJob(collector, job)) {
				job.setCollectorId(collector.getId());
				job.setEnabled(false); // Do not enable for collection. Will be
										// enabled when added to dashboard
				job.setDescription(job.getJobName());
				jenkinsCucumberTestJobRepository.save(job);
				count++;
			}

		}
		log("New jobs", start, count);
	}

	private void addNewTestSuites(List<JenkinsJob> enabledJobs,
			Map<JenkinsJob, Set<Build>> buildsByJob) {
		long start = System.currentTimeMillis();
		int count = 0;

		for (JenkinsJob job : enabledJobs) {

			for (Build buildSummary : nullSafe(buildsByJob.get(job))) {

				if (jenkinsClient.buildHasCucumberResults(buildSummary
						.getBuildUrl())
						&& isNewCucumberResult(job, buildSummary)) {

					// Obtain the Test Result
					TestResult result = jenkinsClient
							.getCucumberTestResult(buildSummary.getBuildUrl());
					if (result != null) {
						result.setCollectorItemId(job.getId());
						result.setTimestamp(System.currentTimeMillis());
						testResultRepository.save(result);
						count++;
					}
				}
			}
		}
		log("New test suites", start, count);
	}

	private boolean isNewJob(JenkinsCucumberTestCollector collector,
			JenkinsJob job) {
		return jenkinsCucumberTestJobRepository.findJenkinsJob(
				collector.getId(), job.getInstanceUrl(), job.getJobName()) == null;
	}

	private boolean isNewCucumberResult(JenkinsJob job, Build build) {
		return testResultRepository.findByCollectorItemIdAndExecutionId(
				job.getId(), build.getNumber()) == null;
	}

	private Set<Build> nullSafe(Set<Build> builds) {
		return builds == null ? new HashSet<Build>() : builds;
	}

	// Helper Log Methods TODO: these should be moved to the super class in core

	private void log(String marker, long start) {
		log(marker, start, null);
	}

	private void log(String text, long start, Integer count) {
		long end = System.currentTimeMillis();
		String elapsed = ((end - start) / 1000) + "s";
		String token2 = "";
		String token3;
		if (count == null) {
			token3 = StringUtils.leftPad(elapsed, 30 - text.length());
		} else {
			String countStr = count.toString();
			token2 = StringUtils.leftPad(countStr, 20 - text.length());
			token3 = StringUtils.leftPad(elapsed, 10);
		}
		LOG.info(text + token2 + token3);
	}

	private void logInstanceBanner(String instanceUrl) {
		LOG.info("------------------------------");
		LOG.info(instanceUrl);
		LOG.info("------------------------------");
	}

}
