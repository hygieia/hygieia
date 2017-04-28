package com.capitalone.dashboard.collector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.XLDeployApplication;
import com.capitalone.dashboard.model.XLDeployApplicationHistoryItem;
import com.capitalone.dashboard.model.XLDeployCollector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.capitalone.dashboard.repository.XLDeployApplicationRepository;
import com.capitalone.dashboard.repository.XLDeployCollectorRepository;
import com.capitalone.dashboard.repository.XLDeployEnvironmentComponentRepository;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@Component
public class XLDeployCollectorTask extends CollectorTask<XLDeployCollector>{
	private final DateFormat FULL_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XLDeployCollectorTask.class);
	
	public static final String STATUS_DONE = "DONE";
	public static final String STATUS_CANCELLED = "CANCELLED";
	
	public static final String DEPLOYMENT_INITIAL = "Initial";
	public static final String DEPLOYMENT_UPDATE = "Update";
	public static final String DEPLOYMENT_UNDEPLOY = "Undeployment";
	public static final String DEPLOYMENT_ROLLBACK = "Rollback";
	
	private final XLDeployCollectorRepository xlDeployCollectorRepository;
	private final XLDeployApplicationRepository xlDeployApplicationRepository;
	private final XLDeployClient xlDeployClient;
	private final XLDeploySettings xlDeploySettings;
	
    private final XLDeployEnvironmentComponentRepository envComponentRepository;
    @SuppressWarnings({"unused", "PMD.SingularField"}) // might need in future
	private final EnvironmentStatusRepository environmentStatusRepository;

    private final ComponentRepository dbComponentRepository;
	
    @Autowired
	public XLDeployCollectorTask(TaskScheduler taskScheduler,
									XLDeployCollectorRepository xlDeployCollectorRepository,
									XLDeployApplicationRepository xlDeployApplicationRepository,
									XLDeployEnvironmentComponentRepository envComponentRepository,
						            EnvironmentStatusRepository environmentStatusRepository,
						            XLDeploySettings xlDeploySettings, XLDeployClient xlDeployClient,
						            ComponentRepository dbComponentRepository) {
		super(taskScheduler, "XLDeploy");
		this.xlDeployCollectorRepository = xlDeployCollectorRepository;
		this.xlDeployApplicationRepository = xlDeployApplicationRepository;
		this.xlDeployClient = xlDeployClient;
		this.xlDeploySettings = xlDeploySettings;
        this.envComponentRepository = envComponentRepository;
        this.environmentStatusRepository = environmentStatusRepository;
        this.dbComponentRepository = dbComponentRepository;
	}
	
    @Override
    public XLDeployCollector getCollector() {
        return XLDeployCollector.prototype(xlDeploySettings.getServers(), xlDeploySettings.getNiceNames());
    }
    
    @Override
    public BaseCollectorRepository<XLDeployCollector> getCollectorRepository() {
        return xlDeployCollectorRepository;
    }

    @Override
    public String getCron() {
        return xlDeploySettings.getCron();
    }
    
    @Override
    public void collect(XLDeployCollector collector) {
    	for (String instanceUrl : collector.getXLdeployServers()) {
    		
    		logBanner(instanceUrl); 
    		
    		long start = System.currentTimeMillis();
    		
    		clean(collector);
    		
    		addNewApplications(xlDeployClient.getApplications(instanceUrl), collector);
    		
    		updateData(enabledApplications(collector, instanceUrl));
    		
    		log("Finished", start);
    	}
    }
    
    /**
     * Clean up unused deployment collector items
     *
     * @param collector the {@link XLDeployCollector}
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    private void clean(XLDeployCollector collector) {
    	deleteUnwantedJobs(collector);
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {
            if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty()) continue;
            List<CollectorItem> itemList = comp.getCollectorItems().get(
                    CollectorType.Deployment);
            if (itemList == null) continue;
            for (CollectorItem ci : itemList) {
                if (ci == null) continue;
                uniqueIDs.add(ci.getId());
            }
        }
        List<XLDeployApplication> appList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet< >();
        udId.add(collector.getId());
        for (XLDeployApplication app : xlDeployApplicationRepository.findByCollectorIdIn(udId)) {
            if (app != null) {
                app.setEnabled(uniqueIDs.contains(app.getId()));
                appList.add(app);
            }
        }
        xlDeployApplicationRepository.save(appList);
    }
    
    private void deleteUnwantedJobs(XLDeployCollector collector) {
        List<XLDeployApplication> deleteAppList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        for (XLDeployApplication app : xlDeployApplicationRepository.findByCollectorIdIn(udId)) {
            if (!collector.getXLdeployServers().contains(app.getInstanceUrl()) ||
                    (!app.getCollectorId().equals(collector.getId()))) {
                deleteAppList.add(app);
            }
        }

        xlDeployApplicationRepository.delete(deleteAppList);
    }
    
    /**
     * For each {@link XLDeployApplication}, update the current
     * {@link EnvironmentComponent}s and {@link EnvironmentStatus}.
     *
     * @param xlDeployApplications list of {@link XLDeployApplication}s that belong to the same XLD instance
     */
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    private void updateData(List<XLDeployApplication> xlDeployApplications) {
    	long startUpdate = System.currentTimeMillis();
    	
    	List<XLDeployApplicationHistoryItem> allHistory = getRelevantHistory(xlDeployApplications);
    	
    	Table<XLDeployApplication, String, List<XLDeployApplicationHistoryItem>> grouped = groupAndSort(allHistory, xlDeployApplications);
    	
    	if (LOGGER.isDebugEnabled()) {
    		dumpHistoryTable(grouped);
    	}
    	
    	for (Map.Entry<XLDeployApplication, Map<String, List<XLDeployApplicationHistoryItem>>> e : grouped.rowMap().entrySet()) {
    		XLDeployApplication application = e.getKey();
    		
    		for (Map.Entry<String, List<XLDeployApplicationHistoryItem>> c : e.getValue().entrySet()) {
    			String environmentId = c.getKey();
    			List<XLDeployApplicationHistoryItem> history = c.getValue();
	    		
    	    	// List in case we want to add in component information (deployables) in future
    			List<EnvironmentComponent> compList = new ArrayList<EnvironmentComponent>();
    			
    			XLDeployApplicationHistoryItem currentStatus = null;
    			
    			for (XLDeployApplicationHistoryItem h : history) {
    				String status = h.getStatus(); // One of DONE, CANCELLED
    	 			String type = h.getType(); // One of Initial, Update, Undeployment, Rollback, Noop (?)
    	 			
    	 			if ("Rollback".equalsIgnoreCase(type)) {
    	 				// For simplicity we won't consider indeterminate cases (like if a rollback fails or is cancelled)
    	 				continue;
    	 			}
    	 			
    	 			if ("CANCELLED".equalsIgnoreCase(status)) {
    	 				continue;
    	 			}
    	 			
    	 			currentStatus = h;
    	 			break;
    			}
    			
    			if (currentStatus != null) {
    				compList.add(getEnvironmentComponent(currentStatus, application));
    			} else {
    				LOGGER.error("Could not find history status for " + application.getApplicationName() + " on environment " + environmentId);
    			}
    			
    			if (!compList.isEmpty()) {
    	            List<EnvironmentComponent> existingComponents = envComponentRepository
    	                    .findByCollectorItemIdAndEnvironmentID(application.getId(), environmentId);
    	            envComponentRepository.delete(existingComponents);
    	            envComponentRepository.save(compList);
    	        }
    			
    	        // Note I don't think XLD can get environment status information
	    	}
    	}
    	
    	// Explicitly iterate through list in case it didn't have any history show up above
    	for (XLDeployApplication application : xlDeployApplications) {
    		application.setLastUpdated(startUpdate);
    	}
    	
    	// We set the last update time so need to save it
		xlDeployApplicationRepository.save(xlDeployApplications);
    	
    	log("Deploy Update", startUpdate);
    }
    
    private List<XLDeployApplicationHistoryItem> getRelevantHistory(List<XLDeployApplication> xlDeployApplications) {
    	long startHistory = System.currentTimeMillis();
    	
    	/*
    	 * Gather history in two phases for efficiency:
    	 * 1. For enabled applications that have updated since the beginning of yesterday only grab history
    	 *    since the beginning of yesterday. If no deployments have been made on an environment then the
    	 *    existing data in mongo will remain.
    	 * 2. For enabled applications that have not been updated since yesterday (includes new ones)
    	 *    gather 6 months worth of history
    	 */
    	List<XLDeployApplicationHistoryItem> allHistory = new ArrayList<XLDeployApplicationHistoryItem>();
    	List<XLDeployApplication> needShortHistory = new ArrayList<XLDeployApplication>();
    	List<XLDeployApplication> needLongHistory = new ArrayList<XLDeployApplication>();
    	
    	long yesterdayBOD = yesterdayBOD().getTime();
    	
    	for (XLDeployApplication app : xlDeployApplications) {
    		long lastUpdate = app.getLastUpdated();
    		
    		if (lastUpdate < yesterdayBOD) {
    			needLongHistory.add(app);
    		} else {
    			needShortHistory.add(app);
    		}
    	}
    	
    	if (!needShortHistory.isEmpty()) {
    		long startHist = System.currentTimeMillis();
    		List<XLDeployApplicationHistoryItem> history = xlDeployClient.getApplicationHistory(xlDeployApplications, yesterdayBOD(), tomorrowEOD());

    		log("Deploy Hist Short", startHist, history.size());
    		
    		allHistory.addAll(history);
    	}
    	
    	if (!needLongHistory.isEmpty()) {
    		long startHist = System.currentTimeMillis();
    		List<XLDeployApplicationHistoryItem> history = xlDeployClient.getApplicationHistory(xlDeployApplications, threeMonthsAgo(), tomorrowEOD());

    		log("Deploy Hist Long", startHist, history.size());
    		
    		allHistory.addAll(history);
    	}
    	
    	log("Deploy Hist All", startHistory, allHistory.size());
    	
    	return allHistory;
    }
    
    private List<XLDeployApplication> enabledApplications(
    		XLDeployCollector collector, String instanceUrl) {
        return xlDeployApplicationRepository.findEnabledApplications(
                collector.getId(), instanceUrl);
    }
    
    /**
     * Add any new {@link XLDeployApplication}s.
     *
     * @param applications list of {@link XLDeployApplication}s
     * @param collector    the {@link XLDeployCollector}
     */
    private void addNewApplications(List<XLDeployApplication> applications,
                                    XLDeployCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        log("All apps", start, applications.size());
        for (XLDeployApplication application : applications) {
        	XLDeployApplication existing = findExistingApplication(collector, application);

        	String niceName = getNiceName(application, collector);
            if (existing == null) {
                application.setCollectorId(collector.getId());
                application.setEnabled(false);
                application.setDescription(application.getApplicationName());
                if (StringUtils.isNotEmpty(niceName)) {
                	application.setNiceName(niceName);
                }
                try {
                    xlDeployApplicationRepository.save(application);
                } catch (org.springframework.dao.DuplicateKeyException ce) {
                    log("Duplicates items not allowed", 0);

                }
                count++;
            } else if (StringUtils.isEmpty(existing.getNiceName()) && StringUtils.isNotEmpty(niceName)) {
				existing.setNiceName(niceName);
                xlDeployApplicationRepository.save(existing);
            }
        }
        
        log("New apps", start, count);
    }

    private XLDeployApplication findExistingApplication(XLDeployCollector collector,
                                     XLDeployApplication application) {
        return xlDeployApplicationRepository.findXLDeployApplication(
                collector.getId(), application.getInstanceUrl(),
                application.getApplicationId());
    }
    
    private String getNiceName(XLDeployApplication application, XLDeployCollector collector) {
        if (CollectionUtils.isEmpty(collector.getXLdeployServers())) return "";
        List<String> servers = collector.getXLdeployServers();
        List<String> niceNames = collector.getNiceNames();
        if (CollectionUtils.isEmpty(niceNames)) return "";
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).equalsIgnoreCase(application.getInstanceUrl()) && niceNames.size() > i) {
                return niceNames.get(i);
            }
        }
        return "";
    }
    
    private EnvironmentComponent getEnvironmentComponent(XLDeployApplicationHistoryItem data, XLDeployApplication application) {
		/**
		 * I believe deployables correspond to the concept of "Component" here
		 * 
		 * In XLD deployables all go together when deploying a package. Versioning information
		 * is on the package and not the deployable. Since all deployeds pass or fail together
		 * and have the same version information we don't gain much by explicitly calling them out.
		 * For that reason I am just creating a component that corresponds to the application.
		 */
		EnvironmentComponent component = new EnvironmentComponent();
		
		component.setCollectorItemId(application.getId());
		
		String packageName = data.getDeploymentPackage();
		String applicationName = packageName != null? packageName.split("/")[0] : null;
		String applicationVersion = packageName != null? packageName.split("/")[1] : null;
		
		component.setEnvironmentID(data.getEnvironmentId());
		component.setEnvironmentName(data.getEnvironmentName());
		component.setEnvironmentUrl(application.getInstanceUrl());
		
		// See note above about "components"
		component.setComponentID(application.getApplicationId());
		component.setComponentName(applicationName);
		component.setComponentVersion(applicationVersion);
		component.setDeployTime(data.getCompletionDate());
		component.setAsOfDate(data.getCompletionDate());
		
		component.setDeployed(STATUS_DONE.equalsIgnoreCase(data.getStatus()) 
				&& (DEPLOYMENT_INITIAL.equalsIgnoreCase(data.getType()) || DEPLOYMENT_UPDATE.equalsIgnoreCase(data.getType())));
		return component;
    }
    
    /**
     * 
     * @param history		a list of history that come from the same XLD instance
     * @param applications	a list of applications that come from the same XLD instance
     * @return
     */
    private Table<XLDeployApplication, String, List<XLDeployApplicationHistoryItem>> groupAndSort(List<XLDeployApplicationHistoryItem> history, List<XLDeployApplication> applications) {
    	Table<XLDeployApplication, String, List<XLDeployApplicationHistoryItem>> rt = HashBasedTable.create();
    	
    	// Setup deref map... I'm pretty sure application names are unique
    	Map<String, XLDeployApplication> nameToApplication = new HashMap<String, XLDeployApplication>();
    	
    	for (XLDeployApplication app : applications) {
    		nameToApplication.put(app.getApplicationName(), app);
    	}
    	
    	// First group
    	for (XLDeployApplicationHistoryItem data : history) {
    		String packageName = data.getDeploymentPackage();
    		String applicationName = packageName != null? packageName.split("/")[0] : null;
    		//String applicationVersion = packageName != null? packageName.split("/")[1] : null;
    		
    		XLDeployApplication app = nameToApplication.get(applicationName);
    		
    		if (app != null) {
    			String environmentId = data.getEnvironmentId();
    			
    			if (rt.get(app, environmentId) == null) {
    				rt.put(app, environmentId, new ArrayList<XLDeployApplicationHistoryItem>());
    			}
    			
    			rt.get(app, environmentId).add(data);
    		} else {
    			LOGGER.error("Could not find application " + applicationName);
    		}
    	}
    	
    	// Now sort - note changes from values() are reflected in table
    	for (List<XLDeployApplicationHistoryItem> hist : rt.values()) {
    		Collections.sort(hist, new Comparator<XLDeployApplicationHistoryItem>() {
				@Override
				public int compare(XLDeployApplicationHistoryItem o1, XLDeployApplicationHistoryItem o2) {
					// Long compareTo for safety (int overflow)
					// want most recent first
					return Long.valueOf(o2.getCompletionDate()).compareTo(Long.valueOf(o1.getCompletionDate()));
				}
    		});
    	}
    	
    	return rt;
    }
    
    private Date threeMonthsAgo() {
    	Calendar time = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    	time.add(Calendar.MONTH, -3);
    	
    	return time.getTime();
    }
    
    private Date tomorrowEOD() {
    	Calendar time = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    	time.add(Calendar.DAY_OF_MONTH, 1);
    	time.set(Calendar.HOUR_OF_DAY, 23);
    	time.set(Calendar.MINUTE, 59);
    	time.set(Calendar.SECOND, 59);
    	
    	return time.getTime();
    }
    
    private Date yesterdayBOD() {
    	Calendar time = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    	time.add(Calendar.DAY_OF_MONTH, -1);
    	time.set(Calendar.HOUR_OF_DAY, 0);
    	time.set(Calendar.MINUTE, 0);
    	time.set(Calendar.SECOND, 0);
    	
    	return time.getTime();
    }
    
    private void dumpHistoryTable(Table<XLDeployApplication, String, List<XLDeployApplicationHistoryItem>> table) {
    	StringBuilder sb = new StringBuilder(8192);
    	
    	sb.append("\n === Application Deployment History Table === \n");
    	
    	for (Map.Entry<XLDeployApplication, Map<String, List<XLDeployApplicationHistoryItem>>> e : table.rowMap().entrySet()) {
    		XLDeployApplication application = e.getKey();
    		
    		sb.append("   \\ -- " + application.getApplicationId() + "\n");
    		
    		for (Map.Entry<String, List<XLDeployApplicationHistoryItem>> c : e.getValue().entrySet()) {
    			String environmentId = c.getKey();
        		List<XLDeployApplicationHistoryItem> applicationHistory = c.getValue();
	    		
	    		sb.append("      \\ -- " + environmentId + "\n");
	    		for (XLDeployApplicationHistoryItem h : applicationHistory) {
		    		String packageName = h.getDeploymentPackage();
		    		//String applicationName = packageName != null? packageName.split("/")[0] : null;
		    		String applicationVersion = packageName != null? packageName.split("/")[1] : null;
	    			
	    			String compl = FULL_DATE.format(new Date(h.getCompletionDate()));
	    			
	    			sb.append(String.format("         + [%26s] %12s %9s %s\n", compl, h.getType(), h.getStatus(), applicationVersion));
	    		}
    		}
    	}
    	
    	LOGGER.debug(sb.toString());
    }
}
