package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.model.UDeployCollector;
import com.capitalone.dashboard.model.UDeployEnvResCompData;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.capitalone.dashboard.repository.UDeployApplicationRepository;
import com.capitalone.dashboard.repository.UDeployCollectorRepository;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collects {@link EnvironmentComponent} and {@link EnvironmentStatus} data from
 * {@link UDeployApplication}s.
 */
@Component
public class UDeployCollectorTask extends CollectorTask<UDeployCollector> {
	private static final Log LOG = LogFactory
			.getLog(UDeployCollectorTask.class);

	private final UDeployCollectorRepository uDeployCollectorRepository;
	private final UDeployApplicationRepository uDeployApplicationRepository;
	private final UDeployClient uDeployClient;
	private final UDeploySettings uDeploySettings;

	private final EnvironmentComponentRepository envComponentRepository;
	private final EnvironmentStatusRepository environmentStatusRepository;

	private final ComponentRepository dbComponentRepository;

	@Autowired
	public UDeployCollectorTask(TaskScheduler taskScheduler,
			UDeployCollectorRepository uDeployCollectorRepository,
			UDeployApplicationRepository uDeployApplicationRepository,
			EnvironmentComponentRepository envComponentRepository,
			EnvironmentStatusRepository environmentStatusRepository,
			UDeploySettings uDeploySettings, UDeployClient uDeployClient,
			ComponentRepository dbComponentRepository) {
		super(taskScheduler, "UDeploy");
		this.uDeployCollectorRepository = uDeployCollectorRepository;
		this.uDeployApplicationRepository = uDeployApplicationRepository;
		this.uDeploySettings = uDeploySettings;
		this.uDeployClient = uDeployClient;
		this.envComponentRepository = envComponentRepository;
		this.environmentStatusRepository = environmentStatusRepository;
		this.dbComponentRepository = dbComponentRepository;
	}

	@Override
	public UDeployCollector getCollector() {
		return UDeployCollector.prototype(uDeploySettings.getServers());
	}

	@Override
	public BaseCollectorRepository<UDeployCollector> getCollectorRepository() {
		return uDeployCollectorRepository;
	}

	@Override
	public String getCron() {
		return uDeploySettings.getCron();
	}

	@Override
	public void collect(UDeployCollector collector) {
		for (String instanceUrl : collector.getUdeployServers()) {

			logInstanceBanner(instanceUrl);

			long start = System.currentTimeMillis();

			clean(collector);

			addNewApplications(uDeployClient.getApplications(instanceUrl),
					collector);
			updateData(enabledApplications(collector, instanceUrl));

			log("Finished", start);
		}
	}

	/**
	 * Clean up unused deployment collector items
	 *
	 * @param collector
	 *            the {@link UDeployCollector}
	 */

	private void clean(UDeployCollector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
				.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(
						CollectorType.Deployment);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null) {
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<UDeployApplication> appList = new ArrayList<UDeployApplication>();
		Set<ObjectId> udId = new HashSet<ObjectId>();
		udId.add(collector.getId());
		for (UDeployApplication app : uDeployApplicationRepository.findByCollectorIdIn(udId)) {
			if (app != null) {
				app.setEnabled(uniqueIDs.contains(app.getId()));
				appList.add(app);
			}
		}
		uDeployApplicationRepository.save(appList);
	}

	/**
	 * For each {@link UDeployApplication}, update the current
	 * {@link EnvironmentComponent}s and {@link EnvironmentStatus}.
	 *
	 * @param uDeployApplications
	 *            list of {@link UDeployApplication}s
	 */
	private void updateData(List<UDeployApplication> uDeployApplications) {
		/**
		 * steps - 1. get environments 2. for each environment, get resources
		 * and non-compliance resources 3. merge resources and non-compliance to
		 * get component name, versions, resource name, health etc.
		 */
		for (UDeployApplication application : uDeployApplications) {
			long startApp = System.currentTimeMillis();
			for (Environment environment : uDeployClient
					.getEnvironments(application)) {
				List<UDeployEnvResCompData> combinedDataList = uDeployClient
						.getEnvironmentResourceStatusData(application,
								environment);

				for (UDeployEnvResCompData combinedData : combinedDataList) {

					EnvironmentComponent component = new EnvironmentComponent();
					component.setComponentName(combinedData.getComponentName());
					component.setComponentVersion(combinedData
							.getComponentVersion());
					component.setDeployed(combinedData.isDeployed());
					component.setEnvironmentName(combinedData
							.getEnvironmentName());

					component.setEnvironmentName(environment.getName());
					component.setAsOfDate(combinedData.getAsOfDate());
					String environmentURL = StringUtils.removeEnd(
							application.getInstanceUrl(), "/")
							+ "/#environment/" + environment.getId();
					component.setEnvironmentUrl(environmentURL);
					List<EnvironmentComponent> existingComponents = envComponentRepository
							.findByCollectorItemId(application.getId());
					EnvironmentComponent existing = findExistingComponent(
							component, existingComponents);

					if (existing == null) {
						// Add new
						component.setCollectorItemId(application.getId());
						envComponentRepository.save(component);
					} else if (changed(component, existing)) {
						// Update date and deployment status of existing
						existing.setAsOfDate(component.getAsOfDate());
						existing.setDeployed(component.isDeployed());
						existing.setComponentVersion(component.getComponentVersion());
						envComponentRepository.save(existing);
					}
				}

				for (UDeployEnvResCompData data : uDeployClient
						.getEnvironmentResourceStatusData(application,
								environment)) {
					EnvironmentStatus status = new EnvironmentStatus();
					status.setCollectorItemId(data.getCollectorItemId());
					status.setComponentID(data.getComponentID());
					status.setComponentName(data.getComponentName());
					status.setEnvironmentName(data.getEnvironmentName());
					status.setOnline(data.isOnline());
					status.setResourceName(data.getResourceName());
					List<EnvironmentStatus> existingStatuses = environmentStatusRepository
							.findByCollectorItemId(application.getId());
					EnvironmentStatus existing = findExistingStatus(status,
							existingStatuses);
					if (existing == null) {
						// Add new
						status.setCollectorItemId(application.getId());
						environmentStatusRepository.save(status);
					} else if (changed(status, existing)) {
						// Update online status of existing
						existing.setOnline(status.isOnline());
						environmentStatusRepository.save(existing);
					}
				}

			}

			log(" " + application.getApplicationName(), startApp);
		}
	}

	private List<UDeployApplication> enabledApplications(
			UDeployCollector collector, String instanceUrl) {
		return uDeployApplicationRepository.findEnabledApplications(
				collector.getId(), instanceUrl);
	}

	/**
	 * Add any new {@link UDeployApplication}s.
	 *
	 * @param applications
	 *            list of {@link UDeployApplication}s
	 * @param collector
	 *            the {@link UDeployCollector}
	 */
	private void addNewApplications(List<UDeployApplication> applications,
			UDeployCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;

		log("All apps", start, applications.size());
		for (UDeployApplication application : applications) {

			if (isNewApplication(collector, application)) {
				application.setCollectorId(collector.getId());
				application.setEnabled(false);
				application.setDescription(application.getApplicationName());
				try {
					uDeployApplicationRepository.save(application);
				} catch (org.springframework.dao.DuplicateKeyException ce) {
					log("Duplicates items not allowed", 0);

				}
				count++;
			}

		}
		log("New apps", start, count);
	}

	private boolean isNewApplication(UDeployCollector collector,
			UDeployApplication application) {
		return uDeployApplicationRepository.findUDeployApplication(
				collector.getId(), application.getInstanceUrl(),
				application.getApplicationId()) == null;
	}

	private boolean changed(EnvironmentStatus status, EnvironmentStatus existing) {
		return existing.isOnline() != status.isOnline();
	}

	private EnvironmentStatus findExistingStatus(
			final EnvironmentStatus proposed,
			List<EnvironmentStatus> existingStatuses) {

		return Iterables.tryFind(existingStatuses,
				new Predicate<EnvironmentStatus>() {
					@Override
					public boolean apply(EnvironmentStatus existing) {
						return existing.getEnvironmentName().equals(
								proposed.getEnvironmentName())
								&& existing.getComponentName().equals(
										proposed.getComponentName())
								&& existing.getResourceName().equals(
										proposed.getResourceName());
					}
				}).orNull();
	}

	private boolean changed(EnvironmentComponent component,
			EnvironmentComponent existing) {
		return existing.isDeployed() != component.isDeployed()
				|| existing.getAsOfDate() != component.getAsOfDate() || !existing.getComponentVersion().equalsIgnoreCase(component.getComponentVersion());
	}

	private EnvironmentComponent findExistingComponent(
			final EnvironmentComponent proposed,
			List<EnvironmentComponent> existingComponents) {

		return Iterables.tryFind(existingComponents,
				new Predicate<EnvironmentComponent>() {
					@Override
					public boolean apply(EnvironmentComponent existing) {
						return existing.getEnvironmentName().equals(
								proposed.getEnvironmentName())
								&& existing.getComponentName().equals(
										proposed.getComponentName());

					}
				}).orNull();
	}

	private void log(String marker, long start) {
		log(marker, start, null);
	}

	private void log(String text, long start, Integer count) {
		long end = System.currentTimeMillis();
		int maxWidth = 25;
		String elapsed = ((end - start) / 1000) + "s";
		String token2 = "";
		String token3;
		if (count == null) {
			token3 = StringUtils.leftPad(elapsed, 30 - text.length());
		} else {
			maxWidth = 17;
			String countStr = count.toString();
			token2 = StringUtils.leftPad(countStr, 20 - text.length());
			token3 = StringUtils.leftPad(elapsed, 10);
		}
		LOG.info(StringUtils.abbreviate(text, maxWidth) + token2 + token3);
	}

	private void logInstanceBanner(String instanceUrl) {
		LOG.info("------------------------------");
		LOG.info(instanceUrl);
		LOG.info("------------------------------");
	}
}
