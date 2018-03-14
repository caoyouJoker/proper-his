package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;

/**
 * <p>
 * Title: 设备维护清单查看
 * </p>
 * 
 * <p>
 * Description: 设备维护清单查看
 * </p>
 * <p>
 * Copyright: BlueCore 2015
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMtnListViewControl extends TControl {
	
	/**
     * WORD对象
     */
    private TWord word;
    
    private TParm parm;
	
	/**
	 * 初始化
	 */
	public void onInit(){		
		Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        this.parm = (TParm) obj;
//        System.out.println("mmmmparm:"+parm);
		this.word = this.getTWord("WORD");
		onOpenTemplet();
//		//缩放
//        word.setShowZoomComboTag("ShowZoom");
//        //字体
//        word.setFontComboTag("ModifyFontCombo");
//        //字体
//        word.setFontSizeComboTag("ModifyFontSizeCombo");
//        //变粗
//        word.setFontBoldButtonTag("FontBMenu");
//        //斜体
//        word.setFontItalicButtonTag("FontIMenu");
//        //取消编辑
        this.word.setCanEdit(false);
	}

    /**
     * 打开模版
     */
    public void onOpenTemplet() {
    	//取消编辑
//        this.word.setCanEdit(false);
//        this.word.onOpen("JHW/门（急）诊病历/门(急)诊病历", "1111", 2, false);
        this.word.onOpen(this.parm.getValue("FILE_PATH"), this.parm.getValue("FILE_NAME"), 3, true);
//        this.word.onOpen("JHW\\设备保养记录\\2015", "2H2BL_2H2BL030901001_20150708", 3, true);
    }
    
    /**
     * 打印
     */
    public void onPrint(){
    	this.word.onPreviewWord();
    	this.word.print();
    	this.closeWindow();
    }
    
    /**
     * 得到WORD对象
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }
}
