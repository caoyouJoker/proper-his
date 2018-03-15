package jdo.spc.bsm;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

import com.dongyang.config.TConfig;

import jdo.sys.SystemTool;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-05-21T08:54:31.515+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "ConsisService", 
                  wsdlLocation = "http://192.168.8.140/webservice/ServiceConsis.asmx?WSDL",
                  targetNamespace = "http://www.willach.com/") 
public class ConsisService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.willach.com/", "ConsisService");
    public final static QName ConsisServiceSoap12 = new QName("http://www.willach.com/", "ConsisServiceSoap12");
    public final static QName ConsisServiceSoap = new QName("http://www.willach.com/", "ConsisServiceSoap");
    static {
        URL url = null;
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        String com = config.getString("", "bsm.path");
        try {
            url = new URL(com);
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ConsisService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", com);
        }
        WSDL_LOCATION = url;
    }

    public ConsisService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ConsisService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ConsisService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ConsisService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ConsisService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ConsisService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns ConsisServiceSoap
     */
    @WebEndpoint(name = "ConsisServiceSoap12")
    public ConsisServiceSoap getConsisServiceSoap12() {
        return super.getPort(ConsisServiceSoap12, ConsisServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ConsisServiceSoap
     */
    @WebEndpoint(name = "ConsisServiceSoap12")
    public ConsisServiceSoap getConsisServiceSoap12(WebServiceFeature... features) {
        //return super.getPort(ConsisServiceSoap12, ConsisServiceSoap.class, features);
    	return null;
    }
    /**
     *
     * @return
     *     returns ConsisServiceSoap
     */
    @WebEndpoint(name = "ConsisServiceSoap")
    public ConsisServiceSoap getConsisServiceSoap() {
        return super.getPort(ConsisServiceSoap, ConsisServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ConsisServiceSoap
     */
    @WebEndpoint(name = "ConsisServiceSoap")
    public ConsisServiceSoap getConsisServiceSoap(WebServiceFeature... features) {
        //return super.getPort(ConsisServiceSoap, ConsisServiceSoap.class, features);
    	return null;
    }

}