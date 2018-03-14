package jdo.opd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
*
* <p>Title: 医嘱tool
*
* <p>Description: 医嘱tool</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20080921
* @version 1.0
*/
public class OPDSubjrecTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPDSubjrecTool instanceObject;
    /**
     * 得到实例
     * @return OPDSubjrecTool
     */
    public static OPDSubjrecTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new OPDSubjrecTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public OPDSubjrecTool() {
        setModuleName("opd\\OPDSubjrecModule.x");
        onInit();
    }
    /**
     * 保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSave(TParm parm, TConnection connection)
    {
        TParm result = new TParm();
        String saveType = parm.getValue("SAVE_TYPE");
        //System.out.println("saveType=" + saveType);
        if("NO".equalsIgnoreCase(saveType)){
        	//System.out.println("no============");
        	return result;
        }
            
        if("INSERT".equalsIgnoreCase(saveType)){
        	//System.out.println("insert============");
        	return insert(parm,connection);
        }
            
        if("UPDATE".equalsIgnoreCase(saveType)){
        	//System.out.println("update=============");
            return update(parm,connection);}
        return result;
    }
    /**
     * 查询方法
     * @param caseNo String 问诊号
     * @return TParm
     */
    public TParm query(String caseNo,String admType)
    {
        TParm parm = new TParm();
        parm.setData("CASE_NO",caseNo);
        parm.setData("ADM_TYPE",admType);
        TParm result=query("selectdata",parm);
        
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            
            return result;
        }
        //System.out.println("subjrecresult"+result);
        return result;
        
    }
    /**
     * 新增方法
     * @param parm TParm
     * @return TParm
     */
    public TParm insert(TParm parm) {
        TParm result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 新增方法(后台使用)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insert(TParm parm, TConnection connection) {
    	//System.out.println("in opdsubjrectool "+parm);
        TParm result = update("insertdata", parm, connection);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 保存方法
     * @param parm TParm
     * @return TParm
     */
    public TParm update(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 保存方法(后台使用)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm update(TParm parm, TConnection connection) {
        TParm result = update("updatedata", parm, connection);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
