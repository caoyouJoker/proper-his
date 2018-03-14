package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
/**
 *
 * <p>Title:医师周班表工具类 </p>
 *
 * <p>Description:医师周班表工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class SchWeekTool extends TJDOTool{
    /**
     * 实例
     */
    public static SchWeekTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static SchWeekTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SchWeekTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SchWeekTool()
    {
        setModuleName("reg\\REGSchWeekModule.x");
        onInit();
    }
    /**
     * 新增周班表
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新周班表
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据条件查询周班表(右忽略)
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm){
//        System.out.println("周班表数据"+parm);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 周转日查询(新)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataNew(TParm parm){
        //System.out.println("周班表数据入参"+parm);

        String regionWhere = "";
        if (parm.getData("REGION_CODE") != null)
            regionWhere = " AND REGION_CODE = '" + parm.getData("REGION_CODE") +
                          "' ";
        String admTypeWhere = "";
        if (parm.getData("ADM_TYPE") != null)
            admTypeWhere = " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                           "' ";
        String dayOfWeek = "";
        if (parm.getData("DAYOFWEEK") != null)
            dayOfWeek = " AND DAYOFWEEK = '" + parm.getData("DAYOFWEEK") + "' ";
        String roomWhere = "";
        if (parm.getData("CLINICROOM_NO") != null)
            roomWhere = " AND CLINICROOM_NO IN (" +
                        parm.getData("CLINICROOM_NO") + ") ";
        String sql =
                " SELECT REGION_CODE, ADM_TYPE, DAYOFWEEK, SESSION_CODE, CLINICTYPE_CODE,"+
                "        DEPT_CODE, DR_CODE, CLINICROOM_NO, QUEGROUP_CODE, WEST_MEDI_FLG,"+
                "        CREAT_DATE, CLINICTMP_FLG, EXP_DATE, EXP_DATE_E, OPT_USER, OPT_DATE,"+
                "        OPT_TERM,REG_SPECIAL_NUMBER "+
                "   FROM REG_SCHWEEK "+
                "  WHERE  (   EXP_DATE IS NULL "+
                "         OR EXP_DATE_E IS NULL "+
                "         OR EXP_DATE > TO_DATE ('"+parm.getData("ADM_DATE")+"', 'yyyyMMdd') "+
                "         OR EXP_DATE_E < TO_DATE ('"+parm.getData("ADM_DATE")+"', 'yyyyMMdd')) "+
                regionWhere +
                admTypeWhere +
                dayOfWeek +
                roomWhere +
                "   ORDER BY ADM_TYPE, DAYOFWEEK, SESSION_CODE, CLINICTYPE_CODE, DEPT_CODE ";
       // System.out.println("周班表数据sql"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = query("selDataNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除
     * @param region String 区域
     * @param admType String 门急别
     * @param dayofWeek String 星期
     * @param sessionCode String 时段
     * @param clinicroomNo String 诊室
     * @return TParm
     */
    public TParm deletedata(String region,String admType,String dayofWeek,String sessionCode,String clinicroomNo){
        TParm parm = new TParm();
        parm.setData("REGION_CODE",region);
        parm.setData("ADM_TYPE",admType);
        parm.setData("DAYOFWEEK",dayofWeek);
        parm.setData("SESSION_CODE",sessionCode);
        parm.setData("CLINICROOM_NO",clinicroomNo);

        TParm result = update("deletedata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
