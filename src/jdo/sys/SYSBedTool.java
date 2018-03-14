package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title:
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
 * Company:JavaHis
 * </p>
 * 
 * @author JiaoY
 * @version 1.0
 */
public class SYSBedTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SYSBedTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static SYSBedTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSBedTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSBedTool() {
		setModuleName("sys\\SYSBedModule.x");
		onInit();
	}

	/**
	 * 查询全字段
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryAll(TParm parm) {
		// System.out.println("查询全字段");
		TParm result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 更新床位
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm upDate(TParm parm, TConnection conn) {
		// System.out.println("<-------------更新床位-------------------->");
		TParm result = update("updateForAdm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 检索床位，解决抢床问题     chenxi
	 * @param parm
	 * @param conn
	 * @return
	 */
   public TParm selectBed(TParm parm ,TConnection conn){
		TParm result = query("selectBed", parm, conn);
		if (result.getErrCode()<0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
   }
	/**
	 * 预约修改预约标记位
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm upDateForResv(TParm parm, TConnection conn) {
		TParm result = update("updateForResv", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 包床管理
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm alloSysbed(TParm parm) {
		TParm result = update("alloSysbed", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * 查询床位号所在的病房的说有病床
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryRoomBed(TParm parm) {
		// System.out.println("查询床位号所在的病房的说有病床");
		TParm result = query("queryRoomBed", parm);
		// System.out.println("查询床位号所在的病房的说有病床result="+result);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 入住前把此病的其他床位的预约清除前先清床（住院处安排床位）
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm clearForadmin(TParm parm, TConnection conn) {
		// System.out.println("清床");
		TParm result = update("clearForadmin", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 入住前把此病的其他床位的预约清除前先清床（住院处安排床位）
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataOccu(TParm parm, TConnection conn) {
		// System.out.println("清床");
		TParm result = update("upDataOccu", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 入住前把此病的其他床位的预约清除前先清床（住院处安排床位）
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm clearAllForadmin(TParm parm, TConnection conn) {
		// System.out.println("清床");
		TParm result = update("clearForAlladmin", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}
	//==========add  by  chenxi 20130222==============
	/**
	 * 改变锁床标记字段
	 */
	public TParm changeLockBed(TParm parm,TConnection conn){
		
		TParm result = this.selectLockBed(parm, conn) ;
		if (result.getCount()>=1) {
			err("ERR:此床被他人处理中，请选择其他空床");
			result.setData("Err", "Code", "-1") ;
			result.setData("Err", "Name", "此床被他人处理中，请选择其他空床") ;
			return result;
		}
		result = this.updateLockBed(parm, conn) ;
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result ;
	}
	/**
	 * 查询此床是否被锁
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm  selectLockBed(TParm parm,TConnection conn){
		TParm result = query("selectLockBed", parm, conn);
		if (result.getErrCode()<0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 更新锁床字段
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateLockBed(TParm parm,TConnection conn){
		TParm result = update("updateLockBed", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	//==========add  by  chenxi 20130222==============

	/**
	 * 转床
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm changBed(TParm parm, TConnection conn) {
		// 清床
		TParm clearBed = new TParm();
		clearBed.setRowData(parm);
		TParm result = this.clearAllForadmin(clearBed, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//===========================   chenxi 解决抢床问题
		//床位检索
		TParm selectBed =  new TParm()  ;
		selectBed.setRowData(parm) ;
		result =  this.selectBed(selectBed, conn) ;
		if (result.getCount()>=1) {
			err("ERR:此床已被抢，请选择其他空床");
			result.setData("Err", "Code", "-1") ;
			result.setData("Err", "Name", "此床已被抢，请选择其他空床") ;
			return result;
		}
		//==========================   chenxi  解决抢床问题
		// 转床

		TParm changeBed = new TParm();
		changeBed.setRowData(parm);

		result = this.upDate(changeBed, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询红色警戒 黄色警戒
	 * 
	 * @return TParm
	 */
	public TParm queryBedYellowRed(TParm parm) {
		TParm result = query("queryYellowRed", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 检核病患是否在床
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm checkInBed(TParm parm) {
		TParm result = new TParm();
		result = query("checkBedStatus", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 出科清床 （根据CASE_NO清空床位）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm clearForAdm(TParm parm, TConnection conn) {
		TParm result = this.update("clearForAdm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 取消包床
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm clearOCCUBed(TParm parm) {
		TParm result = this.update("clearOCCUBed", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 是否是ICU
	 * @param parm
	 * @return
	 */
	public boolean checkIsICU(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean icuFlg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query("IsICUBed", inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        icuFlg=result.getBoolean("ICU_FLG",0);
		return icuFlg;
	}
	
	/**
	 * 是否是CCU
	 * @param parm
	 * @return
	 */
	public boolean checkIsCCU(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean ccuFlg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query("IsCCUBed", inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        ccuFlg=result.getBoolean("CCU_FLG",0);
		return ccuFlg;
	}
}
