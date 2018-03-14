package com.javahis.ui.bil;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.bil.BILComparator;
import jdo.ibs.IBSBillmTool;
import jdo.ibs.IBSTool;
import jdo.sta.STAOutRecallTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.javahis.util.OdiUtil;

import jdo.sys.Operator;

import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BILPatStatusSelControl extends TControl {
    private BILComparator compare = new BILComparator();//modify by wanglong 20130828
    private boolean ascending = false;
    private int sortColumn =-1;
    TParm endParm;
    /**
     * 1
     */
    private static String TABLE1 = "TABLE1";
    /**
     * 2
     */
    private static String TABLE2 = "TABLE2";
    /**
     * 3
     */
    private static String TABLE3 = "TABLE3";
    /**
     * 4
     */
    private static String TABLE4 = "TABLE4";
    /**
     * ���ճ�Ժ
     */
    private static String TABLE5="TABLE5";
    /**
     * סԺ����>=85,<90()
     */
    Color nsNodeColor = new Color(128, 0, 128);
    // סԺ����>=90Ϊ��ɫ
    Color red = new Color(255, 0, 0);
    
    /**
     * ��ʼ������
     */
    public void onInitParameter() {
//        this.setPopedem("SYSDBA",true);
    }

    public void onInit() {
        super.onInit();
        this.initPage();
    }

    /**
     * �õ�TTabbedPane
     * @param tag String
     * @return TTabbedPane
     */
    public TTabbedPane getTTabbedPane(String tag) {
        return (TTabbedPane)this.getComponent(tag);
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
     * ��ʼ������
     */
    public void initPage() {
        // Ȩ��
        onInitPopeDem();
        // ��ǰʱ��
        Timestamp today = SystemTool.getInstance().getDate();
        // ��ȡѡ�����ڵ�ǰһ�������
        Timestamp yesterday = StringTool.rollDate(today, -1);
        //Timestamp day = StringTool.rollDate(today, 1);
        this.setValue("START_DATE", yesterday);
        this.setValue("END_DATE", today);
		//String startTime = StringTool.getString(today, "yyyy/MM/dd");
		String endTime = StringTool.getString(today, "yyyy/MM/dd");
		setValue("DS_START_DATE", endTime+" 00:00:00");
		setValue("DS_END_DATE", endTime+" 23:59:59");
        callFunction("UI|save|setEnabled", false);
        // �������
        addListener(this.getTTable(TABLE1));
        addListener(this.getTTable(TABLE2));
        addListener(this.getTTable(TABLE4));
        addListener(this.getTTable(TABLE5));
    }

    /**
     * ��ʼ��Ȩ��
     */
    public void onInitPopeDem() {
        // ��ͨ
        if (this.getPopedem("NORMAL")) {
            this.setValue("DEPT_CODE1", Operator.getDept());
            this.setValue("DEPT_CODE2", Operator.getDept());
            this.setValue("DEPT_CODE3", Operator.getDept());
            this.setValue("DS_DEPT_CODE", Operator.getDept());
            this.setValue("DS_STATION_CODE", Operator.getStation());
            this.setValue("STATION_CODE1", Operator.getStation());
            this.setValue("STATION_CODE2", Operator.getStation());
            this.setValue("STATION_CODE3", Operator.getStation());
            getTTabbedPane("TTABBEDPANE").setEnabledAt(1, false);
            this.callFunction("UI|DEPT_CODE1|setEnabled", false);
            this.callFunction("UI|DEPT_CODE2|setEnabled", false);
            this.callFunction("UI|DEPT_CODE3|setEnabled", false);
            this.callFunction("UI|STATION_CODE1|setEnabled", false);
            this.callFunction("UI|STATION_CODE2|setEnabled", false);
            this.callFunction("UI|STATION_CODE3|setEnabled", false);
            return;
        }
        // һ��
        if (this.getPopedem("SYSOPER")) {
            this.setValue("DEPT_CODE1", Operator.getDept());
            this.setValue("DEPT_CODE2", Operator.getDept());
            this.setValue("DEPT_CODE3", Operator.getDept());
            this.setValue("DS_DEPT_CODE", Operator.getDept());
            this.setValue("DS_STATION_CODE", Operator.getStation());
            this.setValue("STATION_CODE1", Operator.getStation());
            this.setValue("STATION_CODE2", Operator.getStation());
            this.setValue("STATION_CODE3", Operator.getStation());
            getTTabbedPane("TTABBEDPANE").setEnabledAt(1, false);
            this.callFunction("UI|DEPT_CODE1|setEnabled", true);
            this.callFunction("UI|DEPT_CODE2|setEnabled", true);
            this.callFunction("UI|DEPT_CODE3|setEnabled", true);
            this.callFunction("UI|STATION_CODE1|setEnabled", true);
            this.callFunction("UI|STATION_CODE2|setEnabled", true);
            this.callFunction("UI|STATION_CODE3|setEnabled", true);
            return;
        }
        // ���
        if (this.getPopedem("SYSDBA")) {
            this.setValue("DEPT_CODE1", Operator.getDept());
            this.setValue("DEPT_CODE2", Operator.getDept());
            this.setValue("DEPT_CODE3", Operator.getDept());
            this.setValue("DS_DEPT_CODE", Operator.getDept());
            this.setValue("DS_STATION_CODE", Operator.getStation());
            this.setValue("STATION_CODE1", Operator.getStation());
            this.setValue("STATION_CODE2", Operator.getStation());
            this.setValue("STATION_CODE3", Operator.getStation());
            getTTabbedPane("TTABBEDPANE").setEnabledAt(1, true);
            this.callFunction("UI|DEPT_CODE1|setEnabled", true);
            this.callFunction("UI|DEPT_CODE2|setEnabled", true);
            this.callFunction("UI|DEPT_CODE3|setEnabled", true);
            this.callFunction("UI|STATION_CODE1|setEnabled", true);
            this.callFunction("UI|STATION_CODE2|setEnabled", true);
            this.callFunction("UI|STATION_CODE3|setEnabled", true);
            return;
        }
        getTTabbedPane("TTABBEDPANE").setEnabledAt(0, false);
        getTTabbedPane("TTABBEDPANE").setEnabledAt(1, false);
        getTTabbedPane("TTABBEDPANE").setEnabledAt(2, false);
        getTTabbedPane("TTABBEDPANE").setEnabledAt(3, false);
        callFunction("UI|query|setEnabled", false);
        callFunction("UI|print|setEnabled", false);
        callFunction("UI|clear|setEnabled", false);
        this.messageBox("δ����Ȩ�ޣ�");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        TParm queryParm = new TParm();
        DecimalFormat df = new DecimalFormat("##########0.00");
        double curAmt = 0.00;//סԺԤ�������
        switch (selTTabbendPane) {
            case 0:          	
                queryParm = new TParm(this.getDBTool().select(getQuerySql(0)));
                // System.out.println("����״̬��ѯ" + queryParm);
                // System.out.println("����" + queryParm.getCount("CASE_NO"));
                // ��������
                //============pangben 2014-8-4 �޸��Ѿ�����Ԥ�����˷ѵĲ���Ҳ���Բ�ѯ������
                TParm bilParm=new TParm();
                String sql="";
                Timestamp sysDate = SystemTool.getInstance().getDate();
                for (int i = 0; i < queryParm.getCount(); i++) {
                	sql="SELECT sum(nvl(PRE_AMT, 0)) TOTAL_BILPAY  FROM BIL_PAY  D WHERE CASE_NO='"+queryParm.getValue("CASE_NO",i)+"' "
                        + "       AND REFUND_FLG = 'N' "
                        + "       AND TRANSACT_TYPE IN ('01', '04') "
                        + "       AND RESET_RECP_NO IS NULL";
                	bilParm= new TParm(this.getDBTool().select(sql));
                	if (bilParm.getCount()>0) {
                		queryParm.setData("TOTAL_BILPAY",i,bilParm.getDouble("TOTAL_BILPAY",0));
					}
                	// ����סԺ����
                    Timestamp tp = queryParm.getTimestamp("DS_DATE", i);
                    if (tp == null) {
                        int days = 0;
                        if (queryParm.getTimestamp("IN_DATE", i) == null) {
                            queryParm.addData("DAYNUM", "");
                        } else {
                            days =
                                    StringTool.getDateDiffer(StringTool.setTime(sysDate, "00:00:00"), 
                                                             StringTool.setTime(queryParm.getTimestamp("IN_DATE", i), "00:00:00"));
                            queryParm.addData("DAYNUM", days == 0 ? 1 : days);
                        }
                    } else {
                        int days = 0;
                        if (queryParm.getTimestamp("IN_DATE", i) == null) {
                            queryParm.addData("DAYNUM", "");
                        } else {
                            days =
                                    StringTool.getDateDiffer(StringTool.setTime(queryParm.getTimestamp("DS_DATE", i), "00:00:00"), 
                                                             StringTool.setTime(queryParm.getTimestamp("IN_DATE", i), "00:00:00"));
                            queryParm.addData("DAYNUM", days == 0 ? 1 : days);
                        }
                    }
                    curAmt = ADMTool.getInstance().updateAdmTotAmt(queryParm.getRow(i));
                	queryParm.setData("CUR_AMT", i, df.format(curAmt));//Ԥ���������ʾ����-xiongwg20150716
				}
                this.setValue("ALLPERSON", queryParm.getCount("CASE_NO") < 0 ? 0 : queryParm.getCount("CASE_NO"));
                break;
            case 1:
                queryParm = new TParm(this.getDBTool().select(getQuerySql(1)));
                for(int i=0;i<queryParm.getCount("MR_NO");i++){
                	curAmt = ADMTool.getInstance().updateAdmTotAmt(queryParm.getRow(i));
                	if(queryParm.getValue("UNLOCKED_FLG",i).equals("0")){
                		queryParm.setData("UNLOCKED_FLG",i,"N");
                		queryParm.setData("LOCK_FLG",i,"N");
                	}
                	if(queryParm.getValue("UNLOCKED_FLG",i).equals("1")){ //��ʱ
                		queryParm.setData("UNLOCKED_FLG",i,"N");
                		queryParm.setData("LOCK_FLG",i,"Y");
                	}
                	if(queryParm.getValue("UNLOCKED_FLG",i).equals("2")){ //����
                		queryParm.setData("UNLOCKED_FLG",i,"Y");
                		queryParm.setData("LOCK_FLG",i,"N");
                	}
                	if(queryParm.getValue("UNLOCKED_FLG",i).equals("3")){ //�ټ�
                		queryParm.setData("UNLOCKED_FLG",i,"N");
                		queryParm.setData("LOCK_FLG",i,"N");
                	}
                	
                	queryParm.setData("CUR_AMT", i, df.format(curAmt));//Ԥ���������ʾ����-xiongwg20150716
                }
                break;
            case 2:
                queryParm = new TParm(this.getDBTool().select(getQuerySql(2)));
                break;
            case 3:
                queryParm = new TParm(this.getDBTool().select(getQuerySql(3)));
                int rowCount = queryParm.getCount();
                for (int i = 0; i < rowCount; i++) {
                    OdiUtil.getInstance();
                    queryParm.setData("BIRTH_DATE", i, OdiUtil.showAge(queryParm.getTimestamp("BIRTH_DATE", i), queryParm.getTimestamp("IN_DATE", i)));
                }
                this.setValue("ONLYCOUNT", rowCount);
                break;
            case 4://���ճ�Ժ
        	    queryParm = new TParm(this.getDBTool().select(getQuerySql(4)));
        	    break;
        }
        if(queryParm.getCount()<=0){
        	this.messageBox("��������");
        	this.getTTable("TABLE" + (selTTabbendPane + 1)).setParmValue(new TParm());
        	return;
        }
        this.getTTable("TABLE" + (selTTabbendPane + 1)).setParmValue(queryParm);
        // ҽ������סԺ��������85���90�����ɫ����
        this.setColor(queryParm);
    }
    
    /**
     * ������ɫ
     */ 
    public void setColor(TParm queryParm) {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        switch (selTTabbendPane) {
            case 0:
                TTable table = this.getTTable(TABLE1);
                // System.out.println("22222===");
                int Count = queryParm.getCount();
                // System.out.println("33333==="+Count);
                for (int i = 0; i < Count; i++) {
                    if (queryParm.getInt("DAYNUM", i) >= 85 && queryParm.getInt("DAYNUM", i) < 90
                            && !queryParm.getData("CTZ1_CODE", i).equals("99")) {
                        table.setRowColor(i, nsNodeColor);
                    } else if (queryParm.getInt("DAYNUM", i) >= 90
                            && !queryParm.getData("CTZ1_CODE", i).equals("99")) {
                        table.setRowColor(i, red);
                    }
                }
                break;
        }
    }
    
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * ���ز�ѯ���
     * @param tableIndex int
     * @return String
     */
    public String getQuerySql(int tableIndex) {
        String sql = "";
        //==========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region.append(" AND A.REGION_CODE='" + Operator.getRegion() + "' ");
        } else
            region.append("");
        //==========pangben modify 20110704 stop
        switch (tableIndex) {
        case 0:
            //================modify by wanglong 20121229========================
        	 sql = "      SELECT B.PAT_NAME,A.MR_NO,A.IPD_NO,A.CASE_NO,B.SEX_CODE,B.BIRTH_DATE,A.DEPT_CODE,"
                 + "             A.STATION_CODE,C.BED_NO_DESC,A.IN_DATE,A.CTZ1_CODE,A.CTZ2_CODE,A.CUR_AMT,CASE WHEN F.TOTAL_AMT<>0 THEN F.TOTAL_AMT ELSE 0 END TOTAL_AMT,A.BILL_STATUS,"
                 + "             A.DS_DATE,0 TOTAL_BILPAY"
                 + "        FROM ADM_INP A,SYS_PATINFO B,SYS_BED C,(SELECT A.CASE_NO,SUM(D.TOT_AMT) TOTAL_AMT FROM ADM_INP A,IBS_ORDM M,IBS_ORDD D " 
                 + " WHERE A.CASE_NO=D.CASE_NO AND M.CASE_NO = D.CASE_NO  "
                 + " AND M.CASE_NO_SEQ =D.CASE_NO_SEQ "+getWhereStr(0)+" GROUP BY A.CASE_NO) F"
                 + "       WHERE A.MR_NO = B.MR_NO "
                 + "         AND A.BED_NO = C.BED_NO AND A.CASE_NO=F.CASE_NO(+)"
                 + "         AND A.CANCEL_FLG = 'N' "
                 + region.toString()
                 + getWhereStr(0);
        	 //================modify end=========================================
            break;
        case 1:
        	String dayOpeCode = "";							//2017.1.18 yanmm �ж��ռ��������� �Ƿ񱻹�ѡ
        	if(this.getCheckBox("DAY_OPE_FLG").isSelected()) {
       		 dayOpeCode =  "AND A.DAY_OPE_FLG = 'Y'";	 			//2017.3.22  yanmm  ��ȡֵΪYʱΪ�ռ�����
            }
	
            //================modify by wanglong 20121229========================
            sql = "  SELECT STOP_BILL_FLG,UNLOCKED_FLG,PAT_NAME,CASE_NO,MR_NO,IPD_NO,CUR_AMT,DEPT_CODE,STATION_CODE,SEX_CODE,"
                    + "     BIRTH_DATE,BED_NO_DESC,IN_DATE,DAY_OPE_FLG,CTZ1_CODE,CTZ2_CODE,sum(nvl(PRE_AMT, 0)) TOTAL_BILPAY "
                    + "FROM ("
                    + "      SELECT A.STOP_BILL_FLG,A.UNLOCKED_FLG,B.PAT_NAME,A.CASE_NO,A.MR_NO,A.IPD_NO,A.CUR_AMT,A.DEPT_CODE,"
                    + "             A.STATION_CODE,B.SEX_CODE,B.BIRTH_DATE,C.BED_NO_DESC,A.IN_DATE,A.CTZ1_CODE,A.CTZ2_CODE,"
                    + "             D.RECEIPT_NO,D.REFUND_FLG,D.TRANSACT_TYPE,D.RESET_RECP_NO,D.PRE_AMT ,A.CANCEL_FLG,A.DAY_OPE_FLG"
                    + "        FROM ADM_INP A, SYS_PATINFO B, SYS_BED C, BIL_PAY D "
                    + "       WHERE A.MR_NO = B.MR_NO "
                    + "         AND A.BED_NO = C.BED_NO "
                    + "         AND A.DS_DATE IS NULL "
                    + region.toString()
                    + getWhereStr(1)
                    + "         AND A.CASE_NO = D.CASE_NO(+) "
                    +"           AND A.CANCEL_FLG = 'N' "      //ȡ��סԺ�Ĳ�������  chenxi
                    + dayOpeCode				//2017.1.22 yanmm
                    + "     ) "
                    + "WHERE (RECEIPT_NO IS NOT NULL "
                    + "       AND REFUND_FLG = 'N' "
                    + "       AND TRANSACT_TYPE IN ('01', '04') "
                    + "       AND RESET_RECP_NO IS NULL  "
                    + ")   OR RECEIPT_NO IS NULL "
                    + "GROUP BY STOP_BILL_FLG,UNLOCKED_FLG,PAT_NAME,CASE_NO,MR_NO,IPD_NO,CUR_AMT,DEPT_CODE,STATION_CODE,"
                    + "         SEX_CODE,BIRTH_DATE,BED_NO_DESC,IN_DATE,CTZ1_CODE,CTZ2_CODE,DAY_OPE_FLG";
            //	System.out.println("rrrr sql--->"+sql);
            //================modify end=========================================
            break;
        case 2:
            sql =
                    "SELECT A.DEPT_CODE,A.STATION_CODE,A.BED_NO,B.PAT_NAME,A.MR_NO,A.IPD_NO," +
                    "B.SEX_CODE,A.CTZ1_CODE,A.IN_DATE,A.PATIENT_STATUS FROM ADM_INP A,SYS_PATINFO B,SYS_BED C" +
                    " WHERE A.MR_NO=B.MR_NO AND A.BED_NO=C.BED_NO AND DS_DATE IS NULL AND PATIENT_STATUS IN ('S0','S1') " +
                    region.toString() + getWhereStr(2);
            break;
        case 3:
            sql = "SELECT A.DEPT_CODE,A.STATION_CODE,C.BED_NO_DESC,B.PAT_NAME,A.MR_NO,A.IPD_NO,D.ICD_CHN_DESC,A.NURSING_CLASS,B.BIRTH_DATE,B.SEX_CODE,A.IN_DATE,A.VS_DR_CODE FROM ADM_INP A,SYS_PATINFO B,SYS_BED C,SYS_DIAGNOSIS D WHERE A.MR_NO=B.MR_NO AND A.BED_NO=C.BED_NO AND A.MAINDIAG=D.ICD_CODE(+) AND A.DS_DATE IS NULL " +
                  region.toString() + getWhereStr(3);
            break;
        case 4:
        	 String dsDate = StringTool.getString((Timestamp)this.getValue(
             "DS_START_DATE"), "yyyyMMddHHmmss");
        	 String eDate = StringTool.getString((Timestamp)this.getValue(
             "DS_END_DATE"), "yyyyMMddHHmmss");
        	 sql = "SELECT A.DEPT_CODE,A.STATION_CODE,C.BED_NO_DESC,B.PAT_NAME,A.MR_NO,A.IPD_NO," +
         			"D.ICD_CHN_DESC,A.NURSING_CLASS,B.BIRTH_DATE,B.SEX_CODE,A.IN_DATE," +
         			"A.VS_DR_CODE FROM ADM_INP A,SYS_PATINFO B,SYS_BED C,SYS_DIAGNOSIS D,ODI_ORDER E " +
         			"WHERE A.MR_NO=B.MR_NO AND A.BED_NO=C.BED_NO(+) AND A.MAINDIAG=D.ICD_CODE(+) " +
         			"AND  A.CASE_NO=E.CASE_NO "+region.toString() + getWhereStr(4)+" AND E.ORDER_CODE='100005' AND E.DR_NOTE LIKE '%���ճ�Ժ%' AND E.ORDER_DATE BETWEEN TO_DATE('"+dsDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" ;
         	break;
        }
        //2016.11.01 zhanglei �޸�SQL��ѯ����
        //System.out.println("sql==========SSSS==========" + sql); 
        return sql;
    }

    /**
     * �õ�����
     * @param tableIndex int
     * @return String
     */
    public String getWhereStr(int tableIndex) {
        String whereStr = "";
        switch (tableIndex) {
        case 0:
            String billStatus = this.getValueString("BILL_STATUS1");
            String deptCode = this.getValueString("DEPT_CODE1");
            String stationCode = this.getValueString("STATION_CODE1");
            if (billStatus.length() > 0) {
                if ("0".equals(this.getValueString("BILL_STATUS1"))) {
                    whereStr += " AND A.DS_DATE IS NULL";
                } else {
                    whereStr += " AND A.BILL_STATUS='" + billStatus + "'";
                }
            }
            if (deptCode.length() > 0) {
                whereStr += " AND A.DEPT_CODE='" + deptCode + "'";
            }
            if (stationCode.length() > 0) {
                whereStr += " AND A.STATION_CODE='" + stationCode + "'";
            }
            if ("1".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS')";
            }

            if ("2".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS')";
            }

            if ("3".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS')";
            }
            if ("4".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS')";
            }
            break;
        case 1:
            String deptCode2 = this.getValueString("DEPT_CODE2");
            String stationCode2 = this.getValueString("STATION_CODE2");
            if (deptCode2.length() > 0) {
                whereStr += " AND A.DEPT_CODE='" + deptCode2 + "'";
            }
            if (stationCode2.length() > 0) {
                whereStr += " AND A.STATION_CODE='" + stationCode2 + "'";
            }
            break;
        case 2:
            String deptCode3 = this.getValueString("DEPT_CODE3");
            String stationCode3 = this.getValueString("STATION_CODE3");
            if (deptCode3.length() > 0) {
                whereStr += " AND A.DEPT_CODE='" + deptCode3 + "'";
            }
            if (stationCode3.length() > 0) {
                whereStr += " AND A.STATION_CODE='" + stationCode3 + "'";
            }
            break;
        case 3:
            //AND IN_DATE <=TO_DATE('20100927184700','YYYYMMDDHH24MISS')
            String payType = this.getValueString("PAYTYPE");
            if (payType.length() > 0) {
                whereStr += " AND A.CTZ1_CODE='" + payType + "'";
            }
            Timestamp sysDate = SystemTool.getInstance().getDate();
            int days = this.getValueInt("ZYCOUNT");
            if (days != 0) {
                String queryDate = StringTool.getString(StringTool.rollDate(
                        sysDate, -days), "yyyyMMddHHmmss");
                whereStr += "AND IN_DATE <=TO_DATE('" + queryDate +
                        "','YYYYMMDDHH24MISS')";
            }
            break;
        case 4:
        	 String dsDeptCode = this.getValueString("DS_DEPT_CODE");
             String dsStationCode = this.getValueString("DS_STATION_CODE");
             if (dsDeptCode.length() > 0) {
                 whereStr += " AND A.DEPT_CODE='" + dsDeptCode + "'";
             }
             if (dsStationCode.length() > 0) {
                 whereStr += " AND A.STATION_CODE='" + dsStationCode + "'";
             }
            if(this.getValueString("DS_MR_NO").length()>0){
            	  whereStr += " AND A.MR_NO='" + this.getValueString("DS_MR_NO") + "'";
             } 
             break;
        }
        return whereStr;
    }
    /**
     * ���ճ�Ժ�����Żس��¼�
     */
    public void onDsMrno(){
    	Pat pat = Pat.onQueryByMrNo(getValueString("DS_MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			this.setValue("DS_MR_NO", "");
			return;
		}
		  //modify by huangtt 20160928 EMPI���߲�����ʾ  start
		 String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("DS_MR_NO"));  
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
	            this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
	        }
        //modify by huangtt 20160928 EMPI���߲�����ʾ  end
		
		this.setValue("DS_MR_NO", pat.getMrNo());
		onQuery();
    }
    /**
     * ����״̬
     */
    public void onSel() {
        if ("0".equals(this.getValueString("BILL_STATUS1"))) {
            ((TTextFormat) this.getComponent("START_DATE")).setEnabled(false);
            ((TTextFormat) this.getComponent("END_DATE")).setEnabled(false);
        } else {
            ((TTextFormat) this.getComponent("START_DATE")).setEnabled(true);
            ((TTextFormat) this.getComponent("END_DATE")).setEnabled(true);
        }
        if (("1".equals(this.getValueString("BILL_STATUS1"))
                || "2".equals(this.getValueString("BILL_STATUS1")) || "3".equals(this
                .getValueString("BILL_STATUS1"))) && this.getPopedem("SYSDBA")) {
            callFunction("UI|save|setEnabled", true);
        } else {
            callFunction("UI|save|setEnabled", false);
        }
        // TTable table = this.getTTable(TABLE1);
        // int rowCount = table.getDataStore().rowCount();
        // Map m = new HashMap();
        // for (int i = 0; i < rowCount; i++) {
        // m.put(i, new Color(255, 255, 0));
        // }
        // table.setRowColorMap(m);
        // this.getTTable(TABLE1).removeRowAll();
    }

    /**
     * ���
     */
    public void onClear() {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        switch (selTTabbendPane) {
            case 0:
                this.clearValue("BILL_STATUS1;DEPT_CODE1;STATION_CODE1;ALLPERSON");
                // ��ǰʱ��
                Timestamp today = SystemTool.getInstance().getDate();
                // ��ȡѡ�����ڵ�ǰһ�������
                Timestamp yesterday = StringTool.rollDate(today, -1);
                this.setValue("START_DATE", yesterday);
                this.setValue("END_DATE", today);
                // TTable table = this.getTTable(TABLE1);
                // int rowCount = table.getDataStore().rowCount();
                // Map m = new HashMap();
                // for (int i = 0; i < rowCount; i++) {
                // m.put(i, new Color(255, 255, 0));
                // }
                // table.setRowColorMap(m);
                this.getTTable(TABLE1).removeRowAll();
                break;
            case 1:
                this.clearValue("DEPT_CODE2;STATION_CODE2");
                this.getTTable(TABLE2).removeRowAll();
                break;
            case 2:
                this.clearValue("DEPT_CODE3;STATION_CODE3");
                this.getTTable(TABLE3).removeRowAll();
                break;
            case 3:
                this.clearValue("ZYCOUNT;PAYTYPE;ONLYCOUNT");
                this.getTTable(TABLE4).removeRowAll();
                break;
            case 4:
            	this.getTTable(TABLE5).removeRowAll();
            	this.clearValue("DS_DEPT_CODE;DS_STATION_CODE;DS_MR_NO");
            	 // ��ǰʱ��
                Timestamp dstoday = SystemTool.getInstance().getDate();
                // ��ȡѡ�����ڵ�ǰһ�������
               // Timestamp day = StringTool.rollDate(dstoday, -1);
        		//String startTime = StringTool.getString(dstoday, "yyyy/MM/dd");
        		String endTime = StringTool.getString(dstoday, "yyyy/MM/dd");
        		setValue("DS_START_DATE", endTime+" 00:00:00");
        		setValue("DS_END_DATE", endTime+" 23:59:59");
            	break;
        }
    }

    /**
     * ֹͣ����
     */
    public void onStop() {
        int row = this.getTTable(TABLE2).getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��ͣ�ò�����");
            return;
        }
        TParm parm = this.getTTable(TABLE2).getParmValue().getRow(row);
      
        String sql = "SELECT UNLOCKED_FLG,STOP_BILL_FLG FROM ADM_INP WHERE CASE_NO = '" +parm.getValue("CASE_NO") + "'";
     	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
     	parm.setData("STOP_BILL_FLG", result.getValue("STOP_BILL_FLG",0));
     	String flg = parm.getBoolean("STOP_BILL_FLG") ? "N" : "Y";
        if (parm.getBoolean("STOP_BILL_FLG")) {
            	if(result.getValue("UNLOCKED_FLG",0).equals("3")){
            		if (this.messageBox("��ʾ��Ϣ Tips",
         					"�û���Ϊ��ʱ��������!\n�Ƿ��Ϊֹͣ����",
         				this.YES_NO_OPTION) != 0)
         				return;
            		this.getDBTool().update(
        					"UPDATE ADM_INP SET UNLOCKED_FLG='0' WHERE CASE_NO='"
        							+ parm.getValue("CASE_NO") + "'");
                	flg ="Y";
            	}
            	System.out.println();
        	
        	
            	
        }else{
        	
         	if(result.getValue("UNLOCKED_FLG",0).equals("1")){
         		if (this.messageBox("��ʾ��Ϣ Tips",
     					"�û���Ϊ��ʱ��������!�Ƿ��������",
     				this.YES_NO_OPTION) != 0)
     				return;
         	}
        	this.getDBTool().update(
					"UPDATE ADM_INP SET UNLOCKED_FLG='0' WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "'");
        }
        TParm saveParm =
                new TParm(this.getDBTool().update("UPDATE ADM_INP SET STOP_BILL_FLG='" + flg
                                                          + "' WHERE CASE_NO='"
                                                          + parm.getValue("CASE_NO") + "'"));
        if (saveParm.getErrCode() != 0) {
            this.messageBox("����ʧ�ܣ�");
            return;
        } else {
            this.messageBox("�����ɹ���");
        }
        this.onQuery();
    }
    
    
    /**
     * ��ʱ����
     */
    public void onStopTem() {
        int row = this.getTTable(TABLE2).getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ����ʱ����������");
            return;
        }
        TParm parm = this.getTTable(TABLE2).getParmValue().getRow(row);
        String sql = "SELECT UNLOCKED_FLG,STOP_BILL_FLG FROM ADM_INP WHERE CASE_NO = '" +parm.getValue("CASE_NO") + "'";
     	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
     	parm.setData("STOP_BILL_FLG", result.getValue("STOP_BILL_FLG",0));
        String flg = parm.getBoolean("STOP_BILL_FLG") ? "N" : "Y";
        if (parm.getBoolean("STOP_BILL_FLG")) {
        	if(result.getValue("UNLOCKED_FLG",0).equals("3")){
        	this.getDBTool().update(
					"UPDATE ADM_INP SET UNLOCKED_FLG='0' WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "'");
        	flg ="N";
        	}
        	if(result.getValue("UNLOCKED_FLG",0).equals("0")){
        		if (this.messageBox("��ʾ��Ϣ Tips",
     					"�û�����ֹͣ����!\n�Ƿ��Ϊ��ʱ����",
     				this.YES_NO_OPTION) != 0)
     				return;
        		this.getDBTool().update(
    					"UPDATE ADM_INP SET UNLOCKED_FLG='3' WHERE CASE_NO='"
    							+ parm.getValue("CASE_NO") + "'");
            	flg ="Y";
        	}
        }else{
        	 
         	if(result.getValue("UNLOCKED_FLG",0).equals("1")){
         		if (this.messageBox("��ʾ��Ϣ Tips",
     					"�û���Ϊ��ʱ��������!�Ƿ��������",
     				this.YES_NO_OPTION) != 0)
     				return;
         	}
        	this.getDBTool().update(
					"UPDATE ADM_INP SET UNLOCKED_FLG='3' WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "'");
        }
        
        TParm saveParm =
                new TParm(this.getDBTool().update("UPDATE ADM_INP SET STOP_BILL_FLG='" + flg
                                                          + "' WHERE CASE_NO='"
                                                          + parm.getValue("CASE_NO") + "'"));
        
        if (saveParm.getErrCode() != 0) {
            this.messageBox("����ʧ�ܣ�");
            return;
        } else {
            this.messageBox("�����ɹ���");
        }
        
        this.onQuery();
    }

	/**
	 * ���ý��� yanmm 201707
	 */
	public void onUnlock() {
		this.getTTable(TABLE2).acceptText();
		TParm parm = this.getTTable(TABLE2).getParmValue();
		int count = parm.getCount("CASE_NO");
		
		
		for (int i = 0; i < count; i++) {
			String sql = "SELECT UNLOCKED_FLG FROM ADM_INP WHERE CASE_NO ='"+ parm.getValue("CASE_NO", i) +"' ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getBoolean("UNLOCKED_FLG", i)) {
				this.getDBTool().update(
						"UPDATE ADM_INP SET UNLOCKED_FLG='2',STOP_BILL_FLG='N' WHERE CASE_NO='"
								+ parm.getValue("CASE_NO", i) + "'");
			}
//			else if(result.getValue("UNLOCKED_FLG").equals("1")){
//				this.getDBTool().update(
//						"UPDATE ADM_INP SET UNLOCKED_FLG='1' WHERE CASE_NO='"
//								+ parm.getValue("CASE_NO", i) + "'");
//			}
			else{
				if(result.getValue("UNLOCKED_FLG",0).equals("1")){
					
				}else{
				this.getDBTool().update(
						"UPDATE ADM_INP SET UNLOCKED_FLG='0' WHERE CASE_NO='"
								+ parm.getValue("CASE_NO", i) + "'");
				}
			}
		}
		this.onQuery();
	}

    /**
     * ����EXECL
     */
    public void onExecl() {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        String execlName = "";
        switch (selTTabbendPane) {
            case 0:
                execlName = "��Ժ״̬";
                break;
            case 1:
                execlName = "�������";
                break;
            case 2:
                execlName = "Σ�ز����б�";
                break;
            case 3:
                execlName = "��Ժ������Ϣ��ѯ";
                break;
            case 4:
                execlName = "���ճ�Ժ������Ϣ��ѯ";
                break;
        }
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE" + (selTTabbendPane + 1)),
                                                  execlName);
    }

    /**
     * ��Ժ���˵��ٻض���
     */
    public void onSave() {
        int row = getTTable("TABLE1").getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��һ����Ϣ");
            return;
        }
        TParm tableParm = getTTable("TABLE1").getParmValue();
        TParm parm = new TParm();
        parm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
        parm.setData("MR_NO", tableParm.getData("MR_NO", row));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        
        String sql = "SELECT * FROM INS_ADM_CONFIRM"
        			+ " WHERE MR_NO = '"+tableParm.getData("MR_NO", row)+"' AND "
        			+ " CASE_NO = '"+tableParm.getData("CASE_NO", row)+"' "
        			+ " AND IN_STATUS = '2' AND CANCEL_FLG = 'N'";
        
        System.out.println(sql);
        
        TParm judgeParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if(judgeParm.getCount() > 0)
        {
        	this.messageBox("ҽ�����ϴ��������ٻء�");
        	return;
        }
        TParm actionParm = new TParm();
        actionParm.setData("DATA", parm.getData());
        TParm result =
                TIOM_AppServer.executeAction("action.adm.ADMWaitTransAction", "admReturn",
                                             actionParm);
        if (result.getErrCode() < 0) {
            this.messageBox("�ٻ�ʧ�ܣ�");
            return;
        } else {
        	insertRecallRecord(tableParm, row); //add by wukai on 20160921 ����һ����Ժ�ٻ�
            this.messageBox("�ٻسɹ���");
            this.getTTable(TABLE1).removeRowAll();
        }
    }
    
    /**
     * ����һ���ٻؼ�¼
     */
    public void insertRecallRecord(TParm tableParm, int row) {
    	TParm parm = new TParm();
    	parm.setData("MR_NO", tableParm.getData("MR_NO", row));
    	parm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
    	//System.out.println("caseNo::::::::::: " +  tableParm.getData("CASE_NO", row));
    	TParm admParm = ADMInpTool.getInstance().selectall(parm);//ADM_INP��ȡ������Ϣ
    	if(admParm.getErrCode() < 0) {
    		err(admParm.getErrName() + " " + admParm.getErrText());
    		return;
    	} else {
    		//System.out.println("admParm ::::::::::: " + admParm);
    		TParm recallParm = admParm.getRow(0);
    		//System.out.println("recallParm ::::::::::: " + recallParm);
    		recallParm.setData("PAT_NAME", tableParm.getData("PAT_NAME", row));
    		recallParm.setData("REFUND_DATE", StringTool.getTimestamp(new Date()));
    		recallParm.setData("REFUND_CODE", Operator.getID());
    		recallParm.setData("RECALL_TYPE", "02A");  //�ٻ����� ��Ʊ01 ��Ʊ02 ҽ���ٻ�A  ����B
    		STAOutRecallTool.getNewInstance().insertRecall(recallParm);
    		//System.out.println("insertReslut :::::  " + STAOutRecallTool.getNewInstance().insertRecall(recallParm));
    	}
    	
    }
    
    // ====================������begin======================
    /**
     * �����������������
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// ȡ�ñ��е�����
                String columnName[] = tableData.getNames("Data");// �������
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // ������������;
                int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * �����������ݣ���TParmתΪVector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * ����ָ���������������е�index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * �����������ݣ���Vectorת��Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================������end======================
    
    
    private TCheckBox getCheckBox(String tagName) {				//2017.1.18 yanmm  ��ȡ�ռ�������ѡ�ֶ�
		return (TCheckBox) getComponent("DAY_OPE_FLG");
	}
    
    private TCheckBox getCheckBx(String tagNam) {				
  		return (TCheckBox) getComponent("SELECT_ALL");
  	}
    
    /**
     * ���ý���ȫѡ		yanmm 	2017/10/24
     */
    public void onCheckSelectAll() {
    	TTable table = (TTable) this.getComponent("TABLE2");
        table.acceptText();
        if (table.getRowCount() < 0) {
        	getCheckBx("SELECT_ALL").setSelected(false);
            return;
        }
        TParm parm = table.getParmValue();
        if (getCheckBx("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.setData("UNLOCKED_FLG", i, "Y");
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.setData("UNLOCKED_FLG", i, "N");
            }
        }
        table.setParmValue(parm);
    }
    
    /**
     * ���
     */
    public void onAudit(TParm billparm) {
        TParm actionParm = new TParm();
        TParm result = new TParm();
    	for(int i=0;i<billparm.getCount();i++){
            actionParm.setData("BILL_NO", billparm.getValue("BILL_NO",i));
            	actionParm.setData("APPROVE_FLG", "Y");
                result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                                      "onSaveAuditFee", actionParm);  
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    this.onClear();
                    return;
                }
        String sql = "SELECT BILL_STATUS FROM ADM_INP WHERE CASE_NO ='"+billparm.getValue("CASE_NO",i)+"'";
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String bill_status = selParm.getValue("BILL_STATUS",0);
    	if(!bill_status.equals("0")){
    		   TParm acParm = new TParm();
    	       acParm.setData("CASE_NO", billparm.getValue("CASE_NO",i));
    	       result = TIOM_AppServer.executeAction("action.bil.BILAction",
    	                                              "onAuditFeeCheck", acParm);
    	        if (result.getErrCode() < 0) {
    	            err(result.getErrName() + " " + result.getErrText());
    	            //ִ��ʧ��
    	            this.messageBox("E0005");
    	            this.onClear();
    	            return;
    	        }
    	}
    	}
    }
    
  
    /**
     * �����������
     */
    public void onBatchAudit(){
    TTable table=this.getTTable(TABLE1);
    TParm tableParm=table.getParmValue();
    TParm parm=new TParm();
    TParm billmParm=new TParm();
    int j=0;
    if(null==tableParm||tableParm.getCount()<=0){
    	this.messageBox("���Ȼ�ò�����Ϣ");
    	return;
    }
    for(int i=0;i<tableParm.getCount("CASE_NO");i++){
    	parm.setData("CASE_NO",tableParm.getValue("CASE_NO",i));
    	parm.setData("MR_NO",tableParm.getValue("MR_NO",i));
    	 billmParm = IBSBillmTool.getInstance().selAuditFee(parm);
    	 onAudit(billmParm);
    }
    //ִ�гɹ�
    this.messageBox("P0005");
    this.onClear();
    }
}
