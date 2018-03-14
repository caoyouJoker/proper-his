package com.javahis.manager.inw;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.List;
import com.dongyang.util.TypeTool;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

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

public class OdiDspnDObserverDspnM
    extends TObserverAdapter {
    public OdiDspnDObserverDspnM() {
    }

    //存储order数据行号和M数据行号的对应关系(key:String value:list)
    Map orderRNToMRN = new HashMap();
    ArrayList MRNList = new ArrayList();

    /**
     * 向ODI_DSPND中插入数据（审核模块）
     * @param ds TDS（ODI_DSPNM）
     * @param row int
     * @return int
     */
    public int insertRow(TParm origin, int row, TParm toDspnd) {
//        System.out.println("===================");
//        System.out.println("调用D观察着的插入方法");
//        System.out.println("===================");


        if (this.getDS() == null)
            return 0;
        //目标TDS
        TDS dspnd = getDS();
        Timestamp time = (Timestamp) dspnd.getAttribute("EXE_DATE");
        if (time == null)
            time = TJDODBTool.getInstance().getDBTime();
        //展开的行数
        int transInRows = ( (List) toDspnd.getData("START_DTTM_LIST")).size();
        List startDttmList = (List) toDspnd.getData("START_DTTM_LIST");
        //循环插入每一列数据
        for (int i = 0; i < transInRows; i++) {
            //四个主键确定该数据是否已经存在（存在不插入行，只得到行号/不存在插入新行，得到行号）
            String caseNo=(String) origin.getData("CASE_NO");
//            System.out.println("=====caseNo======>"+caseNo);
            String orderNo=(String) origin.getData("ORDER_NO");
//            System.out.println("=====caseNo======>"+orderNo);
            String orderSeq=origin.getData("ORDER_SEQ").toString();
//            System.out.println("=====caseNo======>"+orderSeq);
            String orderDate = startDttmList.get(i).toString();//医嘱时间
//            System.out.println("=====caseNo======>"+orderDate);
            //当不存在的时候返回-1.存在返回行号
            int newRow=isExist(caseNo, orderNo, orderSeq, orderDate);
            if ( newRow== -1){
                //向ODI_DSPND中插入一个空行，不存在更新行号
                newRow = dspnd.insertRow();
            }
//            System.out.println("=行数1111111111111==>" + newRow);
            MRNList.add(newRow);
//            System.out.println("=行数1111111111111==>" + MRNList);

            dspnd.setItem(newRow, "CASE_NO", origin.getData("CASE_NO"));
            dspnd.setItem(newRow, "ORDER_NO", origin.getData("ORDER_NO"));
            dspnd.setItem(newRow, "ORDER_SEQ", origin.getData("ORDER_SEQ"));
            dspnd.setItem(newRow, "ORDER_DATE", orderDate);
            dspnd.setItem(newRow, "ORDER_DATETIME", time); //医嘱执行时间

//            dspnd.setItem(newRow, "STATION_CODE",
//                          origin.getData("STATION_CODE"));

            dspnd.setItem(newRow, "BATCH_CODE", origin.getData("BATCH_CODE"));
            dspnd.setItem(newRow, "TREAT_START_TIME",
                          origin.getData("TREAT_START_TIME"));
            dspnd.setItem(newRow, "TREAT_END_TIME",
                          origin.getData("TREAT_END_TIME"));
            dspnd.setItem(newRow, "NURSE_DISPENSE_FLG",
                          origin.getData("NURSE_DISPENSE_FLG"));
            dspnd.setItem(newRow, "BAR_CODE", origin.getData("BAR_CODE"));

            dspnd.setItem(newRow, "ORDER_CODE",
                          origin.getData("ORDER_CODE"));
            //UDD中来的数据
            dspnd.setItem(newRow, "MEDI_QTY",
                          toDspnd.getData("MEDI_QTY"));
            dspnd.setItem(newRow, "MEDI_UNIT",
                          toDspnd.getData("MEDI_UNIT"));
            dspnd.setItem(newRow, "DOSAGE_QTY",
                          toDspnd.getData("DOSAGE_QTY"));
            dspnd.setItem(newRow, "DOSAGE_UNIT",
                          toDspnd.getData("DOSAGE_UNIT"));

            dspnd.setItem(newRow, "TOT_AMT", origin.getData("TOT_AMT"));
            dspnd.setItem(newRow, "DC_DATE", origin.getData("DC_DATE"));
            dspnd.setItem(newRow, "PHA_DISPENSE_NO",
                          origin.getData("PHA_DISPENSE_NO"));
            dspnd.setItem(newRow, "PHA_DOSAGE_CODE",
                          origin.getData("PHA_DOSAGE_CODE"));
            dspnd.setItem(newRow, "PHA_DOSAGE_DATE",
                          origin.getData("PHA_DOSAGE_DATE"));

            dspnd.setItem(newRow, "PHA_DISPENSE_CODE",
                          origin.getData("PHA_DISPENSE_CODE"));
            dspnd.setItem(newRow, "PHA_DISPENSE_DATE",
                          origin.getData("PHA_DISPENSE_DATE"));
            dspnd.setItem(newRow, "NS_EXEC_CODE",
                          origin.getData("NS_EXEC_CODE"));
            dspnd.setItem(newRow, "NS_EXEC_DATE", origin.getData("NS_EXEC_DATE"));
            dspnd.setItem(newRow, "NS_EXEC_DC_CODE",
                          origin.getData("NS_EXEC_DC_CODE"));

            dspnd.setItem(newRow, "NS_EXEC_DC_DATE",
                          origin.getData("NS_EXEC_DC_DATE"));
            dspnd.setItem(newRow, "NS_USER", origin.getData("NS_USER"));
            dspnd.setItem(newRow, "EXEC_NOTE", origin.getData("EXEC_NOTE"));
            dspnd.setItem(newRow, "EXEC_DEPT_CODE",
                          origin.getData("EXEC_DEPT_CODE"));
            dspnd.setItem(newRow, "BILL_FLG", origin.getData("BILL_FLG"));

            dspnd.setItem(newRow, "CASHIER_CODE",
                          origin.getData("CASHIER_CODE"));
            dspnd.setItem(newRow, "CASHIER_DATE", origin.getData("CASHIER_DATE"));
            dspnd.setItem(newRow, "PHA_RETN_CODE",
                          origin.getData("PHA_RETN_CODE"));
            dspnd.setItem(newRow, "PHA_RETN_DATE",
                          origin.getData("PHA_RETN_DATE"));
            dspnd.setItem(newRow, "TRANSMIT_RSN_CODE",
                          origin.getData("TRANSMIT_RSN_CODE"));

            dspnd.setItem(newRow, "STOPCHECK_USER",
                          origin.getData("STOPCHECK_USER"));
            dspnd.setItem(newRow, "STOPCHECK_DATE",
                          origin.getData("STOPCHECK_DATE"));
            dspnd.setItem(newRow, "IBS_CASE_NO",
                          origin.getData("IBS_CASE_NO"));
            dspnd.setItem(newRow, "IBS_CASE_NO_SEQ",
                          origin.getData("IBS_CASE_NO_SEQ"));

            dspnd.setItem(newRow, "OPT_DATE", time);
            dspnd.setItem(newRow, "OPT_USER", Operator.getID());
            dspnd.setItem(newRow, "OPT_TERM", Operator.getIP());
        }

        //以order表的行号为KEY，M表的行号为VALUE
        orderRNToMRN.put(row,MRNList);
        return 0;
    }

    /**
     * 插入数据的时候调用（审核模块）
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS ds, int row) {
        //得到需要插入多少行的参数，调用EHUI的方法返回行号
        TParm orderPrm = (TParm) ds.getAttribute("m_parm");
        int modifyRow = (Integer) ds.getAttribute("m_row");
        TParm dapndData = (TParm) ds.getAttribute("m_dspndData");
        //调用重载的插入方法
        insertRow(orderPrm, modifyRow, dapndData);
        return 0;
    }


    /**
     * 更新操作(执行模块)
     * @param dspnm TDS
     * @param row int
     * @param flg String 为“Y”是执行/“N”是取消执行
     * @return int
     */

    public int updateRow(TParm dspnm, int row, String flg, Timestamp now) {
//        System.out.println("=执行---》更新字段==");
        if (this.getDS() == null)
            return 0;
        //目标TDS
        TDS dspnd = getDS();
        int count = dspnd.rowCount();

        //循环插入每一列数据
        for (int i = 0; i < count; i++) {

            //执行的时候对ODI_DSPND的动作
            if (flg.equals("Y")) {
                dspnd.setItem(i, "NS_EXEC_CODE", Operator.getID());
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
            else { //取消执行的时候对ODI_DSPND的动作
//                System.out.println("===>取消执行");
                dspnd.setItem(i, "NS_EXEC_CODE", null);
                dspnd.setItem(i, "NS_EXEC_DATE", null);
            }
        }
        return 0;
    }


    /**
     * 删除数据
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds, int row) {

//        System.out.println("wwwwwwwwwwwwwwwwwww");
//        System.out.println("调用D观察着的删除方法");
//        System.out.println("wwwwwwwwwwwwwwwwwww");
        if(ds.getAttribute("MRowNumber")==null){
//            System.out.println("=========空空=======>");
            return false;
        }
//        System.out.println("fffffffffffffffff>>>>>>" + ds.getAttribute("MRowNumber"));
        int MRowNumber=(Integer) ds.getAttribute("MRowNumber");
        ArrayList mRows = (ArrayList) orderRNToMRN.get(MRowNumber);
        //当时空的时候相当于M表中没有插入行
        if (mRows != null) {
//            System.out.println("D的行数===>" + mRows.toString());
            //得到该行order数据对应的M表中的行数
            int count = mRows.size();
            for (int i = 0; i < count; i++) {
                int rowNumber = (Integer) mRows.get(i);
                this.getDS().setActive(rowNumber, false); //该条M表数据ACTIVE是false
            }
        }

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

    public boolean setItem(TDS ds, int row, String column, Object value) {
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
     * 判断该条医嘱是否已经存在，如果存在则不再新插入（以免主键重复）
     * @return boolean 四主键确定唯一数据（ODI_DSPND）
     */
    private int isExist(String caseNo,String orderNo,String orderSeq,String orderDate){
//        System.out.println("======888888888888888888888====>"+caseNo+"  "+orderNo+"  "+orderSeq+"  "+orderDate);
        int row=-1;

        //得到当前的TDS
        TParm dspnD_P=this.getDS().getBuffer(TDS.PRIMARY);
        int count=dspnD_P.getCount();
        if(count<=0)
            return -1;
        Vector caseNo_V=(Vector) dspnD_P.getData("CASE_NO");
        Vector orderNo_V=(Vector) dspnD_P.getData("ORDER_NO");
        Vector orderSeq_V=(Vector) dspnD_P.getData("ORDER_SEQ");
        Vector orderDate_V=(Vector) dspnD_P.getData("ORDER_DATE");
//        System.out.println("=====>"+caseNo_V);
//        System.out.println("=====>"+orderNo_V);
//        System.out.println("=====>"+orderSeq_V);
//        System.out.println("=====>"+orderDate_V);
        for(int i=0;i<count;i++){
//            System.out.println("==111111111======>"+caseNo);
            if(!caseNo.equals(caseNo_V.get(i))){
//                System.out.println("==222222222======>"+caseNo);
                continue;
            }
            if(!orderNo.equals(orderNo_V.get(i))){
//                System.out.println("==333333333======>"+orderNo);
                continue;
            }
            if(!orderSeq.equals(orderSeq_V.get(i).toString())){
//                System.out.println("==44444444444======>"+orderSeq);
                continue;
            }
            if(!orderDate.equals(orderDate_V.get(i))){
//                System.out.println("==555555555======>"+orderDate);
                continue;
            }
            row=i;
        }
//        System.out.println("======888888888888888888888====>"+row);
        return row;
    }


    /**
     * 设置其他列数据（执行模块）
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean 该列目前的值
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {

        //查看执行标记
        if ("EXE_FLG".equals(column)) {
            //判断执行标记是否为真
            if (TypeTool.getBoolean(value)) {

                odiDspnm.setItem(row, "NS_EXEC_CODE", Operator.getID());
                Timestamp time = (Timestamp) odiDspnm.getAttribute("EXE_DATE");
                if (time == null)
                    time = TJDODBTool.getInstance().getDBTime();
                odiDspnm.setItem(row, "NS_EXEC_DATE", time);
                //在勾选的时候激发插入动作
                this.updateRow(parm, row, "Y", time);

            }
            else {
                odiDspnm.setItem(row, "NS_EXEC_CODE", null);
                odiDspnm.setItem(row, "NS_EXEC_DATE", null);
                this.updateRow(parm, row, "N", null);
            }

            return true;
        }

        return true;
    }
}
