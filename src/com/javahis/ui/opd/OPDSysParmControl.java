package com.javahis.ui.opd;

import jdo.opd.OPDSysParmTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 *
 * <p>
 * Title: 医生参数档Panel
 * </p>
 *
 * <p>
 * Description:医生参数档Panel
 * </p>
 *
 * <p>
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDSysParmControl
    extends TControl {
    public void onInit() {
        super.onInit();
        OPDSysParmTool o;
        TParm parm = OPDSysParmTool.getInstance().selectAll();
        if (parm.getErrCode() < 0) {
            this.messageBox("初始化失败");
            return;
        }
        /*
         * 儿童处方签年龄设定	I	AGE
         处方签每页种类数	I	PAGE_NUM
         预设饮片付数	I	DCT_TAKE_DAYS
         预设饮片使用计量(ml)	I	DCT_TAKE_QTY
         怀孕周数	I	PREGNANT_WEEKS
         开立保存即立合理用药审核注记	I	SAVERDU_FLG
         西成药医保检核
         医保检核注记	I	W_NHICHECK_FLG
         药品种类数	I	W_TYPE_NUM
         日份	I	W_TAKE_DAYS
         总价	I	W_TOT_AMT
         饮片医保检核
         医保检核注记	I	G_NHICHECK_FLG
         药品种类数	I	G_TYPE_NUM
         日份	I	G_TAKE_DAYS
         总价	I	G_TOT_AMT
         急诊限定看诊天数  E_DAYS
         */
        String value = parm.getValue("AGE", 0);
        int year = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("AGE", year);

        value = parm.getValue("PAGE_NUM", 0);
        int PAGE_NUM = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("PAGE_NUM", PAGE_NUM);

        value = parm.getValue("DCT_TAKE_DAYS", 0);
        int DCT_TAKE_DAYS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("DCT_TAKE_DAYS", DCT_TAKE_DAYS);

        value = parm.getValue("DCT_TAKE_QTY", 0);
        int DCT_TAKE_QTY = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("DCT_TAKE_QTY", DCT_TAKE_QTY);

        value = parm.getValue("PREGNANT_WEEKS", 0);
        int PREGNANT_WEEKS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("PREGNANT_WEEKS", PREGNANT_WEEKS);

        value = parm.getValue("SAVERDU_FLG", 0);
        boolean SAVERDU_FLG = ("Y".equalsIgnoreCase(value)) ? true : false;
        this.setValue("SAVERDU_FLG", SAVERDU_FLG);

        value = parm.getValue("W_NHICHECK_FLG", 0);
        boolean W_NHICHECK_FLG = ("Y".equalsIgnoreCase(value)) ? true : false;
        this.setValue("W_NHICHECK_FLG", W_NHICHECK_FLG);

        value = parm.getValue("W_TYPE_NUM", 0);
        int W_TYPE_NUM = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("W_TYPE_NUM", W_TYPE_NUM);

        value = parm.getValue("W_TAKE_DAYS", 0);
        int W_TAKE_DAYS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("W_TAKE_DAYS", W_TAKE_DAYS);

        value = parm.getValue("W_TOT_AMT", 0);
        double W_TOT_AMT = (value == null || value.trim().length() < 1) ? 0.0 :
            Double.parseDouble(value);
        this.setValue("W_TOT_AMT", W_TOT_AMT);

        value = parm.getValue("G_NHICHECK_FLG", 0);
        boolean G_NHICHECK_FLG = ("Y".equalsIgnoreCase(value)) ? true : false;
        this.setValue("G_NHICHECK_FLG", G_NHICHECK_FLG);

        value = parm.getValue("G_TYPE_NUM", 0);
        int G_TYPE_NUM = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("G_TYPE_NUM", G_TYPE_NUM);

        value = parm.getValue("G_TAKE_DAYS", 0);
        int G_TAKE_DAYS = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("G_TAKE_DAYS", G_TAKE_DAYS);

        value = parm.getValue("G_TOT_AMT", 0);
        double G_TOT_AMT = (value == null || value.trim().length() < 1) ? 0.0 :
            Double.parseDouble(value);
        this.setValue("G_TOT_AMT", G_TOT_AMT);

        value = parm.getValue("E_DAYS", 0);
        int days = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("E_DAYS", days);
        //==zhangp 20120702 start
        value = parm.getValue("DS_MED_DAY", 0);
        int ds_med_day = (value == null || value.trim().length() < 1) ? 0 :
            Integer.parseInt(value);
        this.setValue("DS_MED_DAY", ds_med_day);
        //==zhangp 20120702 end
    }

    /**
     * 保存
     */
    public void onSave() {
        /*
         * 儿童处方签年龄设定	I	AGE
         处方签每页种类数	I	PAGE_NUM
         预设饮片付数	I	DCT_TAKE_DAYS
         预设饮片使用计量(ml)	I	DCT_TAKE_QTY
         怀孕周数	I	PREGNANT_WEEKS
         开立保存即立合理用药审核注记	I	SAVERDU_FLG
         西成药医保检核
         医保检核注记	I	W_NHICHECK_FLG
         药品种类数	I	W_TYPE_NUM
         日份	I	W_TAKE_DAYS
         总价	I	W_TOT_AMT
         饮片医保检核
         医保检核注记	I	G_NHICHECK_FLG
         药品种类数	I	G_TYPE_NUM
         日份	I	G_TAKE_DAYS
         总价	I	G_TOT_AMT
         急诊限定看诊天数  E_DAYS
         */
        TParm parm = getParmForTag("AGE;PAGE_NUM;DCT_TAKE_DAYS;DCT_TAKE_QTY;PREGNANT_WEEKS;SAVERDU_FLG;W_NHICHECK_FLG;W_TYPE_NUM;W_TAKE_DAYS;W_TOT_AMT;G_NHICHECK_FLG;G_TYPE_NUM;G_TAKE_DAYS;G_TOT_AMT;E_DAYS" +
        		//===ZHANGP 20120702 START
        		";DS_MED_DAY");
        //===ZHANGP 20120702 END

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = OPDSysParmTool.getInstance().updataData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    }
}
