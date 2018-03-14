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
 * Title: 物资库存查询
 * </p>
 * 
 * <p>
 * Description:物资库存查询
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
	 * 普通颜色(黑色)
	 */
	Color normalColor = new Color(0, 0, 0);
	/**
	 * 一个月近效期(红色)
	 */
	Color Color = new Color(255, 0, 0);
	/**
	 * 三个月近效期(黄色)
	 */
	Color nhiColor = new Color(255, 150, 0);
	// $$=============add by wangjingchun 20150123
	// 加入排序功能start==================$$//
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	// $$=============add by wangjingchun 20150123
	// 加入排序功能end==================$$//
	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化用户
		String deptCode = Operator.getDept();
		this.setValue("DEPT_CODE", deptCode);
		callFunction("UI|INV_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\inv\\INVBasePopup.x");
		// textfield接受回传值
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
		// $$=====add by wangjingchun 20150123 加入排序方法start============$$//
		addListener(getTable("TABLE"));
		// $$=====add by wangjingchun 20150123 加入排序方法end============$$//

	}

	/**
	 * 编码接受返回值方法
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
	 * 查询
	 */
	public void onQuery() {
		// //效期起
		// String vaildTimeS =
		// this.getValueString("VAILD_S").replace("-","").substring(0, 8);
		// //效期讫
		// String vaildTimeE =
		// this.getValueString("VAILD_E").replace("-","").substring(0, 8);
		SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
		DecimalFormat ff = new DecimalFormat("######0.00");
		// 科室
		String deptcode = this.getValueString("DEPT_CODE");
		// 物资编码
		String invcode = this.getValueString("INV_CODE");
		// 设备分类(高值低值)
		String expensiveFlg = this.getValueString("EXPENSIVE_FLG");
		// 设备大分类
		String kind = this.getValueString("KIND");
		// 供应商
		String supCode = this.getValueString("SUP_CODE");
		// 上级供应商
		String exSupCode = this.getValueString("EX_SUP_CODE");
		// 上级供应商
		String locCode = this.getValueString("MATERIAL_LOC_CODE");
		// 补货量
		int replqty = this.getValueInt("REPL_QTY");

		// 物资编码、物资名称、规格、数量、单位、
		// 采购价格（单价）、采购金额（总额）、供应商、上级供应商、生产商、
		// 物资分类（见字典物资分类）、基数、安全库存量
		StringBuffer SQL = new StringBuffer();
		String groupBy = "";
		String orderby = "";
		// 不包含基数
		if (this.getCheckBox("BASE_FLG").isSelected()) {
			// 批次序号
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
		// 科室
		if (!deptcode.equals("")) {
			SQL.append(" AND A.ORG_CODE='" + deptcode + "'");
		}
		// 物资名称
		if (!invcode.equals("")) {
			SQL.append(" AND B.INV_CODE='" + invcode + "'");
		}
		// 设备分类(高值低值)
		if (expensiveFlg.equals("A")) {
			SQL.append(" AND B.EXPENSIVE_FLG='Y' AND B.SEQMAN_FLG = 'Y' ");
		}
		// 设备大分类
		if (!kind.equals("")) {
			SQL.append(" AND B.INV_KIND='" + kind + "'");
		}
		// 供应商
		if (!supCode.equals("")) {
			SQL.append(" AND B.SUP_CODE='" + supCode + "'");
		}
		// 上级供应商
		if (!exSupCode.equals("")) {
			SQL.append(" AND　B.UP_SUP_CODE='" + exSupCode + "'");
		}
		// 料位
		if (!locCode.equals("")) {
			SQL.append(" AND　A.MATERIAL_LOC_CODE ='" + locCode + "'");
		}

		// 库存不为0
		if (this.getCheckBox("QTY_FLG").isSelected()) {
			SQL.append(" AND A.STOCK_QTY>0");
		}

		// 安全库存量
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
		// 判断错误值
		if (result == null || result.getCount() <= 0) {
			callFunction("UI|TABLE|removeRowAll");
			callFunction("UI|TABLEDD|removeRowAll");
			this.messageBox("没有查询数据");
			this.clearValue("NUM");
			this.clearValue("TOT_VALUE");
			return;
		}
		// 笔数
		int num = 0;
		// 总数量
		double numTot = 0;
		double totValue = 0.00;
		for (int i = 0; i < result.getCount(); i++) {
			num++;
			totValue = totValue + result.getDouble("TOT", i);
			numTot = numTot + result.getDouble("STOCK_QTY", i);

			// 获取补货量后将所有<0的数据均设为0
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
		totValue = Math.abs(totValue); // 2014-01-15添加
		DecimalFormat df = new DecimalFormat("0.00");
		String AllFee = df.format(totValue);
		AllFee = feeConversion(AllFee);// 加逗号处理
		// this.setValue("NUM", num);
		this.setValue("NUM", numTot);// 20150701 wangjc modify
		this.setValue("NUM_TOT", numTot);
		this.setValue("TOT_VALUE", AllFee);
		this.callFunction("UI|TABLE|setParmValue", result);

	}

	/**
	 * 每三位加逗号处理
	 */
	public String feeConversion(String fee) {
		String str1 = "";
		String[] s = fee.split("\\.");// 以"."来分割
		// reverse翻转
		str1 = new StringBuilder(s[0].toString()).reverse().toString();// 先将字符串颠倒顺序
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
		// 最后再将顺序反转过来
		String str3 = new StringBuilder(str2).reverse().toString();
		// 加上小数点后的数
		StringBuffer str4 = new StringBuffer(str3);
		str4 = str4.append(".").append(s[1]);
		return str4.toString();
	}

	/**
	 * 清空
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
	 * 主表点击事件
	 */
	public void onTableClicked() {
		// messageBox("主表点击事件");
		// //效期起
		// String vaildTimeS =
		// this.getValueString("VAILD_S").replace("-","").substring(0, 8);
		// //效期讫
		// String vaildTimeE =
		// this.getValueString("VAILD_E").replace("-","").substring(0, 8);
		// 科室
		String deptcode = this.getValueString("DEPT_CODE");
		int row = this.getTable("TABLE").getClickedRow();
		TParm parm = this.getTable("TABLE").getParmValue().getRow(row);
		// 查询截止日期
		// String verTime = this.getValueString("VERIFYIN_DATE");
		// System.out.println("parm"+parm);
		String regionCode = Operator.getRegion();
		// rfid,100;物资编码,100;物资名称,100;规格,200;单位,50;
		// 采购价格（单价）,120;采购金额（总额）,120;供应商,100;上级供应商,100;生产商,100;物资分类,100
		// RFID;INV_CODE;INV_CHN_DESC;DESCRIPTION;STOCK_UNIT;CONTRACT_PRICE;TOT_DD;SUP_CODE;UP_SUP_CODE;MAN_CODE;INV_KIND

//		String sql = " SELECT A.RFID,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,B.STOCK_UNIT,"
//				+ "        C.CONTRACT_PRICE,C.CONTRACT_PRICE * A.STOCK_QTY AS TOT_DD,B.SUP_CODE,B.UP_SUP_CODE,"
//				+ "        B.MAN_CODE,B.INV_KIND,A.BATCH_NO,A.VALID_DATE,A.BATCH_SEQ "
//				+ "		   ,A.ORGIN_CODE "
//				+ // 20150127 wangjingchun add SN号
//				"   FROM INV_STOCKDD A,INV_BASE B,INV_AGENT C"
//				+ "   WHERE    A.INV_CODE = B.INV_CODE"
//				+ "   AND      A.INV_CODE = C.INV_CODE"
//				+ "   AND      B.SUP_CODE = C.SUP_CODE"
//				+ // 20161229 lij 增加条件
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
		// 查询截止日期
		// if(!verTime.equals("")){
		// verTime = verTime.substring(0, 10).replace('-', ' ').trim();
		// SQL.append(" AND A.VERIFYIN_DATE <= TO_DATE ('"+verTime+"235959"+"', 'yyyyMMddHH24miss')");
		// }
		// 效期
		// SQL.append(" AND A.VALID_DATE BETWEEN TO_DATE ('" + vaildTimeS +
		// "000000" +
		// "', 'yyyyMMddHH24miss')" +
		// "    AND TO_DATE ('" + vaildTimeE + "235959" +
		// "', 'yyyyMMddHH24miss')");
		SQL.append(" ORDER BY A.INV_CODE, A.ORG_CODE, A.INVSEQ_NO");
		// messageBox("主表点击事件SQL明细"+SQL);
		//System.out.println("SQL明细 = = = = = "+SQL.toString());
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(SQL.toString()));
		if (result.getCount() <= 0) {
			callFunction("UI|TABLEDD|removeRowAll");
			return;
		}
		this.callFunction("UI|TABLEDD|setParmValue", result);
		// 算法有问题
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
	 * 获取月份差
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
	 * 打印
	 */
	public void onPrint() {
		// if (this.getTable("TABLE").getRowCount() <= 0) {
		// this.messageBox("没有要打印的数据");
		// return;
		// }
		// TParm prtParm = new TParm();
		// //表头
		// prtParm.setData("TITLE","TEXT","物资库存统计报表");
		// //日期
		// prtParm.setData("PRINT_DATE","TEXT","打印日期：" +
		// StringTool.getString(StringTool.getTimestamp(new Date()),
		// "yyyy年MM月dd日"));
		// // //财产总计
		// // prtParm.setData("TOT","TEXT", "财产总计："
		// +this.getValueDouble("TOT_VALUE"));
		// //得到带控件
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
		// //表尾
		// prtParm.setData("USER","TEXT", "制表人：" + Operator.getName());
		// this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVStockQueryReport.jhw",
		// prtParm);
	}

	/**
	 * 批次序号注记改变事件
	 */
	public void onCharge() {
		if (this.getCheckBox("VAILD_FLG").isSelected()) {
			this.getCheckBox("BASE_FLG").setEnabled(false);
		} else {
			this.getCheckBox("BASE_FLG").setEnabled(true);
		}
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (this.getTable("TABLE").getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
					"物资库存统计报表");
		}
	}

	// $$==============add by wangjingchun 20150123 加入排序功能start=============$$//
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTable("TABLE").getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = getTable("TABLE").getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// 行数据->列
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
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
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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

	// $$==============add by wangjingchun 20150123 加入排序功能end=============$$//
	/**
	 * 得到表控件
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到文本控件
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TCheckBox对象
	 * 
	 * @param tagName
	 *            String
	 * @return TCheckBox
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
