package com.javahis.ui.reg;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MedStatistiscControl extends TControl {
    public void onInit(){
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("ST",StringTool.getTimestamp(dateStr,"yyyyMMdd"));
        this.setValue("EN",StringTool.getTimestamp(dateStr,"yyyyMMdd"));
        //======pangben modify 20110410 start
        setValue("REGION_CODE", Operator.getRegion());
        this.setValue("E_TIME",StringTool.getString(sysDate,"HH:mm:ss"));
        //======pangben modify 20110410 stop
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }
    /**
     * 导出EXECL
     */
    public void onExecl(){
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),"就诊分析");
    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 查询
     */
    public void onQuery(){
             //=========fuxin   modify  20120306
        String sql = "select g.REGION_CHN_ABN,c.dept_chn_desc, a.dr_code,e.user_name,b.mr_no,b.pat_name,"+
            " a. order_code,a.order_desc,"+
            " a.ORDER_DATE,A.BILL_DATE,a.PHA_CHECK_DATE,a.PHA_DOSAGE_DATE,a.PHA_DISPENSE_DATE,a.DECOCT_DATE,"+
            " d.RESERVED_DATE,d.REGISTER_DATE,d.INSPECT_DATE,d.REPORT_DATE,d.EXAMINE_DATE,f.ORDER_CAT1_DESC,"+
            " a.dosage_qty,a.OWN_PRICE,a.ar_amt,a.rx_no,a.ORDERSET_GROUP_NO,a.SETMAIN_FLG,a.hide_flg,D.PRINT_DATE"+
            " from opd_order a, sys_patinfo b ,sys_dept c ,med_apply d,sys_operator e,sys_order_cat1 f,sys_region g "+
            //=========pangben modify 20110412 修改查找时间格式
            " where a.order_date  between to_date('"+StringTool.getString((Timestamp)this.getValue("ST"),"yyyyMMdd")+"000000"+"','YYYYMMDDHH24MISS')   and to_date('"+StringTool.getString((Timestamp)this.getValue("EN"),"yyyyMMdd")+this.getValueString("E_TIME").replace(":","")+"','YYYYMMDDHH24MISS') "+
//            " and  a.hide_flg = 'N' "+
            " and a.mr_no =b.mr_no "+
            " and a.dept_code =c.dept_code "+
            " and a.dr_code =e.user_id "+
            " and a.med_apply_no =d.application_no(+) "+
            " AND A.RX_NO=D.ORDER_NO(+) "+
            " AND A.SEQ_NO=D.SEQ_NO(+) "+
             //================pangben modify 20110408 start
            " AND a.region_code=g.region_code "+
              //================pangben modify 20110408 stop
            " and a.order_cat1_code=f.order_cat1_code ";
        //================pangben modify 20110408 start
       if (this.getValueString("REGION_CODE").length() != 0)
           sql += " AND a.REGION_CODE= '" + this.getValue("REGION_CODE") + "' ";
       //================pangben modify 20110408 stop

        if(this.getValueString("ADM_TYPE").length()!=0){
            sql+=" AND A.ADM_TYPE='"+this.getValueString("ADM_TYPE")+"'";
        }
        if(this.getValueString("DEPT_CODE").length()!=0){
            sql+=" AND A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
        }
        if(this.getValueString("OPERATOR").length()!=0){
            sql+=" AND A.DR_CODE='"+this.getValueString("OPERATOR")+"'";
        }
            sql+=" order by a.dept_code,a.dr_code,a.mr_no,a.order_date,g.REGION_CHN_ABN ";//=====fuxin modify 20120306
      //      System.out.println("SQL==:"+sql);
        TParm parm = new TParm(getDBTool().select(sql));
        int rowCount = parm.getCount();
//        this.messageBox_(rowCount);
        for(int i=0;i<rowCount;i++){
            TParm temp = parm.getRow(i);
            if("Y".equals(temp.getValue("SETMAIN_FLG"))){
                double price = 0;
                String rxNo = temp.getValue("RX_NO");
                String orderSetGroupNo = temp.getValue("ORDERSET_GROUP_NO");
                for(int j=0;j<rowCount;j++){
                    TParm itemTemp = parm.getRow(j);
                    if((rxNo+orderSetGroupNo).equals(itemTemp.getValue("RX_NO")+itemTemp.getValue("ORDERSET_GROUP_NO"))){
//                        System.out.println(rxNo+orderSetGroupNo+"===="+(itemTemp.getValue("RX_NO")+itemTemp.getValue("ORDERSET_GROUP_NO")));
                        price+=itemTemp.getDouble("AR_AMT");
//                        System.out.println("price"+price);
                    }
                }
                parm.setData("AR_AMT",i,price);
                parm.setData("OWN_PRICE",i,price);
            }
        }
        for(int k=rowCount-1;k>=0;k--){
            if(parm.getBoolean("HIDE_FLG",k)){
                parm.removeRow(k);
            }
        }
        this.getTTable("TABLE").setParmValue(parm);
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("ADM_TYPE;DEPT_CODE;OPERATOR");
        //=============pangben modify 20110410
        setValue("REGION_CODE", Operator.getRegion());
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("ST",StringTool.getTimestamp(dateStr,"yyyyMMdd"));
        this.setValue("EN",sysDate);
        this.getTTable("TABLE").removeRowAll();
    }

}
