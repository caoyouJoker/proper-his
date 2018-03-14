package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.emr.EMRCdrTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;



/**
 * <p>
 * Title: ����\סԺ��ʿ\ҽ��վ ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2018��1��
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * @version JavaHis 5.0
 */



public class SYSOpeQiTaPDFControl extends TControl{
	private String caseNo="";
	private String printType="";
	TTable table ;
	private String tempPath;// add by huangjw
	private String serverPath;// add by huangjw
	String opBookNo = "";
	private String mrNo = "";
	private boolean ascending = false;
	private int sortColumn = -1;
	private BILComparator compare = new BILComparator();
	/**������鱨��*/
	String type="";
	/**�ļ���*/
	String pdfFileName="";
	@Override
	public void onInit() {
		super.onInit();
		tempPath = "C:\\JavaHisFile\\temp\\pdf";
		serverPath = "";
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		TParm parm=(TParm) this.getParameter();
		caseNo = parm.getValue("CASE_NO");
		mrNo = parm.getValue("MR_NO");
		
//		if(type.equals("1")){  //�ı������
//			this.setTitle("��֢�໤");
//			printType = "CCEMR";
//		}else if(type.equals("2")){
//			this.setTitle("���鲡��");
//			printType = "OPEMR";
//		}else if(type.equals("3")){
//			this.setTitle("�ĵ籨��");
//			opBookNo = parm.getValue("OPE_BOOK_NO");
//		}
		table = (TTable) this.getComponent("TABLE");
		addListener(getTTable("TABLE"));
		getTableData();
	}
	
	/**
	 * ��ѯ����TABLEд������
	 */
	public void getTableData(){
		String sql= " SELECT A.APPLICATION_NO, A.REPORT_DATE, A.CASE_NO, A.ORDER_DESC, A.CAT1_TYPE AS TYPE�� " + 
					" (CASE A.CAT1_TYPE WHEN 'RIS' THEN '���' ELSE '����' END) AS CAT1_TYPE " +
					" FROM MED_APPLY A, SYS_RISORDER B " +
					" WHERE A.CASE_NO = '" + caseNo + "' " +
					" AND A.ORDER_CODE = B.ORDER_CODE " +
					" AND A.STATUS IN (6,7) " ;
		
//		System.out.println("qitajianbaogaoPDFSQL:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable("TABLE")
						.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * ˫���¼�
	 */
	public void onTableDoubleClick(){
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue().getRow(row);
		
		String pdfPath =
	            "PDF\\" + caseNo.substring(0, 2) + "\\" + caseNo.substring(2, 4) + "\\"
	                    + mrNo;
//			this.messageBox("pdfPath  " + pdfPath);
			serverPath =
	            TConfig.getSystemValue("FileServer.Main.Root") + "\\"
	                    + TConfig.getSystemValue("EmrData") + "\\" + pdfPath;
//			this.messageBox("serverPath  " + serverPath);
//			System.out.println("lujing-------------- " + serverPath);
		    String pdfFile[] = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), serverPath);
//		    this.messageBox("pdfFile===== " + pdfFile);
//		    for(int i = 0;i<pdfFile.length;i++){
//		    	this.messageBox(pdfFile[i]);
//		    }
		    if (pdfFile == null || pdfFile.length < 1) {
		    	this.messageBox(serverPath+"�����ļ���");
		        return ;
		    }
		    List pdfList = Arrays.asList(pdfFile);
		    if(tableParm.getValue("TYPE").equals("LIS")){
//		    	this.messageBox(tableParm.getValue("TYPE"));
		    	type = "���鱨��";
		    	pdfFileName = caseNo + "_" + type + "_" + tableParm.getValue("APPLICATION_NO");
				    for(int i = 0;i<pdfFile.length;i++){
				    	if(pdfFile[i].contains(pdfFileName)){
				    		pdfFileName = pdfFile[i];
				    	}
				    }
		    }else{
//		    	this.messageBox(tableParm.getValue("TYPE"));
		    	type = "��鱨��";
		    	pdfFileName = caseNo + "_" + type + "_" + tableParm.getValue("APPLICATION_NO") + ".pdf";
		    }
//		    this.messageBox(pdfFileName);
		    if(pdfFileName.length() <= 0){
		    	this.messageBox("����ҽ���Ƿ���ȷ");
		    	return;
		    }
	    	
//	        this.messageBox("pdfList  " + pdfList);
//	        this.messageBox("pdfList.contains(pdfFileName)  " + pdfList.contains(pdfFileName));
	        if(pdfList.contains(pdfFileName)){
		        byte data[] =
		            TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), serverPath + "\\"
		                    + pdfFileName);
//		        String y = null;
//			    for(int x = 0;x<pdfFile.length;x++){
//			    	this.messageBox(pdfFile[x]);
//			    	y = y + "    " + pdfFile[x];
//			    }
//		        this.messageBox("data[]  " + y);
			    if (data == null) {
			        this.messageBox_("��������û���ҵ��ļ� " + serverPath + "\\" + pdfFileName);
			    	return;
			    }
			    try {
//			    	this.messageBox("FileTool.setByte(tempPath + '\\' + pdfFileName, data)");
			        FileTool.setByte(tempPath + "\\" + pdfFileName, data);
			    }
			    catch (Exception e) {
			        e.printStackTrace();
			    }
			    Runtime runtime = Runtime.getRuntime();
			    try {
					runtime.exec("cmd.exe /C start  " + tempPath + "\\"
			                + pdfFileName); 
//					this.messageBox("cmd.exe /C start acrord32 /P /h " + tempPath + "\\"
//			                + pdfFileName);
			    }
			    catch (Exception ex) {
			        ex.printStackTrace();
			    }
	        }else{
	        	this.messageBox_("��������û���ҵ��ļ� " + serverPath + "\\" + pdfFileName);
		    	return;
	        }
//		try {
//			// ���ļ�
//			runtime.exec("rundll32 url.dll FileProtocolHandler "
//					+ tempPath + "\\" + fileName);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	
	}
   
	
}
