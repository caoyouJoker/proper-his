package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class INSINSPAYKINDControl extends TControl {
    private static String TABLE = "Table";
   private String action = "INSERT";
    public void onInit() {
        super.onInit();
        callFunction("UI|" + TABLE + "|addEventListener",TABLE + "->" + TTableEvent.CLICKED,this,"onTableClicked");
        onClear();
    }
    public void onTableClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|delete|setEnabled",true);
        callFunction("UI|NHI_COMPANY|setEnabled",false);
        callFunction("UI|PAY_KIND_CODE|setEnabled",false);
        action = "EDIT";
    }
    /**
     * 保存
     */
    public void onSave(){
        out("保存begin");
        TParm parm = new TParm();
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        if("EDIT".equals(action))
        {
            if(!emptyTextCheck("NHI_COMPANY,PAY_KIND_DESC"))
                return;
            callFunction("UI|" + TABLE + "|setModuleParmUpdate",parm);
            if(!(Boolean)callFunction("UI|" + TABLE + "|onUpdate")){
                messageBox_("保存失败");
                return;
            }
            messageBox_("保存成功");
        }else
        {
            if(!emptyTextCheck("NHI_COMPANY,PAY_KIND_CODE,PAY_KIND_DESC"))
                return;
            callFunction("UI|" + TABLE + "|setModuleParmInsert",parm);
            if(!(Boolean)callFunction("UI|" + TABLE + "|onInsert")){
                messageBox_("新增失败");
                return;
            }
            messageBox_("新增成功");
            action = "EDIT";
            callFunction("UI|delete|setEnabled",true);
            callFunction("UI|NHI_COMPANY|setEnabled",false);
            callFunction("UI|PAY_KIND_CODE|setEnabled",false);
        }
        out("保存end");
    }
    /**
     * 删除
     */
    public void onDelete(){
        out("删除begin");
        if(messageBox("提示信息","是否删除?",this.YES_NO_OPTION) != 0)
            return;
        int row = (Integer)callFunction("UI|" + TABLE + "|getSelectedRow");
        if(row < 0)
            return;
        if(!(Boolean)callFunction("UI|" + TABLE + "|onDelete"))
        {
            messageBox_("删除失败");
            return;
        }
        messageBox_("删除成功");
        int rows = (Integer)callFunction("UI|" + TABLE + "|getSelectedRow");
        if(rows<0){
            this.onClear();
        }
        out("删除end");
    }
    /**
     * 清空
     */
    public void onClear(){
        out("清空begin");
        clearValue("NHI_COMPANY;PAY_KIND_CODE;PAY_KIND_DESC;PAY_KIND_PY");
        callFunction("UI|" + TABLE + "|clearSelection");
        callFunction("UI|save|setEnabled",true);
        callFunction("UI|delete|setEnabled",false);
        callFunction("UI|NHI_COMPANY|setEnabled",true);
        callFunction("UI|PAY_KIND_CODE|setEnabled",true);
        action = "INSERT";
        out("清空end");
    }
    /**
     * 查询初始化自动执行
     */
    public void onQuery()
    {
        callFunction("UI|" + TABLE + "|onQuery");
        int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
        if (row < 0)
            this.onClear();

    }
    /**
   * 回车带回拼音
   */
  public void onCode(){
      if ("".equals(String.valueOf(this.getValue("PAY_KIND_DESC")))) {
          return;
      }
      SystemTool st = new SystemTool();
      String value = st.charToCode(String.valueOf(this.getValue(
          "PAY_KIND_DESC")));
      if (null == value || "".equals(value)) {
          return;
      }
      this.setValue("PAY_KIND_PY", value);
      this.callFunction("UI|afterFocus","PAY_KIND_DESC");
  }

}
