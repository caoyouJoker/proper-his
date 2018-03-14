/**
 * @className INDTool.java 
 * @author litong
 * @Date 2013-3-18 
 * @version V 1.0 
 */
package jdo.udd;

//import jdo.ekt.EKTTool;

import java.util.ArrayList;
import java.util.List;

import action.udd.client.SpcIndStock;
import action.udd.client.SpcOdiDspnms;
import action.udd.client.SpcOdiService_SpcOdiServiceImplPort_Client;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * @author litong
 * @Date 2013-3-18
 */
public class UDDTool extends TJDOTool {

	/**
	 * 构造器
	 */
	private UDDTool() {
		// 加载Module文件
		this.setModuleName("UDD\\UDDSellOrder.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static UDDTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static UDDTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new UDDTool();
		}
		return instanceObject;
	}

	/**
	 * 查询抗生素销售数据
	 * 
	 * @return TParm
	 */
	public TParm getSellOrder(TParm parm) {
		TParm result = query("getSellOrder", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询抗生素销售数据
	 * 
	 * @return TParm
	 */
	public TParm getSellOrder6(TParm parm) {
		TParm result = query("getSellOrder6", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm getAllOrder(TParm parm) {
		TParm result = query("getAllOrder", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm getAllOrder3(TParm parm) {
		TParm result = query("getAllOrder3", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm getSellOrder1(TParm parm) {
		TParm result = query("getSellOrder1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm getSellOrder3(TParm parm) {
		TParm result = query("getSellOrder3", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm getAllOrder1(TParm parm) {
		TParm result = query("getAllOrder1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm getAllOrder6(TParm parm) {
		TParm result = query("getAllOrder6", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm getSellOrderOPD(TParm parm) {
		TParm result = query("getSellOrderOPD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderODI(TParm parm) {
		TParm result = query("getSellOrderODI", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderOPD(TParm parm) {
		TParm result = query("getAllOrderOPD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderODI(TParm parm) {
		TParm result = query("getAllOrderODI", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderOPD1(TParm parm) {
		TParm result = query("getSellOrderOPD1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderOPD1(TParm parm) {
		TParm result = query("getAllOrderOPD1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderODI1(TParm parm) {
		TParm result = query("getSellOrderODI1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderODI1(TParm parm) {
		TParm result = query("getAllOrderODI1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * add caoyong 门诊
	 * 20140210
	 * @param parm
	 * @return
	 */
	public TParm getSellOrderO(TParm parm) {
		TParm result = query("getSellOrderO", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * add caoyong 急诊
	 * 20140210
	 * @param parm
	 * @return
	 */
	public TParm getSellOrderE(TParm parm) {
		TParm result = query("getSellOrderE", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	public TParm getAllOrderO(TParm parm) {
		TParm result = query("getAllOrderO", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm getAllOrderE(TParm parm) {
		TParm result = query("getAllOrderE", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderE1(TParm parm) {
		TParm result = query("getAllOrderE1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderO1(TParm parm) {
		TParm result = query("getAllOrderO1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderO1(TParm parm) {
		TParm result = query("getSellOrderO1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderE1(TParm parm) {
		TParm result = query("getSellOrderE1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 
	* @Title: getBatchNo
	* @Description: TODO(查询物联网药品批号，住院配药操作使用)
	* @author pangben
	* @param orderCode
	* @param execDeptCode
	* @return
	* @throws
	 */
	public String getBatchNo(String orderCode,String execDeptCode){
		SpcOdiDspnms Dspnmss = new SpcOdiDspnms();	
		List<SpcIndStock>  StockLists = new ArrayList<SpcIndStock>();
		SpcIndStock sis = new SpcIndStock();
		sis.setOrderCode(orderCode);
		sis.setOrgCode(execDeptCode);
		StockLists.add(sis);
		Dspnmss.setSpcIndStocks(StockLists);
		//fux modify 20150508 修改为取得批号方法
		String batchNo = SpcOdiService_SpcOdiServiceImplPort_Client
		.onQueryBatch(Dspnmss);
		return batchNo;
	}
}