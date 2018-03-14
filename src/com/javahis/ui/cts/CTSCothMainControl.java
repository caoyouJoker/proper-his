package com.javahis.ui.cts;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.hibernate.event.def.OnReplicateVisitor;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.device.RFIDMidUtils;
import com.javahis.device.Uitltool;
import com.javahis.system.textFormat.TextFormatSYSOperator;



/**
 * 20130808 最新
 * @author Administrator
 *
 */
public class CTSCothMainControl extends TControl{
	private static String CLOTH_NO = "";
	private static String INV_CODE ="";
	private static String OWNER = "";
	private static String OWNER_CODE= "";
//	private static String DEPT_CODE = "";
	private static String STATION_CODE = "";
	private static String optIp = "";
	private static String optId = "";
	private static String ACTIVE_FLG = "N";
	private static String PAT_FLG = "N";
	private static String WRITE_FLG = "";
	private static String TURN_POINT = "";
//	private static String STATE = "";
	private static TCheckBox active_flg;
	private static TTable table;
	private static int row = -1;
	private static TParm rowParm = null;
	private static TTextField cloth_no;
	private static TTextField owner_code;
//	private static TTextField turn_point;
	private static TTable rfid_table;
//	private static TTextField inv_code;
	private static TRadioButton tRadioButton_1;
	private static TRadioButton tRadioButton_0;
	private static TComboBox write_flg;
	private static boolean rfid_flg; 
	private static TParm RfidParm;
	private static boolean rfid_write_flg;  //每写入一次变为true，其他为false
	
	private static String houseman;  //实习医生工号
	private String oldRfid="";
	
	private boolean ascending = false;
	private int sortColumn =-1;
	private Compare compare = new Compare();
	
	

    

	
    /**
	 * 初始化方法
	 */
	public void onInit() {
	
		optIp = Operator.getIP();
		optId = Operator.getID();
		active_flg = (TCheckBox) getComponent("ACTIVE_FLG");
		cloth_no = (TTextField) getComponent("CLOTH_NO");
		owner_code = (TTextField) getComponent("OWNER_CODE");
//		turn_point = (TTextField) getComponent("TURN_POINT");
		table = (TTable) getComponent("TABLE");
		rfid_table = (TTable) getComponent("RFID_TABLE");
//		inv_code = (TTextField) getComponent("INV_CODE");
		tRadioButton_1 =  (TRadioButton) getComponent("tRadioButton_1");
		tRadioButton_0 =  (TRadioButton) getComponent("tRadioButton_0");
		write_flg = (TComboBox) getComponent("WRITE_FLG");
		rfid_flg=false;
		rfid_write_flg=false;
		
		
		RfidParm = new TParm();
		TParm parm = new TParm();
//        // 设置弹出菜单
//		inv_code.setPopupMenuParameter("UI", getConfigParm().newConfig(
//            "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
//        // 定义接受返回值方法
//		inv_code.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
//                                    "popReturn");
		
		TextFormatSYSOperator ss = new TextFormatSYSOperator();
//		ss.setAttribute("EndDateFlg", "1");
		
		//查找实习医生工号
		String sql="SELECT USER_ID FROM SYS_OPERATOR WHERE HOUSEMAN_FLG='Y'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		houseman=selParm.getValue("USER_ID", 0);

//		onQuery();
	}
	
	public void popReturn(String tag, Object obj) {
        if (obj == null)
            return;
        //invBase返回的数据包
        TParm parm = (TParm) obj;
        //System.out.println("parm==="+parm);
        String inv_chn_desc = parm.getValue("INV_CHN_DESC");
        INV_CODE = parm.getValue("INV_CODE");
        setValue("INV_CODE", inv_chn_desc);
	}
	public void selStation(){
		OWNER = getValueString("OWNER");
		if(OWNER.equals(houseman)){
			clearValue(OWNER_CODE);
			owner_code.setEnabled(true);
		}else{
			String sql="SELECT USER_ID, USER_NAME, COST_CENTER_CODE FROM SYS_OPERATOR WHERE USER_ID = '"+OWNER+"'";
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			this.setValue("STATION_CODE", selParm.getValue("COST_CENTER_CODE", 0));
			this.setValue("OWNER_CODE", selParm.getValue("USER_NAME", 0));
			owner_code.setEnabled(false);
		}
		
	}
	
	 /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
	
    
	
	/**
	 * 保存
	 */
	public void onSave(){
		getThisValue();
		String clothno=this.getValueString("CLOTH_NO");
//		System.out.println("aaaa======"+clothno);
		TParm result = null;
//		if(!OWNER.equals("")){
//			String sql="SELECT USER_ID, USER_NAME, COST_CENTER_CODE FROM SYS_OPERATOR  WHERE USER_ID = '"+OWNER+"'";
//			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//			STATION_CODE=selParm.getValue("COST_CENTER_CODE", 0);
//		}
		result = update();
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			new Exception(result.getErrText());
			return;
		}
		
		if(!rfid_write_flg){
			messageBox("操作成功");
		}
		
		
//		onClear();
//		onQuery();
//		onTableSelectRow(clothno);
	}
	
	public void onTableSelectRow(String cloth){
		TParm parm = table.getParmValue();
//		System.out.println("CLOTH_NO==="+cloth);
		for(int i=0;i<parm.getCount();i++){
			if(parm.getValue("CLOTH_NO", i).equals(cloth)){
//				System.out.println("aaaaaa");
				
//				TParm parmW = table.getParmValue().getRow(i);
//				parmW.setData("WRITE_FLG", "T");
//				table.setRowParmValue(i, parmW);
				table.setSelectedRow(i);
				return;
			}
		}
	}
	
	
	
	
//	/**
//	 * 保存
//	 */
//	public TParm save(){
//		String sql =
//			" INSERT INTO CTS_CLOTH " +
//			" (CLOTH_NO, INV_CODE, OWNER, DEPT_CODE, STATION_CODE, ACTIVE_FLG," +
//			" PAT_FLG, STATE, OPT_USER," +
//			" OPT_DATE, OPT_TERM" +
//			" )" +
//			" VALUES ('" + CLOTH_NO + "', '" + INV_CODE + "', '" + OWNER + "', '" + DEPT_CODE + "', '" + STATION_CODE + "', '" + ACTIVE_FLG + "'," +
//			" '" + PAT_FLG + "', '" + STATE + "', '" + optId + "'," +
//			" SYSDATE, '" + optIp + "'" +
//			" )";
//		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//		return result;
//	}
	
	
	/**
	 * 查询
	 */
	public void onQuery(){
		
//		TTable table4 = this.getTTable("TABLE");
//		int rowCount4 = table.getRowCount();
//		Map m4=new HashMap();
//		for (int i = 0; i < rowCount4; i++) {
//			m4.put(i,new Color(255,255,0));
//		}
//		 table.setRowColorMap(m4); 
//        this.getTTable("TABLE").removeRowAll();
//		
        
        
		getThisValue();
//		System.out.println("INV_CODE=="+INV_CODE);
		StringBuilder sql = new StringBuilder();
		sql.append(
//				" SELECT CLOTH_NO, INV_CODE, OWNER, DEPT_CODE, STATION_CODE, ACTIVE_FLG," +
//				" PAT_FLG, STATE, OPT_USER, OPT_DATE, OPT_TERM" +
//				" FROM CTS_CLOTH" +
//				" WHERE PAT_FLG = '" + PAT_FLG + "' ");
				" SELECT   A.RFID CLOTH_NO, A.CTS_COST_CENTRE STATION_CODE, A.INV_CODE, B.INV_CHN_DESC," +
				" A.ACTIVE_FLG, A.PAT_FLG, A.OWNER, B.DESCRIPTION, B.COST_PRICE ,A.WRITE_FLG,A.OWNER_CODE,A.TURN_POINT" +
				" FROM INV_STOCKDD A, INV_BASE B" +
				" WHERE A.INV_CODE = B.INV_CODE " +
				" AND A.RFID IS NOT NULL " +
				" AND B.INV_KIND = '08'" );
		

		if(!"".equals(CLOTH_NO)){
			sql.append(" AND A.RFID = '"+CLOTH_NO+"' ");
		}
		
		
		if(!"".equals(INV_CODE)){
			sql.append(" AND B.INV_CODE = '"+INV_CODE+"' ");
		}
		
		if(!"".equals(OWNER)){
			sql.append(" AND A.OWNER = '"+OWNER+"' ");
		}
//		if(!"".equals(DEPT_CODE)){
//			sql.append(" AND DEPT_CODE = '"+DEPT_CODE+"' ");
//		}
//		if(!"".equals(STATION_CODE)){
//			sql.append(" AND A.CTS_COST_CENTRE = '"+STATION_CODE+"' ");
//		}
		if(!"".equals(WRITE_FLG)){
			sql.append(" AND WRITE_FLG = '"+WRITE_FLG+"' ");
		}
		if(WRITE_FLG.equals("Y")){
			sql.append(" ORDER BY  A.RFID DESC, A.INV_CODE, A.CTS_COST_CENTRE");
		}else{
			sql.append(" ORDER BY  A.RFID, A.INV_CODE, A.CTS_COST_CENTRE");
		}

//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if(result.getCount() <0){
			this.messageBox("没有要查询的数据");
			return;
		}
		if(rfid_flg){
			rfid_flg=false;
//			RfidParm = new TParm();
			RfidParm.setData(result.getData());
//			RfidParm = result;
			
			
		}else{
			table.setParmValue(result);
		}
		// 排序监听	
		addListener(this.getTTable("TABLE"));
		
	}
	
	 /**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public  void addListener(final TTable table) {
//		 System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		 TParm tableDate = table.getParmValue();
		 
//		 System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
					int rowCount = table.getRowCount();
//					Map m1=new HashMap();
//					for (int i = 0; i < rowCount; i++) {
//						m1.put(i,new Color(255,255,0));
//					}
//					 table.setRowColorMap(m1);
//				 System.out.println("===排rrrrrrr前===");		
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
//				 System.out.println("+i+"+i);
//				 System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
//				 System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.根据点击的列,对vector排序
//				 System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				
				java.util.Collections.sort(vct, compare);
				cloneVectoryParam(vct, new TParm(), strNames,table);
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	
	
	/**
	 * table监听器
	 */
	public void onClickTable(){
//		cloth_no.setEnabled(false);
//		inv_code.setEnabled(false);
		row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		rowParm = tableParm.getRow(row);
		setValue("CLOTH_NO", rowParm.getValue("CLOTH_NO"));
		setValue("INV_CODE", rowParm.getValue("INV_CODE"));
		setValue("OWNER", rowParm.getValue("OWNER"));
		setValue("OWNER_CODE", rowParm.getValue("OWNER_CODE"));
		setValue("STATION_CODE", rowParm.getValue("STATION_CODE"));
		setValue("STATE", rowParm.getValue("STATE"));
//		setValue("TURN_POINT", rowParm.getValue("TURN_POINT"));
		if(rowParm.getValue("ACTIVE_FLG").equals("Y")){
			active_flg.setSelected(true);
		}else{
			active_flg.setSelected(false);
		}
		if(rowParm.getValue("PAT_FLG").equals("Y")){
			tRadioButton_0.setSelected(true);
			PAT_FLG = "Y";
		}
		if(rowParm.getValue("PAT_FLG").equals("N")){
			tRadioButton_1.setSelected(true);
			PAT_FLG = "N";
		}
		setValue("WRITE_FLG", rowParm.getValue("WRITE_FLG"));
	}
	/**
	 * 清空
	 * 
	 */
	public void onClear(){
		clearData();
		table.removeRowAll();
		
	}
	
	public void clearData(){
		cloth_no.setEnabled(true);
//		inv_code.setEnabled(true);
		owner_code.setEnabled(true);
		clearValue("CLOTH_NO;WRITE_FLG;OWNER;DEPT_CODE;STATION_CODE;STATE;OWNER_CODE;TURN_POINT");
		active_flg.setSelected(true);
		row = -1;
		ACTIVE_FLG = "N";
//		INV_CODE="";
		rfid_table.removeRowAll();
		
		onNoPat();
		tRadioButton_1.setSelected(true);
//		onQuery();
	}
	
	
	/**
	 * 删除
	 */
	public void onDelete(){
//		if(row < 0){
//			messageBox("请先选择");
//			return;
//		}
//		getThisValue();
//		String sql = 
//			"DELETE CTS_CLOTH WHERE CLOTH_NO = '"+CLOTH_NO+"'";
//		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//		if(result.getErrCode()<0){
//			messageBox(result.getErrText());
//			return;
//		}
//		messageBox("操作成功");
//		onClear();
//		onQuery(); 
		
		int i = messageBox("是否删除", "是否删除？", this.YES_NO_OPTION);
		if(i == 1){
			return;
		}

		
		table.acceptText();
		int row = table.getSelectedRow();
		String rfid=table.getParmValue().getValue("CLOTH_NO", row);
		String sql =
			" UPDATE INV_STOCKDD" +
			" SET " +
			" OWNER = ''," +
			" OWNER_CODE = '',"+
			" CTS_COST_CENTRE = ''," +
			" WRITE_FLG = 'N'," +
			" PAT_FLG = 'N'," +
//			" STATE = '"+STATE+"'," +
			" OPT_USER = '"+optId+"'," +
			" OPT_DATE = SYSDATE," +
			" OPT_TERM = '"+optIp+"'" +
			" WHERE RFID = '"+rfid+"'";
//		System.out.println("update==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("操作成功");
		onClear();
	}
	/**
	 * 修改    
	 */
	public TParm update(){
		String sql =
			" UPDATE INV_STOCKDD" +
			" SET " +
			" OWNER = '"+OWNER+"'," +
			" CTS_COST_CENTRE = '"+STATION_CODE+"'," +
			" WRITE_FLG = 'Y'," +
			" PAT_FLG = '"+PAT_FLG+"'," +
			" OWNER_CODE = '"+OWNER_CODE+"',"+
			" TURN_POINT = '"+TURN_POINT+"',"+
//			" STATE = '"+STATE+"'," +
			" OPT_USER = '"+optId+"'," +
			" OPT_DATE = SYSDATE," +
			" OPT_TERM = '"+optIp+"'" +
			" WHERE RFID = '"+CLOTH_NO+"'";
//		System.out.println("update==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	/**
	 * 取得值
	 */
	public void getThisValue(){
		CLOTH_NO = getValueString("CLOTH_NO");
		INV_CODE = getValueString("INV_CODE");
		OWNER = getValueString("OWNER");
		OWNER_CODE = getValueString("OWNER_CODE");
		TURN_POINT = getValueString("TURN_POINT");
		STATION_CODE = getValueString("STATION_CODE");
//		STATE = getValueString("STATE");
		if(active_flg.isSelected()){
			ACTIVE_FLG = "Y";
		}else{
			ACTIVE_FLG = "N";
		}
		WRITE_FLG = getValueString("WRITE_FLG");
		
	}
	
	public void onSpreadout(){
		this.openWindow("%ROOT%\\config\\cts\\CTSUnfold.x");
	}
	
	
	
	public void onPat(){
//		onClear();
		PAT_FLG = "Y";
//		this.callFunction("UI|DEPT_CODE|setEnabled", true);
//		this.callFunction("UI|STATION_CODE|setEnabled", true);
//		this.callFunction("UI|OWNER|setEnabled", false);
	}
	
	public void onNoPat(){
//		onClear();
		PAT_FLG = "N";
//		this.callFunction("UI|DEPT_CODE|setEnabled", false);
//		this.callFunction("UI|STATION_CODE|setEnabled", false);
//		this.callFunction("UI|OWNER|setEnabled", true);
	}
	
	public void onOperator(){
//		String sql =
//			" SELECT DEPT_CODE" +
//			" FROM SYS_OPERATOR_DEPT" +
//			" WHERE USER_ID = '" + getValue("OWNER") + "' AND MAIN_FLG = 'Y'";
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		if(result.getCount()>0){
//			setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
//		}
//		sql =
//			" SELECT STATION_CLINIC_CODE" +
//			" FROM SYS_OPERATOR_STATION" +
//			" WHERE USER_ID = '" + getValue("OWNER") + "' AND MAIN_FLG = 'Y'";
//		result = new TParm(TJDODBTool.getInstance().select(sql));
//		if(result.getCount()>0){
//			setValue("STATION_CODE", result.getValue("STATION_CLINIC_CODE", 0));
//		}
	}
	
	/**
	 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	 */
	public void creat() {
		try {
			RFIDMidUtils.connect();
			this.callFunction("UI|OPEN|setEnabled", false);
			this.callFunction("UI|DISCONN|setEnabled", true);
		} catch (Exception e) {
			e.printStackTrace();
			this.callFunction("UI|OPEN|setEnabled", true);
			this.callFunction("UI|DISCONN|setEnabled", false);
			messageBox("失败");
		}
		
	}

	public void disConn() {
		try {
			
			RFIDMidUtils.disconnect();
			this.callFunction("UI|DISCONN|setEnabled", false);
			this.callFunction("UI|OPEN|setEnabled", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.callFunction("UI|DISCONN|setEnabled", true);
			this.callFunction("UI|OPEN|setEnabled", false);
			messageBox("失败");
		}
	}
	
	public void read(){
		read("1");
	}
	
	public void read(String flg) {
		rfid_table.removeRowAll();
		try {
//			RFIDMidUtils.connect();
//			System.out.println("ssssss==="+RFIDMidUtils.readTags());
//			System.out.println("sssdddddsss==="+RFIDMidUtils.readTags().length);
			String[] s = RFIDMidUtils.readTags();
			TParm parm = new TParm();
			for (String a : s) {
//				System.out.println("sssssaaa===="+a);
				String rfid = Uitltool.decode(a);
				parm.addData("CODE", a);
				parm.addData("RFID", rfid);

				if(flg.length()>0){
					//判断是否写成功
					if(rfid_write_flg){
						rfid_write_flg=false;
						if(!rfid.equals(getValue("CLOTH_NO"))){
							this.messageBox("没有写成功！");
							
							return;
						}
						
						
					}
					
					clearData();
					rfid_flg=true;
					setValue("CLOTH_NO", rfid);
					clearValue(INV_CODE);
					onQuery();
					parm.addData("OWNER", RfidParm.getValue("OWNER", 0));
					parm.addData("OWNER_CODE", RfidParm.getValue("OWNER_CODE", 0));
					parm.addData("STATION_CODE", RfidParm.getValue("STATION_CODE", 0));
					parm.addData("PAT_FLG", RfidParm.getValue("PAT_FLG", 0));
//					TParm tableParm = table.getParmValue();
					this.setValue("INV_CODE", RfidParm.getValue("INV_CODE", 0));
					this.setValue("TURN_POINT", RfidParm.getValue("TURN_POINT", 0));
//					INV_CODE=RfidParm.getValue("INV_CODE", 0);
					oldRfid="";
				}else{
					//判断是否签是否写过
					String sql="SELECT RFID FROM INV_STOCKDD WHERE WRITE_FLG='Y' AND RFID='"+rfid+"'";
					TParm write = new TParm(TJDODBTool.getInstance().select(sql));
					if(write.getCount()>0){
						int i = messageBox("是否重写标签", "标签已经写过，是否重写？", this.YES_NO_OPTION);
						if(i == 1){
							return;
						}
						oldRfid = rfid;
					}else{
						oldRfid="";
					}
				}
			}
			
//			System.out.println("parm1111====="+parm);
			rfid_table.setParmValue(parm);
			rfid_table.setSelectedRow(0);
//			RFIDMidUtils.disconnect();
			this.callFunction("UI|WRITE|setEnabled", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void write(){
		this.read("");
		onWrite();
//		onSave();
		this.read("1");
		
	}
	
	public void onWrite(){
		try {
			int row = rfid_table.getSelectedRow();
			String oldValue = rfid_table.getParmValue().getValue("CODE", row);
			String data = getValueString("CLOTH_NO");
//			RFIDMidUtils.connect();
			RFIDMidUtils.writeTag(oldValue, Uitltool.encode(data));
//			RFIDMidUtils.disconnect();
			rfid_table.removeRowAll();
			rfid_write_flg=true;
			onSave();
			if(oldRfid.length()>0){
				onRfidReplace();
			}
			
//			messageBox("成功");
		} catch (Exception e) { // TODO Auto-generated catch block
			//e.printStackTrace();
//			System.out.println("error==="+e.getMessage());
		}
	}
	
	/**
	 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	 */
	
	
	 public boolean onClosing(){
		 disConn();
		 return true;
	 }
	 

	 /**
		 * 得到 Vector 值
		 * 
		 * @param group
		 *            String 组名
		 * @param names
		 *            String "ID;NAME"
		 * @param size
		 *            int 最大行数
		 * @return Vector
		 */
		private Vector getVector(TParm parm, String group, String names, int size) {
			Vector data = new Vector();
			String nameArray[] = StringTool.parseLine(names, ";");
			if (nameArray.length == 0) {
				return data;
			}
			int count = parm.getCount(group, nameArray[0]);
			if (size > 0 && count > size)
				count = size;
			for (int i = 0; i < count; i++) {
				Vector row = new Vector();
				for (int j = 0; j < nameArray.length; j++) {
					row.add(parm.getData(group, nameArray[j], i));
				}
				data.add(row);
			}
			return data;
		}  
	
		/**
		 * 
		 * @param columnName
		 * @param tblColumnName
		 * @return
		 */
		private int tranParmColIndex(String columnName[], String tblColumnName) {
			int index = 0;
			for (String tmp : columnName) {

				if (tmp.equalsIgnoreCase(tblColumnName)) {
					return index;
				}
				index++;
			}

			return index;
		}
		/**
		 * vector转成TParm
		 */
		 void cloneVectoryParam(Vector vectorTable, TParm parmTable,
				String columnNames,TTable table) {
			//
			// System.out.println("===vectorTable==="+vectorTable);
			// 行数据->列
			// System.out.println("========names==========="+columnNames);
			String nameArray[] = StringTool.parseLine(columnNames, ";");
			// 行数据;
			for (Object row : vectorTable) {
				int rowsCount = ((Vector) row).size();
				for (int i = 0; i < rowsCount; i++) {
					Object data = ((Vector) row).get(i);
					parmTable.addData(nameArray[i], data);
				}
			}
			parmTable.setCount(vectorTable.size());
			table.setParmValue(parmTable);
			
			// System.out.println("排序后===="+parmTable);

		}
		 
	public void onRfidReplace() {
//		oldRfid = "C13000018285";		
		String sql = "SELECT COUNT (CLOTH_NO) AS TIMES FROM CTS_OUTD WHERE CLOTH_NO = '"+ oldRfid + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String sql1 = "SELECT OLD_TIMES FROM CTS_RFID_REPLACE WHERE NEW_RFID = '"+ oldRfid + "'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm parm = new TParm();
		parm.setData("OLD_RFID", oldRfid);
		if(result1.getCount()>0){
			parm.setData("OLD_TIMES", result.getInt("TIMES", 0)+result1.getInt("OLD_TIMES", 0));
			parm.setData("VALID_FLG", "N");
		}else{
			parm.setData("OLD_TIMES", result.getInt("TIMES", 0));
			parm.setData("VALID_FLG", "Y");
		}

		parm.setData("NEW_RFID", CLOTH_NO);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		result = TIOM_AppServer.executeAction("action.cts.CTSAction",
				"saveCtsRfidReplce", parm);
		if (result.getErrCode() < 0) {
			new Exception(result.getErrText());
		}
	
	}
	 
}
