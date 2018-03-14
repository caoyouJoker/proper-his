package jdo.odo;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 *
 * <p>
 * Title: ҽ��վ����
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui 20091015
 * @version 1.0
 */
public class ODOSaveTool extends TJDOTool {
	/**
     * ʵ��
     */
    public static ODOSaveTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ODOSaveTool
     */
    public static ODOSaveTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new ODOSaveTool();
        }

        return instanceObject;
    }
    /**
     * ����
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm,TConnection conn){

		TParm result=new TParm();
		String[] sql=(String[])parm.getData("SQL");
		//System.out.println("ODOSaveTool.sql.length:"+sql.length);
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		for(String tempSql:sql){
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
				System.out.println("ODOSaveTool wrong sql:"+tempSql);
				return result;
			}
		}
		return result;

    }
}
