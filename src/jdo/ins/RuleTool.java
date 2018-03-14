package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import jdo.sys.PositionTool;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: 支付标准控制类
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis
 *
 * @author wangl 2008.08.18
 * @version 1.0
 */
public class RuleTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static RuleTool instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static RuleTool getInstance() {
        if (instanceObject == null)
            instanceObject = new RuleTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public RuleTool() {
        setModuleName("ins\\INSRuleModule.x");

        onInit();
    }

    /**
     * 新增支付标准
     * @param nhiCompany String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
//        String nhiCompany = parm.getValue("NHI_COMPANY");
//        if(existsRule(nhiCompany)){
//            result.setErr(-1,"请款机关 "+nhiCompany+" 已经存在!");
//            return result ;
//        }
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新支付标准
     * @param nhiCompany String
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
     * 根据请款单位代码查询支付标准信息(右忽略)
     * @param nhiCompany String 请款单位代码
     * @return TParm
     */
    public TParm selectdata(String nhiCompany,String feeType,String ctzCode,double startRange) {
        TParm parm = new TParm();
        nhiCompany += "%";
        parm.setData("NHI_COMPANY", nhiCompany);
        feeType += "%";
        parm.setData("FEE_TYPE", feeType);
        ctzCode += "%";
        parm.setData("CTZ_CODE", ctzCode);
        parm.setData("START_RANGE", startRange);
        String name = "selectdata";
        if(startRange==0)
            name = "selectdata1";
        TParm result = query(name, parm);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
   /**
     * 删除选中支付标准
     * @param nhiCompany String
     * @return boolean
     */
    public TParm deletedata(String nhiCompany,String feeType,String ctzCode) {
        TParm parm = new TParm();
        parm.setData("NHI_COMPANY", nhiCompany);
        parm.setData("FEE_TYPE", feeType);
        parm.setData("CTZ_CODE", ctzCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 取得请款机关combo信息
     * @return TParm
     */
    public TParm getComanyCombo() {
        TParm parm = new TParm();
        return query("getComanyCombo", parm);
    }

    /**
     * 取得收费等级combo信息
     * @return TParm
     */
    public TParm getTypeCombo() {
        TParm parm = new TParm();
        return query("getTypeCombo", parm);
    }

    /**
     * 取得身份combo信息
     * @return TParm
     */

    public TParm getCtzCombo() {
        TParm parm = new TParm();
        return query("getCtzCombo", parm);

    }
    /**
     * 判断是否存在支付方式
     * @param nhiCompany String 支付方式
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsRule(String nhiCompany){
        TParm parm = new TParm();
        parm.setData("NHI_COMPANY",nhiCompany);
        return getResultInt(query("existsRule",parm),"COUNT") > 0;
    }

}
