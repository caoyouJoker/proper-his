package action.spc;

import com.dongyang.patch.Patch;
import com.dongyang.data.TParm;

import java.util.Calendar;


import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;

import jdo.spc.INDTool;
import jdo.spc.INDSQL;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 药房自动拨补作业
 * </p>
 * 
 * <p>
 * Description: 药房自动拨补作业
 * </p>
 * 
 * <p>
 * Copyright: ProperSoft (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liyh 2012.10.13
 * @version 1.0
 */

public class INDAutoReqBatchPatch extends Patch {

	private static final String Flag_Y = "Y";
	private static final String Flag_N = "N";
	private static final String Flag_ONE = "1";
	private static final String Flag_ZERO = "0";
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public boolean run() {
		TConnection connection = TDBPoolManager.getInstance().getConnection();
		// 得到药库拨补周期参数
		TParm assignParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getAssignorg()));
		// 得到药库拨补参数
		TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		// 自动拨补方式：0:每日(周)定量拨补, 1:依最大库存量(低于安全库存时) 2:依最小库存量(低于安全库存时)
		String fixedAmountFlagMain = parm.getValue("FIXEDAMOUNT_FLG", 0);
		if (null == fixedAmountFlagMain || "".equals(fixedAmountFlagMain)) {
			fixedAmountFlagMain = Flag_ZERO;
		}
		// 自动拨补量 :2，拨补到最大库存量；1，定量拨补
		String autoFillTypeMain = parm.getValue("AUTO_FILL_TYPE", 0);
		if (null == autoFillTypeMain || "".equals(autoFillTypeMain)) {
			autoFillTypeMain = Flag_ONE;
		}

		// 查询所有可以自动拨补的药房
		//System.out.println("-------------INDSQL.queryOrgCodeAuto(): " + INDSQL.queryOrgCodeAuto());
		TParm orgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.queryOrgCodeAuto()));
		// 数据检核
		if (orgParm == null || orgParm.getCount() < 1)
			return false;
		TParm result = new TParm();
		for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
			String orgCode = orgParm.getValue("ORG_CODE", i);
			//查询药库部门是否社会自了自动拨补方法和数量
			TParm orgAutoParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(orgCode, "H01")));
			//自动拨补方式
			String fixedAmountFlag = fixedAmountFlagMain;
			//自动拨补数量
			String autoFillType = autoFillTypeMain;
			if (null != orgAutoParm && orgAutoParm.getCount() > 0) {
				//药库主档自带的 自动拨补方式：0:每日(周)定量拨补, 1:依最大库存量(低于安全库存时) 2:依最小库存量(低于安全库存时)
				String fixedAmountFlagNew = orgAutoParm.getValue("FIXEDAMOUNT_FLG", i);
				if (null != fixedAmountFlagNew || !"".equals(fixedAmountFlagNew)) {//如果药库部门没有设定自动拨补方式 则参考 药库参数统一规定的自动拨补方式
					fixedAmountFlag = fixedAmountFlagNew;
				}
				//药库主档自带的 自动拨补量 :2，拨补到最大库存量；1，定量拨补
				String autoFillTypeNew = orgAutoParm.getValue("AUTO_FILL_TYPE", i);
				if (null != autoFillTypeNew || !"".equals(autoFillTypeNew)) {////如果药库部门没有设定自动拨补补量 则参考 药库参数统一规定的自动拨补补量
					autoFillType = autoFillTypeNew;
				}
			}
			// 拨补部门
			System.out.println(""+orgParm.getValue("DISPENSE_ORG_CODE", i));
			String toOrgCode =orgParm.getValue("DISPENSE_ORG_CODE", i);
			//子库的父库的拨补
//			String childOrgSubOrg = "";
			//部门编码用来判断是否有拨补
			String orgCodeAssign = orgCode;
			// 是否是子库 N，否，Y：是
//			String isSubOrg = orgParm.getValue("IS_SUBORG", i);

			/***********去掉子库start**********/
/*			if(Flag_Y.equals(isSubOrg)){//如果是子库，先查询其父库的拨补部门
				//如果是子库 根据 其父库查询其父库的拨补部门
				TParm subParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getSubOrgInfo(toOrgCode)));
				if(null == subParm || subParm.getCount() < 1){
					return false;
				}
				childOrgSubOrg = subParm.getValue("SUP_ORG_CODE", 0);
				orgCodeAssign =toOrgCode;// 
			}*/
			/***********去掉子库end**********/
			// 如果有药品经行 拨补 ，先判断 拨补周期,看当日 是否拨补，false：不拨补，true:拨补
			boolean flag = isAssignAuto(assignParm, orgCodeAssign);
//			System.out.println("---------flag: "+flag);           
			if (!flag) {// 如果是否 跳出本循环
//				System.out.println(orgCode+"------orgCode-nononoono-----");
				continue;
			}   
              
			// 查询可以自动拨补的药品           
			System.out.println("------------INDSQL.queryStockM(orgCode, fixedAmountFlag): " + INDSQL.queryStockM(orgCode, fixedAmountFlag));
			//非麻精
			TParm parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockM(orgCode, fixedAmountFlag)));
			//麻精
			TParm parmDrugD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMDrug(orgCode, fixedAmountFlag)));
			if ((null == parmD || parmD.getCount() < 1)  && (null == parmDrugD || parmDrugD.getCount() < 1)) {// 如果为空则 进行下一个部门判断
				continue;
			}

			if ("-1".equals(toOrgCode)) {// 走主库
				// 得到移货单号
				if(null != parmD && parmD.getCount()>0){//非麻精自动生成订购
					String purorderNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_PURORDER", "No");
					TParm purOrderM = getPurOrderMParm(purorderNo,orgCode,"18", fixedAmountFlag, autoFillType,"1");
					// 保存自动拨补主/细 
					result = INDTool.getInstance().onSavePurOrderMAuto(purOrderM, parmD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;      
					}
				}
				if(null != parmDrugD && parmDrugD.getCount()>0){//非麻精自动生成订购
					String purorderNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_PURORDER", "No");
					TParm purOrderM = getPurOrderMParm(purorderNo,orgCode,"18", fixedAmountFlag, autoFillType,"2");
					// 保存自动拨补主/细
					result = INDTool.getInstance().onSavePurOrderMAuto(purOrderM, parmDrugD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}				
			
			} 
			else {
/*				if(Flag_Y.equals(isSubOrg)){//如果是子库，先查询其父库的拨补部门
					toOrgCode = childOrgSubOrg;
				}*/
				if(null != parmD && parmD.getCount()>0){//非麻精自动生成订购
					// 得到请领单号
					String requestNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_REQUEST", "No");
					// 组装请领主档信息
					TParm parmM = getRequestMParm(requestNo, orgCode, toOrgCode, fixedAmountFlag, autoFillType,"1");
					// 保存自动拨补主/细
					result = INDTool.getInstance().onSaveRequestMAuto(parmM, parmD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}
				if(null != parmDrugD && parmDrugD.getCount()>0){//麻精自动生成订购
					// 得到请领单号
					String requestNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_REQUEST", "No");
					// 组装请领主档信息
					TParm parmM = getRequestMParm(requestNo, orgCode, toOrgCode, fixedAmountFlag, autoFillType,"2");
					// 保存自动拨补主/细
					result = INDTool.getInstance().onSaveRequestMAuto(parmM, parmDrugD, connection);
					if (result.getErrCode() < 0) {
						System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						connection.close();
						return false;
					}
				}

			}

		}

		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * 判断部门今天是否自动拨补
	 * 
	 * @param assignParm
	 * @param orgCode
	 * @return
	 */
	public boolean isAssignAuto(TParm assignParm, String orgCode) {
		// 返回标志
		boolean flag = false;
		// 拨补周期 1：星期；0：月
		String cycleType = Flag_ZERO;
		String assignDay = "";
		if (null == assignParm || assignParm.getCount() < 0) {// 如果没有拨补周期返回false;
			return false;
		}
		// 循环 得到改部门 拨补的 方式，按日期还是月份
		for (int i = 0; i < assignParm.getCount(); i++) {
			String orgCode1 = assignParm.getValue("ORG_CODE", i);
			if (orgCode.equals(orgCode1)) {
				cycleType = assignParm.getValue("CYCLE_TYPE", i);
				assignDay = assignParm.getValue("ASSIGNED_DAY", i);
				// 判断当日 是否拨补
				flag = isAtuoToday(cycleType, assignDay);
				break;
			}
		}
		return flag;
	}

	/**
	 * 判断今天是否自动拨补
	 * 
	 * @return
	 */
	public boolean isAtuoToday(String cycleType, String assignDay) {
		boolean flag = false;
		if (Flag_ONE.equals(cycleType)) {// 按星期拨补
			// 得到今天是星期几，日，一，二，三，四，五，六
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
			// 重新格式化日期安排：日，一，二，三，四， 五，六
			String assday = getFromatAssingDay(assignDay);
			// 截取当前星期值对应的数据
			String subString = assday.substring(day, day + 1);
			if (Flag_Y.equals(subString)) {
				flag = true;
			} else {
				flag = false;
			}
		}
		if (Flag_ZERO.equals(cycleType)) {// 按月份拨补
			// 得到今天是几号
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			// 截取当日期值对应的数据
			String subString = assignDay.substring(day-1, day);
			if (Flag_Y.equals(subString)) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 重新排列星期组合
	 * 
	 * @des 原先格式 星期一，二，三，四，五，六，日，改为星期日，一，二，三，四，五，六
	 * @param assignDay
	 * @return
	 */
	public String getFromatAssingDay(String assignDay) {
		int len = assignDay.length();
		String newStr = assignDay.substring(0, len - 1);
		String lastStr = assignDay.substring(len - 1);
		return lastStr + newStr;
	}

	/**
	 * 获得自动请领主项的数据PARM
	 * @param isDrug 是否为麻精2是，1不是
	 * @param parm
	 * @return
	 */
	private TParm getRequestMParm(String requestNo, String orgCode, String toOrgCode, String fixedType, String autoFillType,String isDrug){
		TParm parm = new TParm();
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("REQTYPE_CODE", "DEP");
		parm.setData("APP_ORG_CODE", orgCode);
		parm.setData("TO_ORG_CODE", toOrgCode);
		// 自动拨补方式
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		parm.setData("DRUG_CATEGORY", isDrug);
		return parm;
	}

	/**
	 * 获得订购/移货主项的数据PARM
	 * @param purOrderNo
	 * @param orgCode
	 * @param SUP_CODE
	 * @param isDrug 是否为麻精 Y是，N不是
	 * @return
	 */
	private TParm getPurOrderMParm(String purOrderNo,String orgCode, String SUP_CODE, String fixedType, String autoFillType,String isDrug) {
		TParm parm = new TParm();
		parm.setData("PURORDER_NO",purOrderNo);
		parm.setData("ORG_CODE", orgCode);
		parm.setData("SUP_CODE", SUP_CODE);
		parm.setData("REASON_CHN_DESC", "");
		parm.setData("REGION_CODE", "H01");
		// 自动拨补方式
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		//自动拨补数量
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		parm.setData("DRUG_CATEGORY", isDrug);
		return parm;
	}
}
