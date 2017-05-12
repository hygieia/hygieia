package com.capitalone.dashboard.model.quality;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results of exexuting a testsuite
 * <p>
 * <p>Java class for testsuite complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="testsuite">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="properties">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="property" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="name" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *                                 &lt;minLength value="1"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="testcase" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice minOccurs="0">
 *                   &lt;element name="error">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;>pre-string">
 *                           &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="failure">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;>pre-string">
 *                           &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                 &lt;attribute name="classname" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                 &lt;attribute name="time" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="system-out">
 *           &lt;simpleType>
 *             &lt;restriction base="{}pre-string">
 *               &lt;whiteSpace value="preserve"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="system-err">
 *           &lt;simpleType>
 *             &lt;restriction base="{}pre-string">
 *               &lt;whiteSpace value="preserve"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="timestamp" use="required" type="{}ISO8601_DATETIME_PATTERN" />
 *       &lt;attribute name="hostname" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="tests" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="failures" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="errors" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="time" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "testsuite")
public class JunitXmlReport implements CodeQualityVisitee {

    @XmlElement(required = true)
    protected JunitXmlReport.Properties properties;
    protected List<Testcase> testcase;
    @XmlElement(name = "system-out", required = true)
    protected String systemOut;
    @XmlElement(name = "system-err", required = true)
    protected String systemErr;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;
    @XmlAttribute(name = "timestamp", required = true)
    protected XMLGregorianCalendar timestamp;
    @XmlAttribute(name = "hostname", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String hostname;
    @XmlAttribute(name = "tests", required = true)
    protected int tests;
    @XmlAttribute(name = "failures", required = true)
    protected int failures;
    @XmlAttribute(name = "errors", required = true)
    protected int errors;
    @XmlAttribute(name = "time", required = true)
    protected BigDecimal time;

    /**
     * Gets the value of the properties property.
     *
     * @return possible object is
     * {@link JunitXmlReport.Properties }
     */
    public JunitXmlReport.Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value allowed object is
     *              {@link JunitXmlReport.Properties }
     */
    public void setProperties(JunitXmlReport.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the testcase property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testcase property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestcase().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JunitXmlReport.Testcase }
     */
    public List<Testcase> getTestcase() {
        if (testcase == null) {
            testcase = new ArrayList<Testcase>();
        }
        return this.testcase;
    }

    /**
     * Gets the value of the systemOut property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSystemOut() {
        return systemOut;
    }

    /**
     * Sets the value of the systemOut property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSystemOut(String value) {
        this.systemOut = value;
    }

    /**
     * Gets the value of the systemErr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSystemErr() {
        return systemErr;
    }

    /**
     * Sets the value of the systemErr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSystemErr(String value) {
        this.systemErr = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the hostname property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the value of the hostname property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setHostname(String value) {
        this.hostname = value;
    }

    /**
     * Gets the value of the tests property.
     */
    public int getTests() {
        return tests;
    }

    /**
     * Sets the value of the tests property.
     */
    public void setTests(int value) {
        this.tests = value;
    }

    /**
     * Gets the value of the failures property.
     */
    public int getFailures() {
        return failures;
    }

    /**
     * Sets the value of the failures property.
     */
    public void setFailures(int value) {
        this.failures = value;
    }

    /**
     * Gets the value of the errors property.
     */
    public int getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     */
    public void setErrors(int value) {
        this.errors = value;
    }

    /**
     * Gets the value of the time property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setTime(BigDecimal value) {
        this.time = value;
    }

    @Override
    public void accept(CodeQualityVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="property" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="name" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
     *                       &lt;minLength value="1"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"property"})
    public static class Properties {

        protected List<Property> property;

        /**
         * Gets the value of the property property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the property property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProperty().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JunitXmlReport.Properties.Property }
         */
        public List<Property> getProperty() {
            if (property == null) {
                property = new ArrayList<Property>();
            }
            return this.property;
        }

        /**
         * <p>Java class for anonymous complex type.
         * <p>
         * <p>The following schema fragment specifies the expected content contained within this class.
         * <p>
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="name" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
         *             &lt;minLength value="1"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Property {

            @XmlAttribute(name = "name", required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            protected String name;
            @XmlAttribute(name = "value", required = true)
            protected String value;

            /**
             * Gets the value of the name property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the value property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice minOccurs="0">
     *         &lt;element name="error">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;>pre-string">
     *                 &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="failure">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;>pre-string">
     *                 &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *       &lt;attribute name="classname" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *       &lt;attribute name="time" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"error", "failure"})
    public static class Testcase {

        protected JunitXmlReport.Testcase.Error error;
        protected JunitXmlReport.Testcase.Failure failure;
        @XmlAttribute(name = "name", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String name;
        @XmlAttribute(name = "classname", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String classname;
        @XmlAttribute(name = "time", required = true)
        protected BigDecimal time;

        /**
         * Gets the value of the error property.
         *
         * @return possible object is
         * {@link JunitXmlReport.Testcase.Error }
         */
        public JunitXmlReport.Testcase.Error getError() {
            return error;
        }

        /**
         * Sets the value of the error property.
         *
         * @param value allowed object is
         *              {@link JunitXmlReport.Testcase.Error }
         */
        public void setError(JunitXmlReport.Testcase.Error value) {
            this.error = value;
        }

        /**
         * Gets the value of the failure property.
         *
         * @return possible object is
         * {@link JunitXmlReport.Testcase.Failure }
         */
        public JunitXmlReport.Testcase.Failure getFailure() {
            return failure;
        }

        /**
         * Sets the value of the failure property.
         *
         * @param value allowed object is
         *              {@link JunitXmlReport.Testcase.Failure }
         */
        public void setFailure(JunitXmlReport.Testcase.Failure value) {
            this.failure = value;
        }

        /**
         * Gets the value of the name property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the classname property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getClassname() {
            return classname;
        }

        /**
         * Sets the value of the classname property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setClassname(String value) {
            this.classname = value;
        }

        /**
         * Gets the value of the time property.
         *
         * @return possible object is
         * {@link BigDecimal }
         */
        public BigDecimal getTime() {
            return time;
        }

        /**
         * Sets the value of the time property.
         *
         * @param value allowed object is
         *              {@link BigDecimal }
         */
        public void setTime(BigDecimal value) {
            this.time = value;
        }

        /**
         * <p>Java class for anonymous complex type.
         * <p>
         * <p>The following schema fragment specifies the expected content contained within this class.
         * <p>
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;>pre-string">
         *       &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"value"})
        public static class Error {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "message")
            protected String message;
            @XmlAttribute(name = "type", required = true)
            protected String type;

            /**
             * Gets the value of the value property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the message property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getMessage() {
                return message;
            }

            /**
             * Sets the value of the message property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setMessage(String value) {
                this.message = value;
            }

            /**
             * Gets the value of the type property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setType(String value) {
                this.type = value;
            }

        }

        /**
         * <p>Java class for anonymous complex type.
         * <p>
         * <p>The following schema fragment specifies the expected content contained within this class.
         * <p>
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;>pre-string">
         *       &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"value"})
        public static class Failure {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "message")
            protected String message;
            @XmlAttribute(name = "type", required = true)
            protected String type;

            /**
             * Gets the value of the value property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the message property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getMessage() {
                return message;
            }

            /**
             * Sets the value of the message property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setMessage(String value) {
                this.message = value;
            }

            /**
             * Gets the value of the type property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setType(String value) {
                this.type = value;
            }

        }

    }

}
