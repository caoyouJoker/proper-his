package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Title: HIS医疗系统</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class ODIPACKOrderControl extends TControl {
    /**
     * 传入参数
     */
    private TParm parmmeter;
    /**
     * 调用系统 ODI 医生站住院 IBS 住院计价
     */
    private String system;
    /**
     * 调用科室
     */
    private String deptCode;
    /**
     * 调用人员
     */
    private String userId;
    /**
     * 权限 0,临时 1，长期 2,出院带药 3,中药 ,4全部
     */
    private int ruleType;
    /**
     * 套餐类别
     */
    private String deptOrdr;

    public void onInitParameter() {
        //测试数据
    }

    /**
     * 初始化参数
     */
    public void onInit(){
//        super.onInit();
        this.parmmeter = new TParm();
        Object obj = this.getParameter();
//        if(obj==null||obj.toString().length()==0)
//            onInitPage();
        if(obj.toString().length()!=0||obj!=null){
            this.parmmeter = (TParm)obj;
        }
        onInitPage();
    }
    /**
     * 初始化页面
     */
    public void onInitPage(){
        //TABLE1单击事件
       callFunction("UI|" + "TABLE1" + "|addEventListener",
                    "TABLE1" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //TABLE2单击事件
       callFunction("UI|" + "TABLE2" + "|addEventListener",
                    "TABLE2" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //TABLE3单击事件
       callFunction("UI|" + "TABLE3" + "|addEventListener",
                    "TABLE3" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //TABLE4单击事件
       callFunction("UI|" + "TABLE4" + "|addEventListener",
                    "TABLE4" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //点击事件
        callFunction("UI|" + "ITEMTABLE" + "|addEventListener",
                    "ITEMTABLE" + "->" + TTableEvent.CLICKED, this,
                    "onTableClickedItem");

        this.setSystem(this.parmmeter.getValue("SYSTEM_TYPE"));
        this.setDeptCode(this.parmmeter.getValue("DEPT_CODE"));
        this.setUserId(this.parmmeter.getValue("USER_ID"));
        this.setRuleType(this.parmmeter.getInt("RULE_TYPE"));
        this.setDeptOrdr(this.parmmeter.getValue("DEPT_OR_DR"));
        if("ODI".equals(this.getSystem())){
            if(getRuleType()==4){
                dbaPage();
            }else{
                normalPage();
            }
        }
        if("IBS".equals(this.getSystem())){
            if(getRuleType()==4){
                dbaPage();
            }else{
                normalPage();
            }
        }
    }

    /**
     * 点击事件
     * @param row int
     */
    public void onTableClicked(int row) {
        this.getTTable("ITEMTABLE").acceptText();
        this.getTTable("ITEMTABLE").clearSelection();
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        String packCode = "";
        switch (tab.getSelectedIndex()) {
            case 0:
                TParm tempST = this.getTTable("TABLE1").getParmValue().getRow(row);
                packCode = tempST.getValue("PACK_CODE");
                break;
            case 1:
                TParm tempUD = this.getTTable("TABLE2").getParmValue().getRow(row);
                packCode = tempUD.getValue("PACK_CODE");
                break;
            case 2:
                TParm tempDS = this.getTTable("TABLE3").getParmValue().getRow(row);
                packCode = tempDS.getValue("PACK_CODE");
                break;
            case 3:
                TParm tempIG = this.getTTable("TABLE4").getParmValue().getRow(row);
                packCode = tempIG.getValue("PACK_CODE");
                break;
        }
        this.onQueryItem(packCode);
    }
    /**
     * 查询套餐细项
     * @param packCode String
     */
    public void onQueryItem(String packCode){
    	//$$================ del by lx 2012/04/14    去掉 重复的规格  start=====================================$$//
    	//B.SPECIFICATION,
        String sql = "SELECT 'Y' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION," +
        		
        		//modify by yangjj 20150529中文名称由取套餐表中的中文改为取SYS_FEE中的中文
        		"B.ORDER_DESC," +
        		//"A.ORDER_DESC," +
        		
        	"A.TRADE_ENG_DESC, "+
            " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT,A.DEPT_OR_DR,A.DEPTORDR_CODE, "+
            " A.ROUTE_CODE,A.FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "+
            " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "+
            " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "+
            " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "+
            " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "+
            " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "+
            " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "+
            " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,PHASE_I_CLINICAL_FLG,BC_INTERVAL_TIME "+ // add by wangb 一期临床注记 2016/6/12
            " FROM ODI_PACK_ORDER A,SYS_FEE B WHERE PACK_CODE='"+packCode+"' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "+
            " ORDER BY A.PACK_CODE,A.SEQ_NO ";
      //$$================ del by lx 2012/04/14    去掉 重复的规格  end=====================================$$//
        //System.out.println(sql);

        TParm parm = new TParm(this.getDBTool().select(sql));
        //System.out.println("======parm====="+parm);
        this.getTTable("ITEMTABLE").setParmValue(parm);
    }
    /**
     * 传回
     */
    public void onSend(){
        this.getTTable("ITEMTABLE").acceptText();
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        String rxKind = getRxKindString(tab.getSelectedIndex());
        TParm parm = this.getTTable("ITEMTABLE").getParmValue();
        int rowCount = parm.getCount();
        List orderList = new ArrayList();
        for(int i=0;i<rowCount;i++){
            TParm temp = parm.getRow(i);
            if("N".equals(temp.getValue("FLG")))
                continue;
            temp.setData("RX_KIND",rxKind);
            orderList.add(temp);
        }
        this.parmmeter.runListener("INSERT_TABLE",orderList);
    }
    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing(){
        TParm parm = new TParm();
        parm.setData("YYLIST","Y");
        this.parmmeter.runListener("INSERT_TABLE_FLG",parm);
        return true;
    }

    /**
     * TABLE点击事件
     * @param row int
     */
    public void onTableClickedItem(int row){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                this.getTTable("TABLE1").acceptText();
                this.getTTable("TABLE1").clearSelection();
                break;
            case 1:
                this.getTTable("TABLE2").acceptText();
                this.getTTable("TABLE2").clearSelection();
                break;
            case 2:
                this.getTTable("TABLE3").acceptText();
                this.getTTable("TABLE3").clearSelection();
                break;
            case 3:
                this.getTTable("TABLE4").acceptText();
                this.getTTable("TABLE4").clearSelection();
                break;
        }
    }

    public void onChange(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                //重新设置TABLE
                this.getTTable("ITEMTABLE").setHeader("传,30,boolean;连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;频次,90,FREQ_CODE;规格,180;备注,200");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3,4,5,6,7,8,9");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.onQuery(0);
                break;
            case 1:
                this.getTTable("ITEMTABLE").setHeader("传,30,boolean;连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;频次,90,FREQ_CODE;规格,180;备注,200");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3,4,5,6,7,8,9");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.onQuery(1);
                break;
            case 2:
                this.getTTable("ITEMTABLE").setHeader("传,30,boolean;连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;天数,40,int;频次,90,FREQ_CODE;规格,180;备注,200");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3,4,5,6,7,8,9,10");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;TAKE_DAYS;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.onQuery(2);
                break;
            case 3:
                this.getTTable("ITEMTABLE").setHeader("传,30,boolean;医嘱名称,120;用量,100,double,#########0.000;特殊煎法,100,DCTAGENT_COMBO");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left;");
                this.getTTable("ITEMTABLE").setParmMap("FLG;ORDER_DESC;MEDI_QTY;DCTEXCEP_CODE");
                this.onQuery(3);
                break;
        }

    }
    /**
     * 普通查询
     */
    public void normalPage(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (this.getRuleType()) {
            case 0:
                tab.setEnabledAt(1,false);
                tab.setEnabledAt(2,false);
                tab.setEnabledAt(3,false);
                tab.setSelectedIndex(0);
                this.onQuery(0);
                return;
            case 1:
                tab.setEnabledAt(0,false);
                tab.setEnabledAt(2,false);
                tab.setEnabledAt(3,false);
                tab.setSelectedIndex(1);
                break;
            case 2:
                tab.setEnabledAt(0,false);
                tab.setEnabledAt(1,false);
                tab.setEnabledAt(3,false);
                tab.setSelectedIndex(2);
                break;
            case 3:
                tab.setEnabledAt(0,false);
                tab.setEnabledAt(1,false);
                tab.setEnabledAt(2,false);
                tab.setSelectedIndex(3);
                break;
        }
        //查询
//        this.onQuery(tab.getSelectedIndex());
    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 拿到科室|人员
     * @param deptOrDr String
     * @return String
     */
    public String getDeptOrDrCodeString(String deptOrDr){
        String deptOrDrCode = "";
        if("1".equals(deptOrDr)||"3".equals(deptOrDr)){
            deptOrDrCode =this.getDeptCode();
        }
        if("2".equals(deptOrDr)||"4".equals(deptOrDr)){
            deptOrDrCode = this.getUserId();
        }
        return deptOrDrCode;
    }
    /**
     * 全选
     */
    public void onSelAll(){
        this.getTTable("ITEMTABLE").acceptText();
        TParm parm = this.getTTable("ITEMTABLE").getParmValue();
        int rowCount = parm.getCount();
        for(int i=0;i<rowCount;i++){
            if(parm.getBoolean("FLG",i)){
                parm.setData("FLG",i,false);
            }else{
                parm.setData("FLG",i,true);
            }
        }
        this.getTTable("ITEMTABLE").setParmValue(parm);
    }
    /**
     * 查询
     */
    public void onQuery(int type){
        String sql = "";
        if("1".equals(this.getDeptOrdr())||"3".equals(this.getDeptOrdr())){
            sql = "SELECT A.PACK_CODE,A.DEPT_OR_DR,B.DEPT_CHN_DESC AS DEPTUSER,A.RX_KIND,A.PACK_DESC,A.GFREQ_CODE,A.GROUTE_CODE,A.GDCTAGENT_CODE,A.OPT_USER,A.OPT_TERM,A.OPT_DATE FROM ODI_PACK_MAIN A,SYS_DEPT B  WHERE A.DEPT_OR_DR='"+this.getDeptOrdr()+"' AND A.DEPTORDR_CODE='"+getDeptOrDrCodeString(this.getDeptOrdr())+"' AND A.RX_KIND='"+getRxKindString(type)+"' AND A.DEPTORDR_CODE=B.DEPT_CODE";
        }
        if("2".equals(this.getDeptOrdr())||"4".equals(this.getDeptOrdr())){
            sql = "SELECT A.PACK_CODE,A.DEPT_OR_DR,B.USER_NAME AS DEPTUSER,A.RX_KIND,A.PACK_DESC,A.GFREQ_CODE,A.GROUTE_CODE,A.GDCTAGENT_CODE,A.OPT_USER,A.OPT_TERM,A.OPT_DATE FROM ODI_PACK_MAIN A,SYS_OPERATOR B  WHERE A.DEPT_OR_DR='"+this.getDeptOrdr()+"' AND A.DEPTORDR_CODE='"+getDeptOrDrCodeString(this.getDeptOrdr())+"' AND A.RX_KIND='"+getRxKindString(type)+"' AND A.DEPTORDR_CODE=B.USER_ID";
        }
        TParm parm = new TParm(this.getDBTool().select(sql));
        this.getTTable("TABLE"+(type+1)).setParmValue(parm);
    }
    public String getRxKindString(int type){
        String rxKind = "ST";
        switch (type) {
            case 0:
                rxKind = "ST";
                break;
            case 1:
                rxKind = "UD";
                break;
            case 2:
                rxKind = "DS";
                break;
            case 3:
                rxKind = "IG";
                break;
        }
        return rxKind;

    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 普通查询
     */
    public void dbaPage(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        onQuery(tab.getSelectedIndex());
    }
    public String getSystem() {
        return system;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public int getRuleType() {
        return ruleType;
    }

    public String getDeptOrdr() {
        return deptOrdr;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public void setDeptOrdr(String deptOrdr) {
        this.deptOrdr = deptOrdr;
    }
}
