package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

import java.text.DecimalFormat;
import java.util.Date;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.Pat;

import java.util.ArrayList;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;

import jdo.bil.BIL;
import jdo.ekt.EKTTool;

/**
 *
 * <p>Title: 医疗卡交易记录控制类</p>
 *
 * <p>Description: 医疗卡交易记录控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangp 2011.12.19
 * @version 1.0
 */
public class EKTTredeRecordControl
    extends TControl {
    int selectRow = -1;
    boolean flg;
    TParm data;
    /**
     * zhangp 20121219 传入病案号
     */
    private TParm acceptData = new TParm(); //接参
    
    private String cardNo = "";
    
    private Pat pat;
    /**
     * 初始化
     */
    public void onInit() {
    	
    	TParm parm = new TParm();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
            cardNo = acceptData.getData("CARD_NO").toString();
            
            parm.setData("CARD_NO", cardNo);
            
        }
        super.onInit();
        initPage();
        onQuery();
    }

    /**
     * 初始化界面
     */
    public void initPage() {

    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    
    /**
     * 总金额table监听事件
     */
    public void onTREDETABLEClicked() {
    	TTable table = (TTable)this.callFunction("UI|TREDETABLE|getThis");
    	int row = table.getSelectedRow();
    	TParm p = new TParm();
    	p.setData("BUSINESS_NO", table.getValueAt(row, 1));
    	this.onQueryDetail(p);
    }
    /**
     * 获得sql
     * @return
     */
    private String getSQL() {
    	String state = this.getValue("STATE").toString();
    	String businessNo = this.getValue("BUSINESS_NO").toString();
    	String businessType = this.getValue("BUSINESS_TYPE").toString();
    	String tredeNo = this.getValue("TREDE_NO").toString();
    	StringBuilder sql = new StringBuilder();
    	String sql1 = "SELECT BUSINESS_NO,CARD_NO,MR_NO,CASE_NO,PAT_NAME,AMT,BUSINESS_TYPE,STATE,TREDE_NO FROM EKT_TREDE WHERE CARD_NO = "+"'"+cardNo+"'";
    	sql.append(sql1);
    	if (!"".equals(state)) {
    		sql.append(" AND STATE = "+"'"+state+"'");
    	}else{
    		sql.append(" AND STATE ='1' or STATE = '0'");
    	}
    	if (!"".equals(businessNo)) {
    		sql.append(" AND BUSINESS_NO = "+"'"+businessNo+"'");
    	}
    	if (!"".equals(businessType)) {
    		sql.append(" AND BUSINESS_TYPE = "+"'"+businessType+"'");
    	}
    	if (!"".equals(tredeNo)) {
    		sql.append(" AND TREDE_NO = "+"'"+tredeNo+"'");
    	}
    	return sql.toString();
    }

    /**
     * 查询
     */
    public void onQuery() {
//        TParm result = EKTTool.getInstance().selectEKTTredeTotal(parm);
//         //判断错误值
//        if (result.getErrCode() < 0) {
//            messageBox(result.getErrText());
//            return;
//        }
//        ((TTable)getComponent("TREDETABLE")).setParmValue(result);
    	String sql = getSQL();
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
          messageBox(result.getErrText());
          return;
        }
        ((TTable)getComponent("TREDETABLE")).setParmValue(result);
    }
    
    /**
     * 查询明细
     * 
     */
    public void onQueryDetail(TParm parm){
        TParm result = EKTTool.getInstance().selectEKTTradeDetail(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        ((TTable)getComponent("TREDETABLEDETAIL")).setParmValue(result);
    }
    
    /**
     * 打印报表
     */
    public void onPrint(){
    	// 打印的数据
		TParm data = new TParm();
		//需要打印的Table
		TTable table = (TTable) this.getComponent("TREDETABLEDETAIL");
		//当列表中不存在任何数据时,提示
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		// 表头数据
		data.setData("TITLE", "TEXT", "医疗卡交易记录");
		data.setData("DATE", "TEXT", "时间: "
				+ SystemTool.getInstance().getDate());
		pat = Pat.onQueryByMrNo(TypeTool.getString(table.getValueAt(0, 2)));
		data.setData("NAME", "TEXT", "制表人: " + pat.getName());
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		double sumOriginalBalance = 0.00;
		DecimalFormat df = new DecimalFormat("##########0.00");
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("BUSINESS_NO", tableParm.getValue("BUSINESS_NO", i));
			parm.addData("CARD_NO", tableParm.getValue("CARD_NO", i));
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
			double originalBalance = tableParm.getDouble("ORIGINAL_BALANCE", i);
			double businessAmt = tableParm.getDouble("BUSINESS_AMT", i);
			double currentBalance = tableParm.getDouble("CURRENT_BALANCE", i);
			parm.addData("ORIGINAL_BALANCE", df.format(originalBalance));
			parm.addData("BUSINESS_AMT", df.format(businessAmt));
			parm.addData("CURRENT_BALANCE", df.format(currentBalance));
		}
		parm.setCount(parm.getCount("BUSINESS_NO"));
		parm.addData("SYSTEM", "COLUMNS", "BUSINESS_NO");
		parm.addData("SYSTEM", "COLUMNS", "CARD_NO");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
		parm.addData("SYSTEM", "COLUMNS", "ORIGINAL_BALANCE");
		parm.addData("SYSTEM", "COLUMNS", "BUSINESS_AMT");
		parm.addData("SYSTEM", "COLUMNS", "CURRENT_BALANCE");
		//把表格数据添加进要打印的parm
		data.setData("TABLE", parm.getData());
		// 表尾数据
		data.setData("NAME", "TEXT", "持卡人："+pat.getName());
		// 调用打印方法,报表路径
		//========modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTredeRecord.jhw",data);
		//========modify by lim 2012/02/24 end

    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
    	 TTable table = (TTable) callFunction("UI|TREDETABLEDETAIL|getThis");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "医疗卡交易记录表");
    }
    
    /**
     * 清空
     */
    public void onClear() {
        String clear =
            "TREDE_NO;BUSINESS_NO;STATE;BUSINESS_TYPE";
        this.clearValue(clear);
    }

    
}
