/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ����ICD</p>
*
* <p>Description:����ICD </p>
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
     * ʵ��
     */
    public static SYSOperationicdTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSOperationicdTool
     */
    public static SYSOperationicdTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSOperationicdTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSOperationicdTool()
    {
        setModuleName("sys\\SYSOperationicdModule.x");
        onInit();
    }
    /**
     * ��ʼ�����棬��ѯ���е�����
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
     * ��������ICD��������ѯ����
     * @param OPERATION_ICD String ����ICD����
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
     * ����ָ������ICD����õ�����
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
    	   result.setErr(-1,"����ICD���� "+OPERATION_ICD+" �Ѿ�����!");
           return result ;
       }

       return result;
	}
	/**
	 * ��������ICD�����table1����
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
	 * �ж�icd_�Ƿ����
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
	 * ����ICD_CODEɾ��table1������
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
	 * ����TAG_CODEɾ��table1������
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
     * �ж��Ƿ��������
     * @param OPERATION_ICD String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsPosition(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
        //System.out.println("existsPosition"+OPERATION_ICD);
        return getResultInt(query("existsICD",parm),"COUNT") > 0;
    }
	/**
     * ����ָ��OPERATION_ICD����
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
        	result.setErr(-1,"����ICD���� "+OPERATION_ICD+" �ոձ�ɾ����");
            return result ;
        }

        return result;
    }
    /**
     * ɾ��ָ�����ְ������
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
