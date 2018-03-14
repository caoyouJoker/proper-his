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
	 * ��ȡExcel��������
	 * 
	 * @param InputStream
	 * @return Map ������Ԫ���������ݵ�Map����
	 * @author zengwendong
	 */
	public Map<Integer, Map<String, String>> readDefaultExcelContent(String[] fields)
			throws Exception {
		Map<Integer, Map<String, String>> content=readExcelContent(0,1,fields);
		return content;
	}
	/**
	 * ��ȡExcel��������
	 * 
	 * @param InputStream
	 * @return Map ������Ԫ���������ݵ�Map����
	 * @author zengwendong
	 */
	public Map<Integer, Map<String, String>> readExcelContent(int sheetNo,int startRow,String[] fields)
			throws Exception {
		if (wb == null) {
			throw new Exception("Workbook����Ϊ�գ�");
		}
		Map<Integer, Map<String, String>> content = new HashMap<Integer, Map<String, String>>();
		sheet = wb.getSheet(sheetNo);
		// �õ�������
		int rowNum = sheet.getRows();
		int colNum = sheet.getColumns();
		int size=fields==null?0:fields.length;
		Cell cell = null;
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���
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
		JXLReadExcelUtil  jre=new JXLReadExcelUtil("C:\\Users\\Administrator\\Desktop\\2013-2014�껼�߻�������.xls");
		String[] fields={"�����","ʡ��","����","����"};
		Map<Integer, Map<String, String>> map=jre.readExcelContent(0, 1, fields);
		System.out.println("============="+map);
	}
}
