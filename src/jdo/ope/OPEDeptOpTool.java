package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 科常用手术Tool</p>
 *
 * <p>Description: 科常用手术Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-24
 * @version 4.0
 */
public class OPEDeptOpTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPEDeptOpTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static OPEDeptOpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEDeptOpTool();
        return instanceObject;
    }

    public OPEDeptOpTool() {
        this.setModuleName("ope\\OPEDeptOpModule.x");
        this.onInit();
    }
    /**
     * 查询数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = query("selectdata",parm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新数据
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(String DEPT_CODE,String OP_CODE){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",DEPT_CODE);
        parm.setData("OP_CODE",OP_CODE);
        TParm result = update("deletedata",parm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入介入护理平台记录数据
     * @param action
     * @return
     */
    public TParm insertInterData(TParm action){
    	TParm result=new TParm();
    	for(int i = 0; i < action.getCount(); i++){
    		//System.out.println("system:::"+action.getValue("SAVE_FLG",i));
    		if(action.getValue("SAVE_FLG",i).equals("Y")){
    			result = update("insertInterData",action.getRow(i));
    		}else{
    			result = update("updateInterData",action.getRow(i));
    		}
    		 // 判断错误值
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
    	}
    	return result;
    }
    /**
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInterData(TParm action){
        TParm result = update("deleteInterData",action);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 获取最大序号
     * @param caseNo
     * @return
     */
	public int getMaxSeqNo(String caseNo){
    	return (new TParm(TJDODBTool.getInstance().
    			select("SELECT MAX(CAST (SEQ_NO AS INT)) SEQ_NO FROM OPE_INTERVENNURPLAT WHERE CASE_NO = '"+caseNo+"'")).
    			getInt("SEQ_NO", 0));
    }
}
