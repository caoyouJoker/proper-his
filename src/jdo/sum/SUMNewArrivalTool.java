package jdo.sum;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 新生儿体温单主Tool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMNewArrivalTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static SUMNewArrivalTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static SUMNewArrivalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SUMNewArrivalTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SUMNewArrivalTool() {
        setModuleName("sum\\SUMNewArrivalModule.x");
        onInit();
    }

    /**
     * 根据OEI，CASE_NO查询该病人的记录--EXAMINE_DATE,USER_ID
     * @param regMethod String
     * @return TParm
     */
    public TParm selectExmDateUser(TParm date) {

        TParm result = query("selectNewArrExmUser", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据OEI，CASE_NO,日期 查询某一天的数据
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateDtl(TParm date) {

        TParm result = query("selectOneDayDtl", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据OEI，CASE_NO,日期 查询某一天的数据
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateMst(TParm date) {

        TParm result = query("selectOneDateMst", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 根据OEI，CASE_NO，日期 查询体温主表
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataMst(TParm date) {

        TParm result = query("selectNewArrivalSign", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 根据OEI，CASE_NO，日期 查询生命标记
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataDtl(TParm date) {

        TParm result = query("selectNewArrivalSignDtl", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 新增一条主表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertNewArrival(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertNewArrival", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 跟新一条主表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateNewArrival(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateNewArrival", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * 新增一条细表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertNewArrivalDtl(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm oneParm = new TParm();
            oneParm = (TParm) parm.getParm(i + "PARM");
            result = update("insertNewArrivalDtl", oneParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;

    }

    /**
     * 跟新一条细表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateNewArrivalDtl(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm oneParm = new TParm();
            oneParm = (TParm) parm.getParm(i + "PARM");
            result = update("updateNewArrivalDtl", oneParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;

    }



    /**
     * 检查该要保存的数据是否应经存在，或者作废
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm checkIsExist(TParm parm) {
        TParm result = new TParm();
        result = query("checkIsExist", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
     /**
      * 新生儿出生体重
      * TParm
      * @param parm
      * @return
      */
    public TParm getNewBornWeight(TParm parm){
    	String weight="";
    	TParm reParm=new TParm();
    	TParm result = new TParm();
        result = query("getNewBornWeight", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        weight=result.getInt("BORNWEIGHT", 0)>0?TypeTool.getString(result.getInt("BORNWEIGHT", 0)):"";
        reParm.setData("NB_WEIGHT",weight);
        //同一就诊号的最小日期的字段
        return reParm;
    }
    /**
     * 新生儿入院体重
     * TParm
     * @param parm
     * @return
     */
   public TParm getNewAdmWeight(TParm parm){
	   String weight="";
	   TParm reParm=new TParm();
   	   TParm result = new TParm();
       result = query("getNewAdmWeight", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       for(int i=0;i<result.getCount();i++){
    	  if(result.getInt("WEIGHT", i)>0) {
    		  weight=TypeTool.getString(result.getInt("WEIGHT", i));//g
    		  break;
    	  }
       }
       reParm.setData("NB_ADM_WEIGHT", weight);
     //同一就诊号的最小日期最小时间的字段
       return reParm;
   }

}
