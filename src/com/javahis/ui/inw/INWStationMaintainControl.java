package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import java.util.Vector;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sta.STADeptListTool;
import jdo.ibs.IBSOrdermTool;
/**
 * <p>Title: </p>
 *
 * <p>Description:住院护士站病区维护主程序</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class INWStationMaintainControl
    extends TControl {

    //主表
    private TTable masterTbl;
    private TTable detailTbl;

    private TTextFormat DEPT;
    private TTextFormat STATION;

    private TTextFormat DATE;

    private TCheckBox CHECK1;
    private TCheckBox CHECK2;
    private TCheckBox CHECK3;
    private TCheckBox AllSel;
    TParm masterTblParm = new TParm();
    private TNumberTextField totPerson;

    public INWStationMaintainControl() {
    }

    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
        //本界面的初始化
        myInitControler();

        //测试根据人员初始化部分
//        System.out.println("=>" + Operator.getDept());
//        System.out.println("=>" + Operator.getName());
//        System.out.println("=>" + Operator.getID());
        initByOptUser();
    }


    /**
     * 初始化
     * 根据登陆人员的(权限)信息初始化
     * （拿到登陆人员的信息）
     */
    public void initByOptUser() {
        DEPT.setValue(Operator.getDept());
        STATION.setValue(Operator.getStation());
    }

    /**
     * 查询
     */
    public void onQuery() {
        //管控不可所有病区查询
        if(DEPT.getValue()==null){
            this.messageBox("请选择科室！");
            return;
        }
        if(STATION.getValue()==null){
            this.messageBox("请选择病区！");
            return;
        }

        //清除先前的数据
        masterTbl.removeRowAll();
        detailTbl.removeRowAll();

        //初始化当前table
        initDataStoreToTable();

    }

    /**
     * 初始化table
     * 查询条件是： caseNo/病区
     */
    /*public void initDataStoreToTable() {
        //如果没有审核--没有LAST_DSPN_DATE就无法查询
        String matserColum = " STATION_CODE, BED_NO,IPD_NO,MR_NO,CASE_NO ";//CASE_NO
        String nowDate=StringTool.getString((Timestamp)DATE.getValue(),"yyyyMMddHHmmss");
        String EXEsql = " SELECT DISTINCT " + " (SELECT PAT_NAME FROM SYS_PATINFO WHERE SYS_PATINFO.MR_NO=ODI_ORDER.MR_NO) AS PAT_NAME, " +
            matserColum +
            " FROM ODI_ORDER WHERE " +
            (buildWhere().equals("") ? "" : (buildWhere() + " AND ")) +
            " RX_KIND = 'UD' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') AND DC_DR_CODE IS NULL " +
            " AND NS_CHECK_CODE IS NOT NULL "+
            " AND LAST_DSPN_DATE <= TO_DATE("+nowDate+",'YYYYMMDDHH24MISS')"+
            "ORDER BY MR_NO ";
//        System.out.println("病区维护SQL===>"+EXEsql);

        TParm parm=new TParm(TJDODBTool.getInstance().select(EXEsql));
        //手动填充第一列
        for(int i=0;i<parm.getCount();i++){
            parm.setData("EXE",i,"N");
        }

        masterTbl.setParmValue(parm);
        totPerson.setValue(masterTbl.getRowCount());
    }*/

    /**
     * 初始化table
     * 查询条件是： caseNo/病区
     */
    public void initDataStoreToTable() {
        //如果没有审核--没有LAST_DSPN_DATE就无法查询
        //=================pangben modify 20110511 start  添加区域条件只能显示本区域的数据
        String region="";
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            region=" AND A.REGION_CODE='"+Operator.getRegion()+"'";
        //=================pangben modify 20110511 stop
//        System.out.println("界面日期" + this.getValue("DATE"));
        String dateS = StringTool.getString(TypeTool.getTimestamp(getValue("DATE")),"yyyyMMdd");
        String EXEsql =
            " SELECT B.PAT_NAME,A.STATION_CODE, A.BED_NO,A.IPD_NO,A.MR_NO,A.CASE_NO " +
            "   FROM ADM_INP A,SYS_PATINFO B" +
            "  WHERE DS_DATE IS NULL" +
            (getValueString("DEPT").length() == 0 ? "" :
             "   AND DEPT_CODE = '" + getValueString("DEPT") + "'") +
            (getValueString("STATION").length() == 0 ? "" :
             "   AND STATION_CODE = '" + getValueString("STATION") + "'") +
            "    AND (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL)" +
            "    AND A.IN_DATE < TO_DATE('"+dateS+"','YYYYMMDD') "+
            "    AND A.BED_NO IS NOT NULL " +
            "    AND A.MR_NO = B.MR_NO" + region +
            "  ORDER BY A.MR_NO ";
//        System.out.println("病区维护SQL===>"+EXEsql);

       TParm parm = new TParm(TJDODBTool.getInstance().select(EXEsql));
       //=======pangben modify 20110511 start
       if (null == parm || parm.getCount("STATION_CODE") < 1) {
           this.messageBox("没有要查找的数据");
           return;
       }
        //=======pangben modify 20110511 stop
        //手动填充第一列
        for(int i=0;i<parm.getCount();i++){
            parm.setData("EXE",i,"N");
        }
        masterTbl.setParmValue(parm);
        totPerson.setValue(masterTbl.getRowCount());
    }

    public void onSave(){
        if(AllSel.getValue().equals("Y"))
            onSaveAll();
        else
            onSavePerson();
    }

    public void onSavePerson(){
        masterTbl.acceptText();
        boolean saveFlag = false;
        for(int i = 0;i < masterTbl.getRowCount();i++){
            if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                continue;
            saveFlag = true;
            break;
        }
        if(!saveFlag){
            messageBox("无展开资料");
            return;
        }
        if (CHECK2.isSelected()) {
            //固定费用的滚动
            if (!unfoldFeePerson()) {
                this.messageBox("固定费用\n展开失败！");
            }
            else
                this.messageBox("固定费用\n展开成功！");
        }
        int rows=masterTbl.getRowCount();
        if(rows<=0){
            this.messageBox("没有长期医嘱展开数据！");
            return;
        }

        //循环测试是否有勾选的项目
        boolean saveFlg=false;
        for(int i=0;i<rows;i++){
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                saveFlg=true;
        }
        if(!saveFlg){
            this.messageBox("请选择展开数据！");
            return;
        }

        //根据选择执行相应的动作--长期医嘱 固定费用 病区日志
        if (CHECK1.isSelected()) {
            //调用展开方法
            if (!unfoldOrder()) {
                this.messageBox("长期处置\n展开失败！");
            }
            else
                this.messageBox("长期处置\n展开成功！");
        }
        onQuery();
        return;
    }

    /**
     * 保存
     */
    public void onSaveAll(){
        if (CHECK2.isSelected()) {
            //固定费用的滚动
            if (!unfoldFee()) {
                this.messageBox("固定费用\n展开失败！");
            }
            else
                this.messageBox("固定费用\n展开成功！");
        }

        if (CHECK3.isSelected()) {
            if(!unfoldLog()){
                this.messageBox("病区日志\n展开失败！");
            }
            else
                this.messageBox("病区日志\n展开成功！");
        }
        masterTbl.acceptText();
        int rows=masterTbl.getRowCount();
        if(rows<=0){
            this.messageBox("没有长期医嘱展开数据！");
            return;
        }

        //循环测试是否有勾选的项目
        boolean saveFlg=false;
        for(int i=0;i<rows;i++){
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                saveFlg=true;
        }
        if(!saveFlg){
            this.messageBox("请选择展开数据！");
            return;
        }

        //根据选择执行相应的动作--长期医嘱 固定费用 病区日志
        if (CHECK1.isSelected()) {
            //调用展开方法
            if (!unfoldOrder()) {
                this.messageBox("长期处置\n展开失败！");
            }
            else
                this.messageBox("长期处置\n展开成功！");
        }
        onQuery();
        return;
    }

    /**
     * 日志展开
     * @return boolean
     */
    private boolean unfoldLog() {
//        TParm parm = new TParm();
//        parm.setData("STATION",STATION.getValue());
//        this.openDialog("%ROOT%/config/sta/STAStationLog.x",parm);
        TParm sql = new TParm();
        Timestamp time = StringTool.rollDate( (Timestamp)this.getValue(
            "DATE"), -1);
        sql.setData("STADATE", StringTool.getString(time,"yyyyMMdd"));
        sql.setData("OPT_USER",Operator.getID());
        sql.setData("OPT_TERM",Operator.getIP());
        sql.setData("REGION_CODE",Operator.getRegion());
//        System.out.println("");
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STADailyAction",
            "insertStation_Daily", parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return false;
        }
        return true;
    }


    /**
     * 费用展开
     * @return boolean
     */
    private boolean unfoldFee() {
        if (("" + STATION.getValue()) == null ||
            ("" + STATION.getValue()).length() == 0 ||
            ("" + STATION.getValue()).equalsIgnoreCase("null")) {
            messageBox("固定费用展开需要传入病区");
            return false;
        }
        String caseNo = "";
        String caseNoSeq = "";
        boolean flg = false;
        for (int i = 0; i < masterTblParm.getCount(); i++) {
//            System.out.println("第"+i+"个标记位"+masterTblParm.getBoolean("EXE", i));
            if (masterTblParm.getBoolean("EXE", i)) {
                caseNo = masterTblParm.getValue("CASE_NO", i);
                caseNoSeq = IBSOrdermTool.getInstance().
                                   selCaseNoSeqForPatch(
                                           caseNo,
                                           StringTool.getString(StringTool.
                        rollDate(SystemTool.
                                 getInstance().getDate(), -1), "yyyyMMdd"));
//                System.out.println("第"+i+"个"+caseNoSeq);
                if (caseNoSeq.length() > 0)
                    flg = true;
            }
        }
//        System.out.println(">>>>>>>>>>>>>>>>"+flg);
        if (flg) {
            if (this.messageBox("提示", "已产生费用,是否重展", 2) == 0) {
//                this.messageBox("重展开始");
                TParm parm = new TParm();
                parm.setData("STATION_CODE", STATION.getValue());
                parm.setData("DATE", SystemTool.getInstance().getDate());
                parm.setData("OPT_USER", Operator.getID());
                parm.setData("OPT_TERM", Operator.getIP());
                TParm result = TIOM_AppServer.executeAction(
                        "action.adm.ADMInpAction",
                        "postAutoBill", parm); // 住院登记保存
                if (result.getErrCode() < 0) {
                    this.messageBox_(result.getErrName() + result.getErrText());
                    return false;
                }
            }
//            this.messageBox("重展结束");
        } else {
//            this.messageBox("重展失败开始");
            TParm parm = new TParm();
            parm.setData("STATION_CODE", STATION.getValue());
            parm.setData("DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                    "action.adm.ADMInpAction",
                    "postAutoBill", parm); // 住院登记保存
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrName() + result.getErrText());
                return false;
            }
//            this.messageBox("重展失败结束");

        }

        return true;
    }

    /**
     * 费用展开(单人)
     * @return boolean
     */
    private boolean unfoldFeePerson() {
        for (int i = 0; i < masterTbl.getRowCount(); i++) {
            if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                continue;
            String caseNo = "" +
                            masterTbl.getParmValue().getData("CASE_NO", i);
            TParm parm = new TParm();
            parm.setData("CASE_NO", caseNo);
            parm.setData("DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            String caseNoSeq = IBSOrdermTool.getInstance().selCaseNoSeqForPatch(
                    caseNo,
                    StringTool.getString( StringTool.rollDate(SystemTool.getInstance().getDate(),-1),
                                         "yyyyMMdd"));
            if (caseNoSeq.length() > 0) {
                if (this.messageBox("提示", "已产生费用,是否重展", 2) == 0) {
                    TParm result = TIOM_AppServer.executeAction(
                            "action.adm.ADMInpAction",
                            "postAutoBillOfMen", parm); // 住院登记保存
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result.getErrName() +
                                         result.getErrText());
                        return false;
                    }

                }
            } else {
                TParm result = TIOM_AppServer.executeAction(
                        "action.adm.ADMInpAction",
                        "postAutoBillOfMen", parm); // 住院登记保存
                if (result.getErrCode() < 0) {
                    this.messageBox_(result.getErrName() +
                                     result.getErrText());
                    return false;
                }

            }
        }
        return true;
    }
    /**
     * 单击主表激发事件
     * @param row int
     */
    public void onMasterTbl(int row){

        detailTbl.removeRowAll();
        //选择的行数
        int selRow=row;
        if(selRow==-1)
            return;
        // 只是显示步骤
        showDate();
    }

    /**
     * 在没有勾选的时候只显示用
     */
    private void showDate(){
    	 Timestamp now = TJDODBTool.getInstance().getDBTime();
         String nowString = StringTool.getString(now, "yyyyMMdd") + "0000";
        //当前选中行号
        int selRow = masterTbl.getSelectedRow();
        //拿到病区和床号就可以确定一个唯一的病患
        String stationCode = (String) masterTbl.getParmValue().getData(
            "STATION_CODE", selRow);
        String bedNo = (String) masterTbl.getParmValue().getData("BED_NO",
            selRow);
        String caseNo = "" + masterTbl.getParmValue().getData("CASE_NO",
            selRow);
        String sql =
            "SELECT * FROM ODI_ORDER WHERE STATION_CODE='" +
            stationCode + "' AND BED_NO='" + bedNo +
            "' AND CASE_NO = '"+caseNo+""+
            "' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') AND RX_KIND = 'UD' " +
            " AND (DC_DATE IS NULL OR DC_DATE > TO_DATE('"+ nowString+ "','YYYYMMDDHH24MI') )"+
            " AND (ORDERSET_CODE IS NULL OR ORDERSET_CODE = ORDER_CODE)"+
            " AND NS_CHECK_DATE IS NOT NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));

        detailTbl.setParmValue(result);

    }
    /**
     * 展开
     * @return boolean
     */
    public boolean unfoldOrder(){

        //拿到所有挑勾--展开人的caseNo
        TParm patData=masterTbl.getParmValue();
        int rowCount=masterTbl.getRowCount();
        Vector VcaseNo=new Vector();
        for (int i = 0; i < rowCount; i++) {
            String caseNo=(String) patData.getData("CASE_NO", i);
            String matserColum = " STATION_CODE, BED_NO,IPD_NO,MR_NO,CASE_NO ";//CASE_NO
            Timestamp now = TJDODBTool.getInstance().getDBTime();
            String nowString = StringTool.getString(now, "yyyyMMdd") + "0000";
            String EXEsql = " SELECT DISTINCT " + " (SELECT PAT_NAME FROM SYS_PATINFO WHERE SYS_PATINFO.MR_NO=ODI_ORDER.MR_NO) AS PAT_NAME, " +
                            matserColum +
                            " FROM ODI_ORDER WHERE " +
                            (buildWhere().equals("") ? "" : (buildWhere() + " AND ")) +
                            " RX_KIND = 'UD' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') " +
                            " AND NS_CHECK_CODE IS NOT NULL "+
                            " AND (DC_DATE IS NULL OR DC_DATE > TO_DATE('"+ nowString+ "','YYYYMMDDHH24MI') )"+
                            " AND CASE_NO = '"+caseNo+"'"+
                            " AND NS_CHECK_DATE IS NOT NULL "+
                            " ORDER BY MR_NO ";
            TParm parm=new TParm(TJDODBTool.getInstance().select(EXEsql));
            if(parm.getCount() <= 0)
                continue;
            //该行花勾并且不存在与caseNo容器中
            if (TypeTool.getBoolean(masterTbl.getValueAt(i,0))&&VcaseNo.indexOf(caseNo)==-1){
                //一次拿到caseNo
                VcaseNo.add(caseNo);
            }
        }

        //如果没有病患
        if(VcaseNo.size()<=0){
//            this.messageBox("请选中要展开的病人！");
//            this.messageBox("无长期处置展开资料！");
            return true;
        }
        //准备传给后台的数据
        TParm toData=new TParm();
        toData.setData("CASE_NO",VcaseNo);
        //操作人员信息
        toData.setData("OPT_USER",Operator.getID());
        toData.setData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
        toData.setData("OPT_TERM",Operator.getIP());

        //调用action执行事务
        TParm result = TIOM_AppServer.executeAction("action.inw.InwStationMaintainAction",
                                                    "onSave", toData);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        }

        return true;
    }
    /**
     * 通过从界面得到的TParm得到where语句
     * @return String
     */
    public String buildWhere() {
        //得到界面上控件
        TParm queryParm = getQueryParm();
        //从queryParm里解析所有的条件
        StringBuffer where = new StringBuffer();
        //加上caseNo
        String deptCodeTemp = (String) queryParm.getData("DEPT");
        if (!"".equals(deptCodeTemp)) {
            where.append(deptCodeTemp);
        }
        //加上stationCode
        String stationCodeTemp = (String) queryParm.getData("STATION");
        if (!"".equals(stationCodeTemp) && !"".equals(where.toString().trim())) {
            where.append(" AND ");
        }
        where.append(stationCodeTemp);
        //最近展开时间
//        String deteTemp = (String) queryParm.getData("DATE");
//        if (!"".equals(deteTemp) && !"".equals(where.toString().trim())) {
//            where.append(" AND ");
//        }
//        where.append(deteTemp);

//        if (!"".equals(where.toString().trim()))
//            return "WHERE " + where.toString();
        return where.toString();

    }

    /**
     * 获得界面上的所有查询参数
     * @return TParm
     */
    public TParm getQueryParm() {
        //获得界面上的参数
        TParm result = new TParm();
        String deptCode=(String) DEPT.getValue();
        if ("".equals(deptCode)) {
            result.setData("DEPT", "");
        }
        else {
//            result.setData("DEPT", "EXEC_DEPT_CODE = '" + deptCode + "'");
            result.setData("DEPT", "DEPT_CODE = '" + deptCode + "'");
        }

        String station=(String) STATION.getValue();
        if ("".equals(station)) {
            result.setData("STATION", "");
        }
        else {
            result.setData("STATION", "STATION_CODE = '" + station + "'");
        }

        String date = (TypeTool.getString( DATE.getValue())).substring(0,10);

        if ("".equals(date)) {
            result.setData("DATE", "");
        }
        else {
            result.setData("DATE", "ORDER_DATE BETWEEN TO_DATE('" + date + " 000000" +"','yyyy-MM-dd hh24miss') AND TO_DATE('" +date + " 235959" +"','yyyy-MM-dd hh24miss')");
        }

        return result;

    }


    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     * 设置
     */
    public void myInitControler() {
        //得到table控件
        masterTbl = (TTable)this.getComponent("masterTbl");
        detailTbl = (TTable)this.getComponent("detailTbl");

        CHECK1 = (TCheckBox)this.getComponent("CHECK1");
        CHECK2 = (TCheckBox)this.getComponent("CHECK2");
        CHECK3 = (TCheckBox)this.getComponent("CHECK3");
        AllSel = (TCheckBox)this.getComponent("AllSel");

        DEPT = (TTextFormat)this.getComponent("DEPT");
        STATION = (TTextFormat)this.getComponent("STATION");

        DATE = (TTextFormat)this.getComponent("DATE");

        totPerson = (TNumberTextField)this.getComponent("totPerson");

        Timestamp date = TJDODBTool.getInstance().getDBTime();
        DATE.setValue(date);

        //给上下table注册单击事件监听
        this.callFunction("UI|masterTbl|addEventListener",
                          "masterTbl->" + TTableEvent.CLICKED, this,
                          "onMasterTbl");
        //给上下table注册checkbox事件监听
        getTTable("masterTbl").addEventListener(
                        TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");

    }
    public TTable getTTable(String tag) {
            return (TTable) this.getComponent(tag);
    }
    /**
     * table中checkbox事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
//        System.out.println("进入监听");
            TTable masterTbl = (TTable) obj;
            masterTbl.acceptText();
            masterTblParm = masterTbl.getParmValue();
//            System.out.println("+++++++++++"+masterTblParm);
            return true;
    }

    /**
     * 全选所有的数据
     */
    public void onAllSel() {
        int rowCount = masterTbl.getRowCount();
        boolean flg = StringTool.getBoolean(AllSel.getValue());
        for(int i=0;i<rowCount;i++){
            masterTbl.getParmValue().setDataN("EXE",i,AllSel.getValue());
            masterTbl.setValueAt(flg,i,0);
        }
        masterTbl.acceptText();
    }

    /**
     * 清空操作
     */
    public void onClear() {
        masterTbl.setParmValue(new TParm());
        detailTbl.setParmValue(new TParm());
        masterTblParm = new TParm();
        DEPT.setValue("");
        STATION.setValue("");

        CHECK1.setSelected(true); ;
        CHECK2.setSelected(true); ;
        CHECK3.setSelected(true);
        AllSel.setSelected(false);

        totPerson.setValue(0);
    }


    //测试用例
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("inw\\INWStationMaintain.x");

    }


}
