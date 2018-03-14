package jdo.sta;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;

/**
*
* <p>Title: ҽ���ձ������ײ�ϸ����� </p>
*
* <p>Description: ҽ���ձ������ײ�ϸ����� </p>
*
* <p>Copyright: Copyright (c) 2014 </p>
*
* <p>Company: BlueCore </p>
*
* @author wangbin 2014.07.15
* @version 1.0
*/
public class STASMSPackageD extends TDataStore{

    //ȡ���ײ���
    private static final String GET_PACKAGE_DESC=" SELECT PACKAGE_DESC FROM STA_SMSPACKAGEM WHERE PACKAGE_CODE='#' ";
    
	/**
	 * �õ�����������
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
