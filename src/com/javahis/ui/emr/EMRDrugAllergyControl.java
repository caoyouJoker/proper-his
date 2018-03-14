package com.javahis.ui.emr;

import java.awt.Component;
import java.sql.Timestamp;

import jdo.odi.OdiDrugAllergy;
import jdo.odi.OdiObject;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

public class EMRDrugAllergyControl extends TControl{
	private static final String GET_CF_SQL="SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT' ORDER BY ID";
    private static final String GET_OTHER_SQL="SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE' ORDER BY ID";
	
	private TRadioButton CF_RADIO;
	private TComboBox CF_COMBO;
	private TTextField CF_NOTE;
	
	private TRadioButton OTHER_RADIO;
	private TComboBox OTHER_COMBO;
	private TTextField OTHER_NOTE;
	
	private TRadioButton PHA_RADIO;
	private TComboBox PHA_COMBO;
	private TTextField PHA;
	private TTextField PHA_CODE;
	private TTextField PHA_NOTE;
	
	private TRadioButton NONE_RADIO;
	
	private TTable TABLE;
	
	private String caseNo;
	private String admType;
	
	private TParm inParm;
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	public void initPage(){
		cfInit();
		otherInit();
		phaInit();
		noneInit();
		TParm para = (TParm) this.getParameter();
		TABLE = (TTable) this.getComponent("TABLE");
		inParm = (TParm)this.getParameter();
		caseNo = para.getValue("CASE_NO");
		admType = para.getValue("ADM_TYPE");
		//caseNo = "151117000992";
		//admType = "I";
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());
		patientInit();
		onQuery();
		
	}
	
	public void patientInit(){
		String sql = getPatientSql(caseNo,admType);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("MR_NO", parm.getValue("MR_NO", 0));
		this.setValue("NAME", parm.getValue("PAT_NAME", 0));
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("DR_CODE", Operator.getID());
		this.setValue("ADM_TYPE", admType);
	}
	
	public String getPatientSql(String caseNo,String admType){
		String sql = ""; 
		if("I".equals(admType)){
			sql = " SELECT " +
					" A.MR_NO, " +
					" A.PAT_NAME " +
				  " FROM " +
				  	" SYS_PATINFO A," +
				  	" ADM_INP B " +
				  " WHERE " +
				  	" B.MR_NO = A.MR_NO " +
				  	" AND B.CASE_NO = '"+caseNo+"' ";
		}else if("E".equals(admType) || "O".equals(admType)){
			sql = " SELECT " +
					" A.MR_NO, " +
					" A.PAT_NAME " +
				  " FROM " +
				  	" SYS_PATINFO A," +
				  	" REG_PATADM B " +
				  " WHERE " +
				  	" B.MR_NO = A.MR_NO " +
				  	" AND B.CASE_NO = '"+caseNo+"' ";
		}
		return sql;
		
	}
	
	public void cfInit(){
		//过敏成分
		CF_RADIO = (TRadioButton) this.getComponent("CF_RADIO");
		CF_COMBO = (TComboBox) this.getComponent("CF_COMBO");
		CF_NOTE = (TTextField) this.getComponent("CF_NOTE");
		TParm cfParm = new TParm(TJDODBTool.getInstance().select(GET_CF_SQL));
		String strCfCombo = "";
		strCfCombo = "[[id,name],[,]";
		for(int i = 0 ; i < cfParm.getCount() ; i++){
			strCfCombo += ",["+cfParm.getValue("ID", i)+","+cfParm.getValue("CHN_DESC", i)+"]";
		}
		strCfCombo += "]";
		CF_COMBO.setStringData(strCfCombo);
	}
	
	public void otherInit(){
		//其他过敏
		OTHER_RADIO = (TRadioButton) this.getComponent("OTHER_RADIO");
		OTHER_COMBO = (TComboBox) this.getComponent("OTHER_COMBO");
		OTHER_NOTE = (TTextField) this.getComponent("OTHER_NOTE");
		
		TParm otherParm = new TParm(TJDODBTool.getInstance().select(GET_OTHER_SQL));
		String strOtherCombo = "";
		strOtherCombo = "[[id,name],[,]";
		for(int i = 0 ; i < otherParm.getCount() ; i++){
			strOtherCombo += ",["+otherParm.getValue("ID", i)+","+otherParm.getValue("CHN_DESC", i)+"]";
		}
		strOtherCombo += "]";
		OTHER_COMBO.setStringData(strOtherCombo);
	}
	
	public void phaInit(){
		PHA_RADIO = (TRadioButton) this.getComponent("PHA_RADIO");
		PHA = (TTextField) this.getComponent("PHA");
		PHA_CODE = (TTextField) this.getComponent("PHA_CODE");
		PHA_NOTE = (TTextField) this.getComponent("PHA_NOTE");
		PHA_COMBO = (TComboBox) this.getComponent("PHA_COMBO");
		
		//药品过敏
		TParm parm = new TParm();
		parm.setData("ODI_ORDER_TYPE", "A");
		// 设置弹出菜单
		PHA.setPopupMenuParameter("GMB", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		PHA.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
		"popReturn");
	}
	
	public void phaSelect(){//add by guoy 20151217
		PHA = (TTextField) this.getComponent("PHA");
		PHA_CODE = (TTextField) this.getComponent("PHA_CODE");
		PHA_COMBO = (TComboBox) this.getComponent("PHA_COMBO");
		PHA_CODE.setValue("");
		PHA.setValue("");
		
		//默认弹出菜单
		TParm tparm = new TParm();
		tparm.setData("ODI_ORDER_TYPE", "A");
		// 设置弹出菜单
		PHA.setPopupMenuParameter("GMB", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), tparm);
		
//		if("B".equals(PHA_COMBO.getValue())){
//			//药品过敏
//			TParm parm = new TParm();
//			parm.setData("ODI_ORDER_TYPE", "A");
//			// 设置弹出菜单
//			PHA.setPopupMenuParameter("GMB", getConfigParm().newConfig(
//					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
//		}
		if("D".equals(PHA_COMBO.getValue())){
			//药品过敏
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "D");
			// 设置弹出菜单
			PHA.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
		}
		if("E".equals(PHA_COMBO.getValue())){
			//药品过敏
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "E");
			// 设置弹出菜单
			PHA.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
		}
		
		PHA.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
		"popReturn");
	}
	
	
	
	public void noneInit(){
		NONE_RADIO = (TRadioButton) this.getComponent("NONE_RADIO");
	}
	
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("PHA", parm.getValue("ORDER_DESC"));
		this.setValue("PHA_CODE", parm.getValue("ORDER_CODE"));
	}
	
	public void onSel(){
		if(PHA_RADIO.isSelected()){
			CF_COMBO.setEnabled(false);
			CF_NOTE.setEnabled(false);
			
			OTHER_COMBO.setEnabled(false);
			OTHER_NOTE.setEnabled(false);
			
			PHA_COMBO.setEnabled(true);
			PHA.setEnabled(true);
			PHA_NOTE.setEnabled(true);
			onClear();
		}else if(CF_RADIO.isSelected()){
			CF_COMBO.setEnabled(true);
			CF_NOTE.setEnabled(true);
			
			OTHER_COMBO.setEnabled(false);
			OTHER_NOTE.setEnabled(false);
			
			PHA_COMBO.setEnabled(false);
			PHA.setEnabled(false);
			PHA_NOTE.setEnabled(false);
			onClear();
		}else if(OTHER_RADIO.isSelected()){
			CF_COMBO.setEnabled(false);
			CF_NOTE.setEnabled(false);
			
			OTHER_COMBO.setEnabled(true);
			OTHER_NOTE.setEnabled(true);
			
			PHA_COMBO.setEnabled(false);
			PHA.setEnabled(false);
			PHA_NOTE.setEnabled(false);
			onClear();
		}else if(NONE_RADIO.isSelected()){
			CF_COMBO.setEnabled(false);
			CF_NOTE.setEnabled(false);
			
			OTHER_COMBO.setEnabled(false);
			OTHER_NOTE.setEnabled(false);
			
			PHA_COMBO.setEnabled(false);
			PHA.setEnabled(false);
			PHA_NOTE.setEnabled(false);
			onClear();
		}
	}
	
	public void onClear(){
		CF_COMBO.setValue("");
		CF_NOTE.setValue("");
		
		OTHER_COMBO.setValue("");
		OTHER_NOTE.setValue("");
		
		PHA_COMBO.setValue("");
		PHA_CODE.setValue("");
		PHA.setValue("");
		PHA_NOTE.setValue("");
		
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());
	}
	
	public void onSave(){
		if(PHA_RADIO.isSelected()){
			savePha();
		}else if(CF_RADIO.isSelected()){
			saveCf();
		}else if(OTHER_RADIO.isSelected()){
			saveOther();
		}else if(NONE_RADIO.isSelected()){
			saveNone();
		}
	}
	
	public int checkCount(String date,String type,String code){
		String mrNo = this.getValueString("MR_NO");
		String sql = " SELECT " + 
						" * " +
					 " FROM " +
						" OPD_DRUGALLERGY " +
					 " WHERE " +
						" MR_NO='"+mrNo+"' " +
						" AND ADM_DATE='"+date+"' " +
						" AND DRUG_TYPE='"+type+"' " +
						" AND DRUGORINGRD_CODE='"+code+"'";
		System.out.println("checkCount sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("check parm:"+parm);
		return parm.getCount();
	}
	
	public void savePha(){
		String type = PHA_COMBO.getValue();
		String phaCode = PHA_CODE.getValue();
		String date = this.getValueString("ADM_DATE");
		if("".equals(type)){
			this.messageBox("请选择过敏药品类别");
			return;
		}
		if("".equals(phaCode)){
			this.messageBox("请选择过敏药品");
			return;
		}
		if("".equals(date)){
			this.messageBox("请选择就诊日期");
			return;
		}
		
		int count = checkCount(date.replace("-", "/").substring(0, 10),type,phaCode);
		
		TParm parm = new TParm();
		parm.setData("ADM_DATE", date.replace("-", "/").substring(0, 10));
		parm.setData("DRUG_TYPE", type);
		parm.setData("DRUGORINGRD_CODE", phaCode);
		parm.setData("ALLERGY_NOTE", this.getValueString("PHA_NOTE"));
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("DR_CODE", this.getValueString("DR_CODE"));
		if(count > 0){
			onUpdate(parm);
		}else{
			onInsert(parm);
		}
		
	}
	
	public void saveCf(){
		String type = "A";
		String cfCode = CF_COMBO.getValue();
		String date = this.getValueString("ADM_DATE");
		if("".equals(cfCode)){
			this.messageBox("请选择过敏类型");
			return;
		}
		if("".equals(date)){
			this.messageBox("请选择就诊日期");
			return;
		}
		
		int count = checkCount(date.replace("-", "/").substring(0, 10),type,cfCode);
		
		TParm parm = new TParm();
		parm.setData("ADM_DATE", date.replace("-", "/").substring(0, 10));
		parm.setData("DRUG_TYPE", type);
		parm.setData("DRUGORINGRD_CODE", cfCode);
		parm.setData("ALLERGY_NOTE", this.getValueString("CF_NOTE"));
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("DR_CODE", this.getValueString("DR_CODE"));
		if(count > 0){
			onUpdate(parm);
		}else{
			onInsert(parm);
		}
		
	}
	
	public void saveOther(){
		String type = "C";
		String otherCode = OTHER_COMBO.getValue();
		String date = this.getValueString("ADM_DATE");
		if("".equals(otherCode)){
			this.messageBox("请选择其他过敏");
			return;
		}
		if("".equals(date)){
			this.messageBox("请选择就诊日期");
			return;
		}
		
		int count = checkCount(date.replace("-", "/").substring(0, 10),type,otherCode);
		
		TParm parm = new TParm();
		parm.setData("ADM_DATE", date.replace("-", "/").substring(0, 10));
		parm.setData("DRUG_TYPE", type);
		parm.setData("DRUGORINGRD_CODE", otherCode);
		parm.setData("ALLERGY_NOTE", this.getValueString("OTHER_NOTE"));
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("DR_CODE", this.getValueString("DR_CODE"));
		if(count > 0){
			onUpdate(parm);
		}else{
			onInsert(parm);
		}
		
	}
	
	public void saveNone(){
		String type = "N";
		String date = this.getValueString("ADM_DATE");
		if("".equals(date)){
			this.messageBox("请选择就诊日期");
			return;
		}
		
		int count = checkCount(date.replace("-", "/").substring(0, 10),type,"N");
		TParm parm = new TParm();
		parm.setData("ADM_DATE", date.replace("-", "/").substring(0, 10));
		parm.setData("DRUG_TYPE", type);
		parm.setData("DRUGORINGRD_CODE", "N");
		parm.setData("ALLERGY_NOTE", "无");
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("DR_CODE", this.getValueString("DR_CODE"));
		if(count > 0){
			onUpdate(parm);
		}else{
			onInsert(parm);
		}
		
	}
	
	public void onInsert(TParm parm){
		String mrNo = this.getValueString("MR_NO");
		String admDate = parm.getValue("ADM_DATE");
		String drugType = parm.getValue("DRUG_TYPE");
		String drugoringrdCode = parm.getValue("DRUGORINGRD_CODE");
		String deptCode = parm.getValue("DEPT_CODE");
		String drCode = parm.getValue("DR_CODE");
		String allergyNote = parm.getValue("ALLERGY_NOTE");
		String optUser = Operator.getID();
		Timestamp optDate = SystemTool.getInstance().getDate();
		String optTerm = Operator.getIP();
		
		String sql = " INSERT INTO " +
						" OPD_DRUGALLERGY(" +
							" MR_NO, " +
							" ADM_DATE, " +
							" DRUG_TYPE, " +
							" DRUGORINGRD_CODE," +
							" ADM_TYPE, " +
							" CASE_NO, " +
							" DEPT_CODE, " +
							" DR_CODE, " +
							" ALLERGY_NOTE, " +
							" OPT_USER, " +
							" OPT_DATE, " +
							" OPT_TERM ) " +
						" VALUES( " +
							" '"+mrNo+"', " +
							" '"+admDate+"', " +
							" '"+drugType+"', " +
							" '"+drugoringrdCode+"', " +
							" '"+admType+"', " +
							" '"+caseNo+"', " +
							" '"+deptCode+"', " +
							" '"+drCode+"', " +
							" '"+allergyNote+"', " +
							" '"+optUser+"', " +
							" TO_DATE('"+optDate.toString().substring(0, 19).replace("-", "/")+"','yyyy/MM/dd HH24:mi:ss'), " +
							" '"+optTerm+"' )";
		System.out.println("insert sql:"+sql);
		TParm p = new TParm(TJDODBTool.getInstance().update(sql));
		p = updateAdmInpAllergy();
		if(p.getErrCode() < 0){
			this.messageBox("保存失败！");
		}else{
			onClear();
			onQuery();
			this.messageBox("保存成功！");
		}
	}
	
	public void onUpdate(TParm parm){
		String mrNo = this.getValueString("MR_NO");
		String admDate = parm.getValue("ADM_DATE");
		String drugType = parm.getValue("DRUG_TYPE");
		String drugoringrdCode = parm.getValue("DRUGORINGRD_CODE");
		String allergyNote = parm.getValue("ALLERGY_NOTE");
		String optUser = Operator.getID();
		Timestamp optDate = SystemTool.getInstance().getDate();
		String optTerm = Operator.getIP();
		
		String sql = " UPDATE " +
						" OPD_DRUGALLERGY " +
					 " SET " +
					 	" ALLERGY_NOTE = '"+allergyNote+"', " +
					 	" OPT_USER = '"+optUser+"', " +
					 	" OPT_DATE = TO_DATE('"+optDate.toString().substring(0, 19).replace("-", "/")+"','yyyy/MM/dd HH24:mi:ss'), " +
					 	" OPT_TERM = '"+optTerm+"' "+
					 " WHERE " +
					 	" MR_NO = '"+mrNo+"' " +
					 	" AND ADM_DATE = '"+admDate+"' " +
					 	" AND DRUG_TYPE = '"+drugType+"' " +
					 	" AND DRUGORINGRD_CODE = '"+drugoringrdCode+"' ";
		System.out.println("update sql:"+sql);
		TParm p = new TParm(TJDODBTool.getInstance().update(sql));
		p = updateAdmInpAllergy();
		
		if(p.getErrCode() < 0){
			this.messageBox("保存失败！");
		}else{
			onClear();
			onQuery();
			this.messageBox("保存成功！");
		}
	}
	
	public void onQuery(){
		String mrNo = this.getValueString("MR_NO");
		TParm parm = new TParm();
		
		
		String sqlA = " SELECT " +
						" A.ADM_DATE, " +
						" '成分过敏' AS DRUG_NAME, " +
						" A.ALLERGY_NOTE, " +
						" B.CHN_DESC AS ORDER_DESC, " +
						" A.DEPT_CODE, " +
						" A.DR_CODE, " +
						" A.ADM_TYPE, " +
						" A.CASE_NO, " +
						" A.DRUGORINGRD_CODE, " +
						" A.DRUG_TYPE, " +
						" A.MR_NO " +
					" FROM " +
						" OPD_DRUGALLERGY A, " +
						" SYS_DICTIONARY B " +
					" WHERE " +
						" A.MR_NO='"+mrNo+"' " +
						" AND A.DRUG_TYPE='A' "  +
						" AND B.GROUP_ID='PHA_INGREDIENT' " +
						" AND B.ID=A.DRUGORINGRD_CODE";
		TParm parmA = new TParm(TJDODBTool.getInstance().select(sqlA));
		for(int i = 0 ; i < parmA.getCount() ; i++){
			parm.addRowData(parmA, i);
		}
		
		String sqlB = " SELECT " +
							" A.ADM_DATE, " +
							" '特定药品' AS DRUG_NAME, " +
							" A.ALLERGY_NOTE, " +
							" B.ORDER_DESC AS ORDER_DESC, " +
							" A.DEPT_CODE, " +
							" A.DR_CODE, " +
							" A.ADM_TYPE, " +
							" A.CASE_NO, " +
							" A.DRUGORINGRD_CODE, " +
							" A.DRUG_TYPE, " +
							" A.MR_NO " +
						" FROM " +
							" OPD_DRUGALLERGY A, " +
							" SYS_FEE B " +
						" WHERE " +
							" A.MR_NO='"+mrNo+"' " +
							" AND A.DRUG_TYPE='B' "  +
							" AND B.ORDER_CODE=A.DRUGORINGRD_CODE";
		TParm parmB = new TParm(TJDODBTool.getInstance().select(sqlB));
		for(int i = 0 ; i < parmB.getCount() ; i++){
			parm.addRowData(parmB, i);
		}
		
		
		String sqlC = " SELECT " +
						" A.ADM_DATE, " +
						" '其他过敏' AS DRUG_NAME, " +
						" A.ALLERGY_NOTE, " +
						" B.CHN_DESC AS ORDER_DESC, " +
						" A.DEPT_CODE, " +
						" A.DR_CODE, " +
						" A.ADM_TYPE, " +
						" A.CASE_NO, " +
						" A.DRUGORINGRD_CODE, " +
						" A.DRUG_TYPE, " +
						" A.MR_NO " +
					" FROM " +
						" OPD_DRUGALLERGY A, " +
						" SYS_DICTIONARY B " +
					" WHERE " +
						" A.MR_NO='"+mrNo+"' " +
						" AND A.DRUG_TYPE='C' "  +
						" AND B.GROUP_ID='SYS_ALLERGYTYPE' " +
						" AND B.ID=A.DRUGORINGRD_CODE";
		TParm parmC = new TParm(TJDODBTool.getInstance().select(sqlC));
		for(int i = 0 ; i < parmC.getCount() ; i++){
			parm.addRowData(parmC, i);
		}
		
		
		String sqlD = " SELECT " +
						" A.ADM_DATE, " +
						" '药理大分类' AS DRUG_NAME, " +
						" A.ALLERGY_NOTE, " +
						" B.CATEGORY_CHN_DESC AS ORDER_DESC, " +
						" A.DEPT_CODE, " +
						" A.DR_CODE, " +
						" A.ADM_TYPE, " +
						" A.CASE_NO, " +
						" A.DRUGORINGRD_CODE, " +
						" A.DRUG_TYPE, " +
						" A.MR_NO " +
					" FROM " +
						" OPD_DRUGALLERGY A, " +
						" SYS_CATEGORY B " +
					" WHERE " +
						" A.MR_NO='"+mrNo+"' " +
						" AND A.DRUG_TYPE='D' "  +
						" AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE";
		TParm parmD = new TParm(TJDODBTool.getInstance().select(sqlD));
		for(int i = 0 ; i < parmD.getCount() ; i++){
			parm.addRowData(parmD, i);
		}
		
		String sqlE = " SELECT " +
						" A.ADM_DATE, " +
						" '药理次分类' AS DRUG_NAME, " +
						" A.ALLERGY_NOTE, " +
						" B.CATEGORY_CHN_DESC AS ORDER_DESC, " +
						" A.DEPT_CODE, " +
						" A.DR_CODE, " +
						" A.ADM_TYPE, " +
						" A.CASE_NO, " +
						" A.DRUGORINGRD_CODE, " +
						" A.DRUG_TYPE, " +
						" A.MR_NO " +
					" FROM " +
						" OPD_DRUGALLERGY A, " +
						" SYS_CATEGORY B " +
					" WHERE " +
						" A.MR_NO='"+mrNo+"' " +
						" AND A.DRUG_TYPE='E' "  +
						" AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE";
		TParm parmE = new TParm(TJDODBTool.getInstance().select(sqlE));
		for(int i = 0 ; i < parmE.getCount() ; i++){
			parm.addRowData(parmE, i);
		}
		
		String sqlN = " SELECT " +
						" A.ADM_DATE, " +
						" '无' AS DRUG_NAME, " +
						" '' AS ALLERGY_NOTE, " +
						" '无' AS ORDER_DESC, " +
						" A.DEPT_CODE, " +
						" A.DR_CODE, " +
						" A.ADM_TYPE, " +
						" A.CASE_NO, " +
						" A.DRUGORINGRD_CODE, " +
						" A.DRUG_TYPE, " +
						" A.MR_NO " +
					" FROM " +
						" OPD_DRUGALLERGY A " +
					" WHERE " +
						" A.MR_NO='"+mrNo+"' " +
						" AND A.DRUG_TYPE='N' " ;
		TParm parmN = new TParm(TJDODBTool.getInstance().select(sqlN));
		for(int i = 0 ; i < parmN.getCount() ; i++){
			parm.addRowData(parmN, i);
		}
		
		TABLE.setParmValue(parm);
		
	}
	
	public void onTableClick(){
		onClear();
		int row = TABLE.getSelectedRow();
		TParm parm = TABLE.getParmValue().getRow(row);
		
		String drugType = parm.getValue("DRUG_TYPE");
		String admDate = parm.getValue("ADM_DATE");
		String orderDesc = parm.getValue("ORDER_DESC");
		String allergyNote = parm.getValue("ALLERGY_NOTE");
		String drugoringedCode = parm.getValue("DRUGORINGRD_CODE");
		
		if("B".equals(drugType) || "D".equals(drugType) || "E".equals(drugType)){
			PHA_RADIO.setSelected(true);
			CF_RADIO.setSelected(false);
			OTHER_RADIO.setSelected(false);
			NONE_RADIO.setSelected(false);
			onSel();
			this.setValue("ADM_DATE", admDate);
			PHA_COMBO.setValue(drugType);
			PHA_CODE.setValue(drugoringedCode);
			PHA.setValue(orderDesc);
			PHA_NOTE.setValue(allergyNote);
		}else if("A".equals(drugType)){
			PHA_RADIO.setSelected(false);
			CF_RADIO.setSelected(true);
			OTHER_RADIO.setSelected(false);
			NONE_RADIO.setSelected(false);
			onSel();
			this.setValue("ADM_DATE", admDate);
			CF_COMBO.setValue(drugoringedCode);
			CF_NOTE.setValue(allergyNote);
		}else if("C".equals(drugType)){
			PHA_RADIO.setSelected(false);
			CF_RADIO.setSelected(false);
			OTHER_RADIO.setSelected(true);
			NONE_RADIO.setSelected(false);
			onSel();
			this.setValue("ADM_DATE", admDate);
			OTHER_COMBO.setValue(drugoringedCode);
			OTHER_NOTE.setValue(allergyNote);
		}else if("N".equals(drugType)){
			PHA_RADIO.setSelected(false);
			CF_RADIO.setSelected(false);
			OTHER_RADIO.setSelected(false);
			NONE_RADIO.setSelected(true);
			onSel();
			this.setValue("ADM_DATE", admDate);
		}
	}
	
	public void onDelete(){
		onClear();
		int row = TABLE.getSelectedRow();
		TParm parm = TABLE.getParmValue().getRow(row);
		String drugType = parm.getValue("DRUG_TYPE");
		String admDate = parm.getValue("ADM_DATE").toString();
		String drugoringedCode = parm.getValue("DRUGORINGRD_CODE");
		String mrNo = this.getValueString("MR_NO");
		String sql = " DELETE " +
					 " FROM " +
					 	" OPD_DRUGALLERGY " +
					 " WHERE " +
						" MR_NO='"+mrNo+"' " +
						" AND ADM_DATE='"+admDate+"' " +
						" AND DRUG_TYPE='"+drugType+"' " +
						" AND DRUGORINGRD_CODE='"+drugoringedCode+"'";
		System.out.println("delete sql:"+sql);
		TParm p = new TParm(TJDODBTool.getInstance().update(sql));
		p = updateAdmInpAllergy();
		if(p.getErrCode() < 0){
			this.messageBox("删除失败！");
		}else{
			onQuery();
			this.messageBox("删除成功！");
		}
	}
	
	public void onReturnAllergy(){
		onQuery();
		TParm parm = new TParm();
		parm = TABLE.getParmValue();
		String str = "";
		for(int i = 0 ; i < parm.getCount() ; i++){
			str += parm.getValue("ORDER_DESC", i)+";";
		}
		if(parm.getCount()==-1){
			str +="-";
		}
		System.out.println("ALLERGY:"+str);
		inParm.runListener("onReturnAllergy",str);
		this.closeWindow();
	}
	
	public TParm updateAdmInpAllergy(){
		onQuery();
		boolean b = false;
		TParm parm = TABLE.getParmValue();
		TParm result = new TParm();
		if(parm.getCount("DRUG_TYPE") < 1){
			b = false;
		}else{
			for(int i = 0 ; i < parm.getCount() ; i++){
				String drugType = parm.getValue("DRUG_TYPE", i);
				if(!"N".equals(drugType)){
					b = true;
					break;
				}
			}
		}
		
		String updateSql = "";
		if(b){
			updateSql = " UPDATE ADM_INP SET ALLERGY = 'Y' WHERE CASE_NO = '"+caseNo+"'";
		}else{
			updateSql = " UPDATE ADM_INP SET ALLERGY = 'N' WHERE CASE_NO = '"+caseNo+"'";
		}
		result = new TParm(TJDODBTool.getInstance().update(updateSql));
		return result;
	}
	
}
