package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.config.TConfigParm;
import jdo.ins.InsPaymTool;
import com.javahis.util.StringUtil;
import com.dongyang.data.TNull;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Date;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 病患确认身份档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class INS_PAYMControl extends TControl {
    private static String TABLE = "Table";
    private String action = "INSERT";
    private TConfigParm configParm;
    private int mrInt;
    private int ipdInt;
    private String caseNo;
    public void onInit() {
        super.onInit();
        configParm = getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\medicareInfo.x");
        this.mrInt = configParm.getConfig().getInt("", "MRNO");
        this.ipdInt = configParm.getConfig().getInt("", "IPDNO");
        callFunction("UI|" + TABLE + "|addEventListener",
                     TABLE + "->" + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
    }

    public void onTableClicked(int row) {
        if (row < 0)
            return;
        callFunction("UI|NHI_NO|setEnabled", false);
        callFunction("UI|MR_NO|setEnabled", false);
        callFunction("UI|IPD_NO|setEnabled", false);
        callFunction("UI|INS_ACCUL_AMT|setEnabled", false);
        callFunction("UI|ADM_TIMES|setEnabled", false);
        callFunction("UI|BED_NO|setEnabled", false);
        callFunction("UI|ADM_DATE|setEnabled", false);
        callFunction("UI|DIS_DATE|setEnabled", false);
        callFunction("UI|REAL_STAY_DAY|setEnabled", false);
        callFunction("UI|INP_DIAG|setEnabled", false);
        callFunction("UI|DIS_DIAG|setEnabled", false);
        callFunction("UI|TOTAL_AMT|setEnabled", false);
        callFunction("UI|renewedly|setEnabled", true);
        action = "EDIT";
        callFunction("UI|save|setEnabled", true);
    }

    /**
     * 保存
     */
    public void onSave() {
        out("保存begin");
        TParm parm = new TParm();
        parm.setData("MR_NO",this.getValue("MR_NO"));
        parm.setData("CTZ_CODE",this.getValue("CTZ_CODE"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        if ("EDIT".equals(action)) {
            if (!emptyTextCheck("MR_NO,IPD_NO,PAT_NAME,SEX,AGE,INS_COMPANY,ID_NO,IN_DEPT,BED_NO,ADM_DATE,REAL_STAY_DAY,CTZ_CODE"))
                return;
            if (getText("DIS_DATE").length() == 0) {
                parm.setData("DIS_DATE", new TNull(Timestamp.class));
            }
            callFunction("UI|" + TABLE + "|setModuleParmUpdate", parm);
            if (!(Boolean) callFunction("UI|" + TABLE + "|onUpdate")) {
                messageBox_("保存失败");
                return;
            }
            TParm parmTemp = InsPaymTool.getInstance().updateAdmInp(parm);
            TParm parmTemp1 = InsPaymTool.getInstance().updateIbsCtz(parm);
            if(!(parmTemp.getErrCode()<0)&&!(parmTemp1.getErrCode()<0)){
                messageBox_("保存成功");
            }
        } else {
            if (!emptyTextCheck("MR_NO,IPD_NO,PAT_NAME,SEX,AGE,INS_COMPANY,ID_NO,IN_DEPT,BED_NO,ADM_DATE,REAL_STAY_DAY,CTZ_CODE"))
                return;

            if (getText("DIS_DATE").length() == 0) {
                parm.setData("DIS_DATE", new TNull(Timestamp.class));
            }
            parm.setData("CASE_NO",this.caseNo);
            callFunction("UI|" + TABLE + "|setModuleParmInsert", parm);
            if (!(Boolean) callFunction("UI|" + TABLE + "|onInsert")) {
                messageBox_("新增失败");
                return;
            }
            //System.out.println("新增数据"+parm);
            TParm parmTemp = InsPaymTool.getInstance().updateAdmInp(parm);
            TParm parmTemp1 = InsPaymTool.getInstance().updateIbsCtz(parm);
            if (!(parmTemp.getErrCode() < 0) && !(parmTemp1.getErrCode() < 0)) {
                messageBox_("新增成功");
            }
            action = "EDIT";
//            callFunction("UI|delete|setEnabled",true);
//            callFunction("UI|FEE_TYPE|setEnabled",false);
        }
        out("保存end");
    }

    /**
     * 清空
     */
    public void onClear() {
        out("清空begin");
        clearValue("NHI_NO;MR_NO;IPD_NO;PAT_NAME;SEX;AGE;INS_COMPANY;CTZ_CODE;INS_PAY_KIND;ID_NO;OFFICE;INS_ACCUL_AMT;ADM_TIMES;IN_DEPT;BED_NO;ADM_DATE;DIS_DATE;REAL_STAY_DAY;INP_DIAG;DIS_DIAG;OUT_STATUS;TOTAL_AMT");
        callFunction("UI|" + TABLE + "|clearSelection");
        callFunction("UI|IN_FLG|setSelected", true);
        callFunction("UI|NHI_NO|setEnabled", true);
        callFunction("UI|MR_NO|setEnabled", true);
        callFunction("UI|IPD_NO|setEnabled", true);
        callFunction("UI|INS_ACCUL_AMT|setEnabled", true);
        callFunction("UI|ADM_TIMES|setEnabled", true);
        callFunction("UI|BED_NO|setEnabled", true);
        callFunction("UI|ADM_DATE|setEnabled", true);
        callFunction("UI|DIS_DATE|setEnabled", true);
        callFunction("UI|REAL_STAY_DAY|setEnabled", true);
        callFunction("UI|INP_DIAG|setEnabled", true);
        callFunction("UI|DIS_DIAG|setEnabled", true);
        callFunction("UI|TOTAL_AMT|setEnabled", true);
        callFunction("UI|renewedly|setEnabled", false);
        callFunction("UI|save|setEnabled", false);
        action = "INSERT";
        this.caseNo = "";
        out("清空end");
    }
    /**
     * 重新汇入的初始化
     */
    public void onCHR(){
        callFunction("UI|IN_FLG|setSelected", true);
        callFunction("UI|NHI_NO|setEnabled", true);
        callFunction("UI|MR_NO|setEnabled", true);
        callFunction("UI|IPD_NO|setEnabled", true);
        callFunction("UI|INS_ACCUL_AMT|setEnabled", true);
        callFunction("UI|ADM_TIMES|setEnabled", true);
        callFunction("UI|BED_NO|setEnabled", true);
        callFunction("UI|ADM_DATE|setEnabled", true);
        callFunction("UI|DIS_DATE|setEnabled", true);
        callFunction("UI|REAL_STAY_DAY|setEnabled", true);
        callFunction("UI|INP_DIAG|setEnabled", true);
        callFunction("UI|DIS_DIAG|setEnabled", true);
        callFunction("UI|TOTAL_AMT|setEnabled", true);
    }

    /**
     * 查询初始化自动执行
     */
    public void onQuery() {
        callFunction("UI|" + TABLE + "|removeRowAll");
        this.caseNo = "";
        if (this.getValue("MR_NO").toString().length() != 0) {
            this.setValue("MR_NO",
                          this.getSelectParm("MR_NO",
                                             this.getValue("MR_NO").toString()));
        }
        if (this.getValue("IPD_NO").toString().length() != 0) {
            this.setValue("IPD_NO",
                          this.getSelectParm("IPD_NO",
                                             this.getValue("IPD_NO").toString()));
        }
        callFunction("UI|" + TABLE + "|onQuery");
        int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
        if (row <= 0) {
            if (this.getValue("NHI_NO").toString().trim().length() == 0 &&
                this.getValue("MR_NO").toString().trim().length() == 0 &&
                this.getValue("IPD_NO").toString().trim().length() == 0) {
                this.onClear();
                return;
            }
            if (messageBox("提示信息", "是否查询基本信息?", this.YES_NO_OPTION) != 0)
                return;
            if (this.getValue("MR_NO").toString().trim().length() == 0 &&
                this.getValue("IPD_NO").toString().trim().length() == 0) {
                messageBox_("请输入病案号或住院号!");
                callFunction("UI|MR_NO|set");
                this.onClear();
                return;
            }
            if (this.getValue("MR_NO").toString().trim().length() != 0) {
                String mrNo = this.getSelectParm("MR_NO",
                                                 this.getValue("MR_NO").toString());
                if ((Boolean) callFunction("UI|IN_FLG|isSelected")) {
                    TParm action = this.selectPatInfo("MR_NO", mrNo, true);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                } else {
                    TParm action = this.selectPatInfo("MR_NO", mrNo, false);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                }
            } else {
                String ipdNo = this.getSelectParm("IPD_NO",
                                                  this.getValue("IPD_NO").
                                                  toString());
                if ((Boolean) callFunction("UI|IN_FLG|isSelected")) {
                    TParm action = this.selectPatInfo("IPD_NO", ipdNo, true);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                } else {
                    TParm action = this.selectPatInfo("IPD_NO", ipdNo, false);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /**
     * 查询后的可编辑行为
     */
    public void queryUIEdit() {
        callFunction("UI|MR_NO|setEnabled", false);
        callFunction("UI|IPD_NO|setEnabled", false);
        callFunction("UI|save|setEnabled", true);
    }

    /**
     * 初始化界面
     * @param parm TParm
     */
    public void initINSPAYMData(TParm parm) {
        //计算年龄
        String age = StringUtil.getInstance().countAge(parm.getValue(
                "BIRTH_DATE", 0), parm.getTimestamp("IN_DATE", 0), "Y");

        TParm parm1 = parm.getParm("INDATA");
        TParm parm2 = parm.getParm("DSDATA");
        TParm parm3 = parm.getParm("ADMCOUNT");
        this.setValue("MR_NO", parm.getValue("MR_NO", 0));
        this.setValue("IPD_NO", parm.getValue("IPD_NO", 0));
        this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        this.setValue("SEX", parm.getValue("SEX_CODE", 0));
        this.setValue("AGE", age);
        this.setValue("INS_COMPANY", parm.getValue("COMPANY_CODE", 0));
        this.setValue("CTZ_CODE", parm.getValue("CTZ_CODE", 0));
        this.setValue("ID_NO", parm.getValue("IDNO", 0));
        this.setValue("OFFICE", parm.getValue("COMPANY_DESC", 0));
        this.setValue("IN_DEPT", parm.getValue("DS_DEPT_CODE", 0));
        this.setValue("BED_NO", parm.getValue("BED_NO", 0));
        this.setValue("ADM_DATE", parm.getTimestamp("IN_DATE", 0));
        this.setValue("DIS_DATE", parm.getTimestamp("DS_DATE", 0));
        this.setValue("INP_DIAG", parm1.getValue("ICD_CHN_DESC", 0));
        this.setValue("DIS_DIAG", parm2.getValue("ICD_CHN_DESC", 0));
        this.setValue("ADM_TIMES", parm3.getInt("COUNT", 0));
//        this.setValue("REAL_STAY_DAY", parm.getValue("ADM_DAYS", 0));
        Timestamp tp = StringTool.getTimestamp(new Date());
        int days = StringTool.getDateDiffer(StringTool.setTime(tp,"00:00:00"),StringTool.setTime(parm.getTimestamp("IN_DATE", 0),"00:00:00"));
        this.setValue("REAL_STAY_DAY",""+days);
    }
    /**
     * 获得查询参数
     * @param type String
     * @param value String
     * @return String
     */
    public String getSelectParm(String type, String value) {
        String result = "";
        if (type.equals("IPD_NO")) {
            String zro = "";
            int count = this.ipdInt - value.length();
            for (int i = 0; i < count; i++) {
                zro += "0";
            }
            result = zro + value;
        } else if (type.equals("MR_NO")) {
            String zro = "";
            int count = this.mrInt - value.length();
            for (int i = 0; i < count; i++) {
                zro += "0";
            }
            result = zro + value;
        }
        return result;
    }

    /**
     * 查询病患基本信息
     * @param type String
     * @param value String
     * @param admTypeStuts boolean
     * @return TParm
     */
    public TParm selectPatInfo(String type, String value, boolean admTypeStuts) {
        if (admTypeStuts) {
            if (type.equals("IPD_NO")) {
                TParm actionParm = new TParm();
                actionParm.setData("ADM_STAT", "Y");
                actionParm.setData("TYPE", "1");
                actionParm.setData("IPD_NO", value);
                actionParm.setData("HOSP_AREA", "HIS");
                TParm parm = InsPaymTool.getInstance().seletADM_INP(actionParm);
                actionParm.setData("CASE_NO", parm.getValue("CASE_NO", 0));
                TParm parm1 = InsPaymTool.getInstance().seletICDINDSAdm(
                        actionParm);
                TParm parm2 = InsPaymTool.getInstance().selectICDDSAdm(
                        actionParm);
                TParm parm3 = InsPaymTool.getInstance().seletAdmCount(
                        actionParm);
                if (parm.getErrCode() < 0 || parm.getErrCode() > 0) {
                    messageBox_("无病患信息");
                    return null;
                }
                parm.setData("INDATA", parm1.getData());
                parm.setData("DSDATA", parm2.getData());
                parm.setData("ADMCOUNT", parm3.getData());
                this.caseNo = parm.getValue("CASE_NO", 0);
                return parm;
            } else {
                TParm actionParm = new TParm();
                actionParm.setData("ADM_STAT", "Y");
                actionParm.setData("TYPE", "2");
                actionParm.setData("MR_NO", value);
                actionParm.setData("HOSP_AREA", "HIS");
                TParm parm = InsPaymTool.getInstance().seletADM_INP(actionParm);
                actionParm.setData("CASE_NO", parm.getValue("CASE_NO", 0));
                TParm parm1 = InsPaymTool.getInstance().seletICDINDSAdm(
                        actionParm);
                TParm parm2 = InsPaymTool.getInstance().selectICDDSAdm(
                        actionParm);
                TParm parm3 = InsPaymTool.getInstance().seletAdmCount(
                        actionParm);
                if (parm.getErrCode() < 0 || parm.getErrCode() > 0) {
                    messageBox_("无病患信息");
                    return null;
                }
                parm.setData("INDATA", parm1.getData());
                parm.setData("DSDATA", parm2.getData());
                parm.setData("ADMCOUNT", parm3.getData());
                this.caseNo = parm.getValue("CASE_NO", 0);
                return parm;
            }
        } else {
            if (type.equals("IPD_NO")) {
                TParm actionParm = new TParm();
                actionParm.setData("ADM_STAT", "N");
                actionParm.setData("TYPE", "1");
                actionParm.setData("IPD_NO", value);
                actionParm.setData("HOSP_AREA", "HIS");
                TParm parm = InsPaymTool.getInstance().seletADM_INP(actionParm);
                actionParm.setData("CASE_NO", parm.getValue("CASE_NO", 0));
                TParm parm1 = InsPaymTool.getInstance().seletICDINDSAdm(
                        actionParm);
                TParm parm2 = InsPaymTool.getInstance().selectICDDSAdm(
                        actionParm);
                TParm parm3 = InsPaymTool.getInstance().seletAdmCount(
                        actionParm);
                if (parm.getErrCode() < 0 || parm.getErrCode() > 0) {
                    messageBox_("无病患信息");
                    return null;
                }
                parm.setData("INDATA", parm1.getData());
                parm.setData("DSDATA", parm2.getData());
                parm.setData("ADMCOUNT", parm3.getData());
                this.caseNo = parm.getValue("CASE_NO", 0);
                return parm;
            } else {
                TParm actionParm = new TParm();
                actionParm.setData("ADM_STAT", "N");
                actionParm.setData("TYPE", "2");
                actionParm.setData("MR_NO", value);
                actionParm.setData("HOSP_AREA", "HIS");
                TParm parm = InsPaymTool.getInstance().seletADM_INP(actionParm);
                actionParm.setData("CASE_NO", parm.getValue("CASE_NO", 0));
                TParm parm1 = InsPaymTool.getInstance().seletICDINDSAdm(
                        actionParm);
                TParm parm2 = InsPaymTool.getInstance().selectICDDSAdm(
                        actionParm);
                TParm parm3 = InsPaymTool.getInstance().seletAdmCount(
                        actionParm);
                if (parm.getErrCode() < 0 || parm.getErrCode() > 0) {
                    messageBox_("无病患信息");
                    return null;
                }
                parm.setData("INDATA", parm1.getData());
                parm.setData("DSDATA", parm2.getData());
                parm.setData("ADMCOUNT", parm3.getData());
                this.caseNo = parm.getValue("CASE_NO", 0);
                return parm;
            }
        }
    }
    /**
     * 重新汇入
     */
    public void onRenewedly() {
        out("重新汇入begin");
        if ("EDIT".equals(action)) {
            this.onCHR();
            if (messageBox("提示信息", "是否查询基本信息?", this.YES_NO_OPTION) != 0)
                return;
            if (this.getValue("MR_NO").toString().trim().length() == 0 &&
                this.getValue("IPD_NO").toString().trim().length() == 0) {
                messageBox_("请输入病案号或住院号!");
                callFunction("UI|MR_NO|set");
                this.onClear();
                return;
            }
            if (this.getValue("MR_NO").toString().trim().length() != 0) {
                String mrNo = this.getSelectParm("MR_NO",
                                                 this.getValue("MR_NO").toString());
                if ((Boolean) callFunction("UI|IN_FLG|isSelected")) {
                    TParm action = this.selectPatInfo("MR_NO", mrNo, true);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                } else {
                    TParm action = this.selectPatInfo("MR_NO", mrNo, false);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                }
            } else {
                String ipdNo = this.getSelectParm("IPD_NO",
                                                  this.getValue("IPD_NO").
                                                  toString());
                if ((Boolean) callFunction("UI|IN_FLG|isSelected")) {
                    TParm action = this.selectPatInfo("IPD_NO", ipdNo, true);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                } else {
                    TParm action = this.selectPatInfo("IPD_NO", ipdNo, false);
                    if (action != null) {
                        this.initINSPAYMData(action);
                        this.queryUIEdit();
                    } else {
                        return;
                    }
                }
            }
        }
        out("重新汇入end");
    }
}
