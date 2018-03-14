package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.StatusDetailsTool;
import com.dongyang.ui.TTreeNode;
import jdo.sys.CTZTool;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.FeeCodeOperateTool;
import com.dongyang.ui.TTableNode;
import jdo.sys.ChargeDetailList;

/**
 * <p>Title:身份明细 </p>
 *
 * <p>Description:身份明细 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSCTZTreeControl
    extends TControl {
    TParm data = new TParm();
    TParm Treedata = new TParm();
    int selectrow = -1; //table被选择
    int treeselect = -1; //树被选择
    private static final String TREE = "Tree";
    TTreeNode treeRoot;

    /**
     * 初始化界面
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
       //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|CTZ_CODE|setEnabled", false);
        this.onInitTree();
        callFunction("UI|panelempty|addItem","SYSCTZ","%ROOT%\\config\\bil\\SYSCTZ.x");
    }

    /**
     * 查询数据
     * @return TParm
     */
    public void onQuery() {
        this.callFunction("UI|Table|removeRowAll"); //清空table
        data = StatusDetailsTool.getInstance().selectalldata();
        this.callFunction("UI|TABLE|setParmValue", data); //给table配参
//        this.onClear(); //调用清空方法
    }

    /**
     * 循环插入院内费用代码
     */
    public void updatedata(TParm parm) {
        //接收不存在的费用代码
        int ex = 0;
        //查询已有的数据
        TParm hadcode = StatusDetailsTool.getInstance().selectalldata(parm);
        //查询全部院内费用代码
        TParm allcode = FeeCodeOperateTool.getInstance().selectalldata();
        //判断尚未插入的数据
        for (int i = 0; i < allcode.getCount(); i++) {
            String alcode = allcode.getValue("CHARGE_HOSP_CODE", i);
            //判断当前alcode在hadcode中是否存在
            for (int j = 0; j <= hadcode.getCount(); j++) {
                String excode = hadcode.getValue("CHARGE_HOSP_CODE", j);
                if (alcode.equals(excode)) {
                    ex = 0;
                    break;
                }
                ex = 1;
            }
            if (ex > 0) {
                //插入数据配参
                parm.setData("CHARGE_HOSP_CODE", alcode);
                parm.setData("OWN_RATE", 1);
                parm.setData("DESCRITPION", "");
                parm.setData("SEQ", 0);
                parm.setData("OPT_USER", Operator.getID());
                parm.setData("OPT_TERM", Operator.getIP());
                //调用插入数据方法
                StatusDetailsTool.getInstance().insertData(parm);
            }
        }
    }
    /**
     * 初始化树
     */
    public void onInitTree() {
        out("begin");
        //取得树根的对象
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        //设置树根显示的文字
        treeRoot.setText("身份分类");
        //组
        //treeRoot.setGroup("ROOT");
        //存放节点的编号 如果是树根写ROOT
        treeRoot.setID("ROOT");
        //为了出图标用的
        treeRoot.setType("ROOT");
        //存附带的数据
        //treeRoot.setValue("ROOT:GROUP");
        //清空树根下的所有垃圾数据
        treeRoot.removeAllChildren();
        //装载树的数据和NOOD
        downloadRootTree(treeRoot);
        //更新页面
        callMessage("UI|" + TREE + "|update");
        out("end");
    }

    /**
     * 下载树数据
     * @param parentNode TTreeNode
     */
    public void downloadRootTree(TTreeNode parentNode) {
        if (parentNode == null)
            return;
        TParm parm = CTZTool.getInstance().selecTtreeData();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String id = parm.getValue("CTZ_CODE", i);
            String name = parm.getValue("CTZ_DESC", i);
            //String type = parm.getValue("TYPE",i);
            TTreeNode node = new TTreeNode(name, "CTZ");
            node.setID(id);
            TParm rowParm = new TParm();
            rowParm.setRowData( -1, parm, i);
            node.setData(rowParm);
            parentNode.add(node);
        }
    }

//    /**
//     *清空
//     */
//    public void onClear() {
//        //清空所有数据
//        clearValue("CHARGE_HOSP_CODE;OWN_RATE;CTZ_CODE;SEQ;DESCRITPION");
//        callFunction("UI|TABLE|learSelectc", true);
//        selectrow = -1;
//        treeselect = -1;
//        //删除不能被编辑
//        callFunction("UI|save|setEnabled", false);
//        callFunction("UI|TABLE|clearSelection");
//    }

    /**
     * 树点击动作
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
//        this.onClear();
        treeselect = 1;
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("ROOT".equals(node.getType())){
            callFunction("UI|panelempty|addItem", "SYSCTZ",
                         "%ROOT%\\config\\bil\\SYSCTZ.x");
//            callFunction("UI|panelempty|removeItem", "SYSCTZDetails");
            return;
        }
        callFunction("UI|panelempty|addItem", "SYSCTZDetails",
                     "%ROOT%\\config\\bil\\SYSCTZDetails.x");
//        callFunction("UI|panelempty|removeItem", "SYSCTZ");

        TParm ID = new TParm();
        ID.setData("CTZ_CODE", node.getID());
        //自动加入费用代码不在的
        this.updatedata(ID);
        //查询细项
        this.selectdetail(ID);
        //setValueForParm("UI|CTZ_CODE|setParmValue",ID,0);
        this.callFunction("UI|TABLE|acceptText");
        this.setValue("CTZ_CODE", node.getID());
        //不可以保存
        callFunction("UI|save|setEnabled", false);
    }

    /**
     * 根据主表查细表数据 CTZ_CODE
     * @return TParm
     */
    public void selectdetail(TParm parm) {
        //细表数据
        data = StatusDetailsTool.getInstance().selectalldata(parm);
        this.callFunction("UI|TABLE|setParmValue", data); //给table配参
    }

}
