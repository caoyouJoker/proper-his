package jdo.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.device.CallNo;
import jdo.hl7.Hl7Communications;
import jdo.ind.INDTool;
import jdo.opb.OPBTool;
import jdo.opd.OrderTool;
import jdo.opd.TotQtyTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.pha.PHAStrike;
import com.dongyang.util.TSystem;
import jdo.pha.PhaSysParmTool;
import jdo.sys.SYSSQL;
import jdo.sys.DeptTool;

/**
 * 
 * <p>
 * Title: 医嘱存储对象
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author ehui 2009.2.11
 * @version 1.0
 */
public class OpdOrder extends StoreBase {
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 医师
	 */
	private String drCode;
	/**
	 * 部门
	 */
	private String deptCode;
	/**
	 * 服务等级
	 */
	private String serviceLevel;
	/**
	 * 判断是否只有新增医嘱 没有修改和删除医嘱
	 */
	boolean isNew = false;
	//private Compare compare = new Compare();
	// 取得药房柜台号
	private String GET_COUNTER_NO = "SELECT * FROM PHA_COUNTERNO WHERE ORG_CODE='#' AND CHOSEN_FLG='Y' ";
	// 取得是否为精二药品
	private String GET_PSYCHOPHA2 = "SELECT B.PSYCHOPHA2_FLG FROM PHA_BASE A,SYS_CTRLDRUGCLASS B WHERE A.ORDER_CODE='#' AND A.CTRLDRUGCLASS_CODE=B.CTRLDRUGCLASS_CODE AND B.PSYCHOPHA2_FLG='Y'";
	// 取得集合医嘱
	private String GET_ORDERSET = "SELECT A.*,B.DOSAGE_QTY,B.HIDE_FLG FROM SYS_FEE A,SYS_ORDERSETDETAIL B WHERE A.ORDER_CODE=B.ORDER_CODE AND B.ORDERSET_CODE='#'";
	// 取得SYS_FEE数据
	public String GET_SYS_FEE = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='#'";
	// 取得医嘱的执行状态
	private String GET_EXEC_FLG = "SELECT RECEIPT_NO,BILL_FLG,BILL_TYPE,EXEC_FLG,RECEIPT_FLG,PRINT_FLG FROM OPD_ORDER WHERE CASE_NO='#' AND RX_NO='#' AND SEQ_NO=#";
	//查看检验检查医嘱是否已经登记=====pangben 2013-1-9
	private String GET_STATUS_FLG="SELECT STATUS FROM MED_APPLY WHERE CAT1_TYPE='#' AND APPLICATION_NO='#'";
	/**
	 * MED_APPLY_NO集合，保存、查詢時清空
	 */
	private Map labMap;
	/**
	 * 是否进入setItem方法注记
	 */
	public boolean itemNow = false;
	/**
	 * MED_APPLY
	 */
	private MedApply med;
	/**
	 * sys_fee的数据
	 */
	public TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");

	/**
	 * 得到病案号
	 * 
	 * @return String
	 */
	public String getMrNo() {
		return this.mrNo;
	}

	/**
	 * 设置病案号
	 * 
	 * @param mrNo
	 *            String
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	/**
	 * 得到就诊号
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}

	/**
	 * 设置就诊号
	 * 
	 * @param caseNo
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	/**
	 * 设置门急住别
	 * 
	 * @param admType
	 *            String
	 */
	public void setAdmType(String admType) {
		this.admType = admType;
	}

	/**
	 * 得到门急住别
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * 设置医师
	 * 
	 * @param drCode
	 *            String
	 */
	public void setDrCode(String drCode) {
		this.drCode = drCode;
	}

	/**
	 * 得到医师
	 * 
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	/**
	 * 设置部门
	 * 
	 * @param deptCode
	 *            String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	/**
	 * 得到部门
	 * 
	 * @return String
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * 设置服务等级
	 * 
	 * @param service_level
	 *            String
	 */
	public void setServiceLevel(String service_level) {
		this.serviceLevel = service_level;
	}

	/**
	 * 等到服务等级
	 * 
	 * @return String
	 */
	public String getServiceLevel() {
		return this.serviceLevel;
	}

	/**
	 * 得到MED_APPLY
	 * 
	 * @return
	 */
	public MedApply getMedApply() {
		if (med == null) {
			med = new MedApply();
			med.onQueryByCaseNo(this.getCaseNo());
		}
		return med;
	}

	/**
	 * 重啟MED_APPLY
	 */
	public void resetMedApply() {
		med = new MedApply();
		med.onQueryByCaseNo(this.getCaseNo());
	}

	/**
	 * 得到MAP
	 * 
	 * @return
	 */
	public Map getLabMap() {
		if (this.labMap == null) {
			labMap = new HashMap();
		}
		return labMap;
	}

	/**
	 * 清空MAP
	 */
	public void resetMap() {
		this.labMap = new HashMap();
	}

	/**
	 * 得到SQL
	 * 
	 * @return String
	 */
	protected String getQuerySQL() {
		return "SELECT 'Y' FLG ,OPD_ORDER.* FROM OPD_ORDER WHERE CASE_NO = '" + getCaseNo()
				+ "' AND RX_TYPE<>'0' ORDER BY RX_TYPE,LINK_NO,RX_NO,SEQ_NO";
	}

	/**
	 * 判断是否只有新增医嘱 没有修改和删除医嘱
	 * 
	 * @return boolean
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * 插入数据
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public int insertRow(int row) {
		int newRow = super.insertRow(row);
		if (newRow == -1)
			return -1;
		// 设置门急住别
		setItem(newRow, "ADM_TYPE", getAdmType());
		setItem(newRow, "DR_CODE", getDrCode());
		setItem(newRow, "DEPT_CODE", getDeptCode());
		setItem(newRow, "CASE_NO", getCaseNo());
		setItem(newRow, "MR_NO", getMrNo());
		setItem(newRow, "REGION_CODE", Operator.getRegion());
		setItem(newRow, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
		return newRow;
	}

	/**
	 * 设置操作用户
	 * 
	 * @param optUser
	 *            String 操作用户
	 * @param optDate
	 *            Timestamp 操作时间
	 * @param optTerm
	 *            String 操作IP
	 * @return boolean true 成功 false 失败
	 */
	public boolean setOperator(String optUser, Timestamp optDate, String optTerm) {
		String storeName = isFilter() ? FILTER : PRIMARY;
		int rows[] = getNewRows(storeName);
		for (int i = 0; i < rows.length; i++)
			setItem(rows[i], "ORDER_DATE", optDate, storeName);
		return super.setOperator(optUser, optDate, optTerm);
	}

	/**
	 * 得到取号原则产生的新的处方号
	 * 
	 * @return String
	 */
	public String getNewRx() {
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			// err("取处方号失败");
			// //System.out.println("取处方号失败");
			return null;
		}
		return rxNo;
	}

	/**
	 * 为处方签列表Combo提供数据
	 * 
	 * @param RXType
	 * @return String[]
	 */
	public String[] getRx(String RXType) {
		if (RXType == null || RXType.length() == 0)
			return new String[] {};
		ArrayList list = new ArrayList();
		String storeName = isFilter() ? FILTER : PRIMARY;
		TParm data = getBuffer(storeName);
		// System.out.println("data==========="+data);
		int count = data.getCount();
		for (int i = 0; i < count; i++) {
			if (!RXType.equals(data.getValue("RX_TYPE", i)))
				continue;
			String rxNo = data.getValue("RX_NO", i);
			if (list.indexOf(rxNo) < 0) {
				list.add(rxNo);
			}
		}
		String rxName = "";
		if ("en".equalsIgnoreCase(Operator.getLanguage())) {
			rxName = "】 Rx";
		} else {
			rxName = "】 处方签";
		}
		for (int i = 0; i < list.size(); i++) {

			String s = (String) list.get(i);
			// //System.out.println("s=:"+s);
			list.set(i, s + ",【" + (i + 1) + rxName);
		}
		// list.add(0,",");
		// System.out.println("list==="+list);
		return (String[]) list.toArray(new String[] {});
	}

	/**
	 * 得到其他列数据
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		double tot = 0.0;
		// System.out.println("getddddddddddOtherColumnValue.parm==="+parm);
		if ("PAYAMOUNT".equalsIgnoreCase(column)) {

			String setCode = this.getItemString(row, "ORDERSET_CODE");
			String rxNo = this.getItemString(row, "RX_NO");
			int groupNo = this.getItemInt(row, "ORDERSET_GROUP_NO");
			//double own_price = 0.0;
			//double ar_price = 0.0;
			String filterString = getFilter();
			this.setFilter("RX_NO='" + rxNo + "' AND  ORDERSET_CODE='"
					+ setCode + "' AND ORDERSET_GROUP_NO= " + groupNo
					+ " AND SETMAIN_FLG='N'");
			// 如果是处置的话，有可能是单项医嘱
			if ("4".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& StringUtil.isNullString(setCode)) {
				this.setFilter("RX_NO='" + rxNo + "' AND ORDER_CODE='"
						+ this.getItemString(row, "ORDER_CODE")
						+ "' AND SEQ_NO = '"
						+ this.getItemString(row, "SEQ_NO") + "'");
			}
			this.filter();
			double ownPriceEach = 0.0;
			double arPriceEach = 0.0;
			for (int i = 0; i < this.rowCount(); i++) {

				ownPriceEach += this.getItemDouble(i, "OWN_AMT");
				arPriceEach += this.getItemDouble(i, "AR_AMT");
			}
			this.setFilter(filterString);
			this.filter();
			return ownPriceEach - arPriceEach;
		}
		if ("OWN_PRICE_MAIN".equalsIgnoreCase(column)
				|| "OWN_AMT_MAIN".equalsIgnoreCase(column)
				|| "AR_AMT_MAIN".equalsIgnoreCase(column)) {
			String setCode = this.getItemString(row, "ORDERSET_CODE");
			String rxNo = this.getItemString(row, "RX_NO");
			int groupNo = this.getItemInt(row, "ORDERSET_GROUP_NO");
			//double mainMedi = this.getItemDouble(row, "MEDI_QTY");// 主项的用量
			String filterString = getFilter();
			this.setFilter("RX_NO='" + rxNo + "' AND  ORDERSET_CODE='"
					+ setCode + "' AND ORDERSET_GROUP_NO= " + groupNo
					+ " AND SETMAIN_FLG='N'");
			// 如果是处置的话，有可能是单项医嘱
			if ("4".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& StringUtil.isNullString(setCode)) {
				this.setFilter("RX_NO='" + rxNo + "' AND ORDER_CODE='"
						+ this.getItemString(row, "ORDER_CODE")
						+ "' AND SEQ_NO = '"
						+ this.getItemString(row, "SEQ_NO") + "'");
			}
			this.filter();
			for (int i = 0; i < this.rowCount(); i++) {
				double ownPriceEach = 0.0;
				if ("AR_AMT_MAIN".equalsIgnoreCase(column)) {
					ownPriceEach = this.getItemDouble(i, "AR_AMT");
				} else if ("OWN_AMT_MAIN".equalsIgnoreCase(column)) {
					ownPriceEach = this.getItemDouble(i, "OWN_AMT");
				} else if ("OWN_PRICE_MAIN".equalsIgnoreCase(column)) {
					// 如果是集合医嘱 那么细项的单价需要乘以数量 来作为主项的单价显示
					if (!StringUtil.isNullString(setCode)) {
						double osTot = this.getOrderSetTot(this.getItemString(
								i, "ORDER_CODE"), setCode);
						ownPriceEach = this.getItemDouble(i, "OWN_PRICE")
								* osTot;
					} else
						ownPriceEach = this.getItemDouble(i, "OWN_PRICE");
				}
				tot += ownPriceEach;
			}
			this.setFilter(filterString);
			this.filter();
			return tot;
		}
		if ("ORDER_ENG_DESC".equalsIgnoreCase(column)) {
			TParm sysFeeParm = sysFee
					.getBuffer(sysFee.isFilter() ? sysFee.FILTER
							: sysFee.PRIMARY);
			Vector orderCode = (Vector) sysFeeParm.getData("ORDER_CODE");
			String code = this.getItemString(row, "ORDER_CODE");
			int rowNow = orderCode.indexOf(code);
			if (rowNow < 0)
				return "";
			return sysFeeParm.getValue("GOODS_DESC", rowNow);
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(column)) {
			TParm sysFeeParm = sysFee
					.getBuffer(sysFee.isFilter() ? sysFee.FILTER
							: sysFee.PRIMARY);
			Vector orderCode = (Vector) sysFeeParm.getData("ORDER_CODE");
			String code = this.getItemString(row, "ORDER_CODE");
			// //System.out.println("row="+row+",code="+code);
			int rowNow = orderCode.indexOf(code);
			if (rowNow < 0) {
				return "";
			}
			String desc = "";
			if ("en".equalsIgnoreCase(Operator.getLanguage())) {
				desc = sysFeeParm.getValue("TRADE_ENG_DESC", rowNow) + " "
						+ sysFeeParm.getValue("SPECIFICATION", rowNow);
			} else {
				desc = sysFeeParm.getValue("ORDER_DESC", rowNow) + " "
						+ sysFeeParm.getValue("SPECIFICATION", rowNow);
			}
			return desc;
		}
		if ("ORDER_DESC1".equalsIgnoreCase(column)) {
			int index = row * 4 + 0;
			return getItemString(index, "ORDER_DESC");
		}
		if ("ORDER_DESC2".equalsIgnoreCase(column)) {
			int index = row * 4 + 1;
			return getItemString(index, "ORDER_DESC");
		}
		if ("ORDER_DESC3".equalsIgnoreCase(column)) {
			int index = row * 4 + 2;
			return getItemString(index, "ORDER_DESC");
		}
		if ("ORDER_DESC4".equalsIgnoreCase(column)) {
			int index = row * 4 + 3;
			return getItemString(index, "ORDER_DESC");
		}
		// ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		/*
		 * ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;
		 * ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;
		 * ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;
		 * ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		 */
		if ("MEDI_QTY1".equalsIgnoreCase(column)) {
			// System.out.println("here in mediQty1");
			int index = row * 4 + 0;
			// System.out.println("parm.getRow"+parm.getRow(index));
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("MEDI_QTY2".equalsIgnoreCase(column)) {
			int index = row * 4 + 1;
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("MEDI_QTY3".equalsIgnoreCase(column)) {
			int index = row * 4 + 2;
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("MEDI_QTY4".equalsIgnoreCase(column)) {
			int index = row * 4 + 3;
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("DCTEXCEP_CODE1".equalsIgnoreCase(column)) {
			int index = row * 4 + 0;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if ("DCTEXCEP_CODE2".equalsIgnoreCase(column)) {
			int index = row * 4 + 1;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if ("DCTEXCEP_CODE3".equalsIgnoreCase(column)) {
			int index = row * 4 + 2;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if ("DCTEXCEP_CODE4".equalsIgnoreCase(column)) {
			int index = row * 4 + 3;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		return "";
	}

	/**
	 * 初始化中药
	 * 
	 * @param row
	 * @param parm
	 */
	public void initOrder(int row, TParm parm) {
		setItem(row, "PRESRT_NO", row + 1);
		setItem(row, "REGION_CODE", Operator.getRegion());
		setItem(row, "RELEASE_FLG", "N");
		setItem(row, "LINKMAIN_FLG", "N");
		setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC").replaceFirst(
				"(" + parm.getValue("SPECIFICATION") + ")", ""));
		setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
		setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE"));
		setItem(row, "CHARGE_HOSP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		setItem(row, "REXP_CODE", BIL.getRexpCode(parm
				.getValue("CHARGE_HOSP_CODE"), this.getAdmType()));
		setItem(row, "SETMAIN_FLG", "N");
		setItem(row, "ORDERSET_GROUP_NO", 0);
		double TAKE_QTY = parm.getDouble("MEDI_QTY", 0);
		setItem(row, "MEDI_QTY", 1.0);
		setItem(row, "DISPENSE_QTY", 1.0);
		setItem(row, "DOSAGE_QTY", 1.0);
		setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
		setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			return;
		}

		//double takeQty = (TAKE_QTY < 1.0) ? 1.0 : TAKE_QTY;
		setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
		setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE", 0));
		String TAKE_DAY = parmBase.getValue("TAKE_DAYS", 0);
		int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 || "0"
				.equalsIgnoreCase(TAKE_DAY)) ? 1 : Integer.valueOf(TAKE_DAY);
		setItem(row, "TAKE_DAYS", takedays);
		setItem(row, "CTRLDRUGCLASS_CODE", parmBase.getValue(
				"CTRLDRUGCLASS_CODE", 0));
		setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG", 0));
		if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
			setItem(row, "DOSAGE_UNIT", parmBase.getValue("STOCK_UNIT", 0));
			setItem(row, "DISPENSE_UNIT", parmBase.getValue("STOCK_UNIT", 0));
			setItem(row, "MEDI_UNIT", parmBase.getValue("STOCK_UNIT", 0));
		} else {
			setItem(row, "DOSAGE_UNIT", parmBase.getValue("DOSAGE_UNIT", 0));
			setItem(row, "DISPENSE_UNIT", parmBase.getValue("DOSAGE_UNIT", 0));
			setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT", 0));
		}

	}

	/**
	 * 
	 * @param rxType
	 *            处方类型
	 * @return String 新处方签号
	 */
	public String newPrsrp(String rxType) {
		String rxNo = getNewRx();
		if (rxNo == null)
			return "";
		if (newOrder(rxType, rxNo) == -1)
			return "";
		return rxNo;
	}

	public boolean deletePrsrp(String rxType) {

		return true;
	}

	/**
	 * 新增一条医嘱
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxNo
	 *            处方号
	 * @return int 新增行号
	 */
	public int newOrder(String rxType, String rxNo) {
		int row = this.insertRow(-1);
		setItem(row, "RX_NO", rxNo);
		setItem(row, "RX_TYPE", rxType);
		setItem(row, "SETMAIN_FLG", "");
		setItem(row, "BILL_TYPE", "C");// 默认是现金流程
		if ("3".equalsIgnoreCase(rxType)) {
			setItem(row, "PHA_TYPE", "G");
		}

		int max = 0;
		for (int i = 0; i < this.rowCount(); i++) {
			if (!rxNo.equals(getItemString(i, "RX_NO")))
				continue;
			if (!rxType.equals(getItemData(i, "RX_TYPE")))
				continue;
			int seqNo = getItemInt(i, "SEQ_NO");
			if (seqNo > max)
				max = seqNo;
		}
		max++;
		setItem(row, "SEQ_NO", max);
		// this.setFilter(filterString);
		// this.filter();
		return row;
	}

	/**
	 * 插入一条处置，如该处置为集合医嘱，则插入集合医嘱，如为单项，则插入单项
	 * 
	 * @param rxNo
	 * @param orderCode
	 * @return
	 */
	public int newOpOrder(String rxNo, String orderCode, String[] ctz, int row) {
		if (StringUtil.isNullString(rxNo) || StringUtil.isNullString(orderCode)) {
			this.deleteRow(row);
			return -1;
		}
		String sql = GET_ORDERSET.replaceFirst("#", orderCode);
		TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
		if (orderSet.getErrCode() != 0) {
			// System.out.println("newOpOrder.getErrSql="+orderSet.getErrText());
			// System.out.println("wrong Sql="+sql);
			this.deleteRow(row);
			return -1;
		}
		sql = GET_SYS_FEE.replaceFirst("#", orderCode);
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(sql));
		if (sysFee.getErrCode() != 0 || sysFee.getCount() != 1) {
			this.deleteRow(row);
			return -1;
		}
		sysFee = sysFee.getRow(0);
		int count = orderSet.getCount();
		//TParm sys = new TParm();
		if (count <= 0) {// 非集合医嘱
			if (!insertRowParm(sysFee, row, ctz)) {
				this.deleteRow(row);
				return -1;
			}
			this.setItem(row, "SETMAIN_FLG", "N");
			this.setItem(row, "HIDE_FLG", "N");// 隐藏注记
			this.setActive(row, true);
		} else {// 集合医嘱
			if (!insertRowParm(sysFee, row, ctz)) {
				this.deleteRow(row);
				return -1;
			}
			this.setActive(row, true);
			int groupNo = getMaxGroupNo();
			this.setItem(row, "SETMAIN_FLG", "Y");
			this.setItem(row, "HIDE_FLG", "N");// 隐藏注记
			String orderSetCode = orderCode;
			this.setItem(row, "ORDERSET_CODE", orderSetCode);
			this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			for (int i = 0; i < count; i++) {
				row = this.newOrder("4", rxNo);
				orderCode = orderSet.getValue("ORDER_CODE", i);
				sql = GET_SYS_FEE.replaceFirst("#", orderCode);
				sysFee = new TParm(TJDODBTool.getInstance().select(sql));
				if (sysFee.getErrCode() != 0 || sysFee.getCount() != 1) {
					this.deleteRow(row);
					return -1;
				}
				sysFee = sysFee.getRow(0);
				insertRowParm(sysFee, row, ctz);
				this.setItem(row, "SETMAIN_FLG", "N");
				this.setItem(row, "HIDE_FLG", orderSet.getData("HIDE_FLG", i));
				this.setItem(row, "ORDERSET_CODE", orderSetCode);
				this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				this
						.setItem(row, "MEDI_QTY", orderSet.getData(
								"DOSAGE_QTY", i));
				this.setItem(row, "DOSAGE_QTY", orderSet.getData("DOSAGE_QTY",
						i));
				this.setItem(row, "DISPENSE_QTY", orderSet.getData(
						"DOSAGE_QTY", i));
				double ownAmt = roundAmt(StringTool.round(this.getItemDouble(
						row, "OWN_PRICE")
						* this.getItemDouble(row, "DOSAGE_QTY"), 2));
				double arAmt = roundAmt(BIL
						.chargeTotCTZ(ctz[0], ctz[1], ctz[2], sysFee
								.getValue("ORDER_CODE"), this.getItemDouble(
								row, "DOSAGE_QTY"), serviceLevel));
				// DISCOUNT_RATE
				this.setItem(row, "DISCOUNT_RATE", BIL.getOwnRate(ctz[0],
						ctz[1], ctz[2], sysFee
								.getValue("CHARGE_HOSP_CODE", row), sysFee
								.getValue("ORDER_CODE")));
				this.setItem(row, "OWN_AMT", ownAmt);
				// System.out.println("ownAmt="+ownAmt);
				this.setItem(row, "AR_AMT", arAmt);
				// System.out.println("arAmt="+arAmt);
				this.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
				this.setActive(row, true);
			}
		}
		return row;
	}

	/**
	 * 返回最大集合医嘱序号+1
	 * 
	 * @return int
	 */
	public int getMaxGroupNo() {
		int count = this.rowCount();
		int groupNo = 0;
		int oldNo = 0;
		for (int i = 0; i < count; i++) {
			if (!"Y".equalsIgnoreCase(this.getItemString(i, "SETMAIN_FLG")))
				continue;

			groupNo = this.getItemInt(i, "ORDERSET_GROUP_NO");
			if (groupNo > oldNo)
				oldNo = groupNo;

		}
		oldNo += 1;
		return oldNo;
	}

	/**
	 * 根据给入数据修改一行数据
	 * 
	 * @param sysFee
	 * @param row
	 * @return
	 */
	private boolean insertRowParm(TParm sysFee, int row, String[] ctz) {
		if (row < 0) {
			return false;
		}
		if (sysFee == null
				|| StringUtil.isNullString(sysFee.getValue("ORDER_CODE"))) {
			return false;
		}
		Timestamp now = this.getDBTime();
		// ORDER_CODE
		this.setItem(row, "ORDER_CODE", sysFee.getValue("ORDER_CODE"));
		// ORDER_DESC
		this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
		// GOODS_DESC
		this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
		// SPECIFICATION
		this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
		// ORDER_CAT1_CODE
		this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
		// CAT1_TYPE
		this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
		// DESCRIPTION
		this.setItem(row, "DR_NOTE", sysFee.getValue("DESCRIPTION"));
		this.setItem(row, "CTZ1_CODE", ctz[0]);
		this.setItem(row, "CTZ2_CODE", ctz[1]);
		this.setItem(row, "CTZ3_CODE", ctz[2]);
		this.itemNow = true;
		// MEDI_QTY
		this.setItem(row, "MEDI_QTY", 1);
		// MEDI_UNIT
		this.setItem(row, "MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
		this.itemNow = true;
		// FREQ_CODE
		this.setItem(row, "FREQ_CODE", "PRN");
		// ROUTE_CODE
		this.setItem(row, "ROUTE_CODE", "");
		this.itemNow = true;
		// TAKE_DAYS
		this.setItem(row, "TAKE_DAYS", 1);
		this.itemNow = true;
		// DOSAGE_QTY
		this.setItem(row, "DOSAGE_QTY", 1);
		// DOSAGE_UNIT
		this.setItem(row, "DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
		this.itemNow = true;
		// DISPENSE_QTY
		this.setItem(row, "DISPENSE_QTY", 1);
		// DISPENSE_UNIT
		this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("UNIT_CODE"));
		// OWN_PRICE
		if ("2".equals(serviceLevel)) {
			this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE2"));
		} else if ("3".equals(serviceLevel)) {
			this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE3"));
		} else
			this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE"));
		// NHI_PRICE
		this.setItem(row, "GOODS_DESC", sysFee.getData("GOODS_DESC"));
		// System.out.println("chargeHospCode="+sysFee.getValue("CHARGE_HOSP_CODE"));
		// System.out.println("rexpCode="+BIL.getRexpCode(sysFee
		// .getValue("CHARGE_HOSP_CODE")));
		this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
		this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee
				.getValue("CHARGE_HOSP_CODE"), this.getAdmType()));

		// DR_CODE
		this.setItem(row, "DR_CODE", Operator.getID());
		// ORDER_DATE
		this.setItem(row, "ORDER_DATE", now);
		String dept = sysFee.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(dept)) {
			dept = Operator.getDept();
		}
		// EXEC_DEPT_CODE
		this.setItem(row, "EXEC_DEPT_CODE", dept);
		// zhangyong20110616
		// COST_CENTER_CODE
		this.setItem(row, "COST_CENTER_CODE", dept);
		// DEGREE_CODE
		this.setItem(row, "DEGREE_CODE", sysFee.getValue("DEGREE_CODE"));
		// INSPAY_TYPE
		this.setItem(row, "INSPAY_TYPE", "C");
		// EXPENSIVE_FLG
		// REXP_CODE
		this.setItem(row, "GOODS_DESC", sysFee.getValue("ORDER_DESC"));
		// HEXP_CODE
		this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
		// CAT1_TYPE
		this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
		// TRADE_ENG_DESC
		this.setItem(row, "TRADE_ENG_DESC", sysFee.getValue("TRADE_ENG_DESC"));
		// System.out.println("dosageQty="+this.getItemDouble(row,
		// "DOSAGE_QTY"));
		// System.out.println("ownPrice="+this.getItemDouble(row, "OWN_PRICE"));
		double ownAmt = roundAmt(StringTool.round(this.getItemDouble(row,
				"OWN_PRICE")
				* this.getItemDouble(row, "DOSAGE_QTY"), 2));
		double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2], sysFee
				.getValue("ORDER_CODE"), this.getItemDouble(row, "DOSAGE_QTY"),
				serviceLevel));
		// DISCOUNT_RATE
		this.setItem(row, "DISCOUNT_RATE", BIL.getOwnRate(ctz[0], ctz[1],
				ctz[2], sysFee.getValue("CHARGE_HOSP_CODE", row), sysFee
				.getValue("ORDER_CODE")));
		this.setItem(row, "OWN_AMT", ownAmt);
		// System.out.println("ownAmt="+ownAmt);
		this.setItem(row, "AR_AMT", arAmt);
		// System.out.println("arAmt="+arAmt);
		this.setItem(row, "PAYAMOUNT", ownAmt - arAmt);

		return true;

	}

	/**
	 * 删除一条医嘱
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxNo
	 *            处方号
	 * @return
	 */
	public boolean deleteOrder(String rxType, String rxNo) {

		return true;
	}

	/**
	 * 判断医嘱是否有order_code，以此来判断此条医嘱是否已经开立过
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxNo
	 *            处方号
	 * @return boolean true:空医嘱；false:已经开立过的医嘱
	 */
	public boolean isNullOrder(String rxType, String rxNo) {
		String filterString = "RX_NO='" + rxNo + "' AND RX_TYPE='" + rxType
				+ "' ";
		this.setFilter(filterString);
		if (!this.filter()) {
			// //System.out.println("isNullOrder.filter failed");
			return false;
		}

		String orderCode = this
				.getItemString(this.rowCount() - 1, "ORDER_CODE");
		boolean result = (orderCode == null || orderCode.trim().length() < 1);
		return result;
	}

	/**
	 * 获取处方签对应医嘱数目
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxNo
	 *            处方号
	 * @return boolean true:空医嘱；false:已经开立过的医嘱
	 */
	public int getCountByRXNo(String rxType, String rxNo) {
		String filterString = "RX_NO='" + rxNo + "' AND RX_TYPE='" + rxType
				+ "' ";
		this.setFilter(filterString);
		if (!this.filter()) {
			// //System.out.println("isNullOrder.filter failed");
			return 0;
		}

		return this.rowCount() - 1;
		// String filterString = "RX_NO='" + rxNo + "' AND RX_TYPE='" + rxType
		// + "' AND CASE_NO='" + caseNo
		// + "' ";
		// TParm result = new
		// TParm(TJDODBTool.getInstance().select("SELECT COUNT(*) COUNT FROM OPD_ORDER WHERE "+filterString));
		//		
		//		
		// return result.getInt(0, 0);
	}

	/**
	 * 列的值改变
	 */
	public boolean setItem(int row, String column, Object value) {
		super.setItem(row, column, value);
		if (itemNow)
			return true;
		if ("MEDI_QTY".equalsIgnoreCase(column)
				|| "TAKE_DAYS".equalsIgnoreCase(column)
				|| "FREQ_CODE".equalsIgnoreCase(column)
				|| "GIVEBOX_FLG".equalsIgnoreCase(column)) {
			TParm parm = this.getRowParm(row);

			TotQtyTool qty = new TotQtyTool();
			if (!TCM_Transform
					.getBoolean(this.getItemData(row, "LINKMAIN_FLG"))) {
				TParm qtyParm = qty.getTotQty(parm);// 计算总量//前台调用
				// TParm qtyParm = new
				// TParm(PHAStrike.getInstance().getTotQty(parm.getData()));//后台调用
				itemNow = true;
				if (("GIVEBOX_FLG".equalsIgnoreCase(column) && StringTool
						.getBoolean((String) value))
						|| (!"GIVEBOX_FLG".equalsIgnoreCase(column) && StringTool
								.getBoolean(this.getItemString(row,
										"GIVEBOX_FLG")))) {
					itemNow = true;
					// System.out.println("名称1："+this.getItemData(row,
					// "ORDER_DESC"));
					// System.out.println("TOT_QTY:"+qtyParm.getDouble("TOT_QTY"));
					// System.out.println("STOCK_UNIT:"+qtyParm.getDouble("STOCK_UNIT"));
					this.setItem(row, "DISPENSE_QTY", qtyParm
							.getDouble("QTY_FOR_STOCK_UNIT"));
					this.setItem(row, "DISPENSE_UNIT", qtyParm
							.getValue("STOCK_UNIT"));
					itemNow = true;
					this.setItem(row, "DOSAGE_QTY", qtyParm
							.getDouble("TOT_QTY"));
					this.setItem(row, "DOSAGE_UNIT", qtyParm
							.getValue("DOSAGE_UNIT"));

				} else {
					itemNow = true;
					// System.out.println("名称2：" + this.getItemData(row,
					// "ORDER_DESC"));
					// System.out.println("TOT_QTY:" +
					// qtyParm.getDouble("TOT_QTY"));
					// System.out.println("DOSAGE_UNIT:" +
					// qtyParm.getDouble("DOSAGE_UNIT"));
					this.setItem(row, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
					this.setItem(row, "DOSAGE_UNIT", qtyParm
							.getValue("DOSAGE_UNIT"));
					this.setItem(row, "DISPENSE_QTY", qtyParm.getDouble("QTY"));
					this.setItem(row, "DISPENSE_UNIT", qtyParm
							.getValue("DOSAGE_UNIT"));
					// 判断是否是集合医嘱主项 如果是主项那么计算其子项的总量
					if (TypeTool.getBoolean(this
							.getItemData(row, "SETMAIN_FLG"))) {
						String orderSetCode = this.getItemString(row,
								"ORDER_CODE");
						String groupID = this.getItemString(row,
								"ORDERSET_GROUP_NO");
						String rxNo = this.getItemString(row, "RX_NO");
						// 循环计算子项的总量
						for (int k = 0; k < this.rowCountFilter(); k++) {
							// 判断是否是该集合医嘱的子项
							if (orderSetCode.equals(TypeTool.getString(this
									.getItemData(k, "ORDERSET_CODE",
											this.FILTER)))
									&& groupID.equals(TypeTool.getString(this
											.getItemData(k,
													"ORDERSET_GROUP_NO",
													this.FILTER)))
									&& rxNo.equals(TypeTool.getString(this
											.getItemData(k, "RX_NO",
													this.FILTER)))
									&& !TypeTool.getBoolean(this.getItemData(k,
											"SETMAIN_FLG", this.FILTER))) {
								// 细项的默认数量
								double osTot = this.getOrderSetTot(TypeTool
										.getString(this.getItemData(k,
												"ORDER_CODE", this.FILTER)),
										TypeTool.getString(this.getItemData(k,
												"ORDERSET_CODE", this.FILTER)));
								this
										.setItem(k, "DOSAGE_QTY", osTot
												* qtyParm.getDouble("QTY"),
												this.FILTER);
								this.setItem(k, "DOSAGE_UNIT", qtyParm
										.getValue("DOSAGE_UNIT"), this.FILTER);
								this
										.setItem(k, "DISPENSE_QTY", osTot
												* qtyParm.getDouble("QTY"),
												this.FILTER);
								this.setItem(k, "DISPENSE_UNIT", qtyParm
										.getValue("DOSAGE_UNIT"), this.FILTER);
								// 如果修改的是用量 那么细项的用量也要和主项的用量一起修改
								if ("MEDI_QTY".equalsIgnoreCase(column)) {
									this.setItem(k, "MEDI_QTY", TypeTool
											.getInt(value)
											* osTot, this.FILTER);
								}
							}
						}
					}
				}
				// 判断是否是集合医嘱主项 如果是主项那么计算其子项的价格合计
				if (TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) {
					String orderSetCode = this.getItemString(row, "ORDER_CODE");
					String groupID = this.getItemString(row,
							"ORDERSET_GROUP_NO");
					String rxNo = this.getItemString(row, "RX_NO");
					// 循环计算子项的费用之和
					for (int k = 0; k < this.rowCountFilter(); k++) {
						// 判断是否是该集合医嘱的子项
						if (orderSetCode.equals(TypeTool.getString(this
								.getItemData(k, "ORDERSET_CODE", this.FILTER)))
								&& groupID.equals(TypeTool.getString(this
										.getItemData(k, "ORDERSET_GROUP_NO",
												this.FILTER)))
								&& rxNo.equals(TypeTool.getString(this
										.getItemData(k, "RX_NO", this.FILTER)))
								&& !TypeTool.getBoolean(this.getItemData(k,
										"SETMAIN_FLG", this.FILTER))) {
							this.setItem(k, "OWN_AMT", roundAmt(TypeTool
									.getDouble(this.getItemData(k, "OWN_PRICE",
											this.FILTER))
									* TypeTool.getDouble(this.getItemData(k,
											"DOSAGE_QTY", this.FILTER))),
									this.FILTER);
							this.setItem(k, "AR_AMT", roundAmt(BIL
									.chargeTotCTZ(this.getItemString(row,
											"CTZ1_CODE"), this.getItemString(
											row, "CTZ2_CODE"), this
											.getItemString(row, "CTZ3_CODE"),
											TypeTool.getString(this
													.getItemData(k,
															"ORDER_CODE",
															this.FILTER)),
											TypeTool.getDouble(this
													.getItemData(k,
															"DOSAGE_QTY",
															this.FILTER)),
											serviceLevel)), this.FILTER);
						}
					}
				} else {// 不是集合医嘱 那么就取自身的价格
					// System.out.println("DOSAGE_QTY:"+this.getItemDouble(row,
					// "DOSAGE_QTY"));
					// System.out.println("OWN_PRICE:"+this.getItemDouble(row,
					// "OWN_PRICE"));
					this.setItem(row, "OWN_AMT", roundAmt(this.getItemDouble(
							row, "OWN_PRICE")
							* this.getItemDouble(row, "DOSAGE_QTY")));
					this.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(this
							.getItemString(row, "CTZ1_CODE"), this
							.getItemString(row, "CTZ2_CODE"), this
							.getItemString(row, "CTZ3_CODE"), this
							.getItemString(row, "ORDER_CODE"), this
							.getItemDouble(row, "DOSAGE_QTY"), serviceLevel)));
				}
				// System.out.println("查看总价:");
				// this.showDebug();
				itemNow = false;
				return true;
			} else {
				itemNow = true;
				// String rxNo=this.getItemString(row, "RX_NO");
				int linkNo = StringTool.getInt(this.getItemString(row,
						"LINK_NO"));
				for (int i = 0; i < this.rowCount(); i++) {
					int temp = StringTool.getInt(this.getItemString(i,
							"LINK_NO"));
					// boolean
					// linkM=TCM_Transform.getBoolean(this.getItemData(i,
					// "LINKMAIN_FLG"));
					if (linkNo != temp) {
						continue;
					}
					if ("TAKE_DAYS".equalsIgnoreCase(column)) {
						this.setItem(i, "TAKE_DAYS", TCM_Transform
								.getInt(value));
					} else if ("FREQ_CODE".equalsIgnoreCase(column)) {
						this.setItem(i, "FREQ_CODE", TCM_Transform
								.getString(value));
					}
					TParm parm1 = this.getRowParm(i);
					// System.out.println("parm1~~~~~~~~~~~~~~~~:"+parm1);
					TParm qtyParm = qty.getTotQty(parm1);// 前台调用
					// System.out.println("qtyParm~~~~~~~~~~~~~~~:"+qtyParm);
					// TParm qtyParm = new
					// TParm(PHAStrike.getInstance().getTotQty(parm1.getData()));//后台调用
					if (StringTool.getBoolean(this.getItemString(i,
							"GIVEBOX_FLG"))) {
						this.setItem(i, "DISPENSE_QTY", qtyParm
								.getDouble("QTY_FOR_STOCK_UNIT"));
						this.setItem(i, "DISPENSE_UNIT", qtyParm
								.getValue("STOCK_UNIT"));
						this.setItem(i, "DOSAGE_QTY", qtyParm
								.getDouble("TOT_QTY"));
						this.setItem(i, "DOSAGE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"));
					} else {
						this.setItem(i, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
						this.setItem(i, "DOSAGE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"));
						this.setItem(i, "DISPENSE_QTY", qtyParm
								.getDouble("QTY"));
						this.setItem(i, "DISPENSE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"));
					}
					this.setItem(i, "OWN_AMT", roundAmt(this.getItemDouble(i,
							"OWN_PRICE")
							* this.getItemDouble(i, "DOSAGE_QTY")));
					this.setItem(i, "AR_AMT", roundAmt(BIL.chargeTotCTZ(this
							.getItemString(i, "CTZ1_CODE"), this.getItemString(
							i, "CTZ2_CODE"),
							this.getItemString(i, "CTZ3_CODE"), this
									.getItemString(i, "ORDER_CODE"), this
									.getItemDouble(i, "DOSAGE_QTY"),
							serviceLevel)));
					this.addOtherShowRowList(i);
				}
				itemNow = false;
				return true;
			}

		}
		// 总量推算用量
		if ("DISPENSE_QTY".equalsIgnoreCase(column)) {
			itemNow = true;
			TParm parm = this.getRowParm(row);
			parm.setData("DOSAGE_QTY", value);
			TotQtyTool qty = new TotQtyTool();
			TParm qtyParm = qty.getTakeQty(parm);
			double qtyDb = qtyParm.getDouble("QTY");
			// System.out.println("计算药品用量=="+qtyParm);

			itemNow = true;
			double dosageQty = TypeTool.getDouble(value);
			if (qtyParm.getData("SUM_QTY") != null) {
				dosageQty = qtyParm.getDouble("SUM_QTY");
			}
			// 判断用量是否有修改
			boolean isMedi = false;
			if (qtyDb != this.getItemDouble(row, "MEDI_QTY")) {
				isMedi = true;
			}
			this.setItem(row, "MEDI_QTY", qtyDb);
			this.setItem(row, "DOSAGE_QTY", dosageQty);
			double totQty = TypeTool.getDouble(dosageQty);
			// 判断是否是集合医嘱主项 如果是主项那么计算其子项的价格合计
			if (TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) {
				String orderSetCode = this.getItemString(row, "ORDER_CODE");
				String groupID = this.getItemString(row, "ORDERSET_GROUP_NO");
				String rxNo = this.getItemString(row, "RX_NO");
				// 循环计算子项的费用之和
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// 判断是否是该集合医嘱的子项
					if (orderSetCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& groupID.equals(TypeTool.getString(this
									.getItemData(k, "ORDERSET_GROUP_NO",
											this.FILTER)))
							&& rxNo.equals(TypeTool.getString(this.getItemData(
									k, "RX_NO", this.FILTER)))
							&& !TypeTool.getBoolean(this.getItemData(k,
									"SETMAIN_FLG", this.FILTER))) {
						// 细项的默认数量
						double osTot = this.getOrderSetTot(TypeTool
								.getString(this.getItemData(k, "ORDER_CODE",
										this.FILTER)), TypeTool.getString(this
								.getItemData(k, "ORDERSET_CODE", this.FILTER)));
						// 修改子项的总量
						this.setItem(k, "DOSAGE_QTY", osTot * dosageQty,
								this.FILTER);
						this.setItem(k, "DOSAGE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"), this.FILTER);
						this.setItem(k, "DISPENSE_QTY", osTot * dosageQty,
								this.FILTER);
						this.setItem(k, "DISPENSE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"), this.FILTER);
						if (isMedi) {// 如果用量有修改纳闷修改子项
							this.setItem(k, "MEDI_QTY", osTot * qtyDb,
									this.FILTER); // 修改子项用量
						}
						// 修改子项的价格
						this.setItem(k, "OWN_AMT", roundAmt(TypeTool
								.getDouble(this.getItemData(k, "OWN_PRICE",
										this.FILTER))
								* TypeTool.getDouble(this.getItemData(k,
										"DOSAGE_QTY", this.FILTER))),
								this.FILTER);
						this.setItem(k, "AR_AMT", roundAmt(BIL.chargeTotCTZ(
								this.getItemString(row, "CTZ1_CODE"), this
										.getItemString(row, "CTZ2_CODE"), this
										.getItemString(row, "CTZ3_CODE"),
								TypeTool.getString(this.getItemData(k,
										"ORDER_CODE", this.FILTER)), TypeTool
										.getDouble(this.getItemData(k,
												"DOSAGE_QTY", this.FILTER)),
								serviceLevel)), this.FILTER);
					}
				}
			} else {
				this.setItem(row, "OWN_AMT", roundAmt(this.getItemDouble(row,
						"OWN_PRICE")
						* totQty));
				this.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(this
						.getItemString(row, "CTZ1_CODE"), this.getItemString(
						row, "CTZ2_CODE"),
						this.getItemString(row, "CTZ3_CODE"), this
								.getItemString(row, "ORDER_CODE"), totQty,
						serviceLevel)));
			}
			itemNow = false;
			return true;
		}
		// 连结医嘱主项
		if ("LINKMAIN_FLG".equalsIgnoreCase(column)) {
			itemNow = true;
			boolean isMain = TypeTool.getBoolean(value);
			if (isMain) {
				int maxLinkNo = getMaxLinkNo();
				// ////System.out.println("maxLinkNo========="+maxLinkNo);
				int linkNo = getMaxLinkNo() + 1;
				this.setItem(row, "LINKMAIN_FLG", "Y");
				itemNow = true;
				for (int i = row; i < this.rowCount(); i++) {
					// if(!StringUtil.isNullString(this.getItemString(i,
					// "ORDER_CODE"))){
					this.setItem(i, "LINK_NO", linkNo);
					itemNow = true;
					// }
				}
			} else {
				if (this.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = this.getItemInt(row, "LINK_NO");
					for (int i = 0; i < this.rowCount(); i++) {
						if (linktemp == this.getItemInt(i, "LINK_NO")) {
							this.setItem(i, "LINK_NO", "");
							itemNow = true;
							this.setItem(i, "LINKMAIN_FLG", "N");
							itemNow = true;
						}
					}
				}
			}
			itemNow = false;
			return true;

		}
		// 如果是连接组号修改 那么进行排序
		// if("LINK_NO".equalsIgnoreCase(column)){
		// System.out.println("111111");
		// this.setSort("LINK_NO,SEQ_NO");
		// this.sort();
		// System.out.println("orderby:"+this.getOrderBy());
		// System.out.println("sort:"+this.getSort());
		// this.showDebug();
		// }
		// zhangyong20110616
		if ("EXEC_DEPT_CODE".equalsIgnoreCase(column)) {
			itemNow = true;
			// 判断是否是集合医嘱主项 如果是主项那么计算其子项的价格合计
			if (TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) {
				String orderSetCode = this.getItemString(row, "ORDER_CODE");
				String groupID = this.getItemString(row, "ORDERSET_GROUP_NO");
				String rxNo = this.getItemString(row, "RX_NO");
				this.setItem(row, "EXEC_DEPT_CODE", value);
				this.setItem(row, "COST_CENTER_CODE", DeptTool.getInstance()
						.getCostCenter(value.toString(), ""));
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// 判断是否是该集合医嘱的子项
					if (orderSetCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& groupID.equals(TypeTool.getString(this
									.getItemData(k, "ORDERSET_GROUP_NO",
											this.FILTER)))
							&& rxNo.equals(TypeTool.getString(this.getItemData(
									k, "RX_NO", this.FILTER)))
							&& !TypeTool.getBoolean(this.getItemData(k,
									"SETMAIN_FLG", this.FILTER))) {
						this.setItem(k, "EXEC_DEPT_CODE", value, this.FILTER);
						this.setItem(k, "COST_CENTER_CODE", DeptTool
								.getInstance().getCostCenter(value.toString(),
										""), this.FILTER);
						// System.out.println("-------"+this.getRowParm(k,this.FILTER));
						itemNow = true;
					}
				}
			}
		}
		// itemNow = false;
		// return true;
		// }

		return true;
	}

	/**
	 * 取最大处方签内的连接号
	 * 
	 * @param order
	 * @return
	 */
	public int getMaxLinkNo() {
		int linkNo = 0;
		for (int i = 0; i < this.rowCount(); i++) {
			if (StringUtil.isNullString(this.getItemString(i, "ORDER_CODE")))
				continue;
			int temp = this.getItemInt(i, "LINK_NO");
			if (temp > linkNo)
				linkNo = temp;
		}
		return linkNo;
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		TParm result = new TParm();

		if (groupNo < 0) {
			// //System.out.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			// //System.out.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TParm parm = this.getBuffer(this.FILTER);

		int count = parm.getCount();
		if (count < 0) {
			// //System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		String tempCode;
		int tempNo;
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION", parm.getValue("SPECIFICATION",
						i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				result.addData("OWN_PRICE", parm.getValue("OWN_PRICE", i));
				result.addData("OWN_AMT", parm.getValue("OWN_AMT", i));
				result.addData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				// zhangyong20110616
				result.addData("COST_CENTER_CODE", parm.getValue(
						"COST_CENTER_CODE", i));
				result
						.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE",
								i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}

	/**
	 * 判断给入的ORDER_CODE是否已经开过
	 * 
	 * @param orderCode
	 *            String 给如的ORDER_CODE
	 * @return boolean true:开过,false:没开过
	 */
	public boolean isSameOrder(String orderCode) {
		// //System.out.println("isSameOrder-》orderCode="+orderCode);	
		TParm parm = this.getBuffer(this.isFilter() ? this.FILTER
				: this.PRIMARY);
		//add by huangtt 20151211 去掉院前用药开立的医嘱 start
		TParm newParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if(!parm.getValue("RX_TYPE", i).equals("6")){
				newParm.addRowData(parm, i);
			}
		}
		//add by huangtt 20151211 去掉院前用药开立的医嘱 end
		Vector code = (Vector) newParm.getData("ORDER_CODE");
		// //System.out.println("code="+code);
		// //System.out.println("result="+(code.indexOf(orderCode)>0));		
		return code.indexOf(orderCode) > -1;
	}

	/**
	 * 如果是连接医嘱的话细相的频次，天数，部门被赋值成主项的量
	 * 
	 * @param linkNo
	 *            int 连接号
	 * @return
	 */
	public TParm getLinkMainParm(int linkNo) {
		TParm result = new TParm();
		if (linkNo < 1)
			return result;
		TParm parm = this.getBuffer(this.PRIMARY);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			if (StringUtil.isNullString(parm.getValue("ORDER_CODE", i)))
				continue;
			if (parm.getBoolean("LINKMAIN_FLG", i)
					&& parm.getInt("LINK_NO", i) == linkNo) {
				result.setData("FREQ_CODE", parm.getValue("FREQ_CODE", i));
				result.setData("TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
				result.setData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				// zhangyong20110616
				result.addData("COST_CENTER_CODE", parm.getValue(
						"COST_CENTER_CODE", i));
				result.setData("ROUTE_CODE", parm.getValue("ROUTE_CODE", i));
			}
		}
		return result;
	}

	/**
	 * 取得修改（包括新增）的检验检查处方号
	 * 
	 * @return
	 */
	public TParm getModifiedExaRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		// ===========pangben 2012-6-28 start 添加或删除操作处方签显示
		TParm parm = this.getBuffer(this.PRIMARY);
		TParm delParm = this.getBuffer(this.DELETE);//======pangben 2013-4-9 
		int count = parm.getCount();
		ArrayList list = new ArrayList();
		// System.out.println("parm:"+parm);
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("#NEW#", i)||parm.getBoolean("#MODIFY#", i))//=====pangben 2013-3-20 新增修改处方签显示
				list.add(new Integer(i));
		}

		mdfRow = getIntArray(list);
		// ===========pangben 2012-6-28 stop
		// System.out.println("----------------------------end");
		for (int row : mdfRow) {
			// System.out.println("row111111="+row);
			String tempRx = this.getItemString(row, "RX_NO");
			// 处置或检验检查不用再保存时打印
			if (!"5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))) {
				continue;
			}
			if (!this.isActive(row)) {
				continue;
			}
			if ("5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& !TypeTool.getBoolean(this
							.getItemData(row, "SETMAIN_FLG"))) {
				continue;
			}

			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
				result.addData("MED_APPLY_NO", this.getItemString(row,
						"MED_APPLY_NO"));
				result.addData("SEQ_NO", this.getItemInt(row, "SEQ_NO"));
				result.addData("CAT1_TYPE", this
						.getItemString(row, "CAT1_TYPE"));
				result.addData("CASE_NO", this.getCaseNo());
				result.addData("ORDER_CAT1_CODE", this.getItemString(row,
						"ORDER_CAT1_CODE"));
				result.setCount(result.getCount("RX_NO"));
			}
		}
		//==========pangben 2013-4-9 添加删除的医嘱，打印当前处方签，如果操作此处方签中的医嘱就会打印
		int index=0;
		for (int i = 0; i <delParm.getCount("RX_NO"); i++) {
			// 处置或检验检查不用再保存时打印
			if (!"5".equalsIgnoreCase(delParm.getValue("RX_TYPE", i))) {
				continue;
			}
			index++;
			printAddParm(result, delParm, i);
			result.addData("MED_APPLY_NO",  delParm.getValue("MED_APPLY_NO",i));
			result.addData("SEQ_NO", delParm.getValue("SEQ_NO",i));
			result.addData("CAT1_TYPE",  delParm.getValue("CAT1_TYPE",i));
			result.addData("CASE_NO", this.getCaseNo());
			result.addData("ORDER_CAT1_CODE",  delParm.getValue("ORDER_CAT1_CODE",i));
		}
		result.setCount(result.getCount("RX_NO")+index);
		this.setFilter(filter);
		this.filter();
		return result;
	}
	/**
	 * ====pangben 2013-4-9 打印处方签删除的医嘱所在的处方签也要打印
	 * @param result
	 * @param delParm
	 * @param i
	 */
	private void printAddParm(TParm result,TParm delParm,int i){
		result.addData("RX_NO", delParm.getValue("RX_NO",i));
		result.addData("RX_TYPE", delParm.getValue("RX_TYPE",i));
		
		
	}
	/**
	 * 取得修改（包括新增）的检验检查处方号
	 * 
	 * @return
	 */
	public TParm getModifiedExaOrder() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		mdfRow = this.getNewRows();
		for (int row : mdfRow) {
			// System.out.println("row="+row);
			TParm parm = this.getRowParm(row);
			// System.out.println("parm===="+parm);
			String tempRx = this.getItemString(row, "RX_NO");
			String orderCode = this.getItemString(row, "ORDER_CODE")
					+ this.getItemInt(row, "SEQ_NO");
			// 处置或检验检查不用再保存时打印
			if (!"5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))) {
				// System.out.println("not 5");
				continue;
			}
			if (!this.isActive(row)) {
				// System.out.println("is not active");
				continue;
			}
			if ("5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& !TypeTool.getBoolean(this
							.getItemData(row, "SETMAIN_FLG"))) {
				// System.out.println("not main");
				continue;
			}
			if (StringTool.getBoolean(this.getItemString(row, "BILL_FLG"))) {
				// System.out.println("is billed");
				continue;
			}
			if (rx.indexOf(orderCode) < 0) {
				rx.add(orderCode);
				// add by wangqing 20170626
				result.addData("ORDER_CODE", orderCode);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
				result.addData("MED_APPLY_NO", this.getItemString(row,
						"MED_APPLY_NO"));
				result.addData("SEQ_NO", this.getItemInt(row, "SEQ_NO"));
				result.addData("CAT1_TYPE", this
						.getItemString(row, "CAT1_TYPE"));
				result.setCount(result.getCount("RX_NO"));
			}
		}
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * 取得修改（包括新增）的处置处方号
	 * 
	 * @return
	 */
	public TParm getModifiedOpRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		TParm delParm = this.getBuffer(this.DELETE);//======pangben 2013-4-9 
		this.setFilter("");
		this.filter();
		mdfRow = this.getNewRows();
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// 处置或检验检查不用再保存时打印
			if (!"4".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))) {
				continue;
			}
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
			}
		}
		//==========pangben 2013-4-9 添加删除的医嘱，打印当前处方签，如果操作此处方签中的医嘱就会打印
		int index=0;
		for (int i = 0; i <delParm.getCount("RX_NO"); i++) {
			// 处置或检验检查不用再保存时打印
			if (!"4".equalsIgnoreCase(delParm.getValue("RX_TYPE",i))) {
				continue;
			}
			index++;
			printAddParm(result, delParm, i);
		}
		result.setCount(result.getCount("RX_NO")+index);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * 取得修改（包括新增）的处方号
	 * 
	 * @return
	 */
	public TParm getModifiedOrderRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		// ===========pangben 2012-7-17 start 添加或删除操作处方签显示
		TParm parm = this.getBuffer(this.PRIMARY);
		TParm delParm = this.getBuffer(this.DELETE);//======pangben 2013-4-9 
		int count = parm.getCount();
		ArrayList list = new ArrayList();
		// System.out.println("parm:"+parm);
		for (int i = 0; i < count; i++) {
			// System.out.println("ACTIVE::::"+parm.getBoolean("#ACTIVE#",i));
			if(parm.getValue("ORDER_CODE",i).length()<=0)
				continue;
			if (parm.getBoolean("#NEW#", i)||parm.getBoolean("#MODIFY#", i))//=====pangben 2013-3-20 新增修改处方签显示
				list.add(new Integer(i));
		}

		mdfRow = getIntArray(list);
		// ===========pangben 2012-7-17 stop
		// mdfRow = this.getModifiedRows();。
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// 处置或检验检查不用再保存时打印
			if (!"1".equalsIgnoreCase(this.getItemString(row, "RX_TYPE")))
				continue;
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
				result.setCount(result.getCount("RX_NO"));
			}
		}
		//==========pangben 2013-4-9 添加删除的医嘱，打印当前处方签，如果操作此处方签中的医嘱就会打印
		int index=0;
		for (int i = 0; i <delParm.getCount("RX_NO"); i++) {
			if (!"1".equalsIgnoreCase(delParm.getValue("RX_TYPE",i)))
				continue;
			index++;
			printAddParm(result, delParm, i);
		}
		result.setCount(result.getCount("RX_NO")+index);
		onCallNo(rx);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * 取得修改（包括新增）的处方号
	 * 管制药品
	 * @return
	 */
	public TParm getModifiedCtrlRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		mdfRow = this.getModifiedRows();
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// 处置或检验检查不用再保存时打印
			if (!"2".equalsIgnoreCase(this.getItemString(row, "RX_TYPE")))
				continue;
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
			}
		}
		onCallNo(rx);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * 给药房发送排队叫号
	 * 
	 * @param rx
	 * @return
	 */
	public boolean onCallNo(List rx) {
		int count = rx.size();
		CallNo callNoUtil = new CallNo();
		// System.out.println();
		if (!"N".equals(TConfig.getSystemValue("ServerIsReady"))) {
			callNoUtil.init();
		}
		/**
		 * @param VISIT_DATE
		 *            String 就诊日期 NOT NULL,
		 * @param VISIT_NO
		 *            String 就诊号 CASE_NO
		 * @param SERIAL_NO
		 *            String PRINT_NO
		 * @param PATIENT_ID
		 *            String 病人卡号 MR_NO
		 * @param PNAME
		 *            String 病人姓名 PAT_NAME
		 * @param PSEX
		 *            String 性别
		 * @param IDENTITY
		 *            String 身份(空)
		 * @param DEPTID
		 *            String 药房名称 ORG_DESC
		 * @param REGISTERING_DATE
		 *            String 开药时间（2010-2-8 11:11:11） OPT_DATE
		 * @param DOCTOR
		 *            String 窗口名称 COUNTER_DESC
		 * @param CTYPE
		 *            String 0 配药 1 发药完成 0
		 * @param OPTYPE
		 *            String 操作类型 2 插入或者更新 2
		 */

		if (count <= 0) {
			return false;
		}
		String caseNo = this.getCaseNo();
		String mrNo = this.getMrNo();
		String orgDesc = "";
		String counterDesc = "";
		String optDate = StringTool.getString(this.getDBTime(),
				"yyyy-MM-dd HH:mm:ss");
		String opType = "2";
		String printNo = "";
		String patName = StringUtil.getDesc("SYS_PATINFO", "PAT_NAME",
				"MR_NO='" + mrNo + "'");
		for (int i = 0; i < count; i++) {
			this.setFilter("RX_NO='" + rx.get(i) + "'");
			this.filter();
			printNo = this.getItemString(0, "PRINT_NO");
			orgDesc = StringUtil.getDesc("IND_ORG", "ORG_CHN_DESC",
					" ORG_CODE='" + this.getItemString(0, "EXEC_DEPT_CODE")
							+ "'");
			counterDesc = StringUtil.getDesc("PHA_COUNTERNO", "COUNTER_DESC",
					"COUNTER_NO='" + this.getItemString(0, "COUNTER_NO") + "'"
							+ " AND ORG_CODE='"
							+ this.getItemString(0, "EXEC_DEPT_CODE") + "'");
			// System.out.println("orderDesc="+counterDesc);
			callNoUtil.SyncDrug("", caseNo, printNo, mrNo, patName, "", "",
					orgDesc, optDate, counterDesc, "0", opType);
		}
		return true;
	}

	/**
	 * 取得修改（包括新增）的处方号
	 * 
	 * @return
	 */
	public TParm getModifiedChnRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		mdfRow = this.getModifiedRows();
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// 处置或检验检查不用再保存时打印
			if (!"3".equalsIgnoreCase(this.getItemString(row, "RX_TYPE")))
				continue;
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
			}
		}
		onCallNo(rx);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * 补充计价更新字段
	 * 
	 * @param caseNo
	 * @param rxNo
	 * @param seq
	 * @param requestNo
	 * @param requestFlg
	 * @param conn
	 * @return
	 */
	public static TParm updateRequest(String caseNo, String rxNo, long seq,
			String requestNo, String requestFlg, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE OPD_ORDER SET REQUEST_NO='" + requestNo
				+ "' ,REQUEST_FLG='" + requestFlg + "' WHERE CASE_NO='"
				+ caseNo + "' AND RX_NO='" + rxNo + "' AND SEQ=" + seq;
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}

	/**
	 * 得到labNo,如沒有則新建一條，並新增一行MED_APPLY的數據
	 * 
	 * @param row
	 * @param odo
	 * @param clinicRoom
	 * @return
	 */
	public String getLabNo(int row, ODO odo) {
		String labNo = "";
		if (odo == null || row < 0) {
			// System.out.println("odo==null:"+odo==null);
			// System.out.println("row<0:"+(row<0));
			// System.out.println("clinicRoom:"+StringUtil.isNullString(clinicRoom));
			// System.out.println("no labNo");
			return labNo;
		}
		String labMapKey = this.getItemString(row, "DEV_CODE")
				+ this.getItemString(row, "OPTITEM_CODE")
				+ this.getItemString(row, "RPTTYPE_CODE")
				+ this.getItemString(row, "RX_NO");//add by huangjw 20160422
		String cat1Type = this.getItemString(row, "CAT1_TYPE");
		if ("RIS".equalsIgnoreCase(cat1Type)) {
			labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
					"LABNO");
			labMap.put(labMapKey, labNo);
		} else {
			if (labMap.get(labMapKey) == null) {
				labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				if (StringUtil.isNullString(labNo)) {
					return labNo;
				}
				labMap.put(labMapKey, labNo);
			} else {
				labNo = (String) labMap.get(labMapKey);
			}
		}
		MedApply med = this.getMedApply();
		int medRow = med.insertRow();
		// value.add(labNo);//0
		med.setItem(medRow, "APPLICATION_NO", labNo);
		// value.add(cat1Type);//1
		med.setItem(medRow, "CAT1_TYPE", this.getItemData(row, "CAT1_TYPE"));
		// // ORDER_NO
		med.setItem(medRow, "ORDER_NO", this.getItemData(row, "RX_NO"));
		// value.add(this.getItemString(row, "RX_NO"));//2
		// //SEQ_NO
		// System.out.println("med_apply~~~~~~~~~~:"+this.getItemData(row,
		// "SEQ_NO"));
		med.setItem(medRow, "SEQ_NO", this.getItemData(row, "SEQ_NO"));
		// value.add(this.getItemData(row, "SEQ_NO"));//3
		// //ORDER_CODE
		med.setItem(medRow, "ORDER_CODE", this.getItemData(row, "ORDER_CODE"));
		// value.add(this.getItemData(row, "ORDER_CODE"));//4
		// //ORDER_DESC
		med.setItem(medRow, "ORDER_DESC", this.getItemData(row, "ORDER_DESC"));
		// value.add(this.getItemData(row, "ORDER_DESC"));
		// //ORDER_DR_CODE
		med.setItem(medRow, "ORDER_DR_CODE", this.getItemData(row, "DR_CODE"));
		// value.add(Operator.getID());
		Timestamp now = this.getDBTime();
		// //ORDER_DATE
		med.setItem(medRow, "ORDER_DATE", now);
		// value.add(now);
		// //ORDER_DEPT_CODE
		med.setItem(medRow, "ORDER_DEPT_CODE", odo.getRegPatAdm()
				.getItemString(0, "REALDEPT_CODE"));
		// value.add(Operator.getDept());
		// //START_DTTM
		med.setItem(medRow, "START_DTTM", now);
		// value.add(now);
		// //EXEC_DEPT_CODE
		med.setItem(medRow, "EXEC_DEPT_CODE", this.getItemData(row,
				"EXEC_DEPT_CODE"));
		// value.add(this.getItemData(row, "EXEC_DEPT_CODE"));
		// //EXEC_DR_CODE
		med.setItem(medRow, "EXEC_DR_CODE", "");
		// value.add("");
		// //OPTITEM_CODE
		med.setItem(medRow, "OPTITEM_CODE", this.getItemData(row,
				"OPTITEM_CODE"));
		// value.add(this.getItemData(row, "OPTITEM_CODE"));
		String optitemDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC",
				" GROUP_ID = 'SYS_OPTITEM' AND ID='"
						+ this.getItemData(row, "OPTITEM_CODE") + "'");
		// //OPTITEM_CHN_DESC
		med.setItem(medRow, "OPTITEM_CHN_DESC", optitemDesc);
		// value.add(optitemDesc);
		// //ORDER_CAT1_CODE
		med.setItem(medRow, "ORDER_CAT1_CODE", this.getItemData(row,
				"ORDER_CAT1_CODE"));
		// URGENT_FLG
		med
				.setItem(medRow, "URGENT_FLG", this.getItemString(row,
						"URGENT_FLG"));
		// value.add(this.getItemData(row, "ORDER_CAT1_CODE"));
		String dealSystem = StringUtil.getDesc("SYS_OPTITEM",
				"OPTITEM_CHN_DESC", "OPTITEM_CODE='"
						+ this.getItemData(row, "OPTITEM_CODE") + "'");
		// //DEAL_SYSTEM
		med.setItem(medRow, "DEAL_SYSTEM", dealSystem);
		// value.add(OrderUtil.getInstance().getDealSystem(this.getItemString(row,
		// "ORDER_CAT1_CODE")));
		// //RPTTYPE_CODE
		med.setItem(medRow, "RPTTYPE_CODE", this.getItemData(row,
				"RPTTYPE_CODE"));
		// value.add(this.getItemData(row, "RPTTYPE_CODE"));
		// //DEV_CODE
		med.setItem(medRow, "DEV_CODE", this.getItemData(row, "DEV_CODE"));
		// value.add(this.getItemData(row, "DEV_CODE"));
		// //REMARK
		med.setItem(medRow, "REMARK", this.getItemData(row, "DR_NOTE"));
		// value.add(this.getItemData(row, "DR_NOTE"));
		// OWN_AMT
		med.setItem(medRow, "OWN_AMT", this.getItemData(row, "OWN_AMT"));
		// AR_AMT
		med.setItem(medRow, "AR_AMT", this.getItemData(row, "AR_AMT"));

		// CASE_NO
		med.setItem(medRow, "CASE_NO", this.getCaseNo());
		// MR_NO
		med.setItem(medRow, "MR_NO", this.getMrNo());
		// ADM_TYPE
		// System.out.println("MED_APPLY~~~~~~~~~~~~~~~~门急别显示："+getAdmType());
		med.setItem(medRow, "ADM_TYPE", getAdmType());
		// PAT_NAME
		med.setItem(medRow, "PAT_NAME", odo.getPatInfo().getItemString(0,
				"PAT_NAME"));
		// PAT_NAME1
		med.setItem(medRow, "PAT_NAME1", odo.getPatInfo().getItemString(0,
				"PAT_NAME1"));
		// BIRTH_DATE
		med.setItem(medRow, "BIRTH_DATE", odo.getPatInfo().getItemData(0,
				"BIRTH_DATE"));
		// SEX_CODE
		med.setItem(medRow, "SEX_CODE", odo.getPatInfo().getItemData(0,
				"SEX_CODE"));
		// POST_CODE
		med.setItem(medRow, "POST_CODE", odo.getPatInfo().getItemData(0,
				"POST_CODE"));
		// ADDRESS
		med.setItem(medRow, "ADDRESS", odo.getPatInfo().getItemData(0,
				"ADDRESS"));
		// COMPANY
		med.setItem(medRow, "COMPANY", odo.getPatInfo().getItemData(0,
				"COMPANY"));
		// TEL
		med.setItem(medRow, "TEL", odo.getPatInfo().getItemData(0, "TEL"));
		// IDNO
		med.setItem(medRow, "IDNO", odo.getPatInfo().getItemData(0, "IDNO"));
		// DEPT_CODE
		med.setItem(medRow, "DEPT_CODE", odo.getRegPatAdm().getItemString(0,
				"REALDEPT_CODE"));
		// REGION_CODE
		med.setItem(medRow, "REGION_CODE", Operator.getRegion());
		// CLINICAREA_CODE
		med.setItem(medRow, "CLINICAREA_CODE", odo.getRegPatAdm()
				.getItemString(0, "CLINICAREA_CODE"));
		// CLINICROOM_NO CLINICROOM_NO
		med.setItem(medRow, "CLINICROOM_NO", odo.getRegPatAdm().getItemString(
				0, "CLINICROOM_NO"));
		// ICD_TYPE
		int main = odo.getDiagrec().getMainDiag();
		med.setItem(medRow, "ICD_TYPE", odo.getDiagrec().getItemData(main,
				"ICD_TYPE"));
		// ICD_CODE
		med.setItem(medRow, "ICD_CODE", odo.getDiagrec().getItemData(main,
				"ICD_CODE"));
		// STATUS
		med.setItem(medRow, "STATUS", "0");
		// BILL_FLG
		med.setItem(medRow, "BILL_FLG", "N");
		// SEND_FLG
		med.setItem(medRow, "SEND_FLG", "0");
		// OPT_USER
		med.setItem(medRow, "OPT_USER", Operator.getID());
		// OPT_DATE
		med.setItem(medRow, "OPT_DATE", now);
		// OPT_TERM
		med.setItem(medRow, "OPT_TERM", Operator.getIP());
		// PRINT_FLG
		med.setItem(medRow, "PRINT_FLG", "N");
		// ORDER_ENG_DESC
		med.setItem(medRow, "ORDER_ENG_DESC", this.getItemData(row,
				"TRADE_ENG_DESC"));

		med.setActive(medRow, true);
		return labNo;
	}

	/**
	 * 根據給入變量刪除一條集合醫囑
	 * 
	 * @param rxNo
	 *            String
	 * @param orderSetCode
	 *            String
	 * @param groupNo
	 *            int
	 * @param ORDER_CODE
	 *            String
	 * @return boolean
	 */
	public boolean deleteOrderSet(String rxNo, String orderSetCode,
			int groupNo, String ORDER_CODE, String seqNo) {
		if (groupNo < 0 || StringUtil.isNullString(rxNo)) {
			return false;
		}
		String filter = this.getFilter();
		String fil = "RX_NO='" + rxNo + "' AND ORDERSET_GROUP_NO=" + groupNo
				+ "";
		// 判断是否有 集合医令代码（判断是否是集合医嘱）
		if (!StringUtil.isNullString(orderSetCode))// 如果是集合医嘱那么要根据“集合医令代码”进行删除
			fil += " AND ORDERSET_CODE='" + orderSetCode + "' ";
		else
			// 如果不是集合医嘱 那么根据ORDER_CODE进行单条删除
			fil += " AND ORDER_CODE = '" + ORDER_CODE + "' AND SEQ_NO='"
					+ seqNo + "'";
		this.setFilter(fil);
		this.filter();
		int count = this.rowCount();
		// 提取出已有的所有 med_apply信息的 APPLICATION_NO
		Map appNo = new HashMap();
		for (int i = 0; i < med.rowCount(); i++) {
			String key = med.getItemString(i, "ORDER_NO")
					+ med.getItemString(i, "SEQ_NO")
					+ med.getItemString(i, "CAT1_TYPE");
			appNo.put(key, med.getItemString(i, "APPLICATION_NO"));
		}
		for (int i = count - 1; i > -1; i--) {
			// 删除med_apply的对应信息
			// 判断是检验检查医嘱的主项 med_apply记录的是主项 删除主项
			if (("LIS".equals(this.getItemString(i, "ORDER_CAT1_CODE")) || "RIS"
					.equals(this.getItemString(i, "ORDER_CAT1_CODE")))
					&& "Y".equals(this.getItemString(i, "SETMAIN_FLG"))) {
				String labMapKey = this.getItemString(i, "RX_NO")
						+ this.getItemString(i, "SEQ_NO")
						+ this.getItemString(i, "CAT1_TYPE");
				med.deleteRowBy((String) appNo.get(labMapKey), this
						.getItemString(i, "RX_NO"), this
						.getItemInt(i, "SEQ_NO"), this.getItemString(i,
						"CAT1_TYPE"));
			}
			this.deleteRow(i);
		}
		this.setFilter(filter);
		this.filter();
		return true;
	}

	/**
	 * 检测保存（OPD_ORDER医嘱）
	 * 
	 * @return boolean
	 */
	public boolean checkSave() {
		// System.out.println("start checkSave");
		// System.out.println("before checkSave");
		String filterString = this.getFilter();
		// System.out.println("filterString="+filterString);
		this.setFilter("");
		this.filter();
		// System.out.println("after checkSave");
		int count = this.rowCount();
		for (int i = 0; i < count; i++) {
			if (!this.isActive(i)) {
				continue;
			}
			// xueyf 不校验 补充计价 start
			if ("7".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))) {
				continue;
			}
			// xueyf 不校验 补充计价 end
			
			// 院前用药不用校验 add by huangtt 20151012 start
			if ("6".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))) {
				continue;
			}
			//院前用药不用校验 add by huangtt 20151012 end
			
			if (StringUtil
					.isNullString(this.getItemString(i, "EXEC_DEPT_CODE"))) {
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " ExecDept is empty, can not save");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ " 执行科室为空，不能保存");
				this.setFilter(filterString);
				this.filter();
				return false;
			}

			if (!"5".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))
					&& TypeTool.getDouble(this.getItemData(i, "MEDI_QTY")) <= 0) {
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " Qty can not be 0");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ " 用量不能为0");
				this.setFilter(filterString);
				this.filter();
				return false;
			}
			if (TypeTool.getDouble(this.getItemData(i, "DISPENSE_QTY")) <= 0) {
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " TotQty can not be 0");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ "总量不能为0");
				this.setFilter(filterString);
				this.filter();
				return false;
			}
			// 中药判断是否填写了"煎药方式"
			if ("3".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))) {
				if (this.getItemString(i, "DCTAGENT_CODE").length() == 0) {
					this.setErrText("请选择煎药方式");
					this.setFilter(filterString);
					this.filter();
					return false;
				}
			}
			// System.out.println("mien cat1Type="+this.getItemString(i,
			// "CAT1_TYPE"));
			//add by huangtt 20150211 西药判断频次是否为空=========Start=======
			if("PHA".equalsIgnoreCase(this.getItemString(i, "CAT1_TYPE")) && 
					TypeTool.getString(this.getItemData(i, "FREQ_CODE")).length() == 0 ){
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " FreqCode can not null");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ "频次不能为空");
				this.setFilter(filterString);
				this.filter();
				return false;
			}
			//add by huangtt 20150211 西药判断频次是否为空=========end=======
			
			if (!"PHA".equalsIgnoreCase(this.getItemString(i, "CAT1_TYPE"))) {
				continue;
			}
			double dispenseQty = TypeTool.getDouble(this.getItemData(i,
					"DOSAGE_QTY"));
			String orderCode = this.getItemString(i, "ORDER_CODE");
			String orgCode = this.getItemString(i, "EXEC_DEPT_CODE");
			if (!TypeTool.getBoolean(this.getItemData(i, "RELEASE_FLG"))) {// 先判断是否是自备药
				// 自备药不看库存
				String orderType = "";
				// 西成药
				if ("W".equalsIgnoreCase(this.getItemString(i, "PHA_TYPE"))
						|| "C".equalsIgnoreCase(this.getItemString(i,
								"PHA_TYPE"))) {
					orderType = "EXA";
				} else if ("G".equalsIgnoreCase(this.getItemString(i,
						"PHA_TYPE"))) {// 中药
					orderType = "CHN";
				}
				if (checkDrugCanUpdate(orderType, i)) {// 判断是否可以修改（没有进行审,配,发）
					//===zhangp 物联网 start
//					if (isCheckKC(orderCode))
//						if (!INDTool.getInstance().inspectIndStock(orgCode,
//								orderCode, dispenseQty)) {// 查库存
//							if ("en".equals((String) TSystem
//									.getObject("Language")))
//								this.setErrText(this.getItemString(i,
//										"TRADE_ENG_DESC")
//										+ " Inventory shortage");
//							else
//								this.setErrText(this.getItemString(i,
//										"ORDER_DESC")
//										+ " 库存不足;"
//										+ this.getItemString(i, "ORDER_CODE"));
//							this.setFilter(filterString);
//							this.filter();
//							return false;
//						}
					//===zhangp 物联网 end
				}
			}
		}

		this.setFilter(filterString);
		this.filter();
		// System.out.println("last filterString="+filterString);
		// System.out.println("at the end");
		return true;
	}

	/**
	 * 取得所有处方签金额
	 * 
	 * @return
	 */
	public double getAllAmt() {
		double amt = 0.0;
		String filterString = this.getFilter();
		this.setFilter("");
		this.filter();
		int count = this.rowCount();
		for (int i = 0; i < count; i++) {
			if (!TypeTool.getBoolean(this.getItemData(i, "BILL_FLG"))) {
				amt += this.getItemDouble(i, "AR_AMT");
			}
		}
		this.setFilter(filterString);
		this.filter();
		return amt;
	}

	/**
	 * 是否为西医用用法
	 * 
	 * @param routeCode
	 * @return int 1:是西医用；0：是中医用；-1：错误
	 */
	public int isMedRoute(String routeCode) {
		if (StringUtil.isNullString(routeCode)) {
			return -1;
		}
		String sql = "SELECT WESMED_FLG,CHIMED_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
				+ routeCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			System.out.println("isMedRoute.err=" + parm.getErrText());
			return -1;
		}
		if (TypeTool.getBoolean(parm.getData("CHIMED_FLG", 0))) {
			return 0;
		}
		return 1;
	}

	/**
	 * 取得领药号
	 * 
	 * @param orgCode
	 * @return
	 */
	public String getPrintNo(String orgCode) {
		String printNo = "";
		if (StringUtil.isNullString(orgCode)) {
			return printNo;
		}
		printNo = SystemTool.getInstance().getNo("ALL", "PHA", "PRINT_NO",
				"PRINT_NO");
		return printNo;
	}

	/**
	 * 取得药房柜台号，领药号递增，所以取得符合条件的柜台集合，用领药号%柜台的总数，得到柜台集合中该结果为下标的柜台号
	 * 
	 * @param orgCode
	 *            String 药房代码
	 * @param printNo
	 *            String 领药号
	 * @param rxType
	 *            String 处方类型
	 * @return counterNo int 柜台号
	 */
	public int getCounterNo(String orgCode, String printNo, String rxType) {
		int counterNo = -1;
		if (StringUtil.isNullString(orgCode)
				|| StringUtil.isNullString(printNo)
				|| StringUtil.isNullString(rxType)) {
			// System.out.println("getCounterNo.param is null");
			return counterNo;
		}
		String sql = this.GET_COUNTER_NO.replaceFirst("#", orgCode);
		if ("2".equalsIgnoreCase(rxType)) {
			sql += " AND CTRL_FLG='Y' ORDER BY COUNTER_NO";
		} else {
			sql += " AND COMMON_FLG='Y' ORDER BY COUNTER_NO";
		}
		int printNoInt = StringTool.getInt(printNo);
		TParm countNos = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("countNos="+countNos);
		if (countNos.getErrCode() != 0) {
			System.out.println("getCounterNo.countNos.err"
					+ countNos.getErrText());
			return counterNo;
		}
		int count = countNos.getCount();
		// System.out.println("getCounterNo.count="+count);
		if (count <= 0) {
			// System.out.println("getCounterNo.countNos.count<=0");
			return counterNo;
		}
		int index = printNoInt % count;
		// System.out.println("index="+index);
		// System.out.println("counterNo="+TypeTool.getInt(countNos.getData("COUNTER_NO",index)));
		counterNo = TypeTool.getInt(countNos.getData("COUNTER_NO", index));
		// System.out.println("counterNo="+counterNo);
		return counterNo;
	}

	/**
	 * 根据传入医嘱判断是否为传入的医嘱类型可开立的医嘱
	 * 
	 * @param orderCode
	 * @return
	 */
	public boolean isDrug(String orderCode, String rxType) {
		if ("1".equalsIgnoreCase(rxType)) {
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				return false;
			}
		} else {
			if (!SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 根据给入医嘱代码判断该医嘱是否为精二，如果是，则此处方签不能有除精二之外的其他医嘱
	 * 
	 * @param orderCode
	 * @param row
	 * @return
	 */
	public boolean isSameDrug(String orderCode, int row) {
		int count = this.rowCount();
		String sql = GET_PSYCHOPHA2.replaceFirst("#", orderCode);
		// System.out.println("isSameDrug.sql="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			System.out.println("isSameDrug.result is wrong :"
					+ result.getErrText());
			return false;
		}
		int resultCnt = result.getCount();
		String phsyFlg = "1";
		if (resultCnt == 1) {
			phsyFlg = "2";
		}
		this.setItem(row, "PSY_FLG", phsyFlg);
		if (count <= 0) {
			// System.out.println("isSameDrug.count<=0");
			return true;
		}
		for (int i = 0; i < count; i++) {
			// System.out.println(this.getItemString(i,
			// "ORDER_DESC")+"in order.psy_flg="+this.getItemString(i,
			// "PSY_FLG"));
			if (StringUtil.isNullString(this.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if (!this.getItemString(i, "PSY_FLG").equalsIgnoreCase(phsyFlg)) {
				// System.out.println("isSameDrug is not same");
				// System.out.println("in order="+this.getItemString(i,
				// "PSY_FLG"));
				// System.out.println("value="+phsyFlg);
				return false;
			}
		}

		return true;
	}
	/**
	 * 修改医嘱
	 * @param updateParm
	 * @param result
	 * @param i
	 */
	private void setUpdateParm(TParm updateParm,TParm result,int i){
		updateParm.addData("CAT1_TYPE", result.getValue(
				"CAT1_TYPE", i));
		OPBTool.getInstance().setNewParm(updateParm, result, i,"Y","E");
	}
	/**
	 * 取得医疗卡用数据
	 * 
	 * @return
	 */
	public TParm getEktParam(TParm orderOldParm) {
		TParm result = new TParm();
		int[] newRows = this.getNewRows();
		//存在新增数据
		if (newRows != null && newRows.length > 0) {
			isNew = true;
		}
		//存在修改的数据
		TParm moduf = this.getBuffer(this.MODIFY);
		if (moduf != null && moduf.getCount() > 0) {
			isNew = false;
		}
		//存在删除的数据
		if (this.getDeleteCount() > 0) {
			isNew = false;
		}
		//当前操作收费后数据库医嘱状态：不存在删除数据  
		//新增 修改数据状态
		result =this.getBuffer(this.isFilter() ? this.FILTER : this.PRIMARY);
		int count = result.getCount();
		TParm parm = new TParm();
		double sum=0.00;//累计金额
		TParm newParm=new TParm();//此次操作的医嘱集合
		TParm updateParm=new TParm();//修改的医嘱集合
		TParm hl7Parm =new TParm();//hl7集合
		TParm delExeParm =new TParm();//收费的医嘱执行删除可以回退
		StringBuffer rxNo=new StringBuffer();//记录这次操作的内部交易号码 UPDATE EKT_TRADE 中已经收费的记录
		StringBuffer tempTradeNo=new StringBuffer();//设置这次操作的医嘱所在的处方签要执行的状态
		StringBuffer phaRxNo = new StringBuffer();//==pangben 2013-5-15门诊药房使用，审核界面跑马灯执行操作的处方签
		//新增医嘱的处方签中已经收费的内部交易号码也要累计
		StringBuffer tradeNo=new StringBuffer();
		double showAmt=0.00;//医疗卡扣款界面显示金额
		//已收费的医嘱暂存操作 获得数据库中所有医嘱
		if (null == orderOldParm) {
			boolean updateFlg=false;
			for (int i = 0; i < count; i++) {
				if (result.getBoolean("#MODIFY#", i)&& result.getBoolean("#ACTIVE#", i)) {
					// 收费的需要记录处方签，没有收费只是修改不用记录 ,汇总这次的医嘱只会记录收费数据的内部交易号码,
					// 查询的所有内部交易号码所在的所有医嘱数据
					if (result.getBoolean("BILL_FLG", i)) {
						updateFlg = true;
						break;
					}
				}
			}
			if(updateFlg){
				TParm parmOne = new TParm();
				parmOne.setData("CASE_NO", result.getValue("CASE_NO", 0));
				// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
				orderOldParm = OrderTool.getInstance()
						.selDataForOPBEKT(parmOne);
				if (orderOldParm.getErrCode() < 0) {
					return orderOldParm;
				}
			}
		}
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(result.getValue("ORDER_CODE", i))) {
				continue;
			}
			
			if("6".equals(result.getValue("RX_TYPE", i))){  // 院前用药不收费  add by huangtt 20151210
				continue;
			}
			if("7".equals(result.getValue("RX_TYPE", i)) && !result.getBoolean("NS_BLOOD_COLL_EXEC_FLG", i)){  //采血费用CDSS规则代出来的医嘱 需要进行收费 
				continue;
			}
			
			if (result.getBoolean("RELEASE_FLG", i)) {// 备药不收费
				continue;
			}
			//还没有改好，需要条件界面数据, 通过选择状态 执行此次操作的数据
			parm.addData("EKT_TRADE_FLG", "Y");//选择注记 执行收费操作
			if (result.getBoolean("#MODIFY#", i)
					&& result.getBoolean("#ACTIVE#", i)) {
				// 收费的需要记录处方签，没有收费只是修改不用记录 ,汇总这次的医嘱只会记录收费数据的内部交易号码,
				// 查询的所有内部交易号码所在的所有医嘱数据
				if (result.getBoolean("BILL_FLG", i)) {
					// // 汇总处方签
					if (!rxNo.toString().contains(result.getValue("RX_NO", i))) {
						rxNo.append(result.getValue("RX_NO", i)).append(",");
					}
					if (null != result.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
							result.getValue("CAT1_TYPE", i).equals("PHA")) {
						if (!phaRxNo.toString().contains(result.getValue("RX_NO", i))) {
							phaRxNo.append(result.getValue("RX_NO", i)).append(",");
						}
					}
					if (!tempTradeNo.toString().contains(result.getValue("BUSINESS_NO", i))) {
						tempTradeNo.append(result.getValue("BUSINESS_NO", i)).append(",");// 汇总这次操作的医嘱使用
						tradeNo.append("'").append(result.getValue("BUSINESS_NO", i)).append("',");// UPDATE EKT_TRADE表使用修改已经扣款的数据冲负使用
					}
					setUpdateParm(updateParm, result, i);
					showAmt += getUpdateOrderAmt(result.getRow(i), orderOldParm);// 累计扣款界面显示金额
					// setNewParm(newParm, parm, i);
				}
			}
			// 没收费的医嘱
			if (result.getValue("BUSINESS_NO", i).length() <= 0
					&& !result.getBoolean("BILL_FLG", i)) {
				// 汇总处方签
				if (!rxNo.toString().contains(result.getValue("RX_NO", i))) {
					rxNo.append(result.getValue("RX_NO", i)).append(",");
				}
				if (null != result.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
						result.getValue("CAT1_TYPE", i).equals("PHA")) {
					if (!phaRxNo.toString().contains(result.getValue("RX_NO", i))) {
						phaRxNo.append(result.getValue("RX_NO", i)).append(",");
					}
				}
				OPBTool.getInstance().setNewParm(newParm, result, i, "Y", "E");
				sum += result.getDouble("AR_AMT", i);// 累计金额
				showAmt += result.getDouble("AR_AMT", i);// 累计扣款界面显示金额
				// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
				OPBTool.getInstance().setHl7TParm(hl7Parm, result, i, "Y");
			}
			setReslutParm(parm, result, i, "Y");
		}
		String delFlg="N";
		//此次删除的医嘱
		TParm delParm = this.getBuffer(this.DELETE);
		for (int i = 0; i < delParm.getCount(); i++) {
			if (StringUtil.isNullString(delParm.getValue("ORDER_CODE", i))) {
				continue;
			}
			
			if("6".equals(delParm.getValue("RX_TYPE", i))){  // 院前用药不收费  add by huangtt 20151210
				continue;
			}
			if("7".equals(delParm.getValue("RX_TYPE", i))){  
				continue;
			}
			
			if (delParm.getBoolean("RELEASE_FLG", i)) {// 备药不收费
				continue;
			}
			if(!delParm.getBoolean("BILL_FLG", i))
				continue;
			// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
			OPBTool.getInstance().setHl7TParm(hl7Parm, delParm, i,"N");
			//汇总处方签
			if(!rxNo.toString().contains(delParm.getValue("RX_NO", i))){
				rxNo.append(delParm.getValue("RX_NO", i)).append(",");
			}
			if (null != delParm.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
					delParm.getValue("CAT1_TYPE", i).equals("PHA")) {
				if (!phaRxNo.toString().contains(delParm.getValue("RX_NO", i))) {
					phaRxNo.append(delParm.getValue("RX_NO", i)).append(",");
				}
			}
			if(!tempTradeNo.toString().contains(delParm.getValue("BUSINESS_NO", i))){
				tempTradeNo.append(delParm.getValue("BUSINESS_NO", i)).append(",");//汇总这次操作的医嘱使用
				tradeNo.append("'").append(delParm.getValue("BUSINESS_NO", i)).append("',");//UPDATE EKT_TRADE 表使用 修改已经扣款的数据 冲负使用
			}
			//删除的医嘱点击取消按钮 可以回退操作
			OPBTool.getInstance().setNewParm(delExeParm, delParm, i,"N","E");
			if (delParm.getValue("CAT1_TYPE", i).equals("LIS")
					|| delParm.getValue("CAT1_TYPE", i).equals("RIS")) {
				delFlg = "Y";
			}
			showAmt-=delParm.getDouble("AR_AMT", i);// 累计扣款界面显示金额
			parm.addData("EKT_TRADE_FLG", "Y");//还没有改好，需要条件界面数据, 通过选择状态 执行此次操作的数据
			setReslutParm(parm, delParm, i,"N");
		}
		if(rxNo.toString().length()<=0){
			parm.setData("OP_FLG",5);//没有扣款操作的医嘱 
			return parm;
		}
		//汇总需要操作的医嘱 (通过内部交易号码查询数据 ,不存在新增的医嘱 在上面的操作中已经添加到集合中 
		//设置此次操作的医嘱状态 EKT_TRADE_FLG=Y 选择的医嘱操作
		String [] tempTradeNames=new String[0];
		if(tempTradeNo.length()>0){
			tempTradeNames=tempTradeNo.toString().substring(0,tempTradeNo.lastIndexOf(",")).split(",");
		}
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < tempTradeNames.length; j++) {
				// 选择的医嘱
				// EKT_TRADE 表内部交易号
				if (result.getValue("BUSINESS_NO", i).equals(tempTradeNames[j])) {
					OPBTool.getInstance().setNewParm(newParm, result, i, "Y","E");
					sum += result.getDouble("AR_AMT", i);// 累计金额
				}
			}
		}
		String exeTradeNo = "";
		// 获得内部交易号码 ：此次操作的医嘱扣款所有需要退还的医嘱
		if (tradeNo.length() > 0) {
			exeTradeNo = tradeNo.toString().substring(0,
					tradeNo.toString().lastIndexOf(","));
		}
		parm.setData("MR_NO", this.getMrNo());// 注意是set进去MR_NO
		parm.setData("IS_NEW", isNew);// 是否只存在新增的医嘱
		parm.setData("BUSINESS_TYPE", "ODO");
		parm.setData("DEL_FLG", delFlg);//存在删除医嘱
		parm.setData("newParm", newParm.getData());//操作医嘱，增删改
		parm.setData("EXE_AMT",sum);//EKT_TRADE 中此次 操作的金额
		parm.setData("hl7Parm",hl7Parm.getData());//HL7发送接口集合
		parm.setData("updateParm",updateParm.getData());//修改的医嘱
		parm.setData("TRADE_SUM_NO",exeTradeNo);//UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
		parm.setData("SHOW_AMT",showAmt);//扣款界面显示金额 	
		parm.setData("delExeParm", delExeParm.getData());//删除医嘱，回退使用
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15添加药房审药显示跑马灯数据
		return parm;
	}
	/**
	 * 赋值
	 * @param parm
	 * @param reslutParm
	 */
	private void setReslutParm(TParm parm,TParm resultParm,int i,String billFlg){
		parm.addData("CAT1_TYPE", resultParm.getValue("CAT1_TYPE", i));// 过滤检验检查，医生修改医嘱医疗卡收费时使用
		parm.addData("RX_NO", resultParm.getValue("RX_NO", i));//处方签
		parm.addData("MED_APPLY_NO", resultParm.getValue("MED_APPLY_NO", i));// 检验检查
		parm.addData("ORDER_CODE", resultParm.getValue("ORDER_CODE", i));
		parm.addData("SETMAIN_FLG", resultParm.getValue("SETMAIN_FLG", i));// 集合医嘱
		parm.addData("SEQ_NO", resultParm.getInt("SEQ_NO", i));//序号
		parm.addData("AMT", resultParm.getDouble("AR_AMT", i));//金额
		parm.addData("CASE_NO", resultParm.getValue("CASE_NO", i));//就诊号 
		parm.addData("EXEC_FLG", resultParm.getData("EXEC_FLG", i));//执行状态 
		parm.addData("RECEIPT_FLG", resultParm.getData("RECEIPT_FLG", i));//收据状态 
		parm.addData("DOSAGE_QTY", resultParm.getDouble("DOSAGE_QTY", i));//发药量 
		parm.addData("TAKE_DAYS", resultParm.getInt("TAKE_DAYS", i));//开药天数 
		parm.addData("FREQ_CODE", resultParm.getData("FREQ_CODE", i));//频次 
		parm.addData("BILL_FLG",billFlg);//收费注记 
		parm.addData("PRINT_NO", resultParm.getValue("PRINT_NO", i));//柜台号码 
		parm.addData("COUNTER_NO", resultParm.getValue("COUNTER_NO", i));
		parm.addData("COST_CENTER_CODE", resultParm.getValue(
				"COST_CENTER_CODE", i));//成本中心
		parm.addData("ORDERSET_CODE", resultParm.getValue("ORDERSET_CODE", i));//集合医嘱
		parm.addData("BUSINESS_NO", resultParm.getValue("BUSINESS_NO", i));//内部交易号码 
		parm.addData("TRADE_ENG_DESC", resultParm.getValue(
						"TRADE_ENG_DESC", i));
		parm.addData("HIDE_FLG", resultParm.getValue("HIDE_FLG", i));
		parm.addData("ADM_TYPE", resultParm.getValue("ADM_TYPE", i));//就诊类型 :门急诊 
		parm.addData("ORDER_CAT1_CODE", resultParm.getValue("ORDER_CAT1_CODE",
				i));
		parm.addData("TEMPORARY_FLG", resultParm.getValue("TEMPORARY_FLG", i));
	}
	/**
	 * 写入初始值
	 * 
	 * @param billType
	 * @return
	 */
	public boolean setBillSets(TParm parm, List hl7List) {
		// System.out.println("parmL=============>"+parm);
		String filterString = this.getFilter();
		this.setFilter("CASE_NO='" + getCaseNo() + "'");
		this.filter();
		int count = this.rowCount();
		Timestamp now = this.getDBTime();
		int type = parm.getInt("OP_TYPE");
		List list = (ArrayList) parm.getData("RX_LIST");

		// System.out.println("type=============="+type);
		// 成功
		if (type == 1 || type == 4) {
			// System.out.println("type1=============="+type);
			for (int i = 0; i < count; i++) {
				if (!this.isActive(i)) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "RELEASE_FLG"))) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "BILL_FLG"))) {
					continue;
				}
				if (type == 1
						&& list.indexOf(this.getItemString(i, "RX_NO")) < 0) {
					continue;
				}
				if ("5".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))
						&& StringTool.getBoolean(this.getItemString(i,
								"SETMAIN_FLG"))) {
					hl7List.add(getHl7Parm(i, true));
				}
				this.setItem(i, "EXEC_FLG", "N");
				this.setItem(i, "RECEIPT_FLG", "N");
				this.setItem(i, "BILL_TYPE", "E");
				this.setItem(i, "BILL_FLG", "Y");
				this.setItem(i, "BILL_USER", Operator.getID());
				this.setItem(i, "BILL_DATE", now);
				// 判断该医嘱是否是检验检查 更新MED_APPLY表的收费注记
				if (("LIS".equals(this.getItemString(i, "CAT1_TYPE")) || "RIS"
						.equals(this.getItemString(i, "CAT1_TYPE")))
						&& "Y".equals(this.getItemString(i, "SETMAIN_FLG"))) {
					String filter = med.getFilter();
					String fill = " ORDER_NO = '"
							+ this.getItemString(i, "RX_NO")
							+ "' AND SEQ_NO = '"
							+ this.getItemString(i, "SEQ_NO")
							+ "' AND APPLICATION_NO = '"
							+ this.getItemString(i, "MED_APPLY_NO")
							+ "' AND CAT1_TYPE = '"
							+ this.getItemString(i, "CAT1_TYPE") + "'";
					// System.out.println("fill:"+fill);
					med.setFilter(fill);
					med.filter();
					if (med.rowCount() > 0) {
						med.setItem(0, "BILL_FLG", "Y");
					}
					med.setFilter(filter);
					med.filter();
				}
			}
			setDischargeList(hl7List);
		}
		// 只退费，把回传的处方的收费注记置为N，
		else if (type == 2) {
			// System.out.println("type1=============="+type);
			list = (ArrayList) parm.getData("RX_LIST");
			this.setFilter("CASE_NO='" + getCaseNo() + "'");
			this.filter();
			count = this.rowCount();
			for (int i = 0; i < count; i++) {
				if (list.indexOf(this.getItemString(i, "RX_NO")) < 0) {
					continue;
				}
				if (!StringTool.getBoolean(this.getItemString(i, "BILL_FLG"))) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "RECEIPT_NO"))) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "EXEC_FLG"))) {
					continue;
				}
				if ("5".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))
						&& StringTool.getBoolean(this.getItemString(i,
								"SETMAIN_FLG"))) {
					hl7List.add(getHl7Parm(i, false));
				}
				this.setItem(i, "EXEC_FLG", "N");
				this.setItem(i, "RECEIPT_FLG", "N");
				this.setItem(i, "BILL_TYPE", "C");
				this.setItem(i, "BILL_FLG", "N");
				this.setItem(i, "BILL_USER", "");
				this.setItem(i, "BILL_DATE", null);
			}
			setDischargeList(hl7List);
		}
		// 不使用医疗卡或者收费错误
		else if (type == 3) {
			this.setFilter(filterString);
			this.filter();
			// System.out.println("type=-3");
			return false;
		} else {
			this.setFilter(filterString);
			this.filter();
			// System.out.println("type=-2"+type);
			return false;
		}
		this.setFilter(filterString);
		this.filter();
		return true;
	}

	/**
	 * 
	 * @param row
	 * @return
	 */
	public boolean isExecute(int row) {
		if (row < 0) {
			return true;
		}
		String caseNo = this.getItemString(row, "CASE_NO");
		String rxNo = this.getItemString(row, "RX_NO");
		int seq = this.getItemInt(row, "SEQ_NO");
		String sql = GET_EXEC_FLG.replaceFirst("#", caseNo).replaceFirst("#",
				rxNo).replaceFirst("#", seq + "");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			// System.out.println("isExecute.sql="+sql);
			return true;
		}
		return result.getBoolean("EXEC_FLG", 0)
				|| result.getBoolean("RECEIPT_FLG", 0);
	}
	/**
	 * 校验此处方签是否已经打票
	 * pangben 2012-7-24
	 * @param row
	 * @return
	 */
	public boolean isExecutePrint(String caseNo ,String rxNo){
		//校验医嘱处方是否已经打票状态=======pangben 2012-7-24
		String GET_PRINT_FLG = "SELECT ORDER_CODE  FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_NO='"+rxNo+"' AND PRINT_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(GET_PRINT_FLG));
		if (result.getErrCode() != 0) {
			// System.out.println("isExecute.sql="+sql);
			return true;
		}
		if (result.getCount()>0) {
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 给HL7发送消息
	 * 
	 * @return
	 */
	public boolean sendHl7() {
		TParm exaParm = this.getModifiedExaOrder();
		// System.out.println("exaParm=="+exaParm);
		int count = exaParm.getCount();
		if (count <= 0) {
			// System.out.println("sendHl7.count<=0");
			return true;
		}

		List orderList = new ArrayList();
		for (int i = 0; i < count; i++) {
			TParm parm = new TParm();
			parm.setData("ADM_TYPE", getAdmType());
			parm.setData("CAT1_TYPE", exaParm.getValue("CAT1_TYPE", i));
			parm.setData("PAT_NAME", OpdRxSheetTool.getInstance().getPatName(
					this.getMrNo()));
			parm.setData("CASE_NO", this.getCaseNo());
			parm.setData("LAB_NO", exaParm.getValue("MED_APPLY_NO", i));
			parm.setData("ORDER_NO", exaParm.getValue("RX_NO", i));
			parm.setData("SEQ_NO", exaParm.getData("SEQ_NO", i));
			parm.setData("FLG", "0");
			// System.out.println("parm for charge=="+parm);
			orderList.add(parm);
		}
		TParm result = Hl7Communications.getInstance().Hl7Message(orderList);
		if (result == null || result.getErrCode() < 0) {
			System.out.println("传送HL7失败：" + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 退费时取得
	 * 
	 * @return
	 */
	public TParm getDischargeExaParm() {
		TParm parm = new TParm();

		return parm;
	}

	/**
	 * 根据给入药房查询是否有可用的窗口
	 * 
	 * @param orgCode
	 * @return
	 */
	public boolean isOrgAvalible(String orgCode) {
		if (StringUtil.isNullString(orgCode)) {
			return false;
		}
		String sql = this.GET_COUNTER_NO.replaceFirst("#", orgCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			System.out.println(result.getErrText());
			return false;
		}
		int count = result.getCount();
		if (count <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * 判断医嘱是否可删除
	 * 
	 * @param row
	 *            boolean flg 医疗卡操作使用管控
	 * @return =========pangben 2012-2-21 添加医疗卡删除管控
	 */
	public boolean isRemovable(int row, boolean flg) {
		String caseNo = this.getItemString(row, "CASE_NO");
		String rxNo = this.getItemString(row, "RX_NO");
		int seq = this.getItemInt(row, "SEQ_NO");
		String sql = GET_EXEC_FLG.replaceFirst("#", caseNo).replaceFirst("#",
				rxNo).replaceFirst("#", seq + "");
		// System.out.println("isRemovable.sql============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (flg) {
			if (result.getBoolean("PRINT_FLG", 0)) {
				return false;
			}
			
			//add by huangtt 20160613添加判断Q医支付的钱不充许修改
			if(result.getValue("RECEIPT_NO", 0).length() > 0){
				return false;
			}
			
		} else {
			if (result.getBoolean("BILL_FLG", 0)) {
				return false;
				// if("C".equalsIgnoreCase(result.getValue("BILL_TYPE",0))){
				// return false;
				// }
				// if(result.getBoolean("EXEC_FLG",0)||result.getBoolean("PRINT_FLG",0)){
				// return false;
				// }
			}
		}
		return true;
	}
	/**
	 * 校验 检验检查已经登记的数据不能删除操作
	 * STATUS=2 已登记
	 * @param row
	 * @return
	 * ===========pangben 2013-1-29
	 */
	public boolean isRemoveMedAppCheckDate(int row){
		String orderCat1Code = this.getItemString(row, "ORDER_CAT1_CODE");
		String medApplyNo = this.getItemString(row, "MED_APPLY_NO");
		if(null==medApplyNo || medApplyNo.length()<=0)
			return true;
		String sql = GET_STATUS_FLG.replaceFirst("#", orderCat1Code).replaceFirst("#",
				medApplyNo);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<=0){
			return true;
		}
		if (result.getInt("STATUS", 0)==2) {
			return false;
		}
		return true;
	}
	/**
	 * 根据行号返回收费HL7消息的TParm
	 * 
	 * @param row
	 * @return
	 */
	public TParm getHl7Parm(int row, boolean send) {
		if (row < 0) {
			return null;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", getAdmType());
		parm.setData("CAT1_TYPE", this.getItemString(row, "CAT1_TYPE"));
		parm.setData("PAT_NAME", OpdRxSheetTool.getInstance().getPatName(
				this.getMrNo()));
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("LAB_NO", this.getItemString(row, "MED_APPLY_NO"));
		parm.setData("ORDER_NO", this.getItemString(row, "RX_NO"));
		parm.setData("SEQ_NO", this.getItemData(row, "SEQ_NO"));
		if (send) {
			parm.setData("FLG", "0");
		} else {
			parm.setData("FLG", "1");
		}

		// System.out.println("parm for charge=="+parm);
		return parm;
	}

	/**
	 * 判断删除区的检验检查是否需要发送取消的消息，如果需要，则放到接口容器中
	 * 
	 * @param hl7List
	 */
	public void setDischargeList(List hl7List) {
		TParm deleteParm = this.getBuffer(this.DELETE);
		if (deleteParm == null || deleteParm.getCount() <= 0) {
			return;
		}
		int count = deleteParm.getCount();
		for (int i = 0; i < count; i++) {
			if (!StringTool.getBoolean(deleteParm.getValue("BILL_FLG", i))) {
				continue;
			}
			if ("5".equalsIgnoreCase(deleteParm.getValue("RX_TYPE", i))
					&& StringTool.getBoolean(deleteParm.getValue("SETMAIN_FLG",
							i))) {
				TParm exaDisParm = new TParm();
				exaDisParm.setData("ADM_TYPE", getAdmType());
				exaDisParm.setData("CAT1_TYPE", deleteParm.getValue(
						"CAT1_TYPE", i));
				exaDisParm.setData("PAT_NAME", OpdRxSheetTool.getInstance()
						.getPatName(this.getMrNo()));
				exaDisParm.setData("CASE_NO", this.getCaseNo());
				exaDisParm.setData("LAB_NO", deleteParm.getValue(
						"MED_APPLY_NO", i));
				exaDisParm.setData("ORDER_NO", deleteParm.getValue("RX_NO", i));
				exaDisParm.setData("SEQ_NO", deleteParm.getInt("SEQ_NO", i));
				exaDisParm.setData("FLG", "1");
				hl7List.add(exaDisParm);
			}
		}
	}

	/**
	 * 检核药品是否已经审配发 是否可以退药
	 * 
	 * @param type
	 *            String "EXA":西药 "CHN":中药
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(String type, int row) {
		boolean needExamineFlg = false;
		// 如果是西药 审核或配药后就不可以再进行修改或者删除
		if ("MED".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// 如果是中药 审核或配药后就不可以再进行修改或者删除
		if ("CHN".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
		// if (this.getItemString(row, "PHA_CHECK_CODE").length() > 0
		// && this.getItemString(row, "PHA_RETN_CODE").length() == 0) {
		// return false;
		// }
		// 如果有审核流程 那么判断审核医师是否为空
		if (needExamineFlg) {
			// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
			if (this.getItemString(row, "PHA_CHECK_CODE").length() > 0
					&& this.getItemString(row, "PHA_RETN_CODE").length() == 0) {
				return false;
			}
		} else {// 没有审核流程 直接配药
			// 判断是否有配药药师
			if (this.getItemString(row, "PHA_DOSAGE_CODE").length() > 0
					&& this.getItemString(row, "PHA_RETN_CODE").length() == 0) {
				return false;// 已经配药不可以做修改
			}
		}
		return true;
	}
	
	/**
	 * 检核药品是否已经审配发 是否可以退药  add by huangtt 20150602
	 * 
	 * @param type
	 *            String "EXA":西药 "CHN":中药
	 * @param row
	 *            int
	 *            flg 校验删除或修改操作，删除不可以删除一行数据，只能删除处方签
	 *            flg true 删除一行 或者修改动作使用   false  删除处方签使用
	 * @return boolean 
	 */
	public boolean checkDrugCanUpdate(String type, int row,TParm parm,boolean flg) {
		boolean needExamineFlg = false;
		// 如果是西药 审核或配药后就不可以再进行修改或者删除
		if ("MED".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// 如果是中药 审核或配药后就不可以再进行修改或者删除
		if ("CHN".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}

		TParm order=this.getRowParm(row);
		int reIndex=OrderTool.getInstance().checkPhaIsExe(needExamineFlg, order,flg);
		switch (reIndex) {// 0正常 1没有审配发  2.没有退费
		case 1:
			parm.setData("MESSAGE","E0189");
			return false;
		case 2:
			parm.setData("MESSAGE","已计费不可以修改或删除操作");
//			parm.setData("MESSAGE_FLG","Y");
			return false;		
		case 5://已退药  未收费
			parm.setData("MESSAGE","处方已退药不可修改，请删除处方");
//			parm.setData("MESSAGE_FLG","Y");
//			parm.setData("MESSAGE_INDEX","Y");
			return false;
		}
		return true;
	}
	

	/**
	 * 获取集合医嘱细项的默认数量
	 * 
	 * @param order_code
	 *            String 细项CODE
	 * @param orderset_code
	 *            String 主项CODE
	 * @return double
	 */
	public double getOrderSetTot(String order_code, String orderset_code) {
		String sql = "SELECT DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE='"
				+ orderset_code + "' AND ORDER_CODE = '" + order_code + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getDouble("DOSAGE_QTY", 0);
	}

	/**
	 * 判断该处方签是否是专用处方签 如果是则不能开普通药品 如果不是那么不能开专用处方签药品
	 * 
	 * @param rxNo
	 *            String
	 * @param orderCode
	 *            String
	 * @return int 0：orderCode属于该处方签类型 1：该处方是专用处方签 新医嘱不是专用处方药品不能开立 2：该处方签不是专用处方签
	 *         新医嘱是专用处方签药品不能开立 3：都是管制药品 但是新Order的管制药品等级与该处方签不符，不可在此处方签上开立
	 */
	public int isPrnRx(String rxNo, String orderCode) {
		String oldFilter = this.getFilter();
		this.setFilter("RX_NO = '" + rxNo + "'");
		this.filter();
		int count = this.rowCount();
		String oldOrder = this.getItemString(count - 2, "ORDER_CODE");// 上一条order
		this.setFilter(oldFilter);
		this.filter();
		// 如果行数等于0，说明该处方签还没有开药 可以开普通药也可以开专用处方签药品
		if (count <= 0 || oldOrder.length() <= 0) {
			return 0;
		}
		// 查询新ORDER是否是专用处方签药品
		String sql = "SELECT B.PRNSPCFORM_FLG,B.CTRLDRUGCLASS_CODE "
				+ " FROM PHA_BASE A,SYS_CTRLDRUGCLASS B "
				+ " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE(+) AND A.ORDER_CODE='"
				+ orderCode + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		String newOrderFlg = result1.getBoolean("PRNSPCFORM_FLG", 0) ? "Y"
				: "N";
		// 查询该处方的上一条医嘱是否是专用处方签药品
		String sql2 = "SELECT B.PRNSPCFORM_FLG,B.CTRLDRUGCLASS_CODE "
				+ " FROM PHA_BASE A,SYS_CTRLDRUGCLASS B "
				+ " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE(+) AND A.ORDER_CODE='"
				+ oldOrder + "' ";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		String oldOrderFlg = result2.getBoolean("PRNSPCFORM_FLG", 0) ? "Y"
				: "N";
		// 新order和上一条Order是相同类型 那么可以开在同一种处方签上
		if (oldOrderFlg.equals(newOrderFlg)
				&& (result1.getValue("CTRLDRUGCLASS_CODE", 0).equals(
						result2.getValue("CTRLDRUGCLASS_CODE", 0))
						|| result1.getValue("CTRLDRUGCLASS_CODE", 0).length() == 0 || result2
						.getValue("CTRLDRUGCLASS_CODE", 0).length() == 0)) {
			return 0;// 新order可以开立
		} else {// 新order不可开立
			// 都是专属处方签 但是管制等级不同
			if (oldOrderFlg.equals(newOrderFlg)
					&& !result1.getValue("CTRLDRUGCLASS_CODE", 0).equals(
							result2.getValue("CTRLDRUGCLASS_CODE", 0))) {
				return 3;
			}
			if ("Y".equals(oldOrderFlg)) {// 专用处方
				return 1;
			} else {// 不是专用处方
				return 2;
			}
		}
	}

	/**
	 * 四舍五入 保留两位小数
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	public double roundAmt(double value) {
		double result = 0;
		if (value > 0)
			result = ((int) (value * 100.0 + 0.5)) / 100.0;
		else if (value < 0)
			result = ((int) (value * 100.0 - 0.5)) / 100.0;
		return result;
	}

	/**
	 * 判断是否检核库存
	 * 
	 * @param orderCode
	 *            String
	 */
	private boolean isCheckKC(String orderCode) {
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0))// 如果是药品备注那么就不检核库存
			return false;
		else
			// 不是药品备注的 要检核库存
			return true;
	}

	public void updateMED(ODO odo) {
		TParm orderParm = odo.getOpdOrder().getBuffer("Modify!");
		// odo.getOpdOrder().setFilter("CAT1_TYPE in('LIS','RIS')");
		// odo.getOpdOrder().filter();
		// TParm modifyParm = odo.getOpdOrder().getModifiedExaOrder();

		TParm primaryParm = odo.getOpdOrder().getBuffer("Filter!");
		int primaryCount = primaryParm.getCount("SEQ_NO");

		for (int i = 0; i < orderParm.getCount("MED_APPLY_NO"); i++) {
			String APPLICATION_NO = (String) orderParm.getData("MED_APPLY_NO",
					i);

			if (StringUtil.isNullString(APPLICATION_NO)) {
				continue;
			}
			Integer SEQ_NO = Integer.valueOf(orderParm.getData("SEQ_NO", i)
					+ "");
			String CASE_NO = (String) orderParm.getData("CASE_NO", i);
			String ORDER_CODE = (String) orderParm.getData("ORDER_CODE", i);
			med.setFilter("APPLICATION_NO='" + APPLICATION_NO + "' and SEQ_NO="
					+ SEQ_NO + " and ORDER_CODE='" + ORDER_CODE + "'");
			med.filter();
			for (int primaryIndex = 0; primaryIndex < primaryCount; primaryIndex++) {
				TParm tempParm = primaryParm.getRow(primaryIndex);
				if (tempParm.getValue("CAT1_TYPE").equals(
						orderParm.getData("CAT1_TYPE", i))
						&& tempParm.getValue("RX_NO").equals(
								orderParm.getData("RX_NO", i))
						&& tempParm.getValue("SEQ_NO").equals(
								orderParm.getData("SEQ_NO", i) + "")
						&& tempParm.getValue("ORDER_CODE").equals(
								orderParm.getData("ORDER_CODE", i))) {

					// //////////////////////////////////////////////////////////////////////////////////////////////////
					// xueyf
					// value.add(labNo);//0
					// value.add(cat1Type);//1
					int medRow = 0;
					med.setItem(medRow, "CAT1_TYPE", tempParm
							.getData("CAT1_TYPE"));
					// // ORDER_NO
					med.setItem(medRow, "ORDER_NO", tempParm.getData("RX_NO"));
					// value.add(this.getItemString(row, "RX_NO"));//2
					// //SEQ_NO
					// System.out.println("med_apply~~~~~~~~~~:"+this.getItemData(row,
					// "SEQ_NO"));
					med.setItem(medRow, "SEQ_NO", tempParm.getData("SEQ_NO"));
					// value.add(this.getItemData(row, "SEQ_NO"));//3
					// //ORDER_CODE
					med.setItem(medRow, "ORDER_CODE", tempParm
							.getData("ORDER_CODE"));
					// value.add(this.getItemData(row, "ORDER_CODE"));//4
					// //ORDER_DESC
					med.setItem(medRow, "ORDER_DESC", tempParm
							.getData("ORDER_DESC"));
					// value.add(this.getItemData(row, "ORDER_DESC"));
					// //ORDER_DR_CODE
					med.setItem(medRow, "ORDER_DR_CODE", tempParm
							.getData("DR_CODE"));
					// value.add(Operator.getID());
					Timestamp now = this.getDBTime();
					// //ORDER_DATE
					med.setItem(medRow, "ORDER_DATE", now);
					// value.add(now);
					// //ORDER_DEPT_CODE
					med.setItem(medRow, "ORDER_DEPT_CODE", odo.getRegPatAdm()
							.getItemString(0, "REALDEPT_CODE"));
					// value.add(Operator.getDept());
					// //START_DTTM
					med.setItem(medRow, "START_DTTM", now);
					// value.add(now);
					// //EXEC_DEPT_CODE
					med.setItem(medRow, "EXEC_DEPT_CODE", tempParm
							.getData("EXEC_DEPT_CODE"));
					// value.add(this.getItemData(row, "EXEC_DEPT_CODE"));
					// //EXEC_DR_CODE
					med.setItem(medRow, "EXEC_DR_CODE", "");
					// value.add("");
					// //OPTITEM_CODE
					med.setItem(medRow, "OPTITEM_CODE", tempParm
							.getData("OPTITEM_CODE"));
					// value.add(this.getItemData(row, "OPTITEM_CODE"));
					String optitemDesc = StringUtil.getDesc("SYS_DICTIONARY",
							"CHN_DESC", " GROUP_ID = 'SYS_OPTITEM' AND ID='"
									+ tempParm.getData("OPTITEM_CODE") + "'");
					// //OPTITEM_CHN_DESC
					med.setItem(medRow, "OPTITEM_CHN_DESC", optitemDesc);
					// value.add(optitemDesc);
					// //ORDER_CAT1_CODE
					med.setItem(medRow, "ORDER_CAT1_CODE", tempParm
							.getData("ORDER_CAT1_CODE"));
					// URGENT_FLG
					med.setItem(medRow, "URGENT_FLG", tempParm
							.getData("URGENT_FLG"));
					// value.add(this.getItemData(row, "ORDER_CAT1_CODE"));
					String dealSystem = StringUtil.getDesc("SYS_OPTITEM",
							"OPTITEM_CHN_DESC", "OPTITEM_CODE='"
									+ tempParm.getData("OPTITEM_CODE") + "'");
					// //DEAL_SYSTEM
					med.setItem(medRow, "DEAL_SYSTEM", dealSystem);
					// value.add(OrderUtil.getInstance().getDealSystem(this.getItemString(row,
					// "ORDER_CAT1_CODE")));
					// //RPTTYPE_CODE
					med.setItem(medRow, "RPTTYPE_CODE", tempParm
							.getData("RPTTYPE_CODE"));
					// value.add(this.getItemData(row, "RPTTYPE_CODE"));
					// //DEV_CODE
					med.setItem(medRow, "DEV_CODE", tempParm
							.getData("DEV_CODE"));
					// value.add(this.getItemData(row, "DEV_CODE"));
					// //REMARK
					med.setItem(medRow, "REMARK", tempParm.getData("DR_NOTE"));
					// value.add(this.getItemData(row, "DR_NOTE"));
					// OWN_AMT
					med.setItem(medRow, "OWN_AMT", tempParm.getData("OWN_AMT"));
					// AR_AMT
					med.setItem(medRow, "AR_AMT", tempParm.getData("AR_AMT"));

					// CASE_NO
					med.setItem(medRow, "CASE_NO", this.getCaseNo());
					// MR_NO
					med.setItem(medRow, "MR_NO", this.getMrNo());
					// ADM_TYPE
					// System.out.println("MED_APPLY~~~~~~~~~~~~~~~~门急别显示："+getAdmType());
					med.setItem(medRow, "ADM_TYPE", getAdmType());
					// PAT_NAME
					med.setItem(medRow, "PAT_NAME", odo.getPatInfo()
							.getItemString(0, "PAT_NAME"));
					// PAT_NAME1
					med.setItem(medRow, "PAT_NAME1", odo.getPatInfo()
							.getItemString(0, "PAT_NAME1"));
					// BIRTH_DATE
					med.setItem(medRow, "BIRTH_DATE", odo.getPatInfo()
							.getItemData(0, "BIRTH_DATE"));
					// SEX_CODE
					med.setItem(medRow, "SEX_CODE", odo.getPatInfo()
							.getItemData(0, "SEX_CODE"));
					// POST_CODE
					med.setItem(medRow, "POST_CODE", odo.getPatInfo()
							.getItemData(0, "POST_CODE"));
					// ADDRESS
					med.setItem(medRow, "ADDRESS", odo.getPatInfo()
							.getItemData(0, "ADDRESS"));
					// COMPANY
					med.setItem(medRow, "COMPANY", odo.getPatInfo()
							.getItemData(0, "COMPANY"));
					// TEL
					med.setItem(medRow, "TEL", odo.getPatInfo().getItemData(0,
							"TEL"));
					// IDNO
					med.setItem(medRow, "IDNO", odo.getPatInfo().getItemData(0,
							"IDNO"));
					// DEPT_CODE
					med.setItem(medRow, "DEPT_CODE", odo.getRegPatAdm()
							.getItemString(0, "REALDEPT_CODE"));
					// REGION_CODE
					med.setItem(medRow, "REGION_CODE", Operator.getRegion());
					// CLINICAREA_CODE
					med.setItem(medRow, "CLINICAREA_CODE", odo.getRegPatAdm()
							.getItemString(0, "CLINICAREA_CODE"));
					// CLINICROOM_NO CLINICROOM_NO
					med.setItem(medRow, "CLINICROOM_NO", odo.getRegPatAdm()
							.getItemString(0, "CLINICROOM_NO"));
					// ICD_TYPE
					int main = odo.getDiagrec().getMainDiag();
					med.setItem(medRow, "ICD_TYPE", odo.getDiagrec()
							.getItemData(main, "ICD_TYPE"));
					// ICD_CODE
					med.setItem(medRow, "ICD_CODE", odo.getDiagrec()
							.getItemData(main, "ICD_CODE"));
					// STATUS
					med.setItem(medRow, "STATUS", "0");
					// BILL_FLG
					med.setItem(medRow, "BILL_FLG", "N");
					// SEND_FLG
					med.setItem(medRow, "SEND_FLG", "0");
					// OPT_USER
					med.setItem(medRow, "OPT_USER", Operator.getID());
					// OPT_DATE
					med.setItem(medRow, "OPT_DATE", now);
					// OPT_TERM
					med.setItem(medRow, "OPT_TERM", Operator.getIP());
					// PRINT_FLG
					med.setItem(medRow, "PRINT_FLG", "N");
					// ORDER_ENG_DESC
					med.setItem(medRow, "ORDER_ENG_DESC", tempParm
							.getData("TRADE_ENG_DESC"));

					med.setActive(medRow, true);
				}
			}
		}
	}

	/**
	 * 判断此条医嘱是否已经收费，门急诊医生站 已经收费药品医嘱 不可以改备注 操作
	 * 
	 * @return
	 * ============pangben 2012-7-19
	 */
	public boolean getOrderCodeIsBillFlg(TParm parm) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ORDER_CODE,BILL_FLG FROM OPD_ORDER WHERE CASE_NO='")
				.append(parm.getValue("CASE_NO")).append("' AND RX_NO='")
				.append(parm.getValue("RX_NO")).append("' AND SEQ_NO='")
				.append(parm.getValue("SEQ_NO")).append("' AND ORDER_CODE='")
				.append(parm.getValue("ORDER_CODE")).append("'");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return false;
		} else {
			if (null!=result.getValue("BILL_FLG",0)&&"Y".equals(result.getValue("BILL_FLG",0))) {
				return true;
			}else
				return false;
		}
	}

	/**
	 * 获得此次修改的金额 EKT扣款显示使用
	 */
	private double getUpdateOrderAmt(TParm orderParm, TParm orderOldParm) {
		for (int j = 0; j < orderOldParm.getCount("ORDER_CODE"); j++) {
			if (orderParm.getValue("RX_NO").equals(
					orderOldParm.getValue("RX_NO", j))
					&& orderParm.getValue("SEQ_NO").equals(
							orderOldParm.getValue("SEQ_NO", j))) {
				if (orderParm.getDouble("AR_AMT") == orderOldParm.getDouble(
						"AR_AMT", j)) {
					return 0;
				}
				return orderParm.getDouble("AR_AMT")
						- orderOldParm.getDouble("AR_AMT", j);
			}
		}
		return 0;
	}
}
