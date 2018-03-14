package com.javahis.ui.ins;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;


import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
/**
 * <p>
 * Title:城乡垫付分割
 * </p>
 * 
 * <p>
 * Description:城乡垫付分割
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author zhangp 2012-2-13
 * @version 1.0
 */
public class INSBalance_DFControl extends TControl{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	TTable tableInfo;// 病患基本信息列表
	TTable oldTable;// 费用分割前数据
	TTable newTable;// 费用分割后数据
	TParm regionParm;// 医保区域代码
	int radio = 1;//1，按住院号下载	2，全部下载
	TTabbedPane tabbedPane;// 页签
	// 费用分割前表格数据
	private String[] pageFour = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	// 费用分割后表格数据
	private String[] pageFive = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"DOSE_CODE", "STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE", "NHI_FEE_DESC",
			"OWN_PRICE", "CHARGE_DATE" };
	
	/**
     * 初始化方法
     */
    public void onInit() {
		tableInfo = (TTable) this.getComponent("TABLEINFO");// 病患基本信息列表
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE");// 页签
		oldTable = (TTable) this.getComponent("OLD_TABLE");// 费用分割前数据
		newTable = (TTable) this.getComponent("NEW_TABLE");// 费用分割后数据
		setValue("START_DATE", SystemTool.getInstance().getDate());
		setValue("END_DATE", SystemTool.getInstance().getDate());
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		setValue("NHI_ORD_CLASS_CODE", "01");
    }
	/**
	 * 校验为空方法
	 * 
	 * @param name
	 * @param message
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}
	/**
	 * 查询
	 */
	public void onQuery() {
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "入院开始时间不可以为空");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "入院结束时间不可以为空");
			return;
		}

		if (((Timestamp) this.getValue("START_DATE")).after(((Timestamp) this
				.getValue("END_DATE")))) {
			this.messageBox("开始时间不可以大于结束时间");
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", sdf.format(this.getValue("START_DATE")));// 入院时间
		parm.setData("END_DATE", sdf.format(this.getValue("END_DATE")));// 入院结束时间
		TParm result = INSADMConfirmTool.getInstance().INS_DF_Seq(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * 下载垫付住院顺序号
	 */
	public void onDownload(){
		if (getValue("START_DATE").equals("") ||
				getValue("END_DATE").equals("")) {
		      messageBox("开始日期和结束日期不能为空");
		      return;
		    }
		    if (radio == 1&&getValueString("CASE_NO").equals("")) {
		      messageBox("按住院号下载垫付顺序号时，登记住院号不能为空");
		      return;
		    }
		    String case_no = " ";
		    if (radio == 1) {
		      case_no = getValueString("CASE_NO");
		    }
		    String startDate = getValueString("START_DATE").substring(0, 19);;
	    	String endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10)+"000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
		    TParm parm = new TParm();
	    	TParm regionParm = SYSRegionTool.getInstance().selectdata(Operator.getRegion());
	    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//获取HOSP_NHI_NO
		    parm.setData("PIPELINE", "DataDown_czyd");
		    parm.setData("PLOT_TYPE", "M");
		    parm.addData("HOSP_NHI_NO", hospital); //医院编码
		    parm.addData("CASE_NO", case_no);
		    parm.addData("BEGIN_DATE", startDate);
		    parm.addData("END_DATE", endDate);
		    parm.addData("CTZ_TYPE", "21"); //住院
		    parm.addData("ADVANCE_TYPE", "01"); //正常垫付
		    parm.addData("PARM_COUNT", 6); //参数数量
		    TParm resultParm = InsManager.getInstance().safe(parm,"");
		    if (resultParm == null) {
		      messageBox("下载失败");
		      return;
		    }
		      this.insert_DF_confrim_no(resultParm);
		    messageBox("下载成功");
	}
	/**
	 * radio监听器
	 * @param i
	 */
	public void onSelect(int i){
		radio = i;
		clearValue("CASE_NO");
		TTextField case_no = (TTextField) getComponent("CASE_NO");
		if(i == 1){
			case_no.setEnabled(true);
		}
		if(i == 2){
			case_no.setEnabled(false);
		}
	}
	/**
	 * 插入INS_ADVANCE_PAYMENT
	 * @param parm
	 */
	public void insert_DF_confrim_no(TParm parm){
		TParm p = new TParm();
		for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++){
			p.setData("CONFIRM_NO", parm.getData("CONFIRM_NO", i));//垫付住院顺序号
			p.setData("SID", parm.getData("SID", i));//身份证号码
			p.setData("NAME", parm.getData("NAME", i));//姓名
			p.setData("SEX_CODE", parm.getData("SEX_CODE", i));//性别
			p.setData("WORK_DEPARTMENT", parm.getData("WORK_DEPARTMENT", i));//单位名称
			p.setData("CTZ_CODE", parm.getData("CTZ_CODE", i));//人员类别
			p.setData("IN_HOSP_DATE", parm.getData("IN_HOSP_DATE", i));//入院时间
			p.setData("OUT_HOSP_DATE", parm.getData("OUT_HOSP_DATE", i));//出院时间
			p.setData("CASE_NO", parm.getData("CASE_NO", i));//住院号
			p.setData("REG_TOT_AMT", parm.getData("REG_TOT_AMT", i));//费用总额
			p.setData("ADVANCE_TYPE", parm.getData("ADVANCE_TYPE", i));//垫付类型
			p.setData("CONTACT_ADDR", parm.getData("CONTACT_ADDR", i));//联系单位
			p.setData("CONTACT_TYPE", parm.getData("CONTACT_TYPE", i));//联系方式
			p.setData("CONTACTS", parm.getData("CONTACTS", i));//联系人
			p.setData("OPT_USER", Operator.getID());
			p.setData("OPT_DATE", SystemTool.getInstance().getDate());
			p.setData("OPT_TERM", Operator.getIP());
			TParm result = INSADMConfirmTool.getInstance().insertAdvancePayment(p);
			if(result.getErrCode()<0){
				messageBox(result.getErrText());
				return;
			}
		}
		onQuery();
	}
	/**
	 * 转申报
	 */
	public void onApply() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		parm.setData("TYPE", "H");// M :转病患信息操作 ,H :转申报操作 A : 自动
		parm.setData("REGION_CODE", Operator.getRegion());// 医院代码
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("END_DATE", sdf.format(SystemTool.getInstance().getDate()));// 系统时间
		TParm result = TIOM_AppServer.executeAction(
											"action.ins.INSBalanceAction", "changeReport", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		String Msg = "转档完毕\n" + "成功笔数:" + result.getValue("SUCCESS_INDEX")
				+ "\n" + "失败笔数:" + result.getValue("ERROR_INDEX");
		this.messageBox(Msg);
	}
	/**
	 * 校验是否有获得焦点
	 * 
	 * @return
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要执行的数据");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", ""));// 期号
		parm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号码
		parm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));// 垫付顺序号
//		parm.setData("START_DATE", parm.getValue("IN_DATE").replace("/", ""));// 开始时间
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("PAT_AGE", parm.getValue("PAT_AGE"));// 年龄
		return parm;
	}
	
	/**
	 * 费用分割执行操作
	 */
	public void onUpdate() {
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
//		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
//			return;
//		}
		if (!this.CheckTotAmt()) {
		} else {
			feePartitionEnable(false);
			updateRun();// 准备上传医保
			update1();// 累计增付
			feePartitionEnable(true);
		}

	}
	
	/**
	 * 费用分割执行以后数据比较
	 * 
	 * @return
	 */
	public boolean CheckTotAmt() {
		TParm parm = getTableSeleted();
		if (null != parm) {
			TParm ibsUpLoadParm = INSIbsUpLoadTool.getInstance()
					.queryCheckSumIbsUpLoad(parm);
			if (ibsUpLoadParm.getErrCode() < 0) {
				return false;
			}
			TParm ibsOrderParm = INSIbsOrderTool.getInstance()
					.queryCheckSumIbsOrder(parm);
			if (ibsOrderParm.getErrCode() < 0) {
				return false;
			}
			if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) == ibsOrderParm
					.getDouble("TOTAL_AMT", 0)) {
				return true;
			} else {
				if (this.messageBox("信息", "费用分割前和费用分割后。总费用不相等是否继续", 2) == 0) {
					return true;
				}
				return false;
			}
		}
		return true;
	}
	/**
	 * 费用分割过程中按钮置灰
	 * 
	 * @param enAble
	 */
	private void feePartitionEnable(boolean enAble) {
		callFunction("UI|save|setEnabled", enAble);
		callFunction("UI|new|setEnabled", enAble);
		callFunction("UI|delete|setEnabled", enAble);
		callFunction("UI|query|setEnabled", enAble);
		callFunction("UI|changeInfo|setEnabled", enAble);
		callFunction("UI|apply|setEnabled", enAble);
		callFunction("UI|onSave|setEnabled", enAble);
		for (int i = 1; i < 11; i++) {
			callFunction("UI|NEW_RDO_" + i + "|setEnabled", enAble);
		}

	}
	/**
	 * 准备上传医保
	 * 城居操作需要判断是否 取得医令是否是儿童用药或儿童处置项目
	 * 
	 * 单病种操作 INS_IBS修改床位费特需金额和医用材料费特需金额
	 */
	private void updateRun() {
		TParm commParm = getTableSeleted();
		if (null == commParm) {

			return;
		}
		TParm parmValue = newTable.getParmValue();// 获得费用分割后表格数据
		double bedFee = regionParm.getDouble("TOP_BEDFEE", 0);
		boolean flg = false;// 输出消息框管控 判断是否分割成功
		TParm tableParm = null;
		TParm newParm = new TParm();// 累计数据
		TParm ctzParm=null;
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			// 累计增负操作时，数据库会添加一条医嘱为***018的数据
			if ("***018".equals(nhiOrderCode) || nhiOrderCode.equals("")) {// 医保号码
				continue;
			}
			if (nhiOrderCode.length() > 4) {
				String billdate = tableParm.getValue("CHARGE_DATE").replace("/", "");// 明细帐日期时间
				TParm parm = new TParm();
				ctzParm=CTZTool.getInstance().getNhiNoCtz(this.getValueString("CTZ1_CODE"));
				parm.setData("NHI_ORDER_CODE", nhiOrderCode);// 医保医嘱代码
				parm.setData("CTZ1_CODE", ctzParm.getValue("NHI_NO",0));// 身份
				parm.setData("QTY", tableParm.getValue("QTY"));// 个数
				parm.setData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT"));// 总金额
				parm.setData("TIPTOP_BED_AMT", bedFee);// 最高床位费
				parm.setData("PHAADD_FLG", tableParm.getValue("PHAADD_FLG")
						.equals("Y") ? "1" : "0");// 药品增负注记
				parm.setData("FULL_OWN_FLG", tableParm.getValue("FULL_OWN_FLG")
						.equals("Y") ? "0" : "1");// 全自费标志
				parm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));// 医保区域代码
				parm.setData("CHARGE_DATE", billdate);// 费用发生时间
				TParm splitParm = new TParm();
				if (this.getValueInt("INS_CROWD_TYPE") == 1) {// 1.城职 2.城居
					// 住院费用明细分割
					splitParm = INSTJTool.getInstance().DataDown_sp1_B(parm);

				} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
					// 住院费用明细分割
					commParm.setData("SFXMBM",nhiOrderCode);
					commParm.setData("CHARGE_DATE",billdate+"000000");//校验在日期内的数据
					TParm tempParm=INSIbsTool.getInstance().queryInsRuleET(commParm);
					if (tempParm.getErrCode()<0) {
						break;
					}
					if (null==commParm.getValue("PAT_AGE") || null==tempParm || null==tempParm.getValue("ETYYBZ",0)) {
						break;
					}
					//银海文件要求以18岁作为判断标志,并且是儿童用药  ETYYBZ=1 不可以执行
					if (commParm.getInt("PAT_AGE")>18 && 
							tempParm.getValue("ETYYBZ",0).equals("1")) {
						System.out.println("不能执行费用分割城居操作");
					}else{
						splitParm = INSTJTool.getInstance().DataDown_sp1_G(parm);
					}
				}
				if (!INSTJTool.getInstance().getErrParm(splitParm)) {
					flg = true;
					this.messageBox(parmValue.getValue("SEQ_NO", i) + "行分割失败");
					break;
				}
				// 累计数据操作
				setIbsUpLoadParm(tableParm, splitParm, newParm);
			} else {
				this.messageBox("请检查" + parmValue.getValue("SEQ_NO", i)
						+ "行医保编码");// 序号
			}

		}
		newParm.setData("OPT_USER",Operator.getID());
		newParm.setData("OPT_TERM",Operator.getIP());
//		newParm.setData("TYPE",type);//判断执行类型 ：SINGLE:单病种操作
		newParm.setData("CASE_NO", commParm.getValue("CASE_NO"));//单病种操作使用
		newParm.setData("YEAR_MON", commParm.getValue("YEAR_MON"));//期号单病种操作使用
		//执行修改INS_IBS_UPLOAD表操作
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onSaveInsUpLoad", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			this.messageBox("分割失败");
		} else {
			this.messageBox("分割成功");
		}
	}
	/**
	 * 累计增付
	 */
	private void update1() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// 累计增负 查询此病患就诊记录的总金额
		// 城职 操作 查询的数据应该是累计增负为Y的数据
		TParm result = INSIbsUpLoadTool.getInstance().querySumIbsUpLoad(parm);
		if (result.getErrCode() < 0) {
			return;
		}
		TParm splitParm = new TParm();
		splitParm.setData("ADDPAY_ADD", result.getDouble("TOTAL_AMT", 0));
		splitParm.setData("HOSP_START_DATE", parm.getValue("YEAR_MON"));
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {// 1.城职 2.城居
			// 城职累计增付
			splitParm = INSTJTool.getInstance().DataDown_sp1_C(splitParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			// 城居 住院累计增负计算
			splitParm = INSTJTool.getInstance().DataDown_sp1_H(splitParm);
		}
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			return;
		}
		TParm exeParm = new TParm();
		exeParm.setData("NHI_AMT", splitParm.getDouble("NHI_AMT"));// 申报金额
		exeParm.setData("TOTAL_AMT", result.getDouble("TOTAL_AMT", 0));// 发生金额
		exeParm.setData("TOTAL_NHI_AMT", result.getDouble("TOTAL_NHI_AMT", 0));// 医保金额
		exeParm.setData("ADD_AMT", splitParm.getDouble("ADDPAY_AMT"));// 累计增负金额
		exeParm.setData("ADDPAY_AMT", splitParm.getDouble("ADDPAY_AMT"));// 累计增负金额
		exeParm.setData("OWN_AMT", splitParm.getDouble("OWN_AMT"));// 自费金额
		exeParm.setData("CASE_NO", this.getValue("CASE_NO"));// 就诊序号
		exeParm.setData("REGION_CODE", Operator.getRegion());// 区域
		// 查询最大SEQ_NO
		TParm maxSeqParm = INSIbsUpLoadTool.getInstance().queryMaxIbsUpLoad(
				parm);
		if (maxSeqParm.getErrCode() < 0) {
			return;
		}
		exeParm.setData("SEQ_NO", maxSeqParm.getInt("SEQ_NO", 0) + 1);// 顺序号
		exeParm.setData("DOSE_CODE", "");// 剂型
		exeParm.setData("STANDARD", "");// 规格
		exeParm.setData("PRICE", 0);// 单价
		exeParm.setData("QTY", 0);// 数量
		exeParm.setData("ADM_SEQ", maxSeqParm.getValue("ADM_SEQ", 0));// 医保就诊号
		exeParm.setData("OPT_USER", Operator.getID());// ID
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("HYGIENE_TRADE_CODE", maxSeqParm.getValue(
				"HYGIENE_TRADE_CODE", 0));// 批准文号
		exeParm.setData("ORDER_CODE", "***018");// 医嘱代码
		exeParm.setData("NHI_ORDER_CODE", "***018");// 医保医嘱代码
		exeParm.setData("ORDER_DESC", "一次性材料累计增付");
		exeParm.setData("ADDPAY_FLG", "Y");// 累计增付标志（Y：累计增付；N：不累计增付）
		exeParm.setData("PHAADD_FLG", "N");// 增负药品
		exeParm.setData("CARRY_FLG", "N");// 出院带药
		exeParm.setData("OPT_TERM", Operator.getIP());//
		exeParm.setData("NHI_ORD_CLASS_CODE", "06");// 统计代码 
		exeParm.setData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("CHARGE_DATE", 0), true));// 明细录入时间
		exeParm.setData("YEAR_MON", parm.getValue("YEAR_MON"));// 期号
		result = TIOM_AppServer.executeAction("action.ins.INSBalanceAction",
				"onAdd", exeParm);
		if (result.getErrCode() < 0) {
			this.messageBox("执行累计增负失败");
			return;
		}
	}
	/**
	 * 费用分割 累计数据 添加INS_IBS_UPLOAD 表操作
	 * 
	 * @param tableParm
	 * @param newParm
	 */
	private void setIbsUpLoadParm(TParm tableParm, TParm splitParm,
			TParm newParm) {
		newParm.addData("ADM_SEQ", tableParm.getValue("ADM_SEQ"));//就诊顺序号
		newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO"));//序号
		newParm.addData("CHARGE_DATE", SystemTool.getInstance()
				.getDateReplace(tableParm.getValue("CHARGE_DATE"), true));// 明细帐日期时间
		newParm.addData("ADDPAY_AMT", splitParm.getValue("ADDPAY_AMT"));// 增负金额
		newParm.addData("TOTAL_NHI_AMT", splitParm.getValue("NHI_AMT"));// 申报金额
		newParm.addData("OWN_AMT", splitParm.getValue("OWN_AMT"));// 全自费金额
		newParm.addData("OWN_RATE", splitParm.getValue("OWN_RATE"));// 自负比例
		newParm.addData("NHI_ORD_CLASS_CODE", splitParm
				.getValue("NHI_ORD_CLASS_CODE"));// 统计代码
		newParm.addData("ADDPAY_FLG", splitParm.getValue("ADDPAY_FLG")
				.equals("Y") ? "1" : "0");// 累计增负标志

	}
	/**
	 * 页签点击事件
	 */
	public void onChangeTab() {
		switch (tabbedPane.getSelectedIndex()) {
		// 1 :费用分割前页签2：费用分割后页签
		case 1:
			onSplitOld();
			break;
		case 2:
			onSplitNew();
			break;
		}
	}
	private void onSplitOld(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，05床位费，06材料费，07其他费，08全血费，09成分血费
		String classCode = getValueString("NHI_ORD_CLASS_CODE");
		parm.setData("NHI_ORD_CLASS_CODE", classCode);
		parm.setData("YEAR_MON", parm.getValue("YEAR_MON").substring(0, 4)+parm.getValue("YEAR_MON").substring(5, 7));
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				this.messageBox("没有查询的数据");
				return;
			}
		} else {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				return;
			}
		}
		double qty = 0.00;// 数量
		double totalAmt = 0.00;// 发生金额
		double totalNhiAmt = 0.00;// 申报金额
		double ownAmt = 0.00;// 自费金额
		double addPayAmt = 0.00;// 增负金额
		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
			totalNhiAmt += result.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += result.getDouble("OWN_AMT", i);
			addPayAmt += result.getDouble("ADDPAY_AMT", i);
		}

		// //添加合计
		for (int i = 0; i < pageFour.length; i++) {
			if (i == 0) {
				result.addData(pageFour[i], "合计:");
				continue;
			}
			result.addData(pageFour[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.addData("TOTAL_NHI_AMT", totalNhiAmt);
		result.addData("OWN_AMT", ownAmt);
		result.addData("ADDPAY_AMT", addPayAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
	}
	/**
	 * 费用分割前数据
	 */
	public void onSplitOld() {
		onSplitOld(true);

	}
	/**
	 * 费用分割后数据
	 */
	public void onSplitNew() {
		onSplitNew(true);
	}
	private void onSplitNew(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，05床位费，06材料费，07其他费，08全血费，09成分血费
			String classCode = getValueString("NHI_ORD_CLASS_CODE");
			parm.setData("NHI_ORD_CLASS_CODE", classCode);
			parm.setData("YEAR_MON", parm.getValue("YEAR_MON").substring(0, 4)+parm.getValue("YEAR_MON").substring(5, 7));
		TParm upLoadParmOne = INSIbsUpLoadTool.getInstance()
				.queryNewSplit(parm);
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		TParm upLoadParmTwo = INSIbsUpLoadTool.getInstance()
				.queryNewSplitUpLoad(parm);
		if (upLoadParmTwo.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (flg) {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				this.messageBox("没有查询的数据");
				callFunction("UI|upload|setEnabled", false);// 没有数据不可以执行分割操作
				return;
			}
		} else {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				callFunction("UI|upload|setEnabled", false);// 没有数据不可以执行分割操作
				return;
			}
		}

		if (null == upLoadParmOne) {
			upLoadParmOne = new TParm();
		}
		// 合并数据
		if (upLoadParmTwo.getCount() > 0) {
			for (int i = 0; i < upLoadParmTwo.getCount(); i++) {
				upLoadParmOne.setRowData(upLoadParmOne.getCount() + 1,
						upLoadParmTwo, i);
			}

		}
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		double qty = 0.00;// 个数
		double totalAmt = 0.00;// 发生金额
		double totalNhiAmt = 0.00;// 申报金额
		double ownAmt = 0.00;// 自费金额
		double addPayAmt = 0.00;// 增负金额
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			if (upLoadParmOne.getValue("ORDER_CODE").equals("***018")) {//上传医嘱不可以累计金额
				continue;
			}
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
			totalNhiAmt += upLoadParmOne.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += upLoadParmOne.getDouble("OWN_AMT", i);
			addPayAmt += upLoadParmOne.getDouble("ADDPAY_AMT", i);
		}

		// //添加合计
		for (int i = 0; i < pageFive.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pageFive[i], "合计:");
				continue;
			}
			upLoadParmOne.addData(pageFive[i], "");
		}
		upLoadParmOne.addData("QTY", qty);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("TOTAL_NHI_AMT", totalNhiAmt);
		upLoadParmOne.addData("OWN_AMT", ownAmt);
		upLoadParmOne.addData("ADDPAY_AMT", addPayAmt);
		upLoadParmOne.addData("ADM_SEQ", "");// 就诊顺序号 主键
		upLoadParmOne.addData("FLG", "");// 新增操作
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", "");// 批文准号
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// 添加合计
		newTable.setParmValue(upLoadParmOne);
		callFunction("UI|upload|setEnabled", true);
	}
	/**
	 * 费用分割后明细数据保存操作
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("没有需要保存的数据");
			return;
		}
		parm.setData("OPT_USER", Operator.getID());// id
		parm.setData("OPT_TERM", Operator.getIP());// Ip
		parm.setData("REGION_CODE", Operator.getRegion());// 区域代码
		// 执行添加INS_IBS_UPLOAD表操作
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "updateUpLoad", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew(false);
		}
	}
	/**
	 * 费用分割后明细数据新建操作
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT", "TOTAL_NHI_AMT",
				"OWN_AMT", "ADDPAY_AMT" };
		TParm parm = newTable.getParmValue();
		TParm result = new TParm();
		// 添加一条新数据
		for (int i = 0; i < pageFive.length; i++) {
			result.setData(pageFive[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}

		result.setData("FLG", "Y");// 新增操作
		if (parm.getCount() > 0) {
			// 获得合计数据
			result.setData("ADM_SEQ", parm.getValue("ADM_SEQ", 0));// 就诊顺序号 主键
			result.setData("HYGIENE_TRADE_CODE", parm.getValue(
					"HYGIENE_TRADE_CODE", 0));// 批文准号
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1);// 移除合计
			int seqNo = -1;// 获得最大顺序号码
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1);// 顺序号
			parm.setRowData(parm.getCount(), result, -1);// 添加新建的数据
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1);// 将合计重新放入
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("没有数据不可以新建操作");
			return;
			// result.setData("ADM_SEQ",parm.getValue("ADM_SEQ",0));//就诊顺序号 主键
			// parm.setRowData(parm.getCount(),result,-1);
			// parm.setCount(parm.getCount()+1);
		}
		newTable.setParmValue(parm);
	}
	/**
	 * 费用分割后明细数据删除操作
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要删除的数据");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("不可以删除合计数据");
			return;
		}
		TParm result = INSIbsUpLoadTool.getInstance().deleteINSIbsUploadSeq(
				parm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		this.messageBox("P0005");// 执行成功
		onSplitNew(false);
	}

}
