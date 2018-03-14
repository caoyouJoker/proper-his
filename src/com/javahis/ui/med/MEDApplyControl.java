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
 * <p> Title: 检验条码打印 </p>
 * 
 * <p> Description: 检验条码打印 </p>
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
	 * 动作类名称
	 */
	private String actionName = "action.med.MedAction";
	
	private static final boolean isDebug=false;

	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 科室
	 */
	private String deptCode;
	/**
	 * 门诊看诊日期住院为当前日期
	 */
	private Timestamp admDate;
	/**
	 * 就诊号
	 */
	private String caseNo = "";
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 病患姓名
	 */
	private String patName;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 床号
	 */
	private String bedNo;
	/**
	 * 病区
	 */
	private String stationCode;
	/**
	 * 诊区
	 */
	private String clinicareaCode;
	/**
	 * 诊室
	 */
	private String clinicroomNo;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	/**
	 * 团体代码、合同代码
	 */
	private String companyCode, contractCode;// add by wanglong 20121214

    /**
	 * 合同对象
	 */
	private HRMContractD contractD;// add by wanglong 20121214
	/**
	 * 合同TTextFormat
	 */
	private TTextFormat contract;// add by wanglong 20121214

	/**
	 *  条码控制码
	 */
	private StringBuffer printText = new StringBuffer();//wanglong add 20140610
    private int offset_x = 0;//wanglong add 20150410
    private int offset_y = 0;//wanglong add 20150410
    private String execBarCode = "";// 记录采样的条码号 yanjing 20140919
	TTextFormat clinicAreaCode;// 诊区add by yanjing 20151104
	private TTextFormat company;// 团体名称TTextFormat
	
	public void onInit() {
		super.onInit();
		contractD = new HRMContractD();// add by wanglong 20121214
		company = (TTextFormat) this.getComponent("COMPANY_CODE");
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		/**
		 * REG_CLINICAREA诊区 REG_CLINICROOM诊室 (住院COMBO权限)(门诊权限)(门急住别权限)
		 */
		// ================pangben modify 20110405 start 区域锁定
		setValue("REGION_CODE", Operator.getRegion());
		// ================pangben modify 20110405 stop
		// ========pangben modify 20110421 start 权限添加
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
					// 一般权限
					if ("1".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("NORMAL", true);
						this.setPopedem("SYSOPERATOR", false);
						this.setPopedem("SYSDBA", false);
					}
					// 角色权限
					if ("2".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSOPERATOR", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSDBA", false);
					}
					// 最高权限
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
			// 住院
			// this.setAdmType("I");
			// this.setDeptCode("10101");
			// this.setCaseNo("090918000012");
			// this.setMrNo("000000000209");
			// this.setPatName("住院者2");
			// this.setAdmDate(StringTool.getTimestamp("20091021","yyyyMMdd"));
			// this.setIpdNo("000000000091");
			// this.setStationCode("001");
			// this.setBedNo("0010110");
			// 门诊
			// this.setAdmType("H");
			// this.setDeptCode("10101");
			// this.setCaseNo("091118000005");
			// this.setMrNo("000000000232");
			// this.setPatName("刘磊");
			// this.setAdmDate(StringTool.getTimestamp("20091118","yyyyMMdd"));
			// this.setClinicareaCode("1");
			// this.setClinicroomNo("A01");
			// this.setAdmDate(StringTool.getTimestamp(new Date()));
		}
		/**
		 * 初始化权限
		 */
		onInitPopeDem();
		/**
		 * 初始化页面
		 */
		onApplyCheck();
		/**
		 * 初始化事件
		 */
		initEvent();
	}

	/**
	 * 初始化事件
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		// 排序监听
		addListener(getTTable(TABLE));
	}

	/**
	 * 初始化参数
	 */
	// public void onInitParameter(){
	// /**
	// * 1、一般权限(NORMAL)
	// * 2、角色权限(SYSOPERATOR)
	// * 2、最高权限(SYSDBA)
	// */
	// //一般权限
	// // this.setPopedem("NORMAL",true);
	// //角色权限
	// // this.setPopedem("SYSOPERATOR",true);
	// //最高权限
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
					// modify by wangb 2017/3/24 解决因点击焦点不准确导致合码项目勾选状态不一致的问题
					parm.setData("FLG", i, parm.getBoolean("FLG", row));
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * 选择事件
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;医师备注,100;科室,100,DEPT_CODE;病区,100,STATION_CODE;床号,100,BED_NO;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");
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
							"选,30,boolean;印,30,boolean;急,30,boolean;序号,50;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");//caowl 20130320 增加员工序号
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");//caowl 20130320 增加员工序号
		}
		this.onQuery();
	}

	/**
	 * 初始化权限
	 */
	public void onInitPopeDem() {
		if (this.getPopedem("NORMAL")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
		
		// modify by wangb 2017/3/16 一期临床健检条码打印与采样执行界面分离
		// add by wangb 2016/11/1 一期临床默认进入采样执行功能
		if ("H".equals(this.getAdmType()) && this.getPopedem("SAMPLING")
				&& StringUtils.isEmpty(getMrNo())) {
			this.setTitle("采样执行");
			this.setValue("APPLY_FLG", "Y");// 采样执行控件自动勾选
			this.getTRadioButton("YESPRINT").setSelected(true);// 默认选中已打印
			this.grabFocus("MR_NO");
		}
	}

	/**
	 * 初始化页面
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;医师备注,100;科室,100,DEPT_CODE;病区,100,STATION_CODE;床号,100,BED_NO;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DR_NOTE;DEPT_CODE;STATION_CODE;BED_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");
		}
		if ("H".equals(this.getAdmType())) {

			/** Yuanxm add 开始时间 与结束时间初始化 begin */
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/"
					+ d.getDate() + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
					+ d.getDate() + " 23:59:59";

			// 初始化时间
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			/** Yuanxm add 开始时间 与结束时间初始化 end */

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
					.setHeader("选,30,boolean;印,30,boolean;急,30,boolean;序号,50;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;住院号,100");//caowl 20130320 增加序号一列
			this
					.getTTable(TABLE)
					.setParmMap("FLG;PRINT_FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;IPD_NO");//caowl 20130320 增加序号一列
		}
		// 查询
		this.onQuery();
	}
	
	/**
	 * 采样执行控件事件
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

			// add by yangjj 20150319 隐藏采样确认panel
			TPanel panel = (TPanel) this.getComponent("tPanel_0");
			panel.setVisible(false);

			// add by yangjj 20150319隐藏采样执行menu
			callFunction("UI|apply|setVisible", false);

			// add by wangb 2016/08/10 一期临床打印PK血条码需填写试验周期
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
		clinicAreaCode = (TTextFormat) this.getComponent("CLINICAREA_CODEMED");// 执行科室
		TextFormatRegClinicArea combo_clinicarea = (TextFormatRegClinicArea) this
				.getComponent("CLINICAREA_CODEMED");
		combo_clinicarea.setDrCode(Operator.getID());
		combo_clinicarea.onQuery();
		
		// add by wangb 2016/08/03 团体名称需要根据不同登录角色筛选
		String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
        
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
	}
	
	/**
	 * 初始化页面
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
		if ("O".equals(this.getAdmType())) {// 添加“开单医生”列ORDER_DR，放在诊区前面，科室与报告类别列的宽度变小，医嘱列放大
											// 前2个各减20 医嘱加40
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE)
					.setHeader(
							"选,30,boolean;急,30,boolean;姓名,70;病案号,100;科室,90,DEPT_CODE;年龄,80;试管颜色,120,TUBE_TYPE;医嘱名称,200;医嘱开立时间,140;开单医生,70;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;条码号,100;报告类别,60,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;采样人员,70,USER_CODE;采样时间,140;采样执行状态,100;打印时间,140");
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
							"选,30,boolean;急,30,boolean;姓名,100;病案号,100;科室,90,DEPT_CODE;年龄,80;试管颜色,120,TUBE_TYPE;医嘱名称,200;医嘱开立时间,140;开单医生,70;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;条码号,100;报告类别,60,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;采样人员,70,USER_CODE;采样时间,140;采样执行状态,100;打印时间,140");
			this.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}

		if ("H".equals(this.getAdmType())) {

			/** Yuanxm add 开始时间 与结束时间初始化 begin */
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/"
					+ d.getDate() + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
					+ d.getDate() + " 23:59:59";
			// 初始化时间
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			/** Yuanxm add 开始时间 与结束时间初始化 end */

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
//							"选,30,boolean;急,30,boolean;序号,50;姓名,100;病案号,100;科室,90,DEPT_CODE;年龄,80;试管颜色,120,TUBE_TYPE;医嘱名称,160;医嘱开立时间,140;科室,90,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140;采样执行状态,100;打印时间,140");// caowl
//																																																																																			// 20130320
//																																																																																			// 增加序号一列
//			this.getTTable(TABLE)
//					.setParmMap(
//							"FLG;URGENT_FLG;NO;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");// caowl
			this
				.getTTable(TABLE)
				.setHeader("选,30,boolean;印,30,boolean;序号,50;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,100,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140;采样执行状态,100");//caowl 20130320 增加序号一列
			this
				.getTTable(TABLE)
				.setParmMap("FLG;PRINT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;BLOOD_STATE");//caowl 20130320 增加序号一列
																																																																							// 增加序号一列
		}
		// 查询
		// this.onQuery();
	}
	

	public static Timestamp getTimestamp(String time) {
		Date date = new Date();
		// 注意format的格式要与日期String的格式相匹配
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
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 拿到TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 返回TRadonButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * 拿到TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		boolean queryFlg = false;
		queryFlg = this.getValueBoolean("APPLY_FLG");
		if (queryFlg) {
			this.applyQuery();// 采样条码勾选
			return ;
		}
		if (this.getValueString("MR_NO").trim().length() != 0) {
			
			//modify by huangtt 20160927 EMPI患者查重提示  start
			Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
			String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
		          this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
		    }
			this.setValue("MR_NO", pat.getMrNo());
			this.setValue("PAT_NAME", pat.getName());

//			this.setValue("MR_NO", PatTool.getInstance().checkMrno(
//					this.getValueString("MR_NO")));
//			this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
//					PatTool.getInstance().checkMrno(
//							this.getValueString("MR_NO"))));
			
			//modify by huangtt 20160927 EMPI患者查重提示  end
			
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
		// 每次查询清空全选
		this.setValue("ALLCHECK", "N");
	}

	/**
	 * 全选
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
	 * 得到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * 返回查询病患结果
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
	 * 得到查询语句
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
        // System.out.println("普通sql:"+sql);
        return sql;
    }

	/**
	 * 得到查询参数
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
	 * 得到健检的查询语句
	 * 
	 * @return String
	 */
	public String getHRMQuerySQL() {// add by wanglong 20121214
		String sql = "SELECT 'N' AS FLG, A.PRINT_FLG,  A.DEPT_CODE, A.STATION_CODE, A.CLINICAREA_CODE,"
				+ "          A.CLINICROOM_NO, A.PAT_NAME, A.APPLICATION_NO, A.RPTTYPE_CODE, A.OPTITEM_CODE, "
				+ "          A.DEV_CODE, A.MR_NO,A.IPD_NO, A.ORDER_DESC, A.CAT1_TYPE,A.OPTITEM_CHN_DESC, "
				+ "          TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,"
				+ "          A.EXEC_DEPT_CODE, A.URGENT_FLG,A.SEX_CODE, A.BIRTH_DATE,A.ORDER_NO, A.SEQ_NO, "
				+ "          A.TEL, A.ADDRESS,A.CASE_NO,A.ORDER_CODE, A.ADM_TYPE, A.PRINT_DATE,C.SEQ_NO AS NO, "//caowl 20130320 增加员工序号
				+ "          C.STAFF_NO,C.PLAN_NO "// add by wangb 2016/08/03 为一期临床增加筛选号及方案号
				+ "     FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C"
				+ "    WHERE A.APPLICATION_NO = B.MED_APPLY_NO "
				+ "      AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "      AND A.ORDER_NO = B.CASE_NO "
				+ "       AND A.SEQ_NO = B.SEQ_NO  "
				+ "      AND B.MR_NO = C.MR_NO ";
        sql += " AND A.ADM_TYPE = 'H' AND B.SETMAIN_FLG='Y' ";// 门级别
        String srartDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// 开始时间
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");// 结束时间
        sql +=
                " AND A.START_DTTM BETWEEN TO_DATE('" + srartDate + "','YYYYMMDDHH24MISS') "
                        + "                     AND TO_DATE('" + endDate + "','YYYYMMDDHH24MISS') ";
        if (this.getValueString("REGION_CODE").length() != 0) {// 区域
            sql += " AND A.REGION_CODE = '" + this.getValueString("REGION_CODE") + "'";
        }
        if (this.getValueString("DEPT_CODEMED").length() != 0) {// 科室
            sql += " AND A.DEPT_CODE = '" + this.getValueString("DEPT_CODEMED") + "'";
        }
        if (getPrintStatus().length() != 0) {// 打印状态（未打印、已打印、全部）
            sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
        }
        if (this.getValueString("MR_NO").length() != 0) {// 病案号
//        	sql += " AND A.MR_NO = '" + this.getValueString("MR_NO") + "'";
            sql += " AND A.MR_NO IN (" + PatTool.getInstance().getMrRegMrNos(this.getValueString("MR_NO"))  + ")";
        }
        if (this.getValueString("COMPANY_CODE").length() != 0) {// 团体号
            sql += " AND C.COMPANY_CODE = '" + this.getValueString("COMPANY_CODE") + "'";
        }
        if (this.getValueString("CONTRACT_CODE").length() != 0) {// 合同号
            sql += " AND C.CONTRACT_CODE = '" + this.getValueString("CONTRACT_CODE") + "'";
        }
        if (this.getValueString("START_SEQ_NO").length() != 0) {// 员工序号开始
            sql += " AND C.SEQ_NO >= '" + this.getValueString("START_SEQ_NO") + "'";
        }
        if (this.getValueString("END_SEQ_NO").length() != 0) {// 员工序号结束
            sql += " AND C.SEQ_NO <= '" + this.getValueString("END_SEQ_NO") + "'";
        }
		sql += " AND A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO ASC ,A.CAT1_TYPE ASC,A.START_DTTM ASC, A.CASE_NO ASC,A.APPLICATION_NO,A.SEQ_NO";// caowl
																																	// 20130305
																																	// 按日期升序排列
//		System.out.println("健康检查sql:"+sql);
		return sql;
	}

	/**
	 * 得到门急住别值
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
	 * 得到打印状态
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
	 * 根据病患生日和传入的截至日期，计算病人年龄并根据是否为儿童以不同的形式显示年龄，如是儿童则显示X岁X月X日，如成人则显示x岁
	 * 
	 * @param odo
	 * @return String 界面显示的年龄
	 */
	public static String showAge(Timestamp birth, Timestamp sysdate) {
		String age = "";
		String[] res;
		res = StringTool.CountAgeByTimestamp(birth, sysdate);
		if (OPDSysParmTool.getInstance().isChild(birth, sysdate)) {//wanglong modify 20150119
			age = res[0] + "岁" + res[1] + "月";
		} else {
			age = (Integer.parseInt(res[0]) == 0 ? 1 : res[0]) + "岁";
		}
		// System.out.println("age" + age);
		return age;
	}

	/**
	 * 传送条码机
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
                            // 试管类别
                            item.setTubeconvert(getTubeType(temp.getValue("ORDER_CODE")));
                            // 备注
                            item.setNotelabel(geturGentFlg(appNoStr).equals("Y") ? "(急)" : "");
                            // 条码
                            item.setBarcodelabel(appNoStr);
                            // 条码序号
                            item.setVariouslabel(temp.getValue("SEQ_NO"));
                            // 时间
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
                        // 叫号
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
                                this.messageBox("更新" + pR.getSurname() + pR.getName() + "医嘱打印状态失败！");
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
            this.messageBox("没有需要打印的项目！");
            return;
        }
        this.messageBox("传送成功！");
    }

	/**
	 * 查询条码类别
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
	 * 条码打印
	 */
	public void onPrint() {
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		Set applicationNo = new LinkedHashSet();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// 一期临床指定的PK采血医嘱代码
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
				String applyNo = ""; // chenxi 条码号
				String drNote = ""; // chenxi 医师备注
				String filterNo = "";// 筛选号
				String planNo = "";// 方案号
				String orderCode = "";
				String cycle = "";// 周期
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
					urgentFlg = geturGentFlg(appNoStr).equals("Y") ? "(急)" : "";
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
						// add by wangb 2016/08/03 一期临床筛选号
						if (!"I".equals(this.getAdmType())) {
							filterNo = temp.getValue("STAFF_NO");
							planNo = temp.getValue("PLAN_NO");
						} else {
							// add by wangb 2016/8/8 一期临床住院受试者编号
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
				// add by wangb 2016/08/02 一期临床方案号
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
                this.messageBox("获取打印参数错误");
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
                    pR = IReportTool.getInstance().getReportParm("Med_ApplyPrint.class", pR);//报表合并 wanglong add 20141010
//                    this.openPrintDialog("%ROOT%\\config\\prt\\MED\\Med_ApplyPrint.jhw", pR, true);
                    // modify by wangb 2016/08/02 一期临床使用单独的条码样式
                    if (this.getPopedem("PIC") && !this.getPopedem("H")) {
						// 所选医嘱为PK采血项目，则使用PK血单独的条码样式
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
                                .getReportPath("Med_ApplyPrint.jhw"), pR, true);//报表合并 wanglong add 20141010
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
                        this.addText(5, 139, "送检组:" + pR.getValue("EXEC_DEPT_CODE", "TEXT"));
                        this.addText(191, 139, "科室:" + pR.getValue("DEPT_CODE", "TEXT"));
                        // STATION_CODE
                        this.addText(5, 164, pR.getValue("ORDER_DESC", "TEXT"));
                        // OPTITEM_CHN_DESC
                        this.addText(5, 188, "采样时间:" + pR.getValue("ORDER_DATE", "TEXT"));
                        this.addText(5, 213, "医师备注:" + pR.getValue("DR_NOTE", "TEXT"));
                        this.addText(350, 80, pR.getValue("URGENT_FLG", "TEXT"));// (急)
                    } else {// wanglong add 20150410
                        TParm parm = new TParm();
                        parm.setData(pR.getValue("PAT_NAME", "TEXT"), "");
                        parm.setData(pR.getValue("SEX_DESC", "TEXT"), "");
                        parm.setData(pR.getValue("BED_NO", "TEXT"), "");
                        parm.setData("送检组:" + pR.getValue("EXEC_DEPT_CODE", "TEXT"), "");
                        parm.setData("科室:" + pR.getValue("DEPT_CODE", "TEXT"), "");
                        parm.setData(pR.getValue("ORDER_DESC", "TEXT"), "");
                        parm.setData("采样时间:" + pR.getValue("ORDER_DATE", "TEXT"), "");
                        parm.setData("医师备注:" + pR.getValue("DR_NOTE", "TEXT"), "");
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
                                              result.getValue("送检组:"
                                                      + pR.getValue("EXEC_DEPT_CODE", "TEXT")));
                        this.addGraphTextCode(171,
                                              140,
                                              result.getValue("科室:"
                                                      + pR.getValue("DEPT_CODE", "TEXT")));
                        // STATION_CODE
                        this.addGraphTextCode(5, 164,
                                              result.getValue(pR.getValue("ORDER_DESC", "TEXT")));
                        // OPTITEM_CHN_DESC
                        this.addGraphTextCode(5,
                                              188,
                                              result.getValue("采样时间:"
                                                      + pR.getValue("ORDER_DATE", "TEXT")));
                        this.addGraphTextCode(5,
                                              212,
                                              result.getValue("医师备注:"
                                                      + pR.getValue("DR_NOTE", "TEXT")));
                        this.addGraphTextCode(350, 80,
                                              result.getValue(pR.getValue("URGENT_FLG", "TEXT")));// (急)
                        
                        this.printText.append("^IDOUTSTR01^FS");//清除图型
                    }
                    if (!printBarCode(printerPort)) {// 控制码打印条码 wanglong add 20140610
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
                    this.messageBox("更新" + pR.getValue("PAT_NAME") + "医嘱打印状态失败！");
                    return;
                }
                // 叫号
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
            this.messageBox("没有需要打印的项目！");
            return;
        }
        // TParm parm = new TParm();
        // parm.setData("MED_APPLY", "TEXT", temp.getData("APPLICATION_NO"));
        // parm.setData("ORDER_DESC", "TEXT", temp.getData("ORDER_DESC"));
    }

	/**
	 * 得到检验条码急做标记
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
	 * 拿到仪器类别
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
	 * 拿到字典信息
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
	 * 拿到科室
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
	 * 拿到科室
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
	 * 清空
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
                                + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // 初始化时间
                this.setValue("START_DATE", getTimestamp(begin));
                this.setValue("END_DATE", getTimestamp(end));
                clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO");// caowl
                                                                                                                       // 增加COMPANY_CODE;CONTRACT_CODE
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
                                + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // 初始化时间
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
                                + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // 初始化时间
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
        
        // add by wangb 2016/11/1 清空时一并将全局变量execBarCode清空
        execBarCode = "";
        this.grabFocus("MR_NO");
	}

	    /**
     * 读卡
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

	// $$===================add by lx 2011/02/13 加入泰心叫号接口
	// start===========================$$//
	/**
	 * 呼叫接口-排队
	 */
    public void onQueueCall() {
        // 病案号不能为空
        if (this.getValue("MR_NO").equals("")) {
            this.messageBox("请输入病案号！");
            return;
        }
        // TABLE无排队信息资料
        if (this.getTTable(TABLE).getRowCount() == 0) {
            this.messageBox("无排队信息资料！");
            return;
        }
        String msg = "";
        String mrNo = (String) this.getValue("MR_NO");
        msg += mrNo + "|";
        String sql =
                "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
        sql += " WHERE MR_NO='" + mrNo + "'";
        sql += " AND a.SEX_CODE=b.ID";
        // 通过mrNo取性别及生日
        TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
        msg += patParm.getValue("PAT_NAME", 0) + "|";
        msg += patParm.getValue("SEX", 0) + "|";
        msg += patParm.getValue("BIRTH_DATE", 0) + "|";
        msg += Operator.getIP();
        TParm inParm = new TParm();
        inParm.setData("msg", msg);
        TIOM_AppServer.executeAction("action.device.CallNoAction", "doLabQueueCall", inParm);
        this.messageBox("发送排队信息成功！");
    }

	/**
	 * 呼中接口-下一个
	 */
	public void onNextCall() {
		String msg = "" + "|" + Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doLabNextCall", inParm);

		this.messageBox("叫号成功！");
	}

	/**
	 * 呼中接口-重叫
	 */
	public void onReCall() {
		String msg = "" + "|" + Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doLabReCall", inParm);

		this.messageBox("重叫成功！");

	}
	
	public void onQueryBar() {
		if (execBarCode.length() == 0) {
			execBarCode += "'" + this.getValueString("BAR_CODE") + "'";
		} else {
			execBarCode += "," + "'" + this.getValueString("BAR_CODE") + "'";
		}
		if (!this.getValueBoolean("APPLY_FLG")) {
			this.setValue("APPLY_FLG", "Y");// 采样执行控件自动勾选
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
			culmulateAge(action);// 年龄的计算
			this.getTTable(TABLE).setParmValue(action);
			
			// add by wangb 2016/11/1 扫描完病案号光标置到条码框
			this.grabFocus("BAR_CODE");
			return;
		}
		String sql = getApplyQuerySQL();
//		if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
//			sql = getHRMQuerySQL();
//		}
		TParm action = new TParm(this.getDBTool().select(sql));
		// 根据出生日期和当前的日期计算年龄
		culmulateAge(action);// 年龄的计算
		// if(action.getCount()<0){
		// this.messageBox("查无数据！");
		// return;
		// }
		this.getTTable(TABLE).setParmValue(action);
		// 每次查询清空全选
		this.setValue("ALLCHECK", "N");
	}
	
	/**
	 * 采样执行 yanjing 20140319
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
			this.messageBox("没有数据！");
			return;
		}
		
		// add by wangb 2016/11/1 一期临床采样执行不输入密码，直接保存
		if (!("H".equals(this.getAdmType()) && this.getPopedem("PIC"))) {
			// 密码判断
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
				// 更新表med_apply
				sql = updateSql(mrNo, caseNo, cat1_type, application_no,
						order_no, seq_no, user_id, now);
				TParm resultParm = new TParm(TJDODBTool.getInstance().update(
						sql));
				have = true;
				success = true;
			}
		}
		if (!have) {
			this.messageBox("没有选中数据！");
			return;
		}
		if (success) {
			this.messageBox("保存成功！");
			// 刷新页面
			onQuery();
			execBarCode = "";
			return;
		} else {
			this.messageBox("保存失败！");
			execBarCode = "";
			return;
		}
	}

	/**
	 * 调用密码验证
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
	 * 计算年龄的方法 yanjing 20140404
	 * 
	 */
	private void culmulateAge(TParm action) {
		// 根据出生日期和当前的日期计算年龄
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
	    * 根据病患生日和传入的截至日期，计算病人年龄不同的形式显示年龄
	    * @param odo
	    * @return String 界面显示的年龄
	    */
	   public String DateUtilshowAge(Timestamp birth, Timestamp sysdate) {
		   //出生时间大于系统时间
		   if(birth.getTime()>sysdate.getTime()){
			  return ""; 
		   }
		   //
	       String strAge = "";
	       String[] res;
	       //res = CountAgeByTimestamp(birth, sysdate);
	       //modified by lx  计算精确年龄
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
	           age = res[0] + "岁" + res[1] + "月" + res[2] + "日";
	       }
	       else {
	           age = (Integer.parseInt(res[0])==0?1:res[0]) + "岁";
	       }*/
	       strAge=showAgeString(res);
	       
	       return strAge;
	   }
	   
	   /**
		 * 爱育华年龄计算处理
		 * 
		 * @param res
		 * @return
		 */
		private static String showAgeString(String[] t) {
			String strAge = "";
			int nYear = Integer.valueOf(t[0]);
			int nMonth = Integer.valueOf(t[1]);
			int nDay = Integer.valueOf(t[2]);  //去掉+1
			int nHour = Integer.valueOf(t[3]);
			// int nMinuter=Integer.valueOf(t[4]);
			int nHour1 = (Integer.valueOf(t[2]) * 24 + nHour);
			//System.out.println("---nYear--"+nYear);
			//System.out.println("---nMonth--"+nMonth);
			//System.out.println("---nHour1--"+nHour1);
			
			// 年龄<=72小时
			if ((nYear == 0 && nMonth == 0) && (nHour1 <= 72)) {
				if(nHour1==0){
					if (isDebug) {
						System.out.println("--strAge1--" + "1小时");
					}
					return strAge = "1小时";
				}
				strAge = nHour1 + "小时";
				if (isDebug) {
					System.out.println("--strAge1--" + strAge);
				}
				return strAge;
			}
			// 72小时<年龄<=28天
			if ((nYear == 0 && nMonth == 0) && (nHour1 > 72 && nDay <= 28)) {
				// 显示0岁0月n天
				strAge = nYear + "岁" + nMonth + "月" + nDay + "天";
				;
				if (isDebug) {
					System.out.println("--strAge1--" + strAge);
				}
				return strAge;
			}

			// 28天<年龄<=2岁 nMonth月是0的情况如何？？？？？
			if ((nYear == 0 && nMonth == 0 && nDay > 28)) {
				// 显示n岁m月s天
				strAge = nYear + "岁" + nMonth + "月" + nDay + "天";
				if (isDebug) {
					System.out.println("--strAge2_1--" + strAge);
				}
				return strAge;
			}

			if (nYear == 0 && nMonth > 0) {
				strAge = nYear + "岁" + nMonth + "月" + nDay + "天";
				if (isDebug) {
					System.out.println("--strAge2_2--" + strAge);
				}
				return strAge;
			}
			if (nYear == 1 && nMonth > 0) {
				strAge = nYear + "岁" + nMonth + "月" + nDay + "天";
				if (isDebug) {
					System.out.println("--strAge2_2--" + strAge);
				}
				return strAge;
			}
			 
			if ((nYear > 0 && nYear <= 2)&&nMonth == 0) {
				// 显示n岁m月s天
				strAge = nYear + "岁" + nMonth + "月" + nDay + "天";
				if (isDebug) {
					System.out.println("--strAge2_3--" + strAge);
				}
				return strAge;
			}

			// 2岁<年龄<18岁
			if ((nYear >= 2) && nYear < 18) {
				// 显示n岁m月s天
				strAge = nYear + "岁" + nMonth + "月";
				if (isDebug) {
					System.out.println("--strAge3--" + strAge);
				}
				return strAge;
			}

			// 年龄>=18岁 显示n岁
			if (isDebug) {
				System.out.println("--strAge4--" + nYear + "岁");
			}
			return nYear + "岁";

		}
	
	/**
	 * 得到查询语句
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
		// 执行状态-未执行或已执行
		if ("N".equals(getConfirmRadioValue())) {// 未执行
			sql += // wanglong modify 20140423 BED_NO改为显示BED_NO_DESC
			" A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO,A.PAT_NAME,A.BIRTH_DATE,A.APPLICATION_NO,A.RPTTYPE_CODE,A.OPTITEM_CODE,"
					+ "A.DEV_CODE,MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
					+ "A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE,A.ORDER_NO,A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,A.ADM_TYPE,TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE,"
					+ "A.BLOOD_USER,TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE,'未确认' AS BLOOD_STATE  FROM MED_APPLY A,SYS_FEE B ";
		} else if ("Y".equals(getConfirmRadioValue())) {// 已执行
			sql += // wanglong modify 20140423 BED_NO改为显示BED_NO_DESC
			" A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO,A.PAT_NAME,A.BIRTH_DATE,A.APPLICATION_NO,A.RPTTYPE_CODE,A.OPTITEM_CODE,"
					+ "A.DEV_CODE,A.MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
					+ "A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE,A.ORDER_NO,A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,A.ADM_TYPE,TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE,"
					+ "A.BLOOD_USER,TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE,'已确认' AS BLOOD_STATE FROM MED_APPLY A,SYS_FEE B ";
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

		// 执行状态-未执行或已执行
		if ("N".equals(getConfirmRadioValue())) {// 未执行
			sql += " AND A.BLOOD_USER IS NULL AND A.BLOOD_DATE IS NULL ";
		} else if ("Y".equals(getConfirmRadioValue())) {// 已执行
			sql += " AND A.BLOOD_USER IS NOT NULL AND A.BLOOD_DATE IS NOT NULL ";
		}
		// 医嘱-条形码
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

		System.out.println("普通sql:"+sql);
		return sql;
	}
	
	/**
	 * 得到执行状态
	 * 
	 * @return String
	 */
	public String getConfirmRadioValue() {
		if (this.getTRadioButton("CONFIRM_N").isSelected())
			return "N";// 未执行
		if (this.getTRadioButton("CONFIRM_Y").isSelected())
			return "Y";// 已执行
		return "N";
	}


	// $$===================add by lx 2011/02/13
	// 加入泰心叫号接口END===========================$$//

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
	 * 右键
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table
				.setPopupMenuSyntax("显示集合医嘱细相 \n Display collection details with your doctor,openRigthPopMenu|TABLE");
	}

	/**
	 * 细项
	 */
	public void openRigthPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		// System.out.println("选中行:"+parm);
		TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
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
			// 计算总价格
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
	 * 团体代码选择事件
	 */
	public void onCompanyChoose() {// add by wanglong 20121213
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			return;
		}
		// 根据团体代码查得该团体的合同主项
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm.getErrCode() != 0) {
			this.messageBox_("没有数据");
		}
		// 构造一个TTextFormat,将合同主项赋值给这个控件，取得最后一个合同代码赋值给这个控件初始值
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = contractParm.getValue("ID", 0);
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("查询失败");
			return;
		}
		contract.setValue(contractCode);

	}

	/**
	 * 合同代码选择事件
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
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				 //System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	
    /**
     * 打印控制码
     * @param port LPT端口号
     * @return
     */
    public boolean printBarCode(String port) {// wanglong add 20140610
        this.printText.insert(0, "^XA");
        this.printText.append("^XZ");
//        System.out.println("----------------控制码---------"+this.printText.toString());
        synchronized (this.printText) { // 同步 送 打印机
            FileWriter fw = null;
            PrintWriter out = null;
            try {
                fw = new FileWriter(port); // 数据送LPT3
                out = new PrintWriter(fw);
                out.print(this.printText.toString());
                return true;
            }
            catch (IOException e) {
                this.messageBox("打印错误：找不到使用" + port + "端口的打印机");
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
     * 生成条码的控制码
     * @param x X坐标
     * @param y Y坐标
     * @param W 宽度
     * @param R Ratio
     * @param H 高度
     * @param barCode 条码
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
     * 增加文字
     * @param x
     * @param y
     * @param str
     */
    public void addText(int x, int y, String str) {
        addText(x + offset_x, y + offset_y, 24, str);
    }

    /**
     * 增加文字
     * @param x
     * @param y
     * @param fontSize 字体大小
     * @param str
     */
    public void addText(int x, int y, int fontSize, String str) {// fontSize默认24
        this.printText.append(getTextCode(x, y, fontSize, str));
    }

    /**
     * 生成文字的控制码
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
     * 设置图形文字的位置
     * @param x
     * @param y
     * @param code
     */
    public void addGraphTextCode(int x, int y, String code) {// wanglong add 20150410
        this.printText.append("^FO" + (x + offset_x) + "," + (y + offset_y) + code + "^FS");
    }
}
