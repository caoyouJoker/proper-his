package action.inw.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-05-10T16:13:26.847+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebService(targetNamespace = "http://services.spc.action/", name = "SpcOdiService")
@XmlSeeAlso({ObjectFactory.class})
public interface SpcOdiService {
    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "inwCheck", targetNamespace = "http://services.spc.action/", className = "action.inw.client.InwCheck")
    @WebMethod
    @ResponseWrapper(localName = "inwCheckResponse", targetNamespace = "http://services.spc.action/", className = "action.inw.client.InwCheckResponse")
    public action.inw.client.SpcInwCheckDtos inwCheck(
        @WebParam(name = "arg0", targetNamespace = "")
        action.inw.client.SpcInwCheckDtos arg0
    );
    
    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onSaveIndCabdspn", targetNamespace = "http://services.spc.action/", className = "action.inw.client.OnSaveIndCabdspn")
    @WebMethod
    @ResponseWrapper(localName = "onSaveIndCabdspnResponse", targetNamespace = "http://services.spc.action/", className = "action.inw.client.OnSaveIndCabdspnResponse")
    public java.lang.String onSaveIndCabdspn(
        @WebParam(name = "arg0", targetNamespace = "")
        action.inw.client.SpcOdiDspnms arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1
    );
}