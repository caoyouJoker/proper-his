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

/**
 * <p>
 * Title:������λ��Ⱦ��������
 * </p>
 * 
 * <p>
 * Description: EXCELģ��
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
public class INFOpeSiteUtil extends TControl {
	private static final INFOpeSiteUtil INSTANCE = new INFOpeSiteUtil();

	private INFOpeSiteUtil() {
	}

	public static INFOpeSiteUtil getInstance() {

		return INSTANCE;
	}

	private TTable table;
	private String parmMap = "";

	/**
	 * ����EXCEL�ķ����Ľӿ�
	 * 
	 * @param table
	 *            TTable table����
	 * @param defName
	 *            String Ĭ���ļ�������
	 */
	public void exportExcel(TTable mainTable, String defName) {
//		System.out.println("111111111111111");
		table = mainTable;
		// �õ�talble��ͷ
		String header = mainTable.getHeader();
		// ֻ�õ�table����ʾ�����ݣ�PS���������ݵò�����
		TParm mainDate = mainTable.getShowParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("�޵���EXCEL���ݣ�");
			return;
		}
		// ���õ���excel����������ͷ�������ݣ��ļ����֣�
		exeSaveExcel(header, mainDate, defName);
	}

	/**
	 * ����excel�ļ�
	 * 
	 * @param headerDate
	 *            String
	 * @param mainDate
	 *            TParm
	 * @param fileDefName
	 *            Ĭ���ļ�����
	 */

	public void exeSaveExcel(String header, TParm mainDate, String fileDefName) {
//		System.out.println("222222222");
		// �����������ͷ���ݣ�ֻ���±��⣩
		Vector arrHeader = arrHeader(header);
//		System.out.println("222zhongde " + arrHeader);
		// ��׼SWING
		JFileChooser chooser = new JFileChooser();
		// ���õ�ǰ��Ŀ¼��Ŀǰд����
		File dir = new File("C:\\JavaHis\\Excel");
		if (!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		// ���õ�ǰʱ�丽�ӵ�Ĭ���ļ�����
		Timestamp optTime = SystemTool.getInstance().getDate();
		// String opttime=StringTool.getString(optTime, "yyyyMMddHHmmss");
		// String a = opttime.substring(4,6);
		// System.out.println("�ַ���ʱ���ȡ:"+a);
		fileDefName = fileDefName
				+ StringTool.getString(optTime, "yyyyMMddHHmmss");
		// Ĭ�ϵ��ļ���������
		chooser.setSelectedFile(new File(fileDefName));// �ṩĬ����
		// ���öԻ���ı���
		chooser.setDialogTitle("����EXCEL����");
		// ���ù��ˣ���չ����
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
	 * ����table�ı���ͷ��ʹֻ֮ʡ�±���
	 * 
	 * @param date
	 *            String ȱ��ʡ״̬�µ�ͷ
	 * @return Vector
	 */
	public Vector arrHeader(String date) {
		// �ѱ������ݰ�������Ϊ�ַ�����
		String[] indate = date.split(";");
		Vector colAndType = getColumnView(indate);
		Vector result = new Vector();
		Vector header = new Vector();
		// ѭ��ȥ��--> �����ȣ����͵�
		for (int j = 0; j < indate.length; j++) {
			// System.out.println("���ŵ�ֵ:::"+indate[j].substring(0, (int)
			// (indate[j].indexOf(","))));
			// ��ȡ����һ��������ǰ������--����
			String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
			header.add(a);
		}
		// ��һ���ǵ�һ�е���Ŀ
		result.add(header);
		// �ڶ������п�
		result.add(colAndType.get(0));
		// ����������������
		result.add(colAndType.get(1));

		// System.out.println("header++++"+header);
		return result;
	}

	public Vector getColumnView(String[] date) {
//		System.out.println("333333333333");
		Vector result = new Vector();
		// �洢�п����������
		Vector colView = new Vector();
		Vector dateType = new Vector();
		// ����
		// System.out.println("date::::"+date);
		int col = date.length;
		for (int i = 0; i < col; i++) {
			// �ӵ�һ�����ź���һλ��ʼ
			// System.out.println("date[]::::"+date[i]);
			int start = date[i].indexOf(",") + 1;
			int end;
			String type;
			// û�еڶ������ŵ�ʱ��˵��û����������
			if (date[i].indexOf(",", start) == -1) {
				// û�еڶ�������ȡ�����--����
				end = date[i].length();
				// û�����;�Ĭ��Ϊ�ַ���
				type = "String";
			} else {
				// �о�ȡ���ڶ���������ǰ--����
				end = date[i].indexOf(",", start);
				// ȡ��ʡ�µ��ַ�--����
				type = date[i].substring(end + 1);
			}
			// �洢���еĳ���
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
	 * �򿪱���Ի���ѡ��·��
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
		// ����һ���ļ�����
		File file;
		try {
			// ��·���а���.xls�Ͳ��Ӻ�׺
			if (!FileName.contains(".xls")) {
				file = new File(FileName + ".xls");
			} else
				file = new File(FileName);
			// �жϸ��ļ��Ƿ���ڣ�����Ѿ�������ʾ�Ƿ񸲸ǣ������Ǿ��˳�������
			if (file.exists()
					&& JOptionPane.showConfirmDialog(null, "save",
							"���ļ��Ѿ�����,�Ƿ񸲸ǣ�", JOptionPane.YES_NO_OPTION) == 1)
				return;
			// ���ļ���������
			FileOutputStream fileOutStream = null;
			fileOutStream = new FileOutputStream(file);
			// ִ��������������
			exportToExcel(header, date, fileOutStream);
			fileOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ת��ʧ��");
		}
	}

	/**
	 * ����excel�������ݺ����ø�ʽ����������
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
		// ȡ����Ҫ������
		Vector header = (Vector) headerDate.get(0);
		Vector colView = (Vector) headerDate.get(1);
		Vector dateType = (Vector) headerDate.get(2);
		// ȡ������
		int colNum = header.size();
		System.out.println("header++++" + header);
		// ���ø�ʽ���б���
		WritableFont fontHeader = new WritableFont(
				WritableFont.createFont("����"), 18, WritableFont.BOLD);
		// �����б���
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
		// ���ø�ʽ����������Ϣ  ҽԺ���ƣ�̩�������Ѫ�ܲ�ҽԺ
		WritableFont fontDetail = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetail = new WritableCellFormat(fontDetail);

		// ���ø�ʽ����ͷ
		WritableFont fontDetailHead1 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead1 = new WritableCellFormat(
				fontDetailHead1);
		WritableFont fontDetailHead2 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead2 = new WritableCellFormat(
				fontDetailHead2);
		WritableFont fontDetailHead3 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead3 = new WritableCellFormat(
				fontDetailHead3);

		// ���ø�ʽ�������ݱ�ͷ �������� ��������
		WritableFont fontName1 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatName1 = new WritableCellFormat(fontName1);
		WritableFont fontName = new WritableFont(WritableFont.createFont("����"),
				14, WritableFont.NO_BOLD);
		WritableCellFormat formatName = new WritableCellFormat(fontName);

		// ���ø�ʽ��������
		WritableFont fontDate = new WritableFont(
				WritableFont.createFont("����"), 10, WritableFont.NO_BOLD);
		// ��ʽ��һ������ݣ�����
		WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
		try {
			formatDateLeft.setWrap(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ��ʽ�����֣����ң�
		WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);
		try {
			formatDateRight.setWrap(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// �趨�б������
			 formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			// �����Զ�����
			formatHeader.setWrap(true);
			// ���ñ�ͷ����
			 formatDetailHead1.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead1.setWrap(true);
			 formatDetailHead2.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead2.setWrap(true);
			formatDetailHead2.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead3.setAlignment(jxl.format.Alignment.CENTRE);
			formatDetailHead3.setWrap(true);
			// ���������ݱ�ͷ
			 formatName1.setAlignment(jxl.format.Alignment.CENTRE);
			formatName1.setWrap(true);
			formatName.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName.setWrap(true);
			// ���ñ�������Ϣ����
			formatDetail.setAlignment(jxl.format.Alignment.LEFT);
			formatDetail.setWrap(true);

			formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
			formatDateRight.setWrap(true);

			formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
			formatDateLeft.setWrap(true);
		} catch (WriteException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ת��ʧ��");
		}
		// ��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0)
		// �Լ���Ԫ������Ϊtest
		Label title = new Label(0, 0, "������λҽԺ��Ⱦ����", formatHeader);
		Label Detail = new Label(0, 1, "ҽԺ���ƣ�̩�������Ѫ�ܲ�ҽԺ",
				formatDetail);

		Label DetailHead1 = new Label(0, 2, "���߻�����Ϣ", formatDetailHead1);
		Label DetailHead2 = new Label(3, 2, "�������", formatDetailHead2);
		Label DetailHead3 = new Label(17, 2, "����Ӧ�ÿ���ҩ��", formatDetailHead3);

		try {
			// ���ļ�
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// ������Ϊ���� ���Ĺ���������0��ʾ���ǵ�һҳ��sheet�����֣�
			WritableSheet sheet1 = workbook.createSheet("��  ��", 0);
			// ������õĵ�Ԫ����ӵ���������
			sheet1.addCell(title);
			sheet1.addCell(Detail);
			sheet1.addCell(DetailHead1);
			sheet1.addCell(DetailHead2);
			sheet1.addCell(DetailHead3);
			// �ϲ���Ԫ��
			sheet1.mergeCells(0, 0, 21, 0);
			sheet1.mergeCells(0, 1, 21, 1);
			sheet1.mergeCells(0, 2, 3, 2);
			sheet1.mergeCells(3, 2, 17, 2);
			sheet1.mergeCells(17, 2, 21, 2);
			sheet1.setRowView(0, 550);
			sheet1.setRowView(1, 450);
			sheet1.setRowView(2, 400);
			sheet1.setColumnView(0, 5);
			sheet1.setColumnView(20, 20);
			sheet1.setColumnView(21, 20);

			// ��ӱ�ͷ
			for (int i = 0; i < colNum; i++) {
				// ��ӱ�ͷ��һ�У����⣩
				Label row1 = new Label(i, 3, (String) header.get(i),
						formatName);
				sheet1.addCell(row1);
			}

			// ���������
			int dateRow = date.getCount();
			
			
			// �õ�Ĭ�����map���ձ�ͨ���������������ݣ���Ϊmap�������ǲ�׼�ģ�
			// String map = (String)table.getParmMap();
			String map = "";
			if (table != null) {
				map = (String) table.getParmMap();
			} else {
				map = this.parmMap;
			}
			String[] title1 = map.split(";");
			
			// ѭ�����������
			for (int j = 3; j < dateRow + 3; j++) { // ��
				for (int n = 0; n < colNum; n++) { // ��
					// ÿһ���������
					Label dateCell;
					// �õ����е�����
					// System.out.println("n:::::::"+n);
					String type = (String) dateType.get(n);
					// ������������;Ϳ���
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// ���ø����ݿ���
						dateCell = new Label(n, j + 1, date.getValue(
								title1[n], j-3), formatDateRight);
					} else {
						// ���ø����ݿ���
						dateCell = new Label(n, j + 1, date.getValue(
								title1[n], j-3), formatDateLeft);
					}
					sheet1.addCell(dateCell);
				}
			}

			workbook.write();
			workbook.close();
			JOptionPane.showMessageDialog(null, "ת���ɹ�");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ת��ʧ��");
		}
	}

	/**
	 * дExcel
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
		// ������
		int columnNum = head.length;
		int rowCount = parm.getInt("ACTION", "COUNT");
		if (columnNum == 0 || rowCount == 0) {
			JOptionPane.showMessageDialog(null, "�޵������ݣ�");
		}
		// ��ӱ���
		WritableWorkbook titleObj = Workbook.createWorkbook(os);
		WritableSheet sheetName = titleObj.createSheet(title, 0);
		// �����
		for (int i = 0; i < columnNum; i++) {
			WritableFont columnName = new WritableFont(
					WritableFont.createFont("���� _GB2312"), 14,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);
			WritableCellFormat columnFormat = new WritableCellFormat(columnName);
			jxl.write.Label column = new Label(i, 0, head[i].split(":")[0],
					columnFormat);
			sheetName.addCell(column);
		}
		for (String temp : head) {
			// System.out.println("����"+temp);
		}
		// �����
		for (int j = 0; j < rowCount; j++) {
			// System.out.println("һ������"+parm.getRow(j));
			for (int i = 0; i < head.length; i++) {
				jxl.write.Label rowData = new jxl.write.Label(i, j + 1, parm
						.getData(head[i].split(":")[1], j).toString());
				sheetName.addCell(rowData);
			}
		}
		try {
			// д��Exel������
			titleObj.write();
			// �ر�Excel����������
			titleObj.close();
			os.flush();
			os.close();
			JOptionPane.showMessageDialog(null, "�浵�ɹ���");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "�浵ʧ�ܣ�");
		}
	}

	/**
	 * ����Excel
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
		// System.out.println("·��"+path);
		if (!path.contains(".xls"))
			return false;
		if (dirSel.exists()
				&& JOptionPane.showConfirmDialog(null, "���ļ��Ѿ�����,�Ƿ񸲸ǣ�", "����",
						JOptionPane.YES_NO_OPTION) == 1)
			return false;
		dirSel.createNewFile();
		writeExcel(title, head, new FileOutputStream(dirSel), parm);
		return true;
	}

	/**
	 * ѡ��·��
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
		chooser.setSelectedFile(new File("δ����"));
		chooser.setDialogTitle("����EXCEL����");
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
			// System.out.println("�ļ�·��"+path+"\\"+fileName);
		}
		return path + "\\" + fileName;
	}

	// -----------------------------����EXCEL�ķ���---end-----------------------------
}
