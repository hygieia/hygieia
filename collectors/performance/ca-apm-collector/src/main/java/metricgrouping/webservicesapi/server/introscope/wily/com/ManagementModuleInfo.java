/**
 * ManagementModuleInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class ManagementModuleInfo  implements java.io.Serializable {
    private boolean active;

    private metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] agentExps;

    private java.lang.String description;

    private java.lang.String domainName;

    private boolean editable;

    private java.lang.String jarFileName;

    private java.lang.String manModuleName;

    public ManagementModuleInfo() {
    }

    public ManagementModuleInfo(
           boolean active,
           metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] agentExps,
           java.lang.String description,
           java.lang.String domainName,
           boolean editable,
           java.lang.String jarFileName,
           java.lang.String manModuleName) {
           this.active = active;
           this.agentExps = agentExps;
           this.description = description;
           this.domainName = domainName;
           this.editable = editable;
           this.jarFileName = jarFileName;
           this.manModuleName = manModuleName;
    }


    /**
     * Gets the active value for this ManagementModuleInfo.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active value for this ManagementModuleInfo.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the agentExps value for this ManagementModuleInfo.
     * 
     * @return agentExps
     */
    public metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] getAgentExps() {
        return agentExps;
    }


    /**
     * Sets the agentExps value for this ManagementModuleInfo.
     * 
     * @param agentExps
     */
    public void setAgentExps(metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] agentExps) {
        this.agentExps = agentExps;
    }


    /**
     * Gets the description value for this ManagementModuleInfo.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this ManagementModuleInfo.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the domainName value for this ManagementModuleInfo.
     * 
     * @return domainName
     */
    public java.lang.String getDomainName() {
        return domainName;
    }


    /**
     * Sets the domainName value for this ManagementModuleInfo.
     * 
     * @param domainName
     */
    public void setDomainName(java.lang.String domainName) {
        this.domainName = domainName;
    }


    /**
     * Gets the editable value for this ManagementModuleInfo.
     * 
     * @return editable
     */
    public boolean isEditable() {
        return editable;
    }


    /**
     * Sets the editable value for this ManagementModuleInfo.
     * 
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    /**
     * Gets the jarFileName value for this ManagementModuleInfo.
     * 
     * @return jarFileName
     */
    public java.lang.String getJarFileName() {
        return jarFileName;
    }


    /**
     * Sets the jarFileName value for this ManagementModuleInfo.
     * 
     * @param jarFileName
     */
    public void setJarFileName(java.lang.String jarFileName) {
        this.jarFileName = jarFileName;
    }


    /**
     * Gets the manModuleName value for this ManagementModuleInfo.
     * 
     * @return manModuleName
     */
    public java.lang.String getManModuleName() {
        return manModuleName;
    }


    /**
     * Sets the manModuleName value for this ManagementModuleInfo.
     * 
     * @param manModuleName
     */
    public void setManModuleName(java.lang.String manModuleName) {
        this.manModuleName = manModuleName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ManagementModuleInfo)) return false;
        ManagementModuleInfo other = (ManagementModuleInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.active == other.isActive() &&
            ((this.agentExps==null && other.getAgentExps()==null) || 
             (this.agentExps!=null &&
              java.util.Arrays.equals(this.agentExps, other.getAgentExps()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.domainName==null && other.getDomainName()==null) || 
             (this.domainName!=null &&
              this.domainName.equals(other.getDomainName()))) &&
            this.editable == other.isEditable() &&
            ((this.jarFileName==null && other.getJarFileName()==null) || 
             (this.jarFileName!=null &&
              this.jarFileName.equals(other.getJarFileName()))) &&
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
        _hashCode += (isActive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getAgentExps() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAgentExps());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAgentExps(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getDomainName() != null) {
            _hashCode += getDomainName().hashCode();
        }
        _hashCode += (isEditable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getJarFileName() != null) {
            _hashCode += getJarFileName().hashCode();
        }
        if (getManModuleName() != null) {
            _hashCode += getManModuleName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ManagementModuleInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "ManagementModuleInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("active");
        elemField.setXmlName(new javax.xml.namespace.QName("", "active"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentExps");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentExps"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentExpression"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domainName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "domainName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("editable");
        elemField.setXmlName(new javax.xml.namespace.QName("", "editable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jarFileName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jarFileName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
