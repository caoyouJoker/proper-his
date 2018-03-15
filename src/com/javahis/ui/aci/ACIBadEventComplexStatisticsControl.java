package com.javahis.ui.aci;

import java.sql.Timestamp;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import jdo.aci.ACIBadEventTool;
import jdo.sys.SystemTool;

/**
 * <p> Title: �����¼�����ͳ�� </p>
 * 
 * <p> Description: �����¼�����ͳ�� </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author WangLong 2012.12.24
 * @version 1.0
 */
public class ACIBadEventComplexStatisticsControl extends TControl {
    TTable table;// ���
    TRadioButton year;// �꣨��ѡ��ť��
    TRadioButton month;// �£���ѡ��ť��
    TRadioButton day;// �գ���ѡ��ť��
    TTextFormat date;// ʱ��
    TRadioButton deptAndSac;// �ϱ�����/SAC�ּ�����ѡ��ť��
    TRadioButton deptAndType;// �ϱ�����/�¼����ࣨ��ѡ��ť��
    TRadioButton sacAndType;// SAC�ּ�/�¼����ࣨ��ѡ��ť��
    TComboBox level;// �¼����༶����������
    private String dateType = "";// ��������
    private String queryType = "";// ��ѯ����
    private String typeLevel = "";// ���༶��

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        year = (TRadioButton) this.getComponent("YEAR");
        month = (TRadioButton) this.getComponent("MONTH");
        day = (TRadioButton) this.getComponent("DAY");
        date = (TTextFormat) this.getComponent("DATE");
        deptAndSac = (TRadioButton) this.getComponent("DEPT_AND_SAC");
        deptAndType = (TRadioButton) this.getComponent("DEPT_AND_TYPE");
        sacAndType = (TRadioButton) this.getComponent("SAC_AND_TYPE");
        level = (TComboBox) this.getComponent("LEVEL");
        initUI();
    }

    /**
     * ��ʼ��������Ϣ
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        deptAndSac.setSelected(true);
        onChooseDeptAndSac();
        level.setSelectedIndex(0);
        onChooseLevel();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("DATE_TYPE", dateType);
        Timestamp startDate = (Timestamp) this.getValue("DATE");
        if(dateType.equals("YEAR")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy"));
        } else if(dateType.equals("MONTH")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy/MM"));
        } else if(dateType.equals("DAY")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy/MM/dd"));
        }
        parm.setData("LEVEL", typeLevel);
        //add by wukai on 20160908 start ��� ���ο��� �� �ϱ����Ų�ѯ����
        if(!StringUtils.isEmpty(this.getText("DEPT_IN_CHARGE"))) {
        	parm.setData("DEPT_IN_CHARGE", this.getValueString("DEPT_IN_CHARGE"));
        }
        if(!StringUtils.isEmpty(this.getText("REPORT_SECTION"))) {
        	 parm.setData("REPORT_SECTION", this.getValueString("REPORT_SECTION"));
        }
        //System.out.println("parm :::::::::::  " + parm);
        //add by wukai on 20160908 end ��� ���ο��� �� �ϱ����Ų�ѯ����
        TParm result = new TParm();
        if(queryType.equals("DEPT_AND_SAC")) {// �����ϱ�����/SAC�ּ���
            result = ACIBadEventTool.getInstance().selectStatisticByDeptAndSac(parm);
        } else if(queryType.equals("DEPT_AND_TYPE")) {// �����ϱ�����/�¼����ࡱ
            result = ACIBadEventTool.getInstance().selectStatisticByDeptAndType(parm);
        } else if(queryType.equals("SAC_AND_TYPE")) {// ����SAC�ּ�/�¼����ࡱ
            result = ACIBadEventTool.getInstance().selectStatisticBySacAndType(parm);
        }
        if(result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
        }
        table.setDSValue();
        table.setHeader(result.getValue("TABLE_HEADER"));
        table.setParmMap(result.getValue("TABLE_PARMMAP"));
        table.setParmValue(result);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        Timestamp startDate = (Timestamp) this.getValue("DATE");
        String dateStr = "";
        if (dateType.equals("YEAR")) {
            dateStr = StringTool.getString(startDate, "yyyy");
        } else if (dateType.equals("MONTH")) {
            dateStr = StringTool.getString(startDate, "yyyy-MM");
        } else if (dateType.equals("DAY")) {
            dateStr = StringTool.getString(startDate, "yyyy-MM-dd");
        }
        if (table.getRowCount() > 0) ExportExcelUtil.getInstance().exportExcel(table, "�����¼�����ͳ��");
    }

    /**
     * ���
     */
    public void onClear() {
        callFunction("UI|TABLE|setParmValue", new TParm());
        this.clearValue("DEPT_IN_CHARGE;REPORT_SECTION");
        initUI();
    }

    /**
     * ѡ���ꡱ
     */
    public void onChooseYear() {
        dateType = "YEAR";
        date.setFormat("yyyy");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * ѡ���¡�
     */
    public void onChooseMonth() {
        dateType = "MONTH";
        date.setFormat("yyyy/MM");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * ѡ���ա�
     */
    public void onChooseDay() {
        dateType = "DAY";
        date.setFormat("yyyy/MM/dd");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * ѡ���ϱ�����/SAC�ּ���
     */
    public void onChooseDeptAndSac() {
        queryType = "DEPT_AND_SAC";
        level.setEnabled(false);
    }

    /**
     * ѡ���ϱ�����/�¼����ࡱ
     */
    public void onChooseDeptAndType() {
        queryType = "DEPT_AND_TYPE";
        level.setEnabled(true);
    }

    /**
     * ѡ��SAC�ּ�/�¼����ࡱ
     */
    public void onChooseSacAndType() {
        queryType = "SAC_AND_TYPE";
        level.setEnabled(true);
    }
    
    /**
     * ѡ���¼����༶��
     */
    public void onChooseLevel() {
        typeLevel = this.getValueString("LEVEL");
    }
}
