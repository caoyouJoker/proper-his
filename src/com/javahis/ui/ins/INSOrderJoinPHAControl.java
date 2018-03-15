package com.javahis.ui.ins;

import jdo.ins.INSRuleTXTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>
 * Title:三目字典
 * </p>
 * 
 * <p>
 * Description: 医保三目字典对应：药品
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangb 2012-1-20
 */
public class INSOrderJoinPHAControl extends TControl {
	private TTable tableFee;// SYS_FEE数据
	private TParm regionParm;// 医保区域代码
	private TTable tableRule;// 医保数据
	private TParm ruleParm;// 获得所有的医保数据
	private TParm tableNewFee;// 条件匹配保存数据
	// 获得一条空行数据 条件匹配查询数据操作使用 在SYS_FEE表格中添加一条空行数据
	private TParm tempSysFeeParm;

	public void onInit() { // 初始化程序
		super.onInit();
		tableFee = (TTable) this.getComponent("TABLE_FEE");// SYS_FEE收费数据
		tableRule = (TTable) this.getComponent("TABLE_RULE");// 医保数据
		regionParm  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//获得医保区域代码
		String sql="SELECT 'N' FLG,SFDLBM, SFXMBM, XMBM, "+
		  " XMMC, XMRJ, TXBZ, "+
		  " XMLB, JX, GG, "+
		  " DW, YF, YL, "+
		  " SL, PZWH, BZJG, "+
		  " SJJG, ZGXJ, ZFBL1, "+
		  " ZFBL2, ZFBL3, BPXE,"+
		  " BZ, TJDM, FLZB1, "+
		   "FLZB2, FLZB3, FLZB4, "+
		  " FLZB5, FLZB6, FLZB7, "+
		  " SPMC, SPMCRJ, LJZFBZ, "+
		  " YYZJBZ, YYSMBM, FPLB, "+
		  " KSSJ, JSSJ, SYFW, "+
		  " SCQY, FCFYBS, MZYYBZ," +
		  " XMBZ, FYMGL, YKD228," +
		  " YKD241, AZBBZ, YKD242," +
		  " TXXMBZ,INS_ORDER_CODE FROM INS_RULE ";
	 //	TParm temParm=new TParm(TJDODBTool.getInstance().select(sql));
		
		//ruleParm = INSRuleTXTool.getInstance().selectINSRule(new TParm());
		tableNewFee = new TParm();// 条件匹配保存数据
		// 医保数据复选框事件
		tableRule.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// SYS_FEE医嘱数据复选框事件
		tableFee.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		getTempSysParm();
		callFunction("UI|save|setEnabled", false);// 保存操作
	}

	/**
	 * 条件匹配数据combox可以执行操作
	 */
	public void onFactor() {
		if (getRadioButton("SELECT_DATA").isSelected()) {
			getComboBox("CMB_FACTOR").setEnabled(true);
			onExeEnabled(false);// 可以执行
		} else {
			getComboBox("CMB_FACTOR").setEnabled(false);// 下拉列表不可选
			onExeEnabled(true);// 不可以执行

		}
		callFunction("UI|query|setEnabled", true);// 查询操作
		this.setValue("CMB_FACTOR", "");
		checkEnabled(true);
		removeTable();
	}

	/**
	 * 获得添加空行数据
	 */
	private void getTempSysParm() {
		String[] tempSysFee = { "FLG", "ORDER_CODE", "ORDER_DESC",
				"NHI_CODE_I", "NHI_CODE_O", "NHI_CODE_E", "NHI_FEE_DESC",
				"NHI_PRICE", "INSPAY_TYPE", "OWN_PRICE", "DOSE_CODE",
				"SPECIFICATION", "MAN_CODE" };
		tempSysFeeParm = new TParm();
		for (int i = 0; i < tempSysFee.length; i++) {
			if ("FLG".equals(tempSysFee[i])) {
				tempSysFeeParm.addData(tempSysFee[i], "N");
			} else {
				tempSysFeeParm.addData(tempSysFee[i], "");
			}

		}
		tempSysFeeParm.setCount(tempSysFee.length);
	}

	/**
	 * 获得单选控件
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * 获得下拉列表控件
	 * 
	 * @param name
	 * @return
	 */
	private TComboBox getComboBox(String name) {
		return (TComboBox) this.getComponent(name);
	}

	/**
	 * 单选未匹配事件
	 */
	public void onClickNoMate() {
		if (getRadioButton("RDO_NO_MATE").isSelected()) {// 未匹配单选按钮
			onExeEnabledOne(true);
			if (getRadioButton("SAME_DATA").isSelected()) {// 完全匹配数据单选按钮选中不可以执行查询操作
				onExeEnabled(true);// 不可以执行
				getComboBox("CMB_FACTOR").setEnabled(false);// 条件匹配下拉列表
			} else {
				onExeEnabled(false);// 可以执行
				getComboBox("CMB_FACTOR").setEnabled(true);// 条件匹配下拉列表

			}
		} else {// 已匹配单选按钮
			onExeEnabledOne(false);
			onExeEnabled(true);// 可以执行
			callFunction("UI|save|setEnabled", false);// 保存操作
		}
		checkEnabled(true);
		removeTable();
	}

	/**
	 * 执行可选择按钮操作
	 * 
	 * @param flg
	 */
	private void onExeEnabledOne(boolean flg) {
		getRadioButton("SAME_DATA").setEnabled(flg);// 完全匹配数据单选按钮
		getRadioButton("SELECT_DATA").setEnabled(flg);// 条件查询执行
		getComboBox("CMB_FACTOR").setEnabled(flg);// 条件匹配下拉列表
		this.setValue("CMB_FACTOR", "");
	}

	/**
	 * 移除表格数据
	 */
	private void removeTable() {
		tableFee.setParmValue(new TParm());
		tableRule.setParmValue(new TParm());
		tableFee.removeRowAll();
		tableRule.removeRowAll();
	}

	/**
	 * 执行完全匹配数据单选按钮操作管控
	 * 
	 * @param flg
	 */
	private void onExeEnabled(boolean flg) {
		// callFunction("UI|query|setEnabled", flg);// 查询操作
		callFunction("UI|removeUpdate|setEnabled", flg);// 撤销操作
		callFunction("UI|save|setEnabled", flg);// 保存操作
	}

	/**
	 * 查询数据
	 */
	public void onQuery() {
		TParm result = new TParm();
		int type = -1;
		// 已匹配选择
		if (getRadioButton("RDO_MATE").isSelected()) {
			result = onQueryMate();
			type = 1;
		} else {
			// 未匹配选择
			if (getRadioButton("SAME_DATA").isSelected()) {// 完全匹配数据选中
				result = onQueryMate();
				type = 1;
			} else {
				result = onQueryNoMate();
				type = 2;
			}
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			removeTable();
			this.messageBox("没有要查询的数据");
			return;
		}
		TParm newRuleParm = null;
		switch (type) {// 查询类型 1.已匹配 2.未匹配
		case 1:
			newRuleParm = getNewRuleParm(result, true);
			break;
		case 2:
			newRuleParm = getNewRuleParm(result, false);
			break;
		}
		if (type == 2) {// 未匹配
			if (null == newRuleParm) {
				this.messageBox("没有要查询的数据");
				callFunction("UI|save|setEnabled", false);// 保存操作
				checkEnabled(true);
				removeTable();
				return;
			}
			callFunction("UI|save|setEnabled", true);// 保存操作
			checkEnabled(false);
			tableFee.setParmValue(tableNewFee);// SYS_FEE 数据
			tableRule.setParmValue(newRuleParm);// 医保数据
		} else if (type == 1) {// 已匹配
			tableFee.setParmValue(result);// SYS_FEE 数据
			tableRule.setParmValue(newRuleParm);// 医保数据
		}

	}

	/**
	 * 医保数据显示
	 * 
	 * @param result
	 *            flg 执行查询条件 true:已匹配数据查询 false ：条件匹配数据查询
	 * @return
	 */
	private TParm getNewRuleParm(TParm result, boolean flg) {
		String xmbm = null;
		TParm newRuleParm = new TParm();// 查询赋值医保数据
		int index = 0;// 累计个数
		// int indexNew = 0;// SYS_FEE表格计算数据
		int ruleDescIndex = -1;// 三目字典服务名称长度
		int type = -1;// 查找相似度
		StringBuffer sameName = null;// 累计相同的医嘱名称 条件匹配数据操作时，添加SYS_FEE表格数据中添加空行
		// 医保表格赋值
		for (int i = 0; i < result.getCount(); i++) {
			String orderDesc = result.getValue("ORDER_DESC", i);// 医嘱名称
			int descIndex = orderDesc.length();// 医嘱名称个数
			sameName = new StringBuffer();
			for (int j = 0; j < ruleParm.getCount(); j++) {
				if (flg) {
					xmbm = getIsNull(result, i);// 获得医保服务项目代码
					if (xmbm.equals(ruleParm.getValue("XMBM", j))) {
						newRuleParm.setRowData(index, ruleParm, j);
						index++;
						break;
					}
				} else {

					type = this.getValueInt("CMB_FACTOR");// 查找相似度 根据
					// 服务项目名称1：100%、2：80%、3：50%、4：50%以下
					switch (type) {
					case 1:
						if (result.getValue("ORDER_DESC", i).equals(
								ruleParm.getValue("XMMC", j))) {// 项目名称相同
							tableNewFee.setRowData(index, result, i);
							newRuleParm.setRowData(index, ruleParm, j);
							index++;
						}
						break;
					case 2:// 80%相同

						// 
						ruleDescIndex = ruleParm.getValue("XMMC", j).length() * 4 / 5;// 三目字典截取80%
						// 相同
						if (descIndex >= ruleDescIndex) {
							// 判断是否属于医嘱名称80%相同
							if (orderDesc.contains(ruleParm.getValue("XMMC", j)
									.substring(0, ruleDescIndex))) {
								if (sameName.toString().contains(orderDesc)) {
									tableNewFee.setRowData(index,
											tempSysFeeParm, 0);// 添加一条空行
								} else {
									tableNewFee.setRowData(index, result, i);// 添加一条数据
									sameName.append(orderDesc + ",");// 累计相同医嘱名称数据
								}

								// System.out.println("sameName:::"+sameName);
								newRuleParm.setRowData(index, ruleParm, j);// 三目字典表格显示数据
								index++;
							}
						}
						break;
					case 3:// 50%相同
						ruleDescIndex = ruleParm.getValue("XMMC", j).length() * 1 / 2;// 三目字典截取50%
						// 相同
						if (descIndex >= ruleDescIndex) {
							// 判断是否属于医嘱名称50%相同
							if (orderDesc.contains(ruleParm.getValue("XMMC", j)
									.substring(0, ruleDescIndex))) {
								if (sameName.toString().contains(orderDesc)) {
									tableNewFee.setRowData(index,
											tempSysFeeParm, 0);// 添加一条空行
								} else {
									tableNewFee.setRowData(index, result, i);
									sameName.append(orderDesc + ",");// 累计相同医嘱名称数据
								}
								// tableNewFee.setRowData(indexNew, result, i);
								newRuleParm.setRowData(index, ruleParm, j);
								index++;
							}
						}

						break;
					case 4:// 50%以下 显示所有没有执行的数据
						tableNewFee = result;
						newRuleParm = ruleParm;
						break;
					}
				}

			}
		}
		// 条件匹配数据操作
		if (!flg && (type == 1 || type == 2 || type == 3)) {
			if (index == 0) {
				return null;
			}
			tableNewFee.setCount(index);
		}
		if (type != 4) {// 条件匹配所有未执行的数据
			newRuleParm.setCount(index);
		}
		return newRuleParm;
	}

	/**
	 * 已匹配数据查询
	 * 
	 * @return
	 */
	private TParm onQueryMate() {
		TParm result = INSRuleTXTool.getInstance()
				.selectMateSysFee(new TParm());
		return result;
	}

	/**
	 * 未匹配数据查询
	 * 
	 * @return
	 */
	private TParm onQueryNoMate() {
		TParm result = null;
		// 条件匹配数据操作
		if (getRadioButton("SELECT_DATA").isSelected()) {
			if (!this.emptyTextCheck("CMB_FACTOR")) {
				return null;
			}
			result = INSRuleTXTool.getInstance().selectNotMateSysFee(
					new TParm());// 查询需要匹配的数据 SYS_FEE 数据
		}

		return result;

	}

	/**
	 * 单击事件
	 */
	public void onTableDoubleClick() {
		int row = tableFee.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = tableFee.getParmValue();
		TParm parm = new TParm();
		//parm.setData("SFDLBM", regionParm.getValue("NHI_NO", 0));
		String xmbm = getIsNull(tableParm, row);// 获得医保服务项目代码
		if (null != xmbm) {
			parm.setData("SFXMBM", xmbm);
		}
		TParm result = INSRuleTXTool.getInstance().selectINSRule(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		tableRule.setParmValue(result);
	}

	/**
	 * 全选按钮操作
	 * 
	 * @param flg
	 */
	private void checkEnabled(boolean flg) {
		callFunction("UI|SELECT_ALL|setEnabled", flg);// 全选
	}

	/**
	 * 获得医保服务项目代码
	 * 
	 * @param parm
	 *            表格数据
	 * @param row
	 *            当前行
	 * @return
	 */
	public String getIsNull(TParm parm, int row) {
		String xmbm = null;
		if (null != parm.getValue("NHI_CODE_I", row)
				&& parm.getValue("NHI_CODE_I", row).length() > 0) {
			xmbm = parm.getValue("NHI_CODE_I", row);// 住院医保
		}
		if (null != parm.getValue("NHI_CODE_O", row)
				&& parm.getValue("NHI_CODE_O", row).length() > 0) {
			xmbm = parm.getValue("NHI_CODE_O", row);// 门诊医保
		}
		if (null != parm.getValue("NHI_CODE_E", row)
				&& parm.getValue("NHI_CODE_E", row).length() > 0) {
			xmbm = parm.getValue("NHI_CODE_E", row);// 急诊医保
		}
		return xmbm;
	}

	/**
	 * 保存操作
	 */
	public void onSave() {
		if (getRadioButton("SAME_DATA").isSelected()) {// 完全匹配数据单选按钮选中操作
			onExeSameSave();
			onQuery();
		}
		if (getComboBox("CMB_FACTOR").isEnabled()) {// 条件匹配数据复选框选择操作{
			// 条件匹配操作 修改医嘱信息
			onExeUpdate();
		}
	}

	/**
	 * 条件匹配数据 修改医嘱操作
	 */
	private void onExeUpdate() {
		boolean flg = false;
		TParm parm = tableFee.getParmValue();// 获得医嘱信息
		TParm updateParm = new TParm();
		int index = 0;// 累计修改数据个数
		for (int i = 0; i < parm.getCount(); i++) {
			// 校验是否被选中
			if (parm.getBoolean("FLG", i)
					&& (null != parm.getValue("NHI_CODE_I", i)
							&& parm.getValue("NHI_CODE_I", i).length() > 0
							|| null != parm.getValue("NHI_CODE_O", i)
							&& parm.getValue("NHI_CODE_O", i).length() > 0 || null != parm
							.getValue("NHI_CODE_E", i)
							&& parm.getValue("NHI_CODE_E", i).length() > 0)) {
				flg = true;
				updateParm.setRowData(index, parm, i);// 累计数据
				index++;
			}
		}
		if (!flg) {
			this.messageBox("请选择要修改的数据");
			return;
		}
		updateParm.setData("OPT_USER", Operator.getID());
		updateParm.setData("OPT_TERM", Operator.getIP());
		updateParm.setCount(index);
		// 修改医嘱操作
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSRuleTXAction", "updateSameSave", updateParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
		} else {
			this.messageBox("P0005");// 执行成功
		}
	}

	/**
	 * 完全匹配数据单选按钮选中操作
	 */
	private void onExeSameSave() {
		TParm sysFeeParm = INSRuleTXTool.getInstance().selectSumSame(
				new TParm());
		if (sysFeeParm.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		TParm parm = new TParm();
		int index = 0;
		for (int i = 0; i < sysFeeParm.getCount(); i++) {// 执行将项目名称相同的数据修改医保项目代码
			for (int j = 0; j < ruleParm.getCount(); j++) {
				if (null != sysFeeParm.getValue("HYGIENE_TRADE_CODE", i)
						&& sysFeeParm.getValue("HYGIENE_TRADE_CODE", i)
								.length() > 0
						&& null != ruleParm.getValue("PZWH", j) && ruleParm.getValue("PZWH", j).length()>0
						&& sysFeeParm.getValue("HYGIENE_TRADE_CODE", i).equals(
								ruleParm.getValue("PZWH", j))) {

					parm.addData("ORDER_CODE", sysFeeParm.getValue(
							"ORDER_CODE", i));// 医嘱代码
					parm.addData("NHI_CODE_O", ruleParm.getValue("XMBM", j));// 门诊医保代码
					parm.addData("NHI_CODE_E", ruleParm.getValue("XMBM", j));// 急诊医保代码
					parm.addData("NHI_CODE_I", ruleParm.getValue("XMBM", j));// 住院医保代码
					parm.addData("NHI_FEE_DESC", ruleParm.getValue("XMMC", j));// 医保名称
					parm.addData("NHI_PRICE", ruleParm.getValue("SJJG", j));// 医保单价
					parm.addData("INSPAY_TYPE", ruleParm.getValue("XMLB", j));// 医保给付类别A：医保
					// B：增付C：自费
					index++;
					break;
					// parm.addData("ADDPAY_RATE", ruleParm.getValue("ZFBL1",
					// i));// 增负比例
				}
			}
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setCount(index);
		// 修改医嘱操作
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSRuleTXAction", "updateSameSave", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
		} else {
			this.messageBox("P0005");// 执行成功
		}
	}

	/**
	 * SYS_FEE全选操作
	 */
	public void onSelectAll() {
		boolean flg = false;// 判断是否选中
		if (this.getValueBoolean("SELECT_ALL")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = tableFee.getParmValue();// 获得总账数据
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		tableFee.setParmValue(parm);
	}

	/**
	 * 复选框选中事件
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		// TParm oldTempParm = new TParm();
		// TParm oldParm = table.getParmValue();
		// int index = 0;
		table.acceptText();
		int ruleRow = table.getSelectedRow();// 医保当前选中行
		TParm ruleParm = table.getParmValue();// 医保数据
		if (getComboBox("CMB_FACTOR").isEnabled()) {// 条件匹配数据复选框选择操作
			int row = tableFee.getSelectedRow();// SYS_FEE医嘱数据获得当前行
			if (row < 0) {
				this.messageBox("请选择要修改的医嘱");
				ruleParm.setData("FLG", ruleRow, "N");// 执行错误重置状态
				tableRule.setParmValue(ruleParm);
				return false;
			}

			TParm sysFeeParm = tableFee.getParmValue();// SYS_FEE医嘱
			// 选中空行不执行
			if (null == sysFeeParm.getValue("ORDER_CODE", row)
					|| sysFeeParm.getValue("ORDER_CODE", row).trim().length() <= 0) {
				this.messageBox("请选择要修改的医嘱");
				ruleParm.setData("FLG", ruleRow, "N");// 执行错误重置状态
				tableRule.setParmValue(ruleParm);
				return false;
			}
			if (ruleParm.getBoolean("FLG", ruleRow)) {// 医保选中的行复选框状态
				sysFeeParm.setData("FLG", row, "Y");// 选中
				sysFeeParm.setData("NHI_CODE_O", row, ruleParm.getValue("XMBM",
						ruleRow));// 门诊医保代码
				sysFeeParm.setData("NHI_CODE_E", row, ruleParm.getValue("XMBM",
						ruleRow));// 急诊医保代码
				sysFeeParm.setData("NHI_CODE_I", row, ruleParm.getValue("XMBM",
						ruleRow));// 住院医保代码
				sysFeeParm.setData("NHI_FEE_DESC", row, ruleParm.getValue(
						"XMMC", ruleRow));// 医保名称
				sysFeeParm.setData("NHI_PRICE", row, ruleParm.getValue("SJJG",
						ruleRow));// 医保单价
				sysFeeParm.setData("INSPAY_TYPE", row, ruleParm.getValue(
						"XMLB", ruleRow));// 医保给付类别A：医保
			} else {
				sysFeeParm.setData("FLG", row, "N");// 选中
				sysFeeParm.setData("NHI_CODE_O", row, "");// 门诊医保代码
				sysFeeParm.setData("NHI_CODE_E", row, "");// 急诊医保代码
				sysFeeParm.setData("NHI_CODE_I", row, "");// 住院医保代码
				sysFeeParm.setData("NHI_FEE_DESC", row, "");// 医保名称
				sysFeeParm.setData("NHI_PRICE", row, "");// 医保单价
				sysFeeParm.setData("INSPAY_TYPE", row, "");// 医保给付类别A：医保
			}

			tableFee.setParmValue(sysFeeParm);// 重新赋值
			tableFee.setSelectedRow(row);// 选中当前行
		}
		return false;

	}

	/**
	 * 移除选中的医嘱 医保数据
	 */
	public void onRemoveUpdate() {
		if (getRadioButton("RDO_MATE").isSelected()
				|| getRadioButton("SAME_DATA").isSelected()) {// 已匹配的数据移除操作
			TParm parm = tableFee.getParmValue();// 已匹配医嘱数据
			if (null == parm) {
				this.messageBox("请选择要执行的数据");
				return;
			}
			boolean flg = false;
			// TParm exeParm=new TParm();//执行的数据
			StringBuffer exeOrderCode = new StringBuffer();// 执行的数据
			for (int i = 0; i < parm.getCount(); i++) {
				if (parm.getBoolean("FLG", i)) {// 选中数据
					flg = true;
					exeOrderCode.append("'" + parm.getValue("ORDER_CODE", i)
							+ "',");
				}
			}
			if (!flg) {
				this.messageBox("请选择要执行的数据");
				return;
			}
			if (this.messageBox("提示", "是否执行移除操作", 2) != 0) {
				return;
			}
			String exeOrder = exeOrderCode.toString().substring(0,
					exeOrderCode.toString().lastIndexOf(","));// 执行的数据去掉末尾","
			// 执行sql
			String sql = " UPDATE SYS_FEE SET NHI_CODE_O=NULL,NHI_CODE_E=NULL,NHI_CODE_I=NULL,NHI_FEE_DESC=NULL,"
					+ "NHI_PRICE = NULL,INSPAY_TYPE = NULL, OPT_USER='"
					+ Operator.getID()
					+ "',OPT_DATE=SYSDATE"
					+ ", OPT_TERM='"
					+ Operator.getIP()
					+ "' WHERE  ORDER_CODE IN ("
					+ exeOrder
					+ ")";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));// 修改移除操作
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");// 执行失败
			} else {
				this.messageBox("P0005");// 执行成功
				onQuery();
			}
		}
	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		removeTable();
		onExeEnabledOne(false);
		callFunction("UI|removeUpdate|setEnabled", true);// 撤销操作
		callFunction("UI|query|setEnabled", true);// 查询操作
		callFunction("UI|save|setEnabled", false);// 保存操作
		checkEnabled(true);// 全选
		callFunction("UI|SELECT_ALL|setSelectd", false);// 全选
		// ((TCheckBox)this.getComponent("SELECT_ALL")).setSelected(false);
		getRadioButton("RDO_MATE").setSelected(true);// 已匹配
		getRadioButton("SAME_DATA").setSelected(true);// 完全匹配数据
	}
}
