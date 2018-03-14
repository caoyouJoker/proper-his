package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.adm.ADMInpControl;
import com.javahis.ui.database.TablePropertyDialogControl;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
/**
 * <p>Title:紧急手术病患列表</p>
 *
 * <p>Description: 紧急手术病患列表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author kangy 2016-6-30
 * @version 1.0
 */
public class OPEUrgentSurgerySickControl extends TControl{
	private static String TABLE = "TABLE";
	Pat pat;
	TParm selparm;
	private TTable table;
	private String mrNo="";
	private String startDate="";
	private String endDate="";
	private String deptCode="";
	private String drCode="";
	private String stationCode="";
	private SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmss");
	private TParm tableParm=new TParm();
	public void onInit(){
        super.onInit();
        this.setValue("MR_NO","");
        this.setValue("DEPT_CODE","");
        this.setValue("ATTEND_DR_CODE1","");
        this.setValue("STATION_CODE","");
   	 Timestamp date = StringTool.getTimestamp(new Date());
	 this.setValue("START_DATE",
			 StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
	 this.setValue("END_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
        table=(TTable) this.getComponent("TABLE");
	}	
	
	//查询 
	public void onQuery(){
		 startDate =sf.format((Date) this.getValue("START_DATE"));
		 endDate = sf.format((Date) this.getValue("END_DATE"));
		 String sql="SELECT A.CASE_NO,A.MR_NO,A.IPD_NO,B.PAT_NAME,  " +
		 		"CASE  WHEN B.SEX_CODE = '1' THEN '男'  " +
		 		"WHEN B.SEX_CODE = '2' THEN '女'  ELSE '未知' END SEX_CODE," +
		 		"B.BIRTH_DATE,B.ADDRESS,B.CONTACTS_NAME,B.CONTACTS_TEL," +
		 		"A.IN_DATE,A.DEPT_CODE,A.STATION_CODE," +
		 		"A.ATTEND_DR_CODE ATTEND_DR_CODE,C.ICD_CHN_DESC,A.BED_NO " +
		 		" FROM ADM_INP A,SYS_PATINFO B,SYS_DIAGNOSIS C " +
		 		" WHERE A.MR_NO=B.MR_NO AND A.MAINDIAG=C.ICD_CODE " +
		 		" AND A.DS_DATE IS NULL AND A.CANCEL_FLG<>'Y'  " +
		 		" AND A.IN_DATE BETWEEN TO_DATE ('"+startDate+"','YYYYMMDDHH24MISS') "+
                " AND TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS' )" +
                		" AND A.URG_FLG='Y'";
		 if(this.getValue("DEPT_CODE").toString().length()>0){
			 sql+=" AND A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
		 }
		 if(this.getValue("STATION_CODE").toString().length()>0){
			 sql+=" AND A.STATION_CODE='"+this.getValueString("STATION_CODE")+"'";
		 }
		 if(this.getValue("ATTEND_DR_CODE1").toString().length()>0){
			 sql+=" AND A.ATTEND_DR_CODE='"+this.getValueString("ATTEND_DR_CODE1")+"'";
		 }
		 if(this.getValue("MR_NO").toString().length()>0){
			 sql+=" AND A.MR_NO='"+this.getValueString("MR_NO")+"'";
		 }
		  
		  tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		  	if(tableParm.getErrCode() < 0 ){
	    		this.messageBox(tableParm.getErrText());
	    		return;
	    	}
	        if(tableParm.getCount() <= 0)
	        {
	        	this.messageBox("查无数据");
	        }
		 table.setParmValue(tableParm);
	
	}
	
	
	//修改信息
	public void onChange(){
	int row= table.getSelectedRow();
		String mrNo=tableParm.getValue("MR_NO",row);
		TParm parm=new TParm();
		parm.setData("MR_NO", mrNo);
		if(row>=0){
			this.openDialog("%ROOT%\\config\\adm\\ADMInp.x",parm);
		
		}else{
			this.messageBox("请选择要修改的数据！");
			return;
		}
	}
/*	public TParm getSelectRowData(String tableTag) {
		// this.messageBox("===getSelectRowData==");
		int selectRow = (Integer) callFunction("UI|" + tableTag
				+ "|getSelectedRow");
		if (selectRow < 0)
			return new TParm();
		// out("行号" + selectRow);
//		TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
		TParm parm = this.getTTable(TABLE).getParmValue();
		// out("GRID数据" + parm);
//		System.out.println("00000 PARM  parmRow is ::"+parm);
		TParm parmRow = parm.getRow(selectRow);
//		System.out.println("111111parmRow parmRow is ::"+parmRow);
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("INWSHEET")) {
			parmRow.setData("INW_DEPT_CODE", parmRow.getValue("DEPT_CODE"));
			parmRow.setData("INW_STATION_CODE", parmRow
					.getValue("STATION_CODE"));
			parmRow.setData("INW_VC_CODE", parmRow.getValue("VC_CODE"));
		}
		return parmRow;
	}*/
	//清空
	public void onClear(){
		 table.removeRowAll();
		onInit();	
		
	}
	/**
	 * 住院证打印
	 */ 
	public void onPrint(){
		
		 TTable table = (TTable) this.getComponent(TABLE);
	        int row = table.getSelectedRow();
		if (row != -1) {
			selparm = tableParm.getRow(row);// 单击选中的行
			String caseNo = selparm.getValue("CASE_NO");
			pat = pat.onQueryByMrNo(selparm.getValue("MR_NO"));
			String subClassCode = TConfig
					.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
			String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");

			String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='"
					+ caseNo + "'";
			sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='"
					+ subClassCode + "'";

			// System.out.println("===sql==33333=" + sql);
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("6result1::::"+result1);
			if (result1.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}
			if (result1.getCount() <= 0) {
				this.onPrint1();
			} else {
				String filePath = result1.getValue("FILE_PATH", 0);
				String fileName = result1.getValue("FILE_NAME", 0);
				// TParm p = new TParm();
				// p.setData("RESV_NO", caseNoresvNo);
				// TParm resvPrint =
				// ADMResvTool.getInstance().selectFroPrint(p);
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("IPD_NO", pat.getIpdNo());
				parm.setData("PAT_NAME", pat.getName());
				parm.setData("SEX", pat.getSexString());
				parm.setData("AGE", StringUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate())); // 年龄
				parm.setData("CASE_NO", caseNo);
				//parm.setData("CASE_NO", caseNo);// duzhw add
				//parm.setData("CASE_NO", resvParm.getValue("RESV_NO",0)); //duzhw add
				Timestamp ts = SystemTool.getInstance().getDate();
				parm.setData("ADM_TYPE", "O");
				parm.setData("DEPT_CODE", selparm.getValue("DEPT_CODE"));
				parm.setData("ADM_DATE", ts);
				parm.setData("STYLETYPE", "1");
				parm.setData("RULETYPE", "3");
				parm.setData("SYSTEM_TYPE", "ODI");
				TParm emrFileData = new TParm();
				emrFileData.setData("FILE_PATH", filePath);
				emrFileData.setData("FILE_NAME", fileName);
				emrFileData
						.setData("FILE_SEQ", result1.getValue("FILE_SEQ", 0));
				emrFileData.setData("SUBCLASS_CODE", subClassCode);
				emrFileData.setData("CLASS_CODE", classCode);
				emrFileData.setData("FLG", true);
				parm.setData("EMR_FILE_DATA", emrFileData);
			//	System.out.println("parmss:::DDDDDD:"+parm);
				this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
			}
		}else{
        	this.messageBox("请选择病患！");
        }
	}

	public void onPrint1() {

		String fileServerMainRoot = TIOM_FileServer
				.getPath("FileServer.Main.Root");
		String emrData = TIOM_FileServer.getPath("EmrData");
		String sql1 = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='"
				+ selparm.getValue("CASE_NO") + "'  ORDER BY OPT_DATE DESC ";
	//	System.out.println("======sql===###########555555555555555########===="
	//			+ sql1);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));

		TParm actionParm = new TParm();
		actionParm.setData("MR_NO", selparm.getValue("MR_NO"));
		actionParm.setData("IPD_NO", selparm.getValue("IPD_NO"));
		actionParm.setData("PAT_NAME", selparm.getValue("PAT_NAME"));
		actionParm.setData("AGE", StringUtil.showAge(pat.getBirthday(), selparm
				.getTimestamp("BIRTH_DATE"))); // 年龄
		Timestamp ts = SystemTool.getInstance().getDate();
		// actionParm.setData("CASE_NO", caseNo);
		String sql = "SELECT RESV_NO FROM ADM_RESV WHERE IN_CASE_NO='"
				+ selparm.getValue("CASE_NO") + "'";
		TParm resvParm = new TParm(TJDODBTool.getInstance().select(sql));
		actionParm.setData("CASE_NO",resvParm.getValue("RESV_NO",0)); // duzhw
		// add
		actionParm.setData("ADM_TYPE", "O");
		actionParm.setData("DEPT_CODE", selparm.getValue("DEPT_CODE"));
		actionParm.setData("STATION_CODE", selparm.getValue("STATION_CODE"));
		// actionParm.setData("ICD_CHN_DESC",
		// selparm.getValue("ICD_CHN_DESC"));
		actionParm.setData("ADM_DATE", ts);
		actionParm.setData("STYLETYPE", "1");
		actionParm.setData("RULETYPE", "3");
		actionParm.setData("SYSTEM_TYPE", "ODO");
		TParm emrFileData = new TParm();
		String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
		String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
		String subClassCode = TConfig
				.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		emrFileData.setData("TEMPLET_PATH", path);
		emrFileData.setData("EMT_FILENAME", fileName);
		emrFileData.setData("SUBCLASS_CODE", subClassCode);
		emrFileData.setData("CLASS_CODE", classCode);
		actionParm.setData("EMR_FILE_DATA", emrFileData);
		this.openWindow(
				"%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
		sql1 = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='"
				+ resvParm.getValue("RESV_NO", 0)
				+ "'  ORDER BY OPT_DATE DESC ";
		result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		String oldFileName = result1.getValue("FILE_NAME", 0);
		String oldFilePath = result1.getValue("FILE_PATH", 0);
		String seq = result1.getValue("FILE_SEQ", 0);
		//判断预约住院号码文件是否存在
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(), fileServerMainRoot + emrData
				+ oldFilePath + "\\" + oldFileName + ".jhw");
		if(null!=data&&data.length>0){
			//病历界面保存操作创建服务器jhw文件
			if (result1.getCount() > 0) {// 保存数据了
				// 移动JHW文件并改名称.
				String dateStr = StringTool.getString(ts, "yyyyMMdd");
				// 获得新的文件路径
				StringBuilder filePathSb = new StringBuilder();
				filePathSb.append("JHW\\").append(dateStr.substring(2, 4))
						.append("\\").append(dateStr.substring(4, 6)).append(
								"\\").append(selparm.getValue("MR_NO"));
				String newFilePath = filePathSb.toString();

				// 获得新的文件名称
				String[] oldFileNameArray = oldFileName.split("_");

				StringBuilder sb = new StringBuilder(selparm
						.getValue("CASE_NO"));
				sb.append("_").append(oldFileNameArray[1]).append("_").append(
						oldFileNameArray[2]);
				String newFileName = sb.toString();
				try {
					// 移动文件
					TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
							fileServerMainRoot + emrData + newFilePath + "\\"
									+ newFileName + ".jhw", data);

					TParm action = new TParm(
							TJDODBTool
									.getInstance()
									.select(
											"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
													+ selparm
															.getValue("CASE_NO")
													+ "'"));
					int index = action.getInt("MAXFILENO", 0);
					// 更新数据库
					String sql2 = "UPDATE EMR_FILE_INDEX SET CASE_NO='"
							+ selparm.getValue("CASE_NO") + "',FILE_PATH='"
							+ newFilePath + "',FILE_NAME='" + newFileName
							+ "',FILE_SEQ='" + index + "' WHERE CASE_NO='"
							+ resvParm.getValue("RESV_NO", 0)
							+ "' AND FILE_SEQ='" + seq + "'";

					// System.out.println("======sql11111========"+sql1);
					TParm result2 = new TParm(TJDODBTool.getInstance().update(
							sql2));
					if (result2.getErrCode() < 0) {
						err(result2.getErrName() + "" + result2.getErrText());
						messageBox("更新失败!");
						return;
					}
//					// 删除老文件；
					boolean delFlg = TIOM_FileServer.deleteFile(TIOM_FileServer
							.getSocket(), fileServerMainRoot + emrData
							+ oldFilePath + "\\" + oldFileName + ".jhw");
					if (!delFlg) {
						this.messageBox("删除原文件失败!");
						return;
					}
				} catch (Exception e) {
					this.messageBox("移动文件失败!");
				}
			}
		}else{
			//病历界面没有保存情况，没有创建服务器jhw文件
			
		}
		
		//System.out.println("result1::s:::::fgsdfg:111111:::" + result1);
		
	}
	
	
	/**
	 * 病案号回车
	 */
	public void onMrnoAction(){
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO")) ;
		this.setValue("MR_NO", mrNo) ;
		this.onQuery();
		
	}
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "紧急手术病患列表");
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
}
