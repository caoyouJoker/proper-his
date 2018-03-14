package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPasswordField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 护工维护
 * </p>
 *
 * <p>
 * Description: 护工维护
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
 * @author yangjj 2015.12.9
 * @version 1.0
 */

public class SYSOperatorNurseControl extends TControl {
	private TTextField USER_ID;
	private TTextField USER_NAME;
	private TTextField PY1;
	private TTextField PY2;
	private TTextField SEQ;
	private TTextField DESCRIPTION;
	private TCheckBox FOREIGNER_FLG;
	private TTextField ID_NO;
	private TComboBox SEX_CODE;
	private TTextField ADDRESS;
	private TTextField TEL;
	private TPasswordField USER_PASSWORD;
	private TTextFormat ACTIVE_DATE;
	private TTextFormat END_DATE;
	private TTable TABLE;
	
	/**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
    }
    
    public void initPage(){
    	USER_ID = (TTextField) this.getComponent("USER_ID");
    	USER_NAME = (TTextField) this.getComponent("USER_NAME");
    	PY1 = (TTextField) this.getComponent("PY1");
    	PY2 = (TTextField) this.getComponent("PY2");
    	SEQ = (TTextField) this.getComponent("SEQ");
    	DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
    	FOREIGNER_FLG = (TCheckBox) this.getComponent("FOREIGNER_FLG");
    	ID_NO = (TTextField) this.getComponent("ID_NO");
    	SEX_CODE = (TComboBox) this.getComponent("SEX_CODE");
    	ADDRESS = (TTextField) this.getComponent("ADDRESS");
    	TEL = (TTextField) this.getComponent("TEL");
    	USER_PASSWORD = (TPasswordField) this.getComponent("USER_PASSWORD");
    	ACTIVE_DATE = (TTextFormat) this.getComponent("ACTIVE_DATE");
    	END_DATE = (TTextFormat) this.getComponent("END_DATE");
    	TABLE = (TTable) this.getComponent("TABLE");
    	
    	onClear();
    	onQuery();
    }
    
    public void onQuery(){
    	TParm parm = getParmValue();
    	TParm result = new TParm(TJDODBTool.getInstance().select(getQuerySql(parm)));
    	System.out.println("result yangjj:"+result);
    	TABLE.setParmValue(result);
    }
    
    public void onNew(){
    	USER_ID.setEnabled(true);
    }
    
    public void onSave(){
    	TParm parm = getParmValue();
    	TParm result = new TParm();
    	if(USER_ID.isEnabled()){
    		TParm count = new TParm(TJDODBTool.getInstance().select(getQueryByUserIdSql(USER_ID.getValue())));
    		if(count.getCount("USER_ID") > 0){
    			this.messageBox("此使用者已存在！");
    			return ;
    		}
    		
    		if(!checkData()){
    			this.messageBox("此输入使用者！");
    			return ;
    		}
    		result = new TParm(TJDODBTool.getInstance().update(getInsertSql(parm)));
    	}else{
    		result = new TParm(TJDODBTool.getInstance().update(getUpdateSql(parm)));
    	}
    	
    	if(result.getErrCode() < 0){
    		this.messageBox("保存失败！");
    	}else{
    		this.messageBox("保存成功！");
    	}
    	onClear();
    	onQuery();
    }
    
    public void onTableClick(){
    	USER_ID.setEnabled(false);
    	int count = TABLE.getSelectedRow();
    	TParm parm = TABLE.getParmValue();
    	TParm row = parm.getRow(count);
    	TParm result = new TParm(TJDODBTool.getInstance().select(getQueryByUserIdSql(row.getValue("USER_ID"))));
    	setParmValue(result.getRow(0));
    }
    
    public boolean checkData(){
    	boolean b = true;
    	if("".equals(USER_ID.getValue())){
    		b = false;
    	}
    	return b;
    }
    
    public String getQuerySql(TParm parm){
    	String sql = "";
    	sql += " SELECT " +
    				" USER_ID,USER_NAME,PY1,PY2,SEQ,DESCRIPTION,FOREIGNER_FLG,ID_NO, " +
    				" TEL,SEX_CODE,ADDRESS,USER_PASSWORD,ACTIVE_DATE,END_DATE,OPT_USER,OPT_DATE,OPT_TERM " +
    			" FROM " +
    				" SYS_OPERATOR_NURSE " +
    			" WHERE " +
    				" 1 = 1 ";
    	if(!"".equals(parm.getValue("USER_ID"))){
    		sql += " AND USER_ID = '"+parm.getValue("USER_ID")+"'";
    	}
    	
    	if(!"".equals(parm.getValue("USER_NAME"))){
    		sql += " AND USER_NAME = '"+parm.getValue("USER_NAME")+"'";
    	}
    	
    	if(!"".equals(parm.getValue("ID_NO"))){
    		sql += " AND ID_NO = '"+parm.getValue("ID_NO")+"'";
    	}
    	
    	if(!"".equals(parm.getValue("SEX_CODE"))){
    		sql += " AND SEX_CODE = '"+parm.getValue("SEX_CODE")+"'";
    	}
    	
    	if(!"".equals(parm.getValue("TEL"))){
    		sql += " AND TEL = '"+parm.getValue("TEL")+"'";
    	}
    	return sql;
    }
    
    public String getInsertSql(TParm parm){
    	String sql = "";
    	sql += " INSERT INTO SYS_OPERATOR_NURSE(" +
    				" USER_ID," +
    				" USER_NAME, " + 
    				" PY1, " +
    				" PY2, " +
    				" SEQ, " +
    				" DESCRIPTION, " +
    				" FOREIGNER_FLG, " +
    				" ID_NO, " +
    				" SEX_CODE, " +
    				" TEL, " +
    				" ADDRESS, " +
    				" USER_PASSWORD, " +
    				" ACTIVE_DATE, " +
    				" END_DATE, " +
    				" OPT_USER, " + 
    				" OPT_DATE, " +
    				" OPT_TERM " +
    			") VALUES(" +
    				"'"+parm.getValue("USER_ID")+"'," +
    				"'"+parm.getValue("USER_NAME")+"'," +
    				"'"+parm.getValue("PY1")+"'," +
    				"'"+parm.getValue("PY2")+"'," +
    				"'"+parm.getValue("SEQ")+"'," +
    				"'"+parm.getValue("DESCRIPTION")+"'," +
    				"'"+parm.getValue("FOREIGNER_FLG")+"'," +
    				"'"+parm.getValue("ID_NO")+"'," +
    				"'"+parm.getValue("SEX_CODE")+"'," +
    				"'"+parm.getValue("TEL")+"'," +
    				"'"+parm.getValue("ADDRESS")+"'," +
    				"'"+parm.getValue("USER_PASSWORD")+"'," +
    				"TO_DATE('"+parm.getValue("ACTIVE_DATE")+"','YYYYMMDD HH24:MI:SS')," +
    				"TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDD HH24:MI:SS')," +
    				"'"+parm.getValue("OPT_USER")+"'," +
    				"TO_DATE('"+parm.getValue("OPT_DATE")+"','YYYYMMDD HH24:MI:SS')," +
    				"'"+parm.getValue("OPT_TERM")+"'" +
    			")";
    	return sql;
    }
    
    public String getUpdateSql(TParm parm){
    	String sql = "";
    	sql += " UPDATE SYS_OPERATOR_NURSE "+
    				" SET USER_NAME='"+parm.getValue("USER_NAME")+"'," +
    					" PY1='"+parm.getValue("PY1")+"'," +
    					" PY2='"+parm.getValue("PY2")+"'," +
    					" SEQ='"+parm.getValue("SEQ")+"'," +
    					" DESCRIPTION='"+parm.getValue("DESCRIPTION")+"'," +
    					" FOREIGNER_FLG='"+parm.getValue("FOREIGNER_FLG")+"'," +
    					" ID_NO='"+parm.getValue("ID_NO")+"'," +
    					" SEX_CODE='"+parm.getValue("SEX_CODE")+"'," +
    					" TEL='"+parm.getValue("TEL")+"'," +
    					" ADDRESS='"+parm.getValue("ADDRESS")+"'," +
    					" USER_PASSWORD='"+parm.getValue("USER_PASSWORD")+"'," +
    					" ACTIVE_DATE=TO_DATE('"+parm.getValue("ACTIVE_DATE")+"','YYYYMMDD HH24:MI:SS')," +
    					" END_DATE=TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDD HH24:MI:SS')," +
    					" OPT_USER='"+parm.getValue("OPT_USER")+"'," +
    					" OPT_DATE=TO_DATE('"+parm.getValue("OPT_DATE")+"','YYYYMMDD HH24:MI:SS')," +
    					" OPT_TERM='"+parm.getValue("OPT_TERM")+"' " +
    				" WHERE USER_ID='"+parm.getValue("USER_ID")+"'";
    	return sql;
    }
    
    public String getQueryByUserIdSql(String userId){
    	String sql = "";
    	sql += " SELECT "+
    	" USER_ID,USER_NAME,PY1,PY2,SEQ,DESCRIPTION,FOREIGNER_FLG,ID_NO,SEX_CODE, " +
		" TEL,SEX_CODE,ADDRESS,USER_PASSWORD,ACTIVE_DATE,END_DATE,OPT_USER,OPT_DATE,OPT_TERM " +
    	" FROM SYS_OPERATOR_NURSE WHERE USER_ID='"+userId+"'";
    	return sql;
    }
    
    public TParm getParmValue(){
    	TParm parm = new TParm();
    	parm.setData("USER_ID", USER_ID.getValue());
    	parm.setData("USER_NAME",USER_NAME.getValue());
    	parm.setData("ID_NO", ID_NO.getValue());
    	parm.setData("SEX_CODE", SEX_CODE.getValue());
    	parm.setData("TEL",TEL.getValue());
    	parm.setData("PY1", PY1.getValue());
    	parm.setData("PY2", PY2.getValue());
    	parm.setData("SEQ", SEQ.getValue());
    	parm.setData("DESCRIPTION", DESCRIPTION.getValue());
    	parm.setData("FOREIGNER_FLG", FOREIGNER_FLG.getValue());
    	parm.setData("ADDRESS", ADDRESS.getValue());
    	String oldPWD = OperatorTool.getInstance().encrypt(
                getValueString("USER_PASSWORD"));
    	parm.setData("USER_PASSWORD", oldPWD);
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().replace("-", "").replace(".0", ""));
    	parm.setData("OPT_TERM", Operator.getIP());
    	
    	
    	if(ACTIVE_DATE.getValue() == null){
    		parm.setData("ACTIVE_DATE", "19000101 00:00:00");
    	}else{
    		parm.setData("ACTIVE_DATE", ACTIVE_DATE.getValue().toString().replace("-", "").replace(".0", ""));
    	}
    	
    	if(END_DATE.getValue() == null){
    		parm.setData("END_DATE", "99991231 23:59:59");
    	}else{
    		parm.setData("END_DATE", END_DATE.getValue().toString().replace("-", "").replace(".0", ""));
    	}
    	return parm;
    }
    
    public void setParmValue(TParm parm){
    	USER_ID.setValue(parm.getValue("USER_ID"));
    	USER_NAME.setValue(parm.getValue("USER_NAME"));
    	ID_NO.setValue(parm.getValue("ID_NO"));
    	SEX_CODE.setValue(parm.getValue("SEX_CODE"));
    	TEL.setValue(parm.getValue("TEL"));
    	PY1.setValue(parm.getValue("PY1"));
    	PY2.setValue(parm.getValue("PY2"));
    	SEQ.setValue(parm.getValue("SEQ"));
    	DESCRIPTION.setValue(parm.getValue("DESCRIPTION"));
    	FOREIGNER_FLG.setValue(parm.getValue("FOREIGNER_FLG"));
    	ADDRESS.setValue(parm.getValue("ADDRESS"));
    	String oldPWD = OperatorTool.getInstance().decrypt(
    			parm.getValue("USER_PASSWORD"));
    	USER_PASSWORD.setValue(oldPWD);
    	ACTIVE_DATE.setValue(parm.getValue("ACTIVE_DATE").toString().replace("-", "/").replace(".0", ""));
    	END_DATE.setValue(parm.getValue("END_DATE").toString().replace("-", "/").replace(".0", ""));
    }
    
    public void onClear(){
    	String clearStr = "USER_ID;USER_NAME;PY1;PY2;SEQ;DESCRIPTION;FOREIGNER_FLG;"
            + "TEL;ID_NO;SEX_CODE;ADDRESS;USER_PASSWORD;ACTIVE_DATE;END_DATE";
        this.clearValue(clearStr);
        USER_ID.setEnabled(true);
        ACTIVE_DATE.setValue(SystemTool.getInstance().getDate());
    }
}
