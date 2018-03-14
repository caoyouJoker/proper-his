package jdo.reg;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title:诊室维护工具类 </p>
 *
 * <p>Description:诊室维护工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class ClinicRoomTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ClinicRoomTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static ClinicRoomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ClinicRoomTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ClinicRoomTool() {
        setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }

    /**
     * 查询诊室
     * @param clinicAreaCode String
     * @return TParm
     */
    public TParm queryTree(String clinicAreaCode) {
        TParm parm = new TParm();
        parm.setData("CLINICAREA_CODE", clinicAreaCode);
        TParm result = query("queryTree", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 根据诊区查询诊室table信息
     * @param AREACODE String
     * @return TParm
     */
    public TParm selectdata(String AREACODE) {
        TParm parm = new TParm();
        parm.setData("CLINICAREA_CODE", AREACODE);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 得到诊区，诊室（For ONW）
     * @param parm TParm 参数格式  ADM_TYPE:门急住别(必填) ;CLINICAREA_CODE:诊区
     * @return TParm
     */
    public TParm getClinicRoomForONW(TParm parm) {
        TParm result = query("getClinicRoomForAdmType", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据诊室得到药房（For OPD）
     * @param clinicRoom String
     * @return TParm
     */
    public TParm getOrgCodeByRoomNo(String clinicRoom) {
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", clinicRoom);
        TParm result = query("getOrgCodeByRoomNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询诊间对应的药房
     * @param regionCode String
     * @param admDate String
     * @param sessionCode String
     * @param admType String
     * @param realdrCode String
     * @param realDeptCode String
     * @return TParm
     */
    public TParm getOrgByOdo(String regionCode, String admDate,
                             String sessionCode, String admType,
                             String realdrCode, String realDeptCode,String CASE_NO) {
        String sql =
            "SELECT DISTINCT A.ORG_CODE " +
            "	FROM REG_CLINICROOM A,REG_PATADM B " +
            "	WHERE B.REGION_CODE= '" + regionCode + "'" +
            "		  AND B.ADM_DATE= TO_DATE('" + admDate + "','YYYYMMDD') " +

            "		  AND B.REALDEPT_CODE='" + realDeptCode + "'" +

            "		  AND B.REALDR_CODE= '" + realdrCode + "'" +
            "             AND B.CASE_NO='"+CASE_NO+"'"+
            "		  AND B.CLINICROOM_NO=A.CLINICROOM_NO";
//    	System.out.println("phaCode--------------->sql->"+sql);

        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//
//    	parm.setData("REGION_CODE",regionCode);
//        parm.setData("ADM_DATE", admDate);
//        parm.setData("SESSION_CODE",sessionCode);
//        parm.setData("ADM_TYPE",admType);
//        parm.setData("REALDR_CODE",realdrCode);
//        TParm result = query("getOrgByODO", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据诊室查询诊区
     * @param roomcode String
     * @return TParm
     */
    public TParm selectfortablebyarea(String roomcode) {
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", roomcode);
        TParm result = query("selectfortablebyarea", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 诊室是否启用
     * @param clinicroomNo String  诊室号
     * @return boolean true 已启用 false 未启用
     */
    public boolean selActiveFlg(String clinicroomNo) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CLINICROOM_NO", clinicroomNo);
        result = query("selActiveFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 根据登录时间、登录科室、登录医师得到当前班表里的诊室
     * @param admType String
     * @param admDate String
     * @param sessionCode String
     * @return String
     */
    public String getRoomNo(String admType, String admDate, String sessionCode,
                            String deptCode, String drCode) {
        String roomNo = "";
        TParm parm = new TParm();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", sessionCode);
        parm.setData("DEPT_CODE", deptCode);
        parm.setData("DR_CODE", drCode);
        TParm result = query("getRoomNo", parm);
        if (result.getErrCode() != 0) {
            //System.out.println("result.errCode" + result.getErrText());
            return null;
        }
//        System.out.println("result==="+result);
        roomNo = result.getValue("CLINICROOM_NO", 0);
//    	System.out.println("roomNo="+roomNo);
        return roomNo;
    }

    /**
     * 查询未使用科室
     * @param parm TParm(就诊日期:ADM_DATE 门急别:ADM_TYPE 时段:SESSION_CODE)
     * @return TParm
     */
    public TParm getNotUseForODO(TParm parm) {
        if (parm == null)
            return parm;
        String sql = "SELECT CLINICROOM_NO AS ID, CLINICROOM_DESC AS NAME, PY1 "+
                     " FROM REG_CLINICROOM "+
                     " WHERE CLINICROOM_NO NOT IN ( "+
                     " SELECT CLINICROOM_NO "+
                     " FROM REG_SCHDAY "+
                     " WHERE ADM_DATE = '"+parm.getValue("ADM_DATE")+"' "+
                     " AND ADM_TYPE = '"+parm.getValue("ADM_TYPE")+"' "+
                     " AND REGION_CODE = '"+parm.getValue("REGION_CODE")+"' " +
                     " AND SESSION_CODE = '"+parm.getValue("SESSION_CODE")+"' ) "+
                     " AND ADM_TYPE='"+parm.getValue("ADM_TYPE")+"' " +
                     " AND REGION_CODE = '"+parm.getValue("REGION_CODE")+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public static void main(String args[]) {
        com.dongyang.util.TDebug.initClient();
        //System.out.println(ClinicRoomTool.getInstance().queryTree("1"));
//        System.out.println(ClinicRoomTool.getInstance().getClinicRoomForAdmType(
//            "E"));
    }
}
