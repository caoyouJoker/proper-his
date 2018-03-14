package com.javahis.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import jdo.sys.SystemTool;

import org.apache.commons.beanutils.BeanUtils;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: CSV工具类
 * </p>
 * 
 * <p>
 * Description: CSV工具类
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
 * @author wangb 2017.4.7
 * @version 1.0
 */
public class CSVUtils {
	
	/**
	 * 实例
	 */
	private static CSVUtils instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static synchronized CSVUtils getInstance() {
		if (instanceObject == null)
			instanceObject = new CSVUtils();
		return instanceObject;
	}

	/**
	 * 导出CSV文件
	 * 
	 * @param exportData
	 * @param map
	 * @param outPutPath
	 * @param fileName
	 * @return csvFile
	 */
	public File createCSVFile(List exportData, LinkedHashMap map,
			String outPutPath, String fileName) {
		File csvFile = null;
		BufferedWriter csvFileOutputStream = null;
		try {
			File file = new File(outPutPath);
			if (!file.exists()) {
				file.mkdir();
			}
			csvFile = File.createTempFile(fileName, ".csv",
					new File(outPutPath));
			csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "GB2312"), 1024);
			for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator
					.hasNext();) {
				Entry propertyEntry = (Entry) propertyIterator.next();
				csvFileOutputStream
						.write("\"" + (String) propertyEntry.getValue() != null ? (String) propertyEntry
								.getValue()
								: "" + "\"");
				if (propertyIterator.hasNext()) {
					csvFileOutputStream.write(",");
				}
			}
			csvFileOutputStream.newLine();
			for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {
				Object row = (Object) iterator.next();
				for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator
						.hasNext();) {
					Entry propertyEntry = (Entry) propertyIterator.next();
					csvFileOutputStream.write((String) BeanUtils.getProperty(
							row, (String) propertyEntry.getKey()));
					if (propertyIterator.hasNext()) {
						csvFileOutputStream.write(",");
					}
				}
				if (iterator.hasNext()) {
					csvFileOutputStream.newLine();
				}
			}
			csvFileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvFile;
	}
	
	/**
     * 导出CSV文件
     * 
     * @param file csv文件
     * @param obj 数据集合
     * @return
     */
	public boolean exportCsv(File file, Object obj) {
		boolean isSucess = false;
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			// 本地默认路径
			String tempPath = "C:\\JavaHisFile\\temp\\csv";
			if (file == null) {
				file = new File(tempPath);
			}

			if (file.isDirectory()) {
				if (!file.exists()) {
					file.mkdir();
				}
				Timestamp optTime = SystemTool.getInstance().getDate();
				file = new File(tempPath + File.separator
						+ StringTool.getString(optTime, "yyyyMMddHHmmss")
						+ ".csv");
			}

			out = new FileOutputStream(file);
			osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
			List<String> dataList = new ArrayList<String>();

			// 如果传入参数为TTable
			if (obj instanceof TTable) {
				TTable table = (TTable) obj;
				// 取得表格显示数据
				TParm parm = table.getShowParmValue();
				int count = parm.getCount();

				// 取得表格标题
				String[] headerArray = table.getHeader().split(";");
				int headerArrayLength = headerArray.length;
				String header = "";
				for (int i = 0; i < headerArrayLength; i++) {
					header = header + headerArray[i].split(",")[0];
					if (i < headerArrayLength - 1) {
						header = header + ",";
					}
				}
				dataList.add(header);

				// 根据表格列显示顺序依次拼接每行csv格式数据
				String[] nameArray = table.getParmMap().split(";");
				int nameArrayLength = nameArray.length;
				String dataStr = "";
				String tempStr = "";
				for (int j = 0; j < count; j++) {
					dataStr = "";
					for (int k = 0; k < nameArrayLength; k++) {
						tempStr = parm.getValue(nameArray[k], j);
						// CSV的一个列中包含逗号的话，需要使用双引号进行修饰
						if (tempStr.contains(",")) {
							tempStr = "\"" + tempStr + "\"";
						}
						dataStr = dataStr + tempStr;
						if (k < nameArrayLength - 1) {
							dataStr = dataStr + ",";
						}
					}
					dataList.add(dataStr);
				}
			} else if (obj instanceof ArrayList) {
				dataList = (ArrayList) obj;
			}

			if (dataList != null && !dataList.isEmpty()) {
				if (dataList.get(0) instanceof String) {
					for (String data : dataList) {
						bw.append(data).append("\r");
					}
					isSucess = true;
				} else {
					isSucess = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isSucess = false;
		} finally {
			if (bw != null) {
				try {
					bw.close();
					bw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
					osw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return isSucess;
	}
}
