
package action.device.client;

import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-04-26T11:43:39.672+08:00
 * Generated source version: 2.5.2
 * 
 */
 
public class RfidControllerService_RfidControllerServiceImplPort_Server{

    protected RfidControllerService_RfidControllerServiceImplPort_Server() throws java.lang.Exception {
        System.out.println("Starting Server");
        Object implementor = new RfidControllerServiceImpl();
        String address = "http://192.168.5.142:8080/rfidcontroller/services/rfidcontrollerservice";
        Endpoint.publish(address, implementor);
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new RfidControllerService_RfidControllerServiceImplPort_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
