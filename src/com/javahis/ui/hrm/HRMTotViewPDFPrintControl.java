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
 * <p> Title: ��������ܼ챨��������ӡ </p>
 * 
 * <p> Description: ��������ܼ챨��������ӡ </p>
 * 
 * <p> Copyright: javahis 20171228 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author zhanglei
 * @version 5.0
 */
public class HRMTotViewPDFPrintControl extends TControl {

    // ������ϢTABLE,ҽ��TABLE
    private TTable patTable;
    private int sortColumn = -1;
    private boolean ascending = false;
    private BILComparator compare = new BILComparator();
    /**��ͬTTextFormat*/
    private TTextFormat contract;
    /**��������TTextFormat*/
    private TTextFormat company;
    /**ȫ������TCheckBox*/
    private TCheckBox all;
    /**��ͬ����*/
    private HRMContractD contractD;
    /**������롢��ͬ����*/
    private String companyCode, contractCode;
    /**����ʱʹ�õ�fileNo*/
    private String fileNo;
    /**ҽ������*/
    private HRMOrder order;
    private TParm patUndoParm = new TParm();
    private TParm patDoParm = new TParm();
    private TParm patAllParm = new TParm();
    /**������д��*/
//    private TWord word;
    /**PDF·��*/
    private String tempPath = "C:\\JavaHisFile\\temp\\pdf";
    /**������־��д�ļ���ַ*/
    private String exception = "C:\\JavaHis\\logs";
    /**�õ�PDFTool����*/
    PDFODITool tool = new PDFODITool();
    
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        initComponent();// ��ʼ���ؼ�
        initData();// ��ʼ������
//        this.messageBox("11");
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
    	patTable = (TTable) this.getComponent("PAT_TABLE");
    	addSortListener(patTable);
    	contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
    	company = (TTextFormat) this.getComponent("COMPANY_CODE");
    	all = (TCheckBox) this.getComponent("ALL1");
    }
    
    /**
     * ��ʼ������
     */
    private void initData() {
    	 this.setValue("Pan", "D");
         this.setValue("WZ", "�����ܼ���������");
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
    	//����������Ҫ���ݲ�ͬ��¼��ɫɸѡ
    	String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
		if(!roleType.contains("SYSOPERATOR")){
			callFunction("UI|Pan|setEnabled", false);
			callFunction("UI|WZ|setEnabled", false);
		}
//		// ��ѯ������Ϣ
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
		company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
    	this.setValue("ALL", "Y");
    	String deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox("ȡ�ÿƱ����Դ���");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
       
    }
    
    /**
     * ��������ѡ�¼�
     */
    public void onCompanyChoose() {
    	companyCode = this.getValueString("COMPANY_CODE");
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm == null || contractParm.getCount() <= 0
                || contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
        // System.out.println("contractParm="+contractParm);
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("��ѯʧ��");
            return;
        }
        contract.setValue(contractCode);   
        onDoQuery();
    }
    
    /**
     * ������ںͿƱ�����ʱ��ִ�в�ѯ
     */
    public void onDoQuery() {
        onQuery();
       
    }
    
    /**
	 * ��ѯ
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
        												contractCode.length()<=0 ? "" : contractCode);// δ���
        patDoParm = this.getFinalCheckPat("", startDate, endDate, "2", 
        									compantCode.length()<=0 ? "" : compantCode, 
        											contractCode.length()<=0 ? "" : contractCode);// �����
        patAllParm = this.getFinalCheckPat("", startDate, endDate, "", 
        									compantCode.length()<=0 ? "" : compantCode, 
        											contractCode.length()<=0 ? "" : contractCode);// ȫ��
        if (patUndoParm.getErrCode() != 0 || patDoParm.getErrCode() != 0
                || patAllParm.getErrCode() != 0) {
            this.messageBox("��ѯʧ�� " + patUndoParm.getErrText() + patDoParm.getErrText()
                    + patAllParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            this.setValue("ALL_NUM", "");
            patTable.removeRowAll();
            return;
        }
        
        // add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� START
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
     	// add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� END
        
        if ((patUndoParm == null || patUndoParm.getCount() <= 0)
                && (patDoParm == null || patDoParm.getCount() <= 0)
                && (patAllParm == null || patAllParm.getCount() <= 0)) {
            this.messageBox("�����ݣ�");
            this.setValue("UNDONE_NUM", "0��");
            this.setValue("DONE_NUM", "0��");
            this.setValue("ALL_NUM", "0��");
            patTable.removeRowAll();
            return;
        }
        undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "��");
        this.setValue("DONE_NUM", doCount + "��");
        this.setValue("ALL_NUM", allCount + "��");
        if (this.getValueBoolean("UNDONE")) {// δ���
            patTable.setParmValue(patUndoParm);
        } else if (this.getValueBoolean("DONE")) {// ���
            patTable.setParmValue(patDoParm);
        } else {// ȫ��
            patTable.setParmValue(patAllParm);
        }
    }

    /**
     * ����MR_NO��ѯ����
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ==== chenxi
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI���߲�����ʾ start
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			 this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160929 EMPI���߲�����ʾ end
        
        // ========================= caowl 20130326 start
        String startDate = "";//����ʱ������
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
            this.messageBox("��ѯʧ�� " + patUndoParm.getErrText() + patDoParm.getErrText()
                    + patAllParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            this.setValue("ALL_NUM", "");
            patTable.removeRowAll();
            return;
        }
        
        // add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� START
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
     	// add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� END
        
        if ((patUndoParm == null || patUndoParm.getCount() <= 0)
                && (patDoParm == null || patDoParm.getCount() <= 0)
                && (patAllParm == null || patAllParm.getCount() <= 0)) {
            this.messageBox("�����ݣ�");
            this.setValue("UNDONE_NUM", "0��");
            this.setValue("DONE_NUM", "0��");
            this.setValue("ALL_NUM", "0��");
            patTable.removeRowAll();
            return;
        }
        undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "��");
        this.setValue("DONE_NUM", doCount + "��");
        this.setValue("ALL_NUM", allCount + "��");
        if (patAllParm.getValue("EXEC_DR_CODE", 0).equals("")) {// ��ѯ��������һ�ξ����¼���Ϊ�����
            this.setValue("UNDONE", "Y");// δ���
            patTable.setParmValue(patUndoParm);
        } else {// ���
            this.setValue("DONE", "Y");
            patTable.setParmValue(patDoParm);
        }
        patTable.setSelectedRow(0);
    }
    
    /**
     * ѡ��״̬
     */
    public void onChooseState() {
        int undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        int doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        int allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "��");
        this.setValue("DONE_NUM", doCount + "��");
        this.setValue("ALL_NUM", allCount + "��");
        if (this.getValueBoolean("UNDONE")) {// δ���
            patTable.setParmValue(patUndoParm);
        } else if (this.getValueBoolean("DONE")) {// ���
            patTable.setParmValue(patDoParm);
        } else {// ȫ��
            patTable.setParmValue(patAllParm);
        }
    }
    
    
    /**
     * ͨ��������ȡ�ô��ܼ�Ĳ����б�
     * @param mrNo
     * @param startDate
     * @param endDate
     * @param isUnDone
     * @return
     */
    public TParm getFinalCheckPat(String mrNo,String startDate, String endDate, String isUnDone, String companyCode, String contractCode) {//add by zhanglei 20170103   
        TParm result = new TParm();
        //��ѯ����
        String a = "";
        //����
        if(companyCode.length()>0){
        	a+=" AND C.COMPANY_CODE ='" + companyCode + "' ";
        }
        //��ͬ
        if(contractCode.length()>0){
        	a+=" AND C.CONTRACT_CODE ='" + contractCode + "'";
        }
        //caowl 20130326 start
        //ȡ�ô��ܼ�Ĳ����б�
        String sql =
                "SELECT  DISTINCT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'δ���' ELSE '�����' END DONE, "
//                        + "'N' AS CHOOSE, A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, B.BIRTHDAY, B.REPORT_STATUS, B.CONTRACT_CODE, B.TEL, B.COMPANY_CODE, C.ROLE_TYPE "//modify by wanglong 20131209
                        + "'N' AS CHOOSE, A.*, B.PAT_NAME, B.SEX_CODE, B.REPORT_DATE, B.BIRTHDAY, B.REPORT_STATUS, B.TEL, B.COMPANY_CODE, C.ROLE_TYPE "//modify by wanglong 20131209
                        + "  FROM HRM_ORDER A,HRM_PATADM B, HRM_CONTRACTD C "
                        + " WHERE A.EXEC_DEPT_CODE = '!'    "// ִ�п���
                        + "   AND A.DEPT_ATTRIBUTE = '04'    "// �ܼ�
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
     * ȫѡ�¼�
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
	 * ��ӡ
	 */
	public void onPrint(){
		int x = 0;
		TParm parm1 = patTable.getParmValue();
        if (parm1 == null) {
        	this.messageBox("�޴�ӡ����");
            return;
        }
        int count = parm1.getCount();
        if (count <= 0) {
        	this.messageBox("�޴�ӡ����");
            return;
        }
        for(int i = 0 ; i<count ; i++){
        	TParm parm = patTable.getParmValue().getRow(i);
//        	System.out.println("CHOOSE:"+parm.getValue("CHOOSE"));
//        	if(true){
//        		continue;
//        	}
        	if(parm.getValue("CHOOSE").equals("Y")){
        		this.delAllFile(tempPath);//��������ļ����е�����
        		++x;
        		String caseNo = parm.getValue("CASE_NO");
        		String mrNo = parm.getValue("MR_NO");
//            	this.messageBox("i" + i + " caseNo " + caseNo);
    	        if (StringUtil.isNullString(caseNo)) {
    	            this.messageBox(parm.getValue("PAT_NAME") + "ȡ��ҽ������ʧ��");
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
//                String ZJFileName = parm.getValue("CASE_NO") + "_�ܼ�";//�ܼ��ݴ��ļ���
//                word.onPreviewWord();
//                word.print();
                try {
					word.getPageManager().setOrientation(1);
					word.getPageManager().print(PrinterJob.getPrinterJob(),
							"0PL");
				} catch (Exception ex) {
					onException("1�����ţ�" + mrNo + "  ����ţ�" + caseNo + "  ���������ܼ챨��ʱ�������������ɻ򵥶���ӡ" );
					ex.printStackTrace();
				}
                TParm p = new TParm();
                int u = printRisReportPDF(parm);//����RIS��Ϣ������
               
                p=this.addPdf( u, tempPath, caseNo, mrNo);
                if(p.getErrCode() != 0){
                	onException("2�����ţ�" + mrNo + "  ����ţ�" + caseNo + "  " + p );
        		}
                String TTSql = "SELECT COMPANY_DESC FROM HRM_COMPANY WHERE COMPANY_CODE ='" + parm.getValue("COMPANY_CODE") + "'";
        		TParm TTP = new TParm(TJDODBTool.getInstance().select(TTSql));
                String HTSql = "SELECT CONTRACT_DESC FROM HRM_CONTRACTM WHERE COMPANY_CODE ='" +  parm.getValue("COMPANY_CODE")
        				+ "' AND CONTRACT_CODE = '" +  parm.getValue("CONTRACT_CODE") + "' ";
        		TParm HTP = new TParm(TJDODBTool.getInstance().select(HTSql));
                //���ļ����Ƶ�ָ��Ŀ¼
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
                     	onException("3�����ţ�" + mrNo + "  ����ţ�" + caseNo + " ���������ļ��ǳ��ִ��� " + b );
             		}
        		}
        	}
        }
        if(x == 0){
    		this.messageBox("��ѡ���ӡ������");
    	}
    }
	
	/**
     * ��ӡ��鱨��
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
				pdfFileName = caseNo + "_��鱨��_"
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
						z=i+6;//������pdf.bat���ļ��������ټ�6
						
					} catch (Exception ex) {
						onException("4�����ţ�" + mrNo + "  ����ţ�" + caseNo + "����" + pdfFileName 
										+ "��鱨��ʱ����" + ex.getMessage() );
						System.out.println(ex.getMessage());
					}
					break;
				}
			}
		}
		return z;
	}
	
	/**
	 * ������
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
//	                System.out.print("    �߳�˯��1�룡\n");
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
	 * У��TABLE�������Ƿ�LIS����ϸ�
	 */
	public void onLISJiaoYan(String caseNo){
		String lisSql = "SELECT SUBSTR(A.ORDER_CODE, 0, 5) AS ORDER_TYPE,A.ORDER_DESC,B.* FROM HRM_ORDER A, MED_LIS_RPT B WHERE A.CASE_NO='" + caseNo + 
				"' AND A.CAT1_TYPE='LIS' AND A.CAT1_TYPE=B.CAT1_TYPE AND A.MED_APPLY_NO=B.APPLICATION_NO ORDER BY ORDER_TYPE DESC,A.ORDER_CODE,B.RPDTL_SEQ";
		TParm lisParm = new TParm(TJDODBTool.getInstance().select(lisSql));
		
	}
	
	/**
	 * ���ݼ���������Χ�趨��ʾ��ʽ
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
		} else if("����".equals(testValue) || "�쳣".equals(testValue)||(testValue != null && testValue.contains("��"))){
			a++;
		}else if(isNumber(testValue) &&  !isNumber(lowerLimit)){//���ֵΪ��ֵ������ֵ ��Ϊ��ֵ(�������Ʋ�Ϊ��ֵʱ����������Ϊ���ַ��������Բ����������ֶ��Ƿ�Ϊ��ֵ)
			
			Double test = Double.parseDouble(testValue);
			if(lowerLimit.contains("<")){
				String lowerValue = lowerLimit.substring(lowerLimit.indexOf("<")+1,lowerLimit.length());
				if(isNumber(lowerValue)){//�ٴ�ȷ�Ͻ�ȡ���ִ��Ƿ�Ϊ��ֵ
					Double lower = Double.parseDouble(lowerValue);
					if(test >= lower){//��ʾ�쳣ֵ
						a++;
				}
			}else if(lowerLimit.contains(">")){
					String lowerValue1 = lowerLimit.substring(lowerLimit.indexOf(">")+1,lowerLimit.length());
					if(isNumber(lowerValue1)){//�ٴ�ȷ�Ͻ�ȡ���ִ��Ƿ�Ϊ��ֵ
						Double lower = Double.parseDouble(lowerValue1);
						if(test < lower){//��ʾ�쳣ֵ
							a++;
						}
					}
				}
				
			}
			
		}
		return a;
		
	}
	
	/**
     * �Ƿ�������
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
     * ��windos ������ļ� Ч�ʱ�IO��
     * @param sour
     * @param dest
     * @return
     */	
	 private  Map copyFile(String sour, String dest) {

		//ִ��doc����
		TParm reset = new TParm();
		try {
			String command = "cmd /c copy  "+sour+" "+dest;
//			this.messageBox(command);
//			System.out.println("111122222222  " + command);
			Process proc = Runtime.getRuntime().exec(command);//��ȡ����
			proc.getOutputStream().close();//�ر������
			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			int a = proc.waitFor();
			//System.out.println("-----a-------" + a);// 0 �ǳɹ� //1��ʧ��
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
	* ���Ƶ����ļ�  IO
	* @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt 
	* @param newPath String ���ƺ�·�� �磺f:/fqf.txt 
	* @return boolean 
	*/ 
	public void copyFile(String oldPath, String newPath, String mrNo, String caseNo) { 
		try {
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //�ļ�����ʱ 
				InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ� 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1444]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) {
						bytesum += byteread; //�ֽ��� �ļ���С 
	//					System.out.println(bytesum); 
						fs.write(buffer, 0, byteread); 
					} 
				inStream.close(); 
			} 
		} 
		catch (Exception e) {
			onException("5�����ţ�" + mrNo + "  ����ţ�" + caseNo  
					+ "�����ļ�ʱ����");
			e.printStackTrace(); 
		} 

	} 
	
	/**
	 * ������־��д
	 */
	public void onException(String a){
		Date beginDate = SystemTool.getInstance().getDate();//�õ�ϵͳʱ��
		String b;
		
		b=beginDate.toString().substring(0,19);
		
		File f = new File(exception);
		if (!f.exists()) {
			f.mkdirs(); //��������ڣ�����Ŀ¼
		}
		
		File file = new File(f, "HRMPLLog.txt");
		try {
			FileOutputStream fou = new FileOutputStream(file,true);
			// д����
				fou.write(("����ʱ�䣺" + b + "  ����ԭ��" + a).getBytes());
				fou.write("\r\n".getBytes());// д��һ������

			fou.close();// �ͷ���Դ
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �ϲ������ļ�
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
//		int z = 0, l = 0;//���̿���
		TParm p = new TParm();
		File f = new File(path);
		String[] listFile=f.list();
		if (!f.exists())
			f.mkdirs();
		int c = parm;
		// ����ִ���ļ�
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				tool.getRoot() + "\\pdftk");
		if (data == null) {
			p.setErr(-1, "��������û���ҵ��ļ� " + tool.getRoot() + "\\pdftk");
			return p;
		}
		try {
			FileTool.setByte(path + "\\pdftk.exe", data);
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		String s = "";
		// С��1000�ݲ���
//		if (c <= 1000) {
			// �����������ļ�
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
				// ִ���������ļ�
				p = new TParm(this.exec(path + "\\","pdf.bat"));//�޸ķ���
				if (p.getErrCode() != 0){
					 try {
			                Thread.sleep(1000);
//			                System.out.print("ִ���������ļ��߳�˯��1�룡\n");
			                p = new TParm(this.exec(path + "\\","pdf.bat"));//�޸ķ���
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            }
//					return p;
				}
		}
		
		return p;
	}
	
	/**
	 * add by lx ��������
	 * ִ��������ϲ�����
	 * @param com
	 * @param caseNo
	 * @return
	 */
	public Map exec(String path,String batFile){
	 
		//2.�ͻ����ϲ��ļ�
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
			//System.out.println("-----a-------" + a);// 0 �ǳɹ� //1��ʧ��
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
	 * �б��ļ�
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
	 * ɾ��ָ���ļ����������ļ�
	 * param path �ļ�����������·��
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
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}
	
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * �����������������
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// ȡ�ñ��е�����
                String columnName[] = tableData.getNames("Data");// �������
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // ������������;
                int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * �����������ݣ���TParmתΪVector
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
     * ����ָ���������������е�index
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
     * �����������ݣ���Vectorת��Parm
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
    // ====================������end======================
    
	/**
	 * ����¼�
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
