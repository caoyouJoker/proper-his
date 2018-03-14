package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 临床路径评估执行</p>
 *
 * <p>Description: 临床路径评估执行</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPEvaluateControl extends TControl {
    private String case_no = "";
    public CLPEvaluateControl() {
    }

    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化页面
     */
    private void initPage() {
        TParm sendParm = (TParm)this.getParameter();
        case_no = sendParm.getValue("CASE_NO");
        //从传参中初始化病人信息
        initPatientInfo(sendParm);
    }

    private void initPatientInfo(TParm parm){
        this.setValue("MR_NO",parm.getValue("MR_NO"));
        this.setValue("PAT_NAME",parm.getValue("PAT_NAME"));
        this.setValue("SEX_DESC",parm.getValue("SEX_DESC"));
        this.setValue("BED_NO",parm.getValue("BED_NO"));
        this.setValue("VS_DR_CODE",parm.getValue("VS_DR_CODE"));
        this.setValue("CLNCPATH_CODE",parm.getValue("CLNCPATH_CODE"));
        this.setValue("IN_DATE",parm.getValue("IN_DATE"));
        this.setValue("DS_DATE",parm.getValue("DS_DATE"));
    }

    /**
     * 大分类联动子分类
     */
    public void onCAT1Change(){
        //得到大分类类别
        String CAT1Code = this.getValueString("CAT1_CODE");
        TTextFormat CAT2Code = (TTextFormat)this.getComponent(
                "CAT2_CODE");
        CAT2Code.setValue("");
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("  SELECT CAT2_CODE AS ID ,CAT2_CHN_DESC AS NAME,CAT2_ENG_DESC AS ENNAME  FROM CLP_EVL_CAT2 ");
        sqlbf.append("   WHERE CAT1_CODE = '" +
                     CAT1Code.trim() + "'");
        CAT2Code.setPopupMenuSQL(sqlbf.toString());
        CAT2Code.onQuery();

    }


}
