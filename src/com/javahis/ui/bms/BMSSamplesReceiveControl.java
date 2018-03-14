package com.javahis.ui.bms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.system.textFormat.TextFormatStation;

/**
 * <p>
 * Title: Ѫ��걾����
 * </p>
 *
 * <p>
 * Description: Ѫ��걾����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author yangjj 2015.04.8
 * @version 1.0
 */


public class BMSSamplesReceiveControl extends TControl {
	//�����ı���
	private TTextField BAR_CODE;
	
	//���
	private TTable TABLE;
	
	//���������б�
	private TTextFormat DEPT;
	
	//���������б�
	private TTextFormat STATION;
	
	//�����������б�
	private TTextFormat RECEIVER;
	
	//δȷ�ϵ�ѡ��ť
	private TRadioButton UNCONFIRM;
	
	//��ȷ�ϵ�ѡ��ť
	private TRadioButton CONFIRM;
	
	//ȷ�Ͽ�ʼ����
	private TTextFormat CONFIRM_START_DATE;
	
	//ȷ�Ͻ�������
	private TTextFormat CONFIRM_END_DATE;
	
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();

	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
        initPage();
    }
    
    /**
     * ��ʼ��������
     */
    public void initPage(){
    	//��ʼ������
    	setValue("REGION_CODE", Operator.getRegion());
    	TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		
		//����Ĭ��ѡ��δȷ�ϵ�ѡ��
		UNCONFIRM = (TRadioButton) this.getComponent("UNCONFIRM");
		CONFIRM = (TRadioButton) this.getComponent("CONFIRM");
		UNCONFIRM.setSelected(true);
		 
		//��ʼ��Ĭ�Ͻ�����Ա������
		
		
		//��ʼ������ı���
		BAR_CODE = (TTextField) this.getComponent("BAR_CODE");
		
		//��ʼ�����
		TABLE = (TTable) this.getComponent("TABLE");
		TABLE.removeRowAll();
		
		//��ʼ������
		DEPT = (TTextFormat) this.getComponent("DEPT");
		
		//��ʼ������
		STATION = (TTextFormat) this.getComponent("STATION");
		
		//��ʼ��ȷ�Ͽ�ʼ����
		CONFIRM_START_DATE = (TTextFormat) this.getComponent("CONFIRM_START_DATE");
		CONFIRM_START_DATE.setEnabled(false);
		
		//��ʼ��ȷ�Ͻ�������
		CONFIRM_END_DATE = (TTextFormat) this.getComponent("CONFIRM_END_DATE");
		CONFIRM_END_DATE.setEnabled(false);
		
		//��ʼ��������
		RECEIVER = (TTextFormat) this.getComponent("RECEIVER");
		
		//��ͷ����
		addListener((TTable)this.getComponent("TABLE"));
		
		TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    	
    	BAR_CODE.grabFocus();
    	
    	this.openDialog("%ROOT%\\config\\sys\\SYSOpenAndCloseDialog.x");
    	
    }
    
    //ѡ��δȷ�ϵ�ѡ���¼�
    public void onUnConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    	CONFIRM_START_DATE.setEnabled(false);
    	CONFIRM_START_DATE.setValue("");
    	CONFIRM_END_DATE.setEnabled(false);
    	CONFIRM_END_DATE.setValue("");
    	RECEIVER.setEnabled(false);
    	
    	TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    }
    
    //ѡ����ȷ�ϵ�ѡ���¼�
    public void onConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	Timestamp date = StringTool.getTimestamp(new Date());
    	save.setEnabled(false);
    	CONFIRM_START_DATE.setEnabled(true);
    	CONFIRM_START_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 00:00:00");
    	CONFIRM_END_DATE.setEnabled(true);
    	CONFIRM_END_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 23:59:59");
    	TABLE.removeRowAll();
    	//��ʼ�������ˣ�Ϊ��ǰ����Ա
		setValue("RECEIVER", Operator.getID());
		RECEIVER.setEnabled(true);
    }
    
    //���
    public void onClear(){
    	onInit();
    	DEPT.setValue("");
    	STATION.setValue("");
    	BAR_CODE.setValue("");
    	TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    	setValue("CONFIRM_START_DATE", "");
    	setValue("CONFIRM_END_DATE", "");
    	BAR_CODE.grabFocus();
    }
    
    //ɨ�����ѯ
    public void onBarCodeEnter(){
    	String barCode = BAR_CODE.getValue();
    	if("".equals(barCode)){
    		this.messageBox("��ɨ��Ѫ������");
    		return ;
    	}
    	
    	String sql = getQuerySql();
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	if (sqlParm.getErrCode() < 0) {
    		messageBox(sqlParm.getErrText());
	    	 return;      
	    }
	    if (sqlParm.getCount() <= 0) {
	    	messageBox("��������");
	    	onClear();
	        return;
	    } 
	    
	    TABLE.removeRowAll();
	    try {
	    	BAR_CODE.setValue("");
	    	TABLE.setParmValue(sqlParm);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox(""+e);
		}
	    
    }
    
    //��ѯ
    public void onQuery(){
    	
    	String sql = getQuerySql();
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	if (sqlParm.getErrCode() < 0) {
    		messageBox(sqlParm.getErrText());
	    	 return;      
	    }
	    if (sqlParm.getCount() <= 0) {
	    	messageBox("��������");
	    	TABLE.removeRowAll();
	        return;
	    } 
	    
	    TABLE.removeRowAll();
	    TABLE.setParmValue(sqlParm);
    }
    
    
    //����
    public void onSave(){
    	TABLE.acceptText();
    	if (TABLE.getRowCount() <= 0) {
	    	this.messageBox("����Ҫ���յı걾");
	        return;
	    }
    	
    	TParm parm = new TParm();
    	parm = TABLE.getParmValue();
    	
    	//��Ҫ���յı걾
    	List<TParm> saveParm = new ArrayList<TParm>();
    	for(int i = 0 ; i < parm.getCount() ; i++ ){
    		
    		TParm p = parm.getRow(i);
    		
    		//�ж��Ƿ�ѡ����
    		if("Y".equals(p.getData("CHECKED")+"")){
    			//trueΪ�ñ걾δ���գ�falseΪ�ñ걾�ѽ���
    			boolean s = checkReceive(p);
    			
    			if(!s){
    				this.messageBox("�ñ걾�ѽ���");
    				return ;
    			}
    			saveParm.add(p);
    		}
    	}
    	if(saveParm.size() <= 0){
    		this.messageBox("����Ҫ���յı걾");
	        return;
    	}
    	
    	//�������ȷ��
    	if(!"OK".equals(checkPW()) ){
    		BAR_CODE.grabFocus();
    		return;
    	}
    	String userCode = Operator.getID();
    	for(int i = 0 ; i < saveParm.size() ; i++ ){
    		TParm p = saveParm.get(i);
    		String application_no = p.getValue("BAR_CODE");
    		String sqlUpdate = getUpdateSql(userCode,application_no);
    		TParm result = new TParm(TJDODBTool.getInstance().update(sqlUpdate)) ;
    		if(result.getErrCode() < 0){
    			this.messageBox("����ʧ�ܣ�");
    			return ;
    		}
    	}
    	this.messageBox("���ճɹ���");
    	onClear();
    }
    
    /**
	 * ����������֤
	 * 
	 * @return result
	 */
	public String checkPW() {
		String result = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", "Y");
		return result;
	}
	
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
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
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
				TParm tableData = table.getParmValue();
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
				 //System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

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
		((TTable)this.getComponent("TABLE")).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

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
    
	//�жϱ걾�Ƿ��ѽ��գ�trueΪδ���ܣ�falseΪ�ѽ���
    public boolean checkReceive(TParm parm){
    	String sql = getReceiverSql(parm);
    	TParm sqlParm =  new TParm(TJDODBTool.getInstance().select(sql)) ;
    	return (sqlParm.getCount()<=0 || "".equals(sqlParm.getData("HANDOVER_USER", 0)));
    }
    
    //��ѯSQL
    public String getQuerySql(){
    	String barCode="";//����
		String receiver="";//������
		String dept_code="";//����
		String station_code="";//����
		String confirm_start_time="";//ȷ�Ͽ�ʼʱ��
		String confirm_end_time="";//ȷ�Ͻ���ʱ��
		
		
    	receiver = this.getValueString("RECEIVER");
    	dept_code = this.getValueString("DEPT");
    	station_code = this.getValueString("STATION");
    	confirm_start_time = this.getValueString("CONFIRM_START_DATE");
    	confirm_end_time = this.getValueString("CONFIRM_END_DATE");
    	barCode = this.getValueString("BAR_CODE");
    	
    	
    	String sql = "";
    	
    	sql += " SELECT 'Y' AS CHECKED, "//ѡ��
        + " A.BED_NO AS BED_NO, "//����
        + " A.MR_NO AS MR_NO, "//������
        + " M.PAT_NAME AS NAME, "//����
        + " S.SEX_CODE AS SEX_CODE, "//�Ա�
        + " FLOOR (MONTHS_BETWEEN (SYSDATE, S.BIRTH_DATE) / 12)||'��' AS AGE, "//����
        + " S.BLOOD_TYPE AS BLOOD_TYPE, "//Ѫ��
        + " M.ORDER_DESC AS ORDER_DESC, "//��Ŀ����
        + " M.DEPT_CODE AS DEPT_CODE, "//����
        + " M.STATION_CODE AS STATION_CODE, "//����
        + " M.APPLICATION_NO AS BAR_CODE, "//�����
        + " O.NS_EXEC_CODE_REAL AS EXEC_CODE, "//ִ�л�ʿ
        + " O.NS_EXEC_DATE_REAL AS EXEC_DATE, "//ִ������
        + " M.HANDOVER_USER AS RECEIVE_CODE, "//������
        + " M.HANDOVER_TIME AS RECEIVE_DATE "//��������
        + " FROM MED_APPLY M, "
        + " ADM_INP A, "
        + " SYS_PATINFO S, "
        + " ODI_ORDER B, "
        + " ODI_DSPND O "
        + " WHERE " 
        + " M.CASE_NO = A.CASE_NO "
        + " AND B.CASE_NO = M.CASE_NO "
        + " AND B.ORDER_SEQ = M.SEQ_NO " 
        + " AND B.ORDER_NO = M.ORDER_NO " 
        + " AND A.MR_NO = S.MR_NO " 
        + " AND B.MED_APPLY_NO = M.APPLICATION_NO "
        + " AND B.CAT1_TYPE=M.CAT1_TYPE "
        + " AND B.CASE_NO = O.CASE_NO " 
        + " AND B.ORDER_SEQ = O.ORDER_SEQ " 
        + " AND B.ORDER_NO = O.ORDER_NO " 
        //��������ʱ�ݹرո����ƣ���ʽ�汾Ӧ��
        + " AND M.ORDER_CAT1_CODE='BMS' "
        
        //�����ù���ʱ��
        //+ " AND M.ORDER_DATE > TO_DATE('20150320000000', 'YYYYMMDDHH24MISS')"
        
        
        + " AND M.CAT1_TYPE='LIS' ";
        
    	//����Ų�Ϊ��
    	if(!"".equals(barCode)){
    		sql += " AND M.APPLICATION_NO = '"+barCode+"'";
    		//return sql;
    	}
    	
    	//�����˲�Ϊ��
    	if(!"".equals(receiver)){
    		sql += " AND M.HANDOVER_USER = '"+receiver+"'";
    	}
    	
    	//���Ҳ�Ϊ��
    	if(!"".equals(dept_code)){
    		sql += " AND M.DEPT_CODE = '"+dept_code+"'";
    	}
    	
    	//������Ϊ��
    	if(!"".equals(station_code)){
    		sql += " AND M.STATION_CODE = '"+station_code+"'";
    	}
    	
    	//ȷ�Ͽ�ʼʱ�䲻Ϊ��
    	if(!"".equals(confirm_start_time)){
    		
			sql += " AND M.HANDOVER_TIME > TO_DATE('" + confirm_start_time.replace("-", "").replace(" ", "").replace(".0", "")  + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	//ȷ�Ͻ���ʱ�䲻Ϊ��
    	if(!"".equals(confirm_end_time)){
    		sql += " AND M.HANDOVER_TIME < TO_DATE('" + confirm_end_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	//trueΪѪ���ѽ��գ�falseΪѪ��δ���ܣ�""Ϊ״̬δ֪
    	
    	if(CONFIRM.isSelected()){
    		sql += " AND M.HANDOVER_USER IS NOT NULL";
    	}else if(UNCONFIRM.isSelected()){
    		sql += " AND M.HANDOVER_USER IS NULL";
    	}
    	
    	return sql;
    }
    
    //�����Ƿ��ѽ���SQL
    public String getReceiverSql(TParm parm){
    	String application_no = parm.getData("BAR_CODE")+"";
    	String sql = "SELECT HANDOVER_USER FROM MED_APPLY WHERE CAT1_TYPE='LIS' AND APPLICATION_NO = '"+application_no+"'";
    	return sql;
    }
    
    //���½����˺ͽ�������SQL
    public String getUpdateSql(String user,String application_no){
    	String sql = "";
    	Timestamp date = TJDODBTool.getInstance().getDBTime();
    	String d = date.toString();
    	d = d.substring(0, d.indexOf(".")).replace("-", "").replace(":", "").trim();
    	sql += " UPDATE MED_APPLY "
    		 + " SET HANDOVER_USER = '"+user+"', "
    		 + " HANDOVER_TIME = TO_DATE('" + d + "', 'YYYYMMDDHH24MISS')"
    		 + " WHERE CAT1_TYPE='LIS' AND APPLICATION_NO = '"+application_no+"'";
    	System.out.println("update:"+sql);
    	return sql;
    }
}

                                                                                                                                                                                                                                                                      