package com.javahis.ui.inf;

import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.write.*;

import java.sql.Timestamp;

import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;

import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.control.TControl;


/**
 * <p>
 * Title: 感染病例明细报告
 * </p>
 * 
 * <p>
 * Description: EXCEL模板
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class INFCaseDetailReportUtil extends TControl {
	private static final INFCaseDetailReportUtil INSTANCE = new INFCaseDetailReportUtil();

	private INFCaseDetailReportUtil() {
	}

	public static INFCaseDetailReportUtil getInstance() {

		return INSTANCE;
	}

	private TTable table;
	private String parmMap = "";

	/**
	 * 导出EXCEL的方法的接口
	 * 
	 * @param table
	 *            TTable table对象
	 * @param defName
	 *            String 默认文件的名字
	 */
	public void exportExcel(TTable mainTable, String defName) {
		System.out.println("111111111111111");
		table = mainTable;
		// 得到table的头
		String header = mainTable.getHeader();
		// 只得到table上显示的数据（PS：隐藏数据得不到）
		TParm mainDate = mainTable.getShowParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("无导出EXCEL数据！");
			return;
		}
		// 调用导出excel方法（标题头，主数据，文件名字）
		exeSaveExcel(header, mainDate, defName);
	}

	/**
	 * 导出excel文件
	 * 
	 * @param headerDate
	 *            String
	 * @param mainDate
	 *            TParm
	 * @param fileDefName
	 *            默认文件名字
	 */

	public void exeSaveExcel(String header, TParm mainDate, String fileDefName) {
		System.out.println("222222222");
		// 重新整理标题头数据（只留下标题）
		Vector arrHeader = arrHeader(header);
		System.out.println("222zhongde " + arrHeader);
		// 标准SWING
		JFileChooser chooser = new JFileChooser();
		// 设置当前的目录（目前写死）
		File dir = new File("C:\\JavaHis\\Excel");
		if (!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		// 利用当前时间附加到默认文件名上
		Timestamp optTime = SystemTool.getInstance().getDate();
		// String opttime=StringTool.getString(optTime, "yyyyMMddHHmmss");
		// String a = opttime.substring(4,6);
		// System.out.println("字符串时间截取:"+a);
		fileDefName = fileDefName
				+ StringTool.getString(optTime, "yyyyMMddHHmmss");
		// 默认的文件保存名字
		chooser.setSelectedFile(new File(fileDefName));// 提供默认名
		// 设置对话框的标题
		chooser.setDialogTitle("导出EXCEL界面");
		// 设置过滤（扩展名）
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toUpperCase().endsWith(".XLS");
			}

			public String getDescription() {
				return "Excel (*.xls)";
			}
		});

		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getAbsolutePath();
			this.writeFile(arrHeader, mainDate, path);
		}
	}

	/**
	 * 整理table的标题头，使之只省下标题
	 * 
	 * @param date
	 *            String 缺身省状态下的头
	 * @return Vector
	 */
	public Vector arrHeader(String date) {
		// 把标题数据按；区间为字符数组
		String[] indate = date.split(";");
		Vector colAndType = getColumnView(indate);
		Vector result = new Vector();
		Vector header = new Vector();
		// 循环去掉--> ，长度，类型等
		for (int j = 0; j < indate.length; j++) {
			// System.out.println("逗号的值:::"+indate[j].substring(0, (int)
			// (indate[j].indexOf(","))));
			// 截取‘第一个’逗号前的文字--标题
			String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
			header.add(a);
		}
		// 第一组是第一行的题目
		result.add(header);
		// 第二组是列宽
		result.add(colAndType.get(0));
		// 第三行是数据类型
		result.add(colAndType.get(1));

		// System.out.println("header++++"+header);
		return result;
	}

	public Vector getColumnView(String[] date) {
		System.out.println("333333333333");
		Vector result = new Vector();
		// 存储列宽和数据类型
		Vector colView = new Vector();
		Vector dateType = new Vector();
		// 行数
		// System.out.println("date::::"+date);
		int col = date.length;
		for (int i = 0; i < col; i++) {
			// 从第一个逗号后面一位开始
			// System.out.println("date[]::::"+date[i]);
			int start = date[i].indexOf(",") + 1;
			int end;
			String type;
			// 没有第二个逗号的时候说明没有数据类型
			if (date[i].indexOf(",", start) == -1) {
				// 没有第二个逗号取到最后--长度
				end = date[i].length();
				// 没有类型就默认为字符串
				type = "String";
			} else {
				// 有就取到第二个逗号以前--长度
				end = date[i].indexOf(",", start);
				// 取得省下的字符--类型
				type = date[i].substring(end + 1);
			}
			// 存储该列的长度
			String view = date[i].substring(start, end);
			colView.add(view);
			dateType.add(type);
			// System.out.println("colView:::"+colView);

		}
		// System.out.println("dateType:::"+dateType);
		result.add(colView);
		result.add(dateType);
		return result;
	}

	/**
	 * 打开保存对话框选择路径
	 * 
	 * @param header
	 *            Vector
	 * @param date
	 *            TParm
	 * @param FileName
	 *            String
	 */
	public void writeFile(Vector header, TParm date, String FileName) {
		System.out.println("4444444444444");
		// 创建一个文件对象
		File file;
		try {
			// 过路径中包含.xls就不加后缀
			if (!FileName.contains(".xls")) {
				file = new File(FileName + ".xls");
			} else
				file = new File(FileName);
			// 判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
			if (file.exists()
					&& JOptionPane.showConfirmDialog(null, "save",
							"该文件已经存在,是否覆盖？", JOptionPane.YES_NO_OPTION) == 1)
				return;
			// 把文件变成输出流
			FileOutputStream fileOutStream = null;
			fileOutStream = new FileOutputStream(file);
			// 执行数据整理并导出
			exportToExcel(header, date, fileOutStream);
			fileOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}

	/**
	 * 导出excel整理数据和设置格式（主方法）
	 * 
	 * @param headerDate
	 *            Vector
	 * @param date
	 *            TParm
	 * @param os
	 *            OutputStream
	 */

	public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {
		System.out.println("55555555555");
		// 取得需要的数据
		Vector header = (Vector) headerDate.get(0);
		Vector colView = (Vector) headerDate.get(1);
		Vector dateType = (Vector) headerDate.get(2);
		// 取得列数
		int colNum = header.size();
		System.out.println("header++++" + header);
		// 设置格式化列标题
		WritableFont fontHeader = new WritableFont(
				WritableFont.createFont("宋体"), 18, WritableFont.BOLD);
		// 定义列标题
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
		// 设置格式化标题下信息 医院全称 填表人 联系电话 
		WritableFont fontDetail = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetail = new WritableCellFormat(fontDetail);

		// 设置格式化表头 
		//患者基本信息
		WritableFont fontDetailHead1 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead1 = new WritableCellFormat(
				fontDetailHead1);
		//医院感染部位1
		WritableFont fontDetailHead2 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead2 = new WritableCellFormat(
				fontDetailHead2);
		//感染诊断
		WritableFont fontDetailHead21 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead21 = new WritableCellFormat(
				fontDetailHead21);
		//送检情况
		WritableFont fontDetailHead22 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead22 = new WritableCellFormat(
				fontDetailHead22);

		//医院感染部位2
		WritableFont fontDetailHead3 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead3 = new WritableCellFormat(
				fontDetailHead3);
		//感染诊断
		WritableFont fontDetailHead31 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead31 = new WritableCellFormat(
				fontDetailHead31);
		//送检情况
		WritableFont fontDetailHead32 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead32 = new WritableCellFormat(
				fontDetailHead32);
		
		//医院感染部位3
		WritableFont fontDetailHead4 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead4 = new WritableCellFormat(
				fontDetailHead4);
		//感染诊断
		WritableFont fontDetailHead41 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead41 = new WritableCellFormat(
				fontDetailHead41);
		//送检情况
		WritableFont fontDetailHead42 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead42 = new WritableCellFormat(
				fontDetailHead42);
		
		//医院感染部位4
		WritableFont fontDetailHead5 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead5 = new WritableCellFormat(
				fontDetailHead5);
		//感染诊断
		WritableFont fontDetailHead51 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead51 = new WritableCellFormat(
				fontDetailHead51);
		//送检情况
		WritableFont fontDetailHead52 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead52 = new WritableCellFormat(
				fontDetailHead52);
		
		//治疗与转归
		WritableFont fontDetailHead6 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead6 = new WritableCellFormat(
				fontDetailHead6);
		
		//易感因素
		WritableFont fontDetailHead7 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead7 = new WritableCellFormat(
				fontDetailHead7);
		//侵入性操作
		WritableFont fontDetailHead71 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead71 = new WritableCellFormat(
				fontDetailHead71);
		//其他易感因素
		WritableFont fontDetailHead72 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead72 = new WritableCellFormat(
				fontDetailHead72);
		//在ICU治疗过
		WritableFont fontDetailHead73 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead73 = new WritableCellFormat(
				fontDetailHead73);
		//感染病例的手术情况
		WritableFont fontDetailHead8 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead8 = new WritableCellFormat(
				fontDetailHead8);
		//主数据表头设置
		WritableFont fontDetailName = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailName = new WritableCellFormat(
				fontDetailName);
		
		// 设置格式化主数据
		WritableFont fontDate = new WritableFont(
				WritableFont.createFont("楷体 _GB2312"), 14, WritableFont.NO_BOLD);
		// 格式化一般的数据（靠左）
		WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
		try {
			formatDateLeft.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		// 格式化数字（靠右）
		WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);
		try {
			formatDateRight.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}

		try {
			// 设定列标题居中
			//formatHeader.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			// 设置自动换行
			formatHeader.setWrap(true);
			// 设置表头居中
			formatDetailHead1.setAlignment(Alignment.CENTRE);
			formatDetailHead1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDetailHead1.setWrap(true);
			formatDetailHead2.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead2.setWrap(true);
			formatDetailHead21.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead21.setWrap(true);
			formatDetailHead22.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead22.setWrap(true);
			formatDetailHead3.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead3.setWrap(true);
			formatDetailHead31.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead31.setWrap(true);
 
			formatDetailHead32.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead32.setWrap(true);
 
			formatDetailHead4.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead4.setWrap(true);
 
			formatDetailHead41.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead41.setWrap(true);
 
			formatDetailHead42.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead42.setWrap(true);
 
			formatDetailHead5.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead5.setWrap(true);
 
			formatDetailHead51.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead51.setWrap(true);
 
			formatDetailHead52.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead52.setWrap(true);
 
			formatDetailHead6.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead6.setWrap(true);
 
			formatDetailHead7.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead7.setWrap(true);
 
			formatDetailHead71.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead71.setWrap(true);
			formatDetailHead72.setAlignment(Alignment.CENTRE);
			formatDetailHead72.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead72.setWrap(true);
 
			formatDetailHead73.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead73.setWrap(true);
 
			formatDetailHead8.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead8.setWrap(true);
			
			//主数据表头
//			formatDetailName.setAlignment(Alignment.CENTRE);
			formatDetailName.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDetailName.setWrap(true);

			// 设置标题下信息靠左
			formatDetail.setAlignment(jxl.format.Alignment.LEFT);
			formatDetail.setWrap(true);
			formatDateRight.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDateRight.setWrap(true);
			formatDateLeft.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDateLeft.setWrap(true);
		} catch (WriteException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
		// 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
		// 以及单元格内容为test
		Label title = new Label(0, 0, "医院感染病例报告", formatHeader);
		Label Detail = new Label(0, 1, "医院全称:泰达国际心血管病医院     填表人:           填表人联系电话:",
				formatDetail);

		Label DetailHead1 = new Label(0, 2, "患者基本信息", formatDetailHead1);
		Label DetailHead2 = new Label(11, 2, "医院感染部位1", formatDetailHead2);
		Label DetailHead21 = new Label(11, 3, "感染诊断", formatDetailHead21);
		Label DetailHead22 = new Label(14, 3, "送检情况", formatDetailHead22);
		Label DetailHead3 = new Label(19, 2, "医院感染部位2", formatDetailHead3);
		Label DetailHead31 = new Label(19, 3, "感染诊断", formatDetailHead31);
		Label DetailHead32 = new Label(22, 3, "送检情况", formatDetailHead32);
		Label DetailHead4 = new Label(27, 2, "医院感染部位3", formatDetailHead4);
		Label DetailHead41 = new Label(27, 3, "感染诊断", formatDetailHead41);
		Label DetailHead42 = new Label(30, 3, "送检情况", formatDetailHead42);
		Label DetailHead5 = new Label(35, 2, "医院感染部位4", formatDetailHead5);
		Label DetailHead51 = new Label(35, 3, "感染诊断", formatDetailHead51);
		Label DetailHead52 = new Label(38, 3, "送检情况", formatDetailHead52);
		Label DetailHead6 = new Label(43, 2, "治疗于转归", formatDetailHead6);
		Label DetailHead7 = new Label(45, 2, "易感因素", formatDetailHead7);
		Label DetailHead71 = new Label(45, 3, "侵入性操作", formatDetailHead71);
		Label DetailHead72 = new Label(51, 3, "其他易感因素", formatDetailHead72);
		Label DetailHead73 = new Label(74, 4, "该患者是否进入ICU", formatDetailHead73);
		Label DetailHead8 = new Label(75, 2, "感染病例的手术情况", formatDetailHead8);

		try {
			// 打开文件
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// 生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
			WritableSheet sheet1 = workbook.createSheet("报  表", 0);
			// 将定义好的单元格添加到工作表中
			sheet1.addCell(title);
			sheet1.addCell(Detail);
			sheet1.addCell(DetailHead1);
			sheet1.addCell(DetailHead2);
			sheet1.addCell(DetailHead21);
			sheet1.addCell(DetailHead22);
			sheet1.addCell(DetailHead3);
			sheet1.addCell(DetailHead31);
			sheet1.addCell(DetailHead32);
			sheet1.addCell(DetailHead4);
			sheet1.addCell(DetailHead41);
			sheet1.addCell(DetailHead42);
			sheet1.addCell(DetailHead5);
			sheet1.addCell(DetailHead51);
			sheet1.addCell(DetailHead52);
			sheet1.addCell(DetailHead6);
			sheet1.addCell(DetailHead7);
			sheet1.addCell(DetailHead71);
			sheet1.addCell(DetailHead72);
			sheet1.addCell(DetailHead73);
			sheet1.addCell(DetailHead8);
			// 合并单元格
		
			sheet1.mergeCells(0, 0, 10, 0);
			sheet1.mergeCells(0, 1, 10, 1);
			sheet1.mergeCells(0, 2, 10, 3);
			sheet1.mergeCells(11, 2, 18, 2);
			sheet1.mergeCells(11, 3, 13, 3);
			sheet1.mergeCells(14, 3, 18, 3);
			sheet1.mergeCells(19, 2, 26, 2);
			sheet1.mergeCells(19, 3, 21, 3);
			sheet1.mergeCells(22, 3, 26, 3);
			sheet1.mergeCells(27, 2, 34, 2);
			sheet1.mergeCells(27, 3, 29, 3);
			sheet1.mergeCells(30, 3, 34, 3);
			sheet1.mergeCells(35, 2, 42, 2);
			sheet1.mergeCells(35, 3, 37, 3);
			sheet1.mergeCells(38, 3, 42, 3);
			sheet1.mergeCells(43, 2, 44, 3);
			sheet1.mergeCells(45, 2, 73, 2);
			sheet1.mergeCells(45, 3, 50, 3);
			sheet1.mergeCells(51, 3, 73, 3);
			sheet1.mergeCells(75, 2, 77, 3);
			//设置单元格大小
			sheet1.setRowView(0, 500);
			sheet1.setRowView(1, 500);
			sheet1.setRowView(2, 500);
			sheet1.setRowView(3, 500);
//			System.out.println("表头数量：：：：：：：：：：：" + colNum);
			// 设置列宽
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// 循环设置列宽（是table上面长度的10分之一）
				sheet1.setColumnView(n, colViwe / 10);
			}

			// 添加表头
			for (int i = 0; i < colNum ; i++) {
				// 添加表头的一行（标题）
				Label row1 = new Label(i, 4, (String) header.get(i),
						formatDetailName);
				sheet1.addCell(row1);
			}
			// 添加主数据
			int dateRow = date.getCount();
			// 得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
			// String map = (String)table.getParmMap();
			String map = "";
			if (table != null) {
				map = (String) table.getParmMap();
			} else {
				map = this.parmMap;
			}
			String[] title1 = map.split(";");
			// 循环添加主数据
			for (int j = 0; j < dateRow ; j++) { // 行
				for (int n = 0; n < colNum ; n++) { // 列
					System.out.println("行：：：："+dateRow);System.out.println("列：：：："+colNum);
					// 每一个格的数据
					Label dateCell;
					// 得到该列的类型
					// System.out.println("n:::::::"+n);
					String type = (String) dateType.get(n);
					// 如果是数字类型就靠右
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// 设置该数据靠右
						dateCell = new Label(n, j + 5, date.getValue(
								title1[n], j), formatDateRight);
					} else {
						// 设置该数据靠左
						dateCell = new Label(n, j + 5, date.getValue(
								title1[n], j), formatDateLeft);
					}
					sheet1.addCell(dateCell);
				}
			}

			workbook.write();
			workbook.close();
			JOptionPane.showMessageDialog(null, "转档成功");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}

	// -----------------------------导出EXCEL的方法---end-----------------------------

}
