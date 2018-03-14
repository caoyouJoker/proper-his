package com.javahis.ui.mro;

import jdo.mro.MRORecordTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

public class MRORecordDataQueryControl extends TControl {

	private TTable table;

	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		this.onInitPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// �趨Ĭ��չ������
		this.setValue("START_DATE", todayDate);
		this.setValue("END_DATE", todayDate);
		this
				.clearValue("DEPT_CODE;MR_NO;PAT_NAME;VS_DR_CODE;OUT_MAIN_DIAG;OP_MAIN_DIAG");
		table.setParmValue(new TParm());
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		if (!checkData()) {
			return;
		}

		String startDate = this.getValueString("START_DATE").substring(0, 10);
		String endDate = this.getValueString("END_DATE").substring(0, 10);
		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("VS_DR_CODE", this.getValueString("VS_DR_CODE"));
		parm.setData("OUT_MAIN_DIAG", this.getValueString("OUT_MAIN_DIAG"));
		parm.setData("OP_MAIN_DIAG", this.getValueString("OP_MAIN_DIAG"));

		// ��ѯ������ҳ����
		TParm result = MRORecordTool.getInstance().queryMroRecordData(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("��������");
			return;
		} else {
			table.setParmValue(result);
		}
	}

	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("���޴˲�����");
				return;
			}

			// modify by huangtt 20160930 EMPI���߲�����ʾ start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			}
			// modify by huangtt 20160930 EMPI���߲�����ʾ end

			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.setValue("PAT_NAME", pat.getName());
		}
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.onInitPage();
	}

	/**
	 * ���ݺ�������֤
	 * 
	 * @return
	 */
	private boolean checkData() {
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		if (StringUtils.isEmpty(startDate)) {
			this.messageBox("����д��ѯ��ʼ����");
			return false;
		}
		if (StringUtils.isEmpty(endDate)) {
			this.messageBox("����д��ѯ��ֹ����");
			return false;
		}

		return true;
	}

	/**
	 * ���
	 */
	public void onExport() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}

		ExportExcelUtil.getInstance().exportExcel(table, "������ҳ");
	}
}
