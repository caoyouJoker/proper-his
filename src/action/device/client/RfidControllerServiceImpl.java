
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package action.device.client;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-04-26T11:43:39.657+08:00
 * Generated source version: 2.5.2
 * 
 */

@javax.jws.WebService(
                      serviceName = "RfidControllerServiceImplService",
                      portName = "RfidControllerServiceImplPort",
                      targetNamespace = "http://impl.ws.demo.sunray.com/",
                      wsdlLocation = "http://192.168.5.142:8080/rfidcontroller/services/rfidcontrollerservice?wsdl",
                      endpointInterface = "action.device.client.RfidControllerService")
                      
public class RfidControllerServiceImpl implements RfidControllerService {

    private static final Logger LOG = Logger.getLogger(RfidControllerServiceImpl.class.getName());

    /* (non-Javadoc)
     * @see action.device.client.RfidControllerService#startScan(java.lang.String  readerId )*
     */
    public void startScan(java.lang.String readerId) { 
        LOG.info("Executing operation startScan");
        System.out.println(readerId);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see action.device.client.RfidControllerService#disconnect(java.lang.String  readerId )*
     */
    public void disconnect(java.lang.String readerId) { 
        LOG.info("Executing operation disconnect");
        System.out.println(readerId);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see action.device.client.RfidControllerService#stopScan(java.lang.String  readerId )*
     */
    public void stopScan(java.lang.String readerId) { 
        LOG.info("Executing operation stopScan");
        System.out.println(readerId);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see action.device.client.RfidControllerService#connect(java.lang.String  readerId )*
     */
    public void connect(java.lang.String readerId) { 
        LOG.info("Executing operation connect");
        System.out.println(readerId);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}