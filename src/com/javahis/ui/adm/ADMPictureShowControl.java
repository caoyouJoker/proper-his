package com.javahis.ui.adm;

import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.ImageTool;
import com.dongyang.ui.TPanel;


//import org.eclipse.wb.swt.PictureShow;

/**
 * <p>
 * Title: 三级检诊信息（ADM入出转调用）
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
 * @author JiaoY 2009.01.14
 * @version 1.0
 */
public class ADMPictureShowControl extends TControl {

	final String Front = "_IDFront.jpg";
	final String Back = "_IDBack.jpg";
	Pic pic = null;

	public ADMPictureShowControl() {
	}

	TParm acceptData = new TParm(); // 接参

	// Pat pat = new Pat();
	// TParm initParm = new TParm(); // 初始数据

	public void onInit() {
//		callFunction("UI|TRAN_BED|setEnabled", false);
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
		// Pat pat = new Pat();
		
		String mrNo = acceptData.getData("MR_NO").toString();
		// pat = pat.onQueryByMrNo(mrNo);
		// this.setValue("MR_NO", pat.getMrNo());
		// this.setValue("PAT_NAME", pat.getName());
		// this.setValue("SEX_CODE", pat.getSexCode());
		// this.setValue("IPD_NO", acceptData.getData("IPD_NO"));
		// 设定可保存权限 可否转床转医师
		// if (acceptData.getBoolean("SAVE_FLG")) {
		// callFunction("UI|save|setVisible", true);
		// } else {
		 callFunction("UI|save|setVisible", false);
		// }
		// this.initQuery();
		viewPhoto(mrNo, Front);
	}

	/**
	 * 显示photo
	 * 
	 * @param mrNo
	 *            String
	 */
	public void viewPhoto(String mrNo, String side) {
		// String photoName = mrNo + Front;
		// String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
			Image image = getFileServer(getFileServerPath(mrNo, side));
			if (image != null) {
				pic = new Pic(image);
				pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
				pic.setLocation(0, 0);
				viewPanel.removeAll();
				viewPanel.add(pic);
				pic.repaint();
			} else {
				this.messageBox_("该患者的身份证未拍照,请联系住院处补拍");
			}
		} catch (Exception e) {
		}
	}

	public void noFront() {
		paint(getFileServer(getFileServerPath(acceptData.getData("MR_NO")
				.toString(), Front)));
	}

	public void noBack() {
		paint(getFileServer(getFileServerPath(acceptData.getData("MR_NO")
				.toString(), Back)));
	}

	public void paint(Image image) {
		if (image != null && this.pic != null) {
			pic.setPic(image);
			pic.repaint();
		}
	}

	class Pic extends JLabel {
		Image image;
		int width = 0;
		int height = 0;

		public Pic(Image image) {
			this.image = image;
			this.width = image.getWidth(null);
			this.height = image.getHeight(null);
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(12, 17, width, height);
			if (image != null) {
				g.drawImage(image, 12, 17, width, height, null);
			}
		}

		public void setPic(Image image) {
			this.image = image;
			this.width = image.getWidth(null);
			this.height = image.getHeight(null);
		}
	}

	/**
	 * 初始化查询
	 */
	// public void initQuery() {
	// TParm parm = new TParm();
	// parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
	// parm.setData("MR_NO", acceptData.getData("MR_NO"));
	// parm.setData("IPD_NO", acceptData.getData("IPD_NO"));
	// // 查询病患住院信息
	// TParm result = ADMInpTool.getInstance().selectall(parm);
	// initParm.setRowData(result);
	// // 获取病患基本信息
	// Pat pat = Pat.onQueryByMrNo(acceptData.getValue("MR_NO"));
	// setValue(
	// "AGE",
	// StringUtil.showAge(pat.getBirthday(),
	// result.getTimestamp("IN_DATE", 0)));
	// setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
	// setValue("STATION_CODE", result.getData("STATION_CODE", 0));
	// setValue("BED_NO", result.getData("BED_NO", 0));
	// setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
	// setValue("ATTEND_DR_CODE", result.getData("ATTEND_DR_CODE", 0));
	// setValue("DIRECTOR_DR_CODE", result.getData("DIRECTOR_DR_CODE", 0));
	// setValue("VS_NURSE_CODE", result.getData("VS_NURSE_CODE", 0));
	// setValue("PATIENT_CONDITION", result.getData("PATIENT_CONDITION", 0));
	// setValue("NURSING_CLASS", result.getData("NURSING_CLASS", 0));
	// setValue("PATIENT_STATUS", result.getData("PATIENT_STATUS", 0));
	// setValue("DIE_CONDITION", result.getData("DIE_CONDITION", 0));
	// setValue("CARE_NUM", result.getData("CARE_NUM", 0));
	// setValue("IO_MEASURE", result.getValue("IO_MEASURE", 0));
	// setValue("ISOLATION", result.getValue("ISOLATION", 0));
	// setValue("TOILET", result.getValue("TOILET", 0));
	// if ("Y".equals(result.getValue("ALLERGY", 0)))
	// setValue("ALLERGY_Y", "Y");
	// }

	/**
	 * 保存事件
	 */
	// public void onSave() {
	// if ("Y".equals(getValue("TRANBED_CHECK"))) {
	// if ("Y".equals(acceptData.getData("BED_OCCU_FLG"))) {
	// this.messageBox_("此病患已包床");
	// return;
	// }
	// }
	// TParm newParm = this
	// .getParmForTag("MR_NO;IPD_NO;PAT_NAME;SEX_CODE;AGE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE;DIRECTOR_DR_CODE;VS_NURSE_CODE;PATIENT_CONDITION;NURSING_CLASS;PATIENT_STATUS;DIE_CONDITION;CARE_NUM;IO_MEASURE;ISOLATION;TOILET");
	// newParm.setData("CASE_NO", acceptData.getData("CASE_NO"));
	// newParm.setData("BED", getValue("TRANBED_CHECK"));
	// newParm.setData("BED_NO", getValue("BED_NO"));
	// newParm.setData("TRAN_BED", getValue("TRAN_BED"));
	// // =====liuf=====//
	// newParm.setData("BED_NO_DESC", ((TComboBox) getComponent("BED_NO"))
	// .getComboEditor().getText());
	// // =====liuf========//
	// if (this.getValueBoolean("ALLERGY_Y")) {
	// newParm.setData("ALLERGY", "Y");
	// } else {
	// newParm.setData("ALLERGY", "N");
	// }
	// newParm.setData("OPT_USER", Operator.getID());
	// newParm.setData("OPT_TERM", Operator.getIP());
	// TParm DATA = new TParm();
	// DATA.setData("OLD_DATA", initParm.getData());
	// DATA.setData("NEW_DATA", newParm.getData());
	// // System.out.println("data=====转床保存的数据==========="+DATA);
	// // ===========pangben modify 20110617 start
	// if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
	// DATA.setData("REGION_CODE", Operator.getRegion());
	// }
	// TParm result = TIOM_AppServer.executeAction(
	// "action.adm.ADMWaitTransAction", "changeDcBed", DATA); // 保存
	// if (result.getErrCode() < 0) {
	// this.messageBox("执行失败！！" + result.getErrName()); // 抢床错误提示 chenxi
	// this.clearValue("TRANBED_CHECK;TRAN_BED");
	// } else {
	// this.messageBox("P0005");
	// // ===liuf 传送给CIS转床的消息===
	// if ("Y".equals(getValue("TRANBED_CHECK"))) {
	// sendMessage(newParm);
	// }
	// this.initQuery();
	// this.clearValue("TRANBED_CHECK;TRAN_BED");
	// ADMXMLTool.getInstance().creatXMLFile(
	// acceptData.getValue("CASE_NO"));
	//
	// }
	// }

	/**
	 * 点选转床
	 */
	// public void onTRANBED_CHECK() {
	// if ("Y".equals(getValue("TRANBED_CHECK"))) {
	// TParm sendParm = new TParm();
	// if (getValue("DEPT_CODE") == null
	// || "".equals(getValue("DEPT_CODE"))) {
	// return;
	// }
	// if (getValue("STATION_CODE") == null
	// || "".equals(getValue("STATION_CODE"))) {
	// return;
	// }
	// sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
	// sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
	// TParm reParm = (TParm) this.openDialog(
	// "%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
	// if (reParm != null) {
	// this.setValue("TRAN_BED", reParm.getValue("BED_NO", 0));
	// } else {
	// this.clearValue("TRANBED_CHECK;TRAN_BED");
	// }
	// } else {
	// this.clearValue("TRANBED_CHECK;TRAN_BED");
	// }
	// }

	/**
	 * 传送CIS和血糖转床消息
	 * 
	 * @param parm
	 */
	// public void sendMessage(TParm parm) {
	// // System.out.println("sendCISMessage()");
	// // ICU、CCU注记
	// String caseNO = parm.getValue("CASE_NO");
	// boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
	// boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
	// // 转床
	// String type = "ADM_TRAN_BED";
	// parm.setData("ADM_TYPE", "I");
	// // CIS
	// if (IsICU || IsCCU) {
	// List list = new ArrayList();
	// parm.setData("SEND_COMP", "CIS");
	// list.add(parm);
	// TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(
	// list, type);
	// if (resultParm.getErrCode() < 0)
	// messageBox(resultParm.getErrText());
	// }
	// // 血糖
	// List list = new ArrayList();
	// parm.setData("SEND_COMP", "NOVA");
	// list.add(parm);
	// TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
	// type);
	// if (resultParm.getErrCode() < 0)
	// messageBox(resultParm.getErrText());
	// }

//	public void onIdentificationPic() {
		// final String Front = "_IDFront.jpg";
		// final String back = "_IDBack.jpg";
		// String mrno = acceptData.getData("MR_NO").toString();
		// String imageFront = getFileServerPath(mrno, Front);
		// String imageback = getFileServerPath(mrno, back);
		// Image imageTemp = getFileServer(imageFront);
		// if ((imageFront == null) || (imageback == null)) {
		// this.messageBox_("该患者的身份证未拍照,请联系住院处补拍");
		// return;
		// }
		// if (imageTemp == null) {
		// this.messageBox_("该患者的身份证未拍照,请联系住院处补拍");
		// return;
		// }

		// new PictureShow(imageFront, imageback).setVisible(true);

//	}

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
}
