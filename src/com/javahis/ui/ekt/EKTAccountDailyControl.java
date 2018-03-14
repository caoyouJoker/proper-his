package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

//import javax.print.DocFlavor.STRING;

//import jdo.bil.BILAccountTool;
import jdo.bil.BILSysParmTool;
import jdo.ekt.EKTTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title: ҽ�ƿ��ۿ������ս������</p>
*
* <p>Description: ҽ�ƿ��ۿ������ս������</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author zhangp
* @version 1.0
*/

public class EKTAccountDailyControl extends TControl{
	String accountSeq = "";
	String todayTime="";
	String endTime="";
	String accountTime="";
	String startTime="";
    public void onInit() {
        super.onInit();
        //table���������¼�
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        TTable table = (TTable)this.getComponent("Table");
        //table����checkBox�¼�
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        String opid = Operator.getID();
        setValue("CASHIER_CODE", opid);

    }
    /**
     * ��ʼ������
     */
    public void initPage() {
        //��ʼ��Ժ��
        setValue("REGION_CODE", Operator.getRegion());
        //��ʼ����ѯ��ʱ,��ʱ
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("S_DATE", yesterday);
        setValue("E_DATE", today);
        String todayTime = StringTool.getString(today, "HH:mm:ss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
            accountTime =getStartTime(accountTime);//=========pangben modify 20110411
        }
        //��ѯ������ʼ����������
        String[] s_time=  accountTime.split(":");
        //�����ݿ��ѯ����ʼ�������һ�뷽��
        s_time=  startTimeTemp(s_time,s_time.length-1);
        //ת��ҳ����ʾ�ĸ�ʽ
        setValue("S_TIME", getStartTime(startTime(s_time)));
        setValue("E_TIME", accountTime);

        //���սᰴťΪ��
        callFunction("UI|unreg|setEnabled", false);
        callFunction("UI|arrive|setEnabled", false);

    }
    /**
     * �õ�����ʱ���
     * @return String
     */
    public String getAccountDate() {
        String accountDate = "";
        TParm accountDateParm = new TParm();
        accountDateParm = BILSysParmTool.getInstance().getDayCycle("O");
        accountDate = accountDateParm.getValue("DAY_CYCLE", 0);
        return accountDate;
    }
    /**
     * ���ʱ���ʽ�Ŀ�ʼ����
     * @param accountTime String
     * @return String
     */
    public String getStartTime(String accountTime){
        return accountTime.substring(0, 2) + ":" +
              accountTime.substring(2, 4) + ":" + accountTime.substring(4, 6);
    }
    /**
     * ��ʼ�������һ��ĵݹ鷽��
     * @param time String[]
     * @param i int
     * @return String[]
     */
    public String[] startTimeTemp(String[] time,int i){
        if(i<0)
             return time;//���صõ�������
        else {
            //�ж��Ƿ��ǿ��Խ�λ��ʱ������
            if (Integer.parseInt(time[i]) == 59 ||
                Integer.parseInt(time[i]) == 23) {
                time[i] = "00";
            } else {
                //���ܽ�λ�����һ����
                if((Integer.parseInt(time[i]) + 1)<10)
                    time[i] =  "0"+(Integer.parseInt(time[i]) + 1) ;
                else
                    time[i] =  ""+(Integer.parseInt(time[i]) + 1) ;
                i=-1;//�˳��ݹ�ѭ��
            }
           return startTimeTemp(time,i-1);
        }
      }
    /**
     * ������ת����ÿ�ʼʱ���ַ�������
     * @param startTime String[]
     * @return String
     */
    public String startTime(String[] startTime){
        String s_time="";
        for(int i=0;i<startTime.length;i++)
            s_time+=startTime[i];
        return s_time;
    }
    
    /**
     * �ս�
     */
    public void onSave() {
        TParm parm = new TParm();
        String today = StringTool.getString(TypeTool.getTimestamp(SystemTool.
            getInstance().getDate()), "yyyyMMdd");
        String todayTime = StringTool.getString(SystemTool.getInstance().
                                                getDate(), "HHmmss");
        String accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
        }
        parm.setData("BUSINESS_DATE", today + accountTime);
        String accountNo = SystemTool.getInstance().getNo("ALL","EKT","ACCOUNT_SEQ","ACCOUNT_SEQ");
        String casherUser= this.getValueString("CASHIER_CODE");
        TParm casherParm;
        if (casherUser == null || casherUser.length() == 0) {
            //�õ�ȫ�������ս���Ա
            casherParm = getAccountUser(Operator.getRegion());
            //ȡ����Ա�ĸ���
            int row = casherParm.getCount();
            //ѭ�����е��շ�Ա
            //===zhangp 20120227 modify start
            List<String> successCashier = new ArrayList<String>();
            List<String>  faileCashier = new ArrayList<String>();
            //====zhangp 20120227 modify end
            for (int i = 0; i < row; i++) {
                //ȡ��һ���շ���Ա
                casherUser = casherParm.getValue("USER_ID", i);
                //����һ���˵��ս����
                if (!accountOneCasher(parm,accountNo,casherUser)) {
//                    messageBox("" + casherParm.getValue("USER_NAME", i) +
//                               "�ս�ʧ��!");
                	//====zhangp 20120227 modify start
//                    this.messageBox(casherParm.getValue("USER_NAME", i) + "���ս�����!");
                	faileCashier.add(casherParm.getValue("USER_NAME", i));
                	//=======zhangp 20120227 modify end
                    continue;
                }
              //====zhangp 20120227 modify start
//                messageBox(casherParm.getValue("USER_NAME", i) + "�ս�ɹ�!");
                successCashier.add(casherParm.getValue("USER_NAME", i));
              //=======zhangp 20120227 modify end
            }
          //====zhangp 20120227 modify start
            String successCashiers = "";
            String faileCashiers = "";
            if(successCashier.size()>0){
            	for (int i = 0; i < successCashier.size(); i++) {
            		if(i%10==0){
            			successCashiers = successCashiers+"," + successCashier.get(i) + "\n";
            		}else{
            			successCashiers = successCashiers+"," + successCashier.get(i);
            		}
				}
            }
            if(faileCashier.size()>0){
            	for (int i = 0; i < faileCashier.size(); i++) {
            		if(i%10==0){
            			faileCashiers = faileCashiers+"," + faileCashier.get(i) + "\n";
            		}else{
            			faileCashiers = faileCashiers+"," + faileCashier.get(i);
            		}
				}
            }
            if(!faileCashiers.equals("")){
            	messageBox(faileCashiers+"\n���ս�����!");
            }
            if(!successCashiers.equals("")){
            	messageBox(successCashiers+"\n�ս�ɹ�!");
            }
          //=======zhangp 20120227 modify end
            return ;
        }else{
            if (!accountOneCasher(parm,accountNo,casherUser)) {
                    messageBox("���ս�����!");
                    return ;
                }
                messageBox("�ս�ɹ�!");
            return ;
        }
        
    }
    
    /**
     * �õ��ս���Ա��
     * @return String[]
     */
    public TParm getAccountUser(String regionCode) {
        String region = "";
        if (!"".equals(regionCode) && null != regionCode)
            region = " AND region_code = '" + regionCode + "' ";
        String sql =
                "SELECT user_id AS USER_ID, user_name AS USER_NAME, user_eng_name AS enname, py1, py2" +
                "  FROM sys_operator "
                + " WHERE pos_code IN (SELECT pos_code"
                + " FROM sys_position"
                + " WHERE pos_type = '5') " + region +//pos_type = '5'�շ�ԱȨ��
                "ORDER BY user_id";
        TParm accountUser = new TParm(TJDODBTool.getInstance().select(sql));
        if (accountUser.getErrCode() < 0)
            System.out.println(" ȡ���շ�Ա " + accountUser.getErrText());
        return accountUser;
    }
    
    /**
     * �ս�ʵ�������û��ս᷽��
     * @param parm TParm
     * @param accountNo String
     * @return boolean
     */
    public boolean  accountOneCasher(TParm parm,String accountNo,String casherUser){
        parm.setData("ACCOUNT_USER", casherUser);
        parm.setData("ACCOUNT_DATE", SystemTool.getInstance().getDate());
        parm.setData("ACCOUNT_SEQ", accountNo);
        parm.setData("ACCOUNT_TYPE", "EKT");
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CASHIER_CODE",Operator.getID());
        parm.setData("ADM_TYPE","T");
        TParm result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
                "onEKTAccount", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        } else {
            //�ս�ɹ�
            return true;
        }
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
         startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
         endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        Timestamp today = SystemTool.getInstance().getDate();
         todayTime = StringTool.getString(today, "HHmmss");
         accountTime = todayTime;
        if (getAccountDate().length() != 0) {
            accountTime = getAccountDate();
            accountTime = accountTime.substring(0, 2) +
                accountTime.substring(2, 4) + accountTime.substring(4, 6);
        }
        TParm result = new TParm();
        TParm selAccountData = new TParm();
        selAccountData.setData("ACCOUNT_TYPE", "REG");
        if (this.getValueString("CASHIER_CODE") == null ||
            this.getValueString("CASHIER_CODE").length() == 0) {}
        else
            selAccountData.setData("ACCOUNT_USER", this.getValueString("CASHIER_CODE"));
        selAccountData.setData("S_TIME", startTime + accountTime);
        selAccountData.setData("E_TIME", endTime + accountTime);
        if(this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals(""))
            selAccountData.setData("REGION_CODE",this.getValue("REGION_CODE"));
//         if(this.getValue("ADM_TYPE")!=null&&!this.getValue("ADM_TYPE").equals(""))
//            selAccountData.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        result = EKTTool.getInstance().accountQuery(selAccountData);
        if(result.getCount()==0)
            this.messageBox("û��Ҫ��ѯ������");
        this.callFunction("UI|Table|setParmValue", result);

    }
    
    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("SELECT","N");
        setValue("TOGEDER_FLG","N");
        setValue("FLG","N");
        accountSeq = new String();
    }
    
    /**
     * ��ӡ
     */
    public void onPrint() {
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("S_TIME");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("E_TIME");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd");
        //��ӡÿһ���ս�����
        if ("N".equals(getValue("TOGEDER_FLG"))) {
           String [] accoutSeqs= accountSeq.split(",");
            for(int i=0;i<accoutSeqs.length;i++){
            	getPrintValue(accoutSeqs[i],sysDate);
            }
        } else {
            //���ս����ݻ���
        	getPrintValue(accountSeq,sysDate);
        }
       



    }
    
    /**
     * ��ӡ����
     */
    public void getPrintValue(String accoutSeq,String sysDate){
    	String sql = "SELECT ACCOUNT_USER,ACCOUNT_DATE,BUY_QTY,CHANGE_QTY,ADD_QTY,SENT_COUNT,FACTORAGE_QTY,FACTORAGE_AMT," +
    			"PAY_MEDICAL_ATM,PAY_MEDICAL_QTY,NPAY_MEDICAL_AMT,NPAY_MEDICAL_QTY,REGION_CODE," +
    			"AR_AMT FROM EKT_ACCOUNT WHERE ACCOUNT_SEQ IN (" + accoutSeq + ")";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		this.messageBox("��ѡ��Ҫ��ӡ�ļ�¼");
    		return;
    	}
    	//zhangp 20120131 �ϲ�
    	int count = 1;
    	if("Y".equals(getValue("TOGEDER_FLG"))){
    		String[] accoutSeqs = accoutSeq.split(",");
    		count = accoutSeqs.length;
    	}
    	//int tot=0;
    	//int used=0;
    	int cancel_qty=0;//��������
    	int change_qty = 0 ;//��������
    	int npay_medical_qty = 0;//�˷�����
    	int sent_count = 0;//��������
    	int factorage_qty = 0;//��������
    	int add_qty = 0;//��ֵ����
    	int buy_qty = 0;//��������
    	int pay_medical_qty = 0;//��ֵ����
    	double npay_medical_amt = 0.0;//�˷ѽ��
    	double factorage_amt = 0.0;//������
    	double ar_amt = 0.0;//�ܽ��
    	double pay_medical_atm = 0.0;//��ֵ����ܼ�
    	String account_user = "";
    	String account_date = "";
    	for (int i = 0; i < count; i++) {
    		change_qty = change_qty + result.getInt("CHANGE_QTY", i);
        	npay_medical_qty = npay_medical_qty + result.getInt("NPAY_MEDICAL_QTY",i);
        	sent_count = sent_count + result.getInt("SENT_COUNT",i);
        	factorage_qty = factorage_qty + result.getInt("FACTORAGE_QTY",i);
        	add_qty = add_qty + result.getInt("ADD_QTY",i);
        	buy_qty = buy_qty + result.getInt("BUY_QTY",i);
        	pay_medical_qty = pay_medical_qty + result.getInt("PAY_MEDICAL_QTY",i);
        	npay_medical_amt = npay_medical_amt + result.getDouble("NPAY_MEDICAL_AMT",i);
        	factorage_amt = factorage_amt + result.getDouble("FACTORAGE_AMT",i);
        	ar_amt = ar_amt + result.getDouble("AR_AMT",i);
        	pay_medical_atm = pay_medical_atm + result.getDouble("PAY_MEDICAL_ATM",i);
        	if(i!=0){
        		account_user = account_user +","+ result.getData("ACCOUNT_USER", i).toString();
        		account_date = account_date + "," + result.getData("ACCOUNT_DATE", i).toString().substring(0, 4)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(5, 7)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(8, 10);
        	}else{
        		account_user = result.getData("ACCOUNT_USER", i).toString();
        		account_date = result.getData("ACCOUNT_DATE", i).toString().substring(0, 4)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(5, 7)+"/"+
            	result.getData("ACCOUNT_DATE", i).toString().substring(8, 10);
        	}
		}
    	String region_code = result.getData("REGION_CODE").toString();
    	TTable table = ((TTable)this.getComponent("Table"));
    	String region = table.getParmValue().getRow(0).getValue("REGION_CHN_DESC");
    	String billsql="";
    	 TParm billParm=null;
    	 String inv_nos = "";
    	if("Y".equals(this.getValue("FLG"))){
    		 billsql =
    		"SELECT BUSINESS_DATE,BUSINESS_NO,CASHIER_CODE FROM EKT_ACCNTDETAIL  WHERE ACCOUNT_SEQ IN ("+accoutSeq+") AND CHARGE_FLG IN ('3','4','5','7','8') ORDER BY BUSINESS_NO";//(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,7,�˷�,8,����)
    		  billParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	}else{
    	 billsql ="SELECT INV_NO,PRINT_DATE BUSINESS_DATE FROM BIL_INVRCP  WHERE ACCOUNT_SEQ IN ("+accoutSeq+")" +
    			" AND RECP_TYPE='EKT' " +
    			" ORDER BY INV_NO";
    	  billParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	//add by kangy
  		String inv_no = billParm.getValue("INV_NO", 0);
  		TParm regParm = new TParm();
  		List<String> reglist = new ArrayList<String>();
  		reglist.add(billParm.getValue("INV_NO", 0));
  		int regcount = 0;
  		for (int i = 1; i < billParm.getCount(); i++) {
  				if (!compareInvno(billParm.getValue("INV_NO", i), inv_no)) {
  					inv_nos += reglist.get(0) + "~"
  							+ reglist.get(reglist.size() - 1) + ";";
  					regcount += reglist.size();
  					reglist = new ArrayList<String>();
  				}
  			inv_no = billParm.getValue("INV_NO", i);
  			reglist.add(billParm.getValue("INV_NO", i));
  		}
  		if (reglist.size() > 1) {
  			inv_nos += reglist.get(0) + "~" + reglist.get(reglist.size() - 1)
  					+ ";";
  		}
  		if (reglist.size()==1) {
  			inv_nos += reglist.get(0) + ";";
  		}
  		regcount += reglist.size();
  		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
  		
  		String cancelSql = "SELECT COUNT(*) CANCEL_TOT FROM BIL_INVRCP WHERE ACCOUNT_SEQ IN ("+accoutSeq+") AND RECP_TYPE='EKT' AND CANCEL_FLG='3'" ;
  		//String s="SELECT COUNT(*) USED FROM EKT_ACCNTDETAIL WHERE ACCOUNT_SEQ IN("+accoutSeq+") ";
  		//TParm UsedParm=new TParm(TJDODBTool.getInstance().select(s));
  		TParm TOTParm=new TParm(TJDODBTool.getInstance().select(cancelSql));
  		 cancel_qty= TOTParm.getInt("CANCEL_TOT",0);
  		 //used=UsedParm.getInt("USED",0);
  		 
  		/*if(tot-used>0){
  			cancel_qty=used-tot;
  		}*/
    	 }
    	billsql = 
    		"SELECT CASHIER_CODE FROM EKT_ACCNTDETAIL  WHERE ACCOUNT_SEQ IN ("+accoutSeq+") AND CHARGE_FLG IN ('3','4','5','7','8') GROUP BY CASHIER_CODE ORDER BY CASHIER_CODE";//(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
    	TParm billUserParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	billsql = 
    		//===zhangp 201203 start
    		"SELECT SUM(AMT) AMT ,SUM(PROCEDURE_AMT) PROCEDURE_AMT,A.GATHER_TYPE,A.ACCNT_TYPE FROM EKT_BIL_PAY A,EKT_ACCNTDETAIL B "+
    		" WHERE A.BIL_BUSINESS_NO = B.BUSINESS_NO AND B.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY A.GATHER_TYPE,A.ACCNT_TYPE ORDER BY A.ACCNT_TYPE";
    	//===zhangp 201203 end
//    	"SELECT SUM(AMT+PROCEDURE_AMT) AMT,A.GATHER_TYPE,A.ACCNT_TYPE FROM EKT_BIL_PAY A,EKT_ACCNTDETAIL B "+
//    	" WHERE A.BIL_BUSINESS_NO = B.BUSINESS_NO AND B.ACCOUNT_SEQ IN ("+accoutSeq+") GROUP BY A.GATHER_TYPE,A.ACCNT_TYPE ORDER BY A.ACCNT_TYPE";
    	TParm gatherParm = new TParm(TJDODBTool.getInstance().select(billsql));
    	double inCash = 0.00;//�ֽ�����
    	double inB = 0.00;//֧Ʊ����
    	double inCard = 0.00;//ˢ������
    	double outCash = 0.00;//�ֽ��˷�
    	double outB = 0.00;//֧Ʊ�˷�
    	double outCard = 0.00;//ˢ���˷�
    	for (int i = 0; i < gatherParm.getCount(); i++) {
			if(gatherParm.getData("ACCNT_TYPE",i).equals("2")){//(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0�ֽ�д��
					inCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1���п���д��
						inCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0֧Ʊ��д��
							inB+= gatherParm.getDouble("AMT", i);
						}
			}
			if(gatherParm.getData("ACCNT_TYPE",i).equals("4")){//(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0�ֽ�д��
					inCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1���п���д��
						inCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0֧Ʊ��д��
							inB+= gatherParm.getDouble("AMT", i);
						}
			}
			if(gatherParm.getData("ACCNT_TYPE",i).equals("6")){//(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
				if(gatherParm.getData("GATHER_TYPE",i).equals("C0")){//C0�ֽ�д��
					outCash+= gatherParm.getDouble("AMT", i);
				}else
					if(gatherParm.getData("GATHER_TYPE",i).equals("C1")){//C1���п���д��
						outCard+= gatherParm.getDouble("AMT", i);
					}else
						if(gatherParm.getData("GATHER_TYPE",i).equals("T0")){//T0֧Ʊ��д��
							outB+= gatherParm.getDouble("AMT", i);
						}
			}
		}
    	double totCash = inCash -outCash;
    	double totB = inB - outB;
    	double totCard = inCard - outCard;
    	String billUser = "";
    	if(billUserParm.getCount()<0){
    		messageBox("δ�ҵ��ս���Ա");
    		return;
    	}
    	String billNo="";
    	String date="";
    	for (int i = 0; i < billUserParm.getCount(); i++) {
    		billUser+= ","+ billUserParm.getData("CASHIER_CODE", i);
		}
    	billUser = billUser.substring(1, billUser.length());
    	if("N".equals(this.getValue("FLG"))){
    	String datesql="SELECT INV_NO,PRINT_DATE BUSINESS_DATE FROM BIL_INVRCP  WHERE ACCOUNT_SEQ IN ("+accoutSeq+")" +
		" AND RECP_TYPE='EKT' " +
		" ORDER BY BUSINESS_DATE";
    	TParm dateParm=new TParm(TJDODBTool.getInstance().select(datesql));
    	 date = getAccntDate(dateParm.getData("BUSINESS_DATE", 0).toString(),dateParm.getData("BUSINESS_DATE", billParm.getCount()-1).toString());
    	 billNo = billParm.getData("PRINT_NO", 0) + " ~ " + billParm.getData("PRINT_NO", billParm.getCount()-1);
    	}else{//������
    		billNo = billParm.getData("BUSINESS_NO", 0) + " ~ " + billParm.getData("BUSINESS_NO", billParm.getCount()-1);
        	 date = getAccntDate(billParm.getData("BUSINESS_DATE", 0).toString(),billParm.getData("BUSINESS_DATE", billParm.getCount()-1).toString());
    	}
    	DecimalFormat df = new DecimalFormat("########0.00");
    	TParm data = new TParm();
    	data.setData("TITLE1", "TEXT", (region != null && !region.equals("") ? region :
            Operator.getHospitalCHNShortName())+"ҽ�ƿ��սᱨ��");
    	String ar_amt_word = StringUtil.getInstance().numberToWord(pay_medical_atm-npay_medical_amt + factorage_amt);
    	if(ar_amt_word.lastIndexOf("��")>0){
    		ar_amt_word=ar_amt_word.substring(0,ar_amt_word.lastIndexOf("��")+1);
    	}
    	data.setData("END_DATE",  account_date);
    	data.setData("PRINT_DATE", sysDate);
    	data.setData("CHANGE_QTY",  change_qty);
    	data.setData("BUY_QTY",  buy_qty);
    	data.setData("ADD_QTY", add_qty);
    	data.setData("SENT_COUNT",  sent_count);
    	data.setData("FACTORAGE_QTY",  factorage_qty);
    	data.setData("PAY_MEDICAL_QTY",  pay_medical_qty);
    	data.setData("NPAY_MEDICAL_QTY",  npay_medical_qty);
    	data.setData("CANCEL_QTY",  cancel_qty);//add by kangy ����Ʊ������
    	data.setData("TOT_MEDICAL_QTY",  npay_medical_qty+pay_medical_qty+cancel_qty);
    	data.setData("FACTORAGE_AMT",  df.format(StringTool.round(factorage_amt,2)));
    	data.setData("PAY_MEDICAL_ATM",  df.format(StringTool.round(pay_medical_atm,2)));
    	data.setData("PAY_MEDICAL_ATM_TOTAL",  df.format(StringTool.round((pay_medical_atm),2)));
    	data.setData("AR_AMT",  df.format(StringTool.round(pay_medical_atm-npay_medical_amt,2)));
    	data.setData("AR_AMT_WORD",  ar_amt_word);
    	data.setData("NPAY_MEDICAL_AMT",  df.format(StringTool.round(npay_medical_amt,2)));
    	data.setData("TOT_MEDICAL_AMT", df.format(StringTool.round((pay_medical_atm-npay_medical_amt),2)));
    	data.setData("NPAY_MEDICAL_AMT_TOTAL", df.format(StringTool.round(npay_medical_amt,2)));
    	data.setData("OPT_NAME","����Ա: "+account_user);
    	data.setData("CASHIER",  billUser);
    	if("N".equals(this.getValue("FLG"))){
    		data.setData("BISSINESS_NO",inv_nos);
    	}else{
    		data.setData("BISSINESS_NO",billNo);
    	}
    	data.setData("BISSINESS_DATE",  date);
    	data.setData("ACCNT_SEQ",  accoutSeq);
    	data.setData("IN_CASH", df.format(StringTool.round(inCash,2)));
    	data.setData("IN_B", df.format(StringTool.round(inB,2)));
    	data.setData("IN_CARD",  df.format(StringTool.round(inCard,2)));
    	data.setData("OUT_CASH", df.format(StringTool.round(outCash,2)));
    	data.setData("OUT_B", df.format(StringTool.round(outB,2)));
    	data.setData("OUT_CARD", df.format(StringTool.round(outCard,2)));
    	data.setData("TOT_CASH", df.format(StringTool.round(totCash,2)));
    	data.setData("TOT_B", df.format(StringTool.round(totB,2)));
    	data.setData("TOT_CARD", df.format(StringTool.round(totCard,2)));
    	data.setData("TOT_AMT", df.format(StringTool.round(totCard,2)));
    	data.setData("TOT_TOT", df.format(StringTool.round(pay_medical_atm-npay_medical_amt + factorage_amt,2)));
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTAccountDaily.jhw",data);

    }
    
    /**
     * table����checkBox�¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        accountSeq = new String();
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int allRow = table.getRowCount();
        StringBuffer allSeq = new StringBuffer();
        for (int i = 0; i < allRow; i++) {
            String seq = "";
            if ("Y".equals(tableParm.getValue("FLG", i))) {
                seq = tableParm.getValue("ACCOUNT_SEQ", i);
                if (allSeq.length() > 0)
                    allSeq.append(",");
                allSeq.append(seq);
            }
        }
        accountSeq = allSeq.toString();
        return true;
    }
    
    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        accountSeq = new String();
        StringBuffer allSeq = new StringBuffer();
        String select = getValueString("SELECT");
        TTable table = (TTable)this.getComponent("Table");
        table.acceptText();
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String seq = "";
            parm.setData("FLG", i, select);
            seq = parm.getValue("ACCOUNT_SEQ", i);
               if (allSeq.length() > 0)
                   allSeq.append(",");
               allSeq.append(seq);
        }
        accountSeq = allSeq.toString();
        table.setParmValue(parm);
    }
    /**
     * ��ȡ��ӡʱ��
     * @param startDate
     * @param enddate
     * @return
     */
    public String getAccntDate(String startDate,String enddate){
    	startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+"/"+startDate.substring(8, 19);
    	enddate = enddate.substring(0, 4)+"/"+enddate.substring(5, 7)+"/"+enddate.substring(8, 19);
    	return startDate + " ~ " + enddate;
    }
    
	/**
	 * �Ƚ�Ʊ��
	 * ===========zhangp 20130312
	 * @param inv_no
	 * @param latestInv_no
	 * @return
	 */
	private boolean compareInvno(String inv_no, String latestInv_no) {
		String inv_no_num = inv_no.replaceAll("[^0-9]", "");// ȥ������
		String inv_no_word = inv_no.replaceAll("[0-9]", "");// ȥ����
		String latestInv_no_num = latestInv_no.replaceAll("[^0-9]", "");
		String latestInv_no_word = latestInv_no.replaceAll("[0-9]", "");
		if (inv_no_word.equals(latestInv_no_word)
				&& Long.valueOf(inv_no_num)
						- Long.valueOf(latestInv_no_num) == 1) {
			return true;
		} else {
			return false;
		}
	}
}