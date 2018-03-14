package jdo.reg;

import java.sql.Timestamp;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;

import jdo.bil.BIL;
import jdo.sys.Operator;

import java.text.DecimalFormat;

import jdo.sys.SysFee;

import com.dongyang.util.TypeTool;

import jdo.util.Manager;

import java.text.SimpleDateFormat;

/**
 *
 * <p>Title:挂号主档工具类 </p>
 *
 * <p>Description:挂号主档工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.28
 * @version 1.0
 */
public class PatAdmTool extends TJDOTool {
    /**
     * 实例
     */
    private static PatAdmTool instanceObject;
    /**
     * 得到实例
     * @return PatAdmTool
     */
    public static PatAdmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PatAdmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PatAdmTool() {
        setModuleName("reg\\REGPatAdmModule.x");
        onInit();
    }

    /**
     * 查询挂号主档表
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {

        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 周转日查询挂号主档表(新)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataNew(TParm parm) {
        String regionWhere = "";
        if (parm.getData("REGION_CODE") != null)
            regionWhere = " AND REGION_CODE = '" + parm.getData("REGION_CODE") +
                          "' ";
        String admTypeWhere = "";
        if (parm.getData("ADM_TYPE") != null)
            admTypeWhere = " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                           "' ";
        String roomWhere = "";
        if (parm.getData("CLINICROOM_NO") != null)
            roomWhere = " AND CLINICROOM_NO IN (" +
                        parm.getData("CLINICROOM_NO") + ") ";
        String sql =
            " SELECT ADM_TYPE, CASE_NO, MR_NO, REGION_CODE, ADM_DATE, REG_DATE,"+
            "        SESSION_CODE, CLINICAREA_CODE, CLINICROOM_NO, QUE_NO, REG_ADM_TIME,"+
            "        DEPT_CODE, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE, VISIT_CODE,"+
            "        REGMETHOD_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, TRANHOSP_CODE,"+
            "        TRIAGE_NO, CONTRACT_CODE, ARRIVE_FLG, REGCAN_USER, REGCAN_DATE,"+
            "        ADM_REGION, PREVENT_SCH_CODE, DRG_CODE, HEAT_FLG, ADM_STATUS,"+
            "        REPORT_STATUS, WEIGHT, HEIGHT, CLINICTYPE_CODE, VIP_FLG, OPT_USER,"+
            "        OPT_DATE, OPT_TERM, SERVICE_LEVEL "+
            "   FROM REG_PATADM "+
            "  WHERE ADM_DATE = TO_DATE('" +
            //===zhangp 20130225 start
//            StringTool.getString(parm.getTimestamp("ADM_DATE"), "YYYYMMDD") +
            parm.getTimestamp("ADM_DATE").toString().substring(0, 4) + parm.getTimestamp("ADM_DATE").toString().substring(5, 7) + parm.getTimestamp("ADM_DATE").toString().substring(8, 10) +
            //===zhangp 20130225 end
            "','yyyyMMdd') " +
            regionWhere+
            admTypeWhere+
            roomWhere;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = query("selDataNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询挂号主档表
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata_name(TParm parm) {

        TParm result = query("selectdata_name", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询挂号主档表 未检伤的记录
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata_erd(TParm parm) {

        TParm result = query("selectdata_erd", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 得到病患信息
     * @param caseNo String
     * @return TParm
     */
    public TParm getInfoForCaseNo(String caseNo) {
        if (caseNo == null || caseNo.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        return query("getInfoForCaseNo", parm);
    }

    /**
     * 新增挂号
     * @param reg Reg 挂号
     * @return boolean true 成功 false 失败
     */
    public boolean newReg(Reg reg) {
        if (reg == null)
            return false;
        String newCaseNo = reg.caseNo();
        if (newCaseNo == null || newCaseNo.length() == 0)
            newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
                    "CASE_NO", "CASE_NO");
        String receiptNo = "";
        if ("N".equals(reg.getApptCode()))
            receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
                    "RECEIPT_NO",
                    "RECEIPT_NO");
        if (newCaseNo == null || newCaseNo.length() == 0) {
            err("-1 取问诊号错误!");
            return false;
        }
        reg.setCaseNo(newCaseNo); //1就诊序号(REG)
        reg.getRegReceipt().setCaseNo(newCaseNo); //1就诊序号(REG_RECEIPT)
        reg.getRegReceipt().setReceiptNo(receiptNo); //2挂号收据(REG_RECEIPT)
        reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); //3票据明细档收据号(BIL_INVRCP)

        //门急诊主档
        TParm regParm = reg.getParm();
        TParm saveParm = new TParm();
        saveParm.setData("REG", regParm.getData());
        saveParm.setData("ADM_TYPE", reg.getAdmType());
        //System.out.println("挂号主档数据"+ regParm.getData());

        //门诊收据
        TParm regReceiptParm = reg.getRegReceipt().getParm();
        saveParm.setData("REG_RECEIPT", regReceiptParm.getData());

        //票据主档
        TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
        saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

        //票据明细档
        TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
        bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
        saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
        saveParm.setData("PRINT_NO", reg.getRegReceipt().getPrintNo()); //===========pangben modify 20110819 判断是否记账,数据为空表示记账
        
        saveParm.setData("EKT_SQL", reg.getEktSql()); //add by huangtt 20161017医疗卡执行SQL
        
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "onNewReg", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 新增挂号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertInfo(TParm parm, TConnection connection) {
        TParm result = update("insertInfo", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 新增挂号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     * ======pangben 2012-4-6 添加特批款预约挂号
     */
    public TParm insertInfoGreen(TParm parm, TConnection connection) {
        TParm result = update("insertInfoGreen", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    /**
     * 保存挂号信息
     * @param reg Reg 挂号对象
     * @return boolean true 成功 false 失败
     */
    public boolean onSave(Reg reg) {
        if (reg == null)
            return false;
        TParm parm = reg.getParm();
        if (!onSave(parm)) {
            return false;
        }
        return true;
    }

    /**
     * 保存挂号信息
     * @param parm TParm
     * @return boolean
     */
    public boolean onSave(TParm parm) {
        if (parm == null)
            return false;
        TParm result = update("updateInfo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 保存挂号信息(带链接,后台使用)
     * @param parm TParm
     * @param connection TConnection
     * @return boolean true 成功  false 失败
     */
    public boolean onSave(TParm parm, TConnection connection) {
        if (parm == null)
            return false;
        TParm result = update("updateInfo", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 根据病患姓名查询就诊序号
     * @param patName String
     * @return String
     */
    public String getCaseByPatName(String patName) {
        if (patName == null || patName.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("PAT_NAME", patName);
        TParm result = query("selcaseNoBypatName", parm);
        result.getValue("CASE_NO");
        return result.getValue("CASE_NO");
    }

    /**
     * 根据ID号查询时间段病患信息
     * @param mrno String
     * @param StartTime Timestamp
     * @param endTime Timestamp
     * @param regionCode String
     * @return TParm
     */
    public TParm selDateByMrNo(String mrno, Timestamp StartTime,
                               Timestamp endTime, String regionCode) {
        TParm parm = new TParm();
        parm.setData("MR_NO", mrno);
        //=========pangben modify 20110421 start
        parm.setData("REGION_CODE", regionCode);
        //=========pangben modify 20110421 start
        parm.setData("STARTTIME", StringTool.setTime(StartTime, "00:00:00"));
        parm.setData("ENDTIME", StringTool.setTime(endTime, "23:59:59"));
//        parm.setData("ARRIVE_FLG", "Y");//===zhangp 20120816
        TParm result = query("selDateByMrNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据ID号查询时间段病患信息
     * @param mrno String
     * @param StartTime Timestamp
     * @param endTime Timestamp
     * @param admType String
     * @param regionCode String
     * @return TParm
     */
    public TParm selDateByMrNoAdm(String mrno, Timestamp StartTime,
                                  Timestamp endTime, String admType,
                                  String regionCode) {
        TParm parm = new TParm();
        parm.setData("MR_NO", mrno);
        parm.setData("STARTTIME", StringTool.setTime(StartTime, "00:00:00"));
        parm.setData("ENDTIME", StringTool.setTime(endTime, "23:59:59"));
        parm.setData("ADM_TYPE", admType);
        parm.setData("REGION_CODE", regionCode);
        TParm result = query("selDateByMrNoAdm", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据arrive_flg,adm_date,session_code,realdept_code,realdr_code查询当天病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForODO(TParm parm) {
        TParm result = query("selDateForODO", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询挂号主档(FOR OPD)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForODOByWait(TParm parm) {
        TParm regSysParm = REGSysParmTool.getInstance().selectdata();
        if (regSysParm.getBoolean("CHECKIN_FLG", 0)) {
            parm.setData("ARRIVE_FLG", "Y");
        }
        TParm result = query("selDateForODOByWait", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 急诊查询挂号主档（FOR OPD）
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForODOEmgc(TParm parm) {
        TParm result = query("selDateForODOEmgc", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据就诊日期，时段，门急住别，真正的医师取得当日号表中的诊间集合
     * @param admDate String
     * @param sessionCode String
     * @param admType String
     * @param deptCode String
     * @param drCode String
     * @return TParm
     */
    public TParm getClinicRoomForODO(String admDate, String sessionCode,
                                     String admType, String deptCode,
                                     String drCode) {
        String sql =
                "SELECT DISTINCT A.CLINICROOM_NO AS ID,A.CLINICROOM_DESC AS NAME,ENG_DESC" +
                "		FROM REG_CLINICROOM A ,REG_PATADM B " +
                "		WHERE B.REALDR_CODE='" + drCode + "' " +
                "	  AND B.ADM_TYPE='" + admType + "'" +
                "	  AND B.ADM_DATE=TO_DATE('" + admDate + "','YYYY-MM-DD') " +
                "	  AND B.SESSION_CODE='" + sessionCode + "'" +
                " 	  AND A.CLINICROOM_NO=B.CLINICROOM_NO " +
                "     AND B.REALDEPT_CODE='" + deptCode + "'" +
                "     ORDER BY A.CLINICROOM_NO";
//        System.out.println("getClinicRoomForODO.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据如下信息查询诊间号
     * @param admDate String
     * @param sessionCode String
     * @param admType String
     * @param drCode String
     * @param dept String
     * @return TParm
     */
    public TParm getClinicRoomByRealDept(String admDate, String sessionCode,
                                         String admType, String drCode,
                                         String dept) {

        String sql =
                "SELECT DISTINCT A.CLINICROOM_NO AS ID,A.CLINICROOM_DESC AS NAME" +
                "		FROM REG_CLINICROOM A ,REG_PATADM B " +
                "		WHERE B.REALDR_CODE='" + drCode + "' " +
                "	  AND B.ADM_TYPE='" + admType + "'" +
                "	  AND B.ADM_DATE=TO_DATE('" + admDate + "','YYYY-MM-DD') " +
                "	  AND B.REALDEPT_CODE='" + dept + "'" +
                "	  AND B.SESSION_CODE='" + sessionCode + "'" +
                " 	  AND A.CLINICROOM_NO=B.CLINICROOM_NO  ORDER BY A.CLINICROOM_NO";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据如下信息查询代诊科室下的医师
     * @param admType String
     * @param sessionCode String
     * @param admDate String
     * @param dept String
     * @return TParm
     */
    public TParm getInsteadDrByDept(String admType, String sessionCode,
                                    String admDate, String dept) {
        String sql =
                " SELECT DISTINCT B.USER_ID AS ID,B.USER_NAME AS NAME,B.PY1 AS PY1,USER_ENG_NAME "
                + "	FROM SYS_OPERATOR B,REG_PATADM A "
                + "	WHERE A.ADM_DATE=TO_DATE('" + admDate + "','YYYY-MM-DD') " +
                "		  AND A.REALDEPT_CODE='" + dept + "'" +
                "		  AND B.USER_ID=A.REALDR_CODE  ORDER BY ID,NAME,PY1";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据如下信息查找代诊科室
     * @param admType String
     * @param admDate String
     * @param sessionCode String
     * @param regionCode String
     * @return TParm
     */
    public TParm getInsteadDept(String admType, String admDate,
                                String sessionCode, String regionCode) {
        String whereAdmType = "";
        if (!"".equals(admType))
            whereAdmType = " AND A.ADM_TYPE = '" + admType + "' ";
        String whereSessionCode = "";
        if (!"".equals(sessionCode))
            whereSessionCode = " AND A.SESSION_CODE = '" + sessionCode + "' ";
        String whereRegionCode = "";
        if (!"".equals(regionCode))
            whereRegionCode = " AND A.REGION_CODE = '" + regionCode + "' ";
        String sql = "";
//    	System.out.println("language="+Operator.getLanguage());
        if ("zh".equalsIgnoreCase(Operator.getLanguage())) {
            sql =
                    " SELECT DISTINCT B.DEPT_CODE AS ID,B.DEPT_CHN_DESC AS NAME,B.PY1 AS PY1 " +
                    "   FROM SYS_DEPT B,REG_PATADM A " +
                    "  WHERE A.ADM_DATE=TO_DATE('" + admDate +
                    "','YYYY-MM-DD') " +
                    "    AND A.REGION_CODE = B.REGION_CODE " +
                    "    AND A.REALDEPT_CODE=B.DEPT_CODE  " +
                    whereAdmType +
                    whereSessionCode +
                    whereRegionCode +
                    "  ORDER BY ID,NAME,PY1";
        } else {
            sql =
                    " SELECT DISTINCT B.DEPT_CODE AS ID,B.DEPT_ENG_DESC AS NAME,B.PY1 AS PY1 " +
                    "   FROM SYS_DEPT B,REG_PATADM A " +
                    "  WHERE A.ADM_DATE=TO_DATE('" + admDate +
                    "','YYYY-MM-DD') " +
                    "    AND A.REGION_CODE = B.REGION_CODE " +
                    "    AND A.REALDEPT_CODE=B.DEPT_CODE  " +
                    whereAdmType +
                    whereSessionCode +
                    whereRegionCode +
                    "  ORDER BY ID,NAME,PY1";
        }
//    	System.out.println("getInsteadDept.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 取得如下条件的代诊医师的病人
     * @param admDate String
     * @param sessionCode String
     * @param drCode String 
     * @param admType String
     * @param deptCode String
     * @return TParm
     */
    public TParm getInsteadPatList(String admDate, String sessionCode,
                                   String drCode, String admType,
                                   String deptCode, boolean flg) {
        String sql = "";
        String OPT_USER = Operator.getID();//当前操作人
        //=============pangben 2012-6-28 start 添加初复诊显示字段
        //=============fux 20131101 start 添加预约挂号显示字段APPT_CODE只管门诊
        if ("O".equalsIgnoreCase(admType)) {
            sql = "SELECT A.QUE_NO, A.MR_NO, B.PAT_NAME, A.ADM_DATE, A.REALDEPT_CODE, A.CLINICROOM_NO, A.ADM_STATUS, A.REPORT_STATUS," +
                  "	   A.CASE_NO, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE, B.BIRTH_DATE, A.REALDR_CODE, " +
                  "	   B.PAT1_CODE, B.PAT2_CODE, B.PAT3_CODE, B.WEIGHT, B.PREMATURE_FLG," +
                  "	   B.HANDICAP_FLG, B.LMP_DATE, B.PREGNANT_DATE, B.BREASTFEED_STARTDATE, B.BREASTFEED_ENDDATE," +
                  "    A.ADM_TYPE, B.PAT_NAME1, A.VISIT_CODE, A.APPT_CODE,A.INS_PAT_TYPE " +
                  "	FROM REG_PATADM A, SYS_PATINFO B, SYS_CTZ C " +   
                  "	WHERE A.ADM_DATE = TO_DATE ('" + admDate +
                  "', 'YYYY-MM-DD') " + 
                  "AND A.CTZ1_CODE = C.CTZ_CODE(+)" +
                  "		  AND A.ARRIVE_FLG = 'Y'" + 
                  "		  AND REALDR_CODE='" + drCode + "'" +
                  "		  AND B.MR_NO=A.MR_NO " +
                  "		  AND A.REGCAN_USER IS NULL ";
            if(!flg){
            	if(!OPT_USER.equals(drCode)){
            		sql+= " AND C.SPECIAL_FLG = 'N' ";
            	}
            }
//            System.out.println("111111:" + sql);
        } else {
            sql = "SELECT A.ENTER_ROUTE, A.PATH_KIND, A.QUE_NO, A.MR_NO, B.PAT_NAME, A.ADM_DATE, A.REALDEPT_CODE, A.CLINICROOM_NO, A.ADM_STATUS, A.REPORT_STATUS," +
                  "	   A.CASE_NO, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE, B.BIRTH_DATE, A.REALDR_CODE," +
                  "	   B.PAT1_CODE, B.PAT2_CODE, B.PAT3_CODE, B.WEIGHT, B.PREMATURE_FLG," +
                  "	   B.HANDICAP_FLG, B.LMP_DATE, B.PREGNANT_DATE, B.BREASTFEED_STARTDATE, B.BREASTFEED_ENDDATE," +
                  "    A.ADM_TYPE, B.PAT_NAME1, MAX(ORDER_DATE) AS ORDER_DATE, D.TIME_LIMIT," +
                  "    A.VISIT_CODE, A.DISE_CODE, A.APPT_CODE,E.DISCHG_TYPE,A.REG_DATE,A.INS_PAT_TYPE " +//modify by wanglong 20121025 增加DISE_CODE 仅限急诊
                  /* modified by Eric 20170604 add TRIAGE_TIME, GATE_TIME*/
                  "    ,TO_CHAR (F.ADM_DATE, 'YYYY/MM/DD')||' '||TO_CHAR (F.COME_TIME, 'HH24:MI:SS') AS GATE_TIME, F.TRIAGE_TIME "+
                  
                  // add by wangqing 20170704
                  " ,D.LEVEL_DESC " +
                  
                  "	FROM REG_PATADM A, SYS_PATINFO B ,OPD_ORDER C,REG_ERD_LEVEL D,ERD_RECORD E , SYS_CTZ F " +
                  /* modified by Eric 20170604 add ERD_EVALUTION*/
                  " ,ERD_EVALUTION F "+
                  
                  "	WHERE A.ADM_DATE = TO_DATE ('" + admDate +
                  "', 'YYYY-MM-DD') " +
                  /* modified by Eric 20170604*/
                  " AND A.CASE_NO=F.CASE_NO(+) "+
                  "		  AND A.ARRIVE_FLG = 'Y'" +
                  //wuxy2017 1010
            	"AND A.CTZ1_CODE = F.CTZ_CODE(+)" ;
            if(!flg){
            	if(!OPT_USER.equals(drCode)){
            		sql+= " AND F.SPECIAL_FLG = 'N' ";
            	}
            }
            //end wuxy
            if (deptCode.length() > 0) {
                sql += " AND REALDEPT_CODE='" + deptCode + "'";
            }
            if (drCode.length() > 0) {
                sql += " AND REALDR_CODE='" + drCode + "'";
            }
            sql += "		  AND B.MR_NO=A.MR_NO " +
                    "		  AND A.REGCAN_USER IS NULL " +
                    "   AND A.CASE_NO=C.CASE_NO (+) " +
                    "   AND A.CASE_NO=E.CASE_NO (+) " +
                    
                    // add by wangqing 20170704
                    " AND A.ERD_LEVEL = D.LEVEL_CODE " +
                    
                    "   GROUP BY A.ENTER_ROUTE, A.PATH_KIND, A.QUE_NO, A.MR_NO, B.PAT_NAME, A.ADM_DATE, A.REG_DATE, A.REALDEPT_CODE, A.CLINICROOM_NO,A.ADM_STATUS,A.REPORT_STATUS," +
                    "		     A.CASE_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE, B.BIRTH_DATE,A.REALDR_CODE,A.REGION_CODE," +
                    "		    B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,B.PREMATURE_FLG," +
                    "		     B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,B.BREASTFEED_ENDDATE," +
                    "		     A.ADM_TYPE,B.PAT_NAME1,D.TIME_LIMIT," +
                    "            A.VISIT_CODE,A.DISE_CODE,A.APPT_CODE,E.DISCHG_TYPE,A.REG_DATE,A.INS_PAT_TYPE " +//modify by wanglong 20121025 增加DISE_CODE 仅限急诊
                    
                    /*modified by Eric 20170605*/
                    " ,F.ADM_DATE,F.COME_TIME,F.TRIAGE_TIME"+
                    " ,D.LEVEL_DESC " +
                    
                    "		    ORDER BY A.CASE_NO ";
//            System.out.println("222222:" + sql);
        }
        
      //=============pangben 2012-6-28 stop
//        System.out.println("sql============="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新挂号主档(FOR ONW)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateInfoForONW(TParm parm, TConnection conn) {
        TParm result = update("updateInfoForONW", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 按ID号,就诊日期查询病患就诊信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selPatInfoForREG(TParm parm) {
//        TParm result = query("selPatInfoForREG", parm);
    	
        String sql = "SELECT ADM_TYPE,CASE_NO,MR_NO,REGION_CODE,ADM_DATE," +
        		" REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,QUE_NO," +
        		" REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,REALDR_CODE," +
        		" APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,CTZ2_CODE," +
        		" CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,ERD_LEVEL,CONTRACT_CODE,ARRIVE_FLG," +
        		"  REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,DRG_CODE," +
        		" HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,HEIGHT," +
        		" CLINICTYPE_CODE,VIP_FLG,OPT_USER,OPT_DATE,OPT_TERM,SERVICE_LEVEL,CONFIRM_NO,INS_PAT_TYPE " +
        		" FROM REG_PATADM " +
        		" WHERE MR_NO IN ("+PatTool.getInstance().getMrRegMrNos(parm.getValue("MR_NO"))+") " +
        		" AND ADM_DATE BETWEEN TO_DATE ('"+parm.getValue("YY_START_DATE")+"', 'YYYYMMDD') " +
        		" AND TO_DATE ('"+parm.getValue("YY_END_DATE")+"', 'YYYYMMDD') " +
        		" AND ADM_TYPE ='"+parm.getValue("ADM_TYPE")+"'" +
        		" AND REGCAN_USER IS NULL " +
        		" AND REGION_CODE='"+parm.getValue("REGION_CODE")+"'" +
        		" AND  NOT (APPT_CODE = 'N' AND ARRIVE_FLG= 'N') " +
        		" ORDER BY CASE_NO";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 退挂写入退挂人员,退挂日期
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateForUnReg(TParm parm, TConnection connection) {
        TParm result = update("updateForUnReg", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据下列字段检索中西医标记
     * @param regionCode String
     * @param admType String
     * @param admDate String
     * @param sessionCode String
     * @param clinicRoomNo String
     * @return TParm
     */
    public TParm getWestMediFlg(String regionCode, String admType,
                                String admDate, String sessionCode,
                                String clinicRoomNo) {

        String sql = "SELECT WEST_MEDI_FLG " +
                     "	   FROM REG_SCHDAY " +
                     "	   WHERE REGION_CODE='" + regionCode + "' " +
                     "			 AND ADM_DATE='" + admDate + "' " +
                     "			 AND CLINICROOM_NO='" + clinicRoomNo + "'";
//        System.out.println("getWestMediFlg.sql-="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 退挂操作
     * @param parm TParm
     * @return TParm
     */
    public TParm onUnReg(TParm parm) {
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "onUnReg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新报道标记位
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForArrive(TParm parm, TConnection conn) {
        TParm result = update("updateForArrive", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    /**
     * 更新到院时间
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForArriveDate(TParm parm, TConnection conn) {
        TParm result = update("updateForArriveDate", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 按时间段查询挂号收入统计表平诊数据(FOR MEDTIIS)
     * @param sTime String
     * @param eTime String
     * @return TParm
     */
    public TParm selSummaryPersonO(String sTime, String eTime) {
        TParm parm = new TParm();
        parm.setData("S_TIME", sTime);
        parm.setData("E_TIME", eTime);
        TParm result = query("selSummaryPersonO", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 按时间段查询挂号收入统计表急诊数据(FOR MEDTIIS)
     * @param sTime String
     * @param eTime String
     * @return TParm
     */
    public TParm selSummaryPersonE(String sTime, String eTime) {
        TParm parm = new TParm();
        parm.setData("S_TIME", sTime);
        parm.setData("E_TIME", eTime);
        TParm result = query("selSummaryPersonE", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 按时间段查询挂号收入统计表退费数据(FOR MEDTIIS)
     * @param sTime String
     * @param eTime String
     * @return TParm
     */
    public TParm selSummaryPersonReturn(String sTime, String eTime) {
        TParm parm = new TParm();
        parm.setData("S_TIME", sTime);
        parm.setData("E_TIME", eTime);
        TParm result = query("selSummaryPersonReturn", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 通过mr_no拿到最大case_no
     * @param mrNo String
     * @param regionCode String
     * @return TParm
     */
    public TParm selMaxCaseNoByMrNo(String mrNo, String regionCode) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("MR_NO", mrNo);
        parm.setData("REGION_CODE", regionCode);
        result = this.query("selMaxCaseNoByMrNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新护士站身高,体重
     * @param parm TParm
     * @return TParm
     */
    public TParm updateWHForONW(TParm parm) {
        TParm result = new TParm();
        result = this.query("updateWHForONW", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询符合条件的可退挂的预约挂号信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectUnAdmData(TParm parm) {
        String sessionWhere = " AND A.SESSION_CODE = '" +
                              parm.getValue("SESSION_CODE") + "' ";
        String clinicRoomWhere = "    AND A.CLINICROOM_NO = '" +
                                 parm.getValue("CLINICROOM") + "' ";
        String regionWhere = "";
        if (parm.getData("REGION") != null)
            regionWhere = " AND A.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") + "' ";
        String sql =
                " SELECT 'N' AS FLG,B.PAT_NAME,A.REGION_CODE,A.ADM_TYPE,A.ADM_DATE," +
                "        A.SESSION_CODE,A.CLINICROOM_NO,A.QUE_NO,A.CASE_NO,A.MR_NO,C.CLINICROOM_DESC " +
                "   FROM REG_PATADM A, SYS_PATINFO B,REG_CLINICROOM C " +
                "  WHERE A.MR_NO = B.MR_NO " +
                "    AND A.APPT_CODE = 'Y' " +
                "    AND A.ARRIVE_FLG = 'N' " +
                "    AND A.SEE_DR_FLG = 'N' " +
                "    AND A.REGCAN_USER IS NULL " +
                "    AND A.CLINICROOM_NO = C.CLINICROOM_NO " +
                "    AND A.ADM_DATE = TO_DATE('" + parm.getValue("ADM_DATE") +
                "','yyyyMMdd')" +
                regionWhere;

        if (parm.getValue("SESSION_CODE").toString().length() != 0)
            sql = sql + sessionWhere;
        if (parm.getValue("CLINICROOM").toString().length() != 0)
            sql = sql + clinicRoomWhere;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 预约挂号 退挂 修改REG_PATADM表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateUnAdmREG_PATADM(TParm parm, TConnection conn) {
        String sql = " UPDATE REG_PATADM " +
                     "    SET REGCAN_USER  = '" + parm.getValue("REGCAN_USER") +
                     "', REGCAN_DATE = SYSDATE, " +
                     "  OPT_USER='" + parm.getValue("OPT_USER") + "'," +
                     "  OPT_TERM='" + parm.getValue("OPT_TERM") + "'," +
                     "  OPT_DATE=SYSDATE " +
                     "  WHERE  CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 预约挂号 退挂 修改REG_CLINICQUE
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateUnAdmREG_CLINICQUE(TParm parm, TConnection conn) {
        String sql = " UPDATE REG_CLINICQUE " +
                     "    SET QUE_STATUS = 'N', " +
                     "  OPT_USER='" + parm.getValue("OPT_USER") + "'," +
                     "  OPT_TERM='" + parm.getValue("OPT_TERM") + "'," +
                     "  OPT_DATE=SYSDATE " +
                     "  WHERE ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "' " +
                     "    AND ADM_DATE = '" + parm.getValue("ADM_DATE") + "'" +
                     "    AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") +
                     "' " +
                     "    AND CLINICROOM_NO = '" +
                     parm.getValue("CLINICROOM_NO") + "' " +
                     "    AND QUE_NO = '" + parm.getValue("QUE_NO") + "' ";
//        System.out.println("sql:"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 医疗卡退挂
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateUnAdmReg(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.updateUnAdmREG_PATADM(parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        result = this.updateUnAdmREG_CLINICQUE(parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询挂号收据打印数据
     * @param CASE_NO String
     * @param COPY String
     * @return TParm
     */
    public TParm getRegPringDate(String CASE_NO, String COPY) {
        TParm result = new TParm();
        String sql =
                "SELECT  B.DEPT_CODE,A.CASE_NO,B.DEPT_CHN_DESC,A.QUE_NO,A.MR_NO,C.USER_NAME,ADM_DATE, " +
                " D.PAT_NAME,E.CHN_DESC AS SEX,D.BIRTH_DATE,F.CTZ_DESC,A.CLINICTYPE_CODE,A.ADM_TYPE, " +
                " A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.SERVICE_LEVEL,G.DESCRIPTION AS AREA_DESCRIPTION, " +
                " H.SESSION_DESC,I.CLINICROOM_DESC,A.INS_PAT_TYPE " + //add by huangtt 20170504 INS_PAT_TYPE
                " FROM REG_PATADM A,SYS_DEPT B,SYS_OPERATOR C,SYS_PATINFO D,SYS_DICTIONARY E," +
                "      SYS_CTZ F,REG_CLINICAREA G,REG_SESSION H,REG_CLINICROOM I " +
                " WHERE A.DEPT_CODE=B.DEPT_CODE " +
                " AND A.DR_CODE=C.USER_ID " +
                " AND A.MR_NO=D.MR_NO " +
                " AND A.CLINICAREA_CODE = G.CLINICAREA_CODE(+) " +
                " AND A.ADM_TYPE = H.ADM_TYPE " +
                " AND A.CLINICROOM_NO = I.CLINICROOM_NO " +
                " AND A.SESSION_CODE = H.SESSION_CODE " +
                " AND E.GROUP_ID='SYS_SEX' " +
                " AND D.SEX_CODE=E.ID(+) " +
                " AND A.CTZ1_CODE=F.CTZ_CODE(+) " +
                " AND A.CASE_NO='" + CASE_NO + "' ";
        TParm reg = new TParm();
        reg.setData(TJDODBTool.getInstance().select(sql));
        String sessionDesc = reg.getValue("SESSION_DESC", 0);
        String adm_type = reg.getValue("ADM_TYPE", 0); //门急住别
        String CLINICTYPE_CODE = reg.getValue("CLINICTYPE_CODE", 0); //号别
        String level = reg.getValue("SERVICE_LEVEL", 0); //服务等级
        String levelSql =
                " SELECT CHN_DESC AS NAME " +
                "   FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SERVICE_LEVEL' AND ID = '" +
                level + "' ";
        TParm levelParm = new TParm(TJDODBTool.getInstance().select(levelSql));
        String levelDesc = levelParm.getValue("NAME", 0);
        String roomDesc = reg.getValue("CLINICROOM_DESC", 0);
        //查询诊查费
//        double clinic_fee = BIL.getRegDetialFee(adm_type, CLINICTYPE_CODE,
//                                                "CLINIC_FEE", level);
        //查询诊查费的ORDER_CODE
//        TParm order1 = PanelTypeFeeTool.getInstance().getOrderCodeDetial(
//                adm_type, CLINICTYPE_CODE, "CLINIC_FEE");
        //查询诊查费的身份折扣
//        double ownrate1 = BIL.getOwnRate(reg.getValue("CTZ1_CODE", 0),
//                                         reg.getValue("CTZ2_CODE", 0),
//                                         reg.getValue("CTZ3_CODE", 0),
//                                         SysFee.getChargeHospCode(order1.
//                getValue("ORDER_CODE", 0)));
        //查询挂号费
//        double reg_fee = BIL.getRegDetialFee(adm_type, CLINICTYPE_CODE,
//                                             "REG_FEE", level);
        //查询挂号费的ORDER_CODE
//        TParm order2 = PanelTypeFeeTool.getInstance().getOrderCodeDetial(
//                adm_type, CLINICTYPE_CODE, "REG_FEE");
        //查询挂号费的身份折扣
//        double ownrate2 = BIL.getOwnRate(reg.getValue("CTZ1_CODE", 0),
//                                         reg.getValue("CTZ2_CODE", 0),
//                                         reg.getValue("CTZ3_CODE", 0),
//                                         SysFee.getChargeHospCode(order2.
//                getValue("ORDER_CODE", 0)));
        DecimalFormat df = new DecimalFormat("0.00");
        result.setData("CLINICROOM_DESC_OPB", roomDesc); //诊室ForOPB
        result.setData("CLINICTYPE_CODE", CLINICTYPE_CODE); //号别  add by huangtt 20160616      
        result.setData("REGFEE", df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level))); //挂号费
        
        //modify by huangtt 20170504 门特诊察费加10元
        TParm inParm = new TParm();
    	inParm.setData("INS_TYPE", reg.getValue("INS_PAT_TYPE", 0));
    	double mtFee = this.getMTClinicFee(inParm).getDouble("fee");
        
        result.setData("CLINICFEE", df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)+mtFee)); //诊查费
        
       
        result.setData("DR_CODE_OPB", reg.getValue("USER_NAME", 0)); //医师ForOPB
        result.setData("QUE_NO_OPB", reg.getValue("QUE_NO", 0)); //诊号ForOPB
        result.setData("DEPT_CODE_OPB", reg.getValue("DEPT_CHN_DESC", 0)); //科室ForOPB
        result.setData("DEPT", "TEXT",
                       reg.getValue("DEPT_CHN_DESC", 0) + " " + roomDesc); //科室
        result.setData("CASE_NO", "TEXT", reg.getValue("CASE_NO", 0)); //门诊号
        result.setData("SEQ", "TEXT", reg.getValue("QUE_NO", 0)); //顺序号
        result.setData("MR_NO", "TEXT", reg.getValue("MR_NO", 0)); //ID号
        result.setData("DR_CODE", "TEXT", reg.getValue("USER_NAME", 0)); //医师
        result.setData("ADM_DATE", "TEXT",
                       StringTool.getString(reg.getTimestamp("ADM_DATE", 0),
                                            "dd/MM/yyyy") + " " + sessionDesc); //挂号日期
        result.setData("PAT_NAME", "TEXT", reg.getValue("PAT_NAME", 0)); //病患姓名
        result.setData("SEX", "TEXT", reg.getValue("SEX", 0)); //性别
        result.setData("AGE", "TEXT",
                       StringUtil.showAge(reg.getTimestamp("BIRTH_DATE", 0),
                                          reg.getTimestamp("ADM_DATE", 0))); //年龄
        result.setData("CTZ1_CODE", "TEXT", reg.getValue("CTZ_DESC", 0));
        result.setData("REG_FEE", "TEXT",
                       df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)) + "(元)"); //挂号费
        

        result.setData("CLINIC_FEE", "TEXT",
                       df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)+mtFee) + "(元)"); //诊查费
        
        result.setData("TOTAL", "TEXT", df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level) +
                                                  BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)+mtFee));
        
        result.setData("Overall_pay", "TEXT", "0.00");
        result.setData("Individual_pay", "TEXT",
                       df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level) +
                                 BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)));
        result.setData("cash", "TEXT", "0.00");
        result.setData("CASH", "TEXT", "" + StringUtil.getInstance().
                       numberToWord(StringTool.
                                    round(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level) +
                                          BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level), 2)));
        result.setData("REG_USER", "TEXT", Operator.getName());
        result.setData("PRINT_DATE", "TEXT",
                       StringTool.getString(SystemTool.getInstance().getDate(),
                                            "yyyy/MM/dd"));

        result.setData("PRINT_TIME", "TEXT",
                       StringTool.getString(SystemTool.getInstance().getDate(),
                                            "HH:mm:ss"));
        //服务等级
        result.setData("SERVICE_LEVEL", "TEXT", levelDesc);
        //诊区备注
        result.setData("AREA_DESCRIPTION", "TEXT",
                       reg.getValue("AREA_DESCRIPTION", 0));
        result.setData("REGION_CODE", "TEXT",
                       Manager.getOrganization().
                       getHospitalCHNFullName(Operator.getRegion()));
        result.setData("TITLE_ENGLISH", "TEXT",
                       Manager.getOrganization().
                       getHospitalENGFullName(Operator.getRegion()));
        result.setData("COPY", "TEXT", COPY);
        //System.out.println("result---"+result);
        return result;
    }

    /**
     * 根据日班表信息 查询每个日班医师的挂号信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getSchDayRegInfo(TParm parm) {
        String sql =
                "SELECT CASE WHEN REGCAN_DATE IS NULL THEN 'N' ELSE 'Y' END AS REGCAN_FLG, " +
                " A.QUE_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.TEL_HOME,A.CASE_NO,A.REGION_CODE,C.SESSION_DESC " +
                " FROM REG_PATADM A,SYS_PATINFO B,REG_SESSION C " +
                " WHERE A.MR_NO=B.MR_NO " +
                " AND A.SESSION_CODE=C.SESSION_CODE " +
                " AND A.REGION_CODE='" + parm.getValue("REGION_CODE") + "' " +
                " AND A.ADM_TYPE='" + parm.getValue("ADM_TYPE") + "' " +
                " AND A.ADM_DATE=TO_DATE('" + parm.getValue("ADM_DATE") +
                "','YYYYMMDD') " +
                " AND A.SESSION_CODE='" + parm.getValue("SESSION_CODE") + "' " +
                " AND A.CLINICROOM_NO='" + parm.getValue("CLINICROOM_NO") +
                "' AND REGCAN_USER IS NULL OR REGCAN_USER=''" +
                " ORDER BY QUE_NO ";
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 新增挂号(医疗卡付费使用)
     * @param reg Reg 挂号
     * @return boolean true 成功 false 失败
     */
    public boolean newRegForEKT(Reg reg) {
        if (reg == null)
            return false;
        String newCaseNo = reg.caseNo();
        String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
                "RECEIPT_NO",
                "RECEIPT_NO");
        if (newCaseNo == null || newCaseNo.length() == 0) {
            err("-1 取问诊号错误!");
            return false;
        }
        reg.setCaseNo(newCaseNo); //1就诊序号(REG)
        reg.getRegReceipt().setCaseNo(newCaseNo); //1就诊序号(REG_RECEIPT)
        reg.getRegReceipt().setReceiptNo(receiptNo); //2挂号收据(REG_RECEIPT)
        reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); //3票据明细档收据号(BIL_INVRCP)

        //门急诊主档
        TParm regParm = reg.getParm();
        TParm saveParm = new TParm();
        saveParm.setData("REG", regParm.getData());

        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "onNewRegForEKT", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    //测试用
    public static void main(String args[]) {
        JavaHisDebug.initClient();
//        System.out.println("就诊序号" +
//                           PatAdmTool.getInstance().getCaseByPatName("张三"));
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", "O");
        parm.setData("ADM_DATE", "2008-10-16");
        parm.setData("SESSION_CODE", "2");
        parm.setData("DEPT_CODE", "201004");
        parm.setData("DR_CODE", "admin");
        parm.setData("SEE_DR", "Y");
        //System.out.println(PatAdmTool.getInstance().selDateForODOByWait(parm));

    }

    /**
     * 通过mr_no拿到case_no pangben 20111009
     * @param parm TParm
     * @return TParm
     */
    public TParm selEKTByMrNo(TParm parm) {
        TParm result = new TParm();
        result = this.query("selEKTByMrNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 挂号主档医疗卡绿色通道金额修改
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * =================pangben 20111009
     */
    public TParm updateEKTGreen(TParm parm, TConnection conn) {
        TParm result = update("updateEKTGreen", parm, conn);
        return result;
    }

    /**
     * 医疗卡绿色通道扣款操作
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * =================pangben 20111009
     */
    public TParm updateEKTGreen1(TParm parm, TConnection conn) {
        TParm result = update("updateEKTGreen1", parm, conn);
        return result;
    }
    
    /**
     * 要更新 检伤评估信息档 ERD_EVALUTION add by huangtt 20151020
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateErdEvalution(TParm parm,TConnection conn){
    	TParm result = update("updateErdEvalution", parm, conn);
        return result;
    }
    
    /**
     * 保存检伤评估信息档 ERD_EVALUTION add by huangtt 20151020
     * @param parm
     * @param conn
     * @return
     */
    public TParm insertErdEvalution(TParm parm,TConnection conn){
    	TParm result = update("insertErdEvalution", parm, conn);
        return result;
    }
    /**
     * 修改检伤评估信息档的检伤等级 ERD_EVALUTION add by huangtt 20151020
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateErdLevelCode(TParm parm,TConnection conn){
    	TParm result = update("updateErdLevelCode", parm, conn);
        return result;
    }
    
    public TParm updateRegErdLevel(TParm parm,TConnection conn){
    	TParm result = update("updateRegErdLevel", parm, conn);
        return result;
    }
    
    /**
     * 补入检伤等级时，有CASE_NO 要更新reg_patad表中的TRIAGE_NO,LEVEL_CODE
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateRegPatadmErd(TParm parm,TConnection conn){
    	TParm result = update("updateRegPatadmErd", parm, conn);
        return result;
    }
    
    /**
     * 保存修改检伤评估信息档LOG表 ERD_LEVEL_LO add by huangtt 20151020
     * @param parm
     * @param conn
     * @return
     */
    public TParm insertErdLevelLog(TParm parm,TConnection conn){
    	TParm result = update("insertErdLevelLog", parm, conn);
        return result;
    }
    
    
    /**
     * 更新离院时间
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateErdEvalutionOutDate(TParm parm,TConnection conn){
    	TParm result = update("updateErdEvalutionOutDate", parm, conn);
        return result;
    }
    
    /**
     * 
     * @return
     */
    public TParm getMTClinicFee(TParm insParm){
    	
    	double fee = 0;
    	String mrCliniFeeCode ="";
    	if(insParm != null){
    		if(insParm.getValue("INS_TYPE").length() > 0 &&
    				(insParm.getValue("INS_TYPE").equals("2")||insParm.getValue("INS_TYPE").equals("3"))){
        		 mrCliniFeeCode = REGSysParmTool.getInstance().selectdata().getValue("MT_CLINIC_FEE_CODE", 0);		
        		if(mrCliniFeeCode.length() > 0){
        			String sql = "SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"+mrCliniFeeCode+"' AND ACTIVE_FLG='Y'";
        			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        			if(parm.getCount() > 0){
        				fee = parm.getDouble("OWN_PRICE", 0);
        			}
        			
        		}
    		}

    		
    	}
    	TParm parm = new TParm();
    	parm.setData("fee", fee);
    	parm.setData("mrCliniFeeCode", mrCliniFeeCode);
		return parm;
    }

}
