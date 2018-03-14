package com.javahis.ui.odi;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.odi.ODIPACKMAINDataStore;
import jdo.odi.ODIPACKORDERDataStore;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 住院医生站套餐</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author WangM
 * @version 1.0
 */
public class ODIPackBackControl extends TControl {
    /**
     * 医嘱类别
     */
    private String rxKind="ST";
    /**
     * 当前编辑的套餐
     */
    private String packCodeOnly="";
    /**
     * 当前的也签
     */
    private int pageIndex;
    /**
     * 临时用药预设频次
     */
    private String odiUddStatCode;
    /**
     * 临时处置预设频次
     */
    private String odiStatCode;
    /**
     * 长期处置预设频次
     */
    private String odiDefaFreg;
    /**
     * 预设饮片付数
     */
    private String dctTakeDays;
    /**
     * 预设饮片使用计量
     */
    private String dctTakeQty;
    /**
     * 中药常用频次，默认一天两次
     */
    private String gFreqCode;
    /**
     * 中药常用用法，口服
     */
    private String gRouteCode;
    /**
     * 煎法
     */
    private String gDctagentCode;
    /**
     * 中医的数据
     */
    private ODIPACKORDERDataStore igOrderData;
    /**
     *  中医当前编辑行
     */
    private int rowOnly=-1;

    /**
     * 动作类名称
     */
    private String actionName = "action.odi.ODIAction";
    /**
     * 权限
     */
    private String rule;

    /**
     * 初始化参数
     */
    public void onInitParameter(){
        //测试数据
//        this.setPopedem("deptEnabled",true);
//        this.setPopedem("operatorEnabled",true);

    }

    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
//        if(obj==null||obj.toString().length()==0){
//            this.messageBox("请先设置权限");
//            this.callFunction("UI|new|setEnabled", false);
//            this.callFunction("UI|save|setEnabled", false);
//            this.callFunction("UI|delete|setEnabled", false);
//            return;
//        }
        rule = (String)this.getParameter();
//        this.rule = "ODI";
        onInitPage();
        onInitPopeDem();
    }
    /**
     * 套餐类别下拉事件
     */
    public void onSelPackType(){
        TComboBox packType = (TComboBox)this.getComponent("DEPT_OR_DR");
        String id = packType.getSelectedID();
        if("ODI".equals(this.rule)){
            if("3".equals(id)||"4".equals(id)){
                this.messageBox("不可以编辑！");
                this.setValue("DEPT_OR_DR","");
                return;
            }
        }
        if("IBS".equals(this.rule)){
            if("1".equals(id)||"2".equals(id)){
                this.messageBox("不可以编辑！");
                this.setValue("DEPT_OR_DR","");
                return;
            }
        }
        if(this.getPopedem("deptEnabled")){
            if ("1".equals(id) || "3".equals(id)) {
                TextFormatDept dept = (TextFormatDept)this.getComponent(
                    "DEPT_CODE");
                TextFormatSYSOperator user = (TextFormatSYSOperator)this.
                    getComponent("USER_ID");
                if (user.isEnabled()) {
                    user.setEnabled(false);
                }
                if (!dept.isEnabled()) {
                    dept.setEnabled(true);
                }
            }
        }
        if (this.getPopedem("operatorEnabled")){
            if ("2".equals(id) || "4".equals(id)) {
                TextFormatDept dept = (TextFormatDept)this.getComponent(
                    "DEPT_CODE");
                TextFormatSYSOperator user = (TextFormatSYSOperator)this.
                    getComponent("USER_ID");
                if (dept.isEnabled()) {
                    dept.setEnabled(false);
                }
                if (!user.isEnabled()) {
                    user.setEnabled(true);
                }
            }
        }
    }
    /**
     * 页签改变事件
     */
    public void onChange(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                this.rxKind="ST";
                if(pageIndex!=0){
                    if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
                        if (messageBox("提示信息", "是否保存?", this.YES_NO_OPTION) !=
                            0) {
                            tab.setSelectedIndex(pageIndex);
                            return;
                        }
                        else {
                            this.onSave();
                        }
                    }
                }
                this.packCodeOnly="";
                pageIndex = 0;
                //重新设置TABLE
                this.getTTable("ITEMTABLE").setHeader("连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;频次,90,FREQ_CODE;规格,180;备注,200");
                this.getTTable("ITEMTABLE").setFocusIndexList("2,3,5,6");
                this.getTTable("ITEMTABLE").setLockColumns("4,7");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.getTTable("ITEMTABLE").setAutoModifyDataStore(true);
                ODIPACKORDERDataStore dsSt = new ODIPACKORDERDataStore();
                dsSt.setPackCode(this.packCodeOnly);
                dsSt.onQuery();
                this.getTTable("ITEMTABLE").setDataStore(dsSt);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
            case 1:
                this.rxKind="UD";
                if(pageIndex!=1){
                    if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
                        if (messageBox("提示信息", "是否保存?", this.YES_NO_OPTION) !=
                            0) {
                            tab.setSelectedIndex(pageIndex);
                            return;
                        }
                        else {
                            this.onSave();
                        }
                    }
                }
                this.packCodeOnly="";
                pageIndex = 1;
                this.getTTable("ITEMTABLE").setHeader("连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;频次,90,FREQ_CODE;规格,180;备注,200");
                this.getTTable("ITEMTABLE").setFocusIndexList("2,3,5,6");
                this.getTTable("ITEMTABLE").setLockColumns("4,7");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.getTTable("ITEMTABLE").setAutoModifyDataStore(true);
                ODIPACKORDERDataStore dsUd = new ODIPACKORDERDataStore();
                dsUd.setPackCode(this.packCodeOnly);
                dsUd.onQuery();
                this.getTTable("ITEMTABLE").setDataStore(dsUd);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
            case 2:
                this.rxKind="DS";
                if(pageIndex!=2){
                    if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
                        if (messageBox("提示信息", "是否保存?", this.YES_NO_OPTION) !=
                            0) {
                            tab.setSelectedIndex(pageIndex);
                            return;
                        }
                        else {
                            this.onSave();
                        }
                    }
                }
                this.packCodeOnly="";
                pageIndex = 2;
                this.getTTable("ITEMTABLE").setHeader("连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;天数,40,int;频次,90,FREQ_CODE;规格,180;备注,200");
                this.getTTable("ITEMTABLE").setFocusIndexList("2,3,5,6,7");
                this.getTTable("ITEMTABLE").setLockColumns("4,8");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;TAKE_DAYS;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.getTTable("ITEMTABLE").setAutoModifyDataStore(true);
                ODIPACKORDERDataStore dsDs = new ODIPACKORDERDataStore();
                dsDs.setPackCode(this.packCodeOnly);
                dsDs.onQuery();
                this.getTTable("ITEMTABLE").setDataStore(dsDs);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
            case 3:
                this.rxKind="IG";
                if(pageIndex!=3){
                    if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
                        if (messageBox("提示信息", "是否保存?", this.YES_NO_OPTION) !=
                            0) {
                            tab.setSelectedIndex(pageIndex);
                            return;
                        }
                        else {
                            this.onSave();
                        }
                }
                }
                this.packCodeOnly="";
                pageIndex = 3;
                this.getTTable("ITEMTABLE").setHeader("医嘱名称,120;用量,100,double,#########0.000;特殊煎法,100,DCTAGENT_COMBO;医嘱名称,120;用量,100,double,#########0.000;特殊煎法,100,DCTAGENT_COMBO;医嘱名称,120;用量,100,double,#########0.000;特殊煎法,100,DCTAGENT_COMBO;医嘱名称,120;用量,100,double,#########0.000;特殊煎法,100,DCTAGENT_COMBO");
                this.getTTable("ITEMTABLE").setFocusIndexList("0,3,6,9");
                this.getTTable("ITEMTABLE").setLockColumns("");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left;");
                this.getTTable("ITEMTABLE").setParmMap("ORDER_DESC_1;MEDI_QTY_1;DCTEXCEP_CODE_1;ORDER_DESC_2;MEDI_QTY_2;DCTEXCEP_CODE_2;ORDER_DESC_3;MEDI_QTY_3;DCTEXCEP_CODE_3;ORDER_DESC_4;MEDI_QTY_4;DCTEXCEP_CODE_4");
                this.getTTable("ITEMTABLE").setAutoModifyDataStore(false);
                ODIPACKORDERDataStore dsIg = new ODIPACKORDERDataStore();
                dsIg.setPackCode(this.packCodeOnly);
                dsIg.onQuery();
                this.getTTable("ITEMTABLE").setDataStore(dsIg);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
        }
    }
    /**
     * 拿到TABLE类型
     * @param type int
     * @return String
     */
    public String getTableTag(int type){
        if(0==type){
            return "ST";
        }
        if(1==type){
            return "UD";
        }
        if(2==type){
            return "DS";
        }
        if(3==type){
            return "IG";
        }
        return "ST";
    }
    /**
     * 初始化界面
     */
    public void onInitPage(){
       this.setValue("DEPT_CODE",Operator.getDept());
       this.setValue("USER_ID",Operator.getID());
//       TTextField QUERY = (TTextField)this.getComponent("PACK_DESC");
//       TParm parm = new TParm();
//       parm.setData("CAT1_TYPE", "");
//       QUERY.setPopupMenuParameter("QUERYPACK", getConfigParm().newConfig(
//            "%ROOT%\\config\\odi\\ODIPackOrderListPopup.x"), parm);
//       // 定义接受返回值方法
//       QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
       //临时TABLE值改变监听
       addEventListener("TABLE1" + "->" + TTableEvent.CHANGE_VALUE, this,
                        "onChangeTableValueST");
       //长期TABLE值改变监听
       addEventListener("TABLE2" + "->" + TTableEvent.CHANGE_VALUE, this,
                        "onChangeTableValueUD");
       //出院带药TABLE值改变监听
       addEventListener("TABLE3" + "->" + TTableEvent.CHANGE_VALUE, this,
                        "onChangeTableValueDS");
       //中药饮片TABLE值改变监听
       addEventListener("TABLE4" + "->" + TTableEvent.CHANGE_VALUE, this,
                        "onChangeTableValueIG");
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
       //临时TABLE1监听事件
        getTTable("ITEMTABLE").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                         "onCreateEditComoponent");
        //连接医嘱监听事件
        getTTable("ITEMTABLE").addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
        //ORDERETABLE值改变监听
        addEventListener("ITEMTABLE" + "->" + TTableEvent.CHANGE_VALUE, this,
                       "onChangeTableValue");
        //点击事件
        callFunction("UI|" + "ITEMTABLE" + "|addEventListener",
                    "ITEMTABLE" + "->" + TTableEvent.CLICKED, this,
                    "onTableClickedItem");

        //当前的也签
        this.pageIndex = 0;
        //临时用药预设频次
        this.setOdiUddStatCode(getOdiSysParmData("UDD_STAT_CODE").toString());
        //临时处置预设频次
        this.setOdiStatCode(getOdiSysParmData("ODI_STAT_CODE").toString());
        //长期处置预设频次
        this.setOdiDefaFreg(getOdiSysParmData("ODI_DEFA_FREG").toString());
        //预设饮片付数
        this.setDctTakeDays(getOpdSysParmData("DCT_TAKE_DAYS").toString());
        //预设饮片使用计量
        this.setDctTakeQty(getOpdSysParmData("DCT_TAKE_QTY").toString());
        //中药常用频次，默认一天两次
        this.setGFreqCode(getOpdSysParmData("G_FREQ_CODE").toString());
        //中药常用用法，口服
        this.setGRouteCode(getOpdSysParmData("G_ROUTE_CODE").toString());
        //中医常用，煎法
        this.setGDctagentCode(getOpdSysParmData("G_DCTAGENT_CODE").toString());
    }
    /**
     * 得到住院参数
     * @param key String
     * @return Object
     */
    public Object getOdiSysParmData(String key){
        return OdiMainTool.getInstance().getOdiSysParmData(key);
    }
    /**
     * 得到门诊参数
     * @param key String
     * @return Object
     */
    public Object getOpdSysParmData(String key){
        return OdiMainTool.getInstance().getOpdSysParmData(key);
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
    /**
     *ORDER值改变事件
     */
    public void onCheckBoxValue(Object obj){
        TTable table = (TTable)obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = this.getTTable("ITEMTABLE").getDataStoreColumnName(col);
        int row = table.getSelectedRow();
        TParm linkParm = table.getDataStore().getRowParm(row);
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                //连接医嘱
                if("LINKMAIN_FLG".equals(columnName)){
                    if("Y".equals(linkParm.getValue("LINKMAIN_FLG"))){
                        if (linkParm.getValue("ORDER_CODE").length() == 0) {
                            //请开立医嘱
                            this.messageBox("请开立医嘱");
                            table.getDataStore().setItem(row,"LINKMAIN_FLG","N");
                            table.setDSValue();
                            return;
                        }
                        //查询最大连结号
                        int maxLinkNo = getMaxLinkNo();
                        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
                        ds.setItem(row,"LINK_NO",maxLinkNo);
                        table.setDSValue(row);
                    }else{
                        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
                        ds.setItem(row,"LINK_NO","");
                        if("PHA".equals(linkParm.getValue("CAT1_TYPE"))){
                            //得到PHA_BASE数据
                            TParm action = new TParm();
                            action.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
                            TParm result = OdiMainTool.getInstance().queryPhaBase(action);
                            ds.setItem(row,"ROUTE_CODE",result.getValue("ROUTE_CODE",0));
                            ds.setItem(row,"FREQ_CODE",getOdiUddStatCode());
                        }else{
                            ds.setItem(row,"ROUTE_CODE","");
                            ds.setItem(row,"FREQ_CODE",getOdiStatCode());
                        }
                        table.setDSValue(row);
                    }
                }
                break;
            case 1:
                //连接医嘱
                if("LINKMAIN_FLG".equals(columnName)){
                    if("Y".equals(linkParm.getValue("LINKMAIN_FLG"))){
                        if (linkParm.getValue("ORDER_CODE").length() == 0) {
                            //请开立医嘱
                            this.messageBox("请开立医嘱");
                            table.getDataStore().setItem(row,"LINKMAIN_FLG","N");
                            table.setDSValue();
                            return;
                        }
                        //查询最大连结号
                        int maxLinkNo = getMaxLinkNo();
                        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
                        ds.setItem(row,"LINK_NO",maxLinkNo);
                        table.setDSValue(row);
                    }else{
                        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
                        ds.setItem(row,"LINK_NO","");
                        if("PHA".equals(linkParm.getValue("CAT1_TYPE"))){
                            //得到PHA_BASE数据
                            TParm action = new TParm();
                            action.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
                            TParm result = OdiMainTool.getInstance().queryPhaBase(action);
                            ds.setItem(row,"ROUTE_CODE",result.getValue("ROUTE_CODE",0));
                            ds.setItem(row,"FREQ_CODE",result.getValue("FREQ_CODE",0));
                        }else{
                            ds.setItem(row,"ROUTE_CODE","");
                            ds.setItem(row,"FREQ_CODE",getOdiDefaFreg());
                        }
                        table.setDSValue(row);
                    }
                }
                break;
            case 2:
                //连接医嘱
                if("LINKMAIN_FLG".equals(columnName)){
                    if("Y".equals(linkParm.getValue("LINKMAIN_FLG"))){
                        if (linkParm.getValue("ORDER_CODE").length() == 0) {
                            //请开立医嘱
                            this.messageBox("请开立医嘱");
                            table.getDataStore().setItem(row,"LINKMAIN_FLG","N");
                            table.setDSValue();
                            return;
                        }
                        //查询最大连结号
                        int maxLinkNo = getMaxLinkNo();
                        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
                        ds.setItem(row,"LINK_NO",maxLinkNo);
                        table.setDSValue(row);
                    }else{
                        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
                        ds.setItem(row,"LINK_NO","");
                        if("PHA".equals(linkParm.getValue("CAT1_TYPE"))){
                            //得到PHA_BASE数据
                            TParm action = new TParm();
                            action.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
                            TParm result = OdiMainTool.getInstance().queryPhaBase(action);
                            ds.setItem(row,"ROUTE_CODE",result.getValue("ROUTE_CODE",0));
                            ds.setItem(row,"FREQ_CODE",result.getValue("FREQ_CODE",0));
                        }else{
                            ds.setItem(row,"ROUTE_CODE","");
                            ds.setItem(row,"FREQ_CODE",getOdiDefaFreg());
                        }
                        table.setDSValue(row);
                    }
                }
                break;
            case 3:
                //预留
                break;
        }
    }
    /**
     * 拿到最大连结号
     * @param table TTable
     */
    public int getMaxLinkNo(){
        ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
        String buff = ds.isFilter()?ds.FILTER:ds.PRIMARY;
        TParm parm = ds.getBuffer(buff);
        int result=0;
        for(int i=0;i<parm.getCount();i++){
            if(!parm.getBoolean("#ACTIVE#",i))
                continue;
            if(parm.getRow(i).getInt("LINK_NO")>result)
                result=parm.getRow(i).getInt("LINK_NO");
        }
//        this.messageBox("返回值:"+result);
        return result==0?1:result+1;
    }

    /**
     * 返回实际列名
     * @param column String
     * @param column int
     * @return String
     */
    public String getFactColumnName(String tableTag,int column){
        int col = this.getThisColumnIndex(column,tableTag);
        return this.getTTable(tableTag).getDataStoreColumnName(col);
    }
    /**
     * 拿到更变之前的列号
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column,String table){
        return this.getTTable(table).getColumnModel().getColumnIndex(column);
    }
    /**
     * 当TABLE创建编辑控件时临时
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        //拿到列名
        String columnName = this.getFactColumnName("ITEMTABLE",column);
        if(!columnName.contains("ORDER_DESC"))
            return;
        if(!(com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        TParm parm = new TParm();
        //临时医嘱设置
        if(tab.getSelectedIndex()!=3){
            parm.setData("ODI_ORDER_TYPE", "A");
        }else{
            String columnArray[] = columnName.split("_");
            TParm columnParm = this.getTTable("ITEMTABLE").getParmValue().getRow(row);
            this.rowOnly = getIGEditRowSetId(columnParm.getInt("ROW_ID_"+columnArray[columnArray.length-1]));
            parm.setData("ODI_ORDER_TYPE", "F");
        }
        //设置弹出菜单
        textFilter.setPopupMenuParameter("ITEM",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),parm);
        //定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    }
    /**
     * 得到住院中医当前编辑行
     * @param row int
     * @return int
     */
    public int getIGEditRowSetId(int row){
        int rowId = -1;
        igOrderData.showDebug();
        int rowCount = igOrderData.rowCount();
        for(int i=0;i<rowCount;i++){
            int rowSet = igOrderData.getItemInt(i,"#ID#");
            if(row==rowSet){
                rowId=i;
                break;
            }
        }
        return rowId;
    }

    /**
     * 返回值事件
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag,Object obj){
        if (obj == null && ! (obj instanceof TParm)) {
            return;
        }
        TParm parm = (TParm)obj;
        if("QUERYPACK".equals(tag)){
            return;
        }
        if("ITEM".equals(tag)){
            this.getTTable("ITEMTABLE").acceptText();
            int selRow = this.getTTable("ITEMTABLE").getSelectedRow();
            //插入行
            onInsertOrderList(selRow,parm);
        }
    }
    /**
     * 插入细项
     * @param type int
     */
    public void onInsertOrderList(int row,TParm parm){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        ODIPACKORDERDataStore orderds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
        String orderCode = parm.getValue("ORDER_CODE");
        if(tab.getSelectedIndex()!=3){
            if(this.isSame(orderds,orderCode,row)){
                if (messageBox("提示信息", "有相同医嘱是否开立此医嘱?", this.YES_NO_OPTION) != 0) {
                    orderds.setItem(row,"ORDER_DESC","");
                    this.getTTable("ITEMTABLE").setDSValue(row);
                    return;
                }else{
                    insertPackOrder(row,parm);
                }
            }else{
                insertPackOrder(row,parm);
            }
        }else{
            if(this.isSame(this.igOrderData,orderCode,this.rowOnly)){
                if (messageBox("提示信息", "有相同医嘱是否开立此医嘱?", this.YES_NO_OPTION) != 0) {
                    return;
                }else{
                    insertPackOrder(row,parm);
                }
            }else{
                insertPackOrder(row,parm);
            }
        }
    }
    /**
     * 插入细项
     * @param row int
     * @param parm TParm
     */
    public void insertPackOrder(int row,TParm parm){
        //草药选中列
        int columnIG=-1;
        //草药选中行
        int rowIG = -1;
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        ODIPACKORDERDataStore orderds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
        switch (tab.getSelectedIndex()) {
            case 0:
                if (row > 0) {
                    TParm temp = orderds.getRowParm(row - 1);
                    if (temp.getValue("LINK_NO").length() != 0&&"PHA".equals(parm.getValue("CAT1_TYPE"))) {
                        orderds.setItem(row, "LINK_NO", temp.getData("LINK_NO"));
                    }
                    else {
                        orderds.setItem(row, "LINK_NO", "");
                    }
                }
                orderds.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
                orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
                orderds.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
                orderds.setItem(row, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
                orderds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
                orderds.setItem(row, "TRADE_ENG_DESC",
                                parm.getData("TRADE_ENG_DESC"));
                orderds.setItem(row, "SPECIFICATION",
                                parm.getData("SPECIFICATION"));
                orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
                if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
                    //得到PHA_BASE数据
                    TParm action = new TParm();
                    action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
                    TParm result = OdiMainTool.getInstance().queryPhaBase(
                        action);
                    orderds.setItem(row, "MEDI_QTY",
                                    result.getData("MEDI_QTY", 0));
                    orderds.setItem(row, "MEDI_UNIT",
                                    result.getData("MEDI_UNIT", 0));
                    if (row > 0) {
                        TParm temp = orderds.getRowParm(row - 1);
                        if (temp.getValue("LINK_NO").length() != 0) {
                            orderds.setItem(row, "ROUTE_CODE",
                                            temp.getValue("ROUTE_CODE"));
                            orderds.setItem(row, "FREQ_CODE",
                                            temp.getValue("FREQ_CODE"));
                        }else{
                            orderds.setItem(row, "ROUTE_CODE",
                                            result.getData("ROUTE_CODE", 0));
                            orderds.setItem(row, "FREQ_CODE",
                                            this.getOdiUddStatCode());
                        }
                    }else{
                        orderds.setItem(row, "ROUTE_CODE",
                                        result.getData("ROUTE_CODE", 0));
                        orderds.setItem(row, "FREQ_CODE",
                                        this.getOdiUddStatCode());
                    }
                }
                else {
                    orderds.setItem(row, "MEDI_QTY", 1);
                    orderds.setItem(row, "MEDI_UNIT", parm.getData("UNIT_CODE"));
                    orderds.setItem(row, "ROUTE_CODE", "");
                    orderds.setItem(row, "FREQ_CODE", getOdiStatCode());
                }
                orderds.setActive(row, true);
                break;
            case 1:
                if("LIS".equals(parm.getValue("CAT1_TYPE"))||"RIS".equals(parm.getValue("CAT1_TYPE"))){
                    this.messageBox("长期医嘱不可开立");
                    orderds.setItem(row, "ORDER_DESC", "");
                    this.getTTable("ITEMTABLE").setDSValue(row);
                    return;
                }
                if (row > 0) {
                    TParm temp = orderds.getRowParm(row - 1);
                    if (temp.getValue("LINK_NO").length() != 0&"PHA".equals(parm.getValue("CAT1_TYPE"))) {
                        orderds.setItem(row, "LINK_NO", temp.getData("LINK_NO"));
                    }
                    else {
                        orderds.setItem(row, "LINK_NO", "");
                    }
                }
                orderds.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
                orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
                orderds.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
                orderds.setItem(row, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
                orderds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
                orderds.setItem(row, "TRADE_ENG_DESC",
                                parm.getData("TRADE_ENG_DESC"));
                orderds.setItem(row, "SPECIFICATION",
                                parm.getData("SPECIFICATION"));
                orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
                if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
                    //得到PHA_BASE数据
                    TParm action = new TParm();
                    action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
                    TParm result = OdiMainTool.getInstance().queryPhaBase(
                        action);
                    orderds.setItem(row, "MEDI_QTY",
                                    result.getData("MEDI_QTY", 0));
                    orderds.setItem(row, "MEDI_UNIT",
                                    result.getData("MEDI_UNIT", 0));
                    if (row > 0) {
                        TParm temp = orderds.getRowParm(row - 1);
                        if (temp.getValue("LINK_NO").length() != 0) {
                            orderds.setItem(row, "ROUTE_CODE",
                                            temp.getValue("ROUTE_CODE"));
                            orderds.setItem(row, "FREQ_CODE",
                                            temp.getValue("FREQ_CODE"));
                        }else{
                            orderds.setItem(row, "ROUTE_CODE",
                                            result.getData("ROUTE_CODE", 0));
                            orderds.setItem(row, "FREQ_CODE",
                                            this.getOdiUddStatCode());
                        }
                    }else{
                        orderds.setItem(row, "ROUTE_CODE",
                                        result.getData("ROUTE_CODE", 0));
                        orderds.setItem(row, "FREQ_CODE",
                                        this.getOdiUddStatCode());
                    }
                }
                else {
                    orderds.setItem(row, "MEDI_QTY", 1);
                    orderds.setItem(row, "MEDI_UNIT", parm.getData("UNIT_CODE"));
                    orderds.setItem(row, "ROUTE_CODE", "");
                    orderds.setItem(row, "FREQ_CODE", this.getOdiDefaFreg());
                }
                orderds.setActive(row, true);
                break;
            case 2:
                if (!"PHA".equals(parm.getValue("CAT1_TYPE"))) {
                    this.messageBox("出院带药不可开立");
                    orderds.setItem(row, "ORDER_DESC", "");
                    this.getTTable("ITEMTABLE").setDSValue(row);
                    return;
                }
                if (row > 0) {
                    TParm temp = orderds.getRowParm(row - 1);
                    if (temp.getValue("LINK_NO").length() != 0) {
                        orderds.setItem(row, "LINK_NO", temp.getData("LINK_NO"));
                    }
                    else {
                        orderds.setItem(row, "LINK_NO", "");
                    }
                }
                orderds.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
                orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
                orderds.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
                orderds.setItem(row, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
                orderds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
                orderds.setItem(row, "TRADE_ENG_DESC",
                                parm.getData("TRADE_ENG_DESC"));
                orderds.setItem(row, "SPECIFICATION",
                                parm.getData("SPECIFICATION"));
                orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
                orderds.setItem(row, "TAKE_DAYS", 1);
                //得到PHA_BASE数据
                TParm actionDs = new TParm();
                actionDs.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
                TParm resultDs = OdiMainTool.getInstance().queryPhaBase(
                    actionDs);
                orderds.setItem(row, "MEDI_QTY",
                                resultDs.getData("MEDI_QTY", 0));
                orderds.setItem(row, "MEDI_UNIT",
                                resultDs.getData("MEDI_UNIT", 0));
                if (row > 0) {
                    TParm temp = orderds.getRowParm(row - 1);
                    if (temp.getValue("LINK_NO").length() != 0) {
                        orderds.setItem(row, "ROUTE_CODE",
                                        temp.getValue("ROUTE_CODE"));
                        orderds.setItem(row, "FREQ_CODE",
                                        temp.getValue("FREQ_CODE"));
                    }
                    else {
                        orderds.setItem(row, "ROUTE_CODE",
                                        resultDs.getData("ROUTE_CODE", 0));
                        orderds.setItem(row, "FREQ_CODE",
                                        this.getOdiUddStatCode());
                    }
                }
                else {
                    orderds.setItem(row, "ROUTE_CODE",
                                    resultDs.getData("ROUTE_CODE", 0));
                    orderds.setItem(row, "FREQ_CODE",
                                    this.getOdiUddStatCode());
                }
                orderds.setActive(row, true);
                break;
            case 3:
                this.igOrderData.setItem(this.rowOnly, "ORDER_CODE", parm.getData("ORDER_CODE"));
                this.igOrderData.setItem(this.rowOnly, "DESCRIPTION", parm.getData("DESCRIPTION"));
                this.igOrderData.setItem(this.rowOnly, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
                this.igOrderData.setItem(this.rowOnly, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
                this.igOrderData.setItem(this.rowOnly, "ORDER_DESC", parm.getData("ORDER_DESC"));
                this.igOrderData.setItem(this.rowOnly, "TRADE_ENG_DESC",
                                parm.getData("TRADE_ENG_DESC"));
                this.igOrderData.setItem(this.rowOnly, "SPECIFICATION",
                                parm.getData("SPECIFICATION"));
                this.igOrderData.setItem(this.rowOnly, "DESCRIPTION", parm.getData("DESCRIPTION"));
                //得到PHA_BASE数据
                TParm actionIG = new TParm();
                actionIG.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
                TParm resultIG = OdiMainTool.getInstance().queryPhaBase(
                    actionIG);
                this.igOrderData.setItem(this.rowOnly, "MEDI_QTY",
                                this.getDctTakeQty());
                this.igOrderData.setItem(this.rowOnly, "MEDI_UNIT",
                                resultIG.getData("MEDI_UNIT", 0));
                this.igOrderData.setItem(this.rowOnly, "DCTEXCEP_CODE","");
                this.igOrderData.setActive(rowOnly, true);
                columnIG = this.getTTable("ITEMTABLE").getSelectedColumn();
                rowIG = this.getTTable("ITEMTABLE").getSelectedRow();
                break;
        }
        //是否有未编辑的医嘱
        if(!isNew(orderds)&&tab.getSelectedIndex()!=3){
            int insRow = orderds.insertRow();
            orderds.setItem(insRow,"PACK_CODE",orderds.getItemData(row,"PACK_CODE"));
            orderds.setItem(insRow,"DEPT_OR_DR",orderds.getItemData(row,"DEPT_OR_DR"));
            orderds.setItem(insRow,"DEPTORDR_CODE",orderds.getItemData(row,"DEPTORDR_CODE"));
            orderds.setActive(insRow,false);
            this.getTTable("ITEMTABLE").setDSValue();
            this.getTTable("ITEMTABLE").getTable().grabFocus();
            this.getTTable("ITEMTABLE").setSelectedRow(row);
            this.getTTable("ITEMTABLE").setSelectedColumn(3);
        }
        if(tab.getSelectedIndex()!=3){
            this.getTTable("ITEMTABLE").setDSValue();
            this.getTTable("ITEMTABLE").getTable().grabFocus();
            this.getTTable("ITEMTABLE").setSelectedRow(row);
            this.getTTable("ITEMTABLE").setSelectedColumn(3);
        }else{
            TParm igD = new TParm();
            igD.setData("PACK_CODE",this.igOrderData.getItemData(this.rowOnly,"PACK_CODE"));
            igD.setData("DEPT_OR_DR",this.igOrderData.getItemData(this.rowOnly,"DEPT_OR_DR"));
            igD.setData("DEPTORDR_CODE",this.igOrderData.getItemData(this.rowOnly,"DEPTORDR_CODE"));
            filter(this.getTTable("ITEMTABLE"),igOrderData,igD);
            this.getTTable("ITEMTABLE").getTable().grabFocus();
            String columnName = this.getFactColumnName("ITEMTABLE",columnIG);
            this.getTTable("ITEMTABLE").setSelectedRow(rowIG);
            this.getTTable("ITEMTABLE").setSelectedColumn(querySetIGFocusColumn(columnName));
            igOrderData.showDebug();
        }
    }
    /**
     * 焦点放置位置
     * @return int
     */
    public int querySetIGFocusColumn(String columnName){
        int row = -1;
        String columnArray[] = columnName.split("_");
        if(columnArray[columnArray.length-1].equals("1")){
            row = 1;
        }
        if(columnArray[columnArray.length-1].equals("2")){
            row = 4;
        }
        if(columnArray[columnArray.length-1].equals("3")){
            row = 7;
        }
        if(columnArray[columnArray.length-1].equals("4")){
            row = 10;
        }
        return row;
    }
    /**
     * 查看是否有未编辑医嘱
     */
    public boolean isNew(TDataStore data){
        boolean falg = false;
        int rowCount = data.rowCount();
        for(int i=0;i<rowCount;i++){
            if(!data.isActive(i)){
                falg = true;
                break;
            }
        }
        return falg;
    }
    /**
     * 是否有相同
     * @param orderCode String
     * @return boolean
     */
    public boolean isSame(TDataStore data,String orderCode,int row){
        boolean falg = false;
        int rowCount = data.rowCount();
        for(int i=0;i<rowCount;i++){
            if(!data.isActive(i))
                continue;
            if(i==row)
                continue;
            if(data.getItemString(i,"ORDER_CODE").equals(orderCode)){
                falg = true;
                break;
            }
        }
        return falg;
    }
    /**
     * 点击事件
     * @param row int
     */
    public void onTableClicked(int row) {
        this.getTTable("ITEMTABLE").acceptText();
        this.getTTable("ITEMTABLE").clearSelection();
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        ODIPACKORDERDataStore packOrder = new ODIPACKORDERDataStore();
        switch (tab.getSelectedIndex()) {
            case 0:
                TParm tempST = this.getTTable("TABLE1").getDataStore().getRowParm(row);
                String packCodeSt = tempST.getValue("PACK_CODE");
                String deptOrDrSt = tempST.getValue("DEPT_OR_DR");
                String deptOrDrCodeSt = tempST.getValue("DEPTORDR_CODE");
                if(packCodeOnly!=null&&packCodeOnly.equals(packCodeSt)){
                    return;
                }
                //判断是否有修改
                if(packCodeOnly!=null&&isChangeItem()){
                    this.messageBox("请保存！");
                    return;
                }
                packCodeOnly = packCodeSt;
                packOrder.setPackCode(packCodeSt);
                packOrder.onQuery();
                int rowidSt = packOrder.insertRow();
                packOrder.setItem(rowidSt,"PACK_CODE",packCodeSt);
                packOrder.setItem(rowidSt,"DEPT_OR_DR",deptOrDrSt);
                packOrder.setItem(rowidSt,"DEPTORDR_CODE",deptOrDrCodeSt);
                packOrder.setActive(rowidSt,false);
                this.getTTable("ITEMTABLE").setDataStore(packOrder);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
            case 1:
                TParm tempUD = this.getTTable("TABLE2").getDataStore().getRowParm(row);
                String packCodeUd = tempUD.getValue("PACK_CODE");
                String deptOrDrUd = tempUD.getValue("DEPT_OR_DR");
                String deptOrDrCodeUd = tempUD.getValue("DEPTORDR_CODE");
                if(packCodeOnly!=null&&packCodeOnly.equals(packCodeUd)){
                    return;
                }
                //判断是否有修改
                if(packCodeOnly!=null&&isChangeItem()){
                    this.messageBox("请保存！");
                    return;
                }
                packCodeOnly = packCodeUd;
                packOrder.setPackCode(packCodeUd);
                packOrder.onQuery();
                int rowidUd = packOrder.insertRow();
                packOrder.setItem(rowidUd,"PACK_CODE",packCodeUd);
                packOrder.setItem(rowidUd,"DEPT_OR_DR",deptOrDrUd);
                packOrder.setItem(rowidUd,"DEPTORDR_CODE",deptOrDrCodeUd);
                packOrder.setActive(rowidUd,false);
                this.getTTable("ITEMTABLE").setDataStore(packOrder);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
            case 2:
                TParm tempDS = this.getTTable("TABLE3").getDataStore().getRowParm(row);
                String packCodeDs = tempDS.getValue("PACK_CODE");
                String deptOrDrDs = tempDS.getValue("DEPT_OR_DR");
                String deptOrDrCodeDs = tempDS.getValue("DEPTORDR_CODE");
                if(packCodeOnly!=null&&packCodeOnly.equals(packCodeDs)){
                    return;
                }
                //判断是否有修改
                if(packCodeOnly!=null&&isChangeItem()){
                    this.messageBox("请保存！");
                    return;
                }
                packCodeOnly = packCodeDs;
                packOrder.setPackCode(packCodeDs);
                packOrder.onQuery();
                int rowidDs = packOrder.insertRow();
                packOrder.setItem(rowidDs,"PACK_CODE",packCodeDs);
                packOrder.setItem(rowidDs,"DEPT_OR_DR",deptOrDrDs);
                packOrder.setItem(rowidDs,"DEPTORDR_CODE",deptOrDrCodeDs);
                packOrder.setActive(rowidDs,false);
                this.getTTable("ITEMTABLE").setDataStore(packOrder);
                this.getTTable("ITEMTABLE").setDSValue();
                break;
            case 3:
                TParm tempIG = this.getTTable("TABLE4").getDataStore().getRowParm(row);
                String packCodeIg = tempIG.getValue("PACK_CODE");
                String deptOrDrIg = tempIG.getValue("DEPT_OR_DR");
                String deptOrDrCodeIg = tempIG.getValue("DEPTORDR_CODE");
                if(packCodeOnly!=null&&packCodeOnly.equals(packCodeIg)){
                    return;
                }
                //判断是否有修改
                if(packCodeOnly!=null&&isChangeItem()){
                    this.messageBox("请保存！");
                    return;
                }
                packCodeOnly = packCodeIg;
                igOrderData = new ODIPACKORDERDataStore();
                igOrderData.setPackCode(packCodeIg);
                igOrderData.onQuery();
                TParm igData = new TParm();
                igData.setData("PACK_CODE",packCodeIg);
                igData.setData("DEPT_OR_DR",deptOrDrIg);
                igData.setData("DEPTORDR_CODE",deptOrDrCodeIg);
                filter(this.getTTable("ITEMTABLE"),igOrderData,igData);
//                int rowidIg = packOrder.insertRow();
//                packOrder.setItem(rowidIg,"PACK_CODE",packCodeIg);
//                packOrder.setItem(rowidIg,"DEPT_OR_DR",deptOrDrIg);
//                packOrder.setItem(rowidIg,"DEPTORDR_CODE",deptOrDrCodeIg);
//                packOrder.setActive(rowidIg,false);
//                this.getTTable("ITEMTABLE").setDataStore(packOrder);
//                this.getTTable("ITEMTABLE").setDSValue();
                igOrderData.showDebug();
                break;
        }
    }
    /**
     * 过滤
     * @return boolean
     */
    public boolean filter(TTable table,TDataStore ds,TParm data){
        boolean falg = true;
        if(!ds.filter())
            falg = false;
        int rowPrimaryCount = ds.rowCount();
//        System.out.println("主分区行"+rowPrimaryCount);
        //先删除未使用行
        for(int i=rowPrimaryCount-1;i>=0;i--){
            if(!ds.isActive(i)){
//                System.out.println("i====="+i);
                ds.deleteRow(i);
            }
        }
        TParm parm = ds.getBuffer(ds.PRIMARY);
//        System.out.println("主分区数据:"+parm);
//        table.setParmValue(this.getIGTableParm(parm));
        TParm action = this.getIGTableParm(parm,ds,data);
//        System.out.println("TABLE行:"+action);
        table.setParmValue(action);
        return falg;
    }
    /**
     * 得到住院中医医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm getIGTableParm(TParm parm,TDataStore ds,TParm data){
        TParm result = new TParm();
//        System.out.println("得到住院中医医嘱"+parm);
        int rowCount = parm.getCount()==-1?0:parm.getCount();
//        System.out.println("行数:"+rowCount);
        //ORDER_DESC_1;MEDI_QTY_1;DCTAGENT_CODE_1;ORDER_DESC_2;MEDI_QTY_2;DCTAGENT_CODE_2;ORDER_DESC_3;MEDI_QTY_3;DCTAGENT_CODE_3;ORDER_DESC_4;MEDI_QTY_4;DCTAGENT_CODE_4
        int row = rowCount%4==0?rowCount/4:Math.abs((rowCount+4)/4);
//        System.out.println("TABLE行数:"+row);
        int k=0;
        for(int i=0;i<row;i++){
           for(int j=1;j<5;j++){
               if(k<rowCount){
                   result.addData("ORDER_DESC_"+j,parm.getData("ORDER_DESC",k));
                   result.addData("MEDI_QTY_"+j,parm.getData("MEDI_QTY",k));
                   result.addData("DCTEXCEP_CODE_"+j,parm.getData("DCTEXCEP_CODE",k));
                   result.addData("ROW_ID_"+j,parm.getData("#ID#",k));
               }else{
                   int insertRow = ds.insertRow();
                   ds.setItem(insertRow, "PACK_CODE",data.getData("PACK_CODE"));
                   ds.setItem(insertRow, "DEPT_OR_DR",data.getData("DEPT_OR_DR"));
                   ds.setItem(insertRow, "DEPTORDR_CODE",data.getData("DEPTORDR_CODE"));
                   ds.setActive(insertRow,false);
                   result.addData("ORDER_DESC_"+j,"");
                   result.addData("MEDI_QTY_"+j,0);
                   result.addData("DCTEXCEP_CODE_"+j,"");
                   result.addData("ROW_ID_"+j,ds.getItemData(insertRow,"#ID#"));
               }
               k++;
           }
        }
        //补齐行数
        int addRowCount = rowCount%4;
        //判断需要添加的行数
        if(addRowCount==0){
            for(int i=1;i<5;i++){
                int insertRow = ds.insertRow();
                ds.setItem(insertRow, "PACK_CODE",data.getData("PACK_CODE"));
                ds.setItem(insertRow, "DEPT_OR_DR",data.getData("DEPT_OR_DR"));
                ds.setItem(insertRow, "DEPTORDR_CODE",data.getData("DEPTORDR_CODE"));
                ds.setActive(insertRow,false);
                result.addData("ORDER_DESC_"+i,"");
                result.addData("MEDI_QTY_"+i,0);
                result.addData("DCTEXCEP_CODE_"+i,"");
                result.addData("ROW_ID_"+i,ds.getItemData(insertRow,"#ID#"));
            }
            row+=1;
        }
        result.setData("ACTION","COUNT",row);
        return result;
    }

    /**
     * 判断是否有修改 Y有N没有
     * @return boolean
     */
    public boolean isChangeItem(){
        boolean falg = false;
        if(this.getTTable("ITEMTABLE").getDataStore()==null&&this.igOrderData==null){
            return falg;
        }
        TDataStore order = this.getTTable("ITEMTABLE").getDataStore();
        order.showDebug();
        if(order.getNewRows().length>0||order.getModifiedRows().length>0||order.getDeleteCount()>0)
            falg = true;
        if(this.igOrderData==null)
            return falg;
        if(this.igOrderData.getNewRows().length>0||this.igOrderData.getModifiedRows().length>0||this.igOrderData.getDeleteCount()>0)
            return falg;
        return falg;
    }
    /**
     * ORDERTABLE值改变事件
     * @param obj Object
     */
    public boolean onChangeTableValue(Object obj){
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //判断当前列是否有医嘱
        int selRow = node.getRow();
        TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                if (orderP.getValue("ORDER_CODE").length() == 0) {
                    //清空医嘱名称
                    node.getTable().getDataStore().setItem(selRow,"ORDER_DESC","");
                    node.getTable().setDSValue(selRow);
                }
                if("FREQ_CODE".equals(columnName)){
                    //判断频次是否可以在临时使用
                    if (!OrderUtil.getInstance().isSTFreq(node.getValue().toString())) {
                        //不是临时用药频次！
                        this.messageBox("不是临时用药频次");
                        return true;
                    }
                    if(orderP.getValue("LINK_NO").length()!=0){
                        int linkNo = orderP.getInt("LINK_NO");
                        int rowCount = node.getTable().getDataStore().rowCount();
                        for(int i=0;i<rowCount;i++){
                            if(!node.getTable().getDataStore().isActive(i))
                                continue;
                            if(i==selRow)
                                continue;
                            if(linkNo==node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")){
                                node.getTable().getDataStore().setItem(i,"FREQ_CODE",node.getValue());
                            }
                        }
                        node.getTable().setDSValue();
                    }
                }
                if("ROUTE_CODE".equals(columnName)){
                    if(orderP.getBoolean("LINKMAIN_FLG")){
                        int linkNo = orderP.getInt("LINK_NO");
                        int rowCount = node.getTable().getDataStore().rowCount();
                        for(int i=0;i<rowCount;i++){
                            if(!node.getTable().getDataStore().isActive(i))
                                continue;
                            if(i==selRow)
                                continue;
                            if(linkNo==node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")){
                                node.getTable().getDataStore().setItem(i,"ROUTE_CODE",node.getValue());
                            }
                        }
                        node.getTable().setDSValue();
                    }
                }
                break;
            case 1:
                if (orderP.getValue("ORDER_CODE").length() == 0) {
                    //清空医嘱名称
                    node.getTable().getDataStore().setItem(selRow,"ORDER_DESC","");
                    node.getTable().setDSValue(selRow);
                }
                if("FREQ_CODE".equals(columnName)){
                    if(orderP.getValue("LINK_NO").length()!=0){
                        int linkNo = orderP.getInt("LINK_NO");
                        int rowCount = node.getTable().getDataStore().rowCount();
                        for(int i=0;i<rowCount;i++){
                            if(!node.getTable().getDataStore().isActive(i))
                                continue;
                            if(i==selRow)
                                continue;
                            if(linkNo==node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")){
                                node.getTable().getDataStore().setItem(i,"FREQ_CODE",node.getValue());
                            }
                        }
                        node.getTable().setDSValue();
                    }
                }
                if("ROUTE_CODE".equals(columnName)){
                    if(orderP.getBoolean("LINKMAIN_FLG")){
                        int linkNo = orderP.getInt("LINK_NO");
                        int rowCount = node.getTable().getDataStore().rowCount();
                        for(int i=0;i<rowCount;i++){
                            if(!node.getTable().getDataStore().isActive(i))
                                continue;
                            if(i==selRow)
                                continue;
                            if(linkNo==node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")){
                                node.getTable().getDataStore().setItem(i,"ROUTE_CODE",node.getValue());
                            }
                        }
                        node.getTable().setDSValue();
                    }
                }
                break;
            case 2:
                if (orderP.getValue("ORDER_CODE").length() == 0) {
                    //清空医嘱名称
                    node.getTable().getDataStore().setItem(selRow,"ORDER_DESC","");
                    node.getTable().setDSValue(selRow);
                }
                if("FREQ_CODE".equals(columnName)){
                    if(orderP.getValue("LINK_NO").length()!=0){
                        int linkNo = orderP.getInt("LINK_NO");
                        int rowCount = node.getTable().getDataStore().rowCount();
                        for(int i=0;i<rowCount;i++){
                            if(!node.getTable().getDataStore().isActive(i))
                                continue;
                            if(i==selRow)
                                continue;
                            if(linkNo==node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")){
                                node.getTable().getDataStore().setItem(i,"FREQ_CODE",node.getValue());
                            }
                        }
                        node.getTable().setDSValue();
                    }
                }
                if("ROUTE_CODE".equals(columnName)){
                    if(orderP.getBoolean("LINKMAIN_FLG")){
                        int linkNo = orderP.getInt("LINK_NO");
                        int rowCount = node.getTable().getDataStore().rowCount();
                        for(int i=0;i<rowCount;i++){
                            if(!node.getTable().getDataStore().isActive(i))
                                continue;
                            if(i==selRow)
                                continue;
                            if(linkNo==node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")){
                                node.getTable().getDataStore().setItem(i,"ROUTE_CODE",node.getValue());
                            }
                        }
                        node.getTable().setDSValue();
                    }
                }
                break;
            case 3:
                String columnArray[] = columnName.split("_");
                TParm columnParm = node.getTable().getParmValue().getRow(node.getRow());
                //当前编辑行
                int onlyEditRow = getIGEditRowSetId(columnParm.getInt("ROW_ID_"+columnArray[columnArray.length-1]));
                //用量
                if(columnName.contains("MEDI_QTY")){
                    this.igOrderData.setItem(onlyEditRow, "MEDI_QTY",node.getValue());
                }
                //特殊煎法
                if(columnName.contains("DCTEXCEP_CODE")){
                    this.igOrderData.setItem(onlyEditRow,"DCTEXCEP_CODE",node.getValue());
                }
                break;
        }
        return false;
    }
    /**
     * 临时医嘱修改事件监听
     * @param obj Object
     */
    public boolean onChangeTableValueST(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //判断当前列是否有医嘱
        int selRow = node.getRow();
        TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
        if("PACK_DESC".equals(columnName)){
            if(node.getValue()==null||node.getValue().toString().trim().length()==0){
                return true;
            }else{
                node.getTable().getDataStore().setActive(selRow,true);
                node.getTable().setDSValue(selRow);
            }
        }
        return false;
    }
    /**
     * 长期医嘱修改事件监听
     * @param obj Object
     */
    public boolean onChangeTableValueUD(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //判断当前列是否有医嘱
        int selRow = node.getRow();
        TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
        if("PACK_DESC".equals(columnName)){
            if(node.getValue()==null||node.getValue().toString().trim().length()==0){
                return true;
            }else{
                node.getTable().getDataStore().setActive(selRow,true);
                node.getTable().setDSValue(selRow);
            }
        }
        return false;
    }

    /**
     * 出院带药医嘱修改事件监听
     * @param obj Object
     */
    public boolean onChangeTableValueDS(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //判断当前列是否有医嘱
        int selRow = node.getRow();
        TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
        if("PACK_DESC".equals(columnName)){
            if(node.getValue()==null||node.getValue().toString().trim().length()==0){
                return true;
            }else{
                node.getTable().getDataStore().setActive(selRow,true);
                node.getTable().setDSValue(selRow);
            }
        }
        return false;
    }
    /**
     * 中药医嘱修改事件监听
     * @param obj Object
     */
    public boolean onChangeTableValueIG(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //判断当前列是否有医嘱
        int selRow = node.getRow();
        TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
        if("PACK_DESC".equals(columnName)){
            if(node.getValue()==null||node.getValue().toString().trim().length()==0){
                return true;
            }else{
                node.getTable().getDataStore().setActive(selRow,true);
                node.getTable().setDSValue(selRow);
                node.getTable().getDataStore().showDebug();
            }
        }
        return false;
    }
    /**
     * 初始化权限
     */
    public void onInitPopeDem() {
        //权限可否选择科室
        if (!this.getPopedem("deptEnabled")) {
            this.callFunction("UI|DEPT_CODE|setEnabled", false);
        }else{
            this.callFunction("UI|DEPT_CODE|setEnabled", true);
        }
        //权限可否选择病区
        if (!this.getPopedem("operatorEnabled")) {
            this.callFunction("UI|USER_ID|setEnabled", false);
        }else{
            this.callFunction("UI|USER_ID|setEnabled", true);
        }
    }
    /**
     * 是否有未保存项目
     * @param tag String
     * @return boolean
     */
    public boolean isSave(String tag){
        boolean falg = false;
        if("ST".equals(tag)){
            if(this.getTTable("TABLE1").getDataStore()==null){
                return falg;
            }
            TDataStore da = this.getTTable("TABLE1").getDataStore();
            if(da.getNewRows().length>0||da.getModifiedRows().length>0||da.getDeleteCount()>0){
                falg = true;
            }
        }
        if("UD".equals(tag)){
            if(this.getTTable("TABLE2").getDataStore()==null){
                return falg;
            }
            TDataStore da = this.getTTable("TABLE2").getDataStore();
            if(da.getNewRows().length>0||da.getModifiedRows().length>0||da.getDeleteCount()>0){
                falg = true;
            }
        }
        if("DS".equals(tag)){
            if(this.getTTable("TABLE3").getDataStore()==null){
                return falg;
            }
            TDataStore da = this.getTTable("TABLE3").getDataStore();
            if(da.getNewRows().length>0||da.getModifiedRows().length>0||da.getDeleteCount()>0){
                falg = true;
            }
        }
        if("IG".equals(tag)){
            if(this.getTTable("TABLE4").getDataStore()==null){
                return falg;
            }
            TDataStore da = this.getTTable("TABLE4").getDataStore();
            if(da.getNewRows().length>0||da.getModifiedRows().length>0||da.getDeleteCount()>0){
                falg = true;
            }
        }
        return falg;
    }
    /**
     * 新增
     */
    public void onNew(){
        if(this.getValueString("DEPT_OR_DR").length()==0){
            this.messageBox("请填写套餐类别！");
            return;
        }
        TComboBox packType = (TComboBox)this.getComponent("DEPT_OR_DR");
        String id = packType.getSelectedID();
        if(this.getValueString("DEPT_CODE").length()==0&&("1".equals(id)||"3".equals(id))){
            this.messageBox("请填写科室！");
            return;
        }
        if(this.getValueString("USER_ID").length()==0&&("2".equals(id)||"4".equals(id))){
            this.messageBox("请填写人员！");
            return;
        }
        String deptOrDr = "";
        if("1".equals(id)||"3".equals(id)){
            deptOrDr = this.getValueString("DEPT_CODE");
        }
        if("2".equals(id)||"4".equals(id)){
            deptOrDr = this.getValueString("USER_ID");
        }
        ODIPACKMAINDataStore odiPackMain = new ODIPACKMAINDataStore();
        odiPackMain.setPackCode("");
        odiPackMain.setRxKind("");
        odiPackMain.setDeptOrDr("");
        odiPackMain.setDeptOrDrCode("");
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                this.getTTable("TABLE1").acceptText();
                if(!isActive("ST")){
                    this.messageBox("已有套餐项！");
                    return;
                }
                //是否已经保存
                if(isSave("ST")){
                    this.messageBox("请保存");
                    return;
                }
                int rowCountSt = this.getTTable("TABLE1").getDataStore().rowCount();
                if(rowCountSt<=0){
                    odiPackMain.onQuery();
                }else{
                    odiPackMain = (ODIPACKMAINDataStore)this.getTTable("TABLE1").getDataStore();
                }
                this.rxKind="ST";
                TParm parmSt = new TParm();
                parmSt.setData("DEPT_OR_DR",id);
                parmSt.setData("DEPTORDR_CODE",deptOrDr);
                parmSt.setData("RX_KIND",rxKind);
                odiPackMain.onInsertRow(parmSt);
                this.getTTable("TABLE1").setDataStore(odiPackMain);
                this.getTTable("TABLE1").setDSValue();
                break;
            case 1:
                this.getTTable("TABLE2").acceptText();
                if(!isActive("UD")){
                    this.messageBox("已有套餐项！");
                    return;
                }
                //是否已经保存
                if(isSave("UD")){
                    this.messageBox("请保存");
                    return;
                }
                int rowCountUd = this.getTTable("TABLE2").getDataStore().rowCount();
                if(rowCountUd<=0){
                    odiPackMain.onQuery();
                }else{
                    odiPackMain = (ODIPACKMAINDataStore)this.getTTable("TABLE2").getDataStore();
                }
                this.rxKind="UD";
                TParm parmUd = new TParm();
                parmUd.setData("DEPT_OR_DR",id);
                parmUd.setData("DEPTORDR_CODE",deptOrDr);
                parmUd.setData("RX_KIND",rxKind);
                odiPackMain.onInsertRow(parmUd);
                this.getTTable("TABLE2").setDataStore(odiPackMain);
                this.getTTable("TABLE2").setDSValue();
                break;
            case 2:
                this.getTTable("TABLE3").acceptText();
                if(!isActive("DS")){
                    this.messageBox("已有套餐项！");
                    return;
                }
                //是否已经保存
                if(isSave("DS")){
                    this.messageBox("请保存");
                    return;
                }
                int rowCountDs = this.getTTable("TABLE3").getDataStore().rowCount();
                if(rowCountDs<=0){
                    odiPackMain.onQuery();
                }else{
                    odiPackMain = (ODIPACKMAINDataStore)this.getTTable("TABLE3").getDataStore();
                }
                this.rxKind="DS";
                TParm parmDs = new TParm();
                parmDs.setData("DEPT_OR_DR",id);
                parmDs.setData("DEPTORDR_CODE",deptOrDr);
                parmDs.setData("RX_KIND",rxKind);
                odiPackMain.onInsertRow(parmDs);
                this.getTTable("TABLE3").setDataStore(odiPackMain);
                this.getTTable("TABLE3").setDSValue();
                break;
            case 3:
                this.getTTable("TABLE4").acceptText();
                if(!isActive("IG")){
                    this.messageBox("已有套餐项！");
                    return;
                }
                //是否已经保存
                if(isSave("IG")){
                    this.messageBox("请保存");
                    return;
                }
                int rowCountIg = this.getTTable("TABLE4").getDataStore().rowCount();
                if(rowCountIg<=0){
                    odiPackMain.onQuery();
                }else{
                    odiPackMain = (ODIPACKMAINDataStore)this.getTTable("TABLE4").getDataStore();
                }
                this.rxKind="IG";
                TParm parmIG = new TParm();
                parmIG.setData("DEPT_OR_DR",id);
                parmIG.setData("DEPTORDR_CODE",deptOrDr);
                parmIG.setData("RX_KIND",rxKind);
                parmIG.setData("GFREQ_CODE",getGFreqCode());
                parmIG.setData("GROUTE_CODE",getGRouteCode());
                parmIG.setData("GDCTAGENT_CODE",getGDctagentCode());
                odiPackMain.onInsertRow(parmIG);
                this.getTTable("TABLE4").setDataStore(odiPackMain);
                this.getTTable("TABLE4").setDSValue();
                break;
        }
    }
    /**
     * 判断是否有没有填写的套餐项目
     * @param rxKind String
     * @return boolean
     */
    public boolean isActive(String rxKind){
        boolean falg = true;
        if("ST".equals(rxKind)){
            if(this.getTTable("TABLE1").getDataStore().rowCount()<=0)
                return falg;
            ODIPACKMAINDataStore da = (ODIPACKMAINDataStore)this.getTTable("TABLE1").getDataStore();
            int rowCount = da.rowCount();
            for(int i=0;i<rowCount;i++){
                if(da.getItemString(i,"PACK_DESC").length()==0||da.getItemString(i,"PACK_DESC")==null){
                    falg = false;
                    break;
                }
            }
        }
        if("UD".equals(rxKind)){
            if(this.getTTable("TABLE2").getDataStore().rowCount()<=0)
                return falg;
            ODIPACKMAINDataStore da = (ODIPACKMAINDataStore)this.getTTable("TABLE2").getDataStore();
            int rowCount = da.rowCount();
            for(int i=0;i<rowCount;i++){
                if(da.getItemString(i,"PACK_DESC").length()==0||da.getItemString(i,"PACK_DESC")==null){
                    falg = false;
                    break;
                }
            }
        }
        if("DS".equals(rxKind)){
            if(this.getTTable("TABLE3").getDataStore().rowCount()<=0)
                return falg;
            ODIPACKMAINDataStore da = (ODIPACKMAINDataStore)this.getTTable("TABLE3").getDataStore();
            int rowCount = da.rowCount();
            for(int i=0;i<rowCount;i++){
                if(da.getItemString(i,"PACK_DESC").length()==0||da.getItemString(i,"PACK_DESC")==null){
                    falg = false;
                    break;
                }
            }

        }
        if("IG".equals(rxKind)){
            if(this.getTTable("TABLE4").getDataStore().rowCount()<=0)
                return falg;
            ODIPACKMAINDataStore da = (ODIPACKMAINDataStore)this.getTTable("TABLE4").getDataStore();
            int rowCount = da.rowCount();
            for(int i=0;i<rowCount;i++){
                if(da.getItemString(i,"PACK_DESC").length()==0||da.getItemString(i,"PACK_DESC")==null){
                    falg = false;
                    break;
                }
            }
        }
        return falg;
    }
    /**
     * 清空
     */
    public void onClear(){
        if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
            if (messageBox("提示信息", "是否保存?", this.YES_NO_OPTION) !=
                0) {
                packCodeOnly = "";
                this.clearValue("DEPT_OR_DR;PACK_DESC");
                this.setValue("DEPT_CODE", Operator.getDept());
                this.setValue("USER_ID", Operator.getID());
                clearTable();
                TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
                tab.setSelectedIndex(0);
                onInitPopeDem();
                return;
            }
            else {
                this.onSave();
            }
        }
        packCodeOnly="";
        this.clearValue("DEPT_OR_DR;PACK_DESC");
        this.setValue("DEPT_CODE", Operator.getDept());
        this.setValue("USER_ID", Operator.getID());
        clearTable();
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        tab.setSelectedIndex(0);
        onInitPopeDem();
    }
    /**
     * 清空TABLE
     */
    public void clearTable(){
        String[] tableNames = new String[]{"TABLE1","TABLE2","TABLE3","TABLE4"};
        for(String temp:tableNames){
            ODIPACKMAINDataStore ds = new ODIPACKMAINDataStore();
            ds.setPackCode("");
            ds.setDeptOrDr("");
            ds.setDeptOrDrCode("");
            ds.setRxKind("");
            ds.onQuery();
            this.getTTable(temp).setDataStore(ds);
            this.getTTable(temp).setDSValue();
        }
        ODIPACKORDERDataStore dsItem = new ODIPACKORDERDataStore();
        dsItem.setPackCode(packCodeOnly);
        dsItem.onQuery();
        this.getTTable("ITEMTABLE").setDataStore(dsItem);
        this.getTTable("ITEMTABLE").setDSValue();

    }
    /**
     * 得到TTabbedPane
     * @param tag String
     * @return TTabbedPane
     */
    public TTabbedPane getTTabbedPane(String tag){
        return (TTabbedPane)this.getComponent(tag);
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
     * 删除医嘱
     */
    public void onDelete(){
        Map tableName = new HashMap();
        tableName.put(0,"TABLE1");
        tableName.put(1,"TABLE2");
        tableName.put(2,"TABLE3");
        tableName.put(3,"TABLE4");
        String tableTag = tableName.get(this.pageIndex).toString();
//        this.messageBox_(this.getTTable(tableTag).getSelectedRow());
//        this.messageBox_(this.getTTable("ITEMTABLE").getSelectedRow());
        int mainSelRow = this.getTTable(tableTag).getSelectedRow();
        if(mainSelRow >=0){
            if(!this.getTTable(tableTag).getDataStore().isActive(mainSelRow)){
                this.messageBox("未使用不可删除！");
                return;
            }
            this.getTTable(tableTag).getDataStore().deleteRow(mainSelRow);
            this.getTTable(tableTag).setDSValue();
            //删除细表
            ODIPACKORDERDataStore dsItem = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
            int rowCount = dsItem.rowCount();
            for(int i=rowCount-1;i>=0;i--){
                if(!dsItem.isActive(i))
                    continue;
                dsItem.deleteRow(i);
            }
            this.getTTable("ITEMTABLE").setDSValue();
        }
        int orderSelRow = this.getTTable("ITEMTABLE").getSelectedRow();
        if(orderSelRow >=0){
            if(!this.getTTable("ITEMTABLE").getDataStore().isActive(orderSelRow)){
                this.messageBox("未使用不可删除！");
                return;
            }
            this.getTTable("ITEMTABLE").getDataStore().deleteRow(orderSelRow);
            this.getTTable("ITEMTABLE").setDSValue();
        }
    }
    /**
     * 保存
     */
    public void onSave(){
        if (!isSave(getTableTag(pageIndex)) && !this.isChangeItem()) {
            this.messageBox("没有需要保存的数据");
            return;
        }
        this.getTTable("TABLE1").acceptText();
        this.getTTable("TABLE2").acceptText();
        this.getTTable("TABLE3").acceptText();
        this.getTTable("TABLE4").acceptText();
        this.getTTable("ITEMTABLE").acceptText();
        //拿到主表SQL
        String[] sql = getRxKindTableSQL(pageIndex);
        sql = StringUtil.getInstance().copyArray(sql,getOrderTableSQL());
        //调试
        /**for (String temp : sql) {
            System.out.println("当前SQL:" + temp);
        }**/
        TParm parm = new TParm();
        parm.setData("ARRAY", sql);
        TParm actionParm = TIOM_AppServer.executeAction(actionName,
            "saveOrder", parm);
        if (actionParm.getErrCode() < 0)
            this.messageBox("保存失败！");
        else
            this.messageBox("保存成功！");
        saveQuery();
        //清空当前编辑
        this.packCodeOnly="";
    }
    /**
     * 保存后查询
     */
    public void saveQuery(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        Map tableName = new HashMap();
        tableName.put(0,"TABLE1");
        tableName.put(1,"TABLE2");
        tableName.put(2,"TABLE3");
        tableName.put(3,"TABLE4");
        ODIPACKMAINDataStore ds = new ODIPACKMAINDataStore();
        ds.setPackCode(this.packCodeOnly);
        ds.setDeptOrDr("");
        ds.setDeptOrDrCode("");
        ds.setRxKind("");
        ds.onQuery();
        this.getTTable(tableName.get(pageIndex).toString()).setDataStore(ds);
        this.getTTable(tableName.get(pageIndex).toString()).setDSValue();
        if(tab.getSelectedIndex()!=3){
            ODIPACKORDERDataStore dsItem = new ODIPACKORDERDataStore();
            dsItem.setPackCode(this.packCodeOnly);
            dsItem.onQuery();
            this.getTTable("ITEMTABLE").setDataStore(dsItem);
            this.getTTable("ITEMTABLE").setDSValue();
        }else{
            TParm igD = new TParm();
            igD.setData("PACK_CODE",this.igOrderData.getItemData(this.rowOnly,"PACK_CODE"));
            igD.setData("DEPT_OR_DR",this.igOrderData.getItemData(this.rowOnly,"DEPT_OR_DR"));
            igD.setData("DEPTORDR_CODE",this.igOrderData.getItemData(this.rowOnly,"DEPTORDR_CODE"));
            this.igOrderData.setPackCode(this.packCodeOnly);
            this.igOrderData.onQuery();
            this.filter(this.getTTable("ITEMTABLE"),this.igOrderData,igD);
            this.rowOnly = -1;
        }
    }
    /**
     * 得到主表更新语句
     * @param type int
     * @return String[]
     */
    public String[] getRxKindTableSQL(int type){
        Timestamp sysDate = SystemTool.getInstance().getDate();
        Map tableName = new HashMap();
        tableName.put(0,"TABLE1");
        tableName.put(1,"TABLE2");
        tableName.put(2,"TABLE3");
        tableName.put(3,"TABLE4");
        ODIPACKMAINDataStore ds = (ODIPACKMAINDataStore)this.getTTable(tableName.get(type).toString()).getDataStore();
        int newRows[] = ds.getNewRows();
        for(int temp:newRows){
            ds.setItem(temp,"OPT_USER",Operator.getID());
            ds.setItem(temp,"OPT_TERM",Operator.getIP());
            ds.setItem(temp,"OPT_DATE",sysDate);
        }
        return ds.getUpdateSQL();
    }
    /**
     * 拿到细表SQL
     * @return String[]
     */
    public String[] getOrderTableSQL(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        Timestamp sysDate = SystemTool.getInstance().getDate();
        TParm parm = new TParm(this.getDBTool().select("SELECT NVL(MAX(SEQ_NO)+1,1) AS SEQ_NO FROM ODI_PACK_ORDER WHERE PACK_CODE='"+this.packCodeOnly+"'"));
        int seqNo=parm.getInt("SEQ_NO",0);
        if(tab.getSelectedIndex()!=3){
            ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore)this.getTTable("ITEMTABLE").getDataStore();
            int rowId[] = ds.getNewRows();
            for(int temp:rowId){
            	if (ds.getItemInt(temp, "SEQ_NO") > 0) {
            		ds.setActive(temp, true);
            	} else {
            		ds.setItem(temp,"SEQ_NO",seqNo);
            	}
                ds.setItem(temp,"OPT_DATE",sysDate);
                ds.setItem(temp,"OPT_USER",Operator.getID());
                ds.setItem(temp,"OPT_TERM",Operator.getIP());
                seqNo++;
            }
            return ds.getUpdateSQL();
        }else{
            int rowId[] = this.igOrderData.getNewRows();
            for(int temp:rowId){
                this.igOrderData.setItem(temp,"SEQ_NO",seqNo);
                this.igOrderData.setItem(temp,"OPT_DATE",sysDate);
                this.igOrderData.setItem(temp,"OPT_USER",Operator.getID());
                this.igOrderData.setItem(temp,"OPT_TERM",Operator.getIP());
                seqNo++;
            }
            return this.igOrderData.getUpdateSQL();
        }
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * 查询
     */
    public void onQuery(){
        if(this.getValueString("DEPT_OR_DR").length()==0){
            this.messageBox("请选择套餐类别！");
            return;
        }

        Map tableName = new HashMap();
        tableName.put(0,"TABLE1");
        tableName.put(1,"TABLE2");
        tableName.put(2,"TABLE3");
        tableName.put(3,"TABLE4");
        String deptDrCode = getDeptOrCodeString();
        String rxKind = getRxKindString();
        ODIPACKMAINDataStore ds = new ODIPACKMAINDataStore();
        ds.setPackCode(this.packCodeOnly);
        ds.setDeptOrDr(this.getValueString("DEPT_OR_DR"));
        ds.setDeptOrDrCode(deptDrCode);
        ds.setRxKind(rxKind);
        ds.setRetrieveType("Q");
        ds.onQuery();
        this.getTTable(tableName.get(this.pageIndex).toString()).setDataStore(ds);
        this.getTTable(tableName.get(this.pageIndex).toString()).setDSValue();
    }
    /**
     *  得到医嘱类别
     * @return String
     */
    public String getRxKindString(){
        String rxKind = "ST";
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
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
     * 得到科室人员
     * @return String
     */
    public String getDeptOrCodeString(){
        TComboBox packType = (TComboBox)this.getComponent("DEPT_OR_DR");
        String id = packType.getSelectedID();
        String deptOrDr = "";
        if("1".equals(id)||"3".equals(id)){
            deptOrDr = this.getValueString("DEPT_CODE");
        }
        if("2".equals(id)||"4".equals(id)){
            //=========pangben modify 20110518 start 科室连动入参是setText
            deptOrDr = this.getValueString("USER_ID");// modify by wanglong 20130123
        }
        return deptOrDr;
    }

    public String getDctTakeDays() {
        return dctTakeDays;
    }

    public String getDctTakeQty() {
        return dctTakeQty;
    }

    public String getOdiDefaFreg() {
        return odiDefaFreg;
    }

    public String getOdiStatCode() {
        return odiStatCode;
    }

    public String getOdiUddStatCode() {
        return odiUddStatCode;
    }

    public void setDctTakeDays(String dctTakeDays) {
        this.dctTakeDays = dctTakeDays;
    }

    public void setDctTakeQty(String dctTakeQty) {
        this.dctTakeQty = dctTakeQty;
    }

    public void setOdiDefaFreg(String odiDefaFreg) {
        this.odiDefaFreg = odiDefaFreg;
    }

    public void setOdiStatCode(String odiStatCode) {
        this.odiStatCode = odiStatCode;
    }

    public void setOdiUddStatCode(String odiUddStatCode) {
        this.odiUddStatCode = odiUddStatCode;
    }

    public String getGFreqCode(){
        return this.gFreqCode;
    }

    public void setGFreqCode(String gFreqCode){
        this.gFreqCode = gFreqCode;
    }

    public String getGDctagentCode(){
        return this.gDctagentCode;
    }

    public void setGDctagentCode(String gDctagentCode){
        this.gDctagentCode = gDctagentCode;
    }
    public String getGRouteCode(){
        return this.gRouteCode;
    }
    public void setGRouteCode(String gRouteCode){
        this.gRouteCode = gRouteCode;
    }

}
