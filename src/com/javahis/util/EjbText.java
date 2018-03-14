package com.javahis.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class EjbText {

    public EjbText() {
    }
    public static void main(String[] args) throws NamingException {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("jndi.properties"));
            InitialContext ctx = new InitialContext(props);
//            TestEJBRemote testEJB = (TestEJBRemote) ctx.lookup("stateless.TestEJBRemote");
//            for(int i=0;i<2000;i++){
//                System.out.println(testEJB.getMessage());
//            }
        } catch (NamingException nex) {
            nex.printStackTrace();
        } catch (FileNotFoundException fnfex) {
            fnfex.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }

    }
}
