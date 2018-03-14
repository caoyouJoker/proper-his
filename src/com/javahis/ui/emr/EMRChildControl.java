package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRChildControl extends TControl {
    /**
     * 树的名字
     */
    private static final String TREE_NAME = "TREECHILD";
    /**
     * 子模板数据
     */
    private TParm emrChildData;
    /**
     * 树根
     */
    private TTreeNode treeRoot;

    /**
     * 初始化方法
     */
    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
        if(obj!=null&&obj instanceof TParm){
            emrChildData = (TParm)obj;
        }
        //初始化页面
        initPage();
        //注册事件
        initEven();

    }
    /**
    * 注册事件
    */
   public void initEven(){
       //单击选中树项目
       addEventListener(TREE_NAME+"->" + TTreeEvent.DOUBLE_CLICKED, "onTreeClicked");
   }
   /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }
    /**
     * 初始化页面
     */
    public void initPage(){
        treeRoot = this.getTTree(TREE_NAME).getRoot();
        //拿到树根
        treeRoot.setText(emrChildData.getValue("NODE_NAME"));
        treeRoot.setType("Root");
        treeRoot.removeAllChildren();
        int rowCount = emrChildData.getInt("ACTION","COUNT");
        for(int i=0;i<rowCount;i++){
            //加节点EMR_TEMPLET表中的NAME,PATH字段
            String deptCode = this.getDeptDesc(emrChildData.getValue("DEPT_CODE", i)).length()==0?"":"("+emrChildData.getValue("DEPT_CODE", i)+")";
            TTreeNode node = new TTreeNode(emrChildData.getValue("SUBCLASS_DESC", i)+deptCode,"1");
            //存储EMR_TEMPLET表的CODE字段
            node.setID(emrChildData.getValue("EMT_FILENAME", i));
            //设置是否打开EMR,EMR_TEMPLET表的EMR_CLASS字段
            node.setValue(emrChildData.getValue("TEMPLET_PATH", i));
            //设置科室权限
            node.setGroup(emrChildData.getValue("DEPT_CODE", i));
            //存储EMR_TEMPLET表的 一条数据
            //this.messageBox("== emr data=="+emrChildData.getRow(i));
            node.setData(emrChildData.getRow(i));
            //加入树根
            treeRoot.add(node);
        }
        this.getTTree(TREE_NAME).update();
    }
    /**
     * 双击事件
     * @param parm Object
     */
    public void onTreeClicked(Object parm){
        if(parm==null)
            return;
        TTreeNode node = (TTreeNode)parm;
        if(!emrChildData.getValue("DEPT_CODE").equals(node.getGroup())&&node.getGroup().length()!=0){
            this.messageBox("您不是此模版编辑的科室！");
            return;
        }
        this.setReturnValue(node.getData());
        this.closeWindow();
    }
    /**
    *  得到树
    * @param tag String
    * @return TTree
    */
   public TTree getTTree(String tag){
       return (TTree)this.getComponent(tag);
   }
   /**
    * 确定
    */
   public void onOK(){
       TTreeNode node = this.getTTree(TREE_NAME).getSelectNode();
       if(node==null){
           this.messageBox("请选择要打开的模版！");
           return;
       }
       if(!emrChildData.getValue("DEPT_CODE").equals(node.getGroup())&&node.getGroup().length()!=0){
           this.messageBox("您不是此模版编辑的科室！");
           return;
       }
       this.setReturnValue(node.getData());
       this.closeWindow();
   }
   /**
    * 取消
    */
   public void onNo(){
       this.closeWindow();
   }
}
