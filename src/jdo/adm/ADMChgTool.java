package jdo.adm;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 入出转管理</p>
 *
 * <p>Description: 入出转管理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMChgTool extends TJDOTool {
    /**
     * 实例
     */
    public static ADMChgTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMChgTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMChgTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public ADMChgTool() {
        setModuleName("adm\\ADMChgModule.x");
        onInit();
    }
    /**
     * 住院登记时插入ADM_CHG
     * @param parm TParm
     * @return TParm
     */
    public TParm insertAdmChg (TParm parm,TConnection conn){
        TParm result = new TParm();
        result = update("insert", parm,conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }

        return result ;
    }
    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertChgDr(TParm parm ,TConnection conn){
        TParm result = new TParm();
           result = update("insert", parm,conn);
           if (result.getErrCode() < 0) {
               err(result.getErrCode() + " " + result.getErrText());
               return result;
           }

           return result ;

    }

    /**
     * 三级检诊
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
public TParm upDateForchgBed(TParm parm,TConnection conn){
    TParm result = new TParm();
       result = update("updateforchgbed", parm,conn);
       if (result.getErrCode() < 0) {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }

       return result ;

}

    /**
     * 动态记录查询
     * @param parm TParm
     */
    public TParm ADMQueryChgLog(TParm parm){
        TParm result = query("queryChg", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

//    /**
//     * 动态记录查询FOR MRO
//     * @param parm TParm
//     */
//    public TParm MROQueryChgLog(TParm parm) {
//        TParm result = query("queryChgForMro", parm);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
//        return result;
//    }
    /**
     * 查询某一病患的动态记录（病案首页使用）
     * @param parm TParm 必须参数： CASE_NO
     * @return TParm
     */
    public TParm queryChgForMRO(TParm parm){
        TParm result= this.query("selectChgForMRO",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询最大seq
     * @return TParm
     */
    public TParm ADMQuerySeq(TParm parm) {
        TParm data = query("querySeq", parm);
        int seq = data.getInt("SEQ_NO",0)+1;
        TParm result = new TParm();
        result.addData("SEQ_NO",seq);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
     * 插入动态记录 传参 case_no ; PSF_KIND ;OPT_USER;OPT_TERM;PSF_HOSP;CANCEL_FLG;CANCEL_DATE;CANCEL_USER
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * ===========pangben modify 20110617 添加区域参数
     */
    public TParm insertChg(TParm parm,TConnection conn){
        //查询最大SEQ
        TParm seqQuery = new TParm();
        seqQuery.setRowData(parm);
        TParm seqParm = this.ADMQuerySeq(seqQuery);
        String seq = "";
        if (seqParm.getData("SEQ_NO", 0) == null ||
            "".equals(seqParm.getData("SEQ_NO", 0)))
            seq = "0";
        else
            seq = seqParm.getData("SEQ_NO", 0).toString();

        //查询 IPD_NO，MR_NO， 科室，病区，病床，经治医师，主治医师，科主任
        TParm patQuery = new TParm();
        patQuery.setRowData(parm);
        TParm patDate = ADMInpTool.getInstance().queryCaseNo(patQuery);
        //插入ADM_CHG
        TParm insertData = new TParm();
        insertData.setData("CASE_NO",parm.getData("CASE_NO"));
        insertData.setData("SEQ_NO",seq);
        insertData.setData("IPD_NO",patDate.getData("IPD_NO",0));
        insertData.setData("CASE_NO",patDate.getData("CASE_NO",0));
        insertData.setData("MR_NO",patDate.getData("MR_NO",0));
        insertData.setData("PSF_KIND",parm.getData("PSF_KIND"));
        insertData.setData("PSF_HOSP",parm.getData("PSF_HOSP"));
        insertData.setData("CANCEL_FLG",parm.getData("CANCEL_FLG"));
        insertData.setData("CANCEL_DATE",parm.getData("CANCEL_DATE"));
        insertData.setData("CANCEL_USER",parm.getData("CANCEL_USER"));
        insertData.setData("DEPT_CODE",patDate.getData("DEPT_CODE",0));
        insertData.setData("STATION_CODE",patDate.getData("STATION_CODE",0));
        insertData.setData("BED_NO",patDate.getData("BED_NO",0)==null?"":patDate.getData("BED_NO",0));
        insertData.setData("VS_CODE_CODE",patDate.getData("VS_CODE_CODE",0)==null?"":patDate.getData("VS_CODE_CODE",0));
        insertData.setData("ATTEND_DR_CODE",patDate.getData("ATTEND_DR_CODE",0)==null?"":patDate.getData("ATTEND_DR_CODE",0));
        insertData.setData("DIRECTOR_DR_CODE",patDate.getData("DIRECTOR_DR_CODE",0)==null?"":patDate.getData("DIRECTOR_DR_CODE",0));
        insertData.setData("OPT_USER",parm.getData("OPT_USER"));
        insertData.setData("OPT_TERM", parm.getData("OPT_TERM"));
        //====pangben modify 20110617
        insertData.setData("REGION_CODE", parm.getData("REGION_CODE"));
        TParm result = update("insert", insertData, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

}
