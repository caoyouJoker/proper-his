package jdo.inv;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: 手术与手术包对应</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: </p>
 *
 * @author fux
 * @version 4.0
 */
public class INVOpeAndPackageTool extends TJDOTool  {
    /**
     * 实例
     */
    public static INVOpeAndPackageTool instanceObject;  

    /**
     * 构造器
     */
    public INVOpeAndPackageTool() {
        setModuleName("inv\\INVOpeAndPackageModule.x");  
        onInit();
    }

    /**
     * 得到实例 
     *
     * @return IndPurPlanMTool
     */
    public static INVOpeAndPackageTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVOpeAndPackageTool();
        return instanceObject;
    }

    /**
     * 新增手术对应手术包
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */ 
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn); 
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;
    }

    /**
     * 更新手术对应手术包
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
       TParm result = this.update("update", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

   /**
     * 删除手术对应手术包
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
       TParm result = this.update("delete", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
    
    
    /**
     * 新增手术包申请
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertAutoRequest(TParm parm, TConnection conn) {
    	List<TParm> parmList = getTParmList(parm);
    	TParm result = new TParm();
    	for (TParm tParm : parmList) {    
    		result = this.update("insertAutoRequest", tParm, conn);
			if (result.getErrCode() < 0) { 
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                    + result.getErrName());   
				conn.close();  
				return result;
			}
		}     
        return result;
    }

    /**
     * 更新手术包申请 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateAutoRequest(TParm parm, TConnection conn) {       
   	List<TParm> parmList = getTParmList(parm);
	TParm result = new TParm();
	for (TParm tParm : parmList) {      
		result = this.update("updateAutoRequest", tParm, conn);
		if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());   
			conn.close();
			return result;
		}
	}     
    return result;
   }
    
    /**
     * 删除手术包申请 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteAutoRequest(TParm parm, TConnection conn) {
       TParm result = this.update("deleteAutoRequest", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
    
    /**
     * 将多条parm转换成parmList
     * @param parm
     * @return
     */
    public List<TParm> getTParmList(TParm parm){
    	// 返回结果list
    	List<TParm> parmList = new ArrayList<TParm>();
    	TParm tempParm;
    	String[] names = parm.getNames();
    	// 一个parm里存放多少条数据
    	int count = parm.getCount(names[0]);
    	for (int i=0; i<count; i++) {
			tempParm = new TParm();
			for (String name : names) {
				tempParm.setData(name, parm.getData(name, i));
			}
			parmList.add(tempParm);
		}
    	
    	return parmList;
    }

}
