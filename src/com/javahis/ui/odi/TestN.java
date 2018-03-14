package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TPanel;
import com.dongyang.manager.TIOM_FileServer;
import java.awt.Image;
import com.dongyang.util.ImageTool;
import jdo.odi.TestNtool;
import java.awt.Component;
import com.dongyang.util.StringTool;
import jdo.sys.PatTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Color;
import com.dongyang.data.TNull;

/**
 * <p>Title: 行动护理执行</p>
 *
 * <p>Description: 行动护理执行</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TestN
    extends TControl {

    //病患基本信息111
    TParm patInfo = new TParm();

    public void onInit() {
        super.onInit();
        //  getTextField("MR_NO").grabFocus();
//        nowDate();
        onControl();
//        R1.setSelected(true);
        setExeDate();
        ((TTextField)getComponent("MR_NO")).grabFocus();
        callFunction("UI|tableM|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onTableCheckBoxChangeValue");

    }

    private void setExeDate(){
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        long timeStart = now.getTime() - 60 * 60 * 1000;
        long timeEnd = now.getTime() + 60 * 60 * 1000;
        setValue("start_Date",new Timestamp(timeStart));
        setValue("end_Date",new Timestamp(timeEnd));
    }

    private TRadioButton R1, R2, R3, R4;
    private String action = "a";
    TestNtool tool = new TestNtool();

    /**
     * 初始化时间
     */
    public void onQuery() {
        onEnter();
        onBarCode();
    }

    public void nowDate() {
        // 得到当前时间
        Timestamp date = SystemTool.getInstance().getDate();
        String Y = date.toString().substring(0, 4);
        String M = date.toString().substring(5, 7);
        String D = date.toString().substring(8, 10);
        String H = date.toString().substring(11, 13);
        int y = Integer.parseInt(Y);
        int m = Integer.parseInt(M);
        int d = Integer.parseInt(D);
        int h = Integer.parseInt(H);
        String YY = "" + y;
        String MM = "" + m;
        String DD = "" + d;
        String HH = "" + h;
        h += 1;
        if (h < 10) {
            HH = "0" + h;
        }
        if (h > 9 && h < 24) {
            HH = "" + h;
        }
        if (h > 24 || h == 24 || h == 00) {
            if (h == 24) {
                HH = "00";
                d += 1;
                DD = "" + d;
            }
            if (h > 24) {
                HH = "0" + (h - 24);
                d += 1;
                DD = "" + d;
            }
            if (d == 28 && y % 4 != 0) {
                DD = "01";
                MM = "03";
            }
            if (d == 30 &&
                (m != 1 || m != 3 || m != 5 || m != 7 || m != 8 || m != 10 ||
                 m != 12)) {
                DD = "01";
                if (m < 10) {
                    MM = "0" + (m + 1);
                }
                if (m > 9 && m <= 12) {
                    MM = "" + m;
                }
                if (m > 12) {
                    MM = "0" + (m - 12);
                    y += 1;
                    YY = "" + y;
                }
            }
            if (d == 30 &&
                (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 ||
                 m == 12)) {
                d += 1;
                DD = "" + d;
                if (m < 10) {
                    MM = "0" + m;
                }
                if (m > 9 && m <= 12) {
                    MM = "" + m;
                }
                if (m > 12) {
                    MM = "0" + (m - 12);
                    y += 1;
                    YY = "" + y;
                }
            }

        }
        if (d == 32) {
            DD = "01";
            MM = "" + (m + 1);
        }
        if (m < 10) {
            MM = "0" + m;
        }
        if (d < 10) {
            DD = "0" + d;
        }

        if (m > 9 && m <= 12) {
            MM = "" + m;
        }
        if (m > 12) {
            MM = "0" + (m - 12);
            YY = "" + (y + 1);
        }

        String end_date = "" + YY + "-" + MM + "-" + DD + " " + HH + ":00:00";
        String start_Date = date.toString().substring(0, 14).replace('-', '/') +
            "00:00";
//        String Start_Date=StringTool;
//        String End_Date=(Timestamp)end_Date;

        end_date = end_date.replace('-', '/');
//        this.messageBox(start_Date);
//         this.messageBox(end_date);
        this.setValue("start_Date", start_Date);
        this.setValue("end_Date", end_date);
    }

    /**
     * 初始化控件
     */
    public void onControl() {
        this.setValue("QY", Operator.getRegion());
        this.setValue("KS", Operator.getDept());
        this.setValue("BQ", Operator.getStation());
//        R1 = (TRadioButton)this.getComponent("R1");

    }

    public void onQueryPatInfo(){
        String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        String SQL = TestNtool.getInstance().queryPatInfo(mrNo);
        patInfo = new TParm(TJDODBTool.getInstance().select(SQL));
        setValue("MR_NO",mrNo);
        setValue("PAT_NAME",patInfo.getValue("PAT_NAME",0));
        setValue("SEX_CODE",patInfo.getValue("CHN_DESC",0));
        setValue("bed_no",patInfo.getValue("BED_NO_DESC",0));
        viewPhoto(mrNo);
        ((TTextField)getComponent("tt")).grabFocus();
    }

    public void onBarCode(){
        String startDate = StringTool.getString((Timestamp)getValue("start_Date"),"yyyyMMddHHmmss");
        String endDate = StringTool.getString((Timestamp)getValue("end_Date"),"yyyyMMddHHmmss");
        String cat1Type = "";
        if(getValueString("R2").equals("Y"))
            cat1Type = "PHA";
        if(getValueString("R3").equals("Y"))
            cat1Type = "LIS','RIS";
        if(getValueString("R4").equals("Y"))
            cat1Type = "TRT','PLN','OTH";
        String isEex = "";
        if(getValueString("ALL").equals("Y"))
            isEex = "A";
        else if(getValueString("YEXE").equals("Y"))
            isEex = "Y";
        else if(getValueString("NEXE").equals("Y"))
            isEex = "N";
        String SQL = TestNtool.getInstance().queryPatOrder(patInfo.getValue("CASE_NO",0),
                                                           getValueString("tt"),
                                                           startDate,
                                                           endDate,
                                                           cat1Type,
                                                           isEex);
        TParm parmTable = new TParm(TJDODBTool.getInstance().select(SQL));
        getTTable("tableM").setParmValue(parmTable);
    }


    public TParm getExeSaveDate(){
        getTTable("tableM").acceptText();
        int row = getTTable("tableM").getRowCount();
        TParm parmTable = getTTable("tableM").getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        TParm parmM = new TParm();
        TParm parmD = new TParm();
        for(int i = 0;i<row;i++){
            parmM.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
            parmM.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
            parmM.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
            parmM.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
            parmM.addData("ORDER_DATETIME", parmTable.getValue("ORDER_DATETIME", i));
            parmM.addData("CANCELRSN_CODE", parmTable.getValue("CANCELRSN_CODE", i));

            parmD.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
            parmD.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
            parmD.addData("ORDER_SEQ", parmTable.getValue("ORDER_SEQ", i));
            parmD.addData("ORDER_DATE", parmTable.getValue("ORDER_DATE", i));
            parmD.addData("ORDER_DATETIME", parmTable.getValue("ORDER_DATETIME", i));
            parmD.addData("INV_CODE", parmTable.getValue("INV_CODE", i));
            parmD.addData("CANCELRSN_CODE", parmTable.getValue("CANCELRSN_CODE", i));
            if(getTTable("tableM").getValueAt(i,0).equals("N")){
                parmD.addData("NS_EXEC_DATE_REAL", parmTable.getData("NS_EXEC_DATE_REAL", i) == null?
                                                   new TNull(Timestamp.class):
                                                   parmTable.getData("NS_EXEC_DATE_REAL", i));
                parmD.addData("NS_EXEC_CODE_REAL", parmTable.getData("NS_EXEC_CODE_REAL", i) == null?
                                                   new TNull(Timestamp.class):
                                                   parmTable.getData("NS_EXEC_CODE_REAL", i));
                continue;
            }
            if(getTTable("tableM").getValueAt(i,1).equals("N")){
                parmD.addData("NS_EXEC_DATE_REAL", now);
                parmD.addData("NS_EXEC_CODE_REAL", Operator.getID());
            }
            else{
                parmD.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
                parmD.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
            }
            if(parmTable.getValue("ORDERSET_GROUP_NO", i).length() == 0)
                continue;
            setDetailOrder(parmTable,i,parmD,now);
        }
        TParm parm = new TParm();
        parm.setData("DSPNM",parmM.getData());
        parm.setData("DSPND",parmD.getData());
        return parm;
    }

    public void onSaveExe(){
        String value = (String)this.openDialog("%ROOT%\\config\\inw\\SingleExeOptCheck.x");
        if (value == null)
            return;
        if(!Operator.getID().equals(value)){
            messageBox("登录人员与当前操作人员不符");
            return;
        }
        getTTable("tableM").acceptText();
        TParm parm = getExeSaveDate();
        if(parm == null)
            return;
        TParm result = TIOM_AppServer.executeAction(
            "action.inw.INWOrderSingleExeAction",
            "onSaveExe", parm);
        if (result.getErrCode() < 0) {
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
        onBarCode();
    }

    private void setDetailOrder(TParm parmTable,int i,TParm parm,Timestamp now){
        String SQL = TestNtool.getInstance().queryPatOrderSetDetail(patInfo.getValue("CASE_NO",0),
                                                                    parmTable.getValue("ORDER_NO", i),
                                                                    parmTable.getValue("ORDER_DATE", i),
                                                                    parmTable.getValue("ORDER_DATETIME", i),
                                                                    parmTable.getValue("ORDERSET_GROUP_NO", i));
        TParm detailParm = new TParm(TJDODBTool.getInstance().select(SQL));
        for(int j = 0;j<detailParm.getCount();j++){
            parm.addData("CASE_NO", detailParm.getValue("CASE_NO", j));
            parm.addData("ORDER_NO", detailParm.getValue("ORDER_NO", j));
            parm.addData("ORDER_SEQ", detailParm.getValue("ORDER_SEQ", j));
            parm.addData("ORDER_DATE", detailParm.getValue("ORDER_DATE", j));
            parm.addData("ORDER_DATETIME",detailParm.getValue("ORDER_DATETIME", j));
            parm.addData("INV_CODE",new TNull(String.class));
            parm.addData("CANCELRSN_CODE",new TNull(String.class));
            if(getTTable("tableM").getValueAt(i,1).equals("N")){
                parm.addData("NS_EXEC_DATE_REAL", now);
                parm.addData("NS_EXEC_CODE_REAL", Operator.getID());
            }
            else{
                parm.addData("NS_EXEC_DATE_REAL", new TNull(Timestamp.class));
                parm.addData("NS_EXEC_CODE_REAL", new TNull(String.class));
            }
        }
    }

    public void onExe(){
        int row = getTTable("tableM").getRowCount();
        for(int i = 0;i<row;i++){
            getTTable("tableM").setValueAt(getValue("EXE_ALL"),i,0);
        }
    }

    public void onTableCheckBoxChangeValue(Object obj){
        TTable table = (TTable) obj;
        table.acceptText();
        int selCol = table.getSelectedColumn();
        int selRow = table.getSelectedRow();
        String columnName = table.getParmMap(selCol);
        int row = table.getRowCount();
        TParm tblParm = table.getParmValue();
        if(!columnName.equals("SEL_FLG"))
            return;
        if(!tblParm.getValue("LINKMAIN_FLG",selRow).equals("Y"))
            return;
        for(int i = 0;i<row;i++){
            if(i == selRow)
                continue;
            if(!tblParm.getValue("LINK_NO",i).equals(tblParm.getValue("LINK_NO",selRow)))
                continue;
            if(!tblParm.getValue("ORDER_NO",i).equals(tblParm.getValue("ORDER_NO",selRow)))
                continue;
            if(!tblParm.getValue("ORDER_DATE",i).equals(tblParm.getValue("ORDER_DATE",selRow)))
                continue;
            if(!tblParm.getValue("ORDER_DATETIME",i).equals(tblParm.getValue("ORDER_DATETIME",selRow)))
                continue;
            table.setValueAt(table.getValueAt(selRow,0),i,0);
        }
    }

    /**
     * 病案号的回车事件
     */
    public void onEnter() {
        onQueryPatInfo();
        /*String start_Date = this.getValueString("start_Date");
        String end_date = this.getValueString("end_date");
        String Star_time = start_Date.toString().substring(11, 13) +
            start_Date.toString().substring(14, 16);
        String End_date = end_date.toString().substring(9, 11) +
            end_date.toString().substring(12, 14);
        String order_date = start_Date.substring(0, 10).replace("/", "");

        Pat pat = new Pat();
        pat = pat.onQueryByMrNo(getValueString("MR_NO"));
        if (pat == null || "".equals(pat.getMrNo())) {
            this.messageBox("查无此病患!");
            return;
        }
        String mr_no = pat.getMrNo();
        this.setValue("MR_NO", mr_no);
        QueryBed_no(mr_no);
        QueryPatInfo(mr_no);
        getTextField("MR_NO").grabFocus();
        viewPhoto(mr_no);
        getTextField("tt").grabFocus();
        onQueryM(mr_no, Operator.getRegion(), Operator.getStation(),
                 Operator.getDept(), Star_time, End_date, order_date);*/
    }

    public void QueryBed_no(String mr_no) {
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        String bed_no = ADMInpTool.getInstance().queryCaseNo(parm).getRow(0).
            getValue("BED_NO");
        this.setValue("bed_no", bed_no);

    }

    public void QueryPatInfo(String mr_no) {
        String sql = tool.sql_Patinfo(mr_no);
        TParm selParm = new TParm();
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        this.setValue("PAT_NAME", selParm.getValue("PAT_NAME", 0));
        this.setValue("SEX_CODE", selParm.getValue("CHN_DESC", 0));
    }

    /**
     * 给table放数据
     * @param MR_NO String
     * @param REGION_CODE String
     * @param STATION_CODE String
     * @param EXEC_DEPT_CODE String
     */
    public void onQueryM(String MR_NO, String REGION_CODE, String STATION_CODE,
                         String EXEC_DEPT_CODE, String Star_time,
                         String End_date, String order_date) {
        String sql1 = tool.sql_caseNo(MR_NO);
        // this.messageBox("sql" + sql1);
        TParm selParm1 = new TParm();
        selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
        String case_no = selParm1.getValue("CASE_NO", 0);
        //this.messageBox(case_no);

        if (null == case_no || "".equals(case_no)) {
            this.messageBox("该患者已出院或是未住院");
            return;
        }

        String sql = tool.sql_TableMessage(case_no, REGION_CODE, STATION_CODE,
                                           EXEC_DEPT_CODE, Star_time, End_date,
                                           order_date);
        TParm selParm = new TParm();
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount() < 0)
            this.messageBox("此患者在本时段没有要执行的医嘱或是已全部执行完毕");
        this.getTTable("tableM").setParmValue(selParm);
    }

    /**
     * 清空方法
     * @param tagName String
     * @return TTextField
     */
    public void onClear() {
        setValue("R1","Y");
        setValue("MR_NO","");
        setValue("PAT_NAME","");
        setValue("SEX_CODE","");
        setValue("bed_no","");
        setExeDate();
        setValue("tt","");
        setValue("EXE_ALL","N");
        getTTable("tableM").removeRowAll();

        TPanel photo = (TPanel)this.getComponent("PHOTO_PANEL");
        Image image = null;
        Pic pic = new Pic(image);
        pic.setSize(photo.getWidth(), photo.getHeight());
        pic.setLocation(0, 0);
        photo.removeAll();
        photo.add(pic);
        pic.repaint();

//        this.clearValue(
//            "MR_NO;PAT_NAME;SEX_CODE;bed_no;tt;PHOTO_PANEL");
//
//        this.getTTable("tableM").setSelectionMode(0);
//        this.getTTable("tableM").removeRowAll();
//        this.action = "a";
//        TPanel photo = (TPanel)this.getCompnent("PHOTO_PANEL");
//        Image image = null;
//        Pic pic = new Pic(image);
//        pic.setSize(photo.getWidth(), photo.getHeight());
//                    pic.setLocation(0, 0);
//        photo.removeAll();
//        photo.add(pic);
//        pic.repaint();
//
//        this.onInit();

    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     * 元素TAG名称
     * @return
     */

    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }


    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
/*
    public void viewPhoto(String mrNo) {
        //  String photoName = mNo + ".jpg";
//        String photoName = "" + mrNo + ".jpg";
//        String fileName = photoName;
//        String mrNo1 = "" + mrNo.substring(0, 3) + "\\";
//        String mrNo2 = "" + mrNo.substring(3, 6) + "\\";
//        String mrNo3 = "" + mrNo.substring(6, 9);
//        try {
//            TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
//            String root = TIOM_FileServer.getRoot();
//            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
//            dir = root + dir + mrNo1 + mrNo2 + mrNo3 + "\\";
//            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
//                dir + fileName);
//            if (data == null)
//                return;
//            double scale = 0.45;
//            boolean flag = true;
//            Image image = ImageTool.scale(data, scale, flag);
//            Pic pic = new Pic(image);
//            pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
//            pic.setLocation(0, 0);
//            viewPanel.removeAll();
//            viewPanel.add(pic);
//            pic.repaint();
//        }
//        catch (Exception e) {
//            this.messageBox_(e);
//        }

    }
*/
    /**
     * 保存事件
     */
    public void onSave() {
        onSaveExe();
        /*String value = (String)this.openDialog(
            "%ROOT%\\config\\inw\\passWordCheck.x");
        if (value == null) {
            return;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        this.getTTable("tableM").setSelectionMode(0);
        TParm parm = getTTable("tableM").getParmValue();
        int row = parm.getCount();
        String a = parm.getValue("SELECT_FLG");
        String b = parm.getValue("EXEC_FLG");
        for (int i = 0; i < row; i++) {
            if ("Y".equals(parm.getValue("EXEC_FLG", i))) {
                TParm tparm = new TParm();
                tparm.setData("OPT_USER", Operator.getID());
                tparm.setData("OPT_DATE", date);
                tparm.setData("OPT_TERM", Operator.getIP());
                tparm.setData("CASE_NO", parm.getValue("CASE_NO", i));
                tparm.setData("ORDER_NO", parm.getValue("ORDER_NO", i));
                tparm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
                tparm.setData("ORDER_DATE", parm.getValue("ORDER_DATE", i));
                tparm.setData("ORDER_DATETIME",
                              parm.getValue("ORDER_DATETIME", i));

                tparm.setData("NS_EXEC_CODE", Operator.getID());

                TestNtool.getInstance().onUpdate(tparm);
            }
            else {
                continue;
            }

        }
        this.messageBox("P0001");*/

    }

    public void viewPhoto(String mrNo) {
        String photoName = mrNo + ".jpg";
        String fileName = photoName;
        try {
            TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
            String root = TIOM_FileServer.getRoot();
            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            dir = root + dir + mrNo.substring(0,3) + "\\" + mrNo.substring(3,6) + "\\" +
                mrNo.substring(6,9) + "\\";

            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
                dir + fileName);
            if (data == null)
                return;
            double scale = 0.5;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
            pic.setLocation(0, 0);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
        }
        catch (Exception e) {}
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
