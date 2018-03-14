package jdo.inv;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��������������Ӧ</p>
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
     * ʵ��
     */
    public static INVOpeAndPackageTool instanceObject;  

    /**
     * ������
     */
    public INVOpeAndPackageTool() {
        setModuleName("inv\\INVOpeAndPackageModule.x");  
        onInit();
    }

    /**
     * �õ�ʵ�� 
     *
     * @return IndPurPlanMTool
     */
    public static INVOpeAndPackageTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVOpeAndPackageTool();
        return instanceObject;
    }

    /**
     * ����������Ӧ������
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
     * ����������Ӧ������
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
     * ɾ��������Ӧ������
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
     * ��������������
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
     * �������������� 
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
     * ɾ������������ 
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
     * ������parmת����parmList
     * @param parm
     * @return
     */
    public List<TParm> getTParmList(TParm parm){
    	// ���ؽ��list
    	List<TParm> parmList = new ArrayList<TParm>();
    	TParm tempParm;
    	String[] names = parm.getNames();
    	// һ��parm���Ŷ���������
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
