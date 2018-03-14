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
 * Title: ������ҩ����������ܱ�
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
		System.out.println("222zhongde " + arrHeader);
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
				WritableFont.createFont("����_GB2312"), 18, WritableFont.BOLD);
		// �����б���
		WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
		// ���ø�ʽ����������Ϣ ����� ��ϵ�绰 ��������ڿ���
		WritableFont fontDetail = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetail = new WritableCellFormat(fontDetail);

		// ���ø�ʽ����ͷ
		WritableFont fontDetailHead1 = new WritableFont(
				WritableFont.createFont("����"), 16, WritableFont.BOLD);
		WritableCellFormat formatDetailHead1 = new WritableCellFormat(
				fontDetailHead1);
		WritableFont fontDetailHead2 = new WritableFont(
				WritableFont.createFont("����"), 16, WritableFont.BOLD);
		WritableCellFormat formatDetailHead2 = new WritableCellFormat(
				fontDetailHead2);

		// ���ø�ʽ�������ݱ�ͷ �������� ��������
		WritableFont fontName1 = new WritableFont(
				WritableFont.createFont("���� _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatName1 = new WritableCellFormat(fontName1);
		WritableFont fontName2 = new WritableFont(
				WritableFont.createFont("���� _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatName2 = new WritableCellFormat(fontName2);
		WritableFont fontName = new WritableFont(WritableFont.createFont("����"),
				10, WritableFont.NO_BOLD);
		WritableCellFormat formatName = new WritableCellFormat(fontName);
		//�����ݱ�ͷ
		WritableFont fontDetailHeader= new WritableFont(
				WritableFont.createFont("���� _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat formatDetailHeader = new WritableCellFormat(fontDetailHeader);

		// ���ø�ʽ��������
		WritableFont fontDate = new WritableFont(
				WritableFont.createFont("���� _GB2312"), 14, WritableFont.NO_BOLD);
		// ��ʽ��һ������ݣ�����
		WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
		try {
			formatDateLeft.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		// ��ʽ�����֣����ң�
		WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);
		try {
			formatDateRight.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}

		try {
			// �趨�б������
			 formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			// �����Զ�����
			formatHeader.setWrap(true);
			// ���ñ�ͷ����
			 formatDetailHead1.setAlignment(jxl.format.Alignment.CENTRE);
//			formatDetailHead1.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetailHead1.setWrap(true);
			 formatDetailHead2.setAlignment(jxl.format.Alignment.CENTRE);
//			formatDetailHead2.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetailHead2.setWrap(true);
			// ���������ݱ�ͷ
			// formatName1.setAlignment(jxl.format.Alignment.CENTRE);
			formatName1.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName1.setWrap(true);
			// formatName2.setAlignment(jxl.format.Alignment.CENTRE);
			formatName2.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName2.setWrap(true);

			// formatName.setAlignment(jxl.format.Alignment.CENTRE);
			formatName.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatName.setWrap(true);
			//�����ݱ�ͷ
			formatDetailHeader.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatDetailHeader.setWrap(true);
			// ���ñ�������Ϣ����
			formatDetail.setVerticalAlignment(VerticalAlignment.CENTRE);
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
		Label title = new Label(0, 0, "�����ҽ�ƻ���������ҩ����������ܱ�", formatHeader);
		Label Detail = new Label(0, 1, "�����:        ��ϵ�绰:       ��������ڿ���:",
				formatDetail);

		Label DetailHead1 = new Label(2, 2, "������ҩ����Ⱦ������", formatDetailHead1);
		Label DetailHead2 = new Label(13, 2, "������ҩ����Ⱦ�����", formatDetailHead2);

		Label Name1 = new Label(0, 2, "ҽ�ƻ�������", formatName1);
		Label Name = new Label(0, 4, "̩�������Ѫ�ܲ�ҽԺ", formatName);
		Label Name2 = new Label(1, 2, "���������������", formatName2);
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
			sheet1.addCell(Name1);
			sheet1.addCell(Name);
			sheet1.addCell(Name2);
			// �ϲ���Ԫ��
			sheet1.mergeCells(0, 0, 12, 0);
			sheet1.mergeCells(0, 1, 12, 1);
			sheet1.mergeCells(2, 2, 12, 2);
			sheet1.mergeCells(13, 2, 27, 2);
			sheet1.mergeCells(0, 2, 0, 3);
			sheet1.mergeCells(1, 2, 1, 3);
			sheet1.setRowView(0, 500);
			sheet1.setRowView(1, 500);
			sheet1.setRowView(2, 500);
//			System.out.println("��ͷ��������������������������" + colNum);
			// �����п�
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// ѭ�������п���table���泤�ȵ�10��֮һ��
				sheet1.setColumnView(n, colViwe / 10);
			}

			// ��ӱ�ͷ
			for (int i = 1; i < colNum + 1; i++) {
				// ��ӱ�ͷ��һ�У����⣩
				Label row1 = new Label(i, 3, (String) header.get(i -1),
						formatDetailHeader);
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
				for (int n = 1; n < colNum + 1; n++) { // ��
					// ÿһ���������
					Label dateCell;
					// �õ����е�����
					// System.out.println("n:::::::"+n);
					String type = (String) dateType.get(n - 1);
					// ������������;Ϳ���
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// ���ø����ݿ���
						dateCell = new Label(n, j +1, date.getValue(
								title1[n - 1], j-3), formatDateRight);
					} else {
						// ���ø����ݿ���
						dateCell = new Label(n, j + 1, date.getValue(
								title1[n - 1], j-3), formatDateLeft);
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

	// -----------------------------����EXCEL�ķ���---end-----------------------------

}
