package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TWord;

/**
 * <p>Title:子模板预览弹窗 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2014 </p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wanglong 2014.9.8
 * @version 1.0
 */
public class EMRSubTemplatePreviewControl extends TControl {

    private TParm parameter;
    private TParm node;
    private TWord word;

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        parameter = this.getInputParm();
        node = (TParm) parameter.getData("NODE");
        word = (TWord) parameter.getData("TWORD");
        TWord word = (TWord) this.getComponent("WORD");
        String isPhrase = node.getValue("LEAF_FLG");
        if (isPhrase.equals("Y")) {
            if (word.onOpen(node.getValue("FILE_PATH"), node.getValue("FILE_NAME"), 2, false)) { // 打开模版文件
                word.onEditWord();
                word.setCanEdit(true); // 设置可编辑
            }
        }
        word.getPageManager().setPageWidth(390);//设置编辑区大小
        word.getPageManager().setPageHeight(340);
        word.getPageManager().setImageableX(15);//设置外留白
        word.getPageManager().setImageableY(15);
        word.getPageManager().setImageableWidth(word.getPageManager().getPageWidth() - 15 * 2);
        word.getPageManager().setImageableHeight(word.getPageManager().getPageHeight() - 15 * 2);
        word.getPageManager().getEditInsets().top = 0;//设置内留白
        word.getPageManager().getEditInsets().bottom = 0;
        word.getPageManager().getEditInsets().left = 0;
        word.getPageManager().getEditInsets().right = 0;
        word.getPageManager().setPageSizeModify();
        word.getPageManager().update();
    }

    /**
     * 传回
     */
    public void onFetchBack() {
        String filePath = node.getValue("FILE_PATH");
        String fileName = node.getValue("FILE_NAME");
        EComponent com = word.getFocusManager().getFocus();
        ECapture firstCapture = null;
        if (com != null) {
            if (com instanceof ECapture) {
                firstCapture = (ECapture) com;
            }
        }
        word.getFocusManager().onInsertFile(filePath, fileName, 2, false);
        if (firstCapture != null) {
            firstCapture.setFocusLast();
            firstCapture.deleteChar();
        }
        word.update();
        this.closeWindow();
    }
}
