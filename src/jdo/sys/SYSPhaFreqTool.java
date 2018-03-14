/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
*
* <p>Title: 药品频率</p>
*
* <p>Description:药品频率 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSPhaFreqTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSPhaFreqTool instanceObject;
    /**
     * 得到实例
     * @return SYSPhaRouteTool
     */
    public static SYSPhaFreqTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSPhaFreqTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSPhaFreqTool()
    {
        setModuleName("sys\\SYSPhaFreqModule.x");
        onInit();
    }
    /**
     * 初始化界面，查询所有的数据
     * @return TParm
     */
    public TParm selectall(){
    	 TParm parm = new TParm();
//         parm.setData("CODE",CODE);
         TParm result = query("selectall",parm);
         if(result.getErrCode() < 0)
         {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
     * 根据频次代码查询数据
     * @param FREQ_CODE String 频次代码
     * @return TParm
     */
    public TParm selectdata(String FREQ_CODE){
        TParm parm = new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        TParm result = query("selectdata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 新增指定频次代码得到数据
     * @param FREQ_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String FREQ_CODE= parm.getValue("FREQ_CODE");
       //System.out.println("FREQ_CODE"+FREQ_CODE);
       TParm result=new TParm();
       if(!existsFREQCODE(FREQ_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"频次代码 "+FREQ_CODE+" 已经存在!");
           return result ;
       }

       return result;
	}
	/**
     * 判断是否存在数据
     * @param FREQ_CODE String
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsFREQCODE(String FREQ_CODE){
        TParm parm = new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        //System.out.println("existsFREQCODE"+FREQ_CODE);
        return getResultInt(query("existsFREQCODE",parm),"COUNT") > 0;
    }
	/**
     * 更新指定UNIT_CODE数据
     * @param UNIT_CODE String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String FREQ_CODE= parm.getValue("FREQ_CODE");
        //System.out.println("true or false"+existsFREQCODE(FREQ_CODE));
        if(existsFREQCODE(FREQ_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"频次代码 "+FREQ_CODE+" 刚刚被删除！");
            return result ;
        }

        return result;
    }
    /**
     * 删除指定频次代码数据
     * @param FREQ_CODE String
     * @return boolean
     */
    public TParm deletedata(String FREQ_CODE){
        TParm parm = new TParm();
        TParm result=new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        //Todo:决定那张表后再判断
//        if(!allowupdate(FREQ_CODE)){
        	result = update("deletedata",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
//        }else{
//        	result.setErr(-1,"频次代码 "+FREQ_CODE+" 正在被使用，不允许删除！");
//        	return result;
//        }

    }
    /**
     * 根据UNIT_CODE判断SYS_FEE中有没有该UNIT_CODE，如有则不允许删除
     * @param UNIT_CODE
     * @return
     */
    public boolean allowupdate(String FREQ_CODE){
    	TParm parm = new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        return getResultInt(query("allowupdate",parm),"COUNT") > 0;
    }
    /**
     * 根据频次代码检索附表数据
     * @param FREQ_CODE 频次代码
     * @return
     */
    public TParm selectSYSTRTFREQTIME(String FREQ_CODE){
    	TParm parm=new TParm();
    	TParm result =new TParm();
    	parm.setData("FREQ_CODE",FREQ_CODE);
    	result=this.query("selectSYSTRTFREQTIME",parm);
    	if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检验该主键记录是否存在
     * @param FREQ_CODE 频次代码
     * @param STANDING_TIME 时点
     * @return
     */
    public int existSYSTRTFREQTIME(TParm inParm){
        TParm parm = new TParm();
        return getResultInt(query("existSYSTRTFREQTIME",inParm),"COUNT");
    }
    /**
     * 插入附表数据
     * @param parm
     * @return
     */
    public TParm insertSYSTRTFREQTIME(TParm parm){
    	 String FREQ_CODE= parm.getValue("FREQ_CODE");
    	 String STANDING_TIME= parm.getValue("STANDING_TIME");
         TParm result=new TParm();
         TParm inParm =new TParm();
         inParm.setData("FREQ_CODE",FREQ_CODE);
         inParm.setData("STANDING_TIME",STANDING_TIME);
         if(existSYSTRTFREQTIME(inParm)>0){
      	   result = update("insertSYSTRTFREQTIME",parm);
             if(result.getErrCode() < 0)
             {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
         }else{
      	   result.setErr(-1,"该记录已经存在!");
             return result ;
         }

         return result;
    }
    public TParm deleteSYSTRTFREQTIME(String FREQ_CODE,String STANDING_TIME){
         TParm result=new TParm();
         TParm parm=new TParm();
         parm.setData("FREQ_CODE",FREQ_CODE);
         parm.setData("STANDING_TIME",STANDING_TIME);
         //Todo:决定那张表后再判断
//         if(!allowupdate(FREQ_CODE)){
         	result = update("deleteSYSTRTFREQTIME",parm);
             if(result.getErrCode() < 0)
             {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
             return result;
//         }else{
//         	result.setErr(-1,"频次代码 "+FREQ_CODE+" 正在被使用，不允许删除！");
//         	return result;
//         }
    }
    /**
     * 抓取指定频次基本数据
     * @param freqCode
     * @return TParm
     */
    public TParm queryForAmount(String freqCode){
    	TParm parm=new TParm();
    	TParm result =new TParm();
    	parm.setData("FREQ_CODE",freqCode);
    	result=this.query("queryForAmount",parm);
    	if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
