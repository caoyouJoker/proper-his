
package action.spc.accountclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.spc.accountclient package. 
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

    private final static QName _OnSaveIndAccountResponse_QNAME = new QName("http://inf.ind.jdo/", "onSaveIndAccountResponse");
    private final static QName _OnSaveIndAccount_QNAME = new QName("http://inf.ind.jdo/", "onSaveIndAccount");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.spc.accountclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnSaveIndAccount }
     * 
     */
    public OnSaveIndAccount createOnSaveIndAccount() {
        return new OnSaveIndAccount();
    }

    /**
     * Create an instance of {@link OnSaveIndAccountResponse }
     * 
     */
    public OnSaveIndAccountResponse createOnSaveIndAccountResponse() {
        return new OnSaveIndAccountResponse();
    }

    /**
     * Create an instance of {@link IndCodeMap }
     * 
     */
    public IndCodeMap createIndCodeMap() {
        return new IndCodeMap();
    }

    /**
     * Create an instance of {@link IndAccounts }
     * 
     */
    public IndAccounts createIndAccounts() {
        return new IndAccounts();
    }

    /**
     * Create an instance of {@link IndAccount }
     * 
     */
    public IndAccount createIndAccount() {
        return new IndAccount();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveIndAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.ind.jdo/", name = "onSaveIndAccountResponse")
    public JAXBElement<OnSaveIndAccountResponse> createOnSaveIndAccountResponse(OnSaveIndAccountResponse value) {
        return new JAXBElement<OnSaveIndAccountResponse>(_OnSaveIndAccountResponse_QNAME, OnSaveIndAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnSaveIndAccount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://inf.ind.jdo/", name = "onSaveIndAccount")
    public JAXBElement<OnSaveIndAccount> createOnSaveIndAccount(OnSaveIndAccount value) {
        return new JAXBElement<OnSaveIndAccount>(_OnSaveIndAccount_QNAME, OnSaveIndAccount.class, null, value);
    }

}
