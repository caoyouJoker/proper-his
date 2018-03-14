package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: 特殊药品分类</p>
 *
 * <p>Description: 处理有关特殊药品分类的全部数据处理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author JiaoY 2008-09-2
 * @version 1.0
 */

public class SYSCtrlDrugClassTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSCtrlDrugClassTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static SYSCtrlDrugClassTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSCtrlDrugClassTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSCtrlDrugClassTool() {
        setModuleName("sys\\SYSCtrlDrugClassModule.x");
        onInit();
    }

    /**
     * 查询数据
     * @param ctrldugclasscode String
     * @return TParm
     */
    public TParm selectdata(String ctrldugclasscode) {
        TParm parm = new TParm();

        if (ctrldugclasscode != null && ctrldugclasscode.length() > 0)
            parm.setData("CTRLDRUGCLASS_CODE", ctrldugclasscode);
        TParm result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 验证SYS_CTRLDRUGCLASS表中是否存在CTRLDRUGCLASS_CODE
     * @param parm TParm
     * @return boolean true 有数据 false 没有数据
     */
    public TParm existsData(TParm parm) {
//       System.out.println("验证begin");
        TParm result = query("existsCTRLDRUGCLASS_CODE", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("验证end--》"+result);
        return result;

    }

    /**
     * 验证pha_base表中是否存在CTRLDRUGCLASS_CODE，若有则不能删除
     * @param parm TParm
     * @return TParm
     */
    public TParm existsPHA(TParm parm) {
        TParm result = query("existsPHA", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }


    /**
     * 新增一条数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm) {
//       System.out.println("新增begin");
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 更新一条数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = new TParm();
        result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * 删除一条数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteData(TParm parm) {
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * 判断药品是否为毒麻药
     * @param order_code String
     * @return boolean
     */
    public boolean getOrderCtrFlg(String order_code) {
        String sql = SYSSQL.getOrderCtrFlg(order_code);
        //System.out.println("getOrderCtrFlg.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        if (result.getCount("CTRLDRUGCLASS_CODE") < 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断毒麻药的使用量是否超过最大限
     * @param order_code String
     * @param qty double
     * @return boolean
     */
    public boolean getCtrOrderMaxDosage(String order_code, double qty) {
        TParm result = new TParm(TJDODBTool.getInstance().select(SYSSQL.
            getPhaTransUnit(order_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        if (result.getCount("MEDI_QTY") < 0) {
            return false;
        }
        if (qty / result.getDouble("MEDI_QTY", 0) > 1) {
            return false;
        }
        return true;
    }

}
