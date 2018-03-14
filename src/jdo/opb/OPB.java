package jdo.opb;

import com.dongyang.jdo.TJDOObject;
import jdo.sys.Pat;
import jdo.reg.Reg;
import jdo.ins.INSIbsTool;
import jdo.opd.PrescriptionList;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.OdoUtil;
import jdo.opd.OrderList;
import jdo.opd.Order;
import java.util.Map;
import jdo.bil.BilInvoice;
import jdo.bil.BilInvrcpt;
import jdo.sys.Operator;
import java.util.List;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.util.Vector;
import jdo.bil.BIL;
import com.dongyang.util.StringTool;
import jdo.pha.PhaSysParmTool;
import com.dongyang.data.TNull;

/**
 * 
 * <p>
 * Title: 门诊收费
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
 * @author lzk 2008.09.26
 * @version 1.0
 */
public class OPB extends TJDOObject {
	// 类别
	private String recpType = "OPB";
	private String admType = "O";
	/**
	 * 患者对象
	 */
	private Pat pat;
	/**
	 * 挂号对象
	 */
	private Reg reg;
	/**
	 * 医嘱处理对象
	 */
	private OdoUtil odoUtil;
	/**
	 * 票据list
	 */
	private OPBReceiptList receiptList;

	/**
	 * 处方签
	 */
	private PrescriptionList prescriptionList;
	/**
	 * 费用类别
	 */
	private Map chargeMap;
	/**
	 * 收费明细数据
	 */
	private TParm chargeParm;
	/**
	 * 票据打印管理
	 */
	private BilInvoice bilInvoice;
	/**
	 * 整理处方签
	 */
	private TParm prescription;
	/**
	 * 票据打印明细
	 */
	private BilInvrcpt bilInvrcpt;
	
	/**
	 * 医疗卡交易数据SQL
	 */
	private String[] ektSql;
	
	public String[] getEktSql() {
		return ektSql;
	}

	public void setEktSql(String[] ektSql) {
		this.ektSql = ektSql;
	}

	/**
	 * 账务方法对象
	 */
	BIL bil = new BIL();

	/**
	 * 初始化
	 */
	public OPB() {
		// 初始化处方签
		setPrescriptionList(new PrescriptionList());
	}

	/**
	 * 设置患者对象
	 * 
	 * @param pat
	 *            Pat
	 */
	public void setPat(Pat pat) {
		this.pat = pat;
	}

	/**
	 * 得到患者对象
	 * 
	 * @return Pat
	 */
	public Pat getPat() {
		return pat;
	}

	/**
	 * 设置挂号对象
	 * 
	 * @param reg
	 *            Reg
	 */
	public void setReg(Reg reg) {
		this.reg = reg;
	}

	/**
	 * 得到挂号对象
	 * 
	 * @return Reg
	 */
	public Reg getReg() {
		return reg;
	}

	/**
	 * 设置医嘱对象
	 * 
	 * @param odoUtil
	 *            OdoUtil
	 */
	public void setOdoUtil(OdoUtil odoUtil) {
		this.odoUtil = odoUtil;
	}

	/**
	 * 得到医嘱对象
	 * 
	 * @return OdoUtil
	 */
	public OdoUtil getOdoUtil() {
		return odoUtil;
	}

	/**
	 * 设置票据List
	 * 
	 * @param opbReceiptList
	 *            OPBReceiptList
	 */
	public void setReceiptList(OPBReceiptList opbReceiptList) {
		this.receiptList = opbReceiptList;
	}

	/**
	 * 得到票据List
	 * 
	 * @return OPBReceiptList
	 */
	public OPBReceiptList getReceiptList() {
		return receiptList;
	}

	/**
	 * 设置处方签
	 * 
	 * @param prescriptionList
	 *            PrescriptionList
	 */
	public void setPrescriptionList(PrescriptionList prescriptionList) {
		this.prescriptionList = prescriptionList;
	}

	/**
	 * 得到处方签
	 * 
	 * @return PrescriptionList
	 */
	public PrescriptionList getPrescriptionList() {
		return prescriptionList;
	}

	/**
	 * 设置收费明细map
	 * 
	 * @param chargeMap
	 *            Map
	 */
	public void setChargeMap(Map chargeMap) {
		this.chargeMap = chargeMap;
	}

	/**
	 * 得到收费明细map
	 * 
	 * @return Map
	 */
	public Map getChargeMap() {
		return this.chargeMap;
	}

	/**
	 * 设置收费
	 * 
	 * @param chargeParm
	 *            TParm
	 */
	public void setChargeParm(TParm chargeParm) {
		this.chargeParm = chargeParm;
	}

	/**
	 * 得到收费
	 * 
	 * @return TParm
	 */
	public TParm getChargeParm() {
		return this.chargeParm;
	}

	/**
	 * 设置票据打印管理
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 */
	public void setBilInvoice(BilInvoice bilInvoice) {
		this.bilInvoice = bilInvoice;
	}

	/**
	 * 得到票据打印管理
	 * 
	 * @return BilInvoice
	 */
	public BilInvoice getBilInvoice() {
		return this.bilInvoice;
	}

	/**
	 * 设置打印票据明细
	 * 
	 * @param bilInvrcpt
	 *            BilInvrcpt
	 */
	public void setBilInvrcpt(BilInvrcpt bilInvrcpt) {
		this.bilInvrcpt = bilInvrcpt;
	}

	/**
	 * 得到打印票据明细
	 * 
	 * @return BilInvrcpt
	 */
	public BilInvrcpt getBilinvrcpt() {
		return this.bilInvrcpt;
	}

	/**
	 * 初始化参数
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		if (!initPrescription(parm.getParm("ORDER")))
			return false;
		return true;
	}

	/**
	 * 初始化处方
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean true:成功，false:失败
	 */
	public boolean initPrescription(TParm parm) {
		if (parm == null)
			return false;
		return getPrescriptionList().initParm(parm);
	}

	/**
	 * 检测数据
	 * 
	 * @return TParm
	 */
	public TParm checkData() {
		TParm result = new TParm();
		return result;
	}

	/**
	 * 得到全部数据
	 * 
	 * @return TParm
	 */
	public TParm getParm() {
		TParm result = new TParm();
		TParm modifyOrderParm = this.getPrescriptionList().getParm();
		modifyOrderParm.getParm(OrderList.MODIFIED);
		// 加载医嘱数据
		result.setData("ORDER", this.getPrescriptionList().getParm().getData());
		// 得到票据
		TParm newOpbReceipt = receiptList.getParm(receiptList.PRIMARY);
		// 加载票据
		result.setData("RECEIPT", newOpbReceipt.getData());
		// 得到当前使用的票据
		TParm newBilInvoice = bilInvoice.getParm();
		result.setData("BILINVOICE", newBilInvoice.getData());
		// 得到打印的票据
		TParm newBilInvrcpt = bilInvrcpt.getParm();
		result.setData("BILINVRCPT", newBilInvrcpt.getData());
		return result;
	}

	/**
	 * 根据病案号查询OPB
	 * 
	 * @param mrno
	 *            String
	 * @return OPB
	 */
	public boolean onQueryByMrNo(String mrno) {
		// 初始化pat
		pat = pat.onQueryByMrNo(mrno);
		if (pat == null)
			return false;
		setPat(pat);
		return true;
	}

	/**
	 * 根据处方签号查询OPB
	 * 
	 * @param rxNo
	 *            String
	 * @return OPB
	 */
	public static OPB onQueryByRxNo(String rxNo) {
		return null;
	}

	/**
	 * 新增票据对象
	 * 
	 * @return OPBReceipt
	 */
	public OPBReceipt newReceipt() {
		OPBReceipt newReceipt = receiptList.newReceipt();
		return newReceipt;
	}

	/**
	 * 根据传入的TParm 构造票据List对象 根据传入的数据行数自动创建
	 * 
	 * @param parm
	 *            TParm
	 */
	public void InitOpbReceiptList(TParm parm) {
		if (parm.getErrCode() < 0) {
			err(parm.getErrCode() + " " + parm.getErrText());
			return;
		}
		receiptList.InitOpbReceiptList(parm);
	}

	/**
	 * 删除一张票据
	 * 
	 * @param receipt
	 *            OPBReceipt
	 */
	public void deleteReceipt(OPBReceipt receipt) {
		if (this.receiptList == null)
			return;

	}

	/**
	 * 整理收费类别
	 */
	public void initReceiptCharge() {
		// System.out.println("ssssssssssss");
		this.setChargeMap(bil.getChargeToRexpCodeMap(admType));
		// TParm parm = new TParm();
		// parm.setData("ADM_TYPE", "O");
		// TParm result = BILRecpParmTool.getInstance().selectcharge(parm);
		// if (result.getErrCode() < 0) {
		// err(result.getErrCode() + " " + result.getErrText());
		// return;
		// }
		// // System.out.println("result>>>>>>>>>>>>>>>>"+result);
		// Map map =
		// map.put(result.getValue("CHARGE01", 0), "CHARGE01");
		// map.put(result.getValue("CHARGE02", 0), "CHARGE02");
		// map.put(result.getValue("CHARGE03", 0), "CHARGE03");
		// map.put(result.getValue("CHARGE04", 0), "CHARGE04");
		// map.put(result.getValue("CHARGE05", 0), "CHARGE05");
		// map.put(result.getValue("CHARGE06", 0), "CHARGE06");
		// map.put(result.getValue("CHARGE07", 0), "CHARGE07");
		// map.put(result.getValue("CHARGE08", 0), "CHARGE08");
		// map.put(result.getValue("CHARGE09", 0), "CHARGE09");
		// map.put(result.getValue("CHARGE10", 0), "CHARGE10");
		// map.put(result.getValue("CHARGE11", 0), "CHARGE11");
		// map.put(result.getValue("CHARGE12", 0), "CHARGE12");
		// map.put(result.getValue("CHARGE13", 0), "CHARGE13");
		// map.put(result.getValue("CHARGE14", 0), "CHARGE14");
		// map.put(result.getValue("CHARGE15", 0), "CHARGE15");
		// map.put(result.getValue("CHARGE16", 0), "CHARGE16");
		// map.put(result.getValue("CHARGE17", 0), "CHARGE17");
		// map.put(result.getValue("CHARGE18", 0), "CHARGE18");
		// map.put(result.getValue("CHARGE19", 0), "CHARGE19");
		// map.put(result.getValue("CHARGE20", 0), "CHARGE20");
		// map.put(result.getValue("CHARGE21", 0), "CHARGE21");
		// map.put(result.getValue("CHARGE22", 0), "CHARGE22");
		// map.put(result.getValue("CHARGE23", 0), "CHARGE23");
		// map.put(result.getValue("CHARGE24", 0), "CHARGE24");
		// map.put(result.getValue("CHARGE25", 0), "CHARGE25");
		// map.put(result.getValue("CHARGE26", 0), "CHARGE26");
		// map.put(result.getValue("CHARGE27", 0), "CHARGE27");
		// map.put(result.getValue("CHARGE28", 0), "CHARGE28");
		// map.put(result.getValue("CHARGE29", 0), "CHARGE29");
		// map.put(result.getValue("CHARGE30", 0), "CHARGE30");
		// //放入对象
		// this.setChargeMap(map);
		return;
	}

	/**
	 * 配置收费金额
	 * 
	 * @return TParm
	 */
	public TParm chargeParm() {
		// TParm chargeParm = new TParm();
		// chargeParm.setData("CHARGE01", 0);
		// chargeParm.setData("CHARGE02", 0);
		// chargeParm.setData("CHARGE03", 0);
		// chargeParm.setData("CHARGE04", 0);
		// chargeParm.setData("CHARGE05", 0);
		// chargeParm.setData("CHARGE06", 0);
		// chargeParm.setData("CHARGE07", 0);
		// chargeParm.setData("CHARGE08", 0);
		// chargeParm.setData("CHARGE09", 0);
		// chargeParm.setData("CHARGE10", 0);
		// chargeParm.setData("CHARGE11", 0);
		// chargeParm.setData("CHARGE12", 0);
		// chargeParm.setData("CHARGE13", 0);
		// chargeParm.setData("CHARGE14", 0);
		// chargeParm.setData("CHARGE15", 0);
		// chargeParm.setData("CHARGE16", 0);
		// chargeParm.setData("CHARGE17", 0);
		// chargeParm.setData("CHARGE18", 0);
		// chargeParm.setData("CHARGE19", 0);
		// chargeParm.setData("CHARGE20", 0);
		// chargeParm.setData("CHARGE21", 0);
		// chargeParm.setData("CHARGE22", 0);
		// chargeParm.setData("CHARGE23", 0);
		// chargeParm.setData("CHARGE24", 0);
		// chargeParm.setData("CHARGE25", 0);
		// chargeParm.setData("CHARGE26", 0);
		// chargeParm.setData("CHARGE27", 0);
		// chargeParm.setData("CHARGE28", 0);
		// chargeParm.setData("CHARGE29", 0);
		// chargeParm.setData("CHARGE30", 0);
		return bil.getChargeParm();
	}

	/**
	 * 初始化当前使用的票据
	 * 
	 * @return boolean
	 */
	public boolean initBilInvoice() {
		bilInvoice = new BilInvoice().initBilInvoice("OPB");
		return true;
	}

	/**
	 * 初始化打印票据
	 * 
	 * @return boolean
	 */
	public boolean initBilInvrcpt() {
		bilInvrcpt = new BilInvrcpt();
		bilInvrcpt.setCashierCode(Operator.getID());
		bilInvrcpt.setRecpType(recpType);
		return true;
	}

	/**
	 * 查找OPB对象
	 * 
	 * @param reg
	 *            Reg
	 * @return OPB
	 */
	public static OPB onQueryByCaseNo(Reg reg) {
		if (reg == null)
			return null;
		if (reg.caseNo() == null || reg.caseNo().length() == 0)
			return null;
		OPB opb = new OPB();
		// 放入pat
		opb.setPat(reg.getPat());
		// 放入reg
		opb.setReg(reg);
		// 放入reg
		opb.onQuery(reg.caseNo());
		// 新建票据list
		OPBReceiptList receiptList = new OPBReceiptList();
		// 票据里添加opb
		receiptList.setReg(reg);
		receiptList.setPat(reg.getPat());
		// 放入票据list
		opb.setReceiptList(receiptList);
		// 初始化收费项目map
		opb.initReceiptCharge();
		// //初始化所有收费费用
		// opb.chargeParm();
		// 初始化当前使用的票据组
		opb.initBilInvoice();
		// 初始化要打印的一张票据明细
		opb.initBilInvrcpt();
		// 给处方签放入挂号对象
		opb.getPrescriptionList().setReg(reg);
		return opb;
	}

	/**
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("OPB->\n");
		sb.append("  PrescriptionList:");
		sb.append(getPrescriptionList());
		return sb.toString();
	}

	/**
	 * 查询
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean onQuery(String caseNo) {
		// 检核pat
		if (getPat() == null)
			return false;
		// 检核casno
		if (getPat().getMrNo() == null || getPat().getMrNo().length() == 0)
			return false;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", getPat().getMrNo());
		// 查询所有的order
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		// 初始化处方签
		if (!initParm(result)) {
			err("数据载入失败");
			return false;
		}
		return true;
	}

	/**
	 * 收费标记全选
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 */
	public void chargeTAll(boolean boo, String rxNo) {
		// 每次计算费用都要重新初始化收费parm
		TParm chargeParm = chargeParm();
		// 调用拿到显示order的数据
		TParm orderParm = getOrderParm(boo, rxNo, false);
		int count = orderParm.getCount();
		for (int i = 0; i < count; i++) {
			// 取一个
			Order order = (Order) orderParm.getData("OBJECT", i);
			// 如果退费标记true
			if (order.getChargeFlg()) {
				// 收费类别
				String rexpCode = order.getRexpCode();
				// 通过收费类别得到CHARGE
				String charge = chargeMap.get(rexpCode).toString();
				// 得到parm中的老金额
				double oldTotAmt = chargeParm.getDouble(charge);
				// 计算新金额
				double totAmt = oldTotAmt - order.getArAmt();
				// 存入收费金额
				chargeParm.setData(charge, totAmt);
			}
		}
		setChargeParm(chargeParm);
	}

	/**
	 * 收费标记全选
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 */
	public void chargeAll(boolean boo, String rxNo) {
		// 每次计算费用都要重新初始化收费parm
		TParm chargeParm = chargeParm();
		// 调用拿到显示order的数据
		TParm orderParm = getOrderParm(boo, rxNo, true);
		int count = orderParm.getCount();
		for (int i = 0; i < count; i++) {
			// 取一个
			Order order = (Order) orderParm.getData("OBJECT", i);
			// 如果收费标记true
			if (order.getChargeFlg()) {
				// 收费类别
				String rexpCode = order.getRexpCode();
				// 通过收费类别得到CHARGE
				String charge = chargeMap.get(rexpCode).toString();
				// 得到parm中的老金额
				double oldTotAmt = chargeParm.getDouble(charge);
				// 计算新金额
				double totAmt = order.getArAmt() + oldTotAmt;
				// 存入收费金额
				chargeParm.setData(charge, totAmt);
			}
		}
		setChargeParm(chargeParm);
	}

	/**
	 * 显示方式(未收费)
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmNotCharge(boolean boo, String rxNo, String cat1Type) {
		// 拿到全部order
		TParm parm = new TParm();
		if (cat1Type == null || cat1Type.length() == 0)
			parm = getOrderParm(boo, rxNo, true);
		else
			parm = getOrderParm(boo, rxNo, true, cat1Type);
		// 每次计算费用都要重新初始化收费parm
		TParm chargeParm = chargeParm();
		// 得到长度
		int count = parm.getCount();
		// parm删除数据行数
		int parmCount = 0;
		for (int i = count - 1; i >= 0; i--) {
			Order order = (Order) parm.getData("OBJECT", i);
			// 如果已收费
			if (cat1Type == null || cat1Type.length() == 0) {
				if (order.getBillFlg().equals("Y")) {
					// 清空隐藏行的收费标记
					order.modifyChargeFlg(false);
					// 删除
					parm.removeRow(i);
					parmCount++;
				} else {
					// 收费类别
					String rexpCode = order.getRexpCode();
					// 通过收费类别得到CHARGE
					String charge = chargeMap.get(rexpCode).toString();
					// 得到parm中的老金额
					double oldTotAmt = chargeParm.getDouble(charge);
					// 计算新金额
					double totAmt = order.getArAmt() + oldTotAmt;
					// 存入收费金额
					chargeParm.setData(charge, totAmt);
				}

			} else {
				if (order.getBillFlg().equals("Y")
						|| !cat1Type.equals(order.getCat1Type())) {
					// 清空隐藏行的收费标记
					order.modifyChargeFlg(false);
					// 删除
					parm.removeRow(i);
					parmCount++;
				} else {
					// 收费类别
					String rexpCode = order.getRexpCode();
					// 通过收费类别得到CHARGE
					String charge = chargeMap.get(rexpCode).toString();
					// 得到parm中的老金额
					double oldTotAmt = chargeParm.getDouble(charge);
					// 计算新金额
					double totAmt = order.getArAmt() + oldTotAmt;
					// 存入收费金额
					chargeParm.setData(charge, totAmt);
				}
			}
		}
		parm.setData("ACTION", "COUNT", count - parmCount);
		setChargeParm(chargeParm);
		return parm;
	}

	/**
	 * 显示方式(EKT退费)
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmEKTTCharge(boolean boo, String rxNo,
			String cat1Type) {
		// 拿到全部order
		TParm parm = new TParm();
		if (cat1Type == null || cat1Type.length() == 0)
			parm = getOrderParm(boo, rxNo, true);
		else
			parm = getOrderParm(boo, rxNo, true, cat1Type);
		// System.out.println("parm"+parm);
		// 得到长度
		int count = parm.getCount();
		// parm删除数据行数
		int parmCount = 0;
		for (int i = count - 1; i >= 0; i--) {
			Order order = (Order) parm.getData("OBJECT", i);
			if (cat1Type == null || cat1Type.length() == 0) {
				// 如果未收费收费
				if ((!order.getBillFlg().equals("Y"))
						|| (order.getBillFlg().equals("Y") && "C".equals(order
								.getBillType()))) {
					// 清空隐藏行的收费标记
					order.modifyChargeFlg(false);
					// 删除
					parm.removeRow(i);
					parmCount++;
				}

			} else {
				// 如果未收费收费
				if (((!order.getBillFlg().equals("Y")) || (order.getBillFlg()
						.equals("Y") && "C".equals(order.getBillType())))
						|| !cat1Type.equals(order.getCat1Type())) {
					// 清空隐藏行的收费标记
					order.modifyChargeFlg(false);
					// 删除
					parm.removeRow(i);
					parmCount++;
				}
			}
		}
		parm.setData("ACTION", "COUNT", count - parmCount);

		return parm;
	}

	/**
	 * 得到处方签的序列comb
	 * 
	 * @param cat1Type
	 *            String
	 * @return Vector
	 */
	public Vector getPrescriptionComb(String cat1Type) {
		// 处方签comb的数据
		Vector prescriptionComb = getPrescriptionList().getPrescriptionComb(
				cat1Type);
		return prescriptionComb;
	}

	/**
	 * 得到所有的数据给table显示
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 * @param changeFlg
	 *            boolean true 收费 false 退费
	 * @return TParm
	 */
	public TParm getOrderParm(boolean boo, String rxNo, boolean changeFlg) {
		// 得到所有的parma和order
		TParm parm = getPrescriptionList().getParmShow();
		// System.out.println("table显示数据"+parm);
		// 数据总量
		int count = parm.getCount();
		int rxNoLength = rxNo.length();
		// parm删除数据行数
		int parmCount = 0;
		for (int i = count - 1; i >= 0; i--) {
			// 拿到当前order
			Order order = (Order) parm.getData("OBJECT", i);
			// 清空隐藏行的收费标记
			order.modifyChargeFlg(false);
			// 如果order中处方签号相等
			if (rxNoLength == 0)
				continue;
			if (!order.getRxNo().equals(rxNo)) {
				parm.removeRow(i);
				parmCount++;
				continue;
			}
		}
		// 当前医嘱数量
		parm.setData("ACTION", "COUNT", count - parmCount);
		// 收费标记
		for (int i = 0; i < count - parmCount; i++) {
			// 拿到医嘱
			Order order = (Order) parm.getData("OBJECT", i);
			// 收费
			if (changeFlg) {
				if (order.getBillFlg().equals("N")) {
					// 给医嘱添加收费标记
					order.modifyChargeFlg(boo);
				}
			} else {
				if (order.getBillFlg().equals("Y")
						&& "E".equals(order.getBillType())) {
					// 给医嘱添加退费标记
					order.modifyChargeFlg(boo);
				}
			}
			// 添加标记
			parm.addData("CHARGE", order.getChargeFlg());
		}
		return parm;
	}

	public TParm getOrderParm(boolean boo, String rxNo, boolean changeFlg,
			String cat1Type) {
		// 得到所有的parma和order
		TParm parm = getPrescriptionList().getParmShow();
		// System.out.println("table显示数据"+parm);
		// 数据总量
		int count = parm.getCount();
		int rxNoLength = rxNo.length();
		// parm删除数据行数
		int parmCount = 0;
		for (int i = count - 1; i >= 0; i--) {
			// 拿到当前order
			Order order = (Order) parm.getData("OBJECT", i);
			// 清空隐藏行的收费标记
			order.modifyChargeFlg(false);
			// 如果order中处方签号相等
			if (rxNoLength == 0)
				continue;
			if (cat1Type == null || cat1Type.length() == 0) {
				if (!order.getRxNo().equals(rxNo)) {
					parm.removeRow(i);
					parmCount++;
					continue;
				}
			} else {
				if (!order.getRxNo().equals(rxNo)
						|| !cat1Type.equals(order.getCat1Type())) {
					parm.removeRow(i);
					parmCount++;
					continue;

				}
			}
		}
		// 当前医嘱数量
		parm.setData("ACTION", "COUNT", count - parmCount);
		// 收费标记
		for (int i = 0; i < count - parmCount; i++) {
			// 拿到医嘱
			Order order = (Order) parm.getData("OBJECT", i);
			// 收费
			if (changeFlg) {
				if (order.getBillFlg().equals("N")) {
					// 给医嘱添加收费标记
					order.modifyChargeFlg(boo);
				}
			} else {
				if (order.getBillFlg().equals("Y")
						&& "E".equals(order.getBillType())) {
					// 给医嘱添加退费标记
					order.modifyChargeFlg(boo);
				}
			}
			// 添加标记
			parm.addData("CHARGE", order.getChargeFlg());
		}
		return parm;
	}

	/**
	 * 得到目前选中数据的收费项目综费用
	 * 
	 * @param chargeFlg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return double
	 */
	public double getFee(boolean chargeFlg, String cat1Type) {
		// 每次计算费用都要重新初始化收费parm
		TParm chargeParm = chargeParm();
		double fee = 0.0;
		// 拿到所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 取一个order
			Order order = (Order) list.get(i);
			// 如果不收费
			if (!order.getChargeFlg())
				continue;
			// 去掉主项
			if (order.getOrderCode().equals(order.getOrdersetCode()))
				continue;
			// 医嘱类别筛选
			if (cat1Type == null || cat1Type.length() == 0) {
			} else {
				if (!cat1Type.equals(order.getCat1Type()))
					continue;
			}
			if (chargeFlg)
				fee += order.getArAmt();
			else
				fee -= order.getArAmt();
			// 收据类别
			String rexpCode = order.getRexpCode();
			// 通过收费类别得到CHARGE
			String charge = chargeMap.get(rexpCode).toString();
			// 得到parm中的老金额
			double oldTotAmt = chargeParm.getDouble(charge);
			// 计算新金额
			double totAmt = order.getArAmt() + oldTotAmt;
			// 存入收费金额
			chargeParm.setData(charge, totAmt);
		}
		setChargeParm(chargeParm);
		// System.out.println("新金额"+fee);
		return fee;
	}

	public double getFee(boolean chargeFlg) {
		// 每次计算费用都要重新初始化收费parm
		TParm chargeParm = chargeParm();
		double fee = 0.0;
		// 拿到所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 取一个order
			Order order = (Order) list.get(i);
			// 如果不收费
			if (!order.getChargeFlg())
				continue;
			// 去掉主项
			if (order.getOrderCode().equals(order.getOrdersetCode()))
				continue;
			if (chargeFlg)
				fee += order.getArAmt();
			else
				fee -= order.getArAmt();
			// 收据类别
			String rexpCode = order.getRexpCode();
			// 通过收费类别得到CHARGE
			String charge = chargeMap.get(rexpCode).toString();
			// 得到parm中的老金额
			double oldTotAmt = chargeParm.getDouble(charge);
			// 计算新金额
			double totAmt = order.getArAmt() + oldTotAmt;
			// 存入收费金额
			chargeParm.setData(charge, totAmt);
		}
		setChargeParm(chargeParm);
		// System.out.println("新金额"+fee);
		return fee;
	}

	/**
	 * 保存前改变order的属性
	 */
	public void chargeOrder() {
		Timestamp date = SystemTool.getInstance().getDate();
		// 取所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 取一条order
			Order order = (Order) list.get(i);
			// 如果没有收费标记
			if (!order.getChargeFlg())
				continue;
			// 加上收费时间
			order.modifyBillDate(date);
			// 加上收费标记
			order.modifyBillFlg("Y");
			// 加上收费人员
			order.modifyBillUser(Operator.getID());
			// 标记现金收费
			order.modifyBillType("C");
		}
	}

	/**
	 * 医疗卡保存前改变order的属性
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	public void chargeOrderEKT(TParm parm, boolean flg) {
		Timestamp date = SystemTool.getInstance().getDate();
		// 取所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		int opType = parm.getInt("OP_TYPE");
		List listRx = (List) parm.getData("RX_LIST");
		for (int i = 0; i < count; i++) {
			// 取一条order
			Order order = (Order) list.get(i);
			// 扣款
			// System.out.println("opType" + opType + " " +
			// order.getChargeFlg());
			if (flg && order.getChargeFlg()) {
				order.modifyBillFlg("N");
				order.modifyBillUser("");
				order.modifyBillType("");
				continue;
			}
			if (opType == 1 || opType == 4) {
				// 如果没有收费标记
				if (!order.getChargeFlg())
					continue;
				// 加上收费时间
				order.modifyBillDate(date);
				// 加上收费标记
				order.modifyBillFlg("Y");
				// 加上收费人员
				order.modifyBillUser(Operator.getID());
				// 标记医疗卡收费
				order.modifyBillType("E");
				continue;
			}
			// 退费
			if (opType == 2) {
				if (listRx.indexOf(order.getRxNo()) == -1)
					continue;
				if ("C".equals(order.getBillType()))
					continue;
				if ("N".equals(order.getBillFlg()))
					continue;
				order.modifyBillFlg("N");
				// 加上收费时间
				order.modifyBillDate(null);
				order.modifyBillUser("");
				order.modifyBillType("");
			}
		}
	}

	/**
	 * 现金保存前改变order的属性
	 */
	public void chargeOrderCash() {
		Timestamp date = SystemTool.getInstance().getDate();
		// 取所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 取一条order
			Order order = (Order) list.get(i);
			// 如果没有收费标记
			if (!order.getChargeFlg())
				continue;
			// 加上收费时间
			order.modifyBillDate(date);
			// 加上收费标记
			order.modifyBillFlg("Y");
			// 加上收费人员
			order.modifyBillUser(Operator.getID());
			// 标记现金收费
			order.modifyBillType("C");
		}
	}

	/**
	 * 检核药品是否已经审配发 是否可以退药
	 * 
	 * @param order
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(TParm order, int row) {
		boolean needExamineFlg = false;
		// System.out.println("校验能否退药"+order);
		// 如果是西药 审核或配药后就不可以再进行修改或者删除
		if ("W".equals(order.getValue("PHA_TYPE"))
				|| "C".equals(order.getValue("PHA_TYPE"))) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// System.out.println("");
		// 如果是中药 审核或配药后就不可以再进行修改或者删除
		if ("G".equals(order.getValue("PHA_TYPE"))) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		// if (order.getValue("PHA_CHECK_CODE", row).length() > 0
		// && order.getValue("PHA_RETN_CODE", row).length() == 0) {
		// return false;
		// }
		// 如果有审核流程 那么判断审核医师是否为空
		if (needExamineFlg) {
			// System.out.println("有审核");
			// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
			if (order.getValue("PHA_CHECK_CODE").length() > 0
					&& order.getValue("PHA_RETN_CODE").length() == 0) {
				return false;
			}
		} else {// 没有审核流程 直接配药
			// 判断是否有配药药师
			// System.out.println("无审核");
			if (order.getValue("PHA_DOSAGE_CODE").length() > 0
					&& order.getValue("PHA_RETN_CODE").length() == 0) {
				return false;// 已经配药不可以做修改
			}
		}
		return true;
	}

	/**
	 * 得到医疗卡入参数据
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getEKTParm(boolean flg, String cat1Type) {
		TParm parm = new TParm();
		// 取所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		double sum=0.00;//此次操作的未收费医嘱
		for (int i = 0; i < count; i++) {
			// 取一条order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			// if("C".equals(order.getBillType()))
			// continue;
			if (null != order.getPrintFlg() && order.getPrintFlg().equals("Y")) {
				continue;
			}
			sum+=order.getArAmt();
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("AR_AMT", order.getArAmt());
			parm.addData("EXEC_FLG", order.getExecFlg());
			parm.addData("RECEIPT_FLG", order.getReceiptFlg());
			parm.addData("OWN_PRICE", order.getOwnPrice());
			parm.addData("QTY", order.getDosageQty());
			parm.addData("BUSINESS_NO", order.getBusinessNo());
			parm.addData("CHARGE_FLG", order.getChargeFlg());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					//parm.setData("BUSINESS_TYPE", "OPBT");
					//parm.setData("TYPE_FLG", "Y");// 医疗卡收费主档EKT_TREDE state 值判断
					// STATE=3 退费
				} else {
					parm.addData("BILL_FLG", "Y");
					//parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		parm.setData("SUM",sum);
		return parm;
	}

	/**
	 * 得到医疗卡入参数据 医保分割操作数据
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm ==================pangben 2012-3-29
	 */
	public TParm getInsEKTParm(boolean flg, String cat1Type) {
		TParm parm = new TParm();
		// 取所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		TParm parmBill = new TParm();
		double billAmt = 0.00;// 未收费医嘱金额
		double sumAmt = 0.00;// 所有金额 包括收费未收费的
		StringBuffer phaRxNo=new StringBuffer(); //表使用 修改已经扣款的数据 冲负使用
		for (int i = 0; i < count; i++) {
			// 取一条order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			if (null != order.getPrintFlg() && order.getPrintFlg().equals("Y")) {
				continue;
			}
			
			//如果Q医已交费未打票不执行  add by huangtt 20160617
			if(order.getReceiptNo().length() > 0){
				continue;
			}
			
			
			// 如果未收费并且没有勾选不执行收费操作
			// =============pangben 2012-4-3
			// ChargeFlg =N && BILL_FLG=N 不执行
			if (!order.getChargeFlg() && order.getBillFlg().equals("N")) {
				continue;
			}
			
//			if (null != order.getBusinessNo()) {
//				if (!tradeNo.toString().contains(order.getBusinessNo())) {
//					// UPDATE EKT_TRADE 表使用 修改已经扣款的数据 冲负使用
//					tradeNo.append("'").append(order.getBusinessNo()).append(
//							"',");
//				}
//			}
			// if("C".equals(order.getBillType()))
			// continue;
			if (null != order.getBillFlg() && order.getBillFlg().equals("N")) {// 未收费医嘱
				setExeParm(parmBill, order);
				if (null != order.getCat1Type() && // ==pangben2013-5-15添加药房审药显示跑马灯数据
						order.getCat1Type().equals("PHA") && !order.getRxType().equals("7")
						&& !order.getRxType().equals("0")) {
					if (!phaRxNo.toString().contains(order.getRxNo())) {
						phaRxNo.append(order.getRxNo()).append(",");
					}
				}
			}
			if (cat1Type == null || cat1Type.length() == 0) {
				if (order.getOrdersetCode() != null
						&& order.getOrdersetCode().equals(order.getOrderCode())) {
					continue;
				}
			} else {
				if (!cat1Type.equals(order.getCat1Type())
						|| (order.getOrdersetCode() != null && order
								.getOrdersetCode().equals(order.getOrderCode()))) {
					continue;
				}

			}
			if (null != order.getBillFlg() && order.getBillFlg().equals("N")) {// 未收费医嘱
				billAmt += order.getArAmt();
			}
			sumAmt += order.getArAmt();// =======pangben 2012-8-13
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("AR_AMT", order.getArAmt());
			parm.addData("REXP_CODE", order.getRexpCode());
			parm.addData("EXEC_FLG", order.getExecFlg());
			parm.addData("RECEIPT_FLG", order.getReceiptFlg());
			parm.addData("OWN_PRICE", order.getOwnPrice());
			parm.addData("QTY", order.getDosageQty());
			parm.addData("DOSAGE_QTY", order.getDosageQty());
			parm.addData("SPECIFICATION", order.getSpecification());
			parm.addData("TAKE_DAYS", order.getTakeDays());
			parm.addData("DR_NOTE", order.getDrNote());
			parm.addData("ORDERSET_CODE", order.getOrdersetCode());
			parm.addData("HIDE_FLG", order.getHideFlg());
			parm.addData("NHI_CODE_O", order.getNhiCodeO());// ====pangben
															// 2012-8-8
			parm.addData("NHI_CODE_E", order.getNhiCodeE());// ====pangben
															// 2012-8-8
			parm.addData("NHI_CODE_I", order.getNhiCodeI());// ====pangben
															// 2012-8-8
			parm.addData("ORDER_DESC", order.getOrderDesc()
					+ order.getSpecification());
			parm.addData("YF", "");// 用法
			parm.addData("ZFBL1", "");// 自负比例
			parm.addData("PZWH", "");// 批准文号
			parm.addData("BUSINESS_NO", order.getBusinessNo());
			// System.out.println("医嘱"+order.getOrderDesc()+"收费注记"+order.getChargeFlg());
			// System.out.println(" order.getBillFlg()::"+ order.getBillFlg());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					//parm.setData("BUSINESS_TYPE", "OPBT");
					parm.setData("TYPE_FLG", "Y");// 医疗卡收费主档EKT_TREDE state 值判断
					// STATE=3 退费
				} else {
					parm.addData("BILL_FLG", "Y");
					//parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		parm.setData("sumAmt", sumAmt);// 所有金额 包括收费未收费
		parm.setData("billAmt", billAmt);// 未收费医嘱金额
		parm.setData("parmBill", parmBill.getData());// 未收费医嘱集合
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15添加药房审药显示跑马灯数据
		//String exeTradeNo = "";
		// 获得内部交易号码 ：此次操作的医嘱扣款所有需要退还的医嘱
//		if (tradeNo.length() > 0) {
//			exeTradeNo = tradeNo.toString().substring(0,
//					tradeNo.toString().lastIndexOf(","));
//		}
	   //parm.setData("TRADE_SUM_NO",exeTradeNo);//UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
		return parm;
	}
	private void setExeParm(TParm result,Order order){
		//result.addData("CAT1_TYPE", order.getCat1Type());// 过滤检验检查，医生修改医嘱医疗卡收费时使用
		result.addData("RX_NO", order.getRxNo());
		//result.addData("MED_APPLY_NO", order.getMedApplyNo());// 检验检查
		//result.addData("SETMAIN_FLG", order.getSetmainFlg());// 集合医嘱
		result.addData("SEQ_NO", order.getSeqNo());
		result.addData("BILL_TYPE","E");
		result.addData("BILL_FLG", "Y");
		result.addData("AMT", order.getArAmt());
		result.addData("AR_AMT", order.getArAmt());
		//result.addData("BUSINESS_NO", null==order.getBusinessNo()?"":order.getBusinessNo());
		result.addData("ORDER_CODE", order.getOrderCode());
	} 
	/**
	 * 得到现金入参数据
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getCashParm(boolean flg, String cat1Type) {
		TParm parm = new TParm();
		// 取所有的order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 取一条order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			// if("C".equals(order.getBillType()))
			// continue;
			if (cat1Type == null || cat1Type.length() == 0) {
				if (order.getOrdersetCode() != null
						&& order.getOrdersetCode().equals(order.getOrderCode())) {
					continue;
				}
			} else {
				if (!cat1Type.equals(order.getCat1Type())
						|| (order.getOrdersetCode() != null && order
								.getOrdersetCode().equals(order.getOrderCode()))) {
					continue;
				}

			}
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("QTY", order.getDctTakeQty());
			parm.addData("EXEC_FLG", order.getExecFlg());
			parm.addData("RECEIPT_FLG", order.getReceiptFlg());
			// System.out.println("医嘱"+order.getOrderDesc()+"收费注记"+order.getChargeFlg());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					parm.setData("BUSINESS_TYPE", "OPBT");
				} else {
					parm.addData("BILL_FLG", "Y");
					parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		return parm;
	}

	/**
	 * 检核是否有要收费的医嘱
	 * 
	 * @return boolean
	 */
	public boolean checkOrder() {
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 去一条order
			Order order = (Order) list.get(i);
			if (!order.getChargeFlg())
				continue;
			return false;
		}
		return true;
	}

	/**
	 * 集合医嘱修改批价列
	 * 
	 * @param orderCode
	 *            String
	 * @param orderSetGroupNo
	 *            int
	 * @param rxNo
	 *            String
	 * @param b
	 *            boolean
	 */
	public void congregation(String orderCode, int orderSetGroupNo,
			String rxNo, boolean b) {
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// 拿到一条order
			Order order = (Order) list.get(i);
			if (!order.getRxNo().equals(rxNo))
				continue;
			// 如果是集合医嘱细项设置收费标记
			if (order.getOrdersetCode().equals(orderCode)
					&& order.getOrderSetGroupNo() == orderSetGroupNo) {
				// 设置收费标记为N
				order.modifyChargeFlg(b);
			}
		}
	}

	/**
	 * 检核是否有医嘱
	 * 
	 * @return boolean
	 */
	public boolean checkOrderCount() {
		List list = getPrescriptionList().getOrder();
		// System.out.println("医嘱list"+list);
		if (list.size() > 0)
			return false;
		return true;
	}

	/**
	 * 检核剩余票据数
	 * 
	 * @return TParm
	 */
	public TParm checkInvoice() {
		TParm parm = new TParm();
		if (bilInvoice == null) {
			parm.addData("COUNT", -1);
			return parm;
		}
		String endNo = bilInvoice.getEndInvno();
		String updateNo = bilInvoice.getUpdateNo();
		return parm;
	}

	/**
	 * 医疗卡保存
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm onSaveEKT(TParm parm, boolean flg) {
		// System.out.println("整理前parm" + parm);

		// 给所有要收费的order添加回写数据
		chargeOrderEKT(parm, flg);
		TParm opbParm =parm.getParm("orderParm");
		// 加载医嘱数据------需要修改 医保收费扣费操作 ORDER parm 修改
		opbParm.setData("TRADE_NO", parm.getData("TRADE_NO"));
		opbParm.setData("HISTORY_NO", parm.getData("HISTORY_NO")); //add by huangtt 医疗卡历史表数据，与医嘱历史表用
		// opbParm.setData("GREEN_FLG", parm.getValue("GREEN_FLG")); //
		// 扣绿色通道金额注记
		opbParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		opbParm.setData("OPT_DATE", parm.getValue("OPT_DATE"));
		opbParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		opbParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		opbParm.setData("MR_NO", parm.getValue("MR_NO"));
		opbParm.setData("EKT_SQL", parm.getData("EKT_SQL"));  //add by huangtt 20160918医疗卡执行数据 
		
//		opbParm.setData("BILL_TYPE", "E");
//		// N:退费 Y:收费
//		if (flg) {
//			opbParm.setData("BILL_FLG", "N");
//		} else {
//			opbParm.setData("BILL_FLG", "Y");
//		}
		// 调用opbaction
//		 System.out.println("整理后parm" + opbParm);
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onSaveEKT", opbParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * 现金保存
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm onSaveCash(TParm parm, boolean flg) {
		// System.out.println("现金整理前parm" + parm);
		// 给所有要收费的order添加回写数据
		chargeOrderCash();
		TParm opbParm = new TParm();
		TParm modifyOrderParm = this.getPrescriptionList().getParm();
		modifyOrderParm.getParm(OrderList.MODIFIED);
		// 加载医嘱数据
		// TParm ins_result = parm.getParm("INS_RESULT");// 医保回参
		// 医保收费操作======pangb 2011-12-03
		opbParm
				.setData("ORDER", this.getPrescriptionList().getParm()
						.getData());
		if (flg)
			opbParm.setData("FLG", "N");
		else
			opbParm.setData("FLG", "Y");
		// 调用opbaction
		// System.out.println("现金整理后parm" + opbParm);
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onSave", opbParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * 保存
	 * 
	 * @param charge
	 *            String
	 * @return TParm
	 */
	public TParm onSave(String charge) {
		TParm parm = checkData();
		if (parm.getErrCode() == -1)
			return parm;
		// 给所有要收费的order添加回写数据
		chargeOrder();
		// 拿到odo中的parma
		TParm opbParm = getParm();
		// 是否收费
		// 收费权限
		TParm right = new TParm();
		// 加入权限
		right.setData("RIGHT", charge);
		// 添加权限
		opbParm.setData("RIGHT", right.getData());
		// 调用opbaction
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onSave", opbParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 得到退费保存parm
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getBackReceiptParm(int row) {
		TParm backReceiptParm = new TParm();
		OPBReceipt receipt = (OPBReceipt) receiptList.get(row);
		if (receipt == null)
			return null;
		String receiptNo = receipt.getReceiptNo();
		if (receiptNo == null || receiptNo.length() == 0)
			return null;
		BilInvrcpt bilInvrcpt = new BilInvrcpt();
		// 初始化一张退费的票据
		if (!bilInvrcpt.initBilInvrcpt(recpType, receipt.getPrintNo()))
			return null;
		// 处理退费order
		receipt.setBackReceiptValue();
		// 得到退费票据
		OPBReceipt receiptBack = receipt.getReceiptByTParm(receipt.getParm(),
				-1);
		TParm receiptParm = receipt.getParm();
		backReceiptParm.setData("UPRECEIPT", receiptParm.getData());
		// 得到收费票据
		TParm receiptBaceParm = receiptBack.getParm();
		backReceiptParm.setData("INRECEIPT", receiptBaceParm.getData());
		// 得到收据数据
		TParm bilInvrcptParm = setBackBilInvrcptValue(receipt.getPrintNo());
		backReceiptParm.setData("BILINVRCPT", bilInvrcptParm.getData());
		// 得到医嘱列表
		OrderList orderList = receipt.getOrderList();
		// 得到医嘱parm
		TParm orderListParm = orderList.getParm();
		backReceiptParm.setData("ORDER", orderListParm.getParm(
				orderList.MODIFIED).getData());

		return backReceiptParm;
	}

	/**
	 * 处理退费数据
	 * 
	 * @param printNo
	 *            String
	 * @return TParm
	 */
	public TParm setBackBilInvrcptValue(String printNo) {
		TParm bilInvrcptParm = new TParm();
		bilInvrcptParm.setData("RECP_TYPE", recpType);
		bilInvrcptParm.setData("CANCEL_FLG", "1");
		bilInvrcptParm.setData("INV_NO", printNo);
		bilInvrcptParm.setData("CANCEL_DATE", SystemTool.getInstance()
				.getDate());
		bilInvrcptParm.setData("CANCEL_USER", Operator.getID());
		bilInvrcptParm.setData("OPT_USER", Operator.getID());
		bilInvrcptParm.setData("OPT_TERM", Operator.getIP());
		return bilInvrcptParm;
	}

	/**
	 * 退费保存
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveBackReceipt(int row) {
		TParm saveParm = getBackReceiptParm(row);
		if (saveParm == null)
			return false;
		// 调用opbaction
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backReceipt", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 得到保存的数据
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getSaveRePrintParm(int row) {
		TParm saveRePrintParm = new TParm();
		OPBReceipt receipt = (OPBReceipt) receiptList.get(row);
		if (receipt == null)
			return null;
		String receiptNo = receipt.getReceiptNo();
		if (receiptNo == null || receiptNo.length() == 0)
			return null;
		BilInvrcpt backBilInvrcpt = new BilInvrcpt();
		// 初始化一张作废票据
		if (!backBilInvrcpt.initBilInvrcpt(recpType, receipt.getPrintNo()))
			return null;

		// 得到作废票据数据处理
		TParm bilInvrcptParm = setBackBilInvrcptValue(receipt.getPrintNo());
		saveRePrintParm.setData("UPBILINVRCPT", bilInvrcptParm.getData());

		// 给票据放入新的打印票号
		receipt.setPrintNo(bilInvoice.getUpdateNo());
		TParm receiptParm = receipt.getParm();
		saveRePrintParm.setData("UPRECEIPT", receiptParm.getData());

		// 得到打印的票据
		// 处理要打印的票据数据
		dealNewInvrcp(receipt);
		TParm newBilInvrcpt = bilInvrcpt.getParm();
		saveRePrintParm.setData("INBILINVRCPT", newBilInvrcpt.getData());

		// 得到当前使用的票据、、当前票号自加1
		bilInvoice.setUpdateNo(StringTool.addString(bilInvoice.getUpdateNo()));
		TParm newBilInvoice = bilInvoice.getParm();
		saveRePrintParm.setData("BILINVOICE", newBilInvoice.getData());
		return saveRePrintParm;
	}

	/**
	 * 处理要打印的票据
	 * 
	 * @param receipt
	 *            OPBReceipt
	 */
	public void dealNewInvrcp(OPBReceipt receipt) {
		bilInvrcpt.setInvNo(bilInvoice.getUpdateNo());
		bilInvrcpt.setArAmt(receipt.getArAmt());
		bilInvrcpt.setReceiptNo(receipt.getReceiptNo());
		bilInvrcpt.setCancelFlg("0");
	}

	/**
	 * 票据补打
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveRePrint(int row) {
		TParm saveRePrintParm = getSaveRePrintParm(row);
		if (saveRePrintParm == null)
			return false;
		// 调用opbaction
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"saveRePrint", saveRePrintParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}

		return true;
	}

	/**
	 * 得到集合医嘱的明细
	 * 
	 * @param groupNo
	 *            int
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetParm(int groupNo, String orderCode) {
		return getPrescriptionList().getOrderSetParm(groupNo, orderCode);
	}

	/**
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.initClient();
		TIOM_AppServer.resetAction();
		Pat pat = Pat.onQueryByMrNo("20");
		Reg reg = Reg.onQueryByCaseNo(pat, "090108000006");
		OPB opb = OPB.onQueryByCaseNo(reg);
		// System.out.println(opb);
		// System.out.println(opb.getOrderParm());
		// ((Order)opb.getOrderParm(true,"").getData("OBJECT",0)).modifyChargeFlg(true);
		// ((Order)opb.getOrderParm(true,"").getData("OBJECT",1)).modifyChargeFlg(true);
		// order.modifyChargeFlg(false);
		// System.out.println(opb.getFee());
		// System.out.println(opb.getPrescriptionList().getParm());
		// System.out.println(opb.getChargeParm());
		// System.out.println(opb.getBilInvoice().getParm());
		// opb.getPrescriptionComb();
		// System.out.println("  收据代码"+BIL.getRexpCode("101"));
		// BIL.getRexpCode("101");
		// System.out.println("11".compareTo("22"));
	}

	/**
	 * 得到建行入参数据 医保分割操作数据
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm
	 * 
	 */
	public TParm getInsCcbParm(boolean flg, TParm tparm) {
		TParm parm = new TParm();
		// 取所有的order
		// List list = getPrescriptionList().getOrder();
		int count = tparm.getCount();
		TParm parmSum = new TParm();
		double billAmt = 0.00;// 未收费医嘱金额
		double sumAmt = 0.00;// 未收费医嘱金额
		TParm result = null;
		for (int i = 0; i < count; i++) {
			// 取一条order
			// Order order = (Order) list.get(i);
			// parm.setData("MR_NO", );
			/*
			 * if (null!=order.getPrintFlg() && order.getPrintFlg().equals("Y"))
			 * { continue; }
			 */
			// 如果未收费并且没有勾选不执行收费操作
			// =============pangben 2012-4-3
			// ChargeFlg =N && BILL_FLG=N 不执行
			parmSum.addData("CAT1_TYPE", tparm.getData("CAT1_TYPE", i));// 过滤检验检查，医生修改医嘱医疗卡收费时使用
			parmSum.addData("RX_NO", tparm.getData("RX_NO", i));
			parmSum.addData("MED_APPLY_NO", tparm.getData("MED_APPLY_NO", i));// 检验检查
			parmSum.addData("SETMAIN_FLG", tparm.getData("SETMAIN_FLG", i));// 集合医嘱
			parmSum.addData("SEQ_NO", tparm.getData("SEQ_NO", i));
			parmSum.addData("BILL_FLG", "Y");

			if (tparm.getData("ORDERSET_CODE", i) != null
					&& tparm.getData("ORDERSET_CODE", i).equals(
							tparm.getData("ORDER_CODE", i))) {
				continue;
			}
			if (null != tparm.getData("BILL_FLG", i)
					&& "N".equals(tparm.getData("BILL_FLG", i))) {// 未收费医嘱
				billAmt += tparm.getDouble("AR_AMT", i);
			}
			sumAmt += tparm.getDouble("AR_AMT", i);
			parm.addData("RX_NO", tparm.getData("RX_NO", i));
			parm.addData("ORDER_CODE", tparm.getData("ORDER_CODE", i));
			parm.addData("SEQ_NO", tparm.getData("SEQ_NO", i));
			parm.addData("AMT", tparm.getData("AMT", i));
			parm.addData("AR_AMT", tparm.getData("AR_AMT", i));
			parm.addData("EXEC_FLG", tparm.getData("EXEC_FLG", i));
			parm.addData("RECEIPT_FLG", tparm.getData("RECEIPT_FLG", i));
			parm.addData("OWN_PRICE", tparm.getData("OWN_PRICE", i));
			parm.addData("QTY", tparm.getData("QTY", i));
			parm.addData("DOSAGE_QTY", tparm.getData("DOSAGE_QTY", i));
			parm.addData("SPECIFICATION", tparm.getData("SPECIFICATION", i));
			parm.addData("TAKE_DAYS", tparm.getData("TAKE_DAYS", i));
			parm.addData("DR_NOTE", tparm.getData("DR_NOTE", i));
			parm.addData("ORDERSET_CODE", tparm.getData("ORDERSET_CODE", i));
			parm.addData("HIDE_FLG", tparm.getData("HIDE_FLG", i));
			parm.addData("NHI_CODE_O", tparm.getData("NHI_CODE_O", i));
			parm.addData("NHI_CODE_E", tparm.getData("NHI_CODE_E", i));
			parm.addData("NHI_CODE_I", tparm.getData("NHI_CODE_I", i));
			parm.addData("ORDER_DESC", (String) tparm.getData("ORDER_DESC", i)
					+ (String) tparm.getData("SPECIFICATION", i));
			TParm insparm = new TParm();
			insparm.setData("BILL_D", SystemTool.getInstance().getDate());
			insparm.setData("INS_CODE", tparm.getData("NHI_CODE_O", i));
			result = INSIbsTool.getInstance().queryInsIbsOrderByInsRule(insparm);
			if (result.getErrCode() < 0) {
				return result;
			}
			parm.addData("YF", result.getValue("YF", 0));// 用法
			parm.addData("ZFBL1", result.getValue("ZFBL1", 0));// 自负比例
			parm.addData("PZWH", result.getValue("PZWH", 0));// 批准文
			if (tparm.getBoolean("HIDE_FLG", i)) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					parm.setData("BUSINESS_TYPE", "OPBT");
					parm.setData("TYPE_FLG", "Y");// 医疗卡收费主档EKT_TREDE state 值判断
					// STATE=3 退费
				} else {
					parm.addData("BILL_FLG", "Y");
					parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", tparm.getData("BILL_FLG", i));
		}
		parm.setData("sumAmt", sumAmt);// 所有金额 包括收费未收费
		parm.setData("billAmt", billAmt);// 未收费医嘱金额
		parm.setData("parmSum", parmSum.getData());// 包括集合医嘱金额修改OPD_ORDER
													// MED_APPLY使用
		return parm;
	}
}
