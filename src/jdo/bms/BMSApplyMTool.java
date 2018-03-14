package jdo.bms;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
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
public class BMSApplyMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BMSApplyMTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static BMSApplyMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSApplyMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSApplyMTool() {
        setModuleName("bms\\BMSApplyMModule.x");
        onInit();
    }

    /**
     * 备血单新增
     *
     * @param parm
     * @return
     */
    public TParm onApplyInsert(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyInsert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 备血单更新
     *
     * @param parm
     * @return
     */
    public TParm onApplyUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdate", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新备血单信息(病患检验)
     *
     * @param parm
     * @return
     */
    public TParm onUpdatePatCheckInfo(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdatePatCheckInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除备血单细项
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onApplyDelete(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyDelete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onApplyQuery(TParm parm) {
        TParm result = this.query("ApplyQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 备血申请单查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onApplyNoQuery(TParm parm) {
        TParm result = this.query("ApplyNoQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
  
	/**
     * 根据标本血管号application_code校验采集时间与当前时间相比是否满足小于72小时才能出库的要求
     * true为允许出库，false为不允许出库
     * @param application_code String
     * @return boolean
     */
    public boolean check72Hour(String application_code){
    	boolean b = false;
    	
    	String sql = "";
    	sql += " SELECT " 
    		+ " O.NS_EXEC_DATE_REAL AS DATE1 " 
    		+ " FROM ODI_DSPND O , ODI_ORDER B " 
    		+ " WHERE "
    		+ " B.ORDER_SEQ = O.ORDER_SEQ " 
    		+ " AND B.ORDER_NO = O.ORDER_NO "
    		+ " AND B.CASE_NO = O.CASE_NO "
    		+ " AND B.SETMAIN_FLG = 'Y' "
    		+ " AND B.MED_APPLY_NO = '"+application_code+"'";
    	System.out.println(sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	if(result.getCount() <= 0 || result.getData("DATE1", 0) == null){
    		return false;
    	}
    	
    	Timestamp execDate = (Timestamp)result.getData("DATE1", 0);
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	
    	Long l = (now.getTime() - execDate.getTime())/1000/60/60;
    	if(l < 72){
    		b = true;
    	}
    	
    	return b;
    }

}
