package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Pat;
import jdo.sys.SystemTool; 
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dongyang.ui.TRadioButton;
import jdo.bms.BMSApplyDTool;
import jdo.bms.BMSBloodTool;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.PatTool;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;
import jdo.bms.BMSSQL;
import jdo.adm.ADMTool;

/**
 * <p>
 * Title: 调阅交叉配血
 * </p>
 *
 * <p>
 * Description:调阅 交叉配血
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author guoy 2015.11.03
 * @version 1.0
 */
public class BMSBloodCrossDetailControl
    extends TControl {

    private TTable table_m;

    private String case_no;

    private String adm_type;

    private String bld_type;

    public BMSBloodCrossDetailControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();

        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            this.setValue("APPLY_NO", parm.getValue("APPLY_NO"));
            this.onApplyNoAction();
        }
    }


    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;TEST_BLD;BLOOD_TEXT;BLD_TYPE;BLD_CODE;"
            + "SUBCAT_CODE;BLOOD_NO;SHIT_FLG;CROSS_MATCH_S;CROSS_MATCH_L;"
            + "ANTI_A;ANTI_B;RESULT;TEST_DATE";
        this.clearValue(clearStr);
        this.getRadioButton("RH_A").setSelected(true);
        table_m.removeRowAll();
        case_no = "";
        adm_type = "";
        bld_type = "";
    }
   

    /**
     * 备血单号查询(回车事件)
     */
    public void onApplyNoAction() {
        String apply_no = this.getValueString("APPLY_NO");
        if (!"".equals(apply_no)) {
            TParm parm = new TParm();
            parm.setData("APPLY_NO", apply_no);
            TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
            //System.out.println("result==="+result);
            if (result.getCount("APPLY_NO") <= 0) {
                this.messageBox("备血单不存在");
                this.setValue("APPLY_NO", "");
                return;
            }
            this.setValue("PRE_DATE", result.getData("PRE_DATE", 0));
            this.setValue("USE_DATE", result.getData("USE_DATE", 0));
            this.setValue("END_DAYS", result.getData("END_DAYS", 0));
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));
            // 查询病患信息
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            String rh_type = pat.getBloodRHType();
            //System.out.println("rh_type===="+rh_type);
            if ("-".equals(rh_type)) {
                getRadioButton("RH_B").setSelected(true);
            }
            else if ("+".equals(rh_type)) {
                getRadioButton("RH_A").setSelected(true);
            }
            else {
                getRadioButton("RH_A").setSelected(false);
                getRadioButton("RH_B").setSelected(false);
            }
            this.setValue("BLOOD_TEXT", pat.getBloodType());
            this.setValue("BLD_TYPE", pat.getBloodType());

            this.setValue("TEST_BLD", result.getData("TEST_BLD", 0));
            case_no = result.getValue("CASE_NO", 0);
            adm_type = result.getValue("ADM_TYPE", 0);
            bld_type = pat.getBloodType();

            result = BMSApplyDTool.getInstance().onApplyQuery(parm);
            parm.setData("STATE_CODE", "1");
            result = BMSBloodTool.getInstance().onQueryBloodCrossExceptOut(parm);
            if (result != null && result.getCount() > 0) {
                table_m.setParmValue(result);
            }
        }
    }


    /**
     * 初始画面数据
     */
    private void initPage() {
        table_m = this.getTable("TABLE_M");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
}
