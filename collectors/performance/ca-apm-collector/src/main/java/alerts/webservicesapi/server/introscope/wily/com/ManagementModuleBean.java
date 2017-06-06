/**
 * ManagementModuleBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class ManagementModuleBean  implements java.io.Serializable {
    private java.lang.String manModuleName;

    public ManagementModuleBean() {
    }

    public ManagementModuleBean(
           java.lang.String manModuleName) {
           this.manModuleName = manModuleName;
    }


    /**
     * Gets the manModuleName value for this ManagementModuleBean.
     * 
     * @return manModuleName
     */
    public java.lang.String getManModuleName() {
        return manModuleName;
    }


    /**
     * Sets the manModuleName value for this ManagementModuleBean.
     * 
     * @param manModuleName
     */
    public void setManModuleName(java.lang.String manModuleName) {
        this.manModuleName = manModuleName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ManagementModuleBean)) return false;
        ManagementModuleBean other = (ManagementModuleBean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.manModuleName==null && other.getManModuleName()==null) || 
             (this.manModuleName!=null &&
              this.manModuleName.equals(other.getManModuleName())));
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
        if (getManModuleName() != null) {
            _hashCode += getManModuleName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ManagementModuleBean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "ManagementModuleBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manModuleName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manModuleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
