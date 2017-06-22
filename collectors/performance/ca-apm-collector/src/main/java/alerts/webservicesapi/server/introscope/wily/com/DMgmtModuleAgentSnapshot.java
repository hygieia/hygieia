/**
 * DMgmtModuleAgentSnapshot.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class DMgmtModuleAgentSnapshot  implements java.io.Serializable {
    private int agentCurrStatus;

    private java.lang.String agentDashboardURL;

    private java.lang.String agentIdentifier;

    private java.lang.String agentName;

    private int agentPrevStatus;

    private java.lang.String hostName;

    private java.lang.String manModuleName;

    private java.lang.String processName;

    private long timeOfStatusChange;

    public DMgmtModuleAgentSnapshot() {
    }

    public DMgmtModuleAgentSnapshot(
           int agentCurrStatus,
           java.lang.String agentDashboardURL,
           java.lang.String agentIdentifier,
           java.lang.String agentName,
           int agentPrevStatus,
           java.lang.String hostName,
           java.lang.String manModuleName,
           java.lang.String processName,
           long timeOfStatusChange) {
           this.agentCurrStatus = agentCurrStatus;
           this.agentDashboardURL = agentDashboardURL;
           this.agentIdentifier = agentIdentifier;
           this.agentName = agentName;
           this.agentPrevStatus = agentPrevStatus;
           this.hostName = hostName;
           this.manModuleName = manModuleName;
           this.processName = processName;
           this.timeOfStatusChange = timeOfStatusChange;
    }


    /**
     * Gets the agentCurrStatus value for this DMgmtModuleAgentSnapshot.
     * 
     * @return agentCurrStatus
     */
    public int getAgentCurrStatus() {
        return agentCurrStatus;
    }


    /**
     * Sets the agentCurrStatus value for this DMgmtModuleAgentSnapshot.
     * 
     * @param agentCurrStatus
     */
    public void setAgentCurrStatus(int agentCurrStatus) {
        this.agentCurrStatus = agentCurrStatus;
    }


    /**
     * Gets the agentDashboardURL value for this DMgmtModuleAgentSnapshot.
     * 
     * @return agentDashboardURL
     */
    public java.lang.String getAgentDashboardURL() {
        return agentDashboardURL;
    }


    /**
     * Sets the agentDashboardURL value for this DMgmtModuleAgentSnapshot.
     * 
     * @param agentDashboardURL
     */
    public void setAgentDashboardURL(java.lang.String agentDashboardURL) {
        this.agentDashboardURL = agentDashboardURL;
    }


    /**
     * Gets the agentIdentifier value for this DMgmtModuleAgentSnapshot.
     * 
     * @return agentIdentifier
     */
    public java.lang.String getAgentIdentifier() {
        return agentIdentifier;
    }


    /**
     * Sets the agentIdentifier value for this DMgmtModuleAgentSnapshot.
     * 
     * @param agentIdentifier
     */
    public void setAgentIdentifier(java.lang.String agentIdentifier) {
        this.agentIdentifier = agentIdentifier;
    }


    /**
     * Gets the agentName value for this DMgmtModuleAgentSnapshot.
     * 
     * @return agentName
     */
    public java.lang.String getAgentName() {
        return agentName;
    }


    /**
     * Sets the agentName value for this DMgmtModuleAgentSnapshot.
     * 
     * @param agentName
     */
    public void setAgentName(java.lang.String agentName) {
        this.agentName = agentName;
    }


    /**
     * Gets the agentPrevStatus value for this DMgmtModuleAgentSnapshot.
     * 
     * @return agentPrevStatus
     */
    public int getAgentPrevStatus() {
        return agentPrevStatus;
    }


    /**
     * Sets the agentPrevStatus value for this DMgmtModuleAgentSnapshot.
     * 
     * @param agentPrevStatus
     */
    public void setAgentPrevStatus(int agentPrevStatus) {
        this.agentPrevStatus = agentPrevStatus;
    }


    /**
     * Gets the hostName value for this DMgmtModuleAgentSnapshot.
     * 
     * @return hostName
     */
    public java.lang.String getHostName() {
        return hostName;
    }


    /**
     * Sets the hostName value for this DMgmtModuleAgentSnapshot.
     * 
     * @param hostName
     */
    public void setHostName(java.lang.String hostName) {
        this.hostName = hostName;
    }


    /**
     * Gets the manModuleName value for this DMgmtModuleAgentSnapshot.
     * 
     * @return manModuleName
     */
    public java.lang.String getManModuleName() {
        return manModuleName;
    }


    /**
     * Sets the manModuleName value for this DMgmtModuleAgentSnapshot.
     * 
     * @param manModuleName
     */
    public void setManModuleName(java.lang.String manModuleName) {
        this.manModuleName = manModuleName;
    }


    /**
     * Gets the processName value for this DMgmtModuleAgentSnapshot.
     * 
     * @return processName
     */
    public java.lang.String getProcessName() {
        return processName;
    }


    /**
     * Sets the processName value for this DMgmtModuleAgentSnapshot.
     * 
     * @param processName
     */
    public void setProcessName(java.lang.String processName) {
        this.processName = processName;
    }


    /**
     * Gets the timeOfStatusChange value for this DMgmtModuleAgentSnapshot.
     * 
     * @return timeOfStatusChange
     */
    public long getTimeOfStatusChange() {
        return timeOfStatusChange;
    }


    /**
     * Sets the timeOfStatusChange value for this DMgmtModuleAgentSnapshot.
     * 
     * @param timeOfStatusChange
     */
    public void setTimeOfStatusChange(long timeOfStatusChange) {
        this.timeOfStatusChange = timeOfStatusChange;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DMgmtModuleAgentSnapshot)) return false;
        DMgmtModuleAgentSnapshot other = (DMgmtModuleAgentSnapshot) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.agentCurrStatus == other.getAgentCurrStatus() &&
            ((this.agentDashboardURL==null && other.getAgentDashboardURL()==null) || 
             (this.agentDashboardURL!=null &&
              this.agentDashboardURL.equals(other.getAgentDashboardURL()))) &&
            ((this.agentIdentifier==null && other.getAgentIdentifier()==null) || 
             (this.agentIdentifier!=null &&
              this.agentIdentifier.equals(other.getAgentIdentifier()))) &&
            ((this.agentName==null && other.getAgentName()==null) || 
             (this.agentName!=null &&
              this.agentName.equals(other.getAgentName()))) &&
            this.agentPrevStatus == other.getAgentPrevStatus() &&
            ((this.hostName==null && other.getHostName()==null) || 
             (this.hostName!=null &&
              this.hostName.equals(other.getHostName()))) &&
            ((this.manModuleName==null && other.getManModuleName()==null) || 
             (this.manModuleName!=null &&
              this.manModuleName.equals(other.getManModuleName()))) &&
            ((this.processName==null && other.getProcessName()==null) || 
             (this.processName!=null &&
              this.processName.equals(other.getProcessName()))) &&
            this.timeOfStatusChange == other.getTimeOfStatusChange();
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
        _hashCode += getAgentCurrStatus();
        if (getAgentDashboardURL() != null) {
            _hashCode += getAgentDashboardURL().hashCode();
        }
        if (getAgentIdentifier() != null) {
            _hashCode += getAgentIdentifier().hashCode();
        }
        if (getAgentName() != null) {
            _hashCode += getAgentName().hashCode();
        }
        _hashCode += getAgentPrevStatus();
        if (getHostName() != null) {
            _hashCode += getHostName().hashCode();
        }
        if (getManModuleName() != null) {
            _hashCode += getManModuleName().hashCode();
        }
        if (getProcessName() != null) {
            _hashCode += getProcessName().hashCode();
        }
        _hashCode += new Long(getTimeOfStatusChange()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DMgmtModuleAgentSnapshot.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAgentSnapshot"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentCurrStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentCurrStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentDashboardURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentDashboardURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentPrevStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentPrevStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hostName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModuleName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModuleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "processName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeOfStatusChange");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeOfStatusChange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
