package action.bil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import jdo.bil.SPCINVRecordTool; //import jdo.bil.String2TParmTool;
//import jdo.bil.client.BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client;

import jdo.ibs.IBSTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

public class SPCINVRecordAction extends TAction {
	// 第一 把这个格式化改好 第二 加异常处理 出现exception的时候把connection关闭

	/**
	 *删除耗用记录数据
	 * */
	public TParm onDeleteFromDB(TParm parm) {
		TParm result = new TParm();
		TConnection connection = this.getConnection();
		result = SPCINVRecordTool.getInstance().deleteFromDB(parm, connection);
		if (result.getErrCode() < 0) {

			connection.rollback();
			connection.close();
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 根据病案号查询
	 * 
	 * @param parm
	 * @return 
	 *         MR_NO;IPD_NO;PAT_NAME;BIRTH_DATE;SEX_CODE;CASE_NO;DEPT_CODE;DR_CODE
	 */
	public TParm onMrNo(TParm parm) {
		TParm result = new TParm();
		String mrNo = parm.getData("MR_NO").toString();
		String adm_type = parm.getData("ADM_TYPE").toString();
		String mr_no = PatTool.getInstance()
				.checkMrno(TypeTool.getString(mrNo));
		// 住院病人
		if (adm_type.equals("I")) {
			String sql = "SELECT A.MR_NO, A.IPD_NO, A.PAT_NAME, A.BIRTH_DATE, A.SEX_CODE, B.CASE_NO,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE AS DR_CODE"
					+ " FROM ADM_INP B, SYS_PATINFO A "
					+ " WHERE A.MR_NO = '"
					+ mr_no
					+ "' "
					+ "  AND A.MR_NO = B.MR_NO "
					+ " AND B.DS_DATE IS NULL "
					+ " AND B.IN_DATE IS NOT NULL "
					+ " AND B.CANCEL_FLG <> 'Y'";
			System.out.println("sql==" + sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
		}
		// 门急诊病人
		if (adm_type.equals("O") || adm_type.equals("E")) {
			java.sql.Timestamp sysDate = SystemTool.getInstance().getDate();
			String reg_date_start = sysDate.toString().substring(0, 10)
					+ " 00:00:00";
			String reg_date_end = sysDate.toString().substring(0, 10)
					+ " 23:59:59";
			String sql = "SELECT A.MR_NO, A.IPD_NO, A.PAT_NAME, A.BIRTH_DATE, A.SEX_CODE, B.CASE_NO,B.DEPT_CODE,B.DR_CODE   "
					+ " FROM   SYS_PATINFO A,REG_PATADM B    "
					+ " WHERE A.MR_NO = B.MR_NO "
					+ " AND A.MR_NO = '"
					+ mrNo
					+ "' "
					+ " AND B.REG_DATE BETWEEN TO_DATE('"
					+ reg_date_start
					+ "','yyyy-mm-dd hh24:mi:ss') AND TO_DATE('"
					+ reg_date_end
					+ "','yyyy-mm-dd hh24:mi:ss')" + " ORDER BY B.CASE_NO DESC";
			// System.out.println("sql=="+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
		}
		// System.out.println("----action-------onMrNo------>"+result);
		return result;
	}

	/**
	 * 保存计费
	 * */
	public TParm onSaveFee(TParm parms) {
		System.out.println("-------------onSaveFee-------------");
		TParm result = new TParm();
		TConnection connection = getConnection();
		TParm resultParm = new TParm();
		TParm IBSOrddParm = parms.getParm("ibsOrddParm");
		TParm invParm = parms.getParm("invParm");
		TParm parmFlg = parms.getParm("parmFlg");
		TParm parmPack = parms.getParm("parmPack");
		for (int i = 0; i < IBSOrddParm.getCount("CASE_NO"); i++) {// wanglong
			// add
			// 20140626
			String checkSql = "SELECT BUSINESS_NO,SEQ FROM SPC_INV_RECORD WHERE BUSINESS_NO='#' AND SEQ=# AND CASE_NO_SEQ IS NOT NULL";
			checkSql = checkSql.replaceFirst("#", IBSOrddParm.getValue(
					"BUSINESS_NO", i));
			checkSql = checkSql.replaceFirst("#", IBSOrddParm
					.getValue("SEQ", i));
			// System.out.println("-----onSaveFee---checkSql---"+checkSql);
			TParm checkParm = new TParm(TJDODBTool.getInstance().select(
					checkSql));
			if (checkParm.getErrCode() < 0) {
				return TParm.newErrParm(-1, "查询提交状态失败");
			}
			if (checkParm.getCount() > 0) {
				return TParm.newErrParm(-1, "有物品已提交计费，请清空界面重新操作");
			}
		}
		for (int i = 0; i < IBSOrddParm.getCount("CASE_NO"); i++) {// wanglong
			// add
			// 20141014
			if (i == 0
					|| !IBSOrddParm.getValue("BUSINESS_NO", i).equals(
							IBSOrddParm.getValue("BUSINESS_NO", i - 1))) {
				String BillDateSql = "SELECT DISTINCT CASE_NO,BILL_DATE FROM SPC_INV_RECORD WHERE BUSINESS_NO='#' AND BILL_DATE IS NOT NULL AND CASE_NO_SEQ IS NULL";
				BillDateSql = BillDateSql.replaceFirst("#", IBSOrddParm
						.getValue("BUSINESS_NO", i));
				TParm billDateParm = new TParm(TJDODBTool.getInstance().select(
						BillDateSql));
				if (billDateParm.getErrCode() < 0) {
					return TParm.newErrParm(-1, "查询提交状态失败");
				}
				if (billDateParm.getCount() > 0) {
					// BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client
					// BILSPCINVWsToolImplClient2 = new
					// BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();
					String billDateStr = StringTool.getString(billDateParm
							.getTimestamp("BILL_DATE", 0),
							"yyyy/MM/dd HH:mm:ss");
					// boolean isBilled =
					// BILSPCINVWsToolImplClient2.onCheckFeeState(billDateParm.getValue("CASE_NO",
					// 0), billDateStr);
					String checksql = "SELECT * FROM IBS_ORDM A, IBS_ORDD B           "
							+ " WHERE A.CASE_NO = B.CASE_NO         "
							+ "   AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "
							+ "   and A.DATA_TYPE = '5'            "
							+ "   AND B.CASE_NO = '#'           "
							+ "   AND B.BILL_DATE =  TO_DATE('#', 'MM/DD/YYYY HH24:MI:SS') ";
					checksql = checksql.replaceFirst("", billDateParm.getValue(
							"CASE_NO", 0));
					checksql = checksql.replaceFirst("", billDateStr);
					TParm checkParm = new TParm(TJDODBTool.getInstance()
							.select(checksql));
					if (checkParm.getCount() > 0) {
						return TParm.newErrParm(-1, "有物品已提交计费，请联系系统维护人员");
					}
				}
			}
		}

		// System.out.println("---------"+invParm);
		// 扣库 高值
		// fux 问题 SYS_DEPT 缺少介入寄售库 SYS_DEPT 触发器问题
		if (invParm.getCount("INV_CODE") > 0) {
			result = this.onSaveStock(invParm, connection);// ////////////////★★★★★★
			if (result.getErrCode() < 0) {

				connection.rollback();
				connection.close();
				return result;

			}
		}
		// 扣库 手术包
		if (parmPack.getCount("PACK_CODE") > 0) {
			// System.out.println("扣库  手术包");
			result = this.onSavePack(parmPack, connection);// ////////////////★★★★★★
			if (result.getErrCode() < 0) {

				connection.rollback();
				connection.close();
				return result;

			}
		}
		// System.out.println("IBSOrddParm::"+IBSOrddParm);
		// 根据开关 spc_flg判断是否是物联网 如果是物联网，呼叫HIS的WS接口，进行计费，否则，直接计费
		String sqlSpcFlg = "SELECT SPC_FLG FROM SYS_REGION WHERE REGION_CODE = '"
				+ IBSOrddParm.getData("REGION_CODE") + "'";
		// System.out.println(sqlSpcFlg);
		TParm spcFlgParm = new TParm(TJDODBTool.getInstance().select(sqlSpcFlg));
		// System.out.println(spcFlgParm);
		if (spcFlgParm.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		// 是物联网 调用webservice接口
		// if(spcFlgParm.getData("SPC_FLG",0).toString().equals("Y")){
		// System.out.println("这里111111111111111");
		// System.out.println("原来是这样"+IBSOrddParm);
		// System.out.println("----------onSaveFee-------IBSOrddParm---------------"+IBSOrddParm);
		if (!(IBSOrddParm.getData("CASE_NO", 0) == null || IBSOrddParm.getData(
				"CASE_NO", 0).equals(""))) {
			// System.out.println("这里2222222222222");
			// 住院
			if (IBSOrddParm.getData("ADM_TYPE").equals("I")) {
				// 调用IBS接口返回费用数据

				// **************************************************
				// System.out.println("走这块了。333。。");
				// BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client
				// BILSPCINVWsToolImplClient1 = new
				// BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();
				// String2TParmTool tool = new String2TParmTool();
				// String inString = tool.tparm2String(IBSOrddParm);
				// //
				// System.out.println("---------------SPCINVRecordAction--------------onFeeData--------");
				//	    		
				// String onFeeData =
				// BILSPCINVWsToolImplClient1.onFeeData(inString);//////////★★★★★SPCINVRecordAction.
				// feeData()本类中
				// TParm forIBSParm1 = tool.string2Ttparm(onFeeData);
				TParm forIBSParm1 = this.feeData(IBSOrddParm);
				// Map data = forIBSParm1.getData();
				TParm parmForFee = new TParm();
				parmForFee.setData("forIBSParm1", forIBSParm1.getData());
				parmForFee.setData("IBSOrddParm", IBSOrddParm.getData());
				// TParm parmM= IBSOrddParm;

				TParm param = new TParm();
				param.setData("BUSINESS_NO", IBSOrddParm.getData("BUSINESS_NO",
						0));// where条件
				param.setData("SEQ", IBSOrddParm.getData("SEQ", 0));// where条件
				param.setData("CASE_NO_SEQ", "");
				param.setData("SEQ_NO", "");
				param.setData("CASHIER_CODE", "");
				param.setData("BILL_DATE", IBSOrddParm
						.getTimestamp("BILL_DATE"));
				// System.out.println("=====================param--->"+param);
				// ========wanglong add 20141014
				// 用以解决HIS死机时89重复提交问题，以BILL_DATE作为是否已经提交的判断字段
				result = SPCINVRecordTool.getInstance().updSpcInvRecord(param,
						connection);

				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}

				// String inString1 = tool.tparm2String(forIBSParm1);
				// String inString2 = tool.tparm2String(IBSOrddParm);
				// String inStringM = tool.tparm2String(parmM);
				// String inStringM = tool.tparm2String(IBSOrddParm);
				// System.out.println("---------------------住院开始执行ws------------------------------");
				// BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client
				// BILSPCINVWsToolImplClient = new
				// BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();
				// System.out.println("-----------SPCINVRecordAction------------------insertIBSOrder--------");

				// String returnDto=
				// BILSPCINVWsToolImplClient.insertIBSOrder(inString1,inString2,inStringM);//★★★★★SPCINVRecordAction.
				// countFee()

				result = this.countFee(forIBSParm1, IBSOrddParm, IBSOrddParm);

				// System.out.println("----------------------住院执行了没有完了-------------------------");

				// System.out.println("returnDto--->"+returnDto);
				if (result.getErrCode() < 0) {
					// System.out.println("---------没有返回值-------");
					// result.setErr(-1, "HIS没有返回值-1");
					connection.rollback();
					connection.close();
					return result;
				} else {
					// TParm resultParms = tool.string2Ttparm(returnDto);
					if (result.getCount("CASE_NO_SEQ") > 0) {
						for (int i = 0; i < result.getCount("CASE_NO_SEQ"); i++) {
							resultParm.addData("CASE_NO_SEQ", result.getValue(
									"CASE_NO_SEQ", i));
							resultParm.addData("SEQ_NO", result.getValue(
									"SEQ_NO", i));
						}
					}

				}

				if (resultParm.getCount("CASE_NO_SEQ") <= 0) {
					connection.rollback();
					connection.close();
					return TParm.newErrParm(-1, "HIS没有返回值-2");
				}

			}

		}

		// 4.物联网接到返参，将对应字段写入
		if (resultParm.getCount("CASE_NO_SEQ") > 0) {

			for (int i = 0; i < resultParm.getCount("CASE_NO_SEQ"); i++) {
				TParm inParm = new TParm();
				inParm.setData("BUSINESS_NO", IBSOrddParm.getData(
						"BUSINESS_NO", i));// where条件
				inParm.setData("SEQ", IBSOrddParm.getData("SEQ", i));// where条件
				inParm.setData("CASE_NO_SEQ", resultParm.getData("CASE_NO_SEQ",
						i));
				inParm.setData("SEQ_NO", resultParm.getData("SEQ_NO", i));
				inParm.setData("CASHIER_CODE", parms.getData("CASHIER_CODE"));
				inParm.setData("BILL_DATE", IBSOrddParm
						.getTimestamp("BILL_DATE"));// wanglong add 20141014
				// System.out.println("=====================inParm--->"+inParm);
				result = SPCINVRecordTool.getInstance().updSpcInvRecord(inParm,
						connection);

				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			}

			for (int i = 0; i < parmFlg.getCount("BUSINESS_NO"); i++) {// wanglong
				// modify
				// 20150305
				TParm updSpcInvRecordStockFlgParm = new TParm();
				updSpcInvRecordStockFlgParm.setData("BUSINESS_NO", parmFlg
						.getData("BUSINESS_NO", i));// where条件
				updSpcInvRecordStockFlgParm.setData("SEQ", parmFlg.getData(
						"SEQ", i));// where条件
				result = SPCINVRecordTool.getInstance().updCommitFlg(
						updSpcInvRecordStockFlgParm, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			}

		}

		// }

		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * 获得计费的基础数据
	 * */
	public TParm feeData(TParm IBSOrddParm) {
		TParm result = new TParm();
		TParm forIBSParm1 = new TParm();
		String ctz1Code = "";
		String ctz2Code = "";
		String ctz3Code = "";
		String bed_no = "";
		String SelSql = "SELECT *" + " FROM ADM_INP WHERE CASE_NO='"
				+ IBSOrddParm.getValue("CASE_NO", 0) + "'";
		// System.out.println("---------feeData------->"+SelSql);
		// 得到该病人所有该执行展开的处置
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(SelSql));

		forIBSParm1.setData("CTZ1_CODE", ctzParm.getData("CTZ1_CODE", 0));
		forIBSParm1.setData("CTZ2_CODE", ctzParm.getData("CTZ2_CODE", 0));
		forIBSParm1.setData("CTZ3_CODE", ctzParm.getData("CTZ3_CODE", 0));
		forIBSParm1.setData("BED_NO", ctzParm.getData("BED_NO", 0));
		forIBSParm1.setData("FLG", IBSOrddParm.getData("FLG", 0));// parm

		// result.setData("forIBSParm1",forIBSParm1);

		return forIBSParm1;
	}

	/**
	 * 手术包扣库存
	 * */
	public TParm onSavePack(TParm parms, TConnection connection) {
		TParm result = new TParm();
		// 循环扣包的库存
		// System.out.println("手术包扣库存参数---》"+parms);

		for (int i = 0; i < parms.getCount("PACK_CODE"); i++) {
			if (parms.getData("PACK_CODE", i) == null) {
				continue;
			}
			String pack_code = parms.getData("PACK_CODE", i).toString();

			String sqlInvSupDispenseD = "SELECT DISPENSE_NO,SEQ_NO,QTY,ACTUAL_QTY,PACK_BATCH_NO FROM INV_SUP_DISPENSED WHERE INV_CODE = '"
					+ pack_code + "' AND ACTUAL_QTY>0 ORDER BY DISPENSE_NO";
			// System.out.println("sqlInvSupDispenseD--->"+sqlInvSupDispenseD);
			TParm parmSupD = new TParm(TJDODBTool.getInstance().select(
					sqlInvSupDispenseD));
			if (parmSupD.getCount("DISPENSE_NO") < 0) {
				result.setErrCode(-3);
				result.setErrText(pack_code + "包库存不足");
				connection.rollback();
				connection.close();
				return result;
			}
			TParm updInvSupDispenseDParm = new TParm();
			String dispenseNo = parmSupD.getValue("DISPENSE_NO", 0);
			Integer seqNo = parmSupD.getInt("SEQ_NO", 0);
			Integer actualQty = parmSupD.getInt("ACTUAL_QTY", 0);
			Integer qtySub = 1;
			if (actualQty < 1) {
				qtySub = actualQty;
			}
			int packBatchNo = parmSupD.getInt("PACK_BATCH_NO", 0);
			updInvSupDispenseDParm.setData("DISPENSE_NO", dispenseNo);
			updInvSupDispenseDParm.setData("SEQ_NO", seqNo);
			updInvSupDispenseDParm.setData("QTY", qtySub);
			result = SPCINVRecordTool.getInstance().updInvSupDispenseD(
					updInvSupDispenseDParm, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
			}

			// 根据主项查询包细项
			String sql = "SELECT INV_CODE,QTY FROM INV_PACKD WHERE PACK_CODE = '"
					+ pack_code + "'";
			// System.out.println("根据主项查询包细项sql"+sql);
			TParm invPackDParm = new TParm(TJDODBTool.getInstance().select(sql));
			// 根据DISPENSE_NO，PACK_BATCH_NO，INV_CODE 查询库存细项
			if (invPackDParm.getCount("INV_CODE") > 0) {

				for (int m = 0; m < invPackDParm.getCount("INV_CODE"); m++) {
					int qty = invPackDParm.getInt("QTY", m);
					String inv_code = invPackDParm.getValue("INV_CODE", m);

					String sqlSubDD = "SELECT QTY FROM INV_SUP_DISPENSEDD "
							+ " WHERE DISPENSE_NO='" + dispenseNo + "' "
							+ " AND PACK_CODE = '" + pack_code + "'"
							+ " AND PACK_BATCH_NO= '" + packBatchNo + "' "
							+ " AND INV_CODE = '" + inv_code + "'";
					// System.out.println("sqlSubDD--->"+sqlSubDD);
					TParm parmSubDD = new TParm(TJDODBTool.getInstance()
							.select(sqlSubDD));
					if (parmSubDD.getCount() < 0) {
						result.setErrCode(-4);
						result.setErrText("查无数据");

						connection.rollback();
						connection.close();
					}

					int qtyOld = parmSubDD.getInt("QTY", 0);
					// System.out.println("qtyOld-->"+qtyOld);
					if (qtyOld < qty) {
						// System.out.println("-----------");
						qty = qtyOld;
					}
					TParm parmInvSupDispenseDD = new TParm();
					parmInvSupDispenseDD.setData("QTY", qty);
					parmInvSupDispenseDD.setData("INV_CODE", inv_code);
					parmInvSupDispenseDD.setData("DISPENSE_NO", dispenseNo);
					parmInvSupDispenseDD.setData("PACK_CODE", pack_code);
					parmInvSupDispenseDD.setData("PACK_BATCH_NO", packBatchNo);
					// System.out.println("qty--->"+qty);
					result = SPCINVRecordTool.getInstance()
							.updInvSupDispenseDD(parmInvSupDispenseDD,
									connection);
					if (result.getErrCode() < 0) {
						connection.rollback();
						connection.close();
					}
				}

			}

		}
		return result;
	}

	/**
	 * 扣库方法
	 * */
	public TParm onSaveStock(TParm parms, TConnection connection) {
		TParm result = new TParm();
		// 按类别进行扣库，数量大于0，扣库，数量小于零，增库
		TParm invParm = parms;
		// System.out.println("invParm--------->"+invParm);
		result = this.onSaveINV(invParm, connection);
		// System.out.println("result:::"+result);
		if (result == null || result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}

		return result;
	}

	/**
	 * 保存
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("参数错误");
			return result;
		}
		// 取得链接
		TConnection conn = getConnection();

		result = SPCINVRecordTool.getInstance().onSave(parm, conn);
		if (result.getErrCode() != 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 保存
	 * */
	public TParm onSaveParm(TParm param) {
		TParm result = new TParm();
		TParm parm = param.getParm("SPC_INV_RECORD");
		if (parm == null) {
			return result.newErrParm(-1, "参数为空");
		}
		TConnection connection = getConnection();
		int count = parm.getCount("BAR_CODE");
		TParm temp=null;
		//========pangben 2015-10-16 添加临床路径字段
		for (int i = 0; i < count; i++) {
			temp=parm.getRow(i);
			temp.setData("SCHD_CODE",null==temp.getValue("SCHD_CODE")?"":temp.getValue("SCHD_CODE"));
			result = SPCINVRecordTool.getInstance().insertData(temp,
					connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 合并INV_CODE相同的项
	 * */
	public TParm onMerge(TParm parm) {
		TParm result = new TParm();
		// System.out.println("parm.getCount"+parm.getCount("INV_CODE"));
		if (parm.getCount("INV_CODE") < 0) {
			return result;
		}
		String inv_code = parm.getData("INV_CODE", 0).toString();
		result.addData("INV_CODE", inv_code);
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {

		}

		return result;
	}

	/**
	 * 计费方法
	 * */
	public TParm countFee(TParm parm1, TParm IBSOrddParm, TParm parmM) {// parmM就是IBSOrddParm
		// System.out.println("----countFee----------forIBSParm1------------"+parm1);//{Data={BED_NO=S14Y3507,
		// CTZ3_CODE=null, FLG=ADD, CTZ2_CODE=null, CTZ1_CODE=12}}
		// System.out.println("----countFee----------IBSOrddParm1------------"+parm2);
		// System.out.println("----countFee----------IBSOrddParm2------------"+parmM);
		TConnection connection = getConnection();
		TParm result = new TParm();
		try {

			TParm forIBSParm1 = parm1;
			forIBSParm1.setData("M", parmM.getData());
			Map data = forIBSParm1.getData();
			forIBSParm1 = new TParm(data);

			// TParm IBSOrddParm = parm2;
			// Map data2 = IBSOrddParm.getData();
			// IBSOrddParm = new TParm(data2);
			// System.out.println("-----------IBSOrddParm1---getData()---------"+IBSOrddParm);

			// System.out.println("forIBSParm1---------->"+forIBSParm1);
			TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(
					forIBSParm1);
			// System.out.println("------------------resultFromIBS-------->"+resultFromIBS);
			TParm forIBSParm2 = new TParm();

			resultFromIBS.setData("EXE_DEPT_CODE", 0, IBSOrddParm.getData(
					"EXE_DEPT_CODE", 0));
			resultFromIBS.setData("BED_NO", 0, forIBSParm1.getData("BED_NO"));
			forIBSParm2.setData("DATA_TYPE", "5"); // 耗费记录调用标记5
			forIBSParm2.setData("M", resultFromIBS.getData());
			forIBSParm2.setData("FLG", IBSOrddParm.getData("FLG"));

			// forIBSParm2.setData("BILL_DATE",
			// StringTool.getTimestamp(IBSOrddParm.getValue("BILL_DATE"),"yyyy/MM/dd HH:mm:ss"));//wanglong
			// add 20141014
			forIBSParm2.setData("BILL_DATE", IBSOrddParm.getData("BILL_DATE"));// wanglong
			// add
			// 20141014

			// TParm data3 = (TParm)forIBSParm2.getData("BILL_DATE");
			// for(int i = 0;i<forIBSParm2.getCount("CASE_NO");i++){
			// Timestamp time =
			// StringTool.getTimestamp(forIBSParm2.getValue("BILL_DATE",i).substring(0,19),
			// "yyyy-MM-dd HH:ss:mm");
			// forIBSParm2.setData("BILL_DATE", i, time);
			//			
			// }

			// try {
			//			
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// 调用IBS提供的Tool继续执行
			result = SPCINVRecordTool.getInstance().insertIBSOrder(forIBSParm2,
					connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				connection.rollback();
				connection.close();
			}
			connection.commit();
			connection.close();

			return result;

		} catch (Exception e) {
			e.printStackTrace();
			connection.rollback();
			connection.close();
		}

		return result;
	}

	/**
	 * 扣库方法
	 * */
	@SuppressWarnings("unchecked")
	public TParm onSaveINV(TParm parm, TConnection connection) {
		// System.out.println("扣库方法开始。。。。。。。。。。。");
		// System.out.println("parm--->"+parm);
		TParm result = new TParm();

		if (parm.getCount("INV_CODE") < 0) {
			return result;
		}
		HashMap map = (HashMap) parm.getData("MERGE");
		if (map.size() < 0) {
			return result;
		}
		// 如果是高值，将前面table的数据INV_CODE相同的数据合并处理 INV_STOCKM,INV_STOCKD
		Iterator ite = map.entrySet().iterator();
		while (ite.hasNext()) {

			// System.out.println("高值？？？？？？？？？");
			Entry<String, Double> entry = (Entry<String, Double>) ite.next();
			String inv_code = entry.getKey();// map中的key
			// System.out.println("---------->"+inv_code);
			Double qty = entry.getValue();// 上面key对应的value
			// System.out.println("---------->"+qty);
			// 扣库 INV_STOCKM和INV_STOCKD
			String orgCode = parm.getData("ORG_CODE", 0).toString();// 高值 暂时写死
			String opt_user = parm.getData("OPT_USER", 0).toString();
			String opt_term = parm.getData("OPT_TERM", 0).toString();
			// --------------------------1.高值---------------------------------------
			// step1.根据ORG_CODE,INV_CODE查询INV_STOCKM ORG_CODE 高值为介入室
			String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY "
					+ " FROM INV_STOCKM " + " WHERE ORG_CODE = '" + orgCode
					+ "' AND INV_CODE = '" + inv_code + "' ";// ORG_CODE暂时写死
			// System.out.println("selectInvStockM---->"+selectInvStockM);
			// step2.更新 INV_STOCKM表的字段 stock_qty(-m)
			result = new TParm(TJDODBTool.getInstance().select(selectInvStockM));
			// System.out.println("查询INV_STOCKM表数据"+result);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			String sql1 = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"
					+ inv_code + "'";
			TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
			String inv_chn_desc = parm1.getData("INV_CHN_DESC", 0).toString();
			String sql2 = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"
					+ orgCode + "'";
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
			String dept_chn_desc = parm2.getData("DEPT_CHN_DESC", 0).toString();
			if (result.getCount("ORG_CODE") < 0) {
				result.setErrCode(-2);
				result.setErrText(dept_chn_desc + "的" + inv_chn_desc + "查无此物资");
				connection.rollback();
				connection.close();
				return result;
				// 查无此物资
			}

			if (Double.parseDouble(result.getData("STOCK_QTY", 0).toString()) < qty) {
				// 库存不足
				result.setErrCode(-2);
				result.setErrText(dept_chn_desc + "的" + inv_chn_desc + "库存不足");
				connection.rollback();
				connection.close();
				return result;

			}

			TParm updInvStockMParm = new TParm();
			updInvStockMParm.setData("ORG_CODE", orgCode);
			updInvStockMParm.setData("INV_CODE", inv_code);
			updInvStockMParm.setData("STOCK_QTY", qty);
			updInvStockMParm.setData("OPT_USER", opt_user);
			updInvStockMParm.setData("OPT_TERM", opt_term);
			result = SPCINVRecordTool.getInstance().updInvStockM(
					updInvStockMParm, connection);

			if (result.getErrCode() < 0) {
				result.setErrText("保存失败！");
				connection.rollback();
				connection.close();
				return result;
			}

		}
		// 更新 INV_STOCKDD表数据
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			// System.out.println("第"+i+"次循环执行");
			String returnSql = "SELECT * FROM INV_RETURNHIGH WHERE RFID = '"
					+ parm.getData("RFID", i) + "'";
			if (parm.getData("FLG", i).toString().equals("HIGH")) {

				// System.out.println("第"+i+"次执行高值扣库");
				String inv_code = parm.getData("INV_CODE", i).toString();
				String orgCode = parm.getData("ORG_CODE", i).toString();
				String opt_user = parm.getData("OPT_USER", i).toString();
				String opt_term = parm.getData("OPT_TERM", i).toString();
				Double qty = parm.getDouble("QTY", i);// 扣库数量
				// step5.查询INV_STOCKDD 根据RFID查询 ，只有一条数据
				String selectInvStockDD = "SELECT INV_CODE,INVSEQ_NO,RFID,STOCK_QTY FROM INV_STOCKDD WHERE INV_CODE='"
						+ inv_code
						+ "' AND RFID = '"
						+ parm.getData("RFID", i)
						+ "'";
				// System.out.println("selectInvStockDD----->"+selectInvStockDD);
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockDD));
				// System.out.println("INV_STOCKDD表的数据：：："+result);
				if (result.getErrCode() < 0) {
					result.setErrText("保存失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				if (result.getCount("INV_CODE") < 0) {
					result.setErrText("查无条码号：" + parm.getData("RFID", i));
					// 查无数据
					connection.rollback();
					connection.close();
					return result;

				}
				TParm updInvStockDDParm = new TParm();
				updInvStockDDParm.setData("RFID", parm.getData("RFID", i));
				if (qty > 0) {
					Timestamp date = StringTool.getTimestamp(new Date());
					// System.out.println("20140408-------扣库------->");
					updInvStockDDParm.setData("WAST_FLG", "Y");
					updInvStockDDParm.setData("COST_FLG", "Y");
					updInvStockDDParm.setData("OUT_USER", opt_user);
					updInvStockDDParm.setData("OUT_DATE", date);
					updInvStockDDParm
							.setData("MR_NO", parm.getData("MR_NO", i));
					updInvStockDDParm.setData("CASE_NO", parm.getData(
							"CASE_NO", i));
					updInvStockDDParm.setData("RX_SEQ", "");
					updInvStockDDParm.setData("ADM_TYPE", parm.getData(
							"ADM_TYPE", i));
					updInvStockDDParm.setData("SEQ_NO", "");
					updInvStockDDParm.setData("WAST_ORG", parm.getData(
							"WAST_ORG", i));
					updInvStockDDParm.setData("INV_CODE", inv_code);
					updInvStockDDParm.setData("STOCK_QTY", qty);
					updInvStockDDParm.setData("OPT_USER", opt_user);
					updInvStockDDParm.setData("OPT_TERM", opt_term);
					updInvStockDDParm.setData("OPT_DATE", date);
				} else {
					Timestamp date = StringTool.getTimestamp(new Date());
					// System.out.println("20140408-------增库------->");
					updInvStockDDParm.setData("OUT_USER", "");
					updInvStockDDParm.setData("OUT_DATE", "");
					updInvStockDDParm.setData("MR_NO", "");
					updInvStockDDParm.setData("CASE_NO", "");
					updInvStockDDParm.setData("RX_SEQ", "");
					updInvStockDDParm.setData("ADM_TYPE", "");
					updInvStockDDParm.setData("SEQ_NO", "");
					updInvStockDDParm.setData("WAST_FLG", "N");
					updInvStockDDParm.setData("COST_FLG", "N");
					updInvStockDDParm.setData("WAST_ORG", "");
					updInvStockDDParm.setData("INV_CODE", inv_code);
					updInvStockDDParm.setData("STOCK_QTY", "1");
					updInvStockDDParm.setData("OPT_USER", opt_user);
					updInvStockDDParm.setData("OPT_TERM", opt_term);
					updInvStockDDParm.setData("OPT_DATE", date);
				}
				// System.out.println("updInvStockDDParm::"+updInvStockDDParm);
				// step6.根据INV_CODE 扣库 更新如下字段
				result = SPCINVRecordTool.getInstance().updInvStockDD(
						updInvStockDDParm, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				// step7 根据主键 更新 spc_inv_record的扣库标记位
				TParm updSpcInvRecordStockFlgParm = new TParm();
				updSpcInvRecordStockFlgParm.setData("BUSINESS_NO", parm
						.getData("BUSINESS_NO", i));
				updSpcInvRecordStockFlgParm.setData("SEQ", parm.getData("SEQ",
						i));
				result = SPCINVRecordTool.getInstance()
						.updSpcInvRecordStockFlg(updSpcInvRecordStockFlgParm,
								connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				// -------------------------低值-----------------------
			} else if (parm.getData("FLG", i).toString().equals("LOW")) {
				String inv_code = parm.getData("INV_CODE", i).toString();
				String orgCode = parm.getData("ORG_CODE", i).toString();
				// String orgCode = "0409";
				String opt_user = parm.getData("OPT_USER", i).toString();
				String opt_term = parm.getData("OPT_TERM", i).toString();
				Double qty = parm.getDouble("QTY", i);// 扣库数量
				// System.out.println("1:"+inv_code);
				// System.out.println("2:"+orgCode);
				// System.out.println("3:"+opt_user);
				// System.out.println("4:"+opt_term);
				// System.out.println("5:"+qty);
				// step1.根据ORG_CODE,INV_CODE查询INV_STOCKM ORG_CODE 高值为介入室
				String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY "
						+ " FROM INV_STOCKM " + " WHERE ORG_CODE = '" + orgCode
						+ "' AND INV_CODE = '" + inv_code + "' ";
				// System.out.println("selectInvStockM---->"+selectInvStockM);
				// step2.更新 INV_STOCKM表的字段 stock_qty(-m)
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockM));
				// System.out.println("查询INV_STOCKM表数据"+result);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				String sql1 = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"
						+ inv_code + "'";
				TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
				// System.out.println("parm1----------->"+parm1);
				String inv_chn_desc = parm1.getData("INV_CHN_DESC", 0)
						.toString();
				String sql2 = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"
						+ orgCode + "'";
				TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
				String dept_chn_desc = parm2.getData("DEPT_CHN_DESC", 0)
						.toString();
				if (result.getCount("ORG_CODE") < 0) {
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc
							+ "查无此物资");
					connection.rollback();
					connection.close();
					return result;
					// 查无此物资
				}

				if (Double.parseDouble(result.getData("STOCK_QTY", 0)
						.toString()) < qty) {
					// 库存不足
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc
							+ "库存不足");
					connection.rollback();
					connection.close();
					return result;

				}

				TParm updInvStockMParm = new TParm();
				updInvStockMParm.setData("ORG_CODE", orgCode);
				updInvStockMParm.setData("INV_CODE", inv_code);
				updInvStockMParm.setData("STOCK_QTY", qty);
				updInvStockMParm.setData("OPT_USER", opt_user);
				updInvStockMParm.setData("OPT_TERM", opt_term);
				result = SPCINVRecordTool.getInstance().updInvStockM(
						updInvStockMParm, connection);

				if (result.getErrCode() < 0) {
					result.setErrText("保存失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				// step3.根据 ORG_CODE,INV_CODE查询INV_STOCKD 根据VALID_DATE升序排序
				String selectInvStockD = "SELECT INV_CODE,ORG_CODE,BATCH_SEQ,STOCK_QTY FROM INV_STOCKD WHERE INV_CODE = '"
						+ inv_code
						+ "' AND ORG_CODE = '"
						+ orgCode
						+ "' ORDER BY VALID_DATE";
				// System.out.println("selectInvStockD---->"+selectInvStockD);
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockD));
				// System.out.println("INV_STOCKD表的数据："+result);
				if (result.getErrCode() < 0) {
					result.setErrText("保存失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				if (result.getCount("ORG_CODE") < 0) {
					// 查无数据
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc
							+ "查无此物资");
					connection.rollback();
					connection.close();
					return result;
				}

				result = saveInvStockD(result, qty, connection, opt_user,
						opt_term);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				// step 4 更新扣库标记位
				TParm updSpcInvRecordStockFlgParm = new TParm();
				updSpcInvRecordStockFlgParm.setData("BUSINESS_NO", parm
						.getData("BUSINESS_NO", i));
				updSpcInvRecordStockFlgParm.setData("SEQ", parm.getData("SEQ",
						i));
				result = SPCINVRecordTool.getInstance()
						.updSpcInvRecordStockFlg(updSpcInvRecordStockFlgParm,
								connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			}

		}

		return result;

	}

	/**
	 * 循环扣inv_stockd d 为要扣的数量
	 * */
	public TParm saveInvStockD(TParm invSockDParm, double d,
			TConnection connection, String opt_user, String opt_term) {

		double qty = invSockDParm.getDouble("STOCK_QTY", 0);
		if (qty >= d) {

			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0),
					invSockDParm.getValue("INV_CODE", 0), invSockDParm
							.getValue("BATCH_SEQ", 0), qty - d, connection,
					opt_user, opt_term, d);

		} else {

			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0),
					invSockDParm.getValue("INV_CODE", 0), invSockDParm
							.getValue("BATCH_SEQ", 0), qty, connection,
					opt_user, opt_term, qty);
			d = d - qty;

			if (invSockDParm.getCount("ORG_CODE") > 0) {
				invSockDParm.removeRow(0);
				this.saveInvStockD(invSockDParm, d, connection, opt_user,
						opt_term);
			} else {
				return invSockDParm;
			}
		}
		return invSockDParm;
	}

	/**
	 * 扣库方法 org inv batch_seq qty
	 * */
	public TParm updateInvStockD(String org, String inv, String batch_seq,
			double qty, TConnection connection, String opt_user,
			String opt_term, Double d) {
		TParm result = new TParm();
    
		TParm updInvStockDParm = new TParm();
		updInvStockDParm.setData("STOCK_QTY", d);
		updInvStockDParm.setData("STOCK_QTYS", qty);
		updInvStockDParm.setData("ORG_CODE", org);
		updInvStockDParm.setData("INV_CODE", inv);
		updInvStockDParm.setData("BATCH_SEQ", batch_seq);
		updInvStockDParm.setData("OPT_USER", opt_user);
		updInvStockDParm.setData("OPT_TERM", opt_term);
		result = SPCINVRecordTool.getInstance().updInvStockD(updInvStockDParm,
				connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 接受确认
	 * */
	public TParm onSaveInvPack(TParm parm) {
		TParm result = new TParm();  
		TConnection connection = this.getConnection();
		String sel = parm.getValue("RES").toString();
		// <SAVE_START><COUNT>1</COUNT><RESULT0><BARCODE0>101010001001</BARCODE0><RECECIEUSER0>D001</RECECIEUSER0></RESULT0><SAVE_END>
		int countstart = sel.indexOf("<COUNT>");
		int countend = sel.indexOf("</COUNT>");
		// 12 20
		String cou = sel.substring(countstart + 7, countend);
		int count = Integer.parseInt(cou);
		Timestamp now = SystemTool.getInstance().getDate();
		String time = StringTool.getString(now, "yyyyMMddHHmmss");
		for (int i = 0; i < count; i++) {
			int barCode_start = sel.indexOf("<BARCODE" + i + ">");
			int barCode_end = sel.indexOf("</BARCODE" + i + ">");
			String lengthBar = "<BARCODE" + i + ">";
			int lengthnumBar = lengthBar.length();
			String barCode = sel.substring(barCode_start + lengthnumBar,
					barCode_end);
			int recevieUser_start = sel.indexOf("<RECEIVE_USER" + i + ">");
			int recevieUser_end = sel.indexOf("</RECEIVE_USER" + i + ">");
			String lengthRec = "<RECEIVE_USER" + i + ">";
			int lengthnumRec = lengthRec.length();
			String recevieUser = sel.substring(
					recevieUser_start + lengthnumRec, recevieUser_end);
			String sqlD = "UPDATE INV_SUP_DISPENSED SET RECEIVE_DATE=TO_DATE('"
					+ time + "','YYYYMMDDHH24MISS')," + "RECEIVE_USER='"
					+ recevieUser + "' WHERE BARCODE='" + barCode + "'";
			result = new TParm(TJDODBTool.getInstance().update(sqlD));
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			String sqlM = "UPDATE INV_PACKSTOCKM SET STATUS = '6'  WHERE BARCODE='"
					+ barCode + "'";
			result = new TParm(TJDODBTool.getInstance().update(sqlM));
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 接受确认
	 * */
	public TParm onSaveInvPackCheck(TParm parm) {
		TParm result = new TParm();
		TConnection connection = this.getConnection();
		String sel = parm.getValue("RES").toString();
		// <SAVE_START><COUNT>1</COUNT><RESULT0><BARCODE0>101010001001</BARCODE0><RECECIEUSER0>D001</RECECIEUSER0></RESULT0><SAVE_END>
		int countstart = sel.indexOf("<COUNT>");
		int countend = sel.indexOf("</COUNT>");
		// 12 20
		String cou = sel.substring(countstart + 7, countend);
		int count = Integer.parseInt(cou);

		  
		int mrnostart = sel.indexOf("<MRNO>");
		int mrnoend = sel.indexOf("</MRNO>");
		// 12 20
		String mrNo = sel.substring(mrnostart + 6, mrnoend);
		
		int caseNostart = sel.indexOf("<CASENO>");
		int caseNoend = sel.indexOf("</CASENO>");
		// 12 20    
		String caseNo = sel.substring(caseNostart + 8, caseNoend);
		
		Timestamp now = SystemTool.getInstance().getDate();
		String time = StringTool.getString(now, "yyyyMMddHHmmss");
		
		for (int i = 0; i < count; i++) {
			int barCode_start = sel.indexOf("<BARCODE" + i + ">");
			int barCode_end = sel.indexOf("</BARCODE" + i + ">");
			String lengthBar = "<BARCODE" + i + ">";
			int lengthnumBar = lengthBar.length();
			String barCode = sel.substring(barCode_start + lengthnumBar,
					barCode_end);
			int checkUser_start = sel.indexOf("<CHECK_USER" + i + ">");
			int checkUser_end = sel.indexOf("</CHECK_USER" + i + ">");
			String lengthRec = "<CHECK_USER" + i + ">";
			int lengthnumRec = lengthRec.length();
			String checkUser = sel.substring(checkUser_start + lengthnumRec,
					checkUser_end);
			String sqlD = "UPDATE INV_SUP_DISPENSED SET CHECK_DATE=TO_DATE('"
					+ time + "','YYYYMMDDHH24MISS')," + "CHECK_USER='"
					+ checkUser + "',CASE_NO = '" + caseNo + "',MR_NO = '"
					+ mrNo + "' WHERE BARCODE='" + barCode + "'";             
			result = new TParm(TJDODBTool.getInstance().update(sqlD));
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 插入 INV_SUP_OPE_CHECKD
	 * */
	public TParm onInsertInvOpeCheck(TParm parm) {
		TParm result = new TParm();
		TConnection connection = this.getConnection();

		Timestamp now = SystemTool.getInstance().getDate();
		String time = StringTool.getString(now, "yyyyMMddHHmmss");

		// 根据inv_code取得唯一值

		String sel = parm.getValue("RES").toString();
		// <SAVE_START><COUNT>1</COUNT><RESULT0><BARCODE0>101010001001</BARCODE0><RECECIEUSER0>D001</RECECIEUSER0></RESULT0><SAVE_END>
		int countstart = sel.indexOf("<COUNT>");
		int countend = sel.indexOf("</COUNT>");
		// 12 20
		String cou = sel.substring(countstart + 7, countend);
		int count = Integer.parseInt(cou);

		int pagestart = sel.indexOf("<PAGE>");
		int pageend = sel.indexOf("</PAGE>");  
		// 12 20
		String couPage = sel.substring(pagestart + 6, pageend);
		int page = Integer.parseInt(couPage);

		int barCode_start = sel.indexOf("<BARCODE>");
		int barCode_end = sel.indexOf("</BARCODE>");
		String lengthBar = "<BARCODE>";  
		int lengthnumBar = lengthBar.length();
		String barCode = sel.substring(barCode_start + lengthnumBar,
				barCode_end);

		for (int i = 0; i < count; i++) {
			int seq_start = sel.indexOf("<SEQ_NO" + i + ">");
			int seq_end = sel.indexOf("</SEQ_NO" + i + ">");
			String seq = "<SEQ_NO" + i + ">";
			int lengthnumseq = seq.length();
			String seqNo = sel.substring(seq_start + lengthnumseq, seq_end);
			int Bf_qty_start = sel.indexOf("<BF_OPE_QTY" + i + ">");
			int Bf_qty_end = sel.indexOf("</BF_OPE_QTY" + i + ">");
			String bfQty = "<BF_OPE_QTY" + i + ">";
			int lengthnumBf = bfQty.length();
			String BF_qty = sel.substring(Bf_qty_start + lengthnumBf,
					Bf_qty_end);
			// int bfqtySql = Integer.parseInt(BF_qty);
			// int seqSql = Integer.parseInt(seq);
			String sqlD = " INSERT INTO INV_SUP_OPE_CHECKD (PACK_CODE,PACKSEQ_NO,INV_CODE,INVSEQ_NO,QTY, "
					+ " BF_OPE_QTY,BF_CLOSE_QTY,AF_CLOSE_QTY,CHECK_USER1,CHECK_USER2, "
					+ " STOCK_UNIT,BATCH_NO,VALID_DATE,BARCODE,SEQ_NO,"
					+ " OPT_USER,OPT_TERM,OPT_DATE) VALUES "
					+ " ('','','','','0',"
					+ " '"  
					+ BF_qty
					+ "','0','0','','',"
					+ " '','',TO_DATE('"
					+ time
					+ "','YYYYMMDDHH24MISS'),'"
					+ barCode
					+ "','"
					+ seqNo
					+ "', "
					+ " 'D001','127.0.0.1',TO_DATE('"
					+ time
					+ "','YYYYMMDDHH24MISS'))";
			//System.out.println("sqlD:" + sqlD);
			result = new TParm(TJDODBTool.getInstance().update(sqlD));
		}
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result.getErrParm();
		}

		String sql = " SELECT D.INV_CHN_DESC,C.STOCK_UNIT,E.UNIT_CHN_DESC "
				+ " ,B.PACK_CODE AS PACK_CODE,0 AS PACKSEQ_NO,C.INV_CODE AS INV_CODE,0 AS INVSEQ_NO"
				+ " ,C.QTY AS QTY ,0 AS AF_CLOSE_QTY ,0 AS BF_CLOSE_QTY,A.BATCH_NO,A.VALID_DATE "
				+ " FROM INV_SUP_DISPENSED A,INV_PACKM B,INV_PACKD C,INV_BASE D,SYS_UNIT E"
				+ " WHERE A.INV_CODE = B.PACK_CODE"
				+ " AND B.PACK_CODE = C.PACK_CODE"
				+ " AND C.INV_CODE =D.INV_CODE"
				+ " AND C.STOCK_UNIT =E.UNIT_CODE " + " AND A.BARCODE = '"
				+ barCode + "'" + "  ORDER BY D.INV_CHN_DESC,A.INV_CODE ";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("selParm::::" + selParm);
		if (selParm.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return selParm.getErrParm();
		}
		for (int i = 0 + (page - 1) * 10; i < count + (page - 1) * 10; i++) {
			String sqlDD = " UPDATE INV_SUP_OPE_CHECKD SET PACK_CODE = '"
					+ selParm.getValue("PACK_CODE", i) + "'," + " INV_CODE = '"
					+ selParm.getValue("INV_CODE", i) + "'," + " QTY = '"
					+ selParm.getValue("QTY", i) + "'," + " STOCK_UNIT = '"
					+ selParm.getValue("UNIT_CHN_DESC", i) + "',"
					+ " BATCH_NO = '" + selParm.getValue("BATCH_NO", i) + "',"
					+ " VALID_DATE = TO_DATE('"
					+ selParm.getValue("VALID_DATE", i)
					+ "','YYYYMMDDHH24MISS')" + " WHERE BARCODE = '" + barCode
					+ "' AND SEQ_NO = '" + String.valueOf(i + 1) + "' ";
			//System.out.println("sqlDUpdate:" + sqlDD);
			result = new TParm(TJDODBTool.getInstance().update(sqlDD));
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result.getErrParm();
			}
		}

		// 最后一页加入checkuser事件 后续调整
		// for (int i = 0 + (page - 1) * 10; i < count + (page - 1) * 10; i++) {
		// String sqlDD = " UPDATE INV_SUP_OPE_CHECKD SET PACK_CODE = '"+
		// selParm.getValue("PACK_CODE", i) + "',"
		// + " CHECK_USER1 = '"+ selParm.getValue("INV_CODE", i) + "',"
		// + " CHECK_USER2 = '"+ selParm.getValue("QTY", i) + "' "
		// + " INV_CODE = '"+ selParm.getValue("INV_CODE", i) + "'"
		// + " WHERE BARCODE = '" + barCode + "' AND SEQ_NO = '"
		// + String.valueOf(i + 1) + "' ";
		// System.out.println("sqlDUpdate:" + sqlDD);
		// result = new TParm(TJDODBTool.getInstance().update(sqlDD));
		// if (result.getErrCode() < 0) {
		// connection.rollback();
		// connection.close();
		// return result.getErrParm();
		// }
		// }

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 更新 INV_SUP_OPE_CHECKD
	 * */
	public TParm onUpdateInvOpeCheck(TParm parm) {
		TParm result = new TParm();  
		TConnection connection = this.getConnection();

		Timestamp now = SystemTool.getInstance().getDate();
		String time = StringTool.getString(now, "yyyyMMddHHmmss");

		// 根据inv_code取得唯一值

		String sel = parm.getValue("RES").toString();
		// <SAVE_START><COUNT>1</COUNT><RESULT0><BARCODE0>101010001001</BARCODE0><RECECIEUSER0>D001</RECECIEUSER0></RESULT0><SAVE_END>
		int countstart = sel.indexOf("<COUNT>");
		int countend = sel.indexOf("</COUNT>");
		// 12 20
		String cou = sel.substring(countstart + 7, countend);
		int count = Integer.parseInt(cou);

		int pagestart = sel.indexOf("<PAGE>");
		int pageend = sel.indexOf("</PAGE>");
		// 12 20
		String couPage = sel.substring(pagestart + 6, pageend);
		int page = Integer.parseInt(couPage);
		System.out.println("page:" + page);

		int barCode_start = sel.indexOf("<BARCODE>");
		int barCode_end = sel.indexOf("</BARCODE>");
		String lengthBar = "<BARCODE>";
		int lengthnumBar = lengthBar.length();
		String barCode = sel.substring(barCode_start + lengthnumBar,
				barCode_end);
		String statues = parm.getValue("STA");
		// 10-16 0-6
		for (int i = 0 + (page - 1) * 10; i < count + (page - 1) * 10; i++) {
			int seq_start = sel
					.indexOf("<SEQ_NO" + (i - (page - 1) * 10) + ">");
			int seq_end = sel.indexOf("</SEQ_NO" + (i - (page - 1) * 10) + ">");
			String seq = "<SEQ_NO" + (i - (page - 1) * 10) + ">";
			int lengthnumseq = seq.length();
			String seqNo = sel.substring(seq_start + lengthnumseq, seq_end);
			// System.out.println("seqNo:" + seqNo);
			// int bfqtySql = Integer.parseInt(BF_qty);
			// int seqSql = Integer.parseInt(seq);
			if (statues.equals("关前")) {           
				int Bf_qty_start = sel.indexOf("<BF_CLOSE_QTY"
						+ (i - (page - 1) * 10) + ">");
				int Bf_qty_end = sel.indexOf("</BF_CLOSE_QTY"
						+ (i - (page - 1) * 10) + ">");
				String bfQty = "<BF_CLOSE_QTY" + (i - (page - 1) * 10) + ">";
				int lengthnumBf = bfQty.length();
				String BF_qty = sel.substring(Bf_qty_start + lengthnumBf,
						Bf_qty_end);
				// CHECK_USER1='' ,CHECK_USER2 = '',
				String sqlD = "UPDATE INV_SUP_OPE_CHECKD SET OPT_DATE=TO_DATE('"
						+ time
						+ "','YYYYMMDDHH24MISS'),"
						+ " BF_CLOSE_QTY = '"
						+ BF_qty
						+ "' "
						+ " WHERE BARCODE='"
						+ barCode
						+ "' AND  SEQ_NO = '" + seqNo + "' ";
				// System.out.println("sqlD:" + sqlD);
				result = new TParm(TJDODBTool.getInstance().update(sqlD));
			} else if (statues.equals("术后")) {
				int Bf_qty_start = sel.indexOf("<AF_CLOSE_QTY"
						+ (i - (page - 1) * 10) + ">");
				int Bf_qty_end = sel.indexOf("</AF_CLOSE_QTY"
						+ (i - (page - 1) * 10) + ">");
				String bfQty = "<AF_CLOSE_QTY" + (i - (page - 1) * 10) + ">";
				int lengthnumBf = bfQty.length();
				String BF_qty = sel.substring(Bf_qty_start + lengthnumBf,
						Bf_qty_end);

				String sqlD = "UPDATE INV_SUP_OPE_CHECKD SET OPT_DATE=TO_DATE('"
						+ time
						+ "','YYYYMMDDHH24MISS'),"
						// CHECK_USER1='' ,CHECK_USER2 = '',
						+ " AF_CLOSE_QTY = '"
						+ BF_qty
						+ "' "
						+ " WHERE BARCODE='"
						+ barCode
						+ "' AND  SEQ_NO = '"
						+ seqNo + "' ";
				//System.out.println("术后sqlD:" + sqlD);
				result = new TParm(TJDODBTool.getInstance().update(sqlD));

			}
		}
		if (result.getErrCode() < 0) {
			connection.rollback();  
			connection.close();
			return result.getErrParm();
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * 更新 INV_SUP_OPE_CHECKD(check_user1 ,check_user2)
	 * */
	public TParm onUpdateCheckUser(TParm parm) {
		TParm result = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		String time = StringTool.getString(now, "yyyyMMddHHmmss");
		TConnection connection = this.getConnection();
		String bar = parm.getValue("BAR").toString();
		String user1 = parm.getValue("USER1").toString();
		String user2 = parm.getValue("USER2").toString();
		String sql = " SELECT D.INV_CHN_DESC AS INV_DESC,E.UNIT_CHN_DESC AS UNIT_DESC,"
				+ " C.QTY,A.BF_OPE_QTY,A.BF_CLOSE_QTY,A.AF_CLOSE_QTY"
				+ " FROM INV_SUP_OPE_CHECKD A,INV_PACKM B,INV_PACKD C,INV_BASE D,SYS_UNIT E"
				+ " WHERE A.PACK_CODE = B.PACK_CODE"
				+ " AND A.PACK_CODE = C.PACK_CODE"
				+ " AND A.INV_CODE =D.INV_CODE"
				+ " AND A.INV_CODE = C.INV_CODE "
				+ " AND C.STOCK_UNIT =E.UNIT_CODE "
				+ " AND A.BARCODE = '"  
				+ bar + "' ORDER BY A.SEQ_NO ";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return selParm.getErrParm();
		}
		for (int i = 0; i < selParm.getCount(); i++) {

			String sqlD = "UPDATE INV_SUP_OPE_CHECKD SET OPT_DATE=TO_DATE('"
					+ time + "','YYYYMMDDHH24MISS')," + " CHECK_USER1 = '"
					+ user1 + "' " + " ,CHECK_USER2 = '" + user2 + "' "
					+ " WHERE BARCODE='" + bar + "' ";  
			//System.out.println("sqlDCheck:" + sqlD);
			result = new TParm(TJDODBTool.getInstance().update(sqlD));
		}

		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result.getErrParm();
		}
		connection.commit();
		connection.close();
		return result;

	}
	
	

	/**
	 * 更新 INV_SUP_OPE_CHECKD(check_user1 ,check_user2)  
	 * */
	public TParm onUpdateRoom(TParm parm) {
		TParm result = new TParm();
		TParm resultUpdate = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();            
		String time = StringTool.getString(now, "yyyyMMddHHmmss");
		TConnection connection = this.getConnection();
		String room = parm.getValue("ROOM").toString(); 
		
		String sqlSelect = " SELECT OPBOOK_SEQ FROM OPE_IPROOM WHERE ROOM_NO='" + room + "'  ";
		result = new TParm(TJDODBTool.getInstance().select(sqlSelect));
		if (result.getErrCode() < 0) {
			connection.rollback();  
			connection.close();    
			return result.getErrParm();  
		}
		String sqlopbook = "UPDATE OPE_OPBOOK SET REMOVE_DATE = TO_DATE('"+time+"','YYYYMMDDHH24MISS')" +    
		" WHERE OPBOOK_SEQ='" + result.getValue("OPBOOK_SEQ", 0) + "' ";    
        //System.out.println("sqlRoom:" + sqlD);  
        result = new TParm(TJDODBTool.getInstance().update(sqlopbook));  
        
		String sqliproom = "UPDATE OPE_IPROOM SET OPBOOK_SEQ = null" +    
		" WHERE ROOM_NO='" + room + "' ";  
		resultUpdate = new TParm(TJDODBTool.getInstance().update(sqliproom));
        if (result.getErrCode() < 0||resultUpdate.getErrCode() < 0) {
        connection.rollback();
        connection.close();      
        if(result.getErrCode() < 0){
        return result.getErrParm();  
        }else if(resultUpdate.getErrCode() < 0){  
        return resultUpdate.getErrParm();
           }
        }
		connection.commit();
		connection.close();
		return result;

	}

}
