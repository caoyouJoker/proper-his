package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 过敏史tool
*
* <p>Description: 过敏史tool</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20080911
* @version 1.0
*/
public class DrugAllergyTool extends TJDOTool {
	/**
     * 实例
     */
    public static DrugAllergyTool instanceObject;
    /**
     * 得到实例
     * @return DrugAllergyTool
     */
    public static DrugAllergyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DrugAllergyTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public DrugAllergyTool() {
        setModuleName("opd\\OPDDrugAllergyModule.x");

        onInit();
    }

    /**
     * 新增医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertdata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
	 /**
     * 判断是否存在数据
     * @param TParm parm
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsOrder(TParm parm){
        return getResultInt(query("existsOrder",parm),"COUNT") > 0;
    }
    /**
     * 更新数据
     * @param parm
     * @return
     */
    public TParm updatedata(TParm parm, TConnection connection){
    	TParm result = new TParm();
        result = update("updatedata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param parm
     * @return
     */
    public TParm deletedata(TParm parm, TConnection connection){
    	TParm result = new TParm();
        result = update("deletedata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检索数据
     * @param parm
     * @return
     */
    public TParm selectdata(TParm parm){
        TParm result = query("selectdata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //System.out.println("allergyresult"+result);
        return result;
    }
    /**
	 * 删除
	 * @param parm
	 * @return result
	 */
	public TParm onDelete(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.deletedata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}

		return result;
	}

	/**
	 * 插入
	 * @param parm
	 * @return result
	 */
	public TParm onInsert(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * 更新
	 * @param parm
	 * @return result
	 */
	public TParm onUpdate(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.updatedata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * odo异动主入口
	 * @param parm
	 * @param connection
	 * @return result 保存结果
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(DrugAllergyList.DELETED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(DrugAllergyList.NEW),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onUpdate(parm.getParm(DrugAllergyList.MODIFIED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 判断是否是过敏史中的药品    add by huangtt 20150506
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public String CBDDrugAllergyCheck(String mrno,String drugcode,String drugname){
		String message = "";
		String sql = "SELECT CLASSIFY1,CLASSIFY2 FROM SYS_RULE WHERE RULE_TYPE='PHA_RULE'";
		TParm classifyParm = new TParm(TJDODBTool.getInstance().select(sql));
		int classify1 = classifyParm.getInt("CLASSIFY1", 0);
		int classify2 = classifyParm.getInt("CLASSIFY2", 0);
		String mcat1code = drugcode.substring(0,classify1);
		String mcat2code = drugcode.substring(0,classify1+classify2);
		sql = "SELECT COUNT(CASE_NO) AS MCNT" +
				" FROM OPD_DRUGALLERGY " +
				" WHERE MR_NO='"+mrno+"' " +
				" AND ((DRUG_TYPE='B' AND DRUGORINGRD_CODE='"+drugcode+"') OR " +
				" (DRUG_TYPE='D' AND DRUGORINGRD_CODE='"+mcat1code+"') OR" +
				" (DRUG_TYPE='E' AND DRUGORINGRD_CODE='"+mcat2code+"'))";
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int mcnt = parm.getInt("MCNT", 0);
		if(mcnt > 0){
			message = "该患者对于"+drugname+"有过敏史"; 			
		}
		return message;
	}
}
