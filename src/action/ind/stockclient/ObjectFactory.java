
package action.ind.stockclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.spc.stockclient package. 
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

    private final static QName _OnSearchIndStockResponse_QNAME = new QName("http://stockinf.spc.jdo/", "onSearchIndStockResponse");
    private final static QName _OnSearchIndStock_QNAME = new QName("http://stockinf.spc.jdo/", "onSearchIndStock");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.spc.stockclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnSearchIndStockResponse }
     * 
     */
    public OnSearchIndStockResponse createOnSearchIndStockResponse() {
        return new OnSearchIndStockResponse();
    }

    /**
     * Create an instance of {@link OnSearchIndStock }
     * 
     */
    public OnSearchIndStock createOnSearchIndStock() {
        return new OnSearchIndStock();
    }

    /**
     * Create an instance of {@link IndStock }
     * 
     */
    public IndStock createIndStock() {
        return new IndStock();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSearchIndStockResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://stockinf.spc.jdo/", name = "onSearchIndStockResponse")
    public JAXBElement<OnSearchIndStockResponse> createOnSearchIndStockResponse(OnSearchIndStockResponse value) {
        return new JAXBElement<OnSearchIndStockResponse>(_OnSearchIndStockResponse_QNAME, OnSearchIndStockResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSearchIndStock }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://stockinf.spc.jdo/", name = "onSearchIndStock")
    public JAXBElement<OnSearchIndStock> createOnSearchIndStock(OnSearchIndStock value) {
        return new JAXBElement<OnSearchIndStock>(_OnSearchIndStock_QNAME, OnSearchIndStock.class, null, value);
    }

}
