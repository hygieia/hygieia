package com.capitalone.dashboard.collector;

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
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.model.RallyBurnDownData;
import com.capitalone.dashboard.model.RallyCollector;
import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.model.RallyProject;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.capitalone.dashboard.repository.RallyBurnDownRepository;
import com.capitalone.dashboard.repository.RallyCollectorRepository;
import com.capitalone.dashboard.repository.RallyFeatureRepository;
import com.capitalone.dashboard.repository.RallyProjectRepository;

@Component
public class RallyCollectorTask extends CollectorTask<RallyCollector> {
	private static final Log LOG = LogFactory.getLog(RallyCollectorTask.class);

	private final RallyCollectorRepository rallyCollectorRepository;
	private final RallyProjectRepository rallyProjectRepository;
	private final RallyFeatureRepository rallyFeatureRepository;
	private final RallyClient rallyClient;
	private final RallySettings rallySettings;
	private final ComponentRepository dbComponentRepository;
	private final RallyBurnDownRepository rallyBurnDownRepository;
	private final ConfigurationRepository configurationRepository;

	@Autowired
	public RallyCollectorTask(TaskScheduler taskScheduler, RallyCollectorRepository rallyCollectorRepository,
			RallyProjectRepository rallyProjectRepository, RallyFeatureRepository rallyFeatureRepository,
			RallySettings rallySettings, RallyClient rallyClient, ComponentRepository dbComponentRepository,
			RallyBurnDownRepository rallyBurnDownRepository, ConfigurationRepository configurationRepository) {
		super(taskScheduler, "Rally");
		this.rallyCollectorRepository = rallyCollectorRepository;
		this.rallyProjectRepository = rallyProjectRepository;
		this.rallyFeatureRepository = rallyFeatureRepository;
		this.rallySettings = rallySettings;
		this.rallyClient = rallyClient;
		this.dbComponentRepository = dbComponentRepository;
		this.rallyBurnDownRepository = rallyBurnDownRepository;
		this.configurationRepository = configurationRepository;
	}

	@Override
	public RallyCollector getCollector() {
		Configuration config = configurationRepository.findByCollectorName("Rally");
	
		if(rallySettings.getHttpProxyHost()!=null && rallySettings.getHttpProxyPort()!=null 
													&& rallySettings.getHttpsProxyHost()!=null
													&& rallySettings.getHttpsProxyPort()!=null) {
		System.setProperty("http.proxyHost", rallySettings.getHttpProxyHost());
		System.setProperty("https.proxyHost", rallySettings.getHttpsProxyHost());
		System.setProperty("http.proxyPort", rallySettings.getHttpProxyPort());
		System.setProperty("https.proxyPort", rallySettings.getHttpsProxyPort());
		}
			
			if(rallySettings.getServers()==null) {
				rallySettings.setUsernames(new ArrayList<>());
				rallySettings.setPasswords(new ArrayList<>());
				rallySettings.setServers(new ArrayList<>());
			} else {
				rallySettings.getUsernames().clear();
				rallySettings.getServers().clear();
				rallySettings.getPasswords().clear();
			}
				
			if (config != null ) {
				config.decryptOrEncrptInfo();
				// To clear the username and password from existing run and
				// pick the latest
				
			for (Map<String, String> rallyServer : config.getInfo()) {
				rallySettings.getServers().add(rallyServer.get("url"));
				rallySettings.getUsernames().add(rallyServer.get("userName"));
				rallySettings.getPasswords().add(rallyServer.get("password"));

			}
		}
		return RallyCollector.prototype(rallySettings.getServers());
	}

	@Override
	public BaseCollectorRepository<RallyCollector> getCollectorRepository() {
		return rallyCollectorRepository;
	}

	@Override
	public String getCron() {
		return rallySettings.getCron();
	}

	@Override
	public void collect(RallyCollector collector) {
		long start = System.currentTimeMillis();
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		List<RallyProject> existingProjects = rallyProjectRepository.findByCollectorIdIn(udId);

		List<RallyProject> latestProjects = new ArrayList<>();

		clean(collector, existingProjects);

		for (String instanceUrl : collector.getRallyServers()) {
			logBanner(instanceUrl);
			List<RallyProject> filteredExistingProjects = existingProjects.stream()
					.filter(project -> project.getInstanceUrl().equals(instanceUrl)).collect(Collectors.toList());

			List<RallyProject> projects = new ArrayList<>();
			try {
				projects = rallyClient.getProjects(instanceUrl);
				latestProjects.addAll(projects);
				int projSize = ((projects != null) ? projects.size() : 0);
				log("Fetched projects   " + projSize, start);
				addNewProjects(projects, filteredExistingProjects, collector);
				deleteUnwantedJobs(latestProjects, filteredExistingProjects, collector);
				refreshData(enabledProjects(collector, instanceUrl));

				log("Finished", start);
			} catch (ParseException | RestClientException e) {
				LOG.error("Error fetching data for the instance URL :" + instanceUrl, e);
			}

		}
	}

	/**
	 * Clean up unused rally collector items
	 *
	 * @param collector
	 *            the {@link RallyCollector}
	 */

	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
	private void clean(RallyCollector collector, List<RallyProject> existingProjects) {
		Set<ObjectId> uniqueIDs = new HashSet<>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.AgileTool);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null && ci.getCollectorId().equals(collector.getId())) {
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<RallyProject> stateChangeJobList = new ArrayList<>();
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		for (RallyProject job : existingProjects) {
			// collect the jobs that need to change state : enabled vs disabled.
			if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) || // if
			// it
			// was
			// enabled
			// but
			// not
			// on
			// a
			// dashboard
					(!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR
				// it
				// was
				// disabled
				// and
				// now
				// on
				// a
				// dashboard
				job.setEnabled(uniqueIDs.contains(job.getId()));
				stateChangeJobList.add(job);
			}
		}
		if (!CollectionUtils.isEmpty(stateChangeJobList)) {
			rallyProjectRepository.save(stateChangeJobList);
		}
	}

	private void deleteUnwantedJobs(List<RallyProject> latestProjects, List<RallyProject> existingProjects,
			RallyCollector collector) {
		List<RallyProject> deleteJobList = new ArrayList<>();

		// First delete collector items that are not supposed to be collected
		// anymore because the servers have moved(?)
		for (RallyProject job : existingProjects) {
			if (job.isPushed())
				continue; // do not delete jobs that are being pushed via API

			if (!collector.getRallyServers().contains(job.getInstanceUrl())
					|| (!job.getCollectorId().equals(collector.getId())) || (!latestProjects.contains(job))) {
				if (!job.isEnabled()) {
					deleteJobList.add(job);
				} else {
					CollectionError error = new CollectionError(HttpStatus.NOT_FOUND.toString(),
							"NOT FOUND");
					job.getErrors().clear();
					job.getErrors().add(error);
					rallyProjectRepository.save(job);
				}
			}
		}
		if (!CollectionUtils.isEmpty(deleteJobList)) {
			rallyProjectRepository.delete(deleteJobList);
		}

	}

	private void refreshData(List<RallyProject> rallyProjects) {
		long start = System.currentTimeMillis();
		int count = 0;
		Date currentDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		for (RallyProject project : rallyProjects) {

			if (project.getErrors().stream()
					.filter(error -> error.getErrorMessage().equalsIgnoreCase("NOT FOUND"))
					.collect(Collectors.toList()).isEmpty()) {
				LOG.info("Fetching Project details for : " + project.getProjectName());
				try {
					List<RallyFeature> iterationDatas = rallyClient.getRallyIterations(project);
					List<RallyFeature> iterationsForProject = rallyFeatureRepository
							.findByProjectId(project.getProjectId());

					for (RallyFeature iteration : iterationsForProject) {						
						if(format.parse(format.format(currentDate)).after(format.parse(iteration.getEndDate().toString())) && iteration.getRemainingDays()!=0) {
							iteration.setRemainingDays(0);
							rallyFeatureRepository.save(iteration);
						}
					}

					if (iterationsForProject.isEmpty()) {
						rallyFeatureRepository.save(iterationDatas);
					} else {
						for (RallyFeature currentIteration : iterationDatas) {

							RallyFeature truncateData = rallyFeatureRepository.findByRallyWidgetDetails(
									currentIteration.getProjectId(), currentIteration.getIterationId());

							if (truncateData != null) { // if present updates
														// the
														// existing record with
														// the
														// latest..
								currentIteration.setId(truncateData.getId());
							}
							JSONArray userStories = rallyClient.getIterationStories(currentIteration);
							currentIteration.getStoryStages().add(rallyClient.getStoryStages(currentIteration.getProjectId(), userStories));
							rallyFeatureRepository.save(currentIteration);

							refreshIterationBurnDownData(currentIteration);

							count++;
						}
					}
					project.setLastUpdated(System.currentTimeMillis());
					project.getErrors().clear();

				} catch (HttpStatusCodeException hc) {
					LOG.error("Error fetching data for:" + project.getProjectName(), hc);
					if (hc.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
						CollectionError error = new CollectionError(hc.getStatusCode().toString(), hc.getMessage());
						project.getErrors().add(error);
					}
				} catch (ResourceAccessException ex) {
					if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
						LOG.error("Error fetching data for:" + project.getProjectName(), ex);
					} else {
						LOG.error("Error fetching data for:" + project.getProjectName(), ex);
						CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST,
								project.getProjectName());
						project.getErrors().add(error);
					}
				} catch (RestClientException ex) {
					LOG.error("Error fetching data for:" + project.getProjectName(), ex);
					CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST, project.getProjectName());
					project.getErrors().add(error);
				} catch (ParseException e) {
					LOG.error("Error fetching data for:" + project.getProjectName(), e);
					CollectionError error = new CollectionError("Error parsing data", project.getProjectName());
					project.getErrors().add(error);
				} catch (java.text.ParseException e) {
					LOG.error("Parsing date error "+e);
				} finally {
					rallyProjectRepository.save(project);
				}

			}
		}
		log("Updated", start, count);
	}

	private void refreshIterationBurnDownData(RallyFeature currentIteration)
			throws RestClientException, ParseException {
		
		String iterationId = currentIteration.getIterationId();
		String projectId = currentIteration.getProjectId();

		RallyBurnDownData existingRallyBurnDownData = rallyBurnDownRepository.findByIterationIdAndProjectId(iterationId,
				projectId);

		RallyBurnDownData rallyBurnDownData = rallyClient.getBurnDownData(currentIteration,
				rallyClient.getIterationStories(currentIteration), existingRallyBurnDownData);
		rallyBurnDownRepository.save(rallyBurnDownData);

	}

	private List<RallyProject> enabledProjects(RallyCollector collector, String instanceUrl) {
		return rallyProjectRepository.findEnabledProjects(collector.getId(), instanceUrl);
	}

	private void addNewProjects(List<RallyProject> projects, List<RallyProject> existingProjects,
			RallyCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;
		List<RallyProject> newProjects = new ArrayList<>();
		for (RallyProject project : projects) {
			if (!existingProjects.contains(project)) {
				project.setCollectorId(collector.getId());
				project.setEnabled(false);
				project.setDescription(project.getProjectName());
				newProjects.add(project);
				count++;
			}
		}
		// save all in one shot
		if (!CollectionUtils.isEmpty(newProjects)) {
			rallyProjectRepository.save(newProjects);
		}
		log("New projects", start, count);
	}

	@SuppressWarnings("unused")
	private boolean isNewProject(RallyCollector collector, RallyProject application) {
		return rallyProjectRepository.findRallyProject(collector.getId(), application.getInstanceUrl(),
				application.getProjectId()) == null;
	}

	private boolean isNewRallyData(RallyProject project, RallyFeature iterations) {
		return rallyFeatureRepository.findByCollectorItemIdAndTimestamp(project.getId(),
				iterations.getLastUpdated()) == null;
	}

}