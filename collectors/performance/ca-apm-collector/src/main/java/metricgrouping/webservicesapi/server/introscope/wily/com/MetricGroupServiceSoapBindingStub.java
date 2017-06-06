/**
 * MetricGroupServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class MetricGroupServiceSoapBindingStub extends org.apache.axis.client.Stub implements metricgrouping.webservicesapi.server.introscope.wily.com.IMetricGroupService {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[3];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("listAgentsMatchingSpecInMetricGrouping");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "metricGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ArrayOfAgentInfo"));
        oper.setReturnClass(metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "listAgentsMatchingSpecInMetricGroupingReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getListOfManagementModules");
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ArrayOfManagementModuleInfo"));
        oper.setReturnClass(metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getListOfManagementModulesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMetricGroupingByNameforMM");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "metricGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "mgmtModuleName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricGrouping"));
        oper.setReturnClass(metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getMetricGroupingByNameforMMReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "fault"),
                      "com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException",
                      new javax.xml.namespace.QName("http://webservicesapi.server.introscope.wily.com", "IntroscopeWebServicesException"), 
                      true
                     ));
        _operations[2] = oper;

    }

    public MetricGroupServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public MetricGroupServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public MetricGroupServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentExpression");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentInfo");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ArrayOfAgentExpression");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentExpression");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ArrayOfAgentInfo");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ArrayOfManagementModuleInfo");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ManagementModuleInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ArrayOfMetricExpression");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.MetricExpression[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricExpression");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ManagementModuleInfo");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricExpression");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.MetricExpression.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricGrouping");
            cachedSerQNames.add(qName);
            cls = metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping.class;
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

    public metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[] listAgentsMatchingSpecInMetricGrouping(java.lang.String metricGroupName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://metricgrouping.webservicesimpl.server.introscope.wily.com", "listAgentsMatchingSpecInMetricGrouping"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {metricGroupName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[]) org.apache.axis.utils.JavaUtils.convert(_resp, metricgrouping.webservicesapi.server.introscope.wily.com.AgentInfo[].class);
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

    public metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[] getListOfManagementModules() throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://metricgrouping.webservicesimpl.server.introscope.wily.com", "getListOfManagementModules"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[]) org.apache.axis.utils.JavaUtils.convert(_resp, metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo[].class);
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

    public metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping getMetricGroupingByNameforMM(java.lang.String metricGroupName, java.lang.String mgmtModuleName) throws java.rmi.RemoteException, com.wily.introscope.server.webservicesapi.IntroscopeWebServicesException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://metricgrouping.webservicesimpl.server.introscope.wily.com", "getMetricGroupingByNameforMM"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {metricGroupName, mgmtModuleName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping) _resp;
            } catch (java.lang.Exception _exception) {
                return (metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping) org.apache.axis.utils.JavaUtils.convert(_resp, metricgrouping.webservicesapi.server.introscope.wily.com.MetricGrouping.class);
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
