package jdo.spc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;

import jdo.ind.INDSQL;
import jdo.ind.IndStockDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:紧急抢救麻精出智能柜
 * </p>
 * 
 * <p>
 * Description:紧急抢救麻精出智能柜
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */
public class SPCJjStoreOutTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static SPCJjStoreOutTool instanceObject;

	/**
	 * 构造器
	 */
	public SPCJjStoreOutTool() {
		// setModuleName("spc\\SpcJjStoreOutModule.x");
		onInit();
	}

	/**
	 * 得到实例
	 * 
	 * @return IndPurPlanMTool
	 */
	public static SPCJjStoreOutTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCJjStoreOutTool();
		return instanceObject;
	}

	/**
	 * 紧急抢救麻精药出智能柜，添加交易表，更改库存
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm JjStockOut(TParm parm, TConnection conn) {
		TParm result = null;
		String orgCode = (String) parm.getData("ORG_CODE");
		List list = (List) parm.getData("list");			
		Set set = (Set) parm.getData("set");
		Iterator iterator = set.iterator();		
		Object[] obj = set.toArray();
		// 出库单号
		String dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_DISPENSE", "No");
		String cabinetId = (String) parm.getData("CABINET_ID");
		int count = 1;
		Map map = new HashMap();
		for (int i = 0; i < obj.length; i++) {
			String containerId = String.valueOf(obj[i]);
			TParm inparm = new TParm();		
			inparm.setData("CONTAINER_ID", containerId);
			result = SPCContainerTool.getInstance().queryInfo(inparm);
			String orderCode = (String) result.getData("ORDER_CODE", 0);
			String orderDesc = (String) result.getData("ORDER_DESC", 0);
			String containerDesc = (String) result.getData("CONTAINER_DESC", 0);
			inparm.setData("DISPENSE_NO", dispense_no);
			inparm.setData("DISPENSE_SEQ_NO", count);
			inparm.setData("CONTAINER_DESC", containerDesc);
			inparm.setData("ORDER_CODE", orderCode);
			inparm.setData("BOX_ESL_ID", "");
			inparm.setData("IS_BOXED", "N");
			inparm.setData("BOXED_USER", "");
			inparm.setData("IS_CRITICAL", "Y");
			inparm.setData("CABINET_ID", cabinetId);
			inparm.setData("BOXED_DATE", SystemTool.getInstance().getDate());
			inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			result = SPCPoisonTool.getInstance().insertToxicmJj(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			map.put(containerId, count);
			count++;
		}
		for (int i = 0; i < list.size(); i++) {
			List containerList = (List) list.get(i);
			String containerId = (String) containerList.get(6);
			TParm toxicdParm = new TParm();
			toxicdParm.setData("DISPENSE_NO", dispense_no);
			toxicdParm.setData("DISPENSE_SEQ_NO", map.get(containerId));
			toxicdParm.setData("CONTAINER_ID", containerId);
			toxicdParm.setData("TOXIC_ID", containerList.get(0));
			toxicdParm.setData("ORDER_CODE", containerList.get(7));
			toxicdParm.setData("BATCH_NO", containerList.get(3));
			toxicdParm.setData("VALID_DATE", containerList.get(4));
			toxicdParm.setData("VERIFYIN_PRICE", containerList.get(9));
			toxicdParm.setData("BATCH_SEQ", containerList.get(8));
			toxicdParm.setData("UNIT_CODE", containerList.get(10));		
			toxicdParm.setData("IS_PACK", "N");
			toxicdParm.setData("IS_CRITICAL", "Y");
			toxicdParm.setData("CABINET_ID", cabinetId);				
			toxicdParm.setData("OPT_USER", parm.getData("OPT_USER"));
			toxicdParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			toxicdParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		    result = SPCPoisonTool.getInstance().insertToxicdJj(
					toxicdParm, conn);      
			if (result.getErrCode() < 0) {
				return result;
			}
			result = SPCContainerTool.getInstance().deleteByToxic(toxicdParm,conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

}
