package com.javahis.ui.inv;

import jdo.inv.INVOrgTool;
import jdo.inv.InvBaseTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.RunClass;
/**
 *
 * <p>Title:选择序号管理的物资 </p>
 *
 * <p>Description: 选择序号管理的物资</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class INVStockDDChooseControl extends TControl {
    /**
     * 表
     */
    private TTable table;
    /**
     * 前层control
     */
    private Object dispenseMControl;
    /**
     * 插入的行数
     */
    private int insertRow=0;
    /**
     * 存储查找到的数据
     */
    TDataStore dataStore=null;
    /**
     * 需要选择的总量
     */
    private int qty=0;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        this.callFunction("UI|TABLE|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onDownTableCheckBoxChangeValue");
        onQuery();
        initCombox();
    }
    /**
   * 物资科室
   */
  public void initCombox() {
      TParm parm = INVOrgTool.getInstance().getDept();
      TComboBox comboBox = new TComboBox();
      comboBox.setParmMap("id:ORG_CODE;name:DEPT_DESC");
      comboBox.setParmValue(parm);
      comboBox.setShowID(true);
      comboBox.setShowName(true);
      comboBox.setExpandWidth(30);
      comboBox.setTableShowList("name");
      table.addItem("DEPT", comboBox);

//        comboBox = (TComboBox)this.getComponent("TO_ORG_CODE");
//        comboBox.setParmValue(parm);
//        comboBox.updateUI();

  }

  /**
   * 查询
   */
  public void onQuery() {
      //得到上个界面传入参数
      TParm parm = (TParm) getParameter();
      if (parm == null)
          return;
      dispenseMControl = parm.getData("INVDISPENSE");
      if (dispenseMControl == null)
          return;
      dataStore = new TDataStore();
      String sql = getSql(parm);
      if (sql == null)
          return;
      dataStore.setSQL(sql);
      dataStore.retrieve();
      TParm parmforTable = dataStore.getBuffer(dataStore.PRIMARY);
      int rowCount = parmforTable.getCount();
      for (int i = 0; i < rowCount; i++) {
          parmforTable.addData("S", "N");
      }
      table.setParmValue(parmforTable);
      table.updateUI();
      //需要选择的个数
      qty = parm.getInt("QTY");
      TParm result=InvBaseTool.getInstance().getInvChnDesc(parm.getValue("INV_CODE"));    //wm2013-06-05  INVBaseTool.
      if(result.getErrCode()<0)
          return;
      this.setValue("INV_CHN_DESC",result.getValue("INV_CHN_DESC",0));
  }
  /**
   * 查询配参
   * @param parm TParm
     * @return String
     */
    public String getSql(TParm parm) {
       String sql="SELECT * FROM INV_STOCKDD WHERE ";
       //物资代码
       String value = parm.getValue("INV_CODE");
       if (value == null || value.length() == 0)
           return null;
       sql += "INV_CODE ='" + value + "'";
       //科室代码
       value = parm.getValue("ORG_CODE");
       if (value == null || value.length() == 0)
           return null;
       sql += " AND ORG_CODE ='" + value + "'";
       //批次序号
       int batchSeq = parm.getInt("BATCH_SEQ");
       if(batchSeq!=0)
           sql += " AND BATCH_SEQ =" + batchSeq;
       //不在任何手术包中
       sql+="AND PACK_FLG = 'N'";
       return sql;
   }
   /**
    * 勾选物资
    * @param obj Object
    */
   public void onDownTableCheckBoxChangeValue(Object obj){
        table.acceptText();
        //选中的行
        int row=table.getSelectedRow();
        getInv(row);
   }
   /**
    * 选中一个物资
    * @param row int
    */
   public void getInv(int row){
       //拿到选择checkBox
        String value=table.getItemString(row,"S");
        //判断是否选中
        if (value.equals("Y")) {
            //从存储的数据中选择选中的行
            TParm parm = dataStore.getRowParm(row);
            //调用前台control的方法
            RunClass.runMethod(dispenseMControl, "onNewTableDDOneRow", new Object[] {parm});
            //选择的数据加加
            insertRow++;
            setValue("QTY",insertRow);
            //如果选则足够的物资自动关闭窗口
            if(insertRow==qty)
               callFunction("UI|onClose");
            //删除刚才选中的行
            table.removeRow(row);
        }

   }
   /**
    * 条码扫描
    */
   public void onSelected() {
       String scream=getValueString("INV_CODE");
       String invCode=scream.substring(0, scream.length() - 4);
       int invSeqNo=Integer.parseInt(scream.substring( (scream.length() - 4), scream.length()));
       clearValue("INV_CODE;INVSEQ_NO");
       int rowCount=table.getRowCount();
       TParm parm=table.getParmValue();

       for(int i=0;i<rowCount;i++){
           if(parm.getValue("INV_CODE",i).equals(invCode) && parm.getInt("INVSEQ_NO",i)==invSeqNo){
               table.setValueAt("Y", i, 0);
               getInv(i);
               ((TTextField)getComponent("INV_CODE")).grabFocus();
               return;
           }
       }
       messageBox_("此物资不在库!");
   }

   /**
    * 是否关闭窗口(当有数据变更时提示是否保存)
    * @return boolean true 关闭 false 不关闭
    */
   public boolean onClosing() {
       this.setReturnValue(insertRow);
       // 传入量不是0
       if (qty != 0){
           //判断选择量是否足够
           if (qty != insertRow)
               switch (this.messageBox("提示信息",
                                       "还有" + (qty - insertRow) +
                                       "个物资没有选择,是否结束",
                                       this.YES_NO_OPTION)) {
                   //保存
                   case 0:
                       return true;
                       //不保存
                   case 1:
                       return false;
               }
       }
       //没有变更的数据
       return true;
    }

}
