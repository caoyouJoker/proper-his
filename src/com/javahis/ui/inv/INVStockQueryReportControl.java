package com.javahis.ui.inv;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * 
 * 
 * <p>
 * 
 * Title: ���ʿ���ѯ
 * </p>
 * 
 * <p>
 * Description:���ʿ���ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) BLUECORE 2013
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author fux 2013.11.08
 * @version 1.0
 */
public class INVStockQueryReportControl extends TControl {
	/**
	 * ��ͨ��ɫ(��ɫ)
	 */
	Color normalColor = new Color(0, 0, 0);
	/**
	 * һ���½�Ч��(��ɫ)
	 */
	Color Color = new Color(255, 0, 0);
	/**
	 * �����½�Ч��(��ɫ)
	 */
	Color nhiColor = new Color(255, 150, 0);
	// $$=============add by wangjingchun 20150123
	// ����������start==================$$//
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	// $$=============add by wangjingchun 20150123
	// ����������end==================$$//
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ���û�
		String deptCode = Operator.getDept();
		this.setValue("DEPT_CODE", deptCode);
		callFunction("UI|INV_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\inv\\INVBasePopup.x");
		// textfield���ܻش�ֵ
		callFunction("UI|INV_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
		// getDate(), -1);
		// setValue("S_TIME", yesterday.toString().substring(0,10)+"00:00:00");
		// setValue("E_TIME",
		// SystemTool.getInstance().getDate().toString().substring(0,10)+"23:59:59");
		// setValue("VAILD_S", yesterday);
		// setValue("VAILD_E", SystemTool.getInstance().getDate());

		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("SUP_CODE", "19");
		this.getCheckBox("SAVE_FLG").isSelected();
		if (this.getPopedem("ALL")) {
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
		} else {
			this.callFunction("UI|DEPT_CODE|setEnabled", true);
		}
		// $$=====add by wangjingchun 20150123 �������򷽷�start============$$//
		addListener(getTable("TABLE"));
		// $$=====add by wangjingchun 20150123 �������򷽷�end============$$//

	}

	/**
	 * ������ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String inv_code = parm.getValue("INV_CODE");
		if (!inv_code.equals("")) {
			getTextField("INV_CODE").setValue(inv_code);
		}
		String inv_desc = parm.getValue("INV_CHN_DESC");
		if (!inv_desc.equals("")) {
			getTextField("INV_CHN_DESC").setValue(inv_desc);
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// //Ч����
		// String vaildTimeS =
		// this.getValueString("VAILD_S").replace("-","").substring(0, 8);
		// //Ч����
		// String vaildTimeE =
		// this.getValueString("VAILD_E").replace("-","").substring(0, 8);
		SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
		DecimalFormat ff = new DecimalFormat("######0.00");
		// ����
		String deptcode = this.getValueString("DEPT_CODE");
		// ���ʱ���
		String invcode = this.getValueString("INV_CODE");
		// �豸����(��ֵ��ֵ)
		String expensiveFlg = this.getValueString("EXPENSIVE_FLG");
		// �豸�����
		String kind = this.getValueString("KIND");
		// ��Ӧ��
		String supCode = this.getValueString("SUP_CODE");
		// �ϼ���Ӧ��
		String exSupCode = this.getValueString("EX_SUP_CODE");
		// �ϼ���Ӧ��
		String locCode = this.getValueString("MATERIAL_LOC_CODE");
		// ������
		int replqty = this.getValueInt("REPL_QTY");

		// ���ʱ��롢�������ơ������������λ��
		// �ɹ��۸񣨵��ۣ����ɹ����ܶ����Ӧ�̡��ϼ���Ӧ�̡������̡�
		// ���ʷ��ࣨ���ֵ����ʷ��ࣩ����������ȫ�����
		StringBuffer SQL = new StringBuffer();
		String groupBy = "";
		String orderby = "";
		// ����������
		if (this.getCheckBox("BASE_FLG").isSelected()) {
			// �������
			// if(this.getCheckBox("VAILD_FLG").isSelected()){
			// String sql =
			// "SELECT B.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION ,(A.STOCK_QTY-D.BASE_QTY) AS STOCK_QTY,A.STOCK_UNIT,"
			// +
			// " C.CONTRACT_PRICE,C.CONTRACT_PRICE * (A.STOCK_QTY-D.BASE_QTY) AS TOT,B.SUP_CODE,B.UP_SUP_CODE,B.MAN_CODE,"+
			// " B.INV_KIND,D.BASE_QTY AS BASE_QTY ,D.SAFE_QTY,A.BATCH_SEQ,D.MATERIAL_LOC_CODE"
			// +
			// " FROM INV_STOCKD A,INV_BASE B,INV_AGENT C,INV_STOCKM D " +
			// " WHERE A.INV_CODE=B.INV_CODE" +
			// " AND A.INV_CODE=C.INV_CODE" +
			// " AND A.INV_CODE=D.INV_CODE" +
			// " AND A.ORG_CODE = D.ORG_CODE" +
			// " AND A.REGION_CODE='"+Operator.getRegion()+"'";
			// SQL.append(sql);
			// }
			// String sql =
			// "SELECT B.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION ,(A.STOCK_QTY-A.BASE_QTY) AS STOCK_QTY,A.STOCK_UNIT,"
			// +
			// " C.CONTRACT_PRICE,C.CONTRACT_PRICE * (A.STOCK_QTY-A.BASE_QTY) AS TOT,B.SUP_CODE,B.UP_SUP_CODE,B.MAN_CODE,"+
			// " B.INV_KIND,A.BASE_QTY AS BASE_QTY,A.SAFE_QTY,A.MATERIAL_LOC_CODE,(A.SAFE_QTY-A.STOCK_QTY) AS REPL_QTY "
			// +
			// " FROM INV_STOCKM A,INV_BASE B,INV_AGENT C " +
			// " WHERE A.INV_CODE=B.INV_CODE" +
			// " AND A.INV_CODE=C.INV_CODE" +
			// " AND B.SUP_CODE=C.SUP_CODE" +
			// " AND A.REGION_CODE='"+Operator.getRegion()+"'";
			// alert by wukai 20161230 start
			String sql = " SELECT B.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, SUM(A.STOCK_QTY - A.BASE_QTY) AS STOCK_QTY, A.STOCK_UNIT,"
					+ " C.CONTRACT_PRICE, SUM(C.CONTRACT_PRICE * (A.STOCK_QTY - A.BASE_QTY)) AS TOT, B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE,"
					+ " B.INV_KIND, A.BASE_QTY AS BASE_QTY, A.SAFE_QTY, A.MATERIAL_LOC_CODE, (A.SAFE_QTY - A.STOCK_QTY) AS REPL_QTY "
					+ " FROM INV_STOCKM A, INV_BASE B, INV_AGENT C "
					+ " WHERE A.INV_CODE = B.INV_CODE "
					+ " AND A.INV_CODE = C.INV_CODE "
					+ " AND B.SUP_CODE = C.SUP_CODE "
					+ " AND A.REGION_CODE = '" + Operator.getRegion() + "' ";
			groupBy = " GROUP BY B.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, A.STOCK_UNIT, "
					+ " C.CONTRACT_PRICE, B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE, "
					+ " B.INV_KIND, A.BASE_QTY, A.SAFE_QTY, A.MATERIAL_LOC_CODE,  (A.SAFE_QTY - A.STOCK_QTY) ";
			orderby = " ORDER BY B.MAN_CODE, B.INV_CODE, B.SUP_CODE ";
			SQL.append(sql);
			// alert by wukai 20161230 end
		} else {
			if (this.getCheckBox("VAILD_FLG").isSelected()) {
				// String sql =
				// "SELECT B.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION ,A.STOCK_QTY AS STOCK_QTY,A.STOCK_UNIT,"
				// +
				// " C.CONTRACT_PRICE,C.CONTRACT_PRICE * A.STOCK_QTY AS TOT,B.SUP_CODE,B.UP_SUP_CODE,B.MAN_CODE,"+
				// " B.INV_KIND,D.BASE_QTY AS BASE_QTY,D.SAFE_QTY,A.BATCH_SEQ,D.MATERIAL_LOC_CODE,(A.SAFE_QTY-A.STOCK_QTY) AS REPL_QTY "
				// +
				// " FROM INV_STOCKD A,INV_BASE B,INV_AGENT C,INV_STOCKM D " +
				// " WHERE A.INV_CODE=B.INV_CODE" +
				// " AND A.INV_CODE=C.INV_CODE" +
				// " AND A.INV_CODE=D.INV_CODE" +
				// " AND A.ORG_CODE = D.ORG_CODE" +
				// " AND B.SUP_CODE=C.SUP_CODE" +
				// " AND A.REGION_CODE='"+Operator.getRegion()+"'";
				// SQL.append(sql);
				// alert by wukai 20161230 start
				String sql = "SELECT B.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION , SUM(A.STOCK_QTY) AS STOCK_QTY, A.STOCK_UNIT,"
						+ " C.CONTRACT_PRICE, SUM(C.CONTRACT_PRICE * A.STOCK_QTY) AS TOT, B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE,"
						+ " B.INV_KIND, D.BASE_QTY AS BASE_QTY, D.SAFE_QTY, A.BATCH_SEQ, D.MATERIAL_LOC_CODE, (D.SAFE_QTY - D.STOCK_QTY) AS REPL_QTY "
						+ " FROM INV_STOCKD A,INV_BASE B,INV_AGENT C,INV_STOCKM D "
						+ " WHERE A.INV_CODE=B.INV_CODE"
						+ " AND A.INV_CODE=C.INV_CODE"
						+ " AND A.INV_CODE=D.INV_CODE"
						+ " AND A.ORG_CODE = D.ORG_CODE"
						+ " AND B.SUP_CODE=C.SUP_CODE"
						+ " AND A.REGION_CODE='"
						+ Operator.getRegion() + "' ";
				groupBy = " GROUP BY B.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, A.STOCK_UNIT, "
						+ " C.CONTRACT_PRICE, B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE, "
						+ " B.INV_KIND, D.BASE_QTY, D.SAFE_QTY, A.BATCH_SEQ, D.MATERIAL_LOC_CODE, (D.SAFE_QTY - D.STOCK_QTY) ";
				orderby = " ORDER BY B.MAN_CODE, B.INV_CODE, B.SUP_CODE ";
				SQL.append(sql);

				// alert by wukai 20161230 end
			} else {
				// String sql =
				// "SELECT B.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION ,A.STOCK_QTY AS STOCK_QTY,A.SAFE_QTY,A.STOCK_UNIT,"
				// +
				// " C.CONTRACT_PRICE,C.CONTRACT_PRICE * A.STOCK_QTY AS TOT,B.SUP_CODE,B.UP_SUP_CODE,B.MAN_CODE,"
				// +
				// " B.INV_KIND,A.BASE_QTY AS BASE_QTY,A.MATERIAL_LOC_CODE,(A.SAFE_QTY-A.STOCK_QTY) AS REPL_QTY "
				// + " FROM INV_STOCKM A,INV_BASE B,INV_AGENT C "
				// + " WHERE A.INV_CODE=B.INV_CODE"
				// + " AND A.INV_CODE=C.INV_CODE"
				// + " AND B.SUP_CODE=C.SUP_CODE"
				// + " AND A.REGION_CODE='"
				// + Operator.getRegion() + "'";
				// SQL.append(sql);
				// alert by wukai 20161230 start
				String sql = "SELECT B.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, SUM(A.STOCK_QTY) AS STOCK_QTY, A.SAFE_QTY, A.STOCK_UNIT,"
						+ " C.CONTRACT_PRICE, SUM(C.CONTRACT_PRICE * A.STOCK_QTY) AS TOT, B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE,"
						+ " B.INV_KIND, A.BASE_QTY AS BASE_QTY, A.MATERIAL_LOC_CODE, (A.SAFE_QTY-A.STOCK_QTY) AS REPL_QTY "
						+ " FROM INV_STOCKM A, INV_BASE B, INV_AGENT C "
						+ " WHERE A.INV_CODE=B.INV_CODE"
						+ " AND A.INV_CODE=C.INV_CODE"
						+ " AND B.SUP_CODE=C.SUP_CODE"
						+ " AND A.REGION_CODE='"
						+ Operator.getRegion() + "'";
				groupBy = " GROUP BY B.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, A.SAFE_QTY, A.STOCK_UNIT, "
						+ " C.CONTRACT_PRICE, B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE, "
						+ " B.INV_KIND, A.BASE_QTY, A.MATERIAL_LOC_CODE, (A.SAFE_QTY - A.STOCK_QTY) ";
				orderby = " ORDER BY B.MAN_CODE, B.INV_CODE, B.SUP_CODE ";
				SQL.append(sql);

				// alert by wukai 20161230 end
			}
		}
		// ����
		if (!deptcode.equals("")) {
			SQL.append(" AND A.ORG_CODE='" + deptcode + "'");
		}
		// ��������
		if (!invcode.equals("")) {
			SQL.append(" AND B.INV_CODE='" + invcode + "'");
		}
		// �豸����(��ֵ��ֵ)
		if (expensiveFlg.equals("A")) {
			SQL.append(" AND B.EXPENSIVE_FLG='Y' AND B.SEQMAN_FLG = 'Y' ");
		}
		// �豸�����
		if (!kind.equals("")) {
			SQL.append(" AND B.INV_KIND='" + kind + "'");
		}
		// ��Ӧ��
		if (!supCode.equals("")) {
			SQL.append(" AND B.SUP_CODE='" + supCode + "'");
		}
		// �ϼ���Ӧ��
		if (!exSupCode.equals("")) {
			SQL.append(" AND��B.UP_SUP_CODE='" + exSupCode + "'");
		}
		// ��λ
		if (!locCode.equals("")) {
			SQL.append(" AND��A.MATERIAL_LOC_CODE ='" + locCode + "'");
		}

		// ��治Ϊ0
		if (this.getCheckBox("QTY_FLG").isSelected()) {
			SQL.append(" AND A.STOCK_QTY>0");
		}

		// ��ȫ�����
		if (this.getCheckBox("SAVE_FLG").isSelected()) {
			if (this.getCheckBox("VAILD_FLG").isSelected()) {
				SQL.append(" AND D.SAFE_QTY > D.STOCK_QTY");
			} else {
				SQL.append(" AND A.SAFE_QTY > A.STOCK_QTY");
			}
		}
		// SQL.append(" ORDER BY A.ORG_CODE,A.INV_CODE ");// 20150123
		// wangjingchun modify
		SQL.append(groupBy); // alert by wukai 20161230
		SQL.append(orderby);// 20150123 wangjingchun add , alert by wukai
							// 20161230

//		System.out.println("SQL---Stockquery:" + SQL);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(SQL.toString()));
		// System.out.println("result"+result);
		// �жϴ���ֵ
		if (result == null || result.getCount() <= 0) {
			callFunction("UI|TABLE|removeRowAll");
			callFunction("UI|TABLEDD|removeRowAll");
			this.messageBox("û�в�ѯ����");
			this.clearValue("NUM");
			this.clearValue("TOT_VALUE");
			return;
		}
		// ����
		int num = 0;
		// ������
		double numTot = 0;
		double totValue = 0.00;
		for (int i = 0; i < result.getCount(); i++) {
			num++;
			totValue = totValue + result.getDouble("TOT", i);
			numTot = numTot + result.getDouble("STOCK_QTY", i);

			// ��ȡ������������<0�����ݾ���Ϊ0
			if (result.getInt("REPL_QTY", i) < 0) {
				result.setData("REPL_QTY", i, 0);

			}
			// //INV_CODE;INV_CHN_DESC;DESCRIPTION;STOCK_QTY;STOCK_UNIT;
			// //COST_PRICE;TOT;SUP_CODE;UP_SUP_CODE;MAN_CODE;INV_KIND;BASE_QTY;SAFE_QTY
			// date.addData("INV_CODE", result.getValue("INV_CODE", i));
			// date.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", i));
			// date.addData("DESCRIPTION", result.getValue("DESCRIPTION", i));
			// date.addData("STOCK_QTY", result.getInt("STOCK_QTY", i));
			// date.addData("STOCK_UNIT", result.getValue("STOCK_UNIT", i));
			// date.addData("CONTRACT_PRICE", result.getValue("CONTRACT_PRICE",
			// i));
			// date.addData("TOT", result.getValue("TOT", i));
			// date.addData("SUP_CODE", result.getInt("SUP_CODE", i));
			// date.addData("UP_SUP_CODE", result.getValue("UP_SUP_CODE", i));
			// date.addData("MAN_CODE", result.getValue("MAN_CODE", i));
			// date.addData("INV_KIND", result.getValue("INV_KIND", i));
			// date.addData("BASE_QTY", result.getValue("BASE_QTY", i));
			// date.addData("SAFE_QTY", result.getValue("SAFE_QTY", i));
		}
		totValue = Math.abs(totValue); // 2014-01-15���
		DecimalFormat df = new DecimalFormat("0.00");
		String AllFee = df.format(totValue);
		AllFee = feeConversion(AllFee);// �Ӷ��Ŵ���
		// this.setValue("NUM", num);
		this.setValue("NUM", numTot);// 20150701 wangjc modify
		this.setValue("NUM_TOT", numTot);
		this.setValue("TOT_VALUE", AllFee);
		this.callFunction("UI|TABLE|setParmValue", result);

	}

	/**
	 * ÿ��λ�Ӷ��Ŵ���
	 */
	public String feeConversion(String fee) {
		String str1 = "";
		String[] s = fee.split("\\.");// ��"."���ָ�
		// reverse��ת
		str1 = new StringBuilder(s[0].toString()).reverse().toString();// �Ƚ��ַ����ߵ�˳��
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// ����ٽ�˳��ת����
		String str3 = new StringBuilder(str2).reverse().toString();
		// ����С��������
		StringBuffer str4 = new StringBuffer(str3);
		str4 = str4.append(".").append(s[1]);
		return str4.toString();
	}

	/**
	 * ���
	 */
	public void onClear() {
		String str = "INV_CODE;INV_CHN_DESC;EXPENSIVE_FLG;KIND;"
				+ "SUP_CODE;QTY_FLG;SAVE_FLG;NUM;TOT_VALUE;VAILD_FLG";
		// EX_SUP_CODE;
		this.clearValue(str);
		callFunction("UI|TABLE|removeRowAll");
		callFunction("UI|TABLEDD|removeRowAll");
		// Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
		// getDate(), -1);
		// setValue("VAILD_S", yesterday);
		// setValue("VAILD_E", SystemTool.getInstance().getDate());
		// this.setValue("NUM", "");
		// this.setValue("TOT_VALUE", "");
	}

	/**
	 * �������¼�
	 */
	public void onTableClicked() {
		// messageBox("�������¼�");
		// //Ч����
		// String vaildTimeS =
		// this.getValueString("VAILD_S").replace("-","").substring(0, 8);
		// //Ч����
		// String vaildTimeE =
		// this.getValueString("VAILD_E").replace("-","").substring(0, 8);
		// ����
		String deptcode = this.getValueString("DEPT_CODE");
		int row = this.getTable("TABLE").getClickedRow();
		TParm parm = this.getTable("TABLE").getParmValue().getRow(row);
		// ��ѯ��ֹ����
		// String verTime = this.getValueString("VERIFYIN_DATE");
		// System.out.println("parm"+parm);
		String regionCode = Operator.getRegion();
		// rfid,100;���ʱ���,100;��������,100;���,200;��λ,50;
		// �ɹ��۸񣨵��ۣ�,120;�ɹ����ܶ,120;��Ӧ��,100;�ϼ���Ӧ��,100;������,100;���ʷ���,100
		// RFID;INV_CODE;INV_CHN_DESC;DESCRIPTION;STOCK_UNIT;CONTRACT_PRICE;TOT_DD;SUP_CODE;UP_SUP_CODE;MAN_CODE;INV_KIND

//		String sql = " SELECT A.RFID,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,B.STOCK_UNIT,"
//				+ "        C.CONTRACT_PRICE,C.CONTRACT_PRICE * A.STOCK_QTY AS TOT_DD,B.SUP_CODE,B.UP_SUP_CODE,"
//				+ "        B.MAN_CODE,B.INV_KIND,A.BATCH_NO,A.VALID_DATE,A.BATCH_SEQ "
//				+ "		   ,A.ORGIN_CODE "
//				+ // 20150127 wangjingchun add SN��
//				"   FROM INV_STOCKDD A,INV_BASE B,INV_AGENT C"
//				+ "   WHERE    A.INV_CODE = B.INV_CODE"
//				+ "   AND      A.INV_CODE = C.INV_CODE"
//				+ "   AND      B.SUP_CODE = C.SUP_CODE"
//				+ // 20161229 lij ��������
//				"   AND      A.REGION_CODE = '"
//				+ regionCode
//				+ "' "
//				+ "   AND A.WAST_FLG = 'N' ";
		
		//alert by wukai 20161230 start
		String sql = " SELECT A.RFID, A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, B.STOCK_UNIT, A.ORG_CODE, A.INVSEQ_NO, "
				+ "        C.CONTRACT_PRICE, SUM(C.CONTRACT_PRICE * A.STOCK_QTY) AS TOT_DD, B.SUP_CODE, B.UP_SUP_CODE,"
				+ "        B.MAN_CODE, B.INV_KIND, A.BATCH_NO, A.VALID_DATE, A.BATCH_SEQ, A.ORGIN_CODE "
				+ "   FROM INV_STOCKDD A,INV_BASE B,INV_AGENT C"
				+ "   WHERE    A.INV_CODE = B.INV_CODE"
				+ "   AND      A.INV_CODE = C.INV_CODE"
				+ "   AND      B.SUP_CODE = C.SUP_CODE"
				+ "   AND      A.REGION_CODE = '"
				+ regionCode
				+ "' "
				+ "   AND A.WAST_FLG = 'N' ";
		//alert by wukai 20161230 end
		StringBuffer SQL = new StringBuffer();
		SQL.append(sql);
		SQL.append(" AND A.INV_CODE='" + parm.getValue("INV_CODE") + "'");
		SQL.append(" AND B.INV_KIND='" + parm.getValue("INV_KIND") + "'");
		if (!deptcode.equals("")) {
			SQL.append(" AND A.ORG_CODE= '" + deptcode + "'");
		}
		if (this.getCheckBox("VAILD_FLG").isSelected()) {
			SQL.append(" AND A.BATCH_SEQ= '" + parm.getValue("BATCH_SEQ")
					+ "'  ");
		}
		//alert by wukai 20161230 start
		SQL.append(" GROUP BY A.RFID, A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, B.STOCK_UNIT, C.CONTRACT_PRICE, A.ORG_CODE, A.INVSEQ_NO,"
				+ " B.SUP_CODE, B.UP_SUP_CODE, B.MAN_CODE, B.INV_KIND, A.BATCH_NO, A.VALID_DATE, A.BATCH_SEQ, A.ORGIN_CODE ");
		//alert by wukai 20161230 end
		// ��ѯ��ֹ����
		// if(!verTime.equals("")){
		// verTime = verTime.substring(0, 10).replace('-', ' ').trim();
		// SQL.append(" AND A.VERIFYIN_DATE <= TO_DATE ('"+verTime+"235959"+"', 'yyyyMMddHH24miss')");
		// }
		// Ч��
		// SQL.append(" AND A.VALID_DATE BETWEEN TO_DATE ('" + vaildTimeS +
		// "000000" +
		// "', 'yyyyMMddHH24miss')" +
		// "    AND TO_DATE ('" + vaildTimeE + "235959" +
		// "', 'yyyyMMddHH24miss')");
		SQL.append(" ORDER BY A.INV_CODE, A.ORG_CODE, A.INVSEQ_NO");
		// messageBox("�������¼�SQL��ϸ"+SQL);
		//System.out.println("SQL��ϸ = = = = = "+SQL.toString());
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(SQL.toString()));
		if (result.getCount() <= 0) {
			callFunction("UI|TABLEDD|removeRowAll");
			return;
		}
		this.callFunction("UI|TABLEDD|setParmValue", result);
		// �㷨������
		for (int i = 0; i < result.getCount(); i++) {
			Timestamp vaildDate = result.getTimestamp("VALID_DATE", i);
			Timestamp today = StringTool.rollDate(SystemTool.getInstance()
					.getDate(), 0);
			// fux modify 20140313
			int num = getMonth(vaildDate, today);
			// System.out.println("num="+num);
			if (num < 1) {
				((TTable) getComponent("TABLEDD")).setRowTextColor(i, Color);
			} else if (num < 3 && num >= 1) {
				((TTable) getComponent("TABLEDD")).setRowTextColor(i, nhiColor);
			} else {
				((TTable) getComponent("TABLEDD")).setRowTextColor(i,
						normalColor);
			}
		}
	}

	/**
	 * ��ȡ�·ݲ�
	 */
	public int getMonth(Timestamp vaildDate, Timestamp today) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(today);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(vaildDate);
		Calendar temp = Calendar.getInstance();
		temp.setTime(vaildDate);
		temp.add(Calendar.DATE, 1);
		int year = endCalendar.get(Calendar.YEAR)
				- startCalendar.get(Calendar.YEAR);
		int month = endCalendar.get(Calendar.MONTH)
				- startCalendar.get(Calendar.MONTH);
		int num = 0;
		if ((startCalendar.get(Calendar.DATE) == 1)
				&& (temp.get(Calendar.DATE) == 1)) {
			num = year * 12 + month + 1;
		} else if ((startCalendar.get(Calendar.DATE) != 1)
				&& (temp.get(Calendar.DATE) == 1)) {
			num = year * 12 + month;
		} else if ((startCalendar.get(Calendar.DATE) == 1)
				&& (temp.get(Calendar.DATE) != 1)) {
			num = year * 12 + month;
		} else {
			num = (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
		}
		return num;
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		// if (this.getTable("TABLE").getRowCount() <= 0) {
		// this.messageBox("û��Ҫ��ӡ������");
		// return;
		// }
		// TParm prtParm = new TParm();
		// //��ͷ
		// prtParm.setData("TITLE","TEXT","���ʿ��ͳ�Ʊ���");
		// //����
		// prtParm.setData("PRINT_DATE","TEXT","��ӡ���ڣ�" +
		// StringTool.getString(StringTool.getTimestamp(new Date()),
		// "yyyy��MM��dd��"));
		// // //�Ʋ��ܼ�
		// // prtParm.setData("TOT","TEXT", "�Ʋ��ܼƣ�"
		// +this.getValueDouble("TOT_VALUE"));
		// //�õ����ؼ�
		// TParm parm = this.getTable("TABLE").getShowParmValue();
		// TParm prtTableParm=new TParm();
		// //INV_CODE;INV_CHN_DESC;DESCRIPTION;STOCK_QTY;STOCK_UNIT;
		// //COST_PRICE;TOT;SUP_CODE;UP_SUP_CODE;MAN_CODE;INV_KIND;BASE_QTY;SAFE_QTY
		// for(int i=0;i<parm.getCount("INV_CODE");i++){
		// prtTableParm.addData("RFID",parm.getRow(i).getValue("RFID"));
		// prtTableParm.addData("INV_CODE",parm.getRow(i).getValue("INV_CODE"));
		// prtTableParm.addData("INV_CHN_DESC",parm.getRow(i).getValue("INV_CHN_DESC"));
		// prtTableParm.addData("DESCRIPTION",parm.getRow(i).getValue("DESCRIPTION"));
		// prtTableParm.addData("STOCK_QTY",parm.getRow(i).getValue("STOCK_QTY"));
		// prtTableParm.addData("STOCK_UNIT",parm.getRow(i).getValue("STOCK_UNIT"));
		// prtTableParm.addData("CONTRACT_PRICE",parm.getRow(i).getValue("CONTRACT_PRICE"));
		// prtTableParm.addData("TOT",parm.getRow(i).getValue("TOT"));
		// prtTableParm.addData("SUP_CODE",parm.getRow(i).getValue("SUP_CODE"));
		// prtTableParm.addData("UP_SUP_CODE",parm.getRow(i).getValue("UP_SUP_CODE"));
		// prtTableParm.addData("MAN_CODE",parm.getRow(i).getValue("MAN_CODE"));
		// prtTableParm.addData("INV_KIND",parm.getRow(i).getValue("INV_KIND"));
		// prtTableParm.addData("BASE_QTY",parm.getRow(i).getValue("BASE_QTY"));
		// prtTableParm.addData("SAFE_QTY",parm.getRow(i).getValue("SAFE_QTY"));
		// }
		// prtTableParm.setCount(prtTableParm.getCount("INV_CODE"));
		// prtTableParm.addData("SYSTEM", "COLUMNS", "RFID");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "INV_CODE");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "STOCK_UNIT");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "TOT");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "UP_SUP_CODE");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "INV_KIND");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "BASE_QTY");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "SAFE_QTY");
		// prtParm.setData("TABLE", prtTableParm.getData());
		// //��β
		// prtParm.setData("USER","TEXT", "�Ʊ��ˣ�" + Operator.getName());
		// this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVStockQueryReport.jhw",
		// prtParm);
	}

	/**
	 * �������ע�Ǹı��¼�
	 */
	public void onCharge() {
		if (this.getCheckBox("VAILD_FLG").isSelected()) {
			this.getCheckBox("BASE_FLG").setEnabled(false);
		} else {
			this.getCheckBox("BASE_FLG").setEnabled(true);
		}
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (this.getTable("TABLE").getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
					"���ʿ��ͳ�Ʊ���");
		}
	}

	// $$==============add by wangjingchun 20150123 ����������start=============$$//
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTable("TABLE").getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = getTable("TABLE").getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTable("TABLE").setParmValue(parmTable);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
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

	// $$==============add by wangjingchun 20150123 ����������end=============$$//
	/**
	 * �õ���ؼ�
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ��ı��ؼ�
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �õ�TCheckBox����
	 * 
	 * @param tagName
	 *            String
	 * @return TCheckBox
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
