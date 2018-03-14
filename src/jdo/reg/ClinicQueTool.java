package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
/**
 *
 * <p>Title:VIP诊排班工具类 </p>
 *
 * <p>Description:VIP诊排班工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.10.06
 * @version 1.0
 */
public class ClinicQueTool extends TJDOTool{
    /**
      * 实例
      */
     public static ClinicQueTool instanceObject;
     /**
      * 得到实例
      * @return ClinicQueTool
      */
     public static ClinicQueTool getInstance()
     {
         if(instanceObject == null)
             instanceObject = new ClinicQueTool();
         return instanceObject;
     }
     /**
      * 构造器
      */
     public ClinicQueTool()
     {
         setModuleName("reg\\REGClinicQueModule.x");
         onInit();
     }
     /**
      * 查询VIP就诊号
      * @param admType String
      * @param admDate Timestamp
      * @param session String
      * @param clinicRoom String
      * @param visitCode String
      * @param apptCode String
      * @param regmethodCode String
      * @return int
      */
     public int selectqueno(String admType, String admDate,
                            String session, String clinicRoom, String visitCode,
                            String apptCode, String regmethodCode) {
         TParm parm = new TParm();
         parm.setData("ADM_TYPE", admType);
         parm.setData("ADM_DATE", admDate);
         parm.setData("SESSION_CODE", session);
         parm.setData("CLINICROOM_NO", clinicRoom);
         //System.out.println("初复诊状态"+visitCode);
         if ("0".equals(visitCode))
             parm.setData("VISIT_CODE", "Y");
         else
             parm.setData("VISIT_CODE", "N");
         parm.setData("APPT_CODE", apptCode);
         parm.setData("REGMETHOD_CODE", regmethodCode);
         int no = this.getResultInt(query("selectqueno", parm), "QUE_NO");
         if(no == 0)
             return -1;
         return no;

     }
     /**
      * 更新VIP就诊号
      * @param
      * @return TParm
      */
     public TParm updatequeno(TParm parm) {
         parm.setData("QUE_STATUS","Y");
         TParm result = update("updatequeno", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }
}
