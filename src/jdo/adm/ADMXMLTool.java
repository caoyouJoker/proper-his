package jdo.adm;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.dongyang.config.TConfig;
import com.dongyang.data.TSocket;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ADMXMLTool  extends TJDOTool {

    private TDataStore sysDictionary;
    private String path;//移动查房xml本地保存路径
    private String fileServerIP;//移动查房ftp服务器IP
    private String port;//移动查房ftp服务器端口
    private String screenPath;//大屏幕xml本地保存路径

    /**
     * 实例
     */
    public static ADMXMLTool instanceObject;
    
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMXMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMXMLTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMXMLTool() {
        setModuleName("");
        onInit();
        sysDictionary = TIOM_Database.getLocalTable("SYS_DICTIONARY");
        path = TConfig.getSystemValue("MONITOR.PATH");
        fileServerIP = TConfig.getSystemValue("MONITOR.SERVER");
        port = TConfig.getSystemValue("MONITOR.PORT");
        screenPath = TConfig.getSystemValue("SCREEN.PATH");
    }

    /**
     * 通过CASE_NO生成信息看板的XML文件
     * @param CASE_NO String
     * @return TParm
     */
    public TParm creatXMLFile(String CASE_NO) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        // 住院信息
        TParm ADM_INP = ADMInpTool.getInstance().selectForXML(parm);
        if (ADM_INP.getErrCode() < 0) {
            err("ERR:" + ADM_INP.getErrCode() + ADM_INP.getErrText() + ADM_INP.getErrName());
            return ADM_INP;
        }
        // 查询手术信息
        TParm OpParm = new TParm();
        OpParm.setData("CASE_NO", CASE_NO);
        OpParm.setData("CANCEL_FLG", "N");
        TParm OP_DATA = OPEOpBookTool.getInstance().selectOpBook(OpParm);
        if (OP_DATA.getErrCode() < 0) {
            err("ERR:" + OP_DATA.getErrCode() + OP_DATA.getErrText() + OP_DATA.getErrName());
            return OP_DATA;
        }
        // 查询入院主诊断
        TParm diagParm = new TParm();
        diagParm.setData("CASE_NO", CASE_NO);
        // modify by wangb 2015/06/08 I_门诊诊断,M_入院诊断
        diagParm.setData("IO_TYPE", "M");
        diagParm.setData("MAINDIAG_FLG", "Y");
        TParm DIAG = ADMDiagTool.getInstance().queryData(diagParm);
        if (DIAG.getErrCode() < 0) {
            err("ERR:" + DIAG.getErrCode() + DIAG.getErrText() + DIAG.getErrName());
            return DIAG;
        }
        // 查询病患转科信息
        // 转入信息
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", CASE_NO);
        inParm.setData("PSF_KIND", "INDP");
        TParm IN = ADMChgTool.getInstance().queryChgForMRO(inParm);
        TParm outParm = new TParm();
        outParm.setData("CASE_NO", CASE_NO);
        outParm.setData("PSF_KIND", "OUDP");
        TParm OUT = ADMChgTool.getInstance().queryChgForMRO(outParm);
        TParm XMLParm = new TParm();
        XMLParm.setData("StationCode", ADM_INP.getValue("STATION_CODE", 0));// 病区CODE
        XMLParm.setData("BedNo", ADM_INP.getValue("BED_NO", 0));// 床号
        XMLParm.setData("RoomNo", ADM_INP.getValue("ROOM_CODE", 0));// 病房号
        XMLParm.setData("MrNo", ADM_INP.getValue("MR_NO", 0));// 病案号
        XMLParm.setData("PatName", ADM_INP.getValue("PAT_NAME", 0));// 姓名
        XMLParm.setData("Sex", ADM_INP.getValue("SEX", 0)); // 性别
        XMLParm.setData("Age", ((String[]) StringTool.CountAgeByTimestamp(ADM_INP
                .getTimestamp("BIRTH_DATE", 0), ADM_INP.getTimestamp("IN_DATE", 0)))[0]);// 年龄
        XMLParm.setData("BirthDate",
                        StringTool.getString(ADM_INP.getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));// 出生日期
                                                                                                   // wanglong
                                                                                                   // add
                                                                                                   // 20140731
        TParm patInfo = PatTool.getInstance().getInfoForMrno(ADM_INP.getValue("MR_NO", 0));
        XMLParm.setData("Marriage", getDesc("SYS_MARRIAGE", patInfo.getValue("MARRIAGE_CODE", 0)));// 婚姻状态
                                                                                                   // wanglong
                                                                                                   // add
                                                                                                   // 20140731
        XMLParm.setData("Contacts", patInfo.getValue("CONTACTS_NAME", 0));// 联系人 wanglong add
                                                                          // 20140731
        XMLParm.setData("ContactTel", patInfo.getValue("CONTACTS_TEL", 0));// 联系人电话 wanglong add
                                                                           // 20140731
        XMLParm.setData("InDate", StringTool.getString(ADM_INP.getTimestamp("IN_DATE", 0),
                                                       "yyyy-MM-dd HH:mm:ss"));// 入院日期
        XMLParm.setData("DirectorDr", ADM_INP.getValue("DIRECTOR_DR_CODE", 0));// 主任医生

        XMLParm.setData("ChiefNs", ADM_INP.getValue("VS_NURSE_CODE", 0));// 护士长
        XMLParm.setData("AttendDr", ADM_INP.getValue("ATTEND_DR_CODE", 0));// 主管医生
        String attendDrDept =
                StringUtil.getDesc("ADM_INP A, SYS_OPERATOR_DEPT B, SYS_DEPT C", "DEPT_CHN_DESC",
                                   "A.VS_DR_CODE = B.USER_ID AND B.MAIN_FLG = 'Y' "
                                           + "AND B.DEPT_CODE = C.DEPT_CODE AND A.CASE_NO = '"
                                           + CASE_NO + "'");
        XMLParm.setData("AttendDrDept", attendDrDept);// 主管医生科室
        XMLParm.setData("OpDate", StringTool.getString(OP_DATA.getTimestamp("OP_DATE", 0),
                                                       "yyyy-MM-dd HH:mm:ss"));// 手术日期
        XMLParm.setData("NursingClass",
                        getDesc("ADM_NURSING_CLASS", ADM_INP.getValue("NURSING_CLASS", 0)));// 护理等级
        XMLParm.setData("DieCondition",
                        getDesc("SYS_DIE_CONDITION", ADM_INP.getValue("DIE_CONDITION", 0)));// 饮食情况
        XMLParm.setData("ChargeClass", ADM_INP.getValue("CTZ_DESC", 0));// 费用类别
        XMLParm.setData("illState",
                        getDesc("ADM_PATIENT_STATUS", ADM_INP.getValue("PATIENT_STATUS", 0)));// 病情
        XMLParm.setData("Cared", ADM_INP.getInt("CARE_NUM", 0) > 0 ? "是" : "否");// 陪护
        XMLParm.setData("Toilet", getDesc("ADM_TOILET", ADM_INP.getValue("TOILET", 0)));// 入厕方式
        XMLParm.setData("Measure", getDesc("ADM_MEASURE", ADM_INP.getValue("IO_MEASURE", 0)));// 计量方式
        XMLParm.setData("IsolationId", getDesc("ADM_ISOLATION", ADM_INP.getValue("ISOLATION", 0)));// 隔离情况
        XMLParm.setData("Allergies", ADM_INP.getValue("ALLERGY", 0).equals("Y") ? "是" : "否");// 过敏情况
        XMLParm.setData("Diag", DIAG.getValue("ICD_CHN_DESC", 0));// 入院主诊断
        XMLParm.setData("IsAdd", ADM_INP.getValue("ISADD", 0));// 是否加床
        XMLParm.setData("OutDate", StringTool.getString(ADM_INP.getTimestamp("DS_DATE", 0),
                                                        "yyyy-MM-dd HH:mm:ss"));// 出院日期
        if (ADM_INP.getValue("CANCEL_FLG", 0).equals("Y")) {
            // 转科信息
            String transSql =
                    "SELECT A.CHG_DATE, A.DEPT_CODE, A.STATION_CODE, A.BED_NO, B.ROOM_CODE AS ROOM_NO "
                            + "  FROM ADM_CHG A, SYS_BED B        "
                            + " WHERE A.PSF_KIND = 'INC'          "
                            + "   AND A.BED_NO = B.BED_NO(+)      "
                            + "   AND A.CASE_NO = '#'             "
                            + "ORDER BY SEQ_NO DESC               ";
            transSql = transSql.replaceFirst("#", CASE_NO);
            TParm TRANS_LOG = new TParm(TJDODBTool.getInstance().select(transSql));
            if (TRANS_LOG.getErrCode() < 0) {
                err("ERR:" + TRANS_LOG.getErrCode() + TRANS_LOG.getErrText()
                        + TRANS_LOG.getErrName());
                return TRANS_LOG;
            }
            XMLParm.setData("OutDate", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE", 0),
                                                            "yyyy-MM-dd HH:mm:ss"));// 出院日期
        }
        XMLParm.setData("TurnIn",
                        StringTool.getString(IN.getTimestamp("CHG_DATE", 0), "yyyy-MM-dd HH:mm:ss"));// 转入日期
        XMLParm.setData("TurnOut", StringTool.getString(OUT.getTimestamp("CHG_DATE", 0),
                                                        "yyyy-MM-dd HH:mm:ss"));// 转出日期
        if (!this.creatXML(XMLParm)) {
            result.setErr(-1, "生成XML失败");
            return result;
        }
        return result;
    }
    
    /**
     * 根据数据生成XML文件
     * @param parm TParm
     * @return boolean
     */
    public boolean creatXML(TParm parm) {
        StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <InPatInfo>" + LINE_SEPARATOR);
        String[] names = {"StationCode", // 病区CODE
                "BedNo", // 床位号
                "RoomNo", // 病房号
                "MrNo", // 病案号
                "PatName", // 病患姓名
                "Sex", // 性别
                "Age", // 年龄
                "BirthDate", // 出生日期 wanglong add 20140731
                "Marriage", // 婚姻状态 wanglong add 20140731
                "Contacts", // 联系人 wanglong add 20140731
                "ContactTel", // 联系人电话 wanglong add 20140731
                "InDate", // 入院日期
                "DirectorDr", // 主任医师
                "ChiefNs", // 护士长
                "AttendDr", // 主管医生
                "AttendDrDept", // 主管医生科室 wanglong add 20140731
                "OpDate", // 手术日期
                "NursingClass", // 护理等级
                "DieCondition", // 饮食情况
                "ChargeClass", // 费用类别
                "illState", // 病情
                "Cared", // 陪护
                "Toilet", // 入厕方式
                "Measure", // 计量方式
                "IsolationId", // 隔离情况
                "Allergies", // 过敏情况
                "Diag", // 入院主诊断
                "IsAdd", // 是否加床
                "OutDate", // 出院日期
                "TurnIn", // 转入日期
                "TurnOut" // 转出日期
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
        stringBuffer.append("  </InPatInfo>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        // System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
        // System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + nowStr + ".xml");
        return TIOM_FileServer.writeFile(socket, path + "PAT_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
    }

    /**
     * 兑换大字典中文
     * @param GROUP_ID String
     * @param ID String
     * @return String
     */
    private String getDesc(String GROUP_ID, String ID) {
        if (sysDictionary == null){
            return ID;  
        }
        String filter = " GROUP_ID='" + GROUP_ID + "'";
        sysDictionary.setFilter(filter);
        sysDictionary.filter();
        TParm parm = sysDictionary.getBuffer(TDataStore.PRIMARY);
        for (int i = 0; i < parm.getCount(); i++) {
            if (ID.equals(parm.getValue("ID", i))){
                return parm.getValue("CHN_DESC", i);
            } 
        }
        return ID;
    }
    
    /**
     * 通过CASE_NO生成信息看板的XML文件
     * @param CASE_NO
     * @param type A01(入院)、A02(转床)、A03(出院)、A04(取消出院)
     * @return
     */
    public TParm creatADMXMLFile(String CASE_NO, String type) {// wanglong add 20140731
        TParm result = new TParm();
        // 住院信息
        String admSql =
                "SELECT A.MR_NO, A.IN_DATE, A.DS_DATE AS OUT_DATE, A.VS_DR_CODE,B.USER_NAME, B.USER_NAME AS ATTEND_DR, A.CTZ1_CODE, C.CTZ_DESC AS CHARGE_CLASS "
                        + "  FROM ADM_INP A, SYS_OPERATOR B, SYS_CTZ C "
                        + " WHERE A.VS_DR_CODE = B.USER_ID "
                        + "   AND A.CTZ1_CODE = C.CTZ_CODE "
                        + "   AND A.CASE_NO = '#'";
        admSql = admSql.replaceFirst("#", CASE_NO);
        TParm ADM_INP = new TParm(TJDODBTool.getInstance().select(admSql));
        if (ADM_INP.getErrCode() < 0) {
            err("ERR:" + ADM_INP.getErrCode() + ADM_INP.getErrText() + ADM_INP.getErrName());
            return ADM_INP;
        }
        // 病患基本信息
        TParm PAT_INFO = PatTool.getInstance().getInfoForMrno(ADM_INP.getValue("MR_NO", 0));
        if (PAT_INFO.getErrCode() < 0) {
            return PAT_INFO;
        }
        
        // add by wangb 2015/10/16 病患主次诊断均发送至大屏 START
        // 查询入院诊断
        TParm diagParm = new TParm();
        diagParm.setData("CASE_NO", CASE_NO);
        diagParm.setData("IO_TYPE", "M");
        String mainDiag = "";
        String secondaryDiag = "";
        TParm diagM = ADMDiagTool.getInstance().queryData(diagParm);
        if (diagM.getErrCode() < 0) {
            err("ERR:" + diagM.getErrCode() + diagM.getErrText() + diagM.getErrName());
            return diagM;
        }
        
        for (int i = 0; i < diagM.getCount("CASE_NO"); i++) {
        	if (StringUtils.equals("Y", diagM.getValue("MAINDIAG_FLG", i))) {
        		mainDiag = diagM.getValue("ICD_CODE", i);
        	} else {
        		secondaryDiag = secondaryDiag + diagM.getValue("ICD_CODE", i) + ";";
        	}
        }
        
        if (secondaryDiag.length() > 0) {
        	secondaryDiag = secondaryDiag.substring(0, secondaryDiag.length() - 1);
        }
        // add by wangb 2015/10/16 病患主次诊断均发送至大屏 END
        
     // 查询主手术  add by guoy 20150730 -----------start--------------
        String opSql = " SELECT OP_DESC,OP_CODE FROM MRO_RECORD_OP WHERE CASE_NO = '" + CASE_NO + "' AND MAIN_FLG = 'Y' ";
        TParm OP_DATA = new TParm(TJDODBTool.getInstance().select(opSql));
        if (OP_DATA.getErrCode() < 0) {
            err("ERR:" + OP_DATA.getErrCode() + OP_DATA.getErrText() + OP_DATA.getErrName());
            return OP_DATA;
        }
        // 查询出院主诊断
        String outDiag = " SELECT ICD_DESC,ICD_CODE FROM MRO_RECORD_DIAG WHERE IO_TYPE='O' AND MAIN_FLG='Y' AND CASE_NO = '"+ CASE_NO +"' ";
        TParm DIAG = new TParm(TJDODBTool.getInstance().select(outDiag));
        if (DIAG.getErrCode() < 0) {
            err("ERR:" + DIAG.getErrCode() + DIAG.getErrText() + DIAG.getErrName());
            return DIAG;
        }
        //出院带药
        String orderSql = " SELECT A.ORDER_CODE,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,B.UNIT_CHN_DESC,A.ROUTE_CODE,C.ROUTE_CHN_DESC,A.FREQ_CODE,A.TAKE_DAYS,A.DR_NOTE " 
        				 +" FROM ODI_ORDER A, SYS_UNIT B,SYS_PHAROUTE C " 
        				 +" WHERE A.MEDI_UNIT = B.UNIT_CODE "
        				 +" AND A.ROUTE_CODE = C.ROUTE_CODE "
        				 +" AND A.RX_KIND='DS' AND A.CASE_NO='"+ CASE_NO + "' ";
        TParm outOrderParm = new TParm(TJDODBTool.getInstance().select(orderSql));
        //-----------------------end------------------------
        
        // 转科信息
        String transSql =
                "SELECT A.CHG_DATE, A.DEPT_CODE, A.STATION_CODE, A.BED_NO, B.ROOM_CODE AS ROOM_NO "
                        + "  FROM ADM_CHG A, SYS_BED B        "
                        + " WHERE A.PSF_KIND = '@'            "
                        + "   AND A.BED_NO = B.BED_NO(+)      "
                        + "   AND A.CASE_NO = '#'             "
                        + "ORDER BY SEQ_NO DESC               ";
        transSql = transSql.replaceFirst("#", CASE_NO);
        if (type.equals("A01")) {// 入院
            transSql = transSql.replaceFirst("@", "IN");
        } else if (type.equals("A04")) {// 取消住院
            transSql = transSql.replaceFirst("@", "INC");
        } else {
            transSql = transSql.replaceFirst("@", "INBD");
        }
        TParm TRANS_LOG = new TParm(TJDODBTool.getInstance().select(transSql));
        if (TRANS_LOG.getErrCode() < 0) {
            err("ERR:" + TRANS_LOG.getErrCode() + TRANS_LOG.getErrText() + TRANS_LOG.getErrName());
            return TRANS_LOG;
        }
        Timestamp now = SystemTool.getInstance().getDate();
        TParm XMLParm = new TParm();
        XMLParm.setData("EventType", type);// 事件类型
        XMLParm.setData("EventDate", StringTool.getString(now, "yyyy-MM-dd HH:mm:ss"));// 事件时间
        XMLParm.setData("MrNo", ADM_INP.getValue("MR_NO", 0));// 病案号
        XMLParm.setData("PatName", PAT_INFO.getValue("PAT_NAME", 0));// 病人姓名
        XMLParm.setData("Sex", getDesc("SYS_SEX", PAT_INFO.getValue("SEX_CODE", 0))); // 性别
        XMLParm.setData("BirthDate",
                        StringTool.getString(PAT_INFO.getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));// 出生日期
        XMLParm.setData("Marriage", getDesc("SYS_MARRIAGE", PAT_INFO.getValue("MARRIAGE_CODE", 0)));// 婚姻状态
        XMLParm.setData("Contacts", PAT_INFO.getValue("CONTACTS_NAME", 0));// 联系人
        XMLParm.setData("ContactTel", PAT_INFO.getValue("CONTACTS_TEL", 0));// 联系人电话
        XMLParm.setData("InDate", StringTool.getString(ADM_INP.getTimestamp("IN_DATE", 0),
                                                       "yyyy-MM-dd HH:mm:ss"));// 入院日期
        XMLParm.setData("TurnIn", "");// 转进时间
        XMLParm.setData("DeptCode", "");// 病人转进科室
        XMLParm.setData("StationCode", "");// 病人转进病区
        XMLParm.setData("RoomCode", "");// 病人转进房间
        XMLParm.setData("BedNo", "");// 病人转进病床
        XMLParm.setData("HisCode", Operator.getRegion());// 病人转进医院
        XMLParm.setData("TurnOut", "");// 转出时间
        XMLParm.setData("OldDeptCode", "");// 病人转出科室
        XMLParm.setData("OldStationCode", "");// 病人转出病区
        XMLParm.setData("OldRoomCode", "");// 病人转出房间
        XMLParm.setData("OldBedNo", "");// 病人转出进病床
        XMLParm.setData("OldHisCode", Operator.getRegion());// 病人转出医院
        if (type.equals("A01")) {// 入院
            XMLParm.setData("TurnIn", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE", 0),
                                                           "yyyy-MM-dd HH:mm:ss"));// 转进时间
            XMLParm.setData("DeptCode", TRANS_LOG.getValue("DEPT_CODE", 0));// 病人转进科室
            XMLParm.setData("StationCode", TRANS_LOG.getValue("STATION_CODE", 0));// 病人转进病区
        } else if (type.equals("A02")) {// 入床
            XMLParm.setData("TurnIn", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE", 0),
                                                           "yyyy-MM-dd HH:mm:ss"));// 转进时间
            XMLParm.setData("DeptCode", TRANS_LOG.getValue("DEPT_CODE", 0));// 病人转进科室
            XMLParm.setData("StationCode", TRANS_LOG.getValue("STATION_CODE", 0));// 病人转进病区
            XMLParm.setData("RoomNo", TRANS_LOG.getValue("ROOM_NO", 0));// 病人转进房间
            XMLParm.setData("BedNo", TRANS_LOG.getValue("BED_NO", 0));// 病人转进病床
            if (TRANS_LOG.getCount() > 1) {
                XMLParm.setData("TurnOut", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE",
                                                                                       1),
                                                                "yyyy-MM-dd HH:mm:ss"));// 转出时间
                XMLParm.setData("OldDeptCode", TRANS_LOG.getValue("DEPT_CODE", 1));// 病人转出科室
                XMLParm.setData("OldStationCode", TRANS_LOG.getValue("STATION_CODE", 1));// 病人转进病区
                XMLParm.setData("OldRoomNo", TRANS_LOG.getValue("ROOM_NO", 1));// 病人转出房间
                XMLParm.setData("OldBedNo", TRANS_LOG.getValue("BED_NO", 1));// 病人转出进病床
            }
        } else if (type.equals("A03") || type.equals("A04")) {// 出院或取消住院
            XMLParm.setData("TurnOut", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE", 0),
                                                            "yyyy-MM-dd HH:mm:ss"));// 转出时间
            XMLParm.setData("OldDeptCode", TRANS_LOG.getValue("DEPT_CODE", 0));// 病人转出科室
            XMLParm.setData("OldStationCode", TRANS_LOG.getValue("STATION_CODE", 0));// 病人转进病区
            XMLParm.setData("OldRoomNo", TRANS_LOG.getValue("ROOM_NO", 0));// 病人转出房间
            XMLParm.setData("OldBedNo", TRANS_LOG.getValue("BED_NO", 0));// 病人转出进病床
            //add by guoy 20150730 ---------------start-------
            XMLParm.setData("IdNo",PAT_INFO.getValue("IDNO",0));//身份证号
            XMLParm.setData("Address",PAT_INFO.getValue("ADDRESS",0));//住址 
            XMLParm.setData("VsDr", ADM_INP.getValue("USER_NAME",0));//经治医生 
            XMLParm.setData("OpeIcdCode",OP_DATA.getValue("OP_CODE",0));//主手术编码
            XMLParm.setData("OpeIcd",OP_DATA.getValue("OP_DESC",0));//主手术ICD名称
            XMLParm.setData("OutDiag", DIAG.getValue("ICD_DESC", 0));// 出院主诊断
            XMLParm.setData("OutDiagCode",DIAG.getValue("ICD_CODE",0));//出院主诊断编码
            XMLParm.setData("outOrderParm", outOrderParm);//出院带药
            //-----------------------end------------------------
        }
        
        XMLParm.setData("AttendDr", ADM_INP.getValue("ATTEND_DR", 0));// 主管医生
        XMLParm.setData("ChargeClass", ADM_INP.getValue("CHARGE_CLASS", 0));// 费用类别
        XMLParm.setData("CaseNo", CASE_NO);// 就诊序号
        XMLParm.setData("OutDate", StringTool.getString(ADM_INP.getTimestamp("OUT_DATE", 0),
                                                        "yyyy-MM-dd HH:mm:ss"));// 出院日期
        
        // add by wangb 2015/10/16 病患主次诊断均发送至大屏 START
        XMLParm.setData("Diag", mainDiag);// 入院主诊断
        XMLParm.setData("SecondaryDiag", secondaryDiag);// 入院次诊断
        // add by wangb 2015/10/16 病患主次诊断均发送至大屏 END
        
        if (type.equals("A04")) {// 取消住院
            XMLParm.setData("OutDate", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE", 0),
                                                            "yyyy-MM-dd HH:mm:ss"));// 出院日期
        }
        
        if (!this.creatADMXML(XMLParm)) {
            result.setErr(-1, "生成XML失败");
            return result;
        }
        
        return result;
    }

    /**
     * 根据数据生成XML文件
     * 
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean creatADMXML(TParm parm) {// wanglong add 20140731
        StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <InPatInfo>" + LINE_SEPARATOR);
        String[] names = {"EventType",// 事件类型
                "EventDate",// 事件时间
                "MrNo",// 病案号
                "PatName", // 病人名称
                "Sex",// 性别
                "BirthDate",// 出生日期
                "Marriage",// 婚姻状况
                "Contacts",// 联系人
                "ContactTel",// 联系人电话
                "InDate", // 入院日期
                "TurnIn",// 转进时间
                "DeptCode", // 病人转进科室
                "StationCode", // 病人转进病区
                "RoomNo", // 病人转进房间
                "BedNo", // 病人转进病床
                "HisCode",// 病人转进医院
                "TurnOut",// 转出时间
                "OldDeptCode",// 病人转出科室
                "OldStationCode",// 病人转出病区
                "OldRoomNo",// 病人转出房间
                "OldBedNo", // 病人转出病床
                "OldHisCode", // 病人转出医院
                "AttendDr",// 主管医生
                "ChargeClass", // 费用类别
                "CaseNo",// 就诊序号
                "Diag", // 入院主诊断
                "SecondaryDiag", // 入院次诊断
                "OutDate", // 出院日期
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
    	//出院带药add by guoy 20150730------start----------
        String type = parm.getValue("EventType");
        if(type.equals("A03")){
        	String [] info = {
        			"IdNo",//身份证号
                    "Address",//住址
                    "VsDr",//经治医生
                    "OutDiag",//出院主诊断
                    "OpeIcd"//主手术名称
        	};
        	String icdCode = parm.getValue("OutDiagCode");
        	String opCode = parm.getValue("OpeIcdCode");
        	for(int i = 0;i < info.length; i++){
        		stringBuffer.append("    <" + info[i] + ">");
        		if(i == 3){
        			stringBuffer.append(icdCode);
        			if(!("".equals(icdCode))){
        				stringBuffer.append("^");
        			}
        			stringBuffer.append(parm.getValue(info[i]));
        		}else if(i == 4){
        			stringBuffer.append(opCode);
        			if(!("".equals(opCode))){
        				stringBuffer.append("^");
        			}
        			stringBuffer.append(parm.getValue(info[i]));
        		}else{
        			stringBuffer.append(parm.getValue(info[i]));
        		}
                stringBuffer.append("</" + info[i] + ">" + LINE_SEPARATOR);
        	}
        	
            stringBuffer.append("    <OrderList>" + LINE_SEPARATOR);
            TParm out = (TParm) parm.getData("outOrderParm");
            if(out.getCount()<=0){
            	stringBuffer.append("        <Order>" + LINE_SEPARATOR);
            	stringBuffer.append("            <OrderCode></OrderCode>" + LINE_SEPARATOR);//医嘱编码
            	stringBuffer.append("            <OrderDesc></OrderDesc>" + LINE_SEPARATOR);//医嘱名称
            	stringBuffer.append("            <MediQty></MediQty>" + LINE_SEPARATOR);//开药量
            	stringBuffer.append("            <MediUnit></MediUnit>" + LINE_SEPARATOR);//开药单位（数学单位）
            	stringBuffer.append("            <Route></Route>" + LINE_SEPARATOR);//用法
            	stringBuffer.append("            <Freq></Freq>" + LINE_SEPARATOR);//频次
            	stringBuffer.append("            <Days></Days>" + LINE_SEPARATOR);//天数
            	stringBuffer.append("            <DRNote></DRNote>" + LINE_SEPARATOR);//备注
            	stringBuffer.append("        </Order>" + LINE_SEPARATOR);
            	stringBuffer.append("    </OrderList>" + LINE_SEPARATOR);
            }else{
            	for(int j = 0;j < out.getCount(); j++ ){
                	stringBuffer.append("        <Order>" + LINE_SEPARATOR);
                	stringBuffer.append("            <OrderCode>"+out.getValue("ORDER_CODE",j)+"</OrderCode>" + LINE_SEPARATOR);//医嘱编码
                	stringBuffer.append("            <OrderDesc>"+out.getValue("ORDER_DESC",j)+"</OrderDesc>" + LINE_SEPARATOR);//医嘱名称
                	stringBuffer.append("            <MediQty>"+out.getValue("MEDI_QTY",j)+"</MediQty>" + LINE_SEPARATOR);//开药量
                	stringBuffer.append("            <MediUnit>"+out.getValue("UNIT_CHN_DESC",j)+"</MediUnit>" + LINE_SEPARATOR);//开药单位（数学单位）
                	stringBuffer.append("            <Route>"+out.getValue("ROUTE_CHN_DESC",j)+"</Route>" + LINE_SEPARATOR);//用法
                	stringBuffer.append("            <Freq>"+out.getValue("FREQ_CODE",j)+"</Freq>" + LINE_SEPARATOR);//频次
                	stringBuffer.append("            <Days>"+out.getValue("TAKE_DAYS",j)+"</Days>" + LINE_SEPARATOR);//天数
                	stringBuffer.append("            <DRNote>"+out.getValue("DR_NOTE",j)+"</DRNote>" + LINE_SEPARATOR);//备注
                	stringBuffer.append("        </Order>" + LINE_SEPARATOR);
                }
                stringBuffer.append("    </OrderList>" + LINE_SEPARATOR);
            }
        }
        //---------end--------------
        stringBuffer.append("</InPatInfo>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        //System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));	
        return TIOM_FileServer.writeFile(socket, path + "ADM_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
        
    }

    /**
     * 生成电视屏所需的科室信息XML文件
     * 
     * @param CASE_NO
     * @return
     */
    public TParm creatDeptXMLFile(String CASE_NO) {// wanglong add 20141010
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        // 住院信息
        TParm ADM_INP = ADMInpTool.getInstance().selectForXML(parm);
        if (ADM_INP.getErrCode() < 0) {
            err("ERR:" + ADM_INP.getErrCode() + ADM_INP.getErrText() + ADM_INP.getErrName());
            return ADM_INP;
        }
        // 数量信息
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String countSql =
                "SELECT (SELECT COUNT(CASE_NO) FROM ADM_INP "
                        + "  WHERE (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL) AND DS_DATE IS NULL "
                        + "    AND STATION_CODE IN(SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#')"
                        + "    AND DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#')) AS IN_STATION_COUNT, "
                        + "(SELECT COUNT(CASE_NO) FROM ADM_INP "
                        + "  WHERE (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL) AND DS_DATE IS NULL) AS IN_ADM_COUNT, "
                        + "(SELECT COUNT(DISTINCT A.CASE_NO) FROM ADM_INP A, ADM_TRANS_LOG B "
                        + "  WHERE A.CASE_NO = B.CASE_NO "
                        + "    AND (A.CANCEL_FLG <> 'Y' OR A.CANCEL_FLG IS NULL) "
                        + "    AND B.IN_STATION_CODE IN (SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#') "
                        + "    AND B.IN_DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#') "
                        + "    AND SUBSTR(B.IN_DATE, 0, 8) = '@') AS TRANS_IN_COUNT, "
                        + "(SELECT COUNT(DISTINCT A.CASE_NO) FROM ADM_INP A, ADM_TRANS_LOG B "
                        + "  WHERE A.CASE_NO = B.CASE_NO "
                        + "    AND (A.CANCEL_FLG <> 'Y' OR A.CANCEL_FLG IS NULL) "
                        // modify by wangb 2015/07/03 修正转出人数统计
                        + "    AND B.IN_STATION_CODE IN (SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#') "
                        + "    AND B.IN_DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#') "
                        + "    AND B.OUT_STATION_CODE IS NOT NULL "
                        + "    AND B.OUT_DEPT_CODE IS NOT NULL "
                        + "    AND TO_CHAR(B.OUT_DATE, 'YYYYMMDD') = '@') AS TRANS_OUT_COUNT, "
                        + "(SELECT COUNT(CASE_NO) FROM ADM_INP "
                        + "  WHERE TO_CHAR(DS_DATE, 'YYYYMMDD') = '@' "
                        + "    AND STATION_CODE IN (SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#')"
                        + "    AND DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#')) AS TODAY_OUT_COUNT, "
                        + "(SELECT COUNT(CASE_NO) FROM ADM_INP "
                        + "  WHERE TO_CHAR(DS_DATE - 1, 'YYYYMMDD') = '@' "
                        + "    AND STATION_CODE IN (SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#')"
                        + "    AND DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#')) AS TOMORROW_OUT_COUNT, "
                        + "(SELECT COUNT(CASE_NO) FROM OPE_OPBOOK "
                      //新的手术状态  0,申请；1,排程完毕；2,接患者；3,手术室交接；4,手术等待；5,手术开始；6,关胸；7,手术结束；8,返回病房
                        + "  WHERE STATE IN ('0','1','2','3','4','5','6') AND CANCEL_FLG = 'N' AND TO_CHAR(OP_DATE, 'YYYYMMDD') = '@'"
                        + "    AND OP_STATION_CODE IN (SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#')"
                        + "    AND OP_DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#')) AS TODAY_OP_COUNT, "
                        + "(SELECT COUNT(CASE_NO) FROM OPE_OPBOOK "
                        + "  WHERE STATE IN ('0','1','2','3','4','5','6') AND CANCEL_FLG = 'N' AND TO_CHAR(OP_DATE - 1, 'YYYYMMDD') = '@'"
                        + "    AND OP_STATION_CODE IN (SELECT STATION_CODE FROM ADM_INP WHERE CASE_NO = '#')"
                        + "    AND OP_DEPT_CODE IN (SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#')) AS TOMORROW_OP_COUNT "
                        + "FROM DUAL ";
        countSql = countSql.replaceAll("#", CASE_NO);
        countSql = countSql.replaceAll("@", StringTool.getString(sysDate, "yyyyMMdd"));
        TParm countParm = new TParm(TJDODBTool.getInstance().select(countSql));
        if (countParm.getErrCode() < 0) {
            err("ERR:" + countParm.getErrCode() + countParm.getErrText() + countParm.getErrName());
            return countParm;
        }
        // ===========开始组装数据
        TParm XMLParm = new TParm();
        XMLParm.setData("MrNo", ADM_INP.getValue("MR_NO", 0));// 病案号
        XMLParm.setData("DeptCode", ADM_INP.getValue("DEPT_CODE", 0));// 科室编码
        XMLParm.setData("StationCode", ADM_INP.getValue("STATION_CODE", 0));// 病区编码
        XMLParm.setData("InStationCount", countParm.getValue("IN_STATION_COUNT", 0));// 在病区人数
        XMLParm.setData("InADMCount", countParm.getValue("IN_ADM_COUNT", 0));// 入院人数
        XMLParm.setData("TransInCount", countParm.getValue("TRANS_IN_COUNT", 0));// 转入人数
        XMLParm.setData("TransOutCount", countParm.getValue("TRANS_OUT_COUNT", 0));// 转出人数
        XMLParm.setData("TodayOutCount", countParm.getValue("TODAY_OUT_COUNT", 0));// 今日出院
        XMLParm.setData("TomorrowOutCount", countParm.getValue("TOMORROW_OUT_COUNT", 0));// 明日出院
        XMLParm.setData("TodayOpCount", countParm.getValue("TODAY_OP_COUNT", 0));// 今日手术
        XMLParm.setData("TomorrowOpCount", countParm.getValue("TOMORROW_OP_COUNT", 0));// 明日手术
        if (!this.creatDeptXML(XMLParm)) {
            result.setErr(-1, "生成电视屏XML失败");
            return result;
        }
        TDS admStation = new TDS();
        String sql = "SELECT * FROM ADM_STATION_INFO WHERE DEPT_CODE='#' AND STATION_CODE='@'";
        sql = sql.replaceFirst("#", XMLParm.getValue("DeptCode"));
        sql = sql.replaceFirst("@", XMLParm.getValue("StationCode"));
        admStation.setSQL(sql);
        admStation.retrieve();
        int row = 0;
        int rowCount = admStation.rowCount();
        if (rowCount < 1) {
            row = admStation.insertRow();
        }
        admStation.setItem(row, "DEPT_CODE", XMLParm.getValue("DeptCode"));
        admStation.setItem(row, "STATION_CODE", XMLParm.getValue("StationCode"));
        admStation.setItem(row, "IN_STATION_COUNT", XMLParm.getInt("InStationCount"));
        admStation.setItem(row, "IN_ADM_COUNT", XMLParm.getInt("InADMCount"));
        admStation.setItem(row, "TRANS_IN_COUNT", XMLParm.getInt("TransInCount"));
        admStation.setItem(row, "TRANS_OUT_COUNT", XMLParm.getInt("TransOutCount"));
        admStation.setItem(row, "TODAY_OUT_COUNT", XMLParm.getInt("TodayOutCount"));
        admStation.setItem(row, "TOMORROW_OUT_COUNT", XMLParm.getInt("TomorrowOutCount"));
        admStation.setItem(row, "TODAY_OP_COUNT", XMLParm.getInt("TodayOpCount"));
        admStation.setItem(row, "TOMORROW_OP_COUNT", XMLParm.getInt("TomorrowOpCount"));
        admStation.setItem(row, "OPT_USER", Operator.getID());
        admStation.setItem(row, "OPT_DATE", sysDate);
        admStation.setItem(row, "OPT_TERM", Operator.getIP());
        admStation.setActive(row);
        boolean result1 = admStation.update();
        if (result1 == false) {
            result.setErr(-1, "数据库插入病区统计数据失败");
            return result;
        }
        return result;
    }

    /**
     * 根据数据生成XML文件
     * 
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean creatDeptXML(TParm parm) {// wanglong add 20141010
        StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <ADMStationInfo>" + LINE_SEPARATOR);
        String[] names = {"DeptCode",// 科室编码
                "StationCode",// 病区编码
                "InStationCount",// 在病区人数
                "InADMCount",// 入院人数
                "TransInCount",// 转入人数
                "TransOutCount",// 转出人数
                "TodayOutCount",// 今日出院
                "TomorrowOutCount",// 明日出院
                "TodayOpCount",// 今日手术
                "TomorrowOpCount"// 明日手术
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
        stringBuffer.append("  </ADMStationInfo>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        // System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
        // System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + nowStr + ".xml");
        return TIOM_FileServer.writeFile(socket, screenPath + "DEPT_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
    }
    
    /**
     * 生成电视屏所需的病患信息XML文件
     * @param CASE_NO
     * @return
     */
    public TParm creatPatXMLFile(String CASE_NO) {// wanglong add 20141010
        TParm result = new TParm();
        TParm XMLParm = creatPatParm(CASE_NO);
        if (!this.creatPatXML(XMLParm)) {
            result.setErr(-1, "生成电视屏XML失败");
            return result;
        }
        return result;
    }
    
    /**
     * 生成电视屏所需的病患信息XML文件
     * @param CASE_NO
     * @return
     */
    public TParm creatPatXMLFile(String CASE_NO, String SKINTEST_NOTE) {// wanglong add 20150527 增加皮试结果
        TParm result = new TParm();
        TParm XMLParm = creatPatParm(CASE_NO);
        XMLParm.setData("SKI", SKINTEST_NOTE);
        if (!this.creatPatXML(XMLParm)) {
            result.setErr(-1, "生成电视屏XML失败");
            return result;
        }
        return result;
    }
    
    /**
     * 生成电视屏所需的病患信息XML内容字符串
     * @param CASE_NO
     * @return
     */
    public String creatPatXMLStr(String CASE_NO) {// wanglong add 20141010
        TParm XMLParm = creatPatParm(CASE_NO);
        if (XMLParm == null || XMLParm.getErrCode() < 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        stringBuffer.append("<CallSysInterface>");
        stringBuffer.append("  <InPatInfo>");
        String[] names = {"DeptCode", // 科室编码
                "StationCode", // 病区编码
                "BedNo", // 床位号
                "RoomNo", // 病房号
                "MrNo", // 病案号
                "IpdNo", // 住院号
                "PatName", // 病患姓名
                "Sex", // 性别
                "Height", // 身高
                "Weight", // 体重
                "BloodType",// 血型
                "SpeciesCode", // 民族
                "BirthDate", // 出生日期 wanglong add 20140731
                "Marriage", // 婚姻状态 wanglong add 20140731
                "Contacts", // 联系人 wanglong add 20140731
                "ContactTel", // 联系人电话 wanglong add 20140731
                "InDate", // 入院日期
                "DirectorDr", // 主任医师
                "AttendDr", // 主管医生
                "AttendDrDept", // 主管医生科室 wanglong add 20140731
                "NurseCode",// 责任护士
                "NursingClass", // 护理等级
                "DieCondition", // 饮食情况
                "ChargeClass", // 费用类别
                "illState", // 病情
                "Cared", // 陪护
                "Toilet", // 入厕方式
                "Measure", // 计量方式
                "IsolationId", // 隔离情况
                "Allergies", // 过敏情况
                "Diag", // 入院主诊断
                "IsAdd", // 是否加床
                "OutDate", // 出院日期
                "TurnIn", // 转入日期
                "TurnOut", // 转出日期
                "AdmDays",// 住院天数
                "MedHistory",// 既往史
                "InfectionScreening",// 传筛结果
                "OPCode",// 手术ICD编码
                "OPDate",// 手术日期
                "OPDr",// 手术医生
                "HR",// 心率
                "BP" // 血压
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(XMLParm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">");
        }
        stringBuffer.append("  </InPatInfo>");
        stringBuffer.append("</CallSysInterface>");
        return stringBuffer.toString();
    }
    
    /**
     * 生成电视屏所需的病患信息Parm
     * @param CASE_NO
     * @return
     */
    public TParm creatPatParm(String CASE_NO) {// wanglong add 20141010
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        if (StringUtil.isNullString(CASE_NO)) {
            return null;
        }
        // 住院信息
        TParm ADM_INP = ADMInpTool.getInstance().selectForXML(parm);
        if (ADM_INP.getErrCode() < 0) {
            err("ERR:" + ADM_INP.getErrCode() + ADM_INP.getErrText() + ADM_INP.getErrName());
            return ADM_INP;
        }
        // 查询手术信息
//        TParm OpParm = new TParm();
//        OpParm.setData("CASE_NO", CASE_NO);
//        OpParm.setData("CANCEL_FLG", "N");
//        OpParm.setData("STATE", "1");// 已排程
//        TParm OP_DATA = OPEOpBookTool.getInstance().selectOpBook(OpParm);
        String opSql =
                "SELECT A.OP_DATE,A.OP_CODE1,A.MAIN_SURGEON FROM OPE_OPBOOK A "
                        + "WHERE A.CANCEL_FLG='N' AND A.CASE_NO='" + CASE_NO+"' "
                        +"AND OP_DATE<=SYSDATE "
                        +"ORDER BY A.OP_DATE DESC";//wanglong modify 20150330
        TParm OP_DATA = new TParm(TJDODBTool.getInstance().select(opSql));
        if (OP_DATA.getErrCode() < 0) {
            err("ERR:" + OP_DATA.getErrCode() + OP_DATA.getErrText() + OP_DATA.getErrName());
            return OP_DATA;
        }
        // 查询入院诊断
        TParm diagParm = new TParm();
        diagParm.setData("CASE_NO", CASE_NO);
        // modify by wangb 2015/06/08 病患主次诊断均发送至大屏 START
        diagParm.setData("IO_TYPE", "M");
//        diagParm.setData("MAINDIAG_FLG", "Y");
        String mainDiag = "";
        String secondaryDiag = "";
        TParm DIAG = ADMDiagTool.getInstance().queryData(diagParm);
        if (DIAG.getErrCode() < 0) {
            err("ERR:" + DIAG.getErrCode() + DIAG.getErrText() + DIAG.getErrName());
            return DIAG;
        }
        
        for (int i = 0; i < DIAG.getCount("CASE_NO"); i++) {
        	if (StringUtils.equals("Y", DIAG.getValue("MAINDIAG_FLG", i))) {
        		mainDiag = DIAG.getValue("ICD_CODE", i);
        	} else {
        		secondaryDiag = secondaryDiag + DIAG.getValue("ICD_CODE", i) + ";";
        	}
        }
        
        if (secondaryDiag.length() > 0) {
        	secondaryDiag = secondaryDiag.substring(0, secondaryDiag.length() - 1);
        }
        // modify by wangb 2015/06/08 病患主次诊断均发送至大屏 END
        
        // 查询病患转科信息 转入信息
        String sql =
                "SELECT TO_DATE(IN_DATE, 'YYYYMMDD HH24MISS') IN_DATE, IN_DEPT_CODE, IN_STATION_CODE, OUT_DATE, "
                        + "       OUT_DEPT_CODE, OUT_STATION_CODE " + "  FROM ADM_TRANS_LOG "
                        + " WHERE CASE_NO = '#' " + "ORDER BY IN_DATE DESC, OUT_DATE DESC";
        sql = sql.replaceFirst("#", CASE_NO);
        TParm TRANS = new TParm(TJDODBTool.getInstance().select(sql));
        if (TRANS.getErrCode() < 0) {
            err("ERR:" + TRANS.getErrCode() + TRANS.getErrText() + TRANS.getErrName());
            return TRANS;
        }
        // 体温信息
        String sumSql =
                "SELECT TO_DATE(EXAMINE_DATE || CASE WHEN EXAMINESESSION = '0' THEN '020000'  "
                        + "                          WHEN EXAMINESESSION = '1' THEN '060000'  "
                        + "                          WHEN EXAMINESESSION = '2' THEN '100000'  "
                        + "                          WHEN EXAMINESESSION = '3' THEN '140000'  "
                        + "                          WHEN EXAMINESESSION = '4' THEN '180000'  "
                        + "                          WHEN EXAMINESESSION = '5' THEN '220000'  "
                        + "                      ELSE '000000' END, 'YYYYMMDDHH24MISS') EXAMINE_DATE, "
                        + "       TO_CHAR(SYSTOLICPRESSURE) AS SYSTOLICPRESSURE,"
                        + "       TO_CHAR(DIASTOLICPRESSURE) AS DIASTOLICPRESSURE, "
                        + "       TO_CHAR(HEART_RATE) AS HEART_RATE,EXAMINESESSION, "
                        + "       TO_CHAR(PLUSE) AS PLUSE "
                        + "  FROM SUM_VTSNTPRDTL               "
                        + " WHERE CASE_NO = '#'                "
                        + "ORDER BY EXAMINE_DATE, EXAMINESESSION";
        sumSql = sumSql.replaceFirst("#", CASE_NO);
        TParm SUM = new TParm(TJDODBTool.getInstance().select(sumSql));
        // ===========开始组装数据
        TParm XMLParm = new TParm();
        XMLParm.setData("DeptCode", ADM_INP.getValue("DEPT_CODE", 0));// 病区CODE
        XMLParm.setData("StationCode", ADM_INP.getValue("STATION_CODE", 0));// 病区CODE
        XMLParm.setData("BedNo", ADM_INP.getValue("BED_NO", 0));// 床号
        XMLParm.setData("RoomNo", ADM_INP.getValue("ROOM_CODE", 0));// 病房号
        XMLParm.setData("MrNo", ADM_INP.getValue("MR_NO", 0));// 病案号
        XMLParm.setData("IpdNo", ADM_INP.getValue("IPD_NO", 0));// 住院号
        XMLParm.setData("CaseNo", CASE_NO);// 就诊号
        XMLParm.setData("PatName", ADM_INP.getValue("PAT_NAME", 0));// 姓名
        XMLParm.setData("Sex", ADM_INP.getValue("SEX_CODE", 0)); // 性别
        TParm patInfo = PatTool.getInstance().getInfoForMrno(ADM_INP.getValue("MR_NO", 0));
        XMLParm.setData("BloodType", patInfo.getValue("BLOOD_TYPE", 0));// 血型
        
        XMLParm.setData("BloodType",patInfo.getValue("BLOOD_TYPE", 0));// A B O 血型
        XMLParm.setData("BloodRhType",patInfo.getValue("BLOOD_RH_TYPE", 0));// RH 血型
        TParm admInp = ADMInpTool.getInstance().selectall(parm);
        
        if(admInp.getValue("INFECT_SCR_RESULT", 0).equals("有")){
        	XMLParm.setData("InfectionScreening","阳性");
        }else if(admInp.getValue("INFECT_SCR_RESULT", 0).equals("正常")){
        	XMLParm.setData("InfectionScreening","阴性");
        }else if(StringUtil.isNullString(admInp.getValue("INFECT_SCR_RESULT", 0))){
        	XMLParm.setData("InfectionScreening","");
        }
        
        XMLParm.setData("Height", admInp.getInt("HEIGHT", 0));// 身高
        XMLParm.setData("Weight", StringTool.round(admInp.getDouble("WEIGHT", 0), 1));// 体重
        XMLParm.setData("SpeciesCode", patInfo.getValue("SPECIES_CODE", 0));// 民族
        XMLParm.setData("BirthDate",
                        StringTool.getString(ADM_INP.getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));// 出生日期
                                                                                                   // wanglong
                                                                                                   // add
                                                                                                   // 20140731
        XMLParm.setData("Marriage", patInfo.getValue("MARRIAGE_CODE", 0));// 婚姻状态 wanglong add
                                                                          // 20140731
        XMLParm.setData("Contacts", patInfo.getValue("CONTACTS_NAME", 0));// 联系人 wanglong add
                                                                          // 20140731
        XMLParm.setData("ContactTel", patInfo.getValue("CONTACTS_TEL", 0));// 联系人电话 wanglong add
                                                                           // 20140731
        XMLParm.setData("InDate", StringTool.getString(ADM_INP.getTimestamp("IN_DATE", 0),
                                                       "yyyy-MM-dd HH:mm:ss"));// 入院日期
        XMLParm.setData("DirectorDr", ADM_INP.getValue("DIRECTOR_DR", 0));// 主任医生
        XMLParm.setData("AttendDr", ADM_INP.getValue("VS_DR_CODE", 0));// 主管医生
        String attendDrDept =
                StringUtil.getDesc("ADM_INP A, SYS_OPERATOR_DEPT B", "B.DEPT_CODE",
                                   "A.VS_DR_CODE = B.USER_ID AND B.MAIN_FLG = 'Y' "
                                           + "AND A.CASE_NO = '" + CASE_NO + "'");
        XMLParm.setData("AttendDrDept", attendDrDept);// 主管医生科室
        XMLParm.setData("NurseCode", ADM_INP.getValue("NURSE_CODE", 0));// 责任护士
        XMLParm.setData("NursingClass", ADM_INP.getValue("NURSING_CLASS", 0));// 护理等级
        XMLParm.setData("DieCondition", ADM_INP.getValue("DIE_CONDITION", 0));// 饮食情况
        XMLParm.setData("ChargeClass", ADM_INP.getValue("CTZ1_CODE", 0));// 费用类别
        XMLParm.setData("illState", ADM_INP.getValue("PATIENT_STATUS", 0));// 病情
        XMLParm.setData("Cared", ADM_INP.getInt("CARE_NUM", 0) > 0 ? "是" : "否");// 陪护
        XMLParm.setData("Toilet", ADM_INP.getValue("TOILET", 0));// 入厕方式
        XMLParm.setData("Measure", ADM_INP.getValue("IO_MEASURE", 0));// 计量方式
        XMLParm.setData("IsolationId", ADM_INP.getValue("ISOLATION", 0));// 隔离情况
        XMLParm.setData("Allergies", ADM_INP.getValue("ALLERGY", 0).equals("Y") ? "是" : "否");// 过敏情况
        // modify by wangb 2015/06/08 病患主次诊断均发送至大屏 START
        XMLParm.setData("Diag", mainDiag);// 入院主诊断
        XMLParm.setData("SecondaryDiag", secondaryDiag);// 入院次诊断
        // modify by wangb 2015/06/08 病患主次诊断均发送至大屏 END
        XMLParm.setData("IsAdd", ADM_INP.getValue("ISADD", 0));// 是否加床
        XMLParm.setData("OutDate", StringTool.getString(ADM_INP.getTimestamp("DS_DATE", 0),
                                                        "yyyy-MM-dd HH:mm:ss"));// 出院日期
        if (ADM_INP.getValue("CANCEL_FLG", 0).equals("Y")) {
            // 转科信息
            String transSql =
                    "SELECT A.CHG_DATE, A.DEPT_CODE, A.STATION_CODE, A.BED_NO, B.ROOM_CODE AS ROOM_NO "
                            + "  FROM ADM_CHG A, SYS_BED B        "
                            + " WHERE A.PSF_KIND = 'INC'          "
                            + "   AND A.BED_NO = B.BED_NO(+)      "
                            + "   AND A.CASE_NO = '#'             "
                            + "ORDER BY SEQ_NO DESC               ";
            transSql = transSql.replaceFirst("#", CASE_NO);
            TParm TRANS_LOG = new TParm(TJDODBTool.getInstance().select(transSql));
            if (TRANS_LOG.getErrCode() < 0) {
                err("ERR:" + TRANS_LOG.getErrCode() + TRANS_LOG.getErrText()
                        + TRANS_LOG.getErrName());
                return TRANS_LOG;
            }
            XMLParm.setData("OutDate", StringTool.getString(TRANS_LOG.getTimestamp("CHG_DATE", 0),
                                                            "yyyy-MM-dd HH:mm:ss"));// 出院日期
        }
        XMLParm.setData("TurnIn", StringTool.getString(TRANS.getTimestamp("IN_DATE", 0),
                                                       "yyyy-MM-dd HH:mm:ss"));// 转入日期
        XMLParm.setData("TurnOut", StringTool.getString(TRANS.getTimestamp("OUT_DATE", 0),
                                                        "yyyy-MM-dd HH:mm:ss"));// 转出日期
        Timestamp sysDate = SystemTool.getInstance().getDate();
        int dateOffset = StringTool.getDateDiffer(sysDate, ADM_INP.getTimestamp("IN_DATE", 0));
        if (dateOffset == 0) {
            XMLParm.setData("AdmDays", 1);// 住院天数
        } else {
            XMLParm.setData("AdmDays", dateOffset);// 住院天数
        }
        XMLParm.setData("MedHistory", "");// 既往史
      //  XMLParm.setData("InfectionScreening", "");// 传筛结果
        int index = 0;
//        for (int i = 0; i < OP_DATA.getCount(); i++) {
//            int temp = StringTool.getDateDiffer(OP_DATA.getTimestamp("OP_DATE", i), sysDate);
//            if (temp > 0 && (differ == 0 || temp < differ)) {
//                differ = temp;
//                index = i;
//            }
//        }
      //add by chenhj start
        if(OP_DATA.getCount()>0){
            XMLParm.setData("OPCode", OP_DATA.getValue("OP_CODE1", 0));// 手术名称
            XMLParm.setData("OPDate", StringTool.getString(OP_DATA.getTimestamp("OP_DATE", 0),
                                                           "yyyy-MM-dd HH:mm:ss"));// 手术日期
            XMLParm.setData("OPDr", OP_DATA.getValue("MAIN_SURGEON", 0));// 手术医生
        }
        if(!StringUtil.isNullString(OP_DATA.getValue("OP_CODE1", 0))){
        	DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
            Timestamp opDate=OP_DATA.getTimestamp("OP_DATE", 0);
        	String opDateString=sdf.format(opDate).replace("-", "");
            index=0;
          	for (int i = 0; i < SUM.getCount(); i++) {
          		String examineDate = sdf.format(SUM.getTimestamp("EXAMINE_DATE", i)).replace("-", "");
          		if(opDateString.equals(examineDate)&&SUM.getValue("EXAMINESESSION", i).equals("1")){
          			index=i;
          		}
          	}
          	//XMLParm.setData("HR",SUM.getValue("HEART_RATE",index));// 心率
          	XMLParm.setData("P",SUM.getValue("PLUSE",index));// 脉搏
          	if(StringUtil.isNullString(SUM.getValue("SYSTOLICPRESSURE",index).toString())&&
               	   StringUtil.isNullString(SUM.getValue("DIASTOLICPRESSURE",index).toString())){
               		 XMLParm.setData("BP","");// 血压
               	}else{ 
               		 XMLParm.setData("BP",SUM.getValue("SYSTOLICPRESSURE",index)
               				              +"/"+
               	                          SUM.getValue("DIASTOLICPRESSURE",index));
             }
        }
         //add by chenhj end
        
        // add by wangb 2017/5/3 保留气管插管停用时间(拔管时间) START
        String tcOrderCode = TConfig.getSystemValue("TC_ORDER_CODE");// 气管插管医嘱
  		if (StringUtils.isEmpty(tcOrderCode)) {
  			// 拔管时间
  			XMLParm.setData("ExtubationTime", "");
  		} else {
  			String tcSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO = '"
  					+ CASE_NO + "' AND ORDER_CODE = '" + tcOrderCode
  					+ "' AND DC_NS_CHECK_DATE IS NOT NULL ORDER BY ORDER_NO DESC";
  			TParm tcParm = new TParm(TJDODBTool.getInstance().select(tcSql));
  			// 拔管时间
  			XMLParm.setData("ExtubationTime", StringTool.getString(tcParm
  					.getTimestamp("DC_DATE", 0), "yyyy-MM-dd HH:mm:ss"));
  		}
  		// add by wangb 2017/5/3 保留气管插管停用时间(拔管时间) END
  		
  		//add by huangtt 20170502 预转出时间  start
		String sqlP = "SELECT TO_CHAR(C.PRETREAT_DATE,'YYYY-MM-DD HH24:MI:SS') PRETREAT_DATE,"
				+ " PRETREAT_IN_DEPT,PRETREAT_IN_STATION "
				+ " FROM ADM_INP A,SYS_BED B,ADM_PRETREAT C "
				+ " WHERE A.CASE_NO='"
				+ CASE_NO
				+ "' AND A.BED_NO=B.BED_NO"
				+ " AND B.PRE_FLG='Y'"
				+ " AND B.PRETREAT_OUT_NO=C.PRETREAT_NO";
        TParm parmP = new TParm(TJDODBTool.getInstance().select(sqlP));
        if(parmP.getCount() > 0){
            XMLParm.setData("PreTurnOut", parmP.getValue("PRETREAT_DATE", 0));
            XMLParm.setData("PreTurnInDept", parmP.getValue("PRETREAT_IN_DEPT", 0));
            XMLParm.setData("PreTurnInStation", parmP.getValue("PRETREAT_IN_STATION", 0));
        }else{
        	XMLParm.setData("PreTurnOut", "");
        	XMLParm.setData("PreTurnInDept", "");
        	XMLParm.setData("PreTurnInStation", "");
        }
 
        //add by huangtt 20170502 预转出时间  end
  		
        return XMLParm;
    }

    /**
     * 根据数据生成XML文件
     * 
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean creatPatXML(TParm parm) {// wanglong add 20141010
        StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <InPatInfo>" + LINE_SEPARATOR);
        String[] names = {"DeptCode", // 科室编码
                "StationCode", // 病区编码
                "BedNo", // 床位号
                "RoomNo", // 病房号
                "MrNo", // 病案号
                "IpdNo", // 住院号
                "CaseNo",// 就诊号
                "PatName", // 病患姓名
                "Sex", // 性别
                "Height", // 身高
                "Weight", // 体重
                "BloodType",// A B O血型
                "BloodRhType",//Rh 血型
                "SpeciesCode", // 民族
                "BirthDate", // 出生日期 wanglong add 20140731
                "Marriage", // 婚姻状态 wanglong add 20140731
                "Contacts", // 联系人 wanglong add 20140731
                "ContactTel", // 联系人电话 wanglong add 20140731
                "InDate", // 入院日期
                "DirectorDr", // 主任医师
                "AttendDr", // 主管医生
                "AttendDrDept", // 主管医生科室 wanglong add 20140731
                "NurseCode",// 责任护士
                "NursingClass", // 护理等级
                "DieCondition", // 饮食情况
                "ChargeClass", // 费用类别
                "illState", // 病情
                "Cared", // 陪护
                "Toilet", // 入厕方式
                "Measure", // 计量方式
                "IsolationId", // 隔离情况
                "Allergies", // 过敏情况
                "Diag", // 入院主诊断
                "SecondaryDiag", // 入院次诊断  modify by wangb 2015/06/08 病患主次诊断均发送至大屏
                "IsAdd", // 是否加床
                "OutDate", // 出院日期
                "TurnIn", // 转入日期
                "TurnOut", // 转出日期
                "AdmDays",// 住院天数
                "MedHistory",// 既往史
                "InfectionScreening",// 传筛结果
                "OPCode",// 手术ICD编码
                "OPDate",// 手术日期
                "OPDr",// 手术医生
                "HR",// 心率
                "BP", // 血压
                "SKI", // 皮试 20150527
                "ExtubationTime", // 拔管时间
                "TransferToICUTime", //转入ICU时间 20170503 add by huangtt
                "PreTurnOut", //预转出时间 20170502 add by huangtt
                "PreTurnInDept", // 预转入科室 2017/5/15 add by wangb
                "PreTurnInStation" // 预转入病区 2017/5/15 add by wangb
        };
        
        for (int i = 0; i < names.length; i++) {
        	// add by wangb 2017/5/3 如果拔管时间为空则不传该节点 START
        	if (StringUtils.isEmpty(parm.getValue("ExtubationTime"))) {
        		if ("ExtubationTime".equals(names[i])) {
        			continue;
        		}
        	}
        	// add by wangb 2017/5/3 如果拔管时间为空则不传该节点 END
        	
        	//add by huangtt 20170503 转入ICU时间如果为空将不组装节点 start
        	if(names[i].equals("TransferToICUTime") && parm.getValue(names[i]).trim().length() == 0){
        		continue;
        	}
        	//add by huangtt 20170503 转入ICU时间如果为空将不组装节点 end
        	
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
        stringBuffer.append("  </InPatInfo>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        // System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
        // System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + nowStr + ".xml");
        return TIOM_FileServer.writeFile(socket, screenPath + "PAT_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
    }
    /**
     * 根据数据生成皮试的XML文件
     * @param parm
     * @return
     */
    public boolean creatPsXML(TParm parm) {//add wuxy 20170504
    	StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <SkinTestInfo>" + LINE_SEPARATOR);
        String[] names = {
        		"MrNo",
        		"CaseNo",
        		"OrderCode", // 药品代码
                "OrderDesc", // 药品名称
                "SkinTestResult", // 皮试结果
                "SkinTestBatchNo" // 皮试代码
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
        stringBuffer.append("  </SkinTestInfo>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        // System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
        // System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + nowStr + ".xml");
        return TIOM_FileServer.writeFile(socket, screenPath + "PS_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
    }
    /**
     * 生成电视屏所需的皮试信息XML文件
     * @param parm
     * @return
     */
    public TParm creatPsXMLFile(TParm parm) {// add wuxy 20170504
        TParm result = new TParm();
            result.setErr(-1, "生成电视屏XML失败");
            if (!this.creatPsXML(parm)) {
            return result;
        }
        return result;
    }
    
    
    /**
     * 生成电视屏所需的手术信息XML文件
     * @param CASE_NO
     * @param OPBOOK_SEQ
     * @return
     */
    public TParm creatOPEInfoXMLFile(String CASE_NO, String OPBOOK_SEQ) {// wanglong add 20141010
        TParm result = new TParm();
        TParm XMLParm = creatOPEInfoParm(CASE_NO, OPBOOK_SEQ);
        // add by wangb 2017/09/04 手术申请单为空时不发送数据
        if (XMLParm.getErrCode() < 0) {
        	return XMLParm;
        }
        if (!this.creatOPEInfoXML(XMLParm)) {
            result.setErr(-1, "生成电视屏XML失败");
            return result;
        }
        return result;
    }

    /**
     * 生成电视屏所需的手术信息XML内容字符串
     * @param CASE_NO
     * @return
     */
    public String creatOPEInfoXMLStr(String CASE_NO, String OPBOOK_SEQ) {// wanglong add 20141010
        TParm XMLParm = creatOPEInfoParm(CASE_NO, OPBOOK_SEQ);
        if (XMLParm == null || XMLParm.getErrCode() < 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        stringBuffer.append("<CallSysInterface>");
        stringBuffer.append("  <OperationInfo>");
        String[] names = {"OPBookSeq",// 手术单号
                "OperationType",// 手术种类
                "OperationStatus",// 手术状态
                "MrNo",// 病案号
                "PatName",// 病患姓名
                "DeptCode",// 科室
                "StationCode",// 病区
                "BedNo",// 床号
                "PatSex",// 性别
                "PatAge",// 年龄
                "PatHeight",// 身高
                "PatWeight",// 体重
                "DiagCode",// 术前诊断
                "OperationCode",// 拟行手术(主)
                "SecondaryOperationCode",// 拟行手术(次)
                "OperationDate",// 手术日期
                "OperationRoom",//术间 wanglong add 20150114
                "BookDRCode",// 术者
                "BookAST1",// 一助
                "BookAST2",// 二助
                "Nurse",// 护士
                "ANACode",// 麻醉方式
                "ANAUser",// 麻醉师
                "ExtraUser",// 体外
                "Remark", // 备注
                "GrantAid",// 资助金额
                "CancelFlg"// 取消注记(Y_已取消,N_未取消)
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(XMLParm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">");
        }
        stringBuffer.append("  </OperationInfo>");
        stringBuffer.append("</CallSysInterface>");
        return stringBuffer.toString();
    }
    
    /**
     * 生成电视屏所需的手术信息Parm
     * @param CASE_NO
     * @return
     */
    public TParm creatOPEInfoParm(String CASE_NO, String OPBOOK_SEQ) {// wanglong add 20141010
    	// add by wangb 2017/09/04 手术单号为空时不发送消息
    	TParm XMLParm = new TParm();
    	if (StringUtils.isEmpty(OPBOOK_SEQ)) {
    		XMLParm.setErr(-1, "手术申请单号为空");
    		return XMLParm;
    	}
        // 手术信息
        String opeSql =
                "SELECT A.OPBOOK_SEQ, A.MR_NO, C.PAT_NAME, B.DEPT_CODE, B.STATION_CODE, B.BED_NO, C.SEX_CODE PAT_SEX, "
                        + "       FLOOR(MONTHS_BETWEEN( SYSDATE, C.BIRTH_DATE) / 12) AS PAT_AGE, B.HEIGHT PAT_HEIGHT, "
                        + "       B.WEIGHT PAT_WEIGHT,A.TYPE_CODE,A.STATE_TIME_0,A.STATE_TIME_1,A.STATE_TIME_2,A.STATE_TIME_3, "
                        + "       CASE WHEN D.OP_CODE1 IS NOT NULL THEN D.OP_CODE1 ELSE A.OP_CODE1 END OP_CODE1, "
                        // add by wangb 手术次诊断
                        + "       CASE WHEN D.OP_CODE2 IS NOT NULL THEN D.OP_CODE2 ELSE A.OP_CODE2 END OP_CODE2, "
                        + "       CASE WHEN D.OP_DATE IS NOT NULL THEN D.OP_DATE ELSE A.OP_DATE END OPERATION_DATE, "
                        + "       CASE WHEN D.ROOM_NO IS NOT NULL THEN D.ROOM_NO ELSE A.ROOM_NO END OPERATION_ROOM, "
                        + "       CASE WHEN D.MAIN_SURGEON IS NOT NULL THEN D.MAIN_SURGEON ELSE A.MAIN_SURGEON END BOOK_DR_CODE, "
                        + "       CASE WHEN D.REAL_AST1 IS NOT NULL THEN D.REAL_AST1 ELSE A.BOOK_AST_1 END BOOK_AST_1, "
                        + "       CASE WHEN D.REAL_AST2 IS NOT NULL THEN D.REAL_AST2 ELSE A.BOOK_AST_2 END BOOK_AST_2, "
                        + "       CASE WHEN D.ANA_CODE IS NOT NULL THEN D.ANA_CODE ELSE A.ANA_CODE END ANA_CODE, "
                        + "       CASE WHEN D.ANA_USER1 IS NOT NULL THEN D.ANA_USER1 ELSE A.ANA_USER1 END ANA_USER1, "
                        + "       CASE WHEN D.ANA_USER2 IS NOT NULL THEN D.ANA_USER2 ELSE A.ANA_USER2 END ANA_USER2, "
                        + "       CASE WHEN D.EXTRA_USER1 IS NOT NULL THEN D.EXTRA_USER1 ELSE A.EXTRA_USER1 END EXTRA_USER1, "
                        + "       CASE WHEN D.EXTRA_USER2 IS NOT NULL THEN D.EXTRA_USER2 ELSE A.EXTRA_USER2 END EXTRA_USER2, "
                        + "       CASE WHEN D.CIRCULE_USER1 IS NOT NULL THEN D.CIRCULE_USER1 ELSE A.CIRCULE_USER1 END CIRCULE_USER, "
                        + "       CASE WHEN D.SCRUB_USER1 IS NOT NULL THEN D.SCRUB_USER1 ELSE A.SCRUB_USER1 END SCRUB_USER, "
                        + "       CASE WHEN D.DIAG_CODE1 IS NOT NULL THEN D.DIAG_CODE1 ELSE A.DIAG_CODE1 END DIAG_CODE, "
                        + "       A.REMARK, A.GRANT_AID, D.CIRCULE_USER1 NURSE, D.OP_RECORD_NO, A.STATE, A.CANCEL_FLG, C.BLOOD_TYPE, C.BLOOD_RH_TYPE, B.INFECT_SCR_RESULT "
                        + "  FROM OPE_OPBOOK A, ADM_INP B, SYS_PATINFO C, OPE_OPDETAIL D "
                        + " WHERE A.OPBOOK_SEQ = '#' "
                        + "   AND A.CASE_NO = '@' "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   AND B.MR_NO = C.MR_NO "
                        + "   AND A.OPBOOK_SEQ = D.OPBOOK_NO(+) ";
        opeSql = opeSql.replaceAll("#", OPBOOK_SEQ);
        opeSql = opeSql.replaceAll("@", CASE_NO);
        TParm opeParm = new TParm(TJDODBTool.getInstance().select(opeSql));
        if (opeParm.getErrCode() < 0) {
            err("ERR:" + opeParm.getErrCode() + opeParm.getErrText() + opeParm.getErrName());
            return opeParm;
        }
        int StateSeq = -1;
        if(opeParm.getValue("TYPE_CODE", 0).equals("1")){// 1手术室手术 wanglong modify 20150330
          //新的手术状态  0,申请；1,排程完毕；2,接患者；3,手术室交接；4,手术等待；5,手术开始；6,关胸；7,手术结束；8,返回病房
            if (!opeParm.getValue("STATE_TIME_3", 0).equals("")) {
                StateSeq = 7;
            } else if (!opeParm.getValue("STATE_TIME_2", 0).equals("")) {
                StateSeq = 6;
            } else if (!opeParm.getValue("STATE_TIME_1", 0).equals("")) {
                StateSeq = 5;
            } else if (!opeParm.getValue("STATE_TIME_0", 0).equals("")) {
                StateSeq = 2;
            } else {
            	// add by wangb 2015/07/08 根据手术的排程状态更新大屏
            	if (StringUtils.equals("0", opeParm.getValue("STATE", 0))) {
            		StateSeq = 0;
            	} else if (StringUtils.equals("1", opeParm.getValue("STATE", 0))) {
            		StateSeq = 1;
            	}
            }
        } else {// 2介入手术
            if (!opeParm.getValue("STATE_TIME_3", 0).equals("")) {
                StateSeq = 7;
            } else if (!opeParm.getValue("STATE_TIME_2", 0).equals("")) {
                StateSeq = 5;
            } else if (!opeParm.getValue("STATE_TIME_1", 0).equals("")) {
                StateSeq = 4;
            } else if (!opeParm.getValue("STATE_TIME_0", 0).equals("")) {
                StateSeq = 2;
            } else {// add by wangb 2015/06/08 介入手术申请时发送消息
            	// modify by wangb 2015/06/25 根据介入手术的排程状态更新大屏
            	if (StringUtils.equals("0", opeParm.getValue("STATE", 0))) {
            		StateSeq = 0;
            	} else if (StringUtils.equals("1", opeParm.getValue("STATE", 0))) {
            		StateSeq = 1;
            	}
            }
        }
        // ===========开始组装数据
        XMLParm.setData("OPBookSeq", OPBOOK_SEQ);// 手术单号
        XMLParm.setData("OperationType", opeParm.getValue("TYPE_CODE", 0));// 手术类型
        XMLParm.setData("OperationStatus", StateSeq);// 手术状态
        XMLParm.setData("MrNo", opeParm.getValue("MR_NO", 0));// 病案号
        XMLParm.setData("PatName", opeParm.getValue("PAT_NAME", 0));// 病患姓名
        XMLParm.setData("DeptCode", opeParm.getValue("DEPT_CODE", 0));// 科室
        XMLParm.setData("StationCode", opeParm.getValue("STATION_CODE", 0));// 病区
        XMLParm.setData("BedNo", opeParm.getValue("BED_NO", 0));// 床号
        XMLParm.setData("PatSex", opeParm.getValue("PAT_SEX", 0));// 性别
        XMLParm.setData("PatAge", opeParm.getValue("PAT_AGE", 0));// 年龄
        XMLParm.setData("PatHeight", opeParm.getInt("PAT_HEIGHT", 0));// 身高
        XMLParm.setData("PatWeight", StringTool.round(opeParm.getDouble("PAT_WEIGHT", 0), 1));// 体重
        XMLParm.setData("DiagCode", opeParm.getValue("DIAG_CODE", 0));// 术前诊断
        XMLParm.setData("OperationCode", getOpeIcdDesc(opeParm.getValue("OP_CODE1", 0)));// 拟行手术(主)  modify by huangtt 20170503 code 改为desc将手术主诊断代码变成中文
        XMLParm.setData("SecondaryOperationCode", getOpeIcdDesc(opeParm.getValue("OP_CODE2", 0)));// 拟行手术(次)
        XMLParm.setData("OperationDate", StringTool.getString(opeParm
                .getTimestamp("OPERATION_DATE", 0), "yyyy-MM-dd HH:mm:ss"));// 手术时间
        XMLParm.setData("OperationRoom", opeParm.getValue("OPERATION_ROOM", 0));// 术间 wanglong add 20150114
        XMLParm.setData("BookDRCode", opeParm.getValue("BOOK_DR_CODE", 0));// 术者
        XMLParm.setData("BookAST1", opeParm.getValue("BOOK_AST_1", 0));// 一助
        XMLParm.setData("BookAST2", opeParm.getValue("BOOK_AST_2", 0));// 二助
        XMLParm.setData("Nurse", opeParm.getValue("NURSE", 0));// 护士
        XMLParm.setData("ANACode", opeParm.getValue("ANA_CODE", 0));// 麻醉方式
        XMLParm.setData("ANAUser1", opeParm.getValue("ANA_USER1", 0));// 主麻醉师
        XMLParm.setData("ANAUser2", opeParm.getValue("ANA_USER2", 0));// 次麻醉师
        XMLParm.setData("ExtraUser1", opeParm.getValue("EXTRA_USER1", 0));// 体外1
        XMLParm.setData("ExtraUser2", opeParm.getValue("EXTRA_USER2", 0));// 体外2
        XMLParm.setData("CirculatingUser", opeParm.getValue("CIRCULE_USER", 0));// 巡回护士
        XMLParm.setData("ScrubUser", opeParm.getValue("SCRUB_USER", 0));// 洗手护士
        XMLParm.setData("Remark", opeParm.getValue("REMARK", 0));// 备注
        XMLParm.setData("GrantAid", opeParm.getDouble("GRANT_AID", 0));// 资助金额
        XMLParm.setData("CancelFlg", opeParm.getValue("CANCEL_FLG", 0));// 资助金额
        

        
       
        
        return XMLParm;
    }
    
    /**
     * 根据数据生成XML文件
     * 
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean creatOPEInfoXML(TParm parm) {// wanglong add 20141010
        StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <OperationInfo>" + LINE_SEPARATOR);
        String[] names = {"OPBookSeq",// 手术单号
                "OperationType",// 手术种类
                "OperationStatus",// 手术状态
                "MrNo",// 病案号
                "PatName",// 病患姓名
                "DeptCode",// 科室
                "StationCode",// 病区
                "BedNo",// 床号
                "PatSex",// 性别
                "PatAge",// 年龄
                "PatHeight",// 身高
                "PatWeight",// 体重
                "DiagCode",//术前诊断
                "OperationCode",// 拟行手术(主)
                "SecondaryOperationCode",// 拟行手术(次)
                "OperationDate",//手术日期
                "OperationRoom",//术间 wanglong add 20150114
                "BookDRCode",// 术者
                "BookAST1",// 一助
                "BookAST2",// 二助
                "Nurse",// 护士
                "ANACode",// 麻醉方式
                "ANAUser1",// 主麻醉师
                "ANAUser2",// 次麻醉师
                "ExtraUser1",// 体外1
                "ExtraUser2",// 体外2
                "CirculatingUser",// 巡回护士
                "ScrubUser",// 洗手护士
                "Remark", // 备注
                "GrantAid",// 资助金额
                "CancelFlg"// 取消注记(Y_已取消,N_未取消)
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
        stringBuffer.append("  </OperationInfo>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        // System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
        // System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + nowStr + ".xml");
        return TIOM_FileServer.writeFile(socket, screenPath + "OPE_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
    }

    /**
     * 生成电视屏所需的手术状态更新XML文件
     * 
     * @param CASE_NO
     * @param OPBOOK_SEQ
     * @return
     */
    public TParm creatOPEStateXMLFile(String CASE_NO, String OPBOOK_SEQ) {// wanglong add 20141010
        TParm result = new TParm();
        // 手术信息
        String opeSql =
                "SELECT MR_NO,TYPE_CODE,STATE_TIME_0,STATE_TIME_1,STATE_TIME_2,STATE_TIME_3,STATE "
                        + " FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '#' AND CASE_NO = '@' ";
        opeSql = opeSql.replaceAll("#", OPBOOK_SEQ);
        opeSql = opeSql.replaceAll("@", CASE_NO);
        TParm opeParm = new TParm(TJDODBTool.getInstance().select(opeSql));
        if (opeParm.getErrCode() < 0) {
            err("ERR:" + opeParm.getErrCode() + opeParm.getErrText() + opeParm.getErrName());
            return opeParm;
        }
        
        // add by wangb 2017/6/26 避免传送空文件
        if (opeParm.getCount() < 1) {
        	result.setErrCode(-1);
        	return result;
        }
        
        String StateSeq = "-1";
        if (opeParm.getValue("TYPE_CODE", 0).equals("1")) {// 1手术室手术 wanglong modify 20150330
          //新的手术状态  0,申请；1,排程完毕；2,接患者；3,手术室交接；4,手术等待；5,手术开始；6,关胸；7,手术结束；8,返回病房
            if (!opeParm.getValue("STATE_TIME_3", 0).equals("")) {
                StateSeq = "7";
            } else if (!opeParm.getValue("STATE_TIME_2", 0).equals("")) {
                StateSeq = "6";
            } else if (!opeParm.getValue("STATE_TIME_1", 0).equals("")) {
                StateSeq = "5";
            } else if (!opeParm.getValue("STATE_TIME_0", 0).equals("")) {
                StateSeq = "2";
            } else {
            	// add by wangb 2015/07/08 根据手术的排程状态更新大屏
            	// modify by wangb 2015/12/18
            	StateSeq = opeParm.getValue("STATE", 0);
            }
        } else {// 2介入手术
            if (!opeParm.getValue("STATE_TIME_3", 0).equals("")) {
                StateSeq = "7";
            } else if (!opeParm.getValue("STATE_TIME_2", 0).equals("")) {
                StateSeq = "5";
            } else if (!opeParm.getValue("STATE_TIME_1", 0).equals("")) {
                StateSeq = "4";
            } else if (!opeParm.getValue("STATE_TIME_0", 0).equals("")) {
                StateSeq = "2";
            } else {
            	// add by wangb 2015/07/08 根据手术的排程状态更新大屏
            	// modify by wangb 2015/12/18
            	StateSeq = opeParm.getValue("STATE", 0);
            }
        }
//        String OperationStatus =
//                StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "ID='" + StateSeq
//                        + "' AND GROUP_ID='OPE_STATE" + opeParm.getValue("TYPE_CODE", 0) + "'");
        // ===========开始组装数据
        TParm XMLParm = new TParm();
        XMLParm.setData("OPBookSeq", OPBOOK_SEQ);// 手术单号
        XMLParm.setData("MrNo", opeParm.getValue("MR_NO", 0));// 病案号
        XMLParm.setData("PatName",
                        StringUtil.getDesc("SYS_PATINFO", "PAT_NAME",
                                           "MR_NO='" + opeParm.getValue("MR_NO", 0) + "'"));// 病患姓名
        XMLParm.setData("OperationType", opeParm.getValue("TYPE_CODE", 0));// 手术种类
        XMLParm.setData("OperationStatus", StateSeq);// 手术状态
        String operationTime = "";
		if ("0,1,2,3".contains(StateSeq)) {
			operationTime = StringTool.getString(opeParm.getTimestamp(
					"STATE_TIME_" + StateSeq, 0), "yyyy-MM-dd HH:mm:ss");
		}
        XMLParm.setData("OperationTime", operationTime);// 状态确认时间
        if (!this.creatOPEStateXML(XMLParm)) {
            result.setErr(-1, "生成XML失败");
            return result;
        }
        return result;
    }

    /**
     * 根据数据生成XML文件
     * 
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean creatOPEStateXML(TParm parm) {// wanglong add 20141010
        StringBuffer stringBuffer = new StringBuffer();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>" + LINE_SEPARATOR);
        stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
        stringBuffer.append("  <OperationStatus>" + LINE_SEPARATOR);
        String[] names = {"OPBookSeq",// 手术单号
                "MrNo",// 病案号
                "PatName",// 病患姓名
                "OperationType", // 手术种类
                "OperationStatus", // 手术状态
                "OperationTime"// 状态确认时间
        };
        for (int i = 0; i < names.length; i++) {
            stringBuffer.append("    <" + names[i] + ">");
            stringBuffer.append(parm.getValue(names[i]));
            stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
        }
        stringBuffer.append("  </OperationStatus>" + LINE_SEPARATOR);
        stringBuffer.append("</CallSysInterface>");
        // System.out.println("stringBuffer:"+stringBuffer);
        Timestamp now = SystemTool.getInstance().getDate();
        String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
        // System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + nowStr + ".xml");
        return TIOM_FileServer.writeFile(socket, screenPath + "OPE_" + parm.getValue("MrNo") + "_"
                + nowStr + ".xml", stringBuffer.toString().getBytes());
    }
    
    /**
     * 向大屏发送医嘱消息
     * 
     * @param parm
     * @return boolean true_发送成功;false_发送失败
     */
	public boolean createOrderInfoXML(TParm parm) {
		StringBuffer stringBuffer = new StringBuffer();
		String LINE_SEPARATOR = System.getProperty("line.separator");
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>"
				+ LINE_SEPARATOR);
		stringBuffer.append("<CallSysInterface>" + LINE_SEPARATOR);
		stringBuffer.append("  <OrderInfo>" + LINE_SEPARATOR);
		String[] names = { "MrNo",// 病案号
				"CaseNo",// 就诊号
				"PatName",// 姓名
				"OrderType",// 医嘱类型
				"OrderNo",// 医嘱单号
				"OrderCode", // 医嘱代码
				"OrderDesc", // 医嘱名称
				"DrNote",// 医嘱备注
				"OrderDate", // 医嘱启用时间
				"CancelFlg" // 取消注记
		};
		for (int i = 0; i < names.length; i++) {
			stringBuffer.append("    <" + names[i] + ">");
			stringBuffer.append(parm.getValue(names[i]));
			stringBuffer.append("</" + names[i] + ">" + LINE_SEPARATOR);
		}
		stringBuffer.append("  </OrderInfo>" + LINE_SEPARATOR);
		stringBuffer.append("</CallSysInterface>");
		Timestamp now = SystemTool.getInstance().getDate();
		String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
		TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
		return TIOM_FileServer.writeFile(socket, screenPath + "ORDER_"
				+ parm.getValue("MrNo") + "_" + nowStr
				+ parm.getValue("OrderNo") + ".xml", stringBuffer.toString()
				.getBytes());
	}
	
	/**
     * 生成电视屏所需的病患信息XML文件
     * @param CASE_NO
     * @return
     * add by huangtt 20170503 手术室-ICU交接单接班人签字保存时，发送病患消息文件，以交接时间作为入ICU时间 
     */
    public TParm creatPatXMLFileTime(String CASE_NO, String TransferToICUTime) {// wanglong add 20150527 增加皮试结果
        TParm result = new TParm();
        TParm XMLParm = creatPatParm(CASE_NO);
        XMLParm.setData("TransferToICUTime", TransferToICUTime);
        if (!this.creatPatXML(XMLParm)) {
            result.setErr(-1, "生成电视屏XML失败");
            return result;
        }
        return result;
    }
    
    /**
     * 生成电视屏所需的手术信息XML文件
     * @param CASE_NO
     * @param OPBOOK_SEQ
     * @return
     * // huangtt add 20170503
     * 手术室-ICU交接单接班人签字保存后，向大屏发送手术信息，此时手术主诊断节点以交接单中的术式为准，如果有值则直接取交接单中的数据，如果没值则依然取OPE_OPBOOK中的主诊断中文
     */
    public TParm creatOPEInfoXMLFile(String CASE_NO, String OPBOOK_SEQ,String opDesc) {
        TParm result = new TParm();
        TParm XMLParm = creatOPEInfoParm(CASE_NO, OPBOOK_SEQ);
        // add by wangb 2017/09/04 手术申请单为空时不发送数据
        if (XMLParm.getErrCode() < 0) {
        	return XMLParm;
        }
        if(opDesc.length() > 0){
        	 XMLParm.setData("OperationCode",opDesc);
        }
       
        if (!this.creatOPEInfoXML(XMLParm)) {
            result.setErr(-1, "生成电视屏XML失败");
            return result;
        }
        return result;
    }
    
    /**
     * 查询手术诊断中文 add by huangtt 20170504
     * @param opeCode
     * @return
     */
    public String getOpeIcdDesc(String opeCode){
    	String sql = "SELECT OPT_CHN_DESC FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+opeCode+"'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	String desc = "";
    	if(parm.getCount() > 0){
    		desc = parm.getValue("OPT_CHN_DESC", 0);
    		
    	}
    	return desc;
    }
	
}
