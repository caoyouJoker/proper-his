
package action.spc.phaSyncClient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.spc.phaSyncClient package. 
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

    private final static QName _UpdatePhaBaseResponse_QNAME = new QName("http://phaBaseSyncClient.spc.jdo/", "updatePhaBaseResponse");
    private final static QName _UpdatePhaBase_QNAME = new QName("http://phaBaseSyncClient.spc.jdo/", "updatePhaBase");
    private final static QName _UpdatePhaTransUnit_QNAME = new QName("http://phaBaseSyncClient.spc.jdo/", "updatePhaTransUnit");
    private final static QName _UpdatePhaTransUnitResponse_QNAME = new QName("http://phaBaseSyncClient.spc.jdo/", "updatePhaTransUnitResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.spc.phaSyncClient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdatePhaBase }
     * 
     */
    public UpdatePhaBase createUpdatePhaBase() {
        return new UpdatePhaBase();
    }

    /**
     * Create an instance of {@link UpdatePhaBaseResponse }
     * 
     */
    public UpdatePhaBaseResponse createUpdatePhaBaseResponse() {
        return new UpdatePhaBaseResponse();
    }

    /**
     * Create an instance of {@link UpdatePhaTransUnitResponse }
     * 
     */
    public UpdatePhaTransUnitResponse createUpdatePhaTransUnitResponse() {
        return new UpdatePhaTransUnitResponse();
    }

    /**
     * Create an instance of {@link UpdatePhaTransUnit }
     * 
     */
    public UpdatePhaTransUnit createUpdatePhaTransUnit() {
        return new UpdatePhaTransUnit();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePhaBaseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://phaBaseSyncClient.spc.jdo/", name = "updatePhaBaseResponse")
    public JAXBElement<UpdatePhaBaseResponse> createUpdatePhaBaseResponse(UpdatePhaBaseResponse value) {
        return new JAXBElement<UpdatePhaBaseResponse>(_UpdatePhaBaseResponse_QNAME, UpdatePhaBaseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePhaBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://phaBaseSyncClient.spc.jdo/", name = "updatePhaBase")
    public JAXBElement<UpdatePhaBase> createUpdatePhaBase(UpdatePhaBase value) {
        return new JAXBElement<UpdatePhaBase>(_UpdatePhaBase_QNAME, UpdatePhaBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePhaTransUnit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://phaBaseSyncClient.spc.jdo/", name = "updatePhaTransUnit")
    public JAXBElement<UpdatePhaTransUnit> createUpdatePhaTransUnit(UpdatePhaTransUnit value) {
        return new JAXBElement<UpdatePhaTransUnit>(_UpdatePhaTransUnit_QNAME, UpdatePhaTransUnit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePhaTransUnitResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://phaBaseSyncClient.spc.jdo/", name = "updatePhaTransUnitResponse")
    public JAXBElement<UpdatePhaTransUnitResponse> createUpdatePhaTransUnitResponse(UpdatePhaTransUnitResponse value) {
        return new JAXBElement<UpdatePhaTransUnitResponse>(_UpdatePhaTransUnitResponse_QNAME, UpdatePhaTransUnitResponse.class, null, value);
    }

}
