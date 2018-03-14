package com.javahis.ui.cts;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.cts.CTSTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class CTSOutQueryControl extends TControl{
	private static TTable tableM;
	private static TTable tableD;
	private static TTable tableInM;
	private static TTable tableInD;
	private static TCheckBox pat_flg;
	private static TCheckBox all;
	private static String washNos;
	private Color red = new Color(255, 255, 0);
	private Color write = new Color(255, 255, 255);
	private List<String> list = new ArrayList<String>();
	
	
	public void onInit(){
		tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
		tableInM = (TTable) getComponent("TABLE3");
		tableInD = (TTable) getComponent("TABLE4");
		pat_flg = (TCheckBox) getComponent("PAT_FLG");
		all = (TCheckBox) getComponent("ALL");
		onClear();
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		 // 面的table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|TABLE1|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTmSelected");
		
		// 面的table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|TABLE3|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTmSelected3");
	}
	
	public void onQuery(){
		// 设置查询条件
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
//		if(getValueString("STATION_CODE").length()==0){
//			messageBox("请选择成本中心");
//			return;
//		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String sql = 
			" SELECT 'N' FLG, A.WASH_NO, A.STATION_CODE, A.END_DATE, A.PAT_FLG, " +
			" A.STATE, A.WASH_CODE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, B.TURN_DESC TURN_POINT, A.QTY " +
			" FROM CTS_OUTM A, CTS_TURN_POINT B " +
			" WHERE A.TURN_POINT = B.ID AND END_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
			" AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')";
		if (!getValueString("WASH_NO").equals("")) {
			sql += " AND A.WASH_NO = '" + getValueString("WASH_NO") + "'";
		}
		if (!getValueString("STATION_CODE").equals("")) {
			sql += " AND A.STATION_CODE = '" + getValueString("STATION_CODE") + "'";
		}
		
		if (!getValueString("TURN_POINT").equals("")) {
			sql += " AND B.ID = '" + getValueString("TURN_POINT") + "'";
		}
		
		if (pat_flg.isSelected()) {
			sql += " AND PAT_FLG = 'Y'";
		}
//		else{
//			sql += " AND PAT_FLG = 'N'";
//		}
		sql += " ORDER BY TURN_POINT ,END_DATE DESC";
		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		tableM.setParmValue(result);
	}
	
	public void onAll(){
		tableM.acceptText();
		tableD.acceptText();
		tableInM.acceptText();
		tableInD.acceptText();
		TParm parmM = tableM.getParmValue();
		String flg= "N";
		if(all.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			parmM.setData("FLG", i, flg);
		}
		tableM.setParmValue(parmM);
		onTmSelected(tableM);
//		onQueryWashM();
	}
	
	
	public void onQueryWashM(){
//		String sql = 
//			" SELECT DISTINCT WASH_NO" +
//			" FROM CTS_WASHD" +
//			" WHERE OUT_WASH_NO IN (" + washNos + ")";
//		TParm washNoParm = new TParm(TJDODBTool.getInstance().select(sql));
//		if(washNoParm.getCount()<0){
//			return;
//		}
		tableD.acceptText();
		//TODO
		String in_wash_nos = "";
		for (int i = 0; i < list.size(); i++) {
			in_wash_nos += "'" + list.get(i) + "',";
		}
		if(in_wash_nos.length()>0){
			in_wash_nos = in_wash_nos.substring(0, in_wash_nos.length() - 1);
		}
		
		String sql = 
			" SELECT DISTINCT A.WASH_NO, 'N' FLG,A.STATION_CODE, A.START_DATE END_DATE, A.PAT_FLG, A.STATE, A.WASH_CODE,  C.TURN_DESC TURN_POINT, A.QTY,A.OUT_QTY" +
			" FROM CTS_WASHM A, CTS_WASHD B, CTS_TURN_POINT C" +
			" WHERE A.WASH_NO IN (" + in_wash_nos +")" +
			" AND A.WASH_NO = B.WASH_NO " +
			" AND A.TURN_POINT = C.ID " + 
			" AND B.OUT_WASH_NO IS NULL";
//		System.out.println("washoddd========="+sql);
		TParm washParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (washParm.getCount() < 0) {
			return;
		}
		
		tableInM.setParmValue(washParm);
//		tableInM.setSelectedRow(0);
		

		
//		TParm washDParm = getOutInD(in_wash_nos);
//		if (washDParm.getCount() < 0) {
//			return;
//		}
//		tableInD.setParmValue(washDParm);
//		TParm tableInDParm = tableInD.getParmValue();
//		TParm tableInMParm = tableInM.getParmValue();
//		for(int d=0;d<tableInDParm.getCount("CLOTH_NO");d++){
//			if(tableInDParm.getValue("OUT_WASH_NO", d).length()==0){
//				tableInD.setRowColor(d, red);
//				
//				for(int m=0;m<tableInMParm.getCount("WASH_NO");m++){
//					
//					if(tableInMParm.getValue("WASH_NO", m).equals(tableInDParm.getValue("WASH_NO", d))){
//						tableInM.setRowColor(m, red);
//					}
//				}
//				
//				
//			}
//			
//		}
		
	}
	public void onTmSelected3(Object obj){
		tableInM = (TTable) obj;
		tableInM.acceptText();
		tableInD.acceptText();
		tableInD.removeRowAll();
		TParm parmM = tableInM.getParmValue();
		String washNos = "";
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			if(parmM.getBoolean("FLG", i)){
				washNos += "'" + parmM.getValue("WASH_NO", i) + "',";
			}
		}
		if(washNos.length()>0){
			washNos = washNos.substring(0, washNos.length() - 1);
		}
		TParm washDParm = getOutInD(washNos);
		tableInD.setParmValue(washDParm);
		setValue("NOT_QTY", washDParm.getCount("CLOTH_NO")==-1?0:washDParm.getCount("CLOTH_NO"));
	}
	
	
	public void onTmSelected(Object obj){
		tableM = (TTable) obj;
		tableM.acceptText();
		tableD.acceptText();
		tableInM.acceptText();
		tableInD.acceptText();
		tableInM.removeRowAll();
		tableInD.removeRowAll();
		
//		for (int i = 0; i < tableD.getParmValue().getCount("CLOTH_NO"); i++) {
//			tableM.setRowColor(i, write);
//			
//		}
		
		TParm parmM = tableM.getParmValue();
		String washNos = "";
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
//			tableM.setRowColor(i, write);
			if(parmM.getBoolean("FLG", i)){
				washNos += "'" + parmM.getValue("WASH_NO", i) + "',";
			}
		}
		if(washNos.length()>0){
			washNos = washNos.substring(0, washNos.length() - 1);
		}
		
//		System.out.println("washNOs==============="+washNos);
		TParm parmD = getOutD(washNos);
		this.washNos = washNos;
		tableD.setParmValue(parmD);
		list = new ArrayList<String>();
		
		for (int i = 0; i < parmD.getCount(); i++) {
			if(!list.contains(parmD.getValue("IN_WASH_NO", i))){
				list.add(parmD.getValue("IN_WASH_NO", i));
			}
			//颜色标记
//			if(parmD.getValue("IN_WASH_NO", i).length() == 0){
//				tableD.setRowColor(i, red);
//				for (int j = 0; j < parmM.getCount("FLG"); j++) {
//					if(parmM.getValue("WASH_NO", j).equals(parmD.getValue("WASH_NO", i))){
//						tableM.setRowColor(j, red);
//					}
//				}
//			}
		}

		setValue("QTY", parmD.getCount("CLOTH_NO")==-1?0:parmD.getCount("CLOTH_NO"));
		onQueryWashM();
		
	}
	
	private TParm getOutD(String washNos){
		String sql = 
			" SELECT  B.RFID CLOTH_NO, B.INV_CODE, '' DEPT_CODE, D.STATION_CODE,"
			+ " E.USER_NAME WASH_CODE, C.INV_CHN_DESC, D.PAT_FLG, '' DEPT_CHN_DESC,"
			+ " G.COST_CENTER_CHN_DESC STATION_DESC, 'Y' OUT_FLG, B.OWNER_CODE OWNER, D.END_DATE, A.WASH_NO , A.IN_WASH_NO, H.USER_ID,  J.TURN_DESC TURN_POINT"
			+ " FROM CTS_OUTD A,"
			+ " INV_STOCKDD B,"
			+ " INV_BASE C,"
			+ " CTS_OUTM D,"
			+ " SYS_OPERATOR E,"
			+ " SYS_COST_CENTER G,"
			+ " CTS_TURN_POINT J,"
			+ " SYS_OPERATOR H" 
			+ " WHERE A.CLOTH_NO = B.RFID" 
			+ " AND B.INV_CODE = C.INV_CODE"
			+ " AND A.TURN_POINT = J.ID"
			+ " AND A.WASH_NO = D.WASH_NO"
			+ " AND D.WASH_CODE = E.USER_ID"
			+ " AND D.STATION_CODE = G.COST_CENTER_CODE"
			+ " AND B.OWNER = H.USER_ID(+)" 
			+ " AND D.WASH_NO IN ("
			+ washNos + ") ORDER BY B.INV_CODE,J.TURN_DESC";
//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	private TParm getOutInD(String washNos){
		String sql = 
			" SELECT  B.RFID CLOTH_NO, B.INV_CODE, '' DEPT_CODE, D.STATION_CODE,"
			+ " E.USER_NAME WASH_CODE, C.INV_CHN_DESC, D.PAT_FLG, '' DEPT_CHN_DESC,"
			+ " G.COST_CENTER_CHN_DESC STATION_DESC, 'Y' OUT_FLG, B.OWNER_CODE OWNER, D.START_DATE, A.WASH_NO ,E.USER_ID, A.OUT_WASH_NO"
			+ " FROM CTS_WASHD A,"
			+ " INV_STOCKDD B,"
			+ " INV_BASE C,"
			+ " CTS_WASHM D,"
			+ " SYS_OPERATOR E,"
			+ " SYS_COST_CENTER G,"
			+ " SYS_OPERATOR H" 
			+ " WHERE A.CLOTH_NO = B.RFID" 
			+ " AND B.INV_CODE = C.INV_CODE"
			+ " AND A.WASH_NO = D.WASH_NO"
			+ " AND D.WASH_CODE = E.USER_ID"
			+ " AND D.STATION_CODE = G.COST_CENTER_CODE"
			+ " AND B.OWNER = H.USER_ID(+)" 
			+ " AND A.OUT_WASH_NO IS NULL"
			+ " AND D.WASH_NO IN ("
			+ washNos + ") ORDER BY B.RFID";
//		System.out.println("aaaaaaaa========"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public void onPrint(){
		
		tableM.acceptText();
		tableD.acceptText();
		TParm ParmM=tableM.getParmValue();
		TParm resultD = tableD.getParmValue();
		
		TParm resultM = new TParm();
//		System.out.println("ParmM.getCount()=="+ParmM.getCount());
		int countM=0;
		for(int i=0;i<ParmM.getCount();i++){
			if(ParmM.getValue("FLG", i).equals("Y")){
//				resultM.addData("WASH_NO", ParmM.getValue("WASH_NO", i));
//				resultM.addData("STATION_CODE", ParmM.getValue("STATION_CODE", i));
				resultM.setRowData(countM, ParmM.getRow(i));
				countM++;
			}
		}
//		System.out.println("resultM=="+resultM);
		int flg;
		
		TParm stationParm= new TParm();
		for(int i=0;i<resultM.getCount("WASH_NO");i++){
			flg=i;
			for(int j=0;j<i;j++){
//				if(resultM.getValue("STATION_CODE", i).equals(resultM.getValue("STATION_CODE", j))){
				if(resultM.getValue("TURN_POINT", i).equals(resultM.getValue("TURN_POINT", j))){
					flg=j;
				}
			}
			if(flg==i){
				stationParm.addData("STATION_CODE", resultM.getValue("STATION_CODE", i));
				stationParm.addData("TURN_POINT", resultM.getValue("TURN_POINT", i));
			}
		}
		
		
		
//		System.out.println("stationParm=="+stationParm);
		for(int i=0;i<stationParm.getCount("STATION_CODE");i++){
//		for(int i=0;i<stationParm.getCount("TURN_POINT");i++){
//			String station= stationParm.getValue("STATION_CODE", i);
			String turnPoint= stationParm.getValue("TURN_POINT", i);
			TParm result=new TParm();
			String washNO="";
			int countD=0;
			for(int j=0;j<resultM.getCount("WASH_NO");j++){
//				if(resultM.getValue("STATION_CODE", j).equals(station)){
				if(resultM.getValue("TURN_POINT", j).equals(turnPoint)){	
					washNO= washNO + resultM.getValue("WASH_NO", j)+",";	
				}
			}
			for(int h=0;h<resultD.getCount();h++){
//				if(resultD.getValue("STATION_CODE", h).equals(station)){
				if(resultD.getValue("TURN_POINT", h).equals(turnPoint)){
					result.setRowData(countD, resultD.getRow(h));
					countD++;
				}
			}
			
			
			TParm printParm = new TParm();
			Timestamp date = StringTool.getTimestamp(new Date());
			Timestamp end_date = result.getTimestamp("END_DATE", 0);
			String wash_no = washNos.replace("'", "");
			printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName()+"洗衣登记出库单");
//			printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
//			if (result.getValue("PAT_FLG", 0).equals("Y")) {
//				printParm.setData("TITLE2", "TEXT", "病患洗衣登记单");
//			} else {
//				printParm.setData("TITLE2", "TEXT", "员工洗衣登记单");
//			}
			printParm.setData("PROGRAM", "TEXT", "装衣清点");
			printParm.setData("PRINT_DATE", "TEXT", date.toString()
					.substring(0, 10).replaceAll("-", "/"));
//			printParm.setData("PRINT_NO", "TEXT", wash_no);  
			printParm.setData("PRINT_NO", "TEXT", washNO.substring(0, washNO.length() - 1));
			printParm.setData("DEPT_CHN_DESC", "TEXT", result.getValue(
					"DEPT_CHN_DESC", 0));
			
//			printParm.setData("STATION_DESC", "TEXT", result.getValue(
//					"STATION_DESC", 0));
			
			printParm.setData("STATION_DESC", "TEXT", result.getValue(
					"TURN_POINT", 0));
			
			
			
//			printParm.setData("QTY", "TEXT", result.getValue("QTY", 0));  getValue("QTY")
			printParm.setData("OUT_QTY", "TEXT", "出库数量: "+result.getCount("CLOTH_NO")+"件");
//			printParm.setData("START_DATE", "TEXT", start_date.toString()
//					.substring(0, 19).replaceAll("-", "/"));
			String str = end_date.toString()
					.substring(0, 19).replaceAll("-", "/");
			printParm.setData("END_DATE", "TEXT", "分拣时间: "+str);
			printParm.setData("WASH_CODE", "TEXT", result.getValue("WASH_CODE", 0));

			TParm clothParm = new TParm();
//			for (int i = 1; i < result.getCount("INV_CODE"); i++) {
//				if (result.getValue("INV_CHN_DESC", i).equals(inv_code)) {
//					count++;
//				} else {
//					clothParm.addData("INV_CODE", inv_code);
//					clothParm.addData("COUNT", count);
//					inv_code = result.getValue("INV_CHN_DESC", i);
//					count = 1;
//				}
//			}
//			clothParm.addData("INV_CODE", inv_code);
//			clothParm.addData("COUNT", count);
			 
			

			
			int count = 1;
			String clothNo="";
			int resultCount=result.getCount("INV_CHN_DESC")-1;
			for(int k=0; k<resultCount; k++){
				if(result.getBoolean("PAT_FLG", k)){
					clothParm.addData("CLOTH_NO", result.getValue("CLOTH_NO", k));
					clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", k));
					clothParm.addData("OWNER", result.getValue("USER_ID", k)+" "+result.getValue("OWNER", k));
					clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", k));
					clothParm.addData("QTY", 1+"件");
					clothParm.addData("COMMON","");
				}else{
					String inv_code=result.getValue("INV_CHN_DESC", k);
					String station_desc=result.getValue("STATION_DESC", k);
					String turn_point=result.getValue("TURN_POINT", k);
					if(result.getValue("INV_CHN_DESC", k+1).equals(inv_code) && result.getValue("STATION_DESC", k+1).equals(station_desc) ){
//					if(result.getValue("INV_CHN_DESC", k+1).equals(inv_code) && result.getValue("TURN_POINT", k+1).equals(turn_point)){	
						count++;
						clothNo = clothNo + result.getValue("CLOTH_NO", k)+",";
					}else{
						clothParm.addData("CLOTH_NO", "");
						clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", k));
						clothParm.addData("OWNER", "");
						clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", k));
						clothParm.addData("QTY", count+"件");
						clothParm.addData("COMMON",clothNo+result.getValue("CLOTH_NO", k));
						count=1;
						clothNo="";
					}
				}
			}
			if(result.getBoolean("PAT_FLG", resultCount)){
				clothParm.addData("CLOTH_NO", result.getValue("CLOTH_NO", resultCount));
				clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
				clothParm.addData("OWNER", result.getValue("USER_ID", resultCount)+" "+result.getValue("OWNER", resultCount));
				clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount));
				clothParm.addData("QTY", 1+"件");
				clothParm.addData("COMMON","");
			}else{
				if(result.getValue("INV_CHN_DESC", resultCount).equals(result.getValue("INV_CHN_DESC", resultCount-1)) &&  result.getValue("STATION_DESC", resultCount).equals(result.getValue("STATION_DESC", resultCount-1)) ){
//				if(result.getValue("INV_CHN_DESC", resultCount).equals(result.getValue("INV_CHN_DESC", resultCount-1)) &&  result.getValue("TURN_POINT", resultCount).equals(result.getValue("TURN_POINT", resultCount-1))){
					clothParm.addData("CLOTH_NO", "");
					clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
					clothParm.addData("OWNER", "");
					clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount));
					clothParm.addData("QTY", count+"件");
					clothParm.addData("COMMON",clothNo+result.getValue("CLOTH_NO", resultCount));
				}else{
					clothParm.addData("CLOTH_NO", "");
					clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
					clothParm.addData("OWNER", "");
					clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount));
					clothParm.addData("QTY", 1+"件");
					clothParm.addData("COMMON",result.getValue("CLOTH_NO", resultCount));
				}
			}
			
			
			
			clothParm.setCount(clothParm.getCount("INV_CHN_DESC"));
			clothParm.addData("SYSTEM", "COLUMNS", "CLOTH_NO");
			clothParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
			clothParm.addData("SYSTEM", "COLUMNS", "OWNER");
			clothParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
			clothParm.addData("SYSTEM", "COLUMNS", "QTY");
			clothParm.addData("SYSTEM", "COLUMNS", "COMMON");
			printParm.setData("TABLE", clothParm.getData());
			System.out.println("clothParm::=="+clothParm);
			
			
//			TParm clothParm2 = new TParm();
//			for (int i = 0; i < clothParm.getCount("INV_CODE"); i++) {
//				if (i % 2 == 0) {
//					clothParm2.addData("C1", clothParm.getValue("INV_CODE", i));
//					clothParm2.addData("C2", clothParm.getValue("COUNT", i));
//				} else {
//					clothParm2.addData("C3", clothParm.getValue("INV_CODE", i));
//					clothParm2.addData("C4", clothParm.getValue("COUNT", i));
//				}
//				if (i == clothParm.getCount("INV_CODE") - 1 && i % 2 == 0) {
//					clothParm2.addData("C3", "");
//					clothParm2.addData("C4", "");
//				}
//			}
//			clothParm2.setCount(clothParm2.getCount("C1"));
//			clothParm2.addData("SYSTEM", "COLUMNS", "C1");
//			clothParm2.addData("SYSTEM", "COLUMNS", "C2");
//			clothParm2.addData("SYSTEM", "COLUMNS", "C3");
//			clothParm2.addData("SYSTEM", "COLUMNS", "C4");
//			printParm.setData("TABLE", clothParm2.getData());
			
			this.openPrintWindow("%ROOT%\\config\\prt\\CTS\\CTSRegList1.jhw",
					printParm, true);
			

			
			
			
			
		}
		
		
		
		
	}
	
	public void onClear(){
		washNos = "";
		tableM.removeRowAll();
		tableD.removeRowAll();
		tableInM.removeRowAll();
		tableInD.removeRowAll();
		clearValue("START_DATE;END_DATE;WASH_NO;STATION_CODE;TURN_POINT;QTY;NOT_QTY");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		pat_flg.setSelected(false);
		all.setSelected(false);
		list = new ArrayList<String>();
	}
	
	
	public void onDelete(){
		TParm result = new TParm();
		TParm parmM = tableM.getParmValue();
		TParm deleteParm = new TParm();
		for(int i=0;i<parmM.getCount("WASH_NO");i++){
			if(parmM.getBoolean("FLG", i)){
				deleteParm.addData("WASH_NO", parmM.getValue("WASH_NO", i));
			}
		}
		
		
		for(int j =0;j<deleteParm.getCount("WASH_NO");j++){
			
			TParm updateParm = CTSTool.getInstance().selectOUTD(deleteParm.getRow(j));
			String clothNo = "";
			for(int i=0;i<updateParm.getCount("WASH_NO");i++){
				clothNo = clothNo+ "'"+updateParm.getValue("CLOTH_NO", i)+"',";
			}
			clothNo = clothNo.substring(0, clothNo.length() - 1);
			String sql = "UPDATE INV_STOCKDD SET STATE='0' WHERE RFID IN ("+clothNo+")";
//			System.out.println("stockDD=="+sql);
			TParm updateResult = new TParm(TJDODBTool.getInstance().update(sql));
			if(updateResult.getErrCode()<0){
				messageBox(updateResult.getErrText());
				return;
			}
			sql = "UPDATE CTS_WASHD SET OUT_WASH_NO='' WHERE CLOTH_NO IN ("+clothNo+")";
			System.out.println("washD=="+sql);
			updateResult = new TParm(TJDODBTool.getInstance().update(sql));
			if(updateResult.getErrCode()<0){
				messageBox(updateResult.getErrText());
				return;
			}
			
		}
		
		result= TIOM_AppServer.executeAction("action.cts.CTSAction",
				"deleteOUTMD", deleteParm);
		
		if (result.getErrCode() < 0) {
			 messageBox("删除失败");				 
		}else{
			 messageBox("删除成功");
			 onClear();
		}
		
		
		
	}
	
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE2|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "洗衣出库单");
	}
	
}
