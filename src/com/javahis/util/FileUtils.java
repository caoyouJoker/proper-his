package com.javahis.util;

import java.io.File;

/**
 * <p>
 * Title: �ļ�����������
 * </p>
 * 
 * <p>
 * Description: �ļ�����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2017.4.13
 * @version 1.0
 */
public class FileUtils {

	/**
	 * ʵ��
	 */
	private static FileUtils instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static synchronized FileUtils getInstance() {
		if (instanceObject == null)
			instanceObject = new FileUtils();
		return instanceObject;
	}

	/**
	 * ɾ��ָ���ļ����ļ�
	 * 
	 * @param path
	 * @return
	 */
	public boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * ɾ���ļ���
	 * 
	 * @param folderPath
	 */
	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
