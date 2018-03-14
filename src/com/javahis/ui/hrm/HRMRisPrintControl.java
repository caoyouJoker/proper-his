package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMPatInfo;
import jdo.sta.STATool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title:检查申请单打印 </p>
 * 
 * <p> Description:检查申请单打印 </p>
 * 
 * <p> Copyright: Copyright (c) 2015 </p>
 * 
 * <p> Company: Bluecore </p>
 * 
 * @author wanglong 20150227
 * @version 1.0
 */
public class HRMRisPrintControl extends TControl {
    
    private TTable table;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;

    /**
     * 就诊号
     */
    private String caseNoList = "";

    /**
     * 团体代码、合同代码
     */
    private String companyCode, contractCode;// add by wanglong 20121214
    /**
     * 合同对象
     */
    private HRMContractD contractD;// add by wanglong 20121214
    /**
     * 合同TTextFormat
     */
    private TTextFormat contract;// add by wanglong 20121214
    /**
     * 角色类型(PIC_一期临床,H_健检)
     */
    private String roleType;

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
//      this.setValue("DEPT_CODE", "0404");//初始化 默认选中 放射科
        table = (TTable) this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxValue");
        addSortListener(table); // 排序监听
        contractD = new HRMContractD();
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        Object obj = this.getParameter();
        // this.messageBox(""+obj);
        if (obj != null) {
            if (obj instanceof TParm) {
                TParm parm = (TParm) obj;
                if(!parm.getValue("CASE_NO").equals("")){
                  this.setValue("MR_NO", parm.getValue("MR_NO"));
                  this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));
                  this.caseNoList=parm.getValue("CASE_NO");
                }else{
                    this.caseNoList=parm.getValue("CASE_NO");
                }
             
                this.companyCode = parm.getValue("COMPANY_CODE");
                this.contractCode = parm.getValue("CONTRACT_CODE");
                if (parm.getValue("POPEDEM").length() != 0) {
                    // 一般权限
                    if ("1".equals(parm.getValue("POPEDEM"))) {
                        this.setPopedem("NORMAL", true);
                        this.setPopedem("SYSOPERATOR", false);
                        this.setPopedem("SYSDBA", false);
                    }
                    // 角色权限
                    if ("2".equals(parm.getValue("POPEDEM"))) {
                        this.setPopedem("SYSOPERATOR", true);
                        this.setPopedem("NORMAL", false);
                        this.setPopedem("SYSDBA", false);
                    }
                    // 最高权限
                    if ("3".equals(parm.getValue("POPEDEM"))) {
                        this.setPopedem("SYSDBA", true);
                        this.setPopedem("NORMAL", false);
                        this.setPopedem("SYSOPERATOR", false);
                    }
                }
                
                // add by wangb 2016/09/19 只有健检默认选中放射科
                roleType = parm.getValue("ROLE_TYPE");
                if ("H".equals(roleType)) {
                	this.setValue("DEPT_CODE", "0404");//初始化 默认选中 放射科
                }
            }
            // else {
            // String date =
            // StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd")
            // + "000000";
            // this.admDate = StringTool.getTimestamp(date, "yyyyMMddHHmmss");
            // }
        }
        /**
         * 初始化权限
         */
        onInitPopeDem();
        /**
         * 初始化页面
         */
        initPage();
    }

    /**
     * 初始化权限
     */
    public void onInitPopeDem() {
        if (this.getPopedem("NORMAL")) {
            getTTextField("MR_NO").setEnabled(false);
            getTTextFormat("COMPANY_CODE").setEnabled(false);
            getTTextFormat("CONTRACT_CODE").setEnabled(false);
            getTTextField("START_SEQ_NO").setEnabled(false);
            getTTextField("END_SEQ_NO").setEnabled(false);
        }
        if (this.getPopedem("SYSOPERATOR") || this.getPopedem("SYSDBA")) {
            getTTextField("MR_NO").setEnabled(true);
            getTTextFormat("COMPANY_CODE").setEnabled(true);
            getTTextFormat("CONTRACT_CODE").setEnabled(true);
            getTTextField("START_SEQ_NO").setEnabled(true);
            getTTextField("END_SEQ_NO").setEnabled(true);
        }
    }

    /**
     * 初始化页面
     */
    public void initPage() {
        Timestamp sysDate = SystemTool.getInstance().getDate();
        Timestamp begin = STATool.getInstance().getLastMonth();// 健检的默认日期为前一个月
        Timestamp end =
                StringTool.getTimestamp(StringTool.getString(sysDate, "yyyy/MM/dd") + " 23:59:59",
                                        "yyyy/MM/dd HH:mm:ss");
        this.setValue("START_DATE", begin);
        this.setValue("END_DATE", end);
        this.setValue("COMPANY_CODE", this.companyCode);
        if (!StringUtil.isNullString(this.companyCode)) {
            TParm contractParm = contractD.onQueryByCompany(this.companyCode);
            contract.setPopupMenuData(contractParm);
            contract.setComboSelectRow();
            contract.popupMenuShowData();
            contract.setValue(this.contractCode);
        }
        // this.setValue("CONTRACT_CODE", this.contractCode); // 增加序号一列
        // 查询
        this.onQuery();
    }

    /**
     * checkBox勾选事件
     * 
     * @param obj
     */
    public void onCheckBoxValue(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = table.getDataStoreColumnName(col);
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        TParm tableParm = parm.getRow(row);
        String applicationNo = tableParm.getValue("APPLICATION_NO");
        if ("FLG".equals(columnName)) {
            int rowCount = parm.getCount("ORDER_DESC");
            for (int i = 0; i < rowCount; i++) {
                if (i == row) continue;
                if (applicationNo.equals(parm.getValue("APPLICATION_NO", i))) {
                    parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N" : "Y");
                }
            }
            table.setParmValue(parm);
        }
    }

    /**
     * 拿到TTextField
     * 
     * @return TTextFormat
     */
    public TTextField getTTextField(String tag) {
        return (TTextField) this.getComponent(tag);
    }

    /**
     * 返回TRadonButton
     * 
     * @param tag
     *            String
     * @return TRadioButton
     */
    public TRadioButton getTRadioButton(String tag) {
        return (TRadioButton) this.getComponent(tag);
    }

    /**
     * 拿到TTextFormat
     * 
     * @return TTextFormat
     */
    public TTextFormat getTTextFormat(String tag) {
        return (TTextFormat) this.getComponent(tag);
    }

    /**
     * 返回数据库操作工具
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        if (!this.getValueString("MR_NO").trim().equals("")&&caseNoList.equals("")) {
            this.setValue("MR_NO", PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
            this.setValue("PAT_NAME",
                          PatTool.getInstance().getInfoForMrno(this.getValueString("MR_NO"))
                                  .getValue("PAT_NAME", 0));
            TParm patInfParm = getPatInfo(this.getValueString("MR_NO"));
            this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
            this.caseNoList = patInfParm.getValue("CASE_NO");
        }
        String sql="";
        if(!"".equals(this.getValue("DEPT_CODE"))&&this.getValue("DEPT_CODE")!=null){
        	sql = getQuerySQL().replaceFirst("@", " B.EXEC_DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND ");
        }else{
        	sql = getQuerySQL().replaceFirst("@", "");
        }
       	
        TParm action = new TParm(this.getDBTool().select(sql));
        table.setParmValue(action);
        // 每次查询清空全选
        this.setValue("ALLCHECK", "N");
        
    }
    
    /**
     * 处理sql
     * @return
     */
   /* public String getSql(){
    	String sql="";
    	if("Y".equals(this.getValue("tCheckBox_0"))){
    		sql = getQuerySQL().replaceFirst("@", " B.EXEC_DEPT_CODE='0404' AND ");
    	}else{
    		sql = getQuerySQL().replaceFirst("@", "");
    	}
    	 return sql;
    }*/
    
    /**
     * 放射科勾选事件
     */
    /*public void selectAction(){
    	onQuery();
    }*/
    
    /**
     * 全选
     */
    public void onSelAll() {
        TParm parm = table.getParmValue();
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            if (((TCheckBox) this.getComponent("ALLCHECK")).isSelected()) parm.setData("FLG", i,
                                                                                       "Y");
            else parm.setData("FLG", i, "N");
        }
        table.setParmValue(parm);
    }

    /**
     * 团体代码选择事件
     */
    public void onCompanyChoose() {// add by wanglong 20121213
        companyCode = this.getValueString("COMPANY_CODE");
        TParm contractParm =new TParm();
        if (!StringUtil.isNullString(companyCode)) {
       // 根据团体代码查得该团体的合同主项
            contractParm = contractD.onQueryByCompany(companyCode);
            if (contractParm.getErrCode() != 0) {
                this.messageBox_("没有数据");
            }
        }
        
       
        // 构造一个TTextFormat,将合同主项赋值给这个控件，取得最后一个合同代码赋值给这个控件初始值
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contract.filter();
        contractCode = contractParm.getValue("ID", 0);
        contract.setValue(contractCode);
    }

    /**
     * 返回查询病患结果
     * @param mrNo
     * @return
     */
    public TParm getPatInfo(String mrNo) {
        TParm result = new TParm();
        if (!StringUtil.isNullString(caseNoList)&&caseNoList.indexOf(",")==-1) {
            TParm hParm =
                    new TParm(
                            this.getDBTool()
                                    .select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE CASE_NO='"
                                                    + this.caseNoList + "'"));
            result.setData("CASE_NO", hParm.getData("CASE_NO", 0));
            result.setData("DEPT_CODE", hParm.getData("DEPT_CODE", 0));
            return result;
        }
        TParm queryParm =
                new TParm(
                        this.getDBTool()
                                .select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE MR_NO='" + mrNo + "'"));
        if (queryParm.getCount() > 1) {
            queryParm.setData("ADM_TYPE", "H");
            Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
            if (obj != null) {
                TParm temp = (TParm) obj;
                result.setData("CASE_NO", temp.getData("CASE_NO"));
                result.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
            }
        } else {
            result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
            result.setData("DEPT_CODE", queryParm.getData("DEPT_CODE", 0));
        }
        return result;
    }

    /**
     * 得到查询语句
     * 
     * @return String
     */
    public String getQuerySQL() {
        String sql =
                "SELECT 'N' AS FLG, A.PRINT_FLG, A.URGENT_FLG, A.ORDER_CODE, A.ORDER_DESC, A.ORDER_DATE, B.DEPT_CODE,"
                        + "      B.DR_CODE, B.EXEC_DEPT_CODE, A.CASE_NO, A.MR_NO, A.PAT_NAME, A.APPLICATION_NO,"
                        + "      A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.PRINT_DATE, A.SEQ_NO, C.STAFF_NO "
                        + " FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C "
                        + "WHERE @ " //huangjw add "@"
                        + "  A.ADM_TYPE = 'H' "
                        + "  AND A.CAT1_TYPE='RIS' "
                        + "  AND B.SETMAIN_FLG='Y' "
                        + "  AND A.APPLICATION_NO = B.MED_APPLY_NO "
                        + "  AND A.ORDER_NO = B.CASE_NO "
                        + "  AND A.SEQ_NO = B.SEQ_NO  "
                        + "  AND B.CONTRACT_CODE = C.CONTRACT_CODE "
                        + "  AND B.MR_NO = C.MR_NO "
                        + "  AND A.STATUS <> 9 "
                        + "  AND A.START_DTTM BETWEEN TO_DATE('#','YYYYMMDDHH24MISS') "
                        + "                       AND TO_DATE('#','YYYYMMDDHH24MISS') ";
        sql =
                sql.replaceFirst("#", StringTool.getString((Timestamp) this.getValue("START_DATE"),
                                                           "yyyyMMddHHmmss"));
        sql =
                sql.replaceFirst("#", StringTool.getString((Timestamp) this.getValue("END_DATE"),
                                                           "yyyyMMddHHmmss"));
        if (getPrintStatus().length() != 0) {// 打印状态（未打印、已打印、全部）
            sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
        }
        if (this.getValueString("MR_NO").length() != 0) {// 病案号
            sql += " AND A.MR_NO = '" + this.getValueString("MR_NO") + "'";
        }
        if (this.getValueString("COMPANY_CODE").length() != 0) {// 团体号
            sql += " AND C.COMPANY_CODE = '" + this.getValueString("COMPANY_CODE") + "'";
        }
        if (this.getValueString("CONTRACT_CODE").length() != 0) {// 合同号
            sql += " AND C.CONTRACT_CODE = '" + this.getValueString("CONTRACT_CODE") + "'";
        }
        if (this.getValueString("START_SEQ_NO").length() != 0) {// 员工序号开始
            sql += " AND C.SEQ_NO >= '" + this.getValueString("START_SEQ_NO") + "'";
        }
        if (this.getValueString("END_SEQ_NO").length() != 0) {// 员工序号结束
            sql += " AND C.SEQ_NO <= '" + this.getValueString("END_SEQ_NO") + "'";
        }
        sql += " ORDER BY C.SEQ_NO, A.CAT1_TYPE,A.START_DTTM DESC,A.CASE_NO";// caowl 20130305
                                                                             // 按日期升序排列
//        System.out.println("健康检查sql:" + sql);
        return sql;
    }

    /**
     * 得到打印状态
     * 
     * @return String
     */
    public String getPrintStatus() {
        if (this.getTRadioButton("ALLPRINT").isSelected()) return "";
        if (this.getTRadioButton("UNPRINT").isSelected()) return "N";
        if (this.getTRadioButton("PRINTED").isSelected()) return "Y";
        return "";
    }

    /**
     * 条码打印
     */
    public void onPrint() {
        table.acceptText();
        ArrayList<String> deptList = new ArrayList<String>();
        ArrayList<String> orderList = new ArrayList<String>();
        String sql = "SELECT * FROM HRM_SYSPARM";
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (sysParm.getErrCode() < 0) {
            this.messageBox(sysParm.getErrText());
        } else {
            if (sysParm.getValue("RIS_PRINT", 0).equals("N")) {
                return;
            } else if (!sysParm.getValue("RIS_PRINT", 0).equals("Y")) {
                String deptSql = "SELECT * FROM HRM_RISDEPT";
                TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
                if (deptParm.getErrCode() < 0) {
                    this.messageBox(deptParm.getErrText());
                } else {
                	// 超声科室代码(暂时写死)
                	String dept = "0405";
                	// add by wangb 2016/09/19  一期临床打印申请单无需排除超声科室
                	for (int i = 0; i < deptParm.getCount(); i++) {
						if ("PIC".equals(roleType)
								&& dept.equals(deptParm
										.getValue("DEPT_CODE", i))) {
							continue;
						}
                        deptList.add(deptParm.getValue("DEPT_CODE", i));
                    }
                }
                String orderSql = "SELECT * FROM HRM_RISORDER";
                TParm orderParm = new TParm(TJDODBTool.getInstance().select(orderSql));
                if (orderParm.getErrCode() < 0) {
                    this.messageBox(orderParm.getErrText());
                } else {
                    for (int i = 0; i < orderParm.getCount(); i++) {
                        orderList.add(orderParm.getValue("ORDER_CODE", i));
                    }
                }
            }
        }
        Timestamp now = SystemTool.getInstance().getDate();
        boolean flag = false;
        HRMPatInfo pat = new HRMPatInfo();
        TParm parmValue = table.getParmValue();
        TParm copyParm = cloneParm(parmValue);
        for (int i = 0; i < copyParm.getCount(); i++) {
            if (!copyParm.getBoolean("FLG", i)
                    || deptList.contains(copyParm.getValue("EXEC_DEPT_CODE", i))// 排除指定执行科室
                    || orderList.contains(copyParm.getValue("ORDER_CODE", i))// 排除指定医嘱
            ) {
                copyParm.setData("FLG", i, "N");
                continue;
            }
            if (copyParm.getBoolean("FLG", i)) {
                // flag = true;
                ArrayList<String> applicationNoList = new ArrayList<String>();
                String orderDescList = copyParm.getValue("ORDER_DESC", i);
                applicationNoList.add(copyParm.getValue("APPLICATION_NO", i));
                for (int j = 0; j < copyParm.getCount(); j++) {
                    if (j == i || !copyParm.getBoolean("FLG", j)
                            || deptList.contains(copyParm.getValue("EXEC_DEPT_CODE", i))// 排除指定执行科室
                            || orderList.contains(copyParm.getValue("ORDER_CODE", i))// 排除指定医嘱
                    ) {
                        copyParm.setData("FLG", i, "N");
                        continue;
                    }
                    if (copyParm.getValue("EXEC_DEPT_CODE", i)
                            .equals(copyParm.getValue("EXEC_DEPT_CODE", j))
                            && copyParm.getValue("DEV_CODE", i)
                                    .equals(copyParm.getValue("DEV_CODE", j))
                                    		&& copyParm.getValue("MR_NO", i)
                                    			.equals(copyParm.getValue("MR_NO", j))
                                    				&& copyParm.getBoolean("FLG", i)//copyParm.getBoolean("FLG", i) add by huangjw 20150728
                         
                    ) {
                        orderDescList += "；" + copyParm.getValue("ORDER_DESC", j);
                        applicationNoList.add(copyParm.getValue("APPLICATION_NO", j));
                    }
                }
                TParm inParam = new TParm();
                TParm patInfo =
                        pat.getHRMPatInfo(copyParm.getValue("MR_NO", i),
                                          copyParm.getValue("CASE_NO", i));
                inParam.setData("MR_NO", "TEXT", copyParm.getValue("MR_NO", i));
                inParam.setData("Barcode", "TEXT", copyParm.getValue("MR_NO", i));
                inParam.setData("SEQ_NO", patInfo.getValue("SEQ_NO", 0));// 人员的序号，非医嘱序号
                inParam.setData("PAT_NAME", patInfo.getValue("PAT_NAME", 0));
                inParam.setData("SEX_DESC", patInfo.getValue("SEX_DESC", 0));
                inParam.setData("BIRTHDAY", StringTool.getString(patInfo
                        .getTimestamp("BIRTHDAY", 0), "yyyy-MM-dd"));
                inParam.setData("AGE", StringTool.CountAgeByTimestamp(patInfo
                        .getTimestamp("BIRTHDAY", 0), now)[0]);
                inParam.setData("TEL", patInfo.getValue("TEL", 0));
                inParam.setData("DR_DESC",
                                StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='"
                                        + copyParm.getValue("DR_CODE", i) + "'"));
                inParam.setData("DEPT_DESC", patInfo.getValue("DEPT_DESC", 0));
                inParam.setData("COMPANY_DESC", patInfo.getValue("COMPANY_DESC", 0));
                inParam.setData("CONTRACT_DESC", patInfo.getValue("CONTRACT_DESC", 0));
                inParam.setData("EXEC_DEPT_DESC",
                                StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='"
                                        + copyParm.getValue("EXEC_DEPT_CODE", i) + "'"));
                inParam.setData("ORDER_DESC", orderDescList);
                inParam.setData("ORDER_DATE", StringTool.getString(now, "yyyy/MM/dd"));
                // add by wangb 2016/09/20 一期临床检查申请单增加筛选号
                if ("PIC".equals(roleType)) {
                	inParam.setData("FILTER_NO", "TEXT",  "筛选号:" + copyParm.getValue("STAFF_NO", i));
                	// 一期临床信息安全需要隐藏电话号码
                	inParam.setData("TEL", "");
                }
                
                this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMRisSheet.jhw", inParam, true);
                flag = true;
                for (int k = 0; k < copyParm.getCount(); k++) {
                    if (applicationNoList.contains(copyParm.getValue("APPLICATION_NO", k))) {
                        copyParm.setData("FLG", k, "N");
                    }
                }
                for (int l = 0; l < applicationNoList.size(); l++) {
                    String updateMedApply =
                            "UPDATE MED_APPLY SET PRINT_FLG='Y',PRINT_DATE=SYSDATE "
                                    + " WHERE CAT1_TYPE = 'RIS' AND APPLICATION_NO='#'";// wanglong
                                                                                        // modify
                                                                                        // 20140610
                    updateMedApply = updateMedApply.replaceFirst("#", applicationNoList.get(l));
                    TParm result = new TParm(TJDODBTool.getInstance().update(updateMedApply));
                    if (result.getErrCode() < 0) {
                        this.messageBox("更新" + copyParm.getValue("PAT_NAME", i) + "医嘱打印状态失败！");
                        return;
                    }
                }
            }
        }
        if (!flag) {
            this.messageBox("没有需要打印的项目！");
            return;
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        Timestamp lastMonth = STATool.getInstance().getLastMonth();// 健检的默认日期为前一个月
        Timestamp sysdate = SystemTool.getInstance().getDate();
        Timestamp todayEnd =
                StringTool.getTimestamp(StringTool.getString(sysdate, "yyyy/MM/dd") + " 23:59:59",
                                        "yyyy/MM/dd HH:mm:ss");
        this.setValue("START_DATE", lastMonth);
        this.setValue("END_DATE", todayEnd);
        this.getTRadioButton("UNPRINT").setSelected(true);
        this.setValue("ALLCHECK", "N");
        table.removeRowAll();
        if (!this.getPopedem("NORMAL")) {
            this.clearValue("MR_NO;PAT_NAME;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO");    
        }
        this.setValue("ALLCHECK", "Y");
    }

    /**
     * 右键
     */
    public void showPopMenu() {
        TTable table = (TTable) this.getComponent("TABLE");
        table.setPopupMenuSyntax("显示集合医嘱细相 \n Display collection details with your doctor,openRigthPopMenu|TABLE");
    }

    /**
     * 细项
     */
    public void openRigthPopMenu(String tableName) {
        TTable table = (TTable) this.getComponent(tableName);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        // System.out.println("选中行:"+parm);
        TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
    }

    /**
     * 返回集合医嘱细相的TParm形式
     * 
     * @return result TParm
     */
    public TParm getOrderSetDetails(String orderCode) {
        TParm result = new TParm();
        String sql =
                "SELECT B.*,A.DOSAGE_QTY FROM SYS_ORDERSETDETAIL A,SYS_FEE B "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDERSET_CODE='" + orderCode
                        + "'";
        TParm parm = new TParm(this.getDBTool().select(sql));
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
            result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
            result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
            // 计算总价格
            double ownPrice = parm.getDouble("OWN_PRICE", i) * parm.getDouble("DOSAGE_QTY", i);
            result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
            result.addData("OWN_AMT", ownPrice);
            result.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
            result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
            result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
        }
        return result;
    }

    /**
     * 克隆TParm
     * 
     * @param srcParm
     * @return
     */
    private TParm cloneParm(TParm srcParm) {// add by wanglong 20121221
        TParm copyParm = new TParm();
        String[] names = srcParm.getNames();
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < srcParm.getCount(); j++) {
                copyParm.addData(names[i], srcParm.getData(names[i], j));
            }
        }
        copyParm.setCount(copyParm.getCount(names[0]));
        return copyParm;
    }

    // ====================排序功能begin======================add by wanglong 20121217
    /**
     * 加入表格排序监听方法
     * 
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
     * 
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
        if (size > 0 && count > size) count = size;
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
     * 
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
     * 
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames,
                                   final TTable table) {
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
}
