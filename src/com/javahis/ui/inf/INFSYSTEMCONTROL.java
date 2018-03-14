package com.javahis.ui.inf;

import java.math.BigDecimal;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.odi.OPDBackupDataToPDFControl;
import com.javahis.util.StringUtil;

public class INFSYSTEMCONTROL  extends TControl{
	public void onInit() {
		super.onInit();
		onQuery();
		getTable("TABLE").addEventListener(
				"TABLE->" + TTableEvent.CLICKED, this,
				"onTableValue");
		((TTable)getComponent("TABLE")).setParmMap("INFSYSTEMNAME;INFPOSITIONNAME");
		
	}
	public void onTableValue(int row){
		if(row<0){
			return;
		}
		TParm parm = ((TTable)getComponent("TABLE")).getParmValue().getRow(row);
		this.setValue("INFSYSTEM_CODE", parm.getValue("INFSYSTEMCODE"));
		this.setValue("INFPOSITION_CODE", parm.getValue("INFPOSITIONCODE"));
	}
	


	
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}
	
	public void onclear(){
		((TTable)getComponent("TABLE")).setParmValue(new TParm());;
		((TTextFormat)getComponent("INFSYSTEM_CODE")).setValue("");
		((TComboBox)getComponent("INFPOSITION_CODE")).setValue("");
	}
	public void onQuery(){
		String sql = "SELECT INFSYSTEMCODE, "+
			         "       INFPOSITIONCODE, "+
			         "       (SELECT CHN_DESC "+
			         "         FROM SYS_DICTIONARY B "+
			         "         WHERE 1 = 1 AND B.GROUP_ID = 'INF_SYSTEM' AND B.ID = A.INFSYSTEMCODE) " +
			         "      AS INFSYSTEMNAME, "+
			         "       (SELECT CHN_DESC "+ 
			         "         FROM SYS_DICTIONARY C "+
			         "         WHERE 1 = 1 AND C.GROUP_ID = 'INF_INFPOSITION'AND C.ID = A.INFPOSITIONCODE) "+
			         "      AS INFPOSITIONNAME "+
			         "FROM INF_SYSTEMDICTRONARY A "+
			         "WHERE 1 = 1 ";
			 
		if(!StringUtil.isNullString(this.getValueString("INFSYSTEM_CODE"))){
			sql += "AND INFSYSTEMCODE ='"+this.getValueString("INFSYSTEM_CODE")+"'";
		}
		//System.out.println("2222222222"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//this.messageBox(result+"");
		((TTable)(getComponent("TABLE"))).setParmValue(result);
	}
	
	public void ondelete(){
		TTable table = (TTable)getComponent("TABLE");
		table.acceptText();
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue().getRow(row);
		String delSql ="DELETE INF_SYSTEMDICTRONARY WHERE INFPOSITIONCODE='"+parm.getValue("INFPOSITIONCODE")+"' AND INFSYSTEMCODE='"+parm.getValue("INFSYSTEMCODE")+"'" ;
		System.out.println("333333333"+delSql);
		TParm result = new TParm(TJDODBTool.getInstance().update(delSql));
		if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
	     }
		 this.messageBox("删除成功");
		 onQuery();
		
	}
	
	public void onsave(){
		
		if(StringUtil.isNullString(this.getValueString("INFSYSTEM_CODE"))){
			this.messageBox("感染系统不可为空");
			return;
		}
		if(StringUtil.isNullString(this.getValueString("INFPOSITION_CODE"))){
			this.messageBox("感染部位不可为空");
			return;
		}
		
		String selectSql = "SELECT * FROM INF_SYSTEMDICTRONARY WHERE INFSYSTEMCODE='"+this.getValueString("INFSYSTEM_CODE")+"'"
				+ "		    AND INFPOSITIONCODE='"+this.getValueString("INFPOSITION_CODE")+"'";
		System.out.println(selectSql);
		TParm p = new TParm(TJDODBTool.getInstance().select(selectSql));
		
		if(p.getCount()>=0){
			this.messageBox("不可添加重复部位");
			return;
		}
		String saveSql = "INSERT INTO INF_SYSTEMDICTRONARY (INFSYSTEMCODE,INFPOSITIONCODE,OPT_USER,OPT_DATE,OPT_TEAM) "
						+"VALUES "
						+"('~','!','@',TO_DATE('#','YYYY-MM-DD HH24:MI:SS'),'$')";
		saveSql = saveSql.replace("~", this.getValueString("INFSYSTEM_CODE")).
							replace("!", this.getValueString("INFPOSITION_CODE")).
							replace("@", Operator.getID()).
							replace("#", SystemTool.getInstance().getDate().toString().substring(0,19)).
							replace("$", Operator.getIP());
		System.out.println(saveSql);
		TParm result = new TParm(TJDODBTool.getInstance().update(saveSql));
		 if (result == null || result.getErrCode() < 0) {
	            this.messageBox("E0001");
	            return;
	     }
		 this.messageBox("保存成功");
		 onQuery();
	}
	
//	public void uu(){
//		this.messageBox("1");
//	}
//	
//	public void RRR(){
//		
//		TTextFormat com = (TTextFormat)getComponent("QQQ");
//		if(this.getValueString("ttt").equals("")){
//			com.setPopupMenuSQL("SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='INF_INFPOSITION' ORDER BY SEQ");
//		}else{
//			com.setPopupMenuSQL(" SELECT ID, CHN_DESC AS NAME "+
//								" FROM SYS_DICTIONARY "+
//								" WHERE     GROUP_ID = 'INF_INFPOSITION' "+
//								" AND ID IN (SELECT INFPOSITIONCODE "+
//								" FROM INF_SYSTEMDICTRONARY "+
//								" WHERE INFSYSTEMCODE = '"+this.getValueString("ttt")+"') ORDER BY SEQ");
//                    
//                   
//		}
//		com.onQuery();
//	}
//	public void SSS(){
//		this.messageBox(this.getValueString("QQQ"));
//	}
//	
}
