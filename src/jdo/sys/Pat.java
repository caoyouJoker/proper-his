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
 * Title: ���߶���
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
	 * ������ MR_NO
	 */
	private String mrNo = "";
	/**
	 * סԺ�� IPD_NO
	 */
	private StringValue ipdNo = new StringValue(this);
	/**
	 * �ϲ������ű�� MERGE_FLG
	 */
	private BooleanValue mergeFlg = new BooleanValue(this);
	/**
	 * �ϲ������� MERGE_TOMRNO
	 */
	private StringValue mergeToMrNo = new StringValue(this);
	/**
	 * ĸ�׵Ĳ����� MOTHER_MRNO
	 */
	private StringValue motherMrNo = new StringValue(this);
	/**
	 * ���� PAT_NAME
	 */
	private StringValue name = new StringValue(this);
	/**
	 * ����2 PAT_NAME1
	 */
	private StringValue name1 = new StringValue(this);
	/**
	 * ƴ���� PY1
	 */
	private StringValue py1 = new StringValue(this);
	/**
	 * ע���� PY2
	 */
	private StringValue py2 = new StringValue(this);
	/**
	 * ����˱�� FOREIGNER_FLG
	 */
	private BooleanValue foreignerFlg = new BooleanValue(this);
	/**
	 * �绰 TEL_COMPANY
	 */
	private StringValue telCompany = new StringValue(this);
	/**
	 * �绰 TEL_HOME
	 */
	private StringValue telHome = new StringValue(this);
	/**
	 * �������� BIRTH_DATE
	 */
	private TimestampValue birthday = new TimestampValue(this);
	/**
	 * �Ա���� SEX_CODE
	 */
	private StringValue sexCode = new StringValue(this);
	/**
	 * ���֤�� IDNO
	 */
	private StringValue idNo = new StringValue(this);
	/**
	 * ��������� CTZ1_CODE
	 */
	private StringValue ctz1Code = new StringValue(this);
	/**
	 * ������� CTZ2_CODE
	 */
	private StringValue ctz2Code = new StringValue(this);
	/**
	 * ������� CTZ3_CODE
	 */
	private StringValue ctz3Code = new StringValue(this);
	/**
	 * �ֻ� CELL_PHONE
	 */
	private StringValue cellPhone = new StringValue(this);
	/**
	 * ������λ COMPANY_DESC
	 */
	private StringValue companyDesc = new StringValue(this);
	/**
	 * �����ʼ� E_MAIL
	 */
	private StringValue email = new StringValue(this);
	/**
	 * Ѫ�� BLOOD_TYPE
	 */
	private StringValue bloodType = new StringValue(this);
	/**
	 * RHѪ�� BLOOD_RH_TYPE RH����: + RH����: -
	 */
	private StringValue bloodRHType = new StringValue(this);
	/**
	 * �������� DEAD_DATE
	 */
	private TimestampValue deadDate = new TimestampValue(this);
	/**
	 * ͨ�ŵ�ַ�ʵ����� POST_CODE
	 */
	private StringValue postCode = new StringValue(this);
	/**
	 * ͨ�ŵ�ַ_· ADDRESS
	 */
	private StringValue address = new StringValue(this);
	/**
	 * �����ʵ����� RESID_POST_CODE
	 */
	private StringValue residPostCode = new StringValue(this);
	/**
	 * ������ַ RESID_ADDRESS
	 */
	private StringValue residAddress = new StringValue(this);
	/**
	 * ������ϵ�� CONTACTS_NAME
	 */
	private StringValue contactsName = new StringValue(this);
	/**
	 * ������ϵ�˹�ϵ RELATION_CODE
	 */
	private StringValue relationCode = new StringValue(this);
	/**
	 * ������ϵ�˵绰 CONTACTS_TEL
	 */
	private StringValue contactsTel = new StringValue(this);
	/**
	 * ������ϵ�˵�ַ CONTACTS_ADDRESS
	 */
	private StringValue contactsAddress = new StringValue(this);
	/**
	 * ����״̬(1-�ѻ�,2-δ��,3-�־�) MARRIAGE_CODE
	 */
	private StringValue marriageCode = new StringValue(this);
	/**
	 * ��ż���֤�� SPOUSE_IDNO
	 */
	private StringValue spouseIdno = new StringValue(this);
	/**
	 * �������֤��(����) FATHER_IDNO
	 */
	private StringValue fatherIdno = new StringValue(this);
	/**
	 * �������֤��(ĸ��) MOTHER_IDNO
	 */
	private StringValue motherIdno = new StringValue(this);
	/**
	 * �ڽ� RELIGION_CODE
	 */
	private StringValue peligionCode = new StringValue(this);
	/**
	 * �����̶ȴ��� EDUCATION_CODE
	 */
	private StringValue educationCode = new StringValue(this);
	/**
	 * ְҵ������ OCC_CODE
	 */
	private StringValue occCode = new StringValue(this);
	/**
	 * �������� NATION_CODE
	 */
	private StringValue nationCode = new StringValue(this);
	/**
	 * ���� SPECIES_CODE
	 */
	private StringValue speciesCode = new StringValue(this);
	/**
	 * �������� FIRST_ADM_DATE
	 */
	private TimestampValue firstAdmDate = new TimestampValue(this);
	/**
	 * ����������� RCNT_OPD_DATE
	 */
	private TimestampValue rcntOpdDate = new TimestampValue(this);
	/**
	 * �������Ʊ� RCNT_OPD_DEPT
	 */
	private StringValue rcntOpdDept = new StringValue(this);
	/**
	 * ���סԺ���� RCNT_IPD_DATE
	 */
	private TimestampValue rcntIpdDate = new TimestampValue(this);
	/**
	 * ���סԺ�Ʊ� RCNT_IPD_DEPT
	 */
	private StringValue rcntIpdDept = new StringValue(this);
	/**
	 * ����������� RCNT_EMG_DATE
	 */
	private TimestampValue rcntEmgDate = new TimestampValue(this);
	/**
	 * �������Ʊ� RCNT_EMG_DEPT
	 */
	private StringValue rcntEmgDept = new StringValue(this);
	/**
	 * ���ˬԼ���� RCNT_MISS_DATE
	 */
	private TimestampValue rcntMissDate = new TimestampValue(this);
	/**
	 * ���ˬԼ�Ʊ� RCNT_MISS_DEPT
	 */
	private StringValue rcntMissDept = new StringValue(this);
	/**
	 * ����׶��������� KID_EXAM_RCNT_DATE
	 */
	private TimestampValue kidExamRcntDate = new TimestampValue(this);
	/**
	 * ����׶�ע������ KID_INJ_RCNT_DATE
	 */
	private TimestampValue kidInjRcntDate = new TimestampValue(this);
	/**
	 * ������˽������� ADULT_EXAM_DATE
	 */
	private TimestampValue adultExamDate = new TimestampValue(this);
	/**
	 * ���ĨƬ������� SMEAR_RCNT_DATE
	 */
	private TimestampValue smearRcntDate = new TimestampValue(this);
	/**
	 * ��� HEIGHT
	 */
	private DoubleValue height = new DoubleValue(this);
	/**
	 * ���� WEIGHT
	 */
	private DoubleValue weight = new DoubleValue(this);
	/**
	 * ��ע DESCRIPTION
	 */
	private StringValue description = new StringValue(this);
	/**
	 * ��Ժ������ BORNIN_FLG
	 */
	private BooleanValue borninFlg = new BooleanValue(this);
	/**
	 * ��������̥ע�� NEWBORN_SEQ
	 */
	private IntValue newbornSeq = new IntValue(this);
	/**
	 * ����� PREMATURE_FLG
	 */
	private BooleanValue prematureFlg = new BooleanValue(this);
	
	
    private StringValue gestationalWeeks= new StringValue(this);//
    /**
     * ����������
     */
    private DoubleValue newBodyWeight = new DoubleValue(this);//
    /**
     * ��������
     */
    private DoubleValue newBodyHeight = new DoubleValue(this);//add by lili 20180403
	/**
	 * �м� HANDICAP_FLG
	 */
	private BooleanValue handicapFlg = new BooleanValue(this);
	/**
	 * ������ BLACK_FLG
	 */
	private BooleanValue blackFlg = new BooleanValue(this);
	/**
	 * ����ע�� NAME_INVISIBLE_FLG
	 */
	private BooleanValue nameInvisibleFlg = new BooleanValue(this);
	/**
	 * �������ܱ������� LAW_PROTECT_FLG
	 */
	private BooleanValue lawProtectFlg = new BooleanValue(this);
	/**
	 * LMP����
	 */
	private TimestampValue LMPDate = new TimestampValue(this);
	/**
	 * Ԥ���� PREGNANT_DATE
	 */
	private TimestampValue pregnantDate = new TimestampValue(this);
	/**
	 * ������ʼ���� BREASTFEED_STARTDATE
	 */
	private TimestampValue breastfeedStartDate = new TimestampValue(this);
	/**
	 * ������ֹ���� BREASTFEED_ENDDATE
	 */
	private TimestampValue breastfeedEndDate = new TimestampValue(this);
	/**
	 * ������״̬һ PAT1_CODE
	 */
	private StringValue pat1Code = new StringValue(this);
	/**
	 * ������״̬�� PAT2_CODE
	 */
	private StringValue pat2Code = new StringValue(this);
	/**
	 * ������״̬�� PAT3_CODE
	 */
	private StringValue pat3Code = new StringValue(this);
	/**
	 * ������
	 */
	private StringValue homePlaceCode = new StringValue(this);
	/**
	 * ����
	 */
	private StringValue birthPlace = new StringValue(this);
	/**
	 * ��λ��ַ
	 */
	private StringValue companyAddress = new StringValue(this);
	/**
	 * ��λ�ʱ�
	 */
	private StringValue companyPost = new StringValue(this);
	/**
	 * ��������
	 */
	private StringValue nhicardNo=new StringValue(this); 
	/**
     *
     */
	private StringValue nhiNo=new StringValue(this); 
	/**
	 * �������֤�Ż�������ͬ�Ĳ���
	 */
	private static final String IS_SAME_PAT_SQL = "SELECT * FROM SYS_PATINFO WHERE IDNO='#' OR PAT_NAME='#'";

	/**
	 * ����������
	 */
	private StringValue patBelong = new StringValue(this); 
	
	/**
	 * �����ܼ�SECURITY_CATEGORY
	 */
	private StringValue securityCategory = new StringValue(this); 
	
	/**
	 * ������
	 */
	public Pat() {
		StringBuffer sb = new StringBuffer();
		// ������
		sb.append("mrNo:MR_NO;");
		// סԺ��
		sb.append("ipdNo:IPD_NO;");
		// �ϲ������ű��
		sb.append("mergeFlg:MERGE_FLG;");
		// �ϲ�������
		sb.append("mergeToMrNo:MERGE_TOMRNO;");
		// ĸ�׵Ĳ�����
		sb.append("motherMrNo:MOTHER_MRNO;");
		// ����
		sb.append("name:PAT_NAME;");
		// ����2
		sb.append("name1:PAT_NAME1;");
		// ƴ����
		sb.append("py1:PY1;");
		// ע����
		sb.append("py2:PY2;");
		// ����˱��
		sb.append("foreignerFlg:FOREIGNER_FLG;");
		// ���֤��
		sb.append("idNo:IDNO;");
		// ��������
		sb.append("birthday:BIRTH_DATE;");
		// �����
		sb.append("ctz1Code:CTZ1_CODE;");
		// �������
		sb.append("ctz2Code:CTZ2_CODE;");
		// �������
		sb.append("ctz3Code:CTZ3_CODE;");
		// �绰
		sb.append("telHome:TEL_HOME;");
		// �����绰
		sb.append("telCompany:TEL_COMPANY;");
		// �Ա����
		sb.append("sexCode:SEX_CODE;");
		// �ֻ�
		sb.append("cellPhone:CELL_PHONE;");
		// ������λ
		sb.append("companyDesc:COMPANY_DESC;");
		// �����ʼ�
		sb.append("email:E_MAIL;");
		// Ѫ��
		sb.append("bloodType:BLOOD_TYPE;");
		// RHѪ��
		sb.append("bloodRHType:BLOOD_RH_TYPE;");
		// ��������
		sb.append("deadDate:DEAD_DATE;");
		// ͨ�ŵ�ַ�ʵ�����
		sb.append("postCode:POST_CODE;");
		// ͨ�ŵ�ַ_·
		sb.append("address:ADDRESS;");
		// �����ʵ�����
		sb.append("residPostCode:RESID_POST_CODE;");
		// ������ַ
		sb.append("residAddress:RESID_ADDRESS;");
		// ������ϵ��
		sb.append("contactsName:CONTACTS_NAME;");
		// ������ϵ�˹�ϵ
		sb.append("relationCode:RELATION_CODE;");
		// ������ϵ�˵绰
		sb.append("contactsTel:CONTACTS_TEL;");
		// ������ϵ�˵�ַ
		sb.append("contactsAddress:CONTACTS_ADDRESS;");
		// ����״̬(1-�ѻ�,2-δ��,3-�־�)
		sb.append("marriageCode:MARRIAGE_CODE;");
		// ��ż���֤��
		sb.append("spouseIdno:SPOUSE_IDNO;");
		// �������֤��(����)
		sb.append("fatherIdno:FATHER_IDNO;");
		// �������֤��(ĸ��)
		sb.append("motherIdno:MOTHER_IDNO;");
		// �ڽ�
		sb.append("peligionCode:RELIGION_CODE;");
		// �����̶ȴ���
		sb.append("educationCode:EDUCATION_CODE;");
		// ְҵ������
		sb.append("occCode:OCC_CODE;");
		// ��������
		sb.append("nationCode:NATION_CODE;");
		// ����
		sb.append("speciesCode:SPECIES_CODE;");
		// ��������
		sb.append("firstAdmDate:FIRST_ADM_DATE;");
		// �����������
		sb.append("rcntOpdDate:RCNT_OPD_DATE;");
		// �������Ʊ�
		sb.append("rcntOpdDept:RCNT_OPD_DEPT;");
		// ���סԺ����
		sb.append("rcntIpdDate:RCNT_IPD_DATE;");
		// ���סԺ�Ʊ�
		sb.append("rcntIpdDept:RCNT_IPD_DEPT;");
		// �����������
		sb.append("rcntEmgDate:RCNT_EMG_DATE;");
		// �������Ʊ�
		sb.append("rcntEmgDept:RCNT_EMG_DEPT;");
		// ���ˬԼ����
		sb.append("rcntMissDate:RCNT_MISS_DATE;");
		// ���ˬԼ�Ʊ�
		sb.append("rcntMissDept:RCNT_MISS_DEPT;");
		// ����׶���������
		sb.append("kidExamRcntDate:KID_EXAM_RCNT_DATE;");
		// ����׶�ע������
		sb.append("kidInjRcntDate:KID_INJ_RCNT_DATE;");
		// ������˽�������
		sb.append("adultExamDate:ADULT_EXAM_DATE;");
		// ���ĨƬ�������
		sb.append("smearRcntDate:SMEAR_RCNT_DATE;");
        
		sb.append("newBodyWeight:NEW_BODY_WEIGHT;");//
        // ��������
        sb.append("newBodyHeight:NEW_BODY_HEIGHT;");//add by lili 20180403
		// ���
		sb.append("height:HEIGHT;");
		// ����
		sb.append("weight:WEIGHT;");
		// ��ע
		sb.append("description:DESCRIPTION;");
		// ��Ժ������
		sb.append("borninFlg:BORNIN_FLG;");
		// ��������̥ע��
		sb.append("newbornSeq:NEWBORN_SEQ;");
		// �����
		sb.append("prematureFlg:PREMATURE_FLG;");
		// �м�
		sb.append("handicapFlg:HANDICAP_FLG;");
		// ������
		sb.append("blackFlg:BLACK_FLG;");
		// ����ע��
		sb.append("nameInvisibleFlg:NAME_INVISIBLE_FLG;");
		// �������ܱ�������
		sb.append("lawProtectFlg:LAW_PROTECT_FLG;");
		// LMP����
		sb.append("LMPDate:LMP_DATE;");
		// Ԥ����
		sb.append("pregnantDate:PREGNANT_DATE;");
		// ������ʼ����
		sb.append("breastfeedStartDate:BREASTFEED_STARTDATE;");
		// ������ֹ����
		sb.append("breastfeedEndDate:BREASTFEED_ENDDATE;");
		// ������״̬һ
		sb.append("pat1Code:PAT1_CODE;");
		// ������״̬��
		sb.append("pat2Code:PAT2_CODE;");
		// ������״̬��
		sb.append("pat3Code:PAT3_CODE;");
		// ������
		sb.append("homePlaceCode:HOMEPLACE_CODE;");
		// ����
		sb.append("birthPlace:BIRTHPLACE;");
		// ��λ��ַ
		sb.append("companyAddress:ADDRESS_COMPANY;");
		// ��λ�ʱ�
		sb.append("companyPost:POST_COMPANY;");
		//��������
		sb.append("nhicardNo:NHICARD_NO;");
		//ҽ������
		sb.append("nhiNo:NHI_NO;");
		//����������
		sb.append("patBelong:PAT_BELONG;");
		//�����ܼ�
		sb.append("securityCategory:SECURITY_CATEGORY");
		setMapString(sb.toString());
	}

	/**
	 * ��ѯ��������(���ݲ�����)
	 * 
	 * @param mrno
	 *            String ������
	 * @return Pat ��������
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
	 * ��������
	 * 
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onNew() {
		return PatTool.getInstance().newPat(this);
	}

	/**
	 * ���涯��
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
	 * ɾ��������Ϣ
	 * 
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onDelete() {
		return PatTool.getInstance().onDelete(this);
	}

	/**
	 * ɾ��������Ϣ(����ɾ��)
	 * 
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onDelete$() {
		return PatTool.getInstance().onDelete$(this);
	}

	/**
	 * �ϲ�������
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
	 * ���ò�����
	 * 
	 * @param mrNo
	 *            String ������
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	// ===================pangben modify 20110808 start
	/**
	 * �õ�������
	 * 
	 * @return String ������
	 */
	public String getMrNo() {
		return mrNo;
	}

	/**
	 * �������񿨺�
	 * 
	 * @param citNo
	 *            String ���񿨺�
	 */
	public void setNhiNo(String nhiNo) {
		this.nhiNo.setValue(nhiNo);
	}

	// ===================pangben modify 20110808 stop
	/**
	 * �õ����񿨺�
	 * 
	 * @return String ���񿨺�
	 */
	public String getNhiNo() {
		return nhiNo.getValue();
	}
	/**
	 * �޸����񿨺�
	 * 
	 * @param ipdNo
	 *            String
	 */
	public void modifyNhiNo(String nhiNo) {
		this.nhiNo.modifyValue(nhiNo);
	}


	/**
	 * ����סԺ��
	 * 
	 * @param ipdNo
	 *            String
	 */
	public void setIpdNo(String ipdNo) {
		this.ipdNo.setValue(ipdNo);
	}

	/**
	 * �õ�סԺ��
	 * 
	 * @return String
	 */
	public String getIpdNo() {
		return ipdNo.getValue();
	}

	/**
	 * �޸�סԺ��
	 * 
	 * @param ipdNo
	 *            String
	 */
	public void modifyIpdNo(String ipdNo) {
		this.ipdNo.modifyValue(ipdNo);
	}

	/**
	 * ���úϲ������ű��
	 * 
	 * @param mergeFlg
	 *            boolean
	 */
	public void setMergeFlg(boolean mergeFlg) {
		this.mergeFlg.setValue(mergeFlg);
	}

	/**
	 * �Ƿ�ϲ������ű��
	 * 
	 * @return boolean
	 */
	public boolean isMergeFlg() {
		return mergeFlg.getValue();
	}

	/**
	 * �޸ĺϲ������ű��
	 * 
	 * @param mergeFlg
	 *            boolean
	 */
	public void modifyMergeFlg(boolean mergeFlg) {
		this.mergeFlg.modifyValue(mergeFlg);
	}

	/**
	 * ���úϲ�������
	 * 
	 * @param mergeToMrNo
	 *            String
	 */
	public void setMergeToMrNo(String mergeToMrNo) {
		this.mergeToMrNo.setValue(mergeToMrNo);
	}

	/**
	 * �õ��ϲ�������
	 * 
	 * @return String
	 */
	public String getMergeToMrNo() {
		return mergeToMrNo.getValue();
	}

	/**
	 * �޸ĺϲ�������
	 * 
	 * @param mergeToMrNo
	 *            String
	 */
	public void modifyMergeToMrNo(String mergeToMrNo) {
		this.mergeToMrNo.modifyValue(mergeToMrNo);
	}

	/**
	 * ����ĸ�׵Ĳ�����
	 * 
	 * @param motherMrNo
	 *            String
	 */
	public void setMotherMrNo(String motherMrNo) {
		this.motherMrNo.setValue(motherMrNo);
	}

	/**
	 * �õ�ĸ�׵Ĳ�����
	 * 
	 * @return String
	 */
	public String getMotherMrNo() {
		return motherMrNo.getValue();
	}

	/**
	 * �޸�ĸ�׵Ĳ�����
	 * 
	 * @param motherMrNo
	 *            String
	 */
	public void modifyMotherMrNo(String motherMrNo) {
		this.motherMrNo.modifyValue(motherMrNo);
	}

	/**
	 * ��������
	 * 
	 * @param name
	 *            String ����
	 */
	public void setName(String name) {
		this.name.setValue(name);
	}

	/**
	 * �õ�����
	 * 
	 * @return String ����
	 */
	public String getName() {
		return name.getValue();
	}

	/**
	 * �޸�����
	 * 
	 * @param name
	 *            String
	 */
	public void modifyName(String name) {
		this.name.modifyValue(name);
	}

	/**
	 * ��������1
	 * 
	 * @param name1
	 *            String ����1
	 */
	public void setName1(String name1) {
		this.name1.setValue(name1);
	}

	/**
	 * �õ�����2
	 * 
	 * @return String ����2
	 */
	public String getName1() {
		return name1.getValue();
	}

	/**
	 * �޸�����2
	 * 
	 * @param name
	 *            String
	 */
	public void modifyName1(String name) {
		this.name1.modifyValue(name);
	}

	/**
	 * ����ƴ����
	 * 
	 * @param py1
	 *            String
	 */
	public void setPy1(String py1) {
		this.py1.setValue(py1);
	}

	/**
	 * �õ�ƴ����
	 * 
	 * @return String
	 */
	public String getPy1() {
		return py1.getValue();
	}

	/**
	 * �޸�ƴ����
	 * 
	 * @param py1
	 *            String
	 */
	public void modifyPy1(String py1) {
		this.py1.modifyValue(py1);
	}

	/**
	 * ����ע����
	 * 
	 * @param py2
	 *            String
	 */
	public void setPy2(String py2) {
		this.py2.setValue(py2);
	}

	/**
	 * �õ�ע����
	 * 
	 * @return String
	 */
	public String getPy2() {
		return py2.getValue();
	}

	/**
	 * �޸�ע����
	 * 
	 * @param py2
	 *            String
	 */
	public void modifyPy2(String py2) {
		this.py2.modifyValue(py2);
	}

	/**
	 * ��������˱��
	 * 
	 * @param foreignerFlg
	 *            boolean
	 */
	public void setForeignerFlg(boolean foreignerFlg) {
		this.foreignerFlg.setValue(foreignerFlg);
	}

	/**
	 * �Ƿ��������
	 * 
	 * @return boolean
	 */
	public boolean isForeignerFlg() {
		return foreignerFlg.getValue();
	}

	/**
	 * �޸�����˱��
	 * 
	 * @param foreignerFlg
	 *            boolean
	 */
	public void modifyForeignerFlg(boolean foreignerFlg) {
		this.foreignerFlg.modifyValue(foreignerFlg);
	}

	/**
	 * ���õ绰
	 * 
	 * @param telCompany
	 *            String�绰
	 */
	public void setTelCompany(String tel) {
		this.telCompany.setValue(tel);
	}

	/**
	 * �õ��绰
	 * 
	 * @return String �绰
	 */
	public String getTelCompany() {
		return telCompany.getValue();
	}

	/**
	 * �޸ĵ绰
	 * 
	 * @param telCompany
	 *            String
	 */
	public void modifyTelCompany(String tel) {
		this.telCompany.modifyValue(tel);
	}

	/**
	 * ���ü�ͥ�ĵ绰
	 * 
	 * @param telHome
	 *            String
	 */
	public void setTelHome(String telHome) {
		this.telHome.setValue(telHome);
	}

	/**
	 * �õ���ͥ�ĵ绰
	 * 
	 * @return String
	 */
	public String getTelHome() {
		return telHome.getValue();
	}

	/**
	 * �޸ļ�ͥ�ĵ绰
	 * 
	 * @param otherTel
	 *            String
	 */
	public void modifyTelHome(String telHome) {
		this.telHome.modifyValue(telHome);
	}

	/**
	 * ���ó�������
	 * 
	 * @param birthday
	 *            Timestamp ��������
	 */
	public void setBirthday(Timestamp birthday) {
		this.birthday.setValue(birthday);
	}

	/**
	 * �õ���������
	 * 
	 * @return Timestamp ��������
	 */
	public Timestamp getBirthday() {
		return birthday.getValue();
	}

	/**
	 * �޸ĳ�������
	 * 
	 * @param birthday
	 *            Timestamp
	 */
	public void modifyBirthdy(Timestamp birthday) {
		this.birthday.modifyValue(birthday);
	}

	/**
	 * �õ���������(String ��)
	 * 
	 * @return String ��������
	 */
	public String getBirthdayString() {
		return StringTool.getString(getBirthday(), "yyyy��mm��dd��");
	}

	/**
	 * �����Ա����
	 * 
	 * @param sexCode
	 *            String �Ա����
	 */
	public void setSexCode(String sexCode) {
		this.sexCode.setValue(sexCode);
	}

	/**
	 * �õ��Ա����
	 * 
	 * @return String �Ա����
	 */
	public String getSexCode() {
		return sexCode.getValue();
	}

	/**
	 * �޸��Ա����
	 * 
	 * @param sexCode
	 *            String
	 */
	public void modifySexCode(String sexCode) {
		this.sexCode.modifyValue(sexCode);
	}

	/**
	 * �õ��Ա�����
	 * 
	 * @return String �Ա�����
	 */
	public String getSexString() {
		String code = getSexCode();
		if (code == null || code.length() == 0)
			return "";
		return DictionaryTool.getInstance().getSexName(code);
	}

	/**
	 * �������֤��
	 * 
	 * @param idNo
	 *            String ���֤��
	 */
	public void setIdNo(String idNo) {
		this.idNo.setValue(idNo);
	}

	/**
	 * �õ����֤��
	 * 
	 * @return String ���֤��
	 */
	public String getIdNo() {
		return idNo.getValue();
	}

	/**
	 * �޸����֤��
	 * 
	 * @param idNo
	 *            String
	 */
	public void modifyIdNo(String idNo) {
		this.idNo.modifyValue(idNo);
	}

	/**
	 * ���������
	 * 
	 * @param ctz1Code
	 *            String �����
	 */
	public void setCtz1Code(String ctz1Code) {
		this.ctz1Code.setValue(ctz1Code);
	}

	/**
	 * �õ������
	 * 
	 * @return String �����
	 */
	public String getCtz1Code() {
		return ctz1Code.getValue();
	}

	/**
	 * �޸������
	 * 
	 * @param ctz1Code
	 *            String
	 */
	public void modifyCtz1Code(String ctz1Code) {
		this.ctz1Code.modifyValue(ctz1Code);
	}

	/**
	 * ���ö������
	 * 
	 * @param ctz2Code
	 *            String �������
	 */
	public void setCtz2Code(String ctz2Code) {
		this.ctz2Code.setValue(ctz2Code);
	}

	/**
	 * �õ��������
	 * 
	 * @return String �������
	 */
	public String getCtz2Code() {
		return ctz2Code.getValue();
	}

	/**
	 * �޸Ķ������
	 * 
	 * @param ctz2Code
	 *            String
	 */
	public void modifyCtz2Code(String ctz2Code) {
		this.ctz2Code.modifyValue(ctz2Code);
	}

	/**
	 * �����������
	 * 
	 * @param ctz3Code
	 *            String �������
	 */
	public void setCtz3Code(String ctz3Code) {
		this.ctz3Code.setValue(ctz3Code);
	}

	/**
	 * �õ��������
	 * 
	 * @return String �������
	 */
	public String getCtz3Code() {
		return ctz3Code.getValue();
	}

	/**
	 * �޸��������
	 * 
	 * @param ctz3Code
	 *            String
	 */
	public void modifyCtz3Code(String ctz3Code) {
		this.ctz3Code.modifyValue(ctz3Code);
	}

	/**
	 * �����ֻ�
	 * 
	 * @param cellPhone
	 *            String �ֻ�
	 */
	public void setCellPhone(String cellPhone) {
		this.cellPhone.setValue(cellPhone);
	}

	/**
	 * �õ��ֻ�
	 * 
	 * @return String �ֻ�
	 */
	public String getCellPhone() {
		return cellPhone.getValue();
	}

	/**
	 * �޸��ֻ�
	 * 
	 * @param cellPhone
	 *            String
	 */
	public void modifyCellPhone(String cellPhone) {
		this.cellPhone.modifyValue(cellPhone);
	}

	/**
	 * ���ù�����λ
	 * 
	 * @param companyDesc
	 *            String ������λ
	 */
	public void setCompanyDesc(String companyDesc) {
		this.companyDesc.setValue(companyDesc);
	}

	/**
	 * �õ�������λ
	 * 
	 * @return String ������λ
	 */
	public String getCompanyDesc() {
		return companyDesc.getValue();
	}

	/**
	 * �޸Ĺ�����λ
	 * 
	 * @param companyDesc
	 *            String ������λ
	 */
	public void modifyCompanyDesc(String companyDesc) {
		this.companyDesc.modifyValue(companyDesc);
	}

	/**
	 * ���õ����ʼ�
	 * 
	 * @param email
	 *            String �����ʼ�
	 */
	public void setEmail(String email) {
		this.email.setValue(email);
	}

	/**
	 * �õ������ʼ�
	 * 
	 * @return String �����ʼ�
	 */
	public String getEmail() {
		return email.getValue();
	}

	/**
	 * �޸ĵ����ʼ�
	 * 
	 * @param email
	 *            String �����ʼ�
	 */
	public void modifyEmail(String email) {
		this.email.modifyValue(email);
	}

	/**
	 * ����Ѫ��
	 * 
	 * @param bloodType
	 *            String Ѫ��
	 */
	public void setBloodType(String bloodType) {
		this.bloodType.setValue(bloodType);
	}

	/**
	 * �õ�Ѫ��
	 * 
	 * @return String Ѫ��
	 */
	public String getBloodType() {
		return bloodType.getValue();
	}

	/**
	 * �޸�Ѫ��
	 * 
	 * @param bloodType
	 *            String Ѫ��
	 */
	public void modifyBloodType(String bloodType) {
		this.bloodType.modifyValue(bloodType);
	}

	/**
	 * ����RHѪ��
	 * 
	 * @param bloodRHType
	 *            String RHѪ��
	 */
	public void setBloodRHType(String bloodRHType) {
		this.bloodRHType.setValue(bloodRHType);
	}

	/**
	 * �õ�RHѪ��
	 * 
	 * @return String RHѪ��
	 */
	public String getBloodRHType() {
		return bloodRHType.getValue();
	}

	/**
	 * �޸�RHѪ��
	 * 
	 * @param bloodRHType
	 *            String RHѪ��
	 */
	public void modifyBloodRHType(String bloodRHType) {
		this.bloodRHType.modifyValue(bloodRHType);
	}

	/**
	 * ������������
	 * 
	 * @param deadDate
	 *            Timestamp ��������
	 */
	public void setDeadDate(Timestamp deadDate) {
		this.deadDate.setValue(deadDate);
	}

	/**
	 * �õ���������
	 * 
	 * @return Timestamp ��������
	 */
	public Timestamp getDeadDate() {
		return deadDate.getValue();
	}

	/**
	 * �޸���������
	 * 
	 * @param deadDate
	 *            Timestamp ��������
	 */
	public void modifyDeadDate(Timestamp deadDate) {
		this.deadDate.modifyValue(deadDate);
	}

	/**
	 * ����ͨ�ŵ�ַ�ʵ�����
	 * 
	 * @param postCode
	 *            String ͨ�ŵ�ַ�ʵ�����
	 */
	public void setPostCode(String postCode) {
		this.postCode.setValue(postCode);
	}

	/**
	 * �õ�ͨ�ŵ�ַ�ʵ�����
	 * 
	 * @return String ͨ�ŵ�ַ�ʵ�����
	 */
	public String getPostCode() {
		return postCode.getValue();
	}

	/**
	 * �޸�ͨ�ŵ�ַ�ʵ�����
	 * 
	 * @param postCode
	 *            String ͨ�ŵ�ַ�ʵ�����
	 */
	public void modifyPostCode(String postCode) {
		this.postCode.modifyValue(postCode);
	}

	/**
	 * ����ͨ�ŵ�ַ_·
	 * 
	 * @param addressRoad
	 *            String ͨ�ŵ�ַ_·
	 */
	public void setAddress(String addressRoad) {
		this.address.setValue(addressRoad);
	}

	/**
	 * �õ�ͨ�ŵ�ַ_·
	 * 
	 * @return String ͨ�ŵ�ַ_·
	 */
	public String getAddress() {
		return address.getValue();
	}

	/**
	 * �޸�ͨ�ŵ�ַ_·
	 * 
	 * @param addressRoad
	 *            String ͨ�ŵ�ַ_·
	 */
	public void modifyAddress(String addressRoad) {
		this.address.modifyValue(addressRoad);
	}

	/**
	 * ���û����ʵ�����
	 * 
	 * @param residPostCode
	 *            String �����ʵ�����
	 */
	public void setResidPostCode(String residPostCode) {
		this.residPostCode.setValue(residPostCode);
	}

	/**
	 * �õ������ʵ�����
	 * 
	 * @return String �����ʵ�����
	 */
	public String getResidPostCode() {
		return residPostCode.getValue();
	}

	/**
	 * �޸Ļ����ʵ�����
	 * 
	 * @param residPostCode
	 *            String �����ʵ�����
	 */
	public void modifyResidPostCode(String residPostCode) {
		this.residPostCode.modifyValue(residPostCode);
	}

	/**
	 * ���û�����ַ
	 * 
	 * @param residRoad
	 *            String ������ַ
	 */
	public void setResidAddress(String residAddress) {
		this.residAddress.setValue(residAddress);
	}

	/**
	 * �õ�������ַ
	 * 
	 * @return String ������ַ
	 */
	public String getResidAddress() {
		return residAddress.getValue();
	}

	/**
	 * �޸Ļ�����ַ
	 * 
	 * @param residRoad
	 *            String ������ַ
	 */
	public void modifyResidAddress(String residAddress) {
		this.residAddress.modifyValue(residAddress);
	}

	/**
	 * ���ý�����ϵ��
	 * 
	 * @param contactsName
	 *            String ������ϵ��
	 */
	public void setContactsName(String contactsName) {
		this.contactsName.setValue(contactsName);
	}

	/**
	 * �õ�������ϵ��
	 * 
	 * @return String ������ϵ��
	 */
	public String getContactsName() {
		return contactsName.getValue();
	}

	/**
	 * �޸Ľ�����ϵ��
	 * 
	 * @param contactsName
	 *            String ������ϵ��
	 */
	public void modifyContactsName(String contactsName) {
		this.contactsName.modifyValue(contactsName);
	}

	/**
	 * ���ý�����ϵ�˹�ϵ
	 * 
	 * @param relationCode
	 *            String ������ϵ�˹�ϵ
	 */
	public void setRelationCode(String relationCode) {
		this.relationCode.setValue(relationCode);
	}

	/**
	 * �õ�������ϵ�˹�ϵ
	 * 
	 * @return String ������ϵ�˹�ϵ
	 */
	public String getRelationCode() {
		return relationCode.getValue();
	}

	/**
	 * �޸Ľ�����ϵ�˹�ϵ
	 * 
	 * @param relationCode
	 *            String ������ϵ�˹�ϵ
	 */
	public void modifyRelationCode(String relationCode) {
		this.relationCode.modifyValue(relationCode);
	}

	/**
	 * ���ý�����ϵ�˵绰
	 * 
	 * @param contactsTel
	 *            String ������ϵ�˵绰
	 */
	public void setContactsTel(String contactsTel) {
		this.contactsTel.setValue(contactsTel);
	}

	/**
	 * �õ�������ϵ�˵绰
	 * 
	 * @return String ������ϵ�˵绰
	 */
	public String getContactsTel() {
		return contactsTel.getValue();
	}

	/**
	 * �޸Ľ�����ϵ�˵绰
	 * 
	 * @param contactsTel
	 *            String ������ϵ�˵绰
	 */
	public void modifyContactsTel(String contactsTel) {
		this.contactsTel.modifyValue(contactsTel);
	}

	/**
	 * ���ý�����ϵ�˵�ַ
	 * 
	 * @param contactsAddress
	 *            String ������ϵ�˵�ַ
	 */
	public void setContactsAddress(String contactsAddress) {
		this.contactsAddress.setValue(contactsAddress);
	}

	/**
	 * �õ�������ϵ�˵�ַ
	 * 
	 * @return String ������ϵ�˵�ַ
	 */
	public String getContactsAddress() {
		return contactsAddress.getValue();
	}

	/**
	 * �޸Ľ�����ϵ�˵�ַ
	 * 
	 * @param contactsAddress
	 *            String ������ϵ�˵�ַ
	 */
	public void modifyContactsAddress(String contactsAddress) {
		this.contactsAddress.modifyValue(contactsAddress);
	}

	/**
	 * ���û���״̬(1-�ѻ�,2-δ��,3-�־�)
	 * 
	 * @param marriageCode
	 *            String ����״̬(1-�ѻ�,2-δ��,3-�־�)
	 */
	public void setMarriageCode(String marriageCode) {
		this.marriageCode.setValue(marriageCode);
	}

	/**
	 * �õ�����״̬(1-�ѻ�,2-δ��,3-�־�)
	 * 
	 * @return String ����״̬(1-�ѻ�,2-δ��,3-�־�)
	 */
	public String getMarriageCode() {
		return marriageCode.getValue();
	}

	/**
	 * �޸Ļ���״̬(1-�ѻ�,2-δ��,3-�־�)
	 * 
	 * @param marriageCode
	 *            String ����״̬(1-�ѻ�,2-δ��,3-�־�)
	 */
	public void modifyMarriageCode(String marriageCode) {
		this.marriageCode.modifyValue(marriageCode);
	}

	/**
	 * ������ż���֤��
	 * 
	 * @param spouseIdno
	 *            String ��ż���֤��
	 */
	public void setSpouseIdno(String spouseIdno) {
		this.spouseIdno.setValue(spouseIdno);
	}

	/**
	 * �õ���ż���֤��
	 * 
	 * @return String ��ż���֤��
	 */
	public String getSpouseIdno() {
		return spouseIdno.getValue();
	}

	/**
	 * �޸���ż���֤��
	 * 
	 * @param spouseIdno
	 *            String ��ż���֤��
	 */
	public void modifySpouseIdno(String spouseIdno) {
		this.spouseIdno.modifyValue(spouseIdno);
	}

	/**
	 * �����������֤��(����)
	 * 
	 * @param fatherIdno
	 *            String �������֤��(����)
	 */
	public void setFatherIdno(String fatherIdno) {
		this.fatherIdno.setValue(fatherIdno);
	}

	/**
	 * �õ��������֤��(����)
	 * 
	 * @return String �������֤��(����)
	 */
	public String getFatherIdno() {
		return fatherIdno.getValue();
	}

	/**
	 * �޸��������֤��(����)
	 * 
	 * @param fatherIdno
	 *            String �������֤��(����)
	 */
	public void modifyFatherIdno(String fatherIdno) {
		this.fatherIdno.modifyValue(fatherIdno);
	}

	/**
	 * �����������֤��(ĸ��)
	 * 
	 * @param motherIdno
	 *            String �������֤��(ĸ��)
	 */
	public void setMotherIdno(String motherIdno) {
		this.motherIdno.setValue(motherIdno);
	}

	/**
	 * �õ��������֤��(ĸ��)
	 * 
	 * @return String �������֤��(ĸ��)
	 */
	public String getMotherIdno() {
		return motherIdno.getValue();
	}

	/**
	 * �޸��������֤��(ĸ��)
	 * 
	 * @param motherIdno
	 *            String �������֤��(ĸ��)
	 */
	public void modifyMotherIdno(String motherIdno) {
		this.motherIdno.modifyValue(motherIdno);
	}

	/**
	 * �����ڽ�
	 * 
	 * @param peligionCode
	 *            String �ڽ�
	 */
	public void setPeligionCode(String peligionCode) {
		this.peligionCode.setValue(peligionCode);
	}

	/**
	 * �õ��ڽ�
	 * 
	 * @return String �ڽ�
	 */
	public String getPeligionCode() {
		return peligionCode.getValue();
	}

	/**
	 * �޸��ڽ�
	 * 
	 * @param peligionCode
	 *            String �ڽ�
	 */
	public void modifyPeligionCode(String peligionCode) {
		this.peligionCode.modifyValue(peligionCode);
	}

	/**
	 * ���ý����̶ȴ���
	 * 
	 * @param educationCode
	 *            String �����̶ȴ���
	 */
	public void setEducationCode(String educationCode) {
		this.educationCode.setValue(educationCode);
	}

	/**
	 * �õ������̶ȴ���
	 * 
	 * @return String �����̶ȴ���
	 */
	public String getEducationCode() {
		return educationCode.getValue();
	}

	/**
	 * �޸Ľ����̶ȴ���
	 * 
	 * @param educationCode
	 *            String �����̶ȴ���
	 */
	public void modifyEducationCode(String educationCode) {
		this.educationCode.modifyValue(educationCode);
	}

	/**
	 * ����ְҵ������
	 * 
	 * @param occCode
	 *            String ְҵ������
	 */
	public void setOccCode(String occCode) {
		this.occCode.setValue(occCode);
	}

	/**
	 * �õ�ְҵ������
	 * 
	 * @return String ְҵ������
	 */
	public String getOccCode() {
		return occCode.getValue();
	}

	/**
	 * �޸�ְҵ������
	 * 
	 * @param occCode
	 *            String ְҵ������
	 */
	public void modifyOccCode(String occCode) {
		this.occCode.modifyValue(occCode);
	}

	/**
	 * ���ù�������
	 * 
	 * @param nationCode
	 *            String ��������
	 */
	public void setNationCode(String nationCode) {
		this.nationCode.setValue(nationCode);
	}

	/**
	 * �õ���������
	 * 
	 * @return String ��������
	 */
	public String getNationCode() {
		return nationCode.getValue();
	}

	/**
	 * �޸Ĺ�������
	 * 
	 * @param nationCode
	 *            String ��������
	 */
	public void modifyNationCode(String nationCode) {
		this.nationCode.modifyValue(nationCode);
	}

	/**
	 * ��������
	 * 
	 * @param speciesCode
	 *            String ����
	 */
	public void setSpeciesCode(String speciesCode) {
		this.speciesCode.setValue(speciesCode);
	}

	/**
	 * �õ�����
	 * 
	 * @return String ����
	 */
	public String getSpeciesCode() {
		return speciesCode.getValue();
	}

	/**
	 * �޸�����
	 * 
	 * @param speciesCode
	 *            String ����
	 */
	public void modifySpeciesCode(String speciesCode) {
		this.speciesCode.modifyValue(speciesCode);
	}

	/**
	 * ���ó�������
	 * 
	 * @param firstAdmDate
	 *            Timestamp ��������
	 */
	public void setFirstAdmDate(Timestamp firstAdmDate) {
		this.firstAdmDate.setValue(firstAdmDate);
	}

	/**
	 * �õ���������
	 * 
	 * @return Timestamp ��������
	 */
	public Timestamp getFirstAdmDate() {
		return firstAdmDate.getValue();
	}

	/**
	 * �޸ĳ�������
	 * 
	 * @param firstAdmDate
	 *            Timestamp ��������
	 */
	public void modifyFirstAdmDate(Timestamp firstAdmDate) {
		this.firstAdmDate.modifyValue(firstAdmDate);
	}

	/**
	 * ���������������
	 * 
	 * @param rcntOpdDate
	 *            Timestamp �����������
	 */
	public void setRcntOpdDate(Timestamp rcntOpdDate) {
		this.rcntOpdDate.setValue(rcntOpdDate);
	}

	/**
	 * �õ������������
	 * 
	 * @return Timestamp �����������
	 */
	public Timestamp getRcntOpdDate() {
		return rcntOpdDate.getValue();
	}

	/**
	 * �޸������������
	 * 
	 * @param rcntOpdDate
	 *            Timestamp �����������
	 */
	public void modifyRcntOpdDate(Timestamp rcntOpdDate) {
		this.rcntOpdDate.modifyValue(rcntOpdDate);
	}

	/**
	 * �����������Ʊ�
	 * 
	 * @param rcntOpdDept
	 *            String �������Ʊ�
	 */
	public void setRcntOpdDept(String rcntOpdDept) {
		this.rcntOpdDept.setValue(rcntOpdDept);
	}

	/**
	 * �õ��������Ʊ�
	 * 
	 * @return String �������Ʊ�
	 */
	public String getRcntOpdDept() {
		return rcntOpdDept.getValue();
	}

	/**
	 * �޸��������Ʊ�
	 * 
	 * @param rcntOpdDept
	 *            String �������Ʊ�
	 */
	public void modifyRcntOpdDept(String rcntOpdDept) {
		this.rcntOpdDept.modifyValue(rcntOpdDept);
	}

	/**
	 * �������סԺ����
	 * 
	 * @param rcntIpdDate
	 *            Timestamp ���סԺ����
	 */
	public void setRcntIpdDate(Timestamp rcntIpdDate) {
		this.rcntIpdDate.setValue(rcntIpdDate);
	}

	/**
	 * �õ����סԺ����
	 * 
	 * @return Timestamp ���סԺ����
	 */
	public Timestamp getRcntIpdDate() {
		return rcntIpdDate.getValue();
	}

	/**
	 * �޸����סԺ����
	 * 
	 * @param rcntIpdDate
	 *            Timestamp ���סԺ����
	 */
	public void modifyRcntIpdDate(Timestamp rcntIpdDate) {
		this.rcntIpdDate.modifyValue(rcntIpdDate);
	}

	/**
	 * �������סԺ�Ʊ�
	 * 
	 * @param rcntIpdDept
	 *            String ���סԺ�Ʊ�
	 */
	public void setRcntIpdDept(String rcntIpdDept) {
		this.rcntIpdDept.setValue(rcntIpdDept);
	}

	/**
	 * �õ����סԺ�Ʊ�
	 * 
	 * @return String ���סԺ�Ʊ�
	 */
	public String getRcntIpdDept() {
		return rcntIpdDept.getValue();
	}

	/**
	 * �޸����סԺ�Ʊ�
	 * 
	 * @param rcntIpdDept
	 *            String ���סԺ�Ʊ�
	 */
	public void modifyRcntIpdDept(String rcntIpdDept) {
		this.rcntIpdDept.modifyValue(rcntIpdDept);
	}

	/**
	 * ���������������
	 * 
	 * @param rcntEmgDate
	 *            Timestamp �����������
	 */
	public void setRcntEmgDate(Timestamp rcntEmgDate) {
		this.rcntEmgDate.setValue(rcntEmgDate);
	}

	/**
	 * �õ������������
	 * 
	 * @return Timestamp �����������
	 */
	public Timestamp getRcntEmgDate() {
		return rcntEmgDate.getValue();
	}

	/**
	 * �޸������������
	 * 
	 * @param rcntEmgDate
	 *            Timestamp �����������
	 */
	public void modifyRcntEmgDate(Timestamp rcntEmgDate) {
		this.rcntEmgDate.modifyValue(rcntEmgDate);
	}

	/**
	 * �����������Ʊ�
	 * 
	 * @param rcntEmgDept
	 *            String �������Ʊ�
	 */
	public void setRcntEmgDept(String rcntEmgDept) {
		this.rcntEmgDept.setValue(rcntEmgDept);
	}

	/**
	 * �õ��������Ʊ�
	 * 
	 * @return String �������Ʊ�
	 */
	public String getRcntEmgDept() {
		return rcntEmgDept.getValue();
	}

	/**
	 * �޸��������Ʊ�
	 * 
	 * @param rcntEmgDept
	 *            String �������Ʊ�
	 */
	public void modifyRcntEmgDept(String rcntEmgDept) {
		this.rcntEmgDept.modifyValue(rcntEmgDept);
	}

	/**
	 * �������ˬԼ����
	 * 
	 * @param rcntMissDate
	 *            Timestamp ���ˬԼ����
	 */
	public void setRcntMissDate(Timestamp rcntMissDate) {
		this.rcntMissDate.setValue(rcntMissDate);
	}

	/**
	 * �õ����ˬԼ����
	 * 
	 * @return Timestamp ���ˬԼ����
	 */
	public Timestamp getRcntMissDate() {
		return rcntMissDate.getValue();
	}

	/**
	 * �޸����ˬԼ����
	 * 
	 * @param rcntMissDate
	 *            Timestamp ���ˬԼ����
	 */
	public void modifyRcntMissDate(Timestamp rcntMissDate) {
		this.rcntMissDate.modifyValue(rcntMissDate);
	}

	/**
	 * �������ˬԼ�Ʊ�
	 * 
	 * @param rcntMissDept
	 *            String ���ˬԼ�Ʊ�
	 */
	public void setRcntMissDept(String rcntMissDept) {
		this.rcntMissDept.setValue(rcntMissDept);
	}

	/**
	 * �õ����ˬԼ�Ʊ�
	 * 
	 * @return String ���ˬԼ�Ʊ�
	 */
	public String getRcntMissDept() {
		return rcntMissDept.getValue();
	}

	/**
	 * �޸����ˬԼ�Ʊ�
	 * 
	 * @param rcntMissDept
	 *            String ���ˬԼ�Ʊ�
	 */
	public void modifyRcntMissDept(String rcntMissDept) {
		this.rcntMissDept.modifyValue(rcntMissDept);
	}

	/**
	 * ��������׶���������
	 * 
	 * @param kidExamRcntDate
	 *            Timestamp ����׶���������
	 */
	public void setKidExamRcntDate(Timestamp kidExamRcntDate) {
		this.kidExamRcntDate.setValue(kidExamRcntDate);
	}

	/**
	 * �õ�����׶���������
	 * 
	 * @return Timestamp ����׶���������
	 */
	public Timestamp getKidExamRcntDate() {
		return kidExamRcntDate.getValue();
	}

	/**
	 * �޸�����׶���������
	 * 
	 * @param kidExamRcntDate
	 *            Timestamp ����׶���������
	 */
	public void modifyKidExamRcntDate(Timestamp kidExamRcntDate) {
		this.kidExamRcntDate.modifyValue(kidExamRcntDate);
	}

	/**
	 * ��������׶�ע������
	 * 
	 * @param kidInjRcntDate
	 *            Timestamp ����׶�ע������
	 */
	public void setKidInjRcntDate(Timestamp kidInjRcntDate) {
		this.kidInjRcntDate.setValue(kidInjRcntDate);
	}

	/**
	 * �õ�����׶�ע������
	 * 
	 * @return Timestamp ����׶�ע������
	 */
	public Timestamp getKidInjRcntDate() {
		return kidInjRcntDate.getValue();
	}

	/**
	 * �޸�����׶�ע������
	 * 
	 * @param kidInjRcntDate
	 *            Timestamp ����׶�ע������
	 */
	public void modifyKidInjRcntDate(Timestamp kidInjRcntDate) {
		this.kidInjRcntDate.modifyValue(kidInjRcntDate);
	}

	/**
	 * ����������˽�������
	 * 
	 * @param adultExamDate
	 *            Timestamp ������˽�������
	 */
	public void setAdultExamDate(Timestamp adultExamDate) {
		this.adultExamDate.setValue(adultExamDate);
	}

	/**
	 * �õ�������˽�������
	 * 
	 * @return Timestamp ������˽�������
	 */
	public Timestamp getAdultExamDate() {
		return adultExamDate.getValue();
	}

	/**
	 * �޸�������˽�������
	 * 
	 * @param adultExamDate
	 *            Timestamp ������˽�������
	 */
	public void modifyAdultExamDate(Timestamp adultExamDate) {
		this.adultExamDate.modifyValue(adultExamDate);
	}

	/**
	 * �������ĨƬ�������
	 * 
	 * @param smearRcntDate
	 *            Timestamp ���ĨƬ�������
	 */
	public void setSmearRcntDate(Timestamp smearRcntDate) {
		this.smearRcntDate.setValue(smearRcntDate);
	}

	/**
	 * �õ����ĨƬ�������
	 * 
	 * @return Timestamp ���ĨƬ�������
	 */
	public Timestamp getSmearRcntDate() {
		return smearRcntDate.getValue();
	}

	/**
	 * �޸����ĨƬ�������
	 * 
	 * @param smearRcntDate
	 *            Timestamp ���ĨƬ�������
	 */
	public void modifySmearRcntDate(Timestamp smearRcntDate) {
		this.smearRcntDate.modifyValue(smearRcntDate);
	}

	/**
	 * �������
	 * 
	 * @param height
	 *            double ���
	 */
	public void setHeight(double height) {
		this.height.setValue(height);
	}

	/**
	 * �õ����
	 * 
	 * @return double ���
	 */
	public double getHeight() {
		return height.getValue();
	}

	/**
	 * �޸����
	 * 
	 * @param height
	 *            double ���
	 */
	public void modifyHeight(double height) {
		this.height.modifyValue(height);
	}

	/**
	 * ��������
	 * 
	 * @param weight
	 *            double ����
	 */
	public void setWeight(double weight) {
		this.weight.setValue(weight);
	}

	/**
	 * �õ�����
	 * 
	 * @return double ����
	 */
	public double getWeight() {
		return weight.getValue();
	}

	/**
	 * �޸�����
	 * 
	 * @param weight
	 *            double ����
	 */
	public void modifyWeight(double weight) {
		this.weight.modifyValue(weight);
	}

	/**
	 * ���ñ�ע
	 * 
	 * @param description
	 *            String ��ע
	 */
	public void setDescription(String description) {
		this.description.setValue(description);
	}

	/**
	 * �õ���ע
	 * 
	 * @return String ��ע
	 */
	public String getDescription() {
		return description.getValue();
	}

	/**
	 * �޸ı�ע
	 * 
	 * @param description
	 *            String ��ע
	 */
	public void modifyDescription(String description) {
		this.description.modifyValue(description);
	}

	/**
	 * ���ñ�Ժ������
	 * 
	 * @param borninFlg
	 *            boolean ��Ժ������
	 */
	public void setBorninFlg(boolean borninFlg) {
		this.borninFlg.setValue(borninFlg);
	}

	/**
	 * �õ���Ժ������
	 * 
	 * @return boolean ��Ժ������
	 */
	public boolean getBorninFlg() {
		return borninFlg.getValue();
	}

	/**
	 * �޸ı�Ժ������
	 * 
	 * @param borninFlg
	 *            boolean ��Ժ������
	 */
	public void modifyBorninFlg(boolean borninFlg) {
		this.borninFlg.modifyValue(borninFlg);
	}

	/**
	 * ������������̥ע��
	 * 
	 * @param newbornSeq
	 *            int ��������̥ע��
	 */
	public void setNewbornSeq(int newbornSeq) {
		this.newbornSeq.setValue(newbornSeq);
	}

	/**
	 * �õ���������̥ע��
	 * 
	 * @return int ��������̥ע��
	 */
	public int getNewbornSeq() {
		return newbornSeq.getValue();
	}

	/**
	 * �޸���������̥ע��
	 * 
	 * @param newbornSeq
	 *            int ��������̥ע��
	 */
	public void modifyNewbornSeq(int newbornSeq) {
		this.newbornSeq.modifyValue(newbornSeq);
	}

	/**
	 * ���������
	 * 
	 * @param handicapFlg
	 *            boolean �����
	 */
	public void setPrematureFlg(boolean prematureFlg) {
		this.prematureFlg.setValue(prematureFlg);
	}

	/**
	 * �õ������
	 * 
	 * @return boolean �����
	 */
	public boolean getPrematureFlg() {
		return prematureFlg.getValue();
	}

	/**
	 * �޸������
	 * 
	 * @param prematureFlg
	 *            boolean �����
	 */
	public void modifyPrematureFlg(boolean prematureFlg) {
		this.prematureFlg.modifyValue(prematureFlg);
	}

	/**
	 * ���òм�
	 * 
	 * @param handicapFlg
	 *            boolean �м�
	 */
	public void setHandicapFlg(boolean handicapFlg) {
		this.handicapFlg.setValue(handicapFlg);
	}

	/**
	 * �õ��м�
	 * 
	 * @return boolean �м�
	 */
	public boolean getHandicapFlg() {
		return handicapFlg.getValue();
	}

	/**
	 * �޸Ĳм�
	 * 
	 * @param handicapFlg
	 *            boolean �м�
	 */
	public void modifyHandicapFlg(boolean handicapFlg) {
		this.handicapFlg.modifyValue(handicapFlg);
	}

	/**
	 * ���ú�����
	 * 
	 * @param blackFlg
	 *            boolean ������
	 */
	public void setBlackFlg(boolean blackFlg) {
		this.blackFlg.setValue(blackFlg);
	}

	/**
	 * �õ�������
	 * 
	 * @return boolean ������
	 */
	public boolean getBlackFlg() {
		return blackFlg.getValue();
	}

	/**
	 * �޸ĺ�����
	 * 
	 * @param blackFlg
	 *            boolean ������
	 */
	public void modifyBlackFlg(boolean blackFlg) {
		this.blackFlg.modifyValue(blackFlg);
	}

	/**
	 * ��������ע��
	 * 
	 * @param nameInvisibleFlg
	 *            boolean ����ע��
	 */
	public void setNameInvisibleFlg(boolean nameInvisibleFlg) {
		this.nameInvisibleFlg.setValue(nameInvisibleFlg);
	}

	/**
	 * �õ�����ע��
	 * 
	 * @return boolean ����ע��
	 */
	public boolean getNameInvisibleFlg() {
		return nameInvisibleFlg.getValue();
	}

	/**
	 * �޸�����ע��
	 * 
	 * @param nameInvisibleFlg
	 *            boolean ����ע��
	 */
	public void modifyNameInvisibleFlg(boolean nameInvisibleFlg) {
		this.nameInvisibleFlg.modifyValue(nameInvisibleFlg);
	}

	/**
	 * ���÷������ܱ�������
	 * 
	 * @param lawProtectFlg
	 *            boolean �������ܱ�������
	 */
	public void setLawProtectFlg(boolean lawProtectFlg) {
		this.lawProtectFlg.setValue(lawProtectFlg);
	}

	/**
	 * �õ��������ܱ�������
	 * 
	 * @return boolean �������ܱ�������
	 */
	public boolean getLawProtectFlg() {
		return lawProtectFlg.getValue();
	}

	/**
	 * �޸ķ������ܱ�������
	 * 
	 * @param lawProtectFlg
	 *            boolean �������ܱ�������
	 */
	public void modifyLawProtectFlg(boolean lawProtectFlg) {
		this.lawProtectFlg.modifyValue(lawProtectFlg);
	}

	/**
	 * ����LMP����
	 * 
	 * @param estideliveryDate
	 *            Timestamp LMP����
	 */
	public void setLMPDatee(Timestamp LMPDate) {
		this.LMPDate.setValue(LMPDate);
	}

	/**
	 * �õ�LMP����
	 * 
	 * @return Timestamp LMP����
	 */
	public Timestamp getLMPDate() {
		return LMPDate.getValue();
	}

	/**
	 * �޸�LMP����
	 * 
	 * @param estideliveryDate
	 *            Timestamp LMP����
	 */
	public void modifyLMPDate(Timestamp LMPDate) {
		this.LMPDate.modifyValue(LMPDate);
	}

	/**
	 * ����Ԥ����
	 * 
	 * @param pregnantDate
	 *            Timestamp Ԥ����
	 */
	public void setPregnantDate(Timestamp pregnantDate) {
		this.pregnantDate.setValue(pregnantDate);
	}

	/**
	 * �õ�Ԥ����
	 * 
	 * @return Timestamp Ԥ����
	 */
	public Timestamp getPregnantDate() {
		return pregnantDate.getValue();
	}

	/**
	 * �޸�Ԥ����
	 * 
	 * @param pregnantDate
	 *            Timestamp Ԥ����
	 */
	public void modifyPregnantDate(Timestamp pregnantDate) {
		this.pregnantDate.modifyValue(pregnantDate);
	}

	/**
	 * ���ò�����ʼ����
	 * 
	 * @param breastfeedStartDate
	 *            Timestamp ������ʼ����
	 */
	public void setBreastfeedStartDate(Timestamp breastfeedStartDate) {
		this.breastfeedStartDate.setValue(breastfeedStartDate);
	}

	/**
	 * �õ�������ʼ����
	 * 
	 * @return Timestamp ������ʼ����
	 */
	public Timestamp getBreastfeedStartDate() {
		return breastfeedStartDate.getValue();
	}

	/**
	 * �޸Ĳ�����ʼ����
	 * 
	 * @param breastfeedStartDate
	 *            Timestamp ������ʼ����
	 */
	public void modifyBreastfeedStartDate(Timestamp breastfeedStartDate) {
		this.breastfeedStartDate.modifyValue(breastfeedStartDate);
	}

	/**
	 * ���ò�����ֹ����
	 * 
	 * @param breastfeedEndDate
	 *            Timestamp ������ֹ����
	 */
	public void setBreastfeedEndDate(Timestamp breastfeedEndDate) {
		this.breastfeedEndDate.setValue(breastfeedEndDate);
	}

	/**
	 * �õ�������ֹ����
	 * 
	 * @return Timestamp ������ֹ����
	 */
	public Timestamp getBreastfeedEndDate() {
		return breastfeedEndDate.getValue();
	}

	/**
	 * �޸Ĳ�����ֹ����
	 * 
	 * @param breastfeedEndDate
	 *            Timestamp ������ֹ����
	 */
	public void modifyBreastfeedEndDate(Timestamp breastfeedEndDate) {
		this.breastfeedEndDate.modifyValue(breastfeedEndDate);
	}

	/**
	 * ���ò�����״̬һ
	 * 
	 * @param pat1Code
	 *            String ������״̬һ
	 */
	public void setPat1Code(String pat1Code) {
		this.pat1Code.setValue(pat1Code);
	}

	/**
	 * �õ�������״̬һ
	 * 
	 * @return String ������״̬һ
	 */
	public String getPat1Code() {
		return pat1Code.getValue();
	}

	/**
	 * �޸Ĳ�����״̬һ
	 * 
	 * @param pat1Code
	 *            String ������״̬һ
	 */
	public void modifyPat1Code(String pat1Code) {
		this.pat1Code.modifyValue(pat1Code);
	}

	/**
	 * ���ò�����״̬��
	 * 
	 * @param pat2Code
	 *            String ������״̬��
	 */
	public void setPat2Code(String pat2Code) {
		this.pat2Code.setValue(pat2Code);
	}

	/**
	 * �õ�������״̬��
	 * 
	 * @return String ������״̬��
	 */
	public String getPat2Code() {
		return pat2Code.getValue();
	}

	/**
	 * �޸Ĳ�����״̬��
	 * 
	 * @param pat2Code
	 *            String ������״̬��
	 */
	public void modifyPat2Code(String pat2Code) {
		this.pat2Code.modifyValue(pat2Code);
	}

	/**
	 * ���ò�����״̬��
	 * 
	 * @param pat3Code
	 *            String ������״̬��
	 */
	public void setPat3Code(String pat3Code) {
		this.pat3Code.setValue(pat3Code);
	}

	/**
	 * �õ�������״̬��
	 * 
	 * @return String ������״̬��
	 */
	public String getPat3Code() {
		return pat3Code.getValue();
	}

	/**
	 * �޸Ĳ�����״̬��
	 * 
	 * @param pat3Code
	 *            String ������״̬��
	 */
	public void modifyPat3Code(String pat3Code) {
		this.pat3Code.modifyValue(pat3Code);
	}

	/**
	 * ������
	 * 
	 * @param homePlaceCode
	 *            String ������
	 */
	public void sethomePlaceCode(String homePlaceCode) {
		this.homePlaceCode.setValue(homePlaceCode);
	}

	/**
	 * ������
	 * 
	 * @return String ������
	 */
	public String gethomePlaceCode() {
		return homePlaceCode.getValue();
	}

	/**
	 * ������
	 * 
	 * @param homePlaceCode
	 *            String ������
	 */
	public void modifyhomePlaceCode(String homePlaceCode) {
		this.homePlaceCode.modifyValue(homePlaceCode);
	}

	/**
	 * ����
	 * 
	 * @return the brithPlace
	 */
	public String getBirthPlace() {
		return birthPlace.getValue();
	}

	/**
	 * ����
	 * 
	 * @param brithPlace
	 *            the brithPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace.setValue(birthPlace);
	}

	/**
	 * ����
	 * 
	 * @param brithPlace
	 *            String ����
	 */
	public void modifyBirthPlace(String birthPlace) {
		this.birthPlace.modifyValue(birthPlace);
	}

	/**
	 * ��λ��ַ
	 * 
	 * @return the companyAddress
	 */
	public String getCompanyAddress() {
		return companyAddress.getValue();
	}

	/**
	 * ��λ��ַ
	 * 
	 * @param companyAddress
	 *            the companyAddress to set
	 */
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress.setValue(companyAddress);
	}

	/**
	 * �޸ĵ�λ��ַ
	 * 
	 * @param companyAddress
	 */
	public void modifyCompanyAddress(String companyAddress) {
		this.companyAddress.modifyValue(companyAddress);
	}

	/**
	 * ��λ�ʱ�
	 * 
	 * @return the companyPost
	 */
	public String getCompanyPost() {
		return companyPost.getValue();
	}

	/**
	 * ��λ�ʱ�
	 * 
	 * @param companyPost
	 *            the companyPost to set
	 */
	public void setCompanyPost(String companyPost) {
		this.companyPost.setValue(companyPost);
	}

	/**
	 * �޸ĵ�λ�ʱ�
	 */
	public void modifyCompanyPost(String companyPost) {
		this.companyPost.modifyValue(companyPost);
	}
	/**
	 * �õ���������
	 * @return the nhicardNo
	 */
	public String getNhicardNo() {
		return nhicardNo.getValue();
	}

	/**
	 * ������������
	 * @param nhicardNo the nhicardNo to set
	 */
	public void setNhicardNo(String nhicardNo) {
		this.nhicardNo.setValue(nhicardNo);
	}
	/**
	 * �޸Ľ�������
	 */
	public void modifyNhicardNo(String nhicardNo) {
		this.nhicardNo.modifyValue(nhicardNo);
	}
	/**
	 * �õ�����
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
	 * ��������
	 * 
	 * @param program
	 *            String ������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean lockPat(String program) {
		if (getMrNo() == null || getMrNo().length() == 0)
			return false;
		return PatTool.getInstance().lockPat(getMrNo(), program);
	}

	/**
	 * ���ݸ�������֤�ź�����������ǰ���֤�Ż�������ͬ�Ĳ���
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
			result.setErrText("��������");
			return result;
		}
		if (!StringTool.isId(idNo)) {
			result.setErrCode(-1);
			result.setErrText("������Ч���֤��");
			return result;
		}
		TJDODBTool s;
		String sql = this.IS_SAME_PAT_SQL.replace("#", idNo).replace("#",
				patName);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * �����Ƿ��Ѿ�����
	 * 
	 * @return boolean true ���� false û������
	 */
	public boolean isLockPat() {
		if (getMrNo() == null || getMrNo().length() == 0)
			return false;
		return PatTool.getInstance().isLockPat(getMrNo());
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @return String
	 */
	public String getLockParmString() {
		if (getMrNo() == null || getMrNo().length() == 0)
			return "";
		return PatTool.getInstance().getLockParmString(getMrNo());
	}

	/**
	 * ��������
	 * 
	 * @return boolean true �ɹ� false ʧ��
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
		// pat.lockPat("ҽ������վ");
		// pat.unLockPat();
		// pat.onNew();
		pat.onSave();

		// System.out.println(pat);
	}
	
	/**
	 * �޸Ĳ��˹���
	 */
	public void modifyPatBelong(String patBelong) {
		this.patBelong.modifyValue(patBelong);
	}
	
	/**
	 * �õ����˹���
	 * @return the nhicardNo
	 */
	public String getPatBelong() {
		return patBelong.getValue();
	}
	
	
	
	/**
	 * ���ò����ܼ�
	 * 
	 * @param securityCategory
	 *            String
	 */
	public void setSecurityCategory(String securityCategory) {
		this.securityCategory.setValue(securityCategory);
	}

	/**
	 * �õ������ܼ�
	 * 
	 * @return String
	 */
	public String getSecurityCategory() {
		return securityCategory.getValue();
	}

	/**
	 * �޸Ĳ����ܼ�
	 * 
	 * @param securityCategory
	 *            String
	 */
	public void modifySecurityCategory(String securityCategory) {
		this.securityCategory.modifyValue(securityCategory);
	}

    /**
     * ��������������
     * 
     * @param newBodyWeight
     *            double ����������
     */
    public void setNewBodyWeight(double newBodyWeight) {
        this.newBodyWeight.setValue(newBodyWeight);
    }

    /**
     * �õ�����������
     * 
     * @return double ����������
     */
    public double getNewBodyWeight() {
        return newBodyWeight.getValue();
    }

    /**
     * �޸�����������
     * 
     * @param newBodyWeight
     *            double ����������
     */
    public void modifyNewBodyWeight(double newBodyWeight) {
        this.newBodyWeight.modifyValue(newBodyWeight);
    }
    
    /**
     * ������������
     * 
     * @param newBodyWeight
     *            double ��������
     * add by yangjj 20150312
     */
    public void setNewBodyHeight(double newBodyHeight) {
        this.newBodyHeight.setValue(newBodyHeight);
    }

    /**
     * �õ���������
     * 
     * @return double ��������
     * add by yangjj 20150312
     */
    public double getNewBodyHeight() {
        return newBodyHeight.getValue();
    }

    /**
     * �޸���������
     * 
     * @param newBodyWeight
     *            double ��������
     * add by yangjj 20150312
     */
    public void modifyNewBodyHeight(double newBodyHeight) {
        this.newBodyHeight.modifyValue(newBodyHeight);
    }

	
	
	
	
	
}
