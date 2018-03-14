package com.javahis.ui.clp;

import com.javahis.util.JavaHisDebug;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

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
public class Test {
    public Test() {
    }
    public static void main(String[] args) {
        JavaHisDebug.initServer();
        TJDODBTool tool = TJDODBTool.getInstance();
        TParm parm = new TParm(tool.select("select * from sys_dept"));
        System.out.println(parm);
    }
}
