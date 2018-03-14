package com.javahis.ui.inv;

import javax.print.DocFlavor.STRING;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import jdo.inv.InvBaseTool;
  
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;  
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.SystemTool;  
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.TMessage;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**    
 * <p>Title: 物资字典</p>
 *
 * <p>Description: </p> 
 *  
 * <p>Copyright: Copyright (c) 2008</p>
 * 
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0   
 */
public class INVBaseControl
    extends TControl {
    public INVBaseControl() {
    }

    private TTable table;

    private TTree tree;

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        // 初始化树结构
        onInitTree();
        table = this.getTable("TABLE");
        tree = (TTree) callMessage("UI|TREE|getThis");
        //给TREE添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }

    /**
     * 接受返回值方法  
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }         
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * 初始化树 
     */
    public void onInitTree() {
        TParm parmRule = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseRule()));
        //System.out.println(parmRule);
        //得到界面上的树对象
        tree = (TTree) callMessage("UI|TREE|getThis");
        tree.getRoot().removeAllChildren();
        TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        treeRoot.setText("物资类别");
        treeRoot.setType("Root");
        TParm parm = analyzeINVType();
        if (parm.getCount("INVTYPE_GROUP") <= 0)
            return;
        for (int i = 0; i < parm.getCount("INVTYPE_GROUP"); i++) {
            TParm parmI = (TParm) parm.getData("INVTYPE_GROUP", i);
            for (int j = 0; j < parmI.getCount("CATEGORY_CODE"); j++) {
                TTreeNode treeNode = new TTreeNode();
                treeNode.setText(parmI.getValue("CATEGORY_CHN_DESC", j));
                treeNode.setID(parmI.getValue("CATEGORY_CODE", j));
                if (parmI.getValue("DETAIL_FLG", j).equals("Y"))
                    treeNode.setType("UI");
                else
                    treeNode.setType("Path");
                if (getParentCode(treeNode.getID(), parmRule).length() == 0)
                    treeRoot.addSeq(treeNode);
                else
                    treeRoot.findNodeForID(getParentCode(treeNode.getID(),
                        parmRule)).addSeq(treeNode);
            }
        }
        tree.update();
    }

    /**
     * 得到父节点编码
     * @param code String     
     * @param parm TParm
     * @return String
     */
    private String getParentCode(String code, TParm parm) {
        int classify1 = parm.getInt("CLASSIFY1", 0);
        int classify2 = parm.getInt("CLASSIFY2", 0);
        int classify3 = parm.getInt("CLASSIFY3", 0);
        int classify4 = parm.getInt("CLASSIFY4", 0);
        int classify5 = parm.getInt("CLASSIFY5", 0);
        int serialNumber = parm.getInt("SERIAL_NUMBER", 0);
        if (code.length() == classify1)
            return "";
        if (code.length() == classify1 + classify2)
            return code.substring(0, classify1);
        if (code.length() == classify1 + classify2 + classify3)
            return code.substring(0, classify1 + classify2);
        if (code.length() == classify1 + classify2 + classify3 + classify4)
            return code.substring(0, classify1 + classify2 + classify3);
        if (code.length() ==
            classify1 + classify2 + classify3 + classify4 + classify5)
            return code.substring(0,
                                  classify1 + classify2 + classify3 + classify4);
        if (code.length() ==
            classify1 + classify2 + classify3 + classify4 + classify5 +
            serialNumber)
            return code.substring(0,
                                  classify1 + classify2 + classify3 + classify4 +
                                  classify5);
        return "";
    }

    /**
     * 整理物资分类信息
     * @return TParm
     */
    public TParm analyzeINVType() {
        TParm parm = new TParm();
        TParm parmInf = selectINVType();
        //System.out.println(parmInf);
        TParm parmLength = getINVTypeLength();
        //System.out.println(parmLength);
        for (int i = 0; i < parmLength.getCount(); i++) {
            int lenght = parmLength.getInt("CATEGORY_LENGTH", i);
            TParm parmI = new TParm();
            for (int j = 0; j < parmInf.getCount(); j++) {
                if (lenght == parmInf.getValue("CATEGORY_CODE", j).length()) {
                    copyTParm(parmInf, parmI, j);
                }
            }
            parm.addData("INVTYPE_GROUP", parmI);
        }
        return parm;
    }

    /**
     * 得到物资分类信息
     * @return TParm
     */
    public TParm selectINVType() {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseCategory()));
    }

    /**
     * 得到物资分类编码长度信息
     * @return TParm
     */
    public TParm getINVTypeLength() {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseCategoryLength()));
    }

    /**
     * 拷贝参数类
     * @param fromTParm TParm
     * @param toTParm TParm
     * @param row int
     */
    public void copyTParm(TParm fromTParm, TParm toTParm, int row) {
        for (int i = 0; i < fromTParm.getNames().length; i++) {
            toTParm.addData(fromTParm.getNames()[i],
                            fromTParm.getValue(fromTParm.getNames()[i], row));
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table.removeRowAll();
        String sql = InvBaseTool.getInstance().queryInvBase(this.getSerch()) ;
//      System.out.println("sql=====sql======="+sql);
        table.setSQL(sql);    
        table.retrieve();
        if (table.getRowCount() < 0) {   
            this.messageBox("没有查询数据");
        }
    }
    /**
     * 获取查询条件数据
     */
    public TParm  getSerch(){
    	TParm result  = new TParm() ;
    	//物资编码
    	if(this.getValueString("INV_CODE").length()>0){
    		result.setData("INV_CODE", this.getValueString("INV_CODE")) ;	
    	}
    	//物资名称
    	if(this.getValueString("INV_CHN_DESC").length()>0){
    		result.setData("INV_CHN_DESC", this.getValueString("INV_CHN_DESC")+"%") ;
    	}
    	//供应厂商
    	if(this.getValueString("SUP_CODE").length()>0){
    		result.setData("SUP_CODE", this.getValueString("SUP_CODE")) ;
    	}
    	//拼音码
    	if(this.getValueString("PY1").length()>0){
    		result.setData("PY1", "%"+this.getValueString("PY1")+"%") ;
    	}
    	//物资属性
    	if(this.getValueString("INVKIND_CODE").length()>0){
    		result.setData("INVKIND_CODE", this.getValueString("INVKIND_CODE")) ;
    	}
    	return result ;
    }
    /** 
     * 新增方法
     */
    public void onNew() {
        if (tree.getSelectNode().isRoot()){
            return;
        }
        if (tree.getSelectNode() == null) {
            this.messageBox("请选择物资类别");
            return;
        }   
        String cString=tree.getSelectNode().getID();
       String e= this.getInvMaxNumber(cString).getValue("INV_CODE",0);
       if (e==null||"".equals(e)) {
		e="0000000";
       }else {
		e=e.substring(6,e.length());
       }
       
       String a=cString.substring(0,2);
       String b=cString.substring(2,4);
        this.setValue("INV_CODE",  a+"."+b+"."+e);
        
        //新增后可以自己修改
        //getTextField("INV_CODE").setEnabled(false);  
    }

    /**  
     * 得到最大流水号  
     * @param typeCode String
     * @return String
     */
    public String getInvMaxSerialNumber(String typeCode){
        TParm parmRule = getINVRule();
        int serialNumber = parmRule.getInt("SERIAL_NUMBER",0);
        int totNumber = parmRule.getInt("TOT_NUMBER",0);
        TParm parm = getInvMaxNumber(typeCode);
        System.out.println("得到最大流水号parm"+parm);
        String numString = ""; 
        if (parm.getCount("INV_CODE") <= 0 || 
            parm.getValue("INV_CODE", 0).length() == 0)
            numString = "0"; 
        else                  
            numString = parm.getValue("INV_CODE", 0).substring(typeCode.length(),
            parm.getValue("INV_CODE", 0).length()).substring(2, 9);
//            numString = parm.getValue("INV_CODE", 0).substring(typeCode.length(),
//            parm.getValue("INV_CODE", 0).length()); 
        //1.0370627             
        int num = Integer.parseInt(numString) + 1;      
        numString = num + "";  
        for (int i = 0; numString.length() < serialNumber; i++)
            numString = "0" + numString;
        String zeroType = "";
        for (int i = 0;
             zeroType.length() < (totNumber - typeCode.length() - numString.length());
             i++) {
            zeroType += "0";
        }
        return typeCode + zeroType + numString;
    }

    /**
     * 得到物资分类编码长度信息
     * @return TParm
     */
    public TParm getINVRule() {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.getInvRule()));
    }

    /**
     * 取得最大号
     * @param invtype_code String
     * @return TParm
     */
    public TParm getInvMaxNumber(String invtype_code) {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvMaxSerialNumber(invtype_code)));
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }  
        // INV_BASE保存数据
        TParm parmBase = new TParm();
        parmBase = getParmBase(parmBase);
        // INV_TRANSUNIT保存数据
        TParm parmUnit = new TParm();
        parmUnit = getParmUnit(parmUnit);
        TParm result = new TParm(); 
        TParm parm = new TParm();
        parm.setData("INV_BASE", parmBase.getData());   
        parm.setData("INV_TRANSUNIT", parmUnit.getData());  
        System.out.println("parm:"+parm);
        if (table.getSelectedRow() < 0) {
            // 新增数据
        	//fux modify 界面与代码同步 将财务分类sys_category 分为主和细两部分 从 INV_ACC 拆分为 INV_ACC_M和 INV_ACC_D
            result = TIOM_AppServer.executeAction(    
                "action.inv.INVBaseAction", "onInsert", parm);  
        }   
        else {         
            // 更新数据  
            result = TIOM_AppServer.executeAction(
                "action.inv.INVBaseAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        this.onClear();
    }  

    /**
     * 数据检核
     * @return boolean
     */
    public boolean checkData() {
        if ("".equals(this.getValueString("INV_CHN_DESC"))) {
            this.messageBox("物资名称不能为空");
            return false;
        }
        if ("".equals(this.getValueString("INVKIND_CODE"))) {
            this.messageBox("物资种类不能为空");
            return false;
        }
        if ("".equals(this.getValueString("MAN_CODE"))) {
            this.messageBox("生产厂商不能为空");
            return false;
        }
        if ("".equals(this.getValue("COST_PRICE"))) {
            this.messageBox("成本价格不能为空");
            return false;
        }  
        if ("".equals(this.getValueString("PURCH_UNIT"))) {
            this.messageBox("进货单位不能为空");
            return false;
        }
        if ("".equals(this.getValueString("STOCK_UNIT"))) {        
            this.messageBox("库存单位不能为空");
            return false;   
        }
        if ("".equals(this.getValueString("DISPENSE_UNIT_NEW"))) {   
            this.messageBox("包装单位不能为空");  
            return false;  
        }    
        if ("".equals(this.getValue("STOCK_QTY"))) {
            this.messageBox("库存量不能为空");           
            return false;   
        }    
        if ("".equals(this.getValue("DISPENSE_QTY"))) {
            this.messageBox("包装量量不能为空");
            return false;
        }
  
        if ("".equals(this.getValue("ORDER_CODE"))) {
            this.messageBox("收费项目不能为空");
            return false;
        }
        return true;
    }

    /**
     * INV_BASE保存数据
     * @param parmBase TParm  
     * @return TParm  
     */  
    public TParm getParmBase(TParm parmBase) {
    	parmBase.setData("ACCOUNTS_CODE", this.getValueString("ACCOUNTS_CODE"));    
        parmBase.setData("INV_CODE", this.getValueString("INV_CODE"));
        parmBase.setData("INVTYPE_CODE", tree.getSelectNode().getID());
        parmBase.setData("PUR_QTY", this.getValueString("PUR_QTY"));
        parmBase.setData("INV_CHN_DESC", this.getValueString("INV_CHN_DESC"));
        parmBase.setData("INV_ABS_DESC", this.getValueString("INV_ABS_DESC"));
        parmBase.setData("INV_ENG_DESC", this.getValueString("INV_ENG_DESC"));
        parmBase.setData("PY1", this.getValueString("PY1"));
        parmBase.setData("PY2", this.getValueString("PY2"));
        parmBase.setData("INVKIND_CODE", this.getValueString("INVKIND_CODE"));
        parmBase.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parmBase.setData("MAN_CODE", this.getValueString("MAN_CODE"));      
        parmBase.setData("MAN_NATION", this.getValueString("MAN_NATION"));
        parmBase.setData("BUYWAY_CODE",
                         this.getRadioButton("BUYWAY_CODE_1").isSelected() ? "1" :
                         "2");
        parmBase.setData("USE_DEADLINE", this.getValueDouble("USE_DEADLINE"));
        parmBase.setData("COST_PRICE", this.getValueDouble("COST_PRICE"));
        parmBase.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        parmBase.setData("HYGIENE_TRADE_CODE",
                         this.getValueString("HYGIENE_TRADE_CODE"));
        
          
        parmBase.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));
        parmBase.setData("DISPENSE_UNIT", this.getValueString("DISPENSE_UNIT_NEW"));  
        parmBase.setData("PUR_UNIT", this.getValueString("PUR_UNIT"));  
        //主财务分类  
        parmBase.setData("USE_CODE", this.getValueString("USE_CODE"));  
        //细财务分类      
        parmBase.setData("FI_CODE", this.getValueString("FI_CODE"));  
        parmBase.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
        parmBase.setData("STOPBUY_FLG", this.getValueString("STOPBUY_FLG"));
        parmBase.setData("NORMALDRUG_FLG", this.getValueString("NORMALDRUG_FLG"));
        parmBase.setData("REQUEST_FLG", this.getValueString("REQUEST_FLG"));
        parmBase.setData("SEQMAN_FLG", this.getValueString("SEQMAN_FLG"));
        parmBase.setData("VALIDATE_FLG", this.getValueString("VALIDATE_FLG"));
        parmBase.setData("EXPENSIVE_FLG", this.getValueString("EXPENSIVE_FLG"));
        parmBase.setData("OPT_USER", Operator.getID());
        parmBase.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parmBase.setData("OPT_TERM", Operator.getIP());    
        parmBase.setData("CONSIGN_FLG", this.getValueBoolean("CONSIGN_FLG"));
        parmBase.setData("CONSIGN_MAN_CODE", this.getValueString("CONSIGN_MAN_CODE"));
          
        parmBase.setData("SUP_CODE", this.getValueString("SUP_CODE")); 
        parmBase.setData("UP_SUP_CODE", this.getValueString("UP_SUP_CODE"));
        parmBase.setData("INV_KIND", this.getValueString("KIND"));  
        parmBase.setData("INV_ACCOUNT", this.getValueString("ACCOUNT"));  
        //System.out.println("parmBase"+parmBase);
        return parmBase;       
    }

    /**
     * INV_TRANSUNIT保存数据  
     * @param parmUnit TParm
     * @return TParm
     */
    public TParm getParmUnit(TParm parmUnit) {
        parmUnit.setData("INV_CODE", this.getValueString("INV_CODE")); 
        parmUnit.setData("PURCH_UNIT", this.getValueString("PURCH_UNIT"));
        parmUnit.setData("PURCH_QTY", this.getValueString("PURCH_QTY"));
        parmUnit.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));  
        parmUnit.setData("STOCK_QTY", this.getValueString("STOCK_QTY"));                
        parmUnit.setData("DISPENSE_UNIT", this.getValueString("DISPENSE_UNIT_NEW"));    
        parmUnit.setData("DISPENSE_QTY",this.getValueString("DISPENSE_QTY"));    
//        parmUnit.setData("PURCH_UNIT", this.getValueString("STOCK_UNIT"));  
//        parmUnit.setData("PURCH_QTY", 1);
//        parmUnit.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));
//        parmUnit.setData("STOCK_QTY", 1);
//        parmUnit.setData("DISPENSE_UNIT", this.getValueString("STOCK_UNIT"));
//        parmUnit.setData("DISPENSE_QTY",1);
        parmUnit.setData("OPT_USER", Operator.getID());
        parmUnit.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parmUnit.setData("OPT_TERM", Operator.getIP());
        return parmUnit;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        table.removeRowAll(); 
        String clearStr = "INV_CODE;INV_CHN_DESC;INV_ABS_DESC;INV_ENG_DESC;PUR_QTY;PUR_UNIT;FI_CODE;USE_CODE;"
            + "PY1;PY2;INVKIND_CODE;DESCRIPTION;MAN_CODE;MAN_NATION;"
            + "USE_DEADLINE;COST_PRICE;ORDER_CODE;ORDER_DESC;"
            + "HYGIENE_TRADE_CODE;ACTIVE_FLG;STOPBUY_FLG;NORMALDRUG_FLG;"
            + "REQUEST_FLG;SEQMAN_FLG;VALIDATE_FLG;EXPENSIVE_FLG;"  
            + "STOCK_QTY;STOCK_UNIT;UNIT;DISPENSE_QTY;DISPENSE_UNIT_NEW;PURCH_QTY;PURCH_UNIT;" + 
              "SUP_CODE;UP_SUP_CODE;KIND;ACCOUNT;CONSIGN_FLG;CONSIGN_MAN_CODE";  
        this.clearValue(clearStr);   
        getTextField("INV_CODE").setEnabled(true);  
    }
   
    /** 
     * 删除方法
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("请选择删除项");
            return;
        }
        TParm parm = new TParm();
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVBaseAction", "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        this.messageBox("删除成功");
        this.onClear();
    }

    /**
     * 树的单击事件
     */
    public void onTreeClicked(){
       //检核是否得到树的节点
       if(((TTree)getComponent("TREE")).getSelectNode() == null)
           return;
       if(((TTree)getComponent("TREE")).getSelectNode().isRoot())
           return;
       onClear();
       queryInvTableInfo(((TTree)getComponent("TREE")).getSelectNode().getID());
    }

    /**
     * 查询表格数据动作
     * @param invType String
     */
    private void queryInvTableInfo(String type_code) {
        //将表格中的信息清空
        ( (TTable) getComponent("TABLE")).removeRowAll();
        ( (TTable) getComponent("TABLE")).setSQL(INVSQL.getInvBaseByTypeCode(
            type_code));
        ( (TTable) getComponent("TABLE")).retrieve();
    }

    /**
     * INV_CHN_DESC回车事件
     */
    public void onInvDescAction() {
        String py = TMessage.getPy(this.getValueString("INV_CHN_DESC"));
        setValue("PY1", py);
    }

    /**
     * STOCK_UNIT回车事件 
     */
    public void onStockUnitAction() {
        setValue("UNIT", this.getValue("STOCK_UNIT"));
    }

    /**
     * 表格单击事件  
     */
    public void onTableClick() {
    	//messageBox("111"); 
        String inv_code = table.getDataStore().getRowParm(table.getSelectedRow()).
            getValue("INV_CODE");
//        System.out.println(""+INVSQL.    
//            getInvBaseInfo(inv_code));  
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseInfo(inv_code)));   
        String parmStr = "INV_CODE;ACTIVE_FLG;INVTYPE_CODE;INVKIND_CODE;PUR_QTY;FI_CODE;USE_CODE;"
            + "INV_CHN_DESC;PY1;INV_ENG_DESC;INV_ABS_DESC;DESCRIPTION;MAN_CODE;"
            + "MAN_NATION;PURCH_UNIT;STOCK_UNIT;"   
            + "ORDER_CODE;HYGIENE_TRADE_CODE;STOPBUY_FLG;NORMALDRUG_FLG;" 
            + "REQUEST_FLG;SEQMAN_FLG;VALIDATE_FLG;EXPENSIVE_FLG;PY2;"       
            + "ORDER_DESC;SUP_CODE;UP_SUP_CODE;KIND;ACCOUNT;PUR_UNIT;" +
              "CONSIGN_MAN_CODE;CONSIGN_FLG";          
        TParm baseParm = parm.getRow(0);          
        this.setValueForParm(parmStr, baseParm);    
        //messageBox("aaaaaaa2222");
        if ("1".equals(baseParm.getValue("BUYWAY_CODE"))) {
            this.getRadioButton("BUYWAY_CODE_1").setSelected(true);
        }
        else {
            this.getRadioButton("BUYWAY_CODE_2").setSelected(true);
        }
        this.setValue("USE_DEADLINE", baseParm.getDouble("USE_DEADLINE"));
        this.setValue("COST_PRICE", baseParm.getDouble("COST_PRICE"));  
        
        this.setValue("PURCH_QTY", baseParm.getValue("PURCH_QTY"));  
        this.setValue("PURCH_UNIT", baseParm.getValue("STOCK_UNIT"));
           
        this.setValue("STOCK_QTY", baseParm.getValue("STOCK_QTY"));
        this.setValue("STOCK_UNIT", baseParm.getValue("STOCK_UNIT"));
        this.setValue("PUR_QTY", 1);
        this.setValue("UNIT", baseParm.getValue("STOCK_UNIT"));    
        this.setValue("DISPENSE_QTY", baseParm.getValue("DISPENSE_QTY"));
        this.setValue("DISPENSE_UNIT_NEW", baseParm.getValue("DISPENSE_UNIT"));
        this.getTextField("INV_CODE").setEnabled(false);
        //messageBox("2222");
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
 
    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
