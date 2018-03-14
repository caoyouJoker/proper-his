package jdo.odo;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import jdo.ekt.EKTIO;
import jdo.opd.OPDSysParmTool;
import jdo.reg.PatAdmTool;
import jdo.sys.DeptTool;
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: ����ҽ������վ����ǩTool</p> 
 *
 * <p>Description:����ҽ������վ����ǩTool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author ehui 20090503
 * @version 1.0
 * ===============pangben 20110718 �޸ı���ȥ��Ӣ��
 */
public class OpdRxSheetTool
    extends TJDOTool {
    //�����鴦������
    private static final String EXA = "5";
    //���ô�������
    private static final String OP = "4";
    //������֪ͨ������
    private static final String exaName = "������֪ͨ��";
    //����֪ͨ������
    private static final String opName = "����֪ͨ��";
    private static final String exaEngName = "Exam Checkin List";
    private static final String opEngName = "Treatment Checkin List";
    ODO odo;
    TParm reg, pat;
    TDataStore order, exa, drug, op, chnorder;
    Map unit, freq, route;
    //ȡ�������code+"	"+desc
    private static final String GET_MAIN_DIAG_SQL =
        " SELECT A.ICD_CODE,B.ICD_CHN_DESC ,A.DIAG_NOTE,B.ICD_ENG_DESC" +
        "	FROM OPD_DIAGREC A,SYS_DIAGNOSIS B ";
    //ȡ��ҩ������
    private static final String GET_ORG_NAME_SQL =
        "SELECT  B.DEPT_CHN_DESC||' '||B. DEPT_ENG_DESC" +
        "	FROM OPD_ORDER A,SYS_DEPT B ";
    //ȡ���������
    private static final String GET_OPD_ORDER = " SELECT * FROM OPD_ORDER";

    //ȡ�ô���ǩ�ż�����ǩ����
    private static final String GET_RX_SQL =
        "SELECT RX_NO,RX_TYPE,COUNT(RX_NO) AS COUNT" +
        "	FROM OPD_ORDER" +
        "  WHERE CASE_NO='<>' " +
        "	GROUP BY RX_NO,RX_TYPE " +
        "	ORDER BY RX_TYPE,RX_NO";
    private static final String GET_COUNTER_DESC = "SELECT COUNTER_DESC,COUNTER_ENG_DESC FROM PHA_COUNTERNO WHERE ORG_CODE='#' AND COUNTER_NO='#' AND CHOSEN_FLG='Y'";
    //����MR_NOȡ��Ӣ����
    private static final String GET_PAT_ENG_NAME =
        "SELECT PAT_NAME1 FROM SYS_PATINFO WHERE MR_NO='#'";
    //��ѯ�Һ�������ʵ�ʿ���ҽʦ
    private static final String GET_REALREG_DR = "SELECT REALDR_CODE FROM REG_PATADM WHERE CASE_NO = '#'";
    //��ѯ�Һ������Ŀ���ҽʦ
    private static final String GET_REG_DR = "SELECT DR_CODE FROM REG_PATADM WHERE CASE_NO = '#'";
    /*
     * ����ǩʹ����Ϣ
     * �������ơ�֧����ʽ���������ơ��Ա���ϡ�������ơ�ʱ�����ơ�����
     */
    /**
     * ʵ��
     */
    public static OpdRxSheetTool instanceObject;
    /**
     * �õ�ʵ��
     * @return OpdRxSheetTool
     */
    public static OpdRxSheetTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OpdRxSheetTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public OpdRxSheetTool() {
        onInit();
    }

    /**
     * ���عҺ�������Ϣ
     * @param caseNo
     * @return TParm
     */
    public TParm getRegPatAdm(String caseNo) {
        if (reg != null)
            return reg;
        reg = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);
        return reg;
    }

    /**
     * ���ز���������Ϣ
     * @param mrNo
     * @return
     */
    public TParm getPatInfo(String mrNo) {
//        if (pat != null)
//            return pat;
        pat = PatTool.getInstance().getInfoForMrno(mrNo);
        return pat;
    }

    /**
     *
     * @param caseNo
     * @return
     */
    public TDataStore getOpdOrder(String caseNo) {
        order = new TDataStore();
        order.setSQL(GET_OPD_ORDER + " WHERE CASE_NO='" + caseNo + "' ");
        order.retrieve();
        return order;
    }
    /**
     *
     * @param caseNo
     * @return
     */
    public TDataStore getOpdOrder(String caseNo,String rxNo) {
        order = new TDataStore();
        order.setSQL(GET_OPD_ORDER + " WHERE CASE_NO='" + caseNo + "' AND RX_NO='"+ rxNo +"'");
        order.retrieve();
        return order;
    }

    /**
     * ���ò�������
     * @param patName String
     */
    public String getPatName(String mrNo) {
        return PatTool.getInstance().getNameForMrno(mrNo);
    }

    /**
     * ���ݲ����Ų�ѯӢ����
     * @param mrNo
     * @return
     */
    public String getPatEngName(String mrNo) {
        String patName1 = "";
        if (StringUtil.isNullString(mrNo)) {
            return patName1;
        }
        String sql = GET_PAT_ENG_NAME.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        patName1 = result.getValue("PAT_NAME1", 0);
        return patName1;
    }

    /**
     * ���ݲ����Ų�ѯ�������֤��
     * @param mrNo
     * @return
     */
    public String getId(String mrNo) {
        return PatTool.getInstance().getIdnoForMrno(mrNo);
    }

    /**
     * ���ø��ʽ
     * @param payTypeName
     */
    public String getPayTypeName(String ctzCode) {
        String payTypeName = "";
        String sql = "SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='" + ctzCode +
            "'";
        //System.out.println("getPayTypesql="+sql);
        payTypeName = new TParm(TJDODBTool.getInstance().select(sql)).getValue(
            "CTZ_DESC", 0);
        //System.out.println("payTypeName="+payTypeName);
        return payTypeName;
    }

    /**
     * ���ݸ�����ݲ�ѯ��ӦӢ������
     * @param cztCode
     * @return
     */
    public String getPayTypeEngName(String ctzCode) {

        String payTypeName = "";
        String sql = "SELECT ENG_DESC FROM SYS_CTZ WHERE CTZ_CODE='" + ctzCode +
            "'";
//		System.out.println("getPayTypesql="+sql);
        payTypeName = new TParm(TJDODBTool.getInstance().select(sql)).getValue(
            "ENG_DESC", 0);
        //System.out.println("payTypeName="+payTypeName);
        return payTypeName;

    }

    /**
     * ���ò�������
     * @param deptName String
     */
    public String getDeptName(String deptName) {
        return DeptTool.getInstance().getDescByCode(deptName);
    }

    /**
     * ���ݴ���ȡ�ò���Ӣ������
     * @param deptCode
     * @return
     */
    public String getDeptEngName(String deptCode) {
        return DeptTool.getInstance().getDeptEngDesc(deptCode);
    }

    /**
     * ȡ�������Ա�
     * @param sexName String
     */
    public String getSexName(String mrNo) {
        return DictionaryTool.getInstance().getName("SYS_SEX",
            this.getPatInfo(mrNo).getValue("SEX_CODE", 0));
    }

    /**
     * ȡ��Ӣ���Ա�
     * @param mrNo
     * @return
     */
    public String getSexEngName(String mrNo) {
        return DictionaryTool.getInstance().getEnName("SYS_SEX",
            this.getPatInfo(mrNo).getValue("SEX_CODE", 0));
    }

    /**
     * ������ʾ�ڴ���ǩ�ϵ����
     */
    public String getIcdName(String caseNo) {
//		//System.out.println("ICD ======================NAME========="+GET_MAIN_DIAG_SQL+" WHERE A.ICD_CODE = B.ICD_CODE AND A.CASE_NO='"+caseNo+"' AND A.MAIN_DIAG_FLG='Y' ");
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            GET_MAIN_DIAG_SQL +
            " WHERE A.ICD_CODE = B.ICD_CODE AND A.CASE_NO='" + caseNo +
            "' AND A.MAIN_DIAG_FLG='Y' "));
        String icdName = parm.getValue("ICD_CHN_DESC", 0);
        String diagNote= parm.getValue("DIAG_NOTE", 0); //add by huangtt 20160121
        if (StringUtil.isNullString(icdName)) {
            icdName = parm.getValue("DIAG_NOTE", 0);
        }
        else{   //add by huangtt 20160121 start
        	 
        	if(diagNote.length() > 0){
        		icdName=icdName+"("+diagNote+")";
        	}
        	
        } //add by huangtt 20160121 end
        String ci = getIcdName2(caseNo);
        if(ci.length()>0)
            return icdName + "; "+ci;
        else
            return icdName;
    }
    /**
     * ��ȡ���������
     * @param caseNo String
     * @return String
     */
    public String getIcdName2(String caseNo){
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            GET_MAIN_DIAG_SQL +
            " WHERE A.ICD_CODE = B.ICD_CODE AND A.CASE_NO='" + caseNo +
            "' AND A.MAIN_DIAG_FLG='N' "));
        String name = "";
        if(parm.getCount()<=0){
            return name;
        }
        if(parm.getCount()==1){
            String icdName = parm.getValue("ICD_CHN_DESC", 0);
            String diagNote= parm.getValue("DIAG_NOTE", 0); //add by huangtt 20160121
            if (StringUtil.isNullString(icdName)) {
                icdName = parm.getValue("DIAG_NOTE", 0);
            }
            else{   //add by huangtt 20160121 start
           	 
            	if(diagNote.length() > 0){
            		icdName=icdName+"("+diagNote+")";
            	}
            	
            } //add by huangtt 20160121 end
            name = icdName;
        }
        if(parm.getCount()>1){
            String icdName = parm.getValue("ICD_CHN_DESC", 0);
            String diagNote= parm.getValue("DIAG_NOTE", 0); //add by huangtt 20160121
            if (StringUtil.isNullString(icdName)) {
                icdName = parm.getValue("DIAG_NOTE", 0);
            }
            else{   //add by huangtt 20160121 start
           	 
            	if(diagNote.length() > 0){
            		icdName=icdName+"("+diagNote+")";
            	}
            	
            } //add by huangtt 20160121 end
            name = icdName;
            icdName = parm.getValue("ICD_CHN_DESC", 1);
            diagNote= parm.getValue("DIAG_NOTE", 1); //add by huangtt 20160121
            if (StringUtil.isNullString(icdName)) {
                icdName = parm.getValue("DIAG_NOTE", 1);
            }
            else{   //add by huangtt 20160121 start
           	 
            	if(diagNote.length() > 0){
            		icdName=icdName+"("+diagNote+")";
            	}
            	
            } //add by huangtt 20160121 end
            name += "; " + icdName;
        }
        return name;
    }
    /**
     * ������ʾ�ڴ���ǩ�ϵ�Ӣ�����
     * @param caseNo String
     * @return String
     */
    public String getIcdEngName(String caseNo) {
        String sql = GET_MAIN_DIAG_SQL +
            " WHERE A.ICD_CODE = B.ICD_CODE AND A.CASE_NO='" + caseNo +
            "' AND A.MAIN_DIAG_FLG='Y' ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        String icdName = parm.getValue("ICD_ENG_DESC", 0);
        if (StringUtil.isNullString(icdName)) {
            icdName = parm.getValue("DR_NOTE", 0);
        }
        String ci = getIcdEngName2(caseNo);
        if(ci.length()>0)
            return icdName + "; "+ci;
        else
            return icdName;
    }
    /**
     * ������ʾ�ڴ���ǩ�ϵ�Ӣ�����
     * @param caseNo String
     * @return String
     */
    public String getIcdEngName2(String caseNo) {
        String sql = GET_MAIN_DIAG_SQL +
            " WHERE A.ICD_CODE = B.ICD_CODE AND A.CASE_NO='" + caseNo +
            "' AND A.MAIN_DIAG_FLG='N' ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        String name = "";
        if(parm.getCount()<=0){
            return name;
        }
        if(parm.getCount()==1){
            String icdName = parm.getValue("ICD_ENG_DESC", 0);
            if (StringUtil.isNullString(icdName)) {
                icdName = parm.getValue("DIAG_NOTE", 0);
            }
            name = icdName;
        }
        if(parm.getCount()>1){
            String icdName = parm.getValue("ICD_ENG_DESC", 0);
            if (StringUtil.isNullString(icdName)) {
                icdName = parm.getValue("DIAG_NOTE", 0);
            }
            name = icdName;
            icdName = parm.getValue("ICD_ENG_DESC", 1);
            if (StringUtil.isNullString(icdName)) {
                icdName = parm.getValue("DIAG_NOTE", 1);
            }
            name += "; " + icdName;
        }
        return name;
    }

    /**
     * �õ������
     * @param clinicName String
     */
    public String getClinicName(String caseNo) {
        // SELECT B.CLINICROOM_DESC FROM REG_PATADM A ,REG_CLINICROOM B
        return StringUtil.getDesc("REG_CLINICROOM", "CLINICROOM_DESC",
                                  " CLINICROOM_NO='" +
                                  getRegPatAdm(caseNo).
                                  getValue("CLINICROOM_NO", 0) + "' ");
    }

    /**
     * �õ�Ӣ�������
     * @param caseNo
     * @return
     */
    public String getClinicEngName(String caseNo) {
        return StringUtil.getDesc("REG_CLINICROOM", "ENG_DESC",
                                  " CLINICROOM_NO='" +
                                  getRegPatAdm(caseNo).
                                  getValue("CLINICROOM_NO", 0) + "' ");
    }

    /**
     * �õ�ʱ����
     * @param sessionName String
     */
    public String getSessionName(String caseNo) {

        return StringUtil.getDesc("REG_SESSION", "SESSION_DESC",
                                  " SESSION_CODE='" +
                                  getRegPatAdm(caseNo).
                                  getValue("SESSION_CODE", 0) + "' ");
    }

    /**
     * �õ�ʱ������Ӣ�ģ�
     * @param caseNo
     * @return
     */



    public String getSessionEngName(String caseNo) {
        return StringUtil.getDesc("REG_SESSION", "ENG_DESC",
                                  " SESSION_CODE='" +
                                  getRegPatAdm(caseNo).
                                  getValue("SESSION_CODE", 0) + "' ");
    }

    /**
     * ������ʾ����
     * @param caseNo
     * @param mrNo
     * @return
     */
    public String getAgeName(String caseNo, String mrNo) {
        return OdoUtil.showAge(getPatInfo(mrNo).getTimestamp("BIRTH_DATE", 0),
                               getRegPatAdm(caseNo).getTimestamp("ADM_DATE", 0));
    }

    /**
     * ������ʾ����(Ӣ��)
     * @param caseNo
     * @param mrNo
     * @return
     */
    public String getAgeEngName(String caseNo, String mrNo) {
        return OdoUtil.showEngAge(getPatInfo(mrNo).getTimestamp("BIRTH_DATE", 0),
                                  getRegPatAdm(caseNo).getTimestamp("ADM_DATE",
            0));
    }

    /**
     * ���س�������
     * @param mrNo
     * @return
     */
    public String getBirthDays(String mrNo) {
        return StringTool.getString(getPatInfo(mrNo).getTimestamp("BIRTH_DATE",
            0), "yyyy/MM/dd");
    }

    /**
     * ����ҽԺȫ��
     * @return String
     */
    public String getHospFullName() {
        return Operator.getHospitalCHNFullName();
    }

    /**
     * ���ز��˸��ʽ
     * @param caseNo
     * @return String
     *
     */
    public String getOrgName(String caseNo, String rxNo) {
        //System.out.println("caseNo======"+caseNo);
        //System.out.println("rxNo======"+rxNo);
        //System.out.println("getOrgName.sql=========="+GET_ORG_NAME_SQL+" WHERE A.CASE_NO='"+caseNo+"' AND A.RX_NO='"+rxNo+"' AND A.EXEC_DEPT_CODE=B.DEPT_CODE");
        TParm parm = new TParm(TJDODBTool.getInstance().select(GET_ORG_NAME_SQL +
            " WHERE A.CASE_NO='" + caseNo + "' AND A.RX_NO='" + rxNo +
            "' AND A.EXEC_DEPT_CODE=B.DEPT_CODE"));
        //System.out.println("getOrgName.parm========="+parm);
        return parm.getValue("DEPT_CHN_DESC", 0);
    }

    /**
     * ����ҽʦ����
     * @param caseNo
     * @return String
     */
    public String getDrName(String caseNo) {
        return StringUtil.getDesc("SYS_OPERATOR", "USER_NAME",
                                  "USER_ID='" +
                                  this.getOpdOrder(caseNo).
                                  getItemString(0, "DR_CODE") + "'");
    }

    /**
     * ����ҽʦ����
     * @param caseNo
     * @return String
     */
    public String getDrName(String caseNo,String rxNo) {
        return StringUtil.getDesc("SYS_OPERATOR", "USER_NAME",
                                  "USER_ID='" +
                                  this.getOpdOrder(caseNo,rxNo).
                                  getItemString(0, "DR_CODE") + "'");
    }
    /**
     * ���ؾ���ʱ��
     * @param caseNo
     * @return
     */
    public String getOrderDate(String caseNo) {
        return StringTool.getString(this.getRegPatAdm(caseNo).getTimestamp(
            "ADM_DATE", 0), "yyyy/MM/dd");
    }
    
    /**
     * ���عҺ�����(����)  add by wanglong 20121011
     * @param caseNo
     * @return
     */
    public String getRegDate(String caseNo) {
    	return StringTool.getString(this.getRegPatAdm(caseNo).getTimestamp(
                "REG_DATE", 0), "yyyy/MM/dd HH:mm");
    }
    
    /**
     * ��������
     * @param caseNo
     * @return
     */
    public String getSubjRec(String caseNo) {
        return StringUtil.getDesc("OPD_SUBJREC", "SUBJ_TEXT",
                                  " CASE_NO='" + caseNo + "'");
    }

    /**
     * ��������
     * @param caseNo
     * @return
     */
    public String getPhysExamRec(String caseNo) {
        return StringUtil.getDesc("OPD_SUBJREC", "PHYSEXAM_REC",
                                  " CASE_NO='" + caseNo + "'");
    }

    /**
     * ���ؿ���
     * @param caseNo
     * @return
     */
    public String getObjRec(String caseNo) {
        return StringUtil.getDesc("OPD_SUBJREC", "OBJ_TEXT",
                                  " CASE_NO='" + caseNo + "'");
    }
    /**
     * ������ز�ʷ������ʷ��
     * @param mrNo String
     * @return String
     */
    public String getRelatedHistory(String mrNo){
        return StringUtil.getDesc("SYS_PATINFO", "FAMILY_HISTORY",
                                  " MR_NO='" + mrNo + "'");
    }
    /**
     * ���ؼ�����������ֲ�ʷ��
     * @param mrNo String
     * @return String
     */
    public String getExaResult(String caseNo){
        return StringUtil.getDesc("OPD_SUBJREC", "EXA_RESULT",
                                  " CASE_NO='" + caseNo + "'");
    }
    /**
     * ���ؽ��飨�����ֲ�ʷ��
     * @param mrNo String
     * @return String
     */
    public String getProposal(String caseNo){
        return StringUtil.getDesc("OPD_SUBJREC", "PROPOSAL",
                                  " CASE_NO='" + caseNo + "'");
    }
    /**
     * ��ʼ��Ƶ�Σ��÷�����λ
     */
    public void initBaseOrder() {
        TDataStore tunit, troute, tfreq;
        tunit = new TDataStore();
        tunit.setSQL("SELECT * FROM SYS_UNIT");
        tunit.retrieve();
        for (int i = 0; i < tunit.rowCount(); i++) {
            unit.put(tunit.getItemData(i, "UNIT_CODE"),
                     tunit.getItemData(i, "UNIT_CHN_DESC"));
        }

        troute = new TDataStore();
        troute.setSQL("SELECT * FROM SYS_PHAROUTE");
        troute.retrieve();
        for (int i = 0; i < troute.rowCount(); i++) {
            route.put(troute.getItemData(i, "ROUTE_CODE"),
                      troute.getItemData(i, "ROUTE_CHN_DESC"));
        }
        tfreq = new TDataStore();
        tfreq.setSQL("SELECT * FROM SYS_PHAFREQ");
        tfreq.retrieve();
        for (int i = 0; i < tfreq.rowCount(); i++) {
            freq.put(tfreq.getItemData(i, "FREQ_CODE"),
                     tfreq.getItemData(i, "FREQ_CHN_DESC"));
        }
    }

    /**
     * ���ݴ������ͷ��ش�����ʾ����
     * @param rxType
     * @return
     */
    public String getExaOrOpName(String rxType) {
        //System.out.println("rxType=============="+rxType);
        if (OP.equalsIgnoreCase(rxType))
            return opName;
        if (EXA.equalsIgnoreCase(rxType)) {
            //System.out.println("exaName="+exaName);
            return exaName;
        }
        return "";
    }

    /**
     * ȡ��Ӣ�ĵ���
     * @param rxType
     * @return
     */
    public String getExaOrOpEngName(String rxType) {
        //System.out.println("rxType=============="+rxType);
        if (OP.equalsIgnoreCase(rxType))
            return opEngName;
        if (EXA.equalsIgnoreCase(rxType)) {
            //System.out.println("exaName="+exaName);
            return exaEngName;
        }
        return "";
    }

    public void initBaseInfo() {
        OpdRxSheetTool.getInstance().initBaseOrder();
        unit = OpdRxSheetTool.getInstance().unit;
        freq = OpdRxSheetTool.getInstance().freq;
        route = OpdRxSheetTool.getInstance().route;
    }

    /**
     * ���ظ��ִ���ǩ������������ʹ��õĴ����ŵ�ÿ�ִ���������
     * @param caseNo
     * @return
     */
    public TParm getRxNo(String caseNo) {
        String sql = GET_RX_SQL.replaceAll("<>", caseNo);
        //System.out.println("print sql==========="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ���ز�����ʾ��ʱ�䣬���磺��Send On 20090514 At 112400��
     * @return
     */
    public String getPrintDate() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String time = StringTool.getString(now, "yyyyMMdd HHmmss");
        String text = "Send On " + time.substring(0, time.indexOf(" ")) +
            " At " + time.substring(time.indexOf(" "));
        return text;
    }

    /**
     * ȡ�ô�ӡ�����������
     * @param rxParm
     * @return
     */
    public TParm getExaPrintParm(TParm rxParm, String realDeptCode,
                                 String rxType, ODO odo) {
        TParm inParam = new TParm();
        String text = "TEXT";

//        inParam.setData("ADDRESS", text,
//                        "OpdExaSheet From " + Operator.getID() + " To LPT1");
        inParam.setData("PRINT_TIME", text,
                        StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                             "yyyy/MM/dd HH:mm:ss"));
        inParam.setData("HOSP_NAME", text,
                        OpdRxSheetTool.getInstance().getHospFullName());
    //    inParam.setData("HOSP_NAME_ENG", text, Operator.getHospitalENGShortName());
        inParam.setData("SHEET_NAME", text,
                        OpdRxSheetTool.getInstance().getExaOrOpName(rxType));
//        inParam.setData("SHEET_NAME_ENG", text,
//                        OpdRxSheetTool.getInstance().getExaOrOpEngName(rxType));
        inParam.setData("PAY_TYPE", text,
                        OpdRxSheetTool.getInstance().
                        getPayTypeName(odo.getRegPatAdm().getCtz1Code()));
//        inParam.setData("PAY_TYPE_ENG", text,
//                        OpdRxSheetTool.getInstance().
//                        getPayTypeEngName(odo.getRegPatAdm().getCtz1Code()));
        inParam.setData("MR_NO", text, "������:" + odo.getMrNo());
     //   inParam.setData("MR_NO_ENG", text, "Pat No:" + odo.getMrNo());
        inParam.setData("PAT_NAME", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().getPatName(odo.getMrNo()));
        
        inParam.setData("PAY_TYPE", text,
                "�ѱ�:" +
                OpdRxSheetTool.getInstance().
                getPayTypeName(odo.getRegPatAdm().
                               getItemString(0, "CTZ1_CODE"))); 
        inParam.setData("DIAG", text, "�ٴ���ϣ�" + getIcdName(odo.getCaseNo()));
//        inParam.setData("PAT_NAME_ENG", text,
//                        "Name:" +
//                        OpdRxSheetTool.getInstance().getPatName(odo.getMrNo()));
        inParam.setData("SEX_CODE", text,
                        "�Ա�:" +
                        OpdRxSheetTool.getInstance().getSexName(odo.getMrNo()));
//        inParam.setData("SEX_CODE_ENG", text,
//                        "Gender:" +
//                        OpdRxSheetTool.getInstance().getSexEngName(odo.getMrNo()));
        inParam.setData("BIRTHDAY", text,
                        "��������:" +
                        OpdRxSheetTool.getInstance().getBirthDays(odo.getMrNo()));
//        inParam.setData("BBB", text,
//                        "Birthday:" +
//                        OpdRxSheetTool.getInstance().getBirthDays(odo.getMrNo()));
        inParam.setData("AGE", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().
                        getAgeName(odo.getCaseNo(), odo.getMrNo()));
//        inParam.setData("AGE_ENG", text,
//                        "Age:" +
//                        OpdRxSheetTool.getInstance().
//                        getAgeEngName(odo.getCaseNo(), odo.getMrNo()));
        inParam.setData("DEPT_CODE", text,
                        "�Ʊ�:" +
                        OpdRxSheetTool.getInstance().getDeptName(realDeptCode));
//        inParam.setData("DEPT_CODE_ENG", text,
//                        "Dept:" +
//                        OpdRxSheetTool.getInstance().getDeptEngName(realDeptCode));
        inParam.setData("CLINIC_ROOM", text,
                        "���:" +
                        OpdRxSheetTool.getInstance().getClinicName(odo.getCaseNo()));
//        inParam.setData("CLINIC_ROOM_ENG", text,
//                        "Exam Room:" +
//                        OpdRxSheetTool.getInstance().
//                        getClinicEngName(odo.getCaseNo()));
        inParam.setData("DR_CODE", text, "ҽ��:" + this.GetRegDr(odo.getCaseNo()));
      //  inParam.setData("DR_CODE_ENG", text, "M.D.:" + this.GetRegDrEng(odo.getCaseNo()));
//        inParam.setData("ADM_DATE", text,
//                        "����ʱ��:" +
//                        OpdRxSheetTool.getInstance().getOrderDate(odo.getCaseNo()));
        //=========pangben 2013-2-21 ��ӡ�����鴦��ǩ�ϵľ���ʱ�����ߵ����ص�
        String admTypeCode = odo.getRegPatAdm().getItemString(0, "ADM_TYPE");
        if ("O".equalsIgnoreCase(admTypeCode)) {// "�� "
        	 String admDate =StringTool.getString(this.getRegPatAdm(odo.getCaseNo()).getTimestamp("ADM_DATE", 0),
                                    "yyyy/MM/dd");
            inParam.setData("ADM_DATE", text, "����ʱ��:" + admDate);
        } else if ("E".equalsIgnoreCase(admTypeCode)) {// "�� "
        	  String regDate =StringTool.getString(this.getRegPatAdm(odo.getCaseNo()).getTimestamp("REG_DATE", 0),
                                     "yyyy/MM/dd");
            inParam.setData("ADM_DATE", text, "����ʱ��:" + regDate);
        }
        // ================add end================================
        inParam.setData("SESSION_CODE", text,
                        "ʱ��:" +
                        OpdRxSheetTool.getInstance().getSessionName(odo.getCaseNo()));
        inParam.setData("BAR_CODE", text, odo.getMrNo());
        int count = rxParm.getCount("RX_NO");
        String rxNo = "";
        String rxNoLast = "";//��¼�������һ�Ŵ���ǩ��
        double amt = 0;//���
        double orginalAmt = 0;//���
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                rxNo += "'" + rxParm.getValue("RX_NO", i) + "',";
                if(rxNoLast.compareTo(rxParm.getValue("RX_NO", i))<0){
                    rxNoLast = rxParm.getValue("RX_NO", i);
                }
                orginalAmt += getArAmt(rxParm.getValue("RX_NO", i), odo.getOpdOrder());
//                System.out.println("orginalAmt~~~~~~~~:"+orginalAmt);
            }
            rxNo = rxNo.substring(0, rxNo.lastIndexOf(","));
        }
        else {
            inParam.setData("ORDER_PARM", rxParm.getData("ORDER_PARM"));
        }
        inParam.setData("RX_NO", rxNo);
        inParam.setData("CASE_NO", odo.getCaseNo());
        inParam.setData("RX_TYPE", rxType);
        inParam.setData("FOOT_DR", text, "ҽʦ:" + this.GetRealRegDr(odo.getCaseNo()));
        inParam.setData("FOOT_DR_CODE", text,
                        "ҽʦ����:" + new TParm(TJDODBTool.getInstance().select(GET_REALREG_DR.replaceFirst("#", odo.getCaseNo()))).
                                  getValue("REALDR_CODE",0));
        inParam.setData("BALANCE", text,
                        "�������:" + orginalAmt);
//        System.out.println("rxNo:"+rxNo);
//        System.out.println("rxNoLast:"+rxNoLast);
        //֪ͨ��������ʾ�š���===============================================================modify by huangjw 20150302
		String orderType = "", orderTypeCode = odo.getRegPatAdm()
				.getItemString(0, "ADM_TYPE");
		if ("O".equalsIgnoreCase(orderTypeCode)) {
			orderType = "�� ";
		} else if ("E".equalsIgnoreCase(orderTypeCode)) {
			orderType = "�� ";
		}
		String birthDate = odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE")
				+ "";
		if ("2".equalsIgnoreCase(rxType) || "1".equalsIgnoreCase(rxType)) {
			String sql = "SELECT B.PRNSPCFORM_FLG,B.CTRLDRUGCLASS_CODE,B.PRN_TYPE_DESC "
					+ " FROM PHA_BASE A,SYS_CTRLDRUGCLASS B "
					+ " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE(+) AND A.ORDER_CODE='"
					+ odo.getOpdOrder().getItemString(0, "ORDER_CODE") + "' ";
			TParm prn = new TParm(TJDODBTool.getInstance().select(sql));
			orderType = prn.getValue("PRN_TYPE_DESC", 0);
		} else if (!StringUtil.isNullString(birthDate)
				&& OPDSysParmTool.getInstance().isChild(
						odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE"))) {
			orderType = "�� ";
		} else if ("3".equalsIgnoreCase(rxType)) {
			orderType = "��ҽ ";
		}
		inParam.setData("ORDER_TYPE", text, orderType);
        //==================================================================================
        amt = EKTIO.getInstance().getRxBalance(odo.getCaseNo(), rxNoLast);
        inParam.setData("CURRENT_AMT", text, "�����:" + amt);
        double RxOrig = EKTIO.getInstance().getRxOriginal(odo.getCaseNo(), rxNoLast);
        inParam.setData("ORINGINAL_AMT", text, "��ԭ��:" + StringTool.round(RxOrig, 2));
        inParam.setData("CHARGE", text, "���οۿ�:" + StringTool.round(RxOrig-amt, 2));
        return inParam;
    }
    /**
     * У���Ƿ�Ϊ��
     * @param data
     * @return
     */
    private String checkIsNull(String data){
    	if (null==data) {
			return "";
		}
    	return data;
    }
    /**
     * ȡ�ô���ǩ����
     * @param rxParm
     * @param realDeptCode
     * @param rxType
     * @param odo
     * @return
     */
    public TParm getOrderPrintParm(String realDeptCode, String rxType, ODO odo,
                                   String rxNo, String psyFlg) {
    	//System.out.println("================sql1 time start=================="+new Date());
        TParm inParam = new TParm();
        String text = "TEXT";
        int printNum = getPrintNum(odo.getCaseNo(),rxNo);//��ӡ����
        inParam.setData("Print.fs",printNum>0?printNum:1);//Ĭ�ϴ�ӡ����
      
//        inParam.setData("ADDRESS", text,
//                        "OpdExaSheet From " + Operator.getID() + " To LPT1");
        inParam.setData("PRINT_TIME", text,
                        StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                             "yyyy/MM/dd HH:mm:ss"));
        inParam.setData("HOSP_NAME", text,
                        OpdRxSheetTool.getInstance().getHospFullName());
    //    inParam.setData("HOSP_NAME_ENG", text, Operator.getHospitalENGShortName());
        
        
        inParam.setData("SHEET_NAME", text,
                        OpdRxSheetTool.getInstance().getExaOrOpName(rxType));
        //$$========lxֱ��ȡ����ǩ��¼start==============$$//
        TParm rxSheetParam=this.getRxSheeData(odo.getCaseNo(),rxNo);
        
        //$$========lxֱ��ȡ����ǩ��¼end ==============$$//
        /**inParam.setData("ORG_CODE", text,
                        OpdRxSheetTool.getInstance().
                        getOrgName(odo.getCaseNo(), rxNo));**/                     
        inParam.setData("ORG_CODE", text,
        		checkIsNull(rxSheetParam.getValue("DEPT_CHN_DESC", 0)));             
                 
        inParam.setData("PAY_TYPE", text,
                        "�ѱ�:" +
                        OpdRxSheetTool.getInstance().
                        getPayTypeName(odo.getRegPatAdm().
                                       getItemString(0, "CTZ1_CODE")));
//        inParam.setData("PAY_TYPE_ENG", text,
//                        "Cate:" +
//                        OpdRxSheetTool.getInstance().
//                        getPayTypeEngName(odo.
//                                          getRegPatAdm().getItemString(0, "CTZ1_CODE")));
        inParam.setData("MR_NO", text, "������:" + odo.getMrNo());
  //      inParam.setData("MR_NO_ENG", text, "Pat ID:" + odo.getMrNo());
        /**inParam.setData("PAT_NAME", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().getPatName(odo.getMrNo()));**/
        
        inParam.setData("PAT_NAME", text,
                "����:" +
                checkIsNull(rxSheetParam.getValue("PAT_NAME", 0)));                      
                        
//        inParam.setData("PAT_NAME_ENG", text,
//                        "Name:" +
//                        OpdRxSheetTool.getInstance().getPatEngName(odo.getMrNo()));
        /**inParam.setData("PAT_ID", text,
                        "�������֤�ţ�" +
                        OpdRxSheetTool.getInstance().getId(odo.getMrNo()));**/
        inParam.setData("PAT_ID", text,
                "�������֤�ţ�" +
                checkIsNull(rxSheetParam.getValue("IDNO", 0)));
        
        /**inParam.setData("SEX_CODE", text,
                        "�Ա�:" +
                        OpdRxSheetTool.getInstance().getSexName(odo.getMrNo()));**/
        inParam.setData("SEX_CODE", text,
                "�Ա�:" +
                checkIsNull(rxSheetParam.getValue("SEX_CODE", 0)));
        
//        inParam.setData("SEX_CODE_ENG", text,
//                       "Gender:" + getSexEngName(odo.getMrNo()));
        /**inParam.setData("BIRTHDAY", text,
                        "��������:" +
                        OpdRxSheetTool.getInstance().getBirthDays(odo.getMrNo()));**/
        inParam.setData("BIRTHDAY", text,
                "��������:" +
                checkIsNull(rxSheetParam.getValue("BIRTH_DATE", 0)));
               
//        inParam.setData("BBB", text,
//                        "Birthday:" +
//                        OpdRxSheetTool.getInstance().getBirthDays(odo.getMrNo()));
        inParam.setData("AGE", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().
                        getAgeName(odo.getCaseNo(), odo.getMrNo()));
//        inParam.setData("AGE_ENG", text,
//                        "Age:" +
//                        OpdRxSheetTool.getInstance().
//                        getAgeEngName(odo.getCaseNo(), odo.getMrNo()));
        /**inParam.setData("DEPT_CODE", text,
                        "�Ʊ�:" +
                        OpdRxSheetTool.getInstance().getDeptName(realDeptCode));**/
        inParam.setData("DEPT_CODE", text,
                "�Ʊ�:" +
                checkIsNull(rxSheetParam.getValue("DEPT_ABS_DESC", 0)));
        
//        inParam.setData("DEPT_CODE_ENG", text,
//                        "Dept:" +
//                        OpdRxSheetTool.getInstance().getDeptEngName(realDeptCode));
        /**inParam.setData("CLINIC_ROOM", text,
                        "���:" +
                        OpdRxSheetTool.getInstance().getClinicName(odo.getCaseNo()));**/
        
        inParam.setData("CLINIC_ROOM", text,
                "���:" +
                checkIsNull(rxSheetParam.getValue("CLINICROOM_DESC", 0)));
        
//        inParam.setData("CLINIC_ROOM_ENG", text,
//                        "Room:" +
//                        OpdRxSheetTool.
//                        getInstance().getClinicEngName(odo.getCaseNo()));
        //inParam.setData("DR_CODE", text, "ҽ��:" + this.GetRegDr(odo.getCaseNo()));
        inParam.setData("DR_CODE", text, "ҽ��:" + checkIsNull(rxSheetParam.getValue("USER_NAME", 0)));        
        
   //     inParam.setData("DR_CODE_ENG", text, "M.D.:" + this.GetRegDrEng(odo.getCaseNo()));
        /**inParam.setData("ADM_DATE", text,
                        "����ʱ��:" +
                        OpdRxSheetTool.getInstance().getOrderDate(odo.getCaseNo()));**/  
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd") ;
        String admDate = null==rxSheetParam.getData("ADM_DATE", 0)?"":df.format(rxSheetParam.getData("ADM_DATE", 0)) ;
      //================add by wanglong 20121011==================
        String regDate=checkIsNull(rxSheetParam.getValue("REG_DATE", 0));
        //====pangben 2013-1-29 ��ӹҺ���ʾ����ʱ��
        String sql="SELECT ADM_TYPE FROM REG_PATADM WHERE CASE_NO='"+odo.getRegPatAdm().getCaseNo()+"'";
        TParm startParm = new TParm(TJDODBTool.getInstance().select(sql));
        String  admTypeCode =null!=startParm &&null!=startParm.getValue("ADM_TYPE",0)?startParm.getValue("ADM_TYPE",0):"";
        if ("O".equalsIgnoreCase(admTypeCode)) {//"�� "
        	//System.out.println("===========����===============");
            inParam.setData("ADM_DATE", text, "����ʱ��:" +admDate);
        }
        else if ("E".equalsIgnoreCase(admTypeCode)) {//"�� "
        	//System.out.println("===========����==============");
            inParam.setData("ADM_DATE", text, "����ʱ��:" +regDate);
        }
        //================add end================================
        //inParam.setData("ADM_DATE", text, "����ʱ��:" +admDate);//delete by wanglong 20121011
        
//        inParam.setData("ADM_DATE_ENG", text,
//                        "Date:" +
//                        OpdRxSheetTool.getInstance().getOrderDate(odo.getCaseNo()));
        inParam.setData("SESSION_CODE", text,
                        "ʱ��:" +
                        OpdRxSheetTool.getInstance().getSessionName(odo.getCaseNo()));
//        inParam.setData("SESSION_CODE_ENG", text,
//                        "Time:" +
//                        OpdRxSheetTool.getInstance().
//                        getSessionEngName(odo.getCaseNo()));
        //inParam.setData("FOOT_DR", text, "ҽʦ:" + this.GetRealRegDr(odo.getCaseNo()));
        inParam.setData("FOOT_DR", text, "ҽʦ:");//====pangben 2013-1-29 �޸�ҽ��ǩ������ʾ
        
        /**inParam.setData("FOOT_DR_CODE", text,
                        "ҽʦ����:" + new TParm(TJDODBTool.getInstance().select(GET_REALREG_DR.replaceFirst("#", odo.getCaseNo()))).
                                  getValue("REALDR_CODE",0));**/
        inParam.setData("FOOT_DR_CODE", text,
                "ҽʦ����:" + checkIsNull(rxSheetParam.getValue("REALDR_CODE", 0)));
        //System.out.println("================sql1 time end=================="+new Date());
        
        //
        inParam.setData("BAR_CODE", text, rxNo);
        // modify by wangb 2017/7/13 ������֪ͨ����������ʾ������
        if ("5".equals(rxType)) {
        	inParam.setData("BAR_CODE", text, odo.getMrNo());
        }
        //inParam.setData("BAR_CODE", text, odo.getMrNo());//������֪ͨ��������ȡ�����ţ����Զ���ͳһmodify by huangjw 20150302
        inParam.setData("BALANCE", text,
                        "�������(��):" + getArAmt(rxNo, odo.getCaseNo()));

        double amt = EKTIO.getInstance().getRxBalance(odo.getCaseNo(), rxNo);
        inParam.setData("CURRENT_AMT", text, "�����(��):" + amt);
        double orginalAmt = EKTIO.getInstance().getRxOriginal(odo.getCaseNo(), rxNo);
        inParam.setData("ORINGINAL_AMT", text, "��ԭ��(��):" + orginalAmt);
        inParam.setData("CHARGE", text, "���οۿ�(��):" + StringTool.round(orginalAmt-amt, 2));
        inParam.setData("DIAG", text, "�ٴ���ϣ�" + getIcdName(odo.getCaseNo()));
//        inParam.setData("DIAG_ENG", text,
//                        "Diagnosis:" + getIcdEngName(odo.getCaseNo()));
        inParam.setData("PRINT_NO_NO", text,
                        odo.getOpdOrder().getItemString(0, "PRINT_NO"));
//		System.out.println("printNoNo===="+odo.getOpdOrder().getItemString(0, "PRINT_NO"));
        inParam.setData("PRINT_NO", text, "��ҩ�ţ�");
    //    inParam.setData("PRINT_NO_ENG", text, "PrscrptNo:");
//        String orgCode = odo.getOpdOrder().getItemString(0, "EXEC_DEPT_CODE");
//        String counterDate = StringTool.getString(odo.getOpdOrder().getDBTime(),
//                                                  "yyyyMMdd");
        String counterNo = odo.getOpdOrder().getItemInt(0, "COUNTER_NO") + "";
//        TParm counterParm = getCounterNames(orgCode, counterDate, counterNo);
//        if (counterParm != null) {
//            counterDesc = counterParm.getValue("COUNTER_DESC", 0);
//            counterEngDesc = counterParm.getValue("COUNTER_ENG_DESC", 0);
//        }
        inParam.setData("COUNTER_NO", text, "��̨��");
 //       inParam.setData("COUNTER_NO_ENG", text, "Counter:" + counterEngDesc);
        inParam.setData("COUNTER_NO_NO", text, counterNo);
//		System.out.println("counterNoNo==="+counterDesc);
        String orderType = "",
            orderTypeCode = odo.getRegPatAdm().getItemString(0, "ADM_TYPE");

        //System.out.println("orderTypeCode=="+orderTypeCode);

        if ("O".equalsIgnoreCase(orderTypeCode)) {
            orderType = "�� ";
        }
        else if ("E".equalsIgnoreCase(orderTypeCode)) {
            orderType = "�� ";
        }
//		System.out.println("birthday="+odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE"));
        String birthDate = odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE") +
            "";
       // System.out.println("========����ǩ����rxType========="+rxType);
        
        
        
        if ("2".equalsIgnoreCase(rxType)||"1".equalsIgnoreCase(rxType)) {
        	//System.out.println("=========sql1  ȡ��ר�ô���ǩ������ start=========="+new Date());
            //ȡ��ר�ô���ǩ������  ����ר�ô���ҩƷΪ��
            sql = "SELECT B.PRNSPCFORM_FLG,B.CTRLDRUGCLASS_CODE,B.PRN_TYPE_DESC "+
                " FROM PHA_BASE A,SYS_CTRLDRUGCLASS B "+
                " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE(+) AND A.ORDER_CODE='"+odo.getOpdOrder().getItemString(0,"ORDER_CODE")+"' ";
            TParm prn = new TParm(TJDODBTool.getInstance().select(sql));
            orderType = prn.getValue("PRN_TYPE_DESC",0);
            //System.out.println("=========sql2 ȡ��ר�ô���ǩ������ end=========="+new Date());
        }
        else if (!StringUtil.isNullString(birthDate) &&
                 OPDSysParmTool.
                 getInstance().isChild(odo.getPatInfo().
                                       getItemTimestamp(0, "BIRTH_DATE"))) {
            orderType = "�� ";
        }
        else if ("3".equalsIgnoreCase(rxType)) {
            orderType = "��ҽ ";
        }

        inParam.setData("ORDER_TYPE", text, orderType);
        inParam.setData("RX_NO", rxNo);
        inParam.setData("CASE_NO", odo.getCaseNo());
        inParam.setData("RX_TYPE", rxType);
        if (!"3".equalsIgnoreCase(rxType)) {
        	//System.out.println("=============�Ǵ���ֱ�ӷ���=====================");
        	//�Ǵ���ֱ�ӷ���;
            return inParam;
        }

//        sql = "SELECT   ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES||'��/'||D.CYCLE||'��' FREQ_CODE,A. TAKE_DAYS,"
//            +
//            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO "
//            +
//            "  FROM   OPD_ORDER A,SYS_DICTIONARY B, SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E"
//            + "  WHERE   RX_NO = '"
//            + rxNo
//            + "'"
//            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
//            + "         AND A.DCTAGENT_CODE=B.ID"
//            + "         AND C.GROUP_ID='PHA_DCTEXCEP'"
//            + "         AND A.DCTEXCEP_CODE IS NULL"
//            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
//            "         AND A.ROUTE_CODE=E.ROUTE_CODE" +
//            " 	UNION" +
//            "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES || '��/' || D.CYCLE || '��' FREQ_CODE,A.TAKE_DAYS," +
//            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO " +
//            "   FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E" +
//            "   WHERE       RX_NO = '" + rxNo + "'" +
//            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
//            "         AND A.DCTAGENT_CODE = B.ID" +
//            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
//            "         AND A.DCTEXCEP_CODE = C.ID" +
//            "         AND A.FREQ_CODE = D.FREQ_CODE"+
//            "         AND A.ROUTE_CODE=E.ROUTE_CODE"+
//            "   ORDER BY SEQ_NO";
//        //System.out.println("sql==============" + sql);
//        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        
//        TParm parm = new TParm() ;
//        TParm parm1 = new TParm() ;
//        TParm parm2 = new TParm() ;
//        String sql = "SELECT   ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES||'��/'||D.CYCLE||'��' FREQ_CODE,A. TAKE_DAYS,"
//            +
//            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO "
//            +
//            "  FROM   OPD_ORDER A,SYS_DICTIONARY B, SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E"
//            + "  WHERE   RX_NO = '"+ rxNo+ "'"
//            + "         AND A.ORDER_CAT1_CODE = 'LIS'"
//            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
//            + "         AND A.DCTAGENT_CODE=B.ID"
//            + "         AND C.GROUP_ID='PHA_DCTEXCEP'"
//            + "         AND A.DCTEXCEP_CODE IS NULL"
//            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
//            "         AND A.ROUTE_CODE=E.ROUTE_CODE" +
//            " 	UNION" +
//            "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES || '��/' || D.CYCLE || '��' FREQ_CODE,A.TAKE_DAYS," +
//            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO " +
//            "   FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E" +
//            "   WHERE       RX_NO = '" + rxNo + "'" +
//            "         AND A.ORDER_CAT1_CODE = 'LIS'"+            
//            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
//            "         AND A.DCTAGENT_CODE = B.ID" +
//            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
//            "         AND A.DCTEXCEP_CODE = C.ID" +
//            "         AND A.FREQ_CODE = D.FREQ_CODE"+
//            "         AND A.ROUTE_CODE=E.ROUTE_CODE"+
//            "   ORDER BY SEQ_NO";
//        System.out.println("sql==============" + sql);
//        parm1 = new TParm(TJDODBTool.getInstance().select(sql));
//        String sql2 = "SELECT   ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES||'��/'||D.CYCLE||'��' FREQ_CODE,A. TAKE_DAYS,"
//            +
//            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO "
//            +
//            "  FROM   OPD_ORDER A,SYS_DICTIONARY B, SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E"
//            + "  WHERE   RX_NO = '"+ rxNo+ "'"
//            + "         AND A.ORDER_CAT1_CODE = 'RIS'"
//            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
//            + "         AND A.DCTAGENT_CODE=B.ID"
//            + "         AND C.GROUP_ID='PHA_DCTEXCEP'"
//            + "         AND A.DCTEXCEP_CODE IS NULL"
//            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
//            "         AND A.ROUTE_CODE=E.ROUTE_CODE" +
//            " 	UNION" +
//            "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES || '��/' || D.CYCLE || '��' FREQ_CODE,A.TAKE_DAYS," +
//            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO " +
//            "   FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E" +
//            "   WHERE       RX_NO = '" + rxNo + "'" +
//            "         AND A.ORDER_CAT1_CODE = 'RIS'"+            
//            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
//            "         AND A.DCTAGENT_CODE = B.ID" +
//            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
//            "         AND A.DCTEXCEP_CODE = C.ID" +
//            "         AND A.FREQ_CODE = D.FREQ_CODE"+
//            "         AND A.ROUTE_CODE=E.ROUTE_CODE"+
//            "   ORDER BY SEQ_NO";
//        System.out.println("sql==============" + sql2);
//        parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//        
//        //��������
//        int lisLen = parm1.getCount() ;
//        for (int i = 0; i < lisLen; i++) {
//			parm.addRowData(parm1, i) ;
//		}
//        //TODO:
////        int len = (lisLen%10 == 0 ? 0:(lisLen/10+1)*10 - lisLen) ;
////        for (int i = 0; i < len; i++) {
////        	parm.addData("ORDER_DESC", "") ;
////        	parm.addData("MEDI_QTY", "") ; 
////        	parm.addData("AR_AMT", "") ;
////        	parm.addData("FREQ_CODE", "") ;
////        	parm.addData("TAKE_DAYS", "") ;
////        	parm.addData("DCTAGENT_CODE", "") ;
////        	parm.addData("DCTEXCEP_CODE", "") ;
////        	parm.addData("DCT_TAKE_QTY", "") ;
////        	parm.addData("PACKAGE_TOT", "") ;
////        	parm.addData("ROUTE_CODE", "") ;
////        	parm.addData("SEQ_NO", "") ;
////		}
//        //�������
//        int risLen = parm2.getCount() ;
//        for (int i = 0; i < risLen; i++) {
//        	parm.addRowData(parm2, i) ;
//		}    
        
//        System.out.println("ʱ��1===="+new Date());
        sql = "SELECT   ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES||'��/'||D.CYCLE||'��' FREQ_CODE,A. TAKE_DAYS,"
            +
            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO "
            +
            "  FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_PHAFREQ D,SYS_PHAROUTE E"
            + "  WHERE   RX_NO = '"
            + rxNo
            + "'"
            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
            + "         AND A.DCTAGENT_CODE=B.ID"
            + "         AND A.DCTEXCEP_CODE IS NULL"
            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
            "         AND A.ROUTE_CODE=E.ROUTE_CODE";
        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println("ʱ��1==1=="+new Date());
//        System.out.println("parm1---sql===="+sql);
//        System.out.println("parm1---===="+parm1);
        
        sql = "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES || '��/' || D.CYCLE || '��' FREQ_CODE,A.TAKE_DAYS," +
            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.SEQ_NO " +
            "   FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E" +
            "   WHERE       RX_NO = '" + rxNo + "'" +
            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
            "         AND A.DCTAGENT_CODE = B.ID" +
            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
            "         AND A.DCTEXCEP_CODE = C.ID" +
            "         AND A.FREQ_CODE = D.FREQ_CODE"+
            "         AND A.ROUTE_CODE=E.ROUTE_CODE";
        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println("ʱ��1==2=="+new Date());
//        System.out.println("parm2---sql===="+sql);
//        System.out.println("parm2---===="+parm2);
        for (int i = 0; i < parm2.getCount(); i++) {
        	parm1.addData("ORDER_DESC", parm2.getData("ORDER_DESC", i));
        	parm1.addData("MEDI_QTY", parm2.getData("MEDI_QTY", i));
        	parm1.addData("AR_AMT", parm2.getData("AR_AMT", i));
        	parm1.addData("FREQ_CODE", parm2.getData("FREQ_CODE", i));
        	parm1.addData("TAKE_DAYS", parm2.getData("TAKE_DAYS", i));
        	parm1.addData("DCTAGENT_CODE", parm2.getData("DCTAGENT_CODE", i));
        	parm1.addData("DCTEXCEP_CODE", parm2.getData("DCTEXCEP_CODE", i));
        	parm1.addData("DCT_TAKE_QTY", parm2.getData("DCT_TAKE_QTY", i));
        	parm1.addData("PACKAGE_TOT", parm2.getData("PACKAGE_TOT", i));
        	parm1.addData("ROUTE_CODE", parm2.getData("ROUTE_CODE", i));
        	parm1.addData("SEQ_NO", parm2.getInt("SEQ_NO", i));
		}
        
//        System.out.println("ʱ��1==22=="+new Date());
        
        Vector seqVector = (Vector) parm1.getData("SEQ_NO");
        Integer[] seqList = new Integer[seqVector.size()];
        for (int i = 0; i < seqList.length; i++) {
        	seqList[i] = Integer.valueOf(seqVector.get(i)+"");
		}
        quick(seqList);
        TParm parm = new TParm();
        for (int i = 0; i < seqList.length; i++) {
			for (int j = 0; j < seqList.length; j++) {
				if(seqList[i] == parm1.getInt("SEQ_NO", j)){
					parm.addData("ORDER_DESC", parm1.getData("ORDER_DESC", j));
		        	parm.addData("MEDI_QTY", parm1.getData("MEDI_QTY", j));
		        	parm.addData("AR_AMT", parm1.getData("AR_AMT", j));
		        	parm.addData("FREQ_CODE", parm1.getData("FREQ_CODE", j));
		        	parm.addData("TAKE_DAYS", parm1.getData("TAKE_DAYS", j));
		        	parm.addData("DCTAGENT_CODE", parm1.getData("DCTAGENT_CODE", j));
		        	parm.addData("DCTEXCEP_CODE", parm1.getData("DCTEXCEP_CODE", j));
		        	parm.addData("DCT_TAKE_QTY", parm1.getData("DCT_TAKE_QTY", j));
		        	parm.addData("PACKAGE_TOT", parm1.getData("PACKAGE_TOT", j));
		        	parm.addData("ROUTE_CODE", parm1.getData("ROUTE_CODE", j));
		        	parm.addData("SEQ_NO", parm1.getInt("SEQ_NO", j));
		        	continue;
				}
			}
		}
        parm.setCount(seqList.length);
//        System.out.println("ʱ��2===="+new Date());
        
        if (parm == null || parm.getErrCode() != 0) {
            //System.out.println("û������");
            return parm;
        }
//        inParam.setData("FOOT_DR", text, "ҽʦ:" + this.GetRealRegDr(odo.getCaseNo()));
        inParam.setData("FOOT_DR", text, "ҽʦ:" +this.getOpdDr(odo.getCaseNo(),rxNo).getValue("USER_NAME"));//==liling 20140811 modify
        //System.out.println("tableparm=========="+parm);
        int count = parm.getCount();
        text = "TEXT";
        double totAmtDouble = 0.0;
        TParm tmp = new TParm();
//		Systme.out.printl
        for (int i = 0; i < count; i++) {
            String row = (i / 4 + 1) + "";
            //System.out.println("row==========="+row);
            String column = (i % 4 + 1) + "";
            //System.out.println("olumn========"+column);
            totAmtDouble += parm.getDouble("TOT_AMT", i);
//            inParam.setData("ORDER_DESC" + row + column, text, parm.getValue(
//                "ORDER_DESC", i));
//            inParam.setData("MEDI_QTY" + row + column, text, parm.getValue(
//                "MEDI_QTY", i)
//                            + "��");
//            inParam.setData("DCTAGENT" + row + column, text, parm.getValue(
//                "DCTEXCEP_CODE", i));
            tmp.setData("ORDER_DESC" + i%4, i/4, parm.getValue(
                    "ORDER_DESC", i));
          	tmp.setData("MEDI_QTY" + i%4, i/4, parm.getValue(
          			"MEDI_QTY", i)+ "��");
          	tmp.setData("DCTAGENT" + i%4, i/4, parm.getValue(
          			"DCTEXCEP_CODE", i));
        }
        tmp.setCount(tmp.getCount("ORDER_DESC0"));
        tmp.addData("SYSTEM", "COLUMNS", "ORDER_DESC0"); 
        tmp.addData("SYSTEM", "COLUMNS", "DCTAGENT0");
        tmp.addData("SYSTEM", "COLUMNS", "MEDI_QTY0");
        tmp.addData("SYSTEM", "COLUMNS", "ORDER_DESC1");      
        tmp.addData("SYSTEM", "COLUMNS", "DCTAGENT1");
        tmp.addData("SYSTEM", "COLUMNS", "MEDI_QTY1");
        tmp.addData("SYSTEM", "COLUMNS", "ORDER_DESC2");       
        tmp.addData("SYSTEM", "COLUMNS", "DCTAGENT2");
        tmp.addData("SYSTEM", "COLUMNS", "MEDI_QTY2");
        tmp.addData("SYSTEM", "COLUMNS", "ORDER_DESC3");
        tmp.addData("SYSTEM", "COLUMNS", "DCTAGENT3");
        tmp.addData("SYSTEM", "COLUMNS", "MEDI_QTY3");
        inParam.setData("ORDER_TABLE", tmp.getData());
        inParam.setData("DIAG_TEXT", text,  getIcdName(odo.getCaseNo()));
        
        inParam.setData("TAKE_DAYS", text, "������"
                        + parm.getValue("TAKE_DAYS", 0) + "��");
//        inParam.setData("FREQ_CODE", text, "��Σ�"
        inParam.setData("FREQ_CODE", text, "������"
                        + parm.getValue("FREQ_CODE", 0));
        inParam.setData("ROUTE_CODE", text, "�÷���"
                        + parm.getValue("ROUTE_CODE", 0));
        inParam.setData("DCTAGENT_CODE", text, "������"
                        + parm.getValue("DCTAGENT_CODE", 0));
        inParam.setData("DCT_TAKE_QTY", text, "ÿ�η�������"
                        + parm.getValue("DCT_TAKE_QTY", 0) + "����");//=========pangben modify 20110804 �޸�Ӣ��
        inParam.setData("PACKAGE_TOT", text, "ÿ���ܿ�����"
                        + parm.getValue("PACKAGE_TOT", 0) + "��");
        return inParam;

    }
    
   

    /**
     * ���ݸ�����Ϣ��ѯ��̨��Ӣ������
     * @param orgCode
     * @param counterDate
     * @param counterNo
     * @return
     */
    public TParm getCounterNames(String orgCode, String counterDate,
                                 String counterNo) {
        TParm result;
        if (StringUtil.isNullString(orgCode) ||
            StringUtil.isNullString(counterDate) ||
            StringUtil.isNullString(counterNo)) {
            return null;
        }
        String sql = this.GET_COUNTER_DESC.replaceFirst("#", orgCode).replaceFirst("#", counterNo);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
//    		System.out.println("getCounterNames.wrong sql="+sql);
            System.out.println("err:" + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ���ݴ�����ȡ�ô������
     * @param rxNo
     * @param order
     * @return
     */
    public double getArAmt(String rxNo, OpdOrder order) {
        double arAmt = 0.0;
        if (StringUtil.isNullString(rxNo) || order == null) {
            return arAmt;
        }
        String filterString = order.getFilter();
        order.setFilter("RX_NO='" + rxNo + "'");
        order.filter();
        TParm result = order.getBuffer(order.PRIMARY);
//        System.out.println("�����ܷ���result:"+result);
        int count = result.getCount("RX_NO");
        for (int i = 0; i < count; i++) {
            //���˵�����
            if(!result.getValue("ORDERSET_CODE",i).equals(result.getValue("ORDER_CODE",i)))
                arAmt += result.getDouble("AR_AMT",i);
        }
        order.setFilter(filterString);
        order.filter();
        arAmt = StringTool.round(arAmt, 2);
        return arAmt;
    }
    /**
     * ���ݴ�����ȡ�ô������
     * @param rxNo
     * @param order
     * @return
     */
    public double getArAmt(String rxNo, String CASE_NO) {
        double arAmt = 0.0;
        if (StringUtil.isNullString(rxNo) || StringUtil.isNullString(CASE_NO)) {
            return arAmt;
        }
        //zhangyong20110308
        String sql =
            "SELECT SUM(AR_AMT) AS AR_AMT FROM OPD_ORDER WHERE CASE_NO = '" +
            CASE_NO + "' AND RX_NO = '" + rxNo + "' AND RELEASE_FLG <> 'Y'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        arAmt = StringTool.round(result.getDouble("AR_AMT", 0), 2);
        return arAmt;
    }

    /**
     * ��ȡӦ����ҽʦ
     * @param CASE_NO String
     * @return String
     */
    public String GetRegDr(String CASE_NO){
        TParm reg = new TParm(TJDODBTool.getInstance().select(GET_REG_DR.replaceFirst("#", CASE_NO)));
        if(reg.getCount()>0){
            return StringUtil.getDesc("SYS_OPERATOR", "USER_NAME",
                                  "USER_ID='" +reg.getValue("DR_CODE",0) + "'");
        }else
            return "";
    }
    /**
     * ��ȡʵ�ʿ���ҽʦ
     * @param CASE_NO String
     * @return String
     */
    public String GetRealRegDr(String CASE_NO){
        TParm reg = new TParm(TJDODBTool.getInstance().select(GET_REALREG_DR.replaceFirst("#", CASE_NO)));
        if(reg.getCount()>0){
            return StringUtil.getDesc("SYS_OPERATOR", "USER_NAME",
                                  "USER_ID='" +reg.getValue("REALDR_CODE",0) + "'");
        }else
            return "";
    }

    /**
     * ��ȡӦ����ҽʦӢ����
     * @param CASE_NO String
     * @return String
     */
    public String GetRegDrEng(String CASE_NO){
        TParm reg = new TParm(TJDODBTool.getInstance().select(GET_REG_DR.replaceFirst("#", CASE_NO)));
        if(reg.getCount()>0){
            return StringUtil.getDesc("SYS_OPERATOR", "USER_ENG_NAME",
                                  "USER_ID='" +reg.getValue("REALDR_CODE",0) + "'");
        }else
            return "";
    }
    /**
     * ��ȡ��ӡ����
     * @param caseNo String
     * @param rxNo String
     * @return int
     */
    public int getPrintNum(String caseNo,String rxNo){
        String sql = "SELECT B.PRINT_TOT "+
            " FROM OPD_ORDER A,SYS_PHAROUTE B "+
            " WHERE A.ROUTE_CODE=B.ROUTE_CODE "+
            " and A.CASE_NO = '"+caseNo+"' "+
            " and A.RX_NO = '"+rxNo+"' ";
//        System.out.println("��ӡ����sql:"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getErrCode()!=0){
            return 1;
        }
        return result.getInt("PRINT_TOT",0);
    }
    
    public TParm getRxSheeData(String caseNo,String rxNo){
    	String sql="SELECT b.DEPT_CHN_DESC,C.CTZ_DESC, D.PAT_NAME, D.IDNO, I.CHN_DESC SEX_CODE,";
               sql+="TO_CHAR(D.BIRTH_DATE,'YYYY/MM/DD') BIRTH_DATE, E.DEPT_ABS_DESC, G.CLINICROOM_DESC,";
               sql+="H.USER_NAME,TO_CHAR(F.REG_DATE,'YYYY/MM/DD') REG_DATE,H.USER_NAME FOOT_DR, F.REALDR_CODE,F.ADM_DATE ";
               sql+=" FROM OPD_ORDER A,";
               sql+="SYS_DEPT B,";
               sql+="SYS_CTZ C,";
               sql+="SYS_PATINFO D,";
               sql+="SYS_DEPT E,";
               sql+="REG_PATADM F,";
               sql+=" REG_CLINICROOM G,";
               sql+=" SYS_OPERATOR H,";
               sql+=" (SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') I";
               sql+=" WHERE A.CASE_NO = '"+caseNo+"'";
               sql+=" AND A.RX_NO = '"+rxNo+"'";
            	  /** AND a.seq_no = '1' **/
               sql+=" AND A.EXEC_DEPT_CODE = B.DEPT_CODE";
               sql+=" AND A.CTZ1_CODE = C.CTZ_CODE";
               sql+=" AND A.MR_NO = D.MR_NO";
               sql+=" AND E.DEPT_CODE = A.DEPT_CODE";
               sql+=" AND F.CASE_NO = A.CASE_NO";
               sql+=" AND F.CLINICROOM_NO = G.CLINICROOM_NO";
               sql+=" AND (F.DR_CODE = H.USER_ID ";
               sql+=" OR F.REALDR_CODE=H.USER_ID)";
               sql+=" AND D.SEX_CODE=I.ID";
        //System.out.println("======SQL========"+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getErrCode() != 0) {
//    		System.out.println("getCounterNames.wrong sql="+sql);
            System.out.println("err:" + result.getErrText());
            return null;
        }
    	
        return result;
    }
    
    private int getMiddle(Integer[] list, int low, int high) {  
        int tmp = list[low];    //����ĵ�һ����Ϊ����  
        while (low < high) {  
            while (low < high && list[high] > tmp) {  
                high--;  
            }  
            list[low] = list[high];   //������С�ļ�¼�Ƶ��Ͷ�  
            while (low < high && list[low] < tmp) {  
                low++;  
            }  
            list[high] = list[low];   //�������ļ�¼�Ƶ��߶�  
        }  
        list[low] = tmp;              //�����¼��β  
        return low;                   //���������λ��  
    }  
    private void _quickSort(Integer[] list, int low, int high) {  
        if (low < high) {  
            int middle = getMiddle(list, low, high);  //��list�������һ��Ϊ��  
            _quickSort(list, low, middle - 1);        //�Ե��ֱ���еݹ�����  
            _quickSort(list, middle + 1, high);       //�Ը��ֱ���еݹ�����  
        }  
    }  
    
    private void quick(Integer[] str) {  
        if (str.length > 0) {    //�鿴�����Ƿ�Ϊ��  
            _quickSort(str, 0, str.length - 1);  
        }  
    }  
    
    /**
     * ��ȡ����ҽʦ�Ĵ��������(���򴦷�)
     * @param caseNo
     * @param rxNo
     * @return
     */
    public TParm getOpdDr(String caseNo,String rxNo){
    	String sql="SELECT DISTINCT USER_ID,USER_NAME,ORDER_DATE FROM SYS_OPERATOR A,OPD_ORDER B " +
    			"WHERE A.USER_ID=B.DR_CODE AND B.CASE_NO= '"+caseNo+"' "+
    			" AND B.RX_NO='"+rxNo+"' order by order_date desc";
    	TParm result= new TParm(TJDODBTool.getInstance().select(sql));   	
    	if (result.getErrCode() != 0) {    		
            System.out.println("err:" + result.getErrText());
            return null;
        }
    	return result.getRow(0);
    }
    
    
    /**
     * ��ȡ��ӡ����
     * @param caseNo String
     * @param rxNo String
     * @return int
     */
    public String getBillType(String caseNo,String rxNo,String medApplyNo){
        String sql = "SELECT BILL_FLG "+
            " FROM OPD_ORDER A "+
            " WHERE CASE_NO = '"+caseNo+"' ";
        if(rxNo.length()>0){
        	sql += " AND RX_NO IN ("+rxNo+") ";
        }
        
        if(medApplyNo.length()> 0){
        	sql += " AND MED_APPLY_NO = '"+medApplyNo+"' ";
        }
           
//        System.out.println("��ӡ����sql:"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String billType = "���շ�";
        for (int i = 0; i < result.getCount(); i++) {
			if(!result.getValue("BILL_FLG", i).equals("Y")){
				billType ="δ�շ�";
				break;
			}
		}
 
        return billType;
    }
    
}
