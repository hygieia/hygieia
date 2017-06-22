/**
 * DEMConfig.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class DEMConfig  implements java.io.Serializable {
    private java.lang.String emDashboardURL;

    private java.lang.String emHostName;

    private java.lang.String emIpAddress;

    private long emLaunchTime;

    private int emWebServerPort;

    public DEMConfig() {
    }

    public DEMConfig(
           java.lang.String emDashboardURL,
           java.lang.String emHostName,
           java.lang.String emIpAddress,
           long emLaunchTime,
           int emWebServerPort) {
           this.emDashboardURL = emDashboardURL;
           this.emHostName = emHostName;
           this.emIpAddress = emIpAddress;
           this.emLaunchTime = emLaunchTime;
           this.emWebServerPort = emWebServerPort;
    }


    /**
     * Gets the emDashboardURL value for this DEMConfig.
     * 
     * @return emDashboardURL
     */
    public java.lang.String getEmDashboardURL() {
        return emDashboardURL;
    }


    /**
     * Sets the emDashboardURL value for this DEMConfig.
     * 
     * @param emDashboardURL
     */
    public void setEmDashboardURL(java.lang.String emDashboardURL) {
        this.emDashboardURL = emDashboardURL;
    }


    /**
     * Gets the emHostName value for this DEMConfig.
     * 
     * @return emHostName
     */
    public java.lang.String getEmHostName() {
        return emHostName;
    }


    /**
     * Sets the emHostName value for this DEMConfig.
     * 
     * @param emHostName
     */
    public void setEmHostName(java.lang.String emHostName) {
        this.emHostName = emHostName;
    }


    /**
     * Gets the emIpAddress value for this DEMConfig.
     * 
     * @return emIpAddress
     */
    public java.lang.String getEmIpAddress() {
        return emIpAddress;
    }


    /**
     * Sets the emIpAddress value for this DEMConfig.
     * 
     * @param emIpAddress
     */
    public void setEmIpAddress(java.lang.String emIpAddress) {
        this.emIpAddress = emIpAddress;
    }


    /**
     * Gets the emLaunchTime value for this DEMConfig.
     * 
     * @return emLaunchTime
     */
    public long getEmLaunchTime() {
        return emLaunchTime;
    }


    /**
     * Sets the emLaunchTime value for this DEMConfig.
     * 
     * @param emLaunchTime
     */
    public void setEmLaunchTime(long emLaunchTime) {
        this.emLaunchTime = emLaunchTime;
    }


    /**
     * Gets the emWebServerPort value for this DEMConfig.
     * 
     * @return emWebServerPort
     */
    public int getEmWebServerPort() {
        return emWebServerPort;
    }


    /**
     * Sets the emWebServerPort value for this DEMConfig.
     * 
     * @param emWebServerPort
     */
    public void setEmWebServerPort(int emWebServerPort) {
        this.emWebServerPort = emWebServerPort;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DEMConfig)) return false;
        DEMConfig other = (DEMConfig) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.emDashboardURL==null && other.getEmDashboardURL()==null) || 
             (this.emDashboardURL!=null &&
              this.emDashboardURL.equals(other.getEmDashboardURL()))) &&
            ((this.emHostName==null && other.getEmHostName()==null) || 
             (this.emHostName!=null &&
              this.emHostName.equals(other.getEmHostName()))) &&
            ((this.emIpAddress==null && other.getEmIpAddress()==null) || 
             (this.emIpAddress!=null &&
              this.emIpAddress.equals(other.getEmIpAddress()))) &&
            this.emLaunchTime == other.getEmLaunchTime() &&
            this.emWebServerPort == other.getEmWebServerPort();
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
        if (getEmDashboardURL() != null) {
            _hashCode += getEmDashboardURL().hashCode();
        }
        if (getEmHostName() != null) {
            _hashCode += getEmHostName().hashCode();
        }
        if (getEmIpAddress() != null) {
            _hashCode += getEmIpAddress().hashCode();
        }
        _hashCode += new Long(getEmLaunchTime()).hashCode();
        _hashCode += getEmWebServerPort();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DEMConfig.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.alerts", "DEMConfig"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emDashboardURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emDashboardURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emHostName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emHostName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emIpAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emIpAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emLaunchTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emLaunchTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emWebServerPort");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emWebServerPort"));
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
