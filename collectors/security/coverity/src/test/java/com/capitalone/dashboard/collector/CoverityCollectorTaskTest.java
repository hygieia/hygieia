package com.capitalone.dashboard.collector;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import com.capitalone.dashboard.collector.coverity.soap.CoveritySoapClient;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.model.CoverityCollector;
import com.capitalone.dashboard.model.CoverityProject;
import com.capitalone.dashboard.model.CoverityScan;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.capitalone.dashboard.repository.CoverityCollectorRepository;
import com.capitalone.dashboard.repository.CoverityProjectRepository;
import com.capitalone.dashboard.repository.CoverityScanRepository;

import coverity.ws.configuration.CovRemoteServiceException_Exception;
import coverity.ws.configuration.ProjectDataObj;
import coverity.ws.configuration.ProjectIdDataObj;
import coverity.ws.configuration.StreamDataObj;
import coverity.ws.configuration.StreamIdDataObj;
import coverity.ws.defect.AttributeDefinitionIdDataObj;
import coverity.ws.defect.AttributeValueIdDataObj;
import coverity.ws.defect.DefectStateAttributeValueDataObj;
import coverity.ws.defect.MergedDefectDataObj;
import coverity.ws.defect.MergedDefectsPageDataObj;

@RunWith(MockitoJUnitRunner.class)
public class CoverityCollectorTaskTest {
    @Mock private TaskScheduler taskScheduler;
    @Mock private CoverityCollectorRepository covCollectorRepo;
    @Mock private CoverityProjectRepository covProjectRepo;
    @Mock private CoverityScanRepository covScanRepo;
    @Mock private CoveritySettings covSettings;
    @Mock private ComponentRepository componentRepo;
    @Mock private ConfigurationRepository configurationRepo;
    @Mock private CoveritySoapClient covClient;
    @Mock private CodeQualityRepository codeQualityRepo;

    @InjectMocks private CoverityCollectorTask task;

    private static final String
        TEST_URL = "http://mockServer.com:8080/ssc",
        TEST_USR = "test",
        TEST_PWD = "testpwd",
        TEST_PROJ_NAME = "QUUX",
        TEST_STREAM = "styx";

    private static final long TEST_PROJ_KEY = 1776l;

    @Test
    public void collect_noBuildServers_nothingAdded() {

        when(configurationRepo.findByCollectorName(any()))
        	.thenReturn(new Configuration());

        task.collect(new CoverityCollector());

        verifyZeroInteractions(covScanRepo);
        verifyZeroInteractions(codeQualityRepo);
    }

    @Test
    public void collector_register() {

        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());

        CoverityCollector collector = task.getCollector();

        assertThat(collector.getCoverityServers().size(), is(1));
        assertThat(collector.getCoverityServers().get(0), is(TEST_URL));
        verifyZeroInteractions(codeQualityRepo);
    }

    @Test
    public void noDuplicateCoverityScanReports() throws Exception {

    	when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());
        // this mocking is just to prevent null exceptions
        when(componentRepo.findAll()).thenReturn(components());
        when(covProjectRepo.findByCollectorIdIn(any()))
            .thenReturn(fromExistingWsdlProject());
        when(covProjectRepo.findEnabledProjects(any(ObjectId.class), anyString()))
            .thenReturn(fromExistingWsdlProject());
        when(covClient.getAllProjects(TEST_URL))
            .thenReturn(wsdlProject());
        // duplication is simulated by returning a "matching" persisted instance
        when(codeQualityRepo.findByCollectorItemIdAndTimestamp(any(ObjectId.class), anyLong()))
            .thenReturn(new CodeQuality());
        when(covClient.getSecurityDefectsForStream(anyString(), any(), anyInt()))
            .thenReturn(getStreamDefects(TEST_STREAM));

        task.collect(coverityCollector(configuration()));

        verify(codeQualityRepo, never()).save(any(CodeQuality.class));
        verify(covScanRepo, never()).save(any(CoverityScan.class));
    }

    @Test
    public void scanReportTest() throws Exception {

        when(componentRepo.findAll()).thenReturn(components());
        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());
        when(covClient.getAllProjects(anyString())).thenReturn(wsdlProject());
        when(covClient.getSecurityDefectsForStream(anyString(), any(), anyInt()))
            .thenReturn(getStreamDefects(TEST_STREAM));
        when(covProjectRepo.findEnabledProjects(any(ObjectId.class), anyString()))
            .thenReturn(fromExistingWsdlProject());
        // simulating new coverity scan data
        when(covScanRepo.findByCollectorItemIdAndTimestamp(any(ObjectId.class), anyLong()))
            .thenReturn(null);
        when(codeQualityRepo.findByCollectorItemIdAndTimestamp(any(ObjectId.class), anyLong()))
            .thenReturn(null);

        task.collect(coverityCollector(configuration()));

        verify(codeQualityRepo, times(1)).save(anyCollection());
        verify(covScanRepo, times(1)).save(anyCollection());
    }

    @Test
    public void noScanReportTest() throws Exception {

        when(componentRepo.findAll()).thenReturn(components());
        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration3());
        when(covClient.getAllProjects(anyString()))
            .thenReturn(wsdlProject())
            .thenThrow(new CovRemoteServiceException_Exception("msg", null));
        // simulate that something goes wrong...
        when(covClient.getSecurityDefectsForStream(anyString(), any(), anyInt()))
            .thenThrow(new coverity.ws.defect.CovRemoteServiceException_Exception("msg", null));
        when(covProjectRepo.findEnabledProjects(any(ObjectId.class), anyString()))
            .thenReturn(fromExistingWsdlProject());
        when(codeQualityRepo.findByCollectorItemIdAndTimestamp(any(ObjectId.class), anyLong()))
            .thenReturn(new CodeQuality());

        task.collect(coverityCollector(configuration3()));

        verify(codeQualityRepo, never()).save(any(CodeQuality.class));
        verify(covScanRepo, never()).save(any(CoverityScan.class));
    }

    @Test public void detectExistingCodeQuality_thenDoNotSave() throws Exception {

        when(componentRepo.findAll()).thenReturn(components());
        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());

        when(covProjectRepo.findByCollectorIdIn(any()))
            .thenReturn(fromExistingWsdlProject());

        when(covClient.getAllProjects(anyString()))
            .thenReturn(wsdlProject());

        when(covProjectRepo.findEnabledProjects(any(ObjectId.class), anyString()))
            .thenReturn(fromExistingWsdlProject());

        when(covScanRepo.findByCollectorItemIdAndTimestamp(any(ObjectId.class), anyLong()))
            .thenReturn(null);

        MergedDefectsPageDataObj defects = getStreamDefects(TEST_STREAM);
        defects.setTotalNumberOfRecords(0);
        defects.getMergedDefects().clear();

        when(covClient.getSecurityDefectsForStream(anyString(), any(), anyInt()))
            .thenReturn(defects);

        when(codeQualityRepo.findByCollectorItemIdOrderByTimestampDesc(any()))
            .thenReturn(Arrays.asList(new CodeQuality(), new CodeQuality()));

        task.collect(coverityCollector(configuration()));

        verify(codeQualityRepo, never()).save(any(CodeQuality.class));
        verify(covScanRepo, never()).save(any(CoverityScan.class));
    }

    @Test
    public void removeUnwantedJobsTest() throws Exception {

        when(componentRepo.findAll()).thenReturn(components());
        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());
        when(covClient.getAllProjects(anyString()))
            .thenReturn(Collections.emptyList()); // no projects on server
        when(covProjectRepo.findByCollectorIdIn(Matchers.anyListOf(ObjectId.class)))
            .thenReturn(fromExistingWsdlProject()); // dead project
        when(covProjectRepo.findEnabledProjects(any(ObjectId.class), anyString()))
            .thenReturn(new ArrayList<>());

        task.collect(coverityCollector(configuration()));

        verify(covProjectRepo, atLeastOnce()).delete(Matchers.anyListOf(CoverityProject.class));
    }

    @Test
    public void addNewProjectsTest() throws Exception {

    	CoverityProject cp = new CoverityProject();
    	cp.setInstanceUrl(TEST_URL);

        when(covClient.getAllProjects(anyString()))
            .thenReturn(wsdlProject());
        when(covProjectRepo.findByCollectorIdIn(Matchers.anySetOf(ObjectId.class)))
            .thenReturn(Arrays.asList(cp));
        when(componentRepo.findAll()).thenReturn(components());
        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());

        task.collect(coverityCollector(configuration()));

        verify(covProjectRepo, atLeastOnce()).save(Matchers.anyListOf(CoverityProject.class));
    }

    @Test
    public void removeDeadProjects() throws Exception {

        CoverityProject zombie = new CoverityProject();
        CoverityProject garbage = new CoverityProject();

        zombie.setEnabled(true);
        zombie.setInstanceUrl("dummy");

        garbage.setEnabled(false);
        garbage.setInstanceUrl("dummy2");

        when(covClient.getAllProjects(TEST_URL))
            .thenReturn(wsdlProject());
        when(covProjectRepo.findByCollectorIdIn(Matchers.anySetOf(ObjectId.class)))
            .thenReturn(Arrays.asList(
                        zombie, garbage
                    ));

        when(componentRepo.findAll()).thenReturn(components());
        when(configurationRepo.findByCollectorName(any())).thenReturn(configuration());

        task.collect(coverityCollector(configuration()));

        verify(covProjectRepo, atLeastOnce()).delete(Matchers.anyListOf(CoverityProject.class));
    }

    private MergedDefectsPageDataObj getStreamDefects(String stream) throws DatatypeConfigurationException {

        MergedDefectsPageDataObj d = new MergedDefectsPageDataObj();

        d.setTotalNumberOfRecords(1);
        d.getMergedDefects().add(new MergedDefectDataObj());
        d.getMergedDefects().get(0).setLastDetectedStream(stream);
        d.getMergedDefects().get(0).setCid(333L);

        d.getMergedDefects().get(0).getDefectStateAttributeValues()
            .add(new DefectStateAttributeValueDataObj());

        AttributeDefinitionIdDataObj
            sevAttrName = new AttributeDefinitionIdDataObj(),
            sevScoreAttrName = new AttributeDefinitionIdDataObj();
        sevAttrName.setName("CVSS_Severity");
        sevScoreAttrName.setName("CVSS_Score");
        AttributeValueIdDataObj
            sevAttrValue = new AttributeValueIdDataObj(),
            sevScoreValue = new AttributeValueIdDataObj();
        sevAttrValue.setName("Critical");
        sevScoreValue.setName("9.8");

        d.getMergedDefects().get(0).getDefectStateAttributeValues().get(0)
            .setAttributeDefinitionId(sevAttrName);
        d.getMergedDefects().get(0).getDefectStateAttributeValues().get(0)
            .setAttributeValueId(sevAttrValue);

        d.getMergedDefects().get(0).setDisplayType("null variable");
        d.getMergedDefects().get(0).setFilePathname("anyProgramWrittenIn.java");

        GregorianCalendar gc  = new GregorianCalendar(10, 10, 10);
        XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

        d.getMergedDefects().get(0).setLastDetected(xgc);

        return d;
    }

    private List<CoverityProject> fromExistingWsdlProject() throws Exception {

        List<CoverityProject> cps = new ArrayList<>();

        for (ProjectDataObj soapProjObj : wsdlProject()) {
            CoverityProject covProj = new CoverityProject();

            covProj.setCollectorId(new ObjectId());
            covProj.setInstanceUrl(TEST_URL);

            String stream = soapProjObj.getStreams().get(0).getId().getName();

            covProj.setDescription(soapProjObj.getId().getName(), stream);
            covProj.setProjectKey(soapProjObj.getProjectKey());
            covProj.setDateCreated(soapProjObj.getDateCreated().toGregorianCalendar().getTimeInMillis() / 1000);
            covProj.setDateModified(soapProjObj.getDateModified().toGregorianCalendar().getTimeInMillis() / 1000);

            cps.add(covProj);
        }

        return cps;
    }

    private List<ProjectDataObj> wsdlProject() throws Exception {

        ProjectDataObj wsdlProj = new ProjectDataObj();

        wsdlProj.setId(new ProjectIdDataObj());
        wsdlProj.getId().setName(TEST_PROJ_NAME);

        wsdlProj.setProjectKey(TEST_PROJ_KEY);

        GregorianCalendar gc  = new GregorianCalendar(10, 10, 10);
        XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        wsdlProj.setDateCreated(xgc);
        wsdlProj.setDateModified(xgc);

        wsdlProj.getStreams().add(new StreamDataObj());
        wsdlProj.getStreams().get(0).setId(new StreamIdDataObj());
        wsdlProj.getStreams().get(0).getId().setName(TEST_STREAM);

        return Arrays.asList(wsdlProj);
    }

    /**
     * Helper method to create configuration object with a details for a fake Coverity server
     * @return Configuration with Coverity server
     */
    private Configuration configuration() {

        Configuration config = new Configuration();
        config.setCollectorName(CoverityCollector.NICE_NAME);
        
        Map<String,String> coverityServer = new HashMap<>();
        coverityServer.put("url", TEST_URL);
        coverityServer.put("userName", TEST_USR);
        coverityServer.put("password", TEST_PWD);

        config.getInfo().add(coverityServer);

        return config;
    }

    private Configuration configuration3() {

        Configuration config = new Configuration();
        config.setCollectorName(CoverityCollector.NICE_NAME);
        
        Map<String,String> coverityServer = new HashMap<>();

        coverityServer.put("url", TEST_URL);
        coverityServer.put("userName", TEST_USR);
        coverityServer.put("password", TEST_PWD);

        config.getInfo().add(coverityServer);
        
        // config #2
        coverityServer = new HashMap<>();

        coverityServer.put("url", TEST_URL);
        coverityServer.put("userName", TEST_USR);
        coverityServer.put("password", TEST_PWD);

        config.getInfo().add(coverityServer);

        // config #3
        coverityServer = new HashMap<>();

        coverityServer.put("url", "example.malformed");
        coverityServer.put("userName", TEST_USR);
        coverityServer.put("password", TEST_PWD);

        config.getInfo().add(coverityServer);
        
        return config;
    }

    private List<com.capitalone.dashboard.model.Component> components() {

        com.capitalone.dashboard.model.Component c = new Component();
        c.setId(new ObjectId());
        c.setName("COMPONENT1");
        c.setOwner("JOHN");

        CollectorItem ci = new CollectorItem();
        ci.setId(new ObjectId());
        ci.setCollectorId(new ObjectId());

        c.getCollectorItems().put(
                CollectorType.StaticSecurityScan,
                Arrays.asList(ci));

        return Arrays.asList(c);
    }

    private CoverityCollector coverityCollector(Configuration config) {

        List<String> serverUrls = config.getInfo().stream()
                .map(server -> server.get("url")).collect(toList());

        CoverityCollector coll = CoverityCollector.prototype(serverUrls);
        coll.setId(new ObjectId(new java.util.Date()));

        return coll;
    }
}
