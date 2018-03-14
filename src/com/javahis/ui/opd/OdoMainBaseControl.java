package com.javahis.ui.opd;

import jdo.ins.INSTJTool;
import jdo.odo.ODO;
import jdo.reg.Reg;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.DMessageIO;

/**
 * 
 * <p>
 * Title: 医生工作站主档
 * </p>
 * 
 * <p>
 * Description:医生工作站主档控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf 2012
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author xueyf 2012.03.13
 * @version 1.0
 */
public class OdoMainBaseControl extends TControl implements DMessageIO {
	protected INSPat insPat = null;
	// pat对象
	protected Pat pat;
	// odo 对象
	protected ODO odo;
	// reg对象
	Reg reg;
	/**
	 * 城职退休
	 */
	private static String INS_CHENGZHITUIXIU = "12";
	/**
	 * 城职离休
	 */
	private static String INS_CHENGZHILIXIU = "13";
	/**
	 * 城居成年居民
	 */
	private static String INS_CHENGJUCHENGNIANJUMING = "23";
	/**
	 * 自费
	 */
	private static String INS_ZIFEI = "99";
	/**
	 * 城职普通
	 */
	private static String INS_CHENGZHIPUTONG = "11";
	/**
	 * 城居新生儿
	 */
	private static String INS_CHENGZHIXINSHENGER = "21";
	/**
	 * 城居学生儿童
	 */
	private static String INS_CHENGJUXUESHENGERTONG = "22";

	/**
	 * 初始化医保接口
	 * 
	 * @param ctzCode
	 */
	protected void initINS() {
		insPat = new INSPat(this, pat);
	}

	public void onInit() {

		super.onInit();
		

	}

	/**
	 * 内部医保接口类
	 * 
	 * @author xueyf
	 * 
	 */
	class INSPat {
		TControl control = null;
		/**
		 * 是否医保身份 true 医保身份 false 费医保身份
		 */
		private boolean isINSPat = false;
		/**
		 * 医保对象
		 */
		private INSTJTool insTool = null;

		/**
		 * 病患身份
		 */
		private String ctzCode = "";

		/**
		 * 初始化设定当前病患是否属于医保对象
		 * 
		 * @param ctzCode
		 */
		public INSPat(TControl control, Pat pat) {
			this.control=control;
			if (insTool == null) {
				insTool = new INSTJTool();
//				TParm parm = insTool.orderCheck("", pat.getCtz1Code(),"O",reg.getInsPatType());
//				if (parm.getErrCode() != -1) {
//					isINSPat = true;
//					ctzCode = pat.getCtz1Code();
//				}
			}
		}

		public boolean isINSPat() {

			return isINSPat;
		}

//		/**
//		 * 门诊医嘱单校验
//		 * 
//		 * @param parm
//		 * @return
//		 */
//		public boolean saveInsCheck1(ODO odo) {
//			// 非医保直接返回
//			boolean isCanSave = true;
//			if (!isINSPat) {
//				return true;
//			}
//			int newRow[] = odo.getOpdOrder().getNewRows();
//			// 自费药品判断
//			// int ZF = 0;
//			for (int i : newRow) {
//				TParm ordParm = odo.getOpdOrder().getRowParm(i);
//				// 自费提示
//				if (parm.getErrCode() == -2) {
//					control.messageBox(orderDesc + ":" + parm.getErrText());
//					// ZF++;
//				}
//				if (ctzCode.equals(INS_CHENGJUXUESHENGERTONG)// 儿童患者处理
//						&& parm.getErrCode() != -5) {
//
//					// 非儿童用药不许保存
//					if (control.messageBox("提示信息 Tips", orderDesc+"属于非儿童医嘱，是否开立？",
//							control.YES_NO_OPTION) != 0) {
//						isCanSave = false;
//						break;
//					}
//					
//				}else if (ctzCode.equals(INS_CHENGJUXUESHENGERTONG)// 门特患者处理 // 修改门特编码
//						&& parm.getErrCode() != -6) {
//
//					// 非门特用药不许保存
//					if (control.messageBox("提示信息 Tips", orderDesc+"属于非门特用药范围，是否开立？",
//							control.YES_NO_OPTION) != 0) {
//						isCanSave = false;
//						break;
//					}
//				}else	if (parm.getErrCode() == -4) {// 住院判断
//					// 住院判断不许保存
//					if (control.messageBox("提示信息 Tips", orderDesc+"属于住院用药范围，是否开立？",
//							control.YES_NO_OPTION) != 0) {
//						isCanSave = false;
//						break;
//					}
//				}
//			}
//			return isCanSave;
//		}
	}
}
