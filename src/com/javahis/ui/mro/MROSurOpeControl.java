package com.javahis.ui.mro;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title:���ҽʦ�����ʽ��ϸ </p>
 * 
 * <p> Description:���ҽʦ�����ʽ��ϸ </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wangbin 2014-7-24
 * @version 1.0
 */
public class MROSurOpeControl extends TControl {
	
	private TTable table; // ����ҽʦ�����ʽ��ϸ

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // ����ҽʦ�����ʽ��ϸ
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
	}
	
	/**
	 * ��ѯ����ҽʦ�����ʽ��ϸ
	 */
	public void onQuery() {
		// ��֤��ѯ����
		if (StringUtils.isEmpty(this.getValueString("OUT_DATE_START"))
				|| StringUtils.isEmpty(this.getValueString("OUT_DATE_END"))) {
			this.messageBox("�������Ժ��������");
			return;
		}
		
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT A.DEPT_CODE, A.MR_NO, C.PAT_NAME, C.SEX_CODE, ");
		sbSql.append(" FLOOR(MONTHS_BETWEEN( SYSDATE, C.BIRTH_DATE) / 12) AS AGE, A.IN_DATE, A.DS_DATE, B.OP_CODE, ");
		sbSql.append(" B.OP_DESC, B.OP_DATE, B.MAIN_SUGEON, B.AST_DR1, B.ANA_DR, '' AS PFT_DR ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 = 'B' ");
		sbSql.append(" AND B.MAIN_FLG = 'Y' ");
		sbSql.append(" AND A.DS_DATE BETWEEN TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_START").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') AND TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_END").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') ");
		
		// ���ұ���
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(this.getValueString("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// ������
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(this.getValueString("MR_NO").trim());
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY A.DEPT_CODE, A.MR_NO, B.SEQ_NO");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox("��ѯ���ݳ��ִ���");
			return;
		}
		
		if (result.getCount() <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("δ�鵽����");
			return;
		}
		
		this.table.setParmValue(result);
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
			// modify by huangtt 20160929 EMPI���߲�����ʾ start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			this.setValue("MR_NO", mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				this.setValue("MR_NO", pat.getMrNo());// ������
			}
			// modify by huangtt 20160929 EMPI���߲�����ʾ start
			this.onQuery();
		}
	}
	
	/**
	 * ��������
	 */
	public void onExport() {
		if (this.table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "���ҽʦ�����ʽ��ϸ");
		} else {
			this.messageBox("û����Ҫ����������");
			return;
		}
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		this.onInit();
		this.setValue("DEPT_CODE", "");
		this.setValue("MR_NO", "");
		this.table.setParmValue(new TParm());
	}
    	
}
