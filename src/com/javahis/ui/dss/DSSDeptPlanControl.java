package com.javahis.ui.dss;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTable;
import jdo.dss.DSSDeptPlanTool;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: 科室绩效等级</p>
 *
 * <p>Description: 科室绩效等级</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author not sundx
 * @version 1.0
 */
public class DSSDeptPlanControl extends TControl {

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
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,"onTableValueChangeM");
    }

    /**
     * 初始化树
     */
    public void onInitTree() {
        TTree tree = (TTree)callMessage("UI|TREE|getThis");
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        root.setText("科室评估方案");
        root.setType("Root");
        root.removeAllChildren();
        TParm parmDGroup = DSSDeptPlanTool.getInstance().analyzeDept();
        for (int i = 0; i < parmDGroup.getCount("CODE_GROUP"); i++) {
            TParm parm = (TParm)parmDGroup.getData("CODE_GROUP",i);
            for(int j = 0; j < parm.getCount("DEPT_CODE"); j++){
                String noteType = parm.getValue("LEAF", j).equals("N")?"Path":"UI";
                TTreeNode evalPlanD = new TTreeNode("KPILEAVE", noteType);
                evalPlanD.setText(parm.getValue("DEPT_CHN_DESC", j) +
                                  parm.getValue("PLAN_DESC", j) +
                                  "(" + parm.getValue("WEIGHT", j)+")");
                evalPlanD.setID(parm.getValue("PLAN_CODE", j) +
                                parm.getValue("DEPT_CODE", j));
                evalPlanD.setName(parm.getValue("PLAN_CODE", j));
                evalPlanD.setValue(parm.getValue("DEPT_CODE", j));
                if(i == 0)
                    root.addSeq(evalPlanD);
                else{
                    for(int k = 0;k < parm.getValue("DEPT_CODE", j).length();k++){
                        String ID = parm.getValue("PLAN_CODE", j) +
                                    parm.getValue("DEPT_CODE", j).substring(0,
                                    parm.getValue("DEPT_CODE", j).length() - 1 - k);
                        if (root.findNodeForID(ID) != null) {
                            root.findNodeForID(ID).add(evalPlanD);
                            break;
                        }
                        if (root.findNodeForID(ID) == null &&
                            k == parm.getValue("DEPT_CODE", j).length()-1)
                            root.add(evalPlanD);
                    }
                }
            }
        }
        tree.update();
    }

    /**
     * 新建空行
     */
    public void onNew(){
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if (tree.getSelectNode() == null)
            return;
        if (((TTable) getComponent("TABLE_M")).getRowCount() > 0)
            return;
        ((TTable) getComponent("TABLE_M")).addRow();
        ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,4,Operator.getID());
        ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,5, TJDODBTool.getInstance().getDBTime());
        ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,6,Operator.getIP());
        ((TTable) getComponent("TABLE_M")).setItem(((TTable) getComponent("TABLE_M")).getRowCount()-1,3,getMaxSeqFromTableM() + 1);
    }

    /**
     * 新建细表空行
     */
    public void onNewD(){
        ((TTable) getComponent("TABLE_D")).addRow();
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,13,Operator.getID());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,14, TJDODBTool.getInstance().getDBTime());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,15,Operator.getIP());
        ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,7,getMaxSeqFromTableD() + 1);
        TTable table = ((TTable) getComponent("TABLE_M"));
        if(table.getRowCount() > 0){
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount() - 1, 0, table.getItemString(0,0));
            TParm parm = DSSDeptPlanTool.getInstance().queryDeptInfByDept("" + table.getItemString(0,0));
            if(parm == null)
                return;
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,2,parm.getValue("LEAF",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,1,table.getItemString(0,1));
        }
    }


    /**
     * 细表值改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        if(node.getColumn() == 1){
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,3,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,4,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,5,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,6,"0");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,9,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,10,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,11,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,12,"");
        }
        if(node.getColumn() == 3){
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,4,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,5,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,6,"0");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,9,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,10,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,11,"");
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,12,"");
            TParm parm = DSSDeptPlanTool.getInstance().
                         getKPIInfo(((TTable) getComponent("TABLE_D")).
                                    getItemString(((TTable) getComponent("TABLE_D")).
                                                  getRowCount()-1,1),"" + node.getValue());
            if(parm.getCount("KPI_LEVEL1") <= 0)
                return true;
            if(parm == null)
                return true;
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,4,parm.getValue("KPI_LEVEL1",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,5,parm.getValue("KPI_LEVEL2",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,6,parm.getValue("WEIGHT",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,9,parm.getValue("KPI_VALUE",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,10,parm.getValue("KPI_GOAL",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,11,parm.getValue("KPI_STATUS",0));
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,12,parm.getValue("KPI_ATTRIBUTE",0));
        }
        if(node.getColumn() == 0){
            TParm parm = DSSDeptPlanTool.getInstance().queryDeptInfByDept("" + node.getValue());
            if(parm == null)
                return true;
            ((TTable) getComponent("TABLE_D")).setItem(((TTable) getComponent("TABLE_D")).getRowCount()-1,2,parm.getValue("LEAF",0));
        }
        return false;
    }

    /**
     * 主表值改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChangeM(Object obj) {
        TTableNode node = (TTableNode)obj;
        if(node.getColumn() == 0)
            updatePlan(node.getValue()+"",((TTable) getComponent("TABLE_M")).getItemString(0,1));
        if(node.getColumn() == 1)
            updatePlan(((TTable) getComponent("TABLE_M")).getItemString(0,0),node.getValue()+"");
        return false;
    }

    /**
     * 保存
     */
    public void onSave(){
        ((TTable)getComponent("TABLE_M")).acceptText();
        ((TTable)getComponent("TABLE_D")).acceptText();
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        if(root.findChildNodeForID(((TTable)getComponent("TABLE_M")).getItemString(0,1)+
                ((TTable)getComponent("TABLE_M")).getItemString(0,0)) != null){
            messageBox("此搭配已经存在");
            return;
        }
        double weight = 0;
        for(int i = 0;i < ((TTable)getComponent("TABLE_D")).getRowCount();i++){
            weight += Double.parseDouble("" + ((TTable)getComponent("TABLE_D")).getValueAt(i,6));
            if(!((TTable)getComponent("TABLE_D")).getValueAt(i,0).equals(
               ((TTable)getComponent("TABLE_M")).getValueAt(0,0))){
                messageBox("第"+(i+1)+"行科室代码错误");
                return;
            }
        }
        if(weight != 100){
            messageBox("权重和不等于100无法保存");
            return;
        }
        ((TTable)getComponent("TABLE_M")).getDataStore().update();
        ((TTable) getComponent("TABLE_D")).getDataStore().update();
        messageBox("保存成功");
        onInitTree();
    }


    /**
     * 树的单击事件
     */
    public void onTreeClicked(){
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        ((TTable) callMessage("UI|TABLE_D|getThis")).removeRowAll();
        ((TTable) callMessage("UI|TABLE_M|getThis")).removeRowAll();
        if (tree.getSelectNode() == null)
            return;
        String SQL_M = " SELECT DEPT_CODE,PLAN_CODE,DESCRIPTION,"+
                       "        SEQ,OPT_USER,OPT_DATE,OPT_TERM"+
                       " FROM   DSS_DEPT_EVAL"+
                       " WHERE  DEPT_CODE = '"+tree.getSelectNode().getValue()+"'"+
                       " AND    PLAN_CODE = '"+tree.getSelectNode().getName()+"'"+
                       " ORDER BY  DEPT_CODE,PLAN_CODE";
        ((TTable) callMessage("UI|TABLE_M|getThis")).setSQL(SQL_M);
        ((TTable) callMessage("UI|TABLE_M|getThis")).retrieve();
        String SQL_D = " SELECT DEPT_CODE,PLAN_CODE,LEAF,KPI_CODE,KPI_LEVEL1,KPI_LEVEL2,"+
                       "        WEIGHT,SEQ,DESCRIPTION,KPI_VALUE,KPI_GOAL,KPI_STATUS,"+
                       "        KPI_ATTRIBUTE,OPT_USER,OPT_DATE,OPT_TERM"+
                       " FROM   DSS_DEPT_EVALD"+
                       " WHERE  DEPT_CODE = '"+tree.getSelectNode().getValue()+"'"+
                       " AND    PLAN_CODE = '"+tree.getSelectNode().getName()+"'"+
                       " ORDER BY  DEPT_CODE,PLAN_CODE";
        ((TTable) callMessage("UI|TABLE_D|getThis")).setSQL(SQL_D);
        ((TTable) callMessage("UI|TABLE_D|getThis")).retrieve();
    }

    /**
     * 展开
     */
    public void onPinyin(){
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        TParm parm = DSSDeptPlanTool.getInstance().getSaveDate(getValueString("DEPT")+"%",getValueString("PLAN"));
        if(parm.getCount("DEPT_CODE") <= 0){
            messageBox("查无资料");
            return;
        }
        for(int i = 0;i < parm.getCount("DEPT_CODE");i++){
            parm.addData("SEQ",i+1);
            parm.addData("DESCRIPTION","");
            parm.addData("OPT_USER",Operator.getID());
            parm.addData("OPT_TERM",Operator.getIP());
            parm.addData("OPT_DATE",date);
         }
         TParm result = TIOM_AppServer.executeAction(
             "action.dss.DSSDeptAction",
             "insertIntoDSSDeptEvald", parm);
         if (result.getErrCode() < 0) {
             this.messageBox("E0005");
             return;
        }
         messageBox("保存成功");
         onInitTree();
    }

    /**
     * 删除事件
     */
    public void onDelete() {
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if (tree.getSelectNode() == null)
            return;
        if(tree.getSelectNode().getChildCount() > 0){
            messageBox("有孩子节点无法删除");
            return;
        }
        TParm parm = DSSDeptPlanTool.getInstance().deleteDSSDeptEvald(tree.getSelectNode().getValue(),
                                                                      tree.getSelectNode().getName());
        if(parm == null){
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功");
        onClear();
        onInitTree();
    }

    /**
     * 得到主表最大序号
     * @return int
     */
    public int getMaxSeqFromTableM(){
        TTable table = ((TTable) getComponent("TABLE_M"));
        int maxSeq = DSSDeptPlanTool.getInstance().getDSSDeptMaxMSeq();
        for(int i = 0;i < table.getTable().getRowCount();i++){
           if(table.getItemInt(i,3) <= maxSeq)
               continue;
           maxSeq = table.getItemInt(i,3);
        }
        return maxSeq;
    }

    /**
     * 得到细表最大号
     * @return int
     */
    public int getMaxSeqFromTableD(){
        TTable table = ((TTable) getComponent("TABLE_D"));
        int maxSeq = DSSDeptPlanTool.getInstance().getDSSDeptMaxDSeq();
        for(int i = 0;i < table.getTable().getRowCount();i++){
           if(table.getItemInt(i,7) <= maxSeq)
               continue;
           maxSeq = table.getItemInt(i,7);
        }
        return maxSeq;
    }

    /**
     * 更新KPI信息
     */
    public void updatePlan(String dept,String plan){
        TTable tableD = (TTable)getComponent("TABLE_D");
        TParm parm = new TParm();
        if(dept.length() ==0 || plan.length() == 0)
            return;
        parm = DSSDeptPlanTool.getInstance().getKPIInfoByPlan(plan);
        if(parm == null)
            return;
        for(int i = tableD.getTable().getRowCount();i>0;i--){
            tableD.removeRow(i - 1);
        }
        TParm parmLeaf = DSSDeptPlanTool.getInstance().queryDeptInfByDept(dept);
        if(parmLeaf == null)
            return;
        int maxSeq = getMaxSeqFromTableD() + 1;
        for(int i = 0;i < parm.getCount("KPI_CODE");i++){
            tableD.addRow();
            tableD.setItem(tableD.getRowCount() - 1, 0, dept);
            tableD.setItem(tableD.getRowCount() - 1, 1, plan);
            tableD.setItem(tableD.getRowCount() - 1, 2, parmLeaf.getValue("LEAF", 0));
            tableD.setItem(tableD.getRowCount() - 1, 3, parm.getValue("KPI_CODE", i));
            tableD.setItem(tableD.getRowCount() - 1, 4, parm.getValue("KPI_LEVEL1", i));
            tableD.setItem(tableD.getRowCount() - 1, 5, parm.getValue("KPI_LEVEL2", i));
            tableD.setItem(tableD.getRowCount() - 1, 6, parm.getValue("WEIGHT", i));
            tableD.setItem(tableD.getRowCount() - 1, 7, maxSeq);
            tableD.setItem(tableD.getRowCount() - 1, 9, parm.getValue("KPI_VALUE", i));
            tableD.setItem(tableD.getRowCount() - 1, 10, parm.getValue("KPI_GOAL", i));
            tableD.setItem(tableD.getRowCount() - 1, 11, parm.getValue("KPI_STATUS", i));
            tableD.setItem(tableD.getRowCount() - 1, 12, parm.getValue("KPI_ATTRIBUTE", i));
            tableD.setItem(tableD.getRowCount() - 1,13, Operator.getID());
            tableD.setItem(tableD.getRowCount() - 1,14, TJDODBTool.getInstance().getDBTime());
            tableD.setItem(tableD.getRowCount() - 1,15, Operator.getIP());
            maxSeq++;
        }
    }


    /**
     * 清空事件
     */
    public void onClear() {
        setValue("DEPT","");
        setValue("PLAN","");
        ((TTable) callMessage("UI|TABLE_D|getThis")).removeRowAll();
        ((TTable) callMessage("UI|TABLE_M|getThis")).removeRowAll();
        ((TTable) callMessage("UI|TABLE_D|getThis")).getDataStore().resetModify();
        ((TTable) callMessage("UI|TABLE_M|getThis")).getDataStore().resetModify();
    }
}
