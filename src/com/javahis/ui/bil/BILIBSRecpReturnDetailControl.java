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
 * <p>Title: 收据召回退费窗口</p>
 *
 * <p>Description: 收据召回退费窗口</p>
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
     * 退费
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
//        System.out.println("回冲预交金入参" + preAmt + "|" + invNo + "|" + recpNo +
//                           "|" + stationCode + "|" + deptCode);
        if (recpNo.length() > 0) {
        	printBillPay(preAmt, payType, recpNo, stationCode, deptCode);
        }
            
        this.messageBox("票据" + result.getValue("INV_NO") + "已收回");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return;
        } else {
        	 this.setReturnValue("true");
        	 insertRecallRecord();  //add by wukai on 20160921 插入一条出院召回
        }
           
        this.closeWindow();
    }
    
    /**
     * 生成一条召回记录
     */
    public void insertRecallRecord() {
    	TParm parm = new TParm();
    	parm.setData("MR_NO", this.getValueString("MR_NO"));
    	parm.setData("CASE_NO", caseNo);
    	//System.out.println("caseNo::::::::::: " + caseNo);
    	TParm admParm = ADMInpTool.getInstance().selectall(parm);//ADM_INP获取病患信息
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
    			recallParm.setData("RECALL_TYPE", "01A");  //医疗召回
    		} else {
    			recallParm.setData("RECALL_TYPE", "01B"); //财务召回
    		}
    		STAOutRecallTool.getNewInstance().insertRecall(recallParm);
    		//System.out.println("insertReslut :::::  " + STAOutRecallTool.getNewInstance().insertRecall(recallParm));
    	}
    	
    }
    
    /**
     * 回冲打票
     * @param preAmt String
     * @param payType String
     * @param recpNo String
     * @param stationCode String
     * @param deptCode String
     */
    public void printBillPay(String preAmt, String payType, String recpNo,
                             String stationCode, String deptCode) {
        //打印预交金收据
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
        forPrtParm.setData("Data", "TEXT", "日期:" + pDate);
        forPrtParm.setData("Name", "TEXT",
                           "姓名:" + this.getValueString("PAT_NAME"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", "收费员:" + Operator.getName());
        forPrtParm.setData("MR_N0", "TEXT",
                           "病案号:" + this.getValueString("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT",
                           "住院号:" + this.getValueString("IPD_NO"));
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "男");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "女");
        else
            forPrtParm.setData("SEX", "TEXT", "性别:" + "不详");
        forPrtParm.setData("DEPT", "TEXT",
                           "就诊科室:" + getDeptDesc(deptCode));
        forPrtParm.setData("STATION", "TEXT",
                           "就诊病区:" + getStationDesc(stationCode));
        forPrtParm.setData("Capital", "TEXT", "(大写):" + bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",
                           "(小写):" +
                           formatObject.format(
                                   TypeTool.getDouble(preAmt)));
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "流水号:" + recpNo);
        forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);
        if (payType.equals("PAY_CASH")) {
            forPrtParm.setData("CASH", "TEXT", "√");
            forPrtParm.setData("BANK", "TEXT", "");
            forPrtParm.setData("OTHERS", "TEXT", "");
        } else if (payType.equals("PAY_BANK_CARD")) {
            forPrtParm.setData("CASH", "TEXT", "");
            forPrtParm.setData("BANK", "TEXT", "√");
            forPrtParm.setData("OTHERS", "TEXT", "");
        } else {
            forPrtParm.setData("CASH", "TEXT", "");
            forPrtParm.setData("BANK", "TEXT", "");
            forPrtParm.setData("OTHERS", "TEXT", "√");
        }
//        System.out.println("回冲预交金" + forPrtParm);
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment.jhw",
                             forPrtParm);

    }

    /**
     * 得到科室名称
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        String deptDesc = "";
        String selDept =
                " SELECT DEPT_CHN_DESC " +
                "   FROM SYS_DEPT " +
                "  WHERE DEPT_CODE = '" + deptCode + "' ";
        //查询科室名称
        TParm selDeptParm = new TParm(TJDODBTool.getInstance().select(
                selDept));
        deptDesc = selDeptParm.getValue("DEPT_CHN_DESC", 0);
        return deptDesc;
    }

    /**
     * 得到病区名称
     * @param stationCode String
     * @return String
     */
    public String getStationDesc(String stationCode) {
        String stationDesc = "";
        String selStation =
                " SELECT STATION_DESC " +
                "   FROM SYS_STATION " +
                "  WHERE STATION_CODE = '" + stationCode + "' ";
        //查询病区名称
        TParm selStationParm = new TParm(TJDODBTool.getInstance().select(
                selStation));
        stationDesc = selStationParm.getValue("STATION_DESC", 0);
        return stationDesc;
    }

}

