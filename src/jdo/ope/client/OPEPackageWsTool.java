package jdo.ope.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;  
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace="http://ope.jdo/", name="OPEPackageWsTool")
@XmlSeeAlso({ObjectFactory.class})
public abstract interface OPEPackageWsTool
{
  @WebResult(name="return", targetNamespace="")
  @RequestWrapper(localName="onSaveOpePackage", targetNamespace="http://ope.jdo/", className="jdo.ope.client.OnSaveOpePackage")
  @WebMethod
  @ResponseWrapper(localName="onSaveOpePackageResponse", targetNamespace="http://ope.jdo/", className="jdo.ope.client.OnSaveOpePackageResponse")
  public abstract String onSaveOpePackage(@WebParam(name="arg0", targetNamespace="") String paramString1, @WebParam(name="arg1", targetNamespace="") String paramString2, @WebParam(name="arg2", targetNamespace="") String paramString3, @WebParam(name="arg3", targetNamespace="") String paramString4, @WebParam(name="arg4", targetNamespace="") String paramString5, @WebParam(name="arg5", targetNamespace="") String paramString6, @WebParam(name="arg6", targetNamespace="") String paramString7);
}