package com.javahis.ui.ins;

import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 门特定点治疗机构信息下载</p>
 *
 * <p>Description: 门特定点治疗机构信息下载</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author pangben  2012.04.21
 * @version 4.0
 */
public class INSMTInfoDownLoadControl  extends TControl{
	private TTable table;
	 /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
    }
    /**
     * 信息下载
     */
    public void onDownload(){
    	if (!this.emptyTextCheck("NHI_HOSP_NO,DOWNLOAD_TYPE")) {
			return;
		}
    	if (this.getValueString("TYPE").length()<=0) {
			this.messageBox("病种类型不可以为空");
			return;
		}
    	TParm parm=new TParm();
    	String [] name={"NHI_HOSP_NO","NHI_HOSP_CLASS","TYPE","CENTER_NO","DOWNLOAD_TYPE"};
    	for (int i = 0; i < name.length; i++) {
    		parm.addData(name[i], this.getValue(name[i]));
		}
    	parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "Z");
		parm.addData("PARM_COUNT", 5);
		TParm result = InsManager.getInstance().safe(parm, "");
		if (null==result || null==result.getValue("PROGRAM_STATE",0) || result.getInt("PROGRAM_STATE",0)<0) {
			this.messageBox(result.getValue("PROGRAM_MESSAGE",0));
			return;
		}
		result.setCount(result.getCount("NHI_HOSP_NO"));
		table.setParmValue(result);
    }
}
