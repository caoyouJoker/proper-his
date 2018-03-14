package com.javahis.ui.emr;

import com.dongyang.control.TControl;

import com.dongyang.tui.text.ECapture;
import com.dongyang.ui.TWord;
import com.dongyang.wcomponent.util.TiString;


/**
 *
 * @author whaosoft
 *
 */
public class EMRECaptureDialogControl extends TControl {

	/**  */
	private TWord word = null;

	/**
	 * 初始化
	 */
	public void onInit() {

		word = (TWord) getParameter();
	}

	/**
	 * 确定
	 */
	public void onSave() {

		if (!this.doCheck())
			return;

		//
		this.closeWindow();
	}

	/**
	 *
	 * @return
	 */
	private boolean doCheck() {

		String ecName = (String) this.getValue("NAME");

		ECapture ec = word.findCapture(ecName);

		if( TiString.isEmpty(ecName) ){
			this.messageBox("控件名称不可以为空!");
			return false;
		}
		else if( null!=ec ){
			this.messageBox("以'"+ecName+"'命名的抓取控件已存在!");
			return false;
		}else{
	        this.addECapture(ecName);
			return true;
		}
	}

	/**
	 *
	 * @param ecName
	 */
	private void addECapture( String ecName ){

        ECapture obj2 = word.insertCaptureObject();
        ECapture obj1  =word.insertCaptureObject();
        obj2.setCaptureType(1);

        //
        obj1.setName(ecName);
        obj2.setName(ecName);
	}

}
