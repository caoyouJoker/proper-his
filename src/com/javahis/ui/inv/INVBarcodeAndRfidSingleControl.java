package com.javahis.ui.inv;

import jdo.inv.INVSQL;
import jdo.inv.InvPurorderMTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTextFieldEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 绑定条码和RFID Control
 * </p>
 *
 * <p>
 * Description: 绑定条码和RFID  Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lit 2013.4.19
 * @version 1.0
 */
public class INVBarcodeAndRfidSingleControl
    extends TControl {

    // 返回数据集合
    private TParm resultParm;

    private TParm parm;
    

    public INVBarcodeAndRfidSingleControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 取得传入参数
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
        }

        // 初始画面数据
        initPage();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化TABLE
    	
    	TParm dParm=new TParm();
    	for (int i = 0; i <10; i++) {
    		dParm.addData("INVSEQ_NO", i+1);
    		dParm.addData("INV_CHN_DESC", "支架");
    		dParm.addData("ORGIN_CODE", "");
    		dParm.addData("RFID", "80.10.1203121000000"+i);
		}
    	getTable("TABLE").setParmValue(dParm);
    	getTextField("rfid").setValue(dParm.getData("RFID",0).toString());
    	getTextField("barcode").grabFocus();
    	callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onChange");
    }
    public void onChange(){
    	//调用 rfid  接口  扫描  rfid
    	
    	TParm dParm=getTable("TABLE").getParmValue();
    	System.out.println(dParm.getCount());
    
    	
    	int d=0;
    	for (int i = 0; i < dParm.getCount(); i++) {
    		String cString=(String)dParm.getData("RFID", i);
    		if (cString.equals(getValueString("rfid"))) {
    			dParm.setData("ORGIN_CODE",i, getValueString("barcode"));
    			getTable("TABLE").setParmValue(dParm);
    			d=i;
				break;
			}
		}
    	
    	getTextField("rfid").setValue(dParm.getData("RFID", d+1).toString());
    	TParm tParm=new TParm();
  		tParm.setData("RFID","TEXT" , getValueString("rfid"));
  		tParm.setData("CODE","TEXT" , getValueString("barcode"));
  		this.openPrintDialog("%ROOT%\\config\\prt\\inv\\InvBarcode.jhw", tParm,true);
    	
    	
    	this.clearValue("barcode");
    	
    	
    }






    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
