
package jdo.opd.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jdo.opd.client package. 
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

    private final static QName _SaveOpdOrderResponse_QNAME = new QName("http://ws.opd.jdo/", "saveOpdOrderResponse");
    private final static QName _SaveOpdOrder_QNAME = new QName("http://ws.opd.jdo/", "saveOpdOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jdo.opd.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SaveOpdOrderResponse }
     * 
     */
    public SaveOpdOrderResponse createSaveOpdOrderResponse() {
        return new SaveOpdOrderResponse();
    }

    /**
     * Create an instance of {@link SaveOpdOrder }
     * 
     */
    public SaveOpdOrder createSaveOpdOrder() {
        return new SaveOpdOrder();
    }

    /**
     * Create an instance of {@link OpdOrderList }
     * 
     */
    public OpdOrderList createOpdOrderList() {
        return new OpdOrderList();
    }

    /**
     * Create an instance of {@link ArrayList }
     * 
     */
    public ArrayList createArrayList() {
        return new ArrayList();
    }

    /**
     * Create an instance of {@link OpdOrder }
     * 
     */
    public OpdOrder createOpdOrder() {
        return new OpdOrder();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveOpdOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.opd.jdo/", name = "saveOpdOrderResponse")
    public JAXBElement<SaveOpdOrderResponse> createSaveOpdOrderResponse(SaveOpdOrderResponse value) {
        return new JAXBElement<SaveOpdOrderResponse>(_SaveOpdOrderResponse_QNAME, SaveOpdOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveOpdOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.opd.jdo/", name = "saveOpdOrder")
    public JAXBElement<SaveOpdOrder> createSaveOpdOrder(SaveOpdOrder value) {
        return new JAXBElement<SaveOpdOrder>(_SaveOpdOrder_QNAME, SaveOpdOrder.class, null, value);
    }

}
