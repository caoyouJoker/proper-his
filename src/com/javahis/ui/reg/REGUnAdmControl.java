package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import java.util.ArrayList;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import jdo.reg.PatAdmTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 *
 * <p>Title: 批量退挂</p>
 *
 * <p>Description: 批量退挂</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class REGUnAdmControl
    extends TControl {
    TParm tableAllParm;
    public void onInit() {
        super.onInit();
        //账单table专用的监听
        getTTable("Table").addEventListener(TTableEvent.
                                            CHECK_BOX_CLICKED, this,
                                            "onTableComponent");
        tableAllParm = new TParm();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        setValue("ADM_DATE", SystemTool.getInstance().getDate());
        this.clearValue("CLINICROOM;SESSION_CODE");
        setValue("ALL", "N");
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
    }

    /**
     * 退挂
     */
    public void onUnReg() {
        TParm result = new TParm();
        result = tableAllParm;
        if (result == null || result.getCount("CASE_NO") < 1) {
            this.messageBox_("无退挂信息");
            return;
        }
        String optId = Operator.getID();
        String term = Operator.getIP();
        int count = result.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            TParm parm = new TParm();
            parm.setData("REGCAN_USER",optId);
            parm.setData("CASE_NO",result.getData("CASE_NO", i));
            parm.setData("ADM_TYPE",result.getValue("ADM_TYPE", i));
            parm.setData("ADM_DATE",StringTool.getString(result.getTimestamp(
                "ADM_DATE", i), "yyyyMMdd"));
            parm.setData("SESSION_CODE",result.getValue("SESSION_CODE",i));
            parm.setData("CLINICROOM_NO",result.getValue("CLINICROOM_NO",i));
            parm.setData("QUE_NO",result.getValue("QUE_NO",i));
            parm.setData("OPT_USER",optId);
            parm.setData("OPT_TERM",term);
            TParm re = TIOM_AppServer.executeAction(
            "action.reg.REGAction",
            "updateUnAdmReg", parm);
            if (re.getErrCode() < 0) {
                this.messageBox_("病患 "+result.getValue("PAT_NAME",i) + " 退挂失败");
            }
        }
        onQuery();
    }

    /**
     * table监听checkBox改变
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        tableAllParm = new TParm();
        int count = tableParm.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                tableAllParm.addData("REGION_CODE",
                                     tableParm.getValue("REGION_CODE", i));
                tableAllParm.addData("ADM_TYPE",
                                     tableParm.getValue("ADM_TYPE", i));
                tableAllParm.addData("ADM_DATE",
                                     tableParm.getData("ADM_DATE", i));
                tableAllParm.addData("SESSION_CODE",
                                     tableParm.getValue("SESSION_CODE", i));
                tableAllParm.addData("CLINICROOM_NO",
                                     tableParm.getValue("CLINICROOM_NO", i));
                tableAllParm.addData("QUE_NO",
                                     tableParm.getValue("QUE_NO", i));
                tableAllParm.addData("CASE_NO",
                                     tableParm.getValue("CASE_NO", i));
            }
        }
        return true;
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm selParm = new TParm();
        if(this.getValueString("ADM_DATE").length()<=0){
            this.messageBox_("请选择就诊日期");
            this.grabFocus("ADM_DATE");
            return;
        }
        if(this.getValueString("SESSION_CODE").length()>0){
            selParm.setData("SESSION_CODE",this.getValueString("SESSION_CODE"));
        }
        if(this.getValueString("CLINICROOM").length()>0){
            selParm.setData("CLINICROOM",this.getValueString("CLINICROOM"));
        }
        selParm.setData("ADM_DATE",
                        StringTool.getString( (Timestamp)this.getValue("ADM_DATE"),
                                             "yyyyMMdd"));
        if ("".equals(Operator.getRegion()))
            selParm.setData("REGION_CODE", Operator.getRegion());
        TParm result = PatAdmTool.getInstance().selectUnAdmData(selParm);
        if (result.getCount("CLINICROOM_NO") < 1) {
            this.messageBox_("查无数据");
            this.callFunction("UI|Table|removeRowAll");
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        this.setValue("ALL","N");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
    }

    /**
     * checkBox选中事件
     */
    public void selAllFlg() {
        TTable table = getTTable("Table");
        TParm tableParm = table.getParmValue();
        int count = 0;
        if (tableParm != null)
            count = tableParm.getCount();
        if ("Y".equals(getValue("ALL"))) {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "Y");
            }
            tableAllParm = tableParm;
        }
        else {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "N");
            }
            tableAllParm = new TParm();
        }
    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
}
