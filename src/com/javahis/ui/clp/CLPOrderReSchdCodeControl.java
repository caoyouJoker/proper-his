package com.javahis.ui.clp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.clp.CLPOrderReSchdCodeTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
/**
 * <p>
 * Title: 费用时程修改
 * </p>
 * 
 * <p>
 * Description: 医嘱时程替换操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2014.09.4
 * @version 4.0
 */
public class CLPOrderReSchdCodeControl  extends TControl{
	private TTable tableOrder;
	private TTable table;
	private Pat pat;
	TextFormatCLPDuration combo_schd;
	TextFormatCLPDuration recombo_schd;
	String mrNo;
	String clpFlg;
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		tableOrder=(TTable)this.getComponent("TABLE_ORDER");
		table=(TTable)this.getComponent("TABLE");
		addListener(tableOrder);
		onClear();
		//getInitParam();
		//传参校验--xiongwg20150427 start
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null || obj.equals("")){
			return;
		}
		if (obj != null && !obj.equals("")) {
			initParm = (TParm) obj;
		}
		mrNo = initParm.getData("CLP", "MR_NO").toString();
		if (initParm.getData("CLP", "FLG") != null) {
			clpFlg = initParm.getData("CLP", "FLG").toString();
			if ("Y".equals(clpFlg)) {
				pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
				setValue("MR_NO", pat.getMrNo());
				setValue("PAT_NAME", pat.getName());
				onQuery();
			}

		}
		//传参校验--xiongwg20150427 end
	}
	/**
	 * 初始化
	 */
	public void getInitParam(){
		this.setValue("START_IN_DATE",StringTool.rollDate(SystemTool.getInstance().getDate(), -7));
		this.setValue("END_IN_DATE",SystemTool.getInstance().getDate());
	}
	/**
	 * 清空
	 */
	public void onClear(){
		getInitParam();
		this.clearValue("DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;" +
				"CLNCPATH_CODE;SCHD_CODE;SCHD_CODE;BIL_DATE_FLG;" +
				"START_BILL_DATE;END_BILL_DATE;OPT_USER;IN_DEPT_CODE;" +
				"IN_STATION_CODE;DR_CODE;RE_CLNCPATH_CODE;RE_SCHD_CODE;CBK_SEL");
		callFunction("UI|START_BILL_DATE|setEnabled", false);
		callFunction("UI|END_BILL_DATE|setEnabled", false);
		table.removeRowAll();
		tableOrder.removeRowAll();
	}
	public void onMrNo(){
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
		setValue("MR_NO", pat.getMrNo());
		setValue("PAT_NAME", pat.getName());
		this.onQuery();
	}
	/**
	 * 查询
	 */
	public void onQuery(){
		TParm parm=new TParm();
		if (this.getValue("ADM_STATUS").equals("1")) {
			parm.setData("STATUS"," AND A.DS_DATE IS NULL");
		}else if(this.getValue("ADM_STATUS").equals("2")){
			parm.setData("STATUS"," AND A.DS_DATE IS NOT NULL");
		}
		String startDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("START_IN_DATE")),
				"yyyyMMdd")+"000000";
		String endDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("END_IN_DATE")),
				"yyyyMMdd")+ "235959";
		parm.setData("START_IN_DATE",startDate);
		parm.setData("END_IN_DATE",endDate);
		if (null!=this.getValue("DEPT_CODE") &&this.getValue("DEPT_CODE").toString().length()>0) {
			parm.setData("DEPT_CODE",this.getValue("DEPT_CODE").toString());
		}
		if (null!=this.getValue("STATION_CODE") &&this.getValue("STATION_CODE").toString().length()>0) {
			parm.setData("STATION_CODE",this.getValue("STATION_CODE").toString());
		}
//		if (null!=pat) {
//			parm.setData("MR_NO",pat.getMrNo());
//		}
		if (null!=this.getValue("MR_NO") &&this.getValue("MR_NO").toString().length()>0) {
			parm.setData("MR_NO",this.getValue("MR_NO").toString());
		}
//		if(!clpFlg.equals("") && clpFlg.equals("Y")){
			parm.setData("FLG",clpFlg);
//		}
		TParm result=CLPOrderReSchdCodeTool.getInstance().selectAdmInp(parm);
		if (result.getErrCode()<0) {
			this.messageBox("查询出现错误");
			table.removeRowAll();
			tableOrder.removeRowAll();
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			table.removeRowAll();
			tableOrder.removeRowAll();
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * 增加对TalbeReplace的监听事件
	 */
	public void onTableClicked(){
		int row=table.getSelectedRow();
		if (row<0) {
			return;
		}
		this.setValue("CLNCPATH_CODE", table.getParmValue().getValue("CLNCPATH_CODE",row));
		combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		//combo_schd.setSqlFlg("Y");
        combo_schd.setClncpathCode(table.getParmValue().getValue("CLNCPATH_CODE",row));
        combo_schd.onQuery();
	}
	/**
	 * 右面查询按钮操作
	 */
	public void onOrderQuery(){
		TParm parm=new TParm();
		int row=table.getSelectedRow();
		if (row<0) {
			this.messageBox("请选择需要查询的病患");
			return;
		}
		TParm tableParm=table.getParmValue();
		if (this.getValue("CLNCPATH_CODE").toString().length()>0) {
			parm.setData("CLNCPATH_CODE",this.getValue("CLNCPATH_CODE").toString());
		}
		if (null!=this.getValue("SCHD_CODE") &&this.getValue("SCHD_CODE").toString().length()>0) {
			parm.setData("SCHD_CODE",this.getValue("SCHD_CODE").toString());
		}
		if (null!=this.getValue("EXEC_DEPT_CODE")&&this.getValue("EXEC_DEPT_CODE").toString().length()>0) {
			parm.setData("EXEC_DEPT_CODE",this.getValue("EXEC_DEPT_CODE").toString());
		}
		if (null!=this.getValue("OPT_USER") &&this.getValue("OPT_USER").toString().length()>0) {
			parm.setData("OPT_USER",this.getValue("OPT_USER").toString());
		}
		if (null!=this.getValue("IN_DEPT_CODE") &&this.getValue("IN_DEPT_CODE").toString().length()>0) {
			parm.setData("IN_DEPT_CODE",this.getValue("IN_DEPT_CODE").toString());
		}
		if (null!=this.getValue("IN_STATION_CODE")&&this.getValue("IN_STATION_CODE").toString().length()>0) {
			parm.setData("IN_STATION_CODE",this.getValue("IN_STATION_CODE").toString());
		}
		if (null!=this.getValue("DR_CODE")&&this.getValue("DR_CODE").toString().length()>0) {
			parm.setData("DR_CODE",this.getValue("DR_CODE").toString());
		}
		parm.setData("CASE_NO",tableParm.getValue("CASE_NO",row));
		if (((TCheckBox)this.getComponent("BIL_DATE_FLG")).isSelected()) {
			if (null==this.getValue("START_BILL_DATE")||
					this.getValue("START_BILL_DATE").toString().length()<=0) {
				this.messageBox("计费开始时间不能为空");
				return;
			}
			if (null==this.getValue("END_BILL_DATE")||
					this.getValue("END_BILL_DATE").toString().length()<=0) {
				this.messageBox("计费结束时间不能为空");
				return;
			}
			String startDate =StringTool.getString(
					TCM_Transform.getTimestamp(this.getValue("START_BILL_DATE")),
					"yyyyMMddHHmmss");
			String endDate = StringTool.getString(
					TCM_Transform.getTimestamp(this.getValue("END_BILL_DATE")),
					"yyyyMMddHHmmss");
			parm.setData("START_BILL_DATE",startDate);
			parm.setData("END_BILL_DATE",endDate);
		}
		TParm result=CLPOrderReSchdCodeTool.getInstance().queryIbsOrder(parm);
		if (result.getErrCode()<0) {
			this.messageBox("查询出现错误");
			tableOrder.removeRowAll();
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			tableOrder.removeRowAll();
			return;
		}
		tableOrder.setParmValue(result);
	}
	/**
	 * 临床路径项目控件选中事件
	 */
	public void onClncpathCode(){
		combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		//combo_schd.setSqlFlg("Y");
        combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_schd.onQuery();
	}
	public void onRgClncpathCode(){
		recombo_schd = (TextFormatCLPDuration) this.getComponent("RE_SCHD_CODE");
		//combo_schd.setSqlFlg("Y");
		recombo_schd.setClncpathCode(this.getValueString("RE_CLNCPATH_CODE"));
		recombo_schd.onQuery();
	}
	/**
	 * 全选
	 */
	public void onSel(){
		TParm tableParm=tableOrder.getParmValue();
		if (((TCheckBox)this.getComponent("CBK_SEL")).isSelected()) {//全选
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i,"Y");
			}
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i,"N");
			}
		}
		tableOrder.setParmValue(tableParm);	
	}
	/**
	 * 替换操作
	 */
	public void onSave(){
		tableOrder.acceptText();
		TParm tableParm=tableOrder.getParmValue();
		if (tableParm.getCount()<0) {
			this.messageBox("没有需要操作的数据");
			return;
		}
		TParm parm=new TParm();
		if (this.getValue("RE_CLNCPATH_CODE").toString().length()>0) {
			parm.setData("RE_CLNCPATH_CODE",this.getValue("RE_CLNCPATH_CODE").toString());
			if (this.getValue("RE_SCHD_CODE").toString().length()<=0) {
				this.messageBox("路径修改操作,时程也需要修改");
				return;
			}
		}
		if (this.getValue("RE_SCHD_CODE").toString().length()>0) {
			parm.setData("RE_SCHD_CODE",this.getValue("RE_SCHD_CODE").toString());
		}
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("FLG",i).equals("Y")) {
				parm.addRowData(tableParm, i);
			}
		}
		if (parm.getCount("FLG")<=0) {
			this.messageBox("请选择需要操作的数据");
			return;
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPOrderReSchdCodeAction", "onSave", parm);
		if (result.getErrCode()<0) {
			this.messageBox("替换失败");
			return;
		}
		this.messageBox("替换成功");
		onOrderQuery();
	}
	/**
	 * 右面复选框事件
	 */
	public void onChk(){
		if (((TCheckBox)this.getComponent("BIL_DATE_FLG")).isSelected()) {
			this.callFunction("UI|START_BILL_DATE|setEnabled", true);
			this.callFunction("UI|END_BILL_DATE|setEnabled", true);
		}else{
			this.callFunction("UI|START_BILL_DATE|setEnabled", false);
			this.callFunction("UI|END_BILL_DATE|setEnabled", false);
		}
		this.setValue("START_BILL_DATE", "");
		this.setValue("END_BILL_DATE", "");
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = tableOrder.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = tableOrder.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
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
		tableOrder.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
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
}
