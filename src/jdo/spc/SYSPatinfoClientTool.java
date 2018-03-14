package jdo.spc;

import jdo.spc.spcPatInfoSyncClient.SysPatinfo;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class SYSPatinfoClientTool {

	private String mrNo;
	private String ipdNo;
	private String deleteFlg;
	private String mergeFlg;
	private String motherMrno;
	private String patName;
	private String patName1;
	private String py1;
	private String py2;
	private String foreignerFlg;
	private String idno;
	private String birthDate;
	private String ctz1Code;
	private String ctz2Code;
	private String ctz3Code;
	private String telCompany;
	private String telHome;
	private String cellPhone;
	private String companyDesc;
	private String EMail;
	private String bloodType;
	private String bloodRhType;
	private String sexCode;
	private String marriageCode;
	private String postCode;
	private String address;
	private String residPostCode;
	private String residAddress;
	private String contactsName;
	private String relationCode;
	private String contactsTel;
	private String contactsAddress;
	private String spouseIdno;
	private String fatherIdno;
	private String motherIdno;
	private String religionCode;
	private String educationCode;
	private String occCode;
	private String nationCode;
	private String speciesCode;
	private String firstAdmDate;
	private String rcntOpdDate;
	private String rcntOpdDept;
	private String rcntIpdDate;
	private String rcntIpdDept;
	private String rcntEmgDate;
	private String rcntEmgDept;
	private String rcntMissDate;
	private String rcntMissDept;
	private String kidExamRcntDate;
	private String kidInjRcntDate;
	private String adultExamDate;
	private String smearRcntDate;
	private String deadDate;
	private Double height;
	private Double weight;
	private String description;
	private String borninFlg;
	private Integer newbornSeq;
	private String prematureFlg;
	private String handicapFlg;
	private String blackFlg;
	private String nameInvisibleFlg;
	private String lawProtectFlg;
	private String lmpDate;
	private String pregnantDate;
	private String breastfeedStartdate;
	private String breastfeedEnddate;
	private String pat1Code;
	private String pat2Code;
	private String pat3Code;
	private String mergeTomrno;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String homeplaceCode;
	private String familyHistory;
	private String handleFlg;
	private String nhiNo;
	private String addressCompany;
	private String postCompany;
	private String birthplace;
	private String ccbPersonNo;
	private String nhiCardNo;
	private SysPatinfo sysPatinfo;

	public SysPatinfo getSysPatinfo() {
		return sysPatinfo;
	}

	public void setSysPatinfo(SysPatinfo sysPatinfo) {
		this.sysPatinfo = sysPatinfo;
	}

	public SYSPatinfoClientTool(String mrNo) {
		String sql = " SELECT MR_NO, IPD_NO, DELETE_FLG, MERGE_FLG, MOTHER_MRNO, PAT_NAME, PAT_NAME1,"
				+ " PY1, PY2, FOREIGNER_FLG, IDNO, BIRTH_DATE, CTZ1_CODE, CTZ2_CODE,"
				+ " CTZ3_CODE, TEL_COMPANY, TEL_HOME, CELL_PHONE, COMPANY_DESC, E_MAIL,"
				+ " BLOOD_TYPE, BLOOD_RH_TYPE, SEX_CODE, MARRIAGE_CODE, POST_CODE, ADDRESS,"
				+ " RESID_POST_CODE, RESID_ADDRESS, CONTACTS_NAME, RELATION_CODE,"
				+ " CONTACTS_TEL, CONTACTS_ADDRESS, SPOUSE_IDNO, FATHER_IDNO, MOTHER_IDNO,"
				+ " RELIGION_CODE, EDUCATION_CODE, OCC_CODE, NATION_CODE, SPECIES_CODE,"
				+ " FIRST_ADM_DATE, RCNT_OPD_DATE, RCNT_OPD_DEPT, RCNT_IPD_DATE,"
				+ " RCNT_IPD_DEPT, RCNT_EMG_DATE, RCNT_EMG_DEPT, RCNT_MISS_DATE,"
				+ " RCNT_MISS_DEPT, KID_EXAM_RCNT_DATE, KID_INJ_RCNT_DATE, ADULT_EXAM_DATE,"
				+ " SMEAR_RCNT_DATE, DEAD_DATE, HEIGHT, WEIGHT, DESCRIPTION, BORNIN_FLG,"
				+ " NEWBORN_SEQ, PREMATURE_FLG, HANDICAP_FLG, BLACK_FLG,"
				+ " NAME_INVISIBLE_FLG, LAW_PROTECT_FLG, LMP_DATE, PREGNANT_DATE,"
				+ " BREASTFEED_STARTDATE, BREASTFEED_ENDDATE, PAT1_CODE, PAT2_CODE,"
				+ " PAT3_CODE, MERGE_TOMRNO, OPT_USER, OPT_DATE, OPT_TERM, HOMEPLACE_CODE,"
				+ " FAMILY_HISTORY, HANDLE_FLG, NHI_NO, ADDRESS_COMPANY, POST_COMPANY,"
				+ " BIRTHPLACE, CCB_PERSON_NO, NHICARD_NO"
				+ " FROM SYS_PATINFO" + " WHERE MR_NO = '" + mrNo + "'";
		System.out.println("sql===="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() > 0) {
			sysPatinfo = setPatinfo(result.getRow(0));
			System.out.println("sysPatinfo===="+sysPatinfo);
		}

	}

	private SysPatinfo setPatinfo(TParm parm) {
		mrNo = parm.getValue("MR_NO");
		ipdNo = parm.getValue("IPD_NO");
		deleteFlg = parm.getValue("DELETE_FLG");
		mergeFlg = parm.getValue("MERGE_FLG");
		motherMrno = parm.getValue("MOTHER_MRNO");
		patName = parm.getValue("PAT_NAME");
		patName1 = parm.getValue("PAT_NAME1");
		py1 = parm.getValue("PY1");
		py2 = parm.getValue("PY2");
		foreignerFlg = parm.getValue("FOREIGNER_FLG");
		idno = parm.getValue("IDNO");
		birthDate = subDate(parm.getValue("BIRTH_DATE"));
		ctz1Code = parm.getValue("CTZ1_CODE");
		ctz2Code = parm.getValue("CTZ2_CODE");
		System.out.println("ctz2Code=tool="+ctz2Code);
		ctz3Code = parm.getValue("CTZ3_CODE");
		System.out.println("ctz3Code=tool="+ctz3Code);
		telCompany = parm.getValue("TEL_COMPANY");
		telHome = parm.getValue("TEL_HOME");
		cellPhone = parm.getValue("CELL_PHONE");
		companyDesc = parm.getValue("COMPANY_DESC");
		EMail = parm.getValue("E_MAIL");
		bloodType = parm.getValue("BLOOD_TYPE");
		bloodRhType = parm.getValue("BLOOD_RH_TYPE");
		sexCode = parm.getValue("SEX_CODE");
		marriageCode = parm.getValue("MARRIAGE_CODE");
		postCode = parm.getValue("POST_CODE");
		address = parm.getValue("ADDRESS");
		residPostCode = parm.getValue("RESID_POST_CODE");
		residAddress = parm.getValue("RESID_ADDRESS");
		contactsName = parm.getValue("CONTACTS_NAME");
		relationCode = parm.getValue("RELATION_CODE");
		contactsTel = parm.getValue("CONTACTS_TEL");
		contactsAddress = parm.getValue("CONTACTS_ADDRESS");
		spouseIdno = parm.getValue("SPOUSE_IDNO");
		fatherIdno = parm.getValue("FATHER_IDNO");
		motherIdno = parm.getValue("MOTHER_IDNO");
		religionCode = parm.getValue("RELIGION_CODE");
		educationCode = parm.getValue("EDUCATION_CODE");
		occCode = parm.getValue("OCC_CODE");
		nationCode = parm.getValue("NATION_CODE");
		speciesCode = parm.getValue("SPECIES_CODE");
		firstAdmDate = subDate(parm.getValue("FIRST_ADM_DATE"));
		rcntOpdDate = subDate(parm.getValue("RCNT_OPD_DATE"));
		rcntOpdDept = parm.getValue("RCNT_OPD_DEPT");
		rcntIpdDate = subDate(parm.getValue("RCNT_IPD_DATE"));
		rcntIpdDept = parm.getValue("RCNT_IPD_DEPT");
		rcntEmgDate = subDate(parm.getValue("RCNT_EMG_DATE"));
		rcntEmgDept = parm.getValue("RCNT_EMG_DEPT");
		rcntMissDate = subDate(parm.getValue("RCNT_MISS_DATE"));
		rcntMissDept = parm.getValue("RCNT_MISS_DEPT");
		kidExamRcntDate = subDate(parm.getValue("KID_EXAM_RCNT_DATE"));
		kidInjRcntDate = subDate(parm.getValue("KID_INJ_RCNT_DATE"));
		adultExamDate = subDate(parm.getValue("ADULT_EXAM_DATE"));
		smearRcntDate = subDate(parm.getValue("SMEAR_RCNT_DATE"));
		deadDate = subDate(parm.getValue("DEAD_DATE"));
		height = parm.getDouble("HEIGHT");
		weight = parm.getDouble("WEIGHT");
		description = parm.getValue("DESCRIPTION");
		borninFlg = parm.getValue("BORNIN_FLG");
		newbornSeq = parm.getInt("NEWBORN_SEQ");
		prematureFlg = parm.getValue("PREMATURE_FLG");
		handicapFlg = parm.getValue("HANDICAP_FLG");
		blackFlg = parm.getValue("BLACK_FLG");
		nameInvisibleFlg = parm.getValue("NAME_INVISIBLE_FLG");
		lawProtectFlg = parm.getValue("LAW_PROTECT_FLG");
		lmpDate = subDate(parm.getValue("LMP_DATE"));
		pregnantDate = subDate(parm.getValue("PREGNANT_DATE"));
		breastfeedStartdate = subDate(parm.getValue("BREASTFEED_STARTDATE"));
		breastfeedEnddate = subDate(parm.getValue("BREASTFEED_ENDDATE"));
		pat1Code = parm.getValue("PAT1_CODE");
		pat2Code = parm.getValue("PAT2_CODE");
		pat3Code = parm.getValue("PAT3_CODE");
		mergeTomrno = parm.getValue("MERGE_TOMRNO");
		optUser = parm.getValue("OPT_USER");
		optDate = subDate(parm.getValue("OPT_DATE"));
		optTerm = parm.getValue("OPT_TERM");
		homeplaceCode = parm.getValue("HOMEPLACE_CODE");
		familyHistory = parm.getValue("FAMILY_HISTORY");
		handleFlg = parm.getValue("HANDLE_FLG");
		nhiNo = parm.getValue("NHI_NO");
		System.out.println("nhiNo==tool==="+nhiNo);
		addressCompany = parm.getValue("ADDRESS_COMPANY");
		postCompany = parm.getValue("POST_COMPANY");
		birthplace = parm.getValue("BIRTHPLACE");
		ccbPersonNo = parm.getValue("CCB_PERSON_NO");
		nhiCardNo = parm.getValue("NHICARD_NO");
		SysPatinfo sysPatinfo = new SysPatinfo(mrNo, ipdNo, deleteFlg,
				mergeFlg, motherMrno, patName, patName1, py1, py2,
				foreignerFlg, idno, birthDate, ctz1Code, ctz2Code, ctz3Code,
				telCompany, telHome, cellPhone, companyDesc, EMail, bloodType,
				bloodRhType, sexCode, marriageCode, postCode, address,
				residPostCode, residAddress, contactsName, relationCode,
				contactsTel, contactsAddress, spouseIdno, fatherIdno,
				motherIdno, religionCode, educationCode, occCode, nationCode,
				speciesCode, firstAdmDate, rcntOpdDate, rcntOpdDept,
				rcntIpdDate, rcntIpdDept, rcntEmgDate, rcntEmgDept,
				rcntMissDate, rcntMissDept, kidExamRcntDate, kidInjRcntDate,
				adultExamDate, smearRcntDate, deadDate, height, weight,
				description, borninFlg, newbornSeq, prematureFlg, handicapFlg,
				blackFlg, nameInvisibleFlg, lawProtectFlg, lmpDate,
				pregnantDate, breastfeedStartdate, breastfeedEnddate, pat1Code,
				pat2Code, pat3Code, mergeTomrno, optUser, optDate, optTerm,
				homeplaceCode, familyHistory, handleFlg, nhiNo, addressCompany,
				postCompany, birthplace, ccbPersonNo, nhiCardNo);
		return sysPatinfo;

	}

	private String subDate(String str) {
		if (str.length() >= 19) {
			str = str.substring(0, 19);
		}
		return str;
	}

}
