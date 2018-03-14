package action.ind.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2012-12-22T12:12:24.716+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "SpcServiceImplService", 
                  wsdlLocation = "http://192.168.0.124:8081/webgy/services/spcService?wsdl",
                  targetNamespace = "http://inf.spc.jdo/") 
public class SpcServiceImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://inf.spc.jdo/", "SpcServiceImplService");
    public final static QName SpcServiceImplPort = new QName("http://inf.spc.jdo/", "SpcServiceImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://192.168.0.124:8081/webgy/services/spcService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(SpcServiceImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://192.168.0.124:8081/webgy/services/spcService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public SpcServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SpcServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SpcServiceImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcServiceImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcServiceImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns SpcService
     */
    @WebEndpoint(name = "SpcServiceImplPort")
    public SpcService getSpcServiceImplPort() {
        return super.getPort(SpcServiceImplPort, SpcService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SpcService
     */
    @WebEndpoint(name = "SpcServiceImplPort")
    public SpcService getSpcServiceImplPort(WebServiceFeature... features) {
        //return super.getPort(SpcServiceImplPort, SpcService.class, features);
    	return null;
    }

}
