package com.javahis.ui.hrm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jdo.hrm.HRMRecruit;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ��������ļ�� </p>
 *
 * <p> Description: ��������ļ��Ϣ���뵼�� </p>
 *
 * <p> Copyright: Copyright (c) 2016 </p>
 *
 * <p> Company: ProperSoft </p>
 *
 * @author guangl 20160308
 * @version 1.0
 */
public class HRMRecruitControl extends TControl {
	
	private final static int[] li_SecPosValue = { 1601, 1637, 1833, 2078, 2274,  
        2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,  
        4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590 };  
	private final static String[] lc_FirstLetter = { "a", "b", "c", "d", "e",  
        "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",  
        "t", "w", "x", "y", "z" };  
	
	private TTextFormat contractCode;
	
	
	private int insertrow;
	
	//�����
	private TTable table;
	
	//TDataStore����
	private HRMRecruit recruit;
	
	String contractcode;
	
	private Map<String,Integer> seq = new HashMap<String , Integer>();
	

	/**
	 * ���췽��
	 */
	public HRMRecruitControl() {
	}
	
	/**
	 * ��ʼ������
	 */
	public void  initPage(){
		contractCode = (TTextFormat) getComponent("CONTRACT_CODE");
		
		table = (TTable) getComponent("TABLE");
		//�������ֵ�ı��¼�
		table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this, "onTableChange");
		recruit = new HRMRecruit();
		table.setDataStore(recruit);
	}
	
	/**
	 * ��ʼ������
	 * 
	 */
	public void initData(){
		freshTextFormat();
		//��ѯ���datastore
		recruit.onQuery();
		//����һ����������
		insertrow = recruit.insertRow();
		recruit.setItem(insertrow, "CONTRACT_CODE", "������������");
		recruit.setItem(insertrow, "SEQ", null);
		recruit.onNew(insertrow);
		//��ʾ
		table.setDSValue();
		
		seq = new HashMap<String,Integer>();
		this.setValue("TOT_COUNT", String.valueOf(recruit.rowCount() - 1));
	}
	
	@Override
	public void onInit() {
		super.onInit();
		initPage();
		initData();
		
	}
	
	/**
	 * ���ӡ����º�ɾ�������������ռ������
	 */
	public void freshTextFormat(){
		contractCode.onQuery();
	}

	/**
	 * ������ļ��Ϣ
	 */
	public void onSave(){
		table.acceptText();
		//���δ�����У���Ԥ���Ŀ���ɾ������ִ�и���
		if("������������".equals(recruit.getItemData(insertrow, "CONTRACT_CODE"))){
			if(!checkInsertRow()){
				return;
			}
			recruit.deleteRow(insertrow);
			if(!recruit.update()){
				messageBox("����ʧ��");
				return;
			}
			
			table.setDSValue();
			messageBox("����ɹ���");
			//�����������Ŀ��мӻ���
			onQuery();
			return;
		}
		
		//���������Ϣ�걸������ʣ��������ֶ�
		recruit.onNew(insertrow);
		if(!checkInsertRow()){
//			initData();
			return;
		}
		
		if(!recruit.update()){
			messageBox("����ʧ�ܣ�");
			
		}else{
			table.setDSValue();
			messageBox("����ɹ���");
		}
		
		onQuery();
	}
	
	/**
	 * У���Ҫ��Ϣ
	 * @return
	 */
	public boolean checkInsertRow(){
		int count = table.getRowCount();
		for (int i = 0; i < count; i++) {
			if("������������".equals(table.getItemString(i, "CONTRACT_CODE"))){
				continue;
			}
			
			if ("".equals(table.getItemString(i, "CONTRACT_DESC"))) {
				messageBox("��" + (i + 1) + "��δ��д�������ƣ��벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "PAT_NAME"))) {
				messageBox("��" + (i + 1) + "������������Ϊ��Ҫ��Ϣ���벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "IDNO"))
					&& "N".equals(recruit.getItemString(insertrow,
							"FOREIGNER_FLG"))) {
				messageBox("��" + (i + 1) + "�����֤��Ϊ��Ҫ��Ϣ���벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "SEX_CODE"))) {
				messageBox("��" + (i + 1) + "���Ա�Ϊ��Ҫ��Ϣ���벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "BIRTH_DATE"))) {
				messageBox("��" + (i + 1) + "������������ڣ������Ҫ���¼��㣬������������ȷ�����֤���롣");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "CELL_PHONE"))) {
				messageBox("��" + (i + 1) + "����ϵ�绰Ϊ��Ҫ��Ϣ���벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if (table.getItemDouble(i, "HEIGHT") == 0.0) {
				messageBox("��" + (i + 1) + "�����Ϊ��Ҫ��Ϣ�������޷�����BMI���벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if (table.getItemDouble(i, "WEIGHT") == 0.0) {
				messageBox("��" + (i + 1) + "������Ϊ��Ҫ��Ϣ�������޷�����BMI���벹ȫ���ٱ��档");
				table.setSelectedRow(i);
				return false;
			}
			if (table.getItemDouble(i, "BMI") == 0.0) {
				messageBox("��" + (i + 1) + "������BMI�������Ҫ���¼��㣬����������������ء�");
				table.setSelectedRow(i);
				return false;
			}
		}
		return true;
	}

	/**
	 * ��ѯ��ļ��Ϣ
	 */
	public void onQuery(){
		String contractcode = this.getValueString("CONTRACT_CODE");
		String patname = this.getValueString("PAT_NAME");
		String idno = this.getValueString("ID_NO");
		String tel = this.getValueString("TEL");
		String sex = this.getValueString("SEX");
		
		String filter = " 1 = 1 ";
		if(!"".equals(patname)){
			filter += "AND PAT_NAME LIKE '%" + patname + "%' ";
		}
		if(!"".equals(contractcode)){
			filter += "AND CONTRACT_CODE = '" + contractcode + "' ";
		}
		if(!"".equals(idno)){
			filter += "AND IDNO = '" + idno + "' ";
		}
		
		if(StringUtils.isNotEmpty(tel)) {
			filter += "AND CELL_PHONE LIKE '%" + tel + "%' ";
		}
		if (StringUtils.isNotEmpty(sex)) {
			filter += "AND SEX_CODE = '" + sex + "' ";
		}
		
		recruit.onQuery();
		recruit.setFilter(filter);
		recruit.filter();
		table.setDSValue();
		
		this.setValue("TOT_COUNT", String.valueOf(recruit.rowCount()));
		
		insertrow = recruit.insertRow();
		recruit.setItem(insertrow, "CONTRACT_CODE", "������������");
		recruit.setItem(insertrow, "SEQ", null);
		recruit.onNew(insertrow);
		//��ʾ
		table.setDSValue();
	}
	
	/**
	 * ɾ�������Ŀ
	 */
	public void onDelete(){
		int row = table.getSelectedRow();
		if(row < 0){
			messageBox("δѡ�м�¼");
			return;
		}
		
		if (this.messageBox("��ʾ", "�Ƿ�ɾ����������?", 2) == 0) {
			recruit.deleteRow(insertrow);
			recruit.deleteRow(row);
			recruit.update();
			table.setDSValue();
			recruit.resetModify();
			onQuery();
			this.messageBox("ɾ���ɹ�");
		}
	}
	
	/**
	 * ��ղ�ѯ������������
	 */
	public void onClear(){
		this.clearValue("CONTRACT_CODE;PAT_NAME;ID_NO;TEL;SEX;TOT_COUNT");
		initData();
	}
	
	/**
	 * ����Excel
	 */
	public void onExport(){
		if(this.messageBox("ע�⣡","��ȷ���ڵ���Excelǰִ�й����������������ɵ������������ݿⲻһ�¡�",this.OK_CANCEL_OPTION)==this.CANCEL_OPTION){
			return ;
		}
		if("������������".equals(recruit.getItemData(insertrow, "CONTRACT_CODE"))){
			recruit.deleteRow(insertrow);
			recruit.update();
			table.setDSValue();
			ExportExcelUtil.getInstance().exportExcel(table, "�ٴ���ļ��Ϣ��");
			return ;
		}
		//���⽫���һ�д�ӡ�������
//		recruit.deleteRow(insertrow);
//		recruit.update();
//		table.setDSValue();
		ExportExcelUtil.getInstance().exportExcel(table, "�ٴ���ļ��Ϣ��");
		initData();
	}
	
	/**
	 * ����Excel
	 * @throws ParseException 
	 */
	public void onImport() throws ParseException {
		HashSet idno = new HashSet();
		// �ֶ�������
		int addRow = insertrow;
		// ��ʾ��Ϣ
		StringBuilder errMsg = new StringBuilder();

		// ���ڸ�ʽ����
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		format.setLenient(false);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {// ����xls�ļ�
					public boolean accept(File f) {
						if (f.isDirectory()) {// �����ļ���
							return true;
						}
						return f.getName().endsWith(".xls");
					}

					public String getDescription() {
						return ".xls";
					}
				});
		// ���ļ�ѡ��Ի��򣬲���¼״̬��
		int option = fileChooser.showOpenDialog(null);
		File file = fileChooser.getSelectedFile();
		// ��Excel�е����ݴ����parm��
		TParm parm = new TParm();
		// ����򿪶Ի����״̬��Ϊ�����ܡ���yes����ok�İ�ť������
		if (option == JFileChooser.APPROVE_OPTION) {
			// ������ѡ�ļ��Ķ���
			try {
				// ���������������°���Sheet������
				Workbook wb = Workbook.getWorkbook(file);
				Sheet st = wb.getSheet(0);
				// �õ�����ж��ٷǿ���
				int row = getRightRows(st);
				// �õ�����
				int column = st.getColumns();
				if (row <= 1 || column <= 0) {
					this.messageBox("excel��û������");
					return;
				}
				// �к�
				ArrayList<Integer> indexList = new ArrayList<Integer>();
				// �б���
				ArrayList<String> titleList = new ArrayList<String>();
				for (int j = 0; j < column; j++) {
					// ��һ���б��⣬�ڶ���Ϊ��ͷ��getCell��һ����������������
					String cell = st.getCell(j, 1).getContents().trim();
					if (cell.indexOf("�������") != -1) {
						indexList.add(j);
						titleList.add("CONTRACT_CODE");
						continue;
					}
					if (cell.indexOf("��������") != -1) {
						indexList.add(j);
						titleList.add("CONTRACT_DESC");
						continue;
					}
					if (cell.indexOf("����״̬") != -1) {
						indexList.add(j);
						titleList.add("INGROUP_FLG");
						continue;
					}
					if (cell.indexOf("����������") != -1) {
						indexList.add(j);
						titleList.add("PAT_NAME");
						continue;
					}
					if (cell.indexOf("������д") != -1) {
						indexList.add(j);
						titleList.add("PY1");
						continue;
					}
					if (cell.indexOf("�Ա�") != -1) {
						indexList.add(j);
						titleList.add("SEX_CODE");
						continue;
					}
					if (cell.indexOf("����") != -1) {
						indexList.add(j);
						titleList.add("SPECIES_CODE");
						continue;
					}
					if (cell.indexOf("���֤��") != -1) {
						indexList.add(j);
						titleList.add("IDNO");
						continue;
					}
					if (cell.indexOf("��������") != -1) {
						indexList.add(j);
						titleList.add("BIRTH_DATE");
						continue;
					}
					if (cell.indexOf("��ϵ�绰") != -1) {
						indexList.add(j);
						titleList.add("CELL_PHONE");
						continue;
					}
					if (cell.indexOf("���") != -1) {
						indexList.add(j);
						titleList.add("HEIGHT");
						continue;
					}
					if (cell.indexOf("����") != -1) {
						indexList.add(j);
						titleList.add("WEIGHT");
						continue;
					}
					if (cell.indexOf("BMI") != -1) {
						indexList.add(j);
						titleList.add("BMI");
						continue;
					}
					if (cell.indexOf("����ʷ") != -1) {
						indexList.add(j);
						titleList.add("ALLERGY_FLG");
						continue;
					}
					if (cell.indexOf("�����Ƿ���Ѫ") != -1) {
						indexList.add(j);
						titleList.add("BLOOD_FLG");
						continue;
					}
					if (cell.indexOf("��ʷ") != -1) {
						indexList.add(j);
						titleList.add("MEDHISTORY_FLG");
						continue;
					}
					if (cell.indexOf("ְҵ") != -1) {
						indexList.add(j);
						titleList.add("OCC_CODE");
						continue;
					}
					if (cell.indexOf("��ϸ��ַ") != -1) {
						indexList.add(j);
						titleList.add("ADDRESS");
						continue;
					}
					if (cell.indexOf("����ʱ��") != -1) {
						indexList.add(j);
						titleList.add("INGROUP_DATE");
						continue;
					}
					if (cell.indexOf("����ʱ��") != -1) {
						indexList.add(j);
						titleList.add("OUTGROUP_DATE");
						continue;
					}
					if (cell.indexOf("δ����ԭ��") != -1) {
						indexList.add(j);
						titleList.add("OUTGROUP_REASON");
						continue;
					}
					if (cell.indexOf("������ϵ��") != -1) {
						indexList.add(j);
						titleList.add("CONTACT_PERSON");
						continue;
					}
					if (cell.indexOf("��ϵ��ʽ") != -1) {
						indexList.add(j);
						titleList.add("CONTACT_TEL");
						continue;
					}
					if (cell.indexOf("����״̬") != -1) {
						indexList.add(j);
						titleList.add("SIGN_FLG");
						continue;
					}
					if (cell.indexOf("��ע") != -1) {
						indexList.add(j);
						titleList.add("DESCRIPTION");
						continue;
					}
				}// �����������Ϣ
				// ����ͳ��
				column = indexList.size();
				// ��ʱ���ж�Excel�Ƿ�ṹ����
				// �Ա��������֤�Ž�����д�ģ���ʱ���ӵ�Excel�л�ȡ����

				int count = 0;
				Cell cell;
				String cellContents = "";
				DateCell dc;
				
				// һ��һ�м���excel�е����ݣ���һ���Ǳ�����ƣ��ڶ����Ǳ�ͷ���ӵ����п�ʼѭ��
				for (int i = 2; i < row; i++) {
					for (int j = 0; j < column; j++) {
						cell = st.getCell(j, i);
						if (cell.getType() == CellType.DATE) {
							dc = (DateCell) cell;
							cellContents = StringTool.getString(dc.getDate(), "yyyy/MM/dd");
						} else {
							cellContents = st.getCell(j, i).getContents();
						}
						parm.addData(titleList.get(j), cellContents);
					}
					count = parm.getCount("PAT_NAME");
					// ����Boolean������
					// ����ʷ
					if (parm.getData("ALLERGY_FLG", count - 1).equals("��")) {
						parm.setData("ALLERGY_FLG", count - 1, "N");
					} else if (parm.getData("ALLERGY_FLG", count - 1).equals(
							"��")) {
						parm.setData("ALLERGY_FLG", count - 1, "Y");
					}
					// �Ƿ���Ѫ
					if (parm.getData("BLOOD_FLG", count - 1).equals("��")) {
						parm.setData("BLOOD_FLG", count - 1, "N");
					} else if (parm.getData("BLOOD_FLG", count - 1).equals("��")) {
						parm.setData("BLOOD_FLG", count - 1, "Y");
					}
					// ��ʷ
					if (parm.getData("MEDHISTORY_FLG", count - 1).equals("��")) {
						parm.setData("MEDHISTORY_FLG", count - 1, "N");
					} else if (parm.getData("MEDHISTORY_FLG", count - 1)
							.equals("��")) {
						parm.setData("MEDHISTORY_FLG", count - 1, "Y");
					}
					// ����״̬
					if (parm.getData("INGROUP_FLG", count - 1).equals("δ����")) {
						parm.setData("INGROUP_FLG", count - 1, "N");
					} else if (parm.getData("INGROUP_FLG", count - 1).equals(
							"����")) {
						parm.setData("INGROUP_FLG", count - 1, "Y");
					}
					// ����״̬
					if (parm.getData("SIGN_FLG", count - 1).equals("��")) {
						parm.setData("SIGN_FLG", count - 1, "N");
					} else if (parm.getData("SIGN_FLG", count - 1).equals("��")) {
						parm.setData("SIGN_FLG", count - 1, "Y");
					}

					parm.setCount(count);
				}
			} catch (BiffException e) {
				this.messageBox_("excel�ļ���������");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				this.messageBox_("���ļ�����");
				e.printStackTrace();
				return;
			}
		} else {
			return;
		}
		
		int row = recruit.rowCount() - 1;
		Timestamp in = null;
		Timestamp out = null;
		TParm parmRow = new TParm();
		String contractCode = "";
		String contractDesc = "";
		String patName = "";
		String py = "";
		String idNo = "";
		String checkId = "";
		String sexCode = "";
		String birthDateFromIdNo = "";
		TParm idResult = new TParm();

		for (int i = 0; i < parm.getCount(); i++) {
			// ȡ��ÿһ�е�Parm
			parmRow = parm.getRow(i);

			contractCode = parmRow.getValue("CONTRACT_CODE");
			contractDesc = parmRow.getValue("CONTRACT_DESC");

			if ("".equals(contractCode)) {
				this.messageBox("���󣺵�" + (i + 3) + "�з������δ��д�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "�з������δ��д�����������Զ�����������¼��\r\n");
				continue;
			}

			if ("".equals(contractDesc)) {
				this.messageBox("���󣺵�" + (i + 3) + "�з�������δ��д�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "�з�������δ��д�����������Զ�����������¼��\r\n");
				continue;
			}

			patName = parmRow.getValue("PAT_NAME");
			if ("".equals(patName)) {
				this.messageBox("���󣺵�" + (i + 3) + "������δ��д�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "������δ��д�����������Զ�����������¼��\r\n");
				continue;
			}

			// modify by wangb 2016/08/26 ����һ���ٴ��������ԣ�ƴ�����û��ֶ�����
			// �Զ���дƴ��
			py = parmRow.getData("PY1").toString();
			if (StringUtils.isEmpty(py)) {
				py = this.getAllFirstLetter(
						parmRow.getData("PAT_NAME").toString()).toUpperCase();
			}

			// �Զ��������֤����д�Ա������
			idNo = parmRow.getValue("IDNO");
			if (StringUtils.isEmpty(idNo)) {
				this.messageBox("���󣺵�" + (i + 3) + "�����֤����δ��д�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "�����֤����δ��д�����������Զ�����������¼��\r\n");
				continue;
			} else {
				checkId = checkID(idNo);
				if (!checkId.equals("TRUE") && !checkId.equals("FALSE")) {
					this.messageBox("���󣺵�" + (i + 3) + "�����֤" + idNo
							+ "У��λ�������һλӦ��Ϊ" + checkId.substring(17, 18) + "�����������Զ�����������¼��");
					errMsg.append("���󣺵�" + (i + 3) + "�����֤" + idNo
							+ "У��λ�������һλӦ��Ϊ" + checkId.substring(17, 18)
							+ "�����������Զ�����������¼��\r\n");
					continue;
				}

				if (!isId(idNo)) {
					this.messageBox("���󣺵�" + (i + 3) + "�����֤" + idNo + "����ȷ�����������Զ�����������¼��");
					errMsg.append("���󣺵�" + (i + 3) + "�����֤" + idNo + "����ȷ�����������Զ�����������¼��\r\n");
					continue;
				}

				sexCode = StringTool.isMaleFromID(parmRow.getData("IDNO")
						.toString());
				if (!"1".equals(sexCode) && !"2".equals(sexCode)) {// ������֤������
					this.messageBox("��ʾ����" + (i + 3)
							+ "�����֤����д�����⣬δ����ȷ����Ա𣬱��������Զ�����������¼��");
					errMsg.append("��ʾ����" + (i + 3)
							+ "�����֤����д�����⣬δ����ȷ����Ա𣬱��������Զ�����������¼��\r\n");
					continue;
				}
				
				// ���֤�����Ա���������д���Ա�У��
				if (("1".equals(sexCode) && !"��".equals(parmRow
						.getValue("SEX_CODE")))
						|| ("2".equals(sexCode) && !"Ů".equals(parmRow
								.getValue("SEX_CODE")))) {
					this.messageBox("���󣺵�" + (i + 3)
							+ "����д���Ա������֤���е��Ա𲻷������������Զ�����������¼��");
					errMsg.append("���󣺵�" + (i + 3)
							+ "����д���Ա������֤���е��Ա𲻷������������Զ�����������¼��\r\n");
					continue;
				}
				
				// ���֤���г���������������д������У��
				birthDateFromIdNo = StringTool.getBirdayFromID(
						parmRow.getValue("IDNO")).toString().substring(0, 10).replace("-", "/");
				if (!parmRow.getValue("BIRTH_DATE").equals(birthDateFromIdNo)) {
					this.messageBox("���󣺵�" + (i + 3)
							+ "����д�ĳ������������֤���еĳ������ڲ��������������Զ�����������¼��");
					errMsg.append("���󣺵�" + (i + 3)
							+ "����д�ĳ������������֤���еĳ������ڲ��������������Զ�����������¼��\r\n");
					continue;
				}
				
				// ��֤excel���Ƿ������ͬ�����֤��
				if (!idno.add(idNo)) {
					this.messageBox("���󣺵�" + (i + 3) + "�����֤��" + idNo
							+ "�Ѵ����ڱ�Excel�У����������Զ�����������¼��");
					errMsg.append("���󣺵�" + (i + 3) + "�����֤��" + idNo
							+ "�Ѵ����ڱ�Excel�У����������Զ�����������¼��\r\n");
					continue;
				}

				String idSQL = "SELECT CONTRACT_CODE "
						+ "FROM HRM_RECRUIT WHERE IDNO = '" + idNo + "'";
				// ��ѯ���ݿ����Ƿ��и����֤������
				idResult = new TParm(TJDODBTool.getInstance().select(idSQL));
				
				if (idResult.getCount() > 0) {
					// ��ͬ�����²��ܴ�����ͬ���֤�ŵ�����
					if (idResult.getValue("CONTRACT_CODE").contains(
							contractCode)) {
						this.messageBox("���󣺵�" + (i + 3) + "�����֤��" + idNo
								+ "����ͬ�ķ��������Ѵ��������ݿ��У����������Զ�����������¼��");
						errMsg.append("���󣺵�" + (i + 3) + "�����֤��" + idNo
								+ "����ͬ�ķ��������Ѵ��������ݿ��У����������Զ�����������¼��\r\n");
						continue;
					}
				}
			}

			if (StringUtils.isEmpty(parmRow.getValue("CELL_PHONE"))) {
				this.messageBox("���󣺵�" + (i + 3) + "�е绰����δ��д�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "�е绰����δ��д�����������Զ�����������¼��\r\n");
				continue;
			}

			if (StringUtils.isEmpty(parmRow.getValue("HEIGHT"))
					|| parmRow.getDouble("HEIGHT") == 0.0) {
				this.messageBox("���󣺵�" + (i + 3) + "�����Ϊ�ջ�Ϊ0�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "�����Ϊ�ջ�Ϊ0�����������Զ�����������¼��\r\n");
				continue;
			}

			if (StringUtils.isEmpty(parmRow.getValue("WEIGHT"))
					|| parmRow.getDouble("WEIGHT") == 0.0) {
				this.messageBox("���󣺵�" + (i + 3) + "������Ϊ�ջ�Ϊ0�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "������Ϊ�ջ�Ϊ0�����������Զ�����������¼��\r\n");
				continue;
			}

			if (StringUtils.isEmpty(parmRow.getValue("BMI"))
					|| parmRow.getDouble("BMI") == 0.0) {
				this.messageBox("���󣺵�" + (i + 3) + "��BMIΪ�ջ�Ϊ0�����������Զ�����������¼��");
				errMsg.append("���󣺵�" + (i + 3) + "��BMIΪ�ջ�Ϊ0�����������Զ�����������¼��\r\n");
				continue;
			}

			// Ҫ��Ԫ���ʽ����Ϊyyyy/mm/dd�����޷�ת�����ؼ���
			try {
				in = null;
				out = null;
				if (StringUtils.isNotEmpty(parmRow.getValue("INGROUP_DATE"))) {
					in = new Timestamp(format.parse(
							parmRow.getValue("INGROUP_DATE")).getTime());
				}
				if (StringUtils.isNotEmpty(parmRow.getValue("OUTGROUP_DATE"))) {
					out = new Timestamp(format.parse(
							parmRow.getValue("OUTGROUP_DATE")).getTime());
				}
			} catch (ParseException e) {
				this.messageBox("��ʾ����" + (i + 3) + "�г�����ʱ���ʽ����ȷ�����������Զ�����������¼��");
				errMsg.append("��ʾ����" + (i + 3) + "�г�����ʱ���ʽ����ȷ�����������Զ�����������¼��\r\n");
				continue;
			}

			// ��ʼ�����������
			row = recruit.insertRow();
			insertrow = row;
			recruit.setItem(row, "CONTRACT_CODE", contractCode);
			recruit.setItem(row, "CONTRACT_DESC", contractDesc);
			recruit.setItem(row, "PAT_NAME", patName);
			recruit.setItem(row, "PY1", py);
			recruit.setItem(row, "IDNO", idNo);
			recruit.setItem(row, "SEX_CODE", sexCode);
			recruit.setItem(row, "BIRTH_DATE", birthDateFromIdNo);
			recruit.setItem(row, "SPECIES_CODE", parmRow
					.getData("SPECIES_CODE"));
			recruit.setItem(row, "CELL_PHONE", parmRow.getValue("CELL_PHONE"));
			recruit.setItem(row, "ADDRESS", parmRow.getData("ADDRESS"));
			recruit.setItem(row, "HEIGHT", parmRow.getData("HEIGHT"));
			recruit.setItem(row, "WEIGHT", parmRow.getData("WEIGHT"));
			recruit.setItem(row, "BMI", parmRow.getData("BMI"));
			recruit.setItem(row, "OCC_CODE", parmRow.getData("OCC_CODE"));
			recruit.setItem(row, "ALLERGY_FLG", parmRow.getData("ALLERGY_FLG"));
			recruit.setItem(row, "BLOOD_FLG", parmRow.getData("BLOOD_FLG"));
			recruit.setItem(row, "MEDHISTORY_FLG", parmRow
					.getData("MEDHISTORY_FLG"));
			recruit.setItem(row, "INGROUP_DATE", in);
			recruit.setItem(row, "OUTGROUP_DATE", out);
			recruit.setItem(row, "INGROUP_FLG", parmRow.getData("INGROUP_FLG"));
			recruit.setItem(row, "OUTGROUP_REASON", parmRow
					.getData("OUTGROUP_REASON"));
			recruit.setItem(row, "CONTACT_PERSON", parmRow
					.getData("CONTACT_PERSON"));
			recruit.setItem(row, "CONTACT_TEL", parmRow.getData("CONTACT_TEL"));
			recruit.setItem(row, "DESCRIPTION", parmRow.getData("DESCRIPTION"));
			recruit.setItem(row, "SIGN_FLG", parmRow.getData("SIGN_FLG"));
			recruit.setItem(row, "OPT_USER", Operator.getID());
			recruit
					.setItem(row, "OPT_DATE", SystemTool.getInstance()
							.getDate());
			recruit.setItem(row, "OPT_TERM", Operator.getIP());

			String sql = "SELECT CONTRACT_CODE , MAX(CONTRACT_DESC) AS CONTRACT_DESC , MAX(SEQ) AS SEQ "
					+ "FROM HRM_RECRUIT WHERE CONTRACT_CODE = '"
					+ contractCode
					+ "' GROUP BY CONTRACT_CODE";
			TParm contract_info = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (seq.containsKey(contractCode)) {
				seq.put(contractCode, seq.get(contractCode) + 1);
			} else {
				if (contract_info.getCount() < 0) {
					seq.put(contractCode, 1);
				} else {
					seq.put(contractCode, contract_info.getInt("SEQ", 0) + 1);
				}
			}

			recruit.setItem(row, "SEQ", seq.get(contractCode));
		}
		
		StringBuilder log = new StringBuilder();
		log.append("-------��ļ�⵼�������־Start--------\r\n");
		log.append("-------" + file.getName() + "--------\r\n");
		String err = errMsg.toString();
		if (!StringUtil.isNullString(err)) {
			err = log.toString() + err + "-------��ļ�⵼�������־End----------\r\n";
			String fileName = "�ٴ���ļ������־��" + file.getName() + "��" + ".txt";
			javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
					.getFileSystemView();
			try {
				FileTool.setString(fsv.getHomeDirectory() + "\\" + fileName,
						err);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		recruit.deleteRow(addRow);
		//����һ����������
		insertrow = recruit.insertRow();
		recruit.setItem(insertrow, "CONTRACT_CODE", "������������");
		recruit.setItem(insertrow, "SEQ", null);
		recruit.onNew(insertrow);
		table.setDSValue();
	}
	
	private int getRightRows(Sheet sheet) {
		int rsCols = sheet.getColumns(); // ����
        int rsRows = sheet.getRows(); // ����
        int nullCellNum;
        int afterRows = rsRows;
        for (int i = 1; i < rsRows; i++) { // ͳ������Ϊ�յĵ�Ԫ����
            nullCellNum = 0;
            for (int j = 0; j < rsCols; j++) {
                String val = sheet.getCell(j, i).getContents();
                val = StringUtils.trimToEmpty(val);
                if (StringUtils.isBlank(val)) nullCellNum++;
            }
            if (nullCellNum >= rsCols) { // ���nullCellNum���ڻ�����ܵ�����
                afterRows--; // ������һ
            }
        }
        return afterRows;
	}

	/**
	 * �Զ����������У��
	 */
	public boolean onTableChange(TTableNode tNode) {
		int row = tNode.getRow();
		int column = tNode.getColumn();
		String colName = table.getParmMap(column);
		Object value = tNode.getValue();
		Object oldValue = tNode.getOldValue();

		if ("������������".equals(recruit.getItemString(row, "CONTRACT_CODE"))
				&& !"CONTRACT_CODE".equals(colName)) {
			messageBox("������д������ţ��������������Ч");
			return true;
		}

		// ��������Զ���д��������,�Զ��������к�,������ȷ˳����д���Ҳ�������Զ���д������
		if ("CONTRACT_CODE".equals(colName)) {
			String contract_code = value.toString().trim();
			String sql = "SELECT CONTRACT_CODE , MAX(CONTRACT_DESC) AS CONTRACT_DESC , MAX(SEQ) AS SEQ FROM HRM_RECRUIT WHERE CONTRACT_CODE = '"
					+ contract_code + "' GROUP BY CONTRACT_CODE";
			TParm contract_info = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (contract_info.getCount() < 0) {
				recruit.setItem(row, "CONTRACT_DESC", "");
				recruit.setItem(row, "SEQ", "1");
			} else {
				recruit.setItem(row, "CONTRACT_DESC", contract_info.getValue(
						"CONTRACT_DESC", 0));
				recruit.setItem(row, "SEQ", Integer.parseInt(contract_info
						.getValue("SEQ", 0)) + 1);
			}
			table.setDSValue();
		}

		// �Զ���д����ƴ��
		if ("PAT_NAME".equals(colName)) {
			String py = this.getAllFirstLetter(value.toString()).toUpperCase();
			recruit.setItem(row, "PY1", py);
			return false;
		}

		// �Զ�����BMI
		if ("HEIGHT".equals(colName) || "WEIGHT".equals(colName)) {
			double height = Double.parseDouble(value.toString());
			double weight = Double.parseDouble(value.toString());
			if ("HEIGHT".equals(colName)) {
				height = Double.parseDouble(value.toString());
				weight = recruit.getItemDouble(row, "WEIGHT");
			}
			if ("WEIGHT".equals(colName)) {
				height = recruit.getItemDouble(row, "HEIGHT");
				weight = Double.parseDouble(value.toString());
			}
			if (height == 0) {
				messageBox("������д���");
				return true;
			}
			// ����������׻������
			height /= 100;
			double bmi = weight / (height * height);
			DecimalFormat df = new DecimalFormat("#.0");
			recruit.setItem(row, "BMI", df.format(bmi));
			table.setDSValue();
			return false;
		}

		// ���֤��Ϣ��֤
		// ���֤18λ��֤
		if ("IDNO".equals(colName)) {

			String strValue = ((String) value).trim();
			if (strValue.equals("")) {
				return true;
			}
			String isForeigner = recruit.getItemString(row, "FOREIGNER_FLG");
			String checkid = checkID(strValue);
			if (!checkid.equals("TRUE") && !checkid.equals("FALSE")) {
				this
						.messageBox_("���֤У��λ�������һλӦ��Ϊ"
								+ checkid.substring(17, 18));
				return false;
			}
			if (!isId(strValue)
					&& ("N".equalsIgnoreCase(isForeigner) || StringUtil
							.isNullString(isForeigner))) {
				this.messageBox_("���֤����ȷ");
				return true;
			}
			int rowCount = recruit.rowCount();
			// add by wangb 2017/3/21 ��ͬ�����²�������Ա�ظ�����ͬ����������Ա�ظ�
			for (int i = 0; i < rowCount; i++) {
				if (!recruit.isActive(i))
					continue;
				if (i == row)
					continue;
				if (recruit.getItemString(i, "FOREIGNER_FLG").equals(
						isForeigner)
						&& recruit.getItemString(i, "IDNO").equals(strValue)
						&& recruit.getItemString(i, "CONTRACT_CODE").equals(
								recruit.getItemString(row, "CONTRACT_CODE"))) {
					this.messageBox("������"
							+ recruit.getItemString(row, "CONTRACT_DESC")
							+ "���и����֤���Ѵ���");
					return true;
				}
			}
			if (isId(strValue)) {
				String sexCode = StringTool.isMaleFromID(strValue);
				// �Զ�����Ա�ͳ�������
				recruit.setItem(row, "SEX_CODE", sexCode);
				recruit.setItem(row, "BIRTH_DATE", StringTool
						.getBirdayFromID(strValue));
			}

			table.setDSValue();
			tNode.getTable().grabFocus();
			return false;
		}
		String buffer = recruit.isFilter() ? recruit.FILTER : recruit.PRIMARY;
		// if (TCM_Transform.isNull(recruit.getItemData(row, "IDNO", buffer))
		// && !TCM_Transform.getBoolean(recruit.getItemData(row,
		// "FOREIGNER_FLG", buffer))) {
		// this.messageBox_("�����������֤��");
		// return false;
		// }
		return false;
	}
	
	/**
	 * ��֤��������
	 * @param idcard
	 * @return
	 */
	private boolean isId(String idcard) {
        if ((idcard == null) || (idcard.length() == 0)) {
            return false;
        }
        if (idcard.length() == 15) {
            idcard = uptoeighteen(idcard);
        }
        if (idcard.length() != 18) {
            return false;
        }
        String birthday = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (birthday.matches(regexString)) {
            if (StringTool.isId(idcard)) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * ������֤��Ϊ15�Զ�������18λ
	 * @param fifteencardid
	 * @return
	 */
	private String uptoeighteen(String fifteencardid) {
        String eightcardid = fifteencardid.substring(0, 6);
        eightcardid = eightcardid + "19";
        eightcardid = eightcardid + fifteencardid.substring(6, 15);
        eightcardid = eightcardid + getIDVerify(eightcardid);
        return eightcardid;
    }

	/**
	 * ����У��
	 * @param idcard
	 * @return
	 */
	private String checkID(String idcard) {
        if (idcard.length() != 15 && idcard.length() != 18) {
            return "FALSE";
        }
        if (idcard.length() == 15) {
            if (StringTool.isId(idcard)) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
        String date = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (date.matches(regexString)) {
            if (!StringTool.isId(idcard)) {
                String verrifyChar = getIDVerify(idcard);
                if (verrifyChar.equals("")) {
                    return "FALSE";
                }
                idcard = idcard.substring(0, 17) + verrifyChar;
                return idcard;
            } else return "TRUE";
        } else return "FALSE";
    }

	/**
	 * �������һλ
	 * @param eightcardid
	 * @return
	 */
	private String getIDVerify(String eightcardid) {
        int[] wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
        int[] vi = {1, 0, 88, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] ai = new int[18];
        int remaining = 0;
        if (eightcardid.length() == 18) {
            eightcardid = eightcardid.substring(0, 17);
        }
        if (eightcardid.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eightcardid.substring(i, i + 1);
                try {
                    ai[i] = Integer.parseInt(k);
                }
                catch (Exception e) {
                    return "";
                }
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remaining = sum % 11;
        }
        return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
    }

	/**
	 * ��Ŀ��Ź���������
	 * @param projectcode
	 * @return;
	 */
	public TParm getContractParm(String projectcode){
		TParm result = new TParm();
		if(StringUtil.isNullString(projectcode)){
			return result;
		}
		String sql = 
			"SELECT CONTRACT_CODE AS ID,MAX(CONTRACT_DESC) AS NAME FROM HRM_RECRUIT WHERE PROJECT_CODE = '" 
			+ projectcode + "' GROUP BY CONTRACT_CODE";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/** 
     * ȡ�ø������ִ�������ĸ��,����ĸ�� 
     * @param str �������ִ� 
     * @return ��ĸ�� 
     */  
    public String getAllFirstLetter(String str) {  
        if (str == null || str.trim().length() == 0) {  
            return "";  
        }  
  
        String _str = "";  
        for (int i = 0; i < str.length(); i++) {  
            _str = _str + this.getFirstLetter(str.substring(i, i + 1));  
        }  
  
        return _str;  
    }  
  
    /** 
     * ȡ�ø������ֵ�����ĸ,����ĸ 
     * @param chinese �����ĺ��� 
     * @return �������ֵ���ĸ 
     */  
    public String getFirstLetter(String chinese) {  
        if (chinese == null || chinese.trim().length() == 0) {  
            return "";  
        }  
        chinese = this.conversionStr(chinese, "GB2312", "ISO8859-1");  
  
        if (chinese.length() > 1) // �ж��ǲ��Ǻ���  
        {  
            int li_SectorCode = (int) chinese.charAt(0); // ��������  
            int li_PositionCode = (int) chinese.charAt(1); // ����λ��  
            li_SectorCode = li_SectorCode - 160;  
            li_PositionCode = li_PositionCode - 160;  
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // ������λ��  
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {  
                for (int i = 0; i < 23; i++) {  
                    if (li_SecPosCode >= li_SecPosValue[i]  
                            && li_SecPosCode < li_SecPosValue[i + 1]) {  
                        chinese = lc_FirstLetter[i];  
                        break;  
                    }  
                }  
            } else // �Ǻ����ַ�,��ͼ�η��Ż�ASCII��  
            {  
                chinese = this.conversionStr(chinese, "ISO8859-1", "GB2312");  
                chinese = chinese.substring(0, 1);  
            }  
        }  
  
        return chinese;  
    }  
  
    /** 
     * �ַ�������ת�� 
     * @param str Ҫת��������ַ��� 
     * @param charsetName ԭ���ı��� 
     * @param toCharsetName ת����ı��� 
     * @return ��������ת������ַ��� 
     */  
    private String conversionStr(String str, String charsetName,String toCharsetName) {  
        try {  
            str = new String(str.getBytes(charsetName), toCharsetName);  
        } catch (UnsupportedEncodingException ex) {  
            System.out.println("�ַ�������ת���쳣��" + ex.getMessage());  
        }  
        return str;  
    }
}
