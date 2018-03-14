package com.javahis.util;

import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;

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
public class EmrUtil {
    private static EmrUtil instanceObject;
    public EmrUtil() {
    }

    public static synchronized EmrUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new EmrUtil();
        }
        return instanceObject;
    }
    /**
     * 拿到模版文件
     * @return String[]
     */
    public String[] getGSTemplet(String deptCode,String admType)
    {
        String sql = "";
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        if(admType.equals("O")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND OPD_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
        }

        if(admType.equals("E")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND EMG_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
        }
//        System.out.println("SQL~~~~~~~~~~~~~:"+sql);

        String s[];
        TParm result = new TParm(this.getDBTool().select(sql));
            if(result.getCount("CLASS_CODE")>0){
                s = new String[] {result.getValue("TEMPLET_PATH",0),result.getValue("SUBCLASS_DESC",0),result.getValue("SUBCLASS_CODE",0)};
            }else{
                s = new String[]{
                    TIOM_FileServer.getPath("ODOEmrTempletZSPath"),
                    TConfig.getSystemValue("ODOEmrTempletZSFileName"),TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE")};
            }

        return s;
    }

    //zhangyong20110426
    /**
     * 拿到模版文件
     * @param deptCode String
     * @param drCode String
     * @param admType String
     * @return String[]
     */
    public String[] getGSTemplet(String deptCode, String drCode, String admType)
    {
        String sql = "";
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        TParm result = new TParm();
        if (admType.equals("O")) {
            // 1.取得医生定制的模板(门诊)
            sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '2' "
                + " AND DEPTORDR_CODE = '" + drCode +
                "' AND B.OPD_FLG = 'Y' "
                +" AND B.CLASS_CODE = '" + classCode
                + "' ORDER BY MAIN_FLG DESC ";
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result == null || result.getCount() <= 0) {
                // 2.取得科室制定的模板(门诊)
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '1' "
                    + " AND DEPTORDR_CODE = '" + deptCode +
                    "' AND B.OPD_FLG = 'Y' "
                    +" AND B.CLASS_CODE = '" + classCode
                    + "' ORDER BY MAIN_FLG DESC ";
                result = new TParm(TJDODBTool.getInstance().select(sql));
            }
        }
        else if (admType.equals("E")) {
            // 1.取得医生定制的模板(急诊)
            sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                + " B.EMT_FILENAME  FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '2' "
                + " AND DEPTORDR_CODE = '" + drCode +
                "' AND B.EMG_FLG = 'Y' "
                +"AND B.CLASS_CODE = '" +classCode
                + "' ORDER BY MAIN_FLG DESC ";
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result == null || result.getCount() <= 0) {
                // 2.取得科室制定的模板(急诊)
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '1' "
                   + " AND DEPTORDR_CODE = '" + deptCode +
                   "' AND B.EMG_FLG = 'Y' "
                   +" AND B.CLASS_CODE = '" + classCode
                   + "' ORDER BY MAIN_FLG DESC ";
                result = new TParm(TJDODBTool.getInstance().select(sql));
            }
        }
        //System.out.println("======未看诊  getGSTempletSql========="+sql);
        String s[];
        if (result.getCount("CLASS_CODE") > 0) {
            s = new String[] {
                result.getValue("TEMPLET_PATH", 0),
                result.getValue("SUBCLASS_DESC", 0),
                result.getValue("SUBCLASS_CODE", 0)};
        }
        // 3.取得默认定制的模板
        else {
            s = new String[] {
                TIOM_FileServer.getPath("ODOEmrTempletZSPath"),
                TConfig.getSystemValue("ODOEmrTempletZSFileName"),
                TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE")};
        }
        return s;
    }


    /**
     * 拿到之前保存的文件
     * @param caseNo String
     * @return String[]
     */
    public String[] getGSFile(String caseNo)
    {
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        String subclassCode = TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");

        TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
        //System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

        String dir="";
        String file="";
        String subClassCode = "";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);
            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
        }

        String s[] = {dir,file,subClassCode};
        return s;
    }
    /**
     * 新建保存
     * @param mrNo String
     * @param caseNo String
     * @return String[]
     */
    public TParm saveGSFile(String mrNo,String caseNo,String subclassCode,String name)
    {
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+mrNo;
        TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"')"));

        if(saveParm.getErrCode()<0)
            return saveParm;
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    /**
     * 拿到样式
     * @param classCode String
     * @return String
     */
    public String getClassStyle(String classCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT CLASS_STYLE FROM EMR_CLASS WHERE CLASS_CODE='"+classCode+"'"));
        if(parm.getCount()<0){
            return "";
        }
        return parm.getValue("CLASS_STYLE",0);
    }
    /**
     * 拿到主诉模版
     * @param parm TParm (ADM_TYPE,SYSTEM_CODE,DEPT_CODE)
     * @return TParm
     */
    public TParm getEmrZSData(TParm parm){
        String admType = parm.getValue("ADM_TYPE");
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        String classStyle = getClassStyle(classCode);
        String deptCode = parm.getValue("DEPT_CODE").length()==0?"":" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
        String sql="";
        if(admType.equals("I")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND IPD_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }

        if(admType.equals("O")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND OPD_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }

        if(admType.equals("E")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND EMG_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }

        if(admType.equals("H")){
                    sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND HRM_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }
        //System.out.println("拿到主诉sql=========="+sql);


        TParm result = new TParm(this.getDBTool().select(sql));
        result.setData("SYSTEM_CODE",parm.getValue("SYSTEM_CODE"));
        result.setData("NODE_NAME",TConfig.getSystemValue("ODOEmrTempletZSPath"));
        result.setData("TYPE",classStyle);
        result.setData("DEPT_CODE",parm.getValue("DEPT_CODE"));
        return result;
    }
    /**
     * 拿到病历或模版
     * @param parm TParm (CASE_NO就诊序号,TYPE类别(ZS主诉现病史),MICROFIELD(TParm)宏参数)
     * @return TWord
     */
    public TParm getTWord(TParm parm){
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String type = parm.getValue("TYPE");
        if(type.equals("ZS")){
            String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
            String subclassCode = TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");
            TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
            if(emrParm.getCount()>0){
                result.setData("PATH",emrParm.getValue("FILE_PATH",0));
                result.setData("FILENAME",emrParm.getValue("FILE_NAME",0));
                result.setData("TYPE",3);
                return result;
            }
            String path = TIOM_FileServer.getPath("ODOEmrTempletZSPath");
            String fileName = TIOM_FileServer.getPath("ODOEmrTempletZSFileName");
            result.setData("PATH",path);
            result.setData("FILENAME",fileName);
            result.setData("TYPE",2);
            return result;
        }
        result.setErrCode(-1);
        result.setErrText("没有找到此类别文件！");
        return result;
    }
    /**
     * 是否属于编辑状态
     * @param word TWord
     * @return TParm
     */
    public TParm isEditFlg(TWord word){
        TParm result = new TParm();
        if(word.isFileEditLock()){
            String userName = this.getOperatorName(word.getFileLockUser());
            String deptName = this.getDeptDesc(word.getFileLockDept());
            result.setErrCode(-1);
            result.setErrText("此病历于"+word.getFileLockDate()+"被IP为"+word.getFileLockIP()+"科室为:"+deptName+"用户为:"+userName+"的用户编辑！");
        }
        result.setErrCode(1);
        return result;
    }
    /**
     * 设置编辑锁
     * @param word TWord
     * @param falg boolean true加锁 false解锁
     */
    public TParm setFileEditLock(TWord word,boolean falg){
        TParm result = new TParm();
        //日期
        String dateStr = StringTool.getString(StringTool.getTimestamp(new Date()),"yyyy年MM月dd日 HH时mm分ss秒");
        if(falg){
            //设置提示不可用
            word.setMessageBoxSwitch(false);
            //文件加锁
            word.setFileEditLock(true);
            //锁定时间
            word.setFileLockDate(dateStr);
            //锁定部门
            word.setFileLockDept(Operator.getDept());
            //锁定人
            word.setFileLockUser(Operator.getID());
            //锁定IP
            word.setFileLockIP(Operator.getIP());
            if(!word.onSave()){
                word.setMessageBoxSwitch(true);
                result.setErrCode(-1);
                result.setErrText("文件服务器异常！");
                return result;
            }
            word.setMessageBoxSwitch(true);
            result.setErrCode(1);
        }else{
            //设置提示不可用
            word.setMessageBoxSwitch(false);
            //文件加锁
            word.setFileEditLock(false);
            //锁定时间
            word.setFileLockDate("");
            //锁定部门
            word.setFileLockDept("");
            //锁定人
            word.setFileLockUser("");
            //锁定IP
            word.setFileLockIP("");
            if(!word.onSave()){
                word.setMessageBoxSwitch(true);
                result.setErrCode(-1);
                result.setErrText("文件服务器异常！");
                return result;
            }
            word.setMessageBoxSwitch(true);
            result.setErrCode(1);
        }
        return result;
    }
    /**
     * 拿到用户名
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
        TParm parm = new TParm(this.getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
        return parm.getValue("USER_NAME",0);
    }
    /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }

    /**
     * 设置宏
     * @param word TWord
     * @param parm TParm 宏对象
     */
    public void setMicroField(TWord word,TParm parm){
        String names[] = parm.getNames();
        for(String temp:names){
            word.setMicroField(temp,parm.getValue(temp));
        }
    }
    /**
     * 设置编辑权限
     * @param word TWord
     * @param falg boolean true 可编辑 false 不可编辑
     */
    public void setCanEidt(TWord word,boolean falg){
        word.setCanEdit(falg);
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * 得到EMR路径
     * @param parm TParm
     * @return String
     */
    public TParm getEmrFilePath(TParm parm){
    	String caseNo=parm.getValue("CASE_NO");
    	String mrCode=parm.getValue("MR_CODE");
        String admType=parm.getValue("ADM_TYPE");
    	TParm result = new TParm();
    	if(caseNo==null||mrCode==null){
    		return result;
    	}
    	if(caseNo.trim().length()<1||mrCode.trim().length()<1){
    		return result;
    	}
    	String sql="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
        " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
        " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE"+
        " AND A.SUBCLASS_CODE='"+mrCode+"' AND B.CASE_NO='"+caseNo+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";
//    	System.out.println("emr.sql1="+sql);
        result = new TParm(this.getDBTool().select(sql));
        if(result.getInt("ACTION","COUNT")>0){
        	result.setData("FLG",true);
        	TParm action = result.getRow(0);
        	action.setData("FLG",result.getData("FLG"));
        	//System.out.println(action);
            return action;
        }else{
            if("H".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND HRM_FLG='Y'";
            }
            if("O".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND OPD_FLG='Y'";
            }
            if("E".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND EMG_FLG='Y'";
            }
            if("I".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND IPD_FLG='Y'";
            }
            if(admType==null||admType.length()==0){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"'";
            }
//            System.out.println("emr.sql2="+sql);
            result = new TParm(this.getDBTool().select(sql));
            result.setData("FLG",false);
        }
        TParm action = result.getRow(0);
        action.setData("FLG",result.getData("FLG"));
        return action;
    }
    /**
     * 拿到报告时间
     * @param caseNo String
     * @return TParm
     */
    public TParm getReportDate(String caseNo){
        String sql = "SELECT REPORT_DATE FROM HRM_PATADM WHERE CASE_NO='"+caseNo+"'";
        TParm emrParm = new TParm(this.getDBTool().select(sql));
        if(emrParm.getErrCode()!=0){
            return null;
        }
        return emrParm.getRow(0);
    }
    /**
     * 根据给定MR_NO得到家族史病历地址，如没有此MR_NO，则返回模板地址
     * @param mrNo String
     * @return file String[]
     */
    public String[] getFamilyHistoryPath(String mrNo,String deptCode,String admType){
    	String[] s=new String[]{};
    	if(StringUtil.isNullString(mrNo)){
    		return null;
    	}
        String classCode = TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_CLASSCODE");
        String sql="SELECT MAX(CASE_NO||FILE_SEQ) SEQ, FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE MR_NO='"+mrNo+"' AND CLASS_CODE='"+classCode+"' GROUP BY FILE_PATH,FILE_NAME, SUBCLASS_CODE,FILE_SEQ ORDER BY FILE_SEQ DESC";
//        System.out.println("familyPath.sql="+sql);
        TParm emrParm = new TParm(this.getDBTool().select(sql));
        String dir="";
        String file="";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);

            s = new String[]{dir,file,"3",emrParm.getValue("SUBCLASS_CODE",0)};
        }else{
            if(admType.equals("O")){
                sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND OPD_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
            }
            if(admType.equals("E")){
                sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND EMG_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
            }
            TParm result = new TParm(this.getDBTool().select(sql));
            if(result.getCount("CLASS_CODE")>0){
                s = new String[] {result.getValue("TEMPLET_PATH",0),result.getValue("SUBCLASS_DESC",0),"2",result.getValue("SUBCLASS_CODE",0)};
            }else{
                s = new String[] {
                    TIOM_FileServer.getPath("ODOEmrTempletFAMILYHISTORY_Path"),
                    TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_FileName"), "2",TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE")};
            }
        }


    	return s;
    }
    /**
     * 给据给入的CASE_NO，MR_NO，保存到EMR_FILE_INDEX中，如果已有该条记录则更新他。
     * @param caseNo
     * @param mrNo
     * @return
     */
    public TParm getFamilyHistorySavePath(String caseNo,String mrNo,String subclassCode,String name){
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        String classCode = TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_CLASSCODE");
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+mrNo;
        TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"')"));

        if(saveParm.getErrCode()<0)
            return saveParm;
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    /**
     * 拿到文件服务器路径
     * @param rootPath String
     * @param fileServerPath String
     * @return String
     */
    public TParm getFileServerEmrName(TParm inParm){
        TParm emrParm =new TParm();
        String emrName = "";
        String indexStr = "01";
        String templetName = inParm.getValue("EMT_FILENAME");
        String caseNo=inParm.getValue("CASE_NO");
        String yy=caseNo.substring(0,2);
        String mm=caseNo.substring(2,4);
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+inParm.getValue("CASE_NO")+"'"));
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        emrName = inParm.getValue("CASE_NO")+"_"+templetName+"_"+indexStr;
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss");
        emrParm.setData("FILE_SEQ",indexStr);
        emrParm.setData("FILE_NAME",emrName);
        emrParm.setData("CLASS_CODE",inParm.getValue("CLASS_CODE"));
        emrParm.setData("SUBCLASS_CODE",inParm.getValue("SUBCLASS_CODE"));
        emrParm.setData("CASE_NO",inParm.getValue("CASE_NO"));
        emrParm.setData("MR_NO",inParm.getValue("MR_NO"));
        emrParm.setData("IPD_NO","");

        emrParm.setData("FILE_PATH",yy+"\\"+mm+"\\"+inParm.getValue("MR_NO"));
        emrParm.setData("DESIGN_NAME",templetName+"("+dateStr+")");
        emrParm.setData("DISPOSAC_FLG","N");
        return emrParm;
    }


}
