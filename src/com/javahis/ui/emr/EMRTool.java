package com.javahis.ui.emr;

import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import com.dongyang.data.TNull;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.data.TParm;
import com.dongyang.tui.DText;
import com.dongyang.control.TControl;
import com.dongyang.ui.TWord;
import jdo.emr.EMRCreateXMLTool;

/**
 * <p>
 * Title: EMR 工具类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class EMRTool {
	public EMRTool(String caseNo, String mrNo, TControl control) {
		this.caseNo = caseNo;
		this.mrNo = mrNo;
		this.control = control;
	}

	private TControl control;
	private String caseNo;
	private String mrNo;

	/**
	 * 得到EMR保存路径
	 * 
	 * @return String
	 */
	private String getEMRSaveFilePath() {
		String caseNo = this.caseNo;
		String mr_no = this.mrNo;
		String path = "";
		path += "JHW\\";
		path += caseNo.substring(0, 2) + "\\";
		path += caseNo.substring(2, 4) + "\\";
		path += mr_no + "";
		return path;
	}

	/**
	 * 得到EMR保存根路径
	 * 
	 * @return String
	 */
	private String getRootEMRSaveFilePath() {
		// 目录表第一个根目录FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// 模板路径服务器
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		String path = rootName + templetPathSer + "";
		return path;
	}

	/**
	 * 得到EMR保存文件名
	 * 
	 * @return String
	 */
	private String getnewEMRSaveFileName(String filename, boolean flg) {
		String fileName = "";
		fileName += this.caseNo;
		fileName += "_";
		fileName += filename;
		// luhai modify 2011-07-18 防止重复文件多次保存
		// fileName += "_";
		// int seq = getFileSEQ(odo.getMrNo(), odo.getCaseNo(), filename,
		// (getRootEMRSaveFilePath() + "\\" +
		// getEMRSaveFilePath()));
		// String seqstr = seq < 10 ? ("0" + seq) : (seq + "");
		// fileName += seqstr;
		// luhai modify 2011-07-18 防止重复文件多次保存end

		String seqstr = "";
		if (flg)
			seqstr = "_" + getFileSEQ(this.caseNo, "");
		fileName += seqstr;
		fileName += ".jhw";
		return fileName;
	}

	/**
	 * 得到EMR保存文件名
	 * 
	 * @return String
	 */
	private String getEMRSaveFileName(String filename) {
		String fileName = "";
		fileName += this.caseNo;
		fileName += "_";
		fileName += filename;
		// luhai modify 2011-07-18 防止重复文件多次保存
		// fileName += "_";
		// int seq = getFileSEQ(odo.getMrNo(), odo.getCaseNo(), filename,
		// (getRootEMRSaveFilePath() + "\\" +
		// getEMRSaveFilePath()));
		// String seqstr = seq < 10 ? ("0" + seq) : (seq + "");
		// fileName += seqstr;
		// luhai modify 2011-07-18 防止重复文件多次保存end

		String seqstr = "_" + getFileSEQ(this.caseNo, "");
		fileName += seqstr;
		fileName += ".jhw";
		return fileName;
	}

	/**
	 * 上传EMR
	 * 
	 * @param word
	 *            TWord
	 * @param fileName
	 *            String
	 */
	public boolean saveEMR(TWord word, String fileName, String classCode,
			String subClassCode, boolean flg) {
		return saveEMRAll(word, fileName, classCode, subClassCode, flg);
		// String fileNameNew = getEMRSaveFileName(fileName.replace("'", ""));
		// System.out.println("====saveEMR fileName===="+fileNameNew);
		// String fileNameNew =fileName;
	}

	/**
	 * 上传EMR
	 * 
	 * @param word
	 *            TWord
	 * @param fileName
	 *            String
	 */
	// ================= modify by chenxi 20120702
	public void saveEMR(TWord word, String fileName, String classCode,
			String subClassCode) {
		saveEMRAll(word, fileName, classCode, subClassCode);

	}

	// ============= modify by chenxi 20120702
	/**
	 * 上传EMR
	 * 
	 * @param DText
	 *            DText
	 * @param fileName
	 *            String
	 */
	// =============== modify by chenxi 20120702 start
	public void saveEMR(DText dText, String fileName, String classCode,
			String subClassCode, boolean flg) {
		saveEMRAll(dText, fileName, classCode, subClassCode, flg);

	}

	// ================= modify by chenxi 20120702 stop

	/**
	 * 上传EMR
	 * 
	 * @param DText
	 *            DText
	 * @param fileName
	 *            String
	 */
	public void saveEMR(DText dText, String fileName, String classCode,
			String subClassCode) {
		saveEMRAll(dText, fileName, classCode, subClassCode);
		/**
		 * String fileNameNew = getEMRSaveFileName(fileName.replace("'", ""));
		 * fileNameNew = fileNameNew.substring(0,fileNameNew.lastIndexOf("."));
		 * EMRCreateXMLTool.getInstance().createXML(getEMRSaveFilePath(),
		 * fileNameNew, "EmrData", dText);
		 **/
	}

	/**
	 * 上传EMR
	 * 
	 * @param obj
	 *            Object
	 * @param fileName
	 *            String
	 */
	// ============modify by chenxi 20120702 start
	public void saveEMR(Object obj, String fileName, String classCode,
			String subClassCode, boolean flg) {
		if (obj instanceof DText || obj instanceof TWord) {
			saveEMR((DText) obj, fileName, classCode, subClassCode, flg);
		}
	}

	// =============== modify by chenxi 20120702 stop
	/**
	 * 上传EMR
	 */
	public void saveEMR(Object obj, String fileName, String classCode,
			String subClassCode) {
		if (obj instanceof DText || obj instanceof TWord) {
			saveEMR((DText) obj, fileName, classCode, subClassCode);
		}
	}

	/**
	 * 上传EMR
	 * 
	 * @param obj
	 *            Object
	 */
	// ============= chenxi modify 20120702 start
	public boolean saveEMRAll(Object obj, String fileName, String classCode,
			String subClassCode, boolean flg) {
		// 处理fileName-filename中的处方号会有引号故滤去
		fileName = fileName.replace("'", "");
		//System.out.println("====fileName1====="+fileName);
		fileName = this.getnewEMRSaveFileName(fileName, flg);
		//System.out.println("====fileName2====="+fileName);
		String fileNameNew = fileName;
		// String sql = "SELECT * FROM EMR_FILE_INDEX";
		// sql += " WHERE CASE_NO='" + caseNo + "' ";
		// sql += " AND  SUBCLASS_CODE='" + subClassCode + "'";
		// // System.out.println("==sql=="+sql);
		// TParm action = new TParm(getDBTool().select(sql));
		// if (!flg) {
		// if (action.getCount() > 0)
		// fileNameNew = action.getValue("FILE_NAME", 0);
		// }
		String filePath = this.getEMRSaveFilePath();
		if (obj instanceof TWord) {
			((TWord) obj).getFileManager().setMessageBoxSwitch(false);
			((TWord) obj).getFileManager()
					.onSaveAsReport(filePath, fileName, 3);
		} else if (obj instanceof DText) {
			((DText) obj).getFileManager().setMessageBoxSwitch(false);
			((DText) obj).getFileManager()
					.onSaveAsReport(filePath, fileName, 3);
		} else {
			return false;
		}
		// 保存数据库
		TParm savaParm = new TParm();
		putBasicSysInfoIntoParm(savaParm);
		savaParm.setData("CASE_NO", this.caseNo);
		// fileName.split("_")[2].replace(".jhw","").replace(".JHW","")
		/**
		 * int seq = getFileSEQ((getRootEMRSaveFilePath() + "\\" +
		 * getEMRSaveFilePath()));
		 **/
		// savaParm.setData("FILE_SEQ", seq);
		savaParm.setData("MR_NO", this.mrNo);
		// 多笔数据
		// $$=========== Modied by lx 2012/04/18 Start===========$$//
		int seq = getFileSEQ(this.caseNo, subClassCode);
		// System.out.println("=====序号seq====="+seq);
		// ==================modify by chenxi 20120702
		savaParm.setData("FILE_SEQ", seq);
		// =======================modify by chenxi 20120702
		// $$=========== Modied by lx 2012/04/18 end===========$$//
		TNull ipdTNull = new TNull(String.class);
		savaParm.setData("IPD_NO", ipdTNull);
		savaParm.setData("FILE_PATH", filePath);
		fileName = fileName.replace(".jhw", "").replace(".JHW", "");
		savaParm.setData("FILE_NAME", fileName);
		savaParm.setData("DESIGN_NAME", fileName);
		savaParm.setData("CLASS_CODE", classCode);
		// TNull subTNull = new TNull(String.class);
		savaParm.setData("SUBCLASS_CODE", subClassCode);
		savaParm.setData("DISPOSAC_FLG", "N");
		savaParm.setData("CREATOR_USER", Operator.getID());
		savaParm.setData("CREATOR_DATE", this.getDBTool().getDBTime());
		savaParm.setData("REPORT_FLG", "Y");
		TParm existResult = OdiMainTool.getInstance().checkEmrFileExist(
				this.cloneTParm(savaParm));
		if (existResult.getRow(0).getInt("TOTAL") > 0) {
			//add by lx 2013/05/08  加入更新病历处理
			//数据存在
			//System.out.print("已存在的病历");
			TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction",
					"updateEmrFileByFile", savaParm);
			if (result.getErrCode() < 0) {
				control.messageBox("文件保存失败");
				return false;
			}
		} else {
			TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction",
					"saveNewEmrFile", savaParm);
			if (result.getErrCode() < 0) {
				control.messageBox("文件保存失败");
				return false;
			}
		}
		// $$===============2011/05/02 生成xml文件start===================$$//
		if (obj instanceof TWord) {

			fileNameNew = fileNameNew
					.substring(0, fileNameNew.lastIndexOf("."));
//			 System.out.println("====fileNameNew=====" + fileNameNew);
			return EMRCreateXMLTool.getInstance().createTXML(getEMRSaveFilePath(),
					fileNameNew, "EmrData", ((TWord) obj));

		} else if (obj instanceof DText) {

			fileNameNew = fileNameNew
					.substring(0, fileNameNew.lastIndexOf("."));
			return EMRCreateXMLTool.getInstance().createTXML(getEMRSaveFilePath(),
					fileNameNew, "EmrData", ((DText) obj));
		}
		// $$===============2011/05/02 生成xml文件end===================$$//
		return true;
	}

	// =============== chenxi modify 20120702 stop

	/**
	 * 上传EMR
	 * 
	 * @param obj
	 *            Object
	 */
	public void saveEMRAll(Object obj, String fileName, String classCode,
			String subClassCode) {
		// 处理fileName-filename中的处方号会有引号故滤去
		fileName = fileName.replace("'", "");
		fileName = this.getEMRSaveFileName(fileName);
		// System.out.println("====fileName====="+fileName);
		String fileNameNew = fileName;

		String filePath = this.getEMRSaveFilePath();
		if (obj instanceof TWord) {
			((TWord) obj).getFileManager().setMessageBoxSwitch(false);
			((TWord) obj).getFileManager()
					.onSaveAsReport(filePath, fileName, 3);
		} else if (obj instanceof DText) {
			((DText) obj).getFileManager().setMessageBoxSwitch(false);
			((DText) obj).getFileManager()
					.onSaveAsReport(filePath, fileName, 3);
		} else {
			return;
		}
		// 保存数据库
		TParm savaParm = new TParm();
		putBasicSysInfoIntoParm(savaParm);
		savaParm.setData("CASE_NO", this.caseNo);
		// fileName.split("_")[2].replace(".jhw","").replace(".JHW","")
		/**
		 * int seq = getFileSEQ((getRootEMRSaveFilePath() + "\\" +
		 * getEMRSaveFilePath()));
		 **/
		// savaParm.setData("FILE_SEQ", seq);
		savaParm.setData("MR_NO", this.mrNo);

		// $$=========== Modied by lx 2012/04/18 Start===========$$//
		int seq = getFileSEQ(this.caseNo, subClassCode);
		// System.out.println("=====序号seq====="+seq);
		// ==================modify by chenxi 20120702

		savaParm.setData("FILE_SEQ", seq);
		// =======================modify by chenxi 20120702

		// $$=========== Modied by lx 2012/04/18 end===========$$//
		TNull ipdTNull = new TNull(String.class);
		savaParm.setData("IPD_NO", ipdTNull);
		savaParm.setData("FILE_PATH", filePath);
		fileName = fileName.replace(".jhw", "").replace(".JHW", "");
		savaParm.setData("FILE_NAME", fileName);
		savaParm.setData("DESIGN_NAME", fileName);
		savaParm.setData("CLASS_CODE", classCode);
		// TNull subTNull = new TNull(String.class);
		savaParm.setData("SUBCLASS_CODE", subClassCode);
		savaParm.setData("DISPOSAC_FLG", "N");
		savaParm.setData("CREATOR_USER", Operator.getID());
		savaParm.setData("CREATOR_DATE", this.getDBTool().getDBTime());
		savaParm.setData("REPORT_FLG", "Y");
		savaParm.setData("CURRENT_USER", Operator.getID());
		TParm existResult = OdiMainTool.getInstance().checkEmrFileExist(
				this.cloneTParm(savaParm));
		if (existResult.getRow(0).getInt("TOTAL") > 0) {
			// 数据存在
		} else {
			TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction",
					"saveNewEmrFile", savaParm);
			if (result.getErrCode() < 0) {
				control.messageBox("文件保存失败");
			}
		}
		// $$===============2011/05/02 生成xml文件start===================$$//
		if (obj instanceof TWord) {

			fileNameNew = fileNameNew
					.substring(0, fileNameNew.lastIndexOf("."));
			// System.out.println("====fileNameNew=====" + fileNameNew);
			EMRCreateXMLTool.getInstance().createXML(getEMRSaveFilePath(),
					fileNameNew, "EmrData", ((TWord) obj));

		} else if (obj instanceof DText) {

			fileNameNew = fileNameNew
					.substring(0, fileNameNew.lastIndexOf("."));
			EMRCreateXMLTool.getInstance().createXML(getEMRSaveFilePath(),
					fileNameNew, "EmrData", ((DText) obj));
		}
		// $$===============2011/05/02 生成xml文件end===================$$//
	}

	/**
	 * 取最打文件号
	 * 
	 * @param caseNo
	 * @param subClassCode
	 * @return
	 */
	private int getFileSEQ(String caseNo, String subClassCode) {
		String sql = "SELECT NVL(MAX(FILE_SEQ)+1,1) MAXFILENO  FROM EMR_FILE_INDEX";
		sql += " WHERE CASE_NO='" + caseNo + "' ";
		// System.out.println("==sql=="+sql);
		TParm action = new TParm(getDBTool().select(sql));
		int index = action.getInt("MAXFILENO", 0);
		return index;
	}

	/**
	 * 判断文件数据是否存在
	 * 
	 * @param caseNo
	 * @param subClassCode
	 * @return
	 */
	private boolean checkSumFileSEQ(String caseNo, String subClassCode) {
		boolean flg = false;
		String sql = "SELECT * FROM EMR_FILE_INDEX";
		sql += " WHERE CASE_NO='" + caseNo + "' ";
		sql += " AND  SUBCLASS_CODE='" + subClassCode + "' ";
		// System.out.println("==sql=="+sql);
		TParm action = new TParm(getDBTool().select(sql));
		if (action.getCount() > 0)
			flg = true;
		return flg;
	}

	/**
	 * 从文件中得到顺序号
	 * 
	 * @param mrno
	 *            String
	 * @param caseno
	 *            String
	 * @param type
	 *            String
	 * @param dir
	 *            String
	 * @return int
	 */
	private synchronized int getFileSEQ(String dir) {
		int seq = 0;
		// File f = new File(dir);
		// if (!f.isDirectory())
		// return (seq + 1);
		// File list[] = f.listFiles();
		String[] list = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(),
				dir);
		// 卢海删除-取文件最大数量
		seq = list.length;
		// for (int i = 0; i < list.length; i++) {
		// if (!list[i].isFile())
		// continue;
		// String name = list[i].getName();
		// // 相同类型的文件 取序号加一
		// // if (type.equalsIgnoreCase(name.split("_")[1])) {
		// int oldSeq = TypeTool.getInt(name.split("_")[2]);
		// if (oldSeq > seq) {
		// seq = oldSeq;
		// }
		// // }
		// }
		// seq += 1;
		return seq;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", this.getDBTool().getDBTime());
		parm.setData("OPT_TERM", Operator.getRegion());
	}

	/**
	 * 克隆对象
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm cloneTParm(TParm from) {
		TParm returnTParm = new TParm();
		for (int i = 0; i < from.getNames().length; i++) {
			returnTParm.setData(from.getNames()[i],
					from.getValue(from.getNames()[i]));
		}
		return returnTParm;
	}

}
