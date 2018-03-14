package com.javahis.util;

import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import javax.swing.JOptionPane;
import java.io.*;
import jxl.Workbook;
import jxl.write.*;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.control.TControl;
import jxl.format.UnderlineStyle;

import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ExportExcelUtil extends TControl{
	private static final ExportExcelUtil INSTANCE =new ExportExcelUtil();

	private ExportExcelUtil() {
	}

	public static ExportExcelUtil getInstance(){

		return INSTANCE;
	}

	private TTable table;
	private String parmMap = "";
	/**
	 * 导出EXCEL的方法的接口
	 * @param table TTable table对象
	 * @param defName String 默认文件的名字
	 */
	public void exportExcel(TTable mainTable, String defName) {
		table=mainTable;
		//得到talble的头
		String header = mainTable.getHeader();
		//只得到table上显示的数据（PS：隐藏数据得不到）
		TParm mainDate = mainTable.getShowParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("无导出EXCEL数据！");
			return;
		}
		//调用导出excel方法（标题头，主数据，文件名字）
		exeSaveExcel(header, mainDate, defName);

	}
	/**
	 * 导出EXCEL的方法的接口
	 * @param table TTable table对象
	 * @param defName String 默认文件的名字
	 */
	public void exportNewExcel(TTable mainTable, String defName) {
		table=mainTable;
		//得到talble的头
		String header = mainTable.getHeader();
		//只得到table上显示的数据（PS：隐藏数据得不到）
		TParm mainDate = mainTable.getParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("无导出EXCEL数据！");
			return;
		}
		//调用导出excel方法（标题头，主数据，文件名字）
		exeSaveExcel(header, mainDate, defName);

	}
	/**
	 * 导出excel文件
	 * @param headerDate String
	 * @param mainDate TParm
	 * @param fileDefName 默认文件名字
	 */

	public void exeSaveExcel(String headerDate, TParm mainDate,String fileDefName) {
		//重新整理标题头数据（只留下标题）
		Vector arrHeader = arrHeader(headerDate);
		//标准SWING
		JFileChooser chooser = new JFileChooser();
		//设置当前的目录（目前写死）
		File dir= new File("C:\\JavaHis\\Excel");
		if(!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		//利用当前时间附加到默认文件名上
		Timestamp optTime = SystemTool.getInstance().getDate();
		fileDefName=fileDefName+StringTool.getString(optTime,"yyyyMMddHHmmss");
		//默认的文件保存名字
		chooser.setSelectedFile(new File(fileDefName));//提供默认名
		//设置对话框的标题
		chooser.setDialogTitle("导出EXCEL界面");
		//设置过滤（扩展名）
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
	 * 导出EXCEL的方法的接口（文件名后不带时间）
	 * @param table TTable table对象
	 * @param defName String 默认文件的名字
	 */
	public void exportExcelWithoutTime(TTable mainTable, String defName) {// add by wanglong 20130419
		table = mainTable;
		// 得到talble的头
		String header = mainTable.getHeader();
		//只得到table上显示的数据（PS：隐藏数据得不到）
		TParm mainDate = mainTable.getShowParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("无导出EXCEL数据！");
			return;
		}
		//调用导出excel方法（标题头，主数据，文件名字）
		exeSaveExcel(header, mainDate, defName, false);
	}

	/**
	 * 导出excel文件(可以选择文件名是否带时间)
	 * @param headerDate String
	 * @param mainDate TParm
	 * @param fileDefName 默认文件名字
	 */

	public void exeSaveExcel(String headerDate, TParm mainDate, String fileDefName, boolean withTime) {// add by wanglong 20130419
		// 重新整理标题头数据（只留下标题）
		Vector arrHeader = arrHeader(headerDate);
		// 标准SWING
		JFileChooser chooser = new JFileChooser();
		//设置当前的目录（目前写死）
		File dir= new File("C:\\JavaHis\\Excel");
		if(!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		//利用当前时间附加到默认文件名上
		Timestamp optTime = SystemTool.getInstance().getDate();
		if (withTime) {
			fileDefName = fileDefName + StringTool.getString(optTime, "yyyyMMddHHmmss");
		}
		//默认的文件保存名字
		chooser.setSelectedFile(new File(fileDefName));//提供默认名
		//设置对话框的标题
		chooser.setDialogTitle("导出EXCEL界面");
		//设置过滤（扩展名）
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
	 * 导出EXCEL的方法的接口
	 * @param table TTable table对象
	 * @param defName String 默认文件的名字
	 */
	public void exportExcel(String header,String parmMap,TParm parm, String defName) {
		//table=mainTable;
		this.parmMap = parmMap;
		//得到talble的头
		/*String header = mainTable.getHeader();
        if (parm.getCount() <= 0) {
            this.messageBox("无导出EXCEL数据！");
            return;
        }*/
		//调用导出excel方法（标题头，主数据，文件名字）
		exeSaveExcel(header, parm, defName);

	}
	/**
	 * 导出excel文件
	 * @param headerDate String
	 * @param mainDate TParm
	 * @param fileDefName 默认文件名字
	 */

	public void exeSaveExcel(TParm[] mainDate,String fileDefName) {
		//重新整理标题头数据（只留下标题）
		int mainDCount =  mainDate.length;
		for(int i=0;i<mainDCount;i++){
			//            System.out.println("重新整理标题头数据==="+mainDate[i].getValue("HEAD"));
			//            System.out.println("数据==="+mainDate[i]);
			mainDate[i].setData("HEAD",arrHeader(mainDate[i].getValue("HEAD")));
		}
		//标准SWING
		JFileChooser chooser = new JFileChooser();
		//设置当前的目录（目前写死）
		File dir= new File("C:\\JavaHis\\Excel");
		if(!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		//利用当前时间附加到默认文件名上
		Timestamp optTime = SystemTool.getInstance().getDate();
		fileDefName=fileDefName+StringTool.getString(optTime,"yyyyMMddHHmmss");
		//默认的文件保存名字
		chooser.setSelectedFile(new File(fileDefName));//提供默认名
		//设置对话框的标题
		chooser.setDialogTitle("导出EXCEL界面");
		//设置过滤（扩展名）
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
			this.writeFile(mainDate, path);
		}
	}
	/**
	 * 打开保存对话框选择路径
	 * @param header Vector
	 * @param date TParm
	 * @param FileName String
	 */
	public void writeFile(Vector header, TParm date, String FileName) {
		//创建一个文件对象
		File file;
		try {
			//过路径中包含.xls就不加后缀
			if (!FileName.contains(".xls")) {
				file = new File(FileName + ".xls");
			}
			else
				file = new File(FileName);
			//判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
			if (file.exists() &&
					JOptionPane.showConfirmDialog(null, "save", "该文件已经存在,是否覆盖？",
							JOptionPane.YES_NO_OPTION) == 1)
				return;
			//把文件变成输出流
			FileOutputStream fileOutStream = null;
			fileOutStream = new FileOutputStream(file);
			//执行数据整理并导出
			exportToExcel(header, date, fileOutStream);
			fileOutStream.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}
	/**
	 * 打开保存对话框选择路径
	 * @param header Vector
	 * @param date TParm
	 * @param FileName String
	 */
	public void writeFile(TParm[] date, String FileName) {
		//创建一个文件对象
		File file;
		try {
			//过路径中包含.xls就不加后缀
			if (!FileName.contains(".xls")) {
				file = new File(FileName + ".xls");
			}
			else
				file = new File(FileName);
			//判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
			if (file.exists() &&
					JOptionPane.showConfirmDialog(null, "save", "该文件已经存在,是否覆盖？",
							JOptionPane.YES_NO_OPTION) == 1)
				return;
			//把文件变成输出流
			FileOutputStream fileOutStream = null;
			fileOutStream = new FileOutputStream(file);
			//执行数据整理并导出
			exportToExcel(date, fileOutStream);
			fileOutStream.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}

	/**
	 * 导出excel整理数据和设置格式（主方法）
	 * @param headerDate Vector
	 * @param date TParm
	 * @param os OutputStream
	 */

	public void exportToExcel(TParm[] date, OutputStream os) {

		int headCount = date.length;
		int err=0;
		//打开文件
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(os);
		}
		catch (IOException ex1) {
			ex1.fillInStackTrace();
		}
		for (int w = 0; w < headCount; w++) {
			Vector temp = date[w].getVector("HEAD");
			//取得需要的数据
			Vector header = (Vector)((Vector) temp.get(0)).get(0);
			Vector colView = (Vector)((Vector) temp.get(1)).get(0);
			Vector dateType = (Vector)((Vector) temp.get(2)).get(0);
			//取得列数
			int colNum = header.size();
			//设置格式化列标题
			WritableFont fontHeader = new WritableFont(WritableFont.createFont(
					"楷体 _GB2312"), 12, WritableFont.BOLD);
			WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
			//设置格式化主数据
			WritableFont fontDate = new WritableFont(WritableFont.createFont(
					"楷体 _GB2312"), 10, WritableFont.NO_BOLD);
			//格式化一般的数据（靠左）
			WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
			//格式化数字（靠右）
			WritableCellFormat formatDateRight = new WritableCellFormat(
					fontDate);

			try {
				//设定列标题居中
				formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
				//设置自动换行
				formatHeader.setWrap(true);

				formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
				formatDateRight.setWrap(true);

				formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
				formatDateLeft.setWrap(true);
			}
			catch (WriteException ex) {
				ex.printStackTrace();
				err = -1;
			}
			try {

				//生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
				WritableSheet sheet1 = workbook.createSheet(date[w].getValue("TITLE"), w);
				//                System.out.println("www========="+w+"===="+date[w].getValue("TITLE"));
				//设置列宽
				for (int n = 0; n < colNum; n++) {
					int colViwe = Integer.parseInt( (String) colView.get(n));
					//循环设置列宽（是table上面长度的10分之一）
					sheet1.setColumnView(n, colViwe / 10);
				}

				//添加表头
				for (int i = 0; i < colNum; i++) {
					//添加表头的一行（标题）
					Label row1 = new Label(i, 0, (String) header.get(i),
							formatHeader);
					sheet1.addCell(row1);
				}

				//添加主数据
				int dateRow = date[w].getCount();
				//得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
				//                String[] title = date[w].getNames();
				//                date[w].getData("SYSTEM","COLUMNS");
				Vector columns = (Vector)date[w].getData("SYSTEM","COLUMNS");
				//循环添加主数据
				for (int j = 0; j < dateRow; j++) { //行
					for (int n = 0; n < colNum; n++) { //列
						//每一个格的数据
						Label dateCell;
						//得到该列的类型
						String type = (String) dateType.get(n);
						//如果是数字类型就靠右
						if (type.equalsIgnoreCase("int") ||
								type.equalsIgnoreCase("float") ||
								type.equalsIgnoreCase("double") ||
								type.equalsIgnoreCase("long")) {
							//设置该数据靠右
							dateCell = new Label(n, j + 1,
									date[w].getValue(columns.get(n).toString(), j),
									formatDateRight);
						}
						else {
							//设置该数据靠左
							//                            System.out.println("Excel导出参数"+date[w]);
							//                            System.out.println("列名=="+columns.get(n).toString());
							//                            System.out.println(""+(j + 1));
							dateCell = new Label(n, j + 1,
									date[w].getValue(columns.get(n).toString(), j),
									formatDateLeft);
						}
						sheet1.addCell(dateCell);
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				err = -1;
			}
		}
		try {
			workbook.write();
		}
		catch (IOException ex2) {
		}
		try {
			workbook.close();
		}
		catch (WriteException ex3) {
		}
		catch (IOException ex3) {
		}
		if(err!=0)
			JOptionPane.showMessageDialog(null, "转档失败");
		else
			JOptionPane.showMessageDialog(null, "转档成功");
	}
	/**
	 * 写Excel
	 * @param title String
	 * @param head String[]
	 * @param os OutputStream
	 * @param parm TParm
	 * @throws Exception
	 */
	public  void writeExcel(String title,String[] head, OutputStream os,TParm parm) throws Exception {
		//列数量
		int columnNum = head.length;
		int rowCount = parm.getInt("ACTION","COUNT");
		if(columnNum==0||rowCount==0){
			JOptionPane.showMessageDialog(null, "无导出数据！");
		}
		//添加标题
		WritableWorkbook titleObj = Workbook.createWorkbook(os);
		WritableSheet sheetName = titleObj.createSheet(title,0);
		//列添加
		for(int i=0;i<columnNum;i++){
			WritableFont columnName = new WritableFont(WritableFont.createFont(
					"宋体"),10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
			WritableCellFormat columnFormat = new WritableCellFormat(columnName);
			jxl.write.Label column = new Label(i,0,head[i].split(":")[0],columnFormat);
			sheetName.addCell(column);
		}
		for(String temp:head){
			//        System.out.println("列名"+temp);
		}
		//行添加
		for (int j = 0; j < rowCount; j++) {
			//        System.out.println("一行数据"+parm.getRow(j));
			for(int i=0;i<head.length;i++){
				jxl.write.Label rowData = new jxl.write.Label(i, j+1, parm.getData(head[i].split(":")[1],j).toString());
				sheetName.addCell(rowData);
			}
		}
		try{
			//写入Exel工作表
			titleObj.write();
			//关闭Excel工作薄对象
			titleObj.close();
			os.flush();
			os.close();
			JOptionPane.showMessageDialog(null, "存档成功！");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "存档失败！");
		}
	}
	/**
	 * 生成Excel
	 * @param title String
	 * @param head String[]
	 * @param parm TParm
	 * @return boolean
	 * @throws IOException
	 */
	public  boolean creatExcelFile(String title,String[] head,TParm parm)throws IOException,Exception{
		String path = getPath();
		if(path.length()==0)
			return false;
		File dirSel = new File(path);
		//        System.out.println("路径"+path);
		if(!path.contains(".xls"))
			return false;
		if (dirSel.exists() &&JOptionPane.showConfirmDialog(null, "该文件已经存在,是否覆盖？", "保存",JOptionPane.YES_NO_OPTION) == 1)
			return false;
		dirSel.createNewFile();
		writeExcel(title,head,new FileOutputStream(dirSel),parm);
		return true;
	}
	/**
	 * 选择路径
	 * @return String
	 */
	public  String getPath() throws IOException{
		String path = "";
		String fileName="";
		//        com.javahis.util.JavaHisDebug.initClient();
		JFileChooser chooser = new JFileChooser();
		File dir= new File("C:\\JavaHis\\Excel");
		if(!dir.exists())
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
			if(!chooser.getSelectedFile().getName().contains(".xls")){
				fileName = chooser.getSelectedFile().getName()+".xls";
			}else{
				fileName = chooser.getSelectedFile().getName();
			}
			path = chooser.getSelectedFile().getParent();
			//            System.out.println("path:"+path);
			//            System.out.println("name:"+fileName);
			//            System.out.println("文件路径"+path+"\\"+fileName);
		}
		return path+"\\"+fileName;
	}
	/**
	 * 导出excel整理数据和设置格式（主方法）
	 * @param headerDate Vector
	 * @param date TParm
	 * @param os OutputStream
	 */

	public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {

		//取得需要的数据
		Vector header = (Vector) headerDate.get(0);
		Vector colView = (Vector) headerDate.get(1);
		Vector dateType = (Vector) headerDate.get(2);
		//取得列数
		int colNum = header.size();

		//设置格式化列标题
		WritableFont fontHeader = new WritableFont(WritableFont.createFont(
				"楷体 _GB2312"), 12, WritableFont.BOLD);
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
		//设置格式化主数据
		WritableFont fontDate = new WritableFont(WritableFont.createFont(
				"楷体 _GB2312"), 10, WritableFont.NO_BOLD);
		//格式化一般的数据（靠左）
		WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
		//格式化数字（靠右）
		WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);

		try {
			//设定列标题居中
			formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			//设置自动换行
			formatHeader.setWrap(true);

			formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
			formatDateRight.setWrap(true);

			formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
			formatDateLeft.setWrap(true);
		}
		catch (WriteException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
		try {
			//打开文件
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			//生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
			WritableSheet sheet1 = workbook.createSheet("报  表", 0);
			//设置列宽
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt( (String) colView.get(n));
				//循环设置列宽（是table上面长度的10分之一）
				sheet1.setColumnView(n, colViwe / 10);
			}

			//添加表头
			for (int i = 0; i < colNum; i++) {
				//添加表头的一行（标题）
				Label row1 = new Label(i, 0, (String) header.get(i),
						formatHeader);
				sheet1.addCell(row1);
			}

			//添加主数据
			int dateRow = date.getCount();
			//得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
			//String map = (String)table.getParmMap();
			String map = "";
			if(table != null){
				map = (String)table.getParmMap();
			}else{
				map = this.parmMap;
			}
			String[] title = map.split(";");
			//循环添加主数据
			for (int j = 0; j < dateRow; j++) { //行
				for (int n = 0; n < colNum; n++) { //列
					//每一个格的数据
					Label dateCell;
					//得到该列的类型
					String type = (String) dateType.get(n);
					//如果是数字类型就靠右
					if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float") ||
							type.equalsIgnoreCase("double") || type.equalsIgnoreCase("long")) {
						//设置该数据靠右
						dateCell = new Label(n, j + 1,
								date.getValue(title[n], j),
								formatDateRight);
					}
					else{
						//设置该数据靠左
						dateCell = new Label(n, j + 1,
								date.getValue(title[n], j),
								formatDateLeft);
					}
					sheet1.addCell(dateCell);
				}
			}

			workbook.write();
			workbook.close();
			JOptionPane.showMessageDialog(null, "转档成功");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}

	/**
	 * 整理table的标题头，使之只省下标题
	 * @param date String 缺身省状态下的头
	 * @return Vector
	 */
	public Vector arrHeader(String date) {

		//把标题数据按；区间为字符数组
		String[] indate = date.split(";");
		Vector colAndType = getColumnView(indate);

		Vector result = new Vector();
		Vector header = new Vector();
		//循环去掉--> ，长度，类型等
		for (int j = 0; j < indate.length; j++) {
			//截取‘第一个’逗号前的文字--标题
			String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
			header.add(a);
		}
		//第一组是第一行的题目
		result.add(header);
		//第二组是列宽
		result.add(colAndType.get(0));
		//第三行是数据类型
		result.add(colAndType.get(1));

		return result;
	}

	public Vector getColumnView(String[] date) {

		Vector result = new Vector();
		//存储列宽和数据类型
		Vector colView = new Vector();
		Vector dateType = new Vector();
		//行数
		int col = date.length;
		for (int i = 0; i < col; i++) {
			//从第一个逗号后面一位开始
			int start = date[i].indexOf(",") + 1;
			int end;
			String type;
			//没有第二个逗号的时候说明没有数据类型
			if (date[i].indexOf(",", start) == -1) {
				//没有第二个逗号取到最后--长度
				end = date[i].length();
				//没有类型就默认为字符串
				type = "String";
			}
			else {
				//有就取到第二个逗号以前--长度
				end = date[i].indexOf(",", start);
				//取得省下的字符--类型
				type = date[i].substring(end + 1);
			}
			//存储该列的长度
			String view = date[i].substring(start, end);
			colView.add(view);
			dateType.add(type);

		}
		result.add(colView);
		result.add(dateType);
		return result;
	}


	//-----------------------------导出EXCEL的方法---end-----------------------------


	/**
	 * 导出血液库存统计表
	 * @param mainTable
	 * @param defName
	 * @author wangqing 20180117
	 */
	public void exportExcelForBmsBloodStatistics(TTable mainTable, String defName) {
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
		exeSaveExcelForBmsBloodStatistics(header, mainDate, defName);
	}

	public void exeSaveExcelForBmsBloodStatistics(String header, TParm mainDate, String fileDefName) {
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
			this.writeFileForBmsBloodStatistics(arrHeader, mainDate, path);
		}
	}

	public void writeFileForBmsBloodStatistics(Vector header, TParm date, String FileName) {
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
			exportToExcelForBmsBloodStatistics(header, date, fileOutStream);
			fileOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}


	public void exportToExcelForBmsBloodStatistics(Vector headerDate, TParm date, OutputStream os) {
		System.out.println("55555555555");
		// 取得需要的数据
		Vector header = (Vector) headerDate.get(0);
		Vector colView = (Vector) headerDate.get(1);
		Vector dateType = (Vector) headerDate.get(2);
		// 取得列数
		int colNum = header.size();
		System.out.println("header++++" + header);

		// 列标题格式：宋体、20、水平居中对齐、垂直居中对齐、自动换行
		WritableFont fontTitle = new WritableFont(
				WritableFont.createFont("宋体"), 20, WritableFont.BOLD);
		WritableCellFormat formatTitle = new WritableCellFormat(fontTitle);		
		try{
			formatTitle.setAlignment(jxl.format.Alignment.CENTRE);
			formatTitle.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); 
			formatTitle.setWrap(true);

			formatTitle.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.WHITE);


		}catch(Exception e){
			e.printStackTrace();
		}


		// 列标题格式：宋体、12、居中对齐、自动换行
		WritableFont fontHeader = new WritableFont(
				WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);		
		try{
			formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			formatHeader.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); 
			formatHeader.setWrap(true);

			formatHeader.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);


		}catch(Exception e){
			e.printStackTrace();
		}

		// 主体数据_右对齐：宋体、12、右对齐、自动换行
		WritableFont fontDetailRight = new WritableFont(
				WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat formatDetailRight = new WritableCellFormat(fontDetailRight);
		try{
			formatDetailRight.setAlignment(jxl.format.Alignment.RIGHT);
			formatDetailRight.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); 
			formatDetailRight.setWrap(true);

			formatDetailRight.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

		}catch(Exception e){
			e.printStackTrace();
		}

		// 主体数据_右对齐：宋体、12、右对齐、自动换行
		WritableFont fontDetailLeft = new WritableFont(
				WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat formatDetailLeft = new WritableCellFormat(fontDetailLeft);
		try{
			formatDetailLeft.setAlignment(jxl.format.Alignment.LEFT);
			formatDetailLeft.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); 
			formatDetailLeft.setWrap(true);

			formatDetailLeft.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

		}catch(Exception e){
			e.printStackTrace();
		}

		// 设置Lable
		Label Title = new Label(0, 0, "血液库存统计表", formatTitle);//

		Label BLDCODE_DESC = new Label(0, 1, "血品名称", formatHeader);// 列, 行, 内容, 单元格格式
		Label UNIT_CHN_DESC = new Label(1, 1, "单位", formatHeader);

		Label A = new Label(2, 1, "A", formatHeader);
		Label B = new Label(4, 1, "B", formatHeader);		
		Label O = new Label(6, 1, "O", formatHeader);
		Label AB = new Label(8, 1, "AB", formatHeader);

		Label null1 = new Label(10, 1, "", formatHeader);
		Label null2 = new Label(11, 1, "", formatHeader);

		Label A_POSITIVE = new Label(2, 2, "Rh阳性", formatHeader);
		Label A_NEGATIVE = new Label(3, 2, "Rh阴性", formatHeader);

		Label B_POSITIVE = new Label(4, 2, "Rh阳性", formatHeader);
		Label B_NEGATIVE = new Label(5, 2, "Rh阴性", formatHeader);

		Label O_POSITIVE = new Label(6, 2, "Rh阳性", formatHeader);
		Label O_NEGATIVE = new Label(7, 2, "Rh阴性", formatHeader);

		Label AB_POSITIVE = new Label(8, 2, "Rh阳性", formatHeader);
		Label AB_NEGATIVE = new Label(9, 2, "Rh阴性", formatHeader);

		Label AB_TOTAL = new Label(10, 2, "合计", formatHeader);
		Label AB_NEAR_TERM_EFFECT = new Label(11, 2, "近效期/量", formatHeader);

		try {
			// 打开文件
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// 生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
			WritableSheet sheet1 = workbook.createSheet("血液库存统计表", 0);

			// 显示表格线
			sheet1.getSettings().setShowGridLines(true);  

			// 将定义好的单元格添加到工作表中
			sheet1.addCell(Title);

			sheet1.addCell(BLDCODE_DESC);
			sheet1.addCell(UNIT_CHN_DESC);

			sheet1.addCell(A);
			sheet1.addCell(B);
			sheet1.addCell(O);
			sheet1.addCell(AB);

			sheet1.addCell(null1);
			sheet1.addCell(null2);

			sheet1.addCell(A_POSITIVE);
			sheet1.addCell(A_NEGATIVE);
			sheet1.addCell(B_POSITIVE);
			sheet1.addCell(B_NEGATIVE);			
			sheet1.addCell(O_POSITIVE);
			sheet1.addCell(O_NEGATIVE);
			sheet1.addCell(AB_POSITIVE);
			sheet1.addCell(AB_NEGATIVE);

			sheet1.addCell(AB_TOTAL);
			sheet1.addCell(AB_NEAR_TERM_EFFECT);

			// 合并单元格
			sheet1.mergeCells(0, 0, 11, 0);

			sheet1.mergeCells(0, 1, 0, 2);
			sheet1.mergeCells(1, 1, 1, 2);

			sheet1.mergeCells(2, 1, 3, 1);
			sheet1.mergeCells(4, 1, 5, 1);
			sheet1.mergeCells(6, 1, 7, 1);
			sheet1.mergeCells(8, 1, 9, 1);

			// 设置行宽
			sheet1.setRowView(0, 600);
			sheet1.setRowView(1, 600);
			sheet1.setRowView(2, 600);

			// 设置列宽

			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// 循环设置列宽（是table上面长度的10分之一）
				sheet1.setColumnView(n, colViwe / 10);
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
					// 每一个格的数据
					Label dateCell;
					// 得到该列的类型
					String type = (String) dateType.get(n);
					// 如果是数字类型就靠右
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// 设置该数据靠右
						dateCell = new Label(n, j + 3, date.getValue(
								title1[n], j), formatDetailRight);
					} else {
						// 设置该数据靠左
						dateCell = new Label(n, j + 3, date.getValue(
								title1[n], j), formatDetailLeft);
					}
					sheet1.addCell(dateCell);
				}
				//  设置行宽
				sheet1.setRowView(j + 3, 600);

			}

			workbook.write();
			workbook.close();
			JOptionPane.showMessageDialog(null, "转档成功");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}




}
