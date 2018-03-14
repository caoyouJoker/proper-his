package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

import jdo.adm.ADMInpTool;

import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

import jdo.sys.PatTool;
import jdo.bil.BILIBSRecpmTool;
import jdo.bil.BILIBSRecpdTool;

import com.javahis.util.StringUtil;

import java.sql.Timestamp;

import com.dongyang.util.StringTool;

import jdo.sys.IReportTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BilInvoice;

import com.dongyang.manager.TIOM_AppServer;

import jdo.bil.BILInvoiceTool;

import java.util.Date;
import java.util.Vector;

/**
 * <p>Title: 住院账务召回控制类</p>
 *
 * <p>Description: 住院账务召回控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpReturnControl
    extends TControl {
    //计算病患住院天数方法
    //int i = ADMTool.getInstance().getAdmDays(CASE_NO);
    TParm mainParm = new TParm();
    TParm detailParm = new TParm();
    String caseNo = "";
    String stationCode = "";
    private boolean isLose = false;//wangjingchun add 20141210
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        //账单主档table专用的监听
        getTTable("MainTable").addEventListener(TTableEvent.
                                                CHECK_BOX_CLICKED, this,
                                                "onMainTableComponent");
//        test();
        this.onClear();
    }
    public void test() {
        String admSql =
                " SELECT CASE_NO FROM ADM_INP ";
        TParm admParm = new TParm(TJDODBTool.getInstance().select(admSql)) ;
        System.out.println("住院数据"+admParm);
        for (int i = 0; i < admParm.getCount(); i++) {
            String sql =
                    " SELECT CASE_NO,SUM (TOT_AMT) AS TOT_AMT " +
                    "   FROM IBS_ORDD " +
                    "  WHERE CASE_NO = '" + admParm.getValue("CASE_NO", i) +
                    "' " +
                    "    AND CASE_NO_SEQ NOT IN (SELECT DISTINCT (CASE_NO_SEQ) " +
                    "                              FROM IBS_ORDM " +
                    "                             WHERE CASE_NO = '" +
                    admParm.getValue("CASE_NO", i) + "') "+
                    "  GROUP BY CASE_NO ";
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            if(parm.getDouble("TOT_AMT",0)!=0)
                System.out.println("第"+i+"个人员"+parm.getValue("CASE_NO",0));
        }
    }
    /**
     * 初始化界面
     */
    public void initPage() {
        //当前时间
        Timestamp today = SystemTool.getInstance().getDate();
        //获取选定日期的前一天的日期
        Timestamp yesterday = StringTool.rollDate(today, -1);
        this.setValue("S_DATE", yesterday);
        this.setValue("E_DATE", today);
    }

    /**
     * 主表的单击事件
     */
    public void onMainTableClicked() {
        TTable mainTable = getTTable("MainTable");
        TTable detailTable = getTTable("DetailTable");
        int row = mainTable.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = mainTable.getParmValue();
        parm.setData("RECEIPT_NO", regionParm.getData("RECEIPT_NO", row));
        TParm data = BILIBSRecpdTool.getInstance().selDateForReturn(parm);
        detailTable.setParmValue(data);
    }

    /**
     * 主表监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onMainTableComponent(Object obj) {
        TTable mainTable = (TTable) obj;
        mainTable.acceptText();
        TParm tableParm = mainTable.getParmValue();
        mainParm = new TParm();
        int count = tableParm.getCount("RECEIPT_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                mainParm.addData("RECEIPT_NO",
                                 tableParm.getValue("RECEIPT_NO", i));
                mainParm.addData("IPD_NO",
                                 tableParm.getValue("IPD_NO", i));
                mainParm.addData("CASE_NO",
                                 tableParm.getValue("CASE_NO", i));
                mainParm.addData("MR_NO",
                                 tableParm.getValue("MR_NO", i));
                //===zhangp 20120827 start
//                mainParm.addData("AR_AMT",
//                                 tableParm.getValue("AR_AMT", i));
                mainParm.addData("AR_AMT",
                				 tableParm.getValue("OWN_AMT", i));
                //===zhangp 20120827 end
            }
        }
        return true;
    }

    /**
     * 查询
     */
    public void onQuery() {
        TTable mainTable = getTTable("MainTable");
        String mrNo = this.getValueString("MR_NO");
        String realMrNo = PatTool.getInstance().checkMrno(mrNo);
        String ipdNo = this.getValueString("IPD_NO");
        String realIpdNo = PatTool.getInstance().checkIpdno(ipdNo);
        TParm parm = new TParm();
        if (getValueString("MR_NO").length() == 0 &&
            getValueString("IPD_NO").length() == 0) {
            this.messageBox("请输入病案号或住院号");
            return;
        }
        
        //modify by huangtt 20160928 EMPI患者查重提示  start
        Pat pat = Pat.onQueryByMrNo(realMrNo);        
        if (!StringUtil.isNullString(realMrNo) && !realMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("病案号" + realMrNo + " 已合并至 " + "" + pat.getMrNo());
            realMrNo = pat.getMrNo();
            this.setValue("MR_NO", realMrNo);
        }
        //modify by huangtt 20160928 EMPI患者查重提示  end
        

        if (getValueString("MR_NO").length() != 0)
            parm.setData("MR_NO", realMrNo);
        if (getValueString("IPD_NO").length() != 0)
            parm.setData("IPD_NO", realIpdNo);
        TParm mParm = BILIBSRecpmTool.getInstance().selDateForReturn(parm);
        if (mParm.getCount("MR_NO") <= 0) {
            this.messageBox("此患者无收据");
            return;
        }
        setValue("MR_NO", mParm.getValue("MR_NO", 0));
        setValue("IPD_NO", mParm.getValue("IPD_NO", 0));
        caseNo = mParm.getValue("CASE_NO", 0);
        mainTable.setParmValue(mParm);
        TParm patInfoParm = PatTool.getInstance().getInfoForMrno(mParm.getValue(
            "MR_NO", 0));
        String patName = patInfoParm.getValue("PAT_NAME", 0);
        String sexCode = patInfoParm.getValue("SEX_CODE", 0);
        Timestamp birthDate = patInfoParm.getTimestamp("BIRTH_DATE", 0);
        setValue("PAT_NAME", patName);
        setValue("SEX_CODE", sexCode);
        TParm admInpParm = new TParm();
        admInpParm.setData("CASE_NO", caseNo);
        TParm admInpInfoParm = ADMInpTool.getInstance().selectall(admInpParm);
        stationCode = admInpInfoParm.getValue("IN_STATION_CODE",0);
        Timestamp inDate = admInpInfoParm.getTimestamp("IN_DATE", 0);
        String AGE = StringUtil.showAge(birthDate, inDate);
        setValue("AGE", AGE);

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
     * 保存事件
     * @return boolean
     */
    public boolean onSave() {
//        this.messageBox(""+mainParm);
        mainParm.addData("PAT_NAME", this.getValueString("PAT_NAME"));
        mainParm.addData("FLG", this.getValueString("FLG"));
        mainParm.addData("SEX_CODE", this.getValueString("SEX_CODE"));
        TParm actionParm  = mainParm.getRow();
        Object result = this.openDialog(
            "%ROOT%\\config\\bil\\BILIBSRecpReturnDetail.x", mainParm, false);
        if (result == null) {
            return false;
        }
        else
            this.messageBox("P0001");
        onClear();
        return true;
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("S_DATE;E_DATE;MR_NO;IPD_NO;PAT_NAME;" +
                   "SEX_CODE;AGE");
        this.callFunction("UI|MainTable|removeRowAll");
        this.callFunction("UI|DetailTable|removeRowAll");
        initPage();
    }

    /**
     * 补印
     * @throws ParseException 
     */
    public void onPrint() throws ParseException {
    	if(!this.isLose){//wangjingchun add 20141210
	        if (!checkNo()) {
	            this.messageBox("尚未开帐,请先开帐");
	            return ;
	        }
    	}
        TTable table = getTTable("MainTable");
        TParm tableParm = table.getParmValue();
        //=======pangben modify 20110513 start
        if (tableParm== null||tableParm.getCount("RECEIPT_NO")==0) {
            this.messageBox_("无可打印的票据!");
            return;
        }

        int count = tableParm.getCount("RECEIPT_NO");

     //=======pangben modify 20110513 stop
//        int row = table.getSelectedRow();
        for (int i = 0; i < count; i++) {
	            if ("Y".equals(tableParm.getValue("FLG", i))) {
	            	String receiptNo = "";
	            	if(!this.isLose){//wangjingchun add 20141210
		                //初始化下一票号
		                BilInvoice invoice = new BilInvoice();
		                invoice = invoice.initBilInvoice("IBS");
		                //检核开关帐
		                if (invoice == null) {
		                    this.messageBox_("你尚未开账!");
		                    return;
		                }
		            
		                //检核当前票号
		                if (invoice.getUpdateNo().length() == 0 ||
		                    invoice.getUpdateNo() == null) {
		                    this.messageBox_("无可打印的票据!");
		                    return;
		                }
		                //检核当前票号
		                if (invoice.getUpdateNo().equals(invoice.
		                    getEndInvno())) {
		                    this.messageBox_("最后一张票据!");
		//            return;
		                }
		           
			            String printNo = invoice.getUpdateNo();
			            receiptNo = tableParm.getValue("RECEIPT_NO", i);
//		                String receiptNo = tableParm.getValue("RECEIPT_NO", i);
		                TParm onREGReprintParm = new TParm();
		                onREGReprintParm.setData("CASE_NO",  tableParm.getValue("CASE_NO", i));
		                onREGReprintParm.setData("RECEIPT_NO", receiptNo);
		                onREGReprintParm.setData("OPT_USER", Operator.getID());
		                onREGReprintParm.setData("OPT_TERM", Operator.getIP());
		                TParm result = new TParm();
		                result = TIOM_AppServer.executeAction("action.bil.BILAction",
		                    "onIBSReprint", onREGReprintParm);
		                if (result.getErrCode() < 0) {
		                    err(result.getErrName());
		                    return;
		                }
	            	}else{
	            		receiptNo = tableParm.getValue("RECEIPT_NO", i);
	            	}
                TParm printParm = getPrintData(receiptNo);
                Timestamp printDate = (SystemTool.getInstance().getDate());

                TParm selAdmInp = new TParm();
                selAdmInp.setData("CASE_NO", caseNo);
                TParm admInpParm = ADMInpTool.getInstance().selectall(selAdmInp);
                Timestamp inDataOut = admInpParm.getTimestamp("IN_DATE", 0);
                Timestamp outDataOut;
                if (admInpParm.getData("DS_DATE", 0) != null)
                    outDataOut = admInpParm.getTimestamp("DS_DATE", 0);
                else
                    outDataOut = printDate;

                String deptCode = admInpParm.getValue("DEPT_CODE", 0);

                String inDate = StringTool.getString(inDataOut, "yyyyMMdd");
                String outDate = StringTool.getString(outDataOut, "yyyyMMdd");
                String pDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
                
                if(getInsAdmConfirm(caseNo).getCount()>1)
                {
                	TParm parm  = getBillmEndDate(receiptNo);
                	System.out.println("parm:"+parm);
                    String enddate = ""; 
                    String appdate = "";
                    Timestamp yesterday = StringTool.rollDate(
            				StringTool.getTimestamp(""+parm.getData("END_DATE",0), "yyyy-MM-dd HH:mm:ss"), -1);
                    enddate  = StringTool.getString(yesterday, "yyyyMMdd") ;
                    Timestamp sysDate = SystemTool.getInstance().getDate();
                    appdate = StringTool.getString(sysDate,"yyyyMMdd");
                    System.out.println("enddate:"+enddate+"  "+appdate);
                    //同一年，并且confirmNo记录为2的 取带KN的资格确认书
                    if (enddate.substring(0, 4).equals(appdate.substring(0, 4))) 
                    {
                   	 inDate = enddate.substring(0,4)+"0101"; 
                   	 inDataOut = StringTool.getTimestamp(inDate, "yyyyMMdd");
                    }
                    //取非KN的资格确认书
                    else {
                   	 outDate = enddate.substring(0,4)+"1231";
                   	 outDataOut = StringTool.getTimestamp(outDate, "yyyyMMdd");
                    }                	
                }
                String sYear = inDate.substring(0,4);
                String sMonth = inDate.substring(4,6);
                String sDate = inDate.substring(6,8);
                String eYear = outDate.substring(0,4);
                String eMonth = outDate.substring(4,6);
                String eDate = outDate.substring(6,8);
                String pYear = pDate.substring(0,4);
                String pMonth = pDate.substring(4,6);
                String pDay = pDate.substring(6,8);
//                int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) ==
//                    0 ? 1 : StringTool.getDateDiffer(outDataOut, inDataOut);
                
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		Date now = new Date();
        		now = sdf.parse(eYear+"-"+eMonth+"-"+eDate+" 23:59:59");
        		Date date = sdf.parse(sYear+"-"+sMonth+"-"+sDate+" 00:00:00");
        		long l=now.getTime()-date.getTime();
        		long day=l/(24*60*60*1000);
                day = (day == 0 ? 1 : day);// add by wanglong 20130715
        		int rollDate = Integer.valueOf(""+day);
        		//  int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut)+1;
    			//====zhangp 20120224 start
        		String sql = 
                	" SELECT BEGIN_DATE, END_DATE" +
                	" FROM IBS_BILLM" +
                	" WHERE RECEIPT_NO = '" + receiptNo + "'" +
                	" ORDER BY BEGIN_DATE";
                TParm beginEndParm = new TParm(TJDODBTool.getInstance().select(sql));
                inDate = StringTool.getString(beginEndParm.getTimestamp("BEGIN_DATE", 0), "yyyy/MM/dd");
                outDate = StringTool.getString(beginEndParm.getTimestamp("END_DATE", beginEndParm.getCount()-1).after(outDataOut)?outDataOut:beginEndParm.getTimestamp("END_DATE", beginEndParm.getCount()-1), "yyyy/MM/dd");
//                inDate = StringTool.getString(inDataOut, "yyyy/MM/dd");
//                outDate = StringTool.getString(outDataOut, "yyyy/MM/dd");
                //wangjingchun 20141202 add start
                String start_date[] = inDate.split("/");
                String end_date[] = outDate.split("/");
                //wangjingchun 20141202 add end
//    			printParm.setData("STARTDATE", "TEXT", inDate);
//    			printParm.setData("ENDDATE", "TEXT", outDate);
    			printParm.setData("PRINTDATE", "TEXT", pDate);
    			//=====zhangp 20120224 end
    			
    			//wangjingchun 20141202 add start
    			//医生姓名
                String drSql = "SELECT DIRECTOR_DR_CODE, IDNO, MR_NO FROM MRO_RECORD WHERE CASE_NO = '"+caseNo+"'";
                TParm drParm = new TParm(TJDODBTool.getInstance().select(drSql));
                String dr_sql = "SELECT VS_DR_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"'";
                TParm dr_parm = new TParm(TJDODBTool.getInstance().select(dr_sql));
                String dr_sql1 = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+dr_parm.getValue("VS_DR_CODE",0)+"' ";
                TParm dr_parm1 = new TParm(TJDODBTool.getInstance().select(dr_sql1));
                printParm.setData("DR_CODE", "TEXT", drParm.getValue("DIRECTOR_DR_CODE",0));
                printParm.setData("DR_NAME", "TEXT", dr_parm1.getValue("USER_NAME",0));
                //业务流水号
                String ibsSql = "SELECT ADM_SEQ,MR_NO FROM INS_IBS WHERE CASE_NO = '"+caseNo+"'";
                TParm ibsParm = new TParm(TJDODBTool.getInstance().select(ibsSql));
                if(ibsParm.getValue("ADM_SEQ", 0).equals("")){
                	printParm.setData("ADM_SEQ","TEXT", receiptNo);
                }else{
                	printParm.setData("ADM_SEQ","TEXT",ibsParm.getValue("ADM_SEQ", 0));
                }
                if(ibsParm.getValue("MR_NO", 0).equals("")){
                	printParm.setData("MR_NO","TEXT",drParm.getValue("MR_NO", 0));
                }else{
                	printParm.setData("MR_NO","TEXT",ibsParm.getValue("MR_NO", 0));
                }
                //医保类型
                String ctzSql = "SELECT S.CTZ_DESC,S.CTZ_CODE FROM SYS_CTZ S,ADM_INP A WHERE S.CTZ_CODE = A.CTZ1_CODE AND A.CASE_NO = '"+caseNo+"'";
                TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
                if(!ctzParm.getValue("CTZ_CODE",0).equals("99")){
                	printParm.setData("CTZ_DESC","TEXT",ctzParm.getValue("CTZ_DESC", 0));
                }else{
                	printParm.setData("CTZ_DESC","TEXT","自费"); // 20170421 zhanglei 自费患者显示自费标识并
                }
                //患者身份证号
                if(!ctzParm.getValue("CTZ_CODE", 0).equals("99")&&!ctzParm.getValue("CTZ_CODE", 0).equals("")){
                	//20150115 wangjingchun modify 756
                	try {
                		String idNO = drParm.getValue("IDNO",0);
                		idNO.length();
                		String idNOBefore = idNO.substring(0, 3);
                		String idNOEnd = idNO.substring(idNO.length()-3, idNO.length());
                		String x = "";
                		for(int ii=0;ii<idNO.length()-6;ii++){
                			x +="*";
                		}
                		printParm.setData("IDNO", "TEXT", idNOBefore+x+idNOEnd);
					} catch (Exception e) {
						// TODO: handle exception
						printParm.setData("IDNO", "TEXT", "");
					}
                }else{
                	printParm.setData("IDNO", "TEXT", "");
                }
                
                //医疗机构类型
                String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
                TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
                String hosp_class = regionParm.getValue("HOSP_CLASS",0);
                String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hosp_class+"'";
        	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
        	    printParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
                //wangjingchun 20141202 add end
    			
                printParm.setData("COPY", "TEXT", "COPY");
                printParm.setData("sYear","TEXT", sYear);
                printParm.setData("sMonth","TEXT", sMonth);
                printParm.setData("sDate","TEXT", sDate);
                printParm.setData("eYear","TEXT", eYear);
                printParm.setData("eMonth","TEXT", eMonth);
                printParm.setData("eDate","TEXT", eDate);
                printParm.setData("pYear","TEXT", pYear);
                printParm.setData("pMonth","TEXT", pMonth);
                printParm.setData("pDay","TEXT", pDay);
                printParm.setData("rollDate","TEXT", rollDate);
                printParm.setData("PAT_NAME","TEXT", this.getValueString("PAT_NAME"));
                printParm.setData("SEX_CODE","TEXT", getDesc(this.getValueString("SEX_CODE")));
                printParm.setData("STATION_CODE","TEXT",getStation(stationCode));
                printParm.setData("RECEIPT_NO","TEXT",receiptNo);
                printParm.setData("CASHIER_CODE","TEXT", Operator.getName());
                printParm.setData("MR_NO","TEXT", this.getValueString("MR_NO"));
                printParm.setData("BILL_DATE","TEXT", outDataOut);
                printParm.setData("CHARGE_DATE","TEXT", outDataOut);
                printParm.setData("DEPT_CODE","TEXT", getDept(deptCode));
                printParm.setData("OPT_USER", "TEXT", Operator.getID());// modify by wanglong 20130221
                printParm.setData("OPT_DATE","TEXT", printDate);
//                String printDateC = StringTool.getString(printDate,"yyyy年MM月dd日");
                String printDateC = "";
                if(!this.isLose){
                	printDateC = StringTool.getString(printDate,"yyyy   MM   dd");//wangjingchun add 20141202
                }else{
                	printDateC = StringTool.getString(printDate,"yyyy年MM月dd日");//wangjingchun add 20141202
                }
                //收费员
                String CashierCode = tableParm.getValue("CASHIER_CODE",i);//===wangjingchun 20141202
                printParm.setData("CASHIER_CODE","TEXT", CashierCode);//===wangjingchun 20141202
                printParm.setData("DATE", "TEXT", printDateC);
                printParm.setData("NO1", "TEXT", receiptNo);
//                printParm.setData("NO2", "TEXT", printNo);
                printParm.setData("HOSP", "TEXT",
                                  Operator.getHospitalCHNFullName());
                printParm.setData("IPD_NO","TEXT", this.getValueString("IPD_NO"));
                printParm.setData("BIL_PAY_C","TEXT",
                                  StringUtil.getInstance().numberToWord(Double.
                    parseDouble(String.valueOf(tableParm.getValue("PAY_BILPAY",i)))));
                printParm.setData("BIL_PAY","TEXT",tableParm.getValue("PAY_BILPAY",i));
                DecimalFormat df = new DecimalFormat("##########0.00");
                double amtD = StringTool.getDouble(tableParm.getValue(
                    "AR_AMT", i)) -
                    StringTool.getDouble(tableParm.getValue("PAY_BILPAY", i));

                String amt = df.format(amtD<0?-amtD:amtD);
                String tmp=StringUtil.getInstance().numberToWord(Double.valueOf(printParm.getData("TOT_AMT", "TEXT").toString()));
                          // StringUtil.getInstance().numberToWord( Double.valueOf(printParm.getData("TOT_AMT", "TEXT").toString()));
                
      		  if(tmp.lastIndexOf("分") > 0){//修改金额为分的，后面不能加正或整字样 modify 2013722 caoyong
                	tmp = tmp.substring(0,tmp.lastIndexOf("分")+1);//取得正确的大写金额
                }
      		  
                printParm.setData("amtToWord", "TEXT",tmp );

                if (StringTool.getDouble(tableParm.getValue("AR_AMT",i))-StringTool.getDouble(tableParm.getValue("PAY_BILPAY",i)) < 0) {
                    printParm.setData("XS","TEXT", 0.00);
                    printParm.setData("YT","TEXT", amt);
                }
                else {
                    printParm.setData("XS","TEXT", amt);
                    printParm.setData("YT","TEXT", 0.00);
                }
                String caseNo = tableParm.getData("CASE_NO",i).toString();
                //===zhangp 20120412 start
//                String sql =
//                	"SELECT SUM(ACCOUNT_PAY_AMT) ACCOUNT_PAY_AMT,SUM(APPLY_AMT) APPLY_AMT,SUM(OWN_AMT) OWN_AMT FROM INS_IBS " +
//                	" WHERE CASE_NO = '"+caseNo+"'";
//                TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
                TParm insParm = getInsParm(caseNo,receiptNo);
                if(insParm.getErrCode()<0){
                	return;
                }
                double armyAi_amt = insParm.getDouble("ARMYAI_AMT");//补助
                double illnesssubsidyamt = insParm.getDouble("ILLNESS_SUBSIDY_AMT"); //城乡大病金额
                double account_pay_amt =  insParm.getDouble("ACCOUNT_PAY_AMT");//个人账户
                double nhi_comment =  insParm.getDouble("NHI_COMMENT");//救助
                double nhi_pay =  insParm.getDouble("NHI_PAY");//统筹         
                double pay_cash = 0.00;              
                //异地病人
                if(insParm.getValue("INS_CROWD_TYPE").equals("3"))
                      pay_cash = insParm.getDouble("AR_AMT")-
                      nhi_comment-nhi_pay-armyAi_amt-account_pay_amt; //现金支付/自付  	
                else{
                	//本地非单病种
                	if(insParm.getValue("SDISEASE_CODE").length()==0)
                  		 pay_cash = insParm.getDouble("OWN_PAY"); //现金支付/自付  
                    else{
                    //本地单病种	
                  		String inDateSdisease = StringTool.getString(
                      	insParm.getTimestamp("IN_DATE"), "yyyyMMdd");
                  	   if (Integer.parseInt(inDateSdisease) < Integer.parseInt("20170101"))
                           pay_cash = insParm.getDouble("OWN_PAY"); //现金支付/自付  
                  	   else
                  	       pay_cash = insParm.getDouble("OWN_PAY")+
                  	             insParm.getDouble("RESTART_STANDARD_AMT"); //现金支付/自付  	
                    }	
                }
                //wangjingchun 20141203 add start
                //补助金额
                printParm.setData("ARMYAI_AMT","TEXT",armyAi_amt);
                //城乡大病金额
                printParm.setData("ILLNESS_SUBSIDY_AMT","TEXT",illnesssubsidyamt);
                //大额救助
                printParm.setData("NHI_COMMENT","TEXT",nhi_comment);
                if(!ctzParm.getValue("CTZ_CODE", 0).equals("99")&&!ctzParm.getValue("CTZ_CODE", 0).equals("")){
                	printParm.setData("OTHER_INS","TEXT","其他医保支付项：补助金额 "+armyAi_amt+",大额救助 "+nhi_comment+",城乡大病 "+illnesssubsidyamt);
                	printParm.setData("ADVANCE_CASE_NO","TEXT","");
                }else{
                	printParm.setData("OTHER_INS","TEXT","");
                	//住院垫付使用
                	printParm.setData("ADVANCE_CASE_NO","TEXT","垫付住院号:"+caseNo+"（垫付患者使用）");
                }
                //医保其他支付
                double other_ins_pay = armyAi_amt+illnesssubsidyamt+nhi_comment;
                printParm.setData("OTHER_INS_PAY", "TEXT",Math.abs(StringTool.round(
            			other_ins_pay,2)));
              
                //wangjingchun 20141203 add END
                
                if(insParm.getCount()>0){
                	//this.messageBox("得到医保数据");
//                	printParm.setData("PAY_INS_CARD", "TEXT",
//                            Math.abs(StringTool.round(
//                            		insParm.getDouble("ACCOUNT_PAY_AMT", 0),
//                                             2)));
//                	printParm.setData("PAY_INS", "TEXT",
//                            Math.abs(StringTool.round(insParm.getDouble("APPLY_AMT", 0),
//                                             2)));
//                	printParm.setData("PAY_INS_PERSON", "TEXT",
//                            Math.abs(StringTool.round(
//                            		insParm.getDouble("OWN_AMT", 0),
//                                             2)));
//                	printParm.setData("PAY_INS_CASH", "TEXT",
//                            Math.abs(StringTool.round(
//                            		insParm.getDouble("OWN_AMT", 0) -
//                            		insParm.getDouble("ACCOUNT_PAY_AMT", 0),
//                                             2)));
                	printParm.setData("PAY_INS_CARD", "TEXT",
                            Math.abs(StringTool.round(
                            		account_pay_amt,
                                             2)));
                	printParm.setData("PAY_INS", "TEXT",
                            Math.abs(StringTool.round(nhi_pay,
                                             2)));
                	printParm.setData("PAY_INS_PERSON", "TEXT",
                            Math.abs(StringTool.round(
                            		pay_cash
                            		,2)));
                	printParm.setData("PAY_INS_CASH", "TEXT",
                            Math.abs(StringTool.round(
                            		pay_cash
                            		,2)));
                	  if(illnesssubsidyamt!=0){
                    printParm.setData("JZ_TYPE","TEXT","城乡大病");	  
                	printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                			illnesssubsidyamt, 2)));
                	  }
                	  else{
                	printParm.setData("JZ_TYPE","TEXT","大额救助");	  
                    printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                          	nhi_comment, 2)));
                	  }
                	//===zhangp 20120412 end
                }else{
                	//this.messageBox("未得到医保数据:" + printParm.getData("TOT_AMT", "TEXT"));
                	printParm.setData("PAY_INS_CARD", "TEXT",
                            Math.abs(StringTool.round(
                                             0.00,
                                             2)));
                	printParm.setData("PAY_INS", "TEXT",
                            Math.abs(StringTool.round(0.00,
                                             2)));
                	printParm.setData("PAY_INS_PERSON", "TEXT",
                            Math.abs(StringTool.round(
                            		0.00,
                                             2)));
//                	double TOT_AMT =  printParm.getDouble("TOT_AMT", "TEXT");
//                	this.messageBox("支持：" + TOT_AMT);
                    printParm.setData("PAY_INS_CASH", "TEXT",
                    		printParm.getDouble("TOT_AMT", "TEXT")); // 20170421 zhanglei 为自费用户添加资费=总额现实
//                	printParm.setData("PAY_INS_CASH", "TEXT",
//                            Math.abs(StringTool.round(
//                            		0.00,
//                                             2)));
                	printParm.setData("PAY_INS_BIG","TEXT", Math.abs(StringTool.round(
                            		0.00,
                                             2)));//caowl 20130318 报表显示上增加大额支付
                }
                //===zhangp 20120321 end
//    			System.out.println("printParm"+printParm);
    			//====zhangp 20120306 modify end
    			//====zhangp 20120306 modify start
//    			double arAmt = tableParm.getDouble("AR_AMT",i)-tableParm.getDouble("PAY_BILPAY",i);
                double arAmt = 0;
                if(pay_cash>0){
                	arAmt = pay_cash - tableParm.getDouble("PAY_BILPAY",i);
                }else{
                	arAmt = tableParm.getDouble("AR_AMT",i) - tableParm.getDouble("PAY_BILPAY",i);
                }
    			if(arAmt>=0){
    				printParm.setData("ADDAMT", "TEXT", Math.abs(StringTool.round(tableParm.getDouble("PAY_CASH",i), 2)));//补缴
    				//===zhangp 20120331 start
    				printParm.setData("ADDCHECKAMT", "TEXT", Math.abs(
    						StringTool.round(
    								//===zhangp 20120417 start
//    								tableParm.getDouble("PAY_CHECK",i)
    								tableParm.getDouble("PAY_CHECK",i) + tableParm.getDouble("PAY_BANK_CARD",i) + tableParm.getDouble("PAY_DEBIT",i)
    								//===zhangp 20120417 end
    						, 2)));//补缴
    				//===zhangp 20120331 end
    				//20141217 wangjingchun add start
    				String pay_add_sum = "";
    				double a1 = Math.abs(StringTool.round(tableParm.getDouble("PAY_CASH",i), 2));
    				double b1 = Math.abs(StringTool.round(tableParm.getDouble("PAY_CHECK",i), 2));
    				double c1 = Math.abs(StringTool.round(tableParm.getDouble("PAY_BANK_CARD",i), 2));
    				if(a1 != 0){
    					pay_add_sum = pay_add_sum+" 现："+a1;
    				}
    				if(b1 != 0){
    					pay_add_sum = pay_add_sum+" 支："+b1;
    				}
    				if(c1 != 0){
    					pay_add_sum = pay_add_sum+" 卡："+c1;
    				}
    				printParm.setData("PAY_ADD_SUM","TEXT",pay_add_sum);
    				//20141217 wangjingchun add end
    			}else{
    				printParm.setData("BACKAMT", "TEXT", Math.abs(StringTool.round(tableParm.getDouble("PAY_CASH",i), 2)));//退
    				//===zhangp 20120331 start
    				printParm.setData("BACKCHECKAMT", "TEXT", Math.abs(
    						StringTool.round(
    								//===zhangp 20120417 start
//    								tableParm.getDouble("PAY_CHECK",i)
    								tableParm.getDouble("PAY_CHECK",i) + tableParm.getDouble("PAY_BANK_CARD",i) + tableParm.getDouble("PAY_DEBIT",i)
    								//===zhangp 20120417 end
    								, 2)));//退
    				//===zhangp 20120331 end
    				//20141217 wangjingchun add start
    				String back_sum = "";
    				double a2 = Math.abs(
    						StringTool.round(tableParm.getDouble("PAY_CASH",i), 2));
    				double b2 = Math.abs(
    						StringTool.round(tableParm.getDouble("PAY_CHECK",i), 2));
    				double c2 = Math.abs(
    						StringTool.round(tableParm.getDouble("PAY_BANK_CARD",i), 2));
    				if(a2 != 0){
    					back_sum += " 现："+a2;
    				}
    				if(b2 != 0){
    					back_sum += " 支："+b2;
    				}
    				if(c2 != 0){
    					back_sum += " 支："+c2;
    				}
    				printParm.setData("BACK_SUM","TEXT",back_sum);
    				//20141217 wangjingchun add end
    			}
//                            printParm.setData("ADDCHECKAMT", "TEXT", 1293.29);
//                            printParm.setData("BACKCHECKAMT", "TEXT",  0.00);
    	           //===zhangp 20120321 start
//                this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\IBSRecp.jhw",
//                                     printParm,true);
    			//20141217 wangjingchun add start
    			if(!this.isLose){
    				printParm.setData("STARTDATE", "TEXT", start_date[0]+"   "+start_date[1]+"   "+start_date[2]);
                    printParm.setData("ENDDATE", "TEXT", end_date[0]+"   "+end_date[1]+"   "+end_date[2]);
	                this.openPrintWindow(IReportTool.getInstance().getReportPath("IBSRecp.jhw"),
	                                     IReportTool.getInstance().getReportParm("IBSRecp.class", printParm),true);//报表合并modify by wanglong 20130730
    			}else{
    				//查询票号，发票上的NO
    				String inv_no_sql = "SELECT INV_NO FROM BIL_INVRCP WHERE RECEIPT_NO = '"+receiptNo+"' AND RECP_TYPE = 'IBS' AND CANCEL_FLG = '0'";
    				TParm invNo_parm = new TParm(TJDODBTool.getInstance().select(inv_no_sql));
                    printParm.setData("INV_NO", "TEXT", invNo_parm.getValue("INV_NO",0));
    				printParm.setData("STARTDATE", "TEXT", start_date[0]+"年"+start_date[1]+"月"+start_date[2]+"日");
                    printParm.setData("ENDDATE", "TEXT", end_date[0]+"年"+end_date[1]+"月"+end_date[2]+"日");
    				this.openPrintWindow(IReportTool.getInstance().getReportPath("IBSRecpLosePrint.jhw"),
                            			 IReportTool.getInstance().getReportParm("IBSRecp.class", printParm),false);
    			}
            }
	            int k = 0;
	            for(int j=i+1;j<count;j++){
	            	if ("Y".equals(tableParm.getValue("FLG", j))) {
	            		k++;
	            	}
	            }
	            if(k == 0){
	            	this.isLose = false;
	            }
	          //20141217 wangjingchun add end

        }

    }
    
    
    /**
     * 票据遗失补印
     * @throws ParseException 
     */
    public void onLosePrint() throws ParseException{
    	this.isLose = true;
    	this.onPrint();
    }
    
    /**
     * 得到科室说明
     * @param deptCode String
     * @return String
     */
    public String getDept(String deptCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='" +
            deptCode + "'"));
        return parm.getValue("DEPT_CHN_DESC", 0);
    }

    /**
     * 得到姓名说明
     * @param code String
     * @return String
     */
    public String getDesc(String code) {
        TParm descParm = new TParm();
        descParm.setData(TJDODBTool.getInstance().select(
            "SELECT ID,CHN_DESC FROM  SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX'"));
        if (descParm.getCount() <= 0)
            return "";
        Vector vct = (Vector) descParm.getData("ID");
        int index = vct.indexOf(code);
        if (index < 0)
            return "";
        return descParm.getValue("CHN_DESC", index);
    }
    /**
     * 得到病区说明
     * @param stationCode String
     * @return String
     */
    public String getStation(String stationCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            "SELECT STATION_CODE,STATION_DESC FROM SYS_STATION  WHERE STATION_CODE='" +
            stationCode + "'"));
        return parm.getValue("STATION_DESC", 0);
    }

    /**
     * 得到票据信息
     * @param receiptNo String
     * @return TParm
     */
    public TParm getPrintData(String receiptNo) {
        DecimalFormat formatObject = new DecimalFormat("###########0.00");
        String selRecpD =
            " SELECT REXP_CODE,WRT_OFF_AMT " +
            "   FROM BIL_IBS_RECPD " +
            "  WHERE RECEIPT_NO = " + receiptNo + " ";
        //查询不同支付方式付款金额(日结金额)
        TParm selRecpDParm = new TParm(TJDODBTool.getInstance().select(
            selRecpD));
        int recpDCount = selRecpDParm.getCount("REXP_CODE");
        TParm charge = new TParm();
        String rexpCode = "";
        double wrtOffAmt101 = 0.00;
        double wrtOffAmt102 = 0.00;
        double wrtOffAmt103 = 0.00;
        double wrtOffAmt104 = 0.00;
        double wrtOffAmt105 = 0.00;
        double wrtOffAmt106 = 0.00;
        double wrtOffAmt107 = 0.00;
        double wrtOffAmt108 = 0.00;
        double wrtOffAmt109 = 0.00;
        double wrtOffAmt110 = 0.00;
        double wrtOffAmt111 = 0.00;
        double wrtOffAmt112 = 0.00;
        double wrtOffAmt113 = 0.00;
        double wrtOffAmt114 = 0.00;
        double wrtOffAmt115 = 0.00;
        double wrtOffAmt116 = 0.00;
        double wrtOffAmt117 = 0.00;
        double wrtOffAmt118 = 0.00;
        double wrtOffAmt119 = 0.00;
        //===zhangp 20120307 modify start
        double wrtOffAmt120 = 0.00;
        //===zhangp 20120307 modify end
        double totAmt = 0.00;
        double wrtOffSingle = 0.00;
        TParm chargeParm = new TParm();
        for (int i = 0; i < recpDCount; i++) {
            rexpCode = selRecpDParm.getValue("REXP_CODE", i);
//            System.out.println("类别第"+i+"次=="+rexpCode);

            wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
//            System.out.println("wrtOffSingle单次第"+i+"次=="+wrtOffSingle);
            totAmt = totAmt + wrtOffSingle;
          //========20120224 zhangp modify start
			if ("020".equals(rexpCode)) { // 床位费
				wrtOffAmt101 = wrtOffAmt101 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt101);
				chargeParm.setData("CHARGE01", arAmtS);
			} else if ("021".equals(rexpCode)) { // 诊查费
				wrtOffAmt102 = wrtOffAmt102 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt102);
				chargeParm.setData("CHARGE02", arAmtS);
			} else if ("022.01".equals(rexpCode)||"022.02".equals(rexpCode)) { // 西药=抗生素+非抗生素
				wrtOffAmt103 = wrtOffAmt103 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt103);
				chargeParm.setData("CHARGE03", arAmtS);
//			} else if ("104".equals(rexpCode)) { // 检查费
//				wrtOffAmt104 = wrtOffAmt104 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt104);
//				chargeParm.setData("CHARGE04", arAmtS);
			} else if ("023".equals(rexpCode)) { // 中成药
				wrtOffAmt105 = wrtOffAmt105 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt105);
				chargeParm.setData("CHARGE05", arAmtS);
			} else if ("024".equals(rexpCode)) { // 中草药
				wrtOffAmt106 = wrtOffAmt106 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt106);
				chargeParm.setData("CHARGE06", arAmtS);
			} else if ("025".equals(rexpCode)) { // 检查费
				wrtOffAmt107 = wrtOffAmt107 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt107);
				chargeParm.setData("CHARGE07", arAmtS);
			} else if ("026".equals(rexpCode)) { // 治疗费
				wrtOffAmt108 = wrtOffAmt108 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt108);
				chargeParm.setData("CHARGE08", arAmtS);
			} else if ("027".equals(rexpCode)) { // 放射费
				wrtOffAmt109 = wrtOffAmt109 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt109);
				chargeParm.setData("CHARGE09", arAmtS);
			} else if ("028".equals(rexpCode)) { // 手术费
				wrtOffAmt110 = wrtOffAmt110 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt110);
				chargeParm.setData("CHARGE10", arAmtS);
			} else if ("029".equals(rexpCode)) { // 化验
				wrtOffAmt111 = wrtOffAmt111 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt111);
				chargeParm.setData("CHARGE11", arAmtS);
			} else if ("02A".equals(rexpCode)) { // 输血费
				wrtOffAmt112 = wrtOffAmt112 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt112);
				chargeParm.setData("CHARGE12", arAmtS);
			} else if ("02B".equals(rexpCode)) { // 氧气费
				wrtOffAmt113 = wrtOffAmt113 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt113);
				chargeParm.setData("CHARGE13", arAmtS);
			} else if ("02C".equals(rexpCode)) { // 接生
				wrtOffAmt114 = wrtOffAmt114 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt114);
				chargeParm.setData("CHARGE14", arAmtS);
			} else if ("02D".equals(rexpCode)) { // 护理
				wrtOffAmt115 = wrtOffAmt115 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt115);
				chargeParm.setData("CHARGE15", arAmtS);
			} else if ("02E".equals(rexpCode)) { // 家床
				wrtOffAmt116 = wrtOffAmt116 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt116);
				chargeParm.setData("CHARGE16", arAmtS);
			} else if ("032".equals(rexpCode)) { // CT
				wrtOffAmt117 = wrtOffAmt117 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt117);
				chargeParm.setData("CHARGE17", arAmtS);
			} else if ("033".equals(rexpCode)) { // MR
				wrtOffAmt118 = wrtOffAmt118 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt118);
				chargeParm.setData("CHARGE18", arAmtS);
			} else if ("02F".equals(rexpCode)) { // 自费
				wrtOffAmt119 = wrtOffAmt119 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt119);
				chargeParm.setData("CHARGE19", arAmtS);
			}
			//====zhangp 20120307 modify start
			else if ("035".equals(rexpCode)) { // 自费
				wrtOffAmt120 = wrtOffAmt120 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt120);
				chargeParm.setData("CHARGE20", arAmtS);
			}
			//===zhangp 20120307 modify end
		}
		charge.setData("CHARGE01", "TEXT",
				chargeParm.getData("CHARGE01") == null ? 0.00 : chargeParm
						.getData("CHARGE01"));
		charge.setData("CHARGE02", "TEXT",
				chargeParm.getData("CHARGE02") == null ? 0.00 : chargeParm
						.getData("CHARGE02"));
		charge.setData("CHARGE03", "TEXT",
				chargeParm.getData("CHARGE03") == null ? 0.00 : chargeParm
						.getData("CHARGE03"));
//		charge.setData("CHARGE04", "TEXT",
//				chargeParm.getData("CHARGE04") == null ? 0.00 : chargeParm
//						.getData("CHARGE04"));
		//======zhangp modify end 20120224
        charge.setData("CHARGE05","TEXT",
                       chargeParm.getData("CHARGE05") == null ? 0.00 :
                       chargeParm.getData("CHARGE05"));
        charge.setData("CHARGE06","TEXT",
                       chargeParm.getData("CHARGE06") == null ? 0.00 :
                       chargeParm.getData("CHARGE06"));
        charge.setData("CHARGE07","TEXT",
                       chargeParm.getData("CHARGE07") == null ? 0.00 :
                       chargeParm.getData("CHARGE07"));
        charge.setData("CHARGE08","TEXT",
                       chargeParm.getData("CHARGE08") == null ? 0.00 :
                       chargeParm.getData("CHARGE08"));
        charge.setData("CHARGE09","TEXT",
                       chargeParm.getData("CHARGE09") == null ? 0.00 :
                       chargeParm.getData("CHARGE09"));
        charge.setData("CHARGE10","TEXT",
                       chargeParm.getData("CHARGE10") == null ? 0.00 :
                       chargeParm.getData("CHARGE10"));
        charge.setData("CHARGE11","TEXT",
                       chargeParm.getData("CHARGE11") == null ? 0.00 :
                       chargeParm.getData("CHARGE11"));
        charge.setData("CHARGE12","TEXT",
                       chargeParm.getData("CHARGE12") == null ? 0.00 :
                       chargeParm.getData("CHARGE12"));
        charge.setData("CHARGE13","TEXT",
                       chargeParm.getData("CHARGE13") == null ? 0.00 :
                       chargeParm.getData("CHARGE13"));
        charge.setData("CHARGE14","TEXT",
                       chargeParm.getData("CHARGE14") == null ? 0.00 :
                       chargeParm.getData("CHARGE14"));
        charge.setData("CHARGE15","TEXT",
                       chargeParm.getData("CHARGE15") == null ? 0.00 :
                       chargeParm.getData("CHARGE15"));
        charge.setData("CHARGE16","TEXT",
                       chargeParm.getData("CHARGE16") == null ? 0.00 :
                       chargeParm.getData("CHARGE16"));
        charge.setData("CHARGE17","TEXT",
                       chargeParm.getData("CHARGE17") == null ? 0.00 :
                       chargeParm.getData("CHARGE17"));
        charge.setData("CHARGE18","TEXT",
                       chargeParm.getData("CHARGE18") == null ? 0.00 :
                       chargeParm.getData("CHARGE18"));
        charge.setData("CHARGE19","TEXT",
                       chargeParm.getData("CHARGE19") == null ? 0.00 :
                       chargeParm.getData("CHARGE19"));
        //===zhangp 20120307 modify start
        charge.setData("CHARGE20","TEXT",
        			chargeParm.getData("CHARGE20") == null ? 0.00 :
        			chargeParm.getData("CHARGE20"));
        //===zhangp 20120307 modify end
        charge.setData("TOT_AMT","TEXT", formatObject.format(totAmt));
        charge.setData("AR_AMT","TEXT", formatObject.format(totAmt));
        return charge;
    }
    /**
     * 校验开关帐
     * @return boolean
     */
    public boolean checkNo() {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", "IBS");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        String updateNo = noParm.getValue("UPDATE_NO", 0);
        if (updateNo == null || updateNo.length() == 0) {
            return false;
        }
        return true;
    }
    /**
     * 获得医保数据
     * ====zhangp 20120412
     * @param caseNo String
     * @return TParm
     */
    public TParm getInsParm(String caseNo,String receiptNo){
    	String sql = " SELECT CONFIRM_NO,SDISEASE_CODE FROM INS_ADM_CONFIRM " +
    			     " WHERE CASE_NO = '"+caseNo+"'" +
    			     " AND IN_STATUS <> '5'";
        TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(confirmParm.getErrCode()<0){
        	messageBox("医保读取错误");
        	return confirmParm;
        }
        if(confirmParm.getCount()<0){
//          messageBox("无资格确认书");//=====wanglong delete 20140408 非医保病患不提示
        	return confirmParm;
        }
    	TParm parm  = getBillmEndDate(receiptNo);
        String enddate = ""; 
        String appdate = "";
        String confirmNo = "";
        Timestamp yesterday = StringTool.rollDate(
				StringTool.getTimestamp(""+parm.getData("END_DATE",0), "yyyy-MM-dd HH:mm:ss"), -1);
        enddate  = StringTool.getString(yesterday, "yyyyMMdd") ;
        Timestamp sysDate = SystemTool.getInstance().getDate();
        appdate = StringTool.getString(sysDate,"yyyyMMdd");
        System.out.println("enddate:"+enddate+"  "+appdate);
        int count = confirmParm.getCount("CONFIRM_NO");
        //同一年，并且confirmNo记录为2的 取带KN的资格确认书
        if (enddate.substring(0, 4).equals(appdate.substring(0, 4)) &&
            count > 1) {
          confirmNo = getConfirmNo(confirmParm, "KN");
        }
        //取非KN的资格确认书
        else {
          confirmNo = getConfirmNo(confirmParm, "");
        }
//        System.out.println("confirmNo:"+confirmNo);
        //普通病人
        if((""+confirmParm.getData("SDISEASE_CODE", 0)).length()==0)
        sql = "SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
        		" B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE,B.ARMYAI_AMT," +
        		" ( CASE" +
        		" WHEN TOT_PUBMANADD_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE TOT_PUBMANADD_AMT" +
        		" END" +
        		" + B.NHI_PAY" +
        		" + CASE" +
        		" WHEN B.REFUSE_TOTAL_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE B.REFUSE_TOTAL_AMT" +
        		" END" +
        		" ) NHI_PAY," +
        		" B.NHI_COMMENT," +
        		" (  B.OWN_AMT" +
        		" + B.ADD_AMT" +
        		" + B.RESTART_STANDARD_AMT" +
        		" + B.STARTPAY_OWN_AMT" +
        		" + B.PERCOPAYMENT_RATE_AMT" +
        		" + B.INS_HIGHLIMIT_AMT" +
        		" - CASE" +
        		" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE B.ACCOUNT_PAY_AMT" +
        		" END" +
        		" - CASE" +
        		" WHEN ARMYAI_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE ARMYAI_AMT" +
        		" END" +
        		" - CASE" +
                " WHEN ILLNESS_SUBSIDY_AMT IS NULL" +
                " THEN 0" +
                " ELSE ILLNESS_SUBSIDY_AMT" +
                " END" +
        		" - CASE" +
        		" WHEN TOT_PUBMANADD_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE TOT_PUBMANADD_AMT" +
        		" END" +
        		" ) OWN_PAY," +
        		//" (  B.NHI_COMMENT" +
        		//" + B.NHI_PAY" +
        		//" + B.OWN_AMT" +
        		//" + B.ADD_AMT" +
        		//" + B.RESTART_STANDARD_AMT" +
        		//" + B.STARTPAY_OWN_AMT" +
        		//" + B.PERCOPAYMENT_RATE_AMT" +
        		//" + B.INS_HIGHLIMIT_AMT" +
        		//" + CASE" +
        		//" WHEN B.REFUSE_TOTAL_AMT IS NULL" +
        		//" THEN 0" +
        		//" ELSE B.REFUSE_TOTAL_AMT" +
        		//" END" +
        		//" ) AR_AMT," +
        		" (B.PHA_AMT  + B.EXM_AMT  + B.TREAT_AMT +  B.OP_AMT  +  B.BED_AMT  +" +
                " B.MATERIAL_AMT + B.OTHER_AMT + B.BLOODALL_AMT  +  B.BLOOD_AMT ) AR_AMT,"+
        		" (CASE" +
        		" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE B.ACCOUNT_PAY_AMT" +
        		" END" +
        		" ) ACCOUNT_PAY_AMT,B.ILLNESS_SUBSIDY_AMT,B.CONFIRM_NO," +
        		" B.INS_CROWD_TYPE,A.IN_DATE,A.SDISEASE_CODE" +
        		" FROM INS_ADM_CONFIRM A, INS_IBS B" +
        		" WHERE B.REGION_CODE = '"+Operator.getRegion()+"'" +
        		" AND A.CONFIRM_NO = '"+confirmNo+"'" +
        		" AND A.CONFIRM_NO = B.CONFIRM_NO " +
        		" AND B.CASE_NO = '"+caseNo+"'" +
        		" AND A.IN_STATUS IN ('1', '2', '3', '4')";
        //单病种
        else
        	sql = "SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
            " B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE,B.ARMYAI_AMT," +
            " ( CASE" +
            " WHEN TOT_PUBMANADD_AMT IS NULL" +
            " THEN 0" +
            " ELSE TOT_PUBMANADD_AMT" +
            " END" +
            " + B.NHI_PAY + B.SINGLE_STANDARD_OWN_AMT - B.SINGLE_SUPPLYING_AMT" +
            " ) NHI_PAY," +
            " B.NHI_COMMENT," +
            " (  B.STARTPAY_OWN_AMT + B.BED_SINGLE_AMT + B.MATERIAL_SINGLE_AMT " +
            " + B.PERCOPAYMENT_RATE_AMT" +
            " + B.INS_HIGHLIMIT_AMT" +
            " - CASE" +
            " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
            " THEN 0" +
            " ELSE B.ACCOUNT_PAY_AMT" +
            " END" +
            " - CASE" +
            " WHEN ARMYAI_AMT IS NULL" +
            " THEN 0" +
            " ELSE ARMYAI_AMT" +
            " END" +
            " - CASE" +
            " WHEN ILLNESS_SUBSIDY_AMT IS NULL" +
            " THEN 0" +
            " ELSE ILLNESS_SUBSIDY_AMT" +
            " END" +
            " - CASE" +
            " WHEN TOT_PUBMANADD_AMT IS NULL" +
            " THEN 0" +
            " ELSE TOT_PUBMANADD_AMT" +
            " END" +
            " ) OWN_PAY," +
            //单病种患者报销金额与总费用不等
            " (B.PHA_AMT  + B.EXM_AMT  + B.TREAT_AMT +  B.OP_AMT  +  B.BED_AMT  + B.MATERIAL_AMT + B.OTHER_AMT + B.BLOODALL_AMT  +  B.BLOOD_AMT )  AS AR_AMT, "+
            " (CASE" +
            " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
            " THEN 0" +
            " ELSE B.ACCOUNT_PAY_AMT" +
            " END" +
            " ) ACCOUNT_PAY_AMT,B.ILLNESS_SUBSIDY_AMT,B.CONFIRM_NO, " +
            " B.INS_CROWD_TYPE,A.IN_DATE,A.SDISEASE_CODE" +
            " FROM INS_ADM_CONFIRM A, INS_IBS B" +
            " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
            " AND A.CONFIRM_NO = '" + confirmParm.getData("CONFIRM_NO", 0) +
            "'" +
            " AND B.CASE_NO = '" + caseNo + "'" +
            " AND A.IN_STATUS IN ('1', '2', '3', '4')"; 	
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParm.getErrCode()<0){
        	messageBox("医保读取错误");
        	return insParm;
        }
        if(insParm.getCount()<0){
        	messageBox("无医保数据");
        	return insParm;
        }
        TParm result = new TParm();
        result = insParm.getRow(0);
        result.setCount(1);
    	return result;

    }
    /**
     * 取得收据对应账单结束时间
     * @return
     */
    public TParm getBillmEndDate(String receiptNo)
    {
	  String sql = " SELECT  MAX(A.END_DATE) AS END_DATE FROM JAVAHIS.IBS_BILLM A" +
	  		       " WHERE  A.RECEIPT_NO  = '"+receiptNo+"'";
	  System.out.println("sql:"+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));   
	  System.out.println("getBillmEndDate:"+result);
      if(result.getErrCode()<0){
      	messageBox("数据读取错误");
      	return result;
      }	  
      return result;	
    }
    /**
     * 取得资格确认书
     * @param caseNo
     * @return
     */
    public TParm getInsAdmConfirm(String caseNo)
    {
    	String sql = "SELECT CONFIRM_NO FROM INS_ADM_CONFIRM WHERE CASE_NO = '"+caseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        System.out.println("getInsAdmConfirm:"+result);
        if(result.getErrCode()<0)
        {
          	messageBox("数据读取错误");
          	return result;
         }	         
        return result;
    }
    /**
     * 过滤资格确认书号
     * @param parm
     * @param type
     * @return
     */
    public String getConfirmNo(TParm parm, String type) {
        String knConfirmNo = "";
        String confirmNo = "";
        for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++) {
          String s = ""+parm.getData("CONFIRM_NO", i);
          if (s.startsWith("KN")) {
            knConfirmNo = s;
          }
          else {
            confirmNo = s;
          }
        }

        if (type.equals("KN")) {
          return knConfirmNo;
        }
        else {
          return confirmNo;
        }
      }    
}
