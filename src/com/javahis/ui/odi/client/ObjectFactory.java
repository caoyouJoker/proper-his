
package com.javahis.ui.odi.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.javahis.ui.odi.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SendElectronicTag_QNAME = new QName("http://services.spc.action/", "sendElectronicTag");
    private final static QName _SendElectronicTagResponse_QNAME = new QName("http://services.spc.action/", "sendElectronicTagResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.javahis.ui.odi.client
     * 
     */
    public ObjectFactory() {
    }

     
    /**
     * Create an instance of {@link SendElectronicTagResponse }
     * 
     */
    public SendElectronicTagResponse createSendElectronicTagResponse() {
        return new SendElectronicTagResponse();
    }

   
    /**
     * Create an instance of {@link SendElectronicTag }
     * 
     */
    public SendElectronicTag createSendElectronicTag() {
        return new SendElectronicTag();
    }
      

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendElectronicTag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "sendElectronicTag")
    public JAXBElement<SendElectronicTag> createSendElectronicTag(SendElectronicTag value) {
        return new JAXBElement<SendElectronicTag>(_SendElectronicTag_QNAME, SendElectronicTag.class, null, value);
    }

    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendElectronicTagResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "sendElectronicTagResponse")
    public JAXBElement<SendElectronicTagResponse> createSendElectronicTagResponse(SendElectronicTagResponse value) {
        return new JAXBElement<SendElectronicTagResponse>(_SendElectronicTagResponse_QNAME, SendElectronicTagResponse.class, null, value);
    }

}
