package jdo.sys;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SysPhaBarTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SysPhaBarTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return OperatorTool
	 */
	public static SysPhaBarTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SysPhaBarTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SysPhaBarTool() {
	}

	/**
	 * 取得药品条码
	 * 
	 * @return String
	 */
	public String getBarCode() {
		return SystemTool.getInstance().getNo("ALL", "UDD", "BAR_CODE",
				"BAR_CODE");
	}

	/**
	 * 组装BAR_CODE对象
	 * 
	 * @param orderParm
	 * @param Type
	 *            （UDD住院药房）
	 * @return
	 */
	public TParm getaddBarParm(TParm orderParm, String Type) {
		TParm result = new TParm();
		String admType = orderParm.getValue("ADM_TYPE");
		String sql = "";
		// 连接医嘱条码
		String LbarCode = "";
		// 口服条码
		String ObarCode = "";
		// 外用条码
		String EbarCode = "";
		// 针剂条码
		String IbarCode = "";
		// 点滴条码
		String FbarCode = "";
		String FlinkNo = "";
		//
		Map map = new HashMap();
		Map linkmap = new HashMap();
		int Ocount = 0;
		for (int i = 0; i < orderParm.getCount("CASE_NO"); i++) {
			TParm parm = orderParm.getRow(i);
			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (admType.equals("O") || admType.equals("E")) {

				} else if (admType.equals("I")) {
					sql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
							+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
							+ parm.getValue("ORDER_SEQ") + "' AND START_DTTM='"
							+ parm.getValue("START_DTTM") + "'";
				} else if (admType.equals("H")) {

				}
				TParm dataParm = new TParm(getDBTool().select(sql));
				// 连接号
				String linkNo = dataParm.getValue("LINK_NO", 0);
				// 药品类型
//				String type = getDoseType(dataParm.getValue("ORDER_CODE", 0));
				String type=getClassifyType(dataParm.getValue("ROUTE_CODE", 0));
				String barCode = dataParm.getValue("BAR_CODE", 0);
				// // 判断连接医嘱（一组一码）
				// if (!linkNo.equals("")) {
				// // System.out.println("连接医嘱");
				// // 连接号不相等且条码字段为空
				// if (barCode.equals("")) {
				// // 连接号不相等取号
				// if (!linkNo.equals(FlinkNo)) {
				// LbarCode = getBarCode();
				// FlinkNo = linkNo;
				// // 连接号相等就诊号不相等取号
				// } else if (linkNo.equals(FlinkNo)
				// && linkmap.get(parm.getValue("CASE_NO")) == null) {
				// LbarCode = getBarCode();
				// linkmap.put(parm.getValue("CASE_NO"),
				// parm.getValue("CASE_NO"));
				// }
				// }
				// // 字段为空将
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", LbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// } else {
				// 口服（一种一码）
				if ("O".equals(type)) {
					// 护士执行
					// if (Type.equals("INW")) {
					// if (barCode.equals(""))
					// orderParm.addData("BAR_CODE", "");
					// else
					// orderParm.addData("BAR_CODE", barCode);
					// }
					// 住院药房配药
					if (Type.equals("UDD")) {
						if (map.get(parm.getValue("CASE_NO")) == null) {
							ObarCode = getBarCode();
							map.put(parm.getValue("CASE_NO"), ObarCode);
						}
						// System.out.println("口服");
						if (barCode.equals(""))
							orderParm.addData("BAR_CODE",
									(String) map.get(parm.getValue("CASE_NO")));
						else
							orderParm.addData("BAR_CODE", barCode);
					}
				} else {
					orderParm.addData("BAR_CODE", barCode);
				}
				// // 外用（一个一码）
				// else if ("E".equals(type)) {
				// EbarCode = getBarCode();
				// // System.out.println("外用");
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", EbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// }
				// // 针剂（一个一码）
				// else if ("I".equals(type)) {
				// IbarCode = getBarCode();
				// // System.out.println("针剂");
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", IbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// }
				// // 点滴（一个一码）
				// else if ("F".equals(type)) {
				// FbarCode = getBarCode();
				// // System.out.println("点滴");
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", FbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// } else {
				// orderParm.addData("BAR_CODE", "");
				// }
				// }
				// } else {
				// orderParm.addData("BAR_CODE", "");
			}
		}
		return orderParm;
	}

	/**
	 * 得到剂型
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getDoseType(String orderCode) {
		String sql = " SELECT A.ORDER_CODE,A.ORDER_DESC,B.DOSE_TYPE FROM PHA_BASE A,PHA_DOSE B "
				+ " WHERE A.DOSE_CODE=B.DOSE_CODE ";
		TParm parm = new TParm();
		if (orderCode.equals("")) {
			return "";
		} else {
			sql += " AND A.ORDER_CODE='" + orderCode + "'";
		}
		parm = new TParm(getDBTool().select(sql));
		if (parm.getCount() < 0) {
			System.out.println("医嘱" + orderCode + "得到剂型错误");
		}
		return parm.getValue("DOSE_TYPE", 0);
	}
    /**
     * 根据服法得到剂型
     * @param routeCode
     * @return
     */
	public String getClassifyType(String routeCode) {
		String sql = " SELECT A.ROUTE_CODE,A.ROUTE_CHN_DESC,A.CLASSIFY_TYPE FROM SYS_PHAROUTE A";
		TParm parm = new TParm();
		if (routeCode.equals("")) {
			return "";
		} else {
			sql += " WHERE A.ROUTE_CODE='" + routeCode + "'";
		}
		parm = new TParm(getDBTool().select(sql));
		if (parm.getCount() < 0) {
			System.out.println("用法" + routeCode + "得到剂型错误");
		}
		return parm.getValue("CLASSIFY_TYPE", 0);
	}

	/**
	 * getDBTool 数据库工具实例
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
