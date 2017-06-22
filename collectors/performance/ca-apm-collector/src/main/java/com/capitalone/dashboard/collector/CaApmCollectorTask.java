package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.CaApm;
import com.capitalone.dashboard.model.CaApmCollector;
import com.capitalone.dashboard.model.CaApmCollectorItem;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CaApmAlertRepository;
import com.capitalone.dashboard.repository.CaApmCollectorRepository;
import com.capitalone.dashboard.repository.CaApmRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

import alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot;
import metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo;

@Component
@SuppressWarnings("PMD")
public class CaApmCollectorTask extends CollectorTask<CaApmCollector> {

	private static final Log LOG = LogFactory.getLog(CaApmCollectorTask.class);
	private final CaApmClient caApmClient;
	private final CaApmSettings caApmSettings;
	private final CaApmCollectorRepository caApmCollectorRepository;
	private final ComponentRepository dbComponentRepository;
	private static final int CLEANUP_INTERVAL = 360000;
	private final CaApmRepository caApmRepository;
	private final CaApmAlertRepository caApmAlertRepository;

	@Autowired
	public CaApmCollectorTask(TaskScheduler taskScheduler, CaApmClient caApmClient, CaApmSettings caApmSettings,
			CaApmCollectorRepository caApmCollectorRepository, ComponentRepository dbComponentRepository,
			CaApmRepository caApmRepository, CaApmAlertRepository caApmAlertRepository) {
		super(taskScheduler, "CaApm");
		this.caApmClient = caApmClient;
		this.caApmSettings = caApmSettings;
		this.caApmCollectorRepository = caApmCollectorRepository;
		this.dbComponentRepository = dbComponentRepository;
		this.caApmRepository = caApmRepository;
		this.caApmAlertRepository = caApmAlertRepository;
	}

	@Override
	public void collect(CaApmCollector collector) {
		long start = System.currentTimeMillis();
		log("Started::::::::::::::::::::::::::::::", start);
		if ((start - collector.getLastExecuted()) > CLEANUP_INTERVAL) {
			clean(collector);
		}
		try {
			LOG.info("collect ::TRY::");
			ManagementModuleInfo[] manModules = caApmClient.getListOfManagementModules(caApmSettings);
			addNewModule(manModules, collector);
			addNewAlerts(enabledModules(collector));
			log("Finished", start);
		} catch (Exception e) {
			e.printStackTrace();
			log("Exception", start);
		}
	}

	private void addNewAlerts(List<CaApmCollectorItem> enabledModules) throws Exception {
		LOG.info("Enter ::addNewAlerts::");
		long start = System.currentTimeMillis();
		int count = 0;
		caApmAlertRepository.deleteAll();
		LOG.info("Enter ::deleteAll::");
		for (CaApmCollectorItem module : enabledModules) {
			DAllAlertsSnapshot[] alerts = caApmClient.getAllAlertsSnapshotForManagementModule(caApmSettings,
					module.getModuleName());
			LOG.info("Enter ::addNewAlerts::module.getModuleName()::" + module.getModuleName());
			for (DAllAlertsSnapshot alert : alerts) {
				CaApm apmAlert = new CaApm();
				apmAlert.setAlertCurrStatus(alert.getAlertCurrStatus());
				apmAlert.setAlertPrevStatus(alert.getAlertPrevStatus());
				apmAlert.setAlertName(alert.getAlertName());
				apmAlert.setManModuleName(alert.getManModuleName());
				apmAlert.setSimpleAlert(alert.isSimpleAlert());
				apmAlert.setThresholdValue(alert.getThresholdValue());
				apmAlert.setAlertStatusChanged(alert.isAlertStatusChanged());
				caApmAlertRepository.save(apmAlert);
				count++;
			}

		}
		log("New Alerts", start, count);
	}

	private List<CaApmCollectorItem> enabledModules(CaApmCollector collector) {
		return caApmRepository.findEnabledModules(collector.getId());
	}

	private void addNewModule(ManagementModuleInfo[] manModules, CaApmCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;
		for (ManagementModuleInfo module : manModules) {

			if (isNewModule(collector, module)) {
				LOG.info("New ::addNewModule::");
				CaApmCollectorItem caApmItem = new CaApmCollectorItem();
				caApmItem.setDescription(module.getDescription());
				caApmItem.setModuleName(module.getManModuleName());
				caApmItem.setJarFileName(module.getJarFileName());
				caApmItem.setDomainName(module.getDomainName());
				caApmItem.setCollectorId(collector.getId());
				caApmRepository.save(caApmItem);
				count++;
			}

		}
		log("New jobs", start, count);
	}

	private boolean isNewModule(CaApmCollector collector, ManagementModuleInfo module) {
		return caApmRepository.findModule(collector.getId(), module.getManModuleName(), module.getDomainName()) == null;
	}

	@Override
	public CaApmCollector getCollector() {
		// TODO Auto-generated method stub
		return CaApmCollector.prototype();
	}

	@Override
	public BaseCollectorRepository<CaApmCollector> getCollectorRepository() {
		// TODO Auto-generated method stub
		return caApmCollectorRepository;
	}

	@Override
	public String getCron() {
		// TODO Auto-generated method stub
		return caApmSettings.getCron();
	}

	private void clean(CaApmCollector collector) {
		Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
			if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
				List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.CaApm);
				if (itemList != null) {
					for (CollectorItem ci : itemList) {
						if (ci != null && ci.getCollectorId().equals(collector.getId())) {
							uniqueIDs.add(ci.getId());
						}
					}
				}
			}
		}
		List<CaApmCollectorItem> moduleList = new ArrayList<CaApmCollectorItem>();
		Set<ObjectId> udId = new HashSet<ObjectId>();
		udId.add(collector.getId());
		LOG.info("collector.getId()::::" + collector.getId());
		for (CaApmCollectorItem module : caApmRepository.findByCollectorIdIn(udId)) {
			if (module != null) {
				module.setEnabled(uniqueIDs.contains(module.getId()));
				moduleList.add(module);
			}
		}
		caApmRepository.save(moduleList);
	}
	/*
	 * private void logInstanceBanner(String instanceUrl) {
	 * LOG.info("------------------------------"); LOG.info(instanceUrl);
	 * LOG.info("------------------------------"); }
	 */
}
