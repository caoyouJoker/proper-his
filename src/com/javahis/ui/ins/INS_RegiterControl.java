package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ������Ŀ��������
 * </p>
 * 
 * <p>
 * Description:������Ŀ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangs 20140414
 * @version 1.0
 */

public class INS_RegiterControl extends TControl {
	private static final String TParm = null;
	private static TTable NeedRegisterItemTable;
	private static TTable RegisterItemTable;
	private static TTable TABLE_FEE;
	private TTabbedPane tabbedPane;
	private static TComboBox CATEGORY;
	private static TComboBox ITEM_CLASSIFICATION;
	private TParm regionParm; // ҽ���������
	
	public void onInit() {
		NeedRegisterItemTable = (TTable) getComponent("NeedRegisterItem");
		RegisterItemTable = (TTable) getComponent("RegisterItem");
		TABLE_FEE= (TTable) getComponent("TABLE_FEE");
		tabbedPane = (TTabbedPane) getComponent("tTabbedPane_0");
		CATEGORY=(TComboBox)getComponent("CATEGORY");
		ITEM_CLASSIFICATION=(TComboBox)getComponent("ITEM_CLASSIFICATION");
		CATEGORY.setSelectedIndex(0);
		ITEM_CLASSIFICATION.setSelectedIndex(0);
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������	 
	}

	public void onQuery() {
		switch (getChangeTab()) {
		// 0 :�豸��Ŀ¼��Ϣҳǩ 1��������Ŀ������Ϣҳǩ 2:HISҽ����ѯ
		case 0:
			onNeedRegisterItemTable_Q();
			break;
		case 1:
			onRegisterItemTable_Q();
			break;
		case 2:
			GetOrderInf("");
			break;
		}

	}
	/**
	 * ҳǩ����¼�
	 */
//	public void onChangeTab() {
//
//		switch (tabbedPane.getSelectedIndex()) {
//		// 3 :���÷ָ�ǰҳǩ 4�����÷ָ��ҳǩ
//		case 0:
//			onNeedRegisterItemTable_Q();
//			break;
//		case 1:
//			onRegisterItemTable_Q();
//			break;
//		}
//	}
	public void onClear() {
		switch (getChangeTab()) {
		// 0 :�豸��Ŀ¼��Ϣҳǩ 1��������Ŀ������Ϣҳǩ
		case 0:
			NeedRegisterItemTable.removeRowAll();
			break;
		case 1:
			RegisterItemTable.removeRowAll();
			CATEGORY.setSelectedIndex(0);
			ITEM_CLASSIFICATION.setSelectedIndex(0);
			break;
		}
	}

	/**
	 * ����Excel
	 * */
	public void onExport() {
		TTable table;
		switch (getChangeTab()) {
		// 0 :�豸��Ŀ¼��Ϣҳǩ 1��������Ŀ������Ϣҳǩ

		case 0:
			table = (TTable) callFunction("UI|NeedRegisterItem|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, "�豸��Ŀ¼��Ϣ");
			break;
		case 1:
			table = (TTable) callFunction("UI|RegisterItem|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, "������Ŀ������Ϣ");
			break;
		case 2:
			table = (TTable) callFunction("UI|TABLE_FEE|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, "��Ժ������Ŀ��Ϣ");
			break;
		}
	}

	// ȡ���豸��Ŀ¼��Ϣ
	public void onNeedRegisterItemTable_Q() {

		String Sql = " SELECT A.NHI_CODE,A.NHI_DESC,  "
				+ " CASE WHEN A.CHARGE_CODE='02' THEN '����' "
				+ " WHEN A.CHARGE_CODE='03' THEN '���Ʒ�' "
				+ " WHEN A.CHARGE_CODE='04' THEN '������' "
				+ " WHEN A.CHARGE_CODE='05' THEN '��λ��' "
				+ " WHEN A.CHARGE_CODE='06' THEN 'ҽ�ò���' "
				+ " WHEN A.CHARGE_CODE='07' THEN '����' "
				+ " WHEN A.CHARGE_CODE='08' THEN '��ȫѪ' "
				+ " WHEN A.CHARGE_CODE='09' THEN '�ɷ���Ѫ' "
				+ " ELSE A.CHARGE_CODE END AS CHARGE_CODE "
				+ " FROM JAVAHIS.INS_NEEDREGISTER_ITEM A "
				+ " ORDER BY A.CHARGE_CODE ,A.NHI_CODE ";
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			onClear();
			return;
		}
//		NeedRegisterItemTable.setHeader("�շ���Ŀ����,120;�շ���Ŀ����,300;ͳ�ƴ���,100");
		NeedRegisterItemTable.setParmMap("NHI_CODE;NHI_DESC;CHARGE_CODE;");
//		NeedRegisterItemTable.setItem("DEPT_CHN_DESC");
		NeedRegisterItemTable.setColumnHorizontalAlignmentData("0,left;1,left;2,left;");
		NeedRegisterItemTable.setParmValue(tabParm);
	} 

	// ȡ��������Ŀ������Ϣ
	public void onRegisterItemTable_Q() {
		String Sql = " SELECT 'N' AS CHOOSE,A.NHI_CODE,A.NHI_DESC,A.ADM_TYPE, "
				+ " CASE WHEN A.CATEGORY='1' THEN '��������' "
				+ " WHEN A.CATEGORY='2' THEN '��������' END AS CATEGORY, "
				+ " 		  CASE WHEN A.ITEM_CLASSIFICATION='1' THEN '����' "
				+ " WHEN A.ITEM_CLASSIFICATION='2' THEN '����' "
				+ " WHEN A.ITEM_CLASSIFICATION='3' THEN 'סԺ' END AS ITEM_CLASSIFICATION, "
				+ " A.MODIFY_PROJECT_REASON, "
				+ " CASE WHEN A.ISVERIFY='1' THEN '�����' "
				+ " WHEN A.ISVERIFY='2' THEN '���ͨ��' "
				+ " WHEN A.ISVERIFY='3' THEN '���δͨ��' "
				+ " WHEN A.ISVERIFY='4' THEN 'δ����' END AS ISVERIFY, "
				+ " A.AUDIT_OPINION, "
				+ " CASE WHEN A.UPDATE_FLG='1' THEN 'δ�ϴ�' "
				+ " WHEN A.UPDATE_FLG='2' THEN '���ϴ�' "
				+ " WHEN A.UPDATE_FLG='3' THEN 'ȡ���ϴ�' END UPDATE_FLG "
				+ " 		  FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.DEL_FLG='N' ";
		String nhi_code=this.getValueString("NHI_CODE").trim();
		if (!StringUtil.isNullString(nhi_code))
			Sql = Sql + " AND A.NHI_CODE='" + nhi_code + "' ";
		Sql = Sql + " ORDER BY A.NHI_CODE ";
		System.out.println("regSql===" + Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			onClear();
			return;
		}
//		RegisterItemTable
//				.setHeader("ѡ,30,boolean;�շ���Ŀ����,80,NHI_CODE;�շ���Ŀ����,120,NHI_DESC;Ӧ�÷�Χ,80,ADM_TYPE;���,80;�޸���Ŀ��ԭ��,80;���״̬,80;������,80,�ϴ�״̬,80");
//		RegisterItemTable
//				.setParmMap("NHI_CODE;NHI_DESC;ADM_TYPE;");
//		RegisterItemTable.setItem("NHI_CODE;NHI_DESC;ADM_TYPE");
//		RegisterItemTable
//				.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;");

		RegisterItemTable.setParmValue(tabParm);
	}

	/**
	 * ȡ�õ�ǰѡ��Ҳǩ��
	 */
	public int getChangeTab() {
//		System.out.println("tabbedPane==="+tabbedPane.getSelectedIndex());
		return tabbedPane.getSelectedIndex();
	}
	/**
	 * �豸��Ŀ¼��Ϣ����
	 */
	public void onNeedRegisterItemDown() {
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.addData("PARM_COUNT", 1);
		System.out.println("onNeedRegisterItemDown:"+parm);
		TParm splitParm = INSTJTool.getInstance().DataDown_zjkd_L(parm);
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("�豸��Ŀ¼��Ϣ����ʧ��\n"+splitParm.getErrText());
			return;
		}
		splitParm.setData("OPT_USER", Operator.getID());
		splitParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onSaveInsNeedRegisterItem", splitParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}else{
			this.messageBox("�豸��Ŀ¼��Ϣ���سɹ�");
		}
	}

	/**
	 * ������Ŀ������Ϣ����
	 */
	public void onRegisterItemDown() {
		if(!onIsNull()){
			return;
		}
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.addData("TYPE", getValueString("CATEGORY")); // ���
		parm.addData("NHI_TYPE", getValueString("ITEM_CLASSIFICATION")); // ��Ŀ���
		parm.addData("PARM_COUNT", 3);
		System.out.println("onRegisterItemDown:"+parm);
		TParm splitParm = INSTJTool.getInstance().DataDown_zjkd_J(parm);
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("������Ŀ������Ϣ����ʧ��\n"+splitParm.getErrText());
			return;
		}
		splitParm.setData("OPT_USER", Operator.getID());
		splitParm.setData("OPT_TERM", Operator.getIP());
		splitParm.setData("CATEGORY", parm.getValue("TYPE",0));
		splitParm.setData("ITEM_CLASSIFICATION", parm.getValue("NHI_TYPE",0));
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onUpdateInsRegisterItem", splitParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}else{
			this.messageBox("������Ŀ������Ϣ���سɹ�");
		}
	}
	/**
	 * ������Ŀ������Ϣȡ��
	 */
	public void onRegisterItemCancel() {
		if (this.messageBox("��Ϣ", "ȡ����ѡ�еı�����¼,�Ƿ����", 2) == 1) {
			return;
		}
		TParm tableParm = null;
		TParm parm = null;
		TParm newParm = new TParm(); // �ۼ�����
		RegisterItemTable.acceptText();
		TParm parmValue = RegisterItemTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			if(tableParm.getValue("CHOOSE").equals("N")){
				continue;
			}
			parm = new TParm();
			parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
			parm.addData("NHI_CODE", tableParm.getValue("NHI_CODE")); // ��Ŀ����
			parm.addData("NHI_TYPE", tableParm.getValue("ADM_TYPE")); // Ӧ�÷�Χ
			parm.addData("PARM_COUNT", 3);
			System.out.println("onRegisterItemCancel:"+parm);
			TParm splitParm = INSTJTool.getInstance().DataDown_zjks_T(parm);
			if (!INSTJTool.getInstance().getErrParm(splitParm)) {
				this.messageBox(tableParm.getValue("NHI_DESC") +tableParm.getValue("ADM_TYPE")+
						"ȡ��ʧ��\n"+splitParm.getErrText());
				continue;
			}
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE"));
		}
		System.out.println("onRegisterItemCancel:"+newParm);
       if(newParm.getCount("NHI_CODE")<=0){
    	   return;
       }
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		newParm.setData("ISVERIFY", "4");
		newParm.setData("UPDATE_FLG", "3");
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction","onCancelInsRegisterItem", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}else{
			this.messageBox("ȡ���ɹ�");
		}
	}
	/**
	 * ������Ŀ������Ϣɾ��
	 */
	public void onRegisterItemDelete() {
//		System.out.println(this.messageBox("��Ϣ", "ɾ����ѡ�еı�����¼,�Ƿ����", 2));
		if (this.messageBox("��Ϣ", "ɾ����ѡ�еı�����¼,�Ƿ����", 2) == 1) {
			return;
		}
		TParm tableParm = null;
		TParm newParm = new TParm(); // �ۼ�����
		String str = "";
		RegisterItemTable.acceptText();
		TParm parmValue = RegisterItemTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
//			System.out.println("CHOOSE:"+tableParm.getValue("CHOOSE"));
			if (tableParm.getValue("CHOOSE").equals("N")) {
				continue;
			}
			str=this.getIsverify(tableParm.getValue("NHI_CODE"));
//			System.out.println("str:"+str);
			if (str.equals("1")|| str.equals("2")) {
				this.messageBox(parmValue.getValue("NHI_DESC", i)
						+ "�Ѿ���˻�����в���ɾ��");
				continue;
			}
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE"));
		}

		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		System.out.println("onDeleteInsRegisterItem:"+newParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onDeleteInsRegisterItem",
				newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
	}
	/**
	 * ������Ŀ������Ϣ����
	 */
	public void onRegisterItemInsert() {
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INS_RegiterItem.x", null);
	}
	/**
	 * ������Ŀ������Ϣ�޸�
	 */
	public void onRegisterItemUpdate() {
		TParm parm=new TParm();
		int row=RegisterItemTable.getSelectedRow();
		TParm tableParm=RegisterItemTable.getParmValue();
//		parm.setData("NHI_CODE", tableParm.getRow(row).getValue("NHI_CODE"));
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INS_RegiterItem.x", tableParm.getRow(row));
	}
	/**
	 * ��ֵ���
	 */
	public boolean onIsNull() {
		if(getValueString("CATEGORY").equals("")){
			this.messageBox("��ѡ�����");
			return false;
		}
		if(getValueString("ITEM_CLASSIFICATION").equals("")){
			this.messageBox("��ѡ����Ŀ���");
			return false;
		}
		return true;
	}

	/**
	 * �������״̬
	 */
	public String getIsverify(String nhi_code) {
		String Sql = " SELECT A.ISVERIFY "
				+ " 		  FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.NHI_CODE='" + nhi_code + "' " + " AND DEL_FLG='N' ";
		// System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("ISVERIFY") < 0) {
			this.messageBox("��Ŀ������");
			return "";
		}
		return tabParm.getValue("ISVERIFY",0);
	}

	/**
	 * ������Ŀ������Ϣ�ϴ�
	 */
	public void onRegisterItemUp() {
		TParm tableParm = null;
		TParm newParm = new TParm(); // �ۼ�����
		RegisterItemTable.acceptText();
		boolean flg=true;
		TParm parmValue = RegisterItemTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
//			System.out.println("CHOOSE:"+tableParm.getValue("CHOOSE"));
			if (tableParm.getValue("CHOOSE").equals("N")) {
				continue;
			}
			TParm parm = onGetUpload(onQuery(tableParm.getValue("NHI_CODE"),tableParm.getValue("ADM_TYPE")));
//			System.out.println("onRegisterItemUp:" + parm);
			TParm splitParm = INSTJTool.getInstance().DataDown_zjks_S(parm);
			if (!INSTJTool.getInstance().getErrParm(splitParm)) {
				this.messageBox(parmValue.getValue("NHI_DESC", i) +"\n"+splitParm.getErrText()+ "\n�ϴ�ʧ��");
				flg=false;
				continue;
			}
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE"));
		}

	       if(newParm.getCount("NHI_CODE")<=0){
	    	   return;
	       }
			newParm.setData("OPT_USER", Operator.getID());
			newParm.setData("OPT_TERM", Operator.getIP());
			newParm.setData("ISVERIFY", "1");
			newParm.setData("UPDATE_FLG", "2");
			TParm result = TIOM_AppServer.executeAction(
					"action.ins.INS_RegiterAction","onCancelInsRegisterItem", newParm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}else{
			if(flg){
				this.messageBox("�ϴ��ɹ�");
			}
			}
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @return TParm
	 */
	private TParm onQuery(String nhi_code,String nhi_type) {
		String Sql = "SELECT A.NHI_CODE,A.NHI_DESC,A.ADM_TYPE AS NHI_TYPE,A.MINISTRY_HEALTHNO AS FILE_NO,A.CONNOTATION_PROJECT AS ITEM_DESC, "
				+ " A.UNIT,A.OWT_AMT AS PRICE,A.ICD_DESC_LIST AS ICD_DESC,A.ICD_CODE_LIST AS ICD_CODE,A.CLINICAL_SIGNIFICANCE AS CLINICAL_DESC, "
				+ " A.DEPT_CODE,A.APPARATUS AS DEVICE,A.REMARK,A.OUTSIDE_FLG AS OUTEXM_FLG,A.OUTSIDE_HOSP_CODE AS OUTEXM_HOSP_NO, "
				+ " A.SPECIAL_CASE AS SPECIAL_DESC,A.REAGENT AS DRUG,A.MEDICAL_MATERIALS AS MATERIAL,A.CATEGORY, "
				+ " A.MODIFY_PROJECT_REASON,A.ISVERIFY,A.AUDIT_OPINION,A.UPDATE_FLG,A.ITEM_CLASSIFICATION "
				+ " FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.NHI_CODE='"
				+ nhi_code + "' " + " AND A.DEL_FLG='N' AND A.ADM_TYPE='"+nhi_type+"' ";
//		 System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		return tabParm;
	}

	private TParm onGetUpload(TParm tabParm) {
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE", 0)); // �շ���Ŀ����
		parm.addData("NHI_DESC", tabParm.getValue("NHI_DESC", 0)); // �շ���Ŀ����
		parm.addData("NHI_TYPE", tabParm.getValue("NHI_TYPE", 0)); // Ӧ�÷�Χ
		parm.addData("FILE_NO", tabParm.getValue("FILE_NO", 0)); // ������۲��Ű䲼���ļ����ݣ��ĺţ�
		parm.addData("ITEM_DESC", tabParm.getValue("ITEM_DESC", 0)); // ��Ŀ�ں�
		parm.addData("UNIT", tabParm.getValue("UNIT", 0)); // �շѵ�λ
		parm.addData("PRICE", tabParm.getValue("PRICE", 0)); // �շѱ�׼
		parm.addData("ICD_DESC", tabParm.getValue("ICD_DESC", 0)); // �ٴ���Ӧ֢
		parm.addData("ICD_CODE", tabParm.getValue("ICD_CODE", 0)); // �ٴ���Ӧ֢ICD����
		parm.addData("CLINICAL_DESC", tabParm.getValue("CLINICAL_DESC", 0)); // �ٴ�����
		parm.addData("DEPT_CODE", tabParm.getValue("DEPT_CODE", 0)); // �ٴ�ʹ�ÿ���
		parm.addData("DEVICE", tabParm.getValue("DEVICE", 0)); // ʹ�õ������豸
		parm.addData("REMARK", tabParm.getValue("REMARK", 0)); // ��ע
		parm.addData("OUTEXM_FLG", tabParm.getValue("OUTEXM_FLG", 0)); // ����־
		parm.addData("OUTEXM_HOSP_NO", tabParm.getValue("OUTEXM_HOSP_NO", 0)); // ���ҽԺ����
		parm.addData("SPECIAL_DESC", tabParm.getValue("SPECIAL_DESC", 0)); // �������˵��
		parm.addData("DRUG", tabParm.getValue("DRUG", 0)); // �Լ�
		parm.addData("MATERIAL", tabParm.getValue("MATERIAL", 0)); // ҽ�ò���
		parm.addData("PARM_COUNT", 19);
		return parm;
	}
	//���sys_fee����
	public void GetOrderInf(String smrj) {
		 String now = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd"); //�õ���ǰ��ʱ��

		String py1 ="";
		if(!smrj.equals(""))
		py1 =smrj.toUpperCase();	
		String sql = 
		"SELECT A.ORDER_CODE,A.ORDER_DESC,A.NHI_CODE_I,A.NHI_CODE_O,A.NHI_CODE_E, " +
		"A.NHI_FEE_DESC,A.NHI_PRICE,A.OWN_PRICE,C.DOSE_CHN_DESC, " +
		"A.SPECIFICATION,A.HYGIENE_TRADE_CODE,A.MAN_CODE " +
	    " FROM SYS_FEE A LEFT JOIN PHA_BASE B " +
	    " ON A.ORDER_CODE = B.ORDER_CODE" +
	    " LEFT JOIN PHA_DOSE C ON B.DOSE_CODE = C.DOSE_CODE " +
	    " WHERE A.PY1 LIKE '%" + py1 + "%' " +
	    " AND A.OWN_PRICE !=0 "+
	    " AND A.ORDER_CAT1_CODE NOT LIKE '%PHA%' ";	
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ����������");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("DATE",i,now);	
			
		}
		TABLE_FEE.setParmValue(result);
	}
}
