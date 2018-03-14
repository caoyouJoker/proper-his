package com.javahis.ui.pes;

import jdo.pes.PESTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title:门诊处方点评
 * </p>
 * 
 * <p>
 * Description:门诊处方点评
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author zhangp 2012.8.2
 * @version 1.0
 */
public class PESMainControl extends TControl {
	private static TTable tableM;
	private static TTable tableD;
	private static int row = -1;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		setValue("EVAL_CODE", Operator.getID());
		setValue("REGION_CODE", Operator.getRegion());
		tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (getValueString("PES_NO").equals("")) {
			messageBox("请选择点评期间");
			return;
		}

		if (getValueString("PES_TYPE").equals("")) {
			messageBox("请选择点评类别");
			return;
		}
		parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		parm.setData("PES_NO", getValueString("PES_NO"));
		TParm result = PESTool.getInstance().selectOPDM(parm);
		//add by huangtt 20131203 start 
		for(int i=0;i<result.getCount();i++){
			String zd = result.getValue("ICD_CHN_DESC", i);
			String zd1 [] = zd.split(";");
			String str = "";
			for(int j=0;j<zd1.length;j++){
				str =str+ "'"+zd1[j]+"',";
			}
			String sql = "SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE IN("+str.substring(0,str.length()-1)+")";
			TParm zdParm = new TParm(TJDODBTool.getInstance().select(sql));
			String icdDesc="";
			for(int k=0;k<zdParm.getCount();k++){
				icdDesc= icdDesc + zdParm.getValue("ICD_CHN_DESC", k)+"；";
			}
			result.setData("ICD_CHN_DESC", i, icdDesc.subSequence(0, icdDesc.length()-1));
		}
		
		//add by huangtt 20131203 end
		tableM.setParmValue(result);
	}

	/**
	 * table1点击事件
	 */
	public void onClickTableM() {
		if (row != tableM.getSelectedRow()) {
			onSave("");
		}
		row = tableM.getSelectedRow();
		TParm parm = new TParm();
		parm.setData("PES_NO", getValueString("PES_NO"));
		parm.setData("CASE_NO", tableM.getParmValue().getValue("CASE_NO", row));
		parm.setData("PES_RX_NO", tableM.getParmValue().getValue("PES_RX_NO",
				row));
		TParm result = PESTool.getInstance().selectOPDD(parm);
		tableD.setParmValue(result);
	}

	/**
	 * 保存按钮
	 */
	public void onSave() {
		onSave("保存成功");
	}

	/**
	 * 保存
	 */
	private void onSave(String message) {
		if (row != -1) {
			tableM.acceptText();
			tableD.acceptText();
			TParm parmM = tableM.getParmValue().getRow(row);
			TParm parmD = tableD.getParmValue();
			// System.out.println(parmM);
			// System.out.println(parmD);
			TParm parm = new TParm();
			parm.setData("OPDM", parmM.getData());
			parm.setData("OPDD", parmD.getData());
			parm.setData("EVAL_CODE", getValue("EVAL_CODE"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result = TIOM_AppServer.executeAction("action.pes.PESAction",
					"updatePESOPDMD", parm);
			if (result.getErrCode() < 0) {
				messageBox("插入失败");
				return;
			}
		}
		if (!message.equals("")) {
			messageBox(message);
		}
	}

	public void onSavePESResult() {
		TParm parm = new TParm();
		if (getValueString("PES_NO").equals("")) {
			messageBox("请选择点评期间");
			return;
		}

		if (getValueString("PES_TYPE").equals("")) {
			messageBox("请选择点评类别");
			return;
		}
		parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		parm.setData("PES_NO", getValueString("PES_NO"));
		TParm tableParm = PESTool.getInstance().selectOPDM(parm);
		parm = new TParm();
		double order_qty = 0;// A：用药品种总数
		double antibiotic_qty = 0;// C：使用抗菌药的处方数
		double inject_qty = 0;// E：使用注射剂的处方数
		double base_qty = 0;// G：处方中基本药物品种总数
		double goods_qty = 0;// I：处方中使用药品通用名总数
		double rx_total = 0;// K：处方总金额
		double o = 0;// O：合理处方总数
		for (int i = 0; i < tableParm.getCount("SEQ"); i++) {
			order_qty += tableParm.getInt("ORDER_QTY", i);
			antibiotic_qty += tableParm.getInt("ANTIBIOTIC_QTY", i);
			inject_qty += tableParm.getInt("INJECT_QTY", i);
			base_qty += tableParm.getInt("BASE_QTY", i);
			goods_qty += tableParm.getInt("GOODS_QTY", i);
			rx_total += tableParm.getDouble("RX_TOTAL", i);
			if (!tableParm.getValue("REASON_FLG", i).equals("Y")) {
				o++;
			}
		}
		double b = order_qty / tableParm.getCount("SEQ");// B：平均每张处方用药品种数 =
		// A/处方总数
		double d = antibiotic_qty / tableParm.getCount("SEQ");// D：抗菌药使用百分率=
		// C/处方总数
		double f = inject_qty / tableParm.getCount("SEQ");// F：注射剂使用百分率= E/处方总数
		double h = base_qty / order_qty;// H：国家基本药物占处方用药的百分率= G/A
		double j = goods_qty / order_qty;// J：药品通用名占处方用药的百分率=I/A
		double l = StringTool.round(rx_total, 2) / tableParm.getCount("SEQ");// L：平均每张处方金额＝K/处方总数
		double p = o / tableParm.getCount("SEQ");// P：合理处方百分率：O/处方总数
		TParm printParm = new TParm();
		printParm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		printParm.setData("PES_NO", getValueString("PES_NO"));
		printParm.setData("PES_A", Double.valueOf(order_qty).intValue());
		printParm.setData("PES_B", StringTool.round(b, 1));
		printParm.setData("PES_C", Double.valueOf(antibiotic_qty).intValue());
		printParm.setData("PES_D", StringTool.round(d, 1));
		printParm.setData("PES_E", Double.valueOf(inject_qty).intValue());
		printParm.setData("PES_F", StringTool.round(f, 1));
		printParm.setData("PES_G", Double.valueOf(base_qty).intValue());
		printParm.setData("PES_H", StringTool.round(h, 1));
		printParm.setData("PES_I", Double.valueOf(goods_qty).intValue());
		printParm.setData("PES_J", StringTool.round(j, 1));
		printParm.setData("PES_K", StringTool.round(rx_total, 2));
		printParm.setData("PES_L", StringTool.round(l, 2));
		printParm.setData("PES_O", Double.valueOf(o).intValue());
		printParm.setData("PES_P", StringTool.round(p, 2));
		printParm.setData("OPT_USER", Operator.getID());
		printParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction("action.pes.PESAction",
				"updatePESResult", printParm);
		if (result.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		}
		messageBox("保存成功");
	}

	/**
	 * 处方点评工作表
	 */
	public void onPrintPesOpdm() {
		TParm parm = new TParm();
		if (getValueString("PES_NO").equals("")) {
			messageBox("请选择点评期间");
			return;
		}

		if (getValueString("PES_TYPE").equals("")) {
			messageBox("请选择点评类别");
			return;
		}
		parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
		parm.setData("PES_NO", getValueString("PES_NO"));
		TParm tableParm = PESTool.getInstance().selectOPDMforPrint(parm);
//		 System.out.println("tableParm=="+tableParm);
		//add by huangtt 20131203 start 
		for(int i=0;i<tableParm.getCount();i++){
			String zd = tableParm.getValue("ICD_CHN_DESC", i);
			String zd1 [] = zd.split(";");
			String str = "";
			for(int j=0;j<zd1.length;j++){
				str =str+ "'"+zd1[j]+"',";
			}
			String sql = "SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE IN("+str.substring(0,str.length()-1)+")";
			TParm zdParm = new TParm(TJDODBTool.getInstance().select(sql));
			String icdDesc="";
			for(int k=0;k<zdParm.getCount();k++){
				icdDesc= icdDesc + zdParm.getValue("ICD_CHN_DESC", k)+"；";
			}
			tableParm.setData("ICD_CHN_DESC", i, icdDesc.subSequence(0, icdDesc.length()-1));
		}
		
		//add by huangtt 20131203 end
		
		TParm resultParm = PESTool.getInstance().selectPESResult(parm);
		// System.out.println("resultParm=="+resultParm);
		parm = new TParm();
		double order_qty = resultParm.getDouble("PES_A", 0);// A：用药品种总数
		double antibiotic_qty = resultParm.getDouble("PES_C", 0);// C：使用抗菌药的处方数
		double inject_qty = resultParm.getDouble("PES_E", 0);// E：使用注射剂的处方数
		double base_qty = resultParm.getDouble("PES_G", 0);// G：处方中基本药物品种总数
		double goods_qty = resultParm.getDouble("PES_I", 0);// I：处方中使用药品通用名总数
		double rx_total = resultParm.getDouble("PES_K", 0);// K：处方总金额
		double o = resultParm.getDouble("PES_O", 0);// O：合理处方总数
		double b = resultParm.getDouble("PES_B", 0);// B：平均每张处方用药品种数 =
		// A/处方总数
		double d = resultParm.getDouble("PES_D", 0);// D：抗菌药使用百分率=
		// C/处方总数
		double f = resultParm.getDouble("PES_F", 0);// F：注射剂使用百分率= E/处方总数
		double h = resultParm.getDouble("PES_H", 0);// H：国家基本药物占处方用药的百分率= G/A
		double j = resultParm.getDouble("PES_J", 0);// J：药品通用名占处方用药的百分率=I/A
		double l = resultParm.getDouble("PES_L", 0);// L：平均每张处方金额＝K/处方总数
		double p = resultParm.getDouble("PES_P", 0);// P：合理处方百分率：O/处方总数
		tableParm.addData("SYSTEM", "COLUMNS", "SEQ");
		tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
		tableParm.addData("SYSTEM", "COLUMNS", "AGE");
		tableParm.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "ORDER_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "ANTIBIOTIC_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "INJECT_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "BASE_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "GOODS_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "RX_TOTAL");
		tableParm.addData("SYSTEM", "COLUMNS", "DR_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "PHA_DOSAGE_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "PHA_DISPENSE_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "REASON_FLG");
		tableParm.addData("SYSTEM", "COLUMNS", "QUESTION_CODE");
		TParm printParm = new TParm();
		printParm.setData("TABLE", tableParm.getData());
		printParm.setData("A", Double.valueOf(order_qty).intValue());
		printParm.setData("B", StringTool.round(b, 1));
		printParm.setData("C", Double.valueOf(antibiotic_qty).intValue());
		printParm.setData("D", (StringTool.round(d, 1)*100+"").substring(0, (StringTool.round(d, 1)*100+"").length()-2));
		printParm.setData("E", Double.valueOf(inject_qty).intValue());
		printParm.setData("F", (StringTool.round(f, 1)*100+"").substring(0, (StringTool.round(f, 1)*100+"").length()-2));
		printParm.setData("G", Double.valueOf(base_qty).intValue());
		printParm.setData("H", (StringTool.round(h, 1)*100+"").substring(0, (StringTool.round(h, 1)*100+"").length()-2));
		printParm.setData("I", Double.valueOf(goods_qty).intValue());
		printParm.setData("J", (StringTool.round(j, 1)*100+"").substring(0, (StringTool.round(j, 1)*100+"").length()-2));
		printParm.setData("K", StringTool.round(rx_total, 2));
		printParm.setData("L", StringTool.round(l, 2));
		printParm.setData("O", Double.valueOf(o).intValue());
		printParm.setData("P", StringTool.round(p, 1));
		
		String date = resultParm.getValue("PES_DATE", 0).toString();
//		printParm.setData("PES_TIME", "TEXT", "填表日期: " );
//				+ date.substring(0, 4)
//				+ "/" + date.substring(5, 7) + "/" + date.substring(8, 10)
		printParm.setData("REGION", "TEXT", Operator.getHospitalCHNShortName());
		printParm.setData("EVAL_CODE", "TEXT", resultParm.getValue("EVAL_CODE",
				0));
		this.openPrintWindow("%ROOT%\\config\\prt\\PES\\PESWorkList.jhw",
				printParm);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		tableM.acceptText();
		// 得到UI对应控件对象的方法
		TParm parm = tableM.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(tableM, "处方点评处方列表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		TParm parm = new TParm();
		tableM.setParmValue(parm);
		tableD.setParmValue(parm);
	}
}
