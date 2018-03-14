package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:健检团体费用统计
 * </p>
 * 
 * <p>
 * Description:健检团体费用统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:BLueCore
 * </p>
 * 
 * @author wangLong 20120620
 * @version 1.0
 */
public class HRMCompanyFeeStatisticsControl
        extends TControl {

    private TTable table;
    private TTextFormat contract;// 合同下拉框
    private HRMContractD contractD;// 合同DataStore
    private InnerComparator comparator = new InnerComparator();
    private BILComparator compare = new BILComparator();// add by wanglong 20130312
    private boolean ascending = false;
    private int sortColumn = -1;
    private TTextFormat company;// add by wangb 2016/06/27 团体名称
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// add by wanglong 20130312
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        onClear();
    }

    /**
     * 查询    //refactor by wanglong 20130110
     */
    public void onQuery() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.messageBox("请填写团体名称");
            return;
        }
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.messageBox("请填写合同名称");
            return;
        }
        String whereSql = "";
        if (this.getValueString("UNREPORT").equalsIgnoreCase("Y")) {
            whereSql = "   AND CC.COVER_FLG = 'N' ";// modify by wanglong 20130224
        } else if (this.getValueString("REPORTED").equalsIgnoreCase("Y")) {
            whereSql = "   AND CC.COVER_FLG = 'Y' ";// modify by wanglong 20130130
        }
        String dataSql = // modify by wanglong 20130510
                "WITH AA AS (SELECT A.SEQ_NO,A.PAT_DEPT,A.STAFF_NO,A.MR_NO,B.CASE_NO,A.PAT_NAME,A.SEX_CODE,A.COVER_FLG,A.REAL_CHK_DATE,"
                        + "         A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,B.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC,"
                        + "         SUM(B.DISPENSE_QTY) DISPENSE_QTY,SUM(B.OWN_AMT) OWN_AMT,SUM(B.DISCOUNT_RATE) DISCOUNT_RATE,"
                        + "         SUM(B.AR_AMT) AR_AMT,A.BILL_FLG,A.DISCNT "
                        + "    FROM (SELECT MR_NO, CASE_NO,CONTRACT_CODE,ORDERSET_CODE,ORDER_DESC,DISCOUNT_RATE,"
                        + "                 SUM(DISPENSE_QTY) DISPENSE_QTY,SUM(OWN_AMT) OWN_AMT,SUM(AR_AMT) AR_AMT,BILL_FLG "
                        + "            FROM (SELECT B.MR_NO,B.CASE_NO,B.CONTRACT_CODE,B.ORDERSET_CODE,C.ORDER_DESC,"
                        + "                         SUM(CASE WHEN B.ORDER_CODE=B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                         SUM(CASE WHEN B.ORDER_CODE=B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                         SUM(B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) AR_AMT,B.BILL_FLG,B.ORDERSET_GROUP_NO "
                        + "                    FROM HRM_ORDER B, SYS_FEE C "
                        + "                   WHERE B.ORDERSET_CODE = C.ORDER_CODE "
                        + "                     AND B.CONTRACT_CODE ='"
                        + contractCode
                        + "'               GROUP BY B.MR_NO,B.CASE_NO,B.CONTRACT_CODE,B.ORDERSET_CODE,C.ORDER_DESC,B.BILL_FLG,B.ORDERSET_GROUP_NO) "
                        + "        GROUP BY MR_NO,CASE_NO,CONTRACT_CODE,ORDERSET_CODE,ORDER_DESC,DISCOUNT_RATE,BILL_FLG) B, HRM_CONTRACTD A "
                        + "   WHERE A.MR_NO = B.MR_NO "
                        + "     AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "GROUP BY A.SEQ_NO,A.PAT_DEPT,A.STAFF_NO,A.MR_NO,B.CASE_NO,A.PAT_NAME,A.SEX_CODE,A.COVER_FLG,A.REAL_CHK_DATE,"
                        + "         A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,B.ORDERSET_CODE,B.ORDER_DESC,A.BILL_FLG,A.DISCNT),"
                        + "BB AS (SELECT DISTINCT C.MR_NO,C.CONTRACT_CODE,EXEC_DR_CODE "
                        + "         FROM (SELECT A.MR_NO,A.CONTRACT_CODE,B.ORDER_CODE,B.SETMAIN_FLG,B.DEPT_ATTRIBUTE,B.EXEC_DR_CODE "
                        + "                 FROM HRM_CONTRACTD A, HRM_ORDER B "
                        + "                WHERE A.MR_NO = B.MR_NO(+) "
                        + "                  AND A.CONTRACT_CODE = B.CONTRACT_CODE(+) "
                        + "                  AND A.CONTRACT_CODE ='"
                        + contractCode
                        + "') C"
                        + "        WHERE C.ORDER_CODE IS NOT NULL "
                        + "          AND C.SETMAIN_FLG = 'Y' "
                        + "          AND C.DEPT_ATTRIBUTE = '04' "
                        + "          AND C.EXEC_DR_CODE IS NOT NULL),"
                        + "CC AS (SELECT AA.*, CASE WHEN NVL(BB.EXEC_DR_CODE, 'x') = 'x' THEN 'N' ELSE 'Y' END FINISH_FLG "
                        + "              FROM AA, BB "
                        + "             WHERE AA.CONTRACT_CODE = BB.CONTRACT_CODE(+) "
                        + "               AND AA.MR_NO = BB.MR_NO(+)) "
                        + "SELECT DISTINCT CC.SEQ_NO,CC.PAT_DEPT,CC.STAFF_NO,CC.MR_NO,CC.PAT_NAME,CC.SEX_CODE,CC.COVER_FLG,CC.REAL_CHK_DATE,"
                        + "        CC.BILL_FLG,CC.COMPANY_CODE,CC.CONTRACT_CODE,CC.PACKAGE_CODE,CC.ORDER_CODE,CC.ORDER_DESC,CC.OWN_AMT,"
                        + "        CC.DISCNT,CC.AR_AMT,CC.FINISH_FLG,D.CAT1_TYPE,D.EXEC_DEPT_CODE "
                        + " FROM CC, SYS_FEE D "
                        + "WHERE CC.ORDER_CODE = D.ORDER_CODE "
                        + whereSql
                        + "ORDER BY CC.MR_NO, CC.ORDER_CODE";
//        System.out.println("================dataSql============"+dataSql);
        // 准备工作1:查出每个人做的项目、费用等信息（多条），后面要把各个项目变成横向显示（最后每个人只显示一行记录）
        TParm dataParm = new TParm(TJDODBTool.getInstance().select(dataSql));
        if (dataParm.getErrCode() != 0) {
            this.messageBox("E0035");// 操作失败
            return;
        }
        if (dataParm.getCount() <= 0) {
            table.setParmValue(new TParm());
            this.messageBox("E0116");// 没有数据
            return;
        }
        String titleSql = // modify by wanglong 20130130
                "SELECT DISTINCT C.*, D.CAT1_TYPE, D.EXEC_DEPT_CODE "
                        + "FROM (                                "
//                        + "SELECT DISTINCT B.ORDER_CODE, B.ORDER_DESC"//delete by wanglong 20130415
//                        + "        FROM HRM_CONTRACTD A, HRM_PACKAGED B "
//                        + "       WHERE A.CONTRACT_CODE = '" + contractCode
//                        + "'        AND A.PACKAGE_CODE = B.PACKAGE_CODE "
//                        + "         AND B.SETMAIN_FLG = 'Y'      "
//                        + "       UNION                          "
                        + "      SELECT DISTINCT B.ORDER_CODE, B.ORDER_DESC, B.DEPT_ATTRIBUTE "//modify by wanglong 20130422
                        + "        FROM HRM_ORDER B              "
                        + "       WHERE B.CONTRACT_CODE = '" + contractCode
                        + "'        AND B.SETMAIN_FLG = 'Y') C, SYS_FEE D "
                        + " WHERE C.ORDER_CODE = D.ORDER_CODE ORDER BY C.ORDER_DESC DESC";
        // 准备工作2:查询各个套餐所含医嘱的最小集合(标题上不能出现重复的医嘱列)
//        System.out.println("================titleSql============"+titleSql);
        TParm titleParm = new TParm(TJDODBTool.getInstance().select(titleSql));
        // ===============第一步:以下将最小集合中的医嘱进行特定排序(按照医院要求的顺序)=====
        List<TParm> commonHeader = new ArrayList<TParm>();
        HashSet orderSet = new HashSet();//add by wanglong 20130419
        for (int i = 0; i < titleParm.getCount(); i++) {
            TParm order = titleParm.getRow(i);
            int oldlength = orderSet.size();
            orderSet.add(order.getValue("ORDER_CODE"));
            int newlength = orderSet.size();
            if (newlength != oldlength) {//add by wanglong 20130419
                commonHeader.add(order);
            }
        }
        java.util.Collections.sort(commonHeader, comparator);
        // ===============第二步:以下设定table的3个属性参数===============================
        String header =
                "序号,50;部门,120;工号,60;病案号,110;姓名,80;性别,60,SEX_CODE;报到状态,80,COVER_FLG;报到日期,140,timestamp,yyyy/MM/dd HH:mm:ss;结算状态,80,BILL_FLG;总检状态,80,FINISH_FLG;团体名称,170,COMPANY_COMBO;合同名称,130,CONTRACT_CODE;套餐名称,150,PACKAGE_CODE;";
        String parmMap =
                "SEQ_NO;PAT_DEPT;STAFF_NO;MR_NO;PAT_NAME;SEX_CODE;COVER_FLG;REAL_CHK_DATE;BILL_FLG;FINISH_FLG;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;";
        String alignment =
                "0,center;1,left;2,left;3,center;4,left;5,center;6,center;7,center;8,center;9,center;10,left;11,left;12,left;";
        String orderHeader = "";
        for (TParm tParm : commonHeader) {
            header +=
                    tParm.getValue("ORDER_DESC")
                            + countTitleItemLength(tParm.getValue("ORDER_DESC"));
            orderHeader += tParm.getValue("ORDER_CODE") + ";";
        }
        parmMap += orderHeader;
        int i = 0;
        for (; i < commonHeader.size(); i++) {
            alignment += (i + 13) + ",right;";
        }
        header += "应收金额,80,double,#########0.00;折扣,60;实收金额,80,double,#########0.00";
        parmMap += "OWN_AMT;DISCNT;AR_AMT";
        alignment += (i + 13) + ",right;" + (i + 14) + ",right;" + (i + 15) + ",right";
        table.setHeader(header);
        table.setColumnHorizontalAlignmentData(alignment);
        table.setParmMap(parmMap);
        // ===============第三步:生成ParmValue===========================================
        TParm parmValue = new TParm();
        String[] titleItem = parmMap.split(";");
        for (int j = 0; j < dataParm.getCount(); j++) {
            if (j == 0) {
                for (int k = 0; k < titleItem.length; k++) {
                    parmValue.addData(titleItem[k], null);// 给parmValue设置初始值null,用于占位
                }
            } else if (dataParm.getValue("MR_NO", j).equals(dataParm.getValue("MR_NO", j - 1))) {
                continue;
            } else {
                for (int k = 0; k < titleItem.length; k++) {
                    parmValue.addData(titleItem[k], null);// 给parmValue设置初始值null,用于占位
                }
            }
        }
        String[] orderTitleItem = orderHeader.split(";");
        int count = 0;// 记录数
        double ownAmt = 0;// 每一个病患的应收金额
        double arAmt = 0;// 每一个病患的实收金额
        for (int j = 0; j < dataParm.getCount(); j++) {
            if (j == 0) {
                parmValue.setData("SEQ_NO", count, dataParm.getData("SEQ_NO", j));
                parmValue.setData("PAT_DEPT", count, dataParm.getData("PAT_DEPT", j));
                parmValue.setData("STAFF_NO", count, dataParm.getData("STAFF_NO", j));
                parmValue.setData("MR_NO", count, dataParm.getData("MR_NO", j));
                parmValue.setData("PAT_NAME", count, dataParm.getData("PAT_NAME", j));
                parmValue.setData("SEX_CODE", count, dataParm.getData("SEX_CODE", j));
                parmValue.setData("COVER_FLG", count, dataParm.getData("COVER_FLG", j));
                parmValue.setData("REAL_CHK_DATE", count, dataParm.getData("REAL_CHK_DATE", j));
                parmValue.setData("BILL_FLG", count, dataParm.getData("BILL_FLG", j));
                parmValue.setData("FINISH_FLG", count, dataParm.getData("FINISH_FLG", j));
                parmValue.setData("COMPANY_CODE", count, dataParm.getData("COMPANY_CODE", j));
                parmValue.setData("CONTRACT_CODE", count, dataParm.getData("CONTRACT_CODE", j));
                parmValue.setData("PACKAGE_CODE", count, dataParm.getData("PACKAGE_CODE", j));
                parmValue.setData("DISCNT", count, dataParm.getData("DISCNT", j));
                for (int k = 0; k < orderTitleItem.length; k++) {
                    if (dataParm.getValue("ORDER_CODE", j).equals(orderTitleItem[k])) {
                        parmValue.setData(orderTitleItem[k], count, dataParm.getData("OWN_AMT", j));
                        ownAmt += StringTool.round(dataParm.getDouble("OWN_AMT", j), 2);
                        arAmt += StringTool.round(dataParm.getDouble("AR_AMT", j), 2);
                        break;//add by wanglong 20130419
                    }
                }
            } else if (dataParm.getValue("MR_NO", j).equals(dataParm.getData("MR_NO", j - 1))) {
                for (int k = 0; k < orderTitleItem.length; k++) {
                    if (dataParm.getValue("ORDER_CODE", j).equals(orderTitleItem[k])) {
                        parmValue.setData(orderTitleItem[k], count, dataParm.getData("OWN_AMT", j));
                        ownAmt += StringTool.round(dataParm.getDouble("OWN_AMT", j), 2);
                        arAmt += StringTool.round(dataParm.getDouble("AR_AMT", j), 2);
                        break;//add by wanglong 20130419
                    }
                }
            } else {
                parmValue.setData("OWN_AMT", count, ownAmt);
                parmValue.setData("AR_AMT", count, arAmt);
                ownAmt = 0;
                arAmt = 0;
                count++;
                parmValue.setData("SEQ_NO", count, dataParm.getData("SEQ_NO", j));
                parmValue.setData("PAT_DEPT", count, dataParm.getData("PAT_DEPT", j));
                parmValue.setData("STAFF_NO", count, dataParm.getData("STAFF_NO", j));
                parmValue.setData("MR_NO", count, dataParm.getData("MR_NO", j));
                parmValue.setData("PAT_NAME", count, dataParm.getData("PAT_NAME", j));
                parmValue.setData("SEX_CODE", count, dataParm.getData("SEX_CODE", j));
                parmValue.setData("COVER_FLG", count, dataParm.getData("COVER_FLG", j));
                parmValue.setData("REAL_CHK_DATE", count, dataParm.getData("REAL_CHK_DATE", j));
                parmValue.setData("BILL_FLG", count, dataParm.getData("BILL_FLG", j));
                parmValue.setData("FINISH_FLG", count, dataParm.getData("FINISH_FLG", j));
                parmValue.setData("COMPANY_CODE", count, dataParm.getData("COMPANY_CODE", j));
                parmValue.setData("CONTRACT_CODE", count, dataParm.getData("CONTRACT_CODE", j));
                parmValue.setData("PACKAGE_CODE", count, dataParm.getData("PACKAGE_CODE", j));
                parmValue.setData("DISCNT", count, dataParm.getData("DISCNT", j));
                for (int k = 0; k < orderTitleItem.length; k++) {
                    if (dataParm.getValue("ORDER_CODE", j).equals(orderTitleItem[k])) {
                        parmValue.setData(orderTitleItem[k], count, dataParm.getData("OWN_AMT", j));
                        ownAmt += StringTool.round(dataParm.getDouble("OWN_AMT", j), 2);
                        arAmt += StringTool.round(dataParm.getDouble("AR_AMT", j), 2);
                        break;//add by wanglong 20130419
                    }
                }
            }
        }
        parmValue.setData("OWN_AMT", count, ownAmt);
        parmValue.setData("AR_AMT", count, arAmt);
        count++;
        parmValue.setCount(count);
        double sumOwnAmt = 0;
        double sumArAmt = 0;
        for (int k = 0; k < count; k++) {
            sumOwnAmt += StringTool.round(parmValue.getDouble("OWN_AMT", k), 2);
            sumArAmt += StringTool.round(parmValue.getDouble("AR_AMT", k), 2);
        }
        double[] orderItemTotAmt = new double[orderTitleItem.length];
        for (int k = 0; k < orderTitleItem.length; k++) {
            for (int k2 = 0; k2 < count; k2++) {
                orderItemTotAmt[k] += StringTool.round(parmValue.getDouble(orderTitleItem[k], k2), 2);
            }
        }
        String[] ParmMapString = parmValue.getNames();
        for (int k = 0; k < ParmMapString.length; k++) {
            parmValue.addData(ParmMapString[k], "");
        }
        parmValue.setCount(count + 1);
        parmValue.setData("PACKAGE_CODE", count, "合计");
        for (int k = 0; k < orderTitleItem.length; k++) {
            parmValue.setData(orderTitleItem[k], count, orderItemTotAmt[k]);
        }
        parmValue.setData("OWN_AMT", count, sumOwnAmt);
        parmValue.setData("DISCNT", count, "");
        parmValue.setData("AR_AMT", count, sumArAmt);
        table.setParmValue(parmValue);
    }

    /**
     * 导出
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "健检团体费用统计");
    }

    /**
     * 清空
     */
    public void onClear() {
        table.removeRowAll();
        table.setHeader("员工编号,60;姓名,60;性别,60;*(保)A，B，O,100;*13碳尿素呼气,100;"
                + "*CT冠状动脉造影,100;*CT头部平扫,80;*癌胚抗原,80;*丙肝抗体,80;彩色多普勒,80");
        ((TRadioButton) this.getComponent("ALL")).setSelected(true);
        this.setValue("CONTRACT_CODE", "");
        this.setValue("COMPANY_CODE", "");
        contract.getPopupMenuData().getData().clear();
        contract.filter();
        contractD = new HRMContractD();
        
        // add by wangb 2016/06/24 团体名称需要根据不同登录角色筛选
		String roleType = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
		}
		
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
    }

    /**
     * 团体代码选择事件
     */
    public void onCompanyChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);// 查询相对应合同下拉框的数据
        if (contractParm == null || contractParm.getCount() <= 0 || contractParm.getErrCode() != 0) {
            this.messageBox("没有合同数据");
            return;
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox("E0034");// 取得数据错误
            return;
        }
        contract.setValue(contractCode);
    }

    /**
     * <p>
     * Title: 内部排序比较器
     * </p>
     * 
     * <p>
     * Description: 用于排列标题栏上各个医嘱的前后顺序(按照医院要求的特定顺序)
     * </p>
     * 
     * <p>
     * Copyright: Copyright (c) 2013
     * </p>
     * 
     * <p>
     * Company:BlueCore
     * </p>
     * 
     * @author WangLong 20130110
     * @version 1.0
     */
    class InnerComparator
            implements Comparator {

        // 项目显示顺序（体检科(dept)>检验(dept)=生化(dept)=免疫(dept)>放射(dept)>超声(dept)>核医学(dept)>电生理(dept)
        // >★处置(type)>药费(type)>★其他(type)>材料费(type)）
        // !此顺序为医院要求的
        // !带星号是我自己加的
        String[] attrSeq = {"04", "03", "05", "06", "01", "02" };// 科别属性顺序（总检>内科>外科>眼科>五官科>妇科）add-by-wanglong-20130422
        String[] deptSeq =
                {"020301", "040201", "040202", "040203", "0404", "0405", "0407", "0406" };// 科室顺序（体检科>检验>生化>免疫>放射>超声>核医学>电生理）
        String[] typeSeq = {"LIS", "RIS", "TRT", "PHA", "OTH" };// 种类顺序（检验>检查>处置>药费>其他）

        public int compare(Object o1, Object o2) {
            Map<String, Integer> deptSeqMap = new HashMap<String, Integer>();
            for (int i = 0; i < deptSeq.length; i++) {
                deptSeqMap.put(deptSeq[i], i);
            }
            Map<String, Integer> typeSeqMap = new HashMap<String, Integer>();
            for (int i = 0; i < typeSeq.length; i++) {
                typeSeqMap.put(typeSeq[i], i);
            }
            TParm t1 = (TParm) o1;
            TParm t2 = (TParm) o2;
            if (!t1.getValue("DEPT_ATTRIBUTE").equals("")//add by wanglong 20130422
                    && !t2.getValue("DEPT_ATTRIBUTE").equals("")) {// attr对attr
                Map<String, Integer> attrSeqMap = new HashMap<String, Integer>();
                for (int i = 0; i < attrSeq.length; i++) {
                    attrSeqMap.put(attrSeq[i], i);
                }
                String deptAttr1 = t1.getValue("DEPT_ATTRIBUTE");
                String deptAttr2 = t2.getValue("DEPT_ATTRIBUTE");
                if (attrSeqMap.get(deptAttr1) != null && attrSeqMap.get(deptAttr2) != null) {
                    if (attrSeqMap.get(deptAttr1) > attrSeqMap.get(deptAttr2)) {
                        return 1;
                    } else if (attrSeqMap.get(deptAttr1) < attrSeqMap.get(deptAttr2)) {
                        return -1;
                    } else return 0;
                } else if (attrSeqMap.get(deptAttr1) != null && attrSeqMap.get(deptAttr2) == null) {
                    return -1;
                } else if (attrSeqMap.get(deptAttr1) == null && attrSeqMap.get(deptAttr2) != null) {
                    return 1;
                } else return 0;
            } else if (t1.getValue("DEPT_ATTRIBUTE").equals("")
                    && !t2.getValue("DEPT_ATTRIBUTE").equals("")) {// dept对attr
                return 1;
            } else if (!t1.getValue("DEPT_ATTRIBUTE").equals("")
                    && t2.getValue("DEPT_ATTRIBUTE").equals("")) {// attr对dept
                return -1;
            }
            if (!t1.getValue("EXEC_DEPT_CODE").equals("")
                    && !t2.getValue("EXEC_DEPT_CODE").equals("")) {// dept对dept
                String deptCode1 = t1.getValue("EXEC_DEPT_CODE");
                String deptCode2 = t2.getValue("EXEC_DEPT_CODE");
                if (deptSeqMap.get(deptCode1) != null && deptSeqMap.get(deptCode2) != null) {
                    if (deptSeqMap.get(deptCode1) > deptSeqMap.get(deptCode2)) {
                        return 1;
                    } else if (deptSeqMap.get(deptCode1) < deptSeqMap.get(deptCode2)) {
                        return -1;
                    } else return 0;
                } else if (deptSeqMap.get(deptCode1) != null && deptSeqMap.get(deptCode2) == null) {
                    return -1;
                } else if (deptSeqMap.get(deptCode1) == null && deptSeqMap.get(deptCode2) != null) {
                    return 1;
                } else return 0;
            } else if (t1.getValue("EXEC_DEPT_CODE").equals("")
                    && !t2.getValue("EXEC_DEPT_CODE").equals("")) {// cat1对dept
                return 1;
            } else if (!t1.getValue("EXEC_DEPT_CODE").equals("")
                    && t2.getValue("EXEC_DEPT_CODE").equals("")) {// dept对cat1
                return -1;
            } else if (t1.getValue("EXEC_DEPT_CODE").equals("")
                    && t2.getValue("EXEC_DEPT_CODE").equals("")) {// cat1对cat1
                String cat1Type1 = t1.getValue("CAT1_TYPE");
                String cat1Type2 = t2.getValue("CAT1_TYPE");
                if (typeSeqMap.get(cat1Type1) != null && typeSeqMap.get(cat1Type2) != null) {
                    if (typeSeqMap.get(cat1Type1) > typeSeqMap.get(cat1Type2)) {
                        return 1;
                    } else if (typeSeqMap.get(cat1Type1) < typeSeqMap.get(cat1Type2)) {
                        return -1;
                    } else return 0;
                } else if (typeSeqMap.get(cat1Type1) != null && typeSeqMap.get(cat1Type2) == null) {
                    return -1;
                } else if (typeSeqMap.get(cat1Type1) == null && typeSeqMap.get(cat1Type2) != null) {
                    return 1;
                } else return 0;
            }
            return 0;
        }
    }

    /**
     * 根据标题字数,生成其长度的字符串 add by wanglong 20130110
     * 
     * @param titleItem
     * @return
     */
    public String countTitleItemLength(String titleItem) {
        try {
            titleItem = new String(titleItem.getBytes("GBK"), "ISO8859_1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = (titleItem.length() * 8) < 50 ? 50 : (titleItem.length() * 8);//宽度不小于50
        return "," + length + ",double,#########0.00;";
    }
    
    // ====================排序功能begin======================// add by wanglong 20130312
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
                // 调用排序方法;
                // 转换出用户想排序的列和底层数据的列，然后判断
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // 表格中parm值一致,
                // 1.取paramw值;
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount() - 1);// add by wanglong 20130108
                // System.out.println("tableData:"+tableData);
                tableData.removeGroupData("SYSTEM");
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
                String tblColumnName = table.getParmMap(sortColumn);
                // 转成parm中的列
                int col = tranParmColIndex(columnName, tblColumnName);
                // System.out.println("==col=="+col);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                TParm lastResultParm = new TParm();// 记录最终结果
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * 列名转列索引值
     * 
     * @param columnName
     * @param tblColumnName
     * @return
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
     * 得到 Vector 值
     * 
     * @param group
     *            String 组名
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int 最大行数
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
     * vectory转成param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
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
        return parmTable;
    }
    // ====================排序功能end======================
}
