package com.javahis.ui.ins;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;
import jdo.ins.INSUpLoadTool;
import jdo.sys.PatTool;

/**
 *
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:取消申报
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author xueyf 2012.02.11
 * @version 1.0
 */
public class INSCancelDeclareContrl extends TControl {
    /**
     * 用户代码 [1,城职],[2,城居]
     */
    String ctzCode;
    /**
     * 城职
     */
    public static String CHENGZHI = "1";
    /**
     * 城居
     */
    public static String CHENGJU = "2";

    public void onInit() {
        super.onInit();
        //初始化界面数据
        initPage();
    }

    /**
     * 初始化界面数据
     */
    public void initPage() {
        this.callFunction("UI|tPanel_0|startDate|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|tPanel_0|endDate|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|tPanel_0|sdiseaseCode|setValue",
                          "0");

    }

    /**
     * 查询
     */
    public void onQuery() {
        String mrNo = "";
        mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        
     // modify by huangtt 20160930 EMPI患者查重提示 start
        Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
     // modify by huangtt 20160930 EMPI患者查重提示 end
        
        setValue("MR_NO", mrNo);
        if (StringUtil.isNullString(getText("startDate"))) {
            this.messageBox("请录入入院开始日期。");
            return;
        }
        if (StringUtil.isNullString(getText("endDate"))) {
            this.messageBox("请录入入院结束日期。");
            return;
        }

        TParm parm = new TParm();
        TParm data = new TParm();
        String sdiseaseCode = (String) ((TComboBox) getComponent("sdiseaseCode"))
                              .getValue();
        ctzCode = (String) ((TComboBox) getComponent("ctzCode")).getValue();
        if (StringUtil.isNullString(sdiseaseCode)) {
            this.messageBox("请选择病人类型。");
            return;
        }
        if (StringUtil.isNullString(ctzCode)) {
            this.messageBox("请选择人员类别。");
            return;
        }
        parm.setData("REGION_CODE", Operator.getRegion());
        if (sdiseaseCode.equals("0")) {

            parm.setData("SDISEASECODENULL", "0");
        } else {

            parm.setData("SDISEASECODE", "1");
        }
        parm.setData("CTZCODE", ctzCode);
        parm.setData("STARTDATE", getText("startDate"));
        parm.setData("ENDDATE", getText("endDate"));
        if (!"".equals(this.getValueString("MR_NO")) &&
            this.getValueString("MR_NO") != null)
            parm.setData("MR_NO", this.getValue("MR_NO"));
        data = TIOM_AppServer.executeAction(
                "action.ins.INSCancelDeclareAction", "selectdata", parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLE|setParmValue", data);
    }

    /**
     * 取消申报
     */
    public void onCancel() {
        int row = ((TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择需要取消的病患。");
            return;
        }
        TParm data = ((TTable) getComponent("TABLE")).getParmValue().getRow(row);
        String sql = " SELECT LOCAL_FLG,CHECK_FLG FROM INS_IBS"+
                     " WHERE CASE_NO = '" + data.getValue("CASE_NO") + "'";       
        TParm flg = new TParm(TJDODBTool.getInstance().select(sql));
        if(!flg.getValue("CHECK_FLG",0).equals("0")){
        	 this.messageBox("异地病人已申请对账,不能取消申报");
             return;
        }   
        TParm tableData = ((TTable) getComponent("TABLE")).getParmValue();
        TParm parm = new TParm();
        Map map = (Map) tableData.getRow(row).getData().get("Data");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            parm.addData(entry.getKey() + "", entry.getValue());
        }
        TParm result = new TParm();
        parm.setCount(parm.getCount("MR_NO"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm nhiHospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        String nhiHospCode = nhiHospParm.getValue("NHI_NO", 0);
        parm.setData("HOSP_NHI_NO", nhiHospCode);
        parm.setData("LOCAL_FLG", flg.getValue("LOCAL_FLG",0));//异地标记
        if (ctzCode.equals(CHENGZHI)||
           (ctzCode.equals(CHENGJU)&&
            flg.getValue("LOCAL_FLG",0).equals("N"))) {//本地城职 、异地城职、异地城居
            result = cancelByCZ(parm);

        } else if(ctzCode.equals(CHENGJU)&&
                flg.getValue("LOCAL_FLG",0).equals("Y")) {//本地城居
            result = cancelByCJ(parm);
        }
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        this.messageBox("P0005");
        this.onQuery();

    }

    /**
     * 取消申报 ————城职
     * @param tableData TParm
     * @return TParm
     */
    private TParm cancelByCZ(TParm tableData) {

        return TIOM_AppServer.executeAction(
                "action.ins.INSCancelDeclareAction", "cancelByCZ", tableData);
    }

    /**
     * 取消申报 ————城居
     * @param tableData TParm
     * @return TParm
     */
    private TParm cancelByCJ(TParm tableData) {
        return TIOM_AppServer.executeAction(
                "action.ins.INSCancelDeclareAction", "cancelByCJ", tableData);
    }

    /**
     *清空
     */
    public void onClear() {
        initPage();
        this.setValue("ctzCode", "");
        this.setValue("MR_NO", "");
        this.callFunction("UI|TABLE|clearSelection");

    }

}
