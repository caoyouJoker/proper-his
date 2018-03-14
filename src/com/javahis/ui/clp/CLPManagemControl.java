package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import java.util.regex.Pattern;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.util.regex.Matcher;
import jdo.clp.CLPManagemTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * Title: 临床路径准进准出
 * </p>
 * 
 * <p>
 * Description: 临床路径准进准出
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class CLPManagemControl extends TControl {
	public CLPManagemControl() {

	}

	/**
	 * 就诊序号
	 */
	private String case_no;
	private String mr_no;
	// 提交对应的列数
	private int commitIndex = 21;

	/**
	 * 页面初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	// 根据病患选择界面查询对应的病人信息
	private void initPatientInfo() {

		TParm inParm = (TParm) this.getParameter();
		case_no = inParm.getValue("CLP", "CASE_NO");
		mr_no = inParm.getValue("CLP", "MR_NO");
		this.setValue("IS_IN", "Y");
		//System.out.println("mr_no:::::"+mr_no);
		String flg = inParm.getValue("CLP", "FLG");
		if (null != flg && flg.equals("Y")) {
			callFunction("UI|close|setEnabled", false);
		}
	}

	/**
	 * 初始化
	 */
	private void initPage() {
		// 绑定表格事件
		callFunction("UI|TABLEMANAGEM|addEventListener", "TABLEMANAGEM->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 页面加载时根据病患选择界面查询对应的病人信息
		initPatientInfo();
		this.onQuery();
		String sql="SELECT CASE_NO FROM ADM_INP WHERE CASE_NO='"+case_no+"' AND DS_DATE IS NOT NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()>0) {//出院病人不可以操作
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|add|setEnabled", false);
			callFunction("UI|change|setEnabled", false);
			callFunction("UI|query|setEnabled", false);
			callFunction("UI|clear|setEnabled", false);
		}
	}

	// 记录行号
	int selectRow = -1;

	/**
	 * 处理当前TOOLBAR
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * 页面查询方法
	 */
	public void onQuery() {
		this.getPaitientManagerListTParm();
	}

	private TParm getPaitientManagerListTParm() {
		
		// 查询条件
		TParm selectTparm = new TParm();
		TParm resultTParm = new TParm();
		// 修改，使用病患选择页面的mr_no
		selectTparm.setData("MR_NO", this.mr_no);
		selectTparm.setData("CASE_NO", this.case_no);
		this.putBasicSysInfoIntoParm(selectTparm);
		if(this.getValueBoolean("IS_IN")){//查询进入路径
			// 调用查询方法
			resultTParm = CLPManagemTool.getInstance()
					.selectData(selectTparm);
			if (resultTParm.getCount() > 0) {
				TParm rowParm = resultTParm.getRow(0);
				String clncPathCode = rowParm.getValue("CLNCPATH_CODE");
				String evlCode = rowParm.getValue("EVL_CODE");
				this.setValue("CLNCPATH_CODE", clncPathCode);
				this.setValue("EVL_CODE", evlCode);
			}else{
				this.setValue("CLNCPATH_CODE", "");
				this.setValue("EVL_CODE", "");
			}
			callFunction("UI|change|setEnabled", true);
			callFunction("UI|timeInterval|setEnabled", true);
			callFunction("UI|save|setEnabled", true);
		}
		if(this.getValueBoolean("IS_CANCELLATION")){//查询作废路径
			// 调用查询方法
			resultTParm = CLPManagemTool.getInstance()
					.selecCanceltData(selectTparm);
			callFunction("UI|change|setEnabled", false);
			callFunction("UI|timeInterval|setEnabled", false);
			callFunction("UI|save|setEnabled", false);
		}
		if(this.getValueBoolean("IS_OVERFLOW")){//查询溢出路径
			// 调用查询方法
			resultTParm = CLPManagemTool.getInstance()
					.selectOverData(selectTparm);
			callFunction("UI|change|setEnabled", false);
			callFunction("UI|timeInterval|setEnabled", false);
			callFunction("UI|save|setEnabled", false);
		}
		this.callFunction("UI|TABLEMANAGEM|setParmValue", resultTParm);
		return resultTParm;
	}

	/**
	 *保存方法
	 */
	public void onSave() {
		// 保存临床路径准进准出状态
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		table.acceptText();
		int selectedRow = this.getSelectedRow("TABLEMANAGEM");
		if (selectedRow < 0) {
			this.messageBox("请选择临床路径");
			return;
		}
		TParm parm = table.getParmValue().getRow(selectedRow);
		TParm updateParm = new TParm();
		updateParm.setData("tableParm", parm.getData());
		updateParm.setData("basicMap", this.getBasicOperatorMap());
		updateParm.setData("currentDateStr", this.getCurrentDateStr());
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "updateManagem", updateParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		// 保存成功后刷新页面记录
		this.onQuery();
	}

	/**
	 * 添加方法
	 */
	public void onAdd() {
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		TParm parm = new TParm();
		parm.setData("MR_NO", this.mr_no);
		parm.setData("CASE_NO", this.case_no);
		this.putBasicSysInfoIntoParm(parm);
		TParm result = CLPManagemTool.getInstance().getPatientInfo(parm);
		if (result.getCount() <= 0) {
			this.messageBox("请先查询住院病人信息");
			return;
		}
		// 调用查询方法
		TParm resultTParm = CLPManagemTool.getInstance().selectData(parm);
		if (result.getCount() <= 0) {
			this.messageBox("请先查询住院病人信息");
			return;
		}
		if (resultTParm.getCount() > 0) {
			this.messageBox("该病人临床路径信息已经存在!");
			return;
		}
		if (!validData()) {
			return;
		}
		parm = getTparmFromBasicInfo();
		putBasicSysInfoIntoParm(parm);
		String sql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND END_DTTM IS NOT NULL";
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selparm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (selparm.getCount("CASE_NO")>0) {
			this.messageBox("此病患的临床路径项目已经溢出,不可以添加");
			return;
		}
		//====yanjing 20140710 查询该路径是否存在作废的数据 start
		String cancleSql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' " +
				"AND CLNCPATH_CODE = '"+parm.getValue("CLNCPATH_CODE")+"'";
		TParm cancleParm = new TParm(TJDODBTool.getInstance().select(cancleSql));
		if (cancleParm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (cancleParm.getCount("CASE_NO")>0) {
			this.messageBox("此病患存在作废的该临床路径项目");
			return;
		}
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("IN_DATE", datestr);
		parm.setData("START_DTTM", getCurrentDateTimeStr());
		TParm resultParam = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "insertManagem", parm);
		if (resultParam.getErrCode() >= 0) {
			this.messageBox("P0002");
		} else {
			this.messageBox("E0002");
		}
		this.onQuery();

	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		selectRow = this.getSelectedRow("TABLEMANAGEM");
		if (selectRow == -1) {
			this.messageBox("请选择需要删除的临床路径");
			return;
		}
		if (this.messageBox("询问", "是否删除", 2) == 0) {
			TTable table = (TTable) this.getComponent("TABLEMANAGEM");
			int selRow = table.getSelectedRow();
			TParm tableParm = table.getParmValue();
			String CASE_NO = tableParm.getValue("CASE_NO", selRow);
			String CLNCPATH_CODE = tableParm.getValue("CLNCPATH_CODE", selRow);
			TParm parm = new TParm();
			parm.setData("CASE_NO", CASE_NO);
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE);
			TParm saveParm = new TParm();
			saveParm.setData("parm", parm.getData());
			saveParm.setData("basicMap", this.getBasicOperatorMap());
			TParm result = TIOM_AppServer.executeAction(
					"action.clp.CLPManagemAction", "deleteManagem", saveParm);
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			}
			// 删除单行显示
			int row = (Integer) callFunction("UI|TABLEMANAGEM|getSelectedRow");
			if (row < 0) {
				return;
			}
			this.callFunction("UI|TABLEMANAGEM|removeRow", row);
			this.callFunction("UI|TABLEMANAGEM|setSelectRow", row);
			this.messageBox("P0003");
		} else {
			return;
		}
	}

	/**
	 *临床路径变动
	 */
	public void onChange() {
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		if (table.getRowCount() <= 0) {
			this.messageBox("请选择路径!");
			return;
		}
		// 防止用户改动临床路径状态为已进入但未保存的情况，所以每次变更都从数据库中重新取值
		TParm tableParm = getPaitientManagerListTParm();
		String isInStr = (String) tableParm.getData("IS_IN", 0);
		if ("N".equals(isInStr)) {
			this.messageBox("请确定您所选路径为已进入，未作废或溢出的路径!若变更了路径状态，请及时保存!");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("OLD_CLNCPATH_CODE", this
				.getValueString("CLNCPATH_CODE"));
		sendParm.setData("MR_NO", this.mr_no);
		sendParm.setData("EVL_CODE", this.getValueString("EVL_CODE"));
		sendParm.setData("CASE_NO", this.case_no);
		String resultstr = (String) this.openDialog(
				"%ROOT%\\config\\clp\\CLPManageChangeNew.x", sendParm);
		if (null!=resultstr) {
			if (resultstr.toLowerCase().indexOf("success") >= 0) {
				this.onQuery();
			}
		}
	}

//	/**
//	 * 实际时程设定
//	 */
//	public void durationConfig() {
//		int selectIndex = this.getSelectedRow("TABLEMANAGEM");
//		if (selectIndex < 0) {
//			this.messageBox("请选择设定数据");
//			return;
//		}
//		TParm sendParm = new TParm();
//		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
//		TParm tableParm = table.getParmValue();
//		sendParm.setData("MR_NO", tableParm.getRow(0).getValue("MR_NO"));
//		sendParm.setData("PAT_NAME", tableParm.getRow(0).getValue("PAT_NAME"));
//		sendParm.setData("CLNCPATH_CODE", tableParm.getRow(0).getValue(
//				"CLNCPATH_CODE"));
//		sendParm.setData("CASE_NO", tableParm.getRow(0).getValue("CASE_NO"));
//		sendParm.setData("VERSION", tableParm.getRow(0).getValue("VERSION"));// =======pangben
//																				// 2012-5-28
//																				// 版本
//		sendParm
//				.setData("DEPT_CODE", tableParm.getRow(0).getValue("DEPT_CODE"));// =======pangben
//																					// 2012-5-28
//																					// 科室
//		String resultstr = (String) this.openDialog(
//				"%ROOT%\\config\\clp\\CLPDurationConfig.x", sendParm);
//	}
	
	/**
	 * 展开实际（路径展开）--去掉“实际时程设定”弹出界面，
	 * 改为之前弹出界面里功能：路径展开--xiongwg20150428
	 */
	public void durationConfig() {
		int selectIndex = this.getSelectedRow("TABLEMANAGEM");
		if (selectIndex < 0) {
			this.messageBox("请选择设定数据");
			return;
		}
		//获取数据参数
		TParm patientTParm = new TParm();
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		//========pangben 2015-10-21 校验多个临床路径
		TParm tableParm = table.getParmValue();
		TParm clpParm= new TParm(TJDODBTool.getInstance().select(
				"SELECT SCHD_CODE FROM IBS_ORDD WHERE CASE_NO='"
						+ tableParm.getRow(0).getValue("CASE_NO") + "' GROUP BY SCHD_CODE"));
    	TParm clpSParm= new TParm(TJDODBTool.getInstance().select(
				"SELECT CLNCPATH_CODE FROM IBS_ORDD WHERE CASE_NO='"
						+ tableParm.getRow(0).getValue("CASE_NO") + "' GROUP BY CLNCPATH_CODE"));
    	if (clpSParm.getCount()<=0) {
			this.messageBox("病案号："+ tableParm.getRow(0).getValue("MR_NO")+
					"姓名:"+tableParm.getRow(0).getValue("PAT_NAME")+"没有获得此病患医嘱数据");
			return;
		}
    	if (clpSParm.getCount()>1) {
    		if (this.messageBox("提示","病案号："+ tableParm.getRow(0).getValue("MR_NO")+
					"姓名:"+tableParm.getRow(0).getValue("PAT_NAME")+"交易表中存在为空值或多个路径的情况,是否继续",2)!=0) {
				return;
			}
		}
    	for (int j = 0; j < clpParm.getCount(); j++) {
			if (null==clpParm.getValue("SCHD_CODE",j)||clpParm.getValue("SCHD_CODE",j).length()<=0) {
				if (this.messageBox("提示","病案号："+ tableParm.getRow(0).getValue("MR_NO")+
					"姓名:"+tableParm.getRow(0).getValue("PAT_NAME")+"存在为空值时程,是否继续",2)!=0) {
					return;
				}
			}
		}
		patientTParm.addData("REGION_CODE",
                this.getOperatorMap().get("REGION_CODE"));
		patientTParm.addData("MR_NO", tableParm.getRow(0).getValue("MR_NO"));
		patientTParm.addData("PAT_NAME", tableParm.getRow(0).getValue("PAT_NAME"));
		patientTParm.addData("CLNCPATH_CODE", tableParm.getRow(0).getValue(
				"CLNCPATH_CODE"));
		patientTParm.addData("CASE_NO", tableParm.getRow(0).getValue("CASE_NO"));
		patientTParm.addData("VERSION", tableParm.getRow(0).getValue("VERSION"));// =======pangben
																				// 2012-5-28
																				// 版本
		patientTParm
				.addData("DEPT_CODE", tableParm.getRow(0).getValue("DEPT_CODE"));// =======pangben
																					// 2012-5-28
																					// 科室
		patientTParm.setCount(1);
		
		TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("展开失败");
        } else {
            this.messageBox("展开成功");
        }
	}
	 /**
     * 得到展开时间
     * @return String
     */
    private String getDeployDate() {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        String datestrtmp = this.getValueString("deployDate");
        if (!this.checkNullAndEmpty(datestrtmp)) {
            datestrtmp = datestr;
        } else {
            datestrtmp = datestrtmp.substring(0, 10).replace("-", "");
        }
        return datestrtmp;
    }
    /**
     * 根据Operator得到map
     * @return Map
     */
    private Map getOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

	/**
	 * 从界面中得到表单
	 * 
	 * @return TParm
	 */
	private TParm getTparmFromBasicInfo() {
		TParm parm = new TParm();
		//this.putParamWithObjName("CASE_NO", parm);
		//this.putParamWithObjName("MR_NO", parm);
		this.putParamWithObjName("CLNCPATH_CODE", parm);
		this.putParamWithObjName("EVL_CODE", parm);
		parm.setData("CASE_NO",case_no);
		parm.setData("MR_NO",mr_no);
		return parm;
	}

	/**
	 * 数据验证方法
	 * 
	 * @return boolean
	 */
	private boolean validData() {
		boolean flag = true;
		if (this.getValueString("CLNCPATH_CODE") == null
				|| this.getValueString("CLNCPATH_CODE").length() <= 0) {
			this.messageBox("请选择临床路径");
			return false;
		}
		if (this.getValueString("EVL_CODE") == null
				|| this.getValueString("EVL_CODE").length() <= 0) {
			this.messageBox("请选择评估代码");
			return false;
		}
		return flag;
	}

	/**
	 * 页面清空方法
	 */
	public void onClear() {
		this.setValue("CLNCPATH_CODE", "");
		this.setValue("EVL_CODE", "");
		 this.setValue("IS_IN", "Y");
		 this.onQuery();
	}

	/**
	 * 表格点击方法
	 */
	public void onTableClicked(int row) {
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		table.acceptText();
		int selectedColumnIndex = table.getSelectedColumn();
		int selectedRowIndex = table.getSelectedRow();
		if (selectedRowIndex < 0) {
			return;
		}
		TParm tableParm = table.getParmValue();
		/* 加入单选判断 begin */
		// 是否进入标识
		String isInstr = (String) tableParm.getData("IS_IN", selectedRowIndex);
		boolean isInFalg = "Y".equals(isInstr) ? true : false;
		// 是否设置作废
		String isCancelLationStr = (String) tableParm.getData(
				"IS_CANCELLATION", selectedRowIndex);
		boolean isCancelLationFlag = "Y".equals(isCancelLationStr) ? true
				: false;
		// 设置溢出的值
		String isOverFlowStr = (String) tableParm.getData("IS_OVERFLOW",
				selectedRowIndex);
		boolean isOverFlowFlag = "Y".equals(isOverFlowStr) ? true : false;
		// 状态标识 (状态与其他的标识没有关联)
		String statusStr = (String) tableParm.getData("STATUS",
				selectedRowIndex);
		boolean isStatusFlag = "Y".equals(statusStr) ? true : false;
		// 实际需要设置的标识，作废，溢出值
		String isInSetStr = isInstr;
		String isCancelLationSetStr = isCancelLationStr;
		String isOverFlowSetStr = isOverFlowStr;
		// 状态值
		String isStatusStr = "N";
		// “进入”列选择
		if (selectedColumnIndex == 0) {
			if (isInFalg) {
				// 原来选中现在也将其选中
				isInSetStr = "Y";
			} else {
				isInSetStr = "Y";
			}
			// 其他
			isCancelLationSetStr = "N";
			isOverFlowSetStr = "N";
		}
		// 作废
		if (selectedColumnIndex == 1) {
			if (isCancelLationFlag) {
				// 原来选中现在也将其选中
				isCancelLationSetStr = "Y";
			} else {
				isCancelLationSetStr = "Y";
			}
			// 其他
			isInSetStr = "N";
			isOverFlowSetStr = "N";
		}
		// 溢出
		if (selectedColumnIndex == 2) {
			if (isOverFlowFlag) {
				// 原来选中现在也将其选中
				isOverFlowSetStr = "Y";
			} else {
				isOverFlowSetStr = "Y";
			}
			// 其他
			isInSetStr = "N";
			isCancelLationSetStr = "N";
		}
		// 状态 (注意状态内容与其他标识没有关联关系)
		if (selectedColumnIndex == commitIndex) {
			if (isStatusFlag) {
				isStatusStr = "N";
			} else {
				isStatusStr = "Y";
			}
		}
		// 将值写入Table的Tparm
		if (selectedColumnIndex == 0 || selectedColumnIndex == 1
				|| selectedColumnIndex == 2
				|| selectedColumnIndex == commitIndex) {
			tableParm.setData("IS_IN", selectedRowIndex, isInSetStr);
			tableParm.setData("IS_CANCELLATION", selectedRowIndex,
					isCancelLationSetStr);
			tableParm
					.setData("IS_OVERFLOW", selectedRowIndex, isOverFlowSetStr);
			tableParm.setData("STATUS", selectedRowIndex, isStatusStr);
			table.setParmValue(tableParm);
		}
		/* 加入单选判断 end */
		if(table.getRowCount() == 1){
			table.setSelectedRow(0);
		}
	}

	/**
	 * 将表格的对应单元格设置成可写，其他的设置成不可写
	 * 
	 * @param tableName
	 *            String
	 * @param rowNum
	 *            int
	 * @param columnNum
	 *            int
	 */
	private void setTableEnabled(String tableName, int rowNum, int columnNum) {
		TTable table = (TTable) this.getComponent(tableName);
		int totalColumnMaxLength = table.getColumnCount();
		int totalRowMaxLength = table.getRowCount();
		// 锁列
		String lockColumnStr = "";
		for (int i = 0; i < totalColumnMaxLength; i++) {
			if (!(i + "").equals(columnNum + "")) {
				lockColumnStr += i + ",";
			}
		}
		lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
		table.setLockColumns(lockColumnStr);
		// 锁行
		String lockRowStr = "";
		for (int i = 0; i < totalRowMaxLength; i++) {
			if (!(i + "").equals(rowNum + "")) {
				lockRowStr += i + ",";
			}
		}
		lockRowStr = lockRowStr.substring(0, ((lockRowStr.length() - 1) < 0 ? 0
				: (lockRowStr.length() - 1)));
		if (lockRowStr.length() > 0) {
			table.setLockRows(lockRowStr);
		}

	}

	/**
	 * 将控件值放入TParam方法(可以传入放置参数值)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		objstr = objstr;
		parm.setData(paramName, objstr);
	}

	/**
	 * 将控件值放入TParam方法(放置参数值与控件名相同)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		//System.out.println(objstr);
		objstr = objstr;
		// 参数值与控件名相同
		parm.setData(objName, objstr);
	}

	/**
	 * 将控件值放入TParam方法(可以传入放置参数值)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamLikeWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr + "%";
			parm.setData(paramName, objstr);
		}

	}

	/**
	 * 将控件值放入TParam方法(放置参数值与控件名相同)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamLikeWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr + "%";
			// 参数值与控件名相同
			parm.setData(objName, objstr);
		}
	}

	/**
	 * 用于放置用于完全匹配进行查询的控件
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm) {
		putParamWithObjNameForQuery(objName, parm, objName);
	}

	/**
	 * 用于放置用于完全匹配进行查询的控件
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 * @param paramName
	 *            String
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			// 参数值与控件名相同
			parm.setData(paramName, objstr);
		}
	}

	/**
	 * 检查控件是否为空
	 * 
	 * @param componentName
	 *            String
	 * @return boolean
	 */
	private boolean checkComponentNullOrEmpty(String componentName) {
		if (componentName == null || "".equals(componentName)) {
			return false;
		}
		String valueStr = this.getValueString(componentName);
		if (valueStr == null || "".equals(valueStr)) {
			return false;
		}
		return true;
	}

	/**
	 * 得到指定table的选中行
	 * 
	 * @param tableName
	 *            String
	 * @return int
	 */
	private int getSelectedRow(String tableName) {
		int selectedIndex = -1;
		if (tableName == null || tableName.length() <= 0) {
			return -1;
		}
		Object componentObj = this.getComponent(tableName);
		if (!(componentObj instanceof TTable)) {
			return -1;
		}
		TTable table = (TTable) componentObj;
		selectedIndex = table.getSelectedRow();
		return selectedIndex;
	}

	/**
	 * 数字验证方法
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validNumber(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		//int total = parm.getCount();
		//System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getIP());
	}

	/**
	 * 得到当前时间字符串方法
	 * 
	 * @param dataFormatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateStr(String dataFormatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataFormatStr);
		return datestr;
	}

	/**
	 * 得到当前时间字符串方法
	 * 
	 * @return String
	 */
	private String getCurrentDateStr() {
		return getCurrentDateStr("yyyyMMdd");
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * 拷贝TParm
	 * 
	 * @param from
	 *            TParm
	 * @param to
	 *            TParm
	 * @param row
	 *            int
	 */
	private void cloneTParm(TParm from, TParm to, int row) {
		for (int i = 0; i < from.getNames().length; i++) {
			to.addData(from.getNames()[i], from.getValue(from.getNames()[i],
					row));
		}
	}

	/**
	 * 根据Operator得到map
	 * 
	 * @return Map
	 */
	private Map getBasicOperatorMap() {
		Map map = new HashMap();
		map.put("REGION_CODE", Operator.getRegion());
		map.put("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		map.put("OPT_DATE", datestr);
		map.put("OPT_TERM", Operator.getIP());
		return map;
	}

	/**
	 * 得到当前的时间，年月日时分秒
	 * 
	 * @return String
	 */
	private String getCurrentDateTimeStr() {
		return getCurrentDateTimeStr("yyyyMMddHHmmss");
	}

	/**
	 * 得到当前时间，年月日时分秒
	 * 
	 * @param formatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateTimeStr(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		String dateStr = format.format(new Date());
		return dateStr;
	}

}
