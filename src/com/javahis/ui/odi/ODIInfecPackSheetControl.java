package com.javahis.ui.odi;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jdo.odi.OdiMainTool;
import jdo.pha.PhaAntiTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OrderUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:住院医生站传回抗菌药物
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author SHIBL
 * @version 4.0
 */
public class ODIInfecPackSheetControl extends TControl {
	/**
	 * 传入参数
	 */
	private TParm parmmeter;

	private TTable tablem;

	private TTable table1;

	private String rxKind;

	private String caseNo;//就诊号
	
	private String mrNo;//病案号
	
	private Timestamp admDate;//入院日期
	
	private String ipdNo;//住院号
	
	private String patName;//病患姓名

	private String type;
	
	private String packCode;
	
	private String icdCode;
	
	private String icdDesc;
	
	private String catCode;//切口代码
	
//	private int antiTakeDays = 0;//抗菌药品使用天数
	
	private int day=7;//抗菌药品使用天数
	
	private String radioFlg="";//标记哪个radioButton被选中
	
	private String arvFlg = "";//是否越权标记
	
	private String catDesc= "";
	/**
	 * 
	 * 临时用药预设频次
	 */
	private String odiUddStatCode;
	/**
	 * 临时处置预设频次
	 */
	private String odiStatCode;
	/**
	 * 长期处置预设频次
	 */
	private String odiDefaFreg;
	/**
	 * 预设饮片付数
	 */
	private String dctTakeDays;
	/**
	 * 预设饮片使用计量
	 */
	private String dctTakeQty;

	public void onInitParameter() {
		// 测试数据
	}

	/**
	 * 初始化参数
	 */
	public void onInit() {
		this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
		}
		type = parmmeter.getValue("TYPE");// UDD长期、ST临时
		caseNo = parmmeter.getValue("CASE_NO");// add caoyong 添加感染建议
		mrNo = parmmeter.getValue("MR_NO");// add caoyong 添加感染建议
		admDate = parmmeter.getTimestamp("ADM_DATE");// add YANJ 添加感染建议
		ipdNo = parmmeter.getValue("IPD_NO");// add caoyong 添加感染建议
		patName = parmmeter.getValue("PAT_NAME");
		packCode = parmmeter.getValue("PACK_CODE");
		this.setValue("DESC", parmmeter.getValue("DESC"));
		// 初始化
		onChangeStart();
	}

	/**
	 * 页签改变事件 type 页签分类 W:治疗 OP 预防 add caoyong 2013905
	 */
	public void onChangeStart() {
		onInitPage();// modify caoyong 2013805 初始化
		// TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (type.equals("UDD")) {
			icdCode = parmmeter.getValue("PACK_CODE1");
			icdDesc = parmmeter.getValue("DESC1");
			if (parmmeter.getValue("DESC").length()<=0) {//===pangben 2013-12-23 没有拟诊诊断
				this.callFunction("UI|WRDO|setEnabled", false);
				((TRadioButton) this.getComponent("OPRDO")).setSelected(true);
			}
			onQuery("W");// mofifby caoyong 2013805 查询不同类型的数据
			this.onChang2();
			//查询是否有手术
			String sql1 = "SELECT PHA_PREVENCODE FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+icdCode+"'";
//			System.out.println("输出sql is:::"+sql1);
			TParm resultparm = new TParm(TJDODBTool.getInstance().select(sql1));
			if(resultparm.getCount()<=0){
				this.callFunction("UI|OPRDO|setEnabled", false);
			}
		} else {
			String catType = "SELECT B.ID,B.CHN_DESC FROM SYS_OPERATIONICD A,SYS_DICTIONARY B " +
					"WHERE A.PHA_PREVENCODE = B.ID AND B.GROUP_ID = 'PHA_PREVEN' " +
					"AND A.OPERATION_ICD = '"+parmmeter.getValue("PACK_CODE")+"'";
			TParm catParm = new TParm(TJDODBTool.getInstance().select(catType));
			catCode = catParm.getValue("ID",0);
			catDesc =catParm.getValue("CHN_DESC",0);
			this.setValue("CAT_TYPE",catParm.getValue("CHN_DESC",0));
			onQuery("OP");// mofifby caoyong 2013805 查询不同类型的数据
			this.onChang1();
		}
	}

	private void onInitPage() {
		// TODO Auto-generated method stub
		rxKind = parmmeter.getValue("RX_KIND");
		tablem = this.getTTable("TABLE");
		table1 = this.getTTable("TABLE1");
		// 临时用药预设频次
		this.setOdiUddStatCode(getOdiSysParmData("UDD_STAT_CODE").toString());
		// 临时处置预设频次
		this.setOdiStatCode(getOdiSysParmData("ODI_STAT_CODE").toString());
		// 长期处置预设频次
		this.setOdiDefaFreg(getOdiSysParmData("ODI_DEFA_FREG").toString());
		// 预设饮片付数
		this.setDctTakeDays(getOpdSysParmData("DCT_TAKE_DAYS").toString());
		// 预设饮片使用计量
		this.setDctTakeQty(getOpdSysParmData("DCT_TAKE_QTY").toString());
		// onQuery();
	}

	/**
	 *临时 页签radiobutton单击事件
	 * yanjing 20130930
	 */
	public void onChang1() {
		if(((TRadioButton) this.getComponent("WRDO")).isSelected()){
			TParm parmorder =null;
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			parm.setData("APPROVE_FLG","Y");//会诊建议
			//parm.setData("USE_FLG","N");//医生使用状态
			parmorder=PhaAntiTool.getInstance().queryPhaAnti(parm);
			if (parmorder.getErrCode() < 0) {
				this.messageBox("套餐医嘱查询错误");
				return;
			}
			TParm parmTable=new TParm();
			TParm ope_result=new TParm();
			String sql="";
			for (int i = 0; i < parmorder.getCount(); i++) {
				if (parmorder.getValue("USE_FLG",i).equals("Y")) {//医生已经使用的医嘱
					sql="SELECT CASE_NO FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
					"' AND RX_KIND='UD' AND ORDER_CODE='"+parmorder.getValue("ORDER_CODE",i)+"'";
					ope_result = new TParm(TJDODBTool.getInstance().select(sql));//查询长期医嘱是否开立
					if (ope_result.getCount()>0) {//长期医嘱已经存在，临时医嘱可以开立一次
						sql="SELECT CASE_NO,ORDER_DESC FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
						"' AND RX_KIND='ST' AND ORDER_CODE='"+parmorder.getValue("ORDER_CODE",i)+"'";
						ope_result = new TParm(TJDODBTool.getInstance().select(sql));//校验临时医嘱是否已经开立
						if (ope_result.getCount()<=0) {
							parmTable.addRowData(parmorder, i);
						}
					}
				}else{
					parmTable.addRowData(parmorder, i);
				}
				if (this.rxKind.equals("ST")) {
					parmTable.setData("FREQ_CODE", i, getOdiUddStatCode());
				}
				if (this.rxKind.equals("DS")) {
					parmTable.setData("TAKE_DAYS", i, 1);
				}
			}
			parmTable.setCount(parmTable.getCount("ORDER_CODE"));
			table1.removeRowAll();
			table1.setParmValue(parmTable);
		}else if(((TRadioButton) this.getComponent("OPRDO")).isSelected()){
			TParm inparm = new TParm();
			String sql ="";
			this.setValue("LBL", "手术：");
			this.setValue("DESC", parmmeter.getValue("DESC"));
			this.setValue("CAT_TYPE", catDesc);
			//根据手术或者诊断的代码，判定套餐代码
			String sql1 = "SELECT PACK_CODE FROM ODI_INFEC_PACKM WHERE PHA_PREVENCODE = '"+catCode+"'";
			TParm resultparm = new TParm(TJDODBTool.getInstance().select(sql1));
			String packcode = resultparm.getValue("PACK_CODE", 0);
			sql = "SELECT 'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION," 
				
				//modify by yangjj 20150529 
				+ " B.ORDER_DESC," 
				//+ " A.ORDER_DESC," 
				
				+ " A.TRADE_ENG_DESC, "
				+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY, MEDI_UNIT, "
				+ " ROUTE_CODE,'"+getOdiUddStatCode()+"' AS FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
				+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
				+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
				+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
				+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
				+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
				+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
				+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
				+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B  WHERE PACK_CODE='"
				+ packcode
				+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
				+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
			inparm = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < inparm.getCount(); i++) {
				if (this.rxKind.equals("DS")) {
					inparm.setData("TAKE_DAYS", i, 1);
				}
			}
			table1.removeRowAll();
		    table1.setParmValue(inparm);
//		    this.table1.setRowColor(0, new Color(255, 255, 132));
		}else if(((TRadioButton) this.getComponent("ORDO")).isSelected()){//诊断
			//radioFlg = "ORDO";//治疗标记
			this.setValue("LBL", "诊断：");
			this.setValue("DESC", parmmeter.getValue("DESC1"));
			TParm icdparm = parmmeter.getParm("ICDPARM");	
			this.setValue("CAT_TYPE", "");
			TParm inparm = this.getTableParm(icdparm);
			String packcode = inparm.getValue("PACK_CODE", 0);
			String sql = "SELECT C.TAKE_DAYS AS ANTI_TAKE_DAYS, 'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
				+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT, "
				+ " A.ROUTE_CODE,'"+getOdiUddStatCode()+"' AS FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
				+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
				+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
				+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
				+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
				+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
				+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
				+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
				+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B,SYS_ANTIBIOTIC C,PHA_BASE D WHERE PACK_CODE='"
				+ packcode
				+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
				+ "  AND C.ANTIBIOTIC_CODE = D.ANTIBIOTIC_CODE AND D.ORDER_CODE = A.ORDER_CODE  "
				+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
			inparm = new TParm(TJDODBTool.getInstance().select(sql));
			table1.removeRowAll();
		    table1.setParmValue(inparm);
		}
		
	}
	/**
	 * 长期页签radiobutton单击事件
	 */
	public void onChang2() {
		if(((TRadioButton) this.getComponent("CONS")).isSelected()){//会诊结果
			radioFlg = "CONS";//会诊结果的标记
			TParm parmorder =null;
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			parm.setData("APPROVE_FLG","Y");//会诊建议
			parm.setData("USE_FLG","N");//医生使用状态
			parmorder=PhaAntiTool.getInstance().queryPhaAnti(parm);
			if (parmorder.getErrCode() < 0) {
				this.messageBox("套餐医嘱查询错误");
				return;
			}
			for (int i = 0; i < parmorder.getCount(); i++) {
				if(parmorder.getInt("ANTI_TAKE_DAYS", i) == 0){
//					this.messageBox("请填写手术申请单!");
//					return;
				}else{
					day = parmorder.getInt("ANTI_TAKE_DAYS", i);
				if (this.rxKind.equals("ST")) {
					parmorder.setData("FREQ_CODE", i, getOdiUddStatCode());
				}
				if (this.rxKind.equals("DS")) {
					parmorder.setData("TAKE_DAYS", i, 1);
				}
				}
			}
			
			table1.removeRowAll();
			table1.setParmValue(parmorder);
			}else if(((TRadioButton) this.getComponent("OPRDO")).isSelected()){//手术
				radioFlg = "OPRDO";//预防标记
				this.setValue("DESC", icdDesc);
				this.setValue("NAME", "手术：");
				TParm inparm = new TParm();
				String sql ="";
				//根据手术或者诊断的代码，判定套餐代码
				String sql1 = "SELECT PHA_PREVENCODE FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+icdCode+"'";
				TParm resultparm = new TParm(TJDODBTool.getInstance().select(sql1));
				String sql2 = "SELECT PACK_CODE FROM ODI_INFEC_PACKM WHERE PHA_PREVENCODE = '"+resultparm.getValue("PHA_PREVENCODE", 0)+"'";
				TParm resultparm1 = new TParm(TJDODBTool.getInstance().select(sql2));
				String packcode = resultparm1.getValue("PACK_CODE", 0);
				sql = "SELECT C.DESCRIPTION AS ANTI_TAKE_DAYS,'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
					+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,MEDI_UNIT, "
					+ " ROUTE_CODE,FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
					+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
					+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
					+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
					+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
					+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
					+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
					+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
					+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B,SYS_DICTIONARY C,ODI_INFEC_PACKM D WHERE A.PACK_CODE='"
					+ packcode
					+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
					+ "  AND D.PHA_PREVENCODE=C.ID AND C.GROUP_ID='PHA_PREVEN' AND D.SMEALTYPE='OP' AND A.PACK_CODE=D.PACK_CODE  "
					+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
				inparm = new TParm(TJDODBTool.getInstance().select(sql));
				table1.removeRowAll();
			    table1.setParmValue(inparm);
//			    this.table1.setRowColor(0, new Color(255, 255, 132));
			}else if(((TRadioButton) this.getComponent("WRDO")).isSelected()){//诊断
				radioFlg = "WRDO";//治疗标记
				this.setValue("NAME", "诊断：");
				this.setValue("DESC", parmmeter.getValue("DESC"));
				TParm icdparm = parmmeter.getParm("ICDPARM");	
				TParm inparm = this.getTableParm(icdparm);
				String packcode = inparm.getValue("PACK_CODE", 0);
				String sql = "SELECT C.TAKE_DAYS AS ANTI_TAKE_DAYS, 'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
					+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT, "
					+ " A.ROUTE_CODE,A.FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
					+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
					+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
					+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
					+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
					+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
					+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
					+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
					+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B,SYS_ANTIBIOTIC C,PHA_BASE D WHERE PACK_CODE='"
					+ packcode
					+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
					+ "  AND C.ANTIBIOTIC_CODE = D.ANTIBIOTIC_CODE AND D.ORDER_CODE = A.ORDER_CODE  "
					+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
				inparm = new TParm(TJDODBTool.getInstance().select(sql));
				table1.removeRowAll();
			    table1.setParmValue(inparm);
			}
			
	}
	/**
	 * 预防查询数据 手术
	 */
	private TParm onQueryST(String typeRdo){
		String sql = "SELECT 'N' AS FLG,PACK_CODE,PACK_DESC FROM ODI_INFEC_PACKM WHERE SMEALTYPE='"
				+ typeRdo + "'ORDER BY PACK_CODE"; // modify caoyong 2013805
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("查询错误");
			return parm;
		}
		return parm;
	}
	/**
	 * 治疗查询数据 诊断
	 */
	private TParm onQueryUDD(String typeRdo){
		TParm icdparm = parmmeter.getParm("ICDPARM");	
		TParm inparm = this.getTableParm(icdparm);
		return inparm;
	}
	/**
	 * 查询
	 */
	public void onQuery(String typeRdo) {
		TParm inparm=new TParm();
		if (type.equals("ST")) {//临时 手术
			inparm=onQueryST(typeRdo);
		}else if (type.equals("UDD")){//长期 UDD 诊断
			if (((TRadioButton) this.getComponent("WRDO")).isSelected()) {
				inparm=onQueryUDD(typeRdo);
			}
		}
		if (inparm.getErrCode()<0) {
			return;
		}
	}

	/**
	 * 治疗数据查询
	 * @param parm
	 * @return
	 */
	private TParm getTableParm(TParm icdparm) {
		if (icdparm.getCount() <= 0)
			return icdparm;
		String sql = "SELECT DISTINCT  A.PACK_CODE,B.PACK_DESC,'N' AS FLG "
				+ " FROM ODI_INFEC_PACKICD A,ODI_INFEC_PACKM B"
				+ " WHERE A.PACK_CODE=B.PACK_CODE AND B.SMEALTYPE='W' AND (";
		String line = "";
		for (int i = 0; i < icdparm.getCount(); i++) {
			TParm parmRow = icdparm.getRow(i);
			String icd = parmRow.getValue("ICD_CODE1");
			if (line.length() > 0) {
				line += " OR ";
			}
			line += " '" + icd + "'"
					+ " BETWEEN A.ICD_CODE_BEGIN AND A.ICD_CODE_END";
		}
		TParm check = new TParm(TJDODBTool.getInstance().select(
				sql + line + ")"));
		if (check.getErrCode() < 0){
			this.messageBox("查询错误");
			return check;
		}
		return check;
	}
	/**
	 * 传回
	 */
	public void onSend() {
		String anti_flg = "02";//治疗，设置抗菌标识
		this.table1.acceptText();// modify caoyong 201394
		TParm parm = table1.getParmValue();// modify caoyong 201394
		int rowCount = parm.getCount();
		TParm result=new TParm();
		if ((type.equals("UDD")&&((TRadioButton) this.getComponent("OPRDO")).isSelected())) {
			
//			String sql="SELECT TIMEOUT_DATE,OP_DATE FROM OPE_OPBOOK WHERE CASE_NO = '"+caseNo+"' " +
//				"AND OP_CODE1 = '"+parmmeter.getValue("PACK_CODE1")+"'" +
//						"ORDER BY TIMEOUT_DATE DESC";//TIME OUT 时间=====pangben 201403-4 
//			TParm opeResult =new TParm(TJDODBTool.getInstance().select(sql));
//			if (opeResult.getCount("TIMEOUT_DATE")>0&&
//					null!=opeResult.getData("TIMEOUT_DATE", 0)&&opeResult.getData("TIMEOUT_DATE", 0).toString().length()>0) {
//				result.addData("OP_START_DATE", opeResult.getTimestamp("TIMEOUT_DATE", 0));
//			}else{
//				sql = "SELECT OP_START_DATE FROM OPE_OPDETAIL WHERE CASE_NO = '"+caseNo+"' " +
//				"AND OP_CODE1 = '"+parmmeter.getValue("PACK_CODE1")+"'" +
//						"ORDER BY OP_START_DATE DESC";
//				TParm ope_result = new TParm(TJDODBTool.getInstance().select(sql));
//				if (ope_result.getCount("OP_START_DATE")>0&&
//						null!=ope_result.getData("OP_START_DATE", 0)&&ope_result.getData("OP_START_DATE", 0).toString().length()>0) {
//					result.addData("OP_START_DATE", ope_result.getTimestamp("OP_START_DATE", 0));//手术切皮时间
//				}else if (opeResult.getCount("OP_DATE")>0&&
//						null!=opeResult.getData("OP_DATE", 0)&&opeResult.getData("OP_DATE", 0).toString().length()>0) {
//					result.addData("OP_START_DATE", opeResult.getTimestamp("OP_DATE", 0));//手术申请时间
//				}else{
//					result.addData("OP_START_DATE",SystemTool.getInstance().getDate());
//				}
//			}
			anti_flg = "01";//预防，设置抗菌标识
//			System.out.println("qwerty停用时间 is：：："+ope_result.getData("OP_START_DATE", 0));
//			System.out.println("qwerty停用时间 is：：："+ope_result.getData("OP_START_DATE"));
		}
		TParm ope_result=null;
		String sql="";
		if ((type.equals("UDD")&&((TRadioButton) this.getComponent("WRDO")).isSelected())) {//治疗默认七天
//			// 检查证照
//			Object obj = parm.getData("LCS_CLASS_CODE");
//			if (obj != null && obj.toString().length() != 0) {
//				if (!OrderUtil.getInstance().checkLcsClassCode(Operator.getID(),
//						"" + obj)) {
//					if(messageBox("提示信息 Tips", "是否越权? ",
//							this.YES_NO_OPTION)!= 0){//否
//						arvFlg = "NO";
//					}else{//是
//						arvFlg = "YES";
//					}
//				}
//			}
			
		}else if(type.equals("ST")&&(((TRadioButton) this.getComponent("ORDO")).isSelected()||
				((TRadioButton) this.getComponent("OPRDO")).isSelected())){//临时医嘱，诊断或预防
			
			for (int i = 0; i < rowCount; i++) {
				TParm temp = parm.getRow(i);
				if (!temp.getBoolean("FLG")) {
					continue;
				}//=======pangben 2014-4-25 预防用药一次手术申请仅能开一次临时医嘱，皮试用药不计算
				sql="SELECT CASE_NO,ORDER_DATE FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
				"' AND RX_KIND='UD' AND ORDER_CODE='"+temp.getValue("ORDER_CODE")+"'";
				ope_result = new TParm(TJDODBTool.getInstance().select(sql));//查询长期医嘱是否开立			
				if (ope_result.getCount()>0) {
					sql = "SELECT CASE_NO,ORDER_DESC FROM ODI_ORDER WHERE CASE_NO='"
						+ caseNo
						+ "' AND RX_KIND='ST' AND ORDER_CODE='"
						+ temp.getValue("ORDER_CODE")
						+ "' AND ROUTE_CODE<>'PS' AND ORDER_DATE>TO_DATE('"
						+ SystemTool.getInstance().getDateReplace(ope_result.getValue("ORDER_DATE",0), true)+"','YYYYMMDDHH24MISS')";
				}else{
					sql="SELECT CASE_NO,ORDER_DESC FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
					"' AND RX_KIND='ST' AND ORDER_CODE='"+temp.getValue("ORDER_CODE")+"' AND ROUTE_CODE<>'PS' ";
					
				}
				ope_result = new TParm(TJDODBTool.getInstance().select(sql));//校验临时医嘱是否已经开立
				//如果长期医嘱已经开立临时医嘱允许开立一次
				if (ope_result.getCount()>0) {
					this.messageBox(ope_result.getValue("ORDER_DESC",0)+"预防用药只能开立一次");
					return;
				}
			}
		}
		if (type.equals("ST")) {
			anti_flg = "01";//预防	
		}
		TParm resultSumParm=new TParm();
		for (int i = 0; i < rowCount; i++) {
			day = TypeTool.getInt(table1.getItemData(i, "ANTI_TAKE_DAYS"));//设置抗菌素的默认天数
			TParm temp = parm.getRow(i);
			String tempLinkNo = temp.getValue("LINK_NO");
			if ("N".equals(temp.getValue("FLG")))
				continue;
			if (null!=tempLinkNo && tempLinkNo.length()>0) {
				for(int j = 0;j<rowCount; j++){
					String linkNo = parm.getValue("LINK_NO", j);
					if(("N".equals(parm.getValue("FLG",j))&&(tempLinkNo.equals(linkNo)))){
						this.messageBox("存在连嘱未传回。");
						return;
					}
				}
			}
			resultSumParm.addRowData(parm, i);
			resultSumParm.addData("ARV_FLG", arvFlg);//是否越权标记
			resultSumParm.addData("RX_KIND", rxKind);
			resultSumParm.addData("PHA_ANTI_FLG", "Y");//传回状态
			resultSumParm.addData("PHA_DS_DAY", day);//停用时间
			resultSumParm.addData("ANTI_FLG", anti_flg);//抗菌标识
			resultSumParm.addData("RADIO_FLG", radioFlg);//radioButton 选中标记

			Timestamp startTime=null;
			Timestamp tempDate=null;
			if(anti_flg.equals("01")&&(type.equals("UDD")&&((TRadioButton) this.getComponent("OPRDO"))
					.isSelected())){//术后预防用药管控====pangben 2016-3-25
				//术后预防用药48小时管控  查询术前此医嘱开立时间往后累加48小时，
				//如果没有术前用药则使用手术timeOut时间->手术切皮时间->手术开立时间
				sql="SELECT A.OP_DATE,B.START_TIME FROM OPE_OPBOOK A,OPE_EVENT B WHERE A.OPBOOK_SEQ=B.OPE_BOOK_NO "+
				"AND A.CASE_NO='"+caseNo+"' ORDER BY A.OP_DATE DESC,B.START_TIME DESC ";
				ope_result = new TParm(TJDODBTool.getInstance().select(sql));//校验手术使用药物时间
				if(ope_result.getCount()<=0)//不存在使用医嘱开立时间
				{
					//查询最近一次术后预防的药品开立时间
					sql="SELECT MAX(EFF_DATE) ORDER_DATE FROM ODI_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_KIND='UD' AND ANTIBIOTIC_WAY='01' ";
					ope_result = new TParm(TJDODBTool.getInstance().select(sql));//校验临时医嘱是否已经开立
					if(ope_result.getCount()<=0){//没有查询到医嘱
						if(null!=temp.getData("EFF_DATE")&&temp.getValue("EFF_DATE").length()>0){
							tempDate=temp.getTimestamp("EFF_DATE");
						}else{
							tempDate=SystemTool.getInstance().getDate();//使用当前时间
						}
					}else{
						tempDate=ope_result.getTimestamp("ORDER_DATE", 0);//使用医嘱开立时间
					}
					if(null==tempDate){
						tempDate=SystemTool.getInstance().getDate();//使用当前时间
					}
					startTime=StringTool.rollDate(tempDate,day);
				}else{
					tempDate=ope_result.getTimestamp("START_TIME",0);//手术使用药物时间
					startTime=StringTool.rollDate(tempDate,day);
					Timestamp nowTime = SystemTool.getInstance().getDate();
					//手术抗菌药品使用时间+2天少于当前时间，说明超过48小时不可以使用
					if (!startTime.after(nowTime)) {
						//术后预防医嘱:"+temp.getValue("ORDER_CODE")+temp.getValue("ORDER_DESC")+"使用超过48小时,不可以传回
						this.messageBox("术后预防医嘱使用超过48小时,不可以传回");
						return;
					}
				}
				
			}else{
				if(null!=result&&null!=result.getTimestamp("OP_START_DATE", 0)&&result.getValue("OP_START_DATE", 0).length()>0){
					startTime=StringTool.rollDate(result.getTimestamp("OP_START_DATE", 0),day);
					tempDate=result.getTimestamp("OP_START_DATE", 0);
				}else{
					startTime=StringTool.rollDate(SystemTool.getInstance().getDate(),day);
					tempDate=SystemTool.getInstance().getDate();
				}
				
			}
			//Timestamp startTime=StringTool.rollDate(result.getTimestamp("OP_START_DATE", 0),day);
			Timestamp nowTime = SystemTool.getInstance().getDate();
			if (!startTime.after(nowTime)) {
				if(null!=temp.getData("EFF_DATE")&&temp.getValue("EFF_DATE").length()>0){
					resultSumParm.addData("OP_START_DATE", temp.getTimestamp("EFF_DATE"));
				}else{
					resultSumParm.addData("OP_START_DATE", SystemTool.getInstance().getDate());
				}
				//resultSumParm.addData("OP_START_DATE", SystemTool.getInstance().getDate());
			}else{
				resultSumParm.addData("OP_START_DATE", tempDate);
			}
		
			resultSumParm.addData("DISPENSE_FLG", "N");//备药
		}
		if (resultSumParm.getCount("ORDER_CODE")<=0) {
			this.messageBox("请选择需要传回的数据");
			return;
		}
		resultSumParm.setData("PHA_APPROVE_FLG",null!=resultSumParm.getValue("APPROVE_FLG",0)&&resultSumParm.getValue("APPROVE_FLG",0).equals("Y")?"Y":"N");
		this.setReturnValue(resultSumParm);
//		System.out.println("111fff接受返回值方法"+result);
		//this.parmmeter.runListener("INSERT_TABLE", orderList);
		this.closeWindow();
	}

	/**
	 * 全选
	 */
	public void onSelAll() {
		this.getTTable("TABLE1").acceptText();
		TParm parm = this.getTTable("TABLE1").getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (parm.getBoolean("FLG", i)) {
				parm.setData("FLG", i, false);
			} else {
				parm.setData("FLG", i, true);
			}
		}
		this.getTTable("TABLE1").setParmValue(parm);
	}

	/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	public String getOdiUddStatCode() {
		return odiUddStatCode;
	}

	public void setOdiUddStatCode(String odiUddStatCode) {
		this.odiUddStatCode = odiUddStatCode;
	}

	public String getOdiStatCode() {
		return odiStatCode;
	}

	public void setOdiStatCode(String odiStatCode) {
		this.odiStatCode = odiStatCode;
	}

	public String getOdiDefaFreg() {
		return odiDefaFreg;
	}

	public void setOdiDefaFreg(String odiDefaFreg) {
		this.odiDefaFreg = odiDefaFreg;
	}

	public String getDctTakeDays() {
		return dctTakeDays;
	}

	public void setDctTakeDays(String dctTakeDays) {
		this.dctTakeDays = dctTakeDays;
	}

	public String getDctTakeQty() {
		return dctTakeQty;
	}

	public void setDctTakeQty(String dctTakeQty) {
		this.dctTakeQty = dctTakeQty;
	}

	/**
	 * 得到门诊参数
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOpdSysParmData(String key) {
		return OdiMainTool.getInstance().getOpdSysParmData(key);
	}
	 /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing(){
        TParm parm = new TParm();
        parm.setData("YYLIST","Y");
        this.parmmeter.runListener("INSERT_TABLE_FLG",parm);
        return true;
    }
	/**
	 * 得到住院参数
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOdiSysParmData(String key) {
		return OdiMainTool.getInstance().getOdiSysParmData(key);
	}
}
