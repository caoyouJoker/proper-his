package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 邮编维护</p>
*
* <p>Description:邮编维护 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800905
* @version 1.0
*/
public class SYSPostTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSPostTool instanceObject;
    /**
     * 得到实例
     * @return SYSPostTool
     */
    public static SYSPostTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSPostTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSPostTool()
    {
        setModuleName("sys\\SYSPostCodeModule.x");
        onInit();
    }
    /**
     * 根据不同参数查询省市邮编的信息
     * @param parm: STATE,CITY,POST_NO3 作为可选的where条件
     * @return TParm
     */
    public TParm selectdata(TParm parm){
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
     * 得到省
     * @param postNo3 String
     * @return String
     */
    public String getState(String postNo3)
    {
        if(postNo3 == null || postNo3.length() == 0)
            return "";
        TParm parm = new TParm();
        parm.setData("POST_CODE",postNo3);
        return getResultString(query("getState",parm),"STATE");
    }

    /**
     * 得到市
     * @param postNo3 String
     * @return String
     */
    public String getCity(String postNo3)
    {
        if(postNo3 == null || postNo3.length() == 0)
            return "";
        TParm parm = new TParm();
        parm.setData("POST_CODE",postNo3);
        return getResultString(query("getCity",parm),"CITY");
    }

    /**
     * 得到省市
     * @param postCode String
     * @return TParm
     */
    public TParm getProvinceCity(String postCode) {
        //System.out.println("===========SYSPostTool===================="+postCode);
        TParm parm = new TParm();
        parm.setData("POST_CODE", postCode);
        TParm result = query("selectall", parm);
        return result;
    }
    /**
     * 得到邮编
     * 20111230 zhangp
     * @param code
     * @return
     */
    public TParm getPostCode(String code){
    	TParm parm = new TParm();
    	String sql = "SELECT POST_CODE,ENNAME,CITY_PY FROM SYS_POSTCODE WHERE ENNAME LIKE '%"+code+
    	"%' OR POST_CODE LIKE '%"+code+"%' OR CITY_PY LIKE '%"+code+"%'";
    	parm = (TParm) TJDODBTool.getInstance().select(sql);
    	return parm;
    }

}
