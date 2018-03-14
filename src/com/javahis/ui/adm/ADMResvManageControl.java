package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

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
public class ADMResvManageControl extends TControl {
    TParm recptype = new TParm();//从预约住院接参
    Pat pat ;
    public void onInit() {
        super.onInit();
        init();
        setValue("APP_DATE", SystemTool.getInstance().getDate());
        setValue("RESV_DATE", SystemTool.getInstance().getDate());
        recptype = (TParm)this.getParameter();
        this.onSelect();
    }
    /**
     * 从预约住院传参赋值
     */
    public void onSelect(){
        String mrNo = recptype.getValue("MR_NO");
        pat = pat.onQueryByMrNo(mrNo);
        setValue("MR_NO",pat.getMrNo());
        setValue("CTZ1_CODE",pat.getCtz1Code());
        setValue("SEX_CODE",pat.getSexCode());
    }

    public void onSave(){
        TParm reData = new TParm();
        reData.addData("res","传回");
//        reData.setData("RESV_DATE",StringTool.getString((Timestamp)this.getValue(""),"yyyymmdd"));//病患来源
        reData.setData("RESV_DATE",this.getValue("RESV_DATE"));
        reData.setData("ADM_SOURCE",getValue("ADM_SOURCE"));//病患来源
        reData.setData("PR_DEPT_CODE",getValue("PR_DEPT_CODE"));//门急诊科别
        reData.setData("DR_CODE",getValue("DR_CODE"));//医师
        reData.setData("PATIENT_CONDITION",getValue("PATIENT_CONDITION"));//病人状况
        reData.setData("STATION_CODE",getValue("STATION_CODE"));//预订住院病区
        reData.setData("DEPT_CODE",getValue("DEPT_CODE"));//预订住院科室
        reData.setData("CLNCPATH_CODE",getValue("CLNCPATH_CODE"));//临床路径
        this.setReturnValue(reData);

    }




}
