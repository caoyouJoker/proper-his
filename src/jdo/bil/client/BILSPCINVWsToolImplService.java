package jdo.bil.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-09-23T15:36:24.659+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "BILSPCINVWsToolImplService", 
                  wsdlLocation = "http://127.0.0.1:8080/web1/services/bilService?wsdl",
                  targetNamespace = "http://bil.jdo/") 
public class BILSPCINVWsToolImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://bil.jdo/", "BILSPCINVWsToolImplService");
    public final static QName BILSPCINVWsToolImplPort = new QName("http://bil.jdo/", "BILSPCINVWsToolImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/web1/services/bilService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(BILSPCINVWsToolImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://127.0.0.1:8080/web1/services/bilService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public BILSPCINVWsToolImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public BILSPCINVWsToolImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BILSPCINVWsToolImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public BILSPCINVWsToolImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public BILSPCINVWsToolImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public BILSPCINVWsToolImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns BILSPCINVWsTool
     */
    @WebEndpoint(name = "BILSPCINVWsToolImplPort")
    public BILSPCINVWsTool getBILSPCINVWsToolImplPort() {
        return super.getPort(BILSPCINVWsToolImplPort, BILSPCINVWsTool.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BILSPCINVWsTool
     */
    @WebEndpoint(name = "BILSPCINVWsToolImplPort")
    public BILSPCINVWsTool getBILSPCINVWsToolImplPort(WebServiceFeature... features) {
        //return super.getPort(BILSPCINVWsToolImplPort, BILSPCINVWsTool.class, features);
    	return null;
    }

}
