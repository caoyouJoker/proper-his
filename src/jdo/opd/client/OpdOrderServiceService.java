package jdo.opd.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-05-22T18:37:00.737+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "OpdOrderServiceService", 
                  wsdlLocation = "http://127.0.0.1:8080/web/services/opdService?wsdl",
                  targetNamespace = "http://ws.opd.jdo/") 
public class OpdOrderServiceService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://ws.opd.jdo/", "OpdOrderServiceService");
    public final static QName OpdOrderServicePort = new QName("http://ws.opd.jdo/", "OpdOrderServicePort");
    static {
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/web/services/opdService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(OpdOrderServiceService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://127.0.0.1:8080/web/services/opdService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public OpdOrderServiceService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public OpdOrderServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public OpdOrderServiceService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public OpdOrderServiceService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public OpdOrderServiceService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public OpdOrderServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns IOpdOrderService
     */
    @WebEndpoint(name = "OpdOrderServicePort")
    public IOpdOrderService getOpdOrderServicePort() {
        return super.getPort(OpdOrderServicePort, IOpdOrderService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IOpdOrderService
     */
    @WebEndpoint(name = "OpdOrderServicePort")
    public IOpdOrderService getOpdOrderServicePort(WebServiceFeature... features) {
        //return super.getPort(OpdOrderServicePort, IOpdOrderService.class, features);
    	return null;
    }

}
