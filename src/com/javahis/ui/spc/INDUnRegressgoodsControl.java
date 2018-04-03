package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.IndVerifyinDTool;
import jdo.spc.IndVerifyinMTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 验收未退货明细Control
 * </p>
 *
 * <p>
 * Description: 验收未退货明细Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.21
 * @version 1.0
 */

public class INDUnRegressgoodsControl
    extends TControl {

    // 传入参数
    private TParm parm;
    // 返回数据集合
    private TParm resultParm;

    private String org_code;

    private String sup_code;  

    public INDUnRegressgoodsControl() {
        super();
    }

    /**
     * 初始化方法  
     */
    public void onInit() {
        // 取得传入参数
        Object obj = getParameter();
 
        if (obj != null) {
            parm = (TParm) obj;
            org_code = parm.getValue("ORG_CODE");
            sup_code = parm.getValue("SUP_CODE");
        }
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
//        // 传入参数集
//        TParm parm = new TParm();
//        parm.setData("ORG_CODE", org_code);
//        parm.setData("SUP_CODE", sup_code);
//        if (!"".equals(getValueString("VERIFYIN_NO"))) {
//            parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
//        }
//        if (!"".equals(getValueString("ORDER_CODE"))) {
//            parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
//        }
//        if (!"".equals(getValueString("START_CHECK_DATE"))
//            && !"".equals(getValueString("END_CHECK_DATE"))) {
//            if (TypeTool.getTimestamp(getValue("START_CHECK_DATE")).compareTo(
//                TypeTool.getTimestamp(getValue("END_CHECK_DATE"))) > 0) {
//                this.messageBox("起始时间不可晚于结束时间");
//                return;
//            }
//            parm.setData("START_CHECK_DATE", getValue("START_CHECK_DATE"));
//            parm.setData("END_CHECK_DATE", getValue("END_CHECK_DATE"));
//        }
//        TParm result = new TParm();
//        
//        result = IndVerifyinDTool.getInstance().onQueryVerifyinDone(parm);
//        if (result.getCount() == 0) {
//            this.messageBox("查无资料");
//            return;
//        }
//         
//        TTable table = getTable("TABLE");
//        table.setParmValue(result);
//        resultParm = result;

    	
    	//fux modify 加入控制
        // 传入参数集
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("SUP_CODE", sup_code);
        if (!"".equals(getValueString("VERIFYIN_NO"))) {
            parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
        }
        if (!"".equals(getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
        }
        if (!"".equals(getValueString("START_CHECK_DATE"))
            && !"".equals(getValueString("END_CHECK_DATE"))) {
            if (TypeTool.getTimestamp(getValue("START_CHECK_DATE")).compareTo(
                TypeTool.getTimestamp(getValue("END_CHECK_DATE"))) > 0) {
                this.messageBox("起始时间不可晚于结束时间");
                return;
            }
            parm.setData("START_CHECK_DATE", getValue("START_CHECK_DATE"));
            parm.setData("END_CHECK_DATE", getValue("END_CHECK_DATE"));
        }
        TParm result = new TParm();
        
        String sql = " SELECT 'N' AS SELECT_FLG, B.VERIFYIN_NO, B.VERIFYIN_DATE, A.ORDER_CODE, C.SPECIFICATION,"+
       			     " A.BATCH_NO, A.VALID_DATE, A.VERIFYIN_QTY, A.GIFT_QTY, A.BILL_UNIT, "+
       			     " A.ACTUAL_QTY, A.UPDATE_FLG, C.ORDER_DESC , C.OWN_PRICE * D.DOSAGE_QTY AS RETAIL_PRICE, "+
       			     " A.SEQ_NO,A.BATCH_SEQ,A.VERIFYIN_PRICE,A.INVOICE_NO,A.INVOICE_DATE,A.SUP_ORDER_CODE "+
  		             " FROM IND_VERIFYIND A, IND_VERIFYINM B, SYS_FEE C, PHA_TRANSUNIT D ," +
  		             " (SELECT VERIFYIN_NO, VERSEQ_NO,SUM (QTY) AS QTY  FROM IND_REGRESSGOODSD GROUP BY VERIFYIN_NO, VERSEQ_NO) E "+
 		             " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO  "+
			         " AND A.VERIFYIN_NO = E.VERIFYIN_NO(+) "+            
			         " AND A.SEQ_NO = E.VERSEQ_NO(+)   "+                  
   			         " AND A.ORDER_CODE = C.ORDER_CODE "+         
   			         " AND (A.UPDATE_FLG = '1' OR A.UPDATE_FLG = '3') "+                           
			         " AND (A.VERIFYIN_QTY > E.QTY OR E.QTY IS　NULL) "+       
   			         " AND A.ORDER_CODE = D.ORDER_CODE "+                  
                     " AND C.ORDER_CODE = D.ORDER_CODE "+         
   			         " AND B.ORG_CODE= '"+org_code+"' "+         
   			         " AND B.SUP_CODE= '"+sup_code+"' " ;  
        if (!"".equals(getValueString("VERIFYIN_NO"))) {
        	sql = sql + " AND B.VERIFYIN_NO= '"+getValueString("VERIFYIN_NO")+"' ";
        }
        if (!"".equals(getValueString("ORDER_CODE"))) {
        	sql = sql + " AND A.ORDER_CODE= '"+getValueString("ORDER_CODE")+"' ";
        }
        
		String startTime = this.getText("START_CHECK_DATE");  
		String endTime = this.getText("END_CHECK_DATE");
		// 使用日期
		 
//		if (!"".equals(startTime) && !"".equals(endTime)) {  
//			startTime = startTime.substring(0, 10).replaceAll("/", "").trim();     
//			endTime = endTime.substring(0, 10).replaceAll("/", "").trim();  
//			sql += " AND F.BILL_DATE BETWEEN TO_DATE('"+startTime+"000000"+"','YYYYMMDDHH24MISS') " +
//					" AND TO_DATE('"+endTime+"235959"+"','YYYYMMDDHH24MISS')";  
//		}
         
        if (!"".equals(getValueString("START_CHECK_DATE"))) { 
        	startTime = startTime.substring(0, 10).replaceAll("/", "").trim();
        	sql = sql + " AND B.CHECK_DATE>= TO_DATE('"+startTime+"000000"+"','YYYYMMDDHH24MISS') "; 
        } 
        if (!"".equals(getValueString("END_CHECK_DATE"))) {  
        	endTime = endTime.substring(0, 10).replaceAll("/", "").trim(); 
        	sql = sql + " AND B.CHECK_DATE<= TO_DATE('"+endTime+"235959"+"','YYYYMMDDHH24MISS') ";
        }
        System.out.println("sql:"+sql);                
        result = new TParm(TJDODBTool.getInstance().select(sql));  
        //result = IndVerifyinDTool.getInstance().onQueryVerifyinDone(parm);
        if (result.getCount() == 0) {  
            this.messageBox("查无资料");  
            return;
        }                                    
         
        TTable table = getTable("TABLE");
        table.setParmValue(result);
        resultParm = result;
    
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr =
            "VERIFYIN_NO;ORDER_CODE;ORDER_DESC;SELECT_ALL;START_CHECK_DATE;"
            + "END_CHECK_DATE";
        this.clearValue(clearStr);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_CHECK_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_CHECK_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        getTable("TABLE").removeRowAll();
    }

    /**
     * 传回方法
     */
    public void onReturn() {
    	this.messageBox("ddd");
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        int j = table.getRowCount();
        TParm result = resultParm;
        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
                result.removeRow(i);
            }
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有传回数据");
            return;
        }
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 页面赋值
        setValue("SUP_CODE", sup_code);
        // 根据供应商和科室查询已完成验收单号
        TParm parm = IndVerifyinMTool.getInstance().onQueryDoneVerByOrgAndSup(
            org_code, sup_code);
        getComboBox("VERIFYIN_NO").setParmValue(parm);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_CHECK_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_CHECK_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        TParm parmIn = new TParm();
        parmIn.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE")
            .setPopupMenuParameter(
                "UD",
                getConfigParm().newConfig(
                    "%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);

        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        resultParm = new TParm();
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
