
package action.spc.opdOrderClient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.spc.opdOrderClient package. 
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

    private final static QName _UpdateOpdOrderResponse_QNAME = new QName("http://spc.jdo/", "updateOpdOrderResponse");
    private final static QName _UpdateOpdOrder_QNAME = new QName("http://spc.jdo/", "updateOpdOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.spc.opdOrderClient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateOpdOrderResponse }
     * 
     */
    public UpdateOpdOrderResponse createUpdateOpdOrderResponse() {
        return new UpdateOpdOrderResponse();
    }

    /**
     * Create an instance of {@link UpdateOpdOrder }
     * 
     */
    public UpdateOpdOrder createUpdateOpdOrder() {
        return new UpdateOpdOrder();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOpdOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spc.jdo/", name = "updateOpdOrderResponse")
    public JAXBElement<UpdateOpdOrderResponse> createUpdateOpdOrderResponse(UpdateOpdOrderResponse value) {
        return new JAXBElement<UpdateOpdOrderResponse>(_UpdateOpdOrderResponse_QNAME, UpdateOpdOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOpdOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spc.jdo/", name = "updateOpdOrder")
    public JAXBElement<UpdateOpdOrder> createUpdateOpdOrder(UpdateOpdOrder value) {
        return new JAXBElement<UpdateOpdOrder>(_UpdateOpdOrder_QNAME, UpdateOpdOrder.class, null, value);
    }

}
