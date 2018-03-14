package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
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
 * @author wangl 2008.09.25
 * @version 1.0
 */
public class PanelRoomTool extends TJDOTool{
    /**
     * 实例
     */
    public static PanelRoomTool instanceObject;
    /**
     * 得到实例
     * @return PanelRoomTool
     */
    public static PanelRoomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelRoomTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PanelRoomTool() {
        setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    /**
     * 查询诊室
     * @param clinicRoomNo String
     * @return TParm
     */
    public TParm queryTree(String clinicRoomNo) {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         TParm result = query("queryTree", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }
     /**
      * 新增诊室
      * @param parm TParm
      * @return TParm
      */
     public TParm insertdata(TParm parm) {
         TParm result = new TParm();
         String clinicRoomNo = parm.getValue("CLINICROOM_NO");
         if(existsClinicRoom(clinicRoomNo)){
             result.setErr(-1,"诊室"+" 已经存在!");
             return result ;
         }

         result = update("insertdata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }
     /**
      * 更新诊室
      * @param parm TParm
      * @return TParm
      */
     public TParm updatedata(TParm parm) {
//         System.out.println("parm"+parm);
         TParm result = update("updatedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }
     /**
      * 查询诊室
      * @param parm TParm
      * @return TParm
      */
     public TParm selectdata(TParm parm) {
         TParm result = new TParm();
         result = query("selectdata",parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 删除诊室
      * @param clinicRoomNo String
      * @return boolean
      */
     public TParm deletedata(String clinicRoomNo) {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         TParm result = update("deletedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 判断是否存在诊室
      * @param clinicRoomNo String 诊室代码
      * @return boolean TRUE 存在 FALSE 不存在
      */
     public boolean existsClinicRoom(String clinicRoomNo) {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         return getResultInt(query("existsClinicRoom", parm), "COUNT") > 0;
     }
     /**
      * 得到诊室
      * @param clinicRoomNo String
      * @return int
      */
     public int initclinicroomno(String clinicRoomNo)
     {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         return getResultInt(query("initclinicroomno",parm),"CLINICROOM_NO");
     }
     /**
      * 根据诊室得到诊区（For REG）
      * @param clinicRoomNo String
      * @return TParm
      */
     public TParm getAreaByRoom(String clinicRoomNo){
     TParm parm = new TParm();
     parm.setData("CLINICROOM_NO",clinicRoomNo);
     TParm result = query("getAreaByRoom",parm);
     return result;
     }
}
