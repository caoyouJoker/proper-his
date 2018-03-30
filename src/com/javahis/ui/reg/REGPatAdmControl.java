package com.javahis.ui.reg;

import java.awt.event.FocusEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.bil.BIL;
import jdo.bil.BILContractRecordTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewTool;
//kangy �ѿ���ԭ     import jdo.ekt.EKTReadCard;
import jdo.ekt.EKTTool;
import jdo.ins.INSMZConfirmTool; //import jdo.ins.INSTJFlow;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg; //import jdo.ins.INSRunTool;
import jdo.opd.OrderTool;
import jdo.reg.PanelRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGCcbReTool;
import jdo.reg.REGClinicQueTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.Reg;
import jdo.reg.RegMethodTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.reg.ws.RegQETool;
import jdo.sid.IdCardO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.EktDriver;
import com.javahis.device.NJCityInwDriver;
import com.javahis.device.NJSMCardDriver;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktTradeContext;
import com.javahis.util.StringUtil;

/**
 * 
 * 
 * <p>
 * Title:�Һ�����������
 * </p>
 * 
 * <p>
 * Description:�Һ�����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author wangl 2008.09.22
 * @version 1.0
 */
public class REGPatAdmControl extends TControl {
	// ��������
	private Pat pat;
	// �ҺŶ���
	private Reg reg;
	// �ż���
	public String admType = "O";
	// ԤԼʱ��
	String startTime;
	// ҽ�ƿ�����
	String ektCard;
	int selectRow = -1;
	public String tredeNo;
	public String businessNo; // �Һų������⳷������
	public String tradeNoT;
	public String endInvNo;
	public TParm p3; // ҽ��������
	private boolean feeShow = false; // =====pangben 20110815 ҽ�����Ļ�÷��ùܿ�
	private boolean txEKT = false; // ̩��ҽ�ƿ�����ִ��ֱ��д������=====pangben 20110916
	public  String ektOldSum; // ҽ�ƿ�����ʧ�ܻ�д���
	public  String ektNewSum; // �ۿ��Ժ�Ľ��
	// ������Ϣ���

	public TParm insParm; // ҽ�����Σ�U ���� A ��������

	public boolean tjINS = false; // ���ҽ���ܿأ��ж��Ƿ�ִ����ҽ�ƿ�����

	public boolean insFlg = false; // ҽ���������ɹ��ܿ�
	// private String caseNo; // ҽ������ˢ��ʱ��Ҫ�����
	private TParm regionParm; // ���ҽ���������
	// zhangp 20111227
	private TParm parmSum; // ִ�г�ֵ��������
	private boolean printBil = false; // ��ӡƱ��ʱʹ��
	private TParm reSetEktParm; // ҽ�ƿ��˷�ʹ���ж��Ƿ�ִ��ҽ�ƿ��˷Ѳ���
	private String confirmNo; // ҽ��������ţ��˹�ʱʱʹ��
	private String reSetCaseNo; // �˹�ʹ�þ������
	private String insType; // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ����� �˹�ʹ��
	private boolean tableFlg = false; // ��һ��ҳǩ��ȫ������� ��ý���ܿ�
	public double ins_amt = 0.00; // ҽ�����
	public boolean ins_exe = false; // �ж��Ƿ�ҽ��ִ�� ������ִ�в���������ʱʵ����;״̬
	public TParm greenParm = null;// //��ɫͨ��ʹ�ý��
	public double accountamtforreg = 0.00;// �����˻�
	BilInvoice ektinvoice;
	BilInvoice invoice;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	//kangy �ѿ���ԭ      private boolean dev_flg=true;
	
	String authCode = "00";//  ==zhanglei VVIP������֤�뷵��һ����ȷ��
	
	/**
	 * ��ʼ������
	 */
	public void onInitParameter() {
		String parmAdmType = (String) this.getParameter();
		if (parmAdmType != null && parmAdmType.length() > 0)
			admType = parmAdmType;
		setValue("ADM_TYPE", admType);
		callFunction("UI|SESSION_CODE|setAdmType", admType);
		callFunction("UI|CLINICTYPE_CODE|setAdmType", admType);
		callFunction("UI|VIP_SESSION_CODE|setAdmType", admType);
		callFunction("UI|setTitle", "O".equals(admType) ? "����ҺŴ���" : "����ҺŴ���");
		callFunction("UI|ERD_LEVEL_TITLE|setVisible", false);
		callFunction("UI|ERD_LEVEL|setVisible", false);
		//��Ժʱ��add by huangjw 20150603 && ���ﲻ��ʾ
		callFunction("UI|ARRIVE_DATE_TIME|setVisible", false);
		callFunction("UI|ARRIVE_DATE|setVisible", false);
		callFunction("UI|TRIAGE_NO_TITLE|setVisible", false);
		callFunction("UI|TRIAGE_NO|setVisible", false);
		
		if (admType.equals("E")) {
			callFunction("UI|ERD_LEVEL_TITLE|setVisible", true);
			callFunction("UI|ERD_LEVEL|setVisible", true);
			callFunction("UI|TRIAGE_NO_TITLE|setVisible", true);
			callFunction("UI|TRIAGE_NO|setVisible", true);
			//������ʾ��Ժʱ�� add by huangjw 20150603
			callFunction("UI|ARRIVE_DATE_TIME|setVisible", true);
			callFunction("UI|ARRIVE_DATE|setVisible", true);
			TParm selTriageFlg = REGSysParmTool.getInstance().selectdata();
			String triageFlg = selTriageFlg.getValue("TRIAGE_FLG", 0);
			if ("N".equals(triageFlg))
				callFunction("UI|ERD_LEVEL|setEnabled", false);
			setValue("ADM_DATE", SystemTool.getInstance().getDate());
			String sessionCode = initSessionCode();
			Timestamp admDate = TJDODBTool.getInstance().getDBTime();
			// ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
			if (!StringUtil.isNullString(sessionCode)
					&& !StringUtil.isNullString(admType)) {
				admDate = SessionTool.getInstance().getDateForSession(admType,
						sessionCode, Operator.getRegion());
				this.setValue("ADM_DATE", admDate);
			}
        } else {
            callFunction("UI|Wrist|setVisible", false);//wanglong add 20150413
        }
		// ��ʼ������Combo
		callFunction("UI|DEPT_CODE|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		// ��ʼ������(��ͨ��sort)Combo
		callFunction("UI|DEPT_CODE_SORT|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		
		// ��ʼ��ʱ��Combo,ȡ��Ĭ��ʱ��
		initSession();
		setValue("REGION_CODE", Operator.getRegion());
		// ========pangben modify 20110421 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

		// ��ʼ��Ĭ��(�ֳ�)�Һŷ�ʽ
		setValue("REGMETHOD_CODE", "A");
		// ��ʼ��ID�������
		onClickRadioButton();
		initSchDay();
		// ��ʼ��ԤԼ��Ϣ��ʼʱ��
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// ��ʼ��VIP���Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// ���˹�,������ťΪ��
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|NHI_NO|setEnabled", false); // ҽ�������ɱ༭
		// ��ʼ��������
		TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
		if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
			setValue("VISIT_CODE_F", "Y");
			callFunction("UI|MR_NO|setEnabled", true);
		}
		// ��ʼ����һƱ��
		 invoice = new BilInvoice();
		invoice = invoice.initBilInvoice("REG");
		//==start==add by kangy ===20160810
		ektinvoice=invoice.initBilInvoice("EKT");
		initBilInvoice(ektinvoice.initBilInvoice("EKT"));
		callFunction("UI|BIL_CODE|setValue", ektinvoice.getUpdateNo());
		callFunction("UI|BIL_CODE|Enabled", false);
		//==end==add by kangy ===20160810
		endInvNo = invoice.getEndInvno();
		// ===zhangp 20120306 modify start
		if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
				Operator.getRegion(), invoice.getUpdateNo())) {
			setValue("NEXT_NO", invoice.getUpdateNo());
		} else {
			messageBox("Ʊ��������");
		}
		// ===zhangp 20120306 modify end
		// ����Ĭ�Ϸ���ȼ�
		setValue("SERVICE_LEVEL", "1");
		// this.onClear();
		// ======zhangp 20120224 modify start
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
		// ======zhangp 20120224 modify end
		if(admType.equals("E")){//��ʼ����Ժʱ�� add by huangjw 20150603
			this.setValue("ARRIVE_DATE", SystemTool.getInstance().getDate().toString().substring(0,16).replaceAll("-", "/"));
		}
	}
	
	/**
	 * ��֤�������
	 */
	public void ChangeCtz(){
		//TTextFormat aa = (TTextFormat) this.getComponent("REG_CTZ1");
		//aa.addEventListener(TComboBoxEvent.SELECTED,this,"ChangeCtz");
		String ctzNo = this.getValueString("REG_CTZ1");
		String sql = "SELECT SPECIAL_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"+ctzNo+"'";
		TParm ctzparm = new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(ctzparm.getValue("SPECIAL_FLG",0))){
			this.openDialog("%ROOT%\\config\\reg\\REGSpecialFlg.x");
		}
	}
	/**
	 * ��ʼ�����
	 */
	public void initSchDay() {
		new Thread() {
			// �߳�,Ϊ��ʡʱ����ߴ򿪹Һ�������Ч��
			public void run() {
				// ��ʼ��Ĭ��֧����ʽ
				//===zhangp ���޸�֧����ʽ 20130517
				TParm selPayWay = REGSysParmTool.getInstance().selPayWay();
				setValue("PAY_WAY", selPayWay.getValue("DEFAULT_PAY_WAY", 0));
				// ��ʼ������ҽʦ�Ű�
				onQueryDrTable();

				// ��ʼ������VIP���
				onQueryVipDrTable();
			}
		}.start();
	}

	/**
	 * ���Ӷ�Table1�ļ���
	 */
	public void onTable1Clicked() {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		int row = (Integer) callFunction("UI|Table1|getClickedRow");
		if (row < 0)
			return;
		//=====20130507 yanjing ��Ӳ�ѯ�հ�����ݿ��ж϶�Ӧ��Ϣ�Ƿ����
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE",admType);
//		 TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("Table1");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
	    String admDate = parm.getValue("ADM_DATE").substring(0, 4)+parm.getValue("ADM_DATE").substring(5,7)
	                +parm.getValue("ADM_DATE").substring(8,10);
	    parm.setData("ADM_DATE", admDate);
		parm.setData("CLINICROOM_NO",tableParm.getValue("CLINICROOM_NO",row ));
		TParm result = SchDayTool.getInstance().selectOneDrTable(parm);
		if (result.getCount()<=0) {
			callFunction("UI|SAVE_REG|setEnabled", false);//�շѰ�ť���ɱ༭=====yanjing
			this.messageBox("���ڡ�ҽʦ��������Ϣ��һ�£���ˢ�½��棡");
			return;
		}
		//=======20130507 yanjing end
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// =====modify by caowl 20120809 ɾ���˴���������ش���

		// ��ùҺŷ�ʽִ���ж��Ƿ��Ʊ����
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // ����Ƿ���Դ�Ʊע��
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("�Һ�ʧ��");
			return;
		}
		// ����Ʊ����
		if (null != tableParm.getValue("TYPE", row)
				&& tableParm.getValue("TYPE", row).equals("VIP")
				&& (null == regMethodParm.getValue("PRINT_FLG", 0) || regMethodParm
						.getValue("PRINT_FLG", 0).length() <= 0)) {

			onClickClinicType(false);
		} else {
			onClickClinicType(true);
		}
		setControlEnabled(false);
		// ���˹Ұ�ť���ɱ༭
		callFunction("UI|unreg|setEnabled", false);
		// �ò�ӡ��ť���ɱ༭
		callFunction("UI|print|setEnabled", false);
		tableFlg = true; // ��һ��ҳǩ�ܿ�
		this.grabFocus("FeeS");
	}

	/**
	 * ���Ӷ�Talbe2�ļ����¼�
	 */
	public void onTable2Clicked() {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		startTime = new String();
		int row = (Integer) callFunction("UI|Table2|getClickedRow");
		if (row < 0)
			return;
		// �õ�table�ؼ�
		TTable table2 = (TTable) callFunction("UI|table2|getThis");
		if (table2.getValueAt(row, table2.getColumnIndex("QUE_STATUS")).equals(
				"Y")) {
			this.messageBox("��ռ��!");
			callFunction("UI|table2|clearSelection");
			return;
		}
		// =====�ѹ���pangben 2012-3-26 start
		String startNowTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), "HHmm");// ϵͳ��ǰʱ��
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// ϵͳ��ǰ����
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// ��ǰ�Һ�����
		TParm data = table2.getParmValue();
		if (admDate.compareTo(admNowDate) < 0) {
			this.messageBox("�Ѿ����ﲻ���ԹҺ�");
			callFunction("UI|table2|clearSelection");
			return;
		} else if (admDate.compareTo(admNowDate) == 0) {
			startTime = data.getValue("START_TIME", row);
			if (startTime.compareTo(startNowTime) < 0) {
				this.messageBox("�Ѿ����ﲻ���ԹҺ�");
				callFunction("UI|table2|clearSelection");
				return;
			}
		}
		// =====�ѹ���pangben 2012-3-26 stop
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				data, row);

		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", data.getValue("DR_CODE", row));
		onClickClinicType(true);
		this.grabFocus("FeeS");
	}

	/**
	 * ���Ӷ�Talbe3�ļ����¼�
	 */
	public void onTable3Clicked() {
		int row = (Integer) callFunction("UI|Table3|getClickedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		TParm parm = table3.getParmValue();
		// System.out.println("�˹���Ϣ" + parm);
		// parm.getValue("ARRIVE_FLG",row);������ȡ��
		String arriveFlg = (String) table3.getValueAt(row, 7);
		// �ж��Ƿ�ԤԼ�Һ�
		if ("N".equals(arriveFlg)) {
			setValueForParm(
					"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE;REGMETHOD_CODE",
					parm, row);

			// setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
			setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));			
			setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));

			onClickClinicType(true);
			// onDateReg();
			callFunction("UI|CLINICROOM_NO|onQuery");
			// �ñ�����ť�ɱ༭
			callFunction("UI|arrive|setEnabled", true);
			// setValue("FeeY", parm.getValue("ARRIVE_FLG", row));
			// setValue("FeeS", parm.getValue("ARRIVE_FLG", row));
			this.messageBox(getValue("PAT_NAME") + "��ԤԼ��Ϣ");
			// ���շѰ�ť���ɱ༭
			// ===zhangp 20120306 modify start
			callFunction("UI|SAVE_REG|setEnabled", false);
			// ===zhangp 20120306 modify end
			// ���˹Ұ�ťΪ��
			callFunction("UI|unreg|setEnabled", true);

		}

		else {
			// System.out.println("�ѹ���Ϣ:::"+parm);
			// this.messageBox_("�ѹ���Ϣ"+parm);
			setValueForParm(
					"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE",
					parm, row);

			setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
			setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));
			setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));
			if("E".equals(admType)){
				setValue("TRIAGE_NO", parm.getValue("TRIAGE_NO", row));
				setValue("ERD_LEVEL", parm.getValue("ERD_LEVEL", row));

			}
			callFunction("UI|DEPT_CODE|onQuery");
			callFunction("UI|DR_CODE|onQuery");
			callFunction("UI|CLINICROOM_NO|onQuery");
			callFunction("UI|CLINICTYPE_CODE|onQuery");
			// onClickClinicType( -1);
			// ==================pangben modify 20110815 �޸Ļ��Ʊ�ݱ��еļ۸���ʾ������
			unregFeeShow(parm.getValue("CASE_NO", row));
			setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
					parm, row);
			// �ñ�����ť���ɱ༭
			callFunction("UI|arrive|setEnabled", false);
			// ���շѰ�ť���ɱ༭
			callFunction("UI|SAVE_REG|setEnabled", false);
			// ���˹Ұ�ť�ɱ༭
			callFunction("UI|unreg|setEnabled", true);
			// �ò�ӡ��ť�ɱ༭
			callFunction("UI|print|setEnabled", true);
		}
		// onDateReg();
		//
		// onSaveRegParm();

	}

	/**
	 * �����ע���¼�
	 */
	public void onSelForeieignerFlg() {
		if (this.getValue("FOREIGNER_FLG").equals("Y"))
			this.grabFocus("BIRTH_DATE");
		if (this.getValue("FOREIGNER_FLG").equals("N"))
			this.grabFocus("IDNO");
	}

	/**
	 * ������״̬
	 */
	public void onClickRadioButton() {
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
//			callFunction("UI|MR_NO|setEnabled", false);
//			this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			callFunction("UI|MR_NO|setEnabled", true);
			this.grabFocus("MR_NO");
		}
		this.onClear();
	}

	/**
	 * ���没����Ϣ
	 */
	public void onSavePat() {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		// ���������ֵ
		if (getValue("BIRTH_DATE") == null) {
			this.messageBox("�������ڲ���Ϊ��!");
			return;
		}
		if (!this.emptyTextCheck("PAT_NAME,SEX_CODE,CTZ1_CODE"))
			return;
		pat = new Pat();
		// ��������
		pat.setName(TypeTool.getString(getValue("PAT_NAME")));
		// Ӣ����
		pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
		// ����ƴ��
		pat.setPy1(TypeTool.getString(getValue("PY1")));
		// ���֤��
		pat.setIdNo(TypeTool.getString(getValue("IDNO")));
		// �����ע��
		pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
		// ��������
		pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
		// �Ա�
		pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
		// �绰
		pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
		// �ʱ�
		pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
		// ��ַ
		pat.setAddress(TypeTool.getString(getValue("ADDRESS")));
		// ���1
		pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
		// ���2
		pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
		// ���3
		pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
		// ҽ��������
		pat.setNhiNo(TypeTool.getString(getValue("NHI_NO"))); // =============pangben
		// modify
		// 20110808
		if (this.messageBox("������Ϣ", "�Ƿ񱣴�", 0) != 0)
			return;
		TParm patParm = new TParm();
		patParm.setData("MR_NO", getValue("MR_NO"));
		patParm.setData("PAT_NAME", getValue("PAT_NAME"));
		patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
		patParm.setData("PY1", getValue("PY1"));
		patParm.setData("IDNO", getValue("IDNO"));
		patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
		patParm.setData("TEL_HOME", getValue("TEL_HOME"));
		patParm.setData("SEX_CODE", getValue("SEX_CODE"));
		patParm.setData("POST_CODE", getValue("POST_CODE"));
		patParm.setData("ADDRESS", getValue("ADDRESS"));
		patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
		patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
		patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		patParm.setData("NHI_NO", getValue("NHI_NO")); // =============pangben
		// modify 20110808
		if (StringUtil.isNullString(getValue("MR_NO").toString())) {
			patParm.setData("MR_NO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
			patParm.setData("PAT_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME1").toString())) {
			patParm.setData("PAT_NAME1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PY1").toString())) {
			patParm.setData("PY1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("IDNO").toString())) {
			patParm.setData("IDNO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
			patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
		}
		if (StringUtil.isNullString(getValue("TEL_HOME").toString())) {
			patParm.setData("TEL_HOME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SEX_CODE").toString())) {
			patParm.setData("SEX_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("POST_CODE").toString())) {
			patParm.setData("POST_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("ADDRESS").toString())) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
			patParm.setData("CTZ1_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
			patParm.setData("CTZ2_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
			patParm.setData("CTZ3_CODE", new TNull(String.class));
		}
		// =============pangben modify 20110808
		if (StringUtil.isNullString(getValue("NHI_NO").toString())) {
			patParm.setData("NHI_NO", new TNull(String.class));
		}
		TParm result = new TParm();
		// ===zhangp 20120613 start
		// if ("Y".equals(getValue("VISIT_CODE_F"))) {
		if (!"".equals(getValueString("MR_NO"))) {
			// ===zhangp 20120613 end
			if (getValue("MR_NO").toString().length() == 0) {
				this.messageBox("���ȼ���������");
				return;
			}
			// ���²���
			result = PatTool.getInstance().upDateForReg(patParm);
			setValue("MR_NO", getValue("MR_NO"));
			pat.setMrNo(getValue("MR_NO").toString());
		} else {
			// ��������
			// pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
			
			pat.onNew();
			setValue("MR_NO", pat.getMrNo());
			
			
		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// �ж��Ƿ����
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("�Ƿ����", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " ǿ�ƽ���[" + aa
//										+ " �����ţ�" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// 20120112 zhangp ����֮�󽨿�
		// ===zhangp 20120309 modify start
		if (getValueBoolean("VISIT_CODE_C")) {
			ektCard();
		}
		// ===������ start
		if (Operator.getSpcFlg().equals("Y")) {
//			SYSPatinfoClientTool sysPatinfoClientTool = new SYSPatinfoClientTool(
//					this.getValue("MR_NO").toString());
//			SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
//			SpcPatInfoService_SpcPatInfoServiceImplPort_Client serviceSpcPatInfoServiceImplPortClient = new SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
//			String msg = serviceSpcPatInfoServiceImplPortClient
//					.onSaveSpcPatInfo(syspat);
//			if (!msg.equals("OK")) {
//				System.out.println(msg);
//			}
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction(
	                "action.sys.SYSSPCPatAction",
	                "getPatName", spcParm);
		}
		// ===������ end
		this.onClear();
	}
	/**
	 * ��ѯ�Ƿ��ں�����
	 * caowl
	 * */
	public void onBlackFlg(String mr_no){		
		String sql = "SELECT BLACK_FLG FROM SYS_PATINFO WHERE MR_NO = '"+mr_no+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getData("BLACK_FLG",0).toString().equals("Y")){
			this.messageBox("�����ں������У�");			
		}
		
	}
	
	public void onMrNo(){
		onQueryNO(true);
		//  ==add by zhanglei 20171116  ���ӹҺ�ʱ���Һ������������ݵ�����֤��
//		if(getREGSpecialFlg().equals("Y")){
//			checkSpecialFlg();
//		}
	}
 
	/**
	 * ��ѯ������Ϣ
	 */
	public void onQueryNO(boolean flg) {
		onClearRefresh();
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		insFlg = false; // ��ʼ��
		insType = null;// ��ʼ��
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!111");
			return;
		}
		
		//true �����֤���������в���  add by huangtt 20170602
		if(flg){
			boolean checkFlg = PatTool.getInstance().selCheckIdNo(pat.getIdNo(), pat.getName());
			if(checkFlg){
				this.messageBox("�ò������ڶ�����Ϣ");
				TParm checkParm = new TParm();
				checkParm.setData("PAT_NAME", pat.getName());
				checkParm.setData("IDNO", pat.getIdNo());
				
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSAutoCheckDuplicate.x", checkParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
//					System.out.println("reg---"+patParm);
					if(patParm.getValue("MR_NO").length() > 0){
						pat = Pat.onQueryByMrNo(patParm.getValue("MR_NO"));
					}
					
				}

			}
		}

        String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        setVisitCodeFC(srcMrNo); //add by huangtt 20151020 �Զ��жϳ�����
        
        if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
        }
        setValue("MR_NO", pat.getMrNo());
        // caowl 20131105 start
        onBlackFlg(pat.getMrNo());
		//caowl 20131105 end
		setValue("PAT_NAME", pat.getName().trim());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		onPast();
		setValue("SEX_CODE", pat.getSexCode());
		setValue("TEL_HOME", pat.getTelHome());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("ADDRESS", pat.getAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// �ж��Ƿ����
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("�Ƿ����", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " ǿ�ƽ���[" + aa
//										+ " �����ţ�" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// ��������Ϣ
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
			// this.messageBox_("�����ɹ�!");//����ר��
			selPatInfoTable();
		// =======20120216 zhangp modify start
		String sql = "select CARD_NO from EKT_ISSUELOG where mr_no = '"
				+ pat.getMrNo() + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
		}
		if (result.getCount() < 0) { // ���δ������ݣ�δ�ƿ������ƿ�
			if (messageBox("��ʾ", "�ò���δ����ҽ�ƿ�,�Ƿ����ҽ�ƿ�", 0) == 0) {
				ektCard(); // �ƿ�
				// ====zhangp 20120227 modify start
				this.onClear();
			}
		}
		// =======20120216 zhangp modify end
		if("E".equals(admType)){
			this.grabFocus("TRIAGE_NO");
		}else{
			this.grabFocus("CLINICROOM_NO");
		}
		
		// ===zhangp 20120413 start
		// ��ʼ����һƱ��
		BilInvoice invoice = new BilInvoice();
		invoice = invoice.initBilInvoice("REG");
		endInvNo = invoice.getEndInvno();
		if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
				Operator.getRegion(), invoice.getUpdateNo())) {
			setValue("NEXT_NO", invoice.getUpdateNo());
		} else {
			messageBox("Ʊ��������");
		}
		// ===zhangp 20120413 end
		//yanjing ������ʱˢ���հ��
//		if (admType.equals("E")) {
//			initSchDay();
//		}
		
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param mrNo
	 *            String
	 */
	public void onQueryMrNO(String mrNo) {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());

		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		setVisitCodeFC(mrNo); //add by huangtt 20151020 �Զ��жϳ�����
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", pat.getName());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("TEL_HOME", pat.getTelHome());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("ADDRESS", pat.getAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// �ж��Ƿ����
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("�Ƿ����", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " ǿ�ƽ���[" + aa
//										+ " �����ţ�" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// ��������Ϣ
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
			// this.messageBox_("�����ɹ�!");//����ר��
			selPatInfoTable();
		if("E".equals(admType)){
			this.grabFocus("TRIAGE_NO");
		}else{
			this.grabFocus("CLINICROOM_NO");
		}
	}

	/**
	 * Ӧ�ս���ý���
	 * 
	 * @param e
	 *            FocusEvent
	 */
	public void onFocusLostAction(FocusEvent e) {
		onFee();

	}
	/**
	 * 
	* @Title: onCheckQueNo
	* @Description: TODO(У������)
	* @author pangben
	* @return
	* @throws
	 */
	private boolean onCheckQueNo(){
	//  pangben 20150302 ���У������ظ�У��
		String regsql="SELECT CASE_NO FROM REG_PATADM WHERE REGION_CODE='"+reg.getRegion()+
		"' AND ADM_TYPE='"+reg.getAdmType()+"' AND ADM_DATE=TO_DATE('"+ StringTool.getString(
				reg.getAdmDate(), "yyyy/MM/dd")+"','YYYY/MM/DD') AND SESSION_CODE='"+reg.getSessionCode()+
				"' AND CLINICROOM_NO='"+reg.getClinicroomNo()+"' AND QUE_NO='"+reg.getQueNo()+"'" +
				" AND REGCAN_USER IS NULL";  //add by huangtt 20150707 ����Ҫ�ų��˹ҵ�
		TParm regQueNoParm = new TParm(TJDODBTool.getInstance().select(regsql));
		if (regQueNoParm.getErrCode()<0) {
			this.messageBox("��ѯ��ų��ִ���");
			return false;
		}
		if (regQueNoParm.getCount()>0) {
			this.messageBox("������Ѿ�ʹ��,�����²���");
			return false;
		}
		return true;
	}
	/**
	 * ����REG����
	 */
	public void onSaveReg() {
		
		if(p3 != null && p3.getValue("PK_CARD_NO").length() > 0){
			TParm eParm = RegQETool.getInstance().getEktMaster(p3.getValue("PK_CARD_NO"));
			if(eParm.getCount() > 0){
				if(this.getValueDouble("EKT_CURRENT_BALANCE")-eParm.getDouble("CURRENT_BALANCE",0) != 0){
					this.messageBox("ҽ�ƿ������仯 �������¶����Һ�!!!!!");
					return;
				}
			}
			
		}
		
		// add by wangqing 201470627 start
		// ����Һű�������˺�
		if (admType.equals("E")) {
			if(this.getValueString("TRIAGE_NO").trim().length()==0){
				this.messageBox("��������˺ţ�����");
				return;
			}
			this.onErd();	
		}
		// add by wangqing 20170627 end
		
				
//		=====yanj 20130502 ���ʱ��У��
		if (admType.equals("E")) {
			String admNowTime1 = StringTool.getString(SystemTool.getInstance()
					.getDate(), "HH:mm:ss");// ϵͳ��ǰʱ��
			String sessionCode = (String)this.getValue("SESSION_CODE");
			String startTime = SessionTool.getInstance().getStartTime(admType, sessionCode);
			String endTime = SessionTool.getInstance().getEndTime(admType, sessionCode);
			if (startTime.compareTo(endTime)<0) {
				if (!(admNowTime1.compareTo(startTime)>0&&(admNowTime1.compareTo(endTime)<0))) {
					this.messageBox("��ˢ�½��棡");
					return;
				}
			}
				else {
					if (admNowTime1.compareTo(startTime)<0&&admNowTime1.compareTo(endTime)>0) {
						this.messageBox("��ˢ�½��棡");
						return;
					}
				}	
			}
		// ====pangben 20131030 �޸�У������ĹҺ����
		if (null==this.getValue("REG_CTZ1")||this.getValue("REG_CTZ1").toString().length()<=0) {
			messageBox("��ѡ�����");
			this.grabFocus("REG_CTZ1");
			return;
		}
		DecimalFormat df = new DecimalFormat("##########0.00");
		// �ֳ��Һ�
		if (this.getValue("REGMETHOD_CODE").equals("A")) {
			// ������У��
			if (TypeTool.getDouble(df.format(getValue("FeeS"))) < TypeTool
					.getDouble(df.format(getValue("FeeY")))) {
				this.messageBox("����");
				return;
			}
		}
		// ���������ֵ
		// if (!this.emptyTextCheck("DEPT_CODE,CLINICTYPE_CODE,PAY_WAY"))
		// return;
		if (this.getValue("DEPT_CODE") == null
				|| this.getValueString("DEPT_CODE").length() == 0) {
			this.messageBox("���Ҳ���Ϊ��");
			return;
		}
		if (this.getValue("CLINICTYPE_CODE") == null
				|| this.getValueString("CLINICTYPE_CODE").length() == 0) {
			this.messageBox("�ű���Ϊ��");
			return;
		}
		if (admType.endsWith("E")) {
			if (this.getValue("ARRIVE_DATE")==null||this.getValue("ARRIVE_DATE").toString().length()<0){//У�鵽Ժʱ�� add by haungjw 20150603
				this.messageBox("��Ժʱ�䲻��Ϊ��");
				this.grabFocus("ARRIVE_DATE");
				return;
			}
//			if (!this.emptyTextCheck("ERD_LEVEL"))
//				return;
		}
		reg = new Reg();
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		if (RegMethodTool.getInstance().selPrintFlg(
				this.getValueString("REGMETHOD_CODE"))) {
			if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null) {
				this.messageBox("��δ����");
				return;
			}
			if (reg.getRegReceipt().getBilInvoice().getUpdateNo().compareTo(
					reg.getRegReceipt().getBilInvoice().getEndInvno()) > 0) {
				this.messageBox("Ʊ��������!");
				return;
			}
		}
		// ����
		if (pat == null) {
			this.messageBox("�޲�����Ϣ");
			return;
		}
		// �ж��Ƿ�Ϊ����������
		if (pat.getBlackFlg())
			this.messageBox("��ע��,��Ϊ����������!");
		pat.setNhiNo(this.getValueString("NHI_NO"));
		// System.out.println("pat::" + pat.getNhiNo());
		reg.setPat(pat);
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("�����Ų���Ϊ��");
			return;
		}
		// �Һ�����,REG����
		// 2�ż���
		if (!onSaveRegParm(true))
			return;
		// ҽ��ҽ�Ʋ��� ���ò���
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����

		reg.setTredeNo(tredeNo);
		String regmethodCode = this.getValueString("REGMETHOD_CODE"); // �Һŷ�ʽ
		// ��ùҺŷ�ʽִ���ж��Ƿ��Ʊ����
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ regmethodCode + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // ����Ƿ���Դ�Ʊע��
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("�Һ�ʧ��");
			return;
		}
		if (null != regMethodParm.getValue("PRINT_FLG", 0)
				&& regMethodParm.getValue("PRINT_FLG", 0).equals("Y")) {
			// ��Ʊ
			reg.setApptCode("N");
			reg.setRegAdmTime("");
		} else if (null == regMethodParm.getValue("PRINT_FLG", 0)
				|| regMethodParm.getValue("PRINT_FLG", 0).length() <= 0
				|| regMethodParm.getValue("PRINT_FLG", 0).equals("N")) {
			// ����Ʊ����
			reg.setApptCode("Y");
			// 12ԤԼʱ��
			reg.setRegAdmTime(startTime);
		}
		// ��õ�һ��ҳǩ����
		if (tableFlg) {
			// �ж��Ƿ�VIP����
			TTable table1 = (TTable) this.getComponent("Table1");
			TParm parm = table1.getParmValue();
			TParm temp = parm.getRow(selectRow); // ��õ�һ��ҳǩ����
			String type = temp.getValue("TYPE"); // VIP ��һ��
			if (type.equals("VIP")) {
				// UPDATE REG_CLINICQUE &
				temp.setData("ADM_TYPE", admType); // �Һ�����
				temp.setData("SESSION_CODE", reg.getSessionCode()); // ʱ��
				temp.setData("ADM_DATE", StringTool.getString(
						(Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
				temp.setData("START_TIME", StringTool.getString(SystemTool
						.getInstance().getDate(), "HHmm"));//ϵͳ��ǰʱ��
				String admNowDate = StringTool.getString(SystemTool
						.getInstance().getDate(), "yyyyMMdd");// ϵͳ��ǰ����
				// String startTime = "";
				if (temp.getValue("ADM_DATE").compareTo(admNowDate) < 0) {
					this.messageBox("�Ѿ����ﲻ���ԹҺ�");
					callFunction("UI|table2|clearSelection");
					return;
				}
				// // ���vip�����
				queryQueNo(temp);
				//add by huangtt 20160621 �ж�VIP�޺�ʱ�����йҺű���
				if(reg.getQueNo() == 0){
					return;
				}
				// ===zhangp 20120629 end
				reg.setVipFlg(true); // vip����
				TParm regParm = reg.getParm();
				String admDate = StringTool.getString(reg.getAdmDate(),
						"yyyyMMdd");
				regParm.setData("ADM_DATE", admDate);
				// =========pangben 2012-7-1 start�غ�����
				if (!onSaveQueNo(regParm)) {
					messageBox("ȡ�þ����ʧ��");
					return;
				}
				// =========pangben 2012-7-1 stop
				if ("N".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(true); // ����
				} else if ("Y".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(false); // ������
				}
				startTime = temp.getValue("START_TIME", 0);
				reg.setRegAdmTime(startTime);
			} else if (getValueString("REGMETHOD_CODE").equals("D")) {
				messageBox("���VIP��");
				return;
			}
		}
		if(!onCheckQueNo())
			return;
		// =====zhangp 20120301 modify start
		if ("A".equals(getValue("REGMETHOD_CODE").toString())) {

			if (!onInsEkt(payWay, null)) {
				// ===========pangben 2012-7-1 ����ʧ�ܻع�VIP�������
//				TParm regParm = reg.getParm();
//				if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
//					this.messageBox("����VIP�������ʧ��,����ϵ��Ϣ����");
//				}
				return;
			}
		}
		if (!onSaveRegOne(payWay, ins_exe)) {
			// ===========pangben 2012-7-1 ����ʧ�ܻع�VIP�������
//			TParm regParm = reg.getParm();
//			if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
//				this.messageBox("����VIP�������ʧ��,����ϵ��Ϣ����");
//			}
			if(tjINS){
				TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
						"deleteOldData", insParm);
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					this.messageBox("����ҽ�������ݲ���ʧ��,����ϵ��Ϣ����");
					// return result;
				}
				
			}
			return;
		}
		if (ins_exe) { // ҽ��������ִ�гɹ� ,ɾ����;״̬���� �޸�Ʊ�ݺ�
			if (!updateINSPrintNo(reg.caseNo(), "REG"))
				return;
			if (!updateReceiptNo(reg.caseNo(),"REG"))
				return;
		}
		// ��Ʊ����
		TParm result = onPrintParm();
		if ("Y".endsWith(reg.getApptCode())) {
			this.messageBox("ԤԼ�ɹ�!");
			
			if (!this.getValue("REGMETHOD_CODE").equals("A")) {
				//huangtt start 20131101  ԤԼ�ɹ�֮���Ͷ���
				TParm parm = new TParm();
		        parm.addData("MrNo", this.getValueString("MR_NO"));
		        parm.addData("Name", this.getValueString("PAT_NAME"));
		        TComboBox sessionCode = (TComboBox) getComponent("SESSION_CODE");
		        TTextFormat drCode = (TTextFormat) getComponent("DR_CODE");
		        String sqlSC = "SELECT SESSION_DESC FROM REG_SESSION WHERE SESSION_CODE = '"+this.getValue("SESSION_CODE")+"'";
		        TParm parmSession = new TParm(TJDODBTool.getInstance().select(sqlSC));
		        
		        String content = "����ԤԼ�ɹ�"+
		        				this.getValue("ADM_DATE").toString().substring(0, 10).replace("-", "/")+" "+
		        				parmSession.getValue("SESSION_DESC", 0) +
		        				"��"+reg.getQueNo()+"��"+
		        				drCode.getText() +"ҽ�����������"+this.getValueString("PAT_NAME")+"���ˣ�����ȡ��������ǰһ�첦�����绰4001568568��Ϊ�˱�֤��׼ʱ���������ǰ����Һ�����";
//		       this.messageBox(content);
		        parm.addData("Content", content);
		        parm.addData("TEL1", this.getValueString("TEL_HOME"));
		        TParm  r =TIOM_AppServer.executeAction(
						"action.reg.REGAction", "orderMessage", parm);
		        
				//huangtt end 20131101
			}
			// �����Ŷӽк�
			/**
			 * if (!"true".equals(callNo("REG", ""))) { this.messageBox("�к�ʧ��");
			 * }
			 **/
			this.onClear();
			return;
		}
		// ================pangben modify 20110817 ���˵�λ������ִ�д�Ʊ
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			// �жϵ��ﲡ����Ʊ
			if ("N".endsWith(reg.getApptCode())) {
				// ҽ�ƿ���Ʊ
				onPrint(result);
				BilInvoice invoice = new BilInvoice();
				invoice = invoice.initBilInvoice("REG");
				// ��ʼ����һƱ��
				// ===zhangp 20120306 modify start
				if (BILTool.getInstance().compareUpdateNo("REG",
						Operator.getID(), Operator.getRegion(),
						invoice.getUpdateNo())) {
					setValue("NEXT_NO", invoice.getUpdateNo());
				} else {
					messageBox("Ʊ��������");
					clearValue("NEXT_NO");
				}
				// ===zhangp 20120306 modify end
				// �����Ŷӽк�
				if (!"true".equals(callNo("REG", reg.caseNo()))) {
					this.messageBox("�к�ʧ��");
				}

			}
			// ����Ʊִ�м��˲���
		} else {
			// =================pangben 20110817
			TParm parm = new TParm();
			parm.setData("RECEIPT_NO", reg.getRegReceipt().getReceiptNo()); // �վݺ�
			parm.setData("CONTRACT_CODE", this.getValue("CONTRACT_CODE")); // ���˵�λ
			parm.setData("ADM_TYPE", reg.getRegReceipt().getAdmType()); // �ż�ס��
			parm.setData("REGION_CODE", Operator.getRegion()); // Ժ��
			parm.setData("CASHIER_CODE", Operator.getID()); // �շ���Ա
			parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // �շ�����ʱ��
			parm.setData("RECEIPT_TYPE", "REG"); // Ʊ�����ͣ�REG ��OPB
			parm.setData("DATA_TYPE", "REG"); // �ۿ���Դ REG OPB HRM
			parm.setData("CASE_NO", reg.caseNo()); // �����
			parm.setData("MR_NO", reg.getPat().getMrNo());
			parm.setData("AR_AMT", reg.getRegReceipt().getArAmt()); // Ӧ�ɽ��
			parm.setData("BIL_STATUS", "1"); // ����״̬1 ���� 2 �������д�� =1
			// caowl 20130307 start
			String sqls = "SELECT * FROM BIL_CONTRACTD WHERE MR_NO = '"
					+ reg.getPat().getMrNo() + "' AND CONTRACT_CODE = '"
					+ this.getValue("CONTRACT_CODE") + "'";
//			System.out.println("��������" + sqls);
			TParm parms = new TParm(TJDODBTool.getInstance().select(sqls));
			if (parms.getCount() <= 0) {
				this.messageBox("�˲��˲����ڸú�ͬ��λ����ȷ�ϣ�");
				return;
			}
			// caowl 20130307 end
			// ���˵�λ�ɷ�ʱ��
			// update =2
			parm.setData("RECEIPT_FLG", "1"); // ״̬��1 �շ� 2 �˷�
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result1 = TIOM_AppServer.executeAction(
					"action.bil.BILContractRecordAction", "insertRecode", parm);
			if (result1.getErrCode() < 0) {
				err(result1.getErrCode() + " " + result1.getErrText());
				this.messageBox("�Һ�ʧ��");
			} else
				this.messageBox("�Һųɹ�,�Ѿ�����");
		}
//		this.messageBox("11");
		//  ==add by zhanglei 20171116  ���ӹҺ�ʱ���Һ������������ݵ�����֤��
//		if(getREGSpecialFlg().equals("Y")){
//			String sql1 = "UPDATE REG_AUTH_CODE SET "+
//					  " CASE_NO = '"+ reg.caseNo() +"'" +
//					  " WHERE AUTH_CODE = '"+ authCode +"'";
////			this.messageBox(sql1);
////			System.out.println("66666666666666" + sql1);
//			TParm a = new TParm(TJDODBTool.getInstance().update(sql1));
//			if(a.getErrCode()<0){
////				this.messageBox("VVIP����Ÿ���ʧ�ܸ���ʧ��");
//				System.out.println("VVIP����Ÿ���ʧ�ܸ���ʧ��");
//			}
//		}
		// ����������Ϣ
//		if (PatTool.getInstance().unLockPat(pat.getMrNo()))
			this.onClear();
		initSession();
		pat = null;
		
		
	}

	/**
	 * ��Ʊ����
	 * 
	 * @return TParm
	 */
	private TParm onPrintParm() {
		// �����Ʊ����
		TParm result = PatAdmTool.getInstance().getRegPringDate(reg.caseNo(),
				"");
		// zhangp 20120206
		result.setData("MR_NO", "TEXT", this.getValue("MR_NO"));
		result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		result.setData("PAY_WAY", this.getValue("PAY_WAY")); // ֧����ʽ
		result.setData("INS_SUMAMT", ins_amt);
		result.setData("ACCOUNT_AMT_FORREG", accountamtforreg);// �����˻�
		return result;
	}

	/**
	 * ҽ��ҽ�Ʋ��� ���ò���
	 * 
	 * @param payWay
	 *            String
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	private boolean onInsEkt(String payWay, String caseNo) {
		
		if (payWay.equals("PAY_MEDICAL_CARD") || payWay.equals("PAY_INS_CARD")){
			TParm ektSumExeParm= new TParm();
			ektSumExeParm.setData("payWay", payWay);
			ektSumExeParm.setData("caseNo", caseNo);
			
			EktParam ektParam = new EktParam();
			ektParam.setType("REG");
			ektParam.setRegPatAdmControl(this);
			ektParam.setReg(reg);
			ektParam.setPat(pat);
			ektParam.setOrderParm(ektSumExeParm);
			
			EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
			try {
				
				//�������������շѽ��棬ִ���շ�  
				ektTradeContext.openClient(reg);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
			

		if(payWay.equals("PAY_MEDICAL_CARD")){
			if (null != greenParm
					&& null != greenParm.getValue("GREEN_FLG")
					&& greenParm.getValue("GREEN_FLG").equals("Y")) {
				// ʹ����ɫͨ�����
				reg.getRegReceipt().setPayMedicalCard(
						TypeTool.getDouble(greenParm.getDouble("EKT_USE")));
				reg.getRegReceipt().setOtherFee1(
						greenParm.getDouble("GREEN_USE"));
			}
		}
		

//		// ҽ�ƿ�֧��
//		if (payWay.equals("PAY_MEDICAL_CARD")) {
//			// ����CASE_NO ��Ϊҽ�ƿ���ҪCASE_NO ��������ҽ�ƿ�֧����ʱ��������CASE_NO
//			if ("N".endsWith(reg.getApptCode())) {
//				// System.out.println("222222222222222222");
//				if (null != caseNo && caseNo.length() > 0) {
//					reg.setCaseNo(caseNo);
//				} else {
//					reg.setCaseNo(SystemTool.getInstance().getNo("ALL", "REG",
//							"CASE_NO", "CASE_NO"));
//				}
//				// ����ҽ�ƿ�
//				if (!this.onEktSave("Y")) {
//					System.out.println("!!!!!!!!!!!ҽ�ƿ��������");
//					return false;
//				}
//				if (null != greenParm
//						&& null != greenParm.getValue("GREEN_FLG")
//						&& greenParm.getValue("GREEN_FLG").equals("Y")) {
//					// ʹ����ɫͨ�����
//					reg.getRegReceipt().setPayMedicalCard(
//							TypeTool.getDouble(greenParm.getDouble("EKT_USE")));
//					reg.getRegReceipt().setOtherFee1(
//							greenParm.getDouble("GREEN_USE"));
//				}
//			}
//		}
//		if (payWay.equals("PAY_INS_CARD")) {
//			TParm result = null;
//			// ҽ����֧��
//			result = onSaveRegTwo(payWay, ins_exe, caseNo);
//			if (null == result) {
//				return false;
//			}
//			ins_exe = result.getBoolean("INS_EXE");
//			ins_amt = result.getDouble("INS_AMT");
//			accountamtforreg = result.getDouble("ACCOUNT_AMT_FORREG");
//		}

		if (ins_exe) {
			// ִ��ҽ�� �ж���;״̬
			TParm runParm = new TParm();
			runParm.setData("CASE_NO", reg.caseNo());
			runParm.setData("EXE_USER", Operator.getID());
			runParm.setData("EXE_TERM", Operator.getIP());
			runParm.setData("EXE_TYPE", "REG");
			runParm = INSRunTool.getInstance().queryInsRun(runParm);
			if (runParm.getErrCode() < 0) {
				return false;
			}
			if (runParm.getCount("CASE_NO") <= 0) {
				// û�в�ѯ�����ݣ�˵����;״̬������
				return false;
			} else {
				if (runParm.getInt("STUTS", 0) != 1) { // STUTS :1.��; 2.�ɹ�
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ִ�б���REG_PATADM BIL_REG_RECP BIL_INVRCP �����(ҽ��ִ�в���)
	 * 
	 * @param payWay
	 *            String
	 * @param ins_exe
	 *            boolean
	 * @return boolean
	 */
	private boolean onSaveRegOne(String payWay, boolean ins_exe) {
		TParm result = new TParm();
		if (!reg.onNew()) {
			this.messageBox("�Һ�ʧ��");
//			if (payWay.equals("PAY_MEDICAL_CARD")) { // ҽ�ƿ�֧��
//				result = new TParm();
//				result.setData("CURRENT_BALANCE", ektOldSum);
//				result.setData("MR_NO", p3.getValue("MR_NO"));
//				result.setData("SEQ", p3.getValue("SEQ"));
//				result = EKTIO.getInstance().TXwriteEKTATM(result,
//						reg.getPat().getMrNo()); // ��дҽ�ƿ����
//				if (result.getErrCode() < 0)
//					System.out.println("err:" + result.getErrText());
//				// ҽ�ƿ��Һų������⳷������
//				cancleEKTData();
//			}
			if (payWay.equals("PAY_INS_CARD")) { // ҽ����֧��
				if (!ins_exe) { // ҽ�������� ,ɾ����;״̬����
					return false;
				}
				result = new TParm();
				insParm.setData("EXE_TYPE", "REG");
				// ִ�г�������----��Ҫʵ��
//				if (tjINS) { // ҽ�ƿ�����
//					result.setData("CURRENT_BALANCE", ektOldSum);
//					result.setData("MR_NO", p3.getValue("MR_NO"));
//					result.setData("SEQ", p3.getValue("SEQ"));
//					result = EKTIO.getInstance().TXwriteEKTATM(result,
//							p3.getValue("MR_NO")); // ��дҽ�ƿ����
//					if (result.getErrCode() < 0)
//						System.out.println("err:" + result.getErrText());
//					// ҽ�ƿ��Һų������⳷������
//					cancleEKTData();
//				}

			}
			// EKTIO.getInstance().unConsume(tredeNo, this);
			return false;
		}
		return true;
	}

//	/**
//	 * ִ�б��� ҽ�������ݲ���
//	 * 
//	 * @param payWay
//	 *            String
//	 * @param ins_amt
//	 *            double
//	 * @param ins_exe
//	 *            boolean
//	 * @param caseNo
//	 *            String
//	 * @return TParm
//	 */
//	private TParm onSaveRegTwo(String payWay, boolean ins_exe, String caseNo) {
//		double ins_amtTemp = 0.00;// ҽ�����
//		TParm result = new TParm();
//		if (payWay.equals("PAY_INS_CARD")) {
//			// ��ѯ�Ƿ�������������
//			if (null == caseNo || caseNo.length() <= 0) {
//				caseNo = SystemTool.getInstance().getNo("ALL", "REG",
//						"CASE_NO", "CASE_NO"); // ��þ����
//			}
//			TParm parm = new TParm();
//			parm.setData("CASE_NO", caseNo);
//			parm = PatAdmTool.getInstance().selEKTByMrNo(parm);
//			if (parm.getErrCode() < 0) {
//				this.messageBox("E0005");
//				return null;
//			}
//
//			if (parm.getDouble("GREEN_BALANCE", 0) > 0) {
//				this.messageBox("�˾��ﲡ��ʹ��������,������ʹ��ҽ������");
//				return null;
//			}
//			if (this.getValue("REG_CTZ1").toString().length() <= 0) {
//				this.messageBox("��ѡ��ҽ������������");
//				return null;
//			}
//			// ��Ҫ���浽REG_PATADM���ݿ����1.��ְ��ͨ
//			// 2.��ְ���� 3.�Ǿ�����
//			// ҽ�����Һ�
//			// ��ùҺŷ��ô��룬���ý�����
//			//��õ�ǰʱ��
//			String sysdate =StringTool.getString(SystemTool.
//					getInstance().getDate(),"yyyyMMddHHmmss");
//			String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
//					+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
//					+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE_HISTORY B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
//					+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='"
//					+ admType
//					+ "'"
//					+ " AND A.CLINICTYPE_CODE='"
//					+ getValue("CLINICTYPE_CODE") + "'" 
//					+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
//
//			// �Һŷ�
//			double reg_fee = BIL.getRegDetialFee(admType, TypeTool
//					.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
//					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
//							.getString(getValue("REG_CTZ2")), TypeTool
//							.getString(getValue("CTZ3_CODE")), this
//							.getValueString("SERVICE_LEVEL") == null ? ""
//							: this.getValueString("SERVICE_LEVEL"));
//			// ���� �����ۿ�
//			double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
//					.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE",
//					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
//							.getString(getValue("REG_CTZ2")), TypeTool
//							.getString(getValue("CTZ3_CODE")), this
//							.getValueString("SERVICE_LEVEL") == null ? ""
//							: this.getValueString("SERVICE_LEVEL"));
//
//			// System.out.println("regFeesql:::::" + regFeesql);
//			TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
//					regFeesql));
//			if (regFeeParm.getErrCode() < 0) {
//				err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
//				this.messageBox("ҽ��ִ�в���ʧ��");
//				return null;
//			}
//			for (int i = 0; i < regFeeParm.getCount(); i++) {
//				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
//					regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
//					regFeeParm.setData("AR_AMT", i, reg_fee);
//				}
//				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
//					regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
//					regFeeParm.setData("AR_AMT", i, clinic_fee);
//				}
//			}
//			// System.out.println("regFeesql::" + regFeesql);
//			result = TXsaveINSCard(regFeeParm, caseNo); // ִ�в���
//			// System.out.println("RESULT::::" + result);
//			if (null == result)
//				return null;
//			if (result.getErrCode() < 0) {
//				err(result.getErrCode() + " " + result.getErrText());
//				this.messageBox("ҽ��ִ�в���ʧ��");
//				return null;
//			}
//			// 24ҽ����֧��(REG_RECEIPT)
//			if (null != result.getValue("MESSAGE_FLG")
//					&& result.getValue("MESSAGE_FLG").equals("Y")) {
//				System.out.println("ҽ�������ִ����ֽ���ȡ");
//			} else {
//				// ҽ��֧��
//				ins_amtTemp = tjInsPay(result, regFeeParm);
//				ins_exe = true; // ҽ��ִ�в��� ��Ҫ�ж���;״̬
//				reg.setInsPatType(insParm.getValue("INS_TYPE")); // ����ҽ������
//				reg.setConfirmNo(insParm.getValue("CONFIRM_NO")); // ҽ�������
//				// CONFIRM_NO
//			}
//
//		}
//		result.setData("INS_AMT", ins_amtTemp);
//		result.setData("INS_EXE", ins_exe);
//
//		return result;
//	}

	/**
	 * ������� ��������
	 * 
	 * @param payWay
	 *            ֧�����
	 */
	private void onSaveRegParm(String payWay) {
		// 20�ֽ�֧��(REG_RECEIPT)
		if (payWay.equals("PAY_CASH")) {
			reg.getRegReceipt()
					.setPayCash(TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);

		}
		// 21���п�֧��(REG_RECEIPT)
		if (payWay.equals("PAY_BANK_CARD")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
		}
		// 22֧Ʊ֧��(REG_RECEIPT)
		if (payWay.equals("PAY_CHECK")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			// 24��ע(д֧Ʊ��)(REG_RECEIPT)
			reg.getRegReceipt().setRemark(
					TypeTool.getString(getValue("REMARK")));

		}
		// 22����֧��(REG_RECEIPT)
		if (payWay.equals("PAY_DEBIT")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			reg.getRegReceipt().setPayDebit(
					TypeTool.getDouble(getValue("FeeY")));
		}
		// 23ҽ�ƿ�֧��(REG_RECEIPT)
		if (payWay.equals("PAY_MEDICAL_CARD")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY")));
		}
	}

	/**
	 * ������� ��������
	 * 
	 * @return boolean =======pangben 2012-7-1��Ӳ��� ���ֱ������� flg=false ���� ִ�� UPDATE
	 *         QUE_NO ����
	 */
	private boolean onSaveRegParm(boolean flg) {
		String regionCode = TypeTool.getString(getValue("REGION_CODE")); // ����
		String ctz1Code = TypeTool.getString(getValue("REG_CTZ1")); // ���1
		String ctz2Code = TypeTool.getString(getValue("REG_CTZ2")); // ���2
		String ctz3Code = TypeTool.getString(getValue("CTZ3_CODE")); // ���3
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����
		reg.setAdmType(admType);
		// 4����
		reg.setRegion(regionCode);
		// 5��������
		reg.setAdmDate(TypeTool.getTimestamp(getValue("ADM_DATE")));
		// 6�ҺŲ�������
		reg.setRegDate(SystemTool.getInstance().getDate());
		// 7ʱ��
		reg.setSessionCode(TypeTool.getString(getValue("SESSION_CODE")));
		// 8����
		reg.setClinicareaCode((PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(getValue("CLINICROOM_NO"))))
				.getValue("CLINICAREA_CODE", 0));
		// 9����
		reg.setClinicroomNo(TypeTool.getString(getValue("CLINICROOM_NO")));
		// 10�ű�
		reg.setClinictypeCode(TypeTool.getString(getValue("CLINICTYPE_CODE")));
		// System.out.println("��������"+reg.getAdmDate());
		// System.out.println("��ǰ����"+SystemTool.getInstance().getDate());
		// 19�Һŷ�ʽ
		reg.setRegmethodCode(TypeTool.getString(getValue("REGMETHOD_CODE")));
		String admDate = StringTool.getString(reg.getAdmDate(), "yyyyMMdd");
		if (RegMethodTool.getInstance().selPrintFlg(
				this.getValueString("REGMETHOD_CODE"))) {
			// ��ʾ��һƱ��
			if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
					|| reg.getRegReceipt().getBilInvoice().getUpdateNo()
							.length() == 0) {
				this.messageBox("��δ����");
				return false;
			}
		}
		/*if ("Y".equals(reg.getApptCode())) {
			if (this.getPopedem("LEADER")) {
				this.messageBox("���鳤����ԤԼ!");
				return false;
			}
		}*/
		// 17ԤԼ����
		// =========pangben 2012-7-1 ���ֱ��� �͵��չҺ��߼�
		if (flg) {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("ԤԼ");
				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
					this.messageBox("��ѡ������ֳ��Һ�,�������ڱ���Ϊ����!");
					return false;
				}
				reg.setApptCode("Y");
				// 12ԤԼʱ��
				reg.setRegAdmTime(startTime);
			} else {
				// System.out.println("����");
				reg.setApptCode("N");
			}
			// 18������
			if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C")))
				// ����
				reg.setVisitCode("0");
			else {
				// ����
				reg.setVisitCode("1");
			}
			// 11����VIPȡֵ���õ������
			if (!tableFlg) {
				if (!onSaveParm(admDate))
					return false;
			} else {
				// ===========pangben 2012-7-1 �޸� UPDATE ���que_no ֻ����һ�����Ӿ������
				TTable table1 = (TTable) this.getComponent("Table1");
				TParm parm = table1.getParmValue();
				TParm temp = parm.getRow(selectRow); // ��õ�һ��ҳǩ����
				String type = temp.getValue("TYPE"); // VIP ��һ��
				if (type.equals("VIP")) {
					// VIP��Һ�
				} else {
					// ��ͨ��Һ�
					int queNo = SchDayTool.getInstance().selectqueno(
							reg.getRegion(),
							reg.getAdmType(),
							TypeTool.getString(reg.getAdmDate()).replaceAll(
									"-", "").substring(0, 8),
							reg.getSessionCode(), reg.getClinicroomNo());
					if (queNo == 0) {
						this.messageBox("���޾����!");
						return false;
					}
					reg.setQueNo(queNo);
					if (reg.getQueNo() == -1) {
						// ���޺Ų��ܹҺ�
						this.messageBox("E0017");
						return false;
					}
					// ==========pangben 2012-6-18 �޸��غ�����
					TParm regParm = reg.getParm();
					regParm.setData("ADM_DATE", admDate);
					if (onSaveQueNo(regParm)) {
						// return true;
					} else {
						return false;
					}
				}
			}
		} else {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("ԤԼ");
				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
					this.messageBox("��ѡ������ֳ��Һ�,�������ڱ���Ϊ����!");
					return false;
				}
			} else {
				reg.setApptCode("N");
			}
		}

		// 13����
		reg.setDeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 14ҽʦ
		reg.setDrCode(TypeTool.getString(getValue("DR_CODE")));
		// 15ʵ���Ʊ�(Ĭ�Ͽ���)
		reg.setRealdeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 16ʵ��ҽʦ(Ĭ��ҽʦ)
		reg.setRealdrCode(TypeTool.getString(getValue("DR_CODE")));
		// 20����ۿ�1
		reg.setCtz1Code(ctz1Code);
		// 21����ۿ�2
		reg.setCtz2Code(ctz2Code);
		// 22����ۿ�3
		reg.setCtz3Code(ctz3Code);

		// 23ת��Ժ��
		reg.setTranhospCode("");
		// 24���˺�
		reg.setTriageNo("");
		// 25���˵�λ
		reg.setContractCode(TCM_Transform.getString(getValue("CONTRACT_CODE")));
		// 26����ע��
		if (getValue("REGMETHOD_CODE").equals("A"))
			reg.setArriveFlg(true);
		else
			reg.setArriveFlg(false);
		// 27�˹���Ա
		// reg.setRegcanUser();
		// 28�˹�����
		// reg.setRegcanDate();
		// 29�Һ�Ժ��
		reg.setAdmRegion(regionCode);
		// 30Ԥ������ʱ��(�ƻ�����)
		// reg.setPreventSchCode();
		// 31DRG��
		// reg.setDrgCode();
		// 32����ע��
		// reg.setHeatFlg();
		// 33�������
		reg.setAdmStatus("1");
		// 34����״̬
		reg.setReportStatus("1");
		// 35����
		// reg.setWeight();
		// 36���
		// reg.setHeight();
		if (admType.equals("E")){
			reg.setTriageNo(getValue("TRIAGE_NO").toString()); //add by huangtt 20151020 ���˺�
			
			if(getValue("TRIAGE_NO").toString().length() == 0){
				this.messageBox("�ü��ﲡ��û����д���˺ţ�����");
			}
			
			reg.setErdLevel(getValue("ERD_LEVEL").toString());
			reg.setArriveDate(TypeTool.getTimestamp(getValue("ARRIVE_DATE")));
		}

		// �ż����վ�(For bill),REG_RECEIPT����
		// reg.createReceipt();
		// 3�ż�ס��(REG_RECEIPT)
		reg.getRegReceipt().setAdmType(admType);
		// 4����(REG_RECEIPT)
		reg.getRegReceipt().setRegion(regionCode);
		// 5ID��(REG_RECEIPT)
		reg.getRegReceipt().setMrNo(TypeTool.getString(getValue("MR_NO")));
		// 6�����վݺ�(REG_RECEIPT)
		// reg.getRegReceipt().setResetReceiptNo("");
		// 8��������(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9�շ�����(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10�վݴ�ӡ����(REG_RECEIPT)
		// ===================pangben modify 20110818 ���˱�ǣ�PRINT_DATE ��λΪ��ʱ�����м���
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			reg.getRegReceipt()
					.setPrintDate(SystemTool.getInstance().getDate());
			// 7�վ�ӡˢ��(REG_RECEIPT)
			reg.getRegReceipt().setPrintNo(
					reg.getRegReceipt().getBilInvoice().getUpdateNo());

		}

		// 11�Һŷ�(REG_RECEIPT)
		// ======================pangben modify 20110815
		onSaveParm(ctz1Code, ctz2Code, ctz3Code);
		// 12�ۿ�ǰ�Һŷ�(REG_RECEIPT)
		reg.getRegReceipt().setRegFeeReal(
				TypeTool.getDouble(getValue("REG_FEE")));

		// 14�ۿ�ǰ����(REG_RECEIPT)
		reg.getRegReceipt().setClinicFeeReal(
				TypeTool.getDouble(getValue("CLINIC_FEE")));
		// 15���ӷ�(REG_RECEIPT)
		// reg.getRegReceipt().setSpcFee(0.00);
		// 16��������1(REG_RECEIPT)
		// reg.getRegReceipt().setOtherFee1(0.00);
		// 17��������2(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee2(0.00);
		// 18��������3(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee3(0.00);
		// 19Ӧ�ս��(REG_RECEIPT)
		reg.getRegReceipt().setArAmt(TypeTool.getDouble(getValue("FeeY")));
		onSaveRegParm(payWay);
		// 24ҽ����֧��(REG_RECEIPT)
		// reg.getRegReceipt().setPayInsCard(0.00);
		// 26�ż����������(REG_RECEIPT)
		// reg.getRegReceipt().setPayIns(0.00);
		// 28�տ�Ա����(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		// 29���ʱ�־(REG_RECEIPT)
		// reg.getRegReceipt().setAccountFlg("");
		// 30�սᱨ���(REG_RECEIPT)
		// reg.getRegReceipt().setAccountSeq("");
		// 31�ս���Ա(REG_RECEIPT)
		// reg.getRegReceipt().setAccountUser(Operator.getName());
		// 32��������(REG_RECEIPT)
		// reg.getRegReceipt().setAccountDate(SystemTool.getInstance().getDate());
		// ����ȼ�
		reg.setServiceLevel(this.getValueString("SERVICE_LEVEL"));
		// Ʊ������BilInvoice(For bil),BIL_INVOICE����
		// reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// reg.getRegReceipt().getBilInvoice().setCashierCode(Operator.getID());
		// //������Ա
		// reg.getRegReceipt().getBilInvoice().setStartValidDate();
		// reg.getRegReceipt().getBilInvoice().setEndValidDate();
		// reg.getRegReceipt().getBilInvoice().setStatus("1");

		// Ʊ����ϸ��BILInvrcpt(For bil),BIL_INVRCP����
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1Ʊ������(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2��Ʊ����(BIL_INVRCP)

		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // ������Ա(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // �ܽ��(BIL_INVRCP)
		// reg.getRegReceipt().getBilInvrcpt().setCancelFlg();
		// reg.getRegReceipt().getBilInvrcpt().setCancelUser();
		// reg.getRegReceipt().getBilInvrcpt().setCancelDate();
		// �жϳ�ʼ��Ʊ��
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		
		return true;
	}

	/**
	 * �������ͳ������
	 * 
	 * @param admDate
	 *            String
	 * @return boolean flg =false ����������ִ�� UPDATE QUE_NO ����
	 */
	private boolean onSaveParm(String admDate) {
		if (SchDayTool.getInstance().isVipflg(reg.getRegion(),
				reg.getAdmType(), admDate, reg.getSessionCode(),
				reg.getClinicroomNo())) {
			int row = (Integer) callFunction("UI|Table2|getClickedRow");
			if (row < 0)
				return false;
			// �õ�table�ؼ�
			TTable table2 = (TTable) callFunction("UI|table2|getThis");
			TParm data = table2.getParmValue();
			setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
					data, row);
			// 20090217 �·��� -------end---------
			// =======pangben 2012-7-31 �޸Ĳ�ѯ�Ƿ��Ѿ�vipռ��
			int queNoVIP = TypeTool.getInt(table2.getValueAt(row, table2
					.getColumnIndex("QUE_NO")));
			String vipSql = "SELECT QUE_NO,QUE_STATUS FROM REG_CLINICQUE "
					+ "WHERE ADM_TYPE='"
					+ reg.getAdmType()
					+ "' AND ADM_DATE='"
					+ TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8) + "'" + " AND SESSION_CODE='"
					+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
					+ reg.getClinicroomNo() + "' AND  QUE_NO='" + queNoVIP
					+ "' AND QUE_STATUS='N'";
			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				this.messageBox("��ռ��!");
				// ��ʼ������VIP���
				onQueryVipDrTable();
				return false;
			}
			if (queNoVIP == 0) {
				this.messageBox("����VIP�����!");
				return false;
			}
			reg.setQueNo(queNoVIP);

			reg.setVipFlg(true);
			if (reg.getQueNo() == -1) {
				this.messageBox("E0017");
				return false;
			}

		} else {
			int queNo = SchDayTool.getInstance().selectqueno(
					reg.getRegion(),
					reg.getAdmType(),
					TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8), reg.getSessionCode(),
					reg.getClinicroomNo());
			if (queNo == 0) {
				this.messageBox("���޾����!");
				return false;
			}
			reg.setQueNo(queNo);
			if (reg.getQueNo() == -1) {
				// ���޺Ų��ܹҺ�
				this.messageBox("E0017");
				return false;
			}
		}
		// =========pangben 2012-6-18
		TParm regParm = reg.getParm();
		regParm.setData("ADM_DATE", admDate);
		if (onSaveQueNo(regParm)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �������ͳ������
	 * 
	 * @param ctz1Code
	 *            String
	 * @param ctz2Code
	 *            String
	 * @param ctz3Code
	 *            String
	 */
	private void onSaveParm(String ctz1Code, String ctz2Code, String ctz3Code) {
		if (!feeShow) { // �ж��Ƿ���ҽ�����Ļ�õķ���====�Ͼ�ҽ��ʹ�ã�����feeShow=false ����ִ��
			// feeShow=true
			reg.getRegReceipt().setRegFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
							ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL")));
			// 13����(REG_RECEIPT)
			reg.getRegReceipt().setClinicFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")),
							"CLINIC_FEE", ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL"))
									+PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee")); //add by huangtt 20170505 ��������);

		} else {
			reg.getRegReceipt().setRegFee(
					TypeTool.getDouble(getValue("REG_FEE")));
			reg.getRegReceipt().setClinicFee(
					TypeTool.getDouble(getValue("CLINIC_FEE")));
		}
	}

	/**
	 * ҽ�ƿ��Һų������⳷������
	 */
	private void cancleEKTData() {
		// ҽ�ƿ��Һų������⳷������
		TParm oldParm = new TParm();
		oldParm.setData("BUSINESS_NO", businessNo);
		oldParm.setData("TREDE_NO", tredeNo);
		TParm result = TIOM_AppServer.executeAction("action.ins.EKTAction",
				"deleteRegOldData", oldParm);
		// if (result.getErrCode() < 0)
		// System.out.println("err:" + result.getErrText());
	}

	/**
	 * ̩�ĹҺ��Ŷӽк�
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		// System.out.println("========caseNo=========="+caseNo);
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// �Һ�����
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// �������
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("DR_CODE", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("USER_NAME", 0) + "|";
		// �ű�
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// ���
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// ���߲�����
		sendString += result.getValue("MR_NO", 0) + "|";

		// ��������
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// �����Ա�

		sendString += result.getValue("SEX", 0) + "|";
		// ��������
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// �������
		sendString += result.getValue("QUE_NO", 0) + "|";

		// System.out.println("==adm date=="+result.getValue("ADM_DATE",0));

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// System.out.println("===noSql=="+noSql);
		// �޹�����
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// �ѹ����� noParm.getValue("QUE_NO", 0)+ "|";
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";
		// this.messageBox("SESSION_CODE"+((TComboBox)
		// this.getComponent("SESSION_CODE")).getSelectedText());
		// ʱ���
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));
		// System.out.println("===timeSql==="+timeSql);

		// �˹ҽк�
		if ("UNREG".equals(type)) {
			// ԤԼ����

			inParm.setData("msg", sendString);
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + Que_No.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 **/
			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);
			// this.messageBox("�˹ҽк�!");

		} else if ("REG".equals(type)) {
			// System.out.println("adm time===="+this.reg.getRegAdmTime());
			sendString += "|";
			// ԤԼ����
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				// sendString += result.getValue("ADM_DATE", 0).replaceAll("-",
				// "").substring(
				// 0, 8)+startTimeParm.getValue("START_TIME", 0) + "00";
				// System.out.println("========ԤԼsendString=========="+sendString);
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|�ڷ��ڴ�л��|000875|�����|����ҽʦ|06����|000000001009|������|Ů|1936-01-05|2|60|2|����|
			inParm.setData("msg", sendString);
			// this.messageBox("�ҺŽк�!");
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + QueNo.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 * System.out.println("Reg_sendString--->" + sendString);
			 **/

			inParm.setData("msg", sendString);

			TIOM_AppServer.executeAction("action.device.CallNoAction", "doReg",
					inParm);

		}

		return "true";

	}

	/**
	 * �ű�Comboֵ�ı��¼�
	 * 
	 * @param flg
	 *            boolean
	 */
	public void onClickClinicType(boolean flg) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// �Һŷ�
		this.setValue("REG_FEE", reg_fee);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		
		//������Ҫ��������10ԪǮ  add by huangtt 20170330
		clinic_fee = clinic_fee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");

		// ����
		this.setValue("CLINIC_FEE", clinic_fee);
		// //Ӧ�շ���
		// if (pat != null) {
		setValue("FeeY", reg_fee + clinic_fee);
		if (flg) { // ԤԼ�ҺŲ���ʾӦ�ս��
			setValue("FeeS", reg_fee + clinic_fee);
		}

		// }
	}

	/**
	 * �ű�Comboֵ�ı��¼�
	 */
	public void onClickClinicType() {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// �Һŷ�
		this.setValue("REG_FEE", reg_fee);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		
		//������Ҫ��������10ԪǮ  add by huangtt 20170330
		clinic_fee = clinic_fee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");

		// ����
		this.setValue("CLINIC_FEE", clinic_fee);
		// //Ӧ�շ���
		setValue("FeeY", reg_fee + clinic_fee);
		setValue("FeeS", reg_fee + clinic_fee);
		
		
	    //  ==add by zhanglei 20171116  ���ӹҺ�ʱ���Һ������������ݵ�����֤��
//		if(getREGSpecialFlg().equals("Y")){
//			checkSpecialFlg();
//		}
				
		//����Ȩ����֤  wuxinyueԴ����
//		 String str = this.getValueString("REG_CTZ1");
//		 String sql = "SELECT * FROM SYS_CTZ WHERE CTZ_CODE = '"+str+"'";
//		 TParm AA = new TParm(TJDODBTool.getInstance().select(sql));
//		 if(AA.getValue("SPECIAL_FLG").equals("Y")){
//			 this.openDialog("%ROOT%\\config\\reg\\REGSpecialFlg.x");
//		 }
	}
	
	/**
	 * VVIP����Ȩ����֤
	 * zhanglei 20171116
	 */
//	public String getREGSpecialFlg(){
//		String sql = "SELECT SPECIAL_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"
//		+ this.getValueString("REG_CTZ1") + "'";
////		this.messageBox("2:" + sql);
////		System.out.println("VVIP����Ȩ����֤SQL:" + sql);
//		TParm AA = new TParm(TJDODBTool.getInstance().select(sql));
//		if(AA.getValue("SPECIAL_FLG",0).equals("Y")){
//			return "Y";
//		 }else{
//			return "N";
//		 }
//	}
	
	/**
	 * ���������Ϊ������ݴ���֤���ڽ��ж�̬������֤
	 * zhanglei 20171117 У��VVIP�Һ�
	 */
//	public void checkSpecialFlg(){
//		TParm a = new TParm();
//		a.setData("MR_NO",this.getValue("MR_NO"));
//		 Object b = this.openDialog("%ROOT%\\config\\reg\\REGSpecialFlg.x",a);
//		 
//		 //�жϴ����Ƿ��˹��ر�
//		 TParm z = new TParm();
//		 z.setData("kg",0,"Y");
//		 if (b instanceof TParm){
//			 	z = (TParm) b;
//			}
//		 if(z.getValue("kg",0).equals("N")){
//			this.setValue("REG_CTZ1", "");
//		}
//		 authCode = z.getValue("AUTH_CODE",0);//ȡ����֤��
//	}

	/**
	 * �ű�Comboֵ�ı��¼�
	 * 
	 * @param fee
	 *            int
	 */
	public void onClickClinicType(int fee) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		// �Һŷ�
		this.setValue("REG_FEE", reg_fee);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		
		//������Ҫ��������10ԪǮ  add by huangtt 20170330
		clinic_fee = clinic_fee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");

		// ����
		this.setValue("CLINIC_FEE", clinic_fee);
		// Ӧ�շ���
		double feeY = reg_fee + clinic_fee;

		// if (pat != null)
		setValue("FeeY", feeY * fee);
		setValue("FeeS", feeY * fee);
	}

	/**
	 * ֧����ʽ�ı��¼�
	 */
	public void onSelPayWay() {
		if (getValue("PAY_WAY").equals("PAY_CHECK"))
			callFunction("UI|REMARK|setEnabled", true);
		else
			callFunction("UI|REMARK|setEnabled", false);
		if (getValue("PAY_WAY").equals("PAY_DEBIT"))
			callFunction("UI|CONTRACT_CODE|setEnabled", true);
		else
			callFunction("UI|CONTRACT_CODE|setEnabled", false);

	}

	/**
	 * ��ѯҽʦ�Ű�(һ��)
	 * 
	 */
	public void onQueryDrTable() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// ɸѡ����ר�����ͨ��
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		// ���ǹ���Ȩ��
		if (this.getPopedem("deptFilter"))
			parm.setData("DEPT_CODE_SORT", "1101020101");
		TParm data = SchDayTool.getInstance().selectDrTable(parm);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
		TTable table = (TTable) this.getComponent("Table1");
		int selRow = table.getSelectedRow();
		if (selRow < 0)
			return;
		String drCode = table.getItemString(selRow, 4);
		String clinicroomNo = table.getItemString(selRow, 3);
		String sql = "SELECT SEE_DR_FLG FROM REG_PATADM" + "  WHERE DR_CODE='"
				+ drCode + "' " + "AND  CLINICROOM_NO ='" + clinicroomNo + "'"
				+ "AND SEE_DR_FLG='N'";
		// System.out.println("sql===="+sql);
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = selparm.getCount();
		this.setValue("COUNT", count + "");
	}

	/**
	 * ��ѯҽʦ�Ű�(VIP)
	 */
	public void onQueryVipDrTable() {
		TTable table2 = new TTable();
		table2.removeAll();
		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE",
				true);
		parm.setData("ADM_TYPE", admType);
		parm.setData("VIP_ADM_DATE", StringTool.getString(
				(Timestamp) getValue("VIP_ADM_DATE"), "yyyyMMdd"));
		TParm data2 = REGClinicQueTool.getInstance().selVIPDate(parm);
		if (data2.getErrCode() < 0) {
			messageBox(data2.getErrText());
			return;
		}
		this.callFunction("UI|Table2|setParmValue", data2);
	}

	/**
	 * ��ѯ�����Һ���Ϣ
	 */
	// CASE_NO;ADM_DATE;SESSION_CODE;DEPT_CODE;DR_CODE;QUE_NO;ADM_STATUS;ARRIVE_FLG;CONFIRM_NO;INS_PAT_TYPE;REGMETHOD_CODE
	public void selPatInfoTable() {
		TParm parm = new TParm();
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_START_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_END_DATE")), "yyyyMMdd");
		parm.setData("MR_NO", pat.getMrNo()); 
		parm.setData("YY_START_DATE", startTime);
		parm.setData("YY_END_DATE", endTime);
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		TParm data = PatAdmTool.getInstance().selPatInfoForREG(parm);
		// System.out.println("table3��ʾ����" + data);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		
		this.callFunction("UI|Table3|setParmValue", data);

	}

	/**
	 * ���ݿ��������б���ѯҽʦ�Ű�(һ��)
	 */
	public void onQueryDrTableByDrCombo() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		parm.setDataN("DEPT_CODE_SORT", TypeTool
				.getString(getValue("DEPT_CODE_SORT")));
		// ɸѡ����ר�����ͨ��
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}

			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		TParm data = SchDayTool.getInstance().selectDrTable(parm);

		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
	}

	/**
	 * ��������
	 */
	public void onFee() {
		DecimalFormat df = new DecimalFormat("##########0.00");
		// ������
		setValue("FeeZ", TypeTool.getDouble(df.format(getValue("FeeS")))
				- TypeTool.getDouble(df.format(getValue("FeeY"))));
		// �õ�����
		this.grabFocus("SAVE_REG");
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		// TParm forPrtParm = new TParm();
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		String caseNo = (String) table3.getValueAt(row, 0);
		String confirmNo = (String) table3.getParmValue().getValue(
				"CONFIRM_NO", row);
		if (this.getValueString("NEXT_NO").length() <= 0
				|| this.getValueString("NEXT_NO").compareTo(endInvNo) > 0) {
			this.messageBox("Ʊ��������!");
			return;
		}
		TParm temp = new TParm();
		temp.setData("RECEIPT_TYPE", "REG");
		temp.setData("CASE_NO", caseNo);
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			temp.setData("REGION_CODE", Operator.getRegion());
		TParm result = BILContractRecordTool.getInstance().regRecodeQuery(temp);
		if (null != result && result.getValue("BIL_STATUS", 0).equals("1")) {
			this.messageBox("���˹Һŷ���û��ִ�н������,�����Դ�Ʊ");
			return;
		}

		TParm onREGReprintParm = new TParm();
		onREGReprintParm.setData("CASE_NO", caseNo);
		onREGReprintParm.setData("OPT_USER", Operator.getID());
		onREGReprintParm.setData("OPT_TERM", Operator.getIP());
		onREGReprintParm.setData("ADM_TYPE", admType);
		result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onREGReprint", onREGReprintParm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ӡ����ʧ��");
			return;
		}
		result = PatAdmTool.getInstance().getRegPringDate(caseNo, "COPY");
		result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("CONFIRM_NO", confirmNo);
		TParm mzConfirmParm = INSMZConfirmTool.getInstance().queryMZConfirm(
				parm); // �жϴ˴β����Ƿ���ҽ������
		if (mzConfirmParm.getErrCode() < 0) {
			return;
		}
		TParm printParm = null;
		if (mzConfirmParm.getCount() > 0) {
			printParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
			insFlg = true;
		}
		onRePrint(result, mzConfirmParm, printParm);
		this.onInit();
	}

	/**
	 * ��ӡ
	 * 
	 * @param parm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @param printParm
	 *            TParm
	 */
	private void onRePrint(TParm parm, TParm mzConfirmParm, TParm printParm) {
		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // ������������
		// ��ʾ��ʽ:����(����)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "��)"); // �ű�
		// ��ʾ��ʽ:�ű�(���)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		parm.setData("BALANCE_NAME", "TEXT", "�� ��"); // �������
		DecimalFormat df = new DecimalFormat("########0.00");
		if (tjINS) {
			ektNewSum = df.format(p3.getDouble("CURRENT_BALANCE"));
		}
		parm.setData("CURRENT_BALANCE", "TEXT", "�� "
				+ df.format(Double.parseDouble(ektNewSum == null
						|| "".equals(ektNewSum) ? "0.00" : ektNewSum))); // ҽ�ƿ�ʣ����

		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_CASH", "TEXT", "�ֽ�:"
					+ StringTool.round(
							(parm.getDouble("TOTAL", "TEXT") - printParm
									.getDouble("PAY_INS_CARD", 0)), 2)); // �ֽ�
			// �����˻�
			String sqlamt = " SELECT ACCOUNT_PAY_AMT  FROM INS_OPD "
					+ " WHERE CASE_NO ='"
					+ mzConfirmParm.getValue("CASE_NO", 0) + "'"
					+ " AND CONFIRM_NO ='"
					+ mzConfirmParm.getValue("CONFIRM_NO", 0) + "'";
			
			TParm insaccountamtParm = new TParm(TJDODBTool.getInstance()
					.select(sqlamt));
			if (insaccountamtParm.getErrCode() < 0) {

			} else {
				parm.setData("PAY_ACCOUNT", "TEXT", "�˻�:"
						+ StringTool.round(insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0), 2));
				parm.setData("PAY_DEBIT", "TEXT", "ҽ��:"
						+ StringTool.round((printParm.getDouble("PAY_INS_CARD",
								0) - insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0)), 2)); // ҽ��֧��
			}
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ mzConfirmParm.getValue("SPECIAL_PAT", 0) + "'";// ҽ��������Ա�����ʾ
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // ����
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		// ===zhangp 20120313 start
		if ("1".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			// parm.setData("Cost_class", "TEXT", "��ͳ");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		} else if ("2".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			// parm.setData("Cost_class", "TEXT", "����");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		}
		// ===zhangp 20120313 end
        String caseNo = parm.getValue("CASE_NO", "TEXT");//add by wanglong 20121217
        TParm oldDataRecpParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);//add by wanglong 20121217
        parm.setData("RECEIPT_NO", "TEXT", oldDataRecpParm.getData("RECEIPT_NO", 0));//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				parm, true);
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("REGRECPPrint.jhw"),
                             IReportTool.getInstance().getReportParm("REGRECPPrint.class", parm), true);//����ϲ�modify by wanglong 20130730
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.initReg();
		clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL;NHI_NO;EKT_CURRENT_BALANCE;COUNT");
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
			setValue("TRIAGE_NO", "");
			this.setValue("ARRIVE_DATE", SystemTool.getInstance().getDate().toString().substring(0,16).replaceAll("-", "/"));
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		callFunction("UI|FOREIGNER_FLG|setEnabled", true); // ����֤���ɱ༭======pangben
		// modify 20110808
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
//			callFunction("UI|MR_NO|setEnabled", false);
//			this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			callFunction("UI|MR_NO|setEnabled", true);
			this.grabFocus("MR_NO");
		}
		// callFunction("UI|MR_NO|setEnabled", true); //����֤���ɱ༭======pangben
		// modify 20110808
		callFunction("UI|CONTRACT_CODE|setEnabled", true); // ���˵�λ�ɱ༭
		callFunction("UI|SAVE_REG|setEnabled", true);//�շѰ�ť�ɱ༭
		// ����Ĭ�Ϸ���ȼ�
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		// feeIstrue = false;
		ins_amt = 0.00; // ҽ�����
		accountamtforreg = 0.00;// �����˻�
		feeShow = false; // �Ͼ�ҽ�����Ļ�÷�����ʾ
		txEKT = false; // ̩��ҽ�ƿ�д���ܿ�
		p3 = null; // ҽ�ƿ�����parm
		insFlg = false; // ҽ������������
		tjINS = false; // ҽ�ƿ�������ɲ���
		reSetEktParm = null; // ҽ�ƿ��˷�ʹ���ж��Ƿ�ִ��ҽ�ƿ��˷Ѳ���
		confirmNo = null; // ҽ��������ţ��˹�ʱʱʹ��
		reSetCaseNo = null; // �˹�ʹ�þ������
		insType = null;
		tableFlg = false; // �ж�ѡ�е�һ��ҳǩ�������
		ektNewSum = "0.00"; // ҽ�ƿ��ۿ����
		// ===zhangp 20120427 start
		greenParm = null;// //��ɫͨ��ʹ�ý��
		insParm=null;
		// ��������
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		initSchDay();
		callFunction("UI|SAVE_REG|setEnabled", true);
		pat = null;
		// reg=null;
		ins_exe = false;
	}

	/**
	 * �Ƿ�رմ���
	 * 
	 * @return boolean true �ر� false ���ر�
	 */
	public boolean onClosing() {
		// ��������
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		return true;
	}

	/**
	 * ��ʼ��ʱ��
	 */
	public void initSession() {
		// ��ʼ��ʱ��Combo,ȡ��Ĭ��ʱ��
		String defSession = SessionTool.getInstance().getDefSessionNow(admType,
				Operator.getRegion());
		setValue("SESSION_CODE", defSession);
		setValue("VIP_SESSION_CODE", defSession);
	}

	/**
	 * Ϊ��պ��ʼ��
	 */
	public void initReg() {
		// ����Ĭ�����
		setValue("CTZ1_CODE", "99");
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// ��������
		combo_ctz.setNhiFlg("");
		combo_ctz.onQuery();
		setValue("REG_CTZ1", "99");
		setValue("REGION_CODE", Operator.getRegion());
		setValue("ADM_DATE", SystemTool.getInstance().getDate());
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
			this.setValue("ADM_DATE", admDate);
		}
		// ��ʼ��Ĭ��(�ֳ�)�Һŷ�ʽ
		setValue("REGMETHOD_CODE", "A");

		// ��ʼ��ԤԼ��Ϣ��ʼʱ��
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// ��ʼ��VIP���Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// ���˹�,����,��ӡ��ťΪ��
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|print|setEnabled", false);
		// ���շѰ�ť�ɱ༭
		callFunction("UI|SAVE_REG|setEnabled", true);
		// �ùҺ���Ϣ����ؼ��ɱ༭
		setControlEnabled(true);
		setRegion();
	}

	/**
	 * ͨ���ʱ�ĵõ�ʡ��
	 */
	public void onPost() {
		String post = getValueString("POST_CODE");
		TParm parm = SYSPostTool.getInstance().getProvinceCity(post);
		if (parm.getErrCode() != 0 || parm.getCount() == 0) {
			return;
		}
		setValue("STATE", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("CITY", parm.getData("POST_CODE", 0).toString());
		this.grabFocus("ADDRESS");
	}

	/**
	 * ���������Ƿ��������
	 */
	public void setRegion() {
		if (!REGSysParmTool.getInstance().selOthHospRegFlg())
			callFunction("UI|REGION_CODE|setEnabled", false);
	}

	/**
	 * ͨ�����д�����������
	 */
	public void selectCode() {
		this.setValue("POST_CODE", this.getValue("CITY"));
	}

	/**
	 * ��ⲡ����ͬ����
	 */
	public void onPatName() {
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE PAT_NAME = '"
				+ patName
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		setPatName1();
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				this.grabFocus("PY1");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryMrNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("PY1");
	}

	/**
	 * ��ⲡ����ͬ���֤��
	 */
	public void onIDNo() {
		String idNo = this.getValueString("IDNO");
		if (StringUtil.isNullString(idNo)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.IDNO = '"
				+ idNo
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ�绰������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				this.grabFocus("TEL_HOME");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryMrNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("TEL_HOME");
	}

	/**
	 * ��ⲡ����ͬ���֤��
	 */
	public void onTelHome() {
		String telHome = this.getValueString("TEL_HOME");
		if (StringUtil.isNullString(telHome)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat =
		// ===zhangp 20120319 start
		"SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.TEL_HOME = '" + telHome + "'  "
				+ " AND A.MR_NO = B.MR_NO (+) " + " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ�绰���벡����Ϣ,�Ƿ�������������Ϣ",
					0);
			if (sameCount != 1) {
				this.grabFocus("POST_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryMrNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("POST_CODE");
	}

	/**
	 * ����
	 */
	public void onArrive() {
	//  ==add by zhanglei 20171116  ���ӹҺ�ʱ���Һ������������ݵ�����֤��
//		if(getREGSpecialFlg().equals("Y")){
//			checkSpecialFlg();
//		}
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		// ====zhangp 20120306 modify start
		TParm table3Parm = table3.getParmValue();
		String admdate = table3Parm.getData("ADM_DATE", row).toString();
		admdate = admdate.substring(0, 10);
		String date = SystemTool.getInstance().getDate().toString();
		date = date.substring(0, 10);
		if (!admdate.equals(date)) {
			messageBox("�ǵ��գ����ܱ���");
			return;
		}
		// ====zhangp 20120306 modify end
		String caseNo = (String) table3.getValueAt(row, 0);
		reg = null;
		reg = reg.onQueryByCaseNo(pat, caseNo);
		// // ����ҽ�ƿ�
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("�����Ų���Ϊ��");
			return;
		}
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		// �Һ�����,REG����
		// 2�ż���
		if (!onSaveRegParm(false))
			return;
		reg.setTredeNo(tredeNo);
		TParm regParm = reg.getParm();
		//add caoyong 20140311 ----start
		if("".equals(this.getValue("REG_CTZ1"))||this.getValue("REG_CTZ1")==null){
			this.messageBox("�Һ����һ����Ϊ��");
			this.grabFocus("REG_CTZ1");
			return ;
		}
		//add by huangw 20150817
		if (admType.endsWith("E")) {
			if (this.getValue("ARRIVE_DATE")==null||this.getValue("ARRIVE_DATE").toString().length()<0){//У�鵽Ժʱ�� add by haungjw 20150603
				this.messageBox("��Ժʱ�䲻��Ϊ��");
				this.grabFocus("ARRIVE_DATE");
				return;
			}
			regParm.setData("ARRIVE_DATE",this.getValue("ARRIVE_DATE").toString().substring(0,19).replaceAll("-", "/"));

		}
		//add caoyong 20140311 ----end
		regParm.setData("CTZ1_CODE", this.getValue("REG_CTZ1"));
		regParm.setData("CTZ2_CODE", this.getValue("REG_CTZ2"));
		regParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");

		reg.getRegReceipt().setCaseNo(caseNo);
		// 8��������(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9�շ�����(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10�վݴ�ӡ����(REG_RECEIPT)
		reg.getRegReceipt().setPrintDate(SystemTool.getInstance().getDate());
		// 28�տ�Ա����(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		reg.getRegReceipt().setReceiptNo(receiptNo); // �Һ��վ�(REG_RECEIPT)
		// Ʊ������BilInvoice(For bil),BIL_INVOICE����
		reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// Ʊ����ϸ��BILInvrcpt(For bil),BIL_INVRCP����
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); // Ʊ����ϸ���վݺ�(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1Ʊ������(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2��Ʊ����(BIL_INVRCP)
		// 7�վ�ӡˢ��(REG_RECEIPT)
		reg.getRegReceipt().setPrintNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo());
		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // ������Ա(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // �ܽ��(BIL_INVRCP)
		// �жϳ�ʼ��Ʊ��
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// ��ʾ��һƱ��
		if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
				|| reg.getRegReceipt().getBilInvoice().getUpdateNo().length() == 0) {
			this.messageBox("��δ����");
			return;
		}
		reg.getRegReceipt().getBilInvoice().getParm();
		// �ż�������
		TParm saveParm = new TParm();

		// Ʊ������
		TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
		saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

		// Ʊ����ϸ��
		TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
		bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
		saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
		saveParm.setData("TREDE_NO", reg.getTredeNo());
		// ҽ��ҽ�Ʋ��� ���ò���
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����
		if (!onInsEkt(payWay, caseNo)) {
			return;
		}
		
		saveParm.setData("EKT_SQL", reg.getEktSql());  //add by huangtt 20160914  ����ʱҽ�ƿ�ִ��SQL
		
		saveParm.setData("REG", regParm.getData());
		// �����վ�
		TParm regReceiptParm = reg.getRegReceipt().getParm();
		saveParm.setData("REG_RECEIPT", regReceiptParm.getData());
		if (ins_exe) {
			saveParm.setData("insParm", insParm.getData());// ����ҽ������ִ���޸�REG_PADADM
			// ����INS_PAT_TYPE ��
			// COMFIRM_NO �ֶ�
		}
		
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onSaveRegister", saveParm);
		// System.out.println("result:::::" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			// EKTIO.getInstance().unConsume(tredeNo, this);
			// ҽ�ƿ�������д���
//			if (payWay.equals("PAY_MEDICAL_CARD")) {
//				TParm writeParm = new TParm();
//				writeParm.setData("CURRENT_BALANCE", ektOldSum);
//				writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
//						pat.getMrNo()); // ��дҽ�ƿ����
//				if (writeParm.getErrCode() < 0)
//					System.out.println("err:" + writeParm.getErrText());
//			}
			return;
		}
		//����ҽ�����е�RECEIPT_NO
		if (ins_exe) {
		 String confirmNo = insParm.getValue("CONFIRM_NO");
	     String sql = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
		   " WHERE CASE_NO ='" + caseNo + "'" +
		   " AND CONFIRM_NO = '" + confirmNo + "'" +
		   " AND RECP_TYPE = 'REG'";
         TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
        if (updateParm.getErrCode() < 0) {
           err(updateParm.getErrCode() + " " + updateParm.getErrText());
             updateParm.setErr(-1, "����ҽ����ʧ��");
             return ;
         }
	}
		// �����Ʊ����
		result = onPrintParm();
		onPrint(result);
		this.onClear();
		// ���������Ŷӽк�
		if (!"true".equals(callNo("REG", caseNo))) {
			this.messageBox("�к�ʧ��");
		}
		BilInvoice invoice = new BilInvoice();
		invoice = invoice.initBilInvoice("REG");
		// ��ʼ����һƱ��
		setValue("NEXT_NO", invoice.getUpdateNo());
		callFunction("UI|arrive|setEnabled", false);
		// this.selPatInfoTable();
		this.callFunction("UI|table3|clearSelection");
	}

	/**
	 * ����Ӣ����
	 */
	public void setPatName1() {
		String patName1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		setValue("PAT_NAME1", patName1);
	}

	/**
	 * �˹Ҳ���
	 */
	public void onUnReg() {
		// =====zhangp 20120301 modify start
		if (this.messageBox("ѯ��", "�Ƿ��˹�", 2) == 0) {
			this.callFunction("UI|unreg|setEnabled", false);
//			if (!this.getPopedem("LEADER")) { //  == zhanglei 20171201 ���ͻ�Ҫ��ȥ���˹ҹܿ�
//				this.messageBox("���鳤�����˹�!");
//				return;
//			}
			TTable table3 = (TTable) callFunction("UI|table3|getThis");
			int row = table3.getSelectedRow();
			if (row < 0) {
				this.messageBox("��ѡ��Ҫ�˹ҵ�����");
				return;
			}
			// ===zhangp 20120316 start
			String arriveFlg = (String) table3.getValueAt(row, 7);
			// �ж��Ƿ�ԤԼ�Һ�
			if ("N".equals(arriveFlg)) {
				//add by huangtt 20160530 start ��ԤԼʱ�������QҽԤԼ�Ľ�Ҫ��Qҽ����Ϣ��������һ��
					String qSql = "SELECT CASE_NO,MR_NO,OPT_USER FROM REG_PATADM WHERE CASE_NO='"+table3.getParmValue().getValue("CASE_NO", row)+"' AND QEAPP_FlG='Y'";
					TParm qParm = new TParm(TJDODBTool.getInstance().select(qSql));
					if(qParm.getCount()>0){
						TParm result = TIOM_AppServer.executeAction("action.reg.REGQeAppAction",
								"unRegQe", qParm.getRow(0));
						System.out.println(result);
						if(result == null){
							System.out.println("Qҽ�ӿڲ���=="+qParm.getRow(0));
							this.messageBox("����Qҽ��ԤԼ�ӿڳ����쳣������ϵQҽ������Ա�ֶ��޸�");
						}
						
						
						
					}
					
				//add by huangtt 20160530 end 
				

				table3.getParmValue().getRow(row);
				String sql = "UPDATE REG_PATADM SET REGCAN_USER = '"
						+ Operator.getID()
						+ "',REGCAN_DATE = SYSDATE,OPT_USER = '"
						+ Operator.getID() + "',"
						+ "OPT_DATE = SYSDATE,OPT_TERM = '" + Operator.getIP()
						+ "' " + "WHERE CASE_NO = '"
						+ table3.getParmValue().getValue("CASE_NO", row) + "'";
				TParm updateParm = new TParm(TJDODBTool.getInstance().update(
						sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("�˹�ʧ��");
					return;
				}
				String admDate = table3.getParmValue()
						.getValue("ADM_DATE", row);
				admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
						+ admDate.substring(8, 10);
				sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
						+ table3.getParmValue().getValue("ADM_TYPE", row)
						+ "'AND ADM_DATE = '"
						+ admDate
						+ "' AND "
						+ "SESSION_CODE = '"
						+ table3.getParmValue().getValue("SESSION_CODE", row)
						+ "' AND "
						+ "CLINICROOM_NO = '"
						+ table3.getParmValue().getValue("CLINICROOM_NO", row)
						+ "' AND "
						+ "QUE_NO = '"
						+ table3.getParmValue().getValue("QUE_NO", row) + "'";
				updateParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("�˹�ʧ��");
					return;
				}
				messageBox("ԤԼȡ���ɹ�");
				// �����Ŷӽк�
				if (!"true".equals(callNo("UNREG", table3.getParmValue()
						.getValue("CASE_NO", row)))) {
					this.messageBox("�к�ʧ��");
				}
				this.onClear();
				return;
			}
			// ===zhangp 20120316 end
			String caseNo = (String) table3.getValueAt(row, 0);
			TParm tredeParm = new TParm(); // ��ѯ�˴��˹Ҳ����Ƿ���ҽ�ƿ��˹�
			tredeParm.setData("CASE_NO", caseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // ����
			tredeParm.setData("STATE", "1"); // ״̬�� 0 �ۿ� 1 �ۿ��Ʊ 2�˹� 3 ����
			confirmNo = table3.getParmValue().getValue("CONFIRM_NO", row); // ҽ�������
			reSetCaseNo = table3.getParmValue().getValue("CASE_NO", row); // ҽ���˹�ʹ��
			insType = table3.getParmValue().getValue("INS_PAT_TYPE", row); // ҽ����������1.��ְ��ͨ2.��ְ����
			// 3.�Ǿ�����
			if (null != confirmNo && confirmNo.length() > 0) {
				// ִ��ҽ������
				// System.out.println("ҽ�����˷�");
			} else {
				reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // ҽ�ƿ��˷Ѳ�ѯ
				if (reSetEktParm.getErrCode() < 0) {
					this.messageBox("�˹�ִ������");
					return;
				}
				if (reSetEktParm.getCount() > 0) { // ������ڵ���û�л��ҽ�ƿ���Ϣ����ʾ==pangb
					// 2011-11-29
					String payWay = this.getValueString("PAY_WAY");
					if (!"PAY_MEDICAL_CARD".equals(payWay)) {
						this.messageBox("���ȡҽ�ƿ���Ϣ");
						return;
					}
				}
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("RECEIPT_TYPE", "REG");
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				parm.setData("REGION_CODE", Operator.getRegion());

			TParm result = BILContractRecordTool.getInstance().regRecodeQuery(
					parm);
			// ��ѯ�Ƿ��м�����Ϣ
			if (null != result && result.getCount() > 0) {
				// �Ѿ�������ɵĹҺŷ�
				if ("2".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes2(caseNo, true);
				} else if ("1".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes1(caseNo);
				}
				// �����˹�
			} else {
				onUnRegNo(caseNo, false);
			}
			this.onClear();
		} else
			return;

	}

	/**
	 * �������֤
	 */
	public void idnoInfo() {
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfoFromID.x");
	}

	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.TBuilder();

	}

	/**
	 * ̩��ҽ�ƿ��ۿ����
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return boolean
	 */
	private boolean onTXEktSave(String FLG, TParm insParm) {
		int type = 0;
		TParm parm = new TParm();
		// ���ʹ��ҽ�ƿ������ҿۿ�ʧ�ܣ��򷵻ز�����
		if (EKTIO.getInstance().ektSwitch()) { // ҽ�ƿ����أ���¼�ں�̨config�ļ���
			if (null == insParm)
				parm = onOpenCard(FLG);
			else
				parm = onOpenCard(FLG, insParm);
			// System.out.println("��ҽ�ƿ�parm=" + parm);
			if (parm == null) {
				this.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			// System.out.println("type===" + type);
			if (type == 3) {
				this.messageBox("E0115");
				return false;
			}
			if (type == 2) {
				return false;
			}
			if (type == -1) {
				this.messageBox("��������!");
				return false;
			}
			tredeNo = parm.getValue("TREDE_NO");
			businessNo = parm.getValue("BUSINESS_NO"); // //����ҽ�ƿ��ۿ��������ʹ��
			ektOldSum = parm.getValue("OLD_AMT"); // ִ��ʧ�ܳ����Ľ��
			ektNewSum = parm.getValue("EKTNEW_AMT"); // �ۿ��Ժ�Ľ��
			// �ж��Ƿ������ɫͨ��
			if (null != parm.getValue("GREEN_FLG")
					&& parm.getValue("GREEN_FLG").equals("Y")) {
				greenParm = parm;
			}
			// System.out.println("ektNewSum======"+ektNewSum);
		} else {
			this.messageBox_("ҽ�ƿ��ӿ�δ����");
			return false;
		}
		return true;

	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @param FLG
	 *            String
	 * @return boolean
	 */
	public boolean onEktSave(String FLG) {
		return onTXEktSave(FLG, null);
	}

	/**
	 * ��ҽ�ƿ�
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG) {
		if (reg == null) {
			return null;
		}
		// ׼������ҽ�ƿ��ӿڵ�����
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("INS_FLG", "N");
		// ҽ�����������ֽ���ȡ
		reg.setInsPatType(""); // ����ҽ������ ��Ҫ���浽REG_PATADM���ݿ����1.��ְ��ͨ 2.��ְ����
		// 3.�Ǿ�����
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))); // ҽ�ƿ��Ѿ��շѵ�����
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);

		return parm;
	}

	/**
	 * ���ҽ��
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG, TParm insParm) {
		// ׼������ҽ�ƿ��ӿڵ�����
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // ҽ�����ԷѲ��ֽ��
		orderParm.setData("INS_AMT", insParm.getDouble("INS_SUMAMT")); // ҽ�����ԷѲ��ֽ��
		orderParm.setData("INS_FLG", "Y"); // ҽ����ע��
		orderParm.setData("OPBEKTFEE_FLG", true);// ȡ����ť
		orderParm.setData("RECP_TYPE", "REG"); // ���EKT_ACCNTDETAIL ������ʹ��
		orderParm.setData("comminuteFeeParm", insParm.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�ز���
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // �˲��������շ�ҽ�������Ѿ���Ʊ��
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // ��ʾ���
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);
		return parm;
	}

	/**
	 * ҽ�ƿ����
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	private TParm orderEKTParm(String FLG) {
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // д�̶�ֵ
		orderParm.addData("ORDER_CODE", "REG"); // д�̶�ֵ
		orderParm.addData("SEQ_NO", "1"); // д�̶�ֵ
		orderParm.addData("EXEC_FLG", "N"); // д�̶�ֵ
		orderParm.addData("RECEIPT_FLG", "N"); // д�̶�ֵ
		orderParm.addData("BILL_FLG", FLG);
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		orderParm.setData("BUSINESS_TYPE", "REG");
		return orderParm;
	}

	/**
	 * �˹Ҳ���ҽ�ƿ��˷Ѳ���
	 * 
	 * @param caseNo
	 * @param type
	 *            1.����ҽ�ƿ��˷� 2.ҽ�����˷�
	 * @return
	 */
	public TParm onOpenCardR(String caseNo) {
		// ׼������ҽ�ƿ��ӿڵ�����
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // д�̶�ֵ
		orderParm.addData("ORDER_CODE", "REG"); // д�̶�ֵ
		orderParm.addData("SEQ_NO", "1"); // д�̶�ֵ
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.addData("EXEC_FLG", "N"); // д�̶�ֵ
		orderParm.addData("RECEIPT_FLG", "N"); // д�̶�ֵ
		orderParm.addData("BILL_FLG", "N");
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		orderParm.setData("BUSINESS_TYPE", "REGT");
		orderParm.setData("TYPE_FLG", "Y");
		if (null != confirmNo && confirmNo.length() > 0) {
			orderParm.setData("OPBEKTFEE_FLG", true);
		}
		orderParm.setData("ektParm", p3.getData());
		// ��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
		TParm parm = new TParm();
		parm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		parm.setData("CASE_NO", caseNo);
		TParm ektSumParm = EKTNewTool.getInstance().selectEktTrade(parm);
		orderParm.setData("EXE_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0)); // ҽ�ƿ��Ѿ��շѵ�����
		orderParm.setData("SHOW_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0));
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		
		
		// System.out.println("MR_NO" + pat.getMrNo());
		// System.out.println("�˹Ҵ�����"+TypeTool.getDouble(getValue("FeeY")));
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
//		parm = EKTIO.getInstance().onOPDAccntClient(orderParm, caseNo, this);
//		return parm;
		return orderParm;
	}

	/**
	 * ̩��ҽ�ƿ����� =========================pangben modify 20110808
	 */
	public void onEKT() {// modify by kangy 20170307
		//kangy  �ѿ���ԭ    p3 = EKTReadCard.getInstance().readEKT();
		p3 = EKTIO.getInstance().TXreadEKT();
		EKT(p3);
		/*// �Ͼ�ҽ������������
		// ̩��ҽ�ƿ�����
		p3 = EKTIO.getInstance().TXreadEKT();
		// System.out.println("P3=================" + p3);
		// 6.�ͷŶ����豸
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.ע��TFReader.dll
		// int ret100 = NJSMCardDriver.close();
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // ҽ�ƿ�����
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql
					.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // �Ͼ�ҽ����
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // ̩��ҽ�ƿ�
			this.setValue("PAY_WAY", "PAY_MEDICAL_CARD"); // ֧����ʽ�޸�
			this.setValue("CONTRACT_CODE", "");
			callFunction("UI|CONTRACT_CODE|setEnabled", false); // ���˵�λ���ɱ༭
		}
		// ͨ�����֤�Ų����Ƿ���ڴ˲�����Ϣ
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//����֤�����ɱ༭
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		// �Ͼ�ҽ��������
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// ===zhangp 20120318 endg
*/	}
	public void EKT(TParm p3){//kangy
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // ҽ�ƿ�����
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql
					.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // �Ͼ�ҽ����
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // ̩��ҽ�ƿ�
			if(null!=p3.getValue("READ_TYPE")&&"INSCARD".equals(p3.getValue("READ_TYPE"))){
				this.setValue("PAY_WAY", "PAY_INS_CARD"); // ֧����ʽ�޸�
			}else if(null!=p3.getValue("READ_TYPE")&&"IDCARD".equals(p3.getValue("READ_TYPE"))){
				this.setValue("PAY_WAY", "PAY_CASH"); // ֧����ʽ�޸�
			}else {
			this.setValue("PAY_WAY", "PAY_MEDICAL_CARD"); // ֧����ʽ�޸�
			}
			this.setValue("CONTRACT_CODE", "");
			callFunction("UI|CONTRACT_CODE|setEnabled", false); // ���˵�λ���ɱ༭
		}
		// ͨ�����֤�Ų����Ƿ���ڴ˲�����Ϣ
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//����֤�����ɱ༭
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		// �Ͼ�ҽ��������
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// ===zhangp 20120318 end
	}

	/**
	 * ������������� ==============pangben 2013-3-18
	 */
	public void onReadIdCard() {// modify by kangy
		//kangy �ѿ���ԭ   start
		/*TParm idParm=new TParm();
		TParm infoParm=new TParm();
		dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());
		if(dev_flg){
			infoParm=EKTReadCard.getInstance().readIDCard();
		}else{
			idParm= IdCardO.getInstance().readIdCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		 p3=new TParm();
		 String sql="SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,B.IDNO,A.EKT_CARD_NO AS CARD_NO,A.CARD_NO AS PK_CARD_NO, A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,C.CURRENT_BALANCE" 
	              +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C, SYS_PATINFO D WHERE " 
	              //+" D.IDNO='430103' "
	              +" D.IDNO='"+idParm.getValue("IDNO")+"'"
	              +" AND D.MR_NO=A.MR_NO "
	              +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
			 infoParm=new TParm(TJDODBTool.getInstance().select(sql));
			}
			if(infoParm.getCount()==0){
				p3=idParm;
			}
			if(infoParm.getCount()>1){
				p3 = (TParm) openDialog(
						"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", infoParm);
			}else
			p3=infoParm.getRow(0);
		p3.setData("READ_TYPE","IDCARD");
		EKT(p3);*/
		//kangy   �ѿ���ԭ    end
		TParm idParm = IdCardO.getInstance().readIdCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		if (idParm.getCount() > 0) {// ����������ʾ
			if (idParm.getCount()==1) {//pangben 2013-8-8 ֻ����һ������
				//onQueryNO(idParm.getValue("MR_NO",0));//�ѿ���ԭ      ���ѿ������в��������汾   ����������
				onQueryMrNO(idParm.getValue("MR_NO",0));
			}else{
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						idParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					//onQueryNO(patParm.getValue("MR_NO"));//�ѿ���ԭ      ���ѿ������в��������汾   ����������
					onQueryMrNO(patParm.getValue("MR_NO"));
				}else{
					return ;
				}
			}
			setValue("VISIT_CODE_F", "Y"); // ����
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
			setPatName1();// ����Ӣ��
		} else {
			String sql="SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
				+idParm.getValue("PAT_NAME")+"%'";
			TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
			if (infoParm.getCount()<=0) {
				this.messageBox(idParm.getValue("MESSAGE"));
				setValue("VISIT_CODE_C", "Y"); // Ĭ�ϳ���
				callFunction("UI|MR_NO|setEnabled", false); // �����Ų��ɱ༭--�������
			}else{
				this.messageBox("������ͬ�����Ĳ�����Ϣ");
				this.grabFocus("PAT_NAME");//Ĭ��ѡ��
			}
			this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
			this.setValue("IDNO", idParm.getValue("IDNO"));
			this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
			this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
			this.setValue("ADDRESS", idParm.getValue("RESID_ADDRESS"));// ��ַ
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
			setPatName1();// ����Ӣ��
			
		}
	}

	/**
	 * ҽ�ƿ���������
	 * 
	 * @param IDParm
	 *            TParm
	 * @param typeEKT
	 *            int
	 */
	private void onReadTxEkt(TParm IDParm, int typeEKT) {
		// TParm IDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// ͨ�����֤�Ų����Ƿ���ڴβ���
		if (IDParm.getValue("MR_NO").length() > 0) {
			setValue("MR_NO", IDParm.getValue("MR_NO")); // ���ڽ���������ʾ
			onQueryNO(false); // ִ�и�ֵ����
			setValue("NHI_NO", IDParm.getValue("NHI_NO")); // ==-============pangben
			// modify
			// 20110808
			tjINS = true; // ���ҽ��ʹ�ã��ж��Ƿ�ִ����ҽ�ƿ�����
			//callFunction("UI|PAY_WAY|setEnabled", false); // ֧����� 20180330 pengtianting ֧����ʽ�ſ�
		} else {
			this.messageBox("��ҽ�ƿ���Ч"); // ��������ʾ�����ϵ���Ϣ�����֤�š����ơ�ҽ����
			switch (typeEKT) {
			// �Ͼ�ҽ���� û�д˲�����Ϣʱִ�и�ֵ����
			case 1:
				this.setValue("IDNO", p3.getValue("identifyNO")); // ���֤��
				this.setValue("NHI_NO", p3.getValue("siNO")); // ҽ����
				this.setValue("PAT_NAME", p3.getValue("patientName").trim()); // ����
				break;
			// ̩��ҽ�ƿ�û�д˲�����Ϣʱִ�и�ֵ����
			case 2:

				// this.setValue("MR_NO",p3.getValue("MR_NO"));
				txEKT = true; // ̩��ҽ�ƿ�д�������ܿ�
				break;
			}
			// this.setValue("VISIT_CODE_C","N");
			callFunction("UI|MR_NO|setEnabled", false); // �����Ų��ɱ༭
			this.grabFocus("PAT_NAME");
			setValue("VISIT_CODE_C", "Y"); // Ĭ�ϳ���
		}
	}
	
	public void TXonEKTRecharge(TParm p){// add by kangy 
		if (null != p && p.getValue("MR_NO").length() > 0) {
			this.setValue("EKTMR_NO", p.getValue("MR_NO"));
			String EKTCARD_CODE = p.getData("CARD_NO").toString();
			this.setValue("EKTCARD_CODE", EKTCARD_CODE);
			this.setValue("CURRENT_BALANCE", p.getValue("CURRENT_BALANCE"));
			return;
		} else {
			this.messageBox(p.getErrText());
		}
		clearEKTValue();
	}
	
	//ҽ�ƿ���Ϣ��������
	public void readCard(){
		//kangy �ѿ���ԭ    start
		/*IccCardRWUtil DEV=new IccCardRWUtil();
		if(dev_flg){
			String cardType=DEV.getCardType();
			if("EKTCard".equals(cardType)){
				TXonEKTR();
			} else if("IDCard".equals(cardType)){
				TXonReadIdCardR();
			} else if("INSCard".equals(cardType)){
				TXonReadInsCardR();
			}
		}else*/
			//kangy �ѿ���ԭ    end	
			TXonEKTR();
	}
	//kangy �ѿ���ԭ      start  ҽ�ƿ���ֵ��ȡ���֤��ҽ������������
	//ҽ�ƿ���ֵ��ȡ���֤
	/*public void TXonReadIdCardR(){// add by kangy �Ҳ�ҽ�ƿ���ֵ��ȡ���֤
		TParm idParm= EKTReadCard.getInstance().readIDCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		if(idParm.getCount()==0){
			this.messageBox("�ò���û��ҽ�ƿ�����ִ���ƿ�����");
			return;
		}
		if(idParm.getCount()>1){
			p3 = (TParm) openDialog(
					"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", idParm);
		}else
			p3=idParm.getRow(0);
		TXonEKTRecharge(p3);
	}
	//ҽ�ƿ���ֵ��ȡҽ����
	public void TXonReadInsCardR(){//add by kangy �Ҳ�ҽ�ƿ���ֵ��ȡҽ����
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		//ҽԺ����@���÷���ʱ��@���
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// ���÷���ʱ��	
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+"1";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_TYPE","1");//����
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSReadInsCard.x", parm);
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		int returnType = insParm.getInt("RETURN_TYPE"); // ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			this.messageBox("��ȡҽ����ʧ��");
			return;
		}
		String sql="SELECT  B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E,SYS_PATINFO D"
	               + " WHERE "
	               //+" E.NHI_NO='6217250200000958634'"
	               +" E.NHI_NO='"+insParm.getParm("opbReadCardParm").getValue("CARD_NO").trim()+"' "
	               //+" D.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
	               +" AND E.MR_NO=A.MR_NO "
	               + " AND A.MR_NO=D.MR_NO "
	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
		TXonEKTRecharge(insParm.getRow(0));
	}*/
	//kangy �ѿ���ԭ     end
	/**
	 * ҽ�ƿ�����
	 */
	public void TXonEKTR() {
		
		//==start====add by kangy 20160912 ===��ȡҽ�ƿ�ˢ����һƱ��
		ektinvoice=new BilInvoice();
		 callFunction("UI|BIL_CODE|setValue", ektinvoice.initBilInvoice("EKT").getUpdateNo());
     	//==end====add by kangy 20160912
		 //kangy  �ѿ���ԭ     start
		/* TParm p=new TParm();
		if(dev_flg)
		 p = EKTReadCard.getInstance().readEKT();
		else
			p= EKTReadCard.getInstance().TXreadEKT();*/
		//kangy  �ѿ���ԭ     end
		TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		if (null != p && p.getValue("MR_NO").length() > 0) {
			// zhangp 20111231 �޸�ҽ�ƿ���
			this.setValue("EKTMR_NO", p.getValue("MR_NO"));
			String EKTCARD_CODE = p.getData("CARD_NO").toString();
			this.setValue("EKTCARD_CODE", EKTCARD_CODE);
			this.setValue("CURRENT_BALANCE", p.getValue("CURRENT_BALANCE"));
			return;
		} else {
			this.messageBox(p.getErrText());
		}
		// zhangp 20111227
		clearEKTValue();
	}

	/**
	 * ��ֵ����
	 */
	public void TXonEKTW() {
		//==start===add by kangy ==20160826====
		if(this.getValue("EKTMR_NO").toString().length()<=0){
			this.messageBox("����ִ�ж�������");
			return;
		}
		//==end===add by kangy ==20160826====
		
		if (this.getValueDouble("TOP_UPFEE") <= 0) {
			this.messageBox("��ֵ����ȷ");
			return;
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
			this.messageBox("֧����ʽ������Ϊ��ֵ");
			return;
		}
		String sql="SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE" 
	              +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C, SYS_PATINFO D WHERE " 
	              +" A.MR_NO='"+this.getValue("EKTMR_NO").toString()+"'"
	              +" AND D.MR_NO=A.MR_NO "
	              +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
			TParm p=new TParm(TJDODBTool.getInstance().select(sql));
			p=p.getRow(0);
		//TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		// zhangp 20111227
		pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		TParm parm = new TParm();
		parm.setData("SEQ", p.getValue("SEQ")); // ���
		parm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2)); // ���
		parm.setData("MR_NO", p.getValue("MR_NO")); // ������

		if (null != p && p.getValue("MR_NO").length() > 0) {
			// result.setData("CURRENT_BALANCE",
			// this.getValue("CURRENT_BALANCE"));
			//yanjing ע
//			TParm result = EKTIO.getInstance().TXwriteEKTATM(parm,
//					p.getValue("MR_NO"));
//			if (result.getErrCode() < 0) {
//				this.messageBox_("ҽ�ƿ���ֵ����ʧ��");
//				return;
//			}
			insbilPay(parm, p);
		} else {
			this.messageBox("��ҽ�ƿ���Ч");
		}
		clearEKTValue();
		// =====zhangp 20120403 start
		//onEKT();
	}

	/**
	 * ҽ�ƿ���ֵ����
	 * 
	 * @param parm
	 *            TParm
	 * @param p
	 *            TParm
	 */
	private void insbilPay(TParm parm, TParm p) {
		//==add by kangy===2016010
		ektinvoice=new BilInvoice();
		TParm checkparm=new TParm();
    	checkparm.setData("RECP_TYPE","EKT");
    	checkparm.setData("INV_NO",this.getValue("BIL_CODE"));
    	checkparm.setData("CASHIER_CODE",Operator.getID());
    	TParm res=BILInvoiceTool.getInstance().checkUpdateNo(checkparm);
    	if(res.getCount("RECP_TYPE")>0){
    		this.messageBox("��Ʊ����ʹ�ã�");
    		onClear();
    		return;
    	}
    	if(!compareInvno(ektinvoice.initBilInvoice("EKT").getStartInvno(),ektinvoice.initBilInvoice("EKT").getEndInvno(),this.getValue("BIL_CODE").toString())){
			this.messageBox("Ʊ�ų�����Χ");
			onClear();
			return;
		}
		// zhangp 20111227
		TParm result = new TParm();
		parmSum = new TParm();
		parmSum.setData("CARD_NO", pat.getMrNo() + p.getValue("SEQ"));
		parmSum.setData("CURRENT_BALANCE", parm.getValue("CURRENT_BALANCE"));
		parmSum.setData("CASE_NO", "none");
		parmSum.setData("NAME", pat.getName());
		parmSum.setData("MR_NO", pat.getMrNo());
		parmSum.setData("ID_NO", null != pat.getIdNo()
				&& pat.getIdNo().length() > 0 ? pat.getIdNo() : "none");
		parmSum.setData("OPT_USER", Operator.getID());
		parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parmSum.setData("OPT_TERM", Operator.getIP());
		parmSum.setData("FLG", false);
		parmSum.setData("ISSUERSN_CODE", "��ֵ"); // ����ԭ��
		parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); // ֧����ʽ
		parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); // ֧����ʽ����
		parmSum.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // ��ֵ���
		parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); // �Ա�
		parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); // ��ע
		parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); // Ʊ�ݺ�
		parmSum.setData("PRINT_NO",ektinvoice.initBilInvoice("EKT").getUpdateNo());//Ʊ�ݺ�====kangy
		 parmSum.setData("CREAT_USER", Operator.getID()); //ִ����Ա//=====yanjing
		 
		 TParm inFeeParm=new TParm();
			inFeeParm.setData("RECP_TYPE","EKT");
			inFeeParm.setData("INV_NO",ektinvoice.initBilInvoice("EKT").getUpdateNo());
			//inFeeParm.setData("RECEIPT_NO",bil_business_no);
			inFeeParm.setData("CASHIER_CODE",Operator.getID());
			inFeeParm.setData("AR_AMT",this.getValue("TOP_UPFEE"));
			inFeeParm.setData("CANCEL_FLG","0");
			inFeeParm.setData("CANCEL_USER","");
			inFeeParm.setData("CANCEL_DATE","");
			inFeeParm.setData("OPT_USER",Operator.getID().toString());
		    //infeeParm.setData("OPT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
			inFeeParm.setData("OPT_TERM",Operator.getIP().toString());
			inFeeParm.setData("ACCOUNT_FLG","");
			inFeeParm.setData("ACCOUNT_SEQ","");
			inFeeParm.setData("ACCOUNT_USER","");
			inFeeParm.setData("ACCOUNT_DATE","");
			inFeeParm.setData("PRINT_USER",Operator.getID());
			inFeeParm.setData("PRINT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
			inFeeParm.setData("ADM_TYPE","T");
			inFeeParm.setData("STATUS","0");
			parmSum.setData("infeeparm",inFeeParm.getData());
				
		    String updateno = StringTool.addString(ektinvoice.initBilInvoice("EKT").getUpdateNo());
         TParm updatanoParm=new TParm();
         BilInvoice bilInvo=ektinvoice.initBilInvoice("EKT");
         updatanoParm.setData("UPDATE_NO",updateno);
         updatanoParm.setData("RECP_TYPE","EKT");
         updatanoParm.setData("STATUS",bilInvo.getStatus());
         updatanoParm.setData("CASHIER_CODE",bilInvo.getCashierCode());
         updatanoParm.setData("START_INVNO",bilInvo.getStartInvno());
        parmSum.setData("updatanoparm",updatanoParm.getData());
	 
		// ��ϸ�����
		TParm feeParm = new TParm();
		feeParm.setData("ORIGINAL_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)); // ԭ���
		feeParm.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // ��ֵ���
		feeParm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
		// EKT_ACCNTDETAIL ����
		parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm)
				.getData());
		// zhangp 20120112 EKT_BIL_PAY ���ֶ�
		parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime()); // �ۿ�����ʱ��
		parmSum.setData("PROCEDURE_AMT", 0.00); // PROCEDURE_AMT
		// bil_pay ��ֵ������
		parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
		// �������
		result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"TXEKTonFee", parmSum); //
		callFunction("UI|tButton_5|setEnabled", false);//��ֵ��ť�����������������===pangben 2013-7-1
		if (result.getErrCode() < 0) {
			this.messageBox("ҽ�ƿ���ֵʧ��");
			callFunction("UI|tButton_5|setEnabled", true);//��ֵ��ť�����������������===pangben 2013-7-1
//			parm = EKTIO.getInstance().TXwriteEKTATM(p, p.getValue("MR_NO"));
//			if (parm.getErrCode() < 0) {
//				System.out.println("�س�ҽ�ƿ����ʧ��");
//			}
		} else {
			printBil = true;
			this.messageBox("ҽ�ƿ���ֵ�ɹ�");
			callFunction("UI|tButton_5|setEnabled", true);//��ֵ��ť�����������������===pangben 2013-7-1
			String bil_business_no = result.getValue("BIL_BUSINESS_NO"); // �վݺ�
			try {
				onPrint(bil_business_no, "");
			} catch (Exception e) {
				this.messageBox("��ӡ��������,��ִ�в�ӡ����");
				// TODO: handle exception
			}
		}
	}

	/**
	 * дҽ�ƿ�
	 */
	public void writeCard() {
	}

	/**
	 * ָ��������Ϣ
	 */
	public void queryConusmeByID() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("�޿�");

			return;
		}
		result = EktDriver.queryConusmeByID("1008250000000021");
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * ��֤
	 */
	public void unConsume() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("�޿�");

			return;
		}
		result = EktDriver.unConsume(1000, "sys", "1008250000000021",
				StringTool.getString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKTBarcode() {
		TParm printParm = new TParm();
		if ((ektCard != null || ektCard.length() != 0)
				&& this.getValueString("MR_NO") != null) {
			printParm.setData("mrNo", "TEXT", this.getValueString("MR_NO")); // ������
			printParm.setData("patName", "TEXT", this
					.getValueString("PAT_NAME")); // ��������
			printParm.setData("barCode", "TEXT", ektCard); // �����
			this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGEktCard.jhw",
					printParm);
		} else {
			this.messageBox("���ȶ�ҽ�ƿ�");
		}

	}

	/**
	 * ����SESSION combo���ż����ԣ������ص�ǰ��SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() {
		// Ϊ�˽����SESSION_CODE��ʾ�ż������𣬷���һ������ʾ��TEXTFIELD��
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}

	/**
	 * �忨 ===================pangben modify 20110808
	 */
	public void clearCard() {
		// EKTIO.getInstance().saveMRNO1(parm, this,true);
		if (null == p3) {
			this.messageBox("û����Ҫ�忨������");
			return;
		}

		p3.setData("identifyNO", this.getValue("IDNO"));
		p3.setData("siNO", this.getValue("NHI_NO"));
		p3.setData("patientName", this.getValue("PAT_NAME"));
		boolean temp = EKTIO.getInstance().writeEKT(p3, true);
		if (temp) {
			// �޸Ľ��˲���ҽ���������
			StringBuffer sql = new StringBuffer();
			sql
					.append("UPDATE SYS_PATINFO SET NHI_NO='',OPT_DATE=SYSDATE WHERE MR_NO='"
							+ this.getValueString("MR_NO").trim() + "'");
			TParm result = new TParm(TJDODBTool.getInstance().update(
					sql.toString()));
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				this.messageBox("�忨ʧ��");
				return;
			}
			this.messageBox("�忨�ɹ�");
		}
	}

	public void onClearRefresh() {
		this.initReg();
		clearValue(" PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL");
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
			this.setValue("ARRIVE_DATE", SystemTool.getInstance().getDate().toString().substring(0,16).replaceAll("-", "/"));
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		// ����Ĭ�Ϸ���ȼ�
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		// ��������
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
	}

	/**
	 * �ؼ��ɱ༭����
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setControlEnabled(boolean flg) {
		callFunction("UI|REGMETHOD_CODE|setEnabled", flg);
		callFunction("UI|ADM_DATE|setEnabled", flg);
		callFunction("UI|SESSION_CODE|setEnabled", flg);
		callFunction("UI|DEPT_CODE|setEnabled", flg);
		callFunction("UI|DR_CODE|setEnabled", flg);
		callFunction("UI|CLINICROOM_NO|setEnabled", flg);
		callFunction("UI|CLINICTYPE_CODE|setEnabled", flg);
		callFunction("UI|REG_FEE|setEnabled", flg);
		callFunction("UI|CLINIC_FEE|setEnabled", flg);
	}

	/**
	 * ��÷���
	 */
	public void showXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		feeShow = true;
		// String
		// mr_no=parm.getValue("TBR").trim().substring(1,parm.getValue("TBR").trim().indexOf("]"));

		// System.out.println("parm:::"+parm);
		// if(this.getValueString("MR_NO").trim().equals(mr_no)){
		if (null == parm)
			return;
		// feeIstrue = true;
		this.setValue("FeeY", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]"))); // �շ�
		this.setValue("REG_FEE", parm.getValue("GHF").substring(1,
				parm.getValue("GHF").indexOf("]"))); // �Һŷ�
		this.setValue("CLINIC_FEE", parm.getValue("ZLF").substring(1,
				parm.getValue("ZLF").indexOf("]"))); // ����
		this.setValue("FeeS", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]")));
		// }
	}

	/**
	 * �˹ҽ����ʾ ҽ�����Ļ�õļ۸���ʾ =====================pangben modify 20110815
	 * 
	 * @param caseNo
	 *            String
	 */
	private void unregFeeShow(String caseNo) {
		int feeunred = -1;
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT REG_FEE,CLINIC_FEE,AR_AMT FROM BIL_REG_RECP WHERE CASE_NO='"
						+ caseNo + "'"); // ����˹ҵĽ��
		// System.out.println("sql:::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		this.setValue("FeeY", result.getDouble("AR_AMT", 0) * feeunred); // �ܷ���
		this.setValue("REG_FEE", result.getDouble("REG_FEE", 0) * feeunred); // �Һ�
		this.setValue("CLINIC_FEE", result.getDouble("CLINIC_FEE", 0)
				* feeunred); // ����
		this.setValue("FeeS", result.getDouble("AR_AMT", 0) * feeunred); // ��ȡ����
	}

	/**
	 * ��������û�м��˵Ĳ��� flg �ж��Ƿ��Ǽ�������
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegNo(String caseNo, boolean flg) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm unRegParm = new TParm();

		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", optUser);

		// ��ѯ��ǰ�����Ƿ��������
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		
		//add by huangtt 20160815�жϸ������Ƿ��Ѵ�Ʊ
		if(unRegRecpParm.getValue("PRINT_NO", 0).length() == 0){
			this.messageBox("��ȥQҽָ���ص��Ʊ���ٽ����˺�");
			return;
		}
		
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		inInvRcpParm.setData("RECP_TYPE", "REG");// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			reSetReg(unRegParm, caseNo, flg, "onUnRegForEKT", "onUnReg", "Y");
		} else {
			this.messageBox("�Ѳ�������,�����˹�!");
			return;
		}
	}

	/**
	 * �����˹Ҳ���:BIL_STATUS=2 �Ѿ������˹Ҳ���
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegYes2(String caseNo, boolean flg) {
		onUnRegNo(caseNo, flg);
	}

	/**
	 * �����˹Ҳ���:BIL_STATUS=1 �ж��Ƿ�������ã����û�в�������ֱ����ӡ��޸Ĳ���BIL_REG_RECP ����Ѿ��������ò������˹�
	 * 
	 * @param caseNo
	 *            String
	 */
	private void onUnRegYes1(String caseNo) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", Operator.getID());
		TParm unRegParm = new TParm();
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		
		//add by huangtt 20160815�жϸ������Ƿ��Ѵ�Ʊ
		if(unRegRecpParm.getValue("PRINT_NO", 0).length() == 0){
			this.messageBox("��ȥQҽָ���ص��Ʊ���ٽ����˺�");
			return;
		}
		
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("RECP_TYPE", "REG");
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		unRegParm.setData("RECEIPT_NO", recpNo);
		unRegParm.setData("OPT_NAME", Operator.getName());
		// ��ѯ��ǰ�����Ƿ��������
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			// û��ִ�н���ķ��ò����˹�
			this.messageBox("û��ִ�н���,�����˷�");
			// ֱ����ӡ��޸Ĳ���BIL_REG_RECP
			// �ֽ��˹Ҷ���
			reSetReg(unRegParm, caseNo, false, "onUnRegForStatusEKT",
					"onUnRegStatus", "Y");
		} else {
			// �Ѳ�������
			this.messageBox("�Ѳ�������,�����˹�!");
		}

	}

	/**
	 * ��ҽ����
	 */
	public void readINSCard() {//modify by kangy 20170307
		/*String sql="SELECT distinct B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E WHERE E.NHI_NO='6217250200000958634' "
	               +" AND E.MR_NO=A.MR_NO "
	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
		p3=infoParm.getRow(0);
		p3.setData("READ_TYPE","INSCARD");*/
		String payWay = this.getValueString("PAY_WAY");// ֧����ʽ
		// ���ҽ��������
		tjReadINSCard(payWay);
	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @return boolean
	 */
	public boolean onSaveINSData() {
		boolean result = false;
		return result;
	}

	/**
	 * ���ҽ�ƿ���Ϣ
	 */
	public void ektOnClear() {
		clearValue("EKTMR_NO;EKTCARD_CODE;CURRENT_BALANCE;TOP_UPFEE;SUM_EKTFEE");
		//���ʱˢ��Ʊ��
		ektinvoice=invoice.initBilInvoice("EKT");
		initBilInvoice(ektinvoice.initBilInvoice("EKT"));
	}

	/**
	 * ����Һ��վݴ�ӡ
	 * 
	 * @param parm
	 *            TParm
	 * 
	 */
	private void onPrint(TParm parm) {
		// //����С��
		// sOTOT_Amt = ""+ TiMath.round( Double.parseDouble(sOTOT_Amt),2);

		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // ������������
		// ��ʾ��ʽ:����(����)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "��)"); // �ű�
		// ��ʾ��ʽ:�ű�(���)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		parm.setData("BALANCE_NAME", "TEXT", "�� ��"); // �������
		DecimalFormat df = new DecimalFormat("########0.00");
		// parm.setData("CURRENT_BALANCE", "TEXT", "�� "
		// + df.format(Double.parseDouble(ektNewSum == null
		// || "".equals(ektNewSum) ? "0.00" : ektNewSum))); // ҽ�ƿ�ʣ����
		parm.setData("CURRENT_BALANCE","TEXT","�� "
		+ df.format(Double.parseDouble(ektNewSum == null
		|| "".equals(ektNewSum) ? ""+ df.format((Double.parseDouble(getValueString("EKT_CURRENT_BALANCE").equals("") ? "0": getValueString("EKT_CURRENT_BALANCE"))- 
		parm.getDouble("TEXT","REGFEE") - parm.getDouble("TEXT","CLINICFEE"))): ektNewSum))); // ҽ�ƿ�ʣ����
		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_DEBIT", "TEXT", "ҽ��:"
					+ StringTool.round((parm.getDouble("INS_SUMAMT") - parm
							.getDouble("ACCOUNT_AMT_FORREG")), 2)); // ҽ��֧��
			parm.setData("PAY_CASH", "TEXT", "�ֽ�:"
					+ StringTool.round((parm.getDouble("TOTAL", "TEXT") - parm
							.getDouble("INS_SUMAMT")), 2)); // �ֽ�
			parm
					.setData("PAY_ACCOUNT", "TEXT", "�˻�:"
							+ StringTool.round(parm
									.getDouble("ACCOUNT_AMT_FORREG"), 2)); // �˻�
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ insParm.getParm("opbReadCardParm").getValue(
							"SP_PRESON_TYPE") + "'";// ҽ��������Ա�����ʾ
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // ����
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		// ===zhangp 20120313 start
		if ("1".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			// parm.setData("Cost_class", "TEXT", "��ͳ");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		} else if ("2".equals(insType) || "3".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			// parm.setData("Cost_class", "TEXT", "����");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		}
		// ===zhangp 20120313 end
        parm.setData("RECEIPT_NO", "TEXT", reg.getRegReceipt().getReceiptNo());//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				parm, true);
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("REGRECPPrint.jhw"),
                             IReportTool.getInstance().getReportParm("REGRECPPrint.class", parm), true);//����ϲ�modify by wanglong 20130730
	}

	/**
	 * ���ҽ������������
	 * 
	 * @param payWay
	 *            String
	 */
	private void tjReadINSCard(String payWay) {
		//yanjing ɾ����SERVICE_LEVEL����� 20130807
		clearValue("REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ ");
		initSchDay();
	/*	if (null == pat && !this.getValueBoolean("VISIT_CODE_C")) {
			this.messageBox("���Ȼ�ò�����Ϣ");
			return;
		}
*/
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		parm.setData("CARD_TYPE", 2); // �����������ͣ�1��������2���Һţ�3���շѣ�4��סԺ,5 :���صǼǣ�
		//ҽԺ����@���÷���ʱ��@���
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// ���÷���ʱ��	
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+"1";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_TYPE","1");//����
		//kangy �ѿ���ԭ    start
		/*insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSReadInsCard.x", parm);*/
		//kangy �ѿ���ԭ   end
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null == insParm) {
			this.setValue("PAY_WAY", payWay); // ֧����ʽ�޸�
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			this.messageBox("��ȡҽ����ʧ��");
			this.setValue("PAY_WAY", payWay); // ֧����ʽ�޸�
			return;
		}
		
	/*	String sql="SELECT  B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E,SYS_PATINFO D"
	               + " WHERE "
	               //+ " D.IDNO='6217250200000958634' "
	               +" E.NHI_NO='"+insParm.getParm("opbReadCardParm").getValue("CARD_NO").trim()+"' "
	              //+" D.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
	               +" AND E.MR_NO=A.MR_NO "
	               + " AND A.MR_NO=D.MR_NO "
	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));*/
		p3=insParm.getRow(0);
		p3.setData("READ_TYPE","INSCARD");
		EKT(p3);

		/*int crowdType = insParm.getInt("CROWD_TYPE"); // ҽ����ҽ��� 1.��ְ 2.�Ǿ�
		insType = insParm.getValue("INS_TYPE"); // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		// ============pangben 2012-4-8 ��ѯ�����Ƿ����ҽ��У��
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (this.getValueBoolean("VISIT_CODE_C")
				&& this.getValue("MR_NO").toString().trim().length() <= 0) {// ������ҽ������
			this.setValue("PAT_NAME", name);
			this.setValue("IDNO", opbReadCardParm.getValue("SID").trim());
			this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // ҽ������
			// ========pangben 2013-3-5 ��ӳ��ﲡ�˲�����Ϣ����
			setPatName1();
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
			// ÿ��ˢ����Ҫ����������ϵͳ���ݡ����صǼǽ���ʱ�䡱�뵱ǰʱ����бȽ�
			if (!insType.equals("1")) {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTH_DATE") ? opbReadCardParm.getValue(
						"BIRTH_DATE").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(6, 8) : "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX_CODE"));
			} else {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTHDAY") ? opbReadCardParm.getValue(
						"BIRTHDAY").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(6,
								opbReadCardParm.getValue("BIRTHDAY").length())
						: "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX"));
			}
			return;
		}
		if (insPresonParm.getErrCode() < 0) {
			this.messageBox("��ò�����Ϣʧ��");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("��ҽ������������ҽ�ƿ���Ϣ,\nҽ����Ϣ:���֤����:"
					+ opbReadCardParm.getValue("SID") + "\nҽ����������:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		} else if (insPresonParm.getCount("MR_NO") > 1) {
			int flg = -1;
			if (this.getValue("MR_NO").toString().length() > 0) {
				for (int i = 0; i < insPresonParm.getCount("MR_NO"); i++) {
					if (insPresonParm.getValue("MR_NO", i).equals(
							this.getValue("MR_NO"))) {
						flg = i;
						break;
					}
				}
				if (flg == -1) {
					this.messageBox("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
			// onPatName();
		}
		// ===================pangben 2012-04-09ҽ���ܿ����
		// ÿ��ˢ����Ҫ����������ϵͳ���ݡ����صǼǽ���ʱ�䡱�뵱ǰʱ����бȽ�
		if (!insType.equals("1")) {

			// �������صǼ���Ч����X��X��X�գ����ڴ�ʱ��ǰ2�����ڵ����򲡼������İ������϶�
			String mtEndDate = opbReadCardParm.getValue("MT_END_DATE");// ���صǼǽ���ʱ��
			this.messageBox("�������صǼ���Ч����" + mtEndDate
					+ "�����ڴ�ʱ��ǰ2�����ڵ����򲡼������İ������϶�");
		}
		// ============pangben 2012-4-9 stop
		// �ж���Ⱥ���
		// ������ۿ۶��ո�ֵ
		// 11����ְ��ͨ ,11:ҽ����\ 12����ְ����,21:ҽ���� \13����ְ����,51:ҽ����
		// 21:�Ǿ������� ,11:ҽ����\22:�Ǿ�ѧ����ͯ 12:ҽ���� \23���Ǿӳ������,13:ҽ����
		this.setValue("REG_CTZ1", insParm.getValue("CTZ_CODE"));
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// ��������
		combo_ctz.setNhiFlg(crowdType + "");
		combo_ctz.onQuery();
		insFlg = true; // ҽ������ȡ�ɹ�
		callFunction("UI|REG_CTZ1|setEnabled", true); // ������
		callFunction("UI|PAY_WAY|setEnabled", false); // ֧�����
		this.setValue("PAY_WAY", "PAY_INS_CARD"); // ֧����ʽ�޸�
		this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // ҽ������
		this.grabFocus("FeeS");*/
	}

//	/**
//	 * ̩��ҽԺҽ�����������
//	 * 
//	 * @param parm
//	 *            TParm
//	 * @param caseNo
//	 *            String
//	 * @return TParm
//	 */
//	private TParm TXsaveINSCard(TParm parm, String caseNo) {
//		// û�л��ҽ�ƿ���Ϣ �ж��Ƿ�ִ���ֽ��շ�
//		if (!tjINS && !insFlg) {
//			if (this.messageBox("��ʾ", "û�л��ҽ�ƿ���Ϣ,ִ���ֽ��շ��Ƿ����", 2) != 0) {
//				return null;
//			}
//		}
//		if (tjINS) { // ҽ�ƿ�����
//			if (p3.getDouble("CURRENT_BALANCE") < this.getValueDouble("FeeY")) {
//				this.messageBox("ҽ�ƿ�����,���ֵ");
//				return null;
//			}
//		}
//		TParm result = new TParm();
//		insParm.setData("REG_PARM", parm.getData()); // ҽ����Ϣ
//		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // ���Ҵ���
//		insParm.setData("MR_NO", pat.getMrNo()); // ������
//
//		reg.setCaseNo(caseNo);
//		insParm.setData("RECP_TYPE", "REG"); // ���ͣ�REG / OPB
//		insParm.setData("CASE_NO", reg.caseNo());
//		insParm.setData("REG_TYPE", "1"); // �Һű�־:1 �Һ�0 �ǹҺ�
//		insParm.setData("OPT_USER", Operator.getID());
//		insParm.setData("OPT_TERM", Operator.getIP());
//		insParm.setData("DR_CODE", this.getValue("DR_CODE"));// ҽ������
//		// insParm.setData("PAY_KIND", "11");// 4 ֧�����:11���ҩ��21סԺ//֧�����12��
//		if (this.getValueString("ERD_LEVEL").length() > 0) {
//			insParm.setData("EREG_FLG", "1"); // ����
//		} else {
//			insParm.setData("EREG_FLG", "0"); // ��ͨ
//		}
//
//		insParm.setData("PRINT_NO", this.getValue("NEXT_NO")); // Ʊ��
//		insParm.setData("QUE_NO", reg.getQueNo());
//
//		TParm returnParm = insExeFee(true);
//		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
//			return null;
//		}
//		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.ʧ�� 1. �ɹ�
//		if (returnType == 0 || returnType == -1) { // ȡ������
//			return null;
//		}
//
//		insParm.setData("comminuteFeeParm", returnParm.getParm(
//				"comminuteFeeParm").getData()); // ���÷ָ�����
//		insParm.setData("settlementDetailsParm", returnParm.getParm(
//				"settlementDetailsParm").getData()); // ���ý���
//
//		// System.out.println("insParm:::::::"+insParm);
//		result = INSTJReg.getInstance().insCommFunction(insParm.getData());
//
//		if (result.getErrCode() < 0) {
//			err(result.getErrCode() + " " + result.getErrText());
//			// this.messageBox("ҽ��ִ�в���ʧ��");
//			return result;
//		}
//		// System.out.println("ҽ����������:" + insParm);
//		// boolean messageFlg = false; // ҽ��������� ִ���ֽ��տ�
//		result.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
//		result.setData("ACCOUNT_AMT_FORREG", returnParm
//				.getDouble("ACCOUNT_AMT_FORREG")); // �˻����
//		insParm.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
//		if (tjINS) { // ҽ�ƿ�����
//		// TParm insExeParm = insExe(returnParm.getDouble("ACCOUNT_AMT"), p3,
//		// reg.caseNo(), "REG", 9);
//		// if (insExeParm.getErrCode() < 0) {
//		// return insExeParm;
//		// }
//			// ִ��ҽ�ƿ��ۿ��������Ҫ�ж�ҽ�������ҽ�ƿ����
//			if (!onTXEktSave("Y", result)) {
//				result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
//						"deleteOldData", insParm);
//				if (result.getErrCode() < 0) {
//					err(result.getErrCode() + " " + result.getErrText());
//					result.setErr(-1, "ҽ����ִ�в���ʧ��");
//					// return result;
//				}
//				result.setErr(-1, "ҽ�ƿ�ִ�в���ʧ��");
//				return result;
//			}
//			// result = new TParm();// ִ���������REG_PATADM
//		}
//		return result;
//	}

	/**
	 * ҽ����ִ�з�����ʾ���� flg �Ƿ�ִ���˹� false�� ִ���˹� true�� �����̲���
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm insExeFee(boolean flg) {
		TParm insFeeParm = new TParm();
		if (flg) {
			//��õ�ǰʱ��
			String sysdate = StringTool.getString(SystemTool.
					getInstance().getDate(), "yyyyMMdd");
			insParm.setData("ADM_DATE", sysdate);
			insFeeParm.setData("insParm", insParm.getData()); // ҽ����Ϣ
			insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // ҽ����ҽ���
		} else {
			insFeeParm.setData("CASE_NO", reSetCaseNo); // �˹�ʹ��
			insFeeParm.setData("INS_TYPE", insType); // �˹�ʹ��
			insFeeParm.setData("RECP_TYPE", "REG"); // �˹�ʹ��
			insFeeParm.setData("CONFIRM_NO", confirmNo); // �˹�ʹ��
		}
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // ������

		insFeeParm.setData("FeeY", this.getValueDouble("FeeY")); // Ӧ�ս��
		insFeeParm.setData("PAY_TYPE", tjINS); // ֧����ʽ
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // �������
		insFeeParm.setData("FEE_FLG", flg); // �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
		TParm returnParm = new TParm();
		if (flg) { // ������
			// returnParm=INSTJReg.getInstance().onInsFee(insFeeParm, this);
			returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
					insFeeParm);
			if (returnParm == null
					|| null == returnParm.getValue("RETURN_TYPE")
					|| returnParm.getInt("RETURN_TYPE") == 0) {
				return null;
			}
		} else {
			// �˷�����
			TParm returnIns = reSetExeFee(insFeeParm);
			if (null == returnIns) {
				return null;
			} else {
				double accountAmt = 0.00;// ҽ�����
				if (returnIns.getValue("INS_CROWD_TYPE").equals("1")) {// ��ְ
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - returnIns
							.getDouble("UNACCOUNT_PAY_AMT")), 2);
					this.messageBox("ҽ���˷ѽ��:"
							+ accountAmt
							+ " �ֽ��˷ѽ��:"
							+ StringTool.round(returnIns
									.getDouble("UNACCOUNT_PAY_AMT"), 2));

				} else if (returnIns.getValue("INS_CROWD_TYPE").equals("2")) {// �Ǿ�
					double payAmt = returnIns.getDouble("TOT_AMT")
							- returnIns.getDouble("TOTAL_AGENT_AMT")
							- returnIns.getDouble("FLG_AGENT_AMT")
							- returnIns.getDouble("ARMY_AI_AMT")
							- returnIns.getDouble("ILLNESS_SUBSIDY_AMT");// �ֽ���
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - payAmt), 2);

					this.messageBox("ҽ���˷ѽ��:" + accountAmt + " �ֽ��˷ѽ��:"
							+ StringTool.round(payAmt, 2));
				}

				returnParm.setData("RETURN_TYPE", 1); // ִ�гɹ�
				returnParm.setData("ACCOUNT_AMT", accountAmt);// ҽ�����
			}

		}
		return returnParm;
	}

	/**
	 * ҽ��ִ���˷Ѳ���
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}

	/**
	 * ���˲�����֧����ʽ���ü���
	 */
	public void contractSelect() {

		if (this.getValue("CONTRACT_CODE").toString().length() > 0) {
			this.setValue("PAY_WAY", "PAY_DEBIT"); // ����

		} else {
			this.setValue("PAY_WAY", "PAY_CASH"); // �ֽ�
		}
	}

	/**
	 * �����ҽ�ƿ� zhangp 20121216
	 */
	public void ektCard() {
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ekt\\EKTWorkUI.x", sendParm);
	}

	/**
	 * ҽ�ƿ���ϸ���������==============zhangp 20111227
	 * 
	 * @param p
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBusinessParm(TParm p, TParm feeParm) {
		// ��ϸ������
		TParm bilParm = new TParm();
		bilParm.setData("BUSINESS_SEQ", 0);
		bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
		bilParm.setData("MR_NO", pat.getMrNo());
		bilParm.setData("CASE_NO", "none");
		bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("SEQ_NO", 0);
		bilParm.setData("CHARGE_FLG", "3"); // ״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
		bilParm.setData("ORIGINAL_BALANCE", feeParm
				.getValue("ORIGINAL_BALANCE")); // �շ�ǰ���
		bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
		bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
		bilParm.setData("CASHIER_CODE", Operator.getID());
		bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
		// 1������ִ�����
		// 2��˫��ȷ�����
		bilParm.setData("BUSINESS_STATUS", "1");
		// 1��δ����
		// 2�����˳ɹ�
		// 3������ʧ��
		bilParm.setData("ACCNT_STATUS", "1");
		bilParm.setData("ACCNT_USER", new TNull(String.class));
		bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
		bilParm.setData("OPT_USER", Operator.getID());
		bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		bilParm.setData("OPT_TERM", Operator.getIP());
		// p.setData("bilParm",bilParm.getData());
		return bilParm;
	}

	/**
	 * ��ֵ��������ݲ���==============zhangp 20111227
	 * 
	 * @param parm
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBillParm(TParm parm, TParm feeParm) {
		TParm billParm = new TParm();
		billParm.setData("CARD_NO", parm.getValue("CARD_NO")); // ����
		billParm.setData("CURT_CARDSEQ", 0); // ���
		billParm.setData("ACCNT_TYPE", "4"); // ��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
		billParm.setData("MR_NO", parm.getValue("MR_NO")); // ������
		billParm.setData("ID_NO", parm.getValue("ID_NO")); // ���֤��
		billParm.setData("NAME", parm.getValue("NAME")); // ��������
		billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); // ��ֵ���
		billParm.setData("CREAT_USER", Operator.getID()); // ִ����Ա
		billParm.setData("OPT_USER", Operator.getID()); // ������Ա
		billParm.setData("OPT_TERM", Operator.getIP()); // ִ��ip
		billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); // ֧����ʽ
		// 20120112 zhangp ���ֶ�
		billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
		billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
		return billParm;
	}

	/**
	 * ��ֵ��ӡ==============zhangp 20111227
	 * 
	 * @param bil_business_no
	 *            String
	 * @param copy
	 *            String
	 */
	private void onPrint(String bil_business_no, String copy) {
		if (!printBil) {
			this.messageBox("����ҽ�ƿ���ֵ�����ſ��Դ�ӡ");
			return;
		}
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", (Operator.getRegion() != null
				&& Operator.getRegion().length() > 0 ? Operator
				.getHospitalCHNFullName() : "����ҽԺ"));
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // ������
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // ����
		parm.setData("GATHER_TYPE", "TEXT", parmSum
				.getValue("GATHER_TYPE_NAME")); // �տʽ
		parm.setData("AMT", "TEXT", StringTool.round(parmSum
				.getDouble("BUSINESS_AMT"), 2)); // ���
		// ====zhangp 20120525 start
		// parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�տʽ
		parm.setData("GATHER_NAME", "TEXT", ""); // �տʽ
		// ====zhangp 20120525 end
		parm.setData("TYPE", "TEXT", "Ԥ ��"); // �ı�Ԥ�ս��
		parm.setData("SEX_TYPE", "TEXT", pat.getSexCode().equals("1") ? "��"
				: "Ů"); // �Ա�
		parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(
				parmSum.getDouble("BUSINESS_AMT"))); // ��д���
		parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); // ̨ͷһ
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyyMMdd"); // ������
		String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "hhmmss"); // ʱ����
		parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); // ̨ͷ��
		yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); // ʱ����
		parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); // ��ע
		parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); // Ʊ�ݺ�
		parm.setData("PRINT_NO", "TEXT", parmSum.getValue("PRINT_NO")); //Ʊ�ݺ�
		if (null == bil_business_no)
			bil_business_no = EKTTool.getInstance().getBillBusinessNo(); // ��ӡ����
		parm.setData("ONFEE_NO", "TEXT", bil_business_no); // �վݺ�
		parm.setData("PRINT_DATE", "TEXT", yMd); // ��ӡʱ��
		parm.setData("DATE", "TEXT", yMd + "    " + hms); // ����
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		parm.setData("COPY", "TEXT", copy); // �տ���
		// ===zhangp 20120525 start
		parm.setData("O", "TEXT", "");
		// this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw",
		// parm,true);
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm,
				true);
		// ===zhangp 20120525 end
	}

	/**
	 * ��ֵ�ı���س��¼�======zhangp 20111227
	 */
	public void addFee() {
		if (this.getValueDouble("TOP_UPFEE") < 0) {
			this.messageBox("��ֵ������Ϊ��ֵ");
			return;
		}
		this.setValue("SUM_EKTFEE", this.getValueDouble("TOP_UPFEE")
				+ this.getValueDouble("CURRENT_BALANCE"));
	}

	/**
	 * ���ҽ�ƿ�ҳǩ============zhangp 20111227
	 */
	public void clearEKTValue() {
		ektOnClear();
		// clearValue("DESCRIPTION;TOP_UPFEE;SUM_EKTFEE");
	}

	/**
	 * ɾ��ҽ����;״̬
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean deleteInsRun(String caseNo, String exeType) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "ҽ����ִ�в���ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * �޸�ҽ��Ʊ�ݺ�
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean updateINSPrintNo(String caseNo, String exeType) {
		TParm parm = new TParm();
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		parm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO"));
		parm.setData("PRINT_NO", insParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", insParm.getValue("RECP_TYPE"));
		TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
				"updateINSPrintNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "ҽ����ִ�в���ʧ��");
			return false;
		}
		return true;
	}
	/**
	 * ����ҽ�����е�RECEIPT_NO
	 * 
	 */
	public boolean updateReceiptNo(String caseNo,String recpType) {
		 String sql = " SELECT RECEIPT_NO FROM BIL_REG_RECP " +
		 		      " WHERE CASE_NO = '"+caseNo+"'";
	     TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	     String receiptNo = parm.getValue("RECEIPT_NO", 0);
	     String confirmNo = insParm.getValue("CONFIRM_NO");
	     String sql1 = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
					   " WHERE CASE_NO ='" + caseNo + "'" +
					   " AND CONFIRM_NO = '" + confirmNo + "'" +
					   " AND RECP_TYPE = '" + recpType + "'";
		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql1));
		if (updateParm.getErrCode() < 0) {
			err(updateParm.getErrCode() + " " + updateParm.getErrText());
			updateParm.setErr(-1, "����ҽ����ʧ��");
			return false;
		}
		return true;	
	}
	/**
	 * ҽ��֧����ֵ
	 * 
	 * @param result
	 *            ҽ�����صĲ���
	 * @param regFeeParm
	 *            ҽ���ָ��ҽ���Ľ��
	 * @return ����ҽ��֧���ܽ��
	 */
	public double tjInsPay(TParm result, TParm regFeeParm) {
		reg.getRegReceipt().setPayBankCard(0.00);
		reg.getRegReceipt().setPayCheck(0.00);
		reg.getRegReceipt().setPayDebit(0.00);
		reg.getRegReceipt().setPayInsCard(result.getDouble("INS_SUMAMT")); // ҽ�����
		double ins_amt = result.getDouble("INS_SUMAMT");
		if (!tjINS) { // �ֽ��շ�
			reg.getRegReceipt().setPayCash(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT"));
			reg.getRegReceipt().setPayMedicalCard(0.00); // ҽ�ƿ����
		} else { // ҽ�ƿ��շ�
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT")); // ҽ�ƿ����
		}
		TParm comminuteFeeParm = result.getParm("comminuteFeeParm"); // ���÷ָ�
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			for (int j = 0; j < comminuteFeeParm.getCount("ORDER_CODE"); j++) {
				if (regFeeParm.getValue("ORDER_CODE", i).equals(
						comminuteFeeParm.getValue("ORDER_CODE", j))) {
					if (comminuteFeeParm.getValue("RECEIPT_TYPE", j).equals(
							"REG_FEE")) {
						reg.getRegReceipt().setRegFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 12�ۿ�ǰ�Һŷ�(REG_RECEIPT)
						reg.getRegReceipt().setRegFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					} else {
						reg.getRegReceipt().setClinicFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 14�ۿ�ǰ����(REG_RECEIPT)
						reg.getRegReceipt().setClinicFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					}
					break;
				}
			}
		}
		return ins_amt;
	}

	/**
	 * �˹Ҳ���ʹ��
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 * @param ektName
	 *            String
	 * @param cashName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetReg(TParm unRegParm, String caseNo, boolean flg,
			String ektName, String cashName, String stutsFlg) {
		// TParm reSetInsParm=new TParm();
		if (!reSetInsSave(unRegParm.getValue("INV_NO")))
			return;
		if ("PAY_MEDICAL_CARD".equals(this.getValueString("PAY_WAY"))) {
			// ��ӽ��п��˹ҷ�֧====pangben 2012-12-07
			TParm ccbParm = checkCcbReSet(caseNo);// �ж��Ƿ�ִ�н��п�����
			if (null == ccbParm || ccbParm.getCount() <= 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				// ���в���
				// TParm ccbp=checkCcbReSet(caseNo);
				unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));// ���н��
				reSetCcbSave(unRegParm, caseNo, stutsFlg);
			}
		} else if ("PAY_CASH".equals(this.getValueString("PAY_WAY"))) { // �ֽ�
			reSetCashSave(unRegParm, stutsFlg, flg, cashName);
		} else if ("PAY_INS_CARD".equals(this.getValueString("PAY_WAY"))) { // ҽ����
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				TParm ccbParm = checkCcbReSet(caseNo);// �ж��Ƿ�ִ�н��п�����
				if (null == ccbParm || ccbParm.getCount() <= 0)
					reSetCashSave(unRegParm, stutsFlg, flg, cashName);
				else {
					// ���в���
					unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));
					reSetCcbSave(unRegParm, caseNo, stutsFlg);
				}
			}
		}
		// ҽ��ɾ����;״̬
		if (null != confirmNo && confirmNo.length() > 0) {
			if (!deleteInsRun(reSetCaseNo, "REGT"))
				return;
			//����ҽ����RECEIPT_NO(�˹���һ��)
			if (!updateUnRegReceiptNo(reSetCaseNo, "REGT"))
				return;
		}


	}
	/**
	 * ����ҽ�����е�RECEIPT_NO(�˹���һ��)
	 * 
	 */
	public boolean updateUnRegReceiptNo(String caseNo,String recpType) {
		 String sql = " SELECT RECEIPT_NO FROM BIL_REG_RECP " +
		 		      " WHERE CASE_NO = '"+caseNo+"'" +
		 		      " AND AR_AMT < 0";
	     TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	     String receiptNo = parm.getValue("RECEIPT_NO", 0);
	     String sql1 = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
					   " WHERE CASE_NO ='" + caseNo + "'" +
					   " AND RECP_TYPE = '" + recpType + "'";
		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql1));
		if (updateParm.getErrCode() < 0) {
			err(updateParm.getErrCode() + " " + updateParm.getErrText());
			updateParm.setErr(-1, "����ҽ����ʧ��");
			return false;
		}
		return true;	
	}
	/**
	 * ҽ���˹Ҳ���
	 * 
	 * @param invNo
	 *            String
	 * @return boolean
	 */
	private boolean reSetInsSave(String invNo) {
		TParm reSetInsParm = new TParm();
		if (null != confirmNo && confirmNo.length() > 0) {
			// ҽ�����˷� ��Ҫ�޸�ҽ�ƿ�����
			if (null == reSetCaseNo && reSetCaseNo.length() <= 0) {
				return false;
			}
			TParm tredeParm = new TParm(); // ��ѯ�˴��˹Ҳ����Ƿ���ҽ�ƿ��˹�
			tredeParm.setData("CASE_NO", reSetCaseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // ����
			tredeParm.setData("STATE", "1"); // ״̬�� 0 �ۿ� 1 �ۿ��Ʊ 2�˹� 3 ����
			TParm reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // ҽ�ƿ��˷Ѳ�ѯ
			if (reSetEktParm.getErrCode() < 0) {
				return false;
			}
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {// ҽ�ƿ��˹Ҳ���
				if (p3 == null || null == p3.getValue("MR_NO")
						|| p3.getValue("MR_NO").length() <= 0) {
					this.messageBox("ҽ�ƿ��˷�,��ִ�ж�������");
					return false;
				}
			}
			TParm parm = insExeFee(false);
			int returnType = parm.getInt("RETURN_TYPE");
			if (returnType == 0 || returnType == -1) { // ȡ��
				return false;
			}
			reSetInsParm.setData("CASE_NO", reSetCaseNo); // �����
			reSetInsParm.setData("CONFIRM_NO", confirmNo); // ҽ�������
			reSetInsParm.setData("INS_TYPE", insType); // ҽ�������
			reSetInsParm.setData("RECP_TYPE", "REG"); // �շ�����
			reSetInsParm.setData("UNRECP_TYPE", "REGT"); // �˷�����
			reSetInsParm.setData("OPT_USER", Operator.getID()); // id
			reSetInsParm.setData("OPT_TERM", Operator.getIP()); // ip
			reSetInsParm.setData("REGION_CODE", regionParm
					.getValue("NHI_NO", 0)); // ҽ���������
			reSetInsParm.setData("PAT_TYPE", this.getValue("REG_CTZ1")); // ���
			reSetInsParm.setData("INV_NO", invNo); // Ʊ�ݺ�
			// System.out.println("reSetInsParm::::::" + reSetInsParm);
			TParm result = INSTJReg.getInstance().insResetCommFunction(
					reSetInsParm.getData());
			if (result.getErrCode() < 0) {
				this.messageBox("ҽ���˹�ʧ��");
				return false;
			}
		}
		return true;
	}

	/**
	 * У���Ƿ��п��˹Ҳ���
	 * 
	 * @return
	 */
	private TParm checkCcbReSet(String reSetCaseNo) {
		String sql = "SELECT CASE_NO,SUM(AMT) AS AMT FROM EKT_CCB_TRADE WHERE CASE_NO='"
				+ reSetCaseNo + "' AND BUSINESS_TYPE='REG' group by case_no";
		TParm reSetParm = new TParm(TJDODBTool.getInstance().select(sql));
		return reSetParm;
	}

	/**
	 * ���п��˷Ѳ��� =====pangben 2012-12-07
	 */
	private void reSetCcbSave(TParm unRegParm, String caseNo, String stutsFlg) {
		// ���ý��нӿ��˷�����
		unRegParm.setData("NHI_NO", regionParm.getValue("NHI_NO", 0));
		unRegParm.setData("RECEIPT_NO", unRegParm.getParm("RECP_PARM")
				.getValue("RECEIPT_NO", 0));
		// ���нӿڲ���
		// TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ccb.CCBServerAction", "getCcbRe", unRegParm);
		if (result.getErrCode() < 0) {
			this.messageBox("���нӿڵ��ó�������,����ϵ��Ϣ����");
			return;
		}
		unRegParm.setData("FLG", "N");
		result.setData("OPT_TERM", Operator.getIP());
		result.setData("OPT_USER", Operator.getID());
		result.setData("BUSINESS_TYPE", "REGT");
		result = REGCcbReTool.getInstance().saveEktCcbTrede(result);
		if (result.getErrCode() < 0) {
			this.messageBox("�����˹�ʧ��");
			return;
		}
		result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onUnReg", unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// �����Ŷӽк�
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("�к�ʧ��");
		}
		if (stutsFlg.equals("Y")) {
			this.messageBox("���п��˹ҳɹ�!Ʊ�ݺ�:" + unRegParm.getValue("INV_NO"));
		}
	}

	/**
	 * ҽ�ƿ��˷Ѳ���
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param ektName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetEktSave(TParm unRegParm, String caseNo, String ektName,
			String stutsFlg) {
		// ҽ�ƿ�
		TParm result = new TParm();
		if (EKTIO.getInstance().ektSwitch()) {
			
			//modify by huangtt 20160914  start �˹��޸�
			
			TParm orderParm = onOpenCardR(caseNo);
			reg = new Reg();
			reg.setCaseNo(caseNo);
			EktParam ektParam = new EktParam();
			ektParam.setType("REG");
			ektParam.setRegPatAdmControl(this);
			ektParam.setReg(reg);
			ektParam.setPat(pat);
			ektParam.setOrderParm(orderParm);
			
			EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
			try {
				
				//�������������շѽ��棬ִ���շ�
				ektTradeContext.openClientR(ektParam);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				return ;
			}
			
			
			
//			int type = 0;
//			if (result == null) {
//				this.messageBox("E0115");
//				return;
//			}
//			type = result.getInt("OP_TYPE");
//			if (type == 3 || type == -1) {
//				this.messageBox("E0115");
//				return;
//			}
//			if (type == 2) {
//				return;
//			}
//			tradeNoT = result.getValue("TRADE_NO");
			
			unRegParm.setData("EKT_SQL",reg.getEktSql());
			
			//modify by huangtt 20160914  end �˹��޸�
			
			
			
			
			unRegParm.setData("TRADE_NO", tradeNoT);
			// ҽ�ƿ��˹�
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					ektName, unRegParm);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				EKTIO.getInstance().unConsume(tradeNoT, this);
				return;
			}
			if (stutsFlg.equals("Y")) {
				this.messageBox("�˹ҳɹ�!Ʊ�ݺ�:" + unRegParm.getValue("INV_NO"));
			}
			// �����Ŷӽк�
			if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
				this.messageBox("�к�ʧ��");
			}

		}
	}

	/**
	 * �ֽ��˷Ѳ���
	 * 
	 * @param unRegParm
	 *            �˹�����
	 * @param flg
	 *            �ֽ��˹ҹܿ�
	 * @param cashName
	 *            �ֽ����ACTION��ӿڷ�������
	 * @param stutsFlg
	 *            �ж��Ƿ�ִ����ʾ��Ϣ��
	 */
	private void reSetCashSave(TParm unRegParm, String stutsFlg, boolean flg,
			String cashName) {
		TParm result = new TParm();
		if (stutsFlg.equals("Y")) {
			unRegParm.setData("FLG", flg);
		}
		// �ֽ��˹Ҷ���
		result = TIOM_AppServer.executeAction("action.reg.REGAction", cashName,
				unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// �����Ŷӽк�
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("�к�ʧ��");
		}
		if (stutsFlg.equals("Y")) {
			this.messageBox("�˹ҳɹ�!Ʊ�ݺ�:" + unRegParm.getValue("INV_NO"));
		}
	}

	/**
	 * VIP ������Һ�������ͬ
	 */
	public void onDateReg() {
		this.setValue("VIP_ADM_DATE", this.getValue("ADM_DATE"));
		onQueryVipDrTable();
	}

	public void onPast() {
		if (this.getValueString("BIRTH_DATE").length() > 0
				&& this.getValueString("BIRTH_DATE") != null)
			this.grabFocus("SEX_CODE");
	}

	/**
	 * ����QUE_NO ����� ����غ����� ��ԭ�������һ�������ֳ����ȱ��������߼� ===============pangben
	 * 2012-6-18
	 */
	private boolean onSaveQueNo(TParm regParm) {
		// ����ű�
		TParm result = null;
		if (regParm.getBoolean("VIP_FLG")) {
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onSaveQueNo", regParm);
		} else {
			// ��ͨ��
			result = SchDayTool.getInstance().updatequeno(regParm);
		}
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * ����������ռ�� ====zhangp 20120629
	 * 
	 * @param temp
	 */
	private void queryQueNo(TParm temp) {
		String vipSql = "SELECT MIN(QUE_NO) QUE_NO FROM REG_CLINICQUE "
				+ "WHERE ADM_TYPE='" + admType + "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
				+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
				+ temp.getValue("CLINICROOM_NO") + "' AND  QUE_STATUS='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (result.getErrCode() < 0) {
			messageBox("���ʧ��");
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("�޾����");
			return;
		}
		int queNo = result.getInt("QUE_NO", 0);
		//add by huangtt 20160621 start
		if(queNo == 0){
			messageBox("�޾����");
			return;
		}
		//add by huangtt 20160621 end
		reg.setQueNo(queNo);
	}
	
    /**
     * �����ӡ
     */
    public void onWrist() {//wanglong add 20150413
        if (this.getValueString("MR_NO").length() == 0 && pat == null && reg.getPat() == null) {
            return;
        }
        String mrNo = "";
        String patName = "";
        String sex = "";
        String birthDay = "";
        if (pat != null) {
            mrNo = pat.getMrNo();
            patName = pat.getName();
            sex = pat.getSexString();
            birthDay = StringTool.getString(pat.getBirthday(), "yyyy/MM/dd");
        } else if (reg.getPat() != null) {
            mrNo = reg.getPat().getMrNo();
            patName = reg.getPat().getName();
            sex = reg.getPat().getSexString();
            birthDay = StringTool.getString(reg.getPat().getBirthday(), "yyyy/MM/dd");
        }
        TParm print = new TParm();
        print.setData("Barcode", "TEXT", mrNo);
        print.setData("PatName", "TEXT", patName);
        print.setData("Sex", "TEXT", sex);
        print.setData("BirthDay", "TEXT", birthDay);
        this.openPrintDialog("%ROOT%\\config\\prt\\ERD\\ERDWrist", print);
    }
    
    /**
     * ���ó�����  add by huangtt 20151020
     * @param mrNo
     */
	public void setVisitCodeFC(String mrNo) {
		String sql1 = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"
				+ mrNo + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (selParm.getInt("SUM", 0) > 0) {
			this.setValue("VISIT_CODE_F", true);
		} else {
			this.setValue("VISIT_CODE_C", true);
		}
	}
	
	public void onErd(){
		String triageNo = this.getValueString("TRIAGE_NO");
		String sql = "SELECT LEVEL_CODE FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' AND MR_NO IS NULL	";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			this.messageBox("�ü��˺����ã�����������");
			this.setValue("TRIAGE_NO", "");
			return;
		}
		this.setValue("ERD_LEVEL", parm.getValue("LEVEL_CODE", 0));
	}
	
	/**
	 * ��ʼ��Ʊ��
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// ��˿�����
		if (bilInvoice == null) {
			this.messageBox_("����δ��ҽ�ƿ�����!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("�޿ɴ�ӡ��ҽ�ƿ�Ʊ��!");
			// this.onClear();
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("���һ��ҽ�ƿ�Ʊ��!");
		}
		String endNo_num = bilInvoice.getEndInvno().replaceAll("[^0-9]", "");
		String endNo_word = bilInvoice.getEndInvno().replaceAll("[0-9]", "");
		String nowNo_num = bilInvoice.getUpdateNo().replaceAll("[^0-9]", "");
		String nowNo_word = bilInvoice.getUpdateNo().replaceAll("[0-9]", "");
		if(nowNo_word.equals(endNo_word)&&Long.valueOf(nowNo_num)- Long.valueOf(endNo_num)==1){
			this.messageBox("Ʊ����ʹ���꣬��������Ʊ");
			this.setValue("BIL_CODE","");
			return false;
		}
		
		if(!compareInvno(bilInvoice.getStartInvno(),bilInvoice.getEndInvno(),bilInvoice.getUpdateNo())){
			this.messageBox("Ʊ�ų�����Χ");
			onClear();
			return false;
		}
		callFunction("UI|BIL_CODE|setValue", bilInvoice.getUpdateNo());
		return true;
	}
	/**
	 * �Ƚ�Ʊ��
	 * @return
	 */
	private boolean compareInvno(String StartInvno, String EndInvno,String UpdateNo) {
		String startNo_num = StartInvno.replaceAll("[^0-9]", "");// ȥ������
		String startNo_word = StartInvno.replaceAll("[0-9]", "");// ȥ����
		String endNo_num = EndInvno.replaceAll("[^0-9]", "");
		String endNo_word = EndInvno.replaceAll("[0-9]", "");
		String nowNo_num = UpdateNo.replaceAll("[^0-9]", "");
		String nowNo_word = UpdateNo.replaceAll("[0-9]", "");
		if (startNo_word.equals(endNo_word)&&startNo_word.equals(nowNo_word)){
			if(Long.valueOf(endNo_num)- Long.valueOf(nowNo_num)>=0&&Long.valueOf(nowNo_num)- Long.valueOf(startNo_num)>=0){
			return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
}
