/**
 * AgentExpression.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metricgrouping.webservicesapi.server.introscope.wily.com;

@SuppressWarnings("PMD")
public class AgentExpression  implements java.io.Serializable {
    private java.lang.String agentExpression;

    public AgentExpression() {
    }

    public AgentExpression(
           java.lang.String agentExpression) {
           this.agentExpression = agentExpression;
    }


    /**
     * Gets the agentExpression value for this AgentExpression.
     * 
     * @return agentExpression
     */
    public java.lang.String getAgentExpression() {
        return agentExpression;
    }


    /**
     * Sets the agentExpression value for this AgentExpression.
     * 
     * @param agentExpression
     */
    public void setAgentExpression(java.lang.String agentExpression) {
        this.agentExpression = agentExpression;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AgentExpression)) return false;
        AgentExpression other = (AgentExpression) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.agentExpression==null && other.getAgentExpression()==null) || 
             (this.agentExpression!=null &&
              this.agentExpression.equals(other.getAgentExpression())));
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
        if (getAgentExpression() != null) {
            _hashCode += getAgentExpression().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AgentExpression.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.wily.introscope.server.webservicesapi.metricgrouping", "AgentExpression"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentExpression");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentExpression"));
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
