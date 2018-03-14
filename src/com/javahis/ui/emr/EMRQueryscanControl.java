package com.javahis.ui.emr;




import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * Title: 高拍病历查询
 * Description:高拍病历查询
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class EMRQueryscanControl extends TControl {
	private TTable table;
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    /**
     * 初始化数据
     */
    public void getData() { 	
    	//开始日期
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
    	//结束日期
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	//初始化
    	this.callFunction("UI|TABLE|setParmValue", new TParm());
    	this.setValue("MR_NO","");

    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return true;
    	}
	    return false; 
	}
    /**
     * 查询
     */
    public void onQuery() {
    	//数据检核
    	if(checkdata())
		    return;
    	String sql1 ="";   	
    	if(!getValue("MR_NO").equals("")){
    		String mrno = PatTool.getInstance().checkMrno(
        			TypeTool.getString(getValue("MR_NO")));	
    		sql1 = " AND A.MR_NO = '"+ mrno + "'";
    		this.setValue("MR_NO",mrno);
    	}
    	 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "START_DATE")), "yyyyMMdd")+"000000"; //开始日期
    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "END_DATE")), "yyyyMMdd")+"235959"; //结束日期
 		//查询病患
 		String sql =" SELECT A.MR_NO,B.PAT_NAME,CASE WHEN B.SEX_CODE = '1' THEN '男'" +
 		    " WHEN B.SEX_CODE = '2' THEN '女' END AS SEX_CODE,"+
 			" CASE WHEN A.ADM_TYPE = 'O' THEN '门诊'"+ 
 			" WHEN A.ADM_TYPE = 'E' THEN '急诊'" +
 			" WHEN A.ADM_TYPE = 'I' THEN '住院' END AS ADM_TYPE," +
 			" A.FILE_NAME,C.USER_NAME,A.CREATOR_DATE,A.FILE_PATH" +
 			" FROM EMR_THRFILE_INDEX A, SYS_PATINFO B,SYS_OPERATOR C" +
 			" WHERE A.MR_NO  = B.MR_NO" +
			sql1+
 			" AND A.CREATOR_USER  = C.USER_ID" +
 			" AND A.CREATOR_DATE  BETWEEN " +
 			" TO_DATE('"+ startdate+ "','YYYYMMDDHH24MISS')" + 
 			" AND  TO_DATE('"+ enddate+ "','YYYYMMDDHH24MISS')" +
 			" ORDER BY A.CREATOR_DATE DESC";		
// 		System.out.println("sql=====:"+sql);
 		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
// 		System.out.println("result=====:"+result);
 		// 判断错误值
 		if (result.getErrCode() < 0) {
 			messageBox(result.getErrText());
 			messageBox("E0005");//执行失败
 			return;
 		}
 		if (result.getCount()<= 0) {
 			messageBox("E0008");//查无资料
 			table.removeRowAll();
 			return;
 		}
 		table.setParmValue(result);	
  }  
    /**
     * 病案号回车事件
     */
    public void onExeQuery() {  	
    	onQuery();
    	
    }
    /**
     * 获得病历
     */
    public void onSelect() {
    	int Row = table.getSelectedRow();//行数
		TParm parm = table.getParmValue().getRow(Row);
		String tempPath = "C:\\JavaHisFile\\temp\\pdf";
		String filename = parm.getData("FILE_NAME")+".pdf";
		String serverPath = TConfig.getSystemValue("FileServer.Main.Root") + "\\"
		+ TConfig.getSystemValue("EmrData") + "\\"
		+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF");
		Runtime runtime = Runtime.getRuntime();
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				serverPath + "\\" + filename);
		if (data == null) {
			messageBox_("服务器上没有找到文件 " + serverPath + "\\"
					+ filename);
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\" + filename, data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" +filename);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
    }
    /**
     * 清空
     */
    public void onClear() {
    	 getData();//初始化数据     
    }

}
