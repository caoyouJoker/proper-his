package com.javahis.ui.mro;

import java.sql.Timestamp;

import jdo.mro.MROLendRegTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;


/**
 * <p>Title: 借阅注册</p>
 *
 * <p>Description: 借阅注册</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROLendRegControl
    extends TControl {
    private TParm Parameter;//存放参数
    private String CASE_NO;//记录参数中的CASE_NO
    //初始化
    public void onInit() {
        super.onInit();
        //接收参数
        Object obj = this.getParameter();
        if (obj != null) {
            if (obj instanceof TParm) { //判断是否是TParm
                Parameter = (TParm) obj;
            }
            else {
                this.closeWindow();
            }
        }
        else {
            this.closeWindow();
        }
        pageInit();
    }

    //页面初始化
    private void pageInit() {
        this.setValue("IPD_NO", Parameter.getValue("IPD_NO"));
        this.setValue("MR_NO",Parameter.getValue("MR_NO"));
        this.setValue("PAT_NAME",Parameter.getValue("PAT_NAME"));
        this.setValue("VS_CODE",Parameter.getValue("VS_CODE"));
        CASE_NO = Parameter.getValue("CASE_NO");
        this.setValue("QUE_DATE",SystemTool.getInstance().getDate());
        this.setValue("REQ_DEPT",Operator.getDept());//借阅人员部门
        this.setValue("MR_PERSON",Operator.getID());//借阅人员
    }
    /**
     * 保存
     */
    public void onSave(){
//        Map taskVariables = new HashMap();
//        taskVariables.put("CASE NO", CASE_NO);
//        taskVariables.put("IF_FORMER", "正式");
//        taskVariables.put("APPLIER", Operator.getName());
//        JbpmTool.createStartTaskInstance("BORROW CASE",Operator.getID(),taskVariables);
//        this.messageBox("保存成功！");
        TParm mrv = new TParm();
        mrv.setData("MR_NO",this.getValue("MR_NO"));
        TParm mrvResult = MROQueueTool.getInstance().selectMRO_MRV_TECH(mrv);
        if(mrvResult.getErrCode()<0){
        	this.messageBox_("查询归档信息失败！" + mrvResult.getErrName() + mrvResult.getErrCode() + mrvResult.getErrText());
            return;
        }
        if(mrvResult.getCount()<=0){
        	this.messageBox_("此病历无归档信息，不可借阅");
            return;
        }
        if(!"3".equalsIgnoreCase(mrvResult.getValue("CHECK_FLG",0))){
            this.messageBox_("此病历尚未归档，不可借阅");
            return;
        }
        if(((TTextFormat)this.getComponent("QUE_DATE")).getValue() == null){
        	this.messageBox("请填写借阅日期！");
        	this.grabFocus("QUE_DATE");
        	return;
        }
        if(this.getValue("LEND_CODE") == null || "".equals(this.getValue("LEND_CODE").toString().trim())){
        	this.messageBox("请填写借阅原因！");
        	this.grabFocus("LEND_CODE");
        	return;
        }
        
        TParm selParm = new TParm();
        selParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm selResultParm = MROLendRegTool.getInstance().selectQueue(selParm);
        if(selResultParm.getCount() > 0 && !selResultParm.getData("ISSUE_CODE", 0).toString().equals("2")){
        	this.messageBox("该病案借出尚未归还，不能重复借出！");
        	return;
        }
        TNull tn = new TNull(Timestamp.class);
        TParm parm = this.getParmForTag("IPD_NO;MR_NO;LEND_CODE;MR_PERSON;REQ_DEPT");
        String QUE_SEQ = SystemTool.getInstance().getNo("ALL", "MRO", "QUE_SEQ",
        "QUE_SEQ"); //调用取号原则
        parm.setData("QUE_SEQ",QUE_SEQ);//借阅号
        parm.setData("ADM_HOSP","");//借阅院区（门急诊用）
        parm.setData("SESSION_CODE","");//时段（门急诊用）
        parm.setData("ISSUE_CODE","0");//出库状态：0 登记未出库
        parm.setData("RTN_DATE",((TTextFormat)this.getComponent("RTN_DATE")).getValue()==null?tn:((TTextFormat)this.getComponent("RTN_DATE")).getValue());//应归还日期
        parm.setData("QUE_DATE",((TTextFormat)this.getComponent("QUE_DATE")).getValue());//借阅日期
        parm.setData("CAN_FLG","N");//标记，取消否
        parm.setData("ADM_TYPE","");//门急住别 病历调阅使用 目前不使用
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("QUE_HOSP",Operator.getRegion());//目前病案所在区域
        parm.setData("DUE_DATE",tn);//出院后，病历应该归档时间暂时不填
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("IN_PERSON", "");
        parm.setData("IN_DATE", tn);
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = MROLendRegTool.getInstance().insertQueue(parm);
        if(result.getErrCode()<0){
            this.messageBox("登记失败！"+ result.getErrName() + result.getErrText());
            return;
        }
        this.messageBox("登记完毕！");
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("LEND_CODE;QUE_DATE;RTN_DATE");
    }
    /**
     * 选择“借阅原因”后自动填写 天数
     */
    public void getLendDays(){
        TParm result = MROLendRegTool.getInstance().queryLendDays(this.getValueString("LEND_CODE"));
        int days = result.getInt("LEND_DAY",0);
        Timestamp rueDate = StringTool.rollDate((Timestamp)((TTextFormat)this.getComponent("QUE_DATE")).getValue(),(long)days);
        this.setValue("RTN_DATE",rueDate);
    }
}
