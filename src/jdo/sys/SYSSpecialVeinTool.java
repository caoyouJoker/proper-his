/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 静脉注射</p>
*
* <p>Description:静脉注射 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSSpecialVeinTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSSpecialVeinTool instanceObject;
    /**
     * 得到实例
     * @return SYSPhaRouteTool
     */
    public static SYSSpecialVeinTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSSpecialVeinTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSSpecialVeinTool()
    {
        setModuleName("sys\\SYSSpecialVeinModule.x");
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
     * 根据特殊静脉用药类别查询数据
     * @param EXCPHATYPE_CODE String 给药途径代码
     * @return TParm
     */
    public TParm selectdata(String EXCPHATYPE_CODE){
        TParm parm = new TParm();
        parm.setData("EXCPHATYPE_CODE",EXCPHATYPE_CODE);
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
     * 新增指定特殊静脉用药类别得到数据
     * @param EXCPHATYPE_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String EXCPHATYPE_CODE= parm.getValue("EXCPHATYPE_CODE");
       //System.out.println("EXCPHATYPE_CODE"+EXCPHATYPE_CODE);
       TParm result=new TParm();
       if(!existsEXCPHATYPECODE(EXCPHATYPE_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"特殊静脉用药类别 "+EXCPHATYPE_CODE+" 已经存在!");
           return result ;
       }

       return result;
	}
	/**
     * 判断是否存在数据
     * @param EXCPHATYPE_CODE String
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsEXCPHATYPECODE(String EXCPHATYPE_CODE){
        TParm parm = new TParm();
        parm.setData("EXCPHATYPE_CODE",EXCPHATYPE_CODE);
        //System.out.println("existsSpecialVein"+EXCPHATYPE_CODE);
        return getResultInt(query("existsSpecialVein",parm),"COUNT") > 0;
    }
	/**
     * 更新指定EXCPHATYPE_CODE数据
     * @param EXCPHATYPE_CODE String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String EXCPHATYPE_CODE= parm.getValue("EXCPHATYPE_CODE");
        //System.out.println("true or false"+existsEXCPHATYPECODE(EXCPHATYPE_CODE));
        if(existsEXCPHATYPECODE(EXCPHATYPE_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"特殊静脉用药类别 "+EXCPHATYPE_CODE+" 刚刚被删除！");
            return result ;
        }

        return result;
    }
    /**
     * 删除指定特殊静脉用药类别数据
     * @param EXCPHATYPE_CODE String
     * @return boolean
     */
    public TParm deletedata(String EXCPHATYPE_CODE){
        TParm parm = new TParm();
        parm.setData("EXCPHATYPE_CODE",EXCPHATYPE_CODE);
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
