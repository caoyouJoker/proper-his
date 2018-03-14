package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.TDebug;
import com.dongyang.util.StringTool;
/**
 *
 * <p>
 * Title: 操作人员数据对象
 * </p>
 *
 * <p>
 * Description: 处理有关操作人员的全部数据处理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author lzk 2008-08-11
 * @version 1.0
 */
public class OperatorTool extends TJDOTool {
        /**
         * 实例
         */
        public static OperatorTool instanceObject;

        /**
         * 得到实例
         *
         * @return OperatorTool
         */
        public static OperatorTool getInstance() {
                if (instanceObject == null)
                        instanceObject = new OperatorTool();
                return instanceObject;
        }

        /**
         * 构造器
         */
        public OperatorTool() {
                setModuleName("sys\\SYSOperatorModule.x");
                onInit();
        }

        /**
         * 得到用户姓名
         *
         * @param userID
         *            String 用户编号
         * @return String 姓名
         */
        public String getOperatorName(String userID) { 
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorName", parm), "USER_NAME");
        }
        /**
         * 得到用户职别  wuxy 20170621 
         * @param userID
         * @return
         */ 
        public String getOperatorPosCode(String userID) {
            TParm parm = new TParm();
            parm.setData("USER_ID", userID);
            return getResultString(query("getOperatorPosCode", parm), "POS_CODE");
            }
        /**
         * 获取用户英文名
         * @param userID String
         * @return String
         */
        public String getOperatorEngName(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorEngName", parm), "USER_ENG_NAME");
        }
        /**
         * 得到成本中心代码
         * @param userID String
         * @return String
         */
        public String getCostCenterCode(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getCostCenterCode", parm), "COST_CENTER_CODE");
        }


        /**
         * 得到用户密码
         *
         * @param userID
         *            String 用户编号
         * @return String 用户密码
         */
        public String getOperatorPassword(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorPassword", parm),
                                "USER_PASSWORD");
        }

        /**
         * 修改用户密码
         *
         * @param userID
         *            String 用户编号
         * @param password
         *            String 新密码
         * @return boolean true 成功 false 失败
         */
        public boolean setOperatorPassword(String userID, String password) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                parm.setData("USER_PASSWORD", password);
                TParm result = update("setOperatorPassword", parm);
                return result.getErrCode() == 0;
        }

        /**
         * 用户是否存在
         *
         * @param userID
         *            String 用户编号
         * @return boolean true 存在 false 不存在
         */
        public boolean existsOperator(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultInt(query("existsOperator", parm), "COUNT") > 0;
        }

        /**
         * 得到异常登陆次数
         * @param userID String
         * @return int
         */
        public int getAbnormaiTimes(String userID) {
                TParm result = new TParm();
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                result = query("queryAbnormaiTimes", parm);
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                        return 0;
                }
                return result.getInt("ABNORMAL_TIMES", 0);
        }

        /**
         * 得到用户的生效日期
         *
         * @param userID
         *            String 用户编号
         * @return Timestamp 生效日期
         */
        public Timestamp getOperatorActiveDate(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultTimestamp(query("getOperatorActiveDate", parm),
                                "ACTIVE_DATE");
        }

        /**
         * 得到用户的截至日期
         *
         * @param userID
         *            String 用户编号
         * @return Timestamp 生效日期
         */
        public Timestamp getOperatorEndDate(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultTimestamp(query("getOperatorEndDate", parm), "END_DATE");
        }

        /**
         * 验证用户时效
         *
         * @param userID
         *            String 用户编号
         * @return boolean true 有效 false 无效
         */
        public boolean affecttedTimeOperator(String userID) {
                Timestamp nowTime = SystemTool.getInstance().getDate();
                Timestamp startTime = getOperatorActiveDate(userID);
                Timestamp endTime = getOperatorEndDate(userID);
                if (startTime == null || endTime == null)
                        return false;
                return nowTime.after(startTime) && nowTime.before(endTime);
        }
        /**
         * 测试密码的有效日期返回是否有效==========pangben modify 20110324
         * @param result TParm
         * @return boolean
         */
        public boolean affecttedTimePwd(TParm result) {
            Timestamp oldTime = result.getTimestamp("PWD_ENDDATE",0);
            //当前日期
            Timestamp nowTime = SystemTool.getInstance().getDate();
            if(null==oldTime)
                return false;
            //当前日期大于设置日期返回true 否则false
            return nowTime.after(oldTime);
        }
        /**
         * 测试密码的修改时间是否已经超过三个月,如果超过三个月提示修改修改更新密码(当前日期大于设置日期返回true 否则false)==========pangben modify 20110428
         * @param result TParm
         * @param parm TParm
         * @return boolean
         */
        public boolean afterThreeMonthPwd(TParm result,TParm parm) {
            Timestamp oldTime = result.getTimestamp("PWD_STARTDATE",0);
            //========== 第二个参数校验密码时间的天数
            oldTime= StringTool.rollDate(oldTime,parm.getInt("DETECTPWDTIME",0));
           // oldTime
            //当前日期
            Timestamp nowTime = SystemTool.getInstance().getDate();
            if(null==oldTime)
                return false;
            //当前日期大于设置日期返回true 否则false
            return nowTime.after(oldTime);
        }
        /**
         * 通过userID编号查询用户密码设置的日期，以及密码启用时间
         * @param userID String
         * @return TParm  保存密码设置的日期，以及密码启用时间
         * ==========pangben modify 20110428
         */
        public TParm getUserInfo(String userID) {
            TParm parm = new TParm();
            parm.setData("USER_ID", userID);
            TParm result = query("getOperatorObateDate", parm);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return null;
            }
            return result;
        }
        /**
         * 得到用户IP
         *
         * @return String
         */
        public String getUserIP() {
                TParm parm = new TParm();
                TParm result = TIOM_AppServer.executeAction("action.sys.LoginAction",
                                "getIP", parm);
                if (result.getErrCode() < 0) {
                        err(result.getErrCode() + " " + result.getErrText());
                        return "";
                }
                return result.getValue("IP");
        }

        /**
         * 登录验证
         * @param userID String 用户名
         * @param password String 密码
         * @param region String 院区
         * @param dept String 部门
         * @param station String 病区
         * @return TParm
         */
        public TParm login(String userID, String password, String region,
                        String dept, String station) {
            TParm result = new TParm();
            if (userID == null || password == null)
            {
                result.setErr(-1,"参数错误!");
                return result;
            }
            TParm parm = new TParm();
            parm.setData("USER_ID", userID);
            parm.setData("USER_PASSWORD",password);
            parm.setData("REGION", region);

            result = TIOM_AppServer.executeAction("action.sys.LoginAction",
                                                        "login", parm);
            if (result.getErrCode() < 0)
                return result;

            String IP = result.getValue("IP");
            // 创建用户对象
            Operator.setData(userID, region, IP, dept, station);

            return result;
        }

        /**
         * 得到用户的默认部门
         *
         * @param userID
         *            String 用户编号
         * @return String
         */
        public String getMainDept(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return this.getResultString(query("getMainDept", parm), "DEPT_CODE");
        }

        /**
         * 得到用户的默认病区
         *
         * @param userID
         *            String
         * @return String
         */
        public String getMainStation(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return this.getResultString(query("getMainStation", parm),
                                "STATION_CLINIC_CODE");
        }

        /**
         * 设置最后一次登录时间
         *
         * @param userID
         *            String 用户编号
         * @return boolean true 成功 false 失败
         */
        public boolean updateRcntLoginDate(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                TParm result = update("updateRcntLoginDate", parm);
                if (result.getErrCode() < 0) {
                        err(result.getErrCode() + " " + result.getErrText());
                        return false;
                }
                return true;
        }
        /**
         * 第一次登陆修改密码之后添加PWD_STARTDATE列数值=======pangben modify 20110531
         * @param userID String
         * @param parm TParm
         * @return boolean
         */
        public boolean updatePwdstartDate(String userID,TParm parm) {
            TParm temp = new TParm();
            temp.setData("USER_ID", userID);
            //当前日期
            Timestamp nowTime = SystemTool.getInstance().getDate();
            //修改之后的日期
            Timestamp time = StringTool.rollDate(nowTime,
                                                 parm.getInt("DETECTPWDTIME",0));
            temp.setData("PWD_ENDDATE",time);
            TParm result = update("updatePwdobateDate", temp);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return false;
            }
            return true;
        }
        /**
         * 得到用户的权限组
         *
         * @param userID
         *            String
         * @return String
         */
        public String getOperatorRole(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorRole", parm), "ROLE_ID");
        }

        public TParm selectByWhere(TParm parm) {
                TParm result = query("selectByWhere", parm);
                if (result.getErrCode() < 0)
                        err(result.getErrCode() + " " + result.getErrText());
                return result;
        }

        public static void main(String args[]) {
                // Log.DEBUG=true;
                TDebug.initServer();
                // TDebug.initServer();
                OperatorTool tool = new OperatorTool();
                TParm parm = new TParm();
                parm.setData("USER_NAME", "a%");
                parm.setData("DEPT_CODE", "302");
                //System.out.println(tool.selectByWhere(parm));

                // tool.setOperatorPassword("admin",tool.encrypt("admin"));
                // System.out.println(tool.login("admin","admin",""));
        }

        /**
         * 根据使用者ID得到使用者的所有科室
         * @param userID String
         * @return TParm
         */
        public TParm getOperatorDept(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                TParm result = this.query("getOperatorDept", parm);
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                        return result;
                }
                return result;
        }
        
		/**
		 * 得到护工用户基本信息
		 * 
		 * @param userID
		 *            String 用户编号
		 * @return TParm 用户基本信息
		 */
		public TParm getOperatorNurseInfo(String userID) {
			TParm parm = new TParm();
			parm.setData("USER_ID", userID);
			return query("getOperatorNurseInfo", parm);
		}
}
