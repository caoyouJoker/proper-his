package com.javahis.ui.clp;

import jdo.clp.CLPOrderReplaceTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.system.textFormat.TextFormatCLPDuration;

/**
 * <p>
 * Title: 路径医嘱替换
 * </p>
 * 
 * <p>
 * Description: 临床路径医嘱替换操作
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
 * @author pangben 2014.08.28
 * @version 4.0
 */
public class CLPOrderReplaceControl extends TControl {
	private TTable tableReplace;
	private TTable tableClpPack;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		this.setValue("CLNCPATH_CODE", "");
		// 只有text有这个方法，调用sys_fee弹出框
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// 接受回传值
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		getInitParam();
	}
	/**
	 * 费用代码下拉列表选择
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
	}
	/**
	 * 接受参数
	 */
	private void getInitParam(){
		tableReplace=(TTable) this.getComponent("TABLE_REPLACE");
		tableClpPack=(TTable) this.getComponent("TABLE_CLP_PACK");
		((TTable) getComponent("TABLE_CLP_PACK")).addEventListener(
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
	}
	/**
	 * 增加对TalbeReplace的监听事件
	 */
	public void onTableClicked(){
		int row=tableReplace.getSelectedRow();
		if (row<0) {
			return;
		}
		TParm parm=tableReplace.getParmValue();
		TParm temp=parm.getRow(row);
		temp.setData("ORDER_CODE",temp.getValue("ORDER_CODE_OLD"));
		if (null!=this.getValue("CLNCPATH_CODE")&& this.getValue("CLNCPATH_CODE").toString().length()>0) {
			temp.setData("CLNCPATH_CODE",this.getValue("CLNCPATH_CODE"));
		}
		if (null!=this.getValue("SCHD_CODE") &&this.getValue("SCHD_CODE").toString().length()>0) {
			temp.setData("SCHD_CODE",this.getValue("SCHD_CODE"));
		}
		TParm result=CLPOrderReplaceTool.getInstance().selectClpPack(temp);
		if (result.getErrCode()<0) {
			this.messageBox("查询出现错误");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			tableClpPack.removeRowAll();
			return;
		}
		TParm parmValue=new TParm();
		for (int i = 0; i < result.getCount(); i++) {
			parmValue.addData("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE",i));
			parmValue.addData("SCHD_CODE", result.getValue("SCHD_CODE",i));
			parmValue.addData("ORDER_TYPE", result.getValue("ORDER_TYPE",i));
			parmValue.addData("ORDER_CODE_OLD", result.getValue("ORDER_CODE",i));
			parmValue.addData("ORDER_DESC_OLD", temp.getValue("ORDER_DESC_OLD"));
			parmValue.addData("CHKTYPE_CODE", result.getValue("CHKTYPE_CODE",i));
			parmValue.addData("ORDER_SEQ_NO", result.getValue("ORDER_SEQ_NO",i));
			parmValue.addData("MEDI_QTY_OLD", result.getDouble("DOSE",i));
			parmValue.addData("MEDI_UNIT_OLD", result.getValue("DOSE_UNIT",i));
			parmValue.addData("ROUTE_CODE_OLD", result.getValue("ROUT_CODE",i));
			parmValue.addData("CLP_STATUS", result.getValue("CLP_STATUS",i));
//			parmValue.addData("ORDER_CODE", temp.getValue("ORDER_CODE"));
			parmValue.addData("ORDER_CODE", parm.getValue("ORDER_CODE",row));
			parmValue.addData("ORDER_DESC", temp.getValue("ORDER_DESC"));
			parmValue.addData("MEDI_QTY",temp.getDouble("MEDI_QTY")*result.getDouble("DOSE",i)/
					temp.getDouble("MEDI_QTY_OLD"));//新医嘱用量 根据比例计算  字典中新医嘱的用量*旧医嘱的用量/字典旧医嘱的用量
			parmValue.addData("UNIT_CODE", temp.getValue("MEDI_UNIT"));
			parmValue.addData("ROUTE_CODE", temp.getValue("ROUTE_CODE"));
			parmValue.addData("VERSION", result.getValue("VERSION",i));
			parmValue.addData("FREQ_CODE",result.getValue("FREQ_CODE",i));
			parmValue.addData("DOSE_DAYS",result.getValue("DOSE_DAYS",i));
			parmValue.addData("NOTE",result.getValue("NOTE",i));
			parmValue.addData("ORDER_FLG",result.getValue("ORDER_FLG",i));
			parmValue.addData("SEQ",result.getValue("SEQ",i));
			parmValue.addData("RBORDER_DEPT_CODE",result.getValue("RBORDER_DEPT_CODE",i));
			parmValue.addData("URGENT_FLG",result.getValue("URGENT_FLG",i));
			parmValue.addData("CHKUSER_CODE",result.getValue("CHKUSER_CODE",i));
			parmValue.addData("EXEC_FLG",result.getValue("EXEC_FLG",i));
			parmValue.addData("ORDTYPE_CODE",result.getValue("ORDTYPE_CODE",i));
			parmValue.addData("STANDARD",result.getValue("STANDARD",i));
			parmValue.addData("OWN_PRICE",temp.getDouble("OWN_PRICE"));
			parmValue.addData("START_DAY",result.getValue("START_DAY",i));
			parmValue.addData("FLG", "N");
		}
		parmValue.setCount(parmValue.getCount("CLNCPATH_CODE"));
		System.out.println("======+++++======parmValue parmValue is ::"+parmValue);
		tableClpPack.setParmValue(parmValue);
	}
	/**
	 * 查询功能
	 */
	public void onQuery(){
		this.onQueryOrder();
	}
	/**
	 * 查询按钮
	 */
	public void onQueryOrder(){
		TParm parm=new TParm();
		if (this.getValue("ORDER_CODE").toString().length()>0) {
			parm.setData("ORDER_CODE_OLD",this.getValue("ORDER_CODE"));
		}
		parm.setData("PACK_CODE","03");//01:医生套餐,02:护士套餐,03:临床路径套餐
		TParm result=CLPOrderReplaceTool.getInstance().selectComorderReplace(parm);
		if (result.getErrCode()<0) {
			this.messageBox("查询出现错误");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			tableReplace.removeRowAll();
			return;
		}
		tableReplace.setParmValue(result);
	}
	/**
	 * 保存操作
	 */
	public void onSave(){
		tableClpPack.acceptText();
		TParm tableParm=tableClpPack.getParmValue();
		TParm parm=new TParm();
		int index=0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("FLG",i).equals("Y")) {//勾选
				parm.addRowData(tableParm, i);
				index++;
			}
		}
		if (index<=0) {
			this.messageBox("没有需要操作的数据");
			return;
		}
		parm.setCount(index);
		parm.setData("OPT_USER_T",Operator.getID());
		parm.setData("OPT_TERM_T",Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPOrderReplaceAction", "onSave", parm);
		if (result.getErrCode()<0) {
			this.messageBox("保存失败");
			System.out.println("保存失败:"+result.getErrText());
			return;
		}
		this.messageBox("保存成功");
		this.onTableClicked();
	}
	/**
	 * 临床路径控件触发事件
	 */
	public void onClickClncpathCode() {
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
				.getComponent("SCHD_CODE");
		if (this.getValue("CLNCPATH_CODE").toString().length() > 0) {
			combo_schd.setClncpathCode(this.getValue("CLNCPATH_CODE")
					.toString());
		}
		combo_schd.onQuery();
	}
	/**
	 * 全选按钮
	 */
	public void onSelAll(){
		TParm tableParm=tableClpPack.getParmValue();
		if (((TCheckBox)this.getComponent("CHK_ALL")).isSelected()) {//全选
			for (int i = 0; i < tableParm.getCount(); i++) {
				if (tableParm.getValue("CLP_STATUS",i).length()<=0||
						tableParm.getValue("CLP_STATUS",i).equals("1")) {//修订状态不可以修改	
				}else{
					tableParm.setData("FLG",i,"Y");
				}
			}
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i,"N");
			}
		}
		tableClpPack.setParmValue(tableParm);	
	}
	/**
	 * 勾选复选框操作
	 */
	public void onTableComponent(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		//TParm tableParm = table.getParmValue();
		int col = tableClpPack.getSelectedColumn();
		int row = tableClpPack.getSelectedRow();
		String columnName = tableClpPack.getDataStoreColumnName(col);
		if (columnName.equals("FLG")&&table.getParmValue().getValue("FLG", row).equals("Y")) {
			if (table.getParmValue().getValue("CLP_STATUS",row).length()>0
					&&!table.getParmValue().getValue("CLP_STATUS",row).equals("2")) {
				this.messageBox("此路径项目在修订中,不可以修改医嘱");
				tableClpPack.getParmValue().setData("FLG",row,"N");
				tableClpPack.setParmValue(tableClpPack.getParmValue());
				return;
			}
		}
	}
	public void onExe(){
		TParm result=new TParm(TJDODBTool.getInstance().select("javahisLSJ", "SELECT * FROM SYMBOL_DICT"));
//		System.out.println("result::::"+result);
		this.messageBox_(result);
	}
	public void onClear(){
		this.setValue("CLNCPATH_CODE", "");
		this.setValue("ORDER_CODE", "");
		this.setValue("SCHD_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("CHK_ALL", false);
		tableClpPack.removeRowAll();
		tableReplace.removeRowAll();
	}
}
