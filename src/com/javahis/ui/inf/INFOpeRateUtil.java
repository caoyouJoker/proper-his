package com.javahis.ui.inf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import jdo.sys.SystemTool;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

public class INFOpeRateUtil extends TControl {
	private static final INFOpeRateUtil INSTANCE = new INFOpeRateUtil();

	private INFOpeRateUtil() {
	}

	public static INFOpeRateUtil getInstance() {

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
//		System.out.println("222zhongde " + arrHeader);
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
				WritableFont.createFont("宋体"), 18, WritableFont.BOLD);
		// 定义列标题
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
		// 设置格式化标题下信息  医院名称：泰达国际心血管病医院
		WritableFont fontDetail = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetail = new WritableCellFormat(fontDetail);

		// 设置格式化表头
		WritableFont fontDetailHead1 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead1 = new WritableCellFormat(
				fontDetailHead1);

		// 设置格式化主数据表头 机构名称 所属季度
		WritableFont fontName1 = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatName1 = new WritableCellFormat(fontName1);
		WritableFont fontName = new WritableFont(WritableFont.createFont("宋体"),
				14, WritableFont.NO_BOLD);
		WritableCellFormat formatName = new WritableCellFormat(fontName);

		// 设置格式化主数据
		WritableFont fontDate = new WritableFont(
				WritableFont.createFont("宋体"), 14, WritableFont.NO_BOLD);
		// 格式化一般的数据（靠左）
		WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
		try {
			formatDateLeft.setWrap(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 格式化数字（靠右）
		WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);
		try {
			formatDateRight.setWrap(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// 设定列标题居中
			 formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			// 设置自动换行
			formatHeader.setWrap(true);
			// 设置表头居中
			 formatDetailHead1.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead1.setWrap(true);
			// 设置主数据表头
			 formatName1.setAlignment(jxl.format.Alignment.CENTRE);
			formatName1.setWrap(true);

			formatName.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName.setWrap(true);
			// 设置标题下信息靠左
			formatDetail.setAlignment(jxl.format.Alignment.LEFT);
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
		Label title = new Label(0, 0, "手术部位医院感染率统计表", formatHeader);
		Label Detail = new Label(0, 1, "医院名称：泰达国际心血管病医院",
				formatDetail);

		Label DetailHead1 = new Label(5, 2, "感染手术部位", formatDetailHead1);
		try {
			// 打开文件
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// 生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
			WritableSheet sheet1 = workbook.createSheet("报  表", 0);
			// 将定义好的单元格添加到工作表中
			sheet1.addCell(title);
			sheet1.addCell(Detail);
			sheet1.addCell(DetailHead1);
			// 合并单元格
			sheet1.mergeCells(0, 0, 7, 0);
			sheet1.mergeCells(0, 1, 7, 1);
			sheet1.mergeCells(5, 2, 7, 2);
			sheet1.mergeCells(0, 2, 0, 3);
			sheet1.mergeCells(1, 2, 1, 3);
			sheet1.mergeCells(2, 2, 2, 3);
			sheet1.mergeCells(3, 2, 3, 3);
			sheet1.mergeCells(4, 2, 4, 3);
			sheet1.setRowView(0, 550);
			sheet1.setRowView(1, 450);
			sheet1.setRowView(2, 450);
			sheet1.setColumnView(0, 18);
			sheet1.setColumnView(1, 32);
			sheet1.setColumnView(2, 16);
			sheet1.setColumnView(3, 16);
			sheet1.setColumnView(4, 16);
			sheet1.setColumnView(5, 16);
			sheet1.setColumnView(6, 16);
			sheet1.setColumnView(7, 16);

			// 添加表头
			for (int i = 0; i < colNum ; i++) {
				// 添加表头的一行（标题）
				Label row1 = new Label(i, 3, (String) header.get(i),
						formatName1);
				sheet1.addCell(row1);
			}
			for (int i = 0; i < 5; i++) {
				Label row2 = new Label(i, 2, (String) header.get(i),
						formatName1);
				sheet1.addCell(row2);
			}
			// 添加主数据
			int dateRow = date.getCount();
			
			
			// 得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
			String map = "";
			if (table != null) {
				map = (String) table.getParmMap();
			} else {
				map = this.parmMap;
			}
			String[] title1 = map.split(";");
			
			// 循环添加主数据
			for (int j = 3; j < dateRow+3 ; j++) { // 行
				for (int n = 0; n < colNum ; n++) { // 列
					// 每一个格的数据
					Label dateCell;
					// 得到该列的类型
					// System.out.println("n:::::::"+n);
					String type = (String) dateType.get(n);
					// 如果是数字类型就靠右
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double,0.00")
							|| type.equalsIgnoreCase("long")) {
						// 设置该数据靠右
						dateCell = new Label(n, j + 1, date.getValue(
								title1[n], j-3), formatDateRight);
					} else {
						// 设置该数据靠左
						dateCell = new Label(n, j + 1, date.getValue(
								title1[n], j-3), formatDateLeft);
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

	/**
	 * 写Excel
	 * 
	 * @param title
	 *            String
	 * @param head
	 *            String[]
	 * @param os
	 *            OutputStream
	 * @param parm
	 *            TParm
	 * @throws Exception
	 */
	public void writeExcel(String title, String[] head, OutputStream os,
			TParm parm) throws Exception {
//		System.out.println("666666666666");
		// 列数量
		int columnNum = head.length;
		int rowCount = parm.getInt("ACTION", "COUNT");
		if (columnNum == 0 || rowCount == 0) {
			JOptionPane.showMessageDialog(null, "无导出数据！");
		}
		// 添加标题
		WritableWorkbook titleObj = Workbook.createWorkbook(os);
		WritableSheet sheetName = titleObj.createSheet(title, 0);
		// 列添加
		for (int i = 0; i < columnNum; i++) {
			WritableFont columnName = new WritableFont(
					WritableFont.createFont("楷体 _GB2312"), 14,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);
			WritableCellFormat columnFormat = new WritableCellFormat(columnName);
			jxl.write.Label column = new Label(i, 0, head[i].split(":")[0],
					columnFormat);
			sheetName.addCell(column);
		}
		for (String temp : head) {
			// System.out.println("列名"+temp);
		}
		// 行添加
		for (int j = 0; j < rowCount; j++) {
			// System.out.println("一行数据"+parm.getRow(j));
			for (int i = 0; i < head.length; i++) {
				jxl.write.Label rowData = new jxl.write.Label(i, j + 1, parm
						.getData(head[i].split(":")[1], j).toString());
				sheetName.addCell(rowData);
			}
		}
		try {
			// 写入Exel工作表
			titleObj.write();
			// 关闭Excel工作薄对象
			titleObj.close();
			os.flush();
			os.close();
			JOptionPane.showMessageDialog(null, "存档成功！");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "存档失败！");
		}
	}

	/**
	 * 生成Excel
	 * 
	 * @param title
	 *            String
	 * @param head
	 *            String[]
	 * @param parm
	 *            TParm
	 * @return boolean
	 * @throws IOException
	 */
	public boolean creatExcelFile(String title, String[] head, TParm parm)
			throws IOException, Exception {
//		System.out.println("7777777777777");
		String path = getPath();
		if (path.length() == 0)
			return false;
		File dirSel = new File(path);
		// System.out.println("路径"+path);
		if (!path.contains(".xls"))
			return false;
		if (dirSel.exists()
				&& JOptionPane.showConfirmDialog(null, "该文件已经存在,是否覆盖？", "保存",
						JOptionPane.YES_NO_OPTION) == 1)
			return false;
		dirSel.createNewFile();
		writeExcel(title, head, new FileOutputStream(dirSel), parm);
		return true;
	}

	/**
	 * 选择路径
	 * 
	 * @return String
	 */
	public String getPath() throws IOException {
//		System.out.println("88888888888");
		String path = "";
		String fileName = "";
		// com.javahis.util.JavaHisDebug.initClient();
		JFileChooser chooser = new JFileChooser();
		File dir = new File("C:\\JavaHis\\Excel");
		if (!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		chooser.setSelectedFile(new File("未命名"));
		chooser.setDialogTitle("导出EXCEL界面");
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
			if (!chooser.getSelectedFile().getName().contains(".xls")) {
				fileName = chooser.getSelectedFile().getName() + ".xls";
			} else {
				fileName = chooser.getSelectedFile().getName();
			}
			path = chooser.getSelectedFile().getParent();
			// System.out.println("path:"+path);
			// System.out.println("name:"+fileName);
			// System.out.println("文件路径"+path+"\\"+fileName);
		}
		return path + "\\" + fileName;
	}

	// -----------------------------导出EXCEL的方法---end-----------------------------
}
