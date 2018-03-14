package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

import jdo.adm.ADMInpTool;
import jdo.sta.STAOutRecallTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import java.util.Date;

import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: �վ��ٻ��˷Ѵ���</p>
 *
 * <p>Description: �վ��ٻ��˷Ѵ���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpReturnDetailControl extends TControl {
    String caseNo = "";
    String sexCode = "";
    public void onInit() {
        super.onInit();
        TParm parm = (TParm)this.getParameter();
        setValue("RECEIPT_NO", parm.getValue("RECEIPT_NO", 0));
        setValue("MR_NO", parm.getValue("MR_NO", 0));
        setValue("IPD_NO", parm.getValue("IPD_NO", 0));
        setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        setValue("AR_AMT", parm.getValue("AR_AMT", 0));
        caseNo = parm.getValue("CASE_NO", 0);
        sexCode = parm.getValue("SEX_CODE", 0);
    }

    /**
     * �˷�
     */
    public void onSave() {
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO", this.getValueString("RECEIPT_NO"));
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("IPD_NO", this.getValueString("IPD_NO"));
        parm.setData("CASE_NO", caseNo);
        parm.setData("AR_AMT", this.getValueString("AR_AMT"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        if (this.getValueBoolean("FLG"))
            parm.setData("FLG", "Y");
        else
            parm.setData("FLG", "N");
        TParm actionParm = new TParm();
        actionParm.setData("DATA", parm.getData());
        TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
                "onSaveReceiptReturn", actionParm);
        String preAmt = result.getValue("PRE_AMT");
        String payType = result.getValue("PAY_TYPE");
        String invNo = result.getValue("INV_NO");
        String stationCode = result.getValue("STATION_CODE");
        String deptCode = result.getValue("DEPT_CODE");
        String recpNo = result.getValue("RECEIPT_NO");
//        System.out.println("�س�Ԥ�������" + preAmt + "|" + invNo + "|" + recpNo +
//                           "|" + stationCode + "|" + deptCode);
        if (recpNo.length() > 0) {
        	printBillPay(preAmt, payType, recpNo, stationCode, deptCode);
        }
            
        this.messageBox("Ʊ��" + result.getValue("INV_NO") + "���ջ�");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return;
        } else {
        	 this.setReturnValue("true");
        	 insertRecallRecord();  //add by wukai on 20160921 ����һ����Ժ�ٻ�
        }
           
        this.closeWindow();
    }
    
    /**
     * ����һ���ٻؼ�¼
     */
    public void insertRecallRecord() {
    	TParm parm = new TParm();
    	parm.setData("MR_NO", this.getValueString("MR_NO"));
    	parm.setData("CASE_NO", caseNo);
    	//System.out.println("caseNo::::::::::: " + caseNo);
    	TParm admParm = ADMInpTool.getInstance().selectall(parm);//ADM_INP��ȡ������Ϣ
    	if(admParm.getErrCode() < 0) {
    		err(admParm.getErrName() + " " + admParm.getErrText());
    		return;
    	} else {
    		//System.out.println("admParm ::::::::::: " + admParm);
    		TParm recallParm = admParm.getRow(0);
    		//System.out.println("recallParm ::::::::::: " + recallParm);
    		recallParm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
    		recallParm.setData("REFUND_DATE", StringTool.getTimestamp(new Date()));
    		recallParm.setData("REFUND_CODE", Operator.getID());
    		if (this.getValueBoolean("FLG")) {
    			recallParm.setData("RECALL_TYPE", "01A");  //ҽ���ٻ�
    		} else {
    			recallParm.setData("RECALL_TYPE", "01B"); //�����ٻ�
    		}
    		STAOutRecallTool.getNewInstance().insertRecall(recallParm);
    		//System.out.println("insertReslut :::::  " + STAOutRecallTool.getNewInstance().insertRecall(recallParm));
    	}
    	
    }
    
    /**
     * �س��Ʊ
     * @param preAmt String
     * @param payType String
     * @param recpNo String
     * @param stationCode String
     * @param deptCode String
     */
    public void printBillPay(String preAmt, String payType, String recpNo,
                             String stationCode, String deptCode) {
        //��ӡԤ�����վ�
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
                getDouble(preAmt));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        forPrtParm.setData("Data", "TEXT", "����:" + pDate);
        forPrtParm.setData("Name", "TEXT",
                           "����:" + this.getValueString("PAT_NAME"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", "�շ�Ա:" + Operator.getName());
        forPrtParm.setData("MR_N0", "TEXT",
                           "������:" + this.getValueString("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT",
                           "סԺ��:" + this.getValueString("IPD_NO"));
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "��");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "Ů");
        else
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "����");
        forPrtParm.setData("DEPT", "TEXT",
                           "�������:" + getDeptDesc(deptCode));
        forPrtParm.setData("STATION", "TEXT",
                           "���ﲡ��:" + getStationDesc(stationCode));
        forPrtParm.setData("Capital", "TEXT", "(��д):" + bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",
                           "(Сд):" +
                           formatObject.format(
                                   TypeTool.getDouble(preAmt)));
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "��ˮ��:" + recpNo);
        forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);
        if (payType.equals("PAY_CASH")) {
            forPrtParm.setData("CASH", "TEXT", "��");
            forPrtParm.setData("BANK", "TEXT", "");
            forPrtParm.setData("OTHERS", "TEXT", "");
        } else if (payType.equals("PAY_BANK_CARD")) {
            forPrtParm.setData("CASH", "TEXT", "");
            forPrtParm.setData("BANK", "TEXT", "��");
            forPrtParm.setData("OTHERS", "TEXT", "");
        } else {
            forPrtParm.setData("CASH", "TEXT", "");
            forPrtParm.setData("BANK", "TEXT", "");
            forPrtParm.setData("OTHERS", "TEXT", "��");
        }
//        System.out.println("�س�Ԥ����" + forPrtParm);
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment.jhw",
                             forPrtParm);

    }

    /**
     * �õ���������
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        String deptDesc = "";
        String selDept =
                " SELECT DEPT_CHN_DESC " +
                "   FROM SYS_DEPT " +
                "  WHERE DEPT_CODE = '" + deptCode + "' ";
        //��ѯ��������
        TParm selDeptParm = new TParm(TJDODBTool.getInstance().select(
                selDept));
        deptDesc = selDeptParm.getValue("DEPT_CHN_DESC", 0);
        return deptDesc;
    }

    /**
     * �õ���������
     * @param stationCode String
     * @return String
     */
    public String getStationDesc(String stationCode) {
        String stationDesc = "";
        String selStation =
                " SELECT STATION_DESC " +
                "   FROM SYS_STATION " +
                "  WHERE STATION_CODE = '" + stationCode + "' ";
        //��ѯ��������
        TParm selStationParm = new TParm(TJDODBTool.getInstance().select(
                selStation));
        stationDesc = selStationParm.getValue("STATION_DESC", 0);
        return stationDesc;
    }

}

