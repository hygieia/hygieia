/**
 * DMgmtModuleAlertDefnSnapshot.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class DMgmtModuleAlertDefnSnapshot  implements java.io.Serializable {
    private boolean active;

    private java.lang.String agentIdentifier;

    private java.lang.String alertDashboardURL;

    private int alertDefnCurrStatus;

    private int alertDefnPrevStatus;

    private java.lang.String alertIdentifier;

    private int criticalThresholdValue;

    private java.lang.String manModuleName;

    private long timeOfStatusChange;

    private int warningThresholdValue;

    public DMgmtModuleAlertDefnSnapshot() {
    }

    public DMgmtModuleAlertDefnSnapshot(
           boolean active,
           java.lang.String agentIdentifier,
           java.lang.String alertDashboardURL,
           int alertDefnCurrStatus,
           int alertDefnPrevStatus,
           java.lang.String alertIdentifier,
           int criticalThresholdValue,
           java.lang.String manModuleName,
           long timeOfStatusChange,
           int warningThresholdValue) {
           this.active = active;
           this.agentIdentifier = agentIdentifier;
           this.alertDashboardURL = alertDashboardURL;
           this.alertDefnCurrStatus = alertDefnCurrStatus;
           this.alertDefnPrevStatus = alertDefnPrevStatus;
           this.alertIdentifier = alertIdentifier;
           this.criticalThresholdValue = criticalThresholdValue;
           this.manModuleName = manModuleName;
           this.timeOfStatusChange = timeOfStatusChange;
           this.warningThresholdValue = warningThresholdValue;
    }


    /**
     * Gets the active value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the agentIdentifier value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return agentIdentifier
     */
    public java.lang.String getAgentIdentifier() {
        return agentIdentifier;
    }


    /**
     * Sets the agentIdentifier value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param agentIdentifier
     */
    public void setAgentIdentifier(java.lang.String agentIdentifier) {
        this.agentIdentifier = agentIdentifier;
    }


    /**
     * Gets the alertDashboardURL value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return alertDashboardURL
     */
    public java.lang.String getAlertDashboardURL() {
        return alertDashboardURL;
    }


    /**
     * Sets the alertDashboardURL value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param alertDashboardURL
     */
    public void setAlertDashboardURL(java.lang.String alertDashboardURL) {
        this.alertDashboardURL = alertDashboardURL;
    }


    /**
     * Gets the alertDefnCurrStatus value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return alertDefnCurrStatus
     */
    public int getAlertDefnCurrStatus() {
        return alertDefnCurrStatus;
    }


    /**
     * Sets the alertDefnCurrStatus value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param alertDefnCurrStatus
     */
    public void setAlertDefnCurrStatus(int alertDefnCurrStatus) {
        this.alertDefnCurrStatus = alertDefnCurrStatus;
    }


    /**
     * Gets the alertDefnPrevStatus value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return alertDefnPrevStatus
     */
    public int getAlertDefnPrevStatus() {
        return alertDefnPrevStatus;
    }


    /**
     * Sets the alertDefnPrevStatus value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param alertDefnPrevStatus
     */
    public void setAlertDefnPrevStatus(int alertDefnPrevStatus) {
        this.alertDefnPrevStatus = alertDefnPrevStatus;
    }


    /**
     * Gets the alertIdentifier value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return alertIdentifier
     */
    public java.lang.String getAlertIdentifier() {
        return alertIdentifier;
    }


    /**
     * Sets the alertIdentifier value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param alertIdentifier
     */
    public void setAlertIdentifier(java.lang.String alertIdentifier) {
        this.alertIdentifier = alertIdentifier;
    }


    /**
     * Gets the criticalThresholdValue value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return criticalThresholdValue
     */
    public int getCriticalThresholdValue() {
        return criticalThresholdValue;
    }


    /**
     * Sets the criticalThresholdValue value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param criticalThresholdValue
     */
    public void setCriticalThresholdValue(int criticalThresholdValue) {
        this.criticalThresholdValue = criticalThresholdValue;
    }


    /**
     * Gets the manModuleName value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return manModuleName
     */
    public java.lang.String getManModuleName() {
        return manModuleName;
    }


    /**
     * Sets the manModuleName value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param manModuleName
     */
    public void setManModuleName(java.lang.String manModuleName) {
        this.manModuleName = manModuleName;
    }


    /**
     * Gets the timeOfStatusChange value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return timeOfStatusChange
     */
    public long getTimeOfStatusChange() {
        return timeOfStatusChange;
    }


    /**
     * Sets the timeOfStatusChange value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param timeOfStatusChange
     */
    public void setTimeOfStatusChange(long timeOfStatusChange) {
        this.timeOfStatusChange = timeOfStatusChange;
    }


    /**
     * Gets the warningThresholdValue value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @return warningThresholdValue
     */
    public int getWarningThresholdValue() {
        return warningThresholdValue;
    }


    /**
     * Sets the warningThresholdValue value for this DMgmtModuleAlertDefnSnapshot.
     * 
     * @param warningThresholdValue
     */
    public void setWarningThresholdValue(int warningThresholdValue) {
        this.warningThresholdValue = warningThresholdValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DMgmtModuleAlertDefnSnapshot)) return false;
        DMgmtModuleAlertDefnSnapshot other = (DMgmtModuleAlertDefnSnapshot) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.active == other.isActive() &&
            ((this.agentIdentifier==null && other.getAgentIdentifier()==null) || 
             (this.agentIdentifier!=null &&
              this.agentIdentifier.equals(other.getAgentIdentifier()))) &&
            ((this.alertDashboardURL==null && other.getAlertDashboardURL()==null) || 
             (this.alertDashboardURL!=null &&
              this.alertDashboardURL.equals(other.getAlertDashboardURL()))) &&
            this.alertDefnCurrStatus == other.getAlertDefnCurrStatus() &&
            this.alertDefnPrevStatus == other.getAlertDefnPrevStatus() &&
            ((this.alertIdentifier==null && other.getAlertIdentifier()==null) || 
             (this.alertIdentifier!=null &&
              this.alertIdentifier.equals(other.getAlertIdentifier()))) &&
            this.criticalThresholdValue == other.getCriticalThresholdValue() &&
            ((this.manModuleName==null && other.getManModuleName()==null) || 
             (this.manModuleName!=null &&
              this.manModuleName.equals(other.getManModuleName()))) &&
            this.timeOfStatusChange == other.getTimeOfStatusChange() &&
            this.warningThresholdValue == other.getWarningThresholdValue();
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
        _hashCode += (isActive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getAgentIdentifier() != null) {
            _hashCode += getAgentIdentifier().hashCode();
        }
        if (getAlertDashboardURL() != null) {
            _hashCode += getAlertDashboardURL().hashCode();
        }
        _hashCode += getAlertDefnCurrStatus();
        _hashCode += getAlertDefnPrevStatus();
        if (getAlertIdentifier() != null) {
            _hashCode += getAlertIdentifier().hashCode();
        }
        _hashCode += getCriticalThresholdValue();
        if (getManModuleName() != null) {
            _hashCode += getManModuleName().hashCode();
        }
        _hashCode += new Long(getTimeOfStatusChange()).hashCode();
        _hashCode += getWarningThresholdValue();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DMgmtModuleAlertDefnSnapshot.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleAlertDefnSnapshot"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("active");
        elemField.setXmlName(new javax.xml.namespace.QName("", "active"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertDashboardURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertDashboardURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertDefnCurrStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertDefnCurrStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertDefnPrevStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertDefnPrevStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("criticalThresholdValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "criticalThresholdValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModuleName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModuleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeOfStatusChange");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeOfStatusChange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warningThresholdValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "warningThresholdValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
