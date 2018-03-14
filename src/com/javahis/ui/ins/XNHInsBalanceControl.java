package com.javahis.ui.ins;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;


import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSCJAdvanceTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import org.apache.ws.xnh.XNHService;
/**
 * <p>
 * Title:��ũ����������
 * Description:��ũ����������
 * Copyright: Copyright (c) 2017
 * @version 1.0
 */
public class XNHInsBalanceControl extends TControl{	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat df = new SimpleDateFormat("yyyy");
	TTable tableInfo;// �渶�����б�
	TTable oldTable;// ��ϸ����ǰ����
	TTable newTable;// ��ϸ���ܺ�����
	TParm regionParm;// ҽ���������
	TTabbedPane tabbedPane;// ҳǩ
	int selectNewRow; // ��ϸ���ܺ����ݻ�õ�ǰѡ����
	// ����
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ��ϸ����ǰ����
	private String[] pagetwo = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	//  ��ϸ���ܺ�����
	private String[] pagethree = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"PRICE","NHI_ORDER_CODE","CLASS_CODE","CHARGE_DATE" };
	
	/**
     * ��ʼ������
     */
    public void onInit() {
		tableInfo = (TTable) this.getComponent("TABLEINFO");//�渶�����б�
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE");// ҳǩ
		oldTable = (TTable) this.getComponent("OLD_TABLE");// ��ϸ����ǰ����
		newTable = (TTable) this.getComponent("NEW_TABLE");// ��ϸ���ܺ�����	
		setValue("START_DATE", SystemTool.getInstance().getDate());
 	    setValue("END_DATE", SystemTool.getInstance().getDate());	 
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		newTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onExaCreateEditComponent");
		 //���� �д���
        this.addEventListener("NEW_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                              "onTableChangeValue");
     // �������
		addListener(newTable);
		
    }
	/**
	 * У��Ϊ�շ���
	 * 
	 * @param name
	 * @param message
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "��Ժ��ʼ���ڲ�����Ϊ��");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "��Ժ�������ڲ�����Ϊ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", sdf.format(this.getValue("START_DATE")));// ��Ժ��ʼʱ��
		parm.setData("END_DATE", sdf.format(this.getValue("END_DATE"))); // ��Ժ����ʱ��
		String sql1 ="";
	    String sql2 ="";
	    if(this.getValue("MR_NO").toString().length()>0)
			sql1 = " AND A.MR_NO = '"+ getValue("MR_NO") + "'";
		if(!this.getValue("STATUS_FLG").equals(""))	
			sql2 = " AND A.STATUS_FLG = '"+ getValue("STATUS_FLG") + "'";	
		String SQL = " SELECT A.MR_NO,B.D507_02 AS PAT_NAME,B.N507_13 AS ID_NO," +
				     " B.N507_19 AS SEX_DESC,C.CTZ_DESC,A.CASE_NO,A.IN_DATE," +
				     " A.DS_DATE,B.N507_01 AS HOSP_CODE,D.USER_NAME AS DR_DESC" +
		             " FROM ADM_INP A,INS_XNH_DOWNLOADZZRECORDS B,SYS_CTZ C,SYS_OPERATOR D" +
		             " WHERE A.CASE_NO = B.CASE_NO" +
		             " AND A.CTZ1_CODE = C.CTZ_CODE" +
		             " AND A.VS_DR_CODE = D.USER_ID" +
		             " AND A.CANCEL_FLG = 'N'" +
				     " AND A.DS_DATE BETWEEN TO_DATE " +
				     " ('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDhh24miss')"+  
				     " AND TO_DATE ('"+ parm.getValue("END_DATE")+"235959"+"', 'YYYYMMDDhh24miss')" +
				     sql1;	
//		System.out.println("SQL=====:"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));	
//	    System.out.println("result=====:"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * ��ѯ����(�����Ų�ѯ)
	 */
	public void onQueryNO() {
		String mrno = PatTool.getInstance().checkMrno(
			TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO",mrno);
		onQuery();		
	}
	/**
	 * У���Ƿ��л�ý���
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("-", "")
				.substring(0, 6)); // �ں�
		parm.setData("CASE_NO", parm.getValue("CASE_NO")); // �������
		parm.setData("START_DATE", 
				parm.getValue("IN_DATE").replace("-", "").substring(0,8));//��ʼʱ��
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("HOSP_CODE", parm.getValue("HOSP_CODE"));//���߲κϵر���
		parm.setData("DR_DESC", parm.getValue("DR_DESC"));//ҽʦ����
//		System.out.println("parm============"+parm);
		return parm;
	}
	/**
	 * ת��ϸ
	 */
	public void onApply(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		parm.setData("REGION_CODE", Operator.getRegion()); // ҽԺ����
		parm.setData("NHIHOSP_NO",regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String endDate = sdf.format(SystemTool.getInstance().getDate());
//		System.out.println("endDate============"+endDate);
		parm.setData("END_DATE", endDate); // ����ʱ��
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.XNHINSBalanceAction", "onExeXnh", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ��:"+result.getErrText());
			return;
		} 
	
		this.messageBox("���ܳɹ�");
	}
	/**
	 * ��ϸ�ϴ�
	 */
	public void onUpload(){
//		if(!this.getRadioButton("NEW_RDO_1").isSelected()){
//			this.messageBox("����ȫ�����ϴ�");
//			return;
//		}
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String chAreaCode = parm.getValue("HOSP_CODE");//���߲κϵر���
		int itemId = 0;//˳���
		String[] str = {"11111"};
		TParm Uploadparm = new TParm();
		TParm dataParm = null;
		//����ϴ���ϸ
		String sql = " SELECT A.ORDER_NO, A.SEQ_NO, A.CLASS_CODE, A.CLASS_DESC, A.NHI_ORDER_CODE,"+ 
			" A.ORDER_CODE, A.ORDER_DESC, A.DOSE_DESC, A.STANDARD, A.UNIN_DESC, A.PRICE,"+  
			" A.TOT_AMT, A.DR_DESC, A.CHARGE_DATE, A.PAY_QTY, A.QTY, A.XNH_ORDER_CODE,"+  
			" A.XNH_ORDER_DESC, A.INS_AMT, A.CREATE_DATE, A.UPDATE_DATE, A.CASE_NO,"+  
			" A.HOSP_CODE, A.HOSP_DESC, A.IMPORT_FLG_CODE, A.IMPORT_FLG_DESC, A.DEDUCTION_AMT,"+ 
			" A.DEDUCTION_REASON, A.LIST_CODE, A.LIST_DESC, A.BUY_SUBJECT_CODE"+ 
			" FROM INS_XNH_UPLOAD A"+ 
			" WHERE A.CASE_NO = '"+ caseNo + "'";
		 TParm data  = new TParm(TJDODBTool.getInstance().select(sql));
		 if (data.getErrCode() < 0) {
				this.messageBox("E0005");// ִ��ʧ��
				return;
			}	
   	 for (int i = 0; i < data.getCount(); i++) {
   		dataParm = data.getRow(i);	    
   	Uploadparm.addData("N707_01", dataParm.getValue("ORDER_NO"));//סԺ������ˮ��
   	Uploadparm.addData("N707_02", dataParm.getValue("SEQ_NO"));//���	
   	Uploadparm.addData("N707_03", dataParm.getValue("CLASS_CODE"));//����������           
   	Uploadparm.addData("N707_04", dataParm.getValue("CLASS_DESC"));//�����������
   	Uploadparm.addData("N707_05", dataParm.getValue("ORDER_CODE"));//HISϵͳ��Ŀ����
   	Uploadparm.addData("N707_06", dataParm.getValue("ORDER_DESC"));//HISϵͳ��Ŀ����
   	Uploadparm.addData("N707_07", dataParm.getValue("DOSE_DESC"));//����
   	Uploadparm.addData("N707_08", dataParm.getValue("STANDARD"));//���
   	Uploadparm.addData("N707_09", dataParm.getValue("UNIN_DESC"));//��λ
   	Uploadparm.addData("N707_10", dataParm.getDouble("PRICE"));//����
   	Uploadparm.addData("N707_11", dataParm.getDouble("TOT_AMT"));//�ܽ��
   	Uploadparm.addData("N707_12", dataParm.getValue("DR_DESC"));//ҽ������  
   	String chargeDate = StringTool.getString(
   			dataParm.getTimestamp("CHARGE_DATE"), "yyyy-MM-dd"); 	
   	Uploadparm.addData("N707_13",chargeDate); // ��������
   	Uploadparm.addData("N707_14", dataParm.getDouble("PAY_QTY"));//����
   	Uploadparm.addData("N707_15", dataParm.getInt("QTY"));//����
   	Uploadparm.addData("N707_16", dataParm.getValue("XNH_ORDER_CODE"));//ũ����Ŀ����    
   	Uploadparm.addData("N707_17", dataParm.getValue("XNH_ORDER_DESC"));//ũ����Ŀ����     
   	Uploadparm.addData("N707_18",dataParm.getDouble("INS_AMT"));//�ɱ������ 
   	String createDate = StringTool.getString(
   			dataParm.getTimestamp("CREATE_DATE"), "yyyy-MM-dd HH:mm:ss");
   	String updateDate = StringTool.getString(
   			dataParm.getTimestamp("UPDATE_DATE"), "yyyy-MM-dd HH:mm:ss");
	Uploadparm.addData("N707_19",createDate);// ��������(��ǰʱ��)
	Uploadparm.addData("N707_20",updateDate);// ��������(��ǰʱ��)
   	Uploadparm.addData("N707_21", dataParm.getValue("CASE_NO"));//סԺ�Ǽ���ˮ��
   	Uploadparm.addData("N707_22", dataParm.getValue("HOSP_CODE"));//��ҽ��������
   	Uploadparm.addData("N707_23", dataParm.getValue("HOSP_DESC"));//��ҽ��������
   	Uploadparm.addData("N707_24", dataParm.getValue("IMPORT_FLG_CODE"));//�������ڱ�ʶ����
   	Uploadparm.addData("N707_25", dataParm.getValue("IMPORT_FLG_DESC"));//�������ڱ�ʶ����
//   	Uploadparm.addData("N707_26", dataParm.getDouble("DEDUCTION_AMTs"));//�ۼ����
//   	Uploadparm.addData("N707_27", dataParm.getValue("DEDUCTION_REASON"));//�ۼ�ԭ��
   	Uploadparm.addData("N707_28", dataParm.getValue("LIST_CODE"));//Ŀ¼����
   	Uploadparm.addData("N707_29", dataParm.getValue("LIST_DESC"));//Ŀ¼��������
   	Uploadparm.addData("N707_30", dataParm.getValue("BUY_SUBJECT_CODE"));//���вɹ���Ŀ����
   } 
	System.out.println("Uploadparm============"+Uploadparm);
		TParm result = XNHService.uploadInpDetails(chAreaCode,Uploadparm,itemId,str);
		System.out.println("result============"+result);
		 if (result.getErrCode() < 0) {
				this.messageBox("E0005");// ִ��ʧ��
				return;
		}else{
			this.messageBox("�ϴ��ɹ�");	
		}
	
		
	}
	/**
	 * ��ϸ����
	 */
	public void onCancelDetail(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
	String chAreaCode = parm.getValue("HOSP_CODE");//���߲κϵر���
	String caseNo = parm.getValue("CASE_NO");//סԺ��ˮ˳���
	TParm result = XNHService.clearInpDetails(chAreaCode,caseNo);	
	 System.out.println("result============"+result);
	 if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
	}else{
		this.messageBox("��ϸ�����ɹ�");	
	}
 
	}
	
	/**
	 * Ԥ����
	 */
	public void onSettlementY(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String[] str = {"11111"};
		 String redeemDate = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy-MM-dd"); //�Ҹ�����
		 String redeemOrgno = regionParm.getData("NHI_NO", 0).toString();//������������
		 //��ò����ϴ�����
		 String sql = " SELECT A.N801_01, A.N801_02, A.N801_03, A.N801_04, A.N801_05, A.N801_06,"+
		 " A.N801_07, A.N801_08, A.N801_09, A.N801_10, A.N801_11, A.N801_12, A.N801_13,"+
		 " A.N801_14, A.N801_15, A.N801_16, A.N801_17, A.N801_18, A.N801_19, A.N801_20,"+
		 " A.N801_21, A.N801_22, A.N801_23, A.N801_24, A.N801_25, A.N801_26, A.N801_27,"+
		 " A.N801_28, A.N801_29, A.N801_30, A.N801_31, A.N801_32, A.N801_33, A.N801_34," +
		 " A.N801_35, A.N801_36, A.N801_37, A.N801_38, A.N801_39, A.N801_40, A.N801_41," +
		 " A.N801_42, A.N801_43, A.N801_44, A.N801_45, A.N801_47, A.N801_48, A.N801_49," +
		 " A.N801_50, A.N801_51, TO_CHAR(A.N801_52,'yyyy-MM-dd') AS N801_52, A.N801_53, A.N801_54, A.N801_55, A.N801_56," +
		 " A.N801_57, A.N801_58, A.CANCEL_FLG " +
		 " FROM INS_XNH_INPREGISTER A "+
         " WHERE N801_01 = '"+ caseNo + "'";
         TParm classParm  = new TParm(TJDODBTool.getInstance().select(sql));	 
		TParm YBlanceparm = new TParm();		
		YBlanceparm.addData("N706_01",caseNo);//HISϵͳ���ݱ��룿��������������������
		YBlanceparm.addData("N706_02",caseNo);//סԺ�Ǽ���ˮ��
		YBlanceparm.addData("N706_03",XNHService.HOSPCODE);//��ҽ���� ����
		YBlanceparm.addData("N706_04",XNHService.HOSPNAME);//��ҽ���� ����
		YBlanceparm.addData("N706_05","4");//��ҽ�����������
		YBlanceparm.addData("N706_06","����ҽ�ƻ���");//��ҽ������������
		YBlanceparm.addData("N706_07",Operator.getID());//ҽԺ��Ϣϵͳ�����ߴ���
		YBlanceparm.addData("N706_08",Operator.getName());//ҽԺ��Ϣϵͳ����������
		YBlanceparm.addData("N706_09",classParm.getValue("N801_04",0));//��������
		YBlanceparm.addData("N706_10",classParm.getValue("N801_05",0));//�����Ա����
		YBlanceparm.addData("N706_11",classParm.getValue("N801_06",0));//�����Ա�����
		YBlanceparm.addData("N706_12",classParm.getValue("N801_07",0));//�������֤��
		YBlanceparm.addData("N706_13",classParm.getValue("N801_08",0));//����
		YBlanceparm.addData("N706_14",classParm.getValue("N801_09",0));//����ͨѶ��ַ
		YBlanceparm.addData("N706_15",classParm.getValue("N801_10",0));//�κ�ʡ����
		YBlanceparm.addData("N706_16",classParm.getValue("N801_11",0));//�κ�ʡ����
		YBlanceparm.addData("N706_17",classParm.getValue("N801_12",0));//�κ��д���
		YBlanceparm.addData("N706_18",classParm.getValue("N801_13",0));//�κ�������
		YBlanceparm.addData("N706_19",classParm.getValue("N801_14",0));//�κ�������
		YBlanceparm.addData("N706_20",classParm.getValue("N801_15",0));//�κ�������
		YBlanceparm.addData("N706_21",classParm.getValue("N801_36",0));//��ϵ������
		YBlanceparm.addData("N706_22",classParm.getValue("N801_37",0));//�绰����
		YBlanceparm.addData("N706_23",classParm.getValue("N801_30",0));//�������ʹ���
		YBlanceparm.addData("N706_24",classParm.getValue("N801_31",0));//������������
		YBlanceparm.addData("N706_25",parm.getValue("DR_DESC"));//ҽ������
		String inDate = parm.getValue("IN_DATE").substring(0,10);
		YBlanceparm.addData("N706_26",inDate);//��Ժ����
		String dsdate =parm.getValue("DS_DATE").substring(0,10);
		YBlanceparm.addData("N706_27",dsdate);//��Ժ����
		String blanncedate= StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyy-MM-dd");
		YBlanceparm.addData("N706_28",blanncedate);//��������
		YBlanceparm.addData("N706_29",classParm.getValue("N801_40",0));//סԺ��
		YBlanceparm.addData("N706_30",classParm.getValue("N801_51",0));//ҽ��֤ /����
		 String sql1 = " SELECT A.ICD_CODE,A.ICD_DESC,B.ID,B.CHN_DESC" + 
			           " FROM MRO_RECORD_DIAG A,SYS_DICTIONARY B" +
			           " WHERE A.CASE_NO = '"+ caseNo + "'" +
			           " AND A.IO_TYPE = 'O'" +
			           " AND A.MAIN_FLG = 'Y'" +
			           " AND A.ICD_STATUS =B.ID" +
			           " AND B.GROUP_ID = 'ADM_RETURN'";
        TParm diagParm  = new TParm(TJDODBTool.getInstance().select(sql1));
		YBlanceparm.addData("N706_31",diagParm.getValue("ICD_CODE",0));//��Ҫ��ϴ���
		YBlanceparm.addData("N706_32",diagParm.getValue("ICD_DESC",0));//��Ҫ�������		
		YBlanceparm.addData("N706_33","");//������ϴ���
		YBlanceparm.addData("N706_34","");//�����������
		YBlanceparm.addData("N706_35","");//��������
		YBlanceparm.addData("N706_36","");//��������
		YBlanceparm.addData("N706_37",classParm.getValue("N801_28",0));//��Ժ���Ҵ���
		YBlanceparm.addData("N706_38",classParm.getValue("N801_29",0));//��Ժ��������
		YBlanceparm.addData("N706_39","");//��Ժ���Ҵ���
		YBlanceparm.addData("N706_40","");//��Ժ��������
		YBlanceparm.addData("N706_41",classParm.getValue("N801_34",0));//��Ժ״̬����
		YBlanceparm.addData("N706_42",classParm.getValue("N801_35",0));//��Ժ״̬����
		String id = "";
		if(diagParm.getValue("ID",0).equals("5"))
			id = "9";
			else
			id	= diagParm.getValue("ID",0);
		YBlanceparm.addData("N706_43",id);//��Ժ״̬����
		YBlanceparm.addData("N706_44",diagParm.getValue("CHN_DESC",0));//��Ժ״̬����
		YBlanceparm.addData("N706_45","");//��Ժ���
		YBlanceparm.addData("N706_46",classParm.getValue("N801_32",0));//����֢����
		YBlanceparm.addData("N706_47",classParm.getValue("N801_33",0));//���� ֢����
		YBlanceparm.addData("N706_48","");//���񽡿�����	
		String sql2 = " SELECT SUM(TOT_AMT) AS TOT_AMT,SUM(INS_AMT) AS INS_AMT " +
				      " FROM INS_XNH_UPLOAD"+
		              " WHERE CASE_NO = '"+ caseNo + "'";
		 TParm insamtParm  = new TParm(TJDODBTool.getInstance().select(sql2));
		YBlanceparm.addData("N706_49",insamtParm.getDouble("TOT_AMT",0));//�����ܶ� ��Ԫ��
		YBlanceparm.addData("N706_50",insamtParm.getDouble("INS_AMT",0));//�ɱ����ܶ� ��Ԫ��		
		YBlanceparm.addData("N706_51",df.format(
				SystemTool.getInstance().getDate()));//���������ߣ����
		String createDate = StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy-MM-dd HH:mm:ss");
	   	String updateDate = StringTool.getString(
	   			SystemTool.getInstance().getDate(), "yyyy-MM-dd HH:mm:ss");
		YBlanceparm.addData("N706_52",createDate);//��������
		YBlanceparm.addData("N706_53",updateDate);//��������
		YBlanceparm.addData("N706_54","");//�����ַ��ö��Ԫ��
		YBlanceparm.addData("N706_55","");//�������������Ԫ��
		YBlanceparm.addData("N706_56","");//�󲡱��տɲ����Ԫ��
		YBlanceparm.addData("N706_57","");//�󲡱���ʵ�ʲ����Ԫ��
//		YBlanceparm.setData("N706_58","");//�ۼ��ܶԪ��
//		YBlanceparm.setData("N706_59","");//�ۼ�ԭ��
		YBlanceparm.addData("N706_60",classParm.getValue("N801_52",0));//��������
		 System.out.println("YBlanceparm============"+YBlanceparm);
		//Ԥ�������
		 TParm result = XNHService.preInpPay(caseNo,redeemDate,redeemOrgno,YBlanceparm,str);
		 System.out.println("result============"+result);
		 if (result.getErrCode() < 0) {
				this.messageBox("E0005");// ִ��ʧ��
				return;
		}else{
			 TParm N708Parm =  ((TParm)result.getData("N708",0));
			 System.out.println("N708Parm============"+N708Parm);			
		//����Ԥ���㷵����Ϣ����INS_XNH
		 String SQL= " INSERT INTO INS_XNH("+
   		 " HOSP_NO,CASE_NO,HOSP_CODE,HOSP_DESC,HOSP_LEVEL_CODE,HOSP_LEVEL_DESC,"+ 
   		 " AREA_CODE,AREA_DESC,PERSONAL_CODE,PAT_NAME,IN_NO ,TEL_NO,"+ 
   		 " SEX_CODE,SEX_DESC,MEDICAL_NO ,VISIT_CODE ,VISIT_DESC , "+
   		 " HEAD_PAT_NAME,ADDRESS_DESC,IPD_NO ,IN_DATE,DS_DATE,"+ 
   		 " MAIN_DIAG_CODE, MAIN_DIAG_DESC,OPE_CODE ,OPE_DESC ,XNH_CODE,"+
   		 " XNH_DESC, TOT_AMT ,OWN_AMT,REAL_INS_AMT ,ESPENSE_YEAR,"+ 
   		 " ESPENSE_TOT_AMT ,SINGLE_DISEASE_AMT,INSURANCE_AMT ,"+ 
   		 " INSURANCE_REAL_AMT ,CIVIL_ASSISTANCE_AMT ,ESPENSE_RATE ,"+
   		 " ACCUMULATIVE_TOTAL_AMT ,ACCUMULATIVE_TOTAL_COUNT,DEDUCTIBLE_AMT,"+ 
   		 " TOP_AMT,REMARK_DESC ,BLANCE_CODE,BLANCE_DESC,DEDUCTION_TOT_AMT,"+
   		 " DEDUCTION_REASON,ADVANCE_TOT_AMT,SETTLE_NO,"+ 
   		 " OPT_USER,OPT_DATE,OPT_TERM,SETTLE_DATE) " +
   		 " VALUES('"+ N708Parm.getValue("N708_01",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_02",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_03",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_04",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_05",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_06",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_07",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_08",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_09",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_10",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_11",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_12",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_13",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_14",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_15",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_16",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_17",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_18",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_19",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_20",0)+ "'," +
			 " TO_DATE('"+N708Parm.getValue("N708_21",0)+ "','YYYY-MM-DD')," +
			 " TO_DATE('"+N708Parm.getValue("N708_22",0)+ "','YYYY-MM-DD')," +
			 " '"+ N708Parm.getValue("N708_23",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_24",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_25",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_26",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_27",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_28",0)+ "'," +
			 " "+ N708Parm.getDouble("N708_29",0)+ "," +
			 " "+ N708Parm.getDouble("N708_30",0)+ "," +
			 " "+ N708Parm.getDouble("N708_31",0)+ "," +
			 " '"+ N708Parm.getValue("N708_32",0)+ "'," +
			 " "+ N708Parm.getDouble("N708_33",0)+ "," +
			 " "+ N708Parm.getDouble("N708_34",0)+ "," +
			 " "+ N708Parm.getDouble("N708_35",0)+ "," +
			 " "+ N708Parm.getDouble("N708_36",0)+ "," +
			 " "+ N708Parm.getDouble("N708_37",0)+ "," +
			 " "+ N708Parm.getDouble("N708_38",0)+ "," +
			 " "+ N708Parm.getDouble("N708_39",0)+ "," +
			 " "+ N708Parm.getDouble("N708_40",0)+ "," +
			 " "+ N708Parm.getDouble("N708_41",0)+ "," +
			 " "+ N708Parm.getDouble("N708_42",0)+ "," +
			 " '"+ N708Parm.getValue("N708_43",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_44",0)+ "'," +
			 " '"+ N708Parm.getValue("N708_45",0)+ "'," +
			 " "+ N708Parm.getDouble("N708_46",0)+ "," +
			 " '"+ N708Parm.getValue("N708_47",0)+ "'," + 
			 " "+ N708Parm.getDouble("N708_48",0)+ "," +	
			 " '"+ N708Parm.getValue("N708_49",0)+ "'," + 
			 " '"+ parm.getValue("OPT_USER")+ "',SYSDATE," +
	   		 " '"+ parm.getValue("OPT_TERM")+ "',SYSDATE)";
		 System.out.println("SQL============"+SQL);	 
		 TParm data = new TParm(TJDODBTool.getInstance().update(SQL)); 
		 System.out.println("data============"+data);	
	        if (data.getErrCode() < 0) {
	        	this.messageBox("����ʧ��");
	               return;
	            }
	        else 
	        	this.messageBox("Ԥ����ɹ�");	
		}  	
	}
	/**
	 * ����
	 */
	public void onSettlement(){		
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String chAreaCode = parm.getValue("HOSP_CODE");//���߲κϵر���
		String opertator = Operator.getID();//������
		 String redeemDate = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy-MM-dd"); //�Ҹ�����
		 String redeemOrgno = regionParm.getData("NHI_NO", 0).toString();//������������
		//���Ԥ������ˮ��
		String sql = " SELECT SETTLE_NO FROM INS_XNH "+
                     " WHERE CASE_NO = '"+ caseNo + "'";
		 TParm parmId  = new TParm(TJDODBTool.getInstance().select(sql));	
		String settleNo = parmId.getValue("SETTLE_NO",0);
		 System.out.println("settleNo============"+settleNo);
		//�������
		TParm result = XNHService.inpPay(chAreaCode,caseNo,opertator,redeemDate,redeemOrgno,settleNo);
		 System.out.println("result============"+result);
		 if (result.getErrCode() < 0) {
				this.messageBox("E0005");// ִ��ʧ��
				return;
		}else{
			double bedamt = 0.00;
			double zcamt = 0.00;
			double jcamt = 0.00;
			double hyamt = 0.00;
			double zlamt = 0.00;
			double opamt = 0.00;
			double hlamt = 0.00;
			double clamt = 0.00;
			double xyamt = 0.00;
			double zcyamt = 0.00;
			double cyamt = 0.00;
			double ysfuamt = 0.00;
			double ybzlamt = 0.00;
			double otheramt = 0.00;
			 String sql1 = " SELECT SUM(TOT_AMT) AS TOT_AMT,CLASS_CODE " +
			 		       " FROM INS_XNH_UPLOAD"+
			               " WHERE CASE_NO = '"+ caseNo + "'"+
			               " GROUP BY CLASS_CODE ";
             TParm classParm  = new TParm(TJDODBTool.getInstance().select(sql1));
            for(int i = 0; i < classParm.getCount(); i++) {
              if(classParm.getValue("CLASS_CODE",i).equals("1"))
            	  bedamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("2"))
            	  zcamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("3"))
            	  jcamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("4"))
            	  hyamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("5"))
            	  zlamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("6"))
            	  opamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("7"))
            	  hlamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("8"))
            	  clamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("9"))
            	  xyamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("10"))
            	  zcyamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("11"))
            	  cyamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("12"))
            	  ysfuamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("13"))
            	  ybzlamt = classParm.getDouble("TOT_AMT",i);
              else if(classParm.getValue("CLASS_CODE",i).equals("14"))
            	  otheramt = classParm.getDouble("TOT_AMT",i);
             }      	  
			 TParm N708Parm =  ((TParm)result.getData("N708",0));
			 System.out.println("N708Parm============"+N708Parm);		
		//���½��㷵����Ϣ����INS_XNH
		 String SQL= " UPDATE INS_XNH SET"+
		" HOSP_NO='"+ N708Parm.getValue("N708_01",0)+ "',"+
     	" HOSP_CODE='"+ N708Parm.getValue("N708_03",0)+ "',"+
     	" HOSP_DESC='"+ N708Parm.getValue("N708_04",0)+ "',"+
     	" HOSP_LEVEL_CODE='"+ N708Parm.getValue("N708_05",0)+ "',"+
     	" HOSP_LEVEL_DESC='"+ N708Parm.getValue("N708_06",0)+ "',"+
     	" AREA_CODE='"+ N708Parm.getValue("N708_07",0)+ "',"+
     	" AREA_DESC='"+ N708Parm.getValue("N708_08",0)+ "',"+
     	" PERSONAL_CODE='"+ N708Parm.getValue("N708_09",0)+ "',"+
     	" PAT_NAME='"+ N708Parm.getValue("N708_10",0)+ "',"+
     	" IN_NO='"+ N708Parm.getValue("N708_11",0)+ "',"+
     	" TEL_NO='"+ N708Parm.getValue("N708_12",0)+ "',"+
     	" SEX_CODE='"+ N708Parm.getValue("N708_13",0)+ "',"+
     	" SEX_DESC='"+ N708Parm.getValue("N708_14",0)+ "',"+
     	" MEDICAL_NO='"+ N708Parm.getValue("N708_15",0)+ "',"+
     	" VISIT_CODE='"+ N708Parm.getValue("N708_16",0)+ "',"+
     	" VISIT_DESC='"+ N708Parm.getValue("N708_17",0)+ "',"+
     	" HEAD_PAT_NAME='"+ N708Parm.getValue("N708_18",0)+ "',"+
     	" ADDRESS_DESC='"+ N708Parm.getValue("N708_19",0)+ "',"+
     	" IPD_NO='"+ N708Parm.getValue("N708_20",0)+ "',"+
     	" IN_DATE=TO_DATE('"+N708Parm.getValue("N708_21",0)+ "','YYYY-MM-DD')," +
     	" DS_DATE=TO_DATE('"+N708Parm.getValue("N708_22",0)+ "','YYYY-MM-DD')," +
     	" MAIN_DIAG_CODE='"+ N708Parm.getValue("N708_23",0)+ "',"+
     	" MAIN_DIAG_DESC='"+ N708Parm.getValue("N708_24",0)+ "',"+
     	" OPE_CODE='"+ N708Parm.getValue("N708_25",0)+ "',"+
     	" OPE_DESC='"+ N708Parm.getValue("N708_26",0)+ "',"+
     	" XNH_CODE='"+ N708Parm.getValue("N708_27",0)+ "',"+
     	" XNH_DESC='"+ N708Parm.getValue("N708_28",0)+ "',"+
     	" TOT_AMT="+ N708Parm.getDouble("N708_29",0)+ ","+
     	" OWN_AMT="+ N708Parm.getDouble("N708_30",0)+ ","+
     	" REAL_INS_AMT="+ N708Parm.getDouble("N708_31",0)+ ","+
     	" ESPENSE_YEAR="+ N708Parm.getDouble("N708_32",0)+ ","+
     	" ESPENSE_TOT_AMT="+ N708Parm.getDouble("N708_33",0)+ ","+
     	" SINGLE_DISEASE_AMT="+ N708Parm.getDouble("N708_34",0)+ ","+
     	" INSURANCE_AMT="+ N708Parm.getDouble("N708_35",0)+ ","+
     	" INSURANCE_REAL_AMT="+ N708Parm.getDouble("N708_36",0)+ ","+
     	" CIVIL_ASSISTANCE_AMT="+ N708Parm.getDouble("N708_37",0)+ ","+
     	" ESPENSE_RATE="+ N708Parm.getDouble("N708_38",0)+ ","+
     	" ACCUMULATIVE_TOTAL_AMT="+ N708Parm.getDouble("N708_39",0)+ ","+
     	" ACCUMULATIVE_TOTAL_COUNT="+ N708Parm.getDouble("N708_40",0)+ ","+
     	" DEDUCTIBLE_AMT="+ N708Parm.getDouble("N708_41",0)+ ","+
     	" TOP_AMT="+ N708Parm.getDouble("N708_42",0)+ ","+
     	" REMARK_DESC='"+ N708Parm.getValue("N708_43",0)+ "'," +
     	" BLANCE_CODE='"+ N708Parm.getValue("N708_44",0)+ "'," +
     	" BLANCE_DESC='"+ N708Parm.getValue("N708_45",0)+ "'," +
     	" DEDUCTION_TOT_AMT="+ N708Parm.getDouble("N708_46",0)+ "," +
     	" DEDUCTION_REASON='"+ N708Parm.getValue("N708_47",0)+ "'," + 
     	" ADVANCE_TOT_AMT="+ N708Parm.getDouble("N708_48",0)+ "," +
     	" BED_AMT = "+ bedamt+ ","+
     	" ZC_AMT = "+ zcamt+ ","+
     	" JC_AMT = "+ jcamt+ ","+
     	" HY_AMT = "+ hyamt+ ","+
     	" ZL_AMT = "+ zlamt+ ","+
     	" OP_AMT = "+ opamt+ ","+
     	" HL_AMT = "+ hlamt+ ","+
     	" CL_AMT = "+ clamt+ ","+
     	" XY_AMT = "+ xyamt+ ","+
     	" ZCY_AMT = "+ zcyamt+ ","+
     	" CY_AMT = "+ cyamt+ ","+
     	" YSFU_AMT = "+ ysfuamt+ ","+
     	" YBZL_AMT = "+ ybzlamt+ ","+
     	" OTHER_AMT = "+ otheramt+ ""+
     	" WHERE CASE_NO='"+ caseNo+ "'";
		 System.out.println("SQL============"+SQL); 
     	 TParm data = new TParm(TJDODBTool.getInstance().update(SQL));  
        if (data.getErrCode() < 0) {
               return;
            }
        else 
        	this.messageBox("����ɹ�");	
		}
	}
	/**
	 * �˽���
	 */
	public void onSettlementC(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String chAreaCode = parm.getValue("HOSP_CODE");//���߲κϵر���
		String opertator = Operator.getID();//������
		//�˽������
		TParm result = XNHService.backPay(chAreaCode,caseNo,opertator);
		 System.out.println("result============"+result);
		 if (result.getErrCode() < 0) {
				this.messageBox("E0005");// ִ��ʧ��
				return;
		}else{
			this.messageBox("�˽���ɹ�");	
		}
	 
		
		
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("MR_NO", "");
		this.setValue("STATUS_FLG", "");
		tableInfo.removeRowAll();
		oldTable.acceptText();
		oldTable.setDSValue();
		oldTable.removeRowAll();
		newTable.acceptText();
		newTable.setDSValue();
		newTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // ��һ��ҳǩ
		clearValue("SUM_AMT;NEW_SUM_AMT");
	}
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {
		switch (tabbedPane.getSelectedIndex()) {
		// 1 :��ϸ����ǰҳǩ 2����ϸ���ܺ�ҳǩ
		case 1:
			onSplitOld();
			break;
		case 2:
			onSplitNew();
			break;
		}
	}
	/**
	 * ��ϸ����ǰ����
	 */
	public void onSplitOld() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�
		//05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("OLD_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"OLD_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			oldTable.acceptText();
			oldTable.setDSValue();
			oldTable.removeRowAll();
			return;
			}		
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������

		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pagetwo.length; i++) {
			if (i == 0) {
				result.addData(pagetwo[i], "�ϼ�:");
				continue;
			}
			result.addData(pagetwo[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
		this.setValue("SUM_AMT", totalAmt); // ����ܽ��
	}
	/**
	 * ��ϸ���ܺ�����
	 */
	public void onSplitNew() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
//		for (int i = 1; i <= 12; i++) {
//			if (this.getRadioButton("NEW_RDO_" + i).isSelected()) {
//				if (i != 1) {
//					parm.setData("CLASS_CODE", this.getRadioButton(
//							"NEW_RDO_" + i).getName());
//					break;
//				}
//				else {
					parm.setData("CLASS_CODE","");	
//				}
//			}
//		}
		
		String sql1 = "";
		if(parm.getValue("CLASS_CODE").length()>0)
		sql1 = " AND A.CLASS_CODE ='" + parm.getData("CLASS_CODE") + "'";
		//�����ϸ���ܺ�����
		 String sql = " SELECT A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC," +
		 " A.PRICE,A.QTY,A.TOT_AMT," +
		 " A.NHI_ORDER_CODE,A.CLASS_CODE," +
		 " TO_CHAR(A.CHARGE_DATE,'YYYY/MM/DD') AS CHARGE_DATE,A.CASE_NO,'N' AS FLG" +
		 " FROM INS_XNH_UPLOAD A " +
		 " WHERE A.CASE_NO = '" + parm.getData("CASE_NO") + "'" +
		 " AND A.TOT_AMT <> 0" +
		 sql1 +
		 " ORDER BY A.SEQ_NO";
		 TParm upLoadParmOne = new TParm(TJDODBTool.getInstance().select(sql));
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		
		if (upLoadParmOne.getCount() == 0) {
			newTable.acceptText();
			newTable.setDSValue();
			newTable.removeRowAll();
			return;
			}
		double qty = 0.00; // ����
		double totAmt = 0.00; // �������
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			qty += upLoadParmOne.getDouble("QTY", i);
			totAmt += upLoadParmOne.getDouble("TOT_AMT", i);
		}
		// //��Ӻϼ�
		for (int i = 0; i < pagethree.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pagethree[i], "�ϼ�:");
				continue;
			}
			upLoadParmOne.addData(pagethree[i], "");
		}
		upLoadParmOne.addData("QTY", qty);
		upLoadParmOne.addData("TOT_AMT", totAmt);
		upLoadParmOne.addData("CASE_NO", ""); //��������
		upLoadParmOne.addData("FLG", ""); // ��������
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// ��Ӻϼ�
		newTable.setParmValue(upLoadParmOne);
		this.setValue("NEW_SUM_AMT", totAmt); // �ܽ����ʾ
		callFunction("UI|upload|setEnabled", true);
	}
	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}
	/**
	 * ��ϸ���ܺ����ݱ������
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // �������
		// ִ�����INS_XNH_UPLOAD�����
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.XNHINSBalanceAction", "updateXnhUpLoad", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew();
		}
	}
	/**
     * �����д���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) { // �����ϼ�����
    	newTable.acceptText();
         TTableNode node = (TTableNode) obj;
         if (node == null) {
             return;
         }
         int row = node.getRow();        
         int column = node.getColumn();
 		// ���㵱ǰ�ܽ��
      	double qty = 0.0;
      	 if (column == 4) {
      		qty = Double.parseDouble(String.valueOf(node.getValue()));
          } else {
         	 qty = Double.parseDouble(String.valueOf(newTable.
                      getItemData(row, "QTY")));
          }
        double price = newTable.getParmValue().getDouble("PRICE",row);
        TParm parm = getTotalAmt(qty,price);
		newTable.setItem(row, "TOT_AMT",parm.getValue("FEES"));
//		System.out.println("newTable=====:"+newTable.getParmValue());
    }
    /**
     * �����ܽ��
     */
    public TParm getTotalAmt(double total, double ownPrice) {
        TParm parm = new TParm();
        double fees =  Math.abs(StringTool.round(total * ownPrice,2));
//    	System.out.println("fees=====:"+fees);
        parm.setData("FEES", fees);
        return parm;
    }
	/**
	 * ��ϸ���ܺ������½�����
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOT_AMT"};
		TParm parm = newTable.getParmValue();
//		System.out.println("parm111=======" + parm);
		TParm result = new TParm();
		// ���һ��������
		for (int i = 0; i < pagethree.length; i++) {
			result.setData(pagethree[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}
		result.setData("FLG", "Y"); // ��������
		if (parm.getCount() > 0) {
			// ��úϼ�����
			result.setData("CASE_NO", parm.getValue("CASE_NO",0)); // ����˳���		
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1); // �Ƴ��ϼ�
			int seqNo = -1; // ������˳�����
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1); // ˳���
			parm.setRowData(parm.getCount(), result, -1); // ����½�������
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1); // ���ϼ����·���
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("û�����ݲ������½�����");
			return;
		}
		newTable.setParmValue(parm);
	}
	/**
	 * ���SYS_FEE��������(�����鴰��)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		selectNewRow = row;
		// �����ǰ�к�
		column = newTable.getColumnModel().getColumnIndex(column);
		String columnName = newTable.getParmMap(column);
		// ҽ�� �� ��������
		if ("ORDER_CODE".equalsIgnoreCase(columnName)
				|| "QTY".equalsIgnoreCase(columnName)) {
		} else {
			return;
		}
		if ("ORDER_CODE".equalsIgnoreCase(columnName)) {
			TTextField textfield = (TTextField) com;
			TParm parm = new TParm();
			parm.setData("RX_TYPE", ""); // ������ CAT1_TYPE = LIS/RIS
			textfield.onInit();
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popExaReturn");
		}
	}
	/**
	 * ���¸�ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		newTable.acceptText();
		TParm newParm = newTable.getParmValue();
		newParm
				.setData("ORDER_CODE", selectNewRow, parm
						.getValue("ORDER_CODE")); // ҽ����
		newParm
				.setData("ORDER_DESC", selectNewRow, parm
						.getValue("ORDER_DESC")); // ҽ������
		newParm.setData("PRICE", selectNewRow, parm.getDouble("OWN_PRICE")); // ����
		newParm.setData("NHI_ORDER_CODE", selectNewRow, parm
				.getValue("NHI_CODE_I")); // ҽ�����ô���
		newTable.setParmValue(newParm);
	}
	
	/**
	 * ��ϸ���ܺ�����ɾ������
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("������ɾ���ϼ�����");
			return;
		}
        String sql = " DELETE FROM INS_XNH_UPLOAD " +
        		     " WHERE CASE_NO= '" + parm.getData("CASE_NO",row) + "' " +
        	         " AND SEQ_NO='" + parm.getData("SEQ_NO",row) + "'";
        TParm  result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		this.messageBox("P0005"); // ִ�гɹ�
		onSplitNew();
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = newTable.getParmValue();
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
				String tblColumnName = newTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		newTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	/**
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
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
	 * ת��parm�е���
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
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

}
