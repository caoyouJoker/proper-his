package jdo.inw;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import jdo.ibs.IBSTool;
import jdo.opd.TotQtyTool;
import com.dongyang.jdo.TJDODBTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TNull;
import java.util.Map;
import com.dongyang.util.TypeTool;
import jdo.bil.BIL;
import jdo.adm.ADMInpTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 住院护士站审核主Tool
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class InwOrderCheckTool extends TJDOTool {

	/**
	 * 实例
	 */
	private static InwOrderCheckTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static InwOrderCheckTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwOrderCheckTool();
		return instanceObject;
	}

	public InwOrderCheckTool() {
	}

	/**
	 * 生成取药单号,取药单号生成 统药单号共11位,编号方式为西元年2位+月2位+日2位+流水号5位,
	 * 
	 * @return String
	 */
	static public String getTakeMedNo() {
		return SystemTool.getInstance().getNo("ALL", "UDD", "TAKEMED_NO",
				"TAKEMED_NO");
	}

	/**
	 * 取消审核
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUndoCheck(TParm parm, TConnection connection) {
		TParm result = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		// 前台传的数据
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			// 取消动作
			TParm undoData = parm.getRow(i);
			undoData.setData("DC_NS_CHECK_DATE", now);
			undoData.setData("NS_CHECK_DATE", new TNull(Timestamp.class));
			undoData.setData("NS_CHECK_CODE", new TNull(String.class));
			result = undoData(undoData, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * 单笔数据提交
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm undoData(TParm parm, TConnection connection) {
		TParm undoData = new TParm();
		TParm result = new TParm();
		undoData.setData("CASE_NO", parm.getData("CASE_NO"));
		undoData.setData("ORDER_NO", parm.getData("ORDER_NO"));
		undoData.setData("ORDER_SEQ", parm.getData("ORDER_SEQ"));
		undoData.setData("OPT_USER", parm.getData("OPT_USER"));
		undoData.setData("OPT_TERM", parm.getData("OPT_TERM"));
		undoData.setData("OPT_DATE", parm.getData("OPT_DATE"));
		undoData.setData("DC_NS_CHECK_DATE", parm.getData("DC_NS_CHECK_DATE"));
		result = InwForOdiTool.getInstance().delOdiDspndForUndoCk(undoData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = InwForOdiTool.getInstance().delOdiDspnmForUndoCk(undoData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 取消审核的时候只清空审核标记
		undoData.setData("NS_CHECK_DATE", parm.getData("NS_CHECK_DATE"));
		undoData.setData("NS_CHECK_CODE", parm.getData("NS_CHECK_CODE"));
		result = InwForOdiTool.getInstance().updateOdiOrderForUndoCk(undoData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新TakeMedOrg
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm upDateTakeMedOrg(TParm parm, TConnection connection) {
		TParm doData = new TParm();
		TParm result = new TParm();
		doData.setData("CASE_NO", parm.getData("CASE_NO"));
		doData.setData("ORDER_NO", parm.getData("ORDER_NO"));
		doData.setData("ORDER_SEQ", parm.getData("ORDER_SEQ"));
		doData.setData("OPT_USER", parm.getData("OPT_USER"));
		doData.setData("OPT_TERM", parm.getData("OPT_TERM"));
		doData.setData("OPT_DATE", parm.getData("OPT_DATE"));
		doData.setData("DC_NS_CHECK_DATE", parm.getData("DC_NS_CHECK_DATE"));
		doData.setData("TAKEMED_ORG", parm.getData("TAKEMED_ORG"));
		doData.setData("TAKEMED_NO", parm.getData("TAKEMED_NO"));
		doData.setData("START_DTTM", parm.getData("START_DTTM"));
		doData.setData("END_DTTM", parm.getData("END_DTTM"));
		result = InwForOdiTool.getInstance().updateOdiOrdertakeMedOrg(doData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = InwForOdiTool.getInstance().updateOdiDspnmtakeMedOrg(doData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = InwForOdiTool.getInstance().updateOdiDspnDtakeMedOrg(doData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 审核主方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm CASE_NO OPT_USER OPT_DATE OPT_TERM
	 */
	public TParm onCheck(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// 前台传的数据
		TParm dataParm = parm.getParm("dataParm");
		String optUser = (String) parm.getData("OPT_USER");
		Timestamp optDate = (Timestamp) parm.getData("OPT_DATE");
		String optTerm = (String) parm.getData("OPT_TERM");
		String takeMedOrg = "";
		// shibl 20121022 modify 利用caseNo将护士审核的医嘱按照就诊号分组
		Map pat = InwOrderExecTool.getInstance().groupByPatParm(dataParm);
		Iterator it = pat.values().iterator();
		while (it.hasNext()) {
			// 取药单
			String takeMedNo = "";
			Map takeMedNoMap = new HashMap();
			TParm patParm = (TParm) it.next();
			for (int i = 0; i < patParm.getCount(); i++) {
				// 支持多人审核
				// 循环1--医嘱：循环需要审核的遗嘱 PS:当为集合医嘱的时候返回主细项
				// 根据前台的CASE_NO,ORDER_NO,ORDER_SEQ找出一条医嘱，根据SETMAIN_FLG区分是否为集合医嘱
				TParm orderPram = getAnOrder(
						patParm.getData("CASE_NO", i) + "",
						patParm.getData("ORDER_NO", i) + "",
						patParm.getData("ORDER_SEQ", i) + "",
						patParm.getData("SETMAIN_FLG", i) + "",
						patParm.getData("ORDERSETGROUP_NO", i) + "");
				// 取药注记
				takeMedOrg = patParm.getValue("TAKEMED_ORG", i);
				// 记录当前UI显示的审核时间--由于会手动修改该时间
				Timestamp checkDate = patParm.getTimestamp("CHECK_DATETIME", i);
				// 循环每个病患的每一条长期处置数据，调用UDD接口生成需要展开的数据插入ODI_DSPND ODI_DSPNM
				int countToM = orderPram.getCount();
				// System.out.println("==需要插入M表的数据行数===" + countToM);
				// 该病人没有长期处置
				if (countToM <= 0)
					continue;
				TParm parmCase = new TParm();
				parmCase.setData("CASE_NO", patParm.getData("CASE_NO") + "");
				parmCase = ADMInpTool.getInstance().selectall(parmCase);
				String serviceLevel = parmCase.getValue("SERVICE_LEVEL", 0);
				// 循环2--集合医嘱/普通医嘱：(与M表1:1)数循环调用每一条医嘱的UDD接口，返回应该展开的数据
				for (int row_M = 0; row_M < countToM; row_M++) {
					// 数据库中的一条数据--orderPram
					String caseNo = orderPram.getData("CASE_NO", row_M) + "";
					// System.out.println("=医嘱==某个病人的医嘱====>"+caseNo);
					String orderNo = orderPram.getData("ORDER_NO", row_M) + "";
					String orderSeq = orderPram.getData("ORDER_SEQ", row_M)
							+ "";
					// 记录该医嘱的RX_KIND（UD-长期；ST-临时：交给UDD接口处理后返回）/（DS-出院带药：直接1：1：1展开）
					String orderRxKind = orderPram.getData("RX_KIND", row_M)
							+ "";
					// PS：分支——(1)UD/ST (2)DS (3)IG
					if ("UD".equals(orderRxKind) || "ST".equals(orderRxKind)
							|| "OP".equals(orderRxKind)) {// WANGJC
						// orderToUddParm记录给UDD接口的所需要的参数
						TParm orderToUddParm = new TParm();
						orderToUddParm = orderPram.getRow(row_M); // 一条ODI_ORDER的数据
						// System.out.println("==orderToUddParm111111=="
						// + orderToUddParm);

						// 调用UDD的接口得到相应的参数:传入的时候没有秒，回来的时候有秒
						String effDate = StringTool.getString(
								(Timestamp) orderToUddParm.getData("EFF_DATE"),
								"yyyyMMddHHmm");
						/******************* SHIBL ADD ******************************/
						// 启用时间调用UDD接口得到下一个摆药时间餐次点
						List effdispenseDttm = TotQtyTool.getInstance()
								.getNextDispenseDttm(
										(Timestamp) orderToUddParm
												.getData("EFF_DATE"));
						// 停用时间调用UDD接口得到下一个摆药时间餐次点
						Timestamp Dcdate = (Timestamp) orderToUddParm
								.getData("DC_DATE");
						List DCdispenseDttm = null;
						if (orderToUddParm.getData("DC_DATE") != null) {
							DCdispenseDttm = TotQtyTool.getInstance()
									.getNextDispenseDttm(Dcdate);
						}
						/******************* SHIBL ADD ******************************/
						Timestamp sysDate = TJDODBTool.getInstance()
								.getDBTime();
						// 调用UDD接口得到下一个摆药时间餐次点
						List dispenseDttm = TotQtyTool.getInstance()
								.getNextDispenseDttm(checkDate);
						// 如果没有下一餐时间点就继续循环
						if (dispenseDttm.size() == 0) {
							System.out
									.println(" WARNING：There is not the 'next DispenseDttm' here ");
							continue;
						}
						// ODI_ORDER表中的LAST_DSPN_DATE
						Timestamp lastDspnDateForOrder;
						// 判断是药嘱还是处置-----false
						String cat1Type = (String) orderToUddParm
								.getData("CAT1_TYPE");
						// 是否为处置抑或药嘱标记位
						boolean isOperation = "TRT".equalsIgnoreCase(cat1Type)
								|| "PLN".equalsIgnoreCase(cat1Type)
								|| "OTH".equalsIgnoreCase(cat1Type);
						// dataFromUdd用来接收从UDD返回来的数据需要展开的
						List dataFromUdd;
						String startDttmInDspnm = ""; // 插入ODI_DSPNM中的START_DTTM
						String endDttmInDspnm = ""; // 插入ODI_DSPNM中的END_DTTM
						// 返回值包括两个[(1)list--该size就是D表该插入的行数；(2)Map--是需要的数据]--PS:如果是处置参数3:effDate的前8位+0000
						// 参数4:effDate的前8位+235959
						if (!isOperation) { // 药品
							// 当前操作时间
							// startDttmInDspnm = StringTool.getString(optDate,
							// "yyyyMMddHHmm");
							// 取药单号取号原则 shibl 20121121 add
							if ((orderRxKind.equals("UD")
									|| orderRxKind.equals("ST") || orderRxKind
										.equals("OP"))// WANGJC
									&& takeMedOrg.equals("1")) {
								if (takeMedNo.equals("")) {
									takeMedNo = this.getTakeMedNo();
									takeMedNoMap.put(caseNo + orderNo
											+ orderSeq, takeMedNo);
								} else {
									takeMedNoMap.put(caseNo + orderNo
											+ orderSeq, takeMedNo);
								}
							}
							/******************* SHIBL MODIFY ******************************/
							if ("UD".equals(orderRxKind)) {
								String str1 = effdispenseDttm.get(0).toString();
								String Str2 = dispenseDttm.get(0).toString();
								String optStr = StringTool.getString(optDate,
										"yyyyMMddHHmm");
								String dcStr = StringTool.getString(
										(Timestamp) orderToUddParm
												.getData("DC_DATE"),
										"yyyyMMddHHmm");
								// 未审核的医嘱
								if (orderToUddParm.getValue("NS_CHECK_CODE")
										.equals("")) {
									dataFromUdd = TotQtyTool
											.getInstance()
											.getOdiStQty(
													effDate, // EFF_DATE
													StringTool
															.getString(
																	(Timestamp) orderToUddParm
																			.getData("DC_DATE"),
																	"yyyyMMddHHmm"), // DC_DATE
													dispenseDttm.get(0)
															.toString(), // 下一次摆药时间START_DTTM
													dispenseDttm.get(1)
															.toString(), // 下一次摆药时间END_DTTM
													orderToUddParm,
													serviceLevel); // 整条数据
									// 首餐时间
									startDttmInDspnm = effDate;
									// 审核在摆药之后
									if (str1.compareTo(Str2) < 0) {
										// 下一次摆药时间
										lastDspnDateForOrder = StringTool
												.getTimestamp(str1,
														"yyyyMMddHHmm");
									} else {
										lastDspnDateForOrder = optDate;
									}
								}
								// 审核DC医嘱
								else {
									dataFromUdd = TotQtyTool
											.getInstance()
											.getOdiStQty(
													effDate, // EFF_DATE
													StringTool
															.getString(
																	(Timestamp) orderToUddParm
																			.getData("DC_DATE"),
																	"yyyyMMddHHmm"), // DC_DATE
													DCdispenseDttm.get(0)
															.toString(), // 下一次摆药时间START_DTTM
													DCdispenseDttm.get(1)
															.toString(), // 下一次摆药时间END_DTTM
													orderToUddParm,
													serviceLevel); // 整条数据
									startDttmInDspnm = effDate;
									// DC时间小于启用时间的下一次摆药时间
									// if (dcStr.compareTo(str1) < 0) {
									// startDttmInDspnm = effDate;
									// } else {
									// // 配药首餐时间取上一次摆药时间
									// startDttmInDspnm = StringTool.getString(
									// (Timestamp) orderToUddParm
									// .getData("LAST_DSPN_DATE"),
									// "yyyyMMddHHmm");
									// }
									lastDspnDateForOrder = optDate;
								}
								/******************* SHIBL MODIFY ******************************/
								// 下一摆药时间
								endDttmInDspnm = dispenseDttm.get(0).toString();
							} else {
								dataFromUdd = TotQtyTool
										.getInstance()
										.getOdiStQty(
												effDate, // EFF_DATE
												StringTool
														.getString(
																(Timestamp) orderToUddParm
																		.getData("DC_DATE"),
																"yyyyMMddHHmm"), // DC_DATE
												dispenseDttm.get(0).toString(), // 下一次摆药时间START_DTTM
												dispenseDttm.get(1).toString(), // 下一次摆药时间END_DTTM
												orderToUddParm, serviceLevel); // 整条数据
								// 首餐时间
								startDttmInDspnm = effDate;
								// 下一摆药时间
								endDttmInDspnm = effDate;
								lastDspnDateForOrder = optDate;
							}
						} else { // 处置
							// 处置调用新的UDD接口方法
							// 20120511 shibl modify 添加临时分支
							if ("UD".equals(orderRxKind)) {
								dataFromUdd = TotQtyTool.getInstance()
										.getOdiTrtStQty(
												orderToUddParm,
												StringTool.getString(optDate,
														"yyyyMMddHHmm"),
												serviceLevel);
								// System.out.println("处置-==----dataFromUdd--------------"
								// + dataFromUdd);
								if (dataFromUdd == null
										|| dataFromUdd.get(2) == null) {
									result.setErrCode(-1);
									err("ERR:" + result.getErrCode()
											+ "请检查审核医嘱的频次用量");
									return result;
								}
								if (((List) dataFromUdd.get(2)).size() > 0)
									startDttmInDspnm = ((List) dataFromUdd
											.get(2)).get(0).toString();
								// 正流程（非DC）有两个时间——start end
								if (((List) dataFromUdd.get(2)).size() > 1) {
									endDttmInDspnm = ((List) dataFromUdd.get(2))
											.get(1).toString();
									// 下一次摆药时间
									lastDspnDateForOrder = (Timestamp) ((Map) dataFromUdd
											.get(1))
											.get("ORDER_LAST_DSPN_DATE");
								} else {
									lastDspnDateForOrder = new Timestamp(0L);
								}
							} else {
								dataFromUdd = TotQtyTool
										.getInstance()
										.getOdiTrtStQty(
												orderToUddParm,
												StringTool
														.getString(
																(Timestamp) orderToUddParm
																		.getData("EFF_DATE"),
																"yyyyMMddHHmm"),
												serviceLevel);
								// 首餐时间
								startDttmInDspnm = effDate;
								// 下一摆药时间
								endDttmInDspnm = effDate;
								lastDspnDateForOrder = optDate;
							}
						}
						// 应该插入ODI_DSPND的行数从UDD返回的list的SIZE
						List orderDateTimeFromUdd = (List) dataFromUdd.get(0);
						int insertDCount = orderDateTimeFromUdd.size(); // 审核的时候是插入D表的行数——审核DC的时候是更新D表的行数
						// System.out.println("insertDCount = " + insertDCount);
						// 当没有首日量的时候，即D表展开的行数为0就不插M/D表
						if (insertDCount != 0) {
							// PS:非零的时候向M/D插入数据
							// 得到需要插到表中的数据：(PS:order表中的数据都在ODI中插入)
							// ORDER表的LASTDSPN_QTY ORDER_LASTDSPN_QTY
							// ORDER表的ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
							// ORDER表的ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
							// M/ORDER表的dispenseQty M_DISPENSE_QTY
							// M/ORDER表的dispenseUnit M_DISPENSE_UNIT
							// M/ORDER表的dosageQty M_DOSAGE_QTY
							// M/ORDER表的dosageUnit M_DOSAGE_UNIT
							// M表的OWN_PRICE OWN_PRICE
							// M表的NHI_PRICE NHI_PRICE
							// M表的OWN_AMT OWN_AMT
							// M表的TOT_AMT TOT_AMTM
							// M表的DISCOUNT_RATE DISCOUNT_RATE
							// D表的TOT_AMT TOT_AMTD
							// D表的MediQty D_MEDI_QTY
							// D表的MediUnit D_MEDI_UNIT
							// D表的dosageQty D_DOSAGE_QTY
							// D表的dosageUnit D_DOSAGE_UNIT
							// 得到返回的UDD返回的值
							Map otherData = (Map) dataFromUdd.get(1);
							double OlastDspnQty = (Double) otherData
									.get("ORDER_LASTDSPN_QTY") == null ? 0.0
									: (Double) otherData
											.get("ORDER_LASTDSPN_QTY");
							double OacumDspnQty = (Double) otherData
									.get("ORDER_ACUMDSPN_QTY") == null ? 0.0
									: (Double) otherData
											.get("ORDER_ACUMDSPN_QTY");
							double OacumMediQty = (Double) otherData
									.get("ORDER_ACUMMEDI_QTY") == null ? 0.0
									: (Double) otherData
											.get("ORDER_ACUMMEDI_QTY");
							double MdispenseQty = (Double) otherData
									.get("M_DISPENSE_QTY") == null ? 0.0
									: (Double) otherData.get("M_DISPENSE_QTY");
							String MdispenseUnit = (String) otherData
									.get("M_DISPENSE_UNIT");
							double MdosageQty = (Double) otherData
									.get("M_DOSAGE_QTY") == null ? 0.0
									: (Double) otherData.get("M_DOSAGE_QTY");
							String MdosageUnit = (String) otherData
									.get("M_DOSAGE_UNIT");
							double ownPrice = (Double) otherData
									.get("OWN_PRICE") == null ? 0.0
									: (Double) otherData.get("OWN_PRICE");
							double nhiPrice = (Double) otherData
									.get("NHI_PRICE") == null ? 0.0
									: (Double) otherData.get("NHI_PRICE");
							double ownAmt = (Double) otherData.get("OWN_AMT") == null ? 0.0
									: (Double) otherData.get("OWN_AMT");
							double totAmtM = (Double) otherData.get("TOT_AMTM") == null ? 0.0
									: (Double) otherData.get("TOT_AMTM");
							double totAmtD = (Double) otherData.get("TOT_AMTD") == null ? 0.0
									: (Double) otherData.get("TOT_AMTD");
							double discountRate = (Double) otherData
									.get("DISCOUNT_RATE") == null ? 0.0
									: (Double) otherData.get("DISCOUNT_RATE");
							double DmediQty = (Double) otherData
									.get("D_MEDI_QTY") == null ? 0.0
									: (Double) otherData.get("D_MEDI_QTY");
							String DmediUnit = (String) otherData
									.get("D_MEDI_UNIT");
							double DdosageQty = (Double) otherData
									.get("D_DOSAGE_QTY") == null ? 0.0
									: (Double) otherData.get("D_DOSAGE_QTY");
							String DdosageUnit = (String) otherData
									.get("D_DOSAGE_UNIT");
							// 是否存在且审核医嘱(非DC) SHIBL MODIFY 20120424
							if (orderToUddParm.getValue("NS_CHECK_CODE")
									.equals("")) {
								// 判断是否存在取消审核记录
								if (checkIsNSDCExistDspnm(caseNo, orderNo,
										orderSeq)) {
									// 审核动作
									TParm doData = new TParm();
									doData.setData("CASE_NO", caseNo);
									doData.setData("ORDER_NO", orderNo);
									doData.setData("ORDER_SEQ", orderSeq);
									doData.setData("OPT_USER", optUser);
									doData.setData("OPT_TERM", optTerm);
									doData.setData("OPT_DATE", optDate);
									doData.setData("DC_NS_CHECK_DATE",
											new TNull(Timestamp.class));
									doData.setData("NS_CHECK_DATE", optDate);
									doData.setData("NS_CHECK_CODE", optUser);
									result = undoData(doData, connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
									doData.setData("TAKEMED_ORG", takeMedOrg);
									String MedNo = takeMedNoMap.get(caseNo
											+ orderNo + orderSeq) == null ? ""
											: (String) takeMedNoMap.get(caseNo
													+ orderNo + orderSeq);
									doData.setData("TAKEMED_NO", MedNo);
									doData.setData("START_DTTM",
											startDttmInDspnm);
									doData.setData("END_DTTM", endDttmInDspnm);
									if (("UD".equals(orderRxKind)
											|| "ST".equals(orderRxKind) || "OP"
												.equals(orderRxKind))// WANGJC
											&& cat1Type.equals("PHA")) {
										result = upDateTakeMedOrg(doData,
												connection);
										if (result.getErrCode() < 0) {
											err("ERR:" + result.getErrCode()
													+ result.getErrText()
													+ result.getErrName());
											return result;
										}
									}
								}
								// 判断是否存在记录
								else if (!this.checkIsExistDspnm(caseNo,
										orderNo, orderSeq, startDttmInDspnm)) {
									String Mdcflg = "N";
									// 循环3--处置细项：(插入D表的每一条数据)
									for (int row_D = 0; row_D < insertDCount; row_D++) {
										// 最后需要插入细表的数据
										TParm finalDataToD = new TParm();
										String orderDateTime = (String) orderDateTimeFromUdd
												.get(row_D);
										if (!orderToUddParm.getValue("DC_DATE")
												.equals("")
												&& StringTool.getTimestamp(
														orderDateTime,
														"yyyyMMddHHmm")
														.compareTo(Dcdate) > 0)
											Mdcflg = "Y";
										finalDataToD = getDataDspnd_(
												dataFromUdd, orderToUddParm,
												orderDateTime, row_D, DmediQty,
												DmediUnit, DdosageQty,
												DdosageUnit, totAmtD, optUser,
												optDate, optTerm, takeMedOrg);
										// 检查该医嘱知否存在
										if (!this.checkIsExistDspnd(caseNo,
												orderNo, orderSeq,
												orderDateTime.substring(0, 8),
												orderDateTime.substring(8))) {
											// 插入ODI_DSPND
											result = InwForOdiTool
													.getInstance()
													.insertOdiDspnd(
															finalDataToD,
															connection);
										} else {
										}
										if (result.getErrCode() < 0) {
											err("ERR:" + result.getErrCode()
													+ result.getErrText()
													+ result.getErrName());
											return result;
										}
									}
									String MedNo = takeMedNoMap.get(caseNo
											+ orderNo + orderSeq) == null ? ""
											: (String) takeMedNoMap.get(caseNo
													+ orderNo + orderSeq);
									// 插入ODI_DSPNM
									TParm finalDataToM = new TParm();
									finalDataToM = getDataDspnm(dataFromUdd,
											orderToUddParm, row_M,
											endDttmInDspnm, startDttmInDspnm,
											endDttmInDspnm, MdispenseQty,
											MdispenseUnit, MdosageQty,
											MdosageUnit, ownPrice, nhiPrice,
											ownAmt, totAmtM, discountRate,
											optUser, optDate, optTerm,
											takeMedOrg, MedNo, Mdcflg);
									if ((finalDataToM.getData("DC_DATE") instanceof TNull)
											&& finalDataToM.getValue(
													"DSPN_KIND").equals("UD")
											&& !isOperation) {
										finalDataToM.setData("DSPN_KIND", "F");
									}
									result = InwForOdiTool.getInstance()
											.insertOdiDspnm(finalDataToM,
													connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
									// 更新ODI_ORDER
									TParm finalDataToOrder = new TParm();
									String caseNoTemp = (String) orderToUddParm
											.getData("CASE_NO");
									String orderNoTemp = (String) orderToUddParm
											.getData("ORDER_NO");
									int orderSeqTemp = Integer
											.parseInt(orderToUddParm
													.getData("ORDER_SEQ") + "");

									// 更新ODI_ORDER的字段LASTDSPN_QTY LAST_DSPN_DATE
									finalDataToOrder = getDataOrder(
											orderToUddParm, caseNoTemp,
											orderNoTemp, orderSeqTemp,
											lastDspnDateForOrder, OlastDspnQty,
											OacumDspnQty, OacumMediQty,
											MdispenseQty, MdispenseUnit,
											MdosageQty, MdosageUnit, optUser,
											optDate, optTerm, checkDate,
											takeMedOrg);
									result = InwForOdiTool.getInstance()
											.updateOdiOrder(finalDataToOrder,
													connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
									if (result.getInt("RETURN") == 0) {// 针对没有更新到数据
										result.setErrCode(-100);
										result.setErrText("审核数据中"
												+ orderToUddParm
														.getValue("ORDER_DESC")
												+ "已不存在,请重新查询");
										return result;
									}
								}
							} // 更新ODI_ORDER——DC阶段
							else {
								String dateTimeDspnm = "";
								// 循环3--处置细项：(插入D表的每一条数据)
								for (int row_D = 0; row_D < insertDCount; row_D++) {
									String orderDateTime = (String) orderDateTimeFromUdd
											.get(row_D);
									// 更新ODI_DSPND的字段
									TParm DCDspnd = new TParm();
									Timestamp dcDate = orderToUddParm
											.getTimestamp("DC_DATE");
									String strDCTime = StringTool.getString(
											dcDate, "yyyyMMddHHmmss");
									String enddcTime = DCdispenseDttm.get(0)
											.toString();
									// shibl 20120413 modify
									if (!isOperation) {// 药品
										if (orderDateTime.compareTo(strDCTime) < 0
												|| orderDateTime
														.compareTo(enddcTime) > 0)
											continue;
										if (dateTimeDspnm.length() == 0)
											dateTimeDspnm = orderDateTime
													.substring(0, 12);
									} else {
										if (orderDateTime.compareTo(strDCTime) < 0)
											continue;
										if (dateTimeDspnm.length() == 0)
											dateTimeDspnm = orderDateTime;
									}
									DCDspnd.setData("CASE_NO", caseNo);
									DCDspnd.setData("ORDER_NO", orderNo);
									DCDspnd.setData("ORDER_SEQ", orderSeq);
									DCDspnd.setData("DC_DATE", dcDate);
									DCDspnd.setData("ORDER_DATE",
											orderDateTime.substring(0, 8));
									DCDspnd.setData("ORDER_DATETIME",
											orderDateTime.substring(8));
									// DCDspnd.setData("NS_EXEC_DC_CODE",
									// optUser);
									// DCDspnd.setData("NS_EXEC_DC_DATE",
									// optDate);
									DCDspnd.setData("OPT_USER", optUser);
									DCDspnd.setData("OPT_DATE", optDate);
									DCDspnd.setData("OPT_TERM", optTerm);
									result = InwForOdiTool.getInstance()
											.updateDCToDspnD(DCDspnd,
													connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
								}
								// 更新ODI_DSPNM的字段(目前不支持隔天DC)
								TParm DCDspnm = new TParm();
								Timestamp dcDate = orderToUddParm
										.getTimestamp("DC_DATE");
								String daDrCode = orderToUddParm
										.getValue("DC_DR_CODE");
								DCDspnm.setData("CASE_NO", caseNo);
								DCDspnm.setData("ORDER_NO", orderNo);
								DCDspnm.setData("ORDER_SEQ", orderSeq);
								DCDspnm.setData("DC_DATE", dcDate);
								DCDspnm.setData("DC_DR_CODE", daDrCode);
								DCDspnm.setData("START_DTTM", startDttmInDspnm);
								// DCDspnm.setData("NS_EXEC_DC_CODE", optUser);
								// DCDspnm.setData("NS_EXEC_DC_DATE", optDate);
								DCDspnm.setData("OPT_USER", optUser);
								DCDspnm.setData("OPT_DATE", optDate);
								DCDspnm.setData("OPT_TERM", optTerm);
								DCDspnm.setData("DATETIME", dateTimeDspnm);
								result = InwForOdiTool.getInstance()
										.updateDCToDspnM(DCDspnm, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
								// 更新ODI_ORDER的字段
								TParm DCData = new TParm();
								DCData.setData("CASE_NO", caseNo);
								DCData.setData("ORDER_NO", orderNo);
								DCData.setData("ORDER_SEQ", orderSeq);
								DCData.setData("DC_NS_CHECK_CODE", optUser);
								DCData.setData("DC_NS_CHECK_DATE", optDate);
								if (orderToUddParm.getValue("NS_CHECK_CODE")
										.equals("")) {
									DCData.setData("NS_CHECK_CODE", optUser);
									DCData.setData("NS_CHECK_DATE", optDate);
								} else {
									DCData.setData("NS_CHECK_CODE",
											orderToUddParm
													.getValue("NS_CHECK_CODE"));
									DCData.setData(
											"NS_CHECK_DATE",
											orderToUddParm
													.getTimestamp("NS_CHECK_DATE"));
								}
								DCData.setData("OPT_USER", optUser);
								DCData.setData("OPT_DATE", optDate);
								DCData.setData("OPT_TERM", optTerm);
								DCData.setData("TAKEMED_ORG", takeMedOrg);
								result = InwForOdiTool.getInstance()
										.updateDCToOrder(DCData, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							}
						} // 没有首日量只更新ODI_ORDER
						else {
							// 判断是否存在取消审核记录 20121030 shibl add
							if (checkIsNSDCExistDspnm(caseNo, orderNo, orderSeq)) {
								// 审核动作
								TParm doData = new TParm();
								doData.setData("CASE_NO", caseNo);
								doData.setData("ORDER_NO", orderNo);
								doData.setData("ORDER_SEQ", orderSeq);
								doData.setData("OPT_USER", optUser);
								doData.setData("OPT_TERM", optTerm);
								doData.setData("OPT_DATE", optDate);
								doData.setData("DC_NS_CHECK_DATE", new TNull(
										Timestamp.class));
								doData.setData("NS_CHECK_DATE", optDate);
								doData.setData("NS_CHECK_CODE", optUser);
								result = undoData(doData, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							} else if (startDttmInDspnm.length() != 0
									&& !this.checkIsExistDspnm(caseNo, orderNo,
											orderSeq, startDttmInDspnm)) {
								// 更新ODI_ORDER
								TParm finalDataToOrder = new TParm();
								String caseNoTemp = (String) orderToUddParm
										.getData("CASE_NO");
								String orderNoTemp = (String) orderToUddParm
										.getData("ORDER_NO");
								int orderSeqTemp = Integer
										.parseInt(orderToUddParm
												.getData("ORDER_SEQ") + "");
								// 更新ODI_ORDER的字段LASTDSPN_QTY LAST_DSPN_DATE
								// ACUMDSPN_QTY ACUMMEDI_QTY
								finalDataToOrder = getDataOrder(orderToUddParm,
										caseNoTemp, orderNoTemp, orderSeqTemp,
										lastDspnDateForOrder, 0, 0, 0, 0, "",
										0, "", optUser, optDate, optTerm,
										checkDate, takeMedOrg);
								result = InwForOdiTool.getInstance()
										.updateOdiOrder(finalDataToOrder,
												connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							} else {
								// 更新ODI_ORDER的字段
								TParm DCData = new TParm();
								DCData.setData("CASE_NO", caseNo);
								DCData.setData("ORDER_NO", orderNo);
								DCData.setData("ORDER_SEQ", orderSeq);
								DCData.setData("DC_NS_CHECK_CODE", optUser);
								DCData.setData("DC_NS_CHECK_DATE", optDate);
								// 是否存在且审核医嘱(非DC) SHIBL MODIFY 20121030
								if (orderToUddParm.getValue("NS_CHECK_CODE")
										.equals("")) {
									DCData.setData("NS_CHECK_CODE", optUser);
									DCData.setData("NS_CHECK_DATE", optDate);
								} else {
									DCData.setData("NS_CHECK_CODE",
											orderToUddParm
													.getValue("NS_CHECK_CODE"));
									DCData.setData(
											"NS_CHECK_DATE",
											orderToUddParm
													.getTimestamp("NS_CHECK_DATE"));
								}
								DCData.setData("OPT_USER", optUser);
								DCData.setData("OPT_DATE", optDate);
								DCData.setData("OPT_TERM", optTerm);
								DCData.setData("TAKEMED_ORG", takeMedOrg);
								result = InwForOdiTool.getInstance()
										.updateDCToOrder(DCData, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							}
						}
					} // 如果是出院带药
					else if ("DS".equals(orderRxKind)) {
						// 最后需要插入细表的数据
						TParm finalDataToD = new TParm();
						String orderDateTime = StringTool.getString(optDate,
								"yyyyMMddHHmmss");
						// 判断是否存在取消审核记录
						if (checkIsNSDCExistDspnm(caseNo, orderNo, orderSeq)) {
							// System.out.println("-------判断是否存在取消审核记录----------");
							// 审核动作
							TParm doData = new TParm();
							doData.setData("CASE_NO", caseNo);
							doData.setData("ORDER_NO", orderNo);
							doData.setData("ORDER_SEQ", orderSeq);
							doData.setData("OPT_USER", optUser);
							doData.setData("OPT_TERM", optTerm);
							doData.setData("OPT_DATE", optDate);
							doData.setData("DC_NS_CHECK_DATE", new TNull(
									Timestamp.class));
							doData.setData("NS_CHECK_DATE", optDate);
							doData.setData("NS_CHECK_CODE", optUser);
							result = undoData(doData, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						} else {
							finalDataToD = getDataDspnd(orderPram,
									orderDateTime, optUser, optDate, optTerm,
									takeMedOrg);
							// 检查该医嘱知否存在
							if (!this.checkIsExistDspnd(caseNo, orderNo,
									orderSeq, orderDateTime.substring(0, 8),
									orderDateTime.substring(8))) {
								// 插入ODI_DSPND
								result = InwForOdiTool.getInstance()
										.insertOdiDspnd(finalDataToD,
												connection);
							} else {

							}
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							String MedNo = takeMedNoMap.get(caseNo + orderNo
									+ orderSeq) == null ? ""
									: (String) takeMedNoMap.get(caseNo
											+ orderNo + orderSeq);
							// 插入ODI_DSPNM
							TParm finalDataToM = new TParm();
							finalDataToM = getDataDspnm(orderPram, optUser,
									optDate, optTerm, serviceLevel, takeMedOrg,
									MedNo);
							result = InwForOdiTool.getInstance()
									.insertOdiDspnm(finalDataToM, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							// 更新ODI_ORDER
							TParm finalDataToOrder = new TParm();
							// 更新ODI_ORDER的字段LASTDSPN_QTY LAST_DSPN_DATE
							// ACUMDSPN_QTY
							// ACUMMEDI_QTY
							finalDataToOrder = getDataOrder(orderPram, optUser,
									optDate, optTerm, checkDate, takeMedOrg);
							result = InwForOdiTool.getInstance()
									.updateOdiOrder(finalDataToOrder,
											connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						}
					} // 住院中药
					else if ("IG".equals(orderRxKind)) {
						// System.out.println("this is IG process");
						// 调用UDD接口返回数量和单位
						// DOSAGE_QTY(PS:将来修改草药颗粒剂的时候只是多返回数据，按返对应回插入)
						TParm getIGInfoFromUDD = TotQtyTool.getInstance()
								.getTotQty(orderPram.getRow(0));
						double dosageQtyIG = getIGInfoFromUDD.getDouble("QTY");
						// 用返回值代替原来的数值
						orderPram.setData("DOSAGE_QTY", 0, dosageQtyIG);
						orderPram.setData("DISPENSE_QTY", 0, dosageQtyIG); // 目前DOSAGE_QTY=DISPENSE_QTY
						// 最后需要插入细表的数据
						TParm finalDataToD = new TParm();
						String orderDateTime = StringTool.getString(optDate,
								"yyyyMMddHHmmss");
						finalDataToD = getDataDspnd(orderPram, orderDateTime,
								optUser, optDate, optTerm, takeMedOrg);
						// 判断是否存在取消审核记录
						if (checkIsNSDCExistDspnm(caseNo, orderNo, orderSeq)) {
							// 审核动作
							TParm doData = new TParm();
							doData.setData("CASE_NO", caseNo);
							doData.setData("ORDER_NO", orderNo);
							doData.setData("ORDER_SEQ", orderSeq);
							doData.setData("OPT_USER", optUser);
							doData.setData("OPT_TERM", optTerm);
							doData.setData("OPT_DATE", optDate);
							doData.setData("DC_NS_CHECK_DATE", new TNull(
									Timestamp.class));
							doData.setData("NS_CHECK_DATE", optDate);
							doData.setData("NS_CHECK_CODE", optUser);
							result = undoData(doData, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						} else {
							// 检查该医嘱知否存在
							if (!this.checkIsExistDspnd(caseNo, orderNo,
									orderSeq, orderDateTime.substring(0, 8),
									orderDateTime.substring(8))) {
							} else {
							}
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							// 插入ODI_DSPNM
							String MedNo = takeMedNoMap.get(caseNo + orderNo
									+ orderSeq) == null ? ""
									: (String) takeMedNoMap.get(caseNo
											+ orderNo + orderSeq);
							TParm finalDataToM = new TParm();
							finalDataToM = getDataDspnm(orderPram, optUser,
									optDate, optTerm, serviceLevel, takeMedOrg,
									MedNo);
							result = InwForOdiTool.getInstance()
									.insertOdiDspnm(finalDataToM, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}

							// 更新ODI_ORDER
							TParm finalDataToOrder = new TParm();
							// 更新ODI_ORDER的字段LASTDSPN_QTY LAST_DSPN_DATE
							// ACUMDSPN_QTY
							// ACUMMEDI_QTY
							finalDataToOrder = getDataOrder(orderPram, optUser,
									optDate, optTerm, checkDate, takeMedOrg);
							result = InwForOdiTool.getInstance()
									.updateOdiOrder(finalDataToOrder,
											connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						}
					}
				}
			}
			connection.commit();
			if ("Y".equals(parm.getValue("OPE"))) {
				String date = SystemTool.getInstance().getDate().toString()
						.substring(0, 19);
				for (int i = 0; i < dataParm.getCount(); i++) {
					String sql = "UPDATE ODI_DSPNM SET NS_EXEC_CODE = '"
							+ parm.getValue("OPT_USER")
							+ "' , NS_EXEC_DATE = TO_DATE('" + date
							+ "','yyyy-MM-dd HH24:mi:ss')"
							+ " WHERE ORDER_NO = '"
							+ dataParm.getValue("ORDER_NO", i)
							+ "' AND ORDER_SEQ = '"
							+ dataParm.getValue("ORDER_SEQ", i)
							+ "' AND CASE_NO = '"
							+ dataParm.getValue("CASE_NO", i) + "'";

					result = new TParm(TJDODBTool.getInstance().update(sql,
							connection));
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}
				}

			}
			connection.commit();
		}
		return result;
	}

	/**
	 * 整理需要插入ODI_DSPNM的数据
	 * 
	 * @param fromUddData
	 *            List 从UDD返回的需要的值
	 * @param orderParm
	 *            TParm 主表的基础数据
	 * @return TParm
	 */
	private TParm getDataDspnm(TParm orderParm, String optUser,
			Timestamp optDate, String optTerm, String serviceLevel,
			String takeMedOrg, String takeMedNo) {
		TParm priceForODI = getPrice(orderParm.getValue("CASE_NO", 0),
				orderParm.getValue("ORDER_CODE", 0),
				orderParm.getDouble("DISPENSE_QTY", 0), serviceLevel);
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO", 0));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO", 0));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ", 0));
		result.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY", 0));// add
																					// by
																					// wanglong
																					// 20121210
		result.setData("START_DTTM", StringTool.getString(optDate, "yyyyMMdd")
				+ "0000");
		result.setData("END_DTTM", StringTool.getString(optDate, "yyyyMMdd")
				+ "2359");
		// =========== add by wukai on 20160601 start
		result.setData("PUMP_CODE", orderParm.getData("PUMP_CODE",0) == null ? ""
				: orderParm.getData("PUMP_CODE",0));
		result.setData(
				"INFLUTION_RATE",
				orderParm.getData("INFLUTION_RATE",0) == null ? "" : orderParm
						.getData("INFLUTION_RATE",0));
		// =========== add by wukai on 20160601 end
		result.setData("REGION_CODE",
				orderParm.getData("REGION_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("REGION_CODE", 0));
		result.setData("STATION_CODE",
				orderParm.getData("STATION_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("STATION_CODE", 0));
		result.setData("DEPT_CODE",
				orderParm.getData("DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DEPT_CODE", 0));
		result.setData("VS_DR_CODE",
				orderParm.getData("VS_DR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("VS_DR_CODE", 0));
		result.setData(
				"BED_NO",
				orderParm.getData("BED_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("BED_NO", 0));

		result.setData(
				"IPD_NO",
				orderParm.getData("IPD_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("IPD_NO", 0));
		result.setData("MR_NO",
				orderParm.getData("MR_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("MR_NO", 0));
		result.setData("DSPN_KIND",
				orderParm.getData("RX_KIND", 0) == null ? new TNull(
						String.class) : orderParm.getData("RX_KIND", 0));
		result.setData("DSPN_DATE",
				orderParm.getData("DSPN_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("DSPN_DATE", 0));
		result.setData("DSPN_USER",
				orderParm.getData("DSPN_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("DSPN_USER", 0));

		result.setData("DISPENSE_EFF_DATE", optDate);
		result.setData("RX_NO",
				orderParm.getData("RX_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("RX_NO", 0));
		result.setData("ORDER_CAT1_CODE",
				orderParm.getData("ORDER_CAT1_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CAT1_CODE", 0));
		result.setData("CAT1_TYPE",
				orderParm.getData("CAT1_TYPE", 0) == null ? new TNull(
						String.class) : orderParm.getData("CAT1_TYPE", 0));
		result.setData("DISPENSE_END_DATE", optDate);

		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE", 0));
		result.setData("DISPENSE_FLG",
				orderParm.getData("DISPENSE_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_FLG", 0));
		result.setData("AGENCY_ORG_CODE",
				orderParm.getData("AGENCY_ORG_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("AGENCY_ORG_CODE", 0));
		result.setData("PRESCRIPT_NO",
				orderParm.getData("PRESCRIPT_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("PRESCRIPT_NO", 0));
		result.setData("LINKMAIN_FLG",
				orderParm.getData("LINKMAIN_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("LINKMAIN_FLG", 0));

		result.setData("LINK_NO",
				orderParm.getData("LINK_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("LINK_NO", 0));
		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE", 0));
		result.setData("ORDER_DESC",
				orderParm.getData("ORDER_DESC", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DESC", 0));
		result.setData("GOODS_DESC",
				orderParm.getData("GOODS_DESC", 0) == null ? new TNull(
						String.class) : orderParm.getData("GOODS_DESC", 0));
		result.setData("SPECIFICATION",
				orderParm.getData("SPECIFICATION", 0) == null ? new TNull(
						String.class) : orderParm.getData("SPECIFICATION", 0));

		result.setData(
				"MEDI_QTY",
				orderParm.getData("MEDI_QTY", 0) == null ? 0.00 : orderParm
						.getData("MEDI_QTY", 0));
		result.setData("MEDI_UNIT",
				orderParm.getData("MEDI_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("MEDI_UNIT", 0));
		result.setData("FREQ_CODE",
				orderParm.getData("FREQ_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("FREQ_CODE", 0));
		result.setData("ROUTE_CODE",
				orderParm.getData("ROUTE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ROUTE_CODE", 0));
		result.setData("TAKE_DAYS",
				orderParm.getData("TAKE_DAYS", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("TAKE_DAYS", 0));

		result.setData(
				"DOSAGE_QTY",
				orderParm.getData("DOSAGE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DOSAGE_QTY", 0));
		result.setData("DOSAGE_UNIT",
				orderParm.getData("DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSAGE_UNIT", 0));
		result.setData(
				"DISPENSE_QTY",
				orderParm.getData("DISPENSE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DISPENSE_QTY", 0));
		result.setData("DISPENSE_UNIT",
				orderParm.getData("DISPENSE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_UNIT", 0));
		result.setData("GIVEBOX_FLG",
				orderParm.getData("GIVEBOX_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("GIVEBOX_FLG", 0));

		// 调用计费接口得到
		result.setData(
				"OWN_PRICE",
				priceForODI.getData("OWN_PRICE") == null ? 0.00 : priceForODI
						.getData("OWN_PRICE"));
		result.setData(
				"NHI_PRICE",
				priceForODI.getData("NHI_PRICE") == null ? 0.00 : priceForODI
						.getData("NHI_PRICE"));
		result.setData(
				"DISCOUNT_RATE",
				priceForODI.getData("RATE") == null ? 0.00 : priceForODI
						.getData("RATE")); // 目前保留
		result.setData("OWN_AMT", priceForODI.getData("OWN_AMT") == null ? 0.00
				: priceForODI.getData("OWN_AMT")); // 费用返回
		result.setData("TOT_AMT", priceForODI.getData("TOT_AMT") == null ? 0.00
				: priceForODI.getData("TOT_AMT"));

		// M表中的ORDER_DATE记录的是ORDER表中的EFF_DATE
		result.setData("ORDER_DATE",
				orderParm.getData("EFF_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("EFF_DATE", 0));
		result.setData("ORDER_DEPT_CODE",
				orderParm.getData("ORDER_DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DEPT_CODE", 0));
		result.setData("ORDER_DR_CODE",
				orderParm.getData("ORDER_DR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DR_CODE", 0));
		result.setData("DR_NOTE",
				orderParm.getData("DR_NOTE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DR_NOTE", 0));
		result.setData("ATC_FLG",
				orderParm.getData("ATC_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("ATC_FLG", 0));

		result.setData("SENDATC_FLG",
				orderParm.getData("SENDATC_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("SENDATC_FLG", 0));
		result.setData("SENDATC_DTTM",
				orderParm.getData("SENDATC_DTTM", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("SENDATC_DTTM", 0));
		result.setData("INJPRAC_GROUP",
				orderParm.getData("INJPRAC_GROUP", 0) == null ? new TNull(
						String.class) : orderParm.getData("INJPRAC_GROUP", 0));
		result.setData("DC_DATE",
				orderParm.getData("DC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("DC_DATE", 0));
		result.setData("DC_TOT", orderParm.getData("DC_TOT", 0) == null ? 0.00
				: orderParm.getData("DC_TOT", 0));

		result.setData(
				"RTN_NO",
				orderParm.getData("RTN_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("RTN_NO", 0));
		result.setData("RTN_NO_SEQ",
				orderParm.getData("RTN_NO_SEQ", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("RTN_NO_SEQ", 0));
		result.setData("RTN_DOSAGE_QTY",
				orderParm.getData("RTN_DOSAGE_QTY", 0) == null ? 0.00
						: orderParm.getData("RTN_DOSAGE_QTY", 0));
		result.setData("RTN_DOSAGE_UNIT",
				orderParm.getData("RTN_DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("RTN_DOSAGE_UNIT", 0));
		result.setData("CANCEL_DOSAGE_QTY",
				orderParm.getData("CANCEL_DOSAGE_QTY", 0) == null ? 0.00
						: orderParm.getData("CANCEL_DOSAGE_QTY", 0));

		result.setData("CANCELRSN_CODE",
				orderParm.getData("CANCELRSN_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("CANCELRSN_CODE", 0));
		result.setData("TRANSMIT_RSN_CODE", orderParm.getData(
				"TRANSMIT_RSN_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE", 0));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE", 0));
		result.setData(
				"PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm
						.getData("PHA_RETN_DATE", 0));
		result.setData("PHA_CHECK_CODE",
				orderParm.getData("PHA_CHECK_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_CHECK_CODE", 0));

		result.setData(
				"PHA_CHECK_DATE",
				orderParm.getData("PHA_CHECK_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_CHECK_DATE",
						0));
		result.setData("PHA_DISPENSE_NO",
				orderParm.getData("PHA_DISPENSE_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_DISPENSE_NO", 0));
		result.setData("PHA_DOSAGE_CODE",
				orderParm.getData("PHA_DOSAGE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_DOSAGE_CODE", 0));
		result.setData(
				"PHA_DOSAGE_DATE",
				orderParm.getData("PHA_DOSAGE_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_DOSAGE_DATE",
						0));
		result.setData("PHA_DISPENSE_CODE", orderParm.getData(
				"PHA_DISPENSE_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("PHA_DISPENSE_CODE", 0));

		result.setData("PHA_DISPENSE_DATE", orderParm.getData(
				"PHA_DISPENSE_DATE", 0) == null ? new TNull(Timestamp.class)
				: orderParm.getData("PHA_DISPENSE_DATE", 0));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE", 0));
		result.setData("NS_EXEC_DATE",
				orderParm.getData("NS_EXEC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE", 0));
		result.setData("NS_EXEC_DC_CODE",
				orderParm.getData("NS_EXEC_DC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_DC_CODE", 0));
		result.setData(
				"NS_EXEC_DC_DATE",
				orderParm.getData("NS_EXEC_DC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DC_DATE",
						0));

		result.setData("NS_USER",
				orderParm.getData("NS_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_USER", 0));
		result.setData("CTRLDRUGCLASS_CODE", orderParm.getData(
				"CTRLDRUGCLASS_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("CTRLDRUGCLASS_CODE", 0));
		result.setData("PHA_TYPE",
				orderParm.getData("PHA_TYPE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_TYPE", 0));
		result.setData("DOSE_TYPE",
				orderParm.getData("DOSE_TYPE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSE_TYPE", 0));
		result.setData("DCTAGENT_CODE",
				orderParm.getData("DCTAGENT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_CODE", 0));

		result.setData("DCTEXCEP_CODE",
				orderParm.getData("DCTEXCEP_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DCTEXCEP_CODE", 0));
		result.setData("DCT_TAKE_QTY",
				orderParm.getData("DCT_TAKE_QTY", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("DCT_TAKE_QTY", 0));
		result.setData("PACKAGE_AMT",
				orderParm.getData("PACKAGE_AMT", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("PACKAGE_AMT", 0));
		result.setData("DCTAGENT_FLG",
				orderParm.getData("DCTAGENT_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_FLG", 0));
		result.setData("PRESRT_NO",
				orderParm.getData("PRESRT_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("PRESRT_NO", 0));

		result.setData("DECOCT_CODE",
				orderParm.getData("DECOCT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DECOCT_CODE", 0));
		result.setData("URGENT_FLG",
				orderParm.getData("URGENT_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("URGENT_FLG", 0));
		result.setData("SETMAIN_FLG",
				orderParm.getData("SETMAIN_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("SETMAIN_FLG", 0));
		result.setData("ORDERSET_GROUP_NO", orderParm.getData(
				"ORDERSET_GROUP_NO", 0) == null ? new TNull(String.class)
				: orderParm.getData("ORDERSET_GROUP_NO", 0));
		result.setData("ORDERSET_CODE",
				orderParm.getData("ORDERSET_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDERSET_CODE", 0));

		result.setData("RPTTYPE_CODE",
				orderParm.getData("RPTTYPE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("RPTTYPE_CODE", 0));
		result.setData("OPTITEM_CODE",
				orderParm.getData("OPTITEM_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("OPTITEM_CODE", 0));
		result.setData("HIDE_FLG",
				orderParm.getData("HIDE_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("HIDE_FLG", 0));
		result.setData("DEGREE_CODE",
				orderParm.getData("DEGREE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DEGREE_CODE", 0));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("BILL_FLG", 0));

		result.setData("CASHIER_USER",
				orderParm.getData("CASHIER_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_USER", 0));
		result.setData("CASHIER_DATE",
				orderParm.getData("CASHIER_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("CASHIER_DATE", 0));
		result.setData(
				"IBS_CASE_NO_SEQ",
				orderParm.getData("IBS_CASE_NO_SEQ", 0) == null ? new TNull(
						Integer.class) : orderParm
						.getData("IBS_CASE_NO_SEQ", 0));
		result.setData("IBS_SEQ_NO",
				orderParm.getData("IBS_SEQ_NO", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("IBS_SEQ_NO", 0));
		result.setData("ANTIBIOTIC_CODE",
				orderParm.getData("ANTIBIOTIC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ANTIBIOTIC_CODE", 0));
		result.setData("DC_DR_CODE",
				orderParm.getData("DC_DR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DC_DR_CODE", 0));

		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);
		// 领药注记默认为2
		result.setData("TAKEMED_ORG", takeMedOrg);
		result.setData("TAKEMED_NO", takeMedNo);
		return result;
	}

	/**
	 * 整理需要插入ODI_ORDER的数据
	 * 
	 * @param fromUddData
	 *            List 从UDD返回的需要的值
	 * @param orderParm
	 *            TParm 主表的基础数据
	 * @return TParm
	 */
	private TParm getDataOrder(TParm orderParm, String caseNo, String orderNo,
			int orderSeq, Timestamp lastDspnDate, double lastDspnQty,
			double acumDspnQty, double acumMediQty, double dispenseQty,
			String dispenseUnit, double dosageQty, String dosageUnit,
			String optUser, Timestamp optDate, String optTerm,
			Timestamp checkDate, String takeMedOrg) {
		TParm result = new TParm();
		result.setData("CASE_NO", caseNo);
		result.setData("ORDER_NO", orderNo);
		result.setData("ORDER_SEQ", orderSeq);
		result.setData("LASTDSPN_QTY", lastDspnQty);
		result.setData("LAST_DSPN_DATE", lastDspnDate);
		result.setData("ACUMDSPN_QTY", acumDspnQty);
		result.setData("ACUMMEDI_QTY", acumMediQty);
		result.setData("NS_CHECK_CODE", optUser);
		// 由于该项会手动修改所以和OPT_DATE不同
		result.setData("NS_CHECK_DATE", checkDate);
		result.setData("DOSAGE_QTY", dosageQty);
		result.setData("DOSAGE_UNIT", dosageUnit);
		result.setData("DISPENSE_QTY", dispenseQty);
		result.setData("DISPENSE_UNIT", dispenseUnit);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_USER", optUser);
		result.setData("OPT_TERM", optTerm);

		// 当没有DC_DATE的时候不更新DC_NS_CHECK_CODE DC_NS_CHECK_DATE
		Timestamp daDate = (Timestamp) orderParm.getData("DC_DATE");
		if (orderParm.getValue("NS_CHECK_CODE").equals("")) {
			result.setData("DC_NS_CHECK_CODE", new TNull(String.class));
			result.setData("DC_NS_CHECK_DATE", new TNull(Timestamp.class));
		} else {
			result.setData("DC_NS_CHECK_CODE", optUser);
			result.setData("DC_NS_CHECK_DATE", optDate);
		}
		result.setData("TAKEMED_ORG", takeMedOrg);
		return result;
	}

	private TParm getDataOrder(TParm orderParm, String optUser,
			Timestamp optDate, String optTerm, Timestamp checkDate,
			String takeMedOrg) {
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO", 0));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO", 0));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ", 0));
		result.setData("LASTDSPN_QTY", 0.00);
		result.setData("LAST_DSPN_DATE", new TNull(Timestamp.class));
		result.setData("ACUMDSPN_QTY", 0.00);
		result.setData("ACUMMEDI_QTY", 0.00);
		result.setData("NS_CHECK_CODE", optUser);
		// 由于该项会手动修改所以和OPT_DATE不同
		result.setData("NS_CHECK_DATE", checkDate);
		result.setData(
				"DOSAGE_QTY",
				orderParm.getData("DOSAGE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DOSAGE_QTY", 0));
		result.setData("DOSAGE_UNIT",
				orderParm.getData("DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSAGE_UNIT", 0));
		result.setData(
				"DISPENSE_QTY",
				orderParm.getData("DISPENSE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DISPENSE_QTY", 0));
		result.setData("DISPENSE_UNIT",
				orderParm.getData("DISPENSE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_UNIT", 0));
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_USER", optUser);
		result.setData("OPT_TERM", optTerm);

		// 当没有DC_DATE的时候不更新DC_NS_CHECK_CODE DC_NS_CHECK_DATE
		Timestamp daDate = (Timestamp) orderParm.getData("DC_DATE", 0);
		if (daDate == null) {
			result.setData("DC_NS_CHECK_CODE", new TNull(String.class));
			result.setData("DC_NS_CHECK_DATE", new TNull(Timestamp.class));
		} else {
			result.setData("DC_NS_CHECK_CODE", optUser);
			result.setData("DC_NS_CHECK_DATE", optDate);
		}
		result.setData("TAKEMED_ORG", takeMedOrg);
		return result;
	}

	/**
	 * 整理需要插入ODI_DSPNM的数据
	 * 
	 * @param fromUddData
	 *            List 从UDD返回的需要的值
	 * @param orderParm
	 *            TParm 主表的基础数据
	 * @return TParm
	 */
	private TParm getDataDspnm(List fromUddData, TParm orderParm, int row,
			String endDttmInDspnm, String startDttm, String endDttm,
			double dispenseQty, String dispenseUnit, double dosageQty,
			String dosageUnit, double ownPrice, double nhiPrice, double ownAmt,
			double totAmtM, double discountRate, String optUser,
			Timestamp optDate, String optTerm, String takeMedOrg,
			String takeMedNo, String Mdcflg) {
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO"));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO"));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ"));
		result.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY"));// add
		// by
		// wanglong
		// 20121210
		// =========== add by wukai on 20160601 start
		result.setData("PUMP_CODE", orderParm.getData("PUMP_CODE") == null ? new TNull(String.class)
				: orderParm.getData("PUMP_CODE"));
		result.setData(
				"INFLUTION_RATE",
				orderParm.getData("INFLUTION_RATE") == null ? new TNull(String.class) : orderParm.getData("INFLUTION_RATE"));
		// =========== add by wukai on 20160601 end
		result.setData("START_DTTM", startDttm);
		result.setData("END_DTTM", endDttm);

		result.setData("REGION_CODE",
				orderParm.getData("REGION_CODE") == null ? new TNull(
						String.class) : orderParm.getData("REGION_CODE"));
		result.setData("STATION_CODE",
				orderParm.getData("STATION_CODE") == null ? new TNull(
						String.class) : orderParm.getData("STATION_CODE"));
		result.setData(
				"DEPT_CODE",
				orderParm.getData("DEPT_CODE") == null ? new TNull(String.class)
						: orderParm.getData("DEPT_CODE"));
		result.setData("VS_DR_CODE",
				orderParm.getData("VS_DR_CODE") == null ? new TNull(
						String.class) : orderParm.getData("VS_DR_CODE"));
		result.setData("BED_NO",
				orderParm.getData("BED_NO") == null ? new TNull(String.class)
						: orderParm.getData("BED_NO"));

		result.setData("IPD_NO",
				orderParm.getData("IPD_NO") == null ? new TNull(String.class)
						: orderParm.getData("IPD_NO"));
		result.setData("MR_NO", orderParm.getData("MR_NO") == null ? new TNull(
				String.class) : orderParm.getData("MR_NO"));
		result.setData("DSPN_KIND",
				orderParm.getData("RX_KIND") == null ? new TNull(String.class)
						: orderParm.getData("RX_KIND"));
		result.setData("DSPN_DATE",
				orderParm.getData("DSPN_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("DSPN_DATE"));
		result.setData(
				"DSPN_USER",
				orderParm.getData("DSPN_USER") == null ? new TNull(String.class)
						: orderParm.getData("DSPN_USER"));

		result.setData("DISPENSE_EFF_DATE", optDate);
		result.setData("RX_NO", orderParm.getData("RX_NO") == null ? new TNull(
				String.class) : orderParm.getData("RX_NO"));
		result.setData("ORDER_CAT1_CODE",
				orderParm.getData("ORDER_CAT1_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CAT1_CODE"));
		result.setData(
				"CAT1_TYPE",
				orderParm.getData("CAT1_TYPE") == null ? new TNull(String.class)
						: orderParm.getData("CAT1_TYPE"));
		result.setData("DISPENSE_END_DATE",
				StringTool.getTimestamp(endDttmInDspnm, "yyyyMMddHHmm"));

		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE"));
		result.setData("DISPENSE_FLG",
				orderParm.getData("DISPENSE_FLG") == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_FLG"));
		result.setData("AGENCY_ORG_CODE",
				orderParm.getData("AGENCY_ORG_CODE") == null ? new TNull(
						String.class) : orderParm.getData("AGENCY_ORG_CODE"));
		result.setData("PRESCRIPT_NO",
				orderParm.getData("PRESCRIPT_NO") == null ? new TNull(
						String.class) : orderParm.getData("PRESCRIPT_NO"));
		result.setData("LINKMAIN_FLG",
				orderParm.getData("LINKMAIN_FLG") == null ? new TNull(
						String.class) : orderParm.getData("LINKMAIN_FLG"));

		result.setData("LINK_NO",
				orderParm.getData("LINK_NO") == null ? new TNull(String.class)
						: orderParm.getData("LINK_NO"));
		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE"));
		result.setData("ORDER_DESC",
				orderParm.getData("ORDER_DESC") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DESC"));
		result.setData("GOODS_DESC",
				orderParm.getData("GOODS_DESC") == null ? new TNull(
						String.class) : orderParm.getData("GOODS_DESC"));
		result.setData("SPECIFICATION",
				orderParm.getData("SPECIFICATION") == null ? new TNull(
						String.class) : orderParm.getData("SPECIFICATION"));

		result.setData("MEDI_QTY", orderParm.getData("MEDI_QTY") == null ? 0.00
				: orderParm.getData("MEDI_QTY"));
		result.setData(
				"MEDI_UNIT",
				orderParm.getData("MEDI_UNIT") == null ? new TNull(String.class)
						: orderParm.getData("MEDI_UNIT"));
		result.setData(
				"FREQ_CODE",
				orderParm.getData("FREQ_CODE") == null ? new TNull(String.class)
						: orderParm.getData("FREQ_CODE"));
		result.setData("ROUTE_CODE",
				orderParm.getData("ROUTE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ROUTE_CODE"));
		result.setData("TAKE_DAYS",
				orderParm.getData("TAKE_DAYS") == null ? new TNull(
						Integer.class) : orderParm.getData("TAKE_DAYS"));

		result.setData("DOSAGE_QTY", dosageQty);
		// 判断库存 20121115 shibl add 取药注记
		result.setData("TAKEMED_ORG", takeMedOrg);
		result.setData("TAKEMED_NO", takeMedNo);

		result.setData("DOSAGE_UNIT", dosageUnit);
		result.setData("DISPENSE_QTY", dispenseQty);
		result.setData("DISPENSE_UNIT", dispenseUnit);
		result.setData("GIVEBOX_FLG",
				orderParm.getData("GIVEBOX_FLG") == null ? new TNull(
						String.class) : orderParm.getData("GIVEBOX_FLG"));

		// 掉用接口费用返回
		result.setData("OWN_PRICE", ownPrice);
		result.setData("NHI_PRICE", nhiPrice);
		result.setData("DISCOUNT_RATE", discountRate);
		result.setData("OWN_AMT", ownAmt);
		result.setData("TOT_AMT", totAmtM);

		// M表中的ORDER_DATE记录的是ORDER表中的EFF_DATE
		result.setData("ORDER_DATE",
				orderParm.getData("EFF_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("EFF_DATE"));
		result.setData("ORDER_DEPT_CODE",
				orderParm.getData("ORDER_DEPT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DEPT_CODE"));
		result.setData("ORDER_DR_CODE",
				orderParm.getData("ORDER_DR_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DR_CODE"));
		result.setData("DR_NOTE",
				orderParm.getData("DR_NOTE") == null ? new TNull(String.class)
						: orderParm.getData("DR_NOTE"));
		result.setData("ATC_FLG",
				orderParm.getData("ATC_FLG") == null ? new TNull(String.class)
						: orderParm.getData("ATC_FLG"));
		result.setData("SENDATC_FLG",
				orderParm.getData("SENDATC_FLG") == null ? new TNull(
						String.class) : orderParm.getData("SENDATC_FLG"));
		result.setData("SENDATC_DTTM",
				orderParm.getData("SENDATC_DTTM") == null ? new TNull(
						Timestamp.class) : orderParm.getData("SENDATC_DTTM"));
		result.setData("INJPRAC_GROUP",
				orderParm.getData("INJPRAC_GROUP") == null ? new TNull(
						String.class) : orderParm.getData("INJPRAC_GROUP"));
		if (Mdcflg.equals("Y")) {
			result.setData("DC_DATE",
					orderParm.getData("DC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm.getData("DC_DATE"));
			result.setData("DC_DR_CODE",
					orderParm.getData("DC_DR_CODE") == null ? new TNull(
							String.class) : orderParm.getData("DC_DR_CODE"));
		} else {
			result.setData("DC_DATE", new TNull(Timestamp.class));
			result.setData("DC_DR_CODE", new TNull(String.class));
		}
		result.setData("DC_TOT", orderParm.getData("DC_TOT") == null ? 0.00
				: orderParm.getData("DC_TOT"));

		result.setData("RTN_NO",
				orderParm.getData("RTN_NO") == null ? new TNull(String.class)
						: orderParm.getData("RTN_NO"));
		result.setData("RTN_NO_SEQ",
				orderParm.getData("RTN_NO_SEQ") == null ? new TNull(
						Integer.class) : orderParm.getData("RTN_NO_SEQ"));
		result.setData(
				"RTN_DOSAGE_QTY",
				orderParm.getData("RTN_DOSAGE_QTY") == null ? 0.00 : orderParm
						.getData("RTN_DOSAGE_QTY"));
		result.setData("RTN_DOSAGE_UNIT",
				orderParm.getData("RTN_DOSAGE_UNIT") == null ? new TNull(
						String.class) : orderParm.getData("RTN_DOSAGE_UNIT"));
		result.setData("CANCEL_DOSAGE_QTY",
				orderParm.getData("CANCEL_DOSAGE_QTY") == null ? 0.00
						: orderParm.getData("CANCEL_DOSAGE_QTY"));

		result.setData("CANCELRSN_CODE",
				orderParm.getData("CANCELRSN_CODE") == null ? new TNull(
						String.class) : orderParm.getData("CANCELRSN_CODE"));
		result.setData("TRANSMIT_RSN_CODE", orderParm
				.getData("TRANSMIT_RSN_CODE") == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE"));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE"));
		result.setData("PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_RETN_DATE"));
		result.setData("PHA_CHECK_CODE",
				orderParm.getData("PHA_CHECK_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_CHECK_CODE"));

		result.setData("PHA_CHECK_DATE",
				orderParm.getData("PHA_CHECK_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_CHECK_DATE"));
		result.setData("PHA_DISPENSE_NO",
				orderParm.getData("PHA_DISPENSE_NO") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DISPENSE_NO"));
		result.setData("PHA_DOSAGE_CODE",
				orderParm.getData("PHA_DOSAGE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DOSAGE_CODE"));
		result.setData("PHA_DOSAGE_DATE",
				orderParm.getData("PHA_DOSAGE_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_DOSAGE_DATE"));
		result.setData("PHA_DISPENSE_CODE", orderParm
				.getData("PHA_DISPENSE_CODE") == null ? new TNull(String.class)
				: orderParm.getData("PHA_DISPENSE_CODE"));

		result.setData("PHA_DISPENSE_DATE", orderParm
				.getData("PHA_DISPENSE_DATE") == null ? new TNull(
				Timestamp.class) : orderParm.getData("PHA_DISPENSE_DATE"));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE"));
		result.setData("NS_EXEC_DATE",
				orderParm.getData("NS_EXEC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE"));
		result.setData("NS_EXEC_DC_CODE",
				orderParm.getData("NS_EXEC_DC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_DC_CODE"));
		result.setData("NS_EXEC_DC_DATE",
				orderParm.getData("NS_EXEC_DC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DC_DATE"));

		result.setData("NS_USER",
				orderParm.getData("NS_USER") == null ? new TNull(String.class)
						: orderParm.getData("NS_USER"));
		result.setData("CTRLDRUGCLASS_CODE",
				orderParm.getData("CTRLDRUGCLASS_CODE") == null ? new TNull(
						String.class) : orderParm.getData("CTRLDRUGCLASS_CODE"));
		result.setData("PHA_TYPE",
				orderParm.getData("PHA_TYPE") == null ? new TNull(String.class)
						: orderParm.getData("PHA_TYPE"));
		result.setData(
				"DOSE_TYPE",
				orderParm.getData("DOSE_TYPE") == null ? new TNull(String.class)
						: orderParm.getData("DOSE_TYPE"));
		result.setData("DCTAGENT_CODE",
				orderParm.getData("DCTAGENT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_CODE"));

		result.setData("DCTEXCEP_CODE",
				orderParm.getData("DCTEXCEP_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DCTEXCEP_CODE"));
		result.setData("DCT_TAKE_QTY",
				orderParm.getData("DCT_TAKE_QTY") == null ? new TNull(
						Integer.class) : orderParm.getData("DCT_TAKE_QTY"));
		result.setData("PACKAGE_AMT",
				orderParm.getData("PACKAGE_AMT") == null ? new TNull(
						Integer.class) : orderParm.getData("PACKAGE_AMT"));
		result.setData("DCTAGENT_FLG",
				orderParm.getData("DCTAGENT_FLG") == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_FLG"));
		result.setData(
				"PRESRT_NO",
				orderParm.getData("PRESRT_NO") == null ? new TNull(String.class)
						: orderParm.getData("PRESRT_NO"));

		result.setData("DECOCT_CODE",
				orderParm.getData("DECOCT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DECOCT_CODE"));
		result.setData("URGENT_FLG",
				orderParm.getData("URGENT_FLG") == null ? new TNull(
						String.class) : orderParm.getData("URGENT_FLG"));
		result.setData("SETMAIN_FLG",
				orderParm.getData("SETMAIN_FLG") == null ? new TNull(
						String.class) : orderParm.getData("SETMAIN_FLG"));
		result.setData("ORDERSET_GROUP_NO", orderParm
				.getData("ORDERSET_GROUP_NO") == null ? new TNull(String.class)
				: orderParm.getData("ORDERSET_GROUP_NO"));
		result.setData("ORDERSET_CODE",
				orderParm.getData("ORDERSET_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDERSET_CODE"));

		result.setData("RPTTYPE_CODE",
				orderParm.getData("RPTTYPE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("RPTTYPE_CODE"));
		result.setData("OPTITEM_CODE",
				orderParm.getData("OPTITEM_CODE") == null ? new TNull(
						String.class) : orderParm.getData("OPTITEM_CODE"));
		result.setData("HIDE_FLG",
				orderParm.getData("HIDE_FLG") == null ? new TNull(String.class)
						: orderParm.getData("HIDE_FLG"));
		result.setData("DEGREE_CODE",
				orderParm.getData("DEGREE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DEGREE_CODE"));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG") == null ? new TNull(String.class)
						: orderParm.getData("BILL_FLG"));

		result.setData("CASHIER_USER",
				orderParm.getData("CASHIER_USER") == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_USER"));
		result.setData("CASHIER_DATE",
				orderParm.getData("CASHIER_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("CASHIER_DATE"));
		result.setData("IBS_CASE_NO_SEQ",
				orderParm.getData("IBS_CASE_NO_SEQ") == null ? new TNull(
						Integer.class) : orderParm.getData("IBS_CASE_NO_SEQ"));
		result.setData("IBS_SEQ_NO",
				orderParm.getData("IBS_SEQ_NO") == null ? new TNull(
						Integer.class) : orderParm.getData("IBS_SEQ_NO"));
		result.setData("ANTIBIOTIC_CODE",
				orderParm.getData("ANTIBIOTIC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ANTIBIOTIC_CODE"));
		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);
		return result;
	}

	/**
	 * 整理需要插入ODI_DSPND的数据
	 * 
	 * @param fromUddData
	 *            List 从UDD返回的需要的值
	 * @param orderParm
	 *            TParm 主表的基础数据
	 * @param orderDateTime
	 *            String 餐次时间
	 * @param row
	 *            int 行数
	 * @param mediQty
	 *            double 要插入ODI_DSPND表的数据
	 * @param mediUnit
	 *            String 要插入ODI_DSPND表的数据
	 * @param dosageQty
	 *            double 要插入ODI_DSPND表的数据
	 * @param dosageUnit
	 *            String 要插入ODI_DSPND表的数据
	 * @param optUser
	 *            String
	 * @param optDate
	 *            Timestamp
	 * @param optTerm
	 *            String
	 * @return TParm
	 */
	private TParm getDataDspnd_(List fromUddData, TParm orderParm,
			String orderDateTime, int row, double mediQty, String mediUnit,
			double dosageQty, String dosageUnit, double totAmtD,
			String optUser, Timestamp optDate, String optTerm, String takeMedOrg) {

		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO"));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO"));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ"));
		result.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY"));// add
		// by
		// wanglong
		// 20121210
		result.setData("ORDER_DATE", orderDateTime.substring(0, 8));
		result.setData("ORDER_DATETIME", orderDateTime.substring(8, 12));

		result.setData("BATCH_CODE",
				orderParm.getData("BATCH_CODE") == null ? new TNull(
						String.class) : orderParm.getData("BATCH_CODE"));
		result.setData("TREAT_START_TIME", orderParm
				.getData("TREAT_START_TIME") == null ? new TNull(String.class)
				: orderParm.getData("TREAT_START_TIME"));
		result.setData("TREAT_END_TIME",
				orderParm.getData("TREAT_END_TIME") == null ? new TNull(
						String.class) : orderParm.getData("TREAT_END_TIME"));
		result.setData("NURSE_DISPENSE_FLG",
				orderParm.getData("NURSE_DISPENSE_FLG") == null ? new TNull(
						String.class) : orderParm.getData("NURSE_DISPENSE_FLG"));
		result.setData("BAR_CODE",
				orderParm.getData("BAR_CODE") == null ? new TNull(String.class)
						: orderParm.getData("BAR_CODE"));

		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE"));
		result.setData("MEDI_QTY", mediQty);
		result.setData("MEDI_UNIT", mediUnit);
		result.setData("DOSAGE_QTY", dosageQty);
		// shibl 20121116 add 取药来源
		result.setData("TAKEMED_ORG", takeMedOrg);

		result.setData("DOSAGE_UNIT", dosageUnit);
		result.setData("TOT_AMT", totAmtD);
		if (!orderParm.getValue("DC_DATE").equals("")
				&& StringTool.getTimestamp(orderDateTime, "yyyyMMddHHmm")
						.compareTo(orderParm.getTimestamp("DC_DATE")) > 0) {
			result.setData("DC_DATE",
					orderParm.getData("DC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm.getData("DC_DATE"));
		} else {
			result.setData("DC_DATE", new TNull(Timestamp.class));
		}
		result.setData("PHA_DISPENSE_NO",
				orderParm.getData("PHA_DISPENSE_NO") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DISPENSE_NO"));
		result.setData("PHA_DOSAGE_CODE",
				orderParm.getData("PHA_DOSAGE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DOSAGE_CODE"));
		result.setData("PHA_DOSAGE_DATE",
				orderParm.getData("PHA_DOSAGE_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_DOSAGE_DATE"));

		result.setData("PHA_DISPENSE_CODE",
				orderParm.getData("NS_EXEC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE"));
		result.setData("PHA_DISPENSE_DATE",
				orderParm.getData("NS_EXEC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE"));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE"));
		result.setData("NS_EXEC_DATE",
				orderParm.getData("NS_EXEC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE"));
		result.setData("NS_EXEC_DC_CODE",
				orderParm.getData("NS_EXEC_DC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_DC_CODE"));

		result.setData("NS_EXEC_DC_DATE",
				orderParm.getData("NS_EXEC_DC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DC_DATE"));
		result.setData("NS_USER",
				orderParm.getData("NS_USER") == null ? new TNull(String.class)
						: orderParm.getData("NS_USER"));
		result.setData(
				"EXEC_NOTE",
				orderParm.getData("EXEC_NOTE") == null ? new TNull(String.class)
						: orderParm.getData("EXEC_NOTE"));
		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE"));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG") == null ? new TNull(String.class)
						: orderParm.getData("BILL_FLG"));

		result.setData("CASHIER_CODE",
				orderParm.getData("CASHIER_CODE") == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_CODE"));
		result.setData("CASHIER_DATE",
				orderParm.getData("CASHIER_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("CASHIER_DATE"));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE"));
		result.setData("PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_RETN_DATE"));
		result.setData("TRANSMIT_RSN_CODE", orderParm
				.getData("TRANSMIT_RSN_CODE") == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE"));

		result.setData("STOPCHECK_USER",
				orderParm.getData("STOPCHECK_USER") == null ? new TNull(
						String.class) : orderParm.getData("STOPCHECK_USER"));
		result.setData("STOPCHECK_DATE",
				orderParm.getData("STOPCHECK_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("STOPCHECK_DATE"));
		result.setData("IBS_CASE_NO",
				orderParm.getData("IBS_CASE_NO") == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO"));
		result.setData("IBS_CASE_NO_SEQ",
				orderParm.getData("IBS_CASE_NO_SEQ") == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO_SEQ"));

		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);

		return result;
	}

	/**
	 * 出院带药使用直接从ODI_ORDER中COPY数据到ODI_DSPND
	 * 
	 * @param orderParm
	 *            TParm
	 * @param optUser
	 *            String
	 * @param optDate
	 *            Timestamp
	 * @param optTerm
	 *            String
	 * @return TParm
	 */
	private TParm getDataDspnd(TParm orderParm, String orderDateTime,
			String optUser, Timestamp optDate, String optTerm, String takeMedOrg) {
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO", 0));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO", 0));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ", 0));
		result.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY", 0));// add
																					// by
																					// wanglong
																					// 20121210
		result.setData("ORDER_DATE", orderDateTime.substring(0, 8));
		result.setData("ORDER_DATETIME", orderDateTime.substring(8));

		result.setData("BATCH_CODE",
				orderParm.getData("BATCH_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("BATCH_CODE", 0));
		result.setData("TREAT_START_TIME", orderParm.getData(
				"TREAT_START_TIME", 0) == null ? new TNull(String.class)
				: orderParm.getData("TREAT_START_TIME", 0));
		result.setData("TREAT_END_TIME",
				orderParm.getData("TREAT_END_TIME", 0) == null ? new TNull(
						String.class) : orderParm.getData("TREAT_END_TIME", 0));
		result.setData("NURSE_DISPENSE_FLG", orderParm.getData(
				"NURSE_DISPENSE_FLG", 0) == null ? new TNull(String.class)
				: orderParm.getData("NURSE_DISPENSE_FLG", 0));
		result.setData("BAR_CODE",
				orderParm.getData("BAR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("BAR_CODE", 0));

		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE", 0));
		result.setData(
				"MEDI_QTY",
				orderParm.getData("MEDI_QTY", 0) == null ? 0.00 : orderParm
						.getData("MEDI_QTY", 0));
		result.setData("MEDI_UNIT",
				orderParm.getData("MEDI_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("MEDI_UNIT", 0));
		result.setData(
				"DOSAGE_QTY",
				orderParm.getData("DOSAGE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DOSAGE_QTY", 0));
		result.setData("DOSAGE_UNIT",
				orderParm.getData("DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSAGE_UNIT", 0));

		result.setData(
				"TOT_AMT",
				orderParm.getData("TOT_AMT", 0) == null ? 0.00 : orderParm
						.getData("TOT_AMT", 0));
		result.setData("DC_DATE",
				orderParm.getData("DC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("DC_DATE", 0));
		result.setData("PHA_DISPENSE_NO",
				orderParm.getData("PHA_DISPENSE_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_DISPENSE_NO", 0));
		result.setData("PHA_DOSAGE_CODE",
				orderParm.getData("PHA_DOSAGE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_DOSAGE_CODE", 0));
		result.setData(
				"PHA_DOSAGE_DATE",
				orderParm.getData("PHA_DOSAGE_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_DOSAGE_DATE",
						0));

		result.setData("PHA_DISPENSE_CODE", orderParm
				.getData("NS_EXEC_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("NS_EXEC_CODE", 0));
		result.setData("PHA_DISPENSE_DATE",
				orderParm.getData("NS_EXEC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE", 0));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE", 0));
		result.setData("NS_EXEC_DATE",
				orderParm.getData("NS_EXEC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE", 0));
		result.setData("NS_EXEC_DC_CODE",
				orderParm.getData("NS_EXEC_DC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_DC_CODE", 0));

		result.setData(
				"NS_EXEC_DC_DATE",
				orderParm.getData("NS_EXEC_DC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DC_DATE",
						0));
		result.setData("NS_USER",
				orderParm.getData("NS_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_USER", 0));
		result.setData("EXEC_NOTE",
				orderParm.getData("EXEC_NOTE", 0) == null ? new TNull(
						String.class) : orderParm.getData("EXEC_NOTE", 0));
		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE", 0));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("BILL_FLG", 0));

		result.setData("CASHIER_CODE",
				orderParm.getData("CASHIER_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_CODE", 0));
		result.setData("CASHIER_DATE",
				orderParm.getData("CASHIER_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("CASHIER_DATE", 0));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE", 0));
		result.setData(
				"PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm
						.getData("PHA_RETN_DATE", 0));
		result.setData("TRANSMIT_RSN_CODE", orderParm.getData(
				"TRANSMIT_RSN_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE", 0));

		result.setData("STOPCHECK_USER",
				orderParm.getData("STOPCHECK_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("STOPCHECK_USER", 0));
		result.setData(
				"STOPCHECK_DATE",
				orderParm.getData("STOPCHECK_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("STOPCHECK_DATE",
						0));
		result.setData("IBS_CASE_NO",
				orderParm.getData("IBS_CASE_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO", 0));
		result.setData("IBS_CASE_NO_SEQ",
				orderParm.getData("IBS_CASE_NO_SEQ", 0) == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO_SEQ", 0));

		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);
		// 领药注记默认为2
		result.setData("TAKEMED_ORG", takeMedOrg);
		return result;
	}

	/**
	 * 根据caseNo得到该病人的长期处置 PS:当不支持多病人审核的时候每次只有一条caseNo
	 */
	private TParm getAnOrder(String caseNo, String orderNo, String orderSeq,
			String setMainFlg, String orderSetGroupNo) {
		TParm result = new TParm();
		String SelSql = "";
		// 是否是集合医嘱主项
		boolean isSetOrder = "Y".equals(setMainFlg);
		// ------------处理集合医嘱--------------start----------------------------
		// 如果该条为集合医嘱找出所有项：主项，细项一起保存
		if (isSetOrder) {
			SelSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo
					+ "' AND ORDER_NO='" + orderNo + "' AND ORDERSET_GROUP_NO="
					+ Integer.parseInt(orderSetGroupNo);
		} // ----------------------------------end--------------------------------
		else { // 普通医嘱
			SelSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo
					+ "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='"
					+ orderSeq + "'";

		}
		// 得到该病人所有该执行展开的处置
		result = new TParm(TJDODBTool.getInstance().select(SelSql));

		return result;
	}

	/**
	 * 检查该医嘱是否存在
	 * 
	 * @param caseNo
	 *            String
	 * @param orderNo
	 *            String
	 * @param orderSeq
	 *            String
	 * @param startDttm
	 *            String
	 * @return boolean
	 */
	private boolean checkIsExistDspnd(String caseNo, String orderNo,
			String orderSeq, String orderDate, String orderDateTime) {
		String checkSql = "SELECT COUNT(OPT_USER) AS EXIST FROM ODI_DSPND WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq
				+ "' AND ORDER_DATE='"
				+ orderDate
				+ "' AND ORDER_DATETIME='" + orderDateTime + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		return TypeTool.getBoolean(result.getData("EXIST", 0));
	}

	// 护士插入记录
	private boolean checkIsExistDspnm(String caseNo, String orderNo,
			String orderSeq, String startDttm) {
		String checkSql = "SELECT COUNT(OPT_USER) AS EXIST FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq + "' AND START_DTTM='" + startDttm + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		return TypeTool.getBoolean(result.getData("EXIST", 0));

	}

	// 护士取消记录(所有展开的)
	private boolean checkIsNSDCExistDspnm(String caseNo, String orderNo,
			String orderSeq) {
		String checkSql = "SELECT COUNT(OPT_USER) AS EXIST FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq + "' AND DC_NS_CHECK_DATE IS NOT NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		return TypeTool.getBoolean(result.getData("EXIST", 0));

	}

	/**
	 * 该医嘱的ORDER_CODE、配药数量
	 * 
	 * @param orderCode
	 *            String
	 * @param doseageQty
	 *            double
	 * @return double
	 */
	private TParm getPrice(String caseNo, String orderCode, double doseageQty,
			String serviceLevel) {
		return BIL.getPriceForODI(caseNo, orderCode, doseageQty, serviceLevel);
	}

	/**
	 * 查询未执行医嘱
	 * 
	 * @param caseNo
	 * @param orderNo
	 * @param orderSeq
	 * @return
	 */
	public TParm queryUnExecOrder(String caseNo, String orderNo, String orderSeq) {// add
																					// by
																					// wanglong
																					// 20130710
		String sql = "SELECT COUNT(*) NUM FROM ODI_DSPNM "
				+ " WHERE CASE_NO = '#' AND ORDER_NO = '#' "
				+ " AND ORDER_SEQ = #  AND NS_EXEC_CODE IS NULL AND DC_NS_CHECK_DATE IS NULL";
		sql = sql.replaceFirst("#", caseNo);
		sql = sql.replaceFirst("#", orderNo);
		sql = sql.replaceFirst("#", orderSeq);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 术中医嘱，审核主方法
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onCheckOPOrder(TParm parm, TConnection connection) {// wanglong
																		// add
																		// 20140707
		TParm result = new TParm();
		// 前台传的数据
		TParm dataParm = parm.getParm("dataParm");
		String optUser = (String) parm.getData("OPT_USER");
		Timestamp optDate = (Timestamp) parm.getData("OPT_DATE");
		String optTerm = (String) parm.getData("OPT_TERM");
		String takeMedOrg = "";
		// shibl 20121022 modify 利用caseNo将护士审核的医嘱按照就诊号分组
		Map pat = InwOrderExecTool.getInstance().groupByPatParm(dataParm);
		Iterator it = pat.values().iterator();
		while (it.hasNext()) {
			TParm patParm = (TParm) it.next();
			for (int i = 0; i < patParm.getCount(); i++) {
				// 支持多人审核
				// 循环1--医嘱：循环需要审核的遗嘱 PS:当为集合医嘱的时候返回主细项
				// 根据前台的CASE_NO,ORDER_NO,ORDER_SEQ找出一条医嘱，根据SETMAIN_FLG区分是否为集合医嘱
				TParm orderParm = getAnOrder(patParm.getValue("CASE_NO", i),
						patParm.getValue("ORDER_NO", i),
						patParm.getValue("ORDER_SEQ", i),
						patParm.getValue("SETMAIN_FLG", i),
						patParm.getValue("ORDERSETGROUP_NO", i));
				// 取药注记
				takeMedOrg = patParm.getValue("TAKEMED_ORG", i);
				// 记录当前UI显示的审核时间--由于会手动修改该时间
				Timestamp checkDate = patParm.getTimestamp("CHECK_DATETIME", i);
				// 循环每个病患的每一条长期处置数据，调用UDD接口生成需要展开的数据插入ODI_DSPND ODI_DSPNM
				int orderCount = orderParm.getCount();
				if (orderCount <= 0)
					continue;
				TParm parmCase = new TParm();
				parmCase.setData("CASE_NO", patParm.getValue("CASE_NO"));
				parmCase = ADMInpTool.getInstance().selectall(parmCase);
				String serviceLevel = parmCase.getValue("SERVICE_LEVEL", 0);
				// 循环2--集合医嘱/普通医嘱：(与M表1:1)数循环调用每一条医嘱的UDD接口，返回应该展开的数据
				for (int row = 0; row < orderCount; row++) {
					// 数据库中的一条数据--orderPram
					// 记录该医嘱的RX_KIND（UD-长期；ST-临时：交给UDD接口处理后返回）/（DS-出院带药：直接1：1：1展开）
					String orderRxKind = orderParm.getValue("RX_KIND", row);
					// PS：分支——(1)UD/ST (2)DS (3)IG
					if ("ST".equals(orderRxKind) || "OP".equals(orderRxKind)) {// 20151109
																				// WANGJC
																				// ADD
						// orderToUddParm记录给UDD接口的所需要的参数
						TParm orderRow = new TParm();
						orderRow = orderParm.getRow(row); // 一条ODI_ORDER的数据
						// System.out.println("==orderToUddParm111111=="
						// + orderToUddParm);
						// 调用UDD的接口得到相应的参数:传入的时候没有秒，回来的时候有秒
						String effDate = StringTool.getString(
								(Timestamp) orderRow.getData("EFF_DATE"),
								"yyyyMMddHHmm");
						Timestamp sysDate = TJDODBTool.getInstance()
								.getDBTime();
						// 调用UDD接口得到下一个摆药时间餐次点
						List dispenseDttm = TotQtyTool.getInstance()
								.getNextDispenseDttm(checkDate);
						// 如果没有下一餐时间点就继续循环
						if (dispenseDttm.size() == 0) {
							System.out
									.println(" WARNING：There is not the 'next DispenseDttm' here ");
							continue;
						}
						// 判断是药嘱还是处置-----false
						String cat1Type = (String) orderRow
								.getData("CAT1_TYPE");
						// 是否为处置抑或药嘱标记位
						boolean isOperation = "TRT".equalsIgnoreCase(cat1Type)
								|| "PLN".equalsIgnoreCase(cat1Type)
								|| "OTH".equalsIgnoreCase(cat1Type);
						// dataFromUdd用来接收从UDD返回来的数据需要展开的
						List dataFromUdd = new ArrayList();
						// 返回值包括两个[(1)list--该size就是D表该插入的行数；(2)Map--是需要的数据]--PS:如果是处置参数3:effDate的前8位+0000
						// 参数4:effDate的前8位+235959
						if (!isOperation) { // 药品
							dataFromUdd = TotQtyTool
									.getInstance()
									.getOdiStQty(
											effDate, // EFF_DATE
											StringTool.getString(
													(Timestamp) orderRow
															.getData("DC_DATE"),
													"yyyyMMddHHmm"), // DC_DATE
											dispenseDttm.get(0).toString(), // 下一次摆药时间START_DTTM
											dispenseDttm.get(1).toString(), // 下一次摆药时间END_DTTM
											orderRow, serviceLevel); // 整条数据
						} else { // 处置
							dataFromUdd = TotQtyTool
									.getInstance()
									.getOdiTrtStQty(
											orderRow,
											StringTool.getString(
													(Timestamp) orderRow
															.getData("EFF_DATE"),
													"yyyyMMddHHmm"),
											serviceLevel);
						}
						// 得到需要插到表中的数据：(PS:order表中的数据都在ODI中插入)
						// ORDER表的LASTDSPN_QTY ORDER_LASTDSPN_QTY
						// ORDER表的ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
						// ORDER表的ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
						// M/ORDER表的dispenseQty M_DISPENSE_QTY
						// M/ORDER表的dispenseUnit M_DISPENSE_UNIT
						// M/ORDER表的dosageQty M_DOSAGE_QTY
						// M/ORDER表的dosageUnit M_DOSAGE_UNIT
						// 得到返回的UDD返回的值
						Map otherData = (Map) dataFromUdd.get(1);
						double OlastDspnQty = (Double) otherData
								.get("ORDER_LASTDSPN_QTY") == null ? 0.0
								: (Double) otherData.get("ORDER_LASTDSPN_QTY");
						double OacumDspnQty = (Double) otherData
								.get("ORDER_ACUMDSPN_QTY") == null ? 0.0
								: (Double) otherData.get("ORDER_ACUMDSPN_QTY");
						double OacumMediQty = (Double) otherData
								.get("ORDER_ACUMMEDI_QTY") == null ? 0.0
								: (Double) otherData.get("ORDER_ACUMMEDI_QTY");
						double MdispenseQty = (Double) otherData
								.get("M_DISPENSE_QTY") == null ? 0.0
								: (Double) otherData.get("M_DISPENSE_QTY");
						String MdispenseUnit = (String) otherData
								.get("M_DISPENSE_UNIT");
						double MdosageQty = (Double) otherData
								.get("M_DOSAGE_QTY") == null ? 0.0
								: (Double) otherData.get("M_DOSAGE_QTY");
						String MdosageUnit = (String) otherData
								.get("M_DOSAGE_UNIT");
						// 是否存在且审核医嘱(非DC) SHIBL MODIFY 20120424
						if (orderRow.getValue("NS_CHECK_CODE").equals("")) {
							// 更新ODI_ORDER
							TParm finalDataToOrder = new TParm();
							String caseNoTemp = (String) orderRow
									.getData("CASE_NO");
							String orderNoTemp = (String) orderRow
									.getData("ORDER_NO");
							int orderSeqTemp = Integer.parseInt(orderRow
									.getData("ORDER_SEQ") + "");
							// ODI_ORDER表中的LAST_DSPN_DATE
							Timestamp lastDspnDateForOrder = optDate;
							// 更新ODI_ORDER的字段LASTDSPN_QTY LAST_DSPN_DATE
							finalDataToOrder = getDataOrder(orderRow,
									caseNoTemp, orderNoTemp, orderSeqTemp,
									lastDspnDateForOrder, OlastDspnQty,
									OacumDspnQty, OacumMediQty, MdispenseQty,
									MdispenseUnit, MdosageQty, MdosageUnit,
									optUser, optDate, optTerm, checkDate,
									takeMedOrg);
							result = InwForOdiTool.getInstance()
									.updateOdiOrder(finalDataToOrder,
											connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							if (result.getInt("RETURN") == 0) {// 针对没有更新到数据
								result.setErrCode(-100);
								result.setErrText("审核数据中"
										+ orderRow.getValue("ORDER_DESC")
										+ "已不存在,请重新查询");
								return result;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 术中医嘱，取消审核主方法
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onUndoCheckOPOrder(TParm parm, TConnection connection) { // wanglong
																			// add
																			// 20140707
		TParm result = new TParm();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			TParm undoData = parm.getRow(i); // 前台传的数据
			// 取消审核的时候只清空审核标记
			undoData.setData("NS_CHECK_DATE", new TNull(Timestamp.class));
			undoData.setData("NS_CHECK_CODE", new TNull(String.class));
			result = InwForOdiTool.getInstance().updateOdiOrderForUndoCk(
					undoData, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * 术中医嘱，计费方法
	 * 
	 * @param orderParm
	 * @param connection
	 * @return
	 */
	public TParm onFee(TParm orderParm, TConnection connection) { // wanglong
																	// add
																	// 20140707
		TParm forIBSParm1 = new TParm();
		String SelSql = "SELECT * FROM ADM_INP WHERE CASE_NO='#'".replaceFirst(
				"#", orderParm.getValue("CASE_NO", 0));
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(SelSql));
		forIBSParm1.setData("CTZ1_CODE", ctzParm.getData("CTZ1_CODE", 0));
		forIBSParm1.setData("CTZ2_CODE", ctzParm.getData("CTZ2_CODE", 0));
		forIBSParm1.setData("CTZ3_CODE", ctzParm.getData("CTZ3_CODE", 0));
		forIBSParm1.setData("BED_NO", ctzParm.getData("BED_NO", 0));
		forIBSParm1.setData("FLG", orderParm.getData("FLG"));
		forIBSParm1.setData("M", orderParm.getData());
		TParm resultFromIBS = IBSTool.getInstance()
				.getIBSOrderData(forIBSParm1);
		resultFromIBS.setData("EXE_DEPT_CODE", 0,
				orderParm.getData("EXE_DEPT_CODE", 0));
		resultFromIBS.setData("BED_NO", 0, forIBSParm1.getData("BED_NO"));
		TParm forIBSParm2 = new TParm();
		forIBSParm2.setData("DATA_TYPE", orderParm.getValue("DATA_TYPE")); // 耗费记录调用标记5
		forIBSParm2.setData("M", resultFromIBS.getData());
		forIBSParm2.setData("FLG", orderParm.getData("FLG"));
		TParm result = IBSTool.getInstance().insertIBSOrder(forIBSParm2,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 校验病区库存以决定临时医嘱的取药部门
	 * 
	 * @param parm
	 * @return
	 * @author wangbin 2015/1/22
	 */
	public TParm getIndQty(TParm parm) {
		String stationCode = parm.getValue("STATION_CODE");
		String regionCode = parm.getValue("REGION_CODE");
		String orderCode = parm.getValue("ORDER_CODE");

		// 构造返回结果
		TParm result = new TParm();
		result.setData("STATION_CODE", stationCode);
		result.setData("REGION_CODE", regionCode);
		TParm searchParm = new TParm();
		StringBuilder sbSql = new StringBuilder();

		// 查药品对应的库存数
		sbSql = new StringBuilder();
		sbSql.append("SELECT C.ORDER_CODE, SUM (C.TOT_QTY) QTY FROM (");
		sbSql.append(" SELECT A.ORDER_CODE, SUM (A.STOCK_QTY) AS TOT_QTY");
		sbSql.append(" FROM IND_STOCK A");
		sbSql.append(" WHERE A.ORG_CODE = '");
		sbSql.append(stationCode);
		sbSql.append("' AND A.ORDER_CODE = '");
		sbSql.append(orderCode);
		sbSql.append("' GROUP BY A.ORDER_CODE");
		sbSql.append(" UNION ALL ");
		sbSql.append(" SELECT B.ORDER_CODE, COUNT (B.TOXIC_ID) AS TOT_QTY");
		sbSql.append(" FROM IND_CABINET A, IND_CONTAINERD B");
		sbSql.append(" WHERE B.CABINET_ID = A.CABINET_ID ");
		sbSql.append("AND A.ORG_CODE = '");
		sbSql.append(stationCode);
		sbSql.append("' AND B.ORDER_CODE = '");
		sbSql.append(orderCode);
		sbSql.append("' GROUP BY B.ORDER_CODE");
		sbSql.append(") C GROUP BY C.ORDER_CODE");

		searchParm = new TParm(TJDODBTool.getInstance()
				.select(sbSql.toString()));

		if (searchParm.getErrCode() < 0) {
			// err("ERR:" + searchParm.getErrCode() + " "
			// + searchParm.getErrText());
			result.setErrCode(searchParm.getErrCode());
			result.setErrText(searchParm.getErrText());
			return result;
		}

		result.setData("QTY", searchParm.getDouble("QTY", 0));
		result.setData("ORDER_CODE", orderCode);

		return result;
	}
}
