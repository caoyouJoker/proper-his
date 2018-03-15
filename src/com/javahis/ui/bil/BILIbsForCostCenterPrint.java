package com.javahis.ui.bil;

import java.util.Map;
import java.util.ArrayList;
import com.dongyang.jdo.TJDODBTool;
import java.util.HashMap;
import java.text.DecimalFormat;
import java.util.List;
import com.dongyang.data.TParm;
import java.util.Iterator;
import java.util.Vector;
import com.dongyang.util.StringTool;

/**
 * <p>Title: �ɱ�����Ʊ�ݶ�Ӧ</p>
 *
 * <p>Description: �ɱ�����Ʊ�ݶ�Ӧ</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2012.02.27
 * @version 1.0
 */
public class BILIbsForCostCenterPrint {
    /**
     * Ʊ��
     */
    private Map chargeMap = new HashMap();

    /**
     * ����Ʊ��
     * @param chargeMap Map
     */
    public void setChargeMap(Map chargeMap) {
        this.chargeMap = chargeMap;
    }

    /**
     * �õ�Ʊ��
     * @return Map
     */
    public Map getChargeMap() {
        return chargeMap;
    }

    /**
     * �վݷ�����ϸ
     */
    private List chargeList = new ArrayList();
    public static final String CHARGE =
            "CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07," +
            "CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14," +
            "CHARGE15, CHARGE16, CHARGE17, CHARGE18,CHARGE19,CHARGE20";

    /**
     * ���ؽ����
     */
    private TParm result = new TParm();
    /**
     * ������
     */
    public BILIbsForCostCenterPrint() {
        initCharge();
        initChargeList();
        initResult();

    }

    /**
     *��ʼ��chargeMap
     */
    public void initCharge() {
        TParm recpParm = getChargeData();
//        System.out.println("Ʊ��" + recpParm);
        Vector columns = (Vector) recpParm.getData("SYSTEM", "COLUMNS");
        for (int i = 0; i < columns.size(); i++) {
            String value = (String) columns.get(i);
            String name = recpParm.getValue(value, 0);
            getChargeMap().put(name, value);
        }
    }

    /**
     * ��ʼ��chargeList
     */
    public void initChargeList() {
        String[] a = StringTool.parseLine(CHARGE, ",");
        int count = a.length;
        for (int i = 0; i < count; i++) {
            chargeList.add(a[i]);
        }
    }

    /**
     * ��ʼ��result
     */
    public void initResult() {
        result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        int count = chargeList.size();

        for (int i = 0; i < count; i++) {
            result.addData("SYSTEM", "COLUMNS", chargeList.get(i));
        }
    }

    /**
     * ������ͷ��
     * @param recpCode String
     * @return String
     */
    public String getCharge(String recpCode) {
        return (String) getChargeMap().get(recpCode);

    }

    public void setResult(TParm result) {
        this.result = result;
    }

    public TParm getResult() {
        return result;
    }

    /**
     * ����
     * @param deptDesc String
     */
    public void addResult(String deptDesc) {
        result.addData("DEPT_CHN_DESC", deptDesc);
        Iterator deptIterator = getChargeMap().keySet().iterator();
        while (deptIterator.hasNext()) {
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            result.addData(value, 0.00);
        }
        result.addData("TOT_AMT", 0.00);
        result.setCount(result.getCount("DEPT_CHN_DESC"));

    }

    /**
     * ���ҿ��Ҷ�Ӧ�к�
     * @param deptDesc String
     * @return int
     */
    public int findDept(String deptDesc) {
        if (result.getCount() <= 0)
            return -1;
        for (int i = 0; i < result.getCount("DEPT_CHN_DESC"); i++) {
            if (result.getValue("DEPT_CHN_DESC", i).equalsIgnoreCase(deptDesc)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ��ÿ��charge��ֵ
     * @param deptDesc String
     * @param recpCode String
     * @param fee double
     */
    public void setValue(String deptDesc, String recpCode,
                         double fee) {
        int row = findDept(deptDesc);
        if (row == -1) {
            this.addResult(deptDesc);
            row = result.getCount() - 1;
        }
        String chargeCode = getCharge(recpCode);
        double value = result.getDouble(chargeCode, row) + fee;
        DecimalFormat df = new DecimalFormat("##########0.00");
        result.setData(chargeCode, row, df.format(value));
    }

    /**
     * �õ�charge����
     * @return TParm
     */
    public TParm getChargeData() {
        String recpSql =
                " SELECT " + CHARGE +
                "   FROM BIL_RECPPARM " +
                "  WHERE ADM_TYPE = 'I' ";
        return new TParm(TJDODBTool.getInstance().select(recpSql));

    }

    /**
     * ���кϼ�
     * @param row int
     */
    public void sumRowTot(int row) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        double totAmt = 0.00;
        Iterator deptIterator = getChargeMap().keySet().iterator();
        while (deptIterator.hasNext()) {
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            totAmt += result.getDouble(value, row);
        }
        result.setData("TOT_AMT", row, df.format(totAmt));

    }

    /**
     * ���кϼ�
     */
    public void sumTot() {
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            sumRowTot(i);
        }
    }

    /**
     * �ܼ�
     */
    public void allSumTot() {
        this.addResult("�ϼ�:");
        int count = result.getCount() - 1;
        Iterator deptIterator = getChargeMap().keySet().iterator();
        double arAmt = 0.00;
        DecimalFormat df = new DecimalFormat("##########0.00");
        while (deptIterator.hasNext()) {
            double totAmt = 0.00;
            String name = (String) deptIterator.next();
            String value = (String) getChargeMap().get(name);
            for (int i = 0; i < count; i++)
                totAmt += result.getDouble(value, i);
            result.setData(value, count, df.format(totAmt));
            arAmt = arAmt + totAmt;
        }
        result.setData("TOT_AMT", count, df.format(arAmt));
//        System.out.println("result"+result);

    }

    /**
     * �������ձ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm getValue(TParm parm) {
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String deptCode = parm.getValue("DEPT_CHN_DESC", i);
            String recpCode = parm.getValue("REXP_CODE", i);
            double totAmt = parm.getDouble("TOT_AMT", i);
            this.setValue(deptCode, recpCode, totAmt);
        }
        this.sumTot(); //���кϼ�
        this.allSumTot(); //�ܼ�
        return getResult();
    }
}