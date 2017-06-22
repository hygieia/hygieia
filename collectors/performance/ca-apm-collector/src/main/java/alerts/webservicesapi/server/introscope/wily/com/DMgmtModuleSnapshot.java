/**
 * DMgmtModuleSnapshot.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class DMgmtModuleSnapshot  implements java.io.Serializable {
    private int manModCurrStatus;

    private java.lang.String manModDashboardURL;

    private int manModPrevStatus;

    private java.lang.String manModuleName;

    private long timeOfStatusChange;

    public DMgmtModuleSnapshot() {
    }

    public DMgmtModuleSnapshot(
           int manModCurrStatus,
           java.lang.String manModDashboardURL,
           int manModPrevStatus,
           java.lang.String manModuleName,
           long timeOfStatusChange) {
           this.manModCurrStatus = manModCurrStatus;
           this.manModDashboardURL = manModDashboardURL;
           this.manModPrevStatus = manModPrevStatus;
           this.manModuleName = manModuleName;
           this.timeOfStatusChange = timeOfStatusChange;
    }


    /**
     * Gets the manModCurrStatus value for this DMgmtModuleSnapshot.
     * 
     * @return manModCurrStatus
     */
    public int getManModCurrStatus() {
        return manModCurrStatus;
    }


    /**
     * Sets the manModCurrStatus value for this DMgmtModuleSnapshot.
     * 
     * @param manModCurrStatus
     */
    public void setManModCurrStatus(int manModCurrStatus) {
        this.manModCurrStatus = manModCurrStatus;
    }


    /**
     * Gets the manModDashboardURL value for this DMgmtModuleSnapshot.
     * 
     * @return manModDashboardURL
     */
    public java.lang.String getManModDashboardURL() {
        return manModDashboardURL;
    }


    /**
     * Sets the manModDashboardURL value for this DMgmtModuleSnapshot.
     * 
     * @param manModDashboardURL
     */
    public void setManModDashboardURL(java.lang.String manModDashboardURL) {
        this.manModDashboardURL = manModDashboardURL;
    }


    /**
     * Gets the manModPrevStatus value for this DMgmtModuleSnapshot.
     * 
     * @return manModPrevStatus
     */
    public int getManModPrevStatus() {
        return manModPrevStatus;
    }


    /**
     * Sets the manModPrevStatus value for this DMgmtModuleSnapshot.
     * 
     * @param manModPrevStatus
     */
    public void setManModPrevStatus(int manModPrevStatus) {
        this.manModPrevStatus = manModPrevStatus;
    }


    /**
     * Gets the manModuleName value for this DMgmtModuleSnapshot.
     * 
     * @return manModuleName
     */
    public java.lang.String getManModuleName() {
        return manModuleName;
    }


    /**
     * Sets the manModuleName value for this DMgmtModuleSnapshot.
     * 
     * @param manModuleName
     */
    public void setManModuleName(java.lang.String manModuleName) {
        this.manModuleName = manModuleName;
    }


    /**
     * Gets the timeOfStatusChange value for this DMgmtModuleSnapshot.
     * 
     * @return timeOfStatusChange
     */
    public long getTimeOfStatusChange() {
        return timeOfStatusChange;
    }


    /**
     * Sets the timeOfStatusChange value for this DMgmtModuleSnapshot.
     * 
     * @param timeOfStatusChange
     */
    public void setTimeOfStatusChange(long timeOfStatusChange) {
        this.timeOfStatusChange = timeOfStatusChange;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DMgmtModuleSnapshot)) return false;
        DMgmtModuleSnapshot other = (DMgmtModuleSnapshot) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.manModCurrStatus == other.getManModCurrStatus() &&
            ((this.manModDashboardURL==null && other.getManModDashboardURL()==null) || 
             (this.manModDashboardURL!=null &&
              this.manModDashboardURL.equals(other.getManModDashboardURL()))) &&
            this.manModPrevStatus == other.getManModPrevStatus() &&
            ((this.manModuleName==null && other.getManModuleName()==null) || 
             (this.manModuleName!=null &&
              this.manModuleName.equals(other.getManModuleName()))) &&
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
        _hashCode += getManModCurrStatus();
        if (getManModDashboardURL() != null) {
            _hashCode += getManModDashboardURL().hashCode();
        }
        _hashCode += getManModPrevStatus();
        if (getManModuleName() != null) {
            _hashCode += getManModuleName().hashCode();
        }
        _hashCode += new Long(getTimeOfStatusChange()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DMgmtModuleSnapshot.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DMgmtModuleSnapshot"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModCurrStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModCurrStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModDashboardURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModDashboardURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModPrevStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModPrevStatus"));
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
