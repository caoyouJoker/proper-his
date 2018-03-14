package com.javahis.ui.opd;

import com.dongyang.control.*;
import jdo.bil.BIL;
import com.dongyang.util.TypeTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.ekt.EKTIO;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.odo.OPDAbnormalRegTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.reg.ClinicRoomTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 非常态门诊</p>
 *
 * <p>Description: 非常态门诊</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-10-26
 * @version 1.0
 */
public class OPDAbnormalRegControl
    extends TControl {
    private String admType = "O";//门急别
    private Pat pat;
    private String CASE_NO;
    private String tredeNo;//医疗卡交易号
    public void onInit(){
        pageInit();
    }
    /**
     * 页面初始化
     */
    public void pageInit(){
        initCombo();
        initClinicRoom();
    }
    /**
     * 初始化combo
     */
    public void initCombo(){
        //初始化日期
        this.setValue("DATE",SystemTool.getInstance().getDate());
        //初始化时段
        String sessionCode = SessionTool.getInstance().getDefSessionNow(admType,
            Operator.getRegion());
        this.setValue("SESSION_CODE", sessionCode);
        //初始化科别，人员
        this.setValue("DEPT_CODE",Operator.getDept());
        this.setValue("DR_CODE",Operator.getID());
        this.setValue("SERVICE_LEVEL","1");
    }
    /**
     * 初始化诊间combo
     */
    public void initClinicRoom(){
        TParm pa = new TParm();
        pa.setData("ADM_TYPE","O");
        pa.setData("ADM_DATE",StringTool.getString((Timestamp)this.getValue("DATE"),"yyyyMMdd"));
        pa.setData("SESSION_CODE",this.getValue("SESSION_CODE"));
        pa.setData("REGION_CODE",Operator.getRegion());
        TParm parm = ClinicRoomTool.getInstance().getNotUseForODO(pa);
        TTextFormat tf = (TTextFormat)this.getComponent("CLINICROOM_NO");
        tf.setPopupMenuData(parm);
        tf.setComboSelectRow();
        tf.popupMenuShowData();
    }
    /**
     * 初始化病患信息
     */
    public void onMrNo(){
        pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        if(pat==null){
            this.messageBox_("无此病患");
            return;
        }
        this.setValue("MR_NO",pat.getMrNo());
        this.setValue("SEX_CODE",pat.getSexCode());
        this.setValue("PAT_NAME",pat.getName());
        this.setValue("CTZ1_CODE",pat.getCtz1Code());
        this.setValue("CTZ2_CODE",pat.getCtz2Code());
        this.setValue("CTZ3_CODE",pat.getCtz3Code());
        this.setValue("BIRTHDAY",pat.getBirthday());
    }
    /**
     * 诊别选择事件
     */
    public void onClickClinicType(){
        double reg_fee = BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
                                             "REG_FEE",getValueString("CTZ1_CODE"),
                                             getValueString("CTZ2_CODE"),
                                             getValueString("CTZ3_CODE"),
                                             this.getValueString("SERVICE_LEVEL"));
        //挂号费
        this.setValue("REG_FEE", reg_fee);
        double clinic_fee = BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
                                                "CLINIC_FEE",getValueString("CTZ1_CODE"),
                                                getValueString("CTZ2_CODE"),
                                                getValueString("CTZ3_CODE"),
                                                this.getValueString("SERVICE_LEVEL"));
        //诊查费
        this.setValue("CLINIC_FEE", clinic_fee);
        //总费用
        setValue("AR_AMT", reg_fee + clinic_fee);
    }
    /**
     * 医疗卡读卡
     */
    public void onEKT(){
        TParm patParm = EKTIO.getInstance().getPat();
//        TParm patParm = new TParm();
//        patParm.setData("CARD_NO", "123");
//        patParm.setData("NAME", "xingming");
//        patParm.setData("SEX", "男");
//        patParm.setData("MR_NO", "000000000033");
//        patParm.setData("CURRENT_BALANCE", "123");
//        // System.out.println("读卡参数："+patParm);
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        if (patParm.getValue("MR_NO") == null ||
            patParm.getValue("MR_NO").length() == 0){
            this.messageBox_("读卡失败");
        }
        else{
            this.setValue("MR_NO",patParm.getValue("MR_NO"));
            this.onMrNo();
        }
    }
    /***
     * 保存
     */
    public void onSave(){
        //检核数据
        if(!checkData()){
            return;
        }
        CASE_NO = SystemTool.getInstance().getNo("ALL", "REG","CASE_NO", "CASE_NO");
        //保存医疗卡
        if (!this.onEktSave("Y")) {
            return;
        }
        //保存挂号信息
        TParm result= TIOM_AppServer.executeAction(
		        "action.opd.OPDAbnormalRegAction", "saveReg", this.getSaveData());
        if(result.getErrCode()!=0){
            EKTIO.getInstance().unConsume(tredeNo,this);
            this.messageBox("E0005");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm re = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
        this.setReturnValue(re);
        this.messageBox("P0005");
        this.closeWindow();
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("CLINICTYPE_CODE;MR_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SERVICE_LEVEL;REG_FEE;CLINIC_FEE;AR_AMT");
        pat = null;
        CASE_NO = "";
        tredeNo="";
        initCombo();
    }
    /**
     * 检核保存数据是否符合条件
     * @return boolean
     */
    private boolean checkData(){
        if(pat==null){
            return false;
        }
        if(this.getValueString("DATE").length()==0){
            this.messageBox_("请选择日期");
            this.grabFocus("DATE");
            return false;
        }
        if(this.getValueString("SESSION_CODE").length()==0){
            this.messageBox_("请选择时段");
            this.grabFocus("SESSION_CODE");
            return false;
        }
        if(this.getValueString("DEPT_CODE").length()==0){
            this.messageBox_("请选择科别");
            this.grabFocus("DEPT_CODE");
            return false;
        }
        if(this.getValueString("DR_CODE").length()==0){
            this.messageBox_("请选择医生");
            this.grabFocus("DR_CODE");
            return false;
        }
        if(this.getValueString("CLINICTYPE_CODE").length()==0){
            this.messageBox_("请选择诊别");
            this.grabFocus("CLINICTYPE_CODE");
            return false;
        }
        if(this.getValueString("CLINICROOM_NO").length()==0){
            this.messageBox_("请选择诊间");
            this.grabFocus("CLINICROOM_NO");
            return false;
        }
        if(this.getValueString("CTZ1_CODE").length()==0){
            this.messageBox_("病患主身份不可为空");
            this.grabFocus("CTZ1_CODE");
            return false;
        }
        if(this.getValueString("SERVICE_LEVEL").length()==0){
            this.messageBox_("服务等级不可为空");
            this.grabFocus("SERVICE_LEVEL");
            return false;
        }
        return true;
    }
    /**
     * 获取要保存的数据
     * @return TParm
     */
    private TParm getSaveData(){
        TParm parm = new TParm();
        //生成CASE_NO
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("ADM_TYPE",admType);
        parm.setData("MR_NO",pat.getMrNo());
        parm.setData("REGION_CODE",Operator.getRegion());
        parm.setData("ADM_DATE",StringTool.getString((Timestamp)this.getValue("DATE"),"yyyyMMdd"));
        parm.setData("REG_DATE",SystemTool.getInstance().getDate());
        parm.setData("SESSION_CODE",this.getValue("SESSION_CODE"));
        parm.setData("CLINICTYPE_CODE",this.getValue("CLINICTYPE_CODE"));
        parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
        parm.setData("DR_CODE",this.getValue("DR_CODE"));
        parm.setData("REALDEPT_CODE",this.getValue("DEPT_CODE"));
        parm.setData("REALDR_CODE",this.getValue("DR_CODE"));
        parm.setData("APPT_CODE","1");//当诊
        parm.setData("VISIT_CODE","0");//初诊
        parm.setData("REGMETHOD_CODE", "A");//挂号方式 默认现场挂号
        parm.setData("CTZ1_CODE",this.getValue("CTZ1_CODE"));
        parm.setData("CTZ2_CODE",this.getValue("CTZ2_CODE"));
        parm.setData("CTZ3_CODE",this.getValue("CTZ3_CODE"));
        parm.setData("ARRIVE_FLG","Y");//报到注记
        parm.setData("ADM_REGION",Operator.getRegion());
        parm.setData("ADM_STATUS","1");//就诊进度  1：已挂号
        parm.setData("REPORT_STATUS","1");//报告状态  1全部未完成
        parm.setData("SEE_DR_FLG","N");//看诊注记
        parm.setData("HEAT_FLG","N");//发热注记
        parm.setData("SERVICE_LEVEL",this.getValue("SERVICE_LEVEL"));//服务等级
        parm.setData("TREDE_NO",tredeNo);//医疗卡交易号
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        parm.setData("WEIGHT",0);
        parm.setData("HEIGHT",0);
        parm.setData("CLINICROOM_NO",this.getValue("CLINICROOM_NO"));//诊间
        return parm;
    }

    /**
     * 医疗卡保存
     * @param FLG String
     * @return boolean
     */
    public boolean onEktSave(String FLG) {
        int type = 0;
        TParm parm = new TParm();
        //如果使用医疗卡，并且扣款失败，则返回不保存
        if (EKTIO.getInstance().ektSwitch()) { //医疗卡开关，记录在后台config文件中
            parm = onOpenCard(FLG);
            if (parm == null) {
                this.messageBox("E0115");
                return false;
            }
            type = parm.getInt("OP_TYPE");
            if (type == 3) {
                this.messageBox("E0115");
                return false;
            }
            if (type == 2) {
                return false;
            }
            tredeNo = parm.getValue("TREDE_NO");
        }
        else {
            this.messageBox_("医疗卡接口未开启");
            return false;
        }
        return true;
    }
    /**
     * 打开医疗卡
     * @param FLG String
     * @return TParm
     */
    public TParm onOpenCard(String FLG) {
        //准备送入医疗卡接口的数据
        TParm orderParm = new TParm();
        orderParm.addData("RX_NO", "REG"); //写固定值
        orderParm.addData("ORDER_CODE", "REG"); //写固定值
        orderParm.addData("SEQ_NO", "1"); //写固定值
        orderParm.addData("AMT", TypeTool.getDouble(getValue("AR_AMT")));
        orderParm.addData("EXEC_FLG", "N"); //写固定值
        orderParm.addData("RECEIPT_FLG", "N"); //写固定值
        orderParm.addData("BILL_FLG", FLG);
        orderParm.setData("MR_NO", pat.getMrNo());
        orderParm.setData("BUSINESS_TYPE", "REG");
        orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
        TParm orderSumParm=new TParm();
		orderSumParm.setData("TOT_AMT",0, TypeTool.getDouble(getValue("AR_AMT")));
		orderParm.setData("orderSumParm", orderSumParm.getData()); // 此病患所有收费医嘱包括已经打票的
		orderParm.setData("ektSumParm", (new TParm()).getData()); // 医疗卡已经收费的数据
        //送医疗卡，返回医疗卡的回传值
        TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm, CASE_NO, this);
        return parm;
    }

}
