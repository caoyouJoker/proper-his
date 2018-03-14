package com.javahis.ui.sta;

import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;

public class STASelYearControl extends TControl {

	public STASelYearControl() {

	}

	public void onInit() {
		super.onInit();
		TComboBox year = (TComboBox) getComponent("YEAR");
		String[] data = new String[20];
		for (int i = 2010; i < 2030; i++) {
			data[i - 2010] = i+"";
		}
		year.setData(data);
	}

	public void onCancel() {
		this.closeWindow();
	}

	public void onOK() {
		String year = this.getValueString("YEAR");
		this.setReturnValue(year);
		this.closeWindow();
	}
}
