package com.javahis.ui.inv;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTextFieldEvent;
import com.javahis.util.RFIDPrintUtils;

/**
 * <p>
 * Title: �������RFID Control
 * </p>
 * 
 * <p>
 * Description: �������RFID  Control
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
public class INVPrintRfidControl
    extends TControl {

    // �������ݼ���
    private TParm resultParm;

    private TParm parm;

    public INVPrintRfidControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
       

        // ��ʼ��������
        initPage();
    }
    
    private Map<String,String> getRfidMap() {
    	  String sql = " SELECT B.RFID, B.INV_CODE,d.INV_CHN_DESC,d.DESCRIPTION,b.VALID_DATE   " +
          " FROM INV_STOCKDD B" +
          " left join inv_base d on d.inv_code=b.inv_code "+
          " WHERE   d.SEQMAN_FLG='Y' and d.EXPENSIVE_FLG='Y' and b.WAST_FLG='N'";
         TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
         Map<String, String> map=new HashMap<String, String>();
         for (int i = 0; i < parm.getCount("RFID"); i++) {
        	map.put(parm.getValue("RFID", i), parm.getValue("INV_CODE", i)+";;"+parm.getValue("INV_CHN_DESC", i)+";;"+parm.getValue("DESCRIPTION", i)+";;"+parm.getValue("VALID_DATE", i)); 
  		}
         return map;
  	}
    
    
    
    /**
     * ��ӡ����
     */
    public void onPrintBarcode() { 
    	
    	for (int i = 0; i < getTable("TABLE").getRowCount(); i++) {
    		TParm newParm=new TParm();
    		String s= (String) map.get(getTable("TABLE").getItemData(i, "RFID"));
			String[] ssStrings=s.split(";;");
    		newParm.setData(RFIDPrintUtils.PARM_CODE, getTable("TABLE").getItemData(i, "RFID").toString().trim());
    		newParm.setData(RFIDPrintUtils.PARM_NAME,  ssStrings[1]);
    		newParm.setData(RFIDPrintUtils.PARM_PRFID, getTable("TABLE").getItemData(i, "RFID").toString().trim());
    		String cString="";
    		if ( ssStrings[3]!=null&&!ssStrings[3].equals("")) {
    			cString=ssStrings[3];
    			cString=cString.substring(0,10);
			}
    		newParm.setData(RFIDPrintUtils.PARM_VALID_DATE, cString);
    		newParm.setData(RFIDPrintUtils.PARM_SPEC, ssStrings[2]);
    		RFIDPrintUtils.send2LPT(newParm);
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	

    }
    
    private Map map;
    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��TABLE
    	
    	TParm dParm=new TParm();
    	getTextField("BARCODE").grabFocus();
    	callFunction("UI|BARCODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChange1");
    	map=getRfidMap();
    }
    public void onChange1(){
    	if (getValueString("BARCODE")!=null&&getValueString("BARCODE").trim()!=null&&getValueString("BARCODE").trim().length()>0) {
    		if (map.containsKey(getValueString("BARCODE").trim())) {
    			
    			
    			TParm parm= new TParm();
    			parm.setData("RFID", getValueString("BARCODE").trim());
    			getTable("TABLE").addRow(parm);
    			this.clearValue("BARCODE");
			}else {
				messageBox("�����RFID��");
				this.clearValue("BARCODE");
				return;
			}
    		
    		
		}
    }
    public void onSave(){
    	onPrintBarcode();
    	
    }
    
    
    public void onChangeRFID(){
    	//���� rfid  �ӿ�  ɨ��  rfid
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
			messageBox("�����RFID��");
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
     * ���ط���
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        setReturnValue(table.getParmValue());
        this.closeWindow();
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
