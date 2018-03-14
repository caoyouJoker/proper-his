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
 * <p>Title: �������</p>
 *
 * <p>Description: �������</p>
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
    //�������
    
    String caseNo = "";
    public void onInit() {
    	
        super.onInit();
        //�˵�����tableר�õļ���
        getTTable("TableM").addEventListener(TTableEvent.
                                             CHECK_BOX_CLICKED, this,
                                             "onTableMComponent");
        onClear();
    }

    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        if (getValue("MR_NO").equals(null) ||
            getValue("MR_NO").toString().length() == 0) {
            this.messageBox("�����벡����");
            return;
        }
        String mrNo = PatTool.getInstance().checkMrno(TCM_Transform.
                getString(getValue("MR_NO")));
        setValue("MR_NO",mrNo);
        
        //modify by huangtt 20160928 EMPI���߲�����ʾ  start
        Pat pat = Pat.onQueryByMrNo(mrNo);        
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
            setValue("MR_NO",pat.getMrNo());
        }
        //modify by huangtt 20160928 EMPI���߲�����ʾ  end
        
        
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
        if(admInpParm.getCount() > 1 ){//==modify by caowl 20120911 ȥ��admInpParm.getCount() < 1
    		TParm inparm = new TParm();
    		inparm.setData("MR_NO", pat.getMrNo());
    		inparm.setData("PAT_NAME", pat.getName());
    		inparm.setData("SEX_CODE", pat.getSexCode());
    		inparm.setData("AGE", age);
    		// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
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
            this.messageBox("���������!");
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
     * ��˱���
     */
    public void onSave() {
        boolean flg = IBSTool.getInstance().checkData(caseNo); 
       
        if (!flg){
        	String sqlAdm = "SELECT COUNT(CASE_NO) AS COUNT FROM ADM_INP WHERE DS_DATE IS NULL AND CASE_NO = '"+caseNo+"'";
        	TParm parmAdm = new TParm(TJDODBTool.getInstance().select(sqlAdm));
        	//��Ժ
        	if(parmAdm.getInt("COUNT",0)==1){
        		this.messageBox("����δ�����˵���ҽ����Ϣ");         		
        	}else{
        	//��Ժ
        		this.messageBox("����δ�����˵���ҽ����Ϣ");  
        		onQuery();
        		return;
        	}       	          	
        }            
        TParm actionParm = new TParm();
        TParm result = new TParm();
     
        if(endParm == null ){
        	this.messageBox("û��Ҫ���µ��˵�");
        	//System.out.println("û��Ҫ���µ��˵�  endParm==null");      
        	return;
        }
       
        if((endParm.getCount("BILL_NO") == -1 )){
        	this.messageBox("û��Ҫ���µ��˵�");
        	//System.out.println("û��Ҫ���µ��˵� endParm.getCount() == -1");
        	return;
        }
        int count = endParm.getCount("BILL_NO");  
        // System.out.println("endParm:::"+endParm);   
       // System.out.println("ѭ����ʼ");
        
        // add by wangb 2017/8/1 У���˵���ϸ�����Ƿ�һ�� START
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
            // У���˵���ϸ�����Ƿ�һ��
            checkParm = IBSTool.getInstance().checkIbsBillAmount(checkParm);
            if (checkParm.getErrCode() < 0) {
            	this.messageBox(checkParm.getErrText());
            	return;
            }
        }
        // add by wangb 2017/8/1 У���˵���ϸ�����Ƿ�һ�� END
        
        for (int i = 0; i < count; i++) {
        	//System.out.println("��"+i+"��ѭ��ִ��");
            String billNo = "";
            billNo = endParm.getValue("BILL_NO", i);
            actionParm.setData("BILL_NO", billNo);
            //======modify by caowl 20120911 start
            boolean approve_flg = endParm.getBoolean("APPROVE_FLG",i); 
           // System.out.println("approve_flg:::"+approve_flg);
            if(approve_flg){
            	//���            	
            	//System.out.println("���ִ��");
            	
            	actionParm.setData("APPROVE_FLG", "Y");
                result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                                      "onSaveAuditFee", actionParm);  
                //System.out.println("���ִ�н��result:::"+result);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return;
                }
            }else{
            	
            	//ȡ�����    
            	//�ж��Ƿ��Ѿ��걨
            	//System.out.println("ȡ�����");
            	String sql = "SELECT IN_STATUS FROM INS_ADM_CONFIRM WHERE CASE_NO = '"+caseNo+"'";
            	
            	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));      
            	if(selParm != null && selParm.getData("IN_STATUS",0) !=null && selParm.getData("IN_STATUS",0).equals("2")){
            		this.messageBox("��δȡ���걨��");
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
           // System.out.println("ѭ������");
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
    	            //ִ��ʧ��
    	            this.messageBox("E0005");
    	            return;
    	        }
    	}
    	
      
        //ִ�гɹ�
        this.messageBox("P0005");
        this.onClear();
    }

    /**
     * ȡ�����
     */
    public void onReturn() {

        boolean flg = IBSTool.getInstance().checkData(caseNo);
        if (!flg) {
            this.messageBox("��δ�����˵���ҽ����Ϣ,�����²����˵�");
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
            //ִ��ʧ��
            this.messageBox("E0005");
            return;
        }
        //ִ�гɹ�
        this.messageBox("P0005");
        this.onClear();

    }

    /**
     * ��������¼�
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
     * ����ĵ����¼�
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
     * ���
     */
    public void onClear() {
        clearValue("REDUCE_REASON;IPD_NO;MR_NO;PAT_NAME");
        this.callFunction("UI|TableM|removeRowAll");
        this.callFunction("UI|TableD|removeRowAll");
        selectRow = -1;
    }

    /**
     * У������
     * @param flg String
     */
    public void checkData(String flg) {
    }
}
