
package jdo.adm.client;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

/**
 * This class was generated by Apache CXF 3.0.7
 * 2018-01-17T16:39:36.179+08:00
 * Generated source version: 3.0.7
 * 
 */
public final class QYIToHISWebServiceSoapClient {

    private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "QYIToHISWebService");

	public static QYIToHISWebServiceSoapClient instance;

	public static QYIToHISWebServiceSoapClient getInstance() {
		if (instance == null) {
			instance = new QYIToHISWebServiceSoapClient();
		}
		return instance;
	}

	private static JaxWsProxyFactoryBean jaxFactory;

	private static JaxWsProxyFactoryBean getJaxFactory() {
		if (jaxFactory == null) {
			jaxFactory = new JaxWsProxyFactoryBean();
		}
		return jaxFactory;
	}
	
	/**
	 * 获得Q医住院接口webserviceIP地址
	 * 
	 * @return url Q医住院接口webserviceIP地址
	 */
	private String getWebServicesIp() {
		String url = TConfig.getSystemValue("QE_ADM_SERVICES_IP");
		return url;
	}
	
	/**
	 * 退Q医渠道的押金
	 * 
	 * @param inXML
	 * @return outXml
	 */
	public String backMoney(String inXML){
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = getJaxFactory();
		factory.setAddress("http://" + ip + "/WebService/QYIToHISWebService.asmx");
		factory.setServiceClass(QYIToHISWebServiceSoap.class);
		QYIToHISWebServiceSoap service = (QYIToHISWebServiceSoap) factory.create();
		String outXml = service.backMoney(inXML);
		return outXml;
	}
	
	/**
	 * 更新入院信息和出院信息
	 * 
	 * @param inXML
	 * @return outXml
	 */
	public String updateHospitalInfo(String inXML){
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = getJaxFactory();
		factory.setAddress("http://" + ip + "/WebService/QYIToHISWebService.asmx");
		factory.setServiceClass(QYIToHISWebServiceSoap.class);
		QYIToHISWebServiceSoap service = (QYIToHISWebServiceSoap) factory.create();
		String outXml = service.updateHospitalInfo(inXML);
		return outXml;
	}
	
	/**
	 * 验证退Q医渠道的押金是否成功
	 * 
	 * @param inXML
	 * @return outXml
	 */
	public String validateBackMoney(String inXML){
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = getJaxFactory();
		factory.setAddress("http://" + ip + "/WebService/QYIToHISWebService.asmx");
		factory.setServiceClass(QYIToHISWebServiceSoap.class);
		QYIToHISWebServiceSoap service = (QYIToHISWebServiceSoap) factory.create();
		String outXml = service.validateBackMoney(inXML);
		return outXml;
	}

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = QYIToHISWebService.WSDL_LOCATION;
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
      
        QYIToHISWebService ss = new QYIToHISWebService(wsdlURL, SERVICE_NAME);
        QYIToHISWebServiceSoap port = ss.getQYIToHISWebServiceSoap();  
        
        {
        System.out.println("Invoking validateBackMoney...");
        java.lang.String _validateBackMoney_inXML = "";
        java.lang.String _validateBackMoney__return = port.validateBackMoney(_validateBackMoney_inXML);
        System.out.println("validateBackMoney.result=" + _validateBackMoney__return);


        }
        {
        System.out.println("Invoking backMoney...");
        java.lang.String _backMoney_inXML = "";
        java.lang.String _backMoney__return = port.backMoney(_backMoney_inXML);
        System.out.println("backMoney.result=" + _backMoney__return);


        }
        {
        System.out.println("Invoking updateHospitalInfo...");
        java.lang.String _updateHospitalInfo_inXML = "";
        java.lang.String _updateHospitalInfo__return = port.updateHospitalInfo(_updateHospitalInfo_inXML);
        System.out.println("updateHospitalInfo.result=" + _updateHospitalInfo__return);


        }

        System.exit(0);
    }

}
