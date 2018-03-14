package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import jdo.emr.OptLogTool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;
import java.util.Date;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.PatTool;
import java.text.*;

/**
 * <p>Title: 电子病历操作日志Control</p>
 *
 * <p>Description: 日志查询和批量删除</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class EMROptLogControl
    extends TControl {
    public EMROptLogControl() {
    }

    private TTextField MR_NO;
    private TTextField IPD_NO;
    private TTextField CASE_NO;
    private TTextField PAT_NAME;
    private TTextFormat DEPT_CODE;
    private TTextFormat STATION_CODE;
    private TTextFormat BED_NO;
    private TTextFormat OPT_TYPE;
    private TTextFormat CLASS_CODE;
    private TTextFormat OPT_USER;
    private TTextFormat OPT_DATE_BEGIN;
    private TTextFormat OPT_DATE_END;

    private TTable EMR_OPTLOG;

    /**
     * 初始化方法
     * 初始化查询全部
     */
    public void onInit() {
        super.onInit();
        // 获取全部输入框控件
        getAllComponent();
        // 注册监听事件
        initControler();
        // 起始时间结束时间默认值
        initPage();
        // 查询全部操作日志
        onQuery();
    }

    /**
     * 起始时间结束时间默认值
     */
    public void initPage() {
        DateFormat dateFormatBegin = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat dateFormatEnd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date sysDate = SystemTool.getInstance().getDate();
        String optDateBegin = dateFormatBegin.format(sysDate) + " 00:00:00";
        String optDateEnd = dateFormatEnd.format(sysDate);
        OPT_DATE_BEGIN.setValue(optDateBegin);
        OPT_DATE_END.setValue(optDateEnd);
    }

    /**
     * 获取全部输入框控件
     */
    public void getAllComponent() {
        this.MR_NO = (TTextField)this.getComponent("MR_NO");
        this.IPD_NO = (TTextField)this.getComponent("IPD_NO");
        this.CASE_NO = (TTextField)this.getComponent("CASE_NO");
        this.PAT_NAME = (TTextField)this.getComponent("PAT_NAME");
        this.PAT_NAME.setEditable(false);
        this.CLASS_CODE = (TTextFormat)this.getComponent("CLASS_CODE");
        this.DEPT_CODE = (TTextFormat)this.getComponent("DEPT_CODE");
        this.STATION_CODE = (TTextFormat)this.getComponent("STATION_CODE");
        this.BED_NO = (TTextFormat)this.getComponent("BED_NO");
        this.OPT_USER = (TTextFormat)this.getComponent("OPT_USER");
        this.OPT_TYPE = (TTextFormat)this.getComponent("OPT_TYPE");
        this.OPT_DATE_BEGIN = (TTextFormat)this.getComponent("OPT_DATE_BEGIN");
        this.OPT_DATE_END = (TTextFormat)this.getComponent("OPT_DATE_END");

        this.EMR_OPTLOG = (TTable)this.getComponent("EMR_OPTLOG");
    }

    /**
     * 注册监听事件
     */
    public void initControler() {

    }

    /**
     * 字符串非空验证
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(Object obj) {
        if (obj == null) {
            return false;
        }
        String str = String.valueOf(obj);
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        this.MR_NO.setValue(null);
        this.IPD_NO.setValue(null);
        this.CASE_NO.setValue(null);
        this.PAT_NAME.setValue(null);
        this.CLASS_CODE.setValue(null);
        this.DEPT_CODE.setValue(null);
        this.STATION_CODE.setValue(null);
        this.BED_NO.setValue(null);
        this.OPT_USER.setValue(null);
        this.OPT_TYPE.setValue(null);
        this.OPT_DATE_BEGIN.setValue(null);
        this.OPT_DATE_END.setValue(null);

    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmWhere();
        TParm result = OptLogTool.getInstance().queryOptLog(parm);
        EMR_OPTLOG.setParmValue(result, EMR_OPTLOG.getParmMap());
    }

    public TParm getParmWhere() {
        TParm parm = new TParm();
        if (checkInputString(MR_NO.getValue())) {
            parm.setData("MR_NO", MR_NO.getValue());
        }
        if (checkInputString(IPD_NO.getValue())) {
            parm.setData("IPD_NO", IPD_NO.getValue());
        }
        if (checkInputString(CASE_NO.getValue())) {
            parm.setData("CASE_NO", CASE_NO.getValue());
        }
//        if (checkInputString(PAT_NAME.getValue())) {
//            parm.setData("PAT_NAME", PAT_NAME.getValue());
//        }
        if (checkInputString(CLASS_CODE.getValue())) {
            parm.setData("CLASS_CODE", CLASS_CODE.getValue());
        }
        if (checkInputString(DEPT_CODE.getValue())) {
            parm.setData("DEPT_CODE", DEPT_CODE.getValue());
        }
        if (checkInputString(STATION_CODE.getValue())) {
            parm.setData("STATION_CODE", STATION_CODE.getValue());
        }
        if (checkInputString(BED_NO.getValue())) {
            parm.setData("BED_NO", BED_NO.getValue());
        }
        if (checkInputString(OPT_USER.getValue())) {
            parm.setData("OPT_USER", OPT_USER.getValue());
        }
        if (checkInputString(OPT_TYPE.getValue())) {
            parm.setData("OPT_TYPE", OPT_TYPE.getValue());
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (checkInputString(OPT_DATE_BEGIN.getValue())) {
            parm.setData("OPT_DATE_BEGIN",
                         dateFormat.format(OPT_DATE_BEGIN.getValue()));
        }
        if (checkInputString(OPT_DATE_END.getValue())) {
            parm.setData("OPT_DATE_END",
                         dateFormat.format(OPT_DATE_END.getValue()));
        }
        return parm;
    }

    public void getMRNO() {
        this.MR_NO.setValue(PatTool.getInstance().
                            checkMrno(this.MR_NO.getValue()));
        String sql = " SELECT * FROM SYS_PATINFO WHERE MR_NO = '"
            + MR_NO.getValue() + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        IPD_NO.setValue(result.getValue("IPD_NO", 0));
        PAT_NAME.setValue(result.getValue("PAT_NAME", 0));
    }

    public void onTableSelect() {
        TCheckBox SEL_FLG = (TCheckBox)this.getComponent("SEL_FLG");
        if ("Y".equals(SEL_FLG.getValue())) {
            int count = EMR_OPTLOG.getRowCount();
            for (int i = 0; i < count; i++) {
                EMR_OPTLOG.setItem(i, "SEL_FLG", "Y");
            }
        }
        if ("N".equals(SEL_FLG.getValue())) {
            int count = EMR_OPTLOG.getRowCount();
            for (int i = 0; i < count; i++) {
                EMR_OPTLOG.setItem(i, "SEL_FLG", "N");
            }
        }
    }


    public void onDelete() {
        EMR_OPTLOG.acceptText();
        TParm parm = new TParm();
        TParm data = EMR_OPTLOG.getParmValue();
        for (int i = 0; i < EMR_OPTLOG.getRowCount(); i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                parm.addData("CASE_NO", data.getValue("CASE_NO", i));
                parm.addData("FILE_SEQ", data.getValue("FILE_SEQ", i));
                parm.addData("OPT_SEQ", data.getValue("OPT_SEQ", i));
            }
        }
        if (parm.getCount("CASE_NO") <= 0) {
            return;
        }
        if (this.messageBox("询问", "是否删除？", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.emr.EMROptLogAction", "delete", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("删除失败！");
            return;
        }
        else {
            this.messageBox("删除成功！");
            onQuery();
            return;
        }

    }


}
