package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 每日费用清单</p>
 *
 * <p>Description: 每日费用清单</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *                
 * <p>Company: Javahis</p> 
 *
 * @author zhangk 2010-9-28
 * @version 1.0
 */   
public class BILDailyFeeQueryTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILDailyFeeQueryTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static BILDailyFeeQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILDailyFeeQueryTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILDailyFeeQueryTool() {
        setModuleName("bil\\BILDailyFeeQueryModule.x");
        onInit();
    }
    /**
     * 查询病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm){
        TParm result = this.query("selectdata",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 打印数据查询
     * @param parm TParm
     * @return TParm
     *  =================pangben modify 20110815 修改sql语句，添加生成xml文件的数据
     */
    public TParm selectPrintData(TParm parm){
    	String insWhere = "";
        String where = "";
        if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
            where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
            insWhere = 
            	" AND (H.KSSJ < TO_DATE ('"+parm.getValue("DATE_S")+"'||'000000', 'YYYY/MM/DDHH24MISS') OR H.KSSJ IS NULL)" +
            	" AND (H.JSSJ > TO_DATE ('"+parm.getValue("DATE_E")+"'||'235959', 'YYYY/MM/DDHH24MISS') OR H.JSSJ IS NULL)";
        }
        //String regionWhere  = "";
        //if(!"".equals(parm.getValue("REGION_CODE")))
        //    regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
        String sql = "SELECT A.REXP_CODE,A.HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
                       " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC, "+
                       " D.CHARGE_HOSP_DESC,E.CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE,A.BILL_DATE AS DATE_BILL,"+
                       " B.NHI_CODE_I,H.PZWH,H.ZFBL1 " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
                       " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F,INS_RULE H "+
                       " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                       where +
                       " AND A.OWN_PRICE<>0 "+
                       //regionWhere+
                       " AND A.ORDER_CODE=B.ORDER_CODE "+
                       " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                       " AND A.HEXP_CODE=D.CHARGE_HOSP_CODE "+
                       " AND E.GROUP_ID='SYS_CHARGE' "+
                       " AND A.REXP_CODE=E.ID "+
                       " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
                       " AND B.NHI_CODE_I = H.SFXMBM(+) " +
                       insWhere+
                       " GROUP BY A.REXP_CODE,A.HEXP_CODE," +
                       //modify by liming 2012/02/06 begin
                       "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                       " D.CHARGE_HOSP_DESC,E.CHN_DESC,B.SPECIFICATION, "+
                       //modify by liming 2012/02/06 end
                       " F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE,A.BILL_DATE " + 
				       // modify by zhangzc 2012/06/12 
				       ",B.NHI_CODE_I,H.PZWH,H.ZFBL1 ";
        String orderBy = "";
        if(parm.getData("ORDER_BY1")!=null){
        	//===zhangp 20120316 start
            orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
        }else if(parm.getData("ORDER_BY2")!=null){
            orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
        }else if(parm.getData("ORDER_BY3")!=null){
            orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
        }else if(parm.getData("ORDER_BY4")!=null){
            orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
            //===zhangp 20120316 end
        }
//        System.out.println("住院每日清单SQL:"+sql+orderBy);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 打印汇总清单数据查询
     * @param parm TParm
     * @return TParm
     * =================pangben modify 20110815 修改sql语句，添加生成xml文件的数据
     */
    public TParm selectPrintDataForHZ(TParm parm){
        String where = "";
        String insWhere = "";
        if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
            where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
            insWhere = 
            	" AND (H.KSSJ < TO_DATE ('"+parm.getValue("DATE_S")+"'||'000000', 'YYYY/MM/DDHH24MISS') OR H.KSSJ IS NULL)" +
            	" AND (H.JSSJ > TO_DATE ('"+parm.getValue("DATE_E")+"'||'235959', 'YYYY/MM/DDHH24MISS') OR H.JSSJ IS NULL)";
        }
        //String regionWhere  = "";
        //if(!"".equals(parm.getValue("REGION_CODE")))
        //regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
        String sql = "SELECT A.REXP_CODE,A.HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
        				//===zhangp 20120521 start
                       " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE, "+
//                       " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE,A.DR_CODE,G.USER_NAME,A.BILL_DATE AS DATE_BILL "+
                       //===zhangp 20120521 end
                       " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE, "+
                       " B.NHI_CODE_I,H.PZWH,H.ZFBL1 " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
                       " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F,INS_RULE H "+
                       " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                       where +
                       " AND A.OWN_PRICE<>0 "+
                       //regionWhere+
                       " AND A.ORDER_CODE=B.ORDER_CODE "+
                       " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                       " AND A.HEXP_CODE=D.CHARGE_HOSP_CODE "+
                       " AND E.GROUP_ID='SYS_CHARGE' "+
                       " AND A.REXP_CODE=E.ID "+
                       " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
                       " AND B.NHI_CODE_I = H.SFXMBM(+) " +//add by zhangzc 20120612
                       insWhere+
                       " GROUP BY A.REXP_CODE,A.HEXP_CODE," +
                       //modify by liming begin
                       "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                       " D.CHARGE_HOSP_DESC,E.CHN_DESC,B.SPECIFICATION, "+
                       //modify by liming end
       					//===zhangp 20120521 start
//                       " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE,A.DR_CODE,G.USER_NAME,A.BILL_DATE ";
        			   " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE " +
        			   // modify by zhangzc 2012/06/12 
				       ",B.NHI_CODE_I,H.PZWH,H.ZFBL1 ";
        //===zhangp 20120521 end
        String orderBy = "";
        if(parm.getData("ORDER_BY1")!=null){
            orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
        }else if(parm.getData("ORDER_BY2")!=null){
            orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
        }else if(parm.getData("ORDER_BY3")!=null){
            orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
        }else if(parm.getData("ORDER_BY4")!=null){
            orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
        }

//        System.out.println("住院汇总清单SQL:"+sql+orderBy);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询病患的部分住院信息
     * @param CASE_NO String
     * @return TParm
     */
    public TParm selectInpInfo(String CASE_NO){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("selectInpInfo",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
