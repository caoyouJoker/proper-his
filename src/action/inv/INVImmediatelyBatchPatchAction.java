package action.inv;

import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;

import java.util.Calendar;
import java.util.Date;


import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;

import jdo.inv.INVSQL;
import jdo.inv.INVTool;
import jdo.spc.INDTool;
import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 立即执行自动拨补作业
 * </p>
 * 
 * <p>  
 * Description: 立即执行自动拨补作业
 * </p>
 * 
 * <p>
 * Copyright: ProperSoft (c) 2013
 * </p>
 * 
 * <p> 
 * Company:
 * </p>
 * 
 * @author wangming 2013.12.22 
 * @version 1.0  
 */   
                                                                                                                                                                                                                                              
public class INVImmediatelyBatchPatchAction extends Patch {
	
	private static final String Flag_Y = "Y";
	private static final String Flag_N = "N";
	private static final String Flag_ONE = "1";
	private static final String Flag_ZERO = "0";
	
	private String deptCode = "";
//	public String getDeptCode() {
//		return deptCode;
//	}
//
//	public void setDeptCode(String deptCode) {
//		this.deptCode = deptCode;
//	}

	/**
	 * @param args         
	 */
	public static void main(String[] args){
		
	} 
 
	//从药库拨补修改成相关物资库拨补
  
	public boolean run() {
//		System.out.println("=====out=====");
		TConnection connection = TDBPoolManager.getInstance().getConnection();		
		// 得到物资库拨补周期参数			
		TParm assignParm = new TParm(TJDODBTool.getInstance().select(INVSQL.getAllAssignorg())); 
		// 得到物资库拨补参数 		
		TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVSysParm()));
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
		//得到自动拨补部门
//2013-10-30注释		TParm orgParm = new TParm(TJDODBTool.getInstance().select(INVSQL.queryOrgCodeAuto()));
		TParm orgParm = new TParm(TJDODBTool.getInstance().select(INVSQL.queryAutoObtainOrgCode()));//2013-10-30新添
//		System.out.println("得到自动拨补部门------------"+orgParm);
		// 数据检核
		if (orgParm == null || orgParm.getCount() < 1)
			return false;
		TParm result = new TParm();  
//		for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
//			String orgCode = orgParm.getValue("ORG_CODE", i);
//			String fromOrgCode = orgParm.getValue("DISPENSE_ORG_CODE",i) ;
//			String orgType = orgParm.getValue("DISPENSE_ORG_CODE", i);
//			TParm orgAutoParm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVORG(orgCode, Operator.getRegion())));
//			//自动拨补方式(0:每日(周)定量拨补, 1:依最大库存量(低于安全库存时) 2:依最小库存量(低于安全库存时))   
			String fixedAmountFlag = fixedAmountFlagMain;
//			//自动拨补类型2，拨补到最大库存量；1，定量拨补  
			String autoFillType = autoFillTypeMain;  
//			if (null != orgAutoParm && orgAutoParm.getCount() > 0) { 
//				String fixedAmountFlagNew = orgAutoParm.getValue("FIXEDAMOUNT_FLG", 0);
//				if (null != fixedAmountFlagNew || !"".equals(fixedAmountFlagNew)) {//如果药库部门没有设定自动拨补方式 则参考 药库参数统一规定的自动拨补方式
//					fixedAmountFlag = fixedAmountFlagNew;
//				}
//				String autoFillTypeNew = orgAutoParm.getValue("AUTO_FILL_TYPE", 0);
//				if (null != autoFillTypeNew || !"".equals(autoFillTypeNew)) {////如果药库部门没有设定自动拨补补量 则参考 药库参数统一规定的自动拨补补量
//					autoFillType = autoFillTypeNew;
//				}
//			}
//			// 拨补部门
//			String toOrgCode =orgParm.getValue("DISPENSE_ORG_CODE", i);
//			//子库的父库的拨补
//			//部门编码用来判断是否有拨补
//			String orgCodeAssign = orgCode; 			
//			boolean flag = isAssignAuto(assignParm, orgCodeAssign);
//			if (!flag) {// 如果是否 跳出本循环 
////				System.out.println(orgCode+"------orgCode-今日不播补-----");
//				continue;
//			}  
			TParm tp = this.getParm();
			System.out.println("------------------------:"+tp);   
			String orgCode = tp.getData("DEPT_CODE").toString();
			System.out.println("this.deptCode-------------:"+orgCode);
			//========获得需要订购物资信息========
			String sql = INVSQL.queryAutoReqStockM(orgCode);
			TParm parmD = new TParm(TJDODBTool.getInstance().select(sql));   //2013-10-29添加
			    
			
			sql = INVSQL.queryAutoReqStockMOtherDept(orgCode);
			TParm parmDOtherDept = new TParm(TJDODBTool.getInstance().select(sql));
			
			
			if ((null == parmD || parmD.getCount() < 1) && (null == parmDOtherDept || parmDOtherDept.getCount() < 1) ) {// 如果为空则进行下一个部门判断
//				continue;  
				return true;
			}    
			if(null != parmD && parmD.getCount()>0){
				//========自动生成订购========
				String purorderNo = SystemTool.getInstance().getNo("ALL", "INV", "INV_PURORDER", "No");
				//订购 --- 现在都是按正常走，但是泰心是单：介入手术室  
				TParm purOrderM = getPurOrderMParm(purorderNo,orgCode,"19", fixedAmountFlag, autoFillType); 
				// 保存自动拨补主/细         
				result = INVTool.getInstance().onSavePurOrderMAuto(purOrderM, parmD, connection);
				if (result.getErrCode() < 0) {  
					System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					connection.close();
					return false;
				}

			}
			
			if(null != parmDOtherDept && parmDOtherDept.getCount()>0){
				//========自动生成订购========
				String requestNo = SystemTool.getInstance().getNo("ALL", "INV", "INV_REQUEST", "No");
				//订购 
				TParm reqOrderM = getRequestOrderMParm(requestNo,orgCode, fixedAmountFlag, autoFillType); 
				// 保存自动拨补主/细         
				result = INVTool.getInstance().onSaveReqOrderMAuto(reqOrderM, parmDOtherDept, connection);
				if (result.getErrCode() < 0) {  
					System.out.println("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					connection.close();
					return false;
				}
			}
			
			
//		}
 
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
		System.out.println("assignParm"+assignParm);
		System.out.println("orgCode"+orgCode);
		boolean flag = false;
		// 拨补周期 1：星期；0：月
		String cycleType = Flag_ZERO;  
		String assignDay = ""; 
		if (null == assignParm || assignParm.getCount() < 0) {
			// 如果没有拨补周期返回false;
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
			//(从周日开始进行拨补)
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
			// 得到今天是几号,得到今天具体日期
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
	 * @param parm
	 * @return 
	 */
	private TParm getRequestMParm(String requestNo, String orgCode, String toOrgCode, String fixedType, String autoFillType){
		TParm parm = new TParm();
		parm.setData("REQUEST_NO", requestNo);
		//请领类型：拨补
		parm.setData("REQUEST_TYPE", "AUT"); 
		//供应科室     
		parm.setData("FROM_ORG_CODE", orgCode);    
		//申请科室
		parm.setData("TO_ORG_CODE", toOrgCode);
		// 自动拨补方式
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		// 自动拨补类型
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		return parm;
	}

	/**
	 * 获得订购/移货主项的数据PARM
	 * @param purOrderNo
	 * @param orgCode
	 * @param SUP_CODE
	 * @return
	 */
	private TParm getPurOrderMParm(String purOrderNo,String orgCode, String SUP_CODE, String fixedType, String autoFillType) {
		TParm parm = new TParm();
		parm.setData("PURORDER_NO",purOrderNo); 
		parm.setData("PURORDER_DATE",StringTool.getTimestamp(new Date()));  
		if(orgCode.equals("011201")){
			parm.setData("CON_ORG", "");
			parm.setData("CON_FLG", "N");
		}
		else {
			parm.setData("CON_ORG", orgCode);
			parm.setData("CON_FLG", "N");
		}
		parm.setData("ORG_CODE", "011201");
		parm.setData("SUP_CODE", SUP_CODE);	
		parm.setData("FIXEDAMOUNT_FLG", fixedType);//自动拨补方式     	
		parm.setData("AUTO_FILL_TYPE", autoFillType);//自动拨补数量
		return parm;
	}
	
	/**
	 * 获得订购/移货主项的数据PARM（生成请领单）
	 * @param purOrderNo
	 * @param orgCode
	 * @param SUP_CODE
	 * @return
	 */
	private TParm getRequestOrderMParm(String purOrderNo,String orgCode, String fixedType, String autoFillType) {
		TParm parm = new TParm();
		parm.setData("REQUEST_NO",purOrderNo); 
		parm.setData("REQUEST_DATE",StringTool.getTimestamp(new Date()));
		parm.setData("REQUEST_TYPE","REQ");
		parm.setData("FROM_ORG_CODE", "011201");
		parm.setData("TO_ORG_CODE", orgCode);
		parm.setData("URGENT_FLG", "N");
		parm.setData("FINAL_FLG", "N");
		parm.setData("FIXEDAMOUNT_FLG", fixedType);//自动拨补方式     	
		parm.setData("AUTO_FILL_TYPE", autoFillType);//自动拨补数量
		return parm;
	}

}
