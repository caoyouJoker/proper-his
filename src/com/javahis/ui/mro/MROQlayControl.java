package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;  
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.mro.MROQlayControlMTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSStation;

/**
 * <p> Title:自动质控 </p>
 * 
 * <p> Description:自动质控  </p>
 * 
 * <p> Copyright: Copyright (c) 2009 </p>
 * 
 * <p> Company: </p>
 * 
 * @author pangben 20110730
 * @version 1.0
 */
public class MROQlayControl extends TControl {

    private TParm result;// 表格数据
    private TParm parmTEMP;// 记录查询参数
    private TTable tableM;// 表格
    // =================排序辅助==============add by wanglong 20121127
    private BILComparator compare = new BILComparator();
    private int sortColumn = -1;
    private boolean ascending = false;
    
    /**
     * 初始化方法
     */
    public void onInit() {
        tableM = (TTable) this.getComponent("TABLE_M");//add by wanglong 20121127
        //callFunction("UI|TABLE_M|addEventListener",  "TABLE_M->" + TTableEvent.CLICKED, this, "onTableClicked");//delete by wanglong 20131025
        tableM.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBox");
        addSortListener(tableM);//加排序 add by wanglong 20121127
        this.setValue("REGION_CODE", Operator.getRegion());
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/'));// 初始化查询区间
        this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10)
                .replace('-', '/'));
        parmTEMP = new TParm();
        onClear();
    }
    
    /**
     * 表格单击事件
     * @param row int
     */
    public void onTableClicked(int row) {
        tableM.acceptText();
        TParm data = tableM.getParmValue();
        setValueForParm("DEPT_CODE;STATION_CODE;VS_DR_CODE;MR_NO;IPD_NO;PAT_NAME;AGE", data, row);
        if (tableM.getSelectedColumn() == 0) {
            String flg = tableM.getItemString(row, 0);
            if (flg.equals("Y")) {
                data.setData("FLG", row, "N");
            } else {
                data.setData("FLG", row, "Y");
            }
            tableM.setParmValue(data);
        }
       
    }
    
    /**
     * 右键菜单弹出事件
     * @param tableName
     */
    public void showPopMenu() {
        tableM.setPopupMenuSyntax("手动质控,openRigthPopMenu");
    }
    

    /**
     * 右键菜单点击处理
     */
    public void openRigthPopMenu() {
//        if ((Boolean) this.callFunction("UI|DO|isSelected")) {
//            this.messageBox("已完成的不能进行手动质控");
//            return;
//        }
        TParm parm = new TParm();
        if (((TRadioButton) this.getComponent("TYPE_IN")).isSelected()) {
            parm.setData("TYPE", "TYPE_IN");// 在院
        } else
            parm.setData("TYPE", "TYPE_OUT");// 出院
        TParm showParm=tableM.getParmValue();
        int rowIndex = tableM.getSelectedRow();
        parm.setData("CASE_NO", showParm.getValue("CASE_NO", rowIndex));
        parm.setData("MR_NO", showParm.getValue("MR_NO", rowIndex));
        parm.setData("PAT_NAME", showParm.getValue("PAT_NAME", rowIndex));
        parm.setData("IPD_NO", showParm.getValue("IPD_NO", rowIndex));
        parm.setData("ADM_DATE", showParm.getTimestamp("ADM_DATE", rowIndex));
        parm.setData("DEPT_CODE", showParm.getValue("DEPT_CODE", rowIndex));
        parm.setData("STATION_CODE", showParm.getValue("STATION_CODE", rowIndex));
        parm.setData("TYPERESULT", showParm.getValue("TYPERESULT", rowIndex)); // 是否完成
        parm.setData("OPEN_USER", Operator.getName());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("VS_DR_CODE", showParm.getValue("VS_DR_CODE", rowIndex));// add by wanglong 20121105
        parm.addListener("onReturnContent", this, "onReturnContent");// add by wanglong 20130819
        this.openWindow("%ROOT%\\config\\mro\\MROQlayDataControlUI.x", parm);// 进入个人细项分值与手动质控
        onQuery();
    }
    
    /**
     * 传回
     * @param value
     */
    public void onReturnContent(String value) {//add by wanglong 20130819
        if (StringUtil.isNullString(value)) {
            return;
        }
        String caseNo = value.split(";")[0];
        double score = StringTool.round(StringTool.getDouble(value.split(";")[1]), 2);
        String typeResult="";
        int row = -1;
        TParm parmValue = tableM.getParmValue();
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (parmValue.getValue("CASE_NO", i).equals(caseNo)) {
                parmValue.setData("SUMSCODE", i, score);
                typeResult = parmValue.getValue("TYPERESULT", i);
                row = i;
                break;
            }
        }
        String TYPERESULT = value.split(";")[2];
        if (!typeResult.equals(TYPERESULT)) {
            parmValue.removeRow(row);
        }
        tableM.setParmValue(parmValue);
    }
    

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("请选择区域！");
            return;
        }
        parmTEMP = new TParm();
        parmTEMP.setData("REGION_CODE", getValueString("REGION_CODE"));
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parmTEMP.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parmTEMP.setData("STATION_CODE", getValueString("STATION_CODE"));
        }
        if (!"".equals(this.getValueString("VS_DR_CODE"))) {
            parmTEMP.setData("VS_DR_CODE", getValueString("VS_DR_CODE"));//modify by wanglong 20121127
        }
//      if (!"".equals(this.getValueString("STATUS"))) {
//          parmTEMP.setData("STATUS", getValueString("STATUS"));//delete by wanglong 20121127
//      }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parmTEMP.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parmTEMP.setData("IPD_NO", getValueString("IPD_NO"));
        }
        if (((TRadioButton) this.getComponent("TYPE_IN")).isSelected()) {
            parmTEMP.setData("TYPE", "0");// 在院
            parmTEMP.setData("TYPE_IN", "TYPE_IN");
        } else {
            parmTEMP.setData("TYPE", "1");// 出院
            parmTEMP.setData("TYPE_OUT", "TYPE_OUT");
            Timestamp startDate = (Timestamp) this.getValue("START_DATE");//modify by wanglong 20121127
            Timestamp endDate = (Timestamp) this.getValue("END_DATE");
            endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
            parmTEMP.setData("START_DATE", startDate);//modify by wanglong 20121127
            parmTEMP.setData("END_DATE", endDate);//modify by wanglong 20121127
        }
        if (((TRadioButton) this.getComponent("UNDO")).isSelected()) {//add by wanglong 20121128
            parmTEMP.setData("TYPERESULT", "0");// 未完成 
        }else{
            parmTEMP.setData("TYPERESULT", "1");// 已完成
        }
        result = MROQlayControlMTool.getInstance().onQueryQlayControlScore(
                parmTEMP);
    
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("没有查询结果");
            tableM.removeRowAll();
        } else {
            tableM.setParmValue(result);
        }
    }


    /**
     * 启动质控
     */
    public void onQlayControlAction() {
        if ((Boolean) this.callFunction("UI|DO|isSelected")) {
            this.messageBox("已完成的不能进行自动质控");
            return;
        }
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        // 执行数据新增
        TParm parmValue = tableM.getParmValue();
        if (parmValue.getCount() > 0) {
            String case_list = "";
            int count = 0;//add by wanglong 20130819
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (parmValue.getBoolean("FLG", i)) {// add by wanglong 20130819
    
                    case_list += "'" + parmValue.getValue("CASE_NO", i) + "',";
                    count++;
                }
            }
            if (count >= 1000) {
                this.messageBox("请不要操作超过1000人");
            }
            if (case_list.length() == 0) {
                return;
            }
            parm.setData("CASE_NO", case_list.substring(0, case_list.length() - 1));
            TParm result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            int j = 0;
            for (; j < result.getCount(); j++) {
                if ("1".equals(result.getValue("TYPERESULT", j))) {
                    break;
                }
            }
            if (j < result.getCount()) {
                if(j ==0){
                    this.messageBox("已提交，不能进行自动质控"); 
                }else{
                    this.messageBox("部分用户已提交，不能进行自动质控，请重新查询"); 
                }
               
                return;
            }
        } else {
            this.messageBox("请先执行查询操作");// add by wanglong 20121108
            return;
        }
        parm.setData("parmTEMP", parmTEMP.getData());
        TParm result = TIOM_AppServer.executeAction(
                "action.mro.MROQlayControlAction", "onQlayControlMethod", parm);
        // 保存判断
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("保存失败" + " , " + result.getErrText());
            return;
        }
      
        // 获得已经提交的病案号
        String personName = result.getValue("personName");
        if (null != personName && personName.length() > 0) {
            personName = personName.substring(0, personName.lastIndexOf(","));
            personName += "\n病患已经完成不可以修改";
            this.messageBox(personName);// 显示已经完成提交的病案号
        }
        this.messageBox("P0001");// 保存成功
        this.clearValue("ALL_FLG");
        onQuery();
    }
    
    /**
     * 提交完成
     * @return
     */
    public void onSubmit() {
        tableM.acceptText();
        TParm tableParm = tableM.getShowParmValue();
        TParm parm=new TParm();
        for (int i = 0; i < tableParm.getCount(); i++) {
            if(tableParm.getValue("FLG", i).equals("Y")){
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                parm.addData("IPD_NO", tableParm.getValue("IPD_NO", i));
                parm.addData("SUMSCODE", tableParm.getValue("SUMSCODE", i));
                parm.addData("TYPERESULT", "1");
                // =============add by wanglong 20131025
                parm.addData("MRO_CHAT_FLG", "2");
                TParm result =
                        TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                     "updateMRO_CHAT_FLG",
                                                     parm.getRow(parm.getCount("MR_NO") - 1));
                if (result.getErrCode() < 0) {
                    this.messageBox("病患：" + tableParm.getValue("PAT_NAME", i) + " 提交失败");
                    parm.removeRow(parm.getCount("MR_NO") - 1);
                }
                // =============add end
            }
        }
        parm.setCount(parm.getCount("MR_NO"));
        TParm result = TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "saveScoreAndState", parm);
        // 保存判断
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("提交失败" + " , " + result.getErrText());
            return;
        }
        messageBox("P0005");// 执行成功
        this.clearValue("ALL_FLG");
        onQuery();
    }
    
    /**
     * 出院在院单选按钮切换方法
     */
    public void onChangeType() {
        if (((TRadioButton) this.getComponent("TYPE_IN")).isSelected()) {
            this.callFunction("UI|START_DATE|setEnabled", false);
            this.callFunction("UI|END_DATE|setEnabled", false);
        } else {
            this.callFunction("UI|START_DATE|setEnabled", true);
            this.callFunction("UI|END_DATE|setEnabled", true);
        }
    }

    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        String mr_no = this.getValueString("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mr_no);
        if (pat == null) {
            this.messageBox("没有查询数据");
            this.setValue("MR_NO", "");
            this.setValue("IPD_NO", "");
            this.setValue("PAT_NAME", "");
            this.setValue("AGE", "");
            return;
        }

        // modify by huangtt 20160929 EMPI患者查重提示 start
        mr_no=PatTool.getInstance().checkMrno(getValueString("mr_no")); 
        this.setValue("MR_NO",mr_no);
        if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			
		}	
        // modify by huangtt 20160929 EMPI患者查重提示 end 
        
        
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("IPD_NO", pat.getIpdNo());
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("AGE", StringUtil.showAge(pat.getBirthday(), date));
    }

    /**
     * 住院号回车事件
     */
    public void onIpdNo() {
        String ipd_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(ipd_no, PatTool.getInstance().getIpdNoLength()));
    }

    /**
     * 科室变更事件
     */
    public void onChangeDept() {
        String dept_code = this.getValueString("DEPT_CODE");
        //System.out.println("==============dept_code================"+dept_code);
        // 过滤医生
        TextFormatSYSOperator user_id = (TextFormatSYSOperator) this.getComponent("VS_DR_CODE");
        user_id.setDept(dept_code);
        user_id.onQuery();
    }

    /**
     * 区域变更事件
     */
    public void onChangeRegion() {
        String region_code = this.getValueString("REGION_CODE");
        // 过滤科室
        TextFormatDept dept_code = (TextFormatDept) this.getComponent("DEPT_CODE");
        dept_code.setRegionCode(region_code);
        dept_code.onQuery();
        // 过滤病区
        TextFormatSYSStation station_code = (TextFormatSYSStation) this.getComponent("STATION_CODE");
        station_code.setRegionCode(region_code);
        station_code.onQuery();
        // 过滤医生
        TextFormatSYSOperator user_id = (TextFormatSYSOperator) this.getComponent("VS_DR_CODE");
        user_id.setRegionCode(region_code);
        user_id.onQuery();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.setValue("REGION_CODE", "");
        this.setValue("REGION_CODE", Operator.getRegion());
        this.setValue("DEPT_CODE", "");
        this.setValue("STATION_CODE", "");
        this.setValue("VS_DR_CODE", "");
        //this.setValue("STATUS", "");
        this.setValue("MR_NO", "");
        this.setValue("IPD_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("AGE", "");
        tableM.removeRowAll();
        onChangeRegion();
        onChangeDept();
        this.callFunction("UI|TYPE_IN|setSelected", true);
        this.callFunction("UI|START_DATE|setEnabled", false);
        this.callFunction("UI|END_DATE|setEnabled", false);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
                .substring(0, 10).replace('-', '/'));
    }

    /**
     * 发布公布栏
     */
    public void onBoardMessage() {
        TParm parmValue = tableM.getParmValue();
        TParm caseParm = new TParm();
        for (int i = 0; i < parmValue.getCount(); i++) {// add by wanglong 20130819
            if (parmValue.getBoolean("FLG", i)) {
                caseParm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            }
        }
        if (caseParm.getCount("CASE_NO") < 1) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CASE_NO", caseParm.getData());//add by wanglong 20130819
        // 执行数据新增
        TParm result = TIOM_AppServer.executeAction(
                "action.mro.MROQlayControlAction", "onBoardMessage", parm);
        // 保存判断
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("发送失败" + " , " + result.getErrText());
            return;
        }
        this.messageBox("发送成功");
    }

    /**
     * 发送邮件
     */
    public void onSendMessage() {
        TParm parmValue = tableM.getParmValue();
        TParm caseParm = new TParm();
        for (int i = 0; i < parmValue.getCount(); i++) {// add by wanglong 20130819
            if (parmValue.getBoolean("FLG", i)) {
                caseParm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            }
        }
        if (caseParm.getCount("CASE_NO") < 1) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CASE_NO", caseParm.getData());//add by wanglong 20130819
        // 执行数据新增
        TParm result = TIOM_AppServer.executeAction(
                "action.mro.MROQlayControlAction", "onSendMessage", parm);
        // 保存判断
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("发送失败" + " , " + result.getErrText());
            return;
        }
        this.messageBox("发送成功");
    }
    
    /**
     * CheckBox点选事件
     * @param obj
     * @return
     */
    public boolean onTableCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return true;
    }
    
    /**
     * 全选事件
     */
    public void onAllChoice() {//add by wanglong 20121128
        String select = getValueString("ALL_FLG");
        TParm parm = tableM.getParmValue();
        if (parm.getCount() > 0) {
            for (int i = 0; i < parm.getCount("FLG"); i++) {
                parm.setData("FLG", i, select);
            }
            tableM.setParmValue(parm);
        }
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {//add by wanglong 20130819
        if (tableM.getRowCount()<1) {
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(tableM, "自动质控名单");
    }
    

    // ====================排序功能begin======================add by wanglong 20121127
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
        parmTable=result;
    }
    // ====================排序功能end======================
}
