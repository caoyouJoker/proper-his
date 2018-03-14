package com.javahis.ui.adm;

import java.util.Map;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import jdo.sys.PatTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title:病患查询 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY 2009.04.30
 * @version 1.0
 */
public class ADMPatQueryControl extends TControl{
    TParm data;
    int selectRow = -1;
     TParm acceptData = new TParm(); //接参
    public void onInit() {
        //zhangp 20111223 传参(医疗卡制卡)
    	Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
        }
        initUI();
  }
  /**
   * 初始化界面
   */
  public void initUI(){
      callFunction("UI|MR_NO|setEnabled", false);
      callFunction("UI|IPD_NO|setEnabled", false);
      callFunction("UI|PAT_NAME|setEnabled", false);
      callFunction("UI|TEL_HOME|setEnabled", false);
      callFunction("UI|ID_NO|setEnabled", false);
      callFunction("UI|BIRTH_DATE|setEnabled", false);
    //zhangp 20111223 传参查询(医疗卡制卡)
      if(!acceptData.getData().isEmpty()){
    	  TRadioButton name=(TRadioButton)this.callFunction("UI|R_PAT_NAME|getThis");
    	  name.setSelected(true);
    	  callFunction("UI|PAT_NAME|setEnabled", true);
    	  this.setValue("PAT_NAME", acceptData.getData("PAT_NAME"));
    	  this.onQuery();
      }
      
        
  }
  /**
   * 查询控件
   */
  public void onRadioButton(){
      initUI();
      TRadioButton mr=(TRadioButton)this.callFunction("UI|R_MR_NO|getThis");
      TRadioButton ipd=(TRadioButton)this.callFunction("UI|R_IPD_NO|getThis");
      TRadioButton name=(TRadioButton)this.callFunction("UI|R_PAT_NAME|getThis");
      TRadioButton tel=(TRadioButton)this.callFunction("UI|R_TEL_NO|getThis");
      TRadioButton id=(TRadioButton)this.callFunction("UI|R_ID_NO|getThis");
      TRadioButton birth=(TRadioButton)this.callFunction("UI|R_BIRTH_DAY|getThis");
      if (mr.isSelected()) {
          this.setData();
          callFunction("UI|MR_NO|setEnabled", true);
      } else if (ipd.isSelected()) {
          this.setData();
          callFunction("UI|IPD_NO|setEnabled", true);
      } else if (name.isSelected()) {
          this.setData();
          callFunction("UI|PAT_NAME|setEnabled", true);
      } else if (tel.isSelected()) {
          this.setData();
          callFunction("UI|TEL_HOME|setEnabled", true);
      } else if (id.isSelected()) {
          this.setData();
          callFunction("UI|ID_NO|setEnabled", true);
      } else if (birth.isSelected()) {
          this.setData();
          callFunction("UI|BIRTH_DATE|setEnabled", true);
      }

  }
  /**
   * setText
   */
  public void setData(){
      setValue("MR_NO","");
      setValue("IPD_NO","");
      setValue("PAT_NAME","");
      setValue("TEL_HOME","");
      setValue("MR_NO","");
      setValue("BIRTH_DATE","");


  }
  /**
    * 增加对Table的监听
    *
    * @param row
    *            int
    */
   public void onTableClicked(int row) {
       // 选中行
       if (row < 0)
           return;
//
       selectRow = row;
       // 不可编辑
//       ((TTextField) getComponent("ORDER_CODE")).setEnabled(false);
       // 设置删除按钮状态
//       ((TMenuItem) getComponent("delete")).setEnabled(true);
   }

  /**
   * 查询事件
   */
  public void onQuery(){
      TParm parm = new TParm();
      TRadioButton mr=(TRadioButton)this.callFunction("UI|R_MR_NO|getThis");
     TRadioButton ipd=(TRadioButton)this.callFunction("UI|R_IPD_NO|getThis");
     TRadioButton name=(TRadioButton)this.callFunction("UI|R_PAT_NAME|getThis");
     TRadioButton tel=(TRadioButton)this.callFunction("UI|R_TEL_NO|getThis");
     TRadioButton id=(TRadioButton)this.callFunction("UI|R_ID_NO|getThis");
     TRadioButton birth=(TRadioButton)this.callFunction("UI|R_BIRTH_DAY|getThis");

      if (mr.isSelected()) {
      parm.setData("MR_NO","%"+getValue("MR_NO")+"%");
   } else if (ipd.isSelected()) {
      parm.setData("IPD_NO","%"+getValue("IPD_NO")+"%");
   } else if (name.isSelected()) {
      parm.setData("PAT_NAME","%"+getValue("PAT_NAME")+"%");
   } else if (tel.isSelected()) {
      parm.setData("TEL_HOME","%"+getValue("TEL_HOME")+"%");
   } else if (id.isSelected()) {
      parm.setData("ID_NO","%"+getValue("ID_NO")+"%");
   } else if (birth.isSelected()) {
      parm.setData("BIRTH_DATE",getValue("BIRTH_DATE"));
   }

   TParm result = PatTool.getInstance().queryPat(parm);
   // 判断错误值
   if (result.getErrCode() < 0) {
       messageBox(result.getErrText());
       return;
   }
   ((TTable) getComponent("TABLE")).setParmValue(result);
}
  /**
   * 传回MR_NO;IPD_NO;PAT_NAME;IDNO;TEL_HOME;BIRTH_DATE
   */
  public void onSave() {
      TTable table = (TTable)this.callFunction("UI|TABLE|getThis");
      int row = table.getSelectedRow();
      TParm backData = new TParm();
      backData.setData("MR_NO", table.getValueAt(row, 0));
      backData.setData("IPD_NO", table.getValueAt(row, 1));
      backData.setData("PAT_NAME", table.getValueAt(row, 2));
      backData.setData("IDNO", table.getValueAt(row, 3));
      backData.setData("TEL_HOME", table.getValueAt(row, 4));
      backData.setData("BIRTH_DATE", table.getValueAt(row, 5));
      this.setReturnValue(backData);
      this.closeWindow();
  }

}
