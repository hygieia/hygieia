/**
 * IAlertPollingService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public interface IAlertPollingService extends java.rmi.Remote {
    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot getManagementModule(java.lang.String manModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DEMConfig getEMConfig() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot getAlertSnapshot(java.lang.String manModuleName, java.lang.String agentIdentifier, java.lang.String alertDefName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[] getAlertSnapshots(java.lang.String manModuleName, java.lang.String agentIdentifier) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[] getManagedModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[] getAllIscopeManagmentModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[] getAllFilteredIscopeManagmentModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[] getAllAlertsSnapshot() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[] getAllAlertsSnapshotForManagementModule(java.lang.String managementModule) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot getAgentSnapshot(java.lang.String manModuleName, java.lang.String agentIdentifier) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[] getAgentSnapshots(java.lang.String manModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
}
