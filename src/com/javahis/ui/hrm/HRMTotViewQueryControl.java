package com.javahis.ui.hrm;

import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 一期临床体检信息
 * </p>
 * 
 * <p>
 * Description: 一期临床体检信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.7.13
 * @version 1.0
 */
public class HRMTotViewQueryControl extends TControl {
	
	private TTable table;
	private TParm parameter;
	private TWord word;
	
	/**
	 * 初始化方法
	 */
    public void onInit() {
    	super.onInit();
    	table = (TTable)getComponent("TABLE");
    	word = (TWord)getComponent("WORD");
    	word.setCanEdit(false);
    	Object obj = this.getParameter();
    	if (null != obj) {
			if (obj instanceof TParm) {
				parameter = (TParm) obj;
				this.onQuery();
			}
		}
    }
    
    /**
     * 查询
     */
    public void onQuery() {
		String sql = "SELECT  A.*,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE,B.BIRTHDAY,B.COMPANY_CODE, "
				+ "B.ID_NO,B.TEL,B.PACKAGE_CODE,B.REPORT_STATUS,B.PAT_DEPT,B.DISCNT,B.MARRIAGE_CODE,C.ROLE_TYPE "
				+ " FROM HRM_ORDER A, HRM_PATADM B ,HRM_CONTRACTD C "
				+ " WHERE A.DEPT_ATTRIBUTE = '#' "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND A.SETMAIN_FLG = 'Y' "
				+ " AND B.CONTRACT_CODE = C.CONTRACT_CODE AND B.MR_NO = C.MR_NO "
				+ " AND A.EXEC_DR_CODE IS NOT NULL AND B.CASE_NO IN ('@') ORDER BY B.CASE_NO DESC ";
		sql = sql.replace("#", parameter.getValue("DEPT_ATTRIBUTE"));
		sql = sql.replace("@", parameter.getValue("CASE_NO"));
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询总检信息错误");
			err("ERR:" + result.getErrText());
			return;
		} else {
			table.setParmValue(result);
		}
    }
    
    /**
     * TABLE双击事件，选择病患的医嘱
     */
	public void onChoosePat() {
		TParm parm = table.getParmValue();
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		if (parm == null) {
			return;
		}
		String mrNo = parm.getValue("MR_NO", row);
		String caseNo = parm.getValue("CASE_NO", row);
		if (StringUtil.isNullString(mrNo)) {
			return;
		}
		// 配置打开结构化病历的参数，打开结构化病历的数据
		String tempName = parm.getValue("MR_CODE", row);
		TParm emrParm = new TParm();
		emrParm.setData("MR_CODE", tempName);
		emrParm.setData("CASE_NO", caseNo);
		emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
		emrParm.setData("FILE_TITLE_TEXT", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()));
		emrParm.setData("FILE_TITLEENG_TEXT", "TEXT", Manager.getOrganization()
				.getHospitalENGFullName(Operator.getRegion()));
		emrParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this
				.getValueString("MR_NO"));
		emrParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "");
		emrParm.setData("FILE_128CODE", "TEXT", this.getValueString("MR_NO"));
		emrParm.setData("TEMPLET_PATH", emrParm.getValue("TEMPLET_PATH"));
		word.onNewFile();
		word.update();
		String filePath = emrParm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\"
				+ emrParm.getValue("FILE_PATH")
				: emrParm.getValue("FILE_PATH");
		String fileName = emrParm.getValue("FILE_NAME");
		word.onOpen(filePath, fileName, 3, false);
		word.setNodeIndex(-1);
		boolean mSwitch = word.getMessageBoxSwitch();
		word.setMessageBoxSwitch(false);
		word.onSave();
		word.setMessageBoxSwitch(mSwitch);
		word.setWordParameter(emrParm);
		word.setCanEdit(false);
	}
}
