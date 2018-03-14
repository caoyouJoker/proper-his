
package action.udd.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.udd.client package. 
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
    private final static QName _OnUpdateRtnCfmResponse_QNAME = new QName("http://services.spc.action/", "onUpdateRtnCfmResponse");
    private final static QName _OnUpdateRtnCfm_QNAME = new QName("http://services.spc.action/", "onUpdateRtnCfm");
    private final static QName _OnCheckStockQty_QNAME = new QName("http://services.spc.action/", "onCheckStockQty");
    private final static QName _OnCheckStockQtyResponse_QNAME = new QName("http://services.spc.action/", "onCheckStockQtyResponse");
    private final static QName _Examine_QNAME = new QName("http://services.spc.action/", "examine");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.udd.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Examine }
     * 
     */
    public Examine createExamine() {
        return new Examine();
    }

    /**
     * Create an instance of {@link ExamineResponse }
     * 
     */
    public ExamineResponse createExamineResponse() {
        return new ExamineResponse();
    }

    /**
     * Create an instance of {@link OnUpdateRtnCfmResponse }
     * 
     */
    public OnUpdateRtnCfmResponse createOnUpdateRtnCfmResponse() {
        return new OnUpdateRtnCfmResponse();
    }

    /**
     * Create an instance of {@link OnUpdateRtnCfm }
     * 
     */
    public OnUpdateRtnCfm createOnUpdateRtnCfm() {
        return new OnUpdateRtnCfm();
    }

   

    /**
     * Create an instance of {@link SpcOdiDspnm }
     * 
     */
    public SpcOdiDspnm createSpcOdiDspnm() {
        return new SpcOdiDspnm();
    }

    /**
     * Create an instance of {@link OnCheckStockQty }
     * 
     */
    public OnCheckStockQty createOnCheckStockQty() {
        return new OnCheckStockQty();
    }
    /**
     * Create an instance of {@link OnCheckStockQtyResponse }
     * 
     */
    public OnCheckStockQtyResponse createOnCheckStockQtyResponse() {
        return new OnCheckStockQtyResponse();
    }

    /**
     * Create an instance of {@link SpcOdiDspnms }
     * 
     */
    public SpcOdiDspnms createSpcOdiDspnms() {
        return new SpcOdiDspnms();
    }

    /**
     * Create an instance of {@link SpcOdiDspnd }
     * 
     */
    public SpcOdiDspnd createSpcOdiDspnd() {
        return new SpcOdiDspnd();
    }

    /**
     * Create an instance of {@link SpcIndStock }
     * 
     */
    public SpcIndStock createSpcIndStock() {
        return new SpcIndStock();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExamineResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "examineResponse")
    public JAXBElement<ExamineResponse> createExamineResponse(ExamineResponse value) {
        return new JAXBElement<ExamineResponse>(_ExamineResponse_QNAME, ExamineResponse.class, null, value);
    }

   

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnUpdateRtnCfmResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "onUpdateRtnCfmResponse")
    public JAXBElement<OnUpdateRtnCfmResponse> createOnUpdateRtnCfmResponse(OnUpdateRtnCfmResponse value) {
        return new JAXBElement<OnUpdateRtnCfmResponse>(_OnUpdateRtnCfmResponse_QNAME, OnUpdateRtnCfmResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnUpdateRtnCfm }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "onUpdateRtnCfm")
    public JAXBElement<OnUpdateRtnCfm> createOnUpdateRtnCfm(OnUpdateRtnCfm value) {
        return new JAXBElement<OnUpdateRtnCfm>(_OnUpdateRtnCfm_QNAME, OnUpdateRtnCfm.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Examine }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "examine")
    public JAXBElement<Examine> createExamine(Examine value) {
        return new JAXBElement<Examine>(_Examine_QNAME, Examine.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnCheckStockQty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "onCheckStockQty")
    public JAXBElement<OnCheckStockQty> createOnCheckStockQty(OnCheckStockQty value) {
        return new JAXBElement<OnCheckStockQty>(_OnCheckStockQty_QNAME, OnCheckStockQty.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnCheckStockQtyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.spc.action/", name = "onCheckStockQtyResponse")
    public JAXBElement<OnCheckStockQtyResponse> createOnCheckStockQtyResponse(OnCheckStockQtyResponse value) {
        return new JAXBElement<OnCheckStockQtyResponse>(_OnCheckStockQtyResponse_QNAME, OnCheckStockQtyResponse.class, null, value);
    } 
}
