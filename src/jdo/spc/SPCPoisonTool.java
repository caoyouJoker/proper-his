package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:麻精药符码打包
 * </p>
 * 
 * <p>
 * Description:麻精药符码打包
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
public class SPCPoisonTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SPCPoisonTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCPoisonTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCPoisonTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCPoisonTool() {
		setModuleName("spc\\SPCPoisonBaleModule.x");
		onInit();
	}

	/**
	 * 查询验收入库信息
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
	 * 查询验收入库信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfoVerifyin(TParm tparm) {
		TParm result = new TParm();
		result = query("queryInfoVerifyin", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询容器数量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainer(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainer", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询容器数量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainerNum(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainerNum", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询容器类型
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryOrgType() {
		TParm result = new TParm();
		result = query("queryOrgType");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询容器数量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainerCount(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainerCount", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 药品打包
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertContainer(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertContainer", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询fid
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryRfid(TParm tparm) {
		TParm result = new TParm();
		result = query("queryRfid", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新容器状态
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainer(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainer", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询已打包容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainerOrder(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainerOrder", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新容器状态
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerD(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainerD", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新容器状态
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOldContainerM(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateOldContainerM", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新容器已打包数量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerNum(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainerNum", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新容器已打包数量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerNumVerifyin(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainerNumVerifyin", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 添加交易表
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm insertToxicm(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicm", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 添加交易表
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm insertToxicmJj(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicmJj", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	
	/**
	 * 添加交易表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertToxicd(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicd", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询已打包容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryYdb(TParm tparm) {
		TParm result = new TParm();
		result = query("queryYdb", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新交易表
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm updateToxicd(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateToxicd", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询已打包容器
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateToxicm(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateToxicm", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 添加交易表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertToxicdJj(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicdJj", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 解除麻精盒子与智能柜的绑定关系
	 * 
	 * @param parm
	 * @return
	 * @author wangbin 2014/11/17
	 */
	public TParm deleteContainerWithCabinet(TParm parm) {
		String sql = "UPDATE IND_CONTAINERM SET CABINET_ID = NULL WHERE CONTAINER_ID = '"
				+ parm.getValue("CONTAINER_ID") + "'";
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(sql.toString()));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 解除麻精盒子与麻精药品的绑定关系
	 * 
	 * @param parm
	 * @return
	 * @author wangbin 2014/11/17
	 */
	public TParm deleteContainerWithToxic(TParm parm) {
		String sql = "UPDATE IND_CONTAINERD SET CONTAINER_ID = NULL,CABINET_ID = NULL WHERE CONTAINER_ID = '"
			+ parm.getValue("CONTAINER_ID") + "'";
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(sql.toString()));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
}
