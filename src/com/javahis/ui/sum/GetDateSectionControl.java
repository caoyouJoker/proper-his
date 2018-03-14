package com.javahis.ui.sum;

import java.sql.*;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.ui.*;
import jdo.adm.*;

/**
 * <p>Title: 得到选择的日期区间</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */
public class GetDateSectionControl
    extends TControl {

    TTextFormat stratDate, endDate;
    TButton ok, cancle;
    String caseNo="";

    public GetDateSectionControl() {
    }

    public void onInit() {

        super.onInit();
        myInitCtl();

    }
    /**
     *  得到控件
     */
    public void myInitCtl() {

        stratDate = (TTextFormat)this.getComponent("START_DATE");
        endDate = (TTextFormat)this.getComponent("END_DATE");
        ok = (TButton)this.getComponent("OK");
        cancle = (TButton)this.getComponent("CANCLE");

        //初始化数据
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //用当前时间初始化时间控件
        TParm outsideParm = (TParm)this.getParameter();
        stratDate.setValue(outsideParm.getData("IN_DATE"));
        endDate.setValue(date);

    }

    /**
     * 选择打印
     */
    public void onOK() {

        //该选择时间区间是否有效
//        if(!ckeckDate("")){
//            this.messageBox("选择时间错误！");
//            return;
//        }
        //获得选择的时间区间
        TParm choiceDate=getChoiceDate();

        this.setReturnValue(choiceDate);
        this.closeWindow();
    }

    /**
     * 选择取消
     */
    public void onCANCLE() {
        this.closeWindow();
    }

    /**
     * 获得选择的时间
     * @return TParm
     */
    private TParm getChoiceDate(){
        TParm result = new TParm();
        result.setData("START_DATE",stratDate.getValue());
        result.setData("END_DATE",endDate.getValue());
        return result;
    }

    /**
     * 检查该选择时间是否对该病人有效
     * startDate不可以超前于病患入院时间
     */
    public boolean ckeckDate(String caseNo){

        TParm getInDate = new TParm();
        getInDate.setData("CASE_NO", caseNo);
        TParm result = ADMTool.getInstance().getADM_INFO(getInDate);
        //拿到选择的时间和住院时间比较
        long inDate = ( (Timestamp) result.getData("IN_DATE", 0)).getTime();
        long selStartDate = ( (Timestamp) stratDate.getValue()).getTime();

        if (inDate > selStartDate)
            return false;

        //检测截止时间是否在现在时间以后

        return true;

    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

}
