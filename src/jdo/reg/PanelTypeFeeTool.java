package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:号别费用工具类 </p>
 *
 * <p>Description:号别费用工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class PanelTypeFeeTool extends TJDOTool{
    /**
     * 实例
     */
    public static PanelTypeFeeTool instanceObject;
    /**
     * 得到实例
     * @return PanelTypeFeeTool
     */
    public static PanelTypeFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelTypeFeeTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PanelTypeFeeTool() {
        setModuleName("reg\\REGPanelTypeFeeModule.x");
        onInit();
    }
    /**
     * 查询号别费用(Tree)
     * @param clinicTypeCode String 号别
     * @return TParm
     */
    public TParm queryTree(String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        TParm result = query("queryTree", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;

    }
    /**
     * 新增号别费用
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String admType = parm.getValue("ADM_TYPE");
        String clinicTypeCode = parm.getValue("CLINICTYPE_CODE");
        String orderCode = parm.getValue("ORDER_CODE");
        if(existsClinictypefee(admType,clinicTypeCode,orderCode)){
            result.setErr(-1,"号别费用"+" 已经存在!");
            return result ;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新号别费用
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据号别查询号别费用table信息
     * @param clinicTypeCode String
     * @return TParm
     */
    public TParm selectdata(String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("CLINICTYPE_CODE",clinicTypeCode);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据门急别,号别代码,费用代码查询号别费用
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除号别费用
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(String admType,String clinicTypeCode,String orderCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        parm.setData("ORDER_CODE", orderCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断是否存在号别费用
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return boolean
     */
    public boolean existsClinictypefee(String admType,String clinicTypeCode,String orderCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        parm.setData("ORDER_CODE", orderCode);
        return getResultInt(query("existsClinictypefee", parm), "COUNT") > 0;
    }
    /**
     * 得到ORDE_RCODE
     * @param admType String 门急别
     * @param clinicType String 号别
     * @return TParm
     */
    public TParm getOrderCode(String admType,String clinicType){
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicType);
        TParm result = query("getOrderCode", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
}
    /**
     * 根据admtype,CLINICTYPE_CODE查找ORDE_RCODE
     * @param admType String
     * @param clinicType String
     * @param receiptType String
     * @return TParm
     */
    public TParm getOrderCodeDetial(String admType,String clinicType,String receiptType){
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicType);
        parm.setData("RECEIPT_TYPE",receiptType);
        TParm result = query("getOrderCodeDetial", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
}

}
