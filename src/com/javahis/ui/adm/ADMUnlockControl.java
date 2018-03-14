package com.javahis.ui.adm;

import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;

import javax.swing.JLabel;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TComboBox;

import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;

import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.StringUtil;

//import org.eclipse.wb.swt.PictureShow;

/**
 * <p>
 * Title: 解锁弹窗
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author yanmm 2017/07/04
 * @version 1.0
 */
public class ADMUnlockControl extends TControl {
	public ADMUnlockControl() {
	}

	TParm acceptData = new TParm(); // 接参
	Pat pat = new Pat();
	TParm initParm = new TParm(); // 初始数据

	public void onInit() {
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			this.initUI(acceptData);
		}
	}

	/**
	 * 界面初始化
	 * 
	 * @param parm
	 *            TParm
	 */
	public void initUI(TParm parm) {
		Pat pat = new Pat();
		String mrNo = acceptData.getData("MR_NO").toString();
		pat = pat.onQueryByMrNo(mrNo);
		this.setValue("MR_NO", pat.getMrNo());
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("SEX_CODE", pat.getSexCode());
		this.setValue("IPD_NO", acceptData.getData("IPD_NO"));
		this.initQuery();
		viewPhoto(pat.getMrNo());
		onFILM_Flg();
	}

	/**
	 * 显示photo
	 * 
	 * @param mrNo
	 *            String
	 */
	public void viewPhoto(String mrNo) {
		String photoName = mrNo + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null)
				return;
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			// Image image = ImageTool.getImage(data);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 10, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 10, 136, 180, null);
			}
		}
	}

	/**
	 * 初始化查询
	 */
	public void initQuery() {
		Timestamp date = StringTool.getTimestamp(new Date());
		String endDate = date.toString().substring(0, 19).replace("-", "");
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", acceptData.getData("MR_NO"));
		parm.setData("IPD_NO", acceptData.getData("IPD_NO"));
		// 查询病患住院信息
		TParm result = ADMInpTool.getInstance().selectall(parm);
		initParm.setRowData(result);
		// 获取病患基本信息
		Pat pat = Pat.onQueryByMrNo(acceptData.getValue("MR_NO"));
		setValue(
				"AGE",
				StringUtil.showAge(pat.getBirthday(),
						result.getTimestamp("IN_DATE", 0)));
		setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
		setValue("STATION_CODE", result.getData("STATION_CODE", 0));
		setValue("BED_NO", result.getData("BED_NO", 0));
		setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
		setValue("ATTEND_DR_CODE", result.getData("ATTEND_DR_CODE", 0));
		setValue("DIRECTOR_DR_CODE", result.getData("DIRECTOR_DR_CODE", 0));
		setValue("VS_NURSE_CODE", result.getData("VS_NURSE_CODE", 0));
		setValue("PATIENT_CONDITION", result.getData("PATIENT_CONDITION", 0));
		setValue("NURSING_CLASS", result.getData("NURSING_CLASS", 0));
		setValue("PATIENT_STATUS", result.getData("PATIENT_STATUS", 0));
		setValue("DIE_CONDITION", result.getData("DIE_CONDITION", 0));
		setValue("CARE_NUM", result.getData("CARE_NUM", 0));
		setValue("LEFT_BILPAY", result.getDouble("TOTAL_BILPAY", 0)); // 预交金
		// setValue("UNLOCK_Q", result.getData("UNLOCK_CASE", 0));
		// setValue("OTHER_Q", result.getData("UNLOCK_CASE_T", 0));
		String sql2 = "SELECT SUM(D.TOT_AMT) TOT_AMT FROM ADM_INP A,IBS_ORDM M,IBS_ORDD D "
				+ "WHERE A.CASE_NO = D.CASE_NO "
				+ "AND M.CASE_NO = D.CASE_NO "
				+ "AND M.CASE_NO_SEQ = D.CASE_NO_SEQ "
				+ "AND A.CASE_NO= '"
				+ parm.getValue("CASE_NO")
				+ "' "
				+ "AND A.CANCEL_FLG = 'N' "
				+ "AND D.BILL_DATE BETWEEN A.IN_DATE "
				+ "AND TO_DATE('"
				+ endDate + "','YYYY/MM/DD HH24:MI:SS')";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		setValue("TOT_AMT", result2.getDouble("TOT_AMT", 0)); // 医疗总费用

		if ((result.getDouble("TOTAL_BILPAY", 0) - result2.getDouble("TOT_AMT",
				0)) < 0) {
			setValue("ARREARAGE_AMT", result.getDouble("TOTAL_BILPAY", 0)
					- result2.getDouble("TOT_AMT", 0)); // 欠费
		}

	}

	/**
	 * 解锁记录
	 */
	public void onRecord() {

		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		// this.messageBox("解锁case"+acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", acceptData.getData("MR_NO"));
		TParm result = ADMInpTool.getInstance().selectall(parm);
		parm.setData("DEPT_CODE", result.getData("DEPT_CODE", 0));
		parm.setData("STATION_CODE", result.getData("STATION_CODE", 0));
		parm.setData("ARREARAGE_AMT", this.getValueDouble("ARREARAGE_AMT"));
		this.openDialog("%ROOT%\\config\\adm\\AdmUnlockReason.x", parm);

	}

	/**
	 * 保存事件
	 */
	public void onSave() {
		// TConnection connection =
		// TDBPoolManager.getInstance().getConnection();
		// this.messageBox(connection+"");
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("UNLOCK_DATE",
				date.toString().substring(0, 19).replace('-', '/'));
		if (StringUtil.isNullString(this.getValueString("UNLOCK_Q"))) {
			this.messageBox("请选择解锁原因!");
			return;
		}
		if (this.getValueString("UNLOCK_Q").equals("03")
				&& this.getValueString("OTHER_Q").equals("")) {
			this.messageBox("请填写其他原因!");
			return;
		}
		TParm parm = new TParm();
		parm.setData("UNLOCK_CASE", this.getValue("UNLOCK_Q"));
		parm.setData("UNLOCK_CASE_TEXT", this.getValue("OTHER_Q"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("UNLOCK_DATE",
				date.toString().substring(0, 19).replace('-', '/'));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ARREARAGE_AMT", this.getValue("ARREARAGE_AMT"));
		// 科室 parm.setData("DEPT_CODE", Operator.getDept());
		// 病区 parm.setData("STATION_CODE", Operator.getLanguage());
		parm.setData("OPT_DATE",
				date.toString().substring(0, 19).replace('-', '/'));

		String sqlSeq = "SELECT MAX(SEQ_NO) SEQ_NO FROM ADM_UNLOCK_CAUSE WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' ";
		TParm resultSeq = new TParm(TJDODBTool.getInstance().select(sqlSeq));
		int j;
		if (resultSeq.getValue("SEQ_NO") == null) {
			j = 1;
		} else {
			j = Integer.parseInt(resultSeq.getValue("SEQ_NO", 0)) + 1;
		}
		parm.setData("SEQ_NO", j);
		TParm NewParm = TIOM_AppServer.executeAction(
				"action.adm.ADMUnlockAction", "insertUnlock", parm);
		//永久解锁病人不更改字段数据
		String sqlS = " SELECT UNLOCKED_FLG FROM ADM_INP WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' ";
		TParm resultS = new TParm(TJDODBTool.getInstance().select(sqlS));
		if(!resultS.getValue("UNLOCKED_FLG",0).equals("2")){
		 TIOM_AppServer.executeAction(
				"action.adm.ADMUnlockAction", "upUnlock", parm);
		}
		if (NewParm.getErrCode() < 0 ) {
			this.messageBox("保存失败");
			return;
		}
		if(!resultS.getValue("UNLOCKED_FLG",0).equals("2")){
			unlockMs();
			}
		this.messageBox("保存成功");

	}

	public void onFILM_Flg() {
		if (getValueString("UNLOCK_Q").equals("03")) {
			callFunction("UI|OTHER_Q|setEnabled", true);
		} else {
			callFunction("UI|OTHER_Q|setEnabled", false);
			clearText("OTHER_Q");
		}
	}

	public void onIdentificationPic() {
		TParm parm = new TParm();
		parm.setData("MR_NO", acceptData.getData("MR_NO").toString().trim());
		this.openDialog("%ROOT%\\config\\adm\\ADMPictureShow.x", parm);
	}

	public void unlockMs() {
		int j = 0;
		double totAmt3 = 0.00;
		double totAmt2 = 0.00;
		Timestamp date = StringTool.getTimestamp(new Date());
		String endDate = date.toString().substring(0, 19).replace("-", "");
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		// 短信发送次数
		String sql1 = "SELECT SMS_COUNT FROM ADM_INP  WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		// 取红黄警戒线
		String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
		TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));
		// 身份比例
		String sqlD = "SELECT A.CASE_NO,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
				+ "DISCOUNT_RATE "
				+ "FROM ADM_INP A, SYS_CTZ_REBATE B "
				+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+) "
				+ "AND A.CASE_NO='"
				+ parm.getValue("CASE_NO") + "' ";
		TParm resultD = new TParm(TJDODBTool.getInstance().select(sqlD));
		// 消费总金额
		String sql2 = "SELECT SUM(D.TOT_AMT) TOT_AMT FROM ADM_INP A,IBS_ORDD D,SYS_CTZ Z "
				+ "WHERE A.CASE_NO=D.CASE_NO "
				+ "AND A.CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' "
				+ "AND A.CTZ1_CODE = Z.CTZ_CODE "
				+ "AND Z.MAIN_CTZ_FLG = 'Y' "
				// + "AND Z.NHI_CTZ_FLG = 'N' "
				+ "AND D.BILL_DATE BETWEEN A.IN_DATE "
				+ "AND TO_DATE('"
				+ endDate + "','YYYY/MM/DD HH24:MI:SS')";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if (result2.getCount() > 0
				&& result2.getValue("TOT_AMT", 0).length() > 0) {
			totAmt2 = StringTool.round(result2.getDouble("TOT_AMT", 0), 2);
		}

		// 预交金
		String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
				+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
				+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
				+ "AND CHARGE_DATE < TO_DATE ('" + endDate
				+ "','YYYY/MM/DD HH24:MI:SS') " + "AND CASE_NO = '"
				+ parm.getValue("CASE_NO") + "' GROUP BY CASE_NO";
		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
		if (result3.getCount() > 0
				&& result3.getValue("PRE_AMT", 0).length() > 0) {
			totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
		}
		j = Integer.parseInt(result1.getValue("SMS_COUNT", 0)) + 1;
		//短信清零
		if (totAmt3 - (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)) > resultSign.getDouble("YELLOW_SIGN", 0)) {
			j = 0;
		}
		if (j < 3
				&& totAmt3 - (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)) < resultSign
						.getDouble("YELLOW_SIGN", 0)) {
			unlockedTell(parm.getValue("CASE_NO"));
			unlockedTellR(parm.getValue("CASE_NO"));

		}
		if (totAmt3 - (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)) < resultSign
				.getDouble("YELLOW_SIGN", 0) && j >= 3) {
			unlockedTellK(parm.getValue("CASE_NO"));
		}
		
		this.getDBTool().update(
				"UPDATE ADM_INP SET SMS_COUNT='" + j + "' WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'");

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
	 * 获得文件服务器photo路径
	 * 
	 * @param mrNo
	 *            String
	 */
	public String getFileServerPath(String mrNo, String side) {
		String photoName = mrNo + side;
		String fileName = photoName;
		try {
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			return (dir + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获得文件服务器photo
	 * 
	 * @param Path
	 *            String
	 */
	public Image getFileServer(String Path) {
		try {
			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					Path);
			if (data == null)
				return null;
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void unlockedTellK(String case_no) { // 临时科主任
		TParm parm = new TParm();
		String sql6 = "SELECT A.CASE_NO,A.MR_NO,A.VS_DR_CODE,A.DEPT_CODE,"
				+ "O.USER_NAME,D.BED_NO_DESC,E.UNLOCK_CASE,B.PAT_NAME,C.DEPT_CHN_DESC,"
				+ "E.UNLOCK_CASE_TEXT,U.CHN_DESC "
				+ "FROM ADM_INP A,SYS_DEPT C,SYS_PATINFO B,SYS_OPERATOR O,ADM_UNLOCK_CAUSE E,SYS_DICTIONARY U,SYS_BED D "
				+ "WHERE  A.CASE_NO = '"
				+ case_no
				+ "' "
				+ "AND A.MR_NO = B.MR_NO AND A.DEPT_CODE = C.DEPT_CODE "
				+ "AND A.VS_DR_CODE = O.USER_ID AND A.CASE_NO = E.CASE_NO AND A.BED_NO=D.BED_NO(+) "
				+ "AND E.UNLOCK_CASE = U.ID AND U.GROUP_ID = 'SYS_UNLOCK_CAUSE' "
				+ "AND E.SEQ_NO=(SELECT MAX(SEQ_NO) FROM ADM_UNLOCK_CAUSE WHERE CASE_NO='"
				+ case_no + "')";
		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
		parm.setData("SEND_DATE", SystemTool.getInstance().getDate());
		parm.setData("CASE_NO", result6.getValue("CASE_NO", 0));
		parm.setData("MR_NO", result6.getValue("MR_NO", 0));
		parm.setData("DEPT_CODE", result6.getValue("DEPT_CODE", 0));
		parm.setData("BED_NO_DESC", result6.getValue("BED_NO_DESC", 0));
		parm.setData("PAT_NAME", result6.getValue("PAT_NAME", 0));
		parm.setData("DEPT_CHN_DESC", result6.getValue("DEPT_CHN_DESC", 0));
		parm.setData("CHN_DESC", result6.getValue("CHN_DESC", 0));
		parm.setData("UNLOCK_CASE_TEXT",
				result6.getValue("UNLOCK_CASE_TEXT", 0));
		parm.setData("UNLOCK_CASE", result6.getValue("UNLOCK_CASE", 0));
		parm.setData("USER_NAME", result6.getValue("USER_NAME", 0));
		parm.setData("REPORT_DATE", SystemTool.getInstance().getDate());
		TIOM_AppServer.executeAction("action.ibs.IBSAction", "unlockedTellK",
				parm);
	}

	public void unlockedTell(String case_no) { // 临时医生
		TParm parm = new TParm();
		String sql6 = "SELECT A.CASE_NO,A.MR_NO,A.VS_DR_CODE,A.DEPT_CODE,"
				+ "O.USER_NAME,D.BED_NO_DESC,E.UNLOCK_CASE,B.PAT_NAME,C.DEPT_CHN_DESC,"
				+ "E.UNLOCK_CASE_TEXT,U.CHN_DESC "
				+ "FROM ADM_INP A,SYS_DEPT C,SYS_PATINFO B,SYS_OPERATOR O,ADM_UNLOCK_CAUSE E,SYS_DICTIONARY U,SYS_BED D "
				+ "WHERE  A.CASE_NO = '"
				+ case_no
				+ "' "
				+ "AND A.MR_NO = B.MR_NO AND A.DEPT_CODE = C.DEPT_CODE "
				+ "AND A.VS_DR_CODE = O.USER_ID AND A.CASE_NO = E.CASE_NO AND A.BED_NO=D.BED_NO(+) "
				+ "AND E.UNLOCK_CASE = U.ID AND U.GROUP_ID = 'SYS_UNLOCK_CAUSE' "
				+ "AND E.SEQ_NO=(SELECT MAX(SEQ_NO) FROM ADM_UNLOCK_CAUSE WHERE CASE_NO='"
				+ case_no + "')";
		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
		parm.setData("SEND_DATE", SystemTool.getInstance().getDate());
		parm.setData("CASE_NO", result6.getValue("CASE_NO", 0));
		parm.setData("MR_NO", result6.getValue("MR_NO", 0));
		parm.setData("DEPT_CODE", result6.getValue("DEPT_CODE", 0));
		parm.setData("BED_NO_DESC", result6.getValue("BED_NO_DESC", 0));
		parm.setData("PAT_NAME", result6.getValue("PAT_NAME", 0));
		parm.setData("DEPT_CHN_DESC", result6.getValue("DEPT_CHN_DESC", 0));
		parm.setData("CHN_DESC", result6.getValue("CHN_DESC", 0));
		parm.setData("UNLOCK_CASE_TEXT",
				result6.getValue("UNLOCK_CASE_TEXT", 0));
		parm.setData("UNLOCK_CASE", result6.getValue("UNLOCK_CASE", 0));
		parm.setData("USER_NAME", result6.getValue("USER_NAME", 0));
		parm.setData("REPORT_DATE", SystemTool.getInstance().getDate());
		// System.out.println("parm-------"+parm);
		// System.out.println("yisheng:::"+parm);
		// this.messageBox("医生短信"+parm);
		TIOM_AppServer.executeAction("action.ibs.IBSAction", "unlockedTell",
				parm);
	}

	public void unlockedTellR(String case_no) { // 临时护士
		TParm parm = new TParm();
		String sql6 = "SELECT A.CASE_NO,A.MR_NO,A.VS_DR_CODE,A.DEPT_CODE,"
				+ "O.USER_NAME,D.BED_NO_DESC,E.UNLOCK_CASE,B.PAT_NAME,C.DEPT_CHN_DESC,"
				+ "E.UNLOCK_CASE_TEXT,U.CHN_DESC "
				+ "FROM ADM_INP A,SYS_DEPT C,SYS_PATINFO B,SYS_OPERATOR O,ADM_UNLOCK_CAUSE E,SYS_DICTIONARY U,SYS_BED D "
				+ "WHERE  A.CASE_NO = '"
				+ case_no
				+ "' "
				+ "AND A.MR_NO = B.MR_NO AND A.DEPT_CODE = C.DEPT_CODE "
				+ "AND A.VS_DR_CODE = O.USER_ID AND A.CASE_NO = E.CASE_NO AND A.BED_NO=D.BED_NO(+) "
				+ "AND E.UNLOCK_CASE = U.ID AND U.GROUP_ID = 'SYS_UNLOCK_CAUSE' "
				+ "AND E.SEQ_NO=(SELECT MAX(SEQ_NO) FROM ADM_UNLOCK_CAUSE WHERE CASE_NO='"
				+ case_no + "')";
		// System.out.println("sqlhushi------"+sql6);
		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
		parm.setData("SEND_DATE", SystemTool.getInstance().getDate());
		parm.setData("CASE_NO", result6.getValue("CASE_NO", 0));
		parm.setData("MR_NO", result6.getValue("MR_NO", 0));
		parm.setData("DEPT_CODE", result6.getValue("DEPT_CODE", 0));
		parm.setData("BED_NO_DESC", result6.getValue("BED_NO_DESC", 0));
		parm.setData("PAT_NAME", result6.getValue("PAT_NAME", 0));
		parm.setData("DEPT_CHN_DESC", result6.getValue("DEPT_CHN_DESC", 0));
		parm.setData("CHN_DESC", result6.getValue("CHN_DESC", 0));
		parm.setData("UNLOCK_CASE", result6.getValue("UNLOCK_CASE", 0));
		parm.setData("UNLOCK_CASE_TEXT",
				result6.getValue("UNLOCK_CASE_TEXT", 0));
		parm.setData("USER_NAME", result6.getValue("USER_NAME", 0));
		parm.setData("REPORT_DATE", SystemTool.getInstance().getDate());
		// System.out.println("parmhushi-------"+parm);
		TIOM_AppServer.executeAction("action.ibs.IBSAction", "unlockedTellR",
				parm);
	}

}
