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
 * 2012-12-25T16:37:57.187+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebService(targetNamespace = "http://ind.jdo/", name = "IIndService")
@XmlSeeAlso({ObjectFactory.class})
public interface IIndService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onSaveDispenseDM", targetNamespace = "http://ind.jdo/", className = "action.spc.client.OnSaveDispenseDM")
    @WebMethod
    @ResponseWrapper(localName = "onSaveDispenseDMResponse", targetNamespace = "http://ind.jdo/", className = "action.spc.client.OnSaveDispenseDMResponse")
    public java.lang.String onSaveDispenseDM(
        @WebParam(name = "arg0", targetNamespace = "")
        action.spc.client.IndDispensem arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onStop", targetNamespace = "http://ind.jdo/", className = "action.spc.client.OnStop")
    @WebMethod
    @ResponseWrapper(localName = "onStopResponse", targetNamespace = "http://ind.jdo/", className = "action.spc.client.OnStopResponse")
    public java.lang.String onStop(
        @WebParam(name = "arg0", targetNamespace = "")
        action.spc.client.IndDispensem arg0
    );
}
