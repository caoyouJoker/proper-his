package com.javahis.ui.ope;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
//import com.javahis.util.StringUtil;

public class OPEOpDetailListControl extends TControl {
	private  TTable table;
	private TParm recptype;
	public void onInit(){
		super.onInit();
//		System.out.println("OPEOpDetailListControl");
		table = (TTable) getComponent("TABLE");
		// 得到前台传来的数据并显示在界面上
		recptype = this.getInputParm();
//		System.out.println(recptype);
		if (recptype != null) {
			this.onQuery();
		}
	}
	private void onQuery(){
		String Sql =
			" SELECT B.OPBOOK_SEQ,B.OP_DATE,B.DIAG_CODE1,B.OP_CODE1,C.BLOOD_TYPE,C.BLOOD_RH_TYPE,A.ALLERGY,A.INFECT_SCR_RESULT,A.INFECT_SCR_RESULT_CONT "+
			" FROM ADM_INP A,OPE_OPBOOK B,SYS_PATINFO C "+
			" WHERE A.CASE_NO='"+recptype.getValue("CASE_NO")+"' "+ 
			" AND A.DS_DATE IS NULL "+
			" AND A.CANCEL_FLG='N' "+
			" AND B.CASE_NO=A.CASE_NO "+
			" AND B.ADM_TYPE='I' "+
			" AND B.CANCEL_FLG='N' "+
			" AND C.MR_NO=A.MR_NO "+
			" AND B.TYPE_CODE = '"+recptype.getValue("TYPE_CODE")+"' ";//add 20170518 lij  区分医生开立的手术申请类型 ：1是手术2介入
		//System.out.println("queryM==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
//		System.out.println(">>>>>>>>>>>>>>>>>>>>"+tabParm.getCount("OPBOOK_SEQ"));
		if(tabParm.getCount("OPBOOK_SEQ")<0){
			this.messageBox("未找到手术申请！");
			return;
		}
		table.setParmValue(tabParm);
		
//		tableM.setHeader("预警,40;类别,40;名称,120;分值,60;指标意义,120;最新评估时间,120,timestamp,yyyy/mm/dd hh:mm:ss");
//		tableM.setParmMap("WARNING;EVALUTION;EVALUTION_DESC;SCORE;SCORE_DESC;EVALUTION_DATE;EVALUTION_CODE;SOURCE;NIS_ID;FILE_PATH;WARNING_FLG;EVALUTION_CLASS");
//		System.out.println("tabParm==="+tabParm);
	}
	public void onReturn(){
	    int rowTot = table.getRowCount();
	    int row = table.getSelectedRow();
	    if (rowTot ==1){
			table.acceptText();
			if(row==-1){
				row = 0;  
			}
			this.setReturnValue(table.getParmValue().getRow(row));
			this.closeWindow();
	    }else if(rowTot>1) {
			table.acceptText();
			if(row==-1){
				this.messageBox("请选择对应手术申请！");
				return;
			}
			this.setReturnValue(table.getParmValue().getRow(row));
			this.closeWindow();
	    }
	}
}
