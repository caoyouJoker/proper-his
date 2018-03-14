package com.javahis.manager;

import com.javahis.util.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>Title: ODI_ORDER到ODI_DSPNM的管理者</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author WangM and ZangJH
 * @version 1.0
 */
public class OdiOrderObserver
    extends TObserverAdapter {
    /**
     * 护士站插入动作
     * @param odiOrder TDS
     * @param row int
     * @return int
     */
    public int insertRow(TParm parm, int row,Timestamp now) {
        System.out.println("===进入插入主表====>");
        //没有绑定表
        if (this.getDS() == null) {
            return 0;
        }
        //传进去D应该插入多少行需要的数据
        //调用ehui方法那到想要的D表中的数据

        //设置改变的数据
        this.getDS().setAttribute("m_parm",parm);
        //改变的行号
        this.getDS().setAttribute("m_row",row);
        //插入一行，得到行号
        int newRow = this.getDS().insertRow();
        //插入数据
        this.getDS().setItem(newRow, "CASE_NO", parm.getData("CASE_NO", row));
        this.getDS().setItem(newRow, "ORDER_NO", parm.getData("ORDER_NO", row));
        this.getDS().setItem(newRow, "ORDER_SEQ", parm.getData("ORDER_SEQ", row));
        this.getDS().setItem(newRow, "START_DTTM", "000001");
        this.getDS().setItem(newRow, "END_DTTM", "000009");


        this.getDS().setItem(newRow, "REGION_CODE",parm.getData("REGION_CODE", row));
        this.getDS().setItem(newRow, "STATION_CODE",parm.getData("REGION_CODE", row));
        this.getDS().setItem(newRow, "DEPT_CODE",parm.getData("DEPT_CODE", row));
        this.getDS().setItem(newRow, "VS_DR_CODE",parm.getData("VS_DR_CODE", row));
        this.getDS().setItem(newRow, "BED_NO",parm.getData("BED_NO", row));

        this.getDS().setItem(newRow, "IPD_NO",parm.getData("IPD_NO", row));
        this.getDS().setItem(newRow, "MR_NO",parm.getData("MR_NO", row));
        this.getDS().setItem(newRow, "DSPN_KIND",parm.getData("DSPN_KIND", row));
        this.getDS().setItem(newRow, "DSPN_DATE",now);
        this.getDS().setItem(newRow, "DSPN_USER",parm.getData("DSPN_USER", row));

        this.getDS().setItem(newRow, "DISPENSE_EFF_DATE",now);
        this.getDS().setItem(newRow, "DISPENSE_END_DATE",now);
        this.getDS().setItem(newRow, "EXEC_DEPT_CODE",parm.getData("EXEC_DEPT_CODE", row));
        this.getDS().setItem(newRow, "AGENCY_ORG_CODE",parm.getData("AGENCY_ORG_CODE", row));
        this.getDS().setItem(newRow, "PRESCRIPT_NO",parm.getData("PRESCRIPT_NO", row));

        this.getDS().setItem(newRow, "LINKMAIN_FLG",parm.getData("LINKMAIN_FLG", row));
        this.getDS().setItem(newRow, "LINK_NO",parm.getData("LINK_NO", row));
        this.getDS().setItem(newRow, "ORDER_CODE",parm.getData("ORDER_CODE", row));
        this.getDS().setItem(newRow, "ORDER_DESC",parm.getData("ORDER_DESC", row));
        this.getDS().setItem(newRow, "GOODS_DESC",parm.getData("GOODS_DESC", row));

        this.getDS().setItem(newRow, "SPECIFICATION",parm.getData("SPECIFICATION", row));
        this.getDS().setItem(newRow, "MEDI_QTY",parm.getData("MEDI_QTY", row));
        this.getDS().setItem(newRow, "MEDI_UNIT",parm.getData("MEDI_UNIT", row));
        this.getDS().setItem(newRow, "FREQ_CODE",parm.getData("FREQ_CODE", row));
        this.getDS().setItem(newRow, "ROUTE_CODE",parm.getData("ROUTE_CODE", row));

        this.getDS().setItem(newRow, "TAKE_DAYS",parm.getData("TAKE_DAYS", row));
        this.getDS().setItem(newRow, "DOSAGE_QTY",parm.getData("DOSAGE_QTY", row));
        this.getDS().setItem(newRow, "DOSAGE_UNIT",parm.getData("DOSAGE_UNIT", row));
        this.getDS().setItem(newRow, "DISPENSE_QTY",parm.getData("DISPENSE_QTY", row));
        this.getDS().setItem(newRow, "DISPENSE_UNIT",parm.getData("DISPENSE_UNIT", row));

        this.getDS().setItem(newRow, "GIVEBOX_FLG",parm.getData("GIVEBOX_FLG", row));
        this.getDS().setItem(newRow, "OWN_PRICE",parm.getData("OWN_PRICE", row));
        this.getDS().setItem(newRow, "NHI_PRICE",parm.getData("NHI_PRICE", row));
        this.getDS().setItem(newRow, "DISCOUNT_RATE",parm.getData("DISCOUNT_RATE", row));
        this.getDS().setItem(newRow, "OWN_AMT",parm.getData("OWN_AMT", row));

        this.getDS().setItem(newRow, "TOT_AMT",parm.getData("TOT_AMT", row));
        this.getDS().setItem(newRow, "ORDER_DATE",now);
        this.getDS().setItem(newRow, "ORDER_DEPT_CODE",parm.getData("ORDER_DEPT_CODE", row));
        this.getDS().setItem(newRow, "ORDER_DR_CODE",parm.getData("ORDER_DR_CODE", row));
        this.getDS().setItem(newRow, "DR_NOTE",parm.getData("DR_NOTE", row));

        this.getDS().setItem(newRow, "ATC_FLG",parm.getData("ATC_FLG", row));
        this.getDS().setItem(newRow, "SENDATC_FLG",parm.getData("SENDATC_FLG", row));
        this.getDS().setItem(newRow, "SENDATC_DTTM",now);
        this.getDS().setItem(newRow, "INJPRAC_GROUP",parm.getData("INJPRAC_GROUP", row));
        this.getDS().setItem(newRow, "DC_DATE",now);

        this.getDS().setItem(newRow, "DC_TOT",parm.getData("DC_TOT", row));
        this.getDS().setItem(newRow, "RTN_NO",parm.getData("RTN_NO", row));
        this.getDS().setItem(newRow, "RTN_NO_SEQ",parm.getData("RTN_NO_SEQ", row));
        this.getDS().setItem(newRow, "RTN_DOSAGE_QTY",parm.getData("RTN_DOSAGE_QTY", row));
        this.getDS().setItem(newRow, "RTN_DOSAGE_UNIT",parm.getData("RTN_DOSAGE_UNIT", row));

        this.getDS().setItem(newRow, "CANCEL_DOSAGE_QTY",parm.getData("CANCEL_DOSAGE_QTY", row));
        this.getDS().setItem(newRow, "CANCELRSN_CODE",parm.getData("CANCELRSN_CODE", row));
        this.getDS().setItem(newRow, "TRANSMIT_RSN_CODE",parm.getData("TRANSMIT_RSN_CODE", row));
        this.getDS().setItem(newRow, "PHA_RETN_CODE",parm.getData("PHA_RETN_CODE", row));
        this.getDS().setItem(newRow, "PHA_RETN_DATE",now);

        this.getDS().setItem(newRow, "PHA_CHECK_CODE",parm.getData("PHA_CHECK_CODE", row));
        this.getDS().setItem(newRow, "PHA_CHECK_DATE",now);
        this.getDS().setItem(newRow, "PHA_DISPENSE_NO",parm.getData("PHA_DISPENSE_NO", row));
        this.getDS().setItem(newRow, "PHA_DOSAGE_CODE",parm.getData("PHA_DOSAGE_CODE", row));
        this.getDS().setItem(newRow, "PHA_DOSAGE_DATE",now);

        this.getDS().setItem(newRow, "PHA_DISPENSE_CODE",parm.getData("PHA_DISPENSE_CODE", row));
        this.getDS().setItem(newRow, "PHA_DISPENSE_DATE",now);
        this.getDS().setItem(newRow, "NS_USER",parm.getData("NS_USER", row));
        this.getDS().setItem(newRow, "CTRLDRUGCLASS_CODE",parm.getData("CTRLDRUGCLASS_CODE", row));
        this.getDS().setItem(newRow, "PHA_TYPE",parm.getData("PHA_TYPE", row));

        this.getDS().setItem(newRow, "DOSE_TYPE",parm.getData("DOSE_TYPE", row));
        this.getDS().setItem(newRow, "DCTAGENT_CODE",parm.getData("DCTAGENT_CODE", row));
        this.getDS().setItem(newRow, "DCTEXCEP_CODE",parm.getData("DCTEXCEP_CODE", row));
        this.getDS().setItem(newRow, "DCT_TAKE_QTY",parm.getData("DCT_TAKE_QTY", row));
        this.getDS().setItem(newRow, "PACKAGE_AMT",parm.getData("PACKAGE_AMT", row));

        this.getDS().setItem(newRow, "DCTAGENT_FLG",parm.getData("DCTAGENT_FLG", row));
        this.getDS().setItem(newRow, "DECOCT_CODE",parm.getData("DECOCT_CODE", row));
        this.getDS().setItem(newRow, "URGENT_FLG",parm.getData("URGENT_FLG", row));
        this.getDS().setItem(newRow, "SETMAIN_FLG",parm.getData("SETMAIN_FLG", row));
        this.getDS().setItem(newRow, "ORDERSET_GROUP_NO",parm.getData("ORDERSET_GROUP_NO", row));

        this.getDS().setItem(newRow, "ORDERSET_CODE",parm.getData("ORDERSET_CODE", row));
        this.getDS().setItem(newRow, "RPTTYPE_CODE",parm.getData("RPTTYPE_CODE", row));
        this.getDS().setItem(newRow, "OPTITEM_CODE",parm.getData("OPTITEM_CODE", row));
        this.getDS().setItem(newRow, "HIDE_FLG",parm.getData("HIDE_FLG", row));
        this.getDS().setItem(newRow, "DEGREE_CODE",parm.getData("DEGREE_CODE", row));

        this.getDS().setItem(newRow, "BILL_FLG",parm.getData("BILL_FLG", row));
        this.getDS().setItem(newRow, "CASHIER_USER",parm.getData("CASHIER_USER", row));
        this.getDS().setItem(newRow, "CASHIER_DATE",now);
        this.getDS().setItem(newRow, "IBS_CASE_NO_SEQ",parm.getData("IBS_CASE_NO_SEQ", row));
        this.getDS().setItem(newRow, "IBS_SEQ_NO",parm.getData("IBS_SEQ_NO", row));

        this.getDS().setItem(newRow, "OPT_DATE",now);
        this.getDS().setItem(newRow, "OPT_USER", Operator.getID());
        this.getDS().setItem(newRow, "OPT_TERM", Operator.getIP());

        return 0;
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
        if ("NS_CHECK_DATE_DAY".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_CHECK_DATE", row);
            if (date == null)
                return "";
            //给这个列一个值
            return StringTool.getString(date, "yyyy/MM/dd");
        }
        //审核时间
        if ("NS_CHECK_DATE_TIME".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_CHECK_DATE", row);
            if (date == null)
                return "";
            return StringTool.getString(date, "HH:mm:ss");
        }
        //执行标记
        if ("EXE_FLG".equals(column)) {
            //获得护士审核时间
            Timestamp date = tParm.getTimestamp("NS_CHECK_DATE", row);
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


        if("PAT_NAME".equals(column)){
            return "aaaaaaaaa";
        }

        return "";
    }

    /**
     * 当非数据库表的列激发时调用
     * @param odiOrder TDS ODI_ORDER表
     * @param parm TParm 所有的数据TParm形式
     * @param row int 修改的行数
     * @param column String 被修改的那列名（假列）
     * @param value Object 假列的值
     * @return boolean
     */

    public boolean setOtherColumnValue(TDS odiOrder, TParm parm, int row,
                                       String column, Object value) {
        //查看执行标记
        if ("EXE_FLG".equals(column)) {
            //判断执行标记是否为真
            if (TCM_Transform.getBoolean(value)) {
                odiOrder.setItem(row, "NS_CHECK_CODE", Operator.getID());
                //获得大对象设置的时间
                Timestamp time = (Timestamp)odiOrder.getAttribute("EXE_DATE");
                System.out.println("time>>>>>>>>>>>>>>>>>>>>>>>>" + time);
                //如果时间为空说明不是执行的“全部执行”
                if(time == null)
                    time = TJDODBTool.getInstance().getDBTime();
                odiOrder.setItem(row, "NS_CHECK_DATE",time);
                //在勾选的时候激发插入动作
                this.insertRow(parm, row,time);
            }
            else {
                odiOrder.setItem(row, "NS_CHECK_CODE", null);
                odiOrder.setItem(row, "NS_CHECK_DATE", null);
            }
            return true;
        }
        return true;
    }

}
