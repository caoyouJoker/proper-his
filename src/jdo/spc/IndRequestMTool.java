package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
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
        setModuleName("spc\\INDRequestMModule.x");
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
     * 查询申请主档
     *
     * @param parm
     * @return
     */
    public TParm onQueryAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("querySPCRequestM", parm);
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
    
    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsertAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("createNewSPCRequestM", parm, conn);
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
     * 更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdateAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("updateSPCRequestM", parm, conn);
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
     * 删除
     *
     * @param parm
     * @return
     */
    public TParm onDeleteAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("deleteSPCRequestM", parm, conn);
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
     * 查询所需出库作业的单据
     *
     * @param parm
     * @return
     */
    public TParm onQueryOutReqNoAcnt(TParm parm) {//wanglong add 20150202
        
        TParm result = this.query("queryOutReqNoAcnt", parm);
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
     * 查询其他入库方式的单据
     *
     * @param parm
     * @return
     */
    public TParm onQueryOtherInNoAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("queryOtherInNoAcnt", parm);
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
     * 要库请领接口
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertAll(TParm parm, TConnection conn)
    {
      TParm parmM = parm.getParm("OUT_M");
      TParm result = update("createNewRequestM", parmM, conn);
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + 
          result.getErrName());
        return result;
      }
      TParm parmD = parm.getParm("OUT_D");
      int count = parmD.getCount();
      for (int i = 0; i < count; ++i) {
        TParm insertParm = new TParm();
        insertParm.setData("REQUEST_NO", parmD.getData("REQUEST_NO", i));
        insertParm.setData("SEQ_NO", parmD.getData("SEQ_NO", i));
        insertParm.setData("ORDER_CODE", parmD.getData("ORDER_CODE", i));
        insertParm.setData("BATCH_NO", parmD.getData("BATCH_NO", i));
        insertParm.setData("VALID_DATE", parmD.getData("VALID_DATE", i));
        insertParm.setData("QTY", parmD.getData("QTY", i));
        insertParm.setData("UNIT_CODE", parmD.getData("UNIT_CODE", i));
        insertParm.setData("RETAIL_PRICE", parmD.getData("RETAIL_PRICE", i));
        insertParm.setData("STOCK_PRICE", parmD.getData("STOCK_PRICE", i));
        insertParm.setData("ACTUAL_QTY", parmD.getData("ACTUAL_QTY", i));
        insertParm.setData("UPDATE_FLG", parmD.getData("UPDATE_FLG", i));
        insertParm.setData("OPT_USER", parmD.getData("OPT_USER", i));
        insertParm.setData("OPT_DATE", parmD.getData("OPT_DATE", i));
        insertParm.setData("OPT_TERM", parmD.getData("OPT_TERM", i));
        insertParm.setData("VERIFYIN_PRICE", parmD.getData("VERIFYIN_PRICE", i));
        insertParm.setData("BATCH_SEQ", parmD.getData("BATCH_SEQ", i));
        result = IndRequestDTool.getInstance().onInsert(insertParm, conn);
        if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() + 
            result.getErrName());
          return result;
        }
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
    
    public TParm onQueryRequestMFinish(TParm parm) {
        TParm result = new TParm();
        String requestNo = parm.getValue("REQUEST_NO");
        String reqType = parm.getValue("REQTYPE_CODE");
        String startDate = parm.getValue("START_DATE");
        String appOrgCode = parm.getValue("APP_ORG_CODE");
        String endDate = parm.getValue("END_DATE");
        String regionCode = parm.getValue("REGION_CODE");
        String drugCategory = parm.getValue("DRUG_CATEGORY");

        String sql = "   SELECT * FROM IND_REQUESTM A     WHERE  A.REQUEST_NO IN ( SELECT  B.REQUEST_NO FROM IND_REQUESTD B WHERE A.REQUEST_NO=B.REQUEST_NO AND B.UPDATE_FLG='3' ) ";

        if ((requestNo != null) && (!requestNo.equals(""))) {
          sql = sql + " AND A.REQUEST_NO='" + requestNo + "' ";
        }

        if ((reqType != null) && (!reqType.equals(""))) {
          sql = sql + " AND A.REQTYPE_CODE='" + reqType + "' ";
        }

        if ((appOrgCode != null) && (!appOrgCode.equals(""))) {
          sql = sql + " AND A.APP_ORG_CODE='" + appOrgCode + "' ";
        }

        if ((startDate != null) && (!startDate.equals(""))) {
          startDate = startDate.substring(0, startDate.length() - 2);
          sql = sql + " AND A.REQUEST_DATE>=TO_DATE( '" + startDate + "' ,'YYYY-MM-DD HH24:MI:SS' ) ";
        }

        if ((endDate != null) && (!endDate.equals(""))) {
          endDate = endDate.substring(0, endDate.length() - 2);
          sql = sql + " AND A.REQUEST_DATE<=TO_DATE( '" + endDate + "' ,'YYYY-MM-DD HH24:MI:SS' ) ";
        }

        if ((regionCode != null) && (!regionCode.equals(""))) {
          sql = sql + " AND A.REGION_CODE='" + regionCode + "' ";
        }

        if ((drugCategory != null) && (!drugCategory.equals(""))) {
          sql = sql + " AND A.DRUG_CATEGORY='" + drugCategory + "' ";
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() + 
            result.getErrName());
          return result;
        }
        return result;
      }

      public TParm onQueryRequestDFinish(TParm parm)
      {
        TParm result = new TParm();
        String requestNo = parm.getValue("REQUEST_NO");
        String reqType = parm.getValue("REQTYPE_CODE");
        String startDate = parm.getValue("START_DATE");
        String appOrgCode = parm.getValue("APP_ORG_CODE");
        String endDate = parm.getValue("END_DATE");
        String regionCode = parm.getValue("REGION_CODE");
        String drugCategory = parm.getValue("DRUG_CATEGORY");
        String sql = " SELECT B.ORDER_DESC,B.SPECIFICATION,A.QTY,A.ACTUAL_QTY,A.UNIT_CODE,         A.VERIFYIN_PRICE AS STOCK_PRICE,A.RETAIL_PRICE AS RETAIL_PRICE,A.BATCH_NO,A.VALID_DATE,B.PHA_TYPE,        A.ORDER_CODE,C.STOCK_QTY,C.DOSAGE_QTY,B.TRADE_PRICE,A.SEQ_NO,        D.UNIT_CHN_DESC,F.MATERIAL_LOC_CODE,F.ELETAG_CODE    FROM IND_REQUESTD A,PHA_BASE B,PHA_TRANSUNIT C,SYS_UNIT D,      IND_REQUESTM E,IND_STOCKM F    WHERE  A.ORDER_CODE = B.ORDER_CODE \t       AND A.ORDER_CODE = C.ORDER_CODE \t       AND A.UNIT_CODE = D.UNIT_CODE(+) \t       AND E.TO_ORG_CODE = F.ORG_CODE  \t       AND A.ORDER_CODE = F.ORDER_CODE         AND A.REQUEST_NO = E.REQUEST_NO ";

        if ((requestNo != null) && (!requestNo.equals(""))) {
          sql = sql + "  AND A.REQUEST_NO = '" + requestNo + "'";
        }
        if ((reqType != null) && (!reqType.equals(""))) {
          sql = sql + " AND E.REQTYPE_CODE='" + reqType + "' ";
        }

        if ((appOrgCode != null) && (!appOrgCode.equals(""))) {
          sql = sql + " AND E.APP_ORG_CODE='" + appOrgCode + "' ";
        }

        if ((startDate != null) && (!startDate.equals(""))) {
          startDate = startDate.substring(0, startDate.length() - 2);
          sql = sql + " AND E.REQUEST_DATE>=TO_DATE( '" + startDate + "' ,'YYYY-MM-DD HH24:MI:SS' ) ";
        }

        if ((endDate != null) && (!endDate.equals(""))) {
          endDate = endDate.substring(0, endDate.length() - 2);
          sql = sql + " AND E.REQUEST_DATE<=TO_DATE( '" + endDate + "' ,'YYYY-MM-DD HH24:MI:SS' ) ";
        }

        if ((regionCode != null) && (!regionCode.equals(""))) {
          sql = sql + " AND E.REGION_CODE='" + regionCode + "' ";
        }

        if ((drugCategory != null) && (!drugCategory.equals(""))) {
          sql = sql + " AND E.DRUG_CATEGORY='" + drugCategory + "' ";
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() + 
            result.getErrName());
          return result;
        }
        return result;
      }

}
