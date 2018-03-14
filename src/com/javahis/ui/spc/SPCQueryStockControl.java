package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCQueryStockTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��ѯ������Control
 * </p>
 * 
 * <p>
 * Description:��ѯ������Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2014-05-16
 * @version 1.0
 */
public class SPCQueryStockControl extends TControl {

	/**
	 * TABLE�ؼ�
	 */
	private TTable table;

	/**
	 * �����ʼ��
	 */
	public void onInit() {
		// ��ʼ���ؼ���Ĭ��ֵ
		table = getTable("TABLE");
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		this.setValue("TRANDATE", date.toString().substring(0, 7));
		this.setValue("DEPT_CODE", Operator.getDept());
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// ��װ��ѯ����
		TParm parm = new TParm();
		String dept_code = this.getValueString("DEPT_CODE");
		String trandate = this.getValueString("TRANDATE");
		// ������(ÿ����25��)
		if (!StringUtil.isNullString(trandate)) {
			trandate = trandate.replaceAll("-", "").substring(0, 6) + "25";
		} else {
			messageBox("��ѡ���ѯʱ��");
		}
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("TRANDATE", trandate);
		TParm result = SPCQueryStockTool.getInstance().queryStock(parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			messageBox("E0008");
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * ����EXCEL
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(
				table,
				this.getValueString("TRANDATE").replaceAll("-", "").substring(
						0, 7)
						+ "25������");
	}

	/**
	 * ���
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("TRANDATE", date);
		this.setValue("DEPT_CODE", Operator.getDept());
		table.removeRowAll();
	}

	/**
	 * ���TABLE�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

}
