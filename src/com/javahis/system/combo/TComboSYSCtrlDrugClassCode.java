package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: 管制药品等级代码下拉列表</p>
 *
 * <p>Description: 管制药品等级代码下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSCtrlDrugClassCode
    extends TComboBox {
    /**
     * 专用处方签注记
     */
    private String prnspcformFlg; // PRNSPCFORM_FLG
    /**
     * 毒麻药注记
     */
    private String ctrlFlg; //CTRL_FLG
    /**
     * 麻醉用药注记
     */
    private String narcoticFlg; //NARCOTIC_FLG
    /**
     * 毒性用药注记
     */
    private String toxicandFlg; //TOXICANT_FLG
    /**
     * 精神用药注记
     */
    private String psychophaFlg; //PSYCHOPHA_FLG
    /**
     * 化疗用药注记
     */
    private String radiaFlg; //RADIA_FLG
    /**
     * 试验用药注记
     */
    private String testDrugFlg; //TEST_DRUG_FLG
    /**
     * 抗菌用药注记
     */
    private String antisepticFlg; //ANTISEPTIC_FLG
    /**
     * 抗生素注记
     */
    private String antibioticFlg; //ANTIBIOTIC_FLG
    /**
     * 全静脉营养注射注记
     */
    private String tpnFlg; //TPN_FLG
    /**
     * 设置专用处方签注记
     * @param prnspcformFlg String
     */
    public void setPrnspcformFlg(String prnspcformFlg) {
        this.prnspcformFlg = prnspcformFlg;
    }

    /**
     * 得到专用处方签注记
     * @return String
     */
    public String getPrnspcformFlg() {
        return prnspcformFlg;
    }

    /**
     * 设置毒麻药注记
     * @param ctrlFlg String
     */
    public void setCtrlFlg(String ctrlFlg) {
        this.ctrlFlg = ctrlFlg;
    }

    /**
     * 得到毒麻药注记
     * @return String
     */
    public String getCtrlFlg() {
        return ctrlFlg;
    }

    /**
     * 设置麻醉用药注记
     * @param narcoticFlg String
     */
    public void setNarcoticFlg(String narcoticFlg) {
        this.narcoticFlg = narcoticFlg;
    }

    /**
     * 得到麻醉用药注记
     * @return String
     */
    public String getNarcoticFlg() {
        return narcoticFlg;
    }

    /**
     * 设置毒性用药注记
     * @param toxicandFlg String
     */
    public void setToxicandFlg(String toxicandFlg) {
        this.toxicandFlg = toxicandFlg;
    }

    /**
     * 得到毒性用药注记
     * @return String
     */
    public String getToxicandFlg() {
        return toxicandFlg;
    }

    /**
     * 设置精神用药注记
     * @param psychophaFlg String
     */
    public void setPsychophaFlg(String psychophaFlg) {
        this.psychophaFlg = psychophaFlg;
    }

    /**
     * 得到精神用药注记
     * @return String
     */
    public String getPsychophaFlg() {
        return psychophaFlg;
    }

    /**
     * 设置化疗用药注记
     * @param radiaFlg String
     */
    public void setRadiaFlg(String radiaFlg) {
        this.radiaFlg = radiaFlg;
    }

    /**
     * 得到化疗用药注记
     * @return String
     */
    public String getRadiaFlg() {
        return radiaFlg;
    }

    /**
     * 设置试验用药注记
     * @param testDrugFlg String
     */
    public void setTestDrugFlg(String testDrugFlg) {
        this.testDrugFlg = testDrugFlg;
    }

    /**
     * 得到试验用药注记
     * @return String
     */
    public String getTestDrugFlg() {
        return testDrugFlg;
    }

    /**
     * 设置抗菌用药注记
     * @param antisepticFlg String
     */
    public void setAntisepticFlg(String antisepticFlg) {
        this.antisepticFlg = antisepticFlg;
    }

    /**
     * 得到抗菌用药注记
     * @return String
     */
    public String getAntisepticFlg() {
        return antisepticFlg;
    }

    /**
     * 设置抗生素注记
     * @param antibioticFlg String
     */
    public void setAntibioticFlg(String antibioticFlg) {
        this.antibioticFlg = antibioticFlg;
    }

    /**
     * 得到抗生素注记
     * @return String
     */
    public String getAntibioticFlg() {
        return antibioticFlg;
    }

    /**
     * 全静脉营养注射注记
     * @param tpnFlg String
     */
    public void setTpnFlg(String tpnFlg) {
        this.tpnFlg = tpnFlg;
    }

    /**
     * 全静脉营养注射注记
     * @return String
     */
    public String getTpnFlg() {
        return tpnFlg;
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("PRNSPCFORM_FLG", getTagValue(getPrnspcformFlg()));
        parm.setDataN("CTRL_FLG", getTagValue(getCtrlFlg()));
        parm.setDataN("NARCOTIC_FLG", getTagValue(getNarcoticFlg()));
        parm.setDataN("TOXICANT_FLG", getTagValue(getToxicandFlg()));
        parm.setDataN("PSYCHOPHA_FLG", getTagValue(getPsychophaFlg()));
        parm.setDataN("RADIA_FLG", getTagValue(getRadiaFlg()));
        parm.setDataN("TEST_DRUG_FLG", getTagValue(getTestDrugFlg()));
        parm.setDataN("ANTISEPTIC_FLG", getTagValue(getAntisepticFlg()));
        parm.setDataN("ANTIBIOTIC_FLG", getTagValue(getAntibioticFlg()));
        parm.setDataN("TPN_FLG", getTagValue(getTpnFlg()));
        setModuleParm(parm);
        super.onQuery();
    }

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "TButton");
        object.setValue("showID", "Y");
        object.setValue("showName", "Y");
        object.setValue("showText", "N");
        object.setValue("showValue", "N");
        object.setValue("showPy1", "Y");
        object.setValue("showPy2", "Y");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "管制药品等级代码");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSCtrlDrugClassModule.x";
    }

    public String getModuleMethodName() {
        return "initCtrlDrugClassCode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("PrnspcformFlg", "String", "", "Left")); //增加科室筛选条件
        data.add(new TAttribute("CtrlFlg", "String", "", "Left"));
        data.add(new TAttribute("NarcoticFlg", "String", "", "Left"));
        data.add(new TAttribute("ToxicandFlg", "String", "", "Left"));
        data.add(new TAttribute("PsychophaFlg", "String", "", "Left"));
        data.add(new TAttribute("RadiaFlg", "String", "", "Left"));
        data.add(new TAttribute("TestDrugFlg", "String", "", "Left"));
        data.add(new TAttribute("AntisepticFlg", "String", "", "Left"));
        data.add(new TAttribute("AntibioticFlg", "String", "", "Left"));
        data.add(new TAttribute("TpnFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("PrnspcformFlg".equalsIgnoreCase(name)) {
            setPrnspcformFlg(value);
            getTObject().setValue("PrnspcformFlg", value);
            return;
        }
        if ("CtrlFlg".equalsIgnoreCase(name)) {
            setCtrlFlg(value);
            getTObject().setValue("CtrlFlg", value);
            return;
        }
        if ("NarcoticFlg".equalsIgnoreCase(name)) {
            setNarcoticFlg(value);
            getTObject().setValue("NarcoticFlg", value);
            return;
        }
        if ("ToxicandFlg".equalsIgnoreCase(name)) {
            setToxicandFlg(value);
            getTObject().setValue("ToxicandFlg", value);
            return;
        }
        if ("PsychophaFlg".equalsIgnoreCase(name)) {
            setPsychophaFlg(value);
            getTObject().setValue("PsychophaFlg", value);
            return;
        }
        if ("RadiaFlg".equalsIgnoreCase(name)) {
            setRadiaFlg(value);
            getTObject().setValue("RadiaFlg", value);
            return;
        }
        if ("TestDrugFlg".equalsIgnoreCase(name)) {
            setTestDrugFlg(value);
            getTObject().setValue("TestDrugFlg", value);
            return;
        }
        if ("AntisepticFlg".equalsIgnoreCase(name)) {
            setAntisepticFlg(value);
            getTObject().setValue("AntisepticFlg", value);
            return;
        }
        if ("AntibioticFlg".equalsIgnoreCase(name)) {
            setAntibioticFlg(value);
            getTObject().setValue("AntibioticFlg", value);
            return;
        }
        if ("TpnFlg".equalsIgnoreCase(name)) {
            setTpnFlg(value);
            getTObject().setValue("TpnFlg", value);
            return;
        }

        super.setAttribute(name, value);
    }
}
