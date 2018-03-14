package jdo.ope.client;

import com.dongyang.config.TConfig;
import java.io.PrintStream;
import javax.xml.namespace.QName;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public final class OPEPackageWsTool_OPEPackageWsToolImplPort_Client
{
  public static final QName SERVICE_NAME = new QName("http://ope.jdo/", "OPEPackageWsToolImplService");

  private static TConfig getProp()
  {
    TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
    return config;
  }

  public static String getWebServicesIp() {
    TConfig config = getProp();
    String url = config.getString("", "WEB_SERVICES_IP");
    return url;
  }

  public String onSaveOpePackage(String opCode, String supTypeCode, String opDateS, String opDateE, String state, String optUser, String optTerm) {
    String ip = getWebServicesIp();
    System.out.println("ip:" + ip);
    String url = "http://" + ip + "/services/opeService";
    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
    factory.setAddress(url);
    factory.setServiceClass(OPEPackageWsTool.class);

    OPEPackageWsTool service = (OPEPackageWsTool)factory.create();
    System.out.println("client-opCode:" + opCode);
    System.out.println("client-supTypeCode:" + supTypeCode);
    System.out.println("client-opDateS:" + opDateS);
    System.out.println("client-opDateE:" + opDateE);
    System.out.println("client-state:" + state);
    System.out.println("client-optUser:" + optUser);
    System.out.println("client-optTerm:" + optTerm);
    String result = service.onSaveOpePackage(opCode, supTypeCode, opDateS, opDateE, state, optUser, optTerm);
    System.out.println("onSaveOpePackage---result:" + result);
    return result;
  }

  public static void main(String[] args)
    throws Exception
  {
  }
}