package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TRadioButton;

import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title:病患查询 </p>
 */
public class ADMPatEmrControl extends TControl{
    TParm result = new TParm();
    private TTable table;
    private TParm parm;
    private String patType = "";//病患类型
    public void onInit() {
    	parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		table = (TTable) this.getComponent("TABLE");
		patType = parm.getValue("PAT_TYPE");
//		System.out.println("patType:::::"+patType);
		//0:门诊,1:住院
		if(patType.equals("0")){
		callFunction("UI|IPD_NO|setEnabled", false);
		callFunction("UI|ADM_TYPE|setEnabled", false);
		this.table.setHeader("病案号,150;姓名,100;性别,50;身份证号,150;" +
		        "电话,100;出生日期,120;就诊号,150;就诊日期,120");
        this.table.setLockColumns("0,1,2,3,4,5,6,7");
        this.table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5," +
		        "left;6,left;7,left");
        this.table.setParmMap("MR_NO;PAT_NAME;SEX_DESC;IDNO;TEL_HOME;" +
		        "BIRTH_DATE;CASE_NO;ADM_DATE");
		}else{
		setValue("ADM_TYPE","0");
		this.table.setHeader("病案号,150;住院号,150;姓名,100;性别,50;身份证号,150;" +
				"电话,100;出生日期,120;就诊号,150;入院日期,120;出院日期,120");
	    this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9");
	    this.table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5," +
	    		"left;6,left;7,left;8,left;9,left");
	    this.table.setParmMap("MR_NO;IPD_NO;PAT_NAME;SEX_DESC;IDNO;TEL_HOME;" +
	    		"BIRTH_DATE;CASE_NO;IN_DATE;DS_DATE");	
		}
  }
  /*
   * 清空
   */
  public void onClear(){
      setValue("MR_NO","");
      setValue("IPD_NO","");
      setValue("PAT_NAME","");
      setValue("TEL_HOME","");
      if(patType.equals("0"))
      setValue("ADM_TYPE","");
      else
      setValue("ADM_TYPE","0");  
      setValue("BIRTH_DATE","");
      setValue("ID_NO","");      
      ((TTable) getComponent("TABLE")).removeRowAll();
      if(patType.equals("0")){
  		callFunction("UI|IPD_NO|setEnabled", false);
  		callFunction("UI|ADM_TYPE|setEnabled", false);
      }
      result = new TParm();

  }
  /**
   * 查询事件
   */
  public void onQuery(){
	  ((TTable) getComponent("TABLE")).removeRowAll();
      TParm parm = new TParm();
      String sql1="";
      String sql2="";
      String sql3="";
      String sql4="";
      String sql5="";
      String sql6="";
      String sql7="";
	 if (!this.getValue("MR_NO").equals("")) {
	      parm.setData("MR_NO","%"+getValue("MR_NO")+"%");
	       sql1 = " AND A.MR_NO LIKE '"+parm.getValue("MR_NO")+"'";
	   } 
	 if (!this.getValue("IPD_NO").equals("")) {
	      parm.setData("IPD_NO","%"+getValue("IPD_NO")+"%");
	       sql2 = " AND A.IPD_NO LIKE '"+parm.getValue("IPD_NO")+"'";
	   }
	 if (!this.getValue("PAT_NAME").equals("")) {
	      parm.setData("PAT_NAME","%"+getValue("PAT_NAME")+"%");
	      sql3 = " AND A.PAT_NAME LIKE '"+parm.getValue("PAT_NAME")+"'";
	   } 
	if (!this.getValue("TEL_HOME").equals("")) {
	      parm.setData("TEL_HOME","%"+getValue("TEL_HOME")+"%");
	      sql4 = " AND A.TEL_HOME LIKE '"+parm.getValue("TEL_HOME")+"'";
	   } 
	 if (!this.getValue("ID_NO").equals("")) {
	      parm.setData("ID_NO","%"+getValue("ID_NO")+"%");
	      sql5 = " AND A.IDNO LIKE '"+parm.getValue("ID_NO")+"'";
	   } 
	 String birthDate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
     "BIRTH_DATE")), "yyyyMMdd"); //拿到界面的时间
	 if (!birthDate.equals("")) {		   
	      parm.setData("BIRTH_DATE",birthDate);
	      sql6 = " AND A.BIRTH_DATE = TO_DATE('"+parm.getValue("BIRTH_DATE")+"','YYYYMMDD')";
	   }
	 if (this.getValue("ADM_TYPE").equals("0"))
		 sql7 = "AND B.DS_DATE IS NULL";
	 else
		 sql7 = "AND B.DS_DATE IS NOT NULL";
      String sqlall =
    		 " SELECT A.MR_NO,A.IPD_NO,A.PAT_NAME,(SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
             " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_SEX'"+ 
             " AND SYS_DICTIONARY.ID =A.SEX_CODE) AS SEX_DESC," +
    		 " A.IDNO,A.TEL_HOME," +
    		 " A.BIRTH_DATE,B.CASE_NO,B.IN_DATE,B.DS_DATE,A.SEX_CODE"+
             " FROM SYS_PATINFO A,ADM_INP B"+		
             " WHERE A.IPD_NO = B.IPD_NO"+
             sql1+
             sql2+
             sql3+
             sql4+
             sql5+
             sql6+
             sql7+
             " AND B.CANCEL_FLG ='N'"+
             " ORDER BY A.MR_NO";
//    System.out.println("SQL=============" + sqlall);	 
      String sql =   
    	     " SELECT A.MR_NO,A.PAT_NAME,(SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
             " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_SEX'"+ 
             " AND SYS_DICTIONARY.ID =A.SEX_CODE) AS SEX_DESC,"+ 
    	     " A.IDNO,A.TEL_HOME,"+ 
    	     " A.BIRTH_DATE,B.CASE_NO,B.ADM_DATE,A.SEX_CODE,B.ADM_TYPE"+
    	     " FROM SYS_PATINFO A,REG_PATADM B"+		
    	     " WHERE A.MR_NO = B.MR_NO"+
    	     sql1+
             sql3+
             sql4+
             sql5+
             sql6+
             " AND B.REGCAN_USER IS NULL"+
             " ORDER BY A.MR_NO";
//    System.out.println("SQL=============" + sql);	    
      if(patType.equals("1"))  
         result = new TParm(TJDODBTool.getInstance().select(sqlall)); 
      else
         result = new TParm(TJDODBTool.getInstance().select(sql)); 	  
//    System.out.println("result=============" + result);   
   // 判断错误值
   if (result.getErrCode() < 0) {
       messageBox(result.getErrText());
       return;
   }
   if (result.getCount()<= 0) {			
		messageBox("E0008");//查无资料
		return;
	}
   ((TTable) getComponent("TABLE")).setParmValue(result);
}
  /**
   * 传回MR_NO;IPD_NO;PAT_NAME;IDNO;TEL_HOME;BIRTH_DATE;
   * CASE_NO;IN_DATE;DS_DATE;SEX_CODE
   */
  public void onSave() {
      TTable table = (TTable)this.callFunction("UI|TABLE|getThis");
      int row = table.getSelectedRow();
      TParm backData = new TParm();
      if(patType.equals("1")){  
      backData.setData("MR_NO", table.getValueAt(row, 0));
      backData.setData("IPD_NO", table.getValueAt(row, 1));
      backData.setData("PAT_NAME", table.getValueAt(row, 2));
      backData.setData("ID_NO", table.getValueAt(row, 4));
      backData.setData("TEL_HOME", table.getValueAt(row, 5));
      backData.setData("BIRTH_DATE", table.getValueAt(row, 6));
      backData.setData("CASE_NO", table.getValueAt(row, 7));
      backData.setData("IN_DATE", table.getValueAt(row, 8));
      backData.setData("DS_DATE", table.getValueAt(row, 9));
      backData.setData("SEX_CODE", result.getData("SEX_CODE", row));
      backData.setData("ADM_TYPE", "I");
      }else{
      backData.setData("MR_NO", table.getValueAt(row, 0));
      backData.setData("PAT_NAME", table.getValueAt(row, 1));
      backData.setData("ID_NO", table.getValueAt(row, 3));
      backData.setData("TEL_HOME", table.getValueAt(row, 4));
      backData.setData("BIRTH_DATE", table.getValueAt(row, 5));
      backData.setData("CASE_NO", table.getValueAt(row, 6));
      backData.setData("ADM_DATE", table.getValueAt(row, 7));
      backData.setData("SEX_CODE", result.getData("SEX_CODE", row));
      backData.setData("ADM_TYPE", result.getData("ADM_TYPE", row));
      }
      this.setReturnValue(backData);
      this.closeWindow();
  }

}
