package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jdo.spc.INDBillPageTool;
import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <strong>Title : INDBillPageControl<br>
 * </strong> <strong>Description : </strong>ҩ����ҳ<br>
 * <strong>Create on : 2012-1-25<br>
 * </strong>
 * <p>
 * <strong>Copyright (C) <br>
 * </strong>
 * <p>
 * 
 * @author luhai<br>
 * @version <strong>ProperSoft</strong><br>
 * <br>
 *          <strong>�޸���ʷ:</strong><br>
 *          �޸��� �޸����� �޸�����<br>
 *          -------------------------------------------<br>
 * <br>
 * <br>
 */
public class INDBillPageControl extends TControl {
	// ҳ��ؼ�
	private TComboBox orgCombo;
	private TTextFormat startDate;
	private TTextFormat endDate;
	private TTable medTable;
	private TTable billTable;
	private TTextField orderCode;
	private TTextField orderDesc;
	private TTextField orderDescription;
	private TTextField orderUnitDesc;
	// ���ڸ�ʽ��
	private SimpleDateFormat formateDate = new SimpleDateFormat("yyyy/MM/dd");
	// regionCode
	private String regionCode = Operator.getRegion();
	// ��ҳ����
	private List<BillPageBean> billPageList;

	private final static String LAST_AMT_DESC = "���ڽ��";
	private final static String CURRENT_AMT_DESC = "���ڽ��";

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// System.out.println("ϵͳ��ʼ��");
		initComponent();
		initPage();
		// System.out.println("ϵͳ��ʼ�����");
	}

	/**
	 * 
	 * ��ʼ���ؼ�Ĭ��ֵ
	 */
	public void initPage() {
		// ���ÿ���������ֵbegin
		// this.orderCode.setse
		TParm parmCbo = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndOrgComobo("A", "", Operator.getRegion())));
		this.orgCombo.setParmValue(parmCbo);
		// ���ÿ���������ֵend
		// ���´���ʼʱ��ͽ���ʱ�� begin luhai 2011-12-07
		Calendar cd = Calendar.getInstance();
		Calendar cdto = Calendar.getInstance();
		cd.add(Calendar.MONTH, -1);
		cd.set(Calendar.DAY_OF_MONTH, 26);
		cdto.set(Calendar.DAY_OF_MONTH, 25);
		String format = formateDate.format(cd.getTime());
		this.startDate.setValue(formateDate.format(cd.getTime()));
		this.endDate.setValue(formateDate.format(cdto.getTime()));
		// ���´���ʼʱ��ͽ���ʱ�� begin luhai 2011-12-07
		// ��ʼ��Ĭ��ҩ��
		orgCombo.setSelectedIndex(1);
		// ��ʼ��ҩƷ�б������ begin
		initMedTable(orgCombo.getValue(), this.regionCode);
		// ��ʼ��ҩƷ�б������ end
		// ��ʼ��ҽ����textField
		// ֻ��text���������������sys_fee������
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER",
				"%ROOT%\\config\\sys\\SYSFeePopup.x", parm);

		// textfield���ܻش�ֵ
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * ����У��
	 */
	public void onCheck() {
		Object result = openDialog("%ROOT%\\config\\spc\\INDBillPageCheck.x",
				"");
	}

	public void popReturn(String tag, Object obj) {
		TParm parmrtn = (TParm) obj;
		this.setValue("ORDER_CODE", parmrtn.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parmrtn.getValue("ORDER_DESC"));
		// ����ҩƷ�������Ϣ
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", parmrtn.getValue("ORDER_CODE"));
		parm.setData("ORG_CODE", this.orgCombo.getValue());
		parm.setData("REGION_CODE", regionCode);
		TParm selectSysFeeMed = INDBillPageTool.getInstance().selectSysFeeMed(
				parm);
		if (selectSysFeeMed.getCount("ORDER_CODE") <= 0) {
			return;
		}
		String orderCode = selectSysFeeMed.getValue("ORDER_CODE", 0);
		String orderDesc = selectSysFeeMed.getValue("ORDER_DESC", 0);
		String orderDescription = selectSysFeeMed.getValue("SPECIFICATION", 0);
		String orderUnitDesc = selectSysFeeMed.getValue("UNIT_CHN_DESC", 0);
		this.orderCode.setValue(orderCode);
		this.orderDesc.setValue(orderDesc);
		this.orderDescription.setValue(orderDescription);
		this.orderUnitDesc.setValue(orderUnitDesc);

	}

	public void queryMedList() {
		String orgValue = orgCombo.getValue();
		if ("".equals(orgValue)) {
			this.messageBox("��ѡ���ѯҩ�⣡");
			return;
		}
		// ��ʼ��ҩƷ�б������ begin
		initMedTable(orgCombo.getValue(), this.regionCode);
		// ��ʼ��ҩƷ�б������ end
	}

	/**
	 * 
	 * ��ѯ��ҳ����ϸ��Ϣ
	 * 
	 * @throws ParseException
	 */
	public void onQuery() throws ParseException {
		// StringBuffer sqlbf = new StringBuffer("");
		// sqlbf.append(" select ORDER_CODE from  sys_fee where  (order_code like 'Y01%'  OR order_code like 'Y02%' or order_code like 'Y03%')  ");
		// Map select = TJDODBTool.getInstance().select(sqlbf.toString());
		// TParm resultsorce=new TParm(select);
		// for(int i=0;i<resultsorce.getCount("ORDER_CODE");i++){
		// String orderCode=resultsorce.getValue("ORDER_CODE",i);
		// StringBuffer sqlbforder=new StringBuffer();
		// sqlbforder.append(" SELECT B.CHARGE_HOSP_CODE FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND  ORDERSET_CODE='"+orderCode+"' ");
		// Map selectcharge =
		// TJDODBTool.getInstance().select(sqlbforder.toString());
		// TParm chargeCodeParm = new TParm(selectcharge);
		// String chargeCode=chargeCodeParm.getValue("CHARGE_HOSP_CODE",0);
		// System.out.println("chargeCode:"+chargeCode);
		// StringBuffer updateSql=new StringBuffer();
		// updateSql.append(" update sys_fee set CHARGE_HOSP_CODE='"+chargeCode+"' where order_code='"+orderCode+"'");
		// System.out.println(updateSql);
		// Map update = TJDODBTool.getInstance().update(updateSql.toString());
		// TParm result = new TParm (update);
		// if(result.getErrCode()<=0){
		// System.out.println("error"+result.getErrText());
		// }else {
		// System.out.println("00000000ok");
		// }
		// updateSql.delete(0, updateSql.toString().length());
		// updateSql.append(" update sys_fee_history set CHARGE_HOSP_CODE='"+chargeCode+"' where order_code='"+orderCode+"'");
		// System.out.println(updateSql);
		// update = TJDODBTool.getInstance().update(updateSql.toString());
		// result = new TParm (update);
		// if(result.getErrCode()<=0){
		// System.out.println("error"+result.getErrText());
		// }else {
		// System.out.println("00000000ok");
		// }
		// }
		String orgCode = this.orgCombo.getValue();
		String orderCode = this.orderCode.getValue();
		if ("".equals(orgCode)) {
			this.messageBox("��ѡ���ѯ���ţ�");
			return;
		}
		if ("".equals(orderCode)) {
			this.messageBox("�������ѯҩƷ��");
			return;
		}
		if ("".equals(startDate.getValue())) {
			this.messageBox("�����뿪ʼʱ�䣡");
			return;
		}
		if ("".equals(endDate.getValue())) {
			this.messageBox("���������ʱ�䣡");
			return;
		}
		// initBillDetailTable(orgCode, orderCode, this.regionCode);
		initBillPageDetailTableNew(orgCode, orderCode, this.regionCode);
	}

	/**
	 * 
	 * ҩƷ�б�click�¼�
	 */
	public void onMedTableClick() {
		int selectedIndx = this.medTable.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		TParm tableparm = this.medTable.getParmValue();
		// ��ҳ���ϴ���ҩƷ���������
		String orderCode = tableparm.getValue("ORDER_CODE", selectedIndx);
		String orderDesc = tableparm.getValue("ORDER_DESC", selectedIndx);
		String orderDescription = tableparm.getValue("SPECIFICATION",
				selectedIndx);
		String orderUnitDesc = tableparm
				.getValue("UNIT_CHN_DESC", selectedIndx);
		this.orderCode.setValue(orderCode);
		this.orderDesc.setValue(orderDesc);
		this.orderDescription.setValue(orderDescription);
		this.orderUnitDesc.setValue(orderUnitDesc);
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (this.billTable.getRowCount() <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}
		TParm prtParm = new TParm();
		// ��ͷ
		prtParm.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()) + "ҩ����ҳ");
		String startDate = this.startDate.getValue().toString();
		String endDate = this.endDate.getValue().toString();
		startDate = startDate.substring(0, 10).replace("-", "/");
		endDate = endDate.substring(0, 10).replace("-", "/");
		prtParm.setData("START_DATE", "TEXT", startDate);
		prtParm.setData("END_DATE", "TEXT", endDate);
		prtParm.setData("UNIT", "TEXT", this.orderUnitDesc.getValue());
		prtParm.setData("MED_NAME", "TEXT", this.orderDesc.getValue());
		prtParm.setData("TYPE", "TEXT", this.orderDescription.getValue());
		prtParm.setData("UNIT", "TEXT", this.orderUnitDesc.getValue());
		TParm tableparm = this.billTable.getParmValue();
		tableparm.setCount(tableparm.getCount("DESC"));
		// ����������
		tableparm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
		tableparm.addData("SYSTEM", "COLUMNS", "BILL_NO");
		tableparm.addData("SYSTEM", "COLUMNS", "DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "IN_NUM");
		tableparm.addData("SYSTEM", "COLUMNS", "IN_PRICE");
		tableparm.addData("SYSTEM", "COLUMNS", "IN_AMT");
		tableparm.addData("SYSTEM", "COLUMNS", "OUT_NUM");
		tableparm.addData("SYSTEM", "COLUMNS", "OUT_PRICE");
		tableparm.addData("SYSTEM", "COLUMNS", "OUT_AMT");
		tableparm.addData("SYSTEM", "COLUMNS", "LAST_NUM");
		tableparm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
		tableparm.addData("SYSTEM", "COLUMNS", "LAST_AMT");
		prtParm.setData("TABLE", tableparm.getData());
		// ��β
		prtParm.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDBillPage.jhw",
				prtParm);
	}

	public void onExport() {
		if (billTable.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(billTable, "ҩ����ҳ");
	}

	/**
	 * ��д ��д ҩ����ҳ ���� �㷨
	 * ��ʼ����ҳ����  === add by wukai on 20170116 ===
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @param regionCode
	 */
	@SuppressWarnings("deprecation")
	private void initBillPageDetailTableNew(String orgCode, String orderCode,
			String regionCode) {
		TParm parm = new TParm();
		parm.setData("ORG_CODE", orgCode);
		parm.setData("REGION_CODE", regionCode);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("START_DATE", (startDate.getValue() + "").substring(0, 10)
				.replace("-", "") + "000000");
		parm.setData("END_DATE", (endDate.getValue() + "").substring(0, 10)
				.replace("-", "") + "235959");
		this.billPageList = new ArrayList<INDBillPageControl.BillPageBean>();
		// �õ�����parm
		TParm dispenseINParm = INDBillPageTool.getInstance().selectDispenseIN(
				parm);
		this.billPageList.addAll(getBillPageBeanList(dispenseINParm));
		// �õ������parm
		TParm dispenseOUTParm = INDBillPageTool.getInstance()
				.selectDispenseOUT(parm);
		this.billPageList.addAll(getBillPageBeanList(dispenseOUTParm));
		// �õ����յ�parm
		TParm verifyinParm = INDBillPageTool.getInstance().selectVerifyin(parm);
		this.billPageList.addAll(getBillPageBeanList(verifyinParm));
		// �õ��˻���Parm
		TParm regressParm = INDBillPageTool.getInstance().selectRegress(parm);
		this.billPageList.addAll(getBillPageBeanList(regressParm));
		// �õ��˻���Parm
		TParm qtyCheckParm = INDBillPageTool.getInstance().selectQtyCheck(parm);
		this.billPageList.addAll(getBillPageBeanList(qtyCheckParm));
		Collections.sort(billPageList, new Comparator<BillPageBean>() {
			public int compare(BillPageBean o1, BillPageBean o2) {
				if (o1 == null && o2 == null)
					return 0;
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getBillDate().compareTo(o2.getBillDate());
			}
		});
//		System.out.println("@@@@ initBillPageDetailTableNew billPageList "
//				+ billPageList);

		// �����һ����Ҫͳ�ƶ����£��Լ�ͳ�ƿ�ʼʱ��
		Timestamp startTime = (Timestamp) startDate.getValue();
		Timestamp endTime = (Timestamp) endDate.getValue();
		// Call �����·ݸ��� �Լ���������ʱ�� ��ʼͳ��ʱ�� 
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeInMillis(startTime.getTime());
		int startYear = cal.get(Calendar.YEAR);
		int startMonth = cal.get(Calendar.MONTH) + 1;
		int startDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTimeInMillis(endTime.getTime());
		int endYear = cal.get(Calendar.YEAR);
		int endMonth = cal.get(Calendar.MONTH) + 1;
		int endDay = cal.get(Calendar.DAY_OF_MONTH);
		int monthCount = 0; //ͳ���·ݸ���
		//==== ���㷶Χ����Ҫͳ���·ݸ��� === start
		if (endYear == startYear) {
			monthCount = endMonth - startMonth;
			if (startDay <= 25 && endDay <= 25 || startDay > 25 && endDay > 25) {
				monthCount++;
			} else if (startDay <= 25 && endDay > 25) {
				monthCount += 2;
			}
		} else {
			monthCount = (endYear - startYear) * 12 - (startMonth - 1)
					+ endMonth;
			if (startDay > 25 && endDay <= 25) {
				monthCount--;
			} else if (startDay <= 25 && endDay > 25) {
				monthCount++;
			}
		}
		//==== ���㷶Χ����Ҫͳ���·ݸ��� === end
		//==== ���㿪ʼʱ��  ==== start
		if (startMonth == 12) {
			if (startDay <= 25)
				cal.set(startYear, startMonth - 1, 25);
			else
				cal.set(startYear + 1, 0, 25);
		} else {
			if (startDay <= 25)
				cal.set(startYear, startMonth - 1, 25);
			else
				cal.set(startYear, startMonth, 25);
		}
		//==== ���㿪ʼʱ��  ==== end
//		System.out.println("@@@@ initBillPageDetailTableNew startMonth "
//				+ startMonth);
//		System.out.println("@@@@ initBillPageDetailTableNew startDay "
//				+ startDay);
//		System.out.println("@@@@ initBillPageDetailTableNew monthCount "
//				+ monthCount);
//		System.out.println("@@@@ initBillPageDetailTableNew startCal "
//				+ this.formateDate.format(cal.getTime()));
//		System.out.println("@@@@ ================================ @@@@\n");
		// ��������е�����
		List<BillPageBean> totalMonthList = new ArrayList<BillPageBean>();
		// ÿ�µ��ۼ����� �ݴ�pageBean
		BillPageBean grandBillPage = new BillPageBean();
		// ���½�桢���ڽ�桢���ڽ�� TParm ����
		TParm selectParmEvery = new TParm();
		// ���½�桢���ڽ�桢���ڽ�� TParm ���
		TParm selectStockQty = new TParm();
		for (int i = 0; i < monthCount; i++) {
			// ѭ���������ҵ���ǰͳ�����·����е�����
			if (i != 0)
				cal.add(Calendar.MONTH, 1);
			int billPageMonth = 0;
			int billPageDay = 0;
			int billPageYear = 0;
			int summerYear = cal.get(Calendar.YEAR);
			int summerMonth = cal.get(Calendar.MONTH) + 1;
			BillPageBean tempMonthSumBean; // ���»�����Ϣbean(���ºϼơ��ۼơ����½��)
			double monthInAmt = 0, monthOutAmt = 0, monthInNum = 0, monthOutNum = 0;
			for (BillPageBean billPage : billPageList) {
				billPageYear = billPage.getBillDate().getYear() + 1900;
				billPageMonth = billPage.getBillDate().getMonth() + 1;
				billPageDay = billPage.getBillDate().getDate();
				if (billPageYear == summerYear) { // ͬ���ж�
					if ((billPageMonth == summerMonth && billPageDay <= 25)
							|| (billPageMonth - summerMonth == -1 && billPageDay > 25)) {
						totalMonthList.add(billPage);
						monthInAmt += billPage.getInAmt();
						monthOutAmt += billPage.getOutAmt();
						monthInNum += billPage.getInNum();
						monthOutNum += billPage.getOutNum();
					}
				} else if (summerYear - billPageYear == 1
						&& billPageMonth == 12 
						&& billPageDay > 26
						&& summerMonth == 1) {
					// �����ж�
					totalMonthList.add(billPage);
					monthInAmt += billPage.getInAmt();
					monthOutAmt += billPage.getOutAmt();
					monthInNum += billPage.getInNum();
					monthOutNum += billPage.getOutNum();
				}
			}
			// 1.���ºϼ�����
			tempMonthSumBean = new BillPageBean();
			tempMonthSumBean.setDesc("���ºϼ�");
			tempMonthSumBean.setBillDate(new Timestamp(cal.getTimeInMillis()));
			tempMonthSumBean.setInAmt(monthInAmt);
			tempMonthSumBean.setOutAmt(monthOutAmt);
			tempMonthSumBean.setInNum(monthInNum);
			tempMonthSumBean.setOutNum(monthOutNum);
			totalMonthList.add(tempMonthSumBean);

			// 2.�ۼ�
			tempMonthSumBean = new BillPageBean();
			tempMonthSumBean.setDesc("�ۼ�");
			tempMonthSumBean.setBillDate(new Timestamp(cal.getTimeInMillis()));
			tempMonthSumBean.setInAmt(grandBillPage.getInAmt() + monthInAmt);
			tempMonthSumBean.setOutAmt(grandBillPage.getOutAmt() + monthOutAmt);
			tempMonthSumBean.setInNum(grandBillPage.getInNum() + monthInNum);
			tempMonthSumBean.setOutNum(grandBillPage.getOutNum() + monthOutNum);
			totalMonthList.add(tempMonthSumBean);

			// 3.�ۼ��ݴ�����ֵ��������һ���·��ۼ�
			grandBillPage.setInAmt(grandBillPage.getInAmt() + monthInAmt);
			grandBillPage.setOutAmt(grandBillPage.getOutAmt() + monthOutAmt);
			grandBillPage.setInNum(grandBillPage.getInNum() + monthInNum);
			grandBillPage.setOutNum(grandBillPage.getOutNum() + monthOutNum);

			// 4.���½��
			int defaultNumEvery = 0;
			selectParmEvery.setData("ORDER_CODE", this.orderCode.getValue());
			selectParmEvery.setData("ORG_CODE", this.orgCombo.getValue());
			selectParmEvery.setData("TRANDATE", formateDate.format(cal.getTime()));
			selectStockQty = INDBillPageTool.getInstance().selectStockQty(
					selectParmEvery);
			if (null != selectStockQty
					&& selectStockQty.getCount("STOCK_QTY") > 0) {
				for (int x = 0; x < selectStockQty.getCount("STOCK_QTY"); x++) {
					tempMonthSumBean = new BillPageBean();
					if (x == 0) {
						tempMonthSumBean.setDesc("���½��");
					} else {
						tempMonthSumBean.setDesc("");
					}
					tempMonthSumBean.setBillDate(new Timestamp(cal
							.getTimeInMillis()));
					tempMonthSumBean.setLastNum(selectStockQty.getDouble(
							"STOCK_QTY", x));
					tempMonthSumBean.setLastPrice(selectStockQty.getDouble(
							"STOCK_PRICE", x));
					tempMonthSumBean.setLastAmt(StringTool.round(
							selectStockQty.getDouble("STOCK_AMT", x), 7));
					totalMonthList.add(tempMonthSumBean);
				}
			} else {
				tempMonthSumBean = new BillPageBean();
				tempMonthSumBean.setDesc("���½��");
				tempMonthSumBean.setBillDate(new Timestamp(cal.getTimeInMillis()));
				tempMonthSumBean.setLastNum(defaultNumEvery);
				tempMonthSumBean.setLastPrice(defaultNumEvery);
				tempMonthSumBean.setLastAmt(defaultNumEvery);
				totalMonthList.add(tempMonthSumBean);
			}
		}
		
		// 5.���ڽ�棨ָ�ϸ�ͳ���µĽ�棩
		// �������ڽ��ͳ��ʱ��
		if(startMonth != 1) {
			if(startDay <= 25) {
				cal.set(startYear, startMonth - 2, 25);
			} else {
				cal.set(startYear, startMonth - 1, 25);
			}
		} else {
			if(startDay <= 25) {  //ȥ��12�·�
				cal.set(startYear - 1, 11, 25);
			} else { //����1��
				cal.set(startYear, startMonth - 1, 25);
			}
		}
		BillPageBean lastYear = new BillPageBean();
		selectParmEvery.setData("ORDER_CODE", this.orderCode.getValue());
		selectParmEvery.setData("ORG_CODE", this.orgCombo.getValue());
		selectParmEvery.setData("TRANDATE", formateDate.format(cal.getTime()));
		selectStockQty = INDBillPageTool.getInstance().selectStockQty(selectParmEvery);
		int defaultNum = 0;
		if (null != selectStockQty && selectStockQty.getCount("STOCK_QTY") > 0) {
			for(int x = 0; x < selectStockQty.getCount("STOCK_QTY"); x ++) {
				lastYear = new BillPageBean();
				if(x == 0) 
					lastYear.setDesc(LAST_AMT_DESC);
				else
					lastYear.setDesc("");
				lastYear.setBillDate(new Timestamp(cal.getTimeInMillis()));
				lastYear.setLastNum(selectStockQty.getDouble("STOCK_QTY", x));
				lastYear.setLastPrice(selectStockQty.getDouble("STOCK_PRICE", x));
				lastYear.setLastAmt(StringTool.round(selectStockQty.getDouble("STOCK_AMT", x), 7));
				totalMonthList.add(x, lastYear);
			}
		} else {
			lastYear.setDesc(LAST_AMT_DESC);
			lastYear.setBillDate(new Timestamp(cal.getTimeInMillis()));
			lastYear.setLastNum(defaultNum);
			lastYear.setLastPrice(defaultNum);
			lastYear.setLastAmt(defaultNum);
			totalMonthList.add(0, lastYear);
		}
		
		// 6. ���ڽ��
		// ��ȡ���ڽ��ʱ��  = ����ʱ��
		cal.set(endYear, endMonth - 1, endDay);
		BillPageBean currentMonth = new BillPageBean();
		selectParmEvery.setData("ORDER_CODE", this.orderCode.getValue());
		selectParmEvery.setData("ORG_CODE", this.orgCombo.getValue());
		selectParmEvery.setData("TRANDATE", this.formateDate.format(cal.getTime()));
		selectStockQty = INDBillPageTool.getInstance().selectStockQty(selectParmEvery);
		if (null != selectStockQty && selectStockQty.getCount("STOCK_QTY") > 0) {
			for (int x = 0; x < selectStockQty.getCount("STOCK_QTY"); x++) {
				currentMonth = new BillPageBean();
				if (x == 0) 
					currentMonth.setDesc(CURRENT_AMT_DESC);
				currentMonth.setBillDate(new Timestamp(cal.getTimeInMillis()));
				currentMonth.setLastNum(selectStockQty.getDouble("STOCK_QTY", x));
				currentMonth.setLastPrice(selectStockQty.getDouble(
						"STOCK_PRICE", x));
				currentMonth.setLastAmt(StringTool.round(
						selectStockQty.getDouble("STOCK_AMT", x), 7));
				totalMonthList.add(currentMonth);
			}
		} else {
			currentMonth.setDesc(CURRENT_AMT_DESC);
			currentMonth.setBillDate(new Timestamp(cal.getTimeInMillis()));
			currentMonth.setLastNum(defaultNum);
			currentMonth.setLastPrice(defaultNum);
			currentMonth.setLastAmt(defaultNum);
			totalMonthList.add(currentMonth);
		}
//		System.out.println("@@@@ initBillPageDetailTableNew totalMonthList \n"
//				+ totalMonthList);
		this.billPageList = totalMonthList;
		totalMonthList = null;
		TParm tableParm = getTParmFromBeanList(this.billPageList);
		this.billTable.setParmValue(tableParm);

	}
	

	/**
	 * ��ʼ����ҳ��ϸ��
	 * 
	 * @param orgCode
	 * @param regionCode
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	private void initBillDetailTable(String orgCode, String orderCode,
			String regionCode) throws ParseException {
		TParm parm = new TParm();
		parm.setData("ORG_CODE", orgCode);
		parm.setData("REGION_CODE", regionCode);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("START_DATE", (startDate.getValue() + "").substring(0, 10)
				.replace("-", "") + "000000");
		parm.setData("END_DATE", (endDate.getValue() + "").substring(0, 10)
				.replace("-", "") + "235959");
		this.billPageList = new ArrayList<INDBillPageControl.BillPageBean>();
		// �õ�����parm
		TParm dispenseINParm = INDBillPageTool.getInstance().selectDispenseIN(
				parm);
		this.billPageList.addAll(getBillPageBeanList(dispenseINParm));
		// �õ������parm
		// sql selectDispenseOUT APP_ORG_CODE -> TO_ORG_CODE
		TParm dispenseOUTParm = INDBillPageTool.getInstance()
				.selectDispenseOUT(parm);
		this.billPageList.addAll(getBillPageBeanList(dispenseOUTParm));
		// �õ����յ�parm
		TParm verifyinParm = INDBillPageTool.getInstance().selectVerifyin(parm);
		this.billPageList.addAll(getBillPageBeanList(verifyinParm));
		// �õ��˻���Parm
		TParm regressParm = INDBillPageTool.getInstance().selectRegress(parm);
		this.billPageList.addAll(getBillPageBeanList(regressParm));
		// fux modify 20151230
		// �õ��˻���Parm
		TParm qtyCheckParm = INDBillPageTool.getInstance().selectQtyCheck(parm);
		this.billPageList.addAll(getBillPageBeanList(qtyCheckParm));
		// fux modify 20150305
		// �õ��ݹ���Parm
		// fux modify 20150827 Ӧ Ҫ�� ҩ��ȥ���ݹ�
		// TParm OddParm=INDBillPageTool.getInstance().selectOdd(parm);
		// this.billPageList.addAll(getBillPageBeanList(OddParm));
		// ����ʱ������biiPageList

		Collections.sort(billPageList, new Comparator<BillPageBean>() {
			public int compare(BillPageBean o1, BillPageBean o2) {
				if (o1 == null && o2 == null)
					return 0;
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getBillDate().compareTo(o2.getBillDate());
			}
		});

		// Collections.sort(billPageList,new Comparator<BillPageBean>(){
		// public int compare(BillPageBean o1, BillPageBean o2) {
		// // long time1 = o1.getBillDate().getTime();
		// // long time2 = o2.getBillDate().getTime();
		// // long diff= time1-time2;
		// // int diffint = (int) diff;
		// // return diffint;
		// }
		// });
		// �����ۼƺ��ܼ� begin
		List<BillPageBean> newBillPageBeanList = new ArrayList<BillPageBean>();
		BillPageBean eachMonth = new BillPageBean();
		// eachMonth.setDesc("�ϼ�");
		BillPageBean totalBean = new BillPageBean();
		// totalBean.setDesc("�ۼ�");

		Date lastDate = null;
		// System.out.println("size:"+billPageList.size());
		// fux modify �ϸ���26�������25
		// 20150617 wangjc add start
		boolean empty = false;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate_1 = null;
		Date endDate_1 = null;
		try {
			startDate_1 = format.parse(this.getValueString("START_DATE")
					.substring(0, 19).replace("/", "-"));
			endDate_1 = format.parse(this.getValueString("END_DATE")
					.substring(0, 19).replace("/", "-"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Timestamp ts1 = new Timestamp(startDate_1.getTime());
		Timestamp ts2 = new Timestamp(endDate_1.getTime());
		startDate_1 = ts1;
		endDate_1 = ts2;
		String startYear = this.getValueString("START_DATE").substring(0, 4);
		String endYear = this.getValueString("END_DATE").substring(0, 4);
		Integer startYearInt = Integer.parseInt(startYear);
		Integer endYearInt = Integer.parseInt(endYear);

		// lastDate = startDate_1; //lastDate
		// System.out.println(startYearInt+"-----"+endYearInt);
		int count = 0;
		// if(startYearInt == endYearInt){
		count = endDate_1.getMonth() - startDate_1.getMonth();
		// }else{
		// count = 11-endDate_1.getMonth()+startDate_1.getMonth();
		// }
		List<BillPageBean> billPageListTmp = new ArrayList<INDBillPageControl.BillPageBean>();
		if (billPageList.size() <= 0) {
			if (startDate_1.getDay() <= 25) {
				count++;
			}
			for (int k = 0; k < count - 1; k++) {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				c.set(Calendar.YEAR, startYearInt);
				if (startDate_1.getDay() <= 25) {
					c.set(Calendar.MONTH, startDate_1.getMonth() + k);
				} else {
					c.set(Calendar.MONTH, startDate_1.getMonth() + k + 1);
				}
				c.set(Calendar.DATE, 25);
				String a = sdf.format(c.getTime());
				Date b = sdf.parse(a);
				Timestamp ts = new Timestamp(b.getTime());
				lastDate = ts;

				BillPageBean tmpEachMonth = new BillPageBean();
				// fux modify 20151224
				Date endDate = this.formateDate.parse((lastDate + "")
						.substring(0, 8).replace("-", "/").trim()
						+ "25");
				// Date endDate =
				// this.formateDate.parse((billPage.getBillDate()+"").substring(0,4).replace("-",
				// "/").trim()+((billPage.getBillDate().getMonth())+"")+"25");
				Calendar endMonthCd2 = Calendar.getInstance();
				endMonthCd2.setTime(endDate);
				tmpEachMonth.setBillDate(new Timestamp(endMonthCd2
						.getTimeInMillis()));
				tmpEachMonth.setDesc("���ºϼ�");
				tmpEachMonth.setInNum(eachMonth.getInNum());
				tmpEachMonth.setOutNum(eachMonth.getOutNum());

				tmpEachMonth.setInAmt(eachMonth.getInAmt());
				tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
				BillPageBean tmpTotalBean = new BillPageBean();
				tmpTotalBean.setBillDate(new Timestamp(endMonthCd2
						.getTimeInMillis()));

				// ������Ϊ���ڽ�� + �ۼ��� -�ۼƳ�
				tmpTotalBean.setDesc("�ۼ�");
				tmpTotalBean.setInNum(totalBean.getInNum());
				tmpTotalBean.setOutNum(totalBean.getOutNum());
				tmpTotalBean.setInAmt(totalBean.getInAmt());
				tmpTotalBean.setOutAmt(totalBean.getOutAmt());
				newBillPageBeanList.add(tmpEachMonth);
				newBillPageBeanList.add(tmpTotalBean);
				eachMonth.setInNum(0);
				eachMonth.setOutNum(0);
				// Date endDate =
				// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
				// "/"));
				// fux need modify
				// 1A0701002
				// +((billPage.getBillDate().getMonth())+"")

				BillPageBean nowcurrentMonth = new BillPageBean();
				nowcurrentMonth.setBillDate(new Timestamp(endMonthCd2
						.getTimeInMillis()));
				// Calendar endMonthCd3=Calendar.getInstance();
				// endMonthCd3.setTime(this.formateDate.parse((lastDateBefore+"").substring(0,8).replace("-",
				// "/").trim()+"25"));//wangjc
				TParm selectParmEvery = new TParm();
				int defaultNumEvery = 0;
				selectParmEvery
						.setData("ORDER_CODE", this.orderCode.getValue());
				selectParmEvery.setData("ORG_CODE", this.orgCombo.getValue());
				selectParmEvery.setData("TRANDATE",
						this.formateDate.format(endMonthCd2.getTime()));
				TParm selectStockQty = INDBillPageTool.getInstance()
						.selectStockQty(selectParmEvery);
				// System.out.println("selectStockQty2222222:"+selectStockQty);
				nowcurrentMonth.setDesc("���½��");
				// alert by wukai start
				if (null != selectStockQty
						&& selectStockQty.getCount("STOCK_QTY") > 0) {
					for (int x = 0; x < selectStockQty.getCount("STOCK_QTY"); x++) {
						nowcurrentMonth = new BillPageBean();
						if (x == 0) {
							nowcurrentMonth.setDesc("���½��");
						}
						nowcurrentMonth.setBillDate(new Timestamp(endMonthCd2
								.getTimeInMillis()));
						nowcurrentMonth.setLastNum(selectStockQty.getInt(
								"STOCK_QTY", x));
						nowcurrentMonth.setLastPrice(selectStockQty.getDouble(
								"STOCK_PRICE", x));
						nowcurrentMonth.setLastAmt(StringTool.round(
								selectStockQty.getDouble("STOCK_AMT", x), 7));
						newBillPageBeanList.add(nowcurrentMonth);
					}
				} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
					nowcurrentMonth.setLastNum(defaultNumEvery);
					nowcurrentMonth.setLastPrice(defaultNumEvery);
					nowcurrentMonth.setLastAmt(defaultNumEvery);
					newBillPageBeanList.add(nowcurrentMonth);
				}
				// alert by wukai end
			}
		}
		int ttType = -1; // add by wukai on 20161207
		int lastType = 0; // �·�ѭ����һ�ε�����
		Timestamp lastDate1 = null;
		Timestamp lastDate2 = null;
		for (int n = 0; n < count; n++) {
			// 20150617 wangjc add end

			for (BillPageBean billPage : billPageList) {
				// 20150617 wangjc add start
				boolean abc = false;

				if (billPageListTmp.size() != billPageList.size()) {
					for (BillPageBean tmp : billPageListTmp) {
						if (tmp == billPage) {
							abc = true;
							break;
						}
					}
				}
				if (abc) {
					continue;
				}
				// if(lastDate == null){
				// lastDate = startDate;
				// System.out.println("startDate_1:"+startDate_1);
				// System.out.println("-----------"+billPage.getBillDate());
				// System.out.println("Month------"+billPage.getBillDate().getMonth());
				// System.out.println("month:"+startDate_1.getMonth());
				// System.out.println("n:"+n);
				if (startDate_1.getMonth() + n == billPage.getBillDate()
						.getMonth() && billPage.getBillDate().getDate() <= 25) {
					// lastDate = billPage.getBillDate();
					lastDate2 = billPage.getBillDate();
					empty = false;
					ttType = 0;
					System.out.println("*****" + 0);
				} else if (startDate_1.getMonth() + n == billPage.getBillDate()
						.getMonth() && billPage.getBillDate().getDate() > 25) {
					lastDate = billPage.getBillDate();
					System.out.println("***** 1 billDate ::: " + lastDate);
					System.out.println("*****" + 1);
					ttType = 1;
					empty = false;
				} else if (startDate_1.getMonth() + n != billPage.getBillDate()
						.getMonth()
						&& ((startDate_1.getMonth() + n
								- billPage.getBillDate().getMonth() == -1) || (startDate_1
								.getMonth()
								+ n
								- billPage.getBillDate().getMonth() == 11))
						&& billPage.getBillDate().getDate() <= 25) {
					// lastDate=billPage.getBillDate(); //alert by wukai on
					// 20161117
					lastDate1 = billPage.getBillDate();
					// System.out.println("***** 1 billDate ::: " + lastDate1);
					ttType = 2;
					System.out.println("*****" + 2);
					empty = false;
				} else if (billPageListTmp.size() == billPageList.size()) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					c.set(Calendar.YEAR, startYearInt);
					if (ttType == 1) {
						c.set(Calendar.MONTH, startDate_1.getMonth() + n + 1);
					} else {
						c.set(Calendar.MONTH, startDate_1.getMonth() + n);
					}

					c.set(Calendar.DATE, 25);
					String a = sdf.format(c.getTime());
					Date b = sdf.parse(a);
					Timestamp ts = new Timestamp(b.getTime());
					lastDate = ts;
					System.out.println("*****" + 3);
					// lastDateBefore = billPage.getBillDate();
					empty = true;
				} else if (startDate_1.getMonth() + n != billPage.getBillDate()
						.getMonth()
						&& ((startDate_1.getMonth() + n
								- billPage.getBillDate().getMonth() == -2) || (startDate_1
								.getMonth()
								+ n
								- billPage.getBillDate().getMonth() == 10))) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					c.set(Calendar.YEAR, startYearInt);
					// c.set(Calendar.MONTH, startDate_1.getMonth() + n + 1);
					System.out.println("**** ttType " + ttType
							+ " lastDate1 :::" + lastDate1 + "billPage ::"
							+ billPage.getBillDate());
					if (ttType == 1) {
						// c.set(Calendar.MONTH, startDate_1.getMonth() + n +
						// 1);
						if (lastDate1 != null) {
							if (lastDate1.getDate() <= 25)
								c.set(Calendar.MONTH, lastDate1.getMonth());
							else
								c.set(Calendar.MONTH, lastDate1.getMonth() + 1);
						}

					} else if (ttType == 2) {
						c.set(Calendar.MONTH, lastDate1.getMonth());
					} else {
						c.set(Calendar.MONTH, startDate_1.getMonth() + n + 1);
					}
					// c.set(Calendar.MONTH, startDate_1.getMonth() + n);
					// //alert by wukai on 20161207
					c.set(Calendar.DATE, 25);
					String a = sdf.format(c.getTime());
					Date b = sdf.parse(a);
					Timestamp ts = new Timestamp(b.getTime());
					lastDate = ts;
					ttType = 4;
					System.out.println("*****" + 4);
					// lastDateBefore = billPage.getBillDate();
					empty = true;
				} else if (startDate_1.getMonth() + n != billPage.getBillDate()
						.getMonth()
						&& ((startDate_1.getMonth() + n
								- billPage.getBillDate().getMonth() == -1) || (startDate_1
								.getMonth()
								+ n
								- billPage.getBillDate().getMonth() == 11))
						&& billPage.getBillDate().getDate() > 25) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					c.set(Calendar.YEAR, startYearInt);
					c.set(Calendar.MONTH, startDate_1.getMonth() + n + 1);
					c.set(Calendar.DATE, 25);
					String a = sdf.format(c.getTime());
					Date b = sdf.parse(a);
					Timestamp ts = new Timestamp(b.getTime());
					lastDate = ts;
					empty = true;
					ttType = 5;
					System.out.println("*****" + 5);
				} else if (startDate_1.getMonth() + n != billPage.getBillDate()
						.getMonth()
						&& (startDate_1.getMonth() + n
								- billPage.getBillDate().getMonth() < -2)) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					c.set(Calendar.YEAR, startYearInt);
					c.set(Calendar.MONTH, startDate_1.getMonth() + n + 1);
					c.set(Calendar.DATE, 25);
					String a = sdf.format(c.getTime());
					Date b = sdf.parse(a);
					Timestamp ts = new Timestamp(b.getTime());
					lastDate = ts;
					empty = true;
					ttType = 6;

					System.out.println("*****" + 6);

				} else {
					System.out.println("*****" + 7);
					// ttType = 6;
					continue;
				}

				if (empty) {
					lastType = ttType;
					BillPageBean tmpEachMonth = new BillPageBean();
					// fux modify 20151224
					Date endDate = this.formateDate.parse((lastDate + "")
							.substring(0, 8).replace("-", "/").trim()
							+ "25");
					// Date endDate =
					// this.formateDate.parse((billPage.getBillDate()+"").substring(0,4).replace("-",
					// "/").trim()+((billPage.getBillDate().getMonth())+"")+"25");
					Calendar endMonthCd2 = Calendar.getInstance();
					endMonthCd2.setTime(endDate);
					tmpEachMonth.setBillDate(new Timestamp(endMonthCd2
							.getTimeInMillis()));
					tmpEachMonth.setDesc("���ºϼ�");
					tmpEachMonth.setInNum(eachMonth.getInNum());
					tmpEachMonth.setOutNum(eachMonth.getOutNum());

					tmpEachMonth.setInAmt(eachMonth.getInAmt());
					tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
					BillPageBean tmpTotalBean = new BillPageBean();
					tmpTotalBean.setBillDate(new Timestamp(endMonthCd2
							.getTimeInMillis()));
					tmpTotalBean.setDesc("�ۼ�");
					tmpTotalBean.setInNum(totalBean.getInNum());
					tmpTotalBean.setOutNum(totalBean.getOutNum());
					tmpTotalBean.setInAmt(totalBean.getInAmt());
					tmpTotalBean.setOutAmt(totalBean.getOutAmt());
					newBillPageBeanList.add(tmpEachMonth);
					newBillPageBeanList.add(tmpTotalBean);
					eachMonth.setInNum(0);
					eachMonth.setOutNum(0);
					// Date endDate =
					// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
					// "/"));
					// fux need modify
					// 1A0701002
					// +((billPage.getBillDate().getMonth())+"")

					BillPageBean nowcurrentMonth = new BillPageBean();
					nowcurrentMonth.setBillDate(new Timestamp(endMonthCd2
							.getTimeInMillis()));
					// Calendar endMonthCd3=Calendar.getInstance();
					// endMonthCd3.setTime(this.formateDate.parse((lastDateBefore+"").substring(0,8).replace("-",
					// "/").trim()+"25"));//wangjc
					TParm selectParmEvery = new TParm();
					int defaultNumEvery = 0;
					selectParmEvery.setData("ORDER_CODE",
							this.orderCode.getValue());
					selectParmEvery.setData("ORG_CODE",
							this.orgCombo.getValue());
					selectParmEvery.setData("TRANDATE",
							this.formateDate.format(endMonthCd2.getTime()));
					TParm selectStockQty = INDBillPageTool.getInstance()
							.selectStockQty(selectParmEvery);
					// System.out.println("selectStockQty2222222:"+selectStockQty);
					nowcurrentMonth.setDesc("���½��");
					// alert by wukai start
					if (null != selectStockQty
							&& selectStockQty.getCount("STOCK_QTY") > 0) {
						for (int x = 0; x < selectStockQty
								.getCount("STOCK_QTY"); x++) {
							nowcurrentMonth = new BillPageBean();
							if (x == 0) {
								nowcurrentMonth.setDesc("���½��");
							}
							nowcurrentMonth.setBillDate(new Timestamp(
									endMonthCd2.getTimeInMillis()));
							nowcurrentMonth.setLastNum(selectStockQty.getInt(
									"STOCK_QTY", x));
							nowcurrentMonth.setLastPrice(selectStockQty
									.getDouble("STOCK_PRICE", x));
							nowcurrentMonth
									.setLastAmt(StringTool.round(selectStockQty
											.getDouble("STOCK_AMT", x), 7));
							newBillPageBeanList.add(nowcurrentMonth);
						}

					} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
						nowcurrentMonth.setLastNum(defaultNumEvery);
						nowcurrentMonth.setLastPrice(defaultNumEvery);
						nowcurrentMonth.setLastAmt(defaultNumEvery);
						newBillPageBeanList.add(nowcurrentMonth);
					}

					// alert by wukai end

					// add by wukai start �޸�����������������ʱ ��ͳ���µ�bug=======
					// if(ttType == 4) {
					// tmpEachMonth = new BillPageBean();
					// //wukai modify 20161207
					// endDate = this.formateDate.parse((lastDate +
					// "").substring(0,8).replace("-", "/").trim()+"25");
					// //Date endDate =
					// this.formateDate.parse((billPage.getBillDate()+"").substring(0,4).replace("-",
					// "/").trim()+((billPage.getBillDate().getMonth())+"")+"25");
					// endMonthCd2 = Calendar.getInstance();
					// endMonthCd2.setTime(endDate);
					// endMonthCd2.add(Calendar.MONTH, 1);
					// tmpEachMonth.setBillDate(new
					// Timestamp(endMonthCd2.getTimeInMillis()));
					// tmpEachMonth.setDesc("���ºϼ�");
					// tmpEachMonth.setInNum(eachMonth.getInNum());
					// tmpEachMonth.setOutNum(eachMonth.getOutNum());
					//
					// tmpEachMonth.setInAmt(eachMonth.getInAmt());
					// tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
					//
					// tmpTotalBean = new BillPageBean();
					//
					// tmpTotalBean.setBillDate(new
					// Timestamp(endMonthCd2.getTimeInMillis()));
					// tmpTotalBean.setDesc("�ۼ�");
					// tmpTotalBean.setInNum(totalBean.getInNum());
					// tmpTotalBean.setOutNum(totalBean.getOutNum());
					// tmpTotalBean.setInAmt(totalBean.getInAmt());
					// tmpTotalBean.setOutAmt(totalBean.getOutAmt());
					// newBillPageBeanList.add(tmpEachMonth);
					// newBillPageBeanList.add(tmpTotalBean);
					// eachMonth.setInNum(0);
					// eachMonth.setOutNum(0);
					// //Date endDate =
					// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
					// "/"));
					// //fux need modify
					// //1A0701002
					// //+((billPage.getBillDate().getMonth())+"")
					//
					// nowcurrentMonth = new BillPageBean();
					// nowcurrentMonth.setBillDate(new
					// Timestamp(endMonthCd2.getTimeInMillis()));
					// // Calendar endMonthCd3=Calendar.getInstance();
					// //
					// endMonthCd3.setTime(this.formateDate.parse((lastDateBefore+"").substring(0,8).replace("-",
					// "/").trim()+"25"));//wangjc
					// TParm selectParmEvery1 = new TParm();
					// //int defaultNumEvery1 = 0;
					// selectParmEvery1.setData("ORDER_CODE",this.orderCode.getValue());
					// selectParmEvery1.setData("ORG_CODE",this.orgCombo.getValue());
					// selectParmEvery1.setData("TRANDATE",this.formateDate.format(endMonthCd2.getTime()));
					// TParm selectStockQty1 =
					// INDBillPageTool.getInstance().selectStockQty(selectParmEvery);
					// //
					// System.out.println("selectStockQty2222222:"+selectStockQty);
					// nowcurrentMonth.setDesc("���½��");
					// //alert by wukai start
					// if(null != selectStockQty1 &&
					// selectStockQty1.getCount("STOCK_QTY")>0) {
					// for(int x = 0; x < selectStockQty1.getCount("STOCK_QTY");
					// x++) {
					// nowcurrentMonth = new BillPageBean();
					// if(x == 0) {
					// nowcurrentMonth.setDesc("���½��");
					// }
					// nowcurrentMonth.setBillDate(new
					// Timestamp(endMonthCd2.getTimeInMillis()));
					// nowcurrentMonth.setLastNum(selectStockQty1.getInt("STOCK_QTY",
					// x));
					// nowcurrentMonth.setLastPrice(selectStockQty1.getDouble("STOCK_PRICE",
					// x));
					// nowcurrentMonth.setLastAmt(StringTool.round(selectStockQty1.getDouble("STOCK_AMT",
					// x),7));
					// newBillPageBeanList.add(nowcurrentMonth);
					// }
					// }else{//by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
					// nowcurrentMonth.setLastNum(defaultNumEvery);
					// nowcurrentMonth.setLastPrice(defaultNumEvery);
					// nowcurrentMonth.setLastAmt(defaultNumEvery);
					// newBillPageBeanList.add(nowcurrentMonth);
					// }
					// ttType = 0;
					// }
					// add by wukai end �޸�����������������ʱ ��ͳ���µ�bug=======
					// lastDate = lastDateBefore;//wangjc
					empty = false;
					eachMonth.setInAmt(0);
					eachMonth.setOutAmt(0);

					eachMonth.setInNum(0);
					eachMonth.setOutNum(0);
					break;
				}
				// 20150617 wangjc add end

				if (lastDate2 != null
						&& ((lastDate2.getMonth() == billPage.getBillDate()
								.getMonth()
								&& billPage.getBillDate().getDate() >= 26 && lastDate2
								.getDate() < 26) || (lastDate2.getMonth()
								- billPage.getBillDate().getMonth() == -1
								&& billPage.getBillDate().getDate() < 26 && lastDate2
								.getDate() < 26))) {
					BillPageBean tmpEachMonth = new BillPageBean();
					// fux modify 20151224
					Date endDate = this.formateDate.parse(((lastDate2 + "")
							.substring(0, 8).replace("-", "/").trim() + "25"));
					Calendar endMonthCd = Calendar.getInstance();
					endMonthCd.setTime(endDate);
					tmpEachMonth.setBillDate(new Timestamp(endMonthCd
							.getTimeInMillis()));
					tmpEachMonth.setDesc("���ºϼ�");
					tmpEachMonth.setInNum(eachMonth.getInNum());
					tmpEachMonth.setOutNum(eachMonth.getOutNum());
					tmpEachMonth.setInAmt(eachMonth.getInAmt());
					tmpEachMonth.setOutAmt(eachMonth.getOutAmt());

					BillPageBean tmpTotalBean = new BillPageBean();
					tmpTotalBean.setBillDate(new Timestamp(endMonthCd
							.getTimeInMillis()));
					tmpTotalBean.setDesc("�ۼ�");
					tmpTotalBean.setInNum(totalBean.getInNum());
					tmpTotalBean.setOutNum(totalBean.getOutNum());
					tmpTotalBean.setInAmt(totalBean.getInAmt());
					tmpTotalBean.setOutAmt(totalBean.getOutAmt());

					newBillPageBeanList.add(tmpEachMonth);
					newBillPageBeanList.add(tmpTotalBean);
					eachMonth.setInNum(0);
					eachMonth.setOutNum(0);

					BillPageBean nowcurrentMonth = new BillPageBean();
					// Date endDate =
					// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
					// "/"));

					nowcurrentMonth.setBillDate(new Timestamp(endMonthCd
							.getTimeInMillis()));
					TParm selectParmEvery = new TParm();
					int defaultNumEvery = 0;
					selectParmEvery.setData("ORDER_CODE",
							this.orderCode.getValue());
					selectParmEvery.setData("ORG_CODE",
							this.orgCombo.getValue());

					selectParmEvery.setData("TRANDATE",
							this.formateDate.format(endMonthCd.getTime()));
					TParm selectStockQty = INDBillPageTool.getInstance()
							.selectStockQty(selectParmEvery);
					nowcurrentMonth.setDesc("���½��");
					// System.out.println("endMonthCd11111:"+this.formateDate.format(endMonthCd.getTime()));
					// System.out.println("selectStockQty1111111:"+selectStockQty);

					if (null != selectStockQty
							&& selectStockQty.getCount("STOCK_QTY") > 0) {
						for (int x = 0; x < selectStockQty
								.getCount("STOCK_QTY"); x++) {
							nowcurrentMonth = new BillPageBean();
							if (x == 0) {
								nowcurrentMonth.setDesc("���½��");
							}
							nowcurrentMonth.setBillDate(new Timestamp(
									endMonthCd.getTimeInMillis()));
							nowcurrentMonth.setLastNum(selectStockQty.getInt(
									"STOCK_QTY", x));
							nowcurrentMonth.setLastPrice(selectStockQty
									.getDouble("STOCK_PRICE", x));
							nowcurrentMonth
									.setLastAmt(StringTool.round(selectStockQty
											.getDouble("STOCK_AMT", x), 7));
							newBillPageBeanList.add(nowcurrentMonth);
						}
					} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
						nowcurrentMonth.setLastNum(defaultNumEvery);
						nowcurrentMonth.setLastPrice(defaultNumEvery);
						nowcurrentMonth.setLastAmt(defaultNumEvery);
						newBillPageBeanList.add(nowcurrentMonth);
					}
					lastDate2 = null;
					eachMonth.setInAmt(0);
					eachMonth.setOutAmt(0);

					eachMonth.setInNum(0);
					eachMonth.setOutNum(0);
					// continue;
					// ��պϼ�(ÿ�µ�)

					// nowcurrentMonth.setDesc("");
					// nowcurrentMonth.setLastNum(0);
					// nowcurrentMonth.setLastPrice(0);
					// nowcurrentMonth.setLastAmt(0);
					// nowcurrentMonth.setInAmt(0);
					// nowcurrentMonth.setOutAmt(0);
				}

				// ͬ�·ݱȽ� right
				if (lastDate != null
						&& lastDate.getMonth() == billPage.getBillDate()
								.getMonth()
						&& billPage.getBillDate().getDate() >= 26
						&& lastDate.getDate() < 26) {

					BillPageBean tmpEachMonth = new BillPageBean();
					// fux modify 20151224
					Date endDate = this.formateDate.parse(((billPage
							.getBillDate() + "").substring(0, 8)
							.replace("-", "/").trim() + "25"));
					Calendar endMonthCd = Calendar.getInstance();
					endMonthCd.setTime(endDate);
					tmpEachMonth.setBillDate(new Timestamp(endMonthCd
							.getTimeInMillis()));
					tmpEachMonth.setDesc("���ºϼ�");
					tmpEachMonth.setInNum(eachMonth.getInNum());
					tmpEachMonth.setOutNum(eachMonth.getOutNum());
					tmpEachMonth.setInAmt(eachMonth.getInAmt());
					tmpEachMonth.setOutAmt(eachMonth.getOutAmt());

					BillPageBean tmpTotalBean = new BillPageBean();
					tmpTotalBean.setBillDate(new Timestamp(endMonthCd
							.getTimeInMillis()));
					tmpTotalBean.setDesc("�ۼ�");
					tmpTotalBean.setInNum(totalBean.getInNum());
					tmpTotalBean.setOutNum(totalBean.getOutNum());
					tmpTotalBean.setInAmt(totalBean.getInAmt());
					tmpTotalBean.setOutAmt(totalBean.getOutAmt());

					newBillPageBeanList.add(tmpEachMonth);
					newBillPageBeanList.add(tmpTotalBean);
					eachMonth.setInNum(0);
					eachMonth.setOutNum(0);

					BillPageBean nowcurrentMonth = new BillPageBean();
					// Date endDate =
					// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
					// "/"));

					nowcurrentMonth.setBillDate(new Timestamp(endMonthCd
							.getTimeInMillis()));
					TParm selectParmEvery = new TParm();
					int defaultNumEvery = 0;
					selectParmEvery.setData("ORDER_CODE",
							this.orderCode.getValue());
					selectParmEvery.setData("ORG_CODE",
							this.orgCombo.getValue());

					selectParmEvery.setData("TRANDATE",
							this.formateDate.format(endMonthCd.getTime()));
					TParm selectStockQty = INDBillPageTool.getInstance()
							.selectStockQty(selectParmEvery);
					nowcurrentMonth.setDesc("���½��");
					// System.out.println("endMonthCd11111:"+this.formateDate.format(endMonthCd.getTime()));
					// System.out.println("selectStockQty1111111:"+selectStockQty);

					if (null != selectStockQty
							&& selectStockQty.getCount("STOCK_QTY") > 0) {
						for (int x = 0; x < selectStockQty
								.getCount("STOCK_QTY"); x++) {
							nowcurrentMonth = new BillPageBean();
							if (x == 0) {
								nowcurrentMonth.setDesc("���½��");
							}
							nowcurrentMonth.setBillDate(new Timestamp(
									endMonthCd.getTimeInMillis()));
							nowcurrentMonth.setLastNum(selectStockQty.getInt(
									"STOCK_QTY", x));
							nowcurrentMonth.setLastPrice(selectStockQty
									.getDouble("STOCK_PRICE", x));
							nowcurrentMonth
									.setLastAmt(StringTool.round(selectStockQty
											.getDouble("STOCK_AMT", x), 7));
							newBillPageBeanList.add(nowcurrentMonth);
						}
					} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
						nowcurrentMonth.setLastNum(defaultNumEvery);
						nowcurrentMonth.setLastPrice(defaultNumEvery);
						nowcurrentMonth.setLastAmt(defaultNumEvery);
						newBillPageBeanList.add(nowcurrentMonth);
					}

					// ��պϼ�(ÿ�µ�)

					// nowcurrentMonth.setDesc("");
					// nowcurrentMonth.setLastNum(0);
					// nowcurrentMonth.setLastPrice(0);
					// nowcurrentMonth.setLastAmt(0);
					// nowcurrentMonth.setInAmt(0);
					// nowcurrentMonth.setOutAmt(0);
				} else if (lastDate != null
						&& lastDate.getMonth() != billPage.getBillDate()
								.getMonth()) {
					// ��ͬ�·ݵ���1���»���11���� right
					if (((billPage.getBillDate().getMonth() - lastDate
							.getMonth()) == 1 || (billPage.getBillDate()
							.getMonth() - lastDate.getMonth()) == -11)
							&& lastDate.getDate() <= 25
							&& billPage.getBillDate().getDate() <= 25
							&& lastType != 4 // add by wukai
					) {
						System.out.println("*****lastType ::: " + lastType);
						BillPageBean tmpEachMonth = new BillPageBean();

						Date endDate = this.formateDate.parse((lastDate + "")
								.substring(0, 8).replace("-", "/").trim()
								+ "25");
						Calendar endMonthCd1 = Calendar.getInstance();
						endMonthCd1.setTime(endDate);
						// fux modify 20151224 ����ʱ��
						tmpEachMonth.setBillDate(new Timestamp(endMonthCd1
								.getTimeInMillis()));
						tmpEachMonth.setDesc("���ºϼ�");
						tmpEachMonth.setInNum(eachMonth.getInNum());
						tmpEachMonth.setOutNum(eachMonth.getOutNum());
						tmpEachMonth.setInAmt(eachMonth.getInAmt());
						tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
						BillPageBean tmpTotalBean = new BillPageBean();
						tmpTotalBean.setBillDate(new Timestamp(endMonthCd1
								.getTimeInMillis()));
						tmpTotalBean.setDesc("�ۼ�");

						tmpTotalBean.setInNum(totalBean.getInNum());
						tmpTotalBean.setOutNum(totalBean.getOutNum());
						tmpTotalBean.setInAmt(totalBean.getInAmt());
						tmpTotalBean.setOutAmt(totalBean.getOutAmt());
						newBillPageBeanList.add(tmpEachMonth);
						newBillPageBeanList.add(tmpTotalBean);
						eachMonth.setInNum(0);
						eachMonth.setOutNum(0);

						// Date endDate =
						// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
						// "/"));

						BillPageBean nowcurrentMonth = new BillPageBean();
						nowcurrentMonth.setBillDate(new Timestamp(endMonthCd1
								.getTimeInMillis()));
						TParm selectParmEvery = new TParm();
						int defaultNumEvery = 0;
						selectParmEvery.setData("ORDER_CODE",
								this.orderCode.getValue());
						selectParmEvery.setData("ORG_CODE",
								this.orgCombo.getValue());
						selectParmEvery.setData("TRANDATE",
								this.formateDate.format(endMonthCd1.getTime()));
						TParm selectStockQty = INDBillPageTool.getInstance()
								.selectStockQty(selectParmEvery);
						// System.out.println("endMonthCd2222222:"+this.formateDate.format(endMonthCd1.getTime()));
						// System.out.println("selectStockQty2222222:"+selectStockQty);
						nowcurrentMonth.setDesc("���½��");
						if (null != selectStockQty
								&& selectStockQty.getCount("STOCK_QTY") > 0) {
							for (int x = 0; x < selectStockQty
									.getCount("STOCK_QTY"); x++) {
								nowcurrentMonth = new BillPageBean();
								if (x == 0) {
									nowcurrentMonth.setDesc("���½��");
								}
								nowcurrentMonth.setBillDate(new Timestamp(
										endMonthCd1.getTimeInMillis()));
								nowcurrentMonth.setLastNum(selectStockQty
										.getInt("STOCK_QTY", x));
								nowcurrentMonth.setLastPrice(selectStockQty
										.getDouble("STOCK_PRICE", x));
								nowcurrentMonth.setLastAmt(StringTool.round(
										selectStockQty
												.getDouble("STOCK_AMT", x), 7));
								newBillPageBeanList.add(nowcurrentMonth);
							}

						} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
							nowcurrentMonth.setLastNum(defaultNumEvery);
							nowcurrentMonth.setLastPrice(defaultNumEvery);
							nowcurrentMonth.setLastAmt(defaultNumEvery);
							newBillPageBeanList.add(nowcurrentMonth);
						}

						// nowcurrentMonth.setDesc("");
						// nowcurrentMonth.setLastNum(0);
						// nowcurrentMonth.setLastPrice(0);
						// nowcurrentMonth.setLastAmt(0);

					}
					// 20150617 wangjc modify
					// ��ͬ�·ݲ�����1���»���11���� �Ƚ�right
					// else
					// if((billPage.getBillDate().getMonth()-lastDate.getMonth())!=1&&
					// (billPage.getBillDate().getMonth()-lastDate.getMonth())!=-11
					// // &&lastDate.getDate()>=26
					// // &&billPage.getBillDate().getDate()<=25
					// ){
					// BillPageBean tmpEachMonth=new BillPageBean();
					// tmpEachMonth.setDesc("���ºϼ�");
					// tmpEachMonth.setInNum(eachMonth.getInNum());
					// tmpEachMonth.setOutNum(eachMonth.getOutNum());
					//
					// tmpEachMonth.setInAmt(eachMonth.getInAmt());
					// tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
					// BillPageBean tmpTotalBean= new BillPageBean();
					// tmpTotalBean.setDesc("�ۼ�");
					// tmpTotalBean.setInNum(totalBean.getInNum());
					// tmpTotalBean.setOutNum(totalBean.getOutNum());
					// tmpTotalBean.setInAmt(totalBean.getInAmt());
					// tmpTotalBean.setOutAmt(totalBean.getOutAmt());
					// newBillPageBeanList.add(tmpEachMonth);
					// newBillPageBeanList.add(tmpTotalBean);
					// eachMonth.setInNum(0);
					// eachMonth.setOutNum(0);
					// //Date endDate =
					// this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-",
					// "/"));
					// //fux need modify
					// //1A0701002
					// //+((billPage.getBillDate().getMonth())+"")
					// Date endDate =
					// this.formateDate.parse((lastDate+"").substring(0,8).replace("-",
					// "/").trim()+"25");
					// //Date endDate =
					// this.formateDate.parse((billPage.getBillDate()+"").substring(0,4).replace("-",
					// "/").trim()+((billPage.getBillDate().getMonth())+"")+"25");
					// Calendar endMonthCd2=Calendar.getInstance();
					// endMonthCd2.setTime(endDate);
					// BillPageBean nowcurrentMonth = new BillPageBean();
					// nowcurrentMonth.setBillDate(new
					// Timestamp(endMonthCd2.getTimeInMillis()));
					// TParm selectParmEvery = new TParm();
					// int defaultNumEvery = 0;
					// selectParmEvery.setData("ORDER_CODE",this.orderCode.getValue());
					// selectParmEvery.setData("ORG_CODE",this.orgCombo.getValue());
					// selectParmEvery.setData("TRANDATE",this.formateDate.format(endMonthCd2.getTime()));
					// TParm selectStockQty =
					// INDBillPageTool.getInstance().selectStockQty(selectParmEvery);
					// //
					// System.out.println("selectStockQty2222222:"+selectStockQty);
					// nowcurrentMonth.setDesc("���½��");
					// if(null != selectStockQty &&
					// selectStockQty.getCount("STOCK_QTY")>0){
					// nowcurrentMonth.setLastNum(selectStockQty.getInt("STOCK_QTY",0));
					// nowcurrentMonth.setLastPrice(selectStockQty.getDouble("STOCK_PRICE",0));
					// nowcurrentMonth.setLastAmt(StringTool.round(selectStockQty.getDouble("STOCK_AMT",0),7));
					// }else{//by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
					// nowcurrentMonth.setLastNum(defaultNumEvery);
					// nowcurrentMonth.setLastPrice(defaultNumEvery);
					// nowcurrentMonth.setLastAmt(defaultNumEvery);
					// }
					// newBillPageBeanList.add(nowcurrentMonth);
					// //��պϼ�(ÿ�µ�)
					// // nowcurrentMonth.setDesc("");
					// // nowcurrentMonth.setLastNum(0);
					// // nowcurrentMonth.setLastPrice(0);
					// // nowcurrentMonth.setLastAmt(0);
					// }
				}
				//
				// identify by shendr 20131231 ͳһ����С�����7λ���ų���һ��Ǯ������
				// if(a1){
				// System.out.println("aaaaaaaaaa");
				eachMonth.setInAmt(StringTool.round(eachMonth.getInAmt()
						+ billPage.getInAmt(), 7));
				eachMonth.setOutAmt(StringTool.round(eachMonth.getOutAmt()
						+ billPage.getOutAmt(), 7));

				eachMonth.setInNum(StringTool.round(eachMonth.getInNum()
						+ billPage.getInNum(), 7));
				eachMonth.setOutNum(StringTool.round(eachMonth.getOutNum()
						+ billPage.getOutNum(), 7));

				totalBean.setInNum(StringTool.round(totalBean.getInNum()
						+ billPage.getInNum(), 7));
				totalBean.setOutNum(StringTool.round(totalBean.getOutNum()
						+ billPage.getOutNum(), 7));

				totalBean.setInAmt(StringTool.round(totalBean.getInAmt()
						+ billPage.getInAmt(), 7));
				totalBean.setOutAmt(StringTool.round(totalBean.getOutAmt()
						+ billPage.getOutAmt(), 7));

				newBillPageBeanList.add(billPage);

				billPageListTmp.add(billPage);// 20150617 wangjc add
				// }
				// lastDate=billPage.getBillDate();//20150617 wangjc modify

			}
		}

		// �б�������ϼ���begin
		// fux modify 20151224 ����ʱ��
		Date endDate = this.formateDate.parse((this.endDate.getValue() + "")
				.substring(0, 10).replace("-", "/"));
		Calendar endMonthCd = Calendar.getInstance();
		endMonthCd.setTime(endDate);

		BillPageBean tmpEachMonth = new BillPageBean();
		tmpEachMonth.setBillDate(new Timestamp(endMonthCd.getTimeInMillis()));
		tmpEachMonth.setDesc("���ºϼ�");
		tmpEachMonth.setInNum(eachMonth.getInNum());
		tmpEachMonth.setOutNum(eachMonth.getOutNum());

		tmpEachMonth.setInAmt(eachMonth.getInAmt());
		tmpEachMonth.setOutAmt(eachMonth.getOutAmt());

		BillPageBean tmpTotalBean = new BillPageBean();
		tmpTotalBean.setBillDate(new Timestamp(endMonthCd.getTimeInMillis()));
		tmpTotalBean.setDesc("�ۼ�");

		tmpTotalBean.setInNum(totalBean.getInNum());
		tmpTotalBean.setOutNum(totalBean.getOutNum());

		tmpTotalBean.setInAmt(totalBean.getInAmt());
		tmpTotalBean.setOutAmt(totalBean.getOutAmt());

		newBillPageBeanList.add(tmpEachMonth);
		newBillPageBeanList.add(tmpTotalBean);
		// �б�������ϼ���end
		this.billPageList = newBillPageBeanList;
		// �����ʱ����
		newBillPageBeanList = null;
		// �����ۼƺ��ܼ� end

		// ����������
		BillPageBean lastYear = new BillPageBean();
		Date startDate = this.formateDate
				.parse((this.startDate.getValue() + "").substring(0, 10)
						.replace("-", "/"));
		Calendar lastYearCd = Calendar.getInstance();
		lastYearCd.setTime(startDate);
		lastYearCd.add(Calendar.DAY_OF_YEAR, -1);
		lastYear.setBillDate(new Timestamp(lastYearCd.getTimeInMillis()));
		lastYear.setDesc(this.LAST_AMT_DESC);
		TParm selectParm = new TParm();
		selectParm.setData("ORDER_CODE", this.orderCode.getValue());
		selectParm.setData("ORG_CODE", this.orgCombo.getValue());
		selectParm.setData("TRANDATE",
				this.formateDate.format(lastYearCd.getTime()));
		TParm selectStockQty = INDBillPageTool.getInstance().selectStockQty(
				selectParm);
		// ����Ĭ��ֵ by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
		int defaultNum = 0;
		if (null != selectStockQty && selectStockQty.getCount("STOCK_QTY") > 0) {
			lastYear.setLastNum(selectStockQty.getInt("STOCK_QTY", 0));
			lastYear.setLastPrice(selectStockQty.getDouble("STOCK_PRICE", 0));
			lastYear.setLastAmt(StringTool.round(
					selectStockQty.getDouble("STOCK_AMT", 0), 7));
		} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
			lastYear.setLastNum(defaultNum);
			lastYear.setLastPrice(defaultNum);
			lastYear.setLastAmt(defaultNum);
		}
		billPageList.add(0, lastYear);
		// ���뱾�½��
		BillPageBean currentMonth = new BillPageBean();
		currentMonth.setBillDate(new Timestamp(endMonthCd.getTimeInMillis()));
		currentMonth.setDesc(this.CURRENT_AMT_DESC);
		selectParm = new TParm();
		selectParm.setData("ORDER_CODE", this.orderCode.getValue());
		selectParm.setData("ORG_CODE", this.orgCombo.getValue());
		selectParm.setData("TRANDATE",
				this.formateDate.format(endMonthCd.getTime()));
		selectStockQty = INDBillPageTool.getInstance().selectStockQty(
				selectParm);
		// alert by wukai on 20161205 start
		if (null != selectStockQty && selectStockQty.getCount("STOCK_QTY") > 0) {
			for (int x = 0; x < selectStockQty.getCount("STOCK_QTY"); x++) {
				currentMonth = new BillPageBean();
				if (x == 0) {
					currentMonth.setDesc(this.CURRENT_AMT_DESC);
				}
				currentMonth.setBillDate(new Timestamp(endMonthCd
						.getTimeInMillis()));
				currentMonth.setLastNum(selectStockQty.getInt("STOCK_QTY", x));
				currentMonth.setLastPrice(selectStockQty.getDouble(
						"STOCK_PRICE", x));
				currentMonth.setLastAmt(StringTool.round(
						selectStockQty.getDouble("STOCK_AMT", x), 7));
				billPageList.add(currentMonth);
			}

		} else {// by liyh 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
			currentMonth.setLastNum(defaultNum);
			currentMonth.setLastPrice(defaultNum);
			currentMonth.setLastAmt(defaultNum);
			billPageList.add(currentMonth);
		}
		// alert by wukai on 20161205 end
		// billPageList.add(currentMonth);
		// this.medTable.setParmValue(selectSysFeeMed);
		// System.out.println("��ѯ����list-size��"+billPageList.size());
		// ��listת����Tparm�������ۼ���Ϣ
		TParm tableParm = getTParmFromBeanList(billPageList);
		this.billTable.setParmValue(tableParm);
	}

	/**
	 * ��TParmת��ΪList<bean>
	 * 
	 * @param parm
	 * @return
	 */
	private List<BillPageBean> getBillPageBeanList(TParm parm) {
		List<BillPageBean> listBean = new ArrayList<INDBillPageControl.BillPageBean>();
		for (int i = 0; i < parm.getCount(); i++) {
			BillPageBean billBean = new BillPageBean();
			String type = parm.getValue("TYPE_CODE", i);
			billBean.setBillDate(parm.getTimestamp("IN_DATE", i));
			billBean.setBillNo(parm.getValue("IND_NO", i));
			// fux modify 20151230 �����̵� (���Ĳ�����)
			// ����1�� �̵㽻�ױ� û�к�װ��� �ǲ���Ҫ��һ��(�̵��м��� ��װ����)
			// if("REGRESS".equals(type)||"DEP".equals(type)||"WAS".equals(type)||"THO".equals(type)||"COS".equals(type)||"ODD".equals(type)){
			if ("REGRESS".equals(type) || "DEP".equals(type)
					|| "WAS".equals(type) || "THO".equals(type)
					|| "COS".equals(type)) {
				billBean.setOutPrice(parm.getDouble("VERIFYIN_PRICE", i));
				// fux modify
				billBean.setOutNum(parm.getDouble("QTY", i));
				// billBean.setOutNumString(parm.getValue("UNIT_QTY",i).toString());
				billBean.setOutAmt(parm.getDouble("VERIFYIN_AMT", i));
				// �̵�ӯ����(���������þ���ֵ)

			}
			// if("VERIFY".equals(type)||"RET".equals(type)||"THI".equals(type)||"ODD".equals(type)){
			if ("VERIFY".equals(type) || "RET".equals(type)
					|| "THI".equals(type)) {
				billBean.setInPrice(parm.getDouble("VERIFYIN_PRICE", i));
				// fux modify
				billBean.setInNum(parm.getDouble("QTY", i));
				// billBean.setInNumString(parm.getValue("UNIT_QTY",i).toString());
				billBean.setInAmt(parm.getDouble("VERIFYIN_AMT", i));
				// �̵�����(���������þ���ֵ)
			}
			if ("PD".equals(type)) {
				if (parm.getDouble("QTY", i) > 0) {
					billBean.setInPrice(parm.getDouble("VERIFYIN_PRICE", i));
					// fux modify
					billBean.setInNum(parm.getDouble("ABS_QTY", i));
					// billBean.setInNumString(parm.getValue("UNIT_QTY",i).toString());
					billBean.setInAmt(parm.getDouble("ABS_VERIFYIN_AMT", i));
				} else if (parm.getDouble("QTY", i) < 0) {

					billBean.setOutPrice(parm.getDouble("VERIFYIN_PRICE", i));
					// fux modify
					billBean.setOutNum(parm.getDouble("ABS_QTY", i));
					// billBean.setOutNumString(parm.getValue("UNIT_QTY",i).toString());
					billBean.setOutAmt(parm.getDouble("ABS_VERIFYIN_AMT", i));
				}
			}

			billBean.setDesc(type);
			// desc
			if ("REGRESS".equals(type)) {
				billBean.setDesc("�˻�");
			}
			if ("DEP".equals(type)) {
				billBean.setDesc("����");
			}
			if ("WAS".equals(type)) {
				billBean.setDesc("���");
			}
			if ("THO".equals(type)) {
				billBean.setDesc("��������");
			}
			if ("REGRESS".equals(type)) {
				billBean.setDesc("�˻�");
			}
			if ("VERIFY".equals(type)) {
				billBean.setDesc("����");
			}
			if ("COS".equals(type)) {
				billBean.setDesc("���Ĳ�����");
			}
			if ("RET".equals(type)) {
				billBean.setDesc("�˿�");
			}
			if ("THI".equals(type)) {
				billBean.setDesc("�������");
			}
			// fux modify 20151223 ȥ���ݹ�
			// if("ODD".equals(type)){
			// billBean.setDesc("�ݹ�");
			// }
			if ("PD".equals(type)) {
				billBean.setDesc("�̵�");
			}
			listBean.add(billBean);
		}
		return listBean;
	}

	/**
	 * 
	 * ��beanת����TParm
	 * 
	 * @return
	 */
	private TParm getTParmFromBeanList(List<BillPageBean> billPageBeanList) {
		TParm tableParm = new TParm();
		double lastAmt = 0.00;
		double inAmt = 0.00;
		double outAmt = 0.00;
		for (BillPageBean billPageBean : billPageBeanList) {
			String billDate = "";
			if (billPageBean.getBillDate() != null) {
				billDate = this.formateDate.format(billPageBean.getBillDate());
			}
			tableParm.addData("BILL_DATE", billDate);
			tableParm.addData("BILL_NO", billPageBean.getBillNo());
			tableParm.addData("DESC", billPageBean.getDesc());
			tableParm
					.addData("IN_NUM", nullToEmptyStr(billPageBean.getInNum()));
			// //fux modify
			// if("���ºϼ�".equals(billPageBean.getDesc())||"�ۼ�".equals(billPageBean.getDesc())){
			// tableParm.addData("IN_NUM",
			// nullToEmptyStr(billPageBean.getInNum()));
			// //tableParm.addData("IN_NUM",
			// nullToEmptyStr(billPageBean.getInNumString()));
			// }else{
			// //tableParm.addData("IN_NUM",
			// nullToEmptyStr(billPageBean.getInNumString()));
			// tableParm.addData("IN_NUM",
			// nullToEmptyStr(billPageBean.getInNum()));
			// }
			tableParm.addData("IN_PRICE",
					nullToEmptyStr(billPageBean.getInPrice()));
			tableParm
					.addData("IN_AMT", nullToEmptyStr(billPageBean.getInAmt()));
			tableParm.addData("OUT_NUM",
					nullToEmptyStr(billPageBean.getOutNum()));
			// //fux modify
			// if("���ºϼ�".equals(billPageBean.getDesc())||"�ۼ�".equals(billPageBean.getDesc())){
			// tableParm.addData("OUT_NUM",
			// nullToEmptyStr(billPageBean.getOutNum()));
			// //tableParm.addData("OUT_NUM",
			// nullToEmptyStr(billPageBean.getOutNumString()));
			// }else{
			// tableParm.addData("OUT_NUM",
			// nullToEmptyStr(billPageBean.getOutNumString()));
			// }
			tableParm.addData("OUT_PRICE",
					nullToEmptyStr(billPageBean.getOutPrice()));
			tableParm.addData("OUT_AMT",
					nullToEmptyStr(billPageBean.getOutAmt()));
			tableParm.addData("LAST_NUM",
					nullToZero(nullToEmptyStr(billPageBean.getLastNum())));

			// tableParm.addData("LAST_PRICE",
			// nullToZero(nullToEmptyStr(billPageBean.getLastPrice())));
			// if(this.LAST_AMT_DESC.equals(billPageBean.getDesc()) ||
			// this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())){//by liyh
			// 20120823 ��Ϧ ���Ϊ��� Ĭ��ֵΪ0
			// tableParm.addData("LAST_NUM",
			// nullToZero(nullToEmptyStr(billPageBean.getLastNum())));
			// }else{
			// tableParm.addData("LAST_NUM",
			// nullToEmptyStr(billPageBean.getLastNum()));
			// }
			tableParm.addData("LAST_PRICE",
					nullToEmptyStr(billPageBean.getLastPrice()));
			if (this.LAST_AMT_DESC.equals(billPageBean.getDesc())
					|| this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())) {// by
																				// liyh
																				// 20120823
																				// ��Ϧ
																				// ���Ϊ���
																				// Ĭ��ֵΪ0
				tableParm.addData("LAST_AMT",
						nullToZero(nullToEmptyStr(billPageBean.getLastAmt())));
			} else {
				tableParm.addData("LAST_AMT",
						nullToEmptyStr(billPageBean.getLastAmt()));
			}
			// ���ڽ��ֵ��ȡ Ȼ��ŵ� ���ڽ������
			if (this.LAST_AMT_DESC.equals(billPageBean.getDesc())) {
				lastAmt = billPageBean.getLastAmt();
			}
			if ("�ۼ�".equals(billPageBean.getDesc())) {
				inAmt = billPageBean.getInAmt();
				outAmt = billPageBean.getOutAmt();
			}

			if (this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())) {
				// messageBox("lastAmt:"+lastAmt);
				// messageBox("inAmt:"+inAmt);
				// messageBox("outAmt:"+outAmt);
				tableParm.addData("CHECK_QTY", nullToEmptyStr(lastAmt + inAmt
						- outAmt));
			} else {
				tableParm.addData("CHECK_QTY", 0.00);
			}

		}
		return tableParm;
	}

	private String nullToEmptyStr(double num) {
		if (("" + num).equals("0.0")) {
			return "";
		} else {
			return num + "";
		}
	}

	// fux modify
	private String nullToEmptyStr(String num) {
		if (num == null) {
			return "0";
		} else {
			return num + "";
		}
	}

	private String nullToZero(String num) {
		if (null == num || "".equals(num)) {
			return "0";
		} else {
			return num;
		}
	}

	/**
	 * ��ʼ��ҩƷ�б� ��������
	 * 
	 * @param orgCode
	 * @param regionCode
	 */
	private void initMedTable(String orgCode, String regionCode) {
		TParm parm = new TParm();
		parm.setData("ORG_CODE", orgCode);
		parm.setData("REGION_CODE", regionCode);
		TParm selectSysFeeMed = INDBillPageTool.getInstance().selectSysFeeMed(
				parm);
		this.medTable.setParmValue(selectSysFeeMed);
	}

	/**
	 * 
	 * ��ʼ��ҳ��ؼ����ڳ������
	 */
	private void initComponent() {
		orgCombo = (TComboBox) this.getComponent("ORG_COMB0");
		startDate = (TTextFormat) this.getComponent("START_DATE");
		endDate = (TTextFormat) this.getComponent("END_DATE");
		this.medTable = (TTable) this.getComponent("MED_TABLE");
		this.orderCode = (TTextField) this.getComponent("ORDER_CODE");
		this.orderDesc = (TTextField) this.getComponent("ORDER_DESC");
		this.orderDescription = (TTextField) this.getComponent("DESCRIPTION");
		this.orderUnitDesc = (TTextField) this.getComponent("ORDER_UNIT_DESC");
		this.billTable = (TTable) this.getComponent("BIL_TABLE");
	}

	class BillPageBean {
		private Timestamp billDate;
		private String billNo;
		private String desc;
		private double inNum;
		// fux modify
		private String inNumString;

		private double inPrice;
		private double inAmt;
		private double outNum;
		// fux modify
		private String outNumString;
		private double outPrice;
		private double outAmt;
		private double lastNum;
		private double lastPrice;
		private double lastAmt;

		public Timestamp getBillDate() {
			return billDate;
		}

		public void setBillDate(Timestamp billDate) {
			this.billDate = billDate;
		}

		public String getBillNo() {
			return billNo;
		}

		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public double getInNum() {
			return inNum;
		}

		public void setInNum(double inNum) {
			this.inNum = inNum;
		}

		public String getInNumString() {
			return inNumString;
		}

		public void setInNumString(String inNumString) {
			this.inNumString = inNumString;
		}

		public double getInPrice() {
			return inPrice;
		}

		public void setInPrice(double inPrice) {
			this.inPrice = inPrice;
		}

		public double getInAmt() {
			return inAmt;
		}

		public void setInAmt(double inAmt) {
			this.inAmt = inAmt;
		}

		public double getOutNum() {
			return outNum;
		}

		public void setOutNum(double outNum) {
			this.outNum = outNum;
		}

		// fux modify
		public String getOutNumString() {
			return outNumString;
		}

		public void setOutNumString(String outNumString) {
			this.outNumString = outNumString;
		}

		public double getOutPrice() {
			return outPrice;
		}

		public void setOutPrice(double outPrice) {
			this.outPrice = outPrice;
		}

		public double getOutAmt() {
			return outAmt;
		}

		public void setOutAmt(double outAmt) {
			this.outAmt = outAmt;
		}

		public double getLastNum() {
			return lastNum;
		}

		public void setLastNum(double lastNum) {
			this.lastNum = lastNum;
		}

		public double getLastPrice() {
			return lastPrice;
		}

		public void setLastPrice(double lastPrice) {
			this.lastPrice = lastPrice;
		}

		public double getLastAmt() {
			return lastAmt;
		}

		public void setLastAmt(double lastAmt) {
			this.lastAmt = lastAmt;
		}

		@Override
		public String toString() {
			return "@@@@ [ " + this.desc + " *** " + this.billDate.toString()
					+ " ]\n";

		}
	}
	
	
	/**
	 * ��շ���
	 */
	public void onClear() {
		billTable.removeRowAll();
		String tags = "ORDER_CODE;ORDER_DESC;DESCRIPTION;ORDER_UNIT_DESC";
		clearValue(tags);

	}
}
