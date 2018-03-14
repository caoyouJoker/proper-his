package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;

import javax.swing.SwingUtilities;

import jdo.device.CallNo;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

//import jdo.device.ECCCall;
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
public class EMRSingleControl extends TControl {
    /**
     * 定义TABLE
     */
    private final String TABLE="TABLEPAS";
    /**
     * 系统别
     */
    private String systemCode;
    /**
     * 门急住别
     */
    private String admType;
    /**
     * 病案号
     */
    private String mrNo;
    /**
     * 病患姓名
     */
    private String patName;
    /**
     * 病患英文名称
     */
    private String patEnName;
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 住院号
     */
    private String ipdNo;
    /**
     * 数据
     */
    private TParm parmData;
    /**
     * 入院日期
     */
    private Timestamp admDate;
    /**
     * 部门
     */
    private String deptCode;
    /**
     * 病区
     */
    private String stationCode;
    /**
     * 打开样式
     */
    private String styleType;
    /**
     * 权限
     */
    private String rultType;
    /**
     * 类型：抗菌药品申请单使用
     * =======pangben 2013-7-30
     */
    private String phaType;
    
    
    /**
	 *  条码控制码
	 */
	private StringBuffer printText = new StringBuffer();//wanglong add 20140610
    private int offset_x = 0;//wanglong add 20150410
    private int offset_y = 0;//wanglong add 20150410
    
    public String getPhaType() {
		return phaType;
	}
	public void setPhaType(String phaType) {
		this.phaType = phaType;
	}
	/**
     * 初始化
     */
    public void onInit(){
        super.onInit();
        //初始化界面
        this.initPage();
        //监听TABLE
        callFunction("UI|" + "TABLEPAS" + "|addEventListener",
                    "TABLEPAS" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableClicked");
    }
    /**
     * 初始化页面
     */
    public void initPage(){
  
        TParm parm  = new TParm();
        Object obj = this.getParameter();
//        this.messageBox(obj+"");
        if(obj!=null){
            parm = (TParm)obj;
            this.setSystemCode(parm.getValue("SYSTEM_CODE"));
            this.setCaseNo(parm.getValue("CASE_NO"));
            this.setMrNo(parm.getValue("MR_NO"));
            this.patEnName = getPatEnName(this.getMrNo());
            this.setIpdNo(parm.getValue("IPD_NO"));
            this.setPatName(parm.getValue("PAT_NAME"));
            this.setAdmDate(parm.getTimestamp("ADM_DATE"));
            this.setAdmType(parm.getValue("ADM_TYPE"));
            this.setDeptCode(parm.getValue("DEPT_CODE"));
            this.setStationCode(parm.getValue("STATION_CODE"));
            this.setStyleType(parm.getValue("STYLETYPE"));
            this.setRultType(parm.getValue("RULETYPE"));
            this.setParmData((TParm)parm.getData("EMR_DATA_LIST"));
            this.setPhaType(parm.getValue("PHATYPE"));//======pangben 2013-7-30 抗菌药申请单
        }
        if("ODI".equals(this.getSystemCode())){
            this.setValue("MR_NO",this.getMrNo());
            this.patEnName = getPatEnName(this.getMrNo());
            this.setValue("IPD_NO",this.getIpdNo());
            if("en".equals(this.getLanguage())&& patEnName!=null&&patEnName.length()>0){
                 this.setValue("PAT_NAME", this.patEnName);
            }else{
                this.setValue("PAT_NAME",this.getPatName());
            }
        }
        if("ODO".equals(this.getSystemCode())){
            this.setValue("MR_NO",this.getMrNo());
            this.patEnName = getPatEnName(this.getMrNo());
            if("en".equals(this.getLanguage())&& patEnName!=null&&patEnName.length()>0){
                 this.setValue("PAT_NAME", this.patEnName);
            }else{
                this.setValue("PAT_NAME",this.getPatName());
            }
            ((TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            ((TTextField)this.getComponent("IPD_NO")).setVisible(false);
        }
        if("EMG".equals(this.getSystemCode())){
            this.setValue("MR_NO",this.getMrNo());
            this.patEnName = getPatEnName(this.getMrNo());
            if("en".equals(this.getLanguage())&& patEnName!=null&&patEnName.length()>0){
                 this.setValue("PAT_NAME", this.patEnName);
            }else{
                this.setValue("PAT_NAME",this.getPatName());
            }
            ((TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            ((TTextField)this.getComponent("IPD_NO")).setVisible(false);
        }
        //初始化TABLE
//        this.messageBox_(this.getParmData());
        this.getTTable(TABLE).setParmValue(this.getParmData());
    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 拿到病患英文名
     * @param mrNo String
     * @return String
     */
    public String getPatEnName(String mrNo){
        String patEnName = "";
        TParm parm = new TParm(this.getDBTool().select("SELECT PAT_NAME1 FROM SYS_PATINFO WHERE MR_NO='"+this.getMrNo()+"'"));
        if(parm.getCount()>0){
            patEnName = parm.getValue("PAT_NAME1",0);
        }
        return patEnName;
    }

    /**
     * 双击
     * @param row int
     */
    public void onTableClicked(int row){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                onOpen();
            }
        });
    }
    /**
     * 打开检查检验单
     */
    public void onOpen(){
        int selectRow = (Integer) callFunction("UI|" + TABLE +"|getSelectedRow");
        if(selectRow<0){
            this.messageBox("E0111");
            return;
        }
        TParm pa = (TParm)this.getParameter();
        TParm parm  = this.getSelectRowData(TABLE);
        TParm actionParm = this.getEmrFilePath(parm);
        TParm action = new TParm();
        action.setData("SYSTEM_TYPE", this.getSystemCode());
        action.setData("CASE_NO",this.getCaseNo());
        action.setData("PAT_NAME",this.getPatName());
        action.setData("MR_NO",this.getMrNo());
        action.setData("IPD_NO",this.getIpdNo());
        action.setData("ADM_DATE",this.getAdmDate());
        action.setData("ADM_TYPE",this.getAdmType());
        action.setData("DEPT_CODE",this.getDeptCode());
        action.setData("STATION_CODE",this.getStationCode());
        action.setData("STYLETYPE",this.getStyleType());
        action.setData("RULETYPE",this.getRultType());
        action.setData("EMR_FILE_DATA",actionParm);
        actionParm.setData("URGENT_FLG",parm.getValue("URGENT_FLG"));//========pangben modify 20110706 添加急作注记
        actionParm.setData("MED_APPLY_NO",parm.getValue("MED_APPLY_NO"));
        actionParm.setData("BILL_TYPE", OpdRxSheetTool.getInstance().getBillType(this.getCaseNo(), "", parm.getValue("MED_APPLY_NO")) ); 
        action.setData("ERD_LEVEL",pa.getValue("ERD_LEVEL"));//wanglong ad 20150407
        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "日间手术":"");//日间手术标记
        action.setListenerData(pa.getListenerData());
        action.addListener("EMR_LISTENER",this,"emrListener");
        action.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
//        System.out.println("---action----------------"+action);
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", action);
    }
    
	/**
	 * 条码打印
	 */
	public void onPrint() {
		int selectRow = (Integer) callFunction("UI|" + TABLE +"|getSelectedRow");
        if(selectRow<0){
            this.messageBox("请选择需要打印的数据！");
            return;
        }
        TParm tableParm  = this.getSelectRowData(TABLE);
        String sql  = " SELECT PRINT_FLG,DEPT_CODE,STATION_CODE,CLINICAREA_CODE,CLINICROOM_NO,BED_NO,PAT_NAME,APPLICATION_NO,RPTTYPE_CODE,OPTITEM_CODE,DEV_CODE,MR_NO,IPD_NO,ORDER_DESC,CAT1_TYPE,OPTITEM_CHN_DESC,TO_CHAR(ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,DR_NOTE,EXEC_DEPT_CODE,URGENT_FLG,SEX_CODE,BIRTH_DATE,ORDER_NO,SEQ_NO,TEL,ADDRESS,CASE_NO,ORDER_CODE,ADM_TYPE,PRINT_DATE " +
        		" FROM MED_APPLY " +
        		" WHERE APPLICATION_NO = '"+ tableParm.getValue("MED_APPLY_NO") +"' ";
        TParm parm = new TParm(this.getDBTool().select(sql));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		List printSize = new ArrayList();
			String appNoStr = "" ;
			StringBuffer orderDesc = new StringBuffer();
			String patName = "";
			String deptExCode = "";
			String orderDate = "";
			String stationCode = "";
			String optItemDesc = "";
			String deptCode = "";
			String urgentFlg = "";
			String mrNo = "";
			String sexDesc = "";
			String age = "";
			String devdesc = "";
			String bedNo = "";
			String transHosp;
			String applyNo = ""; // chenxi 条码号
			String drNote = ""; // chenxi 医师备注
			patName = parm.getValue("PAT_NAME",0);
			deptExCode = parm.getValue("EXEC_DEPT_CODE",0);
			deptCode = parm.getValue("DEPT_CODE",0);
			stationCode = parm.getValue("STATION_CODE",0);
			applyNo = parm.getValue("APPLICATION_NO",0);
			drNote = parm.getValue("DR_NOTE",0);
			String bedSql = "SELECT B.BED_NO_DESC FROM MED_APPLY A,SYS_BED B WHERE A.APPLICATION_NO ='"
					+ applyNo + "'" + "AND A.BED_NO=B.BED_NO ";
			TParm selParm = new TParm(TJDODBTool.getInstance().select(
					bedSql));
			bedNo = selParm.getValue("BED_NO_DESC", 0);
			orderDate = String.valueOf(sysDate).substring(0, 19)
					.replaceAll("-", "/");
			optItemDesc = parm.getValue("OPTITEM_CHN_DESC",0);
			mrNo = parm.getValue("MR_NO",0);
			sexDesc = this.getDictionary("SYS_SEX", parm
					.getValue("SEX_CODE",0));
			age = StringTool.CountAgeByTimestamp(parm
					.getTimestamp("BIRTH_DATE",0), sysDate)[0];
				orderDesc.append(parm.getValue("ORDER_DESC",0));
		   String transHospSql = " SELECT A.CHN_DESC FROM SYS_DICTIONARY A, SYS_FEE B WHERE A.GROUP_ID = 'SYS_TRN_HOSP' AND A.ID = B.TRANS_HOSP_CODE AND B.ORDER_CODE = '"+tableParm.getValue("ORDER_CODE")+"' ";
		   TParm transHospParm = new TParm(this.getDBTool().select(transHospSql));
		   transHosp = transHospParm.getValue("CHN_DESC",0);
		TParm printParm = new TParm();
		printParm.setData("APPLICATION_NO", "TEXT", applyNo);
		printParm.setData("PAT_NAME", "TEXT", patName);
		printParm.setData("DEPT_CODE", "TEXT", deptCode);
		printParm.setData("STATION_CODE", "TEXT", stationCode);
		printParm.setData("URGENT_FLG", "TEXT", urgentFlg);
		printParm.setData("EXEC_DEPT_CODE", "TEXT", deptExCode);
		printParm.setData("ORDER_DATE", "TEXT", orderDate);
		printParm.setData("OPTITEM_CHN_DESC", "TEXT", optItemDesc);
		printParm.setData("ORDER_DESC", "TEXT", orderDesc.toString());
		printParm.setData("MR_NO", "TEXT", mrNo);
		printParm.setData("SEX_DESC", "TEXT", sexDesc);
		printParm.setData("AGE", "TEXT", age);
		printParm.setData("BED_NO", "TEXT", bedNo);
		printParm.setData("DR_NOTE", "TEXT", drNote);
		printParm.setData("TRANS_HOSP","TEXT",transHosp);
		printSize.add(printParm);
   //    int listRowCount = printSize.size();
   // for (int i = 0; i < listRowCount; i++) {
        TParm pR = (TParm) printSize.get(0);
        pR.setData("EXEC_DEPT_CODE", "TEXT",
                   getDeptDesc(pR.getValue("EXEC_DEPT_CODE", "TEXT")));
        pR.setData("DEPT_CODE", "TEXT", getDeptDesc(pR.getValue("DEPT_CODE", "TEXT")) + "("
                + getStationDesc(pR.getValue("STATION_CODE", "TEXT")) + ")");
            this.openPrintDialog(IReportTool.getInstance()
                                         .getReportPath("Med_ApplyPrint.jhw"), pR, true);
   //    }
   }

    /**
	 * 拿到科室
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}
    /**
	 * 拿到科室
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}
    
    /**
     * 事件
     * @param parm TParm
     */
    public void emrListener(TParm parm)
    {
        if(parm.getBoolean("FLG"))
            return;
        TParm tableParm  = this.getSelectRowData(TABLE);
        String iptItemCode = getDictionary("SYS_OPTITEM",tableParm.getValue("OPTITEM_CODE"));
//        System.out.println("--------emrListener----------------------"+tableParm);
        //"ORDER_CODE",tableParm.getValue("ORDER_DESC")
        String sql = " SELECT A.CHN_DESC FROM SYS_DICTIONARY A, SYS_FEE B WHERE A.GROUP_ID = 'SYS_TRN_HOSP' AND A.ID = B.TRANS_HOSP_CODE AND B.ORDER_CODE = '"+tableParm.getValue("ORDER_CODE")+"' ";
        TParm result = new TParm(this.getDBTool().select(sql));
        parm.runListener("setCaptureValue","TRANS_HOSP",result.getValue("CHN_DESC",0));
        parm.runListener("setCaptureValue","ORDER_CODE",tableParm.getValue("ORDER_DESC"));
        parm.runListener("setCaptureValue","ITEM_CODE",iptItemCode);
        parm.runListener("setCaptureValue","DAY_OPE_FLG","Y".equals(tableParm.getValue("DAY_OPE_FLG")) ? "日间手术":"");
        
    }
    /**
     * 执行保存
     * @param pamr TParm
     * @return boolean
     */
    public boolean emrSaveListener(TParm parm){
        if(parm.getValue("CASE_NO").length()==0)
            return true;
//        System.out.println("parm===="+parm);
//        this.messageBox_("保存==="+parm);
        TParm emrParm = new TParm();
        if("I".equals(parm.getValue("ADM_TYPE"))){
            //System.out.println("UPDATE ODI_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ")+"'");
            emrParm = new TParm(this.getDBTool().update("UPDATE ODI_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ")+"'"));
//            ECCCall ecall = new ECCCall();
//            if(!ecall.init()){
//                return true;
//            }
//            TParm eccParm = new TParm(this.getDBTool().select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ")+"'"));
//            if(eccParm.getCount()>0){eccParm.getTimestamp("ORDER_DATE",0);
//                ecall.callEccApp(eccParm.getValue("MED_APPLY_NO",0),eccParm.getValue("CASE_NO",0),eccParm.getValue("CASE_NO",0),eccParm.getValue("DR_NOTE",0),eccParm.getValue("DR_NOTE",0),"2","3",eccParm.getValue("EXEC_DEPT_CODE",0),"电生理",StringTool.getString(eccParm.getTimestamp("ORDER_DATE",0),"yyyyMMddHHmmss"),eccParm.getValue("ORDER_DEPT_CODE",0),eccParm.getValue("ORDER_DR_CODE",0),"XX","",eccParm.getValue("ORDER_NO",0),eccParm.getValue("ORDER_SEQ",0),eccParm.getValue("ORDER_DESC",0),eccParm.getValue("ORDER_CODE",0),"1","20");
//            }
        }
        if("O".equals(parm.getValue("ADM_TYPE"))){
           emrParm = new TParm(this.getDBTool().update("UPDATE OPD_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'"));
        }
        if("E".equals(parm.getValue("ADM_TYPE"))){
           emrParm = new TParm(this.getDBTool().update("UPDATE OPD_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'"));
        }
        if("H".equals(parm.getValue("ADM_TYPE"))){
           emrParm = new TParm(this.getDBTool().update("UPDATE HRM_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'"));
        }
        if(emrParm.getErrCode()<0){
            return false;
        }
        TParm parmTable  = this.getTTable(TABLE).getParmValue();
        int rowCount = parmTable.getCount("FILE_NO");
        for(int i=0;i<rowCount;i++){
            TParm temp = parmTable.getRow(i);
            if(temp.getValue("ORDER_NO").equals(parm.getValue("ORDER_NO"))&&temp.getValue("ORDER_SEQ").equals(parm.getValue("ORDER_SEQ"))){
                parmTable.setData("FILE_NO",i,parm.getValue("FILE_SEQ"));
            }
        }
//        this.messageBox_(parmTable);
        this.getTTable(TABLE).setParmValue(parmTable);
        return true;
    }
    /**
     * 得到EMR路径
     * @param parm TParm
     * @return String
     */
    public TParm getEmrFilePath(TParm parm){
        String sqlO="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
            " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
            " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.OPD_FLG='Y'"+
            " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";

        String sqlE="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
            " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
            " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.EMG_FLG='Y'"+
            " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";

        String sqlH="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
           " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
           " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.HRM_FLG='Y'"+
           " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";

        String sqlI="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
            " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
            " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.IPD_FLG='Y'"+
            " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";
        TParm result = new TParm();
        if("O".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlO));
            //System.out.println("=======EMRO路径:"+sqlO);
        }
        if("E".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlE));
            //System.out.println("=======EMRE路径:"+sqlE);
        }
        if("H".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlH));
            //System.out.println("=======EMRH路径:"+sqlH);
        }
        if("I".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlI));
            //System.out.println("=======EMRI路径:"+sqlI);
        }
//        this.messageBox_("查询历史:"+result);
        if(result.getInt("ACTION","COUNT")>0){
            result.setData("FLG",true);
            TParm action = result.getRow(0);
            action.setData("FLG",result.getData("FLG"));
            return action;
        }else{
            //System.out.println("MR_CODE"+parm.getValue("MR_CODE"));
            String sqlODO = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND OPD_FLG='Y'";
            String sqlEMG = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND EMG_FLG='Y'";
            String sqlHRM = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND HRM_FLG='Y'";
            String sqlODI = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND IPD_FLG='Y'";
            if("O".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlODO));
                //System.out.println("=======EMROSQL:"+sqlODO);
            }
            if("E".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlEMG));
                //System.out.println("=======EMRESQL:"+sqlEMG);
            }
            if("H".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlHRM));
            }
            if("I".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlODI));
            }
            result.setData("FLG",false);
        }
        //System.out.println("result"+result);
        TParm action = result.getRow(0);
        action.setData("FLG",result.getData("FLG"));
        action.setData("ORDER_NO",parm.getValue("ORDER_NO"));
        action.setData("ORDER_SEQ",parm.getValue("ORDER_SEQ"));
        action.setData("TYPEEMR","SQD");
//        System.out.println("action================"+action);

        return action;
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }

    /**
     * 得到选中行数据
     * @param tableTag String
     * @return TParm
     */
    public TParm getSelectRowData(String tableTag){
        int selectRow = (Integer) callFunction("UI|" + tableTag +"|getSelectedRow");
        if(selectRow<0)
            return new TParm();
        out("行号" + selectRow);
        TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
        out("GRID数据" + parm);
        TParm parmRow = parm.getRow(selectRow);
        //System.out.println("选中行:"+parmRow);
        return parmRow;
    }
    /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public TParm getParmData() {
        return parmData;
    }

    public Timestamp getAdmDate() {
        return admDate;
    }

    public String getAdmType() {
        return admType;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public String getRultType() {
        return rultType;
    }

    public String getStyleType() {
        return styleType;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public void setParmData(TParm parmData) {
        this.parmData = parmData;
    }

    public void setAdmDate(Timestamp admDate) {
        this.admDate = admDate;
    }

    public void setAdmType(String admType) {
        this.admType = admType;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setRultType(String rultType) {
        this.rultType = rultType;
    }

    public void setStyleType(String styleType) {
        this.styleType = styleType;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
}
