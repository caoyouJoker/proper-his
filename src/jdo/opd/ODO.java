package jdo.opd;

import java.sql.Timestamp;

import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.data.StringValue;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
import com.dongyang.jdo.TJDOObject;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;

/**
 *
 * <p>Title: ODO主结构
 *
 * <p>Description: ODO主结构</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: javahis
 *
 * @author ehui 20080911
 * @version 1.0
 */
public class ODO extends TJDOObject {
	private Reg reg;
	/**
	 * 患者对象
	 */
	private Pat pat;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 问诊号
	 */
	private String caseNo;
	/**
	 * 医师
	 */
	private String drCode;
	/**
	 * 儿童注记
	 */
	private boolean Child;
	/**
	 * 开立时间
	 */
	private TimestampValue orderDate=new TimestampValue(null);
	/**
	 * 主诉
	 */
	private StringValue subjText = new StringValue(null);
	/**
	 * 现病史
	 */
	private StringValue objText = new StringValue(null);
	/**
	 * 体征
	 */
	private StringValue physexamRec = new StringValue(null);
	/**
	 * 既往史
	 */
	private MedhistoryList medhistoryList;
	/**
	 * 过敏史
	 */
	private DrugAllergyList drugAllergyList;
	/**
	 * 诊断
	 */
	private DiagRecList diagRecList;
	/**
	 * 处方签
	 */
	private PrescriptionList prescriptionList;
	/**
	 * 医嘱历史
	 */
	private OrderHistoryList orderhistoryList;
	/**
	 * 新建标记
	 */
	private boolean newFlg;
	/**
	 * 暂存标记
	 */
	private boolean tempSaveFlg;

	/**
	 * 构造器
	 */
	public ODO() {
		//初始化问诊号
		setCaseNo("");
		//初始化门急住别
		setAdmType("");
		//初始化主诉
		setSubjText("");
		//初始化现病史
		setObjText("");
		//初始化体征
		setPhysexamRec("");
		//初始化医师
		setDrCode("");
		//初始化既往史
		setMedhistoryList(new MedhistoryList());
		//初始化诊断
		setDiagRecList(new DiagRecList());
		//初始化过敏史
		setDrugAllergyList(new DrugAllergyList());
		PrescriptionList p=new PrescriptionList(this);
		p.setReg(this.getReg());
		//初始化处方签
		setPrescriptionList(p);
		//初始化医嘱历史
		setOrderhistoryList(new OrderHistoryList());
		//设置存储状态的标记位
		this.setNewFlg(true);
	}
	/**
	 * 设置儿童注记
	 * @param child boolean
	 */
	public void SetChild(boolean child){
		this.Child=child;
	}
	/**
	 * 得到儿童注记
	 * @return child boolean
	 */
	public boolean isChild(){
		return this.Child;
	}
	/**
	 * 设置挂号对象
	 * @param reg 挂号对象
	 */
	public void setReg(Reg reg){
		if(reg==null){
			this.reg=new Reg();
			return;
		}
		this.reg=reg;
		this.setPat(reg.getPat());
		this.prescriptionList.setReg(this.getReg());
	}
	/**
	 * 得到挂号对象
	 * @return Reg 对象
	 */
	public Reg getReg(){
		return this.reg;
	}
	/**
	 * 设置患者对象
	 * @param pat Pat
	 */
	public void setPat(Pat pat) {
		this.pat = pat;
		
	}

	/**
	 * 得到患者对象
	 * @return Pat
	 */
	public Pat getPat() {
		return pat;
	}

	/**
	 * 设置门急住别
	 * @param value String
	 */
	public void setAdmType(String value) {
		admType = value;
	}

	/**
	 * 得到门急住别
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * 设置问诊号
	 * @param value String
	 */
	public void setCaseNo(String value) {
		this.caseNo = value;
	}

	/**
	 * 得到问诊号
	 * @return String
	 */
	public String getCaseNo() {
		return caseNo;
	}

	/**
	 * 设置医师
	 * @param value String
	 */
	public void setDrCode(String value) {
		this.drCode = value;
	}

	/**
	 * 得到医师
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	
	/**
	 * 设置开立日期
	 * @param orderDate Timestamp
	 */
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate.setValue(orderDate);
	}

	/**
	 * 得到开立时间
	 * @return Timestamp
	 */
	public Timestamp getOrderDate() {
		return orderDate.getValue();
	}

	/**
	 * 设置主诉
	 * @param value String
	 */
	public void setSubjText(String value) {
		subjText.setValue(value);
	}

	/**
	 * 得到主诉
	 * @return String
	 */
	public String getSubjText() {
		return subjText.getValue();
	}

	/**
	 * 修改主诉
	 * @param value String
	 */
	public void modifySubjText(String value) {
		subjText.modifyValue(value);
	}

	/**
	 * 设置现病史
	 * @param value String
	 */
	public void setObjText(String value) {
		objText.setValue(value);
	}

	/**
	 * 得到现病史
	 * @return String
	 */
	public String getObjText() {
		return objText.getValue();
	}

	/**
	 * 修改
	 * @param value String
	 */
	public void modifyObjText(String value) {
		objText.modifyValue(value);
	}

	/**
	 * 设置体征
	 * @param value String
	 */
	public void setPhysexamRec(String value) {
		physexamRec.setValue(value);
	}

	/**
	 * 得到体征
	 * @return String
	 */
	public String getPhysexamRec() {
		return physexamRec.getValue();
	}

	/**
	 * 修改体征
	 * @param value String
	 */
	public void modifyPhysexamRec(String value) {
		physexamRec.modifyValue(value);
	}

	/**
	 * 设置既往史
	 * @param medhistoryList MedhistoryList
	 */
	public void setMedhistoryList(MedhistoryList medhistoryList) {
		this.medhistoryList = medhistoryList;
	}

	/**
	 * 得到既往史
	 * @return MedhistoryList
	 */
	public MedhistoryList getMedhistoryList() {
		return medhistoryList;
	}

	/**
	 * 设置诊断
	 * @param diagRecList DiagRecList
	 */
	public void setDiagRecList(DiagRecList diagRecList) {
		this.diagRecList = diagRecList;
	}

	/**
	 * 得到诊断
	 * @return DiagRecList
	 */
	public DiagRecList getDiagRecList() {
		return diagRecList;
	}

	/**
	 * 设置过敏史
	 * @param drugAllergyList DrugAllergyList
	 */
	public void setDrugAllergyList(DrugAllergyList drugAllergyList) {
		this.drugAllergyList = drugAllergyList;
	}

	/**
	 * 得到过敏史
	 * @return DrugAllergyList
	 */
	public DrugAllergyList getDrugAllergyList() {
		return drugAllergyList;
	}

	/**
	 * 设置处方签
	 * @param prescriptionList PrescriptionList
	 */
	public void setPrescriptionList(PrescriptionList prescriptionList) {
		this.prescriptionList = prescriptionList;
	}

	/**
	 * 得到处方签
	 * @return PrescriptionList
	 */
	public PrescriptionList getPrescriptionList() {
		return prescriptionList;
	}

	/**
	 * 得到医嘱历史
	 * @return orderhistoryList
	 */
	public OrderHistoryList getOrderhistoryList() {
		return orderhistoryList;
	}

	/**
	 * 设置医嘱历史
	 * @param orderhistoryList OrderHistoryList
	 */
	public void setOrderhistoryList(OrderHistoryList orderhistoryList) {
		this.orderhistoryList = orderhistoryList;
	}

	/**
	 * 设置新增标记
	 * @param newFlg boolean
	 */
	public void setNewFlg(boolean newFlg) {
		this.newFlg = newFlg;
	}

	/**
	 * 是否是新增状态
	 * @return boolean true 是第一次保存 false 不是第一次保存
	 */
	public boolean isNewFlg() {
		return newFlg;
	}

	/**
	 * 设置暂存标记
	 * @param tempSaveFlg boolean
	 */
	public void setTempSaveFlg(boolean tempSaveFlg) {
		this.tempSaveFlg = tempSaveFlg;
	}

	/**
	 * 是否是暂存
	 * @return boolean true 暂存 false 保存
	 */
	public boolean isTempSaveFlg() {
		return tempSaveFlg;
	}

	/**
	 * 初始化参数
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		//初始化主诉客诉
		if (!initSubjrecParm(parm.getParm("SUBJREC")))
			return false;
		//初始化处方
		if (!initPrescription(parm.getParm("ORDER")))
			return false;
		//初始化诊断
		if (!initDiagRec(parm.getParm("DIAGREC")))
			return false;
		//初始化过敏史
		if (!initDrugAllergy(parm.getParm("DRUGALLERGY")))
			return false;
		//初始化既往史
		if (!initMedHistory(parm.getParm("MEDHISTORY")))
			return false;
		//初始化处方历史
		if (!initOrderHistoryList(parm.getParm("ORDERHISTORY")))
			return false;
		//设置看诊注记
		if(reg!=null){
			reg.modifySeeDrFlg("Y");
		}
		
		return true;
	}

	/**
	 * 初始化处方
	 * @param parm
	 * @return boolean 真：成功，假：失败 
	 */
	public boolean initPrescription(TParm parm) {
		if (parm == null)
			return false;
		return getPrescriptionList().initParm(parm);
	}

	/**
	 * 初始化主诉客诉
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initSubjrecParm(TParm parm) {
		if (parm == null)
			return false;
		//问诊号
		setCaseNo(parm.getValue("CASE_NO", 0));
		//门急住别
		setAdmType(parm.getValue("ADM_TYPE", 0));
		//医师
		setDrCode(parm.getValue("DR_CODE", 0));
		//得到主诉
		setSubjText(parm.getValue("SUBJ_TEXT", 0));
		//现病史
		setObjText(parm.getValue("OBJ_TEXT", 0));
		//体征
		setPhysexamRec(parm.getValue("PHYSEXAM_REC", 0));
		//开立日期
		setOrderDate(parm.getTimestamp("ORDER_DATE", 0));
		return true;
	}

	/**
	 * 初始化过敏史
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initDrugAllergy(TParm parm) {
		if (parm == null)
			return false;
		return this.getDrugAllergyList().initParm(parm);
	}

	/**
	 * 初始化既往史
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initMedHistory(TParm parm) {
		if (parm == null)
			return false;
		//System.out.println("inmethodd"+parm);
		return this.getMedhistoryList().initParm(parm);
	}

	/**
	 * 初始化诊断
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initDiagRec(TParm parm) {
		if (parm == null)
			return false;
		return this.getDiagRecList().initParm(parm);
	}

	/**
	 * 初始化医嘱历史
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initOrderHistoryList(TParm parm) {
		if (parm == null)
			return false;
		return getOrderhistoryList().initParm(parm);
	}

	/**
	 * 检测数据
	 * @return TParm
	 */
	public TParm checkData() {
		TParm result = new TParm();
		return result;
	}

	/**
	 * 得到全部数据
	 * @return TParm
	 */
	public TParm getParm() {
		TParm result = new TParm();
		//加载主诉客诉数据
		result.setData("SUBJREC", getSubjrecParm().getData());
		//加载医嘱数据
		result.setData("ORDER", this.getPrescriptionList().getParm().getData());
		//加载过敏史数据
		result.setData("DRUGALLERGY", this.getDrugAllergyList().getParm().getData());
		//加载既往史数据
		result.setData("MEDHISTORY", this.getMedhistoryList().getParm()
				.getData());
		//加载诊断数据
		result.setData("DIAGREC", this.getDiagRecList().getParm().getData());
		//加载医嘱历史数据
		result.setData("ORDERHISTORY", this.getOrderhistoryList().getParm()
				.getData());
		//加载挂号数据
		result.setData("REG",this.getReg().getParm().getData());
		//加载病患数据
		result.setData("PAT",this.getPat().getParm().getData());
		
		return result;
	}

	/**
	 * 判断主诉客诉是否修改
	 * @return boolean true 修改 false 没有修改
	 */
	public boolean isModifiedSubjrec() {
		if (subjText.isModified())
			return true;
		if (objText.isModified())
			return true;
		if (physexamRec.isModified())
			return true;
		return false;
	}

	/**
	 * 得到主诉客诉Parm
	 * @return TParm
	 */
	public TParm getSubjrecParm() {
		TParm parm = new TParm();
		if (!isNewFlg() && !isModifiedSubjrec()) {
			parm.setData("SAVE_TYPE", "NO");
			return parm;
		}
		//设置保存类型 第一次保存使用INSERT 再次保存UPDATE
		parm.setData("SAVE_TYPE", isNewFlg() ? "INSERT" : "UPDATE");
		//问诊号
		parm.setData("CASE_NO", getCaseNo());
		if (isNewFlg()) {
			//门急住别
			parm.setData("ADM_TYPE", getAdmType());
			//医师
			parm.setData("DR_CODE", getDrCode());
		}
		//得到主诉
		parm.setData("SUBJ_TEXT", getSubjText());
		//现病史
		parm.setData("OBJ_TEXT", getObjText());
		//体征
		parm.setData("PHYSEXAM_REC", getPhysexamRec());
		//操作人员编号
		parm.setData("OPT_USER", Operator.getID());
		//操作人员IP
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

	/**
	 * 保存
	 * @return TParm
	 */
	public TParm onSave() {
		TParm parm = getParm();
		// 保存诊断
		TParm diagrecparm = parm.getParm("DIAGREC");
		//System.out.println("odo in odoAction==="+diagrecparm);
		TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onSave", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		reset();
		return result;
	}
	public void reset()
	{
		prescriptionList.reset();
	}
	/**
	 * 查询
	 * @param caseNo String
	 */
	public boolean onQuery(String caseNo,String admType) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO",pat.getMrNo());
		parm.setData("ADM_TYPE",admType);
		TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!initParm(result)) {
			err("数据载入失败");
			return false;
		}
		setCaseNo(caseNo);
		this.setNewFlg(false);
		return true;

	}



	public static void main(String args[]) {
		JavaHisDebug.initClient();
		StringTool f;
		int days=StringTool.getDateDiffer(StringTool.getTimestamp("20090916","yyyyMMdd"), StringTool.getTimestamp("20090918","yyyyMMdd"));
		//System.out.println("days="+days);
				
	}
}
