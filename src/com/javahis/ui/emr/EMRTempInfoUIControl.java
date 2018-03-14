package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.StringUtil;

/**
 * <p>Title:模板属性 </p>
 *
 * <p>Description: 模板属性</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRTempInfoUIControl extends TControl {

    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
        //this.messageBox("aaaa"+(String)((TParm)obj).getData("NEW_SUBCLASS_CODE"));
        if(obj!=null){
            TParm parm = (TParm)obj;
            if(parm.getData("OP")!=null&&parm.getData("OP").equals("ADD")){
                 String subClassCode=((String)parm.getData("SUBCLASS_CODE")).substring(0,9);
                 String subClassSeq=((String)parm.getData("SUBCLASS_CODE")).substring(9,((String)parm.getData("SUBCLASS_CODE")).length());
                 this.setValue("SUBCLASS_CODE",subClassCode);
                 this.setValue("SUBCLASS_SEQ",subClassSeq);
                 this.setValue("SEALED_N", true);

            }else{
                this.setValue("USER_ID",parm.getData("USER_ID"));
                this.setValue("DEPT_CODE",parm.getData("DEPT_CODE"));
                this.setValue("EMT_FILENAME",parm.getData("EMT_FILENAME"));
                this.setValue("SUBCLASS_DESC",parm.getData("SUBCLASS_DESC"));
                this.setValue("RUN_PROGARM", parm.getData("RUN_PROGARM"));
                this.setValue("SUBTEMPLET_CODE", parm.getData("SUBTEMPLET_CODE"));
                this.setValue("CLASS_STYLE", parm.getData("CLASS_STYLE"));
                this.setValue("PY1",parm.getData("PY1"));
                this.setValue("IPD_FLG",parm.getData("IPD_FLG"));
                this.setValue("HRM_FLG",parm.getData("HRM_FLG"));
                this.setValue("OPD_FLG",parm.getData("OPD_FLG"));
                this.setValue("EMG_FLG",parm.getData("EMG_FLG"));
                this.setValue("OIDR_FLG",parm.getData("OIDR_FLG"));
                this.setValue("NSS_FLG",parm.getData("NSS_FLG"));
                this.setValue("DOCUMENT_TYPE",parm.getData("DOCUMENT_TYPE"));
                
                this.setValue("REF_FLG",parm.getData("REF_FLG"));
//                this.messageBox("---PATIENT_SIGN_FLG="+parm.getData("PATIENT_SIGN_FLG"));
                this.setValue("PATIENT_SIGN_FLG",parm.getData("PATIENT_SIGN_FLG"));// add by Eric 20170621 是否需要患者签名
                this.setValue("INTERNSHIP_FLG",parm.getData("INTERNSHIP_FLG"));// add by wangqing 2017804 规培适用
                this.setValue("PATIENT_SIGN_TWO_FLG",parm.getData("PATIENT_SIGN_TWO_FLG"));// add by wangqing 20170926 患者可否二次签名
                if(parm.getBoolean("SEALED_TYPE")){
                	this.setValue("SEALED_Y", true);
                }else{
                	this.setValue("SEALED_N", true);
                }
                

                String strSubClassCode= ((String)parm.getData("SUBCLASS_CODE"));
                if(strSubClassCode.length()>=9){
                    String subClassCode=((String)parm.getData("SUBCLASS_CODE")).substring(0,9);
                    String subClassSeq=((String)parm.getData("SUBCLASS_CODE")).substring(9,((String)parm.getData("SUBCLASS_CODE")).length());
                    this.setValue("SUBCLASS_CODE",subClassCode);
                    this.setValue("SUBCLASS_SEQ",subClassSeq);
                }else{
                    this.setValue("SUBCLASS_CODE",strSubClassCode);
                }
                this.setValue("SYSTEM_CODE",parm.getData("SYSTEM_CODE"));
                setTRadioButtonStatus();

            }

        }
    }
    /**
     * 确认
     */
    public void onOK(){
        if(this.getValueString("SUBCLASS_SEQ").length()==0){
             this.messageBox("请填写模版序号！");
            return;
        }
        if(this.getValueString("EMT_FILENAME").length()!=0&&this.getValueString("SUBCLASS_DESC").length()!=0){

            TParm parm = new TParm();
            parm.setData("USER_ID",this.getValueString("USER_ID"));
            parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
            parm.setData("EMT_FILENAME",this.getValueString("EMT_FILENAME").trim());
            parm.setData("SUBCLASS_DESC",this.getValueString("SUBCLASS_DESC"));
            parm.setData("RUN_PROGARM",this.getValueString("RUN_PROGARM"));
            parm.setData("SUBTEMPLET_CODE",this.getValueString("SUBTEMPLET_CODE"));
            parm.setData("CLASS_STYLE",this.getValueString("CLASS_STYLE"));
            parm.setData("PY1",this.getValueString("PY1"));
            parm.setData("IPD_FLG",getFlgStatus("IPD_FLG"));
            parm.setData("HRM_FLG",getFlgStatus("HRM_FLG"));
            parm.setData("OPD_FLG",getFlgStatus("OPD_FLG"));
            parm.setData("EMG_FLG",getFlgStatus("EMG_FLG"));
            parm.setData("OIDR_FLG",getFlgStatus("OIDR_FLG"));
            parm.setData("NSS_FLG",getFlgStatus("NSS_FLG"));
            parm.setData("SYSTEM_CODE",this.getValueString("SYSTEM_CODE"));
            parm.setData("TITLE_TYPE",getTitleType());
            parm.setData("SUBCLASS_CODE",this.getValueString("SUBCLASS_CODE")+this.getValueString("SUBCLASS_SEQ"));
            parm.setData("DOCUMENT_TYPE",this.getValueString("DOCUMENT_TYPE"));
            parm.setData("REF_FLG",this.getValueString("REF_FLG"));          
            parm.setData("PATIENT_SIGN_FLG",this.getValueString("PATIENT_SIGN_FLG"));// add by wangqing 是否需要患者签名           
            parm.setData("INTERNSHIP_FLG",this.getValueString("INTERNSHIP_FLG"));// add by wangqing 20170804 规培适用
            parm.setData("PATIENT_SIGN_TWO_FLG",this.getValueString("PATIENT_SIGN_TWO_FLG"));// add by wangqing 20170926 患者可否二次签名
            if("Y".equalsIgnoreCase(this.getValueString("SEALED_Y"))){
            	 parm.setData("SEALED_TYPE","Y");
            }else if("Y".equalsIgnoreCase(this.getValueString("SEALED_N"))){
            	 parm.setData("SEALED_TYPE","N");
            }
            
            this.setReturnValue(parm);
            this.closeWindow();
        }else{
            this.messageBox("请填写模版名称和显示名称！");
            return;
        }
    }
    /**
     * 得到标题类型
     * @return String
     */
    public String getTitleType(){
        if(getTRadioButton("NULLTITLE").isSelected()){
            return "1";
        }
        if(getTRadioButton("TITLE").isSelected()){
            return "2";
        }
        if(getTRadioButton("IMGTITLE").isSelected()){
            return "3";
        }
        if(getTRadioButton("A5NULL").isSelected()){
            return "4";
        }
        if(getTRadioButton("A5TITLE").isSelected()){
            return "5";
        }
        if(getTRadioButton("A5IMG").isSelected()){
            return "6";
        }
        return "1";
    }
    /**
     * 设置模版状态
     */
    public void setTRadioButtonStatus(){
        getTRadioButton("NULLTITLE").setEnabled(false);
        getTRadioButton("TITLE").setEnabled(false);
        getTRadioButton("IMGTITLE").setEnabled(false);
        getTRadioButton("A5NULL").setEnabled(false);
        getTRadioButton("A5TITLE").setEnabled(false);
        getTRadioButton("A5IMG").setEnabled(false);
    }
    /**
     * 得到TRadioButton
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getTRadioButton(String tag){
        return (TRadioButton)this.getComponent(tag);
    }
    /**
     * 取消
     */
    public void onCancel(){
        this.closeWindow();
    }
    /**
     * 拼音事件
     */
    public void onPY(){
        String py1 = StringUtil.onCode(this.getValueString("SUBCLASS_DESC"));
        this.setValue("PY1",py1);
    }
    /**
     * 得到TCheckBox状态
     * @param tag String
     * @return String
     */
    public String getFlgStatus(String tag){
        if(getTCheckBox(tag).isSelected())
            return "Y";
        else
            return "N";
    }
    /**
     * 得到TCheckBox
     * @param tag String
     * @return TCheckBox
     */
    public TCheckBox getTCheckBox(String tag){
        return (TCheckBox)this.getComponent(tag);
    }
}
