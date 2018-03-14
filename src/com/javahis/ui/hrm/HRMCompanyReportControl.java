package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMFeePackTool;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.med.MEDApplyTool;
import jdo.odo.MedApply;
import jdo.sid.IdCardO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ����������屨�� </p>
 * 
 * <p> Description: ����������屨�� </p>
 * 
 * <p> Copyright: javahis 20090922 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyReportControl extends TControl {

    private TTable table;// ����TABLE
    private TTextFormat contract, patName;// ��ͬ����������TTextFormat
    private TRadioButton report,unReport;// ������δ����Radio
    private TCheckBox all;// ȫ������TCheckBox
    private String companyCode, contractCode;// ������롢��ͬ����
    private HRMPatInfo pat;// ��������
    private HRMPatAdm adm;// ��������
    private HRMOrder order;// ҽ������
    private HRMContractD contractD;// ��ͬ����
    private String packageCode, sexCode;// �ײʹ��롢�Ա���� add by wanglong 20121217
    private BILComparator compare = new BILComparator();// add by wanglong 20121217
    private boolean ascending = false;
    private int sortColumn = -1;
    private String caseNo="";//�����Ҳ���Ʒ���
    private String mrNo="";//�����Ҳ���Ʒ���
    private boolean dbaFlg = false;//dbaȨ��
	/**
	 *  ���������
	 */
	private StringBuffer printText = new StringBuffer();
    private int offset_x = 0;
    private int offset_y = 0;
    private TTextFormat company;// ��������TTextFormat
    
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        initComponent();// ��ʼ���ؼ�
        initData();// ��ʼ������
        
        initTMenu();
    }
    
    /**
     * Ȩ�޿���
     */
    public void initTMenu(){
    	dbaFlg = this.getPopedem("SYSDBA");
    	if(dbaFlg){
    		//((TMenuItem)this.getComponent("delete")).setEnabled(true);//ȡ������
    		((TMenuItem)this.getComponent("closeOrder")).setEnabled(true);//ȡ��չ��
    		((TMenuItem)this.getComponent("batchDelete")).setEnabled(true);//����ɾ��
    		((TMenuItem)this.getComponent("singleOpt")).setEnabled(true);//���˲���
    	}else{
    		((TMenuItem)this.getComponent("delete")).setEnabled(false);
    		((TMenuItem)this.getComponent("closeOrder")).setEnabled(false);
    		((TMenuItem)this.getComponent("batchDelete")).setEnabled(false);
    		((TMenuItem)this.getComponent("singleOpt")).setEnabled(false);
    	}
    	
    	// һ���ٴ���ӡ���
    	if (this.getPopedem("PIC")) {
    		callFunction("UI|printWristBands|Visible", true);
    	} else {
    		callFunction("UI|printWristBands|Visible", false);
    	}
    }
    
    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        patName = (TTextFormat) this.getComponent("PAT_NAME");
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// add by wanglong 20121217
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                "onCheckBox");
        report = (TRadioButton) this.getComponent("REPORT");
        unReport = (TRadioButton) this.getComponent("UNREPORT");
        all = (TCheckBox) this.getComponent("ALL");
        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this,
                "onTabValueChanged");// ��ͬϸ��TABLEֵ�ı��¼�
    }
    
    /**
     * ��ʼ������
     */
    private void initData() {
    	// ������ʾ�õĿؼ����
        this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE;PY1;TEL");
        unReport.setSelected(true);
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
        // ʵ�������ݶ���
        pat = new HRMPatInfo();
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        contractD = new HRMContractD();
        contractD.onQuery("", "", "");
        
        // add by wangb 2016/06/23 ����������Ҫ���ݲ�ͬ��¼��ɫɸѡ
		String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
        
		// ��ѯ������Ϣ
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
        
        // add by wangb 2016/7/6 �ײ�������
        TTextFormat packageCode = (TTextFormat) this.getComponent("PACKAGE_CODE");
		// ��ѯ�ײ���Ϣ
		TParm pakcageData = HRMFeePackTool.getInstance().selectHrmPackageByRoleType(roleType);
		packageCode.setPopupMenuData(pakcageData);
		packageCode.setComboSelectRow();
		packageCode.popupMenuShowData();
    }

    /**
     * TABLE�����¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm tableParm = table.getParmValue().getRow(row);
        this.setValueForParm("PAT_NAME;MR_NO;IDNO;SEX_CODE;PACKAGE_CODE;PY1;TEL", tableParm);
    }
    
    /**
     * TABLEֵ�ı��¼�
     * @param tNode
     * @return
     */
    public boolean onTabValueChanged(TTableNode tNode) {
//      if (TypeTool.getBoolean(this.getValue("REPORT"))) {
//          this.messageBox_("�ѱ���Ա�������޸���Ϣ");
//          return true;
//      }
        return false;
    }
    
    /**
     * �Ƿ񱣴�CHECK_BOX�������������Ϊ�գ����ܱ���
     * @param obj
     * @return
     */
    public boolean onCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        TParm parm = table.getParmValue();
        int row = table.getSelectedRow();
        String deptCode = parm.getValue("DEPT_CODE", row);
        if (TypeTool.getBoolean(this.getValue("UNREPORT"))) {
            if (StringUtil.isNullString(deptCode)) {
                this.messageBox_("�������Ҳ���Ϊ��");
                parm.setData("CHOOSE", row, !TypeTool.getBoolean(parm.getData(
                        "CHOOSE", row)));
                table.setParmValue(parm);
                return true;
            }
        }
        return false;
    }
    
    /**
     * �һ�MENU�����¼�
     * @param tableName
     */
    public void showPopMenu() {
        if (unReport.isSelected()) {
            table.setPopupMenuSyntax("");
        } else {
            table.setPopupMenuSyntax("����,openRigthPopMenu");
        }
    }

    /**
     * �Ҽ��¼�
     */
    public void openRigthPopMenu() {
        TParm tableParm = table.getParmValue().getRow(table.getSelectedRow());
        tableParm.setData("PRO", "HRMCompanyReportControl");
        // System.out.println("tableParm"+tableParm);
        if (tableParm.getValue("BILL_NO").length() != 0) {
            this.messageBox("�Ѿ����㲻���Ի��");
            return;
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMPersonReport.x", tableParm);
    }
    
    /**
     * ��������ѡ�¼�
     */
    public void onCompanyChoose() {
        // ����ѡ���������룬���첢��ʼ��������ĺ�ͬ��ϢTTextFormat
    	String roleType = this.getSelComRoleType();
        
        if ("PIC".equals(roleType)) {
			table.setHeader(table.getHeader().replace("����", "����").replace(
					"����", "ɸѡ��"));
    	} else {
			table.setHeader(table.getHeader().replace("����", "����").replace(
					"ɸѡ��", "����"));
    	}
        
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
        contractCode = contractParm.getValue("ID", 0);// ============xueyf modify 23050305
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("��ѯʧ��");
            return;
        }
        contract.setValue(contractCode);    
        String isReport = this.getValueString("REPORT");
        TParm reportParm = contractD.getUnReportParm(companyCode, contractCode,
                isReport);
        table.setParmValue(reportParm);
        if (reportParm.getCount() >= 0) {
            this.setValue("COUNT", reportParm.getCount() + "");
        } else
            this.setValue("COUNT", 0 + "");
        contractD.setSQL("SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='"
                + companyCode + "' AND CONTRACT_CODE='" + contractCode
                + "' AND COVER_FLG='" + isReport + "'");
        contractD.retrieve();
        // ����ѡ��ĺ�ͬ���룬���첢��ʼ���ú�ͬ�Ĳ�����ϢTTextFormat
        TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
        patName.setPopupMenuData(patParm);
        patName.setComboSelectRow();
        patName.popupMenuShowData();
    }

    /**
     * ��ͬ�����ѡ�¼�,����ѡ��ĺ�ͬ���룬���첢��ʼ���ú�ͬ�Ĳ�����ϢTTextFormat
     */
    public void onConChoose() {
        contractCode = this.getValueString("CONTRACT_CODE");
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        String isReport = this.getValueString("REPORT");
        TParm reportParm = contractD.getUnReportParm(companyCode, contractCode,
                isReport);
        table.setParmValue(reportParm);
        if (reportParm.getCount() >= 0) {
            this.setValue("COUNT", reportParm.getCount() + "");
        } else
            this.setValue("COUNT", 0 + "");
        contractD.setSQL("SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='"
                + companyCode + "' AND CONTRACT_CODE='" + contractCode
                + "' AND COVER_FLG='" + isReport + "'");
        contractD.retrieve();
        // contractD.showDebug();
        TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
        patName.setPopupMenuData(patParm);
        patName.setComboSelectRow();
        patName.popupMenuShowData();
    }

    /**
     * �ײʹ��롢�Ա�����ѡ�¼�
     */
    public void onPackageAndSexChoose() {// add by wanglong 20121217
        onConChoose();
        packageCode = this.getValueString("PACKAGE_CODE");
        sexCode = this.getValueString("SEX_CODE");
        String isReport = this.getValueString("REPORT");
        TParm reportParm = table.getParmValue();
        if (reportParm == null) {
            return;
        }
        if (reportParm.getCount() < 1) {
            return;
        }
        int count = reportParm.getCount();
        if (StringUtil.isNullString(packageCode)
                && StringUtil.isNullString(sexCode)) {
        } else if ((!StringUtil.isNullString(packageCode))
                && (!StringUtil.isNullString(sexCode))) {// sex��Ϊ��
            for (int i = count - 1; i >= 0; i--) {
                if (reportParm.getValue("PACKAGE_CODE", i).equals(packageCode)
                        && reportParm.getValue("SEX_CODE", i).equals(sexCode)) {
                    continue;

                } else
                    reportParm.removeRow(i);
            }
        } else if (StringUtil.isNullString(packageCode)) {
            for (int i = count - 1; i >= 0; i--) {
                if (!reportParm.getValue("SEX_CODE", i).equals(sexCode)) {
                    reportParm.removeRow(i);
                }
            }
        } else if (StringUtil.isNullString(sexCode)) {
            for (int i = count - 1; i >= 0; i--) {
                if (!reportParm.getValue("PACKAGE_CODE", i).equals(packageCode)) {
                    reportParm.removeRow(i);
                }
            }
        }
        table.setParmValue(reportParm);
        if (reportParm.getCount() >= 0) {
            this.setValue("COUNT", reportParm.getCount() + "");
        } else
            this.setValue("COUNT", 0 + "");
        this.setValue("PAT_NAME", "");
        this.setValue("MR_NO", "");
        this.setValue("IDNO", "");
        String sql = "SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='"
                + companyCode + "' AND CONTRACT_CODE='" + contractCode + "'";
        if (!StringUtil.isNullString(packageCode)) {
            sql += " AND PACKAGE_CODE='" + packageCode + "' ";
        }
        if (!StringUtil.isNullString(sexCode)) {
            sql += " AND SEX_CODE='" + sexCode + "' ";
        }
        sql += " AND COVER_FLG='" + isReport + "' ";
        contractD.setSQL(sql);
        contractD.retrieve();
        TParm patParm = HRMContractD.getPatComboByPackageAndSex(companyCode,
                contractCode, packageCode, sexCode);
        patName.setPopupMenuData(patParm);
        patName.setComboSelectRow();
        patName.popupMenuShowData();
    }

    /**
     * ���ݲ������鵽����
     */
    public void onPatName() {
        String patName = getText("PAT_NAME").trim();
        if (StringUtil.isNullString(patName)) {
            return;
        }
        // modify by wangb 2016/4/25 START
        /*String sql =
                "SELECT DISTINCT MR_NO, OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, "
                        + "BIRTH_DATE, POST_CODE, ADDRESS FROM SYS_PATINFO WHERE PAT_NAME = '"
                        + patName + "' ORDER BY OPT_DATE DESC NULLS LAST";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0081");// ���޴˲���
            onClear();
            return;
        } else {
            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
            TParm patParm = new TParm();
            if (obj != null) {
                patParm = (TParm) obj;
                setValue("MR_NO", patParm.getValue("MR_NO"));
                setValue("PAT_NAME", patName);
            } else return;
            onQueryByMr();
        }*/
    }

    /**
     * �����Ų�ѯ
     */
    public void onQueryByMr() {//modify by wanglong 20121217
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI���߲�����ʾ start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);// ������
		}
		// modify by huangtt 20160929 EMPI���߲�����ʾ end
        
        
        if (StringUtil.isNullString(companyCode)) {
            TParm result = HRMCompanyTool.getInstance().getContractDByMr(mrNo);
            if (result.getErrCode() != 0) {
                this.messageBox_("��ѯʧ��");
                return;
            }
            
            if (result.getCount() < 1) {
                return;
            } else if (result.getCount() == 1) {// ��������
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (result.getValue("COVER_FLG").equals("Y")) {
                    report.setSelected(true);// ����"�ѱ���"��ѡ��
                    this.callFunction("UI|save|setEnabled", false);
                    if(dbaFlg){//��Ȩ���Ȳſ���ȡ������
                    	this.callFunction("UI|delete|setEnabled", true);
                    }
                } else {
                    unReport.setSelected(true);
                    this.callFunction("UI|save|setEnabled", true);
                    this.callFunction("UI|delete|setEnabled", false);
                }
                this.setValueForParm("COMPANY_CODE;PACKAGE_CODE;PAT_NAME;MR_NO;IDNO;SEX_CODE", result);//modify by wanglong 201212127
                //add by wanglong 20121227
                companyCode=result.getValue("COMPANY_CODE");
                TParm contractParm = contractD.onQueryByCompany(companyCode);
                if (contractParm == null || contractParm.getCount() <= 0
                        || contractParm.getErrCode() != 0) {
                    this.messageBox_("û������");
                    return;
                }
                contract.setPopupMenuData(contractParm);
                contract.setComboSelectRow();
                contract.popupMenuShowData();
                contractCode=result.getValue("CONTRACT_CODE");
                contract.setValue(contractCode);  
                packageCode = result.getValue("PACKAGE_CODE");
            } else {// ��������(����ѡ�񴰿�)
                Object obj = this.openDialog(
                        "%ROOT%\\config\\hrm\\HRMPatRecord.x", result);
                if (obj != null) {
                    TParm rowParm = (TParm) obj;
                    table.setParmValue(rowParm);
                    table.setSelectedRow(0);
                    rowParm = rowParm.getRow(0);
                    if (rowParm.getValue("COVER_FLG").equals("Y")) {
                        report.setSelected(true);// ����"�ѱ���"��ѡ��
                        this.callFunction("UI|save|setEnabled", false);
                        if(dbaFlg){//��Ȩ���Ȳſ���ȡ������
                        	this.callFunction("UI|delete|setEnabled", true);
                        }
                    } else {
                        unReport.setSelected(true);
                        this.callFunction("UI|save|setEnabled", true);
                        this.callFunction("UI|delete|setEnabled", false);
                    }
                    this.setValueForParm("COMPANY_CODE;PACKAGE_CODE;PAT_NAME;MR_NO;IDNO;SEX_CODE",
                            rowParm);//modify by wanglong 201212127
                    //add by wanglong 20121227
                    companyCode=rowParm.getValue("COMPANY_CODE");
                    TParm contractParm = contractD.onQueryByCompany(companyCode);
                    if (contractParm == null || contractParm.getCount() <= 0
                            || contractParm.getErrCode() != 0) {
                        this.messageBox_("û������");
                        return;
                    }
                    contract.setPopupMenuData(contractParm);
                    contract.setComboSelectRow();
                    contract.popupMenuShowData();
                    contractCode=rowParm.getValue("CONTRACT_CODE");
                    contract.setValue(contractCode);  
                    packageCode=rowParm.getValue("PACKAGE_CODE");
                }
            }
        } else {// ��������
            TParm result = HRMCompanyTool.getInstance().getContractDByMr(
                    companyCode, contractCode, mrNo);
            if (result.getErrCode() != 0) {
                this.messageBox_("��ѯʧ��");
                return;
            }
            if (result.getCount() < 1) {
                return;
            } else if (result.getCount() == 1) {
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (result.getValue("COVER_FLG").equals("Y")) {
                    report.setSelected(true);// ����"�ѱ���"��ѡ��
                    this.callFunction("UI|save|setEnabled", false);
                    if(dbaFlg){//��Ȩ���Ȳſ���ȡ������
                    	this.callFunction("UI|delete|setEnabled", true);
                    }
                } else {
                    unReport.setSelected(true);
                    this.callFunction("UI|save|setEnabled", true);
                    this.callFunction("UI|delete|setEnabled", false);
                }
                this.setValueForParm("PAT_NAME;MR_NO;IDNO;SEX_CODE", result);
            }
        }
    }
    
    /**
     * ���֤�Ų�ѯ
     */
    public void onQueryByIdNo() {
        if (StringUtil.isNullString(companyCode)) {
            this.messageBox_("������벻��Ϊ��");
            return;
        }
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("��ͬ���벻��Ϊ��");
            return;
        }
        String idNo = this.getValueString("IDNO");
        String isReport = this.getValueString("REPORT");
        TParm conDParm = HRMCompanyTool.getInstance().getContractDById(
                companyCode, contractCode, idNo, isReport);
        table.setParmValue(conDParm);
    }

    /**
     * �ѱ���
     */
    public void onReport() {
        onQueryAfterSave();
        this.callFunction("UI|save|setEnabled", false);
        if(dbaFlg){//��Ȩ���Ȳſ���ȡ������
        	this.callFunction("UI|delete|setEnabled", true);
        }
    }

    /**
     * δ����
     */
    public void onUnReport() {
        onQueryAfterSave();
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
    }
    
    /**
     * ȫѡ�¼�
     */
    public void onChooseAll() {
        if (table == null) {
            return;
        }
        TParm parm = table.getParmValue();
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
        table.setParmValue(parm);
    }

    /**
     * ɸѡ
     */
    public void onCustomizeChoose() {// modify by wanglong 20130206
        TParm parm = table.getParmValue();
        if (((TRadioButton) this.getComponent("SEQ_BUTTON")).isSelected()) {// ѡ�����
            if (this.getValueString("START_SEQ_NO").equals("")
                    && this.getValueString("END_SEQ_NO").equals("")) {
                onQueryAfterSave();
                return;
            }
            if (!this.getValueString("START_SEQ_NO").matches("\\-?[0-9]+")
                    || !this.getValueString("END_SEQ_NO").matches("\\-?[0-9]+")) {
                messageBox("����������");
                return;
            }
            int startSeq = this.getValueInt("START_SEQ_NO");
            int endSeq = this.getValueInt("END_SEQ_NO");
            if (startSeq > endSeq) {
                startSeq = startSeq + endSeq;
                endSeq = startSeq - endSeq;
                startSeq = startSeq - endSeq;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (parm.getInt("SEQ_NO", i) < startSeq || (parm.getInt("SEQ_NO", i) > endSeq)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        } else if (((TRadioButton) this.getComponent("AGE_BUTTON")).isSelected()) {// ѡ������ add by wanglong 20130225
            if (this.getValueString("START_SEQ_NO").equals("")
                    && this.getValueString("END_SEQ_NO").equals("")) {
                onQueryAfterSave();
                return;
            }
            if (!this.getValueString("START_SEQ_NO").matches("\\-?[0-9]+")
                    || !this.getValueString("END_SEQ_NO").matches("\\-?[0-9]+")) {
                messageBox("����������");
                return;
            }
            int startAge = this.getValueInt("START_SEQ_NO");
            int endAge = this.getValueInt("END_SEQ_NO");
            if (startAge > endAge) {
                startAge = startAge + endAge;
                endAge = startAge - endAge;
                startAge = startAge - endAge;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (parm.getInt("AGE", i) < startAge || (parm.getInt("AGE", i) > endAge)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        } else if (((TRadioButton) this.getComponent("SEX_BUTTON")).isSelected()) {// ѡ���Ա�
            String sexCode = this.getValueString("CHOOSE_SEX_CODE");
            if (sexCode.equals("")) {
                onQueryAfterSave();
                return;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (!parm.getValue("SEX_CODE", i).equals(sexCode)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        } else if (((TRadioButton) this.getComponent("MARRIAGE_BUTTON")).isSelected()) {// ѡ����� add by wanglong 20130225
            String marriageCode = this.getValueString("MARRIAGE_CODE");
            if (marriageCode.equals("")) {
                onQueryAfterSave();
                return;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (!parm.getValue("MARRIAGE_CODE", i).equals(marriageCode)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        }
    }

    /**
     * ����ɸѡ��ťѡ���¼�
     */
    public void onCustomizeButtonChoose() {// add by wanglong 20130225
        if (((TRadioButton) this.getComponent("SEQ_BUTTON")).isSelected()// ѡ�����
                || ((TRadioButton) this.getComponent("AGE_BUTTON")).isSelected()) {// ѡ������
            this.callFunction("UI|START_SEQ_NO|setEnabled", true);
            this.callFunction("UI|END_SEQ_NO|setEnabled", true);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", false);
        } else if (((TRadioButton) this.getComponent("SEX_BUTTON")).isSelected()) {// ѡ���Ա�
            this.callFunction("UI|START_SEQ_NO|setEnabled", false);
            this.callFunction("UI|END_SEQ_NO|setEnabled", false);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", true);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", false);
        } else if (((TRadioButton) this.getComponent("MARRIAGE_BUTTON")).isSelected()) {// ѡ�����
            this.callFunction("UI|START_SEQ_NO|setEnabled", false);
            this.callFunction("UI|END_SEQ_NO|setEnabled", false);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", true);
        }
    }
    
    /**
     * �����¼�
     */
    public void onSave() {
        TParm result =onExeParm();
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            return;
        }
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < result.getCount(); i++) {
            mrList += "'" + result.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        // ����Ԥ��ӡ���������start======================$$//
        // δ��ӡ��¼
        List unReportRecords = new ArrayList();
        List listHl7 = new ArrayList();
        // �ж�HRM_ADM���Ƿ�������ݣ��������ݵĻ��� ֻ����һ�� HRM_ADM D����ѱ�����־
        Timestamp now = adm.getDBTime();
        TParm resultParm = new TParm();
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // this.messageBox("==mrNo=="+mrNo);
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (!StringUtil.isNullString(caseNo)) {////////////////��չ�����ѱ���������״̬���绰��
                String tel = parmRow.getValue("TEL");
                resultParm = adm.updateCoverFlg(caseNo, now, tel);
                if (resultParm.getErrCode() < 0) {
                    continue;
                }
                resultParm = contractD.updateCoverFlg(companyCode, contractCode, mrNo, now, tel);
                if (resultParm.getErrCode() < 0) {
                    continue;
                }
                resultParm = contractD.updateTel(companyCode, contractCode, mrNo, tel);
                resultParm = contractD.updateSysTel(mrNo, tel);//wanglong add 20140512
                this.getHl7List(listHl7, caseNo);
                // ȷʵû��caseno˵�����ֳ�����,��¼�յ�CASE_NO
            } else {
                unReportRecords.add(parmRow);
            }
        }
        // $$==============add by lx 2012/05/22 end=======================$$//
        // ѭ������hrm_patadm,hrm_order,
        boolean flag = true;
        for (int i = 0; i < unReportRecords.size(); i++) {///////////////////δ������ִ�б����Ĳ�����
            TParm parmRow = (TParm) unReportRecords.get(i);
            String mrNo = parmRow.getValue("MR_NO");
            TParm patParm = new TParm();
            for (int j = 0; j < patInfoTparm.getCount(); j++) {// add by wanglong 20130304
                if (patInfoTparm.getValue("MR_NO", j).equals(mrNo)) {
                    patParm = patInfoTparm.getRow(j);
                }
            }
            patParm.setData("PAT_NAME", parmRow.getData("PAT_NAME"));
            patParm.setData("COMPANY_PAY_FLG", parmRow.getData("COMPANY_PAY_FLG"));
            String compCode = (String) parmRow.getData("COMPANY_CODE");
            String contraCode = (String) parmRow.getData("CONTRACT_CODE");
            String packCode = parmRow.getValue("PACKAGE_CODE");
            patParm.setData("COMPANY_CODE", compCode);
            patParm.setData("CONTRACT_CODE", contraCode);
            patParm.setData("PACKAGE_CODE", packCode);
            patParm.setData("REPORTLIST", parmRow.getData("REPORTLIST"));
            patParm.setData("INTRO_USER", parmRow.getData("INTRO_USER"));
            patParm.setData("DISCNT", parmRow.getData("DISCNT"));
            patParm.setData("TEL", parmRow.getData("TEL"));
            patParm.setData("MARRIAGE_CODE", parmRow.getData("MARRIAGE_CODE"));// add by wanglong 20130117
            patParm.setData("PAT_DEPT", parmRow.getData("PAT_DEPT"));// add by wanglong 20130225
            if (!adm.onNewAdm(patParm, now)) {
                this.messageBox_("���:" + parmRow.getData("SEQ_NO") + "  ������" + parmRow.getData("PAT_NAME") + ",����HRM_PATADM����ʧ��");
                flag = false;
                adm = new HRMPatAdm();
                adm.onQuery();
                order = new HRMOrder();
                order.onQuery("", "");
                continue;
            }
            String caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox_("���:" + parmRow.getData("SEQ_NO") + "  ������" + parmRow.getData("PAT_NAME") + ",ȡ������ʧ��");
                flag = false;
                adm = new HRMPatAdm();
                adm.onQuery();
                order = new HRMOrder();
                order.onQuery("", "");
                continue;
            }
            order.filt(caseNo);
            order.initOrderByTParm(packCode, caseNo, mrNo, contractCode, patParm);
            String tel = parmRow.getValue("TEL");
            if (!StringUtil.isNullString(tel)) {
                contractD.updateTel(compCode, contraCode, mrNo, tel);
                contractD.updateSysTel(mrNo, tel);//wanglong add 20140512
            }
            contractD.updateCoverFlg(companyCode, contractCode, mrNo, parmRow.getValue("TEL"));
            String[] sql = adm.getUpdateSQL();
            String updateAdm = // add by wanglong 20130408
                    "UPDATE HRM_PATADM SET COVER_FLG = 'Y', REPORT_DATE = TO_DATE( '"
                            + StringTool.getString(now, "yyyyMMddHHmmss")
                            + "', 'yyyyMMddHH24miss'), START_DATE = TO_DATE( '"
                            + StringTool.getString(now, "yyyyMMddHHmmss")
                            + "', 'yyyyMMddHH24miss'), END_DATE = TO_DATE( '"
                            + StringTool.getString(now, "yyyyMMddHHmmss")
                            + "', 'yyyyMMddHH24miss') WHERE CASE_NO = '" + caseNo + "'";
            sql = StringTool.copyArray(sql, new String[]{updateAdm });
            sql = StringTool.copyArray(sql, order.getUpdateSQL());
            sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm saveResult =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
            if (saveResult.getErrCode() != 0) {
                this.messageBox("���:" + parmRow.getData("SEQ_NO") + "  ����:" + parmRow.getData("PAT_NAME") + "\n����ʧ��");
                flag = false;
            } else {
                // this.messageBox_("�����ɹ�");
                this.getHl7List(listHl7, caseNo);
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        // ����HL7��Ϣ
        TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(listHl7);
        
        if (hl7Parm.getErrCode() < 0) {
            this.messageBox("����HL7��Ϣʧ��" + hl7Parm.getErrText());
            flag = false;
        }
        
        if (flag) {
            this.messageBox("�����ɹ�");
            
			// modify by wangb 2016/6/14 ����������屨���Զ���ӡ����(��������̩�����ջ�)
			String autoPrintSwitch = TConfig
					.getSystemValue("HRM.AUTO_PRINT.SWITCH");
			// Ŀǰ̩��ʹ���Զ���ӡ
			if ("Y".equalsIgnoreCase(autoPrintSwitch)) {
				// �����Ϲ�ѡ�Զ���ӡʱ���������Զ���ӡ�����������뵥������
				if (this.getComponent("AUTO_PRINT") != null
						&& ((TCheckBox) this.getComponent("AUTO_PRINT"))
								.isSelected()) {
					
					// ����ѡ���������룬���첢��ʼ��������ĺ�ͬ��ϢTTextFormat
					String roleType = this.getSelComRoleType();
			        
					// ����
					if (this.getPopedem("H") && "H".equals(roleType)) {
						// ��ӡ������
						this.onReportPrint("save");

						// ������ӡ���뵥
						this.onPrintExa();

						// ������ӡ����
						this.queryLisPrint();
					}

					// һ���ٴ�
					if (this.getPopedem("PIC") && "PIC".equals(roleType)) {
						// �Զ���ӡ���
//						this.printWristBands();
						
						// ������ӡ���뵥
						this.onPrintExa();

						// ������ӡ����
						this.queryLisPrint();
					}
				}
			}
            
//            this.setValue("REPORT", "Y");
        }
        // add by wanglong 20140214 ��ӡ������뵥
//        TParm parm = (TParm) listHl7.get(0);
//        TParm risPatParm = new TParm();
//        String[] s = parm.getNames();
//        for (int i = 0; i < listHl7.size(); i++) {
//            parm = (TParm) listHl7.get(i);
//            if (!parm.getValue("CAT1_TYPE").equals("RIS")) {
//                continue;
//            }
//            for (int j = 0; j < s.length; j++) {
//                risPatParm.addData(s[j], parm.getValue(s[j]));
//            }
//        }
//        risPatParm.setCount(risPatParm.getCount("CASE_NO"));
//        if (risPatParm.getCount() > 0) {
//            onPrintRIS(risPatParm);
//        }
        // add end
        
//        onReport();
        this.onQueryAfterSave();
        ((TTextField) this.getComponent("MR_NO")).requestFocus();// add by wanglong 20130117
    }

    /**
     * �õ�HL7����
     * @param listHl7 List
     * @param caseNo String
     * @return List
     */
    public List getHl7List(List listHl7, String caseNo) {
        String sql = "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,MR_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO,EXEC_DEPT_CODE FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='"
                + caseNo + "' AND SEND_FLG < 2 AND STATUS <> 9";
        //  System.out.println("SQLMED=="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        int rowCount = parm.getCount(); 
//      String preLabNo = "";
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
//          String labNo = temp.getValue("LAB_NO");
//          if(!preLabNo.equals(labNo)){//delete by wanglong 20130402
                temp.setData("ADM_TYPE", "H");
                temp.setData("FLG", "0");
                listHl7.add(temp);
//              preLabNo = labNo ;
//          }
        }
        return listHl7;
    }

    /**
     * չ��������Ŀ
     */
    public void onOpenOrder() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        for (int i = 0; i < countParm; i++) {
            if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parm.getValue("DEPT_CODE", i))) {
                continue;
            }
            for (int j = 0; j < names.length; j++) {
                result.addData(names[j], parm.getValue(names[j], i));
            }
        }
        result.setCount(result.getCount("MR_NO"));
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            return;
        }
        if (count >= 1000) {//add by wanglong 20130426
            this.messageBox_("ÿ�β�����Ҫ����1000��");
            return;
        }
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < result.getCount(); i++) {
            mrList += "'" + result.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        // ����δ������ ����HRM_ADM��������
        // add by lx Ԥ����
        Timestamp now = adm.getDBTime();
        boolean flag = true;
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (StringUtil.isNullString(caseNo)) {
                TParm patParm = new TParm();
                for (int j = 0; j < patInfoTparm.getCount(); j++) {// add by wanglong 20130304
                    if (patInfoTparm.getValue("MR_NO", j).equals(mrNo)) {
                        patParm = patInfoTparm.getRow(j);
                    }
                }
                patParm.setData("PAT_NAME", result.getData("PAT_NAME", i));
                patParm.setData("COMPANY_PAY_FLG", result.getData("COMPANY_PAY_FLG", i));
                patParm.setData("COMPANY_CODE", result.getData("COMPANY_CODE", i));
                patParm.setData("CONTRACT_CODE", result.getData("CONTRACT_CODE", i));
                String packCode = result.getValue("PACKAGE_CODE", i);
                patParm.setData("PACKAGE_CODE", packCode);
                patParm.setData("REPORTLIST", result.getData("REPORTLIST", i));
                patParm.setData("INTRO_USER", result.getData("INTRO_USER", i));
                patParm.setData("DISCNT", result.getData("DISCNT", i));
                patParm.setData("TEL", result.getData("TEL", i));
                patParm.setData("MARRIAGE_CODE", result.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
                patParm.setData("PAT_DEPT", result.getData("PAT_DEPT", i));// add by wanglong 20130225
                // 1.Ԥ����
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",Ԥ��������HRM_PATADM����ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    flag = false;
                    continue;
                }
                // 2.HRM_ORDER
                String admCaseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                // caseNo = admCaseNo1;
                if (StringUtil.isNullString(admCaseNo1)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",ȡ������ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    flag = false;
                    continue;
                }
                order.filt(admCaseNo1);
                order.initOrderByTParm(packCode, admCaseNo1, mrNo, contractCode, patParm);
                String[] sql = adm.getUpdateSQL();
                sql = StringTool.copyArray(sql, order.getUpdateSQL());
                sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
                // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("���:" + result.getData("SEQ_NO", i) + "  ����:" + result.getData("PAT_NAME", i) + "\nҽ��չ��ʧ��");
                }
            } else {
                result.setData("CASE_NO", i, caseNo);// add by wanglong 20130304
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        if (flag == true) {
            this.messageBox("ҽ��չ���ɹ�");
        }
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        onQueryAfterSave();
    }
    
    /**
     *  ���������Ŀ
     */
    public void onCopyOrder() {//add by wanglong 20130508
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("��ѡ��Ҫ�����ļ�¼");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        if (result.getCount("MR_NO") == 1) {
            String caseNo = "";
            if (StringUtil.isNullString(result.getValue("CASE_NO", 0))) {
                caseNo = adm.getLatestCaseNoBy(result.getValue("MR_NO", 0), result.getValue("CONTRACT_CODE", 0));
                if(!StringUtil.isNullString(caseNo)){
                    this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ����չ��");
                    return;
                }
            }else{
                this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ����չ��");
                return;
            }
  
        }
        // �����򿪶���
        Object o = this.openDialog("%ROOT%\\config\\hrm\\HRMCopyOrder.x", result);
        onQueryAfterSave();
    }
    
    /**
     * ȡ��չ������
     * ==================pangben 2013-3-10 
     */
    public void onCloseOrder() {
        TParm result = onExeParm();
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("û����Ҫ����������");
            return;
        }
        if (report.isSelected()) {
            this.messageBox("�ѱ�����Ա�޷�ȡ��չ��������");
            return;
        }
        for (int i = count - 1; i >= 0; i--) {
            TParm pat = result.getRow(i);
            String patName = pat.getValue("PAT_NAME");
            String mrNo = pat.getValue("MR_NO");
            String contractCode = pat.getValue("CONTRACT_CODE");
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE MR_NO='" + mrNo
                            + "' AND CONTRACT_CODE='" + contractCode + "' AND BILL_NO IS NOT NULL";
            TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
            if (result1.getErrCode() != 0) {
                this.messageBox("����û�����״̬ʧ��");
                return;
            }
            if (result1.getCount() >= 0 && !result1.getValue("BILL_NO", 0).equals("")) {
                this.messageBox(patName + "�ѽ��㣬����ȡ��չ��");
                result.removeRow(i);
            }
        }
        if (this.messageBox("��ʾ", "�Ƿ�ִ��ȡ��չ������", 2) != 0) {
            return;
        }
        result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onDeleteOrder",
                                             result);
        if (result.getErrCode() < 0) {
            this.messageBox("ȡ��չ��ʧ�� " + result.getErrText());
            return;
        }
        if (result.getValue("INDEX_MESSAGE").length() > 0) {
            this.messageBox(result.getValue("INDEX_MESSAGE"));
        } else {
            if (result.getValue("HL7_MESSAGE").length() > 0) {
                this.messageBox("Ա��:" + result.getValue("HL7_MESSAGE") + " ȡ��չ��ʧ�ܣ�����HL7��Ϣʧ�ܣ�");
            }
            if (result.getValue("PAT_MESSAGE").length() > 0) {
                this.messageBox("Ա��:" + result.getValue("PAT_MESSAGE") + " û����Ҫȡ��������");
            }
            this.messageBox("ȡ��չ���ɹ�");
        }
        onQueryAfterSave();
    }

    /**
     * ��Ҫִ�е�����
     * =================pangben 2013-3-10
     * 
     * @return
     */
    private TParm onExeParm() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        int countParm = parm.getCount();
        int index = 0;
        for (int i = 0; i < countParm; i++) {
            if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parm.getValue("DEPT_CODE", i))) {
                continue;
            }
            result.setRowData(index, parm, i);
            index++;
        }
        result.setCount(index);
        return result;
    }
    
    /**
     * ��ӡ������
     */
    public void onReportPrint(String type) {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        for (int i = 0; i < countParm; i++) {
            if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parm.getValue("DEPT_CODE", i))) {
                continue;
            }
            for (int j = 0; j < names.length; j++) {
                result.addData(names[j], parm.getValue(names[j], i));
            }
        }
        result.setCount(result.getCount("MR_NO"));
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            return;
        }
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < result.getCount(); i++) {
            mrList += "'" + result.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        // ����δ������ ����HRM_ADM��������
        // add by lx Ԥ����
        Timestamp now = adm.getDBTime();
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (StringUtil.isNullString(caseNo)) {
                TParm patParm = new TParm();
                for (int j = 0; j < patInfoTparm.getCount(); j++) {// add by wanglong 20130304
                    if (patInfoTparm.getValue("MR_NO", j).equals(mrNo)) {
                        patParm = patInfoTparm.getRow(j);
                    }
                }
                patParm.setData("PAT_NAME", result.getData("PAT_NAME", i));
                patParm.setData("COMPANY_PAY_FLG", result.getData("COMPANY_PAY_FLG", i));
                patParm.setData("COMPANY_CODE", result.getData("COMPANY_CODE", i));
                patParm.setData("CONTRACT_CODE", result.getData("CONTRACT_CODE", i));
                String packCode = result.getValue("PACKAGE_CODE", i);
                patParm.setData("PACKAGE_CODE", packCode);
                patParm.setData("REPORTLIST", result.getData("REPORTLIST", i));
                patParm.setData("INTRO_USER", result.getData("INTRO_USER", i));
                patParm.setData("DISCNT", result.getData("DISCNT", i));
                patParm.setData("TEL", result.getData("TEL", i));
                patParm.setData("MARRIAGE_CODE", result.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
                patParm.setData("PAT_DEPT", result.getData("PAT_DEPT", i));// add by wanglong 20130225
                // 1.Ԥ����
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������"
                            + result.getData("PAT_NAME", i) + ",Ԥ��������HRM_PATADM����ʧ��");
                    // pat = new HRMPatInfo();
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    continue;
                }
                // 2.HRM_ORDER
                String admCaseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                result.setData("CASE_NO", i, admCaseNo1);// add by wanglong 20130304
                if (StringUtil.isNullString(admCaseNo1)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",ȡ������ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    continue;
                }
                order.filt(admCaseNo1);
                order.initOrderByTParm(packCode, admCaseNo1, mrNo, contractCode, patParm);
                String[] sql = adm.getUpdateSQL();
                sql = StringTool.copyArray(sql, order.getUpdateSQL());
                sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
                // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("���:" + result.getData("SEQ_NO", i) + "  ����:" + result.getData("PAT_NAME", i) + "\nҽ��չ��ʧ��");
                }
            } else {
                result.setData("CASE_NO", i, caseNo);// add by wanglong 20130304
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        // ����δ��������ӡ�������
        // $$==============add by lx end Ԥ����====================$$//
        TParm errParm = new TParm();
        String message = "";
        for (int i = 0; i < count; i++) {
            // System.out.println("parm==="+parm);
        	
            TParm parmRow = result.getRow(i);
            String isVip = "";
        	if("2".equals(parmRow.getValue("IS_VIP"))){
        		isVip = "��";
        	}
            String mrNo = parmRow.getValue("MR_NO");
            // System.out.println("mrNo========="+mrNo);
            String caseNo = "";
            if (parmRow.getValue("CASE_NO").length() == 0) caseNo =
                    adm.getLatestCaseNoBy(mrNo, contractCode);
            else caseNo = parmRow.getValue("CASE_NO");
            // δ���ɾ���ţ����Բ��ܴ�ӡ?????
            if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
                errParm.addRowData(parm, i);
                message +=
                        "��ţ�" + parmRow.getValue("SEQ_NO") + "  ������" + parmRow.getValue("PAT_NAME") + "\n";
                continue;
            }
            // ����SQL
//          parmRow = order.getReportTParm(mrNo, caseNo);//=======================��õ���������
            parmRow = IReportTool.getInstance().getReportParm("HRMReportSheetNew.class", parmRow);//��õ���������modify by wanglong 20130730
            if (parmRow == null) {
                this.messageBox_("ȡ������ʧ��111");
                continue;
            }
            if (parmRow.getErrCode() != 0) {
                this.messageBox_("ȡ������ʧ��222");
                continue;
            }
            
            if(!StringUtil.isNullString(caseNo) && !StringUtil.isNullString(mrNo)){
            	TParm orderParm = new TParm(TJDODBTool.getInstance().select("SELECT MR_NO FROM HRM_ORDER WHERE ORDER_CODE = 'Y1003001' AND CASE_NO = '"+caseNo+"'"));
            	if(orderParm.getCount() > 0){
            		parmRow.setData("ACTION","TEXT","���ڱ��������Ҫ����С��Ҹβ���ѧ��顱����������ǩ��");
            	}
            }
            
            parmRow.setData("VIP","TEXT",isVip);
//            openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMReportSheetNew.jhw", parmRow,
//                            !this.getValueBoolean("PRINT_FLG"));
            openPrintDialog(IReportTool.getInstance().getReportPath("HRMReportSheetNew.jhw"),
                            parmRow, !this.getValueBoolean("PRINT_FLG"));//����ϲ�modify by wanglong 20130730
        }
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        
        if (!"save".equals(type)) {
        	onQueryAfterSave();
        }
        
        if (message != null && !message.equals("")) {
            this.messageBox(message + "����չ��������Ŀ!");
        }
    }

    /**
     * �����Ĳ�ѯ
     */
    public void onQueryAfterSave() {
        this.setValue("ALL", "N");
        this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE;PACKAGE_CODE;PY1;TEL");// add by wanglong 20121217
        onPackageAndSexChoose();// add by wanglong 20121217
    }

    /**
     * �����ӡ
     */
    public void onBarCode() {
        if (table == null) {
            return;
        }
        TParm tableParm = table.getParmValue();
        if (tableParm == null) {
            return;
        }
        int count = tableParm.getCount();
        if (count < 1) {
            return;
        }
        
        // add by wangb 2016/08/02 ����ѡ���������Ŀ������������ʽ
        String roleType = this.getSelComRoleType();
        
        HRMPatAdm patAdm = new HRMPatAdm();
        for (int i = 0; i < count; i++) {
            if (!TypeTool.getBoolean(tableParm.getData("CHOOSE", i))) {
                continue;
            }
            String caseNo = adm.getLatestCaseNoBy(tableParm.getValue("MR_NO", i), contractCode);
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox("�����Ϊ�գ����ȴ�ӡ�����������ȱ���");
                continue;
            }
            patAdm.onQueryByCaseNo(caseNo);
            TParm parm = new TParm();
            // ����
            parm.setData("DEPT_CODE", Operator.getDept());
            parm.setData("ADM_TYPE", "H");
            parm.setData("COMPANY_CODE", tableParm.getValue("COMPANY_CODE", i));//modify by wanglong 20130726
            parm.setData("CONTRACT_CODE", tableParm.getValue("CONTRACT_CODE", i));
            parm.setData("CASE_NO", caseNo);
            parm.setData("MR_NO", tableParm.getValue("MR_NO", i));
            parm.setData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
            parm.setData("ADM_DATE", patAdm.getItemData(0, "REPORT_DATE"));
            parm.setData("POPEDEM", "1");
            parm.setData("ROLE_TYPE", roleType);
            String value = (String) this.openDialog(
                    "%ROOT%\\config\\med\\MEDApply.x", parm);
        }
    }

    /**
     * �������뵥��ӡ����
     */
    public void onOpenExa() {// add by wanglong 20140214
        table.acceptText();
        TParm parmValue = table.getParmValue();
        String caseList = "";
        String mrNo = "";
        String patName = "";
        int count = 0;
        for (int i = 0; i < parmValue.getCount(); i++) {
            if ("N".equalsIgnoreCase(parmValue.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parmValue.getValue("CASE_NO", i))) {
                continue;
            }
            caseList += "'" + parmValue.getValue("CASE_NO", i) + "',";
            mrNo = parmValue.getValue("MR_NO", i);
            patName = parmValue.getValue("PAT_NAME", i);
            count++;
        }
        if (caseList.equals("")) {
            this.messageBox_("������");
            return;
        }
        TParm parm = new TParm();
        if (count >= 1000) {
            this.messageBox("�������ܳ���1000");
            return;
        } else if (count == 1) {
            parm.setData("MR_NO", mrNo);
            parm.setData("PAT_NAME", patName);
            parm.setData("CASE_NO", caseList.replaceAll("[^0-9]+", ""));
        }
        caseList = caseList.substring(0, caseList.length() - 1);
        parm.setData("CASE_NO", caseList);
        parm.setData("COMPANY_CODE", this.getValueString("COMPANY_CODE"));
        parm.setData("CONTRACT_CODE", this.getValueString("CONTRACT_CODE"));
        parm.setData("POPEDEM", "1");
        // add by wangb 2016/09/19 һ���ٴ�ȫ����ӡ�����������뵥
		String roleType = this.getSelComRoleType();
		parm.setData("ROLE_TYPE", roleType);
        this.openDialog("%ROOT%\\config\\hrm\\HRMRisPrint.x", parm);
    }
    
    /**
	 * ������ӡ���뵥
	 */
	public void onPrintExa() {// add by wanglong 20140214
		table.acceptText();
		TParm parm = table.getParmValue();
		String caseList = "";
		int count = 0;
		int dataCount = parm.getCount();
		String mrNoList = "";
		
		// δ��ǰչ�������ݱ���û������case_no�����ֱ�ӵ��������ť���ˢ��ץȡ����case_no
		for (int i = 0; i < dataCount; i++) {
			if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
				continue;
			}
			mrNoList = mrNoList + "'" + parm.getValue("MR_NO", i) + "',";
			count++;
		}
		
		if (mrNoList.length() == 0) {
			this.messageBox("������");
			return;
		} else {
			mrNoList = mrNoList.substring(0, mrNoList.length() - 1);
		}
		
		String patSql = "SELECT MR_NO,CASE_NO FROM HRM_PATADM WHERE MR_NO IN ("
				+ mrNoList + ") AND CONTRACT_CODE = '" + contractCode + "'";
		
		TParm patResult = new TParm(TJDODBTool.getInstance().select(patSql));
		if (patResult.getErrCode() < 0) {
			this.messageBox("��ѯ��������Ϣ����");
			err("��ѯ�����Ա��Ϣ����:" + patResult.getErrText());
			return;
		} else if (patResult.getCount() < 1) {
			this.messageBox("δ��ѯ����������Ϣ,��ȷ����չ��ҽ����Ŀ");
			return;
		} else {
			caseList = "'" + patResult.getValue("CASE_NO").replace("[", "").replace(
					"]", "").replace(" ", "").replace(",", "','") + "'";
		}
		
		if (caseList.equals("")) {
			this.messageBox_("������");
			return;
		}
		if (count >= 1000) {
			this.messageBox("�������ܳ���1000");
			return;
		}
		
		String orderList = "";
		String sql = "SELECT * FROM HRM_RISORDER";
		TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (orderParm.getErrCode() < 0) {
			this.messageBox(orderParm.getErrText());
			return;
		}
		for (int i = 0; i < orderParm.getCount(); i++) {
			orderList += "'" + orderParm.getValue("ORDER_CODE", i) + "',";
		}
		if (orderList.length() > 0) {
			orderList = orderList.substring(0, orderList.length() - 1);
		}
		String orderSql = "SELECT A.*, B.PAT_NAME, C.STAFF_NO "
				+ " FROM (SELECT A.CAT1_TYPE,A.CASE_NO,A.MR_NO, WM_CONCAT(A.SEQ_NO) SEQ_NO,A.EXEC_DEPT_CODE,A.DEV_CODE "
				+ "         FROM HRM_ORDER A      "
				+ "        WHERE CAT1_TYPE = 'RIS' "
				+ "          AND MED_APPLY_NO IS NOT NULL "
				+ "          AND A.CASE_NO IN (#) @ "
				+ "     GROUP BY A.CAT1_TYPE, A.CASE_NO, A.MR_NO, A.EXEC_DEPT_CODE, A.DEV_CODE) A,HRM_PATADM B,HRM_CONTRACTD C "
				+ " WHERE A.CASE_NO = B.CASE_NO      "
				+ "   AND B.COMPANY_CODE = C.COMPANY_CODE "
				+ "   AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "   AND B.MR_NO = C.MR_NO               "
				+ "ORDER BY C.SEQ_NO, A.DEV_CODE";
		orderSql = orderSql.replaceFirst("#", caseList);
		if (orderList.length() > 0) {
			orderSql = orderSql.replaceFirst("@", " AND A.ORDER_CODE NOT IN ("
					+ orderList + ") ");
		} else {
			orderSql = orderSql.replaceFirst("@", "");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(orderSql));
		if (result.getErrCode() != 0) {
			this.messageBox("��ѯҽ����Ϣ����");
			err("��ѯҽ����Ϣ����:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			return;
		}
		onPrintRIS(result);
	}
	
	
	 /**
     * ��ӡ������뵥
     */
    public void onPrintRIS(TParm parm) {// add by wanglong 20140214
        String sql = "SELECT * FROM HRM_SYSPARM";
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (sysParm.getErrCode() < 0) {
            this.messageBox(sysParm.getErrText());
            return;
        }
        ArrayList<String> deptList = new ArrayList<String>();
        // ���ݵ�ǰѡ���������Ϣ����ɫ����
        String roleType = this.getSelComRoleType();
        
        if (sysParm.getValue("RIS_PRINT", 0).equals("N")) {
            return;
        } else if (!sysParm.getValue("RIS_PRINT", 0).equals("Y")) {
            String deptSql = "SELECT * FROM HRM_RISDEPT";
            TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
            if (deptParm.getErrCode() < 0) {
                this.messageBox(deptParm.getErrText());
                return;
            }
            
            // �������Ҵ���(��ʱд��)
        	String dept = "0405";
            for (int i = 0; i < deptParm.getCount(); i++) {
            	// add by wangb 2016/09/19  һ���ٴ���ӡ���뵥�����ų���������
				if ("PIC".equals(roleType)
						&& dept.equals(deptParm.getValue("DEPT_CODE", i))) {
					continue;
				}
                deptList.add(deptParm.getValue("DEPT_CODE", i));
            }
        }
        pat = new HRMPatInfo();
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm inParam = parm.getRow(i);
            String seq = inParam.getValue("SEQ_NO");
            String where = "CASE_NO='" + inParam.getValue("CASE_NO") + "' AND SEQ_NO in(" + seq + ")";
            inParam.setData("ORDER_SEQ", seq);
            inParam.setData("EXEC_DEPT_DESC",
                            StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + inParam.getValue("EXEC_DEPT_CODE") + "'"));
            String descSql =
                    "SELECT WM_CONCAT(ORDER_CODE) ORDER_CODE,WM_CONCAT(ORDER_DESC) ORDER_DESC "
                            + " FROM HRM_ORDER WHERE #";
            descSql = descSql.replaceFirst("#", where);
            TParm descParm = new TParm(TJDODBTool.getInstance().select(descSql));
            if (descParm.getErrCode() < 0) {
                this.messageBox(descParm.getErrText());
                return;
            }
            inParam.setData("ORDER_CODE", descParm.getValue("ORDER_CODE", 0).replaceAll(",", "��"));
            inParam.setData("ORDER_DESC", descParm.getValue("ORDER_DESC", 0).replaceAll(",", "��"));
            String execDeptCode = StringUtil.getDesc("HRM_ORDER", "EXEC_DEPT_CODE", where);
            if (deptList.size() != 0 && deptList.contains(execDeptCode)) {
                continue;
            }
            TParm patInfo = pat.getHRMPatInfo(inParam.getValue("MR_NO"), inParam.getValue("CASE_NO"));
            inParam.setData("MR_NO", inParam.getValue("MR_NO"));
            inParam.setData("MR_NO", "TEXT", inParam.getValue("MR_NO"));
            inParam.setData("Barcode", "TEXT", inParam.getValue("MR_NO"));
            inParam.setData("CASE_NO", inParam.getValue("CASE_NO"));
            inParam.setData("SEQ", seq);//ҽ�����
            inParam.setData("PAT_NAME", patInfo.getValue("PAT_NAME", 0));
            inParam.setData("SEX_CODE", patInfo.getValue("SEX_CODE", 0));
            inParam.setData("SEX_DESC", patInfo.getValue("SEX_DESC", 0));
            inParam.setData("BIRTHDAY", StringTool.getString(patInfo.getTimestamp("BIRTHDAY", 0), "yyyy-MM-dd"));
            inParam.setData("AGE", StringTool.CountAgeByTimestamp(patInfo.getTimestamp("BIRTHDAY", 0), now)[0]);
            inParam.setData("COMPANY_CODE", patInfo.getValue("COMPANY_CODE", 0));
            inParam.setData("COMPANY_DESC", patInfo.getValue("COMPANY_DESC", 0));
            inParam.setData("CONTRACT_CODE", patInfo.getValue("CONTRACT_CODE", 0));
            inParam.setData("CONTRACT_DESC", patInfo.getValue("CONTRACT_DESC", 0));
            inParam.setData("SEQ_NO", patInfo.getValue("SEQ_NO", 0));//��Ա����ţ���ҽ�����
            inParam.setData("TEL", patInfo.getValue("TEL", 0));
            String orderSql = "SELECT * FROM HRM_ORDER WHERE # ORDER BY SEQ_NO";
            orderSql = orderSql.replaceFirst("#", where);
            TParm orderParm = new TParm(TJDODBTool.getInstance().select(orderSql));
            if (orderParm.getErrCode() < 0) {
                this.messageBox(orderParm.getErrText());
                return;
            }
            inParam.setData("DR_CODE", orderParm.getValue("DR_CODE", 0));//wanglong add 20140512
            inParam.setData("DR_DESC", StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='"
                    + orderParm.getValue("DR_CODE", 0) + "'"));// wanglong add 20140512
            inParam.setData("DEPT_CODE", patInfo.getValue("DEPT_CODE", 0));
            inParam.setData("DEPT_DESC", patInfo.getValue("DEPT_DESC", 0));
            inParam.setData("ORDER_DATE", StringTool.getString(now, "yyyy/MM/dd"));
            // һ���ٴ���ӡ���뵥���ص绰������ʾɸѡ��
            if ("PIC".equals(roleType)) {
            	inParam.setData("FILTER_NO", "TEXT", "ɸѡ��:" + inParam.getValue("STAFF_NO"));
            	inParam.setData("TEL", "");
            }
            this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMRisSheet.jhw", inParam, true);
        }
    }
    /**
     * ȡ�������¼�
     */
    public void onDelete() {
        if (unReport.isSelected()) {
            this.messageBox("�޷�ȡ��������");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ�ִ��ȡ����������", 2) != 0) {
            return;
        }
        TParm tableParm = table.getParmValue();
        int rowCount = tableParm.getCount();
        int rightCount = 0;// ��ȷ����
        for (int j = 0; j < rowCount; j++) {
            boolean chosen = tableParm.getBoolean("CHOOSE", j);
            if (!chosen) {
                continue;
            }
            TParm delParm = tableParm.getRow(j);
            String seqNo = delParm.getValue("SEQ_NO");
            String patName = delParm.getValue("PAT_NAME");
            String caseNo = delParm.getValue("CASE_NO");
            if (delParm.getValue("BILL_NO").length() != 0 || delParm.getInt("BILL_FLG") != 0) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " �Ѿ����㣬������ȡ��������");
                continue;
            }
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO='" + caseNo
                            + "' AND BILL_NO IS NOT NULL";
            TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
            if (result1.getErrCode() != 0) {
                this.messageBox("����û�����״̬ʧ��");
                return;
            }
            if (result1.getCount() >= 0 && !result1.getValue("BILL_NO", 0).equals("")) {
                this.messageBox(patName + " ����ҽ�������㣬����ȡ������");
                continue;
            }
            String mrNo = delParm.getValue("MR_NO");
            String companyCode = delParm.getValue("COMPANY_CODE");
            String contractCode = delParm.getValue("CONTRACT_CODE");
            HRMContractD contract = new HRMContractD();
            String conSql =
                    "SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='" + companyCode
                            + "' AND CONTRACT_CODE='" + contractCode + "' AND MR_NO='" + mrNo + "'";
            contract.setSQL(conSql);
            contract.retrieve();
            contract.setItem(0, "COVER_FLG", "N");
            contract.setItem(0, "REAL_CHK_DATE", "");
            HRMPatAdm patAdm = new HRMPatAdm();
            patAdm.onQueryByCaseNo(caseNo);
            patAdm.deleteRow(patAdm.rowCount() - 1);
            HRMOrder order = new HRMOrder();
            order.onQueryByCaseNo(caseNo, "N");
            int count = order.rowCount();
            for (int i = count - 1; i > -1; i--) {
                order.deleteRow(i);
            }
            //Hl7����ȡ��ִ�е���Ϣ��med_apply����status=9,send_flg=1��
            List hl7ParmDel = new ArrayList();
            TParm orderDelParm = order.getBuffer(order.DELETE);
            int rowDelOrderCount = orderDelParm.getCount();
            for (int i = 0; i < rowDelOrderCount; i++) {
                TParm delTemp = new TParm();
                TParm tempDel = orderDelParm.getRow(i);
                if ("Y".equals(tempDel.getValue("SETMAIN_FLG"))
                        && tempDel.getValue("MED_APPLY_NO").length() != 0) {
                    delTemp.setData("ADM_TYPE", "H");
                    delTemp.setData("PAT_NAME", tableParm.getValue("PAT_NAME", j));
                    delTemp.setData("CAT1_TYPE", tempDel.getValue("CAT1_TYPE"));
                    delTemp.setData("CASE_NO", tempDel.getValue("CASE_NO"));
                    delTemp.setData("LAB_NO", tempDel.getValue("MED_APPLY_NO"));
                    delTemp.setData("ORDER_NO", tempDel.getValue("CASE_NO"));
                    delTemp.setData("SEQ_NO", tempDel.getValue("SEQ_NO"));
                    delTemp.setData("FLG", "1");
                    try {
                        if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")
                                && this.messageBox("��ʾ", patName + " ���ּ����ִ�У��Ƿ����ɾ������������ҽ����", 2) != 0) {
                            continue;
                        }
                    }
                    catch (Exception ex) {
                        System.err.print("�����ִ���ж�ʧ�ܡ�");
                        ex.printStackTrace();
                    }
                    hl7ParmDel.add(delTemp);
                }
            }
            if (hl7ParmDel.size() > 0) {// modify by wanglong 20130408
                TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ParmDel);
                if (hl7Parm.getErrCode() < 0) {
                    this.messageBox(patName + " ȡ������ʧ��(HL7��Ϣ����ʧ��) " + hl7Parm.getErrText());
                    continue;
                }
            }
            String[] sql = patAdm.getUpdateSQL();
            sql = StringTool.copyArray(sql, order.getUpdateSQL());
            sql = StringTool.copyArray(sql, contract.getUpdateSQL());
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm saveResult =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
            if (saveResult.getErrCode() != 0) {
                this.messageBox(patName + " ȡ������ʧ��");
                continue;
            }
            rightCount++;
        }
        if (rightCount > 0) {
            this.messageBox("ȡ�������ɹ�");
            this.setValue("UNREPORT", "Y");
            onUnReport();
        }
    }

    /**
     * ����¼�
     */
    public void onClear() {
        initData();
        table.setParmValue(new TParm());
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("PAT_NAME", "");
        this.setValue("MR_NO", "");
        this.setValue("IDNO", "");
        this.setValue("SEX_CODE", "");
        this.setValue("UNREPORT", "Y");
        this.setValue("ALL", "N");
        this.setValue("PRINT_FLG", "N");
        this.setValue("PACKAGE_CODE", "");
        this.setValue("START_SEQ_NO", "");
        this.setValue("END_SEQ_NO", "");
        this.setValue("CHOOSE_SEX_CODE", "");// add by wanglong 20130206
        this.setValue("PY1", "");
        this.setValue("TEL", "");
        this.callFunction("UI|SEQ_BUTTON|setSelected", true);// add by wanglong 20130206
        this.callFunction("UI|START_SEQ_NO|setEnabled", true);// add by wanglong 20130206
        this.callFunction("UI|END_SEQ_NO|setEnabled", true);// add by wanglong 20130206
        this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);// add by wanglong 20130206
        this.setValue("COUNT", "");
        companyCode = null;
        contractCode = null;
//      patName.getPopupMenuData().getData().clear();
//      patName.filter();
//      contract.getPopupMenuData().getData().clear();
//      contract.filter();
        patName.setPopupMenuData(new TParm());
        patName.filter();
//        patName.popupMenuShowData();
        contract.setPopupMenuData(new TParm());
        contract.filter();
//        contract.popupMenuShowData();
    }

    /**
     * ��������
     */
    public void batchAdd() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("��ѡ��Ҫ�����ļ�¼");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        result.addData("METHOD", "ADD");
        if (result.getCount("MR_NO") == 1) {// add by wanglong 20130422 ��ֻѡ��һ����ʱ����ǰ��ʾ�Ƿ��Ѿ����㡣
            String billSql =
                    "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                            + " WHERE A.CASE_NO = B.CASE_NO AND B.MR_NO = '#' "
                            + " AND B.CONTRACT_CODE = '#'";
            billSql = billSql.replaceFirst("#", result.getValue("MR_NO", 0));
            billSql = billSql.replaceFirst("#", result.getValue("CONTRACT_CODE", 0));
            TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
            if (billParm.getErrCode() != 0) {
                this.messageBox("��������Ϣʧ�� " + billParm.getErrText());
                return;
            }
            if (billParm.getCount() > 1
                    || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                            .getValue("BILL_NO", 0).trim()))) {
                this.messageBox("������" + result.getValue("PAT_NAME", 0) + " �ѽ��㣬����������");
                return;
            } else if (billParm.getCount() < 1) {
                String patSql =
                        "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
                patSql = patSql.replaceFirst("#", result.getValue("MR_NO", 0));
                patSql = patSql.replaceFirst("#", result.getValue("CONTRACT_CODE", 0));
                TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong 20130428
                if (patParm.getErrCode() != 0) {
                    this.messageBox("���ҽ��չ��״̬ʧ�� " + patParm.getErrText());
                    return;
                }
                if (patParm.getCount() < 1) {
                    this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ��δչ��������������");
                    return;
                }
            }
        }
        // �����򿪶���
        Object o = this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
        // �������ɹ���Ҫˢ��pat����
        if (o != null && !o.equals("")) {
            adm = new HRMPatAdm();
            adm.onQuery();
        }

    }

    /**
     * ����ɾ��
     */
    public void batchDelete() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("��ѡ��Ҫɾ���ļ�¼");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        result.addData("METHOD", "DELETE");
        this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
    }

    /**
     * ���˲���
     */
    public void onSingleOpt() {//wanglong add 20140829
        table.acceptText();
        TParm parm = table.getParmValue();
        int count = -1;
        for (int i = 0; i < parm.getCount(); i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
                if (count > -1) {
                    this.messageBox("��֧�ֵ��˲���");
                    return;
                }
                count = i;
            }
        }
        if (count < 0) {
            this.messageBox("��ѡ��Ҫ��������Ա");
            return;
        }
        String patSql = "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
        patSql = patSql.replaceFirst("#", parm.getValue("MR_NO", count));
        patSql = patSql.replaceFirst("#", parm.getValue("CONTRACT_CODE", count));
        TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong
                                                                           // 20130428
        if (patParm.getErrCode() != 0) {
            this.messageBox("���ҽ��չ��״̬ʧ�� " + patParm.getErrText());
            return;
        }
        if (patParm.getCount() < 1) {
            this.messageBox("������" + parm.getValue("PAT_NAME", count) + " ҽ��δչ��������������");
            return;
        }
        String billSql = "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '#'";
        billSql = billSql.replaceFirst("#", parm.getValue("CASE_NO", count));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("��ѯ������Ϣʧ�� " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("����Ա�ѽ��㣬������������");
            return;
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMSingleOperation.x", parm.getRow(count));
    }
    
    /**
     * ���µ绰         add by wanglong 20130110
     */
    public void onUpdateTel(){
        table.acceptText();
        TParm tableParm = table.getParmValue();
        TParm tableShowParm = table.getShowParmValue();
        for (int i = 0; i < tableShowParm.getCount(); i++) {
            tableParm.setData("TEL", i, tableShowParm.getData("TEL", i));
        }
        //System.out.println("===========update Tel========="+tableParm);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction",
                                             "updateHRMPatTEL", tableParm);
        if (result.getErrCode() != 0) {
            this.messageBox("" + result.getErrText());
            return;
        } else if (!result.getValue("MR_NO").equals("")) {
            String patNames = "";
            for (int i = 0; i < result.getCount(); i++) {
                patNames += result.getValue("PAT_NAME", i) + ",";
            }
            patNames = patNames.substring(0, patNames.length() - 1);
            messageBox(patNames + "\n����绰ʧ�ܣ�");
        } else {
            this.messageBox("P0001");// ����ɹ�
        }
        
    }
    
    /**
     * ���Excel
     */
    public void onExcel() {// add by wanglong 20130206
        if (table.getRowCount() <= 0) {
            this.messageBox("E0116");
            return;
        }
        if (unReport.isSelected()) {
            ExportExcelUtil.getInstance().exportExcel(table, "��������δ������Ա�б�");
        } else {
            ExportExcelUtil.getInstance().exportExcel(table, "���������ѱ�����Ա�б�");
        }
    }
    
    // ====================������begin======================add by wanglong 20121217
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
	 * �����Ҳ���Ʒ�
	 *//*
	public void onOperation() {
		//===================================================���Ƶ��˲��� start
		table.acceptText();
        TParm parm = table.getParmValue();
        int count = -1;
        for (int i = 0; i < parm.getCount(); i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
                if (count > -1) {
                    this.messageBox("��֧�ֵ��˲���");
                    return;
                }
                count = i;
            }
        }
        if (count < 0) {
            this.messageBox("��ѡ��Ҫ��������Ա");
            return;
        }
        String patSql = "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
        patSql = patSql.replaceFirst("#", parm.getValue("MR_NO", count));
        patSql = patSql.replaceFirst("#", parm.getValue("CONTRACT_CODE", count));
        TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong
                                                                           // 20130428
        if (patParm.getErrCode() != 0) {
            this.messageBox("���ҽ��չ��״̬ʧ�� " + patParm.getErrText());
            return;
        }
        if (patParm.getCount() < 1) {
            this.messageBox("������" + parm.getValue("PAT_NAME", count) + " ҽ��δչ��������������");
            return;
        }
        String billSql = "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '#'";
        billSql = billSql.replaceFirst("#", parm.getValue("CASE_NO", count));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("��ѯ������Ϣʧ�� " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("����Ա�ѽ��㣬������������");
            return;
        }
		//=======================================================���Ƶ��˲��� end
        caseNo=parm.getValue("CASE_NO", count);
        mrNo=parm.getValue("MR_NO",count);
        String rateSql="SELECT DISCOUNT_RATE FROM HRM_ORDER WHERE CONTRACT_CODE='"+this.contractCode+"' AND CASE_NO='"+caseNo+"' AND MR_NO='"+mrNo+"'";
        TParm rateParm=new TParm(TJDODBTool.getInstance().select(rateSql));
        double discountRate=rateParm.getDouble("DISCOUNT_RATE",0);
		TParm operationParm = new TParm();
		TParm dataParm = new TParm();
		dataParm.setData("CASE_NO",parm.getValue("CASE_NO", count));
		dataParm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", dataParm,
				false);
		
		if (null==operationParm) {//==pangben  2013-08-05
			return;
		}
		TParm parm_obj = new TParm();
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '"
				+ operationParm.getValue("ORDER_CODE", i) + "' AND ACTIVE_FLG = 'Y'";
			parm_obj = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm_obj == null || parm_obj.getCount() <= 0) {
				continue;
			}
			int seqNo = order.getOrderMaxSeqNo(caseNo);// ��һ���
	        int orderGroupNo = order.getOrderMaxGroupNo(caseNo)+1;
			if(!insertHrmData(parm_obj.getRow(0),seqNo,orderGroupNo,operationParm.getDouble("DOSAGE_QTY",i),operationParm.getValue("DOSAGE_UNIT",i),discountRate)){
				this.messageBox("�Ʒ�ʧ��");
				return;
			}
		}
		this.messageBox("�Ʒѳɹ�");
	}
	*//**
	 * ��������
	 *//*
	public boolean insertHrmData(TParm parm,int seqNo,int orderGroupNo,double dosageQty,String dosageUnit,double discountRate){
		String sql="INSERT INTO HRM_ORDER(CASE_NO,SEQ_NO,REGION_CODE,MR_NO,ORDER_CODE," +
				" ORDER_DESC,GOODS_DESC,SPECIFICATION,ORDER_CAT1_CODE,CAT1_TYPE," +
				" DISPENSE_QTY,DISPENSE_UNIT,OWN_PRICE,DISCOUNT_RATE," +
				" OWN_AMT,AR_AMT,DR_CODE,ORDER_DATE,DEPT_CODE,EXEC_DEPT_CODE," +
				" SETMAIN_FLG,ORDERSET_GROUP_NO,ORDERSET_CODE,HIDE_FLG,FILE_NO," +
				" URGENT_FLG,REXP_CODE,HEXP_CODE,CONTRACT_CODE,DCT_TAKE_QTY," +
				" PACKAGE_TOT,REQUEST_FLG,OPT_USER,OPT_DATE," +
				" OPT_TERM,MEDI_QTY) " +
				"VALUES('"+caseNo+"',"+seqNo+",'"+Operator.getRegion()+"','"+mrNo+"','"+parm.getValue("ORDER_CODE")+"'," +
						"'"+parm.getValue("ORDER_DESC")+"','"+parm.getValue("GOODS_DESC")+"','"+parm.getValue("SPECIFICATION")+"','"+parm.getValue("ORDER_CAT1_CODE")+"','"+parm.getValue("CAT1_TYPE")+"'," +
						""+dosageQty+",'"+parm.getValue("UNIT_CODE")+"',"+parm.getDouble("OWN_PRICE")+","+discountRate+"," +
						""+dosageQty*parm.getDouble("OWN_PRICE")+","+dosageQty*parm.getDouble("OWN_PRICE")*discountRate+",'"+Operator.getID()+"',sysDate,'"+Operator.getDept()+"','"+Operator.getDept()+"'," +
						"'Y',"+orderGroupNo+",'"+parm.getValue("ORDER_CODE")+"','N',0," +
						"'N','010.02','2E0.002',"+this.contractCode+",0," +
						"0,'Y','"+Operator.getID()+"',sysDate,'"+Operator.getIP()+"',"+parm.getDouble("MEDI_QTY")+")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			this.messageBox(""+result.getErrText());
			return false;
		}
		return true;
	}*/
    
	/**
	 * ��ȡ�������֤
	 * 
	 * @author wangb 2016/4/21
	 */
	public void onIdCard() {
		// ��ȡ���֤
		TParm idParm = IdCardO.getInstance().readIdCard();
		
		if (StringUtils.isNotEmpty(idParm.getValue("MESSAGE"))) {
			this.messageBox(idParm.getValue("MESSAGE"));
		}
		
		if (idParm.getErrCode() < 0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		
		if (idParm.getCount() > 1) {// ����������ʾ
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
					idParm);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				this.setValue("MR_NO", patParm.getValue("MR_NO"));
				this.setValue("IDNO", patParm.getValue("IDNO"));
				this.setValue("PAT_NAME", patParm.getValue("PAT_NAME"));
			}
		} else {
			this.setValue("PAT_NAME", idParm.getValue("PAT_NAME", 0));
			this.setValue("IDNO", idParm.getValue("IDNO", 0));
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
		}
		
		// ����ָ��������ѯ����������Ϣ
		this.queryPatReportInfo("IDNO");
	}
	
	/**
	 * ����ָ��������ѯ����������Ϣ
	 * 
	 * @author wangb 2016/4/25
	 */
	public void queryPatReportInfo(String type) {
		String patName = ((TTextFormat)getComponent("PAT_NAME")).getText();
		if (StringUtils.equals(type, "PAT_NAME")) {
			if (StringUtils.isEmpty(patName)) {
				return;
			}
		} else if (StringUtils.equals(type, "MR_NO")) {
			String mrNo = this.getValueString("MR_NO").trim();
	        if (mrNo.equals("")) {
	            return;
	        }
	        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
	        this.setValue("MR_NO", mrNo);
	        
	        // modify by huangtt 20160929 EMPI���߲�����ʾ start
    		Pat pat = Pat.onQueryByMrNo(mrNo);
    		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
    			mrNo = pat.getMrNo();
    			this.setValue("MR_NO", mrNo);// ������
    		}
    		// modify by huangtt 20160929 EMPI���߲�����ʾ end
	        
		} else {
			if (StringUtils.isEmpty(this.getValueString(type))) {
				return;
			} 
		}
		
		TParm parm = getParmForTag("COMPANY_CODE;CONTRACT_CODE;MR_NO;PAT_NAME;IDNO;PY1;TEL");
		// modify by wangb 2016/6/1 Ϊ�����û�����ϰ����ϵͳ�Զ��жϱ���״̬
		// ����״̬
//		if (report.isSelected()) {
//			parm.setData("COVER_FLG", "Y");
//		} else {
//			parm.setData("COVER_FLG", "N");
//		}
		
		if (StringUtils.isNotEmpty(patName)) {
			parm.setData("PAT_NAME", patName);
		}
		
		// ��ѯ���챨����Ϣ
		TParm result = HRMCompanyTool.getInstance().selectContractCoverInfo(parm);
		
        // add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� START
        String filter = "";
        if (!this.getPopedem("SYSDBA")) {
        	filter = this.getPopedemParm().getValue("ID");
        	int count = result.getCount();
        	for (int i = count - 1; i > -1; i--) {
				if (!filter.contains(result.getValue("ROLE_TYPE",
						i))) {
					result.removeRow(i);
				}
        	}
        }
     	// add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� END
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ������Ϣ����");
			err("ERR:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("���޴˲���");
			return;
		} else if (result.getCount() == 1) {
			// ����ѯ���ı�����Ϣ����ҳ��ؼ�
			this.setValueToPage(result);
		} else {
			Object obj = this.openDialog("%ROOT%\\config\\hrm\\HRMPatRecord.x",
					result);
			if (obj != null) {
				TParm selRowParm = (TParm) obj;
				// ����ѯ���ı�����Ϣ����ҳ��ؼ�
				this.setValueToPage(selRowParm);
			}
		}
	}
	
	/**
	 * ����ѯ���ı�����Ϣ����ҳ��ؼ�
	 * 
	 * @author wangb 2016/4/25
	 */
	private void setValueToPage(TParm parm) {
		table.setParmValue(parm);
        table.setSelectedRow(0);
        parm = parm.getRow(0);
        if (parm.getValue("COVER_FLG").equals("Y")) {
            report.setSelected(true);// ����"�ѱ���"��ѡ��
            this.callFunction("UI|save|setEnabled", false);
            if(dbaFlg){//��Ȩ���Ȳſ���ȡ������
            	this.callFunction("UI|delete|setEnabled", true);
            }
        } else {
            unReport.setSelected(true);
            this.callFunction("UI|save|setEnabled", true);
            this.callFunction("UI|delete|setEnabled", false);
        }
        this.setValueForParm("COMPANY_CODE;PACKAGE_CODE;PAT_NAME;MR_NO;IDNO;SEX_CODE;PY1;TEL", parm);
		companyCode = parm.getValue("COMPANY_CODE");
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm == null || contractParm.getCount() <= 0
                || contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
            return;
        }
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = parm.getValue("CONTRACT_CODE");
		contract.setValue(contractCode);
		packageCode = parm.getValue("PACKAGE_CODE");
		
		// ����
        TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
        patName.setPopupMenuData(patParm);
        patName.setComboSelectRow();
        patName.popupMenuShowData();
	}
	
    /**
     * ����������Ŀ����ӡ����
     * 
     * @author wangb 2016/4/25
     */
    public void queryLisPrint() {
    	TParm parm = table.getParmValue();
    	TParm lisParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
				continue;
			}
			
			// ��ѯָ�������ļ�����Ŀ��Ϣ
			lisParm = HRMCompanyTool.getInstance().selectMedApplyInfo(parm.getRow(i));
			
			// �Զ���ӡ��������
			this.onAutoPrintBarCode(lisParm);
		}
    }
    
    /**
     * �Զ���ӡ��������
     * 
     * @author wangb 2016/4/25
     */
	private void onAutoPrintBarCode(TParm lisParm) {
		int rowCount = lisParm.getCount();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String bedNo = "";
		
		// ȡ��LIS����ҽ��
		MedApply medApply = new MedApply();
        TParm lisMergeOrder = medApply.getLisMergeOrder(lisParm.getValue("CASE_NO", 0));
        if (lisMergeOrder.getErrCode() < 0) {
        	this.messageBox("ȡ�ü������ҽ��ʧ��");
            return;
        }
        Map<String, String> lisMergeMap = new HashMap<String,String>();
        for (int i = 0; i < lisMergeOrder.getCount(); i++) {
			lisMergeMap.put(lisMergeOrder.getValue("APPLICATION_NO", i),
					lisMergeOrder.getValue("ORDER_DESC", i));
        }
        List<String> applicationNoList = new ArrayList<String>();
        
        // add by wangb 2016/09/19 ����ѡ���������Ŀ������������ʽ
        String roleType = this.getSelComRoleType();

		if (rowCount > 0) {
			TParm printParm = new TParm();
			List printSize = new ArrayList();
			String orderDesc = "";
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
			String applyNo = ""; // chenxi �����
			String drNote = ""; // chenxi ҽʦ��ע
			String filterNo = "";// ɸѡ��
			String planNo = "";// ������
			
			for (int i = 0; i < rowCount; i++) {
				TParm temp = lisParm.getRow(i);
				applyNo = temp.getValue("APPLICATION_NO");
				// add by wangb 2016/6/1 ��ͬ����ŵļ�����Ŀֻ��ӡһ��
				if (!applicationNoList.contains(applyNo)) {
					applicationNoList.add(applyNo);
				} else {
					continue;
				}
				patName = temp.getValue("PAT_NAME");
				deptExCode = temp.getValue("EXEC_DEPT_CODE");
				deptCode = temp.getValue("DEPT_CODE");
				stationCode = temp.getValue("STATION_CODE");
				drNote = temp.getValue("DR_NOTE");
				urgentFlg = geturGentFlg(applyNo).equals("Y") ? "(��)" : "";
				orderDate = String.valueOf(sysDate).substring(0, 19)
						.replaceAll("-", "/");
				optItemDesc = temp.getValue("OPTITEM_CHN_DESC");
				mrNo = temp.getValue("MR_NO");
				sexDesc = this.getDictionary("SYS_SEX", temp
						.getValue("SEX_CODE"));
				age = StringTool.CountAgeByTimestamp(temp
						.getTimestamp("BIRTH_DATE"), sysDate)[0];
				orderDesc = lisMergeMap.get(applyNo).replace("��", "");
				
				if ("PIC".equals(roleType)) {
					filterNo = temp.getValue("STAFF_NO");
					planNo = temp.getValue("PLAN_NO");
				}

				printParm = new TParm();
				printParm.setData("APPLICATION_NO", "TEXT", applyNo);
				if (StringUtils.isNotEmpty(filterNo)) {
					filterNo = filterNo + "-";
				}
				printParm.setData("PAT_NAME", "TEXT", filterNo + patName);
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
				// add by wangb 2016/09/19 һ���ٴ�������
				printParm.setData("PLAN_NO", "TEXT", planNo);
				printSize.add(printParm);
			}

			int listRowCount = printSize.size();
			String paramSql = "SELECT * FROM MED_PRINTER_LIST WHERE PRINTER_TERM='#'"
					.replaceFirst("#", Operator.getIP());
			TParm printParam = new TParm(TJDODBTool.getInstance().select(
					paramSql));
			if (printParam.getErrCode() < 0) {
				this.messageBox("��ȡ��ӡ��������");
				return;
			}
			String printerPort = printParam.getValue("PRINTER_PORT", 0);
			boolean zebraFlg = printParam.getBoolean("ZEBRA_FLG", 0)
					&& !printerPort.equals("");
			for (int i = 0; i < listRowCount; i++) {
				TParm pR = (TParm) printSize.get(i);
				pR.setData("EXEC_DEPT_CODE", "TEXT", getDeptDesc(pR.getValue(
						"EXEC_DEPT_CODE", "TEXT")));
				pR.setData("DEPT_CODE", "TEXT", getDeptDesc(pR.getValue(
						"DEPT_CODE", "TEXT"))
						+ "("
						+ getStationDesc(pR.getValue("STATION_CODE", "TEXT"))
						+ ")");
				if (!zebraFlg) {
					pR = IReportTool.getInstance().getReportParm(
							"Med_ApplyPrint.class", pR);// ����ϲ�
					// this.openPrintDialog("%ROOT%\\config\\prt\\MED\\Med_ApplyPrint.jhw",
					// pR, true);
					// modify by wangb 2016/09/19 һ���ٴ�ʹ�õ�����������ʽ
					if ("PIC".equals(roleType)) {
						this
								.openPrintDialog(
										"%ROOT%\\config\\prt\\MED\\Med_ApplyPrintForPIC.jhw",
										pR, true);
					} else {
						this.openPrintDialog(IReportTool.getInstance()
								.getReportPath("Med_ApplyPrint.jhw"), pR, true);// ����ϲ�
					}
				} else {
					boolean noFontFlg = printParam.getBoolean("NOFONT_FLG", 0);
					offset_x = printParam.getInt("OFFSET_X", 0);
					offset_y = printParam.getInt("OFFSET_Y", 0);
					this.printText = new StringBuffer();// wanglong add 20140610
					this.addBarcode(101, 0, 2, 3.0, 95, pR.getValue(
							"APPLICATION_NO", "TEXT"));
					this.addText(5, 115, pR.getValue("MR_NO", "TEXT"));
					if (!noFontFlg) {
						this.addText(191, 115, pR.getValue("PAT_NAME", "TEXT"));
						this.addText(271, 115, pR.getValue("SEX_DESC", "TEXT"));
						// AGE
						this.addText(310, 115, pR.getValue("BED_NO", "TEXT"));
						this.addText(5, 139, "�ͼ���:"
								+ pR.getValue("EXEC_DEPT_CODE", "TEXT"));
						this.addText(191, 139, "����:"
								+ pR.getValue("DEPT_CODE", "TEXT"));
						// STATION_CODE
						this.addText(5, 164, pR.getValue("ORDER_DESC", "TEXT"));
						// OPTITEM_CHN_DESC
						this.addText(5, 188, "����ʱ��:"
								+ pR.getValue("ORDER_DATE", "TEXT"));
						this.addText(5, 213, "ҽʦ��ע:"
								+ pR.getValue("DR_NOTE", "TEXT"));
						this
								.addText(350, 80, pR.getValue("URGENT_FLG",
										"TEXT"));// (��)
					} else {
						TParm parm = new TParm();
						parm.setData(pR.getValue("PAT_NAME", "TEXT"), "");
						parm.setData(pR.getValue("SEX_DESC", "TEXT"), "");
						parm.setData(pR.getValue("BED_NO", "TEXT"), "");
						parm.setData("�ͼ���:"
								+ pR.getValue("EXEC_DEPT_CODE", "TEXT"), "");
						parm.setData("����:" + pR.getValue("DEPT_CODE", "TEXT"),
								"");
						parm.setData(pR.getValue("ORDER_DESC", "TEXT"), "");
						parm.setData("����ʱ��:"
								+ pR.getValue("ORDER_DATE", "TEXT"), "");
						parm.setData("ҽʦ��ע:" + pR.getValue("DR_NOTE", "TEXT"),
								"");
						parm.setData(pR.getValue("URGENT_FLG", "TEXT"), "");
						TParm result = TIOM_AppServer.executeAction(
								"action.med.MedAction", "getCHNControlCode",
								parm);
						this.addGraphTextCode(171, 116, result.getValue(pR
								.getValue("PAT_NAME", "TEXT")));
						this.addGraphTextCode(266, 116, result.getValue(pR
								.getValue("SEX_DESC", "TEXT")));
						// AGE
						this.addGraphTextCode(310, 116, result.getValue(pR
								.getValue("BED_NO", "TEXT")));
						this.addGraphTextCode(5, 140, result.getValue("�ͼ���:"
								+ pR.getValue("EXEC_DEPT_CODE", "TEXT")));
						this.addGraphTextCode(171, 140, result.getValue("����:"
								+ pR.getValue("DEPT_CODE", "TEXT")));
						// STATION_CODE
						this.addGraphTextCode(5, 164, result.getValue(pR
								.getValue("ORDER_DESC", "TEXT")));
						// OPTITEM_CHN_DESC
						this.addGraphTextCode(5, 188, result.getValue("����ʱ��:"
								+ pR.getValue("ORDER_DATE", "TEXT")));
						this.addGraphTextCode(5, 212, result.getValue("ҽʦ��ע:"
								+ pR.getValue("DR_NOTE", "TEXT")));
						this.addGraphTextCode(350, 80, result.getValue(pR
								.getValue("URGENT_FLG", "TEXT")));// (��)

						this.printText.append("^IDOUTSTR01^FS");// ���ͼ��
					}
					if (!printBarCode(printerPort)) {// �������ӡ����
						return;
					}
				}
				String[] sqlMedApply = new String[] { "UPDATE MED_APPLY SET PRINT_FLG='Y',PRINT_DATE=SYSDATE,OPT_DATE=SYSDATE,OPT_USER='"
						+ Operator.getID()
						+ "',OPT_TERM='"
						+ Operator.getIP()
						+ "' WHERE CAT1_TYPE = 'LIS' AND APPLICATION_NO='"
						+ pR.getValue("APPLICATION_NO", "TEXT") + "'" };
				TParm sqlParm = new TParm();
				sqlParm.setData("SQL", sqlMedApply);
				TParm actionParm = TIOM_AppServer.executeAction(
						"action.med.MedAction", "saveMedApply", sqlParm);
				if (actionParm.getErrCode() < 0) {
					this.messageBox("����" + pR.getValue("PAT_NAME")
							+ "ҽ����ӡ״̬ʧ�ܣ�");
					return;
				}
				/*
				 * // �к� CallNo call = new CallNo(); if (!call.init()) {
				 * continue; } String dateJH = StringTool.getString(sysDate,
				 * "yyyy-MM-dd HH:mm:ss"); String s =
				 * call.SyncLisMaster(pR.getValue("APPLICATION_NO", "TEXT"),
				 * pR.getValue("MR_NO", "TEXT"), pR.getValue( "ORDER_DESC",
				 * "TEXT"), pR.getValue("PAT_NAME", "TEXT"),
				 * pR.getValue("SEX_DESC", "TEXT"), pR.getValue("AGE", "TEXT"),
				 * pR.getValue("URGENT_FLG", "TEXT"), dateJH, "", "0", "2");
				 */
			}
		} else {
			this.messageBox("û����Ҫ��ӡ����Ŀ��");
			return;
		}
	}
    
    /**
     * ����ͼ�����ֵ�λ��
     * @param x
     * @param y
     * @param code
     */
    public void addGraphTextCode(int x, int y, String code) {// wanglong add 20150410
        this.printText.append("^FO" + (x + offset_x) + "," + (y + offset_y) + code + "^FS");
    }
    
    /**
	 * �õ��������뼱�����
	 */
    public String geturGentFlg(String appNoStr) {
        String flg = "";
        if (appNoStr.equals("")) return flg;
        String medsql =
                "SELECT CASE_NO,ORDER_NO,SEQ_NO,ADM_TYPE FROM MED_APPLY "
                        + " WHERE APPLICATION_NO='" + appNoStr + "' AND CAT1_TYPE='LIS'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(medsql));
        if (parm.getErrCode() < 0) return flg;
        if (parm.getCount() <= 0) return flg;
        String admType = "";
        String caseNo = "";
        String orderNo = "";
        String seqNo = "";
        if (parm.getCount() > 0) {
            String orderSql = "";
            admType = parm.getValue("ADM_TYPE", 0);
            caseNo = parm.getValue("CASE_NO", 0);
            orderNo = parm.getValue("ORDER_NO", 0);
            seqNo = String.valueOf(parm.getInt("SEQ_NO", 0));
            if (admType.equals("O") || admType.equals("E")) {
                orderSql =
                        " SELECT URGENT_FLG FROM OPD_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND RX_NO='" + orderNo + "' AND SEQ_NO='" + seqNo + "'";
            }
            if (admType.equals("I")) {
                orderSql =
                        " SELECT URGENT_FLG FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='" + seqNo + "'";
            }
            if (admType.equals("H")) {
                orderSql =
                        " SELECT URGENT_FLG FROM HRM_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND SEQ_NO='" + seqNo + "'";
            }
            TParm result = new TParm(TJDODBTool.getInstance().select(orderSql));
            flg = result.getValue("URGENT_FLG", 0);
        }
        return flg;
    }
    
    /**
     * ��������
     * @param x
     * @param y
     * @param str
     */
    public void addText(int x, int y, String str) {
        addText(x + offset_x, y + offset_y, 24, str);
    }

    /**
     * ��������
     * @param x
     * @param y
     * @param fontSize �����С
     * @param str
     */
    public void addText(int x, int y, int fontSize, String str) {// fontSizeĬ��24
        this.printText.append(getTextCode(x, y, fontSize, str));
    }
    
    /**
     * �������ֵĿ�����
     * @param x
     * @param y
     * @param fontSize
     * @param str
     * @return
     */
    public static String getTextCode(int x, int y, int fontSize, String str) {
        StringBuffer temp = new StringBuffer();
        try {
            for (int i = 0; i < str.length(); i++) {
                String s = str.substring(i, i + 1);
                byte[] ba = s.getBytes("GBK");
                if (ba.length == 1) {
                    temp.append("^CI0^FO" + x + "," + (y + 4) + "^A0N," + fontSize + "," + fontSize
                            + "^FD" + s + "^FS");
                    x += fontSize / 2;
                } else if (ba.length == 2) {
                    StringBuffer inTmp = new StringBuffer();
                    for (int j = 0; j < 2; j++) {
                        String hexStr = Integer.toHexString(ba[j] - 128);
                        hexStr = hexStr.substring(hexStr.length() - 2);
                        inTmp.append(hexStr + ")");
                    }
                    temp.append("^CI14^FO" + x + "," + y + "^AJN," + fontSize + "," + fontSize
                            + "^FH)^FD)" + inTmp + "^FS");
                    x += fontSize;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp.toString();
    }
    
	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}
	
	/**
	 * �õ��ֵ���Ϣ
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}
	
	/**
     * ��������Ŀ�����
     * @param x X����
     * @param y Y����
     * @param W ���
     * @param R Ratio
     * @param H �߶�
     * @param barCode ����
     */
    public void addBarcode(int x, int y, int W, double R, int H, String barCode) {
        // ^XA
        // ^FO35,5^BY2,3.0,50
        // ^BC
        // ^FD>;1404090017501^FS
        // ^XZ
        this.printText.append("^FO" + (x + offset_x) + "," + (y + offset_y) + "^BY" + W + "," + R
                + "," + H + "^BC^FD>;" + barCode + "^FS");
    }
    
    /**
     * ��ӡ������
     * @param port LPT�˿ں�
     * @return
     */
    public boolean printBarCode(String port) {// wanglong add 20140610
        this.printText.insert(0, "^XA");
        this.printText.append("^XZ");
//        System.out.println("----------------������---------"+this.printText.toString());
        synchronized (this.printText) { // ͬ�� �� ��ӡ��
            FileWriter fw = null;
            PrintWriter out = null;
            try {
                fw = new FileWriter(port); // ������LPT3
                out = new PrintWriter(fw);
                out.print(this.printText.toString());
                return true;
            }
            catch (IOException e) {
                this.messageBox("��ӡ�����Ҳ���ʹ��" + port + "�˿ڵĴ�ӡ��");
                e.printStackTrace();
                return false;
            }
            finally {
                out.close();
                try {
                    fw.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
	 * �����ӡ
	 */
	public void printWristBands() {
		TParm tableParm = table.getParmValue();
		TParm showParm = table.getShowParmValue();
		if (tableParm == null) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		TParm print = new TParm();
        int rowCount = tableParm.getCount();
        if (rowCount < 0) {
        	this.messageBox("�޴�ӡ����");
        	return;
        }
        
        if (!tableParm.getValue("CHOOSE").contains("Y")) {
        	this.messageBox("�빴ѡҪ��ӡ������");
        	return;
        }
        
        String patName = "";
        String birthDate = "";
		for (int i = 0; i < tableParm.getCount(); i++) {
			if ("N".equalsIgnoreCase(tableParm.getValue("CHOOSE", i))) {
				continue;
			}
			
			print = tableParm.getRow(i);
			patName = print.getValue("STAFF_NO");
			birthDate = print.getValue("BIRTH_DATE");
			if (StringUtils.isNotEmpty(patName)) {
				patName = patName + "-";
			}
			
			if (birthDate.length() >= 10) {
				birthDate = birthDate.substring(0, 10).replace("-", "/");
			}
			print.setData("PlanNo", "TEXT", print.getValue("PLAN_NO"));
			print.setData("Barcode", "TEXT", print.getValue("MR_NO"));
			print.setData("PatName", "TEXT", patName + print.getValue("PAT_NAME"));
			print.setData("Sex", "TEXT", showParm.getValue("SEX_CODE", i));
			print.setData("BirthDay", "TEXT", birthDate);
			this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMWrist", print, true);
		}
	}
	
	/**
	 * ���ݵ�ǰѡ���������Ϣ����ɫ����
	 * 
	 * @return roleType ��ɫ����(PIC_һ���ٴ�,H_����)
	 */
	private String getSelComRoleType() {
        companyCode = this.getValueString("COMPANY_CODE");
        TParm companyParm = company.getPopupMenuData();
        int index = 0;
        for (int i = 0; i < companyParm.getCount(); i++) {
        	if (companyCode.equals(companyParm.getValue("ID", i))) {
        		index = i;
        	}
        }
        
        String roleType = "H";
        if ("PIC".equals(companyParm.getValue("ROLE_TYPE", index))) {
        	roleType = "PIC";
        }
        
        return roleType;
	}
	
	/**
	 * ��������
	 */
	public void onSubmitPDF() {
		table.acceptText();
        TParm parmValue = table.getParmValue();
        int dataCount = parmValue.getCount();
        int count = 0;
        TParm parm = new TParm();
        
        for (int i = 0; i < dataCount; i++) {
            if ("N".equalsIgnoreCase(parmValue.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parmValue.getValue("CASE_NO", i))) {
                continue;
            }
            count++;
            
            parm = new TParm();
    		parm.setData("MR_NO", parmValue.getValue("MR_NO", i));
    		parm.setData("CASE_NO", parmValue.getValue("CASE_NO", i));
    		parm.setData("SEX_CODE", parmValue.getValue("SEX_CODE", i));
    		parm.setData("PAT_NAME", parmValue.getValue("PAT_NAME", i));
    		parm.setData("DEPT_CODE", parmValue.getValue("DEPT_CODE", i));
    		this.openDialog("%ROOT%\\config\\hrm\\HRMDocQuery.x", parm);
        }
        
        if (count == 0) {
        	this.messageBox("�빴ѡ�ѱ���������");
        }
		
	}
}
