package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 单病种转入工具类</p>
 *
 * <p>Description: 单病种转入工具类</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2012.02.13
 * @version 1.0
 */
public class INSSingleTransferTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INSSingleTransferTool instanceObject;
    /**
     * 得到实例
     * @return INSSingleTransferTool
     */
    public static INSSingleTransferTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSSingleTransferTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSSingleTransferTool() {
        onInit();
    }

    /**
     * 得到单病种已转入资料
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleTransData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        
        String wsql  = "";
        String deptsql  = "";      
        String casenosql  = " ";
        String cardType="";// add by kangy 20171204 添加医保类别为空条件
        if(parm.getValue("MR_NO").length()  != 0)
        	wsql  = " AND C.MR_NO  = '"+parm.getValue("MR_NO")+"' " ; 
        if(parm.getValue("DEPT_CODE").length()  != 0)
        	deptsql  = " AND A.DEPT_CODE = '"+parm.getValue("DEPT_CODE")+"' " ;  
        if(parm.getValue("SINGLE_PARM").length()  != 0)
        	casenosql = " AND A.CASE_NO IN ("+parm.getValue("SINGLE_PARM")+")";
        if(parm.getValue("CARD_TYPE").length()!=0)// add by kangy 20171204 添加医保类别为空条件
        	cardType=" AND B.INS_CROWD_TYPE = '" + parm.getData("CARD_TYPE") +"' ";
        String sql =
            " SELECT CASE SUBSTR (C.CONFIRM_NO, 1, 2) WHEN 'KN' THEN ''  ELSE TO_CHAR (A.IN_DATE, 'yyyymmdd') END AS CONFIRM," +
            "        A.CASE_NO, C.CONFIRM_NO, C.PAT_NAME, D.CHN_DESC AS SEX_DESC," +
            "        B.CTZ_DESC, C.IDNO," +
            "        CASE IN_STATUS " +
            "        WHEN '0' " +
            "        THEN '资格确认书录入' " +
            "        WHEN '1' " +
            "        THEN '费用已结算' " +
            "        WHEN '2' " +
            "        THEN '费用已上传' " +
            "        WHEN '3' " +
            "        THEN '下载已审核' " +
            "        WHEN '4' " +
            "        THEN '下载已支付' " +
            "        WHEN '5' " +
            "        THEN '撤销确认书' " +
            "        WHEN '6' " +
            "        THEN '开具资格确认书失败' " +
            "        WHEN '7' " +
            "        THEN '资格确认书已审核' ELSE '' END AS IN_STATUS," +
            "        A.MR_NO, C.SDISEASE_CODE, E.CHN_DESC AS SINGLEDISEASE_DESC " +
            "   FROM ADM_INP A,SYS_CTZ B,INS_ADM_CONFIRM C,SYS_DICTIONARY D,SYS_DICTIONARY E " +
            "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
            "    AND A.DS_DATE BETWEEN TO_DATE ('" + parm.getData("S_DATE")+"000000"+
            "', 'YYYYMMDDhh24miss') " +
            "                  AND TO_DATE ('" + parm.getData("E_DATE")+"235959"+
            "', 'YYYYMMDDhh24miss') " +
            "    AND B.CTZ_CODE = A.CTZ1_CODE " +
            "    AND B.NHI_CTZ_FLG = 'Y' " +
            "    AND C.CASE_NO = A.CASE_NO " +
            "    AND (C.IN_STATUS IN ('0', '1', '7') OR C.IN_STATUS IS NULL) " +
            "    AND C.SEX_CODE = D.ID(+) " +
            "    AND D.GROUP_ID = 'SYS_SEX' " +
            "    AND C.SDISEASE_CODE = E.ID(+) " +
            "    AND E.GROUP_ID = 'SIN_DISEASE' " +
            "    AND C.SDISEASE_CODE IS NOT NULL " +
            "    AND C.LOCAL_FLG = 'Y' " +
            "    AND A.BILL_STATUS = '3'" +
            cardType//// modify by kangy 20171204 添加医保类别为空条件
            //"    AND B.INS_CROWD_TYPE = '" + parm.getData("CARD_TYPE") +"' " 
            + wsql
            + deptsql
            + casenosql;
        //System.out.println("得到单病种已转入资料" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到单病种未转入资料
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleNoTransData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        
        String wsql  = "";
        String deptsql  = "";
        String casenosql  = "";
        String cardType=" ";
        if(parm.getValue("MR_NO").length()  != 0)
        	wsql  = " AND C.MR_NO  = '"+parm.getValue("MR_NO")+"' " ; 
        if(parm.getValue("DEPT_CODE").length()  != 0)
        	deptsql  = " AND A.DEPT_CODE = '"+parm.getValue("DEPT_CODE")+"' " ; 
        if(parm.getValue("SINGLE_PARM").length()  != 0)
        	casenosql = " AND A.CASE_NO IN ("+parm.getValue("SINGLE_PARM")+")";
        if(parm.getValue("CARD_TYPE").length()  != 0)// add by kangy 20171204 添加医保类别为空条件
        	cardType=" AND B.INS_CROWD_TYPE = '" + parm.getData("CARD_TYPE") + "' ";
        String sql =
            " SELECT CASE SUBSTR (C.CONFIRM_NO, 1, 2) WHEN 'KN' THEN ''  ELSE TO_CHAR (A.IN_DATE, 'yyyymmdd') END AS CONFIRM," +
            "        A.CASE_NO, C.CONFIRM_NO, C.PAT_NAME, D.CHN_DESC AS SEX_DESC," +
            "        B.CTZ_DESC, C.IDNO," +
            "        CASE IN_STATUS " +
            "        WHEN '0' " +
            "        THEN '资格确认书录入' " +
            "        WHEN '1' " +
            "        THEN '费用已结算' " +
            "        WHEN '2' " +
            "        THEN '费用已上传' " +
            "        WHEN '3' " +
            "        THEN '下载已审核' " +
            "        WHEN '4' " +
            "        THEN '下载已支付' " +
            "        WHEN '5' " +
            "        THEN '撤销确认书' " +
            "        WHEN '6' " +
            "        THEN '开具资格确认书失败' " +
            "        WHEN '7' " +
            "        THEN '资格确认书已审核' ELSE '' END AS IN_STATUS," +
            "        A.MR_NO, '','' " +
            "   FROM ADM_INP A,SYS_CTZ B,INS_ADM_CONFIRM C,SYS_DICTIONARY D " +
            "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
            "    AND A.DS_DATE BETWEEN TO_DATE ('" + parm.getData("S_DATE")+"000000"+
            "', 'YYYYMMDDhh24miss') " +
            "                  AND TO_DATE ('" + parm.getData("E_DATE")+"235959"+
            "', 'YYYYMMDDhh24miss') " +
            "    AND B.CTZ_CODE = A.CTZ1_CODE " +
            "    AND B.NHI_CTZ_FLG = 'Y' " +
            "    AND C.CASE_NO = A.CASE_NO " +
            "    AND (C.IN_STATUS IN ('0', '1', '7') OR C.IN_STATUS IS NULL) " +
            "    AND C.SEX_CODE = D.ID(+) " +
            "    AND D.GROUP_ID = 'SYS_SEX' " +
            "    AND C.SDISEASE_CODE IS NULL " +
            "    AND C.LOCAL_FLG = 'Y' " +
            "    AND A.BILL_STATUS = '3'" +
            cardType//modify by kangy 20171204 添加医保类别为空条件
            //"    AND B.INS_CROWD_TYPE = '" + parm.getData("CARD_TYPE") + "' "  
            + wsql 
            + deptsql
            + casenosql;
        //System.out.println("得到单病种未转入资料" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新资格确认书状态
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpINSIbsUpload(TParm parm) {
        TParm result = new TParm();
        String setSql = "";
        String diseaseCode  = ""+ parm.getValue("SDISEASE_CODE");
        
        if (diseaseCode.length() != 0)
            setSql = "    SET SDISEASE_CODE = '" + parm.getData("SDISEASE_CODE") + "' ";
        else
            setSql = "    SET SDISEASE_CODE = NULL ";
        
        String sql =
            " UPDATE INS_ADM_CONFIRM " +
            setSql +
            "  WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

}

