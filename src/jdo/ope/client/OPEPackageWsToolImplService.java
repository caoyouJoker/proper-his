package jdo.ope.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name="OPEPackageWsToolImplService", wsdlLocation="http://127.0.0.1:8080/webwz/services/opeService?wsdl", targetNamespace="http://ope.jdo/")
public class OPEPackageWsToolImplService extends Service
{
  static URL url = null;
  public static final URL WSDL_LOCATION = url;       

  public static final QName SERVICE = new QName("http://ope.jdo/", "OPEPackageWsToolImplService");
  public static final QName OPEPackageWsToolImplPort = new QName("http://ope.jdo/", "OPEPackageWsToolImplPort");

  static { URL url = null;
    try {
      url = new URL("http://127.0.0.1:8080/webwz/services/opeService?wsdl");
    } catch (MalformedURLException e) {
      Logger.getLogger(OPEPackageWsToolImplService.class.getName())
        .log(Level.INFO, 
        "Can not initialize the default wsdl from {0}", "http://127.0.0.1:8080/webwz/services/opeService?wsdl");
    }
  }

  public OPEPackageWsToolImplService(URL wsdlLocation)
  {
    super(wsdlLocation, SERVICE);
  }

  public OPEPackageWsToolImplService(URL wsdlLocation, QName serviceName) {
    super(wsdlLocation, serviceName);
  }

  public OPEPackageWsToolImplService() {
    super(WSDL_LOCATION, SERVICE);
  }

  public OPEPackageWsToolImplService(WebServiceFeature[] features)
  {
    super(WSDL_LOCATION, SERVICE);
  }

  public OPEPackageWsToolImplService(URL wsdlLocation, WebServiceFeature[] features)
  {
    super(wsdlLocation, SERVICE);
  }

  public OPEPackageWsToolImplService(URL wsdlLocation, QName serviceName, WebServiceFeature[] features)
  {
    super(wsdlLocation, serviceName);
  }

  @WebEndpoint(name="OPEPackageWsToolImplPort")
  public OPEPackageWsTool getOPEPackageWsToolImplPort()
  {
    return (OPEPackageWsTool)super.getPort(OPEPackageWsToolImplPort, OPEPackageWsTool.class);
  }

  @WebEndpoint(name="OPEPackageWsToolImplPort")
  public OPEPackageWsTool getOPEPackageWsToolImplPort(WebServiceFeature[] features)
  {
    //return (OPEPackageWsTool)super.getPort(OPEPackageWsToolImplPort, OPEPackageWsTool.class, features);
	  return null;
  }
}