package com.javahis.ui.hrm;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 身份证验证 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class HRMIDCheckUIControl extends TControl {

    /**
     * 初始化
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));
            this.setValue("STAFF_NO", parm.getValue("STAFF_NO"));
            this.setValue("IDNO", parm.getValue("IDNO"));
            this.setValue("FOREIGNER_FLG", parm.getValue("FOREIGNER_FLG"));
            this.setValue("SEX_CODE", parm.getValue("SEX_CODE"));
            this.setValue("BIRTHDAY", parm.getValue("BIRTHDAY"));
        }
    }

    /**
     * 返回
     */
    public void onBack() {
        String foreignerFlg = this.getValueString("FOREIGNER_FLG");
        String idNo = this.getValueString("IDNO");
        if ("N".equalsIgnoreCase(foreignerFlg)
                && (!idNo.matches("\\d{15}|\\d{18}|\\d{14}[xX]|\\d{17}[xX]") // add by wanglong 20130530
                || !StringTool.isId(idNo))) {
            this.messageBox("身份证错误！若为外国人，请选中“其他证件”");
            return;
        }
        String sexCode = this.getValueString("SEX_CODE");
        if (sexCode.equals("")) {
            this.messageBox("性别不能为空！");
            return;
        }
        String birthDay = this.getText("BIRTHDAY");
        if (birthDay.equals("")) {
            this.messageBox("出生日期不能为空！");
            return;
        }
        TParm returnParm = new TParm();
        returnParm.setData("IDNO", idNo);
        returnParm.setData("FOREIGNER_FLG", foreignerFlg);
        if ("N".equalsIgnoreCase(foreignerFlg)) {// 国人
            String sex = StringTool.isMaleFromID(idNo);
            returnParm.setData("SEX_CODE", sex);
            Timestamp birthdayTime = StringTool.getBirdayFromID(idNo);
            returnParm.setData("BIRTHDAY", birthdayTime);
        } else {// 外国人
            returnParm.setData("SEX_CODE", sexCode);
            returnParm.setData("BIRTHDAY", StringTool.getTimestamp(birthDay, "yyyyMMdd"));
        }
        this.setReturnValue(returnParm);
        this.closeWindow();
    }

    /**
     * 身份证回车事件
     */
    public void onInclude() {
        if ("N".equalsIgnoreCase(this.getValueString("FOREIGNER_FLG"))
                && (!this.getValueString("IDNO").matches("\\d{15}|\\d{18}|\\d{14}[xX]|\\d{17}[xX]")  // add by wanglong 20130530
                || !StringTool.isId(this.getValueString("IDNO")))) {
            this.messageBox("身份证错误！");
            return;
        }
        String sex = StringTool.isMaleFromID(this.getValueString("IDNO"));
        this.setValue("SEX_CODE", sex);
        Timestamp birthdayTime;
        birthdayTime = StringTool.getBirdayFromID(this.getValueString("IDNO"));
        this.setValue("BIRTHDAY", birthdayTime);
    }
}
