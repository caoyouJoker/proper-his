package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.device.JMFRegistry;
import com.javahis.device.JMStudio;
import com.javahis.util.StringUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import jdo.mro.MROTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;

public class SYSPatInfoControl extends TControl
{
  TParm data;
  TParm comboldata;
  Pat pat;
  String action = "NEW";
  String recpt = "";

  String oldOccCode = "";
  String oldRelationCode = "";

  public void onInit()
  {
    callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(true) });
    callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|PHOTO_BOTTON|setEnabled", new Object[] { Boolean.valueOf(false) });

    onSavePopedem();

    Object obj = getParameter();
    TParm recptParm = new TParm();
    if ((obj instanceof TParm)) {
      recptParm = (TParm)obj;
      initUI(recptParm);
    }
  }

  private void onSavePopedem()
  {
    if (getPopedem("readOnlyEnabled")) {
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
    }
  }

  public void viewPhoto(String mrNo)
  {
    String photoName = mrNo + ".jpg";
    String fileName = photoName;
    try {
      TPanel viewPanel = (TPanel)getComponent("VIEW_PANEL");
      String root = TIOM_FileServer.getRoot();
      String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
      dir = root + dir + mrNo.substring(0, 3) + "\\" + 
        mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";

      byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), 
        dir + fileName);
      if (data == null) {
        viewPanel.removeAll();
        return;
      }
      double scale = 0.5D;
      boolean flag = true;
      Image image = ImageTool.scale(data, scale, flag);

      Pic pic = new Pic(image);
      pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
      pic.setLocation(0, 0);
      viewPanel.removeAll();
      viewPanel.add(pic);
      pic.repaint();
    } catch (Exception localException) {
    }
    onSavePopedem();
  }

  public void onIdNo()
  {
    String homeCode = "";
    if ("Y".equals(getValue("FOREIGNER_FLG"))) {
      setValue("HOMEPLACE_CODE", "");
      return;
    }
    String idNo = getValueString("IDNO");

    homeCode = StringUtil.getIdNoToHomeCode(idNo);
    if (PatTool.getInstance().isExistHomePlace(homeCode))
      setValue("HOMEPLACE_CODE", homeCode);
    else {
      setValue("HOMEPLACE_CODE", "");
    }
    grabFocus("CTZ1_CODE");

    TParm parm = PatTool.getInstance().getInfoForNEWIdno(idNo);

    if (parm.getErrCode() < 0) {
      messageBox("查无此病患!");
      return;
    }
    if (parm.getCount() <= 0) {
      messageBox("查无此病患!");
      return;
    }

    if (parm.getCount() > 1) {
      Object result = (TParm)openDialog("%ROOT%\\config\\sys\\SYSIDInformation.x", parm);
      if ((result instanceof TParm)) {
        TParm IDparm = (TParm)result;
        setValue("MR_NO", IDparm.getValue("MR_NO"));
        onQuery();
      } else {
        return;
      }
    }
    if (parm.getCount() == 1) {
      setValue("MR_NO", parm.getValue("MR_NO", 0));
      onQuery();
    }
  }

  public void initUI(TParm recptParm)
  {
    if (!recptParm.checkEmpty("OPD", recptParm)) {
      this.recpt = "OPD";
      setValue("MR_NO", recptParm.getData("MR_NO"));
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|clear|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|query|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      onQuery();
    } else if (!recptParm.checkEmpty("ONW", recptParm)) {
      this.recpt = "ONW";
      setValue("MR_NO", recptParm.getData("MR_NO"));
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|clear|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|query|setEnabled", new Object[] { Boolean.valueOf(true) });
      onQuery();
    } else if (!recptParm.checkEmpty("ADM", recptParm)) {
      this.recpt = "ADM";
      setValue("MR_NO", recptParm.getData("MR_NO"));
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|clear|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|query|setEnabled", new Object[] { Boolean.valueOf(true) });
      onQuery();
    } else if (!recptParm.checkEmpty("OPE", recptParm)) {
      this.recpt = "OPE";
      setValue("MR_NO", recptParm.getData("MR_NO"));
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|clear|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|query|setEnabled", new Object[] { Boolean.valueOf(true) });
      onQuery();
    } else if (!recptParm.checkEmpty("HRM", recptParm)) {
      this.recpt = "HRM";
      if (!"N".equals(recptParm.getValue("SAVE_FLG"))) {
        setValue("MR_NO", recptParm.getData("MR_NO"));
        callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(true) });
        callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
        callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
        callFunction("UI|clear|setEnabled", new Object[] { Boolean.valueOf(true) });
        callFunction("UI|query|setEnabled", new Object[] { Boolean.valueOf(true) });
        onQuery();
      } else {
        onNew();
      }
    } else if (!recptParm.checkEmpty("RESV", recptParm)) {
      this.recpt = "RESV";
      setValue("MR_NO", recptParm.getData("MR_NO"));
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|clear|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|query|setEnabled", new Object[] { Boolean.valueOf(true) });
      if (!"".equals(recptParm.getValue("MR_NO")))
        onQuery();
      else {
        callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      }
    }

    onSavePopedem();
  }

  public String getParmMap() {
    StringBuffer sb = new StringBuffer();

    sb.append("MR_NO;");
    sb.append("FOREIGNER_FLG;");
    sb.append("IDNO;");
    sb.append("MERGE_FLG;");
    sb.append("MERGE_TOMRNO;");
    sb.append("IPD_NO;");
    sb.append("PAT_NAME;");
    sb.append("PY1;");
    sb.append("PAT_NAME1;");
    sb.append("PY2;");
    sb.append("BIRTH_DATE;");
    sb.append("SEX_CODE;");
    sb.append("HOMEPLACE_CODE;");
    sb.append("CTZ1_CODE;");
    sb.append("CTZ2_CODE;");
    sb.append("CTZ3_CODE;");

    sb.append("NATION_CODE;");
    sb.append("SPECIES_CODE;");
    sb.append("E_MAIL;");
    sb.append("TEL_HOME;");
    sb.append("TEL_COMPANY;");
    sb.append("OCC_CODE;");
    sb.append("COMPANY_DESC;");
    sb.append("CELL_PHONE;");
    sb.append("MARRIAGE_CODE;");
    sb.append("HEIGHT;");
    sb.append("WEIGHT;");
    sb.append("BLOOD_TYPE;");
    sb.append("BLOOD_RH_TYPE;");

    sb.append("POST_CODE;");
    sb.append("ADDRESS;");

    sb.append("RESID_POST_CODE;");
    sb.append("RESID_ADDRESS;");

    sb.append("CONTACTS_NAME;");
    sb.append("RELATION_CODE;");
    sb.append("CONTACTS_TEL;");
    sb.append("CONTACTS_ADDRESS;");

    sb.append("EDUCATION_CODE;");
    sb.append("SPECIES_CODE;");
    sb.append("OCC_CODE;");
    sb.append("COMPANY_DESC;");
    sb.append("MARRIAGE_CODE;");
    sb.append("HEIGHT;");
    sb.append("WEIGHT;");
    sb.append("BLOOD_TYPE;");
    sb.append("POST_CODE;");
    sb.append("ADDRESS_ROAD;");
    sb.append("RESID_POST_CODE;");
    sb.append("RESID_ROAD;");
    sb.append("CONTACTS_NAME;");
    sb.append("RELATION_CODE;");
    sb.append("CONTACTS_TEL;");
    sb.append("CONTACTS_POST;");
    sb.append("CONTACTS_ADDRESS;");

    sb.append("EDUCATION_CODE;");
    sb.append("RELIGION_CODE;");
    sb.append("SPOUSE_IDNO;");
    sb.append("FATHER_IDNO;");
    sb.append("MOTHER_IDNO;");

    sb.append("FIRST_ADM_DATE;");
    sb.append("DEAD_DATE;");
    sb.append("RCNT_OPD_DATE;");
    sb.append("RCNT_OPD_DEPT;");
    sb.append("RCNT_EMG_DATE;");
    sb.append("RCNT_EMG_DEPT;");
    sb.append("RCNT_IPD_DATE;");
    sb.append("RCNT_IPD_DEPT;");
    sb.append("RCNT_MISS_DATE;");
    sb.append("RCNT_MISS_DEPT;");
    sb.append("ADULT_EXAM_DATE;");
    sb.append("SMEAR_RCNT_DATE;");
    sb.append("KID_EXAM_RCNT_DATE;");
    sb.append("KID_INJ_RCNT_DATE;");
    sb.append("LMP_DATE;");
    sb.append("BREASTFEED_STARTDATE;");
    sb.append("BREASTFEED_ENDDATE;");
    sb.append("PAT1_CODE;");
    sb.append("PAT2_CODE;");
    sb.append("PAT3_CODE;");
    sb.append("PREGNANT_DATE;");
    sb.append("DESCRIPTION;");

    sb.append("BIRTHPLACE;");

    sb.append("ADDRESS_COMPANY;");
    sb.append("POST_COMPANY;");
    sb.append("NHICARD_NO;");
    sb.append("NHI_NO;");

    sb.append("SECURITY_CATEGORY");
    onSavePopedem();
    return sb.toString();
  }

  public void onQueryInformation()
  {
    String mrno = "";
    String idno = "";
    mrno = getValueString("MR_NO");
    idno = getValueString("IDNO");
    if (!StringUtil.isNullString(mrno)){
    	//this.messageBox("1");
      onQuery();
      return;
    }else if(!StringUtil.isNullString(idno)){ 
    	onIdNo();
    	//this.messageBox("2");
    	return;
    }else if (!"".equals(getValueString("PY1"))) {
    	TParm inparm = new TParm();
    	inparm.setData("PY1", getValueString("PY1"));
    	Object obj = (TParm)openDialog(
    			"%ROOT%\\config\\sys\\SYSPatQuery.x", inparm);
    	if ((obj instanceof TParm)) {
    		TParm queryParm = (TParm)obj;
    		if (queryParm != null) {
    			setValue("MR_NO", queryParm.getValue("MR_NO"));
    		}
    	}
    	onQuery();
    	//this.messageBox("3"); 
    	return;
    }else{
    	this.messageBox("请输入查询条件");
    	return;
    }
  }

  public void onQuery()
  {
//    if (!"".equals(getValueString("PY1"))) {
//      TParm inparm = new TParm();
//      inparm.setData("PY1", getValueString("PY1"));
//      Object obj = (TParm)openDialog(
//        "%ROOT%\\config\\sys\\SYSPatQuery.x", inparm);
//      if ((obj instanceof TParm)) {
//        TParm queryParm = (TParm)obj;
//        if (queryParm != null) {
//          setValue("MR_NO", queryParm.getValue("MR_NO"));
//        }
//      }
//      else
//      {
//        return;
//      }
//    }

    this.pat = Pat.onQueryByMrNo(getValueString("MR_NO").trim());

    if (this.pat == null) {
      messageBox("查无此病患!");
      callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });

      onSavePopedem();
      return;
    }

    String srcMrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO").trim());
    if ((!StringUtil.isNullString(srcMrNo)) && (!srcMrNo.equals(this.pat.getMrNo()))) {
      messageBox("病案号" + srcMrNo + " 已合并至 " + this.pat.getMrNo());
    }

    callFunction("UI|PHOTO_BOTTON|setEnabled", new Object[] { Boolean.valueOf(false) });

    TParm parm = this.pat.getParm();

    if (parm.getData("FOREIGNER_FLG").equals(Boolean.valueOf(true)))
      setValue("FOREIGNER_FLG", "Y");
    else {
      setValue("FOREIGNER_FLG", "N");
    }
    if (parm.getData("MERGE_FLG").equals(Boolean.valueOf(true)))
      setValue("MERGE_FLG", "Y");
    else
      setValue("MERGE_FLG", "N");
    onMergeFlg();

    if (parm.getData("BLOOD_RH_TYPE").equals("+"))
      setValue("BLOOD_RH_TYPE", "Y");
    else if (parm.getData("BLOOD_RH_TYPE").equals("-")) {
      setValue("tRadioButton_3", "Y");
    }
    setValueForParm(getParmMap(), parm);

    callFunction("UI|MR_NO|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|PHOTO_BOTTON|setEnabled", new Object[] { Boolean.valueOf(true) });
    this.action = "EDIT";

    TParm parmBase = new TParm();

    parmBase.setData("NATION_CODE", this.pat.getNationCode());
    parmBase.setData("SPECIES_CODE", this.pat.getSpeciesCode());
    parmBase.setData("E_MAIL", this.pat.getEmail());
    parmBase.setData("TEL_HOME", this.pat.getTelHome());
    parmBase.setData("TEL_COMPANY", this.pat.getTelCompany());
    parmBase.setData("OCC_CODE", this.pat.getOccCode());
    parmBase.setData("COMPANY_DESC", this.pat.getCompanyDesc());
    parmBase.setData("CELL_PHONE", this.pat.getCellPhone());
    parmBase.setData("MARRIAGE_CODE", this.pat.getMarriageCode());
    parmBase.setData("HEIGHT", Double.valueOf(this.pat.getHeight()));
    parmBase.setData("WEIGHT", Double.valueOf(this.pat.getWeight()));
    parmBase.setData("BLOOD_TYPE", this.pat.getBloodType());
    if (this.pat.getBloodRHType().equals("+"))
      callFunction("UI|BLOOD_RH_TYPE", new Object[0]);
    parmBase.setData("BLOOD_RH_TYPE", "N");
    parmBase.setData("POST_CODE", this.pat.getPostCode());

    parmBase.setData("ADDRESS", this.pat.getAddress());
    parmBase.setData("RESID_POST_CODE", this.pat.getResidPostCode());

    parmBase.setData("RESID_ADDRESS", this.pat.getResidAddress());
    parmBase.setData("ADDRESS_COMPANY", this.pat.getCompanyAddress());

    parmBase.setData("POST_COMPANY", this.pat.getCompanyPost());
    parmBase.setData("CONTACTS_NAME", this.pat.getContactsName());
    parmBase.setData("RELATION_CODE", this.pat.getRelationCode());
    parmBase.setData("CONTACTS_TEL", this.pat.getContactsTel());
    parmBase.setData("CONTACTS_ADDRESS", this.pat.getContactsAddress());
    parmBase.setData("PAT_BELONG", this.pat.getPatBelong());
    parmBase.setData("SECURITY_CATEGORY", this.pat.getSecurityCategory());

    setValueForParm(
      "NATION_CODE;SPECIES_CODE;E_MAIL;TEL_H1;TEL_H2;OCC_CODE;COMPANY_DESC;TEL_O1;CELL_PHONE;MARRIAGE_CODE;HEIGHT;WEIGHT;BLOOD_TYPE;BLOOD_RH_TYPE;POST_CODE;ADDRESS_ROAD;RESID_POST_CODE;RESID_ROAD;CONTACTS_NAME;RELATION_CODE;CONTACTS_TEL;CONTACTS_POST;CONTACTS_ADDRESS;POST_COMPANY;ADDRESS_COMPANY;PAT_BELONG;SECURITY_CATEGORY", 
      parmBase, -1);
    viewPhoto(this.pat.getMrNo());

    TComboBox occCodeCombo = (TComboBox)getComponent("OCC_CODE");
    TComboBox relationCombo = (TComboBox)getComponent("RELATION_CODE");
    occCodeCombo.setCanEdit(true);
    relationCombo.setCanEdit(true);
    String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" + 
      this.pat.getOccCode() + "' ";
    String sql2 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" + 
      this.pat.getRelationCode() + "' ";
    TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
    TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    occCodeCombo.setText((String)result1.getData("CHN_DESC", 0));
    relationCombo.setText((String)result2.getData("CHN_DESC", 0));
    this.oldOccCode = this.pat.getOccCode();
    this.oldRelationCode = this.pat.getRelationCode();

    if ("".equals(this.recpt)) {
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
      callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(true) });
    }
    onSavePopedem();
  }

  public Pat modifyValue()
  {
    onSavePopedem();
    if (getValue("FOREIGNER_FLG").equals("Y"))
      this.pat.modifyForeignerFlg(true);
    else
      this.pat.modifyForeignerFlg(false);
    this.pat.modifyIdNo(getValueString("IDNO"));
    this.pat.modifyhomePlaceCode(getValueString("HOMEPLACE_CODE"));
    if (getValue("MERGE_FLG").equals("Y"))
      this.pat.modifyMergeFlg(true);
    else
      this.pat.modifyMergeFlg(false);
    this.pat.modifyMergeToMrNo(getValueString("MERGE_TOMRNO"));
    this.pat.modifyIpdNo(getValueString("IPD_NO"));
    this.pat.modifyName(getValueString("PAT_NAME"));
    this.pat.modifyPy1(getValueString("PY1"));
    this.pat.modifyName1(getValueString("PAT_NAME1"));
    this.pat.modifyPy2(getValueString("PY2"));
    this.pat.modifyBirthdy(TCM_Transform.getTimestamp(getValue("BIRTH_DATE")));
    this.pat.modifyBirthPlace(getValueString("BIRTHPLACE"));
    this.pat.modifyNhicardNo(getValueString("NHICARD_NO"));
    this.pat.modifyNhiNo(getValueString("NHI_NO"));

    this.pat.modifySexCode(getValueString("SEX_CODE"));
    this.pat.modifyCtz1Code(getValueString("CTZ1_CODE"));
    this.pat.modifyCtz2Code(getValueString("CTZ2_CODE"));
    this.pat.modifyCtz3Code(getValueString("CTZ3_CODE"));

    this.pat.modifyNationCode(getValueString("NATION_CODE"));
    this.pat.modifySpeciesCode(getValueString("SPECIES_CODE"));
    this.pat.modifyEmail(getValueString("E_MAIL"));
    this.pat.modifyTelHome(getValueString("TEL_HOME"));
    this.pat.modifyTelCompany(getValueString("TEL_COMPANY"));

    String sql001 = "SELECT count(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" + 
      getValueString("OCC_CODE") + "' ";
    TParm result001 = new TParm(TJDODBTool.getInstance().select(sql001));
    int count001 = Integer.parseInt(result001.getValue("COUNT", 0));
    if (count001 == 0)
      this.pat.modifyOccCode(this.oldOccCode);
    else {
      this.pat.modifyOccCode(getValueString("OCC_CODE"));
    }

    this.pat.modifyCompanyDesc(getValueString("COMPANY_DESC"));
    this.pat.modifyCellPhone(getValueString("CELL_PHONE"));
    this.pat.modifyMarriageCode(getValueString("MARRIAGE_CODE"));
    this.pat.modifyHeight(getValueDouble("HEIGHT"));
    this.pat.modifyWeight(getValueDouble("WEIGHT"));
    this.pat.modifyBloodType(getValueString("BLOOD_TYPE"));
    if (getValue("BLOOD_RH_TYPE").equals("Y"))
      this.pat.modifyBloodRHType("+");
    else if (getValue("tRadioButton_3").equals("Y"))
      this.pat.modifyBloodRHType("-");
    else {
      this.pat.modifyBloodRHType("");
    }
    this.pat.modifyPostCode(getValueString("POST_CODE"));
    this.pat.modifyAddress(getValueString("ADDRESS"));

    this.pat.modifyResidPostCode(getValueString("RESID_POST_CODE"));
    this.pat.modifyResidAddress(getValueString("RESID_ADDRESS"));

    this.pat.modifyCompanyAddress(getValueString("ADDRESS_COMPANY"));
    this.pat.modifyCompanyPost(getValueString("POST_COMPANY"));
    this.pat.modifyContactsName(getValueString("CONTACTS_NAME"));

    String sql002 = "SELECT COUNT(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" + 
      getValueString("RELATION_CODE") + "' ";
    TParm result002 = new TParm(TJDODBTool.getInstance().select(sql002));
    int count002 = Integer.parseInt(result002.getValue("COUNT", 0));
    if (count002 == 0)
      this.pat.modifyRelationCode(this.oldRelationCode);
    else {
      this.pat.modifyRelationCode(getValueString("RELATION_CODE"));
    }

    this.pat.modifyContactsTel(getValueString("CONTACTS_TEL"));
    this.pat.modifyContactsAddress(getValueString("CONTACTS_ADDRESS"));

    this.pat.modifyEducationCode(getValueString("EDUCATION_CODE"));
    this.pat.modifyPeligionCode(getValueString("RELIGION_CODE"));
    this.pat.modifySpouseIdno(getValueString("SPOUSE_IDNO"));
    this.pat.modifyFatherIdno(getValueString("FATHER_IDNO"));
    this.pat.modifyMotherIdno(getValueString("MOTHER_IDNO"));

    this.pat.modifyFirstAdmDate(
      TCM_Transform.getTimestamp(getValue("FIRST_ADM_DATE")));
    this.pat.modifyDeadDate(TCM_Transform.getTimestamp(getValue("DEAD_DATE")));
    this.pat.modifyRcntOpdDate(
      TCM_Transform.getTimestamp(getValue("RCNT_OPD_DATE")));
    this.pat.modifyRcntOpdDept(getValueString("RCNT_OPD_DEPT"));
    this.pat.modifyRcntEmgDate(
      TCM_Transform.getTimestamp(getValue("RCNT_EMG_DATE")));
    this.pat.modifyRcntEmgDept(getValueString("RCNT_EMG_DEPT"));
    this.pat.modifyRcntIpdDate(
      TCM_Transform.getTimestamp(getValue("RCNT_IPD_DATE")));
    this.pat.modifyRcntIpdDept(getValueString("RCNT_IPD_DEPT"));
    this.pat.modifyRcntMissDate(
      TCM_Transform.getTimestamp(getValue("RCNT_MISS_DATE")));
    this.pat.modifyRcntMissDept(getValueString("RCNT_MISS_DEPT"));
    this.pat.modifyAdultExamDate(
      TCM_Transform.getTimestamp(getValue("ADULT_EXAM_DATE")));
    this.pat.modifySmearRcntDate(
      TCM_Transform.getTimestamp(getValue("SMEAR_RCNT_DATE")));
    this.pat.modifyKidExamRcntDate(
      TCM_Transform.getTimestamp(getValue("KID_EXAM_RCNT_DATE")));
    this.pat.modifyKidInjRcntDate(
      TCM_Transform.getTimestamp(getValue("KID_INJ_RCNT_DATE")));
    this.pat.modifyLMPDate(TCM_Transform.getTimestamp(getValue("LMP_DATE")));
    this.pat.modifyBreastfeedStartDate(
      TCM_Transform.getTimestamp(getValue("BREASTFEED_STARTDATE")));
    this.pat.modifyBreastfeedEndDate(
      TCM_Transform.getTimestamp(getValue("BREASTFEED_ENDDATE")));
    this.pat.modifyPat1Code(getValueString("PAT1_CODE"));
    this.pat.modifyPat2Code(getValueString("PAT2_CODE"));
    this.pat.modifyPat3Code(getValueString("PAT3_CODE"));
    this.pat.modifyPregnantDate(
      TCM_Transform.getTimestamp(getValue("PREGNANT_DATE")));
    this.pat.modifyDescription(getValueString("DESCRIPTION"));
    if (getValue("BORNIN_FLG").equals("Y"))
      this.pat.modifyBorninFlg(true);
    else {
      this.pat.modifyBorninFlg(false);
    }

    if (getValue("PREMATURE_FLG").equals("Y"))
      this.pat.modifyPrematureFlg(true);
    else
      this.pat.modifyPrematureFlg(false);
    if (getValue("HANDICAP_FLG").equals("Y"))
      this.pat.modifyHandicapFlg(true);
    else
      this.pat.modifyHandicapFlg(false);
    if (getValue("BLACK_FLG").equals("Y"))
      this.pat.modifyBlackFlg(true);
    else
      this.pat.modifyBlackFlg(false);
    if (getValue("NAME_INVISIBLE_FLG").equals("Y"))
      this.pat.modifyNameInvisibleFlg(true);
    else
      this.pat.modifyNameInvisibleFlg(false);
    if (getValue("LAW_PROTECT_FLG").equals("Y"))
      this.pat.modifyLawProtectFlg(true);
    else
      this.pat.modifyLawProtectFlg(false);
    this.pat.modifyPatBelong(getValueString("PAT_BELONG"));

    this.pat.modifySecurityCategory(getValueString("SECURITY_CATEGORY"));
    return this.pat;
  }

  public void onSave()
  {
    onSavePopedem();
    if (this.action.equals("NEW"))
    {
      this.pat = modifyValue();
      String patName = this.pat.getName();
      if (StringUtil.isNullString(patName)) {
        messageBox("请填写病患姓名");
        return;
      }

      if (!this.pat.onNew()) {
        messageBox("E0005");
      } else {
        messageBox("P0005");
        setValue("MR_NO", this.pat.getMrNo());
        callFunction("UI|PHOTO_BOTTON|setEnabled", new Object[] { Boolean.valueOf(true) });
        this.action = "EDIT";
      }
    }
    else {
      TParm parm = new TParm(TJDODBTool.getInstance().select(
        "SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + this.pat.getMrNo() + 
        "'"));

      TParm caseParm = new TParm(TJDODBTool.getInstance().select(
        "SELECT CASE_NO FROM MRO_RECORD WHERE MR_NO = '" + this.pat.getMrNo() + 
        "' AND OUT_DATE IS NULL "));
      this.pat = modifyValue();

      if (this.pat.onSave())
      {
        if (caseParm.getCount("CASE_NO") > 0)
        {
          TParm result = new TParm();
          String user_id = Operator.getID();
          String user_ip = Operator.getIP();
          TParm opt = new TParm();
          opt.setData("MR_NO", this.pat.getMrNo());
          opt.setData("CASE_NO", caseParm.getValue("CASE_NO", 0));
          opt.setData("OPT_USER", user_id);
          opt.setData("OPT_TERM", user_ip);
          result = MROTool.getInstance().updateMROPatInfo(opt);
          if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
          }
        }

        messageBox("P0005");
        writeLog(this.pat.getMrNo(), parm.getRow(0), "UPDATE");
      }
      else {
        messageBox("E0005");
      }

    }

    if ("HRM".equals(this.recpt)) {
      setReturnValue(this.pat.getMrNo());
      closeWindow();
    }
  }

  public void onPhoto()
    throws IOException
  {
    String mrNo = getValue("MR_NO").toString();
    String photoName = mrNo + ".jpg";
    String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
    new File(dir).mkdirs();
    JMStudio jms = JMStudio.openCamera(dir + photoName);
    jms.addListener("onCameraed", this, "sendpic");
  }

  public void onRegist()
  {
    JMFRegistry jmfr = new JMFRegistry();
    jmfr.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent event) {
        event.getWindow().dispose();
        System.exit(0);
      }
    });
    jmfr.setVisible(true);
  }

  public void sendpic(Image image)
  {
    String mrNo = getValue("MR_NO").toString();
    String photoName = mrNo + ".jpg";
    String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
    String localFileName = dir + photoName;
    try {
      byte[] data = FileTool.getByte(localFileName);
      new File(localFileName).delete();

      String root = TIOM_FileServer.getRoot();
      dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
      dir = root + dir + mrNo.substring(0, 3) + "\\" + 
        mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";

      TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir + 
        photoName, data);
    } catch (Exception localException) {
    }
    viewPhoto(this.pat.getMrNo());
  }

  public void onNew()
  {
    onClear();
    setValue("MR_NO", "");
    this.action = "NEW";
    this.pat = new Pat();
    callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(true) });
    callFunction("UI|delete|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|MR_NO|setEnabled", new Object[] { Boolean.valueOf(false) });
    onSavePopedem();
  }

  public void onSameto1()
  {
    setValue("RESID_POST_CODE", getValue("POST_CODE"));

    setValue("RESID_ADDRESS", getValue("ADDRESS"));
  }

  public void onSameto3()
  {
    setValue("POST_COMPANY", getValue("POST_CODE"));

    setValue("ADDRESS_COMPANY", getValue("ADDRESS"));
  }

  public void onSameto2()
  {
    setValue("CONTACTS_ADDRESS", getValue("ADDRESS"));
  }

  public void onClear()
  {
    ((TRadioButton)getComponent("tRadioButton_0")).setSelected(true);
    this.pat = new Pat();
    TPanel photo = (TPanel)getComponent("VIEW_PANEL");
    Image image = null;

    Pic pic = new Pic(image);
    pic.setSize(photo.getWidth(), photo.getHeight());
    pic.setLocation(0, 0);
    photo.removeAll();
    photo.add(pic);
    pic.repaint();

    callFunction("UI|PHOTO_BOTTON|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|new|setEnabled", new Object[] { Boolean.valueOf(true) });
    callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
    callFunction("UI|MR_NO|setEnabled", new Object[] { Boolean.valueOf(true) });
    clearValue("E_MAIL;COMPANY_DESC;HEIGHT;WEIGHT;BLOOD_TYPE;CELL_PHONE;TEL_COMPANY;TEL_HOME;SPECIES_CODE;OCC_CODE;MARRIAGE_CODE;NATION_CODE");
    clearValue("SPOUSE_IDNO;FATHER_IDNO;MOTHER_IDNO;EDUCATION_CODE;RELIGION_CODE");
    clearValue("FIRST_ADM_DATE;DEAD_DATE;RCNT_OPD_DATE;RCNT_OPD_DEPT;RCNT_EMG_DATE;RCNT_EMG_DEPT;RCNT_IPD_DATE;RCNT_IPD_DEPT;RCNT_MISS_DATE;RCNT_MISS_DEPT");
    clearValue("ADULT_EXAM_DATE;SMEAR_RCNT_DATE;KID_EXAM_RCNT_DATE;KID_INJ_RCNT_DATE;DESCRIPTION;BORNIN_FLG;NEWBORN_SEQ;PREMATURE_FLG;HANDICAP_FLG;BLACK_FLG");
    clearValue("NAME_INVISIBLE_FLG;LAW_PROTECT_FLG;LMP_DATE;BREASTFEED_STARTDATE;BREASTFEED_ENDDATE;PAT1_CODE;PAT2_CODE;PAT3_CODE;PREGNANT_DATE;EDUCATION_CODE");
    clearValue("SPOUSE_IDNO;FATHER_IDNO;MOTHER_IDNO;EDUCATION_CODE;RELIGION_CODE");
    clearValue("CONTACTS_NAME;RELATION_CODE;CONTACTS_ADDRESS;tComboBox_9;tComboBox_10;CONTACTS_TEL;RESID_POST_CODE;tComboBox_7;tComboBox_8;RESID_ADDRESS");
    clearValue("POST_CODE;ADDRESS");
    clearValue("PAT_NAME;PY1;PAT_NAME1;PY2;BIRTH_DATE;SEX_CODE;MERGE_FLG;MERGE_TOMRNO;FOREIGNER_FLG;CTZ1_CODE;CTZ2_CODE;IDNO;MR_NO;IPD_NO;CTZ3_CODE;HOMEPLACE_CODE");
    clearValue("BIRTHPLACE;ADDRESS_COMPANY;POST_COMPANY");
    clearValue("NHICARD_NO;NHI_NO;PAT_BELONG");
    clearValue("SECURITY_CATEGORY");
    onSavePopedem();
  }

  public void onMergeFlg()
  {
    TTextField text = (TTextField)
      callFunction("UI|MERGE_TOMRNO|getThis", new Object[0]);
    text.setEditable(false);
    if ("Y".equals(getValue("MERGE_FLG")))
      text.setEditable(true);
  }

  public void onDelate()
  {
    this.pat.onDelete();

    writeLog(this.pat.getMrNo(), new TParm(), "DELETE");
  }

  public Object onCode()
  {
    if (TCM_Transform.getString(getValue("PAT_NAME")).length() < 1) {
      return null;
    }
    String value = TMessage.getPy(getValueString("PAT_NAME"));
    if ((value == null) || (value.length() < 1)) {
      return null;
    }
    setValue("PAT_NAME1", value);

    ((TTextField)getComponent("IDNO")).grabFocus();
    return null;
  }

  public boolean onClosing()
  {
    if ("RESV".equals(this.recpt)) {
      TParm result = new TParm();
      result.setData("MR_NO", getValueString("MR_NO"));
      setReturnValue(result);
    }
    return true;
  }

  private void writeLog(String mr_no, TParm patParm, String action)
  {
    String insert_sql = "INSERT INTO SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) VALUES('" + 
      mr_no + "', SYSDATE, '#', '#', '#', '" + 
      Operator.getID() + "', '" + Operator.getIP() + "')";
    TParm parm = new TParm();

    String[] columns = { 
      "FOREIGNER_FLG", 
      "IDNO", 
      "HOMEPLACE_CODE", 
      "MERGE_FLG", 
      "MERGE_TOMRNO", 
      "IPD_NO", 
      "PAT_NAME", 
      "PY1", 
      "PAT_NAME1", 
      "PY2", 
      "BIRTH_DATE", 
      "SEX_CODE", 
      "CTZ1_CODE", 
      "CTZ2_CODE", 
      "CTZ3_CODE", 
      "NATION_CODE", 
      "SPECIES_CODE", 
      "E_MAIL", 
      "TEL_HOME", 
      "TEL_COMPANY", 
      "OCC_CODE", 
      "COMPANY_DESC", 
      "CELL_PHONE", 
      "MARRIAGE_CODE", 
      "HEIGHT", 
      "WEIGHT", 
      "BLOOD_TYPE", 
      "BLOOD_RH_TYPE", 
      "POST_CODE", 
      "ADDRESS", 
      "RESID_POST_CODE", 
      "RESID_ADDRESS", 
      "CONTACTS_NAME", 
      "RELATION_CODE", 
      "CONTACTS_TEL", 
      "CONTACTS_ADDRESS", 
      "EDUCATION_CODE", 
      "RELIGION_CODE", 
      "SPOUSE_IDNO", 
      "FATHER_IDNO", 
      "MOTHER_IDNO", 
      "FIRST_ADM_DATE", 
      "DEAD_DATE", 
      "RCNT_OPD_DATE", 
      "RCNT_OPD_DEPT", 
      "RCNT_EMG_DATE", 
      "RCNT_EMG_DEPT", 
      "RCNT_IPD_DATE", 
      "RCNT_IPD_DEPT", 
      "RCNT_MISS_DATE", 
      "RCNT_MISS_DEPT", 
      "ADULT_EXAM_DATE", 
      "SMEAR_RCNT_DATE", 
      "KID_EXAM_RCNT_DATE", 
      "KID_INJ_RCNT_DATE", 
      "LMP_DATE", "BREASTFEED_STARTDATE", 
      "BREASTFEED_ENDDATE", 
      "PAT1_CODE", 
      "PAT2_CODE", 
      "PAT3_CODE", "PREGNANT_DATE", "DESCRIPTION", 
      "BORNIN_FLG", 
      "PREMATURE_FLG", 
      "HANDICAP_FLG", "BLACK_FLG", "NAME_INVISIBLE_FLG", 
      "LAW_PROTECT_FLG", "MR_NO", 
      "DELETE_FLG", "BIRTHPLACE", "ADDRESS_COMPANY", "POST_COMPANY", 
      "NHI_NO", "NHICARD_NO" };

    String[] columnNames = { "外国人注记", "身份证号", "出生地", "合并注记", "母亲的病案号", 
      "住院号", "姓名", "拼音号", "姓名2", "助记码", 
      "出生日期", "性别代码", "身份一", "身份二", "身份三", 
      "国籍代码", "种族", "邮箱", "家里电话", "单位电话", 
      "职业类别代码", "工作单位", "手机号码", "婚姻状态", "身高", 
      "体重", "血型", "RH血型", "邮编", "通信地址", 
      "户籍邮编", "户籍地址", "紧急联系人", "紧急联系人关系", "紧急联系人电话", 
      "紧急联系人地址", "教育程度代码", "宗教", "配偶身份证号", "父亲身份证号", 
      "母亲身份证号", "初诊日期", "死亡日期", "最近门诊日期", "最近门诊科别", 
      "最近急诊日期", "最近急诊科别", "最近住院日期", "最近住院科别", "最近爽约日期", 
      "最近爽约科别", "最近成人健检日期", "最近抹片检查日期", "最近幼儿健诊日期", "最近幼儿注射日期", 
      "LMP日期", "哺乳起始日", "哺乳迄日", "病生理状态一", "病生理状态二", 
      "病生理状态三", "预产期", "备注", "本院出生", "早产儿", 
      "残疾", "黑名单", "隐名注记", "法规隐密保护功能", "病案号", 
      "删除", "籍贯", "单位地址", "单位邮编", 
      "健康卡号", "医保卡号" };

    if ("UPDATE".equals(action)) {
      for (int i = 0; i < columns.length; i++)
        if (!"DELETE_FLG".equals(columns[i]))
        {
          if ("BLOOD_RH_TYPE".equals(columns[i])) {
            String rh_type = "";
            if ("Y".equals(getValueString("BLOOD_RH_TYPE"))) {
              rh_type = "+";
            }
            else if ("Y"
              .equals(getValueString("tRadioButton_3")))
              rh_type = "-";
            else {
              rh_type = "";
            }
            if (!rh_type.equals(patParm.getData(columns[i]))) {
              parm.addData("MODI_ITEM", columnNames[i]);
              parm.addData("ITEM_OLD", patParm.getData(columns[i]));
              parm.addData("ITEM_NEW", rh_type);
            }

          }
          else if (("BIRTH_DATE".equals(columns[i])) || 
            ("FIRST_ADM_DATE".equals(columns[i])) || 
            ("RCNT_OPD_DATE".equals(columns[i])) || 
            ("RCNT_EMG_DATE".equals(columns[i])) || 
            ("RCNT_IPD_DATE".equals(columns[i])) || 
            ("RCNT_MISS_DATE".equals(columns[i])) || 
            ("ADULT_EXAM_DATE".equals(columns[i])) || 
            ("KID_EXAM_RCNT_DATE".equals(columns[i])) || 
            ("LMP_DATE".equals(columns[i])) || 
            ("PREGNANT_DATE".equals(columns[i])) || 
            ("DEAD_DATE".equals(columns[i])) || 
            ("SMEAR_RCNT_DATE".equals(columns[i])) || 
            ("KID_INJ_RCNT_DATE".equals(columns[i])) || 
            ("BREASTFEED_STARTDATE".equals(columns[i])) || 
            ("BREASTFEED_ENDDATE".equals(columns[i]))) {
            String new_date_time = getValueString(columns[i]);
            String old_date_time = TypeTool.getString(patParm
              .getData(columns[i]));
            if ((old_date_time != null) && (old_date_time.length() >= 10)) {
              new_date_time = new_date_time.substring(0, 10);
              old_date_time = old_date_time.substring(0, 10);
              if (!old_date_time.equals(new_date_time)) {
                parm.addData("MODI_ITEM", columnNames[i]);
                parm.addData("ITEM_OLD", old_date_time);
                parm.addData("ITEM_NEW", new_date_time);
              }

            }
            else if ((new_date_time != null) && 
              (new_date_time.length() > 10)) {
              parm.addData("MODI_ITEM", columnNames[i]);
              parm.addData("ITEM_OLD", "");
              parm.addData("ITEM_NEW", 
                new_date_time.substring(0, 10));
            }

          }
          else if (("HEIGHT".equals(columns[i])) || 
            ("WEIGHT".equals(columns[i]))) {
            double new_double = 
              TypeTool.getDouble(getValueString(columns[i]));
            double old_double = TypeTool.getDouble(patParm
              .getData(columns[i]));
            if (new_double != old_double) {
              parm.addData("MODI_ITEM", columnNames[i]);
              parm.addData("ITEM_OLD", Double.valueOf(old_double));
              parm.addData("ITEM_NEW", Double.valueOf(new_double));
            }

          }
          else if (!getValueString(columns[i]).equals(
            patParm.getData(columns[i]))) {
            parm.addData("MODI_ITEM", columnNames[i]);
            parm.addData("ITEM_OLD", patParm.getData(columns[i]));
            parm.addData("ITEM_NEW", 
              getValueString(columns[i]));
          }
        }
    }
    else {
      for (int i = 0; i < columns.length; i++) {
        parm.addData("MODI_ITEM", "删除");
        parm.addData("ITEM_OLD", "N");
        parm.addData("ITEM_NEW", "Y");
      }
    }
    parm.setData("SQL", insert_sql);
    if ((parm == null) || (parm.getCount("MODI_ITEM") <= 0)) {
      return;
    }

    TParm result = TIOM_AppServer.executeAction(
      "action.sys.SYSWriteLogAction", "onSYSPatLog", parm);
    if ((result == null) || (result.getErrCode() < 0))
      messageBox("LOG写入失败！");
  }

  public void onCheckMrNo()
  {
    String srcMrNo = PatTool.getInstance().checkMrno(getValueString("MERGE_TOMRNO").trim());
    setValue("MERGE_TOMRNO", srcMrNo);

    Pat patMe = Pat.onQueryByMrNo(srcMrNo);
    String mrNo = getValueString("MR_NO").trim();
    String insCard1 = getInsInfo(mrNo);
    String insCard2 = getInsInfo(srcMrNo);

    if ((this.pat.getIdNo().length() > 0) && (patMe.getIdNo().length() > 0)) {
      if (!this.pat.getIdNo().equals(patMe.getIdNo())) {
        messageBox("合并病案号的两个病患的身份证不同，请确认是否为同一个人");
        callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      }

    }
    else if ((insCard1.length() > 0) && (insCard2.length() > 0)) {
      if (!insCard1.equals(insCard2)) {
        messageBox("合并病案号的两个病患的医保卡号不同，请确认是否为同一个人");
        callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      }

    }
    else if (this.pat.getName().equals(patMe.getName())) {
      if (this.pat.getSexCode().equals(patMe.getSexCode())) {
        if (!this.pat.getBirthday().equals(patMe.getBirthday())) {
          messageBox("合并病案号的两个病患的出生日期不同，请确认是否为同一个人");
          callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
        }
      }
      else
      {
        messageBox("合并病案号的两个病患的性别不同，请确认是否为同一个人");
        callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      }
    }
    else
    {
      messageBox("合并病案号的两个病患的姓名不同，请确认是否为同一个人");
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      return;
    }

    if (getAdmInpCount(mrNo) > 0) {
      messageBox(mrNo + "为住院患者，不应合并病案号");
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      return;
    }if (getAdmInpCount(srcMrNo) > 0) {
      messageBox(srcMrNo + "为住院患者，不应合并病案号");
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      return;
    }

    if (getOpdOrderCount(mrNo) > 0) {
      messageBox("门急诊患者" + mrNo + "有未缴费医嘱项目不允许合并");
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      return;
    }if (getOpdOrderCount(srcMrNo) > 0) {
      messageBox("门急诊患者" + srcMrNo + "有未缴费医嘱项目不允许合并");
      callFunction("UI|save|setEnabled", new Object[] { Boolean.valueOf(false) });
      return;
    }

    if (checkMeReg(mrNo, srcMrNo));
  }

  public int getAdmInpCount(String mrNo)
  {
    String sql = "SELECT COUNT(CASE_NO) COUNT FROM ADM_INP WHERE MR_NO='" + mrNo + "' AND DS_DATE IS NULL";
    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    return parm.getInt("COUNT", 0);
  }

  public int getOpdOrderCount(String mrNo) {
    String sql = "SELECT COUNT(CASE_NO) COUNT FROM OPD_ORDER WHERE MR_NO='" + mrNo + "' AND (BILL_FLG ='N' OR BILL_FLG IS NULL)";
    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    return parm.getInt("COUNT", 0);
  }

  public String getInsInfo(String mrNo)
  {
    String insCardNo = "";
    String sql = "SELECT INSCARD_NO FROM INS_MZ_CONFIRM WHERE MR_NO='" + mrNo + "'";
    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    if (parm.getCount() > 0) {
      insCardNo = parm.getValue("INSCARD_NO", 0);
    }

    return insCardNo;
  }

  public boolean checkMeReg(String mrNo, String mrRegMrNo) {
    String sql = "SELECT MERGE_FLG,MERGE_TOMRNO FROM SYS_PATINFO WHERE MR_NO='" + mrRegMrNo + "'";
    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    if (parm.getCount() < 0) {
      messageBox(mrRegMrNo + "不存在病患信息，请重新输入");
      clearValue("MERGE_TOMRNO");
      return true;
    }
    if ((parm.getBoolean("MERGE_FLG", 0)) && 
      (mrNo.equals(parm.getValue("MERGE_TOMRNO", 0)))) {
      messageBox(mrRegMrNo + "存在合并信息，合并病案号为" + mrNo + ",不应在进行合并");
      return true;
    }

    return false;
  }

  public String dateToString(Date date)
  {
    String dateStr = "";

    DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try
    {
      dateStr = sdf2.format(date);
      System.out.println(dateStr);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dateStr;
  }

  class Pic extends JLabel
  {
    Image image;

    public Pic(Image image)
    {
      this.image = image;
    }

    public void paint(Graphics g) {
      g.setColor(new Color(161, 220, 230));
      g.fillRect(4, 15, 100, 100);
      if (this.image != null)
        g.drawImage(this.image, 4, 15, 105, 142, null);
    }
  }
}