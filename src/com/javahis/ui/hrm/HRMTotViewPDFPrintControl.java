package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import jdo.bil.BILComparator;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMSchdayDr;
import jdo.pdf.PDFODITool;
import jdo.pdf.StreamGobbler;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.EText;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 健康检查总检报告批量打印 </p>
 * 
 * <p> Description: 健康检查总检报告批量打印 </p>
 * 
 * <p> Copyright: javahis 20171228 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author zhanglei
 * @version 5.0
 */
public class HRMTotViewPDFPrintControl extends TControl {

    // 患者信息TABLE,医嘱TABLE
    private TTable patTable;
    private int sortColumn = -1;
    private boolean ascending = false;
    private BILComparator compare = new BILComparator();
    /**合同TTextFormat*/
    private TTextFormat contract;
    /**团体名称TTextFormat*/
    private TTextFormat company;
    /**全部报到TCheckBox*/
    private TCheckBox all;
    /**合同对象*/
    private HRMContractD contractD;
    /**团体代码、合同代码*/
    private String companyCode, contractCode;
    /**保存时使用的fileNo*/
    private String fileNo;
    /**医嘱对象*/
    private HRMOrder order;
    private TParm patUndoParm = new TParm();
    private TParm patDoParm = new TParm();
    private TParm patAllParm = new TParm();
    /**病历书写器*/
//    private TWord word;
    /**PDF路径*/
    private String tempPath = "C:\\JavaHisFile\\temp\\pdf";
    /**错误日志书写文件地址*/
    private String exception = "C:\\JavaHis\\logs";
    /**得到PDFTool对象*/
    PDFODITool tool = new PDFODITool();
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        initComponent();// 初始化控件
        initData();// 初始化数据
//        this.messageBox("11");
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
    	patTable = (TTable) this.getComponent("PAT_TABLE");
    	addSortListener(patTable);
    	contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
    	company = (TTextFormat) this.getComponent("COMPANY_CODE");
    	all = (TCheckBox) this.getComponent("ALL1");
    }
    
    /**
     * 初始化数据
     */
    private void initData() {
    	 this.setValue("Pan", "D");
         this.setValue("WZ", "键检总检批量生成");
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        Timestamp date1 = SystemTool.getInstance().getDate();
//        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("START_DATE",
                StringTool.rollDate(date1, -7).toString().substring(0, 10).
                replace('-', '/') + " 00:00:00");
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
    	contractD = new HRMContractD();
    	contractD.onQuery("", "", "");
    	//团体名称需要根据不同登录角色筛选
    	String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
		if(!roleType.contains("SYSOPERATOR")){
			callFunction("UI|Pan|setEnabled", false);
			callFunction("UI|WZ|setEnabled", false);
		}
//		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
		company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
    	this.setValue("ALL", "Y");
    	String deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox("取得科别属性错误");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
       
    }
    
    /**
     * 团体代码点选事件
     */
    public void onCompanyChoose() {
    	companyCode = this.getValueString("COMPANY_CODE");
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm == null || contractParm.getCount() <= 0
                || contractParm.getErrCode() != 0) {
            this.messageBox_("没有数据");
            return;
        }
        // System.out.println("contractParm="+contractParm);
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("查询失败");
            return;
        }
        contract.setValue(contractCode);   
        onDoQuery();
    }
    
    /**
     * 点击日期和科别属性时，执行查询
     */
    public void onDoQuery() {
        onQuery();
       
    }
    
    /**
	 * 查询
	 */
    public void onQuery() {
        Timestamp now = (Timestamp) this.getValue("START_DATE");
        Timestamp tomorrow = (Timestamp) this.getValue("END_DATE");
        String startDate = StringTool.getString(now, "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(tomorrow, "yyyyMMdd") + "235959";
        fileNo = "";
//        order = new HRMOrder(); 
        String  compantCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        patUndoParm = this.getFinalCheckPat("", startDate, endDate, "1", 
        										compantCode.length()<=0 ? "" : compantCode, 
        												contractCode.length()<=0 ? "" : contractCode);// 未完成
        patDoParm = this.getFinalCheckPat("", startDate, endDate, "2", 
        									compantCode.length()<=0 ? "" : compantCode, 
        											contractCode.length()<=0 ? "" : contractCode);// 已完成
        patAllParm = this.getFinalCheckPat("", startDate, endDate, "", 
        									compantCode.length()<=0 ? "" : compantCode, 
        											contractCode.length()<=0 ? "" : contractCode);// 全部
        if (patUndoParm.getErrCode() != 0 || patDoParm.getErrCode() != 0
                || patAllParm.getErrCode() != 0) {
            this.messageBox("查询失败 " + patUndoParm.getErrText() + patDoParm.getErrText()
                    + patAllParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            this.setValue("ALL_NUM", "");
            patTable.removeRowAll();
            return;
        }
        
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
//		String filter = "";
//		filter = this.getPopedemParm().getValue("ID");
////		this.messageBox(this.getPopedemParm().getValue("ID"));
		int doCount = patDoParm.getCount();
		int undoCount = patUndoParm.getCount();
		int allCount = patAllParm.getCount();
//		for (int i = doCount - 1; i > -1; i--) {
//			if (!filter.contains(patDoParm.getValue("ROLE_TYPE", i))) {
//				patDoParm.removeRow(i);
//			}
//		}
//
//		for (int j = undoCount - 1; j > -1; j--) {
//			if (!filter.contains(patUndoParm.getValue("ROLE_TYPE", j))) {
//				patUndoParm.removeRow(j);
//			}
//		}
//
//		for (int k = allCount - 1; k > -1; k--) {
//			if (!filter.contains(patAllParm.getValue("ROLE_TYPE", k))) {
//				patAllParm.removeRow(k);
//			}
//		}
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
        
        if ((patUndoParm == null || patUndoParm.getCount() <= 0)
                && (patDoParm == null || patDoParm.getCount() <= 0)
                && (patAllParm == null || patAllParm.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", "0人");
            this.setValue("DONE_NUM", "0人");
            this.setValue("ALL_NUM", "0人");
            patTable.removeRowAll();
            return;
        }
        undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "人");
        this.setValue("DONE_NUM", doCount + "人");
        this.setValue("ALL_NUM", allCount + "人");
        if (this.getValueBoolean("UNDONE")) {// 未完成
            patTable.setParmValue(patUndoParm);
        } else if (this.getValueBoolean("DONE")) {// 完成
            patTable.setParmValue(patDoParm);
        } else {// 全部
            patTable.setParmValue(patAllParm);
        }
    }

    /**
     * 根据MR_NO查询数据
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ==== chenxi
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI患者查重提示 start
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			 this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
        // ========================= caowl 20130326 start
        String startDate = "";//不卡时间条件
        String endDate = "";
        fileNo = "";
//        order = new HRMOrder();
        String  compantCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        patUndoParm = this.getFinalCheckPat(mrNo, startDate, endDate, "1", 
        										compantCode.length() <=0 ? "" : compantCode, 
        												contractCode.length() <=0 ? "" : contractCode);
        patDoParm = this.getFinalCheckPat(mrNo, startDate, endDate, "2", 
        									  compantCode.length() <=0 ? "" : compantCode, 
        											contractCode.length() <=0 ? "" : contractCode);
        patAllParm = this.getFinalCheckPat(mrNo, startDate, endDate, "", 
							        		  compantCode.length() <=0 ? "" : compantCode, 
							        				contractCode.length() <=0 ? "" : contractCode);
        if (patUndoParm.getErrCode() != 0 || patDoParm.getErrCode() != 0
                || patAllParm.getErrCode() != 0) {
            this.messageBox("查询失败 " + patUndoParm.getErrText() + patDoParm.getErrText()
                    + patAllParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            this.setValue("ALL_NUM", "");
            patTable.removeRowAll();
            return;
        }
        
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
        String filter = "";
		filter = this.getPopedemParm().getValue("ID");
		int doCount = patDoParm.getCount();
		int undoCount = patUndoParm.getCount();
		int allCount = patAllParm.getCount();
		for (int i = doCount - 1; i > -1; i--) {
			if (!filter.contains(patDoParm.getValue("ROLE_TYPE", i))) {
				patDoParm.removeRow(i);
			}
		}

		for (int j = undoCount - 1; j > -1; j--) {
			if (!filter.contains(patUndoParm.getValue("ROLE_TYPE", j))) {
				patUndoParm.removeRow(j);
			}
		}

		for (int k = allCount - 1; k > -1; k--) {
			if (!filter.contains(patAllParm.getValue("ROLE_TYPE", k))) {
				patAllParm.removeRow(k);
			}
		}
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
        
        if ((patUndoParm == null || patUndoParm.getCount() <= 0)
                && (patDoParm == null || patDoParm.getCount() <= 0)
                && (patAllParm == null || patAllParm.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", "0人");
            this.setValue("DONE_NUM", "0人");
            this.setValue("ALL_NUM", "0人");
            patTable.removeRowAll();
            return;
        }
        undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "人");
        this.setValue("DONE_NUM", doCount + "人");
        this.setValue("ALL_NUM", allCount + "人");
        if (patAllParm.getValue("EXEC_DR_CODE", 0).equals("")) {// 查询出的最新一次就诊记录如果为已完成
            this.setValue("UNDONE", "Y");// 未完成
            patTable.setParmValue(patUndoParm);
        } else {// 完成
            this.setValue("DONE", "Y");
            patTable.setParmValue(patDoParm);
        }
        patTable.setSelectedRow(0);
    }
    
    /**
     * 选择状态
     */
    public void onChooseState() {
        int undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        int doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        int allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "人");
        this.setValue("DONE_NUM", doCount + "人");
        this.setValue("ALL_NUM", allCount + "人");
        if (this.getValueBoolean("UNDONE")) {// 未完成
            patTable.setParmValue(patUndoParm);
        } else if (this.getValueBoolean("DONE")) {// 完成
            patTable.setParmValue(patDoParm);
        } else {// 全部
            patTable.setParmValue(patAllParm);
        }
    }
    
    
    /**
     * 通过病案号取得待总检的病患列表
     * @param mrNo
     * @param startDate
     * @param endDate
     * @param isUnDone
     * @return
     */
    public TParm getFinalCheckPat(String mrNo,String startDate, String endDate, String isUnDone, String companyCode, String contractCode) {//add by zhanglei 20170103   
        TParm result = new TParm();
        //查询条件
        String a = "";
        //团体
        if(companyCode.length()>0){
        	a+=" AND C.COMPANY_CODE ='" + companyCode + "' ";
        }
        //合同
        if(contractCode.length()>0){
        	a+=" AND C.CONTRACT_CODE ='" + contractCode + "'";
        }
        //caowl 20130326 start
        //取得待总检的病患列表
        String sql =
                "SELECT  DISTINCT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN '未完成' ELSE '已完成' END DONE, "
//                        + "'N' AS CHOOSE, A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, B.BIRTHDAY, B.REPORT_STATUS, B.CONTRACT_CODE, B.TEL, B.COMPANY_CODE, C.ROLE_TYPE "//modify by wanglong 20131209
                        + "'N' AS CHOOSE, A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, B.BIRTHDAY, B.REPORT_STATUS, B.TEL, B.COMPANY_CODE, C.ROLE_TYPE "//modify by wanglong 20131209
                        + "  FROM HRM_ORDER A,HRM_PATADM B, HRM_CONTRACTD C "
                        + " WHERE A.EXEC_DEPT_CODE = '!'    "// 执行科室
                        + "   AND A.DEPT_ATTRIBUTE = '04'    "// 总检
                        + "   AND A.CASE_NO = B.CASE_NO      "
                        + "   AND A.SETMAIN_FLG='Y'          "
                        + "   AND B.CONTRACT_CODE = C.CONTRACT_CODE AND B.MR_NO = C.MR_NO "
                        + a
                        + "   @  #  &  ORDER BY A.CASE_NO DESC ";
        sql = sql.replaceFirst("!", Operator.getDept());
        if (!StringUtil.isNullString(mrNo)) {
            sql = sql.replaceFirst("@", " AND B.MR_NO='" + mrNo + "'");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // caowl 20130326 start
        if (!StringUtil.isNullString(startDate) && !StringUtil.isNullString(endDate)) {
            sql =
                    sql.replaceFirst("#", " AND B.REPORT_DATE BETWEEN TO_DATE('" + startDate
                            + "','YYYYMMDDHH24MISS') " + " AND TO_DATE('" + endDate
                            + "','YYYYMMDDHH24MISS') ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        // caowl 20130326 end
        if ("1".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("&", "AND A.EXEC_DR_CODE IS NULL");
        } else if ("2".equalsIgnoreCase(isUnDone)) {
            sql = sql.replaceFirst("&", "AND A.EXEC_DR_CODE IS NOT NULL");
        } else {
            sql = sql.replaceFirst("&", "");
        }
        System.out.println("ZJSQL " + sql);
        //caowl 20130326 end
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 全选事件
     */
    public void onChooseAll() {
        if (patTable == null) {
            return;
        }
        TParm parm = patTable.getParmValue();
        if (parm == null) {
            return;
        }
        if (parm.getCount() < 1) {
            return;
        }
        int count = parm.getCount();
        if (all.isSelected()) {
            for (int i = 0; i < count; i++) {
                parm.setData("CHOOSE", i, "Y");
            }
        } else {
            for (int i = 0; i < count; i++) {
                parm.setData("CHOOSE", i, "N");
            }
        }
        patTable.setParmValue(parm);
    }
    
    public void onClickTableCheckbox(){
//    	System.out.println(1);
		int column = patTable.getSelectedRow();
		int row = patTable.getSelectedColumn();
		if ("N".equals(patTable.getItemString(column, "CHOOSE"))
				&& row == 0) {
			patTable.setItem(column, "CHOOSE", "Y");
		} else if ("Y".equals(patTable.getItemString(column, "CHOOSE"))
				&& row == 0){
			patTable.setItem(column, "CHOOSE", "N");
		}
    }
    
    /**
	 * 打印
	 */
	public void onPrint(){
		int x = 0;
		TParm parm1 = patTable.getParmValue();
        if (parm1 == null) {
        	this.messageBox("无打印数据");
            return;
        }
        int count = parm1.getCount();
        if (count <= 0) {
        	this.messageBox("无打印数据");
            return;
        }
        for(int i = 0 ; i<count ; i++){
        	TParm parm = patTable.getParmValue().getRow(i);
//        	System.out.println("CHOOSE:"+parm.getValue("CHOOSE"));
//        	if(true){
//        		continue;
//        	}
        	if(parm.getValue("CHOOSE").equals("Y")){
        		this.delAllFile(tempPath);//清除缓存文件夹中的数据
        		++x;
        		String caseNo = parm.getValue("CASE_NO");
        		String mrNo = parm.getValue("MR_NO");
//            	this.messageBox("i" + i + " caseNo " + caseNo);
    	        if (StringUtil.isNullString(caseNo)) {
    	            this.messageBox(parm.getValue("PAT_NAME") + "取得医嘱数据失败");
    	            return;
    	        }
    	        TParm emrParm = new TParm();
    	        emrParm.setData("MR_CODE", parm.getValue("MR_CODE"));
    	        emrParm.setData("CASE_NO", parm.getValue("CASE_NO"));
    	        emrParm.setData("ADM_TYPE", "H");
    	        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
//    	        this.messageBox("EMR " + emrParm);
//    	        this.messageBox("FILE_PATH " + emrParm.getValue("FILE_PATH") + " FILE_PATH " + emrParm.getValue("FILE_PATH"));
    	        String filePath =
                        emrParm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + emrParm.getValue("FILE_PATH") : emrParm.getValue("FILE_PATH");
                TWord word = new TWord();
                word.onOpen(filePath, emrParm.getValue("FILE_NAME"), 3, true);
//                String ZJFileName = parm.getValue("CASE_NO") + "_总检";//总检暂存文件名
//                word.onPreviewWord();
//                word.print();
                try {
					word.getPageManager().setOrientation(1);
					word.getPageManager().print(PrinterJob.getPrinterJob(),
							"0PL");
				} catch (Exception ex) {
					onException("1病案号：" + mrNo + "  就诊号：" + caseNo + "  病患生成总检报告时报错，请重新生成或单独打印" );
					ex.printStackTrace();
				}
                TParm p = new TParm();
                int u = printRisReportPDF(parm);//缓存RIS信息到本地
               
                p=this.addPdf( u, tempPath, caseNo, mrNo);
                if(p.getErrCode() != 0){
                	onException("2病案号：" + mrNo + "  就诊号：" + caseNo + "  " + p );
        		}
                String TTSql = "SELECT COMPANY_DESC FROM HRM_COMPANY WHERE COMPANY_CODE ='" + parm.getValue("COMPANY_CODE") + "'";
        		TParm TTP = new TParm(TJDODBTool.getInstance().select(TTSql));
                String HTSql = "SELECT CONTRACT_DESC FROM HRM_CONTRACTM WHERE COMPANY_CODE ='" +  parm.getValue("COMPANY_CODE")
        				+ "' AND CONTRACT_CODE = '" +  parm.getValue("CONTRACT_CODE") + "' ";
        		TParm HTP = new TParm(TJDODBTool.getInstance().select(HTSql));
                //将文件复制到指定目录
        		String oldPath = tempPath + "\\" + caseNo + ".pdf";
        		
        		String newPath = this.getValueString("Pan").trim() + ":\\" + this.getValueString("WZ").trim().replaceAll("/", "\\") + "\\" + TTP.getValue("COMPANY_DESC",0) + "_" + HTP.getValue("CONTRACT_DESC",0) + "_" 
        								+ parm.getValue("PAT_NAME")+ "_" + mrNo + ".pdf";
        		File f = new File(this.getValueString("Pan").trim() + ":\\" + this.getValueString("WZ").trim().replaceAll("/", "\\"));
        		if (!f.exists()) {
        			f.mkdirs();
        		}
        		if (f.exists()) {
//        			copyFile(oldPath, newPath, mrNo, caseNo);
        			TParm b = new TParm(copyFile(oldPath, newPath));
        			 if(b.getErrCode() != 0){
                     	onException("3病案号：" + mrNo + "  就诊号：" + caseNo + " 复制最终文件是出现错误 " + b );
             		}
        		}
        	}
        }
        if(x == 0){
    		this.messageBox("请选择打印的数据");
    	}
    }
	
	/**
     * 打印检查报告
     */
	public int printRisReportPDF(TParm patTableParm) {
		String serverPath = "";
		int z = 5;
//		int row = patTable.getSelectedRow();
//		TParm patTableParm = patTable.getParmValue();
		String caseNo = patTableParm.getValue("CASE_NO");
		String mrNo = patTableParm.getValue("MR_NO");
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		String pdfPath = "PDF\\" + caseNo.substring(0, 2) + "\\"
				+ caseNo.substring(2, 4) + "\\" + mrNo;
		serverPath = TConfig.getSystemValue("FileServer.Main.Root") + "\\"
				+ TConfig.getSystemValue("EmrData") + "\\" + pdfPath;
		String pdfFileArray[] = TIOM_FileServer.listFile(TIOM_FileServer
				.getSocket(), serverPath);
//		if(pdfFileArray.length > 0){
//			for(int i=0;i<pdfFileArray.length;i++){
//				System.out.println("pdfFileArray[" +i+ "] " + pdfFileArray[i]);
//				}
//		}
		if (pdfFileArray == null || pdfFileArray.length < 1) {
			return z;
		}
		String medSql = "SELECT * FROM MED_APPLY WHERE CASE_NO ='" + caseNo
				+ "' AND CAT1_TYPE = 'RIS' ";
		TParm medParm = new TParm(TJDODBTool.getInstance().select(medSql));
		int count = pdfFileArray.length;
		String pdfFileName = "";
		byte[] data;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < medParm.getCount(); j++) {
				pdfFileName = caseNo + "_检查报告_"
						+ medParm.getValue("APPLICATION_NO", j);
//				this.messageBox(pdfFileArray[i]+pdfFileName);
				if (pdfFileArray[i].contains(pdfFileName)) {
					data = TIOM_FileServer.readFile(
							TIOM_FileServer.getSocket(), serverPath + "\\"
									+ pdfFileArray[i]);
					if (data == null) {
						continue;
					}
					try {
//						FileTool.setByte(tempPath + "\\" + pdfFileArray[i],
//								data);
						FileTool.setByte(tempPath + "\\" + (i+1) + "PL.pdf",
								data);
						z=i+6;//由于有pdf.bat等文件所以至少加6
						
					} catch (Exception ex) {
						onException("4病案号：" + mrNo + "  就诊号：" + caseNo + "生成" + pdfFileName 
										+ "检查报告时报错：" + ex.getMessage() );
						System.out.println(ex.getMessage());
					}
					break;
				}
			}
		}
		return z;
	}
	
	/**
	 * 测试类
	 */
	public void onCrawl(){
//		String a[] = TIOM_FileServer.listFile(TIOM_FileServer
//				.getSocket(), tempPath);
//		this.messageBox_(a.length);
		
//		int z = 0;
//		int i = 0;
//		File f = new File(tempPath);
//		String[] listFile=f.list();
//		for (i = 0; i < 4; i++) {
//			for (int j = 0; j < listFile.length; j++) {
//				if ((i + "PL.pdf").equals(listFile[j]))
////				if ((listFile[j]).contains("PL.pdf"))
////					sb.append(i + ".pdf ");
//					z++;
//					
//				// break;
//			}
//		}
//		this.messageBox("i="+ i + " z=" + z);
		
//		 for (int i = 0; i < 100; i++) {
//	            if ((i) % 10 == 0) {
//	                System.out.println("-------" + i);
//	            }
//	            System.out.print(i);
//	            try {
//	                Thread.sleep(1000);
//	                System.out.print("    线程睡眠1秒！\n");
//	            } catch (InterruptedException e) {
//	                e.printStackTrace();
//	            }
//	        }
		
//		onException("b");
		
//		String sour = "C:\\JavaHisFile\\temp\\pdf\\180103000001.pdf";
//		String dest = "D:\\a1\\"+"1"+".pdf";
//		String oldPath = "C:\\JavaHisFile\\temp\\pdf\\180103000001.pdf";
//		String newPath = "D:\\a1\\"+"1"+".pdf";

//		copyFile(oldPath, newPath);
		
//		String sour = "C:\\JavaHisFile\\temp\\pdf\\180103000001.pdf";		
//     	String dest = "E:\\1.pdf";
//		TParm a = (TParm) copyFile(sour , dest);
		

	}
	
	/**
	 * 校验TABLE中数据是否LIS检验合格
	 */
	public void onLISJiaoYan(String caseNo){
		String lisSql = "SELECT SUBSTR(A.ORDER_CODE, 0, 5) AS ORDER_TYPE,A.ORDER_DESC,B.* FROM HRM_ORDER A, MED_LIS_RPT B WHERE A.CASE_NO='" + caseNo + 
				"' AND A.CAT1_TYPE='LIS' AND A.CAT1_TYPE=B.CAT1_TYPE AND A.MED_APPLY_NO=B.APPLICATION_NO ORDER BY ORDER_TYPE DESC,A.ORDER_CODE,B.RPDTL_SEQ";
		TParm lisParm = new TParm(TJDODBTool.getInstance().select(lisSql));
		
	}
	
	/**
	 * 根据检验结果及范围设定显示样式
	 */
//	private int showLisResult(String testValue, String upperLimit,
//			String lowerLimit) {
	private int showLisResult(String testValueZ, String upperLimit,
			String lowerLimit) {
		String testValue =testValueZ.replaceAll(" ", "");
		int a = 0;
		if (isNumber(testValue) && isNumber(upperLimit) && isNumber(lowerLimit)) {
			Double test = Double.parseDouble(testValue);
			Double uppe = Double.parseDouble(upperLimit);
			Double lower = Double.parseDouble(lowerLimit);
			if (test > uppe || test < lower) {
				a++;
			} 
		} else if("阳性".equals(testValue) || "异常".equals(testValue)||(testValue != null && testValue.contains("阳"))){
			a++;
		}else if(isNumber(testValue) &&  !isNumber(lowerLimit)){//结果值为数值，下限值 不为数值(当下限制不为数值时，上限制是为空字符串，所以不考虑上限字段是否为数值)
			
			Double test = Double.parseDouble(testValue);
			if(lowerLimit.contains("<")){
				String lowerValue = lowerLimit.substring(lowerLimit.indexOf("<")+1,lowerLimit.length());
				if(isNumber(lowerValue)){//再次确认截取的字串是否为数值
					Double lower = Double.parseDouble(lowerValue);
					if(test >= lower){//表示异常值
						a++;
				}
			}else if(lowerLimit.contains(">")){
					String lowerValue1 = lowerLimit.substring(lowerLimit.indexOf(">")+1,lowerLimit.length());
					if(isNumber(lowerValue1)){//再次确认截取的字串是否为数值
						Double lower = Double.parseDouble(lowerValue1);
						if(test < lower){//表示异常值
							a++;
						}
					}
				}
				
			}
			
		}
		return a;
		
	}
	
	/**
     * 是否是数字
     * 
     * @return boolean
     */
    public boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }
	
    /**
     * 用windos 命令复制文件 效率比IO高
     * @param sour
     * @param dest
     * @return
     */	
	 private  Map copyFile(String sour, String dest) {

		//执行doc命令
		TParm reset = new TParm();
		try {
			String command = "cmd /c copy  "+sour+" "+dest;
//			this.messageBox(command);
//			System.out.println("111122222222  " + command);
			Process proc = Runtime.getRuntime().exec(command);//获取进程
			proc.getOutputStream().close();//关闭输出流
			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			int a = proc.waitFor();
			//System.out.println("-----a-------" + a);// 0 是成功 //1是失败
			if (a != 0) {
				// System.out.println("errorGobbler"+errorGobbler.getErrors());
				String strErrors="";
				for (String v : errorGobbler.getErrors()) {
					//System.out.println("------Errors-----" + v);
					strErrors+=v+"\n";
				}
				reset.setErr(-1, strErrors);
			}else{
				//reset.setErr(-1, strErrors)
				//System.out.println("-----success------");
			}
		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
//		
		return reset.getData();
		
	}


	/** 
	* 复制单个文件  IO
	* @param oldPath String 原文件路径 如：c:/fqf.txt 
	* @param newPath String 复制后路径 如：f:/fqf.txt 
	* @return boolean 
	*/ 
	public void copyFile(String oldPath, String newPath, String mrNo, String caseNo) { 
		try {
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1444]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) {
						bytesum += byteread; //字节数 文件大小 
	//					System.out.println(bytesum); 
						fs.write(buffer, 0, byteread); 
					} 
				inStream.close(); 
			} 
		} 
		catch (Exception e) {
			onException("5病案号：" + mrNo + "  就诊号：" + caseNo  
					+ "复制文件时报错");
			e.printStackTrace(); 
		} 

	} 
	
	/**
	 * 错误日志书写
	 */
	public void onException(String a){
		Date beginDate = SystemTool.getInstance().getDate();//得到系统时间
		String b;
		
		b=beginDate.toString().substring(0,19);
		
		File f = new File(exception);
		if (!f.exists()) {
			f.mkdirs(); //如果不存在，创建目录
		}
		
		File file = new File(f, "HRMPLLog.txt");
		try {
			FileOutputStream fou = new FileOutputStream(file,true);
			// 写数据
				fou.write(("错误时间：" + b + "  错误原因：" + a).getBytes());
				fou.write("\r\n".getBytes());// 写入一个换行

			fou.close();// 释放资源
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 合并病历文件
	 * 
	 * @param parm
	 *            TParm
	 * @param path
	 *            String
	 * @param caseno
	 *            String
	 * @return TParm
	 */
	public TParm addPdf(int parm, String path, String caseno, String mrNO) {
		//System.out.println("--------come in---------");
//		int z = 0, l = 0;//进程开关
		TParm p = new TParm();
		File f = new File(path);
		String[] listFile=f.list();
		if (!f.exists())
			f.mkdirs();
		int c = parm;
		// 下载执行文件
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				tool.getRoot() + "\\pdftk");
		if (data == null) {
			p.setErr(-1, "服务器上没有找到文件 " + tool.getRoot() + "\\pdftk");
			return p;
		}
		try {
			FileTool.setByte(path + "\\pdftk.exe", data);
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		String s = "";
		// 小于1000份病历
//		if (c <= 1000) {
			// 制作批处理文件
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < c; i++) {
				for (int j = 0; j < listFile.length; j++) {
					if ((i + "PL.pdf").equals(listFile[j])){
//						System.out.println("listFile:" + listFile[j]);
//					if ((listFile[j]).contains(".pdf"))
						sb.append(i + "PL.pdf ");
//						z++;
//						sb.append(listFile[j]);
					// break;
					}
				}
			}
			
//			s = path.substring(0, 2) + " \n" + "cd " + path + " \n"
//					+ "pdftk.exe " + sb.toString() + " cat output " + caseno
//					+ ".pdf \n exit ";
			s = path.substring(0, 2) + " \n" + "cd " + path + " \n"
					+ "pdftk.exe " + sb.toString() + " cat output " + caseno
					+ ".pdf \n exit ";

		try {
			FileTool.setByte(path + "/pdf.bat", s.replaceAll("/", "\\\\")
					.getBytes());
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		
		for(int m=0 ; m<10 ; m++){
				// 执行批处理文件
				p = new TParm(this.exec(path + "\\","pdf.bat"));//修改方法
				if (p.getErrCode() != 0){
					 try {
			                Thread.sleep(1000);
//			                System.out.print("执行批处理文件线程睡眠1秒！\n");
			                p = new TParm(this.exec(path + "\\","pdf.bat"));//修改方法
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            }
//					return p;
				}
		}
		
		return p;
	}
	
	/**
	 * add by lx 新增方法
	 * 执行批处理合并操作
	 * @param com
	 * @param caseNo
	 * @return
	 */
	public Map exec(String path,String batFile){
	 
		//2.客户机合并文件
		TParm reset = new TParm();
		try {
			String command = "cmd /k "+path+batFile;
//			this.messageBox(command);
//			System.out.println("111122222222  " + command);
			Process proc = Runtime.getRuntime().exec(command);
			proc.getOutputStream().close();
			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			int a = proc.waitFor();
			//System.out.println("-----a-------" + a);// 0 是成功 //1是失败
			if (a != 0) {
				// System.out.println("errorGobbler"+errorGobbler.getErrors());
				String strErrors="";
				for (String v : errorGobbler.getErrors()) {
					//System.out.println("------Errors-----" + v);
					strErrors+=v+"\n";
				}
				reset.setErr(-1, strErrors);
			}else{
				//reset.setErr(-1, strErrors)
				//System.out.println("-----success------");
			}
		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
		//
		return reset.getData();
	}
	
	/**
	 * 列表文件
	 * 
	 * @param mrno
	 *            String
	 * @param caseno
	 *            String
	 * @return String[]
	 */
	public String[] listPDFFile(String filePath) {
		return TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), filePath);
	}
	
	/**
	 * 删除指定文件夹下所有文件
	 * param path 文件夹完整绝对路径
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
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
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
    
	/**
	 * 清空事件
	 */
    public void onClear() {
        initData();
        patTable.removeRowAll();
        this.clearText("MR_NO;PAT_NAME;SEX_CODE;BIRTHDAY");
        clearValue("UNDONE_NUM;DONE_NUM;ALL_NUM");// add by wanglong 20130328
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("ALL", "Y");
        this.setValue("ALL1", "N");
    }
}
