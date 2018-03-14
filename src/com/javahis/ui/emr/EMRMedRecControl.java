package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

/**
 * <p>Title: </p>
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
public class EMRMedRecControl extends TControl {
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            String type = ((TParm)obj).getValue("TYPE");
            if("1".equals(type)){
                getTRadioButton("ADDLOW").setSelected(true);
            }
            if("2".equals(type)){
                getTRadioButton("ADDAFTER").setSelected(true);
            }
            String fileName = ((TParm)obj).getValue("FILE_NAME");
            this.setValue("FILE_NAME",fileName);
            this.setValue("SYS_DATE",StringTool.getTimestamp(new Date()));
            this.setValue("ADM_DATE",StringTool.getString(((TParm)obj).getTimestamp("ADM_DATE"),"yyyy/MM/dd HH:mm:ss"));
            if(((TParm)obj).getValue("DS_DATE").length()==0){
                this.setValue("DS_DATE","");
            }else{
                this.setValue("DS_DATE",StringTool.getString(((TParm)obj).getTimestamp("DS_DATE"),"yyyy/MM/dd HH:mm:ss"));
            }
        }
    }
    /**
     * 确定
     */
    public void onOk(){
        //当前插入时间是否已经超过下限
        Timestamp sysDate = (Timestamp)this.getValue("SYS_DATE");
        long sDate = strToDate(StringTool.getString(sysDate,"yyyy/MM/dd HH:mm:ss"),"yyyy/MM/dd HH:mm:ss").getTime();
        long dDate = 0;
        if(this.getValueString("DS_DATE").length()!=0){
            Timestamp dsDate = StringTool.getTimestamp(this.getValueString("DS_DATE"),"yyyy/MM/dd HH:mm:ss");
            dDate = strToDate(StringTool.getString(dsDate,"yyyy/MM/dd HH:mm:ss"),"yyyy/MM/dd HH:mm:ss").getTime();
        }
        if(sDate>dDate&&dDate!=0){
            this.messageBox("已经超过时间下限！");
            return;
        }
        TParm returnParm = new TParm();
//        if("1".equals(type)){
//                getTRadioButton("ADDLOW").setSelected(true);
//            }
//            if("2".equals(type)){
//                getTRadioButton("ADDAFTER").setSelected(true);
//            }
        if(getTRadioButton("ADDAFTER").isSelected()){
            returnParm.setData("EMRADD_DATE",StringTool.getString(sysDate,"yyyy/MM/dd HH:mm:ss"));
        }
        if(getTRadioButton("ADDLOW").isSelected()){
            returnParm.setData("EMR_DATE",StringTool.getString(sysDate,"yyyy/MM/dd HH:mm:ss"));
        }
        this.setReturnValue(returnParm);
        this.closeWindow();
    }
    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * @param strDate String
     * @return Date
     */
    public Date strToDate(String strDate,String forMat) {
        SimpleDateFormat formatter = new SimpleDateFormat(forMat);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到TRadioButton
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getTRadioButton(String tag){
        return (TRadioButton)this.getComponent(tag);
    }
}
