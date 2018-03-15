
package action.emr.client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

/**
 * This class was generated by Apache CXF 3.0.7
 * 2016-02-02T12:02:34.069+08:00
 * Generated source version: 3.0.7
 * 
 */
public final class ServiceSoap_ServiceSoap_Client {

    private static final QName SERVICE_NAME = new QName("http://172.20.10.152/signpdf/", "Service");

    private ServiceSoap_ServiceSoap_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = Service.WSDL_LOCATION;
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
      
        Service ss = new Service(wsdlURL, SERVICE_NAME);
        ServiceSoap port = ss.getServiceSoap();  
        
        {
        System.out.println("Invoking signPDF...");
        byte[] _signPDF_fs = new byte[0];
        int _signPDF_x = 0;
        int _signPDF_y = 0;
        int _signPDF_w = 0;
        int _signPDF_h = 0;
        int _signPDF_p = 0;
        java.lang.String _signPDF__return = port.signPDF(_signPDF_fs, _signPDF_x, _signPDF_y, _signPDF_w, _signPDF_h, _signPDF_p);
        System.out.println("signPDF.result=" + _signPDF__return);


        }

        System.exit(0);
    }
    
    /**
     * ���Ӳ�����ǩ����ǩ��
     * 
     * @param fs �ļ���
     * @param x x����
     * @param y y����
     * @param w ͼƬ����
     * @param h ͼƬ�߶�
     * @param p ��ǩҳ
     * @return filePath ��ǩ����ļ��洢·��
     */
	public static String signPDF(byte[] fs, int x, int y, int w, int h, int p) {
		String url = TConfig.getSystemValue("CA.WEB_SERVICE");
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(ServiceSoap.class);
		ServiceSoap service = (ServiceSoap) factory.create();
		String filePath = service.signPDF(fs, x, y, w, h, p);
		return filePath;
	}

}