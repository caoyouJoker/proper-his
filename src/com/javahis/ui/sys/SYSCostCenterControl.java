package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import jdo.sys.SYSRuleTool;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:成本中心管理 </p>
 *
 * <p>Description:成本中心管理 </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author pangben 2011-06-01
 * @version 1.0
 */
public class SYSCostCenterControl extends TControl{
    /**
      * 树根
      */
     private TTreeNode treeRoot;
     /**
      * 编号规则类别工具
      */
     SYSRuleTool ruleTool;
     /**
      * 树的数据放入datastore用于对树的数据管理
      */
     TDataStore treeDataStore = new TDataStore();

     /**
      * 初始化
      */
     public void onInit() { // 初始化程序
         super.onInit();
         // 初始化树
         onInitSelectTree();
         // 给tree添加监听事件
         addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
         // 给Table添加侦听事件
         addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                          "onTableChangeValue");
         // 种树
         onCreatTree();
         callFunction("UI|new|setEnabled", false);
         onPageInit();
     }

     /**
      * 初始化树
      */
     public void onInitSelectTree() {
         // 得到树根
         treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
         if (treeRoot == null)
             return;
         // 给根节点添加文字显示
         treeRoot.setText("成本中心");
         // 给根节点赋tag
         treeRoot.setType("Root");
         // 设置根节点的id
         treeRoot.setID("");
         // 清空所有节点的内容
         treeRoot.removeAllChildren();
         // 调用树点初始化方法
         callMessage("UI|TREE|update");
     }

     /**
      * 初始化树上的节点
      */
     public void onCreatTree() {
         // 给dataStore赋值
         treeDataStore.setSQL("SELECT RULE_TYPE, CATEGORY_CODE,"
                              + " CATEGORY_CHN_DESC, CATEGORY_ENG_DESC,"
                              + " PY1, PY2, SEQ, DESCRIPTION, DETAIL_FLG,"
                              + " OPT_USER, OPT_DATE, OPT_TERM"
                              + " FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_COST_CENTER'");
         // 如果从dataStore中拿到的数据小于0
         if (treeDataStore.retrieve() <= 0)
             return;
         // 过滤数据,是编码规则中的成本中心数据
         ruleTool = new SYSRuleTool("SYS_COST_CENTER");
         if (ruleTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
             TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                 "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
             // 循环给树安插节点
             for (int i = 0; i < node.length; i++)
                 treeRoot.addSeq(node[i]);
         }
         // 得到界面上的树对象
         TTree tree = (TTree) callMessage("UI|TREE|getThis");
         // 更新树
         tree.update();
         // 设置树的默认选中节点
         tree.setSelectNode(treeRoot);
     }

     /**
      * 单击树
      *
      * @param parm
      *            Object
      */
     public void onTreeClicked(Object parm) { // 新增按钮不能用
         callFunction("UI|new|setEnabled", false);
         // 得到点击树的节点对象
         TTreeNode node = (TTreeNode) parm;
         if (node == null)
             return;
         // 得到table对象
         TTable table = (TTable) callFunction("UI|TABLE|getThis");
         // table接收所有改变值
         table.acceptText();
         // 如果点击的是树的根结点
         if (node.getType().equals("Root"))
             // 根据过滤条件过滤Tablet上的数据
             table.setFilter(getQuerySrc());
         else { // 如果点的不是根结点

             // 拿到当前选中的节点的id值
             String id = node.getID();
             // 拿到过滤条件
             String s = getQuerySrc();
             // 如果过滤条件中存在数据
             if (s.length() > 0)
                 s += " AND ";
             s += "COST_CENTER_CODE like '" + id + "%'";
             // table中的datastore中过滤数据添加条件
             table.setFilter(s);
         }
         // 执行table的过滤
         table.filter();
         // 给table数据加排序条件
         table.setSort("COST_CENTER_CODE");
         // table排序后重新赋值
         table.sort();
         // 得到父ID
         String parentID = node.getID();
         int classify = 1;
         if (parentID.length() > 0)
             classify = ruleTool.getNumberClass(parentID) + 1;
         // 如果是最小节点,可以增加一行
         if (classify > ruleTool.getClassifyCurrent()) {
             callFunction("UI|new|setEnabled", true);
         }
     }
     /**
      * 属性Table改变值
      * @param obj Object
      * @return boolean
      */
     public boolean onTableChangeValue(Object obj) {
         // 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
         TTableNode node = (TTableNode) obj;
         if (node == null)
             return false;
         // 如果改变的节点数据和原来的数据相同就不改任何数据
         if (node.getValue().equals(node.getOldValue()))
             return false;
         // 拿到table上的parmmap的列名
         String columnName = node.getTable().getDataStoreColumnName(
             node.getColumn());
         // 拿到当前改变后的数据
         String value = "" + node.getValue();
         // 如果是名称改变了拼音1自动带出,并且成本中心名称不能为空
         if ("COST_CENTER_CHN_DESC".equals(columnName)) {
             if (value.equals("") || value == null) {
                 messageBox_("成本中心名称不能为空!");
                 return true;
             }
             String py = TMessage.getPy(value);
             node.getTable().setItem(node.getRow(), "PY1", py);
             return false;
         }
         // 如果是简称改变了拼音1自动带出,并且成本中心名称不能为空
         if ("COST_CENTER_ABS_DESC".equals(columnName)) {
             if (value.equals("") || value == null) {
                 messageBox_("成本中心简称不能为空!");
                 return true;
             }
         }

         // 如果是成本中心代码改变了要进行几项校验
         if ("COST_CENTER_CODE".equals(columnName)) {
             // 保存编码规则的数据长度
             int length = ruleTool.getTot();
             // 如果改编后的长度不等于编码规则中的长度返回
             if (length != value.length()) {
                 messageBox_("编号" + node.getValue() + "长度不等于" + length + "!");
                 return true;
             }
             // 对成本中心代码进行检核...如果存在则重复
             if (node.getTable().getDataStore().exist(
                 "COST_CENTER_CODE='" + value + "'")) {
                 messageBox_("编号" + node.getValue() + "重复!");
                 return true;
             }
             // 得到上层编码
             String partent = ruleTool.getNumberParent(value);
             // 检核上层编码是否存在
             if (treeRoot.findNodeForID(partent) == null) {
                 messageBox_("编号分类" + partent + "不存在!");
                 return true;
             }
             return false;
         }
         return false;
     }

     /**
      * 新增方法
      */
     public void onNew() { // 拿到table对象
         TTable table = (TTable) callFunction("UI|TABLE|getThis");
         // 接收文本
         table.acceptText();
         // 取table数据
         TDataStore dataStore = table.getDataStore();
         // 找到最大科室代码(dataStore,列名)
         String maxCode = getMaxCode(dataStore, "COST_CENTER_CODE");
         // 拿到树对象
         TTree tree = getTree();
         // 找到选中的树节点
         TTreeNode node = tree.getSelectionNode();
         // 如果没有选中的节点
         if (node == null)
             return;
         // 得到父ID
         String parentID = node.getID();
         int classify = 1;
         // 如果点的是树的父节点存在,得到当前编码规则
         if (parentID.length() > 0)
             classify = ruleTool.getNumberClass(parentID) + 1;
         // 如果是最小节点,可以增加一行
         if (classify > ruleTool.getClassifyCurrent()) { // 得到默认的自动添加的科室代码
             String no = ruleTool.getNewCode(maxCode, classify);
             // 得到新添加的table数据行号
             int row = table.addRow();
             // 设置当前选中行为添加的行
             table.setSelectedRow(row);
             // 给科室代码添加默认值
             table.setItem(row, "COST_CENTER_CODE", parentID + no);
             // 默认成本中心名称
             table.setItem(row, "COST_CENTER_CHN_DESC", "(新建成本中心)");
             // 默认成本中心简称
             table.setItem(row, "COST_CENTER_ABS_DESC", "(新建成本中心名称)");
             // 默认区域
             table.setItem(row, "REGION_CODE", Operator.getRegion());
             // 默认最小科室注记
             table.setItem(row, "FINAL_FLG", "Y");
             // 顺序号
             int seq = Integer.parseInt(getMaxCode(table.getDataStore(), "SEQ"));
             table.setItem(row, "SEQ", seq + 1);
         }
     }

     /**
      * 清空方法
      */
     public void onClear() {
         this.setValue("COST_CENTER_CODE", "");
         this.setValue("COST_CENTER_CHN_DESC", "");
     }

     /**
      * 得到树对象
      *
      * @return TTree
      */
     public TTree getTree() {
         return (TTree) callFunction("UI|TREE|getThis");
     }

     /**
      * 得到最大的编号
      *
      * @param dataStore
      *            TDataStore
      * @param columnName
      *            String
      * @return String
      */
     public String getMaxCode(TDataStore dataStore, String columnName) {
         if (dataStore == null)
             return "";
         int count = dataStore.rowCount();
         String s = "";
         for (int i = 0; i < count; i++) {
             String value = dataStore.getItemString(i, columnName);
             if (StringTool.compareTo(s, value) < 0)
                 s = value;
         }
         return s;
     }
     /**
      * 保存数据
      * @return boolean
      */
     public boolean onUpdate() {
         Timestamp date = SystemTool.getInstance().getDate();
         TTable table = (TTable) callFunction("UI|TABLE|getThis");
//         System.out.println("table显示数据"+table.getShowParmValue());
         // 接收文本
         table.acceptText();
         // 获得全部改动的行号
         int rows[] = table.getModifiedRowsFilter();
         //查询停用的成本中心代码
         String sql =
             " SELECT COST_CENTER_CODE FROM SYS_COST_CENTER WHERE ACTIVE_FLG = 'N' ";
         TParm selCostCenterParm = new TParm(TJDODBTool.getInstance().select(sql));
         int costCenterCount = selCostCenterParm.getCount("COST_CENTER_CODE");
         StringBuffer allCostCenter = new StringBuffer();
         for (int j = 0; j < costCenterCount; j++) {
         String costCenter="";
             costCenter = selCostCenterParm.getValue("COST_CENTER_CODE", j);
             if (allCostCenter.length() > 0)
                 allCostCenter.append(",");
             allCostCenter.append(costCenter);
         }
         String allCostCenterStr = allCostCenter.toString();
         // 给固定数据配数据
         for (int i = 0; i < rows.length; i++) {
             table.setItemFilter(rows[i], "OPT_USER", Operator.getID());
             table.setItemFilter(rows[i], "OPT_DATE", date);
             table.setItemFilter(rows[i], "OPT_TERM", Operator.getIP());
         }
//         System.out.println("保存sql》》》》"+table.getSQL());
         if (!table.update()) {
             messageBox("E0001");
             return false;
         }
         //更新科室表中成本中心代码为空
         String upSql =
             " UPDATE SYS_COST_CENTER SET COST_CENTER_CODE = '' WHERE SYS_COST_CENTER IN ("+allCostCenterStr+") ";
         if (allCostCenterStr.length() > 0) {
             TParm upCostCenter = new TParm(TJDODBTool.getInstance().update(
                 upSql));
             if (upCostCenter.getErrCode() < 0) {
                 err("ERR:" + upCostCenter.getErrCode() +
                     upCostCenter.getErrText() +
                     upCostCenter.getErrName());
                 return false;
             }
         }
         messageBox("P0001");
         return true;
     }

     /**
      * 过滤条件
      *
      * @return String
      */
     private String getQuerySrc() { // 拿到成本中心代码
         String code = getText("COST_CENTER_CODE");
         // 得到成本中心名称
         String desc = getText("COST_CENTER_CHN_DESC");
         String sb = "";
         if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
             sb= " REGION_CODE='"+Operator.getRegion()+"' ";
         // 配过滤条件
         if (code != null && code.length() > 0)
             sb += "COST_CENTER_CODE like '" + code + "%'";
         if (desc != null && desc.length() > 0) {
//             if (sb.length() > 0)
//                 sb += " AND ";
             sb += "COST_CENTER_CHN_DESC like '" + desc + "%'";
         }
         return sb;
     }

     /**
      * 成本中心名称和成本中心代码回车查事件
      */
     public void onQuery() {
         onTreeClicked(getTree().getSelectionNode());
     }

     /**
      * 是否关闭窗口
      *
      * @return boolean true 关闭 false 不关闭
      */
     public boolean onClosing() {
         // 如果有数据变更
         if (CheckChange())
             switch (this.messageBox("提示信息", "是否保存", this.YES_NO_CANCEL_OPTION)) {
                 // 保存
                 case 0:
                     if (!onUpdate())
                         return false;
                     break;
                     // 不保存
                 case 1:
                     return true;
                     // 撤销
                 case 2:
                     return false;
             }
         // 没有变更的数据
         return true;

     }

     /**
      * 检核是否有数据变更
      *
      * @return boolean
      */
     public boolean CheckChange() {
         // 检查数据变更
         TTable tableBed = (TTable) callFunction("UI|TABLE|getThis");
         if (tableBed.isModified())
             return true;
         return false;
     }
     /**
      * 得到最大的编号 +1
      * @param dataStore TDataStore
      * @param columnName String
      * @param dbBuffer String
      * @return int
      */
     public int getMaxSeq(TDataStore dataStore, String columnName,
                          String dbBuffer) {
         if (dataStore == null)
             return 0;
         // 保存数据量
         int count = dataStore.getBuffer(dbBuffer).getCount();
         // 保存最大号
         int max = 0;
         for (int i = 0; i < count; i++) {
             int value = TCM_Transform.getInt(dataStore.getItemData(i,
                 columnName, dbBuffer));
             // 保存最大值
             if (max < value) {
                 max = value;
                 continue;
             }
         }
         // 最大号加1
         max++;
         return max;
     }
     /**
      * 初始化界面
      */
     public void onPageInit() {
         String s = "";
         if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
             s = " REGION_CODE='" + Operator.getRegion() + "' ";

         TTable table = (TTable) callFunction("UI|TABLE|getThis");
         table.setFilter(s);
         // 给table数据加排序条件
         table.setSort("COST_CENTER_CODE");
         // table排序后重新赋值
         table.sort();
         // 执行table的过滤
         table.filter();
     }

}
