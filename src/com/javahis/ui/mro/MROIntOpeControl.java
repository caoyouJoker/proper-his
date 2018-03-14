package com.javahis.ui.mro;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title:����ҽʦ�����ʽ��ϸ </p>
 * 
 * <p> Description:����ҽʦ�����ʽ��ϸ </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company:bluecore </p>
 * 
 * @author wangbin 2014-7-24
 * @version 1.0
 */
public class MROIntOpeControl extends TControl {
	
	private TTable table; // ����ҽʦ�����ʽ��ϸ
	private String initTableHeader; // ҳ����ԭʼ����
	private String initTableParmMap; // ҳ����ԭʼParmMap
	private String initTableColHorAlign; // ҳ�������ݶ��뷽ʽ

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // ����ҽʦ�����ʽ��ϸ
		initTableHeader = this.table.getHeader();
		initTableParmMap = this.table.getParmMap();
		this.initTableColHorAlign = "0,left;1,center;2,left;3,left;4,right;5,center;6,center;7,right";
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
		sbSql.append(" SELECT MAX(T.OPE_COUNT) AS MAX_OPE_COUNT FROM (");
		sbSql.append(" SELECT COUNT(A.MR_NO) AS OPE_COUNT,A.CASE_NO ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D,SYS_OPERATIONICD E ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 <> 'B' ");
		sbSql.append(" AND B.OP_CODE=E.OPERATION_ICD ");
		sbSql.append(" AND (E.OPERATION_ICD <='88.50000' OR E.OPERATION_ICD >='88.58000') ");
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
			sbSql.append(this.getValueString("MR_NO"));
			sbSql.append("' ");
		}
		sbSql.append(" GROUP BY A.CASE_NO ");
		sbSql.append(" ORDER BY OPE_COUNT DESC) T ");
		
		// ȡ��ָ����ѯ��Χ�����������������ֵ�������ֵΪ��׼���������
		TParm maxOpeCountParm = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (maxOpeCountParm.getErrCode() < 0) {
			err("ERR:" + maxOpeCountParm.getErrCode() + maxOpeCountParm.getErrText()
					+ maxOpeCountParm.getErrName());
			this.messageBox("��ѯ���ݳ��ִ���");
			return;
		}
		
		if (maxOpeCountParm.getCount() <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("δ�鵽����");
			return;
		}
		
		sbSql = new StringBuilder();
		sbSql.append(" SELECT DISTINCT * FROM (");
		sbSql.append(" SELECT  A.CASE_NO,A.DEPT_CODE, A.MR_NO, A.IPD_NO, C.PAT_NAME, C.SEX_CODE, ");
		sbSql.append(" FLOOR(MONTHS_BETWEEN( SYSDATE, C.BIRTH_DATE) / 12) AS AGE, A.IN_DATE, A.DS_DATE, ");
		sbSql.append(" CASE WHEN TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) < 1 THEN 1 ELSE TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) END AS REAL_STAY_DAYS ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D,SYS_OPERATIONICD E ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 <> 'B' ");
		sbSql.append(" AND B.OP_CODE=E.OPERATION_ICD ");
		sbSql.append(" AND (E.OPERATION_ICD <='88.50000' OR E.OPERATION_ICD >='88.58000') ");
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
			sbSql.append(this.getValueString("MR_NO"));
			sbSql.append("' ");
		}
		sbSql.append(" ) T ");
		sbSql.append(" ORDER BY T.DEPT_CODE, T.MR_NO, T.IPD_NO ");
		
		// ȡ��ָ����ѯ��Χ�ڲ�����Ϣ
		TParm patInfoParm = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (patInfoParm.getErrCode() < 0) {
			err("ERR:" + patInfoParm.getErrCode() + patInfoParm.getErrText()
					+ patInfoParm.getErrName());
			this.messageBox("��ѯ���ݳ��ִ���");
			return;
		}
		
		int patCount = patInfoParm.getCount();
		if (patCount <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("δ�鵽����");
			return;
		}
		
		// ���ݼ���������ѯ����
		sbSql = new StringBuilder();
		sbSql.append(" SELECT * FROM ( ");
		sbSql.append(" SELECT A.DEPT_CODE, A.CASE_NO, A.MR_NO, A.IPD_NO, C.PAT_NAME, C.SEX_CODE, ");
		sbSql.append(" FLOOR(MONTHS_BETWEEN( SYSDATE, C.BIRTH_DATE) / 12) AS AGE, A.IN_DATE, A.DS_DATE, ");
		sbSql.append(" CASE WHEN TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) < 1 THEN 1 ELSE TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) END AS REAL_STAY_DAYS, ");
		sbSql.append(" B.OP_CODE, B.OP_DESC, B.OP_DATE, B.SEQ_NO, NVL(B.MAIN_FLG,'N') AS MAIN_FLG, B.MAIN_SUGEON ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D,SYS_OPERATIONICD E ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 <> 'B' ");
		sbSql.append(" AND B.OP_CODE = E.OPERATION_ICD ");
		// ���ݹ��ҹ涨88.50000-88.58000��Χ�ڵ�����������������ǽ�������
		sbSql.append(" AND (E.OPERATION_ICD <= '88.50000' OR E.OPERATION_ICD >= '88.58000')");
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
			sbSql.append(this.getValueString("MR_NO"));
			sbSql.append("' ");
		}
		
		sbSql.append(") T");
		sbSql.append(" ORDER BY T.DEPT_CODE, T.MR_NO, T.IPD_NO, T.MAIN_FLG DESC, T.SEQ_NO");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox("��ѯ���ݳ��ִ���");
			return;
		}
		
		int dataCount = result.getCount();
		if (dataCount <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("δ�鵽����");
			return;
		}
		
		// ȡ�ò�ѯ�ڼ����������������Ĳ��˵���������
		int maxOpeCount = maxOpeCountParm.getInt("MAX_OPE_COUNT", 0);
		// ���б��ı���
		String tableHeader = this.initTableHeader + ";";
		// ���б���ParmMap
		String parmMap = this.initTableParmMap + ";";
		// ���б������ݶ��뷽ʽ
		String tableColHorAlign = this.initTableColHorAlign + ";";
		
		for (int i = 1; i <= maxOpeCount; i++) {
			tableHeader = tableHeader + "��ʽ" + i + ",200;";
			tableHeader = tableHeader + "����" + ",80;";
			
			parmMap = parmMap + "OP_DESC" + i + ";OP_CODE" + i + ";";
		}
		
		tableHeader = tableHeader + "����,80,OPERATOR_CODE";
		parmMap = parmMap + "MAIN_SUGEON";
		
		// �������ݵĸ����������ݵĶ��뷽ʽ
		for (int j = 1; j <= maxOpeCount*2 + 1; j++) {
			tableColHorAlign = tableColHorAlign + (7 + j) + ",left;";
		}
		
		// �������ñ�����
		this.table.setHeader(tableHeader);
		// �����趨���ParmMap
		this.table.setParmMap(parmMap);
		// �����趨������ݶ��뷽ʽ
		this.table.setColumnHorizontalAlignmentData(tableColHorAlign);
		
		for (int i = 0; i < patCount; i++) {
			int count = 0;
			for (int j = 0; j < dataCount; j++) {
				if (StringUtils.equals(patInfoParm.getValue("CASE_NO", i), result.getValue("CASE_NO", j))) {
					count = count + 1;
					patInfoParm.setData("OP_DESC" + count, i, result.getValue("OP_DESC", j));
					patInfoParm.setData("OP_CODE" + count, i, result.getValue("OP_CODE", j));
					// ��ͬ����ŵ����ݣ�result�еĵ�һ��Ϊ����������Ϊ��ѯ�������������Ƿ�������������
					if (count == 1) {
						// ����ʱ��
						patInfoParm.setData("OP_DATE", i, StringTool.getString(result.getTimestamp("OP_DATE", j), "yyyy/MM/dd"));
						// ����
						patInfoParm.setData("MAIN_SUGEON", i, result.getValue("MAIN_SUGEON", j));
					}
				}
			}
			
			// ����maxOpeCount��ֵ���ʣ�������
			for (int k = count + 1; k <= maxOpeCount; k++) {
				patInfoParm.setData("OP_DESC" + k, i, "");
				patInfoParm.setData("OP_CODE" + k, i, "");
				
			}
		}
		
		this.table.setParmValue(patInfoParm);
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
			ExportExcelUtil.getInstance().exportExcel(table, "����ҽʦ�����ʽ��ϸ");
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
