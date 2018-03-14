package jdo.bil;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import action.bil.SPCINVRecordAction;

import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.OdiUtil;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: PDA webservice接口
 * </p>
 * 
 * <p>
 * Description: PDA webservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author caowl 2012-6-27
 * @version 4.0
 */
@WebService
public class BILPDAWsToolImpl implements BILPDAWsTool {

	private int packGroupNo = 0;

	public String onQueryPda(String mrNo, String caseNo, String optUser,
			String deptCodes, String stationCode, String regionCode,
			String optTerm, String flg) {

		// String result = "";
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		Timestamp today = SystemTool.getInstance().getDate();
		String date_today = today.toString().substring(0, 10).replace("-", "");
		String date_yesterday = yesterday.toString().substring(0, 10).replace(
				"-", "");
		String sql = "SELECT BUSINESS_NO,SEQ,CASE_NO,MR_NO,CLASS_CODE,"
				+ "BAR_CODE,INV_CODE,INV_DESC,OPMED_CODE,ORDER_CODE,"
				+ "ORDER_DESC,OWN_PRICE,QTY,UNIT_CODE,AR_AMT,BATCH_SEQ,"
				+ "VALID_DATE,BILL_FLG,BILL_DATE,NS_CODE,OP_ROOM,"
				+ "DEPT_CODE,EXE_DEPT_CODE,CASE_NO_SEQ,SEQ_NO,OPT_USER,"
				+ "OPT_DATE,OPT_TERM,YEAR_MONTH,RECLAIM_USER,RECLAIM_DATE,REQUEST_NO "
				+ " FROM SPC_INV_RECORD " + " WHERE MR_NO = '" + mrNo + "' "
				+ " AND CASE_NO = '" + caseNo + "'"
				+ " AND BILL_DATE BETWEEN TO_DATE('" + date_yesterday
				+ "000000', 'YYYYMMDDHH24MISS') " + " AND TO_DATE('"
				+ date_today + "235959', 'YYYYMMDDHH24MISS')";
		if (flg.equals("ONLY")) {
			sql += " AND OPT_USER = '" + optUser + "'";
		}
		// System.out.println("PDA查询"+sql);
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = selParm.getCount();

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PDA__RESULT>");

		strBuf.append("<STATUS>");
		if (selParm.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(selParm.getCount());
		strBuf.append("</COUNT>");
		// System.out.println("循环开始");
		for (int i = 0; i < count; i++) {

			String classCode = selParm.getData("CLASS_CODE", i).toString();
			String barCode = selParm.getData("BAR_CODE", i).toString();
			String invCode = selParm.getData("INV_CODE", i).toString();
			String invDesc = selParm.getData("INV_DESC", i).toString();

			String orderCode = selParm.getData("ORDER_CODE", i).toString();
			String orderDesc = selParm.getData("ORDER_DESC", i).toString();
			String ownPrice = selParm.getData("OWN_PRICE", i).toString();
			String qty = selParm.getData("QTY", i).toString();
			// String unitCode = selParm.getData("UNIT_CODE",i).toString();
			String arAmt = selParm.getData("AR_AMT", i).toString();

			strBuf.append("<RESULT" + i + ">");

			strBuf.append("<CLASS_CODE" + i + ">");
			strBuf.append(classCode);
			strBuf.append("</CLASS_CODE" + i + ">");

			if (classCode.equals("3")) {
				strBuf.append("<DESC" + i + ">");
				strBuf.append(invDesc);
				strBuf.append("</DESC" + i + ">");

				String descrSql = "SELECT DESCRIPTION FROM INV_BASE WHERE INV_CODE = '"
						+ invCode + "'";
				TParm parm = new TParm(TJDODBTool.getInstance()
						.select(descrSql));

				strBuf.append("<DESCRIPTION" + i + ">");
				strBuf.append(parm.getData("DESCRIPTION", 0));
				strBuf.append("</DESCRIPTION" + i + ">");
			} else {
				strBuf.append("<DESC" + i + ">");
				strBuf.append(orderDesc);
				strBuf.append("</DESC" + i + ">");

				String descrSql = "SELECT SPECIFICATION FROM SYS_FEE WHERE ORDER_CODE = '"
						+ orderCode + "'";
				TParm parm = new TParm(TJDODBTool.getInstance()
						.select(descrSql));
				strBuf.append("<DESCRIPTION" + i + ">");
				strBuf.append(parm.getData("DESCRIPTION", 0));
				strBuf.append("</DESCRIPTION" + i + ">");
			}

			strBuf.append("<BAR_CODE" + i + ">");
			strBuf.append(barCode);
			strBuf.append("</BAR_CODE" + i + ">");

			strBuf.append("<INV_CODE" + i + ">");
			strBuf.append(invCode);
			strBuf.append("</INV_CODE" + i + ">");

			strBuf.append("<INV_DESC" + i + ">");
			strBuf.append(invDesc);
			strBuf.append("</INV_DESC" + i + ">");

			strBuf.append("<ORDER_CODE" + i + ">");
			strBuf.append(orderCode);
			strBuf.append("</ORDER_CODE" + i + ">");

			strBuf.append("<ORDER_DESC" + i + ">");
			strBuf.append(orderDesc);
			strBuf.append("</ORDER_DESC" + i + ">");

			strBuf.append("<OWN_PRICE" + i + ">");
			strBuf.append(ownPrice);
			strBuf.append("</OWN_PRICE" + i + ">");

			strBuf.append("<QTY" + i + ">");
			strBuf.append(qty);
			strBuf.append("</QTY" + i + ">");

			// strBuf.append("<UNIT_CODE>");
			// strBuf.append(unitCode);
			// strBuf.append("</UNIT_CODE>");

			strBuf.append("<AR_AMT" + i + ">");
			strBuf.append(arAmt);
			strBuf.append("</AR_AMT" + i + ">");
			strBuf.append("</RESULT" + i + ">");

		}
		// System.out.println("循环结束");
		strBuf.append("</OPE_PDA__RESULT>");
		// System.out.println("耗用记录查询结果----》"+strBuf.toString());
		return strBuf.toString();

	}

	public String onSavePda(String BarCode, String mr_no, String case_no,
			String qty, String userId, String deptCode, String stationCode,
			String regionCode, String optTerm, String patName,
			String deptCodeOfPat, String op_room) {
		// System.out.println("------------onSavePda 开始-----------------");
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("<OPE_PDA_SAVE_RESULT>");
		mr_no = PatTool.getInstance().checkMrno(TypeTool.getString(mr_no));
		String str = mr_no;
		boolean isNum = str.matches("[0-9]+");
		// 输入的病案号不合法
		if (!isNum) {

			strBuf.append("<ERR>");
			strBuf.append("-1");
			strBuf.append("</ERR>");
			return strBuf.toString();
		}
		// ========================================================
		// ====================start=====================
		String pack_code = "";// 手术包的唯一条码
		String sql = "";
		if (BarCode.length() == 12) {
			pack_code = BarCode;
		}
		if (pack_code.length() == 12
				&& pack_code.substring(6, 12).equals("000000")) {
			// 诊疗包
			// this.messageBox("诊疗包");
			sql = "SELECT B.BARCODE AS PACK_CODE,A.PACK_DESC,A.PY2,A.DESCRIPTION,A.USE_COST,A.VALUE_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PACK_CODE AS PACK_BATCH_NO"
					+ " FROM INV_PACKM A,INV_PACKSTOCKM B"
					+ " WHERE A.PACK_CODE = B.PACK_CODE"
					+ "       AND B.BARCODE = '"
					+ pack_code
					+ "'"
					+ " ORDER BY B.PACK_BATCH_NO DESC";
			// System.out.println("诊疗包主包---"+sql);

		} else if (pack_code.length() == 12
				&& !pack_code.substring(6, 12).equals("000000")) {
			// 手术包
			sql = " SELECT B.BARCODE AS PACK_CODE,A.PACK_DESC,A.PY1,A.DESCRIPTION,A.USE_COST,A.VALUE_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'' AS PACK_BATCH_NO,0 AS CLASS_CODE"
					+ " FROM INV_PACKM A, INV_PACKSTOCKM_HISTORY B"
					+ " WHERE A.PACK_CODE = B.PACK_CODE "
					+ "       AND B.BARCODE = '" + pack_code + "'";
		}
		TParm selParm = new TParm();

		if (sql.length() != 0 && !sql.equals("")) {
			selParm = new TParm(TJDODBTool.getInstance().select(sql));
		}
		if (selParm.getCount() > 0) {
			String pack_desc = selParm.getData("PACK_DESC", 0).toString();

			String packBacthNo = selParm.getData("PACK_BATCH_NO", 0).toString();
			String sqlD = getSql(pack_code, packBacthNo);// 查询细表
			TParm selParmD = new TParm(TJDODBTool.getInstance().select(sqlD));
			// HashMap map = new HashMap();
			// 查询该病人是否扫描过这个包
			String sqls = "SELECT MAX(PACK_GROUP_NO) AS PACK_GROUP_NO"
					+ " FROM SPC_INV_RECORD" + " WHERE CASE_NO = '" + case_no
					+ "' " + " AND MR_NO = '" + mr_no + "'"
					+ " AND PACK_BARCODE = '" + pack_code + "'";
			// System.out.println("库里是否扫描过这个包"+sqls);
			TParm parmPack = new TParm(TJDODBTool.getInstance().select(sqls));
			// System.out.println(parmPack.getCount("PACK_GROUP_NO"));
			if (parmPack.getInt("PACK_GROUP_NO", 0) < 0) {
				packGroupNo = 0;
			} else {
				packGroupNo = parmPack.getInt("PACK_GROUP_NO", 0) + 1;
			}

			for (int i = 0; i < selParmD.getCount(); i++) {
				TParm parms = new TParm();
				BarCode = selParmD.getData("INV_CODE", i).toString();
				parms.setData("PACK_CODE", pack_code);
				parms.setData("QTY", selParmD.getData("QTY", i));
				parms.setData("USED_FLG", "N");
				parms.setData("CHECK_FLG", "N");
				parms.setData("PACK_GROUP_NO", packGroupNo);
				parms.setData("PACK_DESC", pack_desc);
				// String patName,String deptCodeOfPat,String packBarCode,String
				// packDesc,String packGroupNo
				TParm parm = this.onBarCode(BarCode, mr_no, case_no, userId,
						deptCode, stationCode, regionCode, optTerm, patName,
						deptCodeOfPat, pack_code, pack_desc, packGroupNo + "",
						op_room);

				TParm Main = new TParm();
				TParm invParm = new TParm();
				Main.setData("SPCINVRecord", parm.getData());// 耗费记录参数

				// 扣库参数
				if (parm.getData("INV_CODE", 0) != null
						&& (!parm.getData("INV_CODE", 0).toString().equals(""))
						&& parm.getData("INV_CODE", 0).toString().substring(0,
								2).equals("08")) {
					// System.out.println("--------高值。。。-------------");
					// INV_STOCKM
					invParm.addData("INV_CODE", parm.getData("INV_CODE", 0));
					invParm.addData("QTY", Integer.parseInt(qty));
					invParm.addData("ORG_CODE", "0306");// ???????????
					invParm.addData("RFID", BarCode);
					invParm.addData("OPT_USER", userId);// ?????????
					invParm.addData("OPT_TERM", optTerm);// ????????
					invParm.addData("OUT_USER", userId);// ?????????????
					invParm.addData("MR_NO", mr_no);
					invParm.addData("CASE_NO", case_no);
					invParm.addData("RX_SEQ", "");
					invParm.addData("ADM_TYPE", "I");
					invParm.addData("SEQ_NO", "");
					invParm.addData("WAST_ORG", "0306");// ???????????
					invParm.addData("FLG", "HIGH");// 是否是高值标记
					// invParm.addData("OP_ROOM",op_room );
					// 低值
				} else if (parm.getData("INV_CODE", 0) != null
						&& !parm.getData("INV_CODE", 0).toString().equals("")
						&& !parm.getData("INV_CODE", 0).toString().substring(0,
								2).equals("08")) {

					// 扣库参数组合
					// INV_STOCKM
					invParm.addData("INV_CODE", parm.getData("INV_CODE", 0));
					invParm.addData("QTY", Integer.parseInt(qty));
					invParm.addData("ORG_CODE", "0306");
					invParm.addData("RFID", BarCode);
					invParm.addData("OPT_USER", userId);
					invParm.addData("OPT_TERM", optTerm);
					invParm.addData("OUT_USER", userId);
					invParm.addData("MR_NO", mr_no);
					invParm.addData("CASE_NO", case_no);
					invParm.addData("RX_SEQ", "");
					invParm.addData("ADM_TYPE", "I");
					invParm.addData("SEQ_NO", "");
					invParm.addData("ORG_CODE", "0306");
					invParm.addData("WAST_ORG", deptCode);
					invParm.addData("FLG", "LOW");// 是否是高值标记
					// invParm.addData("OP_ROOM",op_room );
					// System.out.println("invParm---低值 --->"+invParm);
				}

				Main.setData("INVParm", invParm.getData());// 扣库参数
				Main.setData("REGION_CODE", regionCode);// 根据region_code查询物联网开关

				// System.out.println("-------------参数配置完毕------------------");
				SPCINVRecordAction spcInvRecord = new SPCINVRecordAction();
				// 从界面获取要保存的数据 写入耗用记录表并进行扣库
				// System.out.println("-----------走到这里了--------------------");
				TParm results = spcInvRecord.onSave(Main);
				// System.out.println("------------save方法执行完毕-----------------------");
				// 保存失败
				if (results.getErrCode() == -1) {
					strBuf.append("<ERR>");
					strBuf.append("0");
					strBuf.append("<ERR>");
					// 保存成功
				} else {
					strBuf.append("<ERR>");
					strBuf.append("1");
					strBuf.append("</ERR>");
				}
			}
		} else {
			// //String patName,String deptCodeOfPat,String packBarCode,String
			// packDesc,String packGroupNo
			TParm parm = this.onBarCode(BarCode, mr_no, case_no, userId,
					deptCode, stationCode, regionCode, optTerm, patName,
					deptCodeOfPat, "", "", "", op_room);

			TParm Main = new TParm();
			TParm invParm = new TParm();
			Main.setData("SPCINVRecord", parm.getData());// 耗费记录参数
			// Main = parm;

			// 扣库参数
			if (parm.getData("INV_CODE", 0) != null
					&& (!parm.getData("INV_CODE", 0).toString().equals(""))
					&& parm.getData("INV_CODE", 0).toString().substring(0, 2)
							.equals("08")) {
				// System.out.println("--------高值。。。-------------");
				// INV_STOCKM
				invParm.addData("INV_CODE", parm.getData("INV_CODE", 0));
				invParm.addData("QTY", Integer.parseInt(qty));
				invParm.addData("ORG_CODE", "0306");// ???????????
				invParm.addData("RFID", BarCode);
				invParm.addData("OPT_USER", userId);// ?????????
				invParm.addData("OPT_TERM", optTerm);// ????????
				invParm.addData("OUT_USER", userId);// ?????????????
				invParm.addData("MR_NO", mr_no);
				invParm.addData("CASE_NO", case_no);
				invParm.addData("RX_SEQ", "");
				invParm.addData("ADM_TYPE", "I");
				invParm.addData("SEQ_NO", "");
				invParm.addData("WAST_ORG", "0306");// ???????????
				invParm.addData("OP_ROOM", op_room);
				invParm.addData("FLG", "HIGH");// 是否是高值标记

				// 低值
			} else if (parm.getData("INV_CODE", 0) != null
					&& !parm.getData("INV_CODE", 0).toString().equals("")
					&& !parm.getData("INV_CODE", 0).toString().substring(0, 2)
							.equals("08")) {

				// 扣库参数组合
				// INV_STOCKM
				invParm.addData("INV_CODE", parm.getData("INV_CODE", 0));
				invParm.addData("QTY", Integer.parseInt(qty));
				invParm.addData("ORG_CODE", "0306");
				invParm.addData("RFID", BarCode);
				invParm.addData("OPT_USER", userId);
				invParm.addData("OPT_TERM", optTerm);
				invParm.addData("OUT_USER", userId);
				invParm.addData("MR_NO", mr_no);
				invParm.addData("CASE_NO", case_no);
				invParm.addData("RX_SEQ", "");
				invParm.addData("ADM_TYPE", "I");
				invParm.addData("SEQ_NO", "");
				invParm.addData("ORG_CODE", "0306");
				invParm.addData("WAST_ORG", deptCode);
				invParm.addData("FLG", "LOW");// 是否是高值标记
				invParm.addData("OP_ROOM", op_room);
				// System.out.println("invParm---低值 --->"+invParm);
			}

			Main.setData("INVParm", invParm.getData());// 扣库参数
			Main.setData("REGION_CODE", regionCode);// 根据region_code查询物联网开关

			// System.out.println("-------------参数配置完毕------------------");
			SPCINVRecordAction spcInvRecord = new SPCINVRecordAction();
			// 从界面获取要保存的数据 写入耗用记录表并进行扣库
			// System.out.println("-----------走到这里了--------------------");
			// System.out.println("Main=========>"+Main);
			TParm results = spcInvRecord.onSave(Main);
			// System.out.println("------------save方法执行完毕-----------------------");
			// 保存失败
			if (results.getErrCode() == -1) {
				strBuf.append("<ERR>");
				strBuf.append("0");
				strBuf.append("<ERR>");
				// 保存成功
			} else {
				strBuf.append("<ERR>");
				strBuf.append("1");
				strBuf.append("</ERR>");
			}
		}

		// ========================================================
		// TParm parm = this.onBarCode(BarCode, mr_no,
		// case_no,userId,deptCode,stationCode,regionCode,optTerm);
		//		
		// TParm Main = new TParm();
		// TParm invParm = new TParm();
		// Main.setData("SPCINVRecord", parm.getData());//耗费记录参数
		// // Main = parm;
		//		 
		// //扣库参数
		// if(parm.getData("INV_CODE",0)!= null &&
		// (!parm.getData("INV_CODE",0).toString().equals("")) &&
		// parm.getData("INV_CODE",0).toString().substring(0,2).equals("08")){
		// System.out.println("--------高值。。。-------------");
		// //INV_STOCKM
		// invParm.addData("INV_CODE", parm.getData("INV_CODE",0));
		// invParm.addData("QTY", Integer.parseInt(qty));
		// invParm.addData("ORG_CODE","0306");//???????????
		// invParm.addData("RFID",BarCode );
		// invParm.addData("OPT_USER",userId );//?????????
		// invParm.addData("OPT_TERM",optTerm);//????????
		// invParm.addData("OUT_USER",userId);//?????????????
		// invParm.addData("MR_NO",mr_no);
		// invParm.addData("CASE_NO", case_no);
		// invParm.addData("RX_SEQ", "");
		// invParm.addData("ADM_TYPE", "I");
		// invParm.addData("SEQ_NO", "");
		// invParm.addData("WAST_ORG", "0306");//???????????
		// invParm.addData("FLG", "HIGH");//是否是高值标记
		//
		// //低值
		// }else if(parm.getData("INV_CODE",0)!= null &&
		// !parm.getData("INV_CODE",0).toString().equals("") &&
		// !parm.getData("INV_CODE",0).toString().substring(0,2).equals("08") ){
		//			 
		// //扣库参数组合
		// //INV_STOCKM
		// invParm.addData("INV_CODE", parm.getData("INV_CODE",0));
		// invParm.addData("QTY",Integer.parseInt(qty));
		// invParm.addData("ORG_CODE","0306");
		// invParm.addData("RFID", BarCode);
		// invParm.addData("OPT_USER",userId);
		// invParm.addData("OPT_TERM",optTerm);
		// invParm.addData("OUT_USER",userId);
		// invParm.addData("MR_NO", mr_no);
		// invParm.addData("CASE_NO", case_no);
		// invParm.addData("RX_SEQ", "");
		// invParm.addData("ADM_TYPE","I");
		// invParm.addData("SEQ_NO", "");
		// invParm.addData("ORG_CODE","0306");
		// invParm.addData("WAST_ORG", deptCode);
		// invParm.addData("FLG", "LOW");//是否是高值标记
		//			 
		// // System.out.println("invParm---低值 --->"+invParm);
		// }
		//			 	 
		// Main.setData("INVParm",invParm.getData());//扣库参数
		// Main.setData("REGION_CODE",regionCode);//根据region_code查询物联网开关
		//
		// System.out.println("-------------参数配置完毕------------------");
		// SPCINVRecordAction spcInvRecord = new SPCINVRecordAction();
		// //从界面获取要保存的数据 写入耗用记录表并进行扣库
		// System.out.println("-----------走到这里了--------------------");
		// TParm results = spcInvRecord.onSave(Main);
		// System.out.println("------------save方法执行完毕-----------------------");
		// strBuf.append("<OPE_PDA_SAVE_RESULT>");
		// //保存失败
		// if(results.getErrCode()==-1){
		// strBuf.append("<ERR>");
		// strBuf.append("0");
		// strBuf.append("<ERR>");
		// //保存成功
		// }else{
		// strBuf.append("<ERR>");
		// strBuf.append("1");
		// strBuf.append("</ERR>");
		// }
		strBuf.append("</OPE_PDA_SAVE_RESULT>");
		// System.out.println("保存的参数---》"+Main);
		// System.out.println("保存结果----》"+strBuf.toString());
		return strBuf.toString();
	}

	/**
	 * 组装参数
	 * */
	public TParm onBarCode(String BarCode, String mr_no, String case_no,
			String optUser, String deptCode, String stationCode,
			String regionCode, String optTerm, String patName,
			String deptCodeOfPat, String packBarCode, String packDesc,
			String packGroupNo, String op_room) {
		// System.out.println("-----------onBarCode  开始----------");
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		TParm selParm = new TParm();
		mr_no = PatTool.getInstance().checkMrno(TypeTool.getString(mr_no));
		// String case_no = this.getValueString("CASE_NO");
		if (!(BarCode == null || BarCode.equals(""))) {
			// 麻精
			String ToxicSql = "SELECT TOXIC_ID,ORDER_CODE,UNIT_CODE,BATCH_NO,VALID_DATE,"
					+ " VERIFYIN_PRICE,BATCH_SEQ,CONTAINER_ID,CABINET_ID,OPT_USER,OPT_DATE,OPT_TERM "
					+ " FROM IND_CONTAINERD "
					+ " WHERE TOXIC_ID='"
					+ BarCode
					+ "'";
			// System.out.println("ToxicSql:::"+ToxicSql);
			selParm = new TParm(TJDODBTool.getInstance().select(ToxicSql));
			if (selParm.getCount() > 0) {
				// System.out.println("-------麻精  START----------");

				selParm.addData("OPMED_CODE", "");
				selParm.addData("CLASS_CODE", "4");// 麻经为4
				selParm.addData("INV_CODE", "");
				selParm.addData("INV_DESC", "");
				selParm.addData("QTY", 1.0);
				String order_code = selParm.getData("ORDER_CODE", 0).toString();// 表里存的是物联网的order_code
				// String getHisOrderSql = "SELECT HIS_ORDER_CODE "+
				// " FROM SYS_FEE_SPC "+
				// " WHERE REGION_CODE='"+regionCode+"' "+
				// "  AND ORDER_CODE='"+order_code+"' ";
				// System.out.println("getHisOrderSql--->"+getHisOrderSql);
				// TParm hisParm = new
				// TParm(TJDODBTool.getInstance().select(getHisOrderSql));
				// if(hisParm.getCount()>0){
				// System.out.println("走这里了不？？");
				String sqls = "SELECT OWN_PRICE,UNIT_CODE,ORDER_DESC,EXEC_DEPT_CODE,SPECIFICATION FROM SYS_FEE WHERE ORDER_CODE = '"
						+ order_code + "'";
				// System.out.println("sqls--->"+sqls);
				// String
				// sqlcode="SELECT OWN_PRICE,UNIT_CODE,ORDER_DESC,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE = '"+hisParm.getData("HIS_ORDER_CODE",0)+"'";
				// TParm parms = new
				// TParm(TJDODBTool.getInstance().select(sqlcode));
				TParm parm = new TParm(TJDODBTool.getInstance().select(sqls));
				selParm.addData("DESC", parm.getValue("ORDER_DESC", 0));
				selParm.addData("ORDER_DESC", parm.getValue("ORDER_DESC", 0));
				selParm.addData("OWN_PRICE", parm.getValue("OWN_PRICE", 0));
				selParm.addData("AR_AMT", parm.getDouble("OWN_PRICE", 0) * 1.0);
				selParm.addData("BATCH_SEQ", selParm.getData("BATCH_SEQ", 0));
				selParm.addData("VALID_DATE", selParm.getTimestamp(
						"VALID_DATE", 0));
				selParm.addData("BILL_FLG", "Y");
				selParm.addData("BILL_DATE", now);
				selParm.addData("EXE_DEPT_CODE", deptCode);
				// }else{
				// //String
				// sqlcode="SELECT OWN_PRICE,UNIT_CODE,ORDER_DESC,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE = '"+order_code+"'";
				// //TParm parm = new
				// TParm(TJDODBTool.getInstance().select(sqlcode));
				// //selParm.addData("DESC", parm.getValue("ORDER_DESC", 0));
				// selParm.addData("ORDER_DESC", "");
				// //selParm.addData("OWN_PRICE",parm.getValue("OWN_PRICE",0));
				// //selParm.addData("AR_AMT", parm.getDouble("OWN_PRICE",
				// 0)*1.0);
				// selParm.addData("BATCH_SEQ",selParm.getData("BATCH_SEQ",0));
				// selParm.addData("VALID_DATE",
				// selParm.getTimestamp("VALID_DATE",0));
				// selParm.addData("BILL_FLG", "N");
				// selParm.addData("BILL_DATE", "");
				// selParm.addData("EXE_DEPT_CODE", deptCode);
				// }
				selParm
						.addData("DESCRIPTION", parm
								.getData("SPECIFICATION", 0));

				// System.out.println("----------------麻精 END------------");
			} else {
				// 药品
				// System.out.println("--------药品 START-----");
				String PhaSql = "SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"
						+ BarCode + "' AND CAT1_TYPE = 'PHA'";
				TParm PhaParm = new TParm(TJDODBTool.getInstance().select(
						PhaSql));
				if (PhaParm.getCount() > 0) {
					PhaSql = "SELECT  A.ORDER_CODE, A.ORDER_DESC, A.OWN_PRICE, A.UNIT_CODE"
							+ " FROM SYS_FEE A WHERE CAT1_TYPE = 'PHA' AND A.ORDER_CODE = '"
							+ BarCode + "'";
					selParm = new TParm(TJDODBTool.getInstance().select(PhaSql));
					selParm.addData("DESC", selParm.getValue("ORDER_DESC", 0));
					selParm.addData("OPMED_CODE", "");
					selParm.addData("CLASS_CODE", "1");
					selParm.addData("INV_CODE", "");
					selParm.addData("INV_DESC", "");
					selParm.addData("QTY", 1.0);
					selParm.addData("AR_AMT",
							selParm.getDouble("OWN_PRICE", 0) * 1.0);
					selParm.addData("BATCH_SEQ", "123");// 调试用
					selParm.addData("VALID_DATE", "");
					selParm.addData("BILL_FLG", "Y");
					selParm.addData("BILL_DATE", now);
					selParm.addData("EXE_DEPT_CODE", deptCode);
					selParm.addData("DESCRIPTION", "");
					// System.out.println("----------药品------END--------");
					// }
					// 物资
				} else {
					// 高值物资
					String InvSql = "SELECT B.INV_CODE, B.INV_CHN_DESC AS INV_DESC, B.ORDER_CODE,"
							+ " B.COST_PRICE AS OWN_PRICE, B.STOCK_UNIT AS UNIT_CODE,"
							+ " A.BATCH_NO AS BATCH_SEQ,B.DESCRIPTION,"
							+ " A.VALID_DATE"
							+ " FROM INV_STOCKDD A, INV_BASE B"
							+ " WHERE A.INV_CODE = B.INV_CODE AND A.RFID = '"
							+ BarCode + "'";
					// System.out.println("物资sql="+InvSql);
					selParm = new TParm(TJDODBTool.getInstance().select(InvSql));
					if (selParm.getCount("INV_CODE") > 0) {
						selParm
								.addData("DESC", selParm
										.getValue("INV_DESC", 0));
						selParm.addData("OPMED_CODE", "");
						selParm.addData("ORDER_DESC", "");

						// if(selParm.getValue("INV_CODE",
						// 0).trim().length()>7){
						selParm.addData("CLASS_CODE", "3");
						// selParm.addData("BILL_FLG", "Y");
						// }else{
						// selParm.addData("CLASS_CODE", "2");
						// selParm.addData("BILL_FLG", "N");
						// }
						selParm.addData("BILL_FLG", "N");
						selParm.addData("EXE_DEPT_CODE", deptCode);

						if (!selParm.getValue("ORDER_CODE", 0).equals("")) {
							String order_code = selParm.getValue("ORDER_CODE",
									0);
							String sqlcode = "SELECT OWN_PRICE,UNIT_CODE,ORDER_DESC,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
									+ order_code + "'";
							TParm parmValue = new TParm(TJDODBTool
									.getInstance().select(sqlcode));
							selParm.setData("OWN_PRICE", 0, parmValue.getValue(
									"OWN_PRICE", 0));
							selParm.setData("UNIT_CODE", 0, parmValue.getValue(
									"UNIT_CODE", 0));
							selParm.setData("ORDER_DESC", 0, parmValue
									.getValue("ORDER_DESC", 0));
							selParm.setData("BILL_FLG", 0, "Y");
							if (!parmValue.getValue("EXEC_DEPT_CODE", 0)
									.equals("")) {
								selParm.setData("EXE_DEPT_CODE", 0, parmValue
										.getValue("EXEC_DEPT_CODE", 0));
							}

						}

						selParm.addData("QTY", 1.0);
						selParm.addData("AR_AMT", selParm.getDouble(
								"OWN_PRICE", 0) * 1.0);
						// System.out.println("aaa===="+selParm.getValue("ORDER_CODE",
						// 0));
						selParm.addData("BILL_DATE", now);
						// 低值物资
					} else {
						String sql = " SELECT B.INV_CODE, B.INV_CHN_DESC AS INV_DESC, B.ORDER_CODE,"
								+ " B.COST_PRICE AS OWN_PRICE, B.STOCK_UNIT AS UNIT_CODE,B.DESCRIPTION"
								+ " FROM  INV_BASE B"
								+ " WHERE B.INV_CODE = '"
								+ BarCode + "'";
						selParm = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (selParm.getCount("INV_CODE") > 0) {
							selParm.addData("DESC", selParm.getValue(
									"INV_DESC", 0));
							selParm.addData("OPMED_CODE", "");
							selParm.addData("ORDER_DESC", "");
							selParm.addData("CLASS_CODE", "2");
							// 如果有order_code默认计费，如果没有order_code，默认不计费
							selParm.addData("BILL_FLG", "N");
							selParm
									.addData("EXE_DEPT_CODE", Operator
											.getDept());
							if (selParm != null
									&& !selParm.getValue("ORDER_CODE", 0)
											.equals("")) {
								String order_code = selParm.getValue(
										"ORDER_CODE", 0);
								String sqlcode = "SELECT OWN_PRICE,UNIT_CODE,ORDER_DESC,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
										+ order_code + "'";
								TParm parmValue = new TParm(TJDODBTool
										.getInstance().select(sqlcode));
								selParm.setData("OWN_PRICE", 0, parmValue
										.getValue("OWN_PRICE", 0));
								selParm.setData("UNIT_CODE", 0, parmValue
										.getValue("UNIT_CODE", 0));
								selParm.setData("ORDER_DESC", 0, parmValue
										.getValue("ORDER_DESC", 0));
								selParm.setData("BILL_FLG", 0, "Y");
								if (!parmValue.getValue("EXEC_DEPT_CODE", 0)
										.equals("")) {
									selParm.setData("EXE_DEPT_CODE", 0,
											parmValue.getValue(
													"EXEC_DEPT_CODE", 0));
								}

							}
							selParm.addData("BATCH_SEQ", "");
							selParm.addData("QTY", 1.0);
							selParm.addData("AR_AMT", selParm.getDouble(
									"OWN_PRICE", 0) * 1.0);
							selParm.addData("BILL_DATE", now);
							selParm.addData("DESCRIPTION", "");
						} else {
							// System.out.println("--------服务费 START-----");
							String Sql = "SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"
									+ BarCode + "' AND CAT1_TYPE = 'OTH'";
							TParm serviceParm = new TParm(TJDODBTool
									.getInstance().select(Sql));
							if (serviceParm.getCount() > 0) {
								Sql = "SELECT  A.ORDER_CODE, A.ORDER_DESC, A.OWN_PRICE, A.UNIT_CODE"
										+ " FROM SYS_FEE A WHERE CAT1_TYPE = 'OTH' AND A.ORDER_CODE = '"
										+ BarCode + "'";
								selParm = new TParm(TJDODBTool.getInstance()
										.select(Sql));
								selParm.addData("DESC", selParm.getValue(
										"ORDER_DESC", 0));
								selParm.addData("OPMED_CODE", "");
								selParm.addData("CLASS_CODE", "5");
								selParm.addData("INV_CODE", "");
								selParm.addData("INV_DESC", "");
								selParm.addData("QTY", 1.0);
								selParm.addData("AR_AMT", selParm.getDouble(
										"OWN_PRICE", 0) * 1.0);
								selParm.addData("BATCH_SEQ", "123");// 调试用
								selParm.addData("VALID_DATE", "");
								selParm.addData("BILL_FLG", "Y");
								selParm.addData("BILL_DATE", now);
								selParm.addData("EXE_DEPT_CODE", deptCode);
								selParm.addData("DESCRIPTION", "");
								// System.out.println("----------服务费-----END--------");
							} else {
								// ======================手术包 开始
								// =============================
								// =======================手术包
								// 结束=============================
							}
						}
					}

				}
			}
		}

		String receiptNo = SystemTool.getInstance().getNo("ALL", "EKT",
				"BUSINESS_NO", "BUSINESS_NO");
		selParm.addData("BUSINESS_NO", receiptNo);
		selParm.addData("SEQ", "1");// ??????????????
		selParm.addData("CASE_NO", case_no);
		selParm.addData("MR_NO", mr_no);
		selParm.addData("BAR_CODE", BarCode);
		selParm.addData("YEAR_MONTH", StringTool.getString(now, "yyyyMM"));
		selParm.addData("NS_CODE", optUser);// 11111111
		selParm.addData("DEPT_CODE", deptCodeOfPat);// ////22222222222
		selParm.addData("CASE_NO_SEQ", "");
		selParm.addData("SEQ_NO", "");
		selParm.addData("OPT_USER", optUser);// 333333333333
		selParm.addData("OPT_DATE", now);

		selParm.addData("OPT_TERM", optTerm);// 444444444444444
		selParm.addData("OP_ROOM", op_room);

		if (packBarCode.equals("")) {
			selParm.addData("PACK_BARCODE", "");
			selParm.addData("PACK_DESC", "");
			selParm.addData("PACK_GROUP_NO", "");
			selParm.addData("USED_FLG", "");
			selParm.addData("CHECK_FLG", "");
		} else {
			selParm.addData("PACK_BARCODE", packBarCode);
			selParm.addData("PACK_DESC", packDesc);
			selParm.addData("PACK_GROUP_NO", packGroupNo);
			selParm.addData("USED_FLG", "Y");
			selParm.addData("CHECK_FLG", "N");
		}

		selParm.addData("PAT_NAME", patName);
		// System.out.println("-----------onBarCode  结束----------");
		// System.out.println("onBarCode--修改结果->"+selParm);
		return selParm;
	}

	/**
	 * 病案号回车查询
	 * */

	public String onMrNo(String mrNo, String admType) {
		// System.out.println("-------start-------");
		admType = "I";

		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("ADM_TYPE", admType);
		SPCINVRecordAction action = new SPCINVRecordAction();
		TParm selParm = action.onMrNo(parm);
		StringBuffer strBuf = new StringBuffer();
		// System.out.println("------------到底有没有数量--------->"+selParm.getCount("MR_NO"));
		if (selParm == null || selParm.equals("")
				|| selParm.getCount("MR_NO") < 0) {
			strBuf.append("<ERR>");
			strBuf.append("-1");
			strBuf.append("</ERR>");
			return strBuf.toString();
		} else {
			strBuf.append("<ERR>");
			strBuf.append("0");
			strBuf.append("</ERR>");
		}

		strBuf.append("<OPE_MRNO_RESULT>");

		strBuf.append("<MR_NO>");
		strBuf.append(selParm.getData("MR_NO", 0));
		strBuf.append("</MR_NO>");

		strBuf.append("<IPD_NO>");
		strBuf.append(selParm.getData("IPD_NO", 0));
		strBuf.append("</IPD_NO>");

		strBuf.append("<PAT_NAME>");
		strBuf.append(selParm.getData("PAT_NAME", 0));
		strBuf.append("</PAT_NAME>");
		// 计算年龄 start
		Timestamp sysDate = SystemTool.getInstance().getDate();

		String birthDate = selParm.getData("BIRTH_DATE", 0).toString()
				.substring(0, 19).replace("-", "/");

		Timestamp birth_date = new Timestamp(Date.parse(birthDate));

		Timestamp temp = birth_date == null ? sysDate : birth_date;

		String age = "0";
		if (birth_date != null)
			age = OdiUtil.getInstance().showAge(temp, sysDate);

		else
			age = "";
		// selParm.addData("AGE", age);
		// 计算年龄 end

		strBuf.append("<BIRTH_DATE>");
		strBuf.append(age);
		strBuf.append("</BIRTH_DATE>");
		String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID = '"
				+ selParm.getData("SEX_CODE", 0) + "'";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		strBuf.append("<SEX_CODE>");
		strBuf.append(parm1.getData("CHN_DESC", 0));
		strBuf.append("</SEX_CODE>");

		strBuf.append("<CASE_NO>");
		strBuf.append(selParm.getData("CASE_NO", 0));
		strBuf.append("</CASE_NO>");

		String sql2 = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"
				+ selParm.getData("DEPT_CODE", 0) + "'";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		strBuf.append("<DEPT_CODE>");
		strBuf.append(parm2.getData("DEPT_CHN_DESC", 0));
		strBuf.append("</DEPT_CODE>");

		String sql3 = "SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE = '"
				+ selParm.getData("STATION_CODE", 0) + "'";
		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
		strBuf.append("<STATION_CODE>");
		strBuf.append(parm3.getData("STATION_DESC", 0));
		strBuf.append("</STATION_CODE>");

		strBuf.append("<DEPT_CODE_TRUE>");
		strBuf.append(selParm.getData("DEPT_CODE", 0));
		strBuf.append("</DEPT_CODE_TRUE>");

		strBuf.append("<STATION_CODE_TRUE>");
		strBuf.append(selParm.getData("STATION_CODE", 0));
		strBuf.append("</STATION_CODE_TRUE>");

		strBuf.append("</OPE_MRNO_RESULT>");

		System.out.println(strBuf.toString());
		// String result = "SUCCESS";
		return strBuf.toString();
		// return result;
	}

	public String queryByBarcode(String mrNo, String caseNo, String barCode,
			String optUser, String deptCode, String stationCode,
			String regionCode, String optTerm, String patName,
			String deptCodeOfPat, String packBarCode, String packDesc,
			String packGroupNos) {
		// System.out.println("---------queryByBarcodes  start---------");
		String sql2 = "SELECT * FROM SPC_INV_RECORD WHERE BAR_CODE = '"
				+ barCode + "'";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		boolean flgUnique = false;
		String mr_no = "";
		if (parm2.getCount() > 0) {
			String sqlHigh = "SELECT INV_CODE FROM INV_STOCKDD WHERE RFID = '"
					+ barCode + "'";

			TParm parmHigh = new TParm(TJDODBTool.getInstance().select(sqlHigh));

			if (parmHigh.getCount("INV_CODE") > 0) {

				flgUnique = true;
				mr_no = parm2.getData("MR_NO", 0).toString();
			}
			String sqlPha = "SELECT TOXIC_ID FROM IND_CONTAINERD WHERE TOXIC_ID = '"
					+ barCode + "'";

			TParm parmPha = new TParm(TJDODBTool.getInstance().select(sqlPha));
			if (parmPha.getCount("TOXIC_ID") > 0) {
				flgUnique = true;
				mr_no = parm2.getData("MR_NO", 0).toString();
			}
		}

		StringBuffer strBuf = new StringBuffer();
		// System.out.println("标记位："+flgUnique);
		if (parm2.getCount() > 0 && flgUnique) {
			strBuf.append("<COUNT>");
			strBuf.append("1");
			strBuf.append("</COUNT>");
			strBuf.append("<COUNTMR_NO>");
			strBuf.append(mr_no);
			strBuf.append("</COUNTMR_NO>");
		} else {
			strBuf.append("<COUNT>");
			strBuf.append("0");
			strBuf.append("</COUNT>");
		}
		// ==================
		// 物品名称
		String desc = "";
		// 规格
		String description = "";
		// 数量
		String qty = "";
		// 单位
		String unit = "";
		// 单价
		String own_price = "";
		// 总价
		String ar_amt = "";

		// ====================start=====================
		String sql = "";
		String pack_code = "";
		if (barCode.length() == 12) {
			pack_code = barCode;// 手术包的唯一条码
		}
		if (barCode.length() == 12
				&& pack_code.substring(6, 12).equals("000000")) {
			// 诊疗包
			// this.messageBox("诊疗包");
			sql = "SELECT B.BARCODE AS PACK_CODE,A.PACK_DESC,A.PY2,A.DESCRIPTION,A.USE_COST,A.VALUE_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PACK_CODE AS PACK_BATCH_NO"
					+ " FROM INV_PACKM A,INV_PACKSTOCKM B"
					+ " WHERE A.PACK_CODE = B.PACK_CODE"
					+ "       AND B.BARCODE = '"
					+ pack_code
					+ "'"
					+ " ORDER BY B.PACK_BATCH_NO DESC";
			// System.out.println("诊疗包主包---"+sql);

		} else if (barCode.length() == 12
				&& !pack_code.substring(6, 12).equals("000000")) {
			// 手术包
			sql = " SELECT B.BARCODE AS PACK_CODE,A.PACK_DESC,A.PY1,A.DESCRIPTION,A.USE_COST,A.VALUE_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'' AS B.PACK_BATCH_NO"
					+ " FROM INV_PACKM A, INV_PACKSTOCKM_HISTORY B"
					+ " WHERE A.PACK_CODE = B.PACK_CODE "
					+ "       AND B.BARCODE = '" + pack_code + "'";
		}
		TParm selParm = new TParm();
		if (!sql.equals("")) {
			selParm = new TParm(TJDODBTool.getInstance().select(sql));
		}

		if (selParm.getCount() > 0) {
			if (selParm.getCount("PACK_DESC") < 0) {
				strBuf.append("<ERR>");
				strBuf.append("-1");
				strBuf.append("</ERR>");
				return strBuf.toString();
			} else {
				strBuf.append("<ERR>");
				strBuf.append("0");
				strBuf.append("</ERR>");
			}
			String pack_desc = selParm.getData("PACK_DESC", 0).toString();

			String packBacthNo = selParm.getData("PACK_BATCH_NO", 0).toString();
			String sqlD = getSql(pack_code, packBacthNo);// 查询细表
			TParm selParmD = new TParm(TJDODBTool.getInstance().select(sqlD));
			HashMap map = new HashMap();
			// 查询该病人是否扫描过这个包
			String sqls = "SELECT MAX(PACK_GROUP_NO) AS PACK_GROUP_NO"
					+ " FROM SPC_INV_RECORD" + " WHERE CASE_NO = '" + caseNo
					+ "' " + " AND MR_NO = '" + mrNo + "'"
					+ " AND PACK_BARCODE = '" + pack_code + "'";
			// System.out.println("库里是否扫描过这个包"+sqls);
			TParm parmPack = new TParm(TJDODBTool.getInstance().select(sqls));
			// System.out.println(parmPack.getCount("PACK_GROUP_NO"));
			if (parmPack.getInt("PACK_GROUP_NO", 0) < 0) {
				packGroupNo = 0;
			} else {
				packGroupNo = parmPack.getInt("PACK_GROUP_NO", 0) + 1;
			}

			desc = pack_desc;
			// 规格
			description = "无";
			// 数量
			qty = "1";
			// 单位
			unit = "包";
			// 单价
			own_price = "0";
			// 总价
			ar_amt = "0";
		} else {
			// System.out.println("非手术包执行-----------");
			TParm onBarCodeParm = this.onBarCode(barCode, mrNo, caseNo,
					optUser, deptCode, stationCode, regionCode, optTerm,
					patName, deptCodeOfPat, "", "", "", "");
			// System.out.println("onBarCodeParm--->"+onBarCodeParm);

			// StringBuffer strBuf = new StringBuffer();
			if (onBarCodeParm.getCount("DESC") < 0) {
				strBuf.append("<ERR>");
				strBuf.append("-1");
				strBuf.append("</ERR>");
				return strBuf.toString();
			} else {
				strBuf.append("<ERR>");
				strBuf.append("0");
				strBuf.append("</ERR>");
			}
			// 物品名称
			desc = onBarCodeParm.getData("DESC", 0).toString();
			// 规格
			description = onBarCodeParm.getData("DESCRIPTION", 0).toString();
			// 数量
			qty = onBarCodeParm.getData("QTY", 0).toString();
			// 单位
			unit = onBarCodeParm.getData("UNIT_CODE", 0).toString();
			// 单价
			own_price = onBarCodeParm.getData("OWN_PRICE", 0).toString();
			// 总价
			ar_amt = onBarCodeParm.getData("AR_AMT", 0).toString();
		}

		// ====================end=======================

		// ====================

		// TParm onBarCodeParm = this.onBarCode(barCode, mrNo,
		// caseNo,optUser,deptCode,stationCode,regionCode,optTerm);
		// System.out.println("onBarCodeParm--->"+onBarCodeParm);
		// //StringBuffer strBuf = new StringBuffer();
		// if(onBarCodeParm.getCount("DESC")<0){
		// strBuf.append("<ERR>");
		// strBuf.append("-1");
		// strBuf.append("</ERR>");
		// return strBuf.toString();
		// }else{
		// strBuf.append("<ERR>");
		// strBuf.append("0");
		// strBuf.append("</ERR>");
		// }

		strBuf.append("<OPE_MRNO_RESULT>");
		strBuf.append("<BAR_CODE>");
		strBuf.append(barCode);
		strBuf.append("</BAR_CODE>");

		strBuf.append("<DESC>");
		strBuf.append(desc);
		strBuf.append("</DESC>");

		strBuf.append("<DESCRIPTION>");
		strBuf.append(description);
		strBuf.append("</DESCRIPTION>");

		strBuf.append("<QTY>");
		strBuf.append(qty);
		strBuf.append("</QTY>");

		String sql1 = "SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '"
				+ unit + "'";
		TParm parmUnit = new TParm(TJDODBTool.getInstance().select(sql1));
		if (parmUnit.getCount() > 0) {
			unit = parmUnit.getData("UNIT_CHN_DESC", 0).toString();
		}

		strBuf.append("<UNIT_CODE>");
		strBuf.append(unit);
		strBuf.append("</UNIT_CODE>");

		strBuf.append("<OWN_PRICE>");
		strBuf.append(own_price);
		strBuf.append("</OWN_PRICE>");

		strBuf.append("<AR_AMT>");
		strBuf.append(ar_amt);
		strBuf.append("</AR_AMT>");

		strBuf.append("</OPE_MRNO_RESULT>");
		// .out.println("-------查询结果--------"+strBuf.toString());
		return strBuf.toString();
	}

	/**
	 * 得到手术包细项
	 * */
	public String getSql(String barCode, String packBacthNo) {
		String sql = "";

		if (barCode.substring(6, 12).equals("000000")) {
			// 诊疗包
			sql = " SELECT B.INV_CODE,B.INV_CHN_DESC,A.DESCRIPTION,A.QTY,A.STOCK_UNIT,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY "
					+ " FROM INV_PACKD A,INV_BASE B "
					+ " WHERE A.INV_CODE = B.INV_CODE  "
					+ " AND A.PACK_CODE = '" + packBacthNo + "'";
			// " AND A.PACK_BATCH_NO  = '"+packBacthNo+"'";
			// System.out.println("诊疗包细包----》"+sql);
		} else {
			// 手术包
			sql = " SELECT B.INV_CODE,B.INV_CHN_DESC,A.DESCRIPTION,A.QTY,A.STOCK_UNIT,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY "
					+ " FROM INV_PACKSTOCKD_HISTORY A,INV_BASE B"
					+ " WHERE A.INV_CODE = B.INV_CODE  AND A.BARCODE = '"
					+ barCode + "' ";
			// System.out.println("手术包细包"+sql);
		}

		return sql;

	}

	/**
	 * 加密字串
	 * 
	 * @param text
	 *            String 源字串
	 * @return String 加密后字串
	 */
	private String encrypt(String text) {
		String av_str = "";
		try {
			byte aa[] = text.getBytes("UTF-16BE");

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < aa.length; i++) {
				aa[i] = (byte) (~aa[i]);
				sb.append(Integer.toHexString(aa[i]).substring(6));
			}
			av_str = sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return av_str;
	}

	/**
	 * 登录
	 * */

	public String onLogin(String userId, String password) {

		// =====================================================================================================
		String enPass = encrypt(password);

		StringBuffer strBuf = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date(0));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		dstr = sysDate.toString().substring(0, 19);
		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + userId + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";

		// System.out.println("-------sql---"+sql);

		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		// System.out.println(count);
		/** 密码错误 */
		if (0 == count) {
			strBuf.append("<ERR>");
			strBuf.append("1");
			strBuf.append("</ERR>");

		} else {
			strBuf.append("<ERR>");
			strBuf.append("0");
			strBuf.append("</ERR>");
			// return "0";
		}

		// =====================================================================================================

		String onQueryOptUser = onQueryOptUser(userId);

		strBuf.append(onQueryOptUser);

		// System.out.println("登录返回数据---》"+strBuf.toString());
		return strBuf.toString();
	}

	/**
	 * 根据用户名查询科室和病区,区域
	 * */

	public String onQueryOptUser(String userId) {

		String sqlDept = "SELECT A.DEPT_CODE,B.DEPT_CHN_DESC,MAIN_FLG "
				+ " FROM SYS_OPERATOR_DEPT A ,SYS_DEPT B "
				+ " WHERE A.DEPT_CODE = B.DEPT_CODE" + " AND A.USER_ID = '"
				+ userId + "'";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(sqlDept));
		StringBuffer strBuf = new StringBuffer();
		int deptCount = deptParm.getCount();

		strBuf.append("<DEPT_COUNT>");
		strBuf.append(deptCount);
		strBuf.append("</DEPT_COUNT>");

		String mainDeptCode = "";
		String mainDeptDesc = "";
		String dept_code = "";
		String dept_desc = "";

		for (int i = 0; i < deptCount; i++) {
			String main_flg = deptParm.getData("MAIN_FLG", i).toString();
			if (main_flg.equals("Y")) {
				mainDeptCode = deptParm.getData("DEPT_CODE", i).toString();
				mainDeptDesc = deptParm.getData("DEPT_CHN_DESC", i).toString();
			} else {
				if (i == deptCount - 1) {
					dept_code += deptParm.getData("DEPT_CODE", i).toString();
					dept_desc += deptParm.getData("DEPT_CHN_DESC", i)
							.toString();
				} else {
					dept_code += deptParm.getData("DEPT_CODE", i).toString()
							+ ",";
					dept_desc += deptParm.getData("DEPT_CHN_DESC", i)
							.toString()
							+ ",";
				}

			}
		}

		strBuf.append("<MAIN_DEPT_CODE>");
		strBuf.append(mainDeptCode);
		strBuf.append("</MAIN_DEPT_CODE>");

		strBuf.append("<MAIN_DEPT_CHN_DESC>");
		strBuf.append(mainDeptDesc);
		strBuf.append("</MAIN_DEPT_CHN_DESC>");

		strBuf.append("<DEPT_CODE>");
		strBuf.append(dept_code);
		strBuf.append("</DEPT_CODE>");

		strBuf.append("<DEPT_CHN_DESC>");
		strBuf.append(dept_desc);
		strBuf.append("</DEPT_CHN_DESC>");

		deptParm.getValue("DEPT_CODE");

		String sqlStation = " SELECT B.STATION_CODE,B.STATION_DESC,MAIN_FLG "
				+ " FROM SYS_OPERATOR_STATION A ,SYS_STATION B "
				+ " WHERE A.STATION_CLINIC_CODE = B.STATION_CODE "
				+ " AND A.USER_ID = '" + userId + "'";

		TParm stationParm = new TParm(TJDODBTool.getInstance().select(
				sqlStation));

		int stationCount = stationParm.getCount();

		strBuf.append("<STATION_CODE>");
		strBuf.append(stationCount);
		strBuf.append("</STATION_CODE>");

		String mainStationCode = "";
		String mainStationDesc = "";
		String stationCode = "";
		String stationDesc = "";

		for (int i = 0; i < stationCount; i++) {
			String main_flg = stationParm.getData("MAIN_FLG", i).toString();
			if (main_flg.equals("Y")) {
				mainStationCode = stationParm.getData("STATION_CODE", i)
						.toString();
				mainStationDesc = stationParm.getData("STATION_DESC", i)
						.toString();
			} else {
				if (i == stationCount - 1) {
					stationCode += stationParm.getData("STATION_CODE", i)
							.toString();
					stationDesc += stationParm.getData("STATION_DESC", i)
							.toString();
				} else {
					stationCode += stationParm.getData("STATION_CODE", i)
							.toString()
							+ ",";
					stationDesc += stationParm.getData("STATION_DESC", i)
							.toString()
							+ ",";
				}
			}
		}
		strBuf.append("<MAIN_STATION_CODE>");
		strBuf.append(mainStationCode);
		strBuf.append("</MAIN_STATION_CODE>");

		strBuf.append("<MAIN_STATION_DESC>");
		strBuf.append(mainStationDesc);
		strBuf.append("</MAIN_STATION_DESC>");

		strBuf.append("<STATION_CODE>");
		strBuf.append(stationCode);
		strBuf.append("</STATION_CODE>");

		strBuf.append("<STATION_DESC>");
		strBuf.append(stationDesc);
		strBuf.append("</STATION_DESC>");

		String sqlRegion = " SELECT REGION_CODE FROM SYS_OPERATOR  WHERE USER_ID = '"
				+ userId + "'";
		TParm regionParm = new TParm(TJDODBTool.getInstance().select(sqlRegion));
		int countRegion = regionParm.getCount();
		if (countRegion > 0) {
			strBuf.append("<REGION_CODE>");
			strBuf.append(regionParm.getData("REGION_CODE", 0));
			strBuf.append("</REGION_CODE>");
		}
		return strBuf.toString();
	}

	/** 获取系统术间信息 */
	public String getHisRooms() {

		StringBuffer strBff = new StringBuffer();
		strBff.append("<OPE_PDA_ROOMS>");
		strBff.append("");

		String sql = "select ID,CHN_DESC from SYS_DICTIONARY where GROUP_ID = 'OPE_OPROOM'";
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < result.getCount(); i++) {
			String id = result.getValue("ID", i);
			String chnDesc = result.getValue("CHN_DESC", i);
			strBff.append("<ROOM>");
			strBff.append("<ID>");
			strBff.append(id);
			strBff.append("</ID>");
			strBff.append("<CHN_DESC>");
			strBff.append(chnDesc);
			strBff.append("</CHN_DESC>");
			strBff.append("</ROOM>");
		}
		strBff.append("</OPE_PDA_ROOMS>");
		// System.out.println("术间信息---》"+ strBff.toString());
		return strBff.toString();
	}

	/**
	 * 手术交接查询接口
	 * */

	public String getOP_Data(String mr_No, String dateStr) {
		// 1.输入参数不合法（两个参数，具体的参数要求包括，长度，合法性。日期格式，日期限制是否是今天）
		// 2.查询过程中出错，具体出错原因javahis是否能出来
		// 3.返回状态：0成功返回，1查询出错, 2 病案号校验不通过，3日期校验不通过，4查无结果，5查出多条记录
		// 4.日志记录

		/** 校验病案号格式 */
		if (1 == this.checkStr(mr_No, "")) {
			return this.returnIncorrectMsg(2);
		}

		String mr_no = PatTool.getInstance().checkMrno(
				TypeTool.getString(mr_No));
		/** 校验日期格式 */
		if (1 == this.checkStr(dateStr, "")) {
			return this.returnIncorrectMsg(3);
		}
		String sql = "SELECT A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ "B.CHN_DESC AS OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
				+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1 "
				+ "FROM OPE_OPBOOK A,SYS_DICTIONARY B,SYS_PATINFO C,SYS_DICTIONARY F,"
				+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"
				+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N  "
				+ "WHERE B.GROUP_ID = 'OPE_OPROOM'AND A.ROOM_NO = B.ID(+) "
				+ "AND A.MR_NO = C.MR_NO (+) "
				+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "
				+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   "
				+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
				+ "AND A.MAIN_SURGEON = I.USER_ID(+) "  
				+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
				+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
				+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "
				+ "AND A.ANA_USER1 = M.USER_ID(+) "
				+ "AND A.EXTRA_USER1 = N.USER_ID(+) "
				+ "AND A.MR_NO='"
				+ mr_no
				+ "' "
				+ "AND OP_DATE BETWEEN "
				+ "TO_DATE('"
				+ dateStr
				+ " 00:00:00','yyyymmdd hh24:mi:ss')"
				+ "AND "
				+ "TO_DATE('"
				+ dateStr
				+ " 23:59:59','yyyymmdd hh24:mi:ss')"
				+ "AND A.CANCEL_FLG <> 'Y' " + "ORDER BY OPBOOK_SEQ";

		Server.autoInit(this);

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// System.out.println("---sql-查询术间信息-"+sql);
		/** 查询中出错 */
		if (result.getErrCode() < 0) {
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (result.getCount() < 1) {
			return this.returnIncorrectMsg(4);
		}

		/** 手术单号 */
		String opbookSeq = result.getValue("OPBOOK_SEQ", 0);
		/** 术间 */
		String opRoom = result.getValue("ROOM_NO", 0);
		/** 时间 */
		String opDate = result.getValue("OP_DATE", 0);
		/** 姓名 */
		String patName = result.getValue("PAT_NAME", 0);
		/** 性别 */
		String sex = result.getValue("SEX", 0);
		/** 生日 */
		Date birthDate = (Date) result.getData("BIRTH_DATE", 0);
		Date today = new Date(0);// ??????????????????
		int age = today.getYear() - birthDate.getYear();// 年龄

		/** 身高 */
		String height = result.getValue("HEIGHT", 0);
		/** 体重 */
		String weight = result.getValue("WEIGHT", 0);
		/** 病案号 */
		String mrNo = result.getValue("MR_NO", 0);
		/** 诊断ICD */
		String icdChnDesc = result.getValue("ICD_CHN_DESC", 0);
		/** 手术ICD */
		String optChnDesc = result.getValue("OPT_CHN_DESC", 0);
		/** 备注 */
		String remark = result.getValue("REMARK", 0);

		/** 术者 */
		String mainSurgeon = result.getValue("MAIN_SURGEON", 0);
		/** 一助 */
		String bookAst1 = result.getValue("BOOK_AST_1", 0);
		/** 护士1 */
		String circuleUser1 = result.getValue("CIRCULE_USER1", 0);
		/** 护士2 */
		String circuleUser2 = result.getValue("CIRCULE_USER2", 0);
		/** 麻醉 */
		String anaUser1 = result.getValue("ANA_USER1", 0);
		/** 体外 */
		String extraUser1 = result.getValue("EXTRA_USER1", 0);

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PDA_SEARCH_RESULT>");

		strBuf.append("<STATUS>");
		if (result.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(result.getCount());
		strBuf.append("</COUNT>");

		strBuf.append("<RESULT>");
		strBuf.append("<OPBOOK_SEQ>");
		strBuf.append(opbookSeq);
		strBuf.append("</OPBOOK_SEQ>");

		strBuf.append("<OP_ROOM>");
		strBuf.append(opRoom);
		strBuf.append("</OP_ROOM>");

		strBuf.append("<OP_DATE>");
		strBuf.append(opDate);
		strBuf.append("</OP_DATE>");

		strBuf.append("<PAT_NAME>");
		strBuf.append(patName);
		strBuf.append("</PAT_NAME>");

		strBuf.append("<SEX>");
		strBuf.append(sex);
		strBuf.append("</SEX>");

		strBuf.append("<AGE>");
		strBuf.append(age);
		strBuf.append("</AGE>");

		strBuf.append("<HEIGHT>");
		strBuf.append(height);
		strBuf.append("</HEIGHT>");

		strBuf.append("<WEIGHT>");
		strBuf.append(weight);
		strBuf.append("</WEIGHT>");

		strBuf.append("<MR_NO>");
		strBuf.append(mrNo);
		strBuf.append("</MR_NO>");

		strBuf.append("<ICD_CHN_DESC>");
		strBuf.append(icdChnDesc);
		strBuf.append("</ICD_CHN_DESC>");

		strBuf.append("<OPT_CHN_DESC>");
		strBuf.append(optChnDesc);
		strBuf.append("</OPT_CHN_DESC>");

		strBuf.append("<REMARK>");
		strBuf.append(remark);
		strBuf.append("</REMARK>");

		strBuf.append("<MAIN_SURGEON>");
		strBuf.append(mainSurgeon);
		strBuf.append("</MAIN_SURGEON>");

		strBuf.append("<BOOK_AST_1>");
		strBuf.append(bookAst1);
		strBuf.append("</BOOK_AST_1>");

		strBuf.append("<CIRCULE_USER1>");
		strBuf.append(circuleUser1);
		strBuf.append("</CIRCULE_USER1>");

		strBuf.append("<CIRCULE_USER2>");
		strBuf.append(circuleUser2);
		strBuf.append("</CIRCULE_USER2>");

		strBuf.append("<ANA_USER1>");
		strBuf.append(anaUser1);
		strBuf.append("</ANA_USER1>");

		strBuf.append("<EXTRA_USER1>");
		strBuf.append(extraUser1);
		strBuf.append("</EXTRA_USER1>");

		strBuf.append("</RESULT>");
		strBuf.append("</OPE_PDA_SEARCH_RESULT>");
		// System.out.println(strBuf.toString());
		return strBuf.toString();
	}

	/**
	 * 查询不成功的报文 。如校验不通过时，或者查询数据库出错时 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnIncorrectMsg(int status) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("<OPE_PDA_SEARCH_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("<COUNT>");
		strBuf.append(String.valueOf(0));
		strBuf.append("</COUNT>");
		strBuf.append("<RESULT>");
		strBuf.append("<OPBOOK_SEQ></OPBOOK_SEQ>");
		strBuf.append("<OP_ROOM></OP_ROOM>");
		strBuf.append("<OP_DATE></OP_DATE>");
		strBuf.append("<PAT_NAME></PAT_NAME>");
		strBuf.append("<SEX></SEX>");
		strBuf.append("<AGE></AGE>");
		strBuf.append("<HEIGHT></HEIGHT>");
		strBuf.append("<WEIGHT></WEIGHT>");
		strBuf.append("<MR_NO></MR_NO>");
		strBuf.append("<ICD_CHN_DESC></ICD_CHN_DESC>");
		strBuf.append("<OPT_CHN_DESC></OPT_CHN_DESC>");
		strBuf.append("<REMARK></REMARK>");
		strBuf.append("<MAIN_SURGEON></MAIN_SURGEON>");
		strBuf.append("<BOOK_AST_1></BOOK_AST_1>");
		strBuf.append("<CIRCULE_USER1></CIRCULE_USER1>");
		strBuf.append("<CIRCULE_USER2></CIRCULE_USER2>");
		strBuf.append("<ANA_USER1></ANA_USER1>");
		strBuf.append("<EXTRA_USER1></EXTRA_USER1>");
		strBuf.append("</RESULT>");
		strBuf.append("</OPE_PDA_SEARCH_RESULT>");
		return strBuf.toString();
	}

	/** 保存接口 */
	// 校验参数
	// 返回状态：0 保存成功，1 保存数据库失败，2 OPBOOK_SEQ校验不通过，3 ROOM_NO校验不通过，4
	// TRANSFER_USER校验不通过，
	// 5 TRANSFER_DATE校验不通过，6 TIMEOUT_USER校验不通过，7 TIMEOUT_DATE校验不通过，
	// 8 DR_CONFORM_FLG校验不通过，9 ANA_CONFORM_FLG校验不通过
	// 日志
	public String saveOP_Data(String opeBookSeq, String roomNo,
			String transferUser, String transfer_Date, String timeoutUser,
			String timeoutDate, String drConformFlg, String anaConformFlg) {
		/** 校验opeBookSeq */
		if (this.isBlank(opeBookSeq) || 1 == this.checkStr(opeBookSeq, "")) {
			return this.returnSaveMsg(2);
		}
		/** 校验roomNo */
		if (!this.isBlank(roomNo) && 1 == this.checkStr(roomNo, "")) {
			return this.returnSaveMsg(3);
		}
		/** 校验transferUser */
		if (!this.isBlank(transferUser) && 1 == this.checkStr(transferUser, "")) {
			return this.returnSaveMsg(4);
		}
		/** 校验transfer_Date */
		if (!this.isBlank(transfer_Date)
				&& 1 == this.checkStr(transfer_Date, "")) {
			return this.returnSaveMsg(5);
		}
		/** 校验timeoutUser */
		if (!this.isBlank(timeoutUser) && 1 == this.checkStr(timeoutUser, "")) {
			return this.returnSaveMsg(6);
		}
		/** 校验timeoutDate */
		if (!this.isBlank(timeoutDate) && 1 == this.checkStr(timeoutDate, "")) {
			return this.returnSaveMsg(7);
		}
		/** 校验drConformFlg */
		if (!this.isBlank(drConformFlg) && 1 == this.checkStr(drConformFlg, "")) {  
			return this.returnSaveMsg(8);
		}
		/** 校验anaConformFlg */
		if (!this.isBlank(anaConformFlg)
				&& 1 == this.checkStr(anaConformFlg, "")) {
			return this.returnSaveMsg(9);
		}

		StringBuffer strBuf = new StringBuffer();
		strBuf.append("UPDATE OPE_OPBOOK SET ");

		/** roomNo */
		if (!this.isBlank(roomNo)) {
			strBuf.append("ROOM_NO='");
			strBuf.append(roomNo);
			strBuf.append("',");
		}
		/** transferUser */
		if (!this.isBlank(transferUser)) {
			strBuf.append("TRANSFER_USER='");
			strBuf.append(transferUser);
			strBuf.append("', ");
		}
		/** transfer_Date */
		if (!this.isBlank(transfer_Date)) {
			strBuf.append("TRANSFER_DATE=TO_DATE('");
			strBuf.append(transfer_Date);
			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
		}

		/** timeoutUser */
		if (!this.isBlank(timeoutUser)) {
			strBuf.append("TIMEOUT_USER='");
			strBuf.append(timeoutUser);
			strBuf.append("', ");
		}
		/** timeoutDate */
		if (!this.isBlank(timeoutDate)) {
			strBuf.append(" TIMEOUT_DATE=TO_DATE('");
			strBuf.append(timeoutDate);
			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
		}
		/** drConformFlg */
		if (!this.isBlank(drConformFlg)) {
			strBuf.append("DR_CONFORM_FLG='");
			strBuf.append(drConformFlg);
			strBuf.append("',");
		}
		/** anaConformFlg */
		if (!this.isBlank(anaConformFlg)) {
			strBuf.append("ANA_CONFORM_FLG='");
			strBuf.append(anaConformFlg);
			strBuf.append("', ");
		}
		strBuf.append("OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("' ");

		strBuf.append(" WHERE OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("'");

		Server.autoInit(this);

		// System.out.println("---strBuf.toString()--:"+strBuf.toString());

		TParm result = new TParm(TJDODBTool.getInstance().update(
				strBuf.toString()));

		if (result.getErrCode() < 0) {
			return this.returnSaveMsg(1);
		}

		return this.returnSaveMsg(0);
	}

	/**
	 * 保存接口返回报文 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnSaveMsg(int status) {

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PAD_SAVE_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("</OPE_PAD_SAVE_RESULT>");

		return strBuf.toString();
	}

	/**
	 * 校验字符串 入参：正则表达式 出参：int类型：0通过，1不通过 这个方法没写？？？？？？？？？？？？
	 */
	private int checkStr(String str, String regMath) {
		// if(!str.matches(regMath)){
		// return 1;
		// }
		return 0;
	}

	/** 校验是否为空 */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

	// fux modify 供应室 问题 strat
	/**
	 * 接受确认 根据条码 查询 包名 数量 条码 以及 接收人(可以自己填写) 查询INV_SUP_DISPENSED 表数据
	 * */

	public String getInvPack(String barCode) {
		// 是否应该只能查询出来没有接受人员的 ？
		String sql = "SELECT B.PACK_DESC,A.QTY,A.BARCODE,A.RECEIVE_USER,A.CHECK_USER,A.CHECK_DATE "
				+ " FROM INV_SUP_DISPENSED A,INV_PACKM B "
				+ " WHERE A.INV_CODE = B.PACK_CODE"
				+ " AND A.BARCODE = '"
				+ barCode + "'";                 
		// System.out.println("接受确认sql" + sql);
		// Server.autoInit(this);

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// System.out.println("---sql-查询术间信息-"+sql);
		/** 查询中出错 */
		if (result.getErrCode() < 0) {
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (result.getCount() < 1) {
			return this.returnIncorrectMsg(4);  
		}

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<INV_PDA_SEARCH_RESULT>");

		strBuf.append("<STATUS>");
		if (result.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(result.getCount());
		strBuf.append("</COUNT>");

		for (int i = 0; i < result.getCount(); i++) {
			/** 包名 */
			String packDesc = result.getValue("PACK_DESC", i);
			/** 数量 */
			String qty = result.getValue("QTY", i);
			/** 条码 */
			String barCodeTable = result.getValue("BARCODE", i);
			/** 接受人 */
			String receiveUser = result.getValue("RECEIVE_USER", i);
			/** 确认人 */
			String checkUser = result.getValue("CHECK_USER", i);
			/** 使用时间 */
			String checkDate = result.getValue("CHECK_DATE", i);

			strBuf.append("<RESULT" + i + ">");
			strBuf.append("<PACK_DESC" + i + ">");
			strBuf.append(packDesc);
			strBuf.append("</PACK_DESC" + i + ">");

			strBuf.append("<QTY" + i + ">");
			strBuf.append(qty);
			strBuf.append("</QTY" + i + ">");            

			strBuf.append("<BARCODE" + i + ">");
			strBuf.append(barCodeTable);
			strBuf.append("</BARCODE" + i + ">");
			
			strBuf.append("<CHECK_DATE" + i + ">");
			strBuf.append(checkDate);
			strBuf.append("</CHECK_DATE" + i + ">");  

			strBuf.append("<RECEIVE_USER" + i + ">");
			strBuf.append(receiveUser);
			strBuf.append("</RECEIVE_USER" + i + ">");  
 
			strBuf.append("<CHECK_USER" + i + ">");
			strBuf.append(checkUser);
			strBuf.append("</CHECK_USER" + i + ">");

			strBuf.append("</RESULT" + i + ">");
		}

		strBuf.append("</INV_PDA_SEARCH_RESULT>");
		// System.out.println(strBuf.toString());
		return strBuf.toString();
	}


	/**
	 * 接受确认 保存INV_SUP_DISPENSED 表 RECEIVE_USER RECEIVE_Date RECEIVE_DEPT 并更新
	 * INV_PACKSTOCKM 中的status 为6(已接受)
	 * */

	public String saveInvPack(String res) {
		int ipStart = res.indexOf("<IP>");
		int ipEnd = res.indexOf("</IP>");     
		String ip = res.substring(ipStart + 4, ipEnd); 
		TParm parm = new TParm();  
		parm.setData("RES", res);        
		TParm result = new TParm();
		//System.out.println("parm:" + parm);
		TSocket socket = new TSocket(ip, 8080, "web");
		result = TIOM_AppServer.executeAction(socket,  
				"action.bil.SPCINVRecordAction", "onSaveInvPack", parm);
		if (result.getErrCode() < 0) {
			return "false";
		}
		return "true";

	}

	/**
	 * 接受确认 保存INV_SUP_DISPENSED 表 CHECK_USER CHECK_DATE MR_NO,CASE_NO并更新
	 * */

	public String saveInvPackCheck(String res) {
		TParm parm = new TParm();
		parm.setData("RES", res);
		TParm result = new TParm();
		int ipStart = res.indexOf("<IP>");
		int ipEnd = res.indexOf("</IP>");     
		String ip = res.substring(ipStart + 4, ipEnd);
		//System.out.println("parm:" + parm);
		TSocket socket = new TSocket(ip, 8080, "web");
		result = TIOM_AppServer.executeAction(socket,
				"action.bil.SPCINVRecordAction", "onSaveInvPackCheck", parm);
		if (result.getErrCode() < 0) {
			return "false";
		}
		return "true";

	}

	/**
	 * (供应室)术前清点查询
	 * */

	public String queryOpeCheckBf(String bar) {
		String sql = " SELECT D.INV_CHN_DESC AS INV_DESC,C.QTY,C.STOCK_UNIT,E.UNIT_CHN_DESC AS UNIT_DESC ,A.CHECK_USER"
				+ " FROM INV_SUP_DISPENSED A,INV_PACKM B,INV_PACKD C,INV_BASE D,SYS_UNIT E"
				+ " WHERE A.INV_CODE = B.PACK_CODE"
				+ " AND B.PACK_CODE = C.PACK_CODE"
				+ " AND C.INV_CODE =D.INV_CODE"
				+ " AND C.STOCK_UNIT =E.UNIT_CODE "
				+ " AND A.BARCODE = '"
				+ bar + "'  ORDER BY D.INV_CHN_DESC,A.INV_CODE  ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		//System.out.println("---sql-(供应室)术前清点查询-" + sql);
		/** 查询中出错 */
		if (result.getErrCode() < 0) {
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (result.getCount() < 1) {
			return this.returnIncorrectMsg(4);
		}

		String sqlDesc = " SELECT DISTINCT B.PACK_DESC "
				+ " FROM INV_SUP_DISPENSED A,INV_PACKM B"
				+ " WHERE A.INV_CODE = B.PACK_CODE" + " AND A.BARCODE = '"
				+ bar + "' ";
		TParm resultDesc = new TParm(TJDODBTool.getInstance().select(sqlDesc));

		/** 查询中出错 */
		if (resultDesc.getErrCode() < 0) {
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (resultDesc.getCount() < 1) {
			return this.returnIncorrectMsg(4);
		}

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<INV_PDA_SEARCH_RESULT>");

		strBuf.append("<STATUS>");
		if (result.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(result.getCount());
		strBuf.append("</COUNT>");

		strBuf.append("<PACKM>");
		strBuf.append(resultDesc.getValue("PACK_DESC", 0));
		strBuf.append("</PACKM>");

		for (int i = 0; i < result.getCount("INV_DESC"); i++) {
			/** 名称 */
			String packDesc = result.getValue("INV_DESC", i);
			/** 数量 */
			String qty = result.getValue("QTY", i);
			/** 单位 */
			String unit = result.getValue("UNIT_DESC", i);
			/** 术前数量 */
			String bfQry = result.getValue("QTY", i);
			/** 确认人 */
			String checkUser = result.getValue("CHECK_USER", i);

			strBuf.append("<RESULT" + i + ">");
			strBuf.append("<INV_DESC" + i + ">");
			strBuf.append(packDesc);
			strBuf.append("</INV_DESC" + i + ">");

			strBuf.append("<QTY" + i + ">");
			strBuf.append(qty);
			strBuf.append("</QTY" + i + ">");

			strBuf.append("<UNIT_DESC" + i + ">");
			strBuf.append(unit);
			strBuf.append("</UNIT_DESC" + i + ">");

			strBuf.append("<CHECK_USER" + i + ">");
			strBuf.append(checkUser);
			strBuf.append("</CHECK_USER" + i + ">");

			strBuf.append("</RESULT" + i + ">");
		}

		strBuf.append("</INV_PDA_SEARCH_RESULT>");
		//System.out.println(strBuf.toString());
		return strBuf.toString();

	}

	/**
	 * (供应室)关前 术后清点查询
	 * */

	public String queryOpeCheckBfCon(String bar, String seq, String statues) {
		// 加入一个action层以保证正确性
		TParm result = new TParm();
		String resultFlg = "";
		if ("术前".equals(statues)) {
			String sql = " SELECT A.BARCODE " + " FROM INV_SUP_OPE_CHECKD A"
					+ " WHERE A.BARCODE = '" + bar + "'  "
					+ " AND A.SEQ_NO = '" + Integer.parseInt(seq) + "' ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("---sql-(供应室)关前清点查询-" + sql);
			if (result.getCount() == 1) {
				resultFlg = "true";
			} else {
				resultFlg = "false";
			}
		} else if ("关前".equals(statues)) {
			String sql = " SELECT A.BARCODE " + " FROM INV_SUP_OPE_CHECKD A"
					+ " WHERE A.BARCODE = '" + bar + "' " + " AND A.SEQ_NO = '"
					+ Integer.parseInt(seq) + "'" + " AND A.BF_CLOSE_QTY = 0 ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("---sql-(供应室)关前清点查询-" + sql);
			if (result.getCount() == 1) {
				resultFlg = "false";
			} else {
				resultFlg = "true";
			}
		} else if ("术后".equals(statues)) {
			String sql = " SELECT A.BARCODE " + " FROM INV_SUP_OPE_CHECKD A"
					+ " WHERE A.BARCODE = '" + bar + "' " + " AND A.SEQ_NO = '"
					+ Integer.parseInt(seq) + "'" + " AND A.AF_CLOSE_QTY = 0 ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("---sql-(供应室)术后清点查询-" + sql);
			if (result.getCount() == 1) {
				resultFlg = "false";
			} else {
				resultFlg = "true";
			}
		}
		return resultFlg;

	}

	/**
	 * (供应室)关前 术后清点查询
	 * */

	public String queryOpeCheckOth(String bar, String statues) {
		// 加入一个action层以保证正确性
		TParm result = new TParm();
		StringBuffer strBuf = new StringBuffer();

		String sqlDesc = " SELECT DISTINCT B.PACK_DESC "
				+ " FROM INV_SUP_OPE_CHECKD A,INV_PACKM B"
				+ " WHERE A.PACK_CODE = B.PACK_CODE" + " AND A.BARCODE = '"
				+ bar + "' ";
		TParm resultDesc = new TParm(TJDODBTool.getInstance().select(sqlDesc));

		if ("关前".equals(statues)) {
			String sql = " SELECT D.INV_CHN_DESC AS INV_DESC,E.UNIT_CHN_DESC AS UNIT_DESC,C.QTY,"
					+ " A.BF_OPE_QTY,A.BF_CLOSE_QTY"
					+ " FROM INV_SUP_OPE_CHECKD A,INV_PACKM B,INV_PACKD C,INV_BASE D,SYS_UNIT E"
					+ " WHERE A.PACK_CODE = B.PACK_CODE"
					+ " AND A.PACK_CODE = C.PACK_CODE"
					+ " AND A.INV_CODE =D.INV_CODE"
					+ " AND A.INV_CODE = C.INV_CODE "
					+ " AND C.STOCK_UNIT =E.UNIT_CODE "
					+ " AND A.BARCODE = '"
					+ bar
					+ "'  "
					+
					// 必须已经审核才能查询出来
					" AND A.CHECK_USER1 IS NOT NULL "
					+ " AND A.CHECK_USER2 IS NOT NULL " + " ORDER BY A.SEQ_NO ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("---sql-(供应室)关前清点查询-" + sql);
			strBuf.append("<INV_PDA_SEARCH_RESULT>");

			strBuf.append("<STATUS>");
			if (result.getCount() == 1) {
				strBuf.append(0);
			} else {
				strBuf.append(5);
			}
			strBuf.append("</STATUS>");

			strBuf.append("<COUNT>");
			strBuf.append(result.getCount());
			strBuf.append("</COUNT>");

			strBuf.append("<PACKM>");
			strBuf.append(resultDesc.getValue("PACK_DESC", 0));
			strBuf.append("</PACKM>");

			for (int i = 0; i < result.getCount(); i++) {
				/** 名称 */
				String packDesc = result.getValue("INV_DESC", i);
				/** 数量 */
				String qty = result.getValue("QTY", i);
				/** 单位 */
				String unit = result.getValue("UNIT_DESC", i);
				/** 术前数量 */
				String bfQry = result.getValue("BF_OPE_QTY", i);
				/** 关前数量 */
				String bfCloseQry = result.getValue("BF_CLOSE_QTY", i);

				strBuf.append("<RESULT" + i + ">");
				strBuf.append("<INV_DESC" + i + ">");
				strBuf.append(packDesc);
				strBuf.append("</INV_DESC" + i + ">");

				strBuf.append("<QTY" + i + ">");
				strBuf.append(qty);
				strBuf.append("</QTY" + i + ">");

				strBuf.append("<UNIT_DESC" + i + ">");
				strBuf.append(unit);
				strBuf.append("</UNIT_DESC" + i + ">");

				strBuf.append("<BF_OPE_QTY" + i + ">");
				strBuf.append(bfQry);
				strBuf.append("</BF_OPE_QTY" + i + ">");

				strBuf.append("<BF_CLOSE_QTY" + i + ">");
				strBuf.append(bfCloseQry);
				strBuf.append("</BF_CLOSE_QTY" + i + ">");

				strBuf.append("</RESULT" + i + ">");
			}

			strBuf.append("</INV_PDA_SEARCH_RESULT>");
		} else if ("术后".equals(statues)) {
			String sql = " SELECT D.INV_CHN_DESC AS INV_DESC,E.UNIT_CHN_DESC AS UNIT_DESC,"
					+ " C.QTY,A.BF_OPE_QTY,A.BF_CLOSE_QTY,A.AF_CLOSE_QTY"
					+ " FROM INV_SUP_OPE_CHECKD A,INV_PACKM B,INV_PACKD C,INV_BASE D,SYS_UNIT E"
					+ " WHERE A.PACK_CODE = B.PACK_CODE"
					+ " AND A.PACK_CODE = C.PACK_CODE"
					+ " AND A.INV_CODE =D.INV_CODE"
					+ " AND A.INV_CODE = C.INV_CODE "
					+ " AND C.STOCK_UNIT =E.UNIT_CODE "
					+ " AND A.BARCODE = '"
					+ bar
					+ "' "
					+
					// 必须已经审核才能查询出来
					" AND A.CHECK_USER1 IS NOT NULL "
					+ " AND A.CHECK_USER2 IS NOT NULL " + " ORDER BY A.SEQ_NO ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("---sql-(供应室)术后清点查询-" + sql);
			strBuf.append("<INV_PDA_SEARCH_RESULT>");

			strBuf.append("<STATUS>");
			if (result.getCount() == 1) {
				strBuf.append(0);
			} else {
				strBuf.append(5);
			}
			strBuf.append("</STATUS>");

			strBuf.append("<COUNT>");
			strBuf.append(result.getCount());
			strBuf.append("</COUNT>");

			strBuf.append("<PACKM>");
			strBuf.append(resultDesc.getValue("PACK_DESC", 0));
			strBuf.append("</PACKM>");

			for (int i = 0; i < result.getCount(); i++) {  
				/** 名称 */
				String packDesc = result.getValue("INV_DESC", i);
				/** 数量 */
				String qty = result.getValue("QTY", i);
				/** 单位 */
				String unit = result.getValue("UNIT_DESC", i);
				/** 术前数量 */
				String bfQry = result.getValue("BF_OPE_QTY", i);
				/** 关前数量 */
				String bfCloseQry = result.getValue("BF_CLOSE_QTY", i);
				/** 关后数量 */
				String afCloseQry = result.getValue("AF_CLOSE_QTY", i);

				strBuf.append("<RESULT" + i + ">");
				strBuf.append("<INV_DESC" + i + ">");
				strBuf.append(packDesc);
				strBuf.append("</INV_DESC" + i + ">");

				strBuf.append("<QTY" + i + ">");
				strBuf.append(qty);
				strBuf.append("</QTY" + i + ">");

				strBuf.append("<UNIT_DESC" + i + ">");
				strBuf.append(unit);
				strBuf.append("</UNIT_DESC" + i + ">");

				strBuf.append("<BF_OPE_QTY" + i + ">");
				strBuf.append(bfQry);
				strBuf.append("</BF_OPE_QTY" + i + ">");

				strBuf.append("<BF_CLOSE_QTY" + i + ">");
				strBuf.append(bfCloseQry);
				strBuf.append("</BF_CLOSE_QTY" + i + ">");

				strBuf.append("<AF_CLOSE_QTY" + i + ">");
				strBuf.append(afCloseQry);
				strBuf.append("</AF_CLOSE_QTY" + i + ">");

				strBuf.append("</RESULT" + i + ">");
			}

			strBuf.append("</INV_PDA_SEARCH_RESULT>");
		}
		//System.out.println("关前or术后:" + strBuf.toString());
		return strBuf.toString();

	}

	// c# 传回来2维list 然后 这里拆解 这样才能完全通过 再commit
	/**
	 * 术前(插入)
	 * */
	public String saveOpeCheckBf(String res, String statues) {
		// 加入一个action层以保证正确性
		TParm parm = new TParm();
		parm.setData("RES", res);
		TParm result = new TParm();
		//System.out.println("parm:" + parm);
		int ipStart = res.indexOf("<IP>");
		int ipEnd = res.indexOf("</IP>");     
		String ip = res.substring(ipStart + 4, ipEnd);
		TSocket socket = new TSocket(ip, 8080, "web");
		result = TIOM_AppServer.executeAction(socket,
				"action.bil.SPCINVRecordAction", "onInsertInvOpeCheck", parm);
		if (result.getErrCode() < 0) {
			return "false";
		}
		return "true";

	}

	/**
	 * 关前(更新) 术后(更新) 保存
	 * */
	public String saveOpeCheckOth(String res, String statues) {
		// 加入一个action层以保证正确性
		TParm parm = new TParm();
		parm.setData("RES", res);
		parm.setData("STA", statues);
		TParm result = new TParm();
		//System.out.println("parm:" + parm);		
		int ipStart = res.indexOf("<IP>");
		int ipEnd = res.indexOf("</IP>");     
		String ip = res.substring(ipStart + 4, ipEnd);
		TSocket socket = new TSocket(ip, 8080, "web");
		result = TIOM_AppServer.executeAction(socket,
				"action.bil.SPCINVRecordAction", "onUpdateInvOpeCheck", parm);
		if (result.getErrCode() < 0) {
			return "false";
		}
		return "true";

	}

	/**
	 * 关前(更新) 术后(更新) 保存
	 * */
	public String saveCheckUser(String bar, String checkuser1, String checkuser2,String ip) {
		// 加入一个action层以保证正确性
		TParm parm = new TParm();
		parm.setData("BAR", bar);
		parm.setData("USER1", checkuser1);
		parm.setData("USER2", checkuser2);
		TParm result = new TParm();
		//System.out.println("parm:" + parm);
		TSocket socket = new TSocket(ip, 8080, "web");
		result = TIOM_AppServer.executeAction(socket,
				"action.bil.SPCINVRecordAction", "onUpdateCheckUser", parm);
		if (result.getErrCode() < 0) {
			return "false";
		}
		return "true";

	}

	/**
	 * 关胸 更新 ope_iproom 对应的 OPBOOK_SEQ 即解除绑定动作
	 * */
	public String updateRoom(String room,String ip) {    
		// 加入一个action层以保证正确性
		TParm parm = new TParm();               
		parm.setData("ROOM", room);   
		TParm result = new TParm();    
		//System.out.println("parm:" + parm);
		TSocket socket = new TSocket(ip, 8080, "web"); 
		result = TIOM_AppServer.executeAction(socket,        
				"action.bil.SPCINVRecordAction", "onUpdateRoom", parm);
		if (result.getErrCode() < 0) {
			return "false";
		}  
		return "true";

	}

	
	/**
	 * (手术室)查询病患信息
	 * */

	public String queryPat(String room) {
		// 加入一个action层以保证正确性  
		TParm result = new TParm();  
		StringBuffer strBuf = new StringBuffer();      
			String sql = " SELECT B.CASE_NO,B.MR_NO,C.PAT_NAME,C.SEX_CODE,D.CHN_DESC,C.BIRTH_DATE,F.DEPT_CHN_DESC "+
                         " FROM OPE_IPROOM A, OPE_OPBOOK B,SYS_PATINFO C,SYS_DICTIONARY D,ADM_INP E,SYS_DEPT F   "+
                         //--,SYS_DEPT 
                         " WHERE A.ROOM_NO = '"+room+"' AND A.OPBOOK_SEQ = B.OPBOOK_SEQ "+  
                         " AND B.MR_NO = C.MR_NO  AND D.GROUP_ID = 'SYS_SEX' "+  
                         " AND C.SEX_CODE = D.ID AND B.MR_NO = E.MR_NO AND E.DEPT_CODE = F.DEPT_CODE ";    
			result = new TParm(TJDODBTool.getInstance().select(sql));    
			//System.out.println("---sql-(手术室)查询病患信息-" + sql);  
			strBuf.append("<PAT_SEARCH_RESULT>");  
  
			strBuf.append("<STATUS>");  
			if (result.getCount() == 1) {
				strBuf.append(0);  
			} else {    
				strBuf.append(5);
			}
			strBuf.append("</STATUS>");
			  
			strBuf.append("<CASENO>");
			strBuf.append(result.getValue("CASE_NO", 0));
			strBuf.append("</CASENO>");

			strBuf.append("<MRNO>");
			strBuf.append(result.getValue("MR_NO", 0));
			strBuf.append("</MRNO>");
			
			strBuf.append("<PATNAME>");
			strBuf.append(result.getValue("PAT_NAME", 0));      
			strBuf.append("</PATNAME>");
			
			strBuf.append("<CHNDESC>");
			strBuf.append(result.getValue("CHN_DESC", 0));
			strBuf.append("</CHNDESC>");
			  
			strBuf.append("<BIRTHDATE>");      
			strBuf.append(result.getValue("BIRTH_DATE", 0));
			strBuf.append("</BIRTHDATE>");
			
			strBuf.append("<DEPTCHNDESC>");
			strBuf.append(result.getValue("DEPT_CHN_DESC", 0));
			strBuf.append("</DEPTCHNDESC>");

			
			strBuf.append("</PAT_SEARCH_RESULT>");
		
		return strBuf.toString();


	}
}
