package com.javahis.ui.ins;

//import jdo.ins.INSADMConfirmTool;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
//import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHomePlace;
import com.javahis.util.StringUtil;

import org.apache.ws.xnh.XNHService;

public class XNHDownloadZZRecordsControl extends TControl {
	
	private TTable tTable ;
	
	private TextFormatHomePlace CHAREACODE ;
	
//	private TComboBox PAY_TYPE ;
	
	private String nhi_no="" ;
	private String nhi_name="";
	private String caseNO="";
	public DecimalFormat df = new DecimalFormat("##########0.00");
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.CHAREACODE = (TextFormatHomePlace)this.getComponent("CHAREACODE") ;
//		this.PAY_TYPE = (TComboBox)this.getComponent("PAY_TYPE") ;
//		PAY_TYPE.setValue("01");
//		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		this.nhi_no = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
		this.nhi_name= regionParm.getData("REGION_CHN_DESC", 0).toString();// ��ȡREGION_CHN_DESC
//	    this.setValue("YEAR_MON", SystemTool.getInstance().getDate());
	}

	/**
	 * ��ѯԤԼδ�᰸
	 */
//	public void onResvNClose() {
//		queryTemp(true);
//
//	}

	/**
	 * ��ѯסԺδ�᰸
	 */
	public void onAdmNClose() {
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
//		System.out.println("result::"+result);
		this.setValue("MR_NO", result.getValue("MR_NO"));
		this.setValue("PAT_NAME", result.getValue("PAT_NAME"));
		this.setValue("IDNO", result.getValue("IDNO"));
		caseNO = result.getValue("CASE_NO");// �����
	
//		Timestamp Indate = result.getTimestamp("IN_DATE");// סԺ����
//		System.out.println("Indate==============="+Indate);
//		TParm queryParm = new TParm();
//		queryParm.setData("CASE_NO", caseNO);
//		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
//		if (queryParm.getErrCode() < 0) {
//			this.messageBox("E0005");
//			return;
//		}
//		// System.out.println("resultl:::::::" + result);
//		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//ҽԺ����
//		setValue("OVERINP_FLG1", "N");
//		callFunction("UI|OVERINP_FLG1|setEnabled", true);
//		this.setValue("ADM_PRJ1", "2");// ��ҽר��
//		// getComboBox("PAY_TYPE").grabFocus();
//		this.grabFocus("ADM_CATEGORY1");// ��ҽ���
//		// getTextField("ADM_CATEGORY1").grabFocus();
	}
	/**
	 * ��ѯ����
	 * 
	 * @param flg
	 *            true:ԤԼδ�᰸ false:����Ȳ�ѯ
	 */
//	private void queryTemp(boolean flg) {
//		TParm parm = new TParm();
//		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
//			parm.setData("REGION_CODE", Operator.getRegion());
//		}
//		if (flg) {
//			parm.setData("FLG", "Y");
//		} else {
//			parm.setData("FLG", "N");
//		}
//		TParm result = (TParm) this.openDialog(
//				"%ROOT%\\config\\ins\\INSResvNClose.x", parm);
//		System.out.println("result::"+result);
//		if (this.getValue("IDNO1").toString().length() > 0) {
//			if (!result.getValue("IDNO").equals(this.getValue("IDNO1"))) {
//				this.messageBox("ˢ��������Ϣ��סԺ������Ϣ����");
//				return;
//			}
//		}
//		this.setValueForParm("RESV_NO;MR_NO;IN_DATE", result);
//		setValueParm(result);
//		this.setValue("DEPT_CODE1", result.getValue("DEPT_CODE"));
//		caseNO = result.getValue("CASE_NO");// �����
//		Indate = result.getTimestamp("IN_DATE");// סԺ����
////		System.out.println("Indate==============="+Indate);
//		if (!flg) {
//			// Ĭ��ѡ����
//			this.getRadioButton("RO_Open").setSelected(true);
//			//��������Ϊ"Y",���ɱ༭
//			this.setValue("OVERINP_FLG1", "Y");
//			callFunction("UI|OVERINP_FLG1|setEnabled", false);
//			//confirmNo = result.getValue("CONFIRM_NO");
//			callFunction("UI|INS_CROWD_TYPE|setEnabled", true);// ��Ⱥ���
//			//���껼��ȡ�ø��˱���
//			String sql = " SELECT  A.PERSONAL_NO, B.MRO_CTZ  " +
//					     " FROM JAVAHIS.INS_ADM_CONFIRM A, SYS_CTZ B  " +
//					     " WHERE  A.MR_NO  ='"+ getValue("MR_NO")+"' " +
//					     " AND  A.CASE_NO  ='"+caseNO+"' " +
//					     " AND  A.HIS_CTZ_CODE  = B.CTZ_CODE ";
//		    TParm resultIns = new TParm(TJDODBTool.getInstance().select(sql));
//		    if (resultIns.getErrCode() < 0) 
//		    {
//		      messageBox("���˱���ȡ��ʧ�ܣ�");
//		      return;
//		    }
//		    //���˱���
//		    setValue("PERSONAL_NO", resultIns.getData("PERSONAL_NO", 0));
//		    //��Ⱥ���
//		    setValue("INS_CROWD_TYPE", resultIns.getData("MRO_CTZ",0));
//		} else {
//			this.setValue("OVERINP_FLG1", "N");
//			callFunction("UI|OVERINP_FLG1|setEnabled", true);
//		}
//		this.setValue("DIAG_DESC1", result.getValue("DIAG_CODE")
//				+ result.getValue("ICD_CHN_DESC"));// סԺ���
//		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//ҽԺ����
//		this.setValue("ADM_PRJ1", "2");// ��ҽר��
//		// getComboBox("PAY_TYPE").grabFocus();
//		this.grabFocus("ADM_CATEGORY1");// ��ҽ���
//	}
	/**
	 * ��ʡת�����뵥����
	 */
	public void onReadCard(){

//		if(this.check()){
//			return;
//		}
//		System.out.println(XNHService.downloadZZRecords("", "", ""));
		TParm result=XNHService.downloadZZRecords(this.getValueString("CHAREACODE"), 
				this.getValueString("REFERRALCODE"), this.getValueString("IDNO"));
		if(result.getErrCode()==-1){
			messageBox(result.getErrText());
		    return;
		}
//		System.out.println("result.getCount(N507_05)"+result.getCount("N507_05"));
		int row=result.getCount("N507_05");
		for(int i=0;i<row;i++){
			String sqlDelete="DELETE FROM INS_XNH_DOWNLOADZZRECORDS WHERE N507_05 = '"+result.getValue("N507_05", i)+"'";
			String sqlInsert="INSERT INTO INS_XNH_DOWNLOADZZRECORDS (D507_01, D507_02, D507_03, D507_04, D507_05, D507_06, D507_07, D507_08, D507_09, D507_10, N507_01, N507_02, N507_05, N507_06, N507_07, N507_08, N507_09, N507_10, N507_11, N507_12, N507_13, N507_14, N507_15, N507_16, N507_17, N507_18, N507_19, N507_20, N507_21, N507_22, N507_23, N507_03, N507_04, N507_24, N507_25, N507_26, N507_27, N507_28, N507_29, N507_30, N507_31, N507_32, N507_33, N507_34, N507_35, N507_36, N507_37, N507_38, N507_39, N507_40, N507_41, N507_42, N507_43, N507_44, OPT_USER, OPT_DATE, OPT_TERM,CASE_NO) VALUES ('"+
			                 result.getValue("D507_01", i)+"', '"+result.getValue("D507_02", i)+"', '"+result.getValue("D507_03", i)+"', TO_DATE('"+result.getValue("D507_04", i)+"','YYYY-MM-DD HH24:MI:SS'), '"+result.getValue("D507_05", i)+"', '"+result.getValue("D507_06", i)+"', '"+result.getValue("D507_07", i)+"', '"+result.getValue("D507_08", i)+"', '"+result.getValue("D507_09", i)+"', TO_DATE('"+result.getValue("D507_10", i)+"','YYYY-MM-DD HH24:MI:SS'), '"+
			                 result.getValue("N507_01", i)+"', '"+result.getValue("N507_02", i)+"', '"+result.getValue("N507_05", i)+"', '"+result.getValue("N507_06", i)+"', '"+result.getValue("N507_07", i)+"', '"+result.getValue("N507_08", i)+"', '"+result.getValue("N507_09", i)+"', '"+result.getValue("N507_10", i)+"', '"+result.getValue("N507_11", i)+"', '"+result.getValue("N507_12", i)+"', '"+result.getValue("N507_13", i)+"', '"+result.getValue("N507_14", i)+"', '"+result.getValue("N507_15", i)+"', '"+result.getValue("N507_16", i)+"', TO_DATE('"+result.getValue("N507_17", i)+"','YYYY-MM-DD HH24:MI:SS'), '"+result.getValue("N507_18", i)+"', '"+result.getValue("N507_19", i)+"', '"+result.getValue("N507_20", i)+"', '"+
			                 result.getValue("N507_21", i)+"', '"+result.getValue("N507_22", i)+"', TO_DATE('"+result.getValue("N507_23", i)+"','YYYY-MM-DD HH24:MI:SS'), TO_DATE('"+result.getValue("N507_03", i)+"','YYYY-MM-DD HH24:MI:SS'), TO_DATE('"+result.getValue("N507_04", i)+"','YYYY-MM-DD HH24:MI:SS'), '"+result.getValue("N507_24", i)+"', '"+result.getValue("N507_25", i)+"', '"+result.getValue("N507_26", i)+"', '"+result.getValue("N507_27", i)+"', '"+result.getValue("N507_28", i)+"', '"+result.getValue("N507_29", i)+"', '"+result.getValue("N507_30", i)+"', '"+result.getValue("N507_31", i)+"', '"+result.getValue("N507_32", i)+"', '"+result.getValue("N507_33", i)+"', '"+result.getValue("N507_34", i)+"', "+result.getValue("N507_35", i)+", '"+result.getValue("N507_36", i)+"', '"+
			                 result.getValue("N507_37", i)+"', '"+result.getValue("N507_38", i)+"', '"+result.getValue("N507_39", i)+"', '"+result.getValue("N507_40", i)+"', '"+result.getValue("N507_41", i)+"', '"+result.getValue("N507_42", i)+"', '"+result.getValue("N507_43", i)+"', '"+result.getValue("N507_44", i)+"', '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+this.caseNO+"')";
			new TParm(TJDODBTool.getInstance().update(sqlDelete));
//			System.out.println("onInsItemRegDown_sql:"+sqlInsert);
			TParm resultIns = new TParm(TJDODBTool.getInstance().update(sqlInsert));
//			System.out.println("onInsItemRegDown_sql:"+resultIns);
			if (resultIns.getErrCode() < 0) {
				this.messageBox(resultIns.getErrText()+"!����������");
				return;
			}else{
//				this.messageBox("���سɹ�");
				this.setUITTable(result,"A");
			}
		}
	}
	public void setUITTable(TParm tabParm,String tag){
		if(tag.equals("A")){
			tTable.setHeader("���˴���,80;��������,80;��������,80;����ʱ��,80;����˵��,80;�����������,80;ת��ҽԺ����,80;ת��ҽԺ1����,80;ת��״̬����,100;�������,100;�κ���(��)��������,100;��������,100;ת�ﵥ��,100;�����������,100;ת��ҽԺ����,100;ת��ҽԺ1����,100;���������,100;���˵��,100;�κ���(��)��������,100;���״̬����,100;���֤��,100;���֤�Ŵ洢��ַ,100;ҽ��֤/����,100;ҽ��֤/���Ŵ洢��ַ,100;��������,100;�Ա����,100;�Ա�����,100;������ϵ��,100;������ϵ��ʽ,100;������ϵ����,100;��������,100;��������,100;��������,100;�����ִ���,100;����������,100;������ϵ��,100;������ϵ�绰,100;����������ʶ,100;�󲡾�����ʶ,100;��ͥ����,100;ת��ҽԺ2����,100;ת��ҽԺ2����,100;ת��ҽԺ3����,100;ת��ҽԺ3����,100;�ۼƽ��(Ԫ),100;�˻�����,100;���������˺�,100;������������,100;ҽ�Ƹ��ѷ�ʽ����,100;ҽ�Ƹ��ѷ�ʽ����,100;�κ�ʡ����,100;�κ�ʡ����,100;�κ��д���,100;�κ�������,100;������,100;����ʱ��,100;����IP,100");
			tTable.setParmMap("D507_01;D507_02;D507_03;D507_04;D507_05;D507_06;D507_07;D507_08;D507_09;D507_10;N507_01;N507_02;N507_05;N507_06;N507_07;N507_08;N507_09;N507_10;N507_11;N507_12;N507_13;N507_14;N507_15;N507_16;N507_17;N507_18;N507_19;N507_20;N507_21;N507_22;N507_23;N507_03;N507_04;N507_24;N507_25;N507_26;N507_27;N507_28;N507_29;N507_30;N507_31;N507_32;N507_33;N507_34;N507_35;N507_36;N507_37;N507_38;N507_39;N507_40;N507_41;N507_42;N507_43;N507_44;OPT_USER;OPT_DATE;OPT_TERM;CASE_NO");
		}else if(tag.equals("B")){
			tTable.setHeader("סԺ�Ǽ���ˮ��,80;��ҽ��������,80;��ҽ��������,80;��������,80;�����Ա����,80;�����Ա�����,80;�������֤��,80;����,80;����ͨѶ��ַ,100;�κ�ʡ����,100;�κ�ʡ����,100;�κ��д���,100;�κ�������,100;�κ�������,100;�κ�������,100;��ͥ����,100;���˴���,100;���,60;����,60;סԺ������ϴ���,100;סԺ�����������,100;�ڶ�������ϴ���,100;�ڶ������������,100;��������,100;��������,100;���Ʊ���,100;��������,100;��Ժ���Ҵ���,100;��Ժ��������,100;�������ʹ���,100;������������,100;����֢����,100;����֢����,100;��Ժ״̬����,100;��Ժ״̬����,100;��ϵ������,100;�绰����,100;ҽ������,100;��Ժ����,100;סԺ��,100;��λ��,100;��Ժ����,100;ת�����ʹ���,100;ת����������,100;ת�ﵥ��,100;תԺ����,100;ҽԺסԺ�շ��վݺ�,100;����֪ͨ���,100;����֤��,100;ҽ��֤/����,100;��������,100;����ʱ��,100;����ʱ��,100;ҽԺ��Ϣϵͳ�����ߴ���,100;ҽԺ��Ϣϵͳ����������,100;������ۼƱ������(Ԫ),100;������ۼƱ�������,100;������,100;����ʱ��,100;����IP,100");
			tTable.setParmMap("N801_01;N801_02;N801_03;N801_04;N801_05;N801_06;N801_07;N801_08;N801_09;N801_10;N801_11;N801_12;N801_13;N801_14;N801_15;N801_16;N801_17;N801_18;N801_19;N801_20;N801_21;N801_22;N801_23;N801_24;N801_25;N801_26;N801_27;N801_28;N801_29;N801_30;N801_31;N801_32;N801_33;N801_34;N801_35;N801_36;N801_37;N801_38;N801_39;N801_40;N801_41;N801_42;N801_43;N801_44;N801_45;N801_47;N801_48;N801_49;N801_50;N801_51;N801_52;N801_53;N801_54;N801_55;N801_56;N801_57;N801_58;OPT_USER;OPT_DATE;OPT_TERM");
		}
//		tTable.setItem("DEPT_CHN_DESC;MR_NO;PAT_NAME");
//		tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,right;11,right;12,right;13,right;14,right;");
		tTable.setParmValue(tabParm);

	}
	public void onConfirmNo(){
		if(this.check()){
			return;
		}
		String Sql =" SELECT * FROM INS_XNH_DOWNLOADZZRECORDS WHERE N507_13='"+this.getValueString("IDNO").trim()+"' ORDER BY N507_05 DESC";
//		System.out.println("onInsItemRegDown_sql:"+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if(tabParm.getCount("N507_05")<=0){
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			return;
		}
		this.setUITTable(tabParm,"A");
	}
	public void onSave(){
		if(this.check()){
			return;
		}
//		String sql=" SELECT A.CASE_NO N801_01,'' N801_02,'' N801_03,B.D507_02 N801_04,B.N507_18 N801_05,B.N507_19 N801_06,B.N507_13 N801_07,FLOOR(MONTHS_BETWEEN(SYSDATE,A.BIRTH_DATE)/12) N801_08,A.ADDRESS N801_09, "+
//			" B.N507_41 N801_10,B.N507_42 N801_11,B.N507_43 N801_12,B.N507_44 N801_13,B.N507_01 N801_14,B.N507_11 N801_15,B.N507_30 N801_16,B.D507_01 N801_17, "+
//			" '' N801_18,'' N801_19,A.OE_DIAG_CODE N801_20,E.ICD_CHN_DESC N801_21,'' N801_22,'' N801_23,'' N801_24,'' N801_25,'' N801_26,'' N801_27,A.IN_DEPT N801_28,C.DEPT_CHN_DESC N801_29, "+
//			" D.OUT_ID N801_30,D.OUT_DESC N801_31,'' N801_32,'' N801_33,A.IN_CONDITION N801_34,CASE A.IN_CONDITION WHEN '1' THEN 'Σ' WHEN '2' THEN '��' ELSE 'һ��' END N801_35, "+
//			" A.CONTACTER N801_36,A.CONT_TEL N801_37,''  N801_38,TO_CHAR(A.IN_DATE,'YYYY-MM-DD')  N801_39,A.CASE_NO  N801_40,''  N801_41,A.IN_STATION  N801_42,'' N801_43,'' N801_44, "+
//			" B.N507_05 N801_45,'' N801_47,'' N801_48,'' N801_49,'' N801_50,B.N507_15 N801_51,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') N801_52,TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') N801_53,TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') N801_54,'' N801_55,'' N801_56 "+
//			" FROM MRO_RECORD A,INS_XNH_DOWNLOADZZRECORDS B,SYS_DEPT C,SYS_DIC_FOROUT D,SYS_DIAGNOSIS E "+
//			" WHERE A.CASE_NO='"+this.caseNO+"' "+
//			" AND A.CASE_NO=B.CASE_NO "+
//			" AND A.IN_DEPT=C.DEPT_CODE "+
//			" AND D.GROUP_ID='S301-05' "+
//			" AND D.ID='I' "+
//			" AND��A.OE_DIAG_CODE=E.ICD_CODE�� ";
		String sql=" SELECT A.CASE_NO N801_01,'' N801_02,'' N801_03,B.D507_02 N801_04,B.N507_18 N801_05,B.N507_19 N801_06,B.N507_13 N801_07,FLOOR(MONTHS_BETWEEN(SYSDATE,A.BIRTH_DATE)/12) N801_08,A.ADDRESS N801_09, "+
		" B.N507_41 N801_10,B.N507_42 N801_11,B.N507_43 N801_12,B.N507_44 N801_13,B.N507_01 N801_14,B.N507_11 N801_15,B.N507_30 N801_16,B.D507_01 N801_17, "+
		" '' N801_18,'' N801_19,A.OE_DIAG_CODE N801_20,E.ICD_CHN_DESC N801_21,'' N801_22,'' N801_23,'' N801_24,'' N801_25,'' N801_26,'' N801_27,A.IN_DEPT N801_28,C.DEPT_CHN_DESC N801_29, "+
		" D.OUT_ID N801_30,D.OUT_DESC N801_31,'' N801_32,'' N801_33,A.IN_CONDITION N801_34,CASE A.IN_CONDITION WHEN '1' THEN 'Σ' WHEN '2' THEN '��' ELSE 'һ��' END N801_35, "+
		" A.CONTACTER N801_36,A.CONT_TEL N801_37,''  N801_38,TO_CHAR(A.IN_DATE,'YYYY-MM-DD')  N801_39,A.CASE_NO  N801_40,''  N801_41,A.IN_STATION  N801_42,'' N801_43,'' N801_44, "+
		" B.N507_05 N801_45,'' N801_47,'' N801_48,'' N801_49,'' N801_50,B.N507_15 N801_51,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') N801_52,TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') N801_53,TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') N801_54,'' N801_55,'' N801_56 "+
		" FROM MRO_RECORD A,INS_XNH_DOWNLOADZZRECORDS B,SYS_DEPT C,SYS_DIC_FOROUT D,SYS_DIAGNOSIS E "+
		" WHERE A.CASE_NO='"+this.caseNO+"' "+
		" AND A.CASE_NO=B.CASE_NO "+
		" AND A.IN_DEPT=C.DEPT_CODE "+
		" AND D.GROUP_ID='S301-05' "+
		" AND D.ID='I' "+
		" AND��A.OE_DIAG_CODE=E.ICD_CODE(+)�� ";
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if(tabParm.getCount("N801_01")<=0){
			this.messageBox("�����μ��ת�ﵥ���ء�סԺ�Ǽǡ��ֵ�����Ƿ���ȷ��");
			return;
		}
		tabParm.setData("N801_02", 0, XNHService.HOSPCODE);
		tabParm.setData("N801_03", 0, nhi_name);
		tabParm.setData("N801_55", 0, Operator.getID());
		tabParm.setData("N801_56", 0, Operator.getName());
		/////////////////////////////////////////////////////
		
		String []names={"N801_01", "N801_02", "N801_03", "N801_04", "N801_05", "N801_06", "N801_07", "N801_08", 
		                "N801_09", "N801_10", "N801_11", "N801_12", "N801_13", "N801_14", "N801_15", "N801_16", 
		                "N801_17", "N801_18", "N801_19", "N801_20", "N801_21", "N801_22", "N801_23", "N801_24", 
		                "N801_25", "N801_26", "N801_27", "N801_28", "N801_29", "N801_30", "N801_31", "N801_32", 
		                "N801_33", "N801_34", "N801_35", "N801_36", "N801_37", "N801_38", "N801_39", "N801_40", 
		                "N801_41", "N801_42", "N801_43", "N801_44", "N801_45", "N801_47", "N801_48", "N801_49", 
		                "N801_50", "N801_51", "N801_52", "N801_53", "N801_54", "N801_55", "N801_56"};
		TParm parm=XNHService.uploadInpRegister(tabParm,names);
		if(parm.getErrCode()<0){
			this.messageBox("סԺ�Ǽ��ϴ�ʧ�ܣ�������Ϣ����\n"+parm.getErrText());
			return;
		}
		/////////////////////////////////////////////////////////
		tabParm.addData("N801_57", parm.getValue("N801_57", 0));
		tabParm.addData("N801_58", parm.getValue("N801_58", 0));
		String sqlAdmInp="UPDATE ADM_INP SET CTZ1_CODE='88',OPT_USER='"+Operator.getID()+"', OPT_DATE=SYSDATE, OPT_TERM='"+Operator.getIP()+"' WHERE CASE_NO = '"+caseNO+"' ";
		new TParm(TJDODBTool.getInstance().update(sqlAdmInp));
		String sqlMro="UPDATE MRO_RECORD SET CTZ1_CODE='88',OPT_USER='"+Operator.getID()+"', OPT_DATE=SYSDATE, OPT_TERM='"+Operator.getIP()+"' WHERE CASE_NO = '"+caseNO+"' ";
		new TParm(TJDODBTool.getInstance().update(sqlMro));
		if(inpRegisterInsert(tabParm)){
			this.setUITTable(tabParm,"B");
		}
	}

	public boolean inpRegisterInsert(TParm parm){
		//û��N801_46
		String sql="INSERT INTO INS_XNH_INPREGISTER (N801_01, N801_02, N801_03, N801_04, N801_05, N801_06, N801_07, N801_08, N801_09, N801_10, N801_11, N801_12, N801_13, N801_14, N801_15, N801_16, N801_17, N801_18, N801_19, N801_20, N801_21, N801_22, N801_23, N801_24, N801_25, N801_26, N801_27, N801_28, N801_29, N801_30, N801_31, N801_32, N801_33, N801_34, N801_35, N801_36, N801_37, N801_38, N801_39, N801_40, N801_41, N801_42, N801_43, N801_44, N801_45, N801_47, N801_48, N801_49, N801_50, N801_51, N801_52, N801_53, N801_54, N801_55, N801_56, N801_57, N801_58, OPT_USER, OPT_DATE, OPT_TERM) VALUES ('"+
		parm.getValue("N801_01", 0)+"', '"+parm.getValue("N801_02", 0)+"', '"+parm.getValue("N801_03", 0)+"', '"+parm.getValue("N801_04", 0)+"', '"+parm.getValue("N801_05", 0)+"', '"+parm.getValue("N801_06", 0)+"', '"+parm.getValue("N801_07", 0)+"', '"+parm.getValue("N801_08", 0)+"', '"+parm.getValue("N801_09", 0)+"', '"+parm.getValue("N801_10", 0)+"', '"+parm.getValue("N801_11", 0)+"', '"+parm.getValue("N801_12", 0)+"', '"+parm.getValue("N801_13", 0)+"', '"+parm.getValue("N801_14", 0)+"', '"+parm.getValue("N801_15", 0)+"', '"+parm.getValue("N801_16", 0)+"', '"+parm.getValue("N801_17", 0)+"', '"+
		parm.getValue("N801_18", 0)+"', '"+parm.getValue("N801_19", 0)+"', '"+parm.getValue("N801_20", 0)+"', '"+parm.getValue("N801_21", 0)+"', '"+parm.getValue("N801_22", 0)+"', '"+parm.getValue("N801_23", 0)+"', '"+parm.getValue("N801_24", 0)+"', '"+parm.getValue("N801_25", 0)+"', '"+parm.getValue("N801_26", 0)+"', '"+parm.getValue("N801_27", 0)+"', '"+parm.getValue("N801_28", 0)+"', '"+parm.getValue("N801_29", 0)+"', '"+parm.getValue("N801_30", 0)+"', '"+parm.getValue("N801_31", 0)+"', '"+parm.getValue("N801_32", 0)+"', '"+parm.getValue("N801_33", 0)+"', '"+parm.getValue("N801_34", 0)+"', '"+
		parm.getValue("N801_35", 0)+"', '"+parm.getValue("N801_36", 0)+"', '"+parm.getValue("N801_37", 0)+"', '"+parm.getValue("N801_38", 0)+"', TO_DATE('"+parm.getValue("N801_39", 0)+"','YYYY-MM-DD HH24:MI:SS'), '"+parm.getValue("N801_40", 0)+"', '"+parm.getValue("N801_41", 0)+"', '"+parm.getValue("N801_42", 0)+"', '"+parm.getValue("N801_43", 0)+"', '"+parm.getValue("N801_44", 0)+"', '"+parm.getValue("N801_45", 0)+"', TO_DATE('"+parm.getValue("N801_47", 0)+"','YYYY-MM-DD HH24:MI:SS'), '"+parm.getValue("N801_48", 0)+"', '"+parm.getValue("N801_49", 0)+"', '"+parm.getValue("N801_50", 0)+"', '"+parm.getValue("N801_51", 0)+"', TO_DATE('"+parm.getValue("N801_52", 0)+"','YYYY-MM-DD HH24:MI:SS'), TO_DATE('"+
		parm.getValue("N801_53", 0)+"','YYYY-MM-DD HH24:MI:SS'), TO_DATE('"+parm.getValue("N801_54", 0)+"','YYYY-MM-DD HH24:MI:SS'), '"+parm.getValue("N801_55", 0)+"', '"+parm.getValue("N801_56", 0)+"', "+(parm.getValue("N801_57", 0).isEmpty()?0:parm.getValue("N801_57", 0))+", "+(parm.getValue("N801_58", 0).isEmpty()?0:parm.getValue("N801_58", 0))+", '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"')";
//		System.out.println("inpRegisterInsert:"+sql);
		TParm resultIns = new TParm(TJDODBTool.getInstance().update(sql));
//		System.out.println("inpRegisterInsert:"+resultIns);
		if (resultIns.getErrCode() < 0) {
			this.messageBox(resultIns.getErrText()+"!�ϴ��ɹ�,����ʧ��,��ʹ��סԺ�Ǽǲ�ѯ����");
			return false;
		}
		return true;
	}
	
	public void onEveInsPat(){
		if(this.check()){
			return;
		}
		String Sql =" SELECT * FROM INS_XNH_INPREGISTER WHERE N801_01='"+this.caseNO+"' AND N801_02='"+XNHService.HOSPCODE+"' AND CANCEL_FLG='N' ORDER BY N801_01 DESC";
//		System.out.println("onInsItemRegDown_sql:"+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if(tabParm.getCount("N801_01")<=0){
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			return;
		}
		this.setUITTable(tabParm,"B");
	}
	public boolean check(){
		if(this.caseNO.isEmpty()){
			this.messageBox("��ѡ��סԺ����");
			return true;
		}
		if(this.getValueString("IDNO").equals("")){
			this.messageBox("��¼�뻼�����֤��");
			return true;
		}
		return false;
	}
	public void onCancelInpRegister(){
		if(this.check()){
			return;
		}
//		System.out.println(this.messageBox("����", "�˲�����ȡ��סԺ�Ǽ���Ϣ�ϴ�", 2));
		if (this.messageBox("����", "�˲�����ȡ��סԺ�Ǽ���Ϣ�ϴ�", 2) == 2) {
			return;
		}
		TParm tabParm = getPat();
		
		if(tabParm.getCount("D507_01")<=0){
			this.messageBox("�����ظû���\n��ʡת�����뵥��");
			return;
		}
		
		TParm parm=XNHService.cancelInpRegister(tabParm.getValue("N507_01", 0),this.caseNO,Operator.getName());
		if(parm.getErrCode()<0){
			this.messageBox("סԺ�Ǽ�ȡ��ʧ�ܣ�������Ϣ����\n"+parm.getErrText());
			return;
		}
		String sqlAdmInp="UPDATE ADM_INP SET CTZ1_CODE='99',OPT_USER='"+Operator.getID()+"', OPT_DATE=SYSDATE, OPT_TERM='"+Operator.getIP()+"' WHERE CASE_NO = '"+caseNO+"' ";
		new TParm(TJDODBTool.getInstance().update(sqlAdmInp));
		String sqlMro="UPDATE MRO_RECORD SET CTZ1_CODE='99',OPT_USER='"+Operator.getID()+"', OPT_DATE=SYSDATE, OPT_TERM='"+Operator.getIP()+"' WHERE CASE_NO = '"+caseNO+"' ";
		new TParm(TJDODBTool.getInstance().update(sqlMro));
//		String sql="UPDATE XNH_INPREGISTER SET CANCEL_FLG='Y',OPT_USER='"+Operator.getID()+"', OPT_DATE=SYSDATE, OPT_TERM='"+Operator.getIP()+"' WHERE N801_01 = '"+caseNO+"' AND N801_02='"+XNHService.HOSPCODE+"' ";
		String sql="DELETE FROM INS_XNH_INPREGISTER WHERE N801_01 = '"+caseNO+"' AND N801_02='"+XNHService.HOSPCODE+"' ";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("�����ɹ���");
	}
	public void onInpRegisterSeek(){
		if(this.check()){
			return;
		}
		TParm tabParm = getPat();
		
		if(tabParm.getCount("D507_01")<=0){
			this.messageBox("�����ظû���\n��ʡת�����뵥��");
			return;
		}
		
		TParm parm=XNHService.InpRegisterSeek(tabParm.getValue("N507_01", 0),tabParm.getValue("D507_01", 0), caseNO);
		if(parm.getErrCode()<0){
			this.messageBox("סԺ�Ǽǲ�ѯʧ�ܣ�������Ϣ����\n"+parm.getErrText());
			return;
		}
		this.setUITTable(parm,"B");
	}
	
	public TParm getPat(){
		String Sql =" SELECT D507_01,N507_01 FROM INS_XNH_DOWNLOADZZRECORDS WHERE N507_13='"+this.getValueString("IDNO").trim()+"' ORDER BY N507_05 DESC";
//		System.out.println("onInsItemRegDown_sql:"+Sql); 
		return new TParm(TJDODBTool.getInstance().select(Sql));
	}
	
	  /**
	    * ��ũ�Ͻ��㵥
	    */
	   public void onPrint(){
		   String Sql ="SELECT A.PAT_NAME,A.IN_NO IDNO,C.N801_08 AS AGE,A.HOSP_DESC,A.IPD_NO, "+
		   " A.MAIN_DIAG_DESC MAIN_ICD_DESC,TO_CHAR(A.IN_DATE,'YYYY-MM-DD') IN_DATE, TO_CHAR(A.DS_DATE,'YYYY-MM-DD') DS_DATE,E.REAL_STAY_DAYS IN_COUNT, "+
		   " TO_CHAR(A.SETTLE_DATE,'YYYY-MM-DD') CHARGE_DATE,A.BED_AMT CHARGE01, A.ZC_AMT CHARGE02, A.JC_AMT CHARGE03, A.HY_AMT CHARGE04, A.ZL_AMT CHARGE05, A.OP_AMT CHARGE06, A.HL_AMT CHARGE07, "+ 
		   " A.CL_AMT CHARGE08, A.XY_AMT CHARGE09, A.ZCY_AMT CHARGE10, A.CY_AMT CHARGE11, A.YSFU_AMT CHARGE12, A.YBZL_AMT CHARGE13, A.OTHER_AMT CHARGE14, "+
		   " A.TOT_AMT, A.ESPENSE_TOT_AMT NHI_PAY,(A.TOT_AMT-A.ESPENSE_TOT_AMT) AS OWN_PAY,( C.N801_57+A.REAL_INS_AMT) AS ADD_AMT, "+
		   " 0.00 AS SINGLEDISEASES_PAY,0.00 AS SINGLEDISEASES_PAY_REAL,A.INSURANCE_AMT SERIOUSILL_PAY, A.INSURANCE_REAL_AMT SERIOUSILL_PAY_REAL,A.REAL_INS_AMT NHI_PAY_REAL,A.DEDUCTIBLE_AMT STARTINGLINE, "+
		   " A.OWN_AMT OWN_PAY_REAL,A.TOP_AMT TOPLINE,A.ADDRESS_DESC ADDRESS,A.TEL_NO TEL,D.DEPT_CHN_DESC DEPT,TO_CHAR(SYSDATE,'YYYY-MM-DD') DATE1 "+
		   " FROM INS_XNH A,ADM_INP B,INS_XNH_INPREGISTER C,SYS_DEPT D,MRO_RECORD E "+
		   " WHERE A.CASE_NO = '"+this.caseNO+"' "+
		   " AND A.CASE_NO = B.CASE_NO "+
		   " AND A.CASE_NO = C.N801_01 "+
		   " AND B.DS_DEPT_CODE = D.DEPT_CODE" +
		   " AND A.CASE_NO = E.CASE_NO ";
//		   System.out.println(Sql);
		   
		   TParm tableParm = new TParm(TJDODBTool.getInstance().select(Sql));

		   TParm printParm =new TParm();
		   printParm.setData("PAT_NAME","TEXT", tableParm.getValue("PAT_NAME",0));//��������
		   printParm.setData("IDNO","TEXT", tableParm.getValue("IDNO",0));//���֤��
		   printParm.setData("AGE", "TEXT",tableParm.getValue("AGE", 0)) ;//����
		   printParm.setData("REGION_CHN_DESC","TEXT", tableParm.getValue("HOSP_DESC", 0)) ;//ҽ�ƻ�������
		   printParm.setData("CASE_NO","TEXT",tableParm.getValue("IPD_NO", 0)) ;//סԺ��
		   printParm.setData("MAIN_ICD_DESC","TEXT",tableParm.getValue("MAIN_ICD_DESC",0)) ;//��Ҫ���
		   printParm.setData("IN_DATE","TEXT",tableParm.getValue("IN_DATE",0)) ;//��Ժ����
		   printParm.setData("DS_DATE","TEXT",tableParm.getValue("DS_DATE",0)) ;//��Ժ����
		   printParm.setData("IN_COUNT","TEXT",tableParm.getValue("IN_COUNT",0)) ;//סԺ����
		   printParm.setData("CHARGE_DATE","TEXT",tableParm.getValue("CHARGE_DATE",0)) ;//��������
		   printParm.setData("CHARGE01","TEXT",df.format(tableParm.getDouble("CHARGE01",0))) ;//��λ��
		   printParm.setData("CHARGE02","TEXT",df.format(tableParm.getDouble("CHARGE02",0))) ;//����
		   printParm.setData("CHARGE03","TEXT",df.format(tableParm.getDouble("CHARGE03",0))) ;//����
		   printParm.setData("CHARGE04","TEXT",df.format(tableParm.getDouble("CHARGE04",0))) ;//�����
		   printParm.setData("CHARGE05","TEXT",df.format(tableParm.getDouble("CHARGE05",0))) ;//���Ʒ�
		   printParm.setData("CHARGE06","TEXT",df.format(tableParm.getDouble("CHARGE06",0))) ;//������
		   printParm.setData("CHARGE07","TEXT",df.format(tableParm.getDouble("CHARGE07",0))) ;//�����
		   printParm.setData("CHARGE08","TEXT",df.format(tableParm.getDouble("CHARGE08",0))) ;//�������Ϸ�
		   printParm.setData("CHARGE09","TEXT",df.format(tableParm.getDouble("CHARGE09",0))) ;//��ҩ��
		   printParm.setData("CHARGE10","TEXT",df.format(tableParm.getDouble("CHARGE10",0))) ;//�г�ҩ��
		   printParm.setData("CHARGE11","TEXT",df.format(tableParm.getDouble("CHARGE11",0))) ;//�в�ҩ��
		   printParm.setData("CHARGE12","TEXT",df.format(tableParm.getDouble("CHARGE12",0))) ;//ҩ�·����
		   printParm.setData("CHARGE13","TEXT",df.format(tableParm.getDouble("CHARGE13",0))) ;//һ�����Ʒ�
		   printParm.setData("CHARGE14","TEXT",df.format(tableParm.getDouble("CHARGE14",0))) ;//����סԺ��
		   printParm.setData("TOT_AMT","TEXT",df.format(tableParm.getDouble("TOT_AMT",0))) ;//�ܷ���
		   printParm.setData("NHI_PAY","TEXT",df.format(tableParm.getDouble("NHI_PAY",0))) ;//�ɲ�����
		   printParm.setData("OWN_PAY","TEXT",df.format(tableParm.getDouble("OWN_PAY",0))) ;//���ɲ�����
		   printParm.setData("ADD_AMT","TEXT",df.format(tableParm.getDouble("ADD_AMT",0))) ;//�ۼƲ���
		   printParm.setData("SINGLEDISEASES_PAY","TEXT",df.format(tableParm.getDouble("SINGLEDISEASES_PAY",0))) ;//�����ֿɲ�����
		   printParm.setData("SINGLEDISEASES_PAY_REAL","TEXT",df.format(tableParm.getDouble("SINGLEDISEASES_PAY_REAL",0))) ;//�����ֲ�������
		   printParm.setData("SERIOUSILL_PAY","TEXT",df.format(tableParm.getDouble("SERIOUSILL_PAY",0))) ;//�󲡿ɲ�����
		   printParm.setData("SERIOUSILL_PAY_REAL","TEXT",df.format(tableParm.getDouble("SERIOUSILL_PAY_REAL",0))) ;//���ֲ�������

		   printParm.setData("NHI_PAY_REAL","TEXT",df.format(tableParm.getDouble("NHI_PAY_REAL",0))) ;//ֱ�����(Сд)
		   printParm.setData("NHI_PAY_REAL_WORDS","TEXT",StringUtil.getInstance().numberToWord(tableParm.getDouble("NHI_PAY_REAL",0))) ;//ֱ�����(��д)
		   printParm.setData("STARTINGLINE","TEXT",df.format(tableParm.getDouble("STARTINGLINE",0))) ;//����
		   printParm.setData("OWN_PAY_REAL","TEXT",df.format(tableParm.getDouble("OWN_PAY_REAL",0))) ;//�Ը����(Сд)
		   printParm.setData("OWN_PAY_REAL_WORDS","TEXT",StringUtil.getInstance().numberToWord(tableParm.getDouble("OWN_PAY_REAL",0))) ;//�Ը����(��д)
		   printParm.setData("TOPLINE","TEXT",df.format(tableParm.getDouble("TOPLINE",0))) ;//�ⶥ��

		   printParm.setData("ADDRESS","TEXT",tableParm.getValue("ADDRESS",0)) ;//���ߵ�ַ
		   printParm.setData("TEL","TEXT",tableParm.getValue("TEL",0)) ;//��ϵ�绰
		   printParm.setData("NAME","TEXT",Operator.getName()) ;//ҽԺ������
		   printParm.setData("DEPT","TEXT",tableParm.getValue("DEPT",0)) ;//�������
		   printParm.setData("DATE","TEXT",tableParm.getValue("DATE1",0)) ;//����
		   
		   //		   printParm.setData("AMT_IN_WORDS","TEXT",StringUtil.getInstance().numberToWord(tot)) ;//
	       this.openPrintWindow("%ROOT%\\config\\prt\\INS\\XNH_Printdetail.jhw",printParm);    
	   } 
		/**
		 * �������ı���س��¼�
		 */
		public void onMrNo() {
			// TParm parm = getTableSeleted();
			// if (null == parm) {
			// return;
			// }
			Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
			if (pat == null) {
				this.messageBox("�޴˲�����!");
				return;
			}
			this.setValue("MR_NO", pat.getMrNo());
			this.setValue("PAT_NAME", pat.getName());
			this.setValue("IDNO", pat.getIdNo());
			
			TParm parm = new TParm();
			//=============pangben 2012-6-18 start ���סԺ��ϢУ�� �ж�case_NO
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT MR_NO,CASE_NO FROM ADM_INP WHERE CANCEL_FLG = 'N' ");
			String temp = "";
			if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
				parm.setData("REGION_CODE", Operator.getRegion());
				temp = " AND  REGION_CODE='" + Operator.getRegion() + "'";
			}
			parm.setData("MR_NO", pat.getMrNo());
			sql.append(" AND MR_NO='" + pat.getMrNo() + "'" + temp);
			TParm result = new TParm(TJDODBTool.getInstance()
					.select(sql.toString()));
			if (result.getCount()<=0) {
				this.messageBox("�˲���û��סԺ��Ϣ");
				this.setValue("MR_NO", "");
				this.setValue("PAT_NAME", "");
				this.setValue("IDNO", "");
				return;
			}
			parm.setData("FLG","Y");
			if (result.getCount("MR_NO") > 1) {
				result = (TParm) this.openDialog(
						"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
				this.caseNO = result.getValue("CASE_NO");
			} else {
				this.caseNO = result.getValue("CASE_NO", 0);
			}
			//=============pangben 2012-6-18 STOP
			// TParm result = INSIbsTool.getInstance().queryIbsSum(parm);//
			// ��ѯ���ݸ����渳ֵ
			// setSumValue(result, parm);
		}
		/**
		 * ���
		 */
		public void onClear(){
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
			this.setValue("IDNO", "");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			this.caseNO="";
		}

}
