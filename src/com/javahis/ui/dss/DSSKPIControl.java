package com.javahis.ui.dss;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import jdo.dss.DSSKPISQLTool;
import com.dongyang.util.TMessage;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;

/**
 * 等级指标单档界面控制类
 * <p>Title: 等级指标单档界面控制类</p>
 *
 * <p>Description:等级指标单档界面控制类 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DSSKPIControl extends TControl {

    /**
     * 初始化方法
     */
    public void onInit() {
        //调用父类初始化方法
        super.onInit();
        //初始化树
        onInitTree();
        //给TREE添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
    }

    /**
     * 初始化树
     */
    public void onInitTree(){
        //得到界面树
        TTree tree = (TTree)getThisItem("TREE");
        //得到界面树的根结点
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        //设置根结点的属性
        root.setText("指标等级");
        root.setType("Root");
        //初始化界面前清理掉所有叶子节点
        root.removeAllChildren();
        //得到等级指标分级后的数据集合
        TParm treeKPIParm = DSSKPISQLTool.getInstance().queryDSSKPI();
        if(treeKPIParm == null)
            return;
        //循环取出每个级别的数据进行树的建立
        for(int i =0;i<treeKPIParm.getCount("KPI_CODE");i++){
            String noteType = "Path";
            //根据节点类型设置是否为叶子节点
            if(treeKPIParm.getValue("LEAF",i).equals("Y"))
                noteType = "UI";
            else
                noteType = "Path";
            //建立新节点
            TTreeNode KPILeave = new TTreeNode("KPILEAVE" + i, noteType);
            //将等级指标信息设置到节点当中
            KPILeave.setText(treeKPIParm.getValue("KPI_DESC", i));
            KPILeave.setValue(treeKPIParm.getValue("KPI_CODE", i));
            //第一级的节点放入根结点
            if (treeKPIParm.getValue("PARENT_CODE", i).length() == 0)
                root.addSeq(KPILeave);
            //其他级别的节点放入相应的父节点下面
            else
                root.findNodeForValue(treeKPIParm.getValue("PARENT_CODE", i)).add(KPILeave);
        }
        //重画树
        tree.update();
    }

    /**
     * 树的双击事件
     */
    public void onTreeClicked(){
       //检核是否得到树的节点
       if(((TTree)getThisItem("TREE")).getSelectNode() == null)
           return;
       //将表格中的信息清空
       ((TTable)getThisItem("TABLE")).removeRowAll();
       //取得父节点信息
       onQueryKPICode(((TTree)getThisItem("TREE")).getSelectNode().getValue());
       //取得相应孩子节点信息
       onQueryChildren(((TTree)getThisItem("TREE")).getSelectNode().getValue());
    }


    /**
     * 查询动作
     */
    public void onQuery(){
        //将表格中的信息清理掉
        ((TTable)getThisItem("TABLE")).removeRowAll();
        //查询父节点信息
        onQueryKPICode(getValueString("KPI_CODE"));
        //查询孩子节点信息
        onQueryChildren(getValueString("KPI_CODE"));
        //设置下一个焦点的位置
        ((TTextField)getThisItem("KPI_DESC")).grabFocus();
    }


    /**
     * 查询父节点方法
     * @param KPICode String
     */
    private void onQueryKPICode(String KPICode){
        //检核等级指标编码是否合法
        if(KPICode == null || KPICode.length() == 0)
            return;
        //通过等级指标代码查询等级指标相关信息
        TParm parm = DSSKPISQLTool.getInstance().queryDSSKPIByCode(KPICode);
        //备份界面等级指标信息
        String code =  getValueString("KPI_CODE");
        //清空界面
        onClear();
        //还原等级指标编码信息
        setValue("KPI_CODE",code);
        if(parm == null||
           parm.getCount("KPI_CODE") <= 0)
            return;
        //将等级指标信息设置到界面上
        setValue("KPI_CODE",parm.getValue("KPI_CODE",0));
        setValue("KPI_DESC",parm.getValue("KPI_DESC",0));
        setValue("PARENT_CODE",parm.getValue("PARENT_CODE",0));
        setValue("LEAF",parm.getValue("LEAF",0));
        setValue("PY1",parm.getValue("PY1",0));
        setValue("PY2",parm.getValue("PY2",0));
        setValue("SEQ",parm.getValue("SEQ",0));
        setValue("DESCRIPTION",parm.getValue("DESCRIPTION",0));
        setValue("KPI_VALUE",parm.getValue("KPI_VALUE",0));
        setValue("KPI_GOAL",parm.getValue("KPI_GOAL",0));
        setValue("KPI_STATUS",parm.getValue("KPI_STATUS",0));
        setValue("KPI_ATTRIBUTE",parm.getValue("KPI_ATTRIBUTE",0));
        setValue("KPI_KIND",parm.getValue("KPI_KIND",0));
    }

    /**
     * 查询孩子节点方法
     * @param parentCode String
     */
    private void onQueryChildren(String parentCode){
        //检核父节点编码是否合法
        if(parentCode == null ||
           parentCode.length() == 0)
            return;
        //查询父节点下属的孩子节点
        TParm parm = DSSKPISQLTool.getInstance().queryDSSKPIByParentCode(parentCode);
        if(parm == null||
           parm.getCount("KPI_CODE") == 0)
            return;
        //将孩子结点的数据放入画面的表格上
        ((TTable)getThisItem("TABLE")).setParmValue(parm);
    }

    /**
     * 获取界面控件
     * @param tag String
     * @return Object
     */
    private Object getThisItem(String tag){
        return  callMessage("UI|"+tag+"|getThis");
    }

    /**
     * KPI名称回车事件
     */
    public void onKPIDesc(){
        //得到熟的根结点
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        //检核此节点编码是否试新编码
        if(root.findNodeForValue(getValueString("KPI_CODE")) == null){
            //得到最新编码
            setValue("SEQ", DSSKPISQLTool.getInstance().getMaxSeq());
            //设置拼音1
            setValue("PY1",TMessage.getPy(getValueString("KPI_DESC")));
            //设置拼音2
            setValue("PY2",TMessage.getPy(getValueString("KPI_DESC")));
        }
        //设置下一焦点位置
        ((TComboBox)getThisItem("KPI_ATTRIBUTE")).grabFocus();
    }

    /**
     * KPI表达式回车事件
     */
    public void onKPIValue(){
        ((TTextField)getThisItem("KPI_GOAL")).grabFocus();
    }

    /**
     * KPI目标表达式回车事件
     */
    public void onKPIGoal(){
        ((TTextField)getThisItem("KPI_STATUS")).grabFocus();
    }

    /**
     * KPI状态表达式回车事件
     */
    public void onKPIStatus(){
        ((TTextField)getThisItem("DESCRIPTION")).grabFocus();
    }

    /**
     * KPI属性回车事件
     */
    public void onKPIAttribute(){
        ((TComboBox)getThisItem("KPI_KIND")).grabFocus();
    }

    /**
     * KPI种类回车事件
     */
    public void onKPIKind(){
        ((TTextField)getThisItem("PARENT_CODE")).grabFocus();
    }

    /**
     * 父类灰回车事件
     */
    public void onParentCode(){
        ((TComboBox)getThisItem("LEAF")).grabFocus();
    }

    /**
     * 叶子回车事件
     */
    public void onLeaf(){
        ((TTextField)getThisItem("KPI_VALUE")).grabFocus();
    }

    /**
     * 界面表格单击事件
     */
    public void onTable(){
        //得到选中行行号
        int row = ((TTable)getThisItem("TABLE")).getSelectedRow();
        //得到选中行数据
        TParm parm = ((TTable)getThisItem("TABLE")).getParmValue();
        //将选中行数据放到画面上方
        setValue("KPI_CODE",parm.getValue("KPI_CODE",row));
        setValue("KPI_DESC",parm.getValue("KPI_DESC",row));
        setValue("PARENT_CODE",parm.getValue("PARENT_CODE",row));
        setValue("LEAF",parm.getValue("LEAF",row));
        setValue("PY1",parm.getValue("PY1",row));
        setValue("PY2",parm.getValue("PY2",row));
        setValue("SEQ",parm.getValue("SEQ",row));
        setValue("DESCRIPTION",parm.getValue("DESCRIPTION",row));
        setValue("KPI_VALUE",parm.getValue("KPI_VALUE",row));
        setValue("KPI_GOAL",parm.getValue("KPI_GOAL",row));
        setValue("KPI_STATUS",parm.getValue("KPI_STATUS",row));
        setValue("KPI_ATTRIBUTE",parm.getValue("KPI_ATTRIBUTE",row));
        setValue("KPI_KIND",parm.getValue("KPI_KIND",row));
    }

    /**
     * 保存按钮事件
     */
    public void onSave(){
        //检核等级指标编码是否为空
        if(getValueString("KPI_CODE").length() == 0){
            messageBox("KPI定义不可为空");
            return;
        }
        //得到熟的根结点
        TTreeNode root = (TTreeNode) callMessage("UI|TREE|getRoot");
        //如果父节点不为空检核父节点是否存在
        if(getValueString("PARENT_CODE").length() != 0&&
           root.findNodeForValue(getValueString("PARENT_CODE")) == null){
            messageBox("父节点不存在");
            ((TTextField)getThisItem("PARENT_CODE")).grabFocus();
            return;
        }
        //检核等级指标编码是否存在
        TParm tParm = DSSKPISQLTool.getInstance().queryDSSKPIByCode(getValueString("KPI_CODE"));
        if(tParm == null)
            return;
        //得到界面数据
        TParm parm = getParmForTag("KPI_CODE;KPI_DESC;PARENT_CODE;LEAF;PY1;" +
                                   "PY2;SEQ;DESCRIPTION;KPI_VALUE;KPI_GOAL;"+
                                   "KPI_STATUS;KPI_ATTRIBUTE;KPI_KIND");
        //设置特殊参数
        parm.setData("SEQ",new Integer("" + parm.getData("SEQ")));
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = new TParm();
        //如果等级指标编码不存在则调用新增方法,如果存在则调用修改方法
        if(tParm.getCount() <= 0)
            result = DSSKPISQLTool.getInstance().insertDSSKPI(parm);
        else
            result = DSSKPISQLTool.getInstance().updateDSSKPIByCode(parm);
        if (result == null){
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
        //重新初始化树
        onInitTree();
        //重新调用查询方法
        onQuery();
    }

    /**
     * 删除按钮触发事件
     */
    public void onDelete() {
        //检核是否选中要删除的树节点
        if (((TTree) getThisItem("TREE")).getSelectNode() == null)
            return;
        //调用删除方法
        TParm result = DSSKPISQLTool.getInstance().deleteKPIAndChlidren(((TTree)getThisItem("TREE")).getSelectNode().getValue());
        if (result == null) {
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功");
        //重新初始化树
        onInitTree();
        //重新调用查询方法
        onQuery();
    }

    /**
     * 清空方法
     */
    public void onClear(){
        setValue("KPI_CODE","");
        setValue("KPI_DESC","");
        setValue("PARENT_CODE","");
        setValue("LEAF","");
        setValue("PY1","");
        setValue("PY2","");
        setValue("SEQ","");
        setValue("DESCRIPTION","");
        setValue("KPI_VALUE","");
        setValue("KPI_GOAL","");
        setValue("KPI_STATUS","");
        setValue("KPI_ATTRIBUTE","");
        setValue("KPI_KIND","");
        ((TTable)getThisItem("TABLE")).removeRowAll();
    }

}
