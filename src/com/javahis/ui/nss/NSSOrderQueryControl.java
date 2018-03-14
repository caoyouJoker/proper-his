package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

import jdo.nss.NSSSQL;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>Title: 订餐查询</p>
 *
 * <p>Description: 订餐查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.18
 * @version 1.0
 */
public class NSSOrderQueryControl
    extends TControl {

    private TTable table;

    private String case_no = "";

    public NSSOrderQueryControl() {
    }

    /*
     * 初始化
     */
    public void onInit() {
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        table = (TTable)this.getComponent("TABLE");
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("DIET_DATE", datetime.substring(0, 10).replace("-", "/"));
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String sql =
            " SELECT 'N' AS SELECT_FLG, TO_DATE(B.DIET_DATE,'YYYY/MM/DD') AS DIET_DATE, "
            + " C.STATION_DESC, D.BED_NO_DESC, E.PAT_NAME, F.MEAL_CHN_DESC, "
            + " B.DIET_KIND, B.PACK_CHN_DESC , G.CALORIES, "
            + " CASE WHEN B.ADD_ON='Y' THEN '加餐' || '  ' ||B.DESCRIPTION ELSE "
            + " B.DESCRIPTION END AS DESCRIPTION, B.PACK_CODE, B.MEAL_CODE "
            + " FROM ADM_INP A,  NSS_ORDER B, SYS_STATION C, SYS_BED D, "
            + " SYS_PATINFO E, NSS_MEAL F, NSS_PACKM G "
            + " WHERE A.CASE_NO = B.CASE_NO "
            + " AND A.STATION_CODE = C.STATION_CODE "
            + " AND A.BED_NO = D.BED_NO AND A.MR_NO = E.MR_NO "
            + " AND B.MEAL_CODE = F.MEAL_CODE AND B.PACK_CODE = G.PACK_CODE"
            + " AND B.BILL_FLG = 'Y' ";
        // 病区
        String station_code = this.getValueString("STATION_CODE");
        if (station_code != null && station_code.length() > 0) {
            sql += " AND A.STATION_CODE = '" + station_code + "' ";
        }
        // 就诊序号
        if (case_no != null && case_no.length() > 0) {
            sql += " AND A.CASE_NO = '" + case_no + "' ";
        }
        // 用餐日期
        String diet_date = this.getValueString("DIET_DATE");
        diet_date = diet_date.substring(0, 4) + diet_date.substring(5, 7) +
            diet_date.substring(8, 10);
        if (diet_date != null && diet_date.length() > 0) {
            sql += " AND B.DIET_DATE = '" + diet_date + "' ";
        }
        // 餐次
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code != null && meal_code.length() > 0) {
            sql += " AND B.MEAL_CODE = '" + meal_code + "' ";
        }
        sql = sql + " ORDER BY B.CASE_NO, B.DIET_DATE, B.MEAL_CODE ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("没有查询数据");
            table.removeRowAll();
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("DIET_DATE", datetime.substring(0, 10).replace("-", "/"));
        this.clearValue("STATION_CODE;MR_NO;IPD_NO;PAT_NAME;MEAL_CODE");
        table.removeRowAll();
        case_no = "";
    }

    /**
     * 打印出餐名单
     */
    public void onPrint() {
        TParm parmTable = table.getParmValue();
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "出餐单");
        // 表格数据
        TParm parm = new TParm();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("N".equals(parmTable.getValue("SELECT_FLG", i))) {
                continue;
            }
            parm.addData("DIET_DATE",
                         parmTable.getValue("DIET_DATE", i).substring(0, 10).
                         replace("-", "/"));
            parm.addData("STATION_DESC", parmTable.getValue("STATION_DESC", i));
            parm.addData("BED_NO_DESC", parmTable.getValue("BED_NO_DESC", i));
            parm.addData("PAT_NAME", parmTable.getValue("PAT_NAME", i));
            parm.addData("MEAL_CHN_DESC", parmTable.getValue("MEAL_CHN_DESC", i));
            parm.addData("DIET_KIND",
                         "1".equals(parmTable.getValue("DIET_KIND", i)) ? "普食" :
                         "治疗饮食");
            parm.addData("PACK_CHN_DESC", parmTable.getValue("PACK_CHN_DESC", i));
            parm.addData("CALORIES", parmTable.getDouble("CALORIES", i));
            parm.addData("DESCRIPTION", parmTable.getValue("DESCRIPTION", i));
        }
        if (parm.getCount("DIET_DATE") <= 0) {
            this.messageBox("没有打印数据");
            return;
        }

        parm.setCount(parm.getCount("DIET_DATE"));
        parm.addData("SYSTEM", "COLUMNS", "DIET_DATE");
        parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm.addData("SYSTEM", "COLUMNS", "MEAL_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DIET_KIND");
        parm.addData("SYSTEM", "COLUMNS", "PACK_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "CALORIES");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");

        date.setData("TABLE", parm.getData());
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
        date.setData("DATE", "TEXT",
                     "制表日期: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSOrderOut.jhw", date);
    }

    /**
     * 打印饮食卡
     */
    public void onCard() {
        TParm parm = table.getParmValue();
        // 打印数据
        TParm printData = new TParm();
        TParm date = new TParm();
        int count = 0;
        //NSSSQL.getNSSPackDD()
        TParm packDD = new TParm();

        for (int i = 0; i < table.getRowCount(); i++) {
            if ("N".equals(parm.getValue("SELECT_FLG", i))) {
                continue;
            }
            packDD = new TParm(TJDODBTool.getInstance().select(NSSSQL.
                getNSSPackDD(parm.getValue("PACK_CODE", i),
                             parm.getValue("MEAL_CODE", i))));
            String meau_list = "";
            for (int j = 0; j < packDD.getCount(); j++) {
                meau_list += "    " + packDD.getValue("MENU_CHN_DESC", j) + "\n\r";
            }
            if (count % 2 == 0) {
                date.addData("TITLE_1", "饮食卡");
                date.addData("DIET_KIND_1",
                             "1".equals(parm.getValue("DIET_KIND", i)) ? "普食" :
                             "治疗饮食");
                date.addData("DIET_DATE_1",
                             parm.getValue("DIET_DATE", i).substring(0, 10).
                             replace("-",
                                     "/"));
                date.addData("MEAL_CHN_DESC_1",
                             parm.getValue("MEAL_CHN_DESC", i));
                date.addData("STATION_DESC_1",
                             parm.getValue("STATION_DESC", i) + "  " +
                             parm.getValue("BED_NO_DESC", i));
                date.addData("PAT_NAME_1", parm.getValue("PAT_NAME", i));
                date.addData("PACK_CHN_DESC_1",
                             parm.getValue("PACK_CHN_DESC", i));
                date.addData("CALORIES_1", parm.getDouble("CALORIES", i) + "卡");
                date.addData("PACK_1", "菜品:\r\n"+meau_list);

                date.addData("DESCRIPTION_1",
                             "备注: " + parm.getValue("DESCRIPTION", i));
            }else{
                date.addData("TITLE_2", "饮食卡");
                date.addData("DIET_KIND_2",
                             "1".equals(parm.getValue("DIET_KIND", i)) ? "普食" :
                             "治疗饮食");
                date.addData("DIET_DATE_2",
                             parm.getValue("DIET_DATE", i).substring(0, 10).
                             replace("-",
                                     "/"));
                date.addData("MEAL_CHN_DESC_2",
                             parm.getValue("MEAL_CHN_DESC", i));
                date.addData("STATION_DESC_2",
                             parm.getValue("STATION_DESC", i) + "  " +
                             parm.getValue("BED_NO_DESC", i));
                date.addData("PAT_NAME_2", parm.getValue("PAT_NAME", i));
                date.addData("PACK_CHN_DESC_2",
                             parm.getValue("PACK_CHN_DESC", i));
                date.addData("CALORIES_2", parm.getDouble("CALORIES", i) + "卡");
                date.addData("PACK_2", "菜品:\r\n" + meau_list);
                date.addData("DESCRIPTION_2",
                             "备注: " + parm.getValue("DESCRIPTION", i));
            }
            count++;
        }

        if (count % 2 != 0) {
            date.addData("TITLE_2", "");
            date.addData("DIET_KIND_2", "");
            date.addData("DIET_DATE_2", "");
            date.addData("MEAL_CHN_DESC_2", "");
            date.addData("STATION_DESC_2", "");
            date.addData("PAT_NAME_2", "");
            date.addData("PACK_CHN_DESC_2", "");
            date.addData("CALORIES_2", "");
            date.addData("PACK_2", "");
            date.addData("DESCRIPTION_2", "");
        }

        if (date.getCount("TITLE_1") <= 0) {
            this.messageBox("请选择打印数据");
            return;
        }

        date.setCount(date.getCount("TITLE_1"));

        date.addData("SYSTEM", "COLUMNS", "TITLE_1");
        date.addData("SYSTEM", "COLUMNS", "DIET_KIND_1");
        date.addData("SYSTEM", "COLUMNS", "DIET_DATE_1");
        date.addData("SYSTEM", "COLUMNS", "MEAL_CHN_DESC_1");
        date.addData("SYSTEM", "COLUMNS", "STATION_DESC_1");
        date.addData("SYSTEM", "COLUMNS", "PAT_NAME_1");
        date.addData("SYSTEM", "COLUMNS", "PACK_CHN_DESC_1");
        date.addData("SYSTEM", "COLUMNS", "CALORIES_1");
        date.addData("SYSTEM", "COLUMNS", "PACK_1");
        date.addData("SYSTEM", "COLUMNS", "DESCRIPTION_1");

        date.addData("SYSTEM", "COLUMNS", "TITLE_2");
        date.addData("SYSTEM", "COLUMNS", "DIET_KIND_2");
        date.addData("SYSTEM", "COLUMNS", "DIET_DATE_2");
        date.addData("SYSTEM", "COLUMNS", "MEAL_CHN_DESC_2");
        date.addData("SYSTEM", "COLUMNS", "STATION_DESC_2");
        date.addData("SYSTEM", "COLUMNS", "PAT_NAME_2");
        date.addData("SYSTEM", "COLUMNS", "PACK_CHN_DESC_2");
        date.addData("SYSTEM", "COLUMNS", "CALORIES_2");
        date.addData("SYSTEM", "COLUMNS", "PACK_2");
        date.addData("SYSTEM", "COLUMNS", "DESCRIPTION_2");

        printData.setData("TABLE", date.getData());

        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSOrderCard.jhw", printData);
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
    }

    /**
     * 全选事件
     */
    public void onSelectAll() {
        String select_flg = this.getValueString("SELECT_ALL");
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", select_flg);
        }
        table.acceptText();
    }

    /**
     * MR_NO回车事件
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
      //modify by huangtt 20160930 EMPI患者查重提示  start
        mr_no = PatTool.getInstance().checkMrno(mr_no);
        Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
	            this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
	            mr_no = pat.getMrNo();
	    }
		//modify by huangtt 20160930 EMPI患者查重提示  end
        
        this.setValue("MR_NO", mr_no); //====cehnxi
        
        
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("该病患当前不在院");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * IPD_NO回车事件
     */
    public void onIpdNoAction() {
        String mr_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(mr_no, PatTool.getInstance().getIpdNoLength())); //====chenxi
        TParm parm = new TParm();
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("该病患当前不在院");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * 输出病患信息
     * @param parm TParm
     */
    private void setPatInfo(TParm parm) {
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        this.setValue("IPD_NO", parm.getValue("IPD_NO"));
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        this.setValue("PAT_NAME", pat.getName());
        case_no = parm.getValue("CASE_NO");
    }

}
