
package jdo.spc.bsm.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import com.dongyang.config.TConfig;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "Service", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://172.16.0.145:8090/Service.asmx?wsdl")
public class Service
    extends javax.xml.ws.Service
{

    private final static URL SERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(jdo.spc.bsm.client.Service.class.getName());

    static {
        URL url = null;
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        String com = config.getString("", "bsmHis.path");
        try {
            URL baseUrl;
            baseUrl = jdo.spc.bsm.client.Service.class.getResource(".");
            url = new URL(baseUrl, com);
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://172.16.0.145:8090/Service.asmx?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SERVICE_WSDL_LOCATION = url;
    }

    public Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Service() {
        super(SERVICE_WSDL_LOCATION, new QName("http://tempuri.org/", "Service"));
    }

    /**
     * 
     * @return
     *     returns ServiceSoap
     */
    @WebEndpoint(name = "ServiceSoap")
    public ServiceSoap getServiceSoap() {
        return super.getPort(new QName("http://tempuri.org/", "ServiceSoap"), ServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ServiceSoap
     */
    @WebEndpoint(name = "ServiceSoap")
    public ServiceSoap getServiceSoap(WebServiceFeature... features) {
        //return super.getPort(new QName("http://tempuri.org/", "ServiceSoap"), ServiceSoap.class, features);
    	return null;
    }

}