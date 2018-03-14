
package action.inw.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.inw.client package. 
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

    private final static QName _ExamineResponse_QNAME = new QName("http://services.spc.action/", "examineResponse");
    private final static QName _InwCheck_QNAME = new QName("http://services.spc.action/", "inwCheck");
    private final static QName _OnUpdateRtnCfmResponse_QNAME = new QName("http://services.spc.action/", "onUpdateRtnCfmResponse");
    private final static QName _InwCheckResponse_QNAME = new QName("http://services.spc.action/", "inwCheckResponse");
    private final static QName _SendElectronicTag_QNAME = new QName("http://services.spc.action/", "sendElectronicTag");
    private final static QName _OnUpdateRtnCfm_QNAME = new QName("http://services.spc.action/", "onUpdateRtnCfm");
    private final static QName _Examine_QNAME = new QName("http://services.spc.action/", "examine");
    private final static QName _SendElectronicTagResponse_QNAME = new QName("http://services.spc.action/", "sendElectronicTagResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.inw.client
     * 
     */
    public ObjectFactory() {
    }

   

    /**
     * Create an instance of {@link InwCheckResponse }
     * 
     */
    public InwCheckResponse createInwCheckResponse() {
        return new InwCheckResponse();
    }

   
    /**
     * Create an instance of {@link InwCheck }
     * 
     */
    public InwCheck createInwCheck() {
        return new InwCheck();
    }

   
   

    /**
     * Create an instance of {@link SpcInwCheckDtos }
     * 
     */
    public SpcInwCheckDtos createSpcInwCheckDtos() {
        return new SpcInwCheckDtos();
    }

   

    /**
     * Create an instance of {@link SpcInwCheckDto }
     * 
     */
    public SpcInwCheckDto createSpcInwCheckDto() {
        return new SpcInwCheckDto();
    }

   
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InwCheck }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "inwCheck")
    public JAXBElement<InwCheck> createInwCheck(InwCheck value) {
        return new JAXBElement<InwCheck>(_InwCheck_QNAME, InwCheck.class, null, value);
    }

   
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InwCheckResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "inwCheckResponse")
    public JAXBElement<InwCheckResponse> createInwCheckResponse(InwCheckResponse value) {
        return new JAXBElement<InwCheckResponse>(_InwCheckResponse_QNAME, InwCheckResponse.class, null, value);
    }
}
