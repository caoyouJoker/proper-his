
package action.odi.spcUddClient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.odi.spcUddClient package. 
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

    private final static QName _GetOdiDspndInfo_QNAME = new QName("http://uddinfo.spc.jdo/", "getOdiDspndInfo");
    private final static QName _GetOdiDspndInfoResponse_QNAME = new QName("http://uddinfo.spc.jdo/", "getOdiDspndInfoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.odi.spcUddClient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetOdiDspndInfo }
     * 
     */
    public GetOdiDspndInfo createGetOdiDspndInfo() {
        return new GetOdiDspndInfo();
    }

    /**
     * Create an instance of {@link GetOdiDspndInfoResponse }
     * 
     */
    public GetOdiDspndInfoResponse createGetOdiDspndInfoResponse() {
        return new GetOdiDspndInfoResponse();
    }

    /**
     * Create an instance of {@link OdiDspndPkVo }
     * 
     */
    public OdiDspndPkVo createOdiDspndPkVo() {
        return new OdiDspndPkVo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOdiDspndInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://uddinfo.spc.jdo/", name = "getOdiDspndInfo")
    public JAXBElement<GetOdiDspndInfo> createGetOdiDspndInfo(GetOdiDspndInfo value) {
        return new JAXBElement<GetOdiDspndInfo>(_GetOdiDspndInfo_QNAME, GetOdiDspndInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOdiDspndInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://uddinfo.spc.jdo/", name = "getOdiDspndInfoResponse")
    public JAXBElement<GetOdiDspndInfoResponse> createGetOdiDspndInfoResponse(GetOdiDspndInfoResponse value) {
        return new JAXBElement<GetOdiDspndInfoResponse>(_GetOdiDspndInfoResponse_QNAME, GetOdiDspndInfoResponse.class, null, value);
    }

}
