package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;
import jdo.sta.STAIn_02TTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import jdo.sta.STADeptListTool;
import java.text.DecimalFormat;
import java.sql.Timestamp;
import jdo.sta.STATool;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: STA_In_02TҽԺ�š����﹤��ͳ��̨��</p>
 *
 * <p>Description: ҽԺ�š����﹤��ͳ��̨��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-1
 * @version 1.0
 */
public class STAIn_02TControl extends TControl {
    public void onInit() {
        super.init();
        showFormat();
    }

    /**
     * �����������
     *====== pangben modify 20110525
     */
    public void showFormat() {
        TTextFormat combo_dept = (TTextFormat)this.getComponent("STA_DEPT1");
        TTextFormat combo_dept1 = (TTextFormat)this.getComponent("STA_DEPT2");
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            combo_dept.setPopupMenuSQL(combo_dept.getPopupMenuSQL() +
                                       " WHERE REGION_CODE='" +
                                       Operator.getRegion() +
                                       "' ORDER BY DEPT_CODE");
            combo_dept1.setPopupMenuSQL(combo_dept1.getPopupMenuSQL() +
                                        " WHERE REGION_CODE='" +
                                        Operator.getRegion() +
                                        "' ORDER BY DEPT_CODE");
        } else {
            combo_dept.setPopupMenuSQL(combo_dept.getPopupMenuSQL() +
                                       " ORDER BY DEPT_CODE");
            combo_dept1.setPopupMenuSQL(combo_dept1.getPopupMenuSQL() +
                                        " ORDER BY DEPT_CODE");

        }

        combo_dept.onQuery();
        combo_dept1.onQuery();
    }

    /**
     *
     */
    public void onClear() {
        this.clearValue(
                "B_YEAR;B_MONTH;STA_DEPT1;QY1;QY2;Q_Month1;Q_Month2;STA_DEPT2;Check2");
        this.setValue("Check1", true);
        this.setValue("B_Radio1", true);
        this.setValue("QS_1", true);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm result;
        //����
        if (((TCheckBox)this.getComponent("Check1")).isSelected()) {
            result = bQuery(); //��ȡ����
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrName() + result.getErrText());
                return;
            }
            gridBind(result, 0); //���ݰ�
        }
        //����
        else if (((TCheckBox)this.getComponent("Check2")).isSelected()) {
            result = q_Query(); //��ȡ��������
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrName() + result.getErrText());
                return;
            }
            gridBind(result, 1); //���ݰ�
        }
    }

    /**
     * ��ѯ���ڵĽ����
     * @return TParm
     */
    private TParm bQuery() {
        TParm result = new TParm(); //���
        String STA_DATE = "";
        String DEPT_CODE = this.getValueString("STA_DEPT1"); //����
        if (DEPT_CODE.trim().length() <= 0) {
            result.setErr( -1, "��ѡ�����");
            return result;
        }
        //��ȡ�ÿ��ҵ���Ϣ
        TParm DeptList = STADeptListTool.getInstance().selectDeptByCode(
                DEPT_CODE, "",Operator.getRegion()); //============pangben modify 20110523 ����
        if (DeptList.getErrCode() < 0) {
            return DeptList;
        }
        String DEPT_DESC = DeptList.getValue("DEPT_DESC", 0); //�м䲿������
        TParm parm = new TParm(); //����
        parm.setData("Dept", DEPT_CODE);
        //��
        if (((TRadioButton)this.getComponent("B_Radio1")).isSelected()) {
            STA_DATE = this.getText("B_YEAR");
            if (STA_DATE.trim().length() <= 0) {
                result.setErr( -1, "����дҪ��ѯ������");
                return result;
            }
            parm.setData("Year", STA_DATE); //��ѯһ��Ĳ���
        } else if (((TRadioButton)this.getComponent("B_Radio2")).isSelected()) { //��
            STA_DATE = this.getText("B_MONTH").replace("/", "");
            if (STA_DATE.trim().length() <= 0) {
                result.setErr( -1, "����дҪ��ѯ������");
                return result;
            }
            parm.setData("Month", STA_DATE); //��ѯһ�µĲ���
        }
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion()); //============pangben modify 20110523 ����
        TParm data = STAIn_02TTool.getInstance().selectBQ(parm);
        if (data.getErrCode() < 0) {
            return data;
        }
        //��ѯ������Ϊ��
        if (data.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "û�з�������������");
            ((TTable)this.getComponent("Table")).removeRowAll();
            return result;
        }
        result = sumResult(data, DEPT_DESC); //�Բ�ѯ������л���
        return result;
    }

    /**
     * ���Ʋ�ѯ
     * @return TParm
     */
    public TParm q_Query() {
        TParm result = new TParm(); //���
        String DEPT_CODE = this.getValueString("STA_DEPT2"); //����
        if (DEPT_CODE.trim().length() <= 0) {
            result.setErr( -1, "��ѡ�����");
            return result;
        }
        //��ȡ�ÿ��ҵ���Ϣ
        TParm DeptList = STADeptListTool.getInstance().selectDeptByCode(
                DEPT_CODE, "",Operator.getRegion()); //=======pangben modify 20110523
        if (DeptList.getErrCode() < 0) {
            return DeptList;
        }
        String DEPT_DESC = DeptList.getValue("DEPT_DESC", 0); //�м䲿������
        //�ж�����˳��
        Timestamp a = null;
        Timestamp b = null;
        int type = 0;

        String DATE_S = ""; //��ʼ����
        String DATE_E = ""; //��������
        if (((TRadioButton)this.getComponent("QS_1")).isSelected()) { //��
            DATE_S = this.getText("QY1").replace("/", "");
            DATE_E = this.getText("QY2").replace("/", "");
            a = (Timestamp)this.getValue("QY1");
            b = (Timestamp)this.getValue("QY2");
            type = 1;
        } else if (((TRadioButton)this.getComponent("QS_2")).isSelected()) { //��
            DATE_S = this.getText("Q_Month1").replace("/", "");
            DATE_E = this.getText("Q_Month2").replace("/", "");
            a = (Timestamp)this.getValue("Q_Month1");
            b = (Timestamp)this.getValue("Q_Month2");
            type = 0;
        }
        if (DATE_S.trim().length() <= 0 || DATE_E.trim().length() <= 0) {
            result.setErr( -1, "����д���ڷ�Χ");
            return result;
        }
        if (a.compareTo(b) == 1) {
            result.setErr( -1, "ͳ���������ڲ������ڽ�ֹ����");
            return result;
        }
        if (type == 1) { //���� ���ͳ�� ����ݼ����·ݣ��Ա�SQL��ѯ���ݿ�
            DATE_S = DATE_S + "01";
            DATE_E = DATE_E + "12";
        }
        TParm parm = new TParm();
        parm.setData("Dept", DEPT_CODE);
        parm.setData("DATE_S", DATE_S);
        parm.setData("DATE_E", DATE_E);
        //===========pangben modify 20110523 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        //===========pangben modify 20110523 stop
        TParm data = STAIn_02TTool.getInstance().selectQS(parm);
        if (data.getErrCode() < 0) {
            return data;
        }
        //��ѯ������Ϊ��
        if (data.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "û�з�������������");
            ((TTable)this.getComponent("Table")).removeRowAll();
            return result;
        }
        result = sumQResult(data, DEPT_DESC, DATE_S, DATE_E, type);
        return result;
    }

    /**
     * ������������
     * @param data TParm  ����
     * @param DEPT_DESC String ��������
     * @param DATE_S String ��ʼʱ��
     * @param DATE_E String ��ֹʱ��
     * @param Type int �������� 0���£�1����
     * @return TParm
     */
    private TParm sumQResult(TParm data, String DEPT_DESC, String DATE_S,
                             String DATE_E, int Type) {
        TParm result = new TParm();
        TParm rowData = new TParm();
        TParm dateData = null;
        if (Type == 0) { //������
            String month = DATE_S;
            //ѭ���ۼӳ�ʼ�·ݣ����ڽ�ֹ�·�ʱ����
            do {
                dateData = getDataByDate(data, month, 0); //ɸѡָ�����ڵ�����
                rowData = sumResult(dateData, DEPT_DESC); //��������
                rowData.addData("STA_DATE",
                                month.substring(0, 4) + "��" + month.substring(4) +
                                "��"); //��������
                result.addRowData(rowData, 0); //��ȡ�����ݼ��뵽�������
                month = STATool.getInstance().rollMonth(month, 1);
            } while (!month.equals(STATool.getInstance().rollMonth(DATE_E, 1)));
        } else if (Type == 1) { //�����
            int year = Integer.parseInt(DATE_S.substring(0, 4));
            int end = Integer.parseInt(DATE_E.substring(0, 4));
            //ѭ���ۼӳ�ʼ��ݣ����ڽ�ֹ���ʱ����
            do {
                rowData = sumResult(getDataByDate(data, String.valueOf(year), 1),
                                    DEPT_DESC); //��������
                rowData.addData("STA_DATE", year + "��"); //��������
                result.addRowData(rowData, 0); //��ȡ�����ݼ��뵽�������
                year++;
            } while (year != (end + 1));
        }
        return result;
    }

    /**
     * ��������ɸѡ����
     * @param data TParm  Ҫ����ɸѡ������
     * @param Date String ���ڸ�ʽ��yyyyMM ���� yyyy
     * @param type int 0:�£�1����
     * @return TParm
     */
    private TParm getDataByDate(TParm data, String Date, int type) {
        TParm result = new TParm();
        String STA_DATE = "";
        for (int i = 0; i < data.getCount("STA_DATE"); i++) {
            if (type == 0) //���·�ͳ��
                STA_DATE = data.getValue("STA_DATE", i);
            else if (type == 1) //����ͳ��
                STA_DATE = data.getValue("STA_DATE", i).substring(0, 4);
            if (STA_DATE.equals(Date)) {
                result.addRowData(data, i);
            }
        }
        return result;
    }

    /**
     * ���ݲ��Ż��ܽ��
     * @param data TParm  ����
     * @param DEPT_DESC String  ��������
     * @return TParm
     */
    private TParm sumResult(TParm data, String DEPT_DESC) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm printData = new TParm(); //�������
        //������� �����ۼ��Ӳ��ŵ���ֵ
        int DATA_02 = 0;
        int DATA_03 = 0;
        int DATA_04 = 0;
        int DATA_05 = 0;
        int DATA_06 = 0;
        int DATA_07 = 0;
        int DATA_08 = 0;
        int DATA_09 = 0;
        int DATA_10 = 0;
        int DATA_11 = 0;
        int DATA_12 = 0;
        int DATA_13 = 0;
        int DATA_14 = 0;
        int DATA_15 = 0;
        int DATA_16 = 0;
        int DATA_17 = 0;
        int DATA_18 = 0;
        double DATA_19 = 0;
        double DATA_20 = 0;
        double DATA_21 = 0;
        double DATA_22 = 0;
        double DATA_23 = 0;
        double DATA_24 = 0;
        int DATA_25 = 0;
        int DATA_26 = 0;
        double DATA_27 = 0;
        double DATA_28 = 0;
        double DATA_29 = 0;
        int DATA_30 = 0;
        double DATA_31 = 0;
        int DATA_32 = 0;
        double DATA_33 = 0;
        double DATA_34 = 0;
        //ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
        for (int j = 0; j < data.getCount("STA_DATE"); j++) {
            DATA_02 += data.getInt("DATA_02", j);
            DATA_03 += data.getInt("DATA_03", j);
            DATA_04 += data.getInt("DATA_04", j);
            DATA_05 += data.getInt("DATA_05", j);
            DATA_06 += data.getInt("DATA_06", j);
            DATA_07 += data.getInt("DATA_07", j);
            DATA_08 += data.getInt("DATA_08", j);
            DATA_09 += data.getInt("DATA_09", j);
            DATA_10 += data.getInt("DATA_10", j);
            DATA_11 += data.getInt("DATA_11", j);
            DATA_12 += data.getInt("DATA_12", j);
            DATA_13 += data.getInt("DATA_13", j);
            DATA_14 += data.getInt("DATA_14", j);
            DATA_15 += data.getInt("DATA_15", j);
            DATA_16 += data.getInt("DATA_16", j);
            DATA_17 += data.getInt("DATA_17", j);
            DATA_18 += data.getInt("DATA_18", j);
            DATA_19 += data.getDouble("DATA_19", j);
            DATA_20 += data.getDouble("DATA_20", j);
            DATA_21 += data.getDouble("DATA_21", j);
            DATA_22 += data.getDouble("DATA_22", j);
            DATA_23 += data.getDouble("DATA_23", j);
            DATA_24 += data.getDouble("DATA_24", j);
            DATA_25 += data.getInt("DATA_25", j);
            DATA_26 += data.getInt("DATA_26", j);
            DATA_30 += data.getInt("DATA_30", j);
            DATA_31 += data.getDouble("DATA_31", j);
            DATA_32 += data.getInt("DATA_32", j);
            DATA_33 += data.getDouble("DATA_33", j);
            DATA_34 += data.getDouble("DATA_34", j);
        }
        if (DATA_25 != 0) {
            DATA_27 = (DATA_25 - DATA_26) / DATA_25; //����������
            DATA_28 = DATA_26 / DATA_25; //�������ȳɹ���
        }
        if (DATA_09 != 0) {
            DATA_29 = DATA_10 / DATA_09; //�۲���������
        }
        printData.addData("DEPT_DESC", DEPT_DESC);
        printData.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        printData.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
        printData.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        printData.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
        printData.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        printData.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
        printData.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        printData.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
        printData.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        printData.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
        printData.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        printData.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
        printData.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        printData.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
        printData.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        printData.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
        printData.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        printData.addData("DATA_19", DATA_19 == 0 ? "" : df.format(DATA_19));
        printData.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
        printData.addData("DATA_21", DATA_21 == 0 ? "" : df.format(DATA_21));
        printData.addData("DATA_22", DATA_22 == 0 ? "" : df.format(DATA_22));
        printData.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
        printData.addData("DATA_24", DATA_24 == 0 ? "" : df.format(DATA_24));
        printData.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
        printData.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
        printData.addData("DATA_27", DATA_27 == 0 ? "" : df.format(DATA_27));
        printData.addData("DATA_28", DATA_28 == 0 ? "" : df.format(DATA_28));
        printData.addData("DATA_29", DATA_29 == 0 ? "" : df.format(DATA_29));
        printData.addData("DATA_30", DATA_30 == 0 ? "" : DATA_30);
        printData.addData("DATA_31", DATA_31 == 0 ? "" : DATA_31);
        printData.addData("DATA_32", DATA_32 == 0 ? "" : DATA_32);
        printData.addData("DATA_33", DATA_33 == 0 ? "" : DATA_33);
        printData.addData("DATA_34", DATA_34 == 0 ? "" : DATA_34);
        return printData;
    }

    /**
     * ��GRID
     * @param data TParm  ����
     * @param type int  �������� 0:�������� 1:��������
     */
    private void gridBind(TParm data, int type) {
    	DecimalFormat df = new DecimalFormat("0.00");
        TTable table = (TTable)this.getComponent("Table");
        if (type == 0) {
            //���ñ�ͷ
            table.setHeader("�Ʊ�,120;�����˴��ܼ�,100;�ż����˴μ�,100;�����˴�,80;�����˴μ�,90;������������,100;�������,80;�Һ��ҹҺ���,100;�۲������ݲ�����,120;�۲�����������,120;��������������ҽʦ,120;����������ҽʦ,110;������סԺҽʦ,110;���������ҽʦ,110;����Сʱ,80;����ʵ�ʹ�����,110;������˴�,100;������˴�,100;ƽ��ÿ�������˴�,120;ƽ��ÿ�ռ����˴�,120;ÿҽʦÿСʱ�����˴�,150;ÿ����ÿ���ż����֮��,160;ÿ���ż������Ժ�˴�,130;�ż������ռ����εİٷֱ�,160;������������,100;���ȳɹ�����,100;����������,90;�������ȳɹ���,110;�۲���������,100;�ż����˴�������,120;�ż����˴����������,140;�ż��������˴�,110;��Ժҽʦ�Ƚ���ҽʦ,130;��������ҽʦ��סԺҽʦ,150");
            table.setLockColumns("all"); //���������в��ɱ༭
            //���ý���˳��
            table.setFocusIndexList("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33");
            //���ö��䷽ʽ
            table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,right;32,right;33,right");
            //�趨���ݶ�Ӧ��  parmMap
            table.setParmMap("DEPT_DESC;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24;DATA_25;DATA_26;DATA_27;DATA_28;DATA_29;DATA_30;DATA_31;DATA_32;DATA_33;DATA_34");
            table.setParmValue(data); //���ݰ�
            table.retrieve();
        } else if (type == 1) {
            //���ñ�ͷ
            table.setHeader("����,80;�Ʊ�,120;�����˴��ܼ�,100;�ż����˴μ�,100;�����˴�,80;�����˴μ�,90;������������,100;�������,80;�Һ��ҹҺ���,100;�۲������ݲ�����,120;�۲�����������,120;��������������ҽʦ,120;����������ҽʦ,110;������סԺҽʦ,110;���������ҽʦ,110;����Сʱ,80;����ʵ�ʹ�����,110;������˴�,100;������˴�,100;ƽ��ÿ�������˴�,120;ƽ��ÿ�ռ����˴�,120;ÿҽʦÿСʱ�����˴�,150;ÿ����ÿ���ż����֮��,160;ÿ���ż������Ժ�˴�,130;�ż������ռ����εİٷֱ�,160;������������,100;���ȳɹ�����,100;����������,90;�������ȳɹ���,110;�۲���������,100;�ż����˴�������,120;�ż����˴����������,140;�ż��������˴�,110;��Ժҽʦ�Ƚ���ҽʦ,130;��������ҽʦ��סԺҽʦ,150");
            table.setLockColumns("all"); //���������в��ɱ༭
            //���ý���˳��
            table.setFocusIndexList("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34");
            //���ö��䷽ʽ
            table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right;29,right;30,right;31,right;32,right;33,right;34:right");
            //�趨���ݶ�Ӧ��  parmMap
            table.setParmMap("STA_DATE;DEPT_DESC;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24;DATA_25;DATA_26;DATA_27;DATA_28;DATA_29;DATA_30;DATA_31;DATA_32;DATA_33;DATA_34;DATA_35");
          //������� �����ۼ��Ӳ��ŵ���ֵ
            int DATA_02 = 0;
            int DATA_03 = 0;
            int DATA_04 = 0;
            int DATA_05 = 0;
            int DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            int DATA_10 = 0;
            int DATA_11 = 0;
            int DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            int DATA_17 = 0;
            int DATA_18 = 0;
            double DATA_19 = 0;
            double DATA_20 = 0;
            double DATA_21 = 0;
            double DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int DATA_25 = 0;
            int DATA_26 = 0;
            double DATA_27 = 0;
            double DATA_28 = 0;
            double DATA_29 = 0;
            int DATA_30 = 0;
            double DATA_31 = 0;
            int DATA_32 = 0;
            double DATA_33 = 0;
            double DATA_34 = 0;
            if(data.getCount("STA_DATE")<=0)
            	return;
            //ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
            for (int j = 0; j < data.getCount("STA_DATE"); j++) {
                DATA_02 += data.getInt("DATA_02", j);
                DATA_03 += data.getInt("DATA_03", j);
                DATA_04 += data.getInt("DATA_04", j);
                DATA_05 += data.getInt("DATA_05", j);
                DATA_06 += data.getInt("DATA_06", j);
                DATA_07 += data.getInt("DATA_07", j);
                DATA_08 += data.getInt("DATA_08", j);
                DATA_09 += data.getInt("DATA_09", j);
                DATA_10 += data.getInt("DATA_10", j);
                DATA_11 += data.getInt("DATA_11", j);
                DATA_12 += data.getInt("DATA_12", j);
                DATA_13 += data.getInt("DATA_13", j);
                DATA_14 += data.getInt("DATA_14", j);
                DATA_15 += data.getInt("DATA_15", j);
                DATA_16 += data.getInt("DATA_16", j);
                DATA_17 += data.getInt("DATA_17", j);
                DATA_18 += data.getInt("DATA_18", j);
                DATA_19 += data.getDouble("DATA_19", j);
                DATA_20 += data.getDouble("DATA_20", j);
                DATA_21 += data.getDouble("DATA_21", j);
                DATA_22 += data.getDouble("DATA_22", j);
                DATA_23 += data.getDouble("DATA_23", j);
                DATA_24 += data.getDouble("DATA_24", j);
                DATA_25 += data.getInt("DATA_25", j);
                DATA_26 += data.getInt("DATA_26", j);
                DATA_30 += data.getInt("DATA_30", j);
                DATA_31 += data.getDouble("DATA_31", j);
                DATA_32 += data.getInt("DATA_32", j);
                DATA_33 += data.getDouble("DATA_33", j);
                DATA_34 += data.getDouble("DATA_34", j);
            }
            if (DATA_25 != 0) {
                DATA_27 = (DATA_25 - DATA_26) / DATA_25; //����������
                DATA_28 = DATA_26 / DATA_25; //�������ȳɹ���
            }
            if (DATA_09 != 0) {
                DATA_29 = DATA_10 / DATA_09; //�۲���������
            }
            data.addData("STA_DATE", "�ܼ�:");
            data.addData("DEPT_DESC", data.getValue("DEPT_DESC", 0));
            data.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
            data.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
            data.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
            data.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
            data.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
            data.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            data.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            data.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            data.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
            data.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
            data.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
            data.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
            data.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
            data.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
            data.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            data.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
            data.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            data.addData("DATA_19", DATA_19 == 0 ? "" : df.format(DATA_19));
            data.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
            data.addData("DATA_21", DATA_21 == 0 ? "" : df.format(DATA_21));
            data.addData("DATA_22", DATA_22 == 0 ? "" : df.format(DATA_22));
            data.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
            data.addData("DATA_24", DATA_24 == 0 ? "" : df.format(DATA_24));
            data.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
            data.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
            data.addData("DATA_27", DATA_27 == 0 ? "" : df.format(DATA_27));
            data.addData("DATA_28", DATA_28 == 0 ? "" : df.format(DATA_28));
            data.addData("DATA_29", DATA_29 == 0 ? "" : df.format(DATA_29));
            data.addData("DATA_30", DATA_30 == 0 ? "" : DATA_30);
            data.addData("DATA_31", DATA_31 == 0 ? "" : DATA_31);
            data.addData("DATA_32", DATA_32 == 0 ? "" : DATA_32);
            data.addData("DATA_33", DATA_33 == 0 ? "" : DATA_33);
            data.addData("DATA_34", DATA_34 == 0 ? "" : DATA_34);
            data.setCount(data.getCount("STA_DATE"));
            table.setParmValue(data); //���ݰ�
            table.retrieve();
        }
    }

    /**
     * check1ѡ���¼�
     */
    public void check1Selected() {
        TCheckBox check1 = (TCheckBox)this.getComponent("Check1");
        TCheckBox check2 = (TCheckBox)this.getComponent("Check2");
        check1.setSelected(true);
        check2.setSelected(false);
    }

    /**
     * check2ѡ���¼�
     */
    public void check2Selected() {
        TCheckBox check1 = (TCheckBox)this.getComponent("Check1");
        TCheckBox check2 = (TCheckBox)this.getComponent("Check2");
        check1.setSelected(false);
        check2.setSelected(true);
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (((TTable)this.getComponent("Table")).getRowCount() <= 0) {
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
        String colName = ""; //��¼���е��ֶ���
        String dataDate = ""; //����
        TParm printParm = new TParm();
        TParm printData = ((TTable)this.getComponent("Table")).getParmValue(); //��ȡ��ӡ����
        //����Ƿ���������,û�������д�ӡ�ľ��Ǳ�������
        if (printData.getValue("STA_DATE").trim().length() <= 0) {
            if (((TRadioButton)this.getComponent("B_Radio1")).isSelected()) { //��
                dataDate = this.getText("B_YEAR") + "��";
            } else if (((TRadioButton)this.getComponent("B_Radio2")).isSelected()) { //��
                dataDate = this.getText("B_MONTH").replace("/", "��") + "��";
            }
            //����������ʾ����
            printParm.setData("TableHeader", "TEXT", "��  ��");
            colName = "DEPT_DESC";
        } else { //��������
            if (((TRadioButton)this.getComponent("QS_1")).isSelected()) { //��
                dataDate = this.getText("QY1") + "�� ~ " + this.getText("QY2") +
                           "��";
            } else if (((TRadioButton)this.getComponent("QS_2")).isSelected()) { //��
                dataDate = this.getText("Q_Month1").replace("/", "��") + "�� ~ " +
                           this.getText("Q_Month2").replace("/", "��") + "��";
            }
            //����������ʾ����
            printParm.setData("TableHeader", "TEXT", "ʱ  ��");
            colName = "STA_DATE";
        }
        printData.setCount(printData.getCount("DEPT_DESC"));
        printData.addData("SYSTEM", "COLUMNS", colName);
        printData.addData("SYSTEM", "COLUMNS", "DATA_02");
        printData.addData("SYSTEM", "COLUMNS", "DATA_03");
        printData.addData("SYSTEM", "COLUMNS", "DATA_04");
        printData.addData("SYSTEM", "COLUMNS", "DATA_05");
        printData.addData("SYSTEM", "COLUMNS", "DATA_06");
        printData.addData("SYSTEM", "COLUMNS", "DATA_07");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08");
        printData.addData("SYSTEM", "COLUMNS", "DATA_09");
        printData.addData("SYSTEM", "COLUMNS", "DATA_10");
        printData.addData("SYSTEM", "COLUMNS", "DATA_11");
        printData.addData("SYSTEM", "COLUMNS", "DATA_12");
        printData.addData("SYSTEM", "COLUMNS", "DATA_13");
        printData.addData("SYSTEM", "COLUMNS", "DATA_14");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15");
        printData.addData("SYSTEM", "COLUMNS", "DATA_16");
        printData.addData("SYSTEM", "COLUMNS", "DATA_17");
        printData.addData("SYSTEM", "COLUMNS", "DATA_18");
        printData.addData("SYSTEM", "COLUMNS", "DATA_19");
        printData.addData("SYSTEM", "COLUMNS", "DATA_20");
        printData.addData("SYSTEM", "COLUMNS", "DATA_21");
        printData.addData("SYSTEM", "COLUMNS", "DATA_22");
        printData.addData("SYSTEM", "COLUMNS", "DATA_23");
        printData.addData("SYSTEM", "COLUMNS", "DATA_24");
        printData.addData("SYSTEM", "COLUMNS", "DATA_25");
        printData.addData("SYSTEM", "COLUMNS", "DATA_26");
        printData.addData("SYSTEM", "COLUMNS", "DATA_27");
        printData.addData("SYSTEM", "COLUMNS", "DATA_28");
        printData.addData("SYSTEM", "COLUMNS", "DATA_29");
        printData.addData("SYSTEM", "COLUMNS", "DATA_30");
        printData.addData("SYSTEM", "COLUMNS", "DATA_31");
        printData.addData("SYSTEM", "COLUMNS", "DATA_32");
        printData.addData("SYSTEM", "COLUMNS", "DATA_33");
        printData.addData("SYSTEM", "COLUMNS", "DATA_34");

        //������Ϣ
        TParm Basic = new TParm(); //������������
        Basic.setData("Date", dataDate); //��������
        //Basic.setData("unit", Operator.getHospitalCHNFullName()); //���λ
        Basic.setData("DeptDesc",
                      "��" + printData.getValue("DEPT_DESC", 0) + "��"); //ͳ�ƵĿ���
        printParm.setData("Basic", Basic.getData());
        printParm.setData("unit","TEXT", Operator.getHospitalCHNFullName());
        printParm.setData("T1", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_02.jhw",
                             printParm);
    }

    /**
     * ���Excel
     */
    public void onExcel() {
        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "ҽԺ�ż��﹤��ͳ��̨��");
    }
}