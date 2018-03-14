package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.DeptTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import device.PassDriver;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title:合理用药工具类
 * </p>
 * 
 * <p>
 * Description:合理用药工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JAVAHIS
 * </p>
 * 
 * @author SUNDX
 * @version 1.0
 */
public class PassTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static PassTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return OrderTool
	 */
	public static PassTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new PassTool();
		}
		return instanceObject;
	}

	/**
	 * 构造方法
	 */
	public PassTool() {
		setModuleName("pha\\PassToolModule.x");
		onInit();
	}

	/**
	 * 初始化合理用药接口
	 */
	public boolean init() {
		try {
			int j = PassDriver.init();
			if (j == 0) {
				return false;
			}
			j = PassDriver.PassInit(
					Operator.getID() + "/" + Operator.getName(),
					Operator.getDept()
							+ "/"
							+ DeptTool.getInstance().getDescByCode(
									Operator.getDept()), 20);
			if (j == 0) {
				return false;
			}
			j = PassDriver.PassSetControlParam(1, 2, 0, 2, 1);
			if (j == 0) {
				return false;
			}
		} catch (UnsatisfiedLinkError e1) {
			e1.printStackTrace();
			return false;
		} catch (NoClassDefFoundError e2) {
			e2.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 设置门诊病患基本信息
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean setPatientInfo(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm = query("queryPatInfo", parm);
		if (parm.getErrCode() < 0) {
			return false;
		}
		TParm visitCountParm = new TParm();
		visitCountParm.setData("MR_NO", parm.getValue("MR_NO", 0));
		visitCountParm = query("queryVisitCount", visitCountParm);
		//System.out.println("visitCountParm" + visitCountParm);
		if (visitCountParm.getErrCode() < 0) {
			return false;
		}
        //System.out.println("-=-------------------"+parm.getValue("BIRTH_DATE",0).substring(0, 10));
		PassDriver.PassSetPatientInfo(
				parm.getValue("MR_NO", 0),
				visitCountParm.getValue("VISIT_COUNT", 0),
				parm.getValue("PAT_NAME", 0),
				parm.getValue("SEX_DESC", 0),
				parm.getValue("BIRTH_DATE",0).substring(0, 10),
				parm.getValue("WEIGHT", 0),
				parm.getValue("HEIGHT", 0),
				parm.getValue("REALDEPT_CODE", 0) + "/"
						+ parm.getValue("DEPT_CHN_DESC", 0),
				parm.getValue("REALDR_CODE", 0) + "/"
						+ parm.getValue("USER_NAME", 0), "");
		return true;
	}

	/**
	 * 设置住院病患基本信息
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean setadmPatientInfo(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm = query("queryadmPatInfo", parm);
		if (parm.getErrCode() < 0) {
			return false;
		}
		TParm visitCountParm = new TParm();
		visitCountParm.setData("MR_NO", parm.getValue("MR_NO", 0));
		visitCountParm = query("queryadmVisitCount", visitCountParm);
//		System.out.println("-------visitCountParm------------"+visitCountParm);
		if (visitCountParm.getErrCode() < 0) {
			return false;
		}
//		System.out.println("-=-------------------"+parm.getValue("BIRTH_DATE",0).substring(0, 10));
		PassDriver
				.PassSetPatientInfo(
						parm.getValue("MR_NO",0),
						visitCountParm.getValue("VISIT_COUNT",0),
						parm.getValue("PAT_NAME",0),
						parm.getValue("SEX_DESC",0),
						parm.getValue("BIRTH_DATE",0).substring(0, 10),
						parm.getValue("WEIGHT",0),
						parm.getValue("HEIGHT",0),
						parm.getValue("REALDEPT_CODE",0) + "/"
								+ parm.getValue("DEPT_CHN_DESC",0),
						parm.getValue("REALDR_CODE",0) + "/"
								+ parm.getValue("USER_NAME",0), "");
		return true;
	}

	/**
	 * 设置过敏史
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean setAllergenInfo(String mrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm = query("queryAllergy", parm);
		if (parm.getErrCode() < 0) {
			return false;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			PassDriver.PassSetAllergenInfo(parm.getValue("CODE", i) + i,
					parm.getValue("CODE", i), parm.getValue("ORDER_DESC", i),
					"USER_MedCond", parm.getValue("NOTE", i));
		}
		return true;
	}

	/**
	 * 传入门诊病生状态
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean setMedCond(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm = query("queryDiag", parm);
		if (parm.getErrCode() < 0) {
			return false;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			PassDriver.PassSetMedCond(parm.getValue("CODE", i) + i,
					parm.getValue("CODE", i), parm.getValue("ICD_CHN_DESC", i),
					"USER_MedCond", "", "");
		}
		return true;
	}

	/**
	 * 传入住院病生状态
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean setadmMedCond(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm = query("queryamdDiag", parm);
//		System.out.println("queryamdDiag" + parm);
		if (parm.getErrCode() < 0) {
			return false;
		}
		for (int i = 0; i < parm.getCount(); i++) {
//			System.out.println("code" + parm.getValue("CODE", i) + i);
//			System.out.println("ICD_CHN_DESC"
//					+ parm.getValue("ICD_CHN_DESC", i));
			PassDriver.PassSetMedCond(parm.getValue("CODE", i) + i,
					parm.getValue("CODE", i), parm.getValue("ICD_CHN_DESC", i),
					"USER_MedCond", "", "");
		}
		return true;
	}

	/**
	 * 传入门诊药品信息
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return boolean
	 */
	public TParm setRecipeInfo(String caseNo, String opdOrdercat) {
		String opdsql = " SELECT A.RX_NO,A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,B.FREQ_TIMES||'/'||B.CYCLE FREQ,"
				+ " A.ORDER_DATE,A.TAKE_DAYS,C.ROUTE_CHN_DESC,A.LINK_NO,"
				+ " D.USER_ID||'/'||D.USER_NAME DR "
				+ " FROM   OPD_ORDER A,SYS_PHAFREQ B,SYS_PHAROUTE C,SYS_OPERATOR D,SYS_UNIT F "
				+ " WHERE  A.CASE_NO ='"
				+ caseNo
				+ "'"
				+ " AND    A.CAT1_TYPE = 'PHA' "
				+ " AND    A.FREQ_CODE = B.FREQ_CODE(+)  "
				+ " AND    A.ROUTE_CODE = C.ROUTE_CODE (+) "
				+ " AND    A.DR_CODE = D.USER_ID (+) "
				+ " AND    A.MEDI_UNIT = F.UNIT_CODE(+) "
				+ " AND "
				+ opdOrdercat;
//		System.out.println("opdsql ============= " + opdsql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(opdsql));
//		System.out.println("parm = " + parm);
		if (parm.getErrCode() < 0) {
			return null;
		}
		String date = StringTool.getString(
				TJDODBTool.getInstance().getDBTime(), "yyyy-MM-dd");
		for (int i = 0; i < parm.getCount(); i++) {
//			System.out.println("ORDER_DESC" + parm.getValue("ORDER_DESC", i));
			Timestamp startDate = (Timestamp) parm.getData("ORDER_DATE", i);
			String startDateString = StringTool.getString(startDate,
					"yyyy-MM-dd");
			long endDateLong = startDate.getTime()
					+ (parm.getInt("TAKE_DAYS", i) - 1) * 24 * 60 * 60 * 1000;
			Timestamp endDate = new Timestamp(endDateLong);
			String endDateString = StringTool.getString(endDate, "yyyy-MM-dd");
			PassDriver.PassSetRecipeInfo(
					parm.getValue("RX_NO", i) + parm.getValue("SEQ_NO", i),
					parm.getValue("ORDER_CODE", i),
					parm.getValue("ORDER_DESC", i),
					parm.getValue("MEDI_QTY", i),
					parm.getValue("UNIT_CHN_DESC", i),
					parm.getValue("FREQ", i), date, date,
					parm.getValue("ROUTE_CHN_DESC", i),
					parm.getValue("LINK_NO", i), "1", parm.getValue("DR", i));
		}
		return parm;
	}

	/**
	 * 传入住院药房药品信息
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return boolean
	 */
	public TParm setadmRecipeInfo(String caseNo, String odiOrdercat, String typeFlg) {
		String typeSql = "";
		if (typeFlg.equals("UDST"))
			typeSql = " AND (A.RX_KIND='ST' OR A.RX_KIND='UD')";
		else if (typeFlg.equals("DS"))
			typeSql = " AND A.RX_KIND='DS' ";

		String odisql = " SELECT A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,B.FREQ_TIMES||'/'||B.CYCLE FREQ,"
				+ " A.ORDER_DATE,A.TAKE_DAYS,C.ROUTE_CHN_DESC,A.LINK_NO,A.RX_KIND,"
				+ " D.USER_ID||'/'||D.USER_NAME DR,A.EFF_DATE "
				+ " FROM   ODI_ORDER A,SYS_PHAFREQ B,SYS_PHAROUTE C,SYS_OPERATOR D,SYS_UNIT F "
				+ " WHERE  A.CASE_NO ='"
				+ caseNo
				+ "'"
				+ " AND    A.CAT1_TYPE = 'PHA' "
				+ typeSql
				+ " AND    A.DC_DATE IS NULL "
				+ " AND    A.FREQ_CODE = B.FREQ_CODE "
				+ " AND    A.ROUTE_CODE = C.ROUTE_CODE "
				+ " AND    A.VS_DR_CODE = D.USER_ID "
				+ " AND    A.MEDI_UNIT = F.UNIT_CODE " + " AND " + odiOrdercat;
//		System.out.println("odisql ============= " + odisql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(odisql));
//		System.out.println("setadmRecipeInfo parm = " + parm);
		if (parm.getErrCode() < 0) {
			return null;
		}
		String date = StringTool.getString(
				TJDODBTool.getInstance().getDBTime(), "yyyy-MM-dd");
		Timestamp d = SystemTool.getInstance().getDate();
		String nowtime = ("" + d).substring(0, 10).replaceAll("-", "");
		long nowStr = d.getTime();
		long startStr = StringTool.getTimestamp(nowtime + "000000",
				"yyyyMMddHHmmss").getTime();
		for (int i = 0; i < parm.getCount(); i++) {
			String effdate = parm.getValue("EFF_DATE",i);
			if (!effdate.equals("")) {
				long leffDate = strToDate(parm.getValue("EFF_DATE",i).substring(0, 19).replaceAll("-", "/"),
						"yyyy/MM/dd HH:mm:ss").getTime();
				if (parm.getValue("RX_KIND",i).equals("ST")
						&& leffDate < startStr)
					continue;
			}
			Timestamp startDate = (Timestamp) parm.getData("ORDER_DATE", i);
//			System.out.println( parm.getValue("ORDER_SEQ", i)+"-----------------------"+parm.getRow(i));
			String startDateString = StringTool.getString(startDate,
					"yyyy-MM-dd");
			long endDateLong = startDate.getTime()
					+ (parm.getInt("TAKE_DAYS", i) - 1) * 24 * 60 * 60 * 1000;
			Timestamp endDate = new Timestamp(endDateLong);
			String endDateString = StringTool.getString(endDate, "yyyy-MM-dd");
			String type = "UD".equals(parm.getValue("RX_KIND")) ? "0" : "1";
			PassDriver.PassSetRecipeInfo(
					parm.getValue("ORDER_NO", i)
							+ StringTool.fill0(parm.getValue("SEQ_NO",i) + "", 3)
				    + parm.getValue("ORDER_CODE"),
					parm.getValue("ORDER_CODE", i),
					parm.getValue("ORDER_DESC", i),
					parm.getValue("MEDI_QTY", i),
					parm.getValue("UNIT_CHN_DESC", i),
					parm.getValue("FREQ", i), date, date,
					parm.getValue("ROUTE_CHN_DESC", i),
					"1", type, parm.getValue("DR", i));
		}
		return parm;
	}

	/**
	 * 门诊药房检核药品自动
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm setRecipeInfoAuto(String caseNo, String opdOrdercat) {
		TParm parm = setRecipeInfo(caseNo, opdOrdercat);
		PassDriver.PassDoCommand(33);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("RX_NO", parm.getValue("RX_NO", i));
			result.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
			result.addData(
					"FLG",
					PassDriver.PassGetWarn1(parm.getValue("RX_NO", i)
							+ parm.getValue("SEQ_NO", i)));
		}
		return result;
	}

	/**
	 * 住院药房检核药品自动
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm setadmRecipeInfoAuto(String caseNo, String odiOrdercat,
			String type) {
		TParm parm = setadmRecipeInfo(caseNo, odiOrdercat, type);
		PassDriver.PassDoCommand(1);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			result.addData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
			result.addData(
					"FLG",
					PassDriver.PassGetWarn1(parm.getValue("ORDER_NO", i)
							+ parm.getValue("ORDER_SEQ", i)));
		}
		return result;
	}

	/**
	 * 查询药品
	 * 
	 * @param orderCode
	 *            String
	 */
	public void setQueryDrug(String orderCode, int commandNo) {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", orderCode);
		parm = query("queryDrug", parm);
		PassDriver.PassSetQueryDrug(parm.getValue("ORDER_CODE", 0),
				parm.getValue("ORDER_DESC", 0),
				parm.getValue("UNIT_CHN_DESC", 0),
				parm.getValue("ROUTE_CHN_DESC", 0));
		PassDriver.PassDoCommand(commandNo);

	}

	/**
	 * 门诊单药品警告检核
	 * 
	 * @param rxNo
	 *            String
	 * @param seqNo
	 *            String
	 */
	public void setWarnDrug1(String rxNo, String seqNo) {
		PassDriver.PassSetWarnDrug1(rxNo + seqNo);
		PassDriver.PassDoCommand(6);
	}

	/**
	 * 住院单药品警告检核
	 * 
	 * @param rxNo
	 *            String
	 * @param seqNo
	 *            String
	 */
	public void setWarnDrug2(String orderNo, String seq) {
		PassDriver.PassSetWarnDrug1(orderNo + seq);
		PassDriver.PassDoCommand(6);
	}

	/**
	 * 门诊检核药品手工
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm setRecipeInfoHand(String caseNo, String opdOrdercat) {
		TParm parm = setRecipeInfo(caseNo, opdOrdercat);
		PassDriver.PassDoCommand(3);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("RX_NO", parm.getValue("RX_NO", i));
			result.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
			result.addData(
					"FLG",
					PassDriver.PassGetWarn1(parm.getValue("RX_NO", i)
							+ parm.getValue("SEQ_NO", i)));
		}
		return result;
	}

	/**
	 * 住院检核药品手工
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm setadmRecipeInfoHand(String caseNo, String odiOrdercat,String typeFlg) {
		TParm parm = setadmRecipeInfo(caseNo, odiOrdercat,typeFlg);
		PassDriver.PassDoCommand(3);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			result.addData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
			result.addData(
					"FLG",
					PassDriver.PassGetWarn1(parm.getValue("ORDER_NO", i)
							+ parm.getValue("ORDER_SEQ", i)));
		}
		return result;
	}
	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate, String forMat) {
		SimpleDateFormat formatter = new SimpleDateFormat(forMat);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
}
