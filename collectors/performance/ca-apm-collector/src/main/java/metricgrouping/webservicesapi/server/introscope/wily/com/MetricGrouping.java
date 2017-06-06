/**
 * MetricGrouping.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class MetricGrouping  implements java.io.Serializable {
    private metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] agentExp;

    private java.lang.String description;

    private java.lang.String domainName;

    private metricgrouping.webservicesapi.server.introscope.wily.com.MetricExpression[] metricExp;

    private java.lang.String metricGroupName;

    private java.lang.String mmName;

    public MetricGrouping() {
    }

    public MetricGrouping(
           metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] agentExp,
           java.lang.String description,
           java.lang.String domainName,
           metricgrouping.webservicesapi.server.introscope.wily.com.MetricExpression[] metricExp,
           java.lang.String metricGroupName,
           java.lang.String mmName) {
           this.agentExp = agentExp;
           this.description = description;
           this.domainName = domainName;
           this.metricExp = metricExp;
           this.metricGroupName = metricGroupName;
           this.mmName = mmName;
    }


    /**
     * Gets the agentExp value for this MetricGrouping.
     * 
     * @return agentExp
     */
    public metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] getAgentExp() {
        return agentExp;
    }


    /**
     * Sets the agentExp value for this MetricGrouping.
     * 
     * @param agentExp
     */
    public void setAgentExp(metricgrouping.webservicesapi.server.introscope.wily.com.AgentExpression[] agentExp) {
        this.agentExp = agentExp;
    }


    /**
     * Gets the description value for this MetricGrouping.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this MetricGrouping.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the domainName value for this MetricGrouping.
     * 
     * @return domainName
     */
    public java.lang.String getDomainName() {
        return domainName;
    }


    /**
     * Sets the domainName value for this MetricGrouping.
     * 
     * @param domainName
     */
    public void setDomainName(java.lang.String domainName) {
        this.domainName = domainName;
    }


    /**
     * Gets the metricExp value for this MetricGrouping.
     * 
     * @return metricExp
     */
    public metricgrouping.webservicesapi.server.introscope.wily.com.MetricExpression[] getMetricExp() {
        return metricExp;
    }


    /**
     * Sets the metricExp value for this MetricGrouping.
     * 
     * @param metricExp
     */
    public void setMetricExp(metricgrouping.webservicesapi.server.introscope.wily.com.MetricExpression[] metricExp) {
        this.metricExp = metricExp;
    }


    /**
     * Gets the metricGroupName value for this MetricGrouping.
     * 
     * @return metricGroupName
     */
    public java.lang.String getMetricGroupName() {
        return metricGroupName;
    }


    /**
     * Sets the metricGroupName value for this MetricGrouping.
     * 
     * @param metricGroupName
     */
    public void setMetricGroupName(java.lang.String metricGroupName) {
        this.metricGroupName = metricGroupName;
    }


    /**
     * Gets the mmName value for this MetricGrouping.
     * 
     * @return mmName
     */
    public java.lang.String getMmName() {
        return mmName;
    }


    /**
     * Sets the mmName value for this MetricGrouping.
     * 
     * @param mmName
     */
    public void setMmName(java.lang.String mmName) {
        this.mmName = mmName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MetricGrouping)) return false;
        MetricGrouping other = (MetricGrouping) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.agentExp==null && other.getAgentExp()==null) || 
             (this.agentExp!=null &&
              java.util.Arrays.equals(this.agentExp, other.getAgentExp()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.domainName==null && other.getDomainName()==null) || 
             (this.domainName!=null &&
              this.domainName.equals(other.getDomainName()))) &&
            ((this.metricExp==null && other.getMetricExp()==null) || 
             (this.metricExp!=null &&
              java.util.Arrays.equals(this.metricExp, other.getMetricExp()))) &&
            ((this.metricGroupName==null && other.getMetricGroupName()==null) || 
             (this.metricGroupName!=null &&
              this.metricGroupName.equals(other.getMetricGroupName()))) &&
            ((this.mmName==null && other.getMmName()==null) || 
             (this.mmName!=null &&
              this.mmName.equals(other.getMmName())));
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
        if (getAgentExp() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAgentExp());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAgentExp(), i);
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
        if (getMetricExp() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMetricExp());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMetricExp(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMetricGroupName() != null) {
            _hashCode += getMetricGroupName().hashCode();
        }
        if (getMmName() != null) {
            _hashCode += getMmName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MetricGrouping.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricGrouping"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentExp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentExp"));
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
        elemField.setFieldName("metricExp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "metricExp"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "MetricExpression"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("metricGroupName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "metricGroupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mmName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mmName"));
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
