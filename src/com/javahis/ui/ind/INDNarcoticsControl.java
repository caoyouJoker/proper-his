package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;

import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����Σ��ҩƷ�yӋ����
 * </p>
 * 
 * <p>
 * Description: ����Σ��ҩƷ�yӋ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liuyl 2016.10.31
 * @version 1.0
 */

public class INDNarcoticsControl extends TControl {

	//TTable table;

	TTable table;
	
	TParm newdata;

	// ҽ��ʵ����Ҫ������
	private int flag = 0;

	public INDNarcoticsControl() {
	}

	// ��ʼ��ҳ��
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TTable");
		//table = (TTable) this.getComponent("Table_Order");
		initPage();
	}

	/**
	 * 
	 * ��ʼ���ؼ�Ĭ��ֵ
	 */
	public void initPage() {
		// ��ʼ��ͳ������
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date)

		.substring(0, 4) + "/" + TypeTool.getString(date).substring(5, 7)
				+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (����25)
		setValue("END_DATE", dateTime);

		// ��ʼʱ��(�ϸ���26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");

		// ��������
		setValue("REGION_CODE", "H01");

//		// ����ҽ��Ĭ��ֵ
//		TParm tParm = new TParm();
//		String ORDER_CODE = getValueString("ORDER_CODE");
//		String ORDER_DESC = getValueString("ORDER_DESC");
//		//String sql1 = "SELECT B.ORDER_CODE,B.ORDER_DESC FROM IND_VERIFYIND A, SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE AND B.ORDER_CODE IN ('2S020001','2S020011','2S020051','2S020050','2S040007','2S020075','2S020057','2S020066','2S020067','2S020069','2S020064','2S040030') GROUP BY B.ORDER_CODE,B.ORDER_DESC ";
//		//String sql1 ="SELECT C.ORDER_CODE,C.ORDER_DESC,C.MONITOR_TYPE,C.ENABLE_FLG FROM IND_MONITOR_MED C WHERE C.MONITOR_TYPE='DAN' AND C.ENABLE_FLG='Y'" ;
//		//System.out.println("sql1:::" + sql1);
//		//newdata = new TParm(TJDODBTool.getInstance().select(sql1));
//		tParm.setData("ORDER_CODE", ORDER_CODE);
//		tParm.setData("ORDER_DESC", ORDER_DESC);
////		table.setParmValue(newdata);
//		//System.out.println("newdata:::"+newdata);
//
//		// ����һ�����������ò�������Ϊ��PHA������ҩƷ��
//		TParm parmIn = new TParm();
//		parmIn.setData("CAT1_TYPE", "PHA");

//		this.getTextField("ORDER_CODE")
//				.setPopupMenuParameter(
//						"UD",
//						getConfigParm().newConfig(
//								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
//		// ������ܷ���ֵ����
//		getTextField("ORDER_CODE").addEventListener(
//				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
//
//		// ɾ��ҽ���������ڵ�tableһ�м�¼
//		TTable table = (TTable) this.getComponent("Table_Order");
//		callFunction("UI|" + table + "|addEventListener", table + "->"
//				+ TTableEvent.CLICKED, this, "onDelete");

	}

//	/**
//	 * ���ܷ���ֵ����
//	 * 
//	 * @param tag
//	 * @param obj
//	 */
//	public void popReturn(String tag, Object obj) {
//		TParm parm = (TParm) obj;
//		String order_code = parm.getValue("ORDER_CODE");
//		if (!StringUtil.isNullString(order_code))
//			getTextField("ORDER_CODE").setValue(order_code);
//		String order_desc = parm.getValue("ORDER_DESC");
//		if (!StringUtil.isNullString(order_desc))
//			getTextField("ORDER_DESC").setValue(order_desc);
//	}
//
//	/**
//	 * ��Table_Order�����ҽ��
//	 */
//
//	public void addOrder() {
//		String ORDER_CODE = getValueString("ORDER_CODE");
//		String ORDER_DESC = getValueString("ORDER_DESC");
//
//		TParm tParm = new TParm();
//		TTable table = (TTable) this.getComponent("Table_Order");
////		if (null == ORDER_CODE || "".equals(ORDER_CODE)) {
////			this.messageBox("δ����ҽ����");
////			return;
////		}
//
//		if (flag == 0) {
//			// ���½����ݣ�Ȼ���������table.setData(tParm);��ӵ�table��
//			tParm.setData("ORDER_CODE", ORDER_CODE);
//			tParm.setData("ORDER_DESC", ORDER_DESC);
//			table.addRow(tParm);
//			table.update();
//			flag++;
//
//		} else {
//			for (int j = 0; j < flag+newdata.getCount(); j++) {
//				if (table.getItemString(j, "ORDER_CODE").equals(ORDER_CODE)) {
//					this.messageBox("��ҽ������ӣ�");
//					return;
//				}
//			}
//
//			// ���½����ݣ�Ȼ���������table.setData(tParm);��ӵ�table��
//			tParm.setData("ORDER_CODE", ORDER_CODE);
//			tParm.setData("ORDER_DESC", ORDER_DESC);
//			table.addRow(tParm);
//			table.update();
//			flag++;
//		}
//
//	}
//
//	/*
//	 * ҽ���������ڵ�table�е�ɾ���ͻ�ɾ��һ����¼
//	 */
//	public void onDelete() {
//		TTable table = (TTable) this.getComponent("Table_Order");
//		int delrow = table.getSelectedRow();
//		table.removeRow(delrow);
//		flag--;
//	}

	/**
	 * ����ҩ�������Լ�����ʱ����в�ѯ
	 */
	public void onQuery() {
		TTable table = (TTable) this.getComponent("TTable");
		this.setValue("TOTAL_AMT", "0");
		//TTable table = (TTable) this.getComponent("Table_Order");

		//String order_code = "";
		// ��δѡ���ѯҩƷ
//		if (newdata.getCount() == 0) {
//			this.messageBox("�������ѯҩƷ��");
//			return;
//		}
		
//		 ��ȡorder_code
//		if (newdata.getCount() == 1) {
//		      order_code = " ='" + table.getItemString(0, "ORDER_CODE") + "' ";
//		    } else {
//		      for (int i = 0; i <newdata.getCount() - 1; i++) {
//		        order_code+=  
//		          table.getItemString(i, "ORDER_CODE") + "','";
//		      }
//		      order_code = "'" +order_code + table.getItemString(newdata.getCount() - 1, "ORDER_CODE") + "'";
//		      order_code = "in ( " + order_code + ")";
//		    }
//		      System.out.println(order_code);

		String sDate = this.getValueString("START_DATE");
		String eDate = this.getValueString("END_DATE");

		String opt_date = "";
		sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		opt_date += " AND a.opt_date > TO_DATE('" + sDate
				+ "','YYYYMMDDHH24MISS') ";
		eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		opt_date += " AND a.opt_date < TO_DATE('" + eDate
				+ "','YYYYMMDDHH24MISS') ";

//		String sql = "SELECT DISTINCT C.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,B.OWN_PRICE,D.UNIT_CHN_DESC AS CHN_DESC,"
//				+ "A.VERIFYIN_PRICE,F.UNIT_CHN_DESC AS UNIT_DESC ,"
//				+ "A.MAN_CODE,SUM (A.VERIFYIN_QTY) AS SUM,F.UNIT_CHN_DESC AS UNIT_DESC1 "
//				+ "FROM IND_VERIFYIND A,SYS_FEE B,SYS_UNIT F,IND_MONITOR_MED C,SYS_UNIT D "
//				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
//				+ "AND B.UNIT_CODE = d.UNIT_CODE "
//				+ "AND A.BILL_UNIT = F.UNIT_CODE "
//				+ "AND A.ORDER_CODE = C.ORDER_CODE "
//				+ opt_date
//                +" AND A.ORDER_CODE IN (SELECT ORDER_CODE FROM IND_MONITOR_MED WHERE MONITOR_TYPE = 'DAN' AND ENABLE_FLG='Y') "
//                + "GROUP BY C.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,B.OWN_PRICE,A.VERIFYIN_PRICE,D.UNIT_CHN_DESC,A.MAN_CODE,F.UNIT_CHN_DESC "
//                + "ORDER BY ORDER_CODE ";
		
		String sql = "SELECT DISTINCT B.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,B.OWN_PRICE,D.UNIT_CHN_DESC AS CHN_DESC,"
				+ "A.VERIFYIN_PRICE,F.UNIT_CHN_DESC AS UNIT_DESC ,"
				+ "A.MAN_CODE,SUM (A.VERIFYIN_QTY) AS SUM,F.UNIT_CHN_DESC AS UNIT_DESC1 "
				+ "FROM IND_VERIFYIND A,SYS_FEE B,SYS_UNIT F,SYS_UNIT D "
//				+ "IND_MONITOR_MED C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND B.UNIT_CODE = d.UNIT_CODE "
				+ "AND A.BILL_UNIT = F.UNIT_CODE "
//				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ opt_date
                +" AND A.ORDER_CODE IN (SELECT ORDER_CODE FROM IND_MONITOR_MED WHERE MONITOR_TYPE = 'DAN' AND ENABLE_FLG='Y') "
                + "GROUP BY B.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,B.OWN_PRICE,A.VERIFYIN_PRICE,D.UNIT_CHN_DESC,A.MAN_CODE,F.UNIT_CHN_DESC "
                + "ORDER BY ORDER_CODE ";

		TParm newdata = new TParm(TJDODBTool.getInstance().select(sql)); 

		//System.out.println("sql++++" + sql);
		if (newdata.getErrCode() < 0) {
			this.messageBox(newdata.getErrText());
			return;
		}
		if (newdata.getCount() <= 0) {
			this.messageBox("��������");
		}

		// System.out.println(newdata);
		 double total = 0;
	        for(int i = 0; i < newdata.getCount(); i++) {
	        	String tot = String.valueOf(newdata.getDouble("VERIFYIN_PRICE", i));
	        	if(StringUtils.isEmpty(tot) || "null".equals(tot)) {
	        		continue;
	        	}
	        	total += Double.parseDouble(tot);
	        }
	        this.setText("TOTAL_AMT", new DecimalFormat("########0.00").format(total));
		// ��table����ʾ��ѯ��Ϣ
		table.setParmValue(newdata);
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		TTable table = this.getTable("TTable");
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "Σ��ҩƷͳ��");
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		String clearStr = "ORDER_CODE;ORDER_DESC;START_DATE;END_DATE";
		this.clearValue(clearStr);
		this.setValue("TOTAL_AMT", "0");
		// ��ʼ��ͳ������
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date)
				.substring(0, 4)
				+ "/"
				+ TypeTool.getString(date).substring(5, 7)
				+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (����25)
		setValue("END_DATE", dateTime);

		// ��ʼʱ��(�ϸ���26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");

		//TTable table = this.getTable("Table_Order");
		TTable table = this.getTable("TTable");
		//table.removeRowAll();
		table.removeRowAll();
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TTable table = getTable("TTABLE");
		// ����
		if (table.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		} else {
			// ��ӡ����
			TParm data = new TParm();
			// ��ͷ����
			data.setData("TITLE", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion())
					+ "����Σ��ҩƷ����");
			String start_date = getValueString("START_DATE");
			String end_date = getValueString("END_DATE");
			data.setData("DATE_AREA", "TEXT", "ͳ������: "
					+ start_date.substring(0, 4) + "/"
					+ start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " "
					+ start_date.substring(11, 13) + ":"
					+ start_date.substring(14, 16) + ":"
					+ start_date.substring(17, 19) + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " "
					+ end_date.substring(11, 13) + ":"
					+ end_date.substring(14, 16) + ":"
					+ end_date.substring(17, 19));

			data.setData("DATE", "TEXT", "�Ʊ�ʱ��: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			
			data.setData("ORG_CODE", "TEXT",
                    "���ղ���: " +
                    this.getComboBox("ORG_CODE").getSelectedName());
			data.setData("TOT_AMT", "TEXT",  "�ɹ����ܼƣ�" + this.getValueString("TOTAL_AMT") + "Ԫ");
			// �������
			TParm parm = new TParm();
			TParm tableParm = table.getShowParmValue();
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", tableParm.getValue(
						"SPECIFICATION", i));
				parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
				parm.addData("CHN_DESC", tableParm.getValue("CHN_DESC", i));
				parm.addData("VERIFYIN_PRICE", tableParm.getValue(
						"VERIFYIN_PRICE", i));
				parm.addData("UNIT_DESC", tableParm.getValue("UNIT_DESC", i));
				parm.addData("MAN_CODE", tableParm.getValue("MAN_CODE", i));
				parm.addData("SUM", tableParm.getValue("SUM",i));
				parm.addData("UNIT_DESC1", tableParm.getValue("UNIT_DESC1", i));
				//System.out.println(parm);
			}
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
			parm.addData("SYSTEM", "COLUMNS", "SUM");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_DESC1");
			//System.out.println("1"+parm);
			data.setData("TABLE", parm.getData());
			//System.out.println("2"+parm);

//			data.setData("OPT_USER", "TEXT", "�����ˣ�" + Operator.getName());
//			data.setData("OPT_DATE", "TEXT", "����ʱ�䣺"
//					+ SystemTool.getInstance().getDate().toString().substring(
//							0, 10));
			// ���ô�ӡ����
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\IND\\INDNarcotics.jhw", data);
		}

	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	
    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

}
