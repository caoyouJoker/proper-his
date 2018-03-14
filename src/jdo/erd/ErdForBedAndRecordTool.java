package jdo.erd;

import jdo.sys.PatTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 记录留观床位和动态记录</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ErdForBedAndRecordTool extends TJDOTool {

    /**
     * 实例
     */
    private static ErdForBedAndRecordTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static ErdForBedAndRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdForBedAndRecordTool();
        return instanceObject;
    }

    public ErdForBedAndRecordTool() {
        this.setModuleName("erd\\ERDMainModule.x");// 加载Module文件
        onInit();
    }

    /**
     * 根据查询条件查询REG_PATADM表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selPat(TParm parm) {
        TParm result = new TParm();
        if (parm.getValue("ADM_STATUS").equals("5")) {
            parm.removeData("ADM_STATUS");
            result = query("selPatInERD", parm);
        } else {
            result = query("selPat", parm);
        }
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据查询条件查询REG_BED表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selBed(TParm parm) {
        TParm result = new TParm();
        result = query("selBed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 插入一条新记录ERD_RECORD
     * @param parm TParm
     * @return TParm
     */
    public TParm insertErdRecord(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("insertErdRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdBed(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("updateErdBed", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新ERD_RECORD.BED_NO
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdRecordBed(TParm parm, TConnection connection) {//wanglong add 20150528
        TParm result = new TParm();
        result = update("updateErdRecordBed", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新REG_PATADM.ADM_STATUS
     * @param parm TParm
     * @return TParm
     */
    public TParm updateAdmStatus(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("updateAdmStauts", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新ERD_RECORD
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdRecord(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("updateErdRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据查询条件查询急诊护士需要执行的医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm selOrderExec(TParm parm) {
        TParm result = new TParm();
        result = query("selOrderExec", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 急诊护士执行更新OPD_ORDER
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm updateExec(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("updateExec", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据就诊序号得到患者急诊留观诊区床号
     * @param parm TParm
     * @return TParm
     */
    public TParm selERDRegionBedByPat(TParm parm){
        TParm result = query("selERDRegionBedByPat",parm);
        return result;
    }
    
    /**
     * 根据检伤号得到患者急诊留观诊区床号
     * @author wangqing
     * @param parm TParm
     * @return TParm
     */
    public TParm selERDRegionBedByPat2(TParm parm){
        TParm result = query("selERDRegionBedByPat2",parm);
        return result;
    }
    
    /**
     * 生成BAR_CODE
     * 
     * @param parm
     * @return
     */
    public TParm generateIFBarcode(TParm parm) {//wanglong add 20150407
        TParm result = new TParm();
        // 前台传的数据
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm execData = new TParm();
            execData.setData("CASE_NO", parm.getData("CASE_NO", i));
            execData.setData("RX_NO", parm.getData("RX_NO", i));
            execData.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            execData.setData("BAR_CODE", parm.getData("BAR_CODE", i));
            execData.setData("OPT_USER", parm.getData("OPT_USER", i));
            execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));
            execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            result = this.updateOPDOrderBarCode(execData);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
                return result;
            }
        }
        return result;
    }
    
    /**
     * 更新配液条码
     * 
     * @param parm
     * @return
     */
    public TParm updateOPDOrderBarCode(TParm parm) {// wanglong add 20150407
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("updateOPDOrderBarCode", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 根据查询条件查询REG_PATADM表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selPatNew(TParm parm) {
    	String sql ="";
        TParm result = new TParm();
        if (parm.getValue("ADM_STATUS").equals("5")) {
            parm.removeData("ADM_STATUS");
            
             sql = "SELECT A.REG_DATE, A.REG_DATE ADM_DATE, A.CASE_NO, A.MR_NO, B.PAT_NAME, A.TRIAGE_NO, " +
            		" A.DEPT_CODE, C.ERD_REGION_CODE, A.ERD_LEVEL, C.BED_NO, C.BED_DESC " +
            		" FROM REG_PATADM A, SYS_PATINFO B, ERD_BED C, ERD_RECORD D " +
            		" WHERE A.CASE_NO = C.CASE_NO " +
            		" AND A.CASE_NO = D.CASE_NO(+) " +
            		" AND A.MR_NO = B.MR_NO " +
            		" AND A.ADM_TYPE = 'E' " +
            		" AND A.REGCAN_USER IS NULL  @ " +
            		" ORDER BY A.REG_DATE DESC";

           
            
//            result = query("selPatInERD", parm);
        } else {
//            result = query("selPat", parm);
        	 sql = "SELECT A.REG_DATE, A.REG_DATE ADM_DATE, A.CASE_NO, A.MR_NO, B.PAT_NAME, A.TRIAGE_NO, " +
        			" A.DEPT_CODE, C.ERD_REGION_CODE, A.ERD_LEVEL, C.BED_NO, C.BED_DESC " +
        			" FROM REG_PATADM A, SYS_PATINFO B, ERD_BED C, ERD_RECORD D " +
        			" WHERE A.CASE_NO = C.CASE_NO(+) " +
        			" AND A.CASE_NO = D.CASE_NO(+) " +
        			" AND A.MR_NO = B.MR_NO " +
        			" AND A.ADM_TYPE = 'E' " +
        			" AND A.REGCAN_USER IS NULL  @ " +
        			" ORDER BY A.REG_DATE DESC";
        	
        	
        }
        
        String where = "";
        
        /*modified by Eric 20170524 
        add time limit*/
        where += " AND A.REG_DATE BETWEEN TO_DATE('"+parm.getValue("date2")+"','yyyy/mm/dd hh24:mi:ss') AND sysdate ";
        
        if(parm.getValue("ADM_STATUS") != null && parm.getValue("ADM_STATUS").length() > 0){
        	where += " AND A.ADM_STATUS='"+parm.getValue("ADM_STATUS")+"'";
        }
        if(parm.getValue("CASE_NO") != null && parm.getValue("CASE_NO").length() > 0){
        	where += " AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
        }
        if(parm.getValue("MR_NO") != null && parm.getValue("MR_NO").length() > 0){
            
        	where += " AND A.MR_NO IN ("+PatTool.getInstance().getMrRegMrNos(parm.getValue("MR_NO")) +")";
        }
        if(parm.getValue("ERD_REGION") != null && parm.getValue("ERD_REGION").length() > 0){
        	where += " AND C.ERD_REGION_CODE='"+parm.getValue("ERD_REGION")+"'";
        }
        if(parm.getValue("REG_DATE") != null && parm.getValue("REG_DATE").length() > 0){
        	
        	 
        	 String regDate = parm.getValue("REG_DATE");
        	 regDate = regDate.substring(0, regDate.lastIndexOf(".")).replace(":", "")
     		.replace("-", "").replace(" ", "");
        	
        	where += " AND A.REG_DATE=TO_DATE('"+regDate+"','YYYYMMDDHH24MISS')";
        }
        
        if((parm.getValue("START_DATE") != null &&  parm.getValue("START_DATE").length() > 0) 
        		&& (parm.getValue("END_DATE") != null &&  parm.getValue("END_DATE").length() > 0)){
     		
     		String date_s = parm.getValue("START_DATE");
    		String date_e = parm.getValue("END_DATE");
    		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
    		.replace("-", "").replace(" ", "");
    		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
    		.replace("-", "").replace(" ", "");
     		
     		
     		where += " AND D.OUT_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') AND TO_DATE('"+date_e+"','YYYYMMDDHH24MISS')";
        	
        }

        sql = sql.replace("@", where);
//        System.out.println(sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        
        
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新ERD_BED
     * @author wangqing 20170626
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdBed2(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 执行module上的insert update delete用update
        result = update("updateErdBed2", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 更新ERD_EVALUTION
     * @author wangqing 20170626
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdEvalution(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateErdEvalution", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入AMI_E_S_RECORD
     * @author wangqing 20170626
     * @param parm TParm
     * @return TParm
     */
    public TParm insertAmiESRecord(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertAmiESRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 更新AMI_E_S_RECORD
     * @author wangqing 20170626
     * @param parm TParm
     * @return TParm
     */
    public TParm updateAmiESRecord(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateAmiESRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新ERD_EVALUTION.OUT_DATE
     * @author wangqing 20170627
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateErdEvalutionOutDate(TParm parm, TConnection connection){
    	TParm result = new TParm();
        result = update("updateErdEvalutionOutDate", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    
}
