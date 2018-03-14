package com.javahis.manager;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 观察ODI_DSPNM表是否需要向ODI_DSPND中插数据</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:JAVAHIS</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0
 */

public class OdiDspnMObserver
    extends TObserverAdapter {
    public OdiDspnMObserver() {
        System.out.println("OdiDspnMObserver");
    }

    /**
     * 向ODI_DSPND中插入数据
     * @param ds TDS（ODI_DSPNM）
     * @param row int
     * @return int
     */
    public int insertRow(TParm origin, int row) {
        System.out.println("向ODI_DSPND中插入数据===>");
        if (this.getDS() == null)
            return 0;
        //目标TDS
        TDS dspnd = getDS();
        Timestamp time = (Timestamp)dspnd.getAttribute("EXE_DATE");
        //得到主表传过来的行数——转换为应该插入细表的行数
        int insertRows = origin.getCount();
        //展开的行数（从UDD来）
        int transInRows = insertRows + 9;
        //循环插入每一列数据
        for (int i = 0; i <= transInRows; i++) {
            //向ODI_DSPND中插入一个空行
            int newRow = dspnd.insertRow();

            dspnd.setItem(newRow, "CASE_NO", origin.getData("CASE_NO", row));
            dspnd.setItem(newRow, "ORDER_NO", origin.getData("ORDER_NO", row));
            dspnd.setItem(newRow, "ORDER_SEQ", origin.getData("ORDER_SEQ", row));
            dspnd.setItem(newRow, "ORDER_DATE", i+row + "");
            dspnd.setItem(newRow, "STATION_CODE",origin.getData("STATION_CODE", row));

            dspnd.setItem(newRow, "BATCH_CODE",origin.getData("BATCH_CODE", 0));
            dspnd.setItem(newRow, "TREAT_START_TIME",origin.getData("TREAT_START_TIME", row));
            dspnd.setItem(newRow, "TREAT_END_TIME",origin.getData("TREAT_END_TIME", row));
            dspnd.setItem(newRow, "NURSE_DISPENSE_FLG",origin.getData("NURSE_DISPENSE_FLG", row));
            dspnd.setItem(newRow, "BAR_CODE",origin.getData("BAR_CODE", row));

            dspnd.setItem(newRow, "ORDER_CODE",origin.getData("ORDER_CODE", row));
            dspnd.setItem(newRow, "DOSAGE_QTY",origin.getData("DOSAGE_QTY", row));
            dspnd.setItem(newRow, "DOSAGE_UNIT",origin.getData("DOSAGE_UNIT", row));
            dspnd.setItem(newRow, "TOT_AMT",origin.getData("TOT_AMT", row));
            dspnd.setItem(newRow, "DC_DATE",time);

            dspnd.setItem(newRow, "PHA_DISPENSE_NO",origin.getData("PHA_DISPENSE_NO", row));
            dspnd.setItem(newRow, "PHA_DOSAGE_CODE",origin.getData("PHA_DOSAGE_CODE", row));
            dspnd.setItem(newRow, "PHA_DOSAGE_DATE",time);
            dspnd.setItem(newRow, "PHA_DISPENSE_CODE",origin.getData("PHA_DISPENSE_CODE", row));
            dspnd.setItem(newRow, "PHA_DISPENSE_DATE",time);

            dspnd.setItem(newRow, "NS_EXEC_CODE",origin.getData("NS_EXEC_CODE", row));
            dspnd.setItem(newRow, "NS_EXEC_DATE",time);
            dspnd.setItem(newRow, "NS_EXEC_DC_CODE",origin.getData("NS_EXEC_DC_CODE", row));
            dspnd.setItem(newRow, "NS_EXEC_DC_DATE",time);
            dspnd.setItem(newRow, "NS_USER",origin.getData("NS_USER", row));

            dspnd.setItem(newRow, "EXEC_NOTE",origin.getData("EXEC_NOTE", row));
            dspnd.setItem(newRow, "EXEC_DEPT_CODE",origin.getData("EXEC_DEPT_CODE", row));
            dspnd.setItem(newRow, "BILL_FLG",origin.getData("BILL_FLG", row));
            dspnd.setItem(newRow, "CASHIER_CODE",origin.getData("CASHIER_CODE", row));
            dspnd.setItem(newRow, "CASHIER_DATE",time);

            dspnd.setItem(newRow, "PHA_RETN_CODE",origin.getData("PHA_RETN_CODE", row));
            dspnd.setItem(newRow, "PHA_RETN_DATE",time);
            dspnd.setItem(newRow, "TRANSMIT_RSN_CODE",origin.getData("TRANSMIT_RSN_CODE", row));
            dspnd.setItem(newRow, "STOPCHECK_USER",origin.getData("STOPCHECK_USER", row));
            dspnd.setItem(newRow, "STOPCHECK_DATE",time);

            dspnd.setItem(newRow, "IBS_CASE_NO",origin.getData("IBS_CASE_NO", row));
            dspnd.setItem(newRow, "IBS_CASE_NO_SEQ",origin.getData("IBS_CASE_NO_SEQ", row));
            dspnd.setItem(newRow, "OPT_DATE",time);
            dspnd.setItem(newRow, "OPT_USER", Operator.getID());
            dspnd.setItem(newRow, "OPT_TERM", Operator.getIP());

        }
        return 0;
    }

    /**
     * gen更新操作
     * @param dspnm TDS
     * @param row int
     * @param flg String 为“Y”是执行/“N”是取消执行
     * @return int
     */

    public int updateRow(TParm dspnm, int row, String flg, Timestamp now) {
        System.out.println("=dddd==>");
        if (this.getDS() == null)
            return 0;
        //目标TDS
        TDS dspnd = getDS();
        dspnd.showDebug();
        //得到主表传过来的行数——转换为应该插入细表的行数
        int insertRows = dspnm.getCount();
        //展开的行数（从UDD来）
        int transInRows = insertRows + 2;
        //循环插入每一列数据
        for (int i = 0; i <= transInRows; i++) {
            System.out.println("===>" + i);

            //执行的时候对ODI_DSPND的动作
            if (flg.equals("Y")) {
                dspnd.setItem(i, "NS_EXEC_CODE", Operator.getID());
                dspnd.setItem(i, "NS_EXEC_DATE",now);
            }
            else { //取消执行的时候对ODI_DSPND的动作
                dspnd.setItem(i, "NS_EXEC_CODE", null);
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
        }
        return 0;
    }

    /**
     * 插入数据的时候调用
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS ds, int row) {

        //得到需要插入多少行的参数，调用饿会的方法返回行号
        TParm orderPrm=(TParm) ds.getAttribute("m_parm");
        int modifyRow = (Integer)ds.getAttribute("m_row");

        //调用重载的插入方法
        insertRow(orderPrm,modifyRow);
        return 0;
    }

    /**
     * 删除数据
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds, int row) {

        return false;
    }

    /**
     * 根据列名字修改对应列的值
     * @param colNames String
     * @return boolean
     */
    public boolean updateDate(String colNames) {

        return false;
    }

    /**
     * 得到其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS odiOrder, TParm tParm, int row,
                                      String column) {

        //如果该列是数据库中本身没有的实际列
        //审核日期
        if ("NS_EXEC_DATE_DAY".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            if (date == null)
                return "";
            //给这个列一个值
            return StringTool.getString(date, "yyyy/MM/dd");
        }
        //审核时间
        if ("NS_EXEC_DATE_TIME".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            if (date == null)
                return "";
            return StringTool.getString(date, "HH:mm:ss");
        }
        //执行标记
        if ("EXE_FLG".equals(column)) {
            //获得护士审核时间
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            //当为空的时候说明没有审核，审核标记为‘N’
            if (date == null)
                return false;

            return true;
        }

        //药品名称+规格ORDER_DESC_AND_SPECIFICATION
        if ("ORDER_DESC_AND_SPECIFICATION".equals(column)) {
            //获得药品名称
            String desc = tParm.getValue("ORDER_DESC", row);
            //获得药品规格
            String specification = tParm.getValue("SPECIFICATION", row);

            return specification.equals("") ? desc :
                desc + "(" + specification + ")";

        }

        return "";
    }


    /**
     * 设置其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {

        //查看执行标记
        if ("EXE_FLG".equals(column)) {
            //Timestamp now=TJDODBTool.getInstance().getDBTime();
            //判断执行标记是否为真
            if (TCM_Transform.getBoolean(value)) {
                odiDspnm.setItem(row, "NS_EXEC_CODE", Operator.getID());
                Timestamp time = (Timestamp)odiDspnm.getAttribute("EXE_DATE");
                if(time == null)
                    time = TJDODBTool.getInstance().getDBTime();
                odiDspnm.setItem(row, "NS_EXEC_DATE",time);
                //在勾选的时候激发插入动作
                this.updateRow(parm, row, "Y",time);
            }
            else {
                odiDspnm.setItem(row, "NS_EXEC_CODE", null);
                odiDspnm.setItem(row, "NS_EXEC_DATE", null);
                this.updateRow(parm, row, "N",null);
            }
            return true;
        }

        return true;
    }
}
