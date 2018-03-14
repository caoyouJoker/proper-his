package jdo.onw;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 医技进度</p>
 *
 * <p>Description: 医技进度</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-26
 * @version 1.0
 */
public class ONWMEDProgressTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ONWMEDProgressTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static ONWMEDProgressTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWMEDProgressTool();
        return instanceObject;
    }
    public ONWMEDProgressTool() {
        setModuleName("onw\\ONWMEDProgressModule.x");
        onInit();
    }
    /**
     * 查询信息
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm selectData(String RPTTYPE_CODE,String DEPT_CODE,String DATE){
        String where1="";
        String where2="";
        String where3="";
        //RPTTYPE_CODE字段前三位表示系统名称 如果前台传入的数据为3位或者小于3位 表示默认以“系统名称”为单位查询
        if(RPTTYPE_CODE.length()<3){
            where1 = "SUBSTR(RPTTYPE_CODE,0,3) AS RPTTYPE_CODE";
        }else{//当前台传入参数大于3位时 以“检查项目”为单位查询
            where1 = "RPTTYPE_CODE";
        }
        if(DEPT_CODE.length()>0){
            where2 = "AND A.EXEC_DEPT_CODE='"+DEPT_CODE+"'";
        }
        if(RPTTYPE_CODE.length()>3){
            where3 = "AND A.RPTTYPE_CODE='"+RPTTYPE_CODE+"'";
        }else if(RPTTYPE_CODE.length()>0&&RPTTYPE_CODE.length()<=3){
            where3 = "AND SUBSTR(A.RPTTYPE_CODE,0,3)='"+RPTTYPE_CODE+"'";
        }
        String sql = "SELECT A.RPTTYPE_CODE, B.DEPT_CHN_DESC, " +
            "COUNT (CASE WHEN A.STATUS IS NOT NULL THEN 1 ELSE NULL END ) AS ORDER_NUM, " + //所有状态都数据开单人数
            "COUNT (CASE WHEN A.STATUS = '0' OR A.STATUS = '1' OR A.STATUS = '2' THEN 1 ELSE NULL END) AS WAIT_NUM " +//状态小于等于2的为等待中的人数
            "FROM (SELECT "+where1+",EXEC_DEPT_CODE,STATUS,ORDER_DATE FROM MED_APPLY) A, SYS_DEPT B " +
            "WHERE A.EXEC_DEPT_CODE = B.DEPT_CODE " +
            where2 +
            where3 +
            "AND A.ORDER_DATE BETWEEN TO_DATE('"+DATE+"','YYYYMMDD') AND TO_DATE('"+DATE+"'||'235959','YYYYMMDDHH24MISS') " +
            "GROUP BY A.RPTTYPE_CODE, A.EXEC_DEPT_CODE, B.DEPT_CHN_DESC ";
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
