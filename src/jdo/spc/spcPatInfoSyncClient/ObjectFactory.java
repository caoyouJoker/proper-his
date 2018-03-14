
package jdo.spc.spcPatInfoSyncClient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jdo.spc.spcPatInfoSyncClient package. 
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

    private final static QName _OnSaveSpcPatInfo_QNAME = new QName("http://reqinf.spc.jdo/", "onSaveSpcPatInfo");
    private final static QName _OnSaveSpcPatInfoResponse_QNAME = new QName("http://reqinf.spc.jdo/", "onSaveSpcPatInfoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jdo.spc.spcPatInfoSyncClient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnSaveSpcPatInfo }
     * 
     */
    public OnSaveSpcPatInfo createOnSaveSpcPatInfo() {
        return new OnSaveSpcPatInfo();
    }

    /**
     * Create an instance of {@link OnSaveSpcPatInfoResponse }
     * 
     */
    public OnSaveSpcPatInfoResponse createOnSaveSpcPatInfoResponse() {
        return new OnSaveSpcPatInfoResponse();
    }

    /**
     * Create an instance of {@link SysPatinfo }
     * 
     */
    public SysPatinfo createSysPatinfo() {
        return new SysPatinfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveSpcPatInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reqinf.spc.jdo/", name = "onSaveSpcPatInfo")
    public JAXBElement<OnSaveSpcPatInfo> createOnSaveSpcPatInfo(OnSaveSpcPatInfo value) {
        return new JAXBElement<OnSaveSpcPatInfo>(_OnSaveSpcPatInfo_QNAME, OnSaveSpcPatInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveSpcPatInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reqinf.spc.jdo/", name = "onSaveSpcPatInfoResponse")
    public JAXBElement<OnSaveSpcPatInfoResponse> createOnSaveSpcPatInfoResponse(OnSaveSpcPatInfoResponse value) {
        return new JAXBElement<OnSaveSpcPatInfoResponse>(_OnSaveSpcPatInfoResponse_QNAME, OnSaveSpcPatInfoResponse.class, null, value);
    }

}
