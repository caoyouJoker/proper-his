package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import jdo.bms.BMSDeptReceiveTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
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
 * Title: Ѫ������
 * </p>
 *
 * <p>
 * Description: Ѫ������
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
 * @author wangjc 2017.09.20
 * @version 1.0
 */


public class OPEBloodbagReceiveControl extends TControl {
	//���ⵥ�ı���
	private TTextField OUT_NO;
	
	//Ժ�������ı���
	private TTextField BLOOD_NO;
	
	//�Ѻ�������
	private TTextField CHECK_SUM;
	
	//������
	private TTextField SUM;
	
	//���
	private TTable TABLE;
	
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
		this.UNCONFIRM = (TRadioButton) this.getComponent("UNCONFIRM");
		this.CONFIRM = (TRadioButton) this.getComponent("CONFIRM");
		this.UNCONFIRM.setSelected(true);
		
		//��ʼ��Ĭ�Ͻ�����Ա������
		
		
		//��ʼ���ı���
		this.BLOOD_NO = (TTextField) this.getComponent("BLOOD_NO");
		this.OUT_NO = (TTextField) this.getComponent("OUT_NO");
		this.CHECK_SUM = (TTextField) this.getComponent("CHECK_SUM");
		this.SUM = (TTextField) this.getComponent("SUM");
		
		//��ʼ�����
		this.TABLE = (TTable) this.getComponent("TABLE");
		this.TABLE.removeRowAll();
		
		//��ʼ��ȷ�Ͽ�ʼ����
		this.CONFIRM_START_DATE = (TTextFormat) this.getComponent("CONFIRM_START_DATE");
		this.CONFIRM_START_DATE.setEnabled(false);
		
		//��ʼ��ȷ�Ͻ�������
		this.CONFIRM_END_DATE = (TTextFormat) this.getComponent("CONFIRM_END_DATE");
		this.CONFIRM_END_DATE.setEnabled(false);
		
		//��ʼ��������
		this.RECEIVER = (TTextFormat) this.getComponent("RECEIVER");
		
		//��ͷ����
		addListener((TTable)this.getComponent("TABLE"));
		
		TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    }
    
    //ѡ��δȷ�ϵ�ѡ���¼�
    public void onUnConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    	this.CONFIRM_START_DATE.setEnabled(false);
    	this.CONFIRM_START_DATE.setValue("");
    	this.CONFIRM_END_DATE.setEnabled(false);
    	this.CONFIRM_END_DATE.setValue("");
    	this.RECEIVER.setEnabled(false);
    	//OUT_NO.setEnabled(true);
    	//BLOOD_NO.setEnabled(true);
    	
    	this.TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    }
    
    //ѡ����ȷ�ϵ�ѡ���¼�
    public void onConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	Timestamp date = StringTool.getTimestamp(new Date());
    	save.setEnabled(false);
    	//OUT_NO.setValue("");
    	//OUT_NO.setEnabled(false);
    	//BLOOD_NO.setValue("");
    	//BLOOD_NO.setEnabled(false);
    	this.CONFIRM_START_DATE.setEnabled(true);
    	this.CONFIRM_START_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 00:00:00");
    	this.CONFIRM_END_DATE.setEnabled(true);
    	this.CONFIRM_END_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 23:59:59");
    	this.TABLE.removeRowAll();
    	//��ʼ�������ˣ�Ϊ��ǰ����Ա
		setValue("RECEIVER", Operator.getID());
		this.RECEIVER.setEnabled(true);
    }
    
    //���
    public void onClear(){
    	onInit();
    	this.OUT_NO.setEnabled(true);
    	this.BLOOD_NO.setEnabled(true);
    	this.OUT_NO.setValue("");
    	this.BLOOD_NO.setValue("");
    	this.SUM.setValue("");
    	this.CHECK_SUM.setValue("");
    	this.TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    	setValue("CONFIRM_START_DATE", "");
    	setValue("CONFIRM_END_DATE", "");
    	this.OUT_NO.setEnabled(true);
    	this.BLOOD_NO.setEnabled(true);
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    }
    
    //ɨ�����ⵥ��ѯ
    public void onOutNo(){
    	String outNo = this.OUT_NO.getValue();
    	if("".equals(outNo)){
    		this.messageBox("��ɨ����ⵥ����");
    		return ;
    	}
    	String sql = getQuerySql();
//    	System.out.println(">>>>>>>>>>>>>>>>>>>>"+sql);
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
	    if (sqlParm.getCount() <= 0) {
	    	messageBox("��������");
	    	onClear();
	        return;
	    } 
	    
	    this.TABLE.removeRowAll();
	    this.TABLE.setParmValue(sqlParm);
	    this.SUM.setValue(sqlParm.getCount()+"");
	    this.CHECK_SUM.setValue("0");
	    
    }
    
    //ɨ��Ժ�����빴ѡ
    public void onBloodNo(){
    	String bloodNo = this.BLOOD_NO.getValue();
    	
    	if(this.TABLE.getParmValue() == null){
    		this.messageBox("����ɨ����ⵥ���룡");
    		return ;
    	}
    	
    	if("".equals(bloodNo)){
    		this.messageBox("����ɨ��Ժ���룡");
    		return ;
    	}
    	
    	this.TABLE.acceptText();
    	TParm tableParm = this.TABLE.getParmValue();
    	
    	for(int i = 0 ; i <= tableParm.getCount() ; i++){
    		if(i == tableParm.getCount()){
    			this.messageBox("�޴�Ѫ����");
    			this.BLOOD_NO.setValue("");
    			return ;
    		}
    		
    		if(bloodNo.equals(tableParm.getValue("BLOOD_NO", i))){
    			tableParm.setData("SELECT_FLG", i, "Y");
    			this.TABLE.setParmValue(tableParm);
    			setValue("BLOOD_NO", "");
    			break;
    		}
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
	        return;
	    } 
	    
	    this.TABLE.removeRowAll();
	    this.TABLE.setParmValue(sqlParm);
	    int n = 0;
	    for(int i=0; i<sqlParm.getCount(); i++){
	    	if(StringUtils.isNotEmpty(sqlParm.getValue("RECEIVED_USER", i))){
	    		n++;
	    	}
	    }
	    this.CHECK_SUM.setValue(n+"");
	    this.SUM.setValue(sqlParm.getCount()+"");
    }
    
    
    //����
    public void onSave(){
    	this.TABLE.acceptText();
    	if (this.TABLE.getParmValue().getCount() <= 0) {
	    	this.messageBox("����Ҫ���յı걾");
	        return;
	    }
    	
    	TParm parm = new TParm();
    	parm = this.TABLE.getParmValue();
    	
    	//��Ҫ���յı걾
//    	List<TParm> saveParm = new ArrayList<TParm>();
    	TParm saveParm = new TParm();
    	//�������ȷ��
    	TParm checkUserParm = checkPW();
    	if(!"OK".equals(checkUserParm.getValue("RESULT")) ){
    		return;
    	}
    	String userCode = checkUserParm.getValue("USER_ID");
    	int n = 0;
//    	ArrayList<Integer> rows = new ArrayList<Integer>();
    	for(int i = 0 ; i < parm.getCount() ; i++ ){
    		TParm p = parm.getRow(i);
    		//�ж��Ƿ�ѡ����
    		if("Y".equals(p.getData("SELECT_FLG")+"")){
    			//trueΪ�ñ걾δ���գ�falseΪ�ñ걾�ѽ���
    			boolean s = checkReceive(p);
    			if(!s){
    				this.messageBox("�ñ걾�ѽ���");
    				return ;
    			}
    			n++;
        		p.setData("RECEIVED_USER", userCode);
    			saveParm.setRowData(n, p);
    			saveParm.setCount(n);
//    			rows.add(i);
    		}
    	}
    	if(saveParm.getCount() <= 0){
    		this.messageBox("����Ҫ���յı걾");
	        return;
    	}
    	TParm result = TIOM_AppServer.executeAction("action.ope.OPEBloodbagAction", "updateReceive", saveParm);
    	if(result.getErrCode() < 0){
			this.messageBox("����ʧ�ܣ�");
			return ;
		}
    	this.CHECK_SUM.setValue(saveParm.getCount()+"");
    	this.messageBox("���ճɹ���");
    	onOutNo();
//    	for(Integer i : rows){
//    		this.TABLE.removeRow(i);
//    	}
//    	onClear();
    }
    
    /**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public TParm checkPW() {
		String singleExe = "singleExe";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", singleExe);
		return parm;
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
    	return (sqlParm.getCount()<=0 || "".equals(sqlParm.getData("RECEIVED_USER", 0)));
    }
    
    //��ѯSQL
    public String getQuerySql(){
    	String outNo=""; //���ⵥ��
		String receiver="";//������
		String confirm_start_time="";//ȷ�Ͽ�ʼ����
		String confirm_end_time="";//ȷ�Ͻ�������
    	
		outNo = this.OUT_NO.getValue();
    	receiver = this.getValueString("RECEIVER");
    	confirm_start_time = this.getValueString("CONFIRM_START_DATE");
    	confirm_end_time = this.getValueString("CONFIRM_END_DATE");
    	
    	String sql = "";
    	sql +=" SELECT "
    		+ "'N' as SELECT_FLG, "
    		+ " A.ORG_BARCODE AS ORG_BARCODE, "
    		+ " A.BLOOD_NO AS BLOOD_NO, "
    		+ " A.OUT_NO AS OUT_NO, "
    		+ " A.BLD_CODE AS BLD_CODE, "
    		+ " A.BLD_TYPE AS BLD_TYPE, "
    		+ " A.RH_FLG AS RH_FLG, "
    		+ " A.SUBCAT_CODE AS SUBCAT_CODE, " 
    		+ " A.MR_NO AS MR_NO, "
    		+ " C.PAT_NAME AS NAME, "
    		+ " C.SEX_CODE AS SEX, "
    		+ " B.BED_NO AS BED_NO, "
    		+ " FLOOR (MONTHS_BETWEEN (SYSDATE, C.BIRTH_DATE) / 12)||'��' AS AGE, "
    		+ " A.OUT_USER AS OUT_USER, "
    		+ " A.OUT_DATE AS OUT_DATE, " 
    		+ " A.RECEIVED_USER AS RECEIVED_USER, "
    		+ " A.RECEIVED_DATE AS RECEIVED_DATE "
    		+ " FROM "
    		+ " BMS_BLOOD A , " 
    		+ " ADM_INP B , "
    		+ " SYS_PATINFO C "
    		+ " WHERE "
    		+ " A.CASE_NO = B.CASE_NO " 
    		+ " AND B.MR_NO = C.MR_NO ";
    	if(!"".equals(outNo)){
    		sql += " AND OUT_NO = '"+outNo+"' ";
    		//return sql;
    	}
    
    	if(!"".equals(receiver)){
    		sql += " AND A.RECEIVED_USER = '"+receiver+"'";
    	}
    	
    	if(!"".equals(confirm_start_time)){
    		sql += " AND A.RECEIVED_DATE > TO_DATE('" + confirm_start_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	if(!"".equals(confirm_end_time)){
    		sql += " AND A.RECEIVED_DATE < TO_DATE('" + confirm_end_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	
    		
    	if(this.UNCONFIRM.isSelected()){
    		sql += " AND A.RECEIVED_USER IS NULL AND OUT_USER IS NOT NULL";
    	}else if(CONFIRM.isSelected()){
    		sql += " AND A.RECEIVED_USER IS NOT NULL ";
    	}
    	
    	return sql;
    }

   
    
    //�����Ƿ��ѽ���SQL
    public String getReceiverSql(TParm parm){
    	String bloodNo = parm.getData("BLOOD_NO")+"";
    	String sql = "SELECT RECEIVED_USER FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodNo+"'";
    	return sql;
    }
    
}

                                                                                                                                                                                                                                                                      