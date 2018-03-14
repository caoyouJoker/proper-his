package jdo.mro;

import jdo.sta.StaGenMroDataTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 病案首页 总Tool
 * </p>
 * 
 * <p>
 * Description: 病案首页 总Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-5
 * @version 1.0
 */
public class MRORecordActionTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static MRORecordActionTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static MRORecordActionTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MRORecordActionTool();
		return instanceObject;
	}

	public MRORecordActionTool() {
	}

	/**
	 * 修改首页所有数据
	 * 
	 * @param Tparm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateRecordDate(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String ADMCHK_FLG = "";// 住院提交
		String DIAGCHK_FLG = "";// 医师提交
		String BILCHK_FLG = "";// 财务提交
		String QTYCHK_FLG = "";// 病案提交
		String ctzCode="";
		TParm page;
		if (parm.getData("Page1") != null) {
			page = parm.getParm("Page1"); // 取第一页签内容
			ADMCHK_FLG = page.getValue("ADMCHK_FLG");
			ctzCode=page.getValue("CTZ1_CODE");
			result = MRORecordTool.getInstance().saveFirst(page, connection);// 修改第一页签
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		if (parm.getData("Page2") != null) {
			page = parm.getParm("Page2"); // 取第二页签内容
			DIAGCHK_FLG = page.getValue("DIAGCHK_FLG");
			result = MRORecordTool.getInstance().saveSecend(page, connection);// 修改第二页签
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			String flg=page.getValue("ISMODIFY_DSDATE");//同步出院日期    shibl 20130108 add 
			if(flg.equals("Y")){
				page.setData("CTZ1_CODE", ctzCode);
				result = MRORecordTool.getInstance().modifydsDate(page, connection);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		if (parm.getData("Page4") != null) {
			page = parm.getParm("Page4"); // 取第四页签内容
			BILCHK_FLG = page.getValue("BILCHK_FLG");
			QTYCHK_FLG = page.getValue("QTYCHK_FLG");
			result = MRORecordTool.getInstance().saveFour(page, connection);// 修改第四页签
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// 取手术信息进行保存
		if (parm.getData("PageOP") != null) {
			page = parm.getParm("PageOP");
//			String[] sql = (String[]) parm.getData("PageOP"); // 取手术内容
			result = MRORecordTool.getInstance().saveOP(page, connection);// 保存手术信息
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		if (parm.getData("PageICD") != null) {
			TParm diag = parm.getParm("PageICD"); // 取诊断内容
			result = MRORecordTool.getInstance()
					.updateMRODiag(diag, connection);// 修改第四页签
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		if (ADMCHK_FLG.equals("Y") && DIAGCHK_FLG.equals("Y")
				&& BILCHK_FLG.equals("Y") && QTYCHK_FLG.equals("Y")) {//同时提交时修改医疗监管中间表     shibl 20130108 add
			// 修改首页更新医疗统计标记
			result = StaGenMroDataTool.getInstance().updateSTAFlg(parm,"","0",connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}
}
