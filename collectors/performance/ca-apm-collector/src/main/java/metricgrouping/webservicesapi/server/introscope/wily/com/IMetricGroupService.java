/**
 * IMetricGroupService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public interface IMetricGroupService extends java.rmi.Remote {
    public metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[] listAgentsMatchingSpecInMetricGrouping(java.lang.String metricGroupName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[] getListOfManagementModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
    public metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping getMetricGroupingByNameforMM(java.lang.String metricGroupName, java.lang.String mgmtModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException;
}
