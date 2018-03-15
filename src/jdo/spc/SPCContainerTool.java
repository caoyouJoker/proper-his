package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 容器
 * </p>
 * 
 * <p>
 * Description:TODO
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class SPCContainerTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static SPCContainerTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SPCContainerTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCContainerTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCContainerTool() {
		setModuleName("spc\\SPCContainerModule.x");
		onInit();
	}

	/**
	 * 查询容器信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm tparm) {
		TParm result = new TParm();
		result = query("queryInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 添加容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInfo(TParm parm) {
		TParm result = new TParm();
		result = update("insertInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 更新容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInfo(TParm parm) {
		TParm result = new TParm();
		result = update("updateInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 删除容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteInfo(TParm parm) {
		TParm result = new TParm();
		result = update("deleteInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 查询容器明细
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryMxInfo(TParm tparm) {
		TParm result = new TParm();
		result = query("queryMxInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据麻禁药流水号查询药品编号
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryMXByToxicId(TParm tparm) {
		TParm result = new TParm();
		result = query("queryMXByToxicId", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新麻禁药容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOrderCode(TParm parm) {
		TParm result = new TParm();
		result = update("updateOrderCode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 查看是否为麻禁药
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryISMj(TParm tparm) {
		TParm result = new TParm();
		result = query("queryISMj", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据麻精流水号＼药品代码查询容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {

		// 麻精流水号
		String toxicId = parm.getValue("TOXIC_ID");
		// 药品CODE
		String orderCode = parm.getValue("ORDER_CODE");
		// 智能柜
		String cabinetId = parm.getValue("CABINET_ID");

		if (StringUtil.isNullString(toxicId)
				|| StringUtil.isNullString(orderCode)) {
			return new TParm();
		}

		String sql = " SELECT A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO, "
				+ " 		  B.VALID_DATE,B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE , "
				+ "           B.SUP_ORDER_CODE,B.SUP_CODE,B.INVENT_PRICE,B.RETAIL_PRICE "
				+ " FROM IND_CONTAINERM A,IND_CONTAINERD B "   
				+ " WHERE  A.CONTAINER_ID=B.CONTAINER_ID  " 
				+ "  AND  B.TOXIC_ID='"
				+ toxicId
				+ "' AND B.CABINET_ID='"
				+ cabinetId
				+ "' AND B.ORDER_CODE='"
				+ orderCode
				+ "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 根据容器查询对应的容量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryBy(TParm parm) {

		String containerId = parm.getValue("CONTAINER_ID");
		if (StringUtil.isNullString(containerId)) {
			return new TParm();
		}
		String sql = " SELECT A.CONTAINER_ID,A.CONTAINER_DESC,A.TOXIC_QTY,A.ORDER_CODE "
				+ " FROM IND_CONTAINERM A "
				+ " WHERE A.CONTAINER_ID='"
				+ containerId + "' ";

		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 整箱出:查询容器已有的麻精
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryByContainerId(TParm parm) {
		String containerId = parm.getValue("CONTAINER_ID");
		String cabinetId = parm.getValue("CABINET_ID");
		if (StringUtil.isNullString(containerId)) {
			return new TParm();
		}
		String sql = " SELECT A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO, B.BATCH_NO, "
				+ " 		  B.VALID_DATE,B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE  "
				+ " FROM IND_CONTAINERM A,IND_CONTAINERD B "
				+ " WHERE  A.CONTAINER_ID=B.CONTAINER_ID "
				+ "     AND A.CONTAINER_ID='"
				+ containerId
				+ "' AND B.CABINET_ID='" + cabinetId + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 更新
	 */
	public TParm onUpdate(TParm parm, TConnection conn) {
		String containerId = parm.getValue("CONTAINER_ID");
		String toxicId = parm.getValue("TOXIC_ID");

		if ((containerId == null || containerId.equals(""))
				|| (toxicId == null || toxicId.equals(""))) {
			return new TParm();
		}

		// modify by wangbin 2015/1/26 内部需求 #792 泰心物联网系统调整方案 : 病区管理 > 临时医嘱出智能柜 相关调整 START
		String sql = " UPDATE IND_CONTAINERD SET CABINET_ID=NULL ,CONTAINER_ID=NULL WHERE TOXIC_ID='"
				+ toxicId + "' ";
		// modify by wangbin 2015/1/26 内部需求 #792 泰心物联网系统调整方案 : 病区管理 > 临时医嘱出智能柜 相关调整 END

		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * 更新CONTAINERM表
	 */
	public TParm onUpdateM(TParm parm, TConnection conn) {
		String containerId = parm.getValue("CONTAINER_ID");

		if ((containerId == null || containerId.equals(""))) {
			return new TParm();
		}

		String sql = " UPDATE IND_CONTAINERM SET CABINET_ID=NULL  "
				+ " WHERE CONTAINER_ID='" + containerId + "' ";
//		System.out.println("onUpdateM-------:" + sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * 更新IND_CONTAINERM表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateM(TParm parm) {
		String sql = "UPDATE IND_CONTAINERM A SET A.CABINET_ID=''  "
				+ "     WHERE (SELECT COUNT(TOXIC_ID) FROM IND_CONTAINERD B WHERE B.CONTAINER_ID=A.CONTAINER_ID)<=0";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	public TParm onQuerySysParm() {
		String sql = "SELECT TOXIC_LENGTH FROM IND_SYSPARM ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm != null && parm.getCount() > 0) {
			return parm.getRow(0);
		}
		return new TParm();
	}

	/**
	 * 查询麻精药
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryJjMj(TParm tparm) {
		TParm result = new TParm();
		result = query("queryJjMj", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据容器查询麻精药
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryJjContainer(TParm tparm) {
		TParm result = new TParm();
		result = query("queryJjContainer", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询麻精条码长度
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryBarCodeSize() {
		String sql = "SELECT TOXIC_LENGTH FROM IND_SYSPARM";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 盘点智能柜药品
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryToxicCheck(TParm parm) {
		String containerId = parm.getValue("CONTAINER_ID");
		String cabinetId = parm.getValue("CABINET_ID");
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC,"
				+ "COUNT(A.CONTAINER_ID) AS CONTAINER_QTY,SUM(A.TOT_QTY) AS TOT_QTY,"
				+ "A.SPECIFICATION,0 AS CHECKNUM FROM( SELECT B.ORDER_CODE,D.ORDER_DESC,B.CONTAINER_ID,"
				+ "C.UNIT_CHN_DESC,COUNT(B.CONTAINER_ID) AS TOT_QTY,D.SPECIFICATION  AS SPECIFICATION"
				+ " FROM IND_CONTAINERD B ,SYS_UNIT C, PHA_BASE D WHERE  CABINET_ID='"
				+ cabinetId
				+ "' "
				+ "AND CONTAINER_ID IN("
				+ containerId
				+ ") AND B.ORDER_CODE=D.ORDER_CODE  "
				+ "AND B.UNIT_CODE=C.UNIT_CODE GROUP BY B.ORDER_CODE,D.ORDER_DESC,CONTAINER_ID,C.UNIT_CHN_DESC ,D.SPECIFICATION) A GROUP BY  A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC ,A.SPECIFICATION";

		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 删除麻精药
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteByToxic(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("deleteByToxic", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 查询紧急抢救药品
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryToxicD(TParm tparm) {
		TParm result = new TParm();
		result = query("queryToxicD", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据ORDER_CODE查询智能柜实际库存数
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm queryCabinetQty(TParm tparm) {
		String cabinetId = tparm.getValue("CABINET_ID");
		String orderCode = tparm.getValue("ORDER_CODE");
		String sql = "SELECT COUNT(*) AS NUM FROM IND_CONTAINERD WHERE ORDER_CODE='"
				+ orderCode + "' AND CABINET_ID='" + cabinetId + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 清空容器
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm resetContainer(TParm tparm) {
		String containerID = tparm.getValue("CONTAINER_ID");
		String sql = "UPDATE IND_CONTAINERD SET CONTAINER_ID=NULL,CABINET_ID=NULL WHERE CONTAINER_ID='"
				+ containerID + "'";		
		return new TParm(TJDODBTool.getInstance().update(sql));		
	}
	
	/**
	 * 查看药品对应的供应商编码
	 * 
	 * @param parm
	 * @return
	 */
	public TParm querySupOrderCode(String orderCode) {
		// add by wangbin 20140721
		// 药品编码
		String sql = "SELECT T.SUP_ORDER_CODE FROM IND_CODE_MAP T WHERE T.ORDER_CODE = '"
				+ orderCode + "' ORDER BY T.SUP_ORDER_CODE DESC";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
		}
		
		return result;	
	}
}
