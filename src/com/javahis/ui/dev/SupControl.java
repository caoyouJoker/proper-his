package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

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
public class SupControl extends TControl {
    /**
     * 动作类名称
     */
    private String actionName = "action.dev.DevAction";

    /**
     * table
     */
    private static String TABLE = "TABLE";
    /**
     * 权限
     */
    private String popedemType="1";
    /**
     * 初始化
     */
    public void onInit() {
        Object obj = this.getParameter();
        if(obj!=null){
            this.getTTable(TABLE).setParmValue((TParm)obj);
            popedemType = ((TParm)obj).getValue("POPEDEM");
        }
        callFunction("UI|" + TABLE + "|addEventListener",
                    TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");
    }
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    /**
     * 双击事件
     * @param row int
     */
    public void onTableDoubleClicked(int row){
        TParm rowParm = this.getTTable(TABLE).getParmValue().getRow(row);
        if(popedemType.equals("1")&&!rowParm.getBoolean("CHOOSE_FLG")){
            this.messageBox("您的权限无法选择未中标的厂商！");
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        if(popedemType.equals("2")&&!rowParm.getBoolean("CHOOSE_FLG")){
            if(this.messageBox("消息提示","此厂商未中标是否更新为中标状态！",this.YES_NO_OPTION)!=0){
                this.closeWindow();
                return;
            }
            if(!updateStart(rowParm)){
                this.closeWindow();
                return;
            }
            this.setReturnValue(rowParm);
            this.closeWindow();
            return;
        }
        this.setReturnValue(rowParm);
        this.closeWindow();
    }
    /**
     * 更新中标状态
     * @param parm TParm
     * @return boolean
     */
    public boolean updateStart(TParm parm){
        boolean falg = true;
        String[] sql =new String[]{"UPDATE DEV_NEGPRICE SET CHK_DATE=SYSDATE,CHK_USER='"+Operator.getID()+"',CHOOSE_FLG='Y' WHERE REQUEST_NO='"+parm.getValue("REQUEST_NO")+"' AND SUP_CODE='"+parm.getValue("SUP_CODE")+"'"};
        TParm sqlParm = new TParm();
        sqlParm.setData("SQL",sql);
        TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
        if (actionParm.getErrCode() < 0) {
            this.messageBox("更新失败！");
            falg = false;
        }
        this.messageBox("更新成功！");
        return falg;
    }
}
