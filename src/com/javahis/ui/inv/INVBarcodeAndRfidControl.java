package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTextFieldEvent;

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
public class INVBarcodeAndRfidControl
    extends TControl {

    // 返回数据集合
    private TParm resultParm;

    private TParm parm;

    public INVBarcodeAndRfidControl() {
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
    	
    	for (int i = 0; i <parm.getCount("RFID"); i++) {
    		dParm.addData("INVSEQ_NO", i+1);
    		dParm.addData("INV_CHN_DESC", parm.getValue("INV_CHN_DESC", i));
    		dParm.addData("ORGIN_CODE",parm.getValue("ORGIN_CODE", i));
    		dParm.addData("RFID", parm.getValue("RFID", i));
		}
    	getTable("TABLE").setParmValue(dParm);
    	//getTextField("BARCODE").grabFocus();
    	callFunction("UI|BARCODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChange1");
    	callFunction("UI|RFID|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChangeRFID");
    	//getTextField("RFID").grabFocus();
    }
    public void onChange1(){
    	if (getValueString("BARCODE")!=null&&getValueString("BARCODE").trim()!=null&&getValueString("BARCODE").trim().length()>0) {
    		getTextField("BARCODE").setEditable(false);
        	getTextField("RFID").grabFocus();
		}
    }
    public void onSave(){
    	TParm c=new TParm();
    	TTable table=getTable("TABLE");
    	for (int i = 0; i <table.getRowCount(); i++) {
    		c.addData("RFID", table.getItemString(i,"RFID").trim());
    		c.addData("ORGIN_CODE", table.getItemString(i,"ORGIN_CODE").trim());
    		if ("".equals(table.getItemString(i,"ORGIN_CODE"))||"".equals( table.getItemString(i,"ORGIN_CODE").trim())) {
    			messageBox("第"+(i+1)+"条数据没有赋码，请重新赋码");
    			return;
				
			}
    		//continue;
			
		}
    	System.out.println("****************"+c);
       TParm result = TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onUpdateBarCode", c);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    	
    }
    
    
    public void onChangeRFID(){
    	//调用 rfid  接口  扫描  rfid
    	TParm dParm=getTable("TABLE").getParmValue();
    	boolean flg=false;
    	for (int i = 0; i < dParm.getCount("RFID"); i++) {
    		String cString=dParm.getData("RFID", i).toString().trim();
    		if (cString.equals(getValueString("RFID").trim())) {
    			getTable("TABLE").setItem(i,"ORGIN_CODE", getValueString("BARCODE"));
    			flg=true;
				break;
			}
		}
    	if (flg==false) {
			messageBox("错误的RFID号");
			this.clearValue("BARCODE");
	    	this.clearValue("RFID");
	    	getTextField("BARCODE").setEditable(true);
	    	getTextField("BARCODE").grabFocus();
			return;
		}
    	this.clearValue("BARCODE");
    	this.clearValue("RFID");
    	getTextField("BARCODE").setEditable(true);
    	getTextField("BARCODE").grabFocus();
    }






    /**
     * 传回方法
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        setReturnValue(table.getParmValue());
        this.closeWindow();
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
