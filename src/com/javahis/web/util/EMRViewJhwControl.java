package com.javahis.web.util;

import jdo.sys.MessageTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.util.StringTool;
import com.dongyang.util.TSystem;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRViewJhwControl extends TControl implements DMessageIO   {
	/**
	 * WORD����
	 */
	private TWord word;
	
	private String WORD_NAME = "WORD";
	/**
	 * �������
	 */
	private String caseNo;
	/**
	 * �����ļ����
	 */
	private String fileSeq;
	/**
	 * �����ļ�·��
	 */
	private String filePath;
	/**
	 * �����ļ�����
	 */
	private String fileName;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TSystem.setObject("MessageObject", new MessageTool());
		callFunction("UI|changeLanguage", "zh");
		TSystem.setObject("ZhFontSizeProportion", StringTool.getDouble(TConfig
				.getSystemValue("ZhFontSizeProportion")));
		TSystem.setObject("EnFontSizeProportion", StringTool.getDouble(TConfig
				.getSystemValue("EnFontSizeProportion")));

		this.word = (TWord) this.getComponent("WORD");
	    addEventListener(WORD_NAME+"->" + TKeyListener.KEY_PRESSED, "onKeyPressed");
		initWord();
	}

	/**
	 * 
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * �򿪲���
	 */
	public void initWord() {
		//Ŀ¼���һ����Ŀ¼FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        System.out.println("rootName:"+rootName);
        //ģ��·��������
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        System.out.println("templetPathSer:"+templetPathSer);
        //��ʱ�ļ���ַ
        String tmpFilePath = rootName + templetPathSer + "tmpFile.x";
        System.out.println("tmpFilePath:"+tmpFilePath);
        //�õ�SocketͨѶ����
        TSocket socket = TIOM_FileServer.getSocket();
        //��ȡ��ʱ�ļ�
		String content = new String(TIOM_FileServer.readFile(socket, tmpFilePath));
		System.out.println("content:"+content);
		//ɾ����ʱ�ļ�
		TIOM_FileServer.deleteFile(socket, tmpFilePath);
		//��ȡ�����ļ���Ϣ
		String tmpFile[] = content.split(",");
		if (tmpFile.length < 4) {
			/*
			this.messageBox("�����ļ���Ϣ����ȷ��");
			return;
			*/
			String levelTmpFile[] = new String[0];
			if(content.contains("/")){
				levelTmpFile = content.split("/");
				System.out.println(levelTmpFile[4]+"\\"+levelTmpFile[5]+"\\"+levelTmpFile[6]+"\\"+levelTmpFile[7]);
				System.out.println(levelTmpFile[8]);
				
				word.onOpen(levelTmpFile[4]+"\\"+levelTmpFile[5]+"\\"+levelTmpFile[6]+"\\"+levelTmpFile[7], levelTmpFile[8],3, false);
				
		        //���ÿɱ༭
		        word.setCanEdit(true);
		        //�༭״̬(������)
		        word.onEditWord();
			}else{
				this.messageBox("�����ļ���Ϣ����ȷ��");
				return;
			}
		}else{
			this.caseNo = tmpFile[0];
			this.fileSeq = tmpFile[1];
			this.filePath = tmpFile[2];
			this.fileName = tmpFile[3];
//			if (!CommonUtil.checkInputString(this.caseNo)) {
//				this.messageBox("������Ų���Ϊ�գ�");
//				return;
//			}
//			if (!CommonUtil.checkInputString(this.fileSeq)) {
//				this.messageBox("�����ļ���Ų���Ϊ�գ�");
//				return;
//			}
//			if (!CommonUtil.checkInputString(this.filePath)) {
//				this.messageBox("�����ļ�·������Ϊ�գ�");
//				return;
//			}
//			if (!CommonUtil.checkInputString(this.fileName)) {
//				this.messageBox("�����ļ����Ʋ���Ϊ�գ�");
//				return;
//			}
//			if (!this.word.onOpen(this.filePath, this.fileName, 3, false)) {
//				this.messageBox("������ʧ�ܣ�");
//				return;
//			}


			word.onOpen(this.filePath, this.fileName,3, false);
			
	        //���ÿɱ༭
	        word.setCanEdit(true);
	        //�༭״̬(������)
	        word.onEditWord();
			
		}
	}
}
            
