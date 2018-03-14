package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.javahis.util.StringUtil;
import jdo.adm.ADMInpTool;

/**
 * <p>Title: 病案审核</p>
 *
 * <p>Description: 病案审核</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2009-4-30
 * @version 1.0
 */
public class MROChrtvetrecTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static MROChrtvetrecTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROChrtvetrecTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROChrtvetrecTool();
        return instanceObject;
    }

    public MROChrtvetrecTool() {
        this.setModuleName("mro\\MROChrtvetrecModule.x");
        this.onInit();
    }
    /**
     * 查询某一患者的住院和个人信息
     * @return TParm
     */
    public TParm selectdata(String CASE_NO){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("selectdata",parm);
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
     * 查询某一位患者的所有审核信息
     * @param CASE_NO String 就诊序号
     * @return TParm
     */
    public TParm selectAllChrtvetrec(String CASE_NO){//MROChrtvetrecModule.x
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("selectAllChr",parm);
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
    public TParm insertdata(TParm parm,TConnection conn) {
        TParm result = update("insertdata", parm,conn);
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
    public TParm updatedata(TParm parm,TConnection conn) {
        TParm result = update("updatedata", parm,conn);
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
     * @param parm TParm  三个必须参数  CASE_NO，EXAMINE_DATE，EXAMINE_CODE
     * @return TParm
     */
    public TParm deletedata(TParm parm,TConnection conn){
        TParm result = update("deletedata",parm,conn);
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
     * 根据条件查询某一病患的审核信息
     * @param parm TParm
     * @return TParm
     */
    public TParm queryData(TParm parm){
        TParm result = new TParm();
        String sql =
            //modify by wanglong 20130819
            "SELECT '' FLG, A.VS_CODE, A.EXAMINE_CODE, A.EXAMINE_DATE, A.DEDUCT_SCORE, A.DEDUCT_NOTE, " +
            "       A.URG_FLG, A.REPLY_DTTM, A.REPLY_DR_CODE, A.REPLY_REMK, A.OPT_USER, " +
            "       A.OPT_DATE, A.OPT_TERM, B.TYPE_CODE, TO_DATE(A.EXAMINE_DATE,'YYYYMMDDHH24MISS') AS EXA_DATE,A.DEDUCT_SCORECOUNT  " +
            "  FROM MRO_CHRTVETREC A,MRO_CHRTVETSTD B,MRO_QLAYCONTROLM C " +
            " WHERE A.EXAMINE_CODE = B.EXAMINE_CODE " +
            "   AND C.QUERYSTATUS = '1' " +
            "   AND C.STATUS = '0' " +
            "   AND A.CASE_NO = C.CASE_NO " +
            "   AND A.EXAMINE_CODE = C.EXAMINE_CODE ";
        if(!parm.getValue("CASE_NO").equals("")){
            sql+= " AND A.CASE_NO = '"+ parm.getValue("CASE_NO") +"'";
        }
        else{
            result.setErr(-1,"CASE_NO是必须参数！");
        }
        //判断 审核信息是否已经被回复
        if(parm.getValue("REPLY").equals("Y")){
            sql += " AND A.REPLY_DR_CODE IS NOT NULL ";
        }
        else if(parm.getValue("REPLY").equals("N")){
            sql += " AND A.REPLY_DR_CODE IS NULL ";
        }
        //判断经治医师
        if(parm.getData("VS_CODE")!=null&&!parm.getValue("VS_CODE").trim().equals("")){
            sql += " AND A.VS_CODE='"+ parm.getValue("VS_CODE").trim() +"'";
        }
        result.setData(TJDODBTool.getInstance().select(sql));
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据条件查询审核信息
     * @param parm TParm 参数:CASE_NO;EXAMINE_CODE;EXAMINE_DATE
     * @return TParm
     */
    public TParm selectChrData(TParm parm){
        TParm result = this.query("selectChrData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据条件查询病患就诊的总分
     * @param parm TParm 参数:CASE_NO;MR_NO;IPD_NO
     * @return TParm
     * ===============pangben modify 20110801
     */
    public TParm selectSumScode(TParm parm){//modify by wanglong 20130819
        String sql="SELECT CASE_NO, IPD_NO, MR_NO " +
        		"     FROM MRO_CHRTVETREC " +
                "    WHERE CASE_NO IN (#) "+
        		" GROUP BY CASE_NO,IPD_NO,MR_NO";
        if (parm.getValue("CASE_NO").length() > 0) {
            sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
        } else {
            sql = sql.replaceFirst("#", parm.getValue("CASE_LIST"));
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = this.query("selectSumScode",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     *  根据条件查询病患就诊的总分
     * @param parm TParm
     * @return TParm
     * ===============pangben modify 20110830
     */
    public TParm selectSumScode1(TParm parm){//modify by wanglong 20130819
        String sql =
                "WITH CATEGORY AS (SELECT CATEGORY_CODE, TO_NUMBER (DESCRIPTION) AS DESCRIPTION "
                        + "          FROM SYS_CATEGORY "
                        + "         WHERE RULE_TYPE = 'MRO_CHRTVETSTD' "
                        + "           AND LENGTH (CATEGORY_CODE) = 3 "
                        + "           AND (CATEGORY_CODE LIKE '21%' OR CATEGORY_CODE LIKE '22%') ), "
                        + "CHRTVETREC AS (SELECT CASE_NO, MR_NO, IPD_NO, CATEGORY_CODE, SUM (DEDUCT_SCORE) AS SUB_DEDUCT_SCORE "
                        + "                 FROM (SELECT B.CASE_NO, B.MR_NO, B.IPD_NO, SUBSTR (A.EXAMINE_CODE, 0, 3) AS CATEGORY_CODE, "
                        + "                              A.DEDUCT_SCORE * A.DEDUCT_SCORECOUNT AS DEDUCT_SCORE "
                        + "                         FROM MRO_CHRTVETREC A, MRO_QLAYCONTROLM B "
                        + "                        WHERE A.CASE_NO = B.CASE_NO "
                        + "                          AND A.EXAMINE_CODE = B.EXAMINE_CODE "
                        + "                          AND B.QUERYSTATUS = '1' "
                        + "                          AND B.STATUS = '0') "
                        + "             GROUP BY CASE_NO, MR_NO, IPD_NO, CATEGORY_CODE) "
                        + "SELECT B.CASE_NO, MR_NO, IPD_NO, 100 - SUM (LEAST (A.DESCRIPTION, B.SUB_DEDUCT_SCORE)) AS SCODE "
                        + "  FROM CATEGORY A, CHRTVETREC B "
                        + " WHERE A.CATEGORY_CODE = B.CATEGORY_CODE "
                        + "   AND B.CASE_NO IN (#) "
                        + "GROUP BY B.CASE_NO, MR_NO, IPD_NO";
        if (parm.getValue("CASE_NO").length() > 0) {
            sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
        } else {
            sql = sql.replaceFirst("#", parm.getValue("CASE_LIST"));
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//           TParm result = this.query("selectSumScode1",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改病案审核注记（包括MR_RECORD和ADM_INP两张表的）
     * @param parm TParm 必须参数：CASE_NO；MRO_CHAT_FLG：0，未审核   1，审核中   2，完成
     * @param conn TConnection
     */
    public TParm updateMRO_CHAT_FLG(TParm parm,TConnection conn){
        TParm result = MRORecordTool.getInstance().updateMRO_CHAT_FLG(parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = ADMInpTool.getInstance().updateMRO_CHAT_FLG(parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新E_Mail状态
     * @param parm TParm  三个必须参数  CASE_NO，EXAMINE_DATE，EXAMINE_CODE
     * @return TParm
     */
    public TParm updateEMail(TParm parm,TConnection conn){
        TParm result = update("updateEMail",parm,conn);
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
     * 更新公布栏状态
     * @param parm TParm  三个必须参数  CASE_NO，EXAMINE_DATE，EXAMINE_CODE
     * @return TParm
     */
    public TParm updateBoard(TParm parm,TConnection conn){
        TParm result = update("updateBoard",parm,conn);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


}
