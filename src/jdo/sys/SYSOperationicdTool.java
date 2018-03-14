/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 手术ICD</p>
*
* <p>Description:手术ICD </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSOperationicdTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSOperationicdTool instanceObject;
    /**
     * 得到实例
     * @return SYSOperationicdTool
     */
    public static SYSOperationicdTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSOperationicdTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSOperationicdTool()
    {
        setModuleName("sys\\SYSOperationicdModule.x");
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
     * 根据手术ICD代码代码查询数据
     * @param OPERATION_ICD String 手术ICD代码
     * @return TParm
     */
    public TParm selectdata(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
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
     * 新增指定手术ICD代码得到数据
     * @param OPERATION_ICD String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String OPERATION_ICD= parm.getValue("OPERATION_ICD");
       //System.out.println("OPERATION_ICD"+OPERATION_ICD);
       TParm result=new TParm();
       if(!existsPosition(OPERATION_ICD)){
    	   
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"手术ICD代码 "+OPERATION_ICD+" 已经存在!");
           return result ;
       }

       return result;
	}
	/**
	 * 插入手术ICD界面的table1数据
	 * @param parm
	 * @return
	 */
	public TParm insertTable1Data(TParm parm){
		TParm result=new TParm();
		for(int i=0;i<parm.getCount();i++){
			String icdCode=parm.getValue("ICD_CODE",i);
			String tagCode=parm.getValue("TAG_CODE",i);
			result.setData("ID",parm.getValue("ID",i));
			result.setData("ICD_CODE",parm.getValue("ICD_CODE",i));
			result.setData("TAG_CODE",parm.getValue("TAG_CODE",i));
			result.setData("OPT_USER",parm.getValue("OPT_USER",i));
			result.setData("OPT_DATE",parm.getTimestamp("OPT_DATE",i));
			result.setData("OPT_TERM",parm.getValue("OPT_TERM",i));
			if(!existIcdCode(icdCode,tagCode)){
				result=update("insertTable1Data",result);
			}else{
				result=update("updateTable1Data",result);
			}
			if(result.getErrCode()<0)
				return result;
		}
		return result;
	}
	/**
	 * 判断icd_是否存在
	 * @param icdCode
	 * @return
	 */
	public boolean existIcdCode(String icdCode,String tagCode){
		TParm parm=new TParm();
		parm.setData("ICD_CODE",icdCode);
		parm.setData("TAG_CODE",tagCode);
		TParm result = query("existsICDCODE",parm);
		if(result.getCount()>0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 根据ICD_CODE删除table1的数据
	 */
	public TParm deleteTable1Data(String icdCode){
		TParm result=new TParm();
		result.setData("ICD_CODE",icdCode);
		result = update("deleteTable1Data",result);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
		return result;
	}
	/**
	 * 根据TAG_CODE删除table1的数据
	 * @param tagCode
	 * @return
	 */
	public TParm deleteTable1DataByTagCode(String tagCode,String icdCode){
		TParm result=new TParm();
		result.setData("TAG_CODE",tagCode);
		result.setData("ICD_CODE",icdCode);
		result = update("deleteTable1DataByTagCode",result);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
		return result;
	}
	/**
     * 判断是否存在数据
     * @param OPERATION_ICD String
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsPosition(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
        //System.out.println("existsPosition"+OPERATION_ICD);
        return getResultInt(query("existsICD",parm),"COUNT") > 0;
    }
	/**
     * 更新指定OPERATION_ICD数据
     * @param posCode String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String OPERATION_ICD= parm.getValue("OPERATION_ICD");
        //System.out.println("true or false"+existsPosition(OPERATION_ICD));
        if(existsPosition(OPERATION_ICD)){
        	
        	 result = update("updatedata", parm);
        	
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"手术ICD代码 "+OPERATION_ICD+" 刚刚被删除！");
            return result ;
        }

        return result;
    }
    /**
     * 删除指定编号职别数据
     * @param posCode String
     * @return boolean
     */
    public TParm deletedata(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
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
