package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.reg.REGQueMethodTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.reg.PanelGroupTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTextField;
import java.sql.Timestamp;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title:给号方式控制类 </p>
 *
 * <p>Description:给号方式控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.9.17
 * @version 1.0
 */
public class REGQueMethodControl
    extends TControl {
    int selectRow = -1;
    private static final String actionName = "action.reg.REGAction";
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLEMETHOD|addEventListener",
                     "TABLEMETHOD->" + TTableEvent.CLICKED, this,
                     "onTABLEMETHODClicked");
       // onQuery();
    }

    /**
     *增加对TABLEMETHOD的监听
     * @param row int
     */
    public void onTABLEMETHODClicked(int row) {

        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLEMETHOD|getParmValue");
        setValueForParm(
            "QUEGROUP_CODE;QUE_NO;VISIT_CODE;APPT_CODE;REGMETHOD_CODE",
            data, row);
        String startTime = data.getValue("START_TIME",row);
        Timestamp sTime =StringTool.getTimestamp(startTime,"HHmm") ;
        setValue("START_TIME",sTime);
        selectRow = row;
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag("QUEGROUP_CODE;QUE_NO:int", true);
        TParm data = REGQueMethodTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEMETHOD|setParmValue", data);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!this.emptyTextCheck("QUEGROUP_CODE,QUE_NO"))
            return;
        //======zhangp 20120227 modify start
        String queGroup = getValueString("QUEGROUP_CODE");
        String queNo = getValueString("QUE_NO");
        if(queGroup.equals("")||queNo.equals("")){
        	messageBox("请输入必填项");
        	return;
        }
        TParm parm = getParmForTag(
            "QUEGROUP_CODE;QUE_NO;VISIT_CODE;APPT_CODE;REGMETHOD_CODE");
        if (this.getValue("VISIT_CODE").equals("Y"))
            parm.setData("VISIT_CODE", "1");
        else
            parm.setData("VISIT_CODE", "0");
        parm.setData("OPT_USER", Operator.getID());
        String time = StringTool.getString(TypeTool.getTimestamp(this.getValue("START_TIME")) ,"HHmm");
        parm.setData("START_TIME",time);
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = REGQueMethodTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table上加入新增的数据显示
        callFunction("UI|TABLEMETHOD|addRow", parm,
                     "QUEGROUP_CODE;QUE_NO;VISIT_CODE;APPT_CODE;REGMETHOD_CODE;START_TIME;OPT_USER;OPT_DATE;OPT_TERM");

        //弹出新增成功提示框
        this.messageBox("P0002");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "QUEGROUP_CODE;QUE_NO;VISIT_CODE;APPT_CODE;REGMETHOD_CODE");
        if (this.getValue("VISIT_CODE").equals("Y"))
            parm.setData("VISIT_CODE", "1");
        else
            parm.setData("VISIT_CODE", "0");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        String time = StringTool.getString(TypeTool.getTimestamp(this.getValue("START_TIME")) ,"HHmm");
        parm.setData("START_TIME",time);

        TParm result = REGQueMethodTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|TABLEMETHOD|getSelectedRow");
        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLEMETHOD|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|TABLEMETHOD|setRowParmValue", row, data);
        this.messageBox("P0001");

    }

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }


    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String quegroupCode = getValue("QUEGROUP_CODE").toString();
            String queNo = getValue("QUE_NO").toString();
            TParm result = REGQueMethodTool.getInstance().deletedata(quegroupCode,
                queNo);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|TABLEMETHOD|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLEMETHOD|removeRow", row);
            this.callFunction("UI|TABLEMETHOD|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }
    }

    /**
     * 诊号设定
     */
    public void setQueNo() {
        TParm parm = getParmForTag("QUEGROUP_CODE;QUE_NO;VISIT_CODE;APPT_CODE;REGMETHOD_CODE;S_NO:int;E_NO:int;I_NO:int");
        String visitCode = "";
        if (this.getValue("VISIT_CODE").equals("Y"))
            visitCode = "1";
        else
            visitCode = "0";
        String apptCode = "";
        if (this.getValue("APPT_CODE").equals("Y"))
            apptCode = "1";
        else
            apptCode = "0";
        parm.setData("VISIT_CODE", visitCode);
        parm.setData("APPT_CODE", apptCode);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm actionParm = TIOM_AppServer.executeAction(actionName,
            "setQueNoForQueMethod", parm);
//        TParm data=(TParm)callFunction("UI|TABLEMETHOD|getParmValue");
//        data.setData("",row,actionParm.getData(""));
//        callFunction("UI|TABLEMETHOD|setRowParmValue", data);
        onQueryForTable();
        this.messageBox("P0001");

    }

    /**
     * 到院时间设定
     */
    public void onArriveHopTime() {
        TParm parm = new TParm();
        String queGroup = this.getValueString("QUEGROUP_CODE");
        int maxNo = PanelGroupTool.getInstance().getMaxQue(queGroup);
        parm.setData("MAX_NO", maxNo);
        String startTime = StringTool.getString((Timestamp)getValue("START_TIME"),"HH:mm") ;
        startTime = startTime.replace(":","");
        String interveenTime = getValue("I_TIME").toString();
        parm.setData("QUEGROUP_CODE", queGroup);
        parm.setData("START_TIME", startTime);
        parm.setData("I_TIME", interveenTime);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm actionParm = TIOM_AppServer.executeAction(actionName,
            "setArriveTime", parm);
        if (actionParm.getErrCode() < 0) {
            err(actionParm.getErrName() + " " + actionParm.getErrText());
            return;
        }
        onQueryForTable();
        this.messageBox("P0001");
    }

    /**
     * 刷新table用查询
     */
    public void onQueryForTable() {
        TParm parm = getParmForTag("QUEGROUP_CODE", true);
        TParm data = REGQueMethodTool.getInstance().seldataForTable(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEMETHOD|setParmValue", data);
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue("QUEGROUP_CODE;QUE_NO;VISIT_CODE;APPT_CODE;REGMETHOD_CODE;S_NO;E_NO;I_NO;START_TIME;I_TIME");
        this.callFunction("UI|Table|clearSelection");
        selectRow = -1;
    }
    /**
     * 检核记录时间格式
     * @return boolean
     */
    public boolean checkTime() {
        TTextField startTime = (TTextField)this.getComponent("START_TIME");
        String recordTime = startTime.getValue();
        //“：”在第二位并且后面还有两位
        int standing = recordTime.indexOf(":");
        if (":".equals(recordTime.trim()) || "".equals(recordTime.trim()) ||
            (standing == 2 && recordTime.substring(standing).length() == 3)) {
            int hour = Integer.parseInt(recordTime.substring(0, 2));
            if (hour >= 24) {
                this.messageBox("小时 输入错误！");
                startTime.setValue(":");
                return false;
            }

            int mini = Integer.parseInt(recordTime.substring(3));
            if (mini >= 60) {
                this.messageBox("分钟 输入错误！");
                startTime.setValue(":");
                return false;
            }

            return true;
        }
        this.messageBox("记录时间输入错误！");
        startTime.setValue(":");
        return false;
    }


    public static void main(String arg[]) {
//        com.javahis.util.JavaHisDebug.runFrame("reg\\REGQueMethod.x");
       // com.javahis.util.JavaHisDebug.initClient();
        String a = "0800";
        String b = "40";

//        addTime(a,b);

    }
}
