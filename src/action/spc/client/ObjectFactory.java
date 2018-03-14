
package action.spc.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.spc.client package. 
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

    private final static QName _OnSaveDispense_QNAME = new QName("http://inf.ind.jdo/", "onSaveDispense");
    private final static QName _OnSaveDispenseResponse_QNAME = new QName("http://inf.ind.jdo/", "onSaveDispenseResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.spc.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnSaveDispense }
     * 
     */
    public OnSaveDispense createOnSaveDispense() {
        return new OnSaveDispense();
    }

    /**
     * Create an instance of {@link OnSaveDispenseResponse }
     * 
     */
    public OnSaveDispenseResponse createOnSaveDispenseResponse() {
        return new OnSaveDispenseResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveDispense }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.ind.jdo/", name = "onSaveDispense")
    public JAXBElement<OnSaveDispense> createOnSaveDispense(OnSaveDispense value) {
        return new JAXBElement<OnSaveDispense>(_OnSaveDispense_QNAME, OnSaveDispense.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveDispenseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.ind.jdo/", name = "onSaveDispenseResponse")
    public JAXBElement<OnSaveDispenseResponse> createOnSaveDispenseResponse(OnSaveDispenseResponse value) {
        return new JAXBElement<OnSaveDispenseResponse>(_OnSaveDispenseResponse_QNAME, OnSaveDispenseResponse.class, null, value);
    }

}
