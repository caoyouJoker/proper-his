package jdo.cdss;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.8
 * 2015-08-14T13:38:48.934+08:00
 * Generated source version: 2.7.8
 * 
 */
@WebService(targetNamespace = "http://service.zhangp.com/", name = "ICDSSService3")
@XmlSeeAlso({ObjectFactory.class})
public interface ICDSSService3 {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "fireRule3", targetNamespace = "http://service.zhangp.com/", className = "jdo.cdss.FireRule3")
    @WebMethod
    @ResponseWrapper(localName = "fireRule3Response", targetNamespace = "http://service.zhangp.com/", className = "jdo.cdss.FireRule3Response")
    public jdo.cdss.HisPojo fireRule3(
        @WebParam(name = "arg0", targetNamespace = "")
        jdo.cdss.HisPojo arg0
    );
}
