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
 * Title: ��Ⱦ������ϸ����
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
	 * ����EXCEL�ķ����Ľӿ�
	 * 
	 * @param table
	 *            TTable table����
	 * @param defName
	 *            String Ĭ���ļ�������
	 */
	public void exportExcel(TTable mainTable, String defName) {
		System.out.println("111111111111111");
		table = mainTable;
		// �õ�table��ͷ
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
		System.out.println("222222222");
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
		System.out.println("333333333333");
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
		System.out.println("4444444444444");
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
		System.out.println("55555555555");
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
		// ���ø�ʽ����������Ϣ ҽԺȫ�� ����� ��ϵ�绰 
		WritableFont fontDetail = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetail = new WritableCellFormat(fontDetail);

		// ���ø�ʽ����ͷ 
		//���߻�����Ϣ
		WritableFont fontDetailHead1 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead1 = new WritableCellFormat(
				fontDetailHead1);
		//ҽԺ��Ⱦ��λ1
		WritableFont fontDetailHead2 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead2 = new WritableCellFormat(
				fontDetailHead2);
		//��Ⱦ���
		WritableFont fontDetailHead21 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead21 = new WritableCellFormat(
				fontDetailHead21);
		//�ͼ����
		WritableFont fontDetailHead22 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead22 = new WritableCellFormat(
				fontDetailHead22);

		//ҽԺ��Ⱦ��λ2
		WritableFont fontDetailHead3 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead3 = new WritableCellFormat(
				fontDetailHead3);
		//��Ⱦ���
		WritableFont fontDetailHead31 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead31 = new WritableCellFormat(
				fontDetailHead31);
		//�ͼ����
		WritableFont fontDetailHead32 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead32 = new WritableCellFormat(
				fontDetailHead32);
		
		//ҽԺ��Ⱦ��λ3
		WritableFont fontDetailHead4 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead4 = new WritableCellFormat(
				fontDetailHead4);
		//��Ⱦ���
		WritableFont fontDetailHead41 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead41 = new WritableCellFormat(
				fontDetailHead41);
		//�ͼ����
		WritableFont fontDetailHead42 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead42 = new WritableCellFormat(
				fontDetailHead42);
		
		//ҽԺ��Ⱦ��λ4
		WritableFont fontDetailHead5 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead5 = new WritableCellFormat(
				fontDetailHead5);
		//��Ⱦ���
		WritableFont fontDetailHead51 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead51 = new WritableCellFormat(
				fontDetailHead51);
		//�ͼ����
		WritableFont fontDetailHead52 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead52 = new WritableCellFormat(
				fontDetailHead52);
		
		//������ת��
		WritableFont fontDetailHead6 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead6 = new WritableCellFormat(
				fontDetailHead6);
		
		//�׸�����
		WritableFont fontDetailHead7 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead7 = new WritableCellFormat(
				fontDetailHead7);
		//�����Բ���
		WritableFont fontDetailHead71 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead71 = new WritableCellFormat(
				fontDetailHead71);
		//�����׸�����
		WritableFont fontDetailHead72 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead72 = new WritableCellFormat(
				fontDetailHead72);
		//��ICU���ƹ�
		WritableFont fontDetailHead73 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead73 = new WritableCellFormat(
				fontDetailHead73);
		//��Ⱦ�������������
		WritableFont fontDetailHead8 = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailHead8 = new WritableCellFormat(
				fontDetailHead8);
		//�����ݱ�ͷ����
		WritableFont fontDetailName = new WritableFont(
				WritableFont.createFont("����"), 14, WritableFont.BOLD);
		WritableCellFormat formatDetailName = new WritableCellFormat(
				fontDetailName);
		
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
			//formatHeader.setVerticalAlignment(VerticalAlignment.CENTRE);
			formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
			// �����Զ�����
			formatHeader.setWrap(true);
			// ���ñ�ͷ����
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
			
			//�����ݱ�ͷ
//			formatDetailName.setAlignment(Alignment.CENTRE);
			formatDetailName.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDetailName.setWrap(true);

			// ���ñ�������Ϣ����
			formatDetail.setAlignment(jxl.format.Alignment.LEFT);
			formatDetail.setWrap(true);
			formatDateRight.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDateRight.setWrap(true);
			formatDateLeft.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatDateLeft.setWrap(true);
		} catch (WriteException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ת��ʧ��");
		}
		// ��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0)
		// �Լ���Ԫ������Ϊtest
		Label title = new Label(0, 0, "ҽԺ��Ⱦ��������", formatHeader);
		Label Detail = new Label(0, 1, "ҽԺȫ��:̩�������Ѫ�ܲ�ҽԺ     �����:           �������ϵ�绰:",
				formatDetail);

		Label DetailHead1 = new Label(0, 2, "���߻�����Ϣ", formatDetailHead1);
		Label DetailHead2 = new Label(11, 2, "ҽԺ��Ⱦ��λ1", formatDetailHead2);
		Label DetailHead21 = new Label(11, 3, "��Ⱦ���", formatDetailHead21);
		Label DetailHead22 = new Label(14, 3, "�ͼ����", formatDetailHead22);
		Label DetailHead3 = new Label(19, 2, "ҽԺ��Ⱦ��λ2", formatDetailHead3);
		Label DetailHead31 = new Label(19, 3, "��Ⱦ���", formatDetailHead31);
		Label DetailHead32 = new Label(22, 3, "�ͼ����", formatDetailHead32);
		Label DetailHead4 = new Label(27, 2, "ҽԺ��Ⱦ��λ3", formatDetailHead4);
		Label DetailHead41 = new Label(27, 3, "��Ⱦ���", formatDetailHead41);
		Label DetailHead42 = new Label(30, 3, "�ͼ����", formatDetailHead42);
		Label DetailHead5 = new Label(35, 2, "ҽԺ��Ⱦ��λ4", formatDetailHead5);
		Label DetailHead51 = new Label(35, 3, "��Ⱦ���", formatDetailHead51);
		Label DetailHead52 = new Label(38, 3, "�ͼ����", formatDetailHead52);
		Label DetailHead6 = new Label(43, 2, "������ת��", formatDetailHead6);
		Label DetailHead7 = new Label(45, 2, "�׸�����", formatDetailHead7);
		Label DetailHead71 = new Label(45, 3, "�����Բ���", formatDetailHead71);
		Label DetailHead72 = new Label(51, 3, "�����׸�����", formatDetailHead72);
		Label DetailHead73 = new Label(74, 4, "�û����Ƿ����ICU", formatDetailHead73);
		Label DetailHead8 = new Label(75, 2, "��Ⱦ�������������", formatDetailHead8);

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
			// �ϲ���Ԫ��
		
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
			//���õ�Ԫ���С
			sheet1.setRowView(0, 500);
			sheet1.setRowView(1, 500);
			sheet1.setRowView(2, 500);
			sheet1.setRowView(3, 500);
//			System.out.println("��ͷ��������������������������" + colNum);
			// �����п�
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// ѭ�������п���table���泤�ȵ�10��֮һ��
				sheet1.setColumnView(n, colViwe / 10);
			}

			// ��ӱ�ͷ
			for (int i = 0; i < colNum ; i++) {
				// ��ӱ�ͷ��һ�У����⣩
				Label row1 = new Label(i, 4, (String) header.get(i),
						formatDetailName);
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
			for (int j = 0; j < dateRow ; j++) { // ��
				for (int n = 0; n < colNum ; n++) { // ��
					System.out.println("�У�������"+dateRow);System.out.println("�У�������"+colNum);
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
						dateCell = new Label(n, j + 5, date.getValue(
								title1[n], j), formatDateRight);
					} else {
						// ���ø����ݿ���
						dateCell = new Label(n, j + 5, date.getValue(
								title1[n], j), formatDateLeft);
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
