package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import jdo.med.MEDApplyTool;
import jdo.odo.OpdOrder;

import java.util.Map;
import java.util.HashMap;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import java.sql.Timestamp;

/**
 * 
 * <p>
 * Title: 医嘱tool
 * 
 * <p>
 * Description: 医嘱tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * 
 * @author ehui 20080911
 * @version 1.0
 */
public class OrderTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static OrderTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return OrderTool
	 */
	public static OrderTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OrderTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public OrderTool() {
		setModuleName("opd\\OPDOrderModule.x");

		onInit();
	}

	/**
	 * 新增医嘱
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertdata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 新增医嘱(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdataForOPB(TParm parm, TConnection connection) {
		TParm result = new TParm();
		parm.setData("RX_TYPE", "7");
		parm.setData("OWN_AMT", StringTool.round(parm.getDouble("DOSAGE_QTY")*parm.getDouble("OWN_PRICE"),2));//====pangben 2013-8-30 身份折扣操作自费金额修改
		result = update("insertdataForOPB", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 新增医嘱(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdataForOPBEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertdataForOPBEKT", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 判断是否存在数据
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean existsOrder(TParm parm) {
		return getResultInt(query("existsOrder", parm), "COUNT") > 0;
	}

	/**
	 * 更新数据
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updatedata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if ("N".equals(parm.getValue("BILL_FLG"))) {
			parm.setData("BILL_DATE", new TNull(Timestamp.class));
			parm.setData("RECEIPT_FLG", "N");// 退费将数据修改
		}
		result = update("updatedata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新数据(For门诊收费)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForOPB(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if ("N".equals(parm.getValue("BILL_FLG")))
			parm.setData("BILL_DATE", new TNull(Timestamp.class));
		result = update("upForOPB", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除数据
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deletedata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("deletedata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 检索数据
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm query(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm data = query("query", parm);
		if (data.getErrCode() < 0) {
			err("Order+ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		TParm result = new TParm();

		int count = data.getCount();
		TParm groupParm = null;
		TParm orderList = null;
		String oldRxType = "";
		String oldRxNo = "";
		for (int i = 0; i < count; i++) {
			// 处方类型
			String rxType = data.getValue("RX_TYPE", i);
			if (!rxType.equalsIgnoreCase(oldRxType)) {
				groupParm = new TParm();
				groupParm.setData("NAME", rxType);
				result.addData("GROUP", groupParm.getData());
				result.setData("ACTION", "COUNT", result.getCount("GROUP"));
				oldRxType = rxType;
				oldRxNo = "";
			}
			// 处方号
			String rxNo = data.getValue("RX_NO", i);
			if (!rxNo.equalsIgnoreCase(oldRxNo)) {
				orderList = new TParm();
				groupParm.addData("LIST", orderList.getData());
				groupParm
						.setData("ACTION", "COUNT", groupParm.getCount("LIST"));
				oldRxNo = rxNo;
			}
			int row = orderList.insertRow(-1, StringTool.getString(data
					.getNames(), ";"));
			orderList.setRowData(row, data, i);
			orderList.setData("ACTION", "COUNT", row + 1);
		}
		return result;
	}

	/**
	 * 为PHA专用的检索方法，parm 中的参数为
	 * 
	 * @param parm
	 *            TParm
	 * @return result TParm
	 */
	public TParm queryForPHA(TParm parm) {
		TParm data = query("selectdataforPha", parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		TParm result = new TParm();

		int count = data.getCount();
		TParm orderList = null;
		String oldRxNo = "";
		for (int i = 0; i < count; i++) {
			// 处方号
			String rxNo = data.getValue("RX_NO", i);
			if (!rxNo.equalsIgnoreCase(oldRxNo)) {
				orderList = new TParm();
				result.addData("LIST", orderList.getData());
				result.setData("ACTION", "COUNT", orderList.getCount("LIST"));
				oldRxNo = rxNo;
			}
			int row = orderList.insertRow(-1, StringTool.getString(data
					.getNames(), ";"));
			orderList.setRowData(row, data, i);
			orderList.setData("ACTION", "COUNT", row + 1);
		}

		return result;
	}

	/**
	 * 为PHA专用退药的检索方法，parm 中的参数为
	 * 
	 * @param parm
	 *            TParm
	 * @return result TParm
	 */
	public TParm queryForPhaReturn(TParm parm) {
		TParm data = query("selectdataforPhaReturn", parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		TParm result = new TParm();

		int count = data.getCount();
		TParm orderList = null;
		String oldRxNo = "";
		for (int i = 0; i < count; i++) {
			// 处方号
			String rxNo = data.getValue("RX_NO", i);
			if (!rxNo.equalsIgnoreCase(oldRxNo)) {
				orderList = new TParm();
				result.addData("LIST", orderList.getData());
				result.setData("ACTION", "COUNT", orderList.getCount("LIST"));
				oldRxNo = rxNo;
			}
			int row = orderList.insertRow(-1, StringTool.getString(data
					.getNames(), ";"));
			orderList.setRowData(row, data, i);
			orderList.setData("ACTION", "COUNT", row + 1);
		}

		return result;
	}

	/**
	 * 删除
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onDelete(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		TParm resulthistory = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.deletedata(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}

		return result;
	}

	/**
	 * 插入
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsert(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		parm = getAppleNo(parm);
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdata(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}

		result = MEDApplyTool.getInstance().insertMedApply(parm, connection);
		if (result.getErrCode() < 0)
			return result;
		return result;
	}

	/**
	 * 插入(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertForOPB(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		parm = getAppleNo(parm);
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdataForOPB(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}

		result = MEDApplyTool.getInstance().insertMedApply(parm, connection);
		if (result.getErrCode() < 0)
			return result;
		return result;
	}

	/**
	 * 医疗卡删除医嘱操作撤销动作使用
	 * 
	 * @param parm
	 * @param connection
	 * @return ==============pangben 2012-3-7
	 */
	public TParm onInsertForOpbEkt(TParm parm, TConnection connection) {
		int count = parm.getCount("ORDER_CODE");
		TParm result = new TParm();
		parm = getAppleNo(parm);
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insertdataForOPBEKT(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
		}
		//========d=pangben 2012-6-6 修改门诊医生站已经收费医疗卡撤销删除操作不执行添加MED_APPLY表数据
		if (null != parm.getValue("MED_FLG")
				&& parm.getValue("MED_FLG").equals("Y")) {

		} else {
			result = MEDApplyTool.getInstance()
					.insertMedApply(parm, connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;

	}

	public TParm getAppleNo(TParm parm) {
		Map labMap = new HashMap();
		Map risNoMap = new HashMap();
		String labNo = "";
		String risNo = "";
		int count = parm.getCount("CASE_NO");
		for (int i = 0; i < count; i++) {
			if (parm.getValue("CAT1_TYPE", i).equals("LIS")
					&& parm.getBoolean("SETMAIN_FLG", i)) {
				String labMapKey = parm.getValue("DEV_CODE", i)
						+ parm.getValue("OPTITEM_CODE", i)
						+ parm.getValue("RPTTYPE_CODE", i);
				if (labMap.get(labMapKey) != null) {
					parm.setData("MED_APPLY_NO", i, labMap.get(labMapKey));
					// 给集合医嘱细项赋条码号
					parm = setOrderSetListLabNo(parm, parm.getValue(
							"ORDERSET_CODE", i), parm.getValue(
							"ORDERSET_GROUP_NO", i), labMap.get(labMapKey)
							.toString());
					continue;
				}
				labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				// 放入新的LAB_NO
				labMap.put(labMapKey, labNo);
				parm.setData("MED_APPLY_NO", i, labNo);
				// 给集合医嘱细项赋条码号
				parm = setOrderSetListLabNo(parm, parm.getValue(
						"ORDERSET_CODE", i), parm.getValue("ORDERSET_GROUP_NO",
						i), labNo);
			}
			if (parm.getValue("CAT1_TYPE", i).equals("RIS")
					&& parm.getBoolean("SETMAIN_FLG", i)) {
				String risMapKey = parm.getValue("ORDERSET_CODE", i)
						+ parm.getValue("ORDERSET_GROUP_NO", i);
				// 如果有就给当前LIS医嘱赋值LAB_NO
				if (risNoMap.get(risMapKey) != null) {
					continue;
				}
				risNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				// 放入新的LAB_NO
				risNoMap.put(risMapKey, risNo);
				parm.setData("MED_APPLY_NO", i, risNo);
				// 给集合医嘱细项赋条码号
				parm = setOrderSetListLabNo(parm, parm.getValue(
						"ORDERSET_CODE", i), parm.getValue("ORDERSET_GROUP_NO",
						i), risNo);
			}
		}
		return parm;
	}

	/**
	 * 给集合医嘱细项赋值
	 * 
	 * @param parm
	 *            TParm
	 * @param orderSetCode
	 *            String
	 * @param groupNo
	 *            String
	 * @param labNo
	 *            String
	 * @return TParm
	 */
	public TParm setOrderSetListLabNo(TParm parm, String orderSetCode,
			String groupNo, String labNo) {
		int count = parm.getCount("CASE_NO");
		for (int i = 0; i < count; i++) {
			String risMapKey = parm.getValue("ORDERSET_CODE", i)
					+ parm.getValue("ORDERSET_GROUP_NO", i);
			// 相同的集合医嘱赋值
			if (risMapKey.equals(orderSetCode + groupNo)) {
				// 主项排除
				if (parm.getBoolean("SETMAIN_FLG", i))
					continue;
				parm.setData("MED_APPLY_NO", i, labNo);
			}
		}
		return parm;
	}

	/**
	 * 更新
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.updatedata(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
			// System.out.println("OPB更新med数据"+inParm);
			result = MEDApplyTool.getInstance()
					.updateStauts(inParm.getValue("CASE_NO"),
							inParm.getValue("MED_APPLY_NO"),
							inParm.getValue("RX_NO"),
							inParm.getValue("SEQ_NO"), parm.getValue("FLG"),
							connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * 更新forOPB
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateOPB(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.upForOPB(inParm, connection);
			if (result.getErrCode() < 0)
				return result;
			// System.out.println("OPB更新med数据"+inParm);
			result = MEDApplyTool.getInstance()
					.updateStauts(inParm.getValue("CASE_NO"),
							inParm.getValue("MED_APPLY_NO"),
							inParm.getValue("RX_NO"),
							inParm.getValue("SEQ_NO"), parm.getValue("FLG"),
							connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * odo异动主入口
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(OrderList.NEW), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm upParm = parm.getParm(OrderList.MODIFIED);
		upParm.setData("FLG", parm.getData("FLG"));
		result = onUpdate(upParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * odo异动主入口(For OPB)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveOPB(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsertForOPB(parm.getParm(OrderList.NEW), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm upParm = parm.getParm(OrderList.MODIFIED);
		upParm.setData("FLG", parm.getData("FLG"));
		result = onUpdate(upParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * odo异动主入口(门诊收费保存)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onOPBSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(OrderList.DELETED), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(OrderList.NEW), connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm upParm = parm.getParm(OrderList.MODIFIED);
		upParm.setData("FLG", parm.getData("FLG"));
		result = onUpdateOPB(upParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 开单科室工作量统计表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOpenDeptList(TParm parm) {
		TParm result = query("selectOpenDeptList", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 皮试医嘱查询 （门诊护士站皮试执行 使用）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPS(TParm parm) {
		TParm result = this.query("selectPS", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 护士执行（门诊护士站 皮试执行）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePS(TParm parm) {
		TParm result = this.update("updatePS", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询全字段数据(门诊收费医疗卡打印)：没有收费
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBEKT(TParm parm) {
		TParm result = query("selDataForOPBEKT", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询全字段数据(门诊收费医疗卡打印)：已经收费
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBEKTC(TParm parm) {
		TParm result = query("selDataForOPBEKTC", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询全字段数据(门诊收费现金打印)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBCash(TParm parm) {
		TParm result = query("selDataForOPBCash", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询全字段数据(门诊收费现金打印)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selDataForOPBCashIns(TParm parm) {
		TParm result = query("selDataForOPBCashIns", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询当前病患费用和(For Reg)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selPatFeeForREG(TParm parm) {
		TParm result = query("selPatFeeForREG", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 门诊医疗卡收费更新（门诊医疗卡收费）
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateForOPBEKT(TParm parm, TConnection connection) {
		TParm result = new TParm();
		parm.setData("PRINT_FLG", "Y");
		result = update("updateForOPBEKT", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 门诊现金收费更新（门诊现金收费）
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateForOPBCash(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateForOPBCash", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 门诊收费医疗卡作废收据：医疗卡
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForOPBEKTReturn(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("upForOPBEKTReturn", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 门诊收费作废收据:现金
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForOPBReturn(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("upForOPBReturn", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询全字段
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm query(TParm parm) {
		TParm result = query("query", parm);
		if (result.getErrCode() < 0) {
			err("Order+ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 现金费用清单使用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ================pangben 20111014
	 */
	public TParm queryFill(TParm parm) {
		TParm result = query("queryFill", parm);
		if (result.getErrCode() < 0) {
			err("Order+ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 医疗卡费用清单使用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ================pangben 20111014
	 */
	public TParm queryFillEKT(TParm parm) {
		TParm result = query("queryFillEKT", parm);
		if (result.getErrCode() < 0) {
			err("Order+ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 执行记账操作，门诊不打票更新
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============pangben 20110818
	 */
	public TParm updateForRecode(TParm parm, TConnection connection) {
		TParm result = update("updateForRecode", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;

	}

	/**
	 * 医疗卡修改医嘱表中数据
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm ======================pangben 2011915
	 */
	public TParm updateBillSets(TParm parm, TConnection connection) {
		TParm result = update("updateBillSets", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}

	/**
	 * 门诊医疗卡退费操作出现问题
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillSetsOne(TParm parm, TConnection connection) {
		TParm result = update("updateBillSetsOne", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}

	/**
	 * 门诊医生站修改医嘱需要更新一个收据号码 重新获得收据金额
	 * 
	 * @param parm
	 * @param connection
	 *            ========pangben 2012-3-02
	 * @return
	 */
	public TParm updateForOPBEKTReceiptNo(TParm parm, TConnection connection) {
		TParm result = update("updateForOPBEKTReceiptNo", parm, connection);
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}

	/**
	 * 删除医嘱(门诊收费界面) ====zhangp 20120414
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBCharge(TParm parm, TConnection connection) {

		String sql = "DELETE FROM OPD_ORDER " + " WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' " + "AND RX_NO = '"
				+ parm.getValue("RX_NO") + "' AND SEQ_NO = '"
				+ parm.getValue("SEQ_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		// 添加医嘱历史数据============pangben 2012-4-15
		sql = "SELECT CASE_NO, RX_NO, SEQ_NO,DC_ORDER_DATE, PRESRT_NO, REGION_CODE, "
				+ " MR_NO, ADM_TYPE, RX_TYPE,  RELEASE_FLG, LINKMAIN_FLG, LINK_NO, "
				+ " ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, "
				+ " MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, "
				+ " DISPENSE_QTY, DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, "
				+ " OWN_AMT, AR_AMT, DR_NOTE, NS_NOTE, DR_CODE, ORDER_DATE, "
				+ " DEPT_CODE, DC_DR_CODE, DC_DEPT_CODE,  EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG,"
				+ " ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG,  RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, "
				+ " MR_CODE, FILE_NO, DEGREE_CODE,  URGENT_FLG, INSPAY_TYPE, PHA_TYPE, "
				+ " DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, PRESCRIPT_NO, HEXP_CODE, "
				+ " CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, NS_EXEC_CODE, NS_EXEC_DATE, "
				+ " NS_EXEC_DEPT, DCTAGENT_CODE, DCTEXCEP_CODE, DCT_TAKE_QTY, PACKAGE_TOT, OPT_USER, "
				+ " OPT_DATE, OPT_TERM FROM OPD_ORDER "
				+ " WHERE RELEASE_FLG <> 'Y' AND CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND  RX_NO = '"
				+ parm.getValue("RX_NO")
				+ "' AND SEQ_NO = '"
				+ parm.getValue("SEQ_NO") + "'";
		TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (orderParm.getCount()<=0) {
			return new TParm();
		}
		// 添加医嘱历史数据============pangben 2012-4-15
		result = OrderHistoryTool.getInstance().insertOpdOrderHistory(
				orderParm.getRow(0), connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 删除医嘱集合细项(门诊收费界面) ====zhangp 20120416
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBChargeSet(TParm parm, TConnection connection) {
		String sql = "DELETE FROM OPD_ORDER " + " WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' " + "AND RX_NO = '"
				+ parm.getValue("RX_NO") + "' AND ORDERSET_CODE = '"
				+ parm.getValue("ORDERSET_CODE") + "' "
				+ "AND ORDERSET_GROUP_NO = '"
				+ parm.getValue("ORDERSET_GROUP_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		// 添加医嘱历史数据============pangben 2012-4-15
		sql = "SELECT CASE_NO, RX_NO, SEQ_NO,DC_ORDER_DATE, PRESRT_NO, REGION_CODE, "
				+ " MR_NO, ADM_TYPE, RX_TYPE,  RELEASE_FLG, LINKMAIN_FLG, LINK_NO, "
				+ " ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, "
				+ " MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, "
				+ " DISPENSE_QTY, DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, "
				+ " OWN_AMT, AR_AMT, DR_NOTE, NS_NOTE, DR_CODE, ORDER_DATE, "
				+ " DEPT_CODE, DC_DR_CODE, DC_DEPT_CODE,  EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG,"
				+ " ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG,  RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, "
				+ " MR_CODE, FILE_NO, DEGREE_CODE,  URGENT_FLG, INSPAY_TYPE, PHA_TYPE, "
				+ " DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, PRESCRIPT_NO, HEXP_CODE, "
				+ " CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, NS_EXEC_CODE, NS_EXEC_DATE, "
				+ " NS_EXEC_DEPT, DCTAGENT_CODE, DCTEXCEP_CODE, DCT_TAKE_QTY, PACKAGE_TOT, OPT_USER, "
				+ " OPT_DATE, OPT_TERM FROM OPD_ORDER "
				+ " WHERE RELEASE_FLG <> 'Y' AND CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND  RX_NO = '"
				+ parm.getValue("RX_NO")
				+ "' AND ORDERSET_CODE = '"
				+ parm.getValue("ORDERSET_CODE")
				+ "' AND ORDERSET_GROUP_NO = '"
				+ parm.getValue("ORDERSET_GROUP_NO") + "'";
		TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
		// 查询集合医嘱数据
		result = OrderHistoryTool.getInstance().onInsertHistory(orderParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	public static void main(String args[]) {
		JavaHisDebug.initClient();
		TParm parm = new TParm();
		parm.setData("CASE_NO", "ABC");
	}
	/**
	 * 物联网写入医嘱数据，门诊使用
	 * @return
	 * ===========pangben 2013-5-20
	 */
	public String insertOpdOrderPhaSpc(TParm parm){
		TParm result = update("insertOpdOrderSpc", parm);
		if (result.getErrCode() != 0)
			return "ERR:" + result.getErrText();
		return "SUCCESS";
	}
	/**
	 * 通过处方签删除操作的医嘱，物联网操作
	 * @param rxNo 
	 * @return
	 * ===========pangben 2013-5-20
	 */
	public String deleteOpdOrderPhaSpc(String rxNo){
		String sql="DELETE FROM OPD_ORDER WHERE RX_NO IN("+rxNo+") AND CAT1_TYPE='PHA' AND BILL_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() != 0)
			return "ERR:" + result.getErrText();
		return "SUCCESS";
	}
	/**
	 * 更新药嘱执行状态的标记
	 * yanjing
	 * 20130415
	 * @return TParm
	 */
	public TParm getFlgUpdateDate(TParm parm,TConnection conn) {
		TParm result = update("savedata", parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 物联网获得此次操作的医嘱，通过处方签获得
	 * @return
	 * =============pangben 2013-5-21
	 */
	public TParm getSumOpdOrderByRxNo(TParm parm){
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.PRESRT_NO,A.REGION_CODE,A.MR_NO,A.ADM_TYPE,A.RX_TYPE,A.TEMPORARY_FLG,")
		.append("A.RELEASE_FLG,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,")
		.append("A.ORDER_CAT1_CODE,A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,")
		.append("A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,")
		.append("A.DR_NOTE,A.NS_NOTE,A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE,A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,")
		.append("A.EXEC_DR_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.HIDE_FLG,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,")
		.append("A.MR_CODE,A.FILE_NO,A.DEGREE_CODE,A.URGENT_FLG,A.INSPAY_TYPE,A.PHA_TYPE,A.DOSE_TYPE,A.EXPENSIVE_FLG,A.PRINTTYPEFLG_INFANT,")
		.append("A.CTRLDRUGCLASS_CODE,A.PRESCRIPT_NO,A.ATC_FLG,A.SENDATC_DATE,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE,A.BILL_USER,A.PRINT_FLG,")
		.append("A.REXP_CODE,A.HEXP_CODE,A.CONTRACT_CODE,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,")
		.append( "A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.PHA_RETN_CODE,A.PHA_RETN_DATE,")
		.append( "A.NS_EXEC_CODE,A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,A.PACKAGE_TOT,A.AGENCY_ORG_CODE,")
		.append("A.DCTAGENT_FLG,A.DECOCT_CODE,A.REQUEST_FLG,A.REQUEST_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.MED_APPLY_NO,A.CAT1_TYPE,")
		.append("A.TRADE_ENG_DESC,A.PRINT_NO,A.COUNTER_NO,A.PSY_FLG,A.EXEC_FLG,A.RECEIPT_FLG,A.BILL_TYPE,A.FINAL_TYPE,A.DECOCT_REMARK,")
		.append("A.SEND_DCT_USER,A.SEND_DCT_DATE,A.DECOCT_USER,A.DECOCT_DATE,A.SEND_ORG_USER,A.SEND_ORG_DATE,A.EXM_EXEC_END_DATE,A.EXEC_DR_DESC,")
		.append("A.COST_AMT,A.COST_CENTER_CODE,A.BATCH_SEQ1,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.BATCH_SEQ2,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,")
		.append("A.BATCH_SEQ3,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.BUSINESS_NO,B.PAT_NAME,CASE ")
		.append("WHEN B.SEX_CODE ='1' ")
		.append("THEN '男' WHEN B.SEX_CODE='2' THEN '女' ")
		.append(" ELSE '未知' ")
		.append(" END AS SEX_TYPE,B.BIRTH_DATE FROM OPD_ORDER A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.BILL_FLG='Y'");
		if (parm.getValue("CASE_NO").length() > 0) {
			sql.append(" AND CASE_NO='").append(parm.getValue("CASE_NO")).append("'");
		}
		if(parm.getValue("RX_NO").length()>0){
			sql.append(" AND RX_NO IN (").append(parm.getValue("RX_NO")).append(")");
		}
		if (parm.getValue("CAT1_TYPE").length() > 0) {
			sql.append(" AND CAT1_TYPE='").append(parm.getValue("CAT1_TYPE")).append("'");
		}
		if (parm.getValue("RX_TYPE").length() > 0) {
			sql.append(" AND RX_TYPE<>'").append(parm.getValue("RX_TYPE")).append("'");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 门诊医生站药品校验审配发
	 * @param needExamineFlg 数据库配置
	 * @param order 医嘱信息
	 * flg true 删除一行 或者修改动作使用   false  删除处方签使用
	 * @return
	 * ==========pangben 2014-1-1
	 */
	public int checkPhaIsExe(boolean needExamineFlg,TParm order,boolean flg){
		// 如果有审核流程 那么判断审核医师是否为空
		TParm result = query("checkPhaIsExe", order);
		if (result.getErrCode() < 0)
			return 1;
		if (result.getCount()<=0) {//没有查询出来数据说明此医嘱还没有保存到数据库
			return 0;
		}
		
		if (result.getValue("PHA_RETN_CODE",0).length()>0) {//已经退药状态
			return 5;// 已经退药不可以做修改
		}
		
		if (needExamineFlg) {
			// System.out.println("有审核");
			// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
			if (flg) {
//				if (result.getValue("PHA_RETN_CODE",0).length()>0){//已退药  未收费
//					return 5;
//				}
				if (result.getValue("PHA_CHECK_CODE",0).length() > 0){
					return 1;
				}
			}else{
				if (result.getValue("PHA_CHECK_CODE",0).length() > 0
						&& result.getValue("PHA_RETN_CODE",0).length() == 0) {
					return 1;
				}
			}
		} else {// 没有审核流程 直接配药
			// 判断是否有配药药师
			// System.out.println("无审核");
					
			// 判断是否有配药药师
			if (flg) {// ============pangben 2013-4-17 添加修改医嘱交验
				if (result.getValue("PHA_DOSAGE_CODE",0).length() > 0) {
					return 1;
				}
			} else {
				if (result.getValue("PHA_DOSAGE_CODE",0).length() > 0
						&& result.getValue("PHA_RETN_CODE",0).length() == 0) {
					return 1;// 已经配药不可以做修改
				}
			}
			
			
		}
		
//		if (result.getValue("BILL_FLG",0).equals("Y")) {
//			return 2;// 已经收费不可以做修改
//		}
		return 0;
	}
	
	/**
	 * 更新门诊采血执行科室
	 * huangtt
	 * 20170419
	 * @return TParm
	 */
	public TParm getUpdateExecDeptBlood(TParm parm,TConnection conn) {
		TParm result = update("updateExecDeptBlood", parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
}
