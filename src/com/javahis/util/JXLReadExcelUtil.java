package com.javahis.util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import jxl.*;
import jxl.read.biff.BiffException;

/**
 * 
 * @author Administrator
 *
 */
public class JXLReadExcelUtil {
	
	private Workbook wb;
	private Sheet sheet;
	
	public JXLReadExcelUtil(String filepath){
		if (filepath == null) {
			return;
		}
		String ext = filepath.substring(filepath.lastIndexOf("."));
		try {
			InputStream is = new FileInputStream(filepath);
			if (".xls".equals(ext)) {
				wb = Workbook.getWorkbook(is);
			}  else {
				wb = null;
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 读取Excel数据内容
	 * 
	 * @param InputStream
	 * @return Map 包含单元格数据内容的Map对象
	 * @author zengwendong
	 */
	public Map<Integer, Map<String, String>> readDefaultExcelContent(String[] fields)
			throws Exception {
		Map<Integer, Map<String, String>> content=readExcelContent(0,1,fields);
		return content;
	}
	/**
	 * 读取Excel数据内容
	 * 
	 * @param InputStream
	 * @return Map 包含单元格数据内容的Map对象
	 * @author zengwendong
	 */
	public Map<Integer, Map<String, String>> readExcelContent(int sheetNo,int startRow,String[] fields)
			throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		Map<Integer, Map<String, String>> content = new HashMap<Integer, Map<String, String>>();
		sheet = wb.getSheet(sheetNo);
		// 得到总行数
		int rowNum = sheet.getRows();
		int colNum = sheet.getColumns();
		int size=fields==null?0:fields.length;
		Cell cell = null;
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = startRow; i <rowNum; i++) {
			int j = 0;
			Map<String, String> cellValue = new HashMap<String, String>();
			while (j < colNum) {
				cell = (Cell)sheet.getCell(j, i);
				if(cell==null)
					continue;
				String obj = cell.getContents();
				cellValue.put((j<=(size-1)?fields[j]:String.valueOf(j)), obj);
				j++;
			}
			content.put(i, cellValue);
		}
		return content;
	}
	
	public static void main(String[] args) throws Exception {
		JXLReadExcelUtil  jre=new JXLReadExcelUtil("C:\\Users\\Administrator\\Desktop\\2013-2014年患者基本资料.xls");
		String[] fields={"就诊号","省市","区县","病种"};
		Map<Integer, Map<String, String>> map=jre.readExcelContent(0, 1, fields);
		System.out.println("============="+map);
	}
}
