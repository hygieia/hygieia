package com.capitalone.dashboard.collector.coverity.soap;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecUsernameToken;

/**
 * Coverity uses ws-security for client requests.
 * This handler will insert username and password to the SOAP header.  
 */
public class ClientAuthHeaderHandlerWSS implements SOAPHandler<SOAPMessageContext> {

    private final String usernameText;
    private final String passwordText;

    public ClientAuthHeaderHandlerWSS(String usernameText, String passwordText) {
        this.usernameText = usernameText;
        this.passwordText = passwordText;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty.booleanValue()) {

            try {
                SOAPMessage soapMessage = context.getMessage();
                soapMessage.removeAllAttachments();

                SOAPPart soappart = soapMessage.getSOAPPart();
                WSSecHeader wsSecHeader = new WSSecHeader();
                wsSecHeader.insertSecurityHeader(soappart);
                WSSecUsernameToken token = new WSSecUsernameToken();
                token.setPasswordType(WSConstants.PASSWORD_TEXT);
                token.setUserInfo(usernameText, passwordText);
                token.build(soappart, wsSecHeader);

                soapMessage.saveChanges();
            } catch (Exception e) {
                throw new RuntimeException("Error on wsSecurityHandler: " + e.getMessage());
            }

        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}
