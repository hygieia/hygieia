package com.capitalone.dashboard.collector;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.WLMonitorCollector;
import com.capitalone.dashboard.model.WLMonitorCollectorItem;
import com.capitalone.dashboard.model.WebLogicMonitor;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.WLMonitorApplicationRepository;
import com.capitalone.dashboard.repository.WLMonitorCollectorRepository;
import com.capitalone.dashboard.repository.WLMonitorRepository;

@SuppressWarnings("PMD")
@Component
public class WLMonitorCollectorTask extends CollectorTask<WLMonitorCollector> {	
	
	private static final Log LOG = LogFactory
			.getLog(WLMonitorCollectorTask.class);	
	
	private final WLMonitorCollectorRepository wLMonitorCollectorRepository;
	private final WLMonitorApplicationRepository wLMonitorApplicationRepository;
	private final WLMonitorClient wLMonitorClient;
	private final WLMonitorSettings wLMonitorSettings;
	private final WLMonitorRepository wLMonitorRepository;
	
	@Autowired
	public WLMonitorCollectorTask(TaskScheduler taskScheduler,
			WLMonitorCollectorRepository wLMonitorCollectorRepository,
			WLMonitorApplicationRepository wLMonitorApplicationRepository,
			WLMonitorClient wLMonitorClient,
			WLMonitorSettings wLMonitorSettings,
			WLMonitorRepository wLMonitorRepository) {
			super(taskScheduler, "WLMonitor");			
			this.wLMonitorCollectorRepository = wLMonitorCollectorRepository;
			this.wLMonitorApplicationRepository = wLMonitorApplicationRepository;
			this.wLMonitorClient = wLMonitorClient;
			this.wLMonitorSettings = wLMonitorSettings;
			this.wLMonitorRepository = wLMonitorRepository;
	}

	@Override
	public WLMonitorCollector getCollector() {
		return WLMonitorCollector.prototype();
	}

	@Override
	public BaseCollectorRepository<WLMonitorCollector> getCollectorRepository() {
		return wLMonitorCollectorRepository;
	}

	@Override
	public String getCron() {
		return wLMonitorSettings.getCron();
	}

	@Override
	public void collect(WLMonitorCollector collector) {
		logBanner("Starting...");
		long start = System.currentTimeMillis();
		addNewEntryInCollectorItem (collector);		
		List<WLMonitorCollectorItem> enabledApps = enabledApplications(collector);		
		if(!enabledApps.isEmpty()){
			for(WLMonitorCollectorItem eApp : enabledApps){
				System.out.println("eApp::"+eApp);
				wLMonitorClient.getWLMonitorEnvironments(eApp.getOptions().get("serverName").toString(),eApp.getDescription());					
			}
		}		
		log("Finished", start);
	}
	
	private int addNewEntryInCollectorItem(WLMonitorCollector collector){		
		List<String> serversList = wLMonitorSettings.getServers();
		int i=0;
		if(!serversList.isEmpty()){
			for(String ser : serversList){
				 String[] out = ser.split("\\|");
				if (getCollectorItemByCollectorIdAndDesc(collector, out[0])) {
					i++;
					WLMonitorCollectorItem vmi = new WLMonitorCollectorItem();
					vmi.setCollector(collector);
					vmi.setCollectorId(collector.getId());
					vmi.setEnabled(false);
					vmi.setDescription(out[0]);
					vmi.setEnvironmentName(out[0]);
					vmi.setLastUpdated(System.currentTimeMillis());
					vmi.setPushed(false);
					vmi.setServerName(out[1]);
					wLMonitorApplicationRepository.save(vmi);
				}
				 else{
					 log("Duplicates items not allowed", 0);					 
				 }				 
			}
		}		
		return i;		
	}
			
	private boolean getCollectorItemByCollectorIdAndDesc(WLMonitorCollector collector,String desc) {
		return wLMonitorApplicationRepository.findVmonitorApplicationByCollectorIdAndDesc(
				collector.getId(),desc) == null;
	}

	
	private void updateData(List<WebLogicMonitor> vMonitors) {
		wLMonitorRepository.deleteAll();
		wLMonitorRepository.save(vMonitors);
	}
	
	private List<WLMonitorCollectorItem> getAllApps(
			WLMonitorCollector collector) {
		return wLMonitorApplicationRepository.findAllApps(collector.getId());
	}	
	
	private List<WLMonitorCollectorItem> enabledApplications(
			WLMonitorCollector collector) {
		return wLMonitorApplicationRepository.findEnabledApplications(
				collector.getId());
	}
	
	protected void log(String marker, long start) {
		log(marker, start, null);
	}

	protected void log(String text, long start, Integer count) {
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

	protected void logInstanceBanner(String instanceUrl) {
		LOG.info("------------------------------");
		LOG.info(instanceUrl);
		LOG.info("------------------------------");
	}

}
