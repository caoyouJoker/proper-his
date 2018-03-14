package jdo.hrm;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 健康检查个人报到保存</p>
 * 
 * <p>Description: 健康检查个人报到保存</p>
 * 
 * <p>Copyright: javahis 20090922</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMPersonReportTool extends TJDOTool {
	/**
     * 实例
     */
    public static HRMPersonReportTool instanceObject;
    /**
     * 得到实例
     * @return HRMPersonReportTool
     */
    public static HRMPersonReportTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new HRMPersonReportTool();
        }
            
        return instanceObject;
    }
    /**
     * 保存
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm,TConnection conn){

		TParm result=new TParm();
		Map inMap=(HashMap)parm.getData("IN_MAP");
		String[] sql=(String[])inMap.get("SQL");
		
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		// System.out.println("in onSave");
		for(String tempSql:sql){
			//// System.out.println("sql=="+tempSql);
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
				// System.out.println("wrong sql=="+tempSql);	
				return result;
			}
		}
		return result;
	
    }
    public TParm onDischarge(TParm parm,TConnection conn){

		TParm result=new TParm();
		Map inMap=(HashMap)parm.getData("IN_MAP");
		String[] sql=(String[])inMap.get("SQL");
		
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		for(String tempSql:sql){
			//// System.out.println("sql=="+tempSql);
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
				// System.out.println("wrong sql=="+tempSql);	
				return result;
			}
		}
		return result;
	
    }

}
