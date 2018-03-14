package com.javahis.ui.database;

import com.dongyang.tui.text.ETextFormat;
import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ETextFormatEditControl extends TControl{
    private ETextFormat eTextFormat;
    private String SQL_SYSTEM = "SELECT SQL_CODE AS ID,SQL_CHN_DESC AS NAME,PY1,PY2 FROM EMR_TEXTFORMAT";
    public void onInit(){
        eTextFormat = (ETextFormat)getParameter();
        if(eTextFormat == null)
            return;
        setValue("ALLOW_NULL",eTextFormat.isAllowNull()?"Y":"N");
        setValue("NAME",eTextFormat.getName());
        TTextFormat value = (TTextFormat)getComponent("SQL_CODE");
        value.setModifySQL(true);
        value.setPopupMenuSQL(SQL_SYSTEM);
        value.popupMenuRetrieve();
        setValue("SQL_CODE",eTextFormat.getData());

        setValue("GROUP_NAME",eTextFormat.getGroupName());
        setValue("MACRONAME",eTextFormat.getMicroName());
        setValue("CHK_ISCDA",eTextFormat.isIsDataElements()?"Y" : "N");
        setValue("CHK_LOCKED",eTextFormat.isLocked()?"Y" : "N");
       /** if(eTextFormat.isIsDataElements()){
          TTextField tf_name = (TTextField)getComponent("NAME");
          TTextFormat tf_drop= (TTextFormat)getComponent("SQL_CODE");

          TCheckBox ck_text = (TCheckBox)getComponent("ALLOW_NULL");

          TTextField tf_groupname = (TTextField)getComponent("GROUP_NAME");
          TTextField tf_macroname = (TTextField)getComponent("MACRONAME");

          tf_name.setEnabled(false);
          ck_text.setEnabled(false);
          tf_drop.setEnabled(false);

          tf_groupname.setEnabled(false);
          //tf_macroname.setEnabled(false);
      }**/



    }
    public void onConfirm(){
        if(eTextFormat == null)
            return;
        setReturnValue("OK");
        eTextFormat.setData(getValueString("SQL_CODE"));
        eTextFormat.setName(getValueString("NAME"));
        eTextFormat.setAllowNull(getValueBoolean("ALLOW_NULL"));

        eTextFormat.setGroupName(getValueString("GROUP_NAME"));
        eTextFormat.setMicroName(getValueString("MACRONAME"));
        if(getValue("CHK_ISCDA").equals("Y")){
           eTextFormat.setDataElements(true);
       }else{
            eTextFormat.setDataElements(false);
       }
       eTextFormat.setLocked(getValueBoolean("CHK_LOCKED"));

       eTextFormat.setModify(true);

        closeWindow();
    }
    public void onSelect(){
        String SQL = " SELECT CODE_SYSTEM FROM EMR_CLINICAL_DATAGROUP"+
                     " WHERE DATA_CODE = '"+getValueString("NAME")+"'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        setValue("SQL_CODE",parm.getValue("CODE_SYSTEM",0));
    }
    public void onCancel(){
        closeWindow();
    }
}
