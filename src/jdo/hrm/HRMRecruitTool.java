/**
 * 
 */
package jdo.hrm;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ��������ļ������</p>
*
* <p>Description: ��������ļ������</p>
*
* <p>Copyright: Copyright (c) 2016</p>
*
* <p>Company: bluecore</p>
*
* @author guangl 20160614
* @version 1.0
*/
public class HRMRecruitTool extends TJDOTool {
	/**
     * ʵ��
     */
    private static HRMRecruitTool instance;
    
    /**
     * �õ�ʵ��
     * @return HRMCompanyTool
     */
	public static HRMRecruitTool getInstance() {
		if (instance == null) {
			instance = new HRMRecruitTool();
		}
		return instance;
	}
	
	public TParm onSave(TParm parm,TConnection conn){
		TParm result = new TParm();
		Map inMap=(HashMap)parm.getData("IN_MAP");
		String[] sql=(String[])inMap.get("SQL");
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		for(String tempSql:sql){
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
				return result;
			}
		}
		return result;
		
	}

}
