package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import jdo.clp.CLPTool;

/**
 * <p>Title: 临床路径多病种</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class CLPUIControl extends TControl {
    /**
     * WORD对象
     */
    private static final String TWORD = "WORD";
    /**
     * 类型
     */
    private String type;
    /**
     * 医院名称
     */
    private String hospDesc;
    /**
     * 病案号
     */
    private String mrNo;
    /**
     * 入院时间
     */
    private String inDate;
    /**
     * 出院日期
     */
    private String outDate;
    /**
     * 操作人员
     */
    private String optUser;
    /**
     * 操作时间
     */
    private String optDate;
    /**
     * 操作IP
     */
    private String optTerm;
    /**
     * 住院总费用
     */
    private double amtPrice;
    /**
     * 介入费用
     */
    private double jrPrice;
    /**
     * 药品费用
     */
    private double phaPrice;
    /**
     * 科室
     */
    private String deptCode;
    /**
     * 住院号
     */
    private String ipdNo;
    /**
     * 医生
     */
    private String drCode;
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 电话
     */
    private String tel;
    /**
     * 术前诊断
     */
    private String sqzd;
    /**
     * 术后诊断
     */
    private String shzd;
    /**
     * 出院诊断
     */
    private String outzd;
    /**
     * WORD控件
     */
    private TWord word;
    /**
     * 保存类型
     */
    private String saveType;
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            this.setType(parm.getValue("TYPE"));
            this.setMrNo(parm.getValue("MR_NO"));
            this.setInDate(parm.getValue("IN_DATE"));
            this.setOutDate(parm.getValue("OUT_DATE"));
            this.setHospDesc(parm.getValue("HOSP_DESC"));
            this.setJrPrice(parm.getDouble("JRPRICE"));
            this.setPhaPrice(parm.getDouble("PHA_PRICE"));
            this.setAmtPrice(parm.getDouble("AMT_PRICE"));
            this.setDeptCode(parm.getValue("DEPT_CODE"));
            this.setDrCode(parm.getValue("DR_CODE"));
            this.setIpdNo(parm.getValue("IPD_NO"));
            this.setCaseNo(parm.getValue("CASE_NO"));
            this.setOptDate(parm.getValue("OPT_DATE"));
            this.setOptUser(parm.getValue("OPT_USER"));
            this.setOptTerm(parm.getValue("OPT_TERM"));
            this.setTel(parm.getValue("TEL"));
            this.setSqzd(parm.getValue("SQZD"));
            this.setShzd(parm.getValue("SHZD"));
            this.setOutzd(parm.getValue("OUT_ZD"));
        }
        this.setType("HF");
//        this.setMrNo("000000000001");
//        this.setInDate("2010/03/01 11:01:59");
//        this.setOutDate("2010/03/05 11:01:59");
//        this.setHospDesc("泰达心血管");
//        this.setJrPrice(1000);
//        this.setPhaPrice(2000);
//        this.setAmtPrice(3000);
//        this.setDeptCode("32300");
//        this.setDrCode("000405");
//        this.setIpdNo("000000000002");
//        this.setCaseNo("100301000001");
//        this.setOptDate("2010/03/05 11:01:59");
//        this.setOptUser("000405");
//        this.setOptTerm("127.0.0.1");
//        this.setTel("24156110");
//        this.setSqzd("无");
//        this.setShzd("无");
//        this.setOutzd("无");
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        initWord();
        TParm parm = getCheckFile();
        if (parm.getErrCode() < 0) {
            this.messageBox(parm.getErrText());
            return;
        }
        if (parm.getCount() > 0) {
            this.word.onOpen(parm.getValue("FILE_PATH", 0),
                             parm.getValue("FILE_NAME", 0), 1, false);
            this.saveType = "UPDATE";
        } else {
            if ("AMI".equals(this.getType())) {
                this.word.onOpen("%ROOT%\\config\\prt\\CLP", "JX.jhw", 1, false);
            }
            if ("HF".equals(this.getType())) {
                this.word.onOpen("%ROOT%\\config\\prt\\CLP", "XL.jhw", 1, false);
            }
            if ("CABG".equals(this.getType())) {
                this.word.onOpen("%ROOT%\\config\\prt\\CLP", "GZ.jhw", 1, false);
            }
            this.saveType = "NEW";
        }

        this.word.setMicroField("HOSP_DESC", this.getHospDesc());
        this.word.setMicroField("MR_NO", this.getMrNo());
        this.word.setMicroField("IN_DATE", this.getInDate());
        this.word.setMicroField("OUT_DATE", this.getOutDate());
        this.word.setMicroField("OPT_DATE", this.getOptDate());
        this.word.setMicroField("OPT_USER", this.getOptUser());
        this.word.setMicroField("AMT_PRICE", "" + this.getAmtPrice());
        this.word.setMicroField("JR_PRICE", "" + this.getJrPrice());
        this.word.setMicroField("PHA_PRICE", "" + this.getPhaPrice());
        this.word.setMicroField("TEL", this.getTel());
        this.word.setMicroField("SQZD", this.getSqzd());
        this.word.setMicroField("SHZD", this.getShzd());
        this.word.setMicroField("OUT_ZD", this.getOutzd());
    }

    /**
     * 打印
     */
    public void onPrint() {
        if (this.word.getFileOpenName() != null) {
            word.onPreviewWord();
            word.print();
        } else {
            this.messageBox("未选择病历！");
        }
    }

    public static void main(String args[]) {
//        JavaHisDebug.initClient();
        /*JFrame f = new JFrame();
             f.getRootPane().add(new EMRPad30());
             f.getRootPane().setLayout(new BorderLayout());
             f.setVisible(true);*/

        JavaHisDebug.TBuilder();

    }

    /**
     * 查找文件路径
     * @return TParm
     */
    public TParm getCheckFile() {
        TParm result = new TParm();
        String tableName = "";
        if ("AMI".equals(this.getType())) {
            tableName = "CLP_AMI";
        }
        if ("HF".equals(this.getType())) {
            tableName = "CLP_HF";
        }
        if ("CABG".equals(this.getType())) {
            tableName = "CLP_CABG";
        }
        if (tableName.length() == 0) {
            result.setErrCode( -1);
            result.setErrText("表名为空！");
            return result;
        }
        result = CLPTool.getInstance().getCheckFile(tableName, this.getCaseNo());
        return result;
    }

    /**
     * 保存
     */
    public void onSave() {
        TParm saveData = new TParm();
        String ty = "";
        if ("AMI".equals(this.getType())) {
            ty = "AIM";
            saveData = getAIMData();
        }
        if ("HF".equals(this.getType())) {
            ty = "HF";
            saveData = getHFData();
        }
        if ("CABG".equals(this.getType())) {
            ty = "CABG";
            saveData = getCABGData();
        }
        //保存到数据文件夹
        String fileNo = "01";
        String filePath = "C:\\JavaHisFile\\EmrFileData\\EmrData\\" +
                          this.getCaseNo().substring(0, 2) + "\\" +
                          this.getCaseNo().substring(2, 4) + "\\" +
                          this.getMrNo();
        String fileName = this.getCaseNo() + "_" + ty + fileNo;
        saveData.setData("FILE_NO", fileNo);
        saveData.setData("FILE_PATH", filePath);
        saveData.setData("FILE_NAME", fileName);
        if ("NEW".equals(this.saveType)) {
            word.setMessageBoxSwitch(false);
            if (!this.word.onSaveAs(filePath, fileName, 1)) {
                this.messageBox("文件保存失败！");
                word.setMessageBoxSwitch(true);
                return;
            }
            word.setMessageBoxSwitch(true);
            TParm action = CLPTool.getInstance().insertCLPData(this.getType(),
                    saveData);
            if (action.getErrCode() < 0) {
                this.messageBox("保存失败！");
                return;
            }
            //数据保存
            this.messageBox("保存成功！");
        }
        if ("UPDATE".equals(this.saveType)) {
            word.setMessageBoxSwitch(false);
            if (!this.word.onSave()) {
                this.messageBox("文件保存失败！");
                word.setMessageBoxSwitch(true);
                return;
            }
            word.setMessageBoxSwitch(true);
            TParm action = CLPTool.getInstance().updateCLPData(this.getType(),
                    saveData);
            if (action.getErrCode() < 0) {
                this.messageBox("保存失败！");
                return;
            }
            //数据保存
            this.messageBox("保存成功！");
        }

    }

    /**
     * 拿到AIM数据
     * @return TParm
     */
    public TParm getAIMData() {
        TParm result = new TParm();
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT current date as DATE FROM sysibm.sysdummy1"));
        String dateStr = StringTool.getString(parm.getTimestamp("DATE", 0),
                                              "yyyy-MM-dd HH:mm:ss");
        result.setData("AMI_1", getNull(word.findGroupValue("1_1")));
        result.setData("AMI_2", getNull(word.findGroupValue("2_1")));
        result.setData("AMI_3", checkData("3", 3));
        result.setData("AMI_3_1", getNull(word.findGroupValue("3_1")));
        result.setData("AMI_3_2", getNull(word.findGroupValue("3_2")));
        result.setData("AMI_3_3", getNull(word.findGroupValue("3_3")));
        result.setData("AMI_4", getNull(word.findGroupValue("4_1")));
        result.setData("AMI_5", getNull(word.findGroupValue("5_1")));
        result.setData("AMI_5_1", getNull(word.findGroupValue("5_2")));
        result.setData("AMI_6", getNull(word.findGroupValue("6_1")));
        result.setData("AMI_7", checkData("7", 3));
        result.setData("AMI_7_1", getNull(word.findGroupValue("7_1")));
        result.setData("AMI_7_2", getNull(word.findGroupValue("7_2")));
        result.setData("AMI_7_3", getNull(word.findGroupValue("7_3")));
        result.setData("HOSP_AREA", "HIS");
        result.setData("CASE_NO", this.getCaseNo());
        result.setData("MR_NO", this.getMrNo());
        result.setData("IPD_NO", this.getIpdNo());
        result.setData("DEPT_CODE", this.getDeptCode());
        result.setData("DR_CODE", this.getDrCode());
        result.setData("TOT_AMT", this.getAmtPrice());
        result.setData("MED_FEE", this.getPhaPrice());
        result.setData("INTERVENE_FEE", this.getJrPrice());
        result.setData("OPT_USER", this.getOptUser());
        result.setData("OPT_TERM", this.getOptTerm());
        result.setData("OPT_DATE", dateStr);
        result.setData("IN_DATE",
                       StringTool.getString(StringTool.getTimestamp(this.getInDate(),
                "yyyy/MM/dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
        return result;
    }

    /**
     * 拿到HF数据
     * @return TParm
     */
    public TParm getHFData() {
        TParm result = new TParm();
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT current date as DATE FROM sysibm.sysdummy1"));
        String dateStr = StringTool.getString(parm.getTimestamp("DATE", 0),
                                              "yyyy-MM-dd HH:mm:ss");
        result.setData("HF_1", getNull(word.findGroupValue("1_1")));
        result.setData("HF_1_1", getNull(word.findGroupValue("1_2")));
        result.setData("HF_2", getNull(word.findGroupValue("2_1")));
        result.setData("HF_3", getNull(word.findGroupValue("3_1")));
        result.setData("HF_4", getNull(word.findGroupValue("4_1")));
        result.setData("HF_5", getNull(word.findGroupValue("5_1")));
        result.setData("HF_6", getNull(word.findGroupValue("6_1")));
        result.setData("HF_7", getNull(word.findGroupValue("7_1")));
        result.setData("HF_8", getNull(word.findGroupValue("8_1")));
        result.setData("HF_9", checkData("9", 4));
        result.setData("HF_9_1", getNull(word.findGroupValue("9_1")));
        result.setData("HF_9_2", getNull(word.findGroupValue("9_2")));
        result.setData("HF_9_3", getNull(word.findGroupValue("9_3")));
        result.setData("HF_9_4", getNull(word.findGroupValue("9_4")));
        result.setData("HOSP_AREA", "HIS");
        result.setData("CASE_NO", this.getCaseNo());
        result.setData("MR_NO", this.getMrNo());
        result.setData("IPD_NO", this.getIpdNo());
        result.setData("DEPT_CODE", this.getDeptCode());
        result.setData("DR_CODE", this.getDrCode());
        result.setData("TOT_AMT", this.getAmtPrice());
        result.setData("MED_FEE", this.getPhaPrice());
        result.setData("OPT_USER", this.getOptUser());
        result.setData("OPT_TERM", this.getOptTerm());
        result.setData("OPT_DATE", dateStr);
        result.setData("IN_DATE",
                       StringTool.getString(StringTool.getTimestamp(this.getInDate(),
                "yyyy/MM/dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
        return result;
    }

    /**
     * 得到CABG数据
     * @return TParm
     */
    public TParm getCABGData() {
        TParm result = new TParm();
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT current date as DATE FROM sysibm.sysdummy1"));
        String dateStr = StringTool.getString(parm.getTimestamp("DATE", 0),
                                              "yyyy-MM-dd HH:mm:ss");
        result.setData("CABG_1", checkData("1", 3));
        result.setData("CABG_1_1", getNull(word.findGroupValue("1_1")));
        result.setData("CABG_1_2", getNull(word.findGroupValue("1_2")));
        result.setData("CABG_1_3", getNull(word.findGroupValue("1_3")));
        result.setData("CABG_2", checkData("2", 2));
        result.setData("CABG_2_1", getNull(word.findGroupValue("2_1")));
        result.setData("CABG_2_2", getNull(word.findGroupValue("2_2")));
        result.setData("CABG_3", checkData("3", 2));
        result.setData("CABG_3_1", getNull(word.findGroupValue("3_1")));
        result.setData("CABG_3_2", getNull(word.findGroupValue("3_2")));
        result.setData("CABG_4", checkData("4", 4));
        result.setData("CABG_4_1", getNull(word.findGroupValue("4_1")));
        result.setData("CABG_4_2", getNull(word.findGroupValue("4_2")));
        result.setData("CABG_4_3", getNull(word.findGroupValue("4_3")));
        result.setData("CABG_4_4", getNull(word.findGroupValue("4_4")));
        result.setData("CABG_5", getNull(word.findGroupValue("5_1")));
        result.setData("CABG_6", checkData("6", 6));
        result.setData("CABG_6_1", getNull(word.findGroupValue("6_1")));
        result.setData("CABG_6_2", getNull(word.findGroupValue("6_2")));
        result.setData("CABG_6_3", getNull(word.findGroupValue("6_3")));
        result.setData("CABG_6_4", getNull(word.findGroupValue("6_4")));
        result.setData("CABG_6_5", getNull(word.findGroupValue("6_5")));
        result.setData("CABG_6_6", getNull(word.findGroupValue("6_6")));
        result.setData("CABG_7", getNull(word.findGroupValue("7_1")));
        result.setData("CABG_8", getNull(word.findGroupValue("8_1")));
        result.setData("CABG_9", getNull(word.findGroupValue("9_1")));
        result.setData("HOSP_AREA", "HIS");
        result.setData("CASE_NO", this.getCaseNo());
        result.setData("MR_NO", this.getMrNo());
        result.setData("IPD_NO", this.getIpdNo());
        result.setData("DEPT_CODE", this.getDeptCode());
        result.setData("DR_CODE", this.getDrCode());
        result.setData("TOT_AMT", this.getAmtPrice());
        result.setData("MED_FEE", this.getPhaPrice());
        result.setData("OPERATION_FEE", this.getJrPrice());
        result.setData("OPT_USER", this.getOptUser());
        result.setData("OPT_TERM", this.getOptTerm());
        result.setData("OPT_DATE", dateStr);
        result.setData("IN_DATE",
                       StringTool.getString(StringTool.getTimestamp(this.getInDate(),
                "yyyy/MM/dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
        return result;
    }

    /**
     * 去空
     * @param str String
     * @return String
     */
    public String getNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * 得到是否有选中
     * @param length int
     * @return String
     */
    public String checkData(String number, int length) {
        String str = "N";
        for (int i = 1; i <= length; i++) {
            if (getNull(word.findGroupValue(number + "_" + i)).length() != 0) {
                str = "Y";
                break;
            }
        }
        return str;
    }

    /**
     * 初始化WORD
     */
    public void initWord() {
        word = this.getTWord(TWORD);
    }

    /**
     * 得到WORD对象
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    public String getType() {
        return type;
    }

    public String getOutDate() {
        return outDate;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getInDate() {
        return inDate;
    }

    public String getHospDesc() {
        return hospDesc;
    }

    public String getOptDate() {
        return optDate;
    }

    public String getOptTerm() {
        return optTerm;
    }

    public String getOptUser() {
        return optUser;
    }

    public double getAmtPrice() {
        return amtPrice;
    }

    public double getJrPrice() {
        return jrPrice;
    }

    public double getPhaPrice() {
        return phaPrice;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public String getDrCode() {
        return drCode;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getTel() {
        return tel;
    }

    public String getShzd() {
        return shzd;
    }

    public String getSqzd() {
        return sqzd;
    }

    public String getOutzd() {
        return outzd;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public void setHospDesc(String hospDesc) {
        this.hospDesc = hospDesc;
    }

    public void setOptDate(String optDate) {
        this.optDate = optDate;
    }

    public void setOptTerm(String optTerm) {
        this.optTerm = optTerm;
    }

    public void setOptUser(String optUser) {
        this.optUser = optUser;
    }

    public void setAmtPrice(double amtPrice) {
        this.amtPrice = amtPrice;
    }

    public void setJrPrice(double jrPrice) {
        this.jrPrice = jrPrice;
    }

    public void setPhaPrice(double phaPrice) {
        this.phaPrice = phaPrice;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setShzd(String shzd) {
        this.shzd = shzd;
    }

    public void setSqzd(String sqzd) {
        this.sqzd = sqzd;
    }

    public void setOutzd(String outzd) {
        this.outzd = outzd;
    }
}
