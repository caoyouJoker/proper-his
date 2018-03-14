package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;

/**
 *
 * <p>Title: �̶��ı����ԶԻ���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.8.27
 * @version 1.0
 */
public class FixedEditControl
    extends TControl {
    /**
     * �̶��ı�����
     */
    private EFixed fixed;
    /**
     * ��ʼ��
     */
    public void onInit() {
        fixed = (EFixed) getParameter();

        if (fixed == null) {
            return;
        }
        setValue("GROUP_NAME", fixed.getGroupName());
        setValue("NAME", fixed.getName());
        setValue("TEXT", fixed.getText());
        setValue("CActionType", fixed.getActionType());
        setValue("TRY_FILENAME", fixed.getTryFileName());
        setValue("TRY_NAME", fixed.getTryName());

        //��Ԫ�������޸�
        setValue("ALLOW_NULL", fixed.isAllowNull() ? "Y" : "N");
        //setDataElements
        setValue("CHK_ISCDA", fixed.isIsDataElements() ? "Y" : "N");
        setValue("CHK_ISELE", fixed.isIsElementContent() ? "Y" : "N");
        setValue("CHK_LOCKED",fixed.isLocked()?"Y" : "N");

       // this.messageBox("isSup" + fixed.isSup());
        //setValue("CHK_SUP", fixed.getScript()==1 ? "Y" : "N");
       //setValue("CHK_SUB", fixed.getScript()==2 ? "Y" : "N");
        //System.out.println("fixed  is data Elements===="+fixed.isIsDataElements());
        /**if(fixed.isIsDataElements()){
            TTextField tf_name = (TTextField)getComponent("NAME");
            //TTextField tf_text = (TTextField)getComponent("TEXT");
            TCheckBox ck_text = (TCheckBox)getComponent("ALLOW_NULL");
            tf_name.setEditable(false);
            //tf_text.setEditable(false);
            ck_text.setEnabled(false);
                 }**/

    }

    /**
     * �����±�
     */
    public void onSub() {
          setValue("CHK_SUP", "N");
       /** if (getValue("CHK_SUB").equals("Y")) {
            setValue("CHK_SUB", "N");
        }
        else {
            setValue("CHK_SUB", "Y");
            setValue("CHK_SUP", "N");
        }**/

    }

    /**
     * �����ϱ�
     */
    public void onSup() {
        setValue("CHK_SUB", "N");
       /** if (getValue("CHK_SUP").equals("Y")) {
            setValue("CHK_SUP", "N");
        }
        else {

            setValue("CHK_SUB", "N");
            setValue("CHK_SUP", "Y");
        }**/
    }


    /**
     * ȷ��
     */
    public void onOK() {
        if (getText("TEXT").length() == 0) {
            this.messageBox_("�ı�����Ϊ��!");
            return;
        }
        fixed.setGroupName(getText("GROUP_NAME"));
        fixed.setName(getText("NAME"));
        fixed.setText(getText("TEXT"));
        fixed.setModify(true);
        fixed.setActionType(getText("CActionType"));
        fixed.setTryFileName(getText("TRY_FILENAME"));
        fixed.setTryName(getText("TRY_NAME"));
        //this.messageBox(""+getValue("CHK_ISCDA"));
        if (getValue("CHK_ISCDA").equals("Y")) {
            fixed.setDataElements(true);
        }
        else {
            fixed.setDataElements(false);
        }
        if (getValue("CHK_ISELE").equals("Y")) {
            fixed.setElementContent(true);
        }
        else {
            fixed.setElementContent(false);
        }
        fixed.setLocked(getValueBoolean("CHK_LOCKED"));
        /**if (getValue("CHK_SUP").equals("Y")) {
            fixed.setScript(1);
        }

        if (getValue("CHK_SUB").equals("Y")) {
            fixed.setScript(2);
        }
        if(!getValue("CHK_SUB").equals("Y")&&!getValue("CHK_SUP").equals("Y")){
            fixed.setScript(0);
        }**/


        setReturnValue("OK");
        closeWindow();
    }

    public void onTryFileName() {

    }

    public void onTryName() {

    }
}
