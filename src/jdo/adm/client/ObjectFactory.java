
package jdo.adm.client;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jdo.adm.client package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jdo.adm.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BackMoney }
     * 
     */
    public BackMoney createBackMoney() {
        return new BackMoney();
    }

    /**
     * Create an instance of {@link BackMoneyResponse }
     * 
     */
    public BackMoneyResponse createBackMoneyResponse() {
        return new BackMoneyResponse();
    }

    /**
     * Create an instance of {@link ValidateBackMoney }
     * 
     */
    public ValidateBackMoney createValidateBackMoney() {
        return new ValidateBackMoney();
    }

    /**
     * Create an instance of {@link ValidateBackMoneyResponse }
     * 
     */
    public ValidateBackMoneyResponse createValidateBackMoneyResponse() {
        return new ValidateBackMoneyResponse();
    }

    /**
     * Create an instance of {@link UpdateHospitalInfo }
     * 
     */
    public UpdateHospitalInfo createUpdateHospitalInfo() {
        return new UpdateHospitalInfo();
    }

    /**
     * Create an instance of {@link UpdateHospitalInfoResponse }
     * 
     */
    public UpdateHospitalInfoResponse createUpdateHospitalInfoResponse() {
        return new UpdateHospitalInfoResponse();
    }

}
