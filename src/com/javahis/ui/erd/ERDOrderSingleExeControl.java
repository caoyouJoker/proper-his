package com.javahis.ui.erd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import jdo.erd.ErdOrderExecTool;
import jdo.hl7.Hl7Communications;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiMultiPanel;
import com.tiis.ui.TiPanel;

/**
 * <p> Title: 行动护理执行 </p>
 * 
 * <p> Description: 行动护理执行 </p>
 * 
 * <p> Copyright: Copyright (c) 2015 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 2015.04.07
 * @version 1.0
 */
public class ERDOrderSingleExeControl extends TControl {

    private TParm patInfo = new TParm(); // 病患基本信息
    TTable table;
    TPanel PANEL_PHAPIC;// 显示药品照片Panel
    TiPanel tiPanel2 = new TiPanel();
    TiPanel tiPanel3 = new TiPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    GridLayout gridLayout1 = new GridLayout();

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        Timestamp yesterday = StringTool.rollDate(now, -1);
        long timeStart = yesterday.getTime() - 60 * 60 * 1000;
        long timeEnd = now.getTime() + 60 * 60 * 1000;
        this.setValue("START_DATE", new Timestamp(timeStart));
        this.setValue("END_DATE", new Timestamp(timeEnd));
        this.setValue("REGION_CODE", Operator.getRegion());
        this.setValue("DEPT_CODE", Operator.getDept());
        this.setValue("CLINICAREA_CODE", Operator.getStation());
        callFunction("UI|CLINICAREA_CODE|onQuery");
        table = (TTable) this.getComponent("tableM");
        callFunction("UI|tableM|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        table.addEventListener(table.getTag() + "->" + TTableEvent.CHANGE_VALUE, this,
                               "onChangeDateTime");
        callFunction("UI|BAR_CODE|addEventListener", TTextFieldEvent.KEY_PRESSED, this, "onBarCode");
        callFunction("UI|MR_NO|grabFocus");
        callFunction("UI|BAR_CODE|grabFocus");
        panelInit();
    }

    /**
     * 初始化显示药品照片Panel
     */
    public void panelInit() {
        tiPanel3.setBorder(null);
        jScrollPane1.setViewportView(tiPanel3);
        jScrollPane1.setBorder(null);
        jScrollPane1.setBounds(new Rectangle(2, 2, 1320, 200));
        tiPanel2.setBounds(new Rectangle(2, 2, 1330, 204));
        tiPanel2.setBorder(null);
        tiPanel2.setLayout(null);
        tiPanel2.add(jScrollPane1, null);
        PANEL_PHAPIC = ((TPanel) getComponent("tPanel_1"));
        PANEL_PHAPIC.add(tiPanel2, null);
    }

    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        
        //modify by huangtt 20160927 EMPI患者查重提示  start
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
	          this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
	          mrNo = pat.getMrNo();
	    }
		//modify by huangtt 20160927 EMPI患者查重提示  end
		
        String clinicAreaCode = this.getValueString("CLINICAREA_CODE");
        String sql =
                "SELECT A.MR_NO, A.CASE_NO, C.BED_DESC, B.PAT_NAME, "
                        + "       (SELECT D.CHN_DESC FROM SYS_DICTIONARY D WHERE B.SEX_CODE = D.ID AND D.GROUP_ID = 'SYS_SEX') SEX_DESC "
                        + "  FROM REG_PATADM A, SYS_PATINFO B, ERD_BED C "
                        + " WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO "
                        + "   AND A.MR_NO = '#' AND A.CLINICAREA_CODE = '#' "
                        + "   AND A.ADM_TYPE = 'E' ORDER BY A.CASE_NO DESC";
        sql = sql.replaceFirst("#", mrNo);
        sql = sql.replaceFirst("#", clinicAreaCode);
        patInfo = new TParm(TJDODBTool.getInstance().select(sql));
        if (patInfo.getCount() < 1) {
            this.messageBox("无就诊信息");
            this.onClear();
            return;
        }
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", patInfo.getValue("PAT_NAME", 0));
        this.setValue("SEX_DESC", patInfo.getValue("SEX_DESC", 0));
        this.setValue("BED_DESC", patInfo.getValue("BED_DESC", 0));
        viewPhoto(mrNo);// 显示照片
        callFunction("UI|BAR_CODE|grabFocus");
    }

    /**
     * 查询
     */
    public void onQuery() {
        if (this.getValueString("MR_NO").equals("")) {
            this.messageBox("请先录入病案号");
            return;
        }
        onMrNo();
        if (TypeTool.getBoolean(this.getValueString("YEXE"))) {
            this.setValue("EXE_ALL", "Y");
        } else if (TypeTool.getBoolean(this.getValueString("NEXE"))) {
            this.setValue("EXE_ALL", "N");
        }
        table.removeRowAll();
        exeQuery();
        callFunction("UI|BAR_CODE|grabFocus");
        this.clearValue("BAR_CODE");
    }

    /**
     * “医嘱”回车事件
     */
    public void onBarCode() {
        if (this.getValueString("MR_NO").equals("")) {
            this.messageBox("请先录入病案号");
            return;
        }
        if (this.getValueString("BAR_CODE").equals("")) {
            return;
        }
        exeQuery();
        callFunction("UI|BAR_CODE|grabFocus");
        this.setValue("BAR_CODE", "");
    }

    /**
     * 执行查询
     */
    public void exeQuery() {
        String startDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");
        String cat1Type = "";
        String doseType = "";
        if (this.getValueString("R2").equals("Y")) {
            cat1Type = "PHA";
        }
        if (this.getValueString("R3").equals("Y")) {
            cat1Type = "LIS','RIS";
        }
        if (this.getValueString("R4").equals("Y")) {
            cat1Type = "TRT','PLN','OTH";
            if (!this.getValueString("BAR_CODE").equals("")) {
                this.messageBox("嘱托无条码！");
                return;
            }
        }
        String isExe = "";
        if (this.getValueString("YEXE").equals("Y")) {
            isExe = "Y";
        } else if (this.getValueString("NEXE").equals("Y")) {
            isExe = "N";
        }
        String barCode = this.getValueString("BAR_CODE");
        String sql =
                ErdOrderExecTool.getInstance().queryPatOrder(patInfo.getValue("CASE_NO", 0),
                                                             barCode, startDate, endDate, cat1Type,
                                                             isExe, doseType);
        TParm parmTable = new TParm(TJDODBTool.getInstance().select(sql));
        if (parmTable.getErrCode() < 0) {
            this.messageBox("E0005");// 执行失败
            return;
        }
        if (parmTable.getCount() <= 0) {
            this.messageBox("E0116");// 没有数据
            return;
        }
        for (int i = 0; i < parmTable.getCount(); i++) {
            parmTable.setData("FLG", i, this.getValueString("EXE_ALL"));
        }
        TParm tableParm = table.getParmValue();
        boolean flg = true;
        int row = 0;
        if (tableParm != null) {
            for (int i = 0; i < tableParm.getCount("BAR_CODE"); i++) {
                if (tableParm.getValue("BAR_CODE", i).equals(parmTable.getValue("BAR_CODE", 0))) {
                    this.messageBox("已扫描此条码！");
                    row = i;
                    flg = false;
                    break;
                } else {
                    row = tableParm.getCount("BAR_CODE");
                }
            }
            if (flg) {
                tableParm.addParm(parmTable);
                table.setParmValue(tableParm);
                table.getTable().grabFocus();
            } else {
                table.getTable().grabFocus();
                return;
            }
        } else {
            table.setParmValue(parmTable);
            table.getTable().grabFocus();
            table.setSelectedRow(row);
        }
        int count = 0;
        TParm afParm = table.getParmValue();
        TParm picParm = new TParm();
        for (int i = 0; i < afParm.getCount("ORDER_DESC"); i++) {
            if (afParm.getValue("CAT1_TYPE", i).equals("PHA")) {
                picParm.setData("ORDER_CODE", count, afParm.getValue("ORDER_CODE", i));
                picParm.setData("ORDER_DESC", count, afParm.getValue("ORDER_DESC", i));
                count++;
            }
        }
        tiPanel3.setLayout(null);
        tiPanel3.removeAll();
        this.panelInit();
        gridLayout1 = new GridLayout(0, 5, 10, 10);
        tiPanel3.setLayout(gridLayout1);
        picParm.setCount(count);
        PHA_PIC phaPic[] = new PHA_PIC[count];
        for (int j = 0; j < count; j++) {
            phaPic[j] =
                    new PHA_PIC(picParm.getValue("ORDER_CODE", j), picParm
                            .getValue("ORDER_DESC", j));
            phaPic[j].setPreferredSize(new Dimension(200, 200));
            tiPanel3.add(phaPic[j], null);
        }
        jScrollPane1.setViewportView(tiPanel3);
    }

    /**
     * 保存
     */
    public void onSave() {
        boolean flg = (Boolean) this.callFunction("UI|ALL|isSelected");
        if (flg) {
            this.messageBox("不能在全部状态下保存");
            return;
        }
        table.acceptText();
        int row = table.getRowCount();
        TParm parmValue = table.getParmValue();
        Timestamp now = SystemTool.getInstance().getDate();
        TParm parm = new TParm();
        for (int i = 0; i < row; i++) {
            if (!parmValue.getBoolean("FLG", i)) {
                continue;
            }
            parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            parm.addData("RX_NO", parmValue.getValue("RX_NO", i));
            parm.addData("SEQ_NO", parmValue.getValue("SEQ_NO", i));
            parm.addData("OPT_USER", Operator.getID());
            parm.addData("OPT_DATE", now);
            parm.addData("OPT_TERM", Operator.getIP());
            parm.addData("ORDER_DATE", parmValue.getTimestamp("ORDER_DATE", i));
            Timestamp execDate =
                    TCM_Transform.getTimestamp(table.getValueAt(i, table
                            .getColumnIndex("NS_EXEC_DATE_DAY")));
            if (execDate == null) {
                this.messageBox("日期格式错误");
                return;
            }
            String execTime =
                    TCM_Transform.getString(table.getValueAt(i, table
                            .getColumnIndex("NS_EXEC_DATE_TIME")));
            if (!execTime.matches("^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")) {
                this.messageBox("时间格式错误");
                return;
            }
            Timestamp checkDateTime =
                    StringTool
                            .getTimestamp(StringTool.getString(execDate, "yyyy/MM/dd") + execTime,
                                          "yyyy/MM/ddHH:mm:ss");
            parm.addData("NS_EXEC_DATE", checkDateTime);
            String execNote = (String) table.getValueAt(i, table.getColumnIndex("NS_NOTE")); // 护士备注
            if (!TCM_Transform.isNull(execNote)) {
                parm.addData("NS_NOTE", execNote);
            } else {
                parm.addData("NS_NOTE", "");
            }
            String setMainFlg = parmValue.getValue("SETMAIN_FLG", i);
            String orderSetGroupNo = parmValue.getValue("ORDERSET_GROUP_NO", i);
            if ("Y".equals(setMainFlg)) { // 处理集合医嘱
                parm =
                        setOrderDetail(parm, parmValue.getValue("CASE_NO", i), parmValue
                                .getValue("RX_NO", i), orderSetGroupNo, now, parmValue
                                .getTimestamp("ORDER_DATE", i), checkDateTime);
            }
        }
        if (parm.getCount("CASE_NO") < 1) {
            this.messageBox("无保存数据");
            return;
        }
        parm.setCount(parm.getCount("CASE_NO"));
        TParm inParm = (TParm) this.openDialog("%ROOT%\\config\\inw\\passWordCheck.x", "singleExe");
        String OK = inParm.getValue("RESULT");
        if (!OK.equals("OK")) {
            return;
        }
        // System.out.println("=============="+parm);
        TParm result =
                TIOM_AppServer.executeAction("action.erd.ERDOrderExecAction", "onSave", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败 " + result.getErrText());
            return;
        } else {
            String cisflg = "N";
            for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
                if (parm.getValue("CAT1_TYPE", i).equals("PHA")) {
                    cisflg = "Y";
                }
            }
            // 药嘱
            if (cisflg.equals("Y") && this.getValueString("NEXE").equals("Y")) {
                // ICU、CCU注记
                String caseNo = parm.getValue("CASE_NO", 0);
                boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNo);
                boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNo);
                if (IsICU || IsCCU) {
                    String typeF = "NBW";
                    List list = new ArrayList();
                    parm.setData("ADM_TYPE", "I");
                    list.add(parm);
                    // 调用接口
                    TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, typeF);
                    if (resultParm.getErrCode() < 0) messageBox(resultParm.getErrText());
                }
            }
            tiPanel3.setLayout(null);
            tiPanel3.removeAll();
            this.panelInit();
            table.removeRowAll();
            this.messageBox("保存成功");
            this.setValue("BAR_CODE", "");
            this.setValue("EXE_ALL", "N");
            table.removeRowAll();
        }
    }

    /**
     * 得到集合医嘱细项，后台保存用
     * @param parm
     * @param caseNo
     * @param orderNo
     * @param orderSetGroupNo
     * @param now
     * @param orderDate
     * @param execDate
     * @return
     */
    private TParm setOrderDetail(TParm parm, String caseNo, String orderNo, String orderSetGroupNo,
                                 Timestamp now, Timestamp orderDate, Timestamp execDate) {
        String sql =
                "SELECT * FROM OPD_ORDER WHERE CASE_NO='" + caseNo + "' AND RX_NO='" + orderNo
                        + "' AND ORDERSET_GROUP_NO=" + TCM_Transform.getInt(orderSetGroupNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            String setMainFlg = result.getValue("SETMAIN_FLG", i);
            if (!"Y".equals(setMainFlg)) { // 因为主项已经在上面压入，所以可以不重复包含
                parm.addData("CASE_NO", caseNo);
                parm.addData("RX_NO", orderNo);
                parm.addData("SEQ_NO", result.getValue("SEQ_NO", i));
                parm.addData("ORDER_DATE", orderDate);
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", now);
                parm.addData("OPT_TERM", Operator.getIP());
                parm.addData("NS_EXEC_DATE", execDate);
                parm.addData("NS_NOTE", "");
            }
        }
        return parm;
    }

    /**
     * Table中check_box勾选事件
     * 
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int selCol = table.getSelectedColumn();
        int selRow = table.getSelectedRow();
        String columnName = table.getParmMap(selCol);
        int row = table.getRowCount();
        TParm tblParm = table.getParmValue();
        Timestamp now = SystemTool.getInstance().getDate();
        Timestamp execDate =
                StringTool.getTimestamp(StringTool.getString(now, "yyyy/MM/dd"), "yyyy/MM/dd");
        String execTime = StringTool.getString(now, "HH:mm:ss");
        boolean flag = TCM_Transform.getBoolean(table.getValueAt(selRow, 0));
        if (!columnName.equals("FLG")) {
            return;
        }
        if (flag) {
            table.setItem(selRow, "NS_EXEC_CODE", Operator.getID());
            table.setItem(selRow, "NS_EXEC_DATE_DAY", execDate);
            table.setItem(selRow, "NS_EXEC_DATE_TIME", execTime);
        } else {
            table.setItem(selRow, "NS_EXEC_CODE", "");
            table.setItem(selRow, "NS_EXEC_DATE_DAY", "");
            table.setItem(selRow, "NS_EXEC_DATE_TIME", "");
        }
        if (tblParm.getValue("LINKMAIN_FLG", selRow).equals("Y")) {
            for (int i = 0; i < row; i++) {
                if (i == selRow) {
                    continue;
                }
                if (tblParm.getValue("RX_NO", i).equals(tblParm.getValue("RX_NO", selRow))
                        && tblParm.getValue("LINK_NO", i)
                                .equals(tblParm.getValue("LINK_NO", selRow))) {
                    if (flag) {
                        table.setValueAt(flag, i, 0);
                        table.setItem(i, "NS_EXEC_CODE", Operator.getID());
                        table.setItem(i, "NS_EXEC_DATE_DAY", execDate);
                        table.setItem(i, "NS_EXEC_DATE_TIME", execTime);
                    } else {
                        table.setValueAt(flag, i, 0);
                        table.setItem(i, "NS_EXEC_CODE", "");
                        table.setItem(i, "NS_EXEC_DATE_DAY", "");
                        table.setItem(i, "NS_EXEC_DATE_TIME", "");
                    }
                }
            }
        }
    }

    /**
     * 修改执行日期和时间（为了抢救医嘱）
     * 
     * @param node
     */
    public boolean onChangeDateTime(TTableNode node) {
        int col = node.getColumn();
        String colName = table.getParmMap(col);
        int row = node.getRow();// wanglong modify 20150128
        TParm parmValue = table.getParmValue();
        TParm rowParm = parmValue.getRow(row);
        Map map = new HashMap();
        if ("ORDER_DATE".equals(colName)) {
            Timestamp temp = (Timestamp) node.getValue();
            if (temp == null) {
                this.messageBox("时间格式错误");
                node.setValue(node.getOldValue());
                return true;
            }
            if (!rowParm.getValue("LINK_NO").equals("")) {
                String linkStr =
                        rowParm.getValue("CASE_NO") + rowParm.getValue("RX_NO")
                                + rowParm.getValue("LINK_NO");
                map.put(linkStr, linkStr);
                int count = parmValue.getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = parmValue.getRow(i);
                    String linkStrTemp =
                            parm.getValue("CASE_NO") + parm.getValue("RX_NO")
                                    + parm.getValue("LINK_NO");
                    if (map.get(linkStrTemp) != null) {
                        table.setItem(i, "ORDER_DATE", temp);
                    }
                }
            }
        }
        if ("NS_EXEC_DATE_DAY".equals(colName)) {
            Timestamp temp = (Timestamp) node.getValue();
            if (temp == null) {
                this.messageBox("时间格式错误");
                node.setValue(node.getOldValue());
                return true;
            }
            if (!rowParm.getValue("LINK_NO").equals("")) {
                String linkStr =
                        rowParm.getValue("CASE_NO") + rowParm.getValue("RX_NO")
                                + rowParm.getValue("LINK_NO");
                map.put(linkStr, linkStr);
                int count = table.getParmValue().getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = parmValue.getRow(i);
                    String linkStrTemp =
                            parm.getValue("CASE_NO") + parm.getValue("RX_NO")
                                    + parm.getValue("LINK_NO");
                    if (map.get(linkStrTemp) != null) {
                        table.setItem(i, "NS_EXEC_DATE", temp);
                    }
                }
            }
        }
        if ("NS_EXEC_DATE_TIME".equals(colName)) {
            String temp = (String) node.getValue();
            if (!temp.matches("^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")) {
                this.messageBox("时间格式错误");
                node.setValue(node.getOldValue());
                return true;
            }
            if (!rowParm.getValue("LINK_NO").equals("")) {
                String linkStr =
                        rowParm.getValue("CASE_NO") + rowParm.getValue("RX_NO")
                                + rowParm.getValue("LINK_NO");
                map.put(linkStr, linkStr);
                int count = table.getParmValue().getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = parmValue.getRow(i);
                    String linkStrTemp =
                            parm.getValue("CASE_NO") + parm.getValue("RX_NO")
                                    + parm.getValue("LINK_NO");
                    if (map.get(linkStrTemp) != null) {
                        table.setItem(i, "NS_EXEC_DATE_TIME", temp);
                    }
                }
            }
        }
        return false;
    }

    /**
     * “执行”勾选事件
     */
    public void onSelAll() {
        boolean flag = TypeTool.getBoolean(this.getValueString("EXE_ALL"));
        int row = table.getRowCount();
        Timestamp now = SystemTool.getInstance().getDate();
        if (flag) {
            Timestamp execDate =
                    StringTool.getTimestamp(StringTool.getString(now, "yyyy/MM/dd"), "yyyy/MM/dd");
            String execTime = StringTool.getString(now, "HH:mm:ss");
            for (int i = 0; i < row; i++) {
                table.setItem(i, "FLG", flag);
                table.setItem(i, "NS_EXEC_CODE", Operator.getID());
                table.setItem(i, "NS_EXEC_DATE_DAY", execDate);
                table.setItem(i, "NS_EXEC_DATE_TIME", execTime);
            }
        } else {
            for (int i = 0; i < row; i++) {
                table.setItem(i, "FLG", flag);
                table.setItem(i, "NS_EXEC_CODE", "");
                table.setItem(i, "NS_EXEC_DATE_DAY", "");
                table.setItem(i, "NS_EXEC_DATE_TIME", "");
            }
        }
    }

    /**
     * 改变单选条件
     */
    public void onChangeState() {
        this.setValue("EXE_ALL", "N");
        table.removeRowAll();
    }

    /**
     * 清空
     */
    public void onClear() {
        this.setValue("MR_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("SEX_DESC", "");
        this.setValue("BED_DESC", "");
        this.setValue("R1", "Y");
        this.setValue("NEXE", "Y");
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        Timestamp yesterday = StringTool.rollDate(now, -1);
        long timeStart = yesterday.getTime() - 60 * 60 * 1000;
        long timeEnd = now.getTime() + 60 * 60 * 1000;
        this.setValue("START_DATE", new Timestamp(timeStart));
        this.setValue("END_DATE", new Timestamp(timeEnd));
        this.setValue("BAR_CODE", "");
        this.setValue("EXE_ALL", "N");
        table.removeRowAll();
        tiPanel3.setLayout(null);
        tiPanel3.removeAll();
        this.panelInit();
        TPanel photo = (TPanel) this.getComponent("PHOTO_PANEL");
        Image image = null;
        Pic pic = new Pic(image);
        pic.setSize(photo.getWidth(), photo.getHeight());
        pic.setLocation(0, 0);
        photo.removeAll();
        photo.add(pic);
        pic.repaint();
    }

    /**
     * 病患照片显示方法
     * 
     * @param mrNo
     */
    public void viewPhoto(String mrNo) {
        String photoName = mrNo + ".jpg";
        String fileName = photoName;
        try {
            TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
            String root = TIOM_FileServer.getRoot();
            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            dir =
                    root + dir + mrNo.substring(0, 3) + "\\" + mrNo.substring(3, 6) + "\\"
                            + mrNo.substring(6, 9) + "\\";
            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), dir + fileName);
            if (data == null) {
                viewPanel.removeAll();
                return;
            }
            double scale = 0.5;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
            pic.setLocation(0, 2);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片
     * 
     */
    public class PHA_PIC
            extends TiMultiPanel
            implements MouseListener {

        private static final long serialVersionUID = 6685653491843293933L;
        TiLabel tiL_orderDesc = new TiLabel();
        TiLabel tiL_Laber = new TiLabel();
        private String OrderDesc = "";
        TiPanel tiPanel1 = new TiPanel();
        TitledBorder titledBorder1;

        /**
         * 构造方法
         */
        public PHA_PIC() {
            try {
                jbInit();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 构造方法
         * @param OrderDesc
         */
        public PHA_PIC(String OrderDesc) {
            try {
                this.OrderDesc = OrderDesc;
                jbInit();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 构造方法
         * @param OrderCode
         * @param OrderDesc
         */
        public PHA_PIC(String OrderCode, String OrderDesc) {
            try {
                this.OrderDesc = OrderDesc;
                jbInit();
                viewPhoto(OrderCode);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void jbInit() throws Exception {
            titledBorder1 = new TitledBorder("");
            tiL_orderDesc.setBackground(Color.black);
            tiL_orderDesc.setFont(new java.awt.Font("宋体", 1, 12));
            tiL_orderDesc.setForeground(Color.black);
            tiL_orderDesc.setText(this.OrderDesc);
            tiL_orderDesc.setBounds(new Rectangle(9, 1, 240, 15));
            tiL_Laber.setBackground(Color.black);
            tiL_Laber.setFont(new java.awt.Font("宋体", 1, 12));
            tiL_Laber.setForeground(Color.black);
            tiL_Laber.setText("没有图片");
            tiL_Laber.setBounds(new Rectangle(60, 70, 112, 15));
            this.setFont(new java.awt.Font("Dialog", 0, 11));
            this.setBorder(BorderFactory.createEtchedBorder());
            this.setLayout(null);
            this.addMouseListener(this);
            tiPanel1.setBounds(new Rectangle(30, 15, 190, 180));
            tiPanel1.setLayout(null);
            this.add(tiPanel1, null);
            this.add(tiL_orderDesc, null);
        }

        /**
         * 图片显示方法
         * 
         * @param orderCode
         */
        public void viewPhoto(String orderCode) {
            String photoName = orderCode + ".jpg";
            String fileName = photoName;
            try {
                String root = TIOM_FileServer.getRoot();
                String dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
                dir = root + dir;
                byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), dir + fileName);
                if (data == null) {
                    tiPanel1.removeAll();
                    tiPanel1.add(tiL_Laber, null);
                    return;
                }
                double scale = 0.7;
                boolean flag = true;
                Image image = ImageTool.scale(data, scale, flag);
                Pic pic = new Pic(image);
                pic.setSize(tiPanel1.getWidth(), tiPanel1.getHeight());
                pic.setLocation(0, 0);
                tiPanel1.removeAll();
                pic.setHorizontalAlignment(0);
                tiPanel1.add(pic, null);
                pic.repaint();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (this.OrderDesc.equals("")) {
                JOptionPane.showMessageDialog(this, "没有图片");
                return;
            }
        }

        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public String getOrderDesc() {
            return OrderDesc;
        }

        public void setOrderDesc(String OrderDesc) {
            this.OrderDesc = OrderDesc;
        }
    }

    class Pic
            extends JLabel {

        Image image;

        public Pic(Image image) {
            this.image = image;
        }

        public void paint(Graphics g) {
            g.setColor(new Color(161, 220, 230));
            g.fillRect(4, 15, 100, 100);
            if (image != null) {
                g.drawImage(image, 4, 15, null);
            }
        }
    }
}
