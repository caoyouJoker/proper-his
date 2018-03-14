package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 申请单主档Tool
 * </p>
 *
 * <p>
 * Description: 申请单主档Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.24
 * @version 1.0
 */
public class IndRequestMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndRequestMTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndStockMTool
     */
    public static IndRequestMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndRequestMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndRequestMTool() {
        setModuleName("ind\\INDRequestMModule.x");
        onInit();
    }

    /**
     * 查询申请主档
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryRequestM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createNewRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    //==============================盒装包药机
    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onBoxInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createBoxRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询所需出库作业的单据
     *
     * @param parm
     * @return
     */
    public TParm onQueryOutReqNo(TParm parm) {
        TParm result = this.query("queryOutReqNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询其他入库方式的单据
     *
     * @param parm
     * @return
     */
    public TParm onQueryOtherInNo(TParm parm) {
        TParm result = this.query("queryOtherInNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 科室备药生成汇总查询(住院)
     *
     * @param parm
     * @return
     */
    public TParm onQueryIBSDeptExmM(TParm parm) {
        TParm result = this.query("queryIBSDeptExmM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 科室备药生成明细查询(住院)
     *
     * @param parm
     * @return
     */
    public TParm onQueryIBSDeptExmD(TParm parm) {
        TParm result = this.query("queryIBSDeptExmD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 科室备药生成明细查询(住院)已完成 
     *
     * 2012-04-27 
     * @param parm
     * @return
     */
    public TParm onQueryIBSDeptExmDFinish(TParm parm) {
    	TParm result = this.query("queryIBSDeptExmDFinish", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }

    /**
     * 科室备药生成汇总查询(门急诊)
     *
     * @param parm
     * @return
     */
    public TParm onQueryOPDDeptExmM(TParm parm) {
        TParm result = this.query("queryOPDDeptExmM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 科室备药生成明细查询(门急诊)
     *
     * @param parm
     * @return
     */
    public TParm onQueryOPDDeptExmD(TParm parm) {
        TParm result = this.query("queryOPDDeptExmD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 科室备药生成汇总查询(完成的)
     *
     * @param parm
     * @return
     */
    public TParm onQueryExm(TParm parm) {
        TParm result = this.query("onQueryExm", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 科室备药生成汇总查询(病区-住院)-未申请
     *
     * @param parm
     * @return
     */
    public TParm queryOdiDsnpmExmM(TParm parm) {
        TParm result = this.query("queryOdiDsnpmExmM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 科室备药生成明细查询(病区-住院)-未申请
     *
     * @param parm
     * @return
     */
    public TParm queryOdiDspnmExmD(TParm parm) {
        TParm result = this.query("queryOdiDspnmExmD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 科室备药生成明细查询(病区-住院)-已经申请
     *
     * @param parm
     * @return
     */
    public TParm queryOdiDspnmExmEd(TParm parm) {
        TParm result = this.query("queryOdiDspnmExmEd", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }    
}
