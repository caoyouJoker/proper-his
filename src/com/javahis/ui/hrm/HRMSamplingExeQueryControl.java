package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ������ִ�в�ѯͳ��
 * </p>
 * 
 * <p>
 * Description: ������ִ�в�ѯͳ��
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
 * @author wangb 2016.7.4
 * @version 1.0
 */
public class HRMSamplingExeQueryControl extends TControl {

	private TTable table;
	private TTextFormat company;// ��������TTextFormat
	private TTextFormat contract;// �����ͬTTextFormat
	private HRMContractD contractD;// ��ͬ����
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		this.initComponent();
		this.onInitPage();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		company = (TTextFormat) this.getComponent("COMPANY_CODE");
		table = (TTable) this.getComponent("TABLE");
		addSortListener(table);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void onInitPage() {
		// ȡ�õ�ǰ����
		Timestamp nowDate = SystemTool.getInstance().getDate();
		// �趨Ĭ��չ������
		this.setValue("START_DATE", nowDate);
		this.setValue("END_DATE", nowDate);
		table.setParmValue(new TParm());

		contractD = new HRMContractD();
		contractD.onQuery("", "", "");

		// ��ѯ������Ϣ(��ʱֻ��ѯһ���ٴ�)
		TParm companyData = HRMCompanyTool.getInstance()
				.selectCompanyComboByRoleType("PIC");
		company.setPopupMenuData(companyData);
		company.setComboSelectRow();
		company.popupMenuShowData();
	}

	/**
	 * ��������ѡ�¼�
	 */
	public void onCompanyChoose() {
		String companyCode = this.getValueString("COMPANY_CODE");
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm == null || contractParm.getCount() <= 0
				|| contractParm.getErrCode() != 0) {
			this.messageBox_("���������޺�ͬ����");
			return;
		}

		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		String contractCode = contractParm.getValue("ID", 0);
		if (StringUtils.isEmpty(contractCode)) {
			this.messageBox_("��ѯʧ��");
			return;
		}
		contract.setValue(contractCode);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			this.messageBox("������ֹʱ�䲻��Ϊ��");
			return;
		}
		
		// ����ʱ��
		startDate = startDate.substring(0, 10).replace("-", "");
		endDate = endDate.substring(0, 10).replace("-", "");
		// ������Ϣ
		String companyCode = this.getValueString("COMPANY_CODE");
		// ��ͬ
		String contractCode = this.getValueString("CONTRACT_CODE");
		// ������
		String mrNo = this.getValueString("MR_NO");
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.MR_NO,C.PAT_NAME,B.STAFF_NO,A.ORDER_DESC,A.BLOOD_DATE,A.BLOOD_USER ");
		sbSql.append(" FROM MED_APPLY A, HRM_CONTRACTD B, HRM_PATADM C ");
		sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO AND B.MR_NO = C.MR_NO ");
		sbSql.append(" AND B.CONTRACT_CODE = C.CONTRACT_CODE AND A.BLOOD_USER IS NOT NULL AND A.BLOOD_DATE >= TO_DATE('");
		sbSql.append(startDate);
		sbSql.append("000000','YYYYMMDDHH24MISS') AND A.BLOOD_DATE <= TO_DATE('");
		sbSql.append(endDate);
		sbSql.append("235959','YYYYMMDDHH24MISS') ");
		
		if (StringUtils.isNotEmpty(companyCode)) {
			sbSql.append(" AND B.COMPANY_CODE = '");
			sbSql.append(companyCode);
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(contractCode)) {
			sbSql.append(" AND B.CONTRACT_CODE = '");
			sbSql.append(contractCode);
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(mrNo)) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(mrNo);
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY BLOOD_DATE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ��������ʧ��");
			err("��ѯ��������ʧ��:" + result.getErrText());
		} else if (result.getCount() < 1) {
			table.setParmValue(new TParm());
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
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}

	/**
	 * ���
	 */
	public void onClear() {
		clearValue("COMPANY_CODE;CONTRACT_CODE;MR_NO");
		this.onInitPage();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "����ִ�в�ѯͳ��");
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ����ָ���������������е�index
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �����������ݣ���Vectorת��Parm
	 * 
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
}
