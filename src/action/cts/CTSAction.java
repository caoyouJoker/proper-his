package action.cts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.cts.CTSTool;
//import jdo.pes.PESTool;

import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: CTS
 * </p>
 * 
 * <p>
 * Description:CTS
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhangp 20121010
 * @version 2.0
 */
public class CTSAction extends TAction {
	public CTSAction() {
	}
	
	public TParm insertCTSANTLIST(TParm parm){
		TParm result = new TParm();
		result = CTSTool.getInstance().insertCTSANTLIST(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	
	public TParm updateCTSANTLIST(TParm parm){
		TParm result = new TParm();
		result = CTSTool.getInstance().updateCTSANTLIST(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	
	public TParm deleteCTSANTLIST(TParm parm){

		TParm result = new TParm();
		TConnection conn = getConnection();
		int count = parm.getCount("ANT_ID");
		for (int i = 0; i < count; i++) {
			if (parm.getValue("DEL", i).equals("Y")) {
				result = CTSTool.getInstance().deleteCTSANTLIST(
						parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}
		conn.commit();
		conn.close();
		return result;
		
	}
	
	
	public TParm insertCTSANT(TParm parm){
		TParm result = new TParm();
		result = CTSTool.getInstance().insertCTSANT(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	
	public TParm updateCTSANT(TParm parm){
		TParm result = new TParm();
		result = CTSTool.getInstance().updateCTSANT(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	
	public TParm deleteCTSANT(TParm parm){

		TParm result = new TParm();
		TConnection conn = getConnection();
		int count = parm.getCount("ANT_ID");
		for (int i = 0; i < count; i++) {
			if (parm.getValue("DEL", i).equals("Y")) {
				result = CTSTool.getInstance().deleteCTSANT(
						parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}
		conn.commit();
		conn.close();
		return result;
		
	}

	public TParm saveWash(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		TParm noList = new TParm();
		TParm tmp = parm.getParm("WASH");
		
		List<TParm> list = new ArrayList<TParm>();
		//如果分人，则分成本中心保存多个洗衣单，否则，直接保存为一个洗衣单
		if(tmp.getValue("PAT_FLG", 0).equals("Y")){
			getWashGroup(tmp, list);
		}else{
			tmp.setData("OPT_USER", parm.getValue("OPT_USER"));
			tmp.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			tmp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			tmp.setData("START_DATE", parm.getTimestamp("START_DATE"));
			tmp.setData("END_DATE", parm.getTimestamp("END_DATE"));
			
			tmp.setData("STATION", parm.getValue("STATION_CODE"));//huangtt 成本中心
			tmp.setData("TURN", parm.getValue("TURN_POINT"));
			
//			insertMD(tmp, connection);
			//huangtt start 
			result = insertMD(tmp, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			noList.addData("WASH_NO", result.getValue("WASH_NO"));
			
			//huangtt end
		}
		
		
//		System.out.println("list.size====="+list.size());
		for (int i = 0; i < list.size(); i++) {
			TParm insertParm = list.get(i);
			insertParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertParm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			insertParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertParm.setData("START_DATE", parm.getTimestamp("START_DATE"));
			insertParm.setData("END_DATE", parm.getTimestamp("END_DATE"));
			insertParm.setData("STATION",  parm.getValue("STATION_CODE"));//huangtt
			insertParm.setData("TURN", parm.getValue("TURN_POINT"));
//			System.out.println(parm.getTimestamp("START_DATE"));
			result = insertMD(insertParm, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			noList.addData("WASH_NO", result.getValue("WASH_NO"));
		}
		connection.commit();
		connection.close();
		result.setData("WASH_NO", noList.getData());
		return result;
	}

	private TParm insertMD(TParm parm, TConnection connection) {
		System.out.println("insertMD=="+parm);
		System.out.println("flg=="+parm.getValue("PAT_FLG", 0));
		int qty = parm.getCount("CLOTH_NO");
		TParm washM = new TParm();
		String wash_no = CTSTool.getInstance().getWashNo();
		washM.setData("WASH_NO", wash_no);
		washM.setData("DEPT_CODE",
				parm.getValue("DEPT_CODE", 0) == null ? new TNull(String.class)
						: parm.getValue("DEPT_CODE", 0));
		//huangtt  start
//		washM.setData("STATION_CODE",
//				parm.getValue("STATION_CODE", 0) == null ? new TNull(
//						String.class) : parm.getValue("STATION_CODE", 0));
		
//		washM.setData("TURN_POINT",
//				parm.getValue("TURN_POINT", 0) == null ? new TNull(
//						String.class) : parm.getValue("TURN_POINT", 0));
		
//		washM.setData("TURN_POINT",
//				parm.getValue("TURN") == null ? new TNull(
//				String.class) : parm.getValue("TURN"));
		
		if(parm.getValue("PAT_FLG", 0).equals("Y")){
			washM.setData("STATION_CODE",
					parm.getValue("STATION_CODE", 0) == null ? new TNull(
							String.class) : parm.getValue("STATION_CODE", 0));
			
			washM.setData("TURN_POINT",
					parm.getValue("TURN_POINT", 0) == null ? new TNull(
							String.class) : parm.getValue("TURN_POINT", 0));
			
		}else{
//			washM.setData("STATION_CODE",
//					parm.getValue("STATION") == null ? new TNull(
//					String.class) : parm.getValue("STATION"));
			washM.setData("STATION_CODE",
					parm.getValue("STATION_CODE", 0) == null ? new TNull(
							String.class) : parm.getValue("STATION_CODE", 0));
			
			washM.setData("TURN_POINT",
					parm.getValue("TURN") == null ? new TNull(
					String.class) : parm.getValue("TURN"));
		}
		
		
		//huangtt end
		washM.setData("QTY", qty);
		washM.setData("START_DATE",
				parm.getTimestamp("START_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("START_DATE"));
		// washM.setData("END_DATE",
		// parm.getTimestamp("END_DATE") == null ? new TNull(
		// Timestamp.class) : parm.getTimestamp("END_DATE"));
		washM.setData("PAT_FLG",
				parm.getValue("PAT_FLG", 0) == null ? new TNull(String.class)
						: parm.getValue("PAT_FLG", 0));
		//分拣时无洗衣单保存时
		if(parm.getValue("END_DATE").length()>0){
			washM.setData("STATE", 1);
		}else{
			washM.setData("STATE", 2);
		}
		washM.setData("WASH_CODE",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		washM.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		washM.setData("OPT_DATE",
				parm.getTimestamp("OPT_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("OPT_DATE"));
		washM.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		
		
		
		
		
		//分拣时无洗衣单保存时
		if(parm.getValue("END_DATE").length()>0){
			washM.setData("NEW_FLG", "Y");
		}else{
			washM.setData("NEW_FLG", "N");
		}
		TParm result = CTSTool.getInstance().insertCTSWASHM(washM, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		TParm washD;
		for (int i = 0; i < qty; i++) {
			washD = new TParm();
			washD.setData("WASH_NO", wash_no);
			washD.setData("SEQ_NO", i + 1);
			
			washD.setData("TURN_POINT",
					parm.getValue("TURN_POINT",i) == null ? new TNull(
					String.class) : parm.getValue("TURN_POINT",i));
			
			washD.setData("CLOTH_NO",
					parm.getValue("CLOTH_NO", i) == null ? new TNull(
							String.class) : parm.getValue("CLOTH_NO", i));
			washD.setData("OWNER",
					parm.getValue("OWNER", i) == null ? new TNull(String.class)
							: parm.getValue("OWNER", i));
			washD.setData("PAT_FLG",
					parm.getValue("PAT_FLG", i) == null ? new TNull(
							String.class) : parm.getValue("PAT_FLG", i));
			washD.setData("OPT_USER",
					parm.getValue("OPT_USER") == null ? new TNull(String.class)
							: parm.getValue("OPT_USER"));
			washD.setData("OPT_DATE",
					parm.getTimestamp("OPT_DATE") == null ? new TNull(
							Timestamp.class) : parm.getTimestamp("OPT_DATE"));
			washD.setData("OPT_TERM",
					parm.getValue("OPT_TERM") == null ? new TNull(String.class)
							: parm.getValue("OPT_TERM"));
			//分拣时无洗衣单保存时
			if(parm.getValue("END_DATE").length()>0){
				washD.setData("OUT_FLG", "Y");
			}else{
				washD.setData("OUT_FLG", "N");
			}
			washD.setData("NEW_FLG",
					parm.getValue("NEW_FLG", i) == null ? new TNull(String.class)
							: parm.getValue("NEW_FLG", i));
			result = CTSTool.getInstance().insertCTSWASHD(washD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		result.setData("WASH_NO", wash_no);
		return result;
	}

	private void getWashGroup(TParm parm, List<TParm> list) {
//		System.out.println("parm=="+parm);
		for (int i = 0; i < list.size(); i++) {
//			System.out.println(i+"==="+list.get(i));
		}
//		String DEPT_CODE = parm.getValue("DEPT_CODE", 0);
		String STATION_CODE = parm.getValue("STATION_CODE", 0);
		String TURN_POINT = parm.getValue("TURN_POINT", 0);
		TParm ss = new TParm();
		ss.addData("CLOTH_NO", parm.getValue("CLOTH_NO", 0));
		ss.addData("INV_CODE", parm.getValue("INV_CODE", 0));
		ss.addData("OWNER", parm.getValue("OWNER", 0));
//		ss.addData("DEPT_CODE", DEPT_CODE);
		ss.addData("STATION_CODE", STATION_CODE);
		ss.addData("STATE", parm.getValue("STATE", 0));
		ss.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG", 0));
		ss.addData("PAT_FLG", parm.getValue("PAT_FLG", 0));
		ss.addData("NEW_FLG", parm.getValue("NEW_FLG", 0));
		ss.addData("TURN_POINT", TURN_POINT);
		parm.removeRow(0);
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < parm.getCount("STATION_CODE"); i++) {
//			if (parm.getValue("DEPT_CODE", i).equals(DEPT_CODE)
//					&& parm.getValue("STATION_CODE", i).equals(STATION_CODE)) {
//			if (parm.getValue("STATION_CODE", i).equals(STATION_CODE)) {
			if (parm.getValue("TURN_POINT", i).equals(TURN_POINT)) {
				ss.addData("CLOTH_NO", parm.getValue("CLOTH_NO", i));
				ss.addData("INV_CODE", parm.getValue("INV_CODE", i));
				ss.addData("OWNER", parm.getValue("OWNER", i));
//				ss.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
				ss.addData("STATION_CODE", parm.getValue("STATION_CODE", i));
				ss.addData("STATE", parm.getValue("STATE", i));
				ss.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG", i));
				ss.addData("PAT_FLG", parm.getValue("PAT_FLG", i));
				ss.addData("NEW_FLG", parm.getValue("NEW_FLG", i));
				ss.addData("TURN_POINT", parm.getValue("TURN_POINT", i));
				l.add(i);
			}
		}
		for (int i = 0; i < l.size(); i++) {
//			System.out.println(i);
//			System.out.println("Integer.valueOf('' + l.get(i)) - i=="+(Integer.valueOf("" + l.get(i)) - i));
//			System.out.println("for parm=="+parm);
			parm.removeRow(Integer.valueOf("" + l.get(i)) - i);
		}
		list.add(ss);
		if (parm.getCount("STATION_CODE") > 0) {
			getWashGroup(parm, list);
		}
	}

	public TParm updateMD(TParm parm) {
		TConnection connection = getConnection();
		TParm washM = parm.getParm("WASHM");
		TParm washD = parm.getParm("WASHD");
//		System.out.println("washD===="+washD);
//		System.out.println("washM===="+washM);
		TParm updateM = new TParm();
		updateM.setData("WASH_NO", washM.getData("WASH_NO"));
		updateM.setData("END_DATE",
				parm.getTimestamp("END_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("END_DATE"));
		updateM.setData("STATE", 1);
		updateM.setData("OUT_QTY",
				washM.getData("OUT_QTY") == null ? new TNull(
						Integer.class) : washM.getData("OUT_QTY"));
		TParm result = new TParm();
		//如果不是按保存键，则不更新M表，否则，更新M表
//		if(!updateM.getValue("TMP_FLG").equals("Y")){
//			result = CTSTool.getInstance().updateWASHM(updateM, connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				connection.close();
//				return result;
//			}
//		}
		TParm updateD;

		for (int i = 0; i < washD.getCount("CLOTH_NO"); i++) {
//			System.out.println("washD===="+washD);
			if(washD.getValue("NEW_FLG", i).equals("Y")){
				updateD = new TParm();
				
				if(washM.getValue("TURN_POINT").equals("")){
					updateD.setData("TURN_POINT", "");
//					System.out.println("washM1===="+washM.getValue("TURN_POINT"));
				}else{
					updateD.setData("TURN_POINT", washM.getData("TURN_POINT"));
//					System.out.println("washM2===="+washM.getData("TURN_POINT"));
				}
				
				
				
				updateD.setData("WASH_NO", washM.getData("WASH_NO"));
				updateD.setData("SEQ_NO", washD.getData("SEQ_NO", i));
				updateD.setData("CLOTH_NO",
						washD.getValue("CLOTH_NO", i) == null ? new TNull(
								String.class) : washD.getValue("CLOTH_NO", i));
				
				updateD.setData("OWNER",
						washD.getValue("OWNER", i) == null ? new TNull(String.class)
								: washD.getValue("OWNER", i));
				updateD.setData("PAT_FLG",
						washD.getValue("PAT_FLG", i) == null ? new TNull(
								String.class) : washD.getValue("PAT_FLG", i));
				updateD.setData("OPT_USER",
						washD.getValue("OPT_USER", i) == null ? new TNull(String.class)
								: washD.getValue("OPT_USER", i));
				updateD.setData("OPT_DATE",
						washD.getTimestamp("OPT_DATE", i) == null ? new TNull(
								Timestamp.class) : washD.getTimestamp("OPT_DATE", i));
				updateD.setData("OPT_TERM",
						washD.getValue("OPT_TERM", i) == null ? new TNull(String.class)
								: washD.getValue("OPT_TERM", i));
				updateD.setData("OUT_FLG", "Y");
				updateD.setData("NEW_FLG",
						washD.getValue("NEW_FLG", i) == null ? new TNull(String.class)
								: washD.getValue("NEW_FLG", i));
				result = CTSTool.getInstance().insertCTSOUTD(updateD, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}

				
				
			}else{
//				updateD = new TParm();
//				updateD.setData("WASH_NO", washM.getData("WASH_NO"));
//				updateD.setData("SEQ_NO", washD.getData("SEQ_NO", i));
//				updateD.setData("CLOTH_NO", washD.getData("CLOTH_NO", i));
//				updateD.setData("OUT_FLG", washD.getData("OUT_FLG", i));
//				result = CTSTool.getInstance().updateWASHD(updateD, connection);
//				if (result.getErrCode() < 0) {
//					connection.rollback();
//					connection.close();
//					return result;
//				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	public TParm saveWashOut(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		TParm tmp = parm.getParm("WASH");
		
		List<TParm> list = new ArrayList<TParm>();
		//如果分人，则分成本中心保存多个洗衣单，否则，直接保存为一个洗衣单
		if(tmp.getValue("PAT_FLG", 0).equals("Y")){
			getWashGroup(tmp, list);
		}else{
			tmp.setData("OPT_USER", parm.getValue("OPT_USER"));
			tmp.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			tmp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			tmp.setData("END_DATE", parm.getTimestamp("END_DATE"));
			tmp.setData("TURN_POINT", parm.getValue("TURN_POINT"));
			result=insertOutMD(tmp, connection);
		}
		
		TParm noList = new TParm();
		for (int i = 0; i < list.size(); i++) {
			TParm insertParm = list.get(i);
			insertParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertParm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			insertParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertParm.setData("END_DATE", parm.getTimestamp("END_DATE"));
			insertParm.setData("TURN_POINT", parm.getValue("TURN_POINT"));
			result = insertOutMD(insertParm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			noList.addData("WASH_NO", result.getValue("WASH_NO"));
		}
		connection.commit();
		connection.close();
		result.setData("WASH_NO", noList.getData());
		return result;
	}

	private TParm insertOutMD(TParm parm, TConnection connection) {
		int qty = parm.getCount("CLOTH_NO");
		TParm washM = new TParm();
		String wash_no = CTSTool.getInstance().getWashOutNo();
		washM.setData("WASH_NO", wash_no);
		washM.setData("STATION_CODE",
				parm.getValue("STATION_CODE", 0) == null ? new TNull(
						String.class) : parm.getValue("STATION_CODE", 0));
		washM.setData("QTY", qty);
		washM.setData("START_DATE",
				parm.getTimestamp("START_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("START_DATE"));
		// washM.setData("END_DATE",
		// parm.getTimestamp("END_DATE") == null ? new TNull(
		// Timestamp.class) : parm.getTimestamp("END_DATE"));
		washM.setData("PAT_FLG",
				parm.getValue("PAT_FLG", 0) == null ? new TNull(String.class)
						: parm.getValue("PAT_FLG", 0));
		//分拣时无洗衣单保存时
		if(parm.getValue("END_DATE").length()>0){
			washM.setData("STATE", 1);
		}else{
			washM.setData("STATE", 2);
		}
		washM.setData("WASH_CODE",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		washM.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		washM.setData("OPT_DATE",
				parm.getTimestamp("OPT_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("OPT_DATE"));
		washM.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		
		washM.setData("TURN_POINT",
				parm.getValue("TURN_POINT", 0) == null ? new TNull(String.class)
						: parm.getValue("TURN_POINT"));
		
		TParm result = CTSTool.getInstance().insertCTSOUTM(washM, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		TParm washD;
		for (int i = 0; i < qty; i++) {
			washD = new TParm();
			washD.setData("WASH_NO", wash_no);
			washD.setData("SEQ_NO", i + 1);
			washD.setData("CLOTH_NO",
					parm.getValue("CLOTH_NO", i) == null ? new TNull(
							String.class) : parm.getValue("CLOTH_NO", i));
			washD.setData("OWNER",
					parm.getValue("OWNER", i) == null ? new TNull(String.class)
							: parm.getValue("OWNER", i));
			washD.setData("PAT_FLG",
					parm.getValue("PAT_FLG", i) == null ? new TNull(
							String.class) : parm.getValue("PAT_FLG", i));
			washD.setData("OPT_USER",
					parm.getValue("OPT_USER") == null ? new TNull(String.class)
							: parm.getValue("OPT_USER"));
			washD.setData("OPT_DATE",
					parm.getTimestamp("OPT_DATE") == null ? new TNull(
							Timestamp.class) : parm.getTimestamp("OPT_DATE"));
			washD.setData("OPT_TERM",
					parm.getValue("OPT_TERM") == null ? new TNull(String.class)
							: parm.getValue("OPT_TERM"));
			washD.setData("NEW_FLG",
					parm.getValue("NEW_FLG", i) == null ? new TNull(String.class)
							: parm.getValue("NEW_FLG", i));
			
			washD.setData("TURN_POINT",
					parm.getValue("TURN_POINT", i) == null ? new TNull(String.class)
							: parm.getValue("TURN_POINT"));
			
			result = CTSTool.getInstance().insertCTSOUTD(washD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		result.setData("WASH_NO", wash_no);
		return result;
	}
	/**
	 * 更新入库单和出库单中的出库单号和入库单号
	 * @param washNo
	 * @param connection
	 * @return
	 */	public TParm updateInOutWashNo(TParm parm){
		String washNo = parm.getValue("WASH_NO");
//		TConnection connection=getConnection();
		TParm washParm = CTSTool.getInstance().selectInOutWashNo(washNo);
		TParm result=new TParm();
		List<String> wash_nos = new ArrayList<String>();
		for(int i=0;i<washParm.getCount();i++){
			TParm updateInWashNoParm = new TParm();
			updateInWashNoParm.setData("CLOTH_NO", washParm.getValue("CLOTH_NO", i));
			updateInWashNoParm.setData("WASH_NO", washParm.getValue("OUT_WASH_NO", i));
			updateInWashNoParm.setData("IN_WASH_NO", washParm.getValue("IN_WASH_NO", i));
			result = CTSTool.getInstance().updateInWashNo(updateInWashNoParm);
			if(result.getErrCode()<0){
//				connection.rollback();
//				connection.close();
				return result;
			}
			TParm updateOutWashNoParm = new TParm();
			updateOutWashNoParm.setData("CLOTH_NO", washParm.getValue("CLOTH_NO", i));
			updateOutWashNoParm.setData("WASH_NO", washParm.getValue("IN_WASH_NO", i));
			updateOutWashNoParm.setData("OUT_WASH_NO", washParm.getValue("OUT_WASH_NO", i));
			result = CTSTool.getInstance().updateOutWashNo(updateOutWashNoParm);
			
			if(result.getErrCode()<0){
//				connection.rollback();
//				connection.close();
				return result;
			}
			//更新cts_washm的out_qty
			result = CTSTool.getInstance().updateWashMOutQty(washParm.getValue("IN_WASH_NO", i));
			if(result.getErrCode()<0){
//				connection.rollback();
//				connection.close();
				return result;
			}
			
			//更新cts_washm的end_date
			result = CTSTool.getInstance().updateWashMEndDate(washParm.getValue("IN_WASH_NO", i));
			if(result.getErrCode()<0){
//				connection.rollback();
//				connection.close();
				return result;
			}
			
		}
		
		return result;
	}

	public TParm deleteWASHMD(TParm parm){
		TParm result = new TParm();
		TConnection conn = getConnection();
		int count = parm.getCount("WASH_NO");
		for (int i = 0; i < count; i++) {
			
				result = CTSTool.getInstance().deleteWASHM(
						parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				result = CTSTool.getInstance().deleteWASHD(
						parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}

		}
		conn.commit();
		conn.close();
		return result;
		
	}
	
	public TParm deleteOUTMD(TParm parm){
		TParm result = new TParm();
		TConnection conn = getConnection();
		int count = parm.getCount("WASH_NO");
		for (int i = 0; i < count; i++) {
			
				result = CTSTool.getInstance().deleteOUTM(
						parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				result = CTSTool.getInstance().deleteOUTD(
						parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}

		}
		conn.commit();
		conn.close();
		return result;
		
	}
	
	/**
	 * 往CTSOUTM和CTSOUTD中插入数据
	 * @param parm
	 * @return
	 */
	public TParm insertCTSOUTMD(TParm parm){
		TParm result = new TParm();
		TConnection conn = getConnection();
		TParm CtsoutM = parm.getParm("CtsoutM");
		TParm CtsoutD = parm.getParm("CtsoutD");
		for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
			result = CTSTool.getInstance().insertCTSOUTM(CtsoutM.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		for(int i=0;i<CtsoutD.getCount("WASH_NO");i++){
			result = CTSTool.getInstance().insertCTSOUTD(CtsoutD.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		return result;
		
	}
	
	/**
	 * 单独录入rfid时用的插入
	 * @param parm
	 * @return
	 */
	public TParm insertCTSWashMD(TParm parm){
		TParm result = new TParm();
		TConnection conn = getConnection();
		TParm washM = parm.getParm("washM");
		TParm washD = parm.getParm("washD");
		TParm parmDD = parm.getParm("parmDD");
		result = CTSTool.getInstance().insertCTSWASHM(washM, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = CTSTool.getInstance().insertCTSWASHD(washD, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = CTSTool.getInstance().updateStockDD1(parmDD, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
		
	}
	
	public TParm saveCtsRfidReplce(TParm parm){
		TParm result = new TParm();
		TConnection conn = getConnection();
		if(!parm.getBoolean("VALID_FLG")){
			result = CTSTool.getInstance().updateCtsReplace(parm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		result = CTSTool.getInstance().InsertCtsReplace(parm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
}
