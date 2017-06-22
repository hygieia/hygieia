/**
 * AgentInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class AgentInfo  implements java.io.Serializable {
    private java.lang.String agent;

    private java.lang.String domain;

    private java.lang.String host;

    private java.lang.String ipAddress;

    private boolean okToAutoUnmount;

    private boolean okToDisconnect;

    private boolean okToUnmount;

    private java.lang.String process;

    private java.lang.String processURL;

    private boolean shutOff;

    private java.lang.String socketType;

    private java.lang.String stateOfAgent;

    private boolean supportsShutOff;

    private boolean supportsTxnTracing;

    public AgentInfo() {
    }

    public AgentInfo(
           java.lang.String agent,
           java.lang.String domain,
           java.lang.String host,
           java.lang.String ipAddress,
           boolean okToAutoUnmount,
           boolean okToDisconnect,
           boolean okToUnmount,
           java.lang.String process,
           java.lang.String processURL,
           boolean shutOff,
           java.lang.String socketType,
           java.lang.String stateOfAgent,
           boolean supportsShutOff,
           boolean supportsTxnTracing) {
           this.agent = agent;
           this.domain = domain;
           this.host = host;
           this.ipAddress = ipAddress;
           this.okToAutoUnmount = okToAutoUnmount;
           this.okToDisconnect = okToDisconnect;
           this.okToUnmount = okToUnmount;
           this.process = process;
           this.processURL = processURL;
           this.shutOff = shutOff;
           this.socketType = socketType;
           this.stateOfAgent = stateOfAgent;
           this.supportsShutOff = supportsShutOff;
           this.supportsTxnTracing = supportsTxnTracing;
    }


    /**
     * Gets the agent value for this AgentInfo.
     * 
     * @return agent
     */
    public java.lang.String getAgent() {
        return agent;
    }


    /**
     * Sets the agent value for this AgentInfo.
     * 
     * @param agent
     */
    public void setAgent(java.lang.String agent) {
        this.agent = agent;
    }


    /**
     * Gets the domain value for this AgentInfo.
     * 
     * @return domain
     */
    public java.lang.String getDomain() {
        return domain;
    }


    /**
     * Sets the domain value for this AgentInfo.
     * 
     * @param domain
     */
    public void setDomain(java.lang.String domain) {
        this.domain = domain;
    }


    /**
     * Gets the host value for this AgentInfo.
     * 
     * @return host
     */
    public java.lang.String getHost() {
        return host;
    }


    /**
     * Sets the host value for this AgentInfo.
     * 
     * @param host
     */
    public void setHost(java.lang.String host) {
        this.host = host;
    }


    /**
     * Gets the ipAddress value for this AgentInfo.
     * 
     * @return ipAddress
     */
    public java.lang.String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the ipAddress value for this AgentInfo.
     * 
     * @param ipAddress
     */
    public void setIpAddress(java.lang.String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Gets the okToAutoUnmount value for this AgentInfo.
     * 
     * @return okToAutoUnmount
     */
    public boolean isOkToAutoUnmount() {
        return okToAutoUnmount;
    }


    /**
     * Sets the okToAutoUnmount value for this AgentInfo.
     * 
     * @param okToAutoUnmount
     */
    public void setOkToAutoUnmount(boolean okToAutoUnmount) {
        this.okToAutoUnmount = okToAutoUnmount;
    }


    /**
     * Gets the okToDisconnect value for this AgentInfo.
     * 
     * @return okToDisconnect
     */
    public boolean isOkToDisconnect() {
        return okToDisconnect;
    }


    /**
     * Sets the okToDisconnect value for this AgentInfo.
     * 
     * @param okToDisconnect
     */
    public void setOkToDisconnect(boolean okToDisconnect) {
        this.okToDisconnect = okToDisconnect;
    }


    /**
     * Gets the okToUnmount value for this AgentInfo.
     * 
     * @return okToUnmount
     */
    public boolean isOkToUnmount() {
        return okToUnmount;
    }


    /**
     * Sets the okToUnmount value for this AgentInfo.
     * 
     * @param okToUnmount
     */
    public void setOkToUnmount(boolean okToUnmount) {
        this.okToUnmount = okToUnmount;
    }


    /**
     * Gets the process value for this AgentInfo.
     * 
     * @return process
     */
    public java.lang.String getProcess() {
        return process;
    }


    /**
     * Sets the process value for this AgentInfo.
     * 
     * @param process
     */
    public void setProcess(java.lang.String process) {
        this.process = process;
    }


    /**
     * Gets the processURL value for this AgentInfo.
     * 
     * @return processURL
     */
    public java.lang.String getProcessURL() {
        return processURL;
    }


    /**
     * Sets the processURL value for this AgentInfo.
     * 
     * @param processURL
     */
    public void setProcessURL(java.lang.String processURL) {
        this.processURL = processURL;
    }


    /**
     * Gets the shutOff value for this AgentInfo.
     * 
     * @return shutOff
     */
    public boolean isShutOff() {
        return shutOff;
    }


    /**
     * Sets the shutOff value for this AgentInfo.
     * 
     * @param shutOff
     */
    public void setShutOff(boolean shutOff) {
        this.shutOff = shutOff;
    }


    /**
     * Gets the socketType value for this AgentInfo.
     * 
     * @return socketType
     */
    public java.lang.String getSocketType() {
        return socketType;
    }


    /**
     * Sets the socketType value for this AgentInfo.
     * 
     * @param socketType
     */
    public void setSocketType(java.lang.String socketType) {
        this.socketType = socketType;
    }


    /**
     * Gets the stateOfAgent value for this AgentInfo.
     * 
     * @return stateOfAgent
     */
    public java.lang.String getStateOfAgent() {
        return stateOfAgent;
    }


    /**
     * Sets the stateOfAgent value for this AgentInfo.
     * 
     * @param stateOfAgent
     */
    public void setStateOfAgent(java.lang.String stateOfAgent) {
        this.stateOfAgent = stateOfAgent;
    }


    /**
     * Gets the supportsShutOff value for this AgentInfo.
     * 
     * @return supportsShutOff
     */
    public boolean isSupportsShutOff() {
        return supportsShutOff;
    }


    /**
     * Sets the supportsShutOff value for this AgentInfo.
     * 
     * @param supportsShutOff
     */
    public void setSupportsShutOff(boolean supportsShutOff) {
        this.supportsShutOff = supportsShutOff;
    }


    /**
     * Gets the supportsTxnTracing value for this AgentInfo.
     * 
     * @return supportsTxnTracing
     */
    public boolean isSupportsTxnTracing() {
        return supportsTxnTracing;
    }


    /**
     * Sets the supportsTxnTracing value for this AgentInfo.
     * 
     * @param supportsTxnTracing
     */
    public void setSupportsTxnTracing(boolean supportsTxnTracing) {
        this.supportsTxnTracing = supportsTxnTracing;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AgentInfo)) return false;
        AgentInfo other = (AgentInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.agent==null && other.getAgent()==null) || 
             (this.agent!=null &&
              this.agent.equals(other.getAgent()))) &&
            ((this.domain==null && other.getDomain()==null) || 
             (this.domain!=null &&
              this.domain.equals(other.getDomain()))) &&
            ((this.host==null && other.getHost()==null) || 
             (this.host!=null &&
              this.host.equals(other.getHost()))) &&
            ((this.ipAddress==null && other.getIpAddress()==null) || 
             (this.ipAddress!=null &&
              this.ipAddress.equals(other.getIpAddress()))) &&
            this.okToAutoUnmount == other.isOkToAutoUnmount() &&
            this.okToDisconnect == other.isOkToDisconnect() &&
            this.okToUnmount == other.isOkToUnmount() &&
            ((this.process==null && other.getProcess()==null) || 
             (this.process!=null &&
              this.process.equals(other.getProcess()))) &&
            ((this.processURL==null && other.getProcessURL()==null) || 
             (this.processURL!=null &&
              this.processURL.equals(other.getProcessURL()))) &&
            this.shutOff == other.isShutOff() &&
            ((this.socketType==null && other.getSocketType()==null) || 
             (this.socketType!=null &&
              this.socketType.equals(other.getSocketType()))) &&
            ((this.stateOfAgent==null && other.getStateOfAgent()==null) || 
             (this.stateOfAgent!=null &&
              this.stateOfAgent.equals(other.getStateOfAgent()))) &&
            this.supportsShutOff == other.isSupportsShutOff() &&
            this.supportsTxnTracing == other.isSupportsTxnTracing();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAgent() != null) {
            _hashCode += getAgent().hashCode();
        }
        if (getDomain() != null) {
            _hashCode += getDomain().hashCode();
        }
        if (getHost() != null) {
            _hashCode += getHost().hashCode();
        }
        if (getIpAddress() != null) {
            _hashCode += getIpAddress().hashCode();
        }
        _hashCode += (isOkToAutoUnmount() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOkToDisconnect() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOkToUnmount() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getProcess() != null) {
            _hashCode += getProcess().hashCode();
        }
        if (getProcessURL() != null) {
            _hashCode += getProcessURL().hashCode();
        }
        _hashCode += (isShutOff() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getSocketType() != null) {
            _hashCode += getSocketType().hashCode();
        }
        if (getStateOfAgent() != null) {
            _hashCode += getStateOfAgent().hashCode();
        }
        _hashCode += (isSupportsShutOff() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isSupportsTxnTracing() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AgentInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domain");
        elemField.setXmlName(new javax.xml.namespace.QName("", "domain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("host");
        elemField.setXmlName(new javax.xml.namespace.QName("", "host"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("okToAutoUnmount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "okToAutoUnmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("okToDisconnect");
        elemField.setXmlName(new javax.xml.namespace.QName("", "okToDisconnect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("okToUnmount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "okToUnmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("process");
        elemField.setXmlName(new javax.xml.namespace.QName("", "process"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "processURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shutOff");
        elemField.setXmlName(new javax.xml.namespace.QName("", "shutOff"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("socketType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "socketType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stateOfAgent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stateOfAgent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supportsShutOff");
        elemField.setXmlName(new javax.xml.namespace.QName("", "supportsShutOff"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supportsTxnTracing");
        elemField.setXmlName(new javax.xml.namespace.QName("", "supportsTxnTracing"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
