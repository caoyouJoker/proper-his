package jdo.aci;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;

/**
*
* <p>Title: 短信套餐细表对象 </p>
*
* <p>Description: 短信套餐细表对象 </p>
*
* <p>Copyright: Copyright (c) 2013 </p>
*
* <p>Company: BlueCore </p>
*
* @author wanglong 2013.11.01
* @version 1.0
*/
public class ACISMSPackageD extends TDataStore{

    //取得套餐名
    private static final String GET_PACKAGE_DESC=" SELECT PACKAGE_DESC FROM ACI_SMSPACKAGEM WHERE PACKAGE_CODE='#' ";
    
	/**
	 * 得到其他列数据
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
        String packCode = parm.getValue("PACKAGE_CODE", row);
        if ("PACKAGE_DESC".equalsIgnoreCase(column)) {
            TParm result = new TParm(TJDODBTool.getInstance().select(GET_PACKAGE_DESC.replace("#", packCode)));
            return result.getValue("PACKAGE_DESC", 0);
        }
        return "";
    }
	

}
