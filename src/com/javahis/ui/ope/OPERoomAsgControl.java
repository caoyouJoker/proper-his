package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.ope.OPEOpBookTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;


/**
 * <p>Title: �����ų�</p>
 *
 * <p>Description: �����ų�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-27
 * @version 4.0
 */
public class OPERoomAsgControl extends TControl {

	TParm DATA;
	//===========������==================add by wanglong 20121212
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * 
	 */
	public void onInit(){
		super.onInit();
		TableInit();
		//��ȡ��ǰʱ��
		String date =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
		this.setValue("OP_DATE_S",StringTool.getTimestamp(date+"000000","yyyyMMddhhmmss"));
		this.setValue("OP_DATE_E",StringTool.getTimestamp(date+"235959","yyyyMMddhhmmss"));
		this.clearValue("OP_DEPT_CODE");
	}

	/**
	 * ����ʼ��
	 */
	private void TableInit(){
		TTable table = (TTable)this.getComponent("Table");
		addSortListener(table);//===�����������=====add by wanglong 20121212
		OrderList orderList = new OrderList();
		table.addItem("OrderList",orderList);
		OpList opList = new OpList();
		table.addItem("OpList",opList);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		TParm parm = new TParm();
		if(this.getValueString("ADM_TYPE").length()>0){
			parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
		}
		if(this.getValueString("OP_DATE_S").length()>0&&this.getValueString("OP_DATE_E").length()>0){
			parm.setData("OP_DATE_S",StringTool.getString((Timestamp)this.getValue("OP_DATE_S"),"yyyyMMddHHmmss"));
			parm.setData("OP_DATE_E",StringTool.getString((Timestamp)this.getValue("OP_DATE_E"),"yyyyMMddHHmmss"));
		}
		if(this.getValueString("TYPE_CODE").length()>0){
			parm.setData("TYPE_CODE",this.getValue("TYPE_CODE"));
		}
		if(this.getValueString("URGBLADE_FLG2").equals("Y")){
			parm.setData("URGBLADE_FLG","Y");
		}
		if(this.getValueString("URGBLADE_FLG3").equals("Y")){
			parm.setData("URGBLADE_FLG","N");
		}
		if(this.getValueString("OP_DEPT_CODE").length()>0){
			parm.setData("OP_DEPT_CODE",this.getValue("OP_DEPT_CODE"));
		}
		if(this.getValueString("OP_STATION_CODE").length()>0){
			parm.setData("OP_STATION_CODE",this.getValue("OP_STATION_CODE"));
		}
		if(this.getValueString("BOOK_DR_CODE").length()>0){
			parm.setData("BOOK_DR_CODE",this.getValue("BOOK_DR_CODE"));
		}
		if(this.getValueString("ROOM_NO").length()>0){
			parm.setData("ROOM_NO",this.getValue("ROOM_NO"));
		}
		parm.setData("CANCEL_FLG","N");//��ѯû�б�ȡ����ԤԼ
		//===============pangben modify 20110630 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		//=============pangben modify 20110630 stop
		DATA = OPEOpBookTool.getInstance().selectOpBook(parm);
		if(DATA.getErrCode()<0){
			this.messageBox("E0005");
			return;
		}
		TTable table = (TTable)this.getComponent("Table");
		table.setParmValue(DATA);
	}

	/**
	 * �����ų�
	 */
	public void onAsg(){
		TTable table = (TTable)this.getComponent("Table");
		int index = table.getSelectedRow();//ѡ����
		if(index<0){
			return;
		}
		TParm data = table.getParmValue();
		String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ",index);
		this.openDialog("%ROOT%/config/ope/OPEPersonnel.x",OPBOOK_SEQ);
		//onQuery();
	}

	/**
	 * ������Ϣ
	 */
	public void onOpInfo(){
		TTable table = (TTable)this.getComponent("Table");
		int index = table.getSelectedRow();//ѡ����
		DATA = table.getParmValue();//add by wanglong 20121212
		String OPBOOK_SEQ = DATA.getValue("OPBOOK_SEQ",index);
		TParm parm = new TParm();
		parm.setData("FLG","update");
		parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
		parm.setData("ADM_TYPE",DATA.getValue("ADM_TYPE",index));
		this.openDialog("%ROOT%/config/ope/OPEOpBook.x",parm);
	}

	/**
	 * ������¼
	 */
	public void onOpRecord(){
		TTable table = (TTable)this.getComponent("Table");
		int index = table.getSelectedRow();//ѡ����
		if(index<0){
			return;
		}
		TParm parm = new TParm();
		DATA = table.getParmValue();//add by wanglong 20121212
		String OPBOOK_SEQ = DATA.getValue("OPBOOK_SEQ",index);
		parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
		parm.setData("MR_NO",DATA.getValue("MR_NO",index));
		parm.setData("ADM_TYPE",DATA.getValue("ADM_TYPE",index));
		this.openDialog("%ROOT%/config/ope/OPEOpDetail.x",parm);
	}

	/**
	 * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER :
				dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * ����CODE�滻���� ģ����ѯ���ڲ��ࣩ
	 */
	public class OpList extends TLabel {
		TDataStore dataStore = new TDataStore();
		public OpList(){
			dataStore.setSQL("select * from SYS_OPERATIONICD");
			dataStore.retrieve();
		}
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER :
				dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("OPERATION_ICD");
			Vector d = (Vector) parm.getData("OPT_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	// ====================������begin======================add by wanglong 20121212
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
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
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================




	/**
	 * 20170328 zhanglei Ϊ���ӵ���ѯ�ռ�����״̬ 
	 * ADM_INP��DAY_OPE_FLG��Y��ʾ�ռ�����
	 */

	private String getDayOpeFlg(String MrNo , String CaseNo){

		String sqlRJ = " SELECT DAY_OPE_FLG FROM ADM_INP WHERE MR_NO = '"+ MrNo +"'  AND  "
				+ "CASE_NO = '"+ CaseNo + "'";
		//System.out.println("sql:::::::"+sqlRJ);		   
		TParm parmRJ = new TParm(TJDODBTool.getInstance().select(sqlRJ));  
		//System.out.println("SQL::"+sqlRJ);	  

		String DayOpeFlg = parmRJ.getValue("DAY_OPE_FLG");


		return DayOpeFlg;
	}


	/**
	 * ���ɽ��ӵ�
	 */
	public void onCreate(){
		TParm action = new TParm();
		TTable table = (TTable)this.getComponent("Table");
		int index = table.getSelectedRow();//ѡ����
		if(index<0){
			this.messageBox("��ѡ�񲡻�");
			return;
		}
		TParm parm = table.getParmValue();
		//	        System.out.println("---parm----------------"+parm);
		
		/** modified by WangQing 20170411 -start*/
		action.setData("MR_NO",parm.getValue("MR_NO",index));//������
		action.setData("CASE_NO",parm.getValue("CASE_NO",index));//�����
		action.setData("PAT_NAME",parm.getValue("PAT_NAME",index));//����       

		//20170328 zhanglei Ϊ�ṹ�������������������ǰ���ӵ� ��ʵ�ռ��������
		String getDayOpeFlg = getDayOpeFlg(action.getValue("MR_NO") , action.getValue("CASE_NO"));
		action.setData("DAY_OPE_FLG","[Y]".equals(getDayOpeFlg) ? "�ռ�����":"");//�ռ��������
//		TParm actionParm = new TParm();// ����ģ��
		if(parm.getValue("TYPE_CODE",index).equals("1")){// �������
//			actionParm = this.getEmrFilePath("EMR0603055");
//			action.setData("DEPT_TYPE_FLG","OPE");//���ڿ���ѡ�������ʾ���ұ��
			action.setData("TRANSFER_CLASS","OC/OW"); //��������
			/*modified by Eric 20170518*/
			action.setData("FROM_DEPT", "030503"); // ת������(������)
		}else{// ��������
//			actionParm = this.getEmrFilePath("EMR0603033");
//			action.setData("DEPT_TYPE_FLG","INT");//���ڿ���ѡ�������ʾ���ұ��
			action.setData("TRANSFER_CLASS","TC/TW"); //��������
			/*modified by Eric 20170518*/
			action.setData("FROM_DEPT", "0306"); // ת������(������)
		}
//		action.setData("FROM_DEPT","030503"); //ת������(������)
		action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ", index));// ��������
		//		    action.setData("OP_CODE",parm.getValue("OP_CODE1",index)); //��ʽ
//		action.setData("TEMPLET_PATH",
//				actionParm.getValue("TEMPLET_PATH",0));//���ӵ�·��
//		action.setData("EMT_FILENAME",
//				actionParm.getValue("EMT_FILENAME",0));//���ӵ�����
		action.setData("FLG",false);//��ģ��
		this.openWindow("%ROOT%\\config\\ope\\OPETransfertype.x", action);
		/** modified by WangQing 20170411 -end*/
	}
	/**
	 * ����һ����
	 */
	public void onTransfer() {
		TParm action = new TParm();
		TTable table = (TTable) this.getComponent("Table");
		int index = table.getSelectedRow();// ѡ����

		if (index < 0) {
			this.messageBox("��ѡ��ָ��������");
			return;
		} else {
			TParm parm = table.getParmValue();
			// System.out.println("---parm----------------"+parm);
			action.setData("MR_NO", parm.getValue("MR_NO", index));// ������
			action.setData("CASE_NO", parm.getValue("CASE_NO", index));// �����
			// action.setData("OP_CODE", parm.getValue("OP_CODE1",index));//��ʽ
			action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ", index));// �������뵥��
		}
		// System.out.println("---action----------------"+action);
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x", action);
	}
	/**
	 * �õ�EMR·��
	 */
	public TParm getEmrFilePath(String subclassCode){
		String sql=" SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE," +
				" A.TEMPLET_PATH FROM EMR_TEMPLET A"+
				" WHERE A.SUBCLASS_CODE = '"+subclassCode+ "'";
		TParm result = new TParm();
		result = new TParm(TJDODBTool.getInstance().select(sql)); 
		//    	System.out.println("---result----------------getEmrFilePath"+result);
		return result;
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {

		TParm data = new TParm();
		TTable table = (TTable)this.getComponent("Table");
		int row  = table.getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ������");
			return;
		}
		TParm rowParm = table.getShowParmValue().getRow(row);
		TParm hideParm = table.getParmValue().getRow(row);
		String sql = " SELECT * "+
				" FROM (  SELECT DISTINCT A.TIME, "+
				" A.HEART_RATE, "+
				" A.BREATH, "+
				" A.PRESSURE, "+
				" A.OXYGEN_SATURATION, "+
				" A.PAIN_ASSESSMENT, "+
				" A.ILLNESS_RECORD, "+
				" A.ORDER_DESC, "+
				" '' ROUTE_CHN_DESC, "+
				" A.MEDI_QTY, "+
				" '' UNIT_CHN_DESC "+
				" FROM OPE_INTERVENNURPLAT A "+
				" WHERE     A.CASE_NO = '"+hideParm.getValue("CASE_NO")+"' "+
				" AND A.OPBOOK_SEQ = '"+hideParm.getValue("OPBOOK_SEQ")+"' "+
				" AND A.ORDER_DESC IS NULL "+
				" UNION  "+
				" SELECT DISTINCT A.TIME, "+
				" A.HEART_RATE, "+
				" A.BREATH, "+
				" A.PRESSURE, "+
				" A.OXYGEN_SATURATION, "+
				" A.PAIN_ASSESSMENT, "+
				" A.ILLNESS_RECORD, "+
				" A.ORDER_DESC, "+
				" C.ROUTE_CHN_DESC, "+
				" B.MEDI_QTY, "+
				" D.UNIT_CHN_DESC "+
				" FROM OPE_INTERVENNURPLAT A, ODI_ORDER B, SYS_PHAROUTE C ,SYS_UNIT D "+
				" WHERE     A.CASE_NO = '"+hideParm.getValue("CASE_NO")+"' "+
				" AND A.OPBOOK_SEQ = '"+hideParm.getValue("OPBOOK_SEQ")+"' "+
				" AND A.CASE_NO = B.CASE_NO "+
				" AND A.ORDER_NO = B.ORDER_NO "+
				" AND A.ORDER_SEQ = B.ORDER_SEQ "+
				" AND B.ROUTE_CODE = C.ROUTE_CODE(+) "+
				" AND B.MEDI_UNIT = D.UNIT_CODE(+)  "+
				" AND A.ORDER_DESC IS NOT NULL "+
				" ) ORDER BY TIME DESC";
		System.out.println("SSSSSS:::::"+sql);
		TParm printParm = new TParm(
				TJDODBTool
				.getInstance()
				.select(sql));

		for (int i = 0; i < printParm.getCount(); i++) {
			data.addData("TIME", printParm.getValue("TIME", i).toString()
					.substring(5, 16).replaceAll("-", "/"));
			data.addData("HEART_RATE", printParm.getValue("HEART_RATE", i)
					.equals("") ? ""
							: (printParm.getValue("HEART_RATE", i) + "��/����"));
			data.addData("BREATH",
					printParm.getValue("BREATH", i).equals("") ? ""
							: (printParm.getValue("BREATH", i) + "��/����"));
			data.addData("PRESSURE", printParm.getValue("PRESSURE", i).equals(
					"") ? "" : (printParm.getValue("PRESSURE", i) + "mmHg"));
			data.addData("OXYGEN_SATURATION", printParm.getValue(
					"OXYGEN_SATURATION", i).equals("") ? "" : (printParm
							.getValue("OXYGEN_SATURATION", i) + "%"));
			data.addData("PAIN_ASSESSMENT", printParm.getValue(
					"PAIN_ASSESSMENT", i));
			data.addData("ORDER_DESC", printParm.getValue("ORDER_DESC", i));
			data.addData("ROUTE_CODE", printParm.getValue("ROUTE_CHN_DESC", i));
			data.addData("MEDI_CODE", printParm.getDouble("MEDI_QTY", i) > 0?printParm.getDouble("MEDI_QTY", i)+" "+printParm.getValue("UNIT_CHN_DESC", i):"" );
			data.addData("ILLNESS_RECORD", printParm.getValue("ILLNESS_RECORD",
					i));
		}
		//

		data.setCount(data.getCount("TIME"));
		data.addData("SYSTEM", "COLUMNS", "TIME");
		data.addData("SYSTEM", "COLUMNS", "HEART_RATE");
		data.addData("SYSTEM", "COLUMNS", "BREATH");
		data.addData("SYSTEM", "COLUMNS", "PRESSURE");
		data.addData("SYSTEM", "COLUMNS", "OXYGEN_SATURATION");
		data.addData("SYSTEM", "COLUMNS", "PAIN_ASSESSMENT");
		data.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		data.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		data.addData("SYSTEM", "COLUMNS", "MEDI_CODE");
		data.addData("SYSTEM", "COLUMNS", "ILLNESS_RECORD");

		TParm result = new TParm();
		result.setData("TABLE", data.getData());
		result.setData("MR_NO", "TEXT", rowParm.getValue("MR_NO"));
		result.setData("OP_DATE", "TEXT", rowParm.getValue("OP_DATE").substring(0,10));
		result.setData("PAT_NAME", "TEXT", rowParm.getValue("PAT_NAME"));
		result.setData("OP_ROOM", "TEXT", rowParm.getValue("ROOM_NO"));
		this
		.openPrintWindow(
				"%ROOT%\\config\\prt\\OPE\\OPEIntervenNurPlatPrint.jhw",
				result);

	}


	//20170208 machao start �������������ų�
	public void onCheckSelectAll() {
		TTable table = (TTable)this.getComponent("Table");
		table.acceptText();
		if (table.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "SELECT_FLAG", true);
			}
		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "SELECT_FLAG", false);
			}            
		}
	}
	/**
	 * �õ�CheckBox����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
	/**
	 * ���������ų̣�Ҫ������ͬһ�������ڵ�
	 */
	public void onBatchAsg(){
		TTable table = (TTable)this.getComponent("Table");
		table.acceptText();
		String deptCode = this.getValueString("OP_DEPT_CODE").toString();
		if(deptCode.length()<=0){
			this.messageBox("���������ų�ʱ�����Ҳ���Ϊ��");
			return;
		}

		List<String> deptLst = new ArrayList<String>();
		for(int i = 0; i < table.getRowCount();i++){
			if("Y".equals(table.getItemString(i, "SELECT_FLAG"))){
				deptLst.add(table.getParmValue().getValue("OP_DEPT_CODE",
						i));
			}
		}
		if(deptLst.size()==0){
			this.messageBox("��ѡ��Ҫ�ų̵�����");
			return;
		}
		Set<String> deptSet =new HashSet<String>();
		deptSet.addAll(deptLst);

		//    	for(String s: deptSet){
		//    		this.messageBox(s+"");
		//    	}

		if(deptSet.size()>1){
			this.messageBox("���������ų�ʱ�����ұ���Ϊͬһ����");
			return;
		}
		Iterator<String> it = deptSet.iterator(); 

		while(it.hasNext()){
			String dept = it.next();
			if(!deptCode.equals(dept)){
				this.messageBox("�������Ҳ�һ�£�");
				return;
			}
		}

		TParm data = table.getParmValue(); 
		String OPBOOK_SEQ = "";

		Boolean flg = false;
		for(int i = 0; i < table.getRowCount();i++){
			if("Y".equals(table.getItemString(i, "SELECT_FLAG"))){
				OPBOOK_SEQ += data.getValue("OPBOOK_SEQ",i)+",";
				//У�������Ƿ����ų̣�����״̬Ϊ��0��
				if(!"0".equals(table.getItemString(i, "STATE"))){
					//this.messageBox("���ֻ������ų̻�����������,���ʵ");
					flg = true;
					//return;
				}
			}

		}   	
		String[] opbookSeqs = OPBOOK_SEQ.substring(0,OPBOOK_SEQ.length()-1).split(",");
		//��֤���ų̽���ʱ���Ƿ���opbook_seq����û�в������
		for(String s :opbookSeqs){
			TParm parm = new TParm();
			parm.setData("OPBOOK_SEQ",s);
			TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
			if(result.getErrCode()<0 || result.getCount()<=0){ 
				this.messageBox_("�����ų�ʱ������������������Ϣ:"+s);	              
				return;
			}
		}
		//��֤���ų̽���ͨ����
		this.openDialog("%ROOT%/config/ope/OPEPersonnel.x",OPBOOK_SEQ);
		//�ų���Ϻ�ˢ��ҳ��
		onQuery();

		//this.getTRadioButton("SELECT_ALL").setSelected(false);

		//    	for(int i = 0; i < table.getRowCount();i++){
		//    		if("Y".equals(table.getItemString(i, "SELECT_FLAG"))){
		//    	        String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ",i);
		//    	        this.openDialog("%ROOT%/config/ope/OPEPersonnel.x",OPBOOK_SEQ);
		//    		}
		//    	}
	}
	//20170208 machao end �������������ų�
	public TCheckBox getTRadioButton(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * ����֪��ͬ����
	 */
	public void onConsent(){
//		this.messageBox("�鿴����֪��ͬ����");
		TTable table = (TTable)this.getComponent("Table");
		if(table == null){
			this.messageBox("table is null");
			return;
		}
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("û��ѡ����");
			return;
		}
		TParm parm = table.getParmValue();
		String caseNo = parm.getValue("CASE_NO", row);
		String mrNo = parm.getValue("MR_NO", row);
		String ipdNo = parm.getValue("IPD_NO", row);
		if(caseNo==null || caseNo.trim().length()<=0){
			this.messageBox("סԺ�����Ϊ��");
			return;
		}
		if(mrNo==null || mrNo.trim().length()<=0){
			this.messageBox("������Ϊ��");
			return;
		}
//		if(ipdNo==null || ipdNo.trim().length()<=0){
//			this.messageBox("סԺ��Ϊ��");
//			return;
//		}
		TParm parm1 = new TParm();
		parm1.setData("CASE_NO", caseNo);
		parm1.setData("MR_NO", mrNo);
		parm1.setData("IPD_NO", ipdNo);
		this.openWindow("%ROOT%\\config\\emr\\EMRConsent.x", parm1);	
	}





}


