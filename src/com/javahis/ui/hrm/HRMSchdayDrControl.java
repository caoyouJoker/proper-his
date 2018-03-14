package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMSchdayDr;
import jdo.reg.SessionTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: 健康检查日班表控制类 </p>
 *
 * <p> Description: 健康检查日班表控制类 </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMSchdayDrControl extends TControl {
	//日班表对象
	private HRMSchdayDr sch;
	//TABLE
	private TTable table;
	//AREA_COMBO,ROOM_COMBO
	private TTextFormat areaCombo,roomCombo;
	//工具类
	private StringUtil util;
	/**
	 * 初始化事件
	 */
	public void onInit() {
		super.onInit();
		//初始化控件
		onInitComponent();
		//初始化数据
		onInitData();
		//清空
		onClear();
	}
	/**
	 * 初始化控件
	 */
	private void onInitComponent(){
		table=(TTable)this.getComponent("TABLE");
		//合同细相TABLE值改变事件
		table.addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE, this,
		"onTabValueChanged");

	}

	/**
	 * 初始化数据
	 */
	private void onInitData(){
		sch=new HRMSchdayDr();
		if(!sch.onQuery()){
			this.messageBox_("初始化失败");
		}
		table.setDataStore(sch);
		table.setDSValue();
	}

	/**
	 * 清空事件
	 */
	public void onClear(){
		sch.onQuery();
		int row = sch.insertRow();
		sch.setItem(row, "OPT_USER", Operator.getID());
		sch.setItem(row, "OPT_TERM", Operator.getIP());
		sch.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		sch.setActive(row, false);
		table.setDataStore(sch);
		table.setDSValue();
		this.setValue("REGION_CODE", Operator.getRegion());
//		this.setValue("ADM_DATE", sch.getDBTime());
//		this.setValue("SESSION_CODE", SessionTool.getInstance().getDrSessionNow("O"));
		this.setValue("CLINIC_AREA", "");
		this.setValue("CLINIC_ROOM", "");
		this.setValue("DEPT_CODE", "");
		this.setValue("OPERATOR", "");
	}
	/**
	 * 查询事件
	 */
	public void onQuery(){
		String regionCode=this.getValueString("REGION_CODE");
		Timestamp adm=(Timestamp)this.getValue("ADM_DATE");
//		String admDate=StringTool.getString(adm,"yyyyMMdd");
//		String sessionCode=this.getValueString("SESSION_CODE");
		String clinicArea=this.getValueString("CLINIC_AREA");
		String clinicRoom=this.getValueString("CLINIC_ROOM");
		String deptCode=this.getValueString("DEPT_CODE");
		String drCode=this.getValueString("OPERATOR");
		String filter="";
		if(!StringUtil.isNullString(regionCode)){
			filter+="REGION_CODE='" +regionCode+"' ";
		}
//		if(!StringUtil.isNullString(admDate)){
//			filter+="AND ADM_DATE='" +admDate+"' ";
//		}
//		if(!StringUtil.isNullString(sessionCode)){
//			filter+="AND SESSION_CODE='" +sessionCode+"' ";
//		}
		if(!StringUtil.isNullString(clinicArea)){
			filter+="AND CLINIC_AREA='" +clinicArea+"' ";
		}
		if(!StringUtil.isNullString(clinicRoom)){
			filter+="AND CLINICROOM_NO='" +clinicRoom+"' ";
		}
		if(!StringUtil.isNullString(deptCode)){
			filter+="AND DEPT_CODE='" +deptCode+"' ";
		}
		if(!StringUtil.isNullString(drCode)){
			filter+="AND DR_CODE='" +drCode+"'";
		}
		if(filter.indexOf("AND")==0){
			filter=filter.replaceFirst("AND", "");
		}
		sch.setFilter(filter);
		sch.filter();
		if(sch.rowCount()<=0){
			this.messageBox_("没有数据");
			onClear();
			return;
		}
		table.setDSValue();
	}
	/**
	 * 保存事件
	 */
	public void onSave(){
                table.acceptText();
		if(sch==null){
			return;
		}
		if(!sch.isModified()){
			this.messageBox_("没有数据可保存");
			return;
		}

		String[] sql=sch.getUpdateSQL();
		TParm parm=new TParm(TJDODBTool.getInstance().update(sql));
		if(parm.getErrCode()!=0){
//			this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		sch.resetModify();
		onClear();
	}
	/**
	 * 删除事件
	 */
	public void onDelete(){
		if(sch==null){
			return;
		}
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox_("没有数据可删除");
			return;
		}
		sch.deleteRow(row);
		onSave();
	}
	/**
	 * 员工TABLE值改变事件
	 * @param tNode
	 * @return
	 */
	public boolean onTabValueChanged(TTableNode tNode){
		int row=table.getSelectedRow();
		int column=table.getSelectedColumn();
		String colName=table.getParmMap(column);
//		String oldDate=sch.getItemString(row, "ADM_DATE");
//		if(!StringUtil.isNullString(oldDate)){
//			Timestamp oldTime=StringTool.getTimestamp(oldDate,"yyyyMMdd");
//			if(StringTool.getDateDiffer(oldTime, sch.getDBTime())==-1){
//				this.messageBox_("不能修改先前记录");
//				return true;
//			}
//		}

		table.acceptText();
//		if("SESSION_CODE".equalsIgnoreCase(colName)){
//			int value=TypeTool.getInt(tNode.getValue());
//			int session=StringTool.getInt(SessionTool.getInstance().getDrSessionNow("O"));
//			if(value<session){
//				this.messageBox_("不能修改先前记录");
//				return true;
//			}
//		}
		if("DR_CODE".equalsIgnoreCase(colName)){
			String value=tNode.getValue()+"";
			if(StringUtil.isNullString(value)){
				return false;
			}
			sch.setActive(row,true);
		}
		if("DEPT_ATTRIBUTE".equalsIgnoreCase(colName)){

			int lastRow;
			if(row==sch.rowCount()-1){
				lastRow=sch.insertRow();
				sch.setItem(lastRow,"OPT_USER",Operator.getID());
				sch.setItem(lastRow, "OPT_DATE", sch.getDBTime());
				sch.setItem(lastRow, "OPT_TERM", Operator.getIP());
				sch.setActive(lastRow,false);
			}
			table.setDSValue();
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			table.setSelectedColumn(column);
		}
		return false;
	}

}
