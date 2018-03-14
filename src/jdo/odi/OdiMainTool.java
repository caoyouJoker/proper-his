package jdo.odi;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
 
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
public class OdiMainTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OdiMainTool instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static OdiMainTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OdiMainTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public OdiMainTool() {
        setModuleName("odi\\ODIMAINTOOL.x");
        onInit();
    }
    /**
     * 查询病患基本档数据
     * @param parm TParm
     * @return TParm
     */
    public TParm queryInPatInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selectdataInDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询病患基本信息数据
     * @param parm TParm
     * @return TParm
     */
    public TParm qeuryOutPatInfo(TParm parm){
        TParm result = new TParm();
        result = query("selectdataOutDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询病患就诊序号
     * @param mrNo String
     * @return String
     */
    public TParm queryPatCaseNo(TParm parm){
        TParm result = query("selectPatCaseNo",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询护士审核注记
     * @return boolean
     */
    public boolean queryNsCheckFlg(){
        TParm result = query("queryNsCheckFlg");
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return false;
        }
        if(result.getData("NS_CHECK_FLG",0).equals("N")){
            return false;
        }
        return true;
    }
    /**
     * 得到住院系统设置参数
     * @return boolean
     */
    public Object getOdiSysParmData(String key){
        TParm result = query("queryNsCheckFlg");
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return "";
        }
        return result.getData(key,0);
    }
    /**
     * 得到门诊中药参数
     * @param key String
     * @return Object
     */
    public Object getOpdSysParmData(String key){
        TParm result = query("queryChnSysParm");
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return "";
        }
        return result.getData(key,0);
    }
    /**
     * 查询PHA_BASE表相关数据
     * @param parm TParm
     * @return TParm
     */
    public TParm queryPhaBase(TParm parm){
        TParm result = query("queryPhaBase",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询SYS_FEE表启用标记
     * @param parm TParm
     * @return TParm
     */
    public TParm querySysFee(TParm parm){
        TParm result = query("querySysFee",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断EMR文件是否存在，参数为文件名FILE_NAME，CASE_NO ,文件路径FILE_PATH
     * @return TParm
     */
    public TParm checkEmrFileExist(TParm parm){
        TParm result = query("checkEmrFileExist",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 保存EMR文件数据
     * @param parm TParm
     * @return TParm
     */
    public TParm saveEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("updateEmrFile",parm,con);
        if(result.getErrCode()<0)
            return result;
        result = this.update("insertEmrFile",parm,con);
        return result;
    }
    /**
     * 保存新建病例
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveNewEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        if(parm.getValue("OPBOOK_SEQ") == null || parm.getValue("OPBOOK_SEQ").length()<=0){
        	parm.setData("OPBOOK_SEQ","");
        }
        result = this.update("insertEmrFile",parm,con);
        return result;
    }
    /**
     * 修改床位号
     * @param parm TParm
     * @return TParm
     */
    public TParm modifBedNoUD(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("modifBedNoUD",parm,con);
        return result;
    }
    /**
     * 更新EMR文件状态
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm updateEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("updateEmrFile",parm,con);
        return result;
    }
    /**
     * 更新EMR文件状态
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm writeEmrFile(TParm parm,TConnection con){
    	TParm result = new TParm();
    	result = this.update("writeEmrFile",parm,con);
    	return result;
    }
    /**
     * 更新EMRPDF状态
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm writePDFEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("writePDFEmrFile",parm,con);
        return result;
    }
    
    /**
     * 通过文件，更新状态
     * @param parm
     * @param con
     * @return
     */
    public TParm updateEmrFileByFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("updateEmrFileByFile",parm,con);
        return result;
    }
    /**
     * 保存审批的医嘱
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm onSaveOdiOrder(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("insertOdiOrder",parm,con);
        return result;
    }
    
}
