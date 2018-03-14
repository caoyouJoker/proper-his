package jdo.bms;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 血液信息
 * </p>
 *
 * <p>
 * Description: 血液信息
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSBloodTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BMSBloodTool instanceObject;

    /**
     * 得到实例
     *
     * @return BMSBloodTool
     */
    public static BMSBloodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSBloodTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSBloodTool() {
        setModuleName("bms\\BMSBloodModule.x");
        onInit();
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
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
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 入库更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateIn", parm, conn);
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
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 交叉配学查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodCross(TParm parm) {
//        TParm result = this.query("queryBloodCross", parm);
    	TParm result = this.query("queryBloodCrossExceptOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 血品库存状态查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodStock(TParm parm) {
        TParm result = this.query("queryBloodStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 库存查询
     *
     * @param parm
     * @return
     */
    public TParm queryBloodStockOrderBy(TParm parm) {
        TParm result = this.query("queryBloodStockOrderBy", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 根据就诊序号,病案号和住院号获得病患输血信息(红细胞,血小板,血浆,全血)
     *
     * @param parm
     * @return
     */
    public TParm getApplyInfo(TParm parm) {
        TParm result = this.query("getApplyInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新备血单信息(交叉配血)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodCross(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdateBloodCross", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新备血单信息(交叉配血审核)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodCrossRecheck(TParm parm, TConnection conn) {
    	//System.out.println("ttttt:"+parm);
    	int row = parm.getCount("APPLY_NO");
    	//System.out.println("gggg"+row);
    	TParm pa = new TParm();
    	pa.setData("APPLY_NO", parm.getData("APPLY_NO", 0));
		pa.setData("RECHECK_USER", parm.getData("USER_ID"));
		pa.setData("RECHECK_TIME", parm.getData("TIME"));
		//System.out.println("gggg"+pa);
    	TParm result1 = this.update("ApplyUpdateBloodCrossRecheckOnBMS_APPLYM", pa, conn);
    	if (result1.getErrCode() < 0) {
            err("ERR:" + result1.getErrCode() + result1.getErrText()
                + result1.getErrName());
            return result1;
        }
    	
    	TParm result2 = new TParm();
    	for(int i = 0;i<row;i++){
    		TParm p = new TParm();
    		p.setData("APPLY_NO", parm.getData("APPLY_NO", i));
    		p.setData("RECHECK_USER", parm.getData("RECHECK_USER", i));
    		p.setData("RECHECK_TIME", parm.getData("RECHECK_TIME", i));    		    		
    		p.setData("BLOOD_NO", parm.getData("BLOOD_NO", i));
    		
            result2 = this.update("ApplyUpdateBloodCrossRecheckOnBMS_BLOOD", p, conn);
            if (result2.getErrCode() < 0) {
                err("ERR:" + result2.getErrCode() + result2.getErrText()
                    + result2.getErrName());
                return result2;
            }
            //System.out.println("wwwwww"+p);
    	}
    	//System.out.println("aaaaaa");
        return result2;
    }

    /**
     * 更新备血单信息(血品出库)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodOut(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdateBloodOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询 (配血量 + 出库量)
     *
     * @param parm
     * @return
     */
    public TParm getSumTot(TParm parm) {
        TParm result = this.query("getSumTot", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 库存状态查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodStockStatus(TParm parm) {
        TParm result = this.query("queryBloodStockStatus", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 血品库存查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodQtyStock(TParm parm) {
        TParm result = this.query("queryBloodQtyStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 血库用血月报表
     *
     * @param parm
     * @return
     */
    public TParm onQueryMonthStock(TParm parm) {
        TParm result = this.query("queryMonthStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 交叉配学查询（已出库的不再显示在交叉配血界面【TABLE_M】中）
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodCrossExceptOut(TParm parm) {
        TParm result = this.query("queryBloodCrossExceptOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询交叉配血
     * 
     * @param parm
     * @return
     */
    public TParm queryBloodCrossData(TParm parm) {
    	String sql = "SELECT * FROM BMS_BLOOD WHERE BLOOD_NO IN (" + parm.getValue("BLOOD_NO") + ")";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
    	if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 用血量总量统计查询(按总量大小倒序)
     * 
     * @param parm
     * @return result TParm
     */
    public TParm queryBloodUseAmount(TParm parm) {
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("SELECT MR_NO, CASE_NO, SUM (BLOOD_VOL) AS SUM_BLOOD_VOL ");
    	sbSql.append(" FROM BMS_BLOOD WHERE CASE_NO IS NOT NULL ");
    	
    	// 血品
    	if (StringUtils.isNotEmpty(parm.getValue("BLD_CODE"))) {
    		sbSql.append(" AND BLD_CODE = '");
    		sbSql.append(parm.getValue("BLD_CODE"));
    		sbSql.append("' ");
    	}
    	
    	// 入库时间
    	if (StringUtils.isNotEmpty(parm.getValue("IN_DATE_S"))) {
    		sbSql.append(" AND IN_DATE > TO_DATE('");
    		sbSql.append(parm.getValue("IN_DATE_S"));
    		sbSql.append("000000','YYYYMMDDHH24MISS') ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("IN_DATE_E"))) {
    		sbSql.append(" AND IN_DATE < TO_DATE('");
    		sbSql.append(parm.getValue("IN_DATE_E"));
    		sbSql.append("235959','YYYYMMDDHH24MISS') ");
    	}
    	
    	// 出库时间
    	if (StringUtils.isNotEmpty(parm.getValue("OUT_DATE_S"))) {
    		sbSql.append(" AND OUT_DATE > TO_DATE('");
    		sbSql.append(parm.getValue("OUT_DATE_S"));
    		sbSql.append("000000','YYYYMMDDHH24MISS') ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("OUT_DATE_E"))) {
    		sbSql.append(" AND OUT_DATE < TO_DATE('");
    		sbSql.append(parm.getValue("OUT_DATE_E"));
    		sbSql.append("235959','YYYYMMDDHH24MISS') ");
    	}
    	
    	// 血型
    	if (StringUtils.isNotEmpty(parm.getValue("BLOOD_TYPE"))) {
    		sbSql.append(" AND BLD_TYPE = '");
    		sbSql.append(parm.getValue("BLOOD_TYPE"));
    		sbSql.append("' ");
    	}
    	
    	sbSql.append(" GROUP BY MR_NO, CASE_NO ");
    	sbSql.append(" ORDER BY SUM_BLOOD_VOL DESC ");
    	TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
    	if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询用血量明细
     * 
     * @param parm
     * @return result TParm
     */
    public TParm queryBloodUseDetail(TParm parm) {
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("SELECT A.BLOOD_NO,A.BLD_CODE,B.BLDCODE_DESC,A.BLOOD_VOL,C.UNIT_CHN_DESC,A.BLD_TYPE,");
    	sbSql.append("A.MR_NO,A.CASE_NO,D.PAT_NAME,A.IN_DATE,A.OUT_DATE,A.OUT_USER ");
    	sbSql.append(" FROM BMS_BLOOD A, BMS_BLDCODE B, SYS_UNIT C, SYS_PATINFO D ");
    	sbSql.append(" WHERE A.BLD_CODE = B.BLD_CODE AND B.UNIT_CODE = C.UNIT_CODE AND A.MR_NO = D.MR_NO ");
    	
    	// 血品
    	if (StringUtils.isNotEmpty(parm.getValue("BLD_CODE"))) {
    		sbSql.append(" AND A.BLD_CODE = '");
    		sbSql.append(parm.getValue("BLD_CODE"));
    		sbSql.append("' ");
    	}
    	
    	// 入库时间
    	if (StringUtils.isNotEmpty(parm.getValue("IN_DATE_S"))) {
    		sbSql.append(" AND A.IN_DATE > TO_DATE('");
    		sbSql.append(parm.getValue("IN_DATE_S"));
    		sbSql.append("000000','YYYYMMDDHH24MISS') ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("IN_DATE_E"))) {
    		sbSql.append(" AND A.IN_DATE < TO_DATE('");
    		sbSql.append(parm.getValue("IN_DATE_E"));
    		sbSql.append("235959','YYYYMMDDHH24MISS') ");
    	}
    	
    	// 出库时间
    	if (StringUtils.isNotEmpty(parm.getValue("OUT_DATE_S"))) {
    		sbSql.append(" AND A.OUT_DATE > TO_DATE('");
    		sbSql.append(parm.getValue("OUT_DATE_S"));
    		sbSql.append("000000','YYYYMMDDHH24MISS') ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("OUT_DATE_E"))) {
    		sbSql.append(" AND A.OUT_DATE < TO_DATE('");
    		sbSql.append(parm.getValue("OUT_DATE_E"));
    		sbSql.append("235959','YYYYMMDDHH24MISS') ");
    	}
    	
    	// 血型
    	if (StringUtils.isNotEmpty(parm.getValue("BLOOD_TYPE"))) {
    		sbSql.append(" AND A.BLD_TYPE = '");
    		sbSql.append(parm.getValue("BLOOD_TYPE"));
    		sbSql.append("' ");
    	}
    	
    	sbSql.append(" ORDER BY A.CASE_NO,A.MR_NO,A.BLD_CODE,A.OUT_DATE ");
    	
    	TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
    	if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
