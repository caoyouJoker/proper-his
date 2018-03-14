package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 手术记录</p>
 *
 * <p>Description: 手术记录</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 1.0
 */
public class OPEOpDetailTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPEOpDetailTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static OPEOpDetailTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEOpDetailTool();
        return instanceObject;
    }

    public OPEOpDetailTool() {
        this.setModuleName("ope\\OPEOpDetailModule.x");
        this.onInit();
    }
    /**
     * 查询手术记录信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询手术记录时间 （体温单中使用）
     * @param parm
     * @return
     */
    public TParm selectDataForSUMVital(TParm parm) {
    	 TParm result = this.query("selectDataForSUM",parm);
         if(result.getErrCode() < 0){
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    
    /**
     * 插入手术记录
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm){
        TParm result = this.update("insertData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入手术记录
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm,TConnection conn){
        //整理修改手术申请单状态的参数
        TParm stateParm = new TParm();
        stateParm.setData("OPBOOK_SEQ",parm.getValue("OPBOOK_NO"));//预约单号
        //新的手术状态  0,申请；1,排程完毕；2,接患者；3,手术室交接；4,手术等待；5,手术开始；6,关胸；7,手术结束；8,返回病房
        stateParm.setData("STATE","7");//2手术完成   7为手术结束 wanglong 20150330
        TParm result = new TParm();
        result = this.update("insertData",parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = OPEOpBookTool.getInstance().updateOPEState(stateParm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改手术记录
     * @param parm TParm
     * @return TParm
     */
    public TParm updateData(TParm parm){
        TParm result = this.update("updateData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 病案手术信息转入（病案首页使用）
     * @return TParm
     */
    public TParm intoOPEDataForMRO(String CASE_NO){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("intoOPEDataForMRO",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 结构化手术记录要带入的数据
     * @param OP_RECORD_NO String
     * @return TParm
     */
    public TParm selectForEmr(String OP_RECORD_NO){
        TParm parm = new TParm();
        parm.setData("OP_RECORD_NO",OP_RECORD_NO);
        TParm result = this.query("selectForEmr",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 根据手术申请号查询手术记录号
     * @param OP_BOOK_NO
     * @return
     */
    public String selectForEmrByBookNo(String OP_BOOK_NO) {// add by wanglong 20130608
        TParm parm = new TParm();
        parm.setData("OP_BOOK_NO", OP_BOOK_NO);
        TParm result = this.query("selectForEmrByBookNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return "";
        }
        return result.getValue("OP_RECORD_NO", 0);
    }
}
