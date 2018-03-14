
package jdo.bil.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/** 
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.javahis.jdo.bil package. 
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

    private final static QName _OnSaveOpePackage_QNAME = new QName("http://ope.jdo/", "onSaveOpePackage");
    private final static QName _OnSaveOpePackageResponse_QNAME = new QName("http://ope.jdo/", "onSaveOpePackageResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.javahis.jdo.bil
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnSaveOpePackage }
     * 
     */
    public OnSaveOpePackage createOnSaveOpePackage() {
        return new OnSaveOpePackage();
    }

    /**
     * Create an instance of {@link OnSaveOpePackageResponse }
     * 
     */
    public OnSaveOpePackageResponse createOnSaveOpePackageResponse() {
        return new OnSaveOpePackageResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveOpePackage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ope.jdo/", name = "onSaveOpePackage")
    public JAXBElement<OnSaveOpePackage> createOnSaveOpePackage(OnSaveOpePackage value) {
        return new JAXBElement<OnSaveOpePackage>(_OnSaveOpePackage_QNAME, OnSaveOpePackage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveOpePackageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ope.jdo/", name = "onSaveOpePackageResponse")
    public JAXBElement<OnSaveOpePackageResponse> createOnSaveOpePackageResponse(OnSaveOpePackageResponse value) {
        return new JAXBElement<OnSaveOpePackageResponse>(_OnSaveOpePackageResponse_QNAME, OnSaveOpePackageResponse.class, null, value);
    }

}
