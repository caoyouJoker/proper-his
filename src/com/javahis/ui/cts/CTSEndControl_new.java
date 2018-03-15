package com.javahis.ui.cts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.cts.CTSTool;
import jdo.sys.Operator;

import com.alien.enterpriseRFID.notify.Message;
import com.alien.enterpriseRFID.tags.Tag;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

import com.javahis.device.Ring;
import com.javahis.device.Uitltool;
import com.javahis.util.AlienRFIDUtil;


/**
 * 
 * <p>
 * Title:洗衣分拣
 * </p>
 * 
 * <p>
 * Description:洗衣分拣
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * implements MessageListener
 * @author zhangp 2012.8.2
 * @version 1.0
 * 
 */

public class CTSEndControl_new extends TControl {
	private static TTable tableM;
	private static TTable tableD;
	private static int rowM = -1;
	private static List<String> cloth_nos = new ArrayList<String>();
	private TParm rfidConfig;
	TParm tparm;
	private String rfid_ip = "";

	String newWashNo="";
	Ring successRing;
	Ring errorRing;
	Ring repeatRing;
	
	AlienRFIDUtil alienRFIDUtil = null;
	
	private TParm CtsoutM;   //出库主表数据
	private TParm CtsoutD;   //出库细表数据
	
	private boolean disconn = true;   //在清空中是否断开连接 和清空队列
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		CtsoutM = new TParm();
		CtsoutD = new TParm();
		successRing = new Ring(Ring.SUCCESS);
		errorRing = new Ring(Ring.ERROR);
		repeatRing = new Ring(Ring.REPEAT);
		tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|tButton_1|setEnabled", false);
		rfidConfig = CTSTool.getInstance().getRfidConfig(Operator.getIP());
		if(rfidConfig.getErrCode()<0){
			messageBox("读写器连接失败");
			this.callFunction("UI|START|setEnabled", false);
			this.callFunction("UI|END|setEnabled", false);
		}
		rfid_ip = rfidConfig.getValue("RFID_IP");
	}


	public void onStart() {
		Timestamp date = StringTool.getTimestamp(new Date());
		TTextFormat START_DATE = (TTextFormat) getComponent("START_DATE"); 
		START_DATE.setValue(date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		setValue("START_DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		callFunction("UI|START|setEnabled", false);
		callFunction("UI|END|setEnabled", true);
		grabFocus("CLOTH_NO");
		new Thread(){
			
			public void run() {
				try {
					alienRFIDUtil = new AlienRFIDUtil(rfid_ip, 23) {
						
						@Override
						public void messageReceived(Message arg0) {
							// TODO Auto-generated method stub
							if (arg0.getTagCount() == 0) {
								System.out.println("(No Tags)");
							} else {
								for (int i = 0; i < arg0.getTagCount(); i++) {
									Tag tag = arg0.getTag(i);
									String rfid = tag.getTagID();
									rfid = rfid.replace(" ", "");
									System.out.println("before decode--"+rfid);
									try {
										rfid = Uitltool.decode(rfid);
										System.out.println("after decode--"+rfid);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									setValue("CLOTH_NO", rfid);
									onEnter();
								}
							}
						}
					};
					alienRFIDUtil.startMessageListener(4000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.start();
	}
	
	public void onEnd() {
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 19).replaceAll(
				"-", "/"));
		alienRFIDUtil.setOpen(false);
			rowM = tableM.getSelectedRow();
		onSave("");
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|tButton_1|setEnabled", true);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		tableM.acceptText();
		this.rowM = tableM.getSelectedRow();
		int selRow=0;   //CtsoutM中的与页面wash_no对应的列
		if(CtsoutM != null){
			for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
				if(CtsoutM.getValue("WASH_NO", i).equals(getValueString("WASH_NO"))){
					selRow = i;
				}
			}
		}
		tableM.acceptText();
		TParm tableParm = tableM.getParmValue();
		if (tableParm == null) {
			tableParm = new TParm();
		}
		int row = 0;
		boolean flg = false;
		for (int i = 0; i < tableParm.getCount("WASH_NO"); i++) {
			if (CtsoutM.getValue("WASH_NO", selRow).equals(
					tableParm.getValue("WASH_NO", i))) {
				row = i;
				flg = true;
			}
		}
		if (!flg) {
			tableParm.addData("WASH_NO", CtsoutM.getData("WASH_NO", selRow));
			tableParm.addData("DEPT_CODE", CtsoutM.getData("DEPT_CODE", selRow));
			tableParm
					.addData("STATION_CODE", CtsoutM.getData("STATION_CODE", selRow));
			tableParm.addData("QTY", CtsoutM.getData("QTY", selRow));
			tableParm.addData("START_DATE", CtsoutM.getData("START_DATE", selRow));
			tableParm.addData("END_DATE", CtsoutM.getData("END_DATE", selRow));
			tableParm.addData("PAT_FLG", CtsoutM.getData("PAT_FLG", selRow));
			tableParm.addData("STATE", CtsoutM.getData("STATE", selRow));
			tableParm.addData("WASH_CODE", CtsoutM.getData("WASH_CODE", selRow));
			tableParm.addData("OPT_USER", CtsoutM.getData("OPT_USER", selRow));
			tableParm.addData("OPT_DATE", CtsoutM.getData("OPT_DATE", selRow));
			tableParm.addData("OPT_TERM", CtsoutM.getData("OPT_TERM", selRow));
			tableParm.addData("TURN_POINT", CtsoutM.getData("TURN_POINT", selRow));
			row = tableParm.getCount("WASH_NO") - 1;
		}
		tableM.setParmValue(tableParm);
		tableM.setSelectedRow(row);
		onClickTableM();
	}

	/**
	 * table1点击事件
	 */
	public void onClickTableM() {
		tableM.acceptText();
		if (rowM != tableM.getSelectedRow()) {
			onSave("");
		}
		tableM.acceptText();
		String washNO = tableM.getParmValue().getValue("WASH_NO",tableM.getSelectedRow());
		TParm result = new TParm();
		for(int i=0;i<CtsoutD.getCount("CLOTH_NO");i++){
			if(CtsoutD.getValue("WASH_NO", i).equals(washNO)){
				result.addData("WASH_NO", CtsoutD.getValue("WASH_NO", i));
				result.addData("CLOTH_NO", CtsoutD.getValue("CLOTH_NO", i));
				result.addData("OWNER", CtsoutD.getValue("OWNER", i));
				result.addData("OWNER_CODE", CtsoutD.getValue("OWNER_CODE", i));
				result.addData("PAT_FLG", CtsoutD.getValue("PAT_FLG", i));
				result.addData("OPT_USER", CtsoutD.getValue("OPT_USER", i));
				result.addData("OPT_DATE", CtsoutD.getValue("OPT_DATE", i));
				result.addData("OPT_TERM", CtsoutD.getValue("OPT_TERM", i));
				result.addData("OUT_FLG", "Y");
			}
		}
		for (int i = 0; i < result.getCount("CLOTH_NO"); i++) {
			result.setData("NEW_FLG", i, "N");
		}
		tableD.setParmValue(result);
	}
	
	/**
	 * 查询outm 与outd 得到出库单号
	 */
	public TParm selectOUTMD(TParm parm){
		TParm result = new TParm();
		String clothNo=parm.getValue("CLOTH_NO");
		if(CtsoutD != null){
			for(int i=0 ; i < CtsoutD.getCount("CLOTH_NO") ; i++){
				if(CtsoutD.getValue("CLOTH_NO", i).equals(clothNo)){
					result.addData("WASH_NO", CtsoutD.getValue("WASH_NO",i ));
					return result;
				}
				
			}
		}else{
			result.addData("WASH_NO", "");
		}
		
		return result;
		
	}
	
	/**
	 * 取得在分拣时新增的洗衣单
	 * @return
	 */
	public TParm getNewWash(String station_code, String pat_flg, String turn_point){
		TParm result = new TParm();
		if(CtsoutM != null){
			for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
				if(CtsoutM.getValue("TURN_POINT", i).equals(turn_point) && CtsoutM.getValue("PAT_FLG", i).equals(pat_flg)){
					result.addData("WASH_NO", CtsoutM.getValue("WASH_NO", i));
					result.addData("STATION_CODE", CtsoutM.getValue("STATION_CODE", i));
					result.addData("END_DATE", CtsoutM.getValue("END_DATE", i));
					result.addData("PAT_FLG", CtsoutM.getValue("PAT_FLG", i));
					result.addData("STATE", CtsoutM.getValue("STATE", i));
					result.addData("WASH_CODE", CtsoutM.getValue("WASH_CODE", i));
					result.addData("OPT_USER", CtsoutM.getValue("OPT_USER", i));
					result.addData("OPT_DATE", CtsoutM.getValue("OPT_DATE", i));
					result.addData("OPT_TERM", CtsoutM.getValue("OPT_TERM", i));
					return result;
				}
			}
		}
		
		return null;
		
	}

	public void onEnter() {
		TParm parm = new TParm();
		parm.setData("CLOTH_NO", getValue("CLOTH_NO"));
		TParm result = CTSTool.getInstance().selectCloth(parm);
		if (result.getCount() > 0) {
			if(!cloth_nos.contains(result.getValue("CLOTH_NO", 0))){
				cloth_nos.add(result.getValue("CLOTH_NO", 0));
				successRing.play();
			}else{
				repeatRing.play();
			}
			String station = getValueString("STATION_CODE");
			String turnPoint = getValueString("TURN_POINT");
			
			setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
			setValue("OWNER", result.getValue("OWNER", 0));
			setValue("OWNER_CODE", result.getValue("OWNER_CODE", 0));
			setValue("TURN_POINT", result.getValue("TURN_POINT", 0));
			
			if(result.getValue("PAT_FLG", 0).equals("Y")){
				setValue("PAT_FLGT", "√");
			}else{
				setValue("PAT_FLGT", "");
			}
			tableD.acceptText();
			TParm td = tableD.getParmValue();
			TParm result1 = selectOUTMD(parm);
			if (getValue("WASH_NO").equals(result1.getValue("WASH_NO", 0))&&getValueString("WASH_NO").length()>0) {
				tableD.acceptText();
				td = tableD.getParmValue();
				if (td == null) {
					td = new TParm();
				}
				for (int i = 0; i < td.getCount("CLOTH_NO"); i++) {
					if (td.getValue("CLOTH_NO", i).equals(
							result.getValue("CLOTH_NO", 0))) {
						td.setData("OUT_FLG", i, "Y");
					}
				}
				tableD.setParmValue(td);
			} else {
				setValue("WASH_NO", result1.getValue("WASH_NO", 0));
				//如果洗衣单不为空，则查询原有洗衣单，否则，新增洗衣单
				if (!getValue("WASH_NO").equals("")) {
					onQuery();
					
				}else{
					TParm newWash = new TParm();
					newWash = getNewWash(result.getValue("STATION_CODE", 0), result.getValue("PAT_FLG", 0), result.getValue("TURN_POINT", 0));
					if(newWash != null){
						if(!turnPoint.equals(result.getValue("TURN_POINT", 0))  ){
							setValue("WASH_NO", newWash.getValue("WASH_NO", 0));
							onQuery();
						}
						tableD.acceptText();
						td = tableD.getParmValue();
						List list = (List) td.getData("CLOTH_NO");
						if(!list.contains(result.getValue("CLOTH_NO", 0))){
							td.addData("WASH_NO", newWash.getValue("WASH_NO", 0));
							td.addData("CLOTH_NO", result.getValue("CLOTH_NO", 0));
							td.addData("SEQ_NO", td.getCount("CLOTH_NO")+1);
							td.addData("OWNER", result.getValue("OWNER", 0));
							td.addData("OWNER_CODE", result.getValue("OWNER_CODE", 0));
							td.addData("PAT_FLG", result.getValue("PAT_FLG", 0));
							td.addData("OUT_FLG", "N");
							td.addData("NEW_FLG", "Y");
							td.addData("OPT_USER", Operator.getID());
							td.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
							td.addData("OPT_TERM", Operator.getIP());
							tableD.setParmValue(td);
						}
					}else{
						TParm parmNew = new TParm();
						parmNew.setData("WASH", result.getData());
						parmNew.setData("OPT_USER", Operator.getID());
						parmNew.setData("OPT_TERM", Operator.getIP());
						parmNew.setData("END_DATE", getValue("START_DATE"));
						parmNew.setData("TURN_POINT", getValue("TURN_POINT"));
						saveWashOut(parmNew);
						onEnter();
						onQuery();
					}
					
				}
				tableD.acceptText();
				td = tableD.getParmValue();
				if (td == null) {
					td = new TParm();
				}
				for (int i = 0; i < td.getCount("CLOTH_NO"); i++) {
					if (td.getValue("CLOTH_NO", i).equals(
							result.getValue("CLOTH_NO", 0))) {
						td.setData("OUT_FLG", i, "Y");
					}
				}
				tableD.setParmValue(td);
			}
		}else{
			errorRing.play();
		}
		setOutQty();
	}
	
	
	//往容器CtsoutM和CtsoutD里面添加数据
	public void saveWashOut(TParm parm){
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
			insertOutMD(tmp);
		}
		
		TParm noList = new TParm();
		for (int i = 0; i < list.size(); i++) {
			TParm insertParm = list.get(i);
			insertParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertParm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			insertParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertParm.setData("END_DATE", parm.getTimestamp("END_DATE"));
			insertParm.setData("TURN_POINT", parm.getValue("TURN_POINT"));
			insertOutMD(insertParm);

			noList.addData("WASH_NO", result.getValue("WASH_NO"));
		}	
	}
	
	private void getWashGroup(TParm parm, List<TParm> list) {
		String STATION_CODE = parm.getValue("STATION_CODE", 0);
		String TURN_POINT = parm.getValue("TURN_POINT", 0);
		TParm ss = new TParm();
		ss.addData("CLOTH_NO", parm.getValue("CLOTH_NO", 0));
		ss.addData("INV_CODE", parm.getValue("INV_CODE", 0));
		ss.addData("OWNER", parm.getValue("OWNER", 0));
		ss.addData("STATION_CODE", STATION_CODE);
		ss.addData("STATE", parm.getValue("STATE", 0));
		ss.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG", 0));
		ss.addData("PAT_FLG", parm.getValue("PAT_FLG", 0));
		ss.addData("NEW_FLG", parm.getValue("NEW_FLG", 0));
		ss.addData("TURN_POINT", TURN_POINT);
		parm.removeRow(0);
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < parm.getCount("STATION_CODE"); i++) {
			if (parm.getValue("TURN_POINT", i).equals(TURN_POINT)) {
				ss.addData("CLOTH_NO", parm.getValue("CLOTH_NO", i));
				ss.addData("INV_CODE", parm.getValue("INV_CODE", i));
				ss.addData("OWNER", parm.getValue("OWNER", i));
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
			parm.removeRow(Integer.valueOf("" + l.get(i)) - i);
		}
		list.add(ss);
		if (parm.getCount("STATION_CODE") > 0) {
			getWashGroup(parm, list);
		}
	}
	
	public void insertOutMD(TParm parm){
		int qty = parm.getCount("CLOTH_NO");
		String wash_no = CTSTool.getInstance().getWashOutNo();
		CtsoutM.addData("WASH_NO", wash_no);
		CtsoutM.addData("STATION_CODE",
				parm.getValue("STATION_CODE", 0) == null ? new TNull(
						String.class) : parm.getValue("STATION_CODE", 0));
		CtsoutM.addData("QTY", qty);
		CtsoutM.addData("START_DATE",
				parm.getTimestamp("START_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("START_DATE"));
		CtsoutM.addData("END_DATE",
		 parm.getTimestamp("END_DATE") == null ? new TNull(
		 Timestamp.class) : parm.getTimestamp("END_DATE"));
		CtsoutM.addData("PAT_FLG",
				parm.getValue("PAT_FLG", 0) == null ? new TNull(String.class)
						: parm.getValue("PAT_FLG", 0));
		CtsoutM.addData("STATE", 1);
		CtsoutM.addData("WASH_CODE",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		CtsoutM.addData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		CtsoutM.addData("OPT_DATE",
				parm.getTimestamp("OPT_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("OPT_DATE"));
		CtsoutM.addData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		
		CtsoutM.addData("TURN_POINT",
				parm.getValue("TURN_POINT", 0) == null ? new TNull(String.class)
						: parm.getValue("TURN_POINT"));
		
	
		for (int i = 0; i < qty; i++) {
			CtsoutD.addData("OWNER_CODE", getValue("OWNER_CODE"));
			CtsoutD.addData("WASH_NO", wash_no);
			CtsoutD.addData("SEQ_NO", i + 1);
			CtsoutD.addData("CLOTH_NO",
					parm.getValue("CLOTH_NO", i) == null ? new TNull(
							String.class) : parm.getValue("CLOTH_NO", i));
			CtsoutD.addData("OWNER",
					parm.getValue("OWNER", i) == null ? new TNull(String.class)
							: parm.getValue("OWNER", i));
			CtsoutD.addData("PAT_FLG",
					parm.getValue("PAT_FLG", i) == null ? new TNull(
							String.class) : parm.getValue("PAT_FLG", i));
			CtsoutD.addData("OPT_USER",
					parm.getValue("OPT_USER") == null ? new TNull(String.class)
							: parm.getValue("OPT_USER"));
			CtsoutD.addData("OPT_DATE",
					parm.getTimestamp("OPT_DATE") == null ? new TNull(
							Timestamp.class) : parm.getTimestamp("OPT_DATE"));
			CtsoutD.addData("OPT_TERM",
					parm.getValue("OPT_TERM") == null ? new TNull(String.class)
							: parm.getValue("OPT_TERM"));
			CtsoutD.addData("NEW_FLG",
					parm.getValue("NEW_FLG", i) == null ? new TNull(String.class)
							: parm.getValue("NEW_FLG", i));
			
			CtsoutD.addData("TURN_POINT",
					parm.getValue("TURN_POINT", i) == null ? new TNull(String.class)
							: parm.getValue("TURN_POINT"));
			
		}
		
	}
	
	
	public void setOutQty() {
		tableD.acceptText();
		TParm d = tableD.getParmValue();
		if (d == null) {
			d = new TParm();
		}
		int count = 0;
		for (int i = 0; i < d.getCount("CLOTH_NO"); i++) {
			if (d.getValue("OUT_FLG", i).equals("Y")) {
				count++;
			}
		}
		tableM.acceptText();
		TParm m = tableM.getParmValue();
		m.setData("OUT_QTY", tableM.getSelectedRow(), count);
		int row = tableM.getSelectedRow();
		tableM.setParmValue(m);
		tableM.setSelectedRow(row);
		
		String washNo = tableM.getParmValue().getValue("WASH_NO", tableM.getSelectedRow());
		for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
			if(CtsoutM.getValue("WASH_NO", i).equals(washNo)){
				CtsoutM.setData("QTY", i, count);
			}
		}
		
		
		setValue("OUT_QTY", cloth_nos.size());
	}

	/**
	 * 保存按钮
	 */
	public void onSave() {
		for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
			CtsoutM.setData("END_DATE", i, getValue("END_DATE"));
			CtsoutM.setData("STATE", i, 3);
		}
		TParm parm = new TParm();
		parm.setData("CtsoutM", CtsoutM.getData());
		parm.setData("CtsoutD", CtsoutD.getData());
		System.out.println("insertCTSOUTMD=="+parm);
		TParm result = TIOM_AppServer.executeAction("action.cts.CTSAction","insertCTSOUTMD", parm);
		if (result.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		}
		updateEndDate();
		onClear();
	}
	

	private void updateEndDate() {
		tableM.acceptText();
		TParm parmM = tableM.getParmValue();
		for (int i = 0; i < parmM.getCount("WASH_NO"); i++) {
			TParm parm = new TParm();
			parm.setData("END_DATE", getValue("END_DATE"));
			parm.setData("STATE", 3);
			parm.setData("WASH_NO", parmM.getValue("WASH_NO", i));
			parm.setData("QTY", parmM.getValue("OUT_QTY", i));
			TParm result= TIOM_AppServer.executeAction("action.cts.CTSAction",
					"updateInOutWashNo", parm);

			String sql="SELECT CLOTH_NO FROM CTS_OUTD  WHERE WASH_NO = '"+parmM.getValue("WASH_NO", i)+"'";
			TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
			for(int j=0;j<sqlParm.getCount();j++){
				TParm parmDD = new TParm();
				parmDD.setData("RFID", sqlParm.getValue("CLOTH_NO", j));
				parmDD.setData("STATE", "1");
				result = CTSTool.getInstance().updateStockDD(parmDD);
				if (result.getErrCode() < 0) {
					messageBox("更新aa失败");
					return;
				}
			}
		}
		messageBox("保存成功");
	}

	/**
	 * 保存    更新ctsd
	 */
	private void onSave(String message) {
		tableM.acceptText();
		tableD.acceptText();
		if (rowM != -1) {
			TParm parmM = null;
			if (message.equals("")) {
				parmM = tableM.getParmValue().getRow(rowM);
				parmM.setData("TMP_FLG", "Y");
			} else {
				parmM = tableM.getParmValue().getRow(tableM.getSelectedRow());
			}
			TParm parmD = tableD.getParmValue();
			TParm parm = new TParm();
			parm.setData("WASHM", parmM.getData());
			parm.setData("WASHD", parmD.getData());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("END_DATE", getValue("END_DATE"));
			updateMD(parm);
		}	
		if (!message.equals("")) {
			messageBox(message);
		}
	}
	
	public void updateMD(TParm parm) {

		TParm washM = parm.getParm("WASHM");
		TParm washD = parm.getParm("WASHD");
		for (int i = 0; i < washD.getCount("CLOTH_NO"); i++) {
			if(washD.getValue("NEW_FLG", i).equals("Y")){

				if(washM.getValue("TURN_POINT").equals("")){
					CtsoutD.addData("TURN_POINT", "");
				}else{
					CtsoutD.addData("TURN_POINT", washM.getData("TURN_POINT"));
				}
				CtsoutD.addData("WASH_NO", washM.getData("WASH_NO"));
				CtsoutD.addData("SEQ_NO", washD.getData("SEQ_NO", i));
				CtsoutD.addData("CLOTH_NO",
						washD.getValue("CLOTH_NO", i) == null ? new TNull(
								String.class) : washD.getValue("CLOTH_NO", i));
				CtsoutD.addData("OWNER",
						washD.getValue("OWNER", i) == null ? new TNull(String.class)
								: washD.getValue("OWNER", i));
				CtsoutD.addData("PAT_FLG",
						washD.getValue("PAT_FLG", i) == null ? new TNull(
								String.class) : washD.getValue("PAT_FLG", i));
				CtsoutD.addData("OPT_USER",
						washD.getValue("OPT_USER", i) == null ? new TNull(String.class)
								: washD.getValue("OPT_USER", i));
				CtsoutD.addData("OPT_DATE",
						washD.getTimestamp("OPT_DATE", i) == null ? new TNull(
								Timestamp.class) : washD.getTimestamp("OPT_DATE", i));
				CtsoutD.addData("OPT_TERM",
						washD.getValue("OPT_TERM", i) == null ? new TNull(String.class)
								: washD.getValue("OPT_TERM", i));
				CtsoutD.addData("OUT_FLG", "Y");
				CtsoutD.addData("NEW_FLG",
						washD.getValue("NEW_FLG", i) == null ? new TNull(String.class)
								: washD.getValue("NEW_FLG", i));
			}
		}
	}
	
	
	/**
	 * 清空
	 */
	public void onClear() {
		
		tableM.acceptText();
		TParm parm = new TParm();
		tableM.setParmValue(parm);
		tableD.setParmValue(parm);
		
		CtsoutM = new TParm();
		CtsoutD = new TParm();
		
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|tButton_1|setEnabled", false);
		this.clearValue("CLOTH_NO;START_DATE;END_DATE;WASH_NO;PAT_FLGT;OUT_QTY;STATION_CODE;OWNER;TURN_POINT;OWNER_CODE");
		cloth_nos = new ArrayList<String>();
	}
	
	public boolean onClosing() {
		try {
			onClear();
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onClosing();
		return true;
	}
}
