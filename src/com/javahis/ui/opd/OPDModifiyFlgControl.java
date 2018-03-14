/**
 * @className OPDSearchDailyRecorder.java 
 * @author litong
 * @Date 2013-4-7 
 * @version V 1.0 
 */
package com.javahis.ui.opd;

import java.sql.Timestamp;
import jdo.bil.BIL;
import jdo.ekt.EKTIO;
import jdo.opb.OPB; //import jdo.opd.OPDModifiyTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool; //import action.opd.OPDAction;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * @author yanjing
 * @Date 2013-4-7
 */
public class OPDModifiyFlgControl extends TControl {
	private TParm EKTTemp;// ҽ�ƿ����ֵ
	private TTable table;
	Pat pat;// pat����
	// reg����
	Reg reg;
	// BIL ����
	BIL opbbil;
	// ������洫��caseNo
	String caseNoPost;
	// ����ΨһcaseNo
	String onlyCaseNo;
	// OPB����
	OPB opb;
	// ������洫��mrNo
	String mrNoPost;
	int drOrderCount = -1;
	int drOrderCountTemp = 0;
	boolean drOrderCountFalse = false;
	boolean addOrder = true;
	private static final String SQL = "SELECT A.RX_NO,B.PAT_NAME,A.ORDER_CODE,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,'Y'AS USE,A.MR_NO,A.ORDER_DESC,A.EXEC_FLG FROM OPD_ORDER A,SYS_PATINFO B WHERE A.MR_NO = B.MR_NO AND CASE_NO = '#' AND ORDERSET_CODE=ORDER_CODE AND SETMAIN_FLG = 'Y'";
	private static final String SELBYCACE_NO = "SELECT SEE_DR_FLG,REG_DATE FROM REG_PATADM WHERE CASE_NO = '$'";

	/**
	 * ��ʼ������
	 * 
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		// ��ʼ������ʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("STARTTIME", date);
		this.setValue("EXEC_FLG", "N");
	}

	/**
	 * �õ�TABLE����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * ��������
	 * 
	 * @return boolean
	 */
	public void onReadEKT() {
		// ��ȡҽ�ƿ�����
		EKTTemp = EKTIO.getInstance().readEkt();
		if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
			this.messageBox(EKTTemp.getErrText());
			return;
		}
		this.setValue("MR_NO", EKTTemp.getValue("MR_NO"));
		
		//modify by huangtt 20160927 EMPI���߲�����ʾ  start
		 String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
		 Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
	            this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
	        }
		//modify by huangtt 20160927 EMPI���߲�����ʾ  end
		
		this.setValue("MR_NO", pat.getMrNo());
		
		return;
	}

	/**
	 * ����þ�����Ƿ���ҽ��
	 */
	public boolean checkOrderCount() {
		if (opb == null) {
			return true;
		}
		if (opb.checkOrderCount()) {
			// this.messageBox("�˲���û���");
			return true;
		}
		return false;
	}

	/**
	 * ���ݲ����Ŵ���������Ϣ
	 */
	public void onQurey() {
		table.removeRowAll();
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());

		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			this.grabFocus("PAT_NAME");
			return;
		}
		
		//modify by huangtt 20160927 EMPI���߲�����ʾ  start
		 String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
	            this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
	        }
		//modify by huangtt 20160927 EMPI���߲�����ʾ  end
		
		
		// ���渳ֵ
		setValueForParm("MR_NO;PAT_NAME;SEX_CODE;", pat.getParm());
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate()); // showAge(Timestamp birth,
		// Timestamp sysdate);
		setValue("AGE", age);
		// ���Ҿ����¼
		String regionCode = Operator.getRegion();
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(pat.getMrNo(),
				(Timestamp) getValue("STARTTIME"),
				(Timestamp) getValue("STARTTIME"), regionCode);
		// ���Ҵ���
		if (parm.getCount() < 0) {
			messageBox_("�������ѡ�����!");
			return;
		}
		// ���Һ���ϢΪ0
		if (parm.getCount() == 0) {
			// this.messageBox("�޽��չҺ���Ϣ!");
			// �������ѡ�����
			onRecord();
			return;
		}
		// �������ֻ��һ�ιҺ���Ϣ
		if (parm.getCount() == 1) {
			// ��ʼ��reg
			String caseNo = parm.getValue("CASE_NO", 0);
			this.setValue("CASE_NO", caseNo);
			reg = Reg.onQueryByCaseNo(pat, caseNo);
			// �жϹҺ���Ϣ
			if (reg == null) {
				return;
			}
			// ͨ��reg��caseNo�õ�pat
			opb = OPB.onQueryByCaseNo(reg);
			onlyCaseNo = opb.getReg().caseNo();
			// �������ϲ��ֵط���ֵ
			if (opb == null || checkOrderCount()) {
				this.messageBox("�˲�����δ����!");
				onRecord();
				return;
			} else {
				String medSql = SQL.replace("#", onlyCaseNo);
				TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
				if (med.getCount() < 1) {
					this.messageBox("û�з���Ҫ���ҽ����");
					return;
				}
				if (med.getErrCode() < 0) {
					this.messageBox_("��ѯҽ����Ϣʧ��");
					return;
				}
				table.setParmValue(med);
			}
			return;
		}
		onRecord();
	}

	/**
	 * �����¼ѡ��
	 */
	public void onRecord() {
		// ��ʼ��pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����!");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", this.getValue("AGE"));
		// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		// ���ݾ���Ų�ѯ�Ƿ��Ｐ����ʱ��
		String dateSql = SELBYCACE_NO.replace("$", caseNo);
		TParm dateParm = new TParm(TJDODBTool.getInstance().select(dateSql));
		String seeDDate = dateParm.getData("REG_DATE", 0).toString();
		String seeDate1 = seeDDate.substring(0, 10);
		String seeFlg1 = (String) dateParm.getData("SEE_DR_FLG", 0);
		String seeDate2 = seeDate1.replace("-", "/");
		this.setValue("STARTTIME", seeDate2);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("�Һ���Ϣ����!");
			return;
		}
		// ͨ��reg��caseNo�õ�opb
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = opb.getReg().caseNo();
		if (opb == null || seeFlg1.equals("N")) {
			this.messageBox_("�˾������δ����!");
			return;

		}
		String medSql = SQL.replace("#", caseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		if (med.getCount() < 1) {
			messageBox("û�з���Ҫ���ҽ����");
			return;
		}
		if (med.getErrCode() < 0) {
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		this.setValue("CASE_NO", caseNo);
		table.setParmValue(med);
	}

	/**
	 * �������
	 */
	public void onSave() {
		TTable table = getTable("TABLE");
		if (this.getValue("MR_NO").equals("") || null == this.getValue("MR_NO")) {
			this.messageBox("�����벡���ţ�");
			return;
		}
		if (table == null || "".equals(table)) {
			this.messageBox("û��Ҫ�����ҽ����");
			return;
		}
		table.acceptText();
		TParm parm = new TParm();
		TParm result = new TParm();
		TParm tableParm=table.getParmValue();
		int count=tableParm.getCount();
		int index=0;
		for (int i = 0; i < count; i++) {
			// �Ƿ�ѡ����
			if(tableParm.getValue("USE", i).equals("Y")){
				index++;//��ѡ�ۼ���Ҫ��������������
			}else{
				continue;
			}
			parm.addData("USE", tableParm.getValue("USE", i));
			//String rx_no = (String) table.getParmValue().getData("RX_NO", i);// ������
			parm.addData("CASE_NO", this.getValue("CASE_NO"));//�����
			parm.addData("EXEC_FLG", this.getValue("EXEC_FLG"));//ִ��״̬
			parm.addData("ORDERSET_CODE", tableParm.getValue(
					"ORDERSET_CODE", i));// ����ҽ���������
			parm.addData("ORDERSET_GROUP_NO", table.getParmValue().getInt(
					"ORDERSET_GROUP_NO", i));// ���
			parm.addData("RX_NO", tableParm.getValue("RX_NO", i));
		}
		if (index<=0) {
			this.messageBox("û��Ҫ���µ����ݣ�");
			return;
		}
		parm.setCount(index);
		result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"updateEXEC_FLG", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("���ݴ���");
			return;
		}
		this.messageBox("�޸ĳɹ���");
		String medSql = SQL.replace("#", onlyCaseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		table.setParmValue(med);
	}

	/**
	 * ȫѡ�¼�
	 */
	public void onSelAll() {
		String select = getValueString("CheckAll");
		table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			parm.setData("USE", i, select);
		}
		table.setParmValue(parm);
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		EKTTemp = null;
		String string = "MR_NO;PAT_NAME;SEX_CODE";
		this.clearValue(string);
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("STARTTIME", date);
		this.setValue("EXEC_FLG", "N");
		table.removeRowAll();

	}
}
