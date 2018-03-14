package com.javahis.ui.bil;

import java.sql.Timestamp;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.bil.BILInvoiceTool;
import jdo.sys.Operator;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:组长管理票号 </p>
 *
 * <p>Description:组长管理票号 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILInvoiceControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        //table的侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table值改变事件
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        this.onQuery();
    }

    /**
     *增加对Table点击的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        setValueForParm("RECP_TYPE;START_INVNO;END_INVNO;UPDATE_NO;CASHIER_CODE;START_VALID_DATE;END_VALID_DATE;STATUS",
                        data, row); //数据上翻
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }

    /**
     * 增加对Table值改变的监听
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        node.getTable().getParmValue().setData("FLG", node.getRow(),
                                               node.getValue());
//          System.out.println(node.getTable().getParmValue());
        //data.setData("FLG", node.getValue());
        //得到修改的行
        //node.getRow();
        //修改的新值
        //node.getValue();
        //callFunction("UI|delete|setEnabled", true);
    }

    /**
     * 查询数据
     */
    public void onQuery() {
        //得到查询参数
        TParm parm = this.getdata();
        //清空table
        this.callFunction("UI|TABLE|removeRowAll");
        data = new TParm();
        if(!"".equals(Operator.getRegion()))
            parm.setData("REGION_CODE",Operator.getRegion());
        //查询数据
        String where = "";
        String date_s = getValueString("START_VALID_DATE");
		String date_e = getValueString("END_VALID_DATE");
		if (date_s.length() > 0 ) {
			date_s = date_s.substring(0, 10).replace("-", "");
			where += " AND A.START_VALID_DATE>=TO_DATE('" + date_s + "','YYYYMMDDHH24MISS')";
		}
		if (date_e.length() > 0 ) {
			date_e = date_e.substring(0, 10).replace("-", "");
			where += " AND A.END_VALID_DATE<=TO_DATE('" + date_e + "','YYYYMMDDHH24MISS')";
		}
        if(parm.getValue("REGION_CODE").length() > 0){
        	where += " AND A.REGION_CODE = '" + parm.getValue("REGION_CODE") + "'";
        }
        if(parm.getValue("RECP_TYPE").length() > 0){
        	where += " AND A.RECP_TYPE = '" + parm.getValue("RECP_TYPE") + "'";
        }
        if(parm.getValue("CASHIER_CODE").length() > 0){
        	where += " AND A.CASHIER_CODE = '" + parm.getValue("CASHIER_CODE") + "'";
        }
        if(parm.getValue("STATUS").length() > 0){
        	where += " AND A.STATUS = '" + parm.getValue("STATUS") + "'";
        }
        String sql = 
        	" SELECT 'N' AS FLG,A.RECP_TYPE,A.START_INVNO,A.END_INVNO,A.UPDATE_NO, " +
        	" A.CASHIER_CODE,A.START_VALID_DATE,A.END_VALID_DATE,A.STATUS, " +
        	" A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'N' AS STATU,A.TERM_IP " +
        	" FROM BIL_INVOICE A,SYS_OPERATOR B  " +
        	" WHERE A.CASHIER_CODE = B.USER_ID " +
        	where +
        	" ORDER BY A.STATUS";
        data = new TParm(TJDODBTool.getInstance().select(sql));
//        data = BILInvoiceTool.getInstance().selDate(parm);
        //整理数据
        data = this.FinishData(data);
        //给table配参
        this.callFunction("UI|TABLE|setParmValue", data);
    }

    /**
     * 查询配参
     * @return TParm
     */
    public TParm getdata() {
        TParm parm = new TParm();
        String value = getValueString("RECP_TYPE");
        if (value.length() > 0)
            parm.setData("RECP_TYPE", value);
        value = getValueString("CASHIER_CODE");
        if (value.length() > 0)
            parm.setData("CASHIER_CODE", value);
        value = getValueString("STATUS");
        if (value.length() > 0)
            parm.setData("STATUS", value);
        return parm;
    }

    /**
     * 整理数据
     * @param parm TParm
     * @return TParm
     */
    public TParm FinishData(TParm parm) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData("STATUS", i).equals("2"))
                parm.setData("FLG", i, "Y");
        }
        return parm;
    }

    /**
     * 保存方法
     * 将会把交回的票确认交回
     */
    public void onSave() {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
        TParm parm = (TParm)this.callFunction("UI|TABLE|getParmValue");
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData("FLG", i).equals("Y")) {
                //01代表使用中
                if (parm.getData("STATUS", i).equals("1")) {
                    this.messageBox("正在使用,不能交回");
                    //========20120217 zhangp modify start
//                    break;
                    return;
                    //======20120217 zhangp modify end
                    
                }
//                     System.out.println(parm.getData("STATUS", i));
                TParm datat = new TParm();
                datat.setRowData( -1, parm, i);
                //3代表确认交回
                datat.setData("STATUS", "3");
                datat.setData("OPT_USER", Operator.getID());
                datat.setData("OPT_TERM", Operator.getIP());
                //调用更新方法//使用状态是确认交回
                TParm result = BILInvoiceTool.getInstance().updataData(datat);
                parm.setData("FLG", i, "N");
                if (result.getErrCode() < 0) {
                    this.messageBox("E0005"); //执行失败
                    return;
                }
            }
        }
        this.messageBox("P0005"); //执行成功
        this.callFunction("UI|TABLE|setParmValue", parm);
    }
    /**
     *领票
     */
    public void Recipients() {
        String value = getValueString("RECP_TYPE");
        if (value.length() == 0) {
            this.messageBox("E0012");
            return;
        }
        this.openDialog("%ROOT%\\config\\bil\\BILRecipients.x", value);
        //====zhangp 20120307 modify start
        onClear();
        //=====zhangp 20120307 modify end
        this.onQuery();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        clearValue("RECP_TYPE;START_INVNO;END_INVNO;UPDATE_NO;CASHIER_CODE;START_VALID_DATE;END_VALID_DATE;STATUS");
        selectrow = -1;
        callFunction("UI|TABLE|clearSelection");
    }
    /**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		 TTable table = (TTable) callFunction("UI|TABLE|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table,
					StringTool.getString(SystemTool.getInstance().getDate() , "yyyy.MM")
							+ "票据管理表");
	}
}
