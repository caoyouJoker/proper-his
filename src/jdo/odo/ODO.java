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
 * Title: 门诊医生站对象
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
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 医师
	 */
	private String drCode;
	/**
	 * 诊断类型
	 */
	private String icdType;
	/**
	 * 部门
	 */
	private String deptCode;
	/**
	 * 主诉客述
	 */
	private Subjrec subjrec;
	/**
	 * 诊断
	 */
	private Diagrec diagrec;
	/**
	 * 过敏史
	 */
	private DrugAllergy drugAllergy;
	/**
	 * 医嘱
	 */
	private OpdOrder opdOrder;
	/**
	 * 既往史
	 */
	private MedHistory medHistory;
	/**
	 * 挂号
	 */
	private RegPatAdm regPatAdm;
	/**
	 * 病患
	 */
	private PatInfo patInfo;
	/**
	 * 医嘱历史
	 */
	private OpdOrderHistory opdOrderHistory;

	/**
	 * 错误信息
	 */
	private String errText = "";
	/**
	 * 数据库工具
	 */
	private TJDODBTool dbTool = new TJDODBTool();
	/**
	 * 医疗卡交易号
	 */
	private String TREDE_NO = "";

	/**
	 * 医疗卡历史表交易号
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
	 * 构造器
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
		// 初始化主诉客述
		setSubjrec(new Subjrec());
		// 初始化诊断
		setDiagrec(new Diagrec());
		// 过敏史
		setDrugAllergy(new DrugAllergy());
		// 医嘱
		setOpdOrder(new OpdOrder());
		// 既往史
		setMedHistory(new MedHistory());
		// 挂号
		setRegPatAdm(new RegPatAdm());
		// 病患
		setPatInfo(new PatInfo());
		// 医嘱历史
		setOpdOrderHistory(new OpdOrderHistory());
	}

	/**
	 * 设置门急住别
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
	 * 得到门急住别
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * 得到诊断类型
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
	 * 设置医师
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
	 * 得到医师
	 * 
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	/**
	 * 得到病案号
	 * yanjing 20131206
	 * @return String
	 */
	public String getMrNo() {
		return this.mrNo;
	}


	/**
	 * 得到就诊号
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}
	/**
	 * 设置病案号
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
	 * 设置就诊号
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
	 * 设置部门
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
	 * 得到部门
	 * 
	 * @return String
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * 设置主诉客述
	 * 
	 * @param subjrec
	 *            Subjrec
	 */
	public void setSubjrec(Subjrec subjrec) {
		this.subjrec = subjrec;
	}

	/**
	 * 得到主诉客述
	 * 
	 * @return Subjrec
	 */
	public Subjrec getSubjrec() {
		return subjrec;
	}

	/**
	 * 设置诊断对象
	 * 
	 * @param diagrec
	 *            Diagrec
	 */
	public void setDiagrec(Diagrec diagrec) {
		this.diagrec = diagrec;
	}

	/**
	 * 得到诊断对象
	 * 
	 * @return Diagrec
	 */
	public Diagrec getDiagrec() {
		return diagrec;
	}

	/**
	 * 设置过敏史对象
	 * 
	 * @param drugAllergy
	 *            DrugAllergy
	 */
	public void setDrugAllergy(DrugAllergy drugAllergy) {
		this.drugAllergy = drugAllergy;
	}

	/**
	 * 得到过敏史对象
	 * 
	 * @return DrugAllergy
	 */
	public DrugAllergy getDrugAllergy() {
		return drugAllergy;
	}

	/**
	 * 设置医嘱对象
	 * 
	 * @param opdOrder
	 *            OpdOrder
	 */
	public void setOpdOrder(OpdOrder opdOrder) {
		this.opdOrder = opdOrder;
	}

	/**
	 * 得到医嘱对象
	 * 
	 * @return OpdOrder
	 */
	public OpdOrder getOpdOrder() {
		return opdOrder;
	}

	/**
	 * 设置既往史对象
	 * 
	 * @param medHistory
	 *            MedHistory
	 */
	public void setMedHistory(MedHistory medHistory) {
		this.medHistory = medHistory;
	}

	/**
	 * 得到既往史对象
	 * 
	 * @return MedHistory
	 */
	public MedHistory getMedHistory() {
		return medHistory;
	}

	/**
	 * 设置挂号对象
	 * 
	 * @param regPatAdm
	 *            RegPatAdm
	 */
	public void setRegPatAdm(RegPatAdm regPatAdm) {
		this.regPatAdm = regPatAdm;
	}

	/**
	 * 得到挂号对象
	 * 
	 * @return RegPatAdm
	 */
	public RegPatAdm getRegPatAdm() {
		return this.regPatAdm;
	}

	/**
	 * 设置病患对象
	 * 
	 * @param patInfo
	 *            PatInfo
	 */
	public void setPatInfo(PatInfo patInfo) {
		this.patInfo = patInfo;
	}

	/**
	 * 得到病患对象
	 * 
	 * @return PatInfo
	 */
	public PatInfo getPatInfo() {
		return this.patInfo;
	}

	/**
	 * 设置医嘱历史对象
	 * 
	 * @param opdOrderHistory
	 *            OpdOrderHistory
	 */
	public void setOpdOrderHistory(OpdOrderHistory opdOrderHistory) {
		this.opdOrderHistory = opdOrderHistory;
	}

	/**
	 * 得到医嘱历史对象
	 * 
	 * @return OpdOrderHistory
	 */
	public OpdOrderHistory getOpdOrderHistory() {
		return this.opdOrderHistory;
	}

	/**
	 * 得到数据库工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return dbTool;
	}

	/**
	 * 设置错误信息
	 * 
	 * @param errText
	 *            String
	 */
	public void setErrText(String errText) {
		this.errText = errText;
	}

	/**
	 * 得到错误信息
	 * 
	 * @return String
	 */
	public String getErrText() {
		return errText;
	}

	/**
	 * 设置医疗卡交易号
	 * 
	 * @param TredeNo
	 *            String
	 */
	public void setTredeNo(String TredeNo) {
		this.TREDE_NO = TredeNo;
	}

	/**
	 * 得到医疗卡交易号
	 */
	public String getTredeNo() {
		return TREDE_NO;
	}

	/**
	 * 查询
	 * 
	 * @return boolean
	 */
	public boolean onQuery() {
		// 重新设置错误信息
		setErrText("");

		// 检核CASE_NO
		if (getCaseNo() == null || getCaseNo().length() <= 0) {
			setErrText("CaseNo为空！");
			return false;
		}
		// 查询主诉客诉数据
		if (!getSubjrec().onQuery()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		if (getSubjrec().rowCount() < 1)
			getSubjrec().insertRow();
		// 查询诊断数据
		if (!getDiagrec().onQuery()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		getDiagrec().insertRow();
		// 查询过敏史数据
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
		// 查询医嘱数据
		if (!getOpdOrder().onQuery()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}
		// 查询既往史数据
		if (!getMedHistory().onQuery()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		getMedHistory().insertRow();
		// 查询挂号数据
		if (!getRegPatAdm().onQuery()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}

		// 查询病患数据
		if (!getPatInfo().onQuery()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		getOpdOrder().getMedApply();// 初始化med_apply
		getOpdOrder().getLabMap();
		// //查询病患数据
		// if(!getPatInfo().onQuery()){
		// setErrText(getPatInfo().getErrText());
		// return false;
		// }
		return true;
	}

	/**
	 * 保存检测
	 * 
	 * @return boolean
	 */
	public boolean checkSave() {
		// 检测主诉客诉
		if (!getSubjrec().checkSave()) {
			setErrText(getSubjrec().getErrText());
			return false;
		}
		// 检测诊断
		if (!getDiagrec().checkSave()) {
			setErrText(getDiagrec().getErrText());
			return false;
		}
		// 检测过敏史
		if (!getDrugAllergy().checkSave()) {
			setErrText(getDrugAllergy().getErrText());
			return false;
		}
		// 检测医嘱
		if (!getOpdOrder().checkSave()) {
			setErrText(getOpdOrder().getErrText());
			return false;
		}
		// 检测既往史
		if (!getMedHistory().checkSave()) {
			setErrText(getMedHistory().getErrText());
			return false;
		}
		// 检测挂号
		if (!getRegPatAdm().checkSave()) {
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		// 检测病患
		if (!getPatInfo().checkSave()) {
			setErrText(getPatInfo().getErrText());
			return false;
		}
		Timestamp optDate = getDBTool().getDBTime();
		// 设置主诉客诉修改行的用户信息
		getSubjrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// 设置诊断修改行的用户信息
		getDiagrec().setOperator(Operator.getID(), optDate, Operator.getIP());
		// 设置过敏史修改行的用户信息
		getDrugAllergy().setOperator(Operator.getID(), optDate,
				Operator.getIP());
		// 设置医嘱修改行的用户信息
		getOpdOrder().setOperator(Operator.getID(), optDate, Operator.getIP());
		// 设置既往史修改行的用户信息
		getMedHistory()
		.setOperator(Operator.getID(), optDate, Operator.getIP());
		return true;
	}

	/**
	 * 保存医嘱
	 * 
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean onSave() throws Exception  {
		// 保存检测
		if (!checkSave())
			return false;

		// 保存主诉客诉
		String sql[] = getSubjrec().getUpdateSQL();

		//		for(int i = 0;i<sql.length;i++){
		//			subjrec.setItem(i, "MR_NO", mrNo);
		//		System.out.println(i+"主诉客诉 is :::"+sql[i]);
		//		}
		// 检核SQL是否为空
		if (sql == null) {
			// System.out.println("subjrec is wrong");
			setErrText(getSubjrec().getErrText());
			return false;
		}

		// add by wangqing 20171024 start
		// 口头医嘱
		opdOrder.setFilter("");
		opdOrder.filter();
		// 新增口头医嘱
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
		// 删除口头医嘱
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

		// 保存诊断
		String sqlTemp[] = getDiagrec().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("diag is wrong");
			setErrText(getDiagrec().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// getDiagrec().showDebug();
		// 保存过敏史
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

		//add by huangtt 20160906 获得新增数据  start
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


		//add by huangtt 20160906 获得新增数据  end

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

		// 保存医嘱
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
			// 保存MED_APPLY
			//============pangben 2013-01-31 删除Med_Apply表操作更改成修改BILL_FLG =N字段
			//去掉删除的sql 只保存 添加的sql语句
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

		// 如果有新增的医嘱，则需要插入历史档  add by huangtt 2016906  
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



		// 如果有删除的医嘱，则需要插入历史档
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





		// 保存既往史
		sqlTemp = getMedHistory().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("medhis is wrong");
			setErrText(getMedHistory().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		// 保存挂号
		sqlTemp = getRegPatAdm().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("regpatadm is wrong");
			setErrText(getRegPatAdm().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);
		// 保存病患
		sqlTemp = getPatInfo().getUpdateSQL();
		if (sqlTemp == null) {
			// System.out.println("patinfo is wrong");
			setErrText(getPatInfo().getErrText());
			return false;
		}
		sql = StringTool.copyArray(sql, sqlTemp);

		//add by huangtt 20160902 医疗卡SQl start
		String[] ektSql = this.getEktSql();
		if(ektSql == null){
			for (int i = 0; i < dParm.getCount("ORDER_CODE"); i++) {
				if(dParm.getBoolean("BILL_FLG", i)){
					throw new Exception("删除医嘱出现问题，请刷新界面重新操作！");
				}

			}

		}else{
			sql = StringTool.copyArray(sql, ektSql);
		}

		//add by huangtt 20160902 医疗卡SQl end
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

		// 清除修改存储
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
			 * /** 挂号同步
			 * 
			 * @param VISIT_DATE
			 *            String 挂号日期 NOT NULL
			 * @param VISIT_NO
			 *            String 挂号号 NOT NULL CASENO
			 * @param CLINIC_LABEL
			 *            String 专家 普通等
			 * @param SERIAL_NO
			 *            String 小号
			 * @param PATIENT_ID
			 *            String 病人卡号
			 * @param PNAME
			 *            String 病人姓名
			 * @param PSEX
			 *            String 性别
			 * @param PAGE
			 *            String 年龄
			 * @param IDENTITY
			 *            String 身份
			 * @param DEPTID
			 *            String 科室ID
			 * @param REGISTERING_DATE
			 *            String 挂号时间（2010-2-8 11:11:11）
			 * @param DOCTOR
			 *            String 医生编号 普通不分为空
			 * @param CTYPE
			 *            String 0 挂号 1 退号 2就诊完成
			 * @param OPTYPE
			 *            String 操作类型 0 清空 1 删除 2 插入或者更新 3 退号
			 * 
			 */
			callNoUtil.SyncClinicMaster("", getCaseNo(), "", "", "", "", "",
					"", "", "", "", "", "2", "", "");
		}
		if(isChanged){
			throw new Exception("您修改或删除的医嘱已被他人修改\r\n您新增的医嘱已保存，但未收费\r\n请重新修改医嘱，并收费");
		}
		return true;
	}

	/**
	 * 判断odo对象是否修改过
	 * 
	 * @return boolean true:修改过,false:未修改
	 */
	public boolean isModified() {
		return getSubjrec().isModified() || getDiagrec().isModified()
				|| getDrugAllergy().isModified() || getOpdOrder().isModified()
				|| getMedHistory().isModified() || getRegPatAdm().isModified()
				|| getPatInfo().isModified();
	}

	/**
	 * 初始化打印信息
	 * 
	 * @return boolean true:成功,false:错误
	 */
	/*
	 * /* 处方签使用信息 病患名称、支付方式、部门名称、性别、诊断、诊间名称、时段名称、年龄
	 */
	/*
	 * private String patName; private String payTypeName; private String
	 * deptName; private String sexName; private String icdName; private String
	 * clinicName; private String sessionName; private String ageName;
	 */

	public static void main(String args[]) {
	}



	/**
	 * 比较a,b中的同一字段是否不同
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
	 * 整理parm并去掉空行
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
	 * 获取改变了的parm中的sql中的关键字
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
	 * 过滤不需要执行的sql
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
	 * 用于门诊医生站保存时进行医嘱比对  add by huangtt 20150907
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
