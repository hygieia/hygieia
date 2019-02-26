package com.capitalone.dashboard.collector.coverity.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.springframework.stereotype.Component;

import coverity.ws.configuration.ConfigurationService;
import coverity.ws.configuration.ConfigurationServiceService;
import coverity.ws.configuration.CovRemoteServiceException_Exception;
import coverity.ws.configuration.ProjectDataObj;
import coverity.ws.configuration.ProjectFilterSpecDataObj;
import coverity.ws.defect.DefectService;
import coverity.ws.defect.DefectServiceService;
import coverity.ws.defect.MergedDefectFilterSpecDataObj;
import coverity.ws.defect.MergedDefectsPageDataObj;
import coverity.ws.defect.PageSpecDataObj;
import coverity.ws.defect.SnapshotScopeSpecDataObj;
import coverity.ws.defect.StreamIdDataObj;

/**
 * CoveritySoapClient is effectively a wrapper around the services defined in the WSDL.
 * The Coverity Connect server queried should have a compatible WSDL definition/version
 * to the one used to generate sources with wsimport.
 */
@Component
public class CoveritySoapClient {

    private ConfigurationService configService;
    private DefectService defectService;

    // Maps contain keys: url, userName, and password
    private Set<Map<String, String>> coverityServers;

    /**
     * Get a list of all Coverity projects (each project includes its own streams)
     * @return List of all projects
     * @throws CovRemoteServiceException_Exception
     * @throws MalformedURLException
     */
    public List<ProjectDataObj> getAllProjects(String instanceUrl)
            throws CovRemoteServiceException_Exception, MalformedURLException {

        String usr = getUsername(instanceUrl);
        String pwd = getPassword(instanceUrl);

        ProjectFilterSpecDataObj filter = new ProjectFilterSpecDataObj();
        filter.setIncludeChildren(true); // so that dates are included in response
        filter.setIncludeStreams(true);  // defects collected by stream for each project

        setCSCreds(instanceUrl, usr, pwd);

        return getProjectsStubbable(filter);
    }

    /**
     * Wrapper method for configService call. Makes mocking in tests possible. Package-Private intentionally.
     * @throws MalformedURLException
     */
    List<ProjectDataObj> getProjectsStubbable(ProjectFilterSpecDataObj filter)
            throws CovRemoteServiceException_Exception {

        return configService.getProjects(filter);
    }

    /**
     * Get defects for Current Snapshot 1000 at a time. Use pageIx to go through pages.
     * @param instanceUrl address to the Coverity server (must include protocol; i.e. 'http://')
     * @param stream Stream in project to be queried
     * @param pageIx Zero-based index of page to return
     * @return Paged defects
     * @throws coverity.ws.defect.CovRemoteServiceException_Exception
     * @throws MalformedURLException
     */
    public MergedDefectsPageDataObj getSecurityDefectsForStream(String instanceUrl, String stream, int pageIx)
            throws coverity.ws.defect.CovRemoteServiceException_Exception, MalformedURLException {

        String usr = getUsername(instanceUrl);
        String pwd = getPassword(instanceUrl);

        StreamIdDataObj sid = new StreamIdDataObj();
        sid.setName(stream);

        List<StreamIdDataObj> streamIds = new ArrayList<>();
        streamIds.add(sid);

        // filter-object based on the settings of the default view in coverity connect
        // "Outstanding Security Issues" (except no grouping)
        MergedDefectFilterSpecDataObj filter = new MergedDefectFilterSpecDataObj();
        filter.getClassificationNameList().addAll(Arrays.asList("Unclassified", "Pending", "Untested"));
        // Impact:   select all (no filtering)
        filter.getIssueKindList().add("SECURITY");
        // Severity: select all (no filtering)
        // Type:     select all (no filtering)

        PageSpecDataObj pageSpec = new PageSpecDataObj();
        pageSpec.setPageSize(1000); // this is the max number of records per page
        pageSpec.setStartIndex(1000*pageIx);

        SnapshotScopeSpecDataObj snapshotScope = new SnapshotScopeSpecDataObj();
        // default values explicitly given here
        snapshotScope.setCompareOutdatedStreams(false);
        snapshotScope.setShowOutdatedStreams(false);
        snapshotScope.setShowSelector("last()");

        setDSCreds(instanceUrl, usr, pwd);

        return getMergedDefectsForStreamsStubbable(streamIds, filter, pageSpec, snapshotScope);
    }

    /**
     * Wrapper method for defectService call. Makes mocking in tests possible. Package-Private intentionally.
     * @throws MalformedURLException
     */
    MergedDefectsPageDataObj getMergedDefectsForStreamsStubbable(List<StreamIdDataObj> streamIds,
            MergedDefectFilterSpecDataObj filterSpec, PageSpecDataObj pageSpec, SnapshotScopeSpecDataObj snapshotScope)
            throws coverity.ws.defect.CovRemoteServiceException_Exception {

        return defectService.getMergedDefectsForStreams(streamIds, filterSpec, pageSpec, snapshotScope);
    }

    private void setCSCreds(String instanceUrl, String user, String pword) throws MalformedURLException {

        ConfigurationServiceService css = stubbableNewCofigSrvcSrvc(instanceUrl);
        configService = css.getConfigurationServicePort();

        BindingProvider bp = (BindingProvider) configService;
        bp.getBinding().setHandlerChain(
                Arrays.asList(new ClientAuthHeaderHandlerWSS(user, pword)));
    }

    ConfigurationServiceService stubbableNewCofigSrvcSrvc(String instanceUrl) throws MalformedURLException {
        return new ConfigurationServiceService(
                new URL(instanceUrl + "/ws/v9/configurationservice?wsdl"),
                new QName("http://ws.coverity.com/v9", "ConfigurationServiceService"));
    }

    private void setDSCreds(String instanceUrl, String user, String pword) throws MalformedURLException {

        DefectServiceService dss = stubbableNewDefectSrvcSrvc(instanceUrl);
        defectService = dss.getDefectServicePort();

        BindingProvider bp = (BindingProvider) defectService;
        bp.getBinding().setHandlerChain(
                Arrays.asList(new ClientAuthHeaderHandlerWSS(user, pword)));
    }

    DefectServiceService stubbableNewDefectSrvcSrvc(String instanceUrl) throws MalformedURLException {
        return new DefectServiceService(
                new URL(instanceUrl + "/ws/v9/defectservice?wsdl"),
                new QName("http://ws.coverity.com/v9", "DefectServiceService"));
    }

    /**
     * Set Coverity Connect server details: url, username, password.
     * Other collectors unfortunately call this method connectDB.
     * @param coverityServers
     */
    public void setServerDetails(Set<Map<String,String>> coverityServers) {
        this.coverityServers = coverityServers;
    }

    private String getUsername(String url) {

        for (Map<String, String> coverityServer : coverityServers) {
            if (url.contains(coverityServer.get("url"))) {
                return coverityServer.get("userName");
            }
        }
        return null;
    }

    private String getPassword(String url) {

        for (Map<String, String> coverityServer : coverityServers) {
            if (url.contains(coverityServer.get("url"))) {
                return coverityServer.get("password");
            }
        }
        return null;
    }
}
