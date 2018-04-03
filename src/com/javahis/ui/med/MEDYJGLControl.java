package com.javahis.ui.med;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.med.MEDYJGLTool;
import jdo.med.MedToLedTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 医技科室排程信息发送 </p>
 * 
 * <p> Description: 医技科室排程信息发送 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company:BlueCore </p>
 * 
 * @author wanglong 2013.11.12
 * @version 1.0
 */
public class MEDYJGLControl
        extends TControl {

    private TTable tableM;// 人员table
    private TTable tableD;// 医嘱table
    private TParm param;// 查询条件
    TTextFormat execDeptCode;// 上报科室 add by wanglong 20131127
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable) this.getComponent("TABLE_M");
        tableD = (TTable) this.getComponent("TABLE_D");
        execDeptCode  = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");// 执行科室
        // this.setValue("ADM_TYPE", "O");
        Timestamp now = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(now, "yyyy/MM/dd");
        this.setValue("START_DATE", dateStr + " 00:00:00");
        this.setValue("END_DATE", now);
        tableD.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxClicked");
        if (this.getPopedem("ADMIN")) {
        	String deptSql = // add by wanglong 20131127
                "SELECT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1 FROM SYS_DEPT A, SYS_OPERATOR_DEPT B "
                        + " WHERE A.DEPT_CODE = B.DEPT_CODE AND B.USER_ID = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
        deptSql = deptSql.replaceFirst("#", Operator.getID());
        execDeptCode.setPopupMenuSQL(deptSql);
        }else if(this.getPopedem("ORDINARY")){
        	execDeptCode.setValue(Operator.getDept());
        	execDeptCode.setEnabled(false);
        }
        if (Operator.getRole().equals("ADMIN")) {
            execDeptCode.setHisOneNullRow(true);
        }
        execDeptCode.onQuery();
        execDeptCode.setValue(Operator.getDept());
    }

    /**
     * 查询
     */
    public void onQuery() {
    
        param = new TParm();
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
//        endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
        param.setData("START_DATE", StringTool.getString(startDate, "yyyyMMddHHmmss"));
        param.setData("END_DATE", StringTool.getString(endDate, "yyyyMMddHHmmss"));
        

        //以报到，未报到，和全部的选择。
        if (this.getValueString("ALL").equals("Y")) {
            param.setData("REPORT_STATE", "ALL");
        } else if (this.getValueString("UNREPORT").equals("Y")) {
            param.setData("REPORT_STATE", "UNREPORT");
        } else if (this.getValueString("REPORTED").equals("Y")) {
            param.setData("REPORT_STATE", "REPORTED");
        }
        //依据病案号查询
        if (!"".equals(this.getValueString("MR_NO"))) {
            param.setData("MR_NO", this.getValueString("MR_NO"));
        }
        //依据门诊查询
        if (!"".equals(this.getValueString("ADM_TYPE"))) {
            param.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        }
        //依据开立医生查询
        //============donglt MedToled 20160304 添加开立医生查询
//        this.messageBox(this.getValueString("C_DR_CODE"));
//        System.out.println("****"+param);
        if (!"".equals(this.getValueString("C_DR_CODE"))) {
            param.setData("C_DR_CODE", this.getValueString("C_DR_CODE"));
        }
        //依据执行科室查询x
        if (!"".equals(this.getValueString("EXEC_DEPT_CODE"))) {// add by wanglong 20131127
            param.setData("EXEC_DEPT_CODE", this.getValueString("EXEC_DEPT_CODE"));
        } else {
            this.messageBox("请选择执行科室");
            return;
        }
        
        // 查询人员信息
//        System.out.println(param);
//        this.messageBox_(param);
        TParm patParm = MEDYJGLTool.getInstance().queryPatInfo(param);
        if (patParm.getErrCode() < 0) {
            this.messageBox("操作失败");// 操作失败
            return;
        }
//        this.messageBox("nihao12222222");
        this.callFunction("UI|regist|setEnabled", true);
        this.callFunction("UI|unRegist|setEnabled", true);
        this.setValue("ALL_SELECT", "N");
        if (patParm.getCount() <= 0) {
            tableD.setDSValue();
            tableM.setDSValue();
            this.setValue("COUNT", "");
            return;
        }else{
            if (this.getValueString("ADM_TYPE").equals("O")
                    || this.getValueString("ADM_TYPE").equals("E")) {
                tableM.setHeader("来源,80,ADM_TYPE;病案号,100;姓名,120;性别,70,SEX_CODE;年龄,50;出生日期,120,timestamp;科室,120,COMPANY_CODE;医师,100,CONTRACT_CODE;诊号");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,right");
            } else if (this.getValueString("ADM_TYPE").equals("I")) {
                tableM.setHeader("来源,80,ADM_TYPE;病案号,100;姓名,120;性别,70,SEX_CODE;年龄,50;出生日期,120,timestamp;科室,120,COMPANY_CODE;医师,100,CONTRACT_CODE;床号");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,left");
            } else if (this.getValueString("ADM_TYPE").equals("H")) {
                tableM.setHeader("来源,80,ADM_TYPE;病案号,100;姓名,120;性别,70,SEX_CODE;年龄,50;出生日期,120,timestamp;团体名称,120,COMPANY_CODE;合同名称,120,CONTRACT_CODE;序号");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,right");
            } else {
                tableM.setHeader("来源,80,ADM_TYPE;病案号,100;姓名,120;性别,70,SEX_CODE;年龄,50;出生日期,120,timestamp;科室/团体名称,120,COMPANY_CODE;医师/合同名称,120,CONTRACT_CODE;诊号/床号/序号");
                tableM.setColumnHorizontalAlignmentData("2,left;4,right;6,left;7,left;8,right");
            }
        }
        //System.out.println("----------------patParm-------------"+patParm);
        tableM.setParmValue(patParm);
        this.setValue("COUNT", patParm.getCount() + "");
        TParm resultParm = new TParm();
        tableD.setParmValue(resultParm);
//        tableD.removeAll();//yanjing 20140411
        
        if (patParm.getCount() == 1) {
            tableM.setSelectedRow(0);
            onTableClicked();
        }
    }

    /**
     * 读卡
     */
    public void onReadCard() {
        TParm readEkt = new TParm();
        try {
            readEkt = EKTIO.getInstance().readEkt();// 医疗卡中只有MR_NO信息
        }
        catch (Exception e) {
          
            this.messageBox("读卡失败");
            e.printStackTrace();
        }
        String mrNo = readEkt.getValue("MR_NO");
        this.setValue("MR_NO", mrNo);
        onMrNo();
    }

    /**
     * 报到
     */
    public void onRegist() {
        TParm parmShowValue = tableD.getShowParmValue();
        TParm parmValue = tableD.getParmValue();
        TParm parm = new TParm();
        for (int i = 0; i < parmShowValue.getCount("ORDER_DESC"); i++) {
            if ("Y".equals(parmShowValue.getValue("FLG", i))) {
                if ("UNREPORT".equals(parmValue.getValue("REPORT_STATE", i))) {
                    if(parmValue.getData("ADM_TYPE", i).equals("I")){
                        String sql =
                                "SELECT A.* FROM ODI_DSPNM A, ODI_ORDER B "
                                        + " WHERE A.CASE_NO = B.CASE_NO   "
                                        + "   AND A.ORDER_NO = B.ORDER_NO "
                                        + "   AND A.ORDER_SEQ = B.ORDER_SEQ "
                                        + "   AND B.CASE_NO = '#'       "
                                        + "   AND B.ORDER_NO = '#'      "
                                        + "   AND B.ORDER_SEQ = #       "
                                        + "   AND B.ORDER_CODE = '#' ";
                        sql = sql.replaceFirst("#", parmValue.getValue("CASE_NO", i));
                        sql = sql.replaceFirst("#", parmValue.getValue("ORDER_NO", i));
                        sql = sql.replaceFirst("#", parmValue.getValue("SEQ_NO", i));
                        sql = sql.replaceFirst("#", parmValue.getValue("ORDER_CODE", i));
                        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                        if (result.getErrCode() < 0) {
                            this.messageBox(result.getErrText());
                            continue;
                        }
                        if (result.getCount() < 1||result.getValue("NS_EXEC_CODE", 0).equals("")) {
                            this.messageBox(parmValue.getData("ORDER_DESC", i) + " 护士未执行，无法报到");
                            continue;
                        }
                    }
                    parm.addData("ADM_TYPE", parmValue.getData("ADM_TYPE", i));
                    parm.addData("CASE_NO", parmValue.getData("CASE_NO", i));
                    parm.addData("MR_NO", parmValue.getData("MR_NO", i));
                    parm.addData("ORDER_NO", parmValue.getData("ORDER_NO", i));
                    parm.addData("ORDER_CODE", parmValue.getData("ORDER_CODE", i));
                    parm.addData("CAT1_TYPE", parmValue.getData("CAT1_TYPE", i));// 住院专用
                    parm.addData("IPD_NO", parmValue.getData("IPD_NO", i));// 住院专用
                    parm.addData("DEPT_CODE", parmValue.getData("DEPT_CODE", i));// 住院专用
                    parm.addData("STATION_CODE", parmValue.getData("STATION_CODE", i));// 住院专用
                    parm.addData("BED_NO", parmValue.getData("BED_NO", i));// 住院专用
                    parm.addData("DOSAGE_QTY", 1);// 住院专用
                    parm.addData("OPT_USER", Operator.getID());// 住院专用
                    parm.addData("OPT_TERM", Operator.getIP());// 住院专用
                    parm.addData("SEQ_NO", parmValue.getData("SEQ_NO", i));
                    parm.addData("APPLICATION_NO", parmValue.getData("APPLICATION_NO", i));
                }
            }
        }
        parm.setData("EXEC_DR_CODE", Operator.getID());//wanglong add 20140514增加更新执行人字段
        parm.setData("EXEC_DR_DESC", Operator.getName());
        parm.setCount(parm.getCount("CASE_NO"));
        
        if((parmValue.getValue("ADM_TYPE", 0).equals("O")
                || parmValue.getValue("ADM_TYPE", 0).equals("E")) && EKTIO.getInstance().ektSwitch()){//门急诊检查余额 add by wanglong 20131209
            TParm orderPreParm = EKTpreDebtTool.getInstance().getMedOpdOrder(parm);
            orderPreParm.setData("CASE_NO", parmValue.getData("CASE_NO", 0));
            orderPreParm.setData("MR_NO", parmValue.getData("MR_NO", 0));
            TParm preParm = EKTpreDebtTool.getInstance().checkMasterForExe(orderPreParm);
            if(preParm.getErrCode()<0){
                messageBox(preParm.getErrText());
                return;
            }
        }
        
        if (parm.getCount() > 0) {
            TParm result = MEDYJGLTool.getInstance().registOrder(parm);//执行报到操作
            if (result.getErrCode() < 0) {
                this.messageBox("报到失败！" + result.getErrText());
                return;
            }
            this.messageBox("报到成功！");
            for (int i = 0; i < parmValue.getCount("ORDER_CODE"); i++) {
                for (int j = 0; j < parm.getCount("CASE_NO"); j++) {
                    if (parmValue.getValue("ORDER_NO", i).equals(parm.getValue("ORDER_NO", j))
                            && parmValue.getValue("ORDER_CODE", i)
                                    .equals(parm.getValue("ORDER_CODE", j))
                            && parmValue.getValue("SEQ_NO", i).equals(parm.getValue("SEQ_NO", j))
                            && parmValue.getValue("APPLICATION_NO", i)
                                    .equals(parm.getValue("APPLICATION_NO", j))) {
                        parmValue.setData("REPORT_STATE", i, "REPORTED");
                    }
                }
            }
            for (int i = parmValue.getCount("ORDER_CODE") - 1; i >= 0; i--) {
                if (parmValue.getValue("REPORT_STATE", i).equals("REPORTED")) {
                    parmValue.removeRow(i);
                }
            }
            tableD.setParmValue(parmValue);
        } else {
            this.messageBox("没有需要报到的数据");
        }
        this.onQuery();
    }

    /**
     * 取消报到
     */
    public void onUnRegist() {
        TParm parmShowValue = tableD.getShowParmValue();
        TParm parmValue = tableD.getParmValue();
        if (parmValue.getCount() <= 0) {
            return;
        }
        TParm parm = new TParm();
        for (int i = 0; i < parmValue.getCount("ORDER_CODE"); i++) {
            if ("Y".equals(parmShowValue.getValue("FLG", i))) {
                if ("REPORTED".equals(parmValue.getValue("REPORT_STATE", i))) {
                    parm.addData("ADM_TYPE", parmValue.getData("ADM_TYPE", i));
                    parm.addData("CASE_NO", parmValue.getData("CASE_NO", i));
                    parm.addData("MR_NO", parmValue.getData("MR_NO", i));
                    parm.addData("ORDER_NO", parmValue.getData("ORDER_NO", i));
                    parm.addData("ORDER_CODE", parmValue.getData("ORDER_CODE", i));
                    parm.addData("IPD_NO", parmValue.getData("IPD_NO", i));// 住院专用
                    parm.addData("DEPT_CODE", parmValue.getData("DEPT_CODE", i));// 住院专用
                    parm.addData("STATION_CODE", parmValue.getData("STATION_CODE", i));// 住院专用
                    parm.addData("BED_NO", parmValue.getData("BED_NO", i));// 住院专用
                    parm.addData("DOSAGE_QTY", 1);// 住院专用
                    parm.addData("OPT_USER", Operator.getID());// 住院专用
                    parm.addData("OPT_TERM", Operator.getIP());// 住院专用
                    parm.addData("SEQ_NO", parmValue.getData("SEQ_NO", i));
                    parm.addData("APPLICATION_NO", parmValue.getData("APPLICATION_NO", i));
                }
            }
        }
        parm.setCount(parm.getCount("CASE_NO"));
        if (parm.getCount() > 0) {
            TParm result = MEDYJGLTool.getInstance().unRegistOrder(parm);//执行取消报到操作
            if (result.getErrCode() < 0) {
                this.messageBox("取消报到失败！" + result.getErrText());
                return;
            }
            this.messageBox("取消报到成功！");
            TParm lumpParm=new TParm();
            lumpParm.setData("CASE_NO",parmValue.getData("CASE_NO", 0));
            result=ADMInpTool.getInstance().selectall(lumpParm);//套餐病患医技管理操作集合医嘱退费需要手动退费
            if (null!=result.getValue("LUMPWORK_CODE",0)&&result.getValue("LUMPWORK_CODE",0).length()>0) {
            	this.messageBox("套餐病患,取消报到需要手动操作退费");
            }
            for (int i = 0; i < parmValue.getCount("ORDER_CODE"); i++) {
                for (int j = 0; j < parm.getCount("CASE_NO"); j++) {
                    if (parmValue.getValue("ORDER_NO", i).equals(parm.getValue("ORDER_NO", j))
                            && parmValue.getValue("ORDER_CODE", i)
                                    .equals(parm.getValue("ORDER_CODE", j))
                            && parmValue.getValue("SEQ_NO", i).equals(parm.getValue("SEQ_NO", j))
                            && parmValue.getValue("APPLICATION_NO", i)
                                    .equals(parm.getValue("APPLICATION_NO", j))) {
                        parmValue.setData("REPORT_STATE", i, "UNREPORT");
                    }
                }
            }
            for (int i = parmValue.getCount("ORDER_CODE") - 1; i >= 0; i--) {
                if (parmValue.getValue("REPORT_STATE", i).equals("UNREPORT")) {
                    parmValue.removeRow(i);
                }
            }
            tableD.setParmValue(parmValue);
        } else {
            this.messageBox("没有需要取消的数据");
        }
    }

    /**
     * 右击MENU弹出事件
     */
    public void showPopMenu() {
        tableD.setPopupMenuSyntax("显示集合医嘱细项,onShowOrderSet");
        // tableD.setPopupMenuSyntax("显示集合医嘱细项,onShowOrderSet;查看报告,showReportResult");
    }

    /**
     * 显示集合医嘱细项
     */
    public void onShowOrderSet() {
        int row = tableD.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm parmValue = tableD.getParmValue();
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", parmValue.getData("ADM_TYPE", row));
        parm.setData("CASE_NO", parmValue.getData("CASE_NO", row));
        parm.setData("ORDER_NO", parmValue.getData("ORDER_NO", row));
        parm.setData("ORDER_CODE", parmValue.getData("ORDER_CODE", row));
        parm.setData("SEQ_NO", parmValue.getData("SEQ_NO", row));
        parm.setData("APPLICATION_NO", parmValue.getData("APPLICATION_NO", row));
        TParm result = MEDYJGLTool.getInstance().getOrderSet(parm);
        if (result.getCount() > 0) {
            this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
        }
    }

//    /**
//     * 查看报告
//     */
//    public void showRept() {
//        String mrNo = tableM.getParmValue().getValue("MR_NO", tableM.getSelectedRow());
//        SystemTool.getInstance().OpenRisWeb(mrNo);
//    }
    
    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm parm = tableM.getParmValue().getRow(row);
//        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;ADM_TYPE", parm);
//        Timestamp now = SystemTool.getInstance().getDate();
//        this.setValue("AGE", StringUtil.showAge(parm.getTimestamp("BIRTH_DATE"), now));
        // 查询医嘱信息
        param.setData("MR_NO", parm.getValue("MR_NO"));
        param.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm orderParm = MEDYJGLTool.getInstance().queryOrderInfo(param);
        if (orderParm.getErrCode() < 0) {
            this.messageBox("E0035");// 操作失败
            return;
        }
        if (orderParm.getCount() <= 0) {
            tableD.setDSValue();
            return;
        }
        tableD.setParmValue(orderParm);
        this.setValue("ALL_SELECT", "N");
    }

    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        if(mrNo.equals("")){
            return;
        }
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("没有查询数据");
            this.setValue("MR_NO", "");
            this.setValue("PAT_NAME", "");
            this.setValue("SEX_CODE", "");
            this.setValue("AGE", "");
            this.setValue("BIRTH_DATE", "");
            return;
        }
        Timestamp now = SystemTool.getInstance().getDate();
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX_CODE", pat.getSexCode());
        this.setValue("AGE", StringUtil.showAge(pat.getBirthday(), now));
        this.setValue("BIRTH_DATE", pat.getBirthday());
        onQuery();
    }

    /**
     * 全选按钮事件
     */
    public void onSelectAll() {
        String select = this.getValueString("ALL_SELECT");
        TParm parm = tableD.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        tableD.setParmValue(parm);
        tableD.setSelectedRow(0);
    }
    
    /**
     * 细表的CHECK_BOX事件
     * @param obj
     * @return
     */
    public boolean onCheckBoxClicked(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int row = table.getSelectedRow();
        TParm parmValue = table.getParmValue();
        String medApplyNo = parmValue.getValue("APPLICATION_NO", row);
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (parmValue.getValue("APPLICATION_NO", i).equals(medApplyNo)) {
                table.setValueAt(table.getItemString(row, "FLG"), i, 0);
            }
        }
        if (table.getParmValue().getValue("REPORT_STATE", row).equals("UNREPORT")) {
            this.callFunction("UI|regist|setEnabled", true);
            this.callFunction("UI|unRegist|setEnabled", false);
        } else if (table.getParmValue().getValue("REPORT_STATE", row).equals("REPORTED")) {
            this.callFunction("UI|regist|setEnabled", false);
            this.callFunction("UI|unRegist|setEnabled", true);
        }
        return true;
    }

    /**
     * 补充计价
     */
    public void onCharge() {// add by wanglong 20131127
        int selRow = tableM.getSelectedRow();// 获取选中行
        if (selRow < 0) {
            this.messageBox("请选择病患");
            return;
        }
        TParm parmRow = tableM.getParmValue().getRow(selRow);
        if (parmRow.getValue("ADM_TYPE").equals("O") || parmRow.getValue("ADM_TYPE").equals("E")) {
            TParm parm = new TParm();
            parm.setData("MR_NO", parmRow.getValue("MR_NO"));
            parm.setData("CASE_NO", parmRow.getValue("CASE_NO"));
            parm.setData("SYSTEM", "ONW");
            parm.setData("ONW_TYPE", parm.getValue("ADM_TYPE"));
            parm.setData("count", "1");
//            this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);//旧补充计费界面
            this.openDialog("%ROOT%\\config\\opbTest\\OPBCharge.x",parm);//新补充计费界面
        } else if (parmRow.getValue("ADM_TYPE").equals("I")) {
            int selRowD = tableD.getSelectedRow();// 获取选中行
            if (selRowD < 0) {
                this.messageBox("请选择医嘱");
                return;
            }
            TParm parm =tableD.getParmValue().getRow(selRowD);
            onIBSCharge(parmRow,parm);
        } else if (parmRow.getValue("ADM_TYPE").equals("H")) {
            int choose = this.messageBox("提示", "请选择操作类型（是：补充计费；否：删除计费），或者取消", YES_NO_CANCEL_OPTION);
            if (choose == YES_OPTION) {
                addHRMCharge(parmRow);
            } else if (choose == NO_OPTION) {
                TParm parm = new TParm();
                deleteHRMCharge(parmRow);
            } else {
                return;
            }
        }
    }
    
    /**
     * 住院补充计价
     */
    public void onIBSCharge(TParm parmM,TParm parmD) {// add by wanglong 20131127
        TParm ibsParm = new TParm();
        TParm parm = ADMTool.getInstance().getADM_INFO(parmM).getRow(0);
        ibsParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        ibsParm.setData("MR_NO", parm.getValue("MR_NO"));
        ibsParm.setData("IPD_NO", parm.getValue("IPD_NO"));
        ibsParm.setData("BED_NO", parm.getValue("BED_NO"));
        ibsParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
        ibsParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        ibsParm.setData("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL"));
        ibsParm.setData("VS_DR_CODE", parm.getValue("VS_DR_CODE"));
        ibsParm.setData("CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
        ibsParm.setData("ORDER_CODE", parmD.getValue("ORDER_CODE"));
        ibsParm.setData("ORDER_NO", parmD.getValue("ORDER_NO"));
        ibsParm.setData("ORDER_SEQ", parmD.getInt("SEQ_NO"));
        this.openDialog("%ROOT%\\config\\ibs\\IBSUnderOrderCharge.x", ibsParm);   
//        ibsParm.setData("IBS", "CASE_NO", parm.getValue("CASE_NO"));
//        ibsParm.setData("IBS", "IPD_NO", parm.getValue("IPD_NO"));
//        ibsParm.setData("IBS", "MR_NO", parm.getValue("MR_NO"));
//        ibsParm.setData("IBS", "BED_NO", parm.getValue("BED_NO"));
//        ibsParm.setData("IBS", "DEPT_CODE", parm.getValue("DEPT_CODE"));
//        ibsParm.setData("IBS", "STATION_CODE", parm.getValue("STATION_CODE"));
//        ibsParm.setData("IBS", "VS_DR_CODE", parm.getValue("VS_DR_CODE"));
//        ibsParm.setData("IBS", "TYPE", "INW");
//        ibsParm.setData("IBS", "CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
//        this.openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
    }
    
    /**
     * 健检增加计费
     */
    public void addHRMCharge(TParm parm) {// add by wanglong 20131127
        TParm result = new TParm();
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            result.addData(names[i], parm.getData(names[i]));
        }
        result.setCount(1);
        result.addData("METHOD", "ADD");
        String billSql =
                "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.CASE_NO = B.CASE_NO AND A.CASE_NO = '#'";
        billSql = billSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("检查结算信息失败 " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 已结算，不允许增项");
            return;
        } else if (billParm.getCount() < 1) {
            String patSql =
                    "SELECT * FROM HRM_PATADM A WHERE A.CASE_NO = '#'";
            patSql = patSql.replaceFirst("#", result.getValue("CASE_NO", 0));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                this.messageBox("检查医嘱展开状态失败 " + patParm.getErrText());
                return;
            }
            if (patParm.getCount() < 1) {
                this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 医嘱未展开，不允许增项");
                return;
            }
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
    }

    /**
     * 健检删除计费
     */
    public void deleteHRMCharge(TParm parm) {// add by wanglong 20131127
        TParm result = new TParm();
        String[] names = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            result.addData(names[i], parm.getData(names[i]));
        }
        result.setCount(1);
        result.addData("METHOD", "DELETE");
        String billSql =
                "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                        + " WHERE A.CASE_NO = B.CASE_NO AND A.CASE_NO = '#' ";
        billSql = billSql.replaceFirst("#", result.getValue("CASE_NO", 0));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("检查结算信息失败 " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 已结算，不允许减项");
            return;
        } else if (billParm.getCount() < 1) {
            String patSql =
                    "SELECT * FROM HRM_PATADM A WHERE A.CASE_NO = '#'";
            patSql = patSql.replaceFirst("#", result.getValue("CASE_NO", 0));
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                this.messageBox("检查医嘱展开状态失败 " + patParm.getErrText());
                return;
            }
            if (patParm.getCount() < 1) {
                this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 医嘱未展开，不允许减项");
                return;
            }
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("MR_NO;PAT_NAME;SEX_CODE;AGE;BIRTH_DATE;ADM_TYPE;COUNT");
        tableM.setParmValue(new TParm());
        tableD.setParmValue(new TParm());
        this.callFunction("UI|regist|setEnabled", true);
        this.callFunction("UI|unRegist|setEnabled", true);
        this.setValue("UNREPORT", "Y");
        this.setValue("ALL_SELECT", "N");
        param = new TParm();
    }
    
}
