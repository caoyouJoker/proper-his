package com.javahis.ui.bil;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import jdo.bil.BIL;
import jdo.bil.SPCINVRecordTool;
import jdo.ibs.IBSTool;
import jdo.ope.OPEOpBookTool;
import jdo.spc.SPCINVRecord;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ���������ü�¼
 * </p>
 * 
 * <p>
 * Description: ���������ü�¼
 * </p>
 * 
 * Copyright: Copyright (c) ProperSoft 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILSPCINVRecordControl
        extends TControl implements MouseListener {

    TTable table;
    private static final String ACTION_PATH = "action.bil.SPCINVRecordAction"; // action��·��
    TParm opMedParm;// �����лش�����
    private int packGroupNo = 0;
    SPCINVRecord record;
    private String type;// ��ѯ������
   
    /**
     * ��ʼ��
     * */
    public void onInit() {
        super.onInit();
        initComponent();// ��ʼ���������
        initUIState(); // ��ʼ����������
        // this.setValue("MR_NO", "1747");
        // onMrNo();// //////////////////////////////
    }

    /**
     * ���ȫ���ؼ�
     */
    public void initComponent() {
        table = (TTable) this.getComponent("Table");
        table.getTable().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        // ���õ����˵�
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "");
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "SYS_FEE",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        callFunction("UI|ORDER_CODE|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popReturn");
        table.addEventListener("Table->" + TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxClick");
        ((TTextField) this.getComponent("MR_NO")).addMouseListener(this);
        ((TTextField) this.getComponent("IPD_NO")).addMouseListener(this);
        ((TTextField) this.getComponent("PAT_NAME")).addMouseListener(this);
    }

    /**
     * ��ʼ����������
     */
    public void initUIState() {
        // Ĭ��ֵ
        this.setValue("ADM_TYPE", "I");
        this.setValue("AR_AMT", 0);
        this.setValue("DEPT", Operator.getDept());
        record = new SPCINVRecord();
        record.onQuery();
        table.setDataStore(record);
        table.setDSValue();
        table.setLockRows(null); // �������
        // ������Ĭ��״̬
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|commit|setEnabled", false);
        callFunction("UI|deleteRow|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|operation|setEnabled", true);
        // �������Ĭ��״̬
        callFunction("UI|MR_NO|setEditable", true);
        // callFunction("UI|CASE_NO|setEnabled", true);
        callFunction("UI|IPD_NO|setEditable", true);
        callFunction("UI|PAT_NAME|setEditable", true);
        callFunction("UI|AGE|setEnabled", true);
        callFunction("UI|SEX_CODE|setEnabled", true);
        // callFunction("UI|ADM_TYPE|setEnabled", true);
        callFunction("UI|DEPT_CODE|setEnabled", true);
        callFunction("UI|STATION_CODE|setEnabled", true);
        callFunction("UI|DR_CODE|setEnabled", true);
        // callFunction("UI|OP_ROOM|setEnabled", true);
        callFunction("UI|BAR_CODE|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", true);
        callFunction("UI|DEPT|setEnabled", true);
        callFunction("UI|PACK1_CODE|setEnabled", true);
        // callFunction("UI|billFlg|setEnabled", true);
        callFunction("UI|SAVE_BTN|setEnabled", true);
        callFunction("UI|BAR_CODE|grabFocus");// ��ý���
//        this.setValue("OP_DEPT", Operator.getDept());
//        this.setValue("OP_DR", Operator.getID());
    }

    /**
     * ���ݲ����Ų�ѯ
     * */
    public void onMrNo() {
        String admType = this.getValueString("ADM_TYPE");
        if (admType.equals("")) {
            this.messageBox("����ѡ���ż�ס��");
            return;
        }
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            this.messageBox("�����Ų���Ϊ��");
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);
        this.setValue("MR_NO", mrNo);
        
        //modify by huangtt 20160928 EMPI���߲�����ʾ  start 
        Pat pat = Pat.onQueryByMrNo(mrNo);       
		 if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
	            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
	            this.setValue("MR_NO", pat.getMrNo());
	            mrNo = pat.getMrNo();
	     }
       //modify by huangtt 20160928 EMPI���߲�����ʾ  end
        
       
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("MR_NO", mrNo);
        TParm result = TIOM_AppServer.executeAction(ACTION_PATH, "onMrNo", parm);
        if (result.getCount("CASE_NO") < 0) {
            this.messageBox("���޴˲���");
            return;
        }
        this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
        this.setValue("CASE_NO", result.getValue("CASE_NO", 0));// ������
        this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
        Timestamp birthday =
                StringTool.getTimestamp(result.getValue("BIRTH_DATE", 0).substring(0, 19),
                                        "yyyy-MM-dd HH:mm:ss");
        if (birthday != null) {
            String age =
                    StringTool.CountAgeByTimestamp(birthday, SystemTool.getInstance().getDate())[0];// ����
            this.setValue("AGE", age);
        } else {
            this.setValue("AGE", "");
        }
        this.setValue("SEX_CODE", result.getValue("SEX_CODE", 0));
        this.setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
        this.setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
        this.setValue("DR_CODE", result.getValue("DR_CODE", 0));
        String opRoomSql =
                " SELECT OP_ROOM FROM SPC_INV_RECORD                         "
                        + "  WHERE CASE_NO = '#' AND OP_ROOM IS NOT NULL     "
                        + "  ORDER BY ORDER_DATE DESC ";
        opRoomSql = opRoomSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        TParm opRoom = new TParm(TJDODBTool.getInstance().select(opRoomSql));
        if (opRoom.getCount("OP_ROOM") > 0) {
            this.setValue("OP_ROOM", opRoom.getValue("OP_ROOM", 0));
        } else {
        	//==== wukai 20161229 �ӽ��뻤��ƽ̨���벡������ start
        	//this.messageBox("444");
        	opRoomSql = "SELECT ROOM_NO FROM OPE_OPBOOK WHERE CASE_NO = '#' AND CANCEL_FLG <> 'Y' ORDER BY OP_DATE DESC";
        	opRoomSql = opRoomSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        	parm = new TParm(TJDODBTool.getInstance().select(opRoomSql));
        	if(parm.getCount("ROOM_NO") > 0) {
        		 this.setValue("OP_ROOM", parm.getValue("ROOM_NO", 0));
        	}
        	//==== wukai 20161229 �ӽ��뻤��ƽ̨���벡������ end
        }
        String opDeptDrSql =
                " SELECT ORDER_DEPT_CODE, ORDER_DR_CODE FROM SPC_INV_RECORD "
                        + "WHERE CASE_NO = '#'    "
                        + "  AND ORDER_DEPT_CODE IS NOT NULL AND ORDER_DR_CODE IS NOT NULL "
                        + "ORDER BY ORDER_DATE DESC ";
        opDeptDrSql = opDeptDrSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        TParm opDeptDr = new TParm(TJDODBTool.getInstance().select(opDeptDrSql));
        if (opDeptDr.getCount() > 0) {
            this.setValue("OP_DEPT", opDeptDr.getValue("ORDER_DEPT_CODE", 0));
            this.setValue("OP_DR", opDeptDr.getValue("ORDER_DR_CODE", 0));
        }
        callFunction("UI|MR_NO|setEditable", false);
        callFunction("UI|IPD_NO|setEditable", false);
        // callFunction("UI|CASE_NO|setEnabled", false);//��������ù�
        callFunction("UI|PAT_NAME|setEditable", false);
        callFunction("UI|AGE|setEnabled", false);
        callFunction("UI|SEX_CODE|setEnabled", false);
        callFunction("UI|DEPT_CODE|setEnabled", false);
        callFunction("UI|STATION_CODE|setEnabled", false);
        callFunction("UI|DR_CODE|setEnabled", false);
        callFunction("UI|BAR_CODE|grabFocus");// ����Ʒ���롱��ý���
        //========pangben 2015-10-16 ����ٴ�·����ʾ
        String sql="SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"+result.getValue("CASE_NO", 0)+"'";
        TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
        this.setValue("SCHD_CODE", "");
        this.setValue("CLNCPATH_CODE", "");
        if (null==admParm.getValue("CLNCPATH_CODE",0)||
        		admParm.getValue("CLNCPATH_CODE",0).length()<=0) {
        	callFunction("UI|SCHD_CODE|setEnabled",false);// 
		}else{
		 	callFunction("UI|SCHD_CODE|setEnabled",true);
		 	this.setValue("CLNCPATH_CODE",admParm.getValue("CLNCPATH_CODE",0));
			TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
			combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
	        combo_schd.onQuery();
	        this.setValue("SCHD_CODE",admParm.getValue("SCHD_CODE",0));
		}
    }

    /**
     * ����Ʒ���롱�س��¼�
     * */
    public void onBarCode() {
        if (this.getValueString("MR_NO").trim().equals("")) {
            this.messageBox("�����벡����");
            return;
        }
        if (this.getValueString("CASE_NO").trim().equals("")) {
            this.messageBox("�������û���Ϣ");
            return;
        }
        String barCode = this.getValueString("BAR_CODE").toUpperCase();
        String sql1 = "SELECT INV_CODE FROM INV_STOCKDD WHERE RFID = '" + barCode + "'";
        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
        String sql2 = "SELECT TOXIC_ID FROM IND_CONTAINERD WHERE TOXIC_ID = '" + barCode + "'";
        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
        String sysFeeSql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='"+barCode+"' AND ACTIVE_FLG = 'Y'";//add by wangjc 20171219 ������¼����ͣ�õ�ҽ��
        TParm sysFeeParm = new TParm(TJDODBTool.getInstance().select(sysFeeSql));//add by wangjc 20171219 ������¼����ͣ�õ�ҽ��
        if (parm1.getCount("INV_CODE") >= 1 || parm2.getCount("TOXIC_ID") >= 1) {
        	//fux modify 20151218���ӹ�����ʾ
            String sqlDate =
                "SELECT RFID FROM INV_STOCKDD WHERE RFID = '" + barCode + "' AND VALID_DATE < sysdate ";
            TParm parmDate = new TParm(TJDODBTool.getInstance().select(sqlDate));
            //fux modify 20151218
            if (parmDate.getCount("RFID") >= 1) {   
            	 messageBox("����Ʒ�Ѿ�����!");
            	 return;    
            }
        	
            String sql =
                    "SELECT BUSINESS_NO FROM SPC_INV_RECORD WHERE BAR_CODE = '" + barCode + "'";
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            

            
            if (parm.getCount("BUSINESS_NO") >= 1) {
                int i = messageBox("����Ʒ�Ѿ�ɨ�����Ƿ���ɨһ��", "����Ʒ�Ѿ�ɨ�����Ƿ���ɨһ�飿", this.YES_NO_OPTION);
                if (i == 1) {
                    return;
                }
            }
            String buff = record.isFilter() ? record.FILTER : record.PRIMARY;
            TParm tableParm = record.getBuffer(buff);
            for (int i = 0; i < tableParm.getCount(); i++) {
                String barCodeTable = tableParm.getValue("BAR_CODE", i);
                if (barCodeTable.equals(barCode)) {
                    return;
                }
            }
        }else if(sysFeeParm.getCount() <= 0){//add by wangjc 20171219 ������¼����ͣ�õ�ҽ��
        	this.messageBox("��ҩƷ��ͣ��");
        	return;
        }
        
        
        
        TParm orderParm = getOrderData(barCode, true);
        if (orderParm.getErrCode() < 0) {
            this.messageBox(orderParm.getErrText());
            this.setValue("BAR_CODE", "");
            this.setValue("BAR_DESC", "");
            this.setValue("BAR_QTY", "");
            return;
        }
        //System.out.println("----------------orderParm-------------"+orderParm);
        record.addRowData(orderParm);
        record.setFilter(" SETMAIN_FLG='Y' ");
        record.filter();
        table.setDSValue();
        this.setValue("BAR_CODE", "");
        this.setValue("BAR_DESC",
                      orderParm.getValue("INV_DESC").equals("") ? orderParm.getValue("ORDER_DESC")
                              : orderParm.getValue("INV_DESC"));
        this.setValue("BAR_QTY", orderParm.getData("QTY") + "");
        this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
    }

    /**
     * ���ܷ���ֵ����
     * 
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        if (this.getValueString("MR_NO").trim().equals("")) {
            this.messageBox("�����벡����");
            return;
        }
        if (this.getValueString("CASE_NO").trim().equals("")) {
            this.messageBox("�������û���Ϣ");
            return;
        }
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        // if ((parm.getValue("CAT1_TYPE").equals("LIS") ||
        // parm.getValue("CAT1_TYPE").equals("RIS"))
        // && parm.getValue("ORDERSET_FLG").equals("Y")) {
        // this.messageBox("��������������Ŀ");
        // return;
        // }
        TParm orderParm = getOrderData(parm.getValue("ORDER_CODE"), false);
        if (orderParm.getErrCode() < 0) {
            this.messageBox(orderParm.getErrText());
            this.setValue("ORDER_CODE", "");
            this.setValue("ORDER_DESC", "");
            // this.setValue("BAR_QTY", "");
            return;
        }
        // System.out.println("----------------orderParm-------------"+orderParm);
        record.addRowData(orderParm);
        record.setFilter(" SETMAIN_FLG='Y' ");    
        record.filter();
        table.setDSValue();
        this.setValue("ORDER_CODE", "");
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        // this.setValue("BAR_QTY", orderParm.getData("QTY"));
        this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
    }

    /**
     * �ײͲ�ѯ
     * ����packGroupNo
     * */
    public void onPackCode(String packCode) {
        if (this.getValueString("MR_NO").trim().equals("")) {
            this.messageBox("�����벡����");
            return;
        }
        if (this.getValueString("CASE_NO").trim().equals("")) {
            this.messageBox("�������û���Ϣ");
            return;
        }
        String pack1Code = "";
        if (StringUtil.isNullString(packCode)) {
            pack1Code = this.getValueString("PACK1_CODE");
        } else {
            pack1Code = packCode;
        }
        // String pack1Code = this.getValueString("PACK1_CODE");
        int length = pack1Code.length();
        if (length == 12) {
            String sql1 = "";
            if (pack1Code.substring(6, 12).equals("000000")) { // ���ư�
                sql1 =
                        "SELECT DISTINCT A.BARCODE PACK_CODE, B.PACK_DESC, D.INV_CODE, D.INV_CHN_DESC, C.DESCRIPTION, C.QTY, "
                                + "       C.STOCK_UNIT, C.OPT_USER, C.OPT_DATE, C.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY "
                                + "  FROM INV_PACKSTOCKM A, INV_PACKM B, INV_PACKD C, INV_BASE D "
                                + " WHERE A.PACK_CODE = B.PACK_CODE "
                                + "   AND B.PACK_CODE = C.PACK_CODE "
                                + "   AND C.INV_CODE = D.INV_CODE   "
                                + "   AND A.BARCODE = '#'           ";
            } else {// ������
                sql1 =
                        "SELECT DISTINCT A.BARCODE PACK_CODE, B.PACK_DESC, C.INV_CODE, C.INV_CHN_DESC, A.DESCRIPTION, A.QTY, "
                                + "       A.STOCK_UNIT, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY "
                                + "  FROM INV_PACKSTOCKD_HISTORY A, INV_PACKM B, INV_BASE C "
                                + " WHERE A.PACK_CODE = B.PACK_CODE "
                                + "   AND A.INV_CODE = C.INV_CODE   "
                                + "   AND A.BARCODE = '#'           ";
            }
            sql1 = sql1.replaceFirst("#", pack1Code);  
            //System.out.println("---------���ư�    ��      ������-------------" + sql1);    
            TParm packParm = new TParm(TJDODBTool.getInstance().select(sql1));
            if (packParm.getCount() < 0) {
                this.messageBox("�޴��ײ�");
                return;
            }
            this.setValue("PACK1_CODE", packParm.getData("PACK_CODE", 0));
            this.setValue("PACK1_DESC", packParm.getData("PACK_DESC", 0));
            packGroupNo = getNextPackGroupNo(pack1Code);// ȡ����һ�ײ����
            for (int i = 0; i < packParm.getCount(); i++) {
                TParm rowParm = getOrderData(packParm.getValue("INV_CODE", i), true);// ////////////////////////////////////////////////
                if (rowParm.getErrCode() < 0) {
                    this.messageBox(packParm.getValue("INV_CHN_DESC", i) + rowParm.getErrText());
                    continue;
                }
                rowParm.setData("QTY", packParm.getData("QTY", i));
                rowParm.setData("AR_AMT",
                                StringTool.round(rowParm.getDouble("OWN_PRICE")
                                                         * packParm.getDouble("QTY", i), 2));
                rowParm.setData("PACK_BARCODE", pack1Code);
                rowParm.setData("PACK_DESC", packParm.getValue("PACK_DESC", 0));
                rowParm.setData("PACK_GROUP_NO", packGroupNo);
                rowParm.setData("USED_FLG", "Y");
                rowParm.setData("USED_QTY", 0);// ///////////////////////////////////////////
                rowParm.setData("NOTUSED_QTY", 0);// /////////////////////////////////////////////
                rowParm.setData("CHECK_FLG", "N");
                record.addRowData(rowParm);
            }
            record.setFilter(" SETMAIN_FLG='Y' ");
            record.filter();
            table.setDSValue();
            this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
        } else if (length == 8) { // �����ѡ���ͨҩƷ�ѡ���ͨ�Ĳ�
            String sql =
                    "SELECT B.PACK_CODE, B.PACK_DESC, B.CLASS_CODE, "
                            + "       CASE WHEN A.INV_CODE IS NULL THEN A.ORDER_CODE ELSE A.INV_CODE END AS INV_CODE, "
                            + "       CASE WHEN A.INV_CODE IS NULL THEN A.ORDER_DESC ELSE C.INV_CHN_DESC END AS INV_CHN_DESC, "
                            + "       A.DOSAGE_QTY AS QTY, A.DOSAGE_UNIT AS STOCK_UNIT, A.HIDE_FLG, A.OPT_USER, A.OPT_DATE, "
                            + "       A.OPT_TERM, A.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY, '' AS DESCRIPTION "
                            + "  FROM SYS_ORDER_PACKD A, SYS_ORDER_PACKM B, INV_BASE C "
                            + " WHERE A.PACK_CODE = B.PACK_CODE     "
                            + "   AND A.INV_CODE = C.INV_CODE(+)    "
                            + "   AND A.PACK_CODE = '#'             ";
            sql = sql.replaceFirst("#", pack1Code);
            //System.out.println("---------�����ѡ���ͨҩƷ�ѡ���ͨ�Ĳ�------------------" + sql);
            TParm invParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (invParm.getCount() < 0) {
                this.messageBox("�޴��ײ�");
                return;
            }
            this.setValue("PACK1_CODE", invParm.getData("PACK_CODE", 0));
            this.setValue("PACK1_DESC", invParm.getData("PACK_DESC", 0));
            for (int i = 0; i < invParm.getCount("INV_CODE"); i++) {
                TParm rowParm = getOrderData(invParm.getValue("INV_CODE", i), true);// ////////////////////////////////////////////////
                if (rowParm.getErrCode() < 0) {
                    this.messageBox(invParm.getValue("INV_CHN_DESC", i) + rowParm.getErrText());
                    continue;
                }
                rowParm.setData("QTY", invParm.getData("QTY", i));
                rowParm.setData("AR_AMT",
                                StringTool.round(rowParm.getDouble("OWN_PRICE")
                                                         * invParm.getDouble("QTY", i), 2));
                rowParm.setData("PACK_BARCODE", pack1Code);
                rowParm.setData("PACK_DESC", invParm.getValue("PACK_DESC", 0));
                rowParm.setData("PACK_GROUP_NO", "");// ���ײͲ�дPACK_GROUP_NO
                rowParm.setData("USED_FLG", "N");
                rowParm.setData("USED_QTY", 0);// ///////////////////////////////////////////
                rowParm.setData("NOTUSED_QTY", 0);// /////////////////////////////////////////////
                rowParm.setData("CHECK_FLG", "N");
                record.addRowData(rowParm);
            }
            record.setFilter(" SETMAIN_FLG='Y' ");
            record.filter();
            table.setDSValue();
            this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
        }
    }

    /**
     * ���������ü�¼
     */
    public void onOperation() {
        if (this.getValueString("MR_NO").trim().equals("")) {
            this.messageBox("�����벡����");
            return;
        }
        if (this.getValueString("CASE_NO").trim().equals("")) {
            this.messageBox("�������û���Ϣ");
            return;
        }
        if(StringUtils.isEmpty(this.getValueString("OP_DR"))){
        	this.messageBox("����ѡ������ҽ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("PACK", "DEPT", Operator.getDept());
        TParm result = (TParm) this.openDialog("%ROOT%\\config\\inv\\INVSPCPack.x", parm, false);
        if (result == null) {
            return;
        }
        String pack_code = result.getValue("BAR_CODE");// ��������������ײͱ���
        int length = pack_code.length();
        if (length == 12) {// �����������ư�
            onPackCode(pack_code);
        } else {// �ײ�---------8λ
            Set<String> invSet = new HashSet<String>();
            Set<String> orderSet = new HashSet<String>();
            for (int i = 0; i < result.getCount("INV_CODE"); i++) {
                invSet.add(result.getValue("INV_CODE", i));// ////////////////////////////////////////////////
                orderSet.add(result.getValue("ORDER_CODE", i));
            }
            String sql =
                    "SELECT B.PACK_CODE, B.PACK_DESC, B.CLASS_CODE, "
                            + "       CASE WHEN A.INV_CODE IS NULL THEN A.ORDER_CODE ELSE A.INV_CODE END AS INV_CODE, "
                            + "       CASE WHEN A.INV_CODE IS NULL THEN A.ORDER_DESC ELSE C.INV_CHN_DESC END AS INV_CHN_DESC, "
                            + "       A.DOSAGE_QTY AS QTY, A.DOSAGE_UNIT AS STOCK_UNIT, A.HIDE_FLG, A.OPT_USER, A.OPT_DATE, "
                            + "       A.OPT_TERM, A.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY, '' AS DESCRIPTION "
                            + "  FROM SYS_ORDER_PACKD A, SYS_ORDER_PACKM B, INV_BASE C "
                            + " WHERE A.PACK_CODE = B.PACK_CODE     "
                            + "   AND A.INV_CODE = C.INV_CODE(+)    "
                            + "   AND A.PACK_CODE = '#'             ";
            sql = sql.replaceFirst("#", pack_code);
//            System.out.println("---------�����ѡ���ͨҩƷ�ѡ���ͨ�Ĳ�------------------" + sql);
            TParm invParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (invParm.getCount() < 0) {
                this.messageBox("�޴��ײ�");
                return;
            }
            this.setValue("PACK1_CODE", invParm.getData("PACK_CODE", 0));
            this.setValue("PACK1_DESC", invParm.getData("PACK_DESC", 0));
            for (int i = 0; i < invParm.getCount("INV_CODE"); i++) {
                if (invSet.contains(invParm.getValue("INV_CODE", i))
                        || orderSet.contains(invParm.getValue("INV_CODE", i))) {// ////////////////////////////////////////////////
                    TParm rowParm = getOrderData(invParm.getValue("INV_CODE", i), true);// ////////////////////////////////////////////////
                    if (rowParm.getErrCode() < 0) {
                        this.messageBox(invParm.getValue("INV_CHN_DESC", i) + rowParm.getErrText());
                        continue;
                    }
                    rowParm.setData("QTY", invParm.getData("QTY", i));
                    rowParm.setData("AR_AMT",
                                    StringTool.round(rowParm.getDouble("OWN_PRICE")
                                                             * invParm.getDouble("QTY", i), 2));
                    rowParm.setData("PACK_BARCODE", pack_code);
                    rowParm.setData("PACK_DESC", invParm.getValue("PACK_DESC", 0));
                    rowParm.setData("PACK_GROUP_NO", "");// ���ײͲ�дPACK_GROUP_NO
                    rowParm.setData("USED_FLG", "N");
                    rowParm.setData("USED_QTY", 0);// ///////////////////////////////////////////
                    rowParm.setData("NOTUSED_QTY", 0);// /////////////////////////////////////////////
                    rowParm.setData("CHECK_FLG", "N");
                    // System.out.println("----------------orderParm-------------"+orderParm);
                    record.addRowData(rowParm);
                }                
                record.setFilter(" SETMAIN_FLG='Y' ");
                record.filter();               
            }// record.showDebug();
            
            table.setDSValue();
        }
        this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
    }

    /**
     * ��ȡҽ����ϸ��Ϣ
     * 
     * @param barCode
     * @param packFlg
     *            �Ƿ�Ϊ�ײͣ����龫��ҩƷ���ߵ�ֵ����Ϊ�ײͣ�Ϊ�˺ͼ���ҽ�����֣�
     * @return
     */
    public TParm getOrderData(String barCode, boolean packFlg) {
        TParm orderParm = new TParm();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String sql =
                "SELECT (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END FROM IND_CONTAINERD WHERE TOXIC_ID = '#') AS A,"// �龫
                        + "   (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END FROM PHA_BASE WHERE ORDER_CODE = '#') AS B,"// ҩƷ
                        + "   (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END FROM INV_STOCKDD WHERE RFID = '#') AS C,"// ��ֵ
                        + "   (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END FROM INV_BASE WHERE INV_CODE = '#') AS D,"// ��ֵ
                        + "   (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END FROM SYS_FEE "
                        + " WHERE CAT1_TYPE IN ('OTH','RIS','TRT','LIS') AND ORDER_CODE='#') AS E "// ������
                        + "  FROM DUAL";
        sql = sql.replaceAll("#", barCode);    
        //System.out.println("--------����--------sql---------------" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        result = result.getRow(0);
        if (result.getValue("A").equals("Y")) {
            // ================================�龫
            String toxicSql =
                    "SELECT B.TOXIC_ID, A.ORDER_CODE, A.ORDER_DESC, A.SPECIFICATION, A.UNIT_CODE, A.OWN_PRICE, A.EXEC_DEPT_CODE, "
                            + "       B.BATCH_NO, B.VALID_DATE, B.VERIFYIN_PRICE, B.BATCH_SEQ, B.CONTAINER_ID, B.CABINET_ID, A.ORDERSET_FLG "
                            + "  FROM SYS_FEE A, IND_CONTAINERD B    "
                            + " WHERE B.ORDER_CODE = A.ORDER_CODE    "
                            + "   AND B.TOXIC_ID = '#'               ";
            toxicSql = toxicSql.replaceFirst("#", barCode);
//            System.out.println("-----------�龫-----toxicSql---------------" + toxicSql);
            TParm toxicParm = new TParm(TJDODBTool.getInstance().select(toxicSql));
            if (toxicParm.getCount() > 0) {
                orderParm.setData("CLASS_CODE", "4");// �龫��ǡ�
                orderParm.setData("INV_CODE", "");
                orderParm.setData("INV_DESC", "");
                orderParm.setData("OPMED_CODE", "");
                orderParm.setData("ORDER_CODE", toxicParm.getData("ORDER_CODE", 0));
                orderParm.setData("ORDER_DESC", toxicParm.getData("ORDER_DESC", 0));
                orderParm.setData("OWN_PRICE", toxicParm.getDouble("OWN_PRICE", 0));
                orderParm.setData("QTY", 1);// ����ֻ����1
                orderParm.setData("UNIT_CODE", toxicParm.getData("UNIT_CODE", 0));
                orderParm.setData("AR_AMT",
                                  StringTool.round(toxicParm.getDouble("OWN_PRICE", 0) * 1, 2));
                orderParm.setData("BATCH_SEQ", toxicParm.getData("BATCH_NO", 0));
                orderParm.setData("VALID_DATE", toxicParm.getTimestamp("VALID_DATE", 0));
                orderParm.setData("BILL_FLG", "Y");// ////////////////////////////
                orderParm.setData("SPECIFICATION", toxicParm.getData("SPECIFICATION", 0));
                // if (toxicParm.getData("ORDERSET_FLG", 0).equals("Y")) {
                orderParm.setData("SETMAIN_FLG", "Y");//
                orderParm.setData("ORDERSET_CODE", toxicParm.getData("ORDER_CODE", 0));//
                // } else {
                // orderParm.setData("SETMAIN_FLG", "N");//
                // orderParm.setData("ORDERSET_CODE", "");//
                // }
                // orderParm.setData("DESC", toxicParm.getData("ORDER_DESC", 0));// ���ݿ���û�д��ֶ�
            } else return TParm.newErrParm(-1, "����Ʒ������");
        } else if (result.getValue("B").equals("Y")) {
            // ================================ҩƷ
            String phaSql =
                    "SELECT A.ORDER_CODE, A.ORDER_DESC, A.OWN_PRICE, A.UNIT_CODE, A.SPECIFICATION, A.ORDERSET_FLG "
                            + "  FROM SYS_FEE A              " + " WHERE A.CAT1_TYPE = 'PHA'    "
                            + "   AND A.ORDER_CODE = '#' ";
            phaSql = phaSql.replaceFirst("#", barCode);
//            System.out.println("--------ҩƷ--------phaSql---------------" + phaSql);
            TParm phaParm = new TParm(TJDODBTool.getInstance().select(phaSql));
            if (phaParm.getCount() > 0) {
                orderParm.setData("CLASS_CODE", "1");// ҩƷ��ǡ�
                orderParm.setData("INV_CODE", "");
                orderParm.setData("INV_DESC", "");
                orderParm.setData("OPMED_CODE", "");
                orderParm.setData("ORDER_CODE", phaParm.getData("ORDER_CODE", 0));
                orderParm.setData("ORDER_DESC", phaParm.getData("ORDER_DESC", 0));
                orderParm.setData("OWN_PRICE", phaParm.getDouble("OWN_PRICE", 0));
                orderParm.setData("QTY", 1);// ����ֻ����1
                orderParm.setData("UNIT_CODE", phaParm.getData("UNIT_CODE", 0));
                orderParm.setData("AR_AMT",
                                  StringTool.round(phaParm.getDouble("OWN_PRICE", 0) * 1, 2));
                orderParm.setData("BATCH_SEQ", "");
                orderParm.setData("VALID_DATE", "");
                orderParm.setData("BILL_FLG", "Y");// ///////////////////////////////
                orderParm.setData("SPECIFICATION", phaParm.getData("SPECIFICATION", 0));
                // if (phaParm.getData("ORDERSET_FLG", 0).equals("Y")) {
                orderParm.setData("SETMAIN_FLG", "Y");//
                orderParm.setData("ORDERSET_CODE", phaParm.getData("ORDER_CODE", 0));//
                // } else {
                // orderParm.setData("SETMAIN_FLG", "N");//
                // orderParm.setData("ORDERSET_CODE", "");//
                // }
                // orderParm.setData("DESC", phaParm.getData("ORDER_DESC", 0));// ���ݿ���û�д��ֶ�
            } else return TParm.newErrParm(-1, "����Ʒ������");
        } else if (result.getValue("C").equals("Y")) {
            // ================================��ֵ����
            String stockSql =
                    "SELECT B.INV_CODE, B.INV_CHN_DESC AS INV_DESC, B.ORDER_CODE, C.ORDER_DESC, B.DESCRIPTION,"
                            + "       A.BATCH_NO AS BATCH_SEQ, A.VALID_DATE, C.EXEC_DEPT_CODE,"
                            + "       CASE WHEN C.ORDER_CODE IS NULL THEN 0 ELSE C.OWN_PRICE END OWN_PRICE,"
                            + "       CASE WHEN C.ORDER_CODE IS NULL THEN B.STOCK_UNIT ELSE C.UNIT_CODE END UNIT_CODE,"
                            + "       CASE WHEN C.ORDER_CODE IS NULL THEN 'N' ELSE 'Y' END BILL_FLG, C.ORDERSET_FLG "
                            + "  FROM INV_STOCKDD A, INV_BASE B, SYS_FEE C "
                            + " WHERE A.INV_CODE = B.INV_CODE              "
                            + "   AND B.ACTIVE_FLG = 'Y'                   " 
                            + "   AND B.ORDER_CODE = C.ORDER_CODE(+)       " // �����order_codeĬ�ϼƷѣ����û��order_code��Ĭ�ϲ��Ʒ�
                            + "   AND A.RFID = '#'                   ";
            stockSql = stockSql.replaceFirst("#", barCode);
//            System.out.println("-----------��ֵ-----stockSql---------------" + stockSql);
            TParm stockParm = new TParm(TJDODBTool.getInstance().select(stockSql));
            if (stockParm.getCount("INV_CODE") > 0) {
                String checkSql = // wanglong add 20141013
                        "SELECT A.CASE_NO, SUM(A.QTY) AS SUM               "
                                + "  FROM SPC_INV_RECORD A, INV_STOCKDD B "
                                + " WHERE A.INV_CODE = B.INV_CODE        "
                                + "   AND A.BAR_CODE = B.RFID            "
                                + "   AND B.RFID = '#'                   "
                                + "   AND A.CASE_NO <> '@'               "
                                + "GROUP BY A.CASE_NO  ORDER BY SUM DESC ";
                checkSql = checkSql.replaceFirst("#", barCode);
                checkSql = checkSql.replaceFirst("@", this.getValueString("CASE_NO"));
                TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
                if (checkParm.getCount() > 0 && checkParm.getInt("SUM", 0) > 0) {
                    return TParm.newErrParm(-1, "��RFID�Ѿ�������ɨ���");
                }
                orderParm.setData("CLASS_CODE", "3");// ��ֵ��ǡ�
                orderParm.setData("INV_CODE", stockParm.getData("INV_CODE", 0));
                orderParm.setData("INV_DESC", stockParm.getData("INV_DESC", 0));
                orderParm.setData("OPMED_CODE", "");
                orderParm.setData("ORDER_CODE", stockParm.getData("ORDER_CODE", 0));
                orderParm.setData("ORDER_DESC", stockParm.getData("ORDER_DESC", 0));
                orderParm.setData("OWN_PRICE", stockParm.getDouble("OWN_PRICE", 0));
                orderParm.setData("QTY", 1);// ����ֻ����1
                orderParm.setData("UNIT_CODE", stockParm.getData("UNIT_CODE", 0));
                orderParm.setData("AR_AMT",
                                  StringTool.round(stockParm.getDouble("OWN_PRICE", 0) * 1, 2));
                orderParm.setData("BATCH_SEQ", stockParm.getData("BATCH_SEQ", 0));
                orderParm.setData("VALID_DATE", stockParm.getData("VALID_DATE", 0));
                orderParm.setData("BILL_FLG", stockParm.getData("BILL_FLG", 0));// ////////////////////////////////
                orderParm.setData("SPECIFICATION", stockParm.getData("DESCRIPTION", 0));
                // if (stockParm.getData("ORDERSET_FLG", 0).equals("Y")) {
                orderParm.setData("SETMAIN_FLG", "Y");//
                orderParm.setData("ORDERSET_CODE", stockParm.getData("ORDER_CODE", 0));//
                // } else {
                // orderParm.setData("SETMAIN_FLG", "N");//
                // orderParm.setData("ORDERSET_CODE", "");//
                // }
                // orderParm.setData("DESC", stockParm.getData("INV_DESC", 0));// ���ݿ���û�д��ֶ�
            } else return TParm.newErrParm(-1, "����Ʒ������");
        } else if (result.getValue("D").equals("Y")) {
            // ================================��ֵ����
            String invSql =
                    " SELECT A.INV_CODE, A.INV_CHN_DESC AS INV_DESC, A.ORDER_CODE, B.ORDER_DESC, A.DESCRIPTION, B.EXEC_DEPT_CODE,"
                            + "       CASE WHEN B.ORDER_CODE IS NULL THEN A.COST_PRICE ELSE B.OWN_PRICE END OWN_PRICE,"
                            + "       CASE WHEN B.ORDER_CODE IS NULL THEN A.STOCK_UNIT ELSE B.UNIT_CODE END UNIT_CODE,"
                            + "       CASE WHEN B.ORDER_CODE IS NULL THEN 'N' ELSE 'Y' END BILL_FLG, B.ORDERSET_FLG "
                            + "  FROM INV_BASE A, SYS_FEE B          "
                            + " WHERE A.ORDER_CODE = B.ORDER_CODE(+) " // �����order_codeĬ�ϼƷѣ����û��order_code��Ĭ�ϲ��Ʒ�
                            + "   AND A.ACTIVE_FLG = 'Y'             " 
                            + "   AND A.INV_CODE = '#' ";
            invSql = invSql.replaceFirst("#", barCode);
//            System.out.println("---------��ֵ-------invSql---------------" + invSql);
            TParm invParm = new TParm(TJDODBTool.getInstance().select(invSql));
            if (invParm.getCount("INV_CODE") > 0) {
                orderParm.setData("CLASS_CODE", "2");// ��ֵ��ǡ�
                orderParm.setData("INV_CODE", invParm.getData("INV_CODE", 0));
                orderParm.setData("INV_DESC", invParm.getData("INV_DESC", 0));
                orderParm.setData("OPMED_CODE", "");
                orderParm.setData("ORDER_CODE", invParm.getData("ORDER_CODE", 0));
                orderParm.setData("ORDER_DESC", invParm.getData("ORDER_DESC", 0));
                orderParm.setData("OWN_PRICE", invParm.getDouble("OWN_PRICE", 0));
                orderParm.setData("QTY", 1);// ����ֻ����1
                orderParm.setData("UNIT_CODE", invParm.getData("UNIT_CODE", 0));
                orderParm.setData("AR_AMT",
                                  StringTool.round(invParm.getDouble("OWN_PRICE", 0) * 1, 2));
                orderParm.setData("BATCH_SEQ", "");
                orderParm.setData("VALID_DATE", "");
                orderParm.setData("BILL_FLG", invParm.getData("BILL_FLG", 0));// ///////////////////////////////
                orderParm.setData("SPECIFICATION", invParm.getData("DESCRIPTION", 0));
                // if (invParm.getData("ORDERSET_FLG", 0).equals("Y")) {
                orderParm.setData("SETMAIN_FLG", "Y");//
                orderParm.setData("ORDERSET_CODE", invParm.getData("ORDER_CODE", 0));//
                // } else {
                // orderParm.setData("SETMAIN_FLG", "N");//
                // orderParm.setData("ORDERSET_CODE", "");//
                // }
                // orderParm.setData("DESC", invParm.getData("INV_DESC", 0));// ���ݿ���û�д��ֶ�
            } else return TParm.newErrParm(-1, "����Ʒ������");
        } else if (result.getValue("E").equals("Y")) {
            // ================================��ֵ����������
            String sysFeeSql =
                    "SELECT  A.ORDER_CODE, A.ORDER_DESC, A.OWN_PRICE, A.UNIT_CODE, A.SPECIFICATION, B.INV_CODE, A.ORDERSET_FLG "
                            + " FROM SYS_FEE A, INV_BASE B                  "
                            + "WHERE CAT1_TYPE IN ('OTH','RIS','TRT','LIS') "
                            + "  AND A.ORDER_CODE = B.ORDER_CODE(+)         "
                            + "  AND (B.ACTIVE_FLG = 'Y' OR B.ACTIVE_FLG IS NULL) " 
                            + "  AND A.ORDER_CODE = '#'";
            sysFeeSql = sysFeeSql.replaceFirst("#", barCode);
            //System.out.println("----------�����ѵ���������------sysFeeSql---------------" + sysFeeSql);
            TParm sysFeeParm = new TParm(TJDODBTool.getInstance().select(sysFeeSql));
            if (sysFeeParm.getCount("INV_CODE") > 0) {
                if (!sysFeeParm.getValue("INV_CODE", 0).equals("")) {
                    orderParm.setData("CLASS_CODE", "2");// ��ֵ��ǡ�
                } else {
                    orderParm.setData("CLASS_CODE", "5");// �����ѱ�ǡ�
                }
                orderParm.setData("INV_CODE", "");
                orderParm.setData("INV_DESC", "");
                orderParm.setData("OPMED_CODE", "");
                orderParm.setData("ORDER_CODE", sysFeeParm.getData("ORDER_CODE", 0));
                orderParm.setData("ORDER_DESC", sysFeeParm.getData("ORDER_DESC", 0));
                orderParm.setData("OWN_PRICE", sysFeeParm.getDouble("OWN_PRICE", 0));
                orderParm.setData("QTY", 1);// ����ֻ����1
                orderParm.setData("UNIT_CODE", sysFeeParm.getData("UNIT_CODE", 0));
                orderParm.setData("AR_AMT",
                                  StringTool.round(sysFeeParm.getDouble("OWN_PRICE", 0) * 1, 2));
                orderParm.setData("BATCH_SEQ", "");
                orderParm.setData("VALID_DATE", "");
                orderParm.setData("BILL_FLG", "Y");// ///////////////////////////////////////
                orderParm.setData("SPECIFICATION", sysFeeParm.getData("SPECIFICATION", 0));
                // if (sysFeeParm.getData("ORDERSET_FLG", 0).equals("Y")) {
                // if (packFlg == true || sysFeeParm.getData("ORDERSET_FLG", 0).equals("Y")) {
                orderParm.setData("SETMAIN_FLG", "Y");//
                orderParm.setData("ORDERSET_CODE", sysFeeParm.getData("ORDER_CODE", 0));//
                // } else {
                // orderParm.setData("SETMAIN_FLG", "N");//
                // orderParm.setData("ORDERSET_CODE", "");//
                // }
                // orderParm.setData("DESC", sysFeeParm.getData("ORDER_DESC", 0));// ���ݿ���û�д��ֶ�
            } else return TParm.newErrParm(-1, "����Ʒ������");
        } else {
            return TParm.newErrParm(-1, "û�д���Ʒ");
            // //////////////////////////////////////////////////////
        }
        orderParm.setData("BUSINESS_NO", "");
        orderParm.setData("SEQ", "");
        if (this.getValueString("CASE_NO").trim().equals("")) {
            return TParm.newErrParm(-1, "�������벡����");
        }
        orderParm.setData("CASE_NO", this.getValueString("CASE_NO"));
        orderParm.setData("MR_NO", this.getValueString("MR_NO"));
        orderParm.setData("BAR_CODE", barCode);// ////////////////////////
        orderParm.setData("BILL_DATE", "");
        orderParm.setData("NS_CODE", Operator.getID());
        orderParm.setData("OP_ROOM", this.getValueString("OP_ROOM"));
        orderParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));// //////////
        orderParm.setData("EXE_DEPT_CODE", Operator.getDept());// ִ�п���
//        orderParm.setData("ORDER_DEPT_CODE", this.getValueString("OP_DEPT"));// ��������wanglong add 20140826
        orderParm.setData("ORDER_DEPT_CODE", this.getValueString("DEPT_CODE"));//modify by wangjc 20171226 
        orderParm.setData("ORDER_DR_CODE", this.getValueString("OP_DR"));// ����ҽ��wanglong add 20140826
        orderParm.setData("CASE_NO_SEQ", "");
        orderParm.setData("SEQ_NO", "");
        orderParm.setData("OPT_USER", Operator.getID());
        orderParm.setData("OPT_DATE", now);
        orderParm.setData("OPT_TERM", Operator.getIP());
        orderParm.setData("YEAR_MONTH", StringTool.getString(now, "yyyyMM"));
        orderParm.setData("RECLAIM_USER", "");
        orderParm.setData("RECLAIM_DATE", "");
        orderParm.setData("REQUEST_NO", "");
        orderParm.setData("STOCK_FLG", "");
        orderParm.setData("COMMIT_FLG", "N");
        orderParm.setData("CASHIER_CODE", "");
        orderParm.setData("PAT_NAME", this.getValueString("PAT_NAME"));// ����
        orderParm.setData("PACK_BARCODE", "");// �ײͺţ���ͬPACK_CODE
        orderParm.setData("PACK_DESC", "");// �ײ���
        orderParm.setData("PACK_GROUP_NO", "");// �ײ����
        orderParm.setData("USED_FLG", "Y");
        orderParm.setData("CHECK_FLG", "");
        orderParm.setData("CHECK_NO", "");
        orderParm.setData("SCAN_ORG_CODE", "");
        orderParm.setData("CANCEL_FLG", "");
        orderParm.setData("RESET_BUSINESS_NO", "");
        orderParm.setData("RESET_SEQ_NO", "");
        orderParm.setData("ORDER_DATE", now);//
        orderParm.setData("ORDERSET_GROUP_NO", "");//
        orderParm.setData("SCHD_CODE", this.getValue("SCHD_CODE"));//===pangben 2015-10-16 ����ٴ�·��ʱ��
        return orderParm;
    }

    /**
     * ��ѯ
     * type="INV" ��ʾֻ��ʾ�Ĳ�
     * */
    public void onQuery(String type) {// this.messageBox(""+type);
        this.type = type;
        if (this.getValueString("MR_NO").trim().equals("")) {
            this.messageBox("�����벡����");
            return;
        }
        String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
        Timestamp today = SystemTool.getInstance().getDate();
        // Timestamp yesterday = StringTool.rollDate(today, -1);
        String todayStr = StringTool.getString(today, "yyyyMMdd");
        // String yesterdayStr = StringTool.getString(yesterday, "yyyyMMdd");
        String sql =
                "SELECT * FROM SPC_INV_RECORD                        "
                        + "WHERE MR_NO='#' AND CASE_NO ='#'          "
                        + "  AND ORDER_DATE BETWEEN TO_DATE('# 000000', 'YYYYMMDD HH24MISS') "
                        + "                    AND TO_DATE('# 235959', 'YYYYMMDD HH24MISS') "
                        + "    @    @     /*@*/ ORDER BY BUSINESS_NO ASC, TO_NUMBER(SEQ) ASC";
        sql = sql.replaceFirst("#", mrNo);
        sql = sql.replaceFirst("#", this.getValueString("CASE_NO"));
        sql = sql.replaceFirst("#", todayStr);
        sql = sql.replaceFirst("#", todayStr);
        if (!StringUtil.isNullString(type) && type.equals("INV")) {// ֻ��ʾ�Ĳ�
            sql = sql.replaceFirst("@", " AND CLASS_CODE IN('2','3') ");// wanglong add 20140605
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (this.getValueString("billFlg").equals("Y")) {// �ѼƷ�
            sql = sql.replaceFirst("@", " AND CASE_NO_SEQ IS NOT NULL AND SEQ_NO IS NOT NULL");
        } else if (this.getValueString("billFlg").equals("N")) {
            sql = sql.replaceFirst("@", " AND CASE_NO_SEQ IS NULL AND SEQ_NO IS NULL");
        }
        // if (this.getValueString("commitFlg").equals("Y")) {// ���ύ ��ʱû��
        // sql = sql.replaceFirst("@", " AND COMMIT_FLG = 'Y'");
        // } else if (this.getValueString("commitFlg").equals("N")) {
        // sql = sql.replaceFirst("@", " AND COMMIT FLG = 'N'");
        // }
//        System.out.println("--------��ѯ-----------------" + sql);
        record = new SPCINVRecord();
        record.setSQL(sql);
        record.retrieve();
        record.setFilter(" SETMAIN_FLG='Y' ");
        record.filter();
        table.setDataStore(record);
        table.setDSValue();
        if (table.getRowCount() > 0) {
            this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
            lockTableRows();
            callFunction("UI|save|setEnabled", false);
            callFunction("UI|commit|setEnabled", true);
            callFunction("UI|deleteRow|setEnabled", false);
            callFunction("UI|delete|setEnabled", true);
            callFunction("UI|operation|setEnabled", false);
            callFunction("UI|BAR_CODE|setEnabled", false);
            callFunction("UI|ORDER_CODE|setEnabled", false);
            callFunction("UI|DEPT|setEnabled", false);
            callFunction("UI|PACK1_CODE|setEnabled", false);
            callFunction("UI|SAVE_BTN|setEnabled", false);
        }
    }

    /**
     * ����
     * */
    public void onSave() {
    	
        table.acceptText();
        String admType = this.getValueString("ADM_TYPE");
        if (admType.equals("")) {
            this.messageBox("����ѡ���ż�ס��");
            return;
        }
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            this.messageBox("�����Ų���Ϊ��");
            return;
        }
        if (this.getValueString("CASE_NO").trim().equals("")) {
            this.messageBox("�������û���Ϣ");
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);
        this.setValue("MR_NO", mrNo);
        if (this.getValueString("OP_ROOM").equals("")) {
            this.messageBox("��ѡ������");
            return;
        }
        if (this.getValueString("OP_DEPT").equals("")) {
            this.messageBox("��ѡ����������");
            return;
        }
        if (this.getValueString("OP_DR").equals("")) {
            this.messageBox("��ѡ������ҽ��");
            return;
        }
        
        
        
        
        // ����ǰ�ٲ�ѯһ��
        TParm param = new TParm();
        param.setData("ADM_TYPE", admType);
        param.setData("MR_NO", mrNo);
        TParm action = TIOM_AppServer.executeAction(ACTION_PATH, "onMrNo", param);
        if (action.getCount("CASE_NO") < 0) {
            this.messageBox("���޴˲���");
            return;
        }
        table.acceptText();
        String buff = record.isFilter() ? record.FILTER : record.PRIMARY;
        int newRow[] = record.getNewRows(buff);
        for (int row : newRow) {
            record.setItem(row, "MR_NO", this.getValue("MR_NO"));
            record.setItem(row, "CASE_NO", this.getValue("CASE_NO"));
//            record.setItem(temp, "DEPT_CODE", this.getValue("DEPT_CODE"));
            record.setItem(row, "DEPT_CODE", Operator.getDept());// �������ң��Ӡ�Ҫ���Ϊ��¼����20140806��
            record.setItem(row, "OP_ROOM", this.getValue("OP_ROOM"));
//            record.setItem(row, "ORDER_DEPT_CODE", this.getValueString("OP_DEPT"));// ��������wanglong add 20140826
            record.setItem(row, "ORDER_DEPT_CODE", this.getValueString("DEPT_CODE"));//��ץȡ��½���Ҹ�Ϊ�������ڿ���--xiongwg20150716
            record.setItem(row, "ORDER_DR_CODE", this.getValueString("OP_DR"));// ����ҽ��wanglong add 20140826
            if (record.getItemString(row, "USED_FLG").equals("Y")) {
                record.setItem(row, "BILL_FLG", "Y");
            }
            //=======pangben 2015-8-25 ��Ӹ���У��
            String orderCode = record.getRowParm(row, buff).getValue("ORDER_CODE");
         // �����˷������ܿ�
			double dosageQty = record.getRowParm(row, buff)
					.getDouble("QTY");
			if (this.getValue("CLNCPATH_CODE").toString().length()>0) {//pangben 2016-10-19�ٴ�·��ʱ��У��
				 String schdCode=record.getRowParm(row, buff).getValue("SCHD_CODE");
				 if (null==schdCode||schdCode.length()<=0) {
					if(this.messageBox("��ʾ","'"+record.getRowParm(row, buff).getValue("ORDER_DESC")+"',ʱ��Ϊ���Ƿ����",2)!=0){
						return;
					}
				}
			}
			if (dosageQty < 0) {
				String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
						+ "   FROM IBS_ORDD "
						+ "  WHERE ORDER_CODE = '"
						+ orderCode
						+ "' "
						+ "    AND CASE_NO = '"
						+ this.getValue("CASE_NO")
						+ "' " + "  GROUP BY ORDER_CODE ";
				// System.out.println("selQtyParmsql" + orderCodeSql);
				TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(
						selQtySql));
				double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
				if (Math.abs(dosageQty) > dosageQtyTot) {
					this.messageBox("�˷����������ϼ�����,���ܱ���");
					return;
				}
			}
//            String checkSql = // wanglong add 20141013
//                    "SELECT (SELECT SUM(A.QTY)         "
//                            + "          FROM SPC_INV_RECORD A, INV_STOCKDD B "
//                            + "         WHERE A.INV_CODE = B.INV_CODE "
//                            + "           AND A.BAR_CODE = B.RFID "
//                            + "           AND A.CASE_NO = '#' AND B.RFID = '@' "
//                            + "        GROUP BY A.CASE_NO) NUM, "
//                            + "       (SELECT SUM(A.QTY)        "
//                            + "          FROM SPC_INV_RECORD A, INV_STOCKDD B "
//                            + "         WHERE A.INV_CODE = B.INV_CODE "
//                            + "           AND A.BAR_CODE = B.RFID "
//                            + "           AND A.CASE_NO <> '#' AND B.RFID = '@' "
//                            + "        GROUP BY A.CASE_NO) OTHER_NUM "
//                            + "  FROM DUAL                           ";
//            checkSql = checkSql.replaceFirst("#", this.getValueString("CASE_NO"));
//            checkSql = checkSql.replaceFirst("@", record.getItemString(row, "BAR_CODE"));
//            TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
//            if (checkParm.getCount() > 0) {
//                if (checkParm.getInt("OTHER_NUM", 0) > 0) {
//                    this.messageBox(record.getItemString(row, "ORDER_DESC") + "��RFID�Ѿ�������ɨ���");
//                    return;
//                }
//                if (record.getItemInt(row, "QTY") + checkParm.getInt("NUM", 0) > 1) {
//                    this.messageBox(record.getItemString(row, "ORDER_DESC") + "��RFID�Ѿ�����ǰ����ɨ���");
//                    return;
//                }
//            }
        }
        TParm parm = record.getBuffer(record.FILTER);
        // String[] sql = record.getUpdateSQL();
        // for (int i = 0; i < sql.length; i++) {
        // System.out.println("----------onSave-----sql["+i+"]-------------"+sql[i]);
        // }
        // if(sql.length<=0){
        // this.messageBox("�ޱ�������");
        // return;
        // }
        // TParm inParm = new TParm();
        // Map inMap = new HashMap();
        // inMap.put("SQL", sql);
        // inParm.setData("IN_MAP", inMap);
        // TParm result = TIOM_AppServer.executeAction(ACTION_PATH, "onSave", inParm);
        // TParm parm = record.getBuffer(buff);
        // for (int i = parm.getCount() - 1; i >= 0; i--) {
        // if (parm.getBoolean("#NEW#", i) != true || parm.getBoolean("#ACTIVE#", i) != true) {
        // parm.removeRow(i);
        // }
        // }
        TParm inParm = new TParm();
//        System.out.println("------����-----parm-----------" + parm);
        inParm.setData("SPC_INV_RECORD", parm.getData());
        TParm result = TIOM_AppServer.executeAction(ACTION_PATH, "onSaveParm", inParm);
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ�� " + result.getErrText());
            return;
        }
        this.messageBox("����ɹ�");
        onClearTable();
        record = new SPCINVRecord();
        record.onQuery();
        table.setDataStore(record);
        table.setDSValue();
        callFunction("UI|BAR_CODE|grabFocus");// ��ý���
    }

    /**
     * �ύ �Ʒ�
     * */
    public void onCommit() {
        String admType = this.getValueString("ADM_TYPE");
        if (admType.equals("")) {
            this.messageBox("����ѡ���ż�ס��");
            return;
        }
        int rowCount = table.getRowCount();
        if (rowCount < 1) {
            this.messageBox("������");
            return;
        }
        
        
        // by yanmm 2017/3/1 ���Ӷ�ִ�п���У������
        // �жϲ�����ҽ��EXE_DEPT_CODE�Ƿ���ֵ�� ���û��ֵ�жϵ�ǰ��¼���ң�Operator.getDeptCode���Ƿ���ֵ��
        // ��ǰ��¼���������ֵ�����¸�����ҽ��EXE_DEPT_CODE��ֵ
       // �����ǰ��¼����û��ֵ  ��ʾ��Ϣ����ǰ������Աû�е�¼���ң������Բ���
        for (int i = 0; i < record.rowCount(); i++) {
        	if(null== record.getItemString(i, "EXE_DEPT_CODE") || record.getItemString(i, "EXE_DEPT_CODE").equals("")){
        		if(null!=Operator.getDept()&& Operator.getDept().length()>0){
        			record.setItem(i, "EXE_DEPT_CODE", Operator.getDept());
        		}else{
        			 this.messageBox("��ǰ������Աû�е�¼���ң������Բ���");
        	         return;
        		}
        	}
        }
      
        
        //fux modify 20150702
        // �ύǰ�ٲ�ѯһ���Ƿ���Ժ  Ӧ����caseNo����
        TParm action = new TParm();
        String caseNo = this.getValueString("CASE_NO");
        // messageBox("caseNo:"+caseNo);
        // סԺ����
        if (admType.equals("I")) {                 
            String sql =
                    "SELECT A.MR_NO, A.IPD_NO, A.PAT_NAME, A.BIRTH_DATE, A.SEX_CODE, B.CASE_NO,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE AS DR_CODE"
                            + " FROM ADM_INP B, SYS_PATINFO A "
                            + " WHERE B.CASE_NO = '"  
                            + caseNo                                   
                            + "' "                                      
                            + "  AND A.MR_NO = B.MR_NO "    
                            + " AND B.DS_DATE IS NULL "
                            + " AND B.IN_DATE IS NOT NULL " + " AND B.CANCEL_FLG <> 'Y'";
             //System.out.println("sql=="+sql);
             action = new TParm(TJDODBTool.getInstance().select(sql));
        } 
        if (action.getCount("CASE_NO") < 0) {            
            this.messageBox("���޴˲���,�����µ�¼��ѯ");    
            return;  
        }
        //==pangben 2016-9-13
        try {
        	IBSTool.getInstance().onCheckClpDiff(caseNo);
		} catch (Exception e) {
			// TODO: handle exception
		}
        // TParm tableParm = table.getParmValue();
        HashMap<String, Double> invMap = new HashMap<String, Double>();
        TParm invParm = new TParm();// �ۿ����
        TParm ibsOrddParm = new TParm();
        TParm parmFlg = new TParm();
        HashMap maps = new HashMap();
        HashMap mapPack = new HashMap();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        record.setFilter("");
        record.filter();
        String orderCode="";
        double qty=0.00;
        for (int i = 0; i < record.rowCount(); i++) {
        	orderCode=record.getItemString(i, "ORDER_CODE");
        	qty=0.00;
			for (int j = 0; j <record.rowCount(); j++) {
				if (orderCode.equals(record.getItemString(j, "ORDER_CODE"))) {
					qty+=record.getItemDouble(j, "QTY");
				}
			}
			if (qty<0) {
				String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
					+ "   FROM IBS_ORDD "
					+ "  WHERE ORDER_CODE = '"
					+ orderCode
					+ "' "
					+ "    AND CASE_NO = '"
					+ this.getValue("CASE_NO")
					+ "' " + "  GROUP BY ORDER_CODE ";
			// System.out.println("selQtyParmsql" + orderCodeSql);
				TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(
					selQtySql));
				double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
				if (Math.abs(qty) > dosageQtyTot) {
					this.messageBox(record.getItemString(i, "ORDER_DESC")+"�˷����������ϼ�����,���ܱ���");
					return;
				}
			}
		}
        for (int i = 0; i < record.rowCount(); i++) {
            if (!record.isActive(i)) {
                continue;
            }
            if (record.getItemDouble(i, "QTY") == 0) {
                if (!record.getItemString(i, "INV_DESC").equals("")) {
                    this.messageBox(record.getItemString(i, "INV_DESC") + " ����Ϊ0�����ܱ��ύ");
                } else {
                    this.messageBox(record.getItemString(i, "ORDER_DESC") + " ����Ϊ0�����ܱ��ύ");
                }
                continue;
            }
            // 2.��ѯ�Ʒѱ�ǺͼƷѻ�д���ֶ�
            // TParm row = tableParm.getRow(i);
            String businessNo = record.getItemString(i, "BUSINESS_NO");
            String seq = record.getItemString(i, "SEQ");
            String billFlg = record.getItemString(i, "BILL_FLG");
            String caseNoSeq = record.getItemString(i, "CASE_NO_SEQ");
            String seqNo = record.getItemString(i, "SEQ_NO");
            String stockFlg = record.getItemString(i, "STOCK_FLG");// �ۿ���
            String commitFlg = record.getItemString(i, "COMMIT_FLG");
            String usedFlg = record.getItemString(i, "USED_FLG");
            String packBarCodes = record.getItemString(i, "PACK_BARCODE");
            // δ�ύ������
            parmFlg.addData("BUSINESS_NO", businessNo);
            parmFlg.addData("SEQ", seq);
            if (billFlg.equals("Y") && caseNoSeq.equals("") && seqNo.equals("")
                    && !commitFlg.equals("Y")) {// Ҫ�շѣ�bill_flg='Y'��������δ�շѣ�caseNoSeq='',seqNo=''��
            // String sql =
            // "SELECT * FROM SPC_INV_RECORD WHERE BUSINESS_NO = '" + businessNo
            // + "' AND SEQ = '" + seq + "'";
            // TParm parmV = new TParm(TJDODBTool.getInstance().select(sql));
                if (packBarCodes != null && !packBarCodes.equals("")) {
                    // ������
                    if (packBarCodes.length() == 12) {
                        if (billFlg.equals("Y")) {
                            ibsOrddParm.addData("BUSINESS_NO", businessNo);
                            ibsOrddParm.addData("SEQ", seq);
                            ibsOrddParm.addData("CASE_NO", this.getValueString("CASE_NO"));
                            ibsOrddParm.addData("ORDER_NO", "");// ����Ʒ�
                            String Sql =
                                    "SELECT ORDER_CODE, SEQ, ORDER_CAT1_CODE, CAT1_TYPE, INDV_FLG,"
                                            + " NHI_PRICE,UNIT_CODE " + " FROM SYS_FEE "
                                            + " WHERE ORDER_CODE = '"
                                            + record.getItemString(i, "ORDER_CODE") + "'";
                            TParm selParm = new TParm(TJDODBTool.getInstance().select(Sql));
                            ibsOrddParm
                                    .addData("ORDER_CODE", record.getItemString(i, "ORDER_CODE"));
                            ibsOrddParm.addData("ORDER_SEQ", record.getItemString(i, "SEQ"));
                            ibsOrddParm.addData("ORDER_CAT1_CODE",
                                                selParm.getValue("ORDER_CAT1_CODE", 0));
                            ibsOrddParm.addData("CAT1_TYPE", selParm.getValue("CAT1_TYPE", 0));
                            ibsOrddParm.addData("NHI_PRICE", selParm.getValue("NHI_PRICE", 0)); // ҽ������
                            ibsOrddParm.addData("ORDERSET_CODE", record.getItemString(i, "ORDERSET_CODE"));//wanglong add 20140721
                            ibsOrddParm.addData("ORDERSET_GROUP_NO", record.getItemString(i, "ORDERSET_GROUP_NO"));
                            if (record.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                                ibsOrddParm.addData("HIDE_FLG", "N");
                            } else {
                                ibsOrddParm.addData("HIDE_FLG", "Y");
                            }
                            ibsOrddParm.addData("IPD_NO", this.getValueString("IPD_NO"));
                            ibsOrddParm.addData("MR_NO", record.getItemString(i, "MR_NO"));
                            // �������ҺͿ�������
                            ibsOrddParm.addData("DEPT_CODE", record.getItemString(i, "DEPT_CODE"));
                            ibsOrddParm
                                    .addData("STATION_CODE", this.getValueString("STATION_CODE"));
                            ibsOrddParm.addData("EXE_DEPT_CODE",
                                                record.getItemString(i, "EXE_DEPT_CODE"));
                            ibsOrddParm.addData("MEDI_QTY", record.getItemDouble(i, "QTY")); // ��ҩ����
                            ibsOrddParm.addData("MEDI_UNIT", selParm.getValue("UNIT_CODE", 0)); // ��ҩ��λ
                            ibsOrddParm.addData("DOSE_CODE", ""); // ����
                            ibsOrddParm.addData("FREQ_CODE", "STAT"); // Ƶ�δ���
                            ibsOrddParm.addData("TAKE_DAYS", "1"); // ��ҩ����
                            ibsOrddParm.addData("DOSAGE_QTY", record.getItemDouble(i, "QTY")); // ��ҩ����
                            ibsOrddParm.addData("DOSAGE_UNIT", selParm.getValue("UNIT_CODE", 0)); // ��ҩ��λ
                            ibsOrddParm.addData("OWN_PRICE", record.getItemDouble(i, "OWN_PRICE")); // �Է�
                            ibsOrddParm
                                    .addData("OWN_AMT",
                                             StringTool.round(record.getItemDouble(i, "OWN_PRICE")
                                                     * record.getItemDouble(i, "QTY"), 2)); // �Է��ܼ�
                            ibsOrddParm.addData("OPT_USER", Operator.getID());
                            ibsOrddParm.addData("OPT_DATE", now);
                            ibsOrddParm.addData("OPT_TERM", Operator.getIP());
                            ibsOrddParm.addData("COST_AMT", "");
                            if (Operator.getCostCenter() != null
                                    && !Operator.getCostCenter().equals("")) {
                                ibsOrddParm.addData("COST_CENTER_CODE", Operator.getCostCenter());
                            } else {
                                ibsOrddParm.addData("COST_CENTER_CODE",
                                                    this.getValueString("DEPT_CODE"));
                            }
                            ibsOrddParm.addData("DS_FLG", "N");
                            ibsOrddParm.addData("ORDER_DR_CODE",
                                                record.getItemString(i, "ORDER_DR_CODE")); // ҽ������
                            ibsOrddParm.addData("ORDER_DEPT_CODE",
                                                record.getItemString(i, "ORDER_DEPT_CODE")); // ����
                            ibsOrddParm.addData("DISPENSE_EFF_DATE",
                                                record.getItemTimestamp(i, "ORDER_DATE"));
                            ibsOrddParm.addData("DISPENSE_END_DATE",
                                                record.getItemTimestamp(i, "ORDER_DATE"));
                            ibsOrddParm.addData("BAR_CODE", record.getItemString(i, "BAR_CODE"));
                            if (record.getItemDouble(i, "QTY") > 0) {
                                ibsOrddParm.addData("FLG", "ADD");
                            } else {
                                ibsOrddParm.addData("FLG", "SUB");
                            }
                            ibsOrddParm.addData("SCHD_CODE", record.getItemString(i, "SCHD_CODE"));//==pangben 2015-10-16
                        }
                        // ����� ��ͨҩƷ��
                    } else if (packBarCodes.length() == 8) {
                        ibsOrddParm.addData("BUSINESS_NO", businessNo);
                        ibsOrddParm.addData("SEQ", seq);    
                        ibsOrddParm.addData("CASE_NO", this.getValueString("CASE_NO"));
                        ibsOrddParm.addData("ORDER_NO", "");// ����Ʒ�
                        TParm selParm = new TParm();
                        if (record.getItemString(i, "CLASS_CODE").equals("4")) {
                            String sqls =
                                    "SELECT HIS_ORDER_CODE FROM SYS_FEE_SPC "   
                                            + " WHERE REGION_CODE='" + Operator.getRegion() + "' "
                                            + "  AND ORDER_CODE='"
                                            + record.getItemString(i, "ORDER_CODE") + "' "
                                            + "  AND ACTIVE_FLG='Y'";
                            TParm hisOrderParm = new TParm(TJDODBTool.getInstance().select(sqls));
                            if (hisOrderParm.getCount() > 0) {                                     
                                String order_code = hisOrderParm.getValue("HIS_ORDER_CODE", 0);
                                String Sql =                   
                                        "SELECT ORDER_CODE, SEQ, ORDER_CAT1_CODE, CAT1_TYPE, INDV_FLG," 
                                                + " NHI_PRICE,UNIT_CODE " + " FROM SYS_FEE "
                                                + " WHERE ORDER_CODE = '"
                                                + record.getItemString(i, "ORDER_CODE") + "'";
                                selParm = new TParm(TJDODBTool.getInstance().select(Sql));
                                ibsOrddParm.addData("ORDER_CODE", order_code);
                            }   
                        } else {                                              
                            String Sql =
                                    "SELECT ORDER_CODE, SEQ, ORDER_CAT1_CODE, CAT1_TYPE, INDV_FLG,"
                                            + " NHI_PRICE,UNIT_CODE " + " FROM SYS_FEE "
                                            + " WHERE ORDER_CODE = '"
                                            + record.getItemString(i, "ORDER_CODE") + "'";
                            selParm = new TParm(TJDODBTool.getInstance().select(Sql));
                            ibsOrddParm.addData("ORDER_CODE", selParm.getValue("ORDER_CODE", 0));
                        }
                        ibsOrddParm.addData("ORDER_SEQ", record.getItemString(i, "SEQ"));
                        ibsOrddParm.addData("ORDER_CAT1_CODE",
                                            selParm.getValue("ORDER_CAT1_CODE", 0));
                        ibsOrddParm.addData("CAT1_TYPE", selParm.getValue("CAT1_TYPE", 0));
                        ibsOrddParm.addData("NHI_PRICE", selParm.getValue("NHI_PRICE", 0)); // ҽ������
                        ibsOrddParm.addData("ORDERSET_CODE", record.getItemString(i, "ORDERSET_CODE"));//wanglong add 20140721
                        ibsOrddParm.addData("ORDERSET_GROUP_NO", record.getItemString(i, "ORDERSET_GROUP_NO"));
                        if (record.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                            ibsOrddParm.addData("HIDE_FLG", "N");
                        } else {
                            ibsOrddParm.addData("HIDE_FLG", "Y");
                        }
                        ibsOrddParm.addData("IPD_NO", this.getValueString("IPD_NO"));
                        ibsOrddParm.addData("MR_NO", this.getValueString("MR_NO"));
                        // �������ҺͿ�������
                        ibsOrddParm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
                        ibsOrddParm.addData("STATION_CODE", this.getValueString("STATION_CODE"));
                        ibsOrddParm.addData("EXE_DEPT_CODE",
                                            record.getItemString(i, "EXE_DEPT_CODE"));
                        ibsOrddParm.addData("MEDI_QTY", record.getItemDouble(i, "QTY")); // ��ҩ����
                        ibsOrddParm.addData("MEDI_UNIT", selParm.getValue("UNIT_CODE", 0)); // ��ҩ��λ
                        ibsOrddParm.addData("DOSE_CODE", ""); // ����
                        ibsOrddParm.addData("FREQ_CODE", "STAT"); // Ƶ�δ���
                        ibsOrddParm.addData("TAKE_DAYS", "1"); // ��ҩ����
                        ibsOrddParm.addData("DOSAGE_QTY", record.getItemDouble(i, "QTY")); // ��ҩ����
                        ibsOrddParm.addData("DOSAGE_UNIT", selParm.getValue("UNIT_CODE", 0)); // ��ҩ��λ
                        ibsOrddParm.addData("OWN_PRICE", record.getItemDouble(i, "OWN_PRICE")); // �Է�
                        ibsOrddParm.addData("OWN_AMT",
                                            StringTool.round(record.getItemDouble(i, "OWN_PRICE")
                                                    * record.getItemDouble(i, "QTY"), 2));
                        ibsOrddParm.addData("OPT_USER", Operator.getID());
                        ibsOrddParm.addData("OPT_DATE", now);
                        ibsOrddParm.addData("OPT_TERM", Operator.getIP());
                        ibsOrddParm.addData("COST_AMT", "");
                        if (Operator.getCostCenter() != null
                                && !Operator.getCostCenter().equals("")) {
                            ibsOrddParm.addData("COST_CENTER_CODE", Operator.getCostCenter());
                        } else {
                            ibsOrddParm.addData("COST_CENTER_CODE",
                                                this.getValueString("DEPT_CODE"));
                        }
                        ibsOrddParm.addData("DS_FLG", "N");
                        ibsOrddParm.addData("ORDER_DR_CODE",
                                            record.getItemString(i, "ORDER_DR_CODE")); // ҽ������
                        ibsOrddParm.addData("ORDER_DEPT_CODE",
                                            record.getItemString(i, "ORDER_DEPT_CODE")); // ����
                        ibsOrddParm.addData("DISPENSE_EFF_DATE",
                                            record.getItemTimestamp(i, "ORDER_DATE"));
                        ibsOrddParm.addData("DISPENSE_END_DATE",
                                            record.getItemTimestamp(i, "ORDER_DATE"));
                        ibsOrddParm.addData("BAR_CODE", record.getItemString(i, "BAR_CODE"));
                        if (record.getItemDouble(i, "QTY") > 0) {
                            ibsOrddParm.addData("FLG", "ADD");
                        } else {
                            ibsOrddParm.addData("FLG", "SUB");
                        }
                        ibsOrddParm.addData("SCHD_CODE", record.getItemString(i, "SCHD_CODE"));//==pangben 2015-10-16
                    }
                }
                // ��������
                else {
                    // System.out.println("��������������������");
                    ibsOrddParm.addData("BUSINESS_NO", businessNo);
                    ibsOrddParm.addData("SEQ", seq);
                    ibsOrddParm.addData("CASE_NO", this.getValueString("CASE_NO"));
                    ibsOrddParm.addData("ORDER_NO", "");// ����Ʒ�
                    // System.out.println("���--->"+parmV.getData("CLASS_CODE",0));
                    TParm selParm = new TParm();
                    if (record.getItemString(i, "CLASS_CODE").equals("4")
                            || record.getItemString(i, "CLASS_CODE").equals("1")) {
                        String Sql =
                                "SELECT ORDER_CODE, SEQ, ORDER_CAT1_CODE, CAT1_TYPE, INDV_FLG,"
                                        + " NHI_PRICE,UNIT_CODE " + " FROM SYS_FEE "
                                        + " WHERE ORDER_CODE = '"
                                        + record.getItemString(i, "ORDER_CODE") + "'";
                        selParm = new TParm(TJDODBTool.getInstance().select(Sql));
                        ibsOrddParm.addData("ORDER_CODE", record.getItemString(i, "ORDER_CODE"));
                    } else {
                        String Sql =
                                "SELECT ORDER_CODE, SEQ, ORDER_CAT1_CODE, CAT1_TYPE, INDV_FLG,"
                                        + " NHI_PRICE,UNIT_CODE " + " FROM SYS_FEE "
                                        + " WHERE ORDER_CODE = '"
                                        + record.getItemString(i, "ORDER_CODE") + "'";
                        selParm = new TParm(TJDODBTool.getInstance().select(Sql));
                        ibsOrddParm.addData("ORDER_CODE", selParm.getValue("ORDER_CODE", 0));
                    }
                    ibsOrddParm.addData("ORDER_SEQ", record.getItemString(i, "SEQ"));
                    ibsOrddParm.addData("ORDER_CAT1_CODE", selParm.getValue("ORDER_CAT1_CODE", 0));
                    ibsOrddParm.addData("CAT1_TYPE", selParm.getValue("CAT1_TYPE", 0));
                    ibsOrddParm.addData("NHI_PRICE", selParm.getValue("NHI_PRICE", 0)); // ҽ������
                    ibsOrddParm.addData("ORDERSET_CODE", record.getItemString(i, "ORDERSET_CODE"));//wanglong add 20140721
                    ibsOrddParm.addData("ORDERSET_GROUP_NO", record.getItemString(i, "ORDERSET_GROUP_NO"));
                    if (record.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                        ibsOrddParm.addData("HIDE_FLG", "N");
                    } else {
                        ibsOrddParm.addData("HIDE_FLG", "Y");
                    }
                    ibsOrddParm.addData("IPD_NO", this.getValueString("IPD_NO"));
                    ibsOrddParm.addData("MR_NO", this.getValueString("MR_NO"));
                    // �������ҺͿ�������
                    ibsOrddParm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
                    ibsOrddParm.addData("STATION_CODE", this.getValueString("STATION_CODE"));
                    ibsOrddParm.addData("EXE_DEPT_CODE", record.getItemString(i, "EXE_DEPT_CODE"));
                    ibsOrddParm.addData("MEDI_QTY", record.getItemDouble(i, "QTY")); // ��ҩ����
                    ibsOrddParm.addData("MEDI_UNIT", selParm.getValue("UNIT_CODE", 0)); // ��ҩ��λ
                    ibsOrddParm.addData("DOSE_CODE", ""); // ����
                    ibsOrddParm.addData("FREQ_CODE", "STAT"); // Ƶ�δ���
                    ibsOrddParm.addData("TAKE_DAYS", "1"); // ��ҩ����
                    ibsOrddParm.addData("DOSAGE_QTY", record.getItemDouble(i, "QTY")); // ��ҩ����
                    ibsOrddParm.addData("DOSAGE_UNIT", selParm.getValue("UNIT_CODE", 0)); // ��ҩ��λ
                    ibsOrddParm.addData("OWN_PRICE", record.getItemDouble(i, "OWN_PRICE")); // �Է�
                    ibsOrddParm.addData("OWN_AMT",
                                        StringTool.round(record.getItemDouble(i, "OWN_PRICE")
                                                * record.getItemDouble(i, "QTY"), 2)); // �Է��ܼ�
                    // own_price*DOSAGE_QTY
                    ibsOrddParm.addData("OPT_USER", Operator.getID());
                    ibsOrddParm.addData("OPT_DATE", now);
                    ibsOrddParm.addData("OPT_TERM", Operator.getIP());
                    ibsOrddParm.addData("COST_AMT", "");
                    if (Operator.getCostCenter() != null && !Operator.getCostCenter().equals("")) {
                        ibsOrddParm.addData("COST_CENTER_CODE", Operator.getCostCenter());
                    } else {
                        ibsOrddParm.addData("COST_CENTER_CODE", this.getValueString("DEPT_CODE"));
                    }
                    ibsOrddParm.addData("DS_FLG", "N");
                    ibsOrddParm.addData("ORDER_DR_CODE",
                                        record.getItemString(i, "ORDER_DR_CODE")); // ҽ������
                    ibsOrddParm.addData("ORDER_DEPT_CODE",
                                        record.getItemString(i, "ORDER_DEPT_CODE")); // ����
                    ibsOrddParm.addData("DISPENSE_EFF_DATE",
                                        record.getItemTimestamp(i, "ORDER_DATE"));
                    ibsOrddParm.addData("DISPENSE_END_DATE",
                                        record.getItemTimestamp(i, "ORDER_DATE"));
                    ibsOrddParm.addData("BAR_CODE", record.getItemString(i, "BAR_CODE"));
                    ibsOrddParm.addData("SCHD_CODE", record.getItemString(i, "SCHD_CODE"));//==pangben 2015-10-16
                    if (record.getItemDouble(i, "QTY") > 0) {
                        ibsOrddParm.addData("FLG", "ADD");
                    } else {
                        ibsOrddParm.addData("FLG", "SUB");
                    }
                }
            }
            // ��������ʣ����пۿ�
            // ��ֵ
            if (!stockFlg.equals("Y")) {// �ۿ��ǣ��������Y
                String sql =
                        "SELECT * FROM SPC_INV_RECORD WHERE BUSINESS_NO = '" + businessNo
                                + "' AND SEQ = '" + seq + "'";
                // TParm parmV = new TParm(TJDODBTool.getInstance().select(sql));
                // ����
                if (!record.getItemString(i, "INV_CODE").equals("")) {
                    String sqlInv =
                            "SELECT SEQMAN_FLG,EXPENSIVE_FLG FROM INV_BASE WHERE INV_CODE = '"
                                    + record.getItemString(i, "INV_CODE") + "'";
                    TParm parmInv = new TParm(TJDODBTool.getInstance().select(sqlInv));
                    if (parmInv.getCount() < 0) {
                        return;
                    }
                    String seqMainFlg = parmInv.getValue("SEQMAN_FLG", 0);// ��Ź����ǣ�Y�Ļ���һ����Ʒһ�����
                    String expensiveFlg = parmInv.getValue("EXPENSIVE_FLG", 0);// ������Ʒ���
                    // SEQMAN_FLG��EXPENSIVE_FLG��ΪY����Ϊ��ֵ
                    String class_code = record.getItemString(i, "CLASS_CODE");
                    // class_code = 3 ��ʾ��ֵ
                    if (seqMainFlg.equals("Y") && expensiveFlg.equals("Y")
                            && class_code.equals("3")) {//���ֵ
                        if (invMap.containsKey(record.getItemString(i, "INV_CODE"))) {
                            Double double1 = invMap.get(record.getItemString(i, "INV_CODE"));
                            invMap.put(record.getItemString(i, "INV_CODE"),
                                       double1 + record.getItemDouble(i, "QTY"));
                            Double double2 = invMap.get(record.getItemString(i, "INV_CODE"));
                        } else {
                            invMap.put(record.getItemString(i, "INV_CODE"),
                                       record.getItemDouble(i, "QTY"));
                        }
                        String sqlOrgCode =
                                "SELECT ORG_CODE FROM INV_STOCKDD WHERE  RFID = '"
                                        + record.getItemString(i, "BAR_CODE") + "'";
                        TParm parmOrgParm = new TParm(TJDODBTool.getInstance().select(sqlOrgCode));
                        String org_code = parmOrgParm.getValue("ORG_CODE", 0);
                        invParm.addData("BUSINESS_NO", record.getItemString(i, "BUSINESS_NO"));
                        invParm.addData("SEQ", record.getItemData(i, "SEQ"));
                        invParm.addData("INV_CODE", record.getItemString(i, "INV_CODE"));
                        invParm.addData("QTY", record.getItemDouble(i, "QTY"));
                        invParm.addData("ORG_CODE", org_code);
                        invParm.addData("RFID", record.getItemString(i, "BAR_CODE"));
                        invParm.addData("OPT_USER", Operator.getID());
                        invParm.addData("OPT_TERM", Operator.getIP());
                        invParm.addData("OUT_USER", Operator.getID());
                        invParm.addData("MR_NO", this.getValue("MR_NO"));
                        invParm.addData("CASE_NO", this.getValueString("CASE_NO"));
                        invParm.addData("RX_SEQ", "");
                        invParm.addData("ADM_TYPE", admType);
                        invParm.addData("SEQ_NO", "");
                        invParm.addData("WAST_ORG", Operator.getDept());
                        invParm.addData("FLG", "HIGH");// �Ƿ��Ǹ�ֵ���
                        // ������� ���ư�(��ֵ)�۰��Ŀ⣬��ͨ��ֵ�����������ۿ�
                    } else {
                        if (!record.getItemString(i, "PACK_BARCODE").equals("")
                                && record.getItemString(i, "PACK_BARCODE").length() == 12
                                && record.getItemString(i, "PACK_BARCODE").substring(6, 12)
                                        .equals("000000")) {// �����ư�
                            // �ۿ�������
                            // INV_STOCKM
                            String sqlPackSql =
                                    "SELECT PACK_BARCODE,PACK_GROUP_NO FROM SPC_INV_RECORD WHERE  BUSINESS_NO = '"
                                            + businessNo + "' AND SEQ = '" + seq + "'";
                            TParm parmPack = new TParm(TJDODBTool.getInstance().select(sqlPackSql));
                            if (parmPack.getData("PACK_BARCODE", 0) != null
                                    && !parmPack.getData("PACK_BARCODE", 0).equals("")) {
                                String packBarCode = parmPack.getValue("PACK_BARCODE", 0);
                                int packGroupNo = parmPack.getInt("PACK_GROUP_NO");
                                if (maps.containsValue(packBarCode + "=" + packGroupNo)) {
                                    continue;
                                } else {
                                    maps.put(i, packBarCode + "=" + packGroupNo);
                                    mapPack.put(i, packBarCode.substring(0, 6));
                                }
                            }
                        }
                    }
                }
            }
        }  
        if (ibsOrddParm.getCount("CASE_NO") < 1) {
            this.messageBox("���ύ����");
            return;
        }
        ibsOrddParm.setData("DATA_TYPE", 5);// M��
        ibsOrddParm.setData("ADM_TYPE", admType);// �ż�ס��
        ibsOrddParm.setData("REGION_CODE", Operator.getRegion());
        ibsOrddParm.setData("BILL_DATE", now);//wanglong add 20141014 ��ǰȷ��BILL_DATE�����ڽ��HIS����������ʱ���ظ��ύ����
        invParm.setData("MERGE", invMap);
        HashMap<String, Double> map = (HashMap<String, Double>) invParm.getData("MERGE");
        TParm parm = new TParm();
        TParm parmPack = new TParm();
        for (int i = 0; i < maps.size(); i++) {
            parmPack.addData("PACK_CODE", mapPack.get(i));
        }
        parm.setData("ibsOrddParm", ibsOrddParm.getData());
        parm.setData("invParm", invParm.getData());
        parm.setData("parmFlg", parmFlg.getData());
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("parmPack", parmPack.getData());
        //System.out.println("------------onCommit-----------" + parm);
        TParm result = TIOM_AppServer.executeAction(ACTION_PATH, "onSaveFee", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("�ύʧ�� " + result.getErrText());
            return;
        }
        this.messageBox("�ύ�ɹ�");
        // ˢ�½���
        this.onQuery("");
    }

    /**
     * ɾ��ҽ��
     */
    public void onDeleteRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String filter = record.getFilter();
        String caseNoSeq = record.getItemString(row, "CASE_NO_SEQ");
        String seqNo = record.getItemString(row, "SEQ_NO");
        String stockFlg = record.getItemString(row, "STOCK_FLG");
        String orderSetCode = record.getItemString(row, "ORDERSET_CODE");
        int ordersetGroupNo = record.getItemInt(row, "ORDERSET_GROUP_NO");
        if (!caseNoSeq.equals("") || !seqNo.equals("") || stockFlg.equals("Y")) {
            this.messageBox("��ҽ���Ѿ��ύ,����ɾ��");
            return;
        }
        if (record.getItemString(row, "PACK_BARCODE").length() == 12) { // ==========������
            if (this.messageBox("������ɾ��ȷ��", "������ֻ������ɾ����ȷ��Ҫɾ����", OK_CANCEL_OPTION) == 0) {// ȷ��ɾ��
                int packGroupNo = record.getItemInt(row, "PACK_GROUP_NO");
                String packBarCode = record.getItemString(row, "PACK_BARCODE");
                int count = record.rowCount();
                for (int i = count - 1; i >= 0; i--) {
                    if (record.getItemInt(i, "PACK_GROUP_NO") == packGroupNo
                            && record.getItemString(i, "PACK_BARCODE").equals(packBarCode)) {
                        record.deleteRow(i);
                    }
                }
            }
        } else if (ordersetGroupNo > 0) { // ==========����ҽ��
            record.setFilter("");
            record.filter();
            int count = record.rowCount();
            for (int i = count - 1; i >= 0; i--) {
                if (record.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
                        && record.getItemInt(i, "ORDERSET_GROUP_NO") == ordersetGroupNo) {
                    record.deleteRow(i);
                }
            }
        } else { // ==========����ҽ��
            record.deleteRow(row);
        }
        record.setFilter(filter);
        record.filter();
        table.setDSValue();
        if (table.getRowCount() > 0) {
            this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
        }
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String caseNoSeq = record.getItemString(row, "CASE_NO_SEQ");
        String seqNo = record.getItemString(row, "SEQ_NO");
        String stockFlg = record.getItemString(row, "STOCK_FLG");
        String businessNo = record.getItemString(row, "BUSINESS_NO");
        int ordersetGroupNo = record.getItemInt(row, "ORDERSET_GROUP_NO");
        String orderSetCode = record.getItemString(row, "ORDERSET_CODE");
        String seq = record.getItemString(row, "SEQ");
        if (!caseNoSeq.equals("") || !seqNo.equals("") || stockFlg.equals("Y")) {
            this.messageBox("��ҽ���Ѿ��ύ,����ɾ��");
            return;
        }
        String checkSql =
                "SELECT * FROM SPC_INV_RECORD WHERE BUSINESS_NO = '#'  AND SEQ = '#' AND CASE_NO_SEQ IS NOT NULL ";
        checkSql = checkSql.replaceFirst("#", businessNo);
        checkSql = checkSql.replaceFirst("#", seq);
        TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
        if (checkParm.getErrCode() < 0) {
            this.messageBox("��ѯ�ύ״̬ʧ��");
            return;
        }
        if (checkParm.getCount() > 0) {
            this.messageBox("��ҽ���Ѿ��ύ,����ɾ��");
            return;
        }
        if (record.getItemString(row, "PACK_BARCODE").length() == 12) { // ==========������
            if (this.messageBox("������ɾ��ȷ��", "������ֻ������ɾ����ȷ��Ҫɾ����", OK_CANCEL_OPTION) == 0) {// ȷ��ɾ��
                int packGroupNo = record.getItemInt(row, "PACK_GROUP_NO");
                String packBarCode = record.getItemString(row, "PACK_BARCODE");
                int count = record.rowCount();
                for (int i = count - 1; i >= 0; i--) {
                    if (record.getItemInt(i, "PACK_GROUP_NO") == packGroupNo
                            && record.getItemString(i, "PACK_BARCODE").equals(packBarCode)) {
                        record.deleteRow(i);
                    }
                }
            }
        } else if (ordersetGroupNo > 0) { // ==========����ҽ��
            record.setFilter("");
            record.filter();
            int count = record.rowCount();
            for (int i = count - 1; i >= 0; i--) {
                if (record.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
                        && record.getItemInt(i, "ORDERSET_GROUP_NO") == ordersetGroupNo) {
                    record.deleteRow(i);
                }
            }
        } else { // ==========����ҽ��
            record.deleteRow(row);
        }
        boolean result = record.update();
        if (!result) {
            this.messageBox("ɾ��ʧ��");
        } else {
            this.messageBox("ɾ���ɹ�");
        }
        onQuery(this.type);
    }

    /**
     * ��ձ��
     * */
    public void onClearTable() {
        record = new SPCINVRecord();
        record.onQuery();
        table.setDataStore(record);
        table.setDSValue();
        table.setLockRows(null); // �������
        this.clearValue("BAR_CODE;ORDER_CODE;PACK1_CODE;BAR_QTY");
        this.setValue("AR_AMT", 0);
        this.setValue("billFlg", "N");
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|commit|setEnabled", false);
        callFunction("UI|deleteRow|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|operation|setEnabled", true);
        callFunction("UI|BAR_CODE|setEnabled", true);
        callFunction("UI|BAR_CODE|grabFocus");// ��ý���
        callFunction("UI|ORDER_CODE|setEnabled", true);
        callFunction("UI|DEPT|setEnabled", true);
        callFunction("UI|PACK1_CODE|setEnabled", true);
        callFunction("UI|SAVE_BTN|setEnabled", true);
        
    }

    /**
     * ���
     * */
    public void onClear() {
        this.clearValue("MR_NO;IPD_NO;PAT_NAME;AGE;SEX_CODE");
        this.clearValue("CASE_NO");// ������
        this.clearValue("ADM_TYPE;DEPT_CODE;STATION_CODE;DR_CODE;OP_ROOM;CLNCPATH_CODE;SCHD_CODE");
        this.clearValue("BAR_CODE;BAR_DESC;BAR_QTY;AR_AMT");
        this.clearValue("ORDER_CODE;ORDER_DESC;billFlg");
        this.clearValue("DEPT;PACK1_CODE;PACK1_DESC;OP_DEPT;OP_DR");
        initUIState();
    }

    /**
     * ��ס����������
     */
    public void lockTableRows() {
        table.acceptText();
        int count = table.getRowCount();
        String rows = "";
        for (int i = 0; i < count; i++) {
            rows = rows + String.valueOf(i) + ",";
        }
        rows = rows.substring(0, rows.length());
        table.setLockRows(rows);
    }

    /**
     * ��Ԫ��ֵ�ı��¼�
     */
    public boolean onTableChangeValue(TTableNode tNode) {
        table.acceptText();
        int row = tNode.getRow();
        int col = tNode.getColumn();
        String colName = table.getParmMap(col);
        if (tNode.getValue().equals(tNode.getOldValue())) {// ֵû�仯
            return true;
        }
        if ("QTY".equals(colName)) {// �ı�"����"��ʱ��
        // if (!tNode.getValue().toString().matches("\\d+")) {
        // return true;
        // }
            double oldQty = TypeTool.getDouble(tNode.getOldValue());
            double newQty = TypeTool.getDouble(tNode.getValue());
            table.setItem(row, "AR_AMT_SET",
                          StringTool.round(table.getItemDouble(row, "OWN_PRICE_SET") * newQty, 2));//
            TParm parm = table.getDataStore().getRowParm(tNode.getRow());// ����
            if (!parm.getValue("PACK_BARCODE").equals("") && parm.getInt("PACK_GROUP_NO") > 0) {
                this.messageBox("���������ܸ�������");
                return true;
            }
            if (!parm.getValue("PACK_BARCODE").equals("") && parm.getInt("PACK_GROUP_NO") > 0) {
                this.messageBox("���������ܸ�������");
                return true;
            }
            if (parm.getValue("CLASS_CODE").equals("3") && (newQty > 1 || newQty < -1)) {
                this.messageBox("��ֵ��Ʒ���������ܳ���1��");
                return true;
            }
            // String cat1SQL="SELECT * FROM SYS_FEE WHERE ORDER_CODE='#'".replaceFirst("#",
            // parm.getValue("ORDER_CODE"));
            // TParm sysFee = new TParm(TJDODBTool.getInstance().select(cat1SQL));
            // if(sysFee.getErrCode()<0){
            // this.messageBox(sysFee.getErrText());
            // return true;
            // }
            // System.out.println("-------------��������-------parm-----------" + parm);
            // if ((sysFee.getValue("CAT1_TYPE", 0).equals("LIS") || sysFee.getValue("CAT1_TYPE", 0)
            // .equals("RIS")) && parm.getValue("SETMAIN_FLG").equals("Y")) {
            // this.messageBox("�����鲻�ܸ�������");
            // return true;
            // }
            int groupNo = parm.getInt("ORDERSET_GROUP_NO");
            String buff = record.isFilter() ? record.FILTER : record.PRIMARY;
            int newRow[] = record.getNewRows(buff);
            // �ҵ����������д�ҽ����ΨһID
            int primaryId = (Integer) record.getItemData(tNode.getRow(), "#ID#", record.PRIMARY);
            for (int i : newRow) {
                TParm linkParm = record.getRowParm(i, buff);
                if (!record.isActive(i, buff)) continue;
                // �ҵ����˻������д�ҽ����ΨһID
                int filterId = (Integer) record.getItemData(i, "#ID#", buff);
                if (filterId == primaryId) {
                    record.setItem(i, "QTY", newQty, buff);
                    double ownPrice = TypeTool.getDouble(record.getItemData(i, "OWN_PRICE", buff));
                    record.setItem(i, "AR_AMT", StringTool.round(newQty * ownPrice, 2), buff);// ����"�ܼ�"��AR_AMT
                    continue;// ����ҽ������
                }
                if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo
                        && !linkParm.getValue("ORDERSET_CODE")
                                .equals(linkParm.getValue("ORDER_CODE"))) {
                    double orginQty =
                            TypeTool.getDouble(record.getItemData(i, "QTY", buff)) / oldQty;
                    double ownPrice = TypeTool.getDouble(record.getItemData(i, "OWN_PRICE", buff));
                    record.setItem(i, "QTY", orginQty * newQty, buff);// ���ļ���ҽ��ϸ�������QTY
                    record.setItem(i, "AR_AMT", StringTool.round(orginQty * newQty * ownPrice, 2),
                                   buff);// ����"�ܼ�"��AR_AMT
                    table.setDSValue(i);
                }
            }
            this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��
        }
        // else if ("USED_FLG".equals(colName)) {
        // String packBarCode=record.getItemString(row, "PACK_BARCODE");
        // if(!packBarCode.equals("")&& packBarCode.length() == 12
        // && .equals("000000")){
        //
        // }
        // // System.out.println("-------------tNode.getValue()---------" + tNode.getValue());
        // //// if (tNode.getValue().equals("Y")) {
        // // table.setItem(row, "BILL_FLG", tNode.getValue());
        // // table.setItem(row, "USED_FLG", tNode.getValue());
        // // record.setItem(row, "BILL_FLG", tNode.getValue());
        // // record.setItem(row, "USED_FLG", tNode.getValue());
        // // table.setDSValue(row);
        //
        // // }
        // }
        return false;
    }

	/**
	 * �����
	 */
	private String caseNo;
	
	private String clncpathCode;// �ٴ�·������
	private String schdCode;// ʱ�̴���
	
	/**
	 * ����·�� ===============pangben 2012-7-9
	 */
	public void onAddCLNCPath() {
		TParm inParm = new TParm();  
		// ����סԺ��  ��ѯ��Ժ�� ���� case_no
		
		String sqlCase = " SELECT CASE_NO,CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE " +
				" IPD_NO = '"+this.getValueString("IPD_NO")+"' AND DS_DATE IS NULL ";
		//System.out.println("sqlCase:"+sqlCase);
		TParm parmCase = new TParm(TJDODBTool.getInstance().select(sqlCase));
		caseNo = parmCase.getValue("CASE_NO",0).toString();
		// �ٴ�·������  
		clncpathCode = parmCase.getValue("CLNCPATH_CODE",0).toString();
		schdCode = parmCase.getValue("SCHD_CODE", 0).toString();// ʱ�̴���
	
		
		//�ٴ�·������
		inParm.setData("CLNCPATH_CODE", clncpathCode);
		inParm.setData("SCHD_CODE", schdCode);  
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("IND_FLG", "Y");  
		inParm.setData("IND_CLP_FLG", true);//סԺ�Ƽ�ע��-xiongwg20150429
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPTemplateOrderQuote.x", inParm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;  
		}
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) { 
	        this.setValue("ORDER_DESC", result.getValue("ORDER_CODE",i));
	        // if ((parm.getValue("CAT1_TYPE").equals("LIS") ||
	        // parm.getValue("CAT1_TYPE").equals("RIS"))
	        // && parm.getValue("ORDERSET_FLG").equals("Y")) {
	        // this.messageBox("��������������Ŀ");
	        // return;
	        // }
	        TParm orderParm = getOrderData(result.getValue("ORDER_CODE",i), false);
	        if (orderParm.getErrCode() < 0) {
	            this.messageBox(orderParm.getErrText()); 
	            this.setValue("ORDER_CODE", "");
	            this.setValue("ORDER_DESC", "");                 
	            // this.setValue("BAR_QTY", "");
	            return;
	        }
	        // System.out.println("----------------orderParm-------------"+orderParm);
	        record.addRowData(orderParm);
	        record.setFilter(" SETMAIN_FLG='Y' ");    
	        record.filter();  
	        table.setDSValue();  
	        this.setValue("ORDER_DESC", result.getValue("ORDER_DESC",i));  
	        // this.setValue("BAR_QTY", orderParm.getData("QTY"));
	        this.setValue("AR_AMT", StringTool.round(record.getTotArAmt(), 2));// �����ܽ��

		}
	}
	
    /**
     * ����ʱ���޸�================xiongwg 2015-4-26
     */
	public void onClpOrderReSchdCode() {          
		String sqlCase = " SELECT CASE_NO,CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE " +
		" IPD_NO = '"+this.getValueString("IPD_NO")+"' AND DS_DATE IS NULL ";
        TParm parmCase = new TParm(TJDODBTool.getInstance().select(sqlCase));  
        caseNo = parmCase.getValue("CASE_NO",0).toString();  
		
		String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "' AND CLNCPATH_CODE IS NOT NULL ";// ��ѯ�û����Ƿ�����ٴ�·��
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {// �����ٴ�·��
			parm = new TParm();
			parm.setData("CLP", "CASE_NO", caseNo);
			parm.setData("CLP", "MR_NO", this.getValueString("MR_NO"));
			parm.setData("CLP", "FLG", "Y");
			TParm result = (TParm) this.openDialog( 
					"%ROOT%\\config\\clp\\CLPOrderReplaceSchdCode.x", parm);
		} else {
			this.messageBox("�������ٴ�·�������ɸ���ʱ�̡�");
			return;   
		}

	}

	
	
	
	
	/**
	 * �õ���ǰ�ɱ༭��
	 * 
	 * @return int
	 */
	public int getExitRow() {
		TTable table = this.getTTable("MAINTABLE");
		int rowCount = table.getDataStore().rowCount();
		int rowOnly = -1;
		for (int i = 0; i < rowCount; i++) {
			if (!table.getDataStore().isActive(i)) {
				rowOnly = i;
				break; 
			}
		}
		return rowOnly;
	}
    
	
	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
    public void onCheckBoxClick(Object obj) {// void����ֵҲ����
        TTable table = (TTable) obj;
        table.acceptText();
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        String colName = table.getParmMap(col);
        if (colName.equals("USED_FLG")) {
            String usedFlg = table.getItemString(row, colName);
            if (usedFlg.equals("Y")) {
                // String packBarCode = record.getItemString(row, "PACK_BARCODE");
                // String packGroupNo = record.getItemString(row, "PACK_GROUP_NO");
                // if(){
                //
                // }
                // int count=record.rowCount();
                // for (int i = 0; i < count; i++) {
                // if( record.getItemString(i,
                // "PACK_BARCODE").equals(packBarCode)&&record.getItemString(i,
                // "PACK_BARCODE").equals(packGroupNo)){
                //
                // }
                // }
                table.setItem(row, "BILL_FLG", usedFlg);
            }
        }
    }

    /**
     * �õ���һ���ײ����
     * 
     * @param pack_code
     * @return
     */
    public int getNextPackGroupNo(String pack_code) {
        // �ȴӱ��ض�����ȡ����;
        TDataStore ds = table.getDataStore();
        String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
        TParm dsParm = ds.getBuffer(buff);
        // System.out.println("===SQL==="+ds.getSQL());
        int dsPackGroupNo = 0;
        for (int i = 0; i < dsParm.getCount(); i++) {
            if (!dsParm.getBoolean("#ACTIVE#", i)) {
                continue;
            }
            int tempPackGroupNo = dsParm.getRow(i).getInt("PACK_GROUP_NO");
            if (tempPackGroupNo > dsPackGroupNo) {
                dsPackGroupNo = tempPackGroupNo;
            }
        }
        int dbPackGroupNo = 0;
        String sql =
                "SELECT MAX(PACK_GROUP_NO) AS PACK_GROUP_NO   "
                        + " FROM SPC_INV_RECORD               "
                        + " WHERE CASE_NO = '                "
                        + this.getValueString("CASE_NO") + "' " + " AND MR_NO = '"
                        + this.getValueString("MR_NO") + "'" + " AND PACK_BARCODE = '" + pack_code
                        + "'";
        TParm dbParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (dbParm.getCount() <= 0) {
            dbPackGroupNo = 0;
        } else {
            dbPackGroupNo = dbParm.getInt("PACK_GROUP_NO", 0);
        }
        // ������Ӷ�����ȡ����;
        if ((dsPackGroupNo + 1) > dbPackGroupNo) {
            return dsPackGroupNo + 1;
        } else {
            return dbPackGroupNo + 1;
        }
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        if (e.getClickCount() == 2) {// ���˫��
            if (e.getComponent() == this.getComponent("MR_NO")) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(this.getText("MR_NO")), null);
            } else if (e.getComponent() == this.getComponent("IPD_NO")) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(this.getText("IPD_NO")), null);
            } else if (e.getComponent() == this.getComponent("PAT_NAME")) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(this.getText("PAT_NAME")), null);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
}
