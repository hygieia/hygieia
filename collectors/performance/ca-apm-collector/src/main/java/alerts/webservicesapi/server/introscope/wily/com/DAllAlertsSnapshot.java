/**
 * DAllAlertsSnapshot.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class DAllAlertsSnapshot  implements java.io.Serializable {
    private int alertCurrStatus;

    private java.lang.String alertName;

    private int alertPrevStatus;

    private boolean alertStatusChanged;

    private java.lang.String manModuleName;

    private boolean simpleAlert;

    private int thresholdValue;

    public DAllAlertsSnapshot() {
    }

    public DAllAlertsSnapshot(
           int alertCurrStatus,
           java.lang.String alertName,
           int alertPrevStatus,
           boolean alertStatusChanged,
           java.lang.String manModuleName,
           boolean simpleAlert,
           int thresholdValue) {
           this.alertCurrStatus = alertCurrStatus;
           this.alertName = alertName;
           this.alertPrevStatus = alertPrevStatus;
           this.alertStatusChanged = alertStatusChanged;
           this.manModuleName = manModuleName;
           this.simpleAlert = simpleAlert;
           this.thresholdValue = thresholdValue;
    }


    /**
     * Gets the alertCurrStatus value for this DAllAlertsSnapshot.
     * 
     * @return alertCurrStatus
     */
    public int getAlertCurrStatus() {
        return alertCurrStatus;
    }


    /**
     * Sets the alertCurrStatus value for this DAllAlertsSnapshot.
     * 
     * @param alertCurrStatus
     */
    public void setAlertCurrStatus(int alertCurrStatus) {
        this.alertCurrStatus = alertCurrStatus;
    }


    /**
     * Gets the alertName value for this DAllAlertsSnapshot.
     * 
     * @return alertName
     */
    public java.lang.String getAlertName() {
        return alertName;
    }


    /**
     * Sets the alertName value for this DAllAlertsSnapshot.
     * 
     * @param alertName
     */
    public void setAlertName(java.lang.String alertName) {
        this.alertName = alertName;
    }


    /**
     * Gets the alertPrevStatus value for this DAllAlertsSnapshot.
     * 
     * @return alertPrevStatus
     */
    public int getAlertPrevStatus() {
        return alertPrevStatus;
    }


    /**
     * Sets the alertPrevStatus value for this DAllAlertsSnapshot.
     * 
     * @param alertPrevStatus
     */
    public void setAlertPrevStatus(int alertPrevStatus) {
        this.alertPrevStatus = alertPrevStatus;
    }


    /**
     * Gets the alertStatusChanged value for this DAllAlertsSnapshot.
     * 
     * @return alertStatusChanged
     */
    public boolean isAlertStatusChanged() {
        return alertStatusChanged;
    }


    /**
     * Sets the alertStatusChanged value for this DAllAlertsSnapshot.
     * 
     * @param alertStatusChanged
     */
    public void setAlertStatusChanged(boolean alertStatusChanged) {
        this.alertStatusChanged = alertStatusChanged;
    }


    /**
     * Gets the manModuleName value for this DAllAlertsSnapshot.
     * 
     * @return manModuleName
     */
    public java.lang.String getManModuleName() {
        return manModuleName;
    }


    /**
     * Sets the manModuleName value for this DAllAlertsSnapshot.
     * 
     * @param manModuleName
     */
    public void setManModuleName(java.lang.String manModuleName) {
        this.manModuleName = manModuleName;
    }


    /**
     * Gets the simpleAlert value for this DAllAlertsSnapshot.
     * 
     * @return simpleAlert
     */
    public boolean isSimpleAlert() {
        return simpleAlert;
    }


    /**
     * Sets the simpleAlert value for this DAllAlertsSnapshot.
     * 
     * @param simpleAlert
     */
    public void setSimpleAlert(boolean simpleAlert) {
        this.simpleAlert = simpleAlert;
    }


    /**
     * Gets the thresholdValue value for this DAllAlertsSnapshot.
     * 
     * @return thresholdValue
     */
    public int getThresholdValue() {
        return thresholdValue;
    }


    /**
     * Sets the thresholdValue value for this DAllAlertsSnapshot.
     * 
     * @param thresholdValue
     */
    public void setThresholdValue(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DAllAlertsSnapshot)) return false;
        DAllAlertsSnapshot other = (DAllAlertsSnapshot) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.alertCurrStatus == other.getAlertCurrStatus() &&
            ((this.alertName==null && other.getAlertName()==null) || 
             (this.alertName!=null &&
              this.alertName.equals(other.getAlertName()))) &&
            this.alertPrevStatus == other.getAlertPrevStatus() &&
            this.alertStatusChanged == other.isAlertStatusChanged() &&
            ((this.manModuleName==null && other.getManModuleName()==null) || 
             (this.manModuleName!=null &&
              this.manModuleName.equals(other.getManModuleName()))) &&
            this.simpleAlert == other.isSimpleAlert() &&
            this.thresholdValue == other.getThresholdValue();
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
        _hashCode += getAlertCurrStatus();
        if (getAlertName() != null) {
            _hashCode += getAlertName().hashCode();
        }
        _hashCode += getAlertPrevStatus();
        _hashCode += (isAlertStatusChanged() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getManModuleName() != null) {
            _hashCode += getManModuleName().hashCode();
        }
        _hashCode += (isSimpleAlert() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getThresholdValue();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DAllAlertsSnapshot.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DAllAlertsSnapshot"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertCurrStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertCurrStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertPrevStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertPrevStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertStatusChanged");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alertStatusChanged"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModuleName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModuleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("simpleAlert");
        elemField.setXmlName(new javax.xml.namespace.QName("", "simpleAlert"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thresholdValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "thresholdValue"));
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
