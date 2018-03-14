package com.javahis.ui.ins;

import java.awt.TextField;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.ins.INSNoticeTool;
import jdo.ins.INSONWUploadTool;
import jdo.ins.InsManager;
import jdo.reg.RegMethodTool;
import jdo.sys.Operator;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.DateUtil;

/**
 * 
 * <p>
 * Title: 门诊日门诊量上传
 * </p>
 * 
 * <p>
 * Description:门诊日门诊量上传
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSONWUploadControl extends TControl {
	TParm data;
	int selectRow = -1;
	/**
	 * 下载权限
	 */
	public static String INS_NOTICE_CONTROL_DOWNLOAD = "INS_NOTICE_CONTROL_DOWNLOAD";
	/**
	 * 查询权限
	 */
	public static String INS_NOTICE_CONTROL_SREACH = "INS_NOTICE_CONTROL_SREACH";
	/**
	 * 当前用户是否拥有下载权限
	 */
	boolean isPossessDownload = false;

	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	public void onInit() {
		super.onInit();

		
		onClear();

	}

	/**
	 * 设置当前登录用户权限
	 */
	private void setPossessDownload() {
		boolean isPossessDownload=this.getPopedem("LEADER");
		// 默认权限 查询
		//isPossessDownload = false;
		// 下载权限
		//isPossessDownload = true;

	}



	


	/**
	 * 查询
	 */
	public void onQuery() {
		if(getText("STATISTICS_DATE").trim().equals("")){
			this.messageBox("请输入统计日期！");
			return ;
		}
		//医保	insurance
		//门诊	outpatient
		TParm queryTParm=new TParm();
		TParm showTparm=new TParm();
		Map showMap=new HashMap();
		queryTParm.setData("STATISTICS_DATE", getText("STATISTICS_DATE"));
		queryTParm.setData("REGION_CODE", Operator.getRegion());
		
		//取医保非门诊联网挂号人次
		TParm insNoOutTparmCount =INSONWUploadTool.getInstance().selectInsNoOutCount(queryTParm);
		//取医保非门诊联网信息
		TParm insNoOutTparm =INSONWUploadTool.getInstance().selectInsNoOut(queryTParm);
		//取非医保门诊挂号人次
		TParm outNoInsCount =INSONWUploadTool.getInstance().selectOutNoInsCount(queryTParm);
		//取非医保门诊信息
		TParm outNoInsTparm =INSONWUploadTool.getInstance().selectOutNoIns(queryTParm);
		
		//取门诊医保联网caseNo
		//TParm caseNoTparm =INSONWUploadTool.getInstance().selectCaseNoTparm(queryTParm);
		//取医保门诊联网人次
		TParm insOutCount =INSONWUploadTool.getInstance().selectInsOutCount(queryTParm);
		//取医保门诊联网信息
		TParm insOutTparm =INSONWUploadTool.getInstance().selectInsOut(queryTParm);
		
		
		showMap.putAll((Map)insNoOutTparmCount.getRow(0).getData().get("Data"));
		showMap.putAll((Map)insNoOutTparm.getRow(0).getData().get("Data"));
		showMap.putAll((Map)outNoInsCount.getRow(0).getData().get("Data"));
		showMap.putAll((Map)outNoInsTparm.getRow(0).getData().get("Data"));
		showMap.putAll((Map)insOutCount.getRow(0).getData().get("Data"));
		showMap.putAll((Map)insOutTparm.getRow(0).getData().get("Data"));
		//System.out.println("showMap==="+showMap);
//		showTparm.addParm(insOutTparm);
//		showTparm.addParm(outNoInsTparm);
		for(int i=1;i<=15;i++){
			try{
			((TTextField) getComponent("TTEXTFIELD_NO"+i)).setText(showMap.get("TTEXTFIELD_NO"+i).toString());
		//	System.out.println(showMap.get("TTEXTFIELD_NO"+i));
			}catch(Exception e){
				
			}
		}
		
	}
    /**
     * 打印
     */
    public void onPrint(){
        TParm printData = new TParm();
       

        TParm printParm = new TParm();
        printParm.setData("TITLE", "TEXT","");
        
        printParm.setData("STATISTICS_DATE","TEXT",getText("STATISTICS_DATE"));
        for(int i=1;i<=15;i++){
			 printParm.setData("tTextField_NO"+i,"TEXT",getText("tTextField_NO"+i));
			
		}
        this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSONWUploadReport.jhw",printParm);

    }


	/**
	 * 调用接口 InsManager 中 L方法
	 * 
	 * @param parm
	 * @return
	 */
	private TParm insManagerL() {
		TParm parm =new TParm();
		for(int i=1;i<=15;i++){
			parm.addData("NO"+i, getText("TTEXTFIELD_NO"+i));
		}
		parm.addData("PARM_COUNT", 15);
		parm.setData("PIPELINE", "DataDown_sp");
		parm.setData("PLOT_TYPE", "Z");

		//TParm result = null;
		TParm result = InsManager.getInstance().safe(parm,"");

		return result;
	}

	private TParm testInsManagerL(TParm parmSreach) {
		

		return null;
	}

	/**
	 * 上传
	 */
	public void onUpload() {
		// clearValue("START_DATE;END_DATE");

		TParm result = insManagerL();
		//if(result!=null && result.getValue("PROGRAM_STATE").equals("Y")){
			this.messageBox("日门诊量上传成功。");
		//}else{
		//	this.messageBox("日门诊量上传失败！");
		//}
	}
	/**
	 * 清空
	 */
	public void onClear() {
		// clearValue("START_DATE;END_DATE");
		for(int i=1;i<=15;i++){
			((TTextField) getComponent("tTextField_NO"+i)).setText("");
			
		}
		this.setValue("STATISTICS_DATE", DateUtil.getNowTime("yyyy/MM/dd"));
	}

public static void main(String[] args) {
	 String sql =
		 " SELECT A.REXP_CODE,SUM(A.WRT_OFF_AMT) " +
         " FROM BIL_WRT_OFF A, BIL_RECPM B" +
         " WHERE B.HOSP_AREA = ''" +
         " AND B.CASE_NO IN ()" +
         " AND B.REFUND_FLG = 'N' " +
         " AND B.OFFRECEIPT_NO IS NULL " +
         " AND A.HOSP_AREA = B.HOSP_AREA " +
         " AND A.RECEIPT_NO = B.RECEIPT_NO " +
         " GROUP BY A.REXP_CODE";
	 //System.out.println(sql);
}
}
