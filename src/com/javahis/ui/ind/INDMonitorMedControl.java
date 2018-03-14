package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.ind.INDMonitorMedTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:药品监测品种维护
 * </p>
 * 
 * <p>
 * Description:药品监测品种维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2017-01-22
 * @version JavaHis 1.0
 */
public class INDMonitorMedControl extends TControl {

	private TTable table;

	@Override
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		// 设置弹出菜单
		TParm parm = new TParm();
		// parm.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		onClear();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm queryParm = new TParm();
		if (this.getTRadioButton("STARTUSE").isSelected()) {
			// 启用
			queryParm.setData("ENABLE_FLG", "Y");
		} else {
			// 停用
			queryParm.setData("ENABLE_FLG", "N");
		}
		String temp = null;
		temp = this.getValueString("ORDER_CODE");
		if (!StringUtils.isEmpty(temp)) {
			queryParm.setData("ORDER_CODE", temp);
		}
		temp = this.getValueString("MONITOR_TYPE");
		if (!StringUtils.isEmpty(temp)) {
			queryParm.setData("MONITOR_TYPE", temp);
		}
		TParm result = INDMonitorMedTool.getNewInstance().selectMonitorMed(
				queryParm);
		if (result.getCount("ORDER_CODE") <= 0) {
			// this.messageBox("查无数据");
			table.setParmValue(new TParm());
		} else {
			table.setParmValue(result);
		}

	}

	/**
	 * 表格选中事件 给上面赋值
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = table.getParmValue();
		this.setValue("ORDER_CODE", tableParm.getData("ORDER_CODE", row));
		this.setValue("ORDER_DESC", tableParm.getData("ORDER_DESC", row));
		this.setValue("SPECIFICATION", tableParm.getData("SPECIFICATION", row));
		getTComboBox("MONITOR_TYPE").setValue(
				tableParm.getData("MONITOR_TYPE", row));
		if ("Y".equals(tableParm.getData("ENABLE_FLG", row))) {
			getTRadioButton("STARTUSE").setSelected(true);
			getTRadioButton("STOPUSE").setSelected(false);
		} else {
			getTRadioButton("STOPUSE").setSelected(true);
			getTRadioButton("STARTUSE").setSelected(false);
		}
		this.getTextField("ORDER_CODE").setEnabled(false);

	}

	/**
	 * 保存或修改
	 */
	public void onSave() {

		String orderCode = this.getValueString("ORDER_CODE");
		if (StringUtils.isEmpty(orderCode)) {
			this.messageBox("请输入药品代码！");
			return;
		}
		TParm saveParm = new TParm();
		saveParm.setData("ORDER_CODE", orderCode);

		String monitorType = this.getValueString("MONITOR_TYPE");
		if (StringUtils.isEmpty(monitorType)) {
			this.messageBox("请输入监控类型！");
			return;
		}
		saveParm.setData("MONITOR_TYPE", monitorType);

		if (this.getTRadioButton("STARTUSE").isSelected()) {
			// 启用
			saveParm.setData("ENABLE_FLG", "Y");
		} else {
			// 停用
			saveParm.setData("ENABLE_FLG", "N");
		}
		saveParm.setData("ORDER_DESC", this.getValueString("ORDER_DESC"));
		saveParm.setData("SPECIFICATION", this.getValueString("SPECIFICATION"));
		saveParm.setData("OPT_USER", Operator.getID());
		saveParm.setData("OPT_TERM", Operator.getIP());
		saveParm.setData("OPT_DATE", this.formatDate(new Date()));

		int row = table.getSelectedRow();
		if (row >= 0) {
			// 更新
			TParm result = null;
			String oldMonitor = table.getParmValue().getData("MONITOR_TYPE",
					row)
					+ "";
			if (!monitorType.equals(oldMonitor)) {
				// moitor变了
				result = INDMonitorMedTool.getNewInstance().selectMonitorMed(
						saveParm);
				if (result.getCount("ORDER_CODE") > 0) {
					this.messageBox(this.getValueString("ORDER_DESC")
							+ "（"
							+ this.getValueString("SPECIFICATION")
							+ "）已存在 ["
							+ this.getTComboBox("MONITOR_TYPE")
									.getSelectedName() + "]监测类别");
					return;
				}
			}
			saveParm.setData("OLD_MONITOR_TYPE",
					table.getParmValue().getData("MONITOR_TYPE", row));
			result = INDMonitorMedTool.getNewInstance().updateMonitorMed(
					saveParm);
			if (result.getErrCode() < 0) {
				this.messageBox("更新失败");
			} else {
				this.messageBox("更新成功");
			}
		} else {
			// 新增
			// 判断此药品是否是医保药品
			if ("NVAL".equals(monitorType)) {
				TParm feeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
				String date = SystemTool.getInstance().getDate().toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
				TParm ruleParm = getMedInsRule(feeParm.getValue("NHI_CODE_I", 0), feeParm.getValue("NHI_CODE_O", 0), feeParm.getValue("NHI_CODE_E", 0), date );
				boolean flg = true;  //初始默认为医保药品
				for(int i = 0; i < ruleParm.getCount("SFXMBM"); i++) {
					if("005306".equals(ruleParm.getData("SFXMBM", i))) {
						//非医保药品
						flg = false;
						break;
					}
				}
				if(flg) {
					// 医保药品 提示一下
					if (this.messageBox("警告",
							this.getValueString("ORDER_DESC") + "\n"
									+ "为医保药品，\n 确认将其加入到非医保检测列表中？",
							this.YES_NO_OPTION) == this.NO_OPTION) {
						return;
					}
				}
			}
			TParm result = INDMonitorMedTool.getNewInstance().selectMonitorMed(
					saveParm);
			if (result.getCount("ORDER_CODE") > 0) {
				this.messageBox(this.getValueString("ORDER_DESC") + "（"
						+ this.getValueString("SPECIFICATION") + "）已存在 ["
						+ this.getTComboBox("MONITOR_TYPE").getSelectedName()
						+ "]监测类别");
				return;
			}
			result = INDMonitorMedTool.getNewInstance()
					.saveMonitorMed(saveParm);
			if (result.getErrCode() < 0) {
				this.messageBox("新增失败");
			} else {
				this.messageBox("新增成功");
			}
		}
		this.getTextField("ORDER_CODE").setEnabled(true);
		this.clearValue("ORDER_CODE;ORDER_DESC;SPECIFICATION");
		onQuery();
	}

	/**
	 * 获取药品的三木编码
	 * @param nhicodeI
	 * @param nhicodeO
	 * @param nhicodeE
	 * @param date
	 * @return
	 */
	public TParm getMedInsRule(String nhicodeI, String nhicodeO, String nhicodeE,
			String date) {
		String sql = " SELECT SFXMBM, XMMC, BZJG, "
				+ "CASE MZYYBZ WHEN '1' THEN 'Y' ELSE 'N' END AS MZYYBZ,"
				+ "CASE ETYYBZ WHEN '1' THEN 'Y'  ELSE 'N' END AS ETYYBZ,"
				+ "CASE YKD242 WHEN '1' THEN 'Y' ELSE 'N' END AS YKD242,"
				+ "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, "
				+ "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ"
				+ " FROM INS_RULE" + " WHERE SFXMBM IN ('" + nhicodeI + "','"
				+ nhicodeO + "'," + "'" + nhicodeE + "') " + " AND   TO_DATE('"
				+ date + "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ"
				+ " ORDER BY SFXMBM";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			//this.messageBox("E0116");// 没有数据
			return result;
		}
		return result;
	}

	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		return sdf.format(date);
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择一条进行删除");
			return;
		}
		if (this.messageBox("提示", "确认删除?", YES_NO_OPTION) == NO_OPTION) {
			return;
		}
		TParm tableParm = table.getParmValue();
		TParm result = INDMonitorMedTool.getNewInstance().delectMonitorMed(
				tableParm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("删除失败");
		} else {
			this.messageBox("删除成功");
		}
		this.getTextField("ORDER_CODE").setEnabled(true);
		this.clearValue("ORDER_CODE;ORDER_DESC;SPECIFICATION");
		onQuery();
	}

	/**
	 * 清空
	 */
	public void onClear() {
		table.setParmValue(new TParm());
		getTRadioButton("STARTUSE").setSelected(true);
		getTRadioButton("STOPUSE").setSelected(false);
		this.getTextField("ORDER_CODE").setEnabled(true);
		this.clearValue("ORDER_CODE;ORDER_DESC;SPECIFICATION;MONITOR_TYPE");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
		String spec = parm.getValue("SPECIFICATION");
		if (!StringUtil.isNullString(spec))
			getTextField("SPECIFICATION").setValue(spec);

	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到RadioButton对象
	 * 
	 * @param tag
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * 获取TComboBox对象
	 * 
	 * @param tag
	 * @return
	 */
	private TComboBox getTComboBox(String tag) {
		return (TComboBox) getComponent(tag);
	}
}
