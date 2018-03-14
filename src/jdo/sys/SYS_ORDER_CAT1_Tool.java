package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
*
* <p>Title: 医嘱细分类</p>
*
* <p>Description:医嘱细分类 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/

public class SYS_ORDER_CAT1_Tool extends TJDOTool{
	/**
     * 实例
     */
    public static SYS_ORDER_CAT1_Tool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static SYS_ORDER_CAT1_Tool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYS_ORDER_CAT1_Tool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYS_ORDER_CAT1_Tool()
    {
        setModuleName("sys\\SYSOrderCAT1Module.x");
        onInit();
    }
    /**
     * 根据医嘱细分类代码询职别信息
     * @param posCode String 职别代码
     * @return TParm
     */
    public TParm selectdata(String ORDER_CAT1_CODE){
        TParm parm = new TParm();
        parm.setData("ORDER_CAT1_CODE",ORDER_CAT1_CODE);
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
     * 新增指定编号数据
     * @param ORDER_CAT1_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String ORDER_CAT1_CODE= parm.getValue("ORDER_CAT1_CODE");
       TParm result=new TParm();
       if(!existsPosition(ORDER_CAT1_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"医嘱细分类代码 "+ORDER_CAT1_CODE+" 已经存在!");
           return result ;
       }

       return result;
	}
	/**
     * 更新指定编号数据
     * @param posCode String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String ORDER_CAT1_CODE= parm.getValue("ORDER_CAT1_CODE");
        //System.out.println("true or false"+existsPosition(ORDER_CAT1_CODE));
        if(existsPosition(ORDER_CAT1_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"该条医嘱细分类 "+ORDER_CAT1_CODE+" 刚刚被删除！");
            return result ;
        }

        return result;
    }
	 /**
     * 判断是否存在数据
     * @param ORDER_CAT1_CODE String
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsPosition(String ORDER_CAT1_CODE){
        TParm parm = new TParm();
        parm.setData("ORDER_CAT1_CODE",ORDER_CAT1_CODE);
        return getResultInt(query("existsPosition",parm),"COUNT") > 0;
    }
    /**
     * 删除指定编号职别数据
     * @param posCode String
     * @return boolean
     */
    public TParm deletedata(String posCode){
        TParm parm = new TParm();
        parm.setData("ORDER_CAT1_CODE",posCode);
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
