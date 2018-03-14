package com.javahis.ui.odi;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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

public class ODIOpeMrControl extends TControl{
	private String caseNo=""; 
	private String printType="";
	TTable table ;
	private String tempPath;// add by huangjw
	private String serverPath;// add by huangjw
	String type = "";
	String opBookNo = "";
	private String mrNo = "";
	private boolean ascending = false;
	private int sortColumn = -1;
	private BILComparator compare = new BILComparator();
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
		type = parm.getValue("TYPE");
		mrNo = parm.getValue("MR_NO");
		if(type.equals("1")){
			this.setTitle("重症监护");
			printType = "CCEMR";
		}else if(type.equals("2")){
			this.setTitle("手麻病历");
			printType = "OPEMR";
		}else if(type.equals("3")){
			this.setTitle("心电报告");
			opBookNo = parm.getValue("OPE_BOOK_NO");
		}
		table = (TTable) this.getComponent("TABLE");
		addListener(getTTable("TABLE"));
		getTableData();
	}
	
	public void getTableData(){
		String sql="";
		if(type.equals("1")||type.equals("2")){
			 sql  = "SELECT * FROM CRP_FILE_INDEX WHERE CASE_NO = '"+caseNo+"' AND CHART_TYPE = '"+printType+"'";
			
		}else{
			// sql  = "SELECT * FROM CRP_FILE_INDEX WHERE CASE_NO = '"+caseNo+"' AND OPE_BOOK_NO IN ("+opBookNo+") ";
			sql = 	  " SELECT DISTINCT" +
			          "  A.CASE_NO," +
			          "  A.FILE_SEQ," +
			          "  A.MR_NO," +
			          "  A.CHART_TYPE," +
			          "  A.CLASS_CODE," +
			          "  A.SUBCLASS_CODE," +
			          "  A.CHART_NAME," +
			          "  A.FILE_NAME," +
			          "  A.CONFIRM_TIME," +
			          "  A.OPE_BOOK_NO," +
			          "  A.EDIT_USER," +
			          "  A.EDIT_DATE," +
			          "  A.STATUS," +
			          "  A.STATE_TIME," +
			          "  A.OPT_DATE," +
			          "  A.OPT_TERM," +
			          "  A.FILE_PATH," +
			          "  CASE" +
			          "   WHEN B.ADM_TYPE IN ('O', 'E') THEN '门'" +
			          "   WHEN B.ADM_TYPE = 'I' THEN '住'" +
			          "  END" +
			          "  OI" +
			          "  FROM (SELECT *" +
			          "  FROM CRP_FILE_INDEX" +
			          "   WHERE MR_NO = '"+mrNo+"'" +
			          "  AND OPE_BOOK_NO IN("+opBookNo+")) A," +
			                         "   MED_APPLY B" +
			                         "   WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = B.CASE_NO ";	
		}
		System.out.println("33333:"+sql);
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
		TParm tableParm = table.getParmValue();
		
		//this.messageBox("=tableParm==="+tableParm);
		String fileName = tableParm.getValue("FILE_NAME", row)
				+ ".pdf";
		String filePath = tableParm.getValue("FILE_PATH", row);
		serverPath = getEmrDataDir(caseNo)
				+ filePath;
		//this.messageBox("====="+filePath);
		Runtime runtime = Runtime.getRuntime();
		byte data[] = TIOM_FileServer.readFile(getFileServerAddress(caseNo), serverPath + "\\" + fileName);
		if (data == null) {
			messageBox_("服务器上没有找到文件 " + serverPath + "\\"
					+ fileName);
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\" + fileName, data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler "
					+ tempPath + "\\" + fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}
    public String getEmrDataDir(String caseNo){
    	//
    	String strEmrDataDir=TIOM_FileServer.getRoot() + TIOM_FileServer.getPath("EmrData");
    	// 1.caseNo不是空串情况下
		if (caseNo != null && !caseNo.equals("")) {
			// 2.包含-的情况
				// 3.取第一个caseNo下的情况前2位的情况
				String sYear = caseNo.substring(0, 2);
				//System.out.println("---sYear：---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		        String root = config.getString("","FileServer." + sYear + ".Root");
		        if (root != null && !root.equals("")) {
				// 4.找指定的配置文件，如果有则：
		        	strEmrDataDir = TIOM_FileServer.getRoot(sYear)+ TIOM_FileServer.getPath("EmrData");
		        }
				
			}
    	
    	return strEmrDataDir;
    }
    
    /**
	 * 通过文件名，确认文件存储位置
	 * 
	 * @param fileName
	 * @return
	 */
	public TSocket getFileServerAddress(String caseNo) {
		System.out.println("=====fileName:====="+caseNo);
		//默认文件服器
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		// 1.caseNo不是空串情况下
		if (caseNo != null && !caseNo.equals("")) {
			// 2.包含-的情况
				// 3.取第一个caseNo下的情况前2位的情况
				String sYear = caseNo.substring(0, 2);
				System.out.println("---sYear：---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		        String ip = config.getString("","FileServer." + sYear + ".IP");
		        System.out.println("====IP：======"+ip);
		        if (ip != null && !ip.equals("")) {
				// 4.找指定的配置文件，如果有则：
		        	tsocket = TIOM_FileServer.getSocket(sYear);
		        }
				
			}
		//   	
		return tsocket;
	}
	
}
