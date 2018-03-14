
package jdo.pha.inf.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jdo.pha.inf.client package. 
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

    private final static QName _GetOpdOrder_QNAME = new QName("http://inf.pha.jdo/", "getOpdOrder");
    private final static QName _GetOpdOrderResponse_QNAME = new QName("http://inf.pha.jdo/", "getOpdOrderResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jdo.pha.inf.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetOpdOrderResponse }
     * 
     */
    public GetOpdOrderResponse createGetOpdOrderResponse() {
        return new GetOpdOrderResponse();
    }

    /**
     * Create an instance of {@link GetOpdOrder }
     * 
     */
    public GetOpdOrder createGetOpdOrder() {
        return new GetOpdOrder();
    }

    /**
     * Create an instance of {@link SpcOpdOrderDto }
     * 
     */
    public SpcOpdOrderDto createSpcOpdOrderDto() {
        return new SpcOpdOrderDto();
    }

    /**
     * Create an instance of {@link SpcSysPatinfoDto }
     * 
     */
    public SpcSysPatinfoDto createSpcSysPatinfoDto() {
        return new SpcSysPatinfoDto();
    }

    /**
     * Create an instance of {@link SpcOpdOrderDtos }
     * 
     */
    public SpcOpdOrderDtos createSpcOpdOrderDtos() {
        return new SpcOpdOrderDtos();
    }

    /**
     * Create an instance of {@link SpcOpdDiagrecDto }
     * 
     */
    public SpcOpdDiagrecDto createSpcOpdDiagrecDto() {
        return new SpcOpdDiagrecDto();
    }

    /**
     * Create an instance of {@link SpcOpdDrugAllergyDto }
     * 
     */
    public SpcOpdDrugAllergyDto createSpcOpdDrugAllergyDto() {
        return new SpcOpdDrugAllergyDto();
    }

    /**
     * Create an instance of {@link SpcRegPatadmDto }
     * 
     */
    public SpcRegPatadmDto createSpcRegPatadmDto() {
        return new SpcRegPatadmDto();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOpdOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.pha.jdo/", name = "getOpdOrder")
    public JAXBElement<GetOpdOrder> createGetOpdOrder(GetOpdOrder value) {
        return new JAXBElement<GetOpdOrder>(_GetOpdOrder_QNAME, GetOpdOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOpdOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.pha.jdo/", name = "getOpdOrderResponse")
    public JAXBElement<GetOpdOrderResponse> createGetOpdOrderResponse(GetOpdOrderResponse value) {
        return new JAXBElement<GetOpdOrderResponse>(_GetOpdOrderResponse_QNAME, GetOpdOrderResponse.class, null, value);
    }

}
