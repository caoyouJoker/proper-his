package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title: 病区普药入药柜操作
 * </p>
 * 
 * <p>
 * Description:  病区普药入药柜操作
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
 * @author liyh 20121017
 * @version 1.0
 */
public class INDDisOutNormalCabinetReginControl extends TControl {
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

/*        //bill_no获得焦点
		TFrame tFrame = (TFrame) getComponent("UI");
		final TTextField mrField = (TTextField) getComponent("RX_NO");
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				mrField.requestFocus();
			}
		});*/
		
	}

	/**统药单回车事件*/
	public void onRxNo() {
		TParm parm = fillNParm(new TParm());
    	( (TTextField) getComponent("BIG_BOX_CODE")).grabFocus();
		this.setValue("PITION_NAME", "王桂英");
		this.setValue("PT_NO", "00000120341");
		this.setValue("BED_NO", "0987");
		this.setValue("AGE", "76");
		this.setValue("SEX", "女");
		this.setValue("DEPT_DESC", "内二科");
		table_n.setParmValue(parm);
	}
	
	/**周转箱回车事件*/
	public void onBigBoxCode() {
		table_n.removeRowAll();
		TParm parm = fillNParm(new TParm());
		table_y.setParmValue(parm);
	}
	
	/**药品名称回车事件*/
	public void onContainerEnter() {
		
		elc_label.setText("00004");
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

		return;
	}

	
	//选项卡点击
	public void onTPanlClicked() {
		table_y.setParmValue(fillYParm(new TParm()));
	}
	
	/**未出库table数据组装*/
	private TParm fillNParm(TParm parm) {
		//MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
		parm.setData("ORDER_CODE", 0, "0102AB");
		parm.setData("ORDER_DESC", 0, "危！硫酸氢氯吡格雷片");
		parm.setData("SPECIFICATION", 0, "75mg*10");
		parm.setData("AMOUNT", 0, "10");
		parm.setData("UNIT", 0, "片");
		parm.setData("PRICE", 0, "5.6000");
		
		parm.setData("ORDER_CODE", 1, "0102AB");
		parm.setData("ORDER_DESC", 1, "吗啡");
		parm.setData("SPECIFICATION", 1, "10mg*5");
		parm.setData("AMOUNT", 1, "10");
		parm.setData("UNIT", 1, "支");
		parm.setData("PRICE", 1, "320.0000");
		
		parm.setData("ORDER_CODE", 2, "0102DB");
		parm.setData("ORDER_DESC", 2, "芬太尼注射液");
		parm.setData("SPECIFICATION", 2, "20mg*10");
		parm.setData("AMOUNT", 2, "10");
		parm.setData("UNIT", 2, "支");
		parm.setData("PRICE", 2, "200.2000");
		
		parm.setCount(3);
		return parm;
	}
	

	/**未出库table数据组装*/
	private TParm fillYParm(TParm parm) {
		//MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
		parm.setData("TRUNK_NO", 0, "1");
		parm.setData("ORDER_CODE", 0, "0102AB");
		parm.setData("ORDER_DESC", 0, "危！硫酸氢氯吡格雷片");
		parm.setData("SPECIFICATION", 0, "75mg*10");
		parm.setData("AMOUNT", 0, "10");
		parm.setData("UNIT", 0, "片");
		parm.setData("PRICE", 0, "5.6000");
		
		parm.setData("TRUNK_NO", 1, "1");
		parm.setData("ORDER_CODE", 1, "0102AB");
		parm.setData("ORDER_DESC", 1, "阿司匹林肠溶片");
		parm.setData("SPECIFICATION", 1, "10mg*5");
		parm.setData("AMOUNT", 1, "10");
		parm.setData("UNIT", 1, "片");
		parm.setData("PRICE", 1, "6.5880");
		
		parm.setData("TRUNK_NO", 2, "1");
		parm.setData("ORDER_CODE", 2, "0102DB");
		parm.setData("ORDER_DESC", 2, "芬太尼注射液");
		parm.setData("SPECIFICATION", 2, "20mg*10");
		parm.setData("AMOUNT", 2, "10");
		parm.setData("UNIT", 2, "支");
		parm.setData("PRICE", 2, "200.2000");
		
		parm.setCount(3);
		return parm;
	}
	
	
	
    /**清空方法*/
    public void onClear() {
        table_n.removeRowAll();
        table_y.removeRowAll();
        this.setValue("RX_NO", "");
		this.setValue("BIG_BOX_CODE", "");
		this.setValue("PITION_NAME", "");
		this.setValue("PT_NO", "");
		this.setValue("BED_NO", "");
		this.setValue("AGE", "");
		this.setValue("SEX", "");
		this.setValue("DEPT_DESC", "");
        
        table_y_parm = new TParm();
    }
    
    /**查询*/
    public void onQuery() {
    	onRxNo();
    }
}
