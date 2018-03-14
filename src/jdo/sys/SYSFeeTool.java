package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class SYSFeeTool
    extends TJDOTool {
    /**
     * 实例
     */
    private static SYSFeeTool instanceObject;

    /**
     * 得到实例
     *
     * @return PatTool
     */
    public static SYSFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFeeTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSFeeTool() {
        setModuleName("sys\\SYSFeeModule.x");
        onInit();
    }
    /**
     * 根据医令代码查询费用
     * @param orderCode String
     * @return TParm
     */
    public TParm getFee(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getFee", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据医令代码查询院内费用代码
     *
     * @param orderCode
     *            String
     * @return TParm
     */
    public TParm getChargHospCode(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getChargHospCode", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据医嘱代码查询费用信息
     *
     * @param orderCode
     *            String
     * @return TParm ownPrice,nhiPrice
     */
    public TParm getFeeData(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getFeeData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 根据医嘱代码查询自费价、医保价与政府最高价
     * @param orderCode String
     * @return TParm
     */
    public TParm getPrice(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getPrice", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 新增
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据医令代码查询费用
     * @param orderCode String
     * @return TParm
     */
    public TParm getFeeOldPrice(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getOldPrice", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 得到Cat1Code
     * @param orderCode String
     * @return TParm
     */
    public TParm getCat1Code(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getCat1Code", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 通过orderCode得到医嘱信息
     * @param orderCode String
     * @return TParm
     */
    public TParm getFeeAllData(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getFeeAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 是否为规定药品类别
     * @param phaType String
     * @param sysFeeType String
     * @return boolean
     */
    public boolean getType(String phaType, String sysFeeType) {
        if (sysFeeType.contains(phaType))
            return true;
        return false;
    }
   /**
     * 通过orderCode得到医嘱信息
     * @param orderCode String
     * @return TParm
     */
    public TParm getCtrFlg(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getCtrFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 通过orderCode得到医嘱信息
     *
     * @param orderCode String
     * @return TParm
     */
    public TParm onUpdateCtrflg(TParm inparm) {
        if (inparm.getErrCode() < 0) {
            err(inparm.getErrCode() + " " + inparm.getErrText());
            return inparm;
        }
        TParm result = update("updateCtrflg", inparm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 医保三目字典修改药品医保对应
     * @param parm
     * @return
     * ==============pangben 2011-12-16
     */
    public TParm updateINSToSysFee(TParm parm, TConnection conn){
    	 TParm result = update("updateINSToSysFee", parm,conn);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
    }
    /**
     * 住院费用分割
     * 判断order是否存在sys_Fee
     * @param parm
     * @return
     */
    public TParm querySysFeeIns(TParm parm){
    	TParm result = query("querySysFeeIns", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
	/**
	 * 查询特殊检查医嘱
	 * 
	 * @param parm
	 * @return result
	 * @author wangb 2017/5/4
	 */
	public TParm querySEOrder() {
		String sql = "SELECT * FROM SYS_FEE WHERE REMARK_1 = 'SE'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			return result;
		}
		return result;
	}
}
