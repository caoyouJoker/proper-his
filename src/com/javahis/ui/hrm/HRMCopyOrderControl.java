package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import com.javahis.util.StringUtil;
import com.javahis.system.textFormat.TextFormatHRMPatPackage;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;


/**
 * <p> Title: 健检复制他人医嘱界面 </p>
 * 
 * <p> Description: 健检复制他人医嘱界面  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 2013.05.07
 * @version 1.0
 */
public class HRMCopyOrderControl extends TControl {
    

    TTable table;
    TTextFormat  contract;
    TextFormatHRMPatPackage patPackage;
    private TParm inParm;
    private String companyCode, contractCode;// 团体代码、合同代码
    private HRMContractD contractD;// 合同对象
    private HRMPatAdm adm;// 报到对象
    private HRMOrder order;
    
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        patPackage = (TextFormatHRMPatPackage) this.getComponent("PAT_PACKAGE");
        contractD = new HRMContractD();
        contractD.onQuery("", "", "");
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery();
        if (this.getParameter() != null && !StringUtil.isNullString(this.getParameter() + "")
                && (this.getParameter() instanceof TParm)) {
            inParm = (TParm) this.getParameter();
            int patCount = inParm.getCount("MR_NO");
            if (patCount >= 1000) {
                this.messageBox("同时操作的人员数不要超过1000！");
                this.closeWindow();
            }
            this.setValue("COMPANY_CODE", inParm.getValue("COMPANY_CODE", 0));
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        onCompanyChoose();
                        contract.setValue(inParm.getValue("CONTRACT_CODE", 0));
                        onContractChoose();
                    }
                    catch (Exception e) {}
                }
            });
        }
    }
    
	/**
	 * 复制医嘱
	 */
    public void onCopy() {
        String copyMrNo = this.getValueString("PAT_PACKAGE");
        if (TCM_Transform.isNull(copyMrNo)) {
            this.messageBox("病案号为空！");
            return;
        }
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (TCM_Transform.isNull(contractCode)) {
            this.messageBox("合同号为空！");
            return;
        }
        int patCount = inParm.getCount("MR_NO");
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < patCount; i++) {
            mrList += "'" + inParm.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patList = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patList.getErrCode() != 0 || patList.getCount() < 1) {
            this.messageBox("查询病患信息出错");
            return;
        }
        boolean flag = true;
        for (int i = 0; i < patCount; i++) {
            TParm parmRow = inParm.getRow(i);
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
            // 判断是未报到，则保存数据，但设置成未报到
            // 1.插入HRM_PATADM
            if (StringUtil.isNullString(caseNo)) {
                TParm patParm = new TParm();
                for (int j = 0; j < patList.getCount(); j++) {// add by wanglong 20130304
                    if (patList.getValue("MR_NO", j).equals(mrNo)) {
                        patParm = patList.getRow(j);
                    }
                }
                patParm.setData("PAT_NAME", inParm.getData("PAT_NAME", i));
                patParm.setData("COMPANY_PAY_FLG", inParm.getData("COMPANY_PAY_FLG", i));
                patParm.setData("COMPANY_CODE", inParm.getData("COMPANY_CODE", i));
                patParm.setData("CONTRACT_CODE", inParm.getData("CONTRACT_CODE", i));
                String packCode = inParm.getValue("PACKAGE_CODE", i);
                patParm.setData("PACKAGE_CODE", packCode);
                patParm.setData("REPORTLIST", inParm.getData("REPORTLIST", i));
                patParm.setData("INTRO_USER", inParm.getData("INTRO_USER", i));
                patParm.setData("DISCNT", inParm.getData("DISCNT", i));
                patParm.setData("TEL", inParm.getData("TEL", i));
                patParm.setData("MARRIAGE_CODE", inParm.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
                patParm.setData("PAT_DEPT", inParm.getData("PAT_DEPT", i));// add by wanglong 20130225
                // 1.预报到
                Timestamp now = order.getDBTime();
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("序号:" + inParm.getData("SEQ_NO", i) + "  姓名："
                            + inParm.getData("PAT_NAME", i) + ",预报到生成HRM_PATADM数据失败");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery();
                    flag = false;
                    continue;
                }
                // 2.HRM_ORDER
                String admCaseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                // caseNo = admCaseNo1;
                if (StringUtil.isNullString(admCaseNo1)) {
                    this.messageBox_("序号:" + inParm.getData("SEQ_NO", i) + "  姓名："
                            + inParm.getData("PAT_NAME", i) + ",取得数据失败");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery();
                    flag = false;
                    continue;
                }
                patParm.setData("CASE_NO", admCaseNo1);
                order.filt(admCaseNo1);
                order.initOrderByCaseNo(contractCode, copyMrNo, patParm);
                String[] sql = adm.getUpdateSQL();
                sql = StringTool.copyArray(sql, order.getUpdateSQL());
                sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
                // 配置送入后台保存方法的参数，并验证后台保存方法的返回值是否成功
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                     inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("序号:" + inParm.getData("SEQ_NO", i) + "  姓名:"
                            + inParm.getData("PAT_NAME", i) + "\n医嘱展开失败");
                }
            } else {
                this.messageBox("序号:" + inParm.getData("SEQ_NO", i) + "  姓名:"
                        + inParm.getData("PAT_NAME", i) + "\n医嘱已展开，将被跳过");
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        if (flag == true) {
            this.messageBox("医嘱展开成功");
        }
        this.closeWindow();
    }

	/**
	 * 取消
	 */
	public void onCancel() {
		this.closeWindow();
	}
	
	/**
     * 团体代码点选事件
     */
    public void onCompanyChoose() {
        // 根据选择的团体代码，构造并初始化该团体的合同信息TTextFormat
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
            this.messageBox_("查询合同失败");
            return;
        }
        contract.setValue(contractCode);
        onContractChoose();
    }

    /**
     * 合同代码点选事件
     */
    public void onContractChoose() {
        contractCode = this.getValueString("CONTRACT_CODE");
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        patPackage.setContractCode(contractCode);
        patPackage.setFilterFlg("Y");
        patPackage.onQuery();
    }
    
    /**
     * 查看医嘱明细
     */
    public void onShowDetail() {
        String mrNo = this.getValueString("PAT_PACKAGE");
        String sql =
                "SELECT DISTINCT A.MR_NO,A.PAT_NAME,A.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC||' '||B.SPECIFICATION ORDER_DESC,A.DISPENSE_QTY,"
                        + "A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,A.OWN_PRICE OWN_AMT,A.DISCOUNT_RATE,A.OWN_AMT AR_AMT "
                        + " FROM (SELECT MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,SUM(DISPENSE_QTY) "
                        + "              DISPENSE_QTY,SUM(OWN_PRICE) OWN_PRICE,SUM(OWN_AMT) OWN_AMT "
                        + "        FROM (SELECT A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                     SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.ORDERSET_GROUP_NO "
                        + "               FROM HRM_PATADM A, HRM_ORDER B "
                        + "              WHERE A.CASE_NO = B.CASE_NO "
                        + "                AND B.CAT1_TYPE <> 'PHA' "
                        + "                AND A.CONTRACT_CODE = '#' "
                        + "                AND A.MR_NO = '#' "
                        + "           GROUP BY A.MR_NO,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO) "
                        + "     GROUP BY MR_NO,CASE_NO,PAT_NAME,ORDERSET_CODE,DISCOUNT_RATE,OWN_PRICE) A, HRM_ORDER B "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE" + " AND A.CASE_NO = B.CASE_NO "
                        + "ORDER BY A.PAT_NAME, ORDER_DESC";
        sql = sql.replaceFirst("#", contractCode);
        sql = sql.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("查询医嘱明细失败 " + result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.setValue("OWN_AMT", "");
            table.setParmValue(new TParm());
        } else {
            double ownAmt = 0;
            for (int i = 0; i < result.getCount(); i++) {
                ownAmt += result.getDouble("OWN_AMT", i);
            }
            ownAmt = StringTool.round(ownAmt, 2);
            this.setValue("OWN_AMT", ownAmt + "");
            table.setParmValue(result);
        }
    }
}
