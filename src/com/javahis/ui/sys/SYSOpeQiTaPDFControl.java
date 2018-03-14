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
 * Title: 门诊\住院护士\医生站 其他报告
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2018年1月
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
	/**检验或检查报告*/
	String type="";
	/**文件名*/
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
		
//		if(type.equals("1")){  //改变标题名
//			this.setTitle("重症监护");
//			printType = "CCEMR";
//		}else if(type.equals("2")){
//			this.setTitle("手麻病历");
//			printType = "OPEMR";
//		}else if(type.equals("3")){
//			this.setTitle("心电报告");
//			opBookNo = parm.getValue("OPE_BOOK_NO");
//		}
		table = (TTable) this.getComponent("TABLE");
		addListener(getTTable("TABLE"));
		getTableData();
	}
	
	/**
	 * 查询并向TABLE写入数据
	 */
	public void getTableData(){
		String sql= " SELECT A.APPLICATION_NO, A.REPORT_DATE, A.CASE_NO, A.ORDER_DESC, A.CAT1_TYPE AS TYPE， " + 
					" (CASE A.CAT1_TYPE WHEN 'RIS' THEN '检查' ELSE '检验' END) AS CAT1_TYPE " +
					" FROM MED_APPLY A, SYS_RISORDER B " +
					" WHERE A.CASE_NO = '" + caseNo + "' " +
					" AND A.ORDER_CODE = B.ORDER_CODE " +
					" AND A.STATUS IN (6,7) " ;
		
//		System.out.println("qitajianbaogaoPDFSQL:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE")
						.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * 双击事件
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
		    	this.messageBox(serverPath+"请检查文件夹");
		        return ;
		    }
		    List pdfList = Arrays.asList(pdfFile);
		    if(tableParm.getValue("TYPE").equals("LIS")){
//		    	this.messageBox(tableParm.getValue("TYPE"));
		    	type = "检验报告";
		    	pdfFileName = caseNo + "_" + type + "_" + tableParm.getValue("APPLICATION_NO");
				    for(int i = 0;i<pdfFile.length;i++){
				    	if(pdfFile[i].contains(pdfFileName)){
				    		pdfFileName = pdfFile[i];
				    	}
				    }
		    }else{
//		    	this.messageBox(tableParm.getValue("TYPE"));
		    	type = "检查报告";
		    	pdfFileName = caseNo + "_" + type + "_" + tableParm.getValue("APPLICATION_NO") + ".pdf";
		    }
//		    this.messageBox(pdfFileName);
		    if(pdfFileName.length() <= 0){
		    	this.messageBox("请检查医嘱是否正确");
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
			        this.messageBox_("服务器上没有找到文件 " + serverPath + "\\" + pdfFileName);
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
	        	this.messageBox_("服务器上没有找到文件 " + serverPath + "\\" + pdfFileName);
		    	return;
	        }
//		try {
//			// 打开文件
//			runtime.exec("rundll32 url.dll FileProtocolHandler "
//					+ tempPath + "\\" + fileName);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	
	}
   
	
}
