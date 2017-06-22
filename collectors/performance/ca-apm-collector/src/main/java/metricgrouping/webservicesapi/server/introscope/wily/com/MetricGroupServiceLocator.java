/**
 * MetricGroupServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

import com.capitalone.dashboard.collector.CaApmSettings;

@SuppressWarnings("PMD")
public class MetricGroupServiceLocator extends org.apache.axis.client.Service implements metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupService {

	private CaApmSettings caApmSettings;
	
    public MetricGroupServiceLocator() {
    }


    public MetricGroupServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MetricGroupServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MetricGroupService
    private java.lang.String MetricGroupService_address = caApmSettings.getModelAddress();

    public java.lang.String getMetricGroupServiceAddress() {
        return MetricGroupService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MetricGroupServiceWSDDServiceName = "MetricGroupService";

    public java.lang.String getMetricGroupServiceWSDDServiceName() {
        return MetricGroupServiceWSDDServiceName;
    }

    public void setMetricGroupServiceWSDDServiceName(java.lang.String name) {
        MetricGroupServiceWSDDServiceName = name;
    }

    public metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService getMetricGroupService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MetricGroupService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMetricGroupService(endpoint);
    }

    public metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService getMetricGroupService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupServiceSoapBindingStub _stub = new metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getMetricGroupServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMetricGroupServiceEndpointAddress(java.lang.String address) {
        MetricGroupService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService.class.isAssignableFrom(serviceEndpointInterface)) {
                metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupServiceSoapBindingStub _stub = new metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupServiceSoapBindingStub(new java.net.URL(MetricGroupService_address), this);
                _stub.setPortName(getMetricGroupServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MetricGroupService".equals(inputPortName)) {
            return getMetricGroupService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricGroupService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricGroupService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MetricGroupService".equals(portName)) {
            setMetricGroupServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
