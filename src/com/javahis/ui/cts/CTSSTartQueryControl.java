package com.javahis.ui.cts;

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
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class CTSSTartQueryControl extends TControl{
	private static TTable table1;
	private static TTable table2;
	private static TTable table3;
	private static TTable table4;
	private static TCheckBox pat_flg;
	private static TCheckBox all;
	private static String washNos;
	
	
	public void onInit(){

		clearValue("STATION_CODE;TURN_POINT");

		table1 = (TTable) getComponent("TABLE1");
		table2 = (TTable) getComponent("TABLE2");
		table3 = (TTable) getComponent("TABLE3");
		table4 = (TTable) getComponent("TABLE4");
		pat_flg = (TCheckBox) getComponent("PAT_FLG");
		all = (TCheckBox) getComponent("ALL");
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
			" SELECT 'N' FLG, A.WASH_NO, A.STATION_CODE, A.START_DATE, A.PAT_FLG, " +
			" A.STATE, A.WASH_CODE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, B.TURN_DESC TURN_POINT," +
			" A.QTY, A.OUT_QTY" +
			" FROM CTS_WASHM A,CTS_TURN_POINT B" +
			" WHERE A.TURN_POINT = B.ID AND A.START_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
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
		
//		if (pat_flg.isSelected()) {
//			sql += " AND PAT_FLG = 'Y'";
//		}else{
//			sql += " AND PAT_FLG = 'N'";
//		}
		sql += " ORDER BY B.TURN_DESC,A.STATION_CODE";
		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
		switch(selTTabbendPane){
			case 0:
				System.out.println("table1");
				table1.setParmValue(result);
				break;
			case 1:
				System.out.println("table3");
				table3.setParmValue(result);
				break;
		}
		
	}
	
	public void onAll(){
		table1.acceptText();
		table2.acceptText();
		TParm parmM = table1.getParmValue();
		String flg= "N";
		if(all.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			parmM.setData("FLG", i, flg);
		}
		table1.setParmValue(parmM);
		onTmSelected(table1);
	}
	
	public void onTmSelected3(Object obj){
		table3 = (TTable) obj;
		table3.acceptText();
		table4.acceptText();
		TParm parmM = table3.getParmValue();
		String washNos = "";
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			if(parmM.getBoolean("FLG", i)){
				washNos += "'" + parmM.getValue("WASH_NO", i) + "',";
			}
		}
		if(washNos.length()>0){
			washNos = washNos.substring(0, washNos.length() - 1);
		}
		TParm parmD3 = getOutD3(washNos);
		this.washNos = washNos;
		table4.setParmValue(parmD3);
	}
	
	public void onTmSelected(Object obj){
		table1 = (TTable) obj;
		table1.acceptText();
		table2.acceptText();
		TParm parmM = table1.getParmValue();
		String washNos = "";
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			if(parmM.getBoolean("FLG", i)){
				washNos += "'" + parmM.getValue("WASH_NO", i) + "',";
			}
		}
		if(washNos.length()>0){
			washNos = washNos.substring(0, washNos.length() - 1);
		}
		TParm parmD = getOutD(washNos);
		this.washNos = washNos;
		table2.setParmValue(parmD);
		setValue("QTY", parmD.getCount("CLOTH_NO")==-1?0:parmD.getCount("CLOTH_NO"));
	}
	
	private TParm getOutD(String washNos){
		String sql = 
			" SELECT  B.RFID CLOTH_NO, B.INV_CODE, '' DEPT_CODE, B.CTS_COST_CENTRE STATION_CODE,"
			+ " E.USER_NAME WASH_CODE, C.INV_CHN_DESC, D.PAT_FLG, '' DEPT_CHN_DESC,"
			+ " G.COST_CENTER_CHN_DESC STATION_DESC, " 
			+ "'Y' OUT_FLG, B.OWNER_CODE OWNER, D.START_DATE, A.WASH_NO, H.USER_ID, J.TURN_DESC TURN_POINT"
			+ " FROM CTS_WASHD A,"
			+ " INV_STOCKDD B,"
			+ " INV_BASE C,"
			+ " CTS_WASHM D,"
			+ " SYS_OPERATOR E,"
			+ " SYS_COST_CENTER G,"
			+ " CTS_TURN_POINT J,"
			+ " SYS_OPERATOR H"
			+ " WHERE A.CLOTH_NO = B.RFID"
			+ " AND B.INV_CODE = C.INV_CODE" 
			+ " AND A.TURN_POINT = J.ID" 
			+ " AND A.WASH_NO = D.WASH_NO"
			+ " AND D.WASH_CODE = E.USER_ID"
			+ " AND B.CTS_COST_CENTRE = G.COST_CENTER_CODE"
			+ " AND B.OWNER = H.USER_ID(+)"
			+ " AND D.WASH_NO IN ("
			+ washNos + ") ORDER BY B.INV_CODE,J.TURN_DESC,B.CTS_COST_CENTRE";
//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	private TParm getOutD3(String washNos){
		String sql = 
			" SELECT  B.RFID CLOTH_NO, B.INV_CODE, '' DEPT_CODE, B.CTS_COST_CENTRE STATION_CODE,"
			+ " E.USER_NAME WASH_CODE, C.INV_CHN_DESC, D.PAT_FLG, '' DEPT_CHN_DESC,"
			+ " G.COST_CENTER_CHN_DESC STATION_DESC, " 
			+ "'Y' OUT_FLG, B.OWNER_CODE OWNER, D.START_DATE, A.WASH_NO, H.USER_ID, J.TURN_DESC TURN_POINT," 
			+ " A.OUT_WASH_NO"
			+ " FROM CTS_WASHD A,"
			+ " INV_STOCKDD B,"
			+ " INV_BASE C,"
			+ " CTS_WASHM D,"
			+ " SYS_OPERATOR E,"
			+ " SYS_COST_CENTER G,"
			+ " CTS_TURN_POINT J,"
			+ " SYS_OPERATOR H"
			+ " WHERE A.CLOTH_NO = B.RFID"
			+ " AND B.INV_CODE = C.INV_CODE" 
			+ " AND A.TURN_POINT = J.ID" 
			+ " AND A.WASH_NO = D.WASH_NO"
			+ " AND D.WASH_CODE = E.USER_ID"
			+ " AND B.CTS_COST_CENTRE = G.COST_CENTER_CODE"
			+ " AND B.OWNER = H.USER_ID(+)"
			+ " AND D.WASH_NO IN ("
			+ washNos + ") ORDER BY A.OUT_WASH_NO,B.INV_CODE,J.TURN_DESC,B.CTS_COST_CENTRE";
//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public void onPrint(){
		table1.acceptText();
		table2.acceptText();
		TParm resultD = table2.getParmValue();
		TParm resultM = new TParm();
		int countM=0;
		TParm parmM = table1.getShowParmValue();
		for(int m=0;m<parmM.getCount("WASH_NO");m++){
			 if(parmM.getBoolean("FLG", m)){
				 resultM.setRowData(countM, parmM.getRow(m));
				 countM++;
			 }
		}
		TParm washNoParm = new TParm();
		countM=resultM.getCount("WASH_NO")-1;
		String washNo="";
		for(int m=0;m<countM;m++){
			if(resultM.getValue("TURN_POINT", m).equals(resultM.getValue("TURN_POINT", m+1))){
				washNo = washNo + resultM.getValue("WASH_NO", m)+",";
			}else{
				washNoParm.addData("WASH_NOS", washNo+resultM.getValue("WASH_NO", m));
				washNoParm.addData("STATION_CODE", resultM.getValue("STATION_CODE", m));
				washNoParm.addData("TURN_POINT", resultM.getValue("TURN_POINT", m));
				washNo="";
			}
		}
		washNoParm.addData("WASH_NOS", washNo+resultM.getValue("WASH_NO", countM));
		washNoParm.addData("STATION_CODE", resultM.getValue("STATION_CODE", countM));
		washNoParm.addData("TURN_POINT", resultM.getValue("TURN_POINT", countM));
		for(int w=0;w<washNoParm.getCount("WASH_NOS");w++){
			String washNos=washNoParm.getValue("WASH_NOS", w);
			String [] wash = washNos.split(",");
			TParm result = new TParm();
			int countD=0;
			List<String> list = new ArrayList<String>();
			for(int j=0;j<wash.length;j++){
				list.add(wash[j]);
			}
			for(int d=0;d<resultD.getCount("CLOTH_NO");d++){

				if(list.contains(resultD.getValue("WASH_NO", d))){
					result.setRowData(countD, resultD.getRow(d));
					countD++;
				}
			}
			
			TParm printParm = new TParm();
			Timestamp date = StringTool.getTimestamp(new Date());
			Timestamp end_date = result.getTimestamp("START_DATE", 0);
			String wash_no = washNos.replace("'", "");
			printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName()+"洗衣登记入库单");
//			printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
//			if (result.getValue("PAT_FLG", 0).equals("Y")) {
//				printParm.setData("TITLE2", "TEXT", "病患洗衣登记单");
//			} else {
//				printParm.setData("TITLE2", "TEXT", "员工洗衣登记单");
//			}
			printParm.setData("PROGRAM", "TEXT", "洗衣清点");
			printParm.setData("PRINT_DATE", "TEXT", date.toString()
					.substring(0, 10).replaceAll("-", "/"));
			printParm.setData("PRINT_NO", "TEXT", washNos);
			printParm.setData("DEPT_CHN_DESC", "TEXT", result.getValue(
					"DEPT_CHN_DESC", 0));
			TTextFormat station = (TTextFormat) getComponent("STATION_CODE");
			
			String sql="SELECT ORG_CODE, ORG_DESC FROM INV_ORG WHERE ORG_CODE='"+washNoParm.getValue("STATION_CODE", w)+"'";
			TParm stationParm = new TParm(TJDODBTool.getInstance().select(sql));
			
//			printParm.setData("STATION_DESC", "TEXT", ""+stationParm.getValue("ORG_DESC",0));
			
//			printParm.setData("STATION_DESC", "TEXT", ""+result.getValue(
//					"STATION_DESC", 0));
			
			printParm.setData("STATION_DESC", "TEXT", ""+washNoParm.getValue("TURN_POINT", w));
			
//			printParm.setData("QTY", "TEXT", result.getValue("QTY", 0));  getValue("QTY")
			printParm.setData("OUT_QTY", "TEXT", "入库数量: "+result.getCount("CLOTH_NO")+"件");
//			printParm.setData("START_DATE", "TEXT", start_date.toString()
//					.substring(0, 19).replaceAll("-", "/"));
			String str = end_date.toString()
					.substring(0, 19).replaceAll("-", "/");
			printParm.setData("END_DATE", "TEXT", "入库时间: "+str);
			printParm.setData("WASH_CODE", "TEXT", result.getValue("WASH_CODE", 0));

			TParm clothParm = new TParm();
			int count = 1;
			String clothNo="";
			int resultCount=result.getCount("INV_CHN_DESC")-1;
//			System.out.println("resultCount==="+resultCount);
			for(int i=0; i<resultCount; i++){
//				System.out.println("CLOTH_NO1==="+result.getValue("CLOTH_NO", i));
//				System.out.println("CLOTH_NO2==="+result.getData("CLOTH_NO", i));
				if(result.getBoolean("PAT_FLG", i)){
					clothParm.addData("CLOTH_NO", result.getValue("CLOTH_NO", i));
					clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", i));
					clothParm.addData("OWNER", result.getValue("USER_ID", i)+" "+result.getValue("OWNER", i));
					clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", i)+"/"+result.getValue("TURN_POINT", i));
					clothParm.addData("QTY", 1+"件");
					clothParm.addData("COMMON","");
				}else{
					String inv_code=result.getValue("INV_CHN_DESC", i);
					String station_desc=result.getValue("STATION_DESC", i);
					String turn_point=result.getValue("TURN_POINT", i);
					if(result.getValue("INV_CHN_DESC", i+1).equals(inv_code) && result.getValue("TURN_POINT", i+1).equals(turn_point)&& result.getValue("STATION_DESC", i+1).equals(station_desc)){
						count++;
						clothNo = clothNo + result.getValue("CLOTH_NO", i)+",";
					}else{
						clothParm.addData("CLOTH_NO", "");
						clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", i));
						clothParm.addData("OWNER", "");
						clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", i)+"/"+result.getValue("TURN_POINT", i));
						clothParm.addData("QTY", count+"件");
						clothParm.addData("COMMON",clothNo+result.getValue("CLOTH_NO", i));
						count=1;
						clothNo="";
					}
				}
			}
			if(result.getBoolean("PAT_FLG", resultCount)){
				clothParm.addData("CLOTH_NO", result.getValue("CLOTH_NO", resultCount));
				clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
				clothParm.addData("OWNER", result.getValue("USER_ID", resultCount)+" "+result.getValue("OWNER", resultCount));
				clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount)+"/"+result.getValue("TURN_POINT", resultCount));
				clothParm.addData("QTY", 1+"件");
				clothParm.addData("COMMON","");
			}else{
				if(result.getValue("INV_CHN_DESC", resultCount).equals(result.getValue("INV_CHN_DESC", resultCount-1)) &&  result.getValue("TURN_POINT", resultCount).equals(result.getValue("TURN_POINT", resultCount-1))&&  result.getValue("STATION_DESC", resultCount).equals(result.getValue("STATION_DESC", resultCount-1))){
					clothParm.addData("CLOTH_NO", "");
					clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
					clothParm.addData("OWNER", "");
					clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount)+"/"+result.getValue("TURN_POINT", resultCount));
					clothParm.addData("QTY", count+"件");
					clothParm.addData("COMMON",clothNo+result.getValue("CLOTH_NO", resultCount));
				}else{
					clothParm.addData("CLOTH_NO", "");
					clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
					clothParm.addData("OWNER", "");
					clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount)+"/"+result.getValue("TURN_POINT", resultCount));
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

			this.openPrintWindow("%ROOT%\\config\\prt\\CTS\\CTSRegList1.jhw",
					printParm,true);
			
			
		}
		
		
		
	}
	
	public void onClear(){
		washNos = "";
		table1.removeRowAll();
		table2.removeRowAll();
		table3.removeRowAll();
		table4.removeRowAll();
		clearValue("START_DATE;END_DATE;WASH_NO;STATION_CODE;TURN_POINT");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		pat_flg.setSelected(false);
		all.setSelected(false);
	}
	
	public void onDelete(){
		table1.acceptText();
		TParm result = new TParm();
		TParm parmM = table1.getParmValue();
		TParm deleteParm = new TParm();
		for(int i=0;i<parmM.getCount("WASH_NO");i++){
			if(parmM.getBoolean("FLG", i)){
				deleteParm.addData("WASH_NO", parmM.getValue("WASH_NO", i));
			}
		}
		result= TIOM_AppServer.executeAction("action.cts.CTSAction",
				"deleteWASHMD", deleteParm);
		
		if (result.getErrCode() < 0) {
			 messageBox("删除失败");
						 
			 }else{
			 messageBox("删除成功");
			 onClear();
	
			 }
		
		for(int j =0;j<deleteParm.getCount("WASH_NO");j++){
		
			TParm updateParm = CTSTool.getInstance().selectWASHD(deleteParm.getRow(j));
			String clothNo = "";
			for(int i=0;i<updateParm.getCount("WASH_NO");i++){
				clothNo = clothNo+ "'"+updateParm.getValue("CLOTH_NO", i)+"',";
			}
			clothNo = clothNo.substring(0, clothNo.length() - 1);
			String sql = "UPDATE INV_STOCKDD SET STATE='1' WHERE RFID IN ("+clothNo+")";
			TParm updateResult = new TParm(TJDODBTool.getInstance().update(sql));
			if(result.getErrCode()<0){
				messageBox(result.getErrText());
				return;
			}
			
		}
		
		
	}
	/**
     * 得到TTabbedPane
     * @param tag String
     * @return TTabbedPane
     */
    public TTabbedPane getTTabbedPane(String tag) {
        return (TTabbedPane)this.getComponent(tag);
    }
	
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE2|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "洗衣入库单");
	}

}
