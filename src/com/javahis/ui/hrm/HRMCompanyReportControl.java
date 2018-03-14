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
 * <p> Title: 健康检查团体报到 </p>
 * 
 * <p> Description: 健康检查团体报到 </p>
 * 
 * <p> Copyright: javahis 20090922 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyReportControl extends TControl {

    private TTable table;// 主项TABLE
    private TTextFormat contract, patName;// 合同、病患名称TTextFormat
    private TRadioButton report,unReport;// 报到、未报到Radio
    private TCheckBox all;// 全部报到TCheckBox
    private String companyCode, contractCode;// 团体代码、合同代码
    private HRMPatInfo pat;// 病患对象
    private HRMPatAdm adm;// 报到对象
    private HRMOrder order;// 医嘱对象
    private HRMContractD contractD;// 合同对象
    private String packageCode, sexCode;// 套餐代码、性别代码 add by wanglong 20121217
    private BILComparator compare = new BILComparator();// add by wanglong 20121217
    private boolean ascending = false;
    private int sortColumn = -1;
    private String caseNo="";//手术室补充计费用
    private String mrNo="";//手术室补充计费用
    private boolean dbaFlg = false;//dba权限
	/**
	 *  条码控制码
	 */
	private StringBuffer printText = new StringBuffer();
    private int offset_x = 0;
    private int offset_y = 0;
    private TTextFormat company;// 团体名称TTextFormat
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        initComponent();// 初始化控件
        initData();// 初始化数据
        
        initTMenu();
    }
    
    /**
     * 权限控制
     */
    public void initTMenu(){
    	dbaFlg = this.getPopedem("SYSDBA");
    	if(dbaFlg){
    		//((TMenuItem)this.getComponent("delete")).setEnabled(true);//取消报道
    		((TMenuItem)this.getComponent("closeOrder")).setEnabled(true);//取消展开
    		((TMenuItem)this.getComponent("batchDelete")).setEnabled(true);//批量删除
    		((TMenuItem)this.getComponent("singleOpt")).setEnabled(true);//单人操作
    	}else{
    		((TMenuItem)this.getComponent("delete")).setEnabled(false);
    		((TMenuItem)this.getComponent("closeOrder")).setEnabled(false);
    		((TMenuItem)this.getComponent("batchDelete")).setEnabled(false);
    		((TMenuItem)this.getComponent("singleOpt")).setEnabled(false);
    	}
    	
    	// 一期临床打印腕带
    	if (this.getPopedem("PIC")) {
    		callFunction("UI|printWristBands|Visible", true);
    	} else {
    		callFunction("UI|printWristBands|Visible", false);
    	}
    }
    
    /**
     * 初始化控件
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
                "onTabValueChanged");// 合同细相TABLE值改变事件
    }
    
    /**
     * 初始化数据
     */
    private void initData() {
    	// 基本显示用的控件清空
        this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE;PY1;TEL");
        unReport.setSelected(true);
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
        // 实例化数据对象
        pat = new HRMPatInfo();
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        contractD = new HRMContractD();
        contractD.onQuery("", "", "");
        
        // add by wangb 2016/06/23 团体名称需要根据不同登录角色筛选
		String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
        
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
        
        // add by wangb 2016/7/6 套餐下拉框
        TTextFormat packageCode = (TTextFormat) this.getComponent("PACKAGE_CODE");
		// 查询套餐信息
		TParm pakcageData = HRMFeePackTool.getInstance().selectHrmPackageByRoleType(roleType);
		packageCode.setPopupMenuData(pakcageData);
		packageCode.setComboSelectRow();
		packageCode.popupMenuShowData();
    }

    /**
     * TABLE单击事件
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
     * TABLE值改变事件
     * @param tNode
     * @return
     */
    public boolean onTabValueChanged(TTableNode tNode) {
//      if (TypeTool.getBoolean(this.getValue("REPORT"))) {
//          this.messageBox_("已报到员工不能修改信息");
//          return true;
//      }
        return false;
    }
    
    /**
     * 是否保存CHECK_BOX，如果报到科室为空，则不能保存
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
                this.messageBox_("报到科室不能为空");
                parm.setData("CHOOSE", row, !TypeTool.getBoolean(parm.getData(
                        "CHOOSE", row)));
                table.setParmValue(parm);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 右击MENU弹出事件
     * @param tableName
     */
    public void showPopMenu() {
        if (unReport.isSelected()) {
            table.setPopupMenuSyntax("");
        } else {
            table.setPopupMenuSyntax("换项,openRigthPopMenu");
        }
    }

    /**
     * 右键事件
     */
    public void openRigthPopMenu() {
        TParm tableParm = table.getParmValue().getRow(table.getSelectedRow());
        tableParm.setData("PRO", "HRMCompanyReportControl");
        // System.out.println("tableParm"+tableParm);
        if (tableParm.getValue("BILL_NO").length() != 0) {
            this.messageBox("已经结算不可以换项！");
            return;
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMPersonReport.x", tableParm);
    }
    
    /**
     * 团体代码点选事件
     */
    public void onCompanyChoose() {
        // 根据选择的团体代码，构造并初始化该团体的合同信息TTextFormat
    	String roleType = this.getSelComRoleType();
        
        if ("PIC".equals(roleType)) {
			table.setHeader(table.getHeader().replace("部门", "入组").replace(
					"工号", "筛选号"));
    	} else {
			table.setHeader(table.getHeader().replace("入组", "部门").replace(
					"筛选号", "工号"));
    	}
        
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
        contractCode = contractParm.getValue("ID", 0);// ============xueyf modify 23050305
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("查询失败");
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
        // 根据选择的合同代码，构造并初始化该合同的病患信息TTextFormat
        TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
        patName.setPopupMenuData(patParm);
        patName.setComboSelectRow();
        patName.popupMenuShowData();
    }

    /**
     * 合同代码点选事件,根据选择的合同代码，构造并初始化该合同的病患信息TTextFormat
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
     * 套餐代码、性别代码点选事件
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
                && (!StringUtil.isNullString(sexCode))) {// sex不为空
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
     * 根据病患名查到病患
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
            this.messageBox("E0081");// 查无此病患
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
     * 病案号查询
     */
    public void onQueryByMr() {//modify by wanglong 20121217
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);// 病案号补齐长度
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);// 病案号
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
        
        if (StringUtil.isNullString(companyCode)) {
            TParm result = HRMCompanyTool.getInstance().getContractDByMr(mrNo);
            if (result.getErrCode() != 0) {
                this.messageBox_("查询失败");
                return;
            }
            
            if (result.getCount() < 1) {
                return;
            } else if (result.getCount() == 1) {// 单行数据
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (result.getValue("COVER_FLG").equals("Y")) {
                    report.setSelected(true);// 设置"已报到"被选中
                    this.callFunction("UI|save|setEnabled", false);
                    if(dbaFlg){//有权限先才可以取消报道
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
                    this.messageBox_("没有数据");
                    return;
                }
                contract.setPopupMenuData(contractParm);
                contract.setComboSelectRow();
                contract.popupMenuShowData();
                contractCode=result.getValue("CONTRACT_CODE");
                contract.setValue(contractCode);  
                packageCode = result.getValue("PACKAGE_CODE");
            } else {// 多行数据(弹出选择窗口)
                Object obj = this.openDialog(
                        "%ROOT%\\config\\hrm\\HRMPatRecord.x", result);
                if (obj != null) {
                    TParm rowParm = (TParm) obj;
                    table.setParmValue(rowParm);
                    table.setSelectedRow(0);
                    rowParm = rowParm.getRow(0);
                    if (rowParm.getValue("COVER_FLG").equals("Y")) {
                        report.setSelected(true);// 设置"已报到"被选中
                        this.callFunction("UI|save|setEnabled", false);
                        if(dbaFlg){//有权限先才可以取消报道
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
                        this.messageBox_("没有数据");
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
        } else {// 单行数据
            TParm result = HRMCompanyTool.getInstance().getContractDByMr(
                    companyCode, contractCode, mrNo);
            if (result.getErrCode() != 0) {
                this.messageBox_("查询失败");
                return;
            }
            if (result.getCount() < 1) {
                return;
            } else if (result.getCount() == 1) {
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (result.getValue("COVER_FLG").equals("Y")) {
                    report.setSelected(true);// 设置"已报到"被选中
                    this.callFunction("UI|save|setEnabled", false);
                    if(dbaFlg){//有权限先才可以取消报道
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
     * 身份证号查询
     */
    public void onQueryByIdNo() {
        if (StringUtil.isNullString(companyCode)) {
            this.messageBox_("团体代码不可为空");
            return;
        }
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("合同代码不可为空");
            return;
        }
        String idNo = this.getValueString("IDNO");
        String isReport = this.getValueString("REPORT");
        TParm conDParm = HRMCompanyTool.getInstance().getContractDById(
                companyCode, contractCode, idNo, isReport);
        table.setParmValue(conDParm);
    }

    /**
     * 已报到
     */
    public void onReport() {
        onQueryAfterSave();
        this.callFunction("UI|save|setEnabled", false);
        if(dbaFlg){//有权限先才可以取消报道
        	this.callFunction("UI|delete|setEnabled", true);
        }
    }

    /**
     * 未报到
     */
    public void onUnReport() {
        onQueryAfterSave();
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
    }
    
    /**
     * 全选事件
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
     * 筛选
     */
    public void onCustomizeChoose() {// modify by wanglong 20130206
        TParm parm = table.getParmValue();
        if (((TRadioButton) this.getComponent("SEQ_BUTTON")).isSelected()) {// 选择序号
            if (this.getValueString("START_SEQ_NO").equals("")
                    && this.getValueString("END_SEQ_NO").equals("")) {
                onQueryAfterSave();
                return;
            }
            if (!this.getValueString("START_SEQ_NO").matches("\\-?[0-9]+")
                    || !this.getValueString("END_SEQ_NO").matches("\\-?[0-9]+")) {
                messageBox("请输入数字");
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
        } else if (((TRadioButton) this.getComponent("AGE_BUTTON")).isSelected()) {// 选择年龄 add by wanglong 20130225
            if (this.getValueString("START_SEQ_NO").equals("")
                    && this.getValueString("END_SEQ_NO").equals("")) {
                onQueryAfterSave();
                return;
            }
            if (!this.getValueString("START_SEQ_NO").matches("\\-?[0-9]+")
                    || !this.getValueString("END_SEQ_NO").matches("\\-?[0-9]+")) {
                messageBox("请输入数字");
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
        } else if (((TRadioButton) this.getComponent("SEX_BUTTON")).isSelected()) {// 选择性别
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
        } else if (((TRadioButton) this.getComponent("MARRIAGE_BUTTON")).isSelected()) {// 选择婚姻 add by wanglong 20130225
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
     * 各种筛选按钮选择事件
     */
    public void onCustomizeButtonChoose() {// add by wanglong 20130225
        if (((TRadioButton) this.getComponent("SEQ_BUTTON")).isSelected()// 选择序号
                || ((TRadioButton) this.getComponent("AGE_BUTTON")).isSelected()) {// 选择年龄
            this.callFunction("UI|START_SEQ_NO|setEnabled", true);
            this.callFunction("UI|END_SEQ_NO|setEnabled", true);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", false);
        } else if (((TRadioButton) this.getComponent("SEX_BUTTON")).isSelected()) {// 选择性别
            this.callFunction("UI|START_SEQ_NO|setEnabled", false);
            this.callFunction("UI|END_SEQ_NO|setEnabled", false);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", true);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", false);
        } else if (((TRadioButton) this.getComponent("MARRIAGE_BUTTON")).isSelected()) {// 选择婚姻
            this.callFunction("UI|START_SEQ_NO|setEnabled", false);
            this.callFunction("UI|END_SEQ_NO|setEnabled", false);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", true);
        }
    }
    
    /**
     * 保存事件
     */
    public void onSave() {
        TParm result =onExeParm();
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("无保存数据");
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
            this.messageBox("查询病患信息出错");
            return;
        }
        // 加入预打印导览单情况start======================$$//
        // 未打印记录
        List unReportRecords = new ArrayList();
        List listHl7 = new ArrayList();
        // 判断HRM_ADM中是否存在数据；存在数据的话， 只更新一下 HRM_ADM D表的已报到标志
        Timestamp now = adm.getDBTime();
        TParm resultParm = new TParm();
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("病案号为空");
                continue;
            }
            String caseNo = "";
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // this.messageBox("==mrNo=="+mrNo);
            // 判断是未报到，则保存数据，但设置成未报到
            // 1.插入HRM_PATADM
            if (!StringUtil.isNullString(caseNo)) {////////////////已展开或已报到（更新状态，电话）
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
                // 确实没有caseno说明是现场报到,记录空的CASE_NO
            } else {
                unReportRecords.add(parmRow);
            }
        }
        // $$==============add by lx 2012/05/22 end=======================$$//
        // 循环插入hrm_patadm,hrm_order,
        boolean flag = true;
        for (int i = 0; i < unReportRecords.size(); i++) {///////////////////未报到（执行报到的操作）
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
                this.messageBox_("序号:" + parmRow.getData("SEQ_NO") + "  姓名：" + parmRow.getData("PAT_NAME") + ",生成HRM_PATADM数据失败");
                flag = false;
                adm = new HRMPatAdm();
                adm.onQuery();
                order = new HRMOrder();
                order.onQuery("", "");
                continue;
            }
            String caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox_("序号:" + parmRow.getData("SEQ_NO") + "  姓名：" + parmRow.getData("PAT_NAME") + ",取得数据失败");
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
            // 配置送入后台保存方法的参数，并验证后台保存方法的返回值是否成功
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm saveResult =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
            if (saveResult.getErrCode() != 0) {
                this.messageBox("序号:" + parmRow.getData("SEQ_NO") + "  姓名:" + parmRow.getData("PAT_NAME") + "\n报到失败");
                flag = false;
            } else {
                // this.messageBox_("报到成功");
                this.getHl7List(listHl7, caseNo);
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        // 发送HL7消息
        TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(listHl7);
        
        if (hl7Parm.getErrCode() < 0) {
            this.messageBox("发送HL7消息失败" + hl7Parm.getErrText());
            flag = false;
        }
        
        if (flag) {
            this.messageBox("报到成功");
            
			// modify by wangb 2016/6/14 健康体检团体报到自动打印开关(用于区分泰心与普华)
			String autoPrintSwitch = TConfig
					.getSystemValue("HRM.AUTO_PRINT.SWITCH");
			// 目前泰心使用自动打印
			if ("Y".equalsIgnoreCase(autoPrintSwitch)) {
				// 界面上勾选自动打印时，报到后自动打印导览单、申请单和条码
				if (this.getComponent("AUTO_PRINT") != null
						&& ((TCheckBox) this.getComponent("AUTO_PRINT"))
								.isSelected()) {
					
					// 根据选择的团体代码，构造并初始化该团体的合同信息TTextFormat
					String roleType = this.getSelComRoleType();
			        
					// 健检
					if (this.getPopedem("H") && "H".equals(roleType)) {
						// 打印导览单
						this.onReportPrint("save");

						// 批量打印申请单
						this.onPrintExa();

						// 批量打印条码
						this.queryLisPrint();
					}

					// 一期临床
					if (this.getPopedem("PIC") && "PIC".equals(roleType)) {
						// 自动打印腕带
//						this.printWristBands();
						
						// 批量打印申请单
						this.onPrintExa();

						// 批量打印条码
						this.queryLisPrint();
					}
				}
			}
            
//            this.setValue("REPORT", "Y");
        }
        // add by wanglong 20140214 打印检查申请单
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
     * 得到HL7数据
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
     * 展开体验项目
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
            this.messageBox_("无保存数据");
            return;
        }
        if (count >= 1000) {//add by wanglong 20130426
            this.messageBox_("每次操作不要超过1000人");
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
            this.messageBox("查询病患信息出错");
            return;
        }
        // 假如未报到， 并且HRM_ADM无数据则
        // add by lx 预保存
        Timestamp now = adm.getDBTime();
        boolean flag = true;
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("病案号为空");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // 判断是未报到，则保存数据，但设置成未报到
            // 1.插入HRM_PATADM
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
                // 1.预报到
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("序号:" + result.getData("SEQ_NO", i) + "  姓名：" + result.getData("PAT_NAME", i) + ",预报到生成HRM_PATADM数据失败");
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
                    this.messageBox_("序号:" + result.getData("SEQ_NO", i) + "  姓名：" + result.getData("PAT_NAME", i) + ",取得数据失败");
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
                // 配置送入后台保存方法的参数，并验证后台保存方法的返回值是否成功
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("序号:" + result.getData("SEQ_NO", i) + "  姓名:" + result.getData("PAT_NAME", i) + "\n医嘱展开失败");
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
            this.messageBox("医嘱展开成功");
        }
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        onQueryAfterSave();
    }
    
    /**
     *  复制体检项目
     */
    public void onCopyOrder() {//add by wanglong 20130508
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// 判断是否选择记录
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("请选择要新增的记录");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        if (result.getCount("MR_NO") == 1) {
            String caseNo = "";
            if (StringUtil.isNullString(result.getValue("CASE_NO", 0))) {
                caseNo = adm.getLatestCaseNoBy(result.getValue("MR_NO", 0), result.getValue("CONTRACT_CODE", 0));
                if(!StringUtil.isNullString(caseNo)){
                    this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 医嘱已展开");
                    return;
                }
            }else{
                this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 医嘱已展开");
                return;
            }
  
        }
        // 触发打开动作
        Object o = this.openDialog("%ROOT%\\config\\hrm\\HRMCopyOrder.x", result);
        onQueryAfterSave();
    }
    
    /**
     * 取消展开操作
     * ==================pangben 2013-3-10 
     */
    public void onCloseOrder() {
        TParm result = onExeParm();
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("没有需要操作的数据");
            return;
        }
        if (report.isSelected()) {
            this.messageBox("已报到人员无法取消展开操作！");
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
                this.messageBox("检查用户结算状态失败");
                return;
            }
            if (result1.getCount() >= 0 && !result1.getValue("BILL_NO", 0).equals("")) {
                this.messageBox(patName + "已结算，不能取消展开");
                result.removeRow(i);
            }
        }
        if (this.messageBox("提示", "是否执行取消展开操作", 2) != 0) {
            return;
        }
        result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onDeleteOrder",
                                             result);
        if (result.getErrCode() < 0) {
            this.messageBox("取消展开失败 " + result.getErrText());
            return;
        }
        if (result.getValue("INDEX_MESSAGE").length() > 0) {
            this.messageBox(result.getValue("INDEX_MESSAGE"));
        } else {
            if (result.getValue("HL7_MESSAGE").length() > 0) {
                this.messageBox("员工:" + result.getValue("HL7_MESSAGE") + " 取消展开失败（发送HL7消息失败）");
            }
            if (result.getValue("PAT_MESSAGE").length() > 0) {
                this.messageBox("员工:" + result.getValue("PAT_MESSAGE") + " 没有需要取消的数据");
            }
            this.messageBox("取消展开成功");
        }
        onQueryAfterSave();
    }

    /**
     * 需要执行的数据
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
     * 打印导览单
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
            this.messageBox_("无保存数据");
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
            this.messageBox("查询病患信息出错");
            return;
        }
        // 假如未报到， 并且HRM_ADM无数据则
        // add by lx 预保存
        Timestamp now = adm.getDBTime();
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("病案号为空");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // 判断是未报到，则保存数据，但设置成未报到
            // 1.插入HRM_PATADM
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
                // 1.预报到
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("序号:" + result.getData("SEQ_NO", i) + "  姓名："
                            + result.getData("PAT_NAME", i) + ",预报到生成HRM_PATADM数据失败");
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
                    this.messageBox_("序号:" + result.getData("SEQ_NO", i) + "  姓名：" + result.getData("PAT_NAME", i) + ",取得数据失败");
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
                // 配置送入后台保存方法的参数，并验证后台保存方法的返回值是否成功
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("序号:" + result.getData("SEQ_NO", i) + "  姓名:" + result.getData("PAT_NAME", i) + "\n医嘱展开失败");
                }
            } else {
                result.setData("CASE_NO", i, caseNo);// add by wanglong 20130304
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        // 假如未报到，打印处理完成
        // $$==============add by lx end 预保存====================$$//
        TParm errParm = new TParm();
        String message = "";
        for (int i = 0; i < count; i++) {
            // System.out.println("parm==="+parm);
        	
            TParm parmRow = result.getRow(i);
            String isVip = "";
        	if("2".equals(parmRow.getValue("IS_VIP"))){
        		isVip = "★";
        	}
            String mrNo = parmRow.getValue("MR_NO");
            // System.out.println("mrNo========="+mrNo);
            String caseNo = "";
            if (parmRow.getValue("CASE_NO").length() == 0) caseNo =
                    adm.getLatestCaseNoBy(mrNo, contractCode);
            else caseNo = parmRow.getValue("CASE_NO");
            // 未生成就诊号，所以不能打印?????
            if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
                errParm.addRowData(parm, i);
                message +=
                        "序号：" + parmRow.getValue("SEQ_NO") + "  姓名：" + parmRow.getValue("PAT_NAME") + "\n";
                continue;
            }
            // 更新SQL
//          parmRow = order.getReportTParm(mrNo, caseNo);//=======================获得导览单数据
            parmRow = IReportTool.getInstance().getReportParm("HRMReportSheetNew.class", parmRow);//获得导览单数据modify by wanglong 20130730
            if (parmRow == null) {
                this.messageBox_("取得数据失败111");
                continue;
            }
            if (parmRow.getErrCode() != 0) {
                this.messageBox_("取得数据失败222");
                continue;
            }
            
            if(!StringUtil.isNullString(caseNo) && !StringUtil.isNullString(mrNo)){
            	TParm orderParm = new TParm(TJDODBTool.getInstance().select("SELECT MR_NO FROM HRM_ORDER WHERE ORDER_CODE = 'Y1003001' AND CASE_NO = '"+caseNo+"'"));
            	if(orderParm.getCount() > 0){
            		parmRow.setData("ACTION","TEXT","您在本次体检中要求进行“乙肝病毒学检查”，请在下面签字");
            	}
            }
            
            parmRow.setData("VIP","TEXT",isVip);
//            openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMReportSheetNew.jhw", parmRow,
//                            !this.getValueBoolean("PRINT_FLG"));
            openPrintDialog(IReportTool.getInstance().getReportPath("HRMReportSheetNew.jhw"),
                            parmRow, !this.getValueBoolean("PRINT_FLG"));//报表合并modify by wanglong 20130730
        }
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        
        if (!"save".equals(type)) {
        	onQueryAfterSave();
        }
        
        if (message != null && !message.equals("")) {
            this.messageBox(message + "请先展开体验项目!");
        }
    }

    /**
     * 保存后的查询
     */
    public void onQueryAfterSave() {
        this.setValue("ALL", "N");
        this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE;PACKAGE_CODE;PY1;TEL");// add by wanglong 20121217
        onPackageAndSexChoose();// add by wanglong 20121217
    }

    /**
     * 条码打印
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
        
        // add by wangb 2016/08/02 根据选择的团体项目来决定条码样式
        String roleType = this.getSelComRoleType();
        
        HRMPatAdm patAdm = new HRMPatAdm();
        for (int i = 0; i < count; i++) {
            if (!TypeTool.getBoolean(tableParm.getData("CHOOSE", i))) {
                continue;
            }
            String caseNo = adm.getLatestCaseNoBy(tableParm.getValue("MR_NO", i), contractCode);
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox("就诊号为空，请先打印导览单或者先报道");
                continue;
            }
            patAdm.onQueryByCaseNo(caseNo);
            TParm parm = new TParm();
            // 参数
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
     * 弹出申请单打印界面
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
            this.messageBox_("无数据");
            return;
        }
        TParm parm = new TParm();
        if (count >= 1000) {
            this.messageBox("人数不能超过1000");
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
        // add by wangb 2016/09/19 一期临床全部打印所有类型申请单
		String roleType = this.getSelComRoleType();
		parm.setData("ROLE_TYPE", roleType);
        this.openDialog("%ROOT%\\config\\hrm\\HRMRisPrint.x", parm);
    }
    
    /**
	 * 批量打印申请单
	 */
	public void onPrintExa() {// add by wanglong 20140214
		table.acceptText();
		TParm parm = table.getParmValue();
		String caseList = "";
		int count = 0;
		int dataCount = parm.getCount();
		String mrNoList = "";
		
		// 未提前展开的数据本身没有生成case_no，因此直接点击报到按钮表格不刷新抓取不到case_no
		for (int i = 0; i < dataCount; i++) {
			if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
				continue;
			}
			mrNoList = mrNoList + "'" + parm.getValue("MR_NO", i) + "',";
			count++;
		}
		
		if (mrNoList.length() == 0) {
			this.messageBox("无数据");
			return;
		} else {
			mrNoList = mrNoList.substring(0, mrNoList.length() - 1);
		}
		
		String patSql = "SELECT MR_NO,CASE_NO FROM HRM_PATADM WHERE MR_NO IN ("
				+ mrNoList + ") AND CONTRACT_CODE = '" + contractCode + "'";
		
		TParm patResult = new TParm(TJDODBTool.getInstance().select(patSql));
		if (patResult.getErrCode() < 0) {
			this.messageBox("查询体检就诊信息错误");
			err("查询体检人员信息错误:" + patResult.getErrText());
			return;
		} else if (patResult.getCount() < 1) {
			this.messageBox("未查询到体检就诊信息,请确保已展开医嘱项目");
			return;
		} else {
			caseList = "'" + patResult.getValue("CASE_NO").replace("[", "").replace(
					"]", "").replace(" ", "").replace(",", "','") + "'";
		}
		
		if (caseList.equals("")) {
			this.messageBox_("无数据");
			return;
		}
		if (count >= 1000) {
			this.messageBox("人数不能超过1000");
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
			this.messageBox("查询医嘱信息出错");
			err("查询医嘱信息出错:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			return;
		}
		onPrintRIS(result);
	}
	
	
	 /**
     * 打印检查申请单
     */
    public void onPrintRIS(TParm parm) {// add by wanglong 20140214
        String sql = "SELECT * FROM HRM_SYSPARM";
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (sysParm.getErrCode() < 0) {
            this.messageBox(sysParm.getErrText());
            return;
        }
        ArrayList<String> deptList = new ArrayList<String>();
        // 根据当前选择的团体信息辨别角色类型
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
            
            // 超声科室代码(暂时写死)
        	String dept = "0405";
            for (int i = 0; i < deptParm.getCount(); i++) {
            	// add by wangb 2016/09/19  一期临床打印申请单无需排除超声科室
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
            inParam.setData("ORDER_CODE", descParm.getValue("ORDER_CODE", 0).replaceAll(",", "；"));
            inParam.setData("ORDER_DESC", descParm.getValue("ORDER_DESC", 0).replaceAll(",", "；"));
            String execDeptCode = StringUtil.getDesc("HRM_ORDER", "EXEC_DEPT_CODE", where);
            if (deptList.size() != 0 && deptList.contains(execDeptCode)) {
                continue;
            }
            TParm patInfo = pat.getHRMPatInfo(inParam.getValue("MR_NO"), inParam.getValue("CASE_NO"));
            inParam.setData("MR_NO", inParam.getValue("MR_NO"));
            inParam.setData("MR_NO", "TEXT", inParam.getValue("MR_NO"));
            inParam.setData("Barcode", "TEXT", inParam.getValue("MR_NO"));
            inParam.setData("CASE_NO", inParam.getValue("CASE_NO"));
            inParam.setData("SEQ", seq);//医嘱序号
            inParam.setData("PAT_NAME", patInfo.getValue("PAT_NAME", 0));
            inParam.setData("SEX_CODE", patInfo.getValue("SEX_CODE", 0));
            inParam.setData("SEX_DESC", patInfo.getValue("SEX_DESC", 0));
            inParam.setData("BIRTHDAY", StringTool.getString(patInfo.getTimestamp("BIRTHDAY", 0), "yyyy-MM-dd"));
            inParam.setData("AGE", StringTool.CountAgeByTimestamp(patInfo.getTimestamp("BIRTHDAY", 0), now)[0]);
            inParam.setData("COMPANY_CODE", patInfo.getValue("COMPANY_CODE", 0));
            inParam.setData("COMPANY_DESC", patInfo.getValue("COMPANY_DESC", 0));
            inParam.setData("CONTRACT_CODE", patInfo.getValue("CONTRACT_CODE", 0));
            inParam.setData("CONTRACT_DESC", patInfo.getValue("CONTRACT_DESC", 0));
            inParam.setData("SEQ_NO", patInfo.getValue("SEQ_NO", 0));//人员的序号，非医嘱序号
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
            // 一期临床打印申请单隐藏电话号且显示筛选号
            if ("PIC".equals(roleType)) {
            	inParam.setData("FILTER_NO", "TEXT", "筛选号:" + inParam.getValue("STAFF_NO"));
            	inParam.setData("TEL", "");
            }
            this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMRisSheet.jhw", inParam, true);
        }
    }
    /**
     * 取消报到事件
     */
    public void onDelete() {
        if (unReport.isSelected()) {
            this.messageBox("无法取消报到！");
            return;
        }
        if (this.messageBox("提示", "是否执行取消报到操作", 2) != 0) {
            return;
        }
        TParm tableParm = table.getParmValue();
        int rowCount = tableParm.getCount();
        int rightCount = 0;// 正确个数
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
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 已经结算，不可以取消报到！");
                continue;
            }
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO='" + caseNo
                            + "' AND BILL_NO IS NOT NULL";
            TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
            if (result1.getErrCode() != 0) {
                this.messageBox("检查用户结算状态失败");
                return;
            }
            if (result1.getCount() >= 0 && !result1.getValue("BILL_NO", 0).equals("")) {
                this.messageBox(patName + " 已有医嘱被结算，不能取消报到");
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
            //Hl7发送取消执行的消息（med_apply设置status=9,send_flg=1）
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
                                && this.messageBox("提示", patName + " 部分检查已执行，是否继续删除他（她）的医嘱？", 2) != 0) {
                            continue;
                        }
                    }
                    catch (Exception ex) {
                        System.err.print("检查已执行判断失败。");
                        ex.printStackTrace();
                    }
                    hl7ParmDel.add(delTemp);
                }
            }
            if (hl7ParmDel.size() > 0) {// modify by wanglong 20130408
                TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ParmDel);
                if (hl7Parm.getErrCode() < 0) {
                    this.messageBox(patName + " 取消报到失败(HL7消息发送失败) " + hl7Parm.getErrText());
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
                this.messageBox(patName + " 取消报到失败");
                continue;
            }
            rightCount++;
        }
        if (rightCount > 0) {
            this.messageBox("取消报到成功");
            this.setValue("UNREPORT", "Y");
            onUnReport();
        }
    }

    /**
     * 清空事件
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
     * 批量新增
     */
    public void batchAdd() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// 判断是否选择记录
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("请选择要新增的记录");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        result.addData("METHOD", "ADD");
        if (result.getCount("MR_NO") == 1) {// add by wanglong 20130422 当只选择一个人时，提前提示是否已经结算。
            String billSql =
                    "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                            + " WHERE A.CASE_NO = B.CASE_NO AND B.MR_NO = '#' "
                            + " AND B.CONTRACT_CODE = '#'";
            billSql = billSql.replaceFirst("#", result.getValue("MR_NO", 0));
            billSql = billSql.replaceFirst("#", result.getValue("CONTRACT_CODE", 0));
            TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
            if (billParm.getErrCode() != 0) {
                this.messageBox("检查结算信息失败 " + billParm.getErrText());
                return;
            }
            if (billParm.getCount() > 1
                    || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                            .getValue("BILL_NO", 0).trim()))) {
                this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 已结算，不允许增项");
                return;
            } else if (billParm.getCount() < 1) {
                String patSql =
                        "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
                patSql = patSql.replaceFirst("#", result.getValue("MR_NO", 0));
                patSql = patSql.replaceFirst("#", result.getValue("CONTRACT_CODE", 0));
                TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong 20130428
                if (patParm.getErrCode() != 0) {
                    this.messageBox("检查医嘱展开状态失败 " + patParm.getErrText());
                    return;
                }
                if (patParm.getCount() < 1) {
                    this.messageBox("姓名：" + result.getValue("PAT_NAME", 0) + " 医嘱未展开，不允许增项");
                    return;
                }
            }
        }
        // 触发打开动作
        Object o = this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
        // 如果保存成功需要刷新pat缓存
        if (o != null && !o.equals("")) {
            adm = new HRMPatAdm();
            adm.onQuery();
        }

    }

    /**
     * 批量删除
     */
    public void batchDelete() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// 判断是否选择记录
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("请选择要删除的记录");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        result.addData("METHOD", "DELETE");
        this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
    }

    /**
     * 单人操作
     */
    public void onSingleOpt() {//wanglong add 20140829
        table.acceptText();
        TParm parm = table.getParmValue();
        int count = -1;
        for (int i = 0; i < parm.getCount(); i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// 判断是否选择记录
                if (count > -1) {
                    this.messageBox("仅支持单人操作");
                    return;
                }
                count = i;
            }
        }
        if (count < 0) {
            this.messageBox("请选择要操作的人员");
            return;
        }
        String patSql = "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
        patSql = patSql.replaceFirst("#", parm.getValue("MR_NO", count));
        patSql = patSql.replaceFirst("#", parm.getValue("CONTRACT_CODE", count));
        TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong
                                                                           // 20130428
        if (patParm.getErrCode() != 0) {
            this.messageBox("检查医嘱展开状态失败 " + patParm.getErrText());
            return;
        }
        if (patParm.getCount() < 1) {
            this.messageBox("姓名：" + parm.getValue("PAT_NAME", count) + " 医嘱未展开，不允许增项");
            return;
        }
        String billSql = "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '#'";
        billSql = billSql.replaceFirst("#", parm.getValue("CASE_NO", count));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("查询结算信息失败 " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("该人员已结算，不允许增减项");
            return;
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMSingleOperation.x", parm.getRow(count));
    }
    
    /**
     * 更新电话         add by wanglong 20130110
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
            messageBox(patNames + "\n保存电话失败！");
        } else {
            this.messageBox("P0001");// 保存成功
        }
        
    }
    
    /**
     * 汇出Excel
     */
    public void onExcel() {// add by wanglong 20130206
        if (table.getRowCount() <= 0) {
            this.messageBox("E0116");
            return;
        }
        if (unReport.isSelected()) {
            ExportExcelUtil.getInstance().exportExcel(table, "健检团体未报到人员列表");
        } else {
            ExportExcelUtil.getInstance().exportExcel(table, "健检团体已报到人员列表");
        }
    }
    
    // ====================排序功能begin======================add by wanglong 20121217
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
	 * 手术室补充计费
	 *//*
	public void onOperation() {
		//===================================================复制单人操作 start
		table.acceptText();
        TParm parm = table.getParmValue();
        int count = -1;
        for (int i = 0; i < parm.getCount(); i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// 判断是否选择记录
                if (count > -1) {
                    this.messageBox("仅支持单人操作");
                    return;
                }
                count = i;
            }
        }
        if (count < 0) {
            this.messageBox("请选择要操作的人员");
            return;
        }
        String patSql = "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
        patSql = patSql.replaceFirst("#", parm.getValue("MR_NO", count));
        patSql = patSql.replaceFirst("#", parm.getValue("CONTRACT_CODE", count));
        TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong
                                                                           // 20130428
        if (patParm.getErrCode() != 0) {
            this.messageBox("检查医嘱展开状态失败 " + patParm.getErrText());
            return;
        }
        if (patParm.getCount() < 1) {
            this.messageBox("姓名：" + parm.getValue("PAT_NAME", count) + " 医嘱未展开，不允许增项");
            return;
        }
        String billSql = "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '#'";
        billSql = billSql.replaceFirst("#", parm.getValue("CASE_NO", count));
        TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("查询结算信息失败 " + billParm.getErrText());
            return;
        }
        if (billParm.getCount() > 1
                || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                        .getValue("BILL_NO", 0).trim()))) {
            this.messageBox("该人员已结算，不允许增减项");
            return;
        }
		//=======================================================复制单人操作 end
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
			int seqNo = order.getOrderMaxSeqNo(caseNo);// 下一序号
	        int orderGroupNo = order.getOrderMaxGroupNo(caseNo)+1;
			if(!insertHrmData(parm_obj.getRow(0),seqNo,orderGroupNo,operationParm.getDouble("DOSAGE_QTY",i),operationParm.getValue("DOSAGE_UNIT",i),discountRate)){
				this.messageBox("计费失败");
				return;
			}
		}
		this.messageBox("计费成功");
	}
	*//**
	 * 插入数据
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
	 * 读取二代身份证
	 * 
	 * @author wangb 2016/4/21
	 */
	public void onIdCard() {
		// 读取身份证
		TParm idParm = IdCardO.getInstance().readIdCard();
		
		if (StringUtils.isNotEmpty(idParm.getValue("MESSAGE"))) {
			this.messageBox(idParm.getValue("MESSAGE"));
		}
		
		if (idParm.getErrCode() < 0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		
		if (idParm.getCount() > 1) {// 多行数据显示
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
					TypeTool.getString(getValue("PAT_NAME"))));// 简拼
		}
		
		// 根据指定条件查询病患报道信息
		this.queryPatReportInfo("IDNO");
	}
	
	/**
	 * 根据指定条件查询病患报道信息
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
	        mrNo = PatTool.getInstance().checkMrno(mrNo);// 病案号补齐长度
	        this.setValue("MR_NO", mrNo);
	        
	        // modify by huangtt 20160929 EMPI患者查重提示 start
    		Pat pat = Pat.onQueryByMrNo(mrNo);
    		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
    			mrNo = pat.getMrNo();
    			this.setValue("MR_NO", mrNo);// 病案号
    		}
    		// modify by huangtt 20160929 EMPI患者查重提示 end
	        
		} else {
			if (StringUtils.isEmpty(this.getValueString(type))) {
				return;
			} 
		}
		
		TParm parm = getParmForTag("COMPANY_CODE;CONTRACT_CODE;MR_NO;PAT_NAME;IDNO;PY1;TEL");
		// modify by wangb 2016/6/1 为方便用户操作习惯由系统自动判断报到状态
		// 报到状态
//		if (report.isSelected()) {
//			parm.setData("COVER_FLG", "Y");
//		} else {
//			parm.setData("COVER_FLG", "N");
//		}
		
		if (StringUtils.isNotEmpty(patName)) {
			parm.setData("PAT_NAME", patName);
		}
		
		// 查询健检报到信息
		TParm result = HRMCompanyTool.getInstance().selectContractCoverInfo(parm);
		
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
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
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询报到信息错误");
			err("ERR:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("查无此病患");
			return;
		} else if (result.getCount() == 1) {
			// 将查询到的报到信息代入页面控件
			this.setValueToPage(result);
		} else {
			Object obj = this.openDialog("%ROOT%\\config\\hrm\\HRMPatRecord.x",
					result);
			if (obj != null) {
				TParm selRowParm = (TParm) obj;
				// 将查询到的报到信息代入页面控件
				this.setValueToPage(selRowParm);
			}
		}
	}
	
	/**
	 * 将查询到的报到信息代入页面控件
	 * 
	 * @author wangb 2016/4/25
	 */
	private void setValueToPage(TParm parm) {
		table.setParmValue(parm);
        table.setSelectedRow(0);
        parm = parm.getRow(0);
        if (parm.getValue("COVER_FLG").equals("Y")) {
            report.setSelected(true);// 设置"已报到"被选中
            this.callFunction("UI|save|setEnabled", false);
            if(dbaFlg){//有权限先才可以取消报道
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
            this.messageBox_("没有数据");
            return;
        }
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = parm.getValue("CONTRACT_CODE");
		contract.setValue(contractCode);
		packageCode = parm.getValue("PACKAGE_CODE");
		
		// 姓名
        TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
        patName.setPopupMenuData(patParm);
        patName.setComboSelectRow();
        patName.popupMenuShowData();
	}
	
    /**
     * 检索检验项目并打印条码
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
			
			// 查询指定病患的检验项目信息
			lisParm = HRMCompanyTool.getInstance().selectMedApplyInfo(parm.getRow(i));
			
			// 自动打印检验条码
			this.onAutoPrintBarCode(lisParm);
		}
    }
    
    /**
     * 自动打印检验条码
     * 
     * @author wangb 2016/4/25
     */
	private void onAutoPrintBarCode(TParm lisParm) {
		int rowCount = lisParm.getCount();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String bedNo = "";
		
		// 取得LIS合码医嘱
		MedApply medApply = new MedApply();
        TParm lisMergeOrder = medApply.getLisMergeOrder(lisParm.getValue("CASE_NO", 0));
        if (lisMergeOrder.getErrCode() < 0) {
        	this.messageBox("取得检验合码医嘱失败");
            return;
        }
        Map<String, String> lisMergeMap = new HashMap<String,String>();
        for (int i = 0; i < lisMergeOrder.getCount(); i++) {
			lisMergeMap.put(lisMergeOrder.getValue("APPLICATION_NO", i),
					lisMergeOrder.getValue("ORDER_DESC", i));
        }
        List<String> applicationNoList = new ArrayList<String>();
        
        // add by wangb 2016/09/19 根据选择的团体项目来决定条码样式
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
			String applyNo = ""; // chenxi 条码号
			String drNote = ""; // chenxi 医师备注
			String filterNo = "";// 筛选号
			String planNo = "";// 方案号
			
			for (int i = 0; i < rowCount; i++) {
				TParm temp = lisParm.getRow(i);
				applyNo = temp.getValue("APPLICATION_NO");
				// add by wangb 2016/6/1 相同条码号的检验项目只打印一遍
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
				urgentFlg = geturGentFlg(applyNo).equals("Y") ? "(急)" : "";
				orderDate = String.valueOf(sysDate).substring(0, 19)
						.replaceAll("-", "/");
				optItemDesc = temp.getValue("OPTITEM_CHN_DESC");
				mrNo = temp.getValue("MR_NO");
				sexDesc = this.getDictionary("SYS_SEX", temp
						.getValue("SEX_CODE"));
				age = StringTool.CountAgeByTimestamp(temp
						.getTimestamp("BIRTH_DATE"), sysDate)[0];
				orderDesc = lisMergeMap.get(applyNo).replace("、", "");
				
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
				// add by wangb 2016/09/19 一期临床方案号
				printParm.setData("PLAN_NO", "TEXT", planNo);
				printSize.add(printParm);
			}

			int listRowCount = printSize.size();
			String paramSql = "SELECT * FROM MED_PRINTER_LIST WHERE PRINTER_TERM='#'"
					.replaceFirst("#", Operator.getIP());
			TParm printParam = new TParm(TJDODBTool.getInstance().select(
					paramSql));
			if (printParam.getErrCode() < 0) {
				this.messageBox("获取打印参数错误");
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
							"Med_ApplyPrint.class", pR);// 报表合并
					// this.openPrintDialog("%ROOT%\\config\\prt\\MED\\Med_ApplyPrint.jhw",
					// pR, true);
					// modify by wangb 2016/09/19 一期临床使用单独的条码样式
					if ("PIC".equals(roleType)) {
						this
								.openPrintDialog(
										"%ROOT%\\config\\prt\\MED\\Med_ApplyPrintForPIC.jhw",
										pR, true);
					} else {
						this.openPrintDialog(IReportTool.getInstance()
								.getReportPath("Med_ApplyPrint.jhw"), pR, true);// 报表合并
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
						this.addText(5, 139, "送检组:"
								+ pR.getValue("EXEC_DEPT_CODE", "TEXT"));
						this.addText(191, 139, "科室:"
								+ pR.getValue("DEPT_CODE", "TEXT"));
						// STATION_CODE
						this.addText(5, 164, pR.getValue("ORDER_DESC", "TEXT"));
						// OPTITEM_CHN_DESC
						this.addText(5, 188, "采样时间:"
								+ pR.getValue("ORDER_DATE", "TEXT"));
						this.addText(5, 213, "医师备注:"
								+ pR.getValue("DR_NOTE", "TEXT"));
						this
								.addText(350, 80, pR.getValue("URGENT_FLG",
										"TEXT"));// (急)
					} else {
						TParm parm = new TParm();
						parm.setData(pR.getValue("PAT_NAME", "TEXT"), "");
						parm.setData(pR.getValue("SEX_DESC", "TEXT"), "");
						parm.setData(pR.getValue("BED_NO", "TEXT"), "");
						parm.setData("送检组:"
								+ pR.getValue("EXEC_DEPT_CODE", "TEXT"), "");
						parm.setData("科室:" + pR.getValue("DEPT_CODE", "TEXT"),
								"");
						parm.setData(pR.getValue("ORDER_DESC", "TEXT"), "");
						parm.setData("采样时间:"
								+ pR.getValue("ORDER_DATE", "TEXT"), "");
						parm.setData("医师备注:" + pR.getValue("DR_NOTE", "TEXT"),
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
						this.addGraphTextCode(5, 140, result.getValue("送检组:"
								+ pR.getValue("EXEC_DEPT_CODE", "TEXT")));
						this.addGraphTextCode(171, 140, result.getValue("科室:"
								+ pR.getValue("DEPT_CODE", "TEXT")));
						// STATION_CODE
						this.addGraphTextCode(5, 164, result.getValue(pR
								.getValue("ORDER_DESC", "TEXT")));
						// OPTITEM_CHN_DESC
						this.addGraphTextCode(5, 188, result.getValue("采样时间:"
								+ pR.getValue("ORDER_DATE", "TEXT")));
						this.addGraphTextCode(5, 212, result.getValue("医师备注:"
								+ pR.getValue("DR_NOTE", "TEXT")));
						this.addGraphTextCode(350, 80, result.getValue(pR
								.getValue("URGENT_FLG", "TEXT")));// (急)

						this.printText.append("^IDOUTSTR01^FS");// 清除图型
					}
					if (!printBarCode(printerPort)) {// 控制码打印条码
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
					this.messageBox("更新" + pR.getValue("PAT_NAME")
							+ "医嘱打印状态失败！");
					return;
				}
				/*
				 * // 叫号 CallNo call = new CallNo(); if (!call.init()) {
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
			this.messageBox("没有需要打印的项目！");
			return;
		}
	}
    
    /**
     * 设置图形文字的位置
     * @param x
     * @param y
     * @param code
     */
    public void addGraphTextCode(int x, int y, String code) {// wanglong add 20150410
        this.printText.append("^FO" + (x + offset_x) + "," + (y + offset_y) + code + "^FS");
    }
    
    /**
	 * 得到检验条码急做标记
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
     * 增加文字
     * @param x
     * @param y
     * @param str
     */
    public void addText(int x, int y, String str) {
        addText(x + offset_x, y + offset_y, 24, str);
    }

    /**
     * 增加文字
     * @param x
     * @param y
     * @param fontSize 字体大小
     * @param str
     */
    public void addText(int x, int y, int fontSize, String str) {// fontSize默认24
        this.printText.append(getTextCode(x, y, fontSize, str));
    }
    
    /**
     * 生成文字的控制码
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
	 * 拿到病区
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
	 * 拿到科室
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
	 * 拿到字典信息
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
     * 生成条码的控制码
     * @param x X坐标
     * @param y Y坐标
     * @param W 宽度
     * @param R Ratio
     * @param H 高度
     * @param barCode 条码
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
     * 打印控制码
     * @param port LPT端口号
     * @return
     */
    public boolean printBarCode(String port) {// wanglong add 20140610
        this.printText.insert(0, "^XA");
        this.printText.append("^XZ");
//        System.out.println("----------------控制码---------"+this.printText.toString());
        synchronized (this.printText) { // 同步 送 打印机
            FileWriter fw = null;
            PrintWriter out = null;
            try {
                fw = new FileWriter(port); // 数据送LPT3
                out = new PrintWriter(fw);
                out.print(this.printText.toString());
                return true;
            }
            catch (IOException e) {
                this.messageBox("打印错误：找不到使用" + port + "端口的打印机");
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
	 * 腕带打印
	 */
	public void printWristBands() {
		TParm tableParm = table.getParmValue();
		TParm showParm = table.getShowParmValue();
		if (tableParm == null) {
			this.messageBox("无打印数据");
			return;
		}
		TParm print = new TParm();
        int rowCount = tableParm.getCount();
        if (rowCount < 0) {
        	this.messageBox("无打印数据");
        	return;
        }
        
        if (!tableParm.getValue("CHOOSE").contains("Y")) {
        	this.messageBox("请勾选要打印的数据");
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
	 * 根据当前选择的团体信息辨别角色类型
	 * 
	 * @return roleType 角色类型(PIC_一期临床,H_健检)
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
	 * 病历整合
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
        	this.messageBox("请勾选已报到的数据");
        }
		
	}
}
