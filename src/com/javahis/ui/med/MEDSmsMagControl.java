package com.javahis.ui.med;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.med.MedSmsTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
/**
 * Σ��ֵ�Ǽǣ�SEND_TIME���Ǽ�ʱ�䣻REPORT_USER���Ǽ��ˣ�REPORT_DEPT_CODE���Ǽǿ���
 * @author wangqing
 *
 */
public class MEDSmsMagControl extends TControl {

	/**
	 * TABLE
	 */
	private static String TABLE = "Table";

	// ��¼���ѡ������
	int selectedRowIndex = -1;

	TTable table;

	private TParm data;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent(TABLE);
		// ע���񵥻��¼�
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");		
		// ��ʼ����ѯ����
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("BEGIN_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 00:00:00");
//		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("SMS_STATE", "1");
		this.setValue("REPORT_USER", Operator.getID());
		this.setValue("REPORT_DEPT_CODE", Operator.getDept());
	}

	/**
	 * �õ����ؼ�
	 * 
	 * @param tableName
	 *            �������
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

	/**
	 * �����Żس����ܣ������Ų��㡢�ϲ������š����ݲ����Ų�ѯ������Ϣ������ҳ��
	 */
	public void onQueryNO() {
		TParm parm = new TParm();
		String mrNo = getValueString("MR_NO");
		String amdType = getValueString("ADM_TYPE");
		if (amdType == null || "".equals(amdType.trim())) {
			this.messageBox("�ż�ס�����Ϊ�գ�");
			return;
		}	
		if (mrNo.length() > 0) {
			// �����Ų��㡢�ϲ�������
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			// modify by huangtt 20160929 EMPI���߲�����ʾ start
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				mrNo = pat.getMrNo();
			}
			this.setValue("MR_NO", pat.getMrNo());// ������
			// modify by huangtt 20160929 EMPI���߲�����ʾ end		
			TParm patNameParm = MedSmsTool.getInstance().getPatName(mrNo);
			if(patNameParm.getErrCode()<0){
				return;
			}
			if(patNameParm.getCount()<=0){
				this.messageBox("û�в�ѯ���˲�������");
				return;
			}
			String patName = patNameParm.getValue("PAT_NAME", 0);
			this.setValue("PATIENT_NAME", patName);
			parm.setData("ADM_TYPE", amdType);
			parm.setData("MR_NO", mrNo);
			TParm result = MedSmsTool.getInstance().getPatInfoByMrNo(parm);
			if(result.getErrCode()<0){
				return;
			}
			if (result.getCount() <= 0) {
				this.messageBox("�޶�Ӧ������Ϣ��");
				return;
			}
			this.setValue("CASE_NO", result.getValue("CASE_NO", 0));
			this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
			this.setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
			this.setValue("BED_NO", result.getValue("BED_NO", 0));
			if(amdType.equals("I")){
				this.setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
			}else{
				this.setValue("CLINIC_CODE", result.getValue("CLINICAREA_CODE", 0));
			}
		} 
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.removeRowAll();
		TParm selectCondition = getParmForTag(
				"ADM_TYPE;MR_NO;DEPT_CODE;SMS_STATE;REPORT_DEPT_CODE;REPORT_USER", true);
		// modified by WangQing 20170502
		if(this.getValueString("ADM_TYPE").equals("I")){
			selectCondition.setData("STATION_CODE", getValue("STATION_CODE"));
		}else{
			selectCondition.setData("STATION_CODE", getValue("CLINIC_CODE"));
		}
		selectCondition.setData("BEGIN_TIME", getValue("BEGIN_TIME"));
		selectCondition.setData("END_TIME", getValue("END_TIME"));
//		System.out.println("--------selectCondition:"+selectCondition);
		data = MedSmsTool.getInstance().onQuery(selectCondition);
		
		if (data.getErrCode() < 0) {
			messageBox("�޷��������ļ�¼��");
			messageBox(data.getErrText());
			return;
		} else {
//			callFunction("UI|Table|setParmValue", new Object[] { data });
			table.setParmValue(data);
			
			for (int i = 0; i < data.getCount(); i++) {
				TParm p = data.getRow(i);
				long time = getDiffTime(p.getValue("SEND_TIME"));
				// System.out.println("time=========:"+time);
				if (!p.getValue("STATE").equals("9")) {
					if (time > 0 && time < 31) {
						/** ����ɫ */
						this.getTable(TABLE).setRowTextColor(i,
								new Color(0, 128, 255));
					}
					if (time > 30 && time < 41) {
						/** ��ɫ */
						this.getTable(TABLE).setRowTextColor(i,
								new Color(0, 0, 255));
					}
					if (time > 41) {
						/** ��ɫ */
						this.getTable(TABLE).setRowTextColor(i,
								new Color(255, 0, 0));
					}
				} else {
					this.getTable(TABLE).setRowTextColor(i, new Color(0, 0, 0));
				}
			}
//			table.setParmValue(data);
			return;
		}
	}
	
	/**
	 * ��񵥻�����
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		} else {
			// modified by WangQing 20170502 			
			String tagNames = "ADM_TYPE;MR_NO;PATIENT_NAME;DEPT_CODE;SMS_CODE;" +
			"TESTITEM_CHN_DESC;TEST_VALUE;CRTCLLWLMT;BILLING_DOCTORS;" +
			"HANDLE_OPINION;CASE_NO;BED_NO;IPD_NO;REPORT_USER;REPORT_DEPT_CODE";
			setValueForParm(tagNames , data, row);
			this.changeAdmType();
			if(this.getValue("ADM_TYPE").equals("I")){
				this.setValue("STATION_CODE", data.getValue("STATION_CODE", row));
			}else{
				this.setValue("CLINIC_CODE", data.getValue("STATION_CODE", row));
			}			
			this.setValue("SMS_STATE", data.getValue("STATE", row));
			selectedRowIndex = row;
			return;
		}
	}

	/**
	 * ����¼�
	 */
	public void onClear() {
		this.clearValue("ADM_TYPE;CASE_NO;IPD_NO;BED_NO;STATION_CODE;DEPT_CODE;" +
				"BEGIN_TIME;END_TIME;MR_NO;HANDLE_OPINION;SMS_STATE;SMS_CODE;PATIENT_NAME;" +
				"CLINIC_CODE;TESTITEM_CHN_DESC;TEST_VALUE;CRTCLLWLMT;BILLING_DOCTORS;REPORT_USER;REPORT_DEPT_CODE");
		selectedRowIndex = -1;
		TParm parm = new TParm();
		table.setParmValue(parm);
		this.getTable("TABLE").clearSelection(); // ���TABLEѡ��״̬
//		setStarLabel(false);
		// ��ʼ������ʱ��
		// ��������
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("BEGIN_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 00:00:00");
//		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("SMS_STATE", "1");
		//add by wukai on 20170413
		this.setValue("REPORT_USER", Operator.getID());
		this.setValue("REPORT_DEPT_CODE", Operator.getDept());
//		onInit();
	}

	/**
	 * ���Ŵ���
	 */
	public void onSave() {
			if (this.getValueString("ADM_TYPE") == null
					|| this.getValueString("ADM_TYPE").length() <= 0) {
				this.messageBox("�ż�ס�����Ϊ�գ���ѡ��");
				this.grabFocus("ADM_TYPE");
				return;
			}
			if (this.getValueString("MR_NO") == null
					|| this.getValueString("MR_NO").length() <= 0) {
				this.messageBox("�����Ų���Ϊ�գ���ѡ��");
				this.grabFocus("MR_NO");
				return;
			}
			if (this.getValueString("PATIENT_NAME") == null
					|| this.getValueString("PATIENT_NAME").length() <= 0) {
				this.onQueryNO();
			}
			if (this.getValueString("DEPT_CODE") == null
					|| this.getValueString("DEPT_CODE").length() <= 0) {
				this.messageBox("���Ҳ���Ϊ�գ���ѡ��");
				this.grabFocus("DEPT_CODE");
				return;
			}
			if(this.getValueString("ADM_TYPE").equals("I")){
				if (this.getValueString("STATION_CODE") == null
						|| this.getValueString("STATION_CODE").length() <= 0) {
					this.messageBox("��������Ϊ�գ���ѡ��");
					this.grabFocus("STATION_CODE");
					return;
				}
			}else{
				if (this.getValueString("CLINIC_CODE") == null
						|| this.getValueString("CLINIC_CODE").length() <= 0) {
					this.messageBox("��������Ϊ�գ���ѡ��");
					this.grabFocus("CLINIC_CODE");
					return;
				}
			}
			
			if (this.getValueString("BILLING_DOCTORS") == null
					|| this.getValueString("BILLING_DOCTORS").length() <= 0) {
				this.messageBox("����ҽ������Ϊ�գ���ѡ��");
				this.grabFocus("BILLING_DOCTORS");
				return;
			}
			
			//add by chenjianxing 20180206
			//̩��#5494 ������Σ��ֵȱ���ϱ��˺��ϱ����ң����Ҫ���зǿ���֤���������ֵ
			//the start
			if (this.getValueString("REPORT_USER") == null
					|| this.getValueString("REPORT_USER").length() <= 0) {
				this.messageBox("�ϱ��˲���Ϊ�գ���ѡ��");
				this.grabFocus("REPORT_USER");
				return;
			}
			
			if (this.getValueString("REPORT_DEPT_CODE") == null
					|| this.getValueString("REPORT_DEPT_CODE").length() <= 0) {
				this.messageBox("�ϱ����Ҳ���Ϊ�գ���ѡ��");
				this.grabFocus("REPORT_DEPT_CODE");
				return;
			}
			//the end
			
			TParm parm = new TParm();
			
			parm.setData("MR_NO", this.getValueString("MR_NO"));
			parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
			// modified by WangQing 20170502
			if(this.getValueString("ADM_TYPE").equals("I"))
				parm.setData("STATION_CODE", this.getValueString("STATION_CODE"));
			else
				parm.setData("STATION_CODE", this.getValueString("CLINIC_CODE"));
			parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
			parm.setData("PAT_NAME", this.getValueString("PATIENT_NAME"));
			parm.setData("BILLING_DOCTORS", this.getValueString("BILLING_DOCTORS"));
//			parm.setData("DIRECTOR_DR_CODE", "");
			parm.setData("TESTITEM_CHN_DESC", this.getValueString("TESTITEM_CHN_DESC"));
			parm.setData("TEST_VALUE", this.getValueString("TEST_VALUE"));
			parm.setData("CRTCLLWLMT", this.getValueString("CRTCLLWLMT"));
			parm.setData("BED_NO", this.getValueString("BED_NO"));
			parm.setData("CASE_NO", this.getValueString("CASE_NO"));
			parm.setData("IPD_NO", this.getValueString("IPD_NO"));
			parm.setData("HANDLE_OPINION", this.getValueString("HANDLE_OPINION"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("REGION_CODE", Operator.getRegion());
			//add by wukai on 20170413�����ϱ����Һ��ϱ��� start
			parm.setData("REPORT_USER", this.getValueString("REPORT_USER"));
			parm.setData("REPORT_DEPT_CODE", this.getValueString("REPORT_DEPT_CODE"));
			//add by wukai on 20170413�����ϱ����Һ��ϱ��� end
			TParm result = TIOM_AppServer.executeAction(
					"action.med.MedSmsAction", "onSaveBySelf", parm);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox("Σ��ֵ�Ǽǳɹ���");
			onClear();
			onQuery();
		}

	/**
	 * ��ʱ��֮�����
	 * 
	 * @param medSms
	 * @return
	 */
	private long getDiffTime(String sendTime) {
		// String systemTime = DateUtil.getNowTime(TIME_FORMAT);
		String systemTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), TIME_FORMAT);
		Date begin = null;
		Date end = null;
		try {
			end = sdf.parse(systemTime);
			begin = sdf.parse(sendTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/** �� **/
		long between = (end.getTime() - begin.getTime()) / 1000;
		/** ���� **/
		long minute = between / 60;
		return minute;
	}
	
	/**
	 * �ż�ס��������ֵ�ı��¼�
	 */
	public void changeAdmType(){
		if(!this.getValueString("ADM_TYPE").equals("I")){
			((TLabel) this.getComponent("AREA_LABEL")).setText("��  ����");
			((TTextFormat) this.getComponent("CLINIC_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("CLINIC_CODE")).setEnabled(true);
			((TTextFormat) this.getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("STATION_CODE")).setEnabled(false);
		}else{
			((TLabel) this.getComponent("AREA_LABEL")).setText("��  ����");
			((TTextFormat) this.getComponent("CLINIC_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("CLINIC_CODE")).setEnabled(false);
			((TTextFormat) this.getComponent("STATION_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("STATION_CODE")).setEnabled(true);
		}
	}
	
	/**
	 * ����Excel
	 */
	public void onExcel(){
		if(table.getRowCount() <= 0){
			this.messageBox("û�����ݣ�");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "Σ��ֵͳ�Ʊ�");
	}

	// -------------------add by wangqing start----------------------






}
