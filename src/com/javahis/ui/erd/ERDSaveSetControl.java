package com.javahis.ui.erd;

import java.sql.Timestamp;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;
import jdo.ekt.EKTIO;
import jdo.erd.ERDLevelTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: ���������趨  </p>
 *
 * <p>Description: ���������趨</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author wangqing 200170623
 *
 * @version 1.0
 */
public class ERDSaveSetControl extends TControl {
    /**
     * ϵͳ������Ĭ��ֵΪRECORD
     */
	private String runFlg = "RECORD";
	/**
	 * �Ѽ���TRadioButton
	 */
	private TRadioButton Radio0;
	/**
	 * �ѹҺ�TRadioButton
	 */
	private TRadioButton Radio1;
	/**
	 * �ѿ���TRadioButton
	 */
	private TRadioButton Radio2;
	/**
	 * ������TRadioButton
	 */
	private TRadioButton Radio3;
	/**
	 * ��ת��TRadioButton
	 */
	private TRadioButton Radio4;
	/**
	 * ��ѯ��ʼ����TTextFormat
	 */
	private TTextFormat from_Date;
	/**
	 * ��ѯ��ֹ����TTextFormat
	 */
	private TTextFormat to_Date;
	/**
	 * ������TTextField
	 */
	private TTextField MR_NO;
	/**
	 * ���˺�TTextField
	 */
	private TTextField TRIAGE_NO;
	/**
	 * ��������TTextField
	 */
	private TTextField PAT_NAME;
	/**
	 * ��λ����TTextField
	 */
	private TTextField BED_DESC;
	/**
	 * ��������TTextFormat
	 */
	private TTextFormat ERD_REGION_CODE;
	
	private TTable table;
	private TPanel panel;
	private String workPanelTag;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// ��ȡϵͳ����
		Object obj = this.getParameter();// RECORD|CHECK
		if (obj != null && obj instanceof String) {
			runFlg = this.getParameter().toString();
		}
		// ��ʼ������
		if(runFlg != null && runFlg.equals("RECORD")){
			this.setTitle("���������趨");
		}else if(runFlg != null && runFlg.equals("CHECK")){
			this.setTitle("�������Ȼ�ʿվ");
		}else{
			
		}
		// ������ĳ�ʼ��
		myInitControler();
		// Ĭ��ѡ��Radio3
		Radio3.setSelected(true);
		// ��ʼ��ʱ������
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int effDays = 3;// ʱ����3��
		Timestamp last3day = StringTool.rollDate(now, -effDays);
		from_Date.setValue(StringTool.getDate(StringTool.getString(last3day, "yyyyMMdd")+ "000000", "yyyyMMddHHmmss"));
		to_Date.setValue(now);
		// ִ�в�ѯ
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					onQuery();
				}
				catch (Exception e) {}
			}
		});		
	}

	/**
	 * ��ʼ���ؼ����󶨼����¼�
	 */
	public void myInitControler() {
		// �õ�ʱ��ؼ�
		from_Date = (TTextFormat) this.getComponent("from_Date");
		to_Date = (TTextFormat) this.getComponent("to_Date");
		// �õ�table�ؼ�
		table = (TTable) this.getComponent("TABLE");
		// �õ���ѯ����UI�Ķ���
		Radio0 = (TRadioButton) this.getComponent("Radio0");
		Radio1 = (TRadioButton) this.getComponent("Radio1");
		Radio2 = (TRadioButton) this.getComponent("Radio2");
		Radio3 = (TRadioButton) this.getComponent("Radio3");
		Radio4 = (TRadioButton) this.getComponent("Radio4");
		MR_NO = (TTextField) this.getComponent("MR_NO");
		// 
		TRIAGE_NO = (TTextField) this.getComponent("TRIAGE_NO");
		PAT_NAME = (TTextField) this.getComponent("NAME");
		BED_DESC = (TTextField) this.getComponent("BED_DESC");
		ERD_REGION_CODE = (TTextFormat) this.getComponent("ERD_REGION_CODE");
		// 
		panel = (TPanel) this.getComponent("PANEL");
		
		// TABLE�����¼�
		callFunction("UI|TABLE|addEventListener", "TABLE" + "->" + TTableEvent.CLICKED, this, "onTableClicked");
		
		// add by wangqing 20171211 table˫���¼�
		callFunction("UI|TABLE|addEventListener", "TABLE" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");
	}
	
	/**
	 * ��ѯ
	 * @author wangqing 20171123
	 */
	public void onQuery() {
		if(table==null){
			table = (TTable) this.getComponent("TABLE");
		}
		table.setParmValue(null);// ���table����
		// ��ȡ��ѯ����
		TParm selParm = this.getQueryParm();
		if(selParm==null){
			this.messageBox("��ȡ��ѯ����ʧ��");
			return;
		}
		// ִ�в�ѯ
		TParm query = this.query(selParm);
		if(query.getErrCode()<0){
			this.messageBox("��ѯʧ��");
			return;
		}
		if (query.getCount() <= 0) {
			this.messageBox("û��������ݣ�");
			return;
		}
		table.setParmValue(query);	
	}
	
	/**
	 * ��ȡ��ѯ����
	 * @author wangqing 20170626
	 * @return TParm
	 */
	public TParm getQueryParm() {
		// FLG 0���Ѽ��� ��1���ѹҺţ�2���ѿ��3�������У�4����ת��
		TParm parm = new TParm();
		if(Radio0.isSelected()){// �Ѽ���	
			parm.setData("FLG", "0");
		}else if(Radio1.isSelected()){// �ѹҺ�	
			parm.setData("FLG", "1");
		}else if (Radio2.isSelected()) {// �Կ���
			parm.setData("FLG", "2");
		}else if (Radio3.isSelected()) {// ������
			parm.setData("FLG", "3");
		}else if (Radio4.isSelected()) {// ��ת��			
			parm.setData("FLG", "4");
		}
		// ��ѯʱ������
		if (getValueString("from_Date").trim().length() == 0
				|| getValueString("to_Date").trim().length() == 0 
				|| getValueString("from_Date").compareTo(getValueString("to_Date")) > 0) {
			messageBox("ʱ�����ڲ��Ϸ�");
			return null;
		}
		// ���ڸ�ʽ���� 2017-06-26 14:12:00.0 -> 2017/06/26 14:12:00
		String fromDate = getValueString("from_Date").replace("-", "/").substring(0, 16);
		String toDate = getValueString("to_Date").replace("-", "/").substring(0, 16);
		parm.setData("FROM_DATE", fromDate);
		parm.setData("TO_DATE", toDate);	
		// ������
		if (getValueString("ERD_REGION").length() != 0) {
			parm.setData("ERD_REGION", getValue("ERD_REGION"));
		}	
		// ������
		if (getValueString("MR_NO").length() != 0){
			String mrNo = getValueString("MR_NO");
			Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
			String srcMrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			}

			this.setValue("MR_NO", pat.getMrNo());
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		} 
		// ���˺�
		if (getValueString("TRIAGE_NO").length() != 0) {
			parm.setData("TRIAGE_NO", getValue("TRIAGE_NO"));
		}
		
		return parm;
	}
	
	/**
	 * ��ѯ���� 
	 * @author wangqing 20171123
	 * @param parm
	 * @return
	 */
	public TParm query(TParm parm){
		// �Ѽ��ˣ��Ѽ��ˣ�δ�Һţ�δ���ȣ�
		String sql0 = " SELECT TRIAGE_NO, CASE_NO, '' AS MR_NO, '' AS PAT_NAME, ERD_REGION_CODE, BED_NO, '' AS BED_DESC "
				+ "FROM ERD_EVALUTION "
				+ "WHERE TRIAGE_TIME BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "AND CASE_NO IS NULL "
				+ "AND ERD_REGION_CODE IS NULL "
				+ "AND BED_NO IS NULL "
				+ "AND OUT_DATE IS NULL "
				+ "ORDER BY TRIAGE_NO DESC";// �����˺ŵ���			
		// �ѹҺţ��Ѽ��ˣ��ԹҺţ�δ���ȣ�
		String sql1 = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, B.PAT_NAME, C.ERD_REGION_CODE, C.BED_NO, '' AS BED_DESC "
				+ "FROM REG_PATADM A, SYS_PATINFO B, ERD_EVALUTION C "
				+ "WHERE A.MR_NO=B.MR_NO(+) "
				+ "AND A.TRIAGE_NO=C.TRIAGE_NO(+) "
				+ "AND A.MR_NO IS NOT NULL "
				+ "AND A.TRIAGE_NO IS NOT NULL "
				+ "AND A.ADM_STATUS='1' "// �ѹҺţ�1���ѿ��2
				+ "AND A.REG_DATE BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "AND C.ERD_REGION_CODE IS NULL "
				+ "AND C.BED_NO IS NULL "
				+ "AND C.OUT_DATE IS NULL "
				+ "ORDER BY A.CASE_NO DESC ";// ������ŵ���		
		// �ѿ���Ѽ��ˣ��ԹҺţ��ѿ��δ���ȣ�
		String sql2 = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, B.PAT_NAME, C.ERD_REGION_CODE, C.BED_NO, '' AS BED_DESC "
				+ "FROM REG_PATADM A, SYS_PATINFO B, ERD_EVALUTION C "
				+ "WHERE A.MR_NO=B.MR_NO(+) "
				+ "AND A.TRIAGE_NO=C.TRIAGE_NO(+) "
				+ "AND A.MR_NO IS NOT NULL "
				+ "AND A.TRIAGE_NO IS NOT NULL "
				+ "AND A.ADM_STATUS='2' "// �ѹҺţ�1���ѿ��2
				+ "AND A.REG_DATE BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "AND C.ERD_REGION_CODE IS NULL "
				+ "AND C.BED_NO IS NULL "
				+ "AND C.OUT_DATE IS NULL "
				+ "ORDER BY A.CASE_NO DESC ";// ������ŵ���
		// ������
		String sql3 = " SELECT A.ERD_REGION_CODE, A.BED_NO, A.BED_DESC, A.TRIAGE_NO, B.CASE_NO, B.MR_NO, B.PAT_NAME "
				+ "FROM ERD_BED A, (SELECT A.TRIAGE_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME FROM ERD_EVALUTION A, (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME FROM REG_PATADM A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+) AND A.MR_NO IS NOT NULL)B WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO IS NOT NULL)B "
				+ "WHERE A.TRIAGE_NO=B.TRIAGE_NO(+) "
				+ "AND A.OCCUPY_FLG='Y' "
				+ "AND A.TRIAGE_NO IS NOT NULL "
				+ "ORDER BY ERD_REGION_CODE, BED_NO ";		
		// ��ת��
		String sql4 = "SELECT A.TRIAGE_NO, A.ERD_REGION_CODE, A.BED_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME, C.BED_DESC "
				+ "FROM ERD_EVALUTION A, (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME FROM REG_PATADM A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+))B, ERD_BED C "
				+ "WHERE A.CASE_NO=B.CASE_NO(+) "
				+ "AND A.ERD_REGION_CODE=C.ERD_REGION_CODE(+) "
				+ "AND A.BED_NO=C.BED_NO(+) "
				+ "AND A.CASE_NO IS NOT NULL "
				+ "AND A.ERD_REGION_CODE IS NOT NULL "
				+ "AND A.BED_NO IS NOT NULL "
				+ "AND A.OUT_DATE IS NOT NULL "
				+ "AND A.OUT_DATE BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "ORDER BY A.OUT_DATE ";
		TParm result = new TParm();		
		String sql = "";
		// �Ѽ���
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("0")){
			sql = sql0;
		}
		// �ѹҺ�
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("1")){
			sql = sql1;
		}
		// �ѿ���
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("2")){
			sql = sql2;
		}
		// ������
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("3")){
			sql = sql3;
		}
		// ��ת��
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("4")){
			sql = sql4;
		}	
		result = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("@test by wangqing@---FLG="+parm.getValue("FLG"));
		System.out.println("@test by wangqing@---sql="+sql);
		System.out.println("@test by wangqing@---result="+result);
		return result;
	}
	
	/**
	 * �л���ѯ��ť
	 */
	public void onChangeAction(){
		this.onClear();// ���
		onQuery();
	}
	
	/**
	 * �����¼�
	 * @param row
	 */
	public void onTableClicked(int row){		
		if (row < 0){
			return;
		}
		TParm tableValue = table.getParmValue();
		String caseNo = (String) tableValue.getData("CASE_NO", row);
		String mrNo = (String) tableValue.getData("MR_NO", row);
		String triageNo = (String) tableValue.getData("TRIAGE_NO", row);
		String patName = (String) tableValue.getData("PAT_NAME", row);		
		String  bedNo= (String) tableValue.getData("BED_NO", row);
		String erdRegionCode = (String) tableValue.getData("ERD_REGION_CODE", row);
		String  bedDesc= (String) tableValue.getData("BED_DESC", row);
		// ��ֵ
		MR_NO.setValue(mrNo);
		TRIAGE_NO.setValue(triageNo);
		PAT_NAME.setValue(patName);	
		BED_DESC.setValue(bedDesc);
		ERD_REGION_CODE.setValue(erdRegionCode);
//		// �򿪹�������
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", caseNo);
//		parm.setData("MR_NO", mrNo);
//		parm.setData("TRIAGE_NO", triageNo);
//		parm.setData("PAT_NAME", patName);	
//		parm.setData("ERD_REGION_CODE", erdRegionCode);
//		parm.setData("BED_NO", bedNo);
//		parm.setData("BED_DESC", bedDesc);		
//		parm.setData("FLG", "NURSE");// ��ʿ���ñ�ǣ��б���ҽ��			
//		
//		
//		System.out.println("---caseNo="+caseNo);
//		
//		if(runFlg !=null && runFlg.equals("RECORD")){// ���������趨		
//			this.openWindow("%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parm, false);				
//		}else if(runFlg !=null && runFlg.equals("CHECK")){// �������Ȼ�ʿվ
//			if(caseNo == null || caseNo.trim().length()<=0){
//				this.messageBox("�˲���δ�Һţ�����");
//				return;
//			}
//			String sql = " SELECT ADM_STATUS FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' ";
//			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//			if(result.getErrCode()<0){
//				this.messageBox("bug:::��ѯADM_STATUS����");
//				return;
//			}
//			if(result.getCount()<=0){
//				this.messageBox("bug:::û�д˲����Һ���Ϣ������");
//				return;
//			}
//			if(result.getValue("ADM_STATUS", 0) == null 
//					|| result.getValue("ADM_STATUS", 0).trim().length()<=0 
//					|| !result.getValue("ADM_STATUS", 0).equals("2")){
//				this.messageBox("�˲���δ�������");
//				return;
//			}
//			this.openWindow("%ROOT%\\config\\erd\\ERDOrderExecMain.x", parm, false);	
//		}else{
//			
//		}
	}
	
	/**
	 * ˫���¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubled(int row) {
		if (row < 0) return;
		TParm tableValue = table.getParmValue();
		String caseNo = (String) tableValue.getData("CASE_NO", row);
		String mrNo = (String) tableValue.getData("MR_NO", row);
		String triageNo = (String) tableValue.getData("TRIAGE_NO", row);
		String patName = (String) tableValue.getData("PAT_NAME", row);		
		String  bedNo= (String) tableValue.getData("BED_NO", row);
		String erdRegionCode = (String) tableValue.getData("ERD_REGION_CODE", row);
		String  bedDesc= (String) tableValue.getData("BED_DESC", row);
		// ��ʼ���ؼ�
		MR_NO.setValue(mrNo);
		TRIAGE_NO.setValue(triageNo);
		PAT_NAME.setValue(patName);	
		BED_DESC.setValue(bedDesc);
		ERD_REGION_CODE.setValue(erdRegionCode);
		if(runFlg!=null && runFlg.equals("RECORD")){
			lockUpContorl(false);
			// ���ý��洫��
			TParm parmToErd = new TParm();
			parmToErd.setData("CASE_NO", caseNo);
			parmToErd.setData("MR_NO", mrNo);
			parmToErd.setData("TRIAGE_NO", triageNo);
			parmToErd.setData("PAT_NAME", patName);	
			parmToErd.setData("ERD_REGION_CODE", erdRegionCode);
			parmToErd.setData("BED_NO", bedNo);
			parmToErd.setData("BED_DESC", bedDesc);
			// ��ʿ���ñ�ǣ��б���ҽ��
			parmToErd.setData("FLG", "NURSE");
			// ����ERD��¼������
			table.setVisible(false);
			panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parmToErd, false);
			workPanelTag = "ERDDynamicRcd";
		}else if(runFlg!=null && runFlg.equals("CHECK")){
			if(caseNo == null || caseNo.trim().length()<=0){
				this.messageBox("�˲���δ�������");
				return;
			}
			String sql = " SELECT ADM_STATUS FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()<0){
				this.messageBox("bug:::��ѯADM_STATUS����");
				return;
			}
			if(result.getCount()<=0){
				this.messageBox("bug:::û�д˲�����Ϣ������");
				return;
			}
			if(result.getValue("ADM_STATUS", 0)!=null && result.getValue("ADM_STATUS", 0).equals("2")){
				
			}else{
				this.messageBox("�˲���δ�������");
				return;
			}		
			
			// add by wangqing 20180205 start
			// �������Ȼ�ʿվ��ÿ�ν�ȥ��ʱ���жϻ����Ƿ��������в���ERD_RECORD�Ƿ��м�¼��������ߴ��������в���ERD_RECORDû�м�¼����¼һ������
			String s = "";
			TParm r = new TParm();
			s = "SELECT A.ERD_REGION_CODE, A.BED_NO, A.BED_DESC, A.OCCUPY_FLG, A.TRIAGE_NO FROM ERD_BED A "
					+ "WHERE A.OCCUPY_FLG='Y' AND A.TRIAGE_NO='"+triageNo+"'";
			r = new TParm(TJDODBTool.getInstance().select(s));	
			if(r.getErrCode()<0){
				this.messageBox(r.getErrText());
				return;
			}				
			TParm erdRecord = getErdRecord(caseNo);
			if(erdRecord.getErrCode()<0){
				this.messageBox(erdRecord.getErrText());
				return;
			}			
			if(r.getCount()>0 && erdRecord.getCount()<=0){
				// ����ERD_RECORD���Ҹ���ERD_BED
				this.insertErdRecordAndUpdateErdBed(mrNo, caseNo, triageNo);
			}
			// add by wangqing 20180205 end
						
			lockUpContorl(false);
			// ���ý��洫��
			TParm parmToExec = new TParm();
			parmToExec.setData("MR_NO", mrNo);
			parmToExec.setData("CASE_NO", caseNo);
			parmToExec.setData("PAT_NAME", patName);			
			parmToExec.setData("TRIAGE_NO", triageNo);
			parmToExec.setData("ERD_REGION_CODE", erdRegionCode);
			parmToExec.setData("BED_NO", bedNo);
			parmToExec.setData("BED_DESC", bedDesc);
			// ����ERD��¼������
			table.setVisible(false);
			panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDOrderExecMain.x", parmToExec, false);
			workPanelTag = "ERDDynamicRcd";
		}else{
			return;
		}
	}
	
	/**
	 * ��շ���
	 */
	public void onClear() {
		setValue("MR_NO", "");
		setValue("TRIAGE_NO", "");
		setValue("PAT_NAME", "");
		setValue("ERD_REGION", "");
		setValue("BED_DESC", "");
		((TTable) getComponent("TABLE")).removeRowAll();
	}
	
	/**
	 * ҽ�ƿ���������
	 */
	public void onEKT() {
		TParm patParm = EKTIO.getInstance().TXreadEKT();
		// TParm patParm = EKTIO.getInstance().getPat();
		if (patParm.getErrCode() < 0) {
			this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
			return;
		}
		setValue("MR_NO", patParm.getValue("MR_NO"));
		onMrNo();
	}
	
	/**
	 * ����MR_NO
	 */
	public void onMrNo() {
		String mrNo = MR_NO.getValue();
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
		String srcMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
			this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
		}
		MR_NO.setValue(pat.getMrNo());
		PAT_NAME.setValue(pat.getName());
	}

	/**
	 * �����������鿴
	 */
	public void onErdTriage(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		TParm dataD = table.getParmValue();
		String caseNo = dataD.getValue("CASE_NO",row);
		String mrNo = dataD.getValue("MR_NO",row);
		String triageNo = dataD.getValue("TRIAGE_NO",row);
		if(triageNo.length() == 0){
			this.messageBox("��ѡ���м��˺ŵĲ�����");
			return;
		}
		String[] saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		parm.setData("ADM_DATE", reg.getAdmDate());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX", pat.getSexString());
		parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate())); //����
		TParm emrFileData = new TParm();
		emrFileData.setData("FILE_PATH", saveFiles[0]);
		emrFileData.setData("FILE_NAME", saveFiles[1]);
		emrFileData.setData("FLG", true);
		parm.setData("EMR_FILE_DATA", emrFileData);
		parm.setData("SYSTEM_TYPE", "EMG");
		parm.setData("RULETYPE", "1");
		parm.setData("ERD",true); 
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * ����
	 */
	public void onTest(){
		TParm parm = new TParm();	
		parm.setData("triageNo", "20171123001");
		this.openWindow("%ROOT%\\config\\reg\\REGPreviewingPreedWindow.x", parm);	
	}
	
	/**
	 * �رչ���ҳ��
	 * 
	 * @return boolean
	 */
	public Object onClose() {
		if (workPanelTag == null || workPanelTag.trim().length() <= 0){ 
			return null;
		}
		TPanel p = (TPanel) getComponent(workPanelTag);
		if(p==null){
			return null;
		}else{
			
		}
		if (!p.getControl().onClosing()) {
			return "OK";
		}
		panel.remove(p);
		workPanelTag = null;
		table.setVisible(true);
		// �Ƴ���UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// �Ƴ���UIToolBar
		callFunction("UI|removeChildToolBar");
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
		lockUpContorl(true);
		onQuery();
		return "OK";
	}
	
	// --------------tool start--------------
	/**
	 * �����������ϱߵĿؼ�
	 * 
	 * @param flg
	 *            boolean
	 */
	private void lockUpContorl(boolean flg) {
		Radio0.setEnabled(flg);
		Radio1.setEnabled(flg);
		Radio2.setEnabled(flg);
		Radio3.setEnabled(flg);
		Radio4.setEnabled(flg);
		MR_NO.setEnabled(flg);
		PAT_NAME.setEnabled(flg);
		TRIAGE_NO.setEnabled(flg);
		BED_DESC.setEnabled(flg);
		ERD_REGION_CODE.setEnabled(flg);
	}
	// --------------tool end----------------
	
	
	
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("erd\\ERDMainEnter.x");
	}
	
	// ------------------------------------add by wangqing 2080131 start------------------------------
	
	/**
	 * ����ERD_RECORD���Ҹ���ERD_BED
	 * @author wangqing 20180131
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 */
	public TParm insertErdRecordAndUpdateErdBed(String mrNo, String caseNo, String triageNo){
		TParm parm = new TParm();
		parm = copyPatDate(mrNo, caseNo, triageNo);
		if(parm.getErrCode()<0){
			return parm;
		}
		parm = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "insertErdRecordAndUpdateErdBed", parm);
		return parm;
	}
	
	/**
	 * ���ƻ��߻�����Ϣ
	 * @param mrNo
	 * @param caseNo
	 * @param inDate
	 * @param erdRegion
	 * @param bedNo
	 * @return
	 */
	public TParm copyPatDate(String mrNo, String caseNo, Timestamp inDate, String erdRegion, String bedNo) {
		// ���߻�����Ϣ
		TParm sysPatInfoParm = this.getPatInfo(mrNo);
		if(sysPatInfoParm.getErrCode()<0 || sysPatInfoParm.getCount()<=0){
			sysPatInfoParm.setErrCode(-1);
			sysPatInfoParm.setErrText("��ȡ���߻�����Ϣ����");
			return sysPatInfoParm;
		}	
		// ���߹Һ���Ϣ
		TParm sysRegPatAdm = this.getPatRegInfo(caseNo);
		if(sysRegPatAdm.getErrCode()<0 || sysRegPatAdm.getCount()<=0){
			sysRegPatAdm.setErrCode(-1);
			sysRegPatAdm.setErrText("��ȡ���߹Һ���Ϣ����");
			return sysRegPatAdm;
		}
				
		/*// ������Ϣ
		TParm erdParm = getErdInfo(triageNo);
		if(erdParm.getErrCode()<0 || erdParm.getCount()<=0){
			return erdParm;
		}	
		// ��ʼ����ʱ��
		TParm erdStartTimeParm = getErdStartTime(triageNo);
		if(erdStartTimeParm.getErrCode()<0 || erdStartTimeParm.getCount()<=0){
			return erdStartTimeParm;
		}*/	
				
		TParm result = new TParm();
		
		result.setData("CASE_NO", caseNo);
		
		result.setData("MR_NO", mrNo);
		
		result.setData("ERD_NO", "");// ����

		result.setData("STATUS", "0");// 0�������У�1����Ժ��2��תסԺ��3���ٻ�
		
		setNull(result, "DISCHG_TYPE;DISCHG_DATE;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE;RETURN_DATE");

		// ���û��߻�����Ϣ
		setValue(sysPatInfoParm, result, "PAT_NAME;SEX;AGE;BIRTH_DATE;MARRIGE;OCCUPATION;RESID_PROVICE;RESID_PROVICE_DESC;RESID_COUNTRY;"
				+ "FOLK;NATION;IDNO;CTZ1_CODE;OFFICE;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL");
		
		setNull(result, "O_ADDRESS;O_TEL;O_POSTNO;H_ADDRESS;H_TEL;H_POSTNO");

		/*if(erdStartTimeParm.getData("IN_DATE", 0)!=null && erdStartTimeParm.getData("IN_DATE", 0).toString().trim().length()>0){
			result.setData("IN_DATE", erdStartTimeParm.getData("IN_DATE", 0));// ����������
		}else{
			result.setData("IN_DATE", "");// ����������
		}			
		result.setData("ERD_REGION", erdParm.getValue("ERD_REGION_CODE", 0));// ��������
		// ���ȴ�
		result.setData("BED_NO", erdParm.getValue("BED_NO", 0));*/	
		result.setData("IN_DATE", inDate);// ����������
		
		result.setData("ERD_REGION", erdRegion);// ��������
		
		result.setData("BED_NO", bedNo);// ���ȴ�
				
		result.setData("IN_DEPT", sysRegPatAdm.getValue("DEPT_CODE", 0));// �����ȿ���
		
		setNull(result, "OUT_DATE;OUT_ERD_REGION;OUT_DEPT");

		setNull(result, "OUT_DIAG_CODE;CODE_REMARK;CODE_STATUS;HEAL_LV");

		setNull(result, "OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL");

		setNull(result, "GET_TIMES;SUCCESS_TIMES");

		result.setData("DR_CODE", sysRegPatAdm.getValue("REALDR_CODE", 0));

		result.setData("REAL_STAY_DAYS", 1);// ����һ��COPY���ݵ�ERD_RECORD���е�ʱ��Ĭ������-��1��

		setNull(result, "ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE");

		result.setData("OPT_USER", Operator.getID());

		result.setData("OPT_TERM", Operator.getIP());
		return result;
	}
	
	/**
	 * ���ƻ��߻�����Ϣ
	 * @author WangQing
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 * @return
	 */
	public TParm copyPatDate(String mrNo, String caseNo, String triageNo) {
		// ���߻�����Ϣ
		TParm sysPatInfoParm = this.getPatInfo(mrNo);
		if(sysPatInfoParm.getErrCode()<0 || sysPatInfoParm.getCount()<=0){
			sysPatInfoParm.setErrCode(-1);
			sysPatInfoParm.setErrText("��ȡ���߻������ݴ���");
			return sysPatInfoParm;
		}	
		// ���߹Һ���Ϣ
		TParm sysRegPatAdm = this.getPatRegInfo(caseNo);
		if(sysRegPatAdm.getErrCode()<0 || sysRegPatAdm.getCount()<=0){
			sysRegPatAdm.setErrCode(-1);
			sysRegPatAdm.setErrText("��ȡ���߹Һ����ݴ���");
			return sysRegPatAdm;
		}		
		// ������Ϣ
		TParm erdParm = getErdInfo(triageNo);
		if(erdParm.getErrCode()<0 || erdParm.getCount()<=0){
			erdParm.setErrCode(-1);
			erdParm.setErrText("��ȡ����������Ϣ����");
			return erdParm;
		}	
		// ��ʼ����ʱ��
		TParm erdStartTimeParm = getErdStartTime(triageNo);
		if(erdStartTimeParm.getErrCode()<0 || erdStartTimeParm.getCount()<=0){
			erdStartTimeParm.setErrCode(-1);
			erdStartTimeParm.setErrText("��ȡ���߿�ʼ����ʱ�����");
			return erdStartTimeParm;
		}	
		
		TParm result = new TParm();
		result.setData("CASE_NO", caseNo);
		result.setData("MR_NO", mrNo);
		
		result.setData("TRIAGE_NO", triageNo);// ���˺�
		
		result.setData("ERD_NO", "");// ����
		result.setData("STATUS", "0");// 0�������У�1����Ժ��2��תסԺ��3���ٻ�
		setNull(result, "DISCHG_TYPE;DISCHG_DATE;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE;RETURN_DATE");
		
		// ������Ϣ
		setValue(sysPatInfoParm, result, "PAT_NAME;SEX;AGE;BIRTH_DATE;MARRIGE;OCCUPATION;RESID_PROVICE;RESID_PROVICE_DESC;RESID_COUNTRY;"
				+ "FOLK;NATION;IDNO;CTZ1_CODE;OFFICE;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL");
		setNull(result, "O_ADDRESS;O_TEL;O_POSTNO;H_ADDRESS;H_TEL;H_POSTNO");

		// �����봲����
		if(erdStartTimeParm.getData("IN_DATE", 0)!=null && erdStartTimeParm.getData("IN_DATE", 0).toString().trim().length()>0){
			result.setData("IN_DATE", erdStartTimeParm.getData("IN_DATE", 0));// ����������
		}else{
			result.setData("IN_DATE", "");// ����������
		}			
		result.setData("ERD_REGION", erdParm.getValue("ERD_REGION_CODE", 0));// ��������
		
		result.setData("BED_NO", erdParm.getValue("BED_NO", 0));// ���ȴ�
				
		/*result.setData("IN_DATE", inDate);// ����������
		result.setData("ERD_REGION", erdRegion);// ��������
		result.setData("BED_NO", bedNo);*/
		
		result.setData("IN_DEPT", sysRegPatAdm.getValue("DEPT_CODE", 0));// �����ȿ���
		
		setNull(result, "OUT_DATE;OUT_ERD_REGION;OUT_DEPT");

		setNull(result, "OUT_DIAG_CODE;CODE_REMARK;CODE_STATUS;HEAL_LV");

		setNull(result, "OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL");

		setNull(result, "GET_TIMES;SUCCESS_TIMES");

		result.setData("DR_CODE", sysRegPatAdm.getValue("REALDR_CODE", 0));

		result.setData("REAL_STAY_DAYS", 1);// ����һ��COPY���ݵ�ERD_RECORD���е�ʱ��Ĭ������-��1��

		setNull(result, "ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE");

		result.setData("OPT_USER", Operator.getID());
		
		result.setData("OPT_TERM", Operator.getIP());

		return result;
	}

	/**
	 * ��ȡ���߻�����Ϣ
	 * @param mrNo
	 * @return
	 */
	public TParm getPatInfo(String mrNo){
		String sql = "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0 || parm.getCount()<=0){
			parm.setErrCode(-1);
			parm.setErrText("getPatInfo err");			
			return parm;
		}
		// �����ֶΣ���һ������
		parm.setData("SEX", 0, parm.getData("SEX_CODE", 0));
		parm.setData("MARRIGE", 0, parm.getData("MARRIAGE_CODE", 0));
		parm.setData("OCCUPATION", 0, parm.getData("OCC_CODE", 0));

		// 
		if(parm.getData("BIRTH_DATE", 0)!=null){
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			parm.setData("AGE", 0, StringUtil.showAge(parm.getTimestamp("BIRTH_DATE", 0), now));
		}else{
			parm.setData("AGE", 0, "");
		}

		// RESID_PROVICEʡ���
		if(parm.getValue("RESID_POST_CODE", 0)!=null && parm.getValue("RESID_POST_CODE", 0).trim().length()>=2){
			parm.setData("RESID_PROVICE", 0, parm.getValue("RESID_POST_CODE", 0).substring(0, 2));
		}else{
			parm.setData("RESID_PROVICE", 0, "");
		}
		// RESID_PROVICE_DESCʡ����
		if(parm.getValue("RESID_PROVICE", 0)!=null && parm.getValue("RESID_PROVICE", 0).trim().length()>0){
			parm.setData("RESID_PROVICE_DESC", 0, getPatHome(parm.getValue("RESID_PROVICE", 0)).getValue("HOMEPLACE_DESC", 0));		
		}else{
			parm.setData("RESID_PROVICE_DESC", 0, "");	
		}
		// �������	
		parm.setData("RESID_COUNTRY", 0, parm.getValue("RESID_POST_CODE", 0));
		
		parm.setData("FOLK", 0, parm.getValue("SPECIES_CODE", 0));
		parm.setData("NATION", 0, parm.getValue("NATION_CODE", 0));
		parm.setData("OFFICE", 0, parm.getValue("COMPANY_DESC", 0));

		parm.setData("CONTACTER", 0, parm.getValue("CONTACTS_NAME", 0));
		parm.setData("RELATIONSHIP", 0, parm.getValue("RELATION_CODE", 0));
		parm.setData("CONT_ADDRESS", 0, parm.getValue("CONTACTS_ADDRESS", 0));
		parm.setData("CONT_TEL", 0, parm.getValue("CONTACTS_TEL", 0));
		return parm;
	}

	/**
	 * ��ȡ���߹Һ���Ϣ
	 * @param caseNo
	 * @return
	 */
	public TParm getPatRegInfo(String caseNo) {
		String sql = " SELECT REALDR_CODE,ADM_DATE FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * У�黼���Ƿ�ռ��
	 * @param traigeNo ���˺�
	 * @return result�����result.getCount()>0��ռ��������û��ռ��
	 */
	public TParm getErdInfo(String triageNo){
		String sql =" SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE TRIAGE_NO='"+triageNo+"' AND OCCUPY_FLG='Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * ��ѯ���ȿ�ʼʱ��
	 * @param triageNo
	 * @return
	 */
	public TParm getErdStartTime(String triageNo){
		String sql = " SELECT MIN(S_M_TIME) AS IN_DATE FROM AMI_E_S_RECORD WHERE TRIAGE_NO='"+triageNo+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ������ֵ
	 * @param parm
	 * @param names
	 * @return
	 */
	public boolean setNull(TParm parm, String names){
		if(parm==null){
			return false;
		}
		if(names==null || names.trim().length()<=0){
			return false;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			parm.setData(nameArr[i], "");
		}
		return true;
	}

	/**
	 * ������ֵ
	 * @author wangqing 20171205
	 * @param parm0
	 * @param parm1
	 * @param names
	 * @return
	 */
	public boolean setValue(TParm parm0, TParm parm1, String names){
		if(parm0==null){
			return false;
		}
		if(parm1==null){
			return false;
		}
		if(names==null || names.trim().length()<=0){
			return false;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			parm1.setData(nameArr[i], parm0.getData(nameArr[i], 0));
		}
		return true;
	}
	
	/**
	 * ��ȡʡ������
	 * @param code
	 * @return
	 */
	public TParm getPatHome(String code) {
		String sql = "SELECT HOMEPLACE_DESC FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	

	/**
	 * ��ȡERD_RECORD����
	 * @param caseNo
	 * @return
	 */
	public TParm getErdRecord(String caseNo){ 
		String sql = "SELECT * FROM ERD_RECORD WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	
	
	// ------------------------------------add by wangqing 2080131 end------------------------------


	
	
	

}
