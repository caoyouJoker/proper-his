package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.JavaHisDebug;
/**
*
* <p>Title: 诊断tool
*
* <p>Description: 诊断tool</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20080911
* @version 1.0
*/
public class DiagRecTool extends TJDOTool {
	 /**
     * 实例
     */
    public static DiagRecTool instanceObject;
    /**
     * 得到实例
     * @return DiagRecTool
     */
    public static DiagRecTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DiagRecTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public DiagRecTool() {
        setModuleName("opd\\OPDDiagRecModule.x");

        onInit();
    }

    /**
     * 新增医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
    	//System.out.println("in diagrec"+parm);
        TParm result = new TParm();
        result = update("insertdata", parm,connection);
        
        //System.out.println("wrong=============="+parm.getValue("ICD_TYPE").length());
        
        if (result.getErrCode() < 0) {
        	//System.out.println("err============"+result.getErrText());
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
    	//System.out.println("diagparm"+parm);
        TParm result = query("selectdata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //System.out.println("diagrecresult"+result);
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
		//System.out.println("in onInsert==========="+count);
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
		TParm result = onDelete(parm.getParm(DiagRecList.DELETED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//System.out.println("in diagrecTool onSave=========="+parm.getParm(DiagRecList.NEW));
		result = onInsert(parm.getParm(DiagRecList.NEW),connection);
		
		if (result.getErrCode() < 0) {
			//System.out.println("in diagrecTool ==============");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//System.out.println("-------------->" + parm.getParm(DiagRecList.MODIFIED));
		result = onUpdate(parm.getParm(DiagRecList.MODIFIED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public static void main(String args[]) {
		JavaHisDebug.initClient();
		TParm parm=new TParm();
		parm.setData("CASE_NO","ABC");
		//System.out.println(DiagRecTool.getInstance().selectdata(parm));

	}
	/**
     * 查询医保数据
     * @param parm
     * @return
     * ========pangben 2012-2-14
     */
    public TParm queryInsData(TParm parm){
    	//System.out.println("diagparm"+parm);
        TParm result = query("queryInsData",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //System.out.println("diagrecresult"+result);
        return result;
    }
}
