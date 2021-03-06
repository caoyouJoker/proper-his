package jdo.device.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.11
 * 2017-02-03T10:23:13.683+08:00
 * Generated source version: 2.5.11
 * 
 */
@WebService(targetNamespace = "http://www.njnu.edu.cn/", name = "CamsCommServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface CamsCommServiceSoap {

    @WebResult(name = "SendMsgResult", targetNamespace = "http://www.njnu.edu.cn/")
    @RequestWrapper(localName = "SendMsg", targetNamespace = "http://www.njnu.edu.cn/", className = "com.SendMsg")
    @WebMethod(operationName = "SendMsg", action = "http://www.njnu.edu.cn/SendMsg")
    @ResponseWrapper(localName = "SendMsgResponse", targetNamespace = "http://www.njnu.edu.cn/", className = "com.SendMsgResponse")
    public int sendMsg(
        @WebParam(name = "host", targetNamespace = "http://www.njnu.edu.cn/")
        java.lang.String host,
        @WebParam(name = "port", targetNamespace = "http://www.njnu.edu.cn/")
        int port,
        @WebParam(name = "msgType", targetNamespace = "http://www.njnu.edu.cn/")
        short msgType,
        @WebParam(name = "msg", targetNamespace = "http://www.njnu.edu.cn/")
        java.lang.String msg
    );

    @WebResult(name = "HelloWorldResult", targetNamespace = "http://www.njnu.edu.cn/")
    @RequestWrapper(localName = "HelloWorld", targetNamespace = "http://www.njnu.edu.cn/", className = "com.HelloWorld")
    @WebMethod(operationName = "HelloWorld", action = "http://www.njnu.edu.cn/HelloWorld")
    @ResponseWrapper(localName = "HelloWorldResponse", targetNamespace = "http://www.njnu.edu.cn/", className = "com.HelloWorldResponse")
    public java.lang.String helloWorld();
    
    @WebResult(name = "GetCurrentCallCamsInfoResult", targetNamespace = "http://www.njnu.edu.cn/")
    @RequestWrapper(localName = "GetCurrentCallCamsInfo", targetNamespace = "http://www.njnu.edu.cn/", className = "com.GetCurrentCallCamsInfo")
    @WebMethod(operationName = "GetCurrentCallCamsInfo", action = "http://www.njnu.edu.cn/GetCurrentCallCamsInfo")
    @ResponseWrapper(localName = "GetCurrentCallCamsInfoResponse", targetNamespace = "http://www.njnu.edu.cn/", className = "com.GetCurrentCallCamsInfoResponse")
    public java.lang.String getCurrentCallCamsInfo(
        @WebParam(name = "devName", targetNamespace = "http://www.njnu.edu.cn/")
        java.lang.String devName
    );
}
