package com.javahis.ui.bil;

import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import jdo.ibs.IBSBillmTool;
import jdo.adm.ADMInpTool;
import jdo.ibs.IBSOrderdTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

import jdo.ibs.IBSTool;

/**
 * <p>Title: 费用审核</p>
 *
 * <p>Description: 费用审核</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl  2010.06.12
 * @version 1.0
 */
public class BILAuditFeeControl extends TControl {
    TParm data;
    int selectRow = -1;
    TParm endParm;
    //就诊序号
    
    String caseNo = "";
    public void onInit() {
    	
        super.onInit();
        //账单主档table专用的监听
        getTTable("TableM").addEventListener(TTableEvent.
                                             CHECK_BOX_CLICKED, this,
                                             "onTableMComponent");
        onClear();
    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 查询
     */
    public void onQuery() {
        if (getValue("MR_NO").equals(null) ||
            getValue("MR_NO").toString().length() == 0) {
            this.messageBox("请输入病案号");
            return;
        }
        String mrNo = PatTool.getInstance().checkMrno(TCM_Transform.
                getString(getValue("MR_NO")));
        setValue("MR_NO",mrNo);
        
        //modify by huangtt 20160928 EMPI患者查重提示  start
        Pat pat = Pat.onQueryByMrNo(mrNo);        
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
            setValue("MR_NO",pat.getMrNo());
        }
        //modify by huangtt 20160928 EMPI患者查重提示  end
        
        
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        TParm selPatInfo = PatTool.getInstance().getInfoForMrno(getValueString(
                "MR_NO"));
        if (selPatInfo.getErrCode() < 0) {
            err(selPatInfo.getErrName() + " " + selPatInfo.getErrText());
            return;
        }
        setValue("PAT_NAME", selPatInfo.getValue("PAT_NAME", 0));
        //===zhangp 20120815 start
        String sql =
        	" SELECT CASE_NO, IPD_NO" +
        	" FROM ADM_INP" +
        	" WHERE MR_NO = '" + this.getValueString("MR_NO")+"'" ;//==modify by caowl 20120911
        TParm admInpParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (admInpParm.getErrCode() < 0) {
            err(admInpParm.getErrName() + " " + admInpParm.getErrText());
            return;
        }
        if(admInpParm.getCount() == 1){
        	caseNo = admInpParm.getValue("CASE_NO", 0);
        }
//        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate());
        if(admInpParm.getCount() > 1 ){//==modify by caowl 20120911 去掉admInpParm.getCount() < 1
    		TParm inparm = new TParm();
    		inparm.setData("MR_NO", pat.getMrNo());
    		inparm.setData("PAT_NAME", pat.getName());
    		inparm.setData("SEX_CODE", pat.getSexCode());
    		inparm.setData("AGE", age);
    		// 判断是否从明细点开的就诊号选择
    		inparm.setData("count", "0");
    		TParm caseNoParm = (TParm) openDialog(
    				"%ROOT%\\config\\bil\\BILChooseVisit.x", inparm);
    		caseNo = caseNoParm.getValue("CASE_NO");
        }
        parm.setData("CASE_NO",caseNo);
        //===zhangp 20120815 end
        setValue("IPD_NO", admInpParm.getValue("IPD_NO", 0));
        TParm billmParm = IBSBillmTool.getInstance().selAuditFee(parm);
        if (billmParm.getErrCode() < 0) {
            err(billmParm.getErrName() + " " + billmParm.getErrText());
            return;
        }
        if (billmParm.getCount() <= 0) {
            this.messageBox("无审核数据!");
            return;
        }
        //===modify by caowl 20120911 start
        for(int i =0;i<billmParm.getCount();i++){
        	billmParm.setData("UPDFLG",i,this.getValueBoolean("UPDFLG"));
        }  
        //====modify by caowl 20120911 end
        this.getTTable("TableM").setParmValue(billmParm);
    }


    /**
     * 审核保存
     */
    public void onSave() {
        boolean flg = IBSTool.getInstance().checkData(caseNo); 
       
        if (!flg){
        	String sqlAdm = "SELECT COUNT(CASE_NO) AS COUNT FROM ADM_INP WHERE DS_DATE IS NULL AND CASE_NO = '"+caseNo+"'";
        	TParm parmAdm = new TParm(TJDODBTool.getInstance().select(sqlAdm));
        	//在院
        	if(parmAdm.getInt("COUNT",0)==1){
        		this.messageBox("还有未产生账单的医嘱信息");         		
        	}else{
        	//出院
        		this.messageBox("还有未产生账单的医嘱信息");  
        		onQuery();
        		return;
        	}       	          	
        }            
        TParm actionParm = new TParm();
        TParm result = new TParm();
     
        if(endParm == null ){
        	this.messageBox("没有要更新的账单");
        	//System.out.println("没有要更新的账单  endParm==null");      
        	return;
        }
       
        if((endParm.getCount("BILL_NO") == -1 )){
        	this.messageBox("没有要更新的账单");
        	//System.out.println("没有要更新的账单 endParm.getCount() == -1");
        	return;
        }
        int count = endParm.getCount("BILL_NO");  
        // System.out.println("endParm:::"+endParm);   
       // System.out.println("循环开始");
        
        // add by wangb 2017/8/1 校验账单主细项金额是否一致 START
        String billNoList = "";
        for (int j = 0; j < count; j++) {
        	if ("Y".equals(endParm.getValue("APPROVE_FLG", j))) {
        		billNoList = billNoList + endParm.getValue("BILL_NO", j) + "','";
        	}
        }
        if (billNoList.length() > 0) {
        	billNoList = billNoList.substring(0, billNoList.length() - 3);
            TParm checkParm = new TParm();
            checkParm.setData("BILL_NO", billNoList);
            // 校验账单主细项金额是否一致
            checkParm = IBSTool.getInstance().checkIbsBillAmount(checkParm);
            if (checkParm.getErrCode() < 0) {
            	this.messageBox(checkParm.getErrText());
            	return;
            }
        }
        // add by wangb 2017/8/1 校验账单主细项金额是否一致 END
        
        for (int i = 0; i < count; i++) {
        	//System.out.println("第"+i+"次循环执行");
            String billNo = "";
            billNo = endParm.getValue("BILL_NO", i);
            actionParm.setData("BILL_NO", billNo);
            //======modify by caowl 20120911 start
            boolean approve_flg = endParm.getBoolean("APPROVE_FLG",i); 
           // System.out.println("approve_flg:::"+approve_flg);
            if(approve_flg){
            	//审核            	
            	//System.out.println("审核执行");
            	
            	actionParm.setData("APPROVE_FLG", "Y");
                result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                                      "onSaveAuditFee", actionParm);  
                //System.out.println("审核执行结果result:::"+result);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return;
                }
            }else{
            	
            	//取消审核    
            	//判断是否已经申报
            	//System.out.println("取消审核");
            	String sql = "SELECT IN_STATUS FROM INS_ADM_CONFIRM WHERE CASE_NO = '"+caseNo+"'";
            	
            	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));      
            	if(selParm != null && selParm.getData("IN_STATUS",0) !=null && selParm.getData("IN_STATUS",0).equals("2")){
            		this.messageBox("还未取消申报！");
                	return;
            	}
                actionParm.setData("APPROVE_FLG", "N");
                result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                                      "onSaveAuditFee", actionParm);               
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return;
                }
            }
            //=====modify by caowl 20120911 end 
           // System.out.println("循环结束");
        }
        String sql = "SELECT BILL_STATUS FROM ADM_INP WHERE CASE_NO ='"+caseNo+"'";
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String bill_status = selParm.getValue("BILL_STATUS",0);
    	if(bill_status.equals("0")){
    		
    	}else{
    		  TParm acParm = new TParm();
    	       acParm.setData("CASE_NO", caseNo);
    	       result = TIOM_AppServer.executeAction("action.bil.BILAction",
    	                                              "onAuditFeeCheck", acParm);
    	        if (result.getErrCode() < 0) {
    	            err(result.getErrName() + " " + result.getErrText());
    	            //执行失败
    	            this.messageBox("E0005");
    	            return;
    	        }
    	}
    	
      
        //执行成功
        this.messageBox("P0005");
        this.onClear();
    }

    /**
     * 取消审核
     */
    public void onReturn() {

        boolean flg = IBSTool.getInstance().checkData(caseNo);
        if (!flg) {
            this.messageBox("有未产生账单的医嘱信息,请重新产生账单");
            return;
        }
        TParm actionParm = new TParm();
        TParm result = new TParm();
        int count = endParm.getCount("BILL_NO");
        for (int i = 0; i < count; i++) {
            String billNo = "";
            billNo = endParm.getValue("BILL_NO", i);
            actionParm.setData("BILL_NO", billNo);
            actionParm.setData("APPROVE_FLG", "N");
            result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                                  "onSaveAuditFee", actionParm);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return;
            }
        }
        TParm acParm = new TParm();
        acParm.setData("CASE_NO", caseNo);
        result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                              "onAuditFeeCheck", acParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            //执行失败
            this.messageBox("E0005");
            return;
        }
        //执行成功
        this.messageBox("P0005");
        this.onClear();

    }

    /**
     * 主表监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableMComponent(Object obj) {
        TTable tableM = (TTable) obj;
        tableM.acceptText();
        TParm tableParm = tableM.getParmValue();
        endParm = new TParm();
        int count = tableParm.getCount("BILL_NO");
        //System.out.println("count::"+count);
        for (int i = 0; i < count; i++) {
        	//System.out.println("tableParm.getData(,i)::"+tableParm.getData("UPDFLG",i));
        	if(tableParm.getData("UPDFLG",i).equals("Y")){//modify by caowl 20120911        		
        		endParm.addData("APPROVE_FLG", tableParm.getValue("APPROVE_FLG",i));//modify by caowl 20120911
                endParm.addData("BILL_NO",
                                tableParm.getValue("BILL_NO", i));
                endParm.addData("AR_AMT",
                                tableParm.getValue("AR_AMT", i));
            }
        }
        int feeCount = endParm.getCount("AR_AMT");
        double totAmt = 0.00;
        for (int j = 0; j < feeCount; j++) {
            totAmt = totAmt + endParm.getDouble("AR_AMT", j);
        }
        return true;
    }

    /**
     * 主表的单击事件
     */
    public void onTableMClicked() {
        TTable TableM = getTTable("TableM");
        TTable TableD = getTTable("TableD");
        int row = TableM.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = TableM.getParmValue();
        parm.setData("BILL_NO", regionParm.getData("BILL_NO", row));
        parm.setData("CASE_NO", regionParm.getData("CASE_NO", row));//=====caowl 20120927
        TParm data = IBSOrderdTool.getInstance().selAuditFeeData(parm);
        TableD.setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("REDUCE_REASON;IPD_NO;MR_NO;PAT_NAME");
        this.callFunction("UI|TableM|removeRowAll");
        this.callFunction("UI|TableD|removeRowAll");
        selectRow = -1;
    }

    /**
     * 校验数据
     * @param flg String
     */
    public void checkData(String flg) {
    }
}
