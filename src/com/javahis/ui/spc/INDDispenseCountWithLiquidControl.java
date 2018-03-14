package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title:住院药房普通药品统药
 * </p>
 * 
 * <p>
 * Description: 住院药房普通药品统药
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liuzhen 20121015
 * @version 1.0
 */
public class INDDispenseCountWithLiquidControl extends TControl {
	// 未取药
	TTable table_n ;

	// 已取药
	TTable table_y ;
	
	//统药单
	private TTextField bill_no;
	
	//病区
	private TTextField area;
	
	//周转箱
	private TTextField trunk_no;
	
	//货位电子标签
	private TTextField elc_label;
	
	//已取药table parm
	private TParm table_y_parm= new TParm();

	
	/**初始化方法*/
	public void onInit() {

		table_n = (TTable) getComponent("TABLE_N");
		table_y = (TTable) getComponent("TABLE_Y");
		
		bill_no = (TTextField) getComponent("BILL_NO");
		area = (TTextField) getComponent("AREA");
        trunk_no = (TTextField) getComponent("TRUNK_NO");
        elc_label = (TTextField) getComponent("ELC_LABEL");
        
/*        //bill_no获得焦点
		TFrame tFrame = (TFrame) getComponent("UI");
		final TTextField mrField = (TTextField) getComponent("BILL_NO");
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				mrField.requestFocus();
			}
		});*/
		
	}

	/**统药单回车事件*/
	public void onBillEnter() {
		TParm parm = fillNParm(new TParm());
		table_n.setParmValue(parm);
		bill_no.setText("00001");
		bill_no.setEditable(false);
		trunk_no.grabFocus();
		area.setText("7病区");
	}
	
	/**周转箱回车事件*/
	public void onTrunkEnter() {
		trunk_no.setText("000020");
		elc_label.grabFocus();
	}
	
	/**药品名称回车事件*/
	public void onContainerEnter() {
		String a = getValueString("ELC_LABEL");
		elc_label.setText(a);
		int count = table_n.getParmValue().getCount();

		for (int i = 0; i < count; i++) {
			//TRUNK_NO;MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
			TParm parm = table_n.getParmValue().getRow(i);
			
			table_y_parm.addData("TRUNK_NO", trunk_no.getText());
			
			table_y_parm.addData("MED_NAME", parm.getValue("MED_NAME"));
			table_y_parm.addData("SPEC", parm.getValue("SPEC"));
			table_y_parm.addData("BETCH_NO", parm.getValue("BETCH_NO"));
			table_y_parm.addData("VALID_DATE", parm.getValue("VALID_DATE"));
			table_y_parm.addData("AMOUNT", parm.getValue("AMOUNT"));
			table_y_parm.addData("UNIT", parm.getValue("UNIT"));
			table_y_parm.addData("PRICE", parm.getValue("PRICE"));
			
			table_n.removeRow(i);
			
			table_y.setParmValue(fillYParm(new TParm()));
			
			break;
		}
		String bottleCode = getValueString("ELC_LABEL");
    	TTextField BOTTLE_CODE = (TTextField) getComponent("ELC_LABEL");
    	BOTTLE_CODE.select(0,bottleCode.length());
    	
		return;
	}

	
	//选项卡点击
	public void onTPanlClicked() {
		table_y.setParmValue(fillYParm(new TParm()));
	}
	
	/**未出库table数据组装*/
	private TParm fillNParm(TParm parm) {
		//MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
		
		parm.setData("MED_NAME", 0, "危！硫酸氢氯吡格雷片");
		parm.setData("SPEC", 0, "25mg*7");
		parm.setData("BETCH_NO", 0, "00002");
		parm.setData("VALID_DATE", 0, "2015-10-13");
		parm.setData("AMOUNT", 0, "10");
		parm.setData("UNIT", 0, "片");
		parm.setData("PRICE", 0, "12.7620");
		
		parm.setData("MED_NAME", 1, "阿司匹林肠溶片");
		parm.setData("SPEC", 1, "75mg*20");
		parm.setData("BETCH_NO", 1, "00002");
		parm.setData("VALID_DATE", 1, "2015-10-13");
		parm.setData("AMOUNT", 1, "10");
		parm.setData("UNIT", 1, "片");
		parm.setData("PRICE", 1, "6.4590");
		
		parm.setData("MED_NAME", 2, "芬太尼注射液");
		parm.setData("SPEC", 2, "100mg*10");
		parm.setData("BETCH_NO", 2, "00002");
		parm.setData("VALID_DATE", 2, "2015-10-13");
		parm.setData("AMOUNT", 2, "10");
		parm.setData("UNIT", 2, "片");
		parm.setData("PRICE",2, "200.0000");
		
		parm.setCount(3);
		return parm;
	}
	
	
	/**已出库table数据组装*/
	private TParm fillYParm(TParm parm) {

		return this.table_y_parm;
	}
	
	
    /**清空方法*/
    public void onClear() {
        table_n.removeRowAll();
        table_y.removeRowAll();
        
        trunk_no.setText("");
        
        bill_no.setText("");
        bill_no.setEditable(true);
        
        elc_label.setText("");
        area.setText("");
        
        table_y_parm = new TParm();
    }
    
    /**查询*/
    public void onQuery() {
    	onBillEnter();
    }
}
