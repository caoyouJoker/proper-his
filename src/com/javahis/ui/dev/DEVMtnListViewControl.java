package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;

/**
 * <p>
 * Title: �豸ά���嵥�鿴
 * </p>
 * 
 * <p>
 * Description: �豸ά���嵥�鿴
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
     * WORD����
     */
    private TWord word;
    
    private TParm parm;
	
	/**
	 * ��ʼ��
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
//		//����
//        word.setShowZoomComboTag("ShowZoom");
//        //����
//        word.setFontComboTag("ModifyFontCombo");
//        //����
//        word.setFontSizeComboTag("ModifyFontSizeCombo");
//        //���
//        word.setFontBoldButtonTag("FontBMenu");
//        //б��
//        word.setFontItalicButtonTag("FontIMenu");
//        //ȡ���༭
        this.word.setCanEdit(false);
	}

    /**
     * ��ģ��
     */
    public void onOpenTemplet() {
    	//ȡ���༭
//        this.word.setCanEdit(false);
//        this.word.onOpen("JHW/�ţ������ﲡ��/��(��)�ﲡ��", "1111", 2, false);
        this.word.onOpen(this.parm.getValue("FILE_PATH"), this.parm.getValue("FILE_NAME"), 3, true);
//        this.word.onOpen("JHW\\�豸������¼\\2015", "2H2BL_2H2BL030901001_20150708", 3, true);
    }
    
    /**
     * ��ӡ
     */
    public void onPrint(){
    	this.word.onPreviewWord();
    	this.word.print();
    	this.closeWindow();
    }
    
    /**
     * �õ�WORD����
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }
}
