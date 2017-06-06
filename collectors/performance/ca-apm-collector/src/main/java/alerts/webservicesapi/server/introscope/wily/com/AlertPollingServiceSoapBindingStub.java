/**
 * AlertPollingServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;

@SuppressWarnings("PMD")
public class AlertPollingServiceSoapBindingStub extends org.apache.axis.client.Stub implements IAlertPollingService {
    private Vector cachedSerClasses = new Vector();
    private Vector cachedSerQNames = new Vector();
    private Vector cachedSerFactories = new Vector();
    private Vector cachedDeserFactories = new Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[11];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        OperationDesc oper;
        ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getManagementModule");
        param = new ParameterDesc(new QName("", "manModuleName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot.class);
        oper.setReturnQName(new QName("", "getManagementModuleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getEMConfig");
        oper.setReturnType(new QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DEMConfig"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DEMConfig.class);
        oper.setReturnQName(new QName("", "getEMConfigReturn"));
        oper.setStyle(Style.RPC);
        oper.setUse(Use.ENCODED);
        oper.addFault(new FaultDesc(
                      new QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAlertSnapshot");
        param = new org.apache.axis.description.ParameterDesc(new QName("", "manModuleName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "agentIdentifier"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new QName("", "alertDefName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAlertDefnSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot.class);
        oper.setReturnQName(new QName("", "getAlertSnapshotReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new FaultDesc(
                      new QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAlertSnapshots");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "manModuleName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agentIdentifier"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDMgmtModuleAlertDefnSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAlertSnapshotsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getManagedModules");
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDMgmtModuleSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getManagedModulesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAllIscopeManagmentModules");
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfManagementModuleBean"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAllIscopeManagmentModulesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAllFilteredIscopeManagmentModules");
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfManagementModuleBean"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAllFilteredIscopeManagmentModulesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAllAlertsSnapshot");
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDAllAlertsSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAllAlertsSnapshotReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAllAlertsSnapshotForManagementModule");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "managementModule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDAllAlertsSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAllAlertsSnapshotForManagementModuleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAgentSnapshot");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "manModuleName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agentIdentifier"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAgentSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAgentSnapshotReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAgentSnapshots");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "manModuleName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDMgmtModuleAgentSnapshot"));
        oper.setReturnClass(alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAgentSnapshotsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[10] = oper;

    }

    public AlertPollingServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public AlertPollingServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public AlertPollingServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException");
            cachedSerQNames.add(qName);
            cls = com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDAllAlertsSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DAllAlertsSnapshot");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDMgmtModuleAgentSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAgentSnapshot");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDMgmtModuleAlertDefnSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAlertDefnSnapshot");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfDMgmtModuleSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleSnapshot");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ArrayOfManagementModuleBean");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ManagementModuleBean");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DAllAlertsSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DEMConfig");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DEMConfig.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAgentSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAlertDefnSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleSnapshot");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ManagementModuleBean");
            cachedSerQNames.add(qName);
            cls = alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot getManagementModule(java.lang.String manModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getManagementModule"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {manModuleName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DEMConfig getEMConfig() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getEMConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DEMConfig) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DEMConfig) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DEMConfig.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot getAlertSnapshot(java.lang.String manModuleName, java.lang.String agentIdentifier, java.lang.String alertDefName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAlertSnapshot"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {manModuleName, agentIdentifier, alertDefName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[] getAlertSnapshots(java.lang.String manModuleName, java.lang.String agentIdentifier) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAlertSnapshots"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {manModuleName, agentIdentifier});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAlertDefnSnapshot[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[] getManagedModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getManagedModules"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleSnapshot[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[] getAllIscopeManagmentModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAllIscopeManagmentModules"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[] getAllFilteredIscopeManagmentModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAllFilteredIscopeManagmentModules"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.ManagementModuleBean[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[] getAllAlertsSnapshot() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAllAlertsSnapshot"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[] getAllAlertsSnapshotForManagementModule(java.lang.String managementModule) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAllAlertsSnapshotForManagementModule"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {managementModule});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot getAgentSnapshot(java.lang.String manModuleName, java.lang.String agentIdentifier) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAgentSnapshot"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {manModuleName, agentIdentifier});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[] getAgentSnapshots(java.lang.String manModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://alerts.webservicesimpl.server.introscope.wily.com", "getAgentSnapshots"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {manModuleName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[]) org.apache.axis.utils.JavaUtils.convert(_resp, alerts.webservicesapi.server.introscope.wily.com.DMgmtModuleAgentSnapshot[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) {
              throw (com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
