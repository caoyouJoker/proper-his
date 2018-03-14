package jdo.sum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;
import jdo.hl7.Hl7Communications;
import jdo.sum.bean.Hl7;
import jdo.sum.bean.Msh;
import jdo.sum.bean.Obx;
import jdo.sum.bean.ObxList;
import jdo.sum.bean.ObxOther;
import jdo.sum.bean.Pid;
import jdo.sum.bean.Pv1;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * 
 * @author shibl
 *
 */
public class SUMXmlTool extends TJDOTool {

	public SUMXmlTool() {

	}

	private Hl7 hl7;

	private Msh msh;

	private Pid pid;

	private Pv1 pv1;

	private ObxOther obxother;

	private ObxList obxlist;
	/**
     * 
     */
	@SuppressWarnings("serial")
	private static final ArrayList<String> timelist = new ArrayList<String>() {
		{
			add("0200");
			add("0600");
			add("1000");
			add("1400");
			add("1800");
			add("2200");
		}
	};
	/**
	 * 住院记录sql
	 */
	private static String admSql = "SELECT 'I' ADM_TYPE,E.DEPT_CHN_DESC AS DEPT,D.ROOM_DESC AS ROOM,B.BED_NO_DESC AS BED,"
			+ "G.USER_NAME AS  VS_DR,F.USER_NAME AS ADM_DR,A.CASE_NO,C.CTZ_DESC,A.IN_DATE,A.DS_DATE "
			+ "FROM ADM_INP A,SYS_BED B,SYS_CTZ C,SYS_ROOM D,SYS_DEPT E,SYS_OPERATOR F,SYS_OPERATOR G "
			+ "WHERE A.CASE_NO=B.CASE_NO(+) "
			+ "AND A.MR_NO=B.MR_NO(+) "
			+ "AND A.BED_NO=B.BED_NO(+) "
			+ "AND A.CTZ1_CODE=C.CTZ_CODE(+) "
			+ "AND B.ROOM_CODE=D.ROOM_CODE(+) "
			+ "AND A.DEPT_CODE=E.DEPT_CODE(+) "
			+ "AND A.OPD_DR_CODE=F.USER_ID(+) "
			+ "AND A.VS_DR_CODE=G.USER_ID(+) " + "AND A.CASE_NO='#'";
	/**
	 * 实例
	 */
	public static SUMXmlTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return SUMXmlTool
	 */
	public static SUMXmlTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SUMXmlTool();
		return instanceObject;
	}

	/**
	 * 组装数据
	 * 
	 * @param parm
	 * @return
	 */
	public synchronized TParm onAssembleData(TParm parm) {
		TParm result = new TParm();
		String mrNo = parm.getValue("MR_NO");
		String caseNo = parm.getValue("CASE_NO");
		// 获取病患对象
		Pat pat = Pat.onQueryByMrNo(mrNo);
		TParm adm = new TParm(TJDODBTool.getInstance().select(
				admSql.replace("#", caseNo)));
		if (adm.getCount() <= 0) {
			result.setErr(-101, "XML就诊数据异常");
			return result;
		}
		TParm sumMstData = SUMVitalSignTool.getInstance()
				.selectOneDateMst(parm);
		if (sumMstData.getCount() <= 0) {
			result.setErr(-101, "XML主表数据异常");
			return result;
		}
		TParm sumDtlData = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		if (sumDtlData.getCount() <= 0) {
			result.setErr(-101, "XML细表数据异常");
			return result;
		}
		try {
			onCreateData(pat, adm, sumMstData, sumDtlData);
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-101, "生成XML异常");
			return result;
		}
		return result;
	}

	/**
	 * 生成文件
	 * @param pat
	 * @param adm
	 * @param sumMstData
	 * @param sumDtlData
	 */
	private synchronized void onCreateData(Pat pat, TParm adm,
			TParm sumMstData, TParm sumDtlData) throws Exception {
		initObject();
		onCreateMsh();
		onCreatePid(pat);
		onCreatePv1(adm.getRow(0));
		onCreateObxOther(sumMstData.getRow(0));
		onCreateObxList(sumDtlData);
		UUID id = UUID.randomUUID();
		XmlUtil xu = new XmlUtil();
		xu.createXml(hl7, adm.getValue("CASE_NO", 0), id + ".xml", "UTF-8");
	}

	/**
	 * 初始化实体
	 */
	private synchronized void initObject() throws Exception {
		hl7 = new Hl7();
		msh = new Msh();
		pid = new Pid();
		pv1 = new Pv1();
		obxother = new ObxOther();
		obxlist = new ObxList();
	}

	/**
	 * 组创msh
	 */
	private synchronized void onCreateMsh() throws Exception {
		Timestamp now = SystemTool.getInstance().getDate();
		String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
		msh.setSendApp("HIS");
		msh.setReceiveApp("NIS");
		msh.setMessageType("ORU^R01");
		msh.setVersion("2.4");
		msh.setSendDate(nowStr);
		msh.setCreateUserId(Operator.getID());
		msh.setCreateUserName(Operator.getName());
		hl7.setMsh(msh);
	}

	/**
	 * 组装PID
	 * 
	 * @param parm
	 */
	private synchronized void onCreatePid(Pat pat) throws Exception {
		pid.setMrNo(pat.getMrNo());
		pid.setPatName(pat.getName());
		pid.setBirthDate(StringTool.getString(pat.getBirthday(), "yyyyMMdd"));
		pid.setSex(Hl7Communications.getInstance().getSexDescHl7(
				pat.getSexCode()));
		pid.setAddress(pat.getAddress());
		pid.setWorkAddress(pat.getCompanyAddress());
		pid.setTel(pat.getCellPhone());
		pid.setRelationTel(pat.getContactsTel());
		pid.setMarriage(Hl7Communications.getInstance().getMarriageCode(
				pat.getMarriageCode()));
		pid.setIdNo(pat.getIdNo());
		hl7.setPid(pid);
	}

	/**
	 * 组装PV1
	 * 
	 * @param parm
	 */
	private synchronized void onCreatePv1(TParm parm) throws Exception {
		pv1.setAdmType(parm.getValue("ADM_TYPE"));
		pv1.setDept(parm.getValue("DEPT"));
		pv1.setRoom(parm.getValue("ROOM"));
		pv1.setBed(parm.getValue("BED"));
		pv1.setVsDrCode(parm.getValue("VS_DR"));
		pv1.setAdmDr(parm.getValue("ADM_DR"));
		pv1.setCaseNo(parm.getValue("CASE_NO"));
		pv1.setCtzDesc(parm.getValue("CTZ_DESC"));
		pv1.setInDate(StringTool.getString(parm.getTimestamp("IN_DATE"),
				"yyyyMMddHHmmss"));
		pv1.setOutDate(StringTool.getString(parm.getTimestamp("OUT_DATE"),
				"yyyyMMddHHmmss"));
		hl7.setPv1(pv1);
	}

	/**
	 * 组装报告其他类数据
	 * 
	 * @param parm
	 */
	private synchronized void onCreateObxOther(TParm parm) throws Exception {
		obxother.setOrderDate(parm.getValue("EXAMINE_DATE"));
		obxother.setOrderTime("1200");
		obxother.setInputNum(BigDecimal.valueOf(parm
				.getDouble("INTAKEFLUIDQTY")));
		obxother.setOutputNum(BigDecimal.valueOf(parm
				.getDouble("OUTPUTURINEQTY")));
		obxother.setStool(BigInteger.valueOf(parm.getInt("STOOL")));
		obxother.setNormalStool(BigInteger.valueOf(parm.getInt("AUTO_STOOL")));
		obxother.setEnema(BigInteger.valueOf(parm.getInt("ENEMA")));
		// 引流量
		obxother.setDrainage(BigDecimal.valueOf(parm.getDouble("DRAINAGE")));
		obxother.setWeight(BigDecimal.valueOf(parm.getDouble("WEIGHT")));
		obxother.setHigh(BigDecimal.valueOf(parm.getDouble("HEIGHT")));
		hl7.setObxOther(obxother);
	}

	/**
	 * 组装报告数据
	 * 
	 * @param parm
	 */
	private synchronized void onCreateObxList(TParm parm) throws Exception {
		long seq = 1;
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmRow = parm.getRow(i);
			Obx obx = new Obx();
			obx.setId(BigInteger.valueOf(seq));
			obx.setValueType("SN");
			obx.setOrderDate(parmRow.getValue("EXAMINE_DATE"));
			obx.setOrderTime(timelist.get(parmRow.getInt("EXAMINESESSION")));
			obx.setTemperatureType(parmRow.getValue("TMPTRKINDCODE"));
			// 体温
			obx.setTemperature(BigDecimal.valueOf(parmRow.getDouble("TEMPERATURE")));
			// 脉搏
			obx.setPulse(BigInteger.valueOf(parmRow.getInt("PLUSE")));
			// 呼吸
			obx.setBreath(BigInteger.valueOf(parmRow.getInt("RESPIRE")));
			// 心率
			obx.setHeartRate(BigInteger.valueOf(parmRow.getInt("HEART_RATE")));
			// 收缩压
			obx.setSystolic(BigInteger.valueOf(parmRow.getInt("SYSTOLICPRESSURE")));
			// 舒张压
			obx.setDiastolic(BigInteger.valueOf(parmRow.getInt("DIASTOLICPRESSURE")));
			
			obx.setTemperatureUnit("cel");
			obx.setPulseUnit("次/min");
			obx.setBreathUnit("次/min");
			obx.setHeartRateUnit("次/min");
			obx.setSystolicUnit("mmHg");
			obx.setDiastolicUnit("mmHg");
			obxlist.getObx().add(obx);
			seq++;
		}
		hl7.setObxList(obxlist);
	}
}
