package jdo.inw;

import jdo.inw.INWAssessmentReadTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: ����������</p>
 *
 * <p>Description:����������</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author huzc 2015.10.22
 * @version 1.0
 */

public class INWAssessmentReadTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INWAssessmentReadTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INWAssessmentReadTool
	 */
	public static INWAssessmentReadTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INWAssessmentReadTool();
		return instanceObject;
	}

	/**
	 * ������
	 * 
	 * @author Huzc
	 */
	public INWAssessmentReadTool(){
		setModuleName("inw\\INWAssessmentReadModule.x");
		onInit();
	}
	
	/**
	 * �������ݷ���
	 * 
	 * @author Huzc
	 */
	public TParm insertINWAssessmentRead(TParm parm){
		TParm result = this.update("insertINWAssessmentRead", parm);
		return result;
	}
	
	/**
	 * ���·���
	 * 
	 * @author Huzc
	 */
	public TParm updateINWAssessmentRead(TParm parm){
		String sql = " UPDATE SYS_EVALUTION_DICT "
			       + " SET EVALUTION_DESC = '"
			       + parm.getValue("EVALUTION_DESC")
			       + "', SHORT_DESC = '"
			       + parm.getValue("SHORT_DESC")
			       + "', PY = '"
			       + parm.getValue("PY")
			       + "', EVALUTION_CLASS = '"
			       + parm.getValue("EVALUTION_CLASS")
			       + "', LOGIC1 = '"
			       + parm.getValue("LOGIC1")
			       + "', SCORE1 = '"
			       + parm.getValue("SCORE1")
			       + "', SCORE_DESC = '"
			       + parm.getValue("SCORE_DESC")
			       + "', OPT_USER = '"
			       + parm.getValue("OPT_USER")
			       + "', OPT_TERM = '"
			       + parm.getValue("OPT_TERM")
			       + "', OPT_DATE = TO_DATE('"
			       + parm.getValue("OPT_DATE")
			       + "','yyyy-mm-dd hh24:mi:ss') WHERE EVALUTION_CODE = '"
			       + parm.getValue("EVALUTION_CODE")
			       + "'";
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(parm.getErrCode() < 0){
			err("ERR:"+result.getErrCode()+result.getErrName()+result.getErrText());
	    	return result;
		}
		return result;
	}
}