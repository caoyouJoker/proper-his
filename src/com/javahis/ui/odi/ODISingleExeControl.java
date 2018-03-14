package com.javahis.ui.odi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;  
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;  
import java.util.Map;     

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import jdo.adm.ADMInpTool;
import jdo.cdss.SysUtil;
import jdo.hl7.Hl7Communications;
import jdo.iva.IVADeploymentTool;
import jdo.nss.NSSEnteralNutritionTool;
import jdo.odi.ODISingleExeTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.time.DateUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;  
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.opd.CDSSStationDosntWork;
import com.javahis.ui.opd.CDSSStationDrools;
import com.javahis.ui.spc.util.ElectronicTagUtil;
import com.javahis.util.OdiUtil;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiMultiPanel;
import com.tiis.ui.TiPanel;

/**
 * <p>
 * Title: �ж�����ִ��
 * </p>
 * 
 * <p>
 * Description: �ж�����ִ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 *
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class ODISingleExeControl extends TControl {

	private static final String TPanel = null;
	// ����������Ϣ
	TParm patInfo = new TParm();
	TPanel tiPanel4;
	TPanel tiPanel0;
	TiPanel tiPanel2 = new TiPanel();
	JScrollPane jScrollPane1 = new JScrollPane();
	GridLayout gridLayout1 = new GridLayout();
	TiPanel tiPanel3 = new TiPanel();
	String SQL = "";
    TParm ctrlParm = new TParm();// �����龫ҩ��add by wanglong 20130603
    boolean enFlg = false; // add by wangbin 20150421 ����Ӫ������ִ��ע��
    private String pkOrderCode; // add by wangb 2016/10/10 һ���ٴ�PK��Ѫҽ������
    private CDSSStationDrools odiSingleExeDrools = new CDSSStationDosntWork();//����ҩƷ��ʾ
//    public TTable table = (TTable) this.getComponent("tableM");
    
	public void onInit() {
		super.onInit();
		onSel("");
		// getTextField("MR_NO").grabFocus();
		// nowDate();
		//20151103 wangjc add start ��Ѫ������Ӧ
		String sql = "SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'TRANSFUSION_REACTION'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TTextFormat transfusionReaction = (TTextFormat) getComponent("TRANSFUSION_REACTION");
		try {
			transfusionReaction.setHorizontalAlignment(2);
			transfusionReaction.setPopupMenuHeader("����,100;����,120");
			transfusionReaction.setPopupMenuWidth(300);
			transfusionReaction.setPopupMenuHeight(300);
			transfusionReaction.setFormatType("combo");
			transfusionReaction.setShowColumnList("NAME");
			transfusionReaction.setValueColumn("ID");
			transfusionReaction.setPopupMenuData(result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.getTable("tableM").addItem("TRANSFUSION_REACTION", transfusionReaction);//20151103 wangjc add
//		this.getTable("tableM").getTable().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		//20151103 wangjc add end ��Ѫ������Ӧ
		onControl();
		// R1.setSelected(true);
		setExeDate();
		((TTextField) getComponent("MR_NO")).grabFocus();
		callFunction("UI|tableM|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");
		callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onExeQuery");
		// callFunction("UI|BAR_CODE|addEventListener",
		// TTextFieldEvent.KEY_RELEASED, this, "onSaveExe");
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		panelInit();
		
		if (this.getPopedem("PIC")) {
			pkOrderCode = TConfig.getSystemValue("PIC_PK_ORDER_CODE");
		}
		
		//����ҩƷ��ʾ
		if (CDSSStationDrools.isCdssOn(Operator.getRegion())) {
		odiSingleExeDrools = new ODISingleExeDrools(this);
		}
	}

	/**
	 * ��ʼ�����
	 */
	public void panelInit() {
		tiPanel2.setBounds(new Rectangle(2, 2, 1330, 204));
		tiPanel2.setBorder(null);
		tiPanel2.setLayout(null);
		tiPanel4 = ((TPanel) getComponent("tPanel_1"));
		tiPanel4.add(tiPanel2, null);
		jScrollPane1.setBorder(null);
		jScrollPane1.setBounds(new Rectangle(2, 2, 1331, 200));
		tiPanel2.add(jScrollPane1, null);
		tiPanel3.setBorder(null);
		jScrollPane1.setViewportView(tiPanel3);
	}

	/**
	 * ҽ����ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC1").setValue(order_desc);
		}
	}

	private void setExeDate() {
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		Timestamp yes = StringTool.rollDate(now, -1);
		long timeStart = yes.getTime() - 60 * 60 * 1000;
		long timeEnd = now.getTime() + 60 * 60 * 1000;
		setValue("start_Date", new Timestamp(timeStart));
		setValue("end_Date", new Timestamp(timeEnd));
	}

	private TRadioButton R1, R2, R3, R4;
	private String action = "a";
	ODISingleExeTool tool = new ODISingleExeTool();

	/**
	 * ��ʼ��ʱ��
	 */
	public void onQuery() {
		
        onEnter();
        ctrlParm=new TParm();
        if (((TRadioButton) this.getComponent("R5")).isSelected()) { // add by wanglong 20130603
        	if(Operator.getSpcFlg().equals("N")){
        		this.messageBox("����������û�����ã����ܲ�ѯ");
        		return;
        	}
            // ������
            String caseNo = patInfo.getValue("CASE_NO", 0);
            String barCode = getValueString("BAR_CODE");
            String startDate = getValueString("start_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String endDate = getValueString("end_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String startTime = getValueString("start_Date").replaceAll("[^0-9]", "").substring(8);
            String endTime = getValueString("end_Date").replaceAll("[^0-9]", "").substring(8);
            TParm inParm = new TParm();
            inParm.setData("CASE_NO", caseNo);
            inParm.setData("BAR_CODE", barCode);
            TParm spcParm =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "onQueryDspnDSpc", inParm);
            if (spcParm == null || spcParm.getErrCode() < 0) {
                this.messageBox(spcParm.getErrText());
                return;
            }
            String QUERY_DSPND_SQL =
                    " SELECT CASE WHEN A.NS_EXEC_DATE_REAL IS  NULL THEN 'Y' ELSE 'N' END SEL_FLG,CASE WHEN A.NS_EXEC_DATE_REAL IS NOT NULL THEN 'Y' ELSE 'N' END EXE_FLG,"
                            + "B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS')NS_EXEC_DATE,"
                            + "B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,"
                            + "B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,'#' AS BAR_CODE,"//modify by wanglong 20130609
                            + "A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.ORDERSET_GROUP_NO,B.CAT1_TYPE,"
//                            + "CASE WHEN B.CAT1_TYPE='PHA' THEN A.BAR_CODE ELSE C.MED_APPLY_NO END AS BAR_CODE,"
                            + " B.PUMP_CODE,B.INFLUTION_RATE,"    //modify by wukai 20160602 ��ӱ��뷽ʽ����Һ����
                            + "A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,'#' BARCODE_1,'#' BARCODE_2,'#' BARCODE_3 "
                            
                            + " FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D "
                            + "WHERE B.CASE_NO = '#' "
                            + "  AND B.ORDER_NO = '#' "
                            + "  AND B.ORDER_SEQ = '#' "
//                            + "  AND A.ORDER_DATE = '#' "
//                            + "  AND A.ORDER_DATETIME = '#' "
                            + "  AND A.CASE_NO = B.CASE_NO "
                            + "  AND A.ORDER_NO = B.ORDER_NO "
                            + "  AND A.ORDER_SEQ = B.ORDER_SEQ "
//                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM "
                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN # AND # "
                            + "  AND A.ORDER_DATE BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND A.ORDER_DATETIME BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND (B.ORDERSET_CODE IS NULL OR B.ORDER_CODE = B.ORDERSET_CODE) "
                            + "  AND A.CASE_NO = C.CASE_NO "
                            + "  AND A.ORDER_NO = C.ORDER_NO "
                            + "  AND A.ORDER_SEQ = C.ORDER_SEQ "
                            + "  AND B.ROUTE_CODE=D.ROUTE_CODE(+) "
                            // cat1_type B.CAT1_TYPE
                            // �Ƿ�ִ��
                            + " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
            for (int i = 0; i < spcParm.getCount(); i++) {
                if (!caseNo.equals(spcParm.getValue("CASE_NO", i))) {
                    this.messageBox("����Ų�һ��");
                    return;
                }
                String orderNo = spcParm.getValue("ORDER_NO", i);
                String orderSeq = spcParm.getValue("ORDER_SEQ", i);
//                String orderDate = spcParm.getValue("ORDER_DATE", i);
//                String orderDateTime = spcParm.getValue("ORDER_DATETIME", i);
                String startDttm = spcParm.getValue("START_DTTM", i);
                String endDttm = spcParm.getValue("END_DTTM", i);
                String barCode1 = spcParm.getValue("BARCODE_1", i);
                String barCode2 = spcParm.getValue("BARCODE_2", i);
                String barCode3 = spcParm.getValue("BARCODE_3", i);
                String sql =
                        QUERY_DSPND_SQL.replaceFirst("#", barCode).replaceFirst("#", barCode1)
                                .replaceFirst("#", barCode2).replaceFirst("#", barCode3)
                                .replaceFirst("#", caseNo).replaceFirst("#", orderNo)
                                .replaceFirst("#", orderSeq).replaceFirst("#", startDttm)
                                .replaceFirst("#", endDttm).replaceFirst("#", startDate)
                                .replaceFirst("#", endDate).replaceFirst("#", startTime)
                                .replaceFirst("#", endTime);
                TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//                System.out.println("ODISingleExe result: >>>  " + result);
                if (result.getErrCode() != 0 || result.getCount() < 1) {
                    this.messageBox("�����龫��ҩ���� " + result.getErrText());
                    return;
                }
                ctrlParm.addRowData(result, 0);
            }
            onSetTableValue();
        } 
        //add by yangjj 20150430������Ѫ����
        else if(((TRadioButton)this.getComponent("R_TRAN")).isSelected()){
        	
            //fux modify 20150831  
    		if(getValueString("NEXE").equals("Y")){
    			this.messageBox("��ɨ��Ѫ������");
    			return;     
    		}
        	
        	String startDate = this.getValueString("start_Date").replace(".0", "").replaceAll("[^0-9]", "");
        	String endDate = this.getValueString("end_Date").replace(".0", "").replaceAll("[^0-9]", "");
        	//fux modify 20150831 
        	String sql = " SELECT " +
							" 'Y' AS SEL_FLG, " +
							" A.BLOOD_NO, " +
							" A.BLD_CODE, " +  
							" A.BLOOD_VOL," +
							" A.FACT_VOL, " +
							" A.SUBCAT_CODE, " +
							" A.BLD_TYPE, " +
							" A.RH_FLG, " + 
							" A.SHIT_FLG, " +
							" A.CROSS_MATCH_L, " +
							" A.CROSS_MATCH_S, " +       
							" A.RESULT, " +
//							" A.RECEIVED_DATE AS BLDTRANS_TIME, " + //20170105 wukai modify ѪƷ����ʱ�� -> ��Ѫʱ��
							" A.BLDTRANS_TIME, " + //20170612 lij �Ļ���Ѫʱ��BLDTRANS_TIME��ͣ��ʱ��BLDTRANS_END_TIME
							" A.TRANSFUSION_REACTION, "+//20151103 wangjc add ��Ѫ��Ӧ
							" A.BLDTRANS_END_TIME, " +//20151103 wangjc add ͣ��ʱ��
//							" A.RECHECK_TIME AS BLDTRANS_END_TIME, " + //20170105 wukai modify ѪƷ��ȫ�˲�ʱ�� -> ͣ��ʱ��
							" A.BLDTRANS_USER," +  
							" C.UNIT_CHN_DESC AS UNIT_CODE "+
						" FROM " +
							" BMS_BLOOD A,BMS_BLDSUBCAT B,SYS_UNIT C " +
						" WHERE " +
							" A.MR_NO = '"+getValueString("MR_NO")+"' "+
							" AND A.OUT_USER IS NOT NULL  " +
							" AND A.SUBCAT_CODE = B.SUBCAT_CODE " +  
        					" AND B.UNIT_CODE = C.UNIT_CODE ";  
        	if(((TRadioButton)this.getComponent("ALL")).isSelected()){
        		
        	}else if(((TRadioButton)this.getComponent("YEXE")).isSelected()){
        		if(!"".equals(endDate)) {
        			//modify by wukai 20170105
        			sql += " AND A.BLDTRANS_TIME < TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS') ";
//        			sql += " AND A.RECEIVED_DATE < TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS') ";
        		}
        		if(!"".equals(startDate)){
        			//modify by wukai 20170105
        			sql += " AND A.BLDTRANS_TIME > TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS') ";
//        			sql += " AND A.RECEIVED_DATE > TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS') ";
        		}
        		sql += " AND A.BLDTRANS_USER IS NOT NULL";
        	}else if(((TRadioButton)this.getComponent("NEXE")).isSelected()){
        		sql += " AND A.BLDTRANS_USER IS NULL";
        	}
        	
        	
        	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if(result.getCount()<=0){
            	this.getTable("tableM").removeRowAll();
            	return;
            }
        	this.getTable("tableM").setParmValue(result);
        }
        //��е��
        else if(((TRadioButton)this.getComponent("R_PACK")).isSelected()){
        	String startDate = StringTool.getString(TypeTool
    				.getTimestamp(getValue("start_Date")), "yyyyMMdd")+"000000";
    		String endDate = StringTool.getString(TypeTool
    				.getTimestamp(getValue("end_Date")), "yyyyMMdd")+"235959";
        	String sql = " SELECT A.BARCODE AS BAR_CODE,B.PACK_DESC,A.QTY," +
        	" D.USER_NAME AS CHECK_USER,A.CHECK_DATE"+
        	" FROM INV_SUP_DISPENSED A, INV_PACKM B,SYS_OPERATOR D"+
        	" WHERE A.MR_NO = '"+getValueString("MR_NO")+"'"+
        	" AND A.INV_CODE = B.PACK_CODE"+
        	" AND A.CHECK_USER = D.USER_ID(+)" +
        	" AND A.RECEIVE_USER IS NOT NULL";
           if(((TRadioButton)this.getComponent("YEXE")).isSelected()){
        		if(!"".equals(endDate)&&!"".equals(startDate)){
        			sql += " AND A.CHECK_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
                           " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
        		}
        		sql += " AND A.CHECK_USER IS  NOT NULL ";
        	}else if(((TRadioButton)this.getComponent("NEXE")).isSelected()){
        		sql += " AND A.CHECK_USER IS NULL ";
        	}
        	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if(result.getCount()<=0){
            	this.getTable("tableM").removeRowAll();
            	return;
            }
        	this.getTable("tableM").setParmValue(result);
        }
        else {
            onBarCode("Query");
        }
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		this.clearValue("BAR_CODE");
	}

	public void nowDate() {
		// �õ���ǰʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		String Y = date.toString().substring(0, 4);
		String M = date.toString().substring(5, 7);
		String D = date.toString().substring(8, 10);
		String H = date.toString().substring(11, 13);
		int y = Integer.parseInt(Y);
		int m = Integer.parseInt(M);
		int d = Integer.parseInt(D);
		int h = Integer.parseInt(H);
		String YY = "" + y;
		String MM = "" + m;
		String DD = "" + d;
		String HH = "" + h;
		h += 1;
		if (h < 10) {
			HH = "0" + h;
		}
		if (h > 9 && h < 24) {
			HH = "" + h;
		}
		if (h > 24 || h == 24 || h == 00) {
			if (h == 24) {
				HH = "00";
				d += 1;
				DD = "" + d;
			}
			if (h > 24) {
				HH = "0" + (h - 24);
				d += 1;
				DD = "" + d;
			}
			if (d == 28 && y % 4 != 0) {
				DD = "01";
				MM = "03";
			}
			if (d == 30
					&& (m != 1 || m != 3 || m != 5 || m != 7 || m != 8
							|| m != 10 || m != 12)) {
				DD = "01";
				if (m < 10) {
					MM = "0" + (m + 1);
				}
				if (m > 9 && m <= 12) {
					MM = "" + m;
				}
				if (m > 12) {
					MM = "0" + (m - 12);
					y += 1;
					YY = "" + y;
				}
			}
			if (d == 30
					&& (m == 1 || m == 3 || m == 5 || m == 7 || m == 8
							|| m == 10 || m == 12)) {
				d += 1;
				DD = "" + d;
				if (m < 10) {
					MM = "0" + m;
				}
				if (m > 9 && m <= 12) {
					MM = "" + m;
				}
				if (m > 12) {
					MM = "0" + (m - 12);
					y += 1;
					YY = "" + y;
				}
			}

		}
		if (d == 32) {
			DD = "01";
			MM = "" + (m + 1);
		}
		if (m < 10) {
			MM = "0" + m;
		}
		if (d < 10) {
			DD = "0" + d;
		}

		if (m > 9 && m <= 12) {
			MM = "" + m;
		}
		if (m > 12) {
			MM = "0" + (m - 12);
			YY = "" + (y + 1);
		}

		String end_date = "" + YY + "-" + MM + "-" + DD + " " + HH + ":00:00";
		String start_Date = date.toString().substring(0, 14).replace('-', '/')
				+ "00:00";
		// String Start_Date=StringTool;
		// String End_Date=(Timestamp)end_Date;

		end_date = end_date.replace('-', '/');
		// this.messageBox(start_Date);
		// this.messageBox(end_date);
		this.setValue("start_Date", start_Date);
		this.setValue("end_Date", end_date);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void onControl() {
		this.setValue("QY", Operator.getRegion());
		this.setValue("BQ", Operator.getStation());
		this.setValue("user", Operator.getID());
		callFunction("UI|BQ|onQuery");
		callFunction("UI|user|onQuery");
		// R1 = (TRadioButton)this.getComponent("R1");

	}

	// ��ѯ������Ϣ
	public void onQueryPatInfo() {
		
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			this.setValue("MR_NO", pat.getMrNo());
			mrNo=pat.getMrNo();
		}	
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		
		String stationCode = getValueString("BQ");
		String SQL = ODISingleExeTool.getInstance().queryPatInfo(mrNo, "",
				stationCode);
		patInfo = new TParm(TJDODBTool.getInstance().select(SQL));
		if (patInfo.getCount() <= 0) {
			this.messageBox("û�в�����");
			this.onClear();
			return;
		}
		
		String patName = patInfo.getValue("PAT_NAME", 0) ;
		String stationDesc = patInfo.getValue("STATION_DESC", 0);
		String bedNoDesc = this.patInfo.getValue("BED_NO_DESC", 0);
		
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", patName);
		setValue("SEX_CODE", patInfo.getValue("CHN_DESC", 0));
		setValue("bed_no", patInfo.getValue("BED_NO_DESC", 0));
		setValue("ICD", patInfo.getValue("ICD",0));
		setValue("BLOOD_TYPE", patInfo.getValue("BLOOD_TYPE", 0));
		String age = "0";
		age = OdiUtil.getInstance().showAge((Timestamp)patInfo.getData("BIRTH_DATE", 0),SystemTool.getInstance().getDate());
		setValue("AGE",age);
		
		viewPhoto(mrNo);
		((TTextField) getComponent("BAR_CODE")).grabFocus();
        // onBarCode();//��ѯSQL
		String caseNo = patInfo.getValue("CASE_NO",0);
        TParm inparm = new TParm();
        inparm.setData("CASE_NO", caseNo);
        inparm.setData("MR_NO", mrNo);// add by wanglong 20130607
        inparm.setData("PAT_NAME", patName);
        inparm.setData("STATION_DESC", stationDesc);
        inparm.setData("OPT_TERM", Operator.getIP());// add by wanglong 20130607
        inparm.setData("BED_NO_DESC", bedNoDesc);
        
        if (Operator.getSpcFlg().equals("Y")) {// shibl add ��ҩ����
            TParm result =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "sendElectronicTag",
                                                 inparm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            if (!result.getValue("RESULT").toLowerCase().equals("success")) {
                this.messageBox("��ѯ����������ҵ��ʧ��");
                return;
            }
        } else {// add by wangb 20150525 �������汾�ϲ�
        	// modify by wangb 2015/06/30 ���ӱ�ǩ�ӿ���Ҫ��ȷ��,Ϊ�������Ӵ�������ٶȹ�����ʱ����
//        	TParm result = ODISingleExeTool.getInstance().sendElectronicTag(inparm);
//        	if (result.getErrCode() < 0) {
//				System.out.println("���õ��ӱ�ǩʧ��:" + result.getErrText());
//        		return;
//        	}
//        	sendElectronicTagNew(result);//���õ��ӱ�ǩ�ӿ�
        }
        // sendElectronicTag();//���ýӿ�
        
        
    }

	/**
	 * ��ѯ
	 */
	public void onBarCode(String s) {
		
		//modify by yangjj 20151019 HIMMS7 ɨ�������ѯҽ��ʱȥ��ִ��ʱ������
		String startDate = "";
		String endDate = "";
		if("Query".equals(s)){
			startDate =
                StringTool.getString((Timestamp) getValue("start_Date"), "yyyyMMddHHmmss");
            endDate = StringTool.getString((Timestamp) getValue("end_Date"), "yyyyMMddHHmmss");
		}else{
			startDate = "190001010000";
			endDate = "99990101235959";
		}
		

		
		
		String cat1Type = "";
		String doseType = "";
		if (getValueString("R2").equals("Y")) {
			cat1Type = "PHA";
		}
		if (getValueString("R3").equals("Y")) {
			cat1Type = "LIS','RIS";
		}
		if (getValueString("R4").equals("Y")) {
			cat1Type = "TRT','PLN','OTH";
			if (!this.getValueString("BAR_CODE").equals("")) {
				// panelInit();
				// getTTable("tableM").removeRowAll();
				this.messageBox("���������룡");
				return;
			}
		}
		String isEex = "";
		if (getValueString("ALL").equals("Y")) {
			isEex = "A";
		} else if (getValueString("YEXE").equals("Y")) {
			isEex = "Y";
		} else if (getValueString("NEXE").equals("Y")) {
			isEex = "N";
		}
		String barCode = getValueString("BAR_CODE");
		/**
		 * ��ѯҩ���ڷ����� 20120912 shibl modify
		 * ��000000378016@1205290567,12;1205290567,
		 * 13;1205290567,15;@20120910|090001
		 */
		if (barCode.contains("@")) {
			String[] str = barCode.split("@");
			if (str.length <= 2) {
				this.messageBox("ɨ�����������쳣");
				return;
			}
			String mrNo = str[0];// ������
			String orderdataStr = str[1];// ҽ��������Ϣ
			String orderDateStr = str[2];// ҽ���ʹ�ʱ��
			String orderNo = "";// ������
			String orderSeq = "";// �������
			String orderDate = "";// ҽ������
			String orderDatetime = "";// ҽ��ʱ��
			if (orderDateStr.contains("|")) {
				orderDate = orderDateStr
						.substring(0, orderDateStr.indexOf("|")).trim();
				if (orderDateStr.substring(orderDateStr.indexOf("|"),
						orderDateStr.length()).length() >= 4)
					orderDatetime = orderDateStr
							.substring(orderDateStr.indexOf("|") + 1,
									orderDateStr.length()).substring(0, 4)
							.trim();
			}
			if (orderdataStr.contains(";")) {
				String[] order = orderdataStr.split(";");
				int count = order.length;
				for (int i = 0; i < count; i++) {
					if (order[i].contains(",")) {
						boolean ISud=orderDatetime.startsWith("2355")?false:true;//�Ƿ�Ϊ����(�ݶ�ʱ��2355Ϊ��ʱ)
						orderNo = order[i].substring(0, order[i].indexOf(","))
								.trim();
						orderSeq = order[i].substring(
								order[i].indexOf(",") + 1, order[i].length())
								.trim();
						SQL = ODISingleExeTool.getInstance().queryPatOrderPhaO(
								patInfo.getValue("CASE_NO", 0), startDate,
								endDate, cat1Type, orderNo, orderSeq,
								orderDate, orderDatetime, isEex,ISud);
						onSetTableValue();
					}
				}
			}
		} else if (barCode.startsWith("0")) { // add by wangbin 20150421 ����Ӫ������ִ��
			// ����Ӫ������ִ��ע��
			enFlg = true;
			TParm parm = new TParm();
			parm.setData("START_DATE", startDate.substring(0, 8));
			parm.setData("END_DATE", endDate.substring(0, 8));
			parm.setData("EN_PREPARE_NO", barCode);
			parm.setData("EXEC_FLG", isEex.replaceAll("A", ""));
			// δִ��
			if (StringUtils.equals("N", isEex)) {
				// ��ѯδִ��ʱֻ����seq������ʾ��һ��
				parm.setData("ROWNUM_LIMIT_FLG", "Y");
			}
			// ��ѯչ��ҽ��ִ�����SQL
			String sql = NSSEnteralNutritionTool.getInstance().queryENDspnDSql(parm);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯչ��ҽ��ִ���������");
				err("ERR:" + result.getErrCode() + result.getErrText());
				return;
			}
			
			if (result.getCount() <= 0) {
				this.messageBox("û�ж�Ӧ���ݣ�");
				return;
			}
			
			this.getTable("tableM").setParmValue(result);
            this.getTable("tableM").getTable().grabFocus();
		} else {// �ǿڷ�ҩSQL����ѯ
			SQL = ODISingleExeTool.getInstance().queryPatOrder(
					patInfo.getValue("CASE_NO", 0), barCode, startDate,
					endDate, cat1Type, isEex, doseType);
			onSetTableValue();
            ////////////////////////////////////////////
    		this.onSendCS5();
    		/////////////////////////////////////////////
		}
	}

	/**
	 * �����ֵ
	 * 
	 * @param sql
	 */
	public void onSetTableValue() {
		this.getTable("tableM").acceptText();
        TParm parmTable = new TParm();
        if (((TRadioButton) this.getComponent("R5")).isSelected()) {// modify by wanglong 20130603
            parmTable = ctrlParm;
        } else {
            parmTable = new TParm(TJDODBTool.getInstance().select(SQL));
            if (parmTable.getCount() <= 0) {
                // panelInit();
                // getTTable("tableM").removeRowAll();
                this.messageBox("û�ж�Ӧ���ݣ�");
                return;
            }
        }
//        System.out.println("onSetTableValue:"+SQL);
        TParm tableParm = this.getTable("tableM").getParmValue();
		// ����Ӫ������ִ��ע��
		if (enFlg) {
			// ���ڳ���Ӫ����ѯ����������������ִ�������в�ͬ,��Ҫ�����������趨���ݶ�����addParm
			// ���������û�رյ��������ִ���˳���Ӫ��ɨ����ִ������ɨ��ǰ����������set����
			tableParm = null;
			enFlg = false;
		}
        boolean flg = true;
        int row = 0;
        if (tableParm != null&&tableParm.getCount()>0) {
            for (int i = 0; i < tableParm.getCount("BAR_CODE"); i++) {
                if (tableParm.getValue("BAR_CODE", i).equals(parmTable.getValue("BAR_CODE", 0))) {
                    this.messageBox("��ɨ������룡");
                    row = i;
                    flg = false;
                    break;
                } else {
                    row = tableParm.getCount("BAR_CODE");
                }
            }
            if (flg) {
                tableParm.addParm(parmTable);
                this.getTable("tableM").setParmValue(tableParm);
                this.getTable("tableM").getTable().grabFocus();
            } else {
                this.getTable("tableM").getTable().grabFocus();
                return;
            }
        } else {
            this.getTable("tableM").setParmValue(parmTable);
            this.getTable("tableM").getTable().grabFocus();
            this.getTable("tableM").setSelectedRow(row);
        }
       
        //start  machao  �ȵ��� Ƥ��ע��  ������ҳ

        //this.messageBox(TConfig.getSystemValue("NISSWITCH"));
        if("Y".equals(TConfig.getSystemValue("NISSWITCH"))){
//            System.out.println("mmmm:"+this.getTable("tableM").getParmValue());
            TParm resultP = this.getTable("tableM").getParmValue();
            int num =  resultP.getCount();
            TParm result = new TParm(TJDODBTool.getInstance().select(
            		  " SELECT ORDER_CODE "
            		+ "	FROM IND_MONITOR_MED "
            		+ " WHERE 1 = 1 AND ENABLE_FLG = 'Y' AND MONITOR_TYPE = 'INSUL'"));//�õ���õ��ȵ�������
            //this.messageBox(result+"");
            
            for(int i = 0;i<num;i++){
            	TParm p = resultP.getRow(i);
            	if(!StringUtil.isNullString(p.getData("EXE_FLG")+"") && //ִ��
            			!StringUtil.isNullString(p.getData("ROUTE_CODE")+"") && //�÷�
            				!StringUtil.isNullString(p.getData("ORDER_CODE")+"")){ //ҽ������
            		if (p.getData("EXE_FLG").equals("N") && //δ��ִ��
            				(p.getData("ROUTE_CODE").equals("IH") || p.getData("ROUTE_CODE").equals("SC"))){//�÷���Ƥ��ע��
            			boolean flgResult = false;
            			//this.messageBox("asd1");
            			for(int j = 0;j<result.getCount("ORDER_CODE");j++){
            				if(result.getData("ORDER_CODE", j).equals(p.getData("ORDER_CODE"))){
            					flgResult = true;
            					SystemTool.getInstance().OpenNisWeb(this.getValueString("MR_NO"), patInfo.getValue("CASE_NO", 0));
            					//this.messageBox("asd");
            					break;
            				}
            			}
            			if(flgResult){
            				break;
            			}
            		}
            	}       	
            }
        	
        }
    //  end machao  �ȵ��� Ƥ��ע��  ������ҳ
        
        int count = 0;
        TParm afParm=getTable("tableM").getParmValue();
        TParm picParm = new TParm();
        for (int i = 0; i < afParm.getCount("ORDER_DESC"); i++) {
            if (afParm.getValue("CAT1_TYPE", i).equals("PHA")) {
                picParm.setData("ORDER_CODE", count, afParm.getValue("ORDER_CODE", i));
                picParm.setData("ORDER_DESC", count, afParm.getValue("ORDER_DESC", i));
                count++;
            }
        }
        tiPanel3.setLayout(null);
		tiPanel3.removeAll();
		this.panelInit();
		gridLayout1 = new GridLayout(0, 5, 10, 10);
        tiPanel3.setLayout(gridLayout1);
        picParm.setCount(count);
        PHA_PIC phaPic[] = new PHA_PIC[count];
        for (int j = 0; j < count; j++) {
            phaPic[j] =
                    new PHA_PIC(picParm.getValue("ORDER_CODE", j),
                            picParm.getValue("ORDER_DESC", j));
            phaPic[j].setPreferredSize(new Dimension(200, 200));
            tiPanel3.add(phaPic[j], null);
        }
        jScrollPane1.setViewportView(tiPanel3);
        
        // һ���ٴ�ɨ��PK��Ѫҽ��ʱ�Զ�����
		if ((Boolean) this.callFunction("UI|NEXE|isSelected")
				&& this.getPopedem("PIC")
				&& pkOrderCode.contains(afParm.getValue("ORDER_CODE", 0))) {
			this.onSaveExe();
		}
    }

	/**
	 * ���õ��ӱ�ǩ�ӿ�
	 * 
	 * @param map
	 */
	public void sendElectronicTag() {
		Map map = new HashMap();
		if (map == null)
			return;
		TParm parmTable = new TParm(TJDODBTool.getInstance().select(SQL));
		for (int i = 0; i < parmTable.getCount(); i++) {
			if (map.get(parmTable.getValue("BOX_ESL_ID", i)) == null
					&& parmTable.getValue("BOX_ESL_ID", i) != null)
				map.put(parmTable.getValue("BOX_ESL_ID", i),
						parmTable.getValue("BOX_ESL_ID", i));
		}
		Iterator it = map.values().iterator();
		try {
			if (it.hasNext()) {
				String  patName= this.patInfo.getValue("PAT_NAME", 0);
//				System.out.println("��������" +patName );
				String stationDesc = this.patInfo.getValue("STATION_DESC", 0);
//				System.out.println("����" +stationDesc );
				String bedNo = this.patInfo.getValue("BED_NO_DESC", 0);
//				System.out.println("����" +bedNo );
				String boxID = (String) it.next();
				int lightNum = 3;
				ElectronicTagUtil.login();
				boolean Send = ElectronicTagUtil.getInstance().sendEleTag(
						boxID, patName, stationDesc, bedNo, lightNum);
//				if (Send) {
//					System.out.println("==========���õ��ӱ�ǩ�ӿڳɹ�=================");
//				} else {
//					System.out.println("==========���õ��ӱ�ǩ�ӿ�ʧ��=================");
//				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
//			System.out.println("==========���õ��ӱ�ǩ�ӿ�ʧ��=================");
		}
	}

	/**
	 * 
	 */
	public void onExeQuery() {
		ctrlParm=new TParm();
		if (this.getValueString("BAR_CODE").equals("")) {
			this.messageBox("���������룡");
			return;
		}
        if (this.getValueString("PAT_NAME").equals("")) {
            this.messageBox("����¼�벡����");
            return;
        }
        
		//fux modify 20150827 ���ǵ����Ѫ��ťʱ�жϲ�����
		if(!((TRadioButton) this.getComponent("R_TRAN")).isSelected()){
        if (this.getValueString("PAT_NAME").equals("")) {
            this.messageBox("����¼�벡����");
            return;
        }
	    }
        
        
		if (((TRadioButton) this.getComponent("R5")).isSelected()) { // add by wanglong 20130603
			if(Operator.getSpcFlg().equals("N")){
        		this.messageBox("����������û�����ã����ܲ�ѯ");
        		return;
        	}
            // ������
            String caseNo = patInfo.getValue("CASE_NO", 0);
            String barCode = getValueString("BAR_CODE");
            String startDate = getValueString("start_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String endDate = getValueString("end_Date").replaceAll("[^0-9]", "").substring(0, 8);
            String startTime = getValueString("start_Date").replaceAll("[^0-9]", "").substring(8);
            String endTime = getValueString("end_Date").replaceAll("[^0-9]", "").substring(8);
            TParm inParm = new TParm();
            inParm.setData("CASE_NO", caseNo);
            inParm.setData("BAR_CODE", barCode);
            TParm spcParm =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "onQueryDspnDSpc", inParm);
            if (spcParm == null || spcParm.getErrCode() < 0) {
                this.messageBox(spcParm.getErrText());
                return;
            }
            String QUERY_DSPND_SQL =
                    " SELECT CASE WHEN A.NS_EXEC_DATE_REAL IS  NULL THEN 'Y' ELSE 'N' END SEL_FLG,CASE WHEN A.NS_EXEC_DATE_REAL IS NOT NULL THEN 'Y' ELSE 'N' END EXE_FLG,"
                            + "B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS')NS_EXEC_DATE,"
                            + "B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,"
                            + "B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,'#' AS BAR_CODE,"//modify by wanglong 20130609
                            + "A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.ORDERSET_GROUP_NO,B.CAT1_TYPE,"
//                            + "CASE WHEN B.CAT1_TYPE='PHA' THEN A.BAR_CODE ELSE C.MED_APPLY_NO END AS BAR_CODE,"
                            + " B.PUMP_CODE,B.INFLUTION_RATE,"    //modify by wukai 20160602 ��ӱ��뷽ʽ����Һ����
                            + "A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,'#' BARCODE_1,'#' BARCODE_2,'#' BARCODE_3 "
                            + " FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D "
                            + "WHERE B.CASE_NO = '#' "
                            + "  AND B.ORDER_NO = '#' "
                            + "  AND B.ORDER_SEQ = '#' "
//                            + "  AND A.ORDER_DATE = '#' "
//                            + "  AND A.ORDER_DATETIME = '#' "
                            + "  AND A.CASE_NO = B.CASE_NO "
                            + "  AND A.ORDER_NO = B.ORDER_NO "
                            + "  AND A.ORDER_SEQ = B.ORDER_SEQ "
//                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM "
                            + "  AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN # AND # "
                            + "  AND A.ORDER_DATE BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND A.ORDER_DATETIME BETWEEN '#' AND '#' "//add by wanglong 20130620
                            + "  AND (B.ORDERSET_CODE IS NULL OR B.ORDER_CODE = B.ORDERSET_CODE) "
                            + "  AND A.CASE_NO = C.CASE_NO "
                            + "  AND A.ORDER_NO = C.ORDER_NO "
                            + "  AND A.ORDER_SEQ = C.ORDER_SEQ "
                            + "  AND B.ROUTE_CODE=D.ROUTE_CODE(+) "
                            // cat1_type B.CAT1_TYPE
                            // �Ƿ�ִ��
                            + " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
            for (int i = 0; i < spcParm.getCount(); i++) {
                if (!caseNo.equals(spcParm.getValue("CASE_NO", i))) {
                    this.messageBox("����Ų�һ��");
                    return;
                }
                String orderNo = spcParm.getValue("ORDER_NO", i);
                String orderSeq = spcParm.getValue("ORDER_SEQ", i);
//                String orderDate = spcParm.getValue("ORDER_DATE", i);
//                String orderDateTime = spcParm.getValue("ORDER_DATETIME", i);
                String startDttm = spcParm.getValue("START_DTTM", i);
                String endDttm = spcParm.getValue("END_DTTM", i);
                String barCode1 = spcParm.getValue("BARCODE_1", i);
                String barCode2 = spcParm.getValue("BARCODE_2", i);
                String barCode3 = spcParm.getValue("BARCODE_3", i);
                String sql =
                        QUERY_DSPND_SQL.replaceFirst("#", barCode).replaceFirst("#", barCode1)
                                .replaceFirst("#", barCode2).replaceFirst("#", barCode3)
                                .replaceFirst("#", caseNo).replaceFirst("#", orderNo)
                                .replaceFirst("#", orderSeq).replaceFirst("#", startDttm)
                                .replaceFirst("#", endDttm).replaceFirst("#", startDate)
                                .replaceFirst("#", endDate).replaceFirst("#", startTime)
                                .replaceFirst("#", endTime);
                TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                if (result.getErrCode() != 0 || result.getCount() < 1) {
                    this.messageBox("�����龫��ҩ���� " + result.getErrText());
                    return;
                }
                ctrlParm.addRowData(result, 0);
            }
            onSetTableValue();
        } 
		//add by yangjj 20150430 ��Ѫִ�в�ѯ
		else if(((TRadioButton) this.getComponent("R_TRAN")).isSelected()){
			
			String con ="";
//			String str ="";
            if(this.getValueString("YEXE").equals("Y")){
//            	con = " AND A.BLDTRANS_USER IS NOT NULL";
            	con = " AND A.BLDTRANS_END_USER IS NOT NULL";//20151104 wangjc modify
//            	str = " A.FACT_VOL ,  ";
            }                                                  
            else if(this.getValueString("NEXE").equals("Y")){
//            	con = " AND A.BLDTRANS_USER IS  NULL";
            	con = " AND A.BLDTRANS_END_USER IS  NULL";//20151104 wangjc modify
//            	str = " A.BLOOD_VOL AS FACT_VOL ,  ";
            }
//            else{
//            	str = " A.FACT_VOL ,  ";  
//            }
            
            String sqlMrNo = " SELECT A.MR_NO "+
		" FROM " +
			" BMS_BLOOD A,ADM_INP B ,SYS_PATINFO C " +
		" WHERE " +
			" A.BLOOD_NO = '"+this.getValueString("BAR_CODE")+"' " +
			" AND A.CASE_NO = B.CASE_NO" +
			" AND A.MR_NO = C.MR_NO ";   
			//fux modify 20150828        
        	String sql = " SELECT A.MR_NO,A.BLD_TYPE,E.UNIT_CHN_DESC AS UNIT_CODE, " +
        					" 'Y' AS SEL_FLG, " +
        					" A.BLOOD_NO, " +
        					" A.BLD_CODE, " +
        					//" A.BLOOD_VOL AS FACT_VOL , " +
        					" CASE WHEN A.FACT_VOL IS NOT NULL THEN TO_NUMBER(A.FACT_VOL) ELSE A.BLOOD_VOL END AS FACT_VOL, "+
//        					str +
        					" A.SUBCAT_CODE, " +
        					" A.BLD_TYPE, " +  
        					" A.RH_FLG, " +   
        					" A.SHIT_FLG, " +
        					" A.CROSS_MATCH_L, " +
        					" A.CROSS_MATCH_S, " +
        					" A.RESULT, " +
        					" A.BLDTRANS_TIME, " +
        					" A.BLDTRANS_USER "+
        					" ,A.TRANSFUSION_REACTION "+//20151103 wangjc add ��Ѫ��Ӧ
        					" ,A.BLDTRANS_END_TIME "+//20151103 wangjc add ����ʱ��
        				" FROM " +
        					" BMS_BLOOD A,ADM_INP B ,SYS_PATINFO C,BMS_BLDSUBCAT D,SYS_UNIT E " +
        				" WHERE " +
        					" A.BLOOD_NO = '"+this.getValueString("BAR_CODE")+"' " +
        					" AND A.OUT_NO IS NOT NULL " +
        					con +    
        					" AND A.RECEIVED_USER IS NOT NULL  " +
        					" AND A.CASE_NO = B.CASE_NO" +
        					" AND A.MR_NO = C.MR_NO " +  
        					" AND A.BLD_CODE = D.BLD_CODE " +
        					" AND A.SUBCAT_CODE = D.SUBCAT_CODE " +
        					" AND D.UNIT_CODE = E.UNIT_CODE ";    
//        	System.out.println("sql"+sql);
        	TParm result = new TParm(TJDODBTool.getInstance().select(sql));  
        	TParm resultMrno = new TParm(TJDODBTool.getInstance().select(sqlMrNo));  
    		String mrNo = PatTool.getInstance().checkMrno(this.getValue("MR_NO").toString());
    		
    		
    		String mrNoBms = resultMrno.getValue("MR_NO",0).toString();
    		
    		
    		String stationCode = getValueString("BQ");           
    		String SQL = ODISingleExeTool.getInstance().queryPatInfo(mrNo, "",
    				stationCode);
    		//System.out.println("SQL:"+SQL);
    		patInfo = new TParm(TJDODBTool.getInstance().select(SQL));  
    		if("".equals(this.getValue("MR_NO").toString())){
    			this.messageBox("��ɨ�財��������룡");             
    			this.onClear();  
    			return;                                 
    		}   
    		
    		if (patInfo.getCount() <= 0) {
    			this.messageBox("���޲�����");             
    			this.onClear();  
    			return;
    		}
    		if(!mrNo.equals(mrNoBms)&&!"".equals(mrNoBms)){      
    			this.messageBox("Ѫ���м�¼����Ѫ����û��߲���");  
    			this.onClear();  
    			return;
    		}
    		//String patName = patInfo.getValue("PAT_NAME", 0) ;
    		String stationDesc = patInfo.getValue("STATION_DESC", 0);
    		String bedNoDesc = this.patInfo.getValue("BED_NO_DESC", 0);
    		
    		//setValue("MR_NO", mrNo);
    		//setValue("PAT_NAME", patName);                          
    		//setValue("SEX_CODE", patInfo.getValue("CHN_DESC", 0));  
    		//setValue("bed_no", patInfo.getValue("BED_NO_DESC", 0));
    		//setValue("ICD", patInfo.getValue("ICD",0));
    		//setValue("BLOOD_TYPE", patInfo.getValue("BLOOD_TYPE", 0));
    		String age = "0";
    		age = OdiUtil.getInstance().showAge((Timestamp)patInfo.getData("BIRTH_DATE", 0),SystemTool.getInstance().getDate());
    		//setValue("AGE",age);
        	if(result.getCount() <= 0){    
        		this.messageBox("���޸�Ѫ����Ϣ����ȷ��Ѫ����������״̬��");  
        		this.getTextField("BAR_CODE").setValue("");   
        		onClear();
        		return;
        	}
        	
        	TTable table_m = this.getTable("tableM");
        	table_m.acceptText();
        	TParm table_mParm = new TParm();
        	table_mParm = table_m.getParmValue();
        	for(int i = 0 ; i <= table_m.getRowCount() ; i++){
        		 if(this.getValueString("YEXE").equals("Y")){
//        			 String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'TRANSFUSION_REACTION' AND ID='"+result.getValue("TRANSFUSION_REACTION", 0)+"'";
//        			 TParm descParm = new TParm(TJDODBTool.getInstance().select(sql1));
//        			 result.setData("TRANSFUSION_REACTION", 0, descParm.getValue("CHN_DESC", 0));
        			 if(i == table_m.getRowCount() && table_mParm.getCount() > 0 && table_mParm != null){
             			table_mParm.addParm(result);
             			break;
             		}else if(table_mParm.getCount() <= 0 || table_mParm == null){
             			table_mParm = result;
             			break;
             		}
             		if(table_mParm.getCount() > 0 && table_mParm != null && table_mParm.getValue("BLOOD_NO", i).equals(result.getValue("BLOOD_NO", 0))){
             			break;
             		}
                 }                                                  
                 else if(this.getValueString("NEXE").equals("Y")){
                	 if(i == table_m.getRowCount() && table_mParm != null && table_mParm.getCount() > 0){
                		 table_mParm.addParm(result);
                		 break;
                	 }else if(table_mParm == null || table_mParm.getCount() <= 0){
                		 table_mParm = result;
                		 break;
                	 }
                	 if(table_mParm != null && table_mParm.getCount() > 0 && table_mParm.getValue("BLOOD_NO", i).equals(result.getValue("BLOOD_NO", 0))){
                		 break;
                	 }
                 }
        	}
//        	System.out.println("table_mParm----------"+table_mParm);
        	table_m.setParmValue(table_mParm);		
        	
        } 
		//ִ����е��ɨ��
		else if(((TRadioButton) this.getComponent("R_PACK")).isSelected()){
			String sql = "SELECT A.BARCODE AS BAR_CODE,B.PACK_DESC,A.QTY," +
					" D.USER_NAME AS CHECK_USER,A.CHECK_DATE" +
			" FROM INV_SUP_DISPENSED A, INV_PACKM B,SYS_OPERATOR D" +
			" WHERE A.BARCODE = '"+this.getValueString("BAR_CODE")+"'" +
			" AND A.INV_CODE = B.PACK_CODE " +
			" AND A.CHECK_USER = D.USER_ID(+)" +
			" AND A.RECEIVE_USER IS NOT NULL" +
			" AND A.CHECK_USER IS NULL ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        	if(result.getCount() <= 0){
        		this.messageBox("���޴���е�������е����ɨ�裡");
        		this.getTextField("BAR_CODE").setValue("");
        		return;
        	}
        	TTable table_m = this.getTable("tableM");
        	table_m.acceptText();
        	TParm table_mParm = new TParm();
        	table_mParm = table_m.getParmValue();
       	     //����һ��������е��ɨ����ʾ
        	for(int i = 0 ; i <= table_m.getRowCount() ; i++){
        		if(i == table_m.getRowCount() && table_mParm != null){
        			table_mParm.addParm(result);
        			break;
        		}else if(table_mParm == null){
        			table_mParm = result;
        			break;
        		}
        		if(table_mParm != null && table_mParm.getValue("BAR_CODE", i).equals
        				(result.getValue("BAR_CODE", 0))){
        			break;
        		}
        	}
        	
        	table_m.setParmValue(table_mParm);				
		}
		else {
            onBarCode("");
        }
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		this.setValue("BAR_CODE", "");
	}

	// �õ�����ִ������
	public TParm getExeSaveDate(TParm inparm) {
		getTTable("tableM").acceptText();
		int row = getTTable("tableM").getRowCount();
		TParm parmTable = getTTable("tableM").getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		TParm parmM = new TParm();
		TParm parmD = new TParm();
		
		//add by yangjj 20151020
		String lateReason = "";
//		System.out.println("getExeSaveDate:"+parmTable);
		for (int i = 0; i < row; i++) {
			parmM.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parmM.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parmM.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
			parmM.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
			parmM.addData("ORDER_DATETIME",
					parmTable.getValue("ORDER_DATETIME", i));
			parmM.addData("CANCELRSN_CODE",
					parmTable.getValue("CANCELRSN_CODE", i));
			//add by wukai on 20160603 ����ʱ��ӱ��뷽ʽ����Һ���� start
			parmM.addData("PUMP_CODE", parmTable.getValue("PUMP_CODE",i) == null ? new TNull(String.class) : parmTable.getValue("PUMP_CODE",i));
			parmM.addData("INFLUTION_RATE", parmTable.getValue("INFLUTION_RATE",i) == null ? new TNull(Double.class):parmTable.getValue("INFLUTION_RATE",i));
			//add by wukai on 20160603 ����ʱ��ӱ��뷽ʽ����Һ���� end
			// ΪCIS�ӿ��ṩ����--------------------------------------begin
			parmM.addData("LINKMAIN_FLG", parmTable.getValue("LINKMAIN_FLG", i));
			parmM.addData("LINK_NO", parmTable.getValue("LINK_NO", i));
			parmM.addData("CAT1_TYPE", parmTable.getValue("CAT1_TYPE", i));
			parmM.addData("ORDER_DR_CODE",
					parmTable.getValue("ORDER_DR_CODE", i));
			parmM.addData("BAR_CODE",parmTable.getValue("BAR_CODE", i));
			String nsexecdate = parmTable.getValue("ORDER_DATE", i)+
            parmTable.getValue("ORDER_DATETIME", i)+"00";
            parmM.addData("NS_EXEC_DATE",nsexecdate);
			// ΪCIS�ӿ��ṩ����--------------------------------------end
            
            // ΪNIS�ӿ��ṩ����--------------------------------------begin
            parmM.addData("ORDER_CODE", parmTable.getValue("ORDER_CODE", i));
            parmM.addData("SEL_FLG", parmTable.getValue("SEL_FLG", i));
            parmM.addData("DC_DATE", parmTable.getValue("DC_DATE", i));
            parmM.addData("ORDER_DESC", parmTable.getValue("ORDER_DESC", i));
            // ΪNIS�ӿ��ṩ����--------------------------------------end
            
			parmD.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parmD.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parmD.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
			parmD.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
			parmD.addData("ORDER_DATETIME",
					parmTable.getValue("ORDER_DATETIME", i));
			parmD.addData("INV_CODE", parmTable.getValue("INV_CODE", i));
			parmD.addData("CANCELRSN_CODE",
					parmTable.getValue("CANCELRSN_CODE", i));
			
			
			//ADD BY YANGJJ 20151020���ӳ���ִ��ҽ��ʱ���ж�
			Timestamp t1 = Timestamp.valueOf(parmTable.getValue("NS_EXEC_DATE", i).replace("/", "-")+"");
			long l = (now.getTime()-t1.getTime())/(1000*60);
			String orderNo = parmTable.getValue("ORDER_NO", i);
			String caseNo = parmTable.getValue("CASE_NO", i);
			String orderSeq = parmTable.getValue("ORDER_SEQ", i);
			String sql = " SELECT CAT1_TYPE FROM ODI_ORDER WHERE ORDER_NO='"+orderNo+"' AND CASE_NO='"+caseNo+"' AND ORDER_SEQ='"+orderSeq+"'";
			TParm orderCat1Type = new TParm(TJDODBTool.getInstance().select(sql));
			
			if( (Math.abs(l) > 120) && (getTTable("tableM").getValueAt(i, 0).equals("Y")) && ("PHA".equals(orderCat1Type.getValue("CAT1_TYPE", 0)))){
				if("".equals(lateReason)){
					TParm p = (TParm) this.openDialog(
							"%ROOT%\\config\\inw\\lateReason.x");
					lateReason = p.getData("p").toString();
				}
				
				parmD.addData("LATE_REASON", lateReason);
			}else{
				parmD.addData("LATE_REASON", new TNull(String.class));
			}
			
			
			if (getTTable("tableM").getValueAt(i, 0).equals("N")) {
				parmD.addData(
						"NS_EXEC_DATE_REAL",
						parmTable.getData("NS_EXEC_DATE_REAL", i) == null ? new TNull(
								Timestamp.class) : parmTable.getData(
								"NS_EXEC_DATE_REAL", i));
				parmD.addData(
						"NS_EXEC_CODE_REAL",
						parmTable.getData("NS_EXEC_CODE_REAL", i) == null ? new TNull(
								String.class) : parmTable.getData(
								"NS_EXEC_CODE_REAL", i));
	            parmD.addData("BARCODE_1", parmTable.getValue("BARCODE_1", i));// add by wanglong 20130604
	            parmD.addData("BARCODE_2", parmTable.getValue("BARCODE_2", i));
	            parmD.addData("BARCODE_3", parmTable.getValue("BARCODE_3", i));
				parmM.addData(
						"NS_EXEC_DATE_REAL",
						parmTable.getData("NS_EXEC_DATE_REAL", i) == null ? new TNull(
								Timestamp.class) : parmTable.getData(
								"NS_EXEC_DATE_REAL", i));
				parmM.addData(
						"NS_EXEC_CODE_REAL",
						parmTable.getData("NS_EXEC_CODE_REAL", i) == null ? new TNull(
								String.class) : parmTable.getData(
								"NS_EXEC_CODE_REAL", i));
				continue;
			}
			if (getTTable("tableM").getValueAt(i, 1).equals("N")) {
				parmD.addData("NS_EXEC_DATE_REAL", now);
				parmD.addData("NS_EXEC_CODE_REAL", inparm.getValue("USER_ID"));
				parmM.addData("NS_EXEC_DATE_REAL", now);
				parmM.addData("NS_EXEC_CODE_REAL", inparm.getValue("USER_ID"));
	            parmD.addData("BARCODE_1", parmTable.getValue("BARCODE_1", i));// add by wanglong 20130604
	            parmD.addData("BARCODE_2", parmTable.getValue("BARCODE_2", i));
	            parmD.addData("BARCODE_3", parmTable.getValue("BARCODE_3", i));
			} else {
				parmD.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
				parmD.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
				parmM.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
				parmM.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
	            parmD.addData("BARCODE_1",new TNull(String.class));// add by wanglong 20130604
	            parmD.addData("BARCODE_2", new TNull(String.class));
	            parmD.addData("BARCODE_3",new TNull(String.class));
			}
			
			
			if (parmTable.getValue("ORDERSET_GROUP_NO", i).length() == 0) {
				continue;
			}
			setDetailOrder(parmTable, i, parmD, now);
		}
		TParm parm = new TParm();
		parm.setData("DSPNM", parmM.getData());
		parm.setData("DSPND", parmD.getData());
		return parm;
	}

	/**
	 * ���涯��
	 */
	public void onSaveExe() {
	    //����ҩƷ��ʾ	
		odiSingleExeDrools.fireRules();
		boolean flg = (Boolean) this.callFunction("UI|ALL|isSelected");
		if (flg) {
			this.messageBox("������ȫ��״̬�±���");
			return;
		}
		String type = "singleExe";
		
		TParm tableParm = getTTable("tableM").getParmValue();
		TParm inParm = new TParm();
        // һ���ٴ�ɨ��PK��Ѫҽ��ʱ����ɨ�����Զ�����
		if (this.getPopedem("PIC")
				&& pkOrderCode.contains(tableParm.getValue("ORDER_CODE", 0))) {
			inParm.setData("USER_ID", Operator.getID());
		} else {
			inParm = (TParm) this.openDialog(
					"%ROOT%\\config\\inw\\passWordCheck.x", type);
			String OK = inParm.getValue("RESULT");
			if (!OK.equals("OK")) {
				return;
			}
		}
		getTTable("tableM").acceptText();
		TParm parm = getExeSaveDate(inParm);
//		System.out.println("******************SingleExe Save Data: \n" + parm);
//		System.out.println("========&&&&&&&&&&&&&&&&&&&&&***********************");
//		System.out.println("=============="+parm);
		if (parm == null) {
			return;
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.INWOrderSingleExeAction", "onSaveExe", parm);
		if (result.getErrCode() < 0) {
			messageBox("����ʧ��");
			return;
		} else {
			String cisflg = "N";
			TParm parm1 = parm.getParm("DSPNM");
			for (int i = 0; i < parm1.getCount("CASE_NO"); i++) {
				if (parm1.getValue("CAT1_TYPE", i).equals("PHA"))
					cisflg = "Y";
			}
			
			/**
			 * 2017.04.13 zhanglei ͬ��NIS��״̬
			 */
			
			//�ж��Ƿ���Ҫͬ��NIS
			
			String nisflg = "N";
			TParm parmNis = new TParm();
			//��ý���Table�Ŀؼ�����
			TTable table = (TTable)this.getComponent("tableM");
			TParm tableparm = table.getParmValue();
			//this.messageBox("111111111" + nis);
			for (int i = 0; i < parm1.getCount("CASE_NO"); i++) {
				if(tableparm.getValue("SEL_FLG", i).equals("Y")){
					TParm nis = queryNISFLG(parm1.getValue("ORDER_CODE" , i));
					//this.messageBox("ѭ��������" + i + "-nis:" + nis);
					//this.messageBox("" + tableparm.getValue("ORDER_DESC", i));
					if (null != nis) {
						if(nis.getValue("SYNC_NIS_FLG",0).equals("Y")){
							parmNis.addRowData(parm1, i);
							nisflg = "Y";
						}
					}
				}
				
				//this.messageBox("" + tableparm.getValue("SEL_FLG", i));
					
					
				
			}
			//ͬ��NIS
		  if (nisflg.equals("Y")){ 
				//this.messageBox("����ͬ��NIS");
				for(int j = 0 ; j < parmNis.getCount("NS_EXEC_DATE_REAL") ; j++){
					String ss = parmNis.getValue("NS_EXEC_DATE_REAL", j);
					if( "<TNULL>".equals(ss)){
						parmNis.setData("NS_EXEC_DATE_REAL", j , null);
					}
				}
				String typeNIS="NIS";
				parmNis.setData("ADM_TYPE", "I");
				List list = new ArrayList();
				list.add(parmNis);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,typeNIS);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			
			
			
			
			// ҩ��
			if (cisflg.equals("Y") && getValueString("NEXE").equals("Y")) {
				// ICU��CCUע��
				String caseNO = parm1.getValue("CASE_NO", 0);
				boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
				boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
				//�����ڷ�ҩ����
				TParm oral = onOral(caseNO);
				if (IsICU || IsCCU||oral.getCount()>0) {
					//add by yangjj 20151021
					for(int j = 0 ; j < parm1.getCount("NS_EXEC_DATE_REAL") ; j++){
						String ss = parm1.getValue("NS_EXEC_DATE_REAL", j);
						if( "<TNULL>".equals(ss)){
							parm1.setData("NS_EXEC_DATE_REAL", j , null);
						}
						
					}

					String typeF = "NBW";
					List list = new ArrayList();
					parm.setData("ADM_TYPE", "I");
					list.add(parm1);
					// ���ýӿ�
					
					TParm resultParm = Hl7Communications.getInstance()
							.Hl7MessageCIS(list, typeF);
					if (resultParm.getErrCode() < 0)
						messageBox(resultParm.getErrText());
					
				}
//////////////////////////////////////////////zhangs add start
//				//��Һ��
//				if (this.checkIsCS5(caseNO)){
//					for(int j = 0 ; j < parm1.getCount("NS_EXEC_DATE_REAL") ; j++){
//						String ss = parm1.getValue("NS_EXEC_DATE_REAL", j);
//						if( "<TNULL>".equals(ss)){
//							parm1.setData("NS_EXEC_DATE_REAL", j , null);
//						}
//						
//					}
//
//					String typeF = "CS5";
//					List list = new ArrayList();
//					parm.setData("ADM_TYPE", "I");
//					list.add(parm1);
//					// ���ýӿ�
//
//					TParm resultParm = Hl7Communications.getInstance()
//					.Hl7MessageCIS(list, typeF);
//        			if (resultParm.getErrCode() < 0)
//		        		messageBox(resultParm.getErrText());
//
//				}
//				////////////////////////////zhangs add end
			}
			
			tiPanel3.setLayout(null);
			tiPanel3.removeAll();
			this.panelInit();
			getTTable("tableM").removeRowAll();
			messageBox("����ɹ�");
			this.onClear();
		}
		// /**********************************************************************************/
		// String caseNo = "", orderNo = "", orderSeq = "", startDttm = "",
		// endDttm = "";
		// // ͨ��CASE_NO��ORDER_NO��ORDER_SEQ��ODI_DSPND�ж�λ����ϸ��
		// TParm tableParm = getTTable("tableM").getParmValue();
		// for (int i = 0; i < tableParm.getCount(); i++) {
		// caseNo = tableParm.getValue("CASE_NO", i);
		// orderNo = tableParm.getValue("ORDER_NO", i);
		// orderSeq = tableParm.getValue("ORDER_SEQ", i);
		// startDttm = tableParm.getValue("START_DTTM", i);
		// endDttm = tableParm.getValue("END_DTTM", i);
		// // ��ѯϸ���SQL
		// String sql =
		// "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
		// + "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE FROM ODI_DSPND "
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
		// + "','YYYYMMDDHH24MISS')"
		// + " ORDER BY ORDER_DATE||ORDER_DATETIME";
		// // ����ϸ���TDS,����������
		// TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
		// }
		// /******************************************************************************************/
		// onBarCode();
	}
	
	/**
	 * ����Ӫ������ִ�б��涯��
	 */
	public void onSaveByExeEN() {
		// ����У��
		if (!validateExeEN()) {
			return;
		}
		
		String type = "singleExe";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		if (!inParm.getValue("RESULT").equals("OK")) {
			return;
		}
		
		TParm parm = getTTable("tableM").getParmValue().getRow(0);
		double totalAccuQty = parm.getDouble("TOTAL_ACCU_QTY") + parm.getDouble("DOSAGE_QTY");
		parm.setData("TOTAL_ACCU_QTY", totalAccuQty);
		// �����ʱҽ����ֻҪִ��һ������Ϊȫ��ִ�����
		if (StringUtils.equals("ST", parm.getValue("RX_KIND"))) {
			parm.setData("EXEC_STATUS", "2");
		} else {
			if (totalAccuQty >= parm.getDouble("TOTAL_QTY")) {
				// 0_δִ��,1_����ִ��,2_ȫ��ִ��
				parm.setData("EXEC_STATUS", "2");
			} else {
				parm.setData("EXEC_STATUS", "1");
			}
		}
		parm.setData("OPT_USER", inParm.getValue("USER_ID"));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("EXEC_FLG", "Y");
		parm.setData("EXEC_USER", inParm.getValue("USER_ID"));
		parm.setData("EN_PREPARE_NO", parm.getValue("BAR_CODE"));
		
		// �������
		TParm result = TIOM_AppServer.executeAction(
				"action.nss.NSSEnteralNutritionAction",
				"onSaveNSSENDspnBySingleExe", parm);

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox(parm.getErrText());
			return;
		}
		
		tiPanel3.setLayout(null);
		tiPanel3.removeAll();
		this.panelInit();
		getTTable("tableM").removeRowAll();
		this.messageBox("P0001");
		this.onClear();
	}
	
	/**
	 * ����Ӫ������ִ��������֤
	 */
	private boolean validateExeEN() {
		TParm parm = getTTable("tableM").getParmValue().getRow(0);
		String message = "";
		
		// ֻ��δִ�����ݿ��Ա���
		if (!getValueString("NEXE").equals("Y")) {
			this.messageBox("δִ��״̬�²ſɽ��б������");
			return false;
		}
		
		if (parm.getBoolean("EXE_FLG")) {
			this.messageBox("��������ִ����ϲ����ظ�ִ��");
			return false;
		}
		
		// ��һ����֤������
		if (!StringUtils.equals(this.getValueString("MR_NO"), parm
				.getValue("MR_NO"))) {
			this.messageBox("ִ�������벡�˲����Ų���");
			return false;
		}
		
		// �ڶ�����֤סԺҽʦҽ���Ƿ�ͣ���Լ�Ӫ��ʦҽ���Ƿ�ͣ��
		TParm result = NSSEnteralNutritionTool.getInstance().queryOrderInfo(
				parm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯҽ����Ϣ�쳣");
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		
		if (StringUtils.isNotEmpty(result.getValue("DC_DATE", 0))) {
			message = "��ҽ���ѱ�סԺҽ��ͣ��";
		} else if (StringUtils.isNotEmpty(result.getValue("EN_DC_DATE", 0))) {
			message = "���䷽�ѱ�Ӫ��ʦͣ��";
		}
		
		if (StringUtils.isNotEmpty(message)) {
			int selectedNo = this.messageBox("ִ��ȷ��", message + ",�Ƿ����ִ�У�", 0);
			if (selectedNo == 1) {
				return false;
			}
		}
		
		// ��������֤��ִ�еĳ���Ӫ���Ƿ��ѹ���
		// ��ѯ��ʳ�����ֵ�
		result = NSSEnteralNutritionTool.getInstance()
				.queryENCategory(parm.getValue("ORDER_CODE"));
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯ��ʳ�����ֵ����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return false;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("������ʳ�����ֵ�����");
    		return false;
    	} else {
    		int dayCount = result.getInt("VALID_PERIOD", 0);
			Date preDate = StringTool.getDate(parm.getTimestamp("PREPARE_DATE"));
    		// ִ�н�ֹ����
			Date endDate = DateUtils.addDays(preDate, dayCount);
			if (endDate.before(SystemTool.getInstance().getDate())) {
				int selectedNo = this.messageBox("ִ��ȷ��", "��ǰִ�г���Ӫ���ѹ���,�Ƿ����ִ�У�", 0);
				if (selectedNo == 1) {
					return false;
				}
			}
    	}
		return true;
	}
	
	private void setDetailOrder(TParm parmTable, int i, TParm parm,
			Timestamp now) {
		String SQL = ODISingleExeTool.getInstance().queryPatOrderSetDetail(
				patInfo.getValue("CASE_NO", 0),
				parmTable.getValue("ORDER_NO", i),
				parmTable.getValue("ORDER_DATE", i),
				parmTable.getValue("ORDER_DATETIME", i),
				parmTable.getValue("ORDERSET_GROUP_NO", i));
		TParm detailParm = new TParm(TJDODBTool.getInstance().select(SQL));
		for (int j = 0; j < detailParm.getCount(); j++) {
			parm.addData("CASE_NO", detailParm.getValue("CASE_NO", j));
			parm.addData("ORDER_NO", detailParm.getValue("ORDER_NO", j));
			parm.addData("ORDER_SEQ", detailParm.getValue("ORDER_SEQ", j));
			parm.addData("ORDER_DATE", detailParm.getValue("ORDER_DATE", j));
			parm.addData("ORDER_DATETIME",
					detailParm.getValue("ORDER_DATETIME", j));
			parm.addData("INV_CODE", new TNull(String.class));
			parm.addData("CANCELRSN_CODE", new TNull(String.class));
			parm.addData("BARCODE_1",new TNull(String.class));// add by wanglong 20130604
			parm.addData("BARCODE_2",new TNull(String.class));
			parm.addData("BARCODE_3",new TNull(String.class));
			if (getTTable("tableM").getValueAt(i, 1).equals("N")) {
				parm.addData("NS_EXEC_DATE_REAL", now);
				parm.addData("NS_EXEC_CODE_REAL", Operator.getID());
			} else {
				parm.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
				parm.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
			}
			
			//add by yangjj 20151020
			parm.addData("LATE_REASON",new TNull(String.class));
		}
	}

	public void onExe() {
		int row = getTTable("tableM").getRowCount();
		for (int i = 0; i < row; i++) {
			getTTable("tableM").setValueAt(getValue("EXE_ALL"), i, 0);
		}
	}

	public void onTableCheckBoxChangeValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int selCol = table.getSelectedColumn();
		int selRow = table.getSelectedRow();
		String columnName = table.getParmMap(selCol);
		int row = table.getRowCount();
		TParm tblParm = table.getParmValue();
		if (!columnName.equals("SEL_FLG")) {
			return;
		}
		if (!tblParm.getValue("LINKMAIN_FLG", selRow).equals("Y")) {
			return;
		}
		for (int i = 0; i < row; i++) {
			if (i == selRow) {
				continue;
			}
			if (!tblParm.getValue("LINK_NO", i).equals(
					tblParm.getValue("LINK_NO", selRow))) {
				continue;
			}
			if (!tblParm.getValue("ORDER_NO", i).equals(
					tblParm.getValue("ORDER_NO", selRow))) {
				continue;
			}
			if (!tblParm.getValue("ORDER_DATE", i).equals(
					tblParm.getValue("ORDER_DATE", selRow))) {
				continue;
			}
			if (!tblParm.getValue("ORDER_DATETIME", i).equals(
					tblParm.getValue("ORDER_DATETIME", selRow))) {
				continue;
			}
			table.setValueAt(table.getValueAt(selRow, 0), i, 0);
		}
	}

	/**
	 * �����ŵĻس��¼�
	 */
	public void onEnter() {
		onQueryPatInfo();
		/*
		 * String start_Date = this.getValueString("start_Date"); String
		 * end_date = this.getValueString("end_date"); String Star_time =
		 * start_Date.toString().substring(11, 13) +
		 * start_Date.toString().substring(14, 16); String End_date =
		 * end_date.toString().substring(9, 11) +
		 * end_date.toString().substring(12, 14); String order_date =
		 * start_Date.substring(0, 10).replace("/", "");
		 * 
		 * Pat pat = new Pat(); pat =
		 * pat.onQueryByMrNo(getValueString("MR_NO")); if (pat == null ||
		 * "".equals(pat.getMrNo())) { this.messageBox("���޴˲���!"); return; }
		 * String mr_no = pat.getMrNo(); this.setValue("MR_NO", mr_no);
		 * QueryBed_no(mr_no); QueryPatInfo(mr_no);
		 * getTextField("MR_NO").grabFocus(); viewPhoto(mr_no);
		 * getTextField("tt").grabFocus(); onQueryM(mr_no, Operator.getRegion(),
		 * Operator.getStation(), Operator.getDept(), Star_time, End_date,
		 * order_date);
		 */
	}

	public void QueryBed_no(String mr_no) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mr_no);
		String bed_no = ADMInpTool.getInstance().queryCaseNo(parm).getRow(0)
				.getValue("BED_NO");
		this.setValue("bed_no", bed_no);

	}

	public void QueryPatInfo(String mr_no) {
		String sql = tool.sql_Patinfo(mr_no);
		TParm selParm = new TParm();
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("PAT_NAME", selParm.getValue("PAT_NAME", 0));
		this.setValue("SEX_CODE", selParm.getValue("CHN_DESC", 0));
	}

	/**
	 * ��table������
	 * 
	 * @param MR_NO
	 *            String
	 * @param REGION_CODE
	 *            String
	 * @param STATION_CODE
	 *            String
	 * @param EXEC_DEPT_CODE
	 *            String
	 */
	public void onQueryM(String MR_NO, String REGION_CODE, String STATION_CODE,
			String EXEC_DEPT_CODE, String Star_time, String End_date,
			String order_date) {
		String sql1 = tool.sql_caseNo(MR_NO);
		// this.messageBox("sql" + sql1);
		TParm selParm1 = new TParm();
		selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		String case_no = selParm1.getValue("CASE_NO", 0);
		// this.messageBox(case_no);

		if (null == case_no || "".equals(case_no)) {
			this.messageBox("�û����ѳ�Ժ����δסԺ");
			return;
		}

		String sql = tool.sql_TableMessage(case_no, REGION_CODE, STATION_CODE,
				EXEC_DEPT_CODE, Star_time, End_date, order_date);
		TParm selParm = new TParm();
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() < 0) {
			this.messageBox("�˻����ڱ�ʱ��û��Ҫִ�е�ҽ��������ȫ��ִ�����");
		}
		this.getTTable("tableM").setParmValue(selParm);
	}

	/**
	 * ��շ���
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	public void onClear() {
		//20151103 wangjc add start ��Ѫ������Ӧ
		String sql = "SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'TRANSFUSION_REACTION'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		TTextFormat transfusionReaction = (TTextFormat) getComponent("TRANSFUSION_REACTION");
//		try {
//			transfusionReaction.setHorizontalAlignment(2);
//			transfusionReaction.setPopupMenuHeader("����,100;����,120");
//			transfusionReaction.setPopupMenuWidth(300);
//			transfusionReaction.setPopupMenuHeight(300);
//			transfusionReaction.setFormatType("combo");
//			transfusionReaction.setShowColumnList("NAME");
//			transfusionReaction.setValueColumn("ID");
//			transfusionReaction.setPopupMenuData(result);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		getTTable("tableM").addItem("TRANSFUSION_REACTION", getComponent("TRANSFUSION_REACTION"));
		//20151103 wangjc add end ��Ѫ������Ӧ
		
		setValue("R1", "Y");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		setValue("SEX_CODE", "");
		setValue("bed_no", "");
		setValue("AGE","");
		setValue("BLOOD_TYPE","");
		setValue("ICD","");
//		setExeDate();
		setValue("BAR_CODE", "");
		setValue("EXE_ALL", "N");
		//add by yangjj 20150504
		onSel("");
//		getTTable("tableM").removeAll();
        ctrlParm = new TParm();// add by wanglong 20130603
		tiPanel3.setLayout(null);
		tiPanel3.removeAll();
		this.panelInit();
		TPanel photo = (TPanel) this.getComponent("PHOTO_PANEL");
		Image image = null;
		Pic pic = new Pic(image);
		pic.setSize(photo.getWidth(), photo.getHeight());
		pic.setLocation(0, 0);
		photo.removeAll();
		photo.add(pic);
		pic.repaint();
		
		// this.clearValue(
		// "MR_NO;PAT_NAME;SEX_CODE;bed_no;tt;PHOTO_PANEL");
		//
		// this.getTTable("tableM").setSelectionMode(0);
		// this.getTTable("tableM").removeRowAll();
		// this.action = "a";
		// TPanel photo = (TPanel)this.getCompnent("PHOTO_PANEL");
		// Image image = null;
		// Pic pic = new Pic(image);
		// pic.setSize(photo.getWidth(), photo.getHeight());
		// pic.setLocation(0, 0);
		// photo.removeAll();
		// photo.add(pic);
		// pic.repaint();
		//
		// this.onInit();

	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */

	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/*
	 * public void viewPhoto(String mrNo) { // String photoName = mNo + ".jpg";
	 * // String photoName = "" + mrNo + ".jpg"; // String fileName = photoName;
	 * // String mrNo1 = "" + mrNo.substring(0, 3) + "\\"; // String mrNo2 = ""
	 * + mrNo.substring(3, 6) + "\\"; // String mrNo3 = "" + mrNo.substring(6,
	 * 9); // try { // TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
	 * // String root = TIOM_FileServer.getRoot(); // String dir =
	 * TIOM_FileServer.getPath("PatInfPIC.ServerPath"); // dir = root + dir +
	 * mrNo1 + mrNo2 + mrNo3 + "\\"; // byte[] data =
	 * TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), // dir + fileName);
	 * // if (data == null) // return; // double scale = 0.45; // boolean flag =
	 * true; // Image image = ImageTool.scale(data, scale, flag); // Pic pic =
	 * new Pic(image); // pic.setSize(viewPanel.getWidth(),
	 * viewPanel.getHeight()); // pic.setLocation(0, 0); //
	 * viewPanel.removeAll(); // viewPanel.add(pic); // pic.repaint(); // } //
	 * catch (Exception e) { // this.messageBox_(e); // }
	 * 
	 * }
	 */
	/**
	 * �����¼�
	 */
	public void onSave() {
		// ִ�г���Ӫ������
		if (enFlg) {
			onSaveByExeEN();
		} 
		//��Ѫ����
		else if(((TRadioButton)this.getComponent("R_TRAN")).isSelected()){
			onSaveByBMSTran();
		}
		//��е������
		else if(((TRadioButton)this.getComponent("R_PACK")).isSelected()){
			onSavePack();
		}
		else {
			onSaveExe();
		}
		/*
		 * String value = (String)this.openDialog(
		 * "%ROOT%\\config\\inw\\passWordCheck.x"); if (value == null) { return;
		 * } Timestamp date = SystemTool.getInstance().getDate();
		 * this.getTTable("tableM").setSelectionMode(0); TParm parm =
		 * getTTable("tableM").getParmValue(); int row = parm.getCount(); String
		 * a = parm.getValue("SELECT_FLG"); String b =
		 * parm.getValue("EXEC_FLG"); for (int i = 0; i < row; i++) { if
		 * ("Y".equals(parm.getValue("EXEC_FLG", i))) { TParm tparm = new
		 * TParm(); tparm.setData("OPT_USER", Operator.getID());
		 * tparm.setData("OPT_DATE", date); tparm.setData("OPT_TERM",
		 * Operator.getIP()); tparm.setData("CASE_NO", parm.getValue("CASE_NO",
		 * i)); tparm.setData("ORDER_NO", parm.getValue("ORDER_NO", i));
		 * tparm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
		 * tparm.setData("ORDER_DATE", parm.getValue("ORDER_DATE", i));
		 * tparm.setData("ORDER_DATETIME", parm.getValue("ORDER_DATETIME", i));
		 * 
		 * tparm.setData("NS_EXEC_CODE", Operator.getID());
		 * 
		 * TestNtool.getInstance().onUpdate(tparm); } else { continue; }
		 * 
		 * } this.messageBox("P0001");
		 */

	}
	
	/*
	 * ��Ѫִ��
	 * */
	public void onSaveByBMSTran(){
		this.getTable("tableM").acceptText();
		TParm parm = this.getTable("tableM").getParmValue();
//		System.out.println("--------"+parm);
		if(parm == null){
			this.messageBox("��ɨ��Ѫ��Ժ���룡");
			return ;
		}
		String type = "singleExe";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		if (!inParm.getValue("RESULT").equals("OK")) {
			return;
		}
		
		String user = inParm.getValue("USER_ID");
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		for(int i = 0; i < parm.getCount(); i++){
			if("Y".equals(parm.getValue("SEL_FLG", i))){  
				//fux modify 20150831 
				String bloodNo = parm.getValue("BLOOD_NO", i);
				String time = date.toString().replace("-", "").replace(":", "").replace(".0", "").replace(" ", "");
				int factVol = parm.getInt("FACT_VOL", i);
				TParm p = new TParm();
				p.setData("BLOOD_NO", bloodNo);
				p.setData("BLDTRANS_USER", user);  
//				p.setData("BLOOD_NO", bloodNo);  
				p.setData("FACT_VOL", factVol);
				TParm result = new TParm();
				//20151103 wangjc add start
				String transfusionReaction = parm.getValue("TRANSFUSION_REACTION", i);//��Ѫ��Ӧ
				String sql = "SELECT * FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodNo+"' ";
				TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
				p.setData("CASE_NO", dataParm.getValue("CASE_NO", 0));
				
				p.setData("MR_NO", dataParm.getValue("MR_NO", 0));
				p.setData("IPD_NO", dataParm.getValue("IPD_NO", 0));
				p.setData("APPLY_NO", dataParm.getValue("APPLY_NO", 0));
				String unitSql = "SELECT UNIT_CODE FROM BMS_BLDSUBCAT WHERE SUBCAT_CODE = '"+dataParm.getValue("SUBCAT_CODE", 0)+"'";
				TParm unitParm = new TParm(TJDODBTool.getInstance().select(unitSql));
				p.setData("DISPENSE_UNIT", unitParm.getValue("UNIT_CODE", 0));
				p.setData("ORDER_CODE", dataParm.getValue("BLD_CODE", 0));
				
				String bldcodeDescSql = "SELECT BLDCODE_DESC FROM BMS_BLDCODE WHERE BLD_CODE ='"+dataParm.getValue("BLD_CODE", 0)+"'";
				TParm bldcodeDescParm = new TParm(TJDODBTool.getInstance().select(bldcodeDescSql));
				p.setData("ORDER_DESC", bldcodeDescParm.getValue("BLDCODE_DESC", 0));
				
				String oldOrderSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO = '"+dataParm.getValue("CASE_NO", 0)+"' AND APPLY_NO = '"+dataParm.getValue("APPLY_NO", 0)+"'";
				TParm oldOrderParm = new TParm(TJDODBTool.getInstance().select(oldOrderSql));
				p.setData("ORDER_DR_CODE", oldOrderParm.getValue("ORDER_DR_CODE", 0));
				p.setData("REGION_CODE", oldOrderParm.getValue("REGION_CODE", 0));
				p.setData("STATION_CODE", oldOrderParm.getValue("STATION_CODE", 0));
				p.setData("DEPT_CODE", oldOrderParm.getValue("DEPT_CODE", 0));
				p.setData("BED_NO", oldOrderParm.getValue("BED_NO", 0));
				p.setData("RX_KIND", "BL");
				if(dataParm.getValue("BLDTRANS_TIME", 0) != null && !dataParm.getValue("BLDTRANS_TIME", 0).equals("")){
					
					p.setData("TRANSFUSION_REACTION", transfusionReaction);
					p.setData("BLDTRANS_END_USER", user);
//					p.setData("BLDTRANS_END_TIME", time);
					p.setData("OPT_USER", user);
					p.setData("OPT_TERM", Operator.getIP());
					result = TIOM_AppServer.executeAction(
							"action.bms.BMSBloodAction", "onTranBlood2", p);
				}else if(dataParm.getValue("BLDTRANS_TIME", 0) == null || dataParm.getValue("BLDTRANS_TIME", 0).equals("")){
					String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
							"ORDER_NO", "ORDER_NO");
					p.setData("ORDER_NO", orderNo);
					String orderSql = "SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM ODI_ORDER WHERE CASE_NO = '"+dataParm.getValue("CASE_NO", 0)+"' AND BLOOD_NO = '"+bloodNo+"'";
					TParm countParm = new TParm(TJDODBTool.getInstance().select(orderSql));
					if(countParm.getCount() > 0){
						p.setData("ORDER_SEQ", countParm.getInt("ORDER_SEQ", 0)+1);
					}else{
						p.setData("ORDER_SEQ", "1");
					}
					p.setData("OPT_TERM", Operator.getIP());
					p.setData("BLDTRANS_USER", user);
					p.setData("BLDTRANS_TIME", time);
					result = TIOM_AppServer.executeAction(
							"action.bms.BMSBloodAction", "onTranBlood", p);
				}
				//20151103 wangjc add end
				if(result.getErrCode() < 0){
					this.messageBox("����ʧ�ܣ�");  
					return ;
				}
			}
		}
		this.messageBox("����ɹ���");
		onClear();
	}
	/*
	 * ��е��ִ��
	 * */
	public void onSavePack(){
		this.getTable("tableM").acceptText();
		TParm parm = this.getTable("tableM").getParmValue();
		if(parm == null){
			this.messageBox("��ɨ����е�����룡");
			return ;
		}
		
		String type = "singleExe";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		if (!inParm.getValue("RESULT").equals("OK")) {
			return;
		}
		String user = inParm.getValue("USER_ID");
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		String caseNo = patInfo.getValue("CASE_NO",0);		
		for(int i = 0; i < parm.getCount(); i++){
			String barcode = parm.getValue("BAR_CODE", i);
	    	String sql = " UPDATE INV_SUP_DISPENSED SET CHECK_USER ='" + user + "',"+
	    	" CHECK_DATE = SYSDATE,MR_NO = '"+mrNo+"'," +
	    	" CASE_NO ='"+caseNo+"'"+
	    	" WHERE BARCODE = '" + barcode + "'";  
	        //System.out.println("sql=========="+sql); 		
		   TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
		      if (updateParm.getErrCode() < 0) {
			         return ;
		          }
		}
		this.messageBox("����ɹ���");
		onClear();
	}
	/**
	 * ͼƬ
	 * 
	 */
	public class PHA_PIC extends TiMultiPanel implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6685653491843293933L;
		TiLabel tiL_orderDesc = new TiLabel();
		TiLabel tiL_Laber = new TiLabel();
		private String OrderDesc = "";
		TiPanel tiPanel1 = new TiPanel();
		TitledBorder titledBorder1;

		public PHA_PIC() {
			try {
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public PHA_PIC(String OrderDesc) {// ��ͼ
			try {
				this.OrderDesc = OrderDesc;
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 
		 * @param OrderCode
		 * @param OrderDesc
		 * @param Color
		 */
		public PHA_PIC(String OrderCode, String OrderDesc) {//
			try {
				// System.out.println("------2-------------"+OrderCode);
				this.OrderDesc = OrderDesc;
				jbInit();
				viewPhoto(OrderCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void jbInit() throws Exception {
			titledBorder1 = new TitledBorder("");
			tiL_orderDesc.setBackground(Color.black);
			tiL_orderDesc.setFont(new java.awt.Font("����", 1, 12));
			tiL_orderDesc.setForeground(Color.black);
			tiL_orderDesc.setText(this.OrderDesc);
			tiL_orderDesc.setBounds(new Rectangle(9, 1, 240, 15));
			tiL_Laber.setBackground(Color.black);
			tiL_Laber.setFont(new java.awt.Font("����", 1, 12));
			tiL_Laber.setForeground(Color.black);
			tiL_Laber.setText("û��ͼƬ");
			tiL_Laber.setBounds(new Rectangle(60, 70, 112, 15));
			this.setFont(new java.awt.Font("Dialog", 0, 11));
			this.setBorder(BorderFactory.createEtchedBorder());
			this.setLayout(null);
			this.addMouseListener(this);
			tiPanel1.setBounds(new Rectangle(30, 15, 190, 180));
			tiPanel1.setLayout(null);
			this.add(tiPanel1, null);
			this.add(tiL_orderDesc, null);
		}

		/**
		 * ͼƬ��ʾ����
		 * 
		 * @param orderCode
		 */
		public void viewPhoto(String orderCode) {
			// System.out.println("------------------------3---------"+orderCode);
			String photoName = orderCode + ".jpg";
			String fileName = photoName;
			try {
				String root = TIOM_FileServer.getRoot();
				String dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
				dir = root + dir;
				byte[] data = TIOM_FileServer.readFile(
						TIOM_FileServer.getSocket(), dir + fileName);
				if (data == null) {
					tiPanel1.removeAll();
					tiPanel1.add(tiL_Laber, null);
					return;
				}
				double scale = 0.7;
				boolean flag = true;
				Image image = ImageTool.scale(data, scale, flag);
				Pic pic = new Pic(image);
				pic.setSize(tiPanel1.getWidth(), tiPanel1.getHeight());
				pic.setLocation(0, 0);
				tiPanel1.removeAll();
				pic.setHorizontalAlignment(0);
				tiPanel1.add(pic, null);
				pic.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (this.OrderDesc.equals("")) {
				JOptionPane.showMessageDialog(this, "û��ͼƬ");
				return;
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public String getOrderDesc() {
			return OrderDesc;
		}

		public void setOrderDesc(String OrderDesc) {
			this.OrderDesc = OrderDesc;
		}
	}

	/**
	 * ��Ƭ��ʾ����
	 * 
	 * @param mrNo
	 */
	public void viewPhoto(String mrNo) {
		String photoName = mrNo + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null) {
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, null);

			}
		}
	}

	/**
	 * �õ�TTable����
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	/**
	 * ���͹���
	 */	
	public void onSendRe(){
		if (!getValueString("YEXE").equals("Y")){
			this.messageBox("������ִ��״̬������");
			return;	
		}
		getTTable("tableM").acceptText();
		int row = getTTable("tableM").getRowCount();
		if(row==0)
			return;
		TParm parmTable = getTTable("tableM").getParmValue();
		String flg ="N";//�Ƿ�ѡ����
		for (int m = 0; m < row; m++) {
			if (getTTable("tableM").getValueAt(m, 0).equals("Y"))	
				flg = "Y";
		}
		if(flg.equals("N")){
			messageBox("��ѡ����������");
			return;
		}			
//		System.out.println("parmTable:"+parmTable);
		TParm parmM = new TParm();		
		for (int i = 0; i < row; i++) {
			if (getTTable("tableM").getValueAt(i, 0).equals("Y")) {		
			parmM.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parmM.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parmM.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
			parmM.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
			parmM.addData("ORDER_DATETIME",
					parmTable.getValue("ORDER_DATETIME", i));
			parmM.addData("CANCELRSN_CODE",
					parmTable.getValue("CANCELRSN_CODE", i));
			// ΪCIS�ӿ��ṩ����--------------------------------------begin
			parmM.addData("LINKMAIN_FLG", parmTable.getValue("LINKMAIN_FLG", i));
			parmM.addData("LINK_NO", parmTable.getValue("LINK_NO", i));
			parmM.addData("CAT1_TYPE", parmTable.getValue("CAT1_TYPE", i));
			parmM.addData("ORDER_DR_CODE",
					parmTable.getValue("ORDER_DR_CODE", i));
			parmM.addData("BAR_CODE",parmTable.getValue("BAR_CODE", i));
			String nsexecdate = parmTable.getValue("ORDER_DATE", i)+
            parmTable.getValue("ORDER_DATETIME", i)+"00";
            parmM.addData("NS_EXEC_DATE",nsexecdate); 
			// ΪCIS�ӿ��ṩ����--------------------------------------end
            // ΪNIS�ӿ��ṩ����--------------------------------------begin
            parmM.addData("ORDER_CODE", parmTable.getValue("ORDER_CODE", i));
            parmM.addData("SEL_FLG", parmTable.getValue("SEL_FLG", i));
            parmM.addData("DC_DATE", parmTable.getValue("DC_DATE", i));
            parmM.addData("ORDER_DESC", parmTable.getValue("ORDER_DESC", i));
            // ΪNIS�ӿ��ṩ����--------------------------------------end
			parmM.addData(
					"NS_EXEC_DATE_REAL",
					parmTable.getData("NS_EXEC_DATE_REAL", i) == null ? new TNull(
							Timestamp.class) : parmTable.getData(
							"NS_EXEC_DATE_REAL", i));
			parmM.addData(
					"NS_EXEC_CODE_REAL",
					parmTable.getData("NS_EXEC_CODE_REAL", i) == null ? new TNull(
							String.class) : parmTable.getData(
							"NS_EXEC_CODE_REAL", i));
			//add by wukai on 20160603 ����ʱ��ӱ��뷽ʽ����Һ���� start
			parmM.addData("PUMP_CODE", parmTable.getValue("PUMP_CODE",i) == null ? new TNull(String.class) : parmTable.getValue("PUMP_CODE",i));
			parmM.addData("INFLUTION_RATE", parmTable.getValue("INFLUTION_RATE",i) == null ? new TNull(Double.class):parmTable.getValue("INFLUTION_RATE",i));
			//add by wukai on 20160603 ����ʱ��ӱ��뷽ʽ����Һ���� end
			
			}
	}
//		System.out.println("parmM:"+parmM);
		String cisflg = "N";
		for (int i = 0; i < parmM.getCount("CASE_NO"); i++) {
			if (parmM.getValue("CAT1_TYPE", i).equals("PHA"))
				cisflg = "Y";
		}
		// ҩ��
		if (cisflg.equals("Y")) {
			// ICU��CCUע��
			String caseNO = parmM.getValue("CASE_NO", 0);
			boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
			boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
			//�����ڷ�ҩ����
			TParm oral = onOral(caseNO);			
			if (IsICU || IsCCU||oral.getCount()>0) {
				String typeF = "NBW";
				List list = new ArrayList();
				list.add(parmM);
				// ���ýӿ�
				TParm resultParm = Hl7Communications.getInstance()
						.Hl7MessageCIS(list, typeF);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
////////////////////////////////////////////zhangs add start
			//��Һ��
			if (this.checkIsCS5(caseNO)){
//				String typeF = "NBW";
				List list = new ArrayList();
				list.add(parmM);
				// ���ýӿ�
				TParm resultParm = Hl7Communications.getInstance()
				.Hl7MessageCIS(list, "CS5");
    			if (resultParm.getErrCode() < 0)
	        		messageBox(resultParm.getErrText());

			}
			////////////////////////////zhangs add end

			messageBox("���ͳɹ�");
		}
		tiPanel3.setLayout(null);
		tiPanel3.removeAll();
		this.panelInit();
		getTTable("tableM").removeRowAll();
		this.onClear();
  }
	/**
	 * �����ڷ�ҩ����
	 */	
	public TParm onOral(String caseNo){
		String sql =" SELECT * FROM ADM_INP"+
		" WHERE CASE_NO = '"+ caseNo+ "'"+
		" AND NURSING_CLASS IN ('N0','N1')";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/*
	 * ѡ����Ѫʱ����table
	 * */
	public void onSel(Object obj){
		TTable table_m = (TTable) this.getComponent("tableM");
		if("TRAN".equals(obj+"")){
			//fux modify 20150831 
			table_m.setParmValue(null);
			table_m.removeRowAll();  
			//table_m.setHeader("ѡ,30,boolean;Ժ������,150;ѪҺ����,170,BLD_CODE;����,50;���,150,SUBCAT_CODE;Ѫ��,50;RH,30;����ɸ��,60;�󽻲�,150,CROSS_MATCH_L;С����,150,CROSS_MATCH_S;���,60,RESULT;��Ѫ����,180,timestamp,yyyy/MM/dd HH:mm:ss;��Ѫ��,90,user");
			table_m.setHeader("ѡ,30,boolean;Ժ������,150;ѪҺ����,170,BLD_CODE;���,150,SUBCAT_CODE;Ѫ��,50;RH,30;����ɸ��,60;����,120,CROSS_MATCH_L;�β�,120,CROSS_MATCH_S;���,60,RESULT;ʵ����Ѫ��,80,int;��λ,50;��Ѫ��Ӧ,100,TRANSFUSION_REACTION;��Ѫʱ��,180,timestamp,yyyy/MM/dd HH:mm:ss;����ʱ��,180,timestamp,yyyy/MM/dd HH:mm:ss;��Ѫ��,90,user");//20151103 wangjc ���ӽ���ʱ�䣬���
//			table_m.setParmMap("SEL_FLG;BLOOD_NO;BLD_CODE;BLOOD_VOL;SUBCAT_CODE;BLD_TYPE;RH_FLG;SHIT_FLG;CROSS_MATCH_L;CROSS_MATCH_S;RESULT;BLDTRANS_TIME;BLDTRANS_USER");
			table_m.setParmMap("SEL_FLG;BLOOD_NO;BLD_CODE;SUBCAT_CODE;BLD_TYPE;RH_FLG;SHIT_FLG;CROSS_MATCH_L;CROSS_MATCH_S;RESULT;FACT_VOL;UNIT_CODE;TRANSFUSION_REACTION;BLDTRANS_TIME;BLDTRANS_END_TIME;BLDTRANS_USER");
			table_m.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,right;11,left;12,left;13,left;14,left;15,left");      
			if (getValueString("NEXE").equals("Y")) {    
				table_m.setLockColumns("1,2,3,4,5,6,7,8,9,11,13,14,15");//20151103 wangjc modify 12������
//				callFunction("UI|query|setEnabled", false); // ��ѯ��ť
			}else{  
				table_m.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
//				callFunction("UI|query|setEnabled", true); // ��ѯ��ť
			}    
			//fux modify 20150831 ��������Һ��ť ��ȫ����ť���ɵ��  
			TRadioButton all = (TRadioButton) this.getComponent("ALL");
			all.setEnabled(false);  
			((TTextField)getComponent("BAR_CODE")).grabFocus();    
			
		} else if("PACK".equals(obj+"")){
			table_m.setParmValue(null);
			table_m.removeRowAll();
			table_m.setHeader("�����,150;����,170;����,50;ȷ����Ա,120;ʹ��ʱ��,180,timestamp,yyyy/MM/dd HH:mm:ss");
			table_m.setParmMap("BAR_CODE;PACK_DESC;QTY;CHECK_USER;CHECK_DATE");
			table_m.setLockColumns("0,1,2,3,4");
			table_m.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,left;4,left");
			TRadioButton all = (TRadioButton) this.getComponent("ALL");
			all.setEnabled(true); 
		}
		else{
			table_m.setParmValue(null);
			table_m.removeRowAll();
			table_m.setHeader("ѡ,30,boolean;ִ,30,boolean;��,30,boolean;��,30;Ӧִ��ʱ��,140;ҽ������,150;ÿ������,60;��λ,60,JL;����,60;��λ,60,JL;Ƶ��,80,PC;�÷�,80,YF;���뷽ʽ,100,PUMP_COMBO;��Һ����(ml/h),120,double,########0.000;ҽ����ע,150;����ҽ��,80,user;ִ������,150,timestamp,yyyy/MM/dd HH:mm:ss;ִ����Ա,80,user;ͣ������,120;ͣ��ҽʦ,80,user;��ִ��ԭ��,100,RESN;����,80;����,150;����ԭ��,150;�ѷ���,80");
			table_m.setParmMap("SEL_FLG;EXE_FLG;LINKMAIN_FLG;LINK_NO;NS_EXEC_DATE;ORDER_DESC;MEDI_QTY;MEDI_UNIT;DOSAGE_QTY;DOSAGE_UNIT;FREQ_CODE;ROUTE_CODE;PUMP_CODE;INFLUTION_RATE;DR_NOTE;ORDER_DR_CODE;NS_EXEC_DATE_REAL;NS_EXEC_CODE_REAL;DC_DATE;DC_DR_CODE;CANCELRSN_CODE;INV_CODE;BAR_CODE;LATE_REASON;SENDCS5_FLG");
			TRadioButton all = (TRadioButton) this.getComponent("ALL");
			all.setEnabled(true); 
		
		}
		
	}
	
	/*    
	 * ѡ��ִ��ʱ������ѯ��ť    
	 * */
	public void onSelButton(Object obj){            
		TTable table_m = (TTable) this.getComponent("tableM");
		TParm parm = new TParm();
		if(getValueString("R_TRAN").equals("Y")){
			if (getValueString("NEXE").equals("Y")) {      
				table_m.setLockColumns("1,2,3,4,5,6,7,8,9,11,13,14,15,18");//20151103 wangjc modify 12������
				//callFunction("UI|query|setEnabled", false); // ��ѯ��ť
				table_m.setParmValue(parm);
			}else if (!getValueString("NEXE").equals("Y")) {      
				table_m.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,18");
				table_m.setParmValue(parm);
				//callFunction("UI|query|setEnabled", true); // ��ѯ��ť
			}
		}else{  
			    table_m.setParmValue(parm);
				//callFunction("UI|query|setEnabled", true); // ��ѯ��ť
			
		}  
		
	}  
	
	
	/**
	 * ���õ��ӱ�ǩ�ӿ�(�������ϲ�����)
	 * 
	 * @param parm
	 * @author wangb 2015/05/25
	 */
	public void sendElectronicTagNew(TParm parm) {
		Map map = new HashMap();
		if (map == null) {
			return;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			if (map.get(parm.getValue("BOX_ESL_ID", i)) == null
					&& parm.getValue("BOX_ESL_ID", i) != null)
				map.put(parm.getValue("BOX_ESL_ID", i),
						parm.getValue("BOX_ESL_ID", i));
		}
		Iterator it = map.values().iterator();
		try {
			if (it.hasNext()) {
				String  patName= this.patInfo.getValue("PAT_NAME", 0);
				String stationDesc = this.patInfo.getValue("STATION_DESC", 0);
				String bedNo = this.patInfo.getValue("BED_NO_DESC", 0);
				String boxID = (String) it.next();
				int lightNum = 3;
				ElectronicTagUtil.login();
				boolean Send = ElectronicTagUtil.getInstance().sendEleTag(
						boxID, patName, stationDesc, bedNo, lightNum);
//				if (Send) {
//					System.out.println("==========���õ��ӱ�ǩ�ӿڳɹ�=================");
//				} else {
//					System.out.println("==========���õ��ӱ�ǩ�ӿ�ʧ��=================");
//				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
//			System.out.println("==========���õ��ӱ�ǩ�ӿ�ʧ��=================");
		}
	}
	
	/*
	 * δִ��ҽ����ѯ
	 * add by yangjj 20151022
	 * */
	public void onUnExecQuery(){
		TParm parm = new TParm();
		parm.setData("STATUS", "SINGLE");
		this.openDialog(
				"%ROOT%\\config\\inw\\INWOrderSingleExecQuery.x",parm);
	}
	
	
	private TComboBox getTComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
//////////////////////////////////////////////zhangs add start
	/**
	 * �Ƿ���CS5
	 * @param parm
	 * @return
	 */
	public boolean checkIsCS5(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean cs5Flg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query(inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        cs5Flg=result.getBoolean("CS5_FLG",0);
		return cs5Flg;
	}


	private TParm query(TParm inparm) {
		String Sql =" SELECT B.CS5_FLG "+
		" FROM ADM_INP A,SYS_DEPT B "+
		" WHERE A.CASE_NO='"+inparm.getValue("CASE_NO")+"' "+
		" AND B.DEPT_CODE=A.DEPT_CODE ";
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("CS5_FLG") < 0) {
			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		return tabParm;
	}
//////////////////////////////////////////////////////zhangs add end
	
	/**
	 * ��ѯSYS_FEE��NIS_FLG״̬ ��֤�Ƿ�ͬ��NIS��
	 * 
	 * 2017.04.13 zhanglei ͬ��NIS��״̬
	 */
	private TParm queryNISFLG(String ORDER_CODE) {
		String Sql =" SELECT SYNC_NIS_FLG "+
		" FROM SYS_FEE "+
		" WHERE ORDER_CODE  = '"+ORDER_CODE+"' ";
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if (tabParm.getCount() <= 0) {
			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		
		
		return tabParm;
	}
	
	/**
	 * ��Һ�÷���
	 */	
	public void onSendCS5(){
		if (!getValueString("NEXE").equals("Y")){
			return;	
		}
		getTTable("tableM").acceptText();
		int row = getTTable("tableM").getRowCount();
		if(row==0)
			return;
		TParm parmTable = getTTable("tableM").getParmValue();
		TParm parmM = new TParm();		
//		System.out.println("������"+getTTable("tableM").getParmValue());
		for (int i = 0; i < row; i++) {
			if (getTTable("tableM").getValueAt(i, 0).equals("Y")&&
					StringUtils.isNotEmpty(parmTable.getValue("PUMP_CODE",i))&&
					parmTable.getValue("SENDCS5_FLG",i).equals("N")) {	
				parmTable.setData("SENDCS5_FLG",i, "Y");
			parmM.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parmM.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parmM.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
			parmM.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
			parmM.addData("ORDER_DATETIME",
					parmTable.getValue("ORDER_DATETIME", i));
			parmM.addData("CANCELRSN_CODE",
					parmTable.getValue("CANCELRSN_CODE", i));
			// ΪCIS�ӿ��ṩ����--------------------------------------begin
			parmM.addData("LINKMAIN_FLG", parmTable.getValue("LINKMAIN_FLG", i));
			parmM.addData("LINK_NO", parmTable.getValue("LINK_NO", i));
			parmM.addData("CAT1_TYPE", parmTable.getValue("CAT1_TYPE", i));
			parmM.addData("ORDER_DR_CODE",
					parmTable.getValue("ORDER_DR_CODE", i));
			parmM.addData("BAR_CODE",parmTable.getValue("BAR_CODE", i));
			String nsexecdate = parmTable.getValue("ORDER_DATE", i)+
            parmTable.getValue("ORDER_DATETIME", i)+"00";
            parmM.addData("NS_EXEC_DATE",nsexecdate); 
			// ΪCIS�ӿ��ṩ����--------------------------------------end
            // ΪNIS�ӿ��ṩ����--------------------------------------begin
            parmM.addData("ORDER_CODE", parmTable.getValue("ORDER_CODE", i));
            parmM.addData("SEL_FLG", parmTable.getValue("SEL_FLG", i));
            parmM.addData("DC_DATE", parmTable.getValue("DC_DATE", i));
            parmM.addData("ORDER_DESC", parmTable.getValue("ORDER_DESC", i));
            // ΪNIS�ӿ��ṩ����--------------------------------------end
			parmM.addData(
					"NS_EXEC_DATE_REAL",
					parmTable.getData("NS_EXEC_DATE_REAL", i) == null ? new TNull(
							Timestamp.class) : parmTable.getData(
							"NS_EXEC_DATE_REAL", i));
			parmM.addData(
					"NS_EXEC_CODE_REAL",
					parmTable.getData("NS_EXEC_CODE_REAL", i) == null ? new TNull(
							String.class) : parmTable.getData(
							"NS_EXEC_CODE_REAL", i));
			//add by wukai on 20160603 ����ʱ��ӱ��뷽ʽ����Һ���� start
			parmM.addData("PUMP_CODE", parmTable.getValue("PUMP_CODE",i) == null ? new TNull(String.class) : parmTable.getValue("PUMP_CODE",i));
			parmM.addData("INFLUTION_RATE", parmTable.getValue("INFLUTION_RATE",i) == null ? new TNull(Double.class):parmTable.getValue("INFLUTION_RATE",i));
			//add by wukai on 20160603 ����ʱ��ӱ��뷽ʽ����Һ���� end
			
			}
	}
		String cisflg = "N";
		for (int i = 0; i < parmM.getCount("CASE_NO"); i++) {
			if (parmM.getValue("CAT1_TYPE", i).equals("PHA"))
				cisflg = "Y";
		}
		// ҩ��
		if (cisflg.equals("Y")) {
			// ICU��CCUע��
			String caseNO = parmM.getValue("CASE_NO", 0);
////////////////////////////////////////////zhangs add start
			//��Һ��
			if (this.checkIsCS5(caseNO)){
//				String typeF = "NBW";
				List list = new ArrayList();
				list.add(parmM);
				// ���ýӿ�
				TParm resultParm = Hl7Communications.getInstance()
				.Hl7MessageCIS(list, "CS5");
    			if (resultParm.getErrCode() < 0)
	        		messageBox(resultParm.getErrText());

			}
			////////////////////////////zhangs add end
			this.getTable("tableM").setParmValue(parmTable);
//			System.out.println("����1��"+getTTable("tableM").getParmValue());
		}
  }


}
