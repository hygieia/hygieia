package com.capitalone.dashboard.collector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.model.FortifyCollector;
import com.capitalone.dashboard.model.FortifyProject;
import com.capitalone.dashboard.model.FortifyScanReport;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.capitalone.dashboard.repository.FortifyCollectorRepository;
import com.capitalone.dashboard.repository.FortifyProjectRepository;
import com.capitalone.dashboard.repository.FortifyScanRepository;

@Component
public class FortifyCollectorTask extends CollectorTask<FortifyCollector> {

	private static final Log LOG = LogFactory.getLog(FortifyCollectorTask.class);

	private final FortifyCollectorRepository fortifyCollectorRepository;
	private final FortifyProjectRepository fortifyProjectRepository;
	private final FortifySettings fortifySettings;
	private final ComponentRepository dbComponentRepository;
	private final ConfigurationRepository configurationRepository;
	private final FortifyClient fortifyClient;
	private final FortifyScanRepository fortifyScanRepository;
	private final CodeQualityRepository codeQualityRepository;

	@Autowired
	public FortifyCollectorTask(TaskScheduler taskScheduler, FortifySettings fortifySettings,
			FortifyCollectorRepository fortifyCollectorRepository, FortifyProjectRepository fortifyProjectRepository,
			ComponentRepository componentRepository, ConfigurationRepository configurationRepository,
			FortifyClient fortifyClient, FortifyScanRepository fortifyScanRepository,
			CodeQualityRepository codeQualityRepository) {
		super(taskScheduler, "fortify");
		this.configurationRepository = configurationRepository;
		this.fortifyCollectorRepository = fortifyCollectorRepository;
		this.fortifyProjectRepository = fortifyProjectRepository;
		this.fortifySettings = fortifySettings;
		this.fortifyClient = fortifyClient;
		this.dbComponentRepository = componentRepository;
		this.fortifyScanRepository = fortifyScanRepository;
		this.codeQualityRepository = codeQualityRepository;
	}

	@Override
	public FortifyCollector getCollector() {
		Configuration config = configurationRepository.findByCollectorName("fortify");
		if (config != null) {
			config.decryptOrEncrptInfo();
			if(fortifySettings.getServers().isEmpty()) {
				fortifySettings.setUserNames(new ArrayList<>());
				fortifySettings.setPasswords(new ArrayList<>());
				fortifySettings.setServers(new ArrayList<>());
			} else {
				fortifySettings.getUserNames().clear();
				fortifySettings.getServers().clear();
				fortifySettings.getPasswords().clear();
			}
			for (Map<String, String> configuration : config.getInfo()) {
 				fortifySettings.getServers().add(configuration.get("url"));
				fortifySettings.getUserNames().add(configuration.get("userName"));
				fortifySettings.getPasswords().add(configuration.get("password"));
			}
		}
		return FortifyCollector.prototype(fortifySettings.getServers());
	}

	@Override
	public BaseCollectorRepository<FortifyCollector> getCollectorRepository() {
		return fortifyCollectorRepository;
	}

	@Override
	public String getCron() {
		return fortifySettings.getCron();
	}

	@Override
	public void collect(FortifyCollector collector) {
		long start = System.currentTimeMillis();

		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		List<FortifyProject> existingApplications = fortifyProjectRepository.findByCollectorIdIn(udId);
		List<FortifyProject> latestApplications = new ArrayList<>();
		deleteJobsFromRemovedServers(existingApplications, collector);

		for (String instanceUrl : collector.getFortifyServers()) {
			logBanner(instanceUrl);
			List<FortifyProject> filteredExistingProjects = existingApplications.stream()
					.filter(project -> project.getInstanceUrl().equals(instanceUrl)).collect(Collectors.toList());
			clean(collector, filteredExistingProjects);

			List<FortifyProject> applications = new ArrayList<>();
			try {
				Map<String, JSONObject> applicationsArray = fortifyClient.getApplicationArray(instanceUrl);
				applications = fortifyClient.getApplications(instanceUrl, applicationsArray.values());
				latestApplications.addAll(applications);
				int appSize = ((applications != null) ? applications.size() : 0);
				log("Fetched applications   " + appSize, start);
				deleteUnwantedJobs(latestApplications, filteredExistingProjects, collector);
				addNewProjects(applications, filteredExistingProjects, collector);
				refreshData(applicationsArray, enabledProjects(collector, instanceUrl));

				log("Finished", start);
			} catch (RestClientException e) {
				LOG.error("Error fetching applications from instance URL : " + instanceUrl, e);
			} catch (ParseException e) {
				LOG.error("Error parsing the response : " + instanceUrl, e);
			}
		}
	}

	private List<FortifyProject> enabledProjects(FortifyCollector collector, String instanceUrl) {
		return fortifyProjectRepository.findEnabledProjects(collector.getId(), instanceUrl);
	}

	private void addNewProjects(List<FortifyProject> applications, List<FortifyProject> filteredExistingProjects,
			FortifyCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;
		List<FortifyProject> newApplications = new ArrayList<>();
		for (FortifyProject application : applications) {
			if (!filteredExistingProjects.contains(application)) {
				application.setCollectorId(collector.getId());
				application.setEnabled(false);
				application.setDescription(application.getProjectName());
				newApplications.add(application);
				count++;
			}
		}
		if (!CollectionUtils.isEmpty(newApplications)) {
			fortifyProjectRepository.save(newApplications);
		}
		log("New Applications", start, count);

	}

	private void deleteUnwantedJobs(List<FortifyProject> latestApplications,
			List<FortifyProject> filteredExistingProjects, FortifyCollector collector) {
		List<FortifyProject> deleteJobList = new ArrayList<>();
		for (FortifyProject job : filteredExistingProjects) {
			if (job.isPushed())
				continue; // do not delete jobs that are being pushed via API
			if (!collector.getFortifyServers().contains(job.getInstanceUrl())
					|| (!job.getCollectorId().equals(collector.getId())) || (!latestApplications.contains(job))) {
				if (!job.isEnabled()) {
					deleteJobList.add(job);
				} else {
					CollectionError error = new CollectionError(HttpStatus.NOT_FOUND.toString(), "NOT FOUND");
					job.getErrors().clear();
					job.getErrors().add(error);
					fortifyProjectRepository.save(job);
				}
			}
		}

		if (!CollectionUtils.isEmpty(deleteJobList)) {
			fortifyProjectRepository.delete(deleteJobList);
		}
	}

	private void refreshData(Map<String, JSONObject> applicationsArray, List<FortifyProject> enabledProjects) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+'");
		int count = 0;
		long start = System.currentTimeMillis();
		FortifyScanReport fortifyScanReport = null;
		CodeQuality codeQuality = new CodeQuality();
		CollectionError error = null;
		CodeQualityMetric metric = null;
		for (FortifyProject application : enabledProjects) {
			LOG.info("Fetching data for applicaiton " + application.getProjectName());
			try {
				JSONObject versionJson = applicationsArray.get(application.getVersionId());
				if (versionJson != null) {
					JSONObject currentStateObj = (JSONObject) versionJson.get("currentState");
					if (currentStateObj.get("metricEvaluationDate") != null) {
						Date date = format.parse(currentStateObj.get("metricEvaluationDate").toString());
						if (isNewCodeQualityData(application, date.getTime())) {
							fortifyScanReport = fortifyClient.getFortifyReport(application, versionJson);
							codeQuality.setCollectorItemId(fortifyScanReport.getCollectorItemId());
							codeQuality.setName(fortifyScanReport.getName());
							codeQuality.setTimestamp(fortifyScanReport.getTimestamp());
							codeQuality.setType(CodeQualityType.SecurityAnalysis);
							codeQuality.setUrl(fortifyScanReport.getUrl());
							codeQuality.setVersion(fortifyScanReport.getVersion());
							for (String threat : fortifyScanReport.getThreats().keySet()) {
								metric = new CodeQualityMetric();
								Integer countValue = fortifyScanReport.getThreats().get(threat).getCount();
								metric.setFormattedValue(countValue.toString());
								metric.setName(threat);
								switch (threat) {
								case "Critical":
									metric.setStatus(CodeQualityMetricStatus.Alert);
									break;
								case "High":
									metric.setStatus(CodeQualityMetricStatus.Warning);
									break;
								case "Medium":
									metric.setStatus(CodeQualityMetricStatus.Warning);
									break;
								default:
									metric.setStatus(CodeQualityMetricStatus.Ok);
									break;
								}
								codeQuality.getMetrics().add(metric);
							}
							codeQualityRepository.save(codeQuality);
							if (isNewFortifyScanData(application, fortifyScanReport)) {
								fortifyScanRepository.save(fortifyScanReport);
							}
						}
						application.getErrors().clear();
						count++;
					} else {
						error = new CollectionError("No analysis data found", application.getProjectName());
					}
				} else {
					error = new CollectionError("NOT FOUND", application.getProjectName());
				}
			} catch (RestClientException | java.text.ParseException | ParseException e) {
				error = new CollectionError(CollectionError.UNKNOWN_HOST, application.getProjectName());
			} finally {
				if (!application.getErrors().isEmpty()) {
					application.getErrors().clear();
				}
				if (error != null) {
					application.getErrors().add(error);
				}
			}
		}
		if (!CollectionUtils.isEmpty(enabledProjects)) {
			fortifyProjectRepository.save(enabledProjects);
		}
		log("Refreshed Applications", start, count);
	}

	private boolean isNewCodeQualityData(FortifyProject application, long timeStamp) {
		CodeQuality existingReport = codeQualityRepository.findByCollectorItemIdAndTimestamp(application.getId(),
				timeStamp);
		if (existingReport == null) {
			application.setLastUpdated(timeStamp);
			return true;
		} else {
			application.setLastUpdated(existingReport.getTimestamp());
			return false;
		}
	}

	private boolean isNewFortifyScanData(FortifyProject application, FortifyScanReport fortifyScanReport) {
		return fortifyScanRepository.findByCollectorItemIdAndTimestamp(application.getId(),
				fortifyScanReport.getTimestamp()) == null;
	}

	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
	private void clean(FortifyCollector collector, List<FortifyProject> existingApplications) {
		Set<ObjectId> uniqueIDs = new HashSet<>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.StaticSecurityScan);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null && ci.getCollectorId().equals(collector.getId())) {
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<FortifyProject> stateChangeJobList = new ArrayList<>();
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		for (FortifyProject job : existingApplications) {
			if ((job.isEnabled() && !uniqueIDs.contains(job.getId()))
					|| (!job.isEnabled() && uniqueIDs.contains(job.getId()))) {
				job.setEnabled(uniqueIDs.contains(job.getId()));
				stateChangeJobList.add(job);
			}
		}
		if (!CollectionUtils.isEmpty(stateChangeJobList)) {
			fortifyProjectRepository.save(stateChangeJobList);
		}
	}

	private void deleteJobsFromRemovedServers(List<FortifyProject> existingJobs, FortifyCollector collector) {
		List<FortifyProject> jobsfromDeletedServer = existingJobs.stream()
				.filter(project -> !collector.getFortifyServers().contains(project.getInstanceUrl()))
				.collect(Collectors.toList());
		fortifyProjectRepository.delete(jobsfromDeletedServer);
	}

}
