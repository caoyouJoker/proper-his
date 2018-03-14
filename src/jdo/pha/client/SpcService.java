package jdo.pha.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2012-12-28T14:02:35.156+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebService(targetNamespace = "http://inf.spc.jdo/", name = "SpcService")
@XmlSeeAlso({ObjectFactory.class})
public interface SpcService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onSaveSpcRequest", targetNamespace = "http://inf.spc.jdo/", className = "jdo.pha.client.OnSaveSpcRequest")
    @WebMethod
    @ResponseWrapper(localName = "onSaveSpcRequestResponse", targetNamespace = "http://inf.spc.jdo/", className = "jdo.pha.client.OnSaveSpcRequestResponse")
    public java.lang.String onSaveSpcRequest(
        @WebParam(name = "arg0", targetNamespace = "")
        jdo.pha.client.SpcIndRequestm arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getPhaStateReturn", targetNamespace = "http://inf.spc.jdo/", className = "jdo.pha.client.GetPhaStateReturn")
    @WebMethod
    @ResponseWrapper(localName = "getPhaStateReturnResponse", targetNamespace = "http://inf.spc.jdo/", className = "jdo.pha.client.GetPhaStateReturnResponse")
    public jdo.pha.client.SpcOpdOrderReturnDto getPhaStateReturn(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        java.lang.String arg2
    );
}
