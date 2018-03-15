package jdo.bms;

import java.sql.Timestamp;
import java.util.TimerTask;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;


/**
 * <p>
 * Title: 输血时间超过4小时通知Task
 * </p>
 * 
 * <p>
 * Description: 输血时间超过4小时通知Task
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhanglei 2017.6.7
 * @version 1.0
 */
public class BMSBloodOutTask extends TimerTask {
	

	/**
	 * Socket传送工具
	 */
	private SocketLink client;


	private static BMSBloodOutTask instance = new BMSBloodOutTask();
	
	private BMSBloodOutTask(){
	}
	
	public static BMSBloodOutTask getInstance() {
		return instance;
	}
	

	/**
	 * 数据采集
	 */
	
	public void run() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String state = StringTool.rollDate(sysDate, -1).toString().substring(0, 10).
                replaceAll("-", "") + "000000" ;
		String end = sysDate.toString().substring(0, 10).replaceAll("-", "")
				+ "235959" ;
		//System.out.println("--------输血时间超过4小时通知-------- " + sysDate);
		//System.out.println("开始时间 " + state + "结束时间 " + end);
		String sql= "SELECT B.MR_NO, S.PAT_NAME, S.SEX_CODE, B.BLDTRANS_USER, "
				   + " B.BLDTRANS_TIME, A.BED_NO_DESC,B.OUT_DATE "
				   + " FROM SYS_PATINFO S, SYS_BED A, BMS_BLOOD B "
				   + " WHERE     B.MR_NO = S.MR_NO"
				   + " AND B.CASE_NO = A.CASE_NO"
				   + " AND B.BLDTRANS_TIME IS NOT NULL"
				   + " AND B.BLDTRANS_END_TIME IS NULL"
				   + " AND OUT_DATE BETWEEN TO_DATE ('" + state + "', "
				   //+ " AND OUT_DATE BETWEEN TO_DATE ('20170410000000', "
				   + " 'YYYYMMDDHH24MISS')"
	               + " AND TO_DATE ('" + end + "',"
	               //+ " AND TO_DATE ('20170410235959',"
	               + " 'YYYYMMDDHH24MISS')";
		System.out.println("--------输血时间超过4小时通知-------- " + sql);
		
		TParm parm = new TParm( TJDODBTool.getInstance().select(sql) );
		System.out.println("--------输血时间超过4小时通知parm-------- " + parm);
		if(parm == null || parm.getErrCode() < 0) {
			System.out.println("输血超时提醒:查询失败");
			return;
		}
		if(parm.getCount("MR_NO") <= 0) {
			System.out.println("输血超时提醒:查无数据");
			return;
		}
		for(int i = 0 ;i<parm.getCount("MR_NO");i++){
			//判断是否通过护士单次执行
			if(parm.getValue("BLDTRANS_TIME",i).length() <= 0){
				System.out.println("非病区输血 " + i);
				continue;
			}
			//将数据库查到的性别代码转换成汉字
			if(getDiffInMinAndSec(parm.getTimestamp("OUT_DATE",i),sysDate) >= 240){
				if(parm.getValue("SEX_CODE",i).toString().equals("1")){
					parm.setData("SEX_CODE", i, "男");
				}else if(parm.getValue("SEX_CODE",i).toString().equals("2")){
					parm.setData("SEX_CODE", i, "女");
				}
			
//				String sqlUser = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '" + Operator.getID() + "'";
//				System.out.println("sqlUser:"+sqlUser);
//				TParm parmUser = new TParm(TJDODBTool.getInstance().select(sqlUser));
//				String pwd = OperatorTool.getInstance().decrypt(
//		                (String) parmUser.getData("USER_PASSWORD",0));
				
				//获得发送人账号密码
				client = SocketLink
						.running("", TConfig.getSystemValue("BMS.BloodOutOvertimeID"), TConfig.getSystemValue("BMS.BloodOutOvertimePWD"));
				System.out.println("用户名 " + TConfig.getSystemValue("BMS.BloodOutOvertimeID") + " 密码 " + TConfig.getSystemValue("BMS.BloodOutOvertimePWD"));
				if (client.isClose()) {
					System.out.println("报错喽^_<");
					System.out.println(client.getErrText());
					return;
				}
				//获得接收人的账号 
				client.sendMessage(parm.getValue("BLDTRANS_USER",i),"病患病案号：" + parm.getValue("MR_NO",i) + " 姓名：" + parm.getValue("PAT_NAME",i) + 
						" 性别：" + parm.getValue("SEX_CODE",i) + " 床号：" + parm.getValue("BED_NO_DESC",i) + " 输血已超过4小时");
				System.out.println("接收人帐号" + parm.getValue("BLDTRANS_USER",i));
				if (client == null)
					return;
				client.close();
				
//				System.out.println("病患病案号：" + parm.getValue("MR_NO",i) + " 姓名：" + parm.getValue("PAT_NAME",i) + 
//						" 性别：" + parm.getValue("SEX_CODE",i) + " 床号：" + parm.getValue("BED_NO_DESC",i) + "输血超过4小时");
			}
//			System.out.println("循环次数：" + i + " 当前时间：" + sysDate + " 血出库时间：" + parm.getValue("OUT_DATE",i) + 
//			BLDTRANS_USER		" 时间差： " + getDiffInMinAndSec(parm.getTimestamp("OUT_DATE",i),sysDate) + " 输血长度：" + parm.getValue("BLDTRANS_TIME",i).length());
		}
//		try {
//			
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	


	/**
	 * 计算 时间差  格式 XX分XX秒
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static int getDiffInMinAndSec(Timestamp t1, Timestamp t2) {
		if((t1 == null || t2 == null) || (t2.getTime() < t1.getTime()) ) {
			return 1;
		}

		if(t2.getTime() == t1.getTime()) {
			return 1;
		}
		
	    double diffSec =	(t2.getTime()  - t1.getTime()) / 1000D;
	    
		return  ((int)(diffSec / 60D));
	}
//	/**
//	 * 读取 TConfig.x
//	 *
//	 * @return TConfig
//	 */
//	private TConfig getProp() {
//		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
//		return config;
//	}
}
