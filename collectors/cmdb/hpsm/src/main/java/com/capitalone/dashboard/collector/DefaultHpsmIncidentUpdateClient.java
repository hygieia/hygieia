package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.HpsmSoapModel;
import com.capitalone.dashboard.model.Incident;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
public class DefaultHpsmIncidentUpdateClient extends DefaultBaseClient implements HpsmIncidentUpdateClient {
    private static final Log LOG = LogFactory.getLog(DefaultHpsmIncidentUpdateClient.class);

    private final HpsmSettings hpsmSettings;

    @Autowired
    public DefaultHpsmIncidentUpdateClient(HpsmSettings hpsmSettings) {
        super(hpsmSettings);
        this.hpsmSettings = hpsmSettings;
    }

    public Incident getIncident(String incidentId) throws HygieiaException {
        Incident incident = null;

        HpsmSoapModel hpsmSoapModel = new HpsmSoapModel();
        hpsmSoapModel.setRequestTypeName(hpsmSettings.getIncidentUpdatesRequestType());
        hpsmSoapModel.setSoapAction(hpsmSettings.getIncidentUpdatesSoapAction());

        String soapString = getSoapMessage(hpsmSoapModel, incidentId);

        String response  = makeSoapCall(soapString, hpsmSoapModel);

        List<Incident> incidentList = responseToIncidentList(response);

        if (!CollectionUtils.isEmpty(incidentList)) { incident = incidentList.get(0); }

        return incident;
    }

    protected String getSoapMessage(HpsmSoapModel hpsmSoapModel, String incidentId) {
        String strMsg = "";
        SOAPMessage soapMsg;
        String requestTypeName = hpsmSoapModel.getRequestTypeName();

        try {
            MessageFactory factory = MessageFactory.newInstance();
            soapMsg = factory.createMessage();
            SOAPPart part = soapMsg.getSOAPPart();

            SOAPEnvelope envelope = part.getEnvelope();
            envelope.addNamespaceDeclaration("ns", "http://schemas.hp.com/SM/7");
            envelope.addNamespaceDeclaration("com", "http://schemas.hp.com/SM/7/Common");
            envelope.addNamespaceDeclaration("xm", "http://www.w3.org/2005/05/xmlmime");

            SOAPBody body = envelope.getBody();
            SOAPBodyElement requestType = body.addBodyElement(envelope.createName(requestTypeName,"ns", ""));

            QName qNameIgnoreEmptyValues = new QName("ignoreEmptyElements");
            requestType.addAttribute(qNameIgnoreEmptyValues, "true");

            SOAPBodyElement modelTag = body.addBodyElement(envelope.createName("model","ns", ""));

            SOAPBodyElement keysTag = body.addBodyElement(envelope.createName("keys","ns", ""));
            SOAPBodyElement instanceIDTag = body.addBodyElement(envelope.createName("IncidentID","ns", ""));
            instanceIDTag.setValue(incidentId);
            keysTag.addChildElement(instanceIDTag);

            // creates instance tag
            body.addBodyElement(envelope.createName("instance", "ns", ""));

            modelTag.addChildElement(keysTag);
            requestType.addChildElement(modelTag);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMsg.writeTo(out);
            strMsg = new String(out.toByteArray());
        } catch (SOAPException e) {
            LOG.error("SOAPException: " + e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException: " + e);
        } catch (IOException e) {
            LOG.error("IOException: " + e);
        }

        return strMsg;
    }
}