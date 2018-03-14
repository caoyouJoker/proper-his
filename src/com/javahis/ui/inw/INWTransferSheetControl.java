package com.javahis.ui.inw;

import org.apache.commons.lang.StringUtils;

import jdo.sys.PatTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.AnimationWindowUtils;
import com.sun.awt.AWTUtilities;



/**
 * Title: 交接一览表
 * Description:交接一览表
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class INWTransferSheetControl extends TControl {
	private TParm parm;
	private TTable table;
	private String runFlg = "";//设置系统代码
	private String srceenWidth = "";
	String transferfilepath = "";//交接单文件存储路径
	String transferfilename = "";//交接单文件存储名称
	private String mrNo;//病案号	
	private String caseNo;//就诊号	
	private String opBookSeq;//手术申请单号
	
	private TComboBox combobox;
    public void onInit() {
        super.onInit();
        parm = (TParm) getParameter();//查询信息
		//若无此信息返回
		if (null == parm) {
			return;
		}
		mrNo = parm.getValue("MR_NO");
		caseNo = parm.getValue("CASE_NO");
		opBookSeq = parm.getValue("OPBOOK_SEQ");
//		System.out.println("---mrNo----------------"+mrNo);
//		System.out.println("---caseNo----------------"+caseNo);
        table = (TTable) this.getComponent("TABLE");
        callFunction("UI|MR_NO|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onExeQuery");
        callFunction("UI|TABLE|addEventListener",
                "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        if(!mrNo.equals("")){
        	 onTableAdd(mrNo);
        }
    	   
        //带出数据
//        combobox = (TComboBox) this.getComponent("TRANSFER_CLASS");
//     // 设置系统代码
//		Object obj = this.getParameter();
//		if (obj != null) {
//			String strParameter = this.getParameter().toString();
//			String sysID = "";
//			// 包含;多个
//			if (strParameter.indexOf(";") != -1) {
//				sysID = strParameter.split(";")[0];
//				this.setSrceenWidth(strParameter.split(";")[1]);
//			} else {
//				sysID = this.getParameter().toString();
//			}
//
//			this.setRunFlg(sysID);
//		} else {
//			this.setRunFlg("");
//		}
//		// 根据护士类型初始化交接类型
//		this.setTransfer();
    }
    /**
	 * 根据护士类型初始化交接类型
	 */
    public void setTransfer() {
//    	[[id,text],[,],[WW,病区-病区],[WT,病区-介入],
//    	[WO,病区-手术室],[TC/TW,介入-CCU/病区],[OI,手术室-ICU]] 	
      if (this.getRunFlg().equals("W")) {
          combobox.setStringData("[[id,text],[,],[WW,病区-病区],[WT,病区-介入]," +
          		"[WO,病区-手术室],[TC/TW,介入-CCU/病区]]");	
      }else if (this.getRunFlg().equals("T")) {
          combobox.setStringData("[[id,text],[,],[WT,病区-介入],[TC/TW,介入-CCU/病区]]");	
      }else if (this.getRunFlg().equals("O")) {
          combobox.setStringData("[[id,text],[,],[WO,病区-手术室],[OI,手术室-ICU]]");	
      }else if (this.getRunFlg().equals("I")) {
          combobox.setStringData("[[id,text],[,],[OI,手术室-ICU]]");	
          this.setValue("TRANSFER_CLASS","OI");	
      }else if (this.getRunFlg().equals("C")) {
    	  combobox.setStringData("[[id,text],[,],[TC/TW,介入-CCU/病区]]");
    	  this.setValue("TRANSFER_CLASS","TC/TW");	
      }
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
//		if(this.getValue("START_DATE").equals("")){
//    		this.messageBox("交接时间不能为空");
//    		return true;
//    	}
//    	if(this.getValue("END_DATE").equals("")){
//    		this.messageBox("交接时间不能为空");
//    		return true;
//    	}
    	if(this.getValue("MR_NO").equals("")){
    		this.messageBox("病案号不能为空");
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
    	String mrno = PatTool.getInstance().checkMrno(
    			TypeTool.getString(getValue("MR_NO")));
//    	System.out.println("mrno=====:"+mrno);
    	if(!mrNo.equals("")&&!mrNo.equals(mrno)){
    		messageBox("非病患交接单,请核实病患信息");
    		 return;
    	} 
    	onTableAdd(mrno);
    	this.setValue("MR_NO", mrno);
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
  }   
    /**
     * 交接单
     */
    public void onTransfer() {
    	//数据检核
//    	if(checkdata())
//		    return;
    	int Row = table.getSelectedRow();//行数
		//若没有数据返回
		if (Row < 0){
			messageBox("请选择数据");
			  return;
		}
    	TParm action = new TParm();
    	TParm data = table.getParmValue().getRow(Row);//获得数据
//    	System.out.println("---data----------------"+data);
    	

    	
    	//病区-手术室WO
    	if(data.getValue("TRANSFER_CLASS_CODE").equals("WO")){
//	    	action.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//交接单号  		
//	    	//调用程序
//	    	action.setData("MR_NO", data.getValue("MR_NO"));//病案号 zhanglei 2017.05.02 增加日间手术标记
//			action.setData("CASE_NO", data.getValue("CASE_NO"));//就诊号 zhanglei 2017.05.02 增加日间手术标记
//			//this.messageBox("MR_NO" + action.getValue("MR_NO") + "--CASE_NO" + action.getValue("CASE_NO"));
//	     	this.openDialog("%ROOT%\\config\\inw\\INWTransferSheetWo.x", action);
	     	
	    	String opbookSeq = data.getValue("OPBOOK_SEQ");
	    	String sql = "SELECT TYPE_CODE FROM OPE_OPBOOk WHERE OPBOOK_SEQ = '"+data.getValue("OPBOOK_SEQ")+"'";
//	    	System.out.println(">>>>>>>>>>"+sql);
	    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	    	if(result == null || result.getCount() <= 0){
	    		this.messageBox("缺少数据");
	    		System.out.println("未在OPE_OPBOOk中查询到数据");
	    		return;
	    	}
	    	if("1".equals(result.getValue("TYPE_CODE", 0))){//手术室
	    		TParm parm = new TParm();
	    		parm.setData("OPBOOK_SEQ", opbookSeq);
	    		parm.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//交接单号  	
//	    		this.openWindow("%ROOT%/config/pda/PDAOpeConnectUI.x", parm);
	    		TFrame frame = new TFrame();
				frame.init(getConfigParm().newConfig(
						"%ROOT%/config/pda/PDAOpeConnectUI.x"));
				if (parm != null)
					frame.setParameter(parm);
				frame.onInit();
				frame.setSize(1012, 739);
				frame.setLocation(200, 5);
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
	    	}else if("2".equals(result.getValue("TYPE_CODE", 0))){//介入室
	    		action.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//交接单号  		
		    	//调用程序
		    	action.setData("MR_NO", data.getValue("MR_NO"));//病案号 zhanglei 2017.05.02 增加日间手术标记
				action.setData("CASE_NO", data.getValue("CASE_NO"));//就诊号 zhanglei 2017.05.02 增加日间手术标记
				//this.messageBox("MR_NO" + action.getValue("MR_NO") + "--CASE_NO" + action.getValue("CASE_NO"));
//				openWindow("%ROOT%\\config\\inw\\INWTransferSheetWo.x", action, true);
				TFrame frame = new TFrame();
				frame.init(getConfigParm().newConfig(
						"%ROOT%\\config\\inw\\INWTransferSheetWo.x"));
				if (action != null)
					frame.setParameter(action);
				frame.onInit();
				frame.setSize(1012, 739);
				frame.setLocation(200, 5);
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
//		     	this.openDialog("%ROOT%\\config\\inw\\INWTransferSheetWo.x", action);
	    	}
    	}else{   	
	    	action.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//交接单号
	    	action.setData("TRANSFER_FILE_PATH",
					data.getValue("TRANSFER_FILE_PATH"));//交接单文件存储路径
	    	action.setData("TRANSFER_FILE_NAME",
					data.getValue("TRANSFER_FILE_NAME"));//交接单文件存储名称
			action.setData("MR_NO", data.getValue("MR_NO"));//病案号
			action.setData("CASE_NO", data.getValue("CASE_NO"));//就诊号
			action.setData("PAT_NAME", data.getValue("PAT_NAME"));//姓名
			if (StringUtils.isEmpty(opBookSeq)) {
				opBookSeq = data.getValue("OPBOOK_SEQ");
			}
			action.setData("OPBOOK_SEQ", opBookSeq);//手术申请单号
			action.setData("FLG",true);//打开已生成病历标记
	//         System.out.println("---action----------------"+action);
	 	    //调用模版 
			TFrame frame = new TFrame();
			frame.init(getConfigParm().newConfig(
					"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
			if (action != null)
			frame.setParameter(action);
			frame.onInit();
			frame.setSize(1012, 739);
			frame.setLocation(200, 5);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
  		}
  		
		
    	 //刷新数据
 	    onTableAdd(data.getValue("MR_NO")); 	
  }   
    /**
     * 清空
     */
    public void onClear() {
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
 	    this.setValue("MR_NO","");
 	    this.setValue("TRANSFER_CLASS","");	
   	    this.setValue("STATUS_FLG","");
   	    this.setValue("FROM_DEPT","");
   	    this.setValue("TO_DEPT","");
//    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
//    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    
    /**
     * TABLE表双击事件
     */
   public void onSelect() {
	   onTransfer();
   }
    /**
     * 病案号回车事件
     */
    public void onExeQuery() {
       	//数据检核
    	if(checkdata())
		    return;
    	String mrno = PatTool.getInstance().checkMrno(
    			TypeTool.getString(getValue("MR_NO")));
//    	System.out.println("mrno=====:"+mrno);
    	if(!mrNo.equals("")&&!mrNo.equals(mrno)){
    		messageBox("非病患交接单,请核实病患信息");
    		 return;
    	} 
    	onTableAdd(mrno);
    	this.setValue("MR_NO", mrno);
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
    }
    /**
     * 病案号扫描事件
     */
    public void onScream() {
       	//数据检核
    	if(checkdata())
		    return; 
    	String mrno = PatTool.getInstance().checkMrno(
    			TypeTool.getString(getValue("MR_NO")));
    	if(!mrNo.equals("")&&!mrNo.equals(mrno)){
    		messageBox("非病患交接单,请核实病患信息");
    		 return;
    	} 
    	onTableAdd(mrno);
    	this.setValue("MR_NO", mrno);
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
    }
    
    public void onTableAdd(String mrno) {
//    	System.out.println("onTableAdd=====:1");
    	TParm result = new TParm();
    	String sql1 ="";
    	String sql2 ="";
    	String sql3 ="";
    	String sql4 ="";
    	String sql5 ="";
    	String transferclass = getValue("TRANSFER_CLASS").toString();//交接类型
//    	System.out.println("transferclass=====:"+transferclass);
		String statusflg = getValue("STATUS_FLG").toString();//交接状态
//		System.out.println("statusflg====="+statusflg);
		if(!transferclass.equals(""))
			sql1 =" AND A.TRANSFER_CLASS = '"+ transferclass + "'";				
		if(!statusflg.equals(""))
			sql2 =" AND A.STATUS_FLG = '"+ statusflg + "'";
//		System.out.println("FROM_DEPT====="+getValue("FROM_DEPT"));
		//转出科室
		if(this.getValue("FROM_DEPT")==null)			
		sql3 = "";
		else if(this.getValue("FROM_DEPT").equals(""))
		sql3 = "";
		else
		sql3 =" AND A.FROM_DEPT = '"+ getValue("FROM_DEPT") + "'";	
		//转入科室
		if(this.getValue("TO_DEPT")==null)			
			sql4 ="";
		else if(this.getValue("TO_DEPT").equals(""))
			sql4 ="";
		else
			sql4 =" AND A.TO_DEPT = '"+ getValue("TO_DEPT") + "'";	
		
		if(!caseNo.equals(""))
			sql5 =" AND A.CASE_NO = '"+ caseNo + "'";
//		String startDate = StringTool.getString(TypeTool
//				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
//		String endDate = StringTool.getString(TypeTool
//				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
//		System.out.println("onTableAdd=====:3");
		//查询病患
		String sql =" SELECT A.TRANSFER_CODE,A.TRANSFER_FILE_PATH," +
				" A.TRANSFER_FILE_NAME,A.CASE_NO," +
		" CASE WHEN A.TRANSFER_CLASS ='WW' THEN '病区-病区'"+
		" WHEN A.TRANSFER_CLASS ='WT' THEN '病区-介入'" + 
		" WHEN A.TRANSFER_CLASS ='WO' THEN '病区-手术室'" + 
		" WHEN A.TRANSFER_CLASS ='TC/TW' THEN '介入-CCU/病区'" + 
		/* modified by WangQing 20170518*/
		" WHEN A.TRANSFER_CLASS ='OC/OW' THEN '手术室-CCU/病区'" +
		" WHEN A.TRANSFER_CLASS ='OI' THEN '手术室-ICU'" +
		
		" WHEN A.TRANSFER_CLASS ='IW' THEN 'ICU-病区' " +
		" WHEN A.TRANSFER_CLASS ='ET' THEN '急诊-介入' " +
		" WHEN A.TRANSFER_CLASS ='EO' THEN '急诊-手术室' " +
		" WHEN A.TRANSFER_CLASS ='EW' THEN '急诊-病区' END AS  TRANSFER_CLASS," +
		" CASE WHEN A.STATUS_FLG ='4' THEN '待接收'" +
		" WHEN A.STATUS_FLG ='5' THEN '已接收' END AS  STATUS_FLG," +
		" B.DEPT_CHN_DESC AS FROM_DEPT,C.DEPT_CHN_DESC AS TO_DEPT," +
		" A.MR_NO,A.PAT_NAME,CASE WHEN D.SEX_CODE ='1' THEN '男'" +
		" WHEN D.SEX_CODE ='2' THEN '女' END AS SEX_CODE,E.USER_NAME AS CRE_USER," +
		" A.CRE_DATE,F.USER_NAME AS FROM_USER,G.USER_NAME AS TO_USER," +
		" A.TRANSFER_DATE,A.TRANSFER_CLASS AS TRANSFER_CLASS_CODE,A.OPBOOK_SEQ " +
		" FROM INW_TRANSFERSHEET A,SYS_DEPT B,SYS_DEPT C,SYS_PATINFO D,SYS_OPERATOR E," +
		" SYS_OPERATOR F,SYS_OPERATOR G" +
		" WHERE A.MR_NO = '"+ mrno + "'" +
		sql1+
		sql2+
		sql3+
		sql4+
		sql5+
		" AND A.FROM_DEPT = B.DEPT_CODE(+)" +
		" AND A.TO_DEPT = C.DEPT_CODE(+)" +
		" AND A.MR_NO = D.MR_NO" +
		" AND A.CRE_USER =E.USER_ID(+)" +
		" AND A.FROM_USER =F.USER_ID(+)" +
		" AND A.TO_USER =G.USER_ID(+)" +
		" ORDER BY A.CRE_DATE DESC";		
//		System.out.println("sql=====:"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result=====:"+result);
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
		TTextField no = ((TTextField) getComponent("MR_NO"));
		no.grabFocus();
    }
    /**
	 * 设置系统代码
	 */
	public void setRunFlg(String runFlg) {
		this.runFlg = runFlg;
	}
	/**
	 * 得到系统代码
	 */
	public String getRunFlg() {
		return runFlg;
	}
	public String getSrceenWidth() {
		return srceenWidth;
	}

	public void setSrceenWidth(String srceenWidth) {
		this.srceenWidth = srceenWidth;
	}
}
