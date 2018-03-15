
package action.device.client;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-04-26T11:43:39.611+08:00
 * Generated source version: 2.5.2
 * 
 */
public final class RfidControllerService_RfidControllerServiceImplPort_Client {

    private static final QName SERVICE_NAME = new QName("http://impl.ws.demo.sunray.com/", "RfidControllerServiceImplService");

    private RfidControllerService_RfidControllerServiceImplPort_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = RfidControllerServiceImplService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        RfidControllerServiceImplService ss = new RfidControllerServiceImplService(wsdlURL, SERVICE_NAME);
        RfidControllerService port = ss.getRfidControllerServiceImplPort();  
        
        {
        System.out.println("Invoking startScan...");
        java.lang.String _startScan_readerId = "";
        port.startScan(_startScan_readerId);


        }
        {
        System.out.println("Invoking disconnect...");
        java.lang.String _disconnect_readerId = "";
        port.disconnect(_disconnect_readerId);


        }
        {
        System.out.println("Invoking stopScan...");
        java.lang.String _stopScan_readerId = "";
        port.stopScan(_stopScan_readerId);


        }
        {
        System.out.println("Invoking connect...");
        java.lang.String _connect_readerId = "";
        port.connect(_connect_readerId);


        }

        System.exit(0);
    }

}