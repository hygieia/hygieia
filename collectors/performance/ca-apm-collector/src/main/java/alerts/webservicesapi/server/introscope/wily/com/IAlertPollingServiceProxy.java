package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class IAlertPollingServiceProxy implements alerts.webservicesapi.server.introscope.wily.com.IAlertPollingService {
  private String _endpoint = null;
  private alerts.webservicesapi.server.introscope.wily.com.IAlertPollingService iAlertPollingService = null;
  
  public IAlertPollingServiceProxy() {
    _initIAlertPollingServiceProxy();
  }
  
  public IAlertPollingServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIAlertPollingServiceProxy();
  }
  
  private void _initIAlertPollingServiceProxy() {
    try {
      iAlertPollingService = (new alerts.webservicesapi.server.introscope.wily.com.AlertPollingServiceLocator()).getAlertPollingService();
      if (iAlertPollingService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iAlertPollingService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iAlertPollingService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iAlertPollingService != null)
      ((javax.xml.rpc.Stub)iAlertPollingService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.IAlertPollingService getIAlertPollingService() {
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService;
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot getManagementModule(java.lang.String manModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getManagementModule(manModuleName);
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DEMConfig getEMConfig() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getEMConfig();
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot getAlertSnapshot(java.lang.String manModuleName, java.lang.String agentIdentifier, java.lang.String alertDefName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAlertSnapshot(manModuleName, agentIdentifier, alertDefName);
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[] getAlertSnapshots(java.lang.String manModuleName, java.lang.String agentIdentifier) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAlertSnapshots(manModuleName, agentIdentifier);
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[] getManagedModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getManagedModules();
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[] getAllIscopeManagmentModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAllIscopeManagmentModules();
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[] getAllFilteredIscopeManagmentModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAllFilteredIscopeManagmentModules();
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[] getAllAlertsSnapshot() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAllAlertsSnapshot();
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[] getAllAlertsSnapshotForManagementModule(java.lang.String managementModule) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAllAlertsSnapshotForManagementModule(managementModule);
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot getAgentSnapshot(java.lang.String manModuleName, java.lang.String agentIdentifier) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAgentSnapshot(manModuleName, agentIdentifier);
  }
  
  public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[] getAgentSnapshots(java.lang.String manModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iAlertPollingService == null)
      _initIAlertPollingServiceProxy();
    return iAlertPollingService.getAgentSnapshots(manModuleName);
  }
  
  
}