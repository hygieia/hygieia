/**
 * AlertPollingServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

import javax.xml.namespace.QName;

import org.apache.axis.client.Service;

import com.capitalone.dashboard.collector.CaApmSettings;

@SuppressWarnings("PMD")
public class AlertPollingServiceLocator extends Service implements AlertPollingService {
	
	private CaApmSettings caApmSettings;

    public AlertPollingServiceLocator() {
    }


    public AlertPollingServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AlertPollingServiceLocator(String wsdlLoc, QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AlertPollingService
    private java.lang.String AlertPollingService_address = caApmSettings.getAlertAddress();

    public java.lang.String getAlertPollingServiceAddress() {
        return AlertPollingService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AlertPollingServiceWSDDServiceName = "AlertPollingService";

    public java.lang.String getAlertPollingServiceWSDDServiceName() {
        return AlertPollingServiceWSDDServiceName;
    }

    public void setAlertPollingServiceWSDDServiceName(java.lang.String name) {
        AlertPollingServiceWSDDServiceName = name;
    }

    public alerts.webservicesapi.server.introscope.wily.com.IAlertPollingService getAlertPollingService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AlertPollingService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAlertPollingService(endpoint);
    }

    public alerts.webservicesapi.server.introscope.wily.com.IAlertPollingService getAlertPollingService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            alerts.webservicesapi.server.introscope.wily.com.AlertPollingServiceSoapBindingStub _stub = new alerts.webservicesapi.server.introscope.wily.com.AlertPollingServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getAlertPollingServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAlertPollingServiceEndpointAddress(java.lang.String address) {
        AlertPollingService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (alerts.webservicesapi.server.introscope.wily.com.IAlertPollingService.class.isAssignableFrom(serviceEndpointInterface)) {
                alerts.webservicesapi.server.introscope.wily.com.AlertPollingServiceSoapBindingStub _stub = new alerts.webservicesapi.server.introscope.wily.com.AlertPollingServiceSoapBindingStub(new java.net.URL(AlertPollingService_address), this);
                _stub.setPortName(getAlertPollingServiceWSDDServiceName());
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
        if ("AlertPollingService".equals(inputPortName)) {
            return getAlertPollingService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "AlertPollingService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "AlertPollingService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AlertPollingService".equals(portName)) {
            setAlertPollingServiceEndpointAddress(address);
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
