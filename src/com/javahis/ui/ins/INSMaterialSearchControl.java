package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title: 医保资料查询</p>
 *
 * <p>Description:医保资料查询</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim
 * @version 1.0
 */
public class INSMaterialSearchControl extends TControl {

    private int currentTabPage = 0;

    private TTabbedPane tablePanel;

    //第一页签中被选中的行号.
    private int brlbTableSelectedRow = -1;

    //就诊序号
    private String admSeq;

    private String ctzDesc;

    private String nhi_no;
    private TTable brlbTable;
    private TTable fymxxxTable;
 // 排序
	private Compare compare = new Compare();
	// 排序
	private boolean ascending = false;
	// 排序
	private int sortColumn = -1;
	
    /**
     * 初始化
     */
    public void onInit() {
    	
        this.tablePanel = ((TTabbedPane) getComponent("TablePanel"));
        this.brlbTable = ((TTable) getComponent("BRLBTABLE"));
        this.fymxxxTable = ((TTable) getComponent("FYMXXXTABLE"));
        tablePanel.setSelectedIndex(0);
        this.tablePanel.getComponentAt(1).setEnabled(false);
        this.tablePanel.getComponentAt(2).setEnabled(false);
        this.tablePanel.getComponentAt(3).setEnabled(false);

        Timestamp date = SystemTool.getInstance().getDate() ;
//        String[] dateStrArray = date.toString().split(" ") ;
//        this.setValue("START_DATE", dateStrArray[0].replace("-", "/")+" 00:00:00") ;
        //this.setValue("START_DATE",date) ;//delete by wanglong 20121011
        this.setValue("END_DATE", date) ;
//        date.setMonth(date.getMonth()-3);//add by wanglong 20121011
//        this.setValue("START_DATE",date) ;//add by wanglong 20121011
		 this.setValue("START_DATE",
		 StringTool.rollDate(date, -90).toString().substring(0, 10).replace('-', '/')); 

        this.setValue("DBZ", "PTBR") ;
        this.setValue("NYBLB", "1") ;
        
        TParm result2 = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
        this.nhi_no = result2.getValue("NHI_NO", 0);

        if (this.getPopedem("LeaderEnabled")) {
        	((TButton)this.getComponent("Button4")).setEnabled(true) ;
        }
        //初始化数据
        onExeEnable(true);
        this.setValue("UPLOAD_START_DATE", date);
        this.setValue("UPLOAD_END_DATE", date);
        this.setValue("LOCAL_FLG", "Y");//区域
        addListener(brlbTable);
        addListener1(fymxxxTable);
    }

    /**
     * 查询
     */
    public void onQuery() {
    	String startDate = "";
    	String endDate = "";
    	String sql1 = "";
    	if (this.getRadioButton("IN_TYPE").isSelected()) {// 入院时间查询
    		startDate = this.getValueString("START_DATE"); //起始时间
    		endDate = this.getValueString("END_DATE"); //结束时间
    		 sql1 = " AND TO_CHAR(A.IN_DATE,'yyyymmdd') <= '" +
                     transferDate(endDate) + "'" +
                    " AND TO_CHAR(A.IN_DATE,'yyyymmdd') >= '" +
                            transferDate(startDate) + "'";
		}else if (this.getRadioButton("UPLOAD_TYPE").isSelected()) {//上传时间查询
			startDate = this.getValueString("UPLOAD_START_DATE"); //上传开始时间
    		endDate = this.getValueString("UPLOAD_END_DATE"); //上传结束时间  
    		sql1 = " AND TO_CHAR(E.UPLOAD_DATE,'yyyymmdd') <= '" +
                   transferDate(endDate) + "'" +
                   " AND TO_CHAR(E.UPLOAD_DATE,'yyyymmdd') >= '" +
                   transferDate(startDate) + "'";
		}
        String dBZ = this.getValueString("DBZ"); //单病种.
        String yblb = this.getValueString("NYBLB"); //医保类别.
        String  in_status = this.getValueString("IN_STATUS");
        String mrno = getValueString("MR_NO");
        String localflg = getValueString("LOCAL_FLG");
        if(in_status.equals("5"))
        	callFunction("UI|Button4|setEnabled", false);
        else   
        	callFunction("UI|Button4|setEnabled", true);
        if (this.currentTabPage == 0) { //病人列表
            if (checkInput(this.currentTabPage)) {
                String sql = "";
                if ("PTBR".equals(dBZ)) { //普通病人.
                    sql =
                            " SELECT DISTINCT A.ADM_SEQ ,TO_CHAR(A.UP_DATE,'yyyy/mm/dd') AS TOCHAR_UP_DATE,A.CONFIRM_NO,A.PAT_NAME, CASE A.SEX_CODE WHEN '1' THEN '男' WHEN '2' THEN '女' ELSE '' END  AS CHN_DESC,A.IDNO,C.CTZ_DESC,A.IN_STATUS ," +
                            " TO_CHAR(A.DOWN_DATE,'yyyy/mm/dd') AS TOCHAR_DOWN_DATE ,D.CASE_NO,D.MR_NO,TO_CHAR(A.IN_DATE,'yyyy/mm/dd') AS TOCHAR_INDATE,' ' AS SDISEASE_CODE,' ' AS DISEASE_DESC," +
                            " TO_CHAR(D.DS_DATE,'yyyy/mm/dd') AS TOCHAR_DSDATE,A.HIS_CTZ_CODE,C.INS_CROWD_TYPE,A.LOCAL_FLG," +
                            " TO_CHAR(E.UPLOAD_DATE,'yyyy/mm/dd') AS UPLOAD_DATE,D.DEPT_CODE " +
                            " FROM INS_ADM_CONFIRM A, SYS_CTZ C ,ADM_INP D,INS_IBS E" +
                            " WHERE A.CASE_NO = D.CASE_NO " +
                            " AND A.CASE_NO = E.CASE_NO(+) " +
//                            " AND S.GROUP_ID = 'SYS_SEX'" +
//                            " AND A.SEX_CODE=S.ID(+)" +
//                            " AND A.CTZ1_CODE=C.NHI_NO" +
                            " AND A.HIS_CTZ_CODE = C.CTZ_CODE"+
                            " AND C.NHI_CTZ_FLG = 'Y'" +
                            sql1 +
                            " AND A.SDISEASE_CODE IS NULL" ;
//                            " AND A.IN_STATUS<>'5'";
                    if(!mrno.equals("")){
                    	sql += " AND D.MR_NO = '" + mrno + "'";
                    }
                    //==============chenxi  modify  2012.05.28
                    if(!in_status.equals("")){
                    	sql += " AND A.IN_STATUS = '" + in_status + "'" ;
                    }
                    //==================chenxi   modify  2012.05.28
                    if(!localflg.equals("")){
                    	sql += " AND A.LOCAL_FLG = '" + localflg + "'" ;
                    } 

                } else if ("DBZBR".equals(dBZ)) { //单病种病人
                    sql =
                            " SELECT DISTINCT A.ADM_SEQ ,TO_CHAR(a.UP_DATE,'yyyy/mm/dd') as TOCHAR_UP_DATE,a.CONFIRM_NO," +
                            " a.PAT_NAME,s.CHN_DESC,a.IDNO,c.CTZ_DESC,a.IN_STATUS ,TO_CHAR(a.DOWN_DATE,'yyyy/mm/dd') " +
                            " as TOCHAR_DOWN_DATE ,d.CASE_NO,d.MR_NO,TO_CHAR(a.in_date,'yyyy/mm/dd') as TOCHAR_INDATE,A.SDISEASE_CODE," +
                            " F.CHN_DESC AS DISEASE_DESC,TO_CHAR(d.ds_date,'yyyy/mm/dd') as TOCHAR_DSDATE," +
                            " A.HIS_CTZ_CODE,C.INS_CROWD_TYPE,A.LOCAL_FLG, " +
                            " TO_CHAR(E.UPLOAD_DATE,'yyyy/mm/dd') AS UPLOAD_DATE,D.DEPT_CODE "+
                            " FROM INS_ADM_CONFIRM A, SYS_DICTIONARY S, SYS_CTZ C ,ADM_INP D,SYS_DICTIONARY F,INS_IBS E " +
                            " WHERE A.CASE_NO = D.CASE_NO " +
                            " AND A.CASE_NO = E.CASE_NO(+) " +
                            " AND S.GROUP_ID = 'SYS_SEX'" +
                            " AND A.SEX_CODE=S.ID" +
//                            " AND A.CTZ1_CODE=C.NHI_NO" +
                            " AND A.HIS_CTZ_CODE = C.CTZ_CODE"+
                            " AND C.NHI_CTZ_FLG = 'Y'" +
                            sql1 +
                            " AND F.GROUP_ID='SIN_DISEASE' "+
                            " AND F.ID = A.SDISEASE_CODE "+
//                            " AND A.sdisease_code=E.disease_code" +
                            " AND A.SDISEASE_CODE IS NOT NULL";
//                            " AND A.IN_STATUS<>'5'";
                    if(!mrno.equals("")){
                    	sql += " AND D.MR_NO = '" + mrno + "'";
                    }
                    //==============chenxi  modify  2012.05.28
                    if(!in_status.equals("")){
                    	sql += " AND A.IN_STATUS = '" + in_status + "'" ;
                    }
                    //==================chenxi   modify  2012.05.28
                    if(!localflg.equals("")){
                    	sql += " AND A.LOCAL_FLG = '" + localflg + "'" ;
                    }   
                }
                StringBuilder sbuilder = new StringBuilder(sql);
//                if (!"".equals(yblb)) {
//                    sbuilder.append(" AND substr(C.CTZ_CODE,0,1)='" + yblb +"'");
//                }
                if("1".equals(yblb)){
//                	sbuilder.append(" AND C.CTZ_CODE IN ('11','12','13')");
                	sbuilder.append(" AND C.INS_CROWD_TYPE='1' ");
                }else if("2".equals(yblb)){
//                	sbuilder.append(" AND C.CTZ_CODE IN ('21','22','23')");
                	sbuilder.append(" AND C.INS_CROWD_TYPE='2' ");
                }else{
//                	sbuilder.append(" AND C.CTZ_CODE IN ('11','12','13','21','22','23')");
                	sbuilder.append(" AND C.INS_CROWD_TYPE IN ('1','2') ");
                }
//                System.out.println("sql===============:"+sql);
                TParm result = new TParm(TJDODBTool.getInstance().select(
                        sbuilder.toString()));
                if (result.getErrCode() < 0) {
                    messageBox(result.getErrText());
                    return;
                }
                if (result.getCount() <= 0) {
                    messageBox("查无数据");
                    this.callFunction("UI|BRLBTABLE|setParmValue", new TParm());
                    return;
                }
                this.callFunction("UI|BRLBTABLE|setParmValue", result);

                this.clearAttr();
            }
        }
    }
    /**
     * 核查全部资格确认书
     */
    public void checkAll() {
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        TParm tableData = brlb.getParmValue();

        if (tableData.getCount() <= 0) {
            messageBox("没有要核查的数据.");
            return;
        }
        tableData.addData("HOSP_NHI_NO", this.nhi_no);

        TParm result = TIOM_AppServer.executeAction(
                "action.ins.INSMaterialSearchAction", "checkAllAction",
                tableData);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        } else {
            messageBox("核查通过.");
            int tempRow = this.brlbTableSelectedRow;
            this.onQuery();
            brlb.setSelectedRow(tempRow);
        }
    }

    /**
     * 核查选中资格确认书
     */
    public void checkSelected() {

        if (this.brlbTableSelectedRow != -1) {
            TParm inparm = new TParm();
            TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
            TParm tableData = brlb.getParmValue();
            if(tableData.getValue("LOCAL_FLG",this.brlbTableSelectedRow).equals("N")){
            	 messageBox("异地医保资格确认书开具成功即代表审核通过,无需再检查");
            	 return;
            }            	
            inparm.addData("ADM_SEQ", this.getAdmSeq());
            inparm.addData("HOSP_NHI_NO", this.getNhi_no());
            inparm.setData("INS_TYPE",tableData.getValue("INS_CROWD_TYPE",this.brlbTableSelectedRow));
            inparm.addData("CONFIRM_NO",
                           tableData.getValue("CONFIRM_NO",
                                              this.brlbTableSelectedRow));
            inparm.addData("IN_STATUS", tableData.getValue("IN_STATUS",
                    this.brlbTableSelectedRow));
            TParm result = TIOM_AppServer.executeAction(
                    "action.ins.INSMaterialSearchAction", "checkSelectedAction",
                    inparm);

            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            } else {
                messageBox("核查通过.");
                int tempRow = this.brlbTableSelectedRow;
                this.onQuery();
                brlb.setSelectedRow(tempRow);
            }
        } else {
            messageBox("请选择要核查的病人信息.");
            return;
        }
    }

    /**
     * 撤销资格确认书
     */
    public void cancelSelected() {
        if (this.brlbTableSelectedRow != -1) {
            TParm inparm = new TParm();
            TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
            TParm tableData = brlb.getParmValue();
            inparm.addData("ADM_SEQ", this.getAdmSeq());
            inparm.addData("HOSP_NHI_NO", this.getNhi_no());
            inparm.addData("CONFIRM_NO",
                           tableData.getValue("CONFIRM_NO",
                                              this.brlbTableSelectedRow));
            inparm.setData("INS_TYPE",tableData.getValue("INS_CROWD_TYPE",this.brlbTableSelectedRow));
            inparm.setData("LOCAL_FLG",tableData.getValue("LOCAL_FLG",this.brlbTableSelectedRow));
            TParm result = TIOM_AppServer.executeAction(
                    "action.ins.INSMaterialSearchAction",
                    "cancelSelectedAction", inparm);

            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            } else {
                messageBox("撤销通过.");
                int tempRow = this.brlbTableSelectedRow;
                this.onQuery();
                brlb.setSelectedRow(tempRow);
            }
        } else {
            messageBox("请选择要撤销核查的病人信息.");
            return;
        }
    }

    /**
     * 汇出
     */
    public void onExport() {
        if (this.currentTabPage == 0) { //病人列表
            TTable table = (TTable)this.getComponent("BRLBTABLE");
            if (table.getRowCount() <= 0) {
                this.messageBox("没有汇出数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "病人列表");
        } else if (this.currentTabPage == 2) { //费用明细信息
            TTable table = (TTable)this.getComponent("FYMXXXTABLE");
            if (table.getRowCount() <= 0) {
                this.messageBox("没有汇出数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "费用明细信息");
        } else if (this.currentTabPage == 3) { //医保中心下载费用明细
            TTable table = (TTable)this.getComponent("YBZXXZFYMX");
            if (table.getRowCount() <= 0) {
                this.messageBox("没有汇出数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "医保中心下载费用明细");
        }
    }

    /**
     * 资格确认书打印
     */
    public void onQualificationPrint() {

        if (this.brlbTableSelectedRow == -1) {
            messageBox("没有选中患者.");
            return;
        }
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        TParm parm = brlb.getParmValue();
        String mrNo = parm.getValue("MR_NO", this.brlbTableSelectedRow); //病案号
        String inDate = parm.getValue("TOCHAR_INDATE",
                                      this.brlbTableSelectedRow); //入院日期
        String ctzDesc = parm.getValue("CTZ_DESC", this.brlbTableSelectedRow); //人员类别
        String sexDesc = parm.getValue("CHN_DESC", this.brlbTableSelectedRow); //性别
        String confirmNo = parm.getValue("CONFIRM_NO", this.brlbTableSelectedRow); //资格确认书号
//        String Sqlstr =
//                " SELECT M.CONFIRM_NO ,F.CHN_DESC AS CBQX, M.NHIHOSP_NO , M.HOSP_CLASS_CODE ,D.CHN_DESC AS ADM_CATEGORY, M.PERSONAL_NO , " +
//                " M.IDNO , M.PAT_NAME , M.SEX_CODE , M.BIRTH_DATE , M.PAT_AGE , M.CTZ1_CODE , M.UNIT_NO , " +
//                " M.INS_UNIT , M.UNIT_CODE , M.UNIT_DESC , M.DEPT_DESC , M.DIAG_DESC , M.IN_DATE , " +
//                " M.EMG_FLG , M.INP_TIME , M.INS_FLG , M.TRANHOSP_NO , M.TRANHOSP_DESC , M.ADM_SEQ , " +
//                " M.TRAN_CLASS , M.TRAN_NUM , M.HOMEBED_TYPE , M.HOMEDIAG_DESC , M.HOMEBED_TIME , " +
//                " M.HOMEBED_DAYS , M.ADDINS_AMT , M.INSBRANCH_CODE , M.ADDOWN_AMT , M.ADDPAY_AMT , " +
//                " M.ADDNUM_AMT , M.INSBASE_LIMIT_BALANCE , M.INS_LIMIT_BALANCE , M.START_STANDARD_AMT , " +
//                " M.RESTART_STANDARD_AMT , M.OWN_RATE , M.DECREASE_RATE , M.REALOWN_RATE , M.INSOWN_RATE , " +
//                " M.STATION_DESC , M.BED_NO , M.TRANHOSP_RESTANDARD_AMT , M.DS_DATE , M.DSDIAG_CODE , M.DSDIAG_DESC , " +
//                " M.DSDIAG_DESC2 , M.DEPT_CODE , M.TRANHOSP_DAYS , M.INLIMIT_DATE , M.ADM_PRJ , M.SPEDRS_CODE , " +
//                " M.INSOCC_CODE , M.MR_NO , M.OVERINP_FLG , M.INSCASE_NO , M.OPEN_FLG , M.SOURCE_CODE , M.CANCEL_FLG , " +
//                " M.CONFIRM_SRC , M.OPT_USER , M.OPT_DATE , M.OPT_TERM , M.IN_STATUS , M.UP_DATE , " +
//                " M.DOWN_DATE , M.AUD_DATE , M.DOWND_DATE , M.CASE_NO, E.CHN_DESC AS SPECIAL_PAT, M.TREATMENT_CLASS," +
//                " C.CONTACTS_ADDRESS, C.TEL_HOME, M.DEPT_CODE , ZFBL2  " + // add 自付比例2
//                " FROM INS_ADM_CONFIRM M , ADM_INP P , SYS_PATINFO C,SYS_DICTIONARY D,SYS_DICTIONARY E,SYS_DICTIONARY F " +
//                " WHERE M.MR_NO = '" + mrNo + "' " +
//                " AND TO_CHAR(M.IN_DATE,'yyyy/mm/dd')='" + inDate + "'" +
//                " AND D.GROUP_ID='INS_JYLB' AND D.ID = M.ADM_CATEGORY "+
//                " AND E.GROUP_ID='SP_PRESON_TYPE' AND E.ID = M.SPECIAL_PAT "+
//                " AND F.GROUP_ID='INS_FZX' AND F.ID=M.INSBRANCH_CODE "+
//                " and m.case_no = p.case_no " +
//                " AND M.MR_NO = C.MR_NO" +
//                " AND M.IN_STATUS <> '5' ";
        String Sqlstr =
            " SELECT M.CONFIRM_NO ,M.INSBRANCH_CODE, M.NHIHOSP_NO , M.HOSP_CLASS_CODE ,M.ADM_CATEGORY, M.PERSONAL_NO , " +
            " M.IDNO , M.PAT_NAME , M.SEX_CODE , M.BIRTH_DATE , M.PAT_AGE , M.CTZ1_CODE , M.UNIT_NO , " +
            " M.INS_UNIT , M.UNIT_CODE , M.UNIT_DESC , M.DEPT_DESC , M.DIAG_DESC , M.IN_DATE , " +
            " M.EMG_FLG , M.INP_TIME , M.INS_FLG , M.TRANHOSP_NO , M.TRANHOSP_DESC , M.ADM_SEQ , " +
            " M.TRAN_CLASS , M.TRAN_NUM , M.HOMEBED_TYPE , M.HOMEDIAG_DESC , M.HOMEBED_TIME , " +
            " M.HOMEBED_DAYS , M.ADDINS_AMT , M.INSBRANCH_CODE , M.ADDOWN_AMT , M.ADDPAY_AMT , " +
            " M.ADDNUM_AMT , M.INSBASE_LIMIT_BALANCE , M.INS_LIMIT_BALANCE , M.START_STANDARD_AMT , " +
            " M.RESTART_STANDARD_AMT , M.OWN_RATE , M.DECREASE_RATE , M.REALOWN_RATE , M.INSOWN_RATE , " +
            " M.STATION_DESC , M.BED_NO , M.TRANHOSP_RESTANDARD_AMT , M.DS_DATE , M.DSDIAG_CODE , M.DSDIAG_DESC , " +
            " M.DSDIAG_DESC2 , M.DEPT_CODE , M.TRANHOSP_DAYS , M.INLIMIT_DATE , M.ADM_PRJ , M.SPEDRS_CODE , " +
            " M.INSOCC_CODE , M.MR_NO , M.OVERINP_FLG , M.INSCASE_NO , M.OPEN_FLG , M.SOURCE_CODE , M.CANCEL_FLG , " +
            " M.CONFIRM_SRC , M.OPT_USER , M.OPT_DATE , M.OPT_TERM , M.IN_STATUS , M.UP_DATE , " +
            " M.DOWN_DATE , M.AUD_DATE , M.DOWND_DATE , M.CASE_NO, M.SPECIAL_PAT, M.TREATMENT_CLASS," +
            " C.CONTACTS_ADDRESS, C.TEL_HOME, M.DEPT_CODE , ZFBL2  " + // add 自付比例2
            " FROM INS_ADM_CONFIRM M , ADM_INP P , SYS_PATINFO C" +
            " WHERE M.MR_NO = '" + mrNo + "' " +
            " AND M.CONFIRM_NO = '"+confirmNo+"'"+
            " AND M.CASE_NO = P.CASE_NO " +
            " AND TO_CHAR(M.IN_DATE,'yyyy/mm/dd')='" + inDate + "'" +
            " AND M.MR_NO = C.MR_NO" +
            " AND M.IN_STATUS <> '5' ";

        TParm result1 = new TParm(TJDODBTool.getInstance().select(Sqlstr));

        if (result1.getCount() <= 0) {
            messageBox("没有打印数据!");
            return;
        }
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm exportData = new TParm();
        //============================lim modify 2012/04/08 begin
        String message="";
        if (result1.getValue("ADM_PRJ", 0).equals("1")) {
        	message="门诊";
		}else if(result1.getValue("ADM_PRJ", 0).equals("2")){
			message="住院";
		}else if(result1.getValue("ADM_PRJ", 0).equals("3")){
			message="转诊转院";
		}

        exportData.setData("JYXM", "TEXT", message); //就医项目
        //===========================lim modify 2012/04/08 end

//        exportData.setData("JYXM", "TEXT", result1.getValue("ADM_PRJ", 0)); //就医项目

        exportData.setData("DWDM", "TEXT", result1.getValue("UNIT_CODE", 0)); //单位代码
		exportData.setData("CBQX", "TEXT",getNameDesc("INS_FZX", result1.getValue("INS_UNIT", 0)) ) ;//参保区县
        exportData.setData("BM", "TEXT", result1.getValue("CONFIRM_NO", 0)); //编码

        exportData.setData("DWMCH", "TEXT", result1.getValue("UNIT_DESC", 0)); //单位名称

        exportData.setData("GMSFHM", "TEXT", result1.getValue("IDNO", 0)); //公民身份证号码
        exportData.setData("XM", "TEXT", result1.getValue("PAT_NAME", 0)); //姓名
        exportData.setData("XB", "TEXT", sexDesc); //性别
        exportData.setData("NL", "TEXT", result1.getValue("PAT_AGE", 0)); //年龄

        exportData.setData("RYLB", "TEXT", ctzDesc); //人员类别
        exportData.setData("SFJZ", "TEXT",
                           String.valueOf("N".equals(result1.
                getValue("EMG_FLG", 0)) ? "否" : "是")); //是否急诊
        exportData.setData("TSRYBS", "TEXT",getNameDesc("SP_PRESON_TYPE", result1.getValue("SPECIAL_PAT", 0))); //特殊人员标识

        exportData.setData("JZHYYMC", "TEXT", Operator.getHospitalCHNFullName()); //就诊医院名称
        exportData.setData("JB", "TEXT", "三级"); //级别
        //String SQLDept = " SELECT DEPT_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+hospDeptCode+"'";
        exportData.setData("KB", "TEXT", result1.getValue("DEPT_DESC", 0)); //科别-----------------------
        exportData.setData("BAH", "TEXT", mrNo); //病案号

        exportData.setData("RYZHD", "TEXT", result1.getValue("DIAG_DESC", 0)); //入院诊断
        exportData.setData("DJCZHY", "TEXT", result1.getValue("INP_TIME", 0)); //第几次住院
        exportData.setData("JYLB", "TEXT", getNameDesc("INS_JYLB", result1.getValue("ADM_CATEGORY", 0))); //就医类别------------------

        exportData.setData("ZHYRQ", "TEXT", inDate); //住院日期
        exportData.setData("ZHYZHCHJZRQ", "TEXT",
                           null!=result1.getValue("INLIMIT_DATE", 0)&&result1.getValue("INLIMIT_DATE", 0).length()>0?result1.getValue("INLIMIT_DATE", 0).substring(0,10):""); //住院最长截止日期

        exportData.setData("ZHZHDJBH", "TEXT",
                           (result1.getValue("TRAN_NUM", 0) == null ? " " :
                            result1.getValue("TRAN_NUM", 0))); //转诊登记编号
        exportData.setData("ZHZHYLJZYTS", "TEXT",
                           result1.getValue("TRANHOSP_DAYS", 0)); //转诊院累计住院天数

        exportData.setData("ZHCHYMCH", "TEXT",
                           (result1.getValue("TRANHOSP_DESC", 0) == null ? " " :
                            result1.getValue("TRANHOSP_DESC", 0))); //转出院名称
        exportData.setData("ZHCHJB", "TEXT",
                           (result1.getValue("TRAN_CLASS", 0) == null ? " " :
                            result1.getValue("TRAN_CLASS", 0))); //级别
        exportData.setData("MTZHY", "TEXT",
                           (result1.getValue("SPEDRS_CODE", 0) == null ? " " :
                            result1.getValue("SPEDRS_CODE", 0))); //门特住院

//		exportData.setData("JLJCHJGMC", "TEXT", data) ;//建立家床机构名称
        exportData.setData("JCHLJTSH", "TEXT",
                           result1.getValue("HOMEBED_DAYS", 0)); //家床累计天数
        exportData.setData("JCHZHD", "TEXT",
                           result1.getValue("HOMEDIAG_DESC", 0)); //家床诊断

		exportData.setData("TCHZFBL1", "TEXT", result1.getValue("OWN_RATE", 0)) ;//统筹自付比例1
		exportData.setData("TCHZFBL2", "TEXT", result1.getValue("REALOWN_RATE", 0)) ;//统筹自付比例2

        exportData.setData("CJYLJZH", "TEXT",
                           String.valueOf("Y".equals(result1.
                getValue("INS_FLG", 0)) ? "是" : "否")); //参加医疗救助
        exportData.setData("JZHZFBL", "TEXT", result1.getValue("INSOWN_RATE", 0)); //救助自付比例

		exportData.setData("QFBZHSHYE", "TEXT", "0.00") ;//起付标准剩余额
        exportData.setData("QFBZH", "TEXT",
                           df.format(result1.getDouble("START_STANDARD_AMT", 0))); //起付标准
        exportData.setData("SHSHQFBZHJE", "TEXT",
                           df.format(result1.getDouble("RESTART_STANDARD_AMT",
                0))); //实收起付标准金额

        exportData.setData("WJSYLJGFSHJE", "TEXT", "0.00"); //未结算医疗机构发生金额
        exportData.setData("WJSYLJGSHBJE", "TEXT", "0.00"); //未结算医疗机构申报金额
        exportData.setData("YJSLJSHPJE", "TEXT",
                           df.format(result1.getDouble("ADDNUM_AMT", 0))); //已结算累计审批金额
        exportData.setData("TCHJJZGZFXESHYE", "TEXT",
                           df.format(result1.
                                     getDouble("INSBASE_LIMIT_BALANCE", 0))); //统筹基金最高支付限额剩余额
        exportData.setData("YLJZHZHGZFXESHYE", "TEXT",
                           df.format(result1.getDouble("INS_LIMIT_BALANCE", 0))); //医疗救助最高支付限额剩余额
        exportData.setData("DEJZHCHBDW", "TEXT",
        		getNameDesc("INS_DEJZDW",result1.getValue("INSOCC_CODE", 0))); //大额救助承办单位

        this.openPrintWindow("%ROOT%\\config\\prt\\INS\\QualificationTable.jhw",
                             exportData);
    }

    /**
     * 获得中文说明
     * @param groupId
     * @param id
     * @return
     */
    private String getNameDesc(String groupId ,String id){
    	String sql=" SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID ='"+groupId+"' AND ID='"+id+"'";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result1.getErrCode()<0) {
			return id;
		}
    	return result1.getValue("CHN_DESC",0);
    }

    /**
     * 二号表打印
     */
    public void onPrint2() {
    	
        if (this.brlbTableSelectedRow == -1) {
            messageBox("没有选中患者.");
            return;
        }
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        TParm parm = brlb.getParmValue();
        String mrNo = parm.getValue("MR_NO", this.brlbTableSelectedRow); //病案号
        //String dsDate = parm.getValue("TOCHAR_DSDATE", this.brlbTableSelectedRow) ;//出院日期
        String confirmNo = parm.getValue("CONFIRM_NO",
                                         this.brlbTableSelectedRow); //资格确认书号
        String admSeq = parm.getValue("ADM_SEQ",
                this.brlbTableSelectedRow); //资格确认书号
        String dBZ = this.getValueString("DBZ"); //单病种.
        //出院日期查询
        String searchSql = "SELECT DS_DATE,IN_STATUS FROM INS_ADM_CONFIRM WHERE CONFIRM_NO = '" +
                           confirmNo + "'";
//        System.out.println("出院日期查询sql"+searchSql);
        TParm searchResult = new TParm(TJDODBTool.getInstance().select(
                searchSql));
        String dsDate = searchResult.getValue("DS_DATE", 0); //出院日期
        String totString=null;
		totString=" M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT AS HJF, " ;
        String[] dsDateArray = dsDate.split(" ");
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        if ("PTBR".equals(dBZ)) { //普通病人
            String sql = " SELECT case " +
//                    " when  substr(ctz_code,0,1) = '1' then '城职' " +
//                    " when  substr(ctz_code,0,1) = '2' then '城乡' " +
                    " when  Z.INS_CROWD_TYPE = '1' then '城职' " +
                    " when  Z.INS_CROWD_TYPE = '2' then '城乡' " +
                    " end as XZ," +
            		" SUBSTR(M.YEAR_MON,1,4)||'年'||SUBSTR(M.YEAR_MON,5,2)||'月' AS SJ, " +
                    " M.ADM_CATEGORY , " +
//                    " case " +
//                    " when  M.ADM_CATEGORY = '21' then '普通住院' " +
//                    " when  M.ADM_CATEGORY = '24' then '计生住院' " +
//                    " when  M.ADM_CATEGORY = '26' then '甲类传染病住院' " +
//                    " when  M.ADM_CATEGORY = '27' then '非甲类传染病' " +
//                    " when  M.ADM_CATEGORY = '31' then 'I类住院特殊病' " +
//                    " when  M.ADM_CATEGORY = '32' then 'II类住院特殊病' " +
//                    " when  M.ADM_CATEGORY = '33' then '社区I类门特家床' " +
//                    " when  M.ADM_CATEGORY = '34' then '社区II类门特家床' " +
//                    " else ''" +
//                    " end AS JYLB," +
                    " V.CHN_DESC AS JYLB ,"+
                    "I.IPD_NO," +
                    " M.IDNO , M.PAT_NAME , X.CHN_DESC , M.PAT_AGE , Z.CTZ_DESC , " +
                    " M.UNIT_DESC , M.UNIT_CODE, " +
//                    " case " +
//                    " when  A.SPECIAL_PAT = '01' then '正常' " +
//                    " when  A.SPECIAL_PAT = '02' then '两航起义' " +
//                    " when  A.SPECIAL_PAT = '03' then '市级退模' " +
//                    " when  A.SPECIAL_PAT = '04' then '伤残军人' " +
//                    " when  A.SPECIAL_PAT = '05' then '老塘人员' " +
//                    " when  A.SPECIAL_PAT = '06' then '公务员' " +
//                    " when  A.SPECIAL_PAT = '07' then '民政救助人员' " +
//                    " when  A.SPECIAL_PAT = '08' then '优抚对象' " +
//                    " when  A.SPECIAL_PAT = '09' then '非典后遗症'  " +
//                    " else '' " +
//                    " end AS RYBS, " +
                    " U.CHN_DESC AS RYBS,"+
                    " M.CONFIRM_NO , O.USER_NAME , B.CHN_DESC AS CBQX, " + //资格确认书号12,经治医师13,参保区县
                    " to_char(M.IN_DATE,'yyyymmdd') AS IN_DATE,to_char(M.DS_DATE,'yyyymmdd') AS DS_DATE,M.IN_DATE AS IN_DATETEMP ,M.DS_DATE AS DS_DATETEMP," +
                   // "CASE WHEN ROUND(TO_NUMBER(M.DS_DATE-M.IN_DATE))<=0 THEN 1 ELSE ROUND(TO_NUMBER(M.DS_DATE-M.IN_DATE)) END AS REAL_STAY_DAYS," +
                    "I.ADM_DAYS AS ZHYTSH, M.MR_NO , " + //住院日期,出院日期,住院天数,住院号
                    " M.BED_NO , " + //床位号
                    " case " +
                    " when  M.HOSP_CLS_CODE = '01' then '一级' " +
                    " when  M.HOSP_CLS_CODE = '02' then '二级' " +
                    " when  M.HOSP_CLS_CODE = '03' then '三级' " +
                    " else ''" +
                    " end AS JB, " + //,级别
                    " M.DEPT_DESC , M.START_STANDARD_AMT , T.CHN_DESC , " + //,科别,起付标准,门特病种//
                    " M.TRANHOSP_DESC , " + //转出院名称
                    " case " +
                    " when  M.TRAN_CLASS = '01' then '一级' " +
                    " when  M.TRAN_CLASS = '02' then '二级' " +
                    " when  M.TRAN_CLASS = '03' then '三级' " +
                    " else '' " +
                    " end AS ZHYJB, " + //,转出院级别
                    " M.TRANHOSP_RESTANDARD_AMT , " + //,转出院实收起付标准,
                    " M.INP_TIME , M.HOMEDIAG_DESC , M.DIAG_CODE||M.DIAG_DESC||' '||" + //家床,家庭病种
                    " (case " +
                    " when  M.TRAN_CLASS = '1' then '治愈'" +
                    " when  M.TRAN_CLASS = '2' then '好转'" +
                    " when  M.TRAN_CLASS = '3' then '未愈'" +
                    " when  M.TRAN_CLASS = '4' then '死亡'" +
                    " when  M.TRAN_CLASS = '5' then '其他'" +
                    " else ''" +
                    " end) AS CHYZHD " + //,出院诊断1
                    ", M.DIAG_DESC2 , " + //,出院诊断2
                    " M.BASEMED_BALANCE , M.INS_BALANCE , " + //上次医保基金支付限额剩余余额,上次救助基金支付剩余额
                    " case when M.OWN_RATE <= 0 then 0 else M.OWN_RATE end AS ZFBL1, " + //自负比例1
                    " case when A.zfbl2 <= 0 then 0 else A.zfbl2 end AS ZFBL2, " + //,自负比例2
                    " case when M.INSOWN_RATE <= 0 then 0 else M.INSOWN_RATE end AS JZHZF, " + //,救助自负比例,
                    " M.PERSON_ACCOUNT_AMT , " + //个人账户余额
                    " case when M.DECREASE_RATE <= 0 then 0 else M.DECREASE_RATE end AS JFBL1, " + //,减负比例
                    " case when M.REALOWN_RATE <= 0 then 0 else M.REALOWN_RATE end AS SHJZF, " + //,实际自负比例
                    // 下部开始
                    " M.RESTART_STANDARD_AMT , STARTPAY_OWN_AMT , " + //实收起付标准金额,超起付标准自负比例金额
                    " M.INS_HIGHLIMIT_AMT , M.PERCOPAYMENT_RATE_AMT , " + //最高限额以上金额,医疗救助自负比例金额
                    " M.OWN_AMT , M.ADD_AMT , M.RESTART_STANDARD_AMT+M.STARTPAY_OWN_AMT+M.OWN_AMT+M.PERCOPAYMENT_RATE_AMT+M.ADD_AMT+M.INS_HIGHLIMIT_AMT AS GRRHJ, " + //自费金额,增负金额,合计,
                    //" M.ACCOUNT_PAY_AMT , M.RESTART_STANDARD_AMT+M.STARTPAY_OWN_AMT+M.OWN_AMT+M.PERCOPAYMENT_RATE_AMT+M.ADD_AMT+M.INS_HIGHLIMIT_AMT-M.ACCOUNT_PAY_AMT - case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end AS SJZFJE , " + //个人账户负担,实际自负金额,
                    " M.ACCOUNT_PAY_AMT , " +//个人账户负担
			        " case when M.RESTART_STANDARD_AMT is null then 0 else M.RESTART_STANDARD_AMT end "+
			        " + "+
			        " case when M.STARTPAY_OWN_AMT is null then 0 else M.STARTPAY_OWN_AMT end "+
			        " + "+
			        " case when M.OWN_AMT is null then 0 else M.OWN_AMT end "+
			        " + "+
			        " case when M.PERCOPAYMENT_RATE_AMT is null then 0 else M.PERCOPAYMENT_RATE_AMT end "+
			        " + "+
			        " case when M.ADD_AMT is null then 0 else M.ADD_AMT end "+
			        " + "+
			        " case when M.INS_HIGHLIMIT_AMT is null then 0 else M.INS_HIGHLIMIT_AMT end "+
			        " - "+
			        " case when M.ACCOUNT_PAY_AMT is null then 0 else M.ACCOUNT_PAY_AMT end "+
			        " - "+
			        " case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end " +
			        " - "+
			        " case when M.ILLNESS_SUBSIDY_AMT is null then 0 else M.ILLNESS_SUBSIDY_AMT end AS SJZFJE," + //实际自负金额,
                    " M.NHI_PAY , M.TOT_PUBMANADD_AMT , M.ARMYAI_AMT , M.ACCOUNT_PAY_AMT , " + //统申,公申,军申,个申
                    " M.NHI_PAY_REAL , case when M.TOT_PUBMANADD_AMT is null then 0 else M.TOT_PUBMANADD_AMT end AS GZ, " + //统支,公支
                    " case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end AS JUNZ, " + //,军支,
                    " case when M.ACCOUNT_PAY_AMT is null  then 0 else M.ACCOUNT_PAY_AMT  end AS GEZHI, " + //个支
                    " M.NHI_PAY+ " +
                    " (case when M.TOT_PUBMANADD_AMT is null then 0 else M.TOT_PUBMANADD_AMT end) + " +
                    " (case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end)+ " +
                    " (case when M.NHI_COMMENT is null then 0 else M.NHI_COMMENT end)+ " +
                    " (case when M.ACCOUNT_PAY_AMT is null  then 0 else M.ACCOUNT_PAY_AMT  end)+ " +
                    " (case when M.ILLNESS_SUBSIDY_AMT is null  then 0 else M.ILLNESS_SUBSIDY_AMT  end) AS YLJGSHQHJ, '整' AS YLJGSHQJEHEDX, " + //医疗机构申请金额合计,医疗机构申请金额合计大写
                    " M.NHI_PAY_REAL+ " +
                    " (case when M.TOT_PUBMANADD_AMT is null then 0 else M.TOT_PUBMANADD_AMT end) + " +
                    " (case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end)+ " +
                    " (case when M.NHI_COMMENT is null then 0 else M.NHI_COMMENT end)+ " +
                    " (case when M.ACCOUNT_PAY_AMT is null  then 0 else M.ACCOUNT_PAY_AMT  end) + " +
                    " (case when M.ILLNESS_SUBSIDY_AMT is null  then 0 else M.ILLNESS_SUBSIDY_AMT  end) AS SHBZHFJEHJ, '整' AS SHBZHFJEHJDX, " + //社保支付金额合计,社保支付金额合计大写
                    " M.APPLY_AMT , '整'AS APPLY_AMT_CHI , " + //医疗机构申请金额,医疗机构申请金额大写
                    " M.HOSP_APPLY_AMT , '整' AS HOSP_APPLY_AMT_CHI, " + //建议救助支付金额,建议救助支付金额大写
                    // 中间部分，不包括拒付，“审核金额”不知道写什么
                    " M.PHA_AMT , M.PHA_OWN_AMT , M.PHA_ADD_AMT , M.PHA_NHI_AMT , '' AS YSH, " + //药发,药自,药增,药申,药审
                    " M.EXM_AMT , M.EXM_OWN_AMT , M.EXM_ADD_AMT , M.EXM_NHI_AMT , '' AS JSH, " + //检发,检自,检增,检申,检审,
                    " M.TREAT_AMT , M.TREAT_OWN_AMT , M.TREAT_ADD_AMT , M.TREAT_NHI_AMT , '' AS ZHSH , " + //治发,治自,治增,治申,治审,
                    " M.OP_AMT , M.OP_OWN_AMT , M.OP_ADD_AMT , M.OP_NHI_AMT , '' AS SHSHEN, " + //手发,手自,手增,手申,手审
                    " M.BED_AMT , M.BED_OWN_AMT , M.BED_ADD_AMT , M.BED_NHI_AMT , '' AS CHSHEN, " + //床发,床自,床增,床申,床审
                    " M.MATERIAL_AMT , M.MATERIAL_OWN_AMT , M.MATERIAL_ADD_AMT , M.MATERIAL_NHI_AMT , '' AS YSHEN, " + //医发,医自,医增,医申,医审
                    " M.BLOODALL_AMT , M.BLOODALL_OWN_AMT , M.BLOODALL_ADD_AMT , M.BLOODALL_NHI_AMT , '' AS SHUSHEN, " + //输发,输自,输增,输申,输审
                    " M.BLOOD_AMT , M.BLOOD_OWN_AMT , M.BLOOD_ADD_AMT, M.BLOOD_NHI_AMT , '' AS CHENSH, " + //成发,成自,成增,成申,成审
                    " M.OTHER_AMT , M.OTHER_OWN_AMT , M.OTHER_ADD_AMT , M.OTHER_NHI_AMT , '' AS QSHEN, " + //其发,其自,其增,其申,其审,
                    // “其中：材料”项目不知道写什么
                    " '' AS CAIF, '' AS CAIZ , '' AS CAIZENG , '' AS CAISHEN, '' AS CAISH, " + //材发,材自,材增,材申,材审,
                    totString+
                    " M.PHA_OWN_AMT+M.EXM_OWN_AMT+M.TREAT_OWN_AMT+M.OP_OWN_AMT+M.BED_OWN_AMT+M.MATERIAL_OWN_AMT+M.BLOODALL_OWN_AMT+M.BLOOD_OWN_AMT+M.OTHER_OWN_AMT AS HEZ, " +
                    " M.PHA_ADD_AMT+M.EXM_ADD_AMT+M.TREAT_ADD_AMT+M.OP_ADD_AMT+M.BED_ADD_AMT+M.MATERIAL_ADD_AMT+M.BLOODALL_ADD_AMT+M.BLOOD_ADD_AMT+M.OTHER_ADD_AMT AS HEZENG, " +
                    " M.PHA_NHI_AMT+M.EXM_NHI_AMT+M.TREAT_NHI_AMT+M.OP_NHI_AMT+M.BED_NHI_AMT+M.MATERIAL_NHI_AMT+M.BLOODALL_NHI_AMT+M.BLOOD_NHI_AMT+M.OTHER_NHI_AMT AS HESHEN, " +
                    " '' AS HESH, " + //合计发,合计自,合计增,合计申,合计审,
                    " M.REGION_CODE , M.ADM_SEQ , M.CONFIRM_NO, A.SPECIAL_PAT, M.ARMYAI_AMT,M.NHI_COMMENT," +
                    " case when M.ILLNESS_SUBSIDY_AMT is null then 0 else M.ILLNESS_SUBSIDY_AMT end AS ILLNESS_SUBSIDY_AMT, " +
                    " case when A.OUTP_CARRYOVER_AMT is null then 0 else A.OUTP_CARRYOVER_AMT end AS OUTP_CARRYOVER_AMT " +
                    " FROM INS_IBS M " +
                    " left JOIN INS_ADM_CONFIRM A " +
                    " ON M.CONFIRM_NO = A.CONFIRM_NO " +
                    " left JOIN ADM_INP I " +
                    " ON M.REGION_CODE = I.REGION_CODE " +
                    " AND M.MR_NO = I.MR_NO " +
                    " AND M.CASE_NO = I.CASE_NO " +
                    " left JOIN SYS_OPERATOR O " +
                    " ON I.REGION_CODE = O.REGION_CODE " +
                    " AND I.VS_DR_CODE = O.USER_ID " +
                    " left JOIN SYS_DICTIONARY X " +
                    " ON M.SEX_CODE = X.ID AND X.GROUP_ID = 'SYS_SEX' " +
                    " INNER JOIN SYS_CTZ Z " +
//                    " ON M.CTZ1_CODE = Z.CTZ_CODE " +//modify by lim 2012/0426
                    " ON A.HIS_CTZ_CODE = Z.CTZ_CODE " +//modify by lim 2012/0426
                    " left JOIN SYS_DICTIONARY B " +
                    " ON M.INSBRANCH_CODE = B.ID AND B.GROUP_ID='INS_FZX' " +
                    " left JOIN SYS_DICTIONARY T " +
                    " ON M.SPEDRS_CODE = T.ID AND T.GROUP_ID = 'INS_MTLBA' " +
                    " left JOIN SYS_DICTIONARY V "+
                    " ON V.GROUP_ID = 'INS_JYLB' AND V.ID = M.ADM_CATEGORY "+
                    " left JOIN SYS_DICTIONARY U "+
                    " ON U.GROUP_ID = 'SP_PRESON_TYPE' AND U.ID = A.SPECIAL_PAT "+
                    " WHERE M.MR_NO = '" + mrNo + "' AND M.ADM_SEQ='"+admSeq+"'" ;
//                    " AND TO_CHAR(M.DS_DATE,'yyyy-mm-dd') = '" + dsDateArray[0] + "' ";
//                    System.out.println("二号表数据sql:::::::::::::::::::::::::::::::::::::::::"+sql);
           
            TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));

            if (result1.getCount() <= 0) {
                messageBox("没有打印数据!");
                return;
            }

            DecimalFormat df = new DecimalFormat("##########0.00");
            TParm exportData = new TParm();
            exportData.setData("YLJGDM", "TEXT", "0124765-6"); //医疗机构代码------------
            exportData.setData("TITLERQ", "TEXT", result1.getValue("SJ", 0)); //日期
            if(result1.getValue("XZ", 0).equals("城职"))
            exportData.setData("BH", "TEXT", "津社保医支字2号"); //表号//TODO:           
            else if(result1.getValue("XZ", 0).equals("城乡"))
            exportData.setData("BH", "TEXT", "津社保医支字2-2号");
            
            exportData.setData("YLJGMCH", "TEXT", "天津市泰达国际心血管病医院"); //医疗机构名称
            exportData.setData("JYLB", "TEXT", result1.getValue("JYLB", 0)); //就医类别
            exportData.setData("DW", "TEXT", "元"); //单位

            exportData.setData("GMSHFZHHM", "TEXT", result1.getValue("IDNO", 0)); //公民身份证号码
            exportData.setData("XM", "TEXT", result1.getValue("PAT_NAME", 0)); //姓名
            exportData.setData("XB", "TEXT", result1.getValue("CHN_DESC", 0)); //性别
            exportData.setData("NL", "TEXT", result1.getValue("PAT_AGE", 0)); //年龄
            exportData.setData("RYLB", "TEXT", result1.getValue("CTZ_DESC", 0)); //人员类别

            exportData.setData("DWMCH", "TEXT", result1.getValue("UNIT_DESC", 0)); //单位名称
            exportData.setData("DWDM", "TEXT", result1.getValue("UNIT_CODE", 0)); //单位代码
            exportData.setData("RYBSH", "TEXT", result1.getValue("RYBS", 0)); //人员标识

            exportData.setData("ZGQRSHH", "TEXT",
                               result1.getValue("CONFIRM_NO", 0)); //资格确认书号
            exportData.setData("JZHYSH", "TEXT",
                               result1.getValue("USER_NAME", 0)); //经治医师
            exportData.setData("CBQX", "TEXT", result1.getValue("CBQX", 0)); //参保区县

            String inDate = result1.getValue("IN_DATE", 0);
            String outDate = result1.getValue("DS_DATE", 0);            
            Timestamp outDatetemp=result1.getTimestamp("DS_DATETEMP", 0);
            Timestamp inDatetemp=result1.getTimestamp("IN_DATETEMP", 0);
            Timestamp date1 = StringTool.getTimestamp(
            		dfdate.format(outDatetemp)+" 00:00:00", "yyyy-MM-dd HH:mm:ss"); 
            Timestamp date2 = StringTool.getTimestamp(
            		dfdate.format(inDatetemp)+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
            if(inDate!=null && !"".equals(inDate)){
				exportData.setData("ZHYRQ", "TEXT", (inDate.substring(0, 4)
						+ "年" + inDate.substring(4, 6) + "月"
						+ inDate.substring(6, 8) + "日")); // 住院日期
			}
            else{
            	exportData.setData("ZHYRQ", "TEXT","") ;
            }
            if(outDate!=null && !"".equals(outDate)){
	            exportData.setData("CHYRQ", "TEXT",
	                               (outDate.substring(0, 4) + "年" +
	                                outDate.substring(4, 6) + "月" +
	                                outDate.substring(6, 8) + "日")); //出院日期
            }else{
            	exportData.setData("CHYRQ", "TEXT","");
            }

//            String inDays = "" ;
//            if( (inDate!=null && !"".equals(inDate)) && (outDate!=null && !"".equals(outDate))){
//            	if(inDate.equals(outDate)){
//            		inDays = "1" ;
//            	}else{
//                	Date inDateNew = new Date(Integer.parseInt(inDate.substring(0, 4)), Integer.parseInt(inDate.substring(4, 6)), Integer.parseInt(inDate.substring(6, 8))) ;
//                	Date outDateNew = new Date(Integer.parseInt(outDate.substring(0, 4)), Integer.parseInt(outDate.substring(4, 6)), Integer.parseInt(outDate.substring(6, 8))) ;
//                	long inDateTs = inDateNew.getTime() ;
//                	long outDateTs = outDateNew.getTime() ;
//                	long diffTime = outDateTs - inDateTs ;
//                	inDays = (diffTime/(24*60*60*1000))+"" ;
//            	}
//            }
            int rollDate =0;
            if(inDate.equals(outDate))
            	rollDate =1;
            else
                rollDate = StringTool.getDateDiffer(date1, date2);
            //exportData.setData("ZHYTSH", "TEXT", result1.getValue("ZHYTSH", 0)); //住院天数
            exportData.setData("ZHYTSH", "TEXT",  rollDate); //住院天数
            exportData.setData("ZHYH", "TEXT", result1.getValue("IPD_NO", 0)); //住院号

            exportData.setData("CHWH", "TEXT", result1.getValue("BED_NO", 0)); //床位号
            exportData.setData("JB", "TEXT", result1.getValue("JB", 0)); //级别
            exportData.setData("KB", "TEXT", result1.getValue("DEPT_DESC", 0)); //科别
            exportData.setData("QFBZH", "TEXT",
                               result1.getValue("START_STANDARD_AMT", 0)); //起付标准
            exportData.setData("MTBZH", "TEXT",
                               result1.getValue("MT_DISEASE_DESC", 0)); //门特病种

            exportData.setData("ZHCHYYMC", "TEXT",
                               result1.getValue("TRANHOSP_DESC", 0)); //转出院名称
            exportData.setData("ZHCYYJB", "TEXT", result1.getValue("ZHYJB", 0)); //转出院级别
            exportData.setData("ZHCHYSHSHQFBZH", "TEXT",
                               result1.getValue("TRANHOSP_RESTANDARD_AMT", 0)); //转出院实收起付标准

            exportData.setData("DJCZHY", "TEXT", result1.getValue("INP_TIME", 0)); //第几次住院
            exportData.setData("JCHBZH", "TEXT",
                               result1.getValue("HOMEDIAG_DESC", 0)); //家床病种
            exportData.setData("JCHZHD", "TEXT", " "); //家床诊断------------

            exportData.setData("CHYZHD1", "TEXT", result1.getValue("CHYZHD", 0)); //出院诊断1
            exportData.setData("CHYZHD2", "TEXT",
                               result1.getValue("DIAG_DESC2", 0)); //出院诊断2

            exportData.setData("ZFBL1", "TEXT", result1.getValue("ZFBL1", 0)); //自负比例1
            exportData.setData("ZFBL2", "TEXT", result1.getValue("ZFBL2", 0)); //自负比例2
            exportData.setData("JFBL1", "TEXT", result1.getValue("JFBL1", 0)); //肩负比例
            exportData.setData("SHJZF", "TEXT", result1.getValue("SHJZF", 0)); //实际自付
            exportData.setData("JZHZF", "TEXT", result1.getValue("JZHZF", 0)); //救助自负

            exportData.setData("SHCYB", "TEXT",
                               df.format(result1.getDouble("BASEMED_BALANCE", 0))); //上次医保基金支付限额
            exportData.setData("SHCJZH", "TEXT",
                               df.format(result1.getDouble("INS_BALANCE", 0))); //上次救助基金
//            exportData.setData("GRZHHYE", "TEXT",
//                               df.format(result1.getDouble("PERSON_ACCOUNT_AMT",
//                    0))); //个人账户余额
            exportData.setData("GRZHHYE", "TEXT",""); //个人账户余额
            //门诊跨年度结转
            exportData.setData("MZKNDJY", "TEXT",
            		           df.format(result1.getDouble("OUTP_CARRYOVER_AMT", 0)));
            //药品费
            exportData.setData("YPF01", "TEXT",
                               df.format(result1.getDouble("PHA_AMT", 0))); //发生金额
            exportData.setData("YPF02", "TEXT",
                               df.format(result1.getDouble("PHA_OWN_AMT", 0))); //自费
            exportData.setData("YPF03", "TEXT",
                               df.format(result1.getDouble("PHA_ADD_AMT", 0))); //增付
            exportData.setData("YPF04", "TEXT",
                               df.format(result1.getDouble("PHA_NHI_AMT", 0))); //申报金额
            exportData.setData("YPF05", "TEXT", " "); //拒付
            exportData.setData("YPF06", "TEXT",
                               df.format(result1.getDouble("YSH", 0))); //审核金额

            //检查费
            exportData.setData("JCF01", "TEXT",
                               df.format(result1.getDouble("EXM_AMT", 0))); //发生金额
            exportData.setData("JCF02", "TEXT",
                               df.format(result1.getDouble("EXM_OWN_AMT", 0))); //自费
            exportData.setData("JCF03", "TEXT",
                               df.format(result1.getDouble("EXM_ADD_AMT", 0))); //增付
            exportData.setData("JCF04", "TEXT",
                               df.format(result1.getDouble("EXM_NHI_AMT", 0))); //申报金额
            exportData.setData("JCF05", "TEXT", " "); //拒付
            exportData.setData("JCF06", "TEXT",
                               df.format(result1.getDouble("JSH", 0))); //审核金额

            //治疗费
            exportData.setData("ZLF01", "TEXT",
                               df.format(result1.getDouble("TREAT_AMT", 0))); //发生金额
            exportData.setData("ZLF02", "TEXT",
                               df.format(result1.getDouble("TREAT_OWN_AMT", 0))); //自费
            exportData.setData("ZLF03", "TEXT",
                               df.format(result1.getDouble("TREAT_ADD_AMT", 0))); //增付
            exportData.setData("ZLF04", "TEXT",
                               df.format(result1.getDouble("TREAT_NHI_AMT", 0))); //申报金额
            exportData.setData("ZLF05", "TEXT", " "); //拒付
            exportData.setData("ZLF06", "TEXT",
                               df.format(result1.getDouble("ZHSH", 0))); //审核金额

            //手术费
            exportData.setData("SSF01", "TEXT",
                               df.format(result1.getDouble("OP_AMT", 0))); //发生金额
            exportData.setData("SSF02", "TEXT",
                               df.format(result1.getDouble("OP_OWN_AMT", 0))); //自费
            exportData.setData("SSF03", "TEXT",
                               df.format(result1.getDouble("OP_ADD_AMT", 0))); //增付
            exportData.setData("SSF04", "TEXT",
                               df.format(result1.getDouble("OP_NHI_AMT", 0))); //申报金额
            exportData.setData("SSF05", "TEXT", " "); //拒付
            exportData.setData("SSF06", "TEXT",
                               df.format(result1.getDouble("SHSHEN", 0))); //审核金额

            //床位费
            exportData.setData("CWF01", "TEXT",
                               df.format(result1.getDouble("BED_AMT", 0))); //发生金额
            exportData.setData("CWF02", "TEXT",
                               df.format(result1.getDouble("BED_OWN_AMT", 0))); //自费
            exportData.setData("CWF03", "TEXT",
                               df.format(result1.getDouble("BED_ADD_AMT", 0))); //增付
            exportData.setData("CWF04", "TEXT",
                               df.format(result1.getDouble("BED_NHI_AMT", 0))); //申报金额
            exportData.setData("CWF05", "TEXT", " "); //拒付
            exportData.setData("CWF06", "TEXT",
                               df.format(result1.getDouble("CHSHEN", 0))); //审核金额

            //医用材料
            exportData.setData("YYCL01", "TEXT",
                               df.format(result1.getDouble("MATERIAL_AMT", 0))); //发生金额
            exportData.setData("YYCL02", "TEXT",
                               df.format(result1.getDouble("MATERIAL_OWN_AMT",
                    0))); //自费
            exportData.setData("YYCL03", "TEXT",
                               df.format(result1.getDouble("MATERIAL_ADD_AMT",
                    0))); //增付
            exportData.setData("YYCL04", "TEXT",
                               df.format(result1.getDouble("MATERIAL_NHI_AMT",
                    0))); //申报金额
            exportData.setData("YYCL05", "TEXT", " "); //拒付
            exportData.setData("YYCL06", "TEXT",
                               df.format(result1.getDouble("YSHEN", 0))); //审核金额

            //输全血
            exportData.setData("SQX01", "TEXT",
                               df.format(result1.getDouble("BLOODALL_AMT", 0))); //发生金额
            exportData.setData("SQX02", "TEXT",
                               df.format(result1.getDouble("BLOODALL_OWN_AMT",
                    0))); //自费
            exportData.setData("SQX03", "TEXT",
                               df.format(result1.getDouble("BLOODALL_ADD_AMT",
                    0))); //增付
            exportData.setData("SQX04", "TEXT",
                               df.format(result1.getDouble("BLOODALL_NHI_AMT",
                    0))); //申报金额
            exportData.setData("SQX05", "TEXT", " "); //拒付
            exportData.setData("SQX06", "TEXT",
                               df.format(result1.getDouble("SHUSHEN", 0))); //审核金额

            //成分输血
            exportData.setData("CFSX01", "TEXT",
                               df.format(result1.getDouble("BLOOD_AMT", 0))); //发生金额
            exportData.setData("CFSX02", "TEXT",
                               df.format(result1.getDouble("BLOOD_OWN_AMT", 0))); //自费
            exportData.setData("CFSX03", "TEXT",
                               df.format(result1.getDouble("BLOOD_ADD_AMT", 0))); //增付
            exportData.setData("CFSX04", "TEXT",
                               df.format(result1.getDouble("BLOOD_NHI_AMT", 0))); //申报金额
            exportData.setData("CFSX05", "TEXT", " "); //拒付
            exportData.setData("CFSX06", "TEXT",
                               df.format(result1.getDouble("CHENSH", 0))); //审核金额

            //其他
            exportData.setData("QT01", "TEXT",
                               df.format(result1.getDouble("OTHER_AMT", 0))); //发生金额
            exportData.setData("QT2", "TEXT",
                               df.format(result1.getDouble("OTHER_OWN_AMT", 0))); //自费
            exportData.setData("QT3", "TEXT",
                               df.format(result1.getDouble("OTHER_ADD_AMT", 0))); //增付
            exportData.setData("QT4", "TEXT",
                               df.format(result1.getDouble("OTHER_NHI_AMT", 0))); //申报金额
            exportData.setData("QT5", "TEXT", " "); //拒付
            exportData.setData("QT6", "TEXT",
                               df.format(result1.getDouble("QSHEN", 0))); //审核金额

            //其中一次性材料
            exportData.setData("QZH01", "TEXT",
                               df.format(result1.getDouble("CAIF", 0))); //发生金额
            exportData.setData("QZH02", "TEXT",
                               df.format(result1.getDouble("CAIZ", 0))); //自费
            exportData.setData("QZH03", "TEXT",
                               df.format(result1.getDouble("CAIZENG", 0))); //增付
            exportData.setData("QZH04", "TEXT",
                               df.format(result1.getDouble("CAISHEN", 0))); //申报金额
            exportData.setData("QZH05", "TEXT", " "); //拒付
            exportData.setData("QZH06", "TEXT",
                               df.format(result1.getDouble("CAISH", 0))); //审核金额

            //合计
            exportData.setData("HJ01", "TEXT",
                               df.format(result1.getDouble("HJF", 0))); //发生金额
            exportData.setData("HJ02", "TEXT",
                               df.format(result1.getDouble("HEZ", 0))); //自费
            exportData.setData("HJ03", "TEXT",
                               df.format(result1.getDouble("HEZENG", 0))); //增付
            exportData.setData("HJ04", "TEXT",
                               df.format(result1.getDouble("HESHEN", 0))); //申报金额
            exportData.setData("HJ05", "TEXT", " "); //拒付
            exportData.setData("HJ06", "TEXT",
                               df.format(result1.getDouble("HESH", 0))); //审核金额

            exportData.setData("GRSSQFBZ", "TEXT",
                               df.format(result1.getDouble("RESTART_STANDARD_AMT",
                    0))); //实收其支付标准金额
            exportData.setData("GRZF", "TEXT",
                               df.format(result1.getDouble("OWN_AMT", 0))); //自费金额
            exportData.setData("GRZFJE", "TEXT",
                               df.format(result1.getDouble("ADD_AMT", 0))); //增付金额
            exportData.setData("GRHJ", "TEXT",
                               df.format(result1.getDouble("GRRHJ", 0))); //合计

            exportData.setData("GRCQFBZZFBL", "TEXT",
                               df.format(result1.
                                         getDouble("STARTPAY_OWN_AMT", 0))); //超起付标准自负比例金额
            exportData.setData("GRYLJZZFBL", "TEXT",
                               df.format(result1.
                                         getDouble("PERCOPAYMENT_RATE_AMT", 0))); //医疗救助自负比例金额
            exportData.setData("GRZGXE", "TEXT",
                               df.format(result1.getDouble("INS_HIGHLIMIT_AMT",
                    0))); //最高限额以上金额
            exportData.setData("GRSJZFJE", "TEXT",
                               df.format(result1.getDouble("SJZFJE", 0))); //实际自负金额

            //医保基金
            exportData.setData("YBJJYLJG", "TEXT",
                               df.format(result1.getDouble("NHI_PAY", 0))); //医疗机构申请金额
            exportData.setData("YBJJYLJG2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.getDouble("NHI_PAY", 0))); //中文
            exportData.setData("YBJJSHBZHF", "TEXT",
                               df.format(result1.getDouble("NHI_PAY_REAL", 0))); //社保支付金额
            exportData.setData("YBJJSHBZHF2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.getDouble("NHI_PAY_REAL", 0))); //中文
            //城乡大病救助金额
            exportData.setData("CXDBJGSQ", "TEXT",
                    df.format(result1.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //医疗机构申请金额
            exportData.setData("CXDBJGSQ2", "TEXT",
                    StringUtil.getInstance().
                    numberToWord(result1.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //中文
            exportData.setData("CXDBJGZF", "TEXT",
                    df.format(result1.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //社保支付金额
            exportData.setData("CXDBJGZF2", "TEXT",
                    StringUtil.getInstance().
                    numberToWord(result1.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //中文
 
            //大额救助
            exportData.setData("DEJZYLJG", "TEXT",
                               df.format(result1.getDouble("NHI_COMMENT", 0))); //医疗机构申请金额
            exportData.setData("DEJZYLJG2", "TEXT",
            		           StringUtil.getInstance().
            		numberToWord(result1.getDouble("NHI_COMMENT", 0))); //中文
            exportData.setData("DEJZSHBJY", "TEXT",
                               df.format(result1.getDouble("NHI_COMMENT", 0))); //社保建议支付金额
            exportData.setData("DEJZSHBJY2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.
                                            getDouble("NHI_COMMENT", 0))); //中文
            //补助显示
            String  spPatType = result1.getValue("SPECIAL_PAT",0);
            double  agentAmt = result1.getDouble("ARMYAI_AMT",0);
            //公务员补助
            if("06".equals(spPatType))
            {
            exportData.setData("GWYYLJG", "TEXT",df.format(agentAmt)); //医疗机构申请金额
            exportData.setData("GWYYLJG2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //中文
            exportData.setData("GWYSHBZF", "TEXT",df.format(agentAmt)); //社保支付金额
            exportData.setData("GWYSHBZF2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //中文
            }
            if("04".equals(spPatType))
            {
            //军残补助
            exportData.setData("JCBZHYLJG", "TEXT",df.format(agentAmt)); //医疗机构申请金额
            exportData.setData("JCBZHYLJG2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //中文
            exportData.setData("JCBZHSHBZHF", "TEXT",df.format(agentAmt)); //社保支付金额
            exportData.setData("JCBZHSHBZHF2", "TEXT",StringUtil.getInstance().numberToWord(agentAmt)); //中文
            }
            if("07".equals(spPatType)||"08".equals(spPatType))
            {
            //民政补助-----------
            exportData.setData("MZHBZHYLJG", "TEXT", df.format(agentAmt)); //医疗机构申请金额      
            exportData.setData("MZHBZHYLJG2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //中文
            exportData.setData("MZHBZHSHBZHF", "TEXT", df.format(agentAmt)); //社保支付金额
            exportData.setData("MZHBZHSHBZHF2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //中文
            }
            if("09".equals(spPatType))
            {
            //非典补助------------
            exportData.setData("FDBZHYLJG", "TEXT", df.format(agentAmt)); //医疗机构申请金额
            exportData.setData("FDBZHYLJG2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //中文
            exportData.setData("FDBZHSHBZHF", "TEXT", df.format(agentAmt)); //社保支付金额
            exportData.setData("FDBZHSHBZHF2", "TEXT", StringUtil.getInstance().numberToWord(agentAmt)); //中文
            }
            //个人账户支付
            exportData.setData("GRZHHYLJG", "TEXT",
                               df.format(result1.getDouble("ACCOUNT_PAY_AMT", 0))); //医疗机构申请金额
            exportData.setData("GRZHHYLJG2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.
                                            getDouble("ACCOUNT_PAY_AMT", 0))); //中文
            exportData.setData("GRZHHSHBZHF", "TEXT",
                               df.format(result1.getDouble("GEZHI", 0))); //社保支付金额
            exportData.setData("GRZHHSHBZHF2", "TEXT",
                               StringUtil.getInstance().
                               numberToWord(result1.getDouble("GEZHI", 0))); //中文
            //TODO:人账户支付与合计支付相同，不合理。
            //caowl 20130321 总计的大小写对不上
            exportData.setData("HJZHFYLJG", "TEXT",
                               df.format(result1.getDouble("YLJGSHQHJ", 0))); //医疗机构申请金额
            exportData.setData("HJZHFYLJG2", "TEXT",
            		StringUtil.getInstance().
                    numberToWord(result1.getDouble("YLJGSHQHJ", 0))); //中文  //caowl 20130321 总计的大小写对不上
            exportData.setData("HJZHFSHBZHF", "TEXT",
                               df.format(result1.getDouble("SHBZHFJEHJ", 0))); //社保支付金额

            exportData.setData("HJZHFSHBZHF2", "TEXT",
            		 StringUtil.getInstance().
                     numberToWord(result1.getDouble("SHBZHFJEHJ", 0))); //中文  //caowl 20130321 总计的大小写对不上
            if(result1.getValue("XZ", 0).equals("城职"))
            this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_PRINT3.jhw",
                                 exportData);
            else if(result1.getValue("XZ", 0).equals("城乡")){
            	  //医疗救助
            double  nhicomment = result1.getDouble("NHI_COMMENT",0);           
            exportData.setData("DEJZYLJG", "TEXT",df.format(nhicomment+agentAmt)); //医疗机构申请金额
            exportData.setData("DEJZYLJG2", "TEXT",StringUtil.getInstance().
                		numberToWord(nhicomment+agentAmt)); //中文
            exportData.setData("DEJZSHBJY", "TEXT",df.format(nhicomment+agentAmt)); //社保建议支付金额
            exportData.setData("DEJZSHBJY2", "TEXT",StringUtil.getInstance().
                                   numberToWord(nhicomment+agentAmt)); //中文
            this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_PRINT3_DB.jhw",
                        exportData);
            }
        } 
        //单病种病人.
        else if ("DBZBR".equals(dBZ)) 
        {  
        	
            // 上部开始
            String sql = " SELECT " +
                         " case " +
//                         " when  substr(ctz_code,0,1) = '1' then '城职' " +
//                         " when  substr(ctz_code,0,1) = '2' then '城乡' " +
                         " when  Z.INS_CROWD_TYPE = '1' then '城职' " +
                         " when  Z.INS_CROWD_TYPE = '2' then '城乡' " +
                         " end as XZ," +
                         " SUBSTR(M.YEAR_MON,1,4)||'年'||SUBSTR(M.YEAR_MON,5,2)||'月' AS YDATE, B.CHN_DESC AS CBQX,M.DEPT_DESC, " +
                         " V.CHN_DESC AS JYLB ,"+
                         " M.IDNO , M.PAT_NAME , X.CHN_DESC AS SEX_DESC , M.PAT_AGE , Z.CTZ_DESC , " +
                         " M.UNIT_DESC , M.UNIT_CODE, " +
                         " U.CHN_DESC AS RYBS ,"+
                         " to_char(M.IN_DATE,'yyyymmdd') AS IN_DATE,to_char(M.DS_DATE,'yyyymmdd') AS DS_DATE,I.ADM_DAYS, M.MR_NO , " +
                         " M.BED_NO , " +
                         " '三级' AS JB, " +
                         //相当起付标准，病种编码，资格确认书号，住院次数
                         " M.START_STANDARD_AMT , A.SDISEASE_CODE ,M.CONFIRM_NO, M.INP_TIME , " +
                         //医疗救助支付比例
                         " M.PERCOPAYMENT_RATE_AMT , " + 
                         //转出医院名称
                         " M.TRANHOSP_DESC , " +
                         //转出医院级别
                         " case " +
                         " when  M.TRAN_CLASS = '01' then '一级' " +
                         " when  M.TRAN_CLASS = '02' then '二级' " +
                         " when  M.TRAN_CLASS = '03' then '三级' " +
                         " else '' " +
                         " end AS TRANS_HOSP_JB, " +
                         //转出医院起付标准
                         " M.TRANHOSP_RESTANDARD_AMT , " +
                         //出院诊断||转归别
                         " M.DIAG_CODE||M.DIAG_DESC||' '||" +
                         " (case " +
                         " when  M.TRAN_CLASS = '1' then '治愈'" +
                         " when  M.TRAN_CLASS = '2' then '好转'" +
                         " when  M.TRAN_CLASS = '3' then '未愈'" +
                         " when  M.TRAN_CLASS = '4' then '死亡'" +
                         " when  M.TRAN_CLASS = '5' then '其他'" +
                         " else ''" +
                         " end) AS OUT_DIAG," +
                         //家床诊断，病种付费标准，病种自负标准
                         " ' ' AS HOME_DIAG, M.SINGLE_STANDARD_AMT_T ,STARTPAY_OWN_AMT ," +
                         //上次统筹基金支付限额剩余额次，上次医疗救助支付限额剩余额
                         " M.BASEMED_BALANCE , M.INS_BALANCE , " +
                         //病种申报金额
                         "M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT - case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end-case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end AS DISEASE_NHI_AMT, " +
                         //病种拒付金额，医院超病种标准自负金额，基本医疗保险补足金额
                         "REFUSE_TOTAL_AMT, M.SINGLE_STANDARD_OWN_AMT ,  SINGLE_SUPPLYING_AMT,  " +
                         // 下部开始   
                         //实际起付标准金额，统筹基金自负标准金额
                         " '0.00' AS REAL_PAY_STANDER_AMT , M.RESTART_STANDARD_AMT, M.STARTPAY_OWN_AMT AS NHI_OWN_PAY , " + 
                         //医疗救助自负标准金额，特需自费金额，最高限额以上金额
                         " M.PERCOPAYMENT_RATE_AMT AS APPLY_OWN_AMT ,case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end+case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end AS TX_OWN_AMT, M.INS_HIGHLIMIT_AMT , " +
                         //实际自负
					     " case when M.STARTPAY_OWN_AMT is null then 0 else M.STARTPAY_OWN_AMT end "+
					     " + "+
					     " case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end "+
					     " + "+
					     " case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end "+
					     " + "+
					     " case when M.PERCOPAYMENT_RATE_AMT is null then 0 else M.PERCOPAYMENT_RATE_AMT end "+
					     " + "+
					     " case when M.INS_HIGHLIMIT_AMT is null then 0 else M.INS_HIGHLIMIT_AMT end "+
					     " - "+
					     " case when M.ACCOUNT_PAY_AMT is null then 0 else M.ACCOUNT_PAY_AMT end "+
					     " - "+
					     " case when M.ARMYAI_AMT is null then 0 else M.ARMYAI_AMT end  " +
					     " - "+
					     " case when M.ILLNESS_SUBSIDY_AMT is null then 0 else M.ILLNESS_SUBSIDY_AMT end AS REAL_TOTAL_OWN_AMT,"+
					     //个人自负合计   
                         " M.STARTPAY_OWN_AMT+case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end+case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end+M.PERCOPAYMENT_RATE_AMT+M.INS_HIGHLIMIT_AMT AS TOTAL_OWN_AMT," +
                         //统筹基金申请金额，大额救助申请金额，个人账户申请金额
                         " M.NHI_PAY ,M.HOSP_APPLY_AMT, M.ACCOUNT_PAY_AMT  ," + 
                         //统筹基金支付金额，大额救助支付金额
                         " M.NHI_PAY_REAL ,M.HOSP_APPLY_AMT," + 
                         // 中间部分，不包括拒付，审核金额
                         " M.PHA_AMT , M.PHA_OWN_AMT ,'0.00' AS PHA_OWN_AMT_S, M.PHA_ADD_AMT , M.PHA_NHI_AMT , '' AS PHA_SH_AMT, " +
                         " M.EXM_AMT , M.EXM_OWN_AMT ,'0.00' AS EXM_OWN_AMT_S, M.EXM_ADD_AMT , M.EXM_NHI_AMT , '' AS EXM_SH_AMT, " +
                         " M.TREAT_AMT , M.TREAT_OWN_AMT ,'0.00' AS TREAT_OWN_AMT_S, M.TREAT_ADD_AMT , M.TREAT_NHI_AMT , '' AS TREAT_SH_AMT , " +
                         " M.OP_AMT , M.OP_OWN_AMT , '0.00' AS OP_OWN_AMT_S,M.OP_ADD_AMT , M.OP_NHI_AMT , '' AS OP_SH_AMT, " +
                         " M.BED_AMT , M.BED_OWN_AMT ,case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end AS BED_OWN_AMT_S, M.BED_ADD_AMT , M.BED_NHI_AMT , '' AS BED_SH_AMT, " +
                         " M.MATERIAL_AMT , M.MATERIAL_OWN_AMT , case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end AS MATERIAL_OWN_AMT_S, M.MATERIAL_ADD_AMT , M.MATERIAL_NHI_AMT , '' AS MATERIAL_SH_AMT , " +
                         " M.BLOODALL_AMT , M.BLOODALL_OWN_AMT , '0.00' AS BLOODALL_OWN_AMT_S,M.BLOODALL_ADD_AMT , M.BLOODALL_NHI_AMT , '' AS BLOODALL_SH_AMT, " +
                         " M.BLOOD_AMT , M.BLOOD_OWN_AMT ,'0.00' AS BLOOD_OWN_AMT_S, M.BLOOD_ADD_AMT, M.BLOOD_NHI_AMT , '' AS BLOOD_SH_AMT , " +
                         " M.OTHER_AMT , M.OTHER_OWN_AMT , '0.00' AS OTHER_OWN_AMT_S,M.OTHER_ADD_AMT , M.OTHER_NHI_AMT , '' AS OTHER_SH_AMT, " +
                         // “其中：材料”项目不知道写什么
                         " '' AS YCXCL_AMT, '' AS YCXCL_OWN_AMT, '' AS  YCXCL_OWN_AMT_S, '' AS YCXCL_ADD_AMT, '' AS YCXCL_NHI_AMT, '' AS YCXCL_SH_AMT, " +
                         " M.PHA_AMT+M.EXM_AMT+M.TREAT_AMT+M.OP_AMT+M.BED_AMT+M.MATERIAL_AMT+M.BLOODALL_AMT+M.BLOOD_AMT+M.OTHER_AMT AS TOT_AMT, " +
                         " M.PHA_OWN_AMT+M.EXM_OWN_AMT+M.TREAT_OWN_AMT+M.OP_OWN_AMT+M.BED_OWN_AMT+M.MATERIAL_OWN_AMT+M.BLOODALL_OWN_AMT+M.BLOOD_OWN_AMT+M.OTHER_OWN_AMT AS TOT_OWN_AMT, " +
                         " case when M.MATERIAL_SINGLE_AMT is null then 0 else M.MATERIAL_SINGLE_AMT end+case when M.BED_SINGLE_AMT is null then 0 else M.BED_SINGLE_AMT end AS TOT_OWN_AMT_S, " +
                         " M.PHA_ADD_AMT+M.EXM_ADD_AMT+M.TREAT_ADD_AMT+M.OP_ADD_AMT+M.BED_ADD_AMT+M.MATERIAL_ADD_AMT+M.BLOODALL_ADD_AMT+M.BLOOD_ADD_AMT+M.OTHER_ADD_AMT AS TOT_ADD_AMT, " +
                         " M.PHA_NHI_AMT+M.EXM_NHI_AMT+M.TREAT_NHI_AMT+M.OP_NHI_AMT+M.BED_NHI_AMT+M.MATERIAL_NHI_AMT+M.BLOODALL_NHI_AMT+M.BLOOD_NHI_AMT+M.OTHER_NHI_AMT AS TOT_NHI_AMT, " +
                         " '' AS TOT_SH_AMT, " +
                         
                         " M.REGION_CODE , M.ADM_SEQ , M.CONFIRM_NO, A.SPECIAL_PAT, M.ARMYAI_AMT,O.USER_NAME,M.IN_DATE AS IN_DATETEMP ,M.NHI_COMMENT," +
                         " M.DS_DATE AS DS_DATETEMP,case when M.ILLNESS_SUBSIDY_AMT is null then 0 else M.ILLNESS_SUBSIDY_AMT end AS ILLNESS_SUBSIDY_AMT," +
                         " case when A.OUTP_CARRYOVER_AMT is null then 0 else A.OUTP_CARRYOVER_AMT end AS OUTP_CARRYOVER_AMT " +
                         " FROM INS_IBS M " +
                         " left JOIN INS_ADM_CONFIRM A " +
                         " ON M.CONFIRM_NO = A.CONFIRM_NO " +
                         " left JOIN ADM_INP I " +
                         " ON M.REGION_CODE = I.REGION_CODE " +
                         " AND M.MR_NO = I.MR_NO " +
                         " AND M.CASE_NO = I.CASE_NO " +
                         " left JOIN SYS_OPERATOR O " +
                         " ON I.REGION_CODE = O.REGION_CODE " +
                         " AND I.VS_DR_CODE = O.USER_ID " +
                         " left JOIN SYS_DICTIONARY X " +
                         " ON M.SEX_CODE = X.ID AND X.GROUP_ID='SYS_SEX' " +
                         " INNER JOIN SYS_CTZ Z " +
                         " ON A.HIS_CTZ_CODE = Z.CTZ_CODE " +//modify by lim 2012/04/26
                         " left JOIN SYS_DICTIONARY B " +
                         " ON M.INSBRANCH_CODE = B.ID AND B.GROUP_ID='INS_FZX' " +
                         " left JOIN INS_MT_DISEASE T " +
                         " ON M.SPEDRS_CODE = T.MT_DISEASE_CODE " +
                         " left JOIN SYS_DICTIONARY K "+
                         " ON K.GROUP_ID='SIN_DISEASE' AND A.SDISEASE_CODE = K.ID "+
                         " left JOIN SYS_DICTIONARY U "+
                         " ON U.GROUP_ID='SP_PRESON_TYPE' AND  U.ID = A.SPECIAL_PAT "+
                         " left JOIN SYS_DICTIONARY V "+
                         " ON V.GROUP_ID = 'INS_JYLB' AND V.ID = M.ADM_CATEGORY "+
                         " WHERE M.MR_NO = '" + mrNo + "' AND M.ADM_SEQ='"+admSeq+"'" ;
//                         " WHERE M.MR_NO = '" + mrNo + "' " +
//                         " AND TO_CHAR(M.DS_DATE,'yyyy-mm-dd') = '" +
//                         dsDateArray[0] + "' ";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//           System.out.println("单病种sql："+sql);
            //System.out.println("result1:"+result);
            if (result.getCount() <= 0) {
                messageBox("没有打印数据!");
                return;
            }
            TParm exportData  =  new TParm();
            DecimalFormat df = new DecimalFormat("##########0.00");
            //表号
            exportData.setData("BH", "TEXT", "津社保医支字2-3号"); 
            //医疗机构名称
            exportData.setData("YLJGMCH", "TEXT", "天津市泰达国际心血管病医院");
            //就医类别
            exportData.setData("JYLB", "TEXT", result.getValue("JYLB", 0));   
            //日期
            exportData.setData("YDATE", "TEXT", result.getValue("YDATE", 0)); 
            //单位
            exportData.setData("DW", "TEXT", "元"); 
            //险种
            exportData.setData("XZ", "TEXT", result.getValue("XZ", 0)); 
            //公民身份证号码
            exportData.setData("IDNO", "TEXT", result.getValue("IDNO", 0)); 
            //姓名
            exportData.setData("PAT_NAME", "TEXT", result.getValue("PAT_NAME", 0)); 
            //性别
            exportData.setData("SEX_DESC", "TEXT", result.getValue("SEX_DESC", 0)); 
            //年龄
            exportData.setData("PAT_AGE", "TEXT", result.getValue("PAT_AGE", 0)); 
            //人员类别
            exportData.setData("CTZ_DESC", "TEXT", result.getValue("CTZ_DESC", 0)); 
            //单位名称
            exportData.setData("UNIT_DESC", "TEXT", result.getValue("UNIT_DESC", 0)); 
            //单位代码
            exportData.setData("UNIT_CODE", "TEXT", result.getValue("UNIT_CODE", 0)); 
            //人员标识
            exportData.setData("RYBS", "TEXT", result.getValue("RYBS", 0)); 
            //参保区县
            exportData.setData("CBQX", "TEXT", result.getValue("CBQX", 0)); 
            //住院日期
            String inDate = result.getValue("IN_DATE", 0);
            String outDate = result.getValue("DS_DATE", 0);
            Timestamp outDatetemp=result.getTimestamp("DS_DATETEMP", 0);
            Timestamp inDatetemp=result.getTimestamp("IN_DATETEMP", 0);
            Timestamp date1 = StringTool.getTimestamp(
            		dfdate.format(outDatetemp)+" 00:00:00", "yyyy-MM-dd HH:mm:ss"); 
            Timestamp date2 = StringTool.getTimestamp(
            		dfdate.format(inDatetemp)+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
            if(inDate!=null && !"".equals(inDate))
                exportData.setData("IN_DATE", "TEXT",
                		(inDate.substring(0, 4) + "年" +
                         inDate.substring(4, 6) + "月" +
                         inDate.substring(6, 8) + "日"));
            else
            	exportData.setData("IN_DATE", "TEXT","");
            //出院日期
            if(outDate!=null && !"".equals(outDate))
                exportData.setData("DS_DATE", "TEXT",
                        (outDate.substring(0, 4) + "年" +
                         outDate.substring(4, 6) + "月" +
                         outDate.substring(6, 8) + "日")); 
            else
            	exportData.setData("DS_DATE", "TEXT","");
            int rollDate =0;
            if(inDate.equals(outDate))
            	rollDate =1;
            else
                rollDate = StringTool.getDateDiffer(date1, date2);
            //住院天数
            exportData.setData("ADM_DAYS", "TEXT", rollDate); 
            //住院号
            exportData.setData("MR_NO", "TEXT", result.getValue("MR_NO", 0));
            //床位号
            exportData.setData("BED_NO", "TEXT", result.getValue("BED_NO", 0)); 
            //级别
            exportData.setData("JB", "TEXT", result.getValue("JB", 0)); 
            //科别
            exportData.setData("DEPT_DESC", "TEXT", result.getValue("DEPT_DESC", 0)); 
            //相当起付标准
            exportData.setData("START_STANDARD_AMT", "TEXT",result.getValue("START_STANDARD_AMT", 0));
            //病种编码
            exportData.setData("SDISEASE_CODE", "TEXT",result.getValue("SDISEASE_CODE", 0));
            //资格确认书号
            exportData.setData("CONFIRM_NO", "TEXT",result.getValue("CONFIRM_NO", 0));
            //第几次住院
            exportData.setData("INP_TIME", "TEXT", result.getValue("INP_TIME", 0));  
            //医疗救助支付比例
            exportData.setData("PERCOPAYMENT_RATE_AMT", "TEXT", result.getValue("PERCOPAYMENT_RATE_AMT", 0));
            //转出院名称
            exportData.setData("TRANHOSP_DESC", "TEXT",result.getValue("TRANHOSP_DESC", 0));
            //转出院级别
            exportData.setData("TRANS_HOSP_JB", "TEXT", result.getValue("TRANS_HOSP_JB", 0)); 
            //转出院实收起付标准
            exportData.setData("TRANHOSP_RESTANDARD_AMT", "TEXT",result.getValue("TRANHOSP_RESTANDARD_AMT", 0));
            //出院诊断
            exportData.setData("OUT_DIAG", "TEXT", result.getValue("OUT_DIAG", 0));
            //家床诊断
            exportData.setData("HOME_DIAG", "TEXT", result.getValue("HOME_DIAG", 0)); 
            //病种付费标准
            exportData.setData("SINGLE_STANDARD_AMT_T", "TEXT", df.format(result.getDouble("SINGLE_STANDARD_AMT_T", 0)));
            //病种付费标准
            exportData.setData("STARTPAY_OWN_AMT", "TEXT", df.format(result.getDouble("STARTPAY_OWN_AMT", 0)));
            //上次医保基金支付限额剩余额
            exportData.setData("BASEMED_BALANCE", "TEXT",df.format(result.getDouble("BASEMED_BALANCE", 0)));
            //上次医疗救助支付限额剩余额
            exportData.setData("INS_BALANCE", "TEXT",df.format(result.getDouble("INS_BALANCE", 0)));
            //门诊跨年度结转
            exportData.setData("MZKNDJY", "TEXT",df.format(result.getDouble("OUTP_CARRYOVER_AMT", 0)));
            //药品费
            exportData.setData("PHA_AMT", "TEXT",df.format(result.getDouble("PHA_AMT", 0))); 
            exportData.setData("PHA_OWN_AMT", "TEXT",df.format(result.getDouble("PHA_OWN_AMT", 0)));
            exportData.setData("PHA_OWN_AMT_S", "TEXT",df.format(result.getDouble("PHA_OWN_AMT_S", 0)));
            exportData.setData("PHA_ADD_AMT", "TEXT",df.format(result.getDouble("PHA_ADD_AMT", 0)));
            exportData.setData("PHA_NHI_AMT", "TEXT",df.format(result.getDouble("PHA_NHI_AMT", 0))); 
            exportData.setData("PHA_REF_AMT", "TEXT","0.00"); 
            exportData.setData("PHA_SH_AMT", "TEXT","0.00"); 
            //检查费
            exportData.setData("EXM_AMT", "TEXT",df.format(result.getDouble("EXM_AMT", 0))); 
            exportData.setData("EXM_OWN_AMT", "TEXT",df.format(result.getDouble("EXM_OWN_AMT", 0)));
            exportData.setData("EXM_OWN_AMT_S", "TEXT",df.format(result.getDouble("EXM_OWN_AMT_S", 0)));
            exportData.setData("EXM_ADD_AMT", "TEXT",df.format(result.getDouble("EXM_ADD_AMT", 0)));
            exportData.setData("EXM_NHI_AMT", "TEXT",df.format(result.getDouble("EXM_NHI_AMT", 0))); 
            exportData.setData("EXM_REF_AMT", "TEXT","0.00"); 
            exportData.setData("EXM_SH_AMT", "TEXT","0.00"); 
            //治疗费
            exportData.setData("TREAT_AMT", "TEXT",df.format(result.getDouble("TREAT_AMT", 0))); 
            exportData.setData("TREAT_OWN_AMT", "TEXT",df.format(result.getDouble("TREAT_OWN_AMT", 0)));
            exportData.setData("TREAT_OWN_AMT_S", "TEXT",df.format(result.getDouble("TREAT_OWN_AMT_S", 0)));
            exportData.setData("TREAT_ADD_AMT", "TEXT",df.format(result.getDouble("TREAT_ADD_AMT", 0)));
            exportData.setData("TREAT_NHI_AMT", "TEXT",df.format(result.getDouble("TREAT_NHI_AMT", 0))); 
            exportData.setData("TREAT_REF_AMT", "TEXT","0.00"); 
            exportData.setData("TREAT_SH_AMT", "TEXT","0.00");
            //手术费
            exportData.setData("OP_AMT", "TEXT",df.format(result.getDouble("OP_AMT", 0))); 
            exportData.setData("OP_OWN_AMT", "TEXT",df.format(result.getDouble("OP_OWN_AMT", 0)));
            exportData.setData("OP_OWN_AMT_S", "TEXT",df.format(result.getDouble("OP_OWN_AMT_S", 0)));
            exportData.setData("OP_ADD_AMT", "TEXT",df.format(result.getDouble("OP_ADD_AMT", 0)));
            exportData.setData("OP_NHI_AMT", "TEXT",df.format(result.getDouble("OP_NHI_AMT", 0))); 
            exportData.setData("OP_REF_AMT", "TEXT","0.00"); 
            exportData.setData("OP_SH_AMT", "TEXT","0.00");
            //床位费
            exportData.setData("BED_AMT", "TEXT",df.format(result.getDouble("BED_AMT", 0))); 
            exportData.setData("BED_OWN_AMT", "TEXT",df.format(result.getDouble("BED_OWN_AMT", 0)));
            exportData.setData("BED_OWN_AMT_S", "TEXT",df.format(result.getDouble("BED_OWN_AMT_S", 0)));
            exportData.setData("BED_ADD_AMT", "TEXT",df.format(result.getDouble("BED_ADD_AMT", 0)));
            exportData.setData("BED_NHI_AMT", "TEXT",df.format(result.getDouble("BED_NHI_AMT", 0))); 
            exportData.setData("BED_REF_AMT", "TEXT","0.00"); 
            exportData.setData("BED_SH_AMT", "TEXT","0.00");            
            //医用材料
            exportData.setData("MATERIAL_AMT", "TEXT",df.format(result.getDouble("MATERIAL_AMT", 0))); 
            exportData.setData("MATERIAL_OWN_AMT", "TEXT",df.format(result.getDouble("MATERIAL_OWN_AMT", 0)));
            exportData.setData("MATERIAL_OWN_AMT_S", "TEXT",df.format(result.getDouble("MATERIAL_OWN_AMT_S", 0)));
            exportData.setData("MATERIAL_ADD_AMT", "TEXT",df.format(result.getDouble("MATERIAL_ADD_AMT", 0)));
            exportData.setData("MATERIAL_NHI_AMT", "TEXT",df.format(result.getDouble("MATERIAL_NHI_AMT", 0))); 
            exportData.setData("MATERIAL_REF_AMT", "TEXT","0.00"); 
            exportData.setData("MATERIAL_SH_AMT", "TEXT","0.00");               
            //输全血
            exportData.setData("BLOODALL_AMT", "TEXT",df.format(result.getDouble("BLOODALL_AMT", 0))); 
            exportData.setData("BLOODALL_OWN_AMT", "TEXT",df.format(result.getDouble("BLOODALL_OWN_AMT", 0)));
            exportData.setData("BLOODALL_OWN_AMT_S", "TEXT",df.format(result.getDouble("BLOODALL_OWN_AMT_S", 0)));
            exportData.setData("BLOODALL_ADD_AMT", "TEXT",df.format(result.getDouble("BLOODALL_ADD_AMT", 0)));
            exportData.setData("BLOODALL_NHI_AMT", "TEXT",df.format(result.getDouble("BLOODALL_NHI_AMT", 0))); 
            exportData.setData("BLOODALL_REF_AMT", "TEXT","0.00"); 
            exportData.setData("BLOODALL_SH_AMT", "TEXT","0.00"); 
            //成分输血
            exportData.setData("BLOOD_AMT", "TEXT",df.format(result.getDouble("BLOOD_AMT", 0))); 
            exportData.setData("BLOOD_OWN_AMT", "TEXT",df.format(result.getDouble("BLOOD_OWN_AMT", 0)));
            exportData.setData("BLOOD_OWN_AMT_S", "TEXT",df.format(result.getDouble("BLOOD_OWN_AMT_S", 0)));
            exportData.setData("BLOOD_ADD_AMT", "TEXT",df.format(result.getDouble("BLOOD_ADD_AMT", 0)));
            exportData.setData("BLOOD_NHI_AMT", "TEXT",df.format(result.getDouble("BLOOD_NHI_AMT", 0))); 
            exportData.setData("BLOOD_REF_AMT", "TEXT","0.00"); 
            exportData.setData("BLOOD_SH_AMT", "TEXT","0.00");            
            //其他
            exportData.setData("OTHER_AMT", "TEXT",df.format(result.getDouble("OTHER_AMT", 0))); 
            exportData.setData("OTHER_OWN_AMT", "TEXT",df.format(result.getDouble("OTHER_OWN_AMT", 0)));
            exportData.setData("OTHER_OWN_AMT_S", "TEXT",df.format(result.getDouble("OTHER_OWN_AMT_S", 0)));
            exportData.setData("OTHER_ADD_AMT", "TEXT",df.format(result.getDouble("OTHER_ADD_AMT", 0)));
            exportData.setData("OTHER_NHI_AMT", "TEXT",df.format(result.getDouble("OTHER_NHI_AMT", 0))); 
            exportData.setData("OTHER_REF_AMT", "TEXT","0.00"); 
            exportData.setData("OTHER_SH_AMT", "TEXT","0.00");     
            //其中一次性材料
            exportData.setData("YCXCL_AMT", "TEXT","0.00"); 
            exportData.setData("YCXCL_OWN_AMT", "TEXT","0.00");
            exportData.setData("YCXCL_OWN_AMT_S", "TEXT","0.00");
            exportData.setData("YCXCL_ADD_AMT", "TEXT","0.00");
            exportData.setData("YCXCL_NHI_AMT", "TEXT","0.00"); 
            exportData.setData("YCXCL_REF_AMT", "TEXT","0.00"); 
            exportData.setData("YCXCL_SH_AMT", "TEXT","0.00");  
            //合计
            exportData.setData("TOT_AMT", "TEXT",df.format(result.getDouble("TOT_AMT", 0))); 
            exportData.setData("TOT_OWN_AMT", "TEXT",df.format(result.getDouble("TOT_OWN_AMT", 0)));
            exportData.setData("TOT_OWN_AMT_S", "TEXT",df.format(result.getDouble("TOT_OWN_AMT_S", 0)));
            exportData.setData("TOT_ADD_AMT", "TEXT",df.format(result.getDouble("TOT_ADD_AMT", 0)));
            exportData.setData("TOT_NHI_AMT", "TEXT",df.format(result.getDouble("TOT_NHI_AMT", 0))); 
            exportData.setData("TOT_REF_AMT", "TEXT","0.00"); 
            exportData.setData("TOT_SH_AMT", "TEXT","0.00");  
            //病种自负金额
            exportData.setData("DISEASE_NHI_AMT", "TEXT",df.format(result.getDouble("DISEASE_NHI_AMT", 0)));
            //病种拒付金额
            exportData.setData("REFUSE_TOTAL_AMT", "TEXT",df.format(result.getDouble("REFUSE_TOTAL_AMT", 0)));
            //医院超病种起付标准自负金额
            exportData.setData("SINGLE_STANDARD_OWN_AMT", "TEXT",df.format(result.getDouble("SINGLE_STANDARD_OWN_AMT", 0)));
            //基本医疗保险补足金额
            exportData.setData("SINGLE_SUPPLYING_AMT", "TEXT",df.format(result.getDouble("SINGLE_SUPPLYING_AMT", 0)));
            
            if (Integer.parseInt(inDate) < Integer.parseInt("20170101"))
            	 //实际起付标准金额
                exportData.setData("REAL_PAY_STANDER_AMT", "TEXT",df.format(result.getDouble("REAL_PAY_STANDER_AMT", 0)));
            else
                //实际起付标准金额
                exportData.setData("REAL_PAY_STANDER_AMT", "TEXT",df.format(result.getDouble("RESTART_STANDARD_AMT", 0)));
            //统筹基金自负标准金额
            exportData.setData("NHI_OWN_PAY", "TEXT",df.format(result.getDouble("NHI_OWN_PAY", 0)));
            //医疗救助自负标准金额
            exportData.setData("APPLY_OWN_AMT", "TEXT",df.format(result.getDouble("APPLY_OWN_AMT", 0)));
            //特需自费金额
            exportData.setData("TX_OWN_AMT", "TEXT",df.format(result.getDouble("TX_OWN_AMT", 0)));
            //最高限额以上金额
            exportData.setData("INS_HIGHLIMIT_AMT", "TEXT",df.format(result.getDouble("INS_HIGHLIMIT_AMT", 0)));
           
            if (Integer.parseInt(inDate) < Integer.parseInt("20170101")){
                //个人自负合计
                exportData.setData("TOTAL_OWN_AMT", "TEXT",df.format(result.getDouble("TOTAL_OWN_AMT", 0)));
                //实际自负金额
                exportData.setData("REAL_TOTAL_OWN_AMT", "TEXT",df.format(result.getDouble("REAL_TOTAL_OWN_AMT", 0)));
            }
            else{
            	//个人自负合计
                exportData.setData("TOTAL_OWN_AMT", "TEXT",
                		df.format(result.getDouble("TOTAL_OWN_AMT", 0)+result.getDouble("RESTART_STANDARD_AMT", 0)));
                //实际自负金额
                exportData.setData("REAL_TOTAL_OWN_AMT", "TEXT",
                		df.format(result.getDouble("REAL_TOTAL_OWN_AMT", 0)+result.getDouble("RESTART_STANDARD_AMT", 0)));	
            }
            //统筹基金申请，支付，大写
            exportData.setData("NHI_PAY", "TEXT",df.format(result.getDouble("NHI_PAY", 0)));
            exportData.setData("NHI_PAY_REAL", "TEXT",df.format(result.getDouble("NHI_PAY", 0)));
            exportData.setData("NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("NHI_PAY", 0)));
            exportData.setData("NHI_PAY_REAL_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("NHI_PAY", 0)));
            //城乡大病救助金额
            exportData.setData("CXDBJGSQ", "TEXT",
                    df.format(result.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //医疗机构申请金额
            exportData.setData("CXDBJGSQ2", "TEXT",
                    StringUtil.getInstance().
                    numberToWord(result.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //中文
            exportData.setData("CXDBJGZF", "TEXT",
                    df.format(result.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //社保支付金额
            exportData.setData("CXDBJGZF2", "TEXT",
                    StringUtil.getInstance().
                    numberToWord(result.getDouble("ILLNESS_SUBSIDY_AMT", 0))); //中文
            //大额救助申请，支付，大写
            exportData.setData("HOSP_APPLY_AMT", "TEXT",df.format(result.getDouble("NHI_COMMENT", 0)));
            exportData.setData("APPLY_PAY_REAL", "TEXT",df.format(result.getDouble("NHI_COMMENT", 0)));
            exportData.setData("APPLY_AMT_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("NHI_COMMENT", 0)));
            exportData.setData("APPLY_PAY_REAL_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("NHI_COMMENT", 0)));
            //个人账户申请，支付，大写
            exportData.setData("ACCOUNT_NHI_PAY", "TEXT",df.format(result.getDouble("ACCOUNT_PAY_AMT", 0)));
            exportData.setData("ACCOUNT_REAL_PAY", "TEXT",df.format(result.getDouble("ACCOUNT_PAY_AMT", 0)));
            exportData.setData("ACCOUNT_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("ACCOUNT_PAY_AMT", 0)));
            exportData.setData("ACCOUNT_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(result.getDouble("ACCOUNT_PAY_AMT", 0)));           
            //补助金额处理
            String  spPatType = result.getValue("SPECIAL_PAT",0);
            double  agentAmt = result.getDouble("ARMYAI_AMT",0);
            //伤残军人
            if("04".equals(spPatType))
            {
              exportData.setData("JC_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("JC_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("JC_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("JC_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
            }
            //公务员补助
            if("06".equals(spPatType))
            {
              exportData.setData("GWY_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("GWY_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("GWY_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("GWY_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              
            }
            //民政补助 或 优抚补助
            if("07".equals(spPatType)||"08".equals(spPatType))
            {
              exportData.setData("MZ_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("MZ_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("MZ_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("MZ_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
            }
            //非典补助
            if("09".equals(spPatType))
            {
              exportData.setData("FD_NHI_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("FD_REAL_PAY", "TEXT",df.format(agentAmt));
              exportData.setData("FD_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
              exportData.setData("FD_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(agentAmt));
            }            
            //合计
            double sumNhiPay = 0;
            sumNhiPay  = result.getDouble("NHI_PAY", 0) +
                         result.getDouble("NHI_COMMENT", 0)+
                         result.getDouble("ACCOUNT_PAY_AMT", 0)+
                         agentAmt+result.getDouble("ILLNESS_SUBSIDY_AMT", 0);
            exportData.setData("TOTAL_NHI_PAY", "TEXT",df.format(sumNhiPay));
            exportData.setData("TOTAL_REAL_PAY", "TEXT",df.format(sumNhiPay));
            exportData.setData("TOTAL_NHI_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(sumNhiPay));
            exportData.setData("TOTAL_REAL_PAY_DX", "TEXT",StringUtil.getInstance().numberToWord(sumNhiPay));
            if(result.getValue("XZ", 0).equals("城职"))
            openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_NO2_SINGLE.jhw",exportData);
            else if(result.getValue("XZ", 0).equals("城乡")){
           	  //医疗救助
            double  hospapplyamt = result.getDouble("NHI_COMMENT",0);    	
            exportData.setData("HOSP_APPLY_AMT", "TEXT",df.format(hospapplyamt+agentAmt));
            exportData.setData("APPLY_PAY_REAL", "TEXT",df.format(hospapplyamt+agentAmt));
            exportData.setData("APPLY_AMT_DX", "TEXT",StringUtil.getInstance().numberToWord(hospapplyamt+agentAmt));
            exportData.setData("APPLY_PAY_REAL_DX", "TEXT",StringUtil.getInstance().numberToWord(hospapplyamt+agentAmt));	
            openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_NO2_SINGLE_DB.jhw",exportData);
            }
        }
       
    }
    /**
     * 异地结算单
     */
    public void onPrintNoLocal() {
    	 if (this.brlbTableSelectedRow == -1) {
             messageBox("没有选中患者.");
             return;
         }
         TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
         TParm parm = brlb.getParmValue();
         //就诊顺序号
         String admSeq = parm.getValue("ADM_SEQ",this.brlbTableSelectedRow);
         TParm result = new TParm();
         TParm Parm = new TParm();
        Parm.setData("PIPELINE", "DataDown_yjks");
        Parm.setData("PLOT_TYPE", "A1");
        Parm.addData("ADM_SEQ",admSeq);//就诊顺序号
        Parm.addData("HOSP_NHI_NO", this.nhi_no);//医院代码
        Parm.addData("PARM_COUNT", 2);//入参数量
        result = InsManager.getInstance().safe(Parm);
//        System.out.println("result============" + result);
      if (result.getErrCode() < 0) {
          this.messageBox(result.getErrText());
          return;
      }
      TParm Data = new TParm();
      DecimalFormat df = new DecimalFormat("##########0.00");
      Data.setData("PAT_NAME", "TEXT", result.getValue("PAT_NAME"));//患者姓名
      Data.setData("SEX_CODE", "TEXT", result.getValue("SEX_CODE"));//性别
      Data.setData("AGE", "TEXT", result.getValue("AGE"));//年龄
      Data.setData("ID_NO", "TEXT", result.getValue("ID_NO"));//社会保障号码
      Data.setData("CARD_NO", "TEXT", result.getValue("CARD_NO"));//社会保障卡卡号
      Data.setData("INSURED_PALCE", "TEXT", result.getValue("INSURED_PALCE"));//参保地
      Data.setData("INS_TYPE", "TEXT", result.getValue("INS_TYPE"));//险种类型
      Data.setData("MEDICAL_PLACE", "TEXT", result.getValue("MEDICAL_PLACE"));//就医地
      Data.setData("HOSP_NHI_DESC", "TEXT", result.getValue("HOSP_NHI_DESC"));//医院名称
      Data.setData("HOSP_NHI_LEVEL", "TEXT", result.getValue("HOSP_NHI_LEVEL"));//医院等级
      Data.setData("HOSP_IN_WAY", "TEXT", result.getValue("HOSP_IN_WAY"));//入院方式
      Data.setData("IPD_NO", "TEXT", result.getValue("IPD_NO"));//住院号
      Data.setData("DEPT_OUT", "TEXT", result.getValue("DEPT_OUT"));//出院科室
      Data.setData("ICD_MAIN_DESC", "TEXT", result.getValue("ICD_MAIN_DESC"));//主要诊断
      Data.setData("ICD_SEC_DESC", "TEXT", result.getValue("ICD_SEC_DESC"));//次要诊断
      Data.setData("IN_DATE", "TEXT", result.getValue("IN_DATE"));//入院日期
      Data.setData("OUT_DATE", "TEXT", result.getValue("OUT_DATE"));//出院日期
      Data.setData("HOSP_OUT_DAYS", "TEXT", result.getValue("HOSP_OUT_DAYS"));//出院天数
      Data.setData("TOT_AMT", "TEXT", df.format(result.getDouble("TOT_AMT")));//总费用 
      Data.setData("NHI_AMT", "TEXT", df.format(result.getDouble("NHI_AMT")));//统筹内费用
      Data.setData("OWN_AMT", "TEXT", df.format(result.getDouble("OWN_AMT")));//自费费用
      Data.setData("START_STANDARD_AMT", "TEXT",result.getValue("START_STANDARD_AMT"));//本次起付标准
      
      String nhipay =result.getValue("NHI_PAY");
//      System.out.println("result============" + result);
	   String nhipaydesc = "";
	   String nhipayamt = "";
	   String[] Array = new String[0];
		if (nhipay.length() > 0) {
			Array = nhipay.split("\\@");
		}
		int count = Array.length;
//		System.out.println("count==========" + count);
		for (int i = 0; i < count; i++) {			
			nhipaydesc= "NHI_PAY_DESC"+""+(i+1);					
			nhipayamt = "NHI_PAY_AMT"+""+(i+1);
			Data.setData(nhipaydesc,"TEXT",Array[i].split("\\,")[1]); 	
			Data.setData(nhipayamt,"TEXT",df.format(Double.valueOf(Array[i].split("\\,")[2])));			
		}          
      Data.setData("CASH_PAY", "TEXT", df.format(result.getDouble("CASH_PAY")));//个人现金支付
      openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_NO2_NOLOCAL_Print.jhw",Data);
    }
    /**
     * 清空
     */
    public void onClear() {
        this.clearQueryCondition();
        this.clearAttr();
        this.clearFirst();
        this.clearSecond();
        this.clearThird();
        this.clearFourth();
        tablePanel.setSelectedIndex(0);
        //===zhangp 20120417 start
        clearValue("MR_NO;PAT_NAME");
        //===zhangp 20120417 end
        Timestamp date = SystemTool.getInstance().getDate() ;
        this.setValue("UPLOAD_START_DATE", date);
        this.setValue("UPLOAD_END_DATE", date);
        this.getRadioButton("IN_TYPE").setSelected(true);
        onExeEnable(true);
    }

    public void onChangedPanel() {
        int index = ((TTabbedPane) getComponent("TablePanel")).getSelectedIndex();
        this.setCurrentTabPage(index);

        //检核用户是否选中一条病人信息.
        if (index != 0 && this.getBrlbTableSelectedRow() == -1) {
            this.tablePanel.setSelectedIndex(0);
            messageBox("请选择一条病人信息.");
            return;
        }

        if (index == 1) { //费用结算信息
            String sql = "SELECT CONFIRM_NO, YEAR_MON, ADM_PRJ, IDNO, SEX_CODE, PAT_AGE, CTZ1_CODE,UNIT_CODE, ADM_CATEGORY, " +
                         "RECEIPT_USER, UNIT_DESC,TO_CHAR (DS_DATE, 'YYYYMMDD') AS DS_DATE, UPLOAD_FLG, START_STANDARD_AMT,SPEDRS_CODE, " +
                         "INS_UNIT, DEPT_DESC, STATION_DESC, BED_NO, HOSP_CLS_CODE,INP_TIME, HOMEBED_TIME, HOMEBED_DAYS, TRANHOSP_RESTANDARD_AMT," +
                         "TRANHOSP_DESC, TRAN_CLASS, DIAG_CODE, DIAG_DESC, SOURCE_CODE," +
                         "HOMEDIAG_DESC, CHEMICAL_DESC, OWN_RATE, DECREASE_RATE, REALOWN_RATE," +
                         "PHA_AMT, PHA_OWN_AMT, PHA_ADD_AMT, PHA_NHI_AMT, EXM_AMT, EXM_OWN_AMT," +
                         "EXM_ADD_AMT, EXM_NHI_AMT, TREAT_AMT, TREAT_OWN_AMT, TREAT_ADD_AMT," +
                         "TREAT_NHI_AMT, OP_AMT, OP_OWN_AMT, OP_ADD_AMT, OP_NHI_AMT, BED_AMT," +
                         "BED_OWN_AMT, BED_ADD_AMT, BED_NHI_AMT, MATERIAL_AMT, MATERIAL_OWN_AMT," +
                         "MATERIAL_ADD_AMT, MATERIAL_NHI_AMT, BLOODALL_AMT, BLOODALL_OWN_AMT," +
                         "BLOODALL_ADD_AMT, BLOODALL_NHI_AMT, BLOOD_AMT, BLOOD_OWN_AMT," +
                         "BLOOD_ADD_AMT, BLOOD_NHI_AMT, OTHER_AMT, OTHER_OWN_AMT, OTHER_ADD_AMT," +
                         "OTHER_NHI_AMT, RESTART_STANDARD_AMT, STARTPAY_OWN_AMT, OWN_AMT," +
                         "PERCOPAYMENT_RATE_AMT, ADD_AMT, INS_HIGHLIMIT_AMT, TRANBLOOD_OWN_AMT," +
                         "NHI_PAY,APPLY_AMT,HOSP_APPLY_AMT, NHI_COMMENT, TO_CHAR (IN_DATE, 'YYYYMMDD'), HOSP_NHI_NO," +
                         "ADM_SEQ, MR_NO, CASE_NO " +
                         "FROM INS_IBS " +
                         "WHERE ADM_SEQ = '" + this.getAdmSeq() + "'";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            if (result.getCount() <= 0) {
                messageBox("查无数据");
                this.clearSecond();
                return;
            }
            DecimalFormat df = new DecimalFormat("##########0.00");
            this.setValue("QH", result.getValue("YEAR_MON", 0)); //期号
            this.setValue("JZXH", result.getValue("ADM_SEQ", 0)); //就诊序号
            this.setValue("BAH", result.getValue("MR_NO", 0)); //病案号
            this.setValue("ZYH", result.getValue("CASE_NO", 0)); //住院号
            this.setValue("RYLB", this.ctzDesc); //人员类别
            this.setValue("SJZFBL", result.getValue("REALOWN_RATE", 0)); //实际自付比例
            this.setValue("CYZD", result.getValue("DIAG_DESC", 0)); //出院诊断

            //药品费
            this.setValue("YPFSJE", df.format(result.getDouble("PHA_AMT", 0))); //发生金额
            this.setValue("YPZFJE", df.format(result.getDouble("PHA_OWN_AMT", 0))); //自付金额
            this.setValue("YPZHFJE",
                          df.format(result.getDouble("PHA_ADD_AMT", 0))); //增付金额
            this.setValue("YPSBJE", df.format(result.getDouble("PHA_NHI_AMT", 0))); //申报金额
            //检查费
            this.setValue("JCFSJE", df.format(result.getDouble("EXM_AMT", 0))); //发生金额
            this.setValue("JCZFJE", df.format(result.getDouble("EXM_OWN_AMT", 0))); //自付金额
            this.setValue("JCZHFJE",
                          df.format(result.getDouble("EXM_ADD_AMT", 0))); //增付金额
            this.setValue("JCSBJE", df.format(result.getDouble("EXM_NHI_AMT", 0))); //申报金额
            //治疗费
            this.setValue("ZLFSJE", df.format(result.getDouble("TREAT_AMT", 0))); //发生金额
            this.setValue("ZLZFJE",
                          df.format(result.getDouble("TREAT_OWN_AMT", 0))); //自付金额
            this.setValue("ZLZHFJE",
                          df.format(result.getDouble("TREAT_ADD_AMT", 0))); //增付金额
            this.setValue("ZLSBJE",
                          df.format(result.getDouble("TREAT_NHI_AMT", 0))); //申报金额
            //手术费
            this.setValue("SSFSJE", df.format(result.getDouble("OP_AMT", 0))); //发生金额
            this.setValue("SSZFJE", df.format(result.getDouble("OP_OWN_AMT", 0))); //自付金额
            this.setValue("SSZHFJE", df.format(result.getDouble("OP_ADD_AMT", 0))); //增付金额
            this.setValue("SSSBJE", df.format(result.getDouble("OP_NHI_AMT", 0))); //申报金额
            //床位费
            this.setValue("CWFSJE", df.format(result.getDouble("BED_AMT", 0))); //发生金额
            this.setValue("CWZFJE", df.format(result.getDouble("BED_OWN_AMT", 0))); //自付金额
            this.setValue("CWZHFJE",
                          df.format(result.getDouble("BED_ADD_AMT", 0))); //增付金额
            this.setValue("CWSBJE", df.format(result.getDouble("BED_NHI_AMT", 0))); //申报金额
            //医用材料费
            this.setValue("YYCLFSJE",
                          df.format(result.getDouble("MATERIAL_AMT", 0))); //发生金额
            this.setValue("YYCLZFJE",
                          df.format(result.getDouble("MATERIAL_OWN_AMT", 0))); //自付金额
            this.setValue("YYCLZHFJE",
                          df.format(result.getDouble("MATERIAL_ADD_AMT", 0))); //增付金额
            this.setValue("YYCLSBJE",
                          df.format(result.getDouble("MATERIAL_NHI_AMT", 0))); //申报金额
            //输全血
            this.setValue("SQXFSJE",
                          df.format(result.getDouble("BLOODALL_AMT", 0))); //发生金额
            this.setValue("SQXZFJE",
                          df.format(result.getDouble("BLOODALL_OWN_AMT", 0))); //自付金额
            this.setValue("SQXZHFJE",
                          df.format(result.getDouble("BLOODALL_ADD_AMT", 0))); //增付金额
            this.setValue("SQXSBJE",
                          df.format(result.getDouble("BLOODALL_NHI_AMT", 0))); //申报金额
            //成分输血
            this.setValue("CFSXFSJE", df.format(result.getDouble("BLOOD_AMT", 0))); //发生金额
            this.setValue("CFSXZFJE",
                          df.format(result.getDouble("BLOOD_OWN_AMT", 0))); //自付金额
            this.setValue("CFSXZHFJE",
                          df.format(result.getDouble("BLOOD_ADD_AMT", 0))); //增付金额
            this.setValue("CFSXSBJE",
                          df.format(result.getDouble("BLOOD_NHI_AMT", 0))); //申报金额
            //其他
            this.setValue("QTFSJE", df.format(result.getDouble("OTHER_AMT", 0))); //发生金额
            this.setValue("QTZFJE",
                          df.format(result.getDouble("OTHER_OWN_AMT", 0))); //自付金额
            this.setValue("QTZHFJE",
                          df.format(result.getDouble("OTHER_ADD_AMT", 0))); //增付金额
            this.setValue("QTSBJE",
                          df.format(result.getDouble("OTHER_NHI_AMT", 0))); //申报金额
            //合计
            double hjfsje = result.getDouble("PHA_AMT", 0) +
                            result.getDouble("EXM_AMT", 0) +
                            result.getDouble("TREAT_AMT", 0) +
                            result.getDouble("OP_AMT", 0) +
                            result.getDouble("BED_AMT", 0) +
                            result.getDouble("MATERIAL_AMT", 0) +
                            result.getDouble("BLOODALL_AMT", 0) +
                            result.getDouble("BLOOD_AMT", 0) +
                            result.getDouble("OTHER_AMT", 0);
            double hjzfje = result.getDouble("PHA_OWN_AMT", 0) +
                            result.getDouble("EXM_OWN_AMT", 0) +
                            result.getDouble("TREAT_OWN_AMT", 0) +
                            result.getDouble("OP_OWN_AMT", 0) +
                            result.getDouble("BED_OWN_AMT", 0) +
                            result.getDouble("MATERIAL_OWN_AMT", 0) +
                            result.getDouble("BLOODALL_OWN_AMT", 0) +
                            result.getDouble("BLOOD_OWN_AMT", 0) +
                            result.getDouble("OTHER_OWN_AMT", 0);
            double hezhfje = result.getDouble("PHA_ADD_AMT", 0) +
                             result.getDouble("EXM_ADD_AMT", 0) +
                             result.getDouble("TREAT_ADD_AMT", 0) +
                             result.getDouble("OP_ADD_AMT", 0) +
                             result.getDouble("BED_ADD_AMT", 0) +
                             result.getDouble("MATERIAL_ADD_AMT", 0) +
                             result.getDouble("BLOODALL_ADD_AMT", 0) +
                             result.getDouble("BLOOD_ADD_AMT", 0) +
                             result.getDouble("OTHER_ADD_AMT", 0);
            double hjsbje = result.getDouble("PHA_NHI_AMT", 0) +
                            result.getDouble("EXM_NHI_AMT", 0) +
                            result.getDouble("TREAT_NHI_AMT", 0) +
                            result.getDouble("OP_NHI_AMT", 0) +
                            result.getDouble("BED_NHI_AMT", 0) +
                            result.getDouble("MATERIAL_NHI_AMT", 0) +
                            result.getDouble("BLOODALL_NHI_AMT", 0) +
                            result.getDouble("BLOOD_NHI_AMT", 0) +
                            result.getDouble("OTHER_NHI_AMT", 0);

            this.setValue("HJFSJE", df.format(hjfsje)); //发生金额
            this.setValue("HJZFJE", df.format(hjzfje)); //自付金额
            this.setValue("HJZHFJE", df.format(hezhfje)); //增付金额
            this.setValue("HJSBJE", df.format(hjsbje)); //申报金额

            this.setValue("BCSSQFBZJE",
                          df.format(result.getDouble("RESTART_STANDARD_AMT", 0))); //本次实收起伏标准金额
            this.setValue("QFBZYSZFBLJE",
                          df.format(result.getDouble("STARTPAY_OWN_AMT", 0))); //起付标准以上自付比例金额
            this.setValue("ZFXMJE", df.format(result.getDouble("OWN_AMT", 0))); //自费项目金额
            this.setValue("YLJZGRABLFDJE",
                          df.format(result.getDouble("PERCOPAYMENT_RATE_AMT", 0))); //自费项目金额
            this.setValue("ZHFXMJE", df.format(result.getDouble("ADD_AMT", 0))); //增付项目金额
            this.setValue("YLJZZGXEYSJE",
                          df.format(result.getDouble("INS_HIGHLIMIT_AMT", 0))); //医疗救助最高限额以上金额
            this.setValue("SXZFJE",
                          df.format(result.getDouble("TRANBLOOD_OWN_AMT", 0))); //输血自付金额
            double grzfhj = result.getDouble("RESTART_STANDARD_AMT", 0) +
                            result.getDouble("STARTPAY_OWN_AMT", 0) +
                            result.getDouble("OWN_AMT", 0) +
                            result.getDouble("PERCOPAYMENT_RATE_AMT", 0) +
                            result.getDouble("ADD_AMT", 0) +
                            result.getDouble("INS_HIGHLIMIT_AMT", 0) +
                            result.getDouble("TRANBLOOD_OWN_AMT", 0);
            this.setValue("GRZFHJ", df.format(grzfhj)); //合计

            this.setValue("TCJJZFYYSQ",
                          df.format(result.getDouble("APPLY_AMT", 0))); //统筹基金支付医院申请
            this.setValue("YLJZYYSQ",
                          df.format(result.getDouble("HOSP_APPLY_AMT", 0))); //统筹基金支付医院申请
        } else if (index == 2) { //费用明细信息

        } else if (index == 3) { //医保中心下载费用明细

        }
    }

    /**
     *第三页签: 费用明细信息Tab页中点击查询按钮触发的操作.
     */
    public void onSearchFeeDetail() {
        String fylb = this.getValueString("FYLB1");
        String sql =
                "SELECT DISTINCT A.SEQ_NO, A.ORDER_DESC, B.XMMC, D.DOSE_CHN_DESC, A.STANDARD," +
                "A.PRICE, A.QTY, A.TOTAL_AMT, A.REFUSE_AMT," +
                "A.REFUSE_REASON_NOTE, A.OWN_AMT, A.ADDPAY_AMT," +
                "A.TOTAL_NHI_AMT, A.OP_FLG, A.CARRY_FLG, A.ADDPAY_FLG," +
                "A.PHAADD_FLG, A.OWN_RATE, A.NHI_ORD_CLASS_CODE, A.OPT_DATE," +
                "A.NHI_ORDER_CODE, A.REFUSE_REASON_CODE, A.ADM_SEQ" +
                " FROM INS_IBS_UPLOAD A LEFT OUTER JOIN PHA_DOSE D" +
                " ON A.DOSE_CODE = D.DOSE_CODE" +
                " LEFT OUTER JOIN INS_RULE B" +
                " ON A.NHI_ORDER_CODE = B.SFXMBM" +
                " AND A.CHARGE_DATE BETWEEN B.KSSJ AND B.JSSJ" +
                " WHERE A.TOTAL_AMT <> 0 AND A.ADM_SEQ = '" + this.getAdmSeq() +
                "'";
        StringBuilder sbuilder = new StringBuilder(sql);
        if (!"".equals(fylb)) {
            sbuilder.append(" And A.NHI_ORD_CLASS_CODE='" + fylb + "'");
        }
        sbuilder.append(" ORDER BY A.SEQ_NO  ");

        TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.
                toString()));
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            messageBox("查无数据");
            this.callFunction("UI|FYMXXXTABLE|setParmValue", new TParm());
            return;
        }
        this.callFunction("UI|FYMXXXTABLE|setParmValue", result);
    }

    /**
     *第四页签: 医保中心下载费用明细.
     */
    public void searchDownloadFeeDetail() {
        String fylb2 = this.getValueString("FYLB2");
        String sql =
                "SELECT DISTINCT A.SEQ_NO, A.ORDER_DESC, B.XMMC, D.DOSE_CHN_DESC, A.STANDARD," +
                "A.PRICE, A.QTY, A.TOTAL_AMT, A.REFUSE_AMT," +
                "A.REFUSE_REASON_NOTE, A.OWN_AMT, A.ADDPAY_AMT," +
                "A.TOTAL_NHI_AMT, A.OP_FLG, A.CARRY_FLG, A.PHAADD_FLG," +
                "A.PHAADD_FLG, A.OWN_RATE, A.NHI_ORD_CLASS_CODE, A.OPT_DATE," +
                "A.NHI_ORDER_CODE, A.REFUSE_REASON_CODE, A.ADM_SEQ" +
                " FROM INS_IBSORDER_DOWNLOAD A LEFT OUTER JOIN PHA_DOSE D" +
                " ON A.DOSE_CODE = D.DOSE_CODE" +
                " LEFT OUTER JOIN INS_RULE B" +
                " ON A.NHI_ORDER_CODE = B.SFXMBM" +
                " AND A.CHARGE_DATE BETWEEN B.KSSJ AND B.JSSJ" +
                //" , SYS_FEE C"+
                " WHERE A.TOTAL_AMT <> 0" +
                " AND A.ADM_SEQ = '" + this.getAdmSeq() + "' ";
        //" AND A.ORDER_CODE = C.ORDER_CODE";

        StringBuilder sbuilder = new StringBuilder(sql);
        if (!"".equals(fylb2)) {
            sbuilder.append(" And A.NHI_ORD_CLASS_CODE='" + fylb2 + "'");
        }
        sbuilder.append(" ORDER BY A.SEQ_NO");

        TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.
                toString()));
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            messageBox("查无数据");
            this.callFunction("UI|YBZXXZFYMX|setParmValue", new TParm());
            return;
        }
        this.callFunction("UI|YBZXXZFYMX|setParmValue", result);
    }

    /**
     *
     * 转换时间.
     * @param date
     * @return
     */
    private String transferDate(String date) {
        String[] dateArray = date.split(" ");
        String[] newDateArray = dateArray[0].split("-");
        return newDateArray[0] + newDateArray[1] + newDateArray[2];
    }

    /**
     *
     * 核查查询条件.
     * @return
     */
    private boolean checkInput(int index) {
        String startDate = this.getValueString("START_DATE"); //起始时间
        String endDate = this.getValueString("END_DATE"); //结束时间
        String dBZ = this.getValueString("DBZ"); //单病种.
        if ("".equals(startDate)) {
            this.messageBox("起始时间不能为空");
            ((TTextField)this.getComponent("START_DATE")).grabFocus();
            return false;
        }
        if ("".equals(endDate)) {
            this.messageBox("截止时间不能为空");
            ((TTextField)this.getComponent("END_DATE")).grabFocus();
            return false;
        }
        if ("".equals(dBZ)) {
            this.messageBox("单病种不能为空");
            ((TTextField)this.getComponent("DBZ")).grabFocus();
            return false;
        }
        return true;
    }

    /**
     * BRLBTABLE 单击.
     */
    public void brlbTableClicked() {
        TTable brlb = ((TTable)this.getComponent("BRLBTABLE"));
        this.brlbTableSelectedRow = brlb.getSelectedRow();
        TParm parm = brlb.getParmValue();
        this.admSeq = parm.getValue("ADM_SEQ", this.brlbTableSelectedRow); //获得就诊序号.
        this.ctzDesc = parm.getValue("CTZ_DESC", this.brlbTableSelectedRow); //获得人员类别.
    }

    /**
     * 清除公用查询条件信息.
     */
    private void clearQueryCondition() {
        this.setValue("START_DATE", "");
        this.setValue("END_DATE", "");
        this.setValue("DBZ", "");
        this.setValue("NYBLB", "");
        //===========CHENXI  2012.05.29
        this.setValue("IN_STATUS", "");
        //==============chenxi  2012.05.29
    }

    /**
     * 清除第一页签信息.
     */
    private void clearFirst() {
        this.callFunction("UI|BRLBTABLE|setParmValue", new TParm());
    }

    /**
     * 清除第二页签信息.
     */
    private void clearSecond() {
        String clearStr =
                "QH;JZXH;BAH;ZYH;RYLB;SJZFBL;CYZD;YPFSJE;YPZFJE;YPZHFJE;YPSBJE;JCFSJE;JCZFJE;JCZHFJE;JCSBJE;ZLFSJE;"+
                "ZLZFJE;ZLZHFJE;ZLSBJE;SSFSJE;SSZFJE;SSZHFJE;SSSBJE;CWFSJE;CWZFJE;CWZHFJE;CWSBJE;YYCLFSJE;YYCLZFJE;"+
                "YYCLZHFJE;YYCLSBJE;SQXFSJE;SQXZFJE;SQXZHFJE;SQXSBJE;CFSXFSJE;CFSXZFJE;CFSXZHFJE;CFSXSBJE;QTFSJE;"+
                "QTZFJE;QTZHFJE;QTSBJE;HJFSJE;HJZFJE;HJZHFJE;HJSBJE;BCSSQFBZJE;ZFXMJE;ZHFXMJE;SXZFJE;QFBZYSZFBLJE;"+
                "YLJZGRABLFDJE;YLJZZGXEYSJE;GRZFHJ;TCJJZFYYSQ;YLJZYYSQ";
        this.clearValue(clearStr);
    }

    /**
     * 清除第三页签信息.
     */
    private void clearThird() {
        this.setValue("FYLB1", "");
        this.callFunction("UI|FYMXXXTABLE|setParmValue", new TParm());
    }

    /**
     * 清除第四页签信息.
     */
    private void clearFourth() {
        this.setValue("FYLB2", "");
        this.callFunction("UI|YBZXXZFYMX|setParmValue", new TParm());
    }

    /**
     * 清除属性内容
     */
    private void clearAttr() {
        this.brlbTableSelectedRow = -1;
        this.admSeq = "";
        this.ctzDesc = "";
    }

    private String specialPat(String specialPat) {
        if (specialPat.equals("01"))
            return "正常";
        else if (specialPat.equals("02"))
            return "两航起义";
        else if (specialPat.equals("03"))
            return "市级退模";
        else if (specialPat.equals("04"))
            return "军残";
        else if (specialPat.equals("05"))
            return "老塘人员";
        else if (specialPat.equals("06"))
            return "公务员";
        return "";
    }

    public int getCurrentTabPage() {
        return currentTabPage;
    }

    public void setCurrentTabPage(int currentTabPage) {
        this.currentTabPage = currentTabPage;
    }

    public int getBrlbTableSelectedRow() {
        return brlbTableSelectedRow;
    }

    public void setBrlbTableSelectedRow(int brlbTableSelectedRow) {
        this.brlbTableSelectedRow = brlbTableSelectedRow;
    }

    public String getAdmSeq() {
        return admSeq;
    }

    public void setAdmSeq(String admSeq) {
        this.admSeq = admSeq;
    }

    public String getCtzDesc() {
        return ctzDesc;
    }

    public void setCtzDesc(String ctzDesc) {
        this.ctzDesc = ctzDesc;
    }

    public String getNhi_no() {
        return nhi_no;
    }

    public void setNhi_no(String nhiNo) {
        nhi_no = nhiNo;
    }
    /**
     * 病患查询
     * ====zhangp 20120417
     */
    public void onQueryPat(){
    	String mrno = getValueString("MR_NO");
    	Pat pat = Pat.onQueryByMrNo(mrno);
    	setValue("MR_NO", pat.getMrNo());
    	setValue("PAT_NAME", pat.getName());
    	onQuery();//add by wanglong 20121011
    }
    /**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = brlbTable.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = brlbTable.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
    /**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener1(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = fymxxxTable.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = fymxxxTable.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam1(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		brlbTable.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	/**
	 * vectory转成param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam1(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		fymxxxTable.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	/**
	 * 得到 Vector 值
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 转换parm中的列
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}
	/**
	 * 单选按钮事件
	 */
	public void onExeType() {
		if (this.getRadioButton("IN_TYPE").isSelected()) {// 入院时间查询
			onExeEnable(true);
		}else if (this.getRadioButton("UPLOAD_TYPE").isSelected()) {//上传时间查询
			onExeEnable(false);	
		}
	}
	private void onExeEnable(boolean flg) {
		 callFunction("UI|START_DATE|setEnabled", flg);//起始时间
	     callFunction("UI|END_DATE|setEnabled", flg);//截止时间
	     callFunction("UI|UPLOAD_START_DATE|setEnabled", flg ? false : true);//上传开始时间
	     callFunction("UI|UPLOAD_END_DATE|setEnabled", flg ? false : true);//上传结束时间  
	     callFunction("UI|IN_STATUS|setEnabled", flg);//状态	
	}
    /**
	* 获得单选控件
	* 
	* @param name
	* @return
	*/
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}	
	
	
}
