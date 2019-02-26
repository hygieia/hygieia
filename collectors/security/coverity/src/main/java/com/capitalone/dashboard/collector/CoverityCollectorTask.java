package com.capitalone.dashboard.collector;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.collector.coverity.soap.CoveritySoapClient;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.model.CoverityCollector;
import com.capitalone.dashboard.model.CoverityProject;
import com.capitalone.dashboard.model.CoverityScan;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.capitalone.dashboard.repository.CoverityCollectorRepository;
import com.capitalone.dashboard.repository.CoverityProjectRepository;
import com.capitalone.dashboard.repository.CoverityScanRepository;

import coverity.ws.configuration.CovRemoteServiceException_Exception;
import coverity.ws.configuration.ProjectDataObj;
import coverity.ws.configuration.StreamDataObj;
import coverity.ws.defect.DefectStateAttributeValueDataObj;
import coverity.ws.defect.MergedDefectDataObj;
import coverity.ws.defect.MergedDefectsPageDataObj;

@Component
public class CoverityCollectorTask extends CollectorTask<CoverityCollector> {

    private static final Log LOG = LogFactory.getLog(CoverityCollectorTask.class);

    private final CoverityCollectorRepository covCollectorRepo;
    private final CoverityProjectRepository covProjectRepo;
    private final CoverityScanRepository covScanRepo;
    private final CodeQualityRepository codeQualityRepo;
    private final ComponentRepository componentRepo;
    private final ConfigurationRepository configurationRepo;
    private final CoveritySoapClient coverityClient;
    private final CoveritySettings covSettings;

    private static final String LOW_SEV = "Low";
    private static final String MED_SEV = "Medium";
    private static final String HIGH_SEV= "High";
    private static final String CRIT_SEV= "Critical";
    
    @Autowired
    public CoverityCollectorTask(TaskScheduler taskScheduler,
            CoverityCollectorRepository covCollectorRepo, CoverityProjectRepository covProjectRepo,
            CoverityScanRepository covScanRepo, CodeQualityRepository codeQualityRepo,
            ComponentRepository componentRepo, ConfigurationRepository configurationRepo,
            CoveritySoapClient coverityClient, CoveritySettings covSettings) {

        super(taskScheduler, CoverityCollector.NICE_NAME);

        this.covCollectorRepo = covCollectorRepo;
        this.covProjectRepo = covProjectRepo;
        this.covScanRepo = covScanRepo;
        this.codeQualityRepo = codeQualityRepo;
        this.componentRepo = componentRepo;
        this.configurationRepo = configurationRepo;
        this.coverityClient = coverityClient;
        this.covSettings = covSettings;
    }

    @Override
    public CoverityCollector getCollector() {

        List<String> serverUrls = new ArrayList<>();

        Configuration config = configurationRepo.findByCollectorName(CoverityCollector.NICE_NAME);

        if (config != null) {
        	for (Map<String, String> coverityServer : config.getInfo()) {
                serverUrls.add(coverityServer.get("url"));
            }	
        }
    
        return CoverityCollector.prototype(serverUrls);
    }

    @Override
    public BaseCollectorRepository<CoverityCollector> getCollectorRepository() {
        return covCollectorRepo;
    }

    @Override
    public String getCron() {
        return covSettings.getCron();
    }

    @Override
    public void collect(CoverityCollector collector) {

        long start = System.currentTimeMillis();

        Configuration config = configurationRepo.findByCollectorName(CoverityCollector.NICE_NAME);
        
        if (config == null) {
        	LOG.info("No Coverity Servers Configured");
            return;
        }
        
        config.decryptOrEncrptInfo();
        coverityClient.setServerDetails(config.getInfo());

        // Retrieve projects previously persisted to the database
        List<CoverityProject> persistedProjs =
                covProjectRepo.findByCollectorIdIn(singleton(collector.getId()));

        // aggregate projects collected from all Coverity Servers
        // used to handleDeadProjects
        List<CoverityProject> allCoverityProjs = new LinkedList<>();

        for (String instanceUrl : collector.getCoverityServers()) {

            logBanner(instanceUrl);

            List<CoverityProject> persistedInstanceProjs = persistedProjs.stream()
                    .filter(p -> p.getInstanceUrl().equals(instanceUrl)).collect(toList());

            refreshEnabledWidgetStateforProjects(collector, persistedInstanceProjs);

            try {
                List<CoverityProject> covProjs = coverityClient.getAllProjects(instanceUrl).stream() // throws
                        .map(this::fromProjectDataObj) // convert each to List<CoverityProject> type
                        .flatMap(List::stream)
                        .collect(toList());

                for (CoverityProject p : covProjs) {
                    p.setCollectorId(collector.getId());
                    p.setInstanceUrl(instanceUrl);
                }

                allCoverityProjs.addAll(covProjs);

                log("Fetched projects: " + covProjs.size(), start);

                refreshProjects(start, persistedInstanceProjs, covProjs);

                // collect defect data for enabled projects
                // enabled means there is a widget on a dashboard configured to view the project
                List<CoverityProject> enabledProjs =
                        covProjectRepo.findEnabledProjects(collector.getId(), instanceUrl);

                log("collecting defects for projects: "+enabledProjs.size());

                List<CoverityScan> scans = new ArrayList<>();
                List<CodeQuality> qualities = new ArrayList<>();

                addDetailsFromProjs(enabledProjs, scans, qualities, instanceUrl, start);

                covScanRepo.save(scans);
                codeQualityRepo.save(qualities);

            } catch (CovRemoteServiceException_Exception e) {
                LOG.error("Project SOAP request error:  "+instanceUrl, e);
            } catch (MalformedURLException e) {
                LOG.error("Malformed Url (may be missing protocol):" + instanceUrl, e);
            }
        }

        handleDeadProjects(persistedProjs, allCoverityProjs, collector);
    }

    private void addDetailsFromProjs(List<CoverityProject> ps, List<CoverityScan> ss, List<CodeQuality> qs,
            String instanceUrl, long start) {

        for (CoverityProject proj : ps) {
            try {
                String streamName = proj.getStream();

                List<MergedDefectDataObj> mergedDefects = collectDefectsForStream(instanceUrl, streamName); // throws

                CoverityScan scan = makeCovScanObj(proj, streamName, mergedDefects, start);
                CodeQuality  quality = makeCodeQualityObj(proj, scan);

                if (isNewCoverityScanData(proj, scan) && isNewCodeQualityData(proj, quality)) {
                    log("Collecting new defect data for "+proj.getDescription());
                    ss.add(scan);
                    qs.add(quality);
                } else {
                    log("No new data for project "+proj.getDescription());
                }
            } catch (coverity.ws.defect.CovRemoteServiceException_Exception e) {
                LOG.error("Defect SOAP request error: "+instanceUrl, e);
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    private boolean isNewCoverityScanData(CoverityProject proj, CoverityScan covScan) {
        return covScanRepo.findByCollectorItemIdAndTimestamp(proj.getId(), covScan.getTimestamp()) == null;
    }

    private boolean isNewCodeQualityData(CoverityProject proj, CodeQuality codeQ) {

        int defectsFound = codeQ.getMetrics().stream()
            .map(CodeQualityMetric::getFormattedValue)
            .mapToInt(Integer::parseInt).sum();

        // if no defects were found in latest fetched scan
        // check the latest saved scan to see if fetched scan is different
        if (defectsFound == 0) {

            List<CodeQuality> persistedCodeQs = codeQualityRepo.findByCollectorItemIdOrderByTimestampDesc(proj.getId());
            if (persistedCodeQs.isEmpty()) {
                return true; // new scan
            }

            int defectsInLastScan = persistedCodeQs.get(0).getMetrics().stream()
                .map(CodeQualityMetric::getFormattedValue)
                .mapToInt(Integer::parseInt).sum();

            return defectsInLastScan != defectsFound; // if different then this is a new scan
        }

        return codeQualityRepo.findByCollectorItemIdAndTimestamp(proj.getId(), codeQ.getTimestamp()) == null;
    }

    // Build a CodeQuality object for the given scan (serves as a summary of the scan)
    private CodeQuality makeCodeQualityObj(CoverityProject proj, CoverityScan cs) {

        Set<CodeQualityMetric> metrics = new HashSet<>();

        for (String severity : getSeverities()) {
            CodeQualityMetric metric = new CodeQualityMetric(severity);

            metric.setFormattedValue(
                    Integer.toString(cs.getDefectsBySeverity().get(severity).size()));

            if (severity.equals(HIGH_SEV) || severity.equals(CRIT_SEV)) {
                metric.setStatus(CodeQualityMetricStatus.Alert);
            } else {
                metric.setStatus(CodeQualityMetricStatus.Warning);
            }

            metrics.add(metric);
        }

        CodeQuality cq = new CodeQuality();
        cq.setCollectorItemId(proj.getId());
        cq.setTimestamp(cs.getTimestamp());
        cq.setName(proj.getDescription());
        cq.setUrl(proj.getInstanceUrl());
        cq.setType(CodeQualityType.SecurityAnalysis);

        cq.getMetrics().addAll(metrics);

        return cq;
    }

    // this method helps guarantee that the severities will be fetched in a fresh state
    private String[] getSeverities() {
        return new String[] {LOW_SEV, MED_SEV, HIGH_SEV, CRIT_SEV};
    }

    // Build a CoverityScan object from the given defects.
    private CoverityScan makeCovScanObj(CoverityProject proj, String stream,
            List<MergedDefectDataObj> mergedDefects, long start) {

        Stream<CoverityScan.Defect> defects = mergedDefects.stream()
                .map(this::fromMergedDefect);

        Map<String, List<CoverityScan.Defect>> defectsBySeverity = defects
                .collect(groupingBy(CoverityScan.Defect::getCvssSeverity));

        CoverityScan cs = new CoverityScan();
        cs.setName(proj.getDescription());
        cs.setProjectKey(proj.getProjectKey());
        cs.setStream(stream);
        cs.setCovConnectUrl(proj.getInstanceUrl());
        cs.setCollectorItemId(proj.getId());

        cs.getDefectsBySeverity().put(LOW_SEV, emptyIfNull(defectsBySeverity.get(LOW_SEV)));
        cs.getDefectsBySeverity().put(MED_SEV, emptyIfNull(defectsBySeverity.get(MED_SEV)));
        cs.getDefectsBySeverity().put(HIGH_SEV, emptyIfNull(defectsBySeverity.get(HIGH_SEV)));
        cs.getDefectsBySeverity().put(CRIT_SEV, emptyIfNull(defectsBySeverity.get(CRIT_SEV)));

        if (mergedDefects.isEmpty()) {
            cs.setTimestamp(start);
        } else {
            cs.setTimestamp(mergedDefects.get(0).getLastDetected().toGregorianCalendar().getTimeInMillis());
        }

        return cs;
    }

    private <T> List<T> emptyIfNull(List<T> nullableList) {
        return nullableList == null
                ? new ArrayList<>()
                : nullableList;
    }

    /**
     * Query the given Coverity Connect Server URL for latest snapshot defects in given stream.
     * @param instanceUrl Coverity Connect Server
     * @param stream Project Stream name
     * @return List of defects (generated WSDL type)
     * @throws coverity.ws.defect.CovRemoteServiceException_Exception
     * @throws MalformedURLException
     */
    private List<MergedDefectDataObj> collectDefectsForStream(String instanceUrl, String stream)
            throws coverity.ws.defect.CovRemoteServiceException_Exception, MalformedURLException {

        // gather all defects
        List<MergedDefectDataObj> defects = new ArrayList<>();

        MergedDefectsPageDataObj defectsPage;
        int pageIx = 0;

        defectsPage = coverityClient.getSecurityDefectsForStream(instanceUrl, stream, pageIx);
        defects.addAll(defectsPage.getMergedDefects());

        int lastPageIx = defectsPage.getTotalNumberOfRecords() / 1000; // 1000 records per page

        for (pageIx = 1; pageIx <= lastPageIx; pageIx++) {
            defectsPage = coverityClient.getSecurityDefectsForStream(instanceUrl, stream, pageIx);
            defects.addAll(defectsPage.getMergedDefects());
        }

        return defects;
    }

    /**
     * Save new projects and update previously persisted projects.
     * @param start
     * @param persistedInstanceProjs
     * @param covConnectProjs
     */
    private void refreshProjects(long start, List<CoverityProject> persistedInstanceProjs,
            List<CoverityProject> covConnectProjs) {

        List<CoverityProject> newProjs = new ArrayList<>();
        List<CoverityProject> staleExistingProjs = new ArrayList<>();

        for (CoverityProject covProj : covConnectProjs) {
            int ix = persistedInstanceProjs.indexOf(covProj);

            if (ix == -1) {
                // new project
                newProjs.add(covProj);
            } else if (covProj.getDateModified() > persistedInstanceProjs.get(ix).getDateModified()) {
                // existing project is stale

                // set ObjectId for update on save
                covProj.setId(persistedInstanceProjs.get(ix).getId());
                staleExistingProjs.add(covProj);
            }
        }

        // add new projects
        ArrayList<CoverityProject> saved =
                (ArrayList<CoverityProject>) covProjectRepo.save(newProjs);

        // save returns null when testing because repo is mocked
        if (saved != null)
            log("New projects saved: " + saved.size(), start);

        // update existing projects
        saved = (ArrayList<CoverityProject>) covProjectRepo.save(staleExistingProjs);

        if (saved != null)
            log("Projects refreshed: " + saved.size(), start);
    }

    /**
     * Dead Projects are projects in database but no longer exists in coverity.
     * Other collectors call this method deleteUnwantedJobs.
     * @param persistedProjs
     * @param allFetchedCovProjs
     * @param collector
     */
    private void handleDeadProjects(List<CoverityProject> persistedProjs, List<CoverityProject> allFetchedCovProjs,
            CoverityCollector collector) {

        List<CoverityProject> deadProjs = persistedProjs.stream()
                .filter(p -> ! p.isPushed()) // maybe only related to jenkins collector (keep for consistency)
                .filter(p -> ! collector.getId().equals(p.getCollectorId())
                        ||   ! collector.getCoverityServers().contains(p.getInstanceUrl())
                        ||   ! allFetchedCovProjs.contains(p))
                .collect(toList());

        List<CoverityProject> zombies = new ArrayList<>();
        List<CoverityProject> garbage = new ArrayList<>();

        for (CoverityProject deadProj : deadProjs) {
            if (deadProj.isEnabled()) {

                LOG.info("setting error for "+deadProj.getDescription());

                // error can be used to inform user
                // widget is configured to zombie project
                CollectionError error = new CollectionError(
                		HttpStatus.NOT_FOUND.toString(),
                        "Coverity project not found");

                deadProj.getErrors().clear();
                deadProj.getErrors().add(error);

                zombies.add(deadProj);
            } else {
                List<CoverityScan> covScans = covScanRepo.findByCollectorItemId(deadProj.getId());
                covScanRepo.delete(covScans);

                List<CodeQuality> cqs = codeQualityRepo.findByCollectorItemIdOrderByTimestampDesc(deadProj.getId());
                codeQualityRepo.delete(cqs);

                garbage.add(deadProj);
            }
        }

        covProjectRepo.save(zombies);
        covProjectRepo.delete(garbage);
    }

    /**
     * Called clean in other collectors. Update whether a widget is using this project.
     * @param collector
     * @param persistedInstanceProjs
     */
    private void refreshEnabledWidgetStateforProjects(CoverityCollector collector,
            List<CoverityProject> persistedInstanceProjs) {

        // extract unique collector item IDs from components
        // (in this context collector_items are coverity projects)
        Set<ObjectId> uniqueIDs = StreamSupport.stream(componentRepo.findAll().spliterator(), false)
                .filter(comp -> comp.getCollectorItems() != null)
                .filter(comp -> ! comp.getCollectorItems().isEmpty())
                .map(comp -> comp.getCollectorItems(CollectorType.StaticSecurityScan))
                // keep nonNull List<CollectorItem>
                .filter(Objects::nonNull)
                // merge all lists (flatten) into a stream
                .flatMap(List::stream)
                // keep nonNull CollectorItems
                .filter(Objects::nonNull)
                .filter(collectorItem -> collectorItem.getCollectorId().equals(collector.getId()))
                .map(CollectorItem::getId).collect(toSet());

        List<CoverityProject> stateChangeProjectList = new ArrayList<>();

        for (CoverityProject proj : persistedInstanceProjs) {
            if ((proj.isEnabled() && !uniqueIDs.contains(proj.getId()))
                    || (!proj.isEnabled() && uniqueIDs.contains(proj.getId()))) {

                proj.setEnabled(uniqueIDs.contains(proj.getId()));
                stateChangeProjectList.add(proj);
            }
        }

        if (!stateChangeProjectList.isEmpty()) {
            covProjectRepo.save(stateChangeProjectList);
        }
    }

    /**
     * Utility method to convert from ProjDataObj to CoverityProjects
     * Each stream in the ProjDataObj will be its own CoverityProject
     * @param soapProjObj received from Soap API
     * @return application model CoverityProjects
     */
    private List<CoverityProject> fromProjectDataObj(ProjectDataObj soapProjObj) {

        List<CoverityProject> covApps = new ArrayList<>();

        for (StreamDataObj stream : soapProjObj.getStreams()) {

            CoverityProject covApp = new CoverityProject();

            // description is used in the api layer to lookup projects
            covApp.setDescription(soapProjObj.getId().getName(), stream.getId().getName());
            covApp.setProjectKey(soapProjObj.getProjectKey());
            covApp.setDateCreated(soapProjObj.getDateCreated().toGregorianCalendar().getTimeInMillis() / 1000);
            covApp.setDateModified(soapProjObj.getDateModified().toGregorianCalendar().getTimeInMillis() / 1000);

            covApps.add(covApp);
        }

        return covApps;
    }

    /**
     * Utility method to convert from MergedDefectDataObj to CoverityScan.Defect
     * @param d SOAP defect
     * @return application model CoverityScan.Defect
     */
    private CoverityScan.Defect fromMergedDefect(MergedDefectDataObj d) {

        String cvssSeverity = "None";
        String cvssScore = "0";

        // coverity admin has added these attributes to all "projects"
        for (DefectStateAttributeValueDataObj attr : d.getDefectStateAttributeValues()) {
            if ("CVSS_Severity".equals(attr.getAttributeDefinitionId().getName())) {
                cvssSeverity = attr.getAttributeValueId().getName();
            }
            if ("CVSS_Score".equals(attr.getAttributeDefinitionId().getName())) {
                cvssScore = attr.getAttributeValueId().getName();
            }
        }

        return new CoverityScan.Defect(
                d.getCid(), d.getDisplayType(), d.getFilePathname(),
                cvssSeverity, cvssScore);
    }
}
