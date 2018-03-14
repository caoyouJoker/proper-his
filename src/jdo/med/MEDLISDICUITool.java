package jdo.med;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p> Title:项目编码与检验编码对应 </p>
 * 
 * <p> Description: 项目编码与检验编码对应 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 *@author 2014.03.10
 * @version 1.0
 */
public class MEDLISDICUITool  extends TJDOTool {
    /**
     * 实例
     */
    public static MEDLISDICUITool instanceObject;
    
    
    /**
	 * 构造器
	 */
	public MEDLISDICUITool() {
		setModuleName("med\\MEDLISDICUIModule.x");
		onInit();
	}
    /**
     * 得到实例
     * @return BILInvrcptTool
     */
    public static MEDLISDICUITool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEDLISDICUITool();
        return instanceObject;
    }
    
    /**
     * @return TParm
     */
    public TParm insertData(TParm parm) {
 	   
        TParm result = update("insertdata",parm);
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * @return TParm
     */
    public TParm updateData(TParm parm) {
    	
    	TParm result = update("updatedata",parm);
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * @return TParm
     */
    public TParm deleteData(TParm parm) {
    	
    	TParm result = update("deletedata",parm);
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * @return TParm
     */
    public TParm selectDataAll() {
    	
    	TParm result = query("selectdataAll");
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * @return TParm
     */
    public TParm selectData(TParm parm) {
    	
    	TParm result = query("selectdata",parm);
    	if(result.getErrCode() < 0)
    	{
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }

    
    /**
     * @return TParm
     */
    public TParm selectDataid(TParm parm) {
    	String where ="";
    	if(!"".equals(parm.getValue("MAP_ID"))&&parm.getValue("MAP_ID")!=null){
    		where=" AND MAP_ID='"+parm.getValue("MAP_ID")+"' ";
		}else if(!"".equals(parm.getValue("MAP_DESC"))&&parm.getValue("MAP_DESC")!=null){
			where=" AND MAP_DESC LIKE '%"+parm.getValue("MAP_DESC")+"%'";
		}
		
		
		String sql="SELECT MAP_ID,MAP_DESC,SEQ,LIS_ID,LIS_DESC,PY1, "+
			       "TYPE,MAP_TYPE,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM "+
			       "FROM　MED_LIS_MAP WHERE 1=1  "+where ; 
    	//System.out.println("xxxxx"+sql);
			     TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
    }
}
