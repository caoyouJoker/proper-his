package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.adm.ADMInpTool;

/**
 * <p>绿色通道 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author JiaoY 20090428
 * @version 1.0
 */
public class BILGreenPathTool extends TJDOTool{
    /**
      * 实例
      */
     public static BILGreenPathTool instanceObject;

     /**
      * 得到实例
      * @return RegMethodTool
      */
     public static BILGreenPathTool getInstance()
     {
         if(instanceObject == null)
             instanceObject = new BILGreenPathTool();
         return instanceObject;
     }

     /**
      * 构造器
      */
     public BILGreenPathTool()
     {
         setModuleName("bil\\BILGreenPathModule.x");
         onInit();
     }

     /**
      * 新增数据
      * @param regMethod String
      * @return TParm
      */
     public TParm insertdata(TParm parm,TConnection conn) {
//         System.out.println("后台Parm:"+parm);
         TParm result = new TParm();
         TParm check =new TParm();
         check.setData("CASE_NO",parm.getData("CASE_NO"));
         check.setData("APPLY_DATE",parm.getData("APPLY_DATE"));
         if(existsPatMethod(check)){
             result.setErr(-1,"时段重复!");
             return result ;
         }
         result = update("insertdata", parm,conn);
         // 判断错误值
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         //查询住院数据 获取原有绿色通道的值
         TParm admParm = new TParm();
         admParm.setData("CASE_NO",parm.getValue("CASE_NO"));
         TParm adm = ADMInpTool.getInstance().selectall(admParm);
//         System.out.println("APPROVE_AMT:"+parm.getDouble("APPROVE_AMT",0));
         double greenPath = adm.getDouble("GREENPATH_VALUE",0)+parm.getDouble("APPROVE_AMT");
         //修改ADM_INP表中的绿色通道字段
         TParm greenParm = new TParm();
         greenParm.setData("CASE_NO",parm.getValue("CASE_NO"));
         greenParm.setData("GREENPATH_VALUE",greenPath);
//         System.out.println("greenParm:"+greenParm);
         result = ADMInpTool.getInstance().updateGREENPATH_VALUE(greenParm,conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 更新
      * @param regMethod String
      * @return TParm
      */
     public TParm updatedata(TParm parm) {
         TParm result = update("updatedata", parm);
         // 判断错误值
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 查询全字段
      * @param  TParm
      * @return TParm
      */
     public TParm selectdata(TParm parm){
         TParm result = query("selectdata",parm);
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
     * 查询绿色通道中数据
     * @param  TParm
     * @return TParm
     */
    public TParm selectGreenPath(TParm parm){
        TParm result = query("selectGreenPath",parm);
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
     * 作废某病人的所有绿色通道
     * @param CASE_NO String
     * @return TParm
     */
    public TParm cancleGreenPath(String CASE_NO,TConnection conn){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = update("deletedata",parm,conn);
        // 判断错误值
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //清空adm_inp中的绿色通道 值
        TParm greenParm = new TParm();
        greenParm.setData("CASE_NO",CASE_NO);
        greenParm.setData("GREENPATH_VALUE",0);
        result = ADMInpTool.getInstance().updateGREENPATH_VALUE(greenParm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
     /**
      * 做废一条绿色通道
      * @param parm TParm   参数：CASE_NO；APPLY_DATE;APPLY_AMT
      * @param conn TConnection
      * @return TParm
      */
     public TParm cancleGreenPath(TParm parm,TConnection conn){
         TParm result = update("deletedata",parm,conn);
         // 判断错误值
         if(result.getErrCode() < 0){
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         //查询住院数据 获取原有绿色通道的值
         TParm admParm = new TParm();
         admParm.setData("CASE_NO",parm.getValue("CASE_NO"));
         TParm adm = ADMInpTool.getInstance().selectall(admParm);
         //减去作废的绿色通道金额
         double greenPath = adm.getDouble("GREENPATH_VALUE",0)-parm.getDouble("APPROVE_AMT");
         //修改ADM_INP表中的绿色通道字段
         TParm greenParm = new TParm();
         greenParm.setData("CASE_NO",parm.getValue("CASE_NO"));
         greenParm.setData("GREENPATH_VALUE",greenPath);
         result = ADMInpTool.getInstance().updateGREENPATH_VALUE(greenParm,conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }


     /**
      *
      * @param caseNo String
      * @return boolean
      */
     public boolean existsPatMethod(TParm parm){
         return getResultInt(query("existsPatMethod",parm),"COUNT") > 0;
     }

}
