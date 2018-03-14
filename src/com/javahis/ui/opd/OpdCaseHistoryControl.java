package com.javahis.ui.opd;

import java.util.HashMap;
import java.util.Map;

import jdo.clp.intoPathStatisticsTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 门诊医生工作站就诊记录
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站就诊记录
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui 20091029
 * @version 1.0
 */
public class OpdCaseHistoryControl extends TControl {
	// 门急住别
	private String admType; // yanj
	// 给入MR_NO
	private String mrNo;
	// 就诊记录TABLE
	public TTable table, diagTable, exaTable, opTable, medTable, chnTable,
			ctrlTable;
	// 中医处方签COMBO
	private TComboBox rxNoCom;
	// 就诊记录
	private TParm parm;
	// 就诊记录查询SQL,20130530 yanj添加ADM_TYPE字段
	//20130617 按时间降序排列 yanj
	private static final String SQL = "SELECT ADM_DATE,REALDR_CODE DR_CODE,CASE_NO,ADM_TYPE FROM REG_PATADM WHERE MR_NO='#' AND SEE_DR_FLG!='N' ORDER BY ADM_DATE Desc";
	// 查询主诉客诉
	private static final String SUBJ_SQL = "SELECT * FROM OPD_SUBJREC WHERE CASE_NO='#'";
	// 查询诊断
	private static final String DIAG_SQL = "SELECT A.*,B.ICD_CHN_DESC,B.ICD_ENG_DESC "
			+ "	FROM OPD_DIAGREC A,SYS_DIAGNOSIS B "
			+ "	WHERE CASE_NO='#' "
			+ "		  AND A.ICD_CODE=B.ICD_CODE "
			+ "	ORDER BY A.MAIN_DIAG_FLG DESC,A.ICD_CODE ";
	// 检验检查查询
	private static final String EXA_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='5' AND SETMAIN_FLG='Y' ORDER BY RX_NO,SEQ_NO";
	// 处置查询
	private static final String OP_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='4' ORDER BY RX_NO,SEQ_NO";
	// 西药查询
	//======yanj2013-03-20,20130530 添加EMG_FIT_FLG，OPD_FIT_FLG
	private static final String MED_SQL = "SELECT 'Y' AS USE,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.ACTIVE_FLG,A.ORDER_CODE,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT," +
			"A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DISPENSE_QTY,A.RELEASE_FLG,A.GIVEBOX_FLG,A.DISPENSE_UNIT,A.EXEC_DEPT_CODE,A.DR_NOTE,A.NS_NOTE,A.URGENT_FLG,A.INSPAY_TYPE,B.ORDER_CODE AS ORDER_CODE_FEE " +
			"FROM OPD_ORDER A , SYS_FEE B " +
			"WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.CASE_NO='#' AND A.RX_TYPE='1' ORDER BY A.RX_NO,A.SEQ_NO";
	// 管制药品查询
	private static final String CTRL_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='2' ORDER BY RX_NO,SEQ_NO";
	// 中医处方签信息查询
	private static final String CHN_RX_SQL = "SELECT DISTINCT RX_NO ID FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='3' ORDER BY RX_NO";
	// 根据处方签查询中医信息
	private static final String CHN_SQL_BY_RX = "SELECT * FROM OPD_ORDER WHERE RX_NO='#'";
	// 回传所有中药处方
	private TParm chnComboParm;
	// 中医数据
	private Map chn = new HashMap();
	// =========pangben 2012-6-28
	private String[] controlName = { "ZS", "KS", "TZ", "DIAG", "JCJG", "JY",
			"ORDER" ,"ALLCHECK"};// 初始化默认选中所有数据
	
	OdiCaseHistory odiCaseHistory;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initParameter();
		initComponent();
		if (!initForm()) {
			// this.messageBox_("herer");
			return;
		}
		odiCaseHistory = new OdiCaseHistory(this);
	}

	/**
	 * 初始化参数
	 */
	public void initParameter() {
		Object obj = this.getParameter();
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj;
		mrNo = parm.getValue("MR_NO");
		admType = parm.getValue("ADM_TYPE");
		if (StringUtil.isNullString(mrNo)) {
			return;
		}
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		table = (TTable) this.getComponent("TABLE");
		diagTable = (TTable) this.getComponent("DIAG_TABLE");
		exaTable = (TTable) this.getComponent("EXA_TABLE");
		opTable = (TTable) this.getComponent("OP_TABLE");
		medTable = (TTable) this.getComponent("MED_TABLE");
		chnTable = (TTable) this.getComponent("CHN_TABLE");
		ctrlTable = (TTable) this.getComponent("CTRL_TABLE");
		TTabbedPane pane = (TTabbedPane) this.getComponent("TTABBEDPANE");
//		System.out.println("%%%###pane is:"+pane);
		pane.setSelectedIndex(2);
		// =============pangben 2012-06-28 初始化默认选中
		for (int i = 0; i < controlName.length; i++) {
			((TCheckBox) this.getComponent(controlName[i])).setSelected(true);
		}
		rxNoCom = (TComboBox) this.getComponent("RX_NO");
	}

	/**
	 * 根据给入MR_NO初始化就诊记录TABLE
	 * 
	 * @return
	 */
	private boolean initForm() {
//		String sql = SQL.replace("#", mrNo);
		String sql = OdiCaseHistory.SQL.replace("#", PatTool.getInstance().getMrRegMrNos(mrNo));
//		 System.out.println("initForm.sql="+sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			return false;
		}
		table.setParmValue(parm);
		return true;
	}

	/**
	 * 就诊记录TABLE单击事件
	 */
	public void onTableClicked() {
		 chnTable.setParmValue(new TParm());
		 medTable.setParmValue(new TParm());
		int row = table.getSelectedRow();
		if (row < 0 || parm == null) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO", row);
//		admType = parm.getValue("ADM_TYPE",row);//yanj 20130530 添加门急住别
		// this.messageBox_(caseNo);
		if (StringUtil.isNullString(caseNo)) {
			// this.messageBox_("no caseNo");
			return;
		}
		
		if("I".equals(parm.getValue("ADM_TYPE",row))){
			odiCaseHistory.onTableClicked(caseNo);
			return;
		}
		odiCaseHistory.checkboxo();
		
		// 给主诉客诉赋值
		String subjSql = SUBJ_SQL.replace("#", caseNo);
		TParm subjRec = new TParm(TJDODBTool.getInstance().select(subjSql));
		// System.out.println("subjRec="+subjRec);
		if (subjRec.getErrCode() < 0) {
			this.messageBox_("查询主诉客诉数据失败");
			return;
		}
		int count = subjRec.getCount();
		if (count > 0) {
			this.setValue("SUBJ_TEXT", subjRec.getValue("SUBJ_TEXT", 0));
			this.setValue("OBJ_TEXT", subjRec.getValue("OBJ_TEXT", 0));
			this.setValue("PHYSEXAM_REC", subjRec.getValue("PHYSEXAM_REC", 0));
			// =========pangben 2012-6-28 添加检查结果 、建议数据
			this.setValue("EXA_RESULT", subjRec.getValue("EXA_RESULT", 0));
			this.setValue("PROPOSAL", subjRec.getValue("PROPOSAL", 0));
		}

		String diagSql = DIAG_SQL.replace("#", caseNo);
		// System.out.println("diuagSql="+diagSql);
		TParm diagRec = new TParm(TJDODBTool.getInstance().select(diagSql));
		if (diagRec.getErrCode() < 0) {
			this.messageBox_("查询诊断信息失败");
			return;
		}
		diagTable.setParmValue(diagRec);
		// 连,30,boolean;组,30,int;医嘱,200;用量,40,double,##0.00;单位,40,UNIT_CODE;频次,50,FREQ_CODE;日份,40,int,#0;总量,40,double;执行科室,80,OP_EXEC_DEPT;医生备注,200;护士备注,200;急作,40,boolean;给付类别,80,INS_PAY
		// LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;DISPENSE_QTY;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INS_PAY
		// ============xueyf modify 20120322 start 暂时屏蔽

		// String exaSql=EXA_SQL.replace("#", caseNo);
		// TParm exa=new TParm(TJDODBTool.getInstance().select(exaSql));
		// if(exa.getErrCode()<0){
		// this.messageBox_("查询医嘱信息失败");
		// return;
		// }
		// exaTable.setParmValue(exa);

		// String opSql=OP_SQL.replace("#", caseNo);
		// TParm op=new TParm(TJDODBTool.getInstance().select(opSql));
		// if(op.getErrCode()<0){
		// this.messageBox_("查询医嘱信息失败");
		// return;
		// }
		// opTable.setParmValue(op);
//========yanj20130319
		String medSql = MED_SQL.replace("#", caseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		if (med.getErrCode() < 0) {
			this.messageBox_("查询医嘱信息失败");
			return;
		}
		medTable.setParmValue(med);

		 String chnRxSql=CHN_RX_SQL.replace("#", caseNo);
		 // System.out.println("chnRxSql="+chnRxSql);
		 chnComboParm=new TParm(TJDODBTool.getInstance().select(chnRxSql));
		 if(chnComboParm==null||chnComboParm.getErrCode()<0){
		 this.messageBox_("查询医嘱信息失败");
		
		 return;
		 }
		 count=chnComboParm.getCount();
		 for(int i=0;i<count;i++){
		 chnComboParm.setData("NAME", i, "第"+(i+1)+"张处方");
		 }
		 rxNoCom.setParmValue(chnComboParm);
		 rxNoCom.setSelectedID(chnComboParm.getValue("ID",0));
		 onChangeRx();
		//		
		// String ctrlSql=CTRL_SQL.replace("#", caseNo);
		// TParm ctrl=new TParm(TJDODBTool.getInstance().select(ctrlSql));
		// if(ctrl.getErrCode()<0){
		// this.messageBox_("查询医嘱信息失败");
		// return;
		// }
		// ctrlTable.setParmValue(ctrl);
		// ============xueyf modify 20120322 stop 暂时屏蔽
		 
		//选择页签
		 TTabbedPane pane = (TTabbedPane) this.getComponent("TTABBEDPANE");
		 if(med.getCount()<=0 || med == null){
			 if(chnComboParm.getCount()<1 || chnComboParm == null){					
					pane.setSelectedIndex(2);
			 }else{
				 pane.setSelectedIndex(3);//草药页签
			 }
		 }else{
			 pane.setSelectedIndex(2);//西药页签
		 }
	}

	/**
	 * 处方签改变事件
	 */
	public void onChangeRx() {
		String rxNo = this.rxNoCom.getSelectedID();
		if (StringUtil.isNullString(rxNo)) {
			return;
		}
		TParm parm = new TParm();
		// if(chn.get(rxNo)==null){
		//			
		// chn.put(rxNo, parm);
		// }else{
		// parm=(TParm)chn.get(rxNo);
		// }

		parm = new TParm(TJDODBTool.getInstance().select(
				this.CHN_SQL_BY_RX.replace("#", rxNo)));
		TParm chnParm = this.getChnParm(parm);
		// System.out.println("chnParm="+chnParm);
		chnTable.setParmValue(chnParm);
		this.setValue("TAKE_DAYS", parm.getValue("TAKE_DAYS", 0));
		this.setValue("DCT_TAKE_QTY", parm.getValue("DCT_TAKE_QTY", 0));
		this.setValue("FREQ_CODE", parm.getValue("FREQ_CODE", 0));
		this.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE", 0));
		this.setValue("CHN_ROUTE_CODE", parm.getValue("ROUTE_CODE", 0));
	}

	/**
	 * 取得
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getChnParm(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			return result;
		}
		int count = parm.getCount();
		if (count < 1) {
			result.setErrCode(-1);
			return result;
		}
		int countAll = (count / 4 + 1) * 4;
		for (int i = 0; i < countAll; i++) {
			int row = i / 4;
			int column = i % 4 + 1;
			if (i < count) {
				result.setData("ORDER_DESC" + column, row, parm.getValue(
						"ORDER_DESC", i));
				result.setData("MEDI_QTY" + column, row, parm.getValue(
						"MEDI_QTY", i));
				result.setData("DCTEXCEP_CODE" + column, row, parm.getValue(
						"DCTEXCEP_CODE", i));
			} else {
				result.setData("ORDER_DESC" + column, row, "");
				result.setData("MEDI_QTY" + column, row, "");
				result.setData("DCTEXCEP_CODE" + column, row, "");
			}

		}
		result.setCount(countAll);

		return result;
	}

	/**
	 * 回传
	 */
	public void onFetch() {
		if (table == null) {
			return;
		}
		if (table.getSelectedRow() < 0) {
			return;
		}
		TParm exa = exaTable.getParmValue();
		TParm op = opTable.getParmValue();
		medTable.acceptText();
		TParm med = medTable.getParmValue();
		TParm ctrl = ctrlTable.getParmValue();
		TParm result = new TParm();
		if (TypeTool.getBoolean(this.getValue("ZS"))) {
			result.setData("SUB", this.getValueString("SUBJ_TEXT"));
		}
		if (TypeTool.getBoolean(this.getValue("KS"))) {
			result.setData("OBJ", this.getValueString("OBJ_TEXT"));
		}
		if (TypeTool.getBoolean(this.getValue("TZ"))) {
			result.setData("PHY", this.getValueString("PHYSEXAM_REC"));
		}
		// ==========pangben 2012-6-28 添加检查结果 \建议回传值
		if (TypeTool.getBoolean(this.getValue("JCJG"))) {
			result.setData("EXA_R", this.getValueString("EXA_RESULT"));
		}
		if (TypeTool.getBoolean(this.getValue("JY"))) {
			result.setData("PRO", this.getValueString("PROPOSAL"));
		}
		TParm medParm=new TParm();//=======pangben 2013-1-5 没有停用的医嘱可以传回
		int medCount=0;//累计个数
		StringBuffer message = new StringBuffer();
		StringBuffer bufRed=new StringBuffer();
		if (TypeTool.getBoolean(this.getValue("ORDER"))) {
			//=========yanj2013-03-20
			for (int i = 0; i < med.getCount(); i++) {
				String flg = med.getValue("USE",i);
				if (flg.equals("N")) {
					this.setValue("ALLCHECK", false);
					
				}
			}
			// ==========yanj 2013-03-22 添加已停用药品校验
			for (int i = 0; i < med.getCount("ORDER_CODE"); i++) {
				//判断如果传回医嘱在SYS_FEE表中不存在 则不能传回并且提示
				if(null==med.getValue("ORDER_CODE_FEE", i)||"".equals(med.getValue("ORDER_CODE_FEE", i))){//modify  caoyong 20131105
					 bufRed.append(med.getValue("ORDER_DESC",i)).append(",");
				}else{
					//========yanjing 2013-3-20选择要传回的药嘱
				if(med.getValue("USE",i).equals("Y")&&med.getValue("ACTIVE_FLG",i).equals("N")){//停用注记
							message.append(med.getValue("ORDER_DESC", i)).append(",");//显示医嘱名称
					}else if (med.getValue("USE",i).equals("Y")){
						// add by yanj 2013/05/30 门急诊适用校验
						// 门诊
						if ("O".equalsIgnoreCase(admType)) {
							// 判断是否住院适用医嘱
							if (!("Y".equals(med.getValue("OPD_FIT_FLG",i)))) {
								// 不是门诊适用医嘱！
								this.messageBox(med.getValue("ORDER_DESC", i)+",不是门诊适用医嘱。");
								return;
							}
						}
						// 急诊
						if ("E".equalsIgnoreCase(admType)) {
							if (!("Y".equals(med.getValue("EMG_FIT_FLG",i)))) {
								// 不是门诊适用医嘱！
								this.messageBox(med.getValue("ORDER_DESC", i)+",不是急诊适用医嘱。");
								return;
							}
						}
						// $$===========add by yanj 2013/05/30门急诊适用校验
						
						//add by huangtt 20150218 start 校验证照权限   
						if (!OdoUtil.isHavingLiciense(med.getValue("ORDER_CODE",i), Operator
								.getID())) {
							this.messageBox(Operator.getID()+" 医师没有药品    "+ med.getValue("ORDER_DESC", i)+" 的证照权限"); // 没有证照权限
							return;
						}
						//add by huangtt 20150218 end
						
						medParm.setRowData(medCount, med,i);
						medCount++;
						}
				    }
					}
			medParm.setCount(medCount);
			if (message.toString().length() > 0) {//累计停用医嘱名称
				if(this.messageBox("提示","药品医嘱名称:"+message.toString().substring(0,message.toString().lastIndexOf(","))+"已经停用,是否传回其它医嘱",0)!=0){
					return;
				}
				//return;
			}
			//add  caoyong 20131105
			if(bufRed.toString().length()>0){
			       String bufMess=bufRed.toString().substring(0,bufRed.toString().lastIndexOf(","))+"医嘱不存在，是否传回其它医嘱？";
			       if(this.messageBox("提示","药品医嘱名称:"+bufMess,0)!=0){
						return;
					}
			}
		
			      
			result.setData("EXA", exa);
			result.setData("OP", op);
			result.setData("MED", medParm);
			result.setData("CTRL", ctrl);
			if (chnComboParm != null) {
				int count = chnComboParm.getCount();
				if (count > -1) {
					for (int i = 0; i < count; i++) {
						String rxNo = chnComboParm.getValue("ID", i);
						TParm chnParm = new TParm(TJDODBTool.getInstance()
								.select(this.CHN_SQL_BY_RX.replace("#", rxNo)));
						chnParm.setData("TAKE_DAYS",i,this.getValueString("TAKE_DAYS"));
						chnParm.setData("DCT_TAKE_QTY",i,this.getValueString("DCT_TAKE_QTY"));
						chnParm.setData("CHN_FREQ_CODE",i,this.getValueString("FREQ_CODE"));
						chnParm.setData("CHN_ROUTE_CODE",i, this.getValueString("CHN_ROUTE_CODE"));
						chnParm.setData("DCTAGENT_CODE",i, this.getValueString("DCTAGENT_CODE"));
						chn.put(rxNo, chnParm);
					}
				}
			}
			result.setData("CHN", chn);
		}
		if (TypeTool.getBoolean(this.getValue("DIAG"))) {
			result.setData("DIAG", diagTable.getParmValue());
		}
		System.out.println("result is :"+result);
		this.setReturnValue(result);
		this.closeWindow();
	}
	//=======yanj2013-03-20
	
	
/**
 * 全选事件
 */
	public void onSelAll() {
		String select = getValueString("ALLCHECK");
        TParm parm = medTable.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("USE", i, select);
        }
        medTable.setParmValue(parm);
	}
	
}
