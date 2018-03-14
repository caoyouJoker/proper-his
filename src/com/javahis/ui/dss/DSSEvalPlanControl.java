package com.javahis.ui.dss;

import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import jdo.dss.DSSEvalPlanTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TMessage;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 绩效评估方案</p>
 *
 * <p>Description: 绩效评估方案</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DSSEvalPlanControl extends TControl {

    TParm primaryParmM = null;
    TParm primaryParmD = null;
    /**
     * 初始化方法
     */
    public void onInit() {
        //调用父类初始化方法
        super.onInit();
        //初始化树
        onInitTree();
        //给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,"onTableValueChangeD");
    }


    /**
     * 初始化树
     */
    public void onInitTree() {
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        root.setText("绩效评估方案");
        root.setType("Root");
        root.removeAllChildren();
        TParm dSSEvalPlanM = DSSEvalPlanTool.getInstance().queryDSSEvalPlanM();
        if (dSSEvalPlanM == null)
            return;
        TParm dSSEvalPlanD = DSSEvalPlanTool.getInstance().queryDSSEvalPlanD();
        if (dSSEvalPlanD == null)
            return;
        for (int i = 0; i < dSSEvalPlanM.getCount("PLAN_CODE"); i++) {
            TTreeNode evalPlanM = new TTreeNode("KPILEAVE" + i, "Path");
            evalPlanM.setText(dSSEvalPlanM.getValue("PLAN_DESC", i));
            evalPlanM.setValue(dSSEvalPlanM.getValue("PLAN_CODE", i));
            root.addSeq(evalPlanM);
        }
        for (int i = 0; i < dSSEvalPlanD.getCount("PLAN_CODE"); i++) {
            TTreeNode evalPlanD = new TTreeNode("KPILEAVE", "UI");
            evalPlanD.setText(dSSEvalPlanD.getValue("KPI_DESC", i) +
                              "(" + dSSEvalPlanD.getValue("WEIGHT", i) + "%)");
            evalPlanD.setValue(dSSEvalPlanD.getValue("PLAN_CODE", i) +" "+
                               dSSEvalPlanD.getValue("KPI_CODE", i));
            evalPlanD.setID(dSSEvalPlanD.getValue("PLAN_CODE", i));
            root.findNodeForValue(dSSEvalPlanD.getValue("PLAN_CODE", i)).add(evalPlanD);
        }
        tree.update();
    }

    /**
     * 保存方法
     */
    public void onSave(){
        ((TTable)getComponent("TABLE_M")).acceptText();
        ((TTable)getComponent("TABLE_D")).acceptText();
        ((TTable)getComponent("TABLE_M")).getDataStore().update();
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        double weight = 0;
        for(int i = 0;i < ((TTable)getComponent("TABLE_D")).getRowCount();i++){
            weight += Double.parseDouble("" + ((TTable)getComponent("TABLE_D")).getValueAt(i,2));
        }
        if(weight != 100 &&
           !tree.getSelectNode().isRoot()&&
           !tree.getSelectNode().getType().equals("UI")){
            messageBox("权重和不等于100无法保存细项");
            return;
        }
        ((TTable) getComponent("TABLE_D")).getDataStore().update();
        messageBox("保存成功");
        onInitTree();
    }

    /**
     * 得到主表最大序号
     * @return int
     */
    public int getMaxSeqFromTableM(){
        TTable table = ((TTable) getComponent("TABLE_M"));
        int maxSeq = DSSEvalPlanTool.getInstance().getDSSEvalMaxMSeq();
        for(int i = 0;i < table.getTable().getRowCount();i++){
           if(table.getItemInt(i,4) <= maxSeq)
               continue;
           maxSeq = table.getItemInt(i,4);
        }
        return maxSeq;
    }

    /**
     * 得到细表最大序号
     * @return int
     */
    public int getMaxSeqFromTableD(){
        TTable table = ((TTable) getComponent("TABLE_D"));
        int maxSeq = DSSEvalPlanTool.getInstance().getDSSEvalMaxDSeq();
        for(int i = 0;i < table.getTable().getRowCount();i++){
           if(table.getItemInt(i,5) <= maxSeq)
               continue;
           maxSeq = table.getItemInt(i,5);
        }
        return maxSeq;
    }

    /**
     * 新建空行
     */
    public void onNew(){
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if (tree.getSelectNode() == null||
            tree.getSelectNode().getType().equals("UI"))
          return;
        if(tree.getSelectNode().isRoot()){
            ((TTable) getComponent("TABLE_M")).addRow();
            ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,6, Operator.getID());
            ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,7,TJDODBTool.getInstance().getDBTime());
            ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,8,Operator.getIP());
            ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,4,getMaxSeqFromTableM()+1);
            return;
        }
        ((TTable) getComponent("TABLE_D")).addRow();
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,7,Operator.getID());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,8,TJDODBTool.getInstance().getDBTime());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,9,Operator.getIP());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,0,tree.getSelectNode().getValue());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,5,getMaxSeqFromTableD()+1);
    }

    /**
     * 删除方法
     */
    public void onDelete(){
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if (tree.getSelectNode() == null)
            return;
        TParm result = new TParm();
        if(tree.getSelectNode().isRoot())
            return;
        if(tree.getSelectNode().getType().equals("UI")){
            messageBox("不可删除叶子节点");
            return;
        }
        if(!tree.getSelectNode().isRoot() && !tree.getSelectNode().getType().equals("UI")){
            result = DSSEvalPlanTool.getInstance().deletePlanM(tree.getSelectNode().getValue());
            result = DSSEvalPlanTool.getInstance().deletePlanD(tree.getSelectNode().getValue());
        }
        if(result == null){
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功");
        onInitTree();
        ((TTable) callMessage("UI|TABLE_M|getThis")).retrieve();
        ((TTable) callMessage("UI|TABLE_D|getThis")).removeRowAll();
    }

    /**
     * 表格编辑事件M
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        if(node.getColumn() == 1){
            String s = TMessage.getPy("" + node.getValue());
            node.getTable().setItem(node.getRow(),2,s);
            node.getTable().setItem(node.getRow(),3,s);
        }
        return false;
    }

    /**
     * 表格编辑事件D
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChangeD(Object obj) {
        TTableNode node = (TTableNode)obj;
        if(node.getColumn() == 1){
             node.getTable().setItem(node.getRow(),3,"");
             node.getTable().setItem(node.getRow(),4,"");
            TParm parm1 = DSSEvalPlanTool.getInstance().getKPI("" + node.getValue());
            if(parm1.getCount("PARENT_CODE") <= 0)
                return false;
            if(parm1.getValue("PARENT_CODE",0).length() == 0)
                return false;
            TParm parm2 = DSSEvalPlanTool.getInstance().getKPI(parm1.getValue("PARENT_CODE",0));
             if(parm2.getCount("PARENT_CODE") <= 0)
                return false;

            if(parm2.getValue("PARENT_CODE",0).length() == 0){
                node.getTable().setItem(node.getRow(),3,parm1.getValue("PARENT_CODE",0));
                return false;
            }
            node.getTable().setItem(node.getRow(),3,parm2.getValue("PARENT_CODE",0));
            node.getTable().setItem(node.getRow(),4,parm1.getValue("PARENT_CODE",0));
        }
        return false;
    }
    /**
     * 树的点击事件
     */
    public void onTreeClicked(){
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        ((TTable) callMessage("UI|TABLE_D|getThis")).removeRowAll();
        if (tree.getSelectNode() == null||
            tree.getSelectNode().getType().equals("UI"))
            return;
        if(tree.getSelectNode().isRoot()){
            ((TTable) callMessage("UI|TABLE_M|getThis")).retrieve();
            return;
        }
        String SQL = " SELECT PLAN_CODE,KPI_CODE,WEIGHT,KPI_LEVEL1,KPI_LEVEL2,SEQ,"+
                     "        DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM"+
                     " FROM   DSS_EVAL_PLAND "+
                     " WHERE  PLAN_CODE = '"+tree.getSelectNode().getValue()+"'";
        ((TTable) callMessage("UI|TABLE_D|getThis")).setSQL(SQL);
        ((TTable) callMessage("UI|TABLE_D|getThis")).retrieve();
    }

    /**
     * 清空事件
     */
    public void onClear() {
        ((TTable) callMessage("UI|TABLE_D|getThis")).removeRowAll();
        ((TTable) callMessage("UI|TABLE_D|getThis")).getDataStore().resetModify();
        ((TTable) callMessage("UI|TABLE_M|getThis")).removeRowAll();
        ((TTable) callMessage("UI|TABLE_M|getThis")).getDataStore().resetModify();
    }

}

