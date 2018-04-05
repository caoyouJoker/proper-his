package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.BooleanValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.IntValue;
import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 患者对象
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2008.9.19
 * @version 1.0
 */
public class Pat extends TModifiedData {
	/**
	 * 病案号 MR_NO
	 */
	private String mrNo = "";
	/**
	 * 住院号 IPD_NO
	 */
	private StringValue ipdNo = new StringValue(this);
	/**
	 * 合并病案号标记 MERGE_FLG
	 */
	private BooleanValue mergeFlg = new BooleanValue(this);
	/**
	 * 合并病案号 MERGE_TOMRNO
	 */
	private StringValue mergeToMrNo = new StringValue(this);
	/**
	 * 母亲的病案号 MOTHER_MRNO
	 */
	private StringValue motherMrNo = new StringValue(this);
	/**
	 * 姓名 PAT_NAME
	 */
	private StringValue name = new StringValue(this);
	/**
	 * 姓名2 PAT_NAME1
	 */
	private StringValue name1 = new StringValue(this);
	/**
	 * 拼音码 PY1
	 */
	private StringValue py1 = new StringValue(this);
	/**
	 * 注记码 PY2
	 */
	private StringValue py2 = new StringValue(this);
	/**
	 * 外国人标记 FOREIGNER_FLG
	 */
	private BooleanValue foreignerFlg = new BooleanValue(this);
	/**
	 * 电话 TEL_COMPANY
	 */
	private StringValue telCompany = new StringValue(this);
	/**
	 * 电话 TEL_HOME
	 */
	private StringValue telHome = new StringValue(this);
	/**
	 * 出生日期 BIRTH_DATE
	 */
	private TimestampValue birthday = new TimestampValue(this);
	/**
	 * 性别代码 SEX_CODE
	 */
	private StringValue sexCode = new StringValue(this);
	/**
	 * 身份证号 IDNO
	 */
	private StringValue idNo = new StringValue(this);
	/**
	 * 设置主身份 CTZ1_CODE
	 */
	private StringValue ctz1Code = new StringValue(this);
	/**
	 * 二级身份 CTZ2_CODE
	 */
	private StringValue ctz2Code = new StringValue(this);
	/**
	 * 三级身份 CTZ3_CODE
	 */
	private StringValue ctz3Code = new StringValue(this);
	/**
	 * 手机 CELL_PHONE
	 */
	private StringValue cellPhone = new StringValue(this);
	/**
	 * 工作单位 COMPANY_DESC
	 */
	private StringValue companyDesc = new StringValue(this);
	/**
	 * 电子邮件 E_MAIL
	 */
	private StringValue email = new StringValue(this);
	/**
	 * 血型 BLOOD_TYPE
	 */
	private StringValue bloodType = new StringValue(this);
	/**
	 * RH血型 BLOOD_RH_TYPE RH阳性: + RH阴性: -
	 */
	private StringValue bloodRHType = new StringValue(this);
	/**
	 * 死亡日期 DEAD_DATE
	 */
	private TimestampValue deadDate = new TimestampValue(this);
	/**
	 * 通信地址邮递区号 POST_CODE
	 */
	private StringValue postCode = new StringValue(this);
	/**
	 * 通信地址_路 ADDRESS
	 */
	private StringValue address = new StringValue(this);
	/**
	 * 户籍邮递区号 RESID_POST_CODE
	 */
	private StringValue residPostCode = new StringValue(this);
	/**
	 * 户籍地址 RESID_ADDRESS
	 */
	private StringValue residAddress = new StringValue(this);
	/**
	 * 紧急联系人 CONTACTS_NAME
	 */
	private StringValue contactsName = new StringValue(this);
	/**
	 * 紧急联系人关系 RELATION_CODE
	 */
	private StringValue relationCode = new StringValue(this);
	/**
	 * 紧急联系人电话 CONTACTS_TEL
	 */
	private StringValue contactsTel = new StringValue(this);
	/**
	 * 紧急联系人地址 CONTACTS_ADDRESS
	 */
	private StringValue contactsAddress = new StringValue(this);
	/**
	 * 婚姻状态(1-已婚,2-未婚,3-分居) MARRIAGE_CODE
	 */
	private StringValue marriageCode = new StringValue(this);
	/**
	 * 配偶身份证号 SPOUSE_IDNO
	 */
	private StringValue spouseIdno = new StringValue(this);
	/**
	 * 依附身份证号(父亲) FATHER_IDNO
	 */
	private StringValue fatherIdno = new StringValue(this);
	/**
	 * 依附身份证号(母亲) MOTHER_IDNO
	 */
	private StringValue motherIdno = new StringValue(this);
	/**
	 * 宗教 RELIGION_CODE
	 */
	private StringValue peligionCode = new StringValue(this);
	/**
	 * 教育程度代码 EDUCATION_CODE
	 */
	private StringValue educationCode = new StringValue(this);
	/**
	 * 职业类别代码 OCC_CODE
	 */
	private StringValue occCode = new StringValue(this);
	/**
	 * 国籍代码 NATION_CODE
	 */
	private StringValue nationCode = new StringValue(this);
	/**
	 * 种族 SPECIES_CODE
	 */
	private StringValue speciesCode = new StringValue(this);
	/**
	 * 初诊日期 FIRST_ADM_DATE
	 */
	private TimestampValue firstAdmDate = new TimestampValue(this);
	/**
	 * 最近门诊日期 RCNT_OPD_DATE
	 */
	private TimestampValue rcntOpdDate = new TimestampValue(this);
	/**
	 * 最近门诊科别 RCNT_OPD_DEPT
	 */
	private StringValue rcntOpdDept = new StringValue(this);
	/**
	 * 最近住院日期 RCNT_IPD_DATE
	 */
	private TimestampValue rcntIpdDate = new TimestampValue(this);
	/**
	 * 最近住院科别 RCNT_IPD_DEPT
	 */
	private StringValue rcntIpdDept = new StringValue(this);
	/**
	 * 最近急诊日期 RCNT_EMG_DATE
	 */
	private TimestampValue rcntEmgDate = new TimestampValue(this);
	/**
	 * 最近急诊科别 RCNT_EMG_DEPT
	 */
	private StringValue rcntEmgDept = new StringValue(this);
	/**
	 * 最近爽约日期 RCNT_MISS_DATE
	 */
	private TimestampValue rcntMissDate = new TimestampValue(this);
	/**
	 * 最近爽约科别 RCNT_MISS_DEPT
	 */
	private StringValue rcntMissDept = new StringValue(this);
	/**
	 * 最近幼儿健诊日期 KID_EXAM_RCNT_DATE
	 */
	private TimestampValue kidExamRcntDate = new TimestampValue(this);
	/**
	 * 最近幼儿注射日期 KID_INJ_RCNT_DATE
	 */
	private TimestampValue kidInjRcntDate = new TimestampValue(this);
	/**
	 * 最近成人健检日期 ADULT_EXAM_DATE
	 */
	private TimestampValue adultExamDate = new TimestampValue(this);
	/**
	 * 最近抹片检查日期 SMEAR_RCNT_DATE
	 */
	private TimestampValue smearRcntDate = new TimestampValue(this);
	/**
	 * 身高 HEIGHT
	 */
	private DoubleValue height = new DoubleValue(this);
	/**
	 * 体重 WEIGHT
	 */
	private DoubleValue weight = new DoubleValue(this);
	/**
	 * 备注 DESCRIPTION
	 */
	private StringValue description = new StringValue(this);
	/**
	 * 本院出生否 BORNIN_FLG
	 */
	private BooleanValue borninFlg = new BooleanValue(this);
	/**
	 * 新生儿胞胎注记 NEWBORN_SEQ
	 */
	private IntValue newbornSeq = new IntValue(this);
	/**
	 * 早产儿 PREMATURE_FLG
	 */
	private BooleanValue prematureFlg = new BooleanValue(this);
	
	
    private StringValue gestationalWeeks= new StringValue(this);//
    /**
     * 新生儿体重
     */
    private DoubleValue newBodyWeight = new DoubleValue(this);//
    /**
     * 新生儿身长
     */
    private DoubleValue newBodyHeight = new DoubleValue(this);//add by lili 20180403
	/**
	 * 残疾 HANDICAP_FLG
	 */
	private BooleanValue handicapFlg = new BooleanValue(this);
	/**
	 * 黑名单 BLACK_FLG
	 */
	private BooleanValue blackFlg = new BooleanValue(this);
	/**
	 * 隐名注记 NAME_INVISIBLE_FLG
	 */
	private BooleanValue nameInvisibleFlg = new BooleanValue(this);
	/**
	 * 法规隐密保护功能 LAW_PROTECT_FLG
	 */
	private BooleanValue lawProtectFlg = new BooleanValue(this);
	/**
	 * LMP日期
	 */
	private TimestampValue LMPDate = new TimestampValue(this);
	/**
	 * 预产期 PREGNANT_DATE
	 */
	private TimestampValue pregnantDate = new TimestampValue(this);
	/**
	 * 哺乳起始日期 BREASTFEED_STARTDATE
	 */
	private TimestampValue breastfeedStartDate = new TimestampValue(this);
	/**
	 * 哺乳终止日期 BREASTFEED_ENDDATE
	 */
	private TimestampValue breastfeedEndDate = new TimestampValue(this);
	/**
	 * 病生理状态一 PAT1_CODE
	 */
	private StringValue pat1Code = new StringValue(this);
	/**
	 * 病生理状态二 PAT2_CODE
	 */
	private StringValue pat2Code = new StringValue(this);
	/**
	 * 病生理状态三 PAT3_CODE
	 */
	private StringValue pat3Code = new StringValue(this);
	/**
	 * 出生地
	 */
	private StringValue homePlaceCode = new StringValue(this);
	/**
	 * 籍贯
	 */
	private StringValue birthPlace = new StringValue(this);
	/**
	 * 单位地址
	 */
	private StringValue companyAddress = new StringValue(this);
	/**
	 * 单位邮编
	 */
	private StringValue companyPost = new StringValue(this);
	/**
	 * 健康卡号
	 */
	private StringValue nhicardNo=new StringValue(this); 
	/**
     *
     */
	private StringValue nhiNo=new StringValue(this); 
	/**
	 * 查找身份证号或姓名相同的病患
	 */
	private static final String IS_SAME_PAT_SQL = "SELECT * FROM SYS_PATINFO WHERE IDNO='#' OR PAT_NAME='#'";

	/**
	 * 病患归属地
	 */
	private StringValue patBelong = new StringValue(this); 
	
	/**
	 * 病患密级SECURITY_CATEGORY
	 */
	private StringValue securityCategory = new StringValue(this); 
	
	/**
	 * 构造器
	 */
	public Pat() {
		StringBuffer sb = new StringBuffer();
		// 病案号
		sb.append("mrNo:MR_NO;");
		// 住院号
		sb.append("ipdNo:IPD_NO;");
		// 合并病案号标记
		sb.append("mergeFlg:MERGE_FLG;");
		// 合并病案号
		sb.append("mergeToMrNo:MERGE_TOMRNO;");
		// 母亲的病案号
		sb.append("motherMrNo:MOTHER_MRNO;");
		// 姓名
		sb.append("name:PAT_NAME;");
		// 姓名2
		sb.append("name1:PAT_NAME1;");
		// 拼音码
		sb.append("py1:PY1;");
		// 注记码
		sb.append("py2:PY2;");
		// 外国人标记
		sb.append("foreignerFlg:FOREIGNER_FLG;");
		// 身份证号
		sb.append("idNo:IDNO;");
		// 出生日期
		sb.append("birthday:BIRTH_DATE;");
		// 主身份
		sb.append("ctz1Code:CTZ1_CODE;");
		// 二级身份
		sb.append("ctz2Code:CTZ2_CODE;");
		// 三级身份
		sb.append("ctz3Code:CTZ3_CODE;");
		// 电话
		sb.append("telHome:TEL_HOME;");
		// 工作电话
		sb.append("telCompany:TEL_COMPANY;");
		// 性别代码
		sb.append("sexCode:SEX_CODE;");
		// 手机
		sb.append("cellPhone:CELL_PHONE;");
		// 工作单位
		sb.append("companyDesc:COMPANY_DESC;");
		// 电子邮件
		sb.append("email:E_MAIL;");
		// 血型
		sb.append("bloodType:BLOOD_TYPE;");
		// RH血型
		sb.append("bloodRHType:BLOOD_RH_TYPE;");
		// 死亡日期
		sb.append("deadDate:DEAD_DATE;");
		// 通信地址邮递区号
		sb.append("postCode:POST_CODE;");
		// 通信地址_路
		sb.append("address:ADDRESS;");
		// 户籍邮递区号
		sb.append("residPostCode:RESID_POST_CODE;");
		// 户籍地址
		sb.append("residAddress:RESID_ADDRESS;");
		// 紧急联系人
		sb.append("contactsName:CONTACTS_NAME;");
		// 紧急联系人关系
		sb.append("relationCode:RELATION_CODE;");
		// 紧急联系人电话
		sb.append("contactsTel:CONTACTS_TEL;");
		// 紧急联系人地址
		sb.append("contactsAddress:CONTACTS_ADDRESS;");
		// 婚姻状态(1-已婚,2-未婚,3-分居)
		sb.append("marriageCode:MARRIAGE_CODE;");
		// 配偶身份证号
		sb.append("spouseIdno:SPOUSE_IDNO;");
		// 依附身份证号(父亲)
		sb.append("fatherIdno:FATHER_IDNO;");
		// 依附身份证号(母亲)
		sb.append("motherIdno:MOTHER_IDNO;");
		// 宗教
		sb.append("peligionCode:RELIGION_CODE;");
		// 教育程度代码
		sb.append("educationCode:EDUCATION_CODE;");
		// 职业类别代码
		sb.append("occCode:OCC_CODE;");
		// 国籍代码
		sb.append("nationCode:NATION_CODE;");
		// 种族
		sb.append("speciesCode:SPECIES_CODE;");
		// 初诊日期
		sb.append("firstAdmDate:FIRST_ADM_DATE;");
		// 最近门诊日期
		sb.append("rcntOpdDate:RCNT_OPD_DATE;");
		// 最近门诊科别
		sb.append("rcntOpdDept:RCNT_OPD_DEPT;");
		// 最近住院日期
		sb.append("rcntIpdDate:RCNT_IPD_DATE;");
		// 最近住院科别
		sb.append("rcntIpdDept:RCNT_IPD_DEPT;");
		// 最近急诊日期
		sb.append("rcntEmgDate:RCNT_EMG_DATE;");
		// 最近急诊科别
		sb.append("rcntEmgDept:RCNT_EMG_DEPT;");
		// 最近爽约日期
		sb.append("rcntMissDate:RCNT_MISS_DATE;");
		// 最近爽约科别
		sb.append("rcntMissDept:RCNT_MISS_DEPT;");
		// 最近幼儿健诊日期
		sb.append("kidExamRcntDate:KID_EXAM_RCNT_DATE;");
		// 最近幼儿注射日期
		sb.append("kidInjRcntDate:KID_INJ_RCNT_DATE;");
		// 最近成人健检日期
		sb.append("adultExamDate:ADULT_EXAM_DATE;");
		// 最近抹片检查日期
		sb.append("smearRcntDate:SMEAR_RCNT_DATE;");
        
		sb.append("newBodyWeight:NEW_BODY_WEIGHT;");//
        // 新生儿身长
        sb.append("newBodyHeight:NEW_BODY_HEIGHT;");//add by lili 20180403
		// 身高
		sb.append("height:HEIGHT;");
		// 体重
		sb.append("weight:WEIGHT;");
		// 备注
		sb.append("description:DESCRIPTION;");
		// 本院出生否
		sb.append("borninFlg:BORNIN_FLG;");
		// 新生儿胞胎注记
		sb.append("newbornSeq:NEWBORN_SEQ;");
		// 早产儿
		sb.append("prematureFlg:PREMATURE_FLG;");
		// 残疾
		sb.append("handicapFlg:HANDICAP_FLG;");
		// 黑名单
		sb.append("blackFlg:BLACK_FLG;");
		// 隐名注记
		sb.append("nameInvisibleFlg:NAME_INVISIBLE_FLG;");
		// 法规隐密保护功能
		sb.append("lawProtectFlg:LAW_PROTECT_FLG;");
		// LMP日期
		sb.append("LMPDate:LMP_DATE;");
		// 预产期
		sb.append("pregnantDate:PREGNANT_DATE;");
		// 哺乳起始日期
		sb.append("breastfeedStartDate:BREASTFEED_STARTDATE;");
		// 哺乳终止日期
		sb.append("breastfeedEndDate:BREASTFEED_ENDDATE;");
		// 病生理状态一
		sb.append("pat1Code:PAT1_CODE;");
		// 病生理状态二
		sb.append("pat2Code:PAT2_CODE;");
		// 病生理状态三
		sb.append("pat3Code:PAT3_CODE;");
		// 出生地
		sb.append("homePlaceCode:HOMEPLACE_CODE;");
		// 籍贯
		sb.append("birthPlace:BIRTHPLACE;");
		// 单位地址
		sb.append("companyAddress:ADDRESS_COMPANY;");
		// 单位邮编
		sb.append("companyPost:POST_COMPANY;");
		//健康卡号
		sb.append("nhicardNo:NHICARD_NO;");
		//医保卡号
		sb.append("nhiNo:NHI_NO;");
		//病患归属地
		sb.append("patBelong:PAT_BELONG;");
		//病患密级
		sb.append("securityCategory:SECURITY_CATEGORY");
		setMapString(sb.toString());
	}

	/**
	 * 查询病患对象(根据病案号)
	 * 
	 * @param mrno
	 *            String 病案号
	 * @return Pat 病患对象
	 */
	public static Pat onQueryByMrNo(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return null;
		TParm parm = PatTool.getInstance().getInfoForMrno(mrno);
//		System.out.println("----------------"+parm);
		if (parm.getErrCode() < 0)
			return null;
		if (parm.getCount() <= 0)
			return null;
		Pat pat = new Pat();
		pat.initParm(parm, 0);
		pat.setNhiNo(null == parm.getValue("NHI_NO", 0) ? "" : parm.getValue(
				"NHI_NO", 0));// =======pangben modify 20110809
	    pat.setNewBodyWeight(parm.getData("NEW_BODY_WEIGHT", 0)==null? 0.0: parm.getDouble("NEW_BODY_WEIGHT", 0));
	    pat.setNewBodyHeight(parm.getData("NEW_BODY_HEIGHT", 0)==null? 0.0: parm.getDouble("NEW_BODY_HEIGHT", 0));//add by lili 20180403
		System.out.println("----------------"+pat);
		return pat;
	}

	/**
	 * 新增动作
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean onNew() {
		return PatTool.getInstance().newPat(this);
	}

	/**
	 * 保存动作
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		if (!isModified())
			return true;
		if (!PatTool.getInstance().onSave(this))
			return false;
		reset();
		return true;
	}

	/**
	 * 删除病患信息
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean onDelete() {
		return PatTool.getInstance().onDelete(this);
	}

	/**
	 * 删除病患信息(物理删除)
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean onDelete$() {
		return PatTool.getInstance().onDelete$(this);
	}

	/**
	 * 合并病案号
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean onMergeToMrNo(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		if (!PatTool.getInstance().existsPat(mrNo))
			return false;
		this.modifyMergeFlg(true);
		this.modifyMergeToMrNo(mrNo);
		if (!onSave())
			return false;
		return true;
	}

	/**
	 * 设置病案号
	 * 
	 * @param mrNo
	 *            String 病案号
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	// ===================pangben modify 20110808 start
	/**
	 * 得到病案号
	 * 
	 * @return String 病案号
	 */
	public String getMrNo() {
		return mrNo;
	}

	/**
	 * 设置市民卡号
	 * 
	 * @param citNo
	 *            String 市民卡号
	 */
	public void setNhiNo(String nhiNo) {
		this.nhiNo.setValue(nhiNo);
	}

	// ===================pangben modify 20110808 stop
	/**
	 * 得到市民卡号
	 * 
	 * @return String 市民卡号
	 */
	public String getNhiNo() {
		return nhiNo.getValue();
	}
	/**
	 * 修改市民卡号
	 * 
	 * @param ipdNo
	 *            String
	 */
	public void modifyNhiNo(String nhiNo) {
		this.nhiNo.modifyValue(nhiNo);
	}


	/**
	 * 设置住院号
	 * 
	 * @param ipdNo
	 *            String
	 */
	public void setIpdNo(String ipdNo) {
		this.ipdNo.setValue(ipdNo);
	}

	/**
	 * 得到住院号
	 * 
	 * @return String
	 */
	public String getIpdNo() {
		return ipdNo.getValue();
	}

	/**
	 * 修改住院号
	 * 
	 * @param ipdNo
	 *            String
	 */
	public void modifyIpdNo(String ipdNo) {
		this.ipdNo.modifyValue(ipdNo);
	}

	/**
	 * 设置合并病案号标记
	 * 
	 * @param mergeFlg
	 *            boolean
	 */
	public void setMergeFlg(boolean mergeFlg) {
		this.mergeFlg.setValue(mergeFlg);
	}

	/**
	 * 是否合并病案号标记
	 * 
	 * @return boolean
	 */
	public boolean isMergeFlg() {
		return mergeFlg.getValue();
	}

	/**
	 * 修改合并病案号标记
	 * 
	 * @param mergeFlg
	 *            boolean
	 */
	public void modifyMergeFlg(boolean mergeFlg) {
		this.mergeFlg.modifyValue(mergeFlg);
	}

	/**
	 * 设置合并病案号
	 * 
	 * @param mergeToMrNo
	 *            String
	 */
	public void setMergeToMrNo(String mergeToMrNo) {
		this.mergeToMrNo.setValue(mergeToMrNo);
	}

	/**
	 * 得到合并病案号
	 * 
	 * @return String
	 */
	public String getMergeToMrNo() {
		return mergeToMrNo.getValue();
	}

	/**
	 * 修改合并病案号
	 * 
	 * @param mergeToMrNo
	 *            String
	 */
	public void modifyMergeToMrNo(String mergeToMrNo) {
		this.mergeToMrNo.modifyValue(mergeToMrNo);
	}

	/**
	 * 设置母亲的病案号
	 * 
	 * @param motherMrNo
	 *            String
	 */
	public void setMotherMrNo(String motherMrNo) {
		this.motherMrNo.setValue(motherMrNo);
	}

	/**
	 * 得到母亲的病案号
	 * 
	 * @return String
	 */
	public String getMotherMrNo() {
		return motherMrNo.getValue();
	}

	/**
	 * 修改母亲的病案号
	 * 
	 * @param motherMrNo
	 *            String
	 */
	public void modifyMotherMrNo(String motherMrNo) {
		this.motherMrNo.modifyValue(motherMrNo);
	}

	/**
	 * 设置姓名
	 * 
	 * @param name
	 *            String 姓名
	 */
	public void setName(String name) {
		this.name.setValue(name);
	}

	/**
	 * 得到姓名
	 * 
	 * @return String 姓名
	 */
	public String getName() {
		return name.getValue();
	}

	/**
	 * 修改姓名
	 * 
	 * @param name
	 *            String
	 */
	public void modifyName(String name) {
		this.name.modifyValue(name);
	}

	/**
	 * 设置姓名1
	 * 
	 * @param name1
	 *            String 姓名1
	 */
	public void setName1(String name1) {
		this.name1.setValue(name1);
	}

	/**
	 * 得到姓名2
	 * 
	 * @return String 姓名2
	 */
	public String getName1() {
		return name1.getValue();
	}

	/**
	 * 修改姓名2
	 * 
	 * @param name
	 *            String
	 */
	public void modifyName1(String name) {
		this.name1.modifyValue(name);
	}

	/**
	 * 设置拼音码
	 * 
	 * @param py1
	 *            String
	 */
	public void setPy1(String py1) {
		this.py1.setValue(py1);
	}

	/**
	 * 得到拼音码
	 * 
	 * @return String
	 */
	public String getPy1() {
		return py1.getValue();
	}

	/**
	 * 修改拼音码
	 * 
	 * @param py1
	 *            String
	 */
	public void modifyPy1(String py1) {
		this.py1.modifyValue(py1);
	}

	/**
	 * 设置注记码
	 * 
	 * @param py2
	 *            String
	 */
	public void setPy2(String py2) {
		this.py2.setValue(py2);
	}

	/**
	 * 得到注记码
	 * 
	 * @return String
	 */
	public String getPy2() {
		return py2.getValue();
	}

	/**
	 * 修改注记码
	 * 
	 * @param py2
	 *            String
	 */
	public void modifyPy2(String py2) {
		this.py2.modifyValue(py2);
	}

	/**
	 * 设置外国人标记
	 * 
	 * @param foreignerFlg
	 *            boolean
	 */
	public void setForeignerFlg(boolean foreignerFlg) {
		this.foreignerFlg.setValue(foreignerFlg);
	}

	/**
	 * 是否是外国人
	 * 
	 * @return boolean
	 */
	public boolean isForeignerFlg() {
		return foreignerFlg.getValue();
	}

	/**
	 * 修改外国人标记
	 * 
	 * @param foreignerFlg
	 *            boolean
	 */
	public void modifyForeignerFlg(boolean foreignerFlg) {
		this.foreignerFlg.modifyValue(foreignerFlg);
	}

	/**
	 * 设置电话
	 * 
	 * @param telCompany
	 *            String电话
	 */
	public void setTelCompany(String tel) {
		this.telCompany.setValue(tel);
	}

	/**
	 * 得到电话
	 * 
	 * @return String 电话
	 */
	public String getTelCompany() {
		return telCompany.getValue();
	}

	/**
	 * 修改电话
	 * 
	 * @param telCompany
	 *            String
	 */
	public void modifyTelCompany(String tel) {
		this.telCompany.modifyValue(tel);
	}

	/**
	 * 设置家庭的电话
	 * 
	 * @param telHome
	 *            String
	 */
	public void setTelHome(String telHome) {
		this.telHome.setValue(telHome);
	}

	/**
	 * 得到家庭的电话
	 * 
	 * @return String
	 */
	public String getTelHome() {
		return telHome.getValue();
	}

	/**
	 * 修改家庭的电话
	 * 
	 * @param otherTel
	 *            String
	 */
	public void modifyTelHome(String telHome) {
		this.telHome.modifyValue(telHome);
	}

	/**
	 * 设置出生日期
	 * 
	 * @param birthday
	 *            Timestamp 出生日期
	 */
	public void setBirthday(Timestamp birthday) {
		this.birthday.setValue(birthday);
	}

	/**
	 * 得到出生日期
	 * 
	 * @return Timestamp 出生日期
	 */
	public Timestamp getBirthday() {
		return birthday.getValue();
	}

	/**
	 * 修改出生日期
	 * 
	 * @param birthday
	 *            Timestamp
	 */
	public void modifyBirthdy(Timestamp birthday) {
		this.birthday.modifyValue(birthday);
	}

	/**
	 * 得到出生日期(String 型)
	 * 
	 * @return String 出生日期
	 */
	public String getBirthdayString() {
		return StringTool.getString(getBirthday(), "yyyy年mm月dd日");
	}

	/**
	 * 设置性别代码
	 * 
	 * @param sexCode
	 *            String 性别代码
	 */
	public void setSexCode(String sexCode) {
		this.sexCode.setValue(sexCode);
	}

	/**
	 * 得到性别编码
	 * 
	 * @return String 性别编码
	 */
	public String getSexCode() {
		return sexCode.getValue();
	}

	/**
	 * 修改性别编码
	 * 
	 * @param sexCode
	 *            String
	 */
	public void modifySexCode(String sexCode) {
		this.sexCode.modifyValue(sexCode);
	}

	/**
	 * 得到性别文字
	 * 
	 * @return String 性别文字
	 */
	public String getSexString() {
		String code = getSexCode();
		if (code == null || code.length() == 0)
			return "";
		return DictionaryTool.getInstance().getSexName(code);
	}

	/**
	 * 设置身份证号
	 * 
	 * @param idNo
	 *            String 身份证号
	 */
	public void setIdNo(String idNo) {
		this.idNo.setValue(idNo);
	}

	/**
	 * 得到身份证号
	 * 
	 * @return String 身份证号
	 */
	public String getIdNo() {
		return idNo.getValue();
	}

	/**
	 * 修改身份证号
	 * 
	 * @param idNo
	 *            String
	 */
	public void modifyIdNo(String idNo) {
		this.idNo.modifyValue(idNo);
	}

	/**
	 * 设置主身份
	 * 
	 * @param ctz1Code
	 *            String 主身份
	 */
	public void setCtz1Code(String ctz1Code) {
		this.ctz1Code.setValue(ctz1Code);
	}

	/**
	 * 得到主身份
	 * 
	 * @return String 主身份
	 */
	public String getCtz1Code() {
		return ctz1Code.getValue();
	}

	/**
	 * 修改主身份
	 * 
	 * @param ctz1Code
	 *            String
	 */
	public void modifyCtz1Code(String ctz1Code) {
		this.ctz1Code.modifyValue(ctz1Code);
	}

	/**
	 * 设置二级身份
	 * 
	 * @param ctz2Code
	 *            String 二级身份
	 */
	public void setCtz2Code(String ctz2Code) {
		this.ctz2Code.setValue(ctz2Code);
	}

	/**
	 * 得到二级身份
	 * 
	 * @return String 二级身份
	 */
	public String getCtz2Code() {
		return ctz2Code.getValue();
	}

	/**
	 * 修改二级身份
	 * 
	 * @param ctz2Code
	 *            String
	 */
	public void modifyCtz2Code(String ctz2Code) {
		this.ctz2Code.modifyValue(ctz2Code);
	}

	/**
	 * 设置三级身份
	 * 
	 * @param ctz3Code
	 *            String 三级身份
	 */
	public void setCtz3Code(String ctz3Code) {
		this.ctz3Code.setValue(ctz3Code);
	}

	/**
	 * 得到三级身份
	 * 
	 * @return String 三级身份
	 */
	public String getCtz3Code() {
		return ctz3Code.getValue();
	}

	/**
	 * 修改三级身份
	 * 
	 * @param ctz3Code
	 *            String
	 */
	public void modifyCtz3Code(String ctz3Code) {
		this.ctz3Code.modifyValue(ctz3Code);
	}

	/**
	 * 设置手机
	 * 
	 * @param cellPhone
	 *            String 手机
	 */
	public void setCellPhone(String cellPhone) {
		this.cellPhone.setValue(cellPhone);
	}

	/**
	 * 得到手机
	 * 
	 * @return String 手机
	 */
	public String getCellPhone() {
		return cellPhone.getValue();
	}

	/**
	 * 修改手机
	 * 
	 * @param cellPhone
	 *            String
	 */
	public void modifyCellPhone(String cellPhone) {
		this.cellPhone.modifyValue(cellPhone);
	}

	/**
	 * 设置工作单位
	 * 
	 * @param companyDesc
	 *            String 工作单位
	 */
	public void setCompanyDesc(String companyDesc) {
		this.companyDesc.setValue(companyDesc);
	}

	/**
	 * 得到工作单位
	 * 
	 * @return String 工作单位
	 */
	public String getCompanyDesc() {
		return companyDesc.getValue();
	}

	/**
	 * 修改工作单位
	 * 
	 * @param companyDesc
	 *            String 工作单位
	 */
	public void modifyCompanyDesc(String companyDesc) {
		this.companyDesc.modifyValue(companyDesc);
	}

	/**
	 * 设置电子邮件
	 * 
	 * @param email
	 *            String 电子邮件
	 */
	public void setEmail(String email) {
		this.email.setValue(email);
	}

	/**
	 * 得到电子邮件
	 * 
	 * @return String 电子邮件
	 */
	public String getEmail() {
		return email.getValue();
	}

	/**
	 * 修改电子邮件
	 * 
	 * @param email
	 *            String 电子邮件
	 */
	public void modifyEmail(String email) {
		this.email.modifyValue(email);
	}

	/**
	 * 设置血型
	 * 
	 * @param bloodType
	 *            String 血型
	 */
	public void setBloodType(String bloodType) {
		this.bloodType.setValue(bloodType);
	}

	/**
	 * 得到血型
	 * 
	 * @return String 血型
	 */
	public String getBloodType() {
		return bloodType.getValue();
	}

	/**
	 * 修改血型
	 * 
	 * @param bloodType
	 *            String 血型
	 */
	public void modifyBloodType(String bloodType) {
		this.bloodType.modifyValue(bloodType);
	}

	/**
	 * 设置RH血型
	 * 
	 * @param bloodRHType
	 *            String RH血型
	 */
	public void setBloodRHType(String bloodRHType) {
		this.bloodRHType.setValue(bloodRHType);
	}

	/**
	 * 得到RH血型
	 * 
	 * @return String RH血型
	 */
	public String getBloodRHType() {
		return bloodRHType.getValue();
	}

	/**
	 * 修改RH血型
	 * 
	 * @param bloodRHType
	 *            String RH血型
	 */
	public void modifyBloodRHType(String bloodRHType) {
		this.bloodRHType.modifyValue(bloodRHType);
	}

	/**
	 * 设置死亡日期
	 * 
	 * @param deadDate
	 *            Timestamp 死亡日期
	 */
	public void setDeadDate(Timestamp deadDate) {
		this.deadDate.setValue(deadDate);
	}

	/**
	 * 得到死亡日期
	 * 
	 * @return Timestamp 死亡日期
	 */
	public Timestamp getDeadDate() {
		return deadDate.getValue();
	}

	/**
	 * 修改死亡日期
	 * 
	 * @param deadDate
	 *            Timestamp 死亡日期
	 */
	public void modifyDeadDate(Timestamp deadDate) {
		this.deadDate.modifyValue(deadDate);
	}

	/**
	 * 设置通信地址邮递区号
	 * 
	 * @param postCode
	 *            String 通信地址邮递区号
	 */
	public void setPostCode(String postCode) {
		this.postCode.setValue(postCode);
	}

	/**
	 * 得到通信地址邮递区号
	 * 
	 * @return String 通信地址邮递区号
	 */
	public String getPostCode() {
		return postCode.getValue();
	}

	/**
	 * 修改通信地址邮递区号
	 * 
	 * @param postCode
	 *            String 通信地址邮递区号
	 */
	public void modifyPostCode(String postCode) {
		this.postCode.modifyValue(postCode);
	}

	/**
	 * 设置通信地址_路
	 * 
	 * @param addressRoad
	 *            String 通信地址_路
	 */
	public void setAddress(String addressRoad) {
		this.address.setValue(addressRoad);
	}

	/**
	 * 得到通信地址_路
	 * 
	 * @return String 通信地址_路
	 */
	public String getAddress() {
		return address.getValue();
	}

	/**
	 * 修改通信地址_路
	 * 
	 * @param addressRoad
	 *            String 通信地址_路
	 */
	public void modifyAddress(String addressRoad) {
		this.address.modifyValue(addressRoad);
	}

	/**
	 * 设置户籍邮递区号
	 * 
	 * @param residPostCode
	 *            String 户籍邮递区号
	 */
	public void setResidPostCode(String residPostCode) {
		this.residPostCode.setValue(residPostCode);
	}

	/**
	 * 得到户籍邮递区号
	 * 
	 * @return String 户籍邮递区号
	 */
	public String getResidPostCode() {
		return residPostCode.getValue();
	}

	/**
	 * 修改户籍邮递区号
	 * 
	 * @param residPostCode
	 *            String 户籍邮递区号
	 */
	public void modifyResidPostCode(String residPostCode) {
		this.residPostCode.modifyValue(residPostCode);
	}

	/**
	 * 设置户籍地址
	 * 
	 * @param residRoad
	 *            String 户籍地址
	 */
	public void setResidAddress(String residAddress) {
		this.residAddress.setValue(residAddress);
	}

	/**
	 * 得到户籍地址
	 * 
	 * @return String 户籍地址
	 */
	public String getResidAddress() {
		return residAddress.getValue();
	}

	/**
	 * 修改户籍地址
	 * 
	 * @param residRoad
	 *            String 户籍地址
	 */
	public void modifyResidAddress(String residAddress) {
		this.residAddress.modifyValue(residAddress);
	}

	/**
	 * 设置紧急联系人
	 * 
	 * @param contactsName
	 *            String 紧急联系人
	 */
	public void setContactsName(String contactsName) {
		this.contactsName.setValue(contactsName);
	}

	/**
	 * 得到紧急联系人
	 * 
	 * @return String 紧急联系人
	 */
	public String getContactsName() {
		return contactsName.getValue();
	}

	/**
	 * 修改紧急联系人
	 * 
	 * @param contactsName
	 *            String 紧急联系人
	 */
	public void modifyContactsName(String contactsName) {
		this.contactsName.modifyValue(contactsName);
	}

	/**
	 * 设置紧急联系人关系
	 * 
	 * @param relationCode
	 *            String 紧急联系人关系
	 */
	public void setRelationCode(String relationCode) {
		this.relationCode.setValue(relationCode);
	}

	/**
	 * 得到紧急联系人关系
	 * 
	 * @return String 紧急联系人关系
	 */
	public String getRelationCode() {
		return relationCode.getValue();
	}

	/**
	 * 修改紧急联系人关系
	 * 
	 * @param relationCode
	 *            String 紧急联系人关系
	 */
	public void modifyRelationCode(String relationCode) {
		this.relationCode.modifyValue(relationCode);
	}

	/**
	 * 设置紧急联系人电话
	 * 
	 * @param contactsTel
	 *            String 紧急联系人电话
	 */
	public void setContactsTel(String contactsTel) {
		this.contactsTel.setValue(contactsTel);
	}

	/**
	 * 得到紧急联系人电话
	 * 
	 * @return String 紧急联系人电话
	 */
	public String getContactsTel() {
		return contactsTel.getValue();
	}

	/**
	 * 修改紧急联系人电话
	 * 
	 * @param contactsTel
	 *            String 紧急联系人电话
	 */
	public void modifyContactsTel(String contactsTel) {
		this.contactsTel.modifyValue(contactsTel);
	}

	/**
	 * 设置紧急联系人地址
	 * 
	 * @param contactsAddress
	 *            String 紧急联系人地址
	 */
	public void setContactsAddress(String contactsAddress) {
		this.contactsAddress.setValue(contactsAddress);
	}

	/**
	 * 得到紧急联系人地址
	 * 
	 * @return String 紧急联系人地址
	 */
	public String getContactsAddress() {
		return contactsAddress.getValue();
	}

	/**
	 * 修改紧急联系人地址
	 * 
	 * @param contactsAddress
	 *            String 紧急联系人地址
	 */
	public void modifyContactsAddress(String contactsAddress) {
		this.contactsAddress.modifyValue(contactsAddress);
	}

	/**
	 * 设置婚姻状态(1-已婚,2-未婚,3-分居)
	 * 
	 * @param marriageCode
	 *            String 婚姻状态(1-已婚,2-未婚,3-分居)
	 */
	public void setMarriageCode(String marriageCode) {
		this.marriageCode.setValue(marriageCode);
	}

	/**
	 * 得到婚姻状态(1-已婚,2-未婚,3-分居)
	 * 
	 * @return String 婚姻状态(1-已婚,2-未婚,3-分居)
	 */
	public String getMarriageCode() {
		return marriageCode.getValue();
	}

	/**
	 * 修改婚姻状态(1-已婚,2-未婚,3-分居)
	 * 
	 * @param marriageCode
	 *            String 婚姻状态(1-已婚,2-未婚,3-分居)
	 */
	public void modifyMarriageCode(String marriageCode) {
		this.marriageCode.modifyValue(marriageCode);
	}

	/**
	 * 设置配偶身份证号
	 * 
	 * @param spouseIdno
	 *            String 配偶身份证号
	 */
	public void setSpouseIdno(String spouseIdno) {
		this.spouseIdno.setValue(spouseIdno);
	}

	/**
	 * 得到配偶身份证号
	 * 
	 * @return String 配偶身份证号
	 */
	public String getSpouseIdno() {
		return spouseIdno.getValue();
	}

	/**
	 * 修改配偶身份证号
	 * 
	 * @param spouseIdno
	 *            String 配偶身份证号
	 */
	public void modifySpouseIdno(String spouseIdno) {
		this.spouseIdno.modifyValue(spouseIdno);
	}

	/**
	 * 设置依附身份证号(父亲)
	 * 
	 * @param fatherIdno
	 *            String 依附身份证号(父亲)
	 */
	public void setFatherIdno(String fatherIdno) {
		this.fatherIdno.setValue(fatherIdno);
	}

	/**
	 * 得到依附身份证号(父亲)
	 * 
	 * @return String 依附身份证号(父亲)
	 */
	public String getFatherIdno() {
		return fatherIdno.getValue();
	}

	/**
	 * 修改依附身份证号(父亲)
	 * 
	 * @param fatherIdno
	 *            String 依附身份证号(父亲)
	 */
	public void modifyFatherIdno(String fatherIdno) {
		this.fatherIdno.modifyValue(fatherIdno);
	}

	/**
	 * 设置依附身份证号(母亲)
	 * 
	 * @param motherIdno
	 *            String 依附身份证号(母亲)
	 */
	public void setMotherIdno(String motherIdno) {
		this.motherIdno.setValue(motherIdno);
	}

	/**
	 * 得到依附身份证号(母亲)
	 * 
	 * @return String 依附身份证号(母亲)
	 */
	public String getMotherIdno() {
		return motherIdno.getValue();
	}

	/**
	 * 修改依附身份证号(母亲)
	 * 
	 * @param motherIdno
	 *            String 依附身份证号(母亲)
	 */
	public void modifyMotherIdno(String motherIdno) {
		this.motherIdno.modifyValue(motherIdno);
	}

	/**
	 * 设置宗教
	 * 
	 * @param peligionCode
	 *            String 宗教
	 */
	public void setPeligionCode(String peligionCode) {
		this.peligionCode.setValue(peligionCode);
	}

	/**
	 * 得到宗教
	 * 
	 * @return String 宗教
	 */
	public String getPeligionCode() {
		return peligionCode.getValue();
	}

	/**
	 * 修改宗教
	 * 
	 * @param peligionCode
	 *            String 宗教
	 */
	public void modifyPeligionCode(String peligionCode) {
		this.peligionCode.modifyValue(peligionCode);
	}

	/**
	 * 设置教育程度代码
	 * 
	 * @param educationCode
	 *            String 教育程度代码
	 */
	public void setEducationCode(String educationCode) {
		this.educationCode.setValue(educationCode);
	}

	/**
	 * 得到教育程度代码
	 * 
	 * @return String 教育程度代码
	 */
	public String getEducationCode() {
		return educationCode.getValue();
	}

	/**
	 * 修改教育程度代码
	 * 
	 * @param educationCode
	 *            String 教育程度代码
	 */
	public void modifyEducationCode(String educationCode) {
		this.educationCode.modifyValue(educationCode);
	}

	/**
	 * 设置职业类别代码
	 * 
	 * @param occCode
	 *            String 职业类别代码
	 */
	public void setOccCode(String occCode) {
		this.occCode.setValue(occCode);
	}

	/**
	 * 得到职业类别代码
	 * 
	 * @return String 职业类别代码
	 */
	public String getOccCode() {
		return occCode.getValue();
	}

	/**
	 * 修改职业类别代码
	 * 
	 * @param occCode
	 *            String 职业类别代码
	 */
	public void modifyOccCode(String occCode) {
		this.occCode.modifyValue(occCode);
	}

	/**
	 * 设置国籍代码
	 * 
	 * @param nationCode
	 *            String 国籍代码
	 */
	public void setNationCode(String nationCode) {
		this.nationCode.setValue(nationCode);
	}

	/**
	 * 得到国籍代码
	 * 
	 * @return String 国籍代码
	 */
	public String getNationCode() {
		return nationCode.getValue();
	}

	/**
	 * 修改国籍代码
	 * 
	 * @param nationCode
	 *            String 国籍代码
	 */
	public void modifyNationCode(String nationCode) {
		this.nationCode.modifyValue(nationCode);
	}

	/**
	 * 设置种族
	 * 
	 * @param speciesCode
	 *            String 种族
	 */
	public void setSpeciesCode(String speciesCode) {
		this.speciesCode.setValue(speciesCode);
	}

	/**
	 * 得到种族
	 * 
	 * @return String 种族
	 */
	public String getSpeciesCode() {
		return speciesCode.getValue();
	}

	/**
	 * 修改种族
	 * 
	 * @param speciesCode
	 *            String 种族
	 */
	public void modifySpeciesCode(String speciesCode) {
		this.speciesCode.modifyValue(speciesCode);
	}

	/**
	 * 设置初诊日期
	 * 
	 * @param firstAdmDate
	 *            Timestamp 初诊日期
	 */
	public void setFirstAdmDate(Timestamp firstAdmDate) {
		this.firstAdmDate.setValue(firstAdmDate);
	}

	/**
	 * 得到初诊日期
	 * 
	 * @return Timestamp 初诊日期
	 */
	public Timestamp getFirstAdmDate() {
		return firstAdmDate.getValue();
	}

	/**
	 * 修改初诊日期
	 * 
	 * @param firstAdmDate
	 *            Timestamp 初诊日期
	 */
	public void modifyFirstAdmDate(Timestamp firstAdmDate) {
		this.firstAdmDate.modifyValue(firstAdmDate);
	}

	/**
	 * 设置最近门诊日期
	 * 
	 * @param rcntOpdDate
	 *            Timestamp 最近门诊日期
	 */
	public void setRcntOpdDate(Timestamp rcntOpdDate) {
		this.rcntOpdDate.setValue(rcntOpdDate);
	}

	/**
	 * 得到最近门诊日期
	 * 
	 * @return Timestamp 最近门诊日期
	 */
	public Timestamp getRcntOpdDate() {
		return rcntOpdDate.getValue();
	}

	/**
	 * 修改最近门诊日期
	 * 
	 * @param rcntOpdDate
	 *            Timestamp 最近门诊日期
	 */
	public void modifyRcntOpdDate(Timestamp rcntOpdDate) {
		this.rcntOpdDate.modifyValue(rcntOpdDate);
	}

	/**
	 * 设置最近门诊科别
	 * 
	 * @param rcntOpdDept
	 *            String 最近门诊科别
	 */
	public void setRcntOpdDept(String rcntOpdDept) {
		this.rcntOpdDept.setValue(rcntOpdDept);
	}

	/**
	 * 得到最近门诊科别
	 * 
	 * @return String 最近门诊科别
	 */
	public String getRcntOpdDept() {
		return rcntOpdDept.getValue();
	}

	/**
	 * 修改最近门诊科别
	 * 
	 * @param rcntOpdDept
	 *            String 最近门诊科别
	 */
	public void modifyRcntOpdDept(String rcntOpdDept) {
		this.rcntOpdDept.modifyValue(rcntOpdDept);
	}

	/**
	 * 设置最近住院日期
	 * 
	 * @param rcntIpdDate
	 *            Timestamp 最近住院日期
	 */
	public void setRcntIpdDate(Timestamp rcntIpdDate) {
		this.rcntIpdDate.setValue(rcntIpdDate);
	}

	/**
	 * 得到最近住院日期
	 * 
	 * @return Timestamp 最近住院日期
	 */
	public Timestamp getRcntIpdDate() {
		return rcntIpdDate.getValue();
	}

	/**
	 * 修改最近住院日期
	 * 
	 * @param rcntIpdDate
	 *            Timestamp 最近住院日期
	 */
	public void modifyRcntIpdDate(Timestamp rcntIpdDate) {
		this.rcntIpdDate.modifyValue(rcntIpdDate);
	}

	/**
	 * 设置最近住院科别
	 * 
	 * @param rcntIpdDept
	 *            String 最近住院科别
	 */
	public void setRcntIpdDept(String rcntIpdDept) {
		this.rcntIpdDept.setValue(rcntIpdDept);
	}

	/**
	 * 得到最近住院科别
	 * 
	 * @return String 最近住院科别
	 */
	public String getRcntIpdDept() {
		return rcntIpdDept.getValue();
	}

	/**
	 * 修改最近住院科别
	 * 
	 * @param rcntIpdDept
	 *            String 最近住院科别
	 */
	public void modifyRcntIpdDept(String rcntIpdDept) {
		this.rcntIpdDept.modifyValue(rcntIpdDept);
	}

	/**
	 * 设置最近急诊日期
	 * 
	 * @param rcntEmgDate
	 *            Timestamp 最近急诊日期
	 */
	public void setRcntEmgDate(Timestamp rcntEmgDate) {
		this.rcntEmgDate.setValue(rcntEmgDate);
	}

	/**
	 * 得到最近急诊日期
	 * 
	 * @return Timestamp 最近急诊日期
	 */
	public Timestamp getRcntEmgDate() {
		return rcntEmgDate.getValue();
	}

	/**
	 * 修改最近急诊日期
	 * 
	 * @param rcntEmgDate
	 *            Timestamp 最近急诊日期
	 */
	public void modifyRcntEmgDate(Timestamp rcntEmgDate) {
		this.rcntEmgDate.modifyValue(rcntEmgDate);
	}

	/**
	 * 设置最近急诊科别
	 * 
	 * @param rcntEmgDept
	 *            String 最近急诊科别
	 */
	public void setRcntEmgDept(String rcntEmgDept) {
		this.rcntEmgDept.setValue(rcntEmgDept);
	}

	/**
	 * 得到最近急诊科别
	 * 
	 * @return String 最近急诊科别
	 */
	public String getRcntEmgDept() {
		return rcntEmgDept.getValue();
	}

	/**
	 * 修改最近急诊科别
	 * 
	 * @param rcntEmgDept
	 *            String 最近急诊科别
	 */
	public void modifyRcntEmgDept(String rcntEmgDept) {
		this.rcntEmgDept.modifyValue(rcntEmgDept);
	}

	/**
	 * 设置最近爽约日期
	 * 
	 * @param rcntMissDate
	 *            Timestamp 最近爽约日期
	 */
	public void setRcntMissDate(Timestamp rcntMissDate) {
		this.rcntMissDate.setValue(rcntMissDate);
	}

	/**
	 * 得到最近爽约日期
	 * 
	 * @return Timestamp 最近爽约日期
	 */
	public Timestamp getRcntMissDate() {
		return rcntMissDate.getValue();
	}

	/**
	 * 修改最近爽约日期
	 * 
	 * @param rcntMissDate
	 *            Timestamp 最近爽约日期
	 */
	public void modifyRcntMissDate(Timestamp rcntMissDate) {
		this.rcntMissDate.modifyValue(rcntMissDate);
	}

	/**
	 * 设置最近爽约科别
	 * 
	 * @param rcntMissDept
	 *            String 最近爽约科别
	 */
	public void setRcntMissDept(String rcntMissDept) {
		this.rcntMissDept.setValue(rcntMissDept);
	}

	/**
	 * 得到最近爽约科别
	 * 
	 * @return String 最近爽约科别
	 */
	public String getRcntMissDept() {
		return rcntMissDept.getValue();
	}

	/**
	 * 修改最近爽约科别
	 * 
	 * @param rcntMissDept
	 *            String 最近爽约科别
	 */
	public void modifyRcntMissDept(String rcntMissDept) {
		this.rcntMissDept.modifyValue(rcntMissDept);
	}

	/**
	 * 设置最近幼儿健诊日期
	 * 
	 * @param kidExamRcntDate
	 *            Timestamp 最近幼儿健诊日期
	 */
	public void setKidExamRcntDate(Timestamp kidExamRcntDate) {
		this.kidExamRcntDate.setValue(kidExamRcntDate);
	}

	/**
	 * 得到最近幼儿健诊日期
	 * 
	 * @return Timestamp 最近幼儿健诊日期
	 */
	public Timestamp getKidExamRcntDate() {
		return kidExamRcntDate.getValue();
	}

	/**
	 * 修改最近幼儿健诊日期
	 * 
	 * @param kidExamRcntDate
	 *            Timestamp 最近幼儿健诊日期
	 */
	public void modifyKidExamRcntDate(Timestamp kidExamRcntDate) {
		this.kidExamRcntDate.modifyValue(kidExamRcntDate);
	}

	/**
	 * 设置最近幼儿注射日期
	 * 
	 * @param kidInjRcntDate
	 *            Timestamp 最近幼儿注射日期
	 */
	public void setKidInjRcntDate(Timestamp kidInjRcntDate) {
		this.kidInjRcntDate.setValue(kidInjRcntDate);
	}

	/**
	 * 得到最近幼儿注射日期
	 * 
	 * @return Timestamp 最近幼儿注射日期
	 */
	public Timestamp getKidInjRcntDate() {
		return kidInjRcntDate.getValue();
	}

	/**
	 * 修改最近幼儿注射日期
	 * 
	 * @param kidInjRcntDate
	 *            Timestamp 最近幼儿注射日期
	 */
	public void modifyKidInjRcntDate(Timestamp kidInjRcntDate) {
		this.kidInjRcntDate.modifyValue(kidInjRcntDate);
	}

	/**
	 * 设置最近成人健检日期
	 * 
	 * @param adultExamDate
	 *            Timestamp 最近成人健检日期
	 */
	public void setAdultExamDate(Timestamp adultExamDate) {
		this.adultExamDate.setValue(adultExamDate);
	}

	/**
	 * 得到最近成人健检日期
	 * 
	 * @return Timestamp 最近成人健检日期
	 */
	public Timestamp getAdultExamDate() {
		return adultExamDate.getValue();
	}

	/**
	 * 修改最近成人健检日期
	 * 
	 * @param adultExamDate
	 *            Timestamp 最近成人健检日期
	 */
	public void modifyAdultExamDate(Timestamp adultExamDate) {
		this.adultExamDate.modifyValue(adultExamDate);
	}

	/**
	 * 设置最近抹片检查日期
	 * 
	 * @param smearRcntDate
	 *            Timestamp 最近抹片检查日期
	 */
	public void setSmearRcntDate(Timestamp smearRcntDate) {
		this.smearRcntDate.setValue(smearRcntDate);
	}

	/**
	 * 得到最近抹片检查日期
	 * 
	 * @return Timestamp 最近抹片检查日期
	 */
	public Timestamp getSmearRcntDate() {
		return smearRcntDate.getValue();
	}

	/**
	 * 修改最近抹片检查日期
	 * 
	 * @param smearRcntDate
	 *            Timestamp 最近抹片检查日期
	 */
	public void modifySmearRcntDate(Timestamp smearRcntDate) {
		this.smearRcntDate.modifyValue(smearRcntDate);
	}

	/**
	 * 设置身高
	 * 
	 * @param height
	 *            double 身高
	 */
	public void setHeight(double height) {
		this.height.setValue(height);
	}

	/**
	 * 得到身高
	 * 
	 * @return double 身高
	 */
	public double getHeight() {
		return height.getValue();
	}

	/**
	 * 修改身高
	 * 
	 * @param height
	 *            double 身高
	 */
	public void modifyHeight(double height) {
		this.height.modifyValue(height);
	}

	/**
	 * 设置体重
	 * 
	 * @param weight
	 *            double 体重
	 */
	public void setWeight(double weight) {
		this.weight.setValue(weight);
	}

	/**
	 * 得到体重
	 * 
	 * @return double 体重
	 */
	public double getWeight() {
		return weight.getValue();
	}

	/**
	 * 修改体重
	 * 
	 * @param weight
	 *            double 体重
	 */
	public void modifyWeight(double weight) {
		this.weight.modifyValue(weight);
	}

	/**
	 * 设置备注
	 * 
	 * @param description
	 *            String 备注
	 */
	public void setDescription(String description) {
		this.description.setValue(description);
	}

	/**
	 * 得到备注
	 * 
	 * @return String 备注
	 */
	public String getDescription() {
		return description.getValue();
	}

	/**
	 * 修改备注
	 * 
	 * @param description
	 *            String 备注
	 */
	public void modifyDescription(String description) {
		this.description.modifyValue(description);
	}

	/**
	 * 设置本院出生否
	 * 
	 * @param borninFlg
	 *            boolean 本院出生否
	 */
	public void setBorninFlg(boolean borninFlg) {
		this.borninFlg.setValue(borninFlg);
	}

	/**
	 * 得到本院出生否
	 * 
	 * @return boolean 本院出生否
	 */
	public boolean getBorninFlg() {
		return borninFlg.getValue();
	}

	/**
	 * 修改本院出生否
	 * 
	 * @param borninFlg
	 *            boolean 本院出生否
	 */
	public void modifyBorninFlg(boolean borninFlg) {
		this.borninFlg.modifyValue(borninFlg);
	}

	/**
	 * 设置新生儿胞胎注记
	 * 
	 * @param newbornSeq
	 *            int 新生儿胞胎注记
	 */
	public void setNewbornSeq(int newbornSeq) {
		this.newbornSeq.setValue(newbornSeq);
	}

	/**
	 * 得到新生儿胞胎注记
	 * 
	 * @return int 新生儿胞胎注记
	 */
	public int getNewbornSeq() {
		return newbornSeq.getValue();
	}

	/**
	 * 修改新生儿胞胎注记
	 * 
	 * @param newbornSeq
	 *            int 新生儿胞胎注记
	 */
	public void modifyNewbornSeq(int newbornSeq) {
		this.newbornSeq.modifyValue(newbornSeq);
	}

	/**
	 * 设置早产儿
	 * 
	 * @param handicapFlg
	 *            boolean 早产儿
	 */
	public void setPrematureFlg(boolean prematureFlg) {
		this.prematureFlg.setValue(prematureFlg);
	}

	/**
	 * 得到早产儿
	 * 
	 * @return boolean 早产儿
	 */
	public boolean getPrematureFlg() {
		return prematureFlg.getValue();
	}

	/**
	 * 修改早产儿
	 * 
	 * @param prematureFlg
	 *            boolean 早产儿
	 */
	public void modifyPrematureFlg(boolean prematureFlg) {
		this.prematureFlg.modifyValue(prematureFlg);
	}

	/**
	 * 设置残疾
	 * 
	 * @param handicapFlg
	 *            boolean 残疾
	 */
	public void setHandicapFlg(boolean handicapFlg) {
		this.handicapFlg.setValue(handicapFlg);
	}

	/**
	 * 得到残疾
	 * 
	 * @return boolean 残疾
	 */
	public boolean getHandicapFlg() {
		return handicapFlg.getValue();
	}

	/**
	 * 修改残疾
	 * 
	 * @param handicapFlg
	 *            boolean 残疾
	 */
	public void modifyHandicapFlg(boolean handicapFlg) {
		this.handicapFlg.modifyValue(handicapFlg);
	}

	/**
	 * 设置黑名单
	 * 
	 * @param blackFlg
	 *            boolean 黑名单
	 */
	public void setBlackFlg(boolean blackFlg) {
		this.blackFlg.setValue(blackFlg);
	}

	/**
	 * 得到黑名单
	 * 
	 * @return boolean 黑名单
	 */
	public boolean getBlackFlg() {
		return blackFlg.getValue();
	}

	/**
	 * 修改黑名单
	 * 
	 * @param blackFlg
	 *            boolean 黑名单
	 */
	public void modifyBlackFlg(boolean blackFlg) {
		this.blackFlg.modifyValue(blackFlg);
	}

	/**
	 * 设置隐名注记
	 * 
	 * @param nameInvisibleFlg
	 *            boolean 隐名注记
	 */
	public void setNameInvisibleFlg(boolean nameInvisibleFlg) {
		this.nameInvisibleFlg.setValue(nameInvisibleFlg);
	}

	/**
	 * 得到隐名注记
	 * 
	 * @return boolean 隐名注记
	 */
	public boolean getNameInvisibleFlg() {
		return nameInvisibleFlg.getValue();
	}

	/**
	 * 修改隐名注记
	 * 
	 * @param nameInvisibleFlg
	 *            boolean 隐名注记
	 */
	public void modifyNameInvisibleFlg(boolean nameInvisibleFlg) {
		this.nameInvisibleFlg.modifyValue(nameInvisibleFlg);
	}

	/**
	 * 设置法规隐密保护功能
	 * 
	 * @param lawProtectFlg
	 *            boolean 法规隐密保护功能
	 */
	public void setLawProtectFlg(boolean lawProtectFlg) {
		this.lawProtectFlg.setValue(lawProtectFlg);
	}

	/**
	 * 得到法规隐密保护功能
	 * 
	 * @return boolean 法规隐密保护功能
	 */
	public boolean getLawProtectFlg() {
		return lawProtectFlg.getValue();
	}

	/**
	 * 修改法规隐密保护功能
	 * 
	 * @param lawProtectFlg
	 *            boolean 法规隐密保护功能
	 */
	public void modifyLawProtectFlg(boolean lawProtectFlg) {
		this.lawProtectFlg.modifyValue(lawProtectFlg);
	}

	/**
	 * 设置LMP日期
	 * 
	 * @param estideliveryDate
	 *            Timestamp LMP日期
	 */
	public void setLMPDatee(Timestamp LMPDate) {
		this.LMPDate.setValue(LMPDate);
	}

	/**
	 * 得到LMP日期
	 * 
	 * @return Timestamp LMP日期
	 */
	public Timestamp getLMPDate() {
		return LMPDate.getValue();
	}

	/**
	 * 修改LMP日期
	 * 
	 * @param estideliveryDate
	 *            Timestamp LMP日期
	 */
	public void modifyLMPDate(Timestamp LMPDate) {
		this.LMPDate.modifyValue(LMPDate);
	}

	/**
	 * 设置预产期
	 * 
	 * @param pregnantDate
	 *            Timestamp 预产期
	 */
	public void setPregnantDate(Timestamp pregnantDate) {
		this.pregnantDate.setValue(pregnantDate);
	}

	/**
	 * 得到预产期
	 * 
	 * @return Timestamp 预产期
	 */
	public Timestamp getPregnantDate() {
		return pregnantDate.getValue();
	}

	/**
	 * 修改预产期
	 * 
	 * @param pregnantDate
	 *            Timestamp 预产期
	 */
	public void modifyPregnantDate(Timestamp pregnantDate) {
		this.pregnantDate.modifyValue(pregnantDate);
	}

	/**
	 * 设置哺乳起始日期
	 * 
	 * @param breastfeedStartDate
	 *            Timestamp 哺乳起始日期
	 */
	public void setBreastfeedStartDate(Timestamp breastfeedStartDate) {
		this.breastfeedStartDate.setValue(breastfeedStartDate);
	}

	/**
	 * 得到哺乳起始日期
	 * 
	 * @return Timestamp 哺乳起始日期
	 */
	public Timestamp getBreastfeedStartDate() {
		return breastfeedStartDate.getValue();
	}

	/**
	 * 修改哺乳起始日期
	 * 
	 * @param breastfeedStartDate
	 *            Timestamp 哺乳起始日期
	 */
	public void modifyBreastfeedStartDate(Timestamp breastfeedStartDate) {
		this.breastfeedStartDate.modifyValue(breastfeedStartDate);
	}

	/**
	 * 设置哺乳终止日期
	 * 
	 * @param breastfeedEndDate
	 *            Timestamp 哺乳终止日期
	 */
	public void setBreastfeedEndDate(Timestamp breastfeedEndDate) {
		this.breastfeedEndDate.setValue(breastfeedEndDate);
	}

	/**
	 * 得到哺乳终止日期
	 * 
	 * @return Timestamp 哺乳终止日期
	 */
	public Timestamp getBreastfeedEndDate() {
		return breastfeedEndDate.getValue();
	}

	/**
	 * 修改哺乳终止日期
	 * 
	 * @param breastfeedEndDate
	 *            Timestamp 哺乳终止日期
	 */
	public void modifyBreastfeedEndDate(Timestamp breastfeedEndDate) {
		this.breastfeedEndDate.modifyValue(breastfeedEndDate);
	}

	/**
	 * 设置病生理状态一
	 * 
	 * @param pat1Code
	 *            String 病生理状态一
	 */
	public void setPat1Code(String pat1Code) {
		this.pat1Code.setValue(pat1Code);
	}

	/**
	 * 得到病生理状态一
	 * 
	 * @return String 病生理状态一
	 */
	public String getPat1Code() {
		return pat1Code.getValue();
	}

	/**
	 * 修改病生理状态一
	 * 
	 * @param pat1Code
	 *            String 病生理状态一
	 */
	public void modifyPat1Code(String pat1Code) {
		this.pat1Code.modifyValue(pat1Code);
	}

	/**
	 * 设置病生理状态二
	 * 
	 * @param pat2Code
	 *            String 病生理状态二
	 */
	public void setPat2Code(String pat2Code) {
		this.pat2Code.setValue(pat2Code);
	}

	/**
	 * 得到病生理状态二
	 * 
	 * @return String 病生理状态二
	 */
	public String getPat2Code() {
		return pat2Code.getValue();
	}

	/**
	 * 修改病生理状态二
	 * 
	 * @param pat2Code
	 *            String 病生理状态二
	 */
	public void modifyPat2Code(String pat2Code) {
		this.pat2Code.modifyValue(pat2Code);
	}

	/**
	 * 设置病生理状态三
	 * 
	 * @param pat3Code
	 *            String 病生理状态三
	 */
	public void setPat3Code(String pat3Code) {
		this.pat3Code.setValue(pat3Code);
	}

	/**
	 * 得到病生理状态三
	 * 
	 * @return String 病生理状态三
	 */
	public String getPat3Code() {
		return pat3Code.getValue();
	}

	/**
	 * 修改病生理状态三
	 * 
	 * @param pat3Code
	 *            String 病生理状态三
	 */
	public void modifyPat3Code(String pat3Code) {
		this.pat3Code.modifyValue(pat3Code);
	}

	/**
	 * 出生地
	 * 
	 * @param homePlaceCode
	 *            String 出生地
	 */
	public void sethomePlaceCode(String homePlaceCode) {
		this.homePlaceCode.setValue(homePlaceCode);
	}

	/**
	 * 出生地
	 * 
	 * @return String 出生地
	 */
	public String gethomePlaceCode() {
		return homePlaceCode.getValue();
	}

	/**
	 * 出生地
	 * 
	 * @param homePlaceCode
	 *            String 出生地
	 */
	public void modifyhomePlaceCode(String homePlaceCode) {
		this.homePlaceCode.modifyValue(homePlaceCode);
	}

	/**
	 * 籍贯
	 * 
	 * @return the brithPlace
	 */
	public String getBirthPlace() {
		return birthPlace.getValue();
	}

	/**
	 * 籍贯
	 * 
	 * @param brithPlace
	 *            the brithPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace.setValue(birthPlace);
	}

	/**
	 * 籍贯
	 * 
	 * @param brithPlace
	 *            String 籍贯
	 */
	public void modifyBirthPlace(String birthPlace) {
		this.birthPlace.modifyValue(birthPlace);
	}

	/**
	 * 单位地址
	 * 
	 * @return the companyAddress
	 */
	public String getCompanyAddress() {
		return companyAddress.getValue();
	}

	/**
	 * 单位地址
	 * 
	 * @param companyAddress
	 *            the companyAddress to set
	 */
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress.setValue(companyAddress);
	}

	/**
	 * 修改单位地址
	 * 
	 * @param companyAddress
	 */
	public void modifyCompanyAddress(String companyAddress) {
		this.companyAddress.modifyValue(companyAddress);
	}

	/**
	 * 单位邮编
	 * 
	 * @return the companyPost
	 */
	public String getCompanyPost() {
		return companyPost.getValue();
	}

	/**
	 * 单位邮编
	 * 
	 * @param companyPost
	 *            the companyPost to set
	 */
	public void setCompanyPost(String companyPost) {
		this.companyPost.setValue(companyPost);
	}

	/**
	 * 修改单位邮编
	 */
	public void modifyCompanyPost(String companyPost) {
		this.companyPost.modifyValue(companyPost);
	}
	/**
	 * 得到健康卡号
	 * @return the nhicardNo
	 */
	public String getNhicardNo() {
		return nhicardNo.getValue();
	}

	/**
	 * 健康卡号设置
	 * @param nhicardNo the nhicardNo to set
	 */
	public void setNhicardNo(String nhicardNo) {
		this.nhicardNo.setValue(nhicardNo);
	}
	/**
	 * 修改健康卡号
	 */
	public void modifyNhicardNo(String nhicardNo) {
		this.nhicardNo.modifyValue(nhicardNo);
	}
	/**
	 * 得到参数
	 * 
	 * @return TParm
	 */
	public TParm getParm() {
		TParm parm = super.getParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("NHI_NO", this.getNhiNo());
		parm.setData("NEW_BODY_WEIGHT", this.getNewBodyWeight());
		parm.setData("NEW_BODY_HEIGHT", this.getNewBodyHeight());//add by lili 20180403
		return parm;
	}

	/**
	 * 加锁病患
	 * 
	 * @param program
	 *            String 程序名
	 * @return boolean true 成功 false 失败
	 */
	public boolean lockPat(String program) {
		if (getMrNo() == null || getMrNo().length() == 0)
			return false;
		return PatTool.getInstance().lockPat(getMrNo(), program);
	}

	/**
	 * 根据给入的身份证号和姓名查找以前身份证号或姓名相同的病人
	 * 
	 * @param idNo
	 *            String
	 * @param patName
	 *            String
	 * @return result TParm
	 */
	public TParm isSamePat(String idNo, String patName) {
		TParm result = new TParm();
		if (StringUtil.isNullString(idNo) || StringUtil.isNullString(patName)) {
			result.setErrCode(-1);
			result.setErrText("参数错误");
			return result;
		}
		if (!StringTool.isId(idNo)) {
			result.setErrCode(-1);
			result.setErrText("不是有效身份证号");
			return result;
		}
		TJDODBTool s;
		String sql = this.IS_SAME_PAT_SQL.replace("#", idNo).replace("#",
				patName);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 病患是否已经锁定
	 * 
	 * @return boolean true 锁定 false 没有锁定
	 */
	public boolean isLockPat() {
		if (getMrNo() == null || getMrNo().length() == 0)
			return false;
		return PatTool.getInstance().isLockPat(getMrNo());
	}

	/**
	 * 得到加锁信息
	 * 
	 * @return String
	 */
	public String getLockParmString() {
		if (getMrNo() == null || getMrNo().length() == 0)
			return "";
		return PatTool.getInstance().getLockParmString(getMrNo());
	}

	/**
	 * 解锁病患
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean unLockPat() {
		if (getMrNo() == null || getMrNo().length() == 0)
			return false;
		return PatTool.getInstance().unLockPat(getMrNo());
	}

	public static void main(String args[]) {
		JavaHisDebug.initClient();
		// Pat pat = new Pat();
		Pat pat = Pat.onQueryByMrNo("1");
		pat.modifyIdNo("icdno");
		pat.modifyMotherMrNo("mothermrno");
		pat.modifyName("name");
		pat.modifyName1("name1");
		pat.modifyPy1("SADFASDF");
		pat.modifyPy2("AS");
		pat.modifyMotherMrNo("123111");
		pat.modifyCtz1Code("1");
		pat.modifyCtz2Code("2");
		pat.modifyCtz3Code("3");
		pat.modifyTelCompany("telCompany");
		pat.modifyTelHome("tel2");
		pat.modifyCellPhone("cellPhone");
		pat.modifyPregnantDate(SystemTool.getInstance().getDate());

		// System.out.println(pat);
		// pat.lockPat("医生工作站");
		// pat.unLockPat();
		// pat.onNew();
		pat.onSave();

		// System.out.println(pat);
	}
	
	/**
	 * 修改病人归属
	 */
	public void modifyPatBelong(String patBelong) {
		this.patBelong.modifyValue(patBelong);
	}
	
	/**
	 * 得到病人归属
	 * @return the nhicardNo
	 */
	public String getPatBelong() {
		return patBelong.getValue();
	}
	
	
	
	/**
	 * 设置病患密级
	 * 
	 * @param securityCategory
	 *            String
	 */
	public void setSecurityCategory(String securityCategory) {
		this.securityCategory.setValue(securityCategory);
	}

	/**
	 * 得到病患密级
	 * 
	 * @return String
	 */
	public String getSecurityCategory() {
		return securityCategory.getValue();
	}

	/**
	 * 修改病患密级
	 * 
	 * @param securityCategory
	 *            String
	 */
	public void modifySecurityCategory(String securityCategory) {
		this.securityCategory.modifyValue(securityCategory);
	}

    /**
     * 设置新生儿体重
     * 
     * @param newBodyWeight
     *            double 新生儿体重
     */
    public void setNewBodyWeight(double newBodyWeight) {
        this.newBodyWeight.setValue(newBodyWeight);
    }

    /**
     * 得到新生儿体重
     * 
     * @return double 新生儿体重
     */
    public double getNewBodyWeight() {
        return newBodyWeight.getValue();
    }

    /**
     * 修改新生儿体重
     * 
     * @param newBodyWeight
     *            double 新生儿体重
     */
    public void modifyNewBodyWeight(double newBodyWeight) {
        this.newBodyWeight.modifyValue(newBodyWeight);
    }
    
    /**
     * 设置新生儿身长
     * 
     * @param newBodyWeight
     *            double 新生儿身长
     * add by yangjj 20150312
     */
    public void setNewBodyHeight(double newBodyHeight) {
        this.newBodyHeight.setValue(newBodyHeight);
    }

    /**
     * 得到新生儿身长
     * 
     * @return double 新生儿身长
     * add by yangjj 20150312
     */
    public double getNewBodyHeight() {
        return newBodyHeight.getValue();
    }

    /**
     * 修改新生儿身长
     * 
     * @param newBodyWeight
     *            double 新生儿身长
     * add by yangjj 20150312
     */
    public void modifyNewBodyHeight(double newBodyHeight) {
        this.newBodyHeight.modifyValue(newBodyHeight);
    }

	
	
	
	
	
}
