package jdo.adm;

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
public class ADMTransLogTool  extends TJDOTool {
    public static ADMTransLogTool instanceObject;
    public static ADMTransLogTool getInstance (){
        if(instanceObject==null)
            instanceObject = new ADMTransLogTool();
        return instanceObject;
    }
    public ADMTransLogTool() {
    setModuleName("adm\\ADMTransLogModule.x");
    onInit();
    }
    /**
     * 查询
     * @param parm
     * @param conn
     * @return
     */
    public TParm selectData(TParm parm ){
    	TParm result = new TParm();
        result = query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
  
    /**
     * 查询出科科室最新
     * @param parm
     * @param conn
     * @return
     */
    public TParm getTranDeptData(TParm parm ){
    	TParm result = new TParm();
        result = query("getTranDeptData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    } 
    /**
     * 查询出科科室最旧
     * @param parm
     * @param conn
     * @return
     */
    public TParm getOldTranDeptData(TParm parm ){
    	TParm result = new TParm();
        result = query("getOldTranDeptData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    } 
    /**
     * 删除方法
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteData(TParm parm){
    	TParm result = new TParm();
        result =this.update("deleteData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
    /**
     * 删除方法
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteAdmData(TParm parm, TConnection conn){
    	TParm result = new TParm();
        result =this.update("deleteData",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
    /**
     * 修改方法
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateData(TParm parm){
    	TParm result = new TParm();
        result =this.update("updateData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
    /**
     * 修改方法
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateAdm(TParm parm, TConnection conn){
    	TParm result = new TParm();
        result =this.update("updateADM",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
    /**
     * 插入log
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDate (TParm parm , TConnection conn){
        TParm result = new TParm();
        result = update("insertAll",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
    /**
     * 病案首页转移记录
     * @param parm
     * @return
     */
    public TParm getTranHospFormro(TParm parm){
    	TParm result = new TParm();
        result = this.query("getTranHospFormro",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }
    
    /**
     * 取消转科时,插入log
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDateForCancel (TParm parm , TConnection conn){
        TParm result = new TParm();
        result = update("insertLogForCancel",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result ;
    }    
}
