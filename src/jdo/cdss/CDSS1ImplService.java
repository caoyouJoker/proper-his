package jdo.cdss;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.8
 * 2015-08-14T13:17:09.026+08:00
 * Generated source version: 2.7.8
 * 
 */
@WebServiceClient(name = "CDSS1ImplService", 
                  wsdlLocation = "http://172.20.10.82:8080/drools/services/cdss1?wsdl",
                  targetNamespace = "http://service.zhangp.com/") 
public class CDSS1ImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://service.zhangp.com/", "CDSS1ImplService");
    public final static QName CDSS1ImplPort = new QName("http://service.zhangp.com/", "CDSS1ImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://172.20.10.82:8080/drools/services/cdss1?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(CDSS1ImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://172.20.10.82:8080/drools/services/cdss1?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public CDSS1ImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public CDSS1ImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CDSS1ImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public CDSS1ImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public CDSS1ImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public CDSS1ImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns ICDSSService1
     */
    @WebEndpoint(name = "CDSS1ImplPort")
    public ICDSSService1 getCDSS1ImplPort() {
        return super.getPort(CDSS1ImplPort, ICDSSService1.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ICDSSService1
     */
    @WebEndpoint(name = "CDSS1ImplPort")
    public ICDSSService1 getCDSS1ImplPort(WebServiceFeature... features) {
        //return super.getPort(CDSS1ImplPort, ICDSSService1.class, features);
    	return null;
    }

}