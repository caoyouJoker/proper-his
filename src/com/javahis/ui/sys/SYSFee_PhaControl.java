package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDSObject;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.TypeTool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;

import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import jdo.sys.SYSHzpyTool;

import com.javahis.device.JMFRegistry;
import com.javahis.device.JMStudio;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.manager.TIOM_FileServer;

import jdo.sys.SYSRegionTool;


/**
 * <p>Title:药品费用基本档 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS 1.0</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0 
 */
public class SYSFee_PhaControl
    extends TControl {

    /**
     * 监控三个费用值，如果有改变在SYS_FEE中生成新的一条数据
     */
    private double oldOwnPrice = 0.0;
    private double oldNhiPrice = 0.0;
    private double oldGovPrice = 0.0;
    private String orderCode = "";
    //$$ add by lx 表格查询的SQL
	private String querySQL="";
    

    /**
     * 大对象
     */
    private TDSObject bigObject = new TDSObject();

    private TDS phaTransUnitDS = new TDS();
    private TDS phaBaseDS = new TDS();
    /**
     * SYS_FEE表的TDS
     */
    TDS sysFeeDS = new TDS();

    //private TDS indStockMDS = new TDS();
    /**
     * SYS_FEE_HISTORY表的TDS
     */
    TDS sysFeeHisDS = new TDS();

    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 编号规则类别工具
     */
    private SYSRuleTool ruleTool;
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    private TDataStore treeDataStore = new TDataStore();

    /**
     * 表格当前选中的行号
     */
    int selRow = -1;
    /**
     * 所有控件的名字
     */
    private String controlNameTab1 =
        "LET_KEYIN_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;" +
        "HRM_FIT_FLG;DR_ORDER_FLG;TRANS_OUT_FLG;EXEC_ORDER_FLG;START_DATE;" +
        "END_DATE;ORDER_DESC;PY1;NHI_FEE_DESC;" +
        "TRADE_ENG_DESC;GOODS_DESC;GOODS_PYCODE;ALIAS_DESC;SPECIFICATION;ALIAS_PYCODE;DESCRIPTION;" +
        "HYGIENE_TRADE_CODE;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;HABITAT_TYPE;" +
        "MAN_CODE;UNIT_CODE;MR_CODE;CHARGE_HOSP_CODE;ORDER_CAT1_CODE;LCS_CLASS_CODE;" +
        "EXEC_DEPT_CODE;INSPAY_TYPE;OWN_PRICE;OWN_PRICE2;" +
        "OWN_PRICE3;ADDPAY_RATE;ADDPAY_AMT;ORD_SUPERVISION";//shibl 20121127 add

    private String controlNameTab2 =
        "MEDI_QTY1;DOSAGE_QTY1;DOSAGE_QTY;STOCK_QTY;STOCK_QTY1;PURCH_QTY;" +
        "MEDI_UNIT;DOSAGE_UNIT1;DOSAGE_UNIT;STOCK_UNIT;STOCK_UNIT1;PURCH_UNIT;" +
        "TYPE_CODE;HALF_USE_FLG;REFUND_FLG;" +
        //============pangben modify 20110428 添加SKINTEST_FLG（皮试）字段PRESCRIPTION_FLG（处方）字段
        "DSPNSTOTDOSE_FLG;GIVEBOX_FLG;SKINTEST_FLG;PRESCRIPTION_FLG;REUSE_FLG;ODD_FLG;UDCARRY_FLG;CTRLDRUGCLASS_CODE;" +
        "ANTIBIOTIC_CODE;SUP_CODE;DOSE_CODE;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;" +
        //=========pangben modify 20110817 添加SYS_GRUG_CLASS(药物种类)字段 NOADDTION_FLG (零差价)字段
        "MEDI_QTY;DEFAULT_TOTQTY;ATC_FLG;IS_REMARK,ATC_FLG_I"+";SYS_GRUG_CLASS;NOADDTION_FLG;" +
        //加DRUG_NOTES_PATIENT（病人用药注意事项） DRUG_NOTES_DR（医师用药注意事项） DRUG_NOTE（药袋说明） DRUG_FORM（外形说明）
        //DDD 
        "DRUG_NOTES_PATIENT;DRUG_NOTES_DR;DRUG_NOTE;DRUG_FORM;DDD";//shibl 20121127 add


    /**
     * 界面的控件
     */
    //树
    TTree tree;
    //主表
    TTable upTable;

    //全部/启用/停用标记
    TRadioButton ALL;
    TRadioButton ACTIVE_Y;
    TRadioButton ACTIVE_N;


    /**
     * ********************第一个标签页的控件*************************************
     */
    //启用注记
    TCheckBox ACTIVE_FLG;
    //允许手动记价注记
    TCheckBox LET_KEYIN_FLG;
    //门,急,住,健,经医适用标记
    TCheckBox OPD_FIT_FLG;
    TCheckBox EMG_FIT_FLG;
    TCheckBox IPD_FIT_FLG;
    TCheckBox HRM_FIT_FLG;
    TCheckBox DR_ORDER_FLG;
    //转出注记
    TCheckBox TRANS_OUT_FLG;
    TCheckBox EXEC_ORDER_FLG;

    TTextFormat START_DATE;
    TTextFormat END_DATE;
    TTextFormat ORD_SUPERVISION;

    TTextField QUERY;
    TTextField ORDER_CODE;
    TTextField ORDER_DESC;
    TTextField PY1;
    TTextField NHI_FEE_DESC;
    TTextField TRADE_ENG_DESC;
    TTextField GOODS_DESC;
    TTextField GOODS_PYCODE;
    TTextField ALIAS_DESC;
    TTextField SPECIFICATION;
    TTextField ALIAS_PYCODE;
    TTextField DESCRIPTION;
    TTextField HYGIENE_TRADE_CODE;
    TTextField NHI_CODE_I;
    TTextField NHI_CODE_O;
    TTextField NHI_CODE_E;
    
    TComboBox HABITAT_TYPE;
    //===zhangp 20120706 start
//    TTextFormat MAN_CODE;
    TTextArea MAN_CODE;
    //===zhangp 20120706 end
    TTextFormat UNIT_CODE;
    TTextFormat MR_CODE;
    TTextFormat CHARGE_HOSP_CODE;
    TTextFormat ORDER_CAT1_CODE;
    TComboBox LCS_CLASS_CODE;
    TComboBox TRANS_HOSP_CODE;
    TTextFormat EXEC_DEPT_CODE;
    TComboBox INSPAY_TYPE;

    TNumberTextField OWN_PRICE;
    TNumberTextField NHI_PRICE;
    TNumberTextField GOV_PRICE;
    TNumberTextField ADDPAY_RATE;
    TNumberTextField ADDPAY_AMT;

    /**
     * ********************第二个标签页的控件*************************************
     */
    //保存到PHA_TRANSUNIT的数据
    TTextField DRUG_NOTES_PATIENT;
    TTextField DRUG_NOTES_DR;
    TTextField DRUG_NOTE;
    TTextField DRUG_FORM;
    TTextField DDD;

    TNumberTextField MEDI_QTY1;
    TNumberTextField DOSAGE_QTY1;
    TNumberTextField DOSAGE_QTY;
    TNumberTextField STOCK_QTY;
    TNumberTextField STOCK_QTY1;
    TNumberTextField PURCH_QTY;
    TTextFormat MEDI_UNIT;
    TTextFormat DOSAGE_UNIT1;
    TTextFormat DOSAGE_UNIT;
    TTextFormat STOCK_UNIT;
    TTextFormat STOCK_UNIT1;
    TTextFormat PURCH_UNIT;
    TComboBox copyMEDI_UNIT;
    TComboBox copyDOSAGE_UNIT1;

    //保存到PHA_BASE的数据
    TRadioButton PHA_TYPE1;
    TRadioButton PHA_TYPE2;
    TRadioButton PHA_TYPE3;

    TComboBox TYPE_CODE;

    TCheckBox HALF_USE_FLG;
    TCheckBox REFUND_FLG;
    /*
     * 药品设置时,默认总量只读,当选中总量给药时.打开默认总量
     * 初始化时,当选中药品是总量给药时,打开默认总量,否则设置默认总量只读
     * Modify ZhenQin 2011-06-01
     */
    TCheckBox DSPNSTOTDOSE_FLG;
    TCheckBox GIVEBOX_FLG;
    TCheckBox SKINTEST_FLG;//=======pangben modify 20110428 皮试
    TCheckBox PRESCRIPTION_FLG;//=======pangben modify 20110428 处方
    TCheckBox NOADDTION_FLG;//=======pangben modify 20110817 零差价
    TCheckBox REUSE_FLG;
    TCheckBox ODD_FLG;
    TCheckBox UDCARRY_FLG;

    TComboBox CTRLDRUGCLASS_CODE;
    TComboBox ANTIBIOTIC_CODE;
    TTextFormat SUP_CODE;
    TTextFormat DOSE_CODE;
    TTextFormat FREQ_CODE;
    TTextFormat ROUTE_CODE;
    TTextFormat SYS_GRUG_CLASS;//=======pangben modify 20110817 药物种类

    TNumberTextField TAKE_DAYS;
    TNumberTextField MEDI_QTY;
    TNumberTextField DEFAULT_TOTQTY;
    TNumberTextField NORMAL_DAYS;

    //外部调用标记
    boolean outShow = false;
    
    // 判断onNew方法执行与否
    boolean isOnNew = false;

    /**
     * 初始化
     */
    public SYSFee_PhaControl() {
    }

    /**
     * 初始化
     */
    public void onInit() { //初始化程序
        super.onInit(); 
        myInitControler();  
        //初始化树  
        onInitTree();
        //给tree添加监听事件  
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //初始化结点
        onInitNode();
        //如果传参是TParm类型的就是外部调用
        if (this.getParameter() instanceof TParm)
            initParmFromOutside();

        canSave();

        //========pangben modify 20110426 start 权限添加
        //初始化院区
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110426 stop
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }


    private void canSave(){
        Object obj = this.getParameter();
        if(obj==null)
            return;
        if(obj.equals("QUERY")){
        	callFunction("UI|save|setEnabled", false);
        	callFunction("UI|PHOTO|setEnabled", false);
        	callFunction("UI|regist|setEnabled", false);
        }
    }

    /**
     * 初始化界面参数caseNo/stationCode（从外部病患管理界面传来的参数）
     */
    public void initParmFromOutside() {

        //从病案管理界面拿到参数TParm
        TParm outsideParm = (TParm)this.getParameter();
        if (outsideParm != null) {
            //按就诊号查询的caseNo
            String ordcode = outsideParm.getData("ORDER_CODE").toString();
            //按病区查询的stationCode
            String flg = outsideParm.getData("FLG").toString();
            //外部调用锁住所有控件
            setEnabledForCtl(false);
            showByOutside(ordcode, flg);
        }
        return;
    }

    /**
     *
     * @param orderByout String
     * @param fromFlg String
     */
    public void showByOutside(String orderByout, String fromFlg) {
        //判断是否由外部接口调入
        if (fromFlg.equals("OPD")) {
            orderCode = orderByout;
            ORDER_CODE.setValue(orderCode);
            outShow = true;
        }
        onQuery();

    }


    /**
     * 初始化树
     */
    public void onInitTree() {
        //得到树根
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        //给根节点添加文字显示
        treeRoot.setText("药品分类");
        //给根节点赋tag
        treeRoot.setType("Root");
        //设置根节点的id
        treeRoot.setID("");
        //清空所有节点的内容
        treeRoot.removeAllChildren();
        //调用树点初始化方法
        callMessage("UI|TREE|update");
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }

    /**
     * 初始化树的结点
     */

    public void onInitNode() {
        //给dataStore赋值
        treeDataStore.setSQL(
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='PHA_RULE'");
        //如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        //过滤数据,是编码规则中的科室数据
        ruleTool = new SYSRuleTool("PHA_RULE");
        if (ruleTool.isLoad()) { //给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //循环给树安插节点
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //更新树
        tree.update();
        //设置树的默认选中节点
        tree.setSelectNode(treeRoot);
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }

    /**
     * 单击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //清空
        onClear();

        //新增按钮不能用
        callFunction("UI|new|setEnabled", false);
        //得到点击树的节点对象
        //TTreeNode node = (TTreeNode) parm;
        TTreeNode node = tree.getSelectNode();
        if (node == null)
            return;
        //得到table对象
        TTable table = (TTable)this.callFunction("UI|upTable|getThis");
        //table接收所有改变值
        table.acceptText();
        //判断点击的是否是树的根结点
        if (node.getType().equals("Root")) {
            //如果是树的根接点table上不显示数据
            table.removeRowAll();
        }
        else { //如果点的不是根结点
            //拿到当前选中的节点的id值
            String id = node.getID();
            //拿到查询TDS的SQL语句（通过上一层的ID去like表中的orderCode）
            String sql = getSQL(id,Operator.getRegion());//======pangben modify 20110426 添加参数
          //$$===add by lx 2013/01/06====$$//
			querySQL=sql;
            //初始化table和TDS
            initTblAndTDS(sql);

        }
        //给table数据加排序条件
//        table.setSort("ORDER_CODE");
        //table排序后重新赋值
//        table.sort();
        //得到当前点击结点的ID
        String nowID = node.getID();

        int classify = 1;
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //如果是最小节点,可以增加一行(使新增按钮可用)&&不是外部调用
        if (classify > ruleTool.getClassifyCurrent() && !outShow) {
            this.callFunction("UI|new|setEnabled", true);
        }
        String string = this.getParameter().toString();
        // 登录用户的角色
        String role = Operator.getRole();
        if (string.equals(role)) {
        	isOnNew = true;
        }
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
    }

    /**
     * 得到初始化TDS的SQL语句(查找目前正在启用的项目列表：不卡START_DATE/END_DATE 只卡ACTIVE_FLG为‘Y’)
     * @return String
     * =========pangben modify 20110426 添加区域过滤
     */
    private String getSQL(String orderCode,String regionCode) {
        String active = "";

        if (ACTIVE_Y.isSelected()) { //启用
            active = " AND ACTIVE_FLG='Y'";
            setEnabledForCtl(true);
        }
        else if (ACTIVE_N.isSelected()) { //停用
            active = " AND ACTIVE_FLG='N'";
            setEnabledForCtl(false);
        }
        else { //全部
            setEnabledForCtl(false);
        }
        //======pangben modify 20110426 start
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        //======pangben modify 20110426 stop
        String sql = "";
        //配过滤条件
        if (orderCode != null && orderCode.length() > 0)
            sql = " SELECT * " +
            	" FROM SYS_FEE_HISTORY" +
            	" WHERE " +
                " ORDER_CODE like '" + orderCode + "%'" +
                active +region+
                " ORDER BY ORDER_CODE";

        return sql;
    }

    /**
     * 初始化上面的表格还有所有的控件数据
     * @param sql String
     */
    public void initTblAndTDS(String sql) {
        sysFeeHisDS.setSQL(sql);
        sysFeeHisDS.retrieve();
        //如果没有数据清空表格上的数据
        if (sysFeeHisDS.rowCount() <= 0) {
            upTable.removeRowAll();
        }
        sysFeeHisDS.showDebug();
        
        upTable.setDataStore(sysFeeHisDS);
        //modify by liming  2012/02/18 begin
        upTable.setDSValue();
      //modify by liming end
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->

    }

    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     * 设置
     */
    public void myInitControler() {

        tree = (TTree) callFunction("UI|TREE|getThis");

        //得到table控件
        upTable = (TTable)this.getComponent("upTable");

        ALL = (TRadioButton)this.getComponent("ALL");
        ACTIVE_Y = (TRadioButton)this.getComponent("ACTIVE_Y");
        ACTIVE_N = (TRadioButton)this.getComponent("ACTIVE_N");

        ACTIVE_FLG = (TCheckBox)this.getComponent("ACTIVE_FLG");
        LET_KEYIN_FLG = (TCheckBox)this.getComponent("LET_KEYIN_FLG");
        OPD_FIT_FLG = (TCheckBox)this.getComponent("OPD_FIT_FLG");
        EMG_FIT_FLG = (TCheckBox)this.getComponent("EMG_FIT_FLG");
        IPD_FIT_FLG = (TCheckBox)this.getComponent("IPD_FIT_FLG");
        HRM_FIT_FLG = (TCheckBox)this.getComponent("HRM_FIT_FLG");
        DR_ORDER_FLG = (TCheckBox)this.getComponent("DR_ORDER_FLG");
        TRANS_OUT_FLG = (TCheckBox)this.getComponent("TRANS_OUT_FLG");
        EXEC_ORDER_FLG = (TCheckBox)this.getComponent("EXEC_ORDER_FLG");

        START_DATE = (TTextFormat)this.getComponent("START_DATE");
        END_DATE = (TTextFormat)this.getComponent("END_DATE");
        ORD_SUPERVISION=(TTextFormat)this.getComponent("ORD_SUPERVISION");
        
        QUERY= (TTextField)this.getComponent("QUERY");
        ORDER_CODE = (TTextField)this.getComponent("ORDER_CODE");
        ORDER_DESC = (TTextField)this.getComponent("ORDER_DESC");
        PY1 = (TTextField)this.getComponent("PY1");
        NHI_FEE_DESC = (TTextField)this.getComponent("NHI_FEE_DESC");
        TRADE_ENG_DESC = (TTextField)this.getComponent("TRADE_ENG_DESC");

        GOODS_DESC = (TTextField)this.getComponent("GOODS_DESC");
        GOODS_PYCODE = (TTextField)this.getComponent("GOODS_PYCODE");

        ALIAS_DESC = (TTextField)this.getComponent("ALIAS_DESC");
        SPECIFICATION = (TTextField)this.getComponent("SPECIFICATION");
        ALIAS_PYCODE = (TTextField)this.getComponent("ALIAS_PYCODE");
        DESCRIPTION = (TTextField)this.getComponent("DESCRIPTION");
        HYGIENE_TRADE_CODE = (TTextField)this.getComponent("HYGIENE_TRADE_CODE");

        NHI_CODE_I = (TTextField)this.getComponent("NHI_CODE_I");
        NHI_CODE_O = (TTextField)this.getComponent("NHI_CODE_O");
        NHI_CODE_E = (TTextField)this.getComponent("NHI_CODE_E");

        HABITAT_TYPE = (TComboBox)this.getComponent("HABITAT_TYPE");
        //===zhangp 20120706 start
//        MAN_CODE = (TTextFormat)this.getComponent("MAN_CODE");
        MAN_CODE = (TTextArea)this.getComponent("MAN_CODE");
        //===zhangp 20120706 end
        UNIT_CODE = (TTextFormat)this.getComponent("UNIT_CODE");
        MR_CODE= (TTextFormat)this.getComponent("MR_CODE");
        CHARGE_HOSP_CODE = (TTextFormat)this.getComponent("CHARGE_HOSP_CODE");
        ORDER_CAT1_CODE = (TTextFormat)this.getComponent("ORDER_CAT1_CODE");
        
        LCS_CLASS_CODE = (TComboBox)this.getComponent("LCS_CLASS_CODE");
        TRANS_HOSP_CODE = (TComboBox)this.getComponent("TRANS_HOSP_CODE");
        EXEC_DEPT_CODE = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");
        INSPAY_TYPE = (TComboBox)this.getComponent("INSPAY_TYPE");

        NHI_PRICE = (TNumberTextField)this.getComponent("OWN_PRICE2");
        GOV_PRICE = (TNumberTextField)this.getComponent("OWN_PRICE3");
        OWN_PRICE = (TNumberTextField)this.getComponent("OWN_PRICE");
        ADDPAY_RATE = (TNumberTextField)this.getComponent("ADDPAY_RATE");
        ADDPAY_AMT = (TNumberTextField)this.getComponent("ADDPAY_AMT");

        //第二个标签页
        MEDI_QTY1 = (TNumberTextField)this.getComponent("MEDI_QTY1");
        DOSAGE_QTY1 = (TNumberTextField)this.getComponent("DOSAGE_QTY1");
        DOSAGE_QTY = (TNumberTextField)this.getComponent("DOSAGE_QTY");
        STOCK_QTY = (TNumberTextField)this.getComponent("STOCK_QTY");
        STOCK_QTY1 = (TNumberTextField)this.getComponent("STOCK_QTY1");
        PURCH_QTY = (TNumberTextField)this.getComponent("PURCH_QTY");

        MEDI_UNIT = (TTextFormat)this.getComponent("MEDI_UNIT"); ;
        DOSAGE_UNIT1 = (TTextFormat)this.getComponent("DOSAGE_UNIT1"); ;
        DOSAGE_UNIT = (TTextFormat)this.getComponent("DOSAGE_UNIT"); ;
        STOCK_UNIT = (TTextFormat)this.getComponent("STOCK_UNIT"); ;
        STOCK_UNIT1 = (TTextFormat)this.getComponent("STOCK_UNIT1"); ;
        PURCH_UNIT = (TTextFormat)this.getComponent("PURCH_UNIT"); ;

        PHA_TYPE1 = (TRadioButton)this.getComponent("PHA_TYPE1");
        PHA_TYPE2 = (TRadioButton)this.getComponent("PHA_TYPE2");
        PHA_TYPE3 = (TRadioButton)this.getComponent("PHA_TYPE3");

        TYPE_CODE = (TComboBox)this.getComponent("TYPE_CODE");

        HALF_USE_FLG = (TCheckBox)this.getComponent("HALF_USE_FLG");
        REFUND_FLG = (TCheckBox)this.getComponent("REFUND_FLG");
        DSPNSTOTDOSE_FLG = (TCheckBox)this.getComponent("DSPNSTOTDOSE_FLG");
        GIVEBOX_FLG = (TCheckBox)this.getComponent("GIVEBOX_FLG");
        NORMAL_DAYS = (TNumberTextField)this.getComponent("NORMAL_DAYS");
        //========pangben modify 20110428 start 皮试字段
        SKINTEST_FLG = (TCheckBox)this.getComponent("SKINTEST_FLG");
        //处方字段
        PRESCRIPTION_FLG = (TCheckBox)this.getComponent("PRESCRIPTION_FLG");
        //零差价
        NOADDTION_FLG=(TCheckBox)this.getComponent("NOADDTION_FLG");
        //========pangben modify 20110428 stop
        REUSE_FLG = (TCheckBox)this.getComponent("REUSE_FLG");
        ODD_FLG = (TCheckBox)this.getComponent("ODD_FLG");
        UDCARRY_FLG = (TCheckBox)this.getComponent("UDCARRY_FLG");

        CTRLDRUGCLASS_CODE = (TComboBox)this.getComponent("CTRLDRUGCLASS_CODE");
        ANTIBIOTIC_CODE = (TComboBox)this.getComponent("ANTIBIOTIC_CODE");
        SUP_CODE = (TTextFormat)this.getComponent("SUP_CODE");
        DOSE_CODE = (TTextFormat)this.getComponent("DOSE_CODE");
        FREQ_CODE = (TTextFormat)this.getComponent("FREQ_CODE");
        ROUTE_CODE = (TTextFormat)this.getComponent("ROUTE_CODE");
        //========pangben modify 20110817 start 药物种类
        SYS_GRUG_CLASS=(TTextFormat)this.getComponent("SYS_GRUG_CLASS");
        //========pangben modify 20110817 stop
        TAKE_DAYS = (TNumberTextField)this.getComponent("TAKE_DAYS");
        MEDI_QTY = (TNumberTextField)this.getComponent("MEDI_QTY");
        DEFAULT_TOTQTY = (TNumberTextField)this.getComponent("DEFAULT_TOTQTY");

        copyMEDI_UNIT = (TComboBox)this.getComponent("copyMEDI_UNIT"); ;
        copyDOSAGE_UNIT1 = (TComboBox)this.getComponent("copyDOSAGE_UNIT1"); ;
        
        DRUG_NOTES_PATIENT = (TTextField)this.getComponent("DRUG_NOTES_PATIENT	");
        DRUG_NOTES_DR = (TTextField)this.getComponent("DRUG_NOTES_DR");
        DRUG_NOTE = (TTextField)this.getComponent("DRUG_NOTE");
        DRUG_FORM = (TTextField)this.getComponent("DRUG_FORM");
        this.DDD=(TTextField)this.getComponent("DDD");
        
        

        //给上下table注册单击事件监听
        this.callFunction("UI|upTable|addEventListener",
                          "upTable->" + TTableEvent.CLICKED, this,
                          "onUpTableClicked");
        TParm parm =new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //新增按钮不能用
        callFunction("UI|new|setEnabled", false);
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            ORDER_CODE.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
        //清空查询控件
        QUERY.setValue("");
        onQuery();
    }


    /**
     * 单击上面的table事件
     */
    public void onUpTableClicked(int row) {

        //当前选中的行号
        selRow = row;
        //清空下面控件的值
        clearCtl();
        //得到表的parm
        TParm tableDate = ( (TDS) upTable.getDataStore()).getBuffer(TDS.
            PRIMARY).getRow(selRow);
        //给所有的控件值
        //setValueForDownCtl(tableDate, row);
        setValueForDownCtl(tableDate);
        //当是显示‘全部’的时候默认不可编辑(启用状态恢复编辑/停用状态不可编辑)
        if (ALL.isSelected()) {
            boolean activeFlg = ACTIVE_FLG.isSelected();
            setEnabledForCtl(activeFlg);
        }

        //记录三个控制生成新SYSFEE数据的关键变量(如果保存的时候orderCode不变，只有三个价格变就说明要更新，插入)
        oldOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
        oldNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        oldGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
        orderCode = ORDER_CODE.getValue();

        //查询PHA_TRANSUNIT生成TDS
        qeuryPhaTransUnit(orderCode);
        qeuryPhaBase(orderCode);

        //初始化标签2
        onTabPanel2();
        this.viewPhoto(orderCode);
    }

    /**
     * 初始化PHA_TRANSUNIT的TDS
     * @param orderCode String
     */
    public void qeuryPhaTransUnit(String orderCode) {
        String sql = "SELECT * FROM PHA_TRANSUNIT WHERE ORDER_CODE = '" +
            orderCode + "'";
        phaTransUnitDS.setSQL(sql);
        phaTransUnitDS.retrieve();

    }

    /**
     * 初始化PHA_BASE的TDS
     * @param orderCode String
     *
     */
    public void qeuryPhaBase(String orderCode) {
        String sql = "SELECT * FROM PHA_BASE WHERE ORDER_CODE = '" + orderCode +
            "'";
        phaBaseDS.setSQL(sql);
        phaBaseDS.retrieve();

    }

    /**
     * 初始化第2个标签页
     */
    public void onTabPanel2() {

        TParm phaTransUnit = phaTransUnitDS.getBuffer(TDS.PRIMARY);
        double mediQty = phaTransUnit.getDouble("MEDI_QTY", 0);
        double dosageQty = phaTransUnit.getDouble("DOSAGE_QTY", 0);
        double stockQty = phaTransUnit.getDouble("STOCK_QTY", 0);
        double purchQty = phaTransUnit.getDouble("PURCH_QTY", 0);
        String mediUnit = phaTransUnit.getValue("MEDI_UNIT", 0);
        String dosageUnit = phaTransUnit.getValue("DOSAGE_UNIT", 0);
        String stockUnit = phaTransUnit.getValue("STOCK_UNIT", 0);
        String purchUnit = phaTransUnit.getValue("PURCH_UNIT", 0);

        MEDI_QTY1.setValue(mediQty);
        DOSAGE_QTY.setValue(dosageQty);
        DOSAGE_QTY1.setValue(1);
        STOCK_QTY.setValue(1);
        STOCK_QTY1.setValue(stockQty);
        PURCH_QTY.setValue(purchQty);
        MEDI_UNIT.setValue(mediUnit);
        DOSAGE_UNIT1.setValue(dosageUnit);
        DOSAGE_UNIT.setValue(dosageUnit);
        STOCK_UNIT.setValue(stockUnit);
        STOCK_UNIT1.setValue(stockUnit);
        PURCH_UNIT.setValue(purchUnit);

        TParm phaBase = phaBaseDS.getBuffer(TDS.PRIMARY);
        String phaType = phaBase.getValue("PHA_TYPE", 0);
        PHA_TYPE1.setSelected("W".equals(phaType));
        PHA_TYPE2.setSelected("C".equals(phaType));
        PHA_TYPE3.setSelected("G".equals(phaType));
        TYPE_CODE.setValue(phaBase.getValue("TYPE_CODE", 0));

        HALF_USE_FLG.setSelected(phaBase.getBoolean("HALF_USE_FLG", 0));
        REFUND_FLG.setSelected(phaBase.getBoolean("REFUND_FLG", 0));
        DSPNSTOTDOSE_FLG.setSelected(phaBase.getBoolean("DSPNSTOTDOSE_FLG", 0));
        GIVEBOX_FLG.setSelected(phaBase.getBoolean("GIVEBOX_FLG", 0));
        REUSE_FLG.setSelected(phaBase.getBoolean("REUSE_FLG", 0));
        ODD_FLG.setSelected(phaBase.getBoolean("ODD_FLG", 0));
        UDCARRY_FLG.setSelected(phaBase.getBoolean("UDCARRY_FLG", 0));
        //=======pangben modify 20110428 start 皮试combo
        SKINTEST_FLG.setSelected(phaBase.getBoolean("SKINTEST_FLG", 0));
        // 处方combo
        PRESCRIPTION_FLG.setSelected(phaBase.getBoolean("PRESCRIPTION_FLG", 0));
        //=======pangben modify 20110428 stop
        CTRLDRUGCLASS_CODE.setValue(phaBase.getValue("CTRLDRUGCLASS_CODE", 0));
        ANTIBIOTIC_CODE.setValue(phaBase.getValue("ANTIBIOTIC_CODE", 0));
        SUP_CODE.setValue(phaBase.getValue("SUP_CODE", 0));

        DOSE_CODE.setValue(phaBase.getValue("DOSE_CODE", 0));
        TAKE_DAYS.setValue(phaBase.getValue("TAKE_DAYS", 0));
        FREQ_CODE.setValue(phaBase.getValue("FREQ_CODE", 0));
        MEDI_QTY.setValue(phaBase.getValue("MEDI_QTY", 0));
        copyMEDI_UNIT.setValue(mediUnit);
        copyDOSAGE_UNIT1.setValue(dosageUnit);
        ROUTE_CODE.setValue(phaBase.getValue("ROUTE_CODE", 0));
        DEFAULT_TOTQTY.setValue(phaBase.getValue("DEFAULT_TOTQTY", 0));

        //只有总量给药选中,则打开默认总量 Modify ZhenQin 2011-06-01
        DEFAULT_TOTQTY.setEnabled(DSPNSTOTDOSE_FLG.isSelected());
        this.DRUG_NOTES_PATIENT.setValue(phaBase.getValue("DRUG_NOTES_PATIENT", 0));
        this.DRUG_NOTES_DR.setValue(phaBase.getValue("DRUG_NOTES_DR", 0));
        this.DRUG_NOTE.setValue(phaBase.getValue("DRUG_NOTE", 0));
        this.DRUG_FORM.setValue(phaBase.getValue("DRUG_FORM", 0));
//        this.DDD.setValue(phaBase.getValue("DDD", 0));
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }

    public void getTabPanel2Vaule() {

        //当前数据库时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        int phaTransUnitRow = 0;
        phaTransUnitDS.showDebug();
        if (phaTransUnitDS.rowCount() == 0) {
            phaTransUnitRow = phaTransUnitDS.insertRow();
        }
        //保存的数据
        phaTransUnitDS.setItem(phaTransUnitRow, "OPT_USER", Operator.getID());
        phaTransUnitDS.setItem(phaTransUnitRow, "OPT_DATE", date);
        phaTransUnitDS.setItem(phaTransUnitRow, "OPT_TERM", Operator.getIP());
        phaTransUnitDS.setItem(phaTransUnitRow, "ORDER_CODE",
                               ORDER_CODE.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "MEDI_QTY", MEDI_QTY1.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "DOSAGE_QTY",
                               DOSAGE_QTY.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "STOCK_QTY", STOCK_QTY1.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "PURCH_QTY", PURCH_QTY.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "MEDI_UNIT", MEDI_UNIT.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "DOSAGE_UNIT",
                               DOSAGE_UNIT.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "STOCK_UNIT",
                               STOCK_UNIT.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "PURCH_UNIT",
                               PURCH_UNIT.getValue());
        //============pangben modify 20110427 start
        phaTransUnitDS.setItem(phaTransUnitRow, "REGION_CODE",
                               this.getValueString("REGION_CODE"));
         //============pangben modify 20110427 stop
        phaTransUnitDS.showDebug();

        int phaBaseRow = 0;
        if (phaBaseDS.rowCount() == 0) {
            phaBaseRow = phaBaseDS.insertRow();
        }

        phaBaseDS.setItem(phaBaseRow, "OPT_USER", Operator.getID());
        phaBaseDS.setItem(phaBaseRow, "OPT_DATE", date);
        phaBaseDS.setItem(phaBaseRow, "OPT_TERM", Operator.getIP());
        phaBaseDS.setItem(phaBaseRow, "ORDER_CODE", ORDER_CODE.getValue());
        String phaType = "";
        if (PHA_TYPE1.isSelected()) {
            phaType = "W";
        }
        else if (PHA_TYPE2.isSelected()) {
            phaType = "C";
        }
        else {
            phaType = "G";
        }
        phaBaseDS.setItem(phaBaseRow, "ALIAS_DESC", ALIAS_DESC.getText());
        phaBaseDS.setItem(phaBaseRow, "ORDER_DESC", ORDER_DESC.getText());
        phaBaseDS.setItem(phaBaseRow, "GOODS_DESC", GOODS_DESC.getText());
        phaBaseDS.setItem(phaBaseRow, "SPECIFICATION", SPECIFICATION.getText());
        phaBaseDS.setItem(phaBaseRow, "DEFAULT_TOTQTY", DEFAULT_TOTQTY.getText());

        phaBaseDS.setItem(phaBaseRow, "DOSAGE_UNIT", DOSAGE_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "STOCK_UNIT", STOCK_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "PURCH_UNIT", PURCH_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "PHA_TYPE", phaType);
        phaBaseDS.setItem(phaBaseRow, "TYPE_CODE", TYPE_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "HALF_USE_FLG", HALF_USE_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "REFUND_FLG", REFUND_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "DSPNSTOTDOSE_FLG",
                          DSPNSTOTDOSE_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "GIVEBOX_FLG", GIVEBOX_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "REUSE_FLG", REUSE_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "ODD_FLG", ODD_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "UDCARRY_FLG", UDCARRY_FLG.getValue());
        //=======pangben modify 20110428 start  皮试combo
        phaBaseDS.setItem(phaBaseRow, "SKINTEST_FLG", SKINTEST_FLG.getValue());
        // 处方combo
        phaBaseDS.setItem(phaBaseRow, "PRESCRIPTION_FLG", PRESCRIPTION_FLG.getValue());
        //=======pangben modify 20110428 stop

        phaBaseDS.setItem(phaBaseRow, "CTRLDRUGCLASS_CODE",
                          CTRLDRUGCLASS_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "ANTIBIOTIC_CODE",
                          ANTIBIOTIC_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "MAN_CHN_DESC", MAN_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "SUP_CODE", SUP_CODE.getValue());

        phaBaseDS.setItem(phaBaseRow, "DOSE_CODE", DOSE_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "TAKE_DAYS", TAKE_DAYS.getValue());
        phaBaseDS.setItem(phaBaseRow, "FREQ_CODE", FREQ_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "MEDI_QTY", MEDI_QTY.getText());
        phaBaseDS.setItem(phaBaseRow, "MEDI_UNIT", MEDI_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "ROUTE_CODE", ROUTE_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "DEFAULT_TOTQTY", DEFAULT_TOTQTY.getValue());
        //采购单位
        phaBaseDS.setItem(phaBaseRow, "PURCH_QTY", PURCH_QTY.getValue());
        //===========pangben modify 20110426 start
        phaBaseDS.setItem(phaBaseRow, "REGION_CODE",this.getValueString("REGION_CODE"));
         //===========pangben modify 20110426 stop
        phaBaseDS.setItem(phaBaseRow, "DRUG_NOTES_PATIENT", DRUG_NOTES_PATIENT.getValue());
        phaBaseDS.setItem(phaBaseRow, "DRUG_NOTES_DR", DRUG_NOTES_DR.getValue());
        phaBaseDS.setItem(phaBaseRow, "DRUG_NOTE", DRUG_NOTE.getValue());
        phaBaseDS.setItem(phaBaseRow, "DRUG_FORM", DRUG_FORM.getValue());
        phaBaseDS.setItem(phaBaseRow, "DDD", DDD.getValue());
        // =========== shendr modify 20140528
        phaBaseDS.setItem(phaBaseRow, "NORMAL_DAYS", NORMAL_DAYS.getValue());
    }


    /**
     * 清空除了table以外的控件的值
     */
    public void clearCtl() {

        this.clearValue(controlNameTab1 + ";ACTIVE_FLG;ORDER_CODE;REFUND_FLG");
        this.clearValue(controlNameTab2 + ";copyMEDI_UNIT;copyDOSAGE_UNIT1");
        StringBuffer cbos=new StringBuffer();
        for(int i=1;i<=20;i++){
            cbos.append("cbo"+i+";");
        }
        //=========pangben modify 20110816 start 清空cbo值
        String cbo=  cbos.toString().substring(0,cbos.toString().lastIndexOf(";"));
        this.clearValue(cbo);
        //=========pangben modify 20110426 start
        this.setValue("REGION_CODE",Operator.getRegion());
         //=========pangben modify 20110426 stop
        PHA_TYPE1.setSelected(true);
    }

    /**
     * 清空操作
     */
    public void onClear() {
        clearCtl();
        //upTable.removeRowAll();
        // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }

    /**
     * 新建
     */
    public void onNew() {
    	// 只有新增的时候才可以编辑单位以及单位转换率
    	isOnNew = true;
    	// 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
        if (outShow) {
            this.messageBox("您没有权限！");
            return;
        }
//        if(!ALL.isSelected()){
//            this.messageBox("请在'全部'状态下新建项目！");
//        }

        //当前数据库时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        clearCtl();
        cerateNewDate();

        //默认给当前时间  
        START_DATE.setValue(date);
        END_DATE.setValue(date);
        //'允许手动记价注记'默认选择
        LET_KEYIN_FLG.setSelected(true);
        //恢复编辑状态
        setEnabledForCtl(true);
        isOnNew = false;
    }

    /**
     * 保存
     */  
    public boolean onSave() {
    	// 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
        if (outShow) {
            this.messageBox("您没有权限！");
            return false;
        } 
        if(onSaveCheck())
            return false;
        double newOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double newNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double newGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
        String nowOrdCode = ORDER_CODE.getValue();

        //判断时间
        String stDate = START_DATE.getText();
        String endDate = END_DATE.getText();
        Timestamp start = (Timestamp) StringTool.getTimestamp(stDate,
            "yyyy/MM/dd HH:mm:ss");
        Timestamp end = (Timestamp) StringTool.getTimestamp(endDate,
            "yyyy/MM/dd HH:mm:ss");
        if (start.getTime() > end.getTime()) {
            this.messageBox("起效日期不可以大于失效日期！");
            return false;
        }

        //拿到当前选中行的数据（要更改和新建的行）
        selRow = upTable.getSelectedRow();
        //取table数据
        TDataStore dataStore = upTable.getDataStore();
        //当拿到的数据位一行，则默认保存就是该行——0
        if(selRow==-1&&dataStore.rowCount()==1)
            selRow=0;
        
        // modify by wangb 2016/4/1
		String region = "";
		if (null != this.getValueString("REGION_CODE")
				&& !"".equals(this.getValueString("REGION_CODE"))) {
			region = " AND (REGION_CODE='" + this.getValueString("REGION_CODE")
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		}
		String sql = "";
		// 配过滤条件 拿到查询code的SQL语句
		if (orderCode != null && orderCode.length() > 0) {
			sql = " SELECT * FROM SYS_FEE_HISTORY WHERE "
					+ " ORDER_CODE like '" + orderCode + "%' "
					+ " AND ACTIVE_FLG='Y' " + region + " ORDER BY ORDER_CODE";
		}
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        
        //如果在代码没有改变的情况下改变了三个价格任一都要在重新生成一条
		if (orderCode.equals(nowOrdCode)
				&& ((newOwnPrice != oldOwnPrice)
						|| (newNhiPrice != oldNhiPrice) || (newGovPrice != oldGovPrice))) {
			if ("N".equals(upTable.getItemString(upTable.getSelectedRow(),
					"ACTIVE_FLG"))
					&& (ACTIVE_N.isSelected() || ALL.isSelected())
					&& parm.getCount() > 0) {
				this.messageBox(parm.getValue("ORDER_CODE") + "已启用,请先停用现行启用的");
				return false;
			}
			
        	//add by lx 
			//$$ add by lx 2012/01/06 没选择要修改的医嘱
			if(selRow==-1){
				this.messageBox("请选择需要修改的医嘱！");
				return false;
			}
        	
            //需要改变日期（true）  
            setDataInTDS(dataStore, selRow, true);
            //插入一行新的数据，拿到行号
            int insertRowNumber = dataStore.insertRow();
            setDataInTDS(dataStore, insertRowNumber, true);
        }
        else {
            //从界面上拿到数据，放到TDS中，用于更新保存
        	//fux 20140212 加入检验如果出现同一医嘱已有启用的，
        	//必须先停用才可以启用提示
			if ("N".equals(upTable.getItemString(upTable.getSelectedRow(),
					"ACTIVE_FLG"))
					&& (ACTIVE_N.isSelected() || ALL.isSelected())) {
				// 因为在未启用界面或者全部界面所以设置设置所有的控件不是可用
				setEnabledForCtl(false);
				if (parm.getCount() > 0) {
					this.messageBox(parm.getValue("ORDER_CODE")
							+ "已启用,请先停用现行启用的");
					return false;
				} else {
					setDataInTDS(dataStore, selRow, false);
				}
			} else {
				setDataInTDS(dataStore, selRow, false);
			}  
        }   
        //fux modify  
        onUpdateIndStock();
        //增加保存SYS_FEE的TDS
//        System.out.println("--------da---------"+setDataInSysFeeTds());
        if (setDataInSysFeeTds())
            bigObject.addDS("SYS_FEE", sysFeeDS);
        //用大对象保存，形成一个事务
        bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);
        getTabPanel2Vaule();
        bigObject.addDS("PHA_TRANSUNIT", phaTransUnitDS);
        bigObject.addDS("PHA_BASE", phaBaseDS);
        if (!bigObject.update()) {
            messageBox("E0001");
            return false;
        }
        
       //add by lx 2013/01/06 刷新一下医嘱表格
		initTblAndTDS(querySQL);
		
        messageBox("P0001");
        //前台刷新
        TIOM_Database.logTableAction("SYS_FEE");
        return true;

    }
         
    public void onUpdateIndStock() {
    	    String active_flg = this.getValueString("ACTIVE_FLG");
    	    String orderCode = this.getValueString("ORDER_CODE");
    		String sql =" UPDATE IND_STOCK " +  
    				    " SET ACTIVE_FLG = '"+active_flg+"'" +    
    				    " WHERE ORDER_CODE = '"+orderCode+"' ";
    		TParm Parm = new TParm(TJDODBTool.getInstance().update(sql));    
//    		System.out.println("sql::::"+sql);
    	}

    /**
     * 得到SYS_FEE的TDS
     */
    private boolean setDataInSysFeeTds() {

        String sql = "";
        String orderCode = ORDER_CODE.getValue();
        //=============pangben modify 20110426 start
        String regionCode=this.getValueString("REGION_CODE");
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"+region;
        //=============pangben modify 20110426 stop
//        System.out.println("-===--=-----2131------------="+sql);
        sysFeeDS.setSQL(sql);
        if (sysFeeDS.retrieve() <= 0)  
            return false;
//        System.out.println("-===--=-----------------=");
        //当前数据库时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //保存的数据
        sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
        sysFeeDS.setItem(0, "OPT_DATE", date);
        sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());
        sysFeeDS.setItem(0, "ORDER_CODE", ORDER_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_DESC", ORDER_DESC.getValue());
        sysFeeDS.setItem(0, "PY1", PY1.getValue());
        sysFeeDS.setItem(0, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
        sysFeeDS.setItem(0, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
        sysFeeDS.setItem(0, "GOODS_DESC", GOODS_DESC.getValue());
        sysFeeDS.setItem(0, "GOODS_PYCODE", GOODS_PYCODE.getValue());
        sysFeeDS.setItem(0, "ALIAS_DESC", ALIAS_DESC.getValue());
        sysFeeDS.setItem(0, "SPECIFICATION", SPECIFICATION.getValue());
        sysFeeDS.setItem(0, "DESCRIPTION", DESCRIPTION.getValue());
        sysFeeDS.setItem(0, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
        sysFeeDS.setItem(0, "HABITAT_TYPE", HABITAT_TYPE.getValue());
        sysFeeDS.setItem(0, "MAN_CODE", MAN_CODE.getValue());
        sysFeeDS.setItem(0, "UNIT_CODE", UNIT_CODE.getValue());

        sysFeeDS.setItem(0, "MR_CODE", MR_CODE.getValue());
        sysFeeDS.setItem(0, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());

        sysFeeDS.setItem(0, "HYGIENE_TRADE_CODE",
                          HYGIENE_TRADE_CODE.getValue());
        sysFeeDS.setItem(0, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());

        sysFeeDS.setItem(0, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
        sysFeeDS.setItem(0, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
        sysFeeDS.setItem(0, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        sysFeeDS.setItem(0, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
        sysFeeDS.setItem(0, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
        sysFeeDS.setItem(0, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        sysFeeDS.setItem(0, "INSPAY_TYPE", INSPAY_TYPE.getValue());
        sysFeeDS.setItem(0, "ADDPAY_RATE", ADDPAY_RATE.getValue());
        sysFeeDS.setItem(0, "ADDPAY_AMT", ADDPAY_AMT.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_I", NHI_CODE_I.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_O", NHI_CODE_O.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_E", NHI_CODE_E.getValue());
        // <---- modify by shendr 20140313
        /*
        if(!"".equals(CTRLDRUGCLASS_CODE.getValue()))
            sysFeeDS.setItem(0, "CTRL_FLG", "Y");
        else
            sysFeeDS.setItem(0, "CTRL_FLG", "N");
        */
        if(!"".equals(CTRLDRUGCLASS_CODE.getValue()))
        	sysFeeDS.setItem(0, "CTRL_FLG", getCtrlFlg(CTRLDRUGCLASS_CODE.getValue()));
        else
        	sysFeeDS.setItem(0, "CTRL_FLG", "N");
        // ---->
        sysFeeDS.setItem(0, "ATC_FLG",getValue("ATC_FLG"));
        sysFeeDS.setItem(0, "ATC_FLG_I",getValue("ATC_FLG_I"));
        sysFeeDS.setItem(0, "CAT1_TYPE",getCta1Type("" + ORDER_CAT1_CODE.getValue()));
        sysFeeDS.setItem(0, "ACTIVE_FLG",getValue("ACTIVE_FLG"));
        sysFeeDS.setItem(0, "IS_REMARK",getValue("IS_REMARK"));
//        //============pangben modify 20110426 start
        sysFeeDS.setItem(0, "REGION_CODE",getValue("REGION_CODE"));
//         //============pangben modify 20110426 stop
        //============pangben modify 20110816 start 药物种类，零差价
        sysFeeDS.setItem(0, "SYS_GRUG_CLASS",getValue("SYS_GRUG_CLASS"));
        sysFeeDS.setItem(0, "NOADDTION_FLG",getValue("NOADDTION_FLG"));
        StringBuffer cbos=new StringBuffer();
        for (int i = 1; i <= 20; i++) {
            if (this.getValue("cbo" + i).equals("Y")) {
                cbos.append(i + ";");
            }
        }
        String cbo=cbos.toString().trim().length()>0?cbos.substring(0,cbos.lastIndexOf(";")):"";
        sysFeeDS.setItem(0, "SYS_PHA_CLASS", cbo);
        //============pangben modify 20110816 start
        sysFeeDS.setItem(0, "DRUG_NOTES_PATIENT", DRUG_NOTES_PATIENT.getValue());
        sysFeeDS.setItem(0, "DRUG_NOTES_DR", DRUG_NOTES_DR.getValue());
        sysFeeDS.setItem(0, "DRUG_NOTE", DRUG_NOTE.getValue());
        sysFeeDS.setItem(0, "DRUG_FORM", DRUG_FORM.getValue());
        sysFeeDS.setItem(0, "DDD", DDD.getValue());
        sysFeeDS.setItem(0, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());
        return true;
    }
    
    /**
     * 获得毒麻药品标志CTRL_FLG
     * @param ctrldrugclass_code
     * @author shendr
     * @return
     */
    private String getCtrlFlg(String ctrldrugclass_code){
    	String SQL = "SELECT CTRL_FLG FROM SYS_CTRLDRUGCLASS WHERE CTRLDRUGCLASS_CODE = '"+ctrldrugclass_code+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        return parm.getValue("CTRL_FLG",0);
    }

    /**
     * 取得医令细分类
     * @param orderCat1Type String
     * @return String
     */
    private String getCta1Type(String orderCat1Type){
        String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"+orderCat1Type+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getErrCode() != 0)
            return "";
        return parm.getValue("CAT1_TYPE",0);
    }

    /**
     * 取得数据库访问类
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }

    /**
     * 收集界面上的值保存使用（更新保存）
     * @param dataStore TDataStore
     */
    public void setDataInTDS(TDataStore dataStore, int row, boolean dateFlg) {
        //当前数据库时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //保存的数据
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());

        //如果修改日期标记为假的时候，两个日期按界面上显示的保存
        String tempStartDate;
        String tempEndDate;
        if (!dateFlg) {
            tempStartDate = START_DATE.getText();
            String startDate = tempStartDate.substring(0, 4) +
                tempStartDate.substring(5, 7) + tempStartDate.substring(8, 10) +
                tempStartDate.substring(11, 13) +
                tempStartDate.substring(14, 16) +
                tempStartDate.substring(17, 19);
            dataStore.setItem(row, "START_DATE", startDate);

            tempEndDate = END_DATE.getText();
            String endDate = tempEndDate.substring(0, 4) +
                tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10) +
                tempEndDate.substring(11, 13) +
                tempEndDate.substring(14, 16) +
                tempEndDate.substring(17, 19);
            dataStore.setItem(row, "END_DATE", endDate);

            //执行标记不变
            dataStore.setItem(row, "ACTIVE_FLG", ACTIVE_FLG.getValue());
            dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
            dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
            dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());

        }
        else { //如果为真START_DATE
            Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
            if (row == selRow) { //如果传如的行号等于被选中的行号说明是需要更新的那行
                tempEndDate = StringTool.getString(nowTime,
                    "yyyy/MM/dd HH:mm:ss");
                String endDate = tempEndDate.substring(0, 4) +
                    tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10) +
                    tempEndDate.substring(11, 13) +
                    tempEndDate.substring(14, 16) +
                    tempEndDate.substring(17, 19);
                dataStore.setItem(row, "END_DATE", endDate);

                //执行标记变为假
                dataStore.setItem(row, "ACTIVE_FLG", 'N');
                //作废的那条数据记录老的价钱（其他的都是新的值）
                dataStore.setItem(row, "OWN_PRICE", oldOwnPrice);
                dataStore.setItem(row, "OWN_PRICE2", oldNhiPrice);
                dataStore.setItem(row, "OWN_PRICE3", oldGovPrice);
            }
            else {
                tempStartDate = StringTool.getString(nowTime,
                    "yyyy/MM/dd HH:mm:ss");
                String startDate = tempStartDate.substring(0, 4) +
                    tempStartDate.substring(5, 7) +
                    tempStartDate.substring(8, 10) +
                    tempStartDate.substring(11, 13) +
                    tempStartDate.substring(14, 16) +
                    tempStartDate.substring(17, 19);
                dataStore.setItem(row, "START_DATE", startDate);

                tempEndDate = END_DATE.getText();
                String endDate = tempEndDate.substring(0, 4) +
                    tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10) +
                    tempEndDate.substring(11, 13) +
                    tempEndDate.substring(14, 16) +
                    tempEndDate.substring(17, 19);
                dataStore.setItem(row, "END_DATE", endDate);

                //执行标记变为真
                dataStore.setItem(row, "ACTIVE_FLG", 'Y');
                dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
                dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
                dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());
            }
        }

        dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
        dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
        dataStore.setItem(row, "PY1", PY1.getValue());
        dataStore.setItem(row, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
        dataStore.setItem(row, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
        dataStore.setItem(row, "GOODS_DESC", GOODS_DESC.getValue());
        dataStore.setItem(row, "GOODS_PYCODE", GOODS_PYCODE.getValue());
        dataStore.setItem(row, "ALIAS_DESC", ALIAS_DESC.getValue());
        dataStore.setItem(row, "SPECIFICATION", SPECIFICATION.getValue());
        dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
        dataStore.setItem(row, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
        dataStore.setItem(row, "HABITAT_TYPE", HABITAT_TYPE.getValue());
        dataStore.setItem(row, "MAN_CODE", MAN_CODE.getValue());
        dataStore.setItem(row, "UNIT_CODE", UNIT_CODE.getValue());

        dataStore.setItem(row, "MR_CODE", MR_CODE.getValue());
        dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());

        dataStore.setItem(row, "HYGIENE_TRADE_CODE",
                          HYGIENE_TRADE_CODE.getValue());
        dataStore.setItem(row, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());

        dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        dataStore.setItem(row, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
        dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        dataStore.setItem(row, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
        dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        dataStore.setItem(row, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
        dataStore.setItem(row, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
        dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        dataStore.setItem(row, "INSPAY_TYPE", INSPAY_TYPE.getValue());
        dataStore.setItem(row, "ADDPAY_RATE", ADDPAY_RATE.getValue());
        dataStore.setItem(row, "ADDPAY_AMT", ADDPAY_AMT.getValue());
        dataStore.setItem(row, "NHI_CODE_I", NHI_CODE_I.getValue());
        dataStore.setItem(row, "NHI_CODE_O", NHI_CODE_O.getValue());
        dataStore.setItem(row, "NHI_CODE_E", NHI_CODE_E.getValue());
        if(!"".equals(CTRLDRUGCLASS_CODE.getValue()))
            dataStore.setItem(row, "CTRL_FLG", "Y");
        else
            dataStore.setItem(row, "CTRL_FLG", "N");
        dataStore.setItem(row, "ATC_FLG", getValue("ATC_FLG"));
        dataStore.setItem(row, "ATC_FLG_I", getValue("ATC_FLG_I"));
        dataStore.setItem(row, "IS_REMARK",getValue("IS_REMARK"));
        dataStore.setItem(row, "CAT1_TYPE",getCta1Type("" + getValue("ORDER_CAT1_CODE")));
        //=============pangben modify 20110426 start
        dataStore.setItem(row, "REGION_CODE",getValue("REGION_CODE"));
        //=============pangben modify 20110426 stop
        //============pangben modify 20110816 start 药物种类，零差价
       dataStore.setItem(row, "SYS_GRUG_CLASS",getValue("SYS_GRUG_CLASS"));
       dataStore.setItem(row, "NOADDTION_FLG",getValue("NOADDTION_FLG"));
       dataStore.setItem(row, "DRUG_NOTES_PATIENT",getValue("DRUG_NOTES_PATIENT"));
       dataStore.setItem(row, "DRUG_NOTES_DR",getValue("DRUG_NOTES_DR"));
       dataStore.setItem(row, "DRUG_NOTE",getValue("DRUG_NOTE"));
       dataStore.setItem(row, "DRUG_FORM",getValue("DRUG_FORM"));
       dataStore.setItem(row, "DDD",getValue("DDD"));
       dataStore.setItem(row, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());
       StringBuffer cbos=new StringBuffer();
       for(int i=1;i<=20;i++){
         if(this.getValue("cbo"+i).equals("Y")){
             cbos.append(i+";");
         }
       }

       String cbo=cbos.toString().trim().length()>0?cbos.substring(0,cbos.lastIndexOf(";")):"";
       dataStore.setItem(row, "SYS_PHA_CLASS",cbo);
       //============pangben modify 20110816 start

    }

    /**
     * 当新建的时候自动生成编码号的主方法
     */
    public void cerateNewDate() {
        String newCode = "";
        //接收文本
        upTable.acceptText();
        //取table数据
        //========pangben modify 20110427 start 注释去掉不需要查询表中的最大编号，通过查询数据库中的的数值显示最大编号
        // TDataStore dataStore = upTable.getDataStore();
        // String maxCode = getMaxCode(dataStore, "ORDER_CODE");
        //System.out.println("maxCode:"+maxCode);
        //========pangben modify 20110427 start
        //找到选中的树节点
        TTreeNode node = tree.getSelectionNode();
        //如果没有选中的节点
        if (node == null)
            return;
        //得到当前ID
        String nowID = node.getID();
        int classify = 1;
        //如果点的是树的父节点存在,得到当前编码规则
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //如果是最小节点,可以增加一行
        if (classify > ruleTool.getClassifyCurrent()) {
            //得到默认的自动添加的医嘱代码
            //===pangben modify 20110427 start
            //============查找此编号规则中编号最大值
            String sql =
                    "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM PHA_BASE WHERE ORDER_CODE LIKE '" +
                    nowID + "%'";
            TParm parm = new TParm(getDBTool().select(sql));
            String maxCode = parm.getValue("ORDER_CODE", 0);
            //===pangben modify 20110427 start
            //找到最大科室代码(dataStore,列名)
            String no = ruleTool.getNewCode(maxCode, classify);
            newCode = nowID + no;
            //得到新添加的table数据行号(相当于TD中的insertRow()方法)
            int row = upTable.addRow();
            //设置当前选中行为添加的行
            upTable.setSelectedRow(row);
            //给科室代码添加默认值
            upTable.setItem(row, "ORDER_CODE", newCode);
            //默认科室名称
            upTable.setItem(row, "ORDER_DESC", "(新建名称)");
            //默认科室简称
            upTable.setItem(row, "SPECIFICATION", null);
            //默认科室
            upTable.setItem(row, "OWN_PRICE", null);
            //默认最小科室注记
            upTable.setItem(row, "UNIT_CODE", null);
            //========pangben modify 20110512 start
            //区域
            upTable.setItem(row, "REGION_CODE", Operator.getRegion());
            //========pangben modify 20110512 start
        }
        //把自动生成的orderCode设置到ORDER_CODE上
        ORDER_CODE.setText(newCode);

        //查询PHA_TRANSUNIT生成TDS
        qeuryPhaTransUnit(newCode);
        qeuryPhaBase(newCode);

    }


    /**
     * 得到最大的编号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */ 
    public String getMaxCode(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return "";
        String s = "";
        if (dataStore.isFilter()) {
            TParm a = dataStore.getBuffer(TDataStore.FILTER);
            int count = a.getCount();
            for (int i = 0; i < count; i++) {
                String value = a.getValue(columnName, i);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        else {
            int count = dataStore.rowCount();
            for (int i = 0; i < count; i++) {
                String value = dataStore.getItemString(i, columnName);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        return s;
    }


    /**
     * 根据table上的某一行数据给下面的控件初始化值
     * @param date TParm
     */
    public void setValueForDownCtl(TParm date) {
        clearCtl();
        this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
        Timestamp start = StringTool.getTimestamp(date.getValue("START_DATE"),
                                                  "yyyyMMddHHmmss");
        this.setValue("START_DATE", start);
        Timestamp end = StringTool.getTimestamp(date.getValue("END_DATE"),
                                                "yyyyMMddHHmmss");
        this.setValue("END_DATE", end);

        this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
        this.setValue("PY1", date.getValue("PY1"));
        this.setValue("NHI_FEE_DESC", date.getValue("NHI_FEE_DESC"));
        this.setValue("TRADE_ENG_DESC", date.getValue("TRADE_ENG_DESC"));
        this.setValue("GOODS_DESC", date.getValue("GOODS_DESC"));
        this.setValue("GOODS_PYCODE", date.getValue("GOODS_PYCODE"));
        this.setValue("ALIAS_DESC", date.getValue("ALIAS_DESC"));
        this.setValue("SPECIFICATION", date.getValue("SPECIFICATION"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
        this.setValue("HABITAT_TYPE", date.getValue("HABITAT_TYPE"));
        this.setValue("MAN_CODE", date.getValue("MAN_CODE"));
        this.setValue("ALIAS_PYCODE", date.getValue("ALIAS_PYCODE"));
        this.setValue("UNIT_CODE", date.getValue("UNIT_CODE"));
        this.setValue("MR_CODE", date.getValue("MR_CODE"));

        this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
        this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
        this.setValue("OWN_PRICE", date.getValue("OWN_PRICE"));
        this.setValue("OWN_PRICE2", date.getValue("OWN_PRICE2"));
        this.setValue("OWN_PRICE3", date.getValue("OWN_PRICE3"));
        this.setValue("HYGIENE_TRADE_CODE",
                      date.getValue("HYGIENE_TRADE_CODE"));
        this.setValue("LET_KEYIN_FLG", date.getValue("LET_KEYIN_FLG"));
        this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
        this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
        this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
        this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
        this.setValue("DR_ORDER_FLG", date.getValue("DR_ORDER_FLG"));
        this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
        this.setValue("EXEC_ORDER_FLG", date.getValue("EXEC_ORDER_FLG"));
        this.setValue("LCS_CLASS_CODE", date.getValue("LCS_CLASS_CODE"));
        this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
        this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));

        this.setValue("INSPAY_TYPE", date.getValue("INSPAY_TYPE"));
        this.setValue("ADDPAY_RATE", date.getValue("ADDPAY_RATE"));
        this.setValue("ADDPAY_AMT", date.getValue("ADDPAY_AMT"));
        this.setValue("NHI_CODE_I", date.getValue("NHI_CODE_I"));
        this.setValue("NHI_CODE_O", date.getValue("NHI_CODE_O"));
        this.setValue("NHI_CODE_E", date.getValue("NHI_CODE_E"));
        this.setValue("ATC_FLG", date.getValue("ATC_FLG"));
        this.setValue("ATC_FLG_I", date.getValue("ATC_FLG_I"));
        this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
        //===========pangben modify 20110426 start
        this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
        //===========pangben modify 20110816
        this.setValue("SYS_GRUG_CLASS", date.getValue("SYS_GRUG_CLASS"));
        this.setValue("NOADDTION_FLG", date.getValue("NOADDTION_FLG"));
        String[] cbos = date.getValue("SYS_PHA_CLASS").split(";");
//        for (int index = 1; index <= 20; index++) {
//            for (int i = 0; i < cbos.length; i++) {
//                if (cbos[i].equals(this.getText("cbo" + index))){
//                    this.setValue("cbo" + index, "Y");
//                    break;
//                }
//            }
//        }
        for (int i = 0; i < cbos.length; i++) {
        	String cTag = "cbo" + cbos[i];
        	String cText = this.getText(cTag)==null?"":this.getText(cTag).trim();
            if (!"".equals(cText)){
                this.setValue(cTag, "Y");
            }
        }
        this.setValue("DRUG_NOTES_PATIENT",date.getValue("DRUG_NOTES_PATIENT"));
        this.setValue("DRUG_NOTES_DR",date.getValue("DRUG_NOTES_DR"));
        this.setValue("DRUG_NOTE",date.getValue("DRUG_NOTE"));
        this.setValue("DRUG_FORM",date.getValue("DRUG_FORM"));
        this.setValue("DDD",date.getValue("DDD"));
        this.setValue("ORD_SUPERVISION",date.getValue("ORD_SUPERVISION"));
    }

    /**
     * 选择TRANS_OUT_FLG标记
     */
    public void onOutHosp() {
        String value = TRANS_OUT_FLG.getValue();
        TRANS_HOSP_CODE.setEnabled(TypeTool.getBoolean(value));
        if (!TypeTool.getBoolean(value)) {
            TRANS_HOSP_CODE.setText("");
        }
    }

    /**
     * 选择开单即执行标记
     */
    public void onExrOrd() {
        String value = EXEC_ORDER_FLG.getValue();
        //不挑勾的时候
        EXEC_DEPT_CODE.setEnabled(TypeTool.getBoolean(value));
        //清空值
        if (!TypeTool.getBoolean(value)) {
            EXEC_DEPT_CODE.setText("");
        }

    }

    /**
     * 激发自费价事件
     */
    public void onOwnPrice() {
        //得到当前自费价格
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());
        //默认医保/政府最高价等于自费价格
//        if (nhiPrice > ownPrice || nhiPrice == 0)
            NHI_PRICE.setValue(ownPrice * 2);
//        if (govPrice < ownPrice || govPrice == 0)
            GOV_PRICE.setValue(ownPrice * 2.5);
        //移动光标
        NHI_PRICE.grabFocus();
    }

    /**
     * 激发医保价事件
     */
    public void onNhiPrice() {/*
        //得到当前医保价格
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());

        //如果医保价格大于自费价格
//        if (ownPrice < nhiPrice || nhiPrice == 0)
            OWN_PRICE.setValue(nhiPrice*0.5);
        //如果最高政府价格小于医保价格
//        if (govPrice < nhiPrice || govPrice == 0)
            GOV_PRICE.setValue(nhiPrice*1.25);
        //移动光标
        GOV_PRICE.grabFocus();*/
    }

    /**
     * 激发政府最高价事件
     */
    public void onGovPrice() {/*
        //得到当前自费价格
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());

        //如果自费价格大于政府最高价格
//        if (ownPrice > govPrice || ownPrice == 0)
            OWN_PRICE.setValue(govPrice*0.4);
        //如果医保价格大于政府最高价格
//        if (nhiPrice > govPrice || govPrice == 0)
            NHI_PRICE.setValue(govPrice*0.8);
        //移动光标
        HYGIENE_TRADE_CODE.grabFocus();*/
    }

    /**
     * 得到名称拼音
     */
    public void onPY1() {
        String orderDesc = ORDER_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        PY1.setText(orderPy);
        NHI_FEE_DESC.grabFocus();
    }

    /**
     * 得到别名拼音
     */
    public void onPY2() {
        String aliasDesc = ALIAS_DESC.getText();
        String aliasPy = SYSHzpyTool.getInstance().charToCode(aliasDesc);
        ALIAS_PYCODE.setText(aliasPy);
        SPECIFICATION.grabFocus();
    }

    /**
     * 化学名拼音
     */
    public void onPY3() {
        String orderDesc = GOODS_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        GOODS_PYCODE.setText(orderPy);
        ALIAS_DESC.grabFocus();
    }


    /**
     * 历史明细按钮
     * @param args String[]
     */
    public void onHistory() {

        String orderCode = ORDER_CODE.getText();
        if ("".equals(orderCode)) {
            this.messageBox("请选择要查看的项目！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        //======pangben modify 20110426 start 添加参数
         parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //======pangben modify 20110426 stop
        //以模态的形式打开窗口： 路径 参数
        TParm returnData = (TParm)this.openDialog(
            "%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_HISTORY.x", parm);

        if (returnData != null) {
            setValueForDownCtl(returnData);
            //从历史中调出的数据不可以修改
            setEnabledForCtl(false);
        }
    }

    /**
     * 设置所有的控件是否可用（历史数据不可以修改）
     * @param flg boolean
     */
    public void setEnabledForCtl(boolean flg) {
        //如果是从外部接口调用
        if (outShow) {
            //附加关闭‘启动’
            ACTIVE_FLG.setEnabled(false);
            return;
        }
        //拿到所有控件的Tag名字
        String allCtlTag = controlNameTab1 + ";" + controlNameTab2;
        String tag[] = allCtlTag.split(";");
        int count = tag.length;
        for (int i = 0; i < count; i++) {
            this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
        }
        //===========pangben modify 20110816
        for(int i=1;i<=20;i++ ){
            this.callFunction("UI|cbo" + i + "|setEnabled", flg);
        }
        ACTIVE_FLG.setEnabled(true);
    }


    public void onExe() {
        Timestamp time = TJDODBTool.getInstance().getDBTime();
        //如果开始时间为空，则默认给当前时间
        if ("".equals(TypeTool.getString(START_DATE.getValue()))) {
            START_DATE.setValue(time);
        }

        //决定启用的时候‘默认’把失效日期写为"9999/12/31 23:59:59"
        if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
        	//modify by wangjc 20180112 重新启用时，开始时间为停用时间+1秒
        	try {
        		TParm tableDate = ( (TDS) this.upTable.getDataStore()).getBuffer(TDS.
        	            PRIMARY).getRow(this.selRow);
        		if("99991231235959".equals(tableDate.getValue("END_DATE"))){
        			START_DATE.setValue(time);
        		}else{
        			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        			Date date = sdf.parse(tableDate.getValue("END_DATE"));
        			Calendar c = Calendar.getInstance();
        			c.setTime(date);
        			c.add(Calendar.SECOND, 1);
        			date = c.getTime();
        			START_DATE.setValue(date);
        		}
        	} catch (Exception e) {
        		// TODO: handle exception
        		e.printStackTrace();
        	}
            END_DATE.setText("9999/12/31 23:59:59");
        }
        else {
            String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
            END_DATE.setText(date);
        }

        //恢复编辑状态
        setEnabledForCtl(true);
    }

    public void onQuery() {
        String selCode = ORDER_CODE.getValue();
        if ("".equals(selCode)) {
            this.messageBox("请输入要查询项目的编码！");
        }

        //拿到查询code的SQL语句
        String sql = getSQL(selCode,this.getValueString("REGION_CODE"));//======pangben modify 20110426 添加参数
      //add by lx 2013/01/06
		querySQL=sql;
        //初始化table和TDS
        initTblAndTDS(sql);
        //当查询结果只有一条数据的时候，直接显示其详细信息
        if (upTable.getRowCount() == 1) {
            onUpTableClicked(0);
        }
      this.viewPhoto(selCode);
      // <-------------- identify by shendr 20131023 设定一个角色，可以修改单位及单位转换率（目前设定为药剂科主任）
      // 参数
      String string = this.getParameter().toString();
      // 登录用户的角色
      String role = Operator.getRole();
      if (string.equals(role)) {
    	isOnNew = true;
      }
      // 判断是否新增，决定是否disable单位及单位转换率 <----------  identify by shendr 2013.09.03
      checkIsOnNew();
      // ---------->
      // ------------->
      TParm normalsDaysParm = getNormalDays(selCode);
      NORMAL_DAYS.setValue(normalsDaysParm.getInt("NORMAL_DAYS",0));
    }
    
    /**
     * 获取常规销售期字段
     * @param order_code
     * @return
     */
    private TParm getNormalDays(String order_code){
    	String sql = "SELECT NORMAL_DAYS FROM PHA_BASE WHERE ORDER_CODE = '"+order_code+"'";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }


    /**
     * table双击选中树
     * @param row int
     */
    public void onTableDoubleCleck() {

        upTable.acceptText();
        int row = upTable.getSelectedRow();
        String value = upTable.getItemString(row, 0);
        //得到上层编码
        String partentID = ruleTool.getNumberParent(value);
        TTreeNode node = treeRoot.findNodeForID(partentID);
        if (node == null)
            return;
        //得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //设置树的选中节点
        tree.setSelectNode(node);
        tree.update();
        //调用查询事件
        //======pangben modify 20110426 start 添加参数
        onCleckClassifyNode(partentID, upTable,this.getValueString("REGION_CODE"));
        //======pangben modify 20110426 stop
        //table中设置选中行
        int count = upTable.getRowCount(); //table的行数
        for (int i = 0; i < count; i++) {
            //拿到物资代码
            String invCode = upTable.getItemString(i, 0);
            if (value.equals(invCode)) {
                //设置选中行
                upTable.setSelectedRow(i);
                return;
            }
        }
    }

    /**
     * 选中对应树节点的事件
     * @param parentID String
     * @param table TTable
     */
    public void onCleckClassifyNode(String parentID, TTable table,String regionCode) {
        //========pangben modify 20110426 start
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
             region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        //table中的datastore中查询数据sql
        table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '" +
                     parentID +
                     "%'"+region);
        //========pangben modify 20110426 stop
        //查找数据
        table.retrieve();
        //放置数据到table中
        table.setDSValue();
        //设置新增按钮
        callFunction("UI|new|setEnabled", true);

    }

    /**
     * 设置单位
     * @param flg Object
     */

    public void onCopy(Object flg) {
        String tag = TypeTool.getString(flg);
        if ("MEDI_UNIT".equals(tag)) {
            copyMEDI_UNIT.setValue(MEDI_UNIT.getValue());
        }
        else if ("DOSAGE_UNIT1".equals(tag)) {
            copyDOSAGE_UNIT1.setValue(DOSAGE_UNIT1.getValue());
        }
    }

    /**
     * 过滤
     * @param ob Object
     */
    public void onFilter(Object ob) {
        if ("ALL".equals(ob.toString())) {
            upTable.setFilter("");
            upTable.filter();
            //放置数据到table中
            upTable.setDSValue();
            //设置新增按钮
            callFunction("UI|new|setEnabled", true);
        }
        else if ("Y".equals(ob.toString())) {
            upTable.setFilter("ACTIVE_FLG='Y'");
            upTable.filter();
            //放置数据到table中
            upTable.setDSValue();
            //设置新增按钮
            callFunction("UI|new|setEnabled", true);
        }
        else {
            upTable.setFilter("ACTIVE_FLG='N'");
            upTable.filter();
            //放置数据到table中
            upTable.setDSValue();
            //设置新增按钮
            callFunction("UI|new|setEnabled", false);
        }
    }


    /**
     * 返回看大对象的SQL用语事务连接
     * @return String[]
     */
    public String[] getSql() {

        return StringTool.copyArray(StringTool.copyArray(bigObject.getDS(
            "SYS_FEE").getUpdateSQL(),
            bigObject.getDS("PHA_TRANSUNIT").getUpdateSQL()),
                                    bigObject.getDS("PHA_BASE").getUpdateSQL());
    }

    public boolean onSaveCheck(){
        if(getValueString("START_DATE").length() == 0){
            messageBox("生效日期不可为空");
            return true;
        }
        if(getValueString("END_DATE").length() == 0){
            messageBox("失效日期不可为空");
            return true;
        }
        if(getValueString("ORDER_CODE").length() == 0){
            messageBox("药品代码不可为空");
            return true;
        }
        if(getValueString("ORDER_DESC").length() == 0){
            messageBox("药品名称不可为空");
            return true;
        }
        if(getValueString("PY1").length() == 0){
            messageBox("名称拼音不可为空");
            return true;
        }
        if(getValueString("UNIT_CODE").length() == 0){
            messageBox("单位不可为空");
            return true;
        }
        if(getValueString("CHARGE_HOSP_CODE").length() == 0){
            messageBox("院内代码不可为空");
            return true;
        }
        if(getValueString("ORDER_CAT1_CODE").length() == 0){
            messageBox("细分类不可为空");
            return true;
        }
        if(getValueString("OWN_PRICE").length() == 0){
            messageBox("自费价不可为空");
            return true;
        }
        if(getValueString("OWN_PRICE2").length() == 0){
            messageBox("贵宾价不可为空");
            return true;
        }
        if(getValueString("OWN_PRICE3").length() == 0){
            messageBox("国际医疗价不可为空");
            return true;
        }
//        if(getValueString("EXEC_DEPT_CODE").length() == 0){
//            messageBox("处理部门不可为空");
//            return true;
//        }
        if(getValueString("TYPE_CODE").length() == 0){
            messageBox("药品分类不可为空");
            return true;
        }
        if(getValueString("MEDI_QTY1").length() == 0){
            messageBox("请核实开药单位换算成配药单位信息");
            return true;
        }
        if(getValueString("MEDI_UNIT").length() == 0){
            messageBox("请核实开药单位换算成配药单位信息");
            return true;
        }
        if(getValueString("DOSAGE_QTY1").length() == 0){
            messageBox("请核实开药单位换算成配药单位信息");
            return true;
        }
        if(getValueString("DOSAGE_UNIT1").length() == 0){
            messageBox("请核实开药单位换算成配药单位信息");
            return true;
        }
        if(getValueString("DOSAGE_QTY").length() == 0){
            messageBox("请核实配药单位换算成库存单位信息");
            return true;
        }
        if(getValueString("DOSAGE_UNIT").length() == 0){
            messageBox("请核实配药单位换算成库存单位信息");
            return true;
        }
        if(getValueString("STOCK_QTY").length() == 0){
            messageBox("请核实配药单位换算成库存单位信息");
            return true;
        }
        if(getValueString("STOCK_UNIT").length() == 0){
            messageBox("请核实配药单位换算成库存单位信息");
            return true;
        }
        if(getValueString("STOCK_QTY1").length() == 0){
            messageBox("请核实库存单位换算成进货单位信息");
            return true;
        }
        if(getValueString("STOCK_UNIT1").length() == 0){
            messageBox("请核实库存单位换算成进货单位信息");
            return true;
        }
        if(getValueString("PURCH_QTY").length() == 0){
            messageBox("请核实库存单位换算成进货单位信息");
            return true;
        }
        if(getValueString("PURCH_UNIT").length() == 0){
            messageBox("请核实库存单位换算成进货单位信息");
            return true;
        }
        if(getValueString("DOSE_CODE").length() == 0){
            messageBox("剂型不可为空");
            return true;
        }
        if(getValueString("FREQ_CODE").length() == 0){
            messageBox("频次不可为空");
            return true;
        }
        if(getValueString("ROUTE_CODE").length() == 0){
            messageBox("用法不可为空");
            return true;
        }
        if(getValueString("TAKE_DAYS").length() == 0){
            messageBox("常用天数不可为空");
            return true;
        }
        if(getValueString("MEDI_QTY").length() == 0){
            messageBox("常用剂量不可为空");
            return true;
        }
        if(getValueString("DEFAULT_TOTQTY").length() == 0){
            messageBox("默认总量不可为空");
            return true;
        }
       return false;
    }

    /**
     * 当在总量给药注记上发生点击时,选中总量给药,则打开默认总量,否则设置默认总量为只读
     */
    public void dspnstotdoseSelect(){
        if(DSPNSTOTDOSE_FLG.isSelected()){
            DEFAULT_TOTQTY.setEnabled(true);
        } else {
            DEFAULT_TOTQTY.setValue(0);
            DEFAULT_TOTQTY.setEnabled(false);
        }
    }
    /**
	 * 拍照
	 * 
	 * @throws IOException
	 */
	public void onPhoto() throws IOException {

		String orderCode = getValue("ORDER_CODE").toString();
		String photoName = orderCode + ".jpg";
		String dir = TIOM_FileServer.getPath("PHAInfoPic.LocalPath");
		new File(dir).mkdirs();
		JMStudio jms = JMStudio.openCamera(dir + photoName);
		jms.addListener("onCameraed", this, "sendpic");
	}

	/**
	 * //注册照相组件
	 */
	public void onRegist() {
		// 注册照相组件
		JMFRegistry jmfr = new JMFRegistry();
		jmfr.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				event.getWindow().dispose();
				System.exit(0);
			}

		});
		jmfr.setVisible(true);
	}

	/**
	 * 传送照片
	 * 
	 * @param image
	 *            Image
	 */
	public void sendpic(Image image) {
		String orderCode = getValue("ORDER_CODE").toString();
		String photoName = orderCode + ".jpg";
		String dir = TIOM_FileServer.getPath("PHAInfoPic.LocalPath");
		String localFileName = dir + photoName;
		try {
			byte[] data = FileTool.getByte(localFileName);
			new File(localFileName).delete();

			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
			dir = root + dir;

			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data);
		} catch (Exception e) {
		}
		this.viewPhoto(orderCode);
	}

	/**
	 * 显示photo
	 * 
	 * @param mrNo
	 *            String 病案号
	 */
	public void viewPhoto(String orderCode) {

		String photoName = orderCode + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
			dir = root + dir;

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null){
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			// Image image = ImageTool.getImage(data);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
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
    //测试用例
    public static void main(String[] args) {
        JavaHisDebug.initClient();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_PHA.x");
    }
    
    /**
     * 判断是否新增
     * @author shendr 2013.08.09
     */
    public void checkIsOnNew(){
    	if(isOnNew){
    		MEDI_QTY1.setEnabled(true);
    		DOSAGE_QTY1.setEnabled(true);
    		DOSAGE_QTY.setEnabled(true);
    		STOCK_QTY.setEnabled(true);
    		STOCK_QTY1.setEnabled(true);
    		PURCH_QTY.setEnabled(true);
    		MEDI_UNIT.setEnabled(true);
    		DOSAGE_UNIT1.setEnabled(true);
    		DOSAGE_UNIT.setEnabled(true);
    		STOCK_UNIT.setEnabled(true);
    		STOCK_UNIT1.setEnabled(true);
    		PURCH_UNIT.setEnabled(true);
    	}else{
    		MEDI_QTY1.setEnabled(false);
    		DOSAGE_QTY1.setEnabled(false);
    		DOSAGE_QTY.setEnabled(false);
    		STOCK_QTY.setEnabled(false);
    		STOCK_QTY1.setEnabled(false);
    		PURCH_QTY.setEnabled(false);
    		MEDI_UNIT.setEnabled(false);
    		DOSAGE_UNIT1.setEnabled(false);
    		DOSAGE_UNIT.setEnabled(false);
    		STOCK_UNIT.setEnabled(false);
    		STOCK_UNIT1.setEnabled(false);
    		PURCH_UNIT.setEnabled(false);
    	}
    }

}
