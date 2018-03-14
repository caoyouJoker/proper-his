package com.javahis.ui.ibs;

import com.dongyang.control.TControl;

import jdo.sys.Pat;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import jdo.ibs.IBSOrdermTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.ibs.IBSOrderdTool;
import jdo.sys.PatTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.Operator;
import com.javahis.system.combo.TComboSYSBed;
import com.javahis.system.combo.TComboSYSStationCode;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;

/**
 *
 * <p>Title: 月底中结控制类</p>
 *
 * <p>Description: 月底中结控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSBillControl
    extends TControl {
    private TRadioButton all;
    private TRadioButton rStationCode;
    private TRadioButton rBedNo;
    private TRadioButton rMrNo;
    private TRadioButton rIpdNo;
    private TComboSYSStationCode STATION_CODE;
    private TComboSYSBed BED_NO;
    private TTextField MR_NO;
    private TTextField IPD_NO;
    private TParm caseParm = new TParm();
    private static final String actionName = "action.ibs.IBSAction";
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        Timestamp today = SystemTool.getInstance().getDate();
        Timestamp yesterday = StringTool.rollDate(today, -1);
        String todayTime = StringTool.getString(yesterday,"yyyy/MM/dd");
        setValue("CUT_BILL_DATE", todayTime+" "+"23:59:59");
        all = (TRadioButton)this.getComponent("All");
        rStationCode = (TRadioButton)this.getComponent("R_STATION_CODE");
        rBedNo = (TRadioButton)this.getComponent("R_BED_NO");
        rMrNo = (TRadioButton)this.getComponent("R_MR_NO");
        rIpdNo = (TRadioButton)this.getComponent("R_IPD_NO");
        STATION_CODE = (TComboSYSStationCode)this.getComponent("STATION_CODE");
        BED_NO = (TComboSYSBed)this.getComponent("BED_NO");
        MR_NO = (TTextField)this.getComponent("MR_NO");
        IPD_NO = (TTextField)this.getComponent("IPD_NO");
        all.setSelected(true);
        STATION_CODE.setEnabled(false);
        BED_NO.setEnabled(false);
        MR_NO.setEnabled(false);
        IPD_NO.setEnabled(false);
    }

    /**
     * 全院radiobutton事件
     */
    public void onSelall() {
        if (all.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(false);
            setValue("STATION_CODE", "");
            setValue("BED_NO", "");
            setValue("MR_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * 病区radiobutton事件
     */
    public void onSelrStationCode() {
        if (rStationCode.getValue().equals("Y")) {
            STATION_CODE.setEnabled(true);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(false);
            setValue("BED_NO", "");
            setValue("MR_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }

    }

    /**
     * 床号radiobutton事件
     */
    public void onSelrBedNo() {
        if (rBedNo.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(true);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(false);
            setValue("STATION_CODE", "");
            setValue("MR_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * 病案号radiobutton事件
     */
    public void onSelrMrNo() {
        if (rMrNo.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(true);
            IPD_NO.setEnabled(false);
            setValue("STATION_CODE", "");
            setValue("BED_NO", "");
            setValue("IPD_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
        }
    }

    /**
     * 病案号radiobutton事件
     */
    public void onSelrIpdNo() {
        if (rIpdNo.getValue().equals("Y")) {
            STATION_CODE.setEnabled(false);
            BED_NO.setEnabled(false);
            MR_NO.setEnabled(false);
            IPD_NO.setEnabled(true);
            setValue("STATION_CODE", "");
            setValue("MR_NO", "");
            setValue("BED_NO", "");
            this.callFunction("UI|RegionTable|removeRowAll");
            this.callFunction("UI|StationTable|removeRowAll");
            this.callFunction("UI|OrderTable|removeRowAll");
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

    /**
     *增加对全院RegionTable的监听
     */
    public void onRegionTableClicked() {
        TTable regionTable = getTTable("RegionTable");
        TTable stationTable = getTTable("StationTable");
        int row = regionTable.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = regionTable.getParmValue();
        parm.setData("STATION_CODE", regionParm.getData("STATION_CODE", row));

        TParm data = IBSOrdermTool.getInstance().selStationPatInfo(parm);
        stationTable.setParmValue(data);
    }

    /**
     *增加对病区StationTable的监听
     */
    public void onStationTableClicked() {
        TTable stationTable = getTTable("StationTable");
        TTable orderTable = getTTable("OrderTable");
        int row = stationTable.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = stationTable.getParmValue();
        parm.setData("CASE_NO", regionParm.getData("CASE_NO", row));
        TParm data = IBSOrderdTool.getInstance().selPatOrderData(parm);
        int sumQty=0;
        double sumNhiPrice=0.00;//医保总金额
        double sumOwnPrice=0.00;//自费总金额
        double sumTotAmt=0.00; //总金额

        for (int i = 0; i < data.getCount(); i++) {
        	sumQty+=data.getDouble("DOSAGE_QTY",i);
        	sumNhiPrice+=data.getDouble("NHI_PRICE",i);
        	sumOwnPrice+=data.getDouble("OWN_PRICE",i);
        	sumTotAmt+=data.getDouble("TOT_AMT",i);
		}
        data.addData("BILL_DATE", data.getData("BILL_DATE",0));
        data.addData("ORDER_DESC", "总计:");
        data.addData("OWN_FLG", "Y");
        data.addData("DOSAGE_QTY", sumQty);
        data.addData("NHI_PRICE", sumNhiPrice);
        data.addData("OWN_PRICE", sumOwnPrice);
        data.addData("TOT_AMT", sumTotAmt);
        data.addData("OPT_USER", data.getData("OPT_USER",0));
        orderTable.setParmValue(data);
    }


    /**
     * 查询
     */
    public void onQuery() {
        TTable regionTable = getTTable("RegionTable");
        TParm parm = new TParm();
        TParm regionParm = new TParm();
        //全院
        //if (all.getValue().equals("Y")) {
        //    parm = new TParm();
        //}
        //病区
        if (rStationCode.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("STATION_CODE").equals(null) ||
                getValue("STATION_CODE").toString().length() == 0) {
                this.messageBox("请选择病区");
                return;
            }
            parm.setData("STATION_CODE", getValue("STATION_CODE"));
        }
        //床位号
        if (rBedNo.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("BED_NO").equals(null) ||
                getValue("BED_NO").toString().length() == 0) {
                this.messageBox("请输入床位号");
                return;
            }
            parm.setData("BED_NO", getValue("BED_NO"));
        }
        //病案号
        if (rMrNo.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("MR_NO").equals(null) ||
                getValue("MR_NO").toString().length() == 0) {
                this.messageBox("请输入病案号");
                return;
            }
            String mrNo = PatTool.getInstance().checkMrno(TCM_Transform.
                    getString(getValue("MR_NO")));
            setValue("MR_NO",mrNo);
            
			// modify by huangtt 20160928 EMPI患者查重提示 start
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
			setValue("PAT_NAME",pat.getName());
			// modify by huangtt 20160928 EMPI患者查重提示 start
            
            parm.setData("MR_NO", this.getValueString("MR_NO"));
//            TParm selPatInfo = PatTool.getInstance().getInfoForMrno(getValueString("MR_NO"));
//            setValue("PAT_NAME",selPatInfo.getValue("PAT_NAME",0));
        }
        //住院号
        if (rIpdNo.getValue().equals("Y")) {
            parm = new TParm();
            if (getValue("IPD_NO").equals(null) ||
                getValue("IPD_NO").toString().length() == 0) {
                this.messageBox("请输入住院号");
                return;
            }
            setValue("IPD_NO",
                     PatTool.getInstance().checkIpdno(TCM_Transform.
                getString(getValue("IPD_NO"))));
            parm.setData("IPD_NO", getValue("IPD_NO"));
        }
        if (!"".equals(Operator.getRegion()))
            parm.setData("REGION_CODE", Operator.getRegion());
        regionParm = IBSOrdermTool.getInstance().selRegionStation(parm);
        caseParm = IBSOrdermTool.getInstance().selRegionStationCaseNo(parm);
        regionTable.setParmValue(regionParm);
    }

    /**
     * 清空
     */
    public void onClear() {

        initPage();
        setValue("STATION_CODE", "");
        setValue("BED_NO", "");
        setValue("MR_NO", "");
        setValue("IPD_NO", "");
        this.callFunction("UI|RegionTable|removeRowAll");
        this.callFunction("UI|StationTable|removeRowAll");
        this.callFunction("UI|OrderTable|removeRowAll");
        caseParm = new TParm();
        setValue("PAT_NAME","");
    }

    /**
     * 过账
     * @return boolean
     */
    public boolean onSave() {
        if(caseParm.getCount() <= 0)
        {
            this.messageBox("没有查询结果!");
            return false;
        }
        caseParm.setData("BILL_DATE", getValue("CUT_BILL_DATE"));
        caseParm.setData("FLG", "N");
        caseParm.setData("OPT_USER", Operator.getID());
        caseParm.setData("OPT_TERM", Operator.getIP());
//        System.out.println("月结入参"+caseParm);
        TParm result = TIOM_AppServer.executeAction(actionName,
            "onNewIBSBill", caseParm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        onClear();
        //保存成功
        this.messageBox("P0001");
        return true;
    }

//    /**
//     * 关闭事件
//     * @return boolean
//     */
//    public boolean onClosing() {
//        switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
//            case 0:
//                if (!onSave())
//                    return false;
//                break;
//            case 1:
//                break;
//            case 2:
//                return false;
//        }
//        return true;
//    }
}
