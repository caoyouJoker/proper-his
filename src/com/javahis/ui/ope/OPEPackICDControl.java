package com.javahis.ui.ope;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Title: �����ײʹ��� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Bluecore </p>
 *
 * @author wanglong 20150225
 * @version 1.0
 */
public class OPEPackICDControl extends TControl {
    
    /**
     * ����ϸTABLE
     */
    private TTable mainTable,detailTable;
    /**
     * �������
     */
    private TParm parameter;
    boolean flag=true;

    /**
     * ��ʼ������
     */
    public void onInit() {
        this.parameter = new TParm();
        Object obj = this.getParameter();
        if (obj != null && obj.toString().length() != 0) {
            this.parameter = (TParm) obj;
        }
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        detailTable = (TTable) this.getComponent("DETAIL_TABLE");
        onQuery();
    }

    /**
     * ����table����¼�
     * 
     * @param row int
     */
    public void onMainTableClick(int row) {
        detailTable.acceptText();
        detailTable.clearSelection();
        TParm parm = mainTable.getParmValue().getRow(row);
        String packCode = parm.getValue("PACK_CODE");
        onQueryDetail(packCode);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String sql = "SELECT * FROM OPE_PACKM A ORDER BY A.PACK_DESC";
        TParm parm = new TParm(this.getDBTool().select(sql));
        mainTable.setParmValue(parm);
    }

    /**
     * ��ѯ�ײ�ϸ��
     * 
     * @param packCode
     *            String
     */
    public void onQueryDetail(String packCode) {
        String sql =
                "SELECT 'Y' AS FLG, A.* FROM OPE_PACKD A WHERE A.PACK_CODE = '" + packCode
                        + "' ORDER BY A.PACK_CODE, A.SEQ_NO";
        // System.out.println(sql);
        TParm parm = new TParm(this.getDBTool().select(sql));
        // System.out.println("======parm====="+parm);
        detailTable.setParmValue(parm);
    }

    /**
     * ȫѡ
     */
    public void onSelAll() {
        detailTable.acceptText();
        TParm parm = detailTable.getParmValue();
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            parm.setData("FLG", i, flag);
        }
        flag = !flag;
        detailTable.setParmValue(parm);
    }

    /**
     * ����
     */
    public void onReturn() {
        detailTable.acceptText();
        TParm parm = detailTable.getParmValue();
        List orderList = new ArrayList();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm temp = parm.getRow(i);
            if ("N".equals(temp.getValue("FLG"))) continue;
            orderList.add(temp);
        }
        this.parameter.runListener("INSERT_TABLE", orderList);
    }

    /**
     * �������ݿ��������
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
