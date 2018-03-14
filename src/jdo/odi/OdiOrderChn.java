package jdo.odi;

import com.dongyang.jdo.*;
import java.util.Map;
import java.sql.Timestamp;
import jdo.sys.Operator;
import java.util.HashMap;
import com.dongyang.data.TParm;
import java.util.Vector;
import jdo.sys.SystemTool;

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
public class OdiOrderChn extends TDataStore {
    //对象初始化SQL
    private final static String SQL="SELECT * FROM ODI_ORDER ";
    /**
     * 查询
     * @param sql String
     * @return boolean true:查询成功,false:查询失败
     */
    public boolean onQuery(){
        return false;
    }
    /**
     * 取得初始化SQL
     * @param caseNo
     * @return
     */
    public String getSql(){
           return "";
    }
}
