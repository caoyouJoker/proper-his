package action.spc.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2012-12-28T08:34:48.750+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebService(targetNamespace = "http://inf.ind.jdo/", name = "IndService")
@XmlSeeAlso({ObjectFactory.class})
public interface IndService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onSaveDispense", targetNamespace = "http://inf.ind.jdo/", className = "action.spc.client.OnSaveDispense")
    @WebMethod
    @ResponseWrapper(localName = "onSaveDispenseResponse", targetNamespace = "http://inf.ind.jdo/", className = "action.spc.client.OnSaveDispenseResponse")
    public java.lang.String onSaveDispense(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );
}