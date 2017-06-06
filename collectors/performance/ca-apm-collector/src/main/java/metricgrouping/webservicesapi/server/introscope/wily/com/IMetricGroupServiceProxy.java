package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class IMetricGroupServiceProxy implements metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService {
  private String _endpoint = null;
  private metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService iMetricGroupService = null;
  
  public IMetricGroupServiceProxy() {
    _initIMetricGroupServiceProxy();
  }
  
  public IMetricGroupServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIMetricGroupServiceProxy();
  }
  
  private void _initIMetricGroupServiceProxy() {
    try {
      iMetricGroupService = (new metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupServiceLocator()).getMetricGroupService();
      if (iMetricGroupService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iMetricGroupService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iMetricGroupService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iMetricGroupService != null)
      ((javax.xml.rpc.Stub)iMetricGroupService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService getIMetricGroupService() {
    if (iMetricGroupService == null)
      _initIMetricGroupServiceProxy();
    return iMetricGroupService;
  }
  
  public metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[] listAgentsMatchingSpecInMetricGrouping(java.lang.String metricGroupName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iMetricGroupService == null)
      _initIMetricGroupServiceProxy();
    return iMetricGroupService.listAgentsMatchingSpecInMetricGrouping(metricGroupName);
  }
  
  public metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[] getListOfManagementModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iMetricGroupService == null)
      _initIMetricGroupServiceProxy();
    return iMetricGroupService.getListOfManagementModules();
  }
  
  public metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping getMetricGroupingByNameforMM(java.lang.String metricGroupName, java.lang.String mgmtModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException{
    if (iMetricGroupService == null)
      _initIMetricGroupServiceProxy();
    return iMetricGroupService.getMetricGroupingByNameforMM(metricGroupName, mgmtModuleName);
  }
  
  
}