package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTextArea;

/**
 * <p>
 * Title: 版本更新记录
 * </p>
 * 
 * <p>
 * Description: 版本更新记录
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHIS
 * </p>
 * 
 * @author wukai 2017.2.14
 * @version 1.0
 */
public class SYSVersionControl extends TControl {

	public SYSVersionControl() {

	}

	@Override
	public void onInit() {
		super.onInit();
		readFile();

	}

	

	/**
	 * 获取TextArea
	 * 
	 * @param tag
	 * @return
	 */
	public TTextArea getTTextArea(String tag) {
		return (TTextArea) this.getComponent(tag);
	}
	
	/**
	 * 刷新，从新从文件读取数据
	 */
	public void onReresh() {
		//this.messageBox("rr");
		readFile();
	}
	
	private void readFile() {
		StringBuilder sb = new StringBuilder("");
		byte[] res = TIOM_AppServer.readFile("%ROOT%\\WEB-INF\\config\\system\\version.txt");
		if(res == null) {
			sb.append("无版本记录！");
		} else {
			sb.append(new String(res));
		}

		this.getTTextArea("VERSION_TEXT").setText(sb.toString());
	}

}
