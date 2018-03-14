package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.NoTool;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TFrame;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.MessageTool;
import com.dongyang.util.TSystem;
import com.dongyang.config.TConfigParm;
import javax.swing.UIManager;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>Title: 取号原则主档
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis
 *
 * @author ZangJH 2008.08.28
 * @version 1.0
 */

public class SYSNoControl
    extends TControl {
    //结果数据
    private TParm data;
    //选中的行
    private int selectRow;


    public SYSNoControl() {
        selectRow = -1;
    }

    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        myInit();
    }

    public void onTableClicked(int row) {
        callFunction("UI|DELETE|setEnabled", true);
        if (row < 0) {
            return;
        }
        else {
            setValueForParm("REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION;STATUS_METHOD_CODE;START_NO;MAX_NO;NO_LENGTH;NO_LENGTH;NO_DESC;NO_FORMAT;CURR_NO",
                            data, row);
            selectRow = row;
            callFunction("UI|REGION_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|SYSTEM_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|OPERATION|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|SUB1_OPERATION|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            return;
        }
    }

    public void onQuery() {
        TParm selectCondition = getParmForTag(
            "REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION", true);
        //直接调用tool类来查询相关数据
        data = NoTool.getInstance().selectdata(selectCondition);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        else {
            callFunction("UI|Table|setParmValue", new Object[] {
                         data
            });
            return;
        }
    }

    public void onInsert() {
        if (!emptyTextCheck("REGION_CODE,SYSTEM_CODE,OPERATION,SUB1_OPERATION,STATUS_METHOD_CODE,CURR_NO,NO_LENGTH,START_NO,MAX_NO"))
            return;
        TParm parm = getParmForTag("REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION;STATUS_METHOD_CODE;CURR_NO;NO_LENGTH;START_NO;MAX_NO;NO_DESC;NO_FORMAT");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = NoTool.getInstance().insertdata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        else {
            messageBox("P0002");
            int rowNumber = ( (Integer) callFunction("UI|TABLE|addRow",
                new Object[] {
                parm
            })).intValue();
            callFunction("UI|Table|setSelectedRow", new Object[] {
                         Integer.valueOf(rowNumber)
            });
            data.setRowData(rowNumber, parm);
            return;
        }
    }

    public void onUpdate() {
        TParm parm = getParmForTag("REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION;STATUS_METHOD_CODE;CURR_NO;NO_LENGTH;START_NO;MAX_NO;NO_DESC;NO_FORMAT");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = NoTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        int row = ( (Integer) callFunction("UI|Table|getSelectedRow",
                                           new Object[0])).intValue();
        if (row < 0) {
            return;
        }
        else {
            data.setRowData(row, parm);
            callFunction("UI|Table|setRowParmValue", new Object[] {
                         Integer.valueOf(row), data
            });
            messageBox("P0005");
            return;
        }
    }

    public void onSave() {
        //检查最大号长度是否符合长度
        if(!checkLength()){
            this.messageBox("保存失败！");
            return;
        }

        if (selectRow == -1) {
            onInsert();
            return;
        }
        else {
            onUpdate();
            return;
        }
    }

    public void onDelete() {
        if (messageBox("询问", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            TParm parm = getParmForTag(
                "REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION");
            //删除数据
            TParm result = NoTool.getInstance().deletedata(parm);
            if (result.getErrCode() < 0) {
                messageBox("E0003");
                onClear();
                onQuery();
                return;
            }
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            callFunction("UI|Table|removeRow", row);
            callFunction("UI|Table|setSelectedRow", row);
            messageBox("P0003");
            setValueForParm("REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION;STATUS_METHOD_CODE;START_NO;MAX_NO;NO_LENGTH;NO_LENGTH;NO_DESC;NO_FORMAT;CURR_NO",
                            data, row);
        }
        else {
            return;
        }
    }

    public void onClear() {
        //回到初始化状态
        myInit();
    }

    public void myInit() {
        selectRow = -1;
        //清空所有数据
        clearValue("REGION_CODE;SYSTEM_CODE;OPERATION;SUB1_OPERATION;STATUS_METHOD_CODE;CURR_NO;NO_LENGTH;START_NO;MAX_NO;NO_DESC;NO_FORMAT");
        //查询所有数据
        onQuery();
        //设置默认的按钮状态
        callFunction("UI|DELETE|setEnabled", false);
        callFunction("UI|REGION_CODE|setEnabled", true);
        callFunction("UI|SYSTEM_CODE|setEnabled", true);
        callFunction("UI|OPERATION|setEnabled", true);
        callFunction("UI|SUB1_OPERATION|setEnabled", true);
    }

    //检验长度是否符合自定义标准
    public boolean checkLength() {
        //是否格式化标记
        boolean formatFlag = TCM_Transform.getBoolean(this.getValue("NO_FORMAT"));
        //自定义长度
        int noLength = TCM_Transform.getInt(this.getValue("NO_LENGTH"));
        //最大号码的长度（位数）
        int maxNoLenth = TCM_Transform.getString(this.getValue("MAX_NO")).
            length();
        //如果格式化[最大号位数+6(年两位月日)=自定义数]/如果不格式化最大号位数<=自定义数
        if (formatFlag && (maxNoLenth + 6) == noLength) {
            return true;
        }
        if (!formatFlag && maxNoLenth <= noLength) {
            return true;

        }
        this.messageBox("最大号长度不符合！");

        return false;
    }


    //测试用例
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e) {
        }
        JavaHisDebug.initClient();
        //JavaHisDebug.initServer();

        TConfigParm parm = new TConfigParm();
        parm.setSocket(TIOM_AppServer.SOCKET);
        parm.setSystemGroup("");
        parm.setConfig("%ROOT%\\config\\sys\\SYSNo.x");
        parm.setConfigColor("%ROOT%\\config\\system\\TColor.x");
        parm.setConfigClass("%ROOT%\\config\\system\\TClass.x");
        TSystem.setObject("MessageObject", new MessageTool());
        TFrame.openWindow(parm);

    }


}
