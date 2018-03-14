/**
 *
 */
package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SYSOperationicdTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

/**
 * 
 * <p>
 * Title: ����ICD
 * </p>
 * 
 * <p>
 * Description:����ICD
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSOperationicdControl extends TControl {
	
	
	//$$=============add by wangjc 2016-01-18 ����������start==================$$//
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
    //$$=============add by wangjc 2016-01-18 ����������end==================$$//


	TParm data;
	int selectRow = -1;
	String icdCode;
	TTable table1;
	TTextFormat tf;
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTABLEClicked");
		init();
		//table1=(TTable) this.getComponent("TABLE1");
		callFunction("UI|TABLE1|addEventListener", "TABLE1->"
				+ TTableEvent.CHANGE_VALUE, this, "onTable1ChangeValue");
		
		table1=(TTable) this.getComponent("TABLE1");
		
		tf = (TTextFormat) table1.getItem("TAG_CODE");
		
		TPanel tp = tf.getPanelPopupMenu();
		tf.setEditValueAction("selectEvent");
		
		this.addListener(this.getTTable("TABLE"));//20160118 wangjc add
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��ʼ�����棬��ѯ���е�����
	 * 
	 * @return TParm
	 */

	public void onQuery() {

		String OPERATION_ICD = getText("OPERATION_ICD");
		String OPT_CHN_DESC = this.getValueString("OPT_CHN_DESC");
		String PY1 = this.getValueString("PY1").toUpperCase();
		String sql = "SELECT OPERATION_ICD,OPT_CHN_DESC,OPT_ENG_DESC,PHA_PREVENCODE,PY1,PY2,"
				+ "SEQ,DESCRIPTION,AVG_IPD_DAY,AVG_OP_FEE,OPE_LEVEL,STA1_CODE,STA1_DESC,"
				+ "OPT_USER,OPT_TERM,OPT_DATE,LAPA_FLG,OPE_INCISION FROM SYS_OPERATIONICD WHERE 1=1 ";
		
		if(OPERATION_ICD != null && !OPERATION_ICD.equals("")){
			sql += " AND OPERATION_ICD = '"+OPERATION_ICD+"' ";
		}
		if(OPT_CHN_DESC != null && !OPT_CHN_DESC.equals("")){
			sql += " AND OPT_CHN_DESC LIKE '%"+OPT_CHN_DESC+"%' ";
		}
		if(PY1 != null && !PY1.equals("")){
			sql += " AND PY1 LIKE '%"+PY1+"%' ";
		}
		sql += " ORDER BY SEQ";
		data = new TParm(TJDODBTool.getInstance().select(sql));
		this.callFunction(
				"UI|TABLE|setParmValue",
				data,
				"SEQ;OPERATION_ICD;OPT_CHN_DESC;PY1;PY2;DESCRIPTION;OPT_ENG_DESC;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;PHA_PREVENCODE;OPT_USER;OPT_DATE;LAPA_FLG;OPE_INCISION");
		
//		if (OPERATION_ICD == null || "".equals(OPERATION_ICD)) {
//			// System.out.println("NULLLLLLLLLLLLLL");
//			init();
//		} else {
//			data = SYSOperationicdTool.getInstance().selectdata(OPERATION_ICD);
//			// System.out.println("result:" + data);
//			if (data.getErrCode() < 0) {
//				messageBox(data.getErrText());
//				return;
//			}
//			this.callFunction(
//							"UI|TABLE|setParmValue",
//							data,
//							"SEQ;OPERATION_ICD;OPT_CHN_DESC;PY1;PY2;DESCRIPTION;OPT_ENG_DESC;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;OPT_USER;OPT_DATE;PHA_PREVENCODE");
//		}
//
	}

	public void onTABLEClicked(int row) {

		 TTable table=(TTable) this.getComponent("TABLE");
		 TParm aa = table.getParmValue().getRow(row);
		if (row < 0) {
			return;
		}
		setTextForParm(
				"OPERATION_ICD;OPT_CHN_DESC;OPT_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;PHA_PREVENCODE",
				data, row);
		//add 20170509 lij ����TABLE��������ѯ��ǻ���������пڷ���
//        setValue("INF_OPESITE",aa.getData("INF_OPESITE"));
        setValue("LAPA_FLG",aa.getData("LAPA_FLG"));
        setValue("OPE_INCISION",aa.getData("OPE_INCISION"));
        
		selectRow = row;
		callFunction("UI|OPERATION_ICD|setEnabled", false);
		//TTable table=(TTable) this.getComponent("TABLE");
		icdCode=this.getValueString("OPERATION_ICD");
        onViewTable1(icdCode);
	}
	
	/**
     * ��ʾtable1
     * @param icdCode
     */
    public void onViewTable1(String icdCode){
    	if("".equals(icdCode))
    		return;
    	String sql="SELECT ICD_CODE,TAG_CODE FROM SYS_OPERATIONICD_TAGS WHERE ICD_CODE='"+icdCode+"'";
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		parm.setData("ICD_CODE",parm.getCount(),icdCode);
    		parm.setData("TAG_CODE",0,parm.getValue("TAG_CODE",0));
    		parm.setCount(parm.getCount("ICD_CODE"));
    	}else{
    		parm.setData("ICD_CODE",0,icdCode);
    		parm.setData("TAG_CODE",0,"");
    		parm.setCount(1);
    	}
    	
    	table1.setParmValue(parm);
    }
    
    /**
     * ����Table1
     */
    public void onTable1ChangeValue(TTableNode tNode){
    	table1.acceptText();
    	TParm parm=table1.getParmValue();
    	int row = tNode.getRow();
    	String columnName = table1.getDataStoreColumnName(tNode.getColumn());
    	
    	if("TAG_CODE".equals(columnName)){
    		if(isExist((String)tNode.getValue(),row)){
    			this.messageBox("�����ظ�ѡ��");
    			//this.messageBox(""+tNode.getOldValue());
    			if(tNode.getOldValue()==null)
    				tNode.setValue("");
    			else
    				tNode.setValue(tNode.getOldValue());
    			return;
    		}
    	}
    	if("TAG_CODE".equals(columnName)&&row==parm.getCount()-1){
    		String oldValue=(String) tNode.getOldValue();
    		String value=(String) tNode.getValue();
    		//this.messageBox("oldValue::"+oldValue+"::::value:::"+value);
    		if(oldValue==null){
    			this.messageBox("oldValue::"+oldValue+"::::value:::"+value);
    			oldValue="";
    		}
    		if(!oldValue.equals(value)&&!"".equals(value)){
	    		int row1=table1.addRow();
	    		parm.setData("ICD_CODE",row1,icdCode);
	    		parm.setData("TAG_CODE",row1,"");
	    		parm.setCount(parm.getCount("ICD_CODE"));
	    		table1.setParmValue(parm);
    		}
    	}
    }
    /**
     * �жϱ���е������Ƿ��ظ�
     * @param tagCode
     * @param row
     * @return
     */
    public boolean isExist(String tagCode,int row){
    	boolean flg=false;
    	TParm parm=table1.getParmValue();
    	for(int i=0;i<parm.getCount();i++){
    		//this.messageBox("i:"+i+":row:"+row+":::tagCode:::"+tagCode+":parmtag_code:"+parm.getValue("TAG_CODE",i));
    		if(i!=row && tagCode.equals(parm.getValue("TAG_CODE",i))){
    			flg=true;
    			break;
    		}
    	}
    	return flg;
    }
    /**
     * ��������ICD����س��¼�
     */
    public void operationIcdEvent(){
    	icdCode=this.getValueString("OPERATION_ICD");
    	onViewTable1(icdCode);
    	
    }
	/**
	 *���
	 */
	public void onClear() {
		// System.out.println("new");

		this.clearValue("OPERATION_ICD;OPT_CHN_DESC;OPT_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;PHA_PREVENCODE;LAPA_FLG;OPE_INCISION");
		// System.out.println("old");
		callFunction("UI|TABLE|removeRowAll");
		//callFunction("UI|TABLE1|removeRowAll");
		this.setValue("SEQ", "0");
		this.setValue("AVG_IPD_DAY", "0");
		this.setValue("AVG_OP_FEE", "0");
		selectRow = -1;
		callFunction("UI|OPERATION_ICD|setEnabled", true);
		table1.setParmValue(new TParm());
	}

	/**
	 * ��ʼ�����棬��ѯ���е�����
	 * 
	 * @return TParm
	 */
	public void init() {
		data = SYSOperationicdTool.getInstance().selectall();
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction(
						"UI|TABLE|setParmValue",
						data,
						"SEQ;OPERATION_ICD;OPT_CHN_DESC;PY1;PY2;DESCRIPTION;OPT_ENG_DESC;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;PHA_PREVENCODE;OPT_USER;OPT_DATE;LAPA_FLG;OPE_INCISION");// add
		//��ʼ��table1																																											// caoyong
		//initTable1();																																											// ��������������
		
	}
	
	/**
	 * �п�����ת������ add caoyong 2013830
	 */
	public String getType(String PHA_PREVENCODE) {
		String phatype = "";
		if ("001".equals(PHA_PREVENCODE)) {
			phatype = "I���п�";
		} else if ("002".equals(PHA_PREVENCODE)) {
			phatype = "II���п�";
		} else if ("003".equals(PHA_PREVENCODE)) {
			phatype = "III���п�";
		}
		return phatype;
	}

	/**
	 * ����
	 */
	public void onInsert() {
		/*if (!this.emptyTextCheck("OPERATION_ICD,OPT_CHN_DESC")) {
			return;
		}*/
		if("".equals(this.getValueString("OPERATION_ICD"))){
			this.messageBox("��������ICD���벻��Ϊ��");
			this.grabFocus("OPERATION_ICD");
			return;
		}
		if("".equals(this.getValueString("OPT_CHN_DESC"))){
			this.messageBox("����˵������Ϊ��");
			this.grabFocus("OPT_CHN_DESC");
			return;
		}

		TParm parm = getParmForTag("OPERATION_ICD;OPT_CHN_DESC;OPT_ENG_DESC;PY1;PY2;SEQ:int;DESCRIPTION;AVG_IPD_DAY:int;AVG_OP_FEE:int;OPE_LEVEL;STA1_CODE;STA1_DESC;PHA_PREVENCODE;LAPA_FLG;OPE_INCISION");// add
		//add lij20170505 ��Ⱦ������λ �� �Ƿ�ǻ������  YΪ�� NΪ����  
//		parm.setData("INF_OPESITE", this.getValueString("INF_OPESITE"));
		if(this.getTCheckBox("LAPA_FLG").isSelected()){
			parm.setData("LAPA_FLG", "Y");
		}else{
			parm.setData("LAPA_FLG", "N");
		}
		//add lij20170509 �пڷ��� 
		parm.setData("OPE_INCISION", this.getValueString("OPE_INCISION"));
		
		parm.setData("PHA_PREVENCODE", this.getValueString("PHA_PREVENCODE"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		SystemTool st = new SystemTool();
		parm.setData("OPT_DATE", st.getDate());
		// System.out.println("pram" + parm);
		data = SYSOperationicdTool.getInstance().insertdata(parm);
		if (data.getErrCode() < 0) {
			this.messageBox(data.getErrText());
			this.messageBox("E0002");
			onClear();
			init();
		} else {
			this.messageBox("P0002");
			onInsertTable1();
			onClear();
			init();
		}
	}
	/**
	 * ����table1������
	 */
	public void onInsertTable1(){
		table1=(TTable) this.getComponent("TABLE1");
		TParm data=table1.getParmValue();
		if(data.getCount()<=1)
			return;
		TParm result=new TParm();
		SystemTool st = new SystemTool();
		for(int i=0;i<data.getCount()-1;i++){
			result.addData("ID", getUid());
			result.addData("ICD_CODE", icdCode);
			result.addData("TAG_CODE", data.getValue("TAG_CODE",i));
			result.addData("OPT_USER", Operator.getID());
			result.addData("OPT_DATE", st.getDate());
			result.addData("OPT_TERM", Operator.getIP());
		}
		result.setCount(data.getCount()-1);
		result=SYSOperationicdTool.getInstance().insertTable1Data(result);
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return;
		}
	}
	
	/**
	 * �������ֵ
	 */
	public String getUid(){
		String sql="SELECT SYS_GUID() ID FROM DUAL";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ID",0);
	}
	
	/**
	 * ��ʼ��table1
	 * @return
	 */
	public void initTable1(){
		String sql="SELECT * FROM SYS_OPERATIONICD_TAGS";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		table1=(TTable) this.getComponent("TABLE1");
		table1.setParmValue(parm);
	}
	/**
	 * ����
	 */
	public void onUpdate() {
		/*if (!this.emptyTextCheck("OPERATION_ICD,OPT_CHN_DESC")) {
			return;
		}*/
		if("".equals(this.getValueString("OPERATION_ICD"))){
			this.messageBox("��������ICD���벻��Ϊ��");
			this.grabFocus("OPERATION_ICD");
			return;
		}
		if("".equals(this.getValueString("OPT_CHN_DESC"))){
			this.messageBox("����˵������Ϊ��");
			this.grabFocus("OPT_CHN_DESC");
			return;
		}
		TParm parm = getParmForTag("SEQ:int;OPERATION_ICD;PY1;PY2;OPT_CHN_DESC;OPT_ENG_DESC;DESCRIPTION;AVG_IPD_DAY:int;AVG_OP_FEE:int;OPE_LEVEL;STA1_CODE;STA1_DESC;PHA_PREVENCODE;LAPA_FLG;OPE_INCISION");// add
																																														// PHA_PREVENCODE
																																														// caoyong
		//add lij20170505 ��Ⱦ������λ �� �Ƿ�ǻ������  YΪ�� NΪ����
//		parm.setData("INF_OPESITE", this.getValueString("INF_OPESITE"));
		if(this.getTCheckBox("LAPA_FLG").isSelected()){
			parm.setData("LAPA_FLG", "Y");
		}else{
			parm.setData("LAPA_FLG", "N");
		}	
		//add lij20170509 �пڷ��� 
		parm.setData("OPE_INCISION", this.getValueString("OPE_INCISION"));
		// 2013830
		// this.messageBox(String.valueOf(Operator.getData()));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		SystemTool st = new SystemTool();
		parm.setData("OPT_DATE", st.getDate());
		// System.out.println("pram" + parm);
		data = SYSOperationicdTool.getInstance().updatedata(parm);
		if (data.getErrCode() < 0) {
			this.messageBox("E0001");
			onClear();
			init();
			return;
		}
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0) {
			this.messageBox("E0001");
			onClear();
			init();
		} else {
			this.messageBox("P0001");
			onInsertTable1();
			onClear();
			init();
		}
		// ����ĩ��ĳ�е�ֵ
		// callFunction("UI|TABLE|setValueAt",getText("POS_DESC"),row,1);
		// callFunction("UI|TABLE|setValueAt",callFunction("UI|POS_TYPE|getSelectedID"),row,2);
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (selectRow == -1) {
			onInsert();
			return;
		}
		onUpdate();
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		// if(selectRow == -1)
		// return;
		
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row<0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		String poscode = getText("OPERATION_ICD");
		if(row>=0){
			if (this.messageBox("ȷ��ɾ��", "ѯ��", 2) == 0) {
				data = SYSOperationicdTool.getInstance().deletedata(poscode);
				data = SYSOperationicdTool.getInstance().deleteTable1Data(poscode);
			} else {
				return;
			}
			
			if (data.getErrCode() < 0) {
				this.messageBox("E0003");
				onClear();
				init();
				return;
			}
		}
		this.messageBox("P0003");
		onClear();
		init();
		//int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		/*if (row < 0) {
			this.messageBox("E0003");
			onClear();
			init();
		} else {
			this.messageBox("P0003");
			onClear();
			init();
		}*/
		// //ɾ��������ʾ
		// this.callFunction("UI|TABLE|removeRow",row);
		// this.callFunction("UI|TABLE|setSelectRow",row);

	}
	/**
	 * ֻɾ����ǩ����
	 */
	public void onDeleteTable1Data(){
		TTable table=(TTable) this.getComponent("TABLE");
		int row = table.getSelectedRow();
		int row1= (Integer) callFunction("UI|TABLE1|getSelectedRow");
		if (row1<0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if(row1>=0){
			if (this.messageBox("ȷ��ɾ��", "ѯ��", 2) == 0) {
				String tagCode = table1.getParmValue().getValue("TAG_CODE",row1);
				String icdCode = table1.getParmValue().getValue("ICD_CODE",row1);
				if(tagCode!=null&&!"".equals(tagCode))
					data = SYSOperationicdTool.getInstance().deleteTable1DataByTagCode(tagCode,icdCode);
			} else {
				return;
			}
			
			if (data.getErrCode() < 0) {
				this.messageBox("E0003");
				onClear();
				init();
				return;
			}
		}
		this.messageBox("P0003");
		onClear();
		init();
		table.setSelectedRow(row);
		onTABLEClicked(row);
	}
	/**
	 * ���ݺ������ƴ������ĸ
	 */
	public void onCode() {
		String desc = this.getValueString("OPT_CHN_DESC");
		String py = TMessage.getPy(desc);
		this.setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	
	public void selectEvent(){
		TTableCellEditor tce = table1.getCellEditor(table1.getSelectedColumn());
		if(table1.getSelectedColumn() == 1 && tf.getComboPopupMenu().isShowing() && tf.getComboValue().length() > 0){
			tce.stopCellEditing();
		}
	}
	
	
	//$$==============add by lx 2012/06/24 ����������start=============$$//
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				
				TParm tableData = getTTable("TABLE").getParmValue();
//				TParm tableData = getTTable("TABLE").getShowParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable("TABLE").setParmValue(parmTable);
		data = getTTable("TABLE").getParmValue();
		// System.out.println("�����===="+parmTable);

	}
	
	
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
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
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	
	public TTable getTTable(String tag){
		return (TTable) this.getComponent(tag);
	}
	//$$==============add by lx 2012/06/24 ����������end=============$$//
	/**
	 * ȡ��TCheckBox�ؼ�
	 * @param checkBoxTag
	 *            String
	 * @return TCheckBox
	 */
	private TCheckBox getTCheckBox(String checkBoxTag) {
		return ((TCheckBox) getComponent(checkBoxTag));
	}
}
