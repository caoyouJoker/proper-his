package jdo.adm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:新生儿注册 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw
 * @version 4.5
 */
public class ADMNewBodyRegisterTool extends TJDOTool {
	
	 /**
     * 实例
     */
    public static ADMNewBodyRegisterTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMNewBodyRegisterTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMNewBodyRegisterTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMNewBodyRegisterTool() {
        setModuleName("adm\\ADMNewBodyRegisterModule.x");
        onInit();
    }
    
    /**
     * 注册新生儿-插入sys_patinfo表
     */
    public TParm insertSysPatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertSysPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询母亲住院诊断
     */
    public TParm queryAdmDiag(TParm parm) {
        TParm result = new TParm();
        result = query("queryDiagForMro", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入住院诊断
     */
    public TParm insertAdmDiag(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertDiag", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询母亲病案信息
     */
    public TParm queryMroRecrd(TParm parm) {
    	TParm result = new TParm();
        result = query("selectInHospInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入病案信息
     */
    public TParm insertMroRecrd(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询母亲病案诊断
     */
    public TParm queryMroRecordDiag(TParm parm) {
    	TParm result = new TParm();
        result = query("queryMRODiag", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入病案诊断信息
     */
    public TParm insertMroRecordDiag(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertMRODiag", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询转科日志
     */
    public TParm queryTransLog(TParm parm) {
    	TParm result = new TParm();
        result = query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入转科日志
     */
    public TParm insertTransLog(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /*
     * 查询动态记录日志
     */
    public TParm queryChg(TParm parm){
    	TParm result = new TParm();
        result = query("queryChg", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入动态记录日志
     */
    public TParm insertChg(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertChg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 新生儿信息修改
     */
    public TParm updateSysPatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("updateSysPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查看占床注记
     */
    public TParm checkBedAlloFlg(TParm parm) {
    	TParm result = new TParm();
        result = query("checkBedAlloFlg", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 新生儿重新入院 
     */
    public TParm newPatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("newPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 跟新原mr_no数据的状态
     */
    public TParm updatePatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("updatePatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
   
    
}
