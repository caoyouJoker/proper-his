package com.javahis.ui.sys;

import java.awt.Color;

import jdo.sys.SystemTool;

//import jdo.sys.Operator;
//import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TTable;
//import com.dongyang.ui.TWord;
//import com.dongyang.ui.base.TWordBase;
//import com.dongyang.util.TypeTool;
//import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;

/**
* <p>Title: 评估一览</p>
*
* <p>Description:评估一览 </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company: </p>
*
* @author zhangs 20151029
* @version 1.0
*/

public class SYSAssessmentListControl extends TControl{

	private  TTable tableM;
	private  TTable tableD;
	private  String caseNo="";
	private  TParm tableParm = null;
	private  String ipdNo="";
    // TWORD
//    private TWord word;
    // 结构化病历保存路径
    private String[] saveFile;
    // 结构化病例路径
//    private TParm pathParm, patParm;

//	private ESingleChoose singleChoose;
	 
	/**
	 * 主表选中的行
	 */
//	private int tableMSelectedRow = -1;

	public void onInit(){
		super.onInit();
//		System.out.println("className:"+getParameter().getClass().getName());
//		System.out.println(getParameter());

//		this.singleChoose = ((ESingleChoose)getParameter());
//	    if (this.singleChoose == null) {
//	      return;
//	    }
		tableM = (TTable) getComponent("tTable_M");
		tableD = (TTable) getComponent("tTable_D");
		// 得到前台传来的数据并显示在界面上
		TParm recptype = this.getInputParm();
		System.out.println(recptype);
		if (recptype != null) {
			this.setValue("MR_NO", recptype.getValue("MR_NO"));
			this.setValue("PAT_NAME", recptype.getValue("PAT_NAME"));
			this.setValue("DEPT", recptype.getValue("DEPT_CODE"));
			this.setValue("BED_NO", recptype.getValue("BED_NO_DESC"));
			caseNo=recptype.getValue("CASE_NO");
			ipdNo=recptype.getValue("IPD_NO");
		}
		this.onQuery();
	}
	
	public void onQuery(){
		 this.queryM(caseNo,getValueString("SOURCE"),getValueString("SORT"));
	}
	/**
	 * 查询
	 */
	public void queryM(String caseNo,String source,String sort) {
		String Sql =
			" SELECT CASE T.WARNING_FLG WHEN '0' THEN '' WHEN '1' THEN '红' END WARNING,CASE T.EVALUTION_CLASS WHEN '0' THEN '护理' WHEN '1' THEN '医务' END EVALUTION, " +
			" T.EVALUTION_DESC,T.SCORE,T.SCORE_DECS,T.EVALUTION_DATE,T.EVALUTION_CODE,T.SOURCE,T.NIS_ID,T.FILE_PATH,T.WARNING_FLG,T.EVALUTION_CLASS "+
			" FROM EMR_EVALUTION_RECORD T "+
			" WHERE T.CASE_NO='"+caseNo+"' " +
			" AND T.EVALUTION_DATE=(SELECT MAX(T.EVALUTION_DATE) FROM EMR_EVALUTION_RECORD WHERE EVALUTION_CODE=T.EVALUTION_CODE) ";
		if(!StringUtil.isNullString(source)){
			Sql =Sql+" AND T.EVALUTION_CLASS='"+source+"' ";
		}
		if(!StringUtil.isNullString(sort)){
		    if(sort.equals("0")){
			    Sql =Sql+" ORDER BY T.EVALUTION_CLASS";
			}else if(sort.equals("1")){
				Sql =Sql+" ORDER BY T.EVALUTION_DATE";
			}
		}
//		System.out.println("queryM==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if(tabParm.getCount("WARNING_FLG")<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
//		tableM.setHeader("预警,40;类别,40;名称,120;分值,60;指标意义,120;最新评估时间,120,timestamp,yyyy/mm/dd hh:mm:ss");
//		System.out.println("WARNING_FLG==="+tabParm.getCount("WARNING_FLG"));
//		tableM.setParmMap("WARNING;EVALUTION;EVALUTION_DESC;SCORE;SCORE_DESC;EVALUTION_DATE;EVALUTION_CODE;SOURCE;NIS_ID;FILE_PATH;WARNING_FLG;EVALUTION_CLASS");
//		table.setItem("DOSAGE_UNIT");
//		tableM.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,right;9,left;10,left;11,left");
//		table.setParmValue(tableinparm);
//		System.out.println("tabParm==="+tabParm);
		this.callFunction("UI|tTable_M|setParmValue", tabParm);
		setTableColor(tableM);
//		tableM.setParmValue(tabParm);
	}
	/**
	 * 主表点击事件
	 */
	public void onTableMClicked() {

		int row = tableM.getSelectedRow();
		
		// 如果选择同一行，不执行任何操作
//		if (tableMSelectedRow == row)
//			return;
//		tableMSelectedRow = row;
		tableM.acceptText();
		tableParm = tableM.getParmValue().getRow(row);
		String Sql =
			" SELECT CASE T.WARNING_FLG WHEN '0' THEN '' WHEN '1' THEN '红' END WARNING," +
			" T.EVALUTION_DATE,T.EVALUTION_DESC,T.SCORE,T.SCORE_DECS,T.EVALUTION_CODE,T.SOURCE,T.NIS_ID,T.FILE_PATH,T.WARNING_FLG "+
			" FROM EMR_EVALUTION_RECORD T "+ 
			" WHERE T.CASE_NO='"+caseNo+"' " +
			" AND T.EVALUTION_CODE='"+tableParm.getValue("EVALUTION_CODE")+"' "+
			" AND T.EVALUTION_CLASS='"+tableParm.getValue("EVALUTION_CLASS")+"' "+
			" ORDER BY T.EVALUTION_DATE DESC ";
//		System.out.println("onTableMClicked==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if(tabParm.getCount("WARNING_FLG")<0){
			this.messageBox("没有要查询的数据！");
			return;
		}
		this.callFunction("UI|tTable_D|setParmValue", tabParm);
		setTableColor(tableD);
//		tableD.setParmValue(tabParm);
	}
	/**
	 * M表鼠标右击事件
	 */
	public void onTableMRightClicked() {
		int row = tableM.getSelectedRow();
		openWindow(tableM,row);
	}
	/**
	 * D表鼠标右击事件
	 */
	public void onTableDRightClicked() {
		int row = tableD.getSelectedRow();
		openWindow(tableD,row);
	}
	/**
	 * 右键打开的窗口
	 */
	public void openWindow(TTable table,int row){
		table.acceptText();
		tableParm = table.getParmValue().getRow(row);		
		if(tableParm.getValue("SOURCE").equals("0")){
			//NIS	
			openNIS(tableParm);
		}else if(tableParm.getValue("SOURCE").equals("1")){
			//HIS
			openHIS(tableParm);
		}
	}
	/**
	 * 来源自HIS的打开方法
	 */
	public void openHIS(TParm table){
//		this.messageBox("openHIS(TTable table)"+table.getValue("FILE_PATH"));
		
        saveFile=this.getFilePath(table.getValue("FILE_PATH"));
//		System.out.println("emrParm="+saveFile[0]);
//		System.out.println("emrParm="+saveFile[1]);
//		word.onOpen(saveFile[0], saveFile[1], 3, false);
		onOpen(caseNo,saveFile);
	}
	/**
	 * 来源自NIS的打开方法
	 */
    public void openNIS(TParm table){
//    	this.messageBox("openNIS(TTable table)"+table.getValue("NIS_ID"));
        String url = 
//            m2Login_login=帐号&encid=住院号&patientid=病例号&encType=I&formId=表单UUId&formType=表单编码
        	"http://"+ TConfig.getSystemValue("NisIP") +
        	"/Nis/m2/user/login?m2Login_login=administrator&encid="+this.caseNo+
        	"&patientid="+this.getValue("MR_NO")+"&encType=I&formId="+table.getValue("NIS_ID")+
        	"&formType="+table.getValue("EVALUTION_CODE");
//        "http://172.20.1.70:8080/nis/m2/user/login?m2Login_login=administrator&encid=000000084283&patientid=000000099999&encType=I&formId=00000150DBAE7371&formType=EMR060206";	
        	SystemTool.getInstance().OpenIE(url);

    }
    /**
	 * 设置高危记录为红色字体
	 */
    public void setTableColor(TTable table){
    	table.acceptText();
		tableParm = table.getParmValue();
//		table.getRowColor(row);
		for (int i = 0; i < tableParm.getCount(); i++) {
			
			if (tableParm.getRow(i).getValue("WARNING_FLG").equals("1")){
				table.setRowTextColor(i,Color.RED);
//				table.setRowColor(i,Color.RED);
			}else{
				table.setRowTextColor(i,Color.BLACK);
//				table.setRowColor(i,Color.WHITE);
			}
		}
    }
	/**
	 * 解析文件路径
	 */

    public String[] getFilePath(String filePate){
    	//JHW\15\05\00000706\150530000003_产科出院记录_64
    	String saveFile[] = new String[2];
    	String temp[]=filePate.split("\\\\");
    	saveFile[1] = temp[temp.length-1];
    	saveFile[0]=filePate.replace("\\"+saveFile[1], "");	
        return saveFile;
    }
	/**
	 * 打开电子病历
	 */
    public void onOpen(String caseNo,String saveFile[]){
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("FILE_NAME", saveFile[1]);		
		parm.setData("FILE_PATH", saveFile[0]);
		
		this.openWindow("%ROOT%\\config\\sys\\SYSEmrView.x", parm);
    }
	/**
	 * 清空
	 */
	public void onClear(){
		this.tableM.removeRowAll();
		this.tableD.removeRowAll();
	}
}
