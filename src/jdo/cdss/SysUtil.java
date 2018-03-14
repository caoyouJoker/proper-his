package jdo.cdss;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;


/**
 * 
 * @author zhangp
 *
 */
public class SysUtil {

	private final String[] CHARS = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	
	private final String DATEFORMAT_PATTERN_US = "dd-MMM-yyyy";
	
	private final String DATEFORMAT_PATTERN = "yyyy-MM-dd";
	
	private final String DATEFORMAT_PATTERN_TIME = "yyyy-MM-dd HH:mm:ss";
	
	private final DateFormat DATEFORMAT_US = new SimpleDateFormat(DATEFORMAT_PATTERN_US, Locale.US);
	
	private final DateFormat DATEFORMAT = new SimpleDateFormat(DATEFORMAT_PATTERN, Locale.CHINA);
	
	private final DateFormat DATEFORMAT_TIME = new SimpleDateFormat(DATEFORMAT_PATTERN_TIME, Locale.CHINA);

	/**
	 * 生成8位UUID
	 * @return
	 */
	public String generateShortUuid() {

		StringBuffer shortBuffer = new StringBuffer();

		String uuid = UUID.randomUUID().toString().replace("-", "");

		for (int i = 0; i < 8; i++) {

			String str = uuid.substring(i * 4, i * 4 + 4);

			int x = Integer.parseInt(str, 16);

			shortBuffer.append(CHARS[x % 0x3E]);

		}

		return shortBuffer.toString();

	}
	
	/**
	 * 中制时间转long型
	 * @param dateStr "yyyy-MM-dd" example "2015-4-23"
	 * @return 1429718400000
	 * @throws ParseException
	 */
	public Long parseDateStr2Long(String dateStr){
		
		try {
			
			Date date = DATEFORMAT.parse(dateStr);
			
			return date.getTime();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
	}
	
	/**
	 * 中制时间转long型
	 * @param dateStr "yyyy-MM-dd" example "2015-4-23  23:59:59"
	 * @return 1429804799000
	 * @throws ParseException
	 */
	public Long parseTimeStr2Long(String dateStr){
		
		try {
			
			Date date = DATEFORMAT_TIME.parse(dateStr);
			
			return date.getTime();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
	}
	
	
	/**
	 * 中制时间转美制时间
	 * @param dateStr "yyyy-MM-dd" example "2015-4-23"
	 * @return "dd-MMM-yyyy" example "23-Apr-2015"
	 * @throws ParseException
	 */
	public String parseDateStrUS(String dateStr){
		
		try {
			
			return DATEFORMAT_US.format(DATEFORMAT.parse(dateStr));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
			
		}
		
	}
	
	/**
	 * 2015-01-01
	 * 
	 * @param timestamp
	 * @return
	 */
	public String getDateStr(Timestamp timestamp) {
		String tss = "" + timestamp;
		tss = tss.substring(0, 10);
		return tss;
	}

	/**
	 * 2015-01-01 23:59:59
	 * 
	 * @param timestamp
	 * @return
	 */
	public String getTimeStr(Timestamp timestamp) {
		String tss = "" + timestamp;
		tss = tss.substring(0, 19);
		return tss;
	}
	
	public TParm parseHisPojoToTParm(HisPojo hisPojo){
		
		TParm hisParm = new TParm();
		TParm orderParm = new TParm();
		TParm lastOrderParm = new TParm();
		TParm chestPainOrderParm = new TParm();
		TParm exaParm = new TParm();
		TParm lastExaParm = new TParm();
		TParm breakPointParm = new TParm();
		TParm adviceParm = new TParm();
		TParm erdParm = new TParm();
		TParm allergyParm = new TParm();
		//用于护士单次执行界面
		TParm singleExeorderParm = new TParm();
		
		List<OrderPojo> orderPojos = hisPojo.getOrderPojos();
		List<OrderPojo> lastOrderPojos = hisPojo.getLastOrderPojos();
		List<OrderPojo> chestPainOrderPojos = hisPojo.getChestpainOrderPojos();
		List<ExaPojo> exaPojos = hisPojo.getExaPojos();
		List<ExaPojo> lastExaPojos = hisPojo.getLastExaPojos();
		List<BreakPoint> breakPoints = hisPojo.getBreakPoints();
		List<String> diags = hisPojo.getDiags();
		List<String> operationDiags = hisPojo.getOperationDiags();
		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();
		List<ErdPojo> erdPojos = hisPojo.getErdPojos();
		List<AllergyPojo> allergyPojos = hisPojo.getAllergyPojos();
		//用于护士单次执行界面
		List<OrderPojo> singleExeorderPojos = hisPojo.getSingleExeOrderPojos();
		
		hisParm = parseOnlyHisPojoToTParm(hisPojo);
		orderParm = parseOnlyOrderPojoToTParm(orderPojos);
		lastOrderParm = parseOnlyOrderPojoToTParm(lastOrderPojos);
		chestPainOrderParm = parseOnlyChestpainOrderPojoToTParm(chestPainOrderPojos);
		exaParm = parseOnlyExaPojoToTParm(exaPojos);
		lastExaParm = parseOnlyExaPojoToTParm(lastExaPojos);
		breakPointParm = parseOnlyBreakPointToTParm(breakPoints);
		erdParm = this.parseOnlyErdPojoToTParm(erdPojos);
		allergyParm = this.parseOnlyAllergyPojoToTParm(allergyPojos);
		//用于护士单次执行界面
		singleExeorderParm=  parseOnlysingleExeOrderPojoToTParm(singleExeorderPojos);
		
		for (String string : diags) {
			hisParm.addData(HisPojoMap.DIAGS, string);
		}
		for (String string : operationDiags) {
			hisParm.addData(HisPojoMap.OPERATIONDIAGS, string);
		}
		adviceParm = parseOnlyAdvicePojoToTParm(advicePojos);
		hisParm.setData(HisPojoMap.ORDERPOJOS, orderParm.getData());
		hisParm.setData(HisPojoMap.LASTORDERPOJOS, lastOrderParm.getData());
		hisParm.setData(HisPojoMap.CHESTPAINORDERPOJOS, chestPainOrderParm.getData());
		hisParm.setData(HisPojoMap.EXAPOJOS, exaParm.getData());
		hisParm.setData(HisPojoMap.LASTEXAPOJOS, lastExaParm.getData());
		hisParm.setData(HisPojoMap.BREAKPOINTS, breakPointParm.getData());
		hisParm.setData(HisPojoMap.ADVICEPOJOS, adviceParm.getData());
		hisParm.setData(HisPojoMap.ERDPOJOS, erdParm.getData());
		hisParm.setData(HisPojoMap.ALLERGYPOJOS, allergyParm.getData());
		//用于护士单次执行界面
		hisParm.setData(HisPojoMap.SINGLEEXEORDERPOJOS, singleExeorderParm.getData());
		
		
		return hisParm;
	}
	
	@SuppressWarnings("unchecked")
	public HisPojo parseTParmToHisPojo(TParm hisParm){
		
		TParm orderParm = hisParm.getParm(HisPojoMap.ORDERPOJOS);
		TParm lastOrderParm = hisParm.getParm(HisPojoMap.LASTORDERPOJOS);
		TParm chestPainOrderParm = hisParm.getParm(HisPojoMap.CHESTPAINORDERPOJOS);
		TParm exaParm = hisParm.getParm(HisPojoMap.EXAPOJOS);
		TParm lastExaParm = hisParm.getParm(HisPojoMap.LASTEXAPOJOS);
		TParm breakPointParm = hisParm.getParm(HisPojoMap.BREAKPOINTS);
		TParm adviceParm = hisParm.getParm(HisPojoMap.ADVICEPOJOS);
		TParm erdParm = hisParm.getParm(HisPojoMap.ERDPOJOS);
		TParm allergyParm = hisParm.getParm(HisPojoMap.ALLERGYPOJOS);
		//用于护士单次执行界面
		TParm singleExeorderParm = hisParm.getParm(HisPojoMap.SINGLEEXEORDERPOJOS);
		
		List<String> diags = (List<String>) hisParm.getData(HisPojoMap.DIAGS);
		List<String> operationDiags = (List<String>) hisParm.getData(HisPojoMap.OPERATIONDIAGS);
		
		HisPojo hisPojo = parseTParmToOnlyHisPojo(hisParm);
		List<OrderPojo> orderPojos = parseOnlyOrderPojoToTParm(orderParm);
		List<OrderPojo> lastOrderPojos = parseOnlyOrderPojoToTParm(lastOrderParm);
		List<OrderPojo> chestPainOrderPojos = parseTParmToOnlychestPainOrderPojo(chestPainOrderParm);
		List<ExaPojo> exaPojos = parseTParmToOnlyExaPojo(exaParm);
		List<ExaPojo> lastExaPojos = parseTParmToOnlyExaPojo(lastExaParm);
		List<BreakPoint> breakPoints = parseTParmToOnlyBreakPoint(breakPointParm);
		List<AdvicePojo> advicePojos = parseOnlyAdvicePojoToTParm(adviceParm);
		List<ErdPojo>  erdPojos = this.parseTParmToOnlyErdPojo(erdParm);
		List<AllergyPojo> allergyPojos = this.parseTParmToOnlyAllergyPojo(allergyParm);
		//用于护士单次执行界面
		List<OrderPojo> singleExeorderPojos = this.parseOnlysingleExeOrderPojoToTParm(singleExeorderParm);
		
		hisPojo.setDiags(diags);
		hisPojo.setOperationDiags(operationDiags);
		hisPojo.setOrderPojos(orderPojos);
		hisPojo.setLastOrderPojos(lastOrderPojos);
		hisPojo.setChestpainOrderPojos(chestPainOrderPojos);
		hisPojo.setExaPojos(exaPojos);
		hisPojo.setLastExaPojos(lastExaPojos);
		hisPojo.setBreakPoints(breakPoints);
		hisPojo.setAdvicePojos(advicePojos);
		hisPojo.setErdPojos(erdPojos);
		hisPojo.setAllergyPojos(allergyPojos);
		//用于护士单次执行界面
		hisPojo.setSingleExeOrderPojos(singleExeorderPojos);
		return hisPojo;
	}
	
	 
	
	public ClpPojo parseTParmToClpPojo(TParm hisParm){
		ClpPojo clpPojo = new ClpPojo();
		clpPojo.setDeptCode(hisParm.getValue(ClpPojoMap.DEPTCODE));
		clpPojo.setCtzCode(hisParm.getValue(ClpPojoMap.CTZCODE));
		String sql = "SELECT TAG_CODE FROM SYS_DIAGNOSIS_TAGS WHERE ICD_CODE = '"+hisParm.getValue(ClpPojoMap.DIAGCODE)+"'";
		TParm diagParm = new TParm(TJDODBTool.getInstance().select(sql));
		List<String> diags = new ArrayList<String>();		
		String diag;
		for (int i = 0; i < diagParm.getCount(); i++) {
			diag = diagParm.getValue("TAG_CODE", i);
			if(diag.length() > 0 && !diags.contains(diag)){
				diags.add(diag);
			}
		}
		clpPojo.setDiags(diags);
		TParm adviceParm = hisParm.getParm(ClpPojoMap.CLPADVICEPOJOS);
		List<ClpAdvicePojo> clpAdvicePojos = parseOnlyClpAdvicePojoToTParm(adviceParm);
		
		clpPojo.setClpAdvicePojos(clpAdvicePojos);
		
		return clpPojo;		
	}
	
	public static void main(String[] args) throws ParseException {
		SysUtil sysUtil = new SysUtil();
		
		System.out.println(sysUtil.parseDateStrUS("2015-4-23"));
		
		System.out.println(sysUtil.parseTimeStr2Long("2015-4-23 23:59:59"));
		
	}
	
	private TParm parseOnlyHisPojoToTParm(HisPojo hisPojo){
		TParm hisParm = new TParm();
		hisParm.setData(HisPojoMap.MRNO, hisPojo.getMrNo());
		hisParm.setData(HisPojoMap.GESTATIONALWEEKS, hisPojo.getGestationalWeeks());
		hisParm.setData(HisPojoMap.MAINDIAG, hisPojo.getMainDiag());
		hisParm.setData(HisPojoMap.SEX, hisPojo.getSex());
		hisParm.setData(HisPojoMap.AGE, hisPojo.getAge());
		hisParm.setData(HisPojoMap.AGEMONTH, hisPojo.getAgeMonth());
		hisParm.setData(HisPojoMap.AGEDAY, hisPojo.getAgeDay());
		hisParm.setData(HisPojoMap.LMPFLG, hisPojo.getLmpFlg());
		hisParm.setData(HisPojoMap.ALLERGYFLG, hisPojo.getAllergyFlg());
		hisParm.setData(HisPojoMap.WEIGHT, hisPojo.getWeight());
		hisParm.setData(HisPojoMap.CALWEIGHT, hisPojo.getCalWeight());
		hisParm.setData(HisPojoMap.NEWBORNFLG, hisPojo.getNewBornFlg());
		hisParm.setData(HisPojoMap.ADMTYPE, hisPojo.getAdmType());
		return hisParm;
	}
	
	private TParm parseOnlyOrderPojoToTParm(List<OrderPojo> orderPojos){
		TParm orderParm = new TParm();
		for (OrderPojo orderPojo : orderPojos) {
			orderParm.addData(OrderPojoMap.ID, orderPojo.getId());
			orderParm.addData(OrderPojoMap.ORDERCODE, orderPojo.getOrderCode());
			orderParm.addData(OrderPojoMap.ORDERDESC, orderPojo.getOrderDesc());
			if(orderPojo.getRxNo().equals("999999999999")){
				orderParm.addData(OrderPojoMap.RXNO, orderPojo.getMedQty()+orderPojo.getUnit()+orderPojo.getFreqCycle()+orderPojo.getOrderTimeLong()); //modify by huangtt 20150818
			}else{
				orderParm.addData(OrderPojoMap.RXNO, orderPojo.getRxNo());
			}			
			orderParm.addData(OrderPojoMap.SEQNO, orderPojo.getSeqNo()); //add by huangtt 20150818
			orderParm.addData(OrderPojoMap.LIQUIDMAINFLG, orderPojo.getLiquidMainFlg());
			orderParm.addData(OrderPojoMap.LIQUIDNO, orderPojo.getLiquidNo());
			orderParm.addData(OrderPojoMap.TAGS, orderPojo.getTags());
			orderParm.addData(OrderPojoMap.MEDQTY, orderPojo.getMedQty());
			orderParm.addData(OrderPojoMap.UNIT, orderPojo.getUnit());
			orderParm.addData(OrderPojoMap.FREQCYCLE, orderPojo.getFreqCycle());
			orderParm.addData(OrderPojoMap.FREQTIMES, orderPojo.getFreqTimes());
			orderParm.addData(OrderPojoMap.TAKEDAYS, orderPojo.getTakeDays());
			orderParm.addData(OrderPojoMap.ORDERDATE, orderPojo.getOrderDate());
			orderParm.addData(OrderPojoMap.SYSDATE, orderPojo.getSysDate());
			orderParm.addData(OrderPojoMap.ORDERDATELONG, orderPojo.getOrderDateLong());
			orderParm.addData(OrderPojoMap.SYSDATELONG, orderPojo.getSysDateLong());
			orderParm.addData(OrderPojoMap.ORDERTIMELONG, orderPojo.getOrderTimeLong());
			orderParm.addData(OrderPojoMap.SYSTIMELONG, orderPojo.getSysTimeLong());
			orderParm.addData(OrderPojoMap.TRANSHOSPCODE, orderPojo.getTransHospCode());
			orderParm.addData(OrderPojoMap.OPTITEMCODE, orderPojo.getOptitemCode());
			orderParm.addData(OrderPojoMap.DEVCODE, orderPojo.getDevCode());
		}
		return orderParm;
	}
	
	private TParm parseOnlyChestpainOrderPojoToTParm(List<OrderPojo> orderPojos){
		TParm orderParm = new TParm();
		for (OrderPojo orderPojo : orderPojos) {
			orderParm.addData(OrderPojoMap.ID, orderPojo.getId());
			orderParm.addData(OrderPojoMap.ORDERCODE, orderPojo.getOrderCode());
			orderParm.addData(OrderPojoMap.SYSPHACLASS1, orderPojo.getSysPhaClass1());
			orderParm.addData(OrderPojoMap.SYSPHACLASS2, orderPojo.getSysPhaClass2());
		}
		return orderParm;
	}
	
	private TParm parseOnlyExaPojoToTParm(List<ExaPojo> exaPojos){
		TParm exaParm = new TParm();
		for (ExaPojo exaPojo : exaPojos) {
			exaParm.addData(ExaPojoMap.ID, exaPojo.getId());
			exaParm.addData(ExaPojoMap.TESTITEMCODE, exaPojo.getTestitemCode());
			exaParm.addData(ExaPojoMap.TESTVALUE, exaPojo.getTestValue());
			exaParm.addData(ExaPojoMap.TESTVALUETEXT, exaPojo.getTestValueText());
			exaParm.addData(ExaPojoMap.TESTUNIT, exaPojo.getTestUnit());
		}
		return exaParm;
	}
	
	private TParm parseOnlyBreakPointToTParm(List<BreakPoint> breakPoints){
		TParm breakPointParm = new TParm();
		for (BreakPoint breakPoint : breakPoints) {
			breakPointParm.addData(BreakPointMap.ID, breakPoint.getId());
			breakPointParm.addData(BreakPointMap.MRNO, breakPoint.getMrNo());
			breakPointParm.addData(BreakPointMap.FLOWID, breakPoint.getFlowId());
			breakPointParm.addData(BreakPointMap.NODEID, breakPoint.getNodeId());
			breakPointParm.addData(BreakPointMap.CREATDATE, breakPoint.getCreatDate());
		}
		return breakPointParm;
	}
	
	private TParm parseOnlyErdPojoToTParm(List<ErdPojo> erdPojos){
		TParm erdParm = new TParm();
		for (ErdPojo erdPojo : erdPojos) {
			erdParm.addData(ErdPojoMap.ID, erdPojo.getId());
			erdParm.addData(ErdPojoMap.MONITORITEMEN, erdPojo.getMonitorItemEn());
			erdParm.addData(ErdPojoMap.MONITORVALUE, erdPojo.getMonitorValue());
			erdParm.addData(ErdPojoMap.MEASUREUNIT, erdPojo.getMeasureUnit());			
		}
		return erdParm;
	}
	
	private TParm parseOnlyAllergyPojoToTParm(List<AllergyPojo> allergyPojos){
		TParm allergyParm = new TParm();
		for(AllergyPojo allergyPojo : allergyPojos){
			allergyParm.addData(AllergyPojoMap.ID, allergyPojo.getId());
			allergyParm.addData(AllergyPojoMap.DRUGTYPE, allergyPojo.getDrugType());
			allergyParm.addData(AllergyPojoMap.DRUGORINGRDCODE, allergyPojo.getDrugoringrdCode());
		}
		return allergyParm;
	}
	
	private TParm parseOnlyAdvicePojoToTParm(List<AdvicePojo> advicePojos){
		TParm adviceParm = new TParm();
		for (AdvicePojo advicePojo : advicePojos) {
			adviceParm.addData(AdvicePojoMap.KNOWLADGEID, advicePojo.getKnowladgeId());
			adviceParm.addData(AdvicePojoMap.LEVEL, advicePojo.getLevel());
			adviceParm.addData(AdvicePojoMap.ADVICETEXT, advicePojo.getAdviceText());
			adviceParm.addData(AdvicePojoMap.MEDQTY, advicePojo.getMedQty());
			adviceParm.addData(AdvicePojoMap.UNIT, advicePojo.getUnit());
			adviceParm.addData(AdvicePojoMap.FREQCODE, advicePojo.getFreqCode());
			adviceParm.addData(AdvicePojoMap.ORDERCODE, advicePojo.getOrderCode());
			adviceParm.addData(AdvicePojoMap.SEQNO, advicePojo.getSeqNo());
			adviceParm.addData(AdvicePojoMap.RXNO, advicePojo.rxNo);
			adviceParm.addData(AdvicePojoMap.REMARKS, advicePojo.remarks);
			adviceParm.addData(AdvicePojoMap.ORDERDESC, advicePojo.getOrderDesc());
			
		}
		return adviceParm;
	}
	
	private HisPojo parseTParmToOnlyHisPojo(TParm hisParm){
		HisPojo hisPojo = new HisPojo();
		hisPojo.setMrNo(hisParm.getValue(HisPojoMap.MRNO));
		hisPojo.setGestationalWeeks(hisParm.getInt(HisPojoMap.GESTATIONALWEEKS));
		hisPojo.setMainDiag(hisParm.getValue(HisPojoMap.MAINDIAG));
		hisPojo.setSex(hisParm.getValue(HisPojoMap.SEX));
		hisPojo.setAge(hisParm.getInt(HisPojoMap.AGE));
		hisPojo.setAgeMonth(hisParm.getInt(HisPojoMap.AGEMONTH));
		hisPojo.setAgeDay(hisParm.getInt(HisPojoMap.AGEDAY));
		hisPojo.setLmpFlg(hisParm.getValue(HisPojoMap.LMPFLG));
		hisPojo.setAllergyFlg(hisParm.getValue(HisPojoMap.ALLERGYFLG));
		hisPojo.setWeight(hisParm.getDouble(HisPojoMap.WEIGHT));
		hisPojo.setCalWeight(hisParm.getDouble(HisPojoMap.CALWEIGHT));
		hisPojo.setNewBornFlg(hisParm.getValue(HisPojoMap.NEWBORNFLG));
		hisPojo.setAdmType(hisParm.getValue(HisPojoMap.ADMTYPE));
		return hisPojo;
		
	}
	
	private List<OrderPojo> parseOnlyOrderPojoToTParm(TParm orderParm){
		
		List<OrderPojo> orderPojos = new ArrayList<OrderPojo>();
		
		OrderPojo orderPojo;
		if(orderParm != null){
			for (int i = 0; i < orderParm.getCount(OrderPojoMap.ID); i++) {
				orderPojo = new OrderPojo();
				orderPojo.setId(orderParm.getValue(OrderPojoMap.ID, i));
				orderPojo.setOrderCode(orderParm.getValue(OrderPojoMap.ORDERCODE, i));
				orderPojo.setOrderDesc(orderParm.getValue(OrderPojoMap.ORDERDESC, i));
				orderPojo.setRxNo(orderParm.getValue(OrderPojoMap.RXNO, i));
				orderPojo.setSeqNo(orderParm.getValue(OrderPojoMap.SEQNO, i));
				orderPojo.setLiquidMainFlg(orderParm.getValue(OrderPojoMap.LIQUIDMAINFLG, i));
				orderPojo.setLiquidNo(orderParm.getValue(OrderPojoMap.LIQUIDNO, i));
				orderPojo.setMedQty(orderParm.getDouble(OrderPojoMap.MEDQTY, i));
				orderPojo.setUnit(orderParm.getValue(OrderPojoMap.UNIT, i));
				orderPojo.setFreqCycle(orderParm.getInt(OrderPojoMap.FREQCYCLE, i));
				orderPojo.setFreqTimes(orderParm.getInt(OrderPojoMap.FREQTIMES, i));
				orderPojo.setTakeDays(orderParm.getInt(OrderPojoMap.TAKEDAYS, i));
				orderPojo.setOrderDate(orderParm.getValue(OrderPojoMap.ORDERDATE, i));
				orderPojo.setSysDate(orderParm.getValue(OrderPojoMap.SYSDATE, i));
				try {
					orderPojo.setOrderDateLong(new Long(orderParm.getValue(OrderPojoMap.ORDERDATELONG, i)));
					orderPojo.setOrderTimeLong(new Long(orderParm.getValue(OrderPojoMap.ORDERTIMELONG, i)));
				} catch (Exception e) {
					// TODO: handle exception
				}
				orderPojo.setSysDateLong(new Long(orderParm.getValue(OrderPojoMap.SYSDATELONG, i)));
				orderPojo.setSysTimeLong(new Long(orderParm.getValue(OrderPojoMap.SYSTIMELONG, i)));
				orderPojo.setOptitemCode(orderParm.getValue(OrderPojoMap.OPTITEMCODE, i));
				orderPojo.setTransHospCode(orderParm.getValue(OrderPojoMap.TRANSHOSPCODE, i));
				orderPojo.setDevCode(orderParm.getValue(OrderPojoMap.DEVCODE, i));
				orderPojos.add(orderPojo);
			}
		}
		
		return orderPojos;
	}
	
	private List<OrderPojo> parseTParmToOnlychestPainOrderPojo(TParm orderParm){
		List<OrderPojo> orderPojos = new ArrayList<OrderPojo>();
		
		OrderPojo orderPojo;
		if(orderParm != null){
			for (int i = 0; i < orderParm.getCount(OrderPojoMap.ID); i++) {
				orderPojo = new OrderPojo();
				orderPojo.setId(orderParm.getValue(OrderPojoMap.ID, i));
				orderPojo.setOrderCode(orderParm.getValue(OrderPojoMap.ORDERCODE, i));
				orderPojo.setSysPhaClass1(orderParm.getValue(OrderPojoMap.SYSPHACLASS1, i));
				orderPojo.setSysPhaClass2(orderParm.getValue(OrderPojoMap.SYSPHACLASS2, i));
				orderPojos.add(orderPojo);
			}
		}
		
		return orderPojos;
	}
	
	
	private List<ExaPojo> parseTParmToOnlyExaPojo(TParm exaParm){
		
		List<ExaPojo> exaPojos = new ArrayList<ExaPojo>();
		
		ExaPojo exaPojo;
		if(exaParm != null){
			for (int i = 0; i < exaParm.getCount(ExaPojoMap.ID); i++) {
				exaPojo = new ExaPojo();
				exaPojo.setId(exaParm.getValue(ExaPojoMap.ID, i));
				exaPojo.setTestitemCode(exaParm.getValue(ExaPojoMap.TESTITEMCODE, i));
				exaPojo.setTestValue(exaParm.getDouble(ExaPojoMap.TESTVALUE, i));
				exaPojo.setTestValueText(exaParm.getValue(ExaPojoMap.TESTVALUETEXT, i));
				exaPojo.setTestUnit(exaParm.getValue(ExaPojoMap.TESTUNIT, i));
				exaPojos.add(exaPojo);
			}
		}
		
		return exaPojos;
	}
	
	private List<ErdPojo> parseTParmToOnlyErdPojo(TParm erdParm){		
		List<ErdPojo> erdPojos = new ArrayList<ErdPojo>();		
		ErdPojo erdPojo;
		if(erdParm != null){
			for (int i = 0; i < erdParm.getCount(ErdPojoMap.ID); i++) {
				erdPojo = new ErdPojo();
				erdPojo.setId(erdParm.getValue(ErdPojoMap.ID, i));
				erdPojo.setMonitorItemEn(erdParm.getValue(ErdPojoMap.MONITORITEMEN, i));
				erdPojo.setMonitorValue(erdParm.getDouble(ErdPojoMap.MONITORVALUE, i));
				erdPojo.setMeasureUnit(erdParm.getValue(ErdPojoMap.MEASUREUNIT, i));
				erdPojos.add(erdPojo);
			}
		}		
		return erdPojos;
	}
	
	private List<AllergyPojo> parseTParmToOnlyAllergyPojo(TParm allergyParm){
		List<AllergyPojo> allergyPojos = new ArrayList<AllergyPojo>();
		AllergyPojo allergyPojo;
		if(allergyParm != null){
			for (int i = 0; i < allergyParm.getCount(AllergyPojoMap.ID); i++) {
				allergyPojo = new AllergyPojo();
				allergyPojo.setId(allergyParm.getValue(AllergyPojoMap.ID, i));
				allergyPojo.setDrugType(allergyParm.getValue(AllergyPojoMap.DRUGTYPE, i));
				allergyPojo.setDrugoringrdCode(allergyParm.getValue(AllergyPojoMap.DRUGORINGRDCODE, i));
				allergyPojos.add(allergyPojo);
			}
		}
		return allergyPojos;
	}
	
	private List<BreakPoint> parseTParmToOnlyBreakPoint(TParm breakPointParm){
		
		List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
		
		BreakPoint breakPoint;
		if(breakPointParm != null){
			for (int i = 0; i < breakPointParm.getCount(BreakPointMap.ID); i++) {
				breakPoint = new BreakPoint();
				breakPoint.setId(breakPointParm.getValue(BreakPointMap.ID, i));
				breakPoint.setMrNo(breakPointParm.getValue(BreakPointMap.MRNO, i));
				breakPoint.setFlowId(breakPointParm.getValue(BreakPointMap.FLOWID, i));
				breakPoint.setNodeId(breakPointParm.getValue(BreakPointMap.NODEID, i));
				breakPoints.add(breakPoint);
			}
		}
		
		return breakPoints;
		
	}
	
	private List<AdvicePojo> parseOnlyAdvicePojoToTParm(TParm adviceParm){
		
		List<AdvicePojo> advicePojos = new ArrayList<AdvicePojo>();
		
		AdvicePojo advicePojo;
		if(adviceParm != null){
			for (int i = 0; i < adviceParm.getCount(AdvicePojoMap.KNOWLADGEID); i++) {
				advicePojo = new AdvicePojo();
				advicePojo.setKnowladgeId(adviceParm.getValue(AdvicePojoMap.KNOWLADGEID, i));
				advicePojo.setLevel(adviceParm.getValue(AdvicePojoMap.LEVEL, i));
				advicePojo.setAdviceText(adviceParm.getValue(AdvicePojoMap.ADVICETEXT, i));
				advicePojo.setMedQty(adviceParm.getDouble(AdvicePojoMap.MEDQTY, i));
				advicePojo.setUnit(adviceParm.getValue(AdvicePojoMap.UNIT, i));
				advicePojo.setFreqCode(adviceParm.getValue(AdvicePojoMap.FREQCODE, i));
				advicePojo.setOrderCode(adviceParm.getValue(AdvicePojoMap.ORDERCODE, i));
				advicePojo.setRxNo(adviceParm.getValue(AdvicePojoMap.RXNO, i));
				advicePojo.setSeqNo(adviceParm.getValue(AdvicePojoMap.SEQNO, i));
				advicePojo.setRemarks(adviceParm.getValue(AdvicePojoMap.REMARKS, i));
				advicePojo.setOrderDesc(adviceParm.getValue(AdvicePojoMap.ORDERDESC, i));
				advicePojos.add(advicePojo);
			}
		}
		
		return advicePojos;
		
	}
	
	private List<ClpAdvicePojo> parseOnlyClpAdvicePojoToTParm(TParm adviceParm){
		
		List<ClpAdvicePojo> clpAdvicePojos = new ArrayList<ClpAdvicePojo>();
		
		ClpAdvicePojo clpAdvicePojo;
		if(adviceParm != null){
			for (int i = 0; i < adviceParm.getCount(ClpAdvicePojoMap.CLNCPATHCODE); i++) {
				clpAdvicePojo = new ClpAdvicePojo();
				clpAdvicePojo.setClncpathCode(adviceParm.getValue(ClpAdvicePojoMap.CLNCPATHCODE, i));
				clpAdvicePojo.setClncpathDesc(adviceParm.getValue(ClpAdvicePojoMap.CLNCPATHDESC, i));
				clpAdvicePojos.add(clpAdvicePojo);
			}
		}
		
		return clpAdvicePojos;
		
	}
	
	public TParm parseClpPojoToTParm(ClpPojo clpPojo){
		TParm adviceParm = new TParm();
		List<ClpAdvicePojo> clpAdvicePojos = clpPojo.getClpAdvicePojos();
		for(ClpAdvicePojo clpAdvicePojo:clpAdvicePojos){
			adviceParm.addData(ClpAdvicePojoMap.CLNCPATHCODE, clpAdvicePojo.getClncpathCode());
			adviceParm.addData(ClpAdvicePojoMap.CLNCPATHDESC, clpAdvicePojo.getClncpathDesc());
			
		}
		return adviceParm;
	}
	
	
	public HisPojo parseJsonToHisPojo(HisPojo hisPojo ,  JSONObject obj ){
		 String jsonS = obj.get("orderPojos").toString();
		 List<OrderPojo> objs = new ArrayList<OrderPojo>();
		 JSONArray jsonArray = JSONArray.fromObject(jsonS);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				OrderPojo obj11 = (OrderPojo) JSONObject.toBean(jsonObject,
						OrderPojo.class);
				objs.add(obj11);
			}
		  hisPojo.setOrderPojos(objs);
		 
		 
		  jsonS = obj.get("lastOrderPojos").toString();
		  objs = new ArrayList<OrderPojo>();
		  jsonArray = JSONArray.fromObject(jsonS);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				OrderPojo obj11 = (OrderPojo) JSONObject.toBean(jsonObject,
						OrderPojo.class);
				objs.add(obj11);
			}
		   hisPojo.setLastOrderPojos(objs);
		   
		   jsonS = obj.get("chestpainOrderPojos").toString();
			  objs = new ArrayList<OrderPojo>();
			  jsonArray = JSONArray.fromObject(jsonS);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					OrderPojo obj11 = (OrderPojo) JSONObject.toBean(jsonObject,
							OrderPojo.class);
					objs.add(obj11);
				}
			   hisPojo.setChestpainOrderPojos(objs);
			 //用于护士单次执行界面
			  jsonS = obj.get("singleExeOrderPojos").toString();
			  objs = new ArrayList<OrderPojo>();
			  jsonArray = JSONArray.fromObject(objs);
			 		for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						OrderPojo obj11 = (OrderPojo) JSONObject.toBean(jsonObject,
								OrderPojo.class);
						objs.add(obj11);
					}
				  hisPojo.setSingleExeOrderPojos(objs);	   
			   
			   
		   
		     String jsonNames = obj.get("exaPojos").toString();
			 List<ExaPojo> objsExaPojo = new ArrayList<ExaPojo>();
			  jsonArray = JSONArray.fromObject(jsonNames);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ExaPojo obj11 = (ExaPojo) JSONObject.toBean(jsonObject,
							ExaPojo.class);
					objsExaPojo.add(obj11);
				}
			 hisPojo.setExaPojos(objsExaPojo);
			 
			 
			  jsonNames = obj.get("lastExaPojos").toString();
			  objsExaPojo = new ArrayList<ExaPojo>();
			  jsonArray = JSONArray.fromObject(jsonNames);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ExaPojo obj11 = (ExaPojo) JSONObject.toBean(jsonObject,
							ExaPojo.class);
					objsExaPojo.add(obj11);
				}
			 hisPojo.setLastExaPojos(objsExaPojo);
			 
			 
			  jsonNames = obj.get("breakPoints").toString();
			 List<BreakPoint> objsBreakPoint = new ArrayList<BreakPoint>();
			  jsonArray = JSONArray.fromObject(jsonNames);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					BreakPoint obj11 = (BreakPoint) JSONObject.toBean(jsonObject,
							BreakPoint.class);
					objsBreakPoint.add(obj11);
				}
			 hisPojo.setBreakPoints(objsBreakPoint);
			 
			 
			  jsonNames = obj.get("advicePojos").toString();
			 List<AdvicePojo> objsAdvicePojo = new ArrayList<AdvicePojo>();
			  jsonArray = JSONArray.fromObject(jsonNames);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					AdvicePojo obj11 = (AdvicePojo) JSONObject.toBean(jsonObject,
							AdvicePojo.class);
					objsAdvicePojo.add(obj11);
				}
			hisPojo.setAdvicePojos(objsAdvicePojo);
			
			
			 
			  jsonNames = obj.get("erdPojos").toString();
			 List<ErdPojo> objsErdPojo = new ArrayList<ErdPojo>();
			  jsonArray = JSONArray.fromObject(jsonNames);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ErdPojo obj11 = (ErdPojo) JSONObject.toBean(jsonObject,
							ErdPojo.class);
					objsErdPojo.add(obj11);
				}
			hisPojo.setErdPojos(objsErdPojo);
			
			
			  jsonNames = obj.get("allergyPojos").toString();
			 List<AllergyPojo> objsAllergyPojo = new ArrayList<AllergyPojo>();
			  jsonArray = JSONArray.fromObject(jsonNames);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					AllergyPojo obj11 = (AllergyPojo) JSONObject.toBean(jsonObject,
							AllergyPojo.class);
					objsAllergyPojo.add(obj11);
				}
			 hisPojo.setAllergyPojos(objsAllergyPojo);
			 
			 return hisPojo;
	}
	

	
	public ClpPojo parseJsonToClpPojo(ClpPojo clpPojo  ,  JSONObject obj ){
		 String jsonS = obj.get("clpAdvicePojos").toString();
		 List<ClpAdvicePojo> objs = new ArrayList<ClpAdvicePojo>();
		 JSONArray jsonArray = JSONArray.fromObject(jsonS);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ClpAdvicePojo obj11 = (ClpAdvicePojo) JSONObject.toBean(jsonObject,
						ClpAdvicePojo.class);
				objs.add(obj11);
			}
			clpPojo.setClpAdvicePojos(objs);
		 

			 
			 return clpPojo;
	}
	//用于护士单次执行界面
	private TParm parseOnlysingleExeOrderPojoToTParm(List<OrderPojo> orderPojos){
		TParm orderParm = new TParm();
		for (OrderPojo orderPojo : orderPojos) {
			orderParm.addData(OrderPojoMap.ID, orderPojo.getId());
			orderParm.addData(OrderPojoMap.ORDERCODE, orderPojo.getOrderCode());
			orderParm.addData(OrderPojoMap.ORDERDESC, orderPojo.getOrderDesc());
		}
		return orderParm;
	}
	private List<OrderPojo> parseOnlysingleExeOrderPojoToTParm(TParm orderParm){		
		List<OrderPojo> orderPojos = new ArrayList<OrderPojo>();		
		OrderPojo orderPojo;
		if(orderParm != null){
			for (int i = 0; i < orderParm.getCount(OrderPojoMap.ID); i++) {
				orderPojo = new OrderPojo();
				orderPojo.setId(orderParm.getValue(OrderPojoMap.ID, i));
				orderPojo.setOrderCode(orderParm.getValue(OrderPojoMap.ORDERCODE, i));
				orderPojo.setOrderDesc(orderParm.getValue(OrderPojoMap.ORDERDESC, i));
				orderPojos.add(orderPojo);
			}
		}		
		return orderPojos;
	}
}
