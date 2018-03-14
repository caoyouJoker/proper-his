package jdo.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
//import stateless.TestEJBRemote;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EjbInterface {
    private static final String EJB_JNDI_NAME="stateless.TestEJBRemote";
    private static final String BUNDLE_CONFIG_CLASSPATH_PROPS = "jndi.properties";
//    private static TestEJBRemote testEJB = getEjbReference();

    private static final Properties getConfigurationProps() {

        Properties props = new Properties();
        InputStream is = EjbInterface.class.getResourceAsStream(BUNDLE_CONFIG_CLASSPATH_PROPS);
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return props;
        }

    }
//
//    public static TestEJBRemote getEjbReference() {
//
//        TestEJBRemote ll = null;
//        try {
//            Properties props = getConfigurationProps();
//            InitialContext ctx = new InitialContext(props);
//            ll = (TestEJBRemote) ctx.lookup(EJB_JNDI_NAME);
//            System.out.println(ll.toString());
//        } catch (NamingException nex) {
//            nex.printStackTrace();
//        } finally {
//
//            return ll;
//        }
//
//
//    }
//    public static void main(String[] args) {
//       System.out.println(testEJB.getMessage());
//    }
}
