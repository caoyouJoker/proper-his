package jdo.spc;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import jdo.bil.BIL;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 耗用记录 </p>
 *
 * <p>Description: 耗用记录 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wanglong 20140611
 * @version 1.0
 */
public class SPCINVRecord extends TDataStore {
    String caseNo;
    String businessNo=""; 
    /**
     * 查询事件
     * 
     * @return
     */
    public int onQuery() {
        this.setSQL("SELECT * FROM SPC_INV_RECORD ORDER BY BUSINESS_NO,SEQ_NO"); // 初始化SQL
        return 0;
    }

    /**
     * 根据BUSINESS_NO初始化对象
     * @param businessNo
     * @return
     */
    public int onQueryByBusinessNo(String businessNo) {
        if (StringUtil.isNullString(businessNo)) {
            return -1;
        }
        this.setSQL("SELECT * FROM SPC_INV_RECORD  WHERE BUSINESS_NO='#'".replaceFirst("#", businessNo));
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        return 0;
    }

    /**
     * 获得BUSINESS_NO对应数据的TParm
     * @param aciNo
     * @return
     */
    public TParm getDataParm() {
        if (this.rowCount() < 1) {
            return null;
        }
        TParm result = new TParm();
        TParm parm = this.getBuffer(this.PRIMARY);
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getBoolean("#ACTIVE#", i)) {
                result.addRowData(parm, i);
            }
        }
        return result;
    }
    
    /**
     * 初始化
     * @param parm
     */
    public boolean addRowData(TParm parm) {//System.out.println("---------------addRowData----------------"+parm);
//    this.showDebug();
//    String buffer = this.isFilter() ? this.FILTER : this.PRIMARY; 
        int row=this.insertRow();
       
        this.setActive(row, false);
        if (!this.getItemString(row, "CASE_NO").equals("")) {
            row = this.insertRow();
        }
        if(businessNo.equals("")){
            businessNo = SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO", "BUSINESS_NO");
        }

        this.setItem(row, "BUSINESS_NO", businessNo);/////////////////////////////

        this.setItem(row, "SEQ", getNextSeqNo());///////////
        this.setItem(row, "CASE_NO", parm.getData("CASE_NO"));
        this.setItem(row, "MR_NO", parm.getData("MR_NO"));
        this.setItem(row, "CLASS_CODE", parm.getData("CLASS_CODE"));
        this.setItem(row, "BAR_CODE", parm.getData("BAR_CODE"));
        this.setItem(row, "INV_CODE", parm.getData("INV_CODE"));
        this.setItem(row, "INV_DESC", parm.getData("INV_DESC"));
        this.setItem(row, "OPMED_CODE", parm.getData("OPMED_CODE"));
        this.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
        this.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
        this.setItem(row, "OWN_PRICE", parm.getData("OWN_PRICE"));///////////////
        this.setItem(row, "QTY", parm.getData("QTY"));//////////////////
        this.setItem(row, "UNIT_CODE", parm.getData("UNIT_CODE"));
        this.setItem(row, "AR_AMT", parm.getData("AR_AMT"));//////////////////////////////
        this.setItem(row, "BATCH_SEQ", parm.getData("BATCH_SEQ"));
        this.setItem(row, "VALID_DATE", parm.getData("VALID_DATE"));
        this.setItem(row, "BILL_FLG", parm.getData("BILL_FLG"));
        this.setItem(row, "BILL_DATE", parm.getData("BILL_DATE"));
        this.setItem(row, "NS_CODE", parm.getData("NS_CODE"));
        this.setItem(row, "OP_ROOM", parm.getData("OP_ROOM"));
        this.setItem(row, "DEPT_CODE", parm.getData("DEPT_CODE"));
        this.setItem(row, "EXE_DEPT_CODE", parm.getData("EXE_DEPT_CODE"));
        this.setItem(row, "ORDER_DEPT_CODE", parm.getData("ORDER_DEPT_CODE"));//wanglong add 20140826
        this.setItem(row, "ORDER_DR_CODE", parm.getData("ORDER_DR_CODE"));//wanglong add 20140826
        this.setItem(row, "CASE_NO_SEQ", parm.getData("CASE_NO_SEQ"));
        this.setItem(row, "SEQ_NO", parm.getData("SEQ_NO"));
        this.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
        this.setItem(row, "OPT_DATE", parm.getData("OPT_DATE"));
        this.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
        this.setItem(row, "YEAR_MONTH", parm.getData("YEAR_MONTH"));
        this.setItem(row, "RECLAIM_USER", parm.getData("RECLAIM_USER"));
        this.setItem(row, "RECLAIM_DATE", parm.getData("RECLAIM_DATE"));
        this.setItem(row, "REQUEST_NO", parm.getData("REQUEST_NO"));
        this.setItem(row, "PACK_BARCODE", parm.getData("PACK_BARCODE"));
        this.setItem(row, "STOCK_FLG", parm.getData("STOCK_FLG"));
        this.setItem(row, "COMMIT_FLG", parm.getData("COMMIT_FLG"));
        this.setItem(row, "CASHIER_CODE", parm.getData("CASHIER_CODE"));
        this.setItem(row, "PAT_NAME", parm.getData("PAT_NAME"));
        this.setItem(row, "PACK_GROUP_NO", parm.getInt("PACK_GROUP_NO"));//数字
        this.setItem(row, "USED_FLG", parm.getData("USED_FLG"));
        this.setItem(row, "CHECK_FLG", parm.getData("CHECK_FLG"));
        this.setItem(row, "CHECK_NO", parm.getData("CHECK_NO"));
        this.setItem(row, "SCAN_ORG_CODE", parm.getData("SCAN_ORG_CODE"));
        this.setItem(row, "PACK_DESC", parm.getData("PACK_DESC"));
        this.setItem(row, "CANCEL_FLG", parm.getData("CANCEL_FLG"));
        this.setItem(row, "RESET_BUSINESS_NO", parm.getData("RESET_BUSINESS_NO"));
        this.setItem(row, "RESET_SEQ_NO", parm.getData("RESET_SEQ_NO"));
        this.setItem(row, "ORDER_DATE", parm.getData("ORDER_DATE"));
        this.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
        
        this.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG"));// ////////////
        boolean isSetMain = parm.getBoolean("SETMAIN_FLG");//集合医嘱
        this.setItem(row, "ORDERSET_CODE", parm.getData("ORDERSET_CODE"));// ////////////
        this.setItem(row, "SCHD_CODE", parm.getData("SCHD_CODE"));//==pangben 2015-10-16
        String sql = // 查询集合医嘱细相
                "SELECT B.DOSAGE_QTY,A.*                          "
                        + "  FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE     "
                        + "   AND B.ORDERSET_CODE = '#' ";
        sql = sql.replace("#", parm.getValue("ORDER_CODE"));
        TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
        if (orderSet.getErrCode() != 0) {
            return false;
        }
        int count = orderSet.getCount("ORDER_CODE");
        int groupNo = getMaxGroupNo();
        if (count > 0 && isSetMain) {
            this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
        } else {
            this.setItem(row, "ORDERSET_GROUP_NO", 0);
        }
//        System.out.println("------isActive---------row------------"+this.isActive(row));
//        System.out.println("------setActive---------row------------"+row);
        try {
            this.setActive(row, true);  
        }
        catch (Exception e) {
          e.printStackTrace();
          this.showDebug();
        }
       
//        this.setActive(row, true,buffer);
        if (count > 0 && isSetMain) {
            for (int i = 0; i < count; i++) {
                row = this.insertRow();
                this.setItem(row, "BUSINESS_NO", businessNo);///////////////////////////////

                this.setItem(row, "SEQ", getNextSeqNo());/////////////
                this.setItem(row, "CASE_NO", parm.getData("CASE_NO"));
                this.setItem(row, "MR_NO", parm.getData("MR_NO"));
                this.setItem(row, "CLASS_CODE", parm.getData("CLASS_CODE"));
                this.setItem(row, "BAR_CODE", parm.getData("BAR_CODE"));
                this.setItem(row, "INV_CODE", parm.getData("INV_CODE"));
                this.setItem(row, "INV_DESC", parm.getData("INV_DESC"));
                this.setItem(row, "OPMED_CODE", parm.getData("OPMED_CODE"));
                this.setItem(row, "ORDER_CODE", orderSet.getData("ORDER_CODE",i));
                this.setItem(row, "ORDER_DESC", orderSet.getData("ORDER_DESC",i));
                this.setItem(row, "OWN_PRICE", orderSet.getDouble("OWN_PRICE",i));
                this.setItem(row, "QTY", orderSet.getDouble("DOSAGE_QTY",i));//从SYS_ORDERSETDETAIL取得数量，这里按集合医嘱数量为1算
                this.setItem(row, "UNIT_CODE", orderSet.getData("UNIT_CODE", i));
                this.setItem(row,
                             "AR_AMT",
                             StringTool.round(orderSet.getDouble("OWN_PRICE", i)
                                                      * orderSet.getDouble("DOSAGE_QTY", i), 2));
                this.setItem(row, "BATCH_SEQ", parm.getData("BATCH_SEQ"));
                this.setItem(row, "VALID_DATE", parm.getData("VALID_DATE"));
                this.setItem(row, "BILL_FLG", parm.getData("BILL_FLG"));
                this.setItem(row, "BILL_DATE", parm.getData("BILL_DATE"));
                this.setItem(row, "NS_CODE", parm.getData("NS_CODE"));
                this.setItem(row, "OP_ROOM", parm.getData("OP_ROOM"));
                this.setItem(row, "DEPT_CODE", parm.getData("DEPT_CODE"));
                this.setItem(row, "EXE_DEPT_CODE", parm.getData("EXE_DEPT_CODE"));
                this.setItem(row, "ORDER_DEPT_CODE", parm.getData("ORDER_DEPT_CODE"));//add by wangjc 20171226 保持与主项一致
                this.setItem(row, "ORDER_DR_CODE", parm.getData("ORDER_DR_CODE"));//add by wangjc 20171226 保持与主项一致
                this.setItem(row, "CASE_NO_SEQ", parm.getData("CASE_NO_SEQ"));
                this.setItem(row, "SEQ_NO", parm.getData("SEQ_NO"));
                this.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
                this.setItem(row, "OPT_DATE", parm.getData("OPT_DATE"));
                this.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
                this.setItem(row, "YEAR_MONTH", parm.getData("YEAR_MONTH"));
                this.setItem(row, "RECLAIM_USER", parm.getData("RECLAIM_USER"));
                this.setItem(row, "RECLAIM_DATE", parm.getData("RECLAIM_DATE"));
                this.setItem(row, "REQUEST_NO", parm.getData("REQUEST_NO"));
                this.setItem(row, "PACK_BARCODE", parm.getData("PACK_BARCODE"));
                this.setItem(row, "STOCK_FLG", parm.getData("STOCK_FLG"));
                this.setItem(row, "COMMIT_FLG", parm.getData("COMMIT_FLG"));
                this.setItem(row, "CASHIER_CODE", parm.getData("CASHIER_CODE"));
                this.setItem(row, "PAT_NAME", parm.getData("PAT_NAME"));
                this.setItem(row, "PACK_GROUP_NO", parm.getInt("PACK_GROUP_NO"));//数字
                this.setItem(row, "USED_FLG", parm.getData("USED_FLG"));
                this.setItem(row, "CHECK_FLG", parm.getData("CHECK_FLG"));
                this.setItem(row, "CHECK_NO", parm.getData("CHECK_NO"));
                this.setItem(row, "SCAN_ORG_CODE", parm.getData("SCAN_ORG_CODE"));
                this.setItem(row, "PACK_DESC", parm.getData("PACK_DESC"));
                this.setItem(row, "CANCEL_FLG", parm.getData("CANCEL_FLG"));
                this.setItem(row, "RESET_BUSINESS_NO", parm.getData("RESET_BUSINESS_NO"));
                this.setItem(row, "RESET_SEQ_NO", parm.getData("RESET_SEQ_NO"));
                this.setItem(row, "ORDER_DATE", parm.getData("ORDER_DATE"));
                this.setItem(row, "SPECIFICATION", orderSet.getData("SPECIFICATION", i));
                this.setItem(row, "SETMAIN_FLG", "N");// //////
                this.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));//和主项一致
                this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
                this.setItem(row, "SCHD_CODE", parm.getData("SCHD_CODE"));//==pangben 2015-10-16
                this.setActive(row, true);
//                this.setActive(row, true,buffer);
            }
        }
        return true;
    }
   
    /**
     * 清空事件
     */
    public void onClear() {
        this.filt("#");
    }
    
    /**
     * 根据给定的就诊序号过滤
     * @param caseNo
     * @return boolean
     */
    public boolean filt(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        this.caseNo = caseNo;
        this.setFilter("CASE_NO='" + caseNo + "' AND BILL_FLG='N' AND EXEC_FLG='N'");
        // boolean result=this.filter();
        filterObject(this, "filter1");
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            this.setActive(i, false);
        }
        // System.out.println("after filt");
        return true;
        // return result;
    }

    /**
     * 过滤方法
     * 
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter1(TParm parm, int row) {
        return caseNo.equals(parm.getValue("CASE_NO", row)) && !parm.getBoolean("BILL_FLG", row)
                && !parm.getBoolean("EXEC_FLG", row);
    }

    /**
     * 根据给定的就诊序号过滤
     * 
     * @param caseNo
     * @return boolean
     */
    public boolean filtCase(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        this.caseNo = caseNo;
        // this.setFilter("CASE_NO='" +caseNo+"' AND BILL_FLG='N' AND EXEC_FLG='N'");
        // boolean result=this.filter();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            this.setActive(i, false);
        }
        // System.out.println("after filt");
        return true;
    }


    /**
     * 从数据库查询取对应的最大GroupNo
     * @param caseNo   就诊号　
     * @return
     */
    public int getOrderMaxGroupNo(String caseNo) {// add by wanglong 20130321
       String sql = "SELECT NVL(MAX(ORDERSET_GROUP_NO),0) AS GROUP_NO FROM HRM_ORDER WHERE CASE_NO='"+caseNo+"'";
       TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
       int maxGroupNo = parm.getInt("GROUP_NO",0);
       return maxGroupNo ;
    }
    /**
     * 从数据库查询取对应的最大SEQ
     * @param caseNo   就诊号　
     * @return
     */
    public int getOrderMaxSeqNo(String caseNo){
       String sql = "SELECT NVL(MAX(SEQ_NO),0)+1 AS SEQ_NO FROM HRM_ORDER WHERE CASE_NO='"+caseNo+"'";
       TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
       int maxSeq = parm.getInt("SEQ_NO",0);
       return maxSeq ;
    }
    /**
     *
     * @param caseNo
     * @return
     */
    public int getMaxSeqNo(String caseNo) {
        TParm p = new TParm();
        if (isFilter()) p = getBuffer(FILTER);
        else p = getBuffer(PRIMARY);
        int count = p.getCount();
        int seq = 0;
        for (int i = 0; i < count; i++) {
            if (!caseNo.equals(p.getValue("CASE_NO", i))) continue;
            int x = p.getInt("SEQ_NO", i);
            if (seq < x) seq = x;
        }
        return seq + 1;
    }
    /**
     * 取得最大集合医嘱序号
     * @return
     */
    public int getMaxGroupNo() {
        String filter = this.getFilter();
        this.setFilter("SETMAIN_FLG='Y'");
        this.filter();
        int count = this.rowCount();
        int no = -1;
        for (int i = 0; i < count; i++) {
            int temp = this.getItemInt(i, "ORDERSET_GROUP_NO");
            if (temp > no) no = temp;
        }
        this.setFilter(filter);
        this.filter();
        return (no + 1);
    }
    
    /**
     * 填充SEQ_NO
     * @return
     */
    public boolean fillSeqNo() {
        int count = this.rowCount();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!this.isActive(i)) {
                continue;
            }
            this.setItem(i, "SEQ_NO", i + 1);
        }
        return true;
    }
 
    /**
     * 拿到最大SEQ_NO
     * @return int
     */
    public int getMaxSeq() {
        int seqNo = 0;
        String buff = this.isFilter() ? this.FILTER : this.PRIMARY;
        TParm parm = this.getBuffer(buff);
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            if (parm.getInt("SEQ_NO", i) > seqNo) seqNo = parm.getInt("SEQ_NO", i);
        }
        return seqNo + 1;
    }
    

    /**
     * 获得下一个SEQ_NO
     * @return
     */
    public int getNextSeqNo() {
        TParm buff = new TParm();
        if (isFilter()) {
            buff = this.getBuffer(FILTER);
        } else {
            buff = getBuffer(PRIMARY);
        }
        int count = buff.getCount();
        int seq = 0;
        for (int i = 0; i < count; i++) {
            int x = buff.getInt("SEQ", i);
            if (seq < x) seq = x;
        }
        return seq + 1;
    }
    
    
    /**
     * 取得总金额,没有过滤条件
     * @return
     */
    public double getTotArAmt() {
        TParm parm = this.getBuffer(this.FILTER);
        double arAmt = 0.0;
        for (int i = 0; i < parm.getCount(); i++) {
            arAmt += parm.getDouble("AR_AMT", i);
        }
        return arAmt;
    }
    
    /**
     * 取得总金额,没有过滤条件
     * @return
     */
    public double getArAmt(String comPayFlg) {
        double arAmt = 0.0;
        String filterString = this.getFilter();
        String caseNo = this.getItemString(0, "CASE_NO");
        this.setFilter("CASE_NO='" + caseNo + "' AND BILL_FLG='N'  AND NEW_FLG='N' ");
        this.filter();
        // System.out.println(filterString+"xueyf sub=="+this.getSQL() );
        // this.getUpdateSQL();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            String name = this.getItemString(i, "ORDER_DESC");
            arAmt += StringTool.round(this.getItemDouble(i, "DISPENSE_QTY") * this.getItemDouble(i, "OWN_PRICE"), 2);
            // arAmt+=this.getItemDouble(i, "AR_AMT");
            // System.out.println(arAmt+"xueyf AR_AMT==第"+i+"个》》》》》》》》》》"+this.getItemDouble(i, "AR_AMT")+"      "+ arAmt);
        }
        arAmt = StringTool.round(arAmt, 2);
        this.setFilter(filterString);
        this.filter();
        // System.out.println(arAmt+"xueyf sub11=="+this.getSQL() );
        // System.out.println("健检返回总价"+arAmt);
        return arAmt;
    }
    
    /**
     * 将caseNo为空的数据填写上case_no
     * @return
     */
    public boolean supplementCaseNo(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return false;
        }
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            String temp = this.getItemString(i, "CASE_NO");
            if ("#".equalsIgnoreCase(temp)) {
                this.setItem(i, "CASE_NO", caseNo);
            }
        }
        return true;
    }
    /**
     * 根据行号删除,判断是集合医嘱主项,将集合医嘱删除
     * @param row
     * @return
     */
    public boolean removeRow(int row) {
        // System.out.println("SETMAIN_FLG"+StringTool.getBoolean(this.getItemString(row, "SETMAIN_FLG")));
        if (StringTool.getBoolean(this.getItemString(row, "SETMAIN_FLG"))) {
            String filter = this.getFilter();
            // System.out.println("filter="+filter);
            String caseNo = this.getItemString(row, "CASE_NO");
            String orderCode = this.getItemString(row, "ORDER_CODE");
            this.setFilter("CASE_NO='" + caseNo + "' AND AND ORDERSET_CODE='" + orderCode + "'");
            this.filter();
            int count = this.rowCount() - 1;
            for (int i = count; i > -1; i--) {
                this.deleteRow(i);
            }
            this.setFilter(filter);
            this.filter();
        } else {
            this.deleteRow(row);
        }
        return true;
    }
    /**
     * 根据给入行号删除集合医嘱
     * @param row
     * @return
     */
    public boolean removeSetOrder(int row){
        if(row<0){
            // System.out.println("removeSetOrder.row<0");
            return false;
        }
        int count=this.rowCount();
        if(count<=0){
            // System.out.println("removeSetOrder.count<=0");
            return false;
        }
        if(!TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))){
            // System.out.println("removeSetOrder setmainFlg is false");
            return false;
        }
        String orderSetCode=this.getItemString(row, "ORDER_CODE");
        String medApplyNo=this.getItemString(row, "MED_APPLY_NO");
        int groupNo=this.getItemInt(row, "ORDERSET_GROUP_NO");
        String filterString =this.getFilter();
        this.setFilter("CASE_NO='" +caseNo+"' AND BILL_FLG='N'");
        this.filter();
        count=this.rowCount();
        for(int i=count-1;i>-1;i--){
            if(orderSetCode.equalsIgnoreCase(this.getItemString(i, "ORDERSET_CODE"))&&groupNo==this.getItemInt(i, "ORDERSET_GROUP_NO")){
                this.deleteRow(i);
            }
        }
        this.setFilter(filterString);
        this.filter();
        return true;
    }
 

    /**
     * 是否有新行
     * 
     * @return boolean
     */
    public int isNewRow() {
        int rowCount = this.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!this.isActive(i)) return i;
        }
        return -1;
    }

    /**
     * 得到其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
//        System.out.println("----------------getOtherColumnValue-------column-----"+column);
        if ("DESC".equalsIgnoreCase(column)) {
            if (parm.getValue("CLASS_CODE", row).equals("4")) {// 麻精
                return parm.getValue("ORDER_DESC", row);
            } else if (parm.getValue("CLASS_CODE", row).equals("1")) {// 药品
                return parm.getValue("ORDER_DESC", row);
            } else if (parm.getValue("CLASS_CODE", row).equals("3")) {// 高值
                return parm.getValue("INV_DESC", row);
            } else if (parm.getValue("CLASS_CODE", row).equals("2")) {// 低值
                if (!parm.getValue("INV_DESC", row).equals("")) {
                    return parm.getValue("INV_DESC", row);
                } else {
                    return parm.getValue("ORDER_DESC", row);
                }
            } else if (parm.getValue("CLASS_CODE", row).equals("5")) {// 手术费
                return parm.getValue("ORDER_DESC", row);
            }
        } else if ("HEXP_CODE".equalsIgnoreCase(column)) {
            return parm.getValue("ORDER_CODE", row);
        } else if ("HEXP_DESC".equalsIgnoreCase(column)) {
            return parm.getValue("ORDER_DESC", row);
        } else if ("OWN_PRICE_SET".equalsIgnoreCase(column)) {
            if (parm.getDouble("OWN_PRICE", row) != 0) {
                return parm.getDouble("OWN_PRICE", row);
            }
            double ownPrice = 0.0;
            String caseNo = parm.getValue("CASE_NO", row);
            int groupNo = parm.getInt("ORDERSET_GROUP_NO", row);
            double qty = parm.getDouble("QTY", row);//主项数量
            String filterString = this.getFilter();
            String filter =
                    "CASE_NO='" + caseNo + "' AND ORDERSET_CODE='"
                            + parm.getValue("ORDERSET_CODE", row) + "' AND ORDERSET_GROUP_NO="
                            + groupNo + " AND SETMAIN_FLG='N'";
            this.setFilter(filter);
            if (!this.filter()) {
                return ownPrice;
            }
            int count = this.rowCount();
            for (int i = 0; i < count; i++) {
                ownPrice += this.getItemDouble(i, "QTY") / qty * this.getItemDouble(i, "OWN_PRICE");
            }
            this.setFilter(filterString);
            this.filter();
            return StringTool.round(ownPrice, 2);
        } 
        else if ("AR_AMT_SET".equalsIgnoreCase(column)) {
            if (parm.getDouble("AR_AMT", row) != 0) {
                return parm.getDouble("AR_AMT", row);
            }
            double arAmt = 0.0;
            String caseNo = parm.getValue("CASE_NO", row);
            int groupNo = parm.getInt("ORDERSET_GROUP_NO", row);
            String filterString = this.getFilter();
            String filter =
                    "CASE_NO='" + caseNo + "' AND ORDERSET_CODE='"
                            + parm.getValue("ORDERSET_CODE", row) + "' AND ORDERSET_GROUP_NO="
                            + groupNo + " AND SETMAIN_FLG='N'";
            this.setFilter(filter);
            if (!this.filter()) {
                return arAmt;
            }
            int count = this.rowCount();
            for (int i = 0; i < count; i++) {
                arAmt += this.getItemDouble(i, "QTY") * this.getItemDouble(i, "OWN_PRICE");
            }
            this.setFilter(filterString);
            this.filter();
            return StringTool.round(arAmt, 2);
        }
//        else if ("BILL_FLG".equalsIgnoreCase(column)) {
//            if (parm.getValue("USED_FLG", row).equals("Y")) {
//                return parm.getValue("USED_FLG", row);
//            }
//        }
        return "";
    }


    /**
     * 根据给定行号删除医嘱，如果是集合医嘱，则删除细相
     * @param row
     * @return
     */
    public boolean onDeleteRow(int row) {
        int count = this.rowCount();
        if (row >= count) {
            return false;
        }
        if (!StringUtil.isNullString(this.getItemString(row, "EXEC_DR_CODE"))) {
            return false;
        }
        String caseNo = this.getItemString(row, "CASE_NO");
        String medApply = this.getItemString(row, "MED_APPLY_NO");
        if (!TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) { //不是集合医嘱
            return this.removeRow(row);
        }
        String ordersetCode = this.getItemString(row, "ORDER_CODE");
        for (int i = count - 1; i > -1; i--) {
            if (ordersetCode.equalsIgnoreCase(this.getItemString(i, "ORDERSET_CODE"))) {
                this.removeRow(i);
            }
        }
        return true;
    }
    /**
     * 根据下述条件过滤取得总价,并置BILL_FLG
     * @param caseNo
     * @param orderCode
     * @param seqNo
     * @return
     */
    public double getAmt(String caseNo, String orderCode, int groupNo, String billFlg, String billNo) {
        double amt = 0.0;
        if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(orderCode)) {
            // System.out.println("param is null");
            return amt;
        }
        String filter = this.getFilter();
        this.setFilter("CASE_NO='" + caseNo + "' AND ORDERSET_CODE='" + orderCode + "' AND ORDERSET_GROUP_NO=" + groupNo);
        this.filter();
        // this.showDebug();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            if (billFlg.equalsIgnoreCase(this.getItemString(i, "BILL_FLG"))) {
                continue;
            }
            if ("Y".equalsIgnoreCase(billFlg)) {
                this.setItem(i, "BILL_FLG", billFlg);
                this.setItem(i, "BILL_NO", billNo);
                this.setActive(i, true);
            } else {
                this.setItem(i, "BILL_FLG", billFlg);
                this.setItem(i, "BILL_NO", "");
                this.setActive(i, true);
            }
            amt += this.getItemDouble(i, "AR_AMT");
        }
        this.setFilter(filter);
        this.filter();
        return amt;
    }

    
    /**
     * 是否是数字
     * @return boolean
     */
    public boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

  
    
    /**
     * 去除数组中重复的记录    add by wanglong 20130131
     * @param a
     * @return
     */
    public String[] arrayUnique(String[] a) {
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * 除法向上取整    add by wanglong 20130131
     * @return
     */
    public int ceil(int a, int b) {
        return (double) a / (double) b > a / b ? a / b + 1 : a / b;
    }
    
    
}
