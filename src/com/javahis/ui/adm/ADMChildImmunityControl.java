package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.adm.ADMChildImmunityTool;
import jdo.sys.Operator;
import javax.swing.SwingUtilities;

/**
 * <p>Title: 新生儿免疫信息</p>
 *
 * <p>Description: 新生儿免疫信息</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-12-10
 * @version 1.0
 */
public class ADMChildImmunityControl
    extends TControl {
    String CASE_NO = "";//记录就诊序号
    String IPD_NO = "";//住院号
    String MR_NO = "";//病案号
    TParm data;
    String saveType = "new";//保存方式  new：新增   update：修改
    /**
     * 初始化
     */
    public void onInit(){
        super.onInit();
        //模拟参数
//        TParm o = new TParm();
//        o.setData("CASE_NO","091113000003");
//        o.setData("IPD_NO","000000000092");
//        o.setData("MR_NO","000000000093");
//        Object obj = o;
        Object obj = this.getParameter();
        if(obj instanceof TParm){
            TParm parm = (TParm)obj;
            CASE_NO = parm.getValue("CASE_NO");
            IPD_NO = parm.getValue("IPD_NO");
            MR_NO = parm.getValue("MR_NO");
        }
        //判断是否新生儿
        if(!ADMChildImmunityTool.getInstance().checkNEW_BORN_FLG(CASE_NO)){
            this.messageBox_("该病患不是新生儿");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        close();
                    }
                    catch (Exception e) {
                    }
                }
            });
        }
        query();
    }
    /**
     * 关闭窗口
     */
    public void close(){
         this.closeWindow();
    }
    /**
     * 保存
     */
    public void onSave(){
        if("new".equals(saveType)){
            insert();
        }else if("update".equals(saveType)){
            update();
        }
    }
    /**
     * 新增数据
     */
    private void insert(){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("IPD_NO",IPD_NO);
        parm.setData("MR_NO",MR_NO);
        //评分
        if(this.getValue("APGAR_NUMBER")==null)
            parm.setData("APGAR_NUMBER","");
        else
            parm.setData("APGAR_NUMBER",this.getValueInt("APGAR_NUMBER"));

        //婴儿卡介苗
        if("Y".equals(this.getValueString("BABY_Y"))){
            parm.setData("BABY_VACCINE_FLG", "Y");
        }else if("Y".equals(this.getValueString("BABY_N"))){
            parm.setData("BABY_VACCINE_FLG", "N");
        }
        //乙肝疫苗
        if("Y".equals(this.getValueString("LIVER_Y"))){
            parm.setData("LIVER_VACCINE_FLG", "Y");
        }else if("Y".equals(this.getValueString("LIVER_N"))){
            parm.setData("LIVER_VACCINE_FLG", "N");
        }
        //TSH
        if("Y".equals(this.getValueString("TSH_Y"))){
            parm.setData("TSH_FLG", "Y");
        }else if("Y".equals(this.getValueString("TSH_N"))){
            parm.setData("TSH_FLG", "N");
        }
        //PKU
        if("Y".equals(this.getValueString("PKU_Y"))){
            parm.setData("PKU_FLG", "Y");
        }else if("Y".equals(this.getValueString("PKU_N"))){
            parm.setData("PKU_FLG", "N");
        }
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = ADMChildImmunityTool.getInstance().insertData(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
    }
    /**
     * 修改数据
     */
    private void update(){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        this.messageBox_(this.getValue("APGAR_NUMBER"));
        //评分
        if(this.getValue("APGAR_NUMBER")==null)
            parm.setData("APGAR_NUMBER","");
        else
            parm.setData("APGAR_NUMBER",this.getValueInt("APGAR_NUMBER"));
        //婴儿卡介苗
        if("Y".equals(this.getValueString("BABY_Y"))){
            parm.setData("BABY_VACCINE_FLG", "Y");
        }else if("Y".equals(this.getValueString("BABY_N"))){
            parm.setData("BABY_VACCINE_FLG", "N");
        }
        //乙肝疫苗
        if("Y".equals(this.getValueString("LIVER_Y"))){
            parm.setData("LIVER_VACCINE_FLG", "Y");
        }else if("Y".equals(this.getValueString("LIVER_N"))){
            parm.setData("LIVER_VACCINE_FLG", "N");
        }
        //TSH
        if("Y".equals(this.getValueString("TSH_Y"))){
            parm.setData("TSH_FLG", "Y");
        }else if("Y".equals(this.getValueString("TSH_N"))){
            parm.setData("TSH_FLG", "N");
        }
        //PKU
        if("Y".equals(this.getValueString("PKU_Y"))){
            parm.setData("PKU_FLG", "Y");
        }else if("Y".equals(this.getValueString("PKU_N"))){
            parm.setData("PKU_FLG", "N");
        }
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = ADMChildImmunityTool.getInstance().updateData(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
    }
    /**
     * 根据CASE_NO查询
     */
    public void query(){
        this.setValue("CASE_NO", CASE_NO);
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        data = ADMChildImmunityTool.getInstance().selectData(parm);
        if(data.getCount()>0){
            this.setValue("APGAR_NUMBER", data.getInt("APGAR_NUMBER", 0));
            this.setValue("BABY_" + data.getValue("BABY_VACCINE_FLG", 0), true);
            this.setValue("LIVER_" + data.getValue("LIVER_VACCINE_FLG", 0), true);
            this.setValue("TSH_" + data.getValue("TSH_FLG", 0), true);
            this.setValue("PKU_" + data.getValue("PKU_FLG", 0), true);
            saveType = "update";
        }else
            saveType = "new";
    }
}
