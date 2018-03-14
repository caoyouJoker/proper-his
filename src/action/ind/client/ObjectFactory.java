
package action.ind.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.javahis.ui.ind.client package. 
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

    private final static QName _OnSaveSpcRequest_QNAME = new QName("http://inf.spc.jdo/", "onSaveSpcRequest");
    private final static QName _OnSaveSpcRequestResponse_QNAME = new QName("http://inf.spc.jdo/", "onSaveSpcRequestResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.javahis.ui.ind.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnSaveSpcRequestResponse }
     * 
     */
    public OnSaveSpcRequestResponse createOnSaveSpcRequestResponse() {
        return new OnSaveSpcRequestResponse();
    }

    /**
     * Create an instance of {@link OnSaveSpcRequest }
     * 
     */
    public OnSaveSpcRequest createOnSaveSpcRequest() {
        return new OnSaveSpcRequest();
    }

    /**
     * Create an instance of {@link SpcIndRequestd }
     * 
     */
    public SpcIndRequestd createSpcIndRequestd() {
        return new SpcIndRequestd();
    }

    /**
     * Create an instance of {@link SpcIndRequestm }
     * 
     */
    public SpcIndRequestm createSpcIndRequestm() {
        return new SpcIndRequestm();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveSpcRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.spc.jdo/", name = "onSaveSpcRequest")
    public JAXBElement<OnSaveSpcRequest> createOnSaveSpcRequest(OnSaveSpcRequest value) {
        return new JAXBElement<OnSaveSpcRequest>(_OnSaveSpcRequest_QNAME, OnSaveSpcRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveSpcRequestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.spc.jdo/", name = "onSaveSpcRequestResponse")
    public JAXBElement<OnSaveSpcRequestResponse> createOnSaveSpcRequestResponse(OnSaveSpcRequestResponse value) {
        return new JAXBElement<OnSaveSpcRequestResponse>(_OnSaveSpcRequestResponse_QNAME, OnSaveSpcRequestResponse.class, null, value);
    }

    
    

}
