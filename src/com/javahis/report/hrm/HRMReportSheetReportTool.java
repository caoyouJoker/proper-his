package com.javahis.report.hrm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.IReport;
import com.javahis.util.StringUtil;
/**
 * <p> Title: 导览单报表数据准备类 </p>
 * 
 * <p> Description: 导览单报表数据准备类  </p>
 * 
 * <p> Copyright: ProperSoft 20130730 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class HRMReportSheetReportTool implements IReport {

    /**
     * 准备报表传参
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        String mrNo = parm.getValue("MR_NO");
        String caseNo = parm.getValue("CASE_NO");
        TParm result=new TParm();
        if(StringUtil.isNullString(mrNo)||StringUtil.isNullString(caseNo)){
            //out.println("mrNo,caseNo is null");
            return null;
        }
        TParm patParm=getPatParm(caseNo);//从hrm_patadm中取人员信息
        if(patParm==null){
            //out.println(":patParm is null");
            return null;
        }
        //=====================modify by wanglong 20130204
        result.setData("COMPANY_DESC", patParm.getValue("COMPANY_DESC", 0));
        result.setData("COMPANY_DESC", "TEXT", patParm.getValue("COMPANY_DESC", 0));
        result.setData("CONTRACT_DESC", patParm.getValue("CONTRACT_DESC", 0));
        result.setData("CONTRACT_DESC", "TEXT", patParm.getValue("CONTRACT_DESC", 0));
        result.setData("PACKAGE_DESC", patParm.getValue("PACKAGE_DESC", 0));
        result.setData("PACKAGE_DESC", "TEXT", patParm.getValue("PACKAGE_DESC", 0));
        result.setData("SEQ_NO", patParm.getValue("SEQ_NO", 0));
        result.setData("SEQ_NO", "TEXT", patParm.getValue("SEQ_NO", 0));
        result.setData("MR_NO", mrNo);
        result.setData("PAT_NAME", "TEXT", patParm.getValue("PAT_NAME", 0));
        result.setData("SEX_CODE", "TEXT", patParm.getValue("SEX_DESC", 0));
        result.setData("BIRTH_DATE",
                       StringTool.getString(patParm.getTimestamp("BIRTHDAY", 0), "yyyy/MM/dd"));
        result.setData("BILL_FLG", "TEXT", patParm.getValue("BILL_FLG", 0).equals("1") ? "是" : "否");// 结算状态
        result.setData("MARRIAGE_DESC", "TEXT", patParm.getValue("MARRIAGE_DESC", 0));// 婚姻状态
        result.setData("TEL", "TEXT", patParm.getValue("TEL", 0));// 电话add by wanglong 20130412
        result.setData("PAT_DEPT", patParm.getValue("PAT_DEPT", 0));// 部门add by wanglong 20130225
        if (!patParm.getValue("PAT_DEPT", 0).trim().equals("")) {// add by wanglong 20130225
            result.setData("DEPT_TITLE", "TEXT", "部门:");
            result.setData("PAT_DEPT", "TEXT", patParm.getValue("PAT_DEPT", 0));
            result.setData("DEPT_TITLE", "部门:");
            result.setData("PAT_DEPT", patParm.getValue("PAT_DEPT", 0));
        }
        result.setData("STAFF_NO", patParm.getValue("STAFF_NO", 0));// 工号add by wanglong 20130225
        if (!patParm.getValue("STAFF_NO", 0).trim().equals("")) {// add by wanglong 20130225
            result.setData("STAFF_TITLE", "TEXT", "员工号:");
            result.setData("STAFF_NO", "TEXT", patParm.getValue("STAFF_NO", 0));
            result.setData("STAFF_TITLE", "员工号:");
            result.setData("STAFF_NO", patParm.getValue("STAFF_NO", 0));
        }
        String age =
                StringTool.CountAgeByTimestamp(patParm.getTimestamp("BIRTHDAY", 0), SystemTool
                        .getInstance().getDate())[0];
        result.setData("AGE", "TEXT", age);// 年龄
        if (!patParm.getValue("ID_NO", 0).matches("\\d{15}|\\d{18}|\\d{14}[xX]|\\d{17}[xX]")//add by wanglong 20130530
                || !StringTool.isId(patParm.getValue("ID_NO", 0))) {
            result.setData("ID_NO", "TEXT",
                           StringTool.getString(patParm.getTimestamp("BIRTHDAY", 0), "yyyy/MM/dd"));// 外国人显示出生日期
        } else {
            result.setData("ID_NO", "TEXT", patParm.getValue("ID_NO", 0));// 身份证号
        }
        //=====================modify end
        result.setData("MR_CODE", "TEXT", mrNo);
        String introUser = patParm.getValue("INTRO_USER", 0);
        String reportlist = patParm.getValue("REPORTLIST", 0);
        String introUserName =
                StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" + introUser + "'");
        String reportlistName = this.getReportlist(reportlist);
        String packageCode = patParm.getValue("PACKAGE_CODE", 0);
        if (reportlistName == null) {
            reportlistName = "";
        }
        result.setData("INTRO_USER", introUserName);
        result.setData("REPORTLIST", "体检顺序：" + reportlistName);
        TParm deptParm = getDeptTParm(caseNo, packageCode);
        if (deptParm != null) {
            if (deptParm.getErrCode() != 0) {
                return deptParm;
            }
            if (deptParm.getData("IN") != null) {
                result.setData("IN_TABLE", ((TParm) deptParm.getData("IN")).getData());// 内科
            }
            if (deptParm.getData("OUT") != null) {
                result.setData("OUT_TABLE", ((TParm) deptParm.getData("OUT")).getData());// 外科
            }
            if (deptParm.getData("EYE") != null) {
                result.setData("EYE_TABLE", ((TParm) deptParm.getData("EYE")).getData());// 眼科Ⅰ、眼科Ⅱ共用
            }
            if (deptParm.getData("FACE_WOMAN") != null) {
                result.setData("FACE_WOMAN_TABLE", ((TParm) deptParm.getData("FACE_WOMAN")).getData());// 五官科Ⅰ、五官科、妇科Ⅱ共用
            }
        }
        TParm orderParm = this.getReportSheet(caseNo, packageCode);
        if (orderParm != null) {
            if (orderParm.getErrCode() != 0) {
                return null;
            }
            result.setData("ORDER_TABLE", orderParm.getData());
        }
        return result;
    }
    
    /**
     * 根据给入CASE_NO查询PAT数据，在导览单显示用
     * @param caseNo
     * @return
     */
    private TParm getPatParm(String caseNo) {
        TParm parm = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        //导览单中个人资料
        String sql =//modify by wanglong 20130225
                "SELECT A.COMPANY_CODE,E.COMPANY_DESC,A.CONTRACT_CODE,F.CONTRACT_DESC,A.PACKAGE_CODE,B.PACKAGE_DESC,"
                        + "F.SEQ_NO,F.STAFF_NO,A.PAT_NAME,A.BIRTHDAY,A.SEX_CODE,C.CHN_DESC SEX_DESC,A.ID_NO,"
                        + "A.MARRIAGE_CODE,D.MARRIAGE_DESC,F.PAT_DEPT,A.REPORTLIST,A.INTRO_USER,A.BILL_FLG,F.TEL "//modify by wanglong 20130412
                        + " FROM HRM_PATADM A,HRM_PACKAGEM B,SYS_DICTIONARY C,"
                        + "      (SELECT ID,CHN_DESC AS MARRIAGE_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_MARRIAGE') D,"
                        + "      HRM_COMPANY E, HRM_CONTRACTD F "
                        + "WHERE A.CASE_NO='#' "
                        + "  AND A.PACKAGE_CODE=B.PACKAGE_CODE(+) "
                        + "  AND A.MARRIAGE_CODE=D.ID(+) "
                        + "  AND A.SEX_CODE=C.ID "
                        + "  AND C.GROUP_ID='SYS_SEX' "
                        + "  AND A.COMPANY_CODE = E.COMPANY_CODE "
                        + "  AND A.CONTRACT_CODE = F.CONTRACT_CODE "
                        + "  AND A.MR_NO = F.MR_NO";
        sql = sql.replaceFirst("#", caseNo);
        // System.out.println("getPatParm.sql="+sql);
        parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() != 0) {
            // System.out.println("getPatParm.errText="+parm.getErrText());
            return null;
        }
        int count = parm.getCount();
        if (count <= 0) {
            // System.out.println("getPatParm.count <=0");
            return null;
        }
        return parm;
    }

    /**
     * 根据给入REPORTLIST，得到该队列内容
     * @param reportList
     * @return
     */
    public String getReportlist(String reportList) {
        StringBuffer sb = new StringBuffer();
        if (StringUtil.isNullString(reportList)) {
            return null;
        }
        String sql = "SELECT ID||'.'||CHN_DESC NAME FROM SYS_DICTIONARY WHERE GROUP_ID='#' ORDER BY SEQ".replaceFirst("#", reportList);
        // System.out.println("getReportlist=="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() != 0 || parm.getCount() <= 0) {
            return null;
        }
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            sb.append(parm.getValue("NAME", i)).append("  ");
        }
        return sb.toString();
    }
    
    /**
     * 
     * @param caseNo
     * @return
     */
    private TParm getDeptTParm(String caseNo, String packageCode) {
        TParm result = new TParm();// modify by wanglong 20130131
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        //导览单中分科TABLE的数据
//        private static final String GET_DEPT_TABLE_TPARM="SELECT B.CHN_DESC ,'' OPT_USER,A.DR_NOTE " +
//                "FROM HRM_ORDER A, SYS_DICTIONARY B,HRM_PACKAGED C  " +
//                "WHERE A.CASE_NO='#' " +
//                "AND A.DEPT_ATTRIBUTE IS NOT NULL " +
//                "AND A.DEPT_ATTRIBUTE = B.ID(+) " +
//                "AND B.GROUP_ID='SYS_DEPT_ATTRIBUTE' " +
//                "AND A.SETMAIN_FLG='Y' " +
//                "AND A.ORDER_CODE=C.ORDER_CODE(+) " +
//                "AND C.PACKAGE_CODE='#' " +
//                "ORDER BY C.CHECK_SEQ ";
//        String sqlN =   "SELECT   B.CHN_DESC, '' OPT_USER, A.DR_NOTE "+
//                " FROM   HRM_ORDER A, SYS_DICTIONARY B "+
//                " WHERE       A.CASE_NO = '"+caseNo+"'"+
//                " AND A.DEPT_ATTRIBUTE IS NOT NULL "+
//                " AND A.DEPT_ATTRIBUTE = B.ID(+) "+
//                " AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "+
//                " AND A.SETMAIN_FLG = 'Y' ORDER BY A.SEQ_NO";
        String sql =// modify by wanglong 20130207
                "SELECT DISTINCT DEPT_ATTRIBUTE, B.CHN_DESC "
                        + "  FROM HRM_ORDER A, SYS_DICTIONARY B "
                        + " WHERE A.CASE_NO = '" + caseNo
                        + "'  AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "
                        + "   AND A.DEPT_ATTRIBUTE = B.ID " 
                        + " UNION "
                        + "SELECT B.ORDER_CODE DEPT_ATTRIBUTE, B.ORDER_DESC CHN_DESC "
                        + "  FROM HRM_ORDER A, SYS_FEE B "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND A.SETMAIN_FLG = 'Y' " 
                        + "   AND A.CASE_NO = '" + caseNo + "'" 
                        + " UNION"
                        + " SELECT A.ORDER_CODE DEPT_ATTRIBUTE,A.ORDER_DESC CHN_DESC FROM HRM_ORDER A WHERE A.ORDER_CODE='Y1010039' AND A.CASE_NO = '" + caseNo + "' ";
//        String sql = // modify by wanglong 20130131
//                "SELECT DISTINCT DEPT_ATTRIBUTE, B.CHN_DESC "
//                        + "  FROM HRM_ORDER A, SYS_DICTIONARY B " 
//                        + " WHERE A.CASE_NO = '"
//                        + caseNo
//                        + "'  AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "
//                        + "   AND A.DEPT_ATTRIBUTE = B.ID "
//                        + "ORDER BY A.DEPT_ATTRIBUTE";
        //System.out.println(":::"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            result.setErrCode(-1);
            result.setErrText("必须开立总检类型的医嘱");
            return null;
        }
        //====================================modify by wanglong 20130131
        Map content = new HashMap();
        for (int i = 0; i < count; i++) {
        	if(result.getValue("DEPT_ATTRIBUTE",i).equals("Y1010039") || result.getValue("DEPT_ATTRIBUTE",i).equals("Y1009011")){//如果是 HPV、TCT医嘱也要带出 妇科模板
        		content.put("02", "妇科");
        		if(result.getValue("DEPT_ATTRIBUTE",i).equals("Y1010039")){
        			content.put("Y1010039", result.getValue("CHN_DESC", i));
        		}
        		if(result.getValue("DEPT_ATTRIBUTE",i).equals("Y1009011")){
        			content.put("Y1009011", result.getValue("CHN_DESC", i));
        		}
        		continue;
        	}
            content.put(result.getValue("DEPT_ATTRIBUTE", i), result.getValue("CHN_DESC", i));
            
        }
        TParm parm = new TParm();
        if (content.containsKey("03")) {// 内科
            TParm inParm = new TParm();// 内科
            inParm.addData("COL1", " 内 科");
            inParm.addData("COL2", "血压:            mmHg");
            inParm.setData("TABLE_VALUE", 0, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            inParm.addData("COL1", "心脏:");
            inParm.addData("COL2", "肺脏：");
            inParm.setData("TABLE_VALUE", 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
            inParm.setCount(inParm.getCount("COL1"));
            inParm.addData("SYSTEM", "COLUMNS", "COL1");
            inParm.addData("SYSTEM", "COLUMNS", "COL2");
            parm.setData("IN", inParm);
        }
        TParm outParm = new TParm();// 外科
        if (content.containsKey("05")) {// 外科
            outParm.addData("COL1", " 外 科");
            outParm.addData("COL2", "身高(cm):        体重(kg):");
            outParm.addData("COL3", "     体重指数(kg/㎡):");
            outParm.setData("TABLE_VALUE", outParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            outParm.addData("COL1", "皮肤:");
            outParm.addData("COL2", "胸部:");
            outParm.addData("COL3", "脊柱、四肢:");
            outParm.setData("TABLE_VALUE", outParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            outParm.addData("COL1", "表浅淋巴结:");
            outParm.addData("COL2", "");
            outParm.addData("COL3", "");
            outParm.setData("TABLE_VALUE", outParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
       
        outParm.setCount(outParm.getCount("COL1"));
        outParm.addData("SYSTEM", "COLUMNS", "COL1");
        outParm.addData("SYSTEM", "COLUMNS", "COL2");
        outParm.addData("SYSTEM", "COLUMNS", "COL3");
        parm.setData("OUT", outParm);
        TParm eyeParm = new TParm();// 眼科
        if (content.containsKey("Y0409002")) {// 眼科Ⅰ
            eyeParm.addData("COL1", " 眼 科Ⅰ");
            eyeParm.addData("COL2", "");
            eyeParm.setData("TABLE_VALUE", 0, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            eyeParm.addData("COL1", "眼底:");
            eyeParm.addData("COL2", "裂隙灯检查:");
            eyeParm.setData("TABLE_VALUE", 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
        }
        if (content.containsKey("Y1009007")) {// 眼科Ⅱ
            eyeParm.addData("COL1", " 眼 科Ⅱ");
            eyeParm.addData("COL2", "");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            eyeParm.addData("COL1", "视力:  左          右");
            eyeParm.addData("COL2", "色觉检查:                       眼底:");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
            eyeParm.addData("COL1", "矫正:  左          右");
            eyeParm.addData("COL2", "裂隙灯检查:");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
        }
        if(content.containsKey("U0201084")){
        	eyeParm.addData("COL1", " 眼 科Ⅲ");
            eyeParm.addData("COL2", "");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            eyeParm.addData("COL1", "眼底:");
            eyeParm.addData("COL2", "裂隙灯检查:");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
            eyeParm.addData("COL1", "眼压:");
            eyeParm.addData("COL2", "");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
        }
        eyeParm.setCount(eyeParm.getCount("COL1"));
        eyeParm.addData("SYSTEM", "COLUMNS", "COL1");
        eyeParm.addData("SYSTEM", "COLUMNS", "COL2");
        parm.setData("EYE", eyeParm);
        TParm faceWomanParm = new TParm();// 五官科或妇科
        if (content.containsKey("Y1009008")) {// 五官科Ⅰ
            faceWomanParm.addData("COL1", " 耳鼻喉Ⅰ");
            faceWomanParm.addData("COL2", "");
            faceWomanParm.addData("COL3", "");
            faceWomanParm.setData("TABLE_VALUE", 0, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            faceWomanParm.addData("COL1", "外耳道:");
            faceWomanParm.addData("COL2", "鼓膜:");
            faceWomanParm.addData("COL3", "鼻:");
            faceWomanParm.setData("TABLE_VALUE", 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            faceWomanParm.addData("COL1", "咽喉:");
            faceWomanParm.addData("COL2", "舌:");
            faceWomanParm.addData("COL3", "");
            faceWomanParm.setData("TABLE_VALUE", 2, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        if (content.containsKey("U0101007")) {// 五官科Ⅱ
            faceWomanParm.addData("COL1", " 耳鼻喉Ⅱ");
            faceWomanParm.addData("COL2", "");
            faceWomanParm.addData("COL3", "");
            faceWomanParm.setData("TABLE_VALUE", faceWomanParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            faceWomanParm.addData("COL1", "听力:  左          右");
            faceWomanParm.addData("COL2", "外耳道:");
            faceWomanParm.addData("COL3", "鼓膜:");
            faceWomanParm.setData("TABLE_VALUE", faceWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            faceWomanParm.addData("COL1", "鼻:");
            faceWomanParm.addData("COL2", "咽喉:");
            faceWomanParm.addData("COL3", "舌:");
            faceWomanParm.setData("TABLE_VALUE", faceWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        if (content.containsKey("02")) {// 妇科
            faceWomanParm.addData("COL1", " 妇 科");
            faceWomanParm.addData("COL2", "");
            faceWomanParm.addData("COL3", "");
            faceWomanParm.setData("TABLE_VALUE", faceWomanParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            faceWomanParm.addData("COL1", "外阴:");
            faceWomanParm.addData("COL2", "阴道:");//modify by wanglong 20130521
            faceWomanParm.addData("COL3", "宫颈:");
            faceWomanParm.setData("TABLE_VALUE", faceWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            String typeName = "";// add by wanglong 20130521
            if (content.containsKey("Y1009011") && content.containsKey("Y1010039")) {//modify by wanglong 20130909
                typeName = "TCT，HPV";
            } else if (content.containsKey("Y1009011")) {// *TCT
                typeName = "TCT";
            } else if (content.containsKey("Y1010039")) {// *HPV
                typeName = "HPV";
            } else {
                typeName = "刮片";
            }
            faceWomanParm.addData("COL1", "病理种类：" + typeName);// modify by wanglong 20130521
            faceWomanParm.addData("COL2", "病理号:");
            faceWomanParm.addData("COL3", "");
            faceWomanParm.setData("TABLE_VALUE", faceWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        faceWomanParm.setCount(faceWomanParm.getCount("COL1"));
        faceWomanParm.addData("SYSTEM", "COLUMNS", "COL1");
        faceWomanParm.addData("SYSTEM", "COLUMNS", "COL2");
        faceWomanParm.addData("SYSTEM", "COLUMNS", "COL3");
        parm.setData("FACE_WOMAN", faceWomanParm);
        //====================================modify by wanglong 20130131
        return parm;
    }
    
    /**
     * 根据CASE_NO取得导览单数据
     * @param caseNo
     * @return
     */
    public TParm getReportSheet(String caseNo, String packageCode) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            // System.out.println("caseNo is not null");
            return null;
        }
        //取得导览单数据 xueyf add(就诊号，集合医嘱主项，报告类别不为空,只查LIS,RIS的)
//        String sql =
//                ("SELECT A.ORDER_DESC,'' EXEC_DR,'' COL1,'' COL2, A.ORDER_CODE, A.DR_NOTE "
//                        + " FROM HRM_ORDER A " 
//                        + "WHERE A.CASE_NO = '#' "
//                        + "  AND A.SETMAIN_FLG = 'Y' " 
//                        + "  AND A.RPTTYPE_CODE IS NOT NULL "
//                        + "  AND (A.CAT1_TYPE='LIS' OR A.CAT1_TYPE='RIS') "
//                        + "ORDER BY A.ORDER_CODE").replaceFirst("#",caseNo);
        String sql = // modify by wanglong 20130131
                "SELECT A.ORDER_CODE, A.ORDER_DESC, A.GOODS_DESC, A.EXEC_DEPT_CODE, B.CATEGORY_CODE, B.CATEGORY_CHN_DESC "//modify by wanglong 20130308
                        + " FROM HRM_ORDER A, SYS_CATEGORY B "
                        + "WHERE A.CASE_NO = '"
                        + caseNo
                        + "' AND A.CAT1_TYPE IN ('LIS','RIS') "
                        + "  AND A.EXEC_DEPT_CODE IS NOT NULL "
                        + "  AND A.SETMAIN_FLG = 'Y' "
                        + "  AND SUBSTR( A.ORDER_CODE, 0, 3) = B.CATEGORY_CODE "
                        + "  AND B.RULE_TYPE='EXM_RULE' "
                        + "  AND A.ORDER_CODE NOT IN ('Y1010039') " //不显示*HPV add by wanglong 20130521
                        + "ORDER BY A.ORDER_CODE DESC";
        result=new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("result===="+result);
        if (result.getErrCode() != 0) {
            // System.out.println("result.getErrCode="+result.getErrText());
            return null;
        }
        int count=result.getCount();
         if (count < 1) {
                return null;
            }
         //=================================modify by wanglong 20130131
        final int COL_NUM = 4;
        TParm items = new TParm();
        String[] depts = new String[count];
        for (int i = 0; i < count; i++) {
            items.addData(result.getValue("CATEGORY_CHN_DESC", i), result.getValue("ORDER_DESC", i));
            depts[i] = result.getValue("CATEGORY_CHN_DESC", i);
        }
        int lisCount=items.getCount("检验科");
        for (int i = 0; i < lisCount; i++) {
            if (items.getValue("检验科", i).matches(".*尿.*")) {
                items.addData("检验科", items.getValue("检验科", i));
                items.removeRow(i, "检验科");
            }
        }
        for (int i = 0; i < lisCount; i++) {
            if (items.getValue("检验科", i).matches(".*妊娠.*")) {
                items.addData("检验科", items.getValue("检验科", i));
                items.removeRow(i, "检验科");
            }
        }
        for (int i = 0; i < lisCount; i++) {
            if (items.getValue("检验科", i).matches(".*便.*")) {
                items.addData("检验科", items.getValue("检验科", i));
                items.removeRow(i, "检验科");
            }
        }
        int uscCount=items.getCount("超声科");
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*心.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*腹.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*盆.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*甲.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*乳.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*其它.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*颈.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("超声科", i).matches(".*肢.*")) {
                items.addData("超声科", items.getValue("超声科", i));
                items.removeRow(i, "超声科");
            }
        }
        depts = arrayUnique(depts);
        TParm orderParm = new TParm();
        for (int i = 0; i < depts.length; i++) {
            orderParm.addData("COL1", " " + depts[i]);
            for (int j = 2; j <= COL_NUM; j++) {
                orderParm.addData("COL" + j, "");
            }
            orderParm.setData("TABLE_VALUE", orderParm.getCount("COL1") - 1,
                              "HLine=N;#0.Bold=Y;#0.FontSize=11");
            int deptOrders = ceil(items.getCount(depts[i]), COL_NUM) * COL_NUM;
            for (int k = 0; k < deptOrders; k++) {
                orderParm.addData("COL" + (k % COL_NUM + 1), items.getValue(depts[i], k));
                orderParm.setData("TABLE_VALUE", orderParm.getCount("COL1") - 1,
                                  "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y;#3.Bold=Y");
            }
        }
        orderParm.setCount(orderParm.getCount("COL1"));
        orderParm.addData("SYSTEM", "COLUMNS", "COL1");
        orderParm.addData("SYSTEM", "COLUMNS", "COL2");
        orderParm.addData("SYSTEM", "COLUMNS", "COL3");
        orderParm.addData("SYSTEM", "COLUMNS", "COL4");
        return orderParm;
        //=========================modify by wanglong 20130131
    }
    
    /**
     * 去除数组中重复的记录    add by wanglong 20130131
     * @param a
     * @return
     */
    public String[] arrayUnique(String[] a) {
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * 除法向上取整    add by wanglong 20130131
     * @return
     */
    public int ceil(int a, int b) {
        return (double) a / (double) b > a / b ? a / b + 1 : a / b;
    }
}
