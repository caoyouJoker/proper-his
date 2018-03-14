package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class OPEBloodbagCheckControl extends TControl {
	
	//Ժ�������ı���
	private TTextField BLOOD_NO;
	
	//��Ѫ��
	private TTextField FACT_VOL;
	
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
		this.FACT_VOL = (TTextField) this.getComponent("FACT_VOL");
		
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
    
    /**
     * ѡ��δȷ�ϵ�ѡ���¼�
     */
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
    
    /**
     * ѡ����ȷ�ϵ�ѡ���¼�
     */
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
    
    /**
     * ���
     */
    public void onClear(){
    	onInit();
    	this.BLOOD_NO.setValue("");
    	this.FACT_VOL.setValue("");
    	this.TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    	setValue("CONFIRM_START_DATE", "");
    	setValue("CONFIRM_END_DATE", "");
    	this.BLOOD_NO.setEnabled(true);
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    }
    
    /**
     * ��ѯ
     */
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
    }
    
    
    /**
     * ����
     */
    public void onSave(){
    	this.TABLE.acceptText();
    	if (this.TABLE.getParmValue().getCount() <= 0) {
	    	this.messageBox("����Ҫ�˲������");
	        return;
	    }
    	
    	TParm parm = new TParm();
    	parm = this.TABLE.getParmValue();
    	int row = this.TABLE.getSelectedRow();
    	if(row < 0){
    		this.messageBox("��ѡ��Ҫ�˲������");
    		return;
    	}
    	String factVol = this.getValue("FACT_VOL").toString();
    	if(StringUtils.isEmpty(factVol)){
    		this.messageBox("����д��Ѫ��");
    		return;
    	}
    	Pattern pattern = Pattern.compile("[0-9]*"); 
	    Matcher isNum = pattern.matcher(factVol);
	    if(!isNum.matches()){
	    	this.messageBox("��Ѫ��ӦΪ����");
    		return;
	    }
    	//�������ȷ��
	    String outType = "bloodbagCheck";
    	TParm checkUser1Parm = checkPW(outType);
    	if("FALSE".equals(checkUser1Parm.getValue("RESULT"))){
    		return;
    	}
    	if(!"OK".equals(checkUser1Parm.getValue("RESULT"))){
    		this.messageBox(checkUser1Parm.getValue("USER_ID")+"�����������");
    		return;
    	}
    	
    	String userCode1 = checkUser1Parm.getValue("USER_ID");
    	String bldtransEndTime = checkUser1Parm.getValue("DATE");
    	outType = "singleExe";
    	TParm checkUser2Parm = checkPW(outType);
    	if("FALSE".equals(checkUser2Parm.getValue("RESULT"))){
    		return;
    	}
    	if(!"OK".equals(checkUser2Parm.getValue("RESULT")) ){
    		this.messageBox(checkUser2Parm.getValue("USER_ID")+"�����������");
    		return;
    	}
    	String userCode2 = checkUser2Parm.getValue("USER_ID");
    	TParm saveParm = parm.getRow(row);
    	saveParm.setData("BLDTRANS_END_USER", userCode1);
    	saveParm.setData("BLDTRANS_END_TIME", bldtransEndTime.substring(0, 19).replace("-", "/"));
    	saveParm.setData("CHECK_USER", userCode2);
    	saveParm.setData("FACT_VOL", this.getValue("FACT_VOL"));
    	TParm result = TIOM_AppServer.executeAction("action.ope.OPEBloodbagAction", "updateRecheck", saveParm);
    	if(result.getErrCode() < 0){
			this.messageBox("�˲�ʧ�ܣ�");
			return ;
		}
    	this.messageBox("�˲�ɹ���");
    	onClear();
    }
    
    /**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public TParm checkPW(String outType) {
//		String singleExe = "singleExe";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\ope\\passWordCheck.x", outType);
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
    
	/**
	 * �жϱ걾�Ƿ��ѽ��գ�trueΪδ���ܣ�falseΪ�ѽ���
	 * @param parm
	 * @return
	 */
    public boolean checkReceive(TParm parm){
    	String sql = getReceiverSql(parm);
    	TParm sqlParm =  new TParm(TJDODBTool.getInstance().select(sql)) ;
    	return (sqlParm.getCount()<=0 || "".equals(sqlParm.getData("RECEIVED_USER", 0)));
    }
    
    /**
     * ��ѯSQL
     * @return
     */
    public String getQuerySql(){
		//������
		String receiver = this.getValueString("RECEIVER");
		//�˲鿪ʼ����
		String confirm_start_time = this.getValueString("CONFIRM_START_DATE");
		//�˲��������
		String confirm_end_time = this.getValueString("CONFIRM_END_DATE");
    	String bloodNo = this.getValueString("BLOOD_NO");
    	String sql = "";
    	sql +=" SELECT "
    		+ " A.ORG_BARCODE, "
    		+ " A.BLOOD_NO, "
    		+ " A.OUT_NO, "
    		+ " A.BLD_CODE, "
    		+ " A.BLD_TYPE, "
    		+ " A.RH_FLG, "
    		+ " A.SUBCAT_CODE, " 
    		+ " A.MR_NO, "
    		+ " C.PAT_NAME AS NAME, "
    		+ " C.SEX_CODE AS SEX, "
    		+ " B.BED_NO, "
    		+ " FLOOR (MONTHS_BETWEEN (SYSDATE, C.BIRTH_DATE) / 12)||'��' AS AGE, "
    		+ " A.OUT_USER, "
    		+ " A.OUT_DATE, " 
    		+ " A.FACT_VOL, " 
    		+ " A.RECHECK_USER, "
    		+ " A.CHECK_USER, "
    		+ " A.CHECK_DATE, "
    		+ " A.BLDTRANS_END_USER, "
    		+ " A.BLDTRANS_END_TIME, "
    		+ " A.RECHECK_TIME "
    		+ " FROM "
    		+ " BMS_BLOOD A , " 
    		+ " ADM_INP B , "
    		+ " SYS_PATINFO C "
    		+ " WHERE "
    		+ " A.CASE_NO = B.CASE_NO " 
    		+ " AND B.MR_NO = C.MR_NO ";
    	if(!"".equals(bloodNo)){
    		sql += " AND A.BLOOD_NO = '"+bloodNo+"' ";
    	}
    
    	if(!"".equals(receiver)){
    		sql += " AND A.BLDTRANS_END_USER = '"+receiver+"'";
    	}
    	
    	if(!"".equals(confirm_start_time)){
    		sql += " AND A.BLDTRANS_END_TIME > TO_DATE('" + confirm_start_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	if(!"".equals(confirm_end_time)){
    		sql += " AND A.BLDTRANS_END_TIME < TO_DATE('" + confirm_end_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	
    		
    	if(this.UNCONFIRM.isSelected()){
    		sql += " AND A.BLDTRANS_END_USER IS NULL AND RECEIVED_USER IS NOT NULL";
    	}else if(CONFIRM.isSelected()){
    		sql += " AND A.BLDTRANS_END_USER IS NOT NULL ";
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

                                                                                                                                                                                                                                                                      