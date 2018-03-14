package com.javahis.ui.ins;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
/**
 * 
 * <p>
 * Title:病患信息查询
 * </p>
 * 
 * <p>
 * Description:病患信息查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2012-1-31
 * @version 2.0
 */
public class INSPatInfoControl extends TControl{
	private String idNo;//身份证号
	private int selectrow = -1;//选择的行
	public void onInit() {
		super.onInit();
		//得到前台传来的数据并显示在界面上
		TParm recptype = (TParm) getParameter();
		idNo=recptype.getValue("IDNO");
		TParm parm=new TParm();
		   //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
		parm.setData("IDNO", idNo);// 身份证号码
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		this.callFunction("UI|TABLE|setParmValue", result);
	}
	 /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(row));
        this.callFunction("UI|onClose");
    }
    /**
    *
    */
   public void onOK() {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue(data.getRow(selectrow));
       this.callFunction("UI|onClose");
   }
}
