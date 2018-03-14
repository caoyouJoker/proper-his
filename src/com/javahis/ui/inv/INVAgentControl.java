package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 物资供应厂商 </p>
 *    
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p> 
 *
 * @author zhangy
 * @version 1.0
 */
public class INVAgentControl extends TControl{
    public INVAgentControl() {
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("SUP_CODE", "");
        // 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBaseAgentPopup.x"),
            parm);                                                                
        // 定义接受返回值方法        
        getTextField("INV_CODE").addEventListener(   
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");  
        table = getTable("TABLE"); 
        //createNewRow();
        
    }  
    
//    /**  
//     * 新增细项数据行
//     * @return int
//     */
//    private int createNewRow() {
//        int row = table.addRow();
//        return row;
//    }


    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm == null){
            return;  
        }
        String inv_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(inv_code))
            getTextField("INV_CODE").setValue(inv_code);
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc))
            getTextField("INV_CHN_DESC").setValue(inv_desc);               
        if (!StringUtil.isNullString("STOCK_UNIT"))
            setValue("STOCK_UNIT", parm.getValue("STOCK_UNIT"));         
        if (!StringUtil.isNullString("PURCH_UNIT"))
            setValue("BILL_UNIT", parm.getValue("PURCH_UNIT"));
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("INV_CODE")) &&
            "".equals(this.getValueString("SUP_CODE"))) {
            this.messageBox("请选择查询项目");
            return;
        }
        table.removeRowAll();
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.  
            getInvAgentBySupOrInv(this.getValueString("SUP_CODE"),
                                  this.getValueString("INV_CODE"))));
//        System.out.println("sql:::"+INVSQL.
//            getInvAgentBySupOrInv(this.getValueString("SUP_CODE"),
//                                  this.getValueString("INV_CODE")));
        //System.out.println("parm=="+parm);
        if (parm.getCount() <= 0) {  
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        // INV_AGENT保存数据
        TParm parmAgent = new TParm();
        parmAgent = getParmAgent(parmAgent);
        TParm result = new TParm();
        if (table.getSelectedRow() < 0) {
        	messageBox("新增");
            // 新增数据                                                                         
            result = TIOM_AppServer.executeAction(
                "action.inv.INVAgentAction", "onInsert", parmAgent);
        }
        else {  
        	messageBox("更新");  
            // 更新数据 
            result = TIOM_AppServer.executeAction(
                "action.inv.INVAgentAction", "onUpdate", parmAgent);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        this.onClear();
    }

    /**
     * INV_AGENT保存数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getParmAgent(TParm parm) {
        parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        parm.setData("GIFT_RATE", this.getValueDouble("GIFT_RATE"));
        parm.setData("GIFT_QTY", this.getValueDouble("GIFT_QTY"));
        parm.setData("DISCOUNT_RATE", this.getValueDouble("DISCOUNT_RATE"));    
        parm.setData("CONTRACT_PRICE", this.getValueDouble("CONTRACT_PRICE"));        
        parm.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));               
        parm.setData("BILL_UNIT", this.getValueString("BILL_UNIT"));
        parm.setData("CONTRACT_NO", this.getValueString("CONTRACT_NO"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("供应商代码不能为空");
            return false;
        }
        if ("".equals(getValueString("INV_CODE"))) {
            this.messageBox("物资代码不能为空");
            return false;
        }
        if ("".equals(getValueString("INV_CHN_DESC"))) {
            this.messageBox("物资名称不正确");
            return false;
        }
        if (getValueDouble("CONTRACT_PRICE") <= 0) {
            this.messageBox("进货单价不能小于或等于0");
            return false;
        }
        return true;
    }


    /**
     * 删除方法
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("请选择删除项");
            return;
        }
        TParm parm = new TParm();
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVAgentAction", "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        this.messageBox("删除成功");
        this.onClear();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "SUP_CODE;INV_CODE;INV_CHN_DESC;GIFT_QTY;GIFT_RATE;"
            + "DISCOUNT_RATE;CONTRACT_NO;CONTRACT_PRICE;BILL_UNIT;STOCK_UNIT;"
            + "LAST_ORDER_DATE;LAST_ORDER_QTY;LAST_ORDER_PRICE;LAST_ORDER_NO;"
            + "LAST_VERIFY_DATE;LAST_VERIFY_PRICE";
        this.clearValue(clearStr);
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextField("INV_CODE").setEnabled(true);
        table.removeRowAll();
        this.setValue("DISCOUNT_RATE", 0);
    }

    /**
     * 表格(CLNDIAG_TABLE)单击事件
     */
    public void onTableClicked() {
    	
    	
        getTextFormat("SUP_CODE").setEnabled(false);
        getTextField("INV_CODE").setEnabled(false);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        
        String inv_code = parm.getValue("INV_CODE");
        
        String sql = " SELECT T.PURORDER_NO , T.PURORDER_DATE, T.PURORDER_QTY, T.PURORDER_PRICE " 
        	+ " FROM ( SELECT  M.PURORDER_NO , M.PURORDER_DATE, D.PURORDER_QTY, D.PURORDER_PRICE FROM INV_PURORDERD D LEFT JOIN INV_PURORDERM M ON D.PURORDER_NO = M.PURORDER_NO " 
        	+ " WHERE  D.INV_CODE = '" + inv_code + "' ORDER BY M.PURORDER_DATE DESC ) T " 
        	+ " WHERE ROWNUM = 1 ";
        
        TParm purParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        sql = " SELECT T.VERIFYIN_NO, T.VERIFYIN_DATE, T.QTY, T.UNIT_PRICE " 
        	+ " FROM ( SELECT  M.VERIFYIN_NO, M.VERIFYIN_DATE, D.QTY, D.UNIT_PRICE FROM INV_VERIFYIND D LEFT JOIN INV_VERIFYINM M ON D.VERIFYIN_NO = M.VERIFYIN_NO " 
        	+ " WHERE D.INV_CODE = '" + inv_code + "' ORDER BY M.VERIFYIN_DATE DESC ) T WHERE ROWNUM = 1 ";
        
        TParm verParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if(purParm.getCount("PURORDER_NO")>0){
        	parm.setData("LAST_ORDER_NO",  purParm.getValue("PURORDER_NO",0));
            parm.setData("LAST_ORDER_PRICE", purParm.getValue("PURORDER_PRICE",0));
            parm.setData("LAST_ORDER_QTY", purParm.getValue("PURORDER_QTY",0));
            parm.setData("LAST_ORDER_DATE", purParm.getValue("PURORDER_DATE",0).substring(0, 10).replaceAll("-", "/"));
        }
        if(verParm.getCount("VERIFYIN_NO")>0){
        	parm.setData("LAST_VERIFY_DATE",  verParm.getValue("VERIFYIN_DATE",0).substring(0, 10).replaceAll("-", "/"));
        	parm.setData("LAST_VERIFY_PRICE",  verParm.getValue("UNIT_PRICE",0));
        }
        
        String textStr = "SUP_CODE;INV_CODE;INV_CHN_DESC;GIFT_QTY;GIFT_RATE;"
            + "DISCOUNT_RATE;CONTRACT_NO;CONTRACT_PRICE;BILL_UNIT;STOCK_UNIT;"
            + "LAST_ORDER_DATE;LAST_ORDER_QTY;LAST_ORDER_PRICE;LAST_ORDER_NO;"
            + "LAST_VERIFY_DATE;LAST_VERIFY_PRICE";
        this.setValueForParm(textStr, parm);
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

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
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
}
