package com.javahis.ui.clp;

import jdo.clp.CLPSingleDiseTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: 历次单病种 选择窗口 </p>
 * 
 * <p> Description: 历次单病种 选择窗口  </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPPatInfoUIControl extends TControl {
	private static String TABLE = "PAT_TABLE";
	private TTable patTable;
	private String mrNo;
	private TParm returnParm;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		TParm parm = new TParm();
		if (obj != null) {
			parm = (TParm) obj;
			mrNo = parm.getValue("MR_NO");
			if(mrNo.trim().equals("")){
				messageBox("E0024");//初始化参数失败
				return;
			}
		}else{
			messageBox("E0024");
			return;
		}
		TParm result = CLPSingleDiseTool.getInstance().queryEMRHistoryForMerge(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("初始化历次病患信息出错：" + result.getErrText());
		} else {
			this.callFunction("UI|" + TABLE + "|setParmValue", result);
		}
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");// 注册Table点击事件
		patTable = (TTable) this.getComponent(TABLE);
	}

	/**
	 * TABLE单击事件
	 * @param row int
	 */
	public void onTableClicked(int row) {
		returnParm = patTable.getParmValue().getRow(row);
	}

	/**
	 * 合并
	 */
	public void onMerge() {
		if (patTable.getSelectedRow() < 0) {
			messageBox("请选择一条记录");
			return;
		}
		if(returnParm.getBoolean("MERGE_FLG")){
		    messageBox("不能重复合并");
		    return;
		}
		this.setReturnValue(returnParm);
		this.closeWindow();
	}

	/**
	 * 取消
	 */
	public void onCancel() {
		this.closeWindow();
	}

}
