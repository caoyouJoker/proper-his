package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.TableModel;

import org.apache.commons.lang.time.DurationFormatUtils;

import jdo.device.CallNo;
import jdo.device.LAB_Service;
import jdo.device.LAB_Service.DynamecMassage;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.med.MEDApplyTool;
import jdo.opd.OPDSysParmTool;
import jdo.spc.StringUtils;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatRegClinicArea;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;
import com.javahis.xml.Item;
import com.javahis.xml.Job;

/**
 * <p> Title: ���������ӡ </p>
 * 
 * <p> Description: ���������ӡ </p>
 * 
 * <p> Copyright: Copyright (c) 2008 </p>
 * 
 * <p> Company: Javahis </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class MEDApplyControl extends TControl {
	/**
	 * ����������
	 */
	private String actionName = "action.med.MedAction";
	
	private static final boolean isDebug=false;

	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ���￴������סԺΪ��ǰ����
	 */
	private Timestamp admDate;
	/**
	 * �����
	 */
	private String caseNo = "";
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * ��������
	 */
	private String patName;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ����
	 */
	private String bedNo;
	/**
	 * ����
	 */
	private String stationCode;
	/**
	 * ����
	 */
	private String clinicareaCode;
	/**
	 * ����
	 */
	private String clinicroomNo;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	/**
	 * ������롢��ͬ����
	 */
	private String companyCode, contractCode;// add by wanglong 20121214

    /**
	 * ��ͬ����
	 */
	private HRMContractD contractD;// add by wanglong 20121214
	/**
	 * ��ͬTTextFormat
	 */
	private TTextFormat contract;// add by wanglong 20121214

	/**
	 *  ���������
	 */
	private StringBuffer printText = new StringBuffer();//wanglong add 20140610
    private int offset_x = 0;//wanglong add 20150410
    private int offset_y = 0;//wanglong add 20150410
    private String execBarCode = "";// ��¼����������� yanjing 20140919
	TTextFormat clinicAreaCode;// ����add by yanjing 20151104
	private TTextFormat company;// ��������TTextFormat
	
	public void onInit() {
		super.onInit();
		contractD = new HRMContractD();// add by wanglong 20121214
		company = (TTextFormat) this.getComponent("COMPANY_CODE");
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		/**
		 * REG_CLINICAREA���� REG_CLINICROOM���� (סԺCOMBOȨ��)(����Ȩ��)(�ż�ס��Ȩ��)
		 */
		// ================pangben modify 20110405 start ��������
		setValue("REGION_CODE", Operator.getRegion());
		// ================pangben modify 20110405 stop
		// ========pangben modify 20110421 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

		Object obj = this.getParameter();
		// this.messageBox(""+obj);

		if (obj != null) {
			if (obj instanceof TParm) {
				TParm parm = (TParm) obj;
				this.setAdmType(parm.getValue("ADM_TYPE"));
				this.setDeptCode(parm.getValue("DEPT_CODE"));
				this.setCaseNo(parm.getValue("CASE_NO"));
				this.setMrNo(parm.getValue("MR_NO"));
				this.setPatName(parm.getValue("PAT_NAME"));
				this.setAdmDate(parm.getTimestamp("ADM_DATE"));
				if(!StringUtil.isNullString(parm.getValue("COMPANY_CODE")) ){//add by wanglong 20130726
				    this.setCompanyCode(parm.getValue("COMPANY_CODE"));
				}
				if(!StringUtil.isNullString(parm.getValue("CONTRACT_CODE"))){//add by wanglong 20130726
                    this.setContractCode(parm.getValue("CONTRACT_CODE"));
                }
				if ("I".equals(this.getAdmType())) {
					this.setIpdNo(parm.getValue("IPD_NO"));
					this.setStationCode(parm.getValue("STATION_CODE"));
					this.setBedNo(parm.getValue("BED_NO"));
					this.setStationCode(Operator.getStation());
				} else {
					this.setClinicareaCode(parm.getValue("CLINICAREA_CODE"));
					this.setClinicroomNo(parm.getValue("CLINICROOM_NO"));
				}
				if (parm.getValue("POPEDEM").length() != 0) {
					// һ��Ȩ��
					if ("1".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("NORMAL", true);
						this.setPopedem("SYSOPERATOR", false);
						this.setPopedem("SYSDBA", false);
					}
					// ��ɫȨ��
					if ("2".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSOPERATOR", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSDBA", false);
					}
					// ���Ȩ��
					if ("3".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSDBA", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSOPERATOR", false);
					}
				}
				
				// add by wangb 2016/08/02
				if ("PIC".equals(parm.getValue("ROLE_TYPE"))) {
					this.setPopedem("PIC", true);
				}
			} else {
				// this.messageBox(""+obj);
				// this.messageBox(""+obj.toString());
				this.setAdmType("" + obj);
				// this.setPopedem("SYSOPERATOR",true);
				String date = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyyMMdd")
						+ "000000";
				this
						.setAdmDate(StringTool.getTimestamp(date,
								"yyyyMMddHHmmss"));
			}

		} else {
			// סԺ
			// this.setAdmType("I");
			// this.setDeptCode("10101");
			// this.setCaseNo("090918000012");
			// this.setMrNo("000000000209");
			// this.setPatName("סԺ��2");
			// this.setAdmDate(StringTool.getTimestamp("20091021","yyyyMMdd"));
			// this.setIpdNo("000000000091");
			// this.setStationCode("001");
			// this.setBedNo("0010110");
			// ����
			// this.setAdmType("H");
			// this.setDeptCode("10101");
			// this.setCaseNo("091118000005");
			// this.setMrNo("000000000232");
			// this.setPatName("����");
			// this.setAdmDate(StringTool.getTimestamp("20091118","yyyyMMdd"));
			// this.setClinicareaCode("1");
			// this.setClinicroomNo("A01");
			// this.setAdmDate(StringTool.getTimestamp(new Date()));
		}
		/**
		 * ��ʼ��Ȩ��
		 */
		onInitPopeDem();
		/**
		 * ��ʼ��ҳ��
		 */
		onApplyCheck();
		/**
		 * ��ʼ���¼�
		 */
		initEvent();
	}

	/**
	 * ��ʼ���¼�
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		// �������
		addListener(getTTable(TABLE));
	}

	/**
	 * ��ʼ������
	 */
	// public void onInitParameter(){
	// /**
	// * 1��һ��Ȩ��(NORMAL)
	// * 2����ɫȨ��(SYSOPERATOR)
	// * 2�����Ȩ��(SYSDBA)
	// */
	// //һ��Ȩ��
	// // this.setPopedem("NORMAL",true);
	// //��ɫȨ��
	// // this.setPopedem("SYSOPERATOR",true);
	// //���Ȩ��
	// // this.setPopedem("SYSDBA",true);
	// // this.setParameter("H");
	// }
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("APPLICATION_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("APPLICATION_NO", i))) {
					// modify by wangb 2017/3/24 ����������㲻׼ȷ���º�����Ŀ��ѡ״̬��һ�µ�����
					parm.setData("FLG", i, parm.getBoolean("FLG", row));
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * ѡ���¼�
	 */
	public void onSelRadioButton(Object obj) {
		this.onClear();
		if ("O".equals("" + obj)) {
			getTTextFormat("DEPT_CODEMED").setEnabled(false);
			getTTextFormat("STATION_CODEMED").setEnabled(false);
			getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
			getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
			getTTextField("IPD_NO").setEnabled(false);
			getTTextField("BED_NO").setEnabled(false);
			getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121214
			getTTextFormat("CONTRACT_CODE").setEnabled(false);
			getTTextField("START_SEQ_NO").setEnabled(false);
			getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("E".equals("" + obj)) {
			getTTextFormat("DEPT_CODEMED").setEnabled(false);
			getTTextFormat("STATION_CODEMED").setEnabled(false);
			getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
			getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
			getTTextField("IPD_NO").setEnabled(false);
			getTTextField("BED_NO").setEnabled(false);
			getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121214
			getTTextFormat("CONTRACT_CODE").setEnabled(false);
			getTTextField("START_SEQ_NO").setEnabled(false);
			getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("I".equals("" + obj)) {
			getTTextFormat("DEPT_CODEMED").setEnabled(true);
			getTTextFormat("STATION_CODEMED").setEnabled(true);
			getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
			getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
			getTTextField("IPD_NO").setEnabled(true);
			getTTextField("BED_NO").setEnabled(true);
			getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121214
			getTTextFormat("CONTRACT_CODE").setEnabled(false);
			getTTextField("START_SEQ_NO").setEnabled(false);
			getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;ҽʦ��ע,100;����,100,DEPT_CODE;����,100,STATION_CODE;����,100,BED_NO;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DR_NOTE;DEPT_CODE;STATION_CODE;BED_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("H".equals("" + obj)) {
			
			getTTextFormat("DEPT_CODEMED").setEnabled(true);
			getTTextFormat("STATION_CODEMED").setEnabled(false);
			getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
			getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
			getTTextField("IPD_NO").setEnabled(false);
			getTTextField("BED_NO").setEnabled(false);
			getTTextFormat("COMPANY_CODE").setEnabled(true);// add-by-wanglong-20121214
			getTTextFormat("CONTRACT_CODE").setEnabled(true);
			getTTextField("START_SEQ_NO").setEnabled(true);
			getTTextField("END_SEQ_NO").setEnabled(true);// add-end
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;���,50;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");//caowl 20130320 ����Ա�����
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");//caowl 20130320 ����Ա�����
		}
		this.onQuery();
	}

	/**
	 * ��ʼ��Ȩ��
	 */
	public void onInitPopeDem() {
		if (this.getPopedem("NORMAL")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("E".equals(this.getAdmType())) {
				this.getTRadioButton("E").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("I".equals(this.getAdmType())) {
				this.getTRadioButton("I").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("I").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("H".equals(this.getAdmType())) {
				this.getTRadioButton("H").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
		}
		if (this.getPopedem("SYSOPERATOR")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("E".equals(this.getAdmType())) {
				this.getTRadioButton("E").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("I".equals(this.getAdmType())) {
				this.getTRadioButton("I").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("I").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(true);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(true);
				getTTextField("BED_NO").setEnabled(true);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("H".equals(this.getAdmType())) {
				this.getTRadioButton("H").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(true);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(true);
				getTTextField("START_SEQ_NO").setEnabled(true);
				getTTextField("END_SEQ_NO").setEnabled(true);// add-end
			}
		}
		if (this.getPopedem("SYSDBA")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("E".equals(this.getAdmType())) {
				this.getTRadioButton("E").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("I".equals(this.getAdmType())) {
				this.getTRadioButton("I").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(true);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(true);
				getTTextField("BED_NO").setEnabled(true);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("H".equals(this.getAdmType())) {
				this.getTRadioButton("H").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
                getTTextFormat("COMPANY_CODE").setEnabled(true);// add-by-wanglong-20121213
                getTTextFormat("CONTRACT_CODE").setEnabled(true);
                getTTextField("START_SEQ_NO").setEnabled(true);
                getTTextField("END_SEQ_NO").setEnabled(true);// add-end
			}
		}
		
		// modify by wangb 2017/3/16 һ���ٴ����������ӡ�����ִ�н������
		// add by wangb 2016/11/1 һ���ٴ�Ĭ�Ͻ������ִ�й���
		if ("H".equals(this.getAdmType()) && this.getPopedem("SAMPLING")
				&& StringUtils.isEmpty(getMrNo())) {
			this.setTitle("����ִ��");
			this.setValue("APPLY_FLG", "Y");// ����ִ�пؼ��Զ���ѡ
			this.getTRadioButton("YESPRINT").setSelected(true);// Ĭ��ѡ���Ѵ�ӡ
			this.grabFocus("MR_NO");
		}
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", this.getAdmDate());
		this.setValue("END_DATE", sysDate);
		if ("O".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("E".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("I".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("IPD_NO", this.getIpdNo());
			this.setValue("BED_NO", this.getBedNo());
			if (this.getMrNo() != null) {
				this.setValue("DEPT_CODEMED", this.getDeptCode());
				this.setValue("STATION_CODEMED", this.getStationCode());
			} else {
				if (this.getPopedem("NORMAL")) {
					this.setValue("USER_ID", Operator.getID());
					this.setValue("STATION_CODEMED", Operator.getStation());
					callFunction("UI|USER_ID|onQuery");
					callFunction("UI|STATION_CODEMED|onQuery");
				}
			}
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;ҽʦ��ע,100;����,100,DEPT_CODE;����,100,STATION_CODE;����,100,BED_NO;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DR_NOTE;DEPT_CODE;STATION_CODE;BED_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("H".equals(this.getAdmType())) {

			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� begin */
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/"
					+ d.getDate() + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
					+ d.getDate() + " 23:59:59";

			// ��ʼ��ʱ��
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� end */

			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.setValue("COMPANY_CODE", this.getCompanyCode());//add by wanglong 20130726
            if (!StringUtil.isNullString(this.getCompanyCode())) {
	            TParm contractParm = contractD.onQueryByCompany(this.getCompanyCode());//add-by-wanglong-20130821
	            contract.setPopupMenuData(contractParm);
	            contract.setComboSelectRow();
	            contract.popupMenuShowData();
	            contract.setValue(this.getContractCode()); //add-end
			}
//			this.setValue("CONTRACT_CODE", this.getContractCode());
			this
					.getTTable(TABLE)
					.setHeader("ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;���,50;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;סԺ��,100");//caowl 20130320 �������һ��
			this
					.getTTable(TABLE)
					.setParmMap("FLG;PRINT_FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");//caowl 20130320 �������һ��
		}
		// ��ѯ
		this.onQuery();
	}
	
	/**
	 * ����ִ�пؼ��¼�
	 */
	public void onApplyCheck() {
		if (this.getValueBoolean("APPLY_FLG")) {
			callFunction("UI|print|setEnabled", false);
			callFunction("UI|send|setEnabled", false);
			callFunction("UI|execute|setEnabled", false);// wanglong add
															// 20141212
			callFunction("UI|cancel|setEnabled", false);
			callFunction("UI|apply|setEnabled", true);
			this.initApplyPage();
		} else {
			callFunction("UI|apply|setEnabled", false);
			callFunction("UI|print|setEnabled", true);
			callFunction("UI|send|setEnabled", true);
			callFunction("UI|execute|setEnabled", true);// wanglong add 20141212
			callFunction("UI|cancel|setEnabled", true);
			this.initPage();
		}
		if ("O".equals(this.getAdmType())) {
			callFunction("UI|O|setEnabled", true);
			callFunction("UI|E|setEnabled", false);
			callFunction("UI|I|setEnabled", false);
			callFunction("UI|H|setEnabled", false);
		} else if ("E".equals(this.getAdmType())) {
			callFunction("UI|O|setEnabled", false);
			callFunction("UI|E|setEnabled", true);
			callFunction("UI|I|setEnabled", false);
			callFunction("UI|H|setEnabled", false);
		} else if ("I".equals(this.getAdmType())) {
			callFunction("UI|O|setEnabled", false);
			callFunction("UI|E|setEnabled", false);
			callFunction("UI|I|setEnabled", true);
			callFunction("UI|H|setEnabled", false);

			// add by yangjj 20150319 ���ز���ȷ��panel
			TPanel panel = (TPanel) this.getComponent("tPanel_0");
			panel.setVisible(false);

			// add by yangjj 20150319���ز���ִ��menu
			callFunction("UI|apply|setVisible", false);

			// add by wangb 2016/08/10 һ���ٴ���ӡPKѪ��������д��������
			if (this.getPopedem("PIC")) {
				callFunction("UI|LABEL_CYCLE|setVisible", true);
				callFunction("UI|CYCLE|setVisible", true);
			}
		} else {
			callFunction("UI|O|setEnabled", false);
			callFunction("UI|E|setEnabled", false);
			callFunction("UI|I|setEnabled", false);
			callFunction("UI|H|setEnabled", true);
		}
		clinicAreaCode = (TTextFormat) this.getComponent("CLINICAREA_CODEMED");// ִ�п���
		TextFormatRegClinicArea combo_clinicarea = (TextFormatRegClinicArea) this
				.getComponent("CLINICAREA_CODEMED");
		combo_clinicarea.setDrCode(Operator.getID());
		combo_clinicarea.onQuery();
		
		// add by wangb 2016/08/03 ����������Ҫ���ݲ�ͬ��¼��ɫɸѡ
		String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
        
		// ��ѯ������Ϣ
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
	}
	
	/**
	 * ��ʼ��ҳ��
	 */
	public void initApplyPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String endDay = StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd") + " 23:59:59";// ==liling
																				// 20140825
																				// add
		this.setValue("START_DATE", this.getAdmDate());
		this.setValue("END_DATE", getTimestamp(endDay));
		// System.out.println("----0000-----mr_no is ::"+this.getMrNo());
		if ("O".equals(this.getAdmType())) {// ��ӡ�����ҽ������ORDER_DR����������ǰ�棬�����뱨������еĿ�ȱ�С��ҽ���зŴ�
											// ǰ2������20 ҽ����40
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;��,30,boolean;����,70;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");
			this.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		if ("E".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");
			this.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}

		if ("H".equals(this.getAdmType())) {

			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� begin */
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/"
					+ d.getDate() + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
					+ d.getDate() + " 23:59:59";
			// ��ʼ��ʱ��
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� end */

			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.setValue("COMPANY_CODE", this.getCompanyCode());// add by
																	// wanglong
																	// 20130726
			if (!StringUtil.isNullString(this.getCompanyCode())) {
				TParm contractParm = contractD.onQueryByCompany(this
						.getCompanyCode());// add-by-wanglong-20130821
				contract.setPopupMenuData(contractParm);
				contract.setComboSelectRow();
				contract.popupMenuShowData();
				contract.setValue(this.getContractCode()); // add-end
			}
			// this.setValue("CONTRACT_CODE", this.getContractCode());
//			this.getTTable(TABLE)
//					.setHeader(
//							"ѡ,30,boolean;��,30,boolean;���,50;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,160;ҽ������ʱ��,140;����,90,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");// caowl
//																																																																																			// 20130320
//																																																																																			// �������һ��
//			this.getTTable(TABLE)
//					.setParmMap(
//							"FLG;URGENT_FLG;NO;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");// caowl
			this
				.getTTable(TABLE)
				.setHeader("ѡ,30,boolean;ӡ,30,boolean;���,50;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,100,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100");//caowl 20130320 �������һ��
			this
				.getTTable(TABLE)
				.setParmMap("FLG;PRINT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;BLOOD_STATE");//caowl 20130320 �������һ��
																																																																							// �������һ��
		}
		// ��ѯ
		// this.onQuery();
	}
	

	public static Timestamp getTimestamp(String time) {
		Date date = new Date();
		// ע��format�ĸ�ʽҪ������String�ĸ�ʽ��ƥ��
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			date = sdf.parse(time);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �õ�TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * ����TRadonButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * �õ�TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		boolean queryFlg = false;
		queryFlg = this.getValueBoolean("APPLY_FLG");
		if (queryFlg) {
			this.applyQuery();// �������빴ѡ
			return ;
		}
		if (this.getValueString("MR_NO").trim().length() != 0) {
			
			//modify by huangtt 20160927 EMPI���߲�����ʾ  start
			Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
			String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
		          this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
		    }
			this.setValue("MR_NO", pat.getMrNo());
			this.setValue("PAT_NAME", pat.getName());

//			this.setValue("MR_NO", PatTool.getInstance().checkMrno(
//					this.getValueString("MR_NO")));
//			this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
//					PatTool.getInstance().checkMrno(
//							this.getValueString("MR_NO"))));
			
			//modify by huangtt 20160927 EMPI���߲�����ʾ  end
			
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "O");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm
						.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm
						.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm
						.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm
						.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("I".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "I");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setValue("STATION_CODEMED", patInfParm
						.getValue("STATION_CODE"));
				this.setValue("IPD_NO", patInfParm.getValue("IPD_NO"));
				this.setValue("BED_NO", patInfParm.getValue("BED_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
//				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getQuerySQL();
			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
				sql = getHRMQuerySQL();
			}
			TParm action = new TParm(this.getDBTool().select(sql));
			this.getTTable(TABLE).setParmValue(action);
			return;
		}
		if (this.getValueString("IPD_NO").trim().length() != 0) {
			this.setValue("IPD_NO", PatTool.getInstance().checkIpdno(
					this.getValueString("IPD_NO")));
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this
						.getValueString("IPD_NO"), "O");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm
						.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm
						.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this
						.getValueString("IPD_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm
						.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm
						.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("I".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this
						.getValueString("IPD_NO"), "I");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setValue("STATION_CODEMED", patInfParm
						.getValue("STATION_CODE"));
				this.setValue("IPD_NO", patInfParm.getValue("IPD_NO"));
				this.setValue("BED_NO", patInfParm.getValue("BED_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this
						.getValueString("IPD_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getQuerySQL();
			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
				sql = getHRMQuerySQL();
			}
			TParm action = new TParm(this.getDBTool().select(sql));
			this.getTTable(TABLE).setParmValue(action);
			return;
		}
		String sql = getQuerySQL();
		if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
			sql = getHRMQuerySQL();
		}
		TParm action = new TParm(this.getDBTool().select(sql));
		this.getTTable(TABLE).setParmValue(action);
		// ÿ�β�ѯ���ȫѡ
		this.setValue("ALLCHECK", "N");
	}

	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		TParm parm = this.getTTable(TABLE).getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (this.getTCheckBox("ALLCHECK").isSelected())
				parm.setData("FLG", i, "Y");
			else
				parm.setData("FLG", i, "N");
		}
		this.getTTable(TABLE).setParmValue(parm);
	}

	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * ���ز�ѯ�������
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            String
	 * @return TParm
	 */
	public TParm getPatInfo(String columnName, String value, String admType) {
		TParm result = new TParm();
		if ("O".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm oParm = new TParm(
						this
								.getDBTool()
								.select(
										"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE CASE_NO='"
												+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", oParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", oParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", oParm.getData("CLINICROOM_NO",
						0));
				TParm oIparm = new TParm(this.getDBTool().select(
						"SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
								+ value + "'"));
				result.setData("PAT_NAME", oIparm.getData("PAT_NAME", 0));
				return result;
			}

			TParm queryParm = new TParm(this.getDBTool().select(
					"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO,MR_NO FROM REG_PATADM WHERE "
//					+ columnName + "='" + value + "'"));
							+ columnName + " IN (" + PatTool.getInstance().getMrRegMrNos(value) + ")"));
			
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "O");
				Object obj = this.openDialog(
						"%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("CLINICAREA_CODE", temp
							.getData("CLINICAREA_CODE"));
					result.setData("CLINICROOM_NO", temp
							.getData("CLINICROOM_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", queryParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", queryParm.getData(
						"CLINICROOM_NO", 0));
			}

		}
		if ("E".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm eParm = new TParm(
						this
								.getDBTool()
								.select(
										"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE CASE_NO='"
												+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", eParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", eParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", eParm.getData("CLINICROOM_NO",
						0));
				TParm eIparm = new TParm(this.getDBTool().select(
						"SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
								+ value + "'"));
				result.setData("PAT_NAME", eIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool().select(
					"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO,MR_NO FROM REG_PATADM WHERE "
//							+ columnName + "='" + value + "'"));
			+ columnName + " IN (" + PatTool.getInstance().getMrRegMrNos(value) + ")"));

			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "E");
				Object obj = this.openDialog(
						"%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("CLINICAREA_CODE", temp
							.getData("CLINICAREA_CODE"));
					result.setData("CLINICROOM_NO", temp
							.getData("CLINICROOM_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", queryParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", queryParm.getData(
						"CLINICROOM_NO", 0));
			}
        }
        if ("I".equals(admType)) {
            if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
                TParm iParm =
                        new TParm(
                                this.getDBTool()
                                        .select("SELECT IN_DATE AS ADM_DATE,CASE_NO,DEPT_CODE,STATION_CODE,IPD_NO,BED_NO FROM ADM_INP WHERE CASE_NO='"
                                                        + this.getCaseNo() + "'"));
                result.setData("CASE_NO", iParm.getData("CASE_NO", 0));
                result.setData("DEPT_CODE", iParm.getData("DEPT_CODE", 0));
                result.setData("STATION_CODE", iParm.getData("STATION_CODE", 0));
                result.setData("IPD_NO", iParm.getData("IPD_NO", 0));
                result.setData("BED_NO", iParm.getData("BED_NO", 0));
                TParm iIparm =
                        new TParm(this.getDBTool()
                                .select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
                                                + value + "'"));
                result.setData("PAT_NAME", iIparm.getData("PAT_NAME", 0));
                return result;
			}
            TParm queryParm =
                    new TParm(
                            this.getDBTool()
                                    .select("SELECT IN_DATE AS ADM_DATE,CASE_NO,DEPT_CODE,STATION_CODE,IPD_NO,BED_NO,MR_NO FROM ADM_INP WHERE "
//                                                    + columnName
//                                                    + "='"
//                                                    + value
                							+ columnName + " IN (" + PatTool.getInstance().getMrRegMrNos(value) + ")"

                                                    + "' AND DS_DATE IS NULL AND BED_NO IS NOT NULL"));
            if (queryParm.getCount() > 1) {
                queryParm.setData("ADM_TYPE", "I");
                Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
                if (obj != null) {
                    TParm temp = (TParm) obj;
                    result.setData("CASE_NO", temp.getData("CASE_NO"));
                    result.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
                    result.setData("STATION_CODE", temp.getData("STATION_CODE"));
                    result.setData("IPD_NO", temp.getData("IPD_NO"));
                    result.setData("BED_NO", temp.getData("BED_NO"));
                }
            } else {
                result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
                result.setData("DEPT_CODE", queryParm.getData("DEPT_CODE", 0));
                result.setData("STATION_CODE", queryParm.getData("STATION_CODE", 0));
                result.setData("IPD_NO", queryParm.getData("IPD_NO", 0));
                result.setData("BED_NO", queryParm.getData("BED_NO", 0));
            }
        }
        if ("H".equals(admType)) {
            if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
                TParm hParm =
                        new TParm(
                                this.getDBTool()
                                        .select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE CASE_NO='"
                                                        + this.getCaseNo() + "'"));
                result.setData("CASE_NO", hParm.getData("CASE_NO", 0));
                result.setData("DEPT_CODE", hParm.getData("DEPT_CODE", 0));
                TParm hIparm =
                        new TParm(this.getDBTool()
                                .select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
                                                + value + "'"));
                result.setData("PAT_NAME", hIparm.getData("PAT_NAME", 0));
                return result;
            }
            TParm queryParm =
                    new TParm(
                            this.getDBTool()
                                    .select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE,MR_NO FROM HRM_PATADM WHERE "
//                                                    + columnName + "='" + value + "'"));
			+ columnName + " IN (" + PatTool.getInstance().getMrRegMrNos(value) + ")"));

            if (queryParm.getCount() > 1) {
                queryParm.setData("ADM_TYPE", "H");
                Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
                if (obj != null) {
                    TParm temp = (TParm) obj;
                    result.setData("CASE_NO", temp.getData("CASE_NO"));
                    result.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
                }
            } else {
                result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
                result.setData("DEPT_CODE", queryParm.getData("DEPT_CODE", 0));
            }
        }
        TParm parm =
                new TParm(this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName
                                                          + "='" + value + "'"));
        result.setData("PAT_NAME", parm.getData("PAT_NAME", 0));
        return result;
    }

	/**
	 * �õ���ѯ���
	 * 
	 * @return String
	 */
    public String getQuerySQL() {
        String sql =
                "SELECT 'N' AS FLG,PRINT_FLG,DEPT_CODE,STATION_CODE,CLINICAREA_CODE,CLINICROOM_NO,BED_NO,PAT_NAME,APPLICATION_NO,RPTTYPE_CODE,OPTITEM_CODE,DEV_CODE,MR_NO,IPD_NO,ORDER_DESC,CAT1_TYPE,OPTITEM_CHN_DESC,TO_CHAR(ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,DR_NOTE,EXEC_DEPT_CODE,URGENT_FLG,SEX_CODE,BIRTH_DATE,ORDER_NO,SEQ_NO,TEL,ADDRESS,CASE_NO,ORDER_CODE,ADM_TYPE,PRINT_DATE FROM MED_APPLY";
        TParm queryParm = this.getParmQuery();
        String columnName[] = queryParm.getNames();
        if (columnName.length > 0) sql += " WHERE ";
        int count = 0;
        for (String temp : columnName) {
            if (temp.equals("END_DATE")) continue;
            if (temp.equals("START_DATE")) {
                if (count > 0) sql += " AND ";
                sql +=
                        " START_DTTM BETWEEN TO_DATE('" + queryParm.getValue("START_DATE")
                                + "','YYYYMMDDHH24MISS') AND TO_DATE('"
                                + queryParm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')";
                count++;
                continue;
            }
            if (count > 0) sql += " AND ";
            //modify by huangtt 20161102 
            if("MR_NO".equals(temp)){
            	 sql += temp + " IN (" + PatTool.getInstance().getMrRegMrNos(queryParm.getValue(temp))  + ") ";
            }else{
            	 sql += temp + "='" + queryParm.getValue(temp) + "' ";
            }

            count++;
        }
        if (count > 0) sql += " AND ";
        if ("H".equals(getAdmTypeRadioValue())) sql +=
                " CAT1_TYPE='LIS' AND STATUS <> 9 ORDER BY CAT1_TYPE,CASE_NO,APPLICATION_NO,SEQ_NO";
        else if ("I".equals(getAdmTypeRadioValue())) sql +=
                " CAT1_TYPE='LIS'  ORDER BY CAT1_TYPE,CASE_NO,BED_NO,APPLICATION_NO,SEQ_NO";
        else sql += " CAT1_TYPE='LIS' AND BILL_FLG='Y' ORDER BY CAT1_TYPE,CASE_NO,BED_NO,APPLICATION_NO,SEQ_NO";
        // System.out.println("��ͨsql:"+sql);
        return sql;
    }

	/**
	 * �õ���ѯ����
	 * 
	 * @return TParm
	 */
    public TParm getParmQuery() {
        TParm result = new TParm();
        result.setData("ADM_TYPE", this.getAdmTypeRadioValue());
        result.setData("START_DATE", StringTool.getString((Timestamp) this.getValue("START_DATE"),
                                                          "yyyyMMddHHmmss"));
        result.setData("END_DATE", StringTool.getString((Timestamp) this.getValue("END_DATE"),
                                                        "yyyyMMddHHmmss"));
        if (this.getValueString("DEPT_CODEMED").length() != 0) {
            result.setData("DEPT_CODE", this.getValueString("DEPT_CODEMED"));
        }
        if (this.getValueString("STATION_CODEMED").length() != 0) {
            result.setData("STATION_CODE", this.getValueString("STATION_CODEMED"));
        }
        if (this.getValueString("CLINICAREA_CODEMED").length() != 0) {
            result.setData("CLINICAREA_CODE", this.getValueString("CLINICAREA_CODEMED"));
        }
        if (this.getValueString("CLINICROOM_CODEMED").length() != 0) {
            result.setData("CLINICROOM_NO", this.getValueString("CLINICROOM_CODEMED"));
        }
        if (getPrintStatus().length() != 0) {
            result.setData("PRINT_FLG", getPrintStatus());
        }
        if (this.getValueString("MR_NO").length() != 0) {
            result.setData("MR_NO", this.getValueString("MR_NO"));
        }
        if (this.getValueString("IPD_NO").length() != 0) {
            result.setData("IPD_NO", this.getValueString("IPD_NO"));
        }
        // if (this.getValueString("BED_NO").length() != 0) {
        // result.setData("BED_NO", this.getValueString("BED_NO"));
        // }
        // ==================pangben modify 20110406 start
        if (this.getValueString("REGION_CODE").length() != 0) {
            result.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        }
        // ==================pangben modify 20110406 stop
        return result;
    }

	/**
	 * �õ�����Ĳ�ѯ���
	 * 
	 * @return String
	 */
	public String getHRMQuerySQL() {// add by wanglong 20121214
		String sql = "SELECT 'N' AS FLG, A.PRINT_FLG,  A.DEPT_CODE, A.STATION_CODE, A.CLINICAREA_CODE,"
				+ "          A.CLINICROOM_NO, A.PAT_NAME, A.APPLICATION_NO, A.RPTTYPE_CODE, A.OPTITEM_CODE, "
				+ "          A.DEV_CODE, A.MR_NO,A.IPD_NO, A.ORDER_DESC, A.CAT1_TYPE,A.OPTITEM_CHN_DESC, "
				+ "          TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,"
				+ "          A.EXEC_DEPT_CODE, A.URGENT_FLG,A.SEX_CODE, A.BIRTH_DATE,A.ORDER_NO, A.SEQ_NO, "
				+ "          A.TEL, A.ADDRESS,A.CASE_NO,A.ORDER_CODE, A.ADM_TYPE, A.PRINT_DATE,C.SEQ_NO AS NO, "//caowl 20130320 ����Ա�����
				+ "          C.STAFF_NO,C.PLAN_NO "// add by wangb 2016/08/03 Ϊһ���ٴ�����ɸѡ�ż�������
				+ "     FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C"
				+ "    WHERE A.APPLICATION_NO = B.MED_APPLY_NO "
				+ "      AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "      AND A.ORDER_NO = B.CASE_NO "
				+ "       AND A.SEQ_NO = B.SEQ_NO  "
				+ "      AND B.MR_NO = C.MR_NO ";
        sql += " AND A.ADM_TYPE = 'H' AND B.SETMAIN_FLG='Y' ";// �ż���
        String srartDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// ��ʼʱ��
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");// ����ʱ��
        sql +=
                " AND A.START_DTTM BETWEEN TO_DATE('" + srartDate + "','YYYYMMDDHH24MISS') "
                        + "                     AND TO_DATE('" + endDate + "','YYYYMMDDHH24MISS') ";
        if (this.getValueString("REGION_CODE").length() != 0) {// ����
            sql += " AND A.REGION_CODE = '" + this.getValueString("REGION_CODE") + "'";
        }
        if (this.getValueString("DEPT_CODEMED").length() != 0) {// ����
            sql += " AND A.DEPT_CODE = '" + this.getValueString("DEPT_CODEMED") + "'";
        }
        if (getPrintStatus().length() != 0) {// ��ӡ״̬��δ��ӡ���Ѵ�ӡ��ȫ����
            sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
        }
        if (this.getValueString("MR_NO").length() != 0) {// ������
//        	sql += " AND A.MR_NO = '" + this.getValueString("MR_NO") + "'";
            sql += " AND A.MR_NO IN (" + PatTool.getInstance().getMrRegMrNos(this.getValueString("MR_NO"))  + ")";
        }
        if (this.getValueString("COMPANY_CODE").length() != 0) {// �����
            sql += " AND C.COMPANY_CODE = '" + this.getValueString("COMPANY_CODE") + "'";
        }
        if (this.getValueString("CONTRACT_CODE").length() != 0) {// ��ͬ��
            sql += " AND C.CONTRACT_CODE = '" + this.getValueString("CONTRACT_CODE") + "'";
        }
        if (this.getValueString("START_SEQ_NO").length() != 0) {// Ա����ſ�ʼ
            sql += " AND C.SEQ_NO >= '" + this.getValueString("START_SEQ_NO") + "'";
        }
        if (this.getValueString("END_SEQ_NO").length() != 0) {// Ա����Ž���
            sql += " AND C.SEQ_NO <= '" + this.getValueString("END_SEQ_NO") + "'";
        }
		sql += " AND A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO ASC ,A.CAT1_TYPE ASC,A.START_DTTM ASC, A.CASE_NO ASC,A.APPLICATION_NO,A.SEQ_NO";// caowl
																																	// 20130305
																																	// ��������������
//		System.out.println("�������sql:"+sql);
		return sql;
	}

	/**
	 * �õ��ż�ס��ֵ
	 * 
	 * @return String
	 */
	public String getAdmTypeRadioValue() {
		if (this.getTRadioButton("O").isSelected())
			return "O";
		if (this.getTRadioButton("E").isSelected())
			return "E";
		if (this.getTRadioButton("I").isSelected())
			return "I";
		if (this.getTRadioButton("H").isSelected())
			return "H";
		return "O";
	}

	/**
	 * �õ���ӡ״̬
	 * 
	 * @return String
	 */
	public String getPrintStatus() {
		if (this.getTRadioButton("ALL").isSelected())
			return "";
		if (this.getTRadioButton("ONPRINT").isSelected())
			return "N";
		if (this.getTRadioButton("YESPRINT").isSelected())
			return "Y";
		return "";
	}

	/**
	 * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲢�����Ƿ�Ϊ��ͯ�Բ�ͬ����ʽ��ʾ���䣬���Ƕ�ͯ����ʾX��X��X�գ����������ʾx��
	 * 
	 * @param odo
	 * @return String ������ʾ������
	 */
	public static String showAge(Timestamp birth, Timestamp sysdate) {
		String age = "";
		String[] res;
		res = StringTool.CountAgeByTimestamp(birth, sysdate);
		if (OPDSysParmTool.getInstance().isChild(birth, sysdate)) {//wanglong modify 20150119
			age = res[0] + "��" + res[1] + "��";
		} else {
			age = (Integer.parseInt(res[0]) == 0 ? 1 : res[0]) + "��";
		}
		// System.out.println("age" + age);
		return age;
	}

	/**
	 * ���������
	 */
    public void onSend() {
        this.getTTable(TABLE).acceptText();
        int rowCount = this.getTTable(TABLE).getRowCount();
        Set caseNoset = new HashSet();
        Timestamp sysDate = SystemTool.getInstance().getDate();
        for (int i = 0; i < rowCount; i++) {
            TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
            if (!temp.getBoolean("FLG")) continue;
            caseNoset.add(temp.getValue("CASE_NO"));
        }
        if (caseNoset.size() > 0) {
            Iterator caseNoIter = caseNoset.iterator();
            while (caseNoIter.hasNext()) {
                String caseNo = "" + caseNoIter.next();
                Set applicationNo = new HashSet();
                for (int i = 0; i < rowCount; i++) {
                    TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
                    if (!temp.getBoolean("FLG")) continue;
                    if (!caseNo.equals(temp.getValue("CASE_NO"))) continue;
                    applicationNo.add(temp.getValue("APPLICATION_NO"));
                }
                if (applicationNo.size() > 0) {
                    Job job = new Job(applicationNo.size());
                    List printSize = new ArrayList();
                    Iterator appNo = applicationNo.iterator();
                    int itemCount = 0;
                    while (appNo.hasNext()) {
                        String appNoStr = "" + appNo.next();
                        StringBuffer orderDesc = new StringBuffer();
                        // item
                        Item item = job.getLabels().getItem(itemCount);
                        for (int i = 0; i < rowCount; i++) {
                            TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
                            if (!appNoStr.equals(temp.getValue("APPLICATION_NO"))) continue;
                            job.setSurname(temp.getValue("PAT_NAME"));
                            job.setName("");
                            job.setSex(this.getDictionary("SYS_SEX", temp.getValue("SEX_CODE")));
                            job.setWard(this.getDeptDesc(temp.getValue("DEPT_CODE")));
                            job.setBdate(this.showAge(temp.getTimestamp("BIRTH_DATE"), sysDate));
                            job.setAdate(StringTool.getString(temp.getTimestamp("ORDER_DATE"),
                                                              "yyyy/MM/dd HH:mm:ss"));
                            job.setPhone(temp.getValue("TEL"));
                            job.setAddress(temp.getValue("ADDRESS"));
                            job.setSpecimenid(caseNo);
                            job.setPatientid(temp.getValue("MR_NO"));
                            job.setRequestid(temp.getValue("ORDER_NO"));
                            job.setNotes(temp.getValue("ADM_TYPE"));
                            // �Թ����
                            item.setTubeconvert(getTubeType(temp.getValue("ORDER_CODE")));
                            // ��ע
                            item.setNotelabel(geturGentFlg(appNoStr).equals("Y") ? "(��)" : "");
                            // ����
                            item.setBarcodelabel(appNoStr);
                            // �������
                            item.setVariouslabel(temp.getValue("SEQ_NO"));
                            // ʱ��
                            item.setPdate(StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss"));
                            if (appNoStr.equals(temp.getValue("APPLICATION_NO"))) {
                                orderDesc.append(temp.getValue("ORDER_DESC"));
                            }
                        }
                        item.setMnemotests(orderDesc.toString());
                        itemCount++;
                    }
                    printSize.add(job);
                    boolean falg = true;
                    int listRowCount = printSize.size();
                    for (int i = 0; i < listRowCount; i++) {
                        Job pR = (Job) printSize.get(i);
                        LAB_Service callLab = new LAB_Service();
                        if (!callLab.init()) continue;
                        String host = "127.0.0.1";
                        if ("O".equals(job.getNotes())) {
                            host = callLab.getHost();
                        }
                        if ("H".equals(job.getNotes())) {
                            host = callLab.getHostH();
                        }
                        if ("E".equals(job.getNotes())) {
                            host = callLab.getHostE();
                        }
                        if ("I".equals(job.getNotes())) {
                            host = callLab.getHostI();
                        }
                        new DynamecMassage(host, callLab.getPort(), pR.toString().getBytes());
                        // �к�
                        /**
                         * CallNo call = new CallNo(); if (!call.init()) { falg
                         * = false; }
                         **/
                        String dateJH = StringTool.getString(sysDate, "yyyy-MM-dd HH:mm:ss");
                        int count = pR.getLabels().getItem().length;
                        for (int k = 0; k < count; k++) {
                            String[] sqlMedApply =
                                    new String[]{"UPDATE MED_APPLY SET PRINT_FLG='Y',OPT_DATE=SYSDATE,OPT_USER='"
                                            + Operator.getID()
                                            + "',OPT_TERM='"
                                            + Operator.getIP()
                                            + "' WHERE APPLICATION_NO='"
                                            + pR.getLabels().getItem(k).getBarcodelabel() + "'" };
                            TParm sqlParm = new TParm();
                            sqlParm.setData("SQL", sqlMedApply);
                            TParm actionParm =
                                    TIOM_AppServer.executeAction(actionName, "saveMedApply",
                                                                 sqlParm);
                            if (actionParm.getErrCode() < 0) {
                                this.messageBox("����" + pR.getSurname() + pR.getName() + "ҽ����ӡ״̬ʧ�ܣ�");
                                continue;
                            }
                            /**
                             * if(falg){ String s =
                             * call.SyncLisMaster(pR.getSpecimenid(),
                             * pR.getPatientid(),
                             * pR.getLabels().getItem(k).getMnemotests(),
                             * pR.getSurname() + pR.getName(), pR.getSex(),
                             * pR.getBdate(), pR.getNotes(), dateJH, "", "0",
                             * "2"); }
                             **/
                        }
                    }
                }
            }
        } else {
            this.messageBox("û����Ҫ��ӡ����Ŀ��");
            return;
        }
        this.messageBox("���ͳɹ���");
    }

	/**
	 * ��ѯ�������
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	public String getTubeType(String orderCode) {
		// "SYS_TUBECONVERT"
		String sqlSys = "SELECT TUBE_TYPE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sqlSys));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("TUBE_TYPE", 0);
	}

	/**
	 * �����ӡ
	 */
	public void onPrint() {
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		Set applicationNo = new LinkedHashSet();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// һ���ٴ�ָ����PK��Ѫҽ������
		String pkOrderCode = "";
		if (this.getPopedem("PIC")) {
			pkOrderCode = TConfig.getSystemValue("PIC_PK_ORDER_CODE");
		}
		
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			applicationNo.add(temp.getValue("APPLICATION_NO"));
		}
		if (applicationNo.size() > 0) {
			List printSize = new ArrayList();
			Iterator appNo = applicationNo.iterator();
			while (appNo.hasNext()) {
				String appNoStr = "" + appNo.next();
				StringBuffer orderDesc = new StringBuffer();
				String patName = "";
				String deptExCode = "";
				String orderDate = "";
				String stationCode = "";
				String optItemDesc = "";
				String deptCode = "";
				String urgentFlg = "";
				String mrNo = "";
				String sexDesc = "";
				String age = "";
				String devdesc = "";
				String applyNo = ""; // chenxi �����
				String drNote = ""; // chenxi ҽʦ��ע
				String filterNo = "";// ɸѡ��
				String planNo = "";// ������
				String orderCode = "";
				String cycle = "";// ����
				for (int i = 0; i < rowCount; i++) {
					TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
					if (!appNoStr.equals(temp.getValue("APPLICATION_NO")))
						continue;
					patName = temp.getValue("PAT_NAME");
					deptExCode = temp.getValue("EXEC_DEPT_CODE");
					deptCode = temp.getValue("DEPT_CODE");
					stationCode = temp.getValue("STATION_CODE");
					// =============== chenxi modify 20120709
					applyNo = temp.getValue("APPLICATION_NO");
					drNote = temp.getValue("DR_NOTE");
					String sql = "SELECT B.BED_NO_DESC FROM MED_APPLY A,SYS_BED B WHERE A.CAT1_TYPE='LIS' AND A.APPLICATION_NO ='"
							+ applyNo + "'" + "AND A.BED_NO=B.BED_NO ";//wanglong modify 20140610
					TParm selParm = new TParm(TJDODBTool.getInstance().select(
							sql));
					bedNo = selParm.getValue("BED_NO_DESC", 0);
					// ================== chenxi modify 20120709
					// devdesc=temp.getValue("DEV_CODE");
					urgentFlg = geturGentFlg(appNoStr).equals("Y") ? "(��)" : "";
					orderDate = String.valueOf(sysDate).substring(0, 19)
							.replaceAll("-", "/");
					optItemDesc = temp.getValue("OPTITEM_CHN_DESC");
					mrNo = temp.getValue("MR_NO");
					sexDesc = this.getDictionary("SYS_SEX", temp
							.getValue("SEX_CODE"));
					age = StringTool.CountAgeByTimestamp(temp
							.getTimestamp("BIRTH_DATE"), sysDate)[0];
					orderCode = temp.getValue("ORDER_CODE");
					cycle = this.getValueString("CYCLE");
					if (appNoStr.equals(temp.getValue("APPLICATION_NO"))) {
						orderDesc.append(temp.getValue("ORDER_DESC"));
					}
					
					if (this.getPopedem("PIC")) {
						// add by wangb 2016/08/03 һ���ٴ�ɸѡ��
						if (!"I".equals(this.getAdmType())) {
							filterNo = temp.getValue("STAFF_NO");
							planNo = temp.getValue("PLAN_NO");
						} else {
							// add by wangb 2016/8/8 һ���ٴ�סԺ�����߱��
							filterNo = MEDApplyTool.getInstance()
									.queryRecruitNo(temp.getValue("CASE_NO"));
							planNo = MEDApplyTool.getInstance().queryPlanNo(
									temp.getValue("CASE_NO"));
						}
					}
				}
				TParm printParm = new TParm();
				printParm.setData("APPLICATION_NO", "TEXT", appNoStr);
				if (StringUtils.isNotEmpty(filterNo)) {
					filterNo = filterNo + "-";
				}
				printParm.setData("PAT_NAME", "TEXT", filterNo + patName);
				printParm.setData("DEPT_CODE", "TEXT", deptCode);
				printParm.setData("STATION_CODE", "TEXT", stationCode);
				printParm.setData("URGENT_FLG", "TEXT", urgentFlg);
				printParm.setData("EXEC_DEPT_CODE", "TEXT", deptExCode);
				printParm.setData("ORDER_DATE", "TEXT", orderDate);
				printParm.setData("OPTITEM_CHN_DESC", "TEXT", optItemDesc);
				printParm.setData("ORDER_DESC", "TEXT", orderDesc.toString());
				printParm.setData("MR_NO", "TEXT", mrNo);
				printParm.setData("SEX_DESC", "TEXT", sexDesc);
				printParm.setData("AGE", "TEXT", age);
				// =============== chenxi
				printParm.setData("BED_NO", "TEXT", bedNo);
				printParm.setData("DR_NOTE", "TEXT", drNote);
				// ============== chenxi
				// add by wangb 2016/08/02 һ���ٴ�������
				printParm.setData("PLAN_NO", "TEXT", planNo);
				printParm.setData("ORDER_CODE", "TEXT", orderCode);
				printParm.setData("CYCLE", "TEXT", cycle);
				printSize.add(printParm);
			}
            int listRowCount = printSize.size();
            String paramSql =
                    "SELECT * FROM MED_PRINTER_LIST WHERE PRINTER_TERM='#'"
                            .replaceFirst("#", Operator.getIP());
            TParm printParam = new TParm(TJDODBTool.getInstance().select(paramSql));
            if (printParam.getErrCode() < 0) {
                this.messageBox("��ȡ��ӡ��������");
                return;
            }
            String printerPort = printParam.getValue("PRINTER_PORT", 0);
            boolean zebraFlg = printParam.getBoolean("ZEBRA_FLG", 0) && !printerPort.equals("");
            for (int i = 0; i < listRowCount; i++) {
                TParm pR = (TParm) printSize.get(i);
                pR.setData("EXEC_DEPT_CODE", "TEXT",
                           getDeptDesc(pR.getValue("EXEC_DEPT_CODE", "TEXT")));
                pR.setData("DEPT_CODE", "TEXT", getDeptDesc(pR.getValue("DEPT_CODE", "TEXT")) + "("
                        + getStationDesc(pR.getValue("STATION_CODE", "TEXT")) + ")");
                // this.messageBox_(pR);
                if (!zebraFlg) {// wanglong modify 20140610
                    pR = IReportTool.getInstance().getReportParm("Med_ApplyPrint.class", pR);//����ϲ� wanglong add 20141010
//                    this.openPrintDialog("%ROOT%\\config\\prt\\MED\\Med_ApplyPrint.jhw", pR, true);
                    // modify by wangb 2016/08/02 һ���ٴ�ʹ�õ�����������ʽ
                    if (this.getPopedem("PIC") && !this.getPopedem("H")) {
						// ��ѡҽ��ΪPK��Ѫ��Ŀ����ʹ��PKѪ������������ʽ
						if (pkOrderCode.contains(pR.getValue("ORDER_CODE", "TEXT"))) {
							this
									.openPrintDialog(
											"%ROOT%\\config\\prt\\MED\\Med_ApplyPrintForPK.jhw",
											pR, true);
						} else {
							this
									.openPrintDialog(
											"%ROOT%\\config\\prt\\MED\\Med_ApplyPrintForPIC.jhw",
											pR, true);
						}
                    } else {
                    	this.openPrintDialog(IReportTool.getInstance()
                                .getReportPath("Med_ApplyPrint.jhw"), pR, true);//����ϲ� wanglong add 20141010
                    }
                } else {
                    boolean noFontFlg = printParam.getBoolean("NOFONT_FLG", 0);// wanglong add
                                                                               // 20150410
                    offset_x = printParam.getInt("OFFSET_X", 0);
                    offset_y = printParam.getInt("OFFSET_y", 0);
                    this.printText = new StringBuffer();// wanglong add 20140610
                    this.addBarcode(101, 0, 2, 3.0, 95, pR.getValue("APPLICATION_NO", "TEXT"));
                    this.addText(5, 115, pR.getValue("MR_NO", "TEXT"));
                    if (!noFontFlg) {
                        this.addText(191, 115, pR.getValue("PAT_NAME", "TEXT"));
                        this.addText(271, 115, pR.getValue("SEX_DESC", "TEXT"));
                        // AGE
                        this.addText(310, 115, pR.getValue("BED_NO", "TEXT"));
                        this.addText(5, 139, "�ͼ���:" + pR.getValue("EXEC_DEPT_CODE", "TEXT"));
                        this.addText(191, 139, "����:" + pR.getValue("DEPT_CODE", "TEXT"));
                        // STATION_CODE
                        this.addText(5, 164, pR.getValue("ORDER_DESC", "TEXT"));
                        // OPTITEM_CHN_DESC
                        this.addText(5, 188, "����ʱ��:" + pR.getValue("ORDER_DATE", "TEXT"));
                        this.addText(5, 213, "ҽʦ��ע:" + pR.getValue("DR_NOTE", "TEXT"));
                        this.addText(350, 80, pR.getValue("URGENT_FLG", "TEXT"));// (��)
                    } else {// wanglong add 20150410
                        TParm parm = new TParm();
                        parm.setData(pR.getValue("PAT_NAME", "TEXT"), "");
                        parm.setData(pR.getValue("SEX_DESC", "TEXT"), "");
                        parm.setData(pR.getValue("BED_NO", "TEXT"), "");
                        parm.setData("�ͼ���:" + pR.getValue("EXEC_DEPT_CODE", "TEXT"), "");
                        parm.setData("����:" + pR.getValue("DEPT_CODE", "TEXT"), "");
                        parm.setData(pR.getValue("ORDER_DESC", "TEXT"), "");
                        parm.setData("����ʱ��:" + pR.getValue("ORDER_DATE", "TEXT"), "");
                        parm.setData("ҽʦ��ע:" + pR.getValue("DR_NOTE", "TEXT"), "");
                        parm.setData(pR.getValue("URGENT_FLG", "TEXT"), "");
                        TParm result =
                                TIOM_AppServer.executeAction("action.med.MedAction",
                                                             "getCHNControlCode", parm);
                        this.addGraphTextCode(171, 116,
                                              result.getValue(pR.getValue("PAT_NAME", "TEXT")));
                        this.addGraphTextCode(266, 116,
                                              result.getValue(pR.getValue("SEX_DESC", "TEXT")));
                        // AGE
                        this.addGraphTextCode(310, 116,
                                              result.getValue(pR.getValue("BED_NO", "TEXT")));
                        this.addGraphTextCode(5,
                                              140,
                                              result.getValue("�ͼ���:"
                                                      + pR.getValue("EXEC_DEPT_CODE", "TEXT")));
                        this.addGraphTextCode(171,
                                              140,
                                              result.getValue("����:"
                                                      + pR.getValue("DEPT_CODE", "TEXT")));
                        // STATION_CODE
                        this.addGraphTextCode(5, 164,
                                              result.getValue(pR.getValue("ORDER_DESC", "TEXT")));
                        // OPTITEM_CHN_DESC
                        this.addGraphTextCode(5,
                                              188,
                                              result.getValue("����ʱ��:"
                                                      + pR.getValue("ORDER_DATE", "TEXT")));
                        this.addGraphTextCode(5,
                                              212,
                                              result.getValue("ҽʦ��ע:"
                                                      + pR.getValue("DR_NOTE", "TEXT")));
                        this.addGraphTextCode(350, 80,
                                              result.getValue(pR.getValue("URGENT_FLG", "TEXT")));// (��)
                        
                        this.printText.append("^IDOUTSTR01^FS");//���ͼ��
                    }
                    if (!printBarCode(printerPort)) {// �������ӡ���� wanglong add 20140610
                        return;
                    }
                }
                String[] sqlMedApply =
                        new String[]{"UPDATE MED_APPLY SET PRINT_FLG='Y',PRINT_DATE=SYSDATE,OPT_DATE=SYSDATE,OPT_USER='"
                                + Operator.getID()
                                + "',OPT_TERM='"
                                + Operator.getIP()
                                + "' WHERE CAT1_TYPE = 'LIS' AND APPLICATION_NO='"//wanglong modify 20140610
                                + pR.getValue("APPLICATION_NO", "TEXT") + "'" };
                TParm sqlParm = new TParm();
                sqlParm.setData("SQL", sqlMedApply);
                TParm actionParm =
                        TIOM_AppServer.executeAction(actionName, "saveMedApply", sqlParm);
                if (actionParm.getErrCode() < 0) {
                    this.messageBox("����" + pR.getValue("PAT_NAME") + "ҽ����ӡ״̬ʧ�ܣ�");
                    return;
                }
                // �к�
                CallNo call = new CallNo();
                if (!call.init()) {
                    continue;
                }
                String dateJH = StringTool.getString(sysDate, "yyyy-MM-dd HH:mm:ss");
                String s =
                        call.SyncLisMaster(pR.getValue("APPLICATION_NO", "TEXT"),
                                           pR.getValue("MR_NO", "TEXT"),
                                           pR.getValue("ORDER_DESC", "TEXT"),
                                           pR.getValue("PAT_NAME", "TEXT"),
                                           pR.getValue("SEX_DESC", "TEXT"),
                                           pR.getValue("AGE", "TEXT"),
                                           pR.getValue("URGENT_FLG", "TEXT"), dateJH, "", "0", "2");
            }
        } else {
            this.messageBox("û����Ҫ��ӡ����Ŀ��");
            return;
        }
        // TParm parm = new TParm();
        // parm.setData("MED_APPLY", "TEXT", temp.getData("APPLICATION_NO"));
        // parm.setData("ORDER_DESC", "TEXT", temp.getData("ORDER_DESC"));
    }

	/**
	 * �õ��������뼱�����
	 */
    public String geturGentFlg(String appNoStr) {
        String flg = "";
        if (appNoStr.equals("")) return flg;
        String medsql =
                "SELECT CASE_NO,ORDER_NO,SEQ_NO,ADM_TYPE FROM MED_APPLY "
                        + " WHERE APPLICATION_NO='" + appNoStr + "' AND CAT1_TYPE='LIS'";
        TParm parm = new TParm(this.getDBTool().select(medsql));
        if (parm.getErrCode() < 0) return flg;
        if (parm.getCount() <= 0) return flg;
        String admType = "";
        String caseNo = "";
        String orderNo = "";
        String seqNo = "";
        if (parm.getCount() > 0) {
            String orderSql = "";
            admType = parm.getValue("ADM_TYPE", 0);
            caseNo = parm.getValue("CASE_NO", 0);
            orderNo = parm.getValue("ORDER_NO", 0);
            seqNo = String.valueOf(parm.getInt("SEQ_NO", 0));
            if (admType.equals("O") || admType.equals("E")) {
                orderSql =
                        " SELECT URGENT_FLG FROM OPD_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND RX_NO='" + orderNo + "' AND SEQ_NO='" + seqNo + "'";
            }
            if (admType.equals("I")) {
                orderSql =
                        " SELECT URGENT_FLG FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='" + seqNo + "'";
            }
            if (admType.equals("H")) {
                orderSql =
                        " SELECT URGENT_FLG FROM HRM_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND SEQ_NO='" + seqNo + "'";
            }
            TParm result = new TParm(this.getDBTool().select(orderSql));
            flg = result.getValue("URGENT_FLG", 0);
        }
        return flg;
    }

	/**
	 * �õ��������
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
    public String getCategory(String categoryCode) {
        String result = "";
        TParm parm =
                new TParm(this.getDBTool()
                        .select("SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE CATEGORY_CODE='"
                                        + categoryCode + "'"));
        result = parm.getValue("CATEGORY_CHN_DESC", 0);
        return result;
    }

	/**
	 * �õ��ֵ���Ϣ
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}

	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * ���
	 */
    public void onClear() {
        TRadioButton h = (TRadioButton) this.getComponent("H");
        String date =
                StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd") + "000000";
        Timestamp sysDate = StringTool.getTimestamp(date, "yyyyMMddHHmmss");
        if (this.getPopedem("NORMAL")) {
            this.onInit();
            if (h.isSelected()) {
                Date d = new Date(sysDate.getTime());
                String begin =
                        (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate()
                                + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // ��ʼ��ʱ��
                this.setValue("START_DATE", getTimestamp(begin));
                this.setValue("END_DATE", getTimestamp(end));
                clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO");// caowl
                                                                                                                       // ����COMPANY_CODE;CONTRACT_CODE
            } else {
                clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK");
                this.setValue("START_DATE", sysDate);
                this.setValue("END_DATE", SystemTool.getInstance().getDate());
                this.getTRadioButton("ONPRINT").setSelected(true);
            }
            
            this.getTTable(TABLE).removeRowAll();
        }
        if (this.getPopedem("SYSOPERATOR")) {
            this.onInit();
            if (h.isSelected()) {
                Date d = new Date(sysDate.getTime());
                String begin =
                        (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate()
                                + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // ��ʼ��ʱ��
                this.setValue("START_DATE", getTimestamp(begin));
                this.setValue("END_DATE", getTimestamp(end));
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO");

			}else{
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED");
				this.setValue("START_DATE", sysDate);
				this.setValue("END_DATE", SystemTool.getInstance().getDate());
				this.getTRadioButton("ONPRINT").setSelected(true);
			}
			
			this.getTTable(TABLE).removeRowAll();
        }
        if (this.getPopedem("SYSDBA")) {
            if (h.isSelected()) {
                Date d = new Date(sysDate.getTime());
                String begin =
                        (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate()
                                + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // ��ʼ��ʱ��
				this.setValue("START_DATE", getTimestamp(begin));
				this.setValue("END_DATE", getTimestamp(end));
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO");

			}else{
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED");
				this.getTRadioButton("ONPRINT").setSelected(true);
				this.setValue("START_DATE", sysDate);
			}
			
			this.setValue("END_DATE", SystemTool.getInstance().getDate());
			this.getTTable(TABLE).removeRowAll();
		}
        
        // add by wangb 2016/11/1 ���ʱһ����ȫ�ֱ���execBarCode���
        execBarCode = "";
        this.grabFocus("MR_NO");
	}

	    /**
     * ����
     */
    public void onRead() {
        // TParm patParm = jdo.ekt.EKTIO.getInstance().getPat();
        TParm patParm = jdo.ekt.EKTIO.getInstance().TXreadEKT();
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        this.setValue("MR_NO", patParm.getValue("MR_NO"));
        this.onQuery();
    }

	// $$===================add by lx 2011/02/13 ����̩�ĽкŽӿ�
	// start===========================$$//
	/**
	 * ���нӿ�-�Ŷ�
	 */
    public void onQueueCall() {
        // �����Ų���Ϊ��
        if (this.getValue("MR_NO").equals("")) {
            this.messageBox("�����벡���ţ�");
            return;
        }
        // TABLE���Ŷ���Ϣ����
        if (this.getTTable(TABLE).getRowCount() == 0) {
            this.messageBox("���Ŷ���Ϣ���ϣ�");
            return;
        }
        String msg = "";
        String mrNo = (String) this.getValue("MR_NO");
        msg += mrNo + "|";
        String sql =
                "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
        sql += " WHERE MR_NO='" + mrNo + "'";
        sql += " AND a.SEX_CODE=b.ID";
        // ͨ��mrNoȡ�Ա�����
        TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
        msg += patParm.getValue("PAT_NAME", 0) + "|";
        msg += patParm.getValue("SEX", 0) + "|";
        msg += patParm.getValue("BIRTH_DATE", 0) + "|";
        msg += Operator.getIP();
        TParm inParm = new TParm();
        inParm.setData("msg", msg);
        TIOM_AppServer.executeAction("action.device.CallNoAction", "doLabQueueCall", inParm);
        this.messageBox("�����Ŷ���Ϣ�ɹ���");
    }

	/**
	 * ���нӿ�-��һ��
	 */
	public void onNextCall() {
		String msg = "" + "|" + Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doLabNextCall", inParm);

		this.messageBox("�кųɹ���");
	}

	/**
	 * ���нӿ�-�ؽ�
	 */
	public void onReCall() {
		String msg = "" + "|" + Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doLabReCall", inParm);

		this.messageBox("�ؽгɹ���");

	}
	
	public void onQueryBar() {
		if (execBarCode.length() == 0) {
			execBarCode += "'" + this.getValueString("BAR_CODE") + "'";
		} else {
			execBarCode += "," + "'" + this.getValueString("BAR_CODE") + "'";
		}
		if (!this.getValueBoolean("APPLY_FLG")) {
			this.setValue("APPLY_FLG", "Y");// ����ִ�пؼ��Զ���ѡ
		}
		applyQuery();
		this.setValue("BAR_CODE", "");
		this.grabFocus("BAR_CODE");

	}
	
	
	public void applyQuery() {
		if (this.getValueString("MR_NO").trim().length() != 0) {
			this.setValue(
					"MR_NO",
					PatTool.getInstance().checkMrno(
							this.getValueString("MR_NO")));
			this.setValue(
					"PAT_NAME",
					PatTool.getInstance().getNameForMrno(
							PatTool.getInstance().checkMrno(
									this.getValueString("MR_NO"))));
			this.setMrNo(this.getValueString("MR_NO"));
			this.setPatName(this.getValueString("PAT_NAME"));
			this.setClinicareaCode(this.getValueString("CLINICAREA_CODEMED"));
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO",
						this.getValueString("MR_NO"), "O");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED",
						patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED",
						patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
				if (patInfParm.getValue("CLINICAREA_CODE").equals(null)
						|| "".equals(patInfParm.getValue("CLINICAREA_CODE"))) {
					this.setValue("CLINICAREA_CODEMED",
							this.getClinicareaCode());

				}
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO",
						this.getValueString("MR_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED",
						patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED",
						patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
				if (patInfParm.getValue("CLINICAREA_CODE").equals(null)
						|| "".equals(patInfParm.getValue("CLINICAREA_CODE"))) {
					this.setValue("CLINICAREA_CODEMED",
							this.getClinicareaCode());

				}
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO",
						this.getValueString("MR_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				// this.setValue("DEPT_CODEMED",
				// patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getApplyQuerySQL();
			// System.out.println("MR-SQL="+sql);
//			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
//				sql = getHRMQuerySQL();
//			}
			TParm action = new TParm(this.getDBTool().select(sql));
			culmulateAge(action);// ����ļ���
			this.getTTable(TABLE).setParmValue(action);
			
			// add by wangb 2016/11/1 ɨ���겡���Ź���õ������
			this.grabFocus("BAR_CODE");
			return;
		}
		String sql = getApplyQuerySQL();
//		if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
//			sql = getHRMQuerySQL();
//		}
		TParm action = new TParm(this.getDBTool().select(sql));
		// ���ݳ������ں͵�ǰ�����ڼ�������
		culmulateAge(action);// ����ļ���
		// if(action.getCount()<0){
		// this.messageBox("�������ݣ�");
		// return;
		// }
		this.getTTable(TABLE).setParmValue(action);
		// ÿ�β�ѯ���ȫѡ
		this.setValue("ALLCHECK", "N");
	}
	
	/**
	 * ����ִ�� yanjing 20140319
	 */
	public void onApply() {
		boolean have = false;
		boolean success = false;
		String user_id = Operator.getID();
		String now = SystemTool.getInstance().getDate().toString()
				.substring(0, 19);
		String sql = "";
		int rowCount = this.getTTable(TABLE).getRowCount();
		if (rowCount <= 0) {
			this.messageBox("û�����ݣ�");
			return;
		}
		
		// add by wangb 2016/11/1 һ���ٴ�����ִ�в��������룬ֱ�ӱ���
		if (!("H".equals(this.getAdmType()) && this.getPopedem("PIC"))) {
			// �����ж�
			if (!checkPW()) {
				return;
			}
		}

		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (temp.getBoolean("FLG")) {
				String mrNo = temp.getValue("MR_NO");
				String caseNo = temp.getValue("CASE_NO");
				String cat1_type = temp.getValue("CAT1_TYPE");
				String application_no = temp.getValue("APPLICATION_NO");
				String order_no = temp.getValue("ORDER_NO");
				String seq_no = temp.getValue("SEQ_NO");
				// ���±�med_apply
				sql = updateSql(mrNo, caseNo, cat1_type, application_no,
						order_no, seq_no, user_id, now);
				TParm resultParm = new TParm(TJDODBTool.getInstance().update(
						sql));
				have = true;
				success = true;
			}
		}
		if (!have) {
			this.messageBox("û��ѡ�����ݣ�");
			return;
		}
		if (success) {
			this.messageBox("����ɹ���");
			// ˢ��ҳ��
			onQuery();
			execBarCode = "";
			return;
		} else {
			this.messageBox("����ʧ�ܣ�");
			execBarCode = "";
			return;
		}
	}

	/**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "inwCheck";
		String value = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", inwCheck);
		if (value == null) {
			return false;
		}
		return value.equals("OK");
	}
	
	public String updateSql(String mr_no, String case_no, String cat1_type,
			String application_no, String order_no, String seq_no,
			String user_id, String now) {
		String sql = "update MED_APPLY set blood_user = '" + user_id + "'"
				+ " ,blood_date = TO_DATE('" + now
				+ "','YYYY/MM/DD HH24:MI:SS') " + " where mr_no = '" + mr_no
				+ "' and case_no = '" + case_no + "' " + " and cat1_type = '"
				+ cat1_type + "' and application_no = '" + application_no
				+ "' " + " and order_no = '" + order_no + "' and seq_no = '"
				+ seq_no + "' ";

		return sql;
	}
	
	/**
	 * ��������ķ��� yanjing 20140404
	 * 
	 */
	private void culmulateAge(TParm action) {
		// ���ݳ������ں͵�ǰ�����ڼ�������
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for (int i = 0; i < action.getCount(); i++) {
			TParm action1 = action.getRow(i);
			String age = "";
			if (!"".equals(action1.getTimestamp("BIRTH_DATE").toString())
					&& !action1.getTimestamp("BIRTH_DATE").toString()
							.equals(null)) {
				age = this.DateUtilshowAge(action1.getTimestamp("BIRTH_DATE"),
						sysDate);
			}
			action.setData("AGE", i, age);
		}
	}
	
	/**
	    * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲻ͬ����ʽ��ʾ����
	    * @param odo
	    * @return String ������ʾ������
	    */
	   public String DateUtilshowAge(Timestamp birth, Timestamp sysdate) {
		   //����ʱ�����ϵͳʱ��
		   if(birth.getTime()>sysdate.getTime()){
			  return ""; 
		   }
		   //
	       String strAge = "";
	       String[] res;
	       //res = CountAgeByTimestamp(birth, sysdate);
	       //modified by lx  ���㾫ȷ����
	       String times=DurationFormatUtils.formatPeriod(birth.getTime(), sysdate.getTime(),
			"y-M-d-H-m-s");
	       res=times.split("-");	      
	       //
	       if(isDebug){
	    	   System.out.println("-----age:----"+times);
	           for (String temp : res) {
		           System.out.println("----temp----\n" + temp);
		       }     
	       }
	      /* if (OPDSysParmTool.getInstance().isChild(birth)) {
	           age = res[0] + "��" + res[1] + "��" + res[2] + "��";
	       }
	       else {
	           age = (Integer.parseInt(res[0])==0?1:res[0]) + "��";
	       }*/
	       strAge=showAgeString(res);
	       
	       return strAge;
	   }
	   
	   /**
		 * ������������㴦��
		 * 
		 * @param res
		 * @return
		 */
		private static String showAgeString(String[] t) {
			String strAge = "";
			int nYear = Integer.valueOf(t[0]);
			int nMonth = Integer.valueOf(t[1]);
			int nDay = Integer.valueOf(t[2]);  //ȥ��+1
			int nHour = Integer.valueOf(t[3]);
			// int nMinuter=Integer.valueOf(t[4]);
			int nHour1 = (Integer.valueOf(t[2]) * 24 + nHour);
			//System.out.println("---nYear--"+nYear);
			//System.out.println("---nMonth--"+nMonth);
			//System.out.println("---nHour1--"+nHour1);
			
			// ����<=72Сʱ
			if ((nYear == 0 && nMonth == 0) && (nHour1 <= 72)) {
				if(nHour1==0){
					if (isDebug) {
						System.out.println("--strAge1--" + "1Сʱ");
					}
					return strAge = "1Сʱ";
				}
				strAge = nHour1 + "Сʱ";
				if (isDebug) {
					System.out.println("--strAge1--" + strAge);
				}
				return strAge;
			}
			// 72Сʱ<����<=28��
			if ((nYear == 0 && nMonth == 0) && (nHour1 > 72 && nDay <= 28)) {
				// ��ʾ0��0��n��
				strAge = nYear + "��" + nMonth + "��" + nDay + "��";
				;
				if (isDebug) {
					System.out.println("--strAge1--" + strAge);
				}
				return strAge;
			}

			// 28��<����<=2�� nMonth����0�������Σ���������
			if ((nYear == 0 && nMonth == 0 && nDay > 28)) {
				// ��ʾn��m��s��
				strAge = nYear + "��" + nMonth + "��" + nDay + "��";
				if (isDebug) {
					System.out.println("--strAge2_1--" + strAge);
				}
				return strAge;
			}

			if (nYear == 0 && nMonth > 0) {
				strAge = nYear + "��" + nMonth + "��" + nDay + "��";
				if (isDebug) {
					System.out.println("--strAge2_2--" + strAge);
				}
				return strAge;
			}
			if (nYear == 1 && nMonth > 0) {
				strAge = nYear + "��" + nMonth + "��" + nDay + "��";
				if (isDebug) {
					System.out.println("--strAge2_2--" + strAge);
				}
				return strAge;
			}
			 
			if ((nYear > 0 && nYear <= 2)&&nMonth == 0) {
				// ��ʾn��m��s��
				strAge = nYear + "��" + nMonth + "��" + nDay + "��";
				if (isDebug) {
					System.out.println("--strAge2_3--" + strAge);
				}
				return strAge;
			}

			// 2��<����<18��
			if ((nYear >= 2) && nYear < 18) {
				// ��ʾn��m��s��
				strAge = nYear + "��" + nMonth + "��";
				if (isDebug) {
					System.out.println("--strAge3--" + strAge);
				}
				return strAge;
			}

			// ����>=18�� ��ʾn��
			if (isDebug) {
				System.out.println("--strAge4--" + nYear + "��");
			}
			return nYear + "��";

		}
	
	/**
	 * �õ���ѯ���
	 * 
	 * @return String
	 */
	public String getApplyQuerySQL() {
		String sql = "SELECT ";
		if (this.getValueString("BAR_CODE").length() > 0) {
			sql += "'Y' AS FLG, ";
		} else {
			sql += "'N' AS FLG, ";
		}
		// ִ��״̬-δִ�л���ִ��
		if ("N".equals(getConfirmRadioValue())) {// δִ��
			sql += // wanglong modify 20140423 BED_NO��Ϊ��ʾBED_NO_DESC
			" A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO,A.PAT_NAME,A.BIRTH_DATE,A.APPLICATION_NO,A.RPTTYPE_CODE,A.OPTITEM_CODE,"
					+ "A.DEV_CODE,MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
					+ "A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE,A.ORDER_NO,A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,A.ADM_TYPE,TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE,"
					+ "A.BLOOD_USER,TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE,'δȷ��' AS BLOOD_STATE  FROM MED_APPLY A,SYS_FEE B ";
		} else if ("Y".equals(getConfirmRadioValue())) {// ��ִ��
			sql += // wanglong modify 20140423 BED_NO��Ϊ��ʾBED_NO_DESC
			" A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO,A.PAT_NAME,A.BIRTH_DATE,A.APPLICATION_NO,A.RPTTYPE_CODE,A.OPTITEM_CODE,"
					+ "A.DEV_CODE,A.MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
					+ "A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE,A.ORDER_NO,A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,A.ADM_TYPE,TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE,"
					+ "A.BLOOD_USER,TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE,'��ȷ��' AS BLOOD_STATE FROM MED_APPLY A,SYS_FEE B ";
		}
		// sql +=
		// " PRINT_FLG,DEPT_CODE,STATION_CODE,CLINICAREA_CODE,CLINICROOM_NO,BED_NO,PAT_NAME,APPLICATION_NO,RPTTYPE_CODE,OPTITEM_CODE,"
		// +
		// "DEV_CODE,MR_NO,IPD_NO,ORDER_DESC,CAT1_TYPE,OPTITEM_CHN_DESC,TO_CHAR(ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
		// +
		// "DR_NOTE,EXEC_DEPT_CODE,URGENT_FLG,SEX_CODE,BIRTH_DATE,ORDER_NO,SEQ_NO,TEL,ADDRESS,CASE_NO,ORDER_CODE,ADM_TYPE,PRINT_DATE,"
		// +
		// "BLOOD_USER,TO_CHAR(BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE, #  FROM MED_APPLY";
		TParm queryParm = this.getParmQuery();
		String columnName[] = queryParm.getNames();
		if (columnName.length > 0)
			sql += " WHERE A.ORDER_CODE = B.ORDER_CODE AND  ";
		int count = 0;

		for (String temp : columnName) {
			if (temp.equals("END_DATE"))
				continue;
			if (temp.equals("START_DATE")) {
				if (count > 0)
					sql += " AND ";
				sql += " A.START_DTTM BETWEEN TO_DATE('"
						+ queryParm.getValue("START_DATE")
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
						+ queryParm.getValue("END_DATE")
						+ "','YYYYMMDDHH24MISS')";
				count++;
				continue;
			}
			if (count > 0)
				sql += " AND ";
			sql += " A." + temp + "='" + queryParm.getValue(temp) + "' ";
			count++;
		}

		// ִ��״̬-δִ�л���ִ��
		if ("N".equals(getConfirmRadioValue())) {// δִ��
			sql += " AND A.BLOOD_USER IS NULL AND A.BLOOD_DATE IS NULL ";
		} else if ("Y".equals(getConfirmRadioValue())) {// ��ִ��
			sql += " AND A.BLOOD_USER IS NOT NULL AND A.BLOOD_DATE IS NOT NULL ";
		}
		// ҽ��-������
		if (this.getValueString("BAR_CODE").length() != 0) {
			sql += " AND A.APPLICATION_NO IN (" + execBarCode + ") ";
		}

		if (count > 0)
			sql += " AND ";
		if ("H".equals(getAdmTypeRadioValue()))
			sql += " A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY A.CAT1_TYPE,A.CASE_NO,A.APPLICATION_NO,A.SEQ_NO ";
		else if ("I".equals(getAdmTypeRadioValue()))
			sql += " A.CAT1_TYPE='LIS'  ORDER BY A.CAT1_TYPE,A.CASE_NO,A.BED_NO,A.APPLICATION_NO,A.SEQ_NO ";
		else
			sql += " A.CAT1_TYPE='LIS'  ORDER BY A.CAT1_TYPE,A.CASE_NO,BED_NO,A.APPLICATION_NO,A.SEQ_NO ";// BIL_FLG
																				// =
																				// 'Y'

		System.out.println("��ͨsql:"+sql);
		return sql;
	}
	
	/**
	 * �õ�ִ��״̬
	 * 
	 * @return String
	 */
	public String getConfirmRadioValue() {
		if (this.getTRadioButton("CONFIRM_N").isSelected())
			return "N";// δִ��
		if (this.getTRadioButton("CONFIRM_Y").isSelected())
			return "Y";// ��ִ��
		return "N";
	}


	// $$===================add by lx 2011/02/13
	// ����̩�ĽкŽӿ�END===========================$$//

	public String getAdmType() {
		return admType;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public String getClinicareaCode() {
		return clinicareaCode;
	}

	public String getClinicroomNo() {
		return clinicroomNo;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getStationCode() {
		return stationCode;
	}

	public String getBedNo() {
		return bedNo;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setClinicareaCode(String clinicareaCode) {
		this.clinicareaCode = clinicareaCode;
	}

	public void setClinicroomNo(String clinicroomNo) {
		this.clinicroomNo = clinicroomNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}
	//========add by wanglong 20130726
    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }
    //========add end
	/**
	 * �Ҽ�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table
				.setPopupMenuSyntax("��ʾ����ҽ��ϸ�� \n Display collection details with your doctor,openRigthPopMenu|TABLE");
	}

	/**
	 * ϸ��
	 */
	public void openRigthPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		// System.out.println("ѡ����:"+parm);
		TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(String orderCode) {
		TParm result = new TParm();
		String sql = "SELECT B.*,A.DOSAGE_QTY FROM SYS_ORDERSETDETAIL A,SYS_FEE B  WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDERSET_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
			result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
			result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
			// �����ܼ۸�
			double ownPrice = parm.getDouble("OWN_PRICE", i)
					* parm.getDouble("DOSAGE_QTY", i);
			result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
			result.addData("OWN_AMT", ownPrice);
			result
					.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",
							i));
			result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
			result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
		}
		return result;
	}

	/**
	 * �������ѡ���¼�
	 */
	public void onCompanyChoose() {// add by wanglong 20121213
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			return;
		}
		// ������������ø�����ĺ�ͬ����
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm.getErrCode() != 0) {
			this.messageBox_("û������");
		}
		// ����һ��TTextFormat,����ͬ���ֵ������ؼ���ȡ�����һ����ͬ���븳ֵ������ؼ���ʼֵ
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = contractParm.getValue("ID", 0);
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("��ѯʧ��");
			return;
		}
		contract.setValue(contractCode);

	}

	/**
	 * ��ͬ����ѡ���¼�
	 */
	public void onContractChoose() {// add by wanglong 20121213
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			this.messageBox_(companyCode);
			return;
		}
		contractCode = this.getValueString("CONTRACT_CODE");

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
				TParm tableData = getTTable(TABLE).getParmValue();
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
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
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
		getTTable(TABLE).setParmValue(parmTable);
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
     * ��ӡ������
     * @param port LPT�˿ں�
     * @return
     */
    public boolean printBarCode(String port) {// wanglong add 20140610
        this.printText.insert(0, "^XA");
        this.printText.append("^XZ");
//        System.out.println("----------------������---------"+this.printText.toString());
        synchronized (this.printText) { // ͬ�� �� ��ӡ��
            FileWriter fw = null;
            PrintWriter out = null;
            try {
                fw = new FileWriter(port); // ������LPT3
                out = new PrintWriter(fw);
                out.print(this.printText.toString());
                return true;
            }
            catch (IOException e) {
                this.messageBox("��ӡ�����Ҳ���ʹ��" + port + "�˿ڵĴ�ӡ��");
                e.printStackTrace();
                return false;
            }
            finally {
                out.close();
                try {
                    fw.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ��������Ŀ�����
     * @param x X����
     * @param y Y����
     * @param W ���
     * @param R Ratio
     * @param H �߶�
     * @param barCode ����
     */
    public void addBarcode(int x, int y, int W, double R, int H, String barCode) {
        // ^XA
        // ^FO35,5^BY2,3.0,50
        // ^BC
        // ^FD>;1404090017501^FS
        // ^XZ
        this.printText.append("^FO" + (x + offset_x) + "," + (y + offset_y) + "^BY" + W + "," + R
                + "," + H + "^BC^FD>;" + barCode + "^FS");
    }

    /**
     * ��������
     * @param x
     * @param y
     * @param str
     */
    public void addText(int x, int y, String str) {
        addText(x + offset_x, y + offset_y, 24, str);
    }

    /**
     * ��������
     * @param x
     * @param y
     * @param fontSize �����С
     * @param str
     */
    public void addText(int x, int y, int fontSize, String str) {// fontSizeĬ��24
        this.printText.append(getTextCode(x, y, fontSize, str));
    }

    /**
     * �������ֵĿ�����
     * @param x
     * @param y
     * @param fontSize
     * @param str
     * @return
     */
    public static String getTextCode(int x, int y, int fontSize, String str) {
        StringBuffer temp = new StringBuffer();
        try {
            for (int i = 0; i < str.length(); i++) {
                String s = str.substring(i, i + 1);
                byte[] ba = s.getBytes("GBK");
                if (ba.length == 1) {
                    temp.append("^CI0^FO" + x + "," + (y + 4) + "^A0N," + fontSize + "," + fontSize
                            + "^FD" + s + "^FS");
                    x += fontSize / 2;
                } else if (ba.length == 2) {
                    StringBuffer inTmp = new StringBuffer();
                    for (int j = 0; j < 2; j++) {
                        String hexStr = Integer.toHexString(ba[j] - 128);
                        hexStr = hexStr.substring(hexStr.length() - 2);
                        inTmp.append(hexStr + ")");
                    }
                    temp.append("^CI14^FO" + x + "," + y + "^AJN," + fontSize + "," + fontSize
                            + "^FH)^FD)" + inTmp + "^FS");
                    x += fontSize;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp.toString();
    }
    
    
    /**
     * ����ͼ�����ֵ�λ��
     * @param x
     * @param y
     * @param code
     */
    public void addGraphTextCode(int x, int y, String code) {// wanglong add 20150410
        this.printText.append("^FO" + (x + offset_x) + "," + (y + offset_y) + code + "^FS");
    }
}
