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
 * Title: 多重耐药菌监测结果汇总表
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

public class INFMultipleDrugUtil extends TControl {
	private static final INFMultipleDrugUtil INSTANCE = new INFMultipleDrugUtil();

	private INFMultipleDrugUtil() {
	}

	public static INFMultipleDrugUtil getInstance() {

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
//		System.out.println("111111111111111");
		table = mainTable;
		// 得到talble的头
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
//		System.out.println("222222222");
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
//		System.out.println("333333333333");
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
//		System.out.println("4444444444444");
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
//		System.out.println("55555555555");
		// 取得需要的数据
		Vector header = (Vector) headerDate.get(0);
		Vector colView = (Vector) headerDate.get(1);
		Vector dateType = (Vector) headerDate.get(2);
		// 取得列数
		int colNum = header.size();
		System.out.println("header++++" + header);
		// 设置格式化列标题
		WritableFont fontHeader = new WritableFont(
				WritableFont.createFont("仿宋_GB2312"), 18, WritableFont.BOLD);
		// 定义列标题
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
		// 设置格式化标题下信息 填表人 联系电话 填表人所在科室
		WritableFont fontDetail = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetail = new WritableCellFormat(fontDetail);

		// 设置格式化表头
		WritableFont fontDetailHead1 = new WritableFont(
				WritableFont.createFont("宋体"), 16, WritableFont.BOLD);
		WritableCellFormat formatDetailHead1 = new WritableCellFormat(
				fontDetailHead1);
		WritableFont fontDetailHead2 = new WritableFont(
				WritableFont.createFont("宋体"), 16, WritableFont.BOLD);
		WritableCellFormat formatDetailHead2 = new WritableCellFormat(
				fontDetailHead2);

		// 设置格式化主数据表头 机构名称 所属季度
		WritableFont fontName1 = new WritableFont(
				WritableFont.createFont("楷体 _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatName1 = new WritableCellFormat(fontName1);
		WritableFont fontName2 = new WritableFont(
				WritableFont.createFont("楷体 _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatName2 = new WritableCellFormat(fontName2);
		WritableFont fontName = new WritableFont(WritableFont.createFont("宋体"),
				10, WritableFont.NO_BOLD);
		WritableCellFormat formatName = new WritableCellFormat(fontName);
		//主数据表头
		WritableFont fontDetailHeader= new WritableFont(
				WritableFont.createFont("楷体 _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatDetailHeader = new WritableCellFormat(fontDetailHeader);

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
			 formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			// 设置自动换行
			formatHeader.setWrap(true);
			// 设置表头居中
			 formatDetailHead1.setAlignment(jxl.format.Alignment.CENTRE);
//			formatDetailHead1.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetailHead1.setWrap(true);
			 formatDetailHead2.setAlignment(jxl.format.Alignment.CENTRE);
//			formatDetailHead2.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetailHead2.setWrap(true);
			// 设置主数据表头
			// formatName1.setAlignment(jxl.format.Alignment.CENTRE);
			formatName1.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName1.setWrap(true);
			// formatName2.setAlignment(jxl.format.Alignment.CENTRE);
			formatName2.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName2.setWrap(true);

			// formatName.setAlignment(jxl.format.Alignment.CENTRE);
			formatName.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName.setWrap(true);
			//主数据表头
			formatDetailHeader.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetailHeader.setWrap(true);
			// 设置标题下信息靠左
			formatDetail.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetail.setWrap(true);

			formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
			formatDateRight.setWrap(true);

			formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
			formatDateLeft.setWrap(true);
		} catch (WriteException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
		// 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
		// 以及单元格内容为test
		Label title = new Label(0, 0, "天津市医疗机构多重耐药菌监测结果汇总表", formatHeader);
		Label Detail = new Label(0, 1, "填表人:        联系电话:       填表人所在科室:",
				formatDetail);

		Label DetailHead1 = new Label(2, 2, "多重耐药菌感染发现率", formatDetailHead1);
		Label DetailHead2 = new Label(13, 2, "多重耐药菌感染检出率", formatDetailHead2);

		Label Name1 = new Label(0, 2, "医疗机构名称", formatName1);
		Label Name = new Label(0, 4, "泰达国际心血管病医院", formatName);
		Label Name2 = new Label(1, 2, "监测数据所属季度", formatName2);
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
			sheet1.addCell(Name1);
			sheet1.addCell(Name);
			sheet1.addCell(Name2);
			// 合并单元格
			sheet1.mergeCells(0, 0, 12, 0);
			sheet1.mergeCells(0, 1, 12, 1);
			sheet1.mergeCells(2, 2, 12, 2);
			sheet1.mergeCells(13, 2, 27, 2);
			sheet1.mergeCells(0, 2, 0, 3);
			sheet1.mergeCells(1, 2, 1, 3);
			sheet1.setRowView(0, 500);
			sheet1.setRowView(1, 500);
			sheet1.setRowView(2, 500);
//			System.out.println("表头数量：：：：：：：：：：：" + colNum);
			// 设置列宽
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// 循环设置列宽（是table上面长度的10分之一）
				sheet1.setColumnView(n, colViwe / 10);
			}

			// 添加表头
			for (int i = 1; i < colNum + 1; i++) {
				// 添加表头的一行（标题）
				Label row1 = new Label(i, 3, (String) header.get(i -1),
						formatDetailHeader);
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
			for (int j = 3; j < dateRow + 3; j++) { // 行
				for (int n = 1; n < colNum + 1; n++) { // 列
					// 每一个格的数据
					Label dateCell;
					// 得到该列的类型
					// System.out.println("n:::::::"+n);
					String type = (String) dateType.get(n - 1);
					// 如果是数字类型就靠右
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// 设置该数据靠右
						dateCell = new Label(n, j +1, date.getValue(
								title1[n - 1], j-3), formatDateRight);
					} else {
						// 设置该数据靠左
						dateCell = new Label(n, j + 1, date.getValue(
								title1[n - 1], j-3), formatDateLeft);
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
