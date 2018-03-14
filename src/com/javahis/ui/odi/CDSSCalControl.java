package com.javahis.ui.odi;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

public class CDSSCalControl extends TControl{
	
	TTable table;
	
	public void onInit(){
		table = (TTable) getComponent("TABLE");
		TParm droolsLogBean = (TParm) getParameter();
		
		TParm parm = droolsLogBean.getParm("LOG_PARM");
//		System.out.println("parm=="+parm);
		table.setParmValue(parm);
		
	}
	
	public void onSave(){
		table.acceptText();
		int row = table.getSelectedRow();
		if(row >= 0){
			TParm p = new TParm();
			p.setData("MEDI_QTY", StringTool.round(getValueDouble("MEDI_QTY"), 4));
			p.setData("UNIT_CODE", getValue("UNIT"));
			p.setData("FREQ_CODE", getValue("FREQ_CODE"));
			setReturnValue(p);
		}
		closeWindow();
	}
	
	public void onClick(){
		table.acceptText();
		TParm p = table.getParmValue();
		int row = table.getSelectedRow();
		setValue("ID", p.getValue("ID", row));
		setValue("ORDER_CODE", p.getValue("ORDER_CODE", row));
		setValue("ORDER_DESC", p.getValue("ORDER_DESC", row));
		setValue("MEDI_QTY", p.getDouble("MEDI_QTY", row));
		setValue("UNIT", p.getValue("UNIT_CODE", row));
		setValue("FREQ_CODE", p.getValue("FREQ_CODE", row));
		setValue("ADVICE", p.getValue("ADVICE", row)+"  "+p.getValue("REMARKS", row));
	}
	
}
