package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.tiis.util.TiString;

/**
 * <p>
 * Title: 医疗票据网络管理
 * </p>
 * 
 * <p>
 * Description: 医疗票据网络管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangp
 * @version JavaHis 1.0
 */
public class INSINVmanageControl extends TControl {

	private TRadioButton applyRadio;// 申请
	private TRadioButton approveRadio;// 审批
	private TTable table1;
	private TTable table2;
	private String apply_flg = "N"; // 申请
	private String pvtHOSP_AREA = "";// region_code
	private String NHI_HOSP_CODE = "";// 医院编码
	private TTabbedPane tp;
	private String year = "";// 年度
	private String apply_no = "";// 购领批号
	private String apply_tran_no = "";// 购领交易号
	private String applyflg = "";// 审批状态
	private String o_year_count = "";// 门诊年核定数量
	private String i_year_count = "";// 住院年核定数量
	private String o_all_count = "";// 门诊累计购领数量
	private String i_all_count = "";// 住院累计购领数量
	private String o_count = "";// 门诊申请增购数量
	private String i_count = "";// 住院申请增购数量
	private String remark = "";// 原因说明
	private String permit_count = "";// 批准数量
	private String part_user = "";// 分中心审核人员
	private TComboBox TypeCombo;

	public void onInit() {
		super.onInit();
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		setValue("NHI_HOSPCODE", regionParm.getValue("NHI_NO", 0));
		setValue("NHI_HOSPCODE_C", regionParm.getValue("NHI_NO", 0));
		NHI_HOSP_CODE = regionParm.getValue("NHI_NO", 0);
		pvtHOSP_AREA = Operator.getRegion();
		applyRadio = (TRadioButton) getComponent("APPLY");
		approveRadio = (TRadioButton) getComponent("APPROVE");
		table1 = (TTable) getComponent("TABLE1");
		table2 = (TTable) getComponent("TABLE2");
		tp = (TTabbedPane) getComponent("tp");
		Timestamp today = SystemTool.getInstance().getDate();
		year = today.toString().substring(0, 4);
		applyRadio.setSelected(true);
		setValue("YEAR", year);
		setValue("YEAR_C", year);
		TypeCombo = (TComboBox)this.getComponent("INV_TYPE_COMBO");
		this.getacountNo();
		// 购领批号,100;医院编码,100;年度,50;门诊申请增购数量,120;住院申请增购数量,120;原因说明,100;购领交易号,100;审批状态,100;门诊年核定数量,120;住院年核定数量,120;门诊累计购领数量,120;住院累计购领数量,120;门诊批准数量,100;住院批准数量,100;分中心审核意见,120;市中心审批意见,120;操作人员,80;操作日期,100;分中心审核人员,120
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (applyRadio.isSelected()) {
			apply_flg = "N"; // 申请
		}
		if (approveRadio.isSelected()) {
			apply_flg = "Y"; // 审批
		}
		this.queryapplydata(apply_flg, "");
	}

	/**
	 * 查询申报
	 * 
	 * @param flg
	 * @param no
	 */
	public void queryapplydata(String flg, String no) {
		// 查询数据
		TParm result = new TParm();
		table1.removeRowAll();
		String sql = " SELECT A.APPLY_NO, A.NHI_HOSPCODE, A.YEAR_CODE, A.O_COUNT, A.I_COUNT,"
				+ " A.REMARK, A.APPLY_TRAN_NO, CASE WHEN A.APPLY_FLG ='N'  THEN '已申请' ELSE '已审批'  END APPLY_FLG,"
				+ " A.O_YEAR_COUNT, A.I_YEAR_COUNT, A.O_ALL_COUNT, A.I_ALL_COUNT, A.O_PERMIT_COUNT,"
				+ " A.I_PERMIT_COUNT, A.PART_COMMENT, A.CITY_COMMENT, B.USER_NAME,A.OPT_DATE,A.PART_USER"
				+ " FROM INS_INVAPPLY A,SYS_OPERATOR B"
				+ " WHERE A.REGION_CODE = '"
				+ pvtHOSP_AREA
				+ "'"
				+ " AND A.OPT_USER = B.USER_ID"
				+ " AND A.APPLY_FLG='"
				+ flg
				+ "'";
		if (!no.equals("")) {
			sql += " AND A.APPLY_NO = '" + no + "'";
		}
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("result====1" + result);
		if (result.getErrCode() < 0 || result.getCount() < 0) {
			messageBox("查无资料");
			table1.removeRowAll();
			return;
		}
		// APPLY_NO;NHI_HOSPCODE;YEAR_CODE;O_COUNT;I_COUNT;REMARK;APPLY_TRAN_NO;APPLY_FLG;O_YEAR_COUNT;I_YEAR_COUNT;O_ALL_COUNT;I_ALL_COUNT;O_PERMIT_COUNT;I_PERMIT_COUNT;PART_COMMENT;CITY_COMMENT;USER_NAME;OPT_DATE;PART_USER
		// table1.setParmValue(result);
		this.callFunction("UI|TABLE1|setParmValue", result);
	}

	// parm.addReturnData("HOSP_AREA", pvtHOSP_AREA);
	// parm.addReturnData("APPLY_NO", applyno);
	// parm.addReturnData("NHI_HOSPCODE", NHI_HOSPCODE.getText());
	// parm.addReturnData("YEAR_CODE", YEAR.getText());
	// parm.addReturnData("O_COUNT", O_COUNT.getText());
	// parm.addReturnData("I_COUNT", I_COUNT.getText());
	// parm.addReturnData("REMARK", REMARK.getText());
	// parm.setCommitData("PIPELINE", "DataDown_yb");
	// parm.setCommitData("PLOT_TYPE", "O");
	// parm.addReturnData("OPT_USER", pvtUserId);
	// parm.addReturnData("OPT_DATE", this.getAppDate());
	// parm.addReturnData("OPT_ITEM", pvtUserIP);
	// result = manager.InsInvNoManager.getInstance().applyInvNo(parm);
	// // System.out.println("result申请======" + result);
	// if (result.getErrCode() != 0)
	// return;
	// else{
	// JOptionPane.showMessageDialog(this, "医保票据购领申请成功!");
	// this.queryapplydata("N", applyno);
	// //获最大序号
	// this.getacountNo();
	// }
	/**
	 * 申报
	 */
	public void onApply() {
		int index = tp.getSelectedIndex();
		if (index != 0)
			return;
		if (this.checkData())
			return;
		TParm parm = new TParm();
		TParm result = new TParm();
		String applyno = NHI_HOSP_CODE + year + getValue("ACCOUNT_NO");

		parm.setData("PIPELINE", "DataDown_yb");
		parm.setData("PLOT_TYPE", "O");
		parm.addData("APPLY_NO", applyno);
		parm.addData("NHI_HOSPCODE", NHI_HOSP_CODE);
		parm.addData("YEAR_CODE", year);
		parm.addData("O_COUNT", getValueInt("O_COUNT"));
		parm.addData("I_COUNT", getValueInt("I_COUNT"));
		parm.addData("REMARK", getValue("REMARK"));
		parm.addData("PARM_COUNT", 6); // 参数数量
		result = InsManager.getInstance().safe(parm);
		if (result == null || result.getErrCode() < 0) {
			messageBox("医保票据购领申请失败!");
			return;
		}
		messageBox("医保票据购领申请成功!");
		String APPLY_TRAN_NO = result.getValue("APPLY_TRAN_NO");// 购领交易号
		String sql = " INSERT INTO INS_INVAPPLY "
				+ " (REGION_CODE,APPLY_NO, NHI_HOSPCODE,YEAR_CODE, O_COUNT, "
				+ " I_COUNT,REMARK, APPLY_TRAN_NO, APPLY_FLG,OPT_USER,OPT_DATE,OPT_TERM) "
				+ " VALUES  " + " ('"
				+ pvtHOSP_AREA
				+ "', '"
				+ applyno
				+ "', '"
				+ NHI_HOSP_CODE
				+ "', '"
				+ year
				+ "', "
				+ getValueInt("O_COUNT")
				+ ", "
				+ " "
				+ getValueInt("I_COUNT")
				+ ", '"
				+ getValue("REMARK")
				+ "', "
				+ " '"
				+ APPLY_TRAN_NO
				+ "', 'N', '"
				+ Operator.getID()
				+ "',SYSDATE,'" + Operator.getIP() + "')";
		TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
		if (result1.getErrCode() < 0) {
			messageBox("插入失败");
		}
		queryapplydata("N", applyno);
		this.getacountNo();
	}

	/**
	 * 检核数据
	 */
	public boolean checkData() {
		int index = tp.getSelectedIndex();
		if (index == 0) {
			// if (this.getValue("O_COUNT").equals("")) {
			// messageBox("请选择门诊申请增购数量!");
			// return true;
			// }
			// if (this.getValue("I_COUNT").equals("")) {
			// messageBox("请选择住院申请增购数量!");
			// return true;
			// }
			if (this.getValue("REMARK").equals("")) {
				messageBox("请选择原因说明!");
				return true;
			}
		}
		 if (index == 1) {
		 if (this.getValue("REMARK_C").equals("")) {
			 messageBox("请填写作废原因!");
		 return true;
		 }

		 if (TypeCombo.getSelectedText().equals("")) {
			 messageBox( "请选择票据类型!");
		 return true;
		 }
		 }
		return false;

	}

	/**
	 *获最大序号
	 */
	public void getacountNo() {
		// 查询数据
		TParm result = new TParm();
		String sql = " SELECT MAX(A.APPLY_NO) APPLY_NO FROM INS_INVAPPLY A"
				+ " WHERE A.REGION_CODE = '" + pvtHOSP_AREA + "'"
				+ " AND  A.APPLY_NO LIKE  '" + NHI_HOSP_CODE + year + "%'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getData("APPLY_NO", 0).equals("")) {
			this.setValue("ACCOUNT_NO", "0001");
		} else {
			String acount_no = result.getData("APPLY_NO", 0).toString();
			this.setValue("ACCOUNT_NO", this.getNo(acount_no));
		}
	}

	/**
	 * 显示序号
	 * 
	 * @return String
	 */
	public String getNo(String account_no) {
		int accountno = Integer.parseInt(account_no.substring(10, 14)) + 1;
		String ACCOUNT_NO = String.valueOf(accountno);
		ACCOUNT_NO = TiString.fillLeft(ACCOUNT_NO, 4, '0'); // 补零
		return ACCOUNT_NO;
	}

	/**
	 * 审批下载动作
	 */
	public void onDownLoad() {
		int index = tp.getSelectedIndex();
		if (index != 0)
			return;
		int row = this.table1.getSelectedRow();
		if (row < 0)
			return;
		TParm parm = new TParm();
		TParm result = new TParm();

		parm.setData("PIPELINE", "DataDown_yb");
		parm.setData("PLOT_TYPE", "P");
		parm.addData("APPLY_NO", apply_no);
		parm.addData("APPLY_TRAN_NO", apply_tran_no);
		parm.addData("NHI_HOSPCODE", NHI_HOSP_CODE);
		parm.addData("PARM_COUNT", 3); // 参数数量
		result = InsManager.getInstance().safe(parm);
		if (result == null || result.getErrCode() < 0) {
			messageBox("购领信息审批下载失败!");
			return;
		}
		String O_YEAR_COUNT = result.getValue("O_YEAR_COUNT"); // 门诊年核定数量
		String I_YEAR_COUNT = result.getValue("I_YEAR_COUNT"); // 住院年核定数量
		String O_ALL_COUNT = result.getValue("O_ALL_COUNT"); // 门诊累计购领数量
		String I_ALL_COUNT = result.getValue("I_ALL_COUNT"); // 住院累计购领数量
		String O_PERMIT_COUNT = result.getValue("O_PERMIT_COUNT"); // 门诊批准数量
		String I_PERMIT_COUNT = result.getValue("I_PERMIT_COUNT"); // 住院批准数量
		String PART_COMMENT = result.getValue("PART_COMMENT"); // 分中心审核意见
		String CITY_COMMENT = result.getValue("CITY_COMMENT"); // 市中心批准意见
		String PART_USER = result.getValue("PART_USER"); // 分中心审核人员(新)
		String sql = " UPDATE INS_INVAPPLY SET " + " O_YEAR_COUNT = "
				+ O_YEAR_COUNT + "," + " I_YEAR_COUNT = " + I_YEAR_COUNT + ","
				+ " O_ALL_COUNT =  " + O_ALL_COUNT + "," + " I_ALL_COUNT = "
				+ I_ALL_COUNT + "," + " O_PERMIT_COUNT = " + O_PERMIT_COUNT
				+ "," + " I_PERMIT_COUNT = " + I_PERMIT_COUNT + ","
				+ " PART_COMMENT = '" + PART_COMMENT + "',"
				+ " CITY_COMMENT = '" + CITY_COMMENT + "',"
				+ " APPLY_FLG = 'Y'," + " OPT_USER = '" + Operator.getID()
				+ "'," + " OPT_DATE = SYSDATE," + " OPT_TERM = '"
				+ Operator.getIP() + "'," + " PART_USER = '" + PART_USER + "'"
				+ " WHERE APPLY_NO ='" + apply_no + "' "
				+ " AND APPLY_TRAN_NO ='" + apply_tran_no + "'"
				+ " AND NHI_HOSPCODE ='" + NHI_HOSP_CODE + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result == null || result.getErrCode() < 0) {
			messageBox("购领信息审批下载失败!");
			return;
		} else {
			messageBox("购领信息审批下载成功!");
			this.queryapplydata("Y", apply_no);
			// 获最大序号
			this.getacountNo();
		}
	}

	/**
	 * table1监听器
	 */
	public void onTable1Clicked() {
		int row = this.table1.getSelectedRow();
		if (row < 0)
			return;
		TParm table1parm = table1.getParmValue();
		table1parm = table1parm.getRow(row);
		apply_no = table1parm.getValue("APPLY_NO");
		apply_tran_no = table1parm.getValue("APPLY_TRAN_NO");
		applyflg = table1parm.getValue("APPLY_FLG");
		o_year_count = table1parm.getValue("O_YEAR_COUNT");
		i_year_count = table1parm.getValue("I_YEAR_COUNT");
		o_all_count = table1parm.getValue("O_ALL_COUNT");
		i_all_count = table1parm.getValue("I_ALL_COUNT");
		o_count = table1parm.getValue("O_COUNT");
		i_count = table1parm.getValue("I_COUNT");
		remark = table1parm.getValue("REMARK");
		part_user = table1parm.getValue("PART_USER");
		permit_count = ""
				+ (table1parm.getInt("O_PERMIT_COUNT") + table1parm
						.getInt("I_PERMIT_COUNT"));
	}

	/**
	 * 清空
	 */
	public void onclear() {
		table1.removeRowAll();
		table2.removeRowAll();
		apply_flg = "N"; // 申请
		apply_no = "";// 购领批号
		apply_tran_no = "";// 购领交易号
		applyflg = "";// 审批状态
		o_year_count = "";// 门诊年核定数量
		i_year_count = "";// 住院年核定数量
		o_all_count = "";// 门诊累计购领数量
		i_all_count = "";// 住院累计购领数量
		o_count = "";// 门诊申请增购数量
		i_count = "";// 住院申请增购数量
		remark = "";// 原因说明
		permit_count = "";// 批准数量
		part_user = "";// 分中心审核人员
		applyRadio.setSelected(true);
		clearValue("O_COUNT;I_COUNT;REMARK");
		clearValue("COUNT_C;REMARK_C;START_INVNO;END_INVNO");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		int index = tp.getSelectedIndex();
		if (index != 0)
			return;
		int row = this.table1.getSelectedRow();
		if (row < 0){
			messageBox("请选择要打印的数据");
			return;
		}
		if (applyflg.equals("已申请")){
			messageBox("请选择已审批数据");
			return;
		}
		TParm print = new TParm();
		// print.setData("APPLY_NO", "TEXT", apply_no);
		// print.setData("APPLY_TRAN_NO", "TEXT", apply_tran_no);
		// print.setData("APPLY_FLG", "TEXT", applyflg);
		// print.setData("O_YEAR_COUNT", "TEXT", o_year_count);
		// print.setData("I_YEAR_COUNT", "TEXT", i_year_count);
		// print.setData("O_ALL_COUNT", "TEXT", o_all_count);
		// print.setData("I_ALL_COUNT", "TEXT", i_all_count);
		// print.setData("O_COUNT", "TEXT", o_count);
		// print.setData("I_COUNT", "TEXT", i_count);
		// print.setData("REMARK", "TEXT", remark);
		// print.setData("PART_USER", "TEXT", part_user);
		// print.setData("PERMIT_COUNT", "TEXT", permit_count);
		print.setData("APPLY_NO", apply_no);
		print.setData("APPLY_TRAN_NO", apply_tran_no);
		print.setData("APPLY_FLG", applyflg);
		print.setData("O_YEAR_COUNT", o_year_count);
		print.setData("I_YEAR_COUNT", i_year_count);
		print.setData("O_ALL_COUNT", o_all_count);
		print.setData("I_ALL_COUNT", i_all_count);
		print.setData("O_COUNT", o_count);
		print.setData("I_COUNT", i_count);
		print.setData("REMARK", "TEXT", remark);
		print.setData("PART_USER", part_user);
		print.setData("PERMIT_COUNT", permit_count);
		print.setData("YEAR", year);
		print.setData("NHI_HOSP_CODE", NHI_HOSP_CODE);
		print.setData("HOSP", Operator.getHospitalCHNFullName());
		String today = SystemTool.getInstance().getDate().toString();
		today = today.substring(0, 4)+"年"+today.substring(5, 7)+"月"+today.substring(8, 10)+"日";
		print.setData("DATE", today);
		print.setData("OPT_USER", Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\ins\\INSINVManager.jhw",
				print);
	}
	/**
	 * 票据作废
	 */
	public void onInvCancel() {
		if (this.checkData())
			return;
//		System.out.println("Type=========" + TypeCombo.getSelectedID());
		TParm parm = new TParm();
		TParm result = new TParm();	
		parm.setData("PIPELINE", "DataDown_yb");
		parm.setData("PLOT_TYPE", "Q");
		parm.addData("NHI_HOSPCODE", NHI_HOSP_CODE);
		parm.addData("YEAR_CODE", year);
		parm.addData("CANCLE_REMARK", getValue("REMARK_C"));
	    parm.addData("CANCLE_COUNT", getValueInt("COUNT_C"));
	    parm.addData("START_INVNO", getValue("START_INVNO"));
	    parm.addData("END_INVNO",  getValue("END_INVNO"));
	    parm.addData("INV_TYPE", TypeCombo.getSelectedID());
//	    System.out.println("parm====" + parm);
		result = InsManager.getInstance().safe(parm);
		if (result == null || result.getErrCode() < 0) {
			messageBox("医保票据作废失败!");
			return;
		}
		String sql = " INSERT INTO INS_INVCANCLE "
			+ " (REGION_CODE,NHI_HOSPCODE,YEAR_CODE,CANCLE_REMARK,"
			+ " CANCLE_COUNT,START_INVNO,END_INVNO,INV_TYPE,OPT_USER,OPT_DATE,OPT_TERM) "
			+ " VALUES  " + " ('"+ pvtHOSP_AREA+ "','"+ NHI_HOSP_CODE+ "','"+ year+ "','"
			+ getValue("REMARK_C")+ "', "+ getValueInt("COUNT_C")+ ", '"+ getValue("START_INVNO")+ "','"
			+ getValue("END_INVNO")+ "','"+ TypeCombo.getSelectedID()+ "','"+ Operator.getID()
			+ "',SYSDATE,'" + Operator.getIP() + "')";
	TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
	if (result1.getErrCode() < 0) {
		messageBox("插入失败");
		return;
	}
	messageBox("医保票据作废成功!");
	queryCanceldata();	
	}
	/**
	 * 票据作废查询
	 */
	public void onQueryCancel() {
		queryCanceldata();	
	}
	/**
	 * 票据作废查询
	 */
	public void queryCanceldata() {
		// 查询数据
		TParm result = new TParm();
		table2.removeRowAll();	
		String sql =  " SELECT  A.NHI_HOSPCODE, A.YEAR_CODE, A.CANCLE_REMARK, A.CANCLE_COUNT," +
	     " A.START_INVNO, A.END_INVNO, CASE WHEN A.INV_TYPE ='1'  THEN '门诊' ELSE '住院' END AS INV_TYPE,"+
	     " B.USER_NAME,TO_CHAR(A.OPT_DATE,'YYYY/MM/DD')AS OPT_DATE"+
	     " FROM INS_INVCANCLE A,SYS_OPERATOR B"+
	     " WHERE A.REGION_CODE = '"+pvtHOSP_AREA+"'"+ 
	     " AND A.OPT_USER = B.USER_ID";
	result = new TParm(TJDODBTool.getInstance().select(sql));
//	 System.out.println("result====1" + result);
	if (result.getErrCode() < 0 || result.getCount() < 0) {
		messageBox("查无资料");
		table2.removeRowAll();
		return;
	}
	this.callFunction("UI|TABLE2|setParmValue", result);	
	}	
	
}
