package com.javahis.ui.inw;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.inw.InwForOdiTool;
import jdo.inw.InwOrderExecTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SysPhaBarTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.server.FileServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��ʿվִ��������
 * </p>
 * table
 * <p>
 * Description: PS��there is interface of quering all station here
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * 
 * @version 1.0
 */
public class INWOrderExecMainControl extends TControl {
    private Compare compare = new Compare();

    private boolean ascending = false;
    private TableModel model;
    private int sortColumn = -1;

    // �����ϵ�UI����
    TComboBox CLP;

    TTextFormat from_Date;
    TTextFormat to_Date;

    TTextField from_Time;
    TTextField to_Time;
    
    // ��ѯ��ʱ������ add by wanglong 20130626
    TRadioButton firstDateRadio;// Ĭ��ʱ��
    TRadioButton secondDateRadio;// ҽ������ʱ��
    
    // ҽ�����
    TRadioButton ord1All;
    TRadioButton ord1ST;
    TRadioButton ord1UD;
    TRadioButton ord1DS;
    TRadioButton ord1IG;

    // ҽ������
    TRadioButton ord2All;
    TRadioButton ord2PHA;
    TRadioButton ord2PL;
    TRadioButton ord2ENT; // ����

    // ҩ������
    TCheckBox typeO;
    TCheckBox typeE;
    TCheckBox typeI;
    TCheckBox typeF;

    // ���״̬
    TRadioButton checkAll;
    TRadioButton checkYES;
    TRadioButton checkNO;

    // ȫ��ִ��
    TCheckBox exeAll;
    TCheckBox printAll;

    TTable masterTbl;
    TTable detailTbl;

    TMovePane mp1;
    TMovePane mp2;
    TMovePane mp3;

    // �ж��Ƿ����˲���Ƽ۵İ�ť
    boolean isCharge = false;
    // �ж��Ƿ��������µ�����
    boolean isVitalSign = false;
    // ĳ�����˵���Ϣ����ʼ�����ⲿ���洫�룩
    String caseNo = "";
    String stationCode = "";
    String mrNo = "";
    String patName = "";
    String ipdNo = "";
    String deptCode = "";
    String ctz1Code = "";
    String ctz2Code = "";
    String ctz3Code = "";
    boolean saveFlg = true;
    String schdCode = "";//ʱ��
	String orderCodeSki = "";// yanjing 20131107 ������ҽ������
	String orderNoSki = "";// yanjing 20131107 ������ҽ��
	String orderSeqSki = "";// yanjing 20131107 ������ҽ��
	String skiResult = "";// yanjing 20131107 ���Ƥ�Խ���ش��ı�ע��Ϣ
    // ��������סԺ�������Ĳ�������������Ƽ�
    TParm outsideParm = new TParm();
    // ��ӡ���������
    TParm forPrtParm = new TParm();
    // �����HL7�ӿڵ�����
    TParm sendHL7Parm = new TParm();
    // ���� ȡ�����
    private int flg;
    // ICUע��
    boolean ICUflg;
    // ����CISHl7�ӿ�
    TParm CISHl7Parm = new TParm();
    private SocketLink client1;

    String clpCode_ = "";
    String pkOrderCode;// һ���ٴ�PK��Ѫҽ������

    public void onInit() {
        super.onInit();
        myInitControler();
        // ������ܷ���ֵ����
        // ���ⲿ���õõ�����(�Ӳ�����������õ�����TParm(����һ�������Ǵ�����IBS))
        outsideParm = (TParm) this.getParameter();
        if (outsideParm != null)
            initParmFromOutside();
        onQuery();
        lockComponet();
        // �������
        addListener(masterTbl);
    }

    public void lockComponet() {
        // ((TTextFormat) getComponent("DEPT_CODE")).setEnabled(false);
        // ((TTextFormat)getComponent("VC_CODE")).setEnabled(false);
        ((TMenuItem) getComponent("save")).setEnabled(saveFlg);
        ((TButton) getComponent("butCharge")).setEnabled(saveFlg);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
		callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107
        masterTbl.setParmValue(new TParm());
        detailTbl.setParmValue(new TParm());
        // ��ʼ����ǰtable
        initTable();
        exeAll.setEnabled(true);
        if (checkYES.isSelected()) {
            exeAll.setSelected(true);
        } else if (checkNO.isSelected()) {
            exeAll.setSelected(false);
        }
        onPrtAll();

    }

    public boolean checkPatNum() {
        int rowCount = masterTbl.getRowCount();
        if (rowCount <= 0)
            return true;
        String mrNoThis = masterTbl.getParmValue().getValue("MR_NO", 0);
        for (int i = 0; i < rowCount; i++) {
            if (mrNoThis.equals(masterTbl.getParmValue().getValue("MR_NO", i)))
                continue;
            return true;
        }
        return false;
    }

    /**
     * ���������table�¼�
     * 
     * @param row
     *            int �к�
     */
    public void onMasterTableClicked(int row) {
        // ���
        detailTbl.setParmValue(new TParm());
        detailTbl.acceptText();
        TParm tableDate = masterTbl.getParmValue();
        boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(row, 15));//modify by machao 20170119 14��15  
        if (selFlg) {
        	 callFunction("UI|skiResult|setEnabled", true);// yanjing 20131107 Ƥ�Խ����ť
		}else{
			 callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107 Ƥ�Խ����ť
		}
        String caseNo = "", orderNo = "", orderSeq = "", startDttm = "", endDttm = "";
        // ͨ��CASE_NO��ORDER_NO��ORDER_SEQ��ODI_DSPND�ж�λ����ϸ��
        caseNo = tableDate.getValue("CASE_NO", row);
        orderNo = tableDate.getValue("ORDER_NO", row);
        orderSeq = tableDate.getValue("ORDER_SEQ", row);
        startDttm = tableDate.getValue("START_DTTM", row);
        endDttm = tableDate.getValue("END_DTTM", row);
        // ��ѯϸ���SQL
        String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
                + "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL FROM ODI_DSPND "
                + "WHERE CASE_NO='"
                + caseNo
                + "' AND ORDER_NO='"
                + orderNo
                + "' AND ORDER_SEQ='"
                + orderSeq
                + "' "
                + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
                + startDttm
                + "','YYYYMMDDHH24MISS') "
                + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
                + endDttm
                + "','YYYYMMDDHH24MISS')"
                + " ORDER BY ORDER_DATE||ORDER_DATETIME";
        // ����ϸ���TDS,����������
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            result.addData("EXE_FLG", checkYES.isSelected());
            String date = (String) result.getData("ORDER_DATE", i);
            result.setData("ORDER_DATE", i, date.substring(0, 4) + "/"
                    + date.substring(4, 6) + "/" + date.substring(6));
            String time = (String) result.getData("ORDER_DATETIME", i);
            result.setData("ORDER_DATETIME", i, time.substring(0, 2) + ":"
                    + time.substring(2, 4));
         // ��ѯҩ���ı�ע��Ϣ
			String noteSql = "SELECT BATCH_NO,CASE SKINTEST_NOTE WHEN '0' THEN '����' WHEN '1' THEN '����' END AS SKINTEST_NOTE"
					+ " FROM PHA_ANTI WHERE CASE_NO = '"
					+ caseNo
					+ "'"
					+ "AND ORDER_CODE = '"
					+ orderCodeSki
					+ "' ORDER BY OPT_DATE DESC";

			TParm noteResult = new TParm(TJDODBTool.getInstance().select(
					noteSql));
			if (noteResult.getCount() <= 0
					|| noteResult.getValue("BATCH_NO", 0).equals(null)
					|| "".equals(noteResult.getValue("BATCH_NO", 0))) {
				skiResult = "";
			} else {
				skiResult = "Ƥ�Խ����" + noteResult.getValue("SKINTEST_NOTE", 0)
						+ ";  ���ţ�" + noteResult.getValue("BATCH_NO", 0);
			}
        }
        detailTbl.setParmValue(result);
        setValue("INW_STATION_CODE", tableDate.getValue("STATION_CODE", row));
        setValue("IPD_NO", tableDate.getValue("IPD_NO", row));
        setValue("MR_NO", tableDate.getValue("MR_NO", row));
        setValue("BED_NO", tableDate.getValue("BED_NO", row));
        setValue("INW_VC_CODE", tableDate.getValue("JZDR", row));
        setValue("PAT_NAME", tableDate.getValue("PAT_NAME", row));
        setValue("INW_DEPT_CODE", tableDate.getValue("DEPT_CODE", row));
        setValue("ADM_DATE", tableDate.getValue("IN_DATE", row) == null ? ""
                : tableDate.getValue("IN_DATE", row).substring(0, 10)
                        .replaceAll("-", "/"));
        setValue("PAY_INS", tableDate.getValue("PAY_INS", row));
        setValue("TOTAL_AMT", tableDate.getValue("TOTAL_AMT", row));
        setValue("YJYE_PRICE", tableDate.getValue("CUR_AMT", row));
        setValue("GREED_PRICE", tableDate.getValue("GREENPATH_VALUE", row));
        setValue("YJJ_PRICE", tableDate.getValue("TOTAL_BILPAY", row));
    }

    public void clearTop() {
        if (getValueString("IPD_NO").length() != 0)
            return;
        setValue("PAT_NAME", "");
        setValue("SEX", "");
        setValue("SERVICE_LEVELIN", "");
        setValue("WEIGHT", "");
        setValue("BED_NO", "");
    }

    private void filterDrugByDoseCode(TParm parm) {
        if (!ord2PHA.isSelected())
            return;
        if (parm.getCount() <= 0)
            return;
        for (int i = parm.getCount() - 1; i >= 0; i--) {
            if (!parm.getValue("ORDER_CAT1_CODE", i).startsWith("PHA"))
                continue;
            if (!typeO.isSelected()
                    && "O".equals(parm.getValue("CLASSIFY_TYPE", i)))
                parm.removeRow(i);
            if (!typeE.isSelected()
                    && "E".equals(parm.getValue("CLASSIFY_TYPE", i)))
                parm.removeRow(i);
            if (!typeI.isSelected()
                    && "I".equals(parm.getValue("CLASSIFY_TYPE", i)))
                parm.removeRow(i);
            if (!typeF.isSelected()
                    && "F".equals(parm.getValue("CLASSIFY_TYPE", i)))
                parm.removeRow(i);
        }
        parm.setCount(parm.getCount("ORDER_CAT1_CODE"));
    }

    /**
     * ��ʼ��table ��ѯ�����ǣ� caseNo/����
     */
    public void initTable() {
        String fromDate = StringTool.getString(
                (Timestamp) from_Date.getValue(), "yyyyMMdd");
        String fromTime = (String) from_Time.getValue();
        String fromCheckDate = fromDate + fromTime.substring(0, 2)
                + fromTime.substring(3);
        String toDate = StringTool.getString((Timestamp) to_Date.getValue(),
                "yyyyMMdd");
        String toTime = (String) to_Time.getValue();
        String toCheckDate = toDate + toTime.substring(0, 2)
                + toTime.substring(3);
        if (fromCheckDate.compareTo(toCheckDate) > 0) {
            messageBox("ִ�����ڲ��Ϸ�");
            return;
        }
        TParm selParm = new TParm();
        selParm = getQueryParm();
        TParm query = new TParm();
        if (firstDateRadio.isSelected() || checkNO.isSelected()) {//add by wanglong 20130626
            query = InwForOdiTool.getInstance().selectOdiDspnm(selParm);
        } else {
            query = InwForOdiTool.getInstance().selectOdiDspnD(selParm);
        }
        System.out.println("OrderExecQuery: " + query);
        filterDrugByDoseCode(query);
        if (query.getCount() <= 0) {
            masterTbl.setParmValue(query);
            // this.messageBox("û��������ݣ�");
            return;
        }
        for (int i = 0; i < query.getCount(); i++) {
            query.addData("EXE_FLG", checkYES.isSelected());
            query.addData("PRT", false);
            // ��ʿִ��ҽ��ʱ��
            Timestamp exeDate = (Timestamp) query.getData("NS_EXEC_DATE", i);
            // ��ʿվִ��DCʱ��
            Timestamp exeDcDate = (Timestamp) query.getData("NS_EXEC_DC_DATE",
                    i);
            // �����ѯ�����
            if (exeDate != null) {
                String day = StringTool.getString(exeDate, "yyyy/MM/dd");
                String time = StringTool.getString(exeDate, "HH:mm:ss")
                        .substring(0, 5);
                query.addData("NS_EXEC_DATE", exeDate);
                query.addData("NS_EXEC_DATE_TIME", time);
            } else {
                query.addData("NS_EXEC_DATE", null);
                query.addData("NS_EXEC_DATE_TIME", null);
            }
            if (exeDcDate != null) {
                String day = StringTool.getString(exeDcDate, "yyyy/MM/dd");
                String time = StringTool.getString(exeDcDate, "HH:mm:ss");
                query.addData("NS_EXEC_DC_DATE_DAY", day);
                query.addData("NS_EXEC_DC_DATE_TIME", time);
            } else {
                query.addData("NS_EXEC_DC_DATE_DAY", null);
                query.addData("NS_EXEC_DC_DATE_TIME", null);
            }
        }
        masterTbl.setParmValue(query);
        existsDateForTabl(masterTbl);
        if (saveFlg) {
            if (checkPatNum()) {
                String SQL = "";
                String deptSQL = "";
                if (getValueString("INW_DEPT_CODE").length() != 0)
                    deptSQL = " AND DEPT_CODE = '"
                            + getValueString("INW_DEPT_CODE") + "'";
                String stationSQL = "";
                if (getValueString("INW_STATION_CODE").length() != 0)
                    stationSQL = " AND STATION_CODE = '"
                            + getValueString("INW_STATION_CODE") + "'";
                // String drSQL = "";
                // if (getValueString("VC_CODE").length() != 0)
                // drSQL = " AND VS_DR_CODE = '" + getValueString("VC_CODE")
                // +"'";
                if (getValueString("MR_NO").length() != 0
                        && getValueString("IPD_NO").length() != 0)
                    SQL = " SELECT * " + " FROM ADM_INP " + " WHERE MR_NO = '"
                            + getValueString("MR_NO") + "'"
                            + " AND   IPD_NO = '" + getValueString("IPD_NO")
                            + "'" + " AND CANCLE_FLG  != 'Y'"
                            + " AND DS_DATE IS NULL" + deptSQL + stationSQL;// +
                // drSQL;
                else if (getValueString("MR_NO").length() != 0)
                    SQL = " SELECT * " + " FROM ADM_INP " + " WHERE MR_NO = '"
                            + getValueString("MR_NO") + "'"
                            + " AND CANCLE_FLG  != 'Y'"
                            + " AND DS_DATE IS NULL" + deptSQL + stationSQL;// +
                // drSQL;
                else if (getValueString("IPD_NO").length() != 0)
                    SQL = " SELECT * " + " FROM ADM_INP " + " WHERE IPD_NO = '"
                            + getValueString("IPD_NO") + "'"
                            + " AND CANCLE_FLG  != 'Y'"
                            + " AND DS_DATE IS NULL" + deptSQL + stationSQL;// +
                // drSQL;
                if (SQL.length() != 0) {
                    TParm result = new TParm(TJDODBTool.getInstance().select(
                            SQL));
                    if (result.getCount() > 0) {
                        this.setCaseNo(result.getValue("CASE_NO", 0));
                        this.setStationCode(result.getValue("STATION_CODE", 0));
                        this.setMrNo(result.getValue("MR_NO", 0));
                        TParm patTParm = new TParm(TJDODBTool.getInstance()
                                .select(
                                        "SELECT * FROM SYS_PATINFO WHERE MR_NO = '"
                                                + result.getValue("MR_NO", 0)
                                                + "'"));
                        this.setPatName(patTParm.getValue("PAT_NAME", 0));
                        this.setIpdNo(result.getValue("IPD_NO", 0));
                        this.setDeptCode(result.getValue("DEPT_CODE", 0));
                        ctz1Code = result.getValue("CTZ1_CODE", 0);
                        ctz2Code = result.getValue("CTZ2_CODE", 0);
                        ctz3Code = result.getValue("CTZ3_CODE", 0);
                        ((TMenuItem) getComponent("print")).setEnabled(true);
                        ((TMenuItem) getComponent("paster")).setEnabled(true);
                    } else {
                        this.setCaseNo("");
                        // ((TMenuItem)
                        // getComponent("print")).setEnabled(false);
                        // ((TMenuItem)
                        // getComponent("paster")).setEnabled(false);
                    }
                } else {
                    this.setCaseNo("");
                    // ((TMenuItem) getComponent("print")).setEnabled(false);
                    // ((TMenuItem) getComponent("paster")).setEnabled(false);
                }
            } else {
                TParm parmMastrt = masterTbl.getParmValue();
                this.setCaseNo(parmMastrt.getValue("CASE_NO", 0));
                this.setStationCode(parmMastrt.getValue("STATION_CODE", 0));
                this.setMrNo(parmMastrt.getValue("MR_NO", 0));
                this.setPatName(parmMastrt.getValue("PAT_NAME", 0));
                this.setIpdNo(parmMastrt.getValue("IPD_NO", 0));
                this.setDeptCode(parmMastrt.getValue("DEPT_CODE", 0));
                TParm admParm = new TParm();
                admParm.setData("CASE_NO", parmMastrt.getValue("CASE_NO", 0));
                admParm = ADMTool.getInstance().getADM_INFO(admParm);
                ctz1Code = admParm.getValue("CTZ1_CODE", 0);
                ctz2Code = admParm.getValue("CTZ2_CODE", 0);
                ctz3Code = admParm.getValue("CTZ3_CODE", 0);
                ((TMenuItem) getComponent("print")).setEnabled(true);
                ((TMenuItem) getComponent("pasterBottle")).setEnabled(true);
                ((TMenuItem) getComponent("Newprint")).setEnabled(true);
            }
        }
        setColor(); // modify by chenxi 20120703
        return;
    }

//    /**
//     * ���˵�����İ�������
//     * @param query
//     */
//    private void filterBedNo(TParm query) {
//    	for (int i = 0; i < query.getCount("MR_NO"); i++) {
//			String bedNo = InwForOdiTool.getInstance().selectBedNoByMrNo(query.getValue("MR_NO", i));
//			query.setData("BED_NO", i, bedNo);
//    	}
//	}

	/**
     * ��ý����ϵ����в�ѯ����
     * 
     * @return TParm
     */
    public TParm getQueryParm() {
        // ��ý����ϵĲ���
        TParm result = new TParm();
        // ===============pangben modify 20110512 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            result.setData("REGION_CODE", Operator.getRegion());
        // ===============pangben modify 20110512 stop

        // modified by WangQing 20170331 -start
        // add ��������
        // ��������
        if(this.getValueString("BED_NO_S") != null && 
        		this.getValueString("BED_NO_E") != null && 
        		!this.getValueString("BED_NO_S").equals("") && 
        		!this.getValueString("BED_NO_E").equals("") && 
        		StringTool.getInt(this.getValueString("BED_NO_S")) <= StringTool.getInt(this.getValueString("BED_NO_E"))){
        	result.setData("BED_NO_S", this.getValueString("BED_NO_S"));
        	result.setData("BED_NO_E", this.getValueString("BED_NO_E"));
        	result.setData("BED_NO_DESC", "");
        }
        // modified by WangQing 20170331 -end

        
        // ȡ��ҽ�����
        if (ord1All.isSelected()) {
            // ����
        	result.setData("INW_EXEC_DSPN_KIND", "ST,UD,F,DS,IG");//20151217 wangjc add
        } else if (ord1ST.isSelected()) {
            // ��ʱ(����ST/F)
            result.setData("DSPN_KINDSTF", "Y");
        } else if (ord1UD.isSelected()) {
            // ����
            result.setData("DSPN_KINDUD", "Y");
        } else if (ord1DS.isSelected()) {
            // ��Ժ��ҩ
            result.setData("DSPN_KINDDS", "Y");
        } else if (ord1IG.isSelected()) {
            // סԺ��ҩ
            result.setData("DSPN_KINDIG", "Y");
        }

        // ҽ������
        if (ord2All.isSelected()) {
            // ����
        } else if (ord2PHA.isSelected()) {
            // ҩ��
            result.setData("CAT1_TYPEPHA", "Y");
        } else if (ord2PL.isSelected()) {
            // ����(������)
            result.setData("CAT1_TYPEPL", "Y");
        } else if (ord2ENT.isSelected()) {
            // ����
            result.setData("CAT1_TYPEENT", "Y");
        }

        // ҽ������
        /*
         * if (typeO.isSelected()) { //�ڷ� result.addData("DOSE_TYPEO", "Y"); }
         * if (typeE.isSelected()) { //���� result.addData("DOSE_TYPEE", "Y"); }
         * if (typeI.isSelected()) { //��� result.addData("DOSE_TYPEI", "Y"); }
         * if (typeF.isSelected()) { //��� result.addData("DOSE_TYPEF", "Y"); }
         */

        // ִ��״̬
        if (checkAll.isSelected()) {
            // ����
            if (firstDateRadio.isSelected()) {
                result.setData("EXECTYPE_ALL", "Y");
                String fromDate =
                        StringTool.getString((Timestamp) from_Date.getValue(), "yyyyMMdd");
                String fromTime = (String) from_Time.getValue();
                String fromCheckDate = fromDate + fromTime.substring(0, 2) + fromTime.substring(3);
                String toDate = StringTool.getString((Timestamp) to_Date.getValue(), "yyyyMMdd");
                String toTime = (String) to_Time.getValue();
                String toCheckDate = toDate + toTime.substring(0, 2) + toTime.substring(3);
                result.setData("fromCheckDate", fromCheckDate);
                result.setData("toCheckDate", toCheckDate);
            } else {// add by wanglong 20130626
                result.setData("EXECTYPE_ALL_ORDERDATETIME", "Y");
                result.setData("fromDateTime", (Timestamp) this.getValue("ORDER_START_DATETIME"));
                result.setData("toDateTime", (Timestamp) this.getValue("ORDER_END_DATETIME"));
            }
        } else if (checkYES.isSelected()) {
            if (firstDateRadio.isSelected()) {
                // ��ִ��
                result.setData("EXECTYPE_YES", "Y");
                String fromDate =
                        StringTool.getString((Timestamp) from_Date.getValue(), "yyyyMMdd");
                String fromTime = (String) from_Time.getValue();
                String fromCheckDate = fromDate + fromTime.substring(0, 2) + fromTime.substring(3);
                String toDate = StringTool.getString((Timestamp) to_Date.getValue(), "yyyyMMdd");
                String toTime = (String) to_Time.getValue();
                String toCheckDate = toDate + toTime.substring(0, 2) + toTime.substring(3);
                result.setData("fromCheckDate", fromCheckDate);
                result.setData("toCheckDate", toCheckDate);
                result.setData("NS_EXEC_DATE", fromCheckDate);
            } else {// add by wanglong 20130626
                result.setData("EXECTYPE_YES", "Y");
                result.setData("fromDateTime", (Timestamp) this.getValue("ORDER_START_DATETIME"));
                result.setData("toDateTime", (Timestamp) this.getValue("ORDER_END_DATETIME"));
                result.setData("ORDER_DATETIME", "Y");
            }
        } else if (checkNO.isSelected()) {
            // δ���
            result.setData("EXECTYPE_NO", "Y");
        }
        if (setQueryParm(result)) {
            clearTop();
            return result;
        }
        // ���翴���
        if (caseNo != null && !"".equals(caseNo.trim())
                && !"null".equals(caseNo)) {
            result.setData("CASE_NO", caseNo);
        } else {
            // Ϊ�յ�ʱ��
        }
        // ���벡����
        if (!this.getValueString("INW_STATION_CODE").equals("")) {
            result.setData("STATION_CODE", getValueString("INW_STATION_CODE"));
        } else {
            // Ϊ�յ�ʱ��
        }
        // �������
        if (!this.getValueString("INW_DEPT_CODE").equals("")) {
            result.setData("DEPT_CODE", getValueString("INW_DEPT_CODE"));
        } else {
            // Ϊ�յ�ʱ��
        }
        // ���뾭��ҽʦ
        if (!this.getValueString("INW_VC_CODE").equals("")) {
            result.setData("VS_DR_CODE", getValueString("INW_VC_CODE"));
        } else {
            // Ϊ�յ�ʱ��
        }
        // System.out.println("-----------------"+result);
        return result;
    }

    public boolean setQueryParm(TParm parm) {
        if (!saveFlg)
            return false;
        if (getValueString("INW_STATION_CODE").length() != 0)
            parm.setData("STATION_CODE", getValue("INW_STATION_CODE"));
        if (getValueString("IPD_NO").length() != 0)
            parm.setData("IPD_NO", getValue("IPD_NO"));
        if (getValueString("MR_NO").length() != 0)
            parm.setData("MR_NO", getValue("MR_NO"));
        // if(getValueString("VC_CODE").length() != 0)
        // parm.setData("VC_CODE",getValue("VC_CODE"));
        if (getValueString("INW_DEPT_CODE").length() != 0)
            parm.setData("DEPT_CODE", getValue("INW_DEPT_CODE"));
        parm.setData("DS_DATE_FLG", false);
        return true;
    }

    // ȫ��ִ��
    public void onCheck() {

        boolean nowFlag = exeAll.isSelected();
        // ��ȫ��ִ�е�ʱ������һ��ʱ��
        Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
        String optName = Operator.getName();

        // �õ�����
        int ordCount = masterTbl.getRowCount();
        for (int i = 0; i < ordCount; i++) {
            // ѭ��ȡ���Թ�
            if (nowFlag) {
                selection(i, optName, chackTime);
            } else { // ȡ�����
                unselection(i);
            }
        }

    }
	public Boolean checkDate(String date){
		Boolean flg = false;
		
		if(date.length()!=5){
			return flg;
		}
		if(!date.contains(":")){
			return flg;
		}
		String hour = date.substring(0, 2);
		String minutes = date.substring(3, 5);
		try{
			int h = Integer.parseInt(hour);
			int m = Integer.parseInt(minutes);
			if(h<0 || h>=24){
				return flg;
			}
			if(m<0 || m>=60){
				return flg;
			}
		}catch (Exception e) {
			return flg;
		}
		System.out.println(Integer.parseInt(hour));
		System.out.println(Integer.parseInt(minutes));
		return true;
	}
    /**
     * ���涯��
     */
    public boolean onSave() {
        // ������֮�����ѡ��
        exeAll.setSelected(false);
        printAll.setSelected(false);

        // ���̽���ֵ�ĸı�
        masterTbl.acceptText();
        detailTbl.acceptText();

        boolean existOption = false;
        if (checkAll.isSelected()) {
            this.messageBox("ȫ��״̬��\n���ɱ��棡");
            return false;
        }
        // ����Ƿ���ѡ�е�����
        for (int i = 0; i < masterTbl.getRowCount(); i++) {
            //boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 14));   //modify by wukai 20160601 12��14
        	boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 15));   //modify by machao 20170119 14��15
            if ((checkNO.isSelected() && selFlg)
                    || (checkYES.isSelected() && !selFlg)) {
                existOption = true;
                break;
            }
        }
        // ���û�д���ѡ�������
        if (!existOption) {
            this.messageBox("ûѡ�б������ݣ�");
            return false;
        }
        //start by machao 20170306  ��ʿִ�к�ҽ����ʧ   ��ʿִ���ж��Ƿ����
        String sqlOdiOrder = "";
        TParm res = masterTbl.getParmValue();
        for(int i = 0; i < masterTbl.getRowCount(); i++) {
        	boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 15));//modify by machao 20170119 14��15
            sqlOdiOrder = "SELECT * FROM ODI_ORDER WHERE CASE_NO='"+res.getRow(i).getData("CASE_NO")+"' "+
					 "AND MR_NO='"+res.getRow(i).getData("MR_NO")+"' ";
        	if((checkNO.isSelected() && selFlg)
                    || (checkYES.isSelected() && !selFlg)){
        		String sql = " AND ORDER_CODE='"+res.getRow(i).getData("ORDER_CODE")+"' ";
        		sql +=" AND ORDER_NO='"+res.getRow(i).getData("ORDER_NO")+"' ";
        		//System.out.println("ssss:"+sqlOdiOrder+sql);
        		TParm resultNsCheckDate = new TParm(TJDODBTool.getInstance().select(sqlOdiOrder+sql));
        		
        		//this.messageBox(resultNsCheckDate.getData("NS_CHECK_DATE",0)+"");
        		
                if(resultNsCheckDate.getData("NS_CHECK_DATE",0) == null || resultNsCheckDate.getData("NS_CHECK_DATE",0) == ""){
                	this.messageBox("ҽ��δ��˻��ѱ���������²�ѯ");
                	onQuery();
                	return false; 
                }
        	}       	
        }
        //end machao 20170306
        
        //ִ��ʱ������ҽ������ʱ�� ��ʾ��ǿ��   start  machao
        TParm masterParm = masterTbl.getParmValue();
        String orderDate = "";//����ʱ�� Ҳ��������ʱ��
        String execDate = "";//ִ��ʱ��
        String time = "";
        Timestamp timeOrderDate = null;//����ʱ��
        Timestamp timeExecDate = null;//ִ��ʱ��
        String orderDesc = "";
        for(int i = 0; i < masterTbl.getRowCount(); i++){
        	boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 15));
        	if(selFlg){
        		orderDate = masterParm.getRow(i).getValue("ORDER_DATE");
            	execDate = masterTbl.getValueAt(i, 20)+"";
            	time = masterTbl.getValueAt(i, 21)+"";
            	// ���δ¼��ð�ŵĴ���
            	if (time.length() == 4 && !time.contains(":")) {
            		time = time.substring(0, 2) + ":" + time.substring(2, 4);
            	}
            		
            	orderDesc = masterParm.getRow(i).getValue("ORDER_DESC");
            	
            	timeOrderDate = masterParm.getRow(i).getTimestamp("ORDER_DATE");//����ʱ��
            	
            	if(execDate.length()>12){
            		execDate = execDate.replaceAll("/", "-").substring(0, 10);
            		execDate = execDate+" "+time+":00";
            	}else{
            		execDate = execDate.replaceAll("/", "-")+" "+time+":00";
            	}
            	timeExecDate = Timestamp.valueOf(execDate);//ִ��ʱ��
        		long tt = timeOrderDate.getTime() - timeExecDate.getTime();
        		if(tt>0){
        			if(!(0 == this.messageBox("��ʾ��Ϣ",
        					"ҽ����"+orderDesc+"\r\nִ��ʱ������ҽ������ʱ�䣬�Ƿ񱣴棿",
    						this.YES_NO_OPTION))){
        				return false;
        			}
        		}
        	}
        }
        //end machao
        
		// ����Ƥ��ҩƷ�Ƿ���Ƥ�� yanjing 20140514
		boolean psResult = false;// �Ƿ����δ��Ƥ�Ե�ҩƷ
		for (int i = 0; i < masterTbl.getRowCount(); i++) {
			//boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 14));  //modify by wukai 20160601 12��14
			boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 15));  //modify by machao 20170119 14��15
			// У���Ƿ���Ƥ����Ʒ
			TParm tablValue = masterTbl.getParmValue();
			String orderCode = (String) tablValue.getData("ORDER_CODE", i);
			String selectSql = "SELECT BATCH_NO,SKINTEST_NOTE FROM PHA_ANTI A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND B.SKINTEST_FLG ='Y' AND A.CASE_NO= '"
					+ caseNo
					+ "' AND A.ORDER_CODE='"
					+ orderCode
					+ "' "
					+ " ORDER BY A.BATCH_NO";
			TParm selectparm = new TParm(TJDODBTool.getInstance().select(
					selectSql));
			if (selectparm.getCount() > 0) {
				String skintest="";
				for (int j = 0; j < selectparm.getCount(); j++) {
					if (null!=selectparm.getData("SKINTEST_NOTE", 0) && selectparm.getData("SKINTEST_NOTE", 0).toString().length()>0) {
						skintest=selectparm.getValue("SKINTEST_NOTE", j);
						break;
					}
				}
				if (checkNO.isSelected()
						&& selFlg
//						&& selectparm.getData("BATCH_NO", 0).toString().equals(
//								"")
						&& skintest
								.equals("")) {// û����Ƥ�ԣ�������δƤ�Խ��ʱҲҪ������ʾ��������ҩ�������������ţ���ʿվ��δ����Ƥ�ԣ�
					psResult = true;
				}
			}
		}

		// �����Ƥ�Ե�ҩƷδ��дƤ�Խ��
		if (psResult) {
			if (this.messageBox("��ʾ", "����δ��Ƥ�Ե�ҩ��,�Ƿ��������?", this.YES_NO_OPTION) != 0) {// �񣬲�����
				return false;
			}
		}
        // �����ж�
        if (!checkPW()) {
            return false;
        }

        // ���ñ���
        if (checkNO.isSelected()) {
            if (!onExec()) {
                this.messageBox("E0001");
                onQuery();
                return false;
            }
        } // �����˱�ѡ��˵������ʱ��--ȡ����ˣ�����Ҫ��֤�Ƿ���ִ�е�
        else {
            if (!onUndoExec()) {
                this.messageBox("E0001");
                onQuery();
                return false;
            }
        }
        this.messageBox("P0001");
        // �������ִ��һ�߲�ѯ
        onQuery();
        // ����ɹ���У���Ƿ��ֹͣ����
        checkStopFee();
        // ����HL7��Ϣ
        if (sendHL7Parm.getCount("CASE_NO") > 0) {
            sendHL7Mes();
        }
        // ����ICU��Ϣ
        // if (this.ICUflg && this.CISHl7Parm.getCount("CASE_NO") > 0) {
        // sendICUHl7Mes();
        // }
        return true;
    }

    private boolean CheckData() {
        boolean falg = false;

        return falg;
    }

    /**
     * ��������ҩƷ��ɫ
     */
    // ================= add by chenxi 20120703
    private void setColor() {
        TParm tableParm = masterTbl.getParmValue();
        Color normalColor = new Color(0, 0, 0);
        Color blueColor = new Color(0, 0, 255);
        for (int i = 0; i < tableParm.getCount(); i++) {
            // =========ҩƷ��ʾ��Ϣ modify by chenxi
            String orderCode = tableParm.getValue("ORDER_CODE", i);
            String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
                    + orderCode + "'";
            TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
            sqlparm = sqlparm.getRow(0);
            if (sqlparm.getValue("DRUG_NOTES_DR").length() == 0) {
                masterTbl.setRowTextColor(i, normalColor);
                continue;
            }
            masterTbl.setRowTextColor(i, blueColor);

        }
    }

    /**
     * ����ĳ��ҽ����״̬������ʾ��Ϣ
     */
    public void onClick() {
        int row = masterTbl.getSelectedRow();
        TParm parm = masterTbl.getParmValue();
        // TParm action1 = mainTable.getDataStore().getRowParm(
        // mainTable.getSelectedRow());
        // =================== modify by chenxi 20120703 start
        String orderCode = parm.getValue("ORDER_CODE", row);
        String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
                + "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
                + " WHERE ORDER_CODE = '" + orderCode + "'";
        TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
        sqlparm = sqlparm.getRow(0);
        orderCodeSki = parm.getValue("ORDER_CODE", row);// yanjing 20131107
		// ҽ�����루Ƥ�ԣ�
		orderNoSki = parm.getValue("ORDER_NO", row);// yanjing 20131107 ҽ�����루Ƥ�ԣ�
		orderSeqSki = parm.getValue("ORDER_SEQ", row);// yanjing 20131107
        // System.out.println("==========="+sql);
        // ״̬����ʾҽ����ʾ
        callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
                + sqlparm.getValue("ORDER_DESC") + " "
                + sqlparm.getValue("GOODS_DESC") + " "
                + sqlparm.getValue("DESCRIPTION") + " "
                + sqlparm.getValue("SPECIFICATION") + " "
                + sqlparm.getValue("REMARK_1") + " "
                + sqlparm.getValue("REMARK_2") + " "
                + sqlparm.getValue("DRUG_NOTES_DR"));
        // ================= add by chenxi 20120703

    }

    /**
     * ����HL7��Ϣ
     * 
     * @param admType
     *            String �ż�ס��
     * @param catType
     *            ҽ�����
     * @param patName
     *            ��������
     * @param caseNo
     *            String �����
     * @param applictionNo
     *            String �����
     * @param flg
     *            String ״̬(0,����1,ȡ��)
     */
    private void sendHL7Mes() {
        int count = ((Vector) sendHL7Parm.getData("CASE_NO")).size();
        if (count <= 0) {
            return;
        }
        List list = new ArrayList();
        for (int i = 0; i < count; i++) {
            String sql = " SELECT * FROM ODI_ORDER WHERE CASE_NO ='"
                    + sendHL7Parm.getValue("CASE_NO", i) + "' AND ORDER_NO='"
                    + sendHL7Parm.getValue("ORDER_NO", i) + "' AND ORDER_SEQ="
                    + sendHL7Parm.getInt("ORDER_SEQ", i) + "";

            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            
            // add by wangb 2016/09/23 һ���ٴ�PKѪ����ҽ����������Ϣ
            if (pkOrderCode.contains(result.getValue("ORDER_CODE", 0))) {
            	continue;
            }
            
            TParm parm = new TParm();
            parm.setData("PAT_NAME", patName);
            parm.setData("ADM_TYPE", "I");
            parm.setData("FLG", flg);
            parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
            parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
            parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
            parm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
            parm.setData("SEQ_NO", result.getInt("ORDER_SEQ", 0));
            // add by wangb 2016/09/26 һ���ٴ���ɫע��
            if ("PIC".equals(outsideParm.getValue("INW", "ROLE_TYPE"))) {
            	parm.setData("ROLE_TYPE", "PIC");
            }
            list.add(parm);
        }
        // ���parm
        while (sendHL7Parm.getCount("CASE_NO") > 0) {
            sendHL7Parm.removeRow(0);
        }
        // ���ýӿ�
        TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
        if (resultParm.getErrCode() < 0)
            this.messageBox(resultParm.getErrText());
    }

    // /**
    // * CIS����HL7��Ϣ
    // */
    // private void sendICUHl7Mes() {
    // // ����CISHl7�ӿ�
    // List list = new ArrayList();
    // list.add(CISHl7Parm);
    // TParm CISparm = Hl7Communications.getInstance().Hl7MessageCIS(list,
    // "NBW");
    // if (CISparm.getErrCode() < 0) {
    // this.messageBox(CISparm.getErrText());
    // } else {
    // this.messageBox("����CIS��Ϣ�ɹ���");
    // }
    // // ���parm
    // while (CISHl7Parm.getCount("CASE_NO") > 0) {
    // CISHl7Parm.removeRow(0);
    // }
    // }

    /**
     * ȡ��ִ��������
     * 
     * @return boolean
     */
    public boolean onUndoExec() {
        // �õ���������--չ���˵�caseNo
        TParm execData = new TParm();
        TParm tablValue = masterTbl.getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int rowCount = masterTbl.getRowCount();
        boolean sendFlg = true;
        for (int i = 0; i < rowCount; i++) {
            String caseNo = (String) tablValue.getData("CASE_NO", i);
            String orderNo = (String) tablValue.getData("ORDER_NO", i);
            String orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
            String cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
            String orderDateD = tablValue.getData("ORDER_DATE", i).toString();
            if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 15))) {//modify by wukai 20160601 12��14  //modify by machao 20170213 14��15
            	// yanjing 20140514 ȡ�����ʱ���PHA_ANTI������Ӧ��Ƥ�Խ����ֵ start
				String orderCode = (String) tablValue.getData("ORDER_CODE", i);
				String psSql = "SELECT ORDER_DATE FROM PHA_ANTI WHERE CASE_NO = '"
						+ caseNo
						+ "' AND ORDER_CODE = '"
						+ orderCode
						+ "' AND ROUTE_CODE = 'PS'  "
						+ "ORDER BY ORDER_DATE DESC";
				TParm selectparm = new TParm(TJDODBTool.getInstance().select(
						psSql));
				if (selectparm.getCount() > 0) {// ���Ƥ��ע��
					String optDate = selectparm.getValue("ORDER_DATE", 0)
							.substring(0, 19).replace("-", "").replace("/", "")
							.replace(" ", "").replace(":", "");
					String updatePhaAnti = " UPDATE PHA_ANTI SET BATCH_NO = '',SKINTEST_NOTE= '' "
							+ "WHERE CASE_NO= '"
							+ caseNo
							+ "' AND ORDER_CODE='"
							+ orderCode
							+ "'AND OPT_DATE = TO_DATE('"
							+ optDate
							+ "','YYYYMMDDHH24MISS') ";
					TParm parm = new TParm(TJDODBTool.getInstance().update(
							updatePhaAnti));

				}
                execData.addData("CASE_NO", caseNo);
                execData.addData("ORDER_NO", orderNo);
                execData.addData("ORDER_SEQ", orderSeq);
                execData.addData("DC_DATE", tablValue.getData("DC_DATE", i));// ����DC_DATE��̨�ж���ȡ��ִ��/ȡ��ִ��DC
                execData.addData("NS_EXEC_CODE", tablValue.getData(
                        "NS_EXEC_CODE", i));
                execData.addData("NS_EXEC_DATE", tablValue.getData(
                        "NS_EXEC_DATE", i));
                execData.addData("NS_EXEC_DC_CODE", tablValue.getData(
                        "NS_EXEC_DC_CODE", i));
                execData.addData("NS_EXEC_DC_DATE", tablValue.getData(
                        "NS_EXEC_DC_DATE", i));
                execData.addData("OPT_USER", Operator.getID());
                // =============yanjing 20140515 ��ջ�ʿ��ע start
				execData.addData("EXEC_NOTE", "");
				execData.addData("ORDER_DATE", orderDateD.substring(0, 4)
						+ orderDateD.substring(5, 7)
						+ orderDateD.substring(8, 10));
				execData.addData("ORDER_DATETIME", orderDateD.substring(11, 13)
						+ orderDateD.substring(15));
				// ==============yanjing 20140515 ��ջ�ʿ��ע end
                execData.addData("OPT_DATE", now);
                execData.addData("OPT_TERM", Operator.getIP());
                execData.addData("CAT1_TYPE", tablValue.getData("CAT1_TYPE", i));
                // ������ҽ��
                String setMainFlg = tablValue.getData("SETMAIN_FLG", i) + "";
                String orderSetGroupNo = tablValue.getData("ORDERSET_GROUP_NO",
                        i)
                        + "";
                execData.addData("SETMAIN_FLG", setMainFlg);
                execData.addData("ORDERSET_GROUP_NO", orderSetGroupNo);
       
                Timestamp falseParm = new Timestamp(0);
                execData.addData("START_DTTM", tablValue.getData("START_DTTM",
                        i));
                execData.addData("END_DTTM", tablValue.getData("END_DTTM", i));
                // ======add by wanglong 20130527
                execData.addData("MR_NO", tablValue.getValue("MR_NO", i));
                execData.addData("ORDER_CODE", tablValue.getValue("ORDER_CODE",
                        i));
                execData.addData("ORDERSET_CODE", tablValue.getValue(
                        "ORDER_CODE", i));
                execData.addData("ORDER_DESC", tablValue.getValue(
                        "ORDER_DESC_AND_SPECIFICATION", i));
                execData.addData("DOSAGE_QTY", tablValue.getValue("DOSAGE_QTY",
                        i));
                // ======add end
                // ���CAT1_TYPEΪLIS����RIS�Ľ��ᷢ�͸�HL7�ӿ�
                if ("LIS".equals(cat1Type) || "RIS".equals(cat1Type)) {
                    if (Hl7Communications.getInstance().IsExeOrder(
                            tablValue.getRow(i), "I")) {
                        String orderDate = tablValue.getValue("ORDER_DATE", i)
                                .substring(0, 19).replaceAll("-", "/");
                        this.messageBox(tablValue.getValue("ORDER_DESC", i)
                                + "�Ѿ�ִ�У�����ȡ����\n �´�ʱ��:" + orderDate);
                        sendFlg = false;
                        break;
                    }
                    sendHL7Parm.addData("CASE_NO", caseNo);
                    sendHL7Parm.addData("ORDER_NO", orderNo);
                    sendHL7Parm
                            .addData("ORDER_SEQ", Integer.parseInt(orderSeq));
                    flg = 1;
                }
            }
        }
        if (!sendFlg) {
            return false;
        }
        execData = checkDCQtyIsLess(execData);// add by wanglong 20130527
        if (execData == null) {// add by wanglong 20130527
            return false;
        }
        // // 20120228--------------------------------
        // execData.setData("ADM_TYPE", "I");
        // // System.out.println("-=-=-------------------------"+execData);
        // execData = SysPhaBarTool.getInstance().getaddBarParm(execData,
        // "INW");
        // �õ�����
        int count = ((Vector) execData.getData("OPT_USER")).size();
        execData.setCount(count);
        // ����actionִ������
        TParm result = TIOM_AppServer.executeAction(
                "action.inw.InwOrderExecAction", "onUndoSave", execData);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        }

        return true;

    }

    /**
     * ִ��������
     * 
     * @return boolean
     */
    public boolean onExec() {
        // �õ���������--չ���˵�caseNo
        TParm inParm = new TParm();// ���
        TParm execData = new TParm();
        TParm tablValue = masterTbl.getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int rowCount = masterTbl.getRowCount();
        Set<String> caseNoSet = new HashSet<String>();// wanglong add 20140731
        // ���������
        for (int i = 0; i < rowCount; i++) {
            String caseNo = (String) tablValue.getData("CASE_NO", i);
            String orderNo = (String) tablValue.getData("ORDER_NO", i);
            String orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
            String cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
            String orderCode = (String) tablValue.getData("ORDER_CODE", i);
            String order_dr = (String) tablValue.getData("ORDER_DR_CODE", i);
            // �ж��Ƿ�дִ��/DC����Ա
            String dcFlg = (String) tablValue.getData("DC_DR_CODE", i);
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 15))) {   //modify by wukai 20160601 12��14  //modify by machao 20170213 14��15
            	
                execData.addData("CASE_NO", caseNo);
                execData.addData("ORDER_NO", orderNo);
                execData.addData("ORDER_SEQ", orderSeq);
                execData.addData("ORDER_CODE", orderCode);
  
                // ��ҽ��ΪDCҽ��
                if (dcFlg != null) {
                    execData.addData("DC_ORDER", true);
                    // =============add by wanglong 20130619
                    if(!tablValue.getRow(i).getValue("CAT1_TYPE").equals("PHA")){
                        TParm dCQty =
                                InwOrderExecTool.getInstance().getDCOrder(tablValue.getRow(i));
                        int dcQty = dCQty.getInt("DC_QYT", 0);
                        String sumCountSql =
                                "SELECT SUM(DOSAGE_QTY) COUNT FROM IBS_ORDD WHERE CASE_NO = '#' AND ORDER_CODE = '#' GROUP BY CASE_NO, ORDER_CODE";
                        String sql =
                                sumCountSql.replaceFirst("#", caseNo).replaceFirst("#", orderCode);
                        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                        if (result.getErrCode() != 0) {
                            this.messageBox("��ѯ��DCҽ��������ʹ�ù���������ʧ��");
                            return false;
                        }
                        if (result.getInt("COUNT", 0) < dcQty) {
                            this.messageBox(tablValue.getRow(i).getValue("ORDER_DESC") + "��"
                                    + orderCode + "��ȡ��ִ�е��������ڲ�����"
                                    + tablValue.getRow(i).getValue("PAT_NAME")
                                    + "��ִ�й�������������ǰ��������ȡ��");
                            return false;
                        }
                    }
                    // =============add end
                } else {
                    caseNoSet.add(caseNo);// wanglong add 20140731
                    execData.addData("DC_ORDER", false);
                }
                execData.addData("OPT_USER", Operator.getID());
                execData.addData("OPT_DATE", now);
                execData.addData("OPT_TERM", Operator.getIP());
                // ҽ����𣺺�̨�����Ƿ��͸�IBS�Ʒ�
                execData.addData("CAT1_TYPE", cat1Type);

                String execDate = "" + masterTbl.getValueAt(i, 20);  //modify by wukai 20160601 18��20
                String execTime = ("" + masterTbl.getValueAt(i, 21)).replace(  //modify by wukai 20160601 19��21
                        ":", "");
                // String execDate = "" + masterTbl.getValueAt(i, 18);
                Timestamp checkDateTime = StringTool.getTimestamp(execDate
                        .substring(0, 10).replaceAll("/", "").replaceAll("-",
                                "")
                        + " " + execTime.substring(0, 4) + "00",
                        "yyyyMMdd HHmmss");

                // �����˹��޸�
                execData.addData("NS_EXEC_DATE", checkDateTime);
                // ������ҽ��
                String setMainFlg = tablValue.getData("SETMAIN_FLG", i) + "";
                String orderSetGroupNo = tablValue.getData("ORDERSET_GROUP_NO",
                        i)
                        + "";
                execData.addData("START_DTTM", tablValue.getData("START_DTTM",
                        i));
                execData.addData("END_DTTM", tablValue.getData("END_DTTM", i));

                // ��IBS�õĲ���
                // execData.addData("CTZ1_CODE", tablValue.getData("CTZ1_CODE",
                // i));
                // execData.addData("CTZ2_CODE", tablValue.getData("CTZ2_CODE",
                // i));
                // execData.addData("CTZ3_CODE", tablValue.getData("CTZ3_CODE",
                // i));

                // // CISHl7����ҽ��
                // CISHl7Parm.addData("CASE_NO", caseNo);
                // CISHl7Parm.addData("ORDER_NO", orderNo);
                // CISHl7Parm.addData("ORDER_SEQ", orderSeq);
                // CISHl7Parm.addData("ORDER_DR_CODE", order_dr);
                // CISHl7Parm.addData("START_DTTM",
                // tablValue.getData("START_DTTM", i));
                execData.addData("SETMAIN_FLG", setMainFlg);
                execData.addData("ORDERSET_GROUP_NO", orderSetGroupNo);
                // System.out.println("cat1Type =----" + cat1Type);
                // ���CAT1_TYPEΪLIS����RIS�Ľ��ᷢ�͸�HL7�ӿ�
                if ("LIS".equals(cat1Type) || "RIS".equals(cat1Type)) {
                    sendHL7Parm.addData("CASE_NO", caseNo);
                    sendHL7Parm.addData("ORDER_NO", orderNo);
                    sendHL7Parm
                            .addData("ORDER_SEQ", Integer.parseInt(orderSeq));
                    flg = 0;
                }
            }
        }
        // -------------------------SHIBLadd
        // // 20120228--------------------------------
        // execData.setData("ADM_TYPE", "I");
        // // System.out.println("-=-=-------------------------"+execData);
        // execData = SysPhaBarTool.getInstance().getaddBarParm(execData,
        // "INW");
        // System.out.println("----------------------------"+execData);
        // ϸ������ �õ�ִ�б�ע��������
        detailTbl.acceptText();
        int TblDRow = detailTbl.getRowCount();
        TParm detailTblValue = detailTbl.getParmValue();
        TParm nuserNote = new TParm();
        int noteCount = 0;
        for (int i = 0; i < TblDRow; i++) {
            String execNote = (String) detailTbl.getValueAt(i, 4);
            String caseNo = (String) detailTblValue.getData("CASE_NO", i);
            String orderNo = (String) detailTblValue.getData("ORDER_NO", i);
            String orderSeq = detailTblValue.getData("ORDER_SEQ", i) + "";
            String orderDate = (String) detailTblValue.getData("ORDER_DATE", i);
            String orderDateTime = (String) detailTblValue.getData(
                    "ORDER_DATETIME", i);
            // ��Ҫ���������
            nuserNote.addData("CASE_NO", caseNo);
            nuserNote.addData("ORDER_NO", orderNo);
            nuserNote.addData("ORDER_SEQ", orderSeq);
            nuserNote.addData("ORDER_DATE", orderDate.substring(0, 4)
                    + orderDate.substring(5, 7) + orderDate.substring(8));
            nuserNote.addData("ORDER_DATETIME", orderDateTime.substring(0, 2)
                    + orderDateTime.substring(3));
            nuserNote.addData("EXEC_NOTE", execNote);
            nuserNote.addData("OPT_USER", Operator.getID());
            nuserNote.addData("OPT_TERM", Operator.getIP());
            noteCount++;
        }
        nuserNote.setCount(noteCount);
        // ����ʿע�Ͳ�Ϊ��
        if (noteCount != 0) {
            // �ѻ�ʿ��עѹ���̨����
            inParm.setData("EXECNOTE", nuserNote.getData());
        }
        // ����Ƿ�����Ҫ���������
        if ((Vector) execData.getData("OPT_USER") != null) {
            // �õ�����
            int count = ((Vector) execData.getData("OPT_USER")).size();
            execData.setCount(count);
            inParm.setData("EXECDATA", execData.getData());
            // ���Ϊ��֪IBS�ӿ��Ǽ������߳帺
            if (checkYES.isSelected())
                inParm.setData("FLG", "SUB");
            else
                inParm.setData("FLG", "ADD");

        }
        // System.out.println("-------------------------"+inParm);
        String caseNoStr = "";// wanglong add 20140731
        // modify by wangb 2015/06/15 ��ʳҽ�������ı�ʱ�����������Ϣ
        String nurseSql =
                "SELECT CASE_NO, NURSING_CLASS,DIE_CONDITION FROM ADM_INP WHERE CASE_NO IN(#) ORDER BY CASE_NO";
        TParm nurseBefore = new TParm();
        if (caseNoSet.size() > 0) {// wanglong add 20140731
            for (String caseNo : caseNoSet) {
                caseNoStr += "'" + caseNo + "',";
            }
            caseNoStr = caseNoStr.substring(0, caseNoStr.length() - 1);// �ݲ����ǳ���1000�˵����
            nurseSql = nurseSql.replaceFirst("#", caseNoStr);
            nurseBefore = new TParm(TJDODBTool.getInstance().select(nurseSql));
        }
        // ����actionִ������
        TParm result = TIOM_AppServer.executeAction(
                "action.inw.InwOrderExecAction", "onSave", inParm);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        } else {
            if (caseNoSet.size() > 0) {// wanglong add 20140731
                TParm nurseAfter = new TParm(TJDODBTool.getInstance().select(nurseSql));
                if (nurseBefore.getCount() == nurseAfter.getCount()) {
                    for (int i = 0; i < nurseBefore.getCount(); i++) {
                    	// modify by wangb 2015/06/15 ��ʳҽ�������ı�ʱ�����������Ϣ
						if (!nurseBefore.getValue("NURSING_CLASS", i).equals(
								nurseAfter.getValue("NURSING_CLASS", i))
								|| !nurseBefore.getValue("DIE_CONDITION", i)
										.equals(nurseAfter.getValue("DIE_CONDITION", i))) {
                            TParm xmlParm = ADMXMLTool.getInstance().creatXMLFile(nurseAfter.getValue("CASE_NO", i));// ���Խӿ�
                            if (xmlParm.getErrCode() < 0) {
                                this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
                            }
                            // �������ӿ� wanglong add 20141010
                            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(nurseAfter.getValue("CASE_NO", i));
                            if (xmlParm.getErrCode() < 0) {
                                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
                            }
                        }
                    }
                }
            }
//            onCheckData(inParm);// shibl add 20130507
            this.onPrint();
        }
        return true;
    }
    /**
     * У�黤ʿִ�к�������ִ�ж�ϸ��δִ��
     * 
     * @param inParm
     */
    public void onCheckData(TParm inParm) {
        TParm Execparm = inParm.getParm("EXECDATA");// ִ������
        StringBuffer str = new StringBuffer();
        Map execpat = InwOrderExecTool.getInstance().groupByPatParm(Execparm);
        Iterator it = execpat.values().iterator();
        while (it.hasNext()) {
            TParm patParm = (TParm) it.next();
            String caseNo = patParm.getValue("CASE_NO", 0);
            String sql = " SELECT DISTINCT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.END_DTTM,A.MR_NO "
                    + " FROM ODI_DSPNM A,"
                    + " (SELECT CASE_NO,ORDER_NO,ORDERSET_CODE,ORDERSET_GROUP_NO "
                    + " FROM ODI_DSPNM WHERE CASE_NO='"
                    + caseNo
                    + "' AND NS_EXEC_CODE IS NOT NULL AND ORDERSET_CODE IS NOT NULL ) B "
                    + " WHERE A.CASE_NO=B.CASE_NO  AND A.ORDER_NO=B.ORDER_NO"
                    + " AND A.ORDERSET_CODE=B.ORDERSET_CODE "
                    + " AND A.ORDERSET_GROUP_NO=B.ORDERSET_GROUP_NO "
                    + " AND A.NS_EXEC_CODE IS  NULL";
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            if (parm.getCount() > 0) {
                if (str.toString().length() > 0)
                    str.append("\r\n");
                str.append("������:" + parm.getValue("MR_NO", 0));
                str.append("\r\n");
                str.append("δִ��ϸ��PARM:" + parm);
                str.append("\r\n");
                str.append("���PARM:" + patParm);
            }
        }
        if (str.toString().length() > 0) {
            String sysDateS = StringTool.getString(TJDODBTool.getInstance()
                    .getDBTime(), "yyyyMMddHHmm");
            String root = TIOM_FileServer.getRoot();
            boolean flg = TIOM_FileServer.writeFile(
                    TIOM_FileServer.getSocket(), root + "\\NsExeclog\\log"
                            + sysDateS + ".txt", str.toString().getBytes());
        }
    }

    /**
     * ����������֤
     * 
     * @return boolean
     */
    public boolean checkPW() {
        String inwExe = "inwExe";
        String value = (String) this.openDialog(
                "%ROOT%\\config\\inw\\passWordCheck.x", inwExe);
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }

    /**
     * ɸѡʱ��ҽ��
     */
    public void SelectTime() {
        masterTbl.acceptText();
        TParm parm = masterTbl.getParmValue();
        if (parm.getCount() <= 0) {
            this.messageBox("û�п�ɸѡ������");
            return;
        }
        String time = this.getValueString("TIME");
        TParm value = (TParm) this.openDialog(
                "%ROOT%\\config\\inw\\INWPhaFreqUI.x", time);
        if (value == null) {
            this.clearValue("TIME");
            return;
        }
        if (value.getCount() <= 0) {
            this.clearValue("TIME");
            return;
        }
        StringBuffer line = new StringBuffer();
        for (int i = 0; i < value.getCount(); i++) {
            line.append(value.getValue("TIME", i).substring(0, 2) + ":"
                    + value.getValue("TIME", i).substring(2, 4));
            line.append(";");
        }
        this.setValue("TIME", line.toString());
        // ���������
        for (int j = parm.getCount("CASE_NO") - 1; j >= 0; j--) {
            String caseNo = parm.getValue("CASE_NO", j);
            String orderNo = parm.getValue("ORDER_NO", j);
            String orderSeq = parm.getValue("ORDER_SEQ", j);
            String startDttm = parm.getValue("START_DTTM", j);
            String endDttm = parm.getValue("END_DTTM", j);
            String cat1Type = parm.getValue("CAT1_TYPE", j);
            if (!ExeDTableData(caseNo, orderNo, orderSeq, startDttm, endDttm,
                    value, cat1Type))
                parm.removeRow(j);
        }
        masterTbl.setParmValue(parm);
        return;
    }

    /**
     * ��������
     * 
     * @param caseNo
     * @param orderNo
     * @param orderSeq
     * @param startDttm
     * @param endDttm
     * @param parm
     * @return
     */
    private boolean ExeDTableData(String caseNo, String orderNo,
            String orderSeq, String startDttm, String endDttm, TParm parm,
            String cat1Type) {
        String sql = "SELECT * FROM ODI_DSPND WHERE CASE_NO='" + caseNo
                + "' AND ORDER_NO='" + orderNo + "' AND " + " ORDER_SEQ='"
                + orderSeq + "' AND ORDER_DATE||ORDER_DATETIME BETWEEN '"
                + startDttm + "' AND '" + endDttm + "'";
        // System.out.println("========sql=============="+sql);
        TParm orderDParm = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("========orderDParm=============="+orderDParm);
        if (orderDParm.getCount() <= 0)
            return false;
        for (int i = 0; i < orderDParm.getCount(); i++) {
            TParm parmRow = orderDParm.getRow(i);
            String orderDataTime = parmRow.getValue("ORDER_DATETIME");
            for (int j = 0; j <= parm.getCount(); j++) {
                String MapTime = parm.getValue("TIME", j);
                if (!cat1Type.equals("PHA") && MapTime.equals("0000")) {
                    MapTime = "2359";
                }
                if (MapTime.equals(orderDataTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ѡ��
     * 
     * @param i
     *            int
     * @param nowFlag
     *            boolean
     * @param optName
     *            String
     * @param chackTime
     *            Timestamp
     */
    private void selection(int i, String optName, Timestamp chackTime) {
        masterTbl.setItem(i, "EXE_FLG", "Y");
        // masterTbl.setValueAt(true, i, 12);
        TParm parm = masterTbl.getParmValue().getRow(i);
        // DCʱ�䣬�����Ƿ���DC
        Timestamp dcDate = (Timestamp) masterTbl.getValueAt(i, 18); //modify by wukai 20160601 15��17 //modify by machao 20170120 17��18
        if (dcDate != null) {
            // ���Ŀǰ��û��ִ��ʱ��Ͱ�ִ��ʱ��һ��д��ȥ
            String execDate = "" + masterTbl.getValueAt(i, 20); //modify by wukai 20160601 18��20
            if ("".equals(execDate.trim()) || "null".equals(execDate.trim())) {
                // ��ʱҽ�����ʱ��Ĭ��Ϊ����ʱ��+10���� shibl 20120723 add
                // if (parm.getValue("DSPN_KIND").equals("ST")
                // && parm.getValue("DR_NOTE").equals("")) {
                // Timestamp effdate = parm.getTimestamp("ORDER_DATE");
                // Timestamp STnsexeTime = new Timestamp(
                // effdate.getTime() + 10L * 60L * 1000L);
                // masterTbl.setItem(i, "NS_EXEC_CODE", optName);
                // masterTbl.setItem(i, "NS_EXEC_DATE",
                // StringTool.getString(STnsexeTime, "yyyy/MM/dd"));
                // masterTbl.setItem(i, "NS_EXEC_DATE_TIME",
                // StringTool.getString(STnsexeTime, "HH:mm:ss").substring(0,
                // 5));
                // // masterTbl.setValueAt(optName, i, 17);
                // // masterTbl.setValueAt(
                // // StringTool.getString(STnsexeTime, "yyyy/MM/dd"), i,
                // // 18);
                // // masterTbl.setValueAt(
                // // StringTool.getString(STnsexeTime, "HH:mm:ss"), i,
                // // 19);
                // } else {
                masterTbl.setItem(i, "NS_EXEC_CODE", optName);
                masterTbl.setItem(i, "NS_EXEC_DATE", StringTool.getString(
                        chackTime, "yyyy/MM/dd"));
                masterTbl.setItem(i, "NS_EXEC_DATE_TIME", StringTool.getString(
                        chackTime, "HH:mm:ss").substring(0, 5));
                // masterTbl.setValueAt(optName, i, 17);
                // masterTbl.setValueAt(
                // StringTool.getString(chackTime, "yyyy/MM/dd"), i,
                // 18);
                // masterTbl.setValueAt(
                // StringTool.getString(chackTime, "HH:mm:ss"), i, 19);
                // }
            }
            masterTbl.setItem(i, "NS_EXEC_DC_CODE", optName);
            masterTbl.setItem(i, "NS_EXEC_DC_DATE_DAY", StringTool.getString(
                    chackTime, "yyyy/MM/dd"));
            masterTbl.setItem(i, "NS_EXEC_DC_DATE_TIME", StringTool.getString(
                    chackTime, "HH:mm:ss"));
            // masterTbl.setValueAt(optName, i, 22);
            // masterTbl.setValueAt(StringTool.getString(chackTime,
            // "yyyy/MM/dd"),
            // i, 23);
            // masterTbl.setValueAt(StringTool.getString(chackTime, "HH:mm:ss"),
            // i, 24);
        } else {
            // ��ʱҽ�����ʱ��Ĭ��Ϊ����ʱ��+10���� shibl 20120723 add
            // if (parm.getValue("DSPN_KIND").equals("ST")
            // && parm.getValue("DR_NOTE").equals("")) {
            // Timestamp effdate = parm.getTimestamp("ORDER_DATE");
            // Timestamp STnsexeTime = new Timestamp(
            // effdate.getTime() + 10L * 60L * 1000L);
            // masterTbl.setItem(i, "NS_EXEC_CODE", optName);
            // masterTbl.setItem(i, "NS_EXEC_DATE",
            // StringTool.getString(STnsexeTime, "yyyy/MM/dd"));
            // masterTbl.setItem(i, "NS_EXEC_DATE_TIME",
            // StringTool.getString(STnsexeTime, "HH:mm:ss").substring(0, 5));
            // // masterTbl.setValueAt(optName, i, 17);
            // // masterTbl.setValueAt(
            // // StringTool.getString(STnsexeTime, "yyyy/MM/dd"), i, 18);
            // // masterTbl.setValueAt(
            // // StringTool.getString(STnsexeTime, "HH:mm:ss"), i, 19);
            // } else {
            masterTbl.setItem(i, "NS_EXEC_CODE", optName);
            masterTbl.setItem(i, "NS_EXEC_DATE", StringTool.getString(
                    chackTime, "yyyy/MM/dd"));
            masterTbl.setItem(i, "NS_EXEC_DATE_TIME", StringTool.getString(
                    chackTime, "HH:mm:ss").substring(0, 5));
            // masterTbl.setValueAt(optName, i, 17);
            // masterTbl.setValueAt(
            // StringTool.getString(chackTime, "yyyy/MM/dd"), i, 18);
            // masterTbl.setValueAt(
            // StringTool.getString(chackTime, "HH:mm:ss"), i, 19);
            // }
        }

    }

    /**
     * table�ϵ�checkBoxע�����
     * 
     * @param obj
     *            Object
     */
    public void onMasterTableCheckBoxChangeValue(Object obj) {
        // ��ȫ��ִ�е�ʱ������һ��ʱ��
        Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
        // ��õ����table����
        TTable table = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        table.acceptText();
        TParm tblParm = table.getParmValue();

        // ���ѡ�е���/��
        int col = table.getSelectedColumn();
        int row = table.getSelectedRow();
        // ���table�ϵ�����
        int rowcount = table.getRowCount();
        // ���ѡ�е��ǵ�16�оͼ���ִ�ж���--ִ��
        String columnName = table.getParmMap(col);
        
        if (columnName.equals("EXE_FLG")) {
            boolean exeFlg;
            // ��õ��ʱ��ֵ
            exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
            // ��ѡʱ
            if (exeFlg) {
                // ��ѡ�ж���
                selection(row, Operator.getName(), chackTime);
                // �õ�ѡ�����ݵ�ҽ�����ͣ����������ǲ�ͬҽ�����͸��Լ������Ӻţ�����Ϊ�˱����ڡ�ȫ������״���»�����ظ����Ӻ������
                String rxKind = (String) tblParm.getValue("DSPN_KIND", row);
                // -----------------------��������ҽ��start----------------------------
                // �ҵ���ͬ�����Ӻ�
                String linkNo = (String) table.getValueAt(row, 4);
                
                //add by yangjj 20150529����MR_NO�ж�
                String mrNo = (String) table.getValueAt(row, 27);    //modify by wukai 20160601 25��27
                
                if (TCM_Transform.getInt(linkNo) > 0) {
                    for (int i = 0; i < rowcount; i++) {
                        // ���˵�ǰ������к������
                        if (i != row
                                && linkNo.equals((String) table
                                        .getValueAt(i, 4))
                                && rxKind.equals((String) tblParm.getValue(
                                        "DSPN_KIND", i))
                                
                                //add by yangjj 20150529����MR_NO�ж�
                                && mrNo.equals((String) table
                                        .getValueAt(i, 27))) {   //modify by wukai 20160601 25��27

                            selection(i, Operator.getName(), chackTime);
                        }
                    }
                }
                // -------------------------�����в�ҩstart-----------------------
                if ("IG".equals(rxKind)) {
                    // �в�ҩ��ʱ����RX_NO(����ǩ)
                    String rxNo = (String) tblParm.getValue("RX_NO", row);
                    for (int i = 0; i < rowcount; i++) {
                        // ��¼������RX_NO
                        String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
                        // ���˵�ǰ������к������
                        if (!"".equals(rxNoTemp) && rxNoTemp.equals(rxNo)) {
                            selection(i, Operator.getName(), chackTime);
                        }
                    }
                }
            } else { // ȡ��ʱ ��ѡ�ж���
                unselection(row);
                callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107 Ƥ�Խ����ť
                // �õ�ѡ�����ݵ�ҽ�����ͣ����������ǲ�ͬҽ�����͸��Լ������Ӻţ�����Ϊ�˱����ڡ�ȫ������״���»�����ظ����Ӻ������
                String rxKind = (String) tblParm.getValue("DSPN_KIND", row);
                // -----------------------��������ҽ��start----------------------------
                // �ҵ���ͬ�����Ӻ�
                String linkNo = (String) table.getValueAt(row, 4);
                //add by yangjj 20150529����MR_NO�ж�
                String mrNo = (String) table.getValueAt(row, 27);   //modify by wukai 20160601��25Ϊ27
                
                if (TCM_Transform.getInt(linkNo) > 0) {
                    for (int i = 0; i < rowcount; i++) {
                        // ���˵�ǰ������к������
                        if (i != row
                                && linkNo.equals((String) table
                                        .getValueAt(i, 4))
                                && rxKind.equals((String) tblParm.getValue(
                                        "DSPN_KIND", i))
                                //add by yangjj 20150529����MR_NO�ж�
                                && mrNo.equals((String) table
                                        .getValueAt(i, 27))) { //modify by wukai 20160601��25Ϊ27
                            unselection(i);
                        }
                    }
                }
                // -------------------------�����в�ҩstart-----------------------
                if ("IG".equals(rxKind)) {
                    // �в�ҩ��ʱ����RX_NO(����ǩ)
                    String rxNo = (String) tblParm.getValue("RX_NO", row);
                    for (int i = 0; i < rowcount; i++) {
                        // ��¼������RX_NO
                        String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
                        // ���˵�ǰ������к������
                        if (!"".equals(rxNoTemp) && rxNoTemp.equals(rxNo)) {
                            unselection(i);
                        }
                    }
                }
            }
            // ---------------------������ͬ����ŵ�ҽ��start-------------------add by wanglong 20130809
            String boolFlg = table.getValueAt(row, col).toString();
            if (tblParm.getValue("ORDER_CAT1_CODE", row).equals("LIS")
                    || tblParm.getValue("ORDER_CAT1_CODE", row).equals("RIS")) {// �������������
                // �ҵ���ͬ�������
                TParm medNoParm = InwForOdiTool.getInstance().queryMedNo(tblParm.getRow(row));
                if (medNoParm.getErrCode() >= 0 && medNoParm.getCount() > 0) {
                    String caseNo = tblParm.getValue("CASE_NO", row);
                    String orderNo = tblParm.getValue("ORDER_NO", row);
                    String orderCat1Code = tblParm.getValue("ORDER_CAT1_CODE", row);
                    String optItemCode = tblParm.getValue("OPTITEM_CODE", row);
                    String medNo = medNoParm.getValue("MED_APPLY_NO", 0);
                    for (int i = 0; i < rowcount; i++) {// ����masterTableÿһ�����ݣ�����ͬ��
                        if (i != row && tblParm.getValue("CASE_NO", i).equals(caseNo)
                                && tblParm.getValue("ORDER_NO", i).equals(orderNo)
                                && tblParm.getValue("ORDER_CAT1_CODE", i).equals(orderCat1Code)
                                && tblParm.getValue("OPTITEM_CODE", i).equals(optItemCode)) {
                            TParm rowMedNoParm =
                                    InwForOdiTool.getInstance().queryMedNo(tblParm.getRow(i));
                            if (rowMedNoParm.getErrCode() < 0) {
                                this.messageBox(rowMedNoParm.getErrText());
                                break;
                            }
                            if (rowMedNoParm.getValue("MED_APPLY_NO", 0).equals(medNo)) {
                                if(boolFlg.equals("Y")){//modify by wanglong 20130820
                                    selection(i, Operator.getName(), chackTime);
                                }else{
                                    unselection(i);
                                }
                            }
                        }
                    }
                } else if (medNoParm.getErrCode() < 0) {
                    this.messageBox(medNoParm.getErrText());
                }
            }
            // -----------------------end----------------------------------------
        }
    }

    /**
     * ȡ��ѡ��
     * 
     * @param i
     *            int
     * @param nowFlag
     *            boolean
     * @param optName
     *            String
     * @param chackTime
     *            Timestamp
     */
    private void unselection(int i) {
        masterTbl.setItem(i, "EXE_FLG", "N");
        // masterTbl.setValueAt(false, i, 12);
        // DCʱ�䣬�����Ƿ���DC
        Timestamp dcDate = (Timestamp) masterTbl.getValueAt(i, 18); //modify by wukai 20160601��15Ϊ17//modify by machao 20170120��17Ϊ18
        if (dcDate != null) {
            masterTbl.setItem(i, "NS_EXEC_DC_CODE", "");
            masterTbl.setItem(i, "NS_EXEC_DC_DATE_DAY", "");
            masterTbl.setItem(i, "NS_EXEC_DC_DATE_TIME", "");
            // masterTbl.setValueAt("", i, 22);
            // masterTbl.setValueAt("", i, 23);
            // masterTbl.setValueAt("", i, 24);

        } else {
            masterTbl.setItem(i, "NS_EXEC_CODE", "");
            masterTbl.setItem(i, "NS_EXEC_DATE", "");
            masterTbl.setItem(i, "NS_EXEC_DATE_TIME", "");
            // masterTbl.setValueAt("", i, 17);
            // masterTbl.setValueAt("", i, 18);
            // masterTbl.setValueAt("", i, 19);
        }
    }

    /**
     * ��ʼ������Ƽ۵���Ӧ����
     */
    public void initPanel() {
        // ��ʼ�����棨�벹��Ƽ���صĲ��֣�,ģ��MOVEPANEL��˫������
        mp1.onDoubleClicked(true);

    }

    /**
     * ��ʼ���������caseNo/stationCode
     */
    public void initParmFromOutside() {

        // ������Ų�ѯ��caseNo
        this.setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
        // ��������ѯ��stationCode
        this.setStationCode(outsideParm.getValue("INW", "STATION_CODE"));
        this.setMrNo(outsideParm.getValue("INW", "MR_NO"));
        this.setPatName(outsideParm.getValue("INW", "PAT_NAME"));
        this.setIpdNo(outsideParm.getValue("INW", "IPD_NO"));
        this.setDeptCode(outsideParm.getValue("INW", "DEPT_CODE"));
        // ����IBS��3����ݲ���
        ctz1Code = outsideParm.getValue("INW", "CTZ1_CODE");
        ctz2Code = outsideParm.getValue("INW", "CTZ2_CODE");
        ctz3Code = outsideParm.getValue("INW", "CTZ3_CODE");
        saveFlg = outsideParm.getBoolean("INW", "SAVE_FLG");
        schdCode = outsideParm.getValue("IBS", "SCHD_CODE");
//        System.out.println("pppppoooooutsideParm outsideParm is ::"+outsideParm);
        // ��ICUע��
        ICUflg = outsideParm.getBoolean("INW", "ICU_FLG");
        clpCode_ = outsideParm.getValue("INW", "CLNCPATH_CODE");
        pkOrderCode = TConfig.getSystemValue("PIC_PK_ORDER_CODE");
    }

    /**
     * �һ�MENU�����¼�
     * 
     * @param tableName
     */
    public void showPopMenu(String tableName) {
        // �õ�table����
        TTable table = (TTable) this.getComponent(tableName);
        // ���ѡ���е�TParm
        TParm action = masterTbl.getParmValue().getRow(
                masterTbl.getSelectedRow());
        if ("LIS".equals(action.getValue("CAT1_TYPE"))
                || "RIS".equals(action.getValue("CAT1_TYPE"))) {
            table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
            return;
        } else {
            table.setPopupMenuSyntax("");
            return;
        }
    }

    /**
     * �򿪼���ҽ��ϸ���ѯ
     */
    public void openRigthPopMenu() {
        // ���ѡ���е�TParm
        TParm action = masterTbl.getParmValue().getRow(
                masterTbl.getSelectedRow());
        int groupNo = action.getInt("ORDERSET_GROUP_NO");
        String orderCode = action.getValue("ORDER_CODE");
        String caseNo = action.getValue("CASE_NO");
        TParm parm = getOrderSetDetails(caseNo, groupNo, orderCode);
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
    }

    /**
     * ���ؼ���ҽ��ϸ���TParm��ʽ
     * 
     * @return result TParm
     */
    public TParm getOrderSetDetails(String caseNo, int groupNo,
            String orderSetCode) {
        TParm result = new TParm();
        if (groupNo < 0) {
            return result;
        }
        if (StringUtil.isNullString(orderSetCode)) {
            return result;
        }
        // ===========pangben modify 20110516 start �������
        String region = "";
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
        }
        // ===========pangben modify 20110516 stop

        String selSetOrder = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
                + "' AND ORDERSET_GROUP_NO<>0" + region;
        TParm parm = new TParm(TJDODBTool.getInstance().select(selSetOrder));
        int count = parm.getCount();
        if (count < 0) {
            return result;
        }
        String tempCode;
        int tempNo;
        for (int i = 0; i < count; i++) {
            tempCode = parm.getValue("ORDERSET_CODE", i);
            tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
            if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
                    && !parm.getBoolean("SETMAIN_FLG", i)) {
                result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
                result.addData("SPECIFICATION", parm.getValue("SPECIFICATION",
                        i));
                result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
                result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
                // ��ѯ����
                TParm ownPriceParm = new TParm(TJDODBTool.getInstance().select(
                        "SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
                                + parm.getValue("ORDER_CODE", i) + "'"));
                // �����ܼ۸�
                double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0)
                        * parm.getDouble("MEDI_QTY", i);
                result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE",
                        0));
                result.addData("OWN_AMT", ownPrice);
                result.addData("EXEC_DEPT_CODE", parm.getValue(
                        "EXEC_DEPT_CODE", i));
                result
                        .addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE",
                                i));
                result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
            }

        }
        return result;
    }

    /**
     * ���������õ�����
     * 
     * @param i
     *            int ����
     * @return String
     */
    public String getColName(int i, TTable tbl) {
        String colName = "";
        colName = tbl.getParmMap(i);
        return colName;
    }

    /**
     * �޸�ִ�����ں�ʱ�䣨Ϊ������ҽ����
     * 
     * @param com
     *            Component
     * @param row
     *            int
     * @param column
     *            int (NS_EXEC_DATE;NS_EXEC_DATE_TIME)
     */
    public void onDateTime(TTableNode node) {
        int col = node.getColumn();
        String colName = getColName(col, masterTbl);
//        int row = masterTbl.getSelectedRow();
        int row = node.getRow();//wanglong modify 20150128
        TParm rowParm = masterTbl.getParmValue().getRow(row);
        Map map = new HashMap();
        if ("NS_EXEC_DATE".equals(colName)) {
            Timestamp temp = (Timestamp) node.getValue();
            Timestamp date = temp;
            node.setValue(date);
            if (isLinkOrder(rowParm)) {
                Timestamp tempT = (Timestamp) node.getValue();
                String linkstr = rowParm.getValue("CASE_NO")
                        + rowParm.getValue("DSPN_KIND")
                        + rowParm.getValue("LINK_NO");
                map.put(linkstr, linkstr);
                int count = masterTbl.getParmValue().getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = masterTbl.getParmValue().getRow(i);
                    String str = parm.getValue("CASE_NO")
                            + parm.getValue("DSPN_KIND")
                            + parm.getValue("LINK_NO");
                    if (map.get(str) != null) {
                        masterTbl.setItem(i, "NS_EXEC_DATE", tempT);
                    }
                }
            }
        }
        if ("NS_EXEC_DATE_TIME".equals(colName)) {
            String temp = (String) node.getValue();
            if (temp.length() > 5 || temp.length() < 4) {
                this.messageBox("ʱ�䳤�ȴ���");
                node.setValue(node.getOldValue());
                return;
            } else {
                String execDate = "" + masterTbl.getValueAt(row, 20); //modify by wukai 20160601��18Ϊ20
                String execTime = "" + temp;
                if (temp.length() == 5) {
                    Timestamp checkDateTime = StringTool.getTimestamp(execDate
                            .substring(0, 10).replaceAll("/", "").replaceAll(
                                    "-", "")
                            + " " + execTime.substring(0, 5) + ":00",
                            "yyyyMMdd HH:mm:ss");
                    if (checkDateTime == null) {
                        this.messageBox("ʱ���ʽ����");
                        node.setValue(node.getOldValue());
                        return;
                    }
                    Pattern pattern = Pattern
                            .compile("((0[0-9])|(1[0-9])|(2[0-3])):([0-5][0-9])");
                    if (!pattern.matcher(execTime).matches()) {
                        this.messageBox("ʱ����ֵ����");
                        node.setValue(node.getOldValue());
                        return;
                    }
                } else if (temp.length() == 4) {
                    Timestamp checkDateTime = StringTool.getTimestamp(execDate
                            .substring(0, 10).replaceAll("/", "").replaceAll(
                                    "-", "")
                            + " " + execTime.substring(0, 4) + "00",
                            "yyyyMMdd HHmmss");
                    if (checkDateTime == null) {
                        this.messageBox("ʱ���ʽ����");
                        node.setValue(node.getOldValue());
                        return;
                    }
                    Pattern pattern = Pattern
                            .compile("((0[0-9])|(1[0-9])|(2[0-3]))([0-5][0-9])");
                    if (!pattern.matcher(execTime).matches()) {
                        this.messageBox("ʱ����ֵ����");
                        node.setValue(node.getOldValue());
                        return;
                    }
                }
            }
            String date = temp;
            node.setValue(date);
            if (isLinkOrder(rowParm)) {
                String tempT = (String) node.getValue();
                String linkstr = rowParm.getValue("CASE_NO")
                        + rowParm.getValue("DSPN_KIND")
                        + rowParm.getValue("LINK_NO");
                map.put(linkstr, linkstr);
                int count = masterTbl.getParmValue().getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = masterTbl.getParmValue().getRow(i);
                    String str = parm.getValue("CASE_NO")
                            + parm.getValue("DSPN_KIND")
                            + parm.getValue("LINK_NO");
                    if (map.get(str) != null) {
                        masterTbl.setItem(i, "NS_EXEC_DATE_TIME", tempT);
                    }
                }
            }
        }
    }

    /**
     * �Ƿ�������ҽ��
     * 
     * @param linkOrder
     *            TParm
     * @return boolean
     */
    public boolean isLinkOrder(TParm linkOrder) {
        boolean falg = false;
        if (linkOrder.getInt("LINK_NO") > 0) {
            falg = true;
        }
        return falg;
    }

    /**
     * ��ʼ��ʱ�õ����пؼ�����
     */
    public void myInitControler() {
        CLP = (TComboBox) this.getComponent("CLP");
        from_Date = (TTextFormat) this.getComponent("from_Date");
        to_Date = (TTextFormat) this.getComponent("to_Date");
        from_Time = (TTextField) this.getComponent("from_Time");
        to_Time = (TTextField) this.getComponent("to_Time");

        // �õ�table�ؼ�
        masterTbl = (TTable) this.getComponent("masterTable");

        detailTbl = (TTable) this.getComponent("detailTable");

        firstDateRadio = (TRadioButton) this.getComponent("firstDateRadio");// add by wanglong 20130626
        secondDateRadio = (TRadioButton) this.getComponent("secondDateRadio");
        
        // �õ���ѯ����UI�Ķ���
        ord1All = (TRadioButton) this.getComponent("ord1All");
        ord1ST = (TRadioButton) this.getComponent("ord1ST");
        ord1UD = (TRadioButton) this.getComponent("ord1UD");
        ord1DS = (TRadioButton) this.getComponent("ord1DS");
        ord1IG = (TRadioButton) this.getComponent("ord1IG");

        ord2All = (TRadioButton) this.getComponent("ord2All");
        ord2PHA = (TRadioButton) this.getComponent("ord2PHA");
        ord2PL = (TRadioButton) this.getComponent("ord2PL");
        ord2ENT = (TRadioButton) this.getComponent("ord2ENT");

        typeO = (TCheckBox) this.getComponent("typeO");
        typeE = (TCheckBox) this.getComponent("typeE");
        typeI = (TCheckBox) this.getComponent("typeI");
        typeF = (TCheckBox) this.getComponent("typeF");

        checkAll = (TRadioButton) this.getComponent("checkAll");
        checkYES = (TRadioButton) this.getComponent("checkYES");
        checkNO = (TRadioButton) this.getComponent("checkNO");
        // �õ�ȫȫ��ִ�пؼ�
        exeAll = (TCheckBox) this.getComponent("exeALL");
        printAll = (TCheckBox) this.getComponent("printAll");

        mp1 = (TMovePane) callFunction("UI|MovePane_1|getThis");
        mp2 = (TMovePane) callFunction("UI|MovePane_2|getThis");
        mp3 = (TMovePane) callFunction("UI|MovePane_3|getThis");

        // �õ�ʱ��ؼ�����

        // ������tableע�ᵥ���¼�����
        this.callFunction("UI|masterTable|addEventListener", "masterTable->"
                + TTableEvent.CLICKED, this, "onMasterTableClicked");
        // ������tableע��CHECK_BOX_CLICKED��������¼�
        this.callFunction("UI|masterTable|addEventListener",
                TTableEvent.CHECK_BOX_CLICKED, this,
                "onMasterTableCheckBoxChangeValue");
        // ������tableע��CHECK_BOX_CLICKED��������¼�
        this.callFunction("UI|detailTable|addEventListener",
                TTableEvent.CHECK_BOX_CLICKED, this,
                "onDetailTableCheckBoxChangeValue");
        masterTbl.addEventListener(masterTbl.getTag() + "->"
                + TTableEvent.CHANGE_VALUE, this, "onDateTime");
        // ��Ӧ�ĵĳ�ʼ������
        initDateTime();
        initPanel();
    }

    /**
     * ��ʼ��ʱ��ؼ�
     */
    public void initDateTime() {
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // �ý����00��00��ʼ����ʼʱ��
        from_Date.setValue(date);
        from_Time.setValue("00:00");
        String dateStr = StringTool.getString(date, "yyyy/MM/dd");
        this.setValue("ORDER_START_DATETIME", dateStr + " 00:00");// add by wanglong 20130626
        // ���´ΰ�ҩʱ���ʼ��
        List dispenseDttm = new ArrayList();
        try {
            dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ���ݵ�ǰʱ���õ������ҩʱ��㣨�����ҩʱ�䣬�´ΰ�ҩʱ�䣩
        // this.messageBox_(dispenseDttm);
        if (dispenseDttm.size() != 0) {
            String disDateTime = (String) dispenseDttm.get(0);
            to_Date.setValue(disDateTime.substring(0, 4) + "/"
                    + disDateTime.substring(4, 6) + "/"
                    + disDateTime.substring(6, 8));
            to_Time.setValue(disDateTime.substring(8, 10) + ":"
                    + disDateTime.substring(10));
            this.setValue("ORDER_END_DATETIME",// add by wanglong 20130626
                          disDateTime.substring(0, 4) + "/" + disDateTime.substring(4, 6) + "/"
                                  + disDateTime.substring(6, 8) + " "
                                  + disDateTime.substring(8, 10) + ":" + disDateTime.substring(10));
        } else {
            // ���û�з�
            to_Date.setValue(date);
            to_Time.setValue("00:00");
            this.setValue("ORDER_END_DATETIME", dateStr + " 00:00");// add by wanglong 20130626
        }
    }

    /**
     * ���table�ϵĵ�������
     */
    public void onRemoveTbl() {
        masterTbl.setParmValue(new TParm());
        detailTbl.setParmValue(new TParm());
        exeAll.setSelected(false);
        // onQuery();
    }

    /**
     * ��ն���
     */
    public void onClear() {
        // �ָ�״̬
		callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107
        firstDateRadio.setSelected(true); // ʱ����� add by wanglong 20130626
        ord1All.setSelected(true); // ҽ�����
        ord2All.setSelected(true); // ҽ������
        checkNO.setSelected(true); // ���״̬
        typeO.setSelected(false);
        typeE.setSelected(false);
        typeI.setSelected(false);
        typeF.setSelected(false);
        // �༭״̬
        typeO.setEnabled(false);
        typeE.setEnabled(false);
        typeI.setEnabled(false);
        typeF.setEnabled(false);
        onRemoveTbl();
        clearQueryCondition();
        if (saveFlg)
            this.setCaseNo("");
    }

    public void clearQueryCondition() {
        setValue("INW_DEPT_CODE", "");
        setValue("BED_NO", "");
        setValue("IPD_NO", "");
        setValue("MR_NO", "");
        setValue("PAT_NAME", "");
        setValue("SEX", "");
        setValue("SERVICE_LEVELIN", "");
        setValue("WEIGHT", "");
        setValue("INW_VC_CODE", "");
        setValue("ADM_DATE", "");
        setValue("TOTAL_AMT", "");
        setValue("PAY_INS", "");
        setValue("YJJ_PRICE", "");
        setValue("GREED_PRICE", "");
        setValue("YJYE_PRICE", "");
        setValue("PRESON_NUM", "");
    }

    /**
     * ��������Ƽ۴���
     */
    public void onCharge(Object isDbClick) {
        // this.messageBox("come in.");
        boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
        // �õ�ѡ�е�����
        /**
         * int selRow = masterTbl.getSelectedRow(); if (selRow < 0) { //
         * messageBox("��ѡ�񲡻�ҽ��"); // return; outsideParm.setData("INWEXE",
         * "CASE_NO", getCaseNo()); outsideParm.setData("INWEXE", "ORDER_NO",
         * ""); outsideParm.setData("INWEXE", "ORDER_SEQ", ""); } else { TParm
         * tableParm = masterTbl.getParmValue(); // ȡ����Ҫ����IBS�Ĳ��� String caseNo =
         * tableParm.getValue("CASE_NO", selRow); String orderNo =
         * tableParm.getValue("ORDER_NO", selRow); String orderSeq =
         * tableParm.getValue("ORDER_SEQ", selRow); // ����IBS������
         * outsideParm.setData("INWEXE", "CASE_NO", caseNo);
         * outsideParm.setData("INWEXE", "ORDER_NO", orderNo);
         * outsideParm.setData("INWEXE", "ORDER_SEQ", orderSeq); }
         **/
        TParm ibsParm = new TParm();
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        String execDeptCode_ = "";
        String vsDrCode_ = "";

        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            // System.out.println("==tableParm=="+tableParm);
            // ȡ����Ҫ����SUM�Ĳ���
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);
            execDeptCode_ = tableParm.getValue("ORDER_DEPT_CODE", selRow);//zhangp �ɱ������޸�Ϊ���ң�����ƷѲ����ٴ�
            // System.out.println("===execDeptCode_==="+execDeptCode_);
            vsDrCode_ = tableParm.getValue("VS_DR_CODE", selRow);
            // System.out.println("===vsDrCode_==="+vsDrCode_);

        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
            execDeptCode_ = Operator.getDept();
            // this.getValue("");
            vsDrCode_ = this.getValueString("VC_CODE");
            // System.out.println("===vsDrCode_==="+vsDrCode_);
            // outsideParm.getValue("")
            // this.getValue("");
            TParm parm = new TParm();
            parm.setData("CASE_NO", caseNo_);
            parm = ADMTool.getInstance().getADM_INFO(parm);
            bedNo_ = parm.getValue("BED_NO", 0);
            // System.out.println("===bedNo_==="+bedNo_);
            // outsideParm.getValue(name)

        }
  //   yanmm
        // ��ѡ���ٵ��ý���
        if (!dbClickFlg) {
            // outsideParm.setData("IBS", "TYPE", "INW");
        	 ibsParm.setData("IBS", "INWLEAD_FLG", false);
        	 ibsParm.setData("IBS", "SCHD_CODE", schdCode);//�ٴ�·��ʱ��
            ibsParm.setData("IBS", "CASE_NO", caseNo_);
            ibsParm.setData("IBS", "IPD_NO", ipdNo_);
            ibsParm.setData("IBS", "MR_NO", mrNo_);
            ibsParm.setData("IBS", "BED_NO", bedNo_);
            ibsParm.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));//===pangben 2015-8-5 �޸Ŀ������һ�ò�����ǰ����
            ibsParm.setData("IBS", "STATION_CODE", station_);
            ibsParm.setData("IBS", "VS_DR_CODE", vsDrCode_);
            ibsParm.setData("IBS", "TYPE", "INW");
            ibsParm.setData("IBS", "CLNCPATH_CODE", clpCode_);

            openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);  //  �½�����Ʒ�   yanmm
            /*
             * //����MovePane��˫��Ч�� mp1.onDoubleClicked(isCharge); if (!isCharge) {
             * 
             * getTPanel("ChargePanel").addItem("ChargePanel_",
             * "%ROOT%\\config\\ibs\\IBSOrderm.x", outsideParm, false); }
             * isCharge = isCharge ? false : true; //�����ұߵİ�ť
             * mp2.onDoubleClicked(true); mp3.onDoubleClicked(true);
             */
        } else { // ������ţ�˫������ outsideParm
            this.callFunction("UI|ChargePanel_|getINWData", ibsParm);
        }
    }

    // public boolean onMasterTableChangeValue(Object obj){
    // // �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
    // this.messageBox("dasd");
    // TTableNode node = (TTableNode) obj;
    // if (node == null)
    // return true;
    // this.messageBox("dasd1");
    // // ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
    // if (node.getValue().equals(node.getOldValue()))
    // return true;
    // this.messageBox("dasd2");
    // TParm tableParm=masterTbl.getParmValue();
    // // �õ�table�ϵ�parmmap������
    // String columnName = node.getTable().getDataStoreColumnName(
    // node.getColumn());
    // int row=masterTbl.getSelectedRow();
    // this.messageBox("1");
    // if(columnName.equals("NS_EXEC_DATE_TIME")){
    // this.messageBox("2");
    // String NsexeTime=(String)masterTbl.getValueAt(row, 19);
    // if(NsexeTime.length()<8){
    // this.messageBox("���ʱ�䳤�Ȳ���");
    // return false;
    // }else{this.messageBox("3");
    // Timestamp
    // datetime=StringTool.getTimestamp(tableParm.getValue("NS_EXEC_DATE",
    // row)+" "+NsexeTime, "yyyy/MM/dd HH:mm:ss");
    // System.out.println("---------------"+datetime);
    // if(datetime==null){
    // this.messageBox("���ʱ���ʽ����");
    // return false;
    // }
    // }
    //
    // }
    // return true;
    // }
    /**
     * �������µ��ӿ�
     */
    public void onVitalSign() {
        isVitalSign = !isVitalSign;
        TParm sumParm = new TParm();
        // �õ�ѡ�е�����
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            // ȡ����Ҫ����SUM�Ĳ���
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
        }
        if (caseNo_.equals("")) {
            this.messageBox("��ѡ���ˣ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo_);
        parm = ADMTool.getInstance().getADM_INFO(parm);
        bedNo_ = parm.getValue("BED_NO", 0);
        sumParm.setData("SUM", "CASE_NO", caseNo_);
        sumParm.setData("SUM", "MR_NO", mrNo_);
        sumParm.setData("SUM", "IPD_NO", ipdNo_);
        sumParm.setData("SUM", "STATION_CODE", station_);
        sumParm.setData("SUM", "BED_NO", bedNo_);
        sumParm.setData("SUM", "ADM_TYPE", "I");
        getTPanel("second").setVisible(false);
        getTPanel("first").addItem("first_",
                "%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm, false);
    }

    /**
     * �������������µ�
     * 
     * @param name
     *            String
     * @return TPanel
     */
    public void onNewArrival() {
        isVitalSign = !isVitalSign;
        TParm sumParm = new TParm();
        // �õ�ѡ�е�����
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            // ȡ����Ҫ����SUM�Ĳ���
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);
        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
        }
        if (caseNo_.equals("")) {
            this.messageBox("��ѡ���ˣ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo_);
        parm = ADMTool.getInstance().getADM_INFO(parm);
        bedNo_ = parm.getValue("BED_NO", 0);
        sumParm.setData("SUM", "CASE_NO", caseNo_);
        sumParm.setData("SUM", "MR_NO", mrNo_);
        sumParm.setData("SUM", "IPD_NO", ipdNo_);
        sumParm.setData("SUM", "STATION_CODE", station_);
        sumParm.setData("SUM", "BED_NO", bedNo_);
        sumParm.setData("SUM", "ADM_TYPE", "I");
        getTPanel("second").setVisible(false);
        getTPanel("first").addItem("first_",
                "%ROOT%\\config\\sum\\SUMNewArrival.x", sumParm, false);
    }

    /**
     * table�ϵ�checkBoxע�����
     * 
     * @param obj
     *            Object
     */
    public void onDetailTableCheckBoxChangeValue(Object obj) {
        // ��õ����table����
        TTable detailTable = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        detailTable.acceptText();

    }

    public void selDOSE(Object flg) {
        boolean temp = TypeTool.getBoolean(flg);
        // ���ѡ��
        typeO.setSelected(temp);
        typeE.setSelected(temp);
        typeI.setSelected(temp);
        typeF.setSelected(temp);
        // �༭״̬
        typeO.setEnabled(temp);
        typeE.setEnabled(temp);
        typeI.setEnabled(temp);
        typeF.setEnabled(temp);
        // ���table
        onRemoveTbl();
    }

    /**
     * ���ýṹ������
     */
    public void onEmr() {
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String patName_ = "";
        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            patName_ = tableParm.getValue("PAT_NAME", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            patName_ = this.getPatName();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
        }
        if (caseNo_.length() == 0) {
            messageBox("��ѡ���ˣ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", caseNo_);// this.getCaseNo());
        parm.setData("PAT_NAME", patName_);// this.getPatName());
        parm.setData("MR_NO", mrNo_);// this.getMrNo());
        parm.setData("IPD_NO", ipdNo_);// this.getIpdNo());
        parm.setData("ADM_DATE", outsideParm.getData("INW", "ADM_DATE"));
        parm.setData("DEPT_CODE", this.getDeptCode());
        parm.setData("EMR_DATA_LIST", new TParm());
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }

    /**
     * �����¼
     */
    public void onNursingRec() {
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String patName_ = "";
        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            patName_ = tableParm.getValue("PAT_NAME", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            patName_ = this.getPatName();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
        }
        if (caseNo_.length() == 0) {
            messageBox("��ѡ���ˣ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", caseNo_);
        parm.setData("PAT_NAME", patName_);
        parm.setData("MR_NO", mrNo_);
        parm.setData("IPD_NO", ipdNo_);
        // parm.setData("CASE_NO",this.getCaseNo());
        // parm.setData("PAT_NAME",this.getPatName());
        // parm.setData("MR_NO",this.getMrNo());
        // parm.setData("IPD_NO",this.getIpdNo());
        parm.setData("ADM_DATE", outsideParm.getData("INW", "ADM_DATE"));
        parm.setData("DEPT_CODE", this.getDeptCode());
        parm.setData("RULETYPE", "2");
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

    }

    /**
     * ����table�ϵ����ݸ���ȫ��ִ�б���Ƿ�ѡ��
     */
    public boolean updateAllExe() {
        int rowCount = masterTbl.getRowCount();
        if (rowCount != 0) {
            for (int i = 0; i < rowCount; i++) {
                if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 15)))   //modify by wukai 20160601��12Ϊ14  //modify by machao 20170213��14Ϊ15
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * ����Ӧtable���Ƿ�������� ��ÿ�β�ѯ��
     * 
     * @param tab
     *            TTable
     */
    public void existsDateForTabl(TTable tab) {
        if (tab.getRowCount() == 0)
            this.messageBox("û��������ݣ�");
        // ����ȫ��ִ�пؼ�
        exeAll.setSelected(updateAllExe());
    }

    /**
     * ������(ȡ�����)�����ʱ�����Ƿ��Ѿ�ִ��
     * 
     * @return boolean
     */
    public boolean isDisp() {
        // �õ���֤�Ƿ�ҩ�Ĳ���
        TParm ordParm = masterTbl.getParmValue();
        Vector caseNo = ordParm.getVector("CASE_NO");
        Vector orderNo = ordParm.getVector("ORDER_NO");
        Vector orderSeq = ordParm.getVector("ORDER_SEQ");
        String inCaseNo = "";
        String inOrderNo = "";
        String inOrderSeq = "";
        boolean flg1 = true;
        boolean flg2 = true;
        boolean flg3 = true;
        for (int i = 0; i < ordParm.getCount(); i++) {
            // �������ݵ�ʱ��(����ΪƴIN)
            if (caseNo.size() != 0) {
                // ��ǰ���Ѿ������ݲ��Һ��µ�һ������ͬ��ʱ����Ҫ��ǰ������ݺ�Ӷ���(���ö�·����Խ��)
                if (!inCaseNo.equals("")
                        && !((Vector) caseNo.get(i - 1)).get(0).equals(
                                ((Vector) caseNo.get(i)).get(0))) {
                    inCaseNo += ",";
                    flg1 = true;
                }
                if (!inOrderNo.equals("")
                        && !((Vector) orderNo.get(i - 1)).get(0).equals(
                                ((Vector) orderNo.get(i)).get(0))) {
                    inOrderNo += ",";
                    flg2 = true;
                }
                if (!inOrderSeq.equals("")
                        && !((Vector) orderSeq.get(i - 1)).get(0).equals(
                                ((Vector) orderSeq.get(i)).get(0))) {
                    inOrderSeq += ",";
                    flg3 = true;
                }
                inCaseNo += flg1 ? "'" + ((Vector) caseNo.get(i)).get(0) + "'"
                        : "";
                flg1 = false;
                inOrderNo += flg2 ? "'" + ((Vector) orderNo.get(i)).get(0)
                        + "'" : "";
                flg2 = false;
                inOrderSeq += flg3 ? "'" + ((Vector) orderSeq.get(i)).get(0)
                        + "'" : "";
                flg3 = false;
            }
        }
        // ===========pangben modify 20110516 start �������
        String region = "";
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
        }
        // ===========pangben modify 20110516 stop

        String checkSql = "SELECT  ORDER_DESC" + " FROM ODI_DSPNM"
                + " WHERE CASE_NO IN(" + inCaseNo + ")" + " AND ORDER_NO IN("
                + inOrderNo + ")" + " AND ORDER_SEQ IN(" + inOrderSeq + ")"
                + " AND PHA_DISPENSE_CODE IS NOT NULL" + region;// ===========pangben
        // modify
        // 20110516
        // ִ��sql���
        TJDODBTool tool = TJDODBTool.getInstance();
        TParm result = new TParm();
        result = new TParm(tool.select(checkSql));
        // �õ����ص�����(PS:��TJDODBTool���غ��װ��TParmȡ���������ⷽ��)
        int count = result.getInt("SYSTEM", "COUNT");
        if (count == 0) {
            return false;
        }
        String hadExecName = "";
        for (int i = 0; i < count; i++) {
            hadExecName += (result.getValue("ORDER_DESC", i) + "\n");

        }
        this.messageBox(hadExecName + "�Ѿ���ҩ����ȡ��");
        return true;
    }

    /**
     * ��ѡ����ѡ���ӡ
     */
    public void onPrtAll() {
        boolean prt = printAll.isSelected();
        int count = masterTbl.getRowCount();
        for (int i = 0; i < count; i++) {
            //masterTbl.setValueAt(prt, i, 15);  //modify by wukai 20160601 13��15
        	masterTbl.setValueAt(prt, i, 16);  //modify by machao 20160601 15��16
        }
    }

    /**
     * ҽ��ִ�е���ӡ
     */
    public void onPrintOrderExeSheet() {
        if (ord2All.isSelected() || ord1All.isSelected()) {
            messageBox("ȫ��״̬�²��ɴ�ӡҽ��ִ�е�");
            return;
        }
        if ((ord2PHA.isSelected())
                && ((typeO.isSelected() || typeE.isSelected()) && (typeI
                        .isSelected() || typeF.isSelected()))) {
            messageBox("��ȷ��ҩƷ�����ھ�������໹�ǿڷ�������");
            return;
        }
        getPrintPrderExeSheetParm(getCasePrintList());
    }

    /**
     * ҽ��ִ�е���ӡ��Ա�б�
     * 
     * @return TParm
     */
    public TParm getCasePrintList() {
        TParm tableParm = masterTbl.getParmValue();
        int count = masterTbl.getRowCount();
        Map map = new HashMap();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 16));//modify by wukai 20160601��13Ϊ15  //modify by machao 20170213��15Ϊ16
            if (!prtFlg)
                continue;
            if (map.get(tableParm.getValue("CASE_NO", i)) != null)
                continue;
            map.put(tableParm.getValue("CASE_NO", i), tableParm.getValue(
                    "CASE_NO", i));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(
                    " SELECT * " + " FROM SYS_PATINFO A " + " WHERE MR_NO ='"
                            + tableParm.getData("MR_NO", i) + "'"));
            TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
                    " SELECT CHN_DESC " + " FROM SYS_DICTIONARY A "
                            + " WHERE GROUP_ID = 'SYS_SEX' " + " AND  ID = '"
                            + patParm.getData("SEX_CODE", 0) + "'"));
            result.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
            result.addData("DEPT", getDeptDesc(tableParm.getValue("DEPT_CODE",
                    i)));
            result.addData("STATION", getStationDesc(tableParm.getValue(
                    "STATION_CODE", i)));
            result.addData("DATE", from_Date.getText() + " "
                    + from_Time.getText() + "��" + to_Date.getText() + " "
                    + to_Time.getText());
            result.addData("BED", tableParm.getData("BED_NO", i));
            result.addData("MR_NO", tableParm.getData("MR_NO", i));
            result.addData("IPD_NO", tableParm.getData("IPD_NO", i));
            result.addData("NAME", tableParm.getData("PAT_NAME", i));
            result.addData("SEX", sexDescParm.getData("CHN_DESC", 0));
            result.addData("AGE", StringUtil.getInstance().showAge(
                    (Timestamp) patParm.getData("BIRTH_DATE", 0),
                    SystemTool.getInstance().getDate()));
            if (ord2PL.isSelected() || ord2ENT.isSelected())
                result.addData("TITLE", "��������ҽ��ִ�е�");
            if (ord2PHA.isSelected()) {
                if (typeO.isSelected() || typeE.isSelected())
                    result.addData("TITLE", "�ڷ�����ҽ��ִ�е�");
                if (typeI.isSelected() || typeF.isSelected())
                    result.addData("TITLE", "����ע��ҽ��ִ�е�");
            }
        }
        result.setCount(result.getCount("CASE_NO"));
        if (result.getCount() <= 0)
            return null;
        return result;
    }

    /**
     * ��ӡҽ��ִ�е�
     * 
     * @param casePrintList
     *            TParm
     * @return TParm
     */
    public TParm getPrintPrderExeSheetParm(TParm casePrintList) {
        if (casePrintList == null) {
            messageBox("�����ӡ����");
            return null;
        }
        TParm result = new TParm();
        TParm tableParm = setOrderDescLength();
        int count = tableParm.getCount();
        //add by wukai on 20170209
        String parpareFlg = null;
        for (int i = 0; i < casePrintList.getCount(); i++) {
            String caseNo = casePrintList.getValue("CASE_NO", i);
            int index = 1;
            int page = 1;
            int pageRow = 15;
            for (int j = 0; j < count; j++) {
                if (!caseNo.equals(tableParm.getValue("CASE_NO", j)))
                    continue;
                if ((index - 1) % pageRow == 0)
                    index = 1;
                if (index == 1) {
                    cloneParmVector(casePrintList, result, i);
                    result.addData("PAGE", page);
                    page++;
                }
                result.addData("ORDER" + index, tableParm.getData(
                        "ORDER_DESC_AND_SPECIFICATION", j));
                result.addData("QTY" + index, numDot(tableParm.getDouble(
                        "MEDI_QTY", j))
                        + " " + getUnit(tableParm.getValue("MEDI_UNIT", j)));
                result.addData("ROUTE" + index, getRouteDesc(tableParm
                        .getValue("ROUTE_CODE", j)));
                result.addData("FREQ" + index, tableParm.getValue("FREQ_CODE",
                        j));
                result.addData("TIME" + index, getTimes(tableParm, j));
                //=== add by wukai 
                parpareFlg =  tableParm.getValue("DISPENSE_FLG", j);
                if(parpareFlg == null || "".equals(parpareFlg)) {
                	 result.addData("PREPARE" + index, "");
                } else {
                	 result.addData("PREPARE" + index, "Y".equals(parpareFlg)?"��":"��");
                }
                //add by chenjianxing 20180131
                //the start
                //��Ŀ�ţ�6237 ҽ����ӡ��ȱ�ٱ��뷽ʽ����Һ���� 
                result.addData("PUMP" + index, getPumpDesc(tableParm.getValue("PUMP_CODE",j)));
                result.addData("RATE" + index, (tableParm.getDouble("INFLUTION_RATE", j)==0) ? "" : String.format("%.3f", tableParm.getDouble("INFLUTION_RATE", j)));
                //the end
                index++;
            }
            for (int j = index; j <= pageRow; j++) {
                result.addData("ORDER" + j, "");
                result.addData("QTY" + j, "");
                result.addData("ROUTE" + j, "");
                result.addData("FREQ" + j, "");
                result.addData("TIME" + j, "");
                //=== add by wukai 
                result.addData("PREPARE" + j,  "");
                //add by chenjianxing 20180131
                //the start
                //��Ŀ�ţ�6237 ҽ����ӡ��ȱ�ٱ��뷽ʽ����Һ���� 
                result.addData("PUMP" + j, "");
                result.addData("RATE" + j, "");
                //the end
            }
        }
        result.setCount(result.getCount("CASE_NO"));
        transportParmReport(result);
        return result;
    }

    private String numDot(double medQty) {
        if (medQty == 0)
            return "";
        if ((int) medQty == medQty)
            return "" + (int) medQty;
        else
            return "" + medQty;
    }

    private String getTimes(TParm parm, int index) {
        if (parm.getValue("FREQ_CODE", index).length() == 0)
            return "";
        TParm result = new TParm(TJDODBTool.getInstance().select(
                " SELECT ORDER_DATETIME,DC_DATE " + " FROM ODI_DSPND "
                        + " WHERE CASE_NO = '" + parm.getData("CASE_NO", index)
                        + "'" + " AND   ORDER_NO = '"
                        + parm.getData("ORDER_NO", index) + "'"
                        + " AND   ORDER_SEQ = '"
                        + parm.getData("ORDER_SEQ", index) + "'"
                        + " AND   ORDER_DATE||ORDER_DATETIME "
                        + "       BETWEEN '"
                        + parm.getData("START_DTTM", index) + "' "
                        + "       AND '" + parm.getData("END_DTTM", index)
                        + "'"));
        String times = "";
        for (int i = 0; i < result.getCount() && i <= 8; i++) {
            if (!result.getValue("DC_DATE", i).equals(""))
                continue;
            times += "     "
                    + result.getValue("ORDER_DATETIME", i).substring(0, 2)
                    + ":"
                    + result.getValue("ORDER_DATETIME", i).substring(2, 4);
        }
        return times;
    }
    /**
	 * Ƥ�Խ��
	 */
	public void onSkiResult() {
		TParm parm = new TParm();
		TParm result = new TParm();
		// ��ѯѡ��ҩ���Ƿ�ΪƤ��ҩƷ
		String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE),"
				+ "B.BATCH_NO,B.SKINTEST_NOTE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,B.MR_NO "
				+ " FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = '"
				+ orderCodeSki
				+ "' AND B.CASE_NO = '"
				+ caseNo
				+ "' "
				+ "GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,B.MR_NO "
				+ "ORDER BY B.OPT_DATE DESC";
		// System.out.println("Ƥ��ҩƷ sql is����"+sql);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getCount() <= 0) {
			this.messageBox("��ҩƷ�����ڡ�");
			return;
		} else if (result1.getValue("SKINTEST_FLG", 0).equals("N")) {
			this.messageBox("��Ƥ��ҩƷ��");
			return;
		} else if (result1.getValue("BATCH_NO", 0).equals(null)
				|| "".equals(result1.getValue("BATCH_NO", 0))) {
			parm.setData("BATCH_NO", "");// ����
			parm.setData("SKINTEST_NOTE", "");// Ƥ�Խ��
		} else {
			parm.setData("BATCH_NO", result1.getValue("BATCH_NO", 0));// ����
			parm.setData("SKINTEST_FLG", result1.getValue("SKINTEST_NOTE", 0));// Ƥ�Խ��
		}
		// //��ѯ�����Ƥ�Խ����Ƥ������
		// String skiNoSql = "SELECT MAX(OPT_DATE),BATCH_NO,SKINTEST_NOTE " +
		// "FROM PHA_ANTI WHERE CASE_NO= '"+caseNo+"' AND ORDER_CODE= '"+orderCodeSki+"' "
		// +
		// "AND  BATCH_NO IS NOT NULL " +
		// "GROUP BY BATCH_NO ,SKINTEST_NOTE,OPT_DATE " +
		// "ORDER BY OPT_DATE";
		// // System.out.println("Ƥ�Բ�ѯ��䣺"+skiNoSql);
		// TParm parm = new TParm(TJDODBTool.getInstance().select(skiNoSql));
		// if(parm.getCount()<=0){
		// parm.setData("BATCH_NO", "11");//����
		// parm.setData("SKINTEST_NOTE", "22");//Ƥ�Խ��
		// }
		// parm.setData("BATCH_NO", parm.getValue("BATCH_NO", 0));//����
		// parm.setData("SKINTEST_NOTE", parm.getValue("SKINTEST_NOTE",
		// 0));//Ƥ�Խ��
		parm.setData("CASE_NO", caseNo);// �����
		parm.setData("ORDER_CODE", orderCodeSki);// ҽ������
		parm.setData("ORDER_NO", orderNoSki);//
		parm.setData("SEQ_NO", orderSeqSki);//
		parm.setData("OPT_USER", Operator.getID());//
		parm.setData("OPT_TERM", Operator.getIP());//
		result = (TParm) this.openDialog("%ROOT%\\config\\inw\\INWSkiResult.x",
				parm, true);
		// System.out.println("Ƥ�Խ���ش���"+result);
		TParm XMLParm = new TParm();
		if (null != result) {
			String psResult = "";
			if (result.getValue("SKINTEST_NOTE", 0).equals("0")) {
				psResult ="(-)����" ;
				XMLParm.setData("SkinTestResult","(-)����");
			} else if (result.getValue("SKINTEST_NOTE", 0).equals("1")) {
				psResult ="(+)����" ;
				XMLParm.setData("SkinTestResult","(+)����");
			}
			skiResult = "Ƥ�Խ����" + psResult + ";  ����:"
					+ result.getValue("BATCH_NO", 0);
			// add wuxy 20170504
			XMLParm.setData("OrderCode",orderCodeSki);
			String PsDesc = "";
			String str1 = "";
			String str2 = "";
			String str3 = "";
			
			if(StringUtil.isNullString(result1.getValue("ORDER_DESC",0))){
				str1="";
			}else{
				str1 = result1.getValue("ORDER_DESC",0);
			}
			if(StringUtil.isNullString(result1.getValue("GOODS_DESC",0))){
				str2 = "";
			}else{
				str2 = "("+result1.getValue("GOODS_DESC",0)+")";
			}
			if(StringUtil.isNullString(result1.getValue("SPECIFICATION",0))){
				str3 = "";
			}else{
				str3 = "("+result1.getValue("SPECIFICATION",0)+")";
			}
			PsDesc = str1+str2+str3;
			XMLParm.setData("OrderDesc",PsDesc);
			XMLParm.setData("CaseNo",caseNo);
			XMLParm.setData("MrNo",result1.getValue("MR_NO", 0));
			XMLParm.setData("SkinTestBatchNo",result.getValue("BATCH_NO", 0));
	        // �������ӿ� wanglong add 20150527
	        TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo, psResult);
			TParm xmlParm1 = ADMXMLTool.getInstance().creatPsXMLFile(XMLParm);
	        if (xmlParm.getErrCode() < 0) {
	            this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
	        }
		}
		for (int j = 0; j < detailTbl.getRowCount(); j++) {
			detailTbl.setItem(j, "EXEC_NOTE", skiResult);
		}
	}
    /**
     * ����TParm��Vector����
     * 
     * @param fromParm
     *            TParm
     * @param toParm
     *            TParm
     * @param index
     *            int
     */
    private void cloneParmVector(TParm fromParm, TParm toParm, int index) {
        String[] names = fromParm.getNames();
        for (int i = 0; i < names.length; i++) {
            if (fromParm.getData(names[i]) instanceof String)
                continue;
            toParm.addData(names[i], fromParm.getData(names[i], index));
        }
    }

    private String getDesc(TParm parm) {
        // if(parm.getValue("ORDER_CODE").startsWith("ZZZ")){
        // ===========pangben modify 20110516 start �������
        String region = "";
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
        }
        // ===========pangben modify 20110516 stop

        TParm result = new TParm(
                TJDODBTool
                        .getInstance()
                        .select(
                                " SELECT ORDER_DESC || "
                                        + "        CASE WHEN GOODS_DESC IS NULL THEN '' ElSE ('(' || GOODS_DESC || ')') END ||"
                                        + "        CASE WHEN SPECIFICATION IS NULL THEN '' ELSE ('(' || SPECIFICATION || ')') END||"
                                        + "        CASE WHEN DR_NOTE IS NULL THEN '' ELSE ('(' || DR_NOTE || ')') END DESCALL"
                                        + " FROM ODI_ORDER"
                                        + " WHERE  CASE_NO = '"
                                        + parm.getValue("CASE_NO") + "'"
                                        + " AND    ORDER_NO = '"
                                        + parm.getValue("ORDER_NO") + "'"
                                        + " AND    ORDER_SEQ = '"
                                        + parm.getValue("ORDER_SEQ") + "'"
                                        + region));// ===========pangben
        // modify
        // 20110516
        return result.getValue("DESCALL", 0);
        // }
        // else
        // return parm.getValue("ORDER_DESC_AND_SPECIFICATION");
    }

    /**
     * ҽ�����ƻ���
     * 
     * @return TParm
     */
    private TParm setOrderDescLength() {
        TParm result = new TParm();
        TParm tableParm = masterTbl.getParmValue();
        int count = masterTbl.getRowCount();
        for (int i = 0; i < count; i++) {
            boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 16));//15���16  modify machao
            if (!prtFlg)
                continue;
            // String orderDesc =
            // tableParm.getValue("ORDER_DESC_AND_SPECIFICATION",i);
            String orderDesc = getDesc(tableParm.getRow(i));
            String desc[] = breakNFixRow(orderDesc, 30, 1);
            for (int k = 0; k < desc.length; k++) {
                if (k == 0) {
                    result.addData("ORDER_DESC_AND_SPECIFICATION", desc[k]);
                    cloneParm(result, tableParm, i);
                    continue;
                }
                result.addData("ORDER_DESC_AND_SPECIFICATION", desc[k]);
                cloneParm(result, tableParm, i);
                setNull(result, result.getCount("CASE_NO") - 1);
            }
        }
        result.setCount(result.getCount("CASE_NO"));
        return result;
    }

    /**
     * ����TParmҽ����Ϣ��ҽ��������
     * 
     * @param result
     *            TParm
     * @param parm
     *            TParm
     * @param index
     *            int
     * @return TParm
     */
    private TParm cloneParm(TParm result, TParm parm, int index) {
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("ORDER_DESC_AND_SPECIFICATION"))
                continue;
            result.addData(names[i], parm.getData(names[i], index));
        }
        return result;
    }

    /**
     * �����ÿ�
     * 
     * @param result
     *            TParm
     * @param index
     *            int
     */
    private void setNull(TParm result, int index) {
        result.setData("MEDI_QTY", index, "");
        result.setData("ROUTE_CODE", index, "");
        result.setData("FREQ_CODE", index, "");
        result.setData("MEDI_UNIT", index, "");
        result.setData("DISPENSE_FLG", index, "");
        //add by chenjianxing 20180131
        //the start
        //��Ŀ�ţ�6237 ҽ����ӡ��ȱ�ٱ��뷽ʽ����Һ���� 
        result.setData("PUMP_CODE", index, "");
        result.setData("INFLUTION_RATE", index, "");
        //the end
    }

    /**
     * ���ʹ�ӡ����
     * 
     * @param parm
     *            TParm
     */
    private void transportParmReport(TParm parm) {
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++)
            parm.addData("SYSTEM", "COLUMNS", names[i]);
        for (int index = 0; index < parm.getCount(); index++) {
            String[] Pnames = parm.getNames();
            TParm printParm = new TParm();
            TParm tParm = new TParm();
            for (String col : names) {
                tParm.addData(col, parm.getValue(col, index));
            }
            tParm.setCount(1);
            printParm.setData("TABLE", tParm.getData());
            openPrintWindow("%ROOT%\\config\\prt\\inw\\INWOrderExeSheet.jhw",
                    printParm, true);
        }
    }

    /**
     * ȡ�ÿ�������
     * 
     * @param deptCode
     *            String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT DEPT_CHN_DESC " + " FROM SYS_DEPT "
                        + " WHERE DEPT_CODE = '" + deptCode + "'"));
        return parm.getValue("DEPT_CHN_DESC", 0);
    }

    /**
     * ȡ��Ƶ��ʱ���
     * 
     * @param freqCode
     *            String
     * @return String
     */
    public String getDescription(String freqCode) {
        if (freqCode.length() == 0)
            return "";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT DESCRIPTION " + " FROM SYS_PHAFREQ "
                        + " WHERE FREQ_CODE = '" + freqCode + "'"));
        return parm.getValue("DESCRIPTION", 0);
    }

    /**
     * ȡ��ҩƷ��λ����
     * 
     * @param code
     *            String
     * @return String
     */
    public String getUnit(String code) {
        if (code.length() == 0)
            return "";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT "
                        + " WHERE UNIT_CODE = '" + code + "'"));
        return parm.getValue("UNIT_CHN_DESC", 0);
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (true) {
            onPrintOrderExeSheet();
            return;
        }
        if (ord2All.isSelected()) {
            messageBox("ȫ��״̬�²��ɴ�ӡҽ��ִ�е�");
            return;
        }
        TParm selectData = getPrintParm();
        TParm printParm = new TParm();
        // �ж�ִ�����ִ�ӡ��
        if (typeO.isSelected()) { // �ڷ���
            printParm = arrangeData(selectData, "O");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
                    "SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
                            + this.getValueString("STATION_CODE") + "'"));
            data.setData("STATION", "TEXT", (String) stationDesc.getData(
                    "STATION_DESC", 0));
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
                    + from_Time.getText() + "��" + to_Date.getText() + " "
                    + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            setNewDate(data);
            this.openPrintWindow(
                    "%ROOT%\\config\\prt\\inw\\INWExecOrderPrt_O.jhw", data);
        } else if (typeI.isSelected()) { // ע��
            printParm = arrangeData(selectData, "I");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
                    "SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
                            + this.getValueString("STATION_CODE") + "'"));
            data.setData("STATION", "TEXT", (String) stationDesc.getData(
                    "STATION_DESC", 0));
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
                    + from_Time.getText() + "��" + to_Date.getText() + " "
                    + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            setNewDate(data);
            this.openPrintWindow(
                    "%ROOT%\\config\\prt\\inw\\INWExecOrderPrt_I.jhw", data);
        } else if (typeF.isSelected()) { // ��Һ
            printParm = arrangeData(selectData, "F");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
                    "SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
                            + this.getValueString("STATION_CODE") + "'"));
            // ����
            data.setData("STATION", "TEXT", (String) stationDesc.getData(
                    "STATION_DESC", 0));
            // ͳ��ʱ��
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
                    + from_Time.getText() + "��" + to_Date.getText() + " "
                    + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            setNewDate(data);
            this.openPrintWindow(
                    "%ROOT%\\config\\prt\\inw\\INWExecOrderPrt_F.jhw", data);
        } else { // ��ִͨ�е�
            printParm = arrangeData(selectData, "COM");
            printParm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
            printParm.addData("SYSTEM", "COLUMNS", "BED_NO");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
                    "SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
                            + this.getValueString("STATION_CODE") + "'"));
            TParm data = new TParm();
            // ����
            data.setData("STATION", "TEXT", (String) stationDesc.getData(
                    "STATION_DESC", 0));
            data.setData("RX_TYPE", "TEXT", "��ͨ");
            // ͳ��ʱ��
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
                    + from_Time.getText() + "��" + to_Date.getText() + " "
                    + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            setNewDate(data);
            this.openPrintWindow(
                    "%ROOT%\\config\\prt\\inw\\INWExecSheetPrt.jhw", data);
        }
    }

    private void setNewDate(TParm parm) {
        parm.setData("DEPT", "TEXT", getDeptDesc(getDeptCode()));
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", getCaseNo());
        admParm = ADMTool.getInstance().getADM_INFO(admParm);
        parm.setData("IPD_NO", "TEXT", admParm.getData("IPD_NO", 0));
        parm.setData("MR_NO", "TEXT", admParm.getData("MR_NO", 0));
        parm.setData("BED_NO", "TEXT", admParm.getData("BED_NO", 0));
        TParm sexParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT * FROM SYS_PATINFO A WHERE MR_NO ='" + getMrNo() + "'"));
        parm.setData("PAT_NAME", "TEXT", sexParm.getData("PAT_NAME", 0));
        TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT CHN_DESC FROM SYS_DICTIONARY A WHERE GROUP_ID = 'SYS_SEX' AND  ID = '"
                        + sexParm.getData("SEX_CODE", 0) + "'"));
        parm.setData("SEX", "TEXT", sexDescParm.getData("CHN_DESC", 0));
        parm.setData("AGE", "TEXT", StringUtil.getInstance().showAge(
                (Timestamp) sexParm.getData("BIRTH_DATE", 0),
                SystemTool.getInstance().getDate()));
        parm.setData("PRT_DATE", "TEXT", StringTool.getString(SystemTool
                .getInstance().getDate(), "yyyy-MM-dd HH:mm:ss"));
        parm.setData("PRT_USER", "TEXT", Operator.getName());
    }

    /**
     * �ж��Ƿ�������ҽ��
     * 
     * @return boolean
     */
    private boolean ifLinkOrder(TParm oneOrder) {
        String LinkNo = (String) oneOrder.getData("LINK_NO");
        if (LinkNo == null || LinkNo.length() == 0)
            return false;
        return true;
    }

    /**
     * �ж��Ƿ�������ҽ������
     * 
     * @return boolean
     */
    private boolean ifLinkOrderSubItem(TParm oneOrder) {
        return !TypeTool.getBoolean(oneOrder.getData("LINKMAIN_FLG"));

    }

    /**
     * ��������
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm, String flg) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            // �ж�����ҽ��
            if (ifLinkOrder(order)) {
                // ���Ϊ����ҽ��ϸ�����账��
                if (ifLinkOrderSubItem(order))
                    continue;
                String finalOrder = getLinkOrder(order, parm);
                String medi = getLinkQty(order, parm);
                result.addData("ORDER_DESC", finalOrder);
                result.addData("MEDI_QTY", medi);
            } else { // ��ͨҽ��
                String drNote = (String) order.getData("DR_NOTE");
                String desc = (String) order.getData("ORDER_DESC");
                // �ж��Ƿ���ҽ����ע
                if (ifZ00Order(order)) {
                    desc = drNote;
                    drNote = "";
                }
                String finalDesc = "" + desc;
                // ��Ҫ����--ҽ��
                result.addData("ORDER_DESC", finalDesc);
                result.addData("MEDI_QTY", "" + order.getData("MEDI_QTY"));
            }
            // ����ҽ���������ò�ͬ��������
            if ("O".equals(flg)) {
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
                result.addData("DISPENSE_QTY", order.getData("DISPENSE_QTY"));
            } else if ("I".equals(flg)) { // ע��
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
            } else if ("F".equals(flg)) { // ��Һ
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
                result.addData("DR_NOTE", order.getData("DR_NOTE"));
            } else if ("COM".equals(flg)) {
                result.addData("CASE_NO", "");// order.getData("CASE_NO")==null?"":order.getData("CASE_NO"));
                result.addData("MR_NO", "");// order.getData("MR_NO")==null?"":order.getData("MR_NO"));
                result.addData("BED_NO", "");// order.getData("BED_NO")==null?"":order.getData("BED_NO"));
                // result.addData("MEDI_QTY", order.getData("MEDI_QTY"));
                result.addData("ROUTE_CODE",
                        order.getData("ROUTE_CODE") == null ? "" : order
                                .getData("ROUTE_CODE"));
                result.addData("FREQ_CODE",
                        order.getData("FREQ_CODE") == null ? "" : order
                                .getData("FREQ_CODE"));
                result.addData("NS_EXEC_DATE",
                        order.getData("NS_EXEC_DATE") == null ? "" : order
                                .getData("NS_EXEC_DATE").toString().substring(
                                        5, 16));
                result.addData("DR_NOTE", order.getData("DR_NOTE") == null ? ""
                        : order.getData("DR_NOTE"));
            }
        }
        result.setCount(result.getCount("ORDER_DESC"));
        return result;
    }

    /**
     * �ж�������ҽ����ע
     * 
     * @param parm
     *            TParm
     * @return boolean
     */
    private boolean ifZ00Order(TParm parm) {
        String orderCode = (String) parm.getData("ORDER_CODE");
        return orderCode.startsWith("Z");
    }

    /**
     * ��������ҽ��ORDER_DESC
     * 
     * @param order
     *            TParm
     * @param parm
     *            TParm
     * @return String
     */
    private String getLinkOrder(TParm order, TParm parm) {
        String resultDesc = "";
        String mainOrder = (String) order.getData("ORDER_DESC");
        String mainLinkNo = (String) order.getData("LINK_NO");
        String mainDspnKind = (String) order.getData("DSPN_KIND");
        resultDesc = mainOrder;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = (String) parm.getData("LINK_NO", i);
            String dspnKind = (String) parm.getData("DSPN_KIND", i);
            if (dspnKind.equals(mainDspnKind) && mainLinkNo.equals(linkNo)
                    && !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subOrder = (String) parm.getData("ORDER_DESC", i);
                resultDesc += "\r" + subOrder;
            } else
                continue;
        }
        return resultDesc;
    }

    /**
     * 
     * @param order
     *            TParm
     * @param parm
     *            TParm
     * @return String
     */
    private String getLinkQty(TParm order, TParm parm) {
        String resultString = "";
        String mainMediQty = (String) order.getData("MEDI_QTY");
        String mainLinkNo = (String) order.getData("LINK_NO");
        String mainDspnKind = (String) order.getData("DSPN_KIND");
        resultString = mainMediQty;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = (String) parm.getData("LINK_NO", i);
            String dspnKind = (String) parm.getData("DSPN_KIND", i);
            if (dspnKind.equals(mainDspnKind) && mainLinkNo.equals(linkNo)
                    && !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subMediQty = (String) parm.getData("MEDI_QTY", i);
                resultString += "\r" + subMediQty;
            } else
                continue;
        }
        return resultString;
    }

    /**
     * �õ���ӡ������֧��4�ֱ�����ִͨ�е� ������Һ��
     * 
     * @return TParm
     */
    private TParm getPrintParm() {
        TParm result = new TParm();
        TParm getShowParm = masterTbl.getShowParmValue();
        TParm hideParm = masterTbl.getParmValue();
        int count = masterTbl.getRowCount();
        for (int i = 0; i < count; i++) {
            boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 16));//modify machao 15���16
            if (prtFlg) {
                result.addData("CASE_NO", hideParm.getData("CASE_NO", i));
                result.addData("MR_NO", hideParm.getData("MR_NO", i));
                result.addData("BED_NO", hideParm.getData("BED_NO", i));

                result.addData("ORDER_CODE", hideParm.getData("ORDER_CODE", i));
                result.addData("ORDER_DESC", hideParm.getData("ORDER_DESC", i));
                result.addData("DSPN_KIND", hideParm.getData("DSPN_KIND", i));
                result.addData("MEDI_QTY", getShowParm.getData("MEDI_QTY", i)
                        + " " + getShowParm.getData("MEDI_UNIT", i));
                result.addData("ROUTE_CODE", getShowParm.getData("ROUTE_CODE",
                        i));
                result
                        .addData("FREQ_CODE", getShowParm.getData("FREQ_CODE",
                                i));
                result.addData("DR_NOTE", getShowParm.getData("DR_NOTE", i));
                result.addData("DISPENSE_QTY", getShowParm.getData(
                        "DISPENSE_QTY", i)
                        + " " + getShowParm.getData("DISPENSE_UNIT", i));
                result.addData("LINKMAIN_FLG", getShowParm.getData(
                        "LINKMAIN_FLG", i));
                result.addData("LINK_NO", getShowParm.getData("LINK_NO", i));
                result.addData("NS_EXEC_DATE", hideParm.getData("NS_EXEC_DATE",
                        i));
                // result.addData("DR_NOTE", hideParm.getData("DR_NOTE", i));
            }
        }
        // ����count
        result.setCount(result.getCount("ORDER_DESC"));
        return result;
    }

    /**
     * ���ñ����У���Ƿ�ֹͣ�ƻ���for ADM��
     */
    private void checkStopFee() {
        String caseNo = this.getCaseNo();
        ADMTool.getInstance().checkStopFee(caseNo);
    }

    /**
     * �ر��¼�
     * 
     * @return boolean
     */
    public boolean onClosing() {
        // TParm a=new TParm();
        // a.setData("a","aaaaaaaaa");
        // TParm result=
        // TJDODBTool.getInstance().exeIOAction("jdo.inw.testINW",a);
        // System.out.println("=>"+result);

        // switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
        // case 0:
        // if (!onSave())
        // return false;
        // break;
        // case 1:
        // if (!restoreUI())
        // return false;
        // break;
        // case 2:
        // return false;
        // }
        if (!restoreUI())
            return false;
        return true;
    }

    /**
     * �ظ�UI����
     * 
     * @return boolean
     */
    private boolean restoreUI() {
        // �رղ���Ƽ۽���
        if (isCharge) {
            // �Ƴ�����Ƽ����
            getTPanel("ChargePanel").removeAll();
            mp1.onDoubleClicked(isCharge);
            isCharge = !isCharge;
            mp2.onDoubleClicked(false);
            mp3.onDoubleClicked(false);
            callFunction("UI|showTopMenu");
            return false;
        }
        if (isVitalSign) {
            getTPanel("first").remove(getTPanel("first_"));
            getTPanel("second").setVisible(true);
            isVitalSign = !isVitalSign;
            // �Ƴ���UIMenuBar
            callFunction("UI|removeChildMenuBar");
            // �Ƴ���UIToolBar
            callFunction("UI|removeChildToolBar");
            // ��ʾUIshowTopMenu
            callFunction("UI|showTopMenu");
            return false;
        }
        return true;
    }

    /**
     * �����л���ʱ�����TopMenu
     */
    public void onShowWindowsFunction() {
        // ��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    // ��̬������Ӧ��panel
    public TPanel getTPanel(String name) {

        TPanel panel = (TPanel) this.getComponent(name);
        return panel;
    }

    /**
     * �����ӡ
     */
    public void onBarCode() {
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
        } else {
            caseNo_ = this.getCaseNo();
        }
        if (caseNo_.length() == 0) {
            messageBox("��ѡ���ˣ�");
            return;
        }
        // String sql1 = "SELECT * FROM ADM_INP WHERE CASE_NO='" +
        // this.getCaseNo() + "'";
        // ===========pangben modify 20110516 start �������
        String region = "";
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
        }
        // ===========pangben modify 20110516 stop

        String sql1 = "SELECT * FROM ADM_INP WHERE CASE_NO='" + caseNo_ + "'"
                + region;
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
        String mrNo = result1.getValue("MR_NO", 0);
        String sql2 = "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
        TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
        TParm parm = new TParm();
        // ����
        parm.setData("DEPT_CODE", result1.getData("DEPT_CODE", 0));
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", caseNo_);
        // parm.setData("CASE_NO", this.getCaseNo());
        parm.setData("MR_NO", result1.getData("MR_NO", 0));
        parm.setData("PAT_NAME", result2.getData("PAT_NAME", 0));
        parm.setData("ADM_DATE", result1.getData("ADM_DATE", 0));
        parm.setData("IPD_NO", result2.getData("IPD_NO", 0));
        parm.setData("BED_NO", result1.getData("BED_NO", 0));
        parm.setData("POPEDEM", "1");
        // һ���ٴ���ɫ����
        if ("PIC".equals(outsideParm.getValue("INW", "ROLE_TYPE"))) {
        	parm.setData("ROLE_TYPE", "PIC");
        }
        String value = (String) this.openDialog(
                "%ROOT%\\config\\med\\MEDApply.x", parm);
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * ���ò�ѯ
     */
    public void onSelIbs() {
        int selRow = masterTbl.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        if (selRow != -1) {
            TParm tableParm = masterTbl.getParmValue();
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
        }
        if (caseNo_.length() == 0) {
            messageBox("��ѡ���ˣ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("IBS", "CASE_NO", caseNo_);
        parm.setData("IBS", "MR_NO", mrNo_);
        // parm.setData("IBS","CASE_NO",this.getCaseNo());
        // parm.setData("IBS","MR_NO",this.getMrNo());
        parm.setData("IBS", "TYPE", "INWSTATION");
        this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
    }

    /**
     * ƿǩ��ӡ
     */
    public void onPrintPaster() {
        Vector vct = new Vector();
        TParm parm = masterTbl.getParmValue();
        for (int i = 0; i < 21; i++) {
            vct.add(new Vector());
        }
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            ((Vector) vct.get(0)).add(parm.getData("BED_NO", i));
            ((Vector) vct.get(1)).add(parm.getData("MR_NO", i));
            ((Vector) vct.get(2)).add(parm.getData("PAT_NAME", i));
            ((Vector) vct.get(3)).add(parm.getData("LINKMAIN_FLG", i));
            ((Vector) vct.get(4)).add(parm.getData("LINK_NO", i));
            ((Vector) vct.get(5)).add(parm.getData("ORDER_DESC", i) + ""
                    + parm.getData("SPECIFICATION", i));
            ((Vector) vct.get(6)).add(parm.getData("MEDI_QTY", i));
            ((Vector) vct.get(7)).add(parm.getData("MEDI_UNIT", i));
            ((Vector) vct.get(8)).add(parm.getData("ORDER_CODE", i));
            ((Vector) vct.get(9)).add(parm.getData("ORDER_NO", i));
            ((Vector) vct.get(10)).add(parm.getData("ORDER_SEQ", i));
            ((Vector) vct.get(11)).add(parm.getData("START_DTTM", i));
            ((Vector) vct.get(12)).add(parm.getData("FREQ_CODE", i));
            ((Vector) vct.get(13)).add(getStationDesc(parm.getValue(
                    "STATION_CODE", i)));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(
                    " SELECT *" + " FROM SYS_PATINFO A" + " WHERE MR_NO ='"
                            + parm.getData("MR_NO", i) + "'"));
            TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
                    " SELECT CHN_DESC" + " FROM SYS_DICTIONARY A "
                            + " WHERE GROUP_ID = 'SYS_SEX'" + " AND  ID = '"
                            + patParm.getData("SEX_CODE", 0) + "'"));
            ((Vector) vct.get(14)).add(sexDescParm.getData("CHN_DESC", 0));
            ((Vector) vct.get(15)).add(StringUtil.getInstance().showAge(
                    (Timestamp) patParm.getData("BIRTH_DATE", 0),
                    SystemTool.getInstance().getDate()));
            String udSt = "";
            if (parm.getData("DSPN_KIND", i).equals("UD")
                    || parm.getData("DSPN_KIND", i).equals("F"))
                udSt = "����ҽ��";
            else if (parm.getData("DSPN_KIND", i).equals("ST"))
                udSt = "��ʱҽ��";
            ((Vector) vct.get(16)).add(udSt);
            ((Vector) vct.get(17)).add(parm.getValue("ROUTE_CODE", i));
            ((Vector) vct.get(18)).add(getOperatorName(parm.getValue(
                    "ORDER_DR_CODE", i)));
            ((Vector) vct.get(19)).add(parm.getData("DISPENSE_QTY", i));
            ((Vector) vct.get(20)).add(parm.getData("DISPENSE_UNIT", i));
        }
        vct.add(getUnitMap());
        openWindow("%ROOT%\\config\\inw\\INWPrintPQUI.x", vct);
    }

    /**
     * 
     * ƿǩ��ӡ luhai 2012-2-24
     */
    public void onPrintPasterBottle() {
        Vector vct = new Vector();
        TParm parm = masterTbl.getParmValue();
        for (int i = 0; i < 24; i++) {
            vct.add(new Vector());
        }
        String cat1Type = "";
        String orderCode = "";
        String orderDesc = "";
        String Dosetype = "";
        String routeCode = "";
        // System.out.println("=====���ݣ�"+parm);
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            cat1Type = parm.getData("CAT1_TYPE", i) + "";
            orderCode = (String) parm.getData("ORDER_CODE", i);
            orderDesc = (String) parm.getData("ORDER_DESC", i);
            routeCode = (String) parm.getData("ROUTE_CODE", i);
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 16))) {//modify machao 15���16
                if (cat1Type.equals("PHA")) {
                    /*
                     * Dosetype = SysPhaBarTool.getInstance().getDoseType(
                     * orderCode);
                     */
                    Dosetype = SysPhaBarTool.getInstance().getClassifyType(
                            routeCode);

                   /* if (!Dosetype.equals("I") && !Dosetype.equals("F")) {
                        this.messageBox(orderDesc + "����������Σ����ܴ�ӡ��");
                        return;
                    }*/
                }
            }
            if (!("true".equals(masterTbl.getValueAt(i, 16) + "") || ("Y"
                    .equals(masterTbl.getValueAt(i, 16) + "")))) {//modify machao 15���16
                continue;
            }
            ((Vector) vct.get(0)).add(parm.getData("BED_NO", i));
            ((Vector) vct.get(1)).add(parm.getData("MR_NO", i));
            ((Vector) vct.get(2)).add(parm.getData("PAT_NAME", i));
            ((Vector) vct.get(3)).add(parm.getData("LINKMAIN_FLG", i));
            ((Vector) vct.get(4)).add(parm.getData("LINK_NO", i));
            ((Vector) vct.get(5)).add(parm.getData("ORDER_DESC", i) + ""
                    + parm.getData("SPECIFICATION", i));
            ((Vector) vct.get(6)).add(parm.getData("MEDI_QTY", i));
            ((Vector) vct.get(7)).add(parm.getData("MEDI_UNIT", i));
            ((Vector) vct.get(8)).add(parm.getData("ORDER_CODE", i));
            ((Vector) vct.get(9)).add(parm.getData("ORDER_NO", i));
            ((Vector) vct.get(10)).add(parm.getData("ORDER_SEQ", i));
            ((Vector) vct.get(11)).add(parm.getData("START_DTTM", i));
            ((Vector) vct.get(12)).add(parm.getData("FREQ_CODE", i));
            ((Vector) vct.get(13)).add(getStationDesc(parm.getValue(
                    "STATION_CODE", i)));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(
                    " SELECT *" + " FROM SYS_PATINFO A" + " WHERE MR_NO ='"
                            + parm.getData("MR_NO", i) + "'"));
            TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
                    " SELECT CHN_DESC" + " FROM SYS_DICTIONARY A "
                            + " WHERE GROUP_ID = 'SYS_SEX'" + " AND  ID = '"
                            + patParm.getData("SEX_CODE", 0) + "'"));
            ((Vector) vct.get(14)).add(sexDescParm.getData("CHN_DESC", 0));
            ((Vector) vct.get(15)).add(StringUtil.getInstance().showAge(
                    (Timestamp) patParm.getData("BIRTH_DATE", 0),
                    SystemTool.getInstance().getDate()));
            String udSt = "";
            if (parm.getData("DSPN_KIND", i).equals("UD")
                    || parm.getData("DSPN_KIND", i).equals("F"))
                udSt = "����ҽ��";
            else if (parm.getData("DSPN_KIND", i).equals("ST"))
                udSt = "��ʱҽ��";
            ((Vector) vct.get(16)).add(udSt);
            ((Vector) vct.get(17)).add(parm.getValue("ROUTE_CODE", i));
            ((Vector) vct.get(18)).add(getOperatorName(parm.getValue(
                    "ORDER_DR_CODE", i)));
            ((Vector) vct.get(19)).add(parm.getData("DISPENSE_QTY", i));
            ((Vector) vct.get(20)).add(parm.getData("DISPENSE_UNIT", i));
            // �������
            ((Vector) vct.get(21)).add(parm.getData("CLASSIFY_TYPE", i));
            // ����CASE_NO
            ((Vector) vct.get(22)).add(parm.getData("CASE_NO", i));
            // ���� END_DTTM
            ((Vector) vct.get(23)).add(parm.getData("END_DTTM", i));
        }
        vct.add(getUnitMap());
        // openWindow("%ROOT%\\config\\inw\\INWPrintBottonUI.x", vct);
        // ��ӡƿǩ��ֱ�Ӵ�ӡ��
        printBottle(vct);
    }

    /**
     * 
     * ��ӡƿǩ���� luhai 2012-2-28
     * 
     * @param buttonVct
     */
    public void printBottle(Vector buttonVct) {
        parm = initPageData((Vector) buttonVct);
        Object objPha = (buttonVct).get((buttonVct).size() - 1);
        if (objPha != null) {
            phaMap = (Map) (buttonVct).get((buttonVct).size() - 1);
        }
        // ��ӡƿǩ
        // ѡ����
        int row = 0;
        // ѡ����
        int column = 0;

        int count = parm.getCount("BED_NO");
        if (count <= 0) {
            this.messageBox_("û��Ҫ��ӡ��ҽ����");
            return;
        }
        TParm actionParm = creatPrintData();
        int rowCount = actionParm.getCount("PRINT_DATAPQ");
        if (rowCount <= 0) {
            this.messageBox_("��ӡ���ݴ���");
            return;
        }
        // TParm printDataPQParm = new TParm();
        // int pRow = row;
        // int pColumn = column;
        // int cnt=0;
        // int rowNull = 0;
        // for(int i = 0; i < 15; i++){
        // if (i % 3 == 0&&i!=0){
        // cnt = 0 ;
        // rowNull++;
        // }
        // if (i < pRow * 3 + pColumn) {
        // printDataPQParm.addData("ORDER_DATE_"+(cnt+1),"");
        // printDataPQParm.addData("BED_"+(cnt+1),"");
        // printDataPQParm.addData("PAT_NAME_"+(cnt+1),"");
        // printDataPQParm.addData("ORDER_1_"+(cnt+1),"");
        // printDataPQParm.addData("QTY_1_"+(cnt+1),"");
        // printDataPQParm.addData("TOT_QTY_1_"+(cnt+1),"");
        // printDataPQParm.addData("ORDER_2_"+(cnt+1),"");
        // printDataPQParm.addData("QTY_2_"+(cnt+1),"");
        // printDataPQParm.addData("TOT_QTY_2_"+(cnt+1),"");
        // printDataPQParm.addData("ORDER_3_"+(cnt+1),"");
        // printDataPQParm.addData("QTY_3_"+(cnt+1),"");
        // printDataPQParm.addData("TOT_QTY_3_"+(cnt+1),"");
        // printDataPQParm.addData("ORDER_4_"+(cnt+1),"");
        // printDataPQParm.addData("QTY_4_"+(cnt+1),"");
        // printDataPQParm.addData("TOT_QTY_4_"+(cnt+1),"");
        // printDataPQParm.addData("ORDER_5_"+(cnt+1),"");
        // printDataPQParm.addData("QTY_5_"+(cnt+1),"");
        // printDataPQParm.addData("TOT_QTY_5_"+(cnt+1),"");
        // printDataPQParm.addData("STATION_DESC_"+(cnt+1),"");
        // printDataPQParm.addData("MR_NO_"+(cnt+1),"");
        // printDataPQParm.addData("SEX_"+(cnt+1),"");
        // printDataPQParm.addData("AGE_"+(cnt+1),"");
        // printDataPQParm.addData("RX_TYPE_"+(cnt+1),"");
        // printDataPQParm.addData("FREQ_CODE_"+(cnt+1),"");
        // printDataPQParm.addData("DOCTOR_"+(cnt+1),"");
        // printDataPQParm.addData("ROUT_"+(cnt+1),"");
        // printDataPQParm.addData("PAGE_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_NAME_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_QTY_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_TOT_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_DR_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_CHECK_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_EXE_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_PAGEF_"+(cnt+1),"");
        // printDataPQParm.addData("TITLE_PAGEB_"+(cnt+1),"");
        // cnt++;
        // continue;
        // }else{
        // break;
        // }
        // }
        // for (int i = 0; i < rowCount; i++) {
        // TParm temp = (TParm)actionParm.getData("PRINT_DATAPQ",i);
        // printDataPQParm.addData("ORDER_DATE_"+(pColumn+1),temp.getData("DATETIME"));
        // printDataPQParm.addData("BED_" + (pColumn + 1),
        // temp.getData("BED_NO"));
        // printDataPQParm.addData("PAT_NAME_" + (pColumn + 1),
        // temp.getData("PAT_NAME"));
        // printDataPQParm.addData("STATION_DESC_" + (pColumn + 1),
        // temp.getData("STATION_DESC"));
        // printDataPQParm.addData("MR_NO_" + (pColumn + 1),
        // temp.getData("MR_NO"));
        // printDataPQParm.addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
        // printDataPQParm.addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
        // printDataPQParm.addData("RX_TYPE_" + (pColumn + 1),
        // temp.getData("RX_TYPE"));
        // printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1),
        // temp.getData("FREQ"));
        // printDataPQParm.addData("DOCTOR_" + (pColumn + 1),
        // temp.getData("DOCTOR"));
        // printDataPQParm.addData("ROUT_" + (pColumn + 1),
        // temp.getData("ROUTE"));
        // printDataPQParm.addData("PAGE_" + (pColumn + 1),
        // temp.getData("PAGE"));
        //
        // printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1),"ҩ��");
        // printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1),"����");
        // printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1),"����");
        // printDataPQParm.addData("TITLE_DR_" + (pColumn + 1),"ҽ��:");
        // printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1),"���:");
        // printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1),"ִ��:");
        // printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1),"��");
        // printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1),"ҳ");
        // int rowOrderCount = temp.getCount("ORDER_DESC");
        // for(int j=0;j<5;j++){
        // if(j>rowOrderCount-1){
        // printDataPQParm.addData("ORDER_"+(j+1)+"_"+(pColumn+1),"");
        // printDataPQParm.addData("QTY_"+(j+1)+"_"+(pColumn+1),"");
        // printDataPQParm.addData("TOT_QTY_"+(j+1)+"_"+(pColumn+1),"");
        // continue;
        // }
        // printDataPQParm.addData("ORDER_"+(j+1)+"_"+(pColumn+1),temp.getData("ORDER_DESC",j));
        // printDataPQParm.addData("QTY_"+(j+1)+"_"+(pColumn+1),numDot(temp.getDouble("QTY",j))+""+temp.getData("UNIT_CODE",j));
        // printDataPQParm.addData("TOT_QTY_"+(j+1)+"_"+(pColumn+1),numDot(temp.getDouble("DOSAGE_QTY",j))+""+temp.getData("DOSAGE_UNIT",j));
        // }
        // pColumn++;
        // if (pColumn == 3) {
        // pColumn = 0;
        // pRow++;
        // }
        // }
        // printDataPQParm.setCount(pRow+rowNull+1);
        // printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
        // printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
        // printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
        // TParm parmForPrint = new TParm();
        // parmForPrint.setData("PRINT_PQ", printDataPQParm.getData());
        // System.out.println("======="+parmForPrint);
        // // TParm parm = new TParm();
        // // TParm prtTableParm = new TParm();
        // // prtTableParm.addData("TEST", "222222222");
        // // prtTableParm.addData("TEST", "333333333333");
        // // prtTableParm.addData("TEST", "122222");
        // // prtTableParm.addData("TEST2", "---------");
        // // prtTableParm.addData("TEST2", "333333333333");
        // // prtTableParm.addData("TEST2", "122222");
        // // prtTableParm.setCount(prtTableParm.getCount("TEST"));
        // // prtTableParm.addData("SYSTEM", "COLUMNS", "A1");
        // // prtTableParm.addData("SYSTEM", "COLUMNS", "A2");
        // // prtTableParm.addData("SYSTEM", "COLUMNS", "A3");
        // // parm.setData("PRINT_PQ", prtTableParm.getData());
        // // System.out.println("****************************"+parm);
        // openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
        // ***************************************************
        // ���´����ӡƿǩ���������������ϵ�һ���н��д�ӡ luhai 2012-2-29 begin
        // ***************************************************
        TParm printDataPQParm = new TParm();
        int pRow = row;
        int pColumn = column;
        int cnt = 0;
        int rowNull = 0;
        for (int i = 0; i < 15; i++) {
            if (i % 3 == 0 && i != 0) {
                // cnt = 0 ;
                rowNull++;
            }
            if (i < pRow * 3 + pColumn) {
                printDataPQParm.addData("ORDER_DATE_" + (cnt + 1), "");
                printDataPQParm.addData("BED_" + (cnt + 1), "");
                printDataPQParm.addData("PAT_NAME_" + (cnt + 1), "");
                printDataPQParm.addData("BAR_CODE_" + (cnt + 1), "");
                printDataPQParm.addData("ORDER_1_" + (cnt + 1), "");
                printDataPQParm.addData("QTY_1_" + (cnt + 1), "");
                printDataPQParm.addData("TOT_QTY_1_" + (cnt + 1), "");
                printDataPQParm.addData("ORDER_2_" + (cnt + 1), "");
                printDataPQParm.addData("QTY_2_" + (cnt + 1), "");
                printDataPQParm.addData("TOT_QTY_2_" + (cnt + 1), "");
                printDataPQParm.addData("ORDER_3_" + (cnt + 1), "");
                printDataPQParm.addData("QTY_3_" + (cnt + 1), "");
                printDataPQParm.addData("TOT_QTY_3_" + (cnt + 1), "");
                printDataPQParm.addData("ORDER_4_" + (cnt + 1), "");
                printDataPQParm.addData("QTY_4_" + (cnt + 1), "");
                printDataPQParm.addData("TOT_QTY_4_" + (cnt + 1), "");
                printDataPQParm.addData("ORDER_5_" + (cnt + 1), "");
                printDataPQParm.addData("QTY_5_" + (cnt + 1), "");
                printDataPQParm.addData("TOT_QTY_5_" + (cnt + 1), "");
                printDataPQParm.addData("STATION_DESC_" + (cnt + 1), "");
                printDataPQParm.addData("MR_NO_" + (cnt + 1), "");
                printDataPQParm.addData("SEX_" + (cnt + 1), "");
                printDataPQParm.addData("AGE_" + (cnt + 1), "");
                printDataPQParm.addData("RX_TYPE_" + (cnt + 1), "");
                printDataPQParm.addData("FREQ_CODE_" + (cnt + 1), "");
                printDataPQParm.addData("DOCTOR_" + (cnt + 1), "");
                printDataPQParm.addData("ROUT_" + (cnt + 1), "");
                printDataPQParm.addData("PAGE_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_NAME_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_QTY_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_TOT_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_DR_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_CHECK_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_EXE_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_PAGEF_" + (cnt + 1), "");
                printDataPQParm.addData("TITLE_PAGEB_" + (cnt + 1), "");
                // cnt++;
                continue;
            } else {
                break;
            }
        }
        // ���ñ�������ִ��ʱ��ʵ��������Ŀ
        int realtotCount = 0;
        for (int i = 0; i < rowCount; i++) {
            TParm temp = (TParm) actionParm.getData("PRINT_DATAPQ", i);
            // TJDODBTool.getInstance().select("  ");
            printDataPQParm.addData("ORDER_DATE_" + (pColumn + 1), temp
                    .getData("DATETIME"));
            printDataPQParm.addData("BED_" + (pColumn + 1), temp
                    .getData("BED_NO"));
            printDataPQParm.addData("PAT_NAME_" + (pColumn + 1), temp
                    .getData("PAT_NAME"));
            // this.messageBox( temp.getData("BAR_CODE")+"");
            printDataPQParm.addData("BAR_CODE_" + (pColumn + 1), temp
                    .getData("BAR_CODE"));
            printDataPQParm.addData("STATION_DESC_" + (pColumn + 1), temp
                    .getData("STATION_DESC"));
            printDataPQParm.addData("MR_NO_" + (pColumn + 1), temp
                    .getData("MR_NO"));
            printDataPQParm
                    .addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
            printDataPQParm
                    .addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
            printDataPQParm.addData("RX_TYPE_" + (pColumn + 1), temp
                    .getData("RX_TYPE"));
            printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1), temp
                    .getData("FREQ"));
            printDataPQParm.addData("DOCTOR_" + (pColumn + 1), temp
                    .getData("DOCTOR"));
            printDataPQParm.addData("ROUT_" + (pColumn + 1), temp
                    .getData("ROUTE"));
            printDataPQParm.addData("PAGE_" + (pColumn + 1), temp
                    .getData("PAGE"));
            printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1), "ҩ��");
            printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1), "����");
            printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1), "����");
            printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "ҽ��:");
            printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "���:");
            printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1), "ִ��:");
            printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1), "��");
            printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1), "ҳ");
            int rowOrderCount = temp.getCount("ORDER_DESC");
            for (int j = 0; j < 5; j++) {
                if (j > rowOrderCount - 1) {
                    printDataPQParm.addData("ORDER_" + (j + 1) + "_"
                            + (pColumn + 1), "");
                    printDataPQParm.addData("QTY_" + (j + 1) + "_"
                            + (pColumn + 1), "");
                    printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
                            + (pColumn + 1), "");
                    continue;
                }
                printDataPQParm.addData("ORDER_" + (j + 1) + "_"
                        + (pColumn + 1), temp.getData("ORDER_DESC", j));
                printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn + 1),
                        numDot(temp.getDouble("QTY", j)) + ""
                                + temp.getData("UNIT_CODE", j));
                printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
                        + (pColumn + 1),
                        numDot(temp.getDouble("DOSAGE_QTY", j)) + ""
                                + temp.getData("DOSAGE_UNIT", j));
            }
            // pColumn++;
            if (pColumn == 3) {
                // pColumn = 0;
                pRow++;
            }
        }
        // printDataPQParm.setCount(pRow+rowNull+1);
        printDataPQParm.setCount(rowCount);
        // --------modify
        printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
        printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
        printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
        TParm parmForPrint = new TParm();
        parmForPrint.setData("PRINT_PQ", printDataPQParm.getData());
        // System.out.println("======="+parmForPrint);
        // TParm parm = new TParm();
        // TParm prtTableParm = new TParm();
        // prtTableParm.addData("TEST", "222222222");
        // prtTableParm.addData("TEST", "333333333333");
        // prtTableParm.addData("TEST", "122222");
        // prtTableParm.addData("TEST2", "---------");
        // prtTableParm.addData("TEST2", "333333333333");
        // prtTableParm.addData("TEST2", "122222");
        // prtTableParm.setCount(prtTableParm.getCount("TEST"));
        // prtTableParm.addData("SYSTEM", "COLUMNS", "A1");
        // prtTableParm.addData("SYSTEM", "COLUMNS", "A2");
        // prtTableParm.addData("SYSTEM", "COLUMNS", "A3");
        // parm.setData("PRINT_PQ", prtTableParm.getData());
        // System.out.println("****************************"+parm);
        // System.out.println("-=========:"+parmForPrint.getRow(0));
        // luhai 2012-2-13 modify ��ƿǩ���ݲ𿪣�ÿҳ�����д�ӡ
        // ����ͼ�㷽ʽ�������⣬��ȡ����ӡԤ�������÷�������ÿҳ���ݷ�ʽʵ�֣�
        TParm pqParm = new TParm((Map) parmForPrint.getData("PRINT_PQ"));
        for (int i = 0; i < pqParm.getCount(); i++) {
            printBottleForEach(pqParm, i);
        }
        // luhai 2012-2-13 modify modify ��ƿǩ���ݲ𿪣�ÿҳ�����д�ӡ
        // ***************************************************
        // ���´����ӡƿǩ���������������ϵ�һ���н��д�ӡ luhai 2012-2-29 end
        // ***************************************************
    }

    /**
     * 
     * ����ƿǩTparm ��ӡ����
     * 
     * @param pqTParm
     *            ƿǩTParm
     * @param index
     *            ��ӡ���� luhai 2012-3-13
     */
    private void printBottleForEach(TParm pqTParm, int index) {
        TParm printTParm = new TParm();
        TParm newPqTParm = new TParm();
        String[] names = pqTParm.getNames();
        for (String key : names) {
            newPqTParm.addData(key, pqTParm.getValue(key, index));
        }
        newPqTParm.setCount(1);
        printTParm.setData("PRINT_PQ", newPqTParm.getData());
        // openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
        openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",
                printTParm, true);
    }

    // ȫ������
    TParm parm = new TParm();
    // ȫ�ֵ�λ
    Map phaMap;

    /**
     * �����ӡ����
     * 
     * @return TParm
     */
    public TParm creatPrintData() {
        TParm result = new TParm();
        Set linkSet = new HashSet();
        Map linkMap = new HashMap();
        int rowCount = parm.getCount("PAT_NAME");
        // ��ӡ���ٸ�ƿǩ
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            if (!"".equals(temp.getValue("LINK_NO"))) {
                String tempStr = temp.getValue("MR_NO")
                        + temp.getValue("LINK_NO")
                        + temp.getValue("START_DTTM")
                        + temp.getValue("BAR_CODE") + temp.getValue("ORDER_NO");
                linkSet.add(tempStr);
            }
        }
        Iterator linkIterator = linkSet.iterator();
        // ÿ��ƿǩ�Ļ�����Ϣ
        while (linkIterator.hasNext()) {
            String tempLinkStr = "" + linkIterator.next();
            for (int j = 0; j < rowCount; j++) {
                TParm tempParm = parm.getRow(j);
                // if(tempLinkStr.equals(tempParm.getValue("MR_NO")+tempParm.getValue("LINK_NO")+tempParm.getValue("START_DTTM")+tempParm.getValue("SEQ_NO")+
                // tempParm.getValue("ORDER_NO"))){
                if (tempLinkStr.equals(tempParm.getValue("MR_NO")
                        + tempParm.getValue("LINK_NO")
                        + tempParm.getValue("START_DTTM")
                        + tempParm.getValue("BAR_CODE")
                        + tempParm.getValue("ORDER_NO"))
                        && !"".equals(tempParm.getValue("LINK_NO"))) {
                    TParm temp = new TParm();
                    // String dateTime = tempParm.getValue("START_DTTM")
                    // .substring(4, 6)
                    // + "/"
                    // + tempParm.getValue("START_DTTM").substring(6, 8);
                    String dateTime = "";
                    try {
                        dateTime = tempParm.getValue("ORDER_DATE").substring(4,
                                6)
                                + "/"
                                + tempParm.getValue("ORDER_DATE").substring(6,
                                        8)
                                + " "
                                + tempParm.getValue("ORDER_DATETIME")
                                        .substring(0, 2)
                                + ":"
                                + tempParm.getValue("ORDER_DATETIME")
                                        .substring(2, 4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    temp.setData("DATETIME", dateTime);
                    temp.setData("BED_NO", tempParm.getValue("BED_NO"));
                    temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
                    temp.setData("MR_NO", tempParm.getValue("MR_NO"));
                    temp.setData("FREQ", tempParm.getValue("FREQ"));
                    temp.setData("STATION_DESC", tempParm
                            .getValue("STATION_DESC"));
                    temp.setData("SEX", tempParm.getValue("SEX"));
                    temp.setData("AGE", tempParm.getValue("AGE"));
                    temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
                    temp.setData("ROUTE", tempParm.getValue("ROUTE"));
                    temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
                    temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
                    linkMap.put(tempLinkStr, temp);
                    break;
                }
            }
            TParm temp = (TParm) linkMap.get(tempLinkStr);
            for (int j = 0; j < rowCount; j++) {
                TParm tempParm = parm.getRow(j);
                if (tempLinkStr.equals(tempParm.getValue("MR_NO")
                        + tempParm.getValue("LINK_NO")
                        + tempParm.getValue("START_DTTM")
                        + tempParm.getValue("BAR_CODE")
                        + tempParm.getValue("ORDER_NO"))) {
                    String orderDesc = tempParm.getValue("ORDER_DESC");
                    String desc[] = breakNFixRow(orderDesc, 23, 1);
                    for (int k = 0; k < desc.length; k++) {
                        if (k == 0) {
                            temp.addData("ORDER_DESC", desc[k]);
                            // ����
                            temp.addData("QTY", tempParm.getValue("QTY"));
                            // ��λ
                            temp.addData("UNIT_CODE", phaMap.get(tempParm
                                    .getValue("UNIT_CODE")));
                            // ����
                            temp.addData("DOSAGE_QTY", tempParm
                                    .getValue("DOSAGE_QTY"));
                            // ������λ
                            temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
                                    .getValue("DOSAGE_UNIT")));
                            // ������
                            temp.addData("LINK_MAIN_FLG", tempParm
                                    .getValue("LINK_MAIN_FLG"));
                            continue;
                        }
                        temp.addData("ORDER_DESC", desc[k]);
                        // ����
                        temp.addData("QTY", "");
                        // ��λ
                        temp.addData("UNIT_CODE", "");
                        // ����
                        temp.addData("DOSAGE_QTY", "");
                        // ������λ
                        temp.addData("DOSAGE_UNIT", "");
                        // ������
                        temp.addData("LINK_MAIN_FLG", "");
                    }
                }
            }
            linkMap.put(tempLinkStr, temp);
            result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
        }
        Set onlySet = new HashSet();
        for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
            onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
                    .getValue("BED_NO"));
        }
        TParm resultTemp = new TParm();
        Iterator iter = onlySet.iterator();
        while (iter.hasNext()) {
            String temp = iter.next().toString();
            for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
                if (temp.equals(((TParm) result.getRow(j).getData(
                        "PRINT_DATAPQ")).getValue("BED_NO"))) {
                    resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
                            .getData("PRINT_DATAPQ"));
                }
            }
        }
        // **************************************************************************************************
        // ���������ҽ�������Ҳ��ʾ����begin
        // **************************************************************************************************
        result = new TParm();
        linkSet = new HashSet();
        linkMap = new HashMap();
        rowCount = parm.getCount("PAT_NAME");
        // ��ӡ���ٸ�ƿǩ
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            if ("".equals(temp.getValue("LINK_NO"))) {
                String tempStr = temp.getValue("MR_NO")
                        + temp.getValue("LINK_NO")
                        + temp.getValue("START_DTTM")
                        + temp.getValue("BAR_CODE") + temp.getValue("ORDER_NO");
                linkSet.add(tempStr);
            }
        }
        linkIterator = linkSet.iterator();
        // ÿ��ƿǩ�Ļ�����Ϣ
        while (linkIterator.hasNext()) {
            String tempLinkStr = "" + linkIterator.next();
            for (int j = 0; j < rowCount; j++) {
                TParm tempParm = parm.getRow(j);
                if (tempLinkStr.equals(tempParm.getValue("MR_NO")
                        + tempParm.getValue("LINK_NO")
                        + tempParm.getValue("START_DTTM")
                        + tempParm.getValue("BAR_CODE")
                        + tempParm.getValue("ORDER_NO"))
                        && "".equals(tempParm.getValue("LINK_NO"))) {
                    TParm temp = new TParm();
                    // String dateTime = tempParm.getValue("START_DTTM")
                    // .substring(4, 6)
                    // + "/"
                    // + tempParm.getValue("START_DTTM").substring(6, 8);
                    String dateTime = "";
                    try {
                        dateTime = tempParm.getValue("ORDER_DATE").substring(4,
                                6)
                                + "/"
                                + tempParm.getValue("ORDER_DATE").substring(6,
                                        8)
                                + " "
                                + tempParm.getValue("ORDER_DATETIME")
                                        .substring(0, 2)
                                + ":"
                                + tempParm.getValue("ORDER_DATETIME")
                                        .substring(2, 4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    temp.setData("DATETIME", dateTime);
                    temp.setData("BED_NO", tempParm.getValue("BED_NO"));
                    temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
                    temp.setData("MR_NO", tempParm.getValue("MR_NO"));
                    temp.setData("FREQ", tempParm.getValue("FREQ"));
                    temp.setData("STATION_DESC", tempParm
                            .getValue("STATION_DESC"));
                    temp.setData("SEX", tempParm.getValue("SEX"));
                    temp.setData("AGE", tempParm.getValue("AGE"));
                    temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
                    temp.setData("ROUTE", tempParm.getValue("ROUTE"));
                    temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
                    temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
                    linkMap.put(tempLinkStr, temp);
                    break;
                }
            }
            TParm temp = (TParm) linkMap.get(tempLinkStr);
            for (int j = 0; j < rowCount; j++) {
                TParm tempParm = parm.getRow(j);
                if (tempLinkStr.equals(tempParm.getValue("MR_NO")
                        + tempParm.getValue("LINK_NO")
                        + tempParm.getValue("START_DTTM")
                        + tempParm.getValue("BAR_CODE")
                        + tempParm.getValue("ORDER_NO"))) {
                    String orderDesc = tempParm.getValue("ORDER_DESC");
                    String desc[] = breakNFixRow(orderDesc, 23, 1);
                    for (int k = 0; k < desc.length; k++) {
                        if (k == 0) {
                            temp.addData("ORDER_DESC", desc[k]);
                            // ����
                            temp.addData("QTY", tempParm.getValue("QTY"));
                            // ��λ
                            temp.addData("UNIT_CODE", phaMap.get(tempParm
                                    .getValue("UNIT_CODE")));
                            // ����
                            temp.addData("DOSAGE_QTY", tempParm
                                    .getValue("DOSAGE_QTY"));
                            // ������λ
                            temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
                                    .getValue("DOSAGE_UNIT")));
                            // ������
                            temp.addData("LINK_MAIN_FLG", tempParm
                                    .getValue("LINK_MAIN_FLG"));
                            continue;
                        }
                        temp.addData("ORDER_DESC", desc[k]);
                        // ����
                        temp.addData("QTY", "");
                        // ��λ
                        temp.addData("UNIT_CODE", "");
                        // ����
                        temp.addData("DOSAGE_QTY", "");
                        // ������λ
                        temp.addData("DOSAGE_UNIT", "");
                        // ������
                        temp.addData("LINK_MAIN_FLG", "");
                    }
                }
            }
            linkMap.put(tempLinkStr, temp);
            result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
        }
        onlySet = new HashSet();
        for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
            onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
                    .getValue("BED_NO"));
        }
        // TParm resultTemp = new TParm();
        iter = onlySet.iterator();
        while (iter.hasNext()) {
            String temp = iter.next().toString();
            for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
                if (temp.equals(((TParm) result.getRow(j).getData(
                        "PRINT_DATAPQ")).getValue("BED_NO"))) {
                    resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
                            .getData("PRINT_DATAPQ"));
                }
            }
        }
        // **************************************************************************************************
        // ���������ҽ�������Ҳ��ʾ����end
        // **************************************************************************************************

        return configParm(resultTemp);
    }

    /**
     * ��ʼ��ҳ���ӡ���� luhai 2012-2-28
     * 
     * @param parm
     *            Vector
     * @return TParm
     */
    public TParm initPageData(Vector parm) {
        TParm result = new TParm();
        int rowCount = ((Vector) parm.get(0)).size();
        for (int i = 0; i < rowCount; i++) {
            // if(((Vector)parm.get(4)).get(i)!=null&&((Vector)parm.get(4)).get(i).toString().trim().length()!=0&&!"null".equals(((Vector)parm.get(4)).get(i))){
            // Ƶ��
            // luhai 2012-2-29 modify ����Ƶ��Ϊ�յ���� begin
            String freqCode = "";
            if (((Vector) (parm.get(12))).get(i) == null) {
                freqCode = "STAT";
            } else {
                freqCode = ((Vector) parm.get(12)).get(i).toString();
            }
            // luhai 2012-2-29 modify ����Ƶ��Ϊ�յ���� end
            // add
            // modify
            TParm freqParm = new TParm(this.getDBTool().select(
                    "SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"
                            + freqCode + "'"));
            int countFreq = freqParm.getInt("FREQ_TIMES", 0);
            // luhai 2012-3-1 ����ִ�д����ļ��㣬֮ǰ�߼�����Ƶ�μ���������������ִ��������������Ҫÿ��ִ�ж���ӡƿǩ begin
            String caseNo = ((Vector) parm.get(22)).get(i) + "";
            String orderNo = ((Vector) parm.get(9)).get(i) + "";
            String orderSeq = ((Vector) parm.get(10)).get(i) + "";
            String startDttm = ((Vector) parm.get(11)).get(i) + "";
            String endDttm = ((Vector) parm.get(23)).get(i) + "";
            // ��ѯϸ���SQL
            String sql = " SELECT "
                    + " BAR_CODE,ORDER_DATE,ORDER_DATETIME,DOSAGE_QTY,DOSAGE_UNIT "//modify by wanglong 20140402
                    + " FROM ODI_DSPND  WHERE CASE_NO='"
                    + caseNo
                    + "' AND ORDER_NO='"
                    + orderNo
                    + "' AND ORDER_SEQ='"
                    + orderSeq
                    + "' "
                    + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
                    + startDttm
                    + "','YYYYMMDDHH24MISS') "
                    + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
                    + endDttm + "','YYYYMMDDHH24MISS')"
                    + " AND DC_DATE IS NULL  "// SHIBL 20130301 MODIFY
                    + " ORDER BY ORDER_DATE||ORDER_DATETIME ";
            // System.out.println("sql=========="+sql);
            TParm resultDspnCnt = new TParm(TJDODBTool.getInstance()
                    .select(sql));
            int totCount = resultDspnCnt.getCount();
            String barCode = "";
            // System.out.println("BAR_CODE-------"+barCode);
            // if (countFreq > 1) {
            // // totCount = totCount * countFreq;
            // // luhai 2012-4-1 ƿǩ��ӡ�������� begin
            // totCount = totCount;
            // // luhai 2012-4-1 ƿǩ��ӡ�������� begin
            // }
            // this.messageBox(totCount+"");
            // int tot_time=
            // luhai 2012-3-1 ����ִ�д����ļ��㣬֮ǰ�߼�����Ƶ�μ���������������ִ��������������Ҫÿ��ִ�ж���ӡƿǩ end
            // ����1��
            int seqNo = 1;
            // // if(countFreq<=1){
            // if (totCount <= 1) {
            // result.addData("BED_NO", ((Vector) parm.get(0)).get(i));
            // result.addData("MR_NO", ((Vector) parm.get(1)).get(i));
            // result.addData("PAT_NAME", ((Vector) parm.get(2)).get(i));
            // result.addData("LINK_MAIN_FLG", ((Vector) parm.get(3)).get(i));
            // result.addData("LINK_NO", ((Vector) parm.get(4)).get(i));
            // result.addData("ORDER_DESC", ((Vector) parm.get(5)).get(i));
            // result.addData("QTY", ((Vector) parm.get(6)).get(i));
            // result.addData("UNIT_CODE", ((Vector) parm.get(7)).get(i));
            // result.addData("ORDER_CODE", ((Vector) parm.get(8)).get(i));
            // result.addData("ORDER_NO", ((Vector) parm.get(9)).get(i));
            // result.addData("ORDER_SEQ", ((Vector) parm.get(10)).get(i));
            // result.addData("START_DTTM", ((Vector) parm.get(11)).get(i));
            // result.addData("SEQ_NO", 0);
            // result.addData("FREQ", ((Vector) parm.get(12)).get(i));
            // result.addData("STATION_DESC", ((Vector) parm.get(13)).get(i));
            // result.addData("SEX", ((Vector) parm.get(14)).get(i));
            // result.addData("AGE", ((Vector) parm.get(15)).get(i));
            // result.addData("RX_TYPE", ((Vector) parm.get(16)).get(i));
            // result.addData("ROUTE", ((Vector) parm.get(17)).get(i));
            // result.addData("DOCTOR", ((Vector) parm.get(18)).get(i));
            // result.addData("DOSAGE_QTY", ((Vector) parm.get(19)).get(i));
            // result.addData("DOSAGE_UNIT", ((Vector) parm.get(20)).get(i));
            // // �������
            // result.addData("CLASSIFY_TYPE", ((Vector) parm.get(21)).get(i));
            // // ����case_no
            // result.addData("CASE_NO", ((Vector) parm.get(22)).get(i));
            // // ����END_DTTM
            // result.addData("END_DTTM", ((Vector) parm.get(23)).get(i));
            // // barCode
            // result.addData("BAR_CODE", barCode);
            //
            // } else {
            // for(int j=0;j<countFreq;j++){
            for (int j = 0; j < totCount; j++) {
                result.addData("BED_NO", ((Vector) parm.get(0)).get(i));
                result.addData("MR_NO", ((Vector) parm.get(1)).get(i));
                result.addData("PAT_NAME", ((Vector) parm.get(2)).get(i));
                result.addData("LINK_MAIN_FLG", ((Vector) parm.get(3)).get(i));
                result.addData("LINK_NO", ((Vector) parm.get(4)).get(i));
                result.addData("ORDER_DESC", ((Vector) parm.get(5)).get(i));
                result.addData("QTY", ((Vector) parm.get(6)).get(i));
                result.addData("UNIT_CODE", ((Vector) parm.get(7)).get(i));
                result.addData("ORDER_CODE", ((Vector) parm.get(8)).get(i));
                result.addData("ORDER_NO", ((Vector) parm.get(9)).get(i));
                result.addData("ORDER_SEQ", ((Vector) parm.get(10)).get(i));
                result.addData("START_DTTM", ((Vector) parm.get(11)).get(i));
                result.addData("SEQ_NO", seqNo);

                result.addData("FREQ", ((Vector) parm.get(12)).get(i));
                result.addData("STATION_DESC", ((Vector) parm.get(13)).get(i));
                result.addData("SEX", ((Vector) parm.get(14)).get(i));
                result.addData("AGE", ((Vector) parm.get(15)).get(i));
                result.addData("RX_TYPE", ((Vector) parm.get(16)).get(i));
                result.addData("ROUTE", ((Vector) parm.get(17)).get(i));
                result.addData("DOCTOR", ((Vector) parm.get(18)).get(i));
//                result.addData("DOSAGE_QTY", ((Vector) parm.get(19)).get(i));
//                result.addData("DOSAGE_UNIT", ((Vector) parm.get(20)).get(i));
                result.addData("DOSAGE_QTY", resultDspnCnt.getDouble("DOSAGE_QTY", j));//modify by wanglong 20140402
                result.addData("DOSAGE_UNIT", resultDspnCnt.getValue("DOSAGE_UNIT", j));
                // �������
                result.addData("CLASSIFY_TYPE", ((Vector) parm.get(21)).get(i));
                // ����case_no
                result.addData("CASE_NO", ((Vector) parm.get(22)).get(i));
                // ����END_DTTM
                result.addData("END_DTTM", ((Vector) parm.get(23)).get(i));
                // shibl 20120415 add
                barCode = resultDspnCnt.getValue("BAR_CODE", j);
                // barCode
                result.addData("BAR_CODE", barCode);
                result.addData("ORDER_DATE", resultDspnCnt.getValue(
                        "ORDER_DATE", j));
                result.addData("ORDER_DATETIME", resultDspnCnt.getValue(
                        "ORDER_DATETIME", j));
                seqNo++;
            }
        }
        return result;
    }

    public String getbedDesc(String bedNo) {
        TParm parm = new TParm(
                TJDODBTool
                        .getInstance()
                        .select(
                                "SELECT A.BED_NO,A.BED_NO_DESC FROM SYS_BED A,ADM_INP B WHERE A.BED_NO=B.BED_NO AND BED_NO = '"
                                        + bedNo + "'"));
        return parm.getValue("BED_NO_DESC", 0);
    }

    /**
     * �������ݿ��������
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    private TParm configParm(TParm parm) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("PRINT_DATAPQ"); i++) {
            TParm parmI = (TParm) parm.getData("PRINT_DATAPQ", i);
            ;
            int rowCount = parmI.getCount("ORDER_DESC");
            int pageCount = 1;
            if (rowCount % 5 == 0)
                pageCount = rowCount / 5;
            else
                pageCount = rowCount / 5 + 1;
            int page = 1;
            for (int j = 0; j < rowCount; j++) {
                if ((j + 1) % 5 == 0) {
                    result.addData("PRINT_DATAPQ", cloneParm(parmI, j - 4, j));
                    ((TParm) result.getData("PRINT_DATAPQ", result
                            .getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
                            page + "/" + pageCount);
                    page++;
                } else if ((j + 1) == rowCount) {
                    result.addData("PRINT_DATAPQ", cloneParm(parmI, rowCount
                            - rowCount % 5, j));
                    ((TParm) result.getData("PRINT_DATAPQ", result
                            .getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
                            page + "/" + pageCount);
                    page++;
                }
            }
        }
        return result;
    }

    private TParm cloneParm(TParm parm, int startIndex, int endIndex) {
        TParm result = new TParm();
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            if (parm.getData(names[i]) instanceof String)
                result.setData(names[i], parm.getData(names[i]));
            else if (parm.getData(names[i]) instanceof Vector) {
                for (int j = startIndex; j <= endIndex; j++)
                    result.addData(names[i], parm.getData(names[i], j));
            }
        }
        return result;
    }

    // ====================luahi modify 2012-2-28 end
    // ================================
    public String getStationDesc(String stationCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT STATION_DESC " + " FROM SYS_STATION "
                        + " WHERE STATION_CODE = '" + stationCode + "'"));
        return parm.getValue("STATION_DESC", 0);
    }

    public String getRouteDesc(String routeCode) {
        if (routeCode.length() == 0)
            return "";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT ROUTE_CHN_DESC " + " FROM SYS_PHAROUTE "
                        + " WHERE ROUTE_CODE = '" + routeCode + "'"));
        return parm.getValue("ROUTE_CHN_DESC", 0);
    }
    
    //add by chenjianxing 20180205
    //סԺ��ʿվ��ӡҽ��ִ�е� ���뷽ʽ��Ϊ��ʾ��������
    //the start
    public String getPumpDesc(String pumpCode) {
    	if (pumpCode.length() == 0)
    		return "";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(
    			" SELECT PUMP_DESC " + " FROM SYS_PUMPTYPE "
    					+ " WHERE PUMP_CODE = '" + pumpCode + "'"));
    	return parm.getValue("PUMP_DESC", 0);
    }
    //the end

    public String getOperatorName(String userID) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT USER_NAME " + " FROM SYS_OPERATOR "
                        + " WHERE USER_ID = '" + userID + "'"));
        return parm.getValue("USER_NAME", 0);
    }

    /**
     * ȡ�õ�λ�ֵ�
     * 
     * @return Map
     */
    public Map getUnitMap() {
        Map map = new HashMap();
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT"));
        for (int i = 0; i < parm.getCount(); i++) {
            map.put(parm.getData("UNIT_CODE", i), parm.getData("UNIT_CHN_DESC",
                    i));
        }
        return map;
    }

    public String[] breakNFixRow(String src, int bre, int fix) {
        return fixRow(breakRow(src, bre), fix);
    }

    public String[] fixRow(String string, int size) {
        Vector splitVector = new Vector();
        int index = 0;
        int separatorCount = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if ("\n".equals(String.valueOf(c))) {
                if (++separatorCount >= size) {
                    splitVector.add(string.substring(index, i));
                    index = i + 1;
                    separatorCount = 0;
                }
            }
        }

        splitVector.add(string.substring(index, string.length()));
        String splitArray[] = new String[splitVector.size()];
        for (int j = 0; j < splitVector.size(); j++)
            splitArray[j] = (String) splitVector.get(j);

        return splitArray;
    }

    public String breakRow(String src, int size) {
        return breakRow(src, size, 0);
    }

    public String breakRow(String src, int size, int shift) {
        StringBuffer tmp = new StringBuffer("");
        tmp.append(space(shift));
        int i = 0;
        int len = 0;
        for (; i < src.length(); i++) {
            char c = src.charAt(i);
            len += getCharSize(c);
            if ("\n".equals(String.valueOf(c))) {
                tmp.append(c);
                tmp.append(space(shift));
                len = 0;
            } else if (size >= len) {
                tmp.append(c);
            } else {
                tmp.append("\n");
                tmp.append(space(shift));
                tmp.append(c);
                len = getCharSize(c);
            }
        }

        return tmp.toString();
    }

    public int getCharSize(char c) {
        return (new String(new char[] { c })).getBytes().length;
    }

    public String space(int n) {
        StringBuffer tmp = new StringBuffer("");
        for (int i = 0; i < n; i++)
            tmp.append(' ');

        return tmp.toString();
    }

    // ��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        // JavaHisDebug.TBuilder();

        // JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("inw\\INWOrderExecMain.x");
    }

    /**
     * ��ʿվִ�в鿴ҽ�� ===========pangben 2011-11-14
     */
    public void OrderExecStuts() {
        StringBuffer sql = new StringBuffer();
        sql
                .append("SELECT A.CASE_NO,B.MR_NO,C.PAT_NAME,B.IPD_NO, A.NS_EXEC_DATE,ADM_DATE  FROM ODI_DSPND A ,ODI_ORDER B,SYS_PATINFO C,ADM_INP D WHERE    A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO=D.CASE_NO(+) AND B.MR_NO=C.MR_NO AND (NS_EXEC_DATE IS NULL OR NS_EXEC_DATE='') ");
        if (null != caseNo && caseNo.length() > 0)
            sql.append(" AND A.CASE_NO ='").append(caseNo).append("'")
                    .toString();
        sql
                .append(
                        " GROUP BY B.MR_NO, C.PAT_NAME, B.IPD_NO, A.NS_EXEC_DATE, ADM_DATE,A.CASE_NO")
                .toString();
        TParm result = new TParm(TJDODBTool.getInstance()
                .select(sql.toString()));
        if (result.getCount() <= 0) {
            messageBox("û����Ҫִ�е�ҽ����Ϣ");
            return;
        }
        TParm parmValue = new TParm();
        String stationSQL = null;
        for (int i = 0; i < result.getCount(); i++) {
            stationSQL = "SELECT STATION_CODE FROM ODI_ORDER WHERE CASE_NO='"
                    + result.getValue("CASE_NO", 0) + "'";
            parmValue = new TParm(TJDODBTool.getInstance().select(stationSQL));
            result.addData("STATION_CODE", parmValue
                    .getValue("STATION_CODE", 0));
        }

        // $$ ============ Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27
        // START==================$$//
        /**
         * client1 = SocketLink.running("", "INWSTATION", "inw"); if
         * (client1.isClose()) { out(client1.getErrText()); return; }
         **/
        StringBuffer message = new StringBuffer();
        String mess = null;
        for (int i = 0; i < result.getCount(); i++) {
            // add by lx
            client1 = SocketLink.running("",
                    result.getValue("STATION_CODE", i), result.getValue(
                            "STATION_CODE", i));

            if (client1.isClose()) {
                out(client1.getErrText());
                return;
            }
            String admDate = StringTool.getString(result.getTimestamp(
                    "ADM_DATE", i), "yyyy/MM/dd HH:mm:ss");
            if (result.getCount() > 1) {
                message.append("CASE_NO:")
                        .append(result.getValue("CASE_NO", i)).append(
                                "|STATION_CODE:").append(
                                result.getValue("STATION_CODE", i)).append(
                                "|MR_NO:").append(result.getValue("MR_NO", i))
                        .append("|PAT_NAME:").append(
                                result.getValue("PAT_NAME", i)).append(
                                "|IPD_NO:")
                        .append(result.getValue("IPD_NO", i)).append(
                                "|ADM_DATE:").append(admDate).append("|")
                        .toString();
                if (result.getCount() - 1 == i)
                    mess = message.toString().substring(0, message.length())
                            .toString();
            } else {
                mess = message.append(message).append("CASE_NO:").append(
                        result.getValue("CASE_NO", i)).append("|STATION_CODE:")
                        .append(result.getValue("STATION_CODE", i)).append(
                                "|MR_NO:").append(result.getValue("MR_NO", i))
                        .append("|PAT_NAME:").append(
                                result.getValue("PAT_NAME", i)).append(
                                "|IPD_NO:")
                        .append(result.getValue("IPD_NO", i)).append(
                                "|ADM_DATE:").append(admDate).toString();
            }

            client1.sendMessage(result.getValue("STATION_CODE", i), mess);
            if (client1 == null) {
                return;
            } else {
                client1.close();
                return;
            }
        }
        /**
         * client1.sendMessage("INWSTATION", mess); if (client1 == null) {
         * return; } else { client1.close(); return; }
         **/
        // $$ ============ Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27
        // END==================$$//
    }

    /**
     * ��ӡ�ಡ��ҽ��ִ�е�
     */
    public void onPrintExe() {
        if (this.ord1All.isSelected() || this.ord2All.isSelected()
                || checkAll.isSelected()) {
            this.messageBox("������ȫ��״̬�´�ӡ��");
            return;
        }
        String orderKind = "";
        if (ord1ST.isSelected()) {
            orderKind = "��ʱ";
        } else if (ord1UD.isSelected()) {
            orderKind = "����";
        } else if (ord1DS.isSelected()) {
            orderKind = "��Ժ��ҩ";
        } else if (ord1IG.isSelected()) {
            orderKind = "��ҩ��Ƭ";
        }
        String orderType = "";
        if (ord2PHA.isSelected()) {
            orderType = "ҩ��";
        } else if (ord2PL.isSelected()) {
            orderType = "����";
        } else if (ord2ENT.isSelected()) {
            orderType = "����";
        }
        String orderExe = "";
        if (this.checkYES.isSelected()) {
            orderExe = "��ִ��";
        } else if (checkNO.isSelected()) {
            orderExe = "δִ��";
        }
        TParm tableDate = masterTbl.getParmValue();
        String caseNo = "", orderNo = "", orderSeq = "", startDttm = "", endDttm = "";
        // ͨ��CASE_NO��ORDER_NO��ORDER_SEQ��ODI_DSPND�ж�λ����ϸ��
        Map map = new HashMap();
        TParm printData = new TParm();
        int count = 0;
        for (int i = 0; i < tableDate.getCount(); i++) {
            boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 16));//modify 15���16 machao
            if (!prtFlg)
                continue;
            if (map.get(tableDate.getValue("CASE_NO", i)) != null) {
                printData.addData("BED_NO", "");
                // printData.addData("MR_NO", "");
                printData.addData("PAT_NAME", "");
                printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
            } else if (map.get(tableDate.getValue("CASE_NO", i)) == null
                    && i == 0) {
                printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
                printData.addData("BED_NO", tableDate.getValue("BED_NO", i)); // =========
                                                                                // chenxi
                                                                                // modify
                                                                                // 20130408
                printData
                        .addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
            } else {
                printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
                printData.addData("BED_NO", " ");
                // printData.addData("MR_NO", "������");
                printData.addData("PAT_NAME", " ");
                printData.addData("LINK_NO", " ");
                printData.addData("ORDER_DESC", " ");
                // printData.addData("ORDER_TIME", " ");
                printData.addData("MEDI_QTY", " ");
                printData.addData("MEDI_UNIT", " ");
                printData.addData("FREQ_CODE", "");
                printData.addData("ROUTE_CODE", " ");
                //add by chenhj
                printData.addData("INFLUTION_RATE", " ");
                
                printData.addData("DC_DATE", " ");
                printData.addData("NS_EXEC_DATE", " ");
                printData.addData("DR_NOTE", " ");
                printData.addData("NS_EXEC_CODE", " ");
                printData.addData("DISPENSE_FLG", " ");
                count++;
                printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
                printData.addData("BED_NO", this.getNewbedDesc(tableDate
                        .getValue("CASE_NO", i)));
                // printData.addData("MR_NO", tableDate.getValue("MR_NO", i));
                printData
                        .addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
            }
            map.put(tableDate.getValue("CASE_NO", i), tableDate.getValue(
                    "CASE_NO", i));
            printData.addData("LINK_NO", tableDate.getValue("LINK_NO", i));
            printData.addData("ORDER_DESC", tableDate.getValue(
                    "ORDER_DESC_AND_SPECIFICATION", i));
            // printData.addData("ORDER_TIME", tableDate.getValue("START_DTTM",
            // i)
            // .substring(4, 6)
            // + "/"
            // + tableDate.getValue("START_DTTM", i).substring(6, 8)
            // + " "
            // + tableDate.getValue("START_DTTM", i).substring(8, 10)
            // + ":"
            // + tableDate.getValue("START_DTTM", i).substring(10, 12)
            // + " "
            // + tableDate.getValue("END_DTTM", i).substring(4, 6)
            // + "/"
            // + tableDate.getValue("END_DTTM", i).substring(6, 8)
            // + " "
            // + tableDate.getValue("END_DTTM", i).substring(8, 10)
            // + ":"
            // + tableDate.getValue("END_DTTM", i).substring(10, 12));
            printData.addData("MEDI_QTY", tableDate.getValue("MEDI_QTY", i));
            printData.addData("MEDI_UNIT", getUnit(tableDate.getValue(
                    "MEDI_UNIT", i)));
            printData.addData("FREQ_CODE", getFreqData(
                    tableDate.getValue("FREQ_CODE", i)).getValue(
                    "FREQ_CHN_DESC", 0));
            printData.addData("ROUTE_CODE", OrderUtil.getInstance().getRoute(
                    tableDate.getValue("ROUTE_CODE", i)));
            //add by chenhj 
            DecimalFormat df = new DecimalFormat("0.000#"); 
            Double IR= Double.parseDouble(tableDate.getValue("INFLUTION_RATE", i));
            printData.addData("INFLUTION_RATE",df.format(IR));
            printData.addData("DC_DATE", tableDate.getValue("DC_DATE", i)
                    .equals("") ? " " : tableDate.getValue("DC_DATE", i)
                    .replaceAll("-", "/").substring(5, 16));
            printData.addData("NS_EXEC_DATE", tableDate.getValue(
                    "NS_EXEC_DATE", i).equals("") ? " "
                    : tableDate.getValue("NS_EXEC_DATE", i)
                            .replaceAll("-", "/").substring(5, 10)
                            + " "
                            + (tableDate.getValue("NS_EXEC_DATE_TIME", i)
                                    .equals("") ? " " : tableDate.getValue(
                                    "NS_EXEC_DATE_TIME", i).substring(0, 5)));
            printData.addData("DR_NOTE", tableDate.getValue("DR_NOTE", i));
            printData.addData("NS_EXEC_CODE", getOperatorName(tableDate
                    .getValue("NS_EXEC_CODE", i)));
            printData.addData("DISPENSE_FLG", "Y".equals(tableDate.getValue("DISPENSE_FLG", i))?"��":"��"); //��ʿִ��"��"
            count++;
        }
        printData.setCount(count);
        TParm GprintParm = new TParm();
        if (count <= 0) {
            this.messageBox("�޴�ӡ���ݣ�");
            return;
        } else {
            Map patMap = new HashMap();
            Map pat = groupByPatParm(printData);
            // Iterator it = pat.values().iterator();
            for (int j = 0; j < tableDate.getCount(); j++) {
                boolean prtFlg = TypeTool.getBoolean(masterTbl
                        .getValueAt(j, 16));//modify  machao 15���16
                if (!prtFlg)
                    continue;
                if (patMap.get(tableDate.getValue("CASE_NO", j)) == null) {
                    if (pat.get(tableDate.getValue("CASE_NO", j)) != null) {
                        TParm patParm = (TParm) pat.get(tableDate.getValue(
                                "CASE_NO", j));
                        int rows = patParm.getCount();
                        for (int i = 0; i < rows; i++) {
                            GprintParm.addData("BED_NO", patParm.getValue(
                                    "BED_NO", i));
                            GprintParm.addData("PAT_NAME", patParm.getValue(
                                    "PAT_NAME", i));
                            GprintParm.addData("LINK_NO", patParm.getValue(
                                    "LINK_NO", i));
                            GprintParm.addData("ORDER_DESC", patParm.getValue(
                                    "ORDER_DESC", i));
                            // GprintParm.addData("ORDER_TIME",
                            // patParm.getValue(
                            // "ORDER_TIME", i));
                            GprintParm.addData("MEDI_QTY", patParm.getValue(
                                    "MEDI_QTY", i));
                            GprintParm.addData("MEDI_UNIT", patParm.getValue(
                                    "MEDI_UNIT", i));
                            GprintParm.addData("FREQ_CODE", patParm.getValue(
                                    "FREQ_CODE", i));
                            GprintParm.addData("ROUTE_CODE", patParm.getValue(
                                    "ROUTE_CODE", i));
                            //add by chenhj
                            GprintParm.addData("INFLUTION_RATE", patParm.getValue(
                                    "INFLUTION_RATE", i));
                            GprintParm.addData("DISPENSE_FLG", patParm.getValue(
                                    "DISPENSE_FLG", i));
                            GprintParm.addData("NS_EXEC_DATE", patParm
                                    .getValue("NS_EXEC_DATE", i));
                            GprintParm.addData("DC_DATE", patParm.getValue(
                                    "DC_DATE", i));
                            GprintParm.addData("DR_NOTE", patParm.getValue(
                                    "DR_NOTE", i));
                            GprintParm.addData("NS_EXEC_CODE", patParm
                                    .getValue("NS_EXEC_CODE", i));
                        }
                    }
                }
                patMap.put(tableDate.getValue("CASE_NO", j), tableDate
                        .getValue("CASE_NO", j));
            }
        }
        GprintParm.setCount(count);
        GprintParm.addData("SYSTEM", "COLUMNS", "BED_NO");
        // printData.addData("SYSTEM", "COLUMNS", "MR_NO");
        GprintParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        GprintParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
        GprintParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        // GprintParm.addData("SYSTEM", "COLUMNS", "ORDER_TIME");
        GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
        GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_UNIT");
        GprintParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
        GprintParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
        //add by chenhj
        GprintParm.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");
       GprintParm.addData("SYSTEM", "COLUMNS", "DISPENSE_FLG");
        GprintParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
        GprintParm.addData("SYSTEM", "COLUMNS", "DC_DATE");
        GprintParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
        GprintParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
        TParm printParm = new TParm();
        printParm.setData("TITLE", "TEXT", "ҽ��ִ�е�");
        printParm.setData("ORDER_KIND", "TEXT", "ҽ�����:" + orderKind);
        printParm.setData("ORDER_TYPE", "TEXT", "ҽ������:" + orderType);
        printParm.setData("ORDER_EXE", "TEXT", "ִ��ȷ��:" + orderExe);
        printParm.setData("STATION_CODE", "TEXT", "������"
                + getStationDesc(this.getValueString("INW_STATION_CODE")));
        printParm.setData("DATE", "TEXT", "ִ��ʱ��:"
                + this.getValueString("from_Date").replaceAll("-", "/")
                        .substring(0, 10)
                + " "
                + this.getValueString("from_Time")
                + "��"
                + this.getValueString("to_Date").replaceAll("-", "/")
                        .substring(0, 10) + " "
                + this.getValueString("to_Time"));
        printParm.setData("PRINT_DATE", "TEXT", "��ӡʱ��:"
                + StringTool.getString(SystemTool.getInstance().getDate(),
                        "yyyy/MM/dd HH:mm:ss"));
        printParm.setData("TABLE", GprintParm.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\inw\\inwExeNewPrint.jhw",
                printParm);
    }

    /**
     * ������������
     * 
     * @param parm
     * @return
     */
    public Map groupByPatParm(TParm parm) {
        Map result = new HashMap();
        if (parm == null) {
            return null;
        }
        int count = parm.getCount();
        if (count < 1) {
            return null;
        }
        TParm temp = new TParm();
        String[] names = parm.getNames();
        if (names == null) {
            return null;
        }
        if (names.length < 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String name : names) {
            sb.append(name).append(";");
        }
        try {
            sb.replace(sb.lastIndexOf(";"), sb.length(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        TParm tranParm = new TParm();
        for (int i = 0; i < count; i++) {
            String orderNo = parm.getValue("CASE_NO", i);
            if (result.get(orderNo) == null) {
                temp = new TParm();
                temp.addRowData(parm, i, sb.toString());
                result.put(orderNo, temp);
            } else {
                tranParm = (TParm) result.get(orderNo);
                tranParm.addRowData(parm, i, sb.toString());
                result.put(orderNo, tranParm);
            }
        }
        return result;
    }

    /**
     * ȡ��Ƶ������
     * 
     * @param freqCode
     * @return
     */
    public TParm getFreqData(String freqCode) {
        TParm parm = new TParm(getDBTool().select(
                " SELECT FREQ_CHN_DESC,FREQ_TIMES,DESCRIPTION "
                        + " FROM SYS_PHAFREQ " + " WHERE FREQ_CODE='"
                        + freqCode + "'"));
        return parm;
    }

    /**
     * ���鱨��
     */
    public void onCheckrep() {
        String mrNo = "";
        // �õ�TabbedPane�ؼ�
        TTabbedPane tabPane = (TTabbedPane) this
                .callFunction("UI|TablePane|getThis");
        int selType = tabPane.getSelectedIndex();
        // 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
        if (selType == 0) {
            mrNo = this.getValueString("MR_NO");
        } else if (selType == 1) {
            mrNo = this.getValueString("MR_NOOUT");
        }
        if (mrNo.equals(""))
            return;
        SystemTool.getInstance().OpenLisWeb(mrNo);
    }

    /**
     * ��鱨��
     */
    public void onTestrep() {
        String mrNo = "";
        // �õ�TabbedPane�ؼ�
        TTabbedPane tabPane = (TTabbedPane) this
                .callFunction("UI|TablePane|getThis");
        int selType = tabPane.getSelectedIndex();
        // 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
        if (selType == 0) {
            mrNo = this.getValueString("MR_NO");
        } else if (selType == 1) {
            mrNo = this.getValueString("MR_NOOUT");
        }
        if (mrNo.equals(""))
            return;
        SystemTool.getInstance().OpenRisWeb(mrNo);
    }

    boolean sortClicked = false;

    /**
     * �����������������
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        // System.out.println("==========�����¼�===========");
        // System.out.println("++��ǰ���++"+masterTbl.getParmValue());
        // TParm tableDate = masterTbl.getParmValue();
        // System.out.println("===tableDate����ǰ==="+tableDate);
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // System.out.println("+i+"+i);
                // System.out.println("+i+"+j);
                // �������򷽷�;
                // ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);

                // �����parmֵһ��,
                // 1.ȡparamwֵ;
                TParm tableData = masterTbl.getParmValue();
                // 2.ת�� vector����, ��vector ;
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                // System.out.println("==strNames=="+strNames);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                // System.out.println("==vct=="+vct);

                // 3.���ݵ������,��vector����
                // System.out.println("sortColumn===="+sortColumn);
                // ������������;
                String tblColumnName = masterTbl.getParmMap(sortColumn);
                // ת��parm�е���
                int col = tranParmColIndex(columnName, tblColumnName);
                // System.out.println("==col=="+col);

                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames);

                // getTMenuItem("save").setEnabled(false);
            }
        });
    }

    /**
     * vectoryת��param
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames) {
        //
        // System.out.println("===vectorTable==="+vectorTable);
        // ������->��
        // System.out.println("========names==========="+columnNames);
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // ������;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        masterTbl.setParmValue(parmTable);
        // System.out.println("�����===="+parmTable);

    }

    /**
     * �õ��˵�
     * 
     * @param tag
     *            String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag) {
        return (TMenuItem) this.getComponent(tag);
    }

    /**
     * �õ� Vector ֵ
     * 
     * @param group
     *            String ����
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int �������
     * @return Vector
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
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {

            if (tmp.equalsIgnoreCase(tblColumnName)) {
                // System.out.println("tmp���");
                return index;
            }
            index++;
        }

        return index;
    }

    /**
     * ������Һ����
     */
    public void GeneratPhaBarcode() {
        TParm dspndParm = new TParm();
        TParm tablValue = masterTbl.getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int rowCount = masterTbl.getRowCount();
        int count = 0;
        TParm linkparm = new TParm();
        Map mapBarCode = new HashMap();
        Map linkmap = new HashMap();
        String caseNo = "";
        String orderNo = "";
        String orderSeq = "";
        String cat1Type = "";
        String orderCode = "";
        String startDttm = "";
        String endDttm = "";
        String orderDesc = "";
        String Dosetype = "";
        String barCode = "";
        String linkNo = "";
        String dspnKind = "";
        String linkStr = "";
        String dcFlg = "";
        String routeCode = "";
        // ���������
        for (int i = 0; i < rowCount; i++) {
            caseNo = (String) tablValue.getData("CASE_NO", i);
            orderNo = (String) tablValue.getData("ORDER_NO", i);
            orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
            cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
            orderCode = (String) tablValue.getData("ORDER_CODE", i);
            startDttm = (String) tablValue.getData("START_DTTM", i);
            endDttm = (String) tablValue.getData("END_DTTM", i);
            orderDesc = (String) tablValue.getData(
                    "ORDER_DESC_AND_SPECIFICATION", i);
            Dosetype = "";
            linkNo = tablValue.getValue("LINK_NO", i);
            dspnKind = (String) tablValue.getData("DSPN_KIND", i);
            routeCode = (String) tablValue.getData("ROUTE_CODE", i);
            //if (TypeTool.getBoolean(masterTbl.getValueAt(i, 14))) {
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 15))) {//modify by machao 20170119 14��15
                if (cat1Type.equals("PHA")) {
                    if (routeCode.equals("")) {
                        this.messageBox(orderDesc + "�÷�Ϊ�գ������������룡");
                        return;
                    }
                    Dosetype = SysPhaBarTool.getInstance().getClassifyType(
                            routeCode);
//                    if (!Dosetype.equals("I") && !Dosetype.equals("F")) {
//                        this.messageBox(orderDesc + "����������Σ������������룡");
//                        return;
//                    }
                    // String checksql = "SELECT BAR_CODE FROM ODI_DSPND "
                    // + "WHERE CASE_NO='"
                    // + caseNo
                    // + "' AND ORDER_NO='"
                    // + orderNo
                    // + "' AND ORDER_SEQ='"
                    // + orderSeq
                    // + "' "
                    // +
                    // " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
                    // + startDttm
                    // + "','YYYYMMDDHH24MISS') "
                    // +
                    // " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
                    // + endDttm
                    // + "','YYYYMMDDHH24MISS') AND BAR_CODE IS NOT NULL ";
                    // TParm check = new TParm(TJDODBTool.getInstance()
                    // .select(checksql));
                    // //������ݴ��ڣ�ѯ���Ƿ���
                    // if (check.getCount("BAR_CODE") > 0) {
                    // continue;
                    // // switch (this.messageBox("��ʾ��Ϣ",
                    // // orderDesc+"���������Ѵ��ڣ��Ƿ��������ɣ�", this.YES_NO_OPTION)) {
                    // // case 0: //����
                    // // break;
                    // // case 1: //������
                    // // continue;
                    // // }
                    // }
                    // �ж�����ҽ����һ��һ�룩
                    if (!linkNo.equals("")) {
                        linkStr = caseNo + orderNo + dspnKind + startDttm
                                + linkNo;
                        if (linkmap.get(linkStr) == null) {
                            // ȡ��
                            barCode = SysPhaBarTool.getInstance().getBarCode();
                            mapBarCode.put(linkStr, barCode);
                        }
                        linkmap.put(linkStr, linkStr);
                        // ��ѯϸ���SQL
                        String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
                                + "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,BAR_CODE FROM ODI_DSPND "
                                + "WHERE CASE_NO='"
                                + caseNo
                                + "' AND ORDER_NO='"
                                + orderNo
                                + "' AND ORDER_SEQ='"
                                + orderSeq
                                + "' "
                                + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
                                + startDttm
                                + "','YYYYMMDDHH24MISS') "
                                + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
                                + endDttm
                                + "','YYYYMMDDHH24MISS')"
                                + " ORDER BY ORDER_DATE||ORDER_DATETIME";
                        // ����ϸ���TDS,����������
                        TParm result = new TParm(TJDODBTool.getInstance()
                                .select(sql));
                        if (result.getCount() <= 0)
                            continue;
                        for (int j = 0; j < result.getCount(); j++) {
                            if (!result.getValue("DC_DATE", j).equals(""))
                                continue;
                            if (!result.getValue("BAR_CODE", j).equals(""))
                                continue;
                            dspndParm.addData("CASE_NO", result.getValue(
                                    "CASE_NO", j));
                            dspndParm.addData("ORDER_NO", result.getValue(
                                    "ORDER_NO", j));
                            dspndParm.addData("ORDER_SEQ", result.getValue(
                                    "ORDER_SEQ", j));
                            dspndParm.addData("ORDER_DATE", result.getValue(
                                    "ORDER_DATE", j));
                            dspndParm.addData("ORDER_DATETIME", result
                                    .getValue("ORDER_DATETIME", j));
                            dspndParm.addData("BAR_CODE", (String) mapBarCode
                                    .get(linkStr)
                                    + j);
                            dspndParm.addData("OPT_USER", Operator.getID());
                            dspndParm.addData("OPT_DATE", now);
                            dspndParm.addData("OPT_TERM", Operator.getIP());
                            count++;
                        }
                    } else {
                        // ȡ��
                        barCode = SysPhaBarTool.getInstance().getBarCode();
                        // ��ѯϸ���SQL
                        String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
                                + "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL,BAR_CODE FROM ODI_DSPND "
                                + "WHERE CASE_NO='"
                                + caseNo
                                + "' AND ORDER_NO='"
                                + orderNo
                                + "' AND ORDER_SEQ='"
                                + orderSeq
                                + "' "
                                + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
                                + startDttm
                                + "','YYYYMMDDHH24MISS') "
                                + " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
                                + endDttm
                                + "','YYYYMMDDHH24MISS')"
                                + " ORDER BY ORDER_DATE||ORDER_DATETIME";

                        // ����ϸ���TDS,����������
                        TParm result = new TParm(TJDODBTool.getInstance()
                                .select(sql));
                        if (result.getCount() <= 0)
                            continue;
                        for (int j = 0; j < result.getCount(); j++) {
                            if (!result.getValue("DC_DATE", j).equals(""))
                                continue;
                            if (!result.getValue("BAR_CODE", j).equals(""))
                                continue;
                            dspndParm.addData("CASE_NO", result.getValue(
                                    "CASE_NO", j));
                            dspndParm.addData("ORDER_NO", result.getValue(
                                    "ORDER_NO", j));
                            dspndParm.addData("ORDER_SEQ", result.getValue(
                                    "ORDER_SEQ", j));
                            dspndParm.addData("ORDER_DATE", result.getValue(
                                    "ORDER_DATE", j));
                            dspndParm.addData("ORDER_DATETIME", result
                                    .getValue("ORDER_DATETIME", j));
                            dspndParm.addData("BAR_CODE", barCode + j);
                            dspndParm.addData("OPT_USER", Operator.getID());
                            dspndParm.addData("OPT_DATE", now);
                            dspndParm.addData("OPT_TERM", Operator.getIP());
                            count++;
                        }
                    }
                }
            }
        }
        dspndParm.setCount(count);
        if (count > 0) {
            TParm result = InwOrderExecTool.getInstance().GeneratIFBarcode(
                    dspndParm);
            if (result.getErrCode() < 0) {
                this.messageBox("��������ʧ�ܣ�");
                return;
            }
            this.messageBox("��������ɹ���");
        } else {
            this.messageBox("�������������ҩƷ");
        }
    }

    /**
     * 
     * 
     */
    public String getNewbedDesc(String caseNo) {
        String bed = "";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE CASE_NO = '"
                        + caseNo + "' AND BED_OCCU_FLG = 'N'"));
        if (parm.getCount() > 0) {
            bed = parm.getValue("BED_NO_DESC", 0);
        }
        return bed;
    }

    /**
     * Ƶ��ɸѡ
     */
    public void FreqCodeSelect() {
        masterTbl.acceptText();
        TParm parm = masterTbl.getParmValue();
        String freqCode = this.getValueString("FREQ_CODETAG");
        // ���������
        for (int i = parm.getCount("CASE_NO") - 1; i >= 0; i--)
            if (!freqCode.equals(parm.getValue("FREQ_CODE", i)))
                parm.removeRow(i);
        masterTbl.setParmValue(parm);
    }

    /**
     * ����DCҽ�����������ܳ����û�������������
     * 
     * @param parm
     * @return
     */
    public TParm checkDCQtyIsLess(TParm parm) {// add by wanglong 20130527
        String sumCountSql = "SELECT SUM(DOSAGE_QTY) COUNT FROM IBS_ORDD WHERE CASE_NO = '#' AND ORDER_CODE = '#' GROUP BY CASE_NO, ORDER_CODE";
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            if (!parm.getValue("ORDER_CODE", i).equals(
                    parm.getValue("ORDERSET_CODE", i))) {
                continue;
            }
            if (parm.getValue("CAT1_TYPE", i).equals("PHA")) {// add by wanglong
                                                                // 20130619
                continue;
            }
            String caseNo = parm.getValue("CASE_NO", i);
            String orderCode = parm.getValue("ORDER_CODE", i);
            String orderNo = parm.getValue("ORDER_NO", i);
            String startDttm = parm.getValue("START_DTTM", i);
            String sql = sumCountSql.replaceFirst("#", caseNo).replaceFirst(
                    "#", orderCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() != 0) {
                this.messageBox("��ѯ��DCҽ��������ʹ�ù���������ʧ��");
                return null;
            }
            if (result.getInt("COUNT", 0) < parm.getInt("DOSAGE_QTY", i)) {
                String patName = PatTool.getInstance().getNameForMrno(
                        parm.getValue("MR_NO", i));
                this.messageBox(parm.getValue("ORDER_DESC", i) + "��"
                        + parm.getValue("ORDER_CODE", i) + "��ȡ��ִ�е��������ڲ�����"
                        + patName + "��ִ�й�������������ǰ��������ȡ��");
                return null;
            }
        }
        return parm;
    }
    /**
	 * ����·������
	 * yanjing 20140919
	 */
    public void onChangeSchd(){
    	String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' AND CLNCPATH_CODE IS NOT NULL ";//��ѯ�û����Ƿ�����ٴ�·��
    	TParm parm = new TParm (TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){//�����ٴ�·��
    		String clncPathCode = parm.getValue("CLNCPATH_CODE", 0);
    		//���ø���ʱ�̵Ľ���
    		TParm sendParm = new TParm();
            sendParm.setData("CASE_NO", caseNo); 
            sendParm.setData("CLNCPATH_CODE", clncPathCode);
            TParm result = (TParm) this.openDialog(
                    "%ROOT%\\config\\odi\\ODIintoDuration.x", sendParm);
    	}else{
    		this.messageBox("�������ٴ�·�������ɸ���ʱ�̡�");
    		return;
    	}
    	
    }
    
    //����NIS����
    public void onNis(){
    	String nisIp = "";
    	nisIp += "http://"+this.getConfig().getSystemValue("NISIP")+
    			 "/Nis/m2/user/login?m2Login_login="+Operator.getID()+
    			 "&linkTo=NursingPlan/start&encid="+caseNo+
    			 "&patientid="+mrNo+
    			 "&encType=I&station="+stationCode;
    	SystemTool.getInstance().OpenIE(nisIp);	
    	
    }
    
    /**
     * Ѫ�Ǳ���
     */
    public void getXTReport(){
    	SystemTool.getInstance().OpenTnbWeb(this.getMrNo());
    }
    
    /**
     * �ĵ籨��
     */
    public void getPdfReport(){
    	String sql = "SELECT  DISTINCT MED_APPLY_NO  FROM ODI_ORDER WHERE CASE_NO = '"+caseNo+"' AND ORDER_CAT1_CODE = 'ECC'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	System.out.println("111111"+result);
    	System.out.println("222222"+sql);
    	if(result.getCount() <= 0){
    		this.messageBox("�ò���û���ĵ���ҽ��");
    		return;
    	}
    	TParm parm = new TParm();
    	String opbBookNo = "";
    	for(int i = 0; i < result.getCount(); i++){
    		opbBookNo += "'"+result.getValue("MED_APPLY_NO", i)+"'"+",";
    	}
    	parm.setData("OPE_BOOK_NO",opbBookNo.substring(0, opbBookNo.length()-1));
    	parm.setData("CASE_NO",caseNo);
    	parm.setData("MR_NO",this.getMrNo());
    	parm.setData("TYPE","3");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
	 * ���鱨��
	 */
	public void onLis() {
		SystemTool.getInstance().OpenLisWeb(this.getMrNo());
	}

	/**
	 * ��鱨��
	 */
	public void onRis() {
		SystemTool.getInstance().OpenRisWeb(this.getMrNo());
	}
	
	 /**
     * ��֢�໤
     */
    public void getCCEmrData(){
    	TParm parm = new TParm();
    	parm.setData("CASE_NO",this.getCaseNo());
    	parm.setData("TYPE","1");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
     * ���鲡��
     */
    public void getOpeMrData(){
    	TParm parm = new TParm();
    	parm.setData("CASE_NO",this.getCaseNo());
    	parm.setData("TYPE","2");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
}
