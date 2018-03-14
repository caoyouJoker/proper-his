package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.adm.ADMTool;
import jdo.sys.SystemTool;
import jdo.adm.ADMInpTool;
import javax.swing.SwingUtilities;
import jdo.adm.ADMDrResvOutTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;

/**
 * <p>Title: 出院通知</p>
 *
 * <p>Description: 出院通知</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2010-2-25
 * @version 4.0
 */
public class ADMDrResvOutControl
extends TControl {
	String CASE_NO = "";//记录就诊序号
	TParm allParm ;
	TParm admInfo;//病患在院信息
	boolean IS_MEDDISCH = false;//记录是否已经下达住院通知
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		onPageInit();
	}
	
	/**
	 * 页面初始化
	 */
	private void onPageInit(){
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox_("参数不可为空");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						closeW();//关闭窗口
					}
					catch (Exception e) {
					}
				}
			});
			return;
		}
		if(obj instanceof String){
			CASE_NO = (String)obj;
		}
		if(obj instanceof TParm){
			allParm = (TParm)obj;
			// modified by WangQing 20170510
			CASE_NO = allParm.getValue("CASE_NO");
		}
		//查询病患在院信息
		TParm admParm = new TParm();
		admParm.setData("CASE_NO",CASE_NO);
		admInfo = ADMTool.getInstance().getADM_INFO(admParm);
		if(admInfo.getData("MEDDISCH_DATE",0)!=null){
			IS_MEDDISCH = true;
		}
		//查询病患 医疗出院日期 如果病人有数据则赋值 否则赋当前时间
		if(admInfo.getValue("MEDDISCH_DATE",0).length()>0){
			this.setValue("MEDDISCH_DATE",admInfo.getData("MEDDISCH_DATE",0));
		}else{
			this.setValue("MEDDISCH_DATE",SystemTool.getInstance().getDate());
		}
		if(admInfo.getValue("DISCH_CODE",0).length()>0){
			this.setValue("DISCH_CODE",admInfo.getValue("DISCH_CODE",0));
		}
		//查询病患诊断信息
		TParm diagParm = new TParm();
		diagParm.setData("IO_TYPE","O");//出院诊断
		diagParm.setData("CASE_NO",CASE_NO);
		diagParm.setData("MAINDIAG_FLG","Y");//主诊断
		TParm diagInfo = ADMDrResvOutTool.getInstance().selectDiag(diagParm);
		if(diagInfo.getCount()>0){
			this.setValue("OUT_ICD",diagInfo.getValue("ICD_CODE",0));
			this.setValue("OUT_DESC",diagInfo.getValue("ICD_CHN_DESC",0));
		}
	}
	/**
	 * 保存
	 */
	public void onSave(){
		if(this.getValueString("OUT_ICD").length()<=0){
			this.messageBox_("请先填写该病患的出院诊断");
			return;
		}
		if(this.getValue("MEDDISCH_DATE")==null){
			this.messageBox_("请选择预定出院日期");
			this.grabFocus("MEDDISCH_DATE");
			return;
		}
		if(this.getValueString("DISCH_CODE").length()<=0){
			this.messageBox_("请选择出院情况");
			this.grabFocus("DISCH_CODE");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MEDDISCH_DATE",this.getValue("MEDDISCH_DATE"));
		parm.setData("DISCH_CODE",this.getValue("DISCH_CODE"));
		parm.setData("CASE_NO",CASE_NO);
		TParm re = ADMInpTool.getInstance().updateMEDDISCH_DATE(parm);
		if(re.getErrCode()<0){
			this.messageBox("E0005");
			return;
		}
		IS_MEDDISCH = true;
		this.messageBox("保存成功");
		this.closeWindow();		
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("DISCH_CODE");
		this.setValue("MEDDISCH_DATE",SystemTool.getInstance().getDate());
	}
	/**
	 * 打印
	 */
	public void onPrint(){
		if(!IS_MEDDISCH){
			this.messageBox_("尚未下达出院通知");
			return;
		}
		//查询打印信息
		TParm print = ADMDrResvOutTool.getInstance().selectPrintInfo(CASE_NO);
		//查询入院主诊断
		TParm inDiagParm = new TParm();
		inDiagParm.setData("IO_TYPE","M");//入院诊断
		inDiagParm.setData("CASE_NO",CASE_NO);
		inDiagParm.setData("MAINDIAG_FLG","Y");//主诊断
		TParm inDiag = ADMDrResvOutTool.getInstance().selectDiag(inDiagParm);
		//查询出院主诊断
		TParm outDiagParm = new TParm();
		outDiagParm.setData("IO_TYPE","O");//入院诊断
		outDiagParm.setData("CASE_NO",CASE_NO);
		outDiagParm.setData("MAINDIAG_FLG","Y");//主诊断
		TParm outDiag = ADMDrResvOutTool.getInstance().selectDiag(outDiagParm);
		TParm printData = new TParm();
		printData.setData("MR_NO","TEXT",admInfo.getValue("MR_NO",0));
		printData.setData("IPD_NO","TEXT",admInfo.getValue("IPD_NO",0));
		printData.setData("CTZ1","TEXT",print.getValue("CTZ_DESC",0));//付款方式
		printData.setData("PAT_NAME","TEXT",print.getValue("PAT_NAME",0));//患者姓名
		printData.setData("SEX","TEXT",print.getValue("CHN_DESC",0));//性别
		printData.setData("BIRTHDAY","TEXT",StringTool.getString(print.getTimestamp("BIRTH_DATE",0),"yyyy年MM月dd日"));//生日
		printData.setData("DEPT","TEXT",print.getValue("DEPT_CHN_DESC",0));//部门
		printData.setData("IN_DATE","TEXT",StringTool.getString(print.getTimestamp("IN_DATE",0),"yyyy年MM月dd日HH时"));//入院时间
		//计算住院天数
		//int inDays = StringTool.getDateDiffer(SystemTool.getInstance().getDate(),print.getTimestamp("IN_DATE",0));
		//$$ add by lx  this.getValue("MEDDISCH_DATE")
		//System.out.println("==MEDDISCH_DATE=="+this.getValue("MEDDISCH_DATE"));
		// Timestamp outdate=StringTool.getTimestamp((String)this.getValue("MEDDISCH_DATE"), "yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//
		String strOut = df.format((Timestamp)this.getValue("MEDDISCH_DATE"));
		//System.out.println("==strOut=="+strOut);
		strOut=strOut.substring(0, strOut.indexOf(" "))+" "+"23:59:59";
		//this.messageBox("==strOut=="+strOut);
		Timestamp tOut = Timestamp.valueOf(strOut.replaceAll("/", "-"));

		int inDays = StringTool.getDateDiffer(tOut,print.getTimestamp("IN_DATE",0));
		//this.messageBox("inDays"+inDays);

		printData.setData("IN_DAYS","TEXT",inDays==0?"1":inDays +" 天");
		printData.setData("PAT_HEAD","TEXT",print.getValue("PAT_NAME",0) + " 患者/亲属");
		printData.setData("IN_DESC","TEXT",inDiag.getValue("ICD_CHN_DESC",0));
		printData.setData("OUT_DESC","TEXT",outDiag.getValue("ICD_CHN_DESC",0));
		printData.setData("HOSP","TEXT",Operator.getHospitalCHNFullName());
		this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMDrResvOut.jhw",printData);
	}
	/**
	 * 关闭窗口
	 */
	private void closeW(){
		this.closeWindow();
	}
	/**
	 * 胸痛转归
	 * @author WangQing 2017/1/11
	 */
	public void onCPCOoutcome(){    	
		TParm sysParm = new TParm();
		String dischCode = this.getValueString("DISCH_CODE");
		sysParm.setData("CASE_NO",CASE_NO);
		sysParm.setData("DISCH_CODE",dischCode);
		System.out.println("---------this is only a test----------");
		// modified by WangQing 20170510
		this.openDialog("%ROOT%\\config\\ami\\AMIOutcom.x", sysParm);
	}

}
