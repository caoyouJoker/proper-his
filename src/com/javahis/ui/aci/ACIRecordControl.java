package com.javahis.ui.aci;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.aci.ACIRecordTool;
import com.dongyang.ui.event.TTableEvent;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 差错与事故管理控制类</p>
 *
 * <p>Description: 差错与事故管理控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2009.10.05
 * @version 1.0
 */
public class ACIRecordControl
    extends TControl {
    int selectRow = -1;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        initPage();
    }

    /**
     * 初始化界面信息
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("S_DATE", yesterday);
        setValue("E_DATE", today);
        setValue("OCCUR_DATE", today);
        setValue("IDENTITY_DATE", today);

    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
            "ACI_RECORD_NO;TYPE_CODE;CLASS_CODE;DEPT_CODE;STATION_CODE;" +
            "OCCUR_DATE;MR_NO;IPD_NO;PAT_NAME;PAT_COMPANY;" +
            "IN_CHARGE_CODE;IDENTITY_DATE;IDENTITY_COMPANY;PATIENT_CONDITITION;RESULT_CODE;" +
            "OPT_USER;OPT_DATE;OPT_TERM",
            data, row);
//        setValue("ADM_DATE",
//                 StringTool.getTimestamp(data.getValue("ADM_DATE", row),
//                                         "yyyyMMdd"));
        selectRow = row;

    }

    /**
     * 病患查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        parm.setData("S_DATE", sDate);
        parm.setData("E_DATE", eDate);
        if (getValue("DEPT_CODE_S").toString().length() != 0 &&
            getValue("DEPT_CODE_S") != null)
            parm.setData("DEPT_CODE", getValue("DEPT_CODE_S"));
        TParm result = ACIRecordTool.getInstance().selectdata(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
        }
        this.callFunction("UI|Table|setParmValue", result);

    }

    /**
     * 新增
     */
    public void onInsert() {
        TParm parm = getParmForTag(
            "ACI_RECORD_NO;TYPE_CODE;CLASS_CODE;DEPT_CODE;STATION_CODE;" +
            "OCCUR_DATE:timestamp;MR_NO;IPD_NO;PAT_NAME;PAT_COMPANY;" +
            "IN_CHARGE_CODE;IDENTITY_DATE:timestamp;IDENTITY_COMPANY;PATIENT_CONDITITION;RESULT_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = ACIRecordTool.getInstance().insertdata(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        else {
            onClear();
            this.messageBox("P0002");
        }

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = new TParm();
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        TParm tableParm = table.getParmValue();
        String recordNo = tableParm.getValue("ACI_RECORD_NO", selRow);
        String typeCode = getValue("TYPE_CODE").toString();
        String classCode = getValue("CLASS_CODE").toString();
        String deptCode = getValue("DEPT_CODE").toString();
        String stationCode = getValue("STATION_CODE").toString();
        Timestamp occurDate = TypeTool.getTimestamp(getValue("OCCUR_DATE"));
        String mrNo = getValue("MR_NO").toString();
        String ipdNo = getValue("IPD_NO").toString();
        String patName = getValue("PAT_NAME").toString();
        String patCompany = getValue("PAT_COMPANY").toString();
        String inChargeCode = getValue("IN_CHARGE_CODE").toString();
        Timestamp identityDate = TypeTool.getTimestamp(getValue("IDENTITY_DATE"));
        String identityCompany = getValue("IDENTITY_COMPANY").toString();
        String patientConditition = getValue("PATIENT_CONDITITION").toString();
        String resultCode = getValue("RESULT_CODE").toString();
        parm.setData("ACI_RECORD_NO", recordNo);
        parm.setData("TYPE_CODE", typeCode);
        parm.setData("CLASS_CODE", classCode);
        parm.setData("DEPT_CODE", deptCode);
        parm.setData("STATION_CODE", stationCode);
        parm.setData("OCCUR_DATE", occurDate);
        parm.setData("MR_NO", mrNo);
        parm.setData("IPD_NO", ipdNo);
        parm.setData("PAT_NAME", patName);
        parm.setData("PAT_COMPANY", patCompany);
        parm.setData("IN_CHARGE_CODE", inChargeCode);
        parm.setData("IDENTITY_DATE", identityDate);
        parm.setData("IDENTITY_COMPANY", identityCompany);
        parm.setData("PATIENT_CONDITITION", patientConditition);
        parm.setData("RESULT_CODE", resultCode);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        if (selRow < 0) {
            return;
        }
        TParm result = ACIRecordTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);
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
        if (this.messageBox("是否删除", "询问", 2) == 0) {
            if (selectRow == -1)
                return;
            TTable table = (TTable)this.getComponent("Table");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String recordNo = tableParm.getValue("ACI_RECORD_NO", selRow);
            TParm result = ACIRecordTool.getInstance().deletedata(recordNo);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }

    }

    /**
     * 清空
     */
    public void onClear() {
        setValue("DEPT_CODE_S", "");
        setValue("TYPE_CODE", "");
        setValue("CLASS_CODE", "");
        setValue("DEPT_CODE", "");
        setValue("STATION_CODE", "");
        setValue("IPD_NO", "");
        setValue("MR_NO", "");
        setValue("PAT_NAME", "");
        setValue("PAT_COMPANY", "");
        setValue("IN_CHARGE_CODE", "");
        setValue("IDENTITY_DATE", "");
        setValue("IDENTITY_COMPANY", "");
        setValue("PATIENT_CONDITITION", "");
        setValue("RESULT_CODE", "");
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        initPage();
    }

    /**
     * 病案号查询
     */
    public void onMrNo() {

        String mrNo = PatTool.getInstance().checkMrno(getValue("MR_NO").
            toString());
        
     // modify by huangtt 20160929 EMPI患者查重提示 start
        this.setValue("MR_NO", mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);// 病案号
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
        TParm parm = PatTool.getInstance().getInfoForMrno(mrNo);
        String parName = parm.getValue("PAT_NAME", 0);
        String patCompany = parm.getValue("COMPANY_DESC", 0);
        setValue("MR_NO", mrNo);
        setValue("PAT_NAME", parName);
        setValue("PAT_COMPANY", patCompany);
    }


    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.initClient();
    }

}
