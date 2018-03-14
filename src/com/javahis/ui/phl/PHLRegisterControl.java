package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.javahis.system.combo.TComboSession;
import com.javahis.system.combo.TComboOperatorReg;
import com.dongyang.data.TParm;
import jdo.phl.PhlRegisterTool;
import jdo.sys.PatTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.javahis.system.combo.TComboPHLBed;
import com.dongyang.manager.TIOM_AppServer;
import jdo.phl.PhlBedTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.phl.PHLSQL;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.phl.PhlRegionTool;
import com.javahis.system.textFormat.TextFormatSYSDeptForReg;
import com.javahis.system.textFormat.TextFormatREGClinicRoomForReg;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;
import jdo.reg.ClinicRoomTool;
import jdo.sys.Pat;

/**
 * <p>
 * Title: �����ұ���
 * </p>
 *
 * <p>
 * Description: �����ұ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis1.0
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class PHLRegisterControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    // ����״̬
    private boolean save_flg;

    public PHLRegisterControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            String adm_date = parm.getValue("ADM_DATE");
            String mr_no = parm.getValue("MR_NO");
            String clinicroom_code = parm.getValue("CLINICROOM_CODE");
            intiPageByONW(adm_date, mr_no, clinicroom_code);
        }
        else {
            initPopedem();
            // ��ʼ��������
            initPage();
        }
        save_flg = true;
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        // ��ʼ������ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", date);
    }

    /**
     * ��ʼ��Ȩ�޹ܿ�
     */
    private void initPopedem() {
        // Ȩ�޹ܿ�
        if (!this.getPopedem("deptAll")) {
            this.getComboBox("REGION_CODE").setEnabled(false);
            String ip = Operator.getIP();
            TParm parm = new TParm();
            TParm result = PhlRegionTool.getInstance().onQuery(parm);
            String start_ip = "";
            String end_ip = "";
            if (result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    start_ip = result.getValue("START_IP", i);
                    end_ip = result.getValue("END_IP", i);
                    if (StringTool.isIPBetween(start_ip, end_ip, ip)) {
                        this.setValue("REGION_CODE",
                                      result.getValue("REGION_CODE", i));
                        onChangeRegion();
                        break;
                    }
                }
            }
        }
    }

    /**
     * ���ﻤʿվ����ʱ��ʼ������
     */
    private void intiPageByONW(String adm_date, String mr_no,
                               String clinicroom_code) {
        // �趨����ؼ���������
        callFunction("UI|START_DATE|setEnabled", false);
        callFunction("UI|MR_NO|setEnabled", false);
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|SESSION_CODE|setEnabled", false);
        callFunction("UI|CLINICROOM_CODE|setEnabled", false);
        callFunction("UI|DEPT_CODE|setEnabled", false);
        callFunction("UI|DR_CODE|setEnabled", false);
        callFunction("UI|REGION_CODE|setEnabled", false);
        // �������
        this.setValue("START_DATE", adm_date);
        this.setValue("MR_NO", mr_no);
        this.setValue("CLINICROOM_CODE", clinicroom_code);
        // �������Ҷ�Ӧ�ľ�����
        TParm parm = ClinicRoomTool.getInstance().selectdata(clinicroom_code);
        if (parm.getCount() <= 0) {
            this.messageBox("������û���趨��Ӧ�ľ�����");
            return;
        }
        this.setValue("REGION_CODE", parm.getValue("PHL_REGION_CODE", 0));
        this.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table_m.removeRowAll();
        TParm parm = new TParm();
        String start_date = getValueString("START_DATE");
        if ("".equals(start_date)) {
            this.messageBox("�������ڲ���Ϊ��");
        }
        parm.setData("ADM_DATE", this.getValue("START_DATE"));

        if (!"".equals(getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(getValueString("ADM_TYPE"))) {
            parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
        }
        if (!"".equals(getValueString("SESSION_CODE"))) {
            parm.setData("SESSION_CODE", getValueString("SESSION_CODE"));
        }
        if (!"".equals(getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        }
        if (!"".equals(getValueString("CLINICROOM_CODE"))) {
            parm.setData("CLINICROOM_CODE", getValueString("CLINICROOM_CODE"));
        }
        if (!"".equals(getValueString("DR_CODE"))) {
            parm.setData("DR_CODE", getValueString("DR_CODE"));
        }
        if (!"".equals(getValueString("REGION_CODE"))) {
            parm.setData("PHL_REGION_CODE", getValueString("REGION_CODE"));
        }
        //======pangben modify 20110622 start
        if (null!=Operator.getRegion()&&!"".equals(Operator.getRegion())) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //======pangben modify 20110622 stop
        TParm result = PhlRegisterTool.getInstance().onQueryRegister(parm);
        //System.out.println("result==="+result);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table_m.setParmValue(result);
        TComboPHLBed bed = (TComboPHLBed)this.getComponent("BED_CODE");
        bed.setBedStatus("0");
        bed.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onSave() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("û�в�����Ϣ");
            return;
        }
        if (!save_flg) {
            this.messageBox("ҽ���ѱ���");
            return;
        }
        TParm result = new TParm();
        // ��鲡���Ƿ񱨵�
        TParm inparm = new TParm();
        inparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        inparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        result = PhlBedTool.getInstance().onQuery(inparm);
        if (result.getCount() > 0) {
            this.messageBox("�ò����ѱ���");
            return;
        }
        if ("".equals(this.getValueString("REGION_CODE"))) {
            this.messageBox("��ѡ�񾲵�����");
            return;
        }
        if ("".equals(this.getValueString("BED_CODE"))) {
            this.messageBox("��ѡ�񾲵㴲λ");
            return;
        }

        // ����Ƿ������Ҫ������ҽ��
        table_d.acceptText();
        boolean flg = false;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("û��ѡ��ҽ��");
            return;
        }

        TParm parm = new TParm();
        // ���´�λ��Ϣ
        TParm bedparm = new TParm();
        bedparm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        bedparm.setData("BED_NO", this.getValueString("BED_CODE"));
        bedparm.setData("BED_STATUS", "1");
        bedparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        bedparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        bedparm.setData("PAT_STATUS", "1");
        Timestamp date = SystemTool.getInstance().getDate();;
        bedparm.setData("REGISTER_DATE", date);
        bedparm.setData("OPT_USER", Operator.getID());
        bedparm.setData("OPT_DATE", date);
        bedparm.setData("OPT_TERM", Operator.getIP());
        parm.setData("BED_PARM", bedparm.getData());

        // ����ҽ����Ϣ
        TParm orderparm = new TParm();
        String start_date = SystemTool.getInstance().getDate().toString();
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
        //this.messageBox(start_date);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            orderparm.setData("START_DATE", i, start_date);
            orderparm.setData("ADM_TYPE", i, table_m.getItemData(row,
                "ADM_TYPE"));
            orderparm.setData("CASE_NO", i, table_m.getItemData(row,
                "CASE_NO"));
            orderparm.setData("ORDER_NO", i,
                              table_d.getParmValue().getValue("RX_NO", i));
            orderparm.setData("SEQ_NO", i,
                              table_d.getParmValue().getInt("SEQ_NO", i));
            orderparm.setData("ORDER_CODE", i,
                              table_d.getParmValue().getValue("ORDER_CODE", i));
            orderparm.setData("MR_NO", i, table_m.getItemData(row,
                "MR_NO"));
            orderparm.setData("DR_CODE", i,
                              table_d.getParmValue().getValue("DR_CODE", i));
            orderparm.setData("ORDER_DTTM", i,
                              table_d.getItemData(i, "ORDER_DATE"));
            orderparm.setData("LINK_MAIN_FLG", i, "Y".equals(table_d.
                getItemString(i, "LINKMAIN_FLG")) ? "Y" : "N");

            orderparm.setData("LINK_NO", i,
                              table_d.getItemData(i, "LINK_NO") == null ? 0 :
                              table_d.getItemData(i, "LINK_NO"));
            orderparm.setData("ROUTE_CODE", i,
                              table_d.getParmValue().getValue("ROUTE_CODE", i));
            orderparm.setData("FREQ_CODE", i,
                              table_d.getParmValue().getValue("FREQ_CODE", i));
            orderparm.setData("TAKE_DAYS", i,
                              table_d.getParmValue().getData("TAKE_DAYS", i));
            // ���÷���ȡ��ƿǩ��
            orderparm.setData("BAR_CODE", i, this.getBarCode(table_d.
                getParmValue().getValue("RX_NO", i),
                table_d.getItemString(i, "LINK_NO")));
            orderparm.setData("BAR_CODE_PRINT_FLG", i, "Y");
            orderparm.setData("EXEC_STATUS", i, "0");
            orderparm.setData("DR_NOTE", i,
                              table_d.getItemString(i, "DR_NOTE"));
            orderparm.setData("NS_NOTE", i,
                              table_d.getParmValue().getValue("NS_NOTE", i));
            orderparm.setData("OPT_USER", i, Operator.getID());
            orderparm.setData("OPT_DATE", i, date);
            orderparm.setData("OPT_TERM", i, Operator.getIP());
        }
        parm.setData("ORDER_PARM", orderparm.getData());
        result = TIOM_AppServer.executeAction("action.phl.PHLAction",
                                              "onPhlRegister", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        // ��ӡ��/����
        this.onBedPrint();
        // ��ӡƿǩ
        this.onBottlePrint();
        onClear();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "MR_NO;ADM_TYPE;SESSION_CODE;DEPT_CODE;CLINICROOM_CODE;"
            + "DR_CODE;BED_CODE";
        this.clearValue(clear);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        TComboPHLBed bed = (TComboPHLBed)this.getComponent("BED_CODE");
        bed.setBedStatus("0");
        bed.onQuery();
        save_flg = true;
    }

    /**
     * ��ͷ��
     */
    public void onCard() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("��ѡ�񾲵�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        Object result = openDialog("%ROOT%\\config\\phl\\PHLCard.x",
                                   parm);
        if (result != null) {
            TParm resultParm = (TParm) result;
            //System.out.println("resultParm=="+resultParm);
            TComboPHLBed bed = (TComboPHLBed)this.getComponent("BED_CODE");
            if ("1".equals(resultParm.getValue("BED_STATUS"))) {
                this.setValue("MR_NO", resultParm.getValue("MR_NO"));
                this.onQuery();
                bed.setBedStatus("1");
                bed.onQuery();
                this.setValue("BED_CODE", resultParm.getValue("BED_NO"));
                //this.messageBox("��λ��ռ��");
                //return;
            }
            else {
                bed.setBedStatus("0");
                bed.onQuery();
                this.setValue("BED_CODE", resultParm.getValue("BED_NO"));
            }
        }
    }

    /**
     * �ż��ﲹ��Ƽ�
     */
    public void onBill() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ�񲡻�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        parm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        parm.setData("SYSTEM","ONW");
        Object result = openDialog("%ROOT%\\config\\opb\\OPBChargesM.x",
                                   parm);
    }

    /**
     * ƿǩ��ӡ
     */
    public void onBottlePrint() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ����Ҫ��ӡƿǩ�Ĳ���");
            return;
        }
        // ��鲡���Ƿ񱨵�
        TParm inparm = new TParm();
        inparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        inparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        TParm result = PhlBedTool.getInstance().onQuery(inparm);
        if (result.getCount() <= 0) {
            this.messageBox("�ò�����δ����");
            return;
        }

        // ƿǩ��ӡ����
        // ƿǩ����
        String bar_code = "";
        // ���Ӻ�
        String link_no = "";
        // ����
        String bed_no = result.getValue("BED_DESC", 0);

        Pat pat = Pat.onQueryByMrNo(table_m.getItemString(table_m.getSelectedRow(),
            "MR_NO"));
        // ��������
        String name = pat.getName();
        // �Ա�
        String sex = pat.getSexString();
        // ����
        Timestamp date = SystemTool.getInstance().getDate();
        String age = StringUtil.getInstance().showAge(pat.getBirthday(), date);
        TParm parmData = new TParm();
        table_d.acceptText();
        TParm tablePrm  = table_d.getParmValue();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            link_no = tablePrm.getValue("LINK_NO", i);
            if ("".equals(link_no)) {
                continue;
            }
            bar_code = this.getBarCode(tablePrm.getValue("RX_NO", i), link_no);
            // ƿǩ��Ϣ����
            parmData.addData("BAR_CODE_NO", bar_code);
            parmData.addData("BAR_CODE", bar_code);
            parmData.addData("LINK_NO", link_no);
            parmData.addData("BED_NO", bed_no);
            parmData.addData("NAME", name);
            parmData.addData("SEX", sex);
            parmData.addData("AGE", age);
            parmData.addData("DR_NAME", tablePrm.getValue("DR_NAME", i));
            // ҽ����Ϣ����
            parmData.addData("ORDER_DESC", tablePrm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION",
                             tablePrm.getValue("SPECIFICATION", i));
            parmData.addData("DISPENSE_QTY",
                             tablePrm.getValue("DISPENSE_QTY", i));
            parmData.addData("UNIT_CHN_DESC",
                             tablePrm.getValue("UNIT_CHN_DESC", i));
            parmData.addData("ROUTE_CHN_DESC",
                             tablePrm.getValue("ROUTE_CHN_DESC", i));
            parmData.addData("DR_NOTE", tablePrm.getValue("DR_NOTE", i));
        }

        if (parmData == null || parmData.getCount("BAR_CODE_NO") <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡƿǩ
        onPrintBottleData(parmData);
    }

    /**
     * ƿǩ��ӡ����
     * @param parm TParm
     */
    private void onPrintBottleData(TParm parm) {
        // ��ӡ����
        TParm date = new TParm();
        // ���ڱ��治ͬ��ƿǩ��(BAR_CODE)
        Map map = new HashMap();
        for (int i = 0; i < parm.getCount("BAR_CODE"); i++) {
            if (map.isEmpty()) {
                map.put(parm.getValue("BAR_CODE", i), i);
            }
            else {
                if (map.containsKey(parm.getValue("BAR_CODE", i))) {
                    continue;
                }
                else {
                    map.put(parm.getValue("BAR_CODE", i), i);
                }
            }
        }

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        String bar_code = "";
        int row = 0;

        String[] order_code_list = {
            "ORDER_CODE_1", "ORDER_CODE_2", "ORDER_CODE_3", "ORDER_CODE_4",
            "ORDER_CODE_5"};
        String[] specification_list = {
            "SPECIFICATION_1", "SPECIFICATION_2", "SPECIFICATION_3",
            "SPECIFICATION_4", "SPECIFICATION_5"};
        String[] user_qty_list = {
            "USER_QTY_1", "USER_QTY_2", "USER_QTY_3", "USER_QTY_4",
            "USER_QTY_5"};
        String[] unit_code_list = {
            "UNIT_CODE_1", "UNIT_CODE_2", "UNIT_CODE_3", "UNIT_CODE_4",
            "UNIT_CODE_5"};
        String[] user_type_list = {
            "USER_TYPE_1", "USER_TYPE_2", "USER_TYPE_3", "USER_TYPE_4",
            "USER_TYPE_5"};
        String[] note_list = {
            "NOTE_1", "NOTE_2", "NOTE_3", "NOTE_4", "NOTE_5"};
        //this.messageBox("");
        while (iterator.hasNext()) {
            // �������
            TParm parmData = new TParm();
            bar_code = TypeTool.getString(iterator.next());
            row = TypeTool.getInt(map.get(bar_code));
            parmData.addData("BAR_CODE_NO", bar_code);
            parmData.addData("BAR_CODE", "�����:" + bar_code);
            parmData.addData("LINK_NO", "���:" + parm.getValue("LINK_NO", row));
            parmData.addData("BED_NO", "��/����:" + parm.getValue("BED_NO", row));
            parmData.addData("NAME", "����:" + parm.getValue("NAME", row));
            parmData.addData("SEX", "�Ա�:"+ parm.getValue("SEX", row));
            parmData.addData("AGE", "����:" + parm.getValue("AGE", row));
            parmData.addData("DR_NAME",
                             "����ҽ��:" + parm.getValue("DR_NAME", row));
            int count = 0;
            String order_desc = "";
            String specification = "";
            for (int i = row; i < parm.getCount("BAR_CODE"); i++) {
                if (bar_code.equals(parm.getValue("BAR_CODE", i))) {
                    order_desc = parm.getValue("ORDER_DESC", i);
                    if (order_desc.length() > 8) {
                        order_desc = order_desc.substring(0, 8);
                    }
                    parmData.addData(order_code_list[count], order_desc);
                    specification = parm.getValue("SPECIFICATION", i);
                    if (specification.length() > 10) {
                        specification = specification.substring(0, 10);
                    }
                    parmData.addData(specification_list[count], specification);
                    parmData.addData(user_qty_list[count],
                                     parm.getValue("DISPENSE_QTY", i));
                    parmData.addData(unit_code_list[count],
                                     parm.getValue("UNIT_CHN_DESC", i));
                    parmData.addData(user_type_list[count],
                                     parm.getValue("ROUTE_CHN_DESC", i));
                    parmData.addData(note_list[count],
                                     parm.getValue("DR_NOTE", i));
                    count++;
                }
                else {
                    break;
                }
            }
            // ����5��ҽ���ֶ�����
            if (count < 4) {
                for (int i = count; i < 5; i++) {
                    parmData.addData(order_code_list[i], "");
                    parmData.addData(specification_list[i], "");
                    parmData.addData(user_qty_list[i], "");
                    parmData.addData(unit_code_list[i], "");
                    parmData.addData(user_type_list[i], "");
                    parmData.addData(note_list[i], "");
                }
            }
            parmData.setCount(1);
            parmData.addData("SYSTEM", "COLUMNS", "BAR_CODE_NO");
            parmData.addData("SYSTEM", "COLUMNS", "BAR_CODE");
            parmData.addData("SYSTEM", "COLUMNS", "LINK_NO");
            parmData.addData("SYSTEM", "COLUMNS", "BED_NO");
            parmData.addData("SYSTEM", "COLUMNS", "NAME");
            parmData.addData("SYSTEM", "COLUMNS", "SEX");
            parmData.addData("SYSTEM", "COLUMNS", "AGE");
            parmData.addData("SYSTEM", "COLUMNS", "DR_NAME");
            for (int i = 0; i < 5; i++) {
                parmData.addData("SYSTEM", "COLUMNS", order_code_list[i]);
                parmData.addData("SYSTEM", "COLUMNS", specification_list[i]);
                parmData.addData("SYSTEM", "COLUMNS", user_qty_list[i]);
                parmData.addData("SYSTEM", "COLUMNS", unit_code_list[i]);
                parmData.addData("SYSTEM", "COLUMNS", user_type_list[i]);
                parmData.addData("SYSTEM", "COLUMNS", note_list[i]);
            }
            //System.out.println("parmData---" + parmData);
            date.setData("TABLE", parmData.getData());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\PHL\\PhlBarCode.jhw",
                                 date);
        }

    }

    /**
     * ��/����ӡ
     */
    public void onBedPrint() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ����Ҫ��ӡ��/�����Ĳ���");
            return;
        }
        // ��鲡���Ƿ񱨵�
        TParm inparm = new TParm();
        inparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        inparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        TParm result = PhlBedTool.getInstance().onQuery(inparm);
        if (result.getCount() <= 0) {
            this.messageBox("�ò�����δ����");
            return;
        }
        // ��/������
        String sql = PHLSQL.getPHLBedCard(table_m.getItemString(row, "MR_NO"),
                                          table_m.getItemString(row, "CASE_NO"));
        TParm parmData = new TParm(TJDODBTool.getInstance().select(sql));
        if (parmData == null || parmData.getCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        //��ӡ��/��ƿǩ
        onPrintBedData(parmData);
    }

    /**
     * ��/��ƿǩ��ӡ����
     * @param parm TParm
     */
    public void onPrintBedData(TParm parm) {
        int row = table_m.getSelectedRow();
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("ORDER_DTTM", "TEXT",
                     "��������:" +
                     table_m.getItemString(row, "ADM_DATE").substring(0, 10).
                     replace('-', '/'));
        String register_date = parm.getValue("REGISTER_DATE", 0);
        date.setData("REGISTER_DATE", "TEXT",
                     "����ʱ��:" + register_date.substring(11, 19));
        date.setData("NAME", "TEXT",
                     "����:" + table_m.getItemString(row, "PAT_NAME"));
        date.setData("SEX", "TEXT",
                     "1".equals(parm.getValue("SEX_CODE", 0)) ? "�Ա�:��" : "�Ա�:Ů");
        Timestamp datetime = SystemTool.getInstance().getDate();
        date.setData("AGE", "TEXT",
                     "����:" + StringUtil.getInstance().showAge(parm.
            getTimestamp("BIRTH_DATE", 0), datetime));
        date.setData("CTZ", "TEXT",
                     "�ѱ�:" + parm.getValue("CTZ_DESC", 0));
        date.setData("REGION_CODE", "TEXT",
                     "��������:" + parm.getValue("REGION_DESC", 0));
        date.setData("BED_NO", "TEXT",
                     "��/����:" + parm.getValue("BED_DESC", 0));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\PHL\\PHLBedCard.jhw",
                             date);
    }

    /**
     * ���(CLNDIAG_TABLE)�����¼�
     */
    public void onTableMClicked() {
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        int row = table_m.getSelectedRow();
        if (row != -1) {
            TParm parm = new TParm();
            parm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
            parm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
            TParm result = PhlRegisterTool.getInstance().onQueryOrderDetail(
                parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û��ҽ����Ϣ");
                return;
            }
            table_d.setParmValue(result);
            TComboPHLBed bed = (TComboPHLBed)this.getComponent("BED_CODE");
            bed.setBedStatus("0");
            bed.onQuery();
        }
    }

    /**
     * �����ű���¼�
     */
    public void onChangeMrNo() {
        String mr_no = PatTool.getInstance().checkMrno(this.
            getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no);
		// modify by huangtt 20160929 EMPI���߲�����ʾ start
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("������" + mr_no + " �Ѻϲ��� " + "" + pat.getMrNo());
			mr_no = pat.getMrNo();
			this.setValue("MR_NO", mr_no);// ������
		}
		// modify by huangtt 20160929 EMPI���߲�����ʾ end

        
        TParm parm = new TParm(TJDODBTool.getInstance().select(PHLSQL.
            getLastAdmDateByMrNo(mr_no)));
        if (parm.getCount() <= 0) {
            this.messageBox("�����ھ����¼");
            return;
        }
        this.setValue("START_DATE",
                      parm.getValue("ADM_DATE", 0).substring(0, 10).
                      replace('-', '/'));
        this.onQuery();
    }

    /**
     * ������Դ����¼�
     */
    public void onChangeAdmType() {
        this.setValue("SESSION_CODE", "");
        ( (TComboSession)this.getComponent("SESSION_CODE")).setAdmType(this.
            getValueString("ADM_TYPE"));
        ( (TComboSession)this.getComponent("SESSION_CODE")).onQuery();
        initDeptList();
        initClinicRoomList();
        initDrCodeList();
    }

    /**
     * ʱ�α���¼�
     */
    public void onChangeSeesion() {
        initDeptList();
        initClinicRoomList();
        initDrCodeList();
    }

    /**
     * �Ʊ����¼�
     */
    public void onChangeDept() {
        initDrCodeList();
    }

    /**
     * �������¼�
     */
    public void onChangeRegion() {
        this.setValue("BED_CODE", "");
        ( (TComboPHLBed)this.getComponent("BED_CODE")).setRegionCode(this.
            getValueString("REGION_CODE"));
        ( (TComboPHLBed)this.getComponent("BED_CODE")).onQuery();
        ( (TextFormatREGClinicRoomForReg)this.getComponent(
            "CLINICROOM_CODE")).setPhlRegionCode(this.getValueString(
            "REGION_CODE"));
        ( (TextFormatREGClinicRoomForReg)this.getComponent("CLINICROOM_CODE")).
            onQuery();
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * ��ʼ���Ʊ�
     */
    private void initDeptList() {
        this.setValue("DEPT_CODE", "");
        String adm_type = this.getValueString("ADM_TYPE");
        String adm_date = this.getValueString("START_DATE");
        adm_date = adm_date.substring(0, 4) + adm_date.substring(5, 7) +
            adm_date.substring(8, 10);
        if (!"".equals(adm_type)) {
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).setAdmType(
                adm_type);
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).setAdmDate(
                adm_date);
        }
        String session_code = this.getValueString("SESSION_CODE");
        if (!"".equals(session_code)) {
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).
                setSessionCode(session_code);
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).setAdmDate(
                adm_date);
        }
        ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).onQuery();
    }

    /**
     * ��ʼ������
     */
    private void initClinicRoomList() {
        String adm_type = this.getValueString("ADM_TYPE");
        String phl_region_code = getValueString("REGION_CODE");
        //this.messageBox(phl_region_code);
        if (!"".equals(adm_type)) {
            ( (TextFormatREGClinicRoomForReg)this.getComponent(
                "CLINICROOM_CODE")).setAdmType(adm_type);
        }
        String session_code = this.getValueString("SESSION_CODE");
        if (!"".equals(session_code)) {
            ( (TextFormatREGClinicRoomForReg)this.getComponent(
                "CLINICROOM_CODE")).setSessionCode(session_code);
        }
        ( (TextFormatREGClinicRoomForReg)this.getComponent(
            "CLINICROOM_CODE")).setPhlRegionCode(phl_region_code);
        ( (TextFormatREGClinicRoomForReg)this.getComponent("CLINICROOM_CODE")).
            onQuery();
    }

    /**
     * ��ʼ������ҽ��
     */
    private void initDrCodeList() {
        this.setValue("DR_CODE", "");
        String adm_type = this.getValueString("ADM_TYPE");
        if (!"".equals(adm_type)) {
            ( (TextFormatREGClinicRoomForReg)this.getComponent("CLINICROOM_CODE")).
                setAdmType(adm_type);
        }
        String session_code = this.getValueString("SESSION_CODE");
        if (!"".equals(session_code)) {
            ( (TextFormatSYSOperatorForReg)this.getComponent("DR_CODE")).
                setSessionCode(session_code);
        }
        String dept_code = this.getValueString("DEPT_CODE");
        if (!"".equals(dept_code)) {
            ( (TextFormatSYSOperatorForReg)this.getComponent("DR_CODE")).
                setDeptCode(dept_code);
        }
        ( (TComboOperatorReg)this.getComponent("DR_CODE")).onQuery();
    }

    /**
     * ȡ��ƿǩ��
     * @param orderNo String
     * @param linkNo String
     * @return String
     */
    private String getBarCode(String orderNo, String linkNo) {
        if ("".equals(linkNo)) {
            return "";
        }
        linkNo = "00".substring(0, 2 - linkNo.length()) + linkNo.trim();
        return orderNo + linkNo;
    }
}
