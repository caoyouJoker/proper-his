/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 用药途径</p>
*
* <p>Description:用药途径 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSPhaRouteTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSPhaRouteTool instanceObject;
    /**
     * 得到实例
     * @return SYSPhaRouteTool
     */
    public static SYSPhaRouteTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSPhaRouteTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSPhaRouteTool()
    {
        setModuleName("sys\\SYSPhaRouteModule.x");
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
     * 根据给药途径代码代码查询数据
     * @param ROUTE_CODE String 给药途径代码
     * @return TParm
     */
    public TParm selectdata(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
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
     * 新增指定给药途径代码得到数据
     * @param ROUTE_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String ROUTE_CODE= parm.getValue("ROUTE_CODE");
       //System.out.println("ROUTE_CODE"+ROUTE_CODE);
       TParm result=new TParm();
       if(!existsROUTECODE(ROUTE_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"给药途径代码 "+ROUTE_CODE+" 已经存在!");
           return result ;
       }

       return result;
	}
	/**
     * 判断是否存在数据
     * @param ROUTE_CODE String
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsROUTECODE(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
        //System.out.println("existsROUTECODE"+ROUTE_CODE);
        return getResultInt(query("existsROUTECODE",parm),"COUNT") > 0;
    }
	/**
     * 更新指定ROUTE_CODE数据
     * @param ROUTE_CODE String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String ROUTE_CODE= parm.getValue("ROUTE_CODE");
        //System.out.println("true or false"+existsROUTECODE(ROUTE_CODE));
        if(existsROUTECODE(ROUTE_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"给药途径代码 "+ROUTE_CODE+" 刚刚被删除！");
            return result ;
        }

        return result;
    }
    /**
     * 删除指定给药途径代码数据
     * @param ROUTE_CODE String
     * @return boolean
     */
    public TParm deletedata(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
        TParm result = update("deletedata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据SYS_PHAROUTE.ORDER_CODE查询SYS_FEE相关数据
     *     ORDER_CODE,ORDER_DESC,DESCRIPTION,GOODS_DESC,ALIAS_DESC
     * @param ROUTE_CODE String 给药途径代码
     * @return TParm
     */
    public TParm selectsysfee(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
        TParm result = query("selectfromsysfee",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询所有SYS_FEE相关数据
     *     ORDER_CODE,ORDER_DESC,DESCRIPTION,GOODS_DESC,ALIAS_DESC
     * @param ROUTE_CODE String 给药途径代码
     * @return TParm
     */
    public TParm selectallsysfee(){
        TParm parm = new TParm();
        TParm result = query("selectallfromsysfee",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 给定OrderCode返回途径代码
     * @param orderCode String 指定的ordercode
     * @return TParm 查询出来的相应的途径代码
     */
    public TParm selectByOrder(String orderCode){
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("selectByOrder",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
