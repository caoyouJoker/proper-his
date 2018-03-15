package com.javahis.ui.ins;

import java.sql.Timestamp;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

import jdo.ekt.EKTIO;
import jdo.ins.INSTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title:��������ҽ������
 * </p>
 * 
 * <p>
 * Description:��������ҽ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author Wanglong 20120921
 * @version 1.0
 */
public class INSPatHistoryControl extends TControl {

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		this.setValue("START_DATE", transDate);
		this.setValue("END_DATE", transDate);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		String mrNo = this.getValueString("MR_NO");
		if (StringUtil.isNullString(mrNo)) {
			this.messageBox("����д������");
			return;
		}
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		
		// modify by huangtt 20160930 EMPI���߲�����ʾ start
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
		// modify by huangtt 20160930 EMPI���߲�����ʾ end
		
		this.setValue("MR_NO", mrNo);
		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("MR_NO", mrNo);

		TParm patHistoryInfo = INSTool.getInstance().queryPatHistoryInfo(parm);
		if (patHistoryInfo.getErrCode() < 0) {
			messageBox(patHistoryInfo.getErrText());
			return;
		}
		if (patHistoryInfo.getCount() <= 0) {
			messageBox("E0008");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			// this.clearValue("MR_NO");
			return;
		}
		this.clearValue("TABLE");
		this.callFunction("UI|TABLE|setParmValue", patHistoryInfo);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("MR_NO");
		this.callFunction("UI|TABLE|setParmValue", new TParm());
	}

	/**
	 * ��������
	 * 
	 * @return boolean
	 */
	public void onReadEKT() {
		// ��ȡҽ�ƿ�����
		TParm EKTTemp = EKTIO.getInstance().TXreadEKT();
		if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		this.setValue("MR_NO", EKTTemp.getValue("MR_NO"));
		

		
		// modify by huangtt 20160930 EMPI���߲�����ʾ start
		String mrNo = this.getValueString("MR_NO");
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160930 EMPI���߲�����ʾ end
		
		
		
	}
}