package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import java.awt.event.KeyEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.tui.text.EAssociateChoose;
import java.util.Vector;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AssociatePopMenuControl
    extends TControl {
    private static final String SUB_TEMPLATE_PAHT="JHW/片语";
    /**
     * 单选文本对象
     */
    private EAssociateChoose eAssociateChoose;
    private TTable table;
    private TTextField value;
    /**
     * 初始化
     */
    public void onInit() {
        eAssociateChoose = (EAssociateChoose) getParameter();
        if (eAssociateChoose == null) {
            return;
        }
        table = (TTable) getComponent("TABLE");
        table.setValue(eAssociateChoose.getData());
        String text = eAssociateChoose.getText();
        value = (TTextField) getComponent("VALUE");
        value.setText(text);
        for (int i = 0; i < table.getRowCount(); i++) {
            if (text.equals(table.getValueAt(i, 0))) {
                table.setSelectedRow(i);
                break;
            }
        }
        table.setSelectedColumn(0);
        callFunction("UI|VALUE|addEventListener",
                     "VALUE->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
    }

    /**
     * 按键事件
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        //this.messageBox("onKeyPressed"+e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            closeWindow();
            return;
        }
        int count = table.getRowCount();
        if (count <= 0) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0) {
                    row = 0;
                }
                table.setSelectedRow(row);
                value.setText( (String) table.getValueAt(row, 0));
                value.selectAll();
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count) {
                    row = count - 1;
                }
                table.setSelectedRow(row);
                value.setText( (String) table.getValueAt(row, 0));
                value.selectAll();
                break;
            case KeyEvent.VK_ENTER:
                onSelect();
                break;
            default:
                table.clearSelection();
                break;
        }
    }

    /**
     * 选中
     */
    public void onSelect() {
        int row = table.getSelectedRow();
        String startTag = eAssociateChoose.getStartTag();
        //this.messageBox("startTag"+startTag);
        String endTag = eAssociateChoose.getEndTag();
        //this.messageBox("endTag"+endTag);

        if (row == -1) {
            eAssociateChoose.setText(value.getText());
        }

        else {
            //this.messageBox("关联模版文件名："+(String) table.getValueAt(row, 3));
            Vector d = table.getValue();
            //this.messageBox("关联模版文件名"+(((Vector)d.get(row))).get(3));
            String fileName = (String) ( ( (Vector) d.get(row))).get(3);

            //this.messageBox("file name" + fileName);
            if (!fileName.equals("")) {
                //删除对范围的内容；
                //delete(startTag,endTag,false);
                eAssociateChoose.clear();
                //this.messageBox("==startTag=="+startTag);
                //插入子模版内容
                EFixed fiexed = (EFixed) eAssociateChoose.getPM().
                    getFocusManager().findObject(startTag,
                                                 EComponent.FIXED_TYPE);
                //this.messageBox("fixed" + fiexed.getName());
                fiexed.setFocusLast();

                //打开文件插入控件;
                //insertFile(SUB_TEMPLATE_PAHT,fileName);

                boolean b = fiexed.getPM().getFocusManager().onInsertFile(
                    "JHW/片语", fileName, 2, false);

               //删除一行子模版开始的回车
               fiexed.setFocusLast();
               boolean b1=fiexed.deleteChar();

               //this.messageBox("b1=="+b1);
               //删除一行结束符子模版结束的回车
               EFixed fiexedEnd = (EFixed) eAssociateChoose.getPM().
                    getFocusManager().findObject(endTag, EComponent.FIXED_TYPE);
               //this.messageBox("fiexedEnd.hasPreviousLink()=="+fiexedEnd.hasPreviousLink());
               fiexedEnd.setFocus(0);
               //this.messageBox("name"+fiexedEnd.getName());
               fiexedEnd.backspaceChar();
               //

            }
            else {
                //删除范围内的内容；
                //delete(startTag, endTag,true);
                 eAssociateChoose.clear();
            }
            eAssociateChoose.setText( (String) table.getValueAt(row, 0));
        }
        eAssociateChoose.getPM().getFocusManager().update();
        closeWindow();
    }

    /**
     * 确定
     */
    public void onOK() {
        onSelect();
    }

    /**
     * 取消
     */
    public void onCancel() {
        closeWindow();
    }

}
