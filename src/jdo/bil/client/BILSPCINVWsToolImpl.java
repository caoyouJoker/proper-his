
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package jdo.bil.client;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-09-23T15:36:24.636+08:00
 * Generated source version: 2.5.2
 * 
 */

@javax.jws.WebService(
                      serviceName = "BILSPCINVWsToolImplService",
                      portName = "BILSPCINVWsToolImplPort",
                      targetNamespace = "http://bil.jdo/",
                      wsdlLocation = "http://127.0.0.1:8080/web1/services/bilService?wsdl",
                      endpointInterface = "jdo.bil.client.BILSPCINVWsTool")
                      
public class BILSPCINVWsToolImpl implements BILSPCINVWsTool {

    private static final Logger LOG = Logger.getLogger(BILSPCINVWsToolImpl.class.getName());

    /* (non-Javadoc)
     * @see jdo.bil.client.BILSPCINVWsTool#onFeeData(java.lang.String  arg0 )*
     */
    public java.lang.String onFeeData(java.lang.String arg0) { 
        LOG.info("Executing operation onFeeData");
        System.out.println(arg0);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see jdo.bil.client.BILSPCINVWsTool#onMrNo(java.lang.String  arg0 ,)java.lang.String  arg1 )*
     */
    public java.lang.String onMrNo(java.lang.String arg0,java.lang.String arg1) { 
        LOG.info("Executing operation onMrNo");
        System.out.println(arg0);
        System.out.println(arg1);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see jdo.bil.client.BILSPCINVWsTool#insertIBSOrder(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.lang.String insertIBSOrder(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) { 
        LOG.info("Executing operation insertIBSOrder");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see jdo.bil.client.BILSPCINVWsTool#insertOpdOrder(java.lang.String  arg0 )*
     */
    public java.lang.String insertOpdOrder(java.lang.String arg0) { 
        LOG.info("Executing operation insertOpdOrder");
        System.out.println(arg0);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

	@Override
	public boolean onCheckFeeState(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
