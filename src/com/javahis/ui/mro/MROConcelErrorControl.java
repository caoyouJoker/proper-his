package com.javahis.ui.mro;

import jdo.mro.MRORecordTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;

public class MROConcelErrorControl extends TControl{
	public void onInit() {
        super.onInit();
//        this.setValue("REGION_CODE", Operator.getRegion());
//		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
//		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
//				this.getValueString("REGION_CODE")));
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
    }
	/**
	 * 一键平
	 */
	public void AkeyPing(){
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("请输入出院日期");
			return;
		}
		StringBuffer bf = new StringBuffer();
		bf.append("SELECT CASE_NO FROM MRO_RECORD ");
//		if (this.getValue("REGION_CODE").toString().length() > 0) {
//			bf.append(" AND REGION_CODE ='").append(
//					this.getValue("REGION_CODE").toString()).append("'");
//		}
		bf.append(" WHERE OUT_DATE BETWEEN TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_START"), true)).append(
				"','YYYYMMDDHH24MISS') AND TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_END"), false)).append(
				"','YYYYMMDDHH24MISS')");
		TParm result = new TParm(TJDODBTool.getInstance().select(bf.toString()));
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		}	
		TParm temp=null;
		for (int i = 0; i < result.getCount(); i++) {
			temp = MRORecordTool.getInstance().updateMROIbsForIBS(result.getValue("CASE_NO",i));
			if (temp.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}	
		}
		this.messageBox("P0001");
	}
	

}
