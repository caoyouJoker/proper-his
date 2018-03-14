package jdo.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jdo.device.CallNo;
import jdo.erd.ErdForBedAndRecordTool;
import jdo.sys.Operator;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.ui.testOpb.tools.AssembleTool;


/**
 * 
 * <p>
 * Title: ����ҽ��վ����
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
 * @author lzk 2009.2.11
 * @version 1.0
 */
public class ODO {
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ҽʦ
	 */
	private String drCode;
	/**
	 * �������
	 */
	private String icdType;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ���߿���
	 */
	private Subjrec subjrec;
	/**
	 * ���
	 */
	private Diagrec diagrec;
	/**
	 * ����ʷ
	 */
	private DrugAllergy drugAllergy;
	/**
	 * ҽ��
	 */
	private OpdOrder opdOrder;
	/**
	 * ����ʷ
	 */
	private MedHistory medHistory;
	/**
	 * �Һ�
	 */
	private RegPatAdm regPatAdm;
	/**
	 * ����
	 */
	private PatInfo patInfo;
	/**
	 * ҽ����ʷ
	 */
	private OpdOrderHistory opdOrderHistory;

	/**
	 * ������Ϣ
	 */
	private String errText = "";
	/**
	 * ���ݿ⹤��
	 */
	private TJDODBTool dbTool = new TJDODBTool();
	/**
	 * ҽ�ƿ����׺�
	 */
	private String TREDE_NO = "";

	/**
	 * ҽ�ƿ���ʷ���׺�
	 */
	private String HISTORY_NO = "";

	private String[] ektSql;


	public String getHistoryNo() {
		return HISTORY_NO;
	}

	public void setHistoryNo(String hISTORYNO) {
		HISTORY_NO = hISTORYNO;
	}

	public String[] getEktSql() {
		return ektSql;
	}

	public void setEktSql(String[] ektSql) {
		this.ektSql = ektSql;
	}

	/**
	 * ������
	 */
	public ODO() {
		setOdo();
	}

	/**
	 * 
	 * @param caseNo
	 * @param mrNo
	 * @param deptCode
	 * @param drCode
	 * @param admType
	 */
	public ODO(String caseNo, String mrNo, String deptCode, String drCode,
			String admType) {
		setOdo();
		setCaseNo(caseNo);
		setMrNo(mrNo);
		setDeptCode(deptCode);
		setDrCode(drCode);
		setAdmType(admType);
	}

	public void setOdo() {
		// ��ʼ�����߿���
		setSubjrec(new Subjrec());
		// ��ʼ�����
		setDiagrec(new Diagrec());
		// ����ʷ
		setDrugAllergy(new DrugAllergy());
		// ҽ��
		setOpdOrder(new OpdOrder());
		// ����ʷ
		setMedHistory(new MedHistory());
		// �Һ�
		setRegPatAdm(new RegPatAdm());
		// ����
		setPatInfo(new PatInfo());
		// ҽ����ʷ
		setOpdOrderHistory(new OpdOrderHistory());
	}

	/**
	 * �����ż�ס��
	 * 
	 * @param admType
	 *            String
	 */
	public void setAdmType(String admType) {
		this.admType = admType;
		getSubjrec().setAdmType(admType);
		getDiagrec().setAdmType(admType);
		getDrugAllergy().setAdmType(admType);
		getOpdOrder().setAdmType(admType);
		// getOpdOrderHistory().setAdmType(admType);
		getMedHistory().setAdmType(admType);
	}

	/**
	 * �õ��ż�ס��
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * �õ��������
	 * 
	 * @return String
	 */
	public String getIcdType() {
		return icdType;
	}

	public void setIcdType(String icdType) {
		this.icdType = icdType;
		getDiagrec().setIcdType(icdType);
	}

	/**
	 * ����ҽʦ
	 * 
	 * @param drCode
	 *            String
	 */
	public void setDrCode(String drCode) {
		this.drCode = drCode;
		getSubjrec().setDrCode(drCode);
		getDiagrec().setDrCode(drCode);
		getDrugAllergy().setDrCode(drCode);
		getOpdOrder().setDrCode(drCode);
		// getOpdOrderHistory().setDrCode(drCode);
		getMedHistory().setDrCode(drCode);
	}

	/**
	 * �õ�ҽʦ
	 * 
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	/**
	 * �õ�������
	 * yanjing 20131206
	 * @return String
	 */
	public String getMrNo() {
		return this.mrNo;
	}


	/**
	 * �õ������
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}
	/**
	 * ���ò�����
	 * 
	 * @param mrNo
	 *            String
	 * yanjing 20131206           
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
		//		System.out.println("iiiiiiiiiiiii mrNo is ::"+mrNo);
		getSubjrec().setMrNo(mrNo);
		getDrugAllergy().setMrNo(mrNo);
		getOpdOrder().setMrNo(mrNo);
		//		 getOpdOrderHistory().setMrNo(mrNo);
		getMedHistory().setMrNo(mrNo);
		getPatInfo().setMrNo(mrNo);
	}
	/**
	 * ���þ����
	 * 
	 * @param caseNo
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
		getSubjrec().setCaseNo(caseNo);
		getDiagrec().setCaseNo(caseNo);
		getDrugAllergy().setCaseNo(caseNo);
		getOpdOrder().setCaseNo(caseNo);
		// getOpdOrderHistory().setCaseNo(caseNo);
		getMedHistory().setCaseNo(caseNo);
		getRegPatAdm().setCaseNo(caseNo);
	}

	/**
	 * ���ò���
	 * 
	 * @param deptCode
	 *            String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
		getDrugAllergy().setDeptCode(deptCode);
		getOpdOrder().setDeptCode(deptCode);
		// getOpdOrderHistory().setDeptCode(deptCode);
		getMedHistory().setDeptCode(deptCode);
	}

	/**
	 * �õ�����
	 * 
	 * @return String
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * �������߿���
	 * 
	 * @param subjrec
	 *            Subjrec
	 */
	public void setSubjrec(Subjrec subjrec) {
		this.subjrec = subjrec;
	}

	/**
	 * �õ����߿���
	 * 
	 * @return Subjrec
	 */
	public Subjrec getSubjrec() {
		return subjrec;
	}

	/**
	 * ������϶���
	 * 
	 * @param diagrec
	 *            Diagrec
	 */
	public void setDiagrec(Diagrec diagrec) {
		this.diagrec = diagrec;
	}

	/**
	 * �õ���϶���
	 * 
	 * @return Diagrec
	 */
	public Diagrec getDiagrec() {
		return diagrec;
	}

	/**
	 * ���ù���ʷ����
	 * 
	 * @param drugAllergy
	 *            DrugAllergy
	 */
	public void setDrugAllergy(DrugAllergy drugAllergy) {
		this.drugAllergy = drugAllergy;
	}

	/**
	 * �õ�����ʷ����
	 * 
	 * @return DrugAllergy
	 */
	public DrugAllergy getDrugAllergy() {
		return drugAllergy;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param opdOrder
	 *            OpdOrder
	 */
	public void setOpdOrder(OpdOrder opdOrder) {
		this.opdOrder = opdOrder;
	}

	/**
	 * �õ�ҽ������
	 * 
	 * @return OpdOrder
	 */
	public OpdOrder getOpdOrder() {
		return opdOrder;
	}

	/**
	 * ���ü���ʷ����
	 * 
	 * @param medHistory
	 *            MedHistory
	 */
	public void setMedHistory(MedHistory medHistory) {
		this.medHistory = medHistory;
	}

	/**
	 * �õ�����ʷ����
	 * 
	 * @return MedHistory
	 */
	public MedHistory getMedHistory() {
		return medHistory;
	}

	/**
	 * ���ùҺŶ���
	 * 
	 * @param regPatAdm
	 *            RegPatAdm
	 */
	public void setRegPatAdm(RegPatAdm regPatAdm) {
		this.regPatAdm = regPatAdm;
	}

	/**
	 * �õ��ҺŶ���
	 * 
	 * @return RegPatAdm
	 */
	public RegPatAdm getRegPatAdm() {
		return this.regPatAdm;
	}

	/**
	 * ���ò�������
	 * 
	 * @param patInfo
	 *            PatInfo
	 */
	public void setPatInfo(PatInfo patInfo) {
		this.patInfo = patInfo;
	}

	/**
	 * �õ���������
	 * 
	 * @return PatInfo
	 */
	public PatInfo getPatInfo() {
		return this.patInfo;
	}

	/**
	 * ����ҽ����ʷ����
	 * 
	 * @param opdOrderHistory
	 *            OpdOrderHistory
	 */
	public void setOpdOrderHistory(OpdOrderHistory opdOrderHistory) {
		this.opdOrderHistory = opdOrderHistory;
	}

	/**
	 * �õ�ҽ����ʷ����
	 * 
	 * @return OpdOrderHistory
	 */
	public OpdOrderHistory getOpdOrderHistory() {
		return this.opdOrderHistory;
	}

	/**
	 * �õ����ݿ⹤��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return dbTool;
	}

	/**
	 * ���ô�����Ϣ
	 * 
	 * @param errText
	 *            String
	 */
	public void setErrText(String errText) {
		this.errText = errText;
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @return String
	 */
	public String getErrText() {
		return errText;
	}

	/**
	 * ����ҽ�ƿ����׺�
	 * 
	 * @param TredeNo
	 *            String
	 */
	public void setTredeNo(String TredeNo) {
		this.TREDE_NO = TredeNo;
	}

	/**
	 * �õ�ҽ�ƿ����׺�
	 */
	public String getTredeNo() {
		return TREDE_NO;
	}

	/**
	 * ��ѯ
	 * 
	 * @return boolean
	 */
	public boolean onQuery() {
		// �������ô�����Ϣ
		setErrText("");

		// ���CASE_NO
		if (getCaseNo() == null || getCaseNo().length() <= 0) {
			setErrText("CaseNoΪ�գ�");
			return false;
		}
		// ��ѯ���߿�������
		if (!getSubjrec().onQuery()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		if (getSubjrec().rowCount() < 1)
			getSubjrec().insertRow();
		// ��ѯ�������
		if (!getDiagrec().onQuery()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		getDiagrec().insertRow();
		// ��ѯ����ʷ����
		if (!getDrugAllergy().onQuery()) {
			setErrText(getDrugAllergy().getErrText());
			return false;
		}
		int row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "A");
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "B");
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "C");
		//add by huangtt 20150505 start 
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "D");
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "E");
		row = getDrugAllergy().insertRow();
		getDrugAllergy().setItem(row, "DRUG_TYPE", "N");
		//add by huangtt 20150505 end
		// ��ѯҽ������
		if (!getOpdOrder().onQuery()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}
		// ��ѯ����ʷ����
		if (!getMedHistory().onQuery()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		getMedHistory().insertRow();
		// ��ѯ�Һ�����
		if (!getRegPatAdm().onQuery()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}

		// ��ѯ��������
		if (!getPatInfo().onQuery()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		getOpdOrder().getMedApply();// ��ʼ��med_apply
		getOpdOrder().getLabMap();
		// //��ѯ��������
		// if(!getPatInfo().onQuery()){
		// setErrText(getPatInfo().getErrText());
		// return false;
		// }
		return true;
	}

	/**
	 * ������
	 * 
	 * @return boolean
	 */
	public boolean checkSave() {
		// ������߿���
		if (!getSubjrec().checkSave()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		// ������
		if (!getDiagrec().checkSave()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		// ������ʷ
		if (!getDrugAllergy().checkSave()) {
			setErrText(getDrugAllergy().getErrText());
			return false;
		}
		// ���ҽ��
		if (!getOpdOrder().checkSave()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}
		// ������ʷ
		if (!getMedHistory().checkSave()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		// ���Һ�
		if (!getRegPatAdm().checkSave()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		// ��ⲡ��
		if (!getPatInfo().checkSave()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		Timestamp optDate = getDBTool().getDBTime();
		// �������߿����޸��е��û���Ϣ
		getSubjrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// ��������޸��е��û���Ϣ
		getDiagrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// ���ù���ʷ�޸��е��û���Ϣ
		getDrugAllergy().setOperator(Operator.getID(), optDate,
				Operator.getIP());
		// ����ҽ���޸��е��û���Ϣ
		getOpdOrder().setOperator(Operator.getID(), optDate, Operator.getIP());
		// ���ü���ʷ�޸��е��û���Ϣ
		getMedHistory()
		.setOperator(Operator.getID(), optDate, Operator.getIP());
		return true;
	}

	/**
	 * ����ҽ��
	 * 
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean onSave() throws Exception  {
		// ������
		if (!checkSave())
			return false;

		// �������߿���
		String sql[] = getSubjrec().getUpdateSQL();

		//		for(int i = 0;i<sql.length;i++){
		//			subjrec.setItem(i, "MR_NO", mrNo);
		//		System.out.println(i+"���߿��� is :::"+sql[i]);
		//		}
		// ���SQL�Ƿ�Ϊ��
		if (sql == null) {
			// System.out.println("subjrec is wrong");
			setErrText(getSubjrec().getErrText());
			return false;
		}

		// add by wangqing 20171024 start
		// ��ͷҽ��
		opdOrder.setFilter("");
		opdOrder.filter();
		// ������ͷҽ��
		TParm orderParm2 = opdOrder.getBuffer(OpdOrder.PRIMARY);
		Vector newData2 = new Vector();
		for (int i=0; i < orderParm2.getCount(); i++) {
			if(orderParm2.getValue("ORDER_CODE", i) != null 
					&& orderParm2.getValue("ORDER_CODE", i).trim().length()>0 
					&& orderParm2.getBoolean("#NEW#", i) 
					&& orderParm2.getValue("ONW_ORDER_FLG", i) != null 
					&& orderParm2.getValue("ONW_ORDER_FLG", i).equals("Y")){
				String updateSql = " UPDATE ONW_ORDER SET EXE_FLG='Y' "
						+ " WHERE TRIAGE_NO='"+orderParm2.getValue("ONW_TRIAGE_NO", i)
						+"' AND SEQ_NO='"+orderParm2.getValue("ONW_ORDER_SEQ", i)
						+"' AND ORDER_CODE='"+orderParm2.getValue("ORDER_CODE", i)
						+"' AND SIGN_NS IS NOT NULL AND LENGTH(SIGN_NS)>0 "
						+ " AND SIGN_DR IS NOT NULL AND LENGTH(SIGN_DR)>0 "
						+ " AND NOT(EXE_FLG IS NOT NULL AND EXE_FLG='Y') ";
				newData2.add(updateSql);
			}		
		}
		// ɾ����ͷҽ��
		TParm deleteParm2 = opdOrder.getBuffer(OpdOrder.DELETE);
		for(int i=0; i<deleteParm2.getCount(); i++){
			if(deleteParm2.getValue("ORDER_CODE", i) != null 
					&& deleteParm2.getValue("ORDER_CODE", i).trim().length()>0 
					&& deleteParm2.getValue("ONW_ORDER_FLG", i) != null 
					&& deleteParm2.getValue("ONW_ORDER_FLG", i).equals("Y")){
				String updateSql = " UPDATE ONW_ORDER SET EXE_FLG='N' "
						+ " WHERE TRIAGE_NO='"+deleteParm2.getValue("ONW_TRIAGE_NO", i)
						+"' AND SEQ_NO='"+deleteParm2.getValue("ONW_ORDER_SEQ", i)
						+"' AND ORDER_CODE='"+deleteParm2.getValue("ORDER_CODE", i)
						+"' AND SIGN_NS IS NOT NULL AND LENGTH(SIGN_NS)>0 "
						+ " AND SIGN_DR IS NOT NULL AND LENGTH(SIGN_DR)>0 "
						+ " AND EXE_FLG IS NOT NULL AND EXE_FLG='Y' ";
				newData2.add(updateSql);
			}
		}	
		String[] updateSqls = (String[])newData2.toArray(new String[]{});	
		if(updateSqls.length>0){
			for(int i=0; i<updateSqls.length; i++){
				System.out.println("======////////updateSqls["+i+"]="+updateSqls[i]);
			}
			sql = StringTool.copyArray(sql, updateSqls);
		}
		// add by wangqing 20171024 end

		// �������
		String sqlTemp[] = getDiagrec().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("diag is wrong");
			setErrText(getDiagrec().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// getDiagrec().showDebug();
		// �������ʷ
		sqlTemp = getDrugAllergy().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("allergy is wrong");
			setErrText(getDrugAllergy().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);


		boolean isChanged = false;

		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		opdOrder.filter();

		//add by huangtt 20160906 �����������  start
		TParm allParm = opdOrder.getBuffer(OpdOrder.PRIMARY);
		TParm insertParm = new TParm();
		String[] aNames = allParm.getNames();
		for (int i = 0; i < allParm.getCount(); i++) {
			if(allParm.getValue("ORDER_CODE", i).length() != 0 && 
					allParm.getBoolean("#NEW#", i)){				
				for (int j = 0; j < aNames.length; j++) {
					if("BUSINESS_NO".equals(aNames[j])){
						insertParm.addData(aNames[j], this.getTredeNo());
					}else{
						insertParm.addData(aNames[j], allParm.getData(aNames[j], i));
					}

				}
			}


		}


		//add by huangtt 20160906 �����������  end

		TParm modifyParm = opdOrder.getBuffer(OpdOrder.MODIFY);
		TParm deleteParm = opdOrder.getBuffer(OpdOrder.DELETE);




		TParm mParm = this.makeParmClean(modifyParm);
		TParm dParm = this.makeParmClean(deleteParm);
		TParm erdParm = new TParm();//wanglong add 20150528
		erdParm.setData("CASE_NO", caseNo);
		erdParm.setData("ADM_STATUS", "5");
		TParm bedParm = ErdForBedAndRecordTool.getInstance().selPat(erdParm);
		if (bedParm.getCount() == 1) {
			String bedNo = bedParm.getValue("BED_NO", 0);
			int[] newRows = opdOrder.getNewRows();
			for (int i : newRows) {
				opdOrder.setItem(i, "BED_NO", bedNo);
			}
		}
		String bSql = 
				" SELECT RX_NO,SEQ_NO,ORDER_CODE,MEDI_QTY,MEDI_UNIT,DOSAGE_QTY,DOSAGE_UNIT,DISPENSE_QTY,AR_AMT,BILL_FLG,PRINT_FLG,BILL_TYPE,EXEC_FLG,PHA_CHECK_CODE,PHA_DOSAGE_CODE,PHA_DISPENSE_CODE,BUSINESS_NO " +
						" FROM OPD_ORDER " +
						" WHERE " +
						" CASE_NO ='" + caseNo +"' ";
		TParm bParm = new TParm(TJDODBTool.getInstance().select(bSql));

		List<String> mList = getKeyWordListHasChanged(mParm, bParm);
		List<String> dList = getKeyWordListHasChanged(dParm, bParm);

		boolean ektFlg = false;

		for (int i = 0; i < mParm.getCount("ORDER_CODE"); i++) {
			if(mParm.getValue("BUSINESS_NO", i).length() > 0){
				ektFlg = true;
				break;
			}
		}
		if(!ektFlg){
			for (int i = 0; i < bParm.getCount("ORDER_CODE"); i++) {
				if(bParm.getValue("BUSINESS_NO", i).length() > 0){
					ektFlg = true;
					break;
				}
			}
		}
		if(!ektFlg){
			for (int i = 0; i < dParm.getCount("ORDER_CODE"); i++) {
				if(dParm.getValue("BUSINESS_NO", i).length() > 0){
					ektFlg = true;
					break;
				}
			}
		}

		// ����ҽ��
		sqlTemp = getOpdOrder().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("order is wrong");
			// getOpdOrder().showDebug();
			setErrText(getOpdOrder().getErrText());
			return false;
		}

		List<String> sqlList = new ArrayList<String>();

		List<String> mTempList = new ArrayList<String>();
		List<String> dTempList = new ArrayList<String>();
		String update = "UPDATE OPD_ORDER SET";
		String delete = "DELETE FROM OPD_ORDER";
		String insert = "INSERT INTO OPD_ORDER";

		for (String ts : sqlTemp) {
			if(ts.contains(update)){
				mTempList.add(ts);
			}
			if(ts.contains(delete)){
				dTempList.add(ts);
			}
			if(ts.contains(insert)){
				sqlList.add(ts);
			}
		}

		this.sqlFilter(mTempList, mList, sqlList);
		this.sqlFilter(dTempList, dList, sqlList);

		if(sqlList.size() != sqlTemp.length){
			isChanged = true;
		}

		List<String> tmpList2 = new ArrayList<String>();

		for (int i = 0; i < sqlList.size(); i++) {
			if(isChanged && ektFlg && (sqlList.get(i).contains(update) || sqlList.get(i).contains(delete))){
				continue;
			}
			tmpList2.add(sqlList.get(i));
		}

		String[] sqls = new String[tmpList2.size()];

		for (int i = 0; i < tmpList2.size(); i++) {
			sqls[i] = tmpList2.get(i);
		}

		opdOrder.setFilter(lastFilter);
		opdOrder.filter();

		sql = StringTool.copyArray(sql, sqls);

		if (getOpdOrder().getMedApply().isModified()) {
			// ����MED_APPLY
			//============pangben 2013-01-31 ɾ��Med_Apply��������ĳ��޸�BILL_FLG =N�ֶ�
			//ȥ��ɾ����sql ֻ���� ��ӵ�sql���
			List list =new ArrayList();
			sqlTemp = getOpdOrder().getMedApply().getUpdateSQL();
			for (int i = 0; i < sqlTemp.length; i++) {
				if(sqlTemp[i].contains("DELETE"))
					continue;
				list.add(sqlTemp[i]);
			}
			String[] insertSqlTemp=null;
			if(list.toArray().length>0){
				insertSqlTemp=new String[list.toArray().length];
				Iterator iterator=list.iterator();
				int i=0;
				while (iterator.hasNext()) {
					insertSqlTemp[i] = iterator.next().toString();
					i++;
				}
			}
			if (insertSqlTemp == null) {
			}else{
				sql = StringTool.copyArray(sql, insertSqlTemp);
			}
		}

		// �����������ҽ��������Ҫ������ʷ��  add by huangtt 2016906  
		//		System.out.println("insertParm==="+insertParm.getCount("ORDER_CODE") );
		if(insertParm.getCount("ORDER_CODE") > 0){
			insertParm.setCount(insertParm.getCount("ORDER_CODE"));

			TParm inParm = new TParm();
			inParm.setData("orderParm", insertParm.getData());
			inParm.setData("EKT_HISTORY_NO", this.getHistoryNo());
			inParm.setData("OPT_TYPE", "INSERT");
			inParm.setData("OPT_USER", Operator.getID());
			inParm.setData("OPT_TERM", Operator.getIP());
			TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);
			TParm sqlParm = historyParm.getParm("sqlParm");

			if(sqlParm.getCount("SQL") > 0){
				sqlTemp = new String[sqlParm.getCount("SQL")];
				for (int i = 0; i < sqlParm.getCount("SQL"); i++) {
					sqlTemp[i]= sqlParm.getValue("SQL",i);
				}

				sql = StringTool.copyArray(sql, sqlTemp);
			}


		}



		// �����ɾ����ҽ��������Ҫ������ʷ��
		if(!isChanged){
			int count = getOpdOrder().getDeleteCount();
			if (count > -1) {
				TParm delParm =getOpdOrder().getBuffer(getOpdOrder().DELETE);
				TParm inParm = new TParm();
				inParm.setData("orderParm", delParm.getData());
				inParm.setData("EKT_HISTORY_NO", this.getHistoryNo());
				inParm.setData("OPT_TYPE", "DELETE");
				inParm.setData("OPT_USER", Operator.getID());
				inParm.setData("OPT_TERM", Operator.getIP());
				TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);
				TParm sqlParm = historyParm.getParm("sqlParm");
				String ids = historyParm.getValue("LastHistoryIds");

				if(sqlParm.getCount("SQL") > 0){
					sqlTemp = new String[sqlParm.getCount("SQL")];
					for (int i = 0; i < sqlParm.getCount("SQL"); i++) {
						sqlTemp[i]= sqlParm.getValue("SQL",i);
					}

					sql = StringTool.copyArray(sql, sqlTemp);
				}

				if(ids.length() > 0){
					sqlTemp = new String[1];
					ids = ids.substring(0, ids.length()-1);
					sqlTemp[0]="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
					sql = StringTool.copyArray(sql, sqlTemp);
				}

			}

		}





		// �������ʷ
		sqlTemp = getMedHistory().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("medhis is wrong");
			setErrText(getMedHistory().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		// ����Һ�
		sqlTemp = getRegPatAdm().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("regpatadm is wrong");
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// ���没��
		sqlTemp = getPatInfo().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("patinfo is wrong");
			setErrText(getPatInfo().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		//add by huangtt 20160902 ҽ�ƿ�SQl start
		String[] ektSql = this.getEktSql();
		if(ektSql == null){
			for (int i = 0; i < dParm.getCount("ORDER_CODE"); i++) {
				if(dParm.getBoolean("BILL_FLG", i)){
					throw new Exception("ɾ��ҽ���������⣬��ˢ�½������²�����");
				}

			}

		}else{
			sql = StringTool.copyArray(sql, ektSql);
		}

		//add by huangtt 20160902 ҽ�ƿ�SQl end
		this.setEktSql(null);
		TParm inParm = new TParm();
		inParm.setData("SQL", sql);
		inParm.setData("TREDE_NO", this.TREDE_NO);

		TParm saveResult = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onSave", inParm);
		if (saveResult.getErrCode() != 0) {
			setErrText(saveResult.getErrText());
			return false;
		}

		for (int i = 0; i < dParm.getCount("CASE_NO"); i++) {
			String sqlUpdate=" UPDATE DSS_CKBLOG SET ORDER_NO='',ORDER_SEQ='' " +
					" WHERE CASE_NO='"+dParm.getValue("CASE_NO", i)+"' " +
					//			" AND ORDER_CODE='"+order.getItemString(row,"ORDER_CODE")+"'" +
					" AND ORDER_NO='"+dParm.getValue("RX_NO", i)+"' " +
					" AND ORDER_SEQ='"+dParm.getValue("SEQ_NO", i)+"' " +
					" AND ADM_TYPE <> 'I'";
			//			System.out.println(sqlUpdate);
			TParm parmU = new TParm(TJDODBTool.getInstance().update(sqlUpdate));
		}

		// ����޸Ĵ洢
		getSubjrec().resetModify();
		getDiagrec().resetModify();
		getDrugAllergy().resetModify();
		getOpdOrder().resetModify();
		getOpdOrder().resetMap();
		getMedHistory().resetModify();
		getRegPatAdm().resetModify();
		getPatInfo().resetModify();
		getOpdOrder().resetMedApply();
		// System.out.println();
		CallNo callNoUtil = new CallNo();
		if (callNoUtil.init()) {
			/**
			 * /** �Һ�ͬ��
			 * 
			 * @param VISIT_DATE
			 *            String �Һ����� NOT NULL
			 * @param VISIT_NO
			 *            String �Һź� NOT NULL CASENO
			 * @param CLINIC_LABEL
			 *            String ר�� ��ͨ��
			 * @param SERIAL_NO
			 *            String С��
			 * @param PATIENT_ID
			 *            String ���˿���
			 * @param PNAME
			 *            String ��������
			 * @param PSEX
			 *            String �Ա�
			 * @param PAGE
			 *            String ����
			 * @param IDENTITY
			 *            String ���
			 * @param DEPTID
			 *            String ����ID
			 * @param REGISTERING_DATE
			 *            String �Һ�ʱ�䣨2010-2-8 11:11:11��
			 * @param DOCTOR
			 *            String ҽ����� ��ͨ����Ϊ��
			 * @param CTYPE
			 *            String 0 �Һ� 1 �˺� 2�������
			 * @param OPTYPE
			 *            String �������� 0 ��� 1 ɾ�� 2 ������߸��� 3 �˺�
			 * 
			 */
			callNoUtil.SyncClinicMaster("", getCaseNo(), "", "", "", "", "",
					"", "", "", "", "", "2", "", "");
		}
		if(isChanged){
			throw new Exception("���޸Ļ�ɾ����ҽ���ѱ������޸�\r\n��������ҽ���ѱ��棬��δ�շ�\r\n�������޸�ҽ�������շ�");
		}
		return true;
	}

	/**
	 * �ж�odo�����Ƿ��޸Ĺ�
	 * 
	 * @return boolean true:�޸Ĺ�,false:δ�޸�
	 */
	public boolean isModified() {
		return getSubjrec().isModified() || getDiagrec().isModified()
				|| getDrugAllergy().isModified() || getOpdOrder().isModified()
				|| getMedHistory().isModified() || getRegPatAdm().isModified()
				|| getPatInfo().isModified();
	}

	/**
	 * ��ʼ����ӡ��Ϣ
	 * 
	 * @return boolean true:�ɹ�,false:����
	 */
	/*
	 * /* ����ǩʹ����Ϣ �������ơ�֧����ʽ���������ơ��Ա���ϡ�������ơ�ʱ�����ơ�����
	 */
	/*
	 * private String patName; private String payTypeName; private String
	 * deptName; private String sexName; private String icdName; private String
	 * clinicName; private String sessionName; private String ageName;
	 */

	public static void main(String args[]) {
	}



	/**
	 * �Ƚ�a,b�е�ͬһ�ֶ��Ƿ�ͬ
	 * @author zhangp
	 * @param aParm
	 * @param bParm
	 * @param column
	 * @return
	 */
	private boolean comPareColumn(TParm aParm, TParm bParm, String column){
		String a = aParm.getValue(column);
		String b = bParm.getValue(column);
		if(!a.equals(b)){
			return true;
		}
		return false;
	}

	/**
	 * ����parm��ȥ������
	 * @author zhangp
	 * @param aParm
	 * @return
	 */
	public TParm makeParmClean(TParm aParm){
		TParm bParm = new TParm();
		String[] aNames = aParm.getNames();
		for (int i = 0; i < aParm.getCount(); i++) {
			if(aParm.getValue("ORDER_CODE", i).length() != 0){
				for (int j = 0; j < aNames.length; j++) {
					bParm.addData(aNames[j], aParm.getData(aNames[j], i));
				}
			}
		}
		return bParm;
	}

	/**
	 * ��ȡ�ı��˵�parm�е�sql�еĹؼ���
	 * @param aParm
	 * @param bParm
	 * @author zhangp
	 * @return
	 */
	private List<String> getKeyWordListHasChanged(TParm aParm, TParm bParm){
		String aRxNo;
		String bRxNo;
		int aSeqNo;
		int bSeqNo;
		TParm aRowParm;
		TParm bRowParm;
		String[] bNames = bParm.getNames();
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < aParm.getCount("ORDER_CODE"); i++) {
			aRxNo = aParm.getValue("RX_NO", i);
			aSeqNo = aParm.getInt("SEQ_NO", i);
			aRowParm = aParm.getRow(i);
			for (int j = 0; j < bParm.getCount("ORDER_CODE"); j++) {
				bRxNo = bParm.getValue("RX_NO", j);
				bSeqNo = bParm.getInt("SEQ_NO", j);
				bRowParm = bParm.getRow(j);
				if(aRxNo.equals(bRxNo) && aSeqNo == bSeqNo){
					for (int j2 = 0; j2 < bNames.length; j2++) {
						if(comPareColumn(aRowParm, bRowParm, bNames[j2])){
							list.add("CASE_NO='" + aParm.getValue("CASE_NO", i) + "' AND RX_NO='" + aParm.getValue("RX_NO", i) + "' AND SEQ_NO=" + aParm.getValue("SEQ_NO", i));
							break;
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * ���˲���Ҫִ�е�sql
	 * @author zhangp
	 * @param tempList
	 * @param list
	 * @param sqlList
	 */
	private void sqlFilter(List<String> tempList, List<String> list, List<String> sqlList){
		List<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < tempList.size(); i++) {
			String bs = tempList.get(i);
			for (String as : list) {
				if(bs.contains(as) && !temp.contains(i)){
					temp.add(i);
				}
			}
		}

		for (int i = 0; i < tempList.size(); i++) {
			if(!temp.contains(i)){
				sqlList.add(tempList.get(i));
			}
		}
	}

	/**
	 * ��������ҽ��վ����ʱ����ҽ���ȶ�  add by huangtt 20150907
	 * @param oParm
	 * @param caseNo
	 * @return
	 */
	public boolean onComparisionParm(TParm oParm,String caseNo){
		String bSql = 
				" SELECT RX_NO,SEQ_NO,ORDER_CODE,MEDI_QTY,MEDI_UNIT,DOSAGE_QTY,DOSAGE_UNIT,DISPENSE_QTY,AR_AMT,BILL_FLG,PRINT_FLG,BILL_TYPE,EXEC_FLG,PHA_CHECK_CODE,PHA_DOSAGE_CODE,PHA_DISPENSE_CODE,BUSINESS_NO " +
						" FROM OPD_ORDER " +
						" WHERE " +
						" CASE_NO ='" + caseNo +"' ";
		TParm bParm = new TParm(TJDODBTool.getInstance().select(bSql));
		//		System.out.println("bParm-----"+bParm);
		String[] aNames = bParm.getNames();
		for (int i = 0; i < bParm.getCount(); i++) {

			String orderCode = bParm.getValue("ORDER_CODE", i);
			String rxNo = bParm.getValue("RX_NO", i);
			String seqNo = bParm.getValue("SEQ_NO", i);
			for (int j = 0; j < oParm.getCount("CASE_NO"); j++) {
				if(orderCode.equals(oParm.getValue("ORDER_CODE", j))
						&& rxNo.equals(oParm.getValue("RX_NO", j))
						&& seqNo.equals(oParm.getValue("SEQ_NO", j))){

					for (int k = 0; k < aNames.length; k++) {
						if(!bParm.getValue(aNames[k], i).equalsIgnoreCase(oParm.getValue(aNames[k], j))){

							return true;
						}
					}

					break;
				}
			}

		}
		return false;
	}

}
